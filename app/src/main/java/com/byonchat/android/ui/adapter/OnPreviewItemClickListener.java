package com.byonchat.android.ui.adapter;

import android.view.View;

import com.byonchat.android.data.model.File;

public interface OnPreviewItemClickListener {
    void onItemClick(View view, String idCheck, File item, String type, String id_task_detail);
}