package com.honda.android.personalRoom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import com.honda.android.R;

/**
 * Created by byonc on 1/19/2017.
 */

public class PlayVideoActivity  extends Activity{
    private VideoView video;
    ProgressDialog dialog;
    private MediaController ctlr;
    String url;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.BLACK);
        }
        setContentView(R.layout.activity_play_video);

        video = (VideoView) findViewById(R.id.video);
        dialog = new ProgressDialog(PlayVideoActivity.this);
        dialog.setMessage("Buffering...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.show();

        url = getIntent().getStringExtra("url");

        try{
            MediaController mediacontroller = new MediaController(this);
            mediacontroller.setAnchorView(video);
            Uri uri = Uri.parse(url);
            video.setMediaController(mediacontroller);
            video.setVideoURI(uri);
        }catch (Exception e){
            e.printStackTrace();
        }

        video.requestFocus();
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                dialog.dismiss();
                video.start();
            }
        });

        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(dialog.isShowing()){
                    dialog.dismiss();
                }
                finish();
            }
        });
    }
}
