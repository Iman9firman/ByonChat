package com.byonchat.android.http;

public interface JobCompleted {
    public void onTaskBegin();

    public void onTaskDone(String result);
}
