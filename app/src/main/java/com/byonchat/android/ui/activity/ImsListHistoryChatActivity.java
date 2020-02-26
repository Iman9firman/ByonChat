package com.byonchat.android.ui.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.communication.NotificationReceiver;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.ui.view.ByonchatRecyclerView;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

public class ImsListHistoryChatActivity extends ImsBaseListHistoryChatActivity {

    public static Intent generateIntent(Context context, String color, String colorText) {
        Intent intent = new Intent(context, ImsListHistoryChatActivity.class);
        intent.putExtra(Constants.EXTRA_COLOR, color);
        intent.putExtra(Constants.EXTRA_COLORTEXT, colorText);
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
        vFrameChatLists = getFrameChatLists();
        vFrameMessageLists = getFrameMessageLists();
        vImgToolbarBack = getImgToolbarBack();
        vSearchView = getMaterialSearchView();
        vToolbarBack = getToolbarBack();
        vToolbarTitle = getToolbarTitle();
        vListHistory = getListHistory();
        vListHistoryFind = getListHistoryFind();
        vSearchEdt = getSearchView();
        vFrameSearch = getFrameSearch();
        vBtnCreateMessage = getFloatingButtonCreateMsg();
        vScrollView = getScrollView();
        vNestedScroll = getNestedScrollView();
        vFrameBottom = getBottomFrame();
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        super.onViewReady(savedInstanceState);

        resolveToolbar();
        resolveListHistory();
        resolveListHistoryFind();
        resolveSearchView();
        resolveMaterialSearchView();
        resolveCreateMessage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*getMenuInflater().inflate(R.menu.ims_list_history_menu, menu);
        resolveSearchMenu(menu);*/
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
        vSearchView.closeSearch();
        resolveOriginView(false);
        resolveChatHistory();
        resolveChatHistorySearch();
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
    protected FrameLayout getFrameChatLists() {
        return (FrameLayout) findViewById(R.id.frame_chat_lists);
    }

    @NonNull
    @Override
    protected FrameLayout getFrameMessageLists() {
        return (FrameLayout) findViewById(R.id.frame_message_lists);
    }

    @NonNull
    @Override
    protected ByonchatRecyclerView getListHistory() {
        return (ByonchatRecyclerView) findViewById(R.id.list_history);
    }

    @NonNull
    @Override
    protected ByonchatRecyclerView getListHistoryFind() {
        return (ByonchatRecyclerView) findViewById(R.id.list_history_find);
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
    protected FloatingActionButton getFloatingButtonCreateMsg() {
        return (FloatingActionButton) findViewById(R.id.button_create_message);
    }

    @NonNull
    @Override
    protected ScrollView getScrollView() {
        return (ScrollView) findViewById(R.id.scrollview);
    }

    @NonNull
    @Override
    protected NestedScrollView getNestedScrollView() {
        return (NestedScrollView) findViewById(R.id.nested_scroll);
    }


    @NonNull
    @Override
    protected RelativeLayout getBottomFrame() {
        return (RelativeLayout) findViewById(R.id.frame_bottom);
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
