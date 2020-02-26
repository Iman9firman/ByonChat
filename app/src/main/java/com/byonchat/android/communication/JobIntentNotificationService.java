package com.byonchat.android.communication;

import android.content.Context;
import android.content.Intent;
import androidx.core.app.JobIntentService;

import com.byonchat.android.utils.UploadService;

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
