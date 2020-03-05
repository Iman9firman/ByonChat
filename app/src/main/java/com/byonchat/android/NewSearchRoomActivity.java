package com.byonchat.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.TagTrending.Tag;
import com.byonchat.android.TagTrending.TagClass;
import com.byonchat.android.TagTrending.TagView;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.createMeme.FilteringImage;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.list.ItemListTrending;
import com.byonchat.android.list.SearchroomAdapter;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.ContactBot;
import com.byonchat.android.provider.IntervalDB;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.RoomsDB;
import com.byonchat.android.suggest.SuggestionAdapter;
import com.byonchat.android.suggest.SuggestionAdapterHashTag;
import com.byonchat.android.suggest.SuggestionAdapterTag;
import com.byonchat.android.ui.activity.MainActivityNew;
import com.byonchat.android.ui.adapter.OnItemClickListener;
import com.byonchat.android.utils.GPSTracker;
import com.byonchat.android.utils.HttpHelper;
import com.byonchat.android.utils.JsonUtil;
import com.byonchat.android.utils.RequestKeyTask;
import com.byonchat.android.utils.TaskCompleted;
import com.byonchat.android.utils.UtilsPD;
import com.byonchat.android.utils.ValidationsKey;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static com.byonchat.android.utils.Utility.reportCatch;

public class NewSearchRoomActivity extends AppCompatActivity {

