package com.byonchat.android.ui.adapter;

import com.byonchat.android.data.model.Video;

public interface ForwardItemClickListener {
    void onItemVideoClick(Video video);

    void onItemVideoLongClick(Video video);
}
