package com.byonchat.android.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.byonchat.android.R;

import static com.byonchat.android.utils.Utility.reportCatch;

public class ImsProfileActivity extends ImsBaseProfileActivity {

    public static Intent generateIntent(Context context, String args) {
        Intent intent = new Intent(context, ImsProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ARGS, args);
        return intent;
    }

    @Override
    protected int getResourceLayout() {
        return R.layout.ims_profile_activity;
    }

    @Override
    protected void onLoadView() {
        vBgImage = getBackgroundImage();
        vToolbar = getToolbar();
        vToolbarBack = getToolbarBack();
        vToolbarTitle = getToolbarTitle();
        vCollapsingToolbar = getCollapsingToolbar();
        vAppbar = getAppBar();
        vBtnEdit = getEditButton();
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        super.onViewReady(savedInstanceState);

        try {
            resolveToolbar();
            resolveBackgroundImage();
            resolveEdit();
        }catch (Exception e){
            reportCatch(e.getLocalizedMessage());
        }
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
    protected ImageView getBackgroundImage() {
        return (ImageView) findViewById(R.id.background);
    }

    @NonNull
    @Override
    protected Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }


    @NonNull
    @Override
    protected CollapsingToolbarLayout getCollapsingToolbar() {
        return (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
    }

    @NonNull
    @Override
    protected AppBarLayout getAppBar() {
        return (AppBarLayout) findViewById(R.id.app_bar);
    }

    @NonNull
    @Override
    protected FrameLayout getEditButton() {
        return (FrameLayout) findViewById(R.id.frame_edit);
    }

    @NonNull
    @Override
    protected RelativeLayout getToolbarBack() {
        return (RelativeLayout) findViewById(R.id.toolbar_back);
    }

    @NonNull
    @Override
    protected TextView getToolbarTitle() {
        return (TextView) findViewById(R.id.toolbar_title);
    }
}
