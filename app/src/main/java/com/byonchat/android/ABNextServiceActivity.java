package com.byonchat.android;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.communication.MessengerConnectionService.MessengerConnectionBinder;

public abstract class ABNextServiceActivity extends ABNextActivity implements
        ServiceConnection {
    protected MessengerConnectionBinder binder;
    protected ProgressDialog pdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (pdialog == null) {
            pdialog = new ProgressDialog(this);
            pdialog.setIndeterminate(true);
            pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        getApplicationContext().unbindService(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getApplicationContext().bindService(
                new Intent(this, MessengerConnectionService.class), this,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName compName, IBinder iBinder) {
        binder = (MessengerConnectionBinder) iBinder;
    }

    @Override
    public void onServiceDisconnected(ComponentName compName) {

    }

}
