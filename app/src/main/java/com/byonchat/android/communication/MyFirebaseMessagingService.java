package com.byonchat.android.communication;

import android.content.Intent;
import androidx.core.app.NotificationCompat;

import android.util.Log;

import com.byonchat.android.utils.UploadService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by Ravi Tamada on 08/08/16.
 * www.androidhive.info
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

  /*  @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "Service Created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "Service Destroyed...");
    }
*/

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Map<String, String> data = remoteMessage.getData();
        //you can get your text message here.
        String text = data.get("text");
        Log.d(TAG, "From: " + text);
        //todo perbaikan nanti dah ga bisa up dari firebase
        // sendNotification(text,text);
    }

    private void sendNotification(String message, String title) {


       /* Intent intentStart = new Intent(this, UploadService.class);
        intentStart.putExtra(UploadService.ACTION, "startService");
        startService(intentStart);
*/
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
                        .bigText("S-Team"))
                .setTicker("S-Team");

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
        Notification notification = notificationBuilder.build();

        notificationManager.notify((int) Calendar.getInstance().getTimeInMillis(), notification);*/

    }
}
