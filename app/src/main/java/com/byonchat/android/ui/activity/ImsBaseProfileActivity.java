package com.byonchat.android.ui.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.byonchat.android.R;
import com.byonchat.android.createMeme.FilteringImage;

public abstract class ImsBaseProfileActivity extends AppCompatActivity {

    protected static final String ARGS = "args";

    protected String mArgs;
    protected String percent;
    protected String color;
    protected String colorForeground;

    @NonNull
    protected ImageView vBgImage;

    @NonNull
    protected Toolbar vToolbar;

    @NonNull
    protected FrameLayout vBtnEdit;

    @NonNull
    protected CollapsingToolbarLayout vCollapsingToolbar;

    @NonNull
    protected AppBarLayout vAppbar;

    @NonNull
    protected RelativeLayout vToolbarBack;
    @NonNull
    protected TextView vToolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onSetStatusBarColor();
        setContentView(getResourceLayout());
        onLoadView();
        onViewReady(savedInstanceState);
    }

    protected void onSetStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
    }

    protected abstract int getResourceLayout();

    protected abstract void onLoadView();

    protected void applyChatConfig() {
        percent = "70";
        color = "#A2550C";
    }

    protected void onViewReady(Bundle savedInstanceState) {
        resolveChatRoom(savedInstanceState);

        applyChatConfig();
    }

    protected void resolveChatRoom(Bundle savedInstanceState) {
        mArgs = getIntent().getStringExtra(ARGS);
        if (mArgs == null && savedInstanceState != null) {
            mArgs = savedInstanceState.getString(ARGS);
        }

        if (mArgs == null) {
            /*// Enable here when it's not MainActivity
            finish();*/
            return;
        }
    }

    protected void resolveToolbar() {
        setSupportActionBar(vToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FilteringImage.SystemBarBackground(getWindow(), getResources().getColor(android.R.color.transparent));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            colorForeground = color.replace("#", "#" + percent);
            vBgImage.setForeground(new ColorDrawable(Color.parseColor(colorForeground)));
        }
        vToolbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        vToolbar.setTitleTextColor(getResources().getColor(R.color.white));

        AppBarLayout.LayoutParams p = (AppBarLayout.LayoutParams) vCollapsingToolbar.getLayoutParams();
        p.setScrollFlags(0);
        vCollapsingToolbar.setLayoutParams(p);

        vToolbarBack.setOnClickListener(v -> onBackPressed());
        vToolbarTitle.setText("Profile");
    }

    protected void resolveBackgroundImage() {
        Glide.with(getApplicationContext())
                .load(R.drawable.wallpaper)
                .centerCrop()
                .skipMemoryCache(true)
                .into(vBgImage);
    }

    protected void resolveEdit() {
        vBtnEdit.setOnClickListener(v -> Toast.makeText(this, "// Do something here", Toast.LENGTH_SHORT).show());
    }

    @NonNull
    protected abstract ImageView getBackgroundImage();

    @NonNull
    protected abstract Toolbar getToolbar();

    @NonNull
    protected abstract CollapsingToolbarLayout getCollapsingToolbar();

    @NonNull
    protected abstract AppBarLayout getAppBar();

    @NonNull
    protected abstract FrameLayout getEditButton();

    @NonNull
    protected abstract RelativeLayout getToolbarBack();

    @NonNull
    protected abstract TextView getToolbarTitle();

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARGS, mArgs);
    }
}
