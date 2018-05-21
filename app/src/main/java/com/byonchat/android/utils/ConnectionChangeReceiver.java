package com.byonchat.android.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Iman Firmansyah on 6/8/2015.
 */
public class ConnectionChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        int status = NetworkUtils.getConnectivityStatusString(context);
        if(status>0){
            Intent intentStart = new Intent(context, UploadService.class);
            intentStart.putExtra(UploadService.ACTION, "startService");
            context.startService(intentStart);
        }

    }

}