package com.qiaoshi.mutiimageselector;

import android.app.Application;

public class MyApplication extends Application{
    private static MyApplication instance;
    public static MyApplication getGlobalContext() {
        return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
