package com.byonchat.android.ISSActivity.Requester;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity;
import com.byonchat.android.R;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.createMeme.FilteringImage;
import com.byonchat.android.data.model.MkgServices;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.list.IconItem;
import com.byonchat.android.ui.adapter.ChildRatingRecyclerView;
import com.byonchat.android.ui.adapter.HeaderRatingRecyclerView;
import com.byonchat.android.ui.adapter.NotifikasinoresultView;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.mindorks.placeholderview.ExpandablePlaceHolderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RequesterBaseRatingActivity extends AppCompatActivity implements ActionMode.Callback {

    protected ActionMode actionMode;
    protected String mColor, mColorText, itemData, idRequest;

    protected ArrayList<MkgServices> items = new ArrayList<>();
    protected List<MkgServices> unSelectedItems = new ArrayList<>();
    protected List<String> maxSelected = new ArrayList<>();
    protected List<Map<String, String>> reportToResources = new ArrayList<>();
    protected List<String> tersedia = new ArrayList<>();
    protected List<String> namaPekerjaan = new ArrayList<>();

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

    String selectedGender0 = "Tampilkan semua";
    public static Map<Integer, String> map_gender = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onSetStatusBarColor();
        setContentView(getResourceLayout());
        onLoadView();
        onViewReady(savedInstanceState);
    }

    protected void onSetStatusBarColor() {
        //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#022b95"));
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

        FilteringImage.SystemBarBackground(getWindow(), Color.parseColor("#022b95"));
        vToolbar.setBackgroundColor(Color.parseColor("#022b95"));
        vToolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        vToolbarTitle.setTextColor(Color.parseColor("#ffffff"));

        Drawable mDrawable = getResources().getDrawable(R.drawable.ic_keyboard_arrow_left_black_24dp);
        mDrawable.setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_ATOP);
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

    protected void resolveListHistory() {
        vList.removeAllViews();
        items.clear();
        maxSelected.clear();
        tersedia.clear();
        namaPekerjaan.clear();

        try {
            JSONArray jsonArraySatu = new JSONArray(itemData);
            for (int i = 0; i < jsonArraySatu.length(); i++) {
                idRequest = jsonArraySatu.getJSONObject(i).getString("id_request");
                JSONArray jsonArrayDua = new JSONArray(jsonArraySatu.getJSONObject(i).getString("sub_request"));

                for (int ia = 0; ia < jsonArrayDua.length(); ia++) {

                    if(map_gender.size() != 0){
                        selectedGender0 = map_gender.get(ia);
                    }

                    if(selectedGender0 == null){
                        selectedGender0 = "Tampilkan semua";
                    }

                    int finalIa = ia;
                    vList.addView(new HeaderRatingRecyclerView(RequesterBaseRatingActivity.this, selectedGender0,jsonArraySatu.getJSONObject(i).getString("nama_jjt") + " - "
                            + jsonArrayDua.getJSONObject(ia).getString("nama_pekerjaan"), jsonArraySatu.getJSONObject(i).getString("jjt_lat") + ":" + jsonArraySatu.getJSONObject(i).getString("jjt_long"), jsonArrayDua.getJSONObject(ia).getString("request_detail"), jsonArrayDua.getJSONObject(ia).getString("jumlah"),
                            new HeaderRatingRecyclerView.OnSpinnerChangeListener() {
                                @Override
                                public void onItemChanges(int position, String gender) {

                                    if(map_gender.get(finalIa) == null) {
                                        map_gender.put(finalIa, gender);
                                        resolveListHistory();
                                    }

                                    if(!map_gender.get(finalIa).equalsIgnoreCase(gender)) {
                                        map_gender.put(finalIa, gender);
                                        resolveListHistory();
                                    }
                                }
                            }));

                    JSONArray jsonArrayTiga = new JSONArray(jsonArrayDua.getJSONObject(ia).getString("request_detail"));

                    maxSelected.add(ia, jsonArrayDua.getJSONObject(ia).getString("jumlah"));
                    tersedia.add(jsonArrayTiga.length() + "");
                    namaPekerjaan.add(jsonArrayDua.getJSONObject(ia).getString("nama_pekerjaan"));

                    if (jsonArrayTiga.length() > 0) {

                        for (int j = 0; j < jsonArrayTiga.length(); j++) {
                            JSONObject jOb = jsonArrayTiga.getJSONObject(j);
                            String gender = "-";
                            if (jOb.has("gender")) {
                                gender = jOb.getString("gender");
                            }

                            if(map_gender.size() != 0) {
                                if (map_gender.get(ia).equalsIgnoreCase("Laki - laki")) {
                                    if (!gender.equalsIgnoreCase("Female")) {
                                        buildChildRelieverList(jsonArrayTiga, j, ia);
                                    }
                                } else if (map_gender.get(ia).equalsIgnoreCase("Perempuan")) {
                                    if (!gender.equalsIgnoreCase("Male")) {
                                        buildChildRelieverList(jsonArrayTiga, j, ia);
                                    }
                                } else {
                                    buildChildRelieverList(jsonArrayTiga, j, ia);
                                }
                            } else {
                                buildChildRelieverList(jsonArrayTiga, j, ia);
                            }
                        }

                        if (Integer.valueOf(jsonArrayDua.getJSONObject(ia).getString("jumlah")) > jsonArrayTiga.length()) {
                            vList.addView(new NotifikasinoresultView(RequesterBaseRatingActivity.this, "*" + (Integer.valueOf(jsonArrayDua.getJSONObject(ia).getString("jumlah")) - jsonArrayTiga.length()) + " Reliever akan dicarikan oleh Team Resources"));
                            Map<String, String> paramss = new HashMap<>();
                            paramss.put("id_sub_request", jsonArrayDua.getJSONObject(ia).getString("id_sub_request"));
                            paramss.put("jumlah", (Integer.valueOf(jsonArrayDua.getJSONObject(ia).getString("jumlah")) - jsonArrayTiga.length()) + "");

                            reportToResources.add(paramss);

                        } else if (items.size() == 0) {
                            vList.addView(new NotifikasinoresultView(RequesterBaseRatingActivity.this, "Reliever " + map_gender.get(ia) +" tidak ditemukan di Posisi ini"));
                            Map<String, String> paramss = new HashMap<>();
                            paramss.put("id_sub_request", jsonArrayDua.getJSONObject(ia).getString("id_sub_request"));
                            paramss.put("jumlah", jsonArrayDua.getJSONObject(ia).getString("jumlah"));

                            reportToResources.add(paramss);
                        }

                    } else {

                        vList.addView(new NotifikasinoresultView(RequesterBaseRatingActivity.this, "Reliever tidak ditemukan, Harap tekan SUBMIT untuk Pencarian Reliever oleh Team Resources"));
                        Map<String, String> paramss = new HashMap<>();
                        paramss.put("id_sub_request", jsonArrayDua.getJSONObject(ia).getString("id_sub_request"));
                        paramss.put("jumlah", jsonArrayDua.getJSONObject(ia).getString("jumlah"));

                        reportToResources.add(paramss);
                    }


                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /* Ketika menambahkan view ListReliever
     *
     */
    private void buildChildRelieverList(JSONArray jsonArrayTiga, int j, int ia) throws JSONException {
        JSONObject jOb = jsonArrayTiga.getJSONObject(j);

        String id = jOb.getString("id_request_detail");
        String name = jOb.getString("nama");
        String id_reliever = jOb.getString("id_reliever");
        String distance = jOb.getString("jarak");
        String total = jOb.getString("total_kerja");
        String status = jOb.getString("status");
        String contact = jOb.getString("hp");
        String location = jOb.getString("lat") + ":" + jOb.getString("long");
        String rating = jOb.getString("rating");
        String gender = "-";
        if (jOb.has("gender")) {
            gender = jOb.getString("gender");
        }

        int titik = distance.length() - distance.indexOf(".");
        if (titik > 4) {
            titik = 4;
        }

        boolean data_checked = false;
        if(unSelectedItems.size() != 0){
            for (int yy = 0; yy < unSelectedItems.size(); yy++) {
                if (unSelectedItems.get(yy).id.equalsIgnoreCase(id)){
                    data_checked = true;
                }
            }
        }

        MkgServices data = new MkgServices();
        data.header_id = ia;
        data.id = id;
        data.id_reliever = id_reliever;
        data.child_name = name;
        data.child_distance = distance.substring(0, distance.indexOf(".") + titik) + " KM";
        data.child_status = status;
        data.child_contact = contact;
        data.child_gender = gender;
        data.child_location = location;
        data.child_rating = rating;
        data.total_kerja = total;
        data.isChecked = data_checked;

        items.add(data);

        vList.addView(new ChildRatingRecyclerView(RequesterBaseRatingActivity.this, data, new ChildRatingRecyclerView.OnCheckedChangeListener() {

            @Override
            public void onItemClick(int position, MkgServices datas, Boolean check) {
                List<MkgServices> selected = new ArrayList<>();
                int size = items.size();
                for (int is = size - 1; is >= 0; is--) {
                    if (items.get(is).id.equalsIgnoreCase(datas.id)) {
                        items.get(is).isChecked = !datas.isChecked();
                        data.isChecked = check;
                    }
                    selected.add(items.get(is));
                }
                for (int oio = 0; oio < selected.size(); oio++) {
                }
                unSelectedItems = getSelectedList(selected);
            }
        }));
    }

    public int getCountCheck(int idHeader) {
        int jjs = 0;
        for (MkgServices is : unSelectedItems) {
            if (is.header_id == idHeader) {
                jjs++;
            }
        }
        if (jjs == Integer.valueOf(maxSelected.get(idHeader))) {
            return jjs;
        }
        return 0;
    }

    public int getCountCheckReal(int idHeader) {
        int jjs = 0;
        for (MkgServices is : unSelectedItems) {
            if (is.header_id == idHeader) {
                jjs++;
            }
        }
        if (jjs == Integer.valueOf(maxSelected.get(idHeader))) {
            return jjs;
        }
        return jjs;
    }


    public List<MkgServices> getSelectedList(List<MkgServices> items) {
        List<MkgServices> selectedContacts = new ArrayList<>();
        int size = items.size();
        for (int i = size - 1; i >= 0; i--) {
            if (items.get(i).isChecked()) {
                selectedContacts.add(items.get(i));
            }
        }
        return selectedContacts;
    }

    protected void onListChecked(List<MkgServices> items) {

        if (NetworkInternetConnectionStatus.getInstance(RequesterBaseRatingActivity.this).isOnline(RequesterBaseRatingActivity.this)) {
            for (int max = 0; max < maxSelected.size(); max++) {
                if (Integer.valueOf(tersedia.get(max)) > Integer.valueOf(maxSelected.get(max))) {
                    if (Integer.valueOf(maxSelected.get(max)) > getCountCheckReal(max)) {
                        Toast.makeText(RequesterBaseRatingActivity.this, "Pilih Reliever " + namaPekerjaan.get(max) + " Sesuai dengan request.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    if (Integer.valueOf(tersedia.get(max)) > getCountCheckReal(max)) {
                        Toast.makeText(RequesterBaseRatingActivity.this, "Pilih semua Reliever " + namaPekerjaan.get(max), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

            }

            //validasi jika tidak ada reliver atau kurang dari request
            if (reportToResources.size() > 0) {

                try {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
//                    callIntent.setData(Uri.parse("tel:+" + "628551551294"));
                    callIntent.setData(Uri.parse("tel:+" + "628561782163")); //Nomor Baru
                    startActivity(callIntent);
                } catch (ActivityNotFoundException activityException) {
                }

                requestByParony("https://bb.byonchat.com/ApiReliever/index.php/Request/req_reliever", reportToResources.get(0), items);
            } else {

                if (items.size() > 0) {
                    String pilih = "";
                    for (MkgServices is : items) {
                        pilih += is.id + ",";
                    }

                    Map<String, String> params = new HashMap<>();
                    params.put("id_request_detail", idRequest);
                    params.put("id", pilih.substring(0, pilih.length() - 1));
                    getDetail("https://bb.byonchat.com/ApiReliever/index.php/Request/submit_reliever", params);

                }
            }

        } else {
            Toast.makeText(RequesterBaseRatingActivity.this, "Tolong Periksa Koneksi Internet Anda", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestByParony(String Url, Map<String, String> params2, List<MkgServices> itemss) {
        ProgressDialog rdialoeg = new ProgressDialog(RequesterBaseRatingActivity.this);
        rdialoeg.setMessage("Request Manual...");
        rdialoeg.show();

        RequestQueue queue = Volley.newRequestQueue(RequesterBaseRatingActivity.this);

        StringRequest sr = new StringRequest(Request.Method.POST, Url,
                response -> {
                    reportToResources.remove(params2);

                    if (reportToResources.size() > 0) {
                        int size = reportToResources.size();
                        for (int i = 0; i < size; i++) {
                            requestByParony("https://bb.byonchat.com/ApiReliever/index.php/Request/req_reliever", reportToResources.get(i), itemss);
                        }
                        rdialoeg.dismiss();
                    } else {
                        rdialoeg.dismiss();
                        if (itemss.size() > 0) {
                            String pilih = "";
                            for (MkgServices is : itemss) {
                                pilih += is.id + ",";
                            }

                            Map<String, String> params = new HashMap<>();
                            params.put("id_request_detail", idRequest);
                            params.put("id", pilih.substring(0, pilih.length() - 1));
                            getDetail("https://bb.byonchat.com/ApiReliever/index.php/Request/submit_reliever", params);

                        } else {
                            Toast.makeText(RequesterBaseRatingActivity.this, "Sukses", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                },
                error -> rdialoeg.dismiss()
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


    private void getDetail(String Url, Map<String, String> params2) {
        ProgressDialog rdialog = new ProgressDialog(RequesterBaseRatingActivity.this);
        rdialog.setMessage("Loading...");
        rdialog.show();

        RequestQueue queue = Volley.newRequestQueue(RequesterBaseRatingActivity.this);

        StringRequest sr = new StringRequest(Request.Method.POST, Url,
                response -> {
                    rdialog.dismiss();
                    finish();
                    Toast.makeText(RequesterBaseRatingActivity.this, "Sukses", Toast.LENGTH_SHORT).show();
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
