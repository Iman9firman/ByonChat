package com.honda.android.ui.adapter;

import android.view.View;

import com.honda.android.data.model.File;

public interface OnRequestItemClickListener {
    void onItemClick(View view, int position, File item);
}
