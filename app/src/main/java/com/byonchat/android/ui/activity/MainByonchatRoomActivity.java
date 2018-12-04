package com.byonchat.android.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.byonchat.android.AdvRecy.ItemMain;
import com.byonchat.android.R;

public class MainByonchatRoomActivity extends MainByonchatRoomBaseActivity {

    public static Intent generateIntent(Context context, ItemMain item) {
        Intent intent = new Intent(context, MainByonchatRoomActivity.class);
        intent.putExtra(EXTRA_ITEM, item);
        return intent;
    }

    @Override
    protected int getResourceLayout() {
        return R.layout.main_byonchat_room_activity;
    }

    @Override
    protected void onLoadView() {
        vToolbar = getToolbar();
        vToolbarBack = getToolbarBack();
        vImgToolbarBack = getImgToolbarBack();
        vToolbarTitle = getToolbarTitle();
        vAppbar = getAppBar();
        vContainerFragment = getFrameFragment();
        vFloatingButton = getFloatingButton();
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        super.onViewReady(savedInstanceState);

        resolveToolbar();
        resolveFragment();
        resolveFloatingButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    protected Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }

    @NonNull
    @Override
    protected AppBarLayout getAppBar() {
        return (AppBarLayout) findViewById(R.id.app_bar);
    }

    @NonNull
    @Override
    protected RelativeLayout getToolbarBack() {
        return (RelativeLayout) findViewById(R.id.toolbar_back);
    }

    @NonNull
    @Override
    protected ImageView getImgToolbarBack() {
        return (ImageView) findViewById(R.id.img_toolbar_back);
    }

    @NonNull
    @Override
    protected TextView getToolbarTitle() {
        return (TextView) findViewById(R.id.toolbar_title);
    }

    @NonNull
    @Override
    protected FrameLayout getFrameFragment() {
        return (FrameLayout) findViewById(R.id.container_open_fragment);
    }

    @NonNull
    @Override
    protected FloatingActionButton getFloatingButton() {
        return (FloatingActionButton) findViewById(R.id.fabAction);
    }
}
