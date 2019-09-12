package com.honda.android.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.VideoView;

import com.honda.android.R;
import com.honda.android.data.model.Video;

public class ByonchatStreamingVideoTubeActivity extends AppCompatActivity {
    public static String EXTRA_VIDEO = "extra_videos";
    public static String EXTRA_COLOR = "color";
    public static String EXTRA_COLOR_TEXT = "colorText";

    public static Intent generateIntent(Context context, Video video, String color, String colorText) {
        Intent intent = new Intent(context, ByonchatStreamingVideoTubeActivity.class);
        intent.putExtra(EXTRA_VIDEO, video);
        intent.putExtra(EXTRA_COLOR, color);
        intent.putExtra(EXTRA_COLOR_TEXT, colorText);
        return intent;
    }

    protected Video video;
    protected VideoView vidView;
    protected ProgressDialog progressDialog;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_VIDEO, video);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b_videotube_streaming_activity);

        resolveVideos(savedInstanceState);

        vidView = (VideoView) findViewById(R.id.myVideo);

        progressDialog = new ProgressDialog(ByonchatStreamingVideoTubeActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Uri vidUri = Uri.parse(video.url);
        vidView.setVideoURI(vidUri);
        vidView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);

                MediaController mediaController = new MediaController(ByonchatStreamingVideoTubeActivity.this);
                mediaController.setAnchorView(vidView);
                vidView.setMediaController(mediaController);
                vidView.requestFocus();
                progressDialog.dismiss();
                vidView.start();
            }
        });
    }

    protected void resolveVideos(Bundle savedInstanceState) {
        video = getIntent().getParcelableExtra(EXTRA_VIDEO);
        if (video == null && savedInstanceState != null) {
            video = savedInstanceState.getParcelable(EXTRA_VIDEO);
        }

        if (video == null) {
            finish();
            return;
        }
    }
}
