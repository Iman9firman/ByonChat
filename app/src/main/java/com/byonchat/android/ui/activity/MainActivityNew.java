package com.byonchat.android.ui.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.byonchat.android.AdvRecy.DraggableGridExampleAdapter;
import com.byonchat.android.AdvRecy.ItemMain;
import com.byonchat.android.ByonChatMainRoomActivity;
import com.byonchat.android.ConversationActivity;
import com.byonchat.android.FinalizingActivity;
import com.byonchat.android.LoadContactScreen;
import com.byonchat.android.LoadingGetTabRoomActivity;
import com.byonchat.android.MainActivity;
import com.byonchat.android.MainSettingActivity;
import com.byonchat.android.NewSearchRoomActivity;
import com.byonchat.android.R;
import com.byonchat.android.RegistrationActivity;
import com.byonchat.android.UpdateProfileActivity;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.communication.MyBroadcastReceiver;
import com.byonchat.android.communication.MyJobService;
import com.byonchat.android.communication.NotificationReceiver;
import com.byonchat.android.communication.WhatsAppJobService;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.list.BotAdapter;
import com.byonchat.android.local.Byonchat;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.ContactBot;
import com.byonchat.android.provider.Interval;
import com.byonchat.android.provider.IntervalDB;
import com.byonchat.android.provider.Skin;
import com.byonchat.android.ui.adapter.OnItemClickListener;
import com.byonchat.android.ui.adapter.OnLongItemClickListener;
import com.byonchat.android.ui.view.ByonchatRecyclerView;
import com.byonchat.android.utils.DialogUtil;
import com.byonchat.android.utils.PermanentLoggerUtil;
import com.byonchat.android.utils.UploadService;
import com.byonchat.android.utils.Utility;
import com.byonchat.android.widget.BadgeView;
import com.eightbitlab.supportrenderscriptblur.SupportRenderScriptBlur;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.internal.Util;

public class MainActivityNew extends MainBaseActivityNew {

    @Override
    protected int getResourceLayout() {
        return R.layout.main_activity_new;
    }

