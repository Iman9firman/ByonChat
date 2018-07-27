package com.byonchat.android.local;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.byonchat.android.utils.AndroidUtil;

import java.util.concurrent.ScheduledFuture;

enum ActivityCallback implements Application.ActivityLifecycleCallbacks {
    INSTANCE;

    private static final long MAX_ACTIVITY_TRANSITION_TIME = 2000;

    private ScheduledFuture<?> activityTransition;
    private boolean foreground;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        stopActivityTransitionTimer();
    }

    @Override
    public void onActivityPaused(Activity activity) {
        startActivityTransitionTimer();
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    boolean isForeground() {
        return foreground;
    }

    private void startActivityTransitionTimer() {
//        activityTransition = AndroidUtil.runOnBackgroundThread(() -> foreground = false,
//                MAX_ACTIVITY_TRANSITION_TIME);

        activityTransition = AndroidUtil.runOnBackgroundThread(new Runnable() {
            @Override
            public void run() {
                foreground = false;
            }
        }, MAX_ACTIVITY_TRANSITION_TIME);
    }

    private void stopActivityTransitionTimer() {
        if (activityTransition != null) {
            activityTransition.cancel(true);
        }

        foreground = true;
    }
}