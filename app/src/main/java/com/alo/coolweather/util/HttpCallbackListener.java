package com.alo.coolweather.util;

/**
 * 获取网络数据的回调接口
 * Created by alo on 2016/12/15.
 */

public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
