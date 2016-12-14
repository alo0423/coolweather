package com.alo.coolweather.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alo.coolweather.db.CoolWeatherOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alo on 2016/12/14.
 */

public class CoolWeatherDB {
    /**
     * 数据库名
     */
    public static final String DB_NAME = "cool_weather";
    /**
     * 数据库版本
     */
    public static final int VERSION = 1;

    /* 持有私有静态实例，防止被引用，此处赋值为null，目的是实现延迟加载 */
    private static CoolWeatherDB coolWeatherDB;

    private SQLiteDatabase db;

    /**
     * 构造方法私有化，防止被实例化
     */
    private CoolWeatherDB(Context context) {
        CoolWeatherOpenHelper coolWeatherOpenHelper = new CoolWeatherOpenHelper(context, DB_NAME, null, VERSION);
        db = coolWeatherOpenHelper.getWritableDatabase();
    }

    //    /**
    //     * 获取CoolWeatherDB的实例
    //     */
    //    public synchronized static CoolWeatherDB getInstance(Context context){
    //        if (coolWeatherDB == null) {
    //            coolWeatherDB=new CoolWeatherDB(context);
    //        }
    //        return coolWeatherDB;
    //    }

    //
    //    /**
    //     *  静态工程方法，获取CoolWeatherDB的实例
    //     *
    //     * 单例类中不建议将getInstance方法修饰为synchronized方法，
    //     * 其原因是一旦这样做了，这种做法会在每次调用getInstance方法时，都需要加锁，相比效率更低。
    //     *
    //     * 似乎解决了之前提到的问题，将synchronized关键字加在了内部，也就是说当调用的时候是不需要加锁的，
    //     * 只有在instance为null，并创建对象的时候才需要加锁，性能有一定的提升。
    //     * 但是，这样的情况，还是有可能有问题的，看下面的情况：在Java指令中创建对象和赋值操作是分开进行的，
    //     * 也就是说instance = new Singleton();语句是分两步执行的。但是JVM并不保证这两个操作的先后顺序，
    //     * 也就是说有可能JVM会为新的Singleton实例分配空间，然后直接赋值给instance成员，然后再去初始化这个Singleton实例。
    //     * 这样就可能出错了，我们以A、B两个线程为例：
    //     a>A、B线程同时进入了第一个if判断
    //     b>A首先进入synchronized块，由于instance为null，所以它执行instance = new Singleton();
    //     c>由于JVM内部的优化机制，JVM先画出了一些分配给Singleton实例的空白内存，并赋值给instance成员
    //     （注意此时JVM没有开始初始化这个实例），然后A离开了synchronized块。
    //     d>B进入synchronized块，由于instance此时不是null，因此它马上离开了synchronized块并将结果返回给调用该方法的程序。
    //     e>此时B线程打算使用Singleton实例，却发现它没有被初始化，于是错误发生了。
    //     所以程序还是有可能发生错误，其实程序在运行过程是很复杂的，从这点我们就可以看出，尤其是在写多线程环境下的程序更有难度，有挑战性。
    //     */
    //    public static CoolWeatherDB getInstance(Context context) {
    //        if (coolWeatherDB == null) {
    //            synchronized (coolWeatherDB) {
    //                if (coolWeatherDB == null)
    //                    coolWeatherDB = new CoolWeatherDB(context);
    //            }
    //        }
    //        return coolWeatherDB;
    //    }

    /**
     * 因为我们只需要在创建类的时候进行同步，所以只要将创建和getInstance()分开，单独为创建加synchronized关键字
     * 考虑性能的话，整个程序只需创建一次实例，所以性能也不会有什么影响。
     */
    private static synchronized void synInit(Context context) {
        if (coolWeatherDB == null) {
            coolWeatherDB = new CoolWeatherDB(context);
        }
    }

    public CoolWeatherDB getInstance(Context context) {
        if (coolWeatherDB == null) {
            synInit(context);
        }
        return coolWeatherDB;
    }

    /* 如果该对象被用于序列化，可以保证对象在序列化前后保持一致 */
    public Object readResolve() {
        return coolWeatherDB;
    }

    /**
     * 将province实例存储到数据库
     */
    public void saveProvince(Province province) {
        if (province != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("province_name", province.getProvinceName());
            contentValues.put("province_code", province.getProvinceCode());
            db.insert("Province", null, contentValues);
        }
    }

    /**
     * 从数据库读取全国所有的省份信息
     */
    public List<Province> loadProvince() {
        List<Province> provinces = new ArrayList<>();
        Cursor cursor = db.query("Province", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                provinces.add(province);
            } while (cursor.moveToNext());
        }
        return provinces;
    }

    /**
     * 将City实例存储到数据库
     */
    public void saveCity(City city) {
        if (city != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("city_name", city.getCityName());
            contentValues.put("city_code", city.getCityCode());
            contentValues.put("province_id", city.getProvinceId());
            db.insert("City", null, contentValues);
        }
    }

    /**
     * 从数据库读取某省的城市信息
     */
    public List<City> loadCity(int provinceId) {
        List<City> cities = new ArrayList<>();
        Cursor cursor = db.query("City", null, "province_id=?", new String[]{String.valueOf(provinceId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                cities.add(city);
            } while (cursor.moveToNext());
        }
        return cities;
    }

    /**
     * 将county实例存储到数据库
     */
    public void saveCounty(County county) {
        if (county != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("county_name", county.getCountyName());
            contentValues.put("county_code", county.getCountyCode());
            contentValues.put("city_id", county.getCityId());
            db.insert("County", null, contentValues);
        }
    }

    /**
     * 从数据库读取某省的城市信息
     */
    public List<County> loadCounty(int cityId) {
        List<County> counties = new ArrayList<>();
        Cursor cursor = db.query("City", null, "city_id=?", new String[]{String.valueOf(cityId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                counties.add(county);
            } while (cursor.moveToNext());
        }
        return counties;
    }


}
