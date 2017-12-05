package jp.ne.jinoxst.mat.itg.activity;

import java.net.UnknownHostException;
import java.util.List;

import jp.ne.jinoxst.mat.itg.R;
import jp.ne.jinoxst.mat.itg.activity.callback.IMainTabs;
import jp.ne.jinoxst.mat.itg.activity.dialog.SimpleDialogFragment;
import jp.ne.jinoxst.mat.itg.pojo.Master;
import jp.ne.jinoxst.mat.itg.pojo.MasterDetail;
import jp.ne.jinoxst.mat.itg.pojo.json.Response011;
import jp.ne.jinoxst.mat.itg.pojo.json.Response011Gen;
import jp.ne.jinoxst.mat.itg.pojo.json.Response021;
import jp.ne.jinoxst.mat.itg.pojo.json.Response021Gen;
import jp.ne.jinoxst.mat.itg.service.CartCircleBroadcastReceiver;
import jp.ne.jinoxst.mat.itg.util.Constant;
import jp.ne.jinoxst.mat.itg.util.FileDownload;
import jp.ne.jinoxst.mat.itg.util.GlobalRegistry;
import jp.ne.jinoxst.mat.itg.util.db.DBAdapter;
import jp.ne.jinoxst.mat.itg.util.https.HttpsClientConnector;
import net.vvakame.util.jsonpullparser.JsonFormatException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

public class MainTabs extends TabActivity implements IMainTabs {
    private final String TAG = "MainTabs";
    private TabHost tabHost;
    private ImageView refreshImageView;
    private MenuItem masterSynchRefreshItemMenu;

