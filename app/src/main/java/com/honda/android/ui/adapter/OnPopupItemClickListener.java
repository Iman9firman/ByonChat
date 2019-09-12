package com.honda.android.ui.adapter;

import android.view.View;

import com.honda.android.data.model.Video;

public interface OnPopupItemClickListener {
    void onItemClick(View view, int position, Video video);
}
