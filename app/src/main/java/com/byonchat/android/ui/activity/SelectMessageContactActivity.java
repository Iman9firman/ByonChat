package com.byonchat.android.ui.activity;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.list.IconItem;
import com.byonchat.android.utils.RefreshContactService;

public class SelectMessageContactActivity extends SelectBaseMessageContactActivity {

    public static Intent generateIntent(Context context, String color, String colorText) {
        Intent intent = new Intent(context, SelectMessageContactActivity.class);
        intent.putExtra(Constants.EXTRA_COLOR, color);
        intent.putExtra(Constants.EXTRA_COLORTEXT, colorText);
        return intent;
    }

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_select_message_contact;
    }

    @Override
    protected void onLoadView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        backBtn = (ImageView) findViewById(R.id.backImage);
        listContact = (RecyclerView) findViewById(R.id.list_contact);
        reload = (ImageView) findViewById(R.id.reload_btn);
        toolbarTitle = (TextView) findViewById(R.id.Title);
        toolbarSub = (TextView) findViewById(R.id.SubTitle);
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        super.onViewReady(savedInstanceState);
        resolveToolbar();
        resolveView();
        resolveContactDB();
        resolveList();
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter f = new IntentFilter(
                MessengerConnectionService.ACTION_STATUS_CHANGED);
        f.addAction(RefreshContactService.ACTION_CONTACT_REFRESHED);
        f.setPriority(1);
        registerReceiver(broadcastHandler, f);
    }

    @Override
    public void onRowClickContact(View view, String jabberID) {
        IconItem chat = new IconItem(jabberID, "pershop", "", "08.00", null);
        openConversation(view, chat);
    }



    @Override
    protected void onPause() {
        unregisterReceiver(broadcastHandler);
        super.onPause();
    }

    @Override
    public void counting(int yzou) {

    }

    protected void resolveChatRoom(Bundle savedInstanceState) {
        mColor = getIntent().getStringExtra(Constants.EXTRA_COLOR);
        mColorText = getIntent().getStringExtra(Constants.EXTRA_COLORTEXT);
        if (mColor == null && mColorText == null && savedInstanceState != null) {
            mColor = savedInstanceState.getString(Constants.EXTRA_COLOR);
            mColorText = savedInstanceState.getString(Constants.EXTRA_COLORTEXT);
        }

        if (mColor == null && mColorText == null) {
            finish();
            return;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.EXTRA_COLOR, mColor);
        outState.putString(Constants.EXTRA_COLORTEXT, mColorText);
    }
}
