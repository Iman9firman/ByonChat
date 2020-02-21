package com.byonchat.android.communication;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.os.Build.VERSION_CODES;

import com.byonchat.android.utils.PermanentLoggerUtil;
import com.byonchat.android.utils.UploadService;

@TargetApi(VERSION_CODES.LOLLIPOP)
public class WhatsAppJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        try{
            if (params.getJobId() == 1) {
                PermanentLoggerUtil.logMessage(this, "Repeating Job onStartJob, jobId: " + params.getJobId());
            } else {
                PermanentLoggerUtil.logMessage(this, "Chargin Job onStartJob, jobId: " + params.getJobId());
            }
            PermanentLoggerUtil.logStatus(this);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent intentStart = new Intent(this, UploadService.class);
                intentStart.putExtra(UploadService.ACTION, "startService");
                startForegroundService(intentStart);
            } else {
                Intent intentStart = new Intent(this, UploadService.class);
                intentStart.putExtra(UploadService.ACTION, "startService");
                startService(intentStart);
            }
        }catch (Exception e){

        }

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        PermanentLoggerUtil.logMessage(this, "JobService onStopJob, jobId: " + params.getJobId());
        return false;
    }
}

