package com.zlfcapp.poster.bean;

import android.content.Context;


import com.zlfcapp.poster.App;
import com.zlfcapp.poster.utils.Device;
import com.zlfcapp.poster.utils.Tools;

import java.util.HashMap;

/**
 * Created by ZhangBx on 2018/6/2.
 */

public class QueryMap {
    public static HashMap<String,String> getExpressQueryMap(Context context){
        HashMap<String,String> map=new HashMap<>();
        map.put("newudid", Device.getUniqueId(context));
        map.put("his", Device.getHis());
        map.put("device_model",Device.getSystemModel());
        map.put("device_brand",Device.getDeviceBrand());
        map.put("udid",Device.getImei(context));
        map.put("mac",Device.getMac(context));
        return map;
    }
    public static HashMap<String,String> getTmKey(String code,Context context){
        String tm=System.currentTimeMillis()/1000+"";
        String key= Tools.md5("cjkj"+code+tm);
        HashMap<String,String> map=new HashMap<>();
        map.put("pkg",context.getPackageName());
        map.put("tm",tm);
        map.put("key",key);
        map.put("lng", App.getApp().getString("lng","0.0"));
        map.put("lat", App.getApp().getString("lat","0.0"));
        map.putAll(getExpressQueryMap(context));
        return map;
    }
}
