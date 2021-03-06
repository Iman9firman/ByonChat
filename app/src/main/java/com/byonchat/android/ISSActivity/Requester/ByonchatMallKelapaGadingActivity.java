package com.byonchat.android.ISSActivity.Requester;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.byonchat.android.R;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.ui.activity.ImsListHistoryChatActivity;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.mindorks.placeholderview.ExpandablePlaceHolderView;

public class ByonchatMallKelapaGadingActivity extends ByonchatBaseMallKelapaGadingActivity {
    Runnable runnable;
    Handler handler = new Handler();

    public static Intent generateIntent(Context context, String color, String colorText, String it, String ro) {
        Intent intent = new Intent(context, ImsListHistoryChatActivity.class);
        intent.putExtra(Constants.EXTRA_COLOR, color);
        intent.putExtra(Constants.EXTRA_COLORTEXT, colorText);
        intent.putExtra(Constants.EXTRA_ITEM, it);
        intent.putExtra(Constants.EXTRA_ROOM, ro);
        return intent;
    }

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_mall_kelapa_gading;
    }

    @Override
    protected void onLoadView() {
        vAppBar = getAppbar();
        vToolbar = getToolbar();
        vToolbarBack = getToolbarBack();
        vToolbarTitle = getToolbarTitle();
        vImgToolbarBack = getImgToolbarBack();
        vSearchView = getMaterialSearchView();
        vList = getListView();
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        super.onViewReady(savedInstanceState);

        resolveToolbar();
        //resolveListHistory();

        runnable = () -> {
            Log.w("selsai", "11");
            resolveListHistory();
            handler.postDelayed(runnable, 3 * 60 * 1000);
        };
        handler.post(runnable);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_search:
                item.setVisible(false);
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @NonNull
    @Override
    protected AppBarLayout getAppbar() {
        return (AppBarLayout) findViewById(R.id.appbar);
    }

    @NonNull
    @Override
    protected Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }

    @NonNull
    @Override
    protected ExpandablePlaceHolderView getListView() {
        return (ExpandablePlaceHolderView) findViewById(R.id.list);
    }

    @NonNull
    @Override
    protected RelativeLayout getToolbarBack() {
        return (RelativeLayout) findViewById(R.id.toolbar_back);
    }

    @NonNull
    @Override
    protected MaterialSearchView getMaterialSearchView() {
        return (MaterialSearchView) findViewById(R.id.search_view_main);
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
}

