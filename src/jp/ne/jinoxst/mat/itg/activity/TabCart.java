package jp.ne.jinoxst.mat.itg.activity;

import java.net.UnknownHostException;

import jp.ne.jinoxst.mat.itg.R;
import jp.ne.jinoxst.mat.itg.activity.adapter.TicketListAdapter;
import jp.ne.jinoxst.mat.itg.activity.dialog.CommonProgressDialog;
import jp.ne.jinoxst.mat.itg.activity.dialog.ShowTicketDialog;
import jp.ne.jinoxst.mat.itg.activity.dialog.SimpleDialogFragment;
import jp.ne.jinoxst.mat.itg.pojo.Ticket;
import jp.ne.jinoxst.mat.itg.pojo.json.Response031;
import jp.ne.jinoxst.mat.itg.pojo.json.Response031Gen;
import jp.ne.jinoxst.mat.itg.service.CartCircleService;
import jp.ne.jinoxst.mat.itg.util.Cart;
import jp.ne.jinoxst.mat.itg.util.Cart.OrderedItem;
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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class TabCart extends BaseActivity {
    private final static String TAG = "TabCart";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalRegistry registry = GlobalRegistry.getInstance();
        registry.setRegistry(Constant.SELECTED_TAB_INDEX, Constant.SELECTED_TAB_INDEX_1);

        initialize();
    }

    private void initialize(){
        setContentView(R.layout.cartlist_def);

        TableLayout tableLayoutTitle = (TableLayout)findViewById(R.id.cartlist_tablelayout_title);
        tableLayoutTitle.setColumnStretchable(0, true);
        tableLayoutTitle.setColumnStretchable(1, true);
        tableLayoutTitle.setColumnStretchable(2, true);

        TableLayout tableLayoutData = (TableLayout)findViewById(R.id.cartlist_tablelayout_data);
        tableLayoutData.setColumnStretchable(0, true);
        tableLayoutData.setColumnStretchable(1, true);
        tableLayoutData.setColumnStretchable(2, true);

        makeTopTitle(tableLayoutTitle);
        Cart cart = Cart.getInstance();
        if(cart.getTotalCount() == 0){
            makeNoData(tableLayoutData);
        }else{
            int idx = 0;
            for(OrderedItem item : cart.getOrderedItem()){
                TableRow tr = null;
                if(idx % 2 == 0){
                    tr = (TableRow)getLayoutInflater().inflate(R.layout.cartlist_def_tablerow_data1, null);
                }else{
                    tr = (TableRow)getLayoutInflater().inflate(R.layout.cartlist_def_tablerow_data2, null);
                }
                makeCartListData(tableLayoutData, tr, item);
                idx++;
            }

            TableLayout tableLayoutSum = (TableLayout)findViewById(R.id.cartlist_tablelayout_sum);
            tableLayoutSum.setColumnStretchable(0, true);
            tableLayoutSum.setColumnStretchable(1, true);
            tableLayoutSum.setColumnStretchable(2, true);
            makeCartListSum(tableLayoutSum);
            makeCartListIssue();
        }
    }

    private void makeTopTitle(TableLayout tableLayout){
        TableRow tr = (TableRow)getLayoutInflater().inflate(R.layout.cartlist_def_tablerow_title, null);
        LinearLayout col1 = (LinearLayout)tr.findViewById(R.id.cartlist_tablerow_title1);
        TextView nmV = (TextView)getLayoutInflater().inflate(R.layout.cartlist_title, null);
        nmV.setText(getResources().getString(R.string.cartlist_toptitle_nm));
        col1.addView(nmV);

        LinearLayout col2 = (LinearLayout)tr.findViewById(R.id.cartlist_tablerow_title2);
        TextView countV = (TextView)getLayoutInflater().inflate(R.layout.cartlist_title, null);
        countV.setText(getResources().getString(R.string.cartlist_toptitle_count));
        col2.addView(countV);

        LinearLayout col3 = (LinearLayout)tr.findViewById(R.id.cartlist_tablerow_title3);
        TextView priceV = (TextView)getLayoutInflater().inflate(R.layout.cartlist_title, null);
        priceV.setText(getResources().getString(R.string.cartlist_toptitle_price));
        col3.addView(priceV);

        tableLayout.addView(tr, new TableLayout.LayoutParams(Constant.MP,Constant.WC));
    }

    private void makeNoData(TableLayout tableLayout){
        TableRow tr = (TableRow)getLayoutInflater().inflate(R.layout.cartlist_def_tablerow_nodata, null);
        LinearLayout col1 = (LinearLayout)tr.findViewById(R.id.cartlist_tablerow_nodata1);
        TextView dummyTextView1 = (TextView)getLayoutInflater().inflate(R.layout.cartlist_textdata, null);
        dummyTextView1.setText("");
        col1.addView(dummyTextView1);

        LinearLayout col2 = (LinearLayout)tr.findViewById(R.id.cartlist_tablerow_nodata2);
        TextView dummyTextView2 = (TextView)getLayoutInflater().inflate(R.layout.cartlist_textdata, null);
        dummyTextView2.setText("");
        col2.addView(dummyTextView2);

        LinearLayout col3 = (LinearLayout)tr.findViewById(R.id.cartlist_tablerow_nodata3);
        TextView dummyTextView3 = (TextView)getLayoutInflater().inflate(R.layout.cartlist_textdata, null);
        dummyTextView3.setText("");
        col3.addView(dummyTextView3);

        tableLayout.addView(tr, new TableLayout.LayoutParams(Constant.MP,Constant.WC));
    }

    private void makeCartListData(TableLayout tableLayout, TableRow tr, OrderedItem item){
        LinearLayout col1 = (LinearLayout)tr.findViewById(R.id.cartlist_tablerow_data1);
        LinearLayout layout = (LinearLayout)getLayoutInflater().inflate(R.layout.cartlist_item, null);
        ImageView imageV = (ImageView)layout.findViewById(R.id.item_image);
        Bitmap itembitmap = BitmapFactory.decodeFile(this.getFilesDir().getPath()+"/"+item.getBrandImg());
        imageV.setImageBitmap(itembitmap);
        TextView unitPriceV = (TextView)layout.findViewById(R.id.emm_price);
        unitPriceV.setText(getString(R.string.yen_mark)+StringUtil.getCurrencyFormat(String.valueOf(item.getUnitPrice())));
        col1.addView(layout);

        LinearLayout col2 = (LinearLayout)tr.findViewById(R.id.cartlist_tablerow_data2);
        LinearLayout countLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.cartlist_countview, null);
        TextView orderedCountTextView = (TextView)countLayout.findViewById(R.id.cart_ordered_count_text);
        orderedCountTextView.setText(String.valueOf(item.getOrderedCnt()));
        Button cartPlusButton = (Button)countLayout.findViewById(R.id.button_cartlist_plus);
        cartPlusButton.setTag(item);
        cartPlusButton.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                OrderedItem item = (OrderedItem)v.getTag();
                Cart cart = Cart.getInstance();
                if(cart.getTotalCount() == Constant.CART_MAX_COUNT){
                    String msg = getResources().getString(R.string.cart_max_count).replace("#1#", String.valueOf(Constant.CART_MAX_COUNT));
                    DialogFragment df = SimpleDialogFragment.newInstance(msg);
                    df.show(TabCart.this.getFragmentManager(), "dialog");
                }else{
                    cart.plusItemIntoCart(item.getEmmId());

                    repaintCartCircle();
                    initialize();
                }
            }
        });

        Button cartMinusButton = (Button)countLayout.findViewById(R.id.button_cartlist_minus);
        cartMinusButton.setTag(item);
        cartMinusButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                OrderedItem item = (OrderedItem)v.getTag();
                Cart cart = Cart.getInstance();
                cart.minusItemFromCart(item.getEmmId());

                repaintCartCircle();
                initialize();
            }
        });
        col2.addView(countLayout);

        LinearLayout col3 = (LinearLayout)tr.findViewById(R.id.cartlist_tablerow_data3);
        LinearLayout priceLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.cartlist_priceview, null);
        TextView cartUnitSumPrice = (TextView)priceLayout.findViewById(R.id.cart_unit_sum_price);
        int unitSumPrice = item.getOrderedCnt() * item.getUnitPrice();
        cartUnitSumPrice.setText(getString(R.string.yen_mark)+StringUtil.getCurrencyFormat(String.valueOf(unitSumPrice)));
        Button cartDeleteButton = (Button)priceLayout.findViewById(R.id.button_cartlist_delete);
        cartDeleteButton.setTag(item);
        cartDeleteButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                OrderedItem item = (OrderedItem)v.getTag();
                Cart cart = Cart.getInstance();
                cart.deleteItemFromCart(item.getEmmId());

                repaintCartCircle();
                initialize();
            }
        });
        col3.addView(priceLayout);

        tableLayout.addView(tr, new TableLayout.LayoutParams(Constant.MP,Constant.WC));
    }

    private void repaintCartCircle(){
        Intent intent = new Intent(getApplicationContext(), CartCircleService.class);
        Cart cart = Cart.getInstance();
        int cartCount = cart.getTotalCount();
        intent.putExtra(Constant.CART_TOTAL_COUNT, cartCount);
        getApplicationContext().startService(intent);
    }

    private void makeCartListSum(TableLayout tableLayout){
        TableRow tr = (TableRow)getLayoutInflater().inflate(R.layout.cartlist_def_tablerow_sum, null);
        LinearLayout col1 = (LinearLayout)tr.findViewById(R.id.cartlist_tablerow_data1);
        TextView nmV = (TextView)getLayoutInflater().inflate(R.layout.cartlist_textsum, null);
        nmV.setText(getResources().getString(R.string.cartlist_textsum));
        col1.addView(nmV);

        LinearLayout col2 = (LinearLayout)tr.findViewById(R.id.cartlist_tablerow_data2);
        TextView countV = (TextView)getLayoutInflater().inflate(R.layout.cartlist_textsum, null);
        Cart cart = Cart.getInstance();
        countV.setText(String.valueOf(cart.getTotalCount()));
        col2.addView(countV);

        LinearLayout col3 = (LinearLayout)tr.findViewById(R.id.cartlist_tablerow_data3);
        TextView priceV = (TextView)getLayoutInflater().inflate(R.layout.cartlist_textsum, null);
        priceV.setText(getString(R.string.yen_mark)+StringUtil.getCurrencyFormat(String.valueOf(cart.getTotalPrice())));
        col3.addView(priceV);

        tableLayout.addView(tr, new TableLayout.LayoutParams(Constant.MP,Constant.WC));
    }

    private void makeCartListIssue(){
        LinearLayout layout = (LinearLayout)findViewById(R.id.cartlist_issue_layout);
        LinearLayout buttonLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.cartlist_button_issue, null);
        Button cartDeleteAllButton = (Button)buttonLayout.findViewById(R.id.button_cartlist_delete_all);
        cartDeleteAllButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                Cart cart = Cart.getInstance();
                cart.clearCartList();

                repaintCartCircle();
                initialize();
            }
        });

        Button issueButton = (Button)buttonLayout.findViewById(R.id.button_cartlist_issue);
        issueButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                CartIssueConfirmDialog df = CartIssueConfirmDialog.newInstance();
                df.show(TabCart.this.getFragmentManager(), "dialog");
            }
        });
        layout.addView(buttonLayout);
    }

    public class AsyncTaskForWebApi031 extends AsyncTask<Void, Void, Void> {
        private DialogFragment orderingD;
        private Context context;
        private int apiResult;

        public AsyncTaskForWebApi031(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            orderingD = CommonProgressDialog.newInstance(getResources().getString(R.string.dialog_print_find_progress_ordering_title));
            orderingD.show(((Activity)context).getFragmentManager(), "dialog");
        }

        @Override
        protected Void doInBackground(Void... v) {
            long l1 = System.currentTimeMillis();
            HttpsClientConnector connector = new HttpsClientConnector(Constant.SERVICE_CODE_031);
            Cart cart = Cart.getInstance();
            connector.setParameter(Constant.ORDER_MATRIX, cart.getOrderMatrix());
            HttpGet request = connector.getRequest();
            DefaultHttpClient httpClient = connector.getHttpClient();
            try {
                HttpResponse response = httpClient.execute(request);
                HttpEntity entity = response.getEntity();
                Response031 res = Response031Gen.get(entity.getContent());
                ticketList = res.getTicketlist();
                apiResult = res.getStatus();
                if(apiResult != 0){
                    showDialog(context,res.getMessage());
                }
            } catch (JsonFormatException j) {
                Log.e("JsonFormatException", ExceptionUtils.getStackTrace(j));
                showDialog(context,context.getResources().getString(R.string.alert_dialog_error_protocol_contents));
            } catch (HttpHostConnectException h) {
                Log.e("HttpHostConnectException", ExceptionUtils.getStackTrace(h));
                showDialog(context,context.getResources().getString(R.string.alert_dialog_error_network_contents));
            } catch (UnknownHostException u) {
                Log.e("UnknownHostException", ExceptionUtils.getStackTrace(u));
                showDialog(context,context.getResources().getString(R.string.alert_dialog_error_network_contents));
            } catch (Exception e) {
                Log.e("Exception", ExceptionUtils.getStackTrace(e));
                showDialog(context,context.getResources().getString(R.string.alert_dialog_error_system_contents));
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
            long l2 = System.currentTimeMillis();
            Log.d("AsyncTaskForWebApi031", "timelap:" + (l2 - l1));
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if(orderingD != null){
                orderingD.dismiss();
            }
            if(apiResult == 0){
                ticketListAdapter = new TicketListAdapter(((Activity)context),R.layout.ticket_unit,ticketList);
                CartIssueCompleteDialog df = CartIssueCompleteDialog.newInstance();
                df.show(((Activity)context).getFragmentManager(), "dialog");
            }
        }
    }

    private void showDialog(Context context, String msg) {
        DialogFragment df = SimpleDialogFragment.newInstance(msg);
        df.show(((Activity)context).getFragmentManager(), "dialog");
    }

    public static class CartIssueConfirmDialog extends DialogFragment {
        public static CartIssueConfirmDialog newInstance() {
            CartIssueConfirmDialog frag = new CartIssueConfirmDialog();
            Bundle args = new Bundle();
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.cart_issue_confirm_dialog, null);
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
                    TabCart tabCart = (TabCart)context;
                    tabCart.onFindPrintProgress(df);
                }
            });

            Button issueButton = (Button)layout.findViewById(R.id.button_dialog_issue);
            issueButton.setOnClickListener(new OnClickListener(){
                public void onClick(View v){
                    alertDialog.dismiss();
                    Context context = getActivity();
                    TabCart tabCart = (TabCart)context;

                    tabCart.new AsyncTaskForWebApi031(tabCart).execute();
                }
            });

            return alertDialog;
        }
    }

    public static class CartIssueCompleteDialog extends DialogFragment {
        public static CartIssueCompleteDialog newInstance() {
            CartIssueCompleteDialog frag = new CartIssueCompleteDialog();
            Bundle args = new Bundle();
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.cart_issue_complete_dialog, null);
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
                TabCart tabCart = (TabCart)context;
                public void onClick(View v){
                    printButtonClicked = true;
                    if(selectedPrintIPAddress != null){
                        tabCart.new AsyncTaskForPrint(tabCart).execute();
                    }else{
                        CommonProgressDialog df = CommonProgressDialog.newInstance(getResources().getString(R.string.dialog_print_find_progress_title));
                        df.show(getActivity().getFragmentManager(), "dialog");
                        tabCart.onFindPrintProgress(df);
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
                    CartIssueCloseConfirmDialog df = CartIssueCloseConfirmDialog.newInstance(alertDialog);
                    df.show(getActivity().getFragmentManager(), "dialog");
                }
            });

            return alertDialog;
        }
    }

    public static class CartIssueCloseConfirmDialog extends DialogFragment {
        static AlertDialog parentD;
        public static CartIssueCloseConfirmDialog newInstance(AlertDialog d) {
            parentD = d;
            CartIssueCloseConfirmDialog frag = new CartIssueCloseConfirmDialog();
            Bundle args = new Bundle();
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.cart_issue_close_confirm_dialog, null);
            builder.setView(layout);
            final AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);

            Button closeButton = (Button)layout.findViewById(R.id.button_dailog_close_ok);
            closeButton.setOnClickListener(new OnClickListener(){
                public void onClick(View v){
                    alertDialog.dismiss();
                    parentD.dismiss();

                    Context context = getActivity();
                    TabCart tabCart = (TabCart)context;

                    Cart cart = Cart.getInstance();
                    cart.clearCartList();
                    tabCart.repaintCartCircle();
                    tabCart.initialize();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}