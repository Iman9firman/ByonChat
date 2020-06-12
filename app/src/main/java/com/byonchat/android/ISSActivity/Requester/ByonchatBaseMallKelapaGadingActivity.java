package com.byonchat.android.ISSActivity.Requester;

import android.app.ProgressDialog;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.byonchat.android.R;
import com.byonchat.android.createMeme.FilteringImage;
import com.byonchat.android.data.model.MkgServices;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.list.IconItem;
import com.byonchat.android.ui.adapter.ChildRecyclerView;
import com.byonchat.android.ui.adapter.HeaderRecyclerView;
import com.byonchat.android.ui.adapter.NotifikasinoresultView;
import com.byonchat.android.utils.ClientSSLSocketFactory;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.mindorks.placeholderview.ExpandablePlaceHolderView;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ByonchatBaseMallKelapaGadingActivity extends AppCompatActivity implements ActionMode.Callback, RatingDialogListener {

    protected ActionMode actionMode;
    protected String mColor, mColorText, idRequse, latlongR;
    String idRequest;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onSetStatusBarColor();
        setContentView(getResourceLayout());
        onLoadView();
        onViewReady(savedInstanceState);

    }

    private void getDetail(String Url, Map<String, String> params2) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext(), new HurlStack(null, ClientSSLSocketFactory.getSocketFactory()));

        StringRequest sr = new StringRequest(Request.Method.POST, Url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jObKategori = jsonArray.getJSONObject(i);

                                vList.addView(new HeaderRecyclerView(ByonchatBaseMallKelapaGadingActivity.this, jObKategori.getString("nama_pekerjaan")));

                                JSONArray jArrKategori = new JSONArray(jObKategori.getString("request_detail"));
                                if (jArrKategori.length() > 0) {
                                    for (int j = 0; j < jArrKategori.length(); j++) {
                                        JSONObject jOb = jArrKategori.getJSONObject(j);
                                        String id = jOb.getString("id_request_detail");
                                        String name = jOb.getString("nama");
                                        String distance = jOb.getString("jarak");
                                        String status = jOb.getString("status");
                                        String totalKerja = jOb.getString("total_kerja");
                                        String id_reliever = jOb.getString("id_reliever");
                                        String waktuMulai = jOb.getString("waktu_mulai");
                                        String waktuSelesai = jOb.getString("waktu_selesai");
                                        String contact = jOb.getString("hp");
                                        String location = latlongR;

                                        MkgServices data = new MkgServices();
                                        data.id = id;
                                        data.child_name = name;
                                        data.child_distance = distance;
                                        data.child_status = status;
                                        data.child_contact = contact;
                                        data.child_location = location;
                                        data.total_kerja = totalKerja;
                                        data.id_reliever = id_reliever;
                                        data.child_distance = waktuMulai;

                                        vList.addView(new ChildRecyclerView(ByonchatBaseMallKelapaGadingActivity.this, data));
                                    }
                                } else {
                                    vList.addView(new NotifikasinoresultView(ByonchatBaseMallKelapaGadingActivity.this, "Waiting for Resources (HO)"));
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                },
                error -> Log.w("hasilny", "error")
        ) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return params2;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(180000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }


    protected void onSetStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
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
        idRequse = getIntent().getStringExtra(Constants.EXTRA_ITEM);
        latlongR = getIntent().getStringExtra(Constants.EXTRA_ROOM);

        if (mColor == null && mColorText == null && savedInstanceState != null) {
            mColor = savedInstanceState.getString(Constants.EXTRA_COLOR);
            mColorText = savedInstanceState.getString(Constants.EXTRA_COLORTEXT);
            idRequse = savedInstanceState.getString(Constants.EXTRA_ITEM);
            latlongR = savedInstanceState.getString(Constants.EXTRA_ROOM);
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
        vToolbarTitle.setText("Request Reliever");
    }

    protected void resolveListHistory() {
        vList.removeAllViews();

        Map<String, String> params = new HashMap<>();
        params.put("id_request", idRequse);
        getDetail("https://bb.byonchat.com/ApiReliever/index.php/Request/sub_list", params);

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.EXTRA_COLOR, mColor);
        outState.putString(Constants.EXTRA_COLORTEXT, mColorText);
        outState.putString(Constants.EXTRA_ITEM, idRequse);
        outState.putString(Constants.EXTRA_ROOM, latlongR);
    }

    public void setRating(String aa) {
        idRequest = aa;
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Bad", "Good", "Very Good", "Excellent !!!"))
                .setDefaultRating(2)
                .setTitle("Rate this Reliever")
                .setDescription("Please select some stars and give your feedback")
                .setCommentInputEnabled(true)
                .setStarColor(R.color.yelow)
                .setTitleTextColor(R.color.black_alpha_50)
                .setDescriptionTextColor(R.color.black_alpha_50)
                .setHint("Please write your comment here ...")
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .create(this)
                .show();
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int i, @NotNull String s) {
        String id = idRequest;
        Map<String, String> paramsLog = new HashMap<>();
        paramsLog.put("id", id);
        paramsLog.put("rating", i + "");
        paramsLog.put("note", s);

        getDetail(id, "https://bb.byonchat.com/ApiReliever/index.php/Rating/reliever", paramsLog, true);
    }

    private void getDetail(String id, String Url, Map<String, String> params2, Boolean hide) {
        ProgressDialog rdialog = new ProgressDialog(ByonchatBaseMallKelapaGadingActivity.this);
        rdialog.setMessage("Loading...");
        rdialog.show();

        RequestQueue queue = Volley.newRequestQueue(ByonchatBaseMallKelapaGadingActivity.this, new HurlStack(null, ClientSSLSocketFactory.getSocketFactory()));

        StringRequest sr = new StringRequest(Request.Method.POST, Url,
                response -> {
                    rdialog.dismiss();
                    if (hide) {
                        Map<String, String> params = new HashMap<>();
                        params.put("id", id);
                        params.put("status", "6");
                        getDetail(id, "https://bb.byonchat.com/ApiReliever/index.php/JobStatus", params, false);
                    } else {
                        Toast.makeText(ByonchatBaseMallKelapaGadingActivity.this, "sukses", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                },
                error -> rdialog.dismiss()
        ) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return params2;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(180000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }


}
