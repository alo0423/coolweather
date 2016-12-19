package com.alo.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.alo.coolweather.model.City;
import com.alo.coolweather.model.CoolWeatherDB;
import com.alo.coolweather.model.County;
import com.alo.coolweather.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 由于服务器返回的省市县数据都是“代号|城市,代号|城市”这种格式的，所以我
 * 们最好再提供一个工具类来解析和处理这种数据
 * Created by alo on 2016/12/15.
 */

public class Utility {
    /**
     * 解析与处理服务器返回的省级数据
     */
    public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0) {
                for (String p : allProvinces
                        ) {
                    //split方法的需要的参数是正则表达式，| 在正则表达式中是特殊符号，需要转义。
                    String[] arrays = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceName(arrays[1]);
                    province.setProvinceCode(arrays[0]);

                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析与处理服务器返回的市级数据
     */
    public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB, String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0) {
                for (String c : allCities
                        ) {
                    //split方法的需要的参数是正则表达式，| 在正则表达式中是特殊符号，需要转义。
                    String[] arrays = c.split("\\|");
                    City city = new City();
                    city.setCityName(arrays[1]);
                    city.setCityCode(arrays[0]);
                    city.setProvinceId(provinceId);
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析与处理服务器返回的县级数据
     */
    public synchronized static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB, String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0) {
                for (String c : allCounties
                        ) {
                    //split方法的需要的参数是正则表达式，| 在正则表达式中是特殊符号，需要转义。
                    String[] arrays = c.split("\\|");
                    County county = new County();
                    county.setCountyName(arrays[1]);
                    county.setCountyCode(arrays[0]);
                    county.setCityId(cityId);
                    coolWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析服务器返回的json天气数据，并将解析出的数据存储到本地。
     * {"weatherinfo":
     * {"city":"昆山","cityid":"101190404","temp1":"21℃","temp2":"9℃",
     * "weather":"多云转小雨","img1":"d1.gif","img2":"n7.gif","ptime":"11:00"}
     * }
     */
    public static void handleWeatherResponse(Context context, String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            jsonObject = jsonObject.getJSONObject("weatherinfo");
            String cityName = jsonObject.getString("city");
            String weatherCode = jsonObject.getString("cityid");
            String temp1 = jsonObject.getString("temp1");
            String temp2 = jsonObject.getString("temp2");
            String weatherDesp = jsonObject.getString("weather");
            String publishTime = jsonObject.getString("ptime");
            saveWeatherInfo(context, cityName, weatherCode, temp1, temp2,
                    weatherDesp, publishTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将服务器返回的所有天气信息存储到SharedPreferences文件中。
     */
    private static void saveWeatherInfo(Context context, String cityName, String weatherCode, String temp1, String temp2, String weatherDesp, String publishTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日");
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publishTime);
        editor.putString("current_date", sdf.format(new Date()));
        editor.commit();
    }
}
