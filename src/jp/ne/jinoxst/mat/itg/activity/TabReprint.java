package jp.ne.jinoxst.mat.itg.activity;

import java.net.UnknownHostException;

import jp.ne.jinoxst.mat.itg.R;
import jp.ne.jinoxst.mat.itg.activity.adapter.TicketListAdapter;
import jp.ne.jinoxst.mat.itg.activity.dialog.CommonProgressDialog;
import jp.ne.jinoxst.mat.itg.activity.dialog.ShowTicketDialog;
import jp.ne.jinoxst.mat.itg.activity.dialog.SimpleDialogFragment;
import jp.ne.jinoxst.mat.itg.pojo.ReprintData;
import jp.ne.jinoxst.mat.itg.pojo.Ticket;
import jp.ne.jinoxst.mat.itg.pojo.json.Response061;
import jp.ne.jinoxst.mat.itg.pojo.json.Response061Gen;
import jp.ne.jinoxst.mat.itg.pojo.json.Response081;
import jp.ne.jinoxst.mat.itg.pojo.json.Response081Gen;
import jp.ne.jinoxst.mat.itg.util.Constant;
import jp.ne.jinoxst.mat.itg.util.GlobalRegistry;
import jp.ne.jinoxst.mat.itg.util.StringUtil;
import jp.ne.jinoxst.mat.itg.util.https.HttpsClientConnector;
import net.vvakame.util.jsonpullparser.JsonFormatException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class TabReprint extends BaseActivity {
    private final static String TAG = "TabReprint";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalRegistry registry = GlobalRegistry.getInstance();
        registry.setRegistry(Constant.SELECTED_TAB_INDEX, Constant.SELECTED_TAB_INDEX_2);

        new AsyncTaskForWebApi061().execute();
    }

    private void initialize(){
        setContentView(R.layout.reprintlist_def);

        TableLayout tableLayoutTitle = (TableLayout)findViewById(R.id.reprintlist_tablelayout_title);
        tableLayoutTitle.setColumnStretchable(0, true);
        tableLayoutTitle.setColumnStretchable(1, true);
        tableLayoutTitle.setColumnStretchable(2, true);
        tableLayoutTitle.setColumnStretchable(3, true);
        tableLayoutTitle.setColumnStretchable(4, true);
        tableLayoutTitle.setColumnStretchable(5, true);
        makeTopTitle(tableLayoutTitle);

        if(reprintList != null && reprintList.size() > 0){
            TableLayout tableLayoutData = (TableLayout)findViewById(R.id.reprintlist_tablelayout_data);
            tableLayoutData.setColumnStretchable(0, true);
            tableLayoutData.setColumnStretchable(1, true);
            tableLayoutData.setColumnStretchable(2, true);
            tableLayoutData.setColumnStretchable(3, true);
            tableLayoutData.setColumnStretchable(4, true);
            tableLayoutData.setColumnStretchable(5, true);

            TableRow tr = null;
            for(int i=0;i<reprintList.size();i++){
                if(i % 2 == 0){
                    tr = (TableRow)getLayoutInflater().inflate(R.layout.reprintlist_def_tablerow_data1, null);
                }else{
                    tr = (TableRow)getLayoutInflater().inflate(R.layout.reprintlist_def_tablerow_data2, null);
                }
                ReprintData data = reprintList.get(i);
                makeReprintListData(tableLayoutData, tr, data, i);
            }
            setReissueButtonVisible(true);
        }else{
            makeNoData(tableLayoutTitle);
            setReissueButtonVisible(false);
        }

        setReissueButtonActivate(false);
    }

    private void setReissueButtonVisible(boolean flag){
        Button button = (Button)findViewById(R.id.button_reprintlist_reissue);
        if(flag){
            button.setVisibility(View.VISIBLE);
        }else{
            button.setVisibility(View.GONE);
        }
    }

    private void setReissueButtonActivate(boolean flag){
        Button reissueB = (Button)findViewById(R.id.button_reprintlist_reissue);
        if(flag){
            reissueB.setBackgroundResource(R.drawable.button_indicator);
            reissueB.setTextColor(getResources().getColor(R.color.button_default_text));
            reissueB.setEnabled(true);
        }else{
            reissueB.setBackgroundResource(R.drawable.button_indicator_disable);
            reissueB.setTextColor(getResources().getColor(R.color.button_diable));
            reissueB.setEnabled(false);
        }
        reissueB.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                TableLayout table = (TableLayout)findViewById(R.id.reprintlist_tablelayout_data);
                String oid = "";
                for(int i=0;i<table.getChildCount();i++){
                    TableRow row = (TableRow)table.getChildAt(i);
                    LinearLayout layout = (LinearLayout)row.findViewById(R.id.reprintlist_tablerow_data0);
                    CheckBox chb = (CheckBox)layout.findViewById(R.id.reprintlist_checkbox);
                    ReprintData data = (ReprintData)chb.getTag();
                    if(chb.isChecked()){
                        oid += data.getSmlSid() + ",";
                    }
                }
                if(!oid.equals("")){
                    String params[] = {oid};
                    ReIssueConfirmDialog df = ReIssueConfirmDialog.newInstance(params);
                    df.show(getFragmentManager(), "dialog");
                }
            }
        });
    }

    public void onTaskPreExecute(){
        setContentView(R.layout.default_progress_layout);
    }

    public void onTaskCompleted(){
        initialize();
    }

    public class AsyncTaskForWebApi061 extends AsyncTask<Void, Void, Void> {

        public AsyncTaskForWebApi061(){
        }

        @Override
        protected void onPreExecute() {
            onTaskPreExecute();
        }

        @Override
        protected Void doInBackground(Void... v) {
            long l1 = System.currentTimeMillis();
            HttpsClientConnector connector = new HttpsClientConnector(Constant.SERVICE_CODE_061);
            HttpGet request = connector.getRequest();
            DefaultHttpClient httpClient = connector.getHttpClient();
            try {
                HttpResponse response = httpClient.execute(request);
                HttpEntity entity = response.getEntity();
                Response061 res = Response061Gen.get(entity.getContent());
                reprintList = res.getReprintlist();
                if(res.getStatus() != 0 && res.getStatus() != Constant.REISSUELIST_NOT_EXIST){
                    showDialog(res.getMessage());
                }
            } catch (JsonFormatException j) {
                Log.e("JsonFormatException", ExceptionUtils.getStackTrace(j));
                showDialog(getResources().getString(R.string.alert_dialog_error_protocol_contents));
            } catch (HttpHostConnectException h) {
                Log.e("HttpHostConnectException", ExceptionUtils.getStackTrace(h));
                showDialog(getResources().getString(R.string.alert_dialog_error_network_contents));
            } catch (UnknownHostException u) {
                Log.e("UnknownHostException", ExceptionUtils.getStackTrace(u));
                showDialog(getResources().getString(R.string.alert_dialog_error_network_contents));
            } catch (Exception e) {
                Log.e("Exception", ExceptionUtils.getStackTrace(e));
                showDialog(getResources().getString(R.string.alert_dialog_error_system_contents));
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
            long l2 = System.currentTimeMillis();
            Log.d("AsyncTaskForWebApi022", "timelap:" + (l2 - l1));
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            onTaskCompleted();
        }
    }

    public class AsyncTaskForWebApi081 extends AsyncTask<String, Void, Void> {
        private int apiResult;

        public AsyncTaskForWebApi081(){
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(String... s) {
            long l1 = System.currentTimeMillis();
            HttpsClientConnector connector = new HttpsClientConnector(Constant.SERVICE_CODE_081);
            connector.setParameter(Constant.OID, s[0]);
            HttpGet request = connector.getRequest();
            DefaultHttpClient httpClient = connector.getHttpClient();
            try {
                HttpResponse response = httpClient.execute(request);
                HttpEntity entity = response.getEntity();
                Response081 res = Response081Gen.get(entity.getContent());
                ticketList = res.getReissuelist();
                apiResult = res.getStatus();
                if(apiResult != 0){
                    showDialog(res.getMessage());
                }
            } catch (JsonFormatException j) {
                Log.e("JsonFormatException", ExceptionUtils.getStackTrace(j));
                showDialog(getResources().getString(R.string.alert_dialog_error_protocol_contents));
            } catch (HttpHostConnectException h) {
                Log.e("HttpHostConnectException", ExceptionUtils.getStackTrace(h));
                showDialog(getResources().getString(R.string.alert_dialog_error_network_contents));
            } catch (UnknownHostException u) {
                Log.e("UnknownHostException", ExceptionUtils.getStackTrace(u));
                showDialog(getResources().getString(R.string.alert_dialog_error_network_contents));
            } catch (Exception e) {
                Log.e("Exception", ExceptionUtils.getStackTrace(e));
                showDialog(getResources().getString(R.string.alert_dialog_error_system_contents));
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
            long l2 = System.currentTimeMillis();
            Log.d("AsyncTaskForWebApi081", "timelap:" + (l2 - l1));
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if(apiResult == 0){
                ticketListAdapter = new TicketListAdapter(TabReprint.this,R.layout.ticket_unit,ticketList);
                ReIssueCompleteDialog df = ReIssueCompleteDialog.newInstance();
                df.show(getFragmentManager(), "dialog");
            }
        }
    }

    public static class ReIssueCompleteDialog extends DialogFragment {
        public static ReIssueCompleteDialog newInstance() {
            ReIssueCompleteDialog frag = new ReIssueCompleteDialog();
            Bundle args = new Bundle();
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.cart_issue_complete_dialog, null);
            TextView textV = (TextView)layout.findViewById(R.id.issue_complete_title);
            textV.setText(getResources().getString(R.string.reissue_complete));
            builder.setView(layout);
            final AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            ListView listView = (ListView)layout.findViewById(R.id.listView_ticketlist);

            listView.setAdapter(ticketListAdapter);
            ticketListAdapter.notifyDataSetChanged();
            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Ticket ticket = (Ticket)ticketListAdapter.getItem(position);
                    ShowTicketDialog df = ShowTicketDialog.newInstance(ticket);
                    df.show(getActivity().getFragmentManager(), "dialog");
                }
            });

            Button printButton = (Button)layout.findViewById(R.id.button_dialog_print_content);
            printButton.setOnClickListener(new OnClickListener(){
                Context context = getActivity();
                TabReprint tabReprint = (TabReprint)context;
                public void onClick(View v){
                    printButtonClicked = true;
                    if(selectedPrintIPAddress != null){
                        tabReprint.new AsyncTaskForPrint(tabReprint).execute();
                    }else{
                        CommonProgressDialog df = CommonProgressDialog.newInstance(getResources().getString(R.string.dialog_print_find_progress_title));
                        df.show(getActivity().getFragmentManager(), "dialog");
                        tabReprint.onFindPrintProgress(df);
                    }
                }
            });

            Button qrcodePageButton = (Button)layout.findViewById(R.id.button_dialog_qrcode_page);
            qrcodePageButton.setOnClickListener(new OnClickListener(){
                public void onClick(View v){
                    alertDialog.dismiss();
                }
            });

            Button closeButton = (Button)layout.findViewById(R.id.button_dialog_close);
            closeButton.setOnClickListener(new OnClickListener(){
                public void onClick(View v){
                    ReIssueCloseConfirmDialog df = ReIssueCloseConfirmDialog.newInstance(alertDialog);
                    df.show(getActivity().getFragmentManager(), "dialog");
                }
            });

            return alertDialog;
        }
    }

    public static class ReIssueCloseConfirmDialog extends DialogFragment {
        static AlertDialog parentD;
        public static ReIssueCloseConfirmDialog newInstance(AlertDialog d) {
            parentD = d;
            ReIssueCloseConfirmDialog frag = new ReIssueCloseConfirmDialog();
            Bundle args = new Bundle();
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.cart_issue_close_confirm_dialog, null);
            TextView textV =  (TextView)layout.findViewById(R.id.issue_print_waring_message);
            textV.setText(getResources().getString(R.string.reissue_print_waring_before_close));
            builder.setView(layout);
            final AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);

            Button closeButton = (Button)layout.findViewById(R.id.button_dailog_close_ok);
            closeButton.setText(getResources().getString(R.string.button_dialog_reprint_complete));
            closeButton.setOnClickListener(new OnClickListener(){
                public void onClick(View v){
                    alertDialog.dismiss();
                    parentD.dismiss();

                    Context context = getActivity();
                    TabReprint tabReprint = (TabReprint)context;
                    tabReprint.new AsyncTaskForWebApi061().execute();
                }
            });

            Button cancelButton = (Button)layout.findViewById(R.id.button_dialog_cancel);
            cancelButton.setOnClickListener(new OnClickListener(){
                public void onClick(View v){
                    alertDialog.dismiss();
                }
            });

            return alertDialog;
        }
    }

    public static class ReIssueConfirmDialog extends DialogFragment {
        public static ReIssueConfirmDialog newInstance(String str[]) {
            ReIssueConfirmDialog frag = new ReIssueConfirmDialog();
            Bundle args = new Bundle();
            args.putStringArray("params", str);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            String params[] = getArguments().getStringArray("params");
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.reissue_confirm_dialog, null);

            builder.setView(layout);
            final AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            Button cancelButton = (Button)layout.findViewById(R.id.button_dialog_cancel);
            cancelButton.setOnClickListener(new OnClickListener(){
                public void onClick(View v){
                    alertDialog.dismiss();
                }
            });

            Button printConfirmButton = (Button)layout.findViewById(R.id.button_dialog_print_confirm);
            printConfirmButton.setOnClickListener(new OnClickListener(){
                public void onClick(View v){
                    CommonProgressDialog df = CommonProgressDialog.newInstance(getResources().getString(R.string.dialog_print_find_progress_title));
                    df.show(getActivity().getFragmentManager(), "dialog");
                    Context context = getActivity();
                    TabReprint tabReprint = (TabReprint)context;
                    tabReprint.onFindPrintProgress(df);
                }
            });

            Button issueButton = (Button)layout.findViewById(R.id.button_dialog_issue);
            issueButton.setTag(params);
            issueButton.setOnClickListener(new OnClickListener(){
                public void onClick(View v){
                    alertDialog.dismiss();
                    String params[] = (String[])v.getTag();
                    TabReprint tabReprint = (TabReprint)getActivity();
                    tabReprint.new AsyncTaskForWebApi081().execute(params);
                }
            });

            return alertDialog;
        }
    }

    private void showDialog(String msg) {
        DialogFragment df = SimpleDialogFragment.newInstance(msg);
        df.show(getFragmentManager(), "dialog");
    }

    private void makeTopTitle(TableLayout tableLayout){
        TableRow tr = (TableRow)getLayoutInflater().inflate(R.layout.reprintlist_def_tablerow_title, null);
        LinearLayout col0 = (LinearLayout)tr.findViewById(R.id.reprintlist_tablerow_title0);
        TextView chV = (TextView)getLayoutInflater().inflate(R.layout.reprintlist_title, null);
        chV.setText("");
        col0.addView(chV);

        LinearLayout col1 = (LinearLayout)tr.findViewById(R.id.reprintlist_tablerow_title1);
        TextView itemV = (TextView)getLayoutInflater().inflate(R.layout.reprintlist_title, null);
        itemV.setText(getResources().getString(R.string.reprintlist_toptitle_item));
        col1.addView(itemV);

        LinearLayout col2 = (LinearLayout)tr.findViewById(R.id.reprintlist_tablerow_title2);
        TextView orderTimeV = (TextView)getLayoutInflater().inflate(R.layout.reprintlist_title, null);
        orderTimeV.setText(getResources().getString(R.string.reprintlist_toptitle_order_time));
        col2.addView(orderTimeV);

        LinearLayout col3 = (LinearLayout)tr.findViewById(R.id.reprintlist_tablerow_title3);
        TextView managenoV = (TextView)getLayoutInflater().inflate(R.layout.reprintlist_title, null);
        managenoV.setText(getResources().getString(R.string.reprintlist_toptitle_manageno));
        col3.addView(managenoV);

        LinearLayout col4 = (LinearLayout)tr.findViewById(R.id.reprintlist_tablerow_title4);
        TextView reissueTimeV = (TextView)getLayoutInflater().inflate(R.layout.reprintlist_title, null);
        reissueTimeV.setText(getResources().getString(R.string.reprintlist_toptitle_reissue_time));
        col4.addView(reissueTimeV);

        LinearLayout col5 = (LinearLayout)tr.findViewById(R.id.reprintlist_tablerow_title5);
        TextView reissueCountV = (TextView)getLayoutInflater().inflate(R.layout.reprintlist_title, null);
        reissueCountV.setText(getResources().getString(R.string.reprintlist_toptitle_reissue_count));
        col5.addView(reissueCountV);

        tableLayout.addView(tr, new TableLayout.LayoutParams(Constant.MP,Constant.WC));
    }

    private void makeNoData(TableLayout tableLayout){
        TableRow tr = (TableRow)getLayoutInflater().inflate(R.layout.reprintlist_def_tablerow_nodata, null);
        LinearLayout col0 = (LinearLayout)tr.findViewById(R.id.reprintlist_tablerow_nodata0);
        TextView dummyTextView0 = (TextView)getLayoutInflater().inflate(R.layout.reprintlist_textnodata, null);
        dummyTextView0.setText("");
        col0.addView(dummyTextView0);

        LinearLayout col1 = (LinearLayout)tr.findViewById(R.id.reprintlist_tablerow_nodata1);
        TextView dummyTextView1 = (TextView)getLayoutInflater().inflate(R.layout.reprintlist_textnodata, null);
        dummyTextView1.setText("");
        col1.addView(dummyTextView1);

        LinearLayout col2 = (LinearLayout)tr.findViewById(R.id.reprintlist_tablerow_nodata2);
        TextView dummyTextView2 = (TextView)getLayoutInflater().inflate(R.layout.reprintlist_textnodata, null);
        dummyTextView2.setText("");
        col2.addView(dummyTextView2);

        LinearLayout col3 = (LinearLayout)tr.findViewById(R.id.reprintlist_tablerow_nodata3);
        TextView dummyTextView3 = (TextView)getLayoutInflater().inflate(R.layout.reprintlist_textnodata, null);
        dummyTextView3.setText("");
        col3.addView(dummyTextView3);

        LinearLayout col4 = (LinearLayout)tr.findViewById(R.id.reprintlist_tablerow_nodata4);
        TextView dummyTextView4 = (TextView)getLayoutInflater().inflate(R.layout.reprintlist_textnodata, null);
        dummyTextView4.setText("");
        col4.addView(dummyTextView4);

        LinearLayout col5 = (LinearLayout)tr.findViewById(R.id.reprintlist_tablerow_nodata5);
        TextView dummyTextView5 = (TextView)getLayoutInflater().inflate(R.layout.reprintlist_textnodata, null);
        dummyTextView5.setText("");
        col5.addView(dummyTextView5);

        tableLayout.addView(tr, new TableLayout.LayoutParams(Constant.MP,Constant.WC));
    }

    private boolean isCheckBoxChecked(){
        boolean checked = false;
        TableLayout table = (TableLayout)findViewById(R.id.reprintlist_tablelayout_data);
        for(int i=0;i<table.getChildCount();i++){
            TableRow row = (TableRow)table.getChildAt(i);
            LinearLayout layout = (LinearLayout)row.getChildAt(0);
            CheckBox chb = (CheckBox)layout.findViewById(R.id.reprintlist_checkbox);
            if(chb.isChecked()){
                checked = true;
                break;
            }
        }
        return checked;
    }

    private void makeReprintListData(TableLayout tableLayout, TableRow tr, ReprintData data, int rowIndex){
        LinearLayout col0 = (LinearLayout)tr.findViewById(R.id.reprintlist_tablerow_data0);
        final CheckBox checkbox = (CheckBox)getLayoutInflater().inflate(R.layout.reprint_checkbox, null);
        checkbox.setTag(data);
        checkbox.setTag(R.string.tagkey_reprintlist_tr_index, Integer.valueOf(rowIndex));
        checkbox.setTag(R.string.tagkey_reprintlist_tr, tr);
        checkbox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(isCheckBoxChecked()){
                    setReissueButtonActivate(true);
                }else{
                    setReissueButtonActivate(false);
                }

                TableRow tr = (TableRow)v.getTag(R.string.tagkey_reprintlist_tr);
                if(checkbox.isChecked()){
                    for(int i=0;i<tr.getChildCount();i++){
                        LinearLayout out = (LinearLayout)tr.getChildAt(i);
                        out.setBackgroundResource(R.color.reprintlist_tr_clicked);
                    }
                }else{
                    for(int i=0;i<tr.getChildCount();i++){
                        LinearLayout out = (LinearLayout)tr.getChildAt(i);
                        int rowIndex = ((Integer)v.getTag(R.string.tagkey_reprintlist_tr_index)).intValue();
                        if(rowIndex % 2 == 0){
                            out.setBackgroundResource(R.color.cartlist_data1_background);
                        }else{
                            out.setBackgroundResource(R.color.cartlist_data2_background);
                        }
                    }
                }
            }
        });
        col0.addView(checkbox);

        LinearLayout col1 = (LinearLayout)tr.findViewById(R.id.reprintlist_tablerow_data1);
        LinearLayout layout = (LinearLayout)getLayoutInflater().inflate(R.layout.reprint_item, null);
        ImageView imageV = (ImageView)layout.findViewById(R.id.brand_image);
        Bitmap itembitmap = BitmapFactory.decodeFile(this.getFilesDir().getPath()+"/"+data.getBrandImg());
        imageV.setImageBitmap(itembitmap);
        TextView priceV = (TextView) layout.findViewById(R.id.emm_price);
        priceV.setText(getResources().getString(R.string.yen_mark)+StringUtil.getCurrencyFormat(data.getEmmPrice()));
        col1.addView(layout);

        LinearLayout col2 = (LinearLayout)tr.findViewById(R.id.reprintlist_tablerow_data2);
        TextView orderTimeV = (TextView)getLayoutInflater().inflate(R.layout.reprintlist_textdata, null);
        orderTimeV.setText(data.getOrderTime());
        col2.addView(orderTimeV);

        LinearLayout col3 = (LinearLayout)tr.findViewById(R.id.reprintlist_tablerow_data3);
        TextView managenoV = (TextView)getLayoutInflater().inflate(R.layout.reprintlist_textdata, null);
        managenoV.setText(data.getManageno());
        col3.addView(managenoV);

        LinearLayout col4 = (LinearLayout)tr.findViewById(R.id.reprintlist_tablerow_data4);
        TextView reissueTimeV = (TextView)getLayoutInflater().inflate(R.layout.reprintlist_textdata, null);
        reissueTimeV.setText(data.getReissueTime());
        col4.addView(reissueTimeV);

        LinearLayout col5 = (LinearLayout)tr.findViewById(R.id.reprintlist_tablerow_data5);
        TextView reissueCountV = (TextView)getLayoutInflater().inflate(R.layout.reprintlist_textdata, null);
        reissueCountV.setText(String.valueOf(data.getReissueCount()));
        col5.addView(reissueCountV);

        tr.setTag(data);
        tr.setTag(R.string.tagkey_reprintlist_tr_index, Integer.valueOf(rowIndex));
        tr.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ReprintData data = (ReprintData)v.getTag();
                checkbox.setTag(data);
                checkbox.setChecked(!checkbox.isChecked());

                TableRow tr = (TableRow)v;
                if(checkbox.isChecked()){
                    for(int i=0;i<tr.getChildCount();i++){
                        LinearLayout out = (LinearLayout)tr.getChildAt(i);
                        out.setBackgroundResource(R.color.reprintlist_tr_clicked);
                    }
                }else{
                    for(int i=0;i<tr.getChildCount();i++){
                        LinearLayout out = (LinearLayout)tr.getChildAt(i);
                        int rowIndex = ((Integer)v.getTag(R.string.tagkey_reprintlist_tr_index)).intValue();
                        if(rowIndex % 2 == 0){
                            out.setBackgroundResource(R.color.cartlist_data1_background);
                        }else{
                            out.setBackgroundResource(R.color.cartlist_data2_background);
                        }
                    }
                }

                if(isCheckBoxChecked()){
                    setReissueButtonActivate(true);
                }else{
                    setReissueButtonActivate(false);
                }
            }
        });

        tableLayout.addView(tr, new TableLayout.LayoutParams(Constant.MP,Constant.WC));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}