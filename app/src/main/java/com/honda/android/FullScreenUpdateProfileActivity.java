package com.honda.android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.honda.android.utils.TouchImageView;

import java.io.File;

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
