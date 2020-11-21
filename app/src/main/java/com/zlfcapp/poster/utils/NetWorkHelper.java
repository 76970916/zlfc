package com.zlfcapp.poster.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.SPUtils;
import com.google.gson.Gson;
import com.vector.update_app.HttpManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zlfcapp.poster.App;
import com.zlfcapp.poster.Constants;
import com.zlfcapp.poster.bean.BaseConfig;
import com.zlfcapp.poster.bean.Result;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * @Description: okhttp 网络请求帮助类
 * @Author: lixh
 * @CreateDate: 2020/4/2 13:43
 * @Version: 1.0
 */
public class NetWorkHelper {

    private static NetWorkHelper netUtils = new NetWorkHelper();

    private NetWorkHelper() {
    }

    public static NetWorkHelper getInstance() {
        return netUtils;
    }

    /**
     * 异步get
     *
     * @param url    get请求地址
     * @param params get参数
     */
    public void asyncGet(@NonNull String url, @NonNull Map<String, String> params, @NonNull final HttpManager.Callback callBack) {
        OkHttpUtils.get()
                .url(url)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Response response, Exception e, int id) {
                        callBack.onError(validateError(e, response));
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        callBack.onResponse(response);
                    }
                });
    }

    /**
     * 异步post
     *
     * @param url    post请求地址
     * @param params post请求参数
     */
    public void asyncPost(@NonNull String url, @NonNull Map<String, String> params, @NonNull final HttpManager.Callback callBack) {
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Response response, Exception e, int id) {
                        callBack.onError(validateError(e, response));
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        callBack.onResponse(response);
                    }
                });

    }

    /**
     * @Author lixh
     * @Date 2020/4/2 14:08
     * @Description: 专用获取配置信息
     * 参数 自定义
     */
    public void getConfigInfo() {
        Map<String, String> params = new HashMap<>();
        params.put("c_number", App.getApp().getChanel());
        params.put("type", "config");
        params.put("packageName", App.mContext.getPackageName());
        NetWorkHelper.getInstance().asyncGet(Constants.BASE_CONFIG_INFO, params, new HttpManager.Callback() {
            @Override
            public void onResponse(String result) {
                if (result != null) {
                    Gson gson = new Gson();
                    Result<String> data = gson.fromJson(result, Result.class);
                    // 存储data 数据
                    SPUtils.getInstance().put(Constants.CONFIG_INFO, data.getData());
                }
            }

            @Override
            public void onError(String error) {
                BaseConfig.getInstance(false);
                Log.w(this.getClass().getName(), "onError: " + error);
            }
        });

    }
}
