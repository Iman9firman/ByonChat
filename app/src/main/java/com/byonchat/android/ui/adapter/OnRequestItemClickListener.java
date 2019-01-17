package com.byonchat.android.ui.adapter;

import android.view.View;

import com.byonchat.android.data.model.File;

public interface OnRequestItemClickListener {
    void onItemClick(View view, int position, File item);
}
