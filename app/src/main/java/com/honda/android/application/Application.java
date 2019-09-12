package com.honda.android.application;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.honda.android.BuildConfig;
import com.honda.android.local.Byonchat;

/**
 * Created by Lukmanpryg on 26/7/2018.
 */
public class Application extends MultiDexApplication {
    public static Application instance;
    public static final String TAG = "VolleyPatterns";
    private RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Byonchat.init(this, "Byonchat");
        Byonchat.setEnableLog(android.support.multidex.BuildConfig.DEBUG);
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    public static synchronized Application getInstance() {
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        VolleyLog.d("Adding request to queue: %s", req.getUrl());
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        Log.w("Sendirir2", "kesini");
        req.setTag(TAG);

        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

}


