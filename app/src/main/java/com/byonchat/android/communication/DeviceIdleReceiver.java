package com.byonchat.android.communication;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION_CODES;
import android.os.PowerManager;

import com.byonchat.android.utils.PermanentLoggerUtil;

public class DeviceIdleReceiver extends BroadcastReceiver {

    @TargetApi(VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PermanentLoggerUtil.logMessage(context, "Idle change, is device idle mode: " + pm.isDeviceIdleMode());
    }
}
