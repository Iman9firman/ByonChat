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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.byonchat.android.AdvRecy.ItemMain;
import com.byonchat.android.R;
import com.byonchat.android.widget.ToolbarWithIndicator;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

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
    protected void onLoadConfig(Bundle savedInstanceState) {
        resolveChatRoom(savedInstanceState);

        applyConfig();
    }

    @Override
    protected void onLoadToolbar() {

    }

    @Override
    protected void onLoadView() {
        vToolbar = getToolbar();
        vToolbarBack = getToolbarBack();
        vImgToolbarBack = getImgToolbarBack();
        vToolbarTitle = getToolbarTitle();
        vAppbar = getAppBar();
        vSearchView = getMaterialSearchView();
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
    protected ToolbarWithIndicator getToolbar() {
        return (ToolbarWithIndicator) findViewById(R.id.toolbar);
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

    @NonNull
    @Override
    protected MaterialSearchView getMaterialSearchView() {
        return (MaterialSearchView) findViewById(R.id.search_view_main);
    }

    @NonNull
    @Override
    protected LinearLayout getFrameSearchAppBar() {
        return (LinearLayout) findViewById(R.id.frame_search_appbar);
    }

    @NonNull
    @Override
    protected Toolbar getSearchToolbar() {
        return (Toolbar) findViewById(R.id.search_toolbar);
    }

    @NonNull
    @Override
    protected EditText getSearchForm() {
        return (EditText) findViewById(R.id.search_edittext);
    }
}
