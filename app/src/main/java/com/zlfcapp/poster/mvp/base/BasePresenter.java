package com.zlfcapp.poster.mvp.base;


import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.zlfcapp.poster.App;
import com.zlfcapp.poster.bean.Result;
import com.zlfcapp.poster.di.component.AppComponent;
import com.zlfcapp.poster.http.APIService;
import com.zlfcapp.poster.http.HttpClient;
import com.zlfcapp.poster.http.listener.HttpClientListener;
import com.zlfcapp.poster.rx.RxManager;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since 2017/2/20
 */

public class BasePresenter<V extends BaseView> extends MvpBasePresenter<V> {

    private App app;
    private AppComponent mAppComponent;
    private HttpClient mClient;

    @Inject
    public BasePresenter(App app) {
        super();
        this.app = app;
        mAppComponent = app.getAppCommponent();
    }

    public V mBaseView;
    public RxManager mRxManager = new RxManager();

    public void attachVM(V v) {
        this.mBaseView = v;
    }

    public void detachVM() {
        mRxManager.clear();
        mBaseView = null;
        mClient.dispose();
    }

    public App getApp() {
        return app;
    }

    @Override
    public boolean isViewAttached() {
        //LogUtils.d("isViewAttached:" + super.isViewAttached());
        return super.isViewAttached();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

    public APIService getService() {
        return getAppComponent().getAPIService();
    }

    public HttpClient getClient() {
        return mClient;
    }

    /**
     * 普通请求，自动解析
     */
    public <T> void httpClient(Observable<Result<T>> observable, HttpClientListener<T> listener) {
        if (mClient == null) {
            mClient = new HttpClient(getAppComponent().getAPIService());
        }
        mClient.client(observable, listener);
    }

    /**
     * 普通请求，返回未解析json字符串
     */
    public void httpClientToOriginal(Observable<String> observable, HttpClientListener<String> listener) {
        if (mClient == null) {
            mClient = new HttpClient(getAppComponent().getAPIService());
        }
        mClient.clientToOriginal(observable, listener);
    }


}
