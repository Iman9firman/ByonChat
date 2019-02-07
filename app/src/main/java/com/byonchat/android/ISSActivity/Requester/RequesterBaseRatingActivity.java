package com.byonchat.android.ISSActivity.Requester;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.R;
import com.byonchat.android.createMeme.FilteringImage;
import com.byonchat.android.data.model.MkgServices;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.list.IconItem;
import com.byonchat.android.ui.adapter.ChildRatingRecyclerView;
import com.byonchat.android.ui.adapter.HeaderRatingRecyclerView;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.mindorks.placeholderview.ExpandablePlaceHolderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public abstract class RequesterBaseRatingActivity extends AppCompatActivity implements ActionMode.Callback {

    protected ActionMode actionMode;
    protected String mColor, mColorText, itemData;

    protected ArrayList<MkgServices> items = new ArrayList<>();
    protected List<MkgServices> unSelectedItems = new ArrayList<>();

    @NonNull
    protected AppBarLayout vAppBar;

    @NonNull
    protected Toolbar vToolbar;

    @NonNull
    protected ExpandablePlaceHolderView vList;

    @NonNull
    protected RelativeLayout vToolbarBack;

    @NonNull
    protected MaterialSearchView vSearchView;

    @NonNull
    protected TextView vToolbarTitle;

    @NonNull
    protected ImageView vImgToolbarBack;

    @NonNull
    protected Button vBtnSubmit;


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
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
    }

    protected void resolveSubmitButton() {
        vBtnSubmit.setOnClickListener(v -> {
            onListChecked(unSelectedItems);
        });
    }

    protected abstract int getResourceLayout();

    protected abstract void onLoadView();

    protected void applyChatConfig() {

    }

    protected void onViewReady(Bundle savedInstanceState) {
        resolveChatRoom(savedInstanceState);

        applyChatConfig();
    }

    protected void resolveChatRoom(Bundle savedInstanceState) {
        mColor = getIntent().getStringExtra(Constants.EXTRA_COLOR);
        mColorText = getIntent().getStringExtra(Constants.EXTRA_COLORTEXT);
        itemData = getIntent().getStringExtra(Constants.EXTRA_ITEM);
        if (mColor == null && mColorText == null && savedInstanceState != null) {
            mColor = savedInstanceState.getString(Constants.EXTRA_COLOR);
            mColorText = savedInstanceState.getString(Constants.EXTRA_COLORTEXT);
        }

        if (mColor == null && mColorText == null) {
            finish();
            return;
        }
    }

    protected void resolveToolbar() {
        setSupportActionBar(vToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FilteringImage.SystemBarBackground(getWindow(), Color.parseColor("#" + mColor));
        vToolbar.setBackgroundColor(Color.parseColor("#" + mColor));
        vToolbar.setTitleTextColor(Color.parseColor("#" + mColorText));
        vToolbarTitle.setTextColor(Color.parseColor("#" + mColorText));

        Drawable mDrawable = getResources().getDrawable(R.drawable.ic_keyboard_arrow_left_black_24dp);
        mDrawable.setColorFilter(Color.parseColor("#" + mColorText), PorterDuff.Mode.SRC_ATOP);
        vImgToolbarBack.setImageDrawable(mDrawable);

        vToolbarBack.setOnClickListener(v -> {
            if (vSearchView.isSearchOpen()) {
                vSearchView.closeSearch();
            } else {
                super.onBackPressed();
            }
        });
        vToolbarTitle.setText("Search Reliever");
    }

    /*protected void resolveListHistory() {
        vList.removeAllViews();

        try {
            JSONArray jsonArraySatu = new JSONArray(itemData);
            for (int i = 0; i < jsonArraySatu.length(); i++) {
                JSONArray jsonArrayDua = new JSONArray(jsonArraySatu.getJSONObject(i).getString("sub_request"));
                for (int ia = 0; ia < jsonArrayDua.length(); ia++) {

                    vList.addView(new HeaderRatingRecyclerView(RequesterBaseRatingActivity.this, jsonArraySatu.getJSONObject(i).getString("nama_jjt") + " "
                            + jsonArrayDua.getJSONObject(ia).getString("nama_pekerjaan"), jsonArraySatu.getJSONObject(i).getString("jjt_lat") + ":" + jsonArraySatu.getJSONObject(i).getString("jjt_long"), jsonArrayDua.getJSONObject(ia).getString("request_detail")));

                    JSONArray jsonArrayTiga = new JSONArray(jsonArrayDua.getJSONObject(ia).getString("request_detail"));
                    for (int j = 0; j < jsonArrayTiga.length(); j++) {
                        JSONObject jOb = jsonArrayTiga.getJSONObject(j);
                        String id = jOb.getString("id_request_detail");
                        String name = jOb.getString("nama");
                        String distance = jOb.getString("jarak");
                        String total = jOb.getString("total_kerja");
                        String status = jOb.getString("status");
                        String contact = jOb.getString("hp");
                        String location = jOb.getString("lat") + ":" + jOb.getString("long");
                        String rating = jOb.getString("rating");

                        int titik = distance.length() - distance.indexOf(".");
                        if (titik > 4) {
                            titik = 4;
                        }
                        MkgServices data = new MkgServices();
                        data.id = id;
                        data.child_name = name;
                        data.child_distance = distance.substring(0, distance.indexOf(".") + titik) + " KM";
                        data.child_status = status;
                        data.child_contact = contact;
                        data.child_location = location;
                        data.child_rating = rating;
                        vList.addView(new ChildRatingRecyclerView(RequesterBaseRatingActivity.this, data));
                    }
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
*/
    protected void resolveListHistory() {
        vList.removeAllViews();

        try {
            JSONArray jsonArraySatu = new JSONArray(itemData);
            for (int i = 0; i < jsonArraySatu.length(); i++) {
                JSONArray jsonArrayDua = new JSONArray(jsonArraySatu.getJSONObject(i).getString("sub_request"));
                for (int ia = 0; ia < jsonArrayDua.length(); ia++) {

                    vList.addView(new HeaderRatingRecyclerView(RequesterBaseRatingActivity.this, jsonArraySatu.getJSONObject(i).getString("nama_jjt") + " "
                            + jsonArrayDua.getJSONObject(ia).getString("nama_pekerjaan"), jsonArraySatu.getJSONObject(i).getString("jjt_lat") + ":" + jsonArraySatu.getJSONObject(i).getString("jjt_long"), jsonArrayDua.getJSONObject(ia).getString("request_detail")));

                    JSONArray jsonArrayTiga = new JSONArray(jsonArrayDua.getJSONObject(ia).getString("request_detail"));
                    for (int j = 0; j < jsonArrayTiga.length(); j++) {
                        JSONObject jOb = jsonArrayTiga.getJSONObject(j);
                        String id = jOb.getString("id_request_detail");
                        String name = jOb.getString("nama");
                        String distance = jOb.getString("jarak");
                        String total = jOb.getString("total_kerja");
                        String status = jOb.getString("status");
                        String contact = jOb.getString("hp");
                        String location = jOb.getString("lat") + ":" + jOb.getString("long");
                        String rating = jOb.getString("rating");

                        int titik = distance.length() - distance.indexOf(".");
                        if (titik > 4) {
                            titik = 4;
                        }
                        MkgServices data = new MkgServices();
                        data.header_id = ia;
                        data.id = id;
                        data.child_name = name;
                        data.child_distance = distance.substring(0, distance.indexOf(".") + titik) + " KM";
                        data.child_status = status;
                        data.child_contact = contact;
                        data.child_location = location;
                        data.child_rating = rating;
                        data.isChecked = false;

                        items.add(data);

                        vList.addView(new ChildRatingRecyclerView(RequesterBaseRatingActivity.this, j, data, new ChildRatingRecyclerView.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position, MkgServices data) {
                                List<MkgServices> selected = new ArrayList<>();
                                List<MkgServices> unSelected = new ArrayList<>();
                                int size = items.size();
                                for (int i = size - 1; i >= 0; i--) {
                                    if (items.get(i).id.equalsIgnoreCase(data.id)) {
                                        items.get(i).isChecked = !data.isChecked;
                                    }
                                    unSelected.add(items.get(i));

                                }
                                unSelectedItems = getSelectedList(unSelected);
                            }
                        }));
                    }
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<MkgServices> getSelectedList(List<MkgServices> items) {
        List<MkgServices> selectedContacts = new ArrayList<>();
        int size = items.size();
        for (int i = size - 1; i >= 0; i--) {
            if (!items.get(i).isChecked()) {
                selectedContacts.add(items.get(i));
            }
        }
        return selectedContacts;
    }

    protected void onListChecked(List<MkgServices> items) {
        int total = items.size();
        Toast.makeText(this, total + "", Toast.LENGTH_SHORT).show();
        String sasa = "";
        for (int i = 0; i < items.size(); i++) {
            sasa += i + ",";
            Log.w("kepoah", items.get(i).id + " -- " + items.get(i).child_name);
        }
        finish();
        //sasa.subString(0,sasa.)
    }

    protected void hideViews() {
    }

    protected void showViews() {
    }

    public void onContactSelected(List<IconItem> selectedItem) {
        int total = selectedItem.size();
        boolean hasCheckedItems = total > 0;
        if (hasCheckedItems && actionMode == null) {
            actionMode = startSupportActionMode(this);
        } else if (!hasCheckedItems && actionMode != null) {
            actionMode.finish();
        }
        if (actionMode != null) {
            actionMode.setTitle(String.valueOf(selectedItem.size()));
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.menu_history_chat, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        menu.findItem(R.id.action_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        actionMode = null;
    }

    @NonNull
    protected abstract AppBarLayout getAppbar();

    @NonNull
    protected abstract Toolbar getToolbar();

    @NonNull
    protected abstract ExpandablePlaceHolderView getListView();

    @NonNull
    protected abstract MaterialSearchView getMaterialSearchView();

    @NonNull
    protected abstract RelativeLayout getToolbarBack();

    @NonNull
    protected abstract ImageView getImgToolbarBack();

    @NonNull
    protected abstract TextView getToolbarTitle();

    @NonNull
    protected abstract Button getSubmitButton();

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.EXTRA_COLOR, mColor);
        outState.putString(Constants.EXTRA_COLORTEXT, mColorText);
    }


}
