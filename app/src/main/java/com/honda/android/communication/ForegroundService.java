package com.honda.android.communication;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.honda.android.R;
import com.honda.android.helpers.Constants;
import com.honda.android.ui.activity.MainActivityNew;

import static android.app.Notification.PRIORITY_MIN;

public class ForegroundService extends Service {
    private static final String LOG_TAG = "ForegroundService";
    long interval = 1000 * 60; // 1 minutes in milliseconds
    NotificationManager mNotificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!MessengerConnectionService.started) {
            MessengerConnectionService.startService(getApplicationContext());
        }

        Log.i(LOG_TAG, "Received Start Foreground Intent ");
        Intent notificationIntent = new Intent(this, MainActivityNew.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.logo_byon);

        String channelId = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = createNotificationChannel("ByonChat", "Connected");

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setContentTitle("ByonChat")
                    .setContentText(MessengerConnectionService.started ? "Connected" : "Not Connected")
                    .setSmallIcon(R.mipmap.ic_launcher)
//                        .setLargeIcon(
//                                Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .build();
            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                    notification);
        } else {
            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("ByonChat")
                    .setContentText("Connected")
                    .setSmallIcon(R.drawable.logo_byon)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true).build();
            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                    notification);
        }
        return START_STICKY;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelId, String channelName) {
        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.createNotificationChannel(chan);
        return channelId;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "In onDestroy");

        /*Intent startIntent = new Intent(this, ForegroundService.class); // cannot be stopped
        startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        startService(startIntent);*/
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case of bound services.
        return null;
    }
}