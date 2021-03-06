package com.byonchat.android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.signature.StringSignature;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.utils.MediaProcessingUtil;
import com.byonchat.android.utils.TouchImageView;
import com.byonchat.android.utils.Validations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FullScreenUpdateProfileActivity extends AppCompatActivity {
    public static final String PATH = "path";
    public static final String JAB_ID = "jabber_id";
    String path = "";

    TouchImageView imageView;
    ImageView btnShare;
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.full_screen_update_profile);


        imageView = (TouchImageView ) findViewById(R.id.touchImageView);
        btnShare = (ImageView ) findViewById(R.id.btnShare);

        path = getIntent().getStringExtra(PATH);

        final File yourFile = new File(path);

        if (yourFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(yourFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
        }else{
            finish();
        }

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND
                       );
                sharingIntent.setType("image/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM,
                        Uri.fromFile(new File(path)));
                startActivity(sharingIntent);
            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
