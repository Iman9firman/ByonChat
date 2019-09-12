package com.honda.android.provider;

/**
 * Created by Lukmanpryg on 10/3/2016.
 */
public class FilesURL {
    public int message_id;
    public String progress;
    public String status;
    public String image;
    public String cache;

    public FilesURL(int message_id,String progress,String status,String image,String cache) {
        this.message_id = message_id;
        this.progress = progress;
        this.status = status;
        this.image = image;
        this.cache = cache;
    }

    public FilesURL(String progress,String status,String image, String cache) {
        this.progress = progress;
        this.status = status;
        this.image = image;
        this.cache = cache;
    }

    public FilesURL(int message_id,String progress,String status) {
        this.message_id = message_id;
        this.progress = progress;
        this.status = status;
    }

    public FilesURL(int message_id,String progress,String status,String cache) {
        this.message_id = message_id;
        this.progress = progress;
        this.status = status;
        this.cache = cache;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCache() {
        return cache;
    }

    public void setCache(String cache) {
        this.cache = cache;
    }

}
