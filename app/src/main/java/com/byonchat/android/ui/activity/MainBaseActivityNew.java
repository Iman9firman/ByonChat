package com.byonchat.android.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.multidex.MultiDex;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.byonchat.android.AdvRecy.DraggableGridExampleAdapter;
import com.byonchat.android.AdvRecy.ItemMain;
import com.byonchat.android.ByonChatMainRoomActivity;
import com.byonchat.android.ConversationActivity;
import com.byonchat.android.ISSActivity.LoginDB.UserDB;
import com.byonchat.android.LoadingGetTabRoomActivity;
import com.byonchat.android.LoginDinamicFingerPrint;
import com.byonchat.android.LoginDinamicRoomActivity;
import com.byonchat.android.LoginISS;
import com.byonchat.android.Manhera.Manhera;
import com.byonchat.android.R;
import com.byonchat.android.RequestPasscodeRoomActivity;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.communication.MyBroadcastReceiver;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.communication.NotificationReceiver;
import com.byonchat.android.communication.WhatsAppJobService;
import com.byonchat.android.curved.CurvedImageView;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.list.BotAdapter;
import com.byonchat.android.local.Byonchat;
import com.byonchat.android.model.SectionSampleItem;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.ContactBot;
import com.byonchat.android.provider.DataBaseDropDown;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.sync.BCServiceSyncAdapter;
import com.byonchat.android.utils.BlurBuilder;
import com.byonchat.android.utils.DialogUtil;
import com.byonchat.android.utils.Fonts;
import com.byonchat.android.utils.HttpHelper;
import com.byonchat.android.utils.LocationAssistant;
import com.byonchat.android.utils.PermanentLoggerUtil;
import com.byonchat.android.utils.RequestKeyTask;
import com.byonchat.android.utils.TaskCompleted;
import com.byonchat.android.utils.UploadService;
import com.byonchat.android.utils.Utility;
import com.byonchat.android.utils.UtilsPD;
import com.byonchat.android.utils.Validations;
import com.byonchat.android.utils.ValidationsKey;
import com.byonchat.android.view.ItemDialog;
import com.byonchat.android.view.UpdateViewDialog;
import com.github.mmin18.widget.RealtimeBlurView;
import com.google.gson.Gson;
import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import eightbitlab.com.blurview.BlurView;
import jp.wasabeef.blurry.Blurry;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
//import me.leolin.shortcutbadger.ShortcutBadger;

import static com.byonchat.android.helpers.Constants.SQL_DELETE_BADGE_TAB_MENU;
import static com.byonchat.android.helpers.Constants.SQL_SELECT_TOTAL_BADGE_TAB_MENU;
import static com.byonchat.android.helpers.Constants.SQL_SELECT_TOTAL_MESSAGES_UNREAD_ALL;
import static com.byonchat.android.helpers.Constants.URL_LAPOR_SELECTED;

