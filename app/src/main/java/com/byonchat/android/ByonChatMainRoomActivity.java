package com.byonchat.android;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
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
import com.byonchat.android.FragmentDinamicRoom.DinamicRoomSearchTaskActivity;
import com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity;
import com.byonchat.android.FragmentDinamicRoom.DinamicSLATaskActivity;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.helpers.Constants;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

import static com.byonchat.android.utils.Utility.reportCatch;

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
            try {
                Gson gson = new Gson();
                JsonObject jObj = new JsonObject();
                JsonArray jArr = new JsonArray();
                for (int i = 0; i < value.size(); i++) {
                    jArr.add(new JsonPrimitive(value.get(i).toString()));
                }
                jObj.add("payload", jArr);
                return gson.toJson(jObj);
            }catch (Exception e){
                reportCatch(e.getLocalizedMessage());
            }
        }

        return "";
    }

    @Override
    protected int getResourceLayout() {
        return R.layout.main_byonchat_room_activity;
    }

    @Override
    protected void onLoadToolbar() {
        try {
            vAppbar = getAppBar();
            vToolbar = getToolbar();

            if (color.equalsIgnoreCase("FFFFFF") && colorText.equalsIgnoreCase("000000")) {
                View lytToolbarDark = getLayoutInflater().inflate(R.layout.toolbar_custom_dark, vAppbar);
                Toolbar toolbarDark = lytToolbarDark.findViewById(R.id.toolbar);
                vAppbar.removeView(vToolbar);
            }
        }catch (Exception e){
            reportCatch(e.getLocalizedMessage());
        }
    }

    @Override
    protected void onLoadConfig(Bundle savedInstanceState) {
        resolveChatRoom(savedInstanceState);

        applyConfig();
    }

    @Override
    protected void onLoadView() {
        try {
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
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        super.onViewReady(savedInstanceState);
        try {
            resolveSearchBar();
            resolveToolbar();
            resolveFragment();
            resolveFloatingButton();
            resolveMaterialSearchView();
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            vSearchView.closeSearch();

            if (searchAppBarLayout.getVisibility() == View.VISIBLE)
                hideSearchBar(positionFromRight);
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
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
        try {
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

                        if (value.get(2).toString().equalsIgnoreCase("2613")) {
                            intent = new Intent(getApplicationContext(), DinamicSLATaskActivity.class);
                        }

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
        }catch (Exception e){
            reportCatch(e.getLocalizedMessage());
        }

        return null;
    }

    public String idLoof(ContentRoom contentRoomFind) {
        try {
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
        }catch (Exception e){
            reportCatch(e.getLocalizedMessage());
        }

        return null;
    }

    public String idLoofSearch(ContentRoom contentRoomFind) {
        try {
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
                        Intent intent = new Intent(getApplicationContext(), DinamicRoomSearchTaskActivity.class);
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
        }catch (Exception e){
            reportCatch(e.getLocalizedMessage());
        }

        return null;
    }


    protected void createShortcut() {
        try {
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
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    public String deleteById(final Integer posss) {
        try {
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
        }catch (Exception e){
            reportCatch(e.getLocalizedMessage());
        }
        return null;
    }

    public String deleteById(ContentRoom contentRoomFind) {
        try {
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
        }catch (Exception e){
            reportCatch(e.getLocalizedMessage());
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
        } else if (category.equalsIgnoreCase("14")) {
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