    IntentFilter intentFilter;
    CartCircleBroadcastReceiver cartCircleReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initRegister();
        tabInitialize();
        actionbarToggle();
    }

    private void tabInitialize() {
        setContentView(R.layout.tabhost);
        setTabs();
        setRefreshAnimMenu();
        setCartCircleBroadCastReceiver();
    }

    private void initRegister() {
        GlobalRegistry registry = GlobalRegistry.getInstance();
        registry.setRegistry(Constant.LOGIN_ID, "itemgarage02");
        registry.setRegistry(Constant.LOGIN_PW, "945ed62d052c82195c30183e8cc00d5d");
        registry.setRegistry(Constant.MACADDRESS, "40-FC-89-76-FE-29");
        registry.setRegistry(Constant.SELECTED_TAB_INDEX, Constant.SELECTED_TAB_INDEX_0);
    }

    private void setTabs() {
        tabHost = getTabHost();

        addTab(R.string.tab_1, OrderLeftMenuList.class, R.drawable.tab_info);
        addTab(R.string.tab_2, TabCart.class, R.drawable.tab_info);
        addTab(R.string.tab_3, TabReprint.class, R.drawable.tab_info);
        addTab(R.string.tab_4, TabSaleRecord.class, R.drawable.tab_info);
    }

    private void addTab(int labelId, Class cls, int drawableId) {
        Intent intent = new Intent(this, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TabHost.TabSpec spec = tabHost.newTabSpec(String.valueOf(labelId));

        View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_unit, getTabWidget(), false);

        TextView title = (TextView) tabIndicator.findViewById(R.id.tab_title);
        title.setText(labelId);
        //        ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
        //        icon.setImageResource(drawableId);

        spec.setIndicator(tabIndicator);
        spec.setContent(intent);
        tabHost.addTab(spec);
        tabHost.setCurrentTab(Constant.SELECTED_TAB_INDEX_0);
    }

    private void setRefreshAnimMenu() {
        LayoutInflater inflater = (LayoutInflater) getApplication().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        refreshImageView = (ImageView) inflater.inflate(R.layout.actionbar_refresh_unit, null);
        ActionBar bar = getActionBar();
        bar.setCustomView(refreshImageView, new ActionBar.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL | Gravity.END));
    }

    private void actionbarToggle() {
        ActionBar bar = getActionBar();
        int change = bar.getDisplayOptions() ^ ActionBar.DISPLAY_SHOW_CUSTOM;
        bar.setDisplayOptions(change, ActionBar.DISPLAY_SHOW_CUSTOM);
    }

    private void setCartCircleBroadCastReceiver() {
        cartCircleReceiver = new CartCircleBroadcastReceiver();
        intentFilter = new IntentFilter(Constant.MAIN_TABS);
        registerReceiver(cartCircleReceiver, intentFilter);

        TextView textCircle = (TextView) findViewById(R.id.cart_circle_textview);
        textCircle.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBroadCastCompleted(int itemCount) {
        TextView textCircle = (TextView) findViewById(R.id.cart_circle_textview);
        textCircle.setText(String.valueOf(itemCount));
        paintCartCircle(textCircle, itemCount);
    }

    private void paintCartCircle(View view, int cartTotalCnt)
    {
        view.clearAnimation();
        Animation fadeout = AnimationUtils.loadAnimation(getApplication(), R.anim.fadeout);
        if (cartTotalCnt == 0) {
            view.setVisibility(View.INVISIBLE);
            view.startAnimation(fadeout);
        } else {
            view.setVisibility(View.VISIBLE);
            view.startAnimation(fadeout);

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            int originalPos[] = new int[2];
            view.getLocationOnScreen(originalPos);
            int x = width / 2 - originalPos[0] - view.getMeasuredWidth() / 2;
            int y = height / 2 - originalPos[1] - view.getMeasuredHeight() / 2;
            x = x - 50;
            y = 7;

            TranslateAnimation anim = new TranslateAnimation(x, x, 0, y);
            anim.setDuration(1000);
            anim.setInterpolator(new BounceInterpolator());
            anim.setFillAfter(true);
            view.setAnimation(anim);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.master_synchronize:
            masterSynchRefreshItemMenu = item;
            syncMaster();
            return true;
        }

        return false;
    }

    private void syncMaster() {
        if (masterSynchRefreshItemMenu.getActionView() == null) {
            new AsyncTaskForMasterSynch(this).execute();
        }
    }

    private class AsyncTaskForMasterSynch extends AsyncTask<Void, Void, Void> {
        private IMainTabs asynchComplete;
        private DBAdapter dbAdapter;

        public AsyncTaskForMasterSynch(IMainTabs masterSync) {
            this.asynchComplete = masterSync;
        }

        @Override
        protected void onPreExecute() {
            Animation anim = AnimationUtils.loadAnimation(getApplication(), R.anim.rotate);
            refreshImageView.startAnimation(anim);
            masterSynchRefreshItemMenu.setActionView(refreshImageView);
            actionbarToggle();
            dbAdapter = new DBAdapter(MainTabs.this);
            dbAdapter.open();
        }

        //        OnJsonObjectAddListener listener = new OnJsonObjectAddListener() {
        //            @Override
        //            public void onAdd(Object obj) {
        //                if (obj instanceof Master) {
        //                    checkMaster((Master)obj);
        //                }else if(obj instanceof MasterDetail){
        //                    checkMasterDetail((MasterDetail)obj);
        //                }
        //            }
        //        };

        @Override
        protected Void doInBackground(Void... v) {
            long l1 = System.currentTimeMillis();
            HttpsClientConnector connector = new HttpsClientConnector(Constant.SERVICE_CODE_011);
            connector.setParameter(Constant.LASTSEQNO, 0 + "");
            HttpGet request = connector.getRequest();
            DefaultHttpClient httpClient = connector.getHttpClient();
            try {
                HttpResponse response = httpClient.execute(request);
                HttpEntity entity = response.getEntity();
                Response011 res = Response011Gen.get(entity.getContent());
                List<Master> masters = res.getMasters();
                checkMaster(masters);
                Log.d("itemgarage", "satus:" + res.getStatus() + ", message:" + res.getMessage());
                if (res.getStatus() != 0) {
                    showDialog(res.getMessage());
                } else {
                    connector = new HttpsClientConnector(Constant.SERVICE_CODE_021);
                    connector.setParameter(Constant.LASTSEQNO, 0 + "");
                    request = connector.getRequest();
                    response = httpClient.execute(request);
                    entity = response.getEntity();
                    Response021 res021 = Response021Gen.get(entity.getContent());
                    List<MasterDetail> masterDetails = res021.getMasterdetails();
                    checkMasterDetail(masterDetails);
                    if (res021.getStatus() != 0) {
                        showDialog(res021.getMessage());
                    }
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
            Log.d("AsyncTaskForMasterSynch", "timelap:" + (l2 - l1));
            return null;
        }

        private void checkMaster(List<Master> masters) {
            for (Master master : masters) {
                if (dbAdapter.isExistLeftMenu(Integer.valueOf(master.getEmmSeq())) == 0) {
                    dbAdapter.insertLeftmenu(master);
                } else {
                    dbAdapter.updateLeftmenu(master);
                }
            }
        }

        private void checkMasterDetail(List<MasterDetail> masterDetails) {
            for (MasterDetail md : masterDetails) {
                if (dbAdapter.isExistMasterDetail(Integer.valueOf(md.getEmmId())) == 0) {
                    dbAdapter.insertMasterDetail(md);
                } else {
                    dbAdapter.updateMasterDetail(md);
                }

                if (md.getImgUrl() != null && !md.getImgUrl().equals("")) {
                    FileDownload file = new FileDownload(getApplicationContext(), md.getImgUrl());
                    file.save();
                }
            }
        }

        @Override
        protected void onPostExecute(Void v) {
            /*Cursor c = dbAdapter.getAllLeftMenu();
            if (c.moveToFirst()) {
                do {
                    Log.d("DBAdapter","fetching => "+c.getInt(1)+"/"+c.getString(2)+"/"+c.getString(3)+"/"+c.getString(4)+"/"+c.getInt(7));
                } while (c.moveToNext());
            }*/
            dbAdapter.close();
            masterSynchRefreshItemMenu.getActionView().clearAnimation();
            masterSynchRefreshItemMenu.setActionView(null);
            actionbarToggle();
            asynchComplete.onTaskCompleted();
        }
    }

    void showDialog(String msg) {
        DialogFragment df = SimpleDialogFragment.newInstance(msg);
        df.show(getFragmentManager(), "dialog");
    }

    @Override
    public void onTaskCompleted() {
        if (tabHost.getCurrentTab() == Constant.SELECTED_TAB_INDEX_0) {
            tabInitialize();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(cartCircleReceiver);
    }
}