public abstract class MainBaseActivityNew extends AppCompatActivity implements /*LocationAssistant.Listener,*/
        AppBarLayout.OnOffsetChangedListener, SwipeRefreshLayout.OnRefreshListener, UpdateViewDialog.changes {

//    @NonNull
//    protected BadgeView bv1;

    @NonNull
    protected CoordinatorLayout root_view;

    @NonNull
    protected CollapsingToolbarLayout collapsingToolbarLayout;

    @NonNull
    protected DrawerLayout drawerLayout;

    @NonNull
    protected NavigationView navigationView;

    @NonNull
    protected ListView vListRooms;

    @NonNull
    protected Toolbar tb;

    @NonNull
    protected ImageView vBtnToolbarSearch;

    @NonNull
    protected TextView vToolbarSearchText;

    @NonNull
    protected FloatingActionButton fab_logo;

    @NonNull
    CounterFab fab_menu_1, fab_menu_2/*, fab_menu_3*/;

    @NonNull
    CounterFab tab_menu_main, tab_menu_21, tab_menu_22, tab_menu_41, tab_menu_42, tab_menu_43, tab_menu_44, tab_menu_91,
            tab_menu_92, tab_menu_93, tab_menu_94, tab_menu_95, tab_menu_96, tab_menu_97, tab_menu_98, tab_menu_99;

    @NonNull
    protected CardView card_search_main;

    @NonNull
    protected LinearLayout vBtnAddRooms;

    @NonNull
    protected ImageView backdropBlur;

    @NonNull
    protected ImageView vImgBlur;

    @NonNull
    protected ImageView vNavLogo;

    @NonNull
    protected TextView vNavTitle;

    @NonNull
    protected ImageView vBtnOpenRooms;

    @NonNull
    protected RelativeLayout vFrameWarning;

    @NonNull
    protected TextView vTxtStatusWarning;

    @NonNull
    protected EditText input_search_main;

    @NonNull
    protected CardView card_menu_main;

    @NonNull
    protected BlurView vBlurView;

    @NonNull
    protected RealtimeBlurView vBlurTopBackground;

    @NonNull
    protected ImageButton but_search_main;

    @NonNull
    protected AppBarLayout appBarLayout;

    @NonNull
    protected CurvedImageView backgroundImage;

    @NonNull
    protected MaterialSearchView searchView;

    @NonNull
    protected RecyclerView recyclerView;

    @NonNull
    protected SwipeRefreshLayout vSwipeRefresh;

    @NonNull
    protected NestedScrollView vFrameTabOne;
    @NonNull
    protected NestedScrollView vFrameTabTwo;
    /*@NonNull
    protected NestedScrollView vFrameTabThree;*/
    @NonNull
    protected NestedScrollView vFrameTabFour;
    /*@NonNull
    protected NestedScrollView vFrameTabFive;
    @NonNull
    protected NestedScrollView vFrameTabSix;
    @NonNull
    protected NestedScrollView vFrameTabSeven;
    @NonNull
    protected NestedScrollView vFrameTabEight;*/
    @NonNull
    protected NestedScrollView vFrameTabNine;

    protected ConstraintLayout vConstraintFiveOne;
    protected ConstraintLayout vConstraintFiveTwo;
    protected ConstraintLayout vConstraintFiveThree;

    protected LinearLayout vFrameGridNineThree;
    protected LinearLayout vFrameGridNineSix;
    protected LinearLayout vFrameGridNineNine;

    protected LinearLayout vFrameGridNineNineOne;
    protected LinearLayout vFrameGridNineNineTwo;
    protected LinearLayout vFrameGridNineNineThree;
    protected LinearLayout vFrameGridNineNineFour;
    protected LinearLayout vFrameGridNineNineFive;
    protected LinearLayout vFrameGridNineNineSix;
    protected LinearLayout vFrameGridNineNineSeven;
    protected LinearLayout vFrameGridNineNineEight;
    protected LinearLayout vFrameGridNineNineNine;

    protected ConstraintLayout vFrameClickTabOne;

    protected LinearLayout vFrameClickTabTwoOne;
    protected LinearLayout vFrameClickTabTwoTwo;

    protected ConstraintLayout vFrameClickTabFourOne;
    protected ConstraintLayout vFrameClickTabFourTwo;
    protected ConstraintLayout vFrameClickTabFourThree;
    protected ConstraintLayout vFrameClickTabFourFour;

    protected ConstraintLayout vFrameClickTabNineOne;
    protected ConstraintLayout vFrameClickTabNineTwo;
    protected ConstraintLayout vFrameClickTabNineThree;
    protected ConstraintLayout vFrameClickTabNineFour;
    protected ConstraintLayout vFrameClickTabNineFive;
    protected ConstraintLayout vFrameClickTabNineSix;
    protected ConstraintLayout vFrameClickTabNineSeven;
    protected ConstraintLayout vFrameClickTabNineEight;
    protected ConstraintLayout vFrameClickTabNineNine;

    @NonNull
    protected ImageView vLogoItemGridOne;
    @NonNull
    protected TextView vTitleItemGridOne;

    @NonNull
    protected ImageView vLogoItemGridTwoOne;
    @NonNull
    protected TextView vTitleItemGridTwoOne;
    @NonNull
    protected ImageView vLogoItemGridTwoTwo;
    @NonNull
    protected TextView vTitleItemGridTwoTwo;

    /*@NonNull
    protected ImageView vLogoItemGridThreeOne;
    @NonNull
    protected TextView vTitleItemGridThreeOne;
    @NonNull
    protected ImageView vLogoItemGridThreeTwo;
    @NonNull
    protected TextView vTitleItemGridThreeTwo;
    @NonNull
    protected ImageView vLogoItemGridThreeThree;
    @NonNull
    protected TextView vTitleItemGridThreeThree;*/

    @NonNull
    protected ImageView vLogoItemGridFourOne;
    @NonNull
    protected TextView vTitleItemGridFourOne;
    @NonNull
    protected ImageView vLogoItemGridFourTwo;
    @NonNull
    protected TextView vTitleItemGridFourTwo;
    @NonNull
    protected ImageView vLogoItemGridFourThree;
    @NonNull
    protected TextView vTitleItemGridFourThree;
    @NonNull
    protected ImageView vLogoItemGridFourFour;
    @NonNull
    protected TextView vTitleItemGridFourFour;

    /*@NonNull
    protected ImageView vLogoItemGridFiveOne;
    @NonNull
    protected TextView vTitleItemGridFiveOne;
    @NonNull
    protected ImageView vLogoItemGridFiveTwo;
    @NonNull
    protected TextView vTitleItemGridFiveTwo;
    @NonNull
    protected ImageView vLogoItemGridFiveThree;
    @NonNull
    protected TextView vTitleItemGridFiveThree;
    @NonNull
    protected ImageView vLogoItemGridFiveFour;
    @NonNull
    protected TextView vTitleItemGridFiveFour;
    @NonNull
    protected ImageView vLogoItemGridFiveFive;
    @NonNull
    protected TextView vTitleItemGridFiveFive;

    @NonNull
    protected ImageView vLogoItemGridSixOne;
    @NonNull
    protected TextView vTitleItemGridSixOne;
    @NonNull
    protected ImageView vLogoItemGridSixTwo;
    @NonNull
    protected TextView vTitleItemGridSixTwo;
    @NonNull
    protected ImageView vLogoItemGridSixThree;
    @NonNull
    protected TextView vTitleItemGridSixThree;
    @NonNull
    protected ImageView vLogoItemGridSixFour;
    @NonNull
    protected TextView vTitleItemGridSixFour;
    @NonNull
    protected ImageView vLogoItemGridSixFive;
    @NonNull
    protected TextView vTitleItemGridSixFive;
    @NonNull
    protected ImageView vLogoItemGridSixSix;
    @NonNull
    protected TextView vTitleItemGridSixSix;

    @NonNull
    protected ImageView vLogoItemGridEightOne;
    @NonNull
    protected TextView vTitleItemGridEightOne;
    @NonNull
    protected ImageView vLogoItemGridEightTwo;
    @NonNull
    protected TextView vTitleItemGridEightTwo;
    @NonNull
    protected ImageView vLogoItemGridEightThree;
    @NonNull
    protected TextView vTitleItemGridEightThree;
    @NonNull
    protected ImageView vLogoItemGridEightFour;
    @NonNull
    protected TextView vTitleItemGridEightFour;
    @NonNull
    protected ImageView vLogoItemGridEightFive;
    @NonNull
    protected TextView vTitleItemGridEightFive;
    @NonNull
    protected ImageView vLogoItemGridEightSix;
    @NonNull
    protected TextView vTitleItemGridEightSix;
    @NonNull
    protected ImageView vLogoItemGridEightSeven;
    @NonNull
    protected TextView vTitleItemGridEightSeven;
    @NonNull
    protected ImageView vLogoItemGridEightEight;
    @NonNull
    protected TextView vTitleItemGridEightEight;*/

    @NonNull
    protected ImageView vLogoItemGridNineOne;
    @NonNull
    protected TextView vTitleItemGridNineOne;
    @NonNull
    protected ImageView vLogoItemGridNineTwo;
    @NonNull
    protected TextView vTitleItemGridNineTwo;
    @NonNull
    protected ImageView vLogoItemGridNineThree;
    @NonNull
    protected TextView vTitleItemGridNineThree;
    @NonNull
    protected ImageView vLogoItemGridNineFour;
    @NonNull
    protected TextView vTitleItemGridNineFour;
    @NonNull
    protected ImageView vLogoItemGridNineFive;
    @NonNull
    protected TextView vTitleItemGridNineFive;
    @NonNull
    protected ImageView vLogoItemGridNineSix;
    @NonNull
    protected TextView vTitleItemGridNineSix;
    @NonNull
    protected ImageView vLogoItemGridNineSeven;
    @NonNull
    protected TextView vTitleItemGridNineSeven;
    @NonNull
    protected ImageView vLogoItemGridNineEight;
    @NonNull
    protected TextView vTitleItemGridNineEight;
    @NonNull
    protected ImageView vLogoItemGridNineNine;
    @NonNull
    protected TextView vTitleItemGridNineNine;

    protected RecyclerView.LayoutManager layoutManager;
    protected RecyclerViewDragDropManager recyclerViewDragDropManager;

    protected ProgressDialog progressDialog;
    protected boolean isRecyclerViewShowed = true;

    protected List<ItemMain> itemList = new ArrayList<>();
    protected List<ItemMain> subItemList = new ArrayList<>();
    protected List<String> positionList = new ArrayList<>();

    protected LaporSelectedRoom laporSelectedRoom;
    //    protected LocationAssistant assistant;
    protected Fonts fonts = new Fonts();

    protected DraggableGridExampleAdapter adapter;
    protected BotAdapter mAdapterRoomList;
    protected RecyclerView.Adapter wrappedAdapter;

    protected UploadService mUploadService;
    protected Intent mServiceIntent;

    protected ArrayList<ContactBot> botArrayLististPrimary = new ArrayList<ContactBot>();
    protected ArrayList<ContactBot> botArrayListist = new ArrayList<ContactBot>();
    protected ArrayList<ContactBot> contactBotsShortcut = new ArrayList<ContactBot>();
    protected List<String> numbers = new ArrayList<>();

    protected BroadcastHandler broadcastHandler = new BroadcastHandler();
    protected static final String ACTION_REFRESH_BADGER = MainBaseActivityNew.class
            .getName() + ".refreshBadger";
    protected static final String ACTION_REFRESH_NOTIF = MainBaseActivityNew.class
            .getName() + ".refreshNotif";
//    protected static int TAG_CODE_PERMISSION_LOCATION = 77;

    public static Activity mActivity;
    protected boolean isVisible = false;
    protected float radius = 3f;
    protected String protect = "";
    protected String targetURL = "";
    protected String success;
    protected String username;
    protected String title;
    protected String image_url = "";
    protected String percent;
    protected String color;
    protected String colorText;
    protected String colorForeground;
    protected String extra_tab = "";
    protected String extra_grid_size = "";
    protected int resourceAdapterId;
    protected int logo;
    protected int background;
    protected int room_id;
    protected String roomid = "";
    protected int i = 0;
    protected ProgressDialog dialog;

    public boolean openDialog = true;

    //    Boolean loginIss = true;
    protected SQLiteDatabase sqLiteDatabase;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onSetStatusBarColor();
        setContentView(getResourceLayout());
        onSetupRoom();
        onLoadView();
        onViewReady(savedInstanceState);
    }

    protected void onSetStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.statusBackground));
        }
    }

    protected abstract int getResourceLayout();

    protected abstract void onLoadView();

    protected abstract void onSetupRoom();

    protected void applyChatConfig() {
        /*assistant = new LocationAssistant(this, this, LocationAssistant.Accuracy.HIGH, 5000, false);
        assistant.setVerbose(true);*/

        if (getIntent().getExtras() != null) {
            if (!getIntent().hasExtra(Constants.EXTRA_ROOM)) {
                success = getIntent().getStringExtra("success");
            }
        }

        image_url = "";
        title = "ByonChat";
        logo = R.drawable.logo_byon;
        background = R.drawable.byonchat_room;
        percent = "70";
        color = "006b9c";
        colorText = "FFFFFF";
        room_id = 1;

        resolveRecyclerView();
        resolveNavHeader();
    }

    protected void onViewReady(Bundle savedInstanceState) {
        applyChatConfig();
    }

    protected void resolveToolbar(ContactBot contactBot) {
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(title);
        collapsingToolbarLayout.setTitle(title);

        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        Cursor cur = Byonchat.getBotListDB().getSingleRoom(botArrayListist.get(0).name);
        if (cur.getCount() > 0) {
            String bcakdrop = cur.getString(cur.getColumnIndex(BotListDB.ROOM_BACKDROP));

            if (bcakdrop == null || bcakdrop.equalsIgnoreCase("") || bcakdrop.equalsIgnoreCase("null")) {
                Manhera.getInstance().get()
                        .load(background)
                        .fitCenter()
                        .into(backgroundImage);
            } else {
                Manhera.getInstance().get()
                        .load(bcakdrop)
                        .fitCenter()
                        .into(backgroundImage);
            }
            resolveListTabRooms(botArrayListist.get(0), cur);
        } else {

        }

        resolveCollapsingToolbar(color);
    }

    protected void resolveListRooms() {
        Byonchat.getRoomsDB().open();
        botArrayLististPrimary = Byonchat.getRoomsDB().retrieveRooms("2", true);
        botArrayListist = Byonchat.getRoomsDB().retrieveRooms("2", false);
        Byonchat.getRoomsDB().close();

        if (botArrayLististPrimary.size() > 0 && botArrayListist.size() == 0) {
            if (botArrayLististPrimary.size() > 0) {
                resolveNavHeader();
                refreshList();

                vTxtStatusWarning.setVisibility(View.GONE);
                vFrameWarning.setVisibility(View.INVISIBLE);
                vBtnAddRooms.setVisibility(View.INVISIBLE);

                vBtnOpenRooms.setVisibility(View.INVISIBLE);
                card_search_main.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        } else {
            if (botArrayListist.size() == 1) {
                resolveNavHeader();
                refreshList();
                vTxtStatusWarning.setVisibility(View.GONE);
                vFrameWarning.setVisibility(View.INVISIBLE);
                vBtnAddRooms.setVisibility(View.INVISIBLE);

                vBtnOpenRooms.setVisibility(View.VISIBLE);
                card_search_main.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            } else if (botArrayListist.size() > 1) {
                resolveNavHeader();
                refreshList();

                vTxtStatusWarning.setVisibility(View.GONE);
                vFrameWarning.setVisibility(View.INVISIBLE);
                vBtnAddRooms.setVisibility(View.INVISIBLE);

                vBtnOpenRooms.setVisibility(View.VISIBLE);
                card_search_main.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            } else {
                vTxtStatusWarning.setVisibility(View.VISIBLE);
                vFrameWarning.setVisibility(View.VISIBLE);
                vBtnAddRooms.setVisibility(View.VISIBLE);

                vBtnOpenRooms.setVisibility(View.INVISIBLE);
                card_search_main.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);

                isRecyclerViewShowed = false;
            }
        }
    }

    protected void resolveOpenRooms() {
        resolveNavMenu(true);

        vBtnOpenRooms.setOnClickListener(v -> {
            roomsOpened();
        });
    }

    protected void resolveRefreshGrid() {
        adapter.getFilter().filter("");
    }

    protected void resolveShowRecyclerView() {
        recyclerView.setVisibility(isRecyclerViewShowed ? View.VISIBLE : View.INVISIBLE);
    }

    protected void roomsOpened() {
        if (vListRooms.getVisibility() == View.GONE) {
            resolveNavMenu(false);
        } else {
            resolveNavMenu(true);
        }
    }

    protected void resolveNavMenu(boolean isTrue) {
        vBtnOpenRooms.setImageDrawable(isTrue ? getResources().getDrawable(R.drawable.ico_arrow_down) : getResources().getDrawable(R.drawable.ico_arrow_up));
        vListRooms.setVisibility(isTrue ? View.GONE : View.VISIBLE);

        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_item_one).setVisible(isTrue);
        nav_Menu.findItem(R.id.nav_item_two).setVisible(isTrue);
        nav_Menu.findItem(R.id.nav_item_three).setVisible(isTrue);
        nav_Menu.findItem(R.id.nav_item_four).setVisible(isTrue);
        nav_Menu.findItem(R.id.nav_item_refresh).setVisible(false);
        nav_Menu.findItem(R.id.nav_item_create_shortcut).setVisible(isTrue);
        nav_Menu.findItem(R.id.nav_item_legal).setVisible(false);

        if (title.equalsIgnoreCase("ISS INDONESIA")) {
            nav_Menu.findItem(R.id.nav_logout_button).setVisible(isTrue);
        }
        Cursor cur = Byonchat.getBotListDB().getSingleRoom(username);
        if (cur.getCount() > 0) {
            String content = cur.getString(cur.getColumnIndex(BotListDB.ROOM_CONTENT));
            try {
                JSONArray jsonArray = new JSONArray(content);
                if (jsonArray.length() < 9)
                    nav_Menu.findItem(R.id.nav_item_grid_size).setVisible(false);
                else
                    nav_Menu.findItem(R.id.nav_item_grid_size).setVisible(isTrue);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    protected void refreshList() {
        new Handler(Looper.getMainLooper()).post(() -> {
            mAdapterRoomList = new BotAdapter(this, botArrayListist, true);
            Byonchat.getRoomsDB().open();
            botArrayListist = Byonchat.getRoomsDB().retrieveRooms("2", false);
            Byonchat.getRoomsDB().close();
            mAdapterRoomList = new BotAdapter(this, botArrayListist, true);
            vListRooms.setAdapter(mAdapterRoomList);
        });

        vListRooms.setClickable(true);
        vListRooms.setOnItemClickListener((a, v, position, id) -> {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.commit();

            drawerLayout.closeDrawer(Gravity.START);

            ContactBot item = botArrayListist.get(position);
            Byonchat.getRoomsDB().open();
            Byonchat.getRoomsDB().updateActiveRooms(item);
            Byonchat.getRoomsDB().close();

            resolveNavHeader();
            refreshList();
        });

        vListRooms.setOnItemLongClickListener((parent, view, position, id) -> {
            roomid = botArrayListist.get(position).getName();
            final Dialog dialogConfirmation;
            dialogConfirmation = DialogUtil.customDialogConversationConfirmation(MainBaseActivityNew.this);
            dialogConfirmation.show();

            TextView txtConfirmation = (TextView) dialogConfirmation.findViewById(R.id.confirmationTxt);
            TextView descConfirmation = (TextView) dialogConfirmation.findViewById(R.id.confirmationDesc);
            txtConfirmation.setText("Delete Confirmation");
            descConfirmation.setVisibility(View.VISIBLE);
            descConfirmation.setText("Do you want to delete this room?");

            Button btnNo = (Button) dialogConfirmation.findViewById(R.id.btnNo);
            Button btnYes = (Button) dialogConfirmation.findViewById(R.id.btnYes);

            btnNo.setText("Cancel");
            btnNo.setOnClickListener(v -> {
                dialogConfirmation.dismiss();
            });

            btnYes.setText("Delete");
            btnYes.setOnClickListener(v -> {
                requestKey();
                dialogConfirmation.dismiss();
            });
            return true;
        });
    }

    protected void resolveNavHeader() {
        Byonchat.getRoomsDB().open();
        botArrayListist = Byonchat.getRoomsDB().retrieveRooms("2", true);
        Byonchat.getRoomsDB().close();

        if (botArrayListist.size() > 0) {
            vBtnToolbarSearch.setVisibility(View.VISIBLE);

            Manhera.getInstance()
                    .get()
                    .load(botArrayListist.get(0).link)
                    .fitCenter()
                    .into(vNavLogo);

            vNavTitle.setText(botArrayListist.get(0).realname);
            vToolbarSearchText.setText(botArrayListist.get(0).realname);

            title = botArrayListist.get(0).realname;
            username = botArrayListist.get(0).getName();

            resolveToolbar(botArrayListist.get(0));
        } else {
            vBtnToolbarSearch.setVisibility(View.GONE);

            Manhera.getInstance()
                    .get()
                    .load(R.drawable.logo_byon)
                    .fitCenter()
                    .into(vNavLogo);

            vNavTitle.setText("ByonChat");
            vToolbarSearchText.setText("ByonChat");

            Manhera.getInstance().get()
                    .load(background)
                    .fitCenter()
                    .into(backgroundImage);
        }

        BitmapDrawable d = (BitmapDrawable) vImgBlur.getDrawable();
        Bitmap b = d.getBitmap();
        Blurry.with(getApplicationContext())
                .radius(10)
                .sampling(10)
                .from(b)
                .into(vImgBlur);
    }

    protected void resolveRecyclerView() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs != null) {
            extra_grid_size = prefs.getString(Constants.EXTRA_GRID_SIZE, Constants.EXTRA_GRID_SIZE_THREE);
            if (extra_grid_size.equalsIgnoreCase(Constants.EXTRA_GRID_SIZE_THREE)) {
                resourceAdapterId = R.layout.list_grid_item;
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    layoutManager = new GridLayoutManager(this, 3, RecyclerView.VERTICAL, false);
                } else {
                    layoutManager = new GridLayoutManager(this, 5, RecyclerView.VERTICAL, false);
                }
            } else {
                resourceAdapterId = R.layout.list_grid_item_four;
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    layoutManager = new GridLayoutManager(this, 4, RecyclerView.VERTICAL, false);
                } else {
                    layoutManager = new GridLayoutManager(this, 7, RecyclerView.VERTICAL, false);
                }
            }
        } else {
            resourceAdapterId = R.layout.list_grid_item;
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                layoutManager = new GridLayoutManager(this, 3, RecyclerView.VERTICAL, false);
            } else {
                layoutManager = new GridLayoutManager(this, 5, RecyclerView.VERTICAL, false);
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
                    //deprecated in API 26
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
            ItemMain im = itemList.get(position);
            intentTabMenu(im, position);
        });

        adapter.setOnLongItemClickListener((view, position) -> {
            showToastTab(adapter.getData().get(position).tab_name);
        });
    }

    protected void resolveListTabRooms(ContactBot item, Cursor sdf) {
        Cursor cur = Byonchat.getBotListDB().getSingleRoom(username);
        String name = cur.getString(cur.getColumnIndex(BotListDB.ROOM_REALNAME));
        color = Utility.jsonResultType(cur.getString(cur.getColumnIndex(BotListDB.ROOM_COLOR)), "a")
                .equalsIgnoreCase("null")
                || Utility.jsonResultType(cur.getString(cur.getColumnIndex(BotListDB.ROOM_COLOR)), "a")
                .equalsIgnoreCase("") ? color :
                Utility.jsonResultType(cur.getString(cur.getColumnIndex(BotListDB.ROOM_COLOR)), "a");
        colorText = Utility.jsonResultType(cur.getString(cur.getColumnIndex(BotListDB.ROOM_COLOR)), "b")
                .equalsIgnoreCase("null")
                || Utility.jsonResultType(cur.getString(cur.getColumnIndex(BotListDB.ROOM_COLOR)), "b")
                .equalsIgnoreCase("") ? color :
                Utility.jsonResultType(cur.getString(cur.getColumnIndex(BotListDB.ROOM_COLOR)), "b");
        String description = Utility.jsonResultType(cur.getString(cur.getColumnIndex(BotListDB.ROOM_COLOR)), "c");
        String targetURL = Utility.jsonResultType(cur.getString(cur.getColumnIndex(BotListDB.ROOM_COLOR)), "e");
        String content = cur.getString(cur.getColumnIndex(BotListDB.ROOM_CONTENT));
        String icon = cur.getString(cur.getColumnIndex(BotListDB.ROOM_ICON));
        String bcakdrop = cur.getString(cur.getColumnIndex(BotListDB.ROOM_BACKDROP));

        protect = Utility.jsonResultType(cur.getString(cur.getColumnIndex(BotListDB.ROOM_COLOR)), "p");

        if (Utility.jsonResultType(cur.getString(cur.getColumnIndex(BotListDB.ROOM_COLOR)), "a").equalsIgnoreCase("error")) {
            if (NetworkInternetConnectionStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
                finish();
                Intent ii = LoadingGetTabRoomActivity.generateIntent(getApplicationContext(), username, null);
                startActivity(ii);
            } else {
                Toast.makeText(MainBaseActivityNew.this, "No Internet Akses", Toast.LENGTH_SHORT).show();
                finish();
            }
            return;
        }

        String current = "";
        if (current.equalsIgnoreCase("")) {
            current = cur.getString(cur.getColumnIndex(BotListDB.ROOM_FIRST_TAB));
        }

        if (bcakdrop == null
                || bcakdrop.equalsIgnoreCase("")
                || bcakdrop.equalsIgnoreCase("null")) {
            Manhera.getInstance().get()
                    .load(background)
                    .fitCenter()
                    .into(backgroundImage);
        }

        if (bcakdrop != null
                || !bcakdrop.equalsIgnoreCase("")
                || !bcakdrop.equalsIgnoreCase("null"))
            new LoadImageFromURL(backdropBlur).execute(bcakdrop);

        try {
            itemList.clear();
            positionList.clear();
            subItemList.clear();
            boolean grouping = false;
            String category_sub = "";

            JSONArray jsonArray;
            if (content.startsWith("JSONnnye")) {

                if (!new ValidationsKey().getInstance(getApplicationContext()).setKeyValue(30, getResources().getString(R.string.app_version))) {
                    new Validations().getInstance(getApplicationContext()).removeById(26);
                    new Validations().getInstance(getApplicationContext()).removeById(27);
                    new Validations().getInstance(getApplicationContext()).removeById(28);
                    new Validations().getInstance(getApplicationContext()).removeById(29);

                    Intent a = new Intent(getApplicationContext(), LoginISS.class);
                    a.putExtra(ConversationActivity.KEY_JABBER_ID, username);
                    a.putExtra(ConversationActivity.KEY_TITLE, "waiting");
                    startActivity(a);
                    finish();
                    return;
                }

                content = content.replace("JSONnnye", "");

                String[] bagibagi = content.split("@@@");
                JSONArray jAAr = new JSONArray(bagibagi[1]);
                for (int on = 0; on < jAAr.length(); on++) {
                    JSONObject jsonObject = /*new JSONObject(bagibagi[1]);*/jAAr.getJSONObject(on);
                    String title1 = jsonObject.getString("name");
                    String icon_name1 = jsonObject.getString("icon_name");
                    String member = "";
                    if (jsonObject.has("section")) {
                        member = jsonObject.getString("section");
                        category_sub = "sla";
                    } else if (jsonObject.has("members")) {
                        member = jsonObject.getString("members");
                        category_sub = "non_sla";
                    }


                    ItemMain itemMain1 = new ItemMain(i, category_sub, title1, null, null,
                            username, null, color, colorText, null, null,
                            member, name, icon, icon_name1);

                    itemList.add(itemMain1);
                    positionList.add(title1);
                }

                jsonArray = new JSONArray(bagibagi[0]);
                grouping = true;
            } else {
                jsonArray = new JSONArray(content);
                grouping = false;
            }

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            if (prefs != null) {
                extra_tab = prefs.getString(Constants.EXTRA_TAB_MOVEMENT, "");
                if (!extra_tab.equalsIgnoreCase("")) {
                    jsonArray = new JSONArray(extra_tab);
                }
            }

            for (int i = 0; i < jsonArray.length(); i++) {
                String category = jsonArray.getJSONObject(i).getString("category_tab").toString();
                String title = jsonArray.getJSONObject(i).getString("tab_name").toString();
                String include_latlong = jsonArray.getJSONObject(i).getString("include_latlong").toString();
                String include_pull = jsonArray.getJSONObject(i).getString("include_pull").toString();
                String url_tembak = jsonArray.getJSONObject(i).getString("url_tembak").toString();
                String id_rooms_tab = jsonArray.getJSONObject(i).getString("id_rooms_tab").toString();
                String status = jsonArray.getJSONObject(i).getString("status").toString();
                String icon_name = "";
                if (jsonArray.getJSONObject(i).has("icon_name")) {
                    icon_name = jsonArray.getJSONObject(i).getString("icon_name").toString();
                }

                if (!extra_tab.equalsIgnoreCase("")) {
                    username = jsonArray.getJSONObject(i).getString("username");
                    color = jsonArray.getJSONObject(i).getString("color");
                    colorText = jsonArray.getJSONObject(i).getString("colorText");
                    targetURL = jsonArray.getJSONObject(i).getString("targetURL");
                    name = jsonArray.getJSONObject(i).getString("name");
                    icon = jsonArray.getJSONObject(i).getString("icon");
                    icon_name = jsonArray.getJSONObject(i).getString("icon_name");
                }

                ItemMain itemMain = new ItemMain(i, category, title, url_tembak, include_pull,
                        username, id_rooms_tab, color, colorText, targetURL, include_latlong,
                        status, name, icon, icon_name);

                if (category.equalsIgnoreCase("1")) {
                    Constants.map.put(i, null);
                    itemMain.iconTest = R.drawable.ic_001;
                } else if (category.equalsIgnoreCase("2")) {
                    Constants.map.put(i, null);
                    itemMain.iconTest = R.drawable.ic_029;
                } else if (category.equalsIgnoreCase("3")) {
                    Constants.map.put(i, null);
                    itemMain.iconTest = R.drawable.ic_024;
                } else if (category.equalsIgnoreCase("4")) {
                    itemMain.iconTest = R.drawable.ic_003;
                    if (include_pull.equalsIgnoreCase("1") || include_pull.equalsIgnoreCase("3")) {
                        List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(title);
                        valSetOne.add(username);
                        valSetOne.add(jsonArray.getJSONObject(i).getString("id_rooms_tab").toString());
                        valSetOne.add(color);
                        valSetOne.add(include_latlong);
                        valSetOne.add("hide");
                        Constants.map.put(i, valSetOne);
                    } else if (include_pull.equalsIgnoreCase("0")) {
                        List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(title);
                        valSetOne.add(username);
                        valSetOne.add(jsonArray.getJSONObject(i).getString("id_rooms_tab").toString());
                        valSetOne.add(color);
                        valSetOne.add(include_latlong);
                        valSetOne.add("show");
                        Constants.map.put(i, valSetOne);
                    } else if (include_pull.equalsIgnoreCase("2")) {
                        Constants.map.put(i, null);
                    } else if (include_pull.equalsIgnoreCase("4") || include_pull.equalsIgnoreCase("5")) {
                        List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(title);
                        valSetOne.add(username);
                        valSetOne.add(jsonArray.getJSONObject(i).getString("id_rooms_tab").toString());
                        valSetOne.add(color);
                        valSetOne.add(include_latlong);
                        valSetOne.add("hideMultiple");
                        Constants.map.put(i, valSetOne);
                    } else if (include_pull.equalsIgnoreCase("6")) {
                        JSONObject jsonRootObject = new JSONObject(jsonArray.getJSONObject(i).getString("url_tembak").toString());
                        List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(title);
                        valSetOne.add(username);
                        valSetOne.add(jsonArray.getJSONObject(i).getString("id_rooms_tab").toString());
                        valSetOne.add(color);
                        valSetOne.add(include_latlong);
                        valSetOne.add("showMultiple");
                        Constants.map.put(i, valSetOne);
                    } else if (include_pull.equalsIgnoreCase("7")) {
                        JSONObject jsonRootObject = new JSONObject(jsonArray.getJSONObject(i).getString("url_tembak").toString());
                        List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(title);
                        valSetOne.add(username);
                        valSetOne.add(jsonArray.getJSONObject(i).getString("id_rooms_tab").toString());
                        valSetOne.add(color);
                        valSetOne.add(include_latlong);
                        valSetOne.add("hideMultiple");
                        Constants.map.put(i, valSetOne);
                    }
                } else if (category.equalsIgnoreCase("5")) {
                    itemMain.iconTest = R.drawable.ic_028;
                    Constants.map.put(i, null);
                } else if (category.equalsIgnoreCase("14")) {
                    itemMain.iconTest = R.drawable.ic_040;
                    List<String> valSetOne = new ArrayList<String>();
                    valSetOne.add(title);
                    valSetOne.add(username);
                    valSetOne.add(jsonArray.getJSONObject(i).getString("id_rooms_tab").toString());
                    valSetOne.add(color);
                    valSetOne.add(include_latlong);
                    valSetOne.add("hide");
                    Constants.map.put(i, valSetOne);
                } else if (category.equalsIgnoreCase("6")) {
                    itemMain.iconTest = R.drawable.ic_040;
                    Constants.map.put(i, null);
                } else if (category.equalsIgnoreCase("7")) {
                    itemMain.iconTest = R.drawable.ic_002;
                    Constants.map.put(i, null);
                } else if (category.equalsIgnoreCase("8")) {
                    itemMain.iconTest = R.drawable.ic_038;
                    Constants.map.put(i, null);
                } else if (category.equalsIgnoreCase("9")) {
                    itemMain.iconTest = R.drawable.ic_045;
                    Constants.map.put(i, null);
                } else if (category.equalsIgnoreCase("10")) {
                    itemMain.iconTest = R.drawable.ic_037;
                    Constants.map.put(i, null);
                } else if (category.equalsIgnoreCase("11")) {
                    itemMain.iconTest = R.drawable.ic_017;
                    List<String> valSetOne = new ArrayList<String>();
                    valSetOne.add("pos");
                    valSetOne.add(username);
                    valSetOne.add(jsonArray.getJSONObject(i).getString("id_rooms_tab").toString());
                    valSetOne.add(color);
                    valSetOne.add(include_latlong);
                    valSetOne.add("show");
                    valSetOne.add(jsonArray.getJSONObject(i).getString("url_tembak").toString());
                    Constants.map.put(i, valSetOne);
                } else if (category.equalsIgnoreCase("15")) {
                    itemMain.iconTest = R.drawable.ic_024;
                    List<String> valSetOne = new ArrayList<String>();
                    valSetOne.add("btube");
                    valSetOne.add(username);
                    valSetOne.add(jsonArray.getJSONObject(i).getString("id_rooms_tab").toString());
                    valSetOne.add(color);
                    valSetOne.add(include_latlong);
                    valSetOne.add("showvideo");
                    valSetOne.add(jsonArray.getJSONObject(i).getString("url_tembak").toString());
                    Constants.map.put(i, valSetOne);
                } else if (category.equalsIgnoreCase("16")) {
                    itemMain.iconTest = R.drawable.ic_040;
                    List<String> valSetOne = new ArrayList<String>();
                    valSetOne.add(title);
                    valSetOne.add(username);
                    valSetOne.add(jsonArray.getJSONObject(i).getString("id_rooms_tab").toString());
                    valSetOne.add(color);
                    valSetOne.add(include_latlong);
                    valSetOne.add("hide");
                    Constants.map.put(i, valSetOne);
                } else if (category.equalsIgnoreCase("17")) {
                    itemMain.iconTest = R.drawable.ic_012;
                    List<String> valSetOne = new ArrayList<String>();
                    valSetOne.add(title);
                    valSetOne.add(username);
                    valSetOne.add(jsonArray.getJSONObject(i).getString("id_rooms_tab").toString());
                    valSetOne.add(color);
                    valSetOne.add(include_latlong);
                    valSetOne.add("hide");
                    Constants.map.put(i, valSetOne);
                } else if (category.equalsIgnoreCase("18")) {
                    itemMain.iconTest = R.drawable.ic_012;
                    List<String> valSetOne = new ArrayList<String>();
                    valSetOne.add(title);
                    valSetOne.add(username);
                    valSetOne.add(jsonArray.getJSONObject(i).getString("id_rooms_tab").toString());
                    valSetOne.add(color);
                    valSetOne.add(include_latlong);
                    valSetOne.add("hide");
                    Constants.map.put(i, valSetOne);
                } else if (category.equalsIgnoreCase("19")) {
//                    loginIss = true;
                    itemMain.iconTest = R.drawable.ic_040;
                    List<String> valSetOne = new ArrayList<String>();
                    valSetOne.add(title);
                    valSetOne.add(username);
                    valSetOne.add(jsonArray.getJSONObject(i).getString("id_rooms_tab").toString());
                    valSetOne.add(color);
                    valSetOne.add(include_latlong);
                    valSetOne.add("hide");
                    Constants.map.put(i, valSetOne);
                } else if (category.equalsIgnoreCase("20")) {
//                    loginIss = true;
                    itemMain.iconTest = R.drawable.ic_015;
                    List<String> valSetOne = new ArrayList<String>();
                    valSetOne.add(title);
                    valSetOne.add(username);
                    valSetOne.add(jsonArray.getJSONObject(i).getString("id_rooms_tab").toString());
                    valSetOne.add(color);
                    valSetOne.add(include_latlong);
                    valSetOne.add("fabSearch");
                    Constants.map.put(i, valSetOne);
                } else if (category.equalsIgnoreCase("21")) {
                    itemMain.iconTest = R.drawable.ic_015;
                    List<String> valSetOne = new ArrayList<String>();
                    valSetOne.add(title);
                    valSetOne.add(username);
                    valSetOne.add(jsonArray.getJSONObject(i).getString("id_rooms_tab").toString());
                    valSetOne.add(color);
                    valSetOne.add(include_latlong);
                    valSetOne.add("hide");
                    Constants.map.put(i, valSetOne);
                } else if (category.equalsIgnoreCase("22")) {
                    itemMain.iconTest = R.drawable.ic_015;
                    List<String> valSetOne = new ArrayList<String>();
                    valSetOne.add(title);
                    valSetOne.add(username);
                    valSetOne.add(jsonArray.getJSONObject(i).getString("id_rooms_tab").toString());
                    valSetOne.add(color);
                    valSetOne.add(include_latlong);
                    valSetOne.add("hide");
                    Constants.map.put(i, valSetOne);
                } else if (category.equalsIgnoreCase("23")) {
                    itemMain.iconTest = R.drawable.ic_008;
                    List<String> valSetOne = new ArrayList<String>();
                    valSetOne.add(title);
                    valSetOne.add(username);
                    valSetOne.add(jsonArray.getJSONObject(i).getString("id_rooms_tab").toString());
                    valSetOne.add(color);
                    valSetOne.add(include_latlong);
                    valSetOne.add("hide");
                    Constants.map.put(i, valSetOne);
                } else if (category.equalsIgnoreCase("24")) {
                    itemMain.iconTest = R.drawable.ic_015;
                    List<String> valSetOne = new ArrayList<String>();
                    valSetOne.add(title);
                    valSetOne.add(username);
                    valSetOne.add(jsonArray.getJSONObject(i).getString("id_rooms_tab").toString());
                    valSetOne.add(color);
                    valSetOne.add(include_latlong);
                    valSetOne.add("hide");
                    Constants.map.put(i, valSetOne);
                } else if (category.equalsIgnoreCase("26")) {
                    itemMain.iconTest = R.drawable.ic_015;
                    List<String> valSetOne = new ArrayList<String>();
                    valSetOne.add(title);
                    valSetOne.add(username);
                    valSetOne.add(jsonArray.getJSONObject(i).getString("id_rooms_tab").toString());
                    valSetOne.add(color);
                    valSetOne.add(include_latlong);
                    valSetOne.add("hide");
                    Constants.map.put(i, valSetOne);
                } else if (category.equalsIgnoreCase("27")) {
                    itemMain.iconTest = R.drawable.ic_028;
                    List<String> valSetOne = new ArrayList<String>();
                    valSetOne.add(title);
                    valSetOne.add(username);
                    valSetOne.add(jsonArray.getJSONObject(i).getString("id_rooms_tab").toString());
                    valSetOne.add(color);
                    valSetOne.add(include_latlong);
                    valSetOne.add("hide");
                    Constants.map.put(i, valSetOne);
                } else if (category.equalsIgnoreCase("28")) {
                    itemMain.iconTest = R.drawable.ic_012;
                    List<String> valSetOne = new ArrayList<String>();
                    valSetOne.add(title);
                    valSetOne.add(username);
                    valSetOne.add(jsonArray.getJSONObject(i).getString("id_rooms_tab").toString());
                    valSetOne.add(color);
                    valSetOne.add(include_latlong);
                    valSetOne.add("hide");
                    Constants.map.put(i, valSetOne);
                } else if (category.equalsIgnoreCase("30")) {
                    List<String> valSetOne = new ArrayList<String>();
                    valSetOne.add(title);
                    valSetOne.add(username);
                    valSetOne.add(jsonArray.getJSONObject(i).getString("id_rooms_tab").toString());
                    valSetOne.add(color);
                    valSetOne.add(include_latlong);
                    valSetOne.add("hide");
                    Constants.map.put(i, valSetOne);
                } else if (category.equalsIgnoreCase("31")) {
                    itemMain.iconTest = R.drawable.ic_046;
                    List<String> valSetOne = new ArrayList<String>();
                    valSetOne.add(title);
                    valSetOne.add(username);
                    valSetOne.add(jsonArray.getJSONObject(i).getString("id_rooms_tab").toString());
                    valSetOne.add(color);
                    valSetOne.add(include_latlong);
                    valSetOne.add("hide");
                    Constants.map.put(i, valSetOne);
                }

                if (grouping == true) {
                    itemList.add(itemMain);
                    positionList.add(title);
                    for (int s1 = 0; s1 < itemList.size(); s1++) {
                        try {
                            JSONArray jsonArrow = new JSONArray(itemList.get(s1).status);
                            String category_sub1 = itemList.get(s1).category_tab;
                            for (int i1 = 0; i1 < jsonArrow.length(); i1++) {
                                if (category_sub1.equalsIgnoreCase("sla")) {
                                    JSONObject jsonObject = jsonArrow.getJSONObject(i1);
                                    JSONArray jsonArray1 = jsonObject.getJSONArray("members");
                                    for (int i2 = 0; i2 < jsonArray1.length(); i2++) {
                                        String id_tab_dftared = jsonArray1.getString(i2);
                                        if (id_rooms_tab.equalsIgnoreCase(id_tab_dftared)) {
                                            itemList.remove(itemMain);
                                        }
                                    }
                                } else {
                                    String id_tab_dftared = jsonArrow.getString(i1);
                                    if (id_tab_dftared.equalsIgnoreCase(id_rooms_tab)) {
                                        itemList.remove(itemMain);
                                    }

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    subItemList.add(itemMain);
                } else {
                    itemList.add(i, itemMain);
                    positionList.add(i, title);
                }

            }

            for (int i = 0; i < itemList.size(); i++) {
                if (itemList.size() == 1) {
                    resolveOneGrid(itemList);
                } else if (itemList.size() == 2) {
                    if (i == 0)
                        resolveTwoGridOne(itemList, i);
                    else
                        resolveTwoGridTwo(itemList, i);
                } else if (itemList.size() == 3) {
                    vFrameGridNineThree.setVisibility(View.INVISIBLE);
                    vFrameGridNineSix.setVisibility(View.VISIBLE);
                    vFrameGridNineNine.setVisibility(View.INVISIBLE);

                    vFrameGridNineNineOne.setVisibility(View.INVISIBLE);

                    if (i == 0)
                        resolveNineGridFour(itemList, i);
                    else if (i == 1)
                        resolveNineGridFive(itemList, i);
                    else if (i == 2)
                        resolveNineGridSix(itemList, i);
                } else if (itemList.size() == 4) {
                    if (i == 0)
                        resolveFourGridOne(itemList, i);
                    else if (i == 1)
                        resolveFourGridTwo(itemList, i);
                    else if (i == 2)
                        resolveFourGridThree(itemList, i);
                    else if (i == 3)
                        resolveFourGridFour(itemList, i);
                } else if (itemList.size() > 4 && itemList.size() <= 8) {
                    if (itemList.size() == 5) {
                        vFrameGridNineThree.setVisibility(View.VISIBLE);
                        vFrameGridNineSix.setVisibility(View.VISIBLE);

                        if (i == 0)
                            resolveNineGridOne(itemList, i);
                        else if (i == 1)
                            resolveNineGridTwo(itemList, i);
                        else if (i == 2)
                            resolveNineGridThree(itemList, i);
                        else if (i == 3)
                            resolveNineGridFour(itemList, i);
                        else if (i == 4)
                            resolveNineGridSix(itemList, i);
                    } else if (itemList.size() == 6) {
                        vFrameGridNineThree.setVisibility(View.VISIBLE);
                        vFrameGridNineSix.setVisibility(View.VISIBLE);

                        if (i == 0)
                            resolveNineGridOne(itemList, i);
                        else if (i == 1)
                            resolveNineGridTwo(itemList, i);
                        else if (i == 2)
                            resolveNineGridThree(itemList, i);
                        else if (i == 3)
                            resolveNineGridFour(itemList, i);
                        else if (i == 4)
                            resolveNineGridFive(itemList, i);
                        else if (i == 5)
                            resolveNineGridSix(itemList, i);
                    } else if (itemList.size() == 7) {
                        vFrameGridNineThree.setVisibility(View.VISIBLE);
                        vFrameGridNineSix.setVisibility(View.VISIBLE);
                        vFrameGridNineNine.setVisibility(View.VISIBLE);

                        if (i == 0)
                            resolveNineGridOne(itemList, i);
                        else if (i == 1)
                            resolveNineGridTwo(itemList, i);
                        else if (i == 2)
                            resolveNineGridThree(itemList, i);
                        else if (i == 3)
                            resolveNineGridFour(itemList, i);
                        else if (i == 4)
                            resolveNineGridFive(itemList, i);
                        else if (i == 5)
                            resolveNineGridSix(itemList, i);
                        else if (i == 6)
                            resolveNineGridEight(itemList, i);
                    } else if (itemList.size() == 8) {
                        vFrameGridNineThree.setVisibility(View.VISIBLE);
                        vFrameGridNineSix.setVisibility(View.VISIBLE);
                        vFrameGridNineNine.setVisibility(View.VISIBLE);

                        if (i == 0)
                            resolveNineGridOne(itemList, i);
                        else if (i == 1)
                            resolveNineGridTwo(itemList, i);
                        else if (i == 2)
                            resolveNineGridThree(itemList, i);
                        else if (i == 3)
                            resolveNineGridFour(itemList, i);
                        else if (i == 4)
                            resolveNineGridFive(itemList, i);
                        else if (i == 5)
                            resolveNineGridSix(itemList, i);
                        else if (i == 6)
                            resolveNineGridSeven(itemList, i);
                        else if (i == 7)
                            resolveNineGridNine(itemList, i);
                    } else {
                        vFrameGridNineThree.setVisibility(View.VISIBLE);
                        vFrameGridNineSix.setVisibility(View.VISIBLE);
                        vFrameGridNineNine.setVisibility(View.VISIBLE);

                        if (i == 0)
                            resolveNineGridOne(itemList, i);
                        else if (i == 1)
                            resolveNineGridTwo(itemList, i);
                        else if (i == 2)
                            resolveNineGridThree(itemList, i);
                        else if (i == 3)
                            resolveNineGridFour(itemList, i);
                        else if (i == 4)
                            resolveNineGridFive(itemList, i);
                        else if (i == 5)
                            resolveNineGridSix(itemList, i);
                        else if (i == 6)
                            resolveNineGridSeven(itemList, i);
                        else if (i == 7)
                            resolveNineGridEight(itemList, i);
                        else if (i == 8)
                            resolveNineGridNine(itemList, i);
                    }
                }
            }
            //   Constants.map.put(itemList.size(), null);

            if (itemList.size() == 1) {
                isRecyclerViewShowed = false;
                vFrameTabOne.setVisibility(View.VISIBLE);
                vFrameTabTwo.setVisibility(View.GONE);
                vFrameTabFour.setVisibility(View.GONE);
                vFrameTabNine.setVisibility(View.GONE);

                recyclerView.setVisibility(View.INVISIBLE);
            } else if (itemList.size() == 2) {
                vFrameTabOne.setVisibility(View.GONE);
                vFrameTabTwo.setVisibility(View.VISIBLE);
                vFrameTabFour.setVisibility(View.GONE);
                vFrameTabNine.setVisibility(View.GONE);
                isRecyclerViewShowed = false;
                recyclerView.setVisibility(View.INVISIBLE);
            } else if (itemList.size() == 4) {
                vFrameTabOne.setVisibility(View.GONE);
                vFrameTabTwo.setVisibility(View.GONE);
                vFrameTabFour.setVisibility(View.VISIBLE);
                vFrameTabNine.setVisibility(View.GONE);
                recyclerView.setVisibility(View.INVISIBLE);
                isRecyclerViewShowed = false;
            } else if (itemList.size() == 3 || itemList.size() > 4 && itemList.size() <= 8) {
                vFrameTabOne.setVisibility(View.GONE);
                vFrameTabTwo.setVisibility(View.GONE);
                vFrameTabFour.setVisibility(View.GONE);
                vFrameTabNine.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
                isRecyclerViewShowed = false;
            } else {
                vFrameTabOne.setVisibility(View.GONE);
                vFrameTabTwo.setVisibility(View.GONE);
                vFrameTabFour.setVisibility(View.GONE);
                vFrameTabNine.setVisibility(View.GONE);
                isRecyclerViewShowed = true;
                recyclerView.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.setItems(itemList, positionList);
    }

    protected void resolveCollapsingToolbar(String color) {
        int colors = Integer.parseInt(color.replaceFirst("^#", ""), 16);
        collapsingToolbarLayout.setContentScrimColor(colors);
        collapsingToolbarLayout.setStatusBarScrimColor(colors);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandedappbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
//                backgroundImage.setForeground(new ColorDrawable(Color.parseColor("#" + percent + color)));
            } catch (Exception e) {
                colorForeground = color.replace("#", "#" + percent);
//                backgroundImage.setForeground(new ColorDrawable(Color.parseColor(colorForeground)));
            }
        }
    }

    protected void resolveAppBar() {
        vSwipeRefresh.setProgressViewOffset(false, 100, 200);

        appBarLayout.addOnOffsetChangedListener(this);
        vSwipeRefresh.setOnRefreshListener(this);
    }

    protected void resolveValidationLogin() {
        if (new Validations().getInstance(getApplicationContext()).getValidationLoginById(25) == 1) {
            if (!protect.equalsIgnoreCase("error") && protect.equalsIgnoreCase("1")) {
                if (success == null) {
                    finish();
                    Intent a = new Intent(getApplicationContext(), LoginDinamicRoomActivity.class);
                    a.putExtra(ConversationActivity.KEY_JABBER_ID, username);
                    a.putExtra(ConversationActivity.KEY_TITLE, Byonchat.getMessengerHelper().getMyContact().getJabberId());
                    startActivity(a);
                }
            } else if (!protect.equalsIgnoreCase("error") && protect.equalsIgnoreCase("2")) {
                if (success == null) {
                    finish();
                    Intent a = new Intent(getApplicationContext(), LoginDinamicFingerPrint.class);
                    a.putExtra(ConversationActivity.KEY_JABBER_ID, username);
                    a.putExtra(ConversationActivity.KEY_TITLE, Byonchat.getMessengerHelper().getMyContact().getJabberId());
                    startActivity(a);
                }
            } else if (!protect.equalsIgnoreCase("error") && protect.equalsIgnoreCase("5")) {
                if (success == null) {
                    finish();
                    Intent a = new Intent(getApplicationContext(), RequestPasscodeRoomActivity.class);
                    a.putExtra(ConversationActivity.KEY_JABBER_ID, username);
                    a.putExtra(ConversationActivity.KEY_TITLE, "request");
                    startActivity(a);
                }
            } else if (!protect.equalsIgnoreCase("error") && protect.equalsIgnoreCase("6")) {
                if (success == null) {
                    finish();
                    Intent a = new Intent(getApplicationContext(), RequestPasscodeRoomActivity.class);
                    a.putExtra(ConversationActivity.KEY_JABBER_ID, username);
                    a.putExtra(ConversationActivity.KEY_TITLE, "waiting");
                    startActivity(a);
                }
            }

        }
        if (new Validations().getInstance(getApplicationContext()).getValidationLoginISSById(26) == 1) {
            if (title.equalsIgnoreCase("ISS INDONESIA")) {
                if (success == null) {
                    Intent a = new Intent(getApplicationContext(), LoginISS.class);
                    a.putExtra(ConversationActivity.KEY_JABBER_ID, username);
                    a.putExtra(ConversationActivity.KEY_TITLE, "waiting");
                    startActivity(a);
                    finish();
                }
            }
        }
    }

    protected void resolveToolbarExpanded() {
        appBarLayout.setExpanded(true, false);
        if (searchView.isSearchOpen())
            searchView.closeSearch();
    }

    void resolveAnimation() {
        if (i == 0)
            vTxtStatusWarning.setText(getResources().getString(R.string.text_empty_selected));
        else if (i == 1)
            vTxtStatusWarning.setText(getResources().getString(R.string.text_manage_selected));

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                vTxtStatusWarning.setVisibility(View.VISIBLE);
                ObjectAnimator animatorY = ObjectAnimator.ofFloat(vTxtStatusWarning, "y", 400f);
                animatorY.setDuration(700);
                ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(vTxtStatusWarning, View.ALPHA, 0.0f, 1.0f);
                alphaAnimation.setDuration(700);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(animatorY, alphaAnimation);
                animatorSet.start();

                animation();

            }
        }, 500);
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == TAG_CODE_PERMISSION_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                resolveServices();
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.permission_request_title), Toast.LENGTH_SHORT).show();
            }
        }
    }*/

    @Override
    public void onRefresh() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            RefreshRoom();
        }, 500);
    }

    void animation() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator animatorY = ObjectAnimator.ofFloat(vTxtStatusWarning, "y", 300f);
                animatorY.setDuration(700);
                ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(vTxtStatusWarning, View.ALPHA, 1.0f, 0.0f);
                alphaAnimation.setDuration(700);
                final AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(animatorY, alphaAnimation);
                animatorSet.start();
                animatorSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {

                        vTxtStatusWarning.setVisibility(View.GONE);
                        animator = ObjectAnimator.ofFloat(vTxtStatusWarning, "y", 500f);
                        animator.setDuration(0);
                        animator.start();

                        i++;

                        if (i == 2) {
                            i = 0;
                            resolveAnimation();
                        } else
                            resolveAnimation();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });

            }
        }, 2000);

    }

    public void addShortcutBadger(Context context) {
        int badgeCount = 0;
        Cursor cursor = Byonchat.getMessengerHelper().query(
                SQL_SELECT_TOTAL_MESSAGES_UNREAD_ALL,
                new String[]{String.valueOf(Message.STATUS_UNREAD)});
        int indexTotal = cursor.getColumnIndex("total");
        while (cursor.moveToNext()) {
            badgeCount = cursor.getInt(indexTotal);
        }
        cursor.close();

        fab_menu_2.setCount(badgeCount);

        /*ShortcutBadger.applyCount(context, badgeCount);

        bv1.setVisibility(badgeCount == 0 ? View.GONE : View.VISIBLE);
        bv1.setText(badgeCount + "");

        if (badgeCount > 0)
            bv1.show();*/
    }

    public void addTabMenuBadger(ItemMain itemMain, CounterFab itemBadger) {
        int badgeCount = 0;

        if (itemMain.category_tab == null) {
            String member = itemMain.status;
            try {
                JSONArray jsonArray = new JSONArray(member);
                for (int ii = 0; ii < jsonArray.length(); ii++) {
                    String id_tab_dftared = jsonArray.getString(ii);

                    Cursor cursor = Byonchat.getMessengerHelper().query(
                            SQL_SELECT_TOTAL_BADGE_TAB_MENU,
                            new String[]{String.valueOf(id_tab_dftared)});
                    int indexTotal = cursor.getColumnIndex("total");
                    while (cursor.moveToNext()) {
                        badgeCount = badgeCount + cursor.getInt(indexTotal);
                    }
                    cursor.close();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Cursor cursor = Byonchat.getMessengerHelper().query(
                    SQL_SELECT_TOTAL_BADGE_TAB_MENU,
                    new String[]{String.valueOf(itemMain.id_rooms_tab)});
            int indexTotal = cursor.getColumnIndex("total");
            while (cursor.moveToNext()) {
                badgeCount = cursor.getInt(indexTotal);
            }
            cursor.close();
        }

        itemBadger.setCount(badgeCount);
    }

    class BroadcastHandler extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MessengerConnectionService.ACTION_MESSAGE_RECEIVED
                    .equals(intent.getAction())) {
                addShortcutBadger(context);
            } else if (MessengerConnectionService.ACTION_REFRESH_NOTIF_FORM
                    .equals(intent.getAction())) {
                onHomeRefresh();
            } else if (ACTION_REFRESH_BADGER.equals(intent.getAction())) {
                addShortcutBadger(context);
            } else if (ACTION_REFRESH_NOTIF.equals(intent.getAction())) {
                addShortcutBadger(context);
                ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                        .cancel(NotificationReceiver.NOTIFY_ID);
                ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                        .cancel(NotificationReceiver.NOTIFY_ID_CARD);
            }
        }
    }

    private void requestKey() {
        new Handler(Looper.getMainLooper()).post(new Runnable() { // Tried new Handler(Looper.myLopper()) also
            @Override
            public void run() {
                RequestKeyTask testAsyncTask = new RequestKeyTask(new TaskCompleted() {
                    @Override
                    public void onTaskDone(String key) {
                        if (key.equalsIgnoreCase("null")) {
                            // Toast.makeText(context, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                        } else {
                            laporSelectedRoom = new LaporSelectedRoom(getApplicationContext());
                            laporSelectedRoom.execute(key);
                        }
                    }
                }, getApplicationContext());

                testAsyncTask.execute();
            }
        });
    }

    class LaporSelectedRoom extends AsyncTask<String, Void, String> {

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();

        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content = null;
        private boolean error = false;
        private Context mContext;

        public LaporSelectedRoom(Context context) {
            this.mContext = context;

        }

        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        protected String doInBackground(String... key) {
            try {
                HttpClient httpClient = HttpHelper
                        .createHttpClient(mContext);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);

                nameValuePairs.add(new BasicNameValuePair("username", Byonchat.getMessengerHelper().getMyContact().getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("key", key[0]));
                nameValuePairs.add(new BasicNameValuePair("room_id", roomid));
                nameValuePairs.add(new BasicNameValuePair("aksi", "2"));

                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                HttpPost post = new HttpPost(URL_LAPOR_SELECTED);
                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                response = httpclient.execute(post);
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    content = out.toString();

                } else {
                    error = true;
                    content = statusLine.getReasonPhrase();
                    response.getEntity().getContent().close();
                    throw new IOException(content);
                }

            } catch (ClientProtocolException e) {
                content = e.getMessage();
                error = true;
            } catch (IOException e) {
                content = e.getMessage();
                error = true;
            } catch (Exception e) {
                error = true;
            }

            return content;
        }

        protected void onCancelled() {
        }

        protected void onPostExecute(String content) {
            dismissProgressDialog();
            if (error) {
                if (content.contains("invalid_key")) {
                    if (NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)) {
                        String key = new ValidationsKey().getInstance(mContext).key(true);
                        if (key.equalsIgnoreCase("null")) {
                            // Toast.makeText(mContext, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                        } else {
                            laporSelectedRoom = new LaporSelectedRoom(getApplicationContext());
                            laporSelectedRoom.execute(key);
                        }
                    } else {
                        // Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Toast.makeText(mContext, content, Toast.LENGTH_LONG).show();
                }
            } else {

                Cursor cur = Byonchat.getBotListDB().getSingleRoom(roomid);

                if (cur.getCount() > 0) {
                    String aaContent = cur.getString(cur.getColumnIndex(BotListDB.ROOM_CONTENT));
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(aaContent);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String aaId = jsonArray.getJSONObject(i).getString("id_rooms_tab").toString();
                            String category = jsonArray.getJSONObject(i).getString("category_tab").toString();
                            if (category.equalsIgnoreCase("4")) {
                                Cursor cursor = Byonchat.getBotListDB().getSingleRoomDetailForm(roomid, aaId);
                                if (cursor.getCount() > 0) {
                                    String contentDetail = cursor.getString(cursor.getColumnIndexOrThrow(BotListDB.ROOM_CONTENT));
                                    JSONArray jsonArrayDetail = new JSONArray(contentDetail);
                                    for (int ii = 0; ii < jsonArrayDetail.length(); ii++) {
                                        String value = jsonArrayDetail.getJSONObject(ii).getString("value").toString();
                                        String tt = jsonArrayDetail.getJSONObject(ii).getString("type").toString();
                                        if (tt.equalsIgnoreCase("dropdown_dinamis")) {
                                            JSONObject jObject = new JSONObject(value);
                                            String url = jObject.getString("url");
                                            String[] aa = url.split("/");
                                            final String nama = aa[aa.length - 1].toString();

                                            File newDB = new File(DataBaseDropDown.getDatabaseFolder() + nama);
                                            if (newDB.exists()) {
                                                newDB.delete();
                                            }

                                        }
                                        /*kodepos delete otomatis by system
                                         else if (tt.equalsIgnoreCase("dropdown_wilayah") || tt.equalsIgnoreCase("input_kodepos")) {
                                            File newDB = new File(DataBaseDropDown.getDatabaseFolder() + "daftarkodepos.sqlite");
                                            if (newDB.exists()) {
                                                newDB.delete();
                                            }

                                        }*/
                                    }

                                }

                            }
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Byonchat.getBotListDB().deleteRoomsbyTAB(roomid);
                Byonchat.getBotListDB().deleteRoomsDetailAllItemSku(roomid);

                Byonchat.getRoomsDB().open();
                Byonchat.getRoomsDB().deletebyName(roomid);
                Byonchat.getRoomsDB().close();

                resolveListRooms();
            }
        }
    }

    protected void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = UtilsPD.createProgressDialog(MainBaseActivityNew.this);
        }
        progressDialog.show();
    }

    protected void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public class LoadImageFromURL extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        public LoadImageFromURL(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            // TODO Auto-generated method stub
            Bitmap bitmap = null;
            Bitmap blurredBitmap = null;
            try {
                URL url = new URL(params[0]);
                bitmap = BitmapFactory.decodeStream((InputStream) url.getContent());
                if (bitmap != null) {
                    blurredBitmap = BlurBuilder.blur(getApplicationContext(), bitmap);
                }

                return blurredBitmap;

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (result != null) {
                imageView.setBackgroundDrawable(new BitmapDrawable(getResources(), result));
            }
        }
    }

    protected void RefreshRoom() {
        if (title.equalsIgnoreCase("ISS INDONESIA")) {
            vSwipeRefresh.setRefreshing(false);
            AlertDialog.Builder alertbox = new AlertDialog.Builder(MainBaseActivityNew.this);
            alertbox.setTitle("Refresh Room ISS INDONESIA");
            alertbox.setMessage("Are you sure you want to Refresh?");
            alertbox.setPositiveButton("Ok", (arg0, arg1) -> {
                if (NetworkInternetConnectionStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

                    dialog = ProgressDialog.show(MainBaseActivityNew.this, "",
                            "Loading. Please wait...", true);
                    Map<String, String> params = new HashMap<>();
                    params.put("username", new Validations().getInstance(getApplicationContext()).getString(28));
                    params.put("password", new Validations().getInstance(getApplicationContext()).getString(29));
                    params.put("bc_user", Byonchat.getMessengerHelper().getMyContact().getJabberId());

                    LoginThis("https://bb.byonchat.com/bc_voucher_client/webservice/get_tab_rooms_iss.php", params, true);

                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Akses", Toast.LENGTH_SHORT).show();
                }
            });
            alertbox.setNegativeButton("Cancel", (arg0, arg1) -> {
            });
            alertbox.show();
        } else {
            vSwipeRefresh.setRefreshing(false);
            Byonchat.getRoomsDB().open();
            botArrayListist = Byonchat.getRoomsDB().retrieveRooms("2", true);
            Byonchat.getRoomsDB().close();
            if (botArrayListist.size() > 0) {
                try {
                    JSONObject jObj = new JSONObject(botArrayListist.get(0).getTargetUrl());
                    String targetURL = jObj.getString("path");

                    AlertDialog.Builder alertbox = new AlertDialog.Builder(MainBaseActivityNew.this);
                    alertbox.setTitle("Refresh Room " + botArrayListist.get(0).realname);
                    alertbox.setMessage("Are you sure you want to Refresh?");
                    alertbox.setPositiveButton("Ok", (arg0, arg1) -> {
                        if (NetworkInternetConnectionStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

                            refreshRoomForm();

                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Constants.EXTRA_SERVICE_PERMISSION, "true");
                            editor.apply();

                            finish();

                            Intent ii = LoadingGetTabRoomActivity.generateIntent(getApplicationContext(), username, targetURL);
                            startActivity(ii);

                        } else {
                            Toast.makeText(getApplicationContext(), "No Internet Akses", Toast.LENGTH_SHORT).show();
                        }
                    });
                    alertbox.setNegativeButton("Cancel", (arg0, arg1) -> {
                    });
                    alertbox.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void LogoutRoom() {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(MainBaseActivityNew.this);
        alertbox.setTitle("Logout Apps");
        alertbox.setMessage("Are you sure you want to Logout?");
        alertbox.setPositiveButton("Ok", (arg0, arg1) -> {
            new Validations().getInstance(getApplicationContext()).removeById(26);
            new Validations().getInstance(getApplicationContext()).removeById(27);
            new Validations().getInstance(getApplicationContext()).removeById(28);
            new Validations().getInstance(getApplicationContext()).removeById(29);

            Intent a = new Intent(getApplicationContext(), LoginISS.class);
            a.putExtra(ConversationActivity.KEY_JABBER_ID, username);
            a.putExtra(ConversationActivity.KEY_TITLE, "waiting");
            startActivity(a);
            finish();
        });
        alertbox.setNegativeButton("Cancel", (arg0, arg1) -> {
        });
        alertbox.show();
    }

    protected void refreshRoomForm() {
        Cursor cur = Byonchat.getBotListDB().getSingleRoom(username);
        if (cur.getCount() > 0) {

            String content = cur.getString(cur.getColumnIndex(BotListDB.ROOM_CONTENT));

            try {
                JSONArray jsonArray = new JSONArray(content);
                itemList.clear();
                positionList.clear();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                if (prefs != null) {
                    extra_tab = prefs.getString(Constants.EXTRA_TAB_MOVEMENT, "");
                    if (!extra_tab.equalsIgnoreCase("")) {
                        jsonArray = new JSONArray(extra_tab);
                    }
                }

                for (int i = 0; i < jsonArray.length(); i++) {
                    String id_rooms_tab = jsonArray.getJSONObject(i).getString("id_rooms_tab").toString();

                    Byonchat.getBotListDB().deleteDetailRoomById(username, id_rooms_tab);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    protected void SwipeRoom() {
        Byonchat.getRoomsDB().open();
        botArrayListist = Byonchat.getRoomsDB().retrieveRooms("2", true);
        Byonchat.getRoomsDB().close();

        try {
            JSONObject jObj = new JSONObject(botArrayListist.get(0).getTargetUrl());
            String targetURL = jObj.getString("path");

            Log.w("Stats sweeping 1", targetURL);
            if (NetworkInternetConnectionStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
                finish();
                Intent ii = LoadingGetTabRoomActivity.generateIntent(getApplicationContext(), username, targetURL);
                Log.w("Stats sweeping 2", username);
                startActivity(ii);
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Akses", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.w("Stats sweeping Error", e);
        }
    }

    protected void shareIt() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareSubject = getResources().getString(R.string.share_subject);
        String shareTitle = getResources().getString(R.string.share_title);
        String shareBody = getResources().getString(R.string.share_body);
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSubject);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, shareTitle));
    }
/*
    @Override
    public void onNeedLocationPermission() {

    }

    @Override
    public void onExplainLocationPermission() {

    }

    @Override
    public void onLocationPermissionPermanentlyDeclined(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {

    }

    @Override
    public void onNeedLocationSettingsChange() {

    }

    @Override
    public void onFallBackToSystemSettings(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {

    }

    @Override
    public void onNewLocationAvailable(Location location) {

    }

    @Override
    public void onMockLocationsDetected(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {
        Toast.makeText(MainBaseActivityNew.this.getApplicationContext(), "Please Turn Off Allow Mock Location", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onError(LocationAssistant.ErrorType type, String message) {

    }*/

    protected void createShortcut() {
        Byonchat.getRoomsDB().open();
        contactBotsShortcut = Byonchat.getRoomsDB().retrieveRooms("2", true);
        Byonchat.getRoomsDB().close();

        if (contactBotsShortcut.size() > 0) {
            Cursor cur = Byonchat.getBotListDB().getSingleRoom(contactBotsShortcut.get(0).name);

            if (cur.getCount() > 0) {
                String name = cur.getString(cur.getColumnIndex(BotListDB.ROOM_REALNAME));
                String icon = cur.getString(cur.getColumnIndex(BotListDB.ROOM_ICON));
                String username = Byonchat.getMessengerHelper().getMyContact().getJabberId();

                final Dialog dialogConfirmation;
                dialogConfirmation = DialogUtil.customDialogConversationConfirmation(this);
                dialogConfirmation.show();

                TextView txtConfirmation = (TextView) dialogConfirmation.findViewById(R.id.confirmationTxt);
                TextView descConfirmation = (TextView) dialogConfirmation.findViewById(R.id.confirmationDesc);
                txtConfirmation.setText(contactBotsShortcut.get(0).realname + " Shortcut");
                descConfirmation.setVisibility(View.VISIBLE);
                descConfirmation.setText("Are you sure you want to create shortcut for "
                        + contactBotsShortcut.get(0).realname + "?");

                Button btnNo = (Button) dialogConfirmation.findViewById(R.id.btnNo);
                Button btnYes = (Button) dialogConfirmation.findViewById(R.id.btnYes);
                btnNo.setText("Cancel");
                btnNo.setOnClickListener(v -> {
                    dialogConfirmation.dismiss();
                });

                btnYes.setOnClickListener(v -> {
                    dialogConfirmation.dismiss();
                    Picasso.with(MainBaseActivityNew.this)
                            .load(icon)
                            .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                            .transform(new RoundedCornersTransformation(30, 0, RoundedCornersTransformation.CornerType.ALL))
                            .into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    if (bitmap != null) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            ShortcutInfo.Builder mShortcutInfoBuilder = new ShortcutInfo.Builder(MainBaseActivityNew.this, name);
                                            mShortcutInfoBuilder.setShortLabel(name);
                                            mShortcutInfoBuilder.setLongLabel(name);
                                            mShortcutInfoBuilder.setIcon(Icon.createWithBitmap(bitmap));
                                            mShortcutInfoBuilder.setIntent(generateShortcutIntent());

                                            ShortcutInfo mShortcutInfo = mShortcutInfoBuilder.build();
                                            ShortcutManager mShortcutManager = getSystemService(ShortcutManager.class);
                                            mShortcutManager.requestPinShortcut(mShortcutInfo, null);
                                        } else {
                                            Intent addIntent = new Intent();
                                            addIntent
                                                    .putExtra(Intent.EXTRA_SHORTCUT_INTENT, generateShortcutIntent());
                                            addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
                                            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);

                                            addIntent
                                                    .setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                                            addIntent.putExtra("duplicate", false);  //may it's already there so   don't duplicate
                                            getApplicationContext().sendBroadcast(addIntent);

                                        }
                                        finish();
                                        Toast.makeText(MainBaseActivityNew.this, "Shortcut Created", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onBitmapFailed(Drawable errorDrawable) {
                                    Toast.makeText(MainBaseActivityNew.this, "Create Shortcut failed", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {
                                    Toast.makeText(MainBaseActivityNew.this, "Please Wait", Toast.LENGTH_SHORT).show();
                                }
                            });
                });
            }
        }
    }

    protected Intent generateShortcutIntent() {
        ArrayList<ContactBot> contactBots = new ArrayList<>();
        Gson gson = new Gson();

        ContactBot contactBot = contactBotsShortcut.get(0);
        contactBots.add(contactBot);

        Intent intent = new Intent(getApplicationContext(), MainActivityNew.class);
        intent.putExtra(ConversationActivity.KEY_JABBER_ID, username);
        intent.putExtra(Constants.EXTRA_ROOM, gson.toJson(contactBots));
        intent.putExtra(Constants.EXTRA_TAB_MOVEMENT, gson.toJson(itemList));
        intent.putExtra(ConversationActivity.KEY_TITLE, contactBotsShortcut.get(0).targetUrl);
        intent.setAction(Intent.ACTION_MAIN);

        return intent;
    }

    protected void showToastTab(String args) {
        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_SHORT);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_toast, null);
        TextView text = (TextView) view.findViewById(R.id.message);
        text.setText(args);
        toast.setView(view);
        toast.setGravity(Gravity.TOP, 0, 100);
        toast.show();
    }

    protected void changeGridSize() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();

        new AlertDialog.Builder(this)
                .setItems(R.array.dialog_grid_size, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            editor.putString(Constants.EXTRA_GRID_SIZE, Constants.EXTRA_GRID_SIZE_THREE);
                            editor.apply();
                            break;
                        case 1:
                            editor.putString(Constants.EXTRA_GRID_SIZE, Constants.EXTRA_GRID_SIZE_FOUR);
                            editor.apply();
                            break;
                    }

                    resolveRecyclerView();
                    onHomeRefresh();
                })
                .show();

        drawerLayout.closeDrawer(GravityCompat.START);
    }

    protected void onHomeRefresh() {
        resolveAppBar();
        resolveValidationLogin();
        resolveToolbarExpanded();
        resolveNavHeader();
        resolveListRooms();
        resolveOpenRooms();
        resolveRefreshGrid();
        resolveShowRecyclerView();

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setVisibility(View.VISIBLE);
            vFrameTabOne.setVisibility(View.INVISIBLE);
            vFrameTabTwo.setVisibility(View.INVISIBLE);
            vFrameTabFour.setVisibility(View.INVISIBLE);
            vFrameTabNine.setVisibility(View.INVISIBLE);
        }
    }

    protected void resolveOneGrid(List<ItemMain> itemList) {
        ItemMain im = itemList.get(0);

        vTitleItemGridOne.setText(Utility.capitalizer(im.getTitle()));

        Manhera.getInstance().get()
                .load(im.icon_name.equalsIgnoreCase(null)
                        || im.icon_name.equalsIgnoreCase("null") ? im.iconTest : im.icon_name)
                .placeholder(R.drawable.logo_byon)
                .into(vLogoItemGridOne);

        fonts.FontFamily(getApplicationContext().getAssets(), vTitleItemGridOne, Fonts.FONT_ROBOTO_BOLD);

        vFrameClickTabOne.setOnClickListener(v -> {
            intentTabMenu(im, 0);
        });

        addTabMenuBadger(im, tab_menu_main);
    }

    protected void resolveTwoGridOne(List<ItemMain> itemList, int position) {
        ItemMain im = itemList.get(position);

        vTitleItemGridTwoOne.setText(Utility.capitalizer(im.getTitle()));

        Manhera.getInstance().get()
                .load(im.icon_name.equalsIgnoreCase(null)
                        || im.icon_name.equalsIgnoreCase("null") ? im.iconTest : im.icon_name)
                .placeholder(R.drawable.logo_byon)
                .into(vLogoItemGridTwoOne);

        fonts.FontFamily(getApplicationContext().getAssets(), vTitleItemGridTwoOne, Fonts.FONT_ROBOTO_BOLD);

        vFrameClickTabTwoOne.setOnClickListener(v -> {
            intentTabMenu(im, position);
        });

        addTabMenuBadger(im, tab_menu_21);
    }

    protected void resolveTwoGridTwo(List<ItemMain> itemList, int position) {
        ItemMain im = itemList.get(position);

        vTitleItemGridTwoTwo.setText(Utility.capitalizer(im.getTitle()));

        Manhera.getInstance().get()
                .load(im.icon_name.equalsIgnoreCase(null)
                        || im.icon_name.equalsIgnoreCase("null") ? im.iconTest : im.icon_name)
                .placeholder(R.drawable.logo_byon)
                .into(vLogoItemGridTwoTwo);

        fonts.FontFamily(getApplicationContext().getAssets(), vTitleItemGridTwoTwo, Fonts.FONT_ROBOTO_BOLD);

        vFrameClickTabTwoTwo.setOnClickListener(v -> {
            intentTabMenu(im, position);
        });

        addTabMenuBadger(im, tab_menu_22);
    }

    protected void resolveFourGridOne(List<ItemMain> itemList, int position) {
        ItemMain im = itemList.get(position);

        vTitleItemGridFourOne.setText(Utility.capitalizer(im.getTitle()));

        Manhera.getInstance().get()
                .load(im.icon_name.equalsIgnoreCase(null)
                        || im.icon_name.equalsIgnoreCase("null") ? im.iconTest : im.icon_name)
                .placeholder(R.drawable.logo_byon)
                .into(vLogoItemGridFourOne);

        fonts.FontFamily(getApplicationContext().getAssets(), vTitleItemGridFourOne, Fonts.FONT_ROBOTO_BOLD);

        vFrameClickTabFourOne.setOnClickListener(v -> {
            intentTabMenu(im, position);
        });

        addTabMenuBadger(im, tab_menu_41);
    }

    protected void resolveFourGridTwo(List<ItemMain> itemList, int position) {
        ItemMain im = itemList.get(position);

        vTitleItemGridFourTwo.setText(Utility.capitalizer(im.getTitle()));

        Manhera.getInstance().get()
                .load(im.icon_name.equalsIgnoreCase(null)
                        || im.icon_name.equalsIgnoreCase("null") ? im.iconTest : im.icon_name)
                .placeholder(R.drawable.logo_byon)
                .into(vLogoItemGridFourTwo);

        fonts.FontFamily(getApplicationContext().getAssets(), vTitleItemGridFourTwo, Fonts.FONT_ROBOTO_BOLD);

        vFrameClickTabFourTwo.setOnClickListener(v -> {
            intentTabMenu(im, position);
        });

        addTabMenuBadger(im, tab_menu_42);
    }

    protected void resolveFourGridThree(List<ItemMain> itemList, int position) {
        ItemMain im = itemList.get(position);

        vTitleItemGridFourThree.setText(Utility.capitalizer(im.getTitle()));

        Manhera.getInstance().get()
                .load(im.icon_name.equalsIgnoreCase(null)
                        || im.icon_name.equalsIgnoreCase("null") ? im.iconTest : im.icon_name)
                .placeholder(R.drawable.logo_byon)
                .into(vLogoItemGridFourThree);

        fonts.FontFamily(getApplicationContext().getAssets(), vTitleItemGridFourThree, Fonts.FONT_ROBOTO_BOLD);

        vFrameClickTabFourThree.setOnClickListener(v -> {
            intentTabMenu(im, position);
        });

        addTabMenuBadger(im, tab_menu_43);
    }

    protected void resolveFourGridFour(List<ItemMain> itemList, int position) {
        ItemMain im = itemList.get(position);

        vTitleItemGridFourFour.setText(Utility.capitalizer(im.getTitle()));

        Manhera.getInstance().get()
                .load(im.icon_name.equalsIgnoreCase(null)
                        || im.icon_name.equalsIgnoreCase("null") ? im.iconTest : im.icon_name)
                .placeholder(R.drawable.logo_byon)
                .into(vLogoItemGridFourFour);

        fonts.FontFamily(getApplicationContext().getAssets(), vTitleItemGridFourFour, Fonts.FONT_ROBOTO_BOLD);

        vFrameClickTabFourFour.setOnClickListener(v -> {
            intentTabMenu(im, position);
        });

        addTabMenuBadger(im, tab_menu_44);
    }

    protected void resolveNineGridOne(List<ItemMain> itemList, int position) {
        vFrameGridNineNineOne.setVisibility(View.VISIBLE);

        ItemMain im = itemList.get(position);

        vTitleItemGridNineOne.setText(Utility.capitalizer(im.getTitle()));

        Manhera.getInstance().get()
                .load(im.icon_name.equalsIgnoreCase(null)
                        || im.icon_name.equalsIgnoreCase("null") ? im.iconTest : im.icon_name)
                .placeholder(R.drawable.logo_byon)
                .into(vLogoItemGridNineOne);

        fonts.FontFamily(getApplicationContext().getAssets(), vTitleItemGridNineOne, Fonts.FONT_ROBOTO_BOLD);

        vFrameClickTabNineOne.setOnClickListener(v -> {
            intentTabMenu(im, position);
        });

        addTabMenuBadger(im, tab_menu_91);
    }

    protected void resolveNineGridTwo(List<ItemMain> itemList, int position) {
        vFrameGridNineNineTwo.setVisibility(View.VISIBLE);

        ItemMain im = itemList.get(position);

        vTitleItemGridNineTwo.setText(Utility.capitalizer(im.getTitle()));

        Manhera.getInstance().get()
                .load(im.icon_name.equalsIgnoreCase(null)
                        || im.icon_name.equalsIgnoreCase("null") ? im.iconTest : im.icon_name)
                .placeholder(R.drawable.logo_byon)
                .into(vLogoItemGridNineTwo);

        fonts.FontFamily(getApplicationContext().getAssets(), vTitleItemGridNineTwo, Fonts.FONT_ROBOTO_BOLD);

        vFrameClickTabNineTwo.setOnClickListener(v -> {
            intentTabMenu(im, position);
        });

        addTabMenuBadger(im, tab_menu_92);
    }

    protected void resolveNineGridThree(List<ItemMain> itemList, int position) {
        vFrameGridNineNineThree.setVisibility(View.VISIBLE);

        ItemMain im = itemList.get(position);

        vTitleItemGridNineThree.setText(Utility.capitalizer(im.getTitle()));

        Manhera.getInstance().get()
                .load(im.icon_name.equalsIgnoreCase(null)
                        || im.icon_name.equalsIgnoreCase("null") ? im.iconTest : im.icon_name)
                .placeholder(R.drawable.logo_byon)
                .into(vLogoItemGridNineThree);

        fonts.FontFamily(getApplicationContext().getAssets(), vTitleItemGridNineThree, Fonts.FONT_ROBOTO_BOLD);

        vFrameClickTabNineThree.setOnClickListener(v -> {
            intentTabMenu(im, position);
        });

        addTabMenuBadger(im, tab_menu_93);
    }

    protected void resolveNineGridFour(List<ItemMain> itemList, int position) {
        vFrameGridNineNineFour.setVisibility(View.VISIBLE);

        ItemMain im = itemList.get(position);

        vTitleItemGridNineFour.setText(Utility.capitalizer(im.getTitle()));

        Manhera.getInstance().get()
                .load(im.icon_name.equalsIgnoreCase(null)
                        || im.icon_name.equalsIgnoreCase("null") ? im.iconTest : im.icon_name)
                .placeholder(R.drawable.logo_byon)
                .into(vLogoItemGridNineFour);

        fonts.FontFamily(getApplicationContext().getAssets(), vTitleItemGridNineFour, Fonts.FONT_ROBOTO_BOLD);

        vFrameClickTabNineFour.setOnClickListener(v -> {
            intentTabMenu(im, position);
        });

        addTabMenuBadger(im, tab_menu_94);
    }

    protected void resolveNineGridFive(List<ItemMain> itemList, int position) {
        vFrameGridNineNineFive.setVisibility(View.VISIBLE);

        ItemMain im = itemList.get(position);

        vTitleItemGridNineFive.setText(Utility.capitalizer(im.getTitle()));

        Manhera.getInstance().get()
                .load(im.icon_name.equalsIgnoreCase(null)
                        || im.icon_name.equalsIgnoreCase("null") ? im.iconTest : im.icon_name)
                .placeholder(R.drawable.logo_byon)
                .into(vLogoItemGridNineFive);

        fonts.FontFamily(getApplicationContext().getAssets(), vTitleItemGridNineFive, Fonts.FONT_ROBOTO_BOLD);

        vFrameClickTabNineFive.setOnClickListener(v -> {
            intentTabMenu(im, position);
        });

        addTabMenuBadger(im, tab_menu_95);
    }

    protected void resolveNineGridSix(List<ItemMain> itemList, int position) {
        vFrameGridNineNineSix.setVisibility(View.VISIBLE);

        ItemMain im = itemList.get(position);

        vTitleItemGridNineSix.setText(Utility.capitalizer(im.getTitle()));

        Manhera.getInstance().get()
                .load(im.icon_name.equalsIgnoreCase(null)
                        || im.icon_name.equalsIgnoreCase("null") ? im.iconTest : im.icon_name)
                .placeholder(R.drawable.logo_byon)
                .into(vLogoItemGridNineSix);

        fonts.FontFamily(getApplicationContext().getAssets(), vTitleItemGridNineSix, Fonts.FONT_ROBOTO_BOLD);

        vFrameClickTabNineSix.setOnClickListener(v -> {
            intentTabMenu(im, position);
        });

        addTabMenuBadger(im, tab_menu_96);
    }

    protected void resolveNineGridSeven(List<ItemMain> itemList, int position) {
        vFrameGridNineNineSeven.setVisibility(View.VISIBLE);

        ItemMain im = itemList.get(position);

        vTitleItemGridNineSeven.setText(Utility.capitalizer(im.getTitle()));

        Manhera.getInstance().get()
                .load(im.icon_name.equalsIgnoreCase(null)
                        || im.icon_name.equalsIgnoreCase("null") ? im.iconTest : im.icon_name)
                .placeholder(R.drawable.logo_byon)
                .into(vLogoItemGridNineSeven);

        fonts.FontFamily(getApplicationContext().getAssets(), vTitleItemGridNineSeven, Fonts.FONT_ROBOTO_BOLD);

        vFrameClickTabNineSeven.setOnClickListener(v -> {
            intentTabMenu(im, position);
        });

        addTabMenuBadger(im, tab_menu_97);
    }

    protected void resolveNineGridEight(List<ItemMain> itemList, int position) {
        vFrameGridNineNineEight.setVisibility(View.VISIBLE);

        ItemMain im = itemList.get(position);

        vTitleItemGridNineEight.setText(Utility.capitalizer(im.getTitle()));

        Manhera.getInstance().get()
                .load(im.icon_name.equalsIgnoreCase(null)
                        || im.icon_name.equalsIgnoreCase("null") ? im.iconTest : im.icon_name)
                .placeholder(R.drawable.logo_byon)
                .into(vLogoItemGridNineEight);

        fonts.FontFamily(getApplicationContext().getAssets(), vTitleItemGridNineEight, Fonts.FONT_ROBOTO_BOLD);

        vFrameClickTabNineEight.setOnClickListener(v -> {
            intentTabMenu(im, position);
        });

        addTabMenuBadger(im, tab_menu_98);
    }

    protected void resolveNineGridNine(List<ItemMain> itemList, int position) {
        vFrameGridNineNineNine.setVisibility(View.VISIBLE);

        ItemMain im = itemList.get(position);

        vTitleItemGridNineNine.setText(Utility.capitalizer(im.getTitle()));

        Manhera.getInstance().get()
                .load(im.icon_name.equalsIgnoreCase(null)
                        || im.icon_name.equalsIgnoreCase("null") ? im.iconTest : im.icon_name)
                .placeholder(R.drawable.logo_byon)
                .into(vLogoItemGridNineNine);

        fonts.FontFamily(getApplicationContext().getAssets(), vTitleItemGridNineNine, Fonts.FONT_ROBOTO_BOLD);

        vFrameClickTabNineNine.setOnClickListener(v -> {
            intentTabMenu(im, position);
        });

        addTabMenuBadger(im, tab_menu_99);
    }

    private void intentTabMenu(ItemMain im, int position) {
        if (adapter.getData().get(position).id_rooms_tab != null) {
            Intent intent = ByonChatMainRoomActivity.generateIntent(getApplicationContext(), (ItemMain) adapter.getData().get(position));
            startActivity(intent);
        } else {
            ArrayList<SectionSampleItem> sample = new ArrayList<>();

            String tab_name = adapter.getData().get(position).tab_name;
            ArrayList<ItemMain> subItemListed = new ArrayList<>();
            String member = adapter.getData().get(position).status;
            String category_sub = adapter.getData().get(position).category_tab;
            boolean non_sla = false;
            try {
                JSONArray jsonArray = new JSONArray(member);
                for (int i = 0; i < jsonArray.length(); i++) {
                    if (category_sub.equalsIgnoreCase("sla")) {
                        JSONObject gr = jsonArray.getJSONObject(i);
                        String name_sub = gr.getString("name");
                        JSONArray group_tab = gr.getJSONArray("members");
                        ArrayList<ItemMain> subItemList2 = new ArrayList<>();

                        for (int a = 0; a < group_tab.length(); a++) {
                            String id_tab_dftared = group_tab.getString(a);
                            for (int u = 0; u < subItemList.size(); u++) {
                                if (subItemList.get(u).id_rooms_tab.equalsIgnoreCase(id_tab_dftared)) {
                                    subItemList2.add(subItemList.get(u));
                                }
                            }
                        }
                        SectionSampleItem samply = new SectionSampleItem();
                        samply.setHeaderTitle(name_sub);
                        samply.setAllItemsInSection(subItemList2);
                        sample.add(samply);
                    } else {
                        String id_tab_dftared = jsonArray.getString(i);
                        for (int u = 0; u < subItemList.size(); u++) {
                            if (subItemList.get(u).id_rooms_tab.equalsIgnoreCase(id_tab_dftared)) {
                                subItemListed.add(subItemList.get(u));
                            }
                        }
                        non_sla = true;
                    }
                }

                if (non_sla) {
                    SectionSampleItem samply = new SectionSampleItem();
                    samply.setHeaderTitle(tab_name);
                    samply.setAllItemsInSection(subItemListed);
                    sample.add(samply);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ItemDialog dialog = new ItemDialog(this, adapter.getData().get(position).tab_name, sample);
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.70);
            dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
            dialog.show();
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    protected void resolveServices() {
        BCServiceSyncAdapter.initializeSyncAdapter(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            if (prefs != null) {
                if (prefs.getString(Constants.EXTRA_SERVICE_PERMISSION, "false").equalsIgnoreCase("true")) {

                } else {
                    JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
                    ComponentName componentName = new ComponentName(MainBaseActivityNew.this, WhatsAppJobService.class);
                    JobInfo jobInfo = new JobInfo.Builder(1, componentName)
                            .setPeriodic(TimeUnit.MINUTES.toMillis(1))
                            .build();

                    PermanentLoggerUtil.logMessage(MainBaseActivityNew.this, "Scheduling recurring job");
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
                ComponentName componentName = new ComponentName(MainBaseActivityNew.this, WhatsAppJobService.class);
                JobInfo jobInfo = new JobInfo.Builder(1, componentName)
                        .setPeriodic(TimeUnit.MINUTES.toMillis(1))
                        .build();

                PermanentLoggerUtil.logMessage(MainBaseActivityNew.this, "Scheduling recurring job");
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

    private void LoginThis(String Url, Map<String, String> params2, Boolean hide) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        StringRequest sr = new StringRequest(Request.Method.POST, Url,
                response -> {
                    if (dialog != null) {
                        dialog.hide();
                    }
                    if (hide) {
                        try {
                            JSONObject jsonRootObject = new JSONObject(response);
                            parseJSON(response, jsonRootObject.getString("json_iss"));
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                },
                error -> {
                    if (dialog != null) {
                        dialog.hide();
                    }
                    Toast.makeText(getApplicationContext(), "Please Try Again : because, " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        ) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return params2;
            }
        };
        sr.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                Log.e("HttpClient", "error: " + error.toString());
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(sr);
    }

    private void parseJSON(String allres, String result) {
        Log.w("Resharusee", result);
        String[] dataLOG = new String[0];
        try {
            JSONObject start = new JSONObject(result);
            String sukses = start.getString("MESSAGE");
            String token = start.getString("URITOKENS");
            String status = start.get("STATUS") + "";
            JSONArray data = start.getJSONArray("RESULT");
            JSONObject jsonObject = data.getJSONObject(0);
            String USERNAME = jsonObject.getString("USER_NAME");
            String EMPLOYEE_NAME = jsonObject.getString("EMPLOYEE_NAME");
            String EMPLOYEE_EMAIL = jsonObject.getString("EMPLOYEE_EMAIL");
            String EMPLOYEE_NIK = jsonObject.getString("EMPLOYEE_NIK");
            String EMPLOYEE_JT = jsonObject.getString("EMPLOYEE_JOBTITLE");
            String EMPLOYEE_MULTICOST = jsonObject.getString("EMPLOYEE_MULTIPLECOST");
            String EMPLOYEE_PHONE = jsonObject.getString("EMPLOYEE_PHONE");
            String EMPLOYEE_PHOTOS = jsonObject.getString("EMPLOYEE_PHOTOS");
            String ATASAN_1_USERNAME = jsonObject.getString("ATASAN1_USER_NAME");
            String ATASAN_1_EMAIL = jsonObject.getString("ATASAN1_EMAIL");
            String ATASAN_1_NIK = jsonObject.getString("ATASAN1_NIK");
            String ATASAN_1_JT = jsonObject.getString("ATASAN1_JOBTITLE");
            String ATASAN_1_NAMA = jsonObject.getString("ATASAN1_NAMA");
            String ATASAN_1_PHONE = jsonObject.getString("ATASAN1_PHONE");
            String DIVISION_CODE = jsonObject.getString("DIVISION_CODE");
            String DIVISION_NAME = jsonObject.getString("DIVISION_NAME");
            String DEPARTEMEN_CODE = jsonObject.getString("DEPARTEMENT_CODE");
            String DEPARTEMEN_NAME = jsonObject.getString("DEPARTEMENT_NAME");
            String ATASAN_2_USERNAME = jsonObject.getString("ATASAN2_USER_NAME");
            String ATASAN_2_EMAIL = jsonObject.getString("ATASAN2_EMAIL");
            String ATASAN_2_NIK = jsonObject.getString("ATASAN2_NIK");
            String ATASAN_2_JT = jsonObject.getString("ATASAN2_JOBTITLE");
            String ATASAN_2_NAMA = jsonObject.getString("ATASAN2_NAMA");
            String ATASAN_2_PHONE = jsonObject.getString("ATASAN2_PHONE");
            String LIST_APPROVE_ROLE1 = jsonObject.getString("LIST_APPROVER_ROLE1");
            String LIST_APPROVE_ROLE2 = jsonObject.getString("LIST_APPROVER_ROLE2");
            String LIST_REQ_ROLE = jsonObject.getString("LIST_REQUESTER_ROLE");
            String MY_ROLE = jsonObject.getString("MYROLE");

            String EMP_JJT_SUBORDINAT = "";
            if (jsonObject.has("EMP_JJT_SUBORDINAT")) {
                EMP_JJT_SUBORDINAT = jsonObject.getString("EMP_JJT_SUBORDINAT");
                JSONArray arrNew = new JSONArray();

                JSONArray arr = new JSONArray(EMPLOYEE_MULTICOST);
                for (int as = 0; as < arr.length(); as++) {
                    JSONObject jo = arr.getJSONObject(as);
                    arrNew.put(jo);
                }

                JSONArray arrSub = new JSONArray(EMP_JJT_SUBORDINAT);
                for (int asSub = 0; asSub < arrSub.length(); asSub++) {
                    JSONObject joSub = arrSub.getJSONObject(asSub);
                    arrNew.put(joSub);
                }

                EMPLOYEE_MULTICOST = arrNew.toString();
            }

            dataLOG = new String[]{token, status, USERNAME, EMPLOYEE_NAME, EMPLOYEE_EMAIL, EMPLOYEE_NIK,
                    EMPLOYEE_JT, EMPLOYEE_MULTICOST, EMPLOYEE_PHONE, EMPLOYEE_PHOTOS, ATASAN_1_USERNAME,
                    ATASAN_1_EMAIL, ATASAN_1_NIK, ATASAN_1_JT, ATASAN_1_NAMA, ATASAN_1_PHONE, DIVISION_CODE,
                    DIVISION_NAME, DEPARTEMEN_CODE, DEPARTEMEN_NAME, ATASAN_2_USERNAME, ATASAN_2_EMAIL,
                    ATASAN_2_NIK, ATASAN_2_JT, ATASAN_2_NAMA, ATASAN_2_PHONE, LIST_APPROVE_ROLE1, LIST_APPROVE_ROLE2,
                    LIST_REQ_ROLE, MY_ROLE};

            UserDB dbHelper = new UserDB(this);
            dbHelper.deleteUser();
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL(getString(R.string.sql_insert_log_iss), dataLOG);


            if (sukses.equalsIgnoreCase("LOGIN BERHASIL")) {
                new Validations().getInstance(getApplicationContext()).setTimebyId(26);
                //Not fix!!! Change how to detect as reliever with another ways
                JSONObject jsonRootObject = new JSONObject(allres);
                JSONArray tab = jsonRootObject.getJSONArray("tab_room");

                Boolean jalankan = false;
                for (int i = 0; i < tab.length(); i++) {
                    String name = tab.getJSONObject(i).getString("tab_name");
                    if (name.equalsIgnoreCase("Job Call")) {
                        jalankan = true;
                    }
                }

                new Validations().getInstance(getApplicationContext()).setShareLocOnOff(jalankan);
                Intent ii = LoadingGetTabRoomActivity.generateISS(getApplicationContext(), allres, username);
                startActivity(ii);

            } else {
                Toast.makeText(this, "Username dan password anda salah", Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Terjadi Kesalah di Server ISS. Terimakasih", Toast.LENGTH_LONG).show();
        }


    }


    @Override
    public void onproses(boolean proses) {
        openDialog = proses;
    }

}
