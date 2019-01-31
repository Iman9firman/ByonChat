package com.byonchat.android.ui.activity;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.AdvRecy.ItemMain;
import com.byonchat.android.ByonChatMainRoomActivity;
import com.byonchat.android.ConversationActivity;
import com.byonchat.android.FragmentDinamicRoom.DinamicRoomSearchTaskActivity;
import com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity;
import com.byonchat.android.FragmentDinamicRoom.FragmentDirectory;
import com.byonchat.android.FragmentDinamicRoom.FragmentRoomAPI;
import com.byonchat.android.FragmentDinamicRoom.FragmentRoomAbout;
import com.byonchat.android.FragmentDinamicRoom.FragmentRoomPOS;
import com.byonchat.android.FragmentDinamicRoom.FragmentStreamingRadio;
import com.byonchat.android.FragmentDinamicRoom.FragmentStreamingVideo;
import com.byonchat.android.FragmentDinamicRoom.RoomPOSdetail;
import com.byonchat.android.LoadingGetTabRoomActivity;
import com.byonchat.android.R;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.createMeme.FilteringImage;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.local.Byonchat;
import com.byonchat.android.personalRoom.FragmentMyCardID;
import com.byonchat.android.personalRoom.FragmentMyNews;
import com.byonchat.android.personalRoom.FragmentMyNewsNew;
import com.byonchat.android.personalRoom.FragmentMyNote;
import com.byonchat.android.personalRoom.FragmentMyPicture;
import com.byonchat.android.personalRoom.FragmentMyVideo;
import com.byonchat.android.personalRoom.FragmentProductCatalog;
import com.byonchat.android.personalRoom.FragmentWebView;
import com.byonchat.android.provider.ContactBot;
import com.byonchat.android.provider.ContentRoom;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.room.FragmentRoomMultipleTask;
import com.byonchat.android.room.FragmentRoomSearchMultiTask;
import com.byonchat.android.room.FragmentRoomTask;
import com.byonchat.android.room.FragmentRoomTaskWater;
import com.byonchat.android.tempSchedule.TempScheduleRoom;
import com.byonchat.android.ui.fragment.ByonchatPDFFragment;
import com.byonchat.android.ui.fragment.ByonchatVideoFragment;
import com.byonchat.android.utils.Utility;
import com.googlecode.mp4parser.authoring.Edit;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MainByonchatRoomBaseActivity extends AppCompatActivity {

    public static String TAG = MainByonchatRoomBaseActivity.class.getName() + " not running";

    public static String FRAGMENT_ROOM_ABOUT = "fragment_room_about";
    public static String FRAGMENT_ROOM_MY_PICTURE = "fragment_room_my_picture";
    public static String FRAGMENT_ROOM_MY_VIDEO = "fragment_room_my_video";
    public static String FRAGMENT_ROOM_TASK_WATER = "fragment_room_task_water";
    public static String FRAGMENT_ROOM_TASK = "fragment_room_task";
    public static String FRAGMENT_ROOM_API = "fragment_room_api";
    public static String FRAGMENT_ROOM_MULTIPLE_TASK = "fragment_room_multiple_task";
    public static String FRAGMENT_ROOM_TEMP_SCHEDULE = "fragment_rooom_temp_schedule";
    public static String FRAGMENT_ROOM_MY_NOTE = "fragment_room_my_note";
    public static String FRAGMENT_ROOM_MY_NEWS = "fragment_room_my_news";
    public static String FRAGMENT_ROOM_READ_MANUAL = "fragment_room_read_manual";
    public static String FRAGMENT_ROOM_DIRECTORY = "fragment_room_directory";
    public static String FRAGMENT_ROOM_STREAMING_VIDEO = "fragment_room_streaming_video";
    public static String FRAGMENT_ROOM_STREAMING_AUDIO = "fragment_room_streaming_audio";
    public static String FRAGMENT_ROOM_POS = "fragment_room_pos";
    public static String FRAGMENT_ROOM_VIDEO_DOWNLOAD = "fragment_room_video_download";
    public static String FRAGMENT_ROOM_PRODUCT_CATALOG = "fragment_room_product_catalog";
    public static String FRAGMENT_ROOM_CARD_ID = "fragment_room_card_id";
    public static String FRAGMENT_ROOM_API_APURA = "fragment_room_api_apura";

    public static final String EXTRA_ITEM = "extra_item";
    public static final String EXTRA_POSITION = "extra_position";
    public static final String EXTRA_COLOR = "extra_color";
    public static final String EXTRA_COLORTEXT = "extra_colortext";
    public static final String EXTRA_TARGETURL = "extra_targeturl";
    public static final String EXTRA_CONTENT = "extra_content";
    public static final String EXTRA_CATEGORY = "extra_category";
    public static final String EXTRA_TITLE = "extra_title";
    public final static String EXTRA_URL_TEMBAK = "extra_url_tembak";
    public static final String EXTRA_ID_ROOMS_TAB = "extra_id_rooms_tab";
    public static final String EXTRA_INCLUDE_PULL = "extra_include_pull";
    public static final String EXTRA_INCLUDE_LATLONG = "extra_include_latlong";
    public static final String EXTRA_STATUS = "extra_status";
    public static final String EXTRA_NAME = "extra_name";
    public static final String EXTRA_ICON = "extra_icon";
    public static final String EXTRA_VALUE = "extra_value";
    public static final String KEY_POSITION = "extra_key_position";

    protected ItemMain listItem;
    protected Fragment mFragment;
    protected Boolean show = false;
    protected Boolean next = true;
    protected Point p;

    protected int position;
    protected String color;
    protected String colorText;
    protected String targetURL;
    protected String username;
    protected String category;
    protected String title;
    protected String url_tembak;
    protected String id_rooms_tab;
    protected String include_pull;
    protected String include_latlong;
    protected String status;
    protected String name;
    protected String icon;
    protected String payload;

    protected boolean isSearchView = false;
    protected static float positionFromRight = 1;
    protected Menu menu;
    protected boolean isVisible = false;

    @NonNull
    protected AppBarLayout vAppbar;

    @NonNull
    protected Toolbar vToolbar;

    @NonNull
    protected RelativeLayout vToolbarBack;

    @NonNull
    protected ImageView vImgToolbarBack;

    @NonNull
    protected TextView vToolbarTitle;

    @NonNull
    protected MaterialSearchView vSearchView;

    @NonNull
    protected FrameLayout vContainerFragment;

    @NonNull
    protected FloatingActionButton vFloatingButton;

    @NonNull
    protected View searchAppBarLayout;

    @NonNull
    protected Toolbar searchToolBar;

    @NonNull
    protected EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            onSetStatusBarColor();
            setContentView(getResourceLayout());
            onLoadConfig(savedInstanceState);
            onLoadToolbar();
            onLoadView();
            onViewReady(savedInstanceState);
        } catch (Exception e) {
            Log.w(TAG, e.toString());
            finish();
            Toast.makeText(this, R.string.str_not_able_open_room, Toast.LENGTH_SHORT).show();
        }
    }

    protected void onSetStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
    }

    protected abstract int getResourceLayout();

    protected abstract void onLoadConfig(Bundle savedInstanceState);

    protected abstract void onLoadView();

    protected abstract void onLoadToolbar();

    protected void applyConfig() {
        position = listItem != null ? listItem.id : getIntent().getExtras().getInt(EXTRA_POSITION, 0);
        username = listItem != null ? listItem.username : getIntent().getExtras().getString(ConversationActivity.KEY_JABBER_ID);
        color = listItem != null ? listItem.color : getIntent().getExtras().getString(EXTRA_COLOR);
        colorText = listItem != null ? listItem.colorText : getIntent().getExtras().getString(EXTRA_COLORTEXT);
        targetURL = listItem != null ? listItem.targetURL : getIntent().getExtras().getString(EXTRA_TARGETURL);
        category = listItem != null ? listItem.category_tab : getIntent().getExtras().getString(EXTRA_CATEGORY);
        title = listItem != null ? listItem.tab_name : getIntent().getExtras().getString(EXTRA_TITLE);
        url_tembak = listItem != null ? listItem.url_tembak : getIntent().getExtras().getString(EXTRA_URL_TEMBAK);
        id_rooms_tab = listItem != null ? listItem.id_rooms_tab : getIntent().getExtras().getString(EXTRA_ID_ROOMS_TAB);
        include_pull = listItem != null ? listItem.include_pull : getIntent().getExtras().getString(EXTRA_INCLUDE_PULL);
        include_latlong = listItem != null ? listItem.include_latlong : getIntent().getExtras().getString(EXTRA_INCLUDE_LATLONG);
        status = listItem != null ? listItem.status : getIntent().getExtras().getString(EXTRA_STATUS);
        name = listItem != null ? listItem.name : getIntent().getExtras().getString(EXTRA_NAME);
        icon = listItem != null ? listItem.icon : getIntent().getExtras().getString(EXTRA_ICON);
    }

    protected void onViewReady(Bundle savedInstanceState) {
    }

    protected void resolveSearchBar() {
        if (searchToolBar != null) {
            searchToolBar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back);
            searchAppBarLayout.setVisibility(View.GONE);
            searchToolBar.setNavigationOnClickListener(v -> {
                hideSearchBar(positionFromRight);
            });

            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (mFragment instanceof FragmentRoomMultipleTask) {
                        FragmentRoomMultipleTask fragment = (FragmentRoomMultipleTask) getSupportFragmentManager().findFragmentById(R.id.container_open_fragment);
                        fragment.onActionSearch(s.toString());
                    } else if (mFragment instanceof FragmentRoomTask) {
                        FragmentRoomTask fragment = (FragmentRoomTask) getSupportFragmentManager().findFragmentById(R.id.container_open_fragment);
                        fragment.onActionSearch(s.toString());
                    } else if (mFragment instanceof FragmentRoomTaskWater) {
                        FragmentRoomTaskWater fragment = (FragmentRoomTaskWater) getSupportFragmentManager().findFragmentById(R.id.container_open_fragment);
                        fragment.onActionSearch(s.toString());
                    } else if (mFragment instanceof ByonchatPDFFragment) {
                        ByonchatPDFFragment fragment = (ByonchatPDFFragment) getSupportFragmentManager().findFragmentById(R.id.container_open_fragment);
                        fragment.onActionSearch(s.toString());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (mFragment instanceof FragmentRoomMultipleTask) {
                        FragmentRoomMultipleTask fragment = (FragmentRoomMultipleTask) getSupportFragmentManager().findFragmentById(R.id.container_open_fragment);
                        fragment.onActionSearch(s.toString());
                    } else if (mFragment instanceof FragmentRoomTask) {
                        FragmentRoomTask fragment = (FragmentRoomTask) getSupportFragmentManager().findFragmentById(R.id.container_open_fragment);
                        fragment.onActionSearch(s.toString());
                    } else if (mFragment instanceof FragmentRoomTaskWater) {
                        FragmentRoomTaskWater fragment = (FragmentRoomTaskWater) getSupportFragmentManager().findFragmentById(R.id.container_open_fragment);
                        fragment.onActionSearch(s.toString());
                    } else if (mFragment instanceof ByonchatPDFFragment) {
                        ByonchatPDFFragment fragment = (ByonchatPDFFragment) getSupportFragmentManager().findFragmentById(R.id.container_open_fragment);
                        fragment.onActionSearch(s.toString());
                    }
                }
            });
        }
    }

    protected void resolveToolbar() {
        setSupportActionBar(vToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FilteringImage.SystemBarBackground(getWindow(), Color.parseColor("#" + color));

        vToolbar.setBackgroundColor(Color.parseColor("#" + color));
        vToolbar.setTitleTextColor(Color.parseColor("#" + colorText));
        vToolbarTitle.setTextColor(Color.parseColor("#" + colorText));

        Drawable mDrawable = getResources().getDrawable(R.drawable.ic_keyboard_arrow_left_black_24dp);
        mDrawable.setColorFilter(Color.parseColor("#" + colorText), PorterDuff.Mode.SRC_ATOP);
        vImgToolbarBack.setImageDrawable(mDrawable);

        vToolbarBack.setOnClickListener(v -> onBackPressed());
        vToolbarTitle.setText(title);
    }

    protected void resolveMaterialSearchView() {
        vSearchView.setHint("Search ...");

        vSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                View view = findViewById(R.id.action_short);
                if (null != view)
                    view.setVisibility(View.GONE);

                if (mFragment instanceof FragmentRoomMultipleTask) {
                    FragmentRoomMultipleTask fragment = (FragmentRoomMultipleTask) getSupportFragmentManager().findFragmentById(R.id.container_open_fragment);
                    fragment.onActionSearch(query);
                } else if (mFragment instanceof FragmentRoomTask) {
                    FragmentRoomTask fragment = (FragmentRoomTask) getSupportFragmentManager().findFragmentById(R.id.container_open_fragment);
                    fragment.onActionSearch(query);
                } else if (mFragment instanceof FragmentRoomTaskWater) {
                    FragmentRoomTaskWater fragment = (FragmentRoomTaskWater) getSupportFragmentManager().findFragmentById(R.id.container_open_fragment);
                    fragment.onActionSearch(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                View view = findViewById(R.id.action_short);
                if (null != view)
                    view.setVisibility(View.GONE);

                if (mFragment instanceof FragmentRoomMultipleTask) {
                    FragmentRoomMultipleTask fragment = (FragmentRoomMultipleTask) getSupportFragmentManager().findFragmentById(R.id.container_open_fragment);
                    fragment.onActionSearch(query);
                } else if (mFragment instanceof FragmentRoomTask) {
                    FragmentRoomTask fragment = (FragmentRoomTask) getSupportFragmentManager().findFragmentById(R.id.container_open_fragment);
                    fragment.onActionSearch(query);
                } else if (mFragment instanceof FragmentRoomTaskWater) {
                    FragmentRoomTaskWater fragment = (FragmentRoomTaskWater) getSupportFragmentManager().findFragmentById(R.id.container_open_fragment);
                    fragment.onActionSearch(query);
                }
                return true;
            }
        });

        vSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                View view = findViewById(R.id.action_short);
                if (null != view)
                    view.setVisibility(View.GONE);
            }

            @Override
            public void onSearchViewClosed() {
                View view = findViewById(R.id.action_short);
                if (null != view)
                    view.setVisibility(View.VISIBLE);
            }
        });
    }

    protected void resolveFragment() {
        Cursor cur = Byonchat.getBotListDB().getSingleRoom(username);
        Log.w("CEK WV GANDHIP", category + "  -  " + title);
        if (cur.getCount() > 0) {
            try {
                if (category.equalsIgnoreCase("1")) {
                    show = true;
                    if (getSupportFragmentManager().findFragmentByTag(FRAGMENT_ROOM_ABOUT) == null)
                        mFragment = FragmentRoomAbout.newInstance(Byonchat.getMessengerHelper().getMyContact().getJabberId(), title, url_tembak, username, id_rooms_tab, color, MainByonchatRoomBaseActivity.this);
                } else if (category.equalsIgnoreCase("2")) {
                    show = true;
                    mFragment = FragmentMyPicture.newInstance(Byonchat.getMessengerHelper().getMyContact().getJabberId(), title, url_tembak, username, id_rooms_tab, color, false, MainByonchatRoomBaseActivity.this);
                } else if (category.equalsIgnoreCase("3")) {
                    //video
                    show = true;
                    mFragment = FragmentMyVideo.newInstance(Byonchat.getMessengerHelper().getMyContact().getJabberId(), title, url_tembak, username, id_rooms_tab, "", false, MainByonchatRoomBaseActivity.this);
                } else if (category.equalsIgnoreCase("4")) {
                    Log.w("kabadu", url_tembak + " - " + include_pull);
                    if (include_pull.equalsIgnoreCase("1") || include_pull.equalsIgnoreCase("3")) {
                        List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(title);
                        valSetOne.add(username);
                        valSetOne.add(id_rooms_tab);
                        valSetOne.add(color);
                        valSetOne.add(include_latlong);
                        show = true;
                        mFragment = FragmentRoomTaskWater.newInstance(title, url_tembak, username, id_rooms_tab, color, include_latlong, MainByonchatRoomBaseActivity.this, "hide");
                        valSetOne.add("hide");
                    } else if (include_pull.equalsIgnoreCase("0")) {
                        List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(title);
                        valSetOne.add(username);
                        valSetOne.add(id_rooms_tab);
                        valSetOne.add(color);
                        valSetOne.add(include_latlong);
                        show = true;
                        valSetOne.add("show");
                        mFragment = FragmentRoomTask.newInstance(title, url_tembak, username, id_rooms_tab, color, include_latlong, MainByonchatRoomBaseActivity.this);
                    } else if (include_pull.equalsIgnoreCase("2")) {
                        show = true;
                        mFragment = FragmentRoomAPI.newInstance(title, url_tembak, username, id_rooms_tab, include_latlong, MainByonchatRoomBaseActivity.this);
                    } else if (include_pull.equalsIgnoreCase("4") || include_pull.equalsIgnoreCase("5")) {
                        List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(title);
                        valSetOne.add(username);
                        valSetOne.add(id_rooms_tab);
                        valSetOne.add(color);
                        valSetOne.add(include_latlong);
                        show = true;
                        mFragment = FragmentRoomMultipleTask.newInstance(title, url_tembak, username, id_rooms_tab, color, include_latlong, MainByonchatRoomBaseActivity.this, "hideMultiple");
                        valSetOne.add("hideMultiple");
                    } else if (include_pull.equalsIgnoreCase("6")) {
                        JSONObject jsonRootObject = new JSONObject(url_tembak);
                        List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(title);
                        valSetOne.add(username);
                        valSetOne.add(id_rooms_tab);
                        valSetOne.add(color);
                        valSetOne.add(include_latlong);
                        show = true;
                        mFragment = FragmentRoomMultipleTask.newInstance(title, jsonRootObject.getString("pull"), username, id_rooms_tab, color, include_latlong, MainByonchatRoomBaseActivity.this, "showMultiple");
                        valSetOne.add("showMultiple");
                    } else if (include_pull.equalsIgnoreCase("7")) {
                        JSONObject jsonRootObject = new JSONObject(url_tembak);
                        List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(title);
                        valSetOne.add(username);
                        valSetOne.add(id_rooms_tab);
                        valSetOne.add(color);
                        valSetOne.add(include_latlong);
                        show = true;
                        mFragment = TempScheduleRoom.newInstance(title, jsonRootObject.getString("pull"), username, id_rooms_tab, color, include_latlong, MainByonchatRoomBaseActivity.this, "showMultiple");
                        valSetOne.add("hideMultiple");
                    }
                } else if (category.equalsIgnoreCase("5")) {
                    show = true;
                    mFragment = FragmentMyNote.newInstance(Byonchat.getMessengerHelper().getMyContact().getJabberId(), title, url_tembak, username, id_rooms_tab, color, false, MainByonchatRoomBaseActivity.this);
                } else if (category.equalsIgnoreCase("14")) {
                    show = true;
                    mFragment = FragmentMyNewsNew.newInstance(Byonchat.getMessengerHelper().getMyContact().getJabberId(), title, url_tembak, username, id_rooms_tab, color, MainByonchatRoomBaseActivity.this);
                } else if (category.equalsIgnoreCase("6")) {
                    //news
                    show = true;
                    mFragment = FragmentMyNews.newInstance(Byonchat.getMessengerHelper().getMyContact().getJabberId(), title, url_tembak, username, id_rooms_tab, color, MainByonchatRoomBaseActivity.this);
                    //   mFragment = FragmentReadManual.newInstance(Byonchat.getMessengerHelper().getMyContact().getJabberId(), title, url_tembak, username, id_rooms_tab,color);
                } else if (category.equalsIgnoreCase("7")) {
                    show = true;
                    mFragment = FragmentDirectory.newInstance(Byonchat.getMessengerHelper().getMyContact().getJabberId(), title, url_tembak, username, id_rooms_tab, color, MainByonchatRoomBaseActivity.this);
                } else if (category.equalsIgnoreCase("8")) {
                    show = true;
                    mFragment = FragmentStreamingVideo.newInstance(Byonchat.getMessengerHelper().getMyContact().getJabberId(), title, url_tembak, username, id_rooms_tab, MainByonchatRoomBaseActivity.this);
                } else if (category.equalsIgnoreCase("9")) {
                    show = true;
                    mFragment = new FragmentStreamingRadio();
                } else if (category.equalsIgnoreCase("10")) {
                    show = true;
                    mFragment = FragmentRoomAPI.newInstance(title, url_tembak, username, id_rooms_tab, "0", MainByonchatRoomBaseActivity.this);
                } else if (category.equalsIgnoreCase("11")) {
                    List<String> valSetOne = new ArrayList<String>();
                    valSetOne.add("pos");
                    valSetOne.add(username);
                    valSetOne.add(id_rooms_tab);
                    valSetOne.add(color);
                    valSetOne.add(include_latlong);
                    show = true;
                    valSetOne.add("show");
                    valSetOne.add(url_tembak);
                    mFragment = FragmentRoomPOS.newInstance(Byonchat.getMessengerHelper().getMyContact().getJabberId(), title, url_tembak, username, id_rooms_tab, color, MainByonchatRoomBaseActivity.this);
                } else if (category.equalsIgnoreCase("15")) {
                    List<String> valSetOne = new ArrayList<String>();
                    valSetOne.add("btube");
                    valSetOne.add(username);
                    valSetOne.add(id_rooms_tab);
                    valSetOne.add(color);
                    valSetOne.add(include_latlong);
                    show = true;
                    valSetOne.add("showvideo");
                    valSetOne.add(url_tembak);
                    mFragment = ByonchatVideoFragment.newInstance(Byonchat.getMessengerHelper().getMyContact().getJabberId(), title, url_tembak, username, id_rooms_tab, color, MainByonchatRoomBaseActivity.this);
                } else if (category.equalsIgnoreCase("16")) {
                    //TIME=WATCH
                    show = true;
                    mFragment = FragmentProductCatalog.newInstance(Byonchat.getMessengerHelper().getMyContact().getJabberId(), title, url_tembak, username, id_rooms_tab, color, false, MainByonchatRoomBaseActivity.this);
                } else if (category.equalsIgnoreCase("17")) {
                    //TIME=WATCH
                    show = true;
                    mFragment = FragmentMyCardID.newInstance(Byonchat.getMessengerHelper().getMyContact().getJabberId(), title, url_tembak, username, id_rooms_tab, color, false, MainByonchatRoomBaseActivity.this);
                } else if (category.equalsIgnoreCase("18")) {
                    //TIME=WATCH
                    show = true;
                    mFragment = FragmentWebView.newInstance(Byonchat.getMessengerHelper().getMyContact().getJabberId(), title, url_tembak + "?bc_user=" + Byonchat.getMessengerHelper().getMyContact().getJabberId() + "&username=" + username, username, id_rooms_tab, color, false, MainByonchatRoomBaseActivity.this);
                } else if (category.equalsIgnoreCase("19")) {
                    show = true;
                    mFragment = ByonchatPDFFragment.newInstance(Byonchat.getMessengerHelper().getMyContact().getJabberId(), title, url_tembak, username, id_rooms_tab, color, MainByonchatRoomBaseActivity.this);
                } else if (category.equalsIgnoreCase("20")) {
                    List<String> valSetOne = new ArrayList<String>();
                    valSetOne.add(title);
                    valSetOne.add(username);
                    valSetOne.add(id_rooms_tab);
                    valSetOne.add(color);
                    valSetOne.add(include_latlong);
                    show = true;
                    valSetOne.add("fabSearch");
                    mFragment = FragmentRoomSearchMultiTask.newInstance(title, url_tembak, username, id_rooms_tab, color, include_latlong, MainByonchatRoomBaseActivity.this, "showMultiple");
                }

                if (status.equalsIgnoreCase("1") && show) {
//                    adapter.addFragment(mFragment, title);
                    FragmentManager manager = getSupportFragmentManager();
                    manager.popBackStack();
                    manager.beginTransaction().replace(R.id.container_open_fragment, mFragment).commit();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            next = false;
            if (NetworkInternetConnectionStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
                /*try {
                    Byonchat.getRoomsDB().open();
                    ArrayList<ContactBot> botArrayListist = new ArrayList<>();
                    botArrayListist = Byonchat.getRoomsDB().retrieveRooms("2", true);
                    Byonchat.getRoomsDB().close();

                    if (botArrayListist.size() > 0) {
                        JSONObject jObj = new JSONObject(botArrayListist.get(0).getType());
                        String targetURL = jObj.getString("path");*/

                finish();
                Intent ii = LoadingGetTabRoomActivity.generateIntent(getApplicationContext(), username, targetURL);
                startActivity(ii);
                    /*}
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/

            } else {
                Toast.makeText(MainByonchatRoomBaseActivity.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    protected void resolveFloatingButton() {
        vFloatingButton.setBackgroundTintList(ColorStateList.valueOf(Color
                .parseColor("#" + color)));
        if (next) {
            List value = (List) Constants.map.get(position);
            if (value != null) {
                if (value.get(5).toString().equalsIgnoreCase("show") || value.get(5).toString().equalsIgnoreCase("showMultiple")) {
                    vFloatingButton.show();
                    Intent intent = new Intent(getApplicationContext(), DinamicRoomTaskActivity.class);
                    String action = value.get(0).toString();
                    if (action.equalsIgnoreCase("pos")) {
                        intent = new Intent(getApplicationContext(), RoomPOSdetail.class);
                        intent.putExtra("urlTembak", value.get(6).toString());
                    }
                    intent.putExtra("tt", value.get(0).toString());
                    if (value.size() > 1) {
                        intent.putExtra("uu", value.get(1).toString());
                        intent.putExtra("ii", value.get(2).toString());
                        intent.putExtra("col", value.get(3).toString());
                        intent.putExtra("ll", value.get(4).toString());
                        intent.putExtra("from", value.get(5).toString());
                        intent.putExtra("idTask", "");
                    }
                    showFabIntent(intent);
                } else if (value.get(5).toString().equalsIgnoreCase("showvideo")) {
                    Intent intent = ByonchatVideoBeforeDownloadActivity.generateIntent(getApplicationContext(),
                            username,
                            value.get(2).toString(),
                            value.get(6).toString(),
                            color,
                            colorText);
                    intent.putExtra("tt", value.get(0).toString());
                    showFabIntent(intent);
                } else if (value.get(5).toString().equalsIgnoreCase("fabSearch")) {
                    vFloatingButton.show();
                    Intent intent = new Intent(getApplicationContext(), DinamicRoomSearchTaskActivity.class);
                    /*String action = value.get(0).toString();
                    if (action.equalsIgnoreCase("pos")) {
                        intent = new Intent(getApplicationContext(), RoomPOSdetail.class);
                        intent.putExtra("urlTembak", value.get(6).toString());
                    }*/
                    intent.putExtra("tt", value.get(0).toString());
                    if (value.size() > 1) {
                        intent.putExtra("uu", value.get(1).toString());
                        intent.putExtra("ii", value.get(2).toString());
                        intent.putExtra("col", value.get(3).toString());
                        intent.putExtra("ll", value.get(4).toString());
                        intent.putExtra("from", value.get(5).toString());
                        intent.putExtra("idTask", "");
                    }
                    showFabIntent(intent);
                } else {
                    vFloatingButton.hide();
                }
            } else {
                vFloatingButton.hide();
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        int[] location = new int[2];
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.fabAction);

        button.getLocationOnScreen(location);

        p = new Point();
        p.x = location[0];
        p.y = location[1];
    }

    public void showFabIntent(final Intent intent) {
        String action = intent.getStringExtra("tt").toString();
        if (action.equalsIgnoreCase("pos")) {
            vFloatingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (p != null)
                        showPopup(MainByonchatRoomBaseActivity.this, p, intent);
                }
            });
        } else {
            vFloatingButton.setOnClickListener(v -> startActivity(intent));
        }
    }

    private void showPopup(final Activity context, Point p, final Intent intent) {

        final PopupWindow popup = new PopupWindow(context);

        ArrayList<String> sortList = new ArrayList<String>();
        sortList.add("Order");
        sortList.add("Cancel Order");
        sortList.add("Add Item");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_textview_simple_center,
                sortList);
        ListView listViewSort = new ListView(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            listViewSort.setBackground(getResources().getDrawable(R.drawable.backgroud_white_no_round));
        }
        listViewSort.setAdapter(adapter);

        listViewSort.setOnItemClickListener((parent, view, position, id) -> {
            popup.dismiss();
            if (position == 0) {
                Intent i = new Intent("android.intent.action.MAIN.BynChatPOS").putExtra("some_msg", "order");
                sendBroadcast(i);
            } else if (position == 1) {
                Intent i = new Intent("android.intent.action.MAIN.BynChatPOS").putExtra("some_msg", "cancel");
                sendBroadcast(i);
            } else if (position == 2) {
                startActivity(intent);
                /*Intent i = new Intent("android.intent.action.MAIN.BynChatPOS").putExtra("some_msg", "add");
                sendBroadcast(i);*/
            }
        });

        popup.setFocusable(true);
        popup.setWidth(250);
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setContentView(listViewSort);

        int OFFSET_X = -70;
        int OFFSET_Y = 0;
        popup.showAtLocation(listViewSort, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);
    }

    @TargetApi(21)
    protected void showSearchBar(float positionFromRight) {
        isSearchView = true;
        AnimatorSet set = new AnimatorSet();
        set.setDuration(100).addListener(new com.nineoldandroids.animation.Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(com.nineoldandroids.animation.Animator animation) {
            }

            @Override
            public void onAnimationEnd(com.nineoldandroids.animation.Animator animation) {
                searchEditText.requestFocus();
                if (searchEditText != null) {
                    InputMethodManager imm = (InputMethodManager)
                            searchEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
                }
            }

            @Override
            public void onAnimationCancel(com.nineoldandroids.animation.Animator animation) {

            }

            @Override
            public void onAnimationRepeat(com.nineoldandroids.animation.Animator animation) {

            }
        });
        set.start();

        int cx = vToolbar.getWidth() - (int) (getResources().getDimension(R.dimen.margin48) * (0.5f + positionFromRight));
        int cy = (vToolbar.getTop() + vToolbar.getBottom()) / 2;

        int dx = Math.max(cx, vToolbar.getWidth() - cx);
        int dy = Math.max(cy, vToolbar.getHeight() - cy);
        float finalRadius = (float) Math.hypot(dx, dy);

        final Animator animator;
        animator = ViewAnimationUtils
                .createCircularReveal(searchAppBarLayout, cx, cy, 0, finalRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(200);
        searchAppBarLayout.setVisibility(View.VISIBLE);
        animator.start();

        searchEditText.requestFocus();
        View view = getCurrentFocus();
        InputMethodManager methodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert methodManager != null && view != null;
        methodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    @TargetApi(21)
    protected void hideSearchBar(float positionFromRight) {
        isSearchView = false;
        int cx = vToolbar.getWidth() - (int) (getResources().getDimension(R.dimen.margin48) * (0.5f + positionFromRight));
        int cy = (vToolbar.getTop() + vToolbar.getBottom()) / 2;

        int dx = Math.max(cx, vToolbar.getWidth() - cx);
        int dy = Math.max(cy, vToolbar.getHeight() - cy);
        float finalRadius = (float) Math.hypot(dx, dy);

        Animator animator;
        animator = ViewAnimationUtils
                .createCircularReveal(searchAppBarLayout, cx, cy, finalRadius, 0);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(200);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                searchAppBarLayout.setVisibility(View.GONE);
                if (searchEditText != null) {
                    InputMethodManager imm = (InputMethodManager)
                            searchEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        searchEditText.setText("");
        animator.start();

        vAppbar.setVisibility(View.VISIBLE);
    }

    @NonNull
    protected abstract AppBarLayout getAppBar();

    @NonNull
    protected abstract Toolbar getToolbar();

    @NonNull
    protected abstract RelativeLayout getToolbarBack();

    @NonNull
    protected abstract ImageView getImgToolbarBack();

    @NonNull
    protected abstract TextView getToolbarTitle();

    @NonNull
    protected abstract FrameLayout getFrameFragment();

    @NonNull
    protected abstract MaterialSearchView getMaterialSearchView();

    @NonNull
    protected abstract FloatingActionButton getFloatingButton();

    @NonNull
    protected abstract LinearLayout getFrameSearchAppBar();

    @NonNull
    protected abstract Toolbar getSearchToolbar();

    @NonNull
    protected abstract EditText getSearchForm();

    protected void resolveChatRoom(Bundle savedInstanceState) {
        if (getIntent().hasExtra(EXTRA_ITEM)) {
            listItem = getIntent().getParcelableExtra(EXTRA_ITEM);
            if (listItem == null && savedInstanceState != null) {
                listItem = savedInstanceState.getParcelable(EXTRA_ITEM);
            }

            if (listItem == null) {
                finish();
                return;
            }
        } else {
            username = getIntent().getStringExtra(ConversationActivity.KEY_JABBER_ID);
            position = getIntent().getExtras().getInt(EXTRA_POSITION, 0);
            username = getIntent().getExtras().getString(ConversationActivity.KEY_JABBER_ID);
            color = getIntent().getExtras().getString(EXTRA_COLOR);
            colorText = getIntent().getExtras().getString(EXTRA_COLORTEXT);
            targetURL = getIntent().getExtras().getString(EXTRA_TARGETURL);
            category = getIntent().getExtras().getString(EXTRA_CATEGORY);
            title = getIntent().getExtras().getString(EXTRA_TITLE);
            url_tembak = getIntent().getExtras().getString(EXTRA_URL_TEMBAK);
            id_rooms_tab = getIntent().getExtras().getString(EXTRA_ID_ROOMS_TAB);
            include_pull = getIntent().getExtras().getString(EXTRA_INCLUDE_PULL);
            include_latlong = getIntent().getExtras().getString(EXTRA_INCLUDE_LATLONG);
            status = getIntent().getExtras().getString(EXTRA_STATUS);
            name = getIntent().getExtras().getString(EXTRA_NAME);
            icon = getIntent().getExtras().getString(EXTRA_ICON);
            payload = getIntent().getExtras().getString(EXTRA_VALUE);

            if (username == null && savedInstanceState != null) {
                username = savedInstanceState.getString(ConversationActivity.KEY_JABBER_ID);
                position = savedInstanceState.getInt(EXTRA_POSITION, 0);
                username = savedInstanceState.getString(ConversationActivity.KEY_JABBER_ID);
                color = savedInstanceState.getString(EXTRA_COLOR);
                colorText = savedInstanceState.getString(EXTRA_COLORTEXT);
                targetURL = savedInstanceState.getString(EXTRA_TARGETURL);
                category = savedInstanceState.getString(EXTRA_CATEGORY);
                title = savedInstanceState.getString(EXTRA_TITLE);
                url_tembak = savedInstanceState.getString(EXTRA_URL_TEMBAK);
                id_rooms_tab = savedInstanceState.getString(EXTRA_ID_ROOMS_TAB);
                include_pull = savedInstanceState.getString(EXTRA_INCLUDE_PULL);
                include_latlong = savedInstanceState.getString(EXTRA_INCLUDE_LATLONG);
                status = savedInstanceState.getString(EXTRA_STATUS);
                name = savedInstanceState.getString(EXTRA_NAME);
                icon = savedInstanceState.getString(EXTRA_ICON);
                payload = savedInstanceState.getString(EXTRA_VALUE);
            }

            try {
                List<String> valSetOne = new ArrayList<String>();

                JSONObject jsonObject = new JSONObject(payload);
                JSONArray jsonArray = new JSONArray(jsonObject.getString("payload"));
                for (int i = 0; i < jsonArray.length(); i++) {
                    valSetOne.add(jsonArray.getString(i));
                }
                Constants.map.put(position, valSetOne);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (username == null) {
                finish();
                return;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (getIntent().hasExtra(EXTRA_ITEM)) {
            outState.putParcelable(EXTRA_ITEM, listItem);
        } else {
            outState.putString(ConversationActivity.KEY_JABBER_ID, username);
            outState.putInt(ByonChatMainRoomActivity.EXTRA_POSITION, position);
            outState.putString(ByonChatMainRoomActivity.EXTRA_COLOR, color);
            outState.putString(ByonChatMainRoomActivity.EXTRA_COLORTEXT, colorText);
            outState.putString(ByonChatMainRoomActivity.EXTRA_TARGETURL, targetURL);
            outState.putString(ByonChatMainRoomActivity.EXTRA_CATEGORY, category);
            outState.putString(ByonChatMainRoomActivity.EXTRA_TITLE, title);
            outState.putString(ByonChatMainRoomActivity.EXTRA_URL_TEMBAK, url_tembak);
            outState.putString(ByonChatMainRoomActivity.EXTRA_ID_ROOMS_TAB, id_rooms_tab);
            outState.putString(ByonChatMainRoomActivity.EXTRA_INCLUDE_PULL, include_pull);
            outState.putString(ByonChatMainRoomActivity.EXTRA_INCLUDE_LATLONG, include_latlong);
            outState.putString(ByonChatMainRoomActivity.EXTRA_STATUS, status);
            outState.putString(ByonChatMainRoomActivity.EXTRA_NAME, name);
            outState.putString(ByonChatMainRoomActivity.EXTRA_ICON, icon);
            outState.putString(ByonChatMainRoomActivity.EXTRA_VALUE, payload);
        }
    }
}
