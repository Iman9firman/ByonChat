package com.byonchat.android.helpers;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by byonc on 4/18/2017.
 */

public class Constants extends AppCompatActivity {

    public static final int REQUEST_CODE_CAPTURE = 2000;

    public static final int FETCH_STARTED = 2001;
    public static final int FETCH_COMPLETED = 2002;
    public static final int ERROR = 2003;

    public static final int MAX_LIMIT = 999;

    public static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 23;
    public static final int PERMISSION_REQUEST_CAMERA = 24;

    public static final String PREF_WRITE_EXTERNAL_STORAGE_REQUESTED = "writeExternalRequested";
    public static final String PREF_CAMERA_REQUESTED = "cameraRequested";

    protected static final String[] CAMERA_PERMISSION = {
            "android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE",
    };

    protected static final int RC_CAMERA_PERMISSION = 128;

    protected static final int REQ_CAMERA = 1201;
    protected static final int REQ_GALLERY = 1202;
    protected static final int SEND_PICTURE_CONFIRMATION_REQUEST = 1203;
    protected static final int SEND_PICTURE_SINGLE_CONFIRMATION_REQUEST = 1204;
}

