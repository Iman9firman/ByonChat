package com.byonchat.android.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class BCServiceSync extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static BCServiceSyncAdapter myServiceSyncAdapter = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("BCServiceSync", "onCreate");
        synchronized (sSyncAdapterLock) {
            if (myServiceSyncAdapter == null) {
                myServiceSyncAdapter = new BCServiceSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("BCServiceSync", "onBind");
        return myServiceSyncAdapter.getSyncAdapterBinder();
    }
}
