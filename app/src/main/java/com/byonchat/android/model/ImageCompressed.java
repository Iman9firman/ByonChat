package com.byonchat.android.model;

/**
 * Created by byonc on 5/2/2017.
 */

public class ImageCompressed {

    private long id;
    private String path;

    public ImageCompressed() {
    }

    public ImageCompressed(long id, String path) {
        this.id = id;
        this.path = path;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
