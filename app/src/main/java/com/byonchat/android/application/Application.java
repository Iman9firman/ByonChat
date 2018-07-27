package com.byonchat.android.application;

import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.byonchat.android.BuildConfig;
import com.byonchat.android.local.Byonchat;

/**
 * Created by Lukmanpryg on 26/7/2018.
 */
public class Application extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Byonchat.init(this);
    }
}

