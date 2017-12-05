package jp.ne.jinoxst.mat.itg.service;

import jp.ne.jinoxst.mat.itg.util.Constant;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

public class CartCircleService extends IntentService {
    final static String TAG = "CartCircleIntentService";

    public CartCircleService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        Intent broadcastIntent = new Intent();
        broadcastIntent.putExtra(Constant.CART_TOTAL_COUNT, bundle.getInt(Constant.CART_TOTAL_COUNT));
        broadcastIntent.setAction(Constant.MAIN_TABS);
        getBaseContext().sendBroadcast(broadcastIntent);
    }
}
