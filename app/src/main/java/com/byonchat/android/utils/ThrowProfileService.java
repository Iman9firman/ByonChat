package com.byonchat.android.utils;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.provider.MessengerDatabaseHelper;

/**
 * Created by byonc on 2/20/2017.
 */

public class ThrowProfileService extends IntentService implements ServiceConnection {
    private Intent      serviceIntent;
    protected MessengerConnectionService.MessengerConnectionBinder binder;
    private String data;


    public ThrowProfileService(String name) {
        super(name);
    }

    public ThrowProfileService(){
        super("ThrowProfileService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        serviceIntent = new Intent(this, UploadService.class);

        if(intent!=null)
        {
            data = (String) intent.getExtras().get("broadcast");
            getApplicationContext().bindService(
                    new Intent(this, MessengerConnectionService.class), this,
                    Context.BIND_AUTO_CREATE);

            Thread t = new Thread(new BackgroundThreadUpdate(this));
            t.start();

        }

    }

    @Override
    public void onServiceConnected(ComponentName compName, IBinder iBinder) {
        binder = (MessengerConnectionService.MessengerConnectionBinder) iBinder;
    }

    @Override
    public void onServiceDisconnected(ComponentName compName) {

    }

    private class BackgroundThreadUpdate implements Runnable {

        Context context;
        String act;

        public BackgroundThreadUpdate( Context ctx) {
            this.context = ctx;
        }

        @Override
        public void run() {
            try {
                Intent i = new Intent(data);
                sendBroadcast(i);
            } catch (Exception e) {

            }

        }
    }

}
