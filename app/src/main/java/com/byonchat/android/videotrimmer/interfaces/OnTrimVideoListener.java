package com.byonchat.android.videotrimmer.interfaces;

import android.net.Uri;

/**
 * Created by byonc on 5/9/2017.
 */

public interface OnTrimVideoListener {
    void onTrimStarted();

//    void getResult(final Uri uri, final String textMessage, final String path);
//    void getResult(final Uri uri, final String textMessage);
    void getResult(final String json, final String textMessage);

    void cancelAction();

    void onError(final String message);
}
