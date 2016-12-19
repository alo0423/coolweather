package com.alo.coolweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.alo.coolweather.service.AutoUpdateService;

/**
 * Created by alo on 2016/12/19.
 */

public class AutoUpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1=new Intent(context, AutoUpdateService.class);
        context.startActivity(intent1);
    }
}
