package com.zlfcapp.poster.mvp.presenter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.zlfcapp.poster.App;
import com.zlfcapp.poster.bean.FeedBacks;
import com.zlfcapp.poster.http.listener.HttpClientListener;
import com.zlfcapp.poster.mvp.base.BasePresenter;
import com.zlfcapp.poster.mvp.view.IFeedBack;

import java.util.Map;

public class FeedBackPresenter extends BasePresenter<IFeedBack> {
    public FeedBackPresenter(App app) {
        super(app);
    }

    public void SubMessage(String msg, Map<String, String> map) {
        httpClient(getService().subFaq(msg, map), new HttpClientListener<String>() {
            @Override
            public void onSuccess(String response) {
                response = "反馈成功";
                getView().OnFeedBackResut(response);
            }

            @Override
            public void onError(int code, String message) {
                getView().onError(new Throwable());
            }
        });
    }


    public void SubGetFeedBackList(Map<String, String> map) {
        httpClient(getService().subGetFeedBack(map), new HttpClientListener<String>() {
            @Override
            public void onSuccess(String response) {
                // 带有日期类型的json字符串解析
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                        .create();
                FeedBacks feedBacks = gson.fromJson(response, new TypeToken<FeedBacks>() {
                }.getType());
                if (getView() != null) {
                    getView().getFeedbackList(feedBacks);
                }
            }

            @Override
            public void onError(int code, String message) {
                getView().onError(new Throwable());
            }
        });
    }
}
