package com.xinlan.imageeditlibrary.editimage;

import android.app.Application;

import org.litepal.LitePal;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
    }
}
