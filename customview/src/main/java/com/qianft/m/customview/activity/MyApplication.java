package com.qianft.m.customview.activity;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by Administrator on 2016/10/21.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