    protected void onSetupRoom() {
        if (getIntent().getExtras() != null) {
            if (getIntent().hasExtra(Constants.EXTRA_ROOM)) {

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(Constants.EXTRA_TAB_MOVEMENT, getIntent().getStringExtra(Constants.EXTRA_TAB_MOVEMENT));
                editor.apply();

                ContactBot item = new ContactBot();
                try {
                    username = getIntent().getStringExtra(ConversationActivity.KEY_JABBER_ID);
                    targetURL = getIntent().getStringExtra(ConversationActivity.KEY_TITLE);
                    JSONArray jsonArray = new JSONArray(getIntent().getStringExtra(Constants.EXTRA_ROOM));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        item.id = jsonObject.getString("id");
                        item.desc = jsonObject.getString("desc");
                        item.link = jsonObject.getString("link");
                        item.name = jsonObject.getString("name");
                        item.realname = jsonObject.getString("realname");
                        item.targetUrl = jsonObject.getString("targetUrl");
                        item.type = jsonObject.getString("type");
                    }

                    Byonchat.getRoomsDB().open();
                    Byonchat.getRoomsDB().updateActiveRoomsManual(item);
                    Byonchat.getRoomsDB().close();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onLoadView() {
        tb = findViewById(R.id.main_toolbar);
        fab_logo = findViewById(R.id.fab_logo_main);
        collapsingToolbarLayout = findViewById(R.id.main_collapsing);
        appBarLayout = findViewById(R.id.main_appbar);
        card_search_main = findViewById(R.id.card_search_main);
        vBtnAddRooms = findViewById(R.id.button_add_rooms);
        input_search_main = findViewById(R.id.input_search_main);
        but_search_main = findViewById(R.id.but_search_main);
        backgroundImage = findViewById(R.id.main_backdrop);
        searchView = findViewById(R.id.search_view_main);
        backdropBlur = findViewById(R.id.backdropblur);
        vImgBlur = findViewById(R.id.bg_blur);
        vSwipeRefresh = findViewById(R.id.swipe_refresh);

        root_view = findViewById(R.id.main_group);
        drawerLayout = findViewById(R.id.drawer_main);
        navigationView = findViewById(R.id.nav_view);
        vListRooms = findViewById(R.id.list_view);
        recyclerView = findViewById(R.id.recycler_main);
        vBtnToolbarSearch = findViewById(R.id.ic_toolbar_title);
        vToolbarSearchText = findViewById(R.id.text_toolbar_title);

        card_menu_main = findViewById(R.id.card_nav_main);
        vBlurView = findViewById(R.id.blurView);
        vBlurTopBackground = findViewById(R.id.blur_top_background);
        fab_menu_1 = findViewById(R.id.fab_menu_1_main);
        fab_menu_2 = findViewById(R.id.fab_menu_2_main);
//        fab_menu_3 = findViewById(R.id.fab_menu_3_main);

        vFrameWarning = findViewById(R.id.frame_warning);
        vTxtStatusWarning = findViewById(R.id.text_status);

        View headerview = navigationView.getHeaderView(0);
        vNavLogo = headerview.findViewById(R.id.nav_logo);
        vNavTitle = headerview.findViewById(R.id.nav_title);
        vBtnOpenRooms = headerview.findViewById(R.id.nav_button_open_room);

        vConstraintFiveOne = findViewById(R.id.constraint_item_grid_five_one);
        vConstraintFiveTwo = findViewById(R.id.constraint_item_grid_five_four);
        vConstraintFiveThree = findViewById(R.id.constraint_item_grid_five_five);

//        LinearLayout.LayoutParams constraintFive = new LinearLayout.LayoutParams(vConstraintFiveOne.getWidth(),
//                vConstraintFiveOne.getHeight());
//        vConstraintFiveTwo.setLayoutParams(constraintFive);
//        vConstraintFiveThree.setLayoutParams(constraintFive);

        vFrameTabOne = findViewById(R.id.frame_tab_one);
        vFrameTabTwo = findViewById(R.id.frame_tab_two);
//        vFrameTabThree = findViewById(R.id.frame_tab_three);
        vFrameTabFour = findViewById(R.id.frame_tab_four);
//        vFrameTabFive = findViewById(R.id.frame_tab_five);
//        vFrameTabSix = findViewById(R.id.frame_tab_six);
//        vFrameTabSeven = findViewById(R.id.frame_tab_seven);
//        vFrameTabEight = findViewById(R.id.frame_tab_eight);
        vFrameTabNine = findViewById(R.id.frame_tab_nine);

        vFrameGridNineThree = findViewById(R.id.frame_grid_nine_three);
        vFrameGridNineSix = findViewById(R.id.frame_grid_nine_six);
        vFrameGridNineNine = findViewById(R.id.frame_grid_nine_nine);

        vFrameGridNineNineOne = findViewById(R.id.frame_grid_nine_nine_one);
        vFrameGridNineNineTwo = findViewById(R.id.frame_grid_nine_nine_two);
        vFrameGridNineNineThree = findViewById(R.id.frame_grid_nine_nine_three);
        vFrameGridNineNineFour = findViewById(R.id.frame_grid_nine_nine_four);
        vFrameGridNineNineFive = findViewById(R.id.frame_grid_nine_nine_five);
        vFrameGridNineNineSix = findViewById(R.id.frame_grid_nine_nine_six);
        vFrameGridNineNineSeven = findViewById(R.id.frame_grid_nine_nine_seven);
        vFrameGridNineNineEight = findViewById(R.id.frame_grid_nine_nine_eight);
        vFrameGridNineNineNine = findViewById(R.id.frame_grid_nine_nine_nine);

        vFrameClickTabOne = findViewById(R.id.frame_click_tab_one);

        vFrameClickTabTwoOne = findViewById(R.id.frame_click_tab_two_one);
        vFrameClickTabTwoTwo = findViewById(R.id.frame_click_tab_two_two);

        vFrameClickTabFourOne = findViewById(R.id.frame_click_tab_four_one);
        vFrameClickTabFourTwo = findViewById(R.id.frame_click_tab_four_two);
        vFrameClickTabFourThree = findViewById(R.id.frame_click_tab_four_three);
        vFrameClickTabFourFour = findViewById(R.id.frame_click_tab_four_four);

        vFrameClickTabNineOne = findViewById(R.id.frame_click_tab_nine_one);
        vFrameClickTabNineTwo = findViewById(R.id.frame_click_tab_nine_two);
        vFrameClickTabNineThree = findViewById(R.id.frame_click_tab_nine_three);
        vFrameClickTabNineFour = findViewById(R.id.frame_click_tab_nine_four);
        vFrameClickTabNineFive = findViewById(R.id.frame_click_tab_nine_five);
        vFrameClickTabNineSix = findViewById(R.id.frame_click_tab_nine_six);
        vFrameClickTabNineSeven = findViewById(R.id.frame_click_tab_nine_seven);
        vFrameClickTabNineEight = findViewById(R.id.frame_click_tab_nine_eight);
        vFrameClickTabNineNine = findViewById(R.id.frame_click_tab_nine_nine);

        vLogoItemGridOne = findViewById(R.id.logo_item_grid_one);
        vTitleItemGridOne = findViewById(R.id.title_item_grid_one);

        vLogoItemGridTwoOne = findViewById(R.id.logo_item_grid_two_one);
        vTitleItemGridTwoOne = findViewById(R.id.title_item_grid_two_one);
        vLogoItemGridTwoTwo = findViewById(R.id.logo_item_grid_two_two);
        vTitleItemGridTwoTwo = findViewById(R.id.title_item_grid_two_two);

        /*vLogoItemGridThreeOne = findViewById(R.id.logo_item_grid_three_one);
        vTitleItemGridThreeOne = findViewById(R.id.title_item_grid_three_one);
        vLogoItemGridThreeTwo = findViewById(R.id.logo_item_grid_three_two);
        vTitleItemGridThreeTwo = findViewById(R.id.title_item_grid_three_two);
        vLogoItemGridThreeThree = findViewById(R.id.logo_item_grid_three_three);
        vTitleItemGridThreeThree = findViewById(R.id.title_item_grid_three_three);*/

        vLogoItemGridFourOne = findViewById(R.id.logo_item_grid_four_one);
        vTitleItemGridFourOne = findViewById(R.id.title_item_grid_four_one);
        vLogoItemGridFourTwo = findViewById(R.id.logo_item_grid_four_two);
        vTitleItemGridFourTwo = findViewById(R.id.title_item_grid_four_two);
        vLogoItemGridFourThree = findViewById(R.id.logo_item_grid_four_three);
        vTitleItemGridFourThree = findViewById(R.id.title_item_grid_four_three);
        vLogoItemGridFourFour = findViewById(R.id.logo_item_grid_four_four);
        vTitleItemGridFourFour = findViewById(R.id.title_item_grid_four_four);

        /*vLogoItemGridFiveOne = findViewById(R.id.logo_item_grid_five_one);
        vTitleItemGridFiveOne = findViewById(R.id.title_item_grid_four_one);
        vLogoItemGridFiveTwo = findViewById(R.id.logo_item_grid_five_two);
        vTitleItemGridFiveTwo = findViewById(R.id.title_item_grid_four_two);
        vLogoItemGridFiveThree = findViewById(R.id.logo_item_grid_five_three);
        vTitleItemGridFiveThree = findViewById(R.id.title_item_grid_four_three);
        vLogoItemGridFiveFour = findViewById(R.id.logo_item_grid_five_four);
        vTitleItemGridFiveFour = findViewById(R.id.title_item_grid_four_four);

        vLogoItemGridFiveOne = findViewById(R.id.logo_item_grid_five_one);
        vTitleItemGridFiveOne = findViewById(R.id.title_item_grid_five_one);
        vLogoItemGridFiveTwo = findViewById(R.id.logo_item_grid_five_two);
        vTitleItemGridFiveTwo = findViewById(R.id.title_item_grid_five_two);
        vLogoItemGridFiveThree = findViewById(R.id.logo_item_grid_five_three);
        vTitleItemGridFiveThree = findViewById(R.id.title_item_grid_five_three);
        vLogoItemGridFiveFour = findViewById(R.id.logo_item_grid_five_four);
        vTitleItemGridFiveFour = findViewById(R.id.title_item_grid_five_four);
        vLogoItemGridFiveFive = findViewById(R.id.logo_item_grid_five_five);
        vTitleItemGridFiveFive = findViewById(R.id.title_item_grid_five_five);

        vLogoItemGridSixOne = findViewById(R.id.logo_item_grid_six_one);
        vTitleItemGridSixOne = findViewById(R.id.title_item_grid_six_one);
        vLogoItemGridSixTwo = findViewById(R.id.logo_item_grid_six_two);
        vTitleItemGridSixTwo = findViewById(R.id.title_item_grid_six_two);
        vLogoItemGridSixThree = findViewById(R.id.logo_item_grid_six_three);
        vTitleItemGridSixThree = findViewById(R.id.title_item_grid_six_three);
        vLogoItemGridSixFour = findViewById(R.id.logo_item_grid_six_four);
        vTitleItemGridSixFour = findViewById(R.id.title_item_grid_six_four);
        vLogoItemGridSixFive = findViewById(R.id.logo_item_grid_six_five);
        vTitleItemGridSixFive = findViewById(R.id.title_item_grid_six_five);
        vLogoItemGridSixSix = findViewById(R.id.logo_item_grid_six_six);
        vTitleItemGridSixSix = findViewById(R.id.title_item_grid_six_six);*/

        vLogoItemGridNineOne = findViewById(R.id.logo_item_grid_nine_one);
        vTitleItemGridNineOne = findViewById(R.id.title_item_grid_nine_one);
        vLogoItemGridNineTwo = findViewById(R.id.logo_item_grid_nine_two);
        vTitleItemGridNineTwo = findViewById(R.id.title_item_grid_nine_two);
        vLogoItemGridNineThree = findViewById(R.id.logo_item_grid_nine_three);
        vTitleItemGridNineThree = findViewById(R.id.title_item_grid_nine_three);
        vLogoItemGridNineFour = findViewById(R.id.logo_item_grid_nine_four);
        vTitleItemGridNineFour = findViewById(R.id.title_item_grid_nine_four);
        vLogoItemGridNineFive = findViewById(R.id.logo_item_grid_nine_five);
        vTitleItemGridNineFive = findViewById(R.id.title_item_grid_nine_five);
        vLogoItemGridNineSix = findViewById(R.id.logo_item_grid_nine_six);
        vTitleItemGridNineSix = findViewById(R.id.title_item_grid_nine_six);
        vLogoItemGridNineSeven = findViewById(R.id.logo_item_grid_nine_seven);
        vTitleItemGridNineSeven = findViewById(R.id.title_item_grid_nine_seven);
        vLogoItemGridNineEight = findViewById(R.id.logo_item_grid_nine_eight);
        vTitleItemGridNineEight = findViewById(R.id.title_item_grid_nine_eight);
        vLogoItemGridNineNine = findViewById(R.id.logo_item_grid_nine_nine);
        vTitleItemGridNineNine = findViewById(R.id.title_item_grid_nine_nine);
    }

    @RequiresApi(23)
    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        super.onViewReady(savedInstanceState);
        resolveRoomConfig();

        resolveView();
        resolveAnimation();
        resolveListRooms();
        resolveOpenRooms();
    }

    protected void resolveRoomConfig() {
        if (getIntent() != null) {
            if (!getIntent().hasExtra(Constants.EXTRA_ROOM)) {
                success = getIntent().getStringExtra("success");
                username = getIntent().getStringExtra(ConversationActivity.KEY_JABBER_ID);
                targetURL = getIntent().getStringExtra(ConversationActivity.KEY_TITLE);
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.WRITE_CONTACTS},
                    TAG_CODE_PERMISSION_LOCATION);
        }

        try {
            int off = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
            if (off == 0) {
                Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(onGPS);
            }
        } catch (Exception e) {
        }

    }


    @Override
    protected void onPause() {
        unregisterReceiver(broadcastHandler);
        assistant.stop();
        numbers.clear();
        appBarLayout.removeOnOffsetChangedListener(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        assistant.start();

        IntentFilter f = new IntentFilter(
                MessengerConnectionService.ACTION_MESSAGE_RECEIVED);
        f.addAction(MainBaseActivityNew.ACTION_REFRESH_BADGER);
        f.addAction(MainBaseActivityNew.ACTION_REFRESH_NOTIF);
        f.setPriority(1);

        registerReceiver(broadcastHandler, f);
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                .cancel(NotificationReceiver.NOTIFY_ID);
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                .cancel(NotificationReceiver.NOTIFY_ID_CARD);
        addShortcutBadger(getApplicationContext());

        onHomeRefresh();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs != null) {
            extra_grid_size = prefs.getString(Constants.EXTRA_GRID_SIZE, Constants.EXTRA_GRID_SIZE_THREE);
            if (extra_grid_size.equalsIgnoreCase(Constants.EXTRA_GRID_SIZE_THREE)) {
                resourceAdapterId = R.layout.list_grid_item;
                if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    layoutManager = new GridLayoutManager(this, 5, RecyclerView.VERTICAL, false);
                } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    layoutManager = new GridLayoutManager(this, 3, RecyclerView.VERTICAL, false);
                }
            } else {
                resourceAdapterId = R.layout.list_grid_item_four;
                if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    layoutManager = new GridLayoutManager(this, 7, RecyclerView.VERTICAL, false);
                } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    layoutManager = new GridLayoutManager(this, 4, RecyclerView.VERTICAL, false);
                }
            }
        } else {
            resourceAdapterId = R.layout.list_grid_item;
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                layoutManager = new GridLayoutManager(this, 5, RecyclerView.VERTICAL, false);
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                layoutManager = new GridLayoutManager(this, 3, RecyclerView.VERTICAL, false);
            }
        }

        recyclerViewDragDropManager = new RecyclerViewDragDropManager();
        recyclerViewDragDropManager.setInitiateOnLongPress(true);
        recyclerViewDragDropManager.setInitiateOnMove(false);
        recyclerViewDragDropManager.setLongPressTimeout(750);
        recyclerViewDragDropManager.setDragStartItemAnimationDuration(250);
        recyclerViewDragDropManager.setDraggingItemAlpha(0.8f);
        recyclerViewDragDropManager.setDraggingItemScale(1.3f);
        recyclerViewDragDropManager.setDraggingItemRotation(15.0f);

        recyclerViewDragDropManager.setOnItemDragEventListener(new RecyclerViewDragDropManager.OnItemDragEventListener() {
            @Override
            public void onItemDragStarted(int position) {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    v.vibrate(50);
                }
            }

            @Override
            public void onItemDragPositionChanged(int fromPosition, int toPosition) {
            }

            @Override
            public void onItemDragFinished(int fromPosition, int toPosition, boolean result) {
            }

            @Override
            public void onItemDragMoveDistanceUpdated(int offsetX, int offsetY) {
            }
        });

        final DraggableGridExampleAdapter myItemAdapter = new DraggableGridExampleAdapter(this, itemList, resourceAdapterId, room_id, positionList);
        adapter = myItemAdapter;
        wrappedAdapter = recyclerViewDragDropManager.createWrappedAdapter(myItemAdapter);
        GeneralItemAnimator animator = new DraggableItemAnimator();

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(wrappedAdapter);
        recyclerView.setItemAnimator(animator);

        recyclerViewDragDropManager.attachRecyclerView(recyclerView);

        adapter.setOnItemClickListener((view, position) -> {
            Intent intent = ByonChatMainRoomActivity.generateIntent(getApplicationContext(), (ItemMain) adapter.getData().get(position));
            startActivity(intent);
        });

        adapter.setOnLongItemClickListener((view, position) -> {
            showToastTab(adapter.getData().get(position).tab_name);
        });

        resolveNavHeader();
        resolveListRooms();
        resolveOpenRooms();
        resolveRefreshGrid();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!isVisible) {
            getMenuInflater().inflate(R.menu.ims_menu_main_search, menu);
            MenuItem item = menu.findItem(R.id.main_search);
            searchView.setMenuItem(item);
            if (searchView.isSearchOpen()) {
                tb.setVisibility(View.GONE);
            } else {
                tb.setVisibility(View.VISIBLE);
            }
            return true;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    protected boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i(MainActivityNew.class.getName(), "isMyServiceRunning? " + true + "");
                return true;
            }
        }

        Log.i(MainActivityNew.class.getName(), "isMyServiceRunning? " + false + "");
        return false;
    }

    @RequiresApi(23)
    @SuppressWarnings("WrongConstant")
    protected void resolveView() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                if (prefs != null) {
                    if (prefs.getString(Constants.EXTRA_SERVICE_PERMISSION, "false").equalsIgnoreCase("true")) {

                    } else {
                        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
                        ComponentName componentName = new ComponentName(MainActivityNew.this, WhatsAppJobService.class);
                        JobInfo jobInfo = new JobInfo.Builder(1, componentName)
                                .setPeriodic(TimeUnit.MINUTES.toMillis(1))
                                .build();

                        PermanentLoggerUtil.logMessage(MainActivityNew.this, "Scheduling recurring job");
                        jobScheduler.schedule(jobInfo);

                        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                        if (pm.isIgnoringBatteryOptimizations(getPackageName())) {
                            Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                            startActivity(intent);
                        }

                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Constants.EXTRA_SERVICE_PERMISSION, "true");
                        editor.apply();
                    }
                } else {
                    JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
                    ComponentName componentName = new ComponentName(MainActivityNew.this, WhatsAppJobService.class);
                    JobInfo jobInfo = new JobInfo.Builder(1, componentName)
                            .setPeriodic(TimeUnit.MINUTES.toMillis(1))
                            .build();

                    PermanentLoggerUtil.logMessage(MainActivityNew.this, "Scheduling recurring job");
                    jobScheduler.schedule(jobInfo);

                    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                    if (pm.isIgnoringBatteryOptimizations(getPackageName())) {
                        Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }

                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constants.EXTRA_SERVICE_PERMISSION, "true");
                    editor.apply();
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Utility.scheduleJob(this);
            } else {
                mUploadService = new UploadService();
                mServiceIntent = new Intent(this, mUploadService.getClass());
                mServiceIntent.putExtra(UploadService.ACTION, "startService");
                if (!isMyServiceRunning(mUploadService.getClass())) {
                    startService(mServiceIntent);
                }

                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 898989,
                        mServiceIntent, 0);
                int alarmType = AlarmManager.ELAPSED_REALTIME;
                final int FIFTEEN_SEC_MILLIS = 8000;
                AlarmManager alarmManager = (AlarmManager)
                        getApplicationContext().getSystemService(getApplicationContext().ALARM_SERVICE);
                alarmManager.setInexactRepeating(alarmType, SystemClock.elapsedRealtime() + FIFTEEN_SEC_MILLIS,
                        FIFTEEN_SEC_MILLIS, pendingIntent);

                ComponentName receiver = new ComponentName(getApplicationContext(), MyBroadcastReceiver.class);
                PackageManager pm = getApplicationContext().getPackageManager();

                pm.setComponentEnabledSetting(receiver,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);
            }

            IntervalDB db = new IntervalDB(getApplicationContext());
            db.open();
            Cursor cursor = db.getSingleContact(12);
            if (cursor.getCount() > 0) {
                if (cursor.getString(cursor.getColumnIndexOrThrow(IntervalDB.COL_TIME)).equalsIgnoreCase("settingUp")) {
                    Intent intents = new Intent(this, LoadContactScreen.class);
                    startActivity(intents);
                    finish();
                    return;
                } else if (cursor.getString(cursor.getColumnIndexOrThrow(IntervalDB.COL_TIME)).equalsIgnoreCase("Finalizing")) {
                    Intent intents = new Intent(this, FinalizingActivity.class);
                    startActivity(intents);
                    finish();
                    return;
                }
            }
            cursor.close();


            final Contact contact = Byonchat.getMessengerHelper().getMyContact();

            if (contact == null) {
                Cursor cursorSkin = db.getCountSkin();
                ArrayList<Skin> skinArrayList = db.retriveallSkin();

                boolean insertByon = true;
                for (Skin s : skinArrayList) {
                    if (s.getTitle().equalsIgnoreCase("byonchat")) insertByon = false;
                }

                if (cursorSkin.getCount() == 0 || insertByon) {
                    Bitmap logos = BitmapFactory.decodeResource(getResources(), R.drawable.logo_byon);
                    Bitmap back = BitmapFactory.decodeResource(getResources(), R.drawable.bg_chat_baru);
                    Bitmap header = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                    Skin skin = new Skin("byonchat", "original", "#006b9c", logos, header, back);
                    db.createSkin(skin);
                }
                cursorSkin.close();

                db.close();
                Intent intent = new Intent(this, RegistrationActivity.class);
                startActivity(intent);
                finish();
                return;
            }

            /*color = getResources().getColor(R.color.colorPrimary);*/


            Cursor cursorSelect = db.getSingleContact(4);
            if (cursorSelect.getCount() > 0) {
                String skin = cursorSelect.getString(cursorSelect.getColumnIndexOrThrow(IntervalDB.COL_TIME));
                Skin skins = null;
                Cursor c = db.getCountSkin();
                if (c.getCount() > 0) {
                    skins = db.retriveSkinDetails(skin);
                    /*color = Color.parseColor(skins.getColor());*/
                }
                c.close();
            } else {
                Interval interval = new Interval();
                interval.setId(4);
                interval.setTime("byonchat");
                db.createContact(interval);
            }
            cursorSelect.close();
            db.close();

            mActivity = this;

            resolveNavHeader();
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

            if (navigationView != null) {
                navigationView.setNavigationItemSelectedListener(menuItem -> {
                    int id = menuItem.getItemId();
                    switch (id) {
                        case R.id.nav_item_one:
                            Intent intent = new Intent(getApplicationContext(), NewSearchRoomActivity.class);
                            intent.putExtra(Constants.EXTRA_COLOR, color);
                            intent.putExtra(Constants.EXTRA_COLORTEXT, colorText);
                            intent.putExtra("search", "all");
                            startActivity(intent);
                            break;
                        case R.id.nav_item_two:
                            shareIt();
                            break;
                        case R.id.nav_item_three:
                            Intent intent2 = new Intent(getApplicationContext(), MainSettingActivity.class);
                            intent2.putExtra(Constants.EXTRA_COLOR, color);
                            intent2.putExtra(Constants.EXTRA_COLORTEXT, colorText);
                            startActivity(intent2);
                            break;
                        case R.id.nav_item_four:
                            Intent intent3 = new Intent(getApplicationContext(), UpdateProfileActivity.class);
                            intent3.putExtra(Constants.EXTRA_COLOR, color);
                            intent3.putExtra(Constants.EXTRA_COLORTEXT, colorText);
                            startActivity(intent3);
                            break;
                        case R.id.nav_item_refresh:
                            RefreshRoom();
                            break;
                        case R.id.nav_item_create_shortcut:
                            createShortcut();
                            break;
                        case R.id.nav_item_grid_size:
                            changeGridSize();
                            break;
                        case R.id.nav_item_legal:
                            /*Byonchat.getRoomsDB().open();
                            Byonchat.getRoomsDB().deleteRooms();
                            Byonchat.getRoomsDB().close();
                            resolveNavHeader();
                            resolveListRooms();
                            resolveOpenRooms();*/

                            Intent intent1 = TestWhatsappLogActivity.generateIntent(this);
                            startActivity(intent1);

                            break;
                    }
                    return true;
                });
            }

            fab_logo.setImageResource(logo);
            fab_menu_1.setOnClickListener(v -> {
                drawerLayout.openDrawer(GravityCompat.START);
            });

            if (!image_url.equalsIgnoreCase("")) {
                Glide.with(this).load(background).into(backgroundImage);
            } else {
                Glide.with(this).load(background).into(backgroundImage);
            }

            final Drawable windowBackground = getWindow().getDecorView().getBackground();
            vBlurView.setupWith(root_view)
                    .setFrameClearDrawable(windowBackground)
                    .setBlurAlgorithm(new SupportRenderScriptBlur(this))
                    .setBlurRadius(radius)
                    .setHasFixedTransformationMatrix(true);

            vBlurTopBackground.setBlurRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics()));

            vBtnAddRooms.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), NewSearchRoomActivity.class);
                intent.putExtra(Constants.EXTRA_COLOR, color);
                intent.putExtra(Constants.EXTRA_COLORTEXT, colorText);
                intent.putExtra("search", "all");
                startActivity(intent);
            });

            resolveListRooms();

            card_search_main.setOnClickListener(v -> {
                appBarLayout.setExpanded(false, true);
                final Handler handler = new Handler();
                handler.postDelayed(() -> {
                    searchView.showSearch(true);
                }, 500);
            });
            but_search_main.setOnClickListener(v -> {
                appBarLayout.setExpanded(false, true);
                final Handler handler = new Handler();
                handler.postDelayed(() -> {
                    searchView.showSearch(true);
                }, 500);
            });
            input_search_main.setOnClickListener(v -> {
                appBarLayout.setExpanded(false, true);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        searchView.showSearch(true);
                    }
                }, 500);
            });
            vBtnToolbarSearch.setOnClickListener(v -> {
                searchView.showSearch(true);
            });

            searchView.setHint("Search ...");
            searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    adapter.getFilter().filter(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    adapter.getFilter().filter(newText);
                    return true;
                }
            });
            searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
                @Override
                public void onSearchViewShown() {
                    tb.setVisibility(View.GONE);
                }

                @Override
                public void onSearchViewClosed() {
                    tb.setVisibility(View.VISIBLE);
                }
            });

            fab_menu_2.setOnClickListener(view -> {
                Intent intent = ImsListHistoryChatActivity.generateIntent(getApplicationContext(), color, colorText);
                startActivity(intent);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (Math.abs(i) - appBarLayout.getTotalScrollRange() == 0) {
            card_search_main.setVisibility(View.GONE);
            isVisible = true;
            invalidateOptionsMenu();
        } else {
            card_search_main.setVisibility(View.GONE);
            isVisible = true;
                    /*if (searchView.isSearchOpen()) {
                        searchView.closeSearch();
                        tb.setVisibility(View.VISIBLE);
                    }*/
            invalidateOptionsMenu();
        }

        float logic1 = Math.abs(i) - appBarLayout.getTotalScrollRange();
        float logic2 = (logic1 / appBarLayout.getTotalScrollRange());
        float pusing = 1 - (logic2 * -1);
        vBlurTopBackground.setAlpha(pusing);

        vSwipeRefresh.setEnabled(i == 0);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START);
        } else if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        if (recyclerViewDragDropManager != null) {
            recyclerViewDragDropManager.release();
            recyclerViewDragDropManager = null;
        }

        if (recyclerView != null) {
            recyclerView.setItemAnimator(null);
            recyclerView.setAdapter(null);
            recyclerView = null;
        }

        if (wrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(wrappedAdapter);
            wrappedAdapter = null;
        }
        adapter = null;
        layoutManager = null;
        super.onDestroy();
    }

    private int dpToPx(int dp) {
        float density = getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

    private void createShortcutOfApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            ShortcutInfo.Builder mShortcutInfoBuilder = new ShortcutInfo.Builder(MainActivityNew.this, getString(R.string.app_name));
            mShortcutInfoBuilder.setShortLabel(getString(R.string.app_name));
            mShortcutInfoBuilder.setLongLabel(getString(R.string.app_name));
            mShortcutInfoBuilder.setIcon(Icon.createWithResource(MainActivityNew.this, R.drawable.logo_byon));
            Intent shortcutIntent = new Intent(getApplicationContext(), MainActivityNew.class);
            shortcutIntent.setAction(Intent.ACTION_CREATE_SHORTCUT);
            mShortcutInfoBuilder.setIntent(shortcutIntent);
            ShortcutInfo mShortcutInfo = mShortcutInfoBuilder.build();
            ShortcutManager mShortcutManager = getSystemService(ShortcutManager.class);
            mShortcutManager.requestPinShortcut(mShortcutInfo, null);

        } else {

            Intent shortcutIntent = new Intent(getApplicationContext(),
                    MainActivityNew.class);
            shortcutIntent.setAction(Intent.ACTION_MAIN);

            Intent addIntent = new Intent();
            addIntent
                    .putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Shortcut");
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                    Intent.ShortcutIconResource.fromContext(getApplicationContext(),
                            R.drawable.logo_byon));

            addIntent
                    .setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            addIntent.putExtra("duplicate", false);  //may it's already there so   don't duplicate
            getApplicationContext().sendBroadcast(addIntent);

        }
        Toast.makeText(this, "Shortcut Created", Toast.LENGTH_SHORT).show();
    }
}
