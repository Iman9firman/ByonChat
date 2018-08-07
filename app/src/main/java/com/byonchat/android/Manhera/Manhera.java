package com.byonchat.android.Manhera;

import android.annotation.SuppressLint;
import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

public class Manhera {
    @SuppressLint("StaticFieldLeak")
    private static Manhera instance;
    private RequestManager requestManager;

    public static void init(Context context) {
        if (instance == null) {
            synchronized (Manhera.class) {
                if (instance == null) {
                    instance = new Manhera(context);
                }
            }
        }
    }

    private Manhera(Context context) {
        requestManager = Glide.with(context.getApplicationContext());
    }

    public static Manhera getInstance() {
        if (instance == null) {
            synchronized (Manhera.class) {
                if (instance == null) {
                    throw new RuntimeException("Please init Manhera before. Call Manhera.init(context)");
                }
            }
        }
        return instance;
    }

    public RequestManager get() {
        return requestManager;
    }
}
