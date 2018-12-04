package com.byonchat.android.local;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Handler;

import com.byonchat.android.Manhera.Manhera;
import com.byonchat.android.data.local.ByonchatVideoTubeDataStore;
import com.byonchat.android.data.local.ByonchatVideoTubeDatabaseHelper;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.RoomsDB;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by Lukmanpryg on 26/7/2018.
 */
public class Byonchat {

    @SuppressLint("StaticFieldLeak")
    private static Application appInstance;
    private static String appServer;
    private static String appId;
    private static String fileServer;
    private static ByonchatVideoTubeDataStore videoTubeDataStore;
    private static Handler appHandler;
    private static String authorities;
    private static BotListDB botListDB;
    private static MessengerDatabaseHelper messengerDatabaseHelper;
    private static RoomsDB roomsDB;
    private static ScheduledThreadPoolExecutor taskExecutor;
    private static boolean enableLog;

    private Byonchat() {
    }

    public static void init(Application application) {
        init(application, "https://bb.byonchat.com");
    }

    public static void init(Application application, String byonchatAppId) {
        init(application, byonchatAppId, "https://bb.byonchat.com", "https://pp.byonchat.com");
    }

    public static void init(Application application, String byonchatAppId, String serverBaseUrl, String fileBaseUrl) {
        appInstance = application;
        appId = byonchatAppId;
        appServer = serverBaseUrl;
        fileServer = fileBaseUrl;
        videoTubeDataStore = new ByonchatVideoTubeDatabaseHelper();
        appHandler = new Handler(appInstance.getApplicationContext().getMainLooper());
        taskExecutor = new ScheduledThreadPoolExecutor(5);
        appInstance.registerActivityLifecycleCallbacks(ActivityCallback.INSTANCE);
        authorities = appInstance.getPackageName();
        botListDB = BotListDB.getInstance(application);
        messengerDatabaseHelper = MessengerDatabaseHelper.getInstance(application);
        roomsDB = new RoomsDB(application);

        Manhera.init(application);
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

    private static void checkAppIdSetup() throws RuntimeException {
        if (appServer == null) {
            throw new RuntimeException("Please init Byonchat with your app id before!");
        }
    }

    public static String getAppId() {
        checkAppIdSetup();
        return appId;
    }

    public static String getFileServer() {
        checkFileIdSetup();
        return fileServer;
    }

    public static ByonchatVideoTubeDataStore getVideoTubeDataStore() {
        return videoTubeDataStore;
    }

    private static void checkFileIdSetup() throws RuntimeException {
        if (fileServer == null) {
            throw new RuntimeException("Please init Byonchat with your file id before!");
        }
    }

    public static BotListDB getBotListDB() {
        return botListDB;
    }

    public static MessengerDatabaseHelper getMessengerHelper() {
        return messengerDatabaseHelper;
    }

    public static RoomsDB getRoomsDB() {
        return roomsDB;
    }
}
