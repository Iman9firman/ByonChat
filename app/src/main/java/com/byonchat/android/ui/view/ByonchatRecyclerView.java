package com.byonchat.android.ui.view;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;

public class ByonchatRecyclerView extends RecyclerView {
    public ByonchatRecyclerView(Context context) {
        super(context);
    }

    public ByonchatRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ByonchatRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setUpAsList() {
        setHasFixedSize(true);
        setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void setUpAsBottomList() {
        setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        setLayoutManager(layoutManager);
    }

    public void setUpAsHorizontalList() {
        setHasFixedSize(true);
        setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    public void setUpAsGrid(int spanCount) {
        setHasFixedSize(true);
        setLayoutManager(new GridLayoutManager(getContext(), spanCount));
    }
}
