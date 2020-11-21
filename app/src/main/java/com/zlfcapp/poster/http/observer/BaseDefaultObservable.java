package com.zlfcapp.poster.http.observer;

import androidx.annotation.NonNull;

import com.zlfcapp.poster.http.listener.ObserverListener;

public class BaseDefaultObservable<T> extends BaseObserver<T> {

    private static final int SUCCESSCODE = 1;

    public BaseDefaultObservable(@NonNull ObserverListener<T> listener) {
        super(listener);
    }

    @Override
    void onResponse(T data, int code, String message) {
        switch (code) {
            case SUCCESSCODE:
                listener.onNext(data);
                break;
            default:
                listener.onError(code, message);
                break;
        }
    }

    @Override
    void onResponseError(int code, String message) {
        listener.onError(code, message);
    }
}
