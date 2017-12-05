package jp.ne.jinoxst.mat.itg.activity;

import java.net.UnknownHostException;
import java.util.List;

import jp.ne.jinoxst.mat.itg.R;
import jp.ne.jinoxst.mat.itg.activity.adapter.LeftMenuListAdapter;
import jp.ne.jinoxst.mat.itg.activity.adapter.SectionedAdapter;
import jp.ne.jinoxst.mat.itg.activity.callback.IOrderLeftMenuList;
import jp.ne.jinoxst.mat.itg.activity.dialog.SimpleDialogFragment;
import jp.ne.jinoxst.mat.itg.pojo.Item;
import jp.ne.jinoxst.mat.itg.pojo.OrderLeftMenu;
import jp.ne.jinoxst.mat.itg.pojo.json.Response022;
import jp.ne.jinoxst.mat.itg.pojo.json.Response022Gen;
import jp.ne.jinoxst.mat.itg.service.CartCircleService;
import jp.ne.jinoxst.mat.itg.util.Cart;
import jp.ne.jinoxst.mat.itg.util.Constant;
import jp.ne.jinoxst.mat.itg.util.GlobalRegistry;
import jp.ne.jinoxst.mat.itg.util.StringUtil;
import jp.ne.jinoxst.mat.itg.util.db.DBAdapter;
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
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class OrderLeftMenuList extends BaseActivity {
    private final static String TAG = "OrderLeftMenuList";
    private static List<Item> itemList;
    private static int leftmenuIndex;
    private static int itemEmmSeq;
    private static String errorMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_leftmenulist);
        GlobalRegistry registry = GlobalRegistry.getInstance();
        registry.setRegistry(Constant.SELECTED_TAB_INDEX, Constant.SELECTED_TAB_INDEX_0);
    }

    public static class ItemList extends Activity implements IOrderLeftMenuList {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                finish();
                return;
            }

            if (savedInstanceState == null) {
                int emmSeq = getIntent().getExtras().getInt("emmSeq");
                int index = getIntent().getExtras().getInt("index");
                OrderLeftMenuList om = new OrderLeftMenuList();
                om.new AsyncTaskForWebApi022(getApplicationContext(), this, index, emmSeq).execute();
            }
        }

        @Override
        public void onTaskCompleted(){
            GlobalRegistry registry = GlobalRegistry.getInstance();
            if(registry.getInt(Constant.SELECTED_TAB_INDEX) == Constant.SELECTED_TAB_INDEX_0){
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ItemListFragment details = ItemListFragment.newInstance(leftmenuIndex,1);
                ft.replace(android.R.id.content, details);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        }

        @Override
        public void onTaskPreExecute(int index){}
    }

    private void showDialog(Context context, String msg) {
        DialogFragment df = SimpleDialogFragment.newInstance(msg);
        df.show(((Activity)context).getFragmentManager(), "dialog");
    }

    public class AsyncTaskForWebApi022 extends AsyncTask<Void, Void, Void> {
        private IOrderLeftMenuList asynchComplete;
        private int emmSeq;
        private Context context;

        public AsyncTaskForWebApi022(Context context, IOrderLeftMenuList asynchComplete, int index, int emmSeq){
            this.asynchComplete = asynchComplete;
            this.emmSeq = emmSeq;
            leftmenuIndex = index;
            this.context = context;
            itemEmmSeq = emmSeq;
        }

        @Override
        protected void onPreExecute() {
            this.asynchComplete.onTaskPreExecute(leftmenuIndex);
        }

        @Override
        protected Void doInBackground(Void... v) {
            long l1 = System.currentTimeMillis();
            HttpsClientConnector connector = new HttpsClientConnector(Constant.SERVICE_CODE_022);
            connector.setParameter(Constant.EMMSEQ,String.valueOf(emmSeq));
            HttpGet request = connector.getRequest();
            DefaultHttpClient httpClient = connector.getHttpClient();
            try {
                HttpResponse response = httpClient.execute(request);
                HttpEntity entity = response.getEntity();
                Response022 res = Response022Gen.get(entity.getContent());
                itemList = res.getItemlist();
                if(res.getStatus() != 0){
                    showDialog(context,res.getMessage());
                }
            } catch (JsonFormatException j) {
                Log.e("JsonFormatException", ExceptionUtils.getStackTrace(j));
                showDialog(context,context.getResources().getString(R.string.alert_dialog_error_protocol_contents));
                errorMsg = context.getResources().getString(R.string.alert_dialog_error_protocol_contents);
            } catch (HttpHostConnectException h) {
                Log.e("HttpHostConnectException", ExceptionUtils.getStackTrace(h));
                showDialog(context,context.getResources().getString(R.string.alert_dialog_error_network_contents));
                errorMsg = context.getResources().getString(R.string.alert_dialog_error_network_contents);
            } catch (UnknownHostException u) {
                Log.e("UnknownHostException", ExceptionUtils.getStackTrace(u));
                showDialog(context,context.getResources().getString(R.string.alert_dialog_error_network_contents));
                errorMsg = context.getResources().getString(R.string.alert_dialog_error_network_contents);
            } catch (Exception e) {
                Log.e("Exception", ExceptionUtils.getStackTrace(e));
                showDialog(context,context.getResources().getString(R.string.alert_dialog_error_system_contents));
                errorMsg = context.getResources().getString(R.string.alert_dialog_error_system_contents);
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
            long l2 = System.currentTimeMillis();
            Log.d("AsyncTaskForWebApi022", "timelap:" + (l2 - l1));
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            this.asynchComplete.onTaskCompleted();
        }
    }

    public static class LeftMenuListManager extends ListFragment implements IOrderLeftMenuList {
        boolean mDualPane;
        int mCurCheckPosition = 0;

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            GlobalRegistry registry = GlobalRegistry.getInstance();
            if(registry.getInt(Constant.SELECTED_TAB_INDEX) == Constant.SELECTED_TAB_INDEX_0){
                DBAdapter dbAdapter = new DBAdapter(getActivity());
                dbAdapter.open();
                List<OrderLeftMenu> leftmenuPrepaid = dbAdapter.getLeftMenuList(Constant.CATEGORY_TYPE_PREPAID);
                List<OrderLeftMenu> leftmenuGame = dbAdapter.getLeftMenuList(Constant.CATEGORY_TYPE_GAME);
                dbAdapter.close();

                adapter.addSection(getString(R.string.leftmenu_header_prepaid), new LeftMenuListAdapter(getActivity(),
                        R.layout.leftmenu_unit, leftmenuPrepaid));
                adapter.addSection(getString(R.string.leftmenu_header_game), new LeftMenuListAdapter(getActivity(),
                        R.layout.leftmenu_unit, leftmenuGame));

                setListAdapter(adapter);

                View detailsFrame = getActivity().findViewById(R.id.itemlist_detail);
                mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

                if (savedInstanceState != null) {
                    mCurCheckPosition = savedInstanceState.getInt("curChoice", 1);
                } else {
                    mCurCheckPosition = 1;
                }

                if (mDualPane) {
                    if(mCurCheckPosition != 0){
                        Object object = adapter.getItem(mCurCheckPosition);
                        if(object instanceof OrderLeftMenu){
                            OrderLeftMenu menu = (OrderLeftMenu)adapter.getItem(mCurCheckPosition);
                            showItemList(mCurCheckPosition, menu.getEmmSeq());
                        }
                    }
                }
                getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                getListView().setItemChecked(1, true);
            }
        }

        SectionedAdapter adapter = new SectionedAdapter() {
            protected View getHeaderView(String caption, int index, View convertView, ViewGroup parent) {
                TextView result = (TextView) convertView;

                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                    result = (TextView) inflater.inflate(R.layout.leftmenu_header, null);
                }
                result.setText(caption);

                return result;
            }
        };

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt("curChoice", mCurCheckPosition);
        }

        @Override
        public void onListItemClick(ListView lv, View v, int position, long id) {
            OrderLeftMenu menu = (OrderLeftMenu)adapter.getItem(position);
            showItemList(position, menu.getEmmSeq());
        }

        void showItemList(int index, int emmSeq) {
            mCurCheckPosition = index;

            if (mDualPane) {
                OrderLeftMenuList om = new OrderLeftMenuList();
                om.new AsyncTaskForWebApi022(getActivity(), this, index, emmSeq).execute();
            } else {
                Intent intent = new Intent();
                intent.setClass(getActivity(), ItemList.class);
                intent.putExtra("index", index);
                intent.putExtra("emmSeq", emmSeq);
                startActivity(intent);
            }
        }

        @Override
        public void onTaskPreExecute(int index){
            ItemListFragment details = ItemListFragment.newInstance(index,0);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.itemlist_detail, details);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }

        @Override
        public void onTaskCompleted(){
            GlobalRegistry registry = GlobalRegistry.getInstance();
            if(registry.getInt(Constant.SELECTED_TAB_INDEX) == Constant.SELECTED_TAB_INDEX_0){
                if (mDualPane) {
                    ItemListFragment details = ItemListFragment.newInstance(leftmenuIndex,1);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.itemlist_detail, details);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.commit();
                } else {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), ItemList.class);
                    intent.putExtra("index", leftmenuIndex);
                    intent.putExtra("emmSeq", itemEmmSeq);
                    startActivity(intent);
                }
            }
        }
    }

    public static class ItemListFragment extends Fragment{
        private static TextView underageWaringTextView;
        public static ItemListFragment newInstance(int index, int commitFlag) {
            ItemListFragment f = new ItemListFragment();

            Bundle args = new Bundle();
            args.putInt("index", index);
            args.putInt("commitFlag", commitFlag);
            f.setArguments(args);

            return f;
        }

        public int getShownIndex() {
            return getArguments().getInt("index", 0);
        }

        public int getCommitFlag() {
            return getArguments().getInt("commitFlag", 0);
        }

        private LinearLayout makeItemView(LayoutInflater inflater, Item item){
            LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.item, null);
            ImageView itemImgView = (ImageView)layout.findViewById(R.id.item_image);
            Bitmap itembitmap = BitmapFactory.decodeFile(getActivity().getFilesDir().getPath()+"/"+item.getBrandImg());
            itemImgView.setImageBitmap(itembitmap);
            TextView itemUnitPrice = (TextView)layout.findViewById(R.id.item_unit_price);
            itemUnitPrice.setText(getString(R.string.yen_mark)+StringUtil.getCurrencyFormat(item.getEmmPrice()));
            TextView itemStockCount = (TextView)layout.findViewById(R.id.item_stock_count);
            itemStockCount.setText(String.valueOf(item.getStockCnt()));
            Button itemPlusButton = (Button)layout.findViewById(R.id.button_item_plus);
            itemPlusButton.setTag(R.string.tagkey_item, item);
            itemPlusButton.setTag(R.string.tagkey_item_item_ordered_count, layout.findViewById(R.id.item_ordered_count));
            itemPlusButton.setOnClickListener(new OnClickListener(){
                public void onClick(View v){
                    TextView itemTextView = (TextView)v.getTag(R.string.tagkey_item_item_ordered_count);
                    Item item = (Item)v.getTag(R.string.tagkey_item);
                    int cnt = 1;
                    if(!"".equals(itemTextView.getText())){
                        cnt = Integer.valueOf(itemTextView.getText().toString()) + 1;
                    }
                    if(cnt > item.getStockCnt()){
                        String msg = getResources().getString(R.string.stock_over);
                        DialogFragment df = SimpleDialogFragment.newInstance(msg);
                        df.show(getActivity().getFragmentManager(), "dialog");
                    }else{
                        itemTextView.setText(String.valueOf(cnt));
                    }
                }
            });

            Button itemMinusButton = (Button)layout.findViewById(R.id.button_item_minus);
            itemMinusButton.setTag(R.string.tagkey_item_item_ordered_count, layout.findViewById(R.id.item_ordered_count));
            itemMinusButton.setOnClickListener(new OnClickListener(){
                public void onClick(View v){
                    TextView itemTextView = (TextView)v.getTag(R.string.tagkey_item_item_ordered_count);
                    int cnt = 0;
                    if(!"".equals(itemTextView.getText())){
                        cnt = Integer.valueOf(itemTextView.getText().toString());
                        if(cnt > 0){
                            cnt--;
                        }
                    }
                    if(cnt == 0){
                        itemTextView.setText("");
                    }else{
                        itemTextView.setText(String.valueOf(cnt));
                    }
                }
            });

            Button addCartButton = (Button)layout.findViewById(R.id.button_add_cart);
            addCartButton.setTag(R.string.tagkey_item, item);
            addCartButton.setTag(R.string.tagkey_item_item_ordered_count, layout.findViewById(R.id.item_ordered_count));
            addCartButton.setOnClickListener(new OnClickListener(){
                public void onClick(View v){
                    Item item = (Item)v.getTag(R.string.tagkey_item);
                    TextView itemTextView = (TextView)v.getTag(R.string.tagkey_item_item_ordered_count);
                    Cart cart = Cart.getInstance();
                    if(!"".equals(itemTextView.getText()) && !"0".equals(itemTextView.getText())){
                        int selectedItemCnt = Integer.parseInt(itemTextView.getText().toString());
                        if( (selectedItemCnt + cart.getTotalCount()) > Constant.CART_MAX_COUNT ){
                            String msg = getResources().getString(R.string.cart_max_count).replace("#1#", String.valueOf(Constant.CART_MAX_COUNT));
                            DialogFragment df = SimpleDialogFragment.newInstance(msg);
                            df.show(getActivity().getFragmentManager(), "dialog");
                        }else{
                            if(item.getUnderageWarningyn() == 1){
                                underageWaringTextView = itemTextView;
                                UnderageWaringConfirmDialog df = UnderageWaringConfirmDialog.newInstance(item);
                                df.show(getActivity().getFragmentManager(), "dialog");
                            }else{
                                if(item.getStockCnt() == cart.getOrderedItemCount(item)){
                                    String msg = getResources().getString(R.string.cart_already_stock_max);
                                    msg = msg.replace("#1#", item.getEmmNm());
                                    msg = msg.replace("#2#", String.valueOf(item.getStockCnt()));
                                    DialogFragment df = SimpleDialogFragment.newInstance(msg);
                                    df.show(getActivity().getFragmentManager(), "dialog");
                                }else{
                                    cart.putItemToCart(item,selectedItemCnt);
                                    itemTextView.setText("");

                                    Intent intent = new Intent(getActivity(), CartCircleService.class);
                                    int cartCount = cart.getTotalCount();
                                    intent.putExtra(Constant.CART_TOTAL_COUNT, cartCount);
                                    getActivity().startService(intent);
                                    Toast toast = Toast.makeText(
                                        getActivity(),
                                        item.getEmmNm()+getResources().getString(R.string.add_item_to_cart).replace("#1#", String.valueOf(selectedItemCnt)),
                                        Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.TOP|Gravity.RIGHT, 0, 0);
                                    toast.show();
                                }
                            }
                        }
                    }
                }
            });
            return layout;
        }

        public static class UnderageWaringConfirmDialog extends DialogFragment {
            public static UnderageWaringConfirmDialog newInstance(Item item) {
                UnderageWaringConfirmDialog frag = new UnderageWaringConfirmDialog();
                Bundle args = new Bundle();
                args.putSerializable("item", item);
                frag.setArguments(args);
                return frag;
            }

            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                Item item = (Item)getArguments().getSerializable("item");
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.underagewaring_confirm_dialog, null);
                builder.setView(layout);
                final AlertDialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                Button noButton = (Button)layout.findViewById(R.id.button_dialog_confirm_no);
                noButton.setOnClickListener(new OnClickListener(){
                    public void onClick(View v){
                        alertDialog.dismiss();
                    }
                });

                Button okButton = (Button)layout.findViewById(R.id.button_dialog_confirm_ok);
                okButton.setTag(item);
                okButton.setOnClickListener(new OnClickListener(){
                    public void onClick(View v){
                        if(underageWaringTextView != null){
                            Item item = (Item)v.getTag();
                            Cart cart = Cart.getInstance();
                            int selectedItemCnt = Integer.parseInt(underageWaringTextView.getText().toString());
                            cart.putItemToCart(item,selectedItemCnt);
                            underageWaringTextView.setText("");

                            Intent intent = new Intent(getActivity(), CartCircleService.class);
                            int cartCount = cart.getTotalCount();
                            intent.putExtra(Constant.CART_TOTAL_COUNT, cartCount);
                            getActivity().startService(intent);
                            Toast toast = Toast.makeText(
                                getActivity(),
                                item.getEmmNm()+getResources().getString(R.string.add_item_to_cart).replace("#1#", String.valueOf(selectedItemCnt)),
                                Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP|Gravity.RIGHT, 0, 0);
                            toast.show();

                            alertDialog.dismiss();
                        }
                    }
                });

                return alertDialog;
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            if (container == null) {
                return null;
            }

            ScrollView scrollView = null;
            if(itemList == null){
                if(errorMsg == null){
                    scrollView = (ScrollView)inflater.inflate(R.layout.item_def_scrollview_progress, null);
                }else{
                    scrollView = (ScrollView)inflater.inflate(R.layout.item_def_scrollview_error, null);
                    TextView em = (TextView)scrollView.findViewById(R.id.error_message);
                    em.setText(errorMsg);
                }
            }else{
                scrollView = (ScrollView)inflater.inflate(R.layout.item_def_scrollview, null);
                TableLayout tableLayout = (TableLayout)scrollView.findViewById(R.id.itemlist_tablelayout);
                if(itemList.size() == 1){
                    tableLayout.setColumnStretchable(0, true);
                    TableRow tr = (TableRow)inflater.inflate(R.layout.item_def_tablerow_1, null);;
                    Item item = itemList.get(0);
                    LinearLayout col1 = (LinearLayout)tr.findViewById(R.id.itemlist_tablerow_col1);
                    col1.addView(makeItemView(inflater,item));
                    tableLayout.addView(tr,new TableLayout.LayoutParams(Constant.MP,Constant.WC));
                }else{
                    tableLayout.setColumnStretchable(0, true);
                    tableLayout.setColumnStretchable(1, true);
                    TableRow tr = null;
                    for(int i=0;i<itemList.size();i++){
                       Item item = itemList.get(i);
                        if(i % 2 == 0){
                            tr = (TableRow)inflater.inflate(R.layout.item_def_tablerow_2, null);
                        }
                        if(i == 0){
                            LinearLayout col1 = (LinearLayout)tr.findViewById(R.id.itemlist_tablerow_col1);
                            col1.addView(makeItemView(inflater,item));
                        }else{
                            if(i % 2 == 0){
                                LinearLayout col1 = (LinearLayout)tr.findViewById(R.id.itemlist_tablerow_col1);
                                col1.addView(makeItemView(inflater,item));
                            }else{
                                LinearLayout col2 = (LinearLayout)tr.findViewById(R.id.itemlist_tablerow_col2);
                                col2.addView(makeItemView(inflater,item));
                            }
                        }
                        if(i % 2 == 1){
                            tableLayout.addView(tr,new TableLayout.LayoutParams(Constant.MP,Constant.WC));
                            tr = null;
                        }
                    }
                    if(tr != null){
                        tableLayout.addView(tr,new TableLayout.LayoutParams(Constant.MP,Constant.WC));
                        tr = null;
                    }
                }
                if(getCommitFlag() == 1){
                    itemList = null;
                }
                errorMsg = null;
            }
            return scrollView;
        }
    }
}
