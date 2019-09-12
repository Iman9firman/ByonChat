package com.honda.android.list;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.File;

public class VideoFragment extends Fragment {
    public static final String KEY_BUNDLE_FILENAME = "com.honda.android.list.VideoFragment.FILENAME";
    public static final String KEY_BUNDLE_START_VIDEO = "com.honda.android.list.VideoFragment.START_VIDEO";
    boolean startVideo;
    private File videoClip;
    private VideoView video;
    private MediaController ctlr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoClip = new File((String) getArguments().get(KEY_BUNDLE_FILENAME));
        startVideo = ((Boolean) getArguments().get(KEY_BUNDLE_START_VIDEO))
                .booleanValue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        video = new VideoView(getActivity());
        video.setVideoPath(videoClip.getAbsolutePath());
        ctlr = new MediaController(getActivity());
        ctlr.setMediaPlayer(video);
        video.setMediaController(ctlr);
        video.requestFocus();
        if (startVideo) {
            video.start();
        }
        return video;
    }
}
