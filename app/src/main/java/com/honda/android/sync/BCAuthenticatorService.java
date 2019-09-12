package com.honda.android.sync;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class BCAuthenticatorService extends Service {
    private BCAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        Log.d("BCAuthenticatorService", "onCreate");
        mAuthenticator = new BCAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("BCAuthenticatorService", "onBind");
        return mAuthenticator.getIBinder();
    }
}