package com.byonchat.android.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.multidex.MultiDex;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.AdvRecy.DraggableGridExampleAdapter;
import com.byonchat.android.AdvRecy.ItemMain;
import com.byonchat.android.AdvRecy.MainDbHelper;
import com.byonchat.android.ByonChatMainRoomActivity;
import com.byonchat.android.ConversationActivity;
import com.byonchat.android.ListSelectedBotFragment;
import com.byonchat.android.LoadingGetTabRoomActivity;
import com.byonchat.android.LoginDinamicFingerPrint;
import com.byonchat.android.LoginDinamicRoomActivity;
import com.byonchat.android.Manhera.Manhera;
import com.byonchat.android.R;
import com.byonchat.android.RequestPasscodeRoomActivity;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.communication.NotificationReceiver;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.list.BotAdapter;
import com.byonchat.android.local.Byonchat;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.ContactBot;
import com.byonchat.android.provider.DataBaseDropDown;
import com.byonchat.android.provider.Message;
import com.byonchat.android.ui.adapter.OnItemClickListener;
import com.byonchat.android.ui.adapter.OnLongItemClickListener;
import com.byonchat.android.ui.view.ByonchatRecyclerView;
import com.byonchat.android.utils.BlurBuilder;
import com.byonchat.android.utils.DialogUtil;
import com.byonchat.android.utils.HttpHelper;
import com.byonchat.android.utils.LocationAssistant;
import com.byonchat.android.utils.RequestKeyTask;
import com.byonchat.android.utils.TaskCompleted;
import com.byonchat.android.utils.Utility;
import com.byonchat.android.utils.UtilsPD;
import com.byonchat.android.utils.Validations;
import com.byonchat.android.utils.ValidationsKey;
import com.byonchat.android.widget.BadgeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import me.leolin.shortcutbadger.ShortcutBadger;

import static com.byonchat.android.helpers.Constants.SQL_SELECT_TOTAL_MESSAGES_UNREAD_ALL;
import static com.byonchat.android.helpers.Constants.URL_LAPOR_SELECTED;

public abstract class MainBaseActivityNew extends AppCompatActivity implements LocationAssistant.Listener {

    @NonNull
    protected BadgeView bv1;

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
    protected FloatingActionButton fab_logo, fab_menu_1, fab_menu_2/*, fab_menu_3*/;

    @NonNull
    protected CardView card_search_main;

    @NonNull
    protected LinearLayout vBtnAddRooms;

    @NonNull
    protected ImageView backdropBlur;

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
    protected ImageButton but_search_main;

    @NonNull
    protected AppBarLayout appBarLayout;

    @NonNull
    protected ImageView backgroundImage;

    @NonNull
    protected MaterialSearchView searchView;

    @NonNull
    protected RecyclerView recyclerView;

    protected RecyclerView.LayoutManager layoutManager;
    protected RecyclerViewDragDropManager recyclerViewDragDropManager;
    protected ProgressDialog progressDialog;

    protected List<ItemMain> itemList = new ArrayList<>();
    protected List<String> positionList = new ArrayList<>();

    protected LaporSelectedRoom laporSelectedRoom;
    protected LocationAssistant assistant;

    protected DraggableGridExampleAdapter adapter;
    protected BotAdapter mAdapterRoomList;
    protected RecyclerView.Adapter wrappedAdapter;

    protected ArrayList<ContactBot> botArrayListist = new ArrayList<ContactBot>();
    protected List<String> numbers = new ArrayList<>();

    protected BroadcastHandler broadcastHandler = new BroadcastHandler();
    protected static final String ACTION_REFRESH_BADGER = MainBaseActivityNew.class
            .getName() + ".refreshBadger";
    protected static final String ACTION_REFRESH_NOTIF = MainBaseActivityNew.class
            .getName() + ".refreshNotif";

    public static Activity mActivity;
    protected boolean isVisible = false;
    protected float radius = 5f;
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
    protected int logo;
    protected int background;
    protected int room_id;
    protected String roomid = "";
    protected int i = 0;

    protected MainDbHelper database;
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

