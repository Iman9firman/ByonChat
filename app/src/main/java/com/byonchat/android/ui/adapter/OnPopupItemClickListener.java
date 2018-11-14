package com.byonchat.android.ui.adapter;

import android.view.View;

import com.byonchat.android.data.model.Video;

public interface OnPopupItemClickListener {
    void onItemClick(View view, int position, Video video);
}
