package com.byonchat.android.provider;

import android.graphics.Bitmap;

/**
 * Created by Iman Firmansyah on 1/9/2015.
 */
public class Files {
    public int message_id;
    public String progress;
    public String status;
    public Bitmap image;

    public Files(int message_id,String progress,String status,Bitmap image) {
        this.message_id = message_id;
        this.progress = progress;
        this.status = status;
        this.image = image;
    }

    public Files(String progress,String status,Bitmap image) {
        this.progress = progress;
        this.status = status;
        this.image = image;
    }

    public Files(int message_id,String progress,String status) {
        this.message_id = message_id;
        this.progress = progress;
        this.status = status;
    }

    public int getMessage_id() {
        return message_id;
    }

    public void setMessage_id(int message_id) {
        this.message_id = message_id;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }


}
