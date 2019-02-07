package com.byonchat.android.communication;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import com.byonchat.android.utils.UploadService;
import com.byonchat.android.utils.Utility;

public class ServiceAutoStart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w("summerparadise", "Auto " + intent.getAction());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Utility.scheduleJob(context);
        } else {
            UploadService mUploadService = new UploadService();
            Intent mServiceIntent = new Intent(context, mUploadService.getClass());
            mServiceIntent.putExtra(UploadService.ACTION, "startService");
            if (!isMyServiceRunning(context, mUploadService.getClass())) {
                context.startService(mServiceIntent);
            }

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 898989,
                    mServiceIntent, 0);
            int alarmType = AlarmManager.ELAPSED_REALTIME;
            final int FIFTEEN_SEC_MILLIS = 15000;
            AlarmManager alarmManager = (AlarmManager)
                    context.getSystemService(context.ALARM_SERVICE);
            alarmManager.setRepeating(alarmType, SystemClock.elapsedRealtime() + FIFTEEN_SEC_MILLIS,
                    FIFTEEN_SEC_MILLIS, pendingIntent);

            ComponentName receiver = new ComponentName(context, MyBroadcastReceiver.class);
            PackageManager pm = context.getPackageManager();

            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }

    }

    protected boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }
}
