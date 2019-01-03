package com.byonchat.android.communication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.byonchat.android.utils.UploadService;

import java.util.Date;

/**
 * Created by imanfirmansyah on 3/3/17.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (NetworkInternetConnectionStatus.getInstance(context).isOnline(context)) {
            Intent intentStart = new Intent(context, UploadService.class);
            intentStart.putExtra(UploadService.ACTION, "startService");
            context.startService(intentStart);
        }
    }
}