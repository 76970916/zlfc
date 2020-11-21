package com.zlfcapp.poster.di.module;

import android.content.Context;

import com.zlfcapp.poster.App;
import com.zlfcapp.poster.Constants;
import com.zlfcapp.poster.http.APIService;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since 2017/2/24
 */
@Module
public class AppModule {
    /**
     * 默认超时时间 单位/秒
     */
    private static final int DEFAULT_TIME_OUT = 10;

    private Context context;

    private String baseUrl;

    public AppModule(App context, String baseUrl) {
        this.context = context;
        this.baseUrl = baseUrl;
    }

    @Provides
    @Singleton
    public Context provideContext() {
        return context;
    }

    @Provides
    @Singleton
    public Retrofit provideRetrofit() {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                // 改成RxJava2CallAdapterFactory
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(Constants.BASE_URL)
                .build();
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient() {

        return new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
                .build();
    }

    @Provides
    @Singleton
    public APIService provideAPIService() {
        return provideRetrofit().create(APIService.class);
    }
}
