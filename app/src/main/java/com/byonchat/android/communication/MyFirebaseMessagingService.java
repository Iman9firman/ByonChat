package com.byonchat.android.communication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.byonchat.android.MainActivity;
import com.byonchat.android.R;
import com.byonchat.android.utils.UploadService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;

/**
 * Created by Ravi Tamada on 08/08/16.
 * www.androidhive.info
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "Service Created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "Service Destroyed...");
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Map<String, String> data = remoteMessage.getData();
        //you can get your text message here.
        String text= data.get("text");
        Log.d(TAG, "From: " + text);
        sendNotification(text,text);
    }

    private void sendNotification(String message, String title) {

        Intent intentStart = new Intent(this, UploadService.class);
        intentStart.putExtra(UploadService.ACTION, "startService");
        startService(intentStart);


       /* hilang firebase
       int requestID = (int) System.currentTimeMillis();
        Intent intent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notif)
                .setContentTitle(title)
                .setContentText("You may have new messages").setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("ByonChat"))
                .setTicker("ByonChat");

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
        Notification notification = notificationBuilder.build();

        notificationManager.notify((int) Calendar.getInstance().getTimeInMillis(), notification);*/

    }
}
