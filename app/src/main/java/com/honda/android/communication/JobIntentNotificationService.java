package com.honda.android.communication;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.JobIntentService;

import com.honda.android.utils.UploadService;

import org.jetbrains.annotations.NotNull;

public class JobIntentNotificationService extends JobIntentService {

    public static void start(Context context) {
        Intent starter = new Intent(context, JobIntentNotificationService.class);
        JobIntentNotificationService.enqueueWork(context, starter);
    }

    static final int JOB_ID = 1000;

    private static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, JobIntentNotificationService.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NotNull Intent intent) {
        Intent intentStart = new Intent(this, UploadService.class);
        intentStart.putExtra(UploadService.ACTION, "startService");
        startService(intentStart);
    }
}
