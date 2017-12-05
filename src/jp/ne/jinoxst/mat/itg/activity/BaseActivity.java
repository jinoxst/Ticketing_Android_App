package jp.ne.jinoxst.mat.itg.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import jp.ne.jinoxst.mat.itg.R;
import jp.ne.jinoxst.mat.itg.activity.adapter.TicketListAdapter;
import jp.ne.jinoxst.mat.itg.activity.dialog.CommonProgressDialog;
import jp.ne.jinoxst.mat.itg.activity.dialog.PrintFindErrorDialog;
import jp.ne.jinoxst.mat.itg.pojo.ReprintData;
import jp.ne.jinoxst.mat.itg.pojo.Ticket;
import jp.ne.jinoxst.mat.itg.util.Constant;
import jp.ne.jinoxst.mat.itg.util.StringUtil;

import org.apache.commons.lang3.exception.ExceptionUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.epson.eposprint.Builder;
import com.epson.eposprint.EposException;
import com.epson.eposprint.Print;
import com.epson.epsonio.DevType;
import com.epson.epsonio.EpsonIo;
import com.epson.epsonio.EpsonIoException;
import com.epson.epsonio.Finder;
import com.epson.epsonio.IoStatus;

public class BaseActivity  extends Activity implements Runnable {
    protected static Print printer = null;
    protected List<ReprintData> reprintList;
    protected long printFindStartMilisec;
    protected DialogFragment printFindProgress;
    protected ArrayList<HashMap<String, String>> printerList = null;
    protected SimpleAdapter printerListAdapter = null;
    protected ScheduledExecutorService scheduler;
    protected ScheduledFuture<?> future;
    protected final static int DISCOVERY_INTERVAL = 500;
    protected static final int SEND_TIMEOUT = 10 * 1000;
    protected int printFindMaxTime = 15;
    protected static final byte[] CMD_ESDPOS = {
        0x1b, 0x3d, 0x01,    // ESC = 1(Enables printer)
        0x1d, 0x49, 0x45,    // GS I 69(Type of mounted additional fonts)
        0x1d, 0x49, 0x43,    // GS I 67(Printer name)
    };
    protected static final int RESPONSE_HEADER = 0x5f;
    protected static final int RESPONSE_TERMINAL = 0x00;
    protected static final int SEND_RESPONSE_TIMEOUT = 1000;
    protected static final int RESPONSE_MAXBYTE = 128;
    protected Handler handler = new Handler();
    protected static String selectedPrintIPAddress;
    protected static String selectedPrintName;
    protected static boolean printButtonClicked = false;
    protected static List<Ticket> ticketList = new ArrayList<Ticket>();
    protected static TicketListAdapter ticketListAdapter;

    private final static String TAG = "CommonActivity";

