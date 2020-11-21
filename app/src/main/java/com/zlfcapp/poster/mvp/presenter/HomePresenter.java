package com.zlfcapp.poster.mvp.presenter;


import com.blankj.utilcode.util.LogUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.xinlan.imageeditlibrary.editimage.bean.LogoTemplate;
import com.xinlan.imageeditlibrary.editimage.bean.TypeFace;
import com.zlfcapp.poster.App;
import com.xinlan.imageeditlibrary.editimage.bean.ImageResult;
import com.zlfcapp.poster.http.listener.HttpClientListener;
import com.zlfcapp.poster.mvp.base.BasePresenter;
import com.zlfcapp.poster.mvp.view.IHomeView;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Created by ZhangBx on 2018/7/25.
 */

public class HomePresenter extends BasePresenter<IHomeView> {
    public HomePresenter(App app) {
        super(app);
    }

    /**
     * @Author lixh
     * @Date 2020/10/15 19:35
     * @Description: 查询字体列表
     */
    public void subQueryFontList(Map<String, Object> map) {
        httpClient(getService().queryFontList(map), new HttpClientListener<String>() {
            @Override
            public void onSuccess(String response) {
                Gson gson = new GsonBuilder().create();
                JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                String results = jsonObject.get("results").toString();
                JsonArray array = new JsonParser().parse(results).getAsJsonArray();
                Type type = new TypeToken<List<TypeFace>>() {
                }.getType();
                List<TypeFace> list = gson.fromJson(array, type);
                getView().queryFont(list);
            }

            @Override
            public void onError(int code, String message) {
                getView().onError(new Throwable());
            }
        });
    }


    /**
     * @Author lixh
     * @Date 2020/10/15 19:35
     * @Description: 查询字体列表
     */
    public void subQueryFontId(Map<String, Object> map) {
        httpClient(getService().queryFontId(map), new HttpClientListener<String>() {
            @Override
            public void onSuccess(String response) {
                LogUtils.d(response);
                getView().queryFontId(response);
            }

            @Override
            public void onError(int code, String message) {
                getView().onError(new Throwable());
            }
        });
    }

    /**
     * @Author lixh
     * @Date 2020/10/15 19:35
     * @Description: 查询素材图片
     */
    public void subQueryImgList(Map<String, Object> map) {
        httpClient(getService().queryImageList(map), new HttpClientListener<String>() {
            @Override
            public void onSuccess(String response) {
                Gson gson = new GsonBuilder().create();
                JsonArray array = new JsonParser().parse(response).getAsJsonArray();
                Type type = new TypeToken<List<ImageResult>>() {
                }.getType();
                List<ImageResult> list = gson.fromJson(array, type);
                getView().queryImgList(list);
            }

            @Override
            public void onError(int code, String message) {
                getView().onError(new Throwable());
            }
        });
    }


    /**
     * @Author lixh
     * @Date 2020/10/26 11:52
     * @Description: 添加模板临时数据
     */
    public void addTempData(Map<String, Object> map) {
        httpClient(getService().AddTempData(map), new HttpClientListener<String>() {
            @Override
            public void onSuccess(String response) {
                LogUtils.d("成功");
            }

            @Override
            public void onError(int code, String message) {
                getView().onError(new Throwable());
                LogUtils.d("失败-->" + message);
            }
        });
    }

    /**
     * @Author lixh
     * @Date 2020/11/4 15:02
     * @Description: 查询所有模板数据
     */
    public void queryAllTempData(Map<String, Object> map) {
        httpClient(getService().queryAllTempData(map), new HttpClientListener<String>() {
            @Override
            public void onSuccess(String response) {
                Gson gson = new GsonBuilder().create();
                JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                String results = jsonObject.get("results").toString();
                JsonArray array = new JsonParser().parse(results).getAsJsonArray();
                Type type = new TypeToken<List<LogoTemplate>>() {
                }.getType();
                List<LogoTemplate> list = gson.fromJson(array, type);
                getView().queryAllTemplate(list);
            }

            @Override
            public void onError(int code, String message) {
                getView().onError(code, message);
            }
        });
    }
}