    protected void applyChatConfig() {
        database = new MainDbHelper(getBaseContext());

        assistant = new LocationAssistant(this, this, LocationAssistant.Accuracy.HIGH, 5000, false);
        assistant.setVerbose(true);

        if (getIntent().getExtras() != null) {
            success = getIntent().getStringExtra("success");
        }

        image_url = "";
        title = "ByonChat";
        logo = R.drawable.logo_byon;
//        background = R.drawable.wallpaper;
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

        Cursor cur = Byonchat.getBotListDB().getSingleRoom(botArrayListist.get(0).name);
        if (cur.getCount() > 0) {
            String bcakdrop = cur.getString(cur.getColumnIndex(BotListDB.ROOM_BACKDROP));

            if (bcakdrop == null || bcakdrop.equalsIgnoreCase("") || bcakdrop.equalsIgnoreCase("null")) {
                Manhera.getInstance().get()
                        .load(R.drawable.wallpaper)
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
        botArrayListist = Byonchat.getRoomsDB().retrieveRooms("2");
        Byonchat.getRoomsDB().close();

        if (botArrayListist.size() == 1) {
            vTxtStatusWarning.setVisibility(View.GONE);
            vBtnOpenRooms.setVisibility(View.GONE);
            vFrameWarning.setVisibility(View.INVISIBLE);
            vBtnAddRooms.setVisibility(View.INVISIBLE);

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
        }
    }

    protected void resolveOpenRooms() {
        resolveNavMenu(true);

        vBtnOpenRooms.setOnClickListener(v -> {
            roomsOpened();
        });
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
        nav_Menu.findItem(R.id.nav_item_refresh).setVisible(isTrue);
        nav_Menu.findItem(R.id.nav_item_legal).setVisible(false);
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
            drawerLayout.closeDrawer(Gravity.START);

            ContactBot item = botArrayListist.get(position);
            Byonchat.getRoomsDB().open();
            Byonchat.getRoomsDB().updateActiveRooms(item);
            Byonchat.getRoomsDB().close();

            resolveNavHeader();
            refreshList();
                /*if (Message.isJSONValid(botArrayListist.get(position).getDesc())){
                    JSONObject jObject = null;
                    try {
                        jObject = new JSONObject(botArrayListist.get(position).getDesc());
                        String desc = jObject.getString("desc");
                        String classs = jObject.getString("apps");
                        final String url = jObject.getString("url");

                        boolean isAppInstalled = appInstalledOrNot(classs);

                        if(isAppInstalled) {
                            Intent LaunchIntent = context.getPackageManager()
                                    .getLaunchIntentForPackage(classs);
                            startActivity(LaunchIntent);
                        } else {
                            AlertDialog.Builder alertbox = new AlertDialog.Builder(context);
                            alertbox.setMessage("Please Install the Plug-in first ");
                            alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    openWebPage(url);
                                }

                            });
                            alertbox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                }
                            });
                            alertbox.show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Intent intent = new Intent(context, ByonChatMainRoomActivity.class);
                    intent.putExtra(ConversationActivity.KEY_JABBER_ID, botArrayListist.get(position).getName());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }*/

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
            Manhera.getInstance()
                    .get()
                    .load(botArrayListist.get(0).link)
                    .fitCenter()
                    .into(vNavLogo);

            vNavTitle.setText(botArrayListist.get(0).realname);

            title = botArrayListist.get(0).realname;
            username = botArrayListist.get(0).getName();

            resolveToolbar(botArrayListist.get(0));
        } else {
            // Do something here if you have not added rooms
            Manhera.getInstance().get()
                    .load(R.drawable.wallpaper)
                    .fitCenter()
                    .into(backgroundImage);
        }
    }

    protected void resolveRecyclerView() {
        layoutManager = new GridLayoutManager(this, 3, RecyclerView.VERTICAL, false);

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
    }

    protected void resolveListTabRooms(ContactBot item, Cursor sdf) {
        Cursor cur = Byonchat.getBotListDB().getSingleRoom(username);
        String name = cur.getString(cur.getColumnIndex(BotListDB.ROOM_REALNAME));
        color = Utility.jsonResultType(cur.getString(cur.getColumnIndex(BotListDB.ROOM_COLOR)), "a");
        colorText = Utility.jsonResultType(cur.getString(cur.getColumnIndex(BotListDB.ROOM_COLOR)), "b");
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

        /*if (color == null || color.equalsIgnoreCase("") || color.equalsIgnoreCase("null")) {
            color = "006b9c";
        }*/
        if (bcakdrop == null || bcakdrop.equalsIgnoreCase("") || bcakdrop.equalsIgnoreCase("null")) {
            Manhera.getInstance().get()
                    .load(R.drawable.earth_byon)
                    .fitCenter()
                    .into(backgroundImage);
        }
        if (colorText == null || colorText.equalsIgnoreCase("") || colorText.equalsIgnoreCase("null")) {
            colorText = "ffffff";
        }
        new LoadImageFromURL(backdropBlur).execute(bcakdrop);

        try {
            JSONArray jsonArray = new JSONArray(content);
            Log.w("aa", content);
            itemList.clear();
            positionList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                String category = jsonArray.getJSONObject(i).getString("category_tab").toString();
                String title = jsonArray.getJSONObject(i).getString("tab_name").toString();
                String include_latlong = jsonArray.getJSONObject(i).getString("include_latlong").toString();
                String include_pull = jsonArray.getJSONObject(i).getString("include_pull").toString();
                String url_tembak = jsonArray.getJSONObject(i).getString("url_tembak").toString();
                String id_rooms_tab = jsonArray.getJSONObject(i).getString("id_rooms_tab").toString();
                String status = jsonArray.getJSONObject(i).getString("status").toString();

                itemList.add(i, new ItemMain(i, category, title, url_tembak, include_pull, username,
                        id_rooms_tab, color, colorText, targetURL, include_latlong, status, name, icon));
                positionList.add(i, title);

                if (category.equalsIgnoreCase("1")) {
                    Constants.map.put(i, null);
                } else if (category.equalsIgnoreCase("2")) {
                    Constants.map.put(i, null);
                } else if (category.equalsIgnoreCase("3")) {
                    Constants.map.put(i, null);
                } else if (category.equalsIgnoreCase("4")) {
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
                    Constants.map.put(i, null);
                } else if (category.equalsIgnoreCase("14")) {
                    Constants.map.put(i, null);
                } else if (category.equalsIgnoreCase("6")) {
                    Constants.map.put(i, null);
                } else if (category.equalsIgnoreCase("7")) {
                    Constants.map.put(i, null);
                } else if (category.equalsIgnoreCase("8")) {
                    Constants.map.put(i, null);
                } else if (category.equalsIgnoreCase("9")) {
                    Constants.map.put(i, null);
                } else if (category.equalsIgnoreCase("10")) {
                    Constants.map.put(i, null);
                } else if (category.equalsIgnoreCase("11")) {
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
                    Constants.map.put(i, null);
                } else if (category.equalsIgnoreCase("17")) {
                    Constants.map.put(i, null);
                }
            }
            Constants.map.put(jsonArray.length(), null);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();
    }

    protected void resolveCollapsingToolbar(String color) {
        int colors = Integer.parseInt(color.replaceFirst("^#", ""), 16);
        collapsingToolbarLayout.setContentScrimColor(colors);
        collapsingToolbarLayout.setStatusBarScrimColor(colors);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandedappbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                backgroundImage.setForeground(new ColorDrawable(Color.parseColor("#" + percent + color)));
            } catch (Exception e) {
                colorForeground = color.replace("#", "#" + percent);
                backgroundImage.setForeground(new ColorDrawable(Color.parseColor(colorForeground)));
            }
        }
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
    }

    protected void resolveToolbarExpanded() {
        appBarLayout.setExpanded(true, false);
        if (searchView.isSearchOpen())
            searchView.showSearch(false);
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

        ShortcutBadger.applyCount(context, badgeCount);

        bv1.setVisibility(badgeCount == 0 ? View.GONE : View.VISIBLE);
        bv1.setText(badgeCount + "");

        if (badgeCount > 0)
            bv1.show();
    }

    class BroadcastHandler extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MessengerConnectionService.ACTION_MESSAGE_RECEIVED
                    .equals(intent.getAction())) {
                addShortcutBadger(context);
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
        Byonchat.getRoomsDB().open();
        botArrayListist = Byonchat.getRoomsDB().retrieveRooms("2", true);
        Byonchat.getRoomsDB().close();
        try {
            JSONObject jObj = new JSONObject(botArrayListist.get(0).getTargetUrl());
            String targetURL = jObj.getString("path");

            AlertDialog.Builder alertbox = new AlertDialog.Builder(MainBaseActivityNew.this);
            alertbox.setTitle("Refresh Room " + botArrayListist.get(0).realname);
            alertbox.setMessage("Are you sure you want to Refresh?");
            alertbox.setPositiveButton("Ok", (arg0, arg1) -> {
                if (NetworkInternetConnectionStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
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

    }
}