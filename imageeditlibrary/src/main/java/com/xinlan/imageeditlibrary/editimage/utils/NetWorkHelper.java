package com.xinlan.imageeditlibrary.editimage.utils;

import androidx.annotation.NonNull;

import com.vector.update_app.HttpManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

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
}