    Context context;
    Toolbar toolbar;
    TextView emptyList;
    private ListView lv;
    IntervalDB intervalDB;
    private int color = 0;
    private Contact contact;
    SearchView mSearchView;
    private RoomsDB roomsDB;
    EditText searchEditText;
    private TagView tagGroup;
    private FrameLayout vToolbarContainer;
    SearchroomAdapter adapter;
    private boolean show = false;
    private ImageView btnRefresh;
    private ArrayList<TagClass> tagList;
    RequestSearchResult requestSearchResult;
    Activity aa = NewSearchRoomActivity.this;
    private String colorAttachment = "#005982";
    ArrayList<ItemListTrending> itemListTrending;
    private MessengerDatabaseHelper messengerHelper;
    LinearLayout linlaHeaderProgress, linearTrending;
    ArrayList<ContactBot> botArrayListist = new ArrayList<ContactBot>();
    private static String URL_GET_SEARCH = "https://" + MessengerConnectionService.HTTP_SERVER + "/room/yangmana.php";
    public String tipe = "", ttipe, skin, latitude = "", longitude = "", searchURL, encodedSearch, trendingURL;
    public static String searchText = "";
    String id = "", name = "", desc = "", realname = "", link = "", type = "", tipe_room = "", targetURL = "";
    private static ArrayList<ContactBot> catArray = new ArrayList<ContactBot>();
    LaporSelectedRoom laporSelectedRoom;
    protected String mColor, mColorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_search_room);
        try {
            if (context == null) {
                context = NewSearchRoomActivity.this;
            }

            if (messengerHelper == null) {
                messengerHelper = MessengerDatabaseHelper.getInstance(context);
            }


            if (roomsDB == null) {
                roomsDB = new RoomsDB(this);
            }

            contact = messengerHelper.getMyContact();

            tipe = getIntent().getStringExtra("search");
            if (tipe.equalsIgnoreCase("all")) {
                tipe = "Search All";
                ttipe = "0";
            } else if (tipe.equalsIgnoreCase("brand")) {
                tipe = "Search Brands";
                ttipe = "3";
            } else if (tipe.equalsIgnoreCase("celeb")) {
                tipe = "Search Celebs";
                ttipe = "2";
            } else if (tipe.equalsIgnoreCase("person")) {
                tipe = "Search Persons";
                ttipe = "1";
            }


            if (getIntent().getStringExtra("addHonda") != null) {
                String id = "1";
                String name = "1_248162126admin";
                String desc = "Honda IKB PRADANA";
                String realname = "HONDA S-TEAM";
                String link = "https://bb.byonchat.com/mediafiles/profile_photo_special_rooms/icon_honda.png";
                String type = "2";

                roomsDB.open();
                boolean isActived = true;
                ContactBot contactBot = new ContactBot("1", name, desc, realname, link, type, isActived, "{\"type\":\"2\",\"tipe_room\":\"3\",\"path\":\"https:\\/\\/hondaikb.byonchat.com\"}");
                roomsDB.insertRooms(contactBot);
                roomsDB.close();
                finish();
            }


            mColor = getIntent().getStringExtra(Constants.EXTRA_COLOR);
            mColorText = getIntent().getStringExtra(Constants.EXTRA_COLORTEXT);

            vToolbarContainer = (FrameLayout) findViewById(R.id.toolbar_container);
            toolbar = (Toolbar) findViewById(R.id.toolbar);

            resolveToolbar();

            IntervalDB db = new IntervalDB(this);
        /*db.open();
        Cursor cursorSelect = db.getSingleContact(4);
        if (cursorSelect.getCount() > 0) {
            String skin = cursorSelect.getString(cursorSelect.getColumnIndexOrThrow(IntervalDB.COL_TIME));
            Skin skins = null;
            Cursor c = db.getCountSkin();
            if (c.getCount() > 0) {
                skins = db.retriveSkinDetails(skin);
                colorAttachment = skins.getColor();
                initBackground(Color.parseColor(colorAttachment));
            }
            c.close();
        }
        cursorSelect.close();
        db.close();*/

            GPSTracker gps = new GPSTracker(context);
            if (gps.canGetLocation()) {
                if (((gps.getLongitude() == 0.0) && (gps.getLatitude() == 0.0))) {
                    db.open();
                    Cursor cursorSelect1 = db.getSingleContact(20);
                    if (cursorSelect1.getCount() > 0) {
                        skin = cursorSelect1.getString(cursorSelect1.getColumnIndexOrThrow(IntervalDB.COL_TIME));
                    }
                    cursorSelect1.close();
                    db.close();
                } else {
                    latitude = gps.getLatitude() + "";
                    longitude = gps.getLongitude() + "";
                    db.open();
                    db.updateContact(20, gps.getLatitude() + "|" + gps.getLongitude());
                    db.close();
                }
            }

            lv = (ListView) findViewById(R.id.gonelist);
            linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
            linearTrending = (LinearLayout) findViewById(R.id.linearTrending);
            emptyList = (TextView) findViewById(R.id.emptyList);
            btnRefresh = (ImageView) findViewById(R.id.btn_refresh);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    showPopup(view, position);
//                ContactBot item = adapter.newList().get(position);
                /*try {
                    JSONObject jObj = new JSONObject(botArrayListist.get(position).getType());
                    type = jObj.getString("type");
                    tipe_room = jObj.getString("tipe_room");
                    targetURL = jObj.getString("path");

                    if (Integer.valueOf(tipe_room) == 1) {
                        Intent intent = new Intent(aa, PersonalRoomActivity.class);
                        intent.putExtra(PersonalRoomActivity.EXTRA_ID, botArrayListist.get(position).getName());
                        intent.putExtra(PersonalRoomActivity.EXTRA_NAME, botArrayListist.get(position).getRealname());
                        startActivity(intent);
                    } else if (Integer.valueOf(tipe_room) == 2) {
                        Intent intent = new Intent(aa, PersonalRoomActivity.class);
                        intent.putExtra(PersonalRoomActivity.EXTRA_ID, botArrayListist.get(position).getName());
                        intent.putExtra(PersonalRoomActivity.EXTRA_NAME, botArrayListist.get(position).getRealname());
                        startActivity(intent);
                    } else if (Integer.valueOf(tipe_room) == 3) {

                        if (Message.isJSONValid(botArrayListist.get(position).getDesc())) {
                            JSONObject jObject = null;
                            try {
                                jObject = new JSONObject(botArrayListist.get(position).getDesc());
                                String desc = jObject.getString("desc");
                                String classs = jObject.getString("apps");
                                final String url = jObject.getString("url");

                                boolean isAppInstalled = appInstalledOrNot(classs);

                                if (isAppInstalled) {
                                    Intent LaunchIntent = getPackageManager()
                                            .getLaunchIntentForPackage(classs);
                                    startActivity(LaunchIntent);
                                } else {
                                    AlertDialog.Builder alertbox = new AlertDialog.Builder(NewSearchRoomActivity.this);
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
                        } else {
                            Intent intent = new Intent(aa, ByonChatMainRoomActivity.class);
                            intent.putExtra(ConversationActivity.KEY_JABBER_ID, botArrayListist.get(position).getName());
                            intent.putExtra(ConversationActivity.KEY_TITLE, targetURL);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }

                    }
                } catch (Exception e) {
                }*/
                }
            });

            btnRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new getTrending().execute(trendingURL);
                    refreshList();
                }
            });

            tagGroup = (TagView) findViewById(R.id.tag_group);


            roomsDB.open();
            itemListTrending = roomsDB.retrieveTrending(ttipe);
            roomsDB.close();

            trendingURL = "https://" + MessengerConnectionService.HTTP_SERVER + "/room/trending.php?type=" + ttipe;

            if (itemListTrending.size() > 0) {
                refreshList();
            } else {
                new getTrending().execute(trendingURL);
                refreshList();
            }

            tagGroup.setOnTagClickListener(new TagView.OnTagClickListener() {
                @Override
                public void onTagClick(Tag tag, int position) {
                    searchText = tag.text;
                    searchEditText.setText(tag.text);
                    requestKey();
                    mSearchView.clearFocus();
                }
            });

            tagGroup.setOnTagDeleteListener(new TagView.OnTagDeleteListener() {

                @Override
                public void onTagDeleted(final TagView view, final Tag tag, final int position) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(NewSearchRoomActivity.this);
                    builder.setMessage("\"" + tag.text + "\" will be delete. Are you sure?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            view.remove(position);
                            Toast.makeText(NewSearchRoomActivity.this, "\"" + tag.text + "\" deleted", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("No", null);
                    builder.show();
                }
            });
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    protected void resolveToolbar() {
        try {
            if (mColor.equalsIgnoreCase("FFFFFF") && mColorText.equalsIgnoreCase("000000")) {
                View lytToolbarDark = getLayoutInflater().inflate(R.layout.toolbar_dark, vToolbarContainer);
                Toolbar toolbarDark = lytToolbarDark.findViewById(R.id.toolbar_dark);
                vToolbarContainer.removeView(toolbar);
                setSupportActionBar(toolbarDark);
            } else {
                setSupportActionBar(toolbar);
                getSupportActionBar().setTitle("");

                FilteringImage.SystemBarBackground(getWindow(), Color.parseColor("#" + mColor));
                toolbar.setBackgroundColor(Color.parseColor("#" + mColor));
                toolbar.setTitleTextColor(Color.parseColor("#" + mColorText));
            }
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_35dp);
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    public void openWebPage(String url) {
        try {
            Uri webpage = Uri.parse(url);
            Intent myIntent = new Intent(Intent.ACTION_VIEW, webpage);
            startActivity(myIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application can handle this request. Please install a web browser or check your URL.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    private void setTags(CharSequence cs) {
        try {
            if (cs.toString().equals("")) {
                tagGroup.addTags(new ArrayList<Tag>());
                return;
            }
            String text = cs.toString();
            ArrayList<Tag> tags = new ArrayList<>();
            Tag tag;
            for (int i = 0; i < itemListTrending.size(); i++) {
                if (itemListTrending.get(i).getName().toLowerCase().startsWith(text.toLowerCase())) {
                    tag = new Tag(itemListTrending.get(i).getName().substring(1));
                    tag.radius = 10f;
                    tag.layoutColor = Color.parseColor(itemListTrending.get(i).getColor());
                    tags.add(tag);
                }
            }
            tagGroup.addTags(tags);
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    public void refreshList() {
        roomsDB.open();
        itemListTrending = roomsDB.retrieveTrending(ttipe);
        roomsDB.close();
        setTags("#");
    }

    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.activity_slide_in_down, R.anim.activity_slide_out_down);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search_rooms, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setTitle(tipe);
        try {
            MenuItemCompat.setOnActionExpandListener(searchItem,
                    new MenuItemCompat.OnActionExpandListener() {
                        @Override
                        public boolean onMenuItemActionExpand(MenuItem menuItem) {
                            return true;
                        }

                        @Override
                        public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                            finish();
                            return true;
                        }
                    });

            final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            searchItem.expandActionView();
            mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchEditText = (EditText) mSearchView.findViewById(androidx.appcompat.R.id.search_src_text);
            searchEditText.setTextColor(Color.parseColor("#" + mColorText));
            searchEditText.setHintTextColor(Color.parseColor("#" + mColorText));
            searchEditText.setHint(tipe);
            searchEditText.setFocusable(true);

            ImageView searchImageView = (ImageView) mSearchView.findViewById(androidx.appcompat.R.id.search_close_btn);
            Drawable mDrawable = context.getResources().getDrawable(R.drawable.ic_byonchat_close);
            mDrawable.setColorFilter(new
                    PorterDuffColorFilter(Color.parseColor("#" + mColorText), PorterDuff.Mode.SRC_ATOP));
            searchImageView.setImageDrawable(mDrawable);

//        ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#" + mColorText));
//        searchEditText.setBackgroundTintList(colorStateList);
            final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) mSearchView.findViewById(androidx.appcompat.R.id.search_src_text);
            searchAutoComplete.setThreshold(0);
            searchAutoComplete.setAdapter(new SuggestionAdapterHashTag(aa));
            searchAutoComplete.setDropDownBackgroundDrawable(getResources().getDrawable(R.drawable.abc_popup_background_mtrl_mult));
            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            final int textViewID = mSearchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            final AutoCompleteTextView searchTextView = (AutoCompleteTextView) mSearchView.findViewById(textViewID);
            try {
                Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
                f.setAccessible(true);
                f.set(searchTextView, getResources().getDrawable(R.drawable.custom_cursor));
            } catch (Exception e) {

            }

            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String queryText) {
                    try {
                        if (queryText.startsWith("#")) {
                            encodedSearch = URLEncoder.encode(queryText.substring(1), "UTF-8");
                            searchText = queryText.substring(1);
                        } else {
                            encodedSearch = URLEncoder.encode(queryText, "UTF-8");
                            searchText = queryText;
                        }

                        requestKey();
                        mSearchView.clearFocus();
                    } catch (Exception e) {
                        Toast.makeText(aa, "Fail to fetch data", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (newText.equalsIgnoreCase("")) {
                        searchText = newText;
                        searchAutoComplete.setAdapter(new SuggestionAdapterTag(aa, newText));

                        if (linlaHeaderProgress.getVisibility() == View.GONE) {
                            linearTrending.setVisibility(View.VISIBLE);
                            emptyList.setVisibility(View.GONE);
                            lv.setVisibility(View.GONE);
                        }
                    } else {
                        if (mSearchView.getQuery().length() >= 3) {
                            searchText = newText;
                            searchAutoComplete.setAdapter(new SuggestionAdapter(aa, newText));
                        }
                    }
                    return true;
                }

            });

            searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                    // TODO Auto-generated method stub;
                    String searchString = (String) parent.getItemAtPosition(position);
                    searchAutoComplete.setText("" + searchString);
                    try {
                        if (searchString.startsWith("#")) {
                            encodedSearch = URLEncoder.encode(searchString.substring(1), "UTF-8");
                        } else {
                            encodedSearch = URLEncoder.encode(searchString, "UTF-8");
                        }

                        requestKey();
                        mSearchView.clearFocus();
                    } catch (Exception e) {
                        Toast.makeText(aa, "Fail to fetch data", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
        return true;
    }

    private void initBackground(int color) {
        try {
            Bitmap back_default = FilteringImage.headerColor(getWindow(), this, color);
            Drawable back_draw_default = new BitmapDrawable(getResources(), back_default);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                toolbar.setBackground(back_draw_default);
            } else {
                toolbar.setBackgroundDrawable(back_draw_default);
            }
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    private class GetData extends AsyncTask<String, Void, String> {
        String searchh = "";

        protected void onPreExecute() {
            super.onPreExecute();
            linlaHeaderProgress.setVisibility(View.VISIBLE);
            emptyList.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... dataURL) {

            StringBuilder dataFeedBuilder = new StringBuilder();
            for (String searchURL : dataURL) {
                HttpClient dataClient = new DefaultHttpClient();
                try {
                    String fileSend[] = searchURL.split("keyword=");
                    searchh = fileSend[1];
                    HttpGet dataGet = new HttpGet(searchURL);
                    HttpResponse dataResponse = dataClient.execute(dataGet);
                    StatusLine searchStatus = dataResponse.getStatusLine();
                    if (searchStatus.getStatusCode() == 200) {
                        HttpEntity dataEntity = dataResponse.getEntity();
                        InputStream dataContent = dataEntity.getContent();
                        InputStreamReader dataInput = new InputStreamReader(dataContent);
                        BufferedReader dataReader = new BufferedReader(dataInput);
                        String lineIn;
                        while ((lineIn = dataReader.readLine()) != null) {
                            dataFeedBuilder.append(lineIn);
                        }
                    } else {
                        aa.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(aa, "Please try again later", Toast.LENGTH_LONG).show();
                            }

                        });
                    }
                } catch (Exception e) {
                    aa.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(aa, "Please try again later", Toast.LENGTH_LONG).show();
                        }

                    });
                }
            }
            return dataFeedBuilder.toString();
        }

        protected void onPostExecute(String result) {
            try {
                linlaHeaderProgress.setVisibility(View.GONE);
                StringBuilder dataResultBuilder = new StringBuilder();
                try {
                    lv.setVisibility(View.VISIBLE);
                    botArrayListist = new ArrayList<ContactBot>();

                    JSONObject resultObject = new JSONObject(result);
                    JSONArray feedArray = resultObject.getJSONArray("Data");
                    for (int i = 0; i < feedArray.length(); i++) {
                        JSONObject feedObj = (JSONObject) feedArray.get(i);
                        ContactBot aa5 = new ContactBot(String.valueOf(i), "", feedObj.getString("deskripsi"), feedObj.getString("room_name"), feedObj.getString("image_location"));
                        botArrayListist.add(aa5);
                    }
                    adapter = new SearchroomAdapter(getApplication(), botArrayListist, show, searchh, contact, messengerHelper, new OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            showPopup(view, position);
                        }
                    });
                    lv.setAdapter(adapter);
                } catch (Exception e) {
                    lv.setVisibility(View.GONE);
                    emptyList.setVisibility(View.VISIBLE);
                    emptyList.setText("No results found for \"" + searchh + "\"");
                }
            } catch (Exception e) {
                reportCatch(e.getLocalizedMessage());
            }
        }
    }

    private void requestKey() {
        try {
            linlaHeaderProgress.setVisibility(View.VISIBLE);
            linearTrending.setVisibility(View.GONE);
            emptyList.setVisibility(View.GONE);
            lv.setVisibility(View.GONE);

            RequestKeyTask testAsyncTask = new RequestKeyTask(new TaskCompleted() {
                @Override
                public void onTaskDone(String key) {
                    if (key.equalsIgnoreCase("null")) {
                        linlaHeaderProgress.setVisibility(View.GONE);
                        linearTrending.setVisibility(View.GONE);
                        emptyList.setVisibility(View.GONE);
                        lv.setVisibility(View.GONE);

                        Toast.makeText(context, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                    } else {
                        if (NetworkInternetConnectionStatus.getInstance(context).isOnline(context)) {
                            requestSearchResult = new RequestSearchResult(context);
                            requestSearchResult.execute(key);
                        } else {
                            linlaHeaderProgress.setVisibility(View.GONE);
                            lv.setVisibility(View.GONE);
                            linearTrending.setVisibility(View.GONE);
                            emptyList.setVisibility(View.VISIBLE);
                            emptyList.setText("No internet connection.");
                        }
                    }
                }
            }, aa);

            testAsyncTask.execute();
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }


    class RequestSearchResult extends AsyncTask<String, Void, String> {

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();

        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content = null;
        private boolean error = false;
        private Context mContext;

        public RequestSearchResult(Context context) {
            this.mContext = context;

        }


        @Override
        protected void onPreExecute() {
            linlaHeaderProgress.setVisibility(View.VISIBLE);
            linearTrending.setVisibility(View.GONE);
            emptyList.setVisibility(View.GONE);
        }

        protected String doInBackground(String... key) {
            try {
                HttpClient httpClient = HttpHelper
                        .createHttpClient(mContext);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);

                nameValuePairs.add(new BasicNameValuePair("username", contact.getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("key", key[0]));
                nameValuePairs.add(new BasicNameValuePair("cari", searchText));
                nameValuePairs.add(new BasicNameValuePair("tipe", ttipe));
                nameValuePairs.add(new BasicNameValuePair("long", longitude));
                nameValuePairs.add(new BasicNameValuePair("lat", latitude));

                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                HttpPost post = new HttpPost(URL_GET_SEARCH);
                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                //Response from the Http Request
                response = httpclient.execute(post);
                StatusLine statusLine = response.getStatusLine();
                //Check the Http Request for success
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
            try {
                if (NetworkInternetConnectionStatus.getInstance(context).isOnline(context)) {
                    linlaHeaderProgress.setVisibility(View.GONE);
                    linearTrending.setVisibility(View.GONE);
                    if (error) {
                        if (content.contains("invalid_key")) {
                            if (NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)) {
                                String key = new ValidationsKey().getInstance(mContext).key(true);
                                if (key.equalsIgnoreCase("null")) {
                                    Toast.makeText(mContext, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                                } else {
                                    requestSearchResult = new RequestSearchResult(context);
                                    requestSearchResult.execute(key);
                                }
                            } else {
                                Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                        }
                    } else {
                        try {
                            lv.setVisibility(View.VISIBLE);
                            botArrayListist = new ArrayList<ContactBot>();

                            if (content.startsWith("[")) {
                                JSONArray result = new JSONArray(content);
                                Log.w("juru", content);
                                for (int i = 0; i < result.length(); i++) {
                                    JSONObject obj = result.getJSONObject(i);
                                    String username = obj.getString("username");
                                    String description = obj.getString("description");
                                    String nama_display = obj.getString("nama_display");
                                    String classs = obj.getString("class");
                                    String url = obj.getString("url");
                                    String foto = obj.getString("foto");
                                    String tipe_room = obj.getString("tipe_room");
                                    String targetUrl = "https://" + MessengerConnectionService.HTTP_SERVER;

                                    if (obj.has("target_url")) {
                                        targetUrl = obj.getString("target_url");
                                    }

                                    Log.w("targetUrl", targetUrl);

                                    ContactBot aa = null;
                                    if (classs.equalsIgnoreCase("")) {
                                        aa = new ContactBot(String.valueOf(i + 1), username, description, nama_display, foto, JsonUtil.toJsonRoom("2", tipe_room, targetUrl));
                                    } else {
                                        aa = new ContactBot(String.valueOf(i + 1), username, JsonUtil.toJsonDescription(description, classs, url), nama_display, foto, JsonUtil.toJsonRoom("2", tipe_room, targetUrl));
                                    }

                                    botArrayListist.add(aa);
                                    if (isCancelled()) break;
                                }
                                adapter = new SearchroomAdapter(NewSearchRoomActivity.this, botArrayListist, show, searchText, contact, messengerHelper, new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        showPopup(view, position);
                                    }
                                });
                                adapter.notifyDataSetChanged();
                                lv.setAdapter(adapter);
                            } else {
                                lv.setVisibility(View.GONE);
                                emptyList.setVisibility(View.VISIBLE);
                                emptyList.setText("No results found for \"" + searchText + "\"");
                            }
                        } catch (Exception e) {
                            lv.setVisibility(View.GONE);
                            emptyList.setVisibility(View.VISIBLE);
                            emptyList.setText("No results found for \"" + searchText + "\"");
                        }
                    }
                } else {
                    lv.setVisibility(View.GONE);
                    linearTrending.setVisibility(View.GONE);
                    emptyList.setVisibility(View.VISIBLE);
                    emptyList.setText("No internet connection.");
                }
            } catch (Exception e) {
                reportCatch(e.getLocalizedMessage());
            }
        }
    }

    private class getTrending extends AsyncTask<String, Void, String> {
        String searchh = "";

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... dataURL) {

            StringBuilder dataFeedBuilder = new StringBuilder();
            for (String trendingURL : dataURL) {
                HttpClient dataClient = new DefaultHttpClient();
                try {
                    String fileSend[] = trendingURL.split("type=");
                    searchh = fileSend[1];
                    HttpGet dataGet = new HttpGet(trendingURL);
                    HttpResponse dataResponse = dataClient.execute(dataGet);
                    StatusLine searchStatus = dataResponse.getStatusLine();
                    if (searchStatus.getStatusCode() == 200) {
                        HttpEntity dataEntity = dataResponse.getEntity();
                        InputStream dataContent = dataEntity.getContent();
                        InputStreamReader dataInput = new InputStreamReader(dataContent);
                        BufferedReader dataReader = new BufferedReader(dataInput);
                        String lineIn;
                        while ((lineIn = dataReader.readLine()) != null) {
                            dataFeedBuilder.append(lineIn);
                        }
                    } else {
                        aa.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(aa, "Please try again later", Toast.LENGTH_LONG).show();
                            }

                        });
                    }
                } catch (Exception e) {
                    aa.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(aa, "Please try again later", Toast.LENGTH_LONG).show();
                        }

                    });
                }
            }
            return dataFeedBuilder.toString();
        }

        protected void onPostExecute(String result) {

            StringBuilder dataResultBuilder = new StringBuilder();
            try {
                roomsDB.open();
                itemListTrending = roomsDB.retrieveTrending(ttipe);
                roomsDB.close();

                if (itemListTrending.size() > 0) {
                    roomsDB.open();
                    roomsDB.deleteTrending(ttipe);
                    roomsDB.close();
                }

                JSONObject resultObject = new JSONObject(result);
                JSONArray feedArray = resultObject.getJSONArray("keyword");
                if (feedArray.length() >= 10) {
                    for (int i = 0; i < 10; i++) {
                        String name = feedArray.getString(i);
                        ItemListTrending trend = new ItemListTrending(String.valueOf(i), name, ttipe);
                        roomsDB.open();
                        roomsDB.insertTrending(trend);
                        roomsDB.close();
                    }
                } else {
                    for (int i = 0; i < feedArray.length(); i++) {
                        String name = feedArray.getString(i);
                        ItemListTrending trend = new ItemListTrending(String.valueOf(i), name, ttipe);
                        roomsDB.open();
                        roomsDB.insertTrending(trend);
                        roomsDB.close();
                    }
                }

            } catch (Exception e) {
            }
        }
    }

    private void showPopup(View view, final int position) {
        // pass the imageview id
        View menuItemView = view.findViewById(R.id.button_popup);
        final PopupMenu popup = new PopupMenu(NewSearchRoomActivity.this, menuItemView);
        MenuInflater inflate = popup.getMenuInflater();
        try {
            JSONObject jObj = new JSONObject(botArrayListist.get(position).getType());
            type = jObj.getString("type");
            tipe_room = jObj.getString("tipe_room");

            if (Integer.valueOf(tipe_room) == 1) {
                inflate.inflate(R.menu.menu_search_room_personal, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_chat:
                                Intent intent = new Intent(NewSearchRoomActivity.this, ConversationActivity.class);
                                intent.putExtra(ConversationActivity.KEY_JABBER_ID, botArrayListist.get(position).getName());
                                startActivity(intent);
                                break;
                            default:
                                return false;
                        }
                        return false;
                    }
                });

            } else if (Integer.valueOf(tipe_room) == 2) {
                inflate.inflate(R.menu.menu_search_room, popup.getMenu());

                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.action_chat:
                            Intent intent = new Intent(NewSearchRoomActivity.this, ConversationActivity.class);
                            intent.putExtra(ConversationActivity.KEY_JABBER_ID, botArrayListist.get(position).getName());
                            startActivity(intent);
                            break;
                        case R.id.action_addToSelected:
                            name = botArrayListist.get(position).getName();
                            desc = botArrayListist.get(position).getDesc();
                            realname = botArrayListist.get(position).getRealname();
                            link = botArrayListist.get(position).getLink();
                            String targetURL = "";

                            try {
                                JSONObject jObjy = new JSONObject(botArrayListist.get(position).getType());
                                targetURL = jObj.getString("path");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            type = "2";
                            insertToDB(String.valueOf(position + 1), botArrayListist.get(position).getName(), botArrayListist.get(position).getDesc(), botArrayListist.get(position).getRealname(), botArrayListist.get(position).getLink(), botArrayListist.get(position).getType(), "2", targetURL);
                            popup.dismiss();
                            break;
                        default:
                            return false;
                    }
                    return false;
                });
            } else if (Integer.valueOf(tipe_room) == 3) {
                inflate.inflate(R.menu.menu_search_room, popup.getMenu());

                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.action_chat:
                            Intent intent = new Intent(NewSearchRoomActivity.this, ConversationActivity.class);
                            intent.putExtra(ConversationActivity.KEY_JABBER_ID, botArrayListist.get(position).getName());
                            startActivity(intent);
                            break;
                        case R.id.action_addToSelected:
                            name = botArrayListist.get(position).getName();
                            desc = botArrayListist.get(position).getDesc();
                            realname = botArrayListist.get(position).getRealname();
                            link = botArrayListist.get(position).getLink();
                            type = "2";
                            String targetURL = "";

                            try {
                                JSONObject jObjt = new JSONObject(botArrayListist.get(position).getType());
                                targetURL = jObjt.getString("path");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            insertToDB(String.valueOf(position + 1), botArrayListist.get(position).getName(), botArrayListist.get(position).getDesc(), botArrayListist.get(position).getRealname(), botArrayListist.get(position).getLink(), botArrayListist.get(position).getType(), "2", targetURL);
                            popup.dismiss();
                            break;
                        default:
                            return false;
                    }
                    return false;
                });
            }
        }  catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }


        popup.show();
    }

    public void insertToDB(String id, String name, String desc, String realname, String link, String json, String type, String path) {
        try {
            if (roomsDB == null) {
                roomsDB = new RoomsDB(NewSearchRoomActivity.this);
            }

            roomsDB.open();
            catArray = roomsDB.retrieveRoomsByName(name, "2");
            roomsDB.close();
            if (catArray.size() > 0) {
                Toast.makeText(NewSearchRoomActivity.this, realname + " is already added to selected rooms", Toast.LENGTH_SHORT).show();
            } else {
                requestKey(path, json);
            }
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    public void insertToDBbyRequest(String id, String name, String desc, String realname, String link, String json, String type, String path) {
        try {
            if (roomsDB == null) {
                roomsDB = new RoomsDB(NewSearchRoomActivity.this);
            }

            roomsDB.open();
            catArray = roomsDB.retrieveRoomsByName(name, "2");
            roomsDB.close();
            if (catArray.size() > 0) {
                Toast.makeText(NewSearchRoomActivity.this, realname + " is already added to selected rooms", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    private void requestKey(final String path, final String json) {
        try {
            RequestKeyTask testAsyncTask = new RequestKeyTask(new TaskCompleted() {
                @Override
                public void onTaskDone(String key) {
                    if (key.equalsIgnoreCase("null")) {
                        Toast.makeText(NewSearchRoomActivity.this, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                    } else {
                        laporSelectedRoom = new LaporSelectedRoom(NewSearchRoomActivity.this, json);
                        laporSelectedRoom.execute(key, path);
                    }
                }
            }, NewSearchRoomActivity.this);

            testAsyncTask.execute();
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
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
        private ProgressDialog progressDialog;

        String path = "";
        String json = "";

        public LaporSelectedRoom(Context context, String json) {
            this.mContext = context;
            this.json = json;

        }

        @Override
        protected void onPreExecute() {
            if (progressDialog == null) {
                progressDialog = UtilsPD.createProgressDialog(NewSearchRoomActivity.this);
                progressDialog.show();
            }
        }

        protected String doInBackground(String... key) {
            try {
                HttpClient httpClient = HttpHelper
                        .createHttpClient(mContext);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);

                nameValuePairs.add(new BasicNameValuePair("username", messengerHelper.getMyContact().getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("key", key[0]));
                nameValuePairs.add(new BasicNameValuePair("room_id", name));
                nameValuePairs.add(new BasicNameValuePair("aksi", "1"));

                path = key[1];

                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                HttpPost post = new HttpPost(Constants.URLLAPORSELECTED);
                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                //Response from the Http Request
                response = httpclient.execute(post);
                StatusLine statusLine = response.getStatusLine();
                //Check the Http Request for success
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    content = out.toString();


                } else {
                    //Closes the connection.
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
            progressDialog.dismiss();
            try {
                if (error) {
                    if (content.contains("invalid_key")) {
                        if (NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)) {
                            String key = new ValidationsKey().getInstance(mContext).key(true);
                            if (key.equalsIgnoreCase("null")) {
                                Toast.makeText(mContext, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                            } else {
                                laporSelectedRoom = new LaporSelectedRoom(NewSearchRoomActivity.this, json);
                                laporSelectedRoom.execute(key, path);
                            }
                        } else {
                            Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mContext, content, Toast.LENGTH_LONG).show();
                    }
                } else {
                    resolveValidation();

                    roomsDB.open();
                    boolean isActived = true;
                    ContactBot contactBot = new ContactBot("", name, desc, realname, link, type, isActived, json);
                    roomsDB.insertRooms(contactBot);
                    roomsDB.close();

                    if (MainActivityNew.mActivity != null) {
                        MainActivityNew.mActivity.finish();
                    }

                    Toast.makeText(NewSearchRoomActivity.this, realname + " has been added to selected rooms", Toast.LENGTH_SHORT).show();
                    finish();

                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.clear();
                    editor.commit();

                    editor.putString(Constants.EXTRA_SERVICE_PERMISSION, "true");
                    editor.apply();

                    Intent ii = LoadingGetTabRoomActivity.generateIntent(getApplicationContext(), name, path);
                    startActivity(ii);

                }
            } catch (Exception e) {
                reportCatch(e.getLocalizedMessage());
            }
        }
    }

    protected void resolveValidation() {
        /*String protect = "";
        Cursor cur = Byonchat.getBotListDB().getSingleRoom(name);
        if (cur.getCount() > 0) {
            protect = jsonResultType(cur.getString(cur.getColumnIndex(BotListDB.ROOM_COLOR)), "p");
        }

        if (new Validations().getInstance(getApplicationContext()).getValidationLoginById(25) == 1) {
            if (!protect.equalsIgnoreCase("error") && protect.equalsIgnoreCase("1")) {
                if (success == null) {
                    finish();
                    Intent a = new Intent(getApplicationContext(), LoginDinamicRoomActivity.class);
                    a.putExtra(ConversationActivity.KEY_JABBER_ID, username);
                    a.putExtra(ConversationActivity.KEY_TITLE, messengerHelper.getMyContact().getJabberId());
                    startActivity(a);
                }
            } else if (!protect.equalsIgnoreCase("error") && protect.equalsIgnoreCase("2")) {
                if (success == null) {
                    finish();
                    Intent a = new Intent(getApplicationContext(), LoginDinamicFingerPrint.class);
                    a.putExtra(ConversationActivity.KEY_JABBER_ID, username);
                    a.putExtra(ConversationActivity.KEY_TITLE, messengerHelper.getMyContact().getJabberId());
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
        }*/
    }
}
