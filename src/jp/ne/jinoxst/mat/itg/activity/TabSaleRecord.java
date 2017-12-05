package jp.ne.jinoxst.mat.itg.activity;

import java.lang.reflect.Field;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.List;

import jp.ne.jinoxst.mat.itg.R;
import jp.ne.jinoxst.mat.itg.activity.dialog.CommonProgressDialog;
import jp.ne.jinoxst.mat.itg.activity.dialog.SimpleDialogFragment;
import jp.ne.jinoxst.mat.itg.pojo.SaleRecord;
import jp.ne.jinoxst.mat.itg.pojo.SaleRecordDayDetail;
import jp.ne.jinoxst.mat.itg.pojo.SaleRecordItem;
import jp.ne.jinoxst.mat.itg.pojo.SaleRecordItemDetail;
import jp.ne.jinoxst.mat.itg.pojo.json.Response091;
import jp.ne.jinoxst.mat.itg.pojo.json.Response091Gen;
import jp.ne.jinoxst.mat.itg.pojo.json.Response092;
import jp.ne.jinoxst.mat.itg.pojo.json.Response092Gen;
import jp.ne.jinoxst.mat.itg.pojo.json.Response093;
import jp.ne.jinoxst.mat.itg.pojo.json.Response093Gen;
import jp.ne.jinoxst.mat.itg.pojo.json.Response094;
import jp.ne.jinoxst.mat.itg.pojo.json.Response094Gen;
import jp.ne.jinoxst.mat.itg.pojo.json.Response095;
import jp.ne.jinoxst.mat.itg.pojo.json.Response095Gen;
import jp.ne.jinoxst.mat.itg.pojo.json.Response096;
import jp.ne.jinoxst.mat.itg.pojo.json.Response096Gen;
import jp.ne.jinoxst.mat.itg.util.Constant;
import jp.ne.jinoxst.mat.itg.util.DateUtil;
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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class TabSaleRecord extends BaseActivity {
    private final static String TAG = "TabSaleRecord";
    private static List<SaleRecord> saleRecordList;
    private static List<SaleRecordItem> saleRecordItemList;
    private static List<SaleRecordDayDetail> dayDetailList;
    private static List<SaleRecordItemDetail> saleRecordItemDetailList;
    private static String errorMsg;
    private static TextView dateTextView;
    private static String apiDateStr;
    private static CommonProgressDialog cdf;
    private static int mCurCheckPosition = 0;
    private static ListView leftmenuList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.salerecord_leftmenulist);
        GlobalRegistry registry = GlobalRegistry.getInstance();
        registry.setRegistry(Constant.SELECTED_TAB_INDEX, Constant.SELECTED_TAB_INDEX_3);
    }

    public static class LeftMenuListManager extends ListFragment {
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            GlobalRegistry registry = GlobalRegistry.getInstance();
            if (registry.getInt(Constant.SELECTED_TAB_INDEX) == Constant.SELECTED_TAB_INDEX_3) {
                setListAdapter(new ArrayAdapter<String>(getActivity(),
                        R.layout.leftmenu_unit,
                        R.id.leftmenu_text1, StringUtil.getLeftmenuList(getResources())));

                if (savedInstanceState != null) {
                    mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
                } else {
                    mCurCheckPosition = 0;
                }
                getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                getListView().setItemChecked(mCurCheckPosition, true);
                leftmenuList = getListView();

                Context context = getActivity();
                TabSaleRecord tsr = (TabSaleRecord)context;
                tsr.showItemList(mCurCheckPosition);
            }
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt("curChoice", mCurCheckPosition);
        }

        @Override
        public void onListItemClick(ListView lv, View v, int position, long id) {
            Context context = getActivity();
            TabSaleRecord tsr = (TabSaleRecord)context;
            tsr.showItemList(position);
        }
    }

    private void setLeftmenuIndex(int index){
        mCurCheckPosition = index;
        leftmenuList.setItemChecked(index, true);
    }

    void showItemList(int index) {
        mCurCheckPosition = index;
        if (index == 0) {
            new AsyncTaskForWebApi091().execute();
        } else if (index == 1) {
            new AsyncTaskForWebApi093().execute("");
        } else if (index == 2) {
            new AsyncTaskForWebApi094().execute("");
        } else if (index == 3) {
            new AsyncTaskForWebApi095().execute("");
        }
    }

    private static void showDialog(Context context, String msg) {
        DialogFragment df = SimpleDialogFragment.newInstance(msg);
        df.show(((Activity) context).getFragmentManager(), "dialog");
    }

    public static String getChartScript(int index, Resources resource, String... str) {
        StringBuffer sb = new StringBuffer();
        if (index < 1) {
            String headerScript = StringUtil.getAssetLineChartHeader(resource);
            sb.append(headerScript);
            sb.append("[\"").append(resource.getString(R.string.chart0_x_axis_text) + "\",").append(str[0])
                    .append("],");
            sb.append("[\"").append(resource.getString(R.string.chart0_y_axis_text) + "\",").append(str[1]).append("]");
            sb.append(StringUtil.getAssetChartFooter(resource));
        } else {
            String headerScript = StringUtil.getAssetPieChartHeader(resource);
            sb.append(headerScript);
            sb.append("[").append("\"年/月\",\"2013/06\"").append("],");
            sb.append("[").append("\"BitCash\",37").append("],");
            sb.append("[").append("\"WebMoney\",11").append("],");
            sb.append("[").append("\"NET CASH\",7").append("],");
            sb.append("[").append("\"チョコットランド\",7").append("],");
            sb.append("[").append("\"カヤック\",7").append("],");
            sb.append(StringUtil.getAssetChartFooter(resource));
        }

        return sb.toString();
    }

    public static class SaleRecordFragment extends Fragment {
        public static SaleRecordFragment newInstance(int commitFlag) {
            SaleRecordFragment f = new SaleRecordFragment();

            Bundle args = new Bundle();
            args.putInt("commitFlag", commitFlag);
            f.setArguments(args);

            return f;
        }

        public int getCommitFlag() {
            return getArguments().getInt("commitFlag", 0);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            if (container == null) {
                return null;
            }
            LinearLayout layout = null;
            if(mCurCheckPosition == 3){
                if (saleRecordItemList == null) {
                    if (errorMsg == null) {
                        layout = (LinearLayout) inflater.inflate(R.layout.salerecord_def_progress, null);
                    } else {
                        layout = (LinearLayout) inflater.inflate(R.layout.salerecord_def_error, null);
                        TextView em = (TextView) layout.findViewById(R.id.error_message);
                        em.setText(errorMsg);
                    }
                } else {
                    layout = (LinearLayout) inflater.inflate(R.layout.salerecord_def_date_search, null);
                    TextView dateSearch = (TextView)layout.findViewById(R.id.salerecord_date_search);
                    dateSearch.setText(getResources().getString(R.string.datepicker_text_ym));
                    dateTextView = (TextView)layout.findViewById(R.id.date_text);
                    dateTextView.setText(DateUtil.getDateTimeYYYYMM("yyyy/MM", apiDateStr));
                    Button openDatePicker = (Button)layout.findViewById(R.id.button_open_datepicker);
                    openDatePicker.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            YMOnlyDatePickerDialog df = YMOnlyDatePickerDialog.newInstance();
                            df.show(getFragmentManager(), "dialog");
                        }
                    });
                    TableLayout tableLayoutTitle = (TableLayout) layout.findViewById(R.id.salerecord_tablelayout_title);
                    tableLayoutTitle.setColumnStretchable(0, true);
                    tableLayoutTitle.setColumnStretchable(1, true);
                    tableLayoutTitle.setColumnStretchable(2, true);
                    makeTopTitle(tableLayoutTitle, inflater);
                    if (saleRecordItemList.size() > 0) {
                        TableLayout tableLayoutData = (TableLayout) layout.findViewById(R.id.salerecord_tablelayout_data);
                        tableLayoutData.setColumnStretchable(0, true);
                        tableLayoutData.setColumnStretchable(1, true);
                        tableLayoutData.setColumnStretchable(2, true);

                        makeSaleRecordSum(inflater, tableLayoutData);

                        TableRow tr = null;
                        for (int i = 0; i < saleRecordItemList.size(); i++) {
                            if (i % 2 == 0) {
                                tr = (TableRow) inflater.inflate(R.layout.salerecord_def_tablerow_data1, null);
                            } else {
                                tr = (TableRow) inflater.inflate(R.layout.salerecord_def_tablerow_data2, null);
                            }
                            SaleRecordItem record = saleRecordItemList.get(i);
                            Context context = getActivity();
                            makeSaleRecordData(inflater, tableLayoutData, tr, record, context);
                        }
                    } else {
                        makeNoData(tableLayoutTitle, inflater);
                    }
                }
                if (getCommitFlag() == 1) {
                    saleRecordItemList = null;
                }
            }else{
                if (saleRecordList == null) {
                    if (errorMsg == null) {
                        layout = (LinearLayout) inflater.inflate(R.layout.salerecord_def_progress, null);
                    } else {
                        layout = (LinearLayout) inflater.inflate(R.layout.salerecord_def_error, null);
                        TextView em = (TextView) layout.findViewById(R.id.error_message);
                        em.setText(errorMsg);
                    }
                } else {
                    if (mCurCheckPosition == 0) {
                        layout = (LinearLayout) inflater.inflate(R.layout.salerecord_def, null);
                    } else if (mCurCheckPosition == 1) {
                        layout = (LinearLayout) inflater.inflate(R.layout.salerecord_def_date_search, null);
                        TextView dateSearch = (TextView)layout.findViewById(R.id.salerecord_date_search);
                        dateSearch.setText(getResources().getString(R.string.datepicker_text_ym));
                        dateTextView = (TextView)layout.findViewById(R.id.date_text);
                        dateTextView.setText(DateUtil.getDateTimeYYYYMM("yyyy/MM", apiDateStr));
                        Button openDatePicker = (Button)layout.findViewById(R.id.button_open_datepicker);
                        openDatePicker.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                YMOnlyDatePickerDialog df = YMOnlyDatePickerDialog.newInstance();
                                df.show(getFragmentManager(), "dialog");
                            }
                        });
                    } else if (mCurCheckPosition == 2) {
                        layout = (LinearLayout) inflater.inflate(R.layout.salerecord_def_date_search, null);
                        TextView dateSearch = (TextView)layout.findViewById(R.id.salerecord_date_search);
                        dateSearch.setText(getResources().getString(R.string.datepicker_text_year));
                        dateTextView = (TextView)layout.findViewById(R.id.date_text);
                        dateTextView.setText(apiDateStr);
                        Button openDatePicker = (Button)layout.findViewById(R.id.button_open_datepicker);
                        openDatePicker.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                YMOnlyDatePickerDialog df = YMOnlyDatePickerDialog.newInstance();
                                df.show(getFragmentManager(), "dialog");
                            }
                        });
                    }
                    TableLayout tableLayoutTitle = (TableLayout) layout.findViewById(R.id.salerecord_tablelayout_title);
                    tableLayoutTitle.setColumnStretchable(0, true);
                    tableLayoutTitle.setColumnStretchable(1, true);
                    tableLayoutTitle.setColumnStretchable(2, true);
                    makeTopTitle(tableLayoutTitle, inflater);
                    if (saleRecordList.size() > 0) {
                        TableLayout tableLayoutData = (TableLayout) layout.findViewById(R.id.salerecord_tablelayout_data);
                        tableLayoutData.setColumnStretchable(0, true);
                        tableLayoutData.setColumnStretchable(1, true);
                        tableLayoutData.setColumnStretchable(2, true);

                        makeSaleRecordSum(inflater, tableLayoutData);

                        TableRow tr = null;
                        for (int i = 0; i < saleRecordList.size(); i++) {
                            if (i % 2 == 0) {
                                tr = (TableRow) inflater.inflate(R.layout.salerecord_def_tablerow_data1, null);
                            } else {
                                tr = (TableRow) inflater.inflate(R.layout.salerecord_def_tablerow_data2, null);
                            }
                            SaleRecord record = saleRecordList.get(i);
                            makeSaleRecordData(inflater, tableLayoutData, tr, record);
                        }
                    } else {
                        makeNoData(tableLayoutTitle, inflater);
                    }
                }
                if (getCommitFlag() == 1) {
                    saleRecordList = null;
                }
            }
            errorMsg = null;

            return layout;
        }

        private void makeTopTitle(TableLayout tableLayout, LayoutInflater inflater) {
            TableRow tr = (TableRow) inflater.inflate(R.layout.salerecord_def_tablerow_title, null);
            LinearLayout col1 = (LinearLayout) tr.findViewById(R.id.salerecord_tablerow_title1);
            TextView title1 = (TextView) inflater.inflate(R.layout.salerecord_title, null);
            if(mCurCheckPosition == 0 || mCurCheckPosition == 1){
                title1.setText(getResources().getString(R.string.salerecord_title1_ymd));
            } else if(mCurCheckPosition == 2) {
                title1.setText(getResources().getString(R.string.salerecord_title1_ym));
            } else if(mCurCheckPosition == 3) {
                title1.setText(getResources().getString(R.string.salerecord_title1_item));
            }
            col1.addView(title1);

            LinearLayout col2 = (LinearLayout) tr.findViewById(R.id.salerecord_tablerow_title2);
            TextView title2 = (TextView) inflater.inflate(R.layout.salerecord_title, null);
            title2.setText(getResources().getString(R.string.salerecord_title2));
            col2.addView(title2);

            LinearLayout col3 = (LinearLayout) tr.findViewById(R.id.salerecord_tablerow_title3);
            TextView title3 = (TextView) inflater.inflate(R.layout.salerecord_title, null);
            title3.setText(getResources().getString(R.string.salerecord_title3));
            col3.addView(title3);

            tableLayout.addView(tr, new TableLayout.LayoutParams(Constant.MP, Constant.WC));
        }

        private void makeNoData(TableLayout tableLayout, LayoutInflater inflater) {
            TableRow tr = (TableRow) inflater.inflate(R.layout.salerecord_def_tablerow_nodata, null);
            LinearLayout col1 = (LinearLayout) tr.findViewById(R.id.salerecord_tablerow_nodata1);
            TextView dummyTextView1 = (TextView) inflater.inflate(R.layout.salerecord_textnodata, null);
            dummyTextView1.setText("");
            col1.addView(dummyTextView1);

            LinearLayout col2 = (LinearLayout) tr.findViewById(R.id.salerecord_tablerow_nodata2);
            TextView dummyTextView2 = (TextView) inflater.inflate(R.layout.salerecord_textnodata, null);
            dummyTextView2.setText("");
            col2.addView(dummyTextView2);

            LinearLayout col3 = (LinearLayout) tr.findViewById(R.id.salerecord_tablerow_nodata3);
            TextView dummyTextView3 = (TextView) inflater.inflate(R.layout.salerecord_textnodata, null);
            dummyTextView3.setText("");
            col3.addView(dummyTextView3);

            tableLayout.addView(tr, new TableLayout.LayoutParams(Constant.MP, Constant.WC));
        }

        private void makeSaleRecordSum(LayoutInflater inflater, TableLayout tableLayout) {
            int cntSum = 0;
            int amtSum = 0;
            if(mCurCheckPosition == 3){
                for (SaleRecordItem record : saleRecordItemList) {
                    cntSum += record.getCnt();
                    amtSum += record.getAmt();
                }
            }else{
                for (SaleRecord record : saleRecordList) {
                    cntSum += record.getCnt();
                    amtSum += record.getAmt();
                }
            }

            TableRow tr = (TableRow) inflater.inflate(R.layout.salerecord_def_tablerow_sum, null);
            LinearLayout col1 = (LinearLayout) tr.findViewById(R.id.salerecord_tablerow_data1);
            TextView nmV = (TextView) inflater.inflate(R.layout.salerecord_textsum, null);
            nmV.setText(getResources().getString(R.string.salerecord_textsum));
            col1.addView(nmV);

            LinearLayout col2 = (LinearLayout) tr.findViewById(R.id.salerecord_tablerow_data2);
            TextView cntSumV = (TextView) inflater.inflate(R.layout.salerecord_textsum, null);
            cntSumV.setText(StringUtil.getCurrencyFormat(cntSum));
            col2.addView(cntSumV);

            LinearLayout col3 = (LinearLayout) tr.findViewById(R.id.salerecord_tablerow_data3);
            TextView amtSumV = (TextView) inflater.inflate(R.layout.salerecord_textsum, null);
            amtSumV.setText(getString(R.string.yen_mark) + StringUtil.getCurrencyFormat(amtSum));
            col3.addView(amtSumV);

            tableLayout.addView(tr, new TableLayout.LayoutParams(Constant.MP, Constant.WC));
        }

        private void makeSaleRecordData(LayoutInflater inflater, TableLayout tableLayout, TableRow tr, SaleRecord data) {
            LinearLayout col1 = (LinearLayout) tr.findViewById(R.id.salerecord_tablerow_data1);
            TextView ymdV = (TextView) inflater.inflate(R.layout.salerecord_textdata, null);
            if(mCurCheckPosition == 0 || mCurCheckPosition == 1){
                ymdV.setText(String.valueOf(DateUtil.getDateTimeYYYYMMDD("yyyy/MM/dd", data.getDate())));
            }else if (mCurCheckPosition == 2){
                ymdV.setText(String.valueOf(DateUtil.getDateTimeYYYYMM("yyyy/MM", data.getDate())));
            }
            col1.addView(ymdV);

            LinearLayout col2 = (LinearLayout) tr.findViewById(R.id.salerecord_tablerow_data2);
            TextView cntV = (TextView) inflater.inflate(R.layout.salerecord_textdata, null);
            cntV.setText(StringUtil.getCurrencyFormat(data.getCnt()));
            col2.addView(cntV);

            LinearLayout col3 = (LinearLayout) tr.findViewById(R.id.salerecord_tablerow_data3);
            TextView amtV = (TextView) inflater.inflate(R.layout.salerecord_textdata, null);
            amtV.setText(String.valueOf(StringUtil.getCurrencyFormat(data.getAmt())));
            col3.addView(amtV);

            tr.setTag(data);
            tr.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    SaleRecord data = (SaleRecord) v.getTag();
                    Context context = getActivity();
                    TabSaleRecord tsr = (TabSaleRecord) context;
                    if(mCurCheckPosition == 0 || mCurCheckPosition == 1){
                        tsr.new AsyncTaskForWebApi092(data.getDate()).execute();
                    }else if(mCurCheckPosition == 2){
                        tsr.setLeftmenuIndex(1);
                        tsr.new AsyncTaskForWebApi093().execute(data.getDate());
                    }
                }
            });

            tableLayout.addView(tr, new TableLayout.LayoutParams(Constant.MP, Constant.WC));
        }

        private void makeSaleRecordData(LayoutInflater inflater, TableLayout tableLayout, TableRow tr, SaleRecordItem data, Context context) {
            LinearLayout col1 = (LinearLayout) tr.findViewById(R.id.salerecord_tablerow_data1);
            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.daydetail_item, null);
            ImageView imageV = (ImageView) layout.findViewById(R.id.brand_image);
            TabSaleRecord tsr = (TabSaleRecord)context;
            Bitmap itembitmap = BitmapFactory.decodeFile(tsr.getFilesDir().getPath() + "/" + data.getBrandImg());
            imageV.setImageBitmap(itembitmap);
            TextView priceV = (TextView) layout.findViewById(R.id.emm_price);
            priceV.setText(tsr.getString(R.string.yen_mark) + StringUtil.getCurrencyFormat(data.getEmmPrice()));
            col1.addView(layout);

            LinearLayout col2 = (LinearLayout) tr.findViewById(R.id.salerecord_tablerow_data2);
            TextView cntV = (TextView) inflater.inflate(R.layout.salerecord_textdata, null);
            cntV.setText(StringUtil.getCurrencyFormat(data.getCnt()));
            col2.addView(cntV);

            LinearLayout col3 = (LinearLayout) tr.findViewById(R.id.salerecord_tablerow_data3);
            TextView amtV = (TextView) inflater.inflate(R.layout.salerecord_textdata, null);
            amtV.setText(String.valueOf(StringUtil.getCurrencyFormat(data.getAmt())));
            col3.addView(amtV);

            tr.setTag(data);
            tr.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    SaleRecordItem data = (SaleRecordItem) v.getTag();
                    Context context = getActivity();
                    TabSaleRecord tsr = (TabSaleRecord) context;
                    tsr.new AsyncTaskForWebApi096(data.getEmmId(), data.getBrandImg(), data.getEmmPrice(), apiDateStr).execute();
                }
            });

            tableLayout.addView(tr, new TableLayout.LayoutParams(Constant.MP, Constant.WC));
        }
    }

    public static class YMOnlyDatePickerDialog extends DialogFragment {
        public static YMOnlyDatePickerDialog newInstance() {
            YMOnlyDatePickerDialog frag = new YMOnlyDatePickerDialog();
            Bundle args = new Bundle();
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.datepicker, null);
            final DatePicker datePicker = (DatePicker) layout.findViewById(R.id.datePicker);
            Calendar cal = Calendar.getInstance();
            int minYear = Integer.valueOf(getResources().getString(R.string.datepicker_min_year));
            cal.set(minYear,0,1);
            datePicker.setMinDate(cal.getTime().getTime());
            if(mCurCheckPosition == 1 || mCurCheckPosition == 3){
                String ym[] = dateTextView.getText().toString().split("/");
                int y = Integer.valueOf(ym[0]);
                int m = Integer.valueOf(ym[1].replace("0", "")) - 1;
                datePicker.init(y,m,1,null);
                try {
                    Field f[] = datePicker.getClass().getDeclaredFields();
                    for (Field field : f) {
                        if ("mDaySpinner".equals(field.getName())) {
                            field.setAccessible(true);
                            Object dayPicker = new Object();
                            dayPicker = field.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }else if(mCurCheckPosition == 2){
                int y = Integer.valueOf(dateTextView.getText().toString());
                datePicker.init(y,0,1,null);
                try {
                    Field f[] = datePicker.getClass().getDeclaredFields();
                    for (Field field : f) {
                        if (field.getName().equals("mMonthSpinner") || "mDaySpinner".equals(field.getName())) {
                            field.setAccessible(true);
                            Object dayPicker = new Object();
                            dayPicker = field.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
            builder.setView(layout);
            final AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);

            Button closeButton = (Button)layout.findViewById(R.id.button_cancel);
            closeButton.setOnClickListener(new OnClickListener(){
                public void onClick(View v){
                    alertDialog.dismiss();
                }
            });

            Button searchB = (Button)layout.findViewById(R.id.button_search);
            searchB.setOnClickListener(new OnClickListener(){
                public void onClick(View v){
                    alertDialog.dismiss();
                    if(mCurCheckPosition == 1){
                        int year = datePicker.getYear();
                        int month = datePicker.getMonth() + 1;
                        String monthS = month < 10 ? "0"+month : month+"";
                        Context context = getActivity();
                        TabSaleRecord tsr = (TabSaleRecord)context;
                        tsr.new AsyncTaskForWebApi093().execute(year+""+monthS);
                    }else if(mCurCheckPosition == 2){
                        int year = datePicker.getYear();
                        Context context = getActivity();
                        TabSaleRecord tsr = (TabSaleRecord)context;
                        tsr.new AsyncTaskForWebApi094().execute(String.valueOf(year));
                    }else if(mCurCheckPosition == 3){
                        int year = datePicker.getYear();
                        int month = datePicker.getMonth() + 1;
                        String monthS = month < 10 ? "0"+month : month+"";
                        Context context = getActivity();
                        TabSaleRecord tsr = (TabSaleRecord)context;
                        tsr.new AsyncTaskForWebApi095().execute(year+""+monthS);
                    }
                }
            });

            return alertDialog;
        }
    }

    public static class DayDetailListDialog extends DialogFragment {
        public static DayDetailListDialog newInstance(String ymd) {
            DayDetailListDialog frag = new DayDetailListDialog();
            Bundle args = new Bundle();
            args.putString("ymd", ymd);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            String ymd = getArguments().getString("ymd");
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.salerecord_daydetail_complete_dialog, null);
            TextView titleV = (TextView) layout.findViewById(R.id.salerecord_daydetaillist_title);
            String title = getResources().getString(R.string.salerecord_daydetaillist_title);
            title = title.replace("#1#", DateUtil.getDateTimeYYYYMMDD("yyyy/MM/dd", ymd));
            titleV.setText(title);
            builder.setView(layout);
            final AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);

            TableLayout tableLayoutTitle = (TableLayout) layout.findViewById(R.id.daydetail_tablelayout_title);
            tableLayoutTitle.setColumnStretchable(0, true);
            tableLayoutTitle.setColumnStretchable(1, true);
            tableLayoutTitle.setColumnStretchable(2, true);
            tableLayoutTitle.setColumnStretchable(3, true);
            Context context = getActivity();
            makeDayDetailTopTitle(tableLayoutTitle, inflater, context);
            if (dayDetailList.size() > 0) {
                TableLayout tableLayoutData = (TableLayout) layout.findViewById(R.id.daydetail_tablelayout_data);
                tableLayoutData.setColumnStretchable(0, true);
                tableLayoutData.setColumnStretchable(1, true);
                tableLayoutData.setColumnStretchable(2, true);
                tableLayoutData.setColumnStretchable(3, true);

                TableRow tr = null;
                int listCnt = dayDetailList.size();
                for (int i = 0; i < dayDetailList.size(); i++) {
                    if (i % 2 == 0) {
                        tr = (TableRow) inflater.inflate(R.layout.salerecord_def_daydetail_tablerow_data1, null);
                    } else {
                        tr = (TableRow) inflater.inflate(R.layout.salerecord_def_daydetail_tablerow_data2, null);
                    }
                    SaleRecordDayDetail data = dayDetailList.get(i);
                    makeSaleRecordDayDetailData(inflater, tableLayoutData, tr, data, (listCnt - i), context);
                }
            }

            Button closeButton = (Button) layout.findViewById(R.id.button_dialog_close);
            closeButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (cdf != null) {
                        cdf.dismiss();
                    }
                    alertDialog.dismiss();
                }
            });

            return alertDialog;
        }
    }

    private static void makeDayDetailTopTitle(TableLayout tableLayout, LayoutInflater inflater, Context context) {
        TabSaleRecord tsr = (TabSaleRecord) context;
        TableRow tr = (TableRow) inflater.inflate(R.layout.salerecord_def_daydetail_tablerow_title, null);
        LinearLayout col1 = (LinearLayout) tr.findViewById(R.id.salerecord_daydetail_tablerow_title1);
        TextView title1 = (TextView) inflater.inflate(R.layout.salerecord_title, null);
        title1.setText(tsr.getString(R.string.salerecord_daydetail_title1));
        col1.addView(title1);

        LinearLayout col2 = (LinearLayout) tr.findViewById(R.id.salerecord_daydetail_tablerow_title2);
        TextView title2 = (TextView) inflater.inflate(R.layout.salerecord_title, null);
        title2.setText(tsr.getResources().getString(R.string.salerecord_daydetail_title2));
        col2.addView(title2);

        LinearLayout col3 = (LinearLayout) tr.findViewById(R.id.salerecord_daydetail_tablerow_title3);
        TextView title3 = (TextView) inflater.inflate(R.layout.salerecord_title, null);
        title3.setText(tsr.getResources().getString(R.string.salerecord_daydetail_title3));
        col3.addView(title3);

        LinearLayout col4 = (LinearLayout) tr.findViewById(R.id.salerecord_daydetail_tablerow_title4);
        TextView title4 = (TextView) inflater.inflate(R.layout.salerecord_title, null);
        title4.setText(tsr.getResources().getString(R.string.salerecord_daydetail_title4));
        col4.addView(title4);

        tableLayout.addView(tr, new TableLayout.LayoutParams(Constant.MP, Constant.WC));
    }

    private static void makeSaleRecordDayDetailData(LayoutInflater inflater, TableLayout tableLayout, TableRow tr,
            SaleRecordDayDetail data, int index, Context context) {
        TabSaleRecord tsr = (TabSaleRecord) context;
        LinearLayout col1 = (LinearLayout) tr.findViewById(R.id.salerecord_daydetail_tablerow_data1);
        TextView noV = (TextView) inflater.inflate(R.layout.salerecord_textdata, null);
        noV.setText(String.valueOf(index));
        col1.addView(noV);

        LinearLayout col2 = (LinearLayout) tr.findViewById(R.id.salerecord_daydetail_tablerow_data2);
        TextView orderTimeV = (TextView) inflater.inflate(R.layout.salerecord_textdata, null);
        orderTimeV.setText(data.getOrderTime());
        col2.addView(orderTimeV);

        LinearLayout col3 = (LinearLayout) tr.findViewById(R.id.salerecord_daydetail_tablerow_data3);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.daydetail_item, null);
        ImageView imageV = (ImageView) layout.findViewById(R.id.brand_image);
        Bitmap itembitmap = BitmapFactory.decodeFile(tsr.getFilesDir().getPath() + "/" + data.getBrandImg());
        imageV.setImageBitmap(itembitmap);
        TextView priceV = (TextView) layout.findViewById(R.id.emm_price);
        priceV.setText(tsr.getString(R.string.yen_mark) + StringUtil.getCurrencyFormat(data.getEmmPrice()));
        col3.addView(layout);

        LinearLayout col4 = (LinearLayout) tr.findViewById(R.id.salerecord_daydetail_tablerow_data4);
        TextView managenoV = (TextView) inflater.inflate(R.layout.salerecord_textdata, null);
        managenoV.setText(data.getManageno());
        col4.addView(managenoV);

        tableLayout.addView(tr, new TableLayout.LayoutParams(Constant.MP, Constant.WC));
    }

    private static void makeItemDetailTopTitle(TableLayout tableLayout, LayoutInflater inflater, Context context) {
        TabSaleRecord tsr = (TabSaleRecord) context;
        TableRow tr = (TableRow) inflater.inflate(R.layout.salerecord_def_itemdetail_tablerow_title, null);
        LinearLayout col1 = (LinearLayout) tr.findViewById(R.id.salerecord_itemdetail_tablerow_title1);
        TextView title1 = (TextView) inflater.inflate(R.layout.salerecord_title, null);
        title1.setText(tsr.getString(R.string.salerecord_itemdetail_title1));
        col1.addView(title1);

        LinearLayout col2 = (LinearLayout) tr.findViewById(R.id.salerecord_itemdetail_tablerow_title2);
        TextView title2 = (TextView) inflater.inflate(R.layout.salerecord_title, null);
        title2.setText(tsr.getResources().getString(R.string.salerecord_itemdetail_title2));
        col2.addView(title2);

        LinearLayout col3 = (LinearLayout) tr.findViewById(R.id.salerecord_itemdetail_tablerow_title3);
        TextView title3 = (TextView) inflater.inflate(R.layout.salerecord_title, null);
        title3.setText(tsr.getResources().getString(R.string.salerecord_itemdetail_title3));
        col3.addView(title3);

        tableLayout.addView(tr, new TableLayout.LayoutParams(Constant.MP, Constant.WC));
    }

    private static void makeSaleRecordItemDetailData(LayoutInflater inflater, TableLayout tableLayout, TableRow tr,
            SaleRecordItemDetail data, int index) {
        LinearLayout col1 = (LinearLayout) tr.findViewById(R.id.salerecord_itemdetail_tablerow_data1);
        TextView noV = (TextView) inflater.inflate(R.layout.salerecord_textdata, null);
        noV.setText(String.valueOf(index));
        col1.addView(noV);

        LinearLayout col2 = (LinearLayout) tr.findViewById(R.id.salerecord_itemdetail_tablerow_data2);
        TextView orderTimeV = (TextView) inflater.inflate(R.layout.salerecord_textdata, null);
        orderTimeV.setText(data.getOrderTime());
        col2.addView(orderTimeV);

        LinearLayout col3 = (LinearLayout) tr.findViewById(R.id.salerecord_itemdetail_tablerow_data3);
        TextView managenoV = (TextView) inflater.inflate(R.layout.salerecord_textdata, null);
        managenoV.setText(data.getManageno());
        col3.addView(managenoV);

        tableLayout.addView(tr, new TableLayout.LayoutParams(Constant.MP, Constant.WC));
    }

    public static class ItemDetailListDialog extends DialogFragment {
        public static ItemDetailListDialog newInstance(String brandImg, int emmPrice) {
            ItemDetailListDialog frag = new ItemDetailListDialog();
            Bundle args = new Bundle();
            args.putString("brandImg", brandImg);
            args.putInt("emmPrice", emmPrice);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.salerecord_itemdetail_complete_dialog, null);
            ImageView imageV = (ImageView) layout.findViewById(R.id.brand_image);
            Context context = getActivity();
            TabSaleRecord tsr = (TabSaleRecord)context;
            Bitmap itembitmap = BitmapFactory.decodeFile(tsr.getFilesDir().getPath() + "/" + getArguments().getString("brandImg"));
            imageV.setImageBitmap(itembitmap);
            TextView priceV = (TextView) layout.findViewById(R.id.emm_price);
            priceV.setText(tsr.getString(R.string.yen_mark) + StringUtil.getCurrencyFormat(getArguments().getInt("emmPrice")));
            builder.setView(layout);
            final AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);

            TableLayout tableLayoutTitle = (TableLayout) layout.findViewById(R.id.itemdetail_tablelayout_title);
            tableLayoutTitle.setColumnStretchable(0, true);
            tableLayoutTitle.setColumnStretchable(1, true);
            tableLayoutTitle.setColumnStretchable(2, true);
            makeItemDetailTopTitle(tableLayoutTitle, inflater, context);
            if (saleRecordItemDetailList.size() > 0) {
                TableLayout tableLayoutData = (TableLayout) layout.findViewById(R.id.itemdetail_tablelayout_data);
                tableLayoutData.setColumnStretchable(0, true);
                tableLayoutData.setColumnStretchable(1, true);
                tableLayoutData.setColumnStretchable(2, true);
                TableRow tr = null;
                int listCnt = saleRecordItemDetailList.size();
                for (int i = 0; i < saleRecordItemDetailList.size(); i++) {
                    if (i % 2 == 0) {
                        tr = (TableRow) inflater.inflate(R.layout.salerecord_def_itemdetail_tablerow_data1, null);
                    } else {
                        tr = (TableRow) inflater.inflate(R.layout.salerecord_def_itemdetail_tablerow_data2, null);
                    }
                    SaleRecordItemDetail data = saleRecordItemDetailList.get(i);
                    makeSaleRecordItemDetailData(inflater, tableLayoutData, tr, data, (listCnt - i));
                }
            }

            Button closeButton = (Button) layout.findViewById(R.id.button_dialog_close);
            closeButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (cdf != null) {
                        cdf.dismiss();
                    }
                    alertDialog.dismiss();
                }
            });

            return alertDialog;
        }
    }

    public void onTaskPreExecute() {
        SaleRecordFragment details = SaleRecordFragment.newInstance(0);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.salerecord_detail, details);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    public void onTaskCompleted() {
        SaleRecordFragment details = SaleRecordFragment.newInstance(1);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.salerecord_detail, details);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    public class AsyncTaskForWebApi091 extends AsyncTask<Void, Void, Void> {
        public AsyncTaskForWebApi091() {
        }

        @Override
        protected void onPreExecute() {
            onTaskPreExecute();
        }

        @Override
        protected Void doInBackground(Void... v) {
            long l1 = System.currentTimeMillis();
            HttpsClientConnector connector = new HttpsClientConnector(Constant.SERVICE_CODE_091);
            HttpGet request = connector.getRequest();
            DefaultHttpClient httpClient = connector.getHttpClient();
            try {
                HttpResponse response = httpClient.execute(request);
                HttpEntity entity = response.getEntity();
                Response091 res = Response091Gen.get(entity.getContent());
                saleRecordList = res.getSalerecordlist();
                if (res.getStatus() != 0) {
                    showDialog(getApplicationContext(), res.getMessage());
                }
            } catch (JsonFormatException j) {
                Log.e("JsonFormatException", ExceptionUtils.getStackTrace(j));
                errorMsg = getApplicationContext().getResources().getString(R.string.alert_dialog_error_protocol_contents);
            } catch (HttpHostConnectException h) {
                Log.e("HttpHostConnectException", ExceptionUtils.getStackTrace(h));
                errorMsg = getApplicationContext().getResources().getString(R.string.alert_dialog_error_network_contents);
            } catch (UnknownHostException u) {
                Log.e("UnknownHostException", ExceptionUtils.getStackTrace(u));
                errorMsg = getApplicationContext().getResources().getString(R.string.alert_dialog_error_network_contents);
            } catch (Exception e) {
                Log.e("Exception", ExceptionUtils.getStackTrace(e));
                errorMsg = getApplicationContext().getResources().getString(R.string.alert_dialog_error_system_contents);
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
            long l2 = System.currentTimeMillis();
            Log.d("AsyncTaskForWebApi091", "timelap:" + (l2 - l1));
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            onTaskCompleted();
        }
    }

    public class AsyncTaskForWebApi092 extends AsyncTask<Void, Void, Void> {
        private String ymd;
        private int apiResult;

        public AsyncTaskForWebApi092(String ymd) {
            cdf = CommonProgressDialog.newInstance(getResources().getString(
                    R.string.dialog_server_access_progress_title));
            this.ymd = ymd;
        }

        @Override
        protected void onPreExecute() {
            cdf.show(getFragmentManager(), "dialog");
        }

        @Override
        protected Void doInBackground(Void... v) {
            long l1 = System.currentTimeMillis();
            HttpsClientConnector connector = new HttpsClientConnector(Constant.SERVICE_CODE_092);
            connector.setParameter(Constant.YMD, this.ymd);
            HttpGet request = connector.getRequest();
            DefaultHttpClient httpClient = connector.getHttpClient();
            try {
                HttpResponse response = httpClient.execute(request);
                HttpEntity entity = response.getEntity();
                Response092 res = Response092Gen.get(entity.getContent());
                dayDetailList = res.getDaydetaillist();
                apiResult = res.getStatus();
                if (apiResult != 0) {
                    showDialog(TabSaleRecord.this, res.getMessage());
                }
            } catch (JsonFormatException j) {
                Log.e("JsonFormatException", ExceptionUtils.getStackTrace(j));
                errorMsg = getResources().getString(R.string.alert_dialog_error_protocol_contents);
            } catch (HttpHostConnectException h) {
                Log.e("HttpHostConnectException", ExceptionUtils.getStackTrace(h));
                errorMsg = getResources().getString(R.string.alert_dialog_error_network_contents);
            } catch (UnknownHostException u) {
                Log.e("UnknownHostException", ExceptionUtils.getStackTrace(u));
                errorMsg = getResources().getString(R.string.alert_dialog_error_network_contents);
            } catch (Exception e) {
                Log.e("Exception", ExceptionUtils.getStackTrace(e));
                errorMsg = getResources().getString(R.string.alert_dialog_error_system_contents);
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
            long l2 = System.currentTimeMillis();
            Log.d("AsyncTaskForWebApi092", "timelap:" + (l2 - l1));
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if (apiResult == 0) {
                DayDetailListDialog df = DayDetailListDialog.newInstance(this.ymd);
                df.show(getFragmentManager(), "dialog");
            }
        }
    }

    public class AsyncTaskForWebApi093 extends AsyncTask<String, Void, Void> {
        public AsyncTaskForWebApi093() {
        }

        @Override
        protected void onPreExecute() {
            onTaskPreExecute();
        }

        @Override
        protected Void doInBackground(String... s) {
            long l1 = System.currentTimeMillis();
            HttpsClientConnector connector = new HttpsClientConnector(Constant.SERVICE_CODE_093);
            connector.setParameter(Constant.YM, s[0]);
            HttpGet request = connector.getRequest();
            DefaultHttpClient httpClient = connector.getHttpClient();
            try {
                HttpResponse response = httpClient.execute(request);
                HttpEntity entity = response.getEntity();
                Response093 res = Response093Gen.get(entity.getContent());
                saleRecordList = res.getSalerecordlist();
                apiDateStr = res.getYm();
                if (res.getStatus() != 0) {
                    showDialog(getApplicationContext(), res.getMessage());
                }
            } catch (JsonFormatException j) {
                Log.e("JsonFormatException", ExceptionUtils.getStackTrace(j));
                errorMsg = getApplicationContext().getResources().getString(R.string.alert_dialog_error_protocol_contents);
            } catch (HttpHostConnectException h) {
                Log.e("HttpHostConnectException", ExceptionUtils.getStackTrace(h));
                errorMsg = getApplicationContext().getResources().getString(R.string.alert_dialog_error_network_contents);
            } catch (UnknownHostException u) {
                Log.e("UnknownHostException", ExceptionUtils.getStackTrace(u));
                errorMsg = getApplicationContext().getResources().getString(R.string.alert_dialog_error_network_contents);
            } catch (Exception e) {
                Log.e("Exception", ExceptionUtils.getStackTrace(e));
                errorMsg = getApplicationContext().getResources().getString(R.string.alert_dialog_error_system_contents);
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
            long l2 = System.currentTimeMillis();
            Log.d("AsyncTaskForWebApi093", "timelap:" + (l2 - l1));
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            onTaskCompleted();
        }
    }

    public class AsyncTaskForWebApi094 extends AsyncTask<String, Void, Void> {
        public AsyncTaskForWebApi094() {
        }

        @Override
        protected void onPreExecute() {
            onTaskPreExecute();
        }

        @Override
        protected Void doInBackground(String... s) {
            long l1 = System.currentTimeMillis();
            HttpsClientConnector connector = new HttpsClientConnector(Constant.SERVICE_CODE_094);
            connector.setParameter(Constant.YEAR, s[0]);
            HttpGet request = connector.getRequest();
            DefaultHttpClient httpClient = connector.getHttpClient();
            try {
                HttpResponse response = httpClient.execute(request);
                HttpEntity entity = response.getEntity();
                Response094 res = Response094Gen.get(entity.getContent());
                saleRecordList = res.getSalerecordlist();
                apiDateStr = res.getYear();
                if (res.getStatus() != 0) {
                    showDialog(getApplicationContext(), res.getMessage());
                }
            } catch (JsonFormatException j) {
                Log.e("JsonFormatException", ExceptionUtils.getStackTrace(j));
                errorMsg = getApplicationContext().getResources().getString(R.string.alert_dialog_error_protocol_contents);
            } catch (HttpHostConnectException h) {
                Log.e("HttpHostConnectException", ExceptionUtils.getStackTrace(h));
                errorMsg = getApplicationContext().getResources().getString(R.string.alert_dialog_error_network_contents);
            } catch (UnknownHostException u) {
                Log.e("UnknownHostException", ExceptionUtils.getStackTrace(u));
                errorMsg = getApplicationContext().getResources().getString(R.string.alert_dialog_error_network_contents);
            } catch (Exception e) {
                Log.e("Exception", ExceptionUtils.getStackTrace(e));
                errorMsg = getApplicationContext().getResources().getString(R.string.alert_dialog_error_system_contents);
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
            long l2 = System.currentTimeMillis();
            Log.d("AsyncTaskForWebApi094", "timelap:" + (l2 - l1));
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            onTaskCompleted();
        }
    }

    public class AsyncTaskForWebApi095 extends AsyncTask<String, Void, Void> {
        public AsyncTaskForWebApi095() {
        }

        @Override
        protected void onPreExecute() {
            onTaskPreExecute();
        }

        @Override
        protected Void doInBackground(String... s) {
            long l1 = System.currentTimeMillis();
            HttpsClientConnector connector = new HttpsClientConnector(Constant.SERVICE_CODE_095);
            connector.setParameter(Constant.YM, s[0]);
            HttpGet request = connector.getRequest();
            DefaultHttpClient httpClient = connector.getHttpClient();
            try {
                HttpResponse response = httpClient.execute(request);
                HttpEntity entity = response.getEntity();
                Response095 res = Response095Gen.get(entity.getContent());
                saleRecordItemList = res.getSalerecordlist();
                apiDateStr = res.getYm();
                if (res.getStatus() != 0) {
                    showDialog(getApplicationContext(), res.getMessage());
                }
            } catch (JsonFormatException j) {
                Log.e("JsonFormatException", ExceptionUtils.getStackTrace(j));
                errorMsg = getApplicationContext().getResources().getString(R.string.alert_dialog_error_protocol_contents);
            } catch (HttpHostConnectException h) {
                Log.e("HttpHostConnectException", ExceptionUtils.getStackTrace(h));
                errorMsg = getApplicationContext().getResources().getString(R.string.alert_dialog_error_network_contents);
            } catch (UnknownHostException u) {
                Log.e("UnknownHostException", ExceptionUtils.getStackTrace(u));
                errorMsg = getApplicationContext().getResources().getString(R.string.alert_dialog_error_network_contents);
            } catch (Exception e) {
                Log.e("Exception", ExceptionUtils.getStackTrace(e));
                errorMsg = getApplicationContext().getResources().getString(R.string.alert_dialog_error_system_contents);
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
            long l2 = System.currentTimeMillis();
            Log.d("AsyncTaskForWebApi095", "timelap:" + (l2 - l1));
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            onTaskCompleted();
        }
    }

    public class AsyncTaskForWebApi096 extends AsyncTask<Void, Void, Void> {
        private int emmId;
        private String brandImg;
        private int emmPrice;
        private String ym;
        private int apiResult;

        public AsyncTaskForWebApi096(int emmId, String brandImg, int emmPrice, String ym) {
            cdf = CommonProgressDialog.newInstance(getResources().getString(
                    R.string.dialog_server_access_progress_title));
            this.emmId = emmId;
            this.brandImg = brandImg;
            this.emmPrice = emmPrice;
            this.ym = ym;
        }

        @Override
        protected void onPreExecute() {
            cdf.show(getFragmentManager(), "dialog");
        }

        @Override
        protected Void doInBackground(Void... v) {
            long l1 = System.currentTimeMillis();
            HttpsClientConnector connector = new HttpsClientConnector(Constant.SERVICE_CODE_096);
            connector.setParameter(Constant.EMMID, String.valueOf(this.emmId));
            connector.setParameter(Constant.YM, this.ym);
            HttpGet request = connector.getRequest();
            DefaultHttpClient httpClient = connector.getHttpClient();
            try {
                HttpResponse response = httpClient.execute(request);
                HttpEntity entity = response.getEntity();
                Response096 res = Response096Gen.get(entity.getContent());
                saleRecordItemDetailList = res.getSalerecordlist();
                apiResult = res.getStatus();
                if (apiResult != 0) {
                    showDialog(TabSaleRecord.this, res.getMessage());
                }
            } catch (JsonFormatException j) {
                Log.e("JsonFormatException", ExceptionUtils.getStackTrace(j));
                errorMsg = getResources().getString(R.string.alert_dialog_error_protocol_contents);
            } catch (HttpHostConnectException h) {
                Log.e("HttpHostConnectException", ExceptionUtils.getStackTrace(h));
                errorMsg = getResources().getString(R.string.alert_dialog_error_network_contents);
            } catch (UnknownHostException u) {
                Log.e("UnknownHostException", ExceptionUtils.getStackTrace(u));
                errorMsg = getResources().getString(R.string.alert_dialog_error_network_contents);
            } catch (Exception e) {
                Log.e("Exception", ExceptionUtils.getStackTrace(e));
                errorMsg = getResources().getString(R.string.alert_dialog_error_system_contents);
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
            long l2 = System.currentTimeMillis();
            Log.d("AsyncTaskForWebApi096", "timelap:" + (l2 - l1));
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if (apiResult == 0) {
                ItemDetailListDialog df = ItemDetailListDialog.newInstance(this.brandImg, this.emmPrice);
                df.show(getFragmentManager(), "dialog");
            }
        }
    }
}