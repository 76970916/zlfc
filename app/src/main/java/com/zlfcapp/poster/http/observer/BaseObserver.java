package com.zlfcapp.poster.http.observer;

import androidx.annotation.NonNull;

import com.zlfcapp.poster.bean.Result;
import com.zlfcapp.poster.http.HttpCodeConstant;
import com.zlfcapp.poster.http.listener.ObserverListener;

import org.json.JSONException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.ParseException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

import static com.zlfcapp.poster.http.HttpCodeConstant.BAD_GATEWAY_CODE;
import static com.zlfcapp.poster.http.HttpCodeConstant.BAD_REQUEST_CODE;
import static com.zlfcapp.poster.http.HttpCodeConstant.FORBIDDEN_CODE;
import static com.zlfcapp.poster.http.HttpCodeConstant.GATEWATTIMEDOUT_CODE;
import static com.zlfcapp.poster.http.HttpCodeConstant.INTERNAL_SERVER_ERROR_CODE;
import static com.zlfcapp.poster.http.HttpCodeConstant.METHOD_NOT_ALLOWED_CODE;
import static com.zlfcapp.poster.http.HttpCodeConstant.NOT_FOUND_CODE;
import static com.zlfcapp.poster.http.HttpCodeConstant.REQUEST_TIMEOUT_CODE;
import static com.zlfcapp.poster.http.HttpCodeConstant.SERVER_UNAVAILABLE_CODE;
import static com.zlfcapp.poster.http.HttpCodeConstant.UNAUTHORIZED_CODE;

abstract public class BaseObserver<T> implements Observer<Result<T>> {

    protected ObserverListener listener;

    public BaseObserver(@NonNull ObserverListener<T> listener) {
        this.listener = listener;
    }

    @Override
    public void onComplete() {
    }

    abstract void onResponse(T data, int code, String message);

    abstract void onResponseError(int code, String message);

    @Override
    public void onSubscribe(Disposable d) {
        listener.onLoading(d);
    }

    @Override
    public void onError(Throwable e) {
        String msg = null;
        int code = -1;
        if (e instanceof HttpException) {             //HTTP错误
            code = ((HttpException) e).code();
            switch (code) {
                //未授权
                case UNAUTHORIZED_CODE:
                    msg = HttpCodeConstant.UNAUTHORIZED;
                    break;
                //禁止访问
                case FORBIDDEN_CODE:
                    //处理403错误
                    msg = HttpCodeConstant.FORBIDDEN;
                    break;
                //无法找到资源
                case NOT_FOUND_CODE:
                    //处理404错误
                    msg = HttpCodeConstant.NOT_FOUND;
                    break;
                //请求方法不允许
                case METHOD_NOT_ALLOWED_CODE:
                    //处理405错误
                    msg = HttpCodeConstant.METHOD_NOT_ALLOWED;
                    break;
                //请求超时
                case REQUEST_TIMEOUT_CODE:
                    //处理408错误
                    msg = HttpCodeConstant.REQUEST_TIMEOUT;
                    break;
                //服务器内部错误
                case BAD_GATEWAY_CODE:
                case INTERNAL_SERVER_ERROR_CODE:
                    //处理500错误
                    msg = HttpCodeConstant.INTERNAL_SERVER_ERROR;
                    break;
                //请求出错
                case BAD_REQUEST_CODE:
                    //处理400错误
                    msg = HttpCodeConstant.BAD_REQUEST;
                    break;
                case SERVER_UNAVAILABLE_CODE:
                    //处理502错误
                case GATEWATTIMEDOUT_CODE:
                    //处理503错误
                    msg = HttpCodeConstant.BAD_NET_WORK;
                default:
                    msg = HttpCodeConstant.UNKNOW_ERROR;
            }//其他
        } else if (e instanceof JSONException
                || e instanceof ParseException
        ) {
            msg = HttpCodeConstant.PARSE_ERROR;
        } else if (e instanceof IOException){
            if (e instanceof SocketTimeoutException){
                msg = HttpCodeConstant.REQUEST_TIMEOUT;
            } else{
                msg = HttpCodeConstant.BAD_NET_WORK;
            }
        } else{
            msg = HttpCodeConstant.UNKNOW_ERROR;
        }
        onResponseError(code, msg);
    }

    @Override
    public void onNext(Result<T> t) {
        if (t!=null){
            onResponse(t.getData(),t.getCode(),t.getMsg());
        }
    }
}
