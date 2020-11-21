package com.zlfcapp.poster.http;


import com.zlfcapp.poster.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since 2017/2/13
 */

public class APIRetrofit {

    /**
     * 默认超时时间 单位/秒
     */
    private static final int DEFAULT_TIME_OUT = 10;

    private static Retrofit sRetrofit;

    private static OkHttpClient sOKHttpClient;

    public static APIService getAPIService() {
        return getInstance().create(APIService.class);
    }

    public static Retrofit getInstance() {
        if (sRetrofit == null) {
            synchronized (APIRetrofit.class) {
                if (sRetrofit == null) {

                    sRetrofit = new Retrofit.Builder()
                            .addConverterFactory(GsonConverterFactory.create())
                            // 改成RxJava2CallAdapterFactory
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .baseUrl(Constants.BASE_URL)
                            .build();

                }
            }
        }

        return sRetrofit;
    }


    public static OkHttpClient getsOKHttpClient() {
        if (sOKHttpClient == null) {
            synchronized (APIRetrofit.class) {

                if (sOKHttpClient == null) {
                    sOKHttpClient = new OkHttpClient.Builder()
                            .connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
                            .readTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
                            .writeTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
                            .build();
                }
            }
        }

        return sOKHttpClient;
    }


}
