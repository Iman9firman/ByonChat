package com.honda.android.ui.adapter;

import com.honda.android.data.model.Video;

public interface ForwardItemClickListener {
    void onItemVideoClick(Video video);

    void onItemVideoLongClick(Video video);
}
