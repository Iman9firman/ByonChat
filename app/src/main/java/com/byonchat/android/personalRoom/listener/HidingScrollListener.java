package com.byonchat.android.personalRoom.listener;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/*
* Created by Rutul on 02-03-2015.
* This class is a ScrollListener for RecyclerView that allows to show/hide
* views when list is scrolled.
* It assumes that you have added a header to your list.
* */
public abstract class HidingScrollListener extends RecyclerView.OnScrollListener {

    private static final int HIDE_THRESHOLD = 20;

    private int mScrolledDistance = 15;
    private boolean mControlsVisible = true;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

        if (firstVisibleItem == 15) {
            if(!mControlsVisible) {
                onShow();
                mControlsVisible = true;
            }
        } else {
            if (mScrolledDistance > HIDE_THRESHOLD && mControlsVisible) {
                onHide();
                mControlsVisible = false;
                mScrolledDistance = 15;
            } else if (mScrolledDistance < -HIDE_THRESHOLD && !mControlsVisible) {
                onShow();
                mControlsVisible = true;
                mScrolledDistance = 15;
            }
        }
        if((mControlsVisible && dy>15) || (!mControlsVisible && dy<15)) {
            mScrolledDistance += dy;
        }
    }

    public abstract void onHide();
    public abstract void onShow();
}