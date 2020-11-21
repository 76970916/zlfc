package com.zlfcapp.poster.utils.updateapp;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.king.base.util.ToastUtils;
import com.vector.update_app.HttpManager;
import com.vector.update_app.UpdateAppBean;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zlfcapp.poster.App;
import com.zlfcapp.poster.Constants;
import com.zlfcapp.poster.bean.Result;
import com.zlfcapp.poster.bean.UpdateMessageBean;
import com.zlfcapp.poster.utils.CommonUtils;
import com.zlfcapp.poster.utils.SPUtils;

import java.io.File;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Description: app 更新专用
 * @Author: lixh
 * @CreateDate: 2020/4/2 13:13
 * @Version: 1.0
 */
public class UpdateAppHttpUtil implements HttpManager {

    /**
     * 异步get
     *
     * @param url      get请求地址
     * @param params   get参数
     * @param callBack 回调
     */
    @Override
    public void asyncGet(@NonNull String url, @NonNull Map<String, String> params, @NonNull final Callback callBack) {
        params.put("c_number", CommonUtils.getChannel());
        params.put("versionCode", String.valueOf(CommonUtils.getAppVersionCode()));
        params.put("packageName", App.mContext.getPackageName());
        params.put("type", "update");
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
                        String transfBean = transferJson(response);
                        callBack.onResponse(transfBean);
                    }
                });
    }

    /**
     * author  lixh
     * date 2020/4/1 20:22
     * desc  进行数据转化
     **/
    private String transferJson(String response) {
        // 存储更新信息
        SPUtils.getInstance(App.mContext).put("app_update_data", response);
        Gson gson = new Gson();
        UpdateAppBean vo = new UpdateAppBean();
        Result result = gson.fromJson(response, Result.class);
        UpdateMessageBean messageBean = gson.fromJson(String.valueOf(result.getData()), UpdateMessageBean.class);
        if (messageBean == null) {
            return "";
        }
        vo.setApkFileUrl(messageBean.getApkPath());
        vo.setNewVersion(String.valueOf(messageBean.getUpdateVersion()));
        vo.setTargetSize(String.valueOf(messageBean.getApkSize()));
        vo.setUpdateLog(messageBean.getUpdate_log());
        int currVersion = Integer.valueOf(CommonUtils.getAppVersionCode());
        int nowVersion = Integer.valueOf(messageBean.getUpdateVersion());
        int compleVersion = Integer.valueOf(messageBean.getCompleVersion());
        vo.setConstraint(compleVersion >= currVersion ? true : false);
        if (nowVersion < currVersion) {
            // 判断是否使用户点击
            boolean isuserclick = SPUtils.getInstance(App.mContext).getBoolean(Constants.ISUSERCLICK, false);
            if (isuserclick) {
                ToastUtils.showToast(App.mContext, "暂无更新");
            }
            SPUtils.getInstance(App.mContext).put(Constants.ISUSERCLICK, false);
        } else {
            vo.setUpdate("Yes");
        }
        return gson.toJson(vo);
    }

    /**
     * 异步post
     *
     * @param url      post请求地址
     * @param params   post请求参数
     * @param callBack 回调
     */
    @Override
    public void asyncPost(@NonNull String url, @NonNull Map<String, String> params, @NonNull final Callback callBack) {
        params.put("c_number", CommonUtils.getChannel());
        params.put("versionCode", String.valueOf(CommonUtils.getAppVersionCode()));
        params.put("packageName", App.mContext.getPackageName());
        params.put("type", "update");
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
                        // 进行数据转化
                        String transfBean = transferJson(response);
                        callBack.onResponse(transfBean);
                    }
                });

    }

    /**
     * 下载
     *
     * @param url      下载地址
     * @param path     文件保存路径
     * @param fileName 文件名称
     * @param callback 回调
     */
    @Override
    public void download(@NonNull String url, @NonNull String path, @NonNull String fileName, @NonNull final FileCallback callback) {
        OkHttpUtils.get()
                .url(url)
                .build()
                .execute(new FileCallBack(path, fileName) {
                    @Override
                    public void inProgress(float progress, long total, int id) {
                        callback.onProgress(progress, total);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e, int id) {
                        callback.onError(validateError(e, response));
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        callback.onResponse(response);

                    }

                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                        callback.onBefore();
                    }
                });
    }
}
