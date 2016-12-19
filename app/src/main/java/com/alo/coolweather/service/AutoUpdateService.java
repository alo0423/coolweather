package com.alo.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.alo.coolweather.util.HttpCallbackListener;
import com.alo.coolweather.util.HttpUtil;
import com.alo.coolweather.util.Utility;

/**
 * 自动启动服务
 * Created by alo on 2016/12/19.
 */

public class AutoUpdateService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 8 * 60 * 60 * 1000;
        //使用SystemClock.elapsedRealtime()方法可
        //以获取到系统开机至今所经历时间的毫秒数，使用System.currentTimeMillis()方法可以获取
        //到1970 年1 月1 日0 点至今所经历时间的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;//526

        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        /**
         * 第一个参数是一个整型参数，用于指定AlarmManager 的
         工作类型，有四种值可选，分别是ELAPSED_REALTIME、ELAPSED_REALTIME_WAKEUP、
         RTC 和RTC_WAKEUP。其中ELAPSED_REALTIME 表示让定时任务的触发时间从系统开
         机开始算起，但不会唤醒CPU。ELAPSED_REALTIME_WAKEUP 同样表示让定时任务的触
         发时间从系统开机开始算起，但会唤醒CPU。RTC 表示让定时任务的触发时间从1970 年1
         月1 日0 点开始算起，但不会唤醒CPU。RTC_WAKEUP 同样表示让定时任务的触发时间从
         1970 年1 月1 日0 点开始算起，但会唤醒CPU。
         第三个参数是一个PendingIntent，这里我们一般会调
         用getBroadcast()方法来获取一个能够执行广播的PendingIntent。这样当定时任务被触发的时
         候，广播接收器的onReceive()方法就可以得到执行.
         */
        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新天气信息
     */
    private void updateWeather() {
        SharedPreferences sp = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(this);
        String weatherCode = sp.getString("weather_code", "");
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(AutoUpdateService.this, response);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

}
