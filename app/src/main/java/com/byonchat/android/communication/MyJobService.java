package com.byonchat.android.communication;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.byonchat.android.R;

import android.app.job.JobParameters;
import android.app.job.JobService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import static android.app.Notification.PRIORITY_MIN;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MyJobService extends JobService {
    NotificationManager mNotificationManager;
    Thread thread = null;

    @Override
    public boolean onStartJob(JobParameters params) {

        thread = new Thread(new BackgroundThreadStart(this));

        if (thread != null) {
            thread.start();
        }

        jobFinished(params, false);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            scheduleRefresh();

        appendLog(new Date().toString() + " : " + "Job running");
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    public static void appendLog(String text) {
        File logFile = new File("sdcard/log.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void scheduleRefresh() {
        JobScheduler mJobScheduler = (JobScheduler) getApplicationContext()
                .getSystemService(JOB_SCHEDULER_SERVICE);
        JobInfo.Builder mJobBuilder =
                new JobInfo.Builder(543534,
                        new ComponentName(getPackageName(),
                                MyJobService.class.getName()));

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mJobBuilder
                    .setMinimumLatency(1 * 1000) //YOUR_TIME_INTERVAL
                    .setOverrideDeadline(3 * 1000) // maximum delay
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED) // require unmetered network
                    .setRequiresDeviceIdle(true) // device should be idle
                    .setRequiresCharging(false); // we don't care if the device is charging or not
        }
        mJobScheduler.schedule(mJobBuilder.build());
    }

    private class BackgroundThreadStart implements Runnable {

        Context context;

        public BackgroundThreadStart(Context ctx) {
            this.context = ctx;
        }

        @Override
        public void run() {
            try {
                if (!MessengerConnectionService.started) {
                    String channelId = "";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        channelId = createNotificationChannel("ByonChat", "Connected");
                    }

                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId);
                    Notification notification = notificationBuilder.setOngoing(true)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setPriority(PRIORITY_MIN)
                            .setCategory(Notification.CATEGORY_SERVICE)
                            .build();
                    startForeground(101, notification);

                    if (NetworkInternetConnectionStatus.getInstance(context).isOnline(context)) {
                        MessengerConnectionService.startService(context);
                    }
                }
            } catch (Exception e) {
                Log.w("datapusat", e.toString());
            }

        }
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

}