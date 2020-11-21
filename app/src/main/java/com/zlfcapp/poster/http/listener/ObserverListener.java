package com.zlfcapp.poster.http.listener;

import io.reactivex.disposables.Disposable;

public interface ObserverListener<T> {
    void onNext(T data);
    void onLoading(Disposable disposable);
    void onError(int code,String message);
}
