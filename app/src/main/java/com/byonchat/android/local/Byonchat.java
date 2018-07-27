package com.byonchat.android.local;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Handler;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by Lukmanpryg on 26/7/2018.
 */
public class Byonchat {

    @SuppressLint("StaticFieldLeak")
    private static Application appInstance;
    private static String appServer;
    private static Handler appHandler;
    private static String authorities;
    private static ScheduledThreadPoolExecutor taskExecutor;
    private static boolean enableLog;

    private Byonchat() {
    }

    public static void init(Application application) {
        init(application, "https://bb.byonchat.com");
    }

    public static void init(Application application, String serverBaseUrl) {
        appInstance = application;
        appServer = serverBaseUrl;
        appHandler = new Handler(appInstance.getApplicationContext().getMainLooper());
        taskExecutor = new ScheduledThreadPoolExecutor(5);
        appInstance.registerActivityLifecycleCallbacks(ActivityCallback.INSTANCE);
        authorities = appInstance.getPackageName();
    }

    public static Application getApps() {
        return appInstance;
    }

    public static String getAppServer() {
        return appServer;
    }

    public static String getProviderAuthorities() {
        return authorities;
    }

    public static Handler getAppsHandler() {
        return appHandler;
    }

    public static ScheduledThreadPoolExecutor getTaskExecutor() {
        return taskExecutor;
    }

    public static String getAppsName() {
        return appInstance.getApplicationInfo().loadLabel(appInstance.getPackageManager()).toString();
    }
    public static void setEnableLog(boolean enableLog) {
        Byonchat.enableLog = enableLog;
    }
    public static boolean isEnableLog() {
        return Byonchat.enableLog;
    }
}
