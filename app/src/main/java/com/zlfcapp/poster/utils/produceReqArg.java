package com.zlfcapp.poster.utils;


import com.blankj.utilcode.util.AppUtils;
import com.zlfcapp.poster.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 生成请求参数
 * @Author: lixh
 * @CreateDate: 2020/4/25 19:33
 * @Version: 1.0
 */
public class produceReqArg {

    public static Map<String, String> generate(String mark) {
        Map<String, String> map = new HashMap<>();
        int tm = (int) (System.currentTimeMillis() / 1000);
        // TODO md5_code 修改成本项目
        String appendMD5 = mark + Constants.MD5_CODE + tm;
        map.put("tm", String.valueOf(tm));
        map.put("key", MD5Utils.MD5(appendMD5));
        return map;
    }

    /**
     * @Author lixh
     * @Date 2020/4/28 16:18
     * @Description: 反馈专用
     */
    public static Map<String, String> generateFeedback(String mark) {
        Map<String, String> map = new HashMap<>();
        int tm = (int) (System.currentTimeMillis() / 1000);
        String appendMD5 = mark + Constants.MD5_CODE_FEEDBACK + tm;
        map.put("tm", String.valueOf(tm));
        map.put("key", MD5Utils.MD5(appendMD5));
        return map;
    }

    public static Map<String, Object> generateObj(String mark) {
        Map<String, Object> map = new HashMap<>();
        int tm = (int) (System.currentTimeMillis() / 1000);
        String appendMD5 = mark + Constants.MD5_CODE + tm;
        map.put("tm", String.valueOf(tm));
        map.put("key", MD5Utils.MD5(appendMD5));
        map.put("packagename", AppUtils.getAppPackageName());
        return map;
    }


}
