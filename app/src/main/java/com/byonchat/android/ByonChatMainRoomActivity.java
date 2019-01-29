package com.byonchat.android;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.AdvRecy.ItemMain;
import com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity;
import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.createMeme.FilteringImage;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.list.ItemListMemberCard;
import com.byonchat.android.local.Byonchat;
import com.byonchat.android.provider.ContentRoom;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.ui.activity.MainByonchatRoomBaseActivity;
import com.byonchat.android.utils.DialogUtil;
import com.byonchat.android.utils.Utility;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class ByonChatMainRoomActivity extends MainByonchatRoomBaseActivity {

    public static Intent generateIntent(Context context, ItemMain item) {
        Intent intent = new Intent(context, ByonChatMainRoomActivity.class);
        intent.putExtra(EXTRA_ITEM, item);
        return intent;
    }

    protected Intent generateShortcutIntent() {
        Intent intent = new Intent(getApplicationContext(), ByonChatMainRoomActivity.class);
        intent.putExtra(ConversationActivity.KEY_JABBER_ID, username);
        intent.putExtra(ByonChatMainRoomActivity.EXTRA_POSITION, position);
        intent.putExtra(ByonChatMainRoomActivity.EXTRA_COLOR, color);
        intent.putExtra(ByonChatMainRoomActivity.EXTRA_COLORTEXT, colorText);
        intent.putExtra(ByonChatMainRoomActivity.EXTRA_TARGETURL, targetURL);
        intent.putExtra(ByonChatMainRoomActivity.EXTRA_CATEGORY, category);
        intent.putExtra(ByonChatMainRoomActivity.EXTRA_TITLE, title);
        intent.putExtra(ByonChatMainRoomActivity.EXTRA_URL_TEMBAK, url_tembak);
        intent.putExtra(ByonChatMainRoomActivity.EXTRA_ID_ROOMS_TAB, id_rooms_tab);
        intent.putExtra(ByonChatMainRoomActivity.EXTRA_INCLUDE_PULL, include_pull);
        intent.putExtra(ByonChatMainRoomActivity.EXTRA_INCLUDE_LATLONG, include_latlong);
        intent.putExtra(ByonChatMainRoomActivity.EXTRA_STATUS, status);
        intent.putExtra(ByonChatMainRoomActivity.EXTRA_NAME, name);
        intent.putExtra(ByonChatMainRoomActivity.EXTRA_ICON, icon);
        intent.putExtra(ByonChatMainRoomActivity.EXTRA_VALUE, generatePayload());
        return intent;
    }

    protected String generatePayload() {
        List value = (List) Constants.map.get(position);

        if (value.size() > 0) {
            Gson gson = new Gson();
            JsonObject jObj = new JsonObject();
            JsonArray jArr = new JsonArray();
            for (int i = 0; i < value.size(); i++) {
                jArr.add(new JsonPrimitive(value.get(i).toString()));
            }
            jObj.add("payload", jArr);

            return gson.toJson(jObj);
        }

        return "";
    }

    @Override
    protected int getResourceLayout() {
        return R.layout.main_byonchat_room_activity;
    }

    @Override
    protected void onLoadToolbar() {
        vAppbar = getAppBar();
        vToolbar = getToolbar();

        if (color.equalsIgnoreCase("FFFFFF") && colorText.equalsIgnoreCase("000000")) {
            View lytToolbarDark = getLayoutInflater().inflate(R.layout.toolbar_custom_dark, vAppbar);
            Toolbar toolbarDark = lytToolbarDark.findViewById(R.id.toolbar);
            vAppbar.removeView(vToolbar);
        }
    }

    @Override
    protected void onLoadConfig(Bundle savedInstanceState) {
        resolveChatRoom(savedInstanceState);

        applyConfig();
    }

    @Override
    protected void onLoadView() {
        vAppbar = getAppBar();
        vToolbar = getToolbar();
        vToolbarBack = getToolbarBack();
        vImgToolbarBack = getImgToolbarBack();
        vToolbarTitle = getToolbarTitle();
        vSearchView = getMaterialSearchView();
        vContainerFragment = getFrameFragment();
        vFloatingButton = getFloatingButton();
        searchAppBarLayout = getFrameSearchAppBar();
        searchToolBar = getSearchToolbar();
        searchEditText = getSearchForm();
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        super.onViewReady(savedInstanceState);

        resolveSearchBar();
        resolveToolbar();
        resolveFragment();
        resolveFloatingButton();
        resolveMaterialSearchView();
    }

    @Override
    public void onResume() {
        super.onResume();

        vSearchView.closeSearch();

        if (searchAppBarLayout.getVisibility() == View.VISIBLE)
            hideSearchBar(positionFromRight);
    }

    public static String jsonResultType(String json, String type) {
        String hasil = "";
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(json);
        } catch (JSONException e) {
            hasil = "error";
            e.printStackTrace();
        }
        if (jObject != null) {
            try {
                hasil = jObject.getString(type);
            } catch (JSONException e) {
                hasil = "error";
                e.printStackTrace();
            }
        }

        if (type.equalsIgnoreCase("e") && hasil.equalsIgnoreCase("error")) {
            hasil = "https://" + MessengerConnectionService.HTTP_SERVER;
        }

        return hasil;
    }

    public String idLoof(Integer posss) {
        List value = (List) Constants.map.get(position);
        if (value != null) {
            String statusBaru = "";
            ArrayList<ContentRoom> listItem = new ArrayList<>();
            ArrayList<RoomsDetail> listItem2;
            listItem2 = Byonchat.getBotListDB().allRoomDetailFormWithFlag("", value.get(1).toString(), value.get(2).toString(), "parent");
            for (RoomsDetail aa : listItem2) {

                ArrayList<RoomsDetail> listItem3 = Byonchat.getBotListDB().allRoomDetailFormWithFlag(aa.getId(), value.get(1).toString(), value.get(2).toString(), "list");
                for (RoomsDetail ii : listItem3) {
                    if (ii.getFlag_content().equalsIgnoreCase("1")) {
                        Log.w("2abub", ii.getContent());
                        JSONObject jO = null;
                        try {
                            jO = new JSONObject(ii.getContent());
                            statusBaru = jO.getString("bb");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                String date = "";
                date = aa.getContent();
                ContentRoom contentRoom = new ContentRoom(aa.getId(), "", date, "", "", "", "");
                listItem.add(contentRoom);
            }

            Collections.sort(listItem, (e1, e2) -> {
                Date satu = Utility.convertStringToDate(Utility.parseDateToddMMyyyy(e1.getTime()));
                Date dua = Utility.convertStringToDate(Utility.parseDateToddMMyyyy(e2.getTime()));
                if (satu.compareTo(dua) > 0) {
                    return -1;
                } else {
                    return 1;
                }
            });

            if (value.size() > 1) {
                try {
                    Intent intent = new Intent(getApplicationContext(), DinamicRoomTaskActivity.class);
                    intent.putExtra("tt", value.get(0).toString());
                    intent.putExtra("uu", value.get(1).toString());
                    intent.putExtra("ii", value.get(2).toString());
                    intent.putExtra("idTask", listItem.get(posss).getIdHex());
                    intent.putExtra("col", value.get(3).toString());
                    intent.putExtra("ll", value.get(4).toString());
                    intent.putExtra("from", value.get(5).toString());
                    if (!statusBaru.equalsIgnoreCase("")) {
                        intent.putExtra("isReject", statusBaru);
                    }


                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public String idLoof(ContentRoom contentRoomFind) {
        List value = (List) Constants.map.get(position);
        if (value != null) {
            String statusBaru = "";
            ArrayList<ContentRoom> listItem = new ArrayList<>();
            ArrayList<RoomsDetail> listItem2;
            listItem2 = Byonchat.getBotListDB().allRoomDetailFormWithFlag("", value.get(1).toString(), value.get(2).toString(), "parent");
            for (RoomsDetail aa : listItem2) {

                ArrayList<RoomsDetail> listItem3 = Byonchat.getBotListDB().allRoomDetailFormWithFlag(aa.getId(), value.get(1).toString(), value.get(2).toString(), "list");
                for (RoomsDetail ii : listItem3) {
                    if (ii.getFlag_content().equalsIgnoreCase("1")) {
                        Log.w("2abub", ii.getContent());
                        JSONObject jO = null;
                        try {
                            jO = new JSONObject(ii.getContent());
                            statusBaru = jO.getString("bb");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                String date = "";
                date = aa.getContent();
                ContentRoom contentRoom = new ContentRoom(aa.getId(), "", date, "", "", "", "");
                listItem.add(contentRoom);
            }

            Collections.sort(listItem, (e1, e2) -> {
                Date satu = Utility.convertStringToDate(Utility.parseDateToddMMyyyy(e1.getTime()));
                Date dua = Utility.convertStringToDate(Utility.parseDateToddMMyyyy(e2.getTime()));
                if (satu.compareTo(dua) > 0) {
                    return -1;
                } else {
                    return 1;
                }
            });

            if (value.size() > 1) {
                try {
                    Intent intent = new Intent(getApplicationContext(), DinamicRoomTaskActivity.class);
                    intent.putExtra("tt", value.get(0).toString());
                    intent.putExtra("uu", value.get(1).toString());
                    intent.putExtra("ii", value.get(2).toString());
                    intent.putExtra("idTask", contentRoomFind.getIdHex());
                    intent.putExtra("col", value.get(3).toString());
                    intent.putExtra("ll", value.get(4).toString());
                    intent.putExtra("from", value.get(5).toString());
                    if (!statusBaru.equalsIgnoreCase("")) {
                        intent.putExtra("isReject", statusBaru);
                    }


                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    protected void createShortcut() {
        final Dialog dialogConfirmation;
        dialogConfirmation = DialogUtil.customDialogConversationConfirmation(this);
        dialogConfirmation.show();

        TextView txtConfirmation = (TextView) dialogConfirmation.findViewById(R.id.confirmationTxt);
        TextView descConfirmation = (TextView) dialogConfirmation.findViewById(R.id.confirmationDesc);
        txtConfirmation.setText("Create Shortcut " + title);
        descConfirmation.setVisibility(View.VISIBLE);
        descConfirmation.setText("Are you sure you want to Create Shortcut?");

        Button btnNo = (Button) dialogConfirmation.findViewById(R.id.btnNo);
        Button btnYes = (Button) dialogConfirmation.findViewById(R.id.btnYes);
        btnNo.setText("Cancel");
        btnNo.setOnClickListener(v -> {
            dialogConfirmation.dismiss();
        });

        btnYes.setOnClickListener(v -> {
            dialogConfirmation.dismiss();
            Picasso.with(ByonChatMainRoomActivity.this)
                    .load(icon)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .transform(new RoundedCornersTransformation(30, 0, RoundedCornersTransformation.CornerType.ALL))
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            if (bitmap != null) {
                                   /* Toast.makeText(ByonChatMainRoomActivity.this, "Create Shortcut Success", Toast.LENGTH_SHORT).show();
                                    Intent aa = new Intent(getApplicationContext(), ByonChatMainRoomActivity.class);
                                    aa.putExtra(ConversationActivity.KEY_JABBER_ID, username);
                                    Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
                                    shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
                                    shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);
                                    shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, aa);
                                    sendBroadcast(shortcutintent);
                                    finish();*/
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                                    ShortcutInfo.Builder mShortcutInfoBuilder = new ShortcutInfo.Builder(ByonChatMainRoomActivity.this, title);
                                    mShortcutInfoBuilder.setShortLabel(title);
                                    mShortcutInfoBuilder.setLongLabel(title);
                                    mShortcutInfoBuilder.setIcon(Icon.createWithBitmap(bitmap));

                                    Intent shortcutIntent = generateShortcutIntent();
                                    shortcutIntent.setAction(Intent.ACTION_CREATE_SHORTCUT);
                                    mShortcutInfoBuilder.setIntent(shortcutIntent);

                                    ShortcutInfo mShortcutInfo = mShortcutInfoBuilder.build();
                                    ShortcutManager mShortcutManager = getSystemService(ShortcutManager.class);
                                    mShortcutManager.requestPinShortcut(mShortcutInfo, null);

                                } else {
                                    Intent shortcutIntent = generateShortcutIntent();
                                    shortcutIntent.setAction(Intent.ACTION_MAIN);

                                    Intent addIntent = new Intent();
                                    addIntent
                                            .putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
                                    addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
                                    addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);

                                    addIntent
                                            .setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                                    addIntent.putExtra("duplicate", false);  //may it's already there so   don't duplicate
                                    getApplicationContext().sendBroadcast(addIntent);

                                }
                                finish();
                                Toast.makeText(ByonChatMainRoomActivity.this, "Shortcut Created", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                            Toast.makeText(ByonChatMainRoomActivity.this, "Create Shortcut failed", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                            Toast.makeText(ByonChatMainRoomActivity.this, "Please Wait", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    public String deleteById(final Integer posss) {
        final List value = (List) Constants.map.get(position);
        if (value != null) {
            final ArrayList<RoomsDetail> listItem2;
            listItem2 = Byonchat.getBotListDB().allRoomDetailFormWithFlag("", value.get(1).toString(), value.get(2).toString(), "parent");

            String title = "";
            final AlertDialog.Builder alertbox = new AlertDialog.Builder(ByonChatMainRoomActivity.this);
            alertbox.setTitle("Delete");
            alertbox.setMessage(title);
            alertbox.setPositiveButton("Yes", (arg0, arg1) -> {
                if (value.size() > 1) {
                    ArrayList<ContentRoom> listItem = new ArrayList<>();
                    for (RoomsDetail aa : listItem2) {
                        String date = "";
                        date = aa.getContent();
                        ContentRoom contentRoom = new ContentRoom(aa.getId(), "", date, "", "", "", "");
                        listItem.add(contentRoom);
                    }

                    Collections.sort(listItem, (e1, e2) -> {
                        Date satu = Utility.convertStringToDate(Utility.parseDateToddMMyyyy(e1.getTime()));
                        Date dua = Utility.convertStringToDate(Utility.parseDateToddMMyyyy(e2.getTime()));
                        if (satu.compareTo(dua) > 0) {
                            return -1;
                        } else {
                            return 1;
                        }
                    });

                    Byonchat.getBotListDB().deleteRoomsDetailbyId(listItem.get(posss).getIdHex(), value.get(2).toString(), value.get(1).toString());
                    finish();
                    getIntent().putExtra(KEY_POSITION, position);
                    startActivity(getIntent());
                }
            });
            alertbox.setNegativeButton("Cancel", (arg0, arg1) -> {
            });
            alertbox.show();
        }

        return null;
    }

    public String deleteById(ContentRoom contentRoomFind) {
        final List value = (List) Constants.map.get(position);
        if (value != null) {
            final ArrayList<RoomsDetail> listItem2;
            listItem2 = Byonchat.getBotListDB().allRoomDetailFormWithFlag("", value.get(1).toString(), value.get(2).toString(), "parent");

            String title = "";
            final AlertDialog.Builder alertbox = new AlertDialog.Builder(ByonChatMainRoomActivity.this);
            alertbox.setTitle("Delete");
            alertbox.setMessage(title);
            alertbox.setPositiveButton("Yes", (arg0, arg1) -> {
                if (value.size() > 1) {
                    ArrayList<ContentRoom> listItem = new ArrayList<>();
                    for (RoomsDetail aa : listItem2) {
                        String date = "";
                        date = aa.getContent();
                        ContentRoom contentRoom = new ContentRoom(aa.getId(), "", date, "", "", "", "");
                        listItem.add(contentRoom);
                    }

                    Collections.sort(listItem, (e1, e2) -> {
                        Date satu = Utility.convertStringToDate(Utility.parseDateToddMMyyyy(e1.getTime()));
                        Date dua = Utility.convertStringToDate(Utility.parseDateToddMMyyyy(e2.getTime()));
                        if (satu.compareTo(dua) > 0) {
                            return -1;
                        } else {
                            return 1;
                        }
                    });

                    Byonchat.getBotListDB().deleteRoomsDetailbyId(contentRoomFind.getIdHex(), value.get(2).toString(), value.get(1).toString());
                    finish();
                    getIntent().putExtra(KEY_POSITION, position);
                    startActivity(getIntent());
                }
            });
            alertbox.setNegativeButton("Cancel", (arg0, arg1) -> {
            });
            alertbox.show();
        }

        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_dinamic, menu);
        MenuItem action_add = menu.findItem(R.id.action_add);
        MenuItem action_refresh = menu.findItem(R.id.action_refresh);
        MenuItem action_search = menu.findItem(R.id.action_search);

        action_add.setVisible(false);
        action_refresh.setVisible(false);
        action_search.setVisible(false);
        invalidateOptionsMenu();

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        String category = listItem != null ? listItem.category_tab : getIntent().getExtras().getString(EXTRA_CATEGORY);
        String include_pull = listItem != null ? listItem.include_pull : getIntent().getExtras().getString(EXTRA_INCLUDE_PULL);
        if (category.equalsIgnoreCase("4")) {
            if (include_pull.equalsIgnoreCase("4") || include_pull.equalsIgnoreCase("5") || include_pull.equalsIgnoreCase("6")) {
                menu.findItem(R.id.action_search).setVisible(true);
            } else if (include_pull.equalsIgnoreCase("0")) {
                menu.findItem(R.id.action_search).setVisible(true);
            } else if (include_pull.equalsIgnoreCase("1") || include_pull.equalsIgnoreCase("3")) {
                menu.findItem(R.id.action_search).setVisible(true);
            }
        } else if (category.equalsIgnoreCase("19")) {
            menu.findItem(R.id.action_search).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                showSearchBar(positionFromRight);
                return true;
            case R.id.action_short:
                createShortcut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @NonNull
    @Override
    protected Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }

    @NonNull
    @Override
    protected AppBarLayout getAppBar() {
        return (AppBarLayout) findViewById(R.id.appbar);
    }

    @NonNull
    @Override
    protected RelativeLayout getToolbarBack() {
        return (RelativeLayout) findViewById(R.id.toolbar_back);
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

    @NonNull
    @Override
    protected FrameLayout getFrameFragment() {
        return (FrameLayout) findViewById(R.id.container_open_fragment);
    }

    @NonNull
    @Override
    protected MaterialSearchView getMaterialSearchView() {
        return (MaterialSearchView) findViewById(R.id.search_view_main);
    }

    @NonNull
    @Override
    protected FloatingActionButton getFloatingButton() {
        return (FloatingActionButton) findViewById(R.id.fabAction);
    }

    @NonNull
    @Override
    protected LinearLayout getFrameSearchAppBar() {
        return (LinearLayout) findViewById(R.id.frame_search_appbar);
    }

    @NonNull
    @Override
    protected Toolbar getSearchToolbar() {
        return (Toolbar) findViewById(R.id.search_toolbar);
    }

    @NonNull
    @Override
    protected EditText getSearchForm() {
        return (EditText) findViewById(R.id.search_edittext);
    }
}


//package com.byonchat.android;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.app.NotificationManager;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.pm.ShortcutInfo;
//import android.content.pm.ShortcutManager;
//import android.content.res.ColorStateList;
//import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Path;
//import android.graphics.Point;
//import android.graphics.PorterDuff;
//import android.graphics.PorterDuffXfermode;
//import android.graphics.Rect;
//import android.graphics.RectF;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.graphics.drawable.Icon;
//import android.location.Location;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.TabLayout;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.KeyEvent;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.PopupWindow;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity;
//import com.byonchat.android.FragmentDinamicRoom.FragmentDirectory;
//import com.byonchat.android.FragmentDinamicRoom.FragmentRoomAPI;
//import com.byonchat.android.FragmentDinamicRoom.FragmentRoomAbout;
//import com.byonchat.android.FragmentDinamicRoom.FragmentRoomPOS;
//import com.byonchat.android.FragmentDinamicRoom.FragmentStreamingRadio;
//import com.byonchat.android.FragmentDinamicRoom.FragmentStreamingVideo;
//import com.byonchat.android.FragmentDinamicRoom.RoomPOSdetail;
//import com.byonchat.android.communication.MessengerConnectionService;
//import com.byonchat.android.communication.NetworkInternetConnectionStatus;
//import com.byonchat.android.communication.NotificationReceiver;
//import com.byonchat.android.createMeme.FilteringImage;
//import com.byonchat.android.personalRoom.FragmentMyCardID;
//import com.byonchat.android.personalRoom.FragmentMyNews;
//import com.byonchat.android.personalRoom.FragmentMyNewsNew;
//import com.byonchat.android.personalRoom.FragmentMyNote;
//import com.byonchat.android.personalRoom.FragmentMyPicture;
//import com.byonchat.android.personalRoom.FragmentMyVideo;
//import com.byonchat.android.personalRoom.FragmentProductCatalog;
//import com.byonchat.android.provider.BotListDB;
//import com.byonchat.android.provider.ContactBot;
//import com.byonchat.android.provider.ContentRoom;
//import com.byonchat.android.provider.Message;
//import com.byonchat.android.provider.MessengerDatabaseHelper;
//import com.byonchat.android.provider.RoomsDB;
//import com.byonchat.android.provider.RoomsDetail;
//import com.byonchat.android.room.FragmentRoomMultipleTask;
//import com.byonchat.android.room.FragmentRoomTask;
//import com.byonchat.android.room.FragmentRoomTaskWater;
//import com.byonchat.android.smsSolders.WelcomeActivitySMS;
//import com.byonchat.android.tempSchedule.TempScheduleRoom;
//import com.byonchat.android.ui.activity.ByonchatVideoBeforeDownloadActivity;
//import com.byonchat.android.ui.fragment.ByonchatVideoFragment;
//import com.byonchat.android.utils.BlurBuilder;
//import com.byonchat.android.utils.DialogUtil;
//import com.byonchat.android.utils.HttpHelper;
//import com.byonchat.android.utils.LocationAssistant;
//import com.byonchat.android.utils.RequestKeyTask;
//import com.byonchat.android.utils.TaskCompleted;
//import com.byonchat.android.utils.Utility;
//import com.byonchat.android.utils.UtilsPD;
//import com.byonchat.android.utils.Validations;
//import com.byonchat.android.utils.ValidationsKey;
//import com.squareup.picasso.NetworkPolicy;
//import com.squareup.picasso.Picasso;
//import com.squareup.picasso.Target;
//
//import org.apache.http.HttpResponse;
//import org.apache.http.HttpStatus;
//import org.apache.http.NameValuePair;
//import org.apache.http.StatusLine;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.conn.params.ConnManagerParams;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.params.HttpConnectionParams;
//import org.apache.http.params.HttpParams;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//import carbon.widget.FrameLayout;
//import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
//
///**
// * Created by Iman Firmansyah on 3/21/2016.
// */
//public class ByonChatMainRoomActivity extends AppCompatActivity implements LocationAssistant.Listener {
//
//    String URLLAPORSELECTED = "https://" + MessengerConnectionService.HTTP_SERVER + "/room/selectapop.php";
//    LinearLayout backButton;
//    Target imageView;
//    Target profilePic;
//    FrameLayout frameImage;
//    ImageView backdropBlur;
//    private RoomsDB roomsDB;
//    private static ArrayList<ContactBot> catArray = new ArrayList<ContactBot>();
//    public final static String KEY_POSITION = "POSITION";
//    FloatingActionButton fab;
//    private TabLayout tabLayout;
//    private ViewPager viewPager;
//    TextView title;
//    ViewPagerAdapter adapter;
//    AlertDialog alertDialogExit;
//    Integer firstTab = 0;
//    String colorText = "";
//    String description = "";
//    String color = "006b9c";
//    Point p;
//    String username = "";
//    String targetURL = "";
//    int position = 0;
//    Integer shakeCount = 0;
//    Map<Integer, List<String>> map = new HashMap<Integer, List<String>>();
//    Boolean next = true;
//    private LaporSelectedRoom laporSelectedRoom;
//
//    MessengerDatabaseHelper messengerHelper = null;
//    BotListDB botListDB = null;
//    String name = "";
//    String content = "";
//    String bcakdrop = "";
//    String current = "";
//    String icon = "";
//    String desc = "", realname = "", link = "", type = "";
//    Context context;
//    private LocationAssistant assistant;
//    private ProgressDialog progressDialog;
//    String protect;
//    String success;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (android.os.Build.VERSION.SDK_INT >= 20) {
//            setTheme(R.style.HeaderTransparent);
//        }
//
//        if (context != null) {
//            context = ByonChatMainRoomActivity.this;
//        }
//        setContentView(R.layout.byonchat_main_room_activity);
//
//        assistant = new LocationAssistant(this, this, LocationAssistant.Accuracy.HIGH, 5000, false);
//        assistant.setVerbose(true);
//
//        final Intent intent = getIntent();
//        success = intent.getStringExtra("success");
//        username = intent.getStringExtra(ConversationActivity.KEY_JABBER_ID);
//        targetURL = intent.getStringExtra(ConversationActivity.KEY_TITLE);
//
//        if (intent.getStringExtra("firstTab") != null) {
//            current = intent.getStringExtra("firstTab");
//        }
////sms soldier
//        if (username.equalsIgnoreCase("1_351102554admin")) {
//            finish();
//            Intent ii = new Intent(ByonChatMainRoomActivity.this, WelcomeActivitySMS.class);
//            ii.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(ii);
//        }
//
//        backButton = (LinearLayout) findViewById(R.id.layout_back_button);
//        imageView = (Target) findViewById(R.id.backdrop);
//        backdropBlur = (ImageView) findViewById(R.id.backdropblur);
//        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        profilePic = (Target) findViewById(R.id.imagePhoto);
//        frameImage = (FrameLayout) findViewById(R.id.frameImage);
//        fab = (FloatingActionButton) findViewById(R.id.fabAction);
//        tabLayout = (TabLayout) findViewById(R.id.tabs);
//        title = (TextView) findViewById(R.id.titleToolbar);
//        toolbar.setTitle("");
//        setSupportActionBar(toolbar);
//
//        if (roomsDB == null) {
//            roomsDB = new RoomsDB(ByonChatMainRoomActivity.this);
//        }
//
//        if (messengerHelper == null) {
//            messengerHelper = MessengerDatabaseHelper.getInstance(getApplicationContext());
//        }
//        if (botListDB == null) {
//            botListDB = BotListDB.getInstance(getApplicationContext());
//        }
//        viewPager = (ViewPager) findViewById(R.id.viewpager);
//
//        Cursor cur = botListDB.getSingleRoom(username);
//
//        if (cur.getCount() > 0) {
//            name = cur.getString(cur.getColumnIndex(BotListDB.ROOM_REALNAME));
//            if (jsonResultType(cur.getString(cur.getColumnIndex(BotListDB.ROOM_COLOR)), "a").equalsIgnoreCase("error")) {
//                if (NetworkInternetConnectionStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
//                    finish();
//                    Intent ii = new Intent(ByonChatMainRoomActivity.this, LoadingGetTabRoomActivity.class);
//                    ii.putExtra(ConversationActivity.KEY_JABBER_ID, username);
//                    if (targetURL != null) {
//                        ii.putExtra(ConversationActivity.KEY_TITLE, targetURL);
//                    }
//                    ii.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(ii);
//                } else {
//                    Toast.makeText(ByonChatMainRoomActivity.this, "No Internet Akses", Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//                return;
//            }
//
//            color = jsonResultType(cur.getString(cur.getColumnIndex(BotListDB.ROOM_COLOR)), "a");
//            colorText = jsonResultType(cur.getString(cur.getColumnIndex(BotListDB.ROOM_COLOR)), "b");
//            description = jsonResultType(cur.getString(cur.getColumnIndex(BotListDB.ROOM_COLOR)), "c");
//            targetURL = jsonResultType(cur.getString(cur.getColumnIndex(BotListDB.ROOM_COLOR)), "e");
//
//
//            protect = jsonResultType(cur.getString(cur.getColumnIndex(BotListDB.ROOM_COLOR)), "p");
//
//
//            content = cur.getString(cur.getColumnIndex(BotListDB.ROOM_CONTENT));
//            icon = cur.getString(cur.getColumnIndex(BotListDB.ROOM_ICON));
//            bcakdrop = cur.getString(cur.getColumnIndex(BotListDB.ROOM_BACKDROP));
//
//            Log.w("icon", icon);
//
//            if (current.equalsIgnoreCase("")) {
//                current = cur.getString(cur.getColumnIndex(BotListDB.ROOM_FIRST_TAB));
//            }
//
//            if (color == null || color.equalsIgnoreCase("") || color.equalsIgnoreCase("null")) {
//                color = "006b9c";
//            }
//            if (bcakdrop == null || bcakdrop.equalsIgnoreCase("") || bcakdrop.equalsIgnoreCase("null")) {
//                loadBackdrop(R.drawable.earth_byon);
//            }
//            if (colorText == null || colorText.equalsIgnoreCase("") || colorText.equalsIgnoreCase("null")) {
//                colorText = "ffffff";
//            }
//
//
//            Picasso.with(ByonChatMainRoomActivity.this.getApplicationContext()).load(Color.parseColor("#" + color))
//                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
//                    .into(imageView);
//            Picasso.with(ByonChatMainRoomActivity.this.getApplicationContext()).load(bcakdrop).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(imageView);
//
//            new LoadImageFromURL(backdropBlur).execute(bcakdrop);
//            title.setText(name);
//
//            FilteringImage.SystemBarBackground(getWindow(), Color.parseColor("#" + color));
//            adapter = new ViewPagerAdapter(getSupportFragmentManager());
//            Picasso.with(ByonChatMainRoomActivity.this.getApplicationContext()).load(icon).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(profilePic);
//
//            try {
//                JSONArray jsonArray = new JSONArray(content);
//                Log.w("aa", content);
//
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    if (current.equalsIgnoreCase(jsonArray.getJSONObject(i).getString("id_rooms_tab").toString())) {
//                        firstTab = i;
//                    }
//                    Boolean show = false;
//                    String category = jsonArray.getJSONObject(i).getString("category_tab").toString();
//                    Fragment aa = null;
//                    String title = jsonArray.getJSONObject(i).getString("tab_name").toString();
//                    String include_latlong = jsonArray.getJSONObject(i).getString("include_latlong").toString();
//                    String include_pull = jsonArray.getJSONObject(i).getString("include_pull").toString();
//
//                    if (category.equalsIgnoreCase("1")) {
//                        show = true;
//                        aa = FragmentRoomAbout.newInstance(messengerHelper.getMyContact().getJabberId(), title, jsonArray.getJSONObject(i).getString("url_tembak").toString(), username, jsonArray.getJSONObject(i).getString("id_rooms_tab").toString(), color, ByonChatMainRoomActivity.this);
//                        map.put(i, null);
//                    } else if (category.equalsIgnoreCase("2")) {
//                        map.put(i, null);
//                        show = true;
//                        aa = FragmentMyPicture.newInstance(messengerHelper.getMyContact().getJabberId(), title, jsonArray.getJSONObject(i).getString("url_tembak").toString(), username, jsonArray.getJSONObject(i).getString("id_rooms_tab").toString(), color, false, ByonChatMainRoomActivity.this);
//                    } else if (category.equalsIgnoreCase("3")) {
//                        //video
//                        map.put(i, null);
//                        show = true;
//                        aa = FragmentMyVideo.newInstance(messengerHelper.getMyContact().getJabberId(), title, jsonArray.getJSONObject(i).getString("url_tembak").toString(), username, jsonArray.getJSONObject(i).getString("id_rooms_tab").toString(), "", false, ByonChatMainRoomActivity.this);
//                    } else if (category.equalsIgnoreCase("4")) {
//                        Log.w("kabadu", jsonArray.getJSONObject(i).getString("url_tembak"));
//                        if (include_pull.equalsIgnoreCase("1") || include_pull.equalsIgnoreCase("3")) {
//                            List<String> valSetOne = new ArrayList<String>();
//                            valSetOne.add(title);
//                            valSetOne.add(username);
//                            valSetOne.add(jsonArray.getJSONObject(i).getString("id_rooms_tab").toString());
//                            valSetOne.add(color);
//                            valSetOne.add(include_latlong);
//                            show = true;
//                            aa = FragmentRoomTaskWater.newInstance(title, jsonArray.getJSONObject(i).getString("url_tembak").toString(), username, jsonArray.getJSONObject(i).getString("id_rooms_tab").toString(), color, include_latlong, ByonChatMainRoomActivity.this, "hide");
//                            valSetOne.add("hide");
//                            map.put(i, valSetOne);
//                        } else if (include_pull.equalsIgnoreCase("0")) {
//                            List<String> valSetOne = new ArrayList<String>();
//                            valSetOne.add(title);
//                            valSetOne.add(username);
//                            valSetOne.add(jsonArray.getJSONObject(i).getString("id_rooms_tab").toString());
//                            valSetOne.add(color);
//                            valSetOne.add(include_latlong);
//                            show = true;
//                            valSetOne.add("show");
//                            aa = FragmentRoomTask.newInstance(title, jsonArray.getJSONObject(i).getString("url_tembak").toString(), username, jsonArray.getJSONObject(i).getString("id_rooms_tab").toString(), color, include_latlong, ByonChatMainRoomActivity.this);
//                            map.put(i, valSetOne);
//                        } else if (include_pull.equalsIgnoreCase("2")) {
//                            show = true;
//                            map.put(i, null);
//                            aa = FragmentRoomAPI.newInstance(title, jsonArray.getJSONObject(i).getString("url_tembak").toString(), username, jsonArray.getJSONObject(i).getString("id_rooms_tab").toString(), include_latlong, ByonChatMainRoomActivity.this);
//                        } else if (include_pull.equalsIgnoreCase("4") || include_pull.equalsIgnoreCase("5")) {
//                            List<String> valSetOne = new ArrayList<String>();
//                            valSetOne.add(title);
//                            valSetOne.add(username);
//                            valSetOne.add(jsonArray.getJSONObject(i).getString("id_rooms_tab").toString());
//                            valSetOne.add(color);
//                            valSetOne.add(include_latlong);
//                            show = true;
//                            aa = FragmentRoomMultipleTask.newInstance(title, jsonArray.getJSONObject(i).getString("url_tembak").toString(), username, jsonArray.getJSONObject(i).getString("id_rooms_tab").toString(), color, include_latlong, ByonChatMainRoomActivity.this, "hideMultiple");
//                            valSetOne.add("hideMultiple");
//                            map.put(i, valSetOne);
//                        } else if (include_pull.equalsIgnoreCase("6")) {
//                            JSONObject jsonRootObject = new JSONObject(jsonArray.getJSONObject(i).getString("url_tembak").toString());
//                            List<String> valSetOne = new ArrayList<String>();
//                            valSetOne.add(title);
//                            valSetOne.add(username);
//                            valSetOne.add(jsonArray.getJSONObject(i).getString("id_rooms_tab").toString());
//                            valSetOne.add(color);
//                            valSetOne.add(include_latlong);
//                            show = true;
//                            aa = FragmentRoomMultipleTask.newInstance(title, jsonRootObject.getString("pull"), username, jsonArray.getJSONObject(i).getString("id_rooms_tab").toString(), color, include_latlong, ByonChatMainRoomActivity.this, "showMultiple");
//                            valSetOne.add("showMultiple");
//                            map.put(i, valSetOne);
//                        } else if (include_pull.equalsIgnoreCase("7")) {
//                            JSONObject jsonRootObject = new JSONObject(jsonArray.getJSONObject(i).getString("url_tembak").toString());
//                            List<String> valSetOne = new ArrayList<String>();
//                            valSetOne.add(title);
//                            valSetOne.add(username);
//                            valSetOne.add(jsonArray.getJSONObject(i).getString("id_rooms_tab").toString());
//                            valSetOne.add(color);
//                            valSetOne.add(include_latlong);
//                            show = true;
//                            aa = TempScheduleRoom.newInstance(title, jsonRootObject.getString("pull"), username, jsonArray.getJSONObject(i).getString("id_rooms_tab").toString(), color, include_latlong, ByonChatMainRoomActivity.this, "showMultiple");
//                            valSetOne.add("hideMultiple");
//                            map.put(i, valSetOne);
//
//                        }
//                    } else if (category.equalsIgnoreCase("5")) {
//                        map.put(i, null);
//                        show = true;
//                        aa = FragmentMyNote.newInstance(messengerHelper.getMyContact().getJabberId(), title, jsonArray.getJSONObject(i).getString("url_tembak").toString(), username, jsonArray.getJSONObject(i).getString("id_rooms_tab").toString(), color, false, ByonChatMainRoomActivity.this);
//                    } else if (category.equalsIgnoreCase("14")) {
//                        show = true;
//                        map.put(i, null);
//                        aa = FragmentMyNewsNew.newInstance(messengerHelper.getMyContact().getJabberId(), title, jsonArray.getJSONObject(i).getString("url_tembak").toString(), username, jsonArray.getJSONObject(i).getString("id_rooms_tab").toString(), color, ByonChatMainRoomActivity.this);
//                    } else if (category.equalsIgnoreCase("6")) {
//                        //news
//                        show = true;
//                        map.put(i, null);
//                        aa = FragmentMyNews.newInstance(messengerHelper.getMyContact().getJabberId(), title, jsonArray.getJSONObject(i).getString("url_tembak").toString(), username, jsonArray.getJSONObject(i).getString("id_rooms_tab").toString(), color, ByonChatMainRoomActivity.this);
//                        //   aa = FragmentReadManual.newInstance(messengerHelper.getMyContact().getJabberId(), title, jsonArray.getJSONObject(i).getString("url_tembak").toString(), username, jsonArray.getJSONObject(i).getString("id_rooms_tab").toString(),color);
//                    } else if (category.equalsIgnoreCase("7")) {
//                        show = true;
//                        map.put(i, null);
//                        aa = FragmentDirectory.newInstance(messengerHelper.getMyContact().getJabberId(), title, jsonArray.getJSONObject(i).getString("url_tembak").toString(), username, jsonArray.getJSONObject(i).getString("id_rooms_tab").toString(), color, ByonChatMainRoomActivity.this);
//                    } else if (category.equalsIgnoreCase("8")) {
//                        show = true;
//                        map.put(i, null);
//                        aa = FragmentStreamingVideo.newInstance(messengerHelper.getMyContact().getJabberId(), title, jsonArray.getJSONObject(i).getString("url_tembak").toString(), username, jsonArray.getJSONObject(i).getString("id_rooms_tab").toString(), ByonChatMainRoomActivity.this);
//                    } else if (category.equalsIgnoreCase("9")) {
//                        show = true;
//                        map.put(i, null);
//                        aa = new FragmentStreamingRadio();
//                    } else if (category.equalsIgnoreCase("10")) {
//                        show = true;
//                        map.put(i, null);
//                        aa = FragmentRoomAPI.newInstance(title, jsonArray.getJSONObject(i).getString("url_tembak").toString(), username, jsonArray.getJSONObject(i).getString("id_rooms_tab").toString(), "0", ByonChatMainRoomActivity.this);
//                    } else if (category.equalsIgnoreCase("11")) {
//                        List<String> valSetOne = new ArrayList<String>();
//                        valSetOne.add("pos");
//                        valSetOne.add(username);
//                        valSetOne.add(jsonArray.getJSONObject(i).getString("id_rooms_tab").toString());
//                        valSetOne.add(color);
//                        valSetOne.add(include_latlong);
//                        show = true;
//                        valSetOne.add("show");
//                        valSetOne.add(jsonArray.getJSONObject(i).getString("url_tembak").toString());
//                        map.put(i, valSetOne);
//                        aa = FragmentRoomPOS.newInstance(messengerHelper.getMyContact().getJabberId(), title, jsonArray.getJSONObject(i).getString("url_tembak").toString(), username, jsonArray.getJSONObject(i).getString("id_rooms_tab").toString(), color, ByonChatMainRoomActivity.this);
//                    } else if (category.equalsIgnoreCase("15")) {
//                        List<String> valSetOne = new ArrayList<String>();
//                        valSetOne.add("btube");
//                        valSetOne.add(username);
//                        valSetOne.add(jsonArray.getJSONObject(i).getString("id_rooms_tab").toString());
//                        valSetOne.add(color);
//                        valSetOne.add(include_latlong);
//                        show = true;
//                        valSetOne.add("showvideo");
//                        valSetOne.add(jsonArray.getJSONObject(i).getString("url_tembak").toString());
//                        map.put(i, valSetOne);
////                        aa = ByonchatVideoFragment.newInstance(messengerHelper.getMyContact().getJabberId(), title, jsonArray.getJSONObject(i).getString("url_tembak").toString(), username, jsonArray.getJSONObject(i).getString("id_rooms_tab").toString(), color, ByonChatMainRoomActivity.this);
//                    } else if (category.equalsIgnoreCase("16")) {
//                        //TIME=WATCH
//                        map.put(i, null);
//                        show = true;
//                        aa = FragmentProductCatalog.newInstance(messengerHelper.getMyContact().getJabberId(), title, jsonArray.getJSONObject(i).getString("url_tembak").toString(), username, jsonArray.getJSONObject(i).getString("id_rooms_tab").toString(), color, false, ByonChatMainRoomActivity.this);
//                    } else if (category.equalsIgnoreCase("17")) {
//                        //TIME=WATCH
//                        map.put(i, null);
//                        show = true;
//                        aa = FragmentMyCardID.newInstance(messengerHelper.getMyContact().getJabberId(), title, jsonArray.getJSONObject(i).getString("url_tembak").toString(), username, jsonArray.getJSONObject(i).getString("id_rooms_tab").toString(), color, false, ByonChatMainRoomActivity.this);
//                    }
//
//
//                    String status = jsonArray.getJSONObject(i).getString("status").toString();
//                    if (status.equalsIgnoreCase("1") && show) {
//                        adapter.addFragment(aa, title);
//                    }
//
//                }
//              /*  map.put(jsonArray.length(),null);
//                Fragment aa = FragmentRoomAPIAPura.newInstance(messengerHelper.getMyContact().getJabberId(), "Flights", "http", username, "");
//                adapter.addFragment(aa, "Flights");*/
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            backButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onBackPressed();
//                }
//            });
//
//           /* if (messengerHelper.getMyContact().getJabberId().matches(username)) {
//                btChat.setVisibility(View.INVISIBLE);
//              //  btAddRoom.setVisibility(View.INVISIBLE);
//            } else {
//                btChat.setVisibility(View.VISIBLE);
//              //  btAddRoom.setVisibility(View.VISIBLE);
//            }*/
//
//         /*   btChat.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });*/
//
//            roomsDB.open();
//            catArray = roomsDB.retrieveRoomsByName(username, "2");
//            roomsDB.close();
//
//            /*if (catArray.size() > 0) {
//                btAddRoom.setVisibility(View.INVISIBLE);
//            } else {
//                btAddRoom.setVisibility(View.VISIBLE);
//            }
//
//            btAddRoom.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });*/
//
//            title.setTextColor(Color.parseColor("#" + colorText));
//
//            tabLayout.setBackgroundColor(Color.parseColor("#" + color));
//            tabLayout.setTabTextColors(FilteringImage.adjustAlpha(Color.parseColor("#" + colorText), 0.5f), Color.parseColor("#" + colorText));
//            tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#" + colorText));
//            fab.setColorFilter(Color.parseColor("#" + colorText));
//            fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#" + color)));
//            fab.setRippleColor(Color.GRAY);
//
//        } else {
//            next = false;
//            if (NetworkInternetConnectionStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
//                finish();
//                Intent ii = new Intent(ByonChatMainRoomActivity.this, LoadingGetTabRoomActivity.class);
//                ii.putExtra(ConversationActivity.KEY_JABBER_ID, username);
//                if (targetURL != null) {
//                    ii.putExtra(ConversationActivity.KEY_TITLE, targetURL);
//                }
//                ii.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(ii);
//            } else {
//                Toast.makeText(ByonChatMainRoomActivity.this, "No Internet Akses", Toast.LENGTH_SHORT).show();
//                finish();
//            }
//        }
//
//
//        if (next) {
//            // fab.hide();
//            setupViewPager(viewPager, adapter);
//            viewPager.setOffscreenPageLimit(1);
//            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//                @Override
//                public void onPageScrolled(int pos, float positionOffset, int positionOffsetPixels) {
//                    List value = (List) map.get(pos);
//                    position = pos;
//                    if (value != null) {
//                        if (value.get(5).toString().equalsIgnoreCase("show") || value.get(5).toString().equalsIgnoreCase("showMultiple")) {
//                            fab.show();
//                            Intent intent = new Intent(getApplicationContext(), DinamicRoomTaskActivity.class);
//                            String action = value.get(0).toString();
//                            if (action.equalsIgnoreCase("pos")) {
//                                intent = new Intent(getApplicationContext(), RoomPOSdetail.class);
//                                intent.putExtra("urlTembak", value.get(6).toString());
//                            }
//                            intent.putExtra("tt", value.get(0).toString());
//                            if (value.size() > 1) {
//                                intent.putExtra("uu", value.get(1).toString());
//                                intent.putExtra("ii", value.get(2).toString());
//                                intent.putExtra("col", value.get(3).toString());
//                                intent.putExtra("ll", value.get(4).toString());
//                                intent.putExtra("from", value.get(5).toString());
//                                intent.putExtra("idTask", "");
//                            }
//                            showFabIntent(intent);
//                        } else if (value.get(5).toString().equalsIgnoreCase("showvideo")) {
//                            Intent intent = ByonchatVideoBeforeDownloadActivity.generateIntent(getApplicationContext(),
//                                    username,
//                                    value.get(2).toString(),
//                                    value.get(6).toString(),
//                                    color,
//                                    colorText);
//                            intent.putExtra("tt", value.get(0).toString());
//                            showFabIntent(intent);
//                        } else {
//                            fab.hide();
//                        }
//                    } else {
//                        fab.hide();
//                    }
//                }
//
//                @Override
//                public void onPageSelected(int pos) {
//                    List value = (List) map.get(pos);
//                    if (value != null) {
//                        if (value.get(5).toString().equalsIgnoreCase("show") || value.get(5).toString().equalsIgnoreCase("showMultiple")) {
//                            fab.show();
//                            Intent intent = new Intent(getApplicationContext(), DinamicRoomTaskActivity.class);
//                            String action = value.get(0).toString();
//                            if (action.equalsIgnoreCase("pos")) {
//                                intent = new Intent(getApplicationContext(), RoomPOSdetail.class);
//                                intent.putExtra("urlTembak", value.get(6).toString());
//                            }
//                            intent.putExtra("tt", value.get(0).toString());
//                            if (value.size() > 1) {
//                                intent.putExtra("uu", value.get(1).toString());
//                                intent.putExtra("ii", value.get(2).toString());
//                                intent.putExtra("col", value.get(3).toString());
//                                intent.putExtra("ll", value.get(4).toString());
//                                intent.putExtra("from", value.get(5).toString());
//                                intent.putExtra("idTask", "");
//                            }
//                            showFabIntent(intent);
//                        } else if (value.get(5).toString().equalsIgnoreCase("showvideo")) {
//                            Intent intent = ByonchatVideoBeforeDownloadActivity.generateIntent(getApplicationContext(),
//                                    username,
//                                    value.get(2).toString(),
//                                    value.get(6).toString(),
//                                    color,
//                                    colorText);
//                            intent.putExtra("tt", value.get(0).toString());
//                            showFabIntent(intent);
//                        } else {
//                            fab.hide();
//                        }
//                    } else {
//                        fab.hide();
//                    }
//                }
//
//                @Override
//                public void onPageScrollStateChanged(int state) {
//                }
//            });
//
//
//            tabLayout.setupWithViewPager(viewPager);
//
//            if (adapter.getCount() < 4) {
//                tabLayout.setTabMode(TabLayout.MODE_FIXED);
//                tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
//            } else {
//                tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
//            }
//
//            viewPager.postDelayed(new Runnable() {
//
//                @Override
//                public void run() {
//                    viewPager.setCurrentItem(intent.getIntExtra(KEY_POSITION, firstTab));
//                }
//            }, 100);
//            //  viewPager.setOffscreenPageLimit(adapter.getCount());
//        }
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_room_dinamic, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_add:
//                realname = name;
//                insertToDB(username, description, name, icon, "2");
//                return true;
//            case R.id.action_short:
//                createShortcut();
//                return true;
//            case R.id.action_refresh:
//                RefreshRoom();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
//
//    private void RefreshRoom() {
//        AlertDialog.Builder alertbox = new AlertDialog.Builder(ByonChatMainRoomActivity.this);
//        alertbox.setTitle("Refresh Room " + title.getText());
//        alertbox.setMessage("Are you sure you want to Refresh?");
//        alertbox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface arg0, int arg1) {
//                if (NetworkInternetConnectionStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
//                    finish();
//                    Intent ii = new Intent(getApplicationContext(), LoadingGetTabRoomActivity.class);
//                    ii.putExtra(ConversationActivity.KEY_JABBER_ID, username);
//                    if (targetURL != null) {
//                        ii.putExtra(ConversationActivity.KEY_TITLE, targetURL);
//                        Log.w("lahan", "1");
//
//                    } else {
//                        Log.w("lahan", "2");
//
//                    }
//
//                    ii.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(ii);
//                } else {
//                    Toast.makeText(getApplicationContext(), "No Internet Akses", Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//
//            }
//        });
//        alertbox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface arg0, int arg1) {
//
//            }
//        });
//        alertbox.show();
//    }
//
//    private void createShortcut() {
//        final Dialog dialogConfirmation;
//        dialogConfirmation = DialogUtil.customDialogConversationConfirmation(this);
//        dialogConfirmation.show();
//
//        TextView txtConfirmation = (TextView) dialogConfirmation.findViewById(R.id.confirmationTxt);
//        TextView descConfirmation = (TextView) dialogConfirmation.findViewById(R.id.confirmationDesc);
//        txtConfirmation.setText("Create Shortcut " + title.getText());
//        descConfirmation.setVisibility(View.VISIBLE);
//        descConfirmation.setText("Are you sure you want to Create Shortcut?");
//
//        Button btnNo = (Button) dialogConfirmation.findViewById(R.id.btnNo);
//        Button btnYes = (Button) dialogConfirmation.findViewById(R.id.btnYes);
//        btnNo.setText("Cancel");
//        btnNo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialogConfirmation.dismiss();
//            }
//        });
//
//        btnYes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialogConfirmation.dismiss();
//                Picasso.with(ByonChatMainRoomActivity.this)
//                        .load(icon)
//                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
//                        .transform(new RoundedCornersTransformation(30, 0, RoundedCornersTransformation.CornerType.ALL))
//                        .into(new Target() {
//                            @Override
//                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                                if (bitmap != null) {
//                                   /* Toast.makeText(ByonChatMainRoomActivity.this, "Create Shortcut Success", Toast.LENGTH_SHORT).show();
//                                    Intent aa = new Intent(getApplicationContext(), ByonChatMainRoomActivity.class);
//                                    aa.putExtra(ConversationActivity.KEY_JABBER_ID, username);
//                                    Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
//                                    shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
//                                    shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);
//                                    shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, aa);
//                                    sendBroadcast(shortcutintent);
//                                    finish();*/
//                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//                                        ShortcutInfo.Builder mShortcutInfoBuilder = new ShortcutInfo.Builder(ByonChatMainRoomActivity.this, name);
//                                        mShortcutInfoBuilder.setShortLabel(name);
//                                        mShortcutInfoBuilder.setLongLabel(name);
//                                        mShortcutInfoBuilder.setIcon(Icon.createWithBitmap(bitmap));
//
//                                        Intent shortcutIntent = new Intent(getApplicationContext(), ByonChatMainRoomActivity.class);
//                                        shortcutIntent.putExtra(ConversationActivity.KEY_JABBER_ID, username);
//                                        shortcutIntent.setAction(Intent.ACTION_CREATE_SHORTCUT);
//                                        mShortcutInfoBuilder.setIntent(shortcutIntent);
//
//                                        ShortcutInfo mShortcutInfo = mShortcutInfoBuilder.build();
//                                        ShortcutManager mShortcutManager = getSystemService(ShortcutManager.class);
//                                        mShortcutManager.requestPinShortcut(mShortcutInfo, null);
//
//                                    } else {
//                                        Intent shortcutIntent = new Intent(getApplicationContext(), ByonChatMainRoomActivity.class);
//                                        shortcutIntent.putExtra(ConversationActivity.KEY_JABBER_ID, username);
//
//                                        shortcutIntent.setAction(Intent.ACTION_MAIN);
//
//                                        Intent addIntent = new Intent();
//                                        addIntent
//                                                .putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
//                                        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
//                                        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);
//
//                                        addIntent
//                                                .setAction("com.android.launcher.action.INSTALL_SHORTCUT");
//                                        addIntent.putExtra("duplicate", false);  //may it's already there so   don't duplicate
//                                        getApplicationContext().sendBroadcast(addIntent);
//
//                                    }
//                                    finish();
//                                    Toast.makeText(ByonChatMainRoomActivity.this, "Shortcut Created", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//
//                            @Override
//                            public void onBitmapFailed(Drawable errorDrawable) {
//                                Toast.makeText(ByonChatMainRoomActivity.this, "Create Shortcut failed", Toast.LENGTH_SHORT).show();
//                            }
//
//                            @Override
//                            public void onPrepareLoad(Drawable placeHolderDrawable) {
//                                Toast.makeText(ByonChatMainRoomActivity.this, "Please Wait", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//            }
//        });
//    }
//
//    public void insertToDB(String name, String desc, String realname, String link, String type) {
//        roomsDB.open();
//        catArray = roomsDB.retrieveRoomsByName(name, "2");
//        roomsDB.close();
//        if (catArray.size() > 0) {
//            Toast.makeText(ByonChatMainRoomActivity.this.getApplicationContext(), realname + " is already added to selected rooms", Toast.LENGTH_SHORT).show();
//        } else {
//            requestKey();
//        }
//    }
//
//    public class LoadImageFromURL extends AsyncTask<String, Void, Bitmap> {
//        private ImageView imageView;
//
//        public LoadImageFromURL(ImageView imageView) {
//            this.imageView = imageView;
//        }
//
//        @Override
//        protected Bitmap doInBackground(String... params) {
//            // TODO Auto-generated method stub
//            Bitmap bitmap = null;
//            Bitmap blurredBitmap = null;
//            try {
//                URL url = new URL(params[0]);
//                bitmap = BitmapFactory.decodeStream((InputStream) url.getContent());
//                if (bitmap != null) {
//                    blurredBitmap = BlurBuilder.blur(getApplicationContext(), bitmap);
//                }
//
//                return blurredBitmap;
//
//            } catch (MalformedURLException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            return null;
//
//        }
//
//        @Override
//        protected void onPostExecute(Bitmap result) {
//            // TODO Auto-generated method stub
//            super.onPostExecute(result);
//            if (result != null) {
//                backdropBlur.setBackgroundDrawable(new BitmapDrawable(getResources(), result));
//            }
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
//                .cancel(NotificationReceiver.NOTIFY_TASK);
//        assistant.start();
//
//        // TODO: 23/11/18 cek setiap 15 menit
//        Log.w("kepanggil", "wow");
//
//        if (new Validations().getInstance(getApplicationContext()).getValidationLoginById(25) == 1) {
//            if (!protect.equalsIgnoreCase("error") && protect.equalsIgnoreCase("1")) {
//                if (success == null) {
//                    finish();
//                    Intent a = new Intent(getApplicationContext(), LoginDinamicRoomActivity.class);
//                    a.putExtra(ConversationActivity.KEY_JABBER_ID, username);
//                    a.putExtra(ConversationActivity.KEY_TITLE, messengerHelper.getMyContact().getJabberId());
//                    startActivity(a);
//                }
//            } else if (!protect.equalsIgnoreCase("error") && protect.equalsIgnoreCase("2")) {
//                if (success == null) {
//                    finish();
//                    Intent a = new Intent(getApplicationContext(), LoginDinamicFingerPrint.class);
//                    a.putExtra(ConversationActivity.KEY_JABBER_ID, username);
//                    a.putExtra(ConversationActivity.KEY_TITLE, messengerHelper.getMyContact().getJabberId());
//                    startActivity(a);
//                }
//            } else if (!protect.equalsIgnoreCase("error") && protect.equalsIgnoreCase("5")) {
//                if (success == null) {
//                    finish();
//                    Intent a = new Intent(getApplicationContext(), RequestPasscodeRoomActivity.class);
//                    a.putExtra(ConversationActivity.KEY_JABBER_ID, username);
//                    a.putExtra(ConversationActivity.KEY_TITLE, "request");
//                    startActivity(a);
//                }
//            } else if (!protect.equalsIgnoreCase("error") && protect.equalsIgnoreCase("6")) {
//                if (success == null) {
//                    finish();
//                    Intent a = new Intent(getApplicationContext(), RequestPasscodeRoomActivity.class);
//                    a.putExtra(ConversationActivity.KEY_JABBER_ID, username);
//                    a.putExtra(ConversationActivity.KEY_TITLE, "waiting");
//                    startActivity(a);
//                }
//            }
//        }
//    }
//
//    @Override
//    public void onPause() {
//        assistant.stop();
//        super.onPause();
//    }
//
//
//    public String idLoof(Integer posss) {
//        List value = (List) map.get(position);
//        if (value != null) {
//            String statusBaru = "";
//            ArrayList<ContentRoom> listItem = new ArrayList<>();
//            ArrayList<RoomsDetail> listItem2;
//            listItem2 = botListDB.allRoomDetailFormWithFlag("", value.get(1).toString(), value.get(2).toString(), "parent");
//            for (RoomsDetail aa : listItem2) {
//
//                ArrayList<RoomsDetail> listItem3 = botListDB.allRoomDetailFormWithFlag(aa.getId(), value.get(1).toString(), value.get(2).toString(), "list");
//                for (RoomsDetail ii : listItem3) {
//                    if (ii.getFlag_content().equalsIgnoreCase("1")) {
//                        Log.w("2abub", ii.getContent());
//                        JSONObject jO = null;
//                        try {
//                            jO = new JSONObject(ii.getContent());
//                            statusBaru = jO.getString("bb");
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//
//                String date = "";
//                date = aa.getContent();
//                ContentRoom contentRoom = new ContentRoom(aa.getId(), "", date, desc, "", "", "");
//                listItem.add(contentRoom);
//            }
//
//            Collections.sort(listItem, new Comparator<ContentRoom>() {
//                @Override
//                public int compare(ContentRoom e1, ContentRoom e2) {
//                    Date satu = Utility.convertStringToDate(Utility.parseDateToddMMyyyy(e1.getTime()));
//                    Date dua = Utility.convertStringToDate(Utility.parseDateToddMMyyyy(e2.getTime()));
//                    if (satu.compareTo(dua) > 0) {
//                        return -1;
//                    } else {
//                        return 1;
//                    }
//                }
//            });
//
//            if (value.size() > 1) {
//                try {
//                    Intent intent = new Intent(getApplicationContext(), DinamicRoomTaskActivity.class);
//                    intent.putExtra("tt", value.get(0).toString());
//                    intent.putExtra("uu", value.get(1).toString());
//                    intent.putExtra("ii", value.get(2).toString());
//                    intent.putExtra("idTask", listItem.get(posss).getIdHex());
//                    intent.putExtra("col", value.get(3).toString());
//                    intent.putExtra("ll", value.get(4).toString());
//                    intent.putExtra("from", value.get(5).toString());
//                    if (!statusBaru.equalsIgnoreCase("")) {
//                        intent.putExtra("isReject", statusBaru);
//                    }
//
//
//                    startActivity(intent);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        return null;
//    }
//
//    private String JsonToStringKey(String title) {
//        if (Message.isJSONValid(title)) {
//            JSONObject jsonObject = null;
//            try {
//                jsonObject = new JSONObject(title);
//                Iterator<String> keys = jsonObject.keys();
//                title = jsonObject.get(keys.next()).toString();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        return title;
//    }
//
//
//    public String deleteById(final Integer posss) {
//        final List value = (List) map.get(position);
//        if (value != null) {
//            final ArrayList<RoomsDetail> listItem2;
//            listItem2 = botListDB.allRoomDetailFormWithFlag("", value.get(1).toString(), value.get(2).toString(), "parent");
//
//            String title = "";
//            final AlertDialog.Builder alertbox = new AlertDialog.Builder(ByonChatMainRoomActivity.this);
//            alertbox.setTitle("Delete");
//            alertbox.setMessage(title);
//            alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface arg0, int arg1) {
//
//                    if (value.size() > 1) {
//                        ArrayList<ContentRoom> listItem = new ArrayList<>();
//                        for (RoomsDetail aa : listItem2) {
//                            String date = "";
//                            date = aa.getContent();
//                            ContentRoom contentRoom = new ContentRoom(aa.getId(), "", date, desc, "", "", "");
//                            listItem.add(contentRoom);
//                        }
//
//                        Collections.sort(listItem, new Comparator<ContentRoom>() {
//                            @Override
//                            public int compare(ContentRoom e1, ContentRoom e2) {
//                                Date satu = Utility.convertStringToDate(Utility.parseDateToddMMyyyy(e1.getTime()));
//                                Date dua = Utility.convertStringToDate(Utility.parseDateToddMMyyyy(e2.getTime()));
//                                if (satu.compareTo(dua) > 0) {
//                                    return -1;
//                                } else {
//                                    return 1;
//                                }
//                            }
//                        });
//
//                        botListDB.deleteRoomsDetailbyId(listItem.get(posss).getIdHex(), value.get(2).toString(), value.get(1).toString());
//                        finish();
//                        getIntent().putExtra(KEY_POSITION, position);
//                        startActivity(getIntent());
//                    }
//
//                }
//            });
//            alertbox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface arg0, int arg1) {
//                }
//            });
//            alertbox.show();
//        }
//
//        return null;
//    }
//
//    public void showFabIntent(final Intent intent) {
//        String action = intent.getStringExtra("tt").toString();
//        if (action.equalsIgnoreCase("pos")) {
//            fab.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //Open popup window
//                    if (p != null)
//                        showPopup(ByonChatMainRoomActivity.this, p, intent);
//                }
//            });
//        } else {
//            fab.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    startActivity(intent);
//                }
//            });
//        }
//    }
//
//    public Bitmap viewToBitmap(View view) {
//        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        Path clipPath = new Path();
//        RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
//        clipPath.addRoundRect(rect, 30.0f, 30.0f, Path.Direction.CW);
//        canvas.clipPath(clipPath);
//        view.draw(canvas);
//        return bitmap;
//    }
//
//
//    private void loadBackdrop(int t) {
//        Picasso.with(ByonChatMainRoomActivity.this.getApplicationContext()).load(t).networkPolicy(NetworkPolicy.NO_STORE, NetworkPolicy.NO_CACHE).into(imageView);
//        return;
//    }
//
//    private void setupViewPager(ViewPager viewPager, ViewPagerAdapter adapter) {
//        viewPager.setAdapter(adapter);
//    }
//
//
//    class ViewPagerAdapter extends FragmentPagerAdapter {
//        private final List<Fragment> mFragmentList = new ArrayList<>();
//        private final List<String> mFragmentTitleList = new ArrayList<>();
//
//        public ViewPagerAdapter(FragmentManager manager) {
//            super(manager);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return mFragmentList.get(position);
//        }
//
//        @Override
//        public int getCount() {
//            return mFragmentList.size();
//        }
//
//        public void addFragment(Fragment fragment, String title) {
//            mFragmentList.add(fragment);
//            mFragmentTitleList.add(title);
//        }
//
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return mFragmentTitleList.get(position);
//        }
//    }
//
//    private String capitalize(String s) {
//        if (s == null || s.length() == 0) {
//            return "";
//        }
//        char first = s.charAt(0);
//        if (Character.isUpperCase(first)) {
//            return s;
//        } else {
//            return Character.toUpperCase(first) + s.substring(1);
//        }
//    }
//
//    public static Bitmap getRoundedCornerBitmapBorder(Bitmap src, float round) {
//        Bitmap result = null;
//        if (src != null) {
//            // Source image size
//            int width = src.getWidth();
//            int height = src.getHeight();
//            // create result bitmap output
//            result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//            // set canvas for painting
//            Canvas canvas = new Canvas(result);
//            canvas.drawARGB(0, 0, 0, 0);
//
//
//            // configure paint
//            final Paint paint = new Paint();
//            paint.setAntiAlias(true);
//            paint.setColor(Color.BLACK);
//
//            // configure rectangle for embedding
//            final Rect rect = new Rect(0, 0, width, height);
//            final RectF rectF = new RectF(rect);
//
//            // draw Round rectangle to canvas
//            canvas.drawRoundRect(rectF, round, round, paint);
//
//            // create Xfer mode
//            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//            // draw source image to canvas
//            canvas.drawBitmap(src, rect, rect, paint);
//        }
//        // return final image
//        return result;
//    }
//
//    public static String jsonResultType(String json, String type) {
//        String hasil = "";
//        JSONObject jObject = null;
//        try {
//            jObject = new JSONObject(json);
//        } catch (JSONException e) {
//            hasil = "error";
//            e.printStackTrace();
//        }
//        if (jObject != null) {
//            try {
//                hasil = jObject.getString(type);
//            } catch (JSONException e) {
//                hasil = "error";
//                e.printStackTrace();
//            }
//        }
//
//        if (type.equalsIgnoreCase("e") && hasil.equalsIgnoreCase("error")) {
//            hasil = "https://" + MessengerConnectionService.HTTP_SERVER;
//        }
//
//        return hasil;
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
//            alertbox.setMessage("Are you sure you want to Exit");
//
//            // Set a positive/yes button and create a listener
//            alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface arg0, int arg1) {
//                    finish();
//                }
//            });
//
//            // Set a negative/no button and create a listener
//            alertbox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface arg0, int arg1) {
//
//
//                }
//            });
//            alertDialogExit = alertbox.create();
//            alertDialogExit.show();
//            return true;
//        }
//
//        return super.onKeyDown(keyCode, event);
//    }
//
//    // Get the x and y position after the button is draw on screen
//// (It's important to note that we can't get the position in the onCreate(),
//// because at that stage most probably the view isn't drawn yet, so it will return (0, 0))
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//
//        int[] location = new int[2];
//        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.fabAction);
//
//        // Get the x, y location and store it in the location[] array
//        // location[0] = x, location[1] = y.
//        button.getLocationOnScreen(location);
//
//        //Initialize the Point with x, and y positions
//        p = new Point();
//        p.x = location[0];
//        p.y = location[1];
//    }
//
//    // The method that displays the popup.
//    private void showPopup(final Activity context, Point p, final Intent intent) {
//
//        final PopupWindow popup = new PopupWindow(context);
//
//        ArrayList<String> sortList = new ArrayList<String>();
//        sortList.add("Order");
//        sortList.add("Cancel Order");
//        sortList.add("Add Item");
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_textview_simple_center,
//                sortList);
//        // the drop down list is a list view
//        ListView listViewSort = new ListView(this);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            listViewSort.setBackground(getResources().getDrawable(R.drawable.backgroud_white_no_round));
//        }
//        // set our adapter and pass our pop up window contents
//        listViewSort.setAdapter(adapter);
//
//        // set on item selected
//        listViewSort.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                popup.dismiss();
//                if (position == 0) {
//                    Intent i = new Intent("android.intent.action.MAIN.BynChatPOS").putExtra("some_msg", "order");
//                    sendBroadcast(i);
//                } else if (position == 1) {
//                    Intent i = new Intent("android.intent.action.MAIN.BynChatPOS").putExtra("some_msg", "cancel");
//                    sendBroadcast(i);
//                } else if (position == 2) {
//                    startActivity(intent);
//
//
//                    /*
//                    Intent i = new Intent("android.intent.action.MAIN.BynChatPOS").putExtra("some_msg", "add");
//                    sendBroadcast(i);*/
//                }
//            }
//        });
//
//        // some other visual settings for popup window
//        popup.setFocusable(true);
//        popup.setWidth(250);
//        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
//        popup.setContentView(listViewSort);
//
//        int OFFSET_X = -70;
//        int OFFSET_Y = 0;
//        popup.showAtLocation(listViewSort, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);
//    }
//
//    private void requestKey() {
//        RequestKeyTask testAsyncTask = new RequestKeyTask(new TaskCompleted() {
//            @Override
//            public void onTaskDone(String key) {
//                if (key.equalsIgnoreCase("null")) {
//                    Toast.makeText(ByonChatMainRoomActivity.this.getApplicationContext(), R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
//                } else {
//                    laporSelectedRoom = new LaporSelectedRoom(ByonChatMainRoomActivity.this.getApplicationContext());
//                    laporSelectedRoom.execute(key);
//                }
//            }
//        }, ByonChatMainRoomActivity.this.getApplicationContext());
//
//        testAsyncTask.execute();
//    }
//
//    class LaporSelectedRoom extends AsyncTask<String, Void, String> {
//
//        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
//        private static final int WAIT_TIMEOUT = 30 * 1000;
//        private final HttpClient httpclient = new DefaultHttpClient();
//
//        final HttpParams params = httpclient.getParams();
//        HttpResponse response;
//        private String content = null;
//        private boolean error = false;
//        private Context mContext;
//
//        public LaporSelectedRoom(Context context) {
//            this.mContext = context;
//
//        }
//
//        @Override
//        protected void onPreExecute() {
//            showProgressDialog();
//        }
//
//        protected String doInBackground(String... key) {
//            try {
//                HttpClient httpClient = HttpHelper
//                        .createHttpClient(mContext);
//                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
//                        1);
//
//                nameValuePairs.add(new BasicNameValuePair("username", messengerHelper.getMyContact().getJabberId()));
//                nameValuePairs.add(new BasicNameValuePair("key", key[0]));
//                nameValuePairs.add(new BasicNameValuePair("room_id", username));
//                nameValuePairs.add(new BasicNameValuePair("aksi", "1"));
//
//                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
//                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
//                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);
//
//                HttpPost post = new HttpPost(URLLAPORSELECTED);
//                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//
//                //Response from the Http Request
//                response = httpclient.execute(post);
//                StatusLine statusLine = response.getStatusLine();
//                //Check the Http Request for success
//                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
//                    ByteArrayOutputStream out = new ByteArrayOutputStream();
//                    response.getEntity().writeTo(out);
//                    out.close();
//                    content = out.toString();
//                } else {
//                    error = true;
//                    content = statusLine.getReasonPhrase();
//                    response.getEntity().getContent().close();
//                    throw new IOException(content);
//                }
//
//            } catch (ClientProtocolException e) {
//                content = e.getMessage();
//                error = true;
//            } catch (IOException e) {
//                content = e.getMessage();
//                error = true;
//            } catch (Exception e) {
//                error = true;
//            }
//
//            return content;
//        }
//
//        protected void onCancelled() {
//
//        }
//
//        protected void onPostExecute(String content) {
//            dismissProgressDialog();
//            if (error) {
//                if (content.contains("invalid_key")) {
//                    if (NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)) {
//                        String key = new ValidationsKey().getInstance(mContext).key(true);
//                        if (key.equalsIgnoreCase("null")) {
//                            Toast.makeText(mContext, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
//                        } else {
//                            laporSelectedRoom = new LaporSelectedRoom(ByonChatMainRoomActivity.this.getApplicationContext());
//                            laporSelectedRoom.execute(key);
//                        }
//                    } else {
//                        Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(mContext, content, Toast.LENGTH_LONG).show();
//                }
//            } else {
//                roomsDB.open();
//                boolean isActived = true;
//                ArrayList<ContactBot> botArrayListist = roomsDB.retrieveRooms("2");
//                if (botArrayListist.size() > 0) {
//                    isActived = false;
//                }
//                ContactBot contactBot = new ContactBot("", username, description, name, icon, "2", isActived);
//                roomsDB.insertRooms(contactBot);
//                roomsDB.close();
//                Toast.makeText(ByonChatMainRoomActivity.this, realname + " has been added to selected rooms", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//
//
///*
//    int[] colorIntArray = {R.color.walking,R.color.running,R.color.biking,R.color.paddling,R.color.golfing};
//    int[] iconIntArray = {R.drawable.ic_walk_white,R.drawable.ic_run_white,R.drawable.ic_bike_white,R.drawable.ic_add_white,R.drawable.ic_arrow_back_white};
//
//    protected void animateFab(final int position) {
//        fab.clearAnimation();
//        // Scale down animation
//        ScaleAnimation shrink =  new ScaleAnimation(1f, 0.2f, 1f, 0.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        shrink.setDuration(150);     // animation duration in milliseconds
//        shrink.setInterpolator(new DecelerateInterpolator());
//        shrink.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                // Change FAB color and icon
//                fab.setBackgroundTintList(getResources().getColorStateList(colorIntArray[position]));
//                fab.setImageDrawable(getResources().getDrawable(iconIntArray[position], null));
//
//                // Scale up animation
//                ScaleAnimation expand =  new ScaleAnimation(0.2f, 1f, 0.2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//                expand.setDuration(100);     // animation duration in milliseconds
//                expand.setInterpolator(new AccelerateInterpolator());
//                fab.startAnimation(expand);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//        fab.startAnimation(shrink);
//    }*/
//
//
//    @Override
//    public void onNeedLocationPermission() {
//
//    }
//
//    @Override
//    public void onExplainLocationPermission() {
//
//    }
//
//    @Override
//    public void onLocationPermissionPermanentlyDeclined(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {
//
//    }
//
//    @Override
//    public void onNeedLocationSettingsChange() {
//
//    }
//
//    @Override
//    public void onFallBackToSystemSettings(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {
//
//    }
//
//    @Override
//    public void onNewLocationAvailable(Location location) {
//
//    }
//
//    @Override
//    public void onMockLocationsDetected(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {
//        Toast.makeText(ByonChatMainRoomActivity.this.getApplicationContext(), "Please Turn Off Allow Mock Location", Toast.LENGTH_SHORT).show();
//        finish();
//    }
//
//    @Override
//    public void onError(LocationAssistant.ErrorType type, String message) {
//
//    }
//
//    @Override
//    protected void onDestroy() {
//        dismissProgressDialog();
//        super.onDestroy();
//    }
//
//    private void showProgressDialog() {
//        if (progressDialog == null) {
//            progressDialog = UtilsPD.createProgressDialog(this);
//        }
//        progressDialog.show();
//    }
//
//    private void dismissProgressDialog() {
//        if (progressDialog != null && progressDialog.isShowing()) {
//            progressDialog.dismiss();
//        }
//    }
//
//}
