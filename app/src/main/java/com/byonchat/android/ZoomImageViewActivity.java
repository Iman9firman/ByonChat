package com.byonchat.android;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.byonchat.android.list.utilLoadImage.ImageLoaderLarge;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.Message;
import com.byonchat.android.utils.TouchImageView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;

public class ZoomImageViewActivity extends ActionBarActivity {
    TouchImageView imageView;
    String uriImage;
    public static final String KEY_FILE = "FILE";
    public static final String FROM = "FROM";
    public static final String KEY_FILE_BASE_A = "A";
    public static final String KEY_FILE_BASE_B = "B";
    public static final String KEY_FILE_BASE_C = "C";
    public static final String KEY_FILE_BASE_D = "D";
    public static final String KEY_FILE_BASE_E = "E";
    // public ImageLoaderLarge imgBarcode;
    Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_zoom_image_view);
        imageView = (TouchImageView) findViewById(R.id.imageView);
        uriImage = getIntent().getStringExtra(KEY_FILE);
        if (uriImage.toString().equalsIgnoreCase(FROM)) {
            BotListDB db = BotListDB.getInstance(getApplicationContext());
            Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(getIntent().getStringExtra(KEY_FILE_BASE_A), getIntent().getStringExtra(KEY_FILE_BASE_B), getIntent().getStringExtra(KEY_FILE_BASE_C), getIntent().getStringExtra(KEY_FILE_BASE_D), getIntent().getStringExtra(KEY_FILE_BASE_E));
            if (cursorCild.getCount() > 0) {
                if (Message.isJSONValid(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)))) {
                    JSONObject jObject = null;
                    try {
                        jObject = new JSONObject(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (jObject != null) {
                        try {
                            String a = jObject.getString("a");
                            imageView.setImageBitmap(decodeBase64(a));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    imageView.setImageBitmap(decodeBase64(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT))));
                }
            }
        } else {
            Log.w("karoAq", uriImage);
            if (uriImage.startsWith("file:")) {
                Picasso.with(mContext).load(uriImage).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(imageView);
            } else if (uriImage.startsWith("http")) {
                Picasso.with(mContext).load(uriImage).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(imageView);
            } else {
                Picasso.with(mContext).load(new File(uriImage)).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(imageView);
            }
        }

        imageView.setMaxZoom(4);
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

}
