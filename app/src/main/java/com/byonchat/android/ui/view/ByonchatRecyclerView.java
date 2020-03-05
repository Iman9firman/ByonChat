package com.byonchat.android.ui.view;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;

import static com.byonchat.android.utils.Utility.reportCatch;

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
        try {
            setHasFixedSize(true);
            setLayoutManager(new LinearLayoutManager(getContext()));
        }catch (Exception e){
            reportCatch(e.getLocalizedMessage());
        }
    }

    public void setUpAsBottomList() {
        try {
            setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setReverseLayout(true);
            setLayoutManager(layoutManager);
        }catch (Exception e){
            reportCatch(e.getLocalizedMessage());
        }
    }

    public void setUpAsHorizontalList() {
        try {
            setHasFixedSize(true);
            setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        }catch (Exception e){
            reportCatch(e.getLocalizedMessage());
        }
    }

    public void setUpAsGrid(int spanCount) {
        try {
            setHasFixedSize(true);
            setLayoutManager(new GridLayoutManager(getContext(), spanCount));
        }catch (Exception e){
            reportCatch(e.getLocalizedMessage());
        }
    }
}
