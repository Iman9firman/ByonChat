package com.byonchat.android.ui.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.communication.NotificationReceiver;
import com.byonchat.android.ui.view.ByonchatRecyclerView;

public class ImsListHistoryChatActivity extends ImsBaseListHistoryChatActivity {

    public static Intent generateIntent(Context context, String args) {
        Intent intent = new Intent(context, ImsListHistoryChatActivity.class);
        intent.putExtra(ARGS, args);
        return intent;
    }

    @Override
    protected int getResourceLayout() {
        return R.layout.ims_list_history;
    }

    @Override
    protected void onLoadView() {
        vAppBar = getAppbar();
        vToolbar = getToolbar();
        vToolbarBack = getToolbarBack();
        vToolbarTitle = getToolbarTitle();
        vListHistory = getListHistory();
        vSearchEdt = getSearchView();
        vFrameSearch = getFrameSearch();
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        super.onViewReady(savedInstanceState);

        resolveToolbar();
        resolveListHistory();
        resolveSearchView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ims_list_history_menu, menu);
        resolveSearchMenu(menu);
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

    @Override
    public void onResume() {
        super.onResume();
        listChatHistory();
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                .cancel(NotificationReceiver.NOTIFY_ID);

        IntentFilter f = new IntentFilter(
                MessengerConnectionService.ACTION_MESSAGE_RECEIVED);
        f.addAction(MessengerConnectionService.ACTION_MESSAGE_DELIVERED);
        f.addAction(MessengerConnectionService.ACTION_MESSAGE_SENT);
        f.addAction(MessengerConnectionService.ACTION_MESSAGE_FAILED);
        f.addAction(MessengerConnectionService.ACTION_REFRESH_CHAT_HISTORY);
        f.addAction(MessengerConnectionService.ACTION_STATUS_CHANGED_CONTACT);
        f.setPriority(2);
        registerReceiver(broadcastHandler, f);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(broadcastHandler);

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
    protected ByonchatRecyclerView getListHistory() {
        return (ByonchatRecyclerView) findViewById(R.id.list_history);
    }

    @NonNull
    @Override
    protected SearchView getSearchView() {
        return (SearchView) findViewById(R.id.edittext_search);
    }

    @NonNull
    @Override
    protected LinearLayout getFrameSearch() {
        return (LinearLayout) findViewById(R.id.frame_search);
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
