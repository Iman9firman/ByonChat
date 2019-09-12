package com.honda.android.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.honda.android.ByonChatMainRoomActivity;
import com.honda.android.R;
import com.honda.android.communication.NetworkInternetConnectionStatus;
import com.honda.android.createMeme.FilteringImage;
import com.honda.android.data.model.Video;
import com.honda.android.data.remote.SoloApi;
import com.honda.android.helpers.Constants;
import com.honda.android.local.Byonchat;
import com.honda.android.provider.BotListDB;
import com.honda.android.ui.adapter.ByonchatVideoAdapter;
import com.honda.android.ui.adapter.ForwardItemClickListener;
import com.honda.android.ui.view.ByonchatRecyclerView;
import com.honda.android.ui.view.RxActivity;
import com.honda.android.utils.ByonchatFileUtil;
import com.honda.android.utils.Utility;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.honda.android.helpers.Constants.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;

public class ByonchatVideoBeforeDownloadActivity extends RxActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static String EXTRA_USERNAME = "username";
    public static String EXTRA_ID_ROOMS_TAB = "id_rooms_tab";
    public static String EXTRA_URL_TEMBAK = "url_tembak";
    public static String EXTRA_COLOR = "color";
    public static String EXTRA_COLOR_TEXT = "colorText";

    public static Intent generateIntent(Context context, String username, String id_rooms_tab, String url_tembak, String color, String colorText) {
        Intent intent = new Intent(context, ByonchatVideoBeforeDownloadActivity.class);
        intent.putExtra(EXTRA_USERNAME, username);
        intent.putExtra(EXTRA_ID_ROOMS_TAB, id_rooms_tab);
        intent.putExtra(EXTRA_URL_TEMBAK, url_tembak);
        intent.putExtra(EXTRA_COLOR, color);
        intent.putExtra(EXTRA_COLOR_TEXT, colorText);
        return intent;
    }

    protected List<Video> videos = new ArrayList<>();
    protected String username, id_rooms_tab, url_tembak, color, colorText;

    protected LinearLayoutManager chatLayoutManager;
    protected ByonchatVideoAdapter mAdapter;

    protected boolean isChanged = true;

    @NonNull
    protected RelativeLayout vFrameError;
    @NonNull
    protected RelativeLayout vFrameInternetError;
    @NonNull
    protected Toolbar vToolbar;
    @NonNull
    protected AppBarLayout vAppBar;
    @NonNull
    protected SearchView vSearchText;
    @NonNull
    protected TextView vWarningTitle;
    @NonNull
    protected TextView vWarningContent;
    @NonNull
    protected TextView vInternetTitle;
    @NonNull
    protected TextView vInternetContent;
    @NonNull
    protected ByonchatRecyclerView vListVideoTube;
    @NonNull
    protected SwipeRefreshLayout vRefreshList;
    @NonNull
    protected FloatingActionButton vFab;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_USERNAME, username);
        outState.putString(EXTRA_ID_ROOMS_TAB, id_rooms_tab);
        outState.putString(EXTRA_URL_TEMBAK, url_tembak);
        outState.putString(EXTRA_COLOR, color);
        outState.putString(EXTRA_COLOR_TEXT, colorText);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b_video_before_download_activity);

        resolveVideos(savedInstanceState);

        checkPermissionREAD_EXTERNAL_STORAGE(ByonchatVideoBeforeDownloadActivity.this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        resolveActivity();
        resolveToolbar();
        onActivityCreated();
    }

    protected void resolveActivity() {
        vFrameError = getFrameError();
        vFrameInternetError = getFrameInternetError();
        vAppBar = getAppbar();
        vToolbar = getToolbar();
        vWarningTitle = getWarningTitle();
        vWarningContent = getWarningContent();
        vInternetTitle = getInternetTitle();
        vInternetContent = getInternetContent();
        vListVideoTube = getListFrequently();
        vSearchText = getSearchbar();
        vRefreshList = getRefreshList();
        vFab = getFabView();
    }

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showDialog("Write External storage", context,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{permission},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    protected void resolveToolbar() {
        setSupportActionBar(vToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FilteringImage.SystemBarBackground(getWindow(), Color.parseColor("#" + color));

        vToolbar.setBackgroundColor(Color.parseColor("#" + color));
        vToolbar.setTitleTextColor(Color.parseColor("#" + colorText));
    }

    protected void onActivityCreated() {
        resolveListVideoTube();
        resolveRefreshList();
        resolveSearchButton();

        onViewReady();
    }

    @Override
    public void onRefresh() {
        onViewReady();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    protected void resolveListVideoTube() {
        videos = new ArrayList<>();
        vListVideoTube.setUpAsList();
        vListVideoTube.setNestedScrollingEnabled(false);
        chatLayoutManager = (LinearLayoutManager) vListVideoTube.getLayoutManager();
        mAdapter = new ByonchatVideoAdapter(getApplicationContext(), videos, new ForwardItemClickListener() {
            @Override
            public void onItemVideoClick(Video contact) {
                if (mAdapter.getSelectedComments().isEmpty()) {
                    adapterSelected(contact);
                } else {
                    adapterSelected(contact);
                }
            }

            @Override
            public void onItemVideoLongClick(Video contact) {

            }
        }, (view, position, video) -> {
            showPopup(view, video);
        });

        mAdapter.setOnItemClickListener((view, position) -> {
            if (isChanged) {
                List<Video> videoList = new ArrayList<>();
                Video video = videos.get(position);
                videoList.add(video);
                Intent intent = ByonchatStreamingVideoTubeActivity.generateIntent(getApplicationContext(), video, color, colorText);
                startActivity(intent);
            } else
                adapterSelected((Video) mAdapter.getData().get(position));
        });

        mAdapter.setOnLongItemClickListener((view, position) -> {
            /*adapterSelected((Video) mAdapter.getData().get(position));*/
        });

        vListVideoTube.setAdapter(mAdapter);
    }

    protected void adapterSelected(Video contact) {
        contact.isSelected = !contact.isSelected();
        mAdapter.notifyDataSetChanged();
        onContactSelected(mAdapter.getSelectedComments());
    }

    public void onContactSelected(List<Video> selectedContacts) {
        int total = selectedContacts.size();
        boolean hasCheckedItems = total > 0;
        if (hasCheckedItems) {
            isChanged = false;
        } else {
            isChanged = true;
        }
    }

    protected void resolveRefreshList() {
        vRefreshList.setOnRefreshListener(this);
    }

    protected void resolveSearchButton() {
        performSearch();
    }

    protected void onViewReady() {
        if (NetworkInternetConnectionStatus.
                getInstance(getApplicationContext()).
                isOnline(getApplicationContext())) {
            vFrameInternetError.setVisibility(View.GONE);
            vListVideoTube.setVisibility(View.VISIBLE);

            new getVideoList(false).execute(username, id_rooms_tab, url_tembak);
        } else {
            resolveConnectionProblem();
        }
    }

    protected void performSearch() {
        vSearchText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (NetworkInternetConnectionStatus.
                        getInstance(getApplicationContext()).
                        isOnline(getApplicationContext())) {
                    new getVideoList(true).execute(username, id_rooms_tab, s, url_tembak);
                } else {
                    vFrameInternetError.setVisibility(View.VISIBLE);
                    vListVideoTube.setVisibility(View.GONE);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    protected void showRefresh() {
        vRefreshList.setRefreshing(true);
    }

    protected void hideRefresh() {
        vRefreshList.setRefreshing(false);
    }

    protected void showPopup(View view, final Video video) {
        View menuItemView = view.findViewById(R.id.more);
        final PopupMenu popup = new PopupMenu(this, menuItemView);
        MenuInflater inflate = popup.getMenuInflater();

        inflate.inflate(R.menu.byonchat_menu_download, popup.getMenu());
        popup.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.action_download:
                    if (checkPermissionREAD_EXTERNAL_STORAGE(ByonchatVideoBeforeDownloadActivity.this)) {
                        FileDownload(video);
                    }
                    break;
                default:
                    return false;
            }
            return false;
        });
        popup.show();
    }

    protected void FileDownload(Video video) {
        video.setDownloading(true);
        SoloApi.getInstance()
                .downloadFile(video.url, "VID-" + video.add_date + "-" + video.title + ".mp4",
                        percentage -> video.setProgress((int) percentage))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .doOnNext(file1 -> {
                    ByonchatFileUtil.notifySystem(file1);
                    video.setDownloading(false);
                    Byonchat.getVideoTubeDataStore().addOrUpdate(video,
                            file1.getAbsolutePath());
                })
                .subscribe(file1 -> {
                    video.url = file1.getAbsolutePath();
                    Byonchat.getVideoTubeDataStore().update(video);

                    video.isSelected = true;
                    notifyDataChanged(video);
                }, throwable -> {
                    throwable.printStackTrace();
                    video.setDownloading(false);
                    showError(getApplicationContext().getString(R.string.byonchat_failed_download_file));
                });
    }

    protected void notifyDataChanged(Video video) {
        mAdapter.addOrUpdate(video);
    }

    protected void showError(String errorMessage) {
        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    protected RelativeLayout getFrameError() {
        return (RelativeLayout) findViewById(R.id.frame_error);
    }

    protected RelativeLayout getFrameInternetError() {
        return (RelativeLayout) findViewById(R.id.frame_internet_error);
    }

    protected AppBarLayout getAppbar() {
        return (AppBarLayout) findViewById(R.id.appbar);
    }

    protected Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }

    protected TextView getWarningTitle() {
        return (TextView) findViewById(R.id.text_warning_title);
    }

    protected TextView getWarningContent() {
        return (TextView) findViewById(R.id.text_warning_content);
    }

    protected TextView getInternetTitle() {
        return (TextView) findViewById(R.id.text_internet_error);
    }

    protected TextView getInternetContent() {
        return (TextView) findViewById(R.id.text_internet_error_content);
    }

    protected ByonchatRecyclerView getListFrequently() {
        return (ByonchatRecyclerView) findViewById(R.id.list_videotube);
    }

    protected SearchView getSearchbar() {
        return (SearchView) findViewById(R.id.searchtext);
    }

    protected SwipeRefreshLayout getRefreshList() {
        return (SwipeRefreshLayout) findViewById(R.id.refresh_videotube);
    }

    protected FloatingActionButton getFabView() {
        return (FloatingActionButton) findViewById(R.id.fab);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private class getVideoList extends AsyncTask<String, Void, String> {
        protected boolean isSearchInput;

        protected getVideoList(boolean isSearchInput) {
            this.isSearchInput = isSearchInput;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showRefresh();
        }

        @Override
        protected String doInBackground(String... params) {
            if (isSearchInput)
                return getVideoListInput(params);
            else
                return getVideoList(params);
        }

        @Override
        protected void onPostExecute(String result) {
            setList(result);
        }
    }

    protected void setList(String result) {

        if (videos.size() > 0)
            videos.clear();

        if (Utility.isJSONValid(result)) {
            try {
                JSONObject jObject = new JSONObject(result);
                String id_rooms_tab = jObject.getString("id_rooms_tab");
                String username_room = jObject.getString("username_room");
                JSONArray jArray = new JSONArray(jObject.getString("data"));
                if (jArray.length() > 0) {
                    vFrameError.setVisibility(View.GONE);
                    vFrameInternetError.setVisibility(View.GONE);
                    vListVideoTube.setVisibility(View.VISIBLE);

                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject object = jArray.getJSONObject(i);
                        String id = object.getString("id");
                        String title = object.getString("title");
                        String description = object.getString("description");
                        String file_gallery = object.getString("file_gallery");
                        String thumbnail = object.getString("thumbnail");
                        String type = object.getString("type");
                        String durasi_video = object.getString("durasi_video");
                        String file_size = object.getString("file_size");
                        String add_date = object.getString("add_date");

                        Video video = new Video();
                        video.id = Long.valueOf(id);
                        video.title = title;
                        video.description = description;
                        video.length = durasi_video;
                        video.size = file_size;
                        video.url = file_gallery;
                        video.thumbnail = thumbnail;
                        video.type = Video.TYPE_TEXT;
                        video.video_type = type;
                        video.add_date = add_date;

                        videos.add(video);
                    }
                } else {
                    resolveEmptyVideo();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                resolveEmptyVideo();
            }
        } else if (result.equalsIgnoreCase("error")) {
            resolveEmptyVideo();
        }

        mAdapter.setItems(videos);

        hideRefresh();
    }

    protected void resolveEmptyVideo() {
        String warning_text = "There are no ByonChat video search results for\n &quot;<b>" + vSearchText.getQuery() + "</b>&quot; at this time";
        vFrameError.setVisibility(View.VISIBLE);
        vWarningTitle.setText("No videos found");
        vWarningContent.setText(Html.fromHtml(warning_text));
    }

    protected void resolveConnectionProblem() {
        String title = "Home isn't responding";
        String content = "That's why we can't show videos right now\nPlease check back later";
        vFrameInternetError.setVisibility(View.VISIBLE);
        vListVideoTube.setVisibility(View.GONE);
        vInternetTitle.setText(title);
        vInternetContent.setText(content);
    }

    public static String getVideoList(String... args) {
        InputStream inputStream;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(args[2]);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                    1);
            nameValuePairs.add(new BasicNameValuePair("username_room", args[0]));
            nameValuePairs.add(new BasicNameValuePair("id_rooms_tab", args[1]));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse httpResponse = httpclient.execute(httpPost);
            inputStream = httpResponse.getEntity().getContent();
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "error";

            StatusLine statusLine = httpResponse.getStatusLine();
            if (statusLine.getStatusCode() != 200)
                result = "error";

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String getVideoListInput(String... args) {
        InputStream inputStream;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(args[3] + "?keywords=" + args[2]);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                    1);
            nameValuePairs.add(new BasicNameValuePair("username_room", args[0]));
            nameValuePairs.add(new BasicNameValuePair("id_rooms_tab", args[1]));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse httpResponse = httpclient.execute(httpPost);
            inputStream = httpResponse.getEntity().getContent();
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "error";

            StatusLine statusLine = httpResponse.getStatusLine();
            if (statusLine.getStatusCode() != 200)
                result = "error";

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    protected void resolveVideos(Bundle savedInstanceState) {
        id_rooms_tab = getIntent().getStringExtra(EXTRA_ID_ROOMS_TAB);
        url_tembak = getIntent().getStringExtra(EXTRA_URL_TEMBAK);
        username = getIntent().getStringExtra(EXTRA_USERNAME);
        color = getIntent().getStringExtra(EXTRA_COLOR);
        colorText = getIntent().getStringExtra(EXTRA_COLOR_TEXT);
        if (id_rooms_tab == null && savedInstanceState != null) {
            id_rooms_tab = savedInstanceState.getString(EXTRA_ID_ROOMS_TAB);
        }
        if (url_tembak == null && savedInstanceState != null) {
            url_tembak = savedInstanceState.getString(EXTRA_URL_TEMBAK);
        }
        if (username == null && savedInstanceState != null) {
            username = savedInstanceState.getString(EXTRA_USERNAME);
        }
        if (color == null && savedInstanceState != null) {
            color = savedInstanceState.getString(EXTRA_COLOR);
        }
        if (colorText == null && savedInstanceState != null) {
            colorText = savedInstanceState.getString(EXTRA_COLOR_TEXT);
        }

        if (id_rooms_tab == null || url_tembak == null || username == null || color == null || colorText == null) {
            finish();
            return;
        }
    }
}
