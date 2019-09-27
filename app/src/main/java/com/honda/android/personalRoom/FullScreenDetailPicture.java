package com.honda.android.personalRoom;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;

import com.honda.android.R;
import com.honda.android.utils.TouchImageView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by Lukmanpryg on 12/30/2016.
 */

public class FullScreenDetailPicture extends AppCompatActivity{
    TouchImageView imageView;
    String photo;
    String jabberId;
    public static final String PHOTO = "photo";
    public static final String JABBER_ID = "id";
    String iconsStoragePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        overridePendingTransition(R.anim.do_not_move, R.anim.do_not_move);
        if (android.os.Build.VERSION.SDK_INT>=20){
            setTheme(R.style.HeaderTransparent);
        }
        setContentView(R.layout.fullscreen_detail_picture);

        imageView = (TouchImageView) findViewById(R.id.touchImageView);

        photo = getIntent().getStringExtra(PHOTO);
        jabberId = getIntent().getStringExtra(JABBER_ID);

        imageView.setBackgroundColor(Color.BLACK);
        Picasso.with(this).load(photo).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(imageView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
