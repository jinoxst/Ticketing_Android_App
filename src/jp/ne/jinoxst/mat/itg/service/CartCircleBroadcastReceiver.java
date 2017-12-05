package jp.ne.jinoxst.mat.itg.service;

import jp.ne.jinoxst.mat.itg.activity.MainTabs;
import jp.ne.jinoxst.mat.itg.util.Constant;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class CartCircleBroadcastReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        int itemCountInCart = bundle.getInt(Constant.CART_TOTAL_COUNT);

        MainTabs activity = (MainTabs)context;
        activity.onBroadCastCompleted(itemCountInCart);
    }
}