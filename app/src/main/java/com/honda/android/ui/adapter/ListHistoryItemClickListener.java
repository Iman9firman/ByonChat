package com.honda.android.ui.adapter;

import android.view.View;

public interface ListHistoryItemClickListener {
    void onItemListClick(View view, int position);

    void onItemListLongClick(View view, int position);
}
