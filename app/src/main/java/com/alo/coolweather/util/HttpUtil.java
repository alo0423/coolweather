package com.alo.coolweather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 获取网络数据
 * Created by alo on 2016/12/15.
 */

public class HttpUtil {
    public static void sendHttpRequest(final String address, final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                InputStream inputStream = null;
                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(5000);
                    connection.setConnectTimeout(5000);
                    // 设定传送的内容类型是可序列化的java对象    (如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)
                    connection.setRequestProperty("Content-type", "application/x-java-serialized-object");
                    connection.connect();

                    inputStream = connection.getInputStream();
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    
                    //报异常java.io.EOFException
//                    String line;
//                    while ((line = bufferedReader.readLine()) != null) {
//                        response.append(line);
//                    }
                    byte[]b=new byte[1024];
                    int len=0;
                    if ((len = inputStream.read(b)) != -1) {
                        response.append(new String(b,0,len));
                    }

                    if (listener != null) {
                        listener.onFinish(response.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.onError(e);
                    }
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