    public static class PrintFindResultDialog extends DialogFragment {
        static SimpleAdapter localPrinterListAdapter;
        public static PrintFindResultDialog newInstance(SimpleAdapter adapter) {
            PrintFindResultDialog frag = new PrintFindResultDialog();
            localPrinterListAdapter = adapter;
            Bundle args = new Bundle();
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.printer_find_result_dialog, null);
            builder.setView(layout);
            final AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            ListView listView = (ListView)layout.findViewById(R.id.listView_printerlist);
            listView.setAdapter(localPrinterListAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Context context = getActivity();
                    BaseActivity baseActivity = (BaseActivity)context;
                    ListView listView = (ListView) parent;
                    HashMap<String, String> item = (HashMap<String, String>) listView.getItemAtPosition(position);
                    selectedPrintIPAddress = item.get(Constant.PRINT_ADDRESS);
                    selectedPrintName = item.get(Constant.PRINT_MODEL);
                    if(selectedPrintIPAddress != null
                            && !selectedPrintIPAddress.equals("")
                            && selectedPrintName != null
                            && selectedPrintName.equals("")){
                        PrintFindErrorDialog df = PrintFindErrorDialog.newInstance(
                                getResources().getString(R.string.dialog_print_find_nownotuse_title),
                                getResources().getString(R.string.dialog_print_find_nownotuse),
                                selectedPrintIPAddress);
                        df.show(baseActivity.getFragmentManager(), "dialog");
                    }
                    if(printButtonClicked){
                        if(selectedPrintIPAddress != null
                                && !selectedPrintIPAddress.equals("")
                                && selectedPrintName != null
                                && !selectedPrintName.equals("")){
                            baseActivity.new AsyncTaskForPrint(baseActivity).execute();
                        }
                        printButtonClicked = false;
                    }
                    alertDialog.dismiss();
                }
            });
            return alertDialog;
        }
    }

    public class AsyncTaskForPrint extends AsyncTask<Void, Void, Void> {
        private DialogFragment statusCheckingD;
        private DialogFragment dataSendingD;
        private Context context;
        public AsyncTaskForPrint(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            statusCheckingD = CommonProgressDialog.newInstance(getResources().getString(R.string.dialog_print_find_progress_status_check_title));
            statusCheckingD.show(((Activity)context).getFragmentManager(), "dialog");
        }

        @Override
        protected Void doInBackground(Void... v) {
            PrintInfo printInfo = getPrinterName(selectedPrintIPAddress);
            selectedPrintName = printInfo.getName();

            if(!selectedPrintIPAddress.equals("")){
                if(selectedPrintName != null && !selectedPrintName.equals("")){
                    dataSendingD = CommonProgressDialog.newInstance(getResources().getString(R.string.dialog_print_find_progress_datasending_title));
                    dataSendingD.show(((Activity)context).getFragmentManager(), "dialog");

                    openPrinter();
                    for(Ticket ticket : ticketList){
                        printText(ticket);
                        cut();
                    }

                    closePrinter();
                }else{
                    PrintFindErrorDialog df = PrintFindErrorDialog.newInstance(
                        getResources().getString(R.string.dialog_print_find_nownotuse_title),
                        getResources().getString(R.string.dialog_print_find_nownotuse),
                        selectedPrintIPAddress);
                    df.show(((Activity)context).getFragmentManager(), "dialog");
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if(statusCheckingD != null){
                statusCheckingD.dismiss();
            }
            if(dataSendingD != null){
                dataSendingD.dismiss();
            }
            printButtonClicked = false;
        }
    }

    protected void onFindPrintProgress(DialogFragment dialog){
        printFindStartMilisec = System.currentTimeMillis();
        printFindProgress = dialog;
        printerList = new ArrayList<HashMap<String, String>>();
        printerListAdapter = new SimpleAdapter(this, printerList, R.layout.print_unit,
            new String[] { Constant.PRINT_ADDRESS, Constant.PRINT_MODEL },
            new int[] { R.id.print_ip_address, R.id.print_model });
        scheduler = Executors.newSingleThreadScheduledExecutor();
        findStart();
    }

    protected void findStart() {
        if(scheduler == null){
            return;
        }

        //stop old finder
        while(true) {
            try{
                Finder.stop();
                break;
            }catch(EpsonIoException e){
                if(e.getStatus() != IoStatus.ERR_PROCESSING){
                    break;
                }
            }
        }

        //stop find thread
        if(future != null){
            future.cancel(false);
            while(!future.isDone()){
                try{
                    Thread.sleep(DISCOVERY_INTERVAL);
                }catch(Exception e){
                    break;
                }
            }
            future = null;
        }

        //clear list
        printerList.clear();

        //get device type and find
        try{
            Finder.start(this, DevType.TCP, "255.255.255.255");
        }catch(Exception e){
            Log.e("Exception", ExceptionUtils.getStackTrace(e));
            return ;
        }

        //start thread
        future = scheduler.scheduleWithFixedDelay(this, 0, DISCOVERY_INTERVAL, TimeUnit.MILLISECONDS);
    }

    @Override
    //find thread
    public synchronized void run() {
        class UpdateListThread extends Thread{
            Context context;
            String[] list;
            public UpdateListThread(String[] listDevices, Context context) {
                list = listDevices;
                this.context = context;
            }

            @Override
            public void run() {
                int printFindElapseSec = (int)((System.currentTimeMillis() - printFindStartMilisec) / 1000);
                if(list == null){
                    if(printerList.size() > 0){
                        printerList.clear();
                    }
                    if(printFindElapseSec > printFindMaxTime){
                        PrintFindErrorDialog df = PrintFindErrorDialog.newInstance(
                                getResources().getString(R.string.dialog_print_find_timeout_title),
                                getResources().getString(R.string.dialog_print_find_timeout));
                        BaseActivity baseActivity = (BaseActivity)context;
                        df.show(baseActivity.getFragmentManager(), "dialog");
                        if(printFindProgress != null){
                            printFindProgress.dismiss();
                        }
                        future.cancel(true);
                    }
                }else if(list.length != printerList.size()){
                    printerList.clear();
                    for(String name : list){
                        HashMap<String, String> item = new HashMap<String, String>();
                        item.put(Constant.PRINT_ADDRESS, name);
                        PrintInfo printInfo = getPrinterName(name);
                        if(printInfo != null){
                            String printerName = printInfo.getName();
                            item.put(Constant.PRINT_MODEL, printerName == null ? "" : printerName);
                        }
                        printerList.add(item);
                    }
                    printerListAdapter.notifyDataSetChanged();
                    if(printFindProgress != null){
                        printFindProgress.dismiss();
                    }
                    PrintFindResultDialog df = PrintFindResultDialog.newInstance(printerListAdapter);
                    BaseActivity baseActivity = (BaseActivity)context;
                    df.show(baseActivity.getFragmentManager(), "dialog");
                    future.cancel(true);
                }
            }
        }

        String[] deviceList = null;
        try{
            deviceList = Finder.getResult();
            handler.post(new UpdateListThread(deviceList, this));
        }catch(Exception e){
            Log.e("Exception", ExceptionUtils.getStackTrace(e));
            return;
        }
    }

    protected class PrintInfo{
        private String name;
        private String font;
        private boolean analysisFlag;
        public PrintInfo(){
        }
        public PrintInfo(String name, String font, boolean flag){
            this.name = name;
            this.font = font;
            this.analysisFlag = flag;
        }
        public String getFont() {
            return font;
        }
        public void setFont(String font) {
            this.font = font;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public boolean isAnalysisFlag() {
            return analysisFlag;
        }
        public void setAnalysisFlag(boolean analysisFlag) {
            this.analysisFlag = analysisFlag;
        }
    }

    protected PrintInfo getPrinterName(String printAddress) {
        PrintInfo printInfo = new PrintInfo();
        EpsonIo port = null;
        try{
            port = new EpsonIo();
            port.open(DevType.TCP, printAddress, null);
            port.write(CMD_ESDPOS, 0, CMD_ESDPOS.length, SEND_RESPONSE_TIMEOUT);
            printInfo = receiveResponse(port, printInfo);
            port.close();
        }catch(Exception e){
            Log.e("Exception", ExceptionUtils.getStackTrace(e));
            try{
                if(port != null){
                    port.close();
                    port = null;
                }
            }catch(Exception e1){
                port = null;
            }
        }

        return printInfo;
    }

    protected PrintInfo receiveResponse(EpsonIo port, PrintInfo printInfo) throws EpsonIoException{
        byte[] receiveBuffer = new byte[RESPONSE_MAXBYTE];
        int receiveCurrent = 0;

        //receive loop(timeout=1000ms)
        long starttime = System.currentTimeMillis();
        while(true){
            //check receive buffer full
            if(receiveCurrent >= receiveBuffer.length){
                break;
            }

            //timeout check
            long readTimeout = SEND_RESPONSE_TIMEOUT - (System.currentTimeMillis() - starttime);
            if(readTimeout < 0){
                break;
            }

            //receive
            int sizeRead;
            try {
                sizeRead = port.read(receiveBuffer,
                        receiveCurrent, receiveBuffer.length - receiveCurrent, (int)readTimeout);
            } catch (EpsonIoException e) {
                if(e.getStatus() == IoStatus.ERR_TIMEOUT){
                    break;
                }else{
                    throw e;
                }
            }
            receiveCurrent += sizeRead;

            //analyze receive data
            printInfo = analyzeResponse(receiveBuffer, receiveCurrent, printInfo);
            if(printInfo.isAnalysisFlag()){
                break;
            }
        }

        return printInfo;
    }

    //analyze GS I response
    protected PrintInfo analyzeResponse(byte[] buffer, int bufferlen, PrintInfo printInfo){
        int currentPos = 0;
        String responseFontName = null;
        String responsePrinterName = null;
        boolean isNormal = true;

        while(responseFontName == null || responsePrinterName == null){
            //check 5f header
            if(buffer[currentPos] != RESPONSE_HEADER){
                isNormal = false;
                break;
            }

            //find terminal(0x00)
            int endPos = currentPos + 1;
            for(; endPos < bufferlen; endPos++){
                if(buffer[endPos] == RESPONSE_TERMINAL){
                    break;
                }
            }
            if(endPos >= bufferlen){
                isNormal = false;
                break;
            }

            //get response string
            String responseString = null;
            try {
                responseString = new String(buffer, currentPos + 1, endPos - currentPos - 1, "US-ASCII");
            } catch (UnsupportedEncodingException e) {
                isNormal = false;
                break;
            }
            if(responseFontName == null){
                responseFontName = responseString;
            }else{
                responsePrinterName = responseString;
            }

            currentPos = endPos + 1;
        }

        printInfo.setFont(responseFontName);
        printInfo.setName(responsePrinterName);
        printInfo.setAnalysisFlag(isNormal);
        return printInfo;
    }

    protected static void setPrinter(Print obj){
        printer = obj;
    }

    protected static Print getPrinter(){
        return printer;
    }

    protected static void closePrinter(){
        try{
            if(printer != null){
                printer.closePrinter();
            }
            printer = null;
        }catch(Exception e){
            printer = null;
            Log.e("Exception", ExceptionUtils.getStackTrace(e));
        }
    }

    protected void openPrinter() {
        if(selectedPrintIPAddress == null){
            return;
        }

        if(printer != null){
            closePrinter();
        }
        //open
        printer = new Print();
        try{
            printer.openPrinter(Print.DEVTYPE_TCP, selectedPrintIPAddress, Print.FALSE, 1000);
        }catch(Exception e){
            printer = null;
            Log.e("Exception", ExceptionUtils.getStackTrace(e));
            return;
        }
    }

    protected void printText(Ticket ticket){
        Builder builder = null;
        try{
            builder = new Builder(StringUtil.getPrintName(getResources(), selectedPrintName), Builder.MODEL_JAPANESE);
            builder.addTextLang(Builder.LANG_JA);
            builder.addTextSize(1,1);
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.FALSE, Builder.COLOR_1);
            builder.addTextPosition(0);
            builder.addText(StringUtil.getReceiptContent(getResources(), ticket));
            builder.addFeedUnit(30);

            int[] status = new int[1];
            int[] battery = new int[1];
            try{
                printer.sendData(builder, SEND_TIMEOUT, status, battery);
            }catch(EposException e){
                Log.e("Exception", ExceptionUtils.getStackTrace(e));
            }
        }catch(Exception e){
            Log.e("Exception", ExceptionUtils.getStackTrace(e));
        }

        if(builder != null){
            try{
                builder.clearCommandBuffer();
                builder = null;
            }catch(Exception e){
                builder = null;
                Log.e("Exception", ExceptionUtils.getStackTrace(e));
            }
        }
    }

    protected void cut(){
        Builder builder = null;
        try{
            builder = new Builder(StringUtil.getPrintName(getResources(), selectedPrintName), Builder.MODEL_JAPANESE);
            builder.addCut(Builder.CUT_FEED);

            //send builder data
            int[] status = new int[1];
            int[] battery = new int[1];
            try{
                printer.sendData(builder, SEND_TIMEOUT, status, battery);
            }catch(EposException e){
                Log.e("Exception", ExceptionUtils.getStackTrace(e));
            }
        }catch(Exception e){
            Log.e("Exception", ExceptionUtils.getStackTrace(e));
        }

        //remove builder
        if(builder != null){
            try{
                builder.clearCommandBuffer();
                builder = null;
            }catch(Exception e){
                builder = null;
                Log.e("Exception", ExceptionUtils.getStackTrace(e));
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //stop find
        if(future != null){
            future.cancel(false);
            while(!future.isDone()){
                try{
                    Thread.sleep(DISCOVERY_INTERVAL);
                }catch(Exception e){
                    break;
               }
            }
            future = null;
        }
        if(scheduler != null){
            scheduler.shutdown();
            scheduler = null;
        }
        //stop old finder
        while(true) {
            try{
                Finder.stop();
                break;
            }catch(EpsonIoException e){
                if(e.getStatus() != IoStatus.ERR_PROCESSING){
                    break;
                }
            }
        }

        closePrinter();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ApplicationFinishDialog df = ApplicationFinishDialog.newInstance();
            df.show(getFragmentManager(), "dialog");
            return true;
        }
        return false;
    }

    public static class ApplicationFinishDialog extends DialogFragment {
        public static ApplicationFinishDialog newInstance() {
            ApplicationFinishDialog frag = new ApplicationFinishDialog();
            Bundle args = new Bundle();
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.application_backbutton_dialog, null);
            builder.setView(layout);
            final AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            Button cancelButton = (Button)layout.findViewById(R.id.button_cancel);
            cancelButton.setOnClickListener(new OnClickListener(){
                public void onClick(View v){
                    alertDialog.dismiss();
                }
            });

            Button printConfirmButton = (Button)layout.findViewById(R.id.button_confirm);
            printConfirmButton.setOnClickListener(new OnClickListener(){
                public void onClick(View v){
                    alertDialog.dismiss();

                    Context context = getActivity();
                    BaseActivity common = (BaseActivity)context;
                    common.finish();
                }
            });

            return alertDialog;
        }
    }
}
