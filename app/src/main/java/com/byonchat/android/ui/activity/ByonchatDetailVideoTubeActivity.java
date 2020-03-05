package com.byonchat.android.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.VideoView;

import com.byonchat.android.R;
import com.byonchat.android.data.model.Video;

import java.io.File;

import static com.byonchat.android.utils.Utility.reportCatch;

public class ByonchatDetailVideoTubeActivity extends AppCompatActivity {

    public static final String EXTRA_VIDEO = "extra_videos";

    public static Intent generateIntent(Context context, Video video) {
        Intent intent = new Intent(context, ByonchatDetailVideoTubeActivity.class);
        intent.putExtra(EXTRA_VIDEO, video);
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

        try {
            resolveVideos(savedInstanceState);

            vidView = (VideoView) findViewById(R.id.myVideo);

            progressDialog = new ProgressDialog(ByonchatDetailVideoTubeActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            File file = new File(video.url);
            Uri uri = FileProvider.getUriForFile(ByonchatDetailVideoTubeActivity.this, getPackageName() + ".provider", file);
            vidView.setVideoURI(uri);
            vidView.setOnPreparedListener(mp -> {
                mp.setLooping(true);

                MediaController mediaController = new MediaController(ByonchatDetailVideoTubeActivity.this);
                mediaController.setAnchorView(vidView);
                vidView.setMediaController(mediaController);
                vidView.requestFocus();
                progressDialog.dismiss();
                vidView.start();
            });
        }catch (Exception e){
            reportCatch(e.getLocalizedMessage());
        }
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
