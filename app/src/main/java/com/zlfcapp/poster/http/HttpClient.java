package com.zlfcapp.poster.http;


import com.zlfcapp.poster.bean.Result;
import com.zlfcapp.poster.http.listener.HttpClientListener;
import com.zlfcapp.poster.http.listener.ObserverListener;
import com.zlfcapp.poster.http.observer.BaseDefaultObservable;
import com.zlfcapp.poster.http.observer.BaseOriginalObserver;
import com.zlfcapp.poster.rx.RxSchedulers;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class HttpClient {
    private APIService apiService;

    private CompositeDisposable compositeDisposable;

    public HttpClient(APIService apiService) {
        this.apiService = apiService;
        compositeDisposable = new CompositeDisposable();
    }

    public APIService getApiService() {
        return apiService;
    }

    /**
     * 自动解析,传入实体类
     */
    public <T> void client(Observable<Result<T>> observable, HttpClientListener<T> listener) {
        BaseDefaultObservable observer = new BaseDefaultObservable<>(new ObserverListener<T>() {
            @Override
            public void onNext(T data) {
                listener.onSuccess(data);
            }

            @Override
            public void onLoading(Disposable disposable) {
                compositeDisposable.add(disposable);
            }

            @Override
            public void onError(int code, String message) {
                listener.onError(code, message);
            }
        });
        observable.compose(RxSchedulers.compose())
                .subscribe(observer);
    }

    /**
     * 获取完整json字符串
     **/
    public void clientToOriginal(Observable<String> observable, HttpClientListener<String> listener) {
        BaseOriginalObserver observer = new BaseOriginalObserver(new ObserverListener<String>() {
            @Override
            public void onNext(String data) {
                listener.onSuccess(data);
            }

            @Override
            public void onLoading(Disposable disposable) {
                compositeDisposable.add(disposable);
            }

            @Override
            public void onError(int code, String message) {
                listener.onError(code, message);
            }
        });
        observable.compose(RxSchedulers.compose())
                .subscribe(observer);
    }

    /**
     * 取消请求
     */
    public void  dispose() {
        compositeDisposable.dispose();
    }

}
