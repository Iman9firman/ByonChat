package com.byonchat.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import static com.byonchat.android.utils.Utility.reportCatch;

public class MediaPlayCatalogActivity  extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener{

    public static final String API_KEY = "AIzaSyDap-7B5rDb6ckg1q1vLdxEsCGUfWbcs7E";
    public static String VIDEO_ID = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.media_play_catalog);
        try {
            Intent i = getIntent();
            VIDEO_ID = i.getStringExtra("url");

            YouTubePlayerView youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtubeplayerview);
            youTubePlayerView.initialize(API_KEY, this);

        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult result) {
        try {
            Toast.makeText(getApplicationContext(),
                    "onInitializationFailure()",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                        boolean wasRestored) {
        try {
            if (!wasRestored) {
                player.setFullscreen(true);
                player.loadVideo(VIDEO_ID);

            }
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }


}