package com.byonchat.android.communication;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.byonchat.android.utils.UploadService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * Created by imanfirmansyah on 3/2/17.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MyJobService extends JobService {
    Context ctx;

    @Override
    public boolean onStartJob(JobParameters params) {
        Intent intentStart = new Intent(this, UploadService.class);
        intentStart.putExtra(UploadService.ACTION, "startService");
        startService(intentStart);
        appendLog(new Date().toString()+" : "+"Job running");
        return false; // true if we're not done yet
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
            }catch (IOException e){
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

}