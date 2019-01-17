package com.byonchat.android.ui.activity;

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
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.byonchat.android.AdvRecy.DraggableGridExampleAdapter;
import com.byonchat.android.AdvRecy.ItemMain;
import com.byonchat.android.AdvRecy.MainDbHelper;
import com.byonchat.android.ByonChatMainRoomActivity;
import com.byonchat.android.ConversationActivity;
import com.byonchat.android.FinalizingActivity;
import com.byonchat.android.LoadContactScreen;
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
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.list.BotAdapter;
import com.byonchat.android.local.Byonchat;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.Interval;
import com.byonchat.android.provider.IntervalDB;
import com.byonchat.android.provider.Skin;
import com.byonchat.android.ui.adapter.OnItemClickListener;
import com.byonchat.android.ui.adapter.OnLongItemClickListener;
import com.byonchat.android.ui.view.ByonchatRecyclerView;
import com.byonchat.android.utils.DialogUtil;
import com.byonchat.android.utils.UploadService;
import com.byonchat.android.utils.Utility;
import com.byonchat.android.widget.BadgeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.internal.Util;

public class MainActivityNew extends MainBaseActivityNew {

    @Override
    protected int getResourceLayout() {
        return R.layout.main_activity_new;
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

        root_view = findViewById(R.id.main_group);
        drawerLayout = findViewById(R.id.drawer_main);
        navigationView = findViewById(R.id.nav_view);
        vListRooms = findViewById(R.id.list_view);
        recyclerView = findViewById(R.id.recycler_main);
        vBtnToolbarSearch = findViewById(R.id.ic_toolbar_title);
        vToolbarSearchText = findViewById(R.id.text_toolbar_title);

        card_menu_main = findViewById(R.id.card_nav_main);
//        blurView = findViewById(R.id.blurView);
        fab_menu_1 = findViewById(R.id.fab_menu_1_main);
        fab_menu_2 = findViewById(R.id.fab_menu_2_main);
//        fab_menu_3 = findViewById(R.id.fab_menu_3_main);

        vFrameWarning = findViewById(R.id.frame_warning);
        vTxtStatusWarning = findViewById(R.id.text_status);

        View headerview = navigationView.getHeaderView(0);
        vNavLogo = headerview.findViewById(R.id.nav_logo);
        vNavTitle = headerview.findViewById(R.id.nav_title);
        vBtnOpenRooms = headerview.findViewById(R.id.nav_button_open_room);
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        super.onViewReady(savedInstanceState);
        resolveRoomConfig(savedInstanceState);

        resolveView();
        resolveAnimation();
        resolveListRooms();
        resolveOpenRooms();
    }

    protected void resolveRoomConfig(Bundle savedInstanceState) {
        if (getIntent() != null) {
            success = getIntent().getStringExtra("success");
            username = getIntent().getStringExtra(ConversationActivity.KEY_JABBER_ID);
            targetURL = getIntent().getStringExtra(ConversationActivity.KEY_TITLE);
        }
    }

    @Override
    protected void onPause() {
        unregisterReceiver(broadcastHandler);
        assistant.stop();
        numbers.clear();
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

        resolveValidationLogin();
        resolveToolbarExpanded();
        resolveNavHeader();
        resolveListRooms();
        resolveOpenRooms();
        resolveRefreshGrid();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = new GridLayoutManager(this, 5, RecyclerView.VERTICAL, false);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new GridLayoutManager(this, 3, RecyclerView.VERTICAL, false);
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


        final DraggableGridExampleAdapter myItemAdapter = new DraggableGridExampleAdapter(this, itemList, room_id, positionList);
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

    @SuppressWarnings("WrongConstant")
    protected void resolveView() {
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                Utility.scheduleJob(this);
            } else {
                mUploadService = new UploadService();
                mServiceIntent = new Intent(this, mUploadService.getClass());
                mServiceIntent.putExtra(UploadService.ACTION, "startService");
                if (!isMyServiceRunning(mUploadService.getClass())) {
                    startService(mServiceIntent);
                }

                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 5555555,
                        mServiceIntent, 0);
                int alarmType = AlarmManager.ELAPSED_REALTIME;
                final int FIFTEEN_SEC_MILLIS = 15000;
                AlarmManager alarmManager = (AlarmManager)
                        getApplicationContext().getSystemService(getApplicationContext().ALARM_SERVICE);
                alarmManager.setRepeating(alarmType, SystemClock.elapsedRealtime() + FIFTEEN_SEC_MILLIS,
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
                        case R.id.nav_item_legal:
                            Byonchat.getRoomsDB().open();
                            Byonchat.getRoomsDB().deleteRooms();
                            Byonchat.getRoomsDB().close();
                            resolveNavHeader();
                            resolveListRooms();
                            resolveOpenRooms();
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

            //appbar
            appBarLayout.addOnOffsetChangedListener((appBarLayout, i) -> {
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
            });

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

            final Drawable windowBackground = getWindow().getDecorView().getBackground();
//            blurView.setupWith(root_view)
//                    .setFrameClearDrawable(windowBackground)
//                    .setBlurAlgorithm(new SupportRenderScriptBlur(this))
//                    .setBlurRadius(radius)
//                    .setHasFixedTransformationMatrix(true);

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

    private void resetGridPosition() {
        sqLiteDatabase = database.getWritableDatabase();
        sqLiteDatabase.delete(MainDbHelper.TABLE_ITEM, null, null);
        sqLiteDatabase.close();

        recreate();

        Toast.makeText(getBaseContext(), "Grid position has been reset", Toast.LENGTH_LONG).show();
    }
}
