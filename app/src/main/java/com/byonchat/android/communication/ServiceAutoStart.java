package com.byonchat.android.communication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.byonchat.android.utils.UploadService;

public class ServiceAutoStart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w("masudl","Auto "+intent.getAction());
        /*
        MessengerConnectionService.startService(context);*/
        Intent intentStart = new Intent(context, UploadService.class);
        intentStart.putExtra(UploadService.ACTION, "startService");
        context.startService(intentStart);
    }

}
