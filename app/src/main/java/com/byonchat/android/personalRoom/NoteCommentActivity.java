package com.byonchat.android.personalRoom;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity;
import com.byonchat.android.FragmentDinamicRoom.FragmentRoomAbout;
import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.personalRoom.adapter.NoteCommentListAdapter;
import com.byonchat.android.personalRoom.asynctask.ProfileSaveDescription;
import com.byonchat.android.personalRoom.model.CommentModel;
import com.byonchat.android.personalRoom.model.NoteFeedItem;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.utils.UtilsPD;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Lukmanpryg on 5/3/2016.
 */
public class NoteCommentActivity extends AppCompatActivity implements EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {

    private static final String TAG = NoteCommentActivity.class.getSimpleName();
    RecyclerView mRecyclerView;
    private List<CommentModel> feedItems;
    private NoteCommentListAdapter adapter;
    EmojiconEditText mWriteComment;
    Button mButtonSend;
    Toolbar toolbar;
    private String URL_SEND_COMMENT = "https://bb.byonchat.com/bc_voucher_client/webservice/proses/add_note_comment.php";
    private String URL_LIST_NOTE_COMMENT = "https://bb.byonchat.com/bc_voucher_client/webservice/category_tab/list_note_comment.php";
    private String URL_LIST_COMMENT_IN_COMMENT = "https://bb.byonchat.com/bc_voucher_client/webservice/category_tab/list_note_comment_branch.php";

    private String URL_SEND_COMMENT_PERSONAL = "https://" + MessengerConnectionService.HTTP_SERVER2 + "/personal_room/webservice/proses/comment.php";
    private String URL_LIST_NOTE_COMMENT_PERSONAL = "https://" + MessengerConnectionService.HTTP_SERVER2 + "/personal_room/webservice/view/comment.php";
    private String URL_LIST_COMMENT_IN_COMMENT_PERSONAL = "https://" + MessengerConnectionService.HTTP_SERVER2 + "/personal_room/webservice/view/comment_branch.php";
    SwipeRefreshLayout mswipeRefreshLayout;
    String color;
    Boolean personal;
    private LinearLayout emojicons, contentRoot;
    private ImageButton btnMic, btn_add_emoticon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pr_mynote_comment);

        toolbar = (Toolbar) findViewById(R.id.abMain);

        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        contentRoot = (LinearLayout) findViewById(R.id.contentRoot);
        mWriteComment = (EmojiconEditText) findViewById(R.id.writeComment);
        mButtonSend = (Button) findViewById(R.id.btnSend);
        mswipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        toolbar.setTitleTextColor(Color.WHITE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        personal = getIntent().getExtras().getBoolean("flag");

        if (!personal) {
            color = getIntent().getStringExtra("color");
            if (color != null) {
                toolbar.setBackgroundColor(Color.parseColor("#" + color));
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.BLACK);
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(NoteCommentActivity.this));
        feedItems = new ArrayList<CommentModel>();
        adapter = new NoteCommentListAdapter(NoteCommentActivity.this, feedItems);
        mRecyclerView.setAdapter(adapter);

        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateComment()) {
                    registerUser();
                    mWriteComment.setText(null);
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
        });

        mswipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });

        emojicons = (LinearLayout) findViewById(R.id.emojiconsLayout);
        btn_add_emoticon = (ImageButton) findViewById(R.id.btn_add_emoticon);

        btn_add_emoticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emojicons.getVisibility() == View.GONE) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                    Animation animFade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_slide_in_bottom);
                    emojicons.setVisibility(View.VISIBLE);
                    emojicons.startAnimation(animFade);

                    mWriteComment.setFocusable(false);
                } else {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    mWriteComment.setFocusableInTouchMode(true);
                    mWriteComment.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(mWriteComment, InputMethodManager.SHOW_IMPLICIT);
                    emojicons.setVisibility(View.GONE);
                }
            }
        });


        mWriteComment.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                mWriteComment.setFocusableInTouchMode(true);
                mWriteComment.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mWriteComment, InputMethodManager.SHOW_IMPLICIT);
                if (emojicons.getVisibility() == View.VISIBLE) {
                    emojicons.setVisibility(View.GONE);
                }

                return false;
            }
        });

        mWriteComment.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    scrollListConversationToBottom();
                }
            }
        });

    }

    private void scrollListConversationToBottom() {
        mRecyclerView.scrollToPosition(feedItems.size() - 1);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(mWriteComment);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshItems();
    }

    void refreshItems() {
        if (adapter.getItemCount() > 0) {
            feedItems.clear();
            adapter.notifyDataSetChanged();
        }
        if (!personal) {
            if (getIntent().getExtras().containsKey("id_comment")) {
                String userid = getIntent().getStringExtra("userid");
                String id_note = getIntent().getStringExtra("id_note");
                String id_comment = getIntent().getStringExtra("id_comment");
                String bc_user = getIntent().getStringExtra("bc_user");
                String idRoomTab = "";
                if (getIntent().getExtras().containsKey("id_room_tab")) {
                    idRoomTab = getIntent().getStringExtra("id_room_tab");
                }
                getListNotesCommentinComment(userid, id_note, id_comment, bc_user, idRoomTab);
            } else {
                String userid = getIntent().getStringExtra("userid");
                String id_note = getIntent().getStringExtra("id_note");
                String bc_user = getIntent().getStringExtra("bc_user");
                String idRoomTab = "";
                if (getIntent().getExtras().containsKey("id_room_tab")) {
                    idRoomTab = getIntent().getStringExtra("id_room_tab");
                }
                getListNotesComment(userid, idRoomTab, id_note, bc_user, URL_LIST_NOTE_COMMENT);
            }
        } else {
            if (getIntent().getExtras().containsKey("id_comment")) {
                String userid = getIntent().getStringExtra("userid");
                String id_note = getIntent().getStringExtra("id_note");
                String id_comment = getIntent().getStringExtra("id_comment");
                String bc_user = getIntent().getStringExtra("bc_user");
                getListNotesCommentinCommentPersonal(userid, id_note, id_comment, bc_user);
            } else {
                String userid = getIntent().getStringExtra("userid");
                String id_note = getIntent().getStringExtra("id_note");
                String bc_user = getIntent().getStringExtra("bc_user");
                getListNotesCommentPersonal(userid, id_note, bc_user);
            }
        }
        onItemsLoadComplete();
    }

    void onItemsLoadComplete() {
        mswipeRefreshLayout.setRefreshing(false);
        contentRoot.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private void registerUser() {
        String textComment = mWriteComment.getText().toString().trim().toLowerCase();
        if (!personal) {
            if (getIntent().getExtras().containsKey("id_comment")) {
                String userid = getIntent().getStringExtra("userid");
                String id_note = getIntent().getStringExtra("id_note");
                String id_comment = getIntent().getStringExtra("id_comment");
                String bc_user = getIntent().getStringExtra("bc_user");
                String idRoomTab = "";
                if (getIntent().getExtras().containsKey("id_room_tab")) {
                    idRoomTab = getIntent().getStringExtra("id_room_tab");
                }
                sendCommentTree(userid, textComment, id_note, id_comment, bc_user, idRoomTab);
            } else {
                String userid = getIntent().getStringExtra("userid");
                String id_note = getIntent().getStringExtra("id_note");
                String bc_user = getIntent().getStringExtra("bc_user");
                String idRoomTab = "";
                if (getIntent().getExtras().containsKey("id_room_tab")) {
                    idRoomTab = getIntent().getStringExtra("id_room_tab");
                }
                sendCommentTest(userid, textComment, id_note, bc_user, idRoomTab);
            }
        } else {
            if (getIntent().getExtras().containsKey("id_comment")) {
                String userid = getIntent().getStringExtra("userid");
                String id_note = getIntent().getStringExtra("id_note");
                String id_comment = getIntent().getStringExtra("id_comment");
                String bc_user = getIntent().getStringExtra("bc_user");
                sendCommentTreePersonal(userid, textComment, id_note, id_comment, bc_user);
            } else {
                String userid = getIntent().getStringExtra("userid");
                String id_note = getIntent().getStringExtra("id_note");
                String bc_user = getIntent().getStringExtra("bc_user");
                sendCommentTestPersonal(userid, textComment, id_note, bc_user);
            }
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean validateComment() {
        if (TextUtils.isEmpty(mWriteComment.getText())) {
            mButtonSend.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_error));
            return false;
        }
        return true;
    }

    private void sendCommentTest(final String userid, String textComment, final String id_note, final String bc_user, final String idRoomTab) {
        class ambilGambar extends AsyncTask<String, Void, String> {
            ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
            String result = "";
            private ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                if (progressDialog == null) {
                    progressDialog = UtilsPD.createProgressDialog(NoteCommentActivity.this);
                    progressDialog.show();
                }
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("username_room", params[0]);
                data.put("id_rooms_tab", params[1]);
                data.put("id_note", params[2]);
                data.put("bc_user", params[3]);
                data.put("comment_content", params[4]);

                String result = profileSaveDescription.sendPostRequest(URL_SEND_COMMENT, data);
                return result;
            }

            protected void onPostExecute(String s) {
                getListNotesCommentSubmit(userid, id_note, bc_user, idRoomTab);
                progressDialog.dismiss();
                if (!s.equals("1")) {
                    Toast.makeText(NoteCommentActivity.this, "Check your internet problem.", Toast.LENGTH_LONG).show();
                }
                super.onPostExecute(s);
            }
        }
        ambilGambar ru = new ambilGambar();
        ru.execute(userid, idRoomTab, id_note, bc_user, textComment);
    }

    private void getListNotesCommentSubmit(String userid, String id_note, final String bc_user, final String idRoomTab) {
        class ambilGambar extends AsyncTask<String, Void, String> {
            ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
            String result = "";

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("username_room", params[0]);
                data.put("id_rooms_tab", params[1]);
                data.put("id_note", params[2]);
                data.put("bc_user", params[3]);
                String result = profileSaveDescription.sendPostRequest(URL_LIST_NOTE_COMMENT, data);
                return result;
            }

            protected void onPostExecute(String s) {
                JSONArray dataJsonArr = null;
                String data = "";

                if (s.equals(null)) {
                    Toast.makeText(NoteCommentActivity.this, "Internet Problem.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        feedItems.clear();
                        JSONObject json = new JSONObject(s);
                        String id_note = json.getString("id_note");

                        dataJsonArr = json.getJSONArray("data");
                        for (int i = 0; i < dataJsonArr.length(); i++) {

                            JSONObject c = dataJsonArr.getJSONObject(i);

                            String id_comment = c.getString("id_comment");
                            String uid = c.getString("userid");
                            String profile_name = c.getString("profile_name");
                            String profile_photo = c.getString("profile_photo");
                            String amount_of_like = c.getString("amount_of_like");
                            String amount_of_dislike = c.getString("amount_of_dislike");
                            String amount_of_comment = c.getString("amount_of_comment");
                            String content_comment = c.getString("content_comment");
                            String tgl_comment = c.getString("tgl_comment");
                            String parent_id = c.getString("parent_id");
                            String userLike = c.getString("user_like");
                            String userDislike = c.getString("user_dislike");

                            CommentModel item = new CommentModel();
                            item.setIdRoomTab(idRoomTab);
                            item.setHeaderColor(color);

                            item.setId_note(id_note);
                            item.setId_comment(id_comment);
                            item.setMyuserid(bc_user);
                            item.setIdRoomTab(idRoomTab);
                            item.setUserid(uid);
                            item.setProfileName(profile_name);
                            String pPhoto = c.isNull("profile_photo") ? null : c.getString("profile_photo");
                            item.setProfile_photo(pPhoto);
                            item.setJumlahLove(amount_of_like);
                            item.setJumlahNix(amount_of_dislike);
                            item.setJumlahComment(amount_of_comment);
                            item.setContent_comment(content_comment);
                            item.setTimeStamp(tgl_comment);
                            item.setParent_id(parent_id);
                            item.setUserLike(userLike);
                            item.setUserDislike(userDislike);
                            item.setFlag(false);

                            feedItems.add(item);
                        }
                        adapter.notifyDataSetChanged();

                        JSONArray ja = new JSONArray();
                        JSONObject last = dataJsonArr.getJSONObject(dataJsonArr.length() - 1);
                        ja.put(last);
                        updateData(feedItems.size() + "", ja);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        ambilGambar ru = new ambilGambar();
        ru.execute(userid, idRoomTab, id_note, bc_user);
    }


    private void getListNotesComment(String username_room, String id_rooms_tab, String id_note, final String bc_user, String url) {
        class ambilGambarSatu extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
            String result = "";
            String idRoomTab = "";
            InputStream inputStream = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                idRoomTab = params[1];
                HashMap<String, String> data = new HashMap<String, String>();
                //username_room, id_rooms_tab, bc_user, id_note
                data.put("username_room", params[0]);
                data.put("id_rooms_tab", params[1]);
                data.put("id_note", params[2]);
                data.put("bc_user", params[3]);
                String result = profileSaveDescription.sendPostRequest(params[4], data);
                return result;
            }

            protected void onPostExecute(String s) {
                JSONArray dataJsonArr = null;
                JSONArray commentBranchJsonArr = null;
                String data = "";

                if (s.equals(null)) {
                    Toast.makeText(NoteCommentActivity.this, "Internet Problem.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        feedItems.clear();
                        JSONObject json = new JSONObject(s);
                        String id_note = json.getString("id_note");

                        dataJsonArr = json.getJSONArray("data");
                        for (int i = 0; i < dataJsonArr.length(); i++) {

                            JSONObject c = dataJsonArr.getJSONObject(i);

                            String id_comment = c.getString("id_comment");
                            String uid = c.getString("userid");
                            String profile_name = c.getString("profile_name");
                            String profile_photo = c.getString("profile_photo");
                            String amount_of_like = c.getString("amount_of_like");
                            String amount_of_dislike = c.getString("amount_of_dislike");
                            String amount_of_comment = c.getString("amount_of_comment");
                            String content_comment = c.getString("content_comment");
                            String tgl_comment = c.getString("tgl_comment");
                            String parent_id = c.getString("parent_id");
                            String userLike = c.getString("user_like");
                            String userDislike = c.getString("user_dislike");

                            CommentModel item = new CommentModel();
                            item.setIdRoomTab(idRoomTab);
                            item.setHeaderColor(color);
                            item.setId_note(id_note);
                            item.setId_comment(id_comment);
                            item.setMyuserid(bc_user);
                            item.setUserid(uid);
                            item.setProfileName(profile_name);

                            String pPhoto = c.isNull("profile_photo") ? null : c.getString("profile_photo");
                            item.setProfile_photo(pPhoto);
                            item.setJumlahLove(amount_of_like);
                            item.setJumlahNix(amount_of_dislike);
                            item.setJumlahComment(amount_of_comment);
                            item.setContent_comment(content_comment);
                            item.setTimeStamp(tgl_comment);
                            item.setParent_id(parent_id);
                            item.setUserLike(userLike);
                            item.setUserDislike(userDislike);
                            item.setFlag(false);

                            int jKomen = c.getJSONArray("comment_branch").length();
                            if (c.getJSONArray("comment_branch").length() > 0) {
                                commentBranchJsonArr = c.getJSONArray("comment_branch");
                                for (int j = 0; j < commentBranchJsonArr.length(); j++) {
                                    JSONObject d = commentBranchJsonArr.getJSONObject(j);

                                    String pName2 = d.isNull("profile_name") ? null : d.getString("profile_name");
                                    item.setName2(pName2);
                                    String pComment2 = d.isNull("content_comment") ? null : d.getString("content_comment");
                                    item.setComment2(pComment2);
                                }
                            }
                            feedItems.add(item);
                        }
                        adapter.notifyDataSetChanged();
                        JSONArray ja = new JSONArray();
                        JSONObject last = dataJsonArr.getJSONObject(dataJsonArr.length() - 1);
                        ja.put(last);
                        updateData(feedItems.size() + "", ja);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        ambilGambarSatu ru = new ambilGambarSatu();
        ru.execute(username_room, id_rooms_tab, id_note, bc_user, url);
    }

    public void updateData(String jumlah, JSONArray lastComment) {
        BotListDB db = null;
        if (db == null) {
            db = BotListDB.getInstance(getApplicationContext());
        }
        String userid = getIntent().getStringExtra("userid");
        String id_note = getIntent().getStringExtra("id_note");
        String idRoomTab = getIntent().getStringExtra("id_room_tab");
        ArrayList<RoomsDetail> allRoomDetailFormWithFlag = db.allRoomDetailFormWithFlag("", userid, idRoomTab, "value");
        for (RoomsDetail oo : allRoomDetailFormWithFlag) {
        }
        Cursor cursorValue = db.getSingleRoomDetailFormWithFlag(id_note, userid, idRoomTab, "value");

        if (cursorValue.getCount() > 0) {
            final String contentValue = cursorValue.getString(cursorValue.getColumnIndexOrThrow(BotListDB.ROOM_CONTENT));
            if (!contentValue.equalsIgnoreCase("")) {
                try {
                    JSONObject c = new JSONObject(contentValue);

                    try {
                        JSONObject j = DinamicRoomTaskActivity.function(c, "amount_of_comment", jumlah);
                        JSONObject own = j;
                        own.put("comment_note", lastComment);

                        RoomsDetail orderModel = new RoomsDetail(id_note, idRoomTab, userid, String.valueOf(own), "", "", "value");
                        db.updateDetailRoomWithFlagContent(orderModel);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
        }
    }

    private void sendCommentTree(final String userid, final String textComment, final String id_note, final String id_comment, final String bc_user, final String id_room_tab) {
        class ambilGambar extends AsyncTask<String, Void, String> {
            ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
            String result = "";
            private ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                if (progressDialog == null) {
                    progressDialog = UtilsPD.createProgressDialog(NoteCommentActivity.this);
                    progressDialog.show();
                }
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("username_room", params[0]);
                data.put("id_rooms_tab", params[1]);
                data.put("id_note", params[2]);
                data.put("bc_user", params[3]);
                data.put("id_comment", params[4]);
                data.put("comment_content", params[5]);
                String result = profileSaveDescription.sendPostRequest(URL_SEND_COMMENT, data);
                return result;
            }

            protected void onPostExecute(String s) {
                getListNotesCommentTreeSubmit(userid, id_note, id_comment, bc_user, id_room_tab);
                progressDialog.dismiss();
                if (!s.equals("1")) {
                    Toast.makeText(NoteCommentActivity.this, "Check your internet problem.", Toast.LENGTH_LONG).show();
                }
                super.onPostExecute(s);
            }
        }
        ambilGambar ru = new ambilGambar();
        ru.execute(userid, id_room_tab, id_note, bc_user, id_comment, textComment);
    }

    private void getListNotesCommentTreeSubmit(String userid, String id_note, String id_comment, final String bc_user, final String tab_id) {
        class ambilGambar extends AsyncTask<String, Void, String> {
            ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
            String result = "";

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("username_room", params[0]);
                data.put("id_rooms_tab", params[1]);
                data.put("id_note", params[2]);
                data.put("bc_user", params[3]);
                data.put("parent_id", params[4]);

                String result = profileSaveDescription.sendPostRequest(URL_LIST_COMMENT_IN_COMMENT, data);
                return result;
            }

            protected void onPostExecute(String s) {
                JSONArray dataJsonArr = null;
                JSONArray commentBranchJsonArr = null;
                String data = "";

                if (s.equals(null)) {
                    Toast.makeText(NoteCommentActivity.this, "Internet Problem.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        feedItems.clear();
                        JSONObject json = new JSONObject(s);
                        String id_note = json.getString("id_note");

                        dataJsonArr = json.getJSONArray("data");
                        for (int i = 0; i < dataJsonArr.length(); i++) {

                            JSONObject c = dataJsonArr.getJSONObject(i);
                            String id_comment = c.getString("id_comment");
                            String uid = c.getString("userid");
                            String profile_name = c.getString("profile_name");
                            String profile_photo = c.getString("profile_photo");
                            String amount_of_like = c.getString("amount_of_like");
                            String amount_of_dislike = c.getString("amount_of_dislike");
                            String amount_of_comment = c.getString("amount_of_comment");
                            String content_comment = c.getString("content_comment");
                            String tgl_comment = c.getString("tgl_comment");
                            String parent_id = c.getString("parent_id");
                            String userLike = c.getString("user_like");
                            String userDislike = c.getString("user_dislike");

                            CommentModel item = new CommentModel();
                            item.setIdRoomTab(tab_id);
                            item.setHeaderColor(color);
                            item.setId_note(id_note);
                            item.setId_comment(id_comment);
                            item.setMyuserid(bc_user);
                            item.setUserid(uid);
                            item.setProfileName(profile_name);
                            String pPhoto = c.isNull("profile_photo") ? null : c.getString("profile_photo");
                            item.setProfile_photo(pPhoto);
                            item.setJumlahLove(amount_of_like);
                            item.setJumlahNix(amount_of_dislike);
                            item.setJumlahComment(amount_of_comment);
                            item.setContent_comment(content_comment);
                            item.setTimeStamp(tgl_comment);
                            item.setParent_id(parent_id);
                            item.setUserLike(userLike);
                            item.setUserDislike(userDislike);
                            item.setFlag(false);


                            int jKomen = c.getJSONArray("comment_branch").length();
                            if (c.getJSONArray("comment_branch").length() > 0) {
                                commentBranchJsonArr = c.getJSONArray("comment_branch");
                                for (int j = 0; j < commentBranchJsonArr.length(); j++) {
                                    JSONObject d = commentBranchJsonArr.getJSONObject(j);

                                    String pName2 = d.isNull("profile_name") ? null : d.getString("profile_name");
                                    item.setName2(pName2);
                                    String pComment2 = d.isNull("content_comment") ? null : d.getString("content_comment");
                                    item.setComment2(pComment2);
                                }
                            }
                            feedItems.add(item);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        ambilGambar ru = new ambilGambar();
        ru.execute(userid, tab_id, id_note, bc_user, id_comment);
    }
    private void sendCommentTreePersonal(final String userid, String textComment, final String id_note, final String id_comment, final String bc_user) {
        class ambilGambar extends AsyncTask<String, Void, String> {
            ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
            String result = "";
            private ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                if (progressDialog == null) {
                    progressDialog = UtilsPD.createProgressDialog(NoteCommentActivity.this);
                    progressDialog.show();
                }
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("userid", params[4]);
                data.put("id_note", params[2]);
                data.put("comment_content", params[1]);
                data.put("id_comment", params[3]);
                String result = profileSaveDescription.sendPostRequest(URL_SEND_COMMENT_PERSONAL,data);
                return  result;
            }

            protected void onPostExecute(String s) {
                progressDialog.dismiss();
                if(!s.equals("1")){
                    Toast.makeText(NoteCommentActivity.this, "Check your internet connection", Toast.LENGTH_LONG).show();
                }else{
                    getListNotesCommentinCommentPersonal(userid, id_note, id_comment, bc_user);
                }
                super.onPostExecute(s);
            }
        }
        ambilGambar ru = new ambilGambar();
        ru.execute(userid,textComment,id_note, id_comment, bc_user);
    }

    private void sendCommentTestPersonal(final String userid, String textComment, final String id_note, final String bc_user) {
        class ambilGambar extends AsyncTask<String, Void, String> {
            ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
            String result = "";
            private ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                if (progressDialog == null) {
                    progressDialog = UtilsPD.createProgressDialog(NoteCommentActivity.this);
                    progressDialog.show();
                }
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("userid", params[3]);
                data.put("comment_content", params[1]);
                data.put("id_note", params[2]);
                String result = profileSaveDescription.sendPostRequest(URL_SEND_COMMENT_PERSONAL,data);
                return  result;
            }

            protected void onPostExecute(String s) {
                progressDialog.dismiss();
                if(!s.equals("1")){
                    Toast.makeText(NoteCommentActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
                }else{
                    getListNotesCommentSubmitPersonal(userid, id_note, bc_user);
                }
                super.onPostExecute(s);
            }
        }
        ambilGambar ru = new ambilGambar();
        ru.execute(userid,textComment,id_note,bc_user);
    }

    private void getListNotesCommentSubmitPersonal(String userid, String id_note, final String bc_user) {
        class ambilGambar extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
            String result = "";
            InputStream inputStream = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("userid", params[0]);
                data.put("id_note", params[1]);
                data.put("bc_user", params[2]);
                String result = profileSaveDescription.sendPostRequest(URL_LIST_NOTE_COMMENT_PERSONAL,data);
                return  result;
            }

            protected void onPostExecute(String s) {
                JSONArray dataJsonArr = null;
                String data = "";

                if(s.equals(null)){
                    Toast.makeText(NoteCommentActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
                }else{
                    try{
                        feedItems.clear();
                        JSONObject json = new JSONObject(s);
                        String id_note = json.getString("id_note");

                        dataJsonArr = json.getJSONArray("data");
                        for (int i = 0; i < dataJsonArr.length(); i++) {

                            JSONObject c = dataJsonArr.getJSONObject(i);

                            String id_comment = c.getString("id_comment");
                            String uid = c.getString("userid");
                            String profile_name = c.getString("profile_name");
                            String profile_photo = c.getString("profile_photo");
                            String amount_of_like = c.getString("amount_of_like");
                            String amount_of_dislike = c.getString("amount_of_dislike");
                            String amount_of_comment = c.getString("amount_of_comment");
                            String content_comment = c.getString("content_comment");
                            String tgl_comment = c.getString("tgl_comment");
                            String parent_id = c.getString("parent_id");
                            String userLike = c.getString("user_like");
                            String userDislike = c.getString("user_dislike");

                            CommentModel item = new CommentModel();
                            item.setId_note(id_note);
                            item.setId_comment(id_comment);
                            item.setMyuserid(bc_user);
                            item.setUserid(uid);
                            item.setProfileName(profile_name);
                            String pPhoto = c.isNull("profile_photo") ? null : c.getString("profile_photo");
                            item.setProfile_photo(pPhoto);
                            item.setJumlahLove(amount_of_like);
                            item.setJumlahNix(amount_of_dislike);
                            item.setJumlahComment(amount_of_comment);
                            item.setContent_comment(Html.fromHtml(URLDecoder.decode(String.valueOf(Html.fromHtml(content_comment.toString())))).toString());
                            item.setTimeStamp(tgl_comment);
                            item.setParent_id(parent_id);
                            item.setUserLike(userLike);
                            item.setUserDislike(userDislike);
                            item.setFlag(personal);

                            feedItems.add(item);
                        }
                        adapter.notifyDataSetChanged();

                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            }
        }
        ambilGambar ru = new ambilGambar();
        ru.execute(userid, id_note, bc_user);
    }

    private void getListNotesCommentTreeSubmitPersonal(String userid, String id_note, String id_comment, final String bc_user) {
        class ambilGambar extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
            String result = "";
            InputStream inputStream = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("userid", params[0]);
                data.put("id_note", params[1]);
                data.put("id_comment", params[2]);
                String result = profileSaveDescription.sendPostRequest(URL_LIST_COMMENT_IN_COMMENT_PERSONAL,data);
                return  result;
            }

            protected void onPostExecute(String s) {
                JSONArray dataJsonArr = null;
                JSONArray commentBranchJsonArr = null;
                String data = "";

                if(s.equals(null)){
                    Toast.makeText(NoteCommentActivity.this, "Internet Problem.", Toast.LENGTH_SHORT).show();
                }else{
                    try{
                        feedItems.clear();
                        JSONObject json = new JSONObject(s);
                        String id_note = json.getString("id_note");

                        dataJsonArr = json.getJSONArray("data");
                        for (int i = 0; i < dataJsonArr.length(); i++) {

                            JSONObject c = dataJsonArr.getJSONObject(i);

                            String id_comment = c.getString("id_comment");
                            String uid = c.getString("userid");
                            String profile_name = c.getString("profile_name");
                            String profile_photo = c.getString("profile_photo");
                            String amount_of_like = c.getString("amount_of_like");
                            String amount_of_dislike = c.getString("amount_of_dislike");
                            String amount_of_comment = c.getString("amount_of_comment");
                            String content_comment = c.getString("content_comment");
                            String tgl_comment = c.getString("tgl_comment");
                            String parent_id = c.getString("parent_id");
                            String userLike = c.getString("user_like");
                            String userDislike = c.getString("user_dislike");

                            Log.e(TAG, "id_comment: " + id_comment
                                    + ", uid: " + uid
                                    + ", profile_name: " + profile_name
                                    + ", profile_photo: " + profile_photo
                                    + ", amount_of_comment: " + amount_of_comment
                                    + ", tgl_comment: " + tgl_comment);

                            CommentModel item = new CommentModel();
                            item.setId_note(id_note);
                            item.setId_comment(id_comment);
                            item.setMyuserid(bc_user);
                            item.setUserid(uid);
                            item.setProfileName(profile_name);
                            String pPhoto = c.isNull("profile_photo") ? null : c.getString("profile_photo");
                            item.setProfile_photo(pPhoto);
                            item.setJumlahLove(amount_of_like);
                            item.setJumlahNix(amount_of_dislike);
                            item.setJumlahComment(amount_of_comment);
                            item.setContent_comment(content_comment);
                            item.setTimeStamp(tgl_comment);
                            item.setParent_id(parent_id);
                            item.setUserLike(userLike);
                            item.setUserDislike(userDislike);
                            item.setFlag(personal);


                            int jKomen = c.getJSONArray("comment_branch").length();
                            if (c.getJSONArray("comment_branch").length() > 0) {
                                commentBranchJsonArr = c.getJSONArray("comment_branch");
                                for (int j = 0; j < commentBranchJsonArr.length(); j++) {
                                    JSONObject d = commentBranchJsonArr.getJSONObject(j);

                                    String pName2 = d.isNull("profile_name") ? null : d.getString("profile_name");
                                    item.setName2(pName2);
                                    String pComment2 = d.isNull("content_comment") ? null : d.getString("content_comment");
                                    item.setComment2(pComment2);
                                }
                            }
                            feedItems.add(item);
                        }
                        adapter.notifyDataSetChanged();

                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            }
        }
        ambilGambar ru = new ambilGambar();
        ru.execute(userid, id_note, id_comment);
    }

    private void getListNotesCommentinComment(String userid, String id_note, String id_comment, final String bc_user, final String idRoomTab) {
        class ambilGambar extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
            String result = "";
            InputStream inputStream = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("username_room", params[0]);
                data.put("id_note", params[1]);
                data.put("parent_id", params[2]);
                data.put("bc_user", params[3]);
                data.put("id_rooms_tab", params[4]);
                String result = profileSaveDescription.sendPostRequest(URL_LIST_COMMENT_IN_COMMENT, data);
                return result;
            }

            protected void onPostExecute(String s) {
                JSONArray dataJsonArr = null;
                JSONArray commentBranchJsonArr = null;
                String data = "";

                if (s.equals(null)) {
                    Toast.makeText(NoteCommentActivity.this, "Internet Problem.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        feedItems.clear();
                        JSONObject json = new JSONObject(s);
                        String id_note = json.getString("id_note");

                        dataJsonArr = json.getJSONArray("data");
                        for (int i = 0; i < dataJsonArr.length(); i++) {

                            JSONObject c = dataJsonArr.getJSONObject(i);

                            String id_comment = c.getString("id_comment");
                            String uid = c.getString("userid");
                            String profile_name = c.getString("profile_name");
                            String profile_photo = c.getString("profile_photo");
                            String amount_of_like = c.getString("amount_of_like");
                            String amount_of_dislike = c.getString("amount_of_dislike");
                            String amount_of_comment = c.getString("amount_of_comment");
                            String content_comment = c.getString("content_comment");
                            String tgl_comment = c.getString("tgl_comment");
                            String parent_id = c.getString("parent_id");
                            String userLike = c.getString("user_like");
                            String userDislike = c.getString("user_dislike");

                            Log.e(TAG, "id_comment: " + id_comment
                                    + ", uid: " + uid
                                    + ", profile_name: " + profile_name
                                    + ", profile_photo: " + profile_photo
                                    + ", amount_of_comment: " + amount_of_comment
                                    + ", tgl_comment: " + tgl_comment);

                            CommentModel item = new CommentModel();
                            item.setIdRoomTab(idRoomTab);
                            item.setHeaderColor(color);
                            item.setId_note(id_note);
                            item.setId_comment(id_comment);
                            item.setMyuserid(bc_user);
                            item.setUserid(uid);
                            item.setProfileName(profile_name);
                            String pPhoto = c.isNull("profile_photo") ? null : c.getString("profile_photo");
                            item.setProfile_photo(pPhoto);
                            item.setHeaderColor(color);
                            item.setJumlahLove(amount_of_like);
                            item.setJumlahNix(amount_of_dislike);
                            item.setJumlahComment(amount_of_comment);
                            item.setContent_comment(content_comment);
                            item.setTimeStamp(tgl_comment);
                            item.setParent_id(parent_id);
                            item.setUserLike(userLike);
                            item.setUserDislike(userDislike);
                            item.setFlag(false);


                            int jKomen = c.getJSONArray("comment_branch").length();
                            if (c.getJSONArray("comment_branch").length() > 0) {
                                commentBranchJsonArr = c.getJSONArray("comment_branch");
                                for (int j = 0; j < commentBranchJsonArr.length(); j++) {
                                    JSONObject d = commentBranchJsonArr.getJSONObject(j);

                                    String pName2 = d.isNull("profile_name") ? null : d.getString("profile_name");
                                    item.setName2(pName2);
                                    String pComment2 = d.isNull("content_comment") ? null : d.getString("content_comment");
                                    item.setComment2(pComment2);
                                }
                            }
                            feedItems.add(item);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        ambilGambar ru = new ambilGambar();
        ru.execute(userid, id_note, id_comment, bc_user, idRoomTab);
    }

    private void getListNotesCommentinCommentPersonal(String userid, String id_note, String id_comment, final String bc_user) {
        class ambilGambar extends AsyncTask<String, Void, String> {
            ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
            String result = "";

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("userid", params[0]);
                data.put("id_note", params[1]);
                data.put("id_comment", params[2]);
                data.put("bc_user", params[3]);
                String result = profileSaveDescription.sendPostRequest(URL_LIST_COMMENT_IN_COMMENT_PERSONAL,data);
                return  result;
            }

            protected void onPostExecute(String s) {
                JSONArray dataJsonArr = null;
                JSONArray commentBranchJsonArr = null;
                String data = "";
                if(s.equals(null)){
                    Toast.makeText(NoteCommentActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
                }else{
                    try{
                        feedItems.clear();
                        JSONObject json = new JSONObject(s);
                        String id_note = json.getString("id_note");

                        dataJsonArr = json.getJSONArray("data");
                        for (int i = 0; i < dataJsonArr.length(); i++) {

                            JSONObject c = dataJsonArr.getJSONObject(i);

                            String id_comment = c.getString("id_comment");
                            String uid = c.getString("userid");
                            String profile_name = c.getString("profile_name");
                            String profile_photo = c.getString("profile_photo");
                            String amount_of_like = c.getString("amount_of_like");
                            String amount_of_dislike = c.getString("amount_of_dislike");
                            String amount_of_comment = c.getString("amount_of_comment");
                            String content_comment = c.getString("content_comment");
                            String tgl_comment = c.getString("tgl_comment");
                            String parent_id = c.getString("parent_id");
                            String userLike = c.getString("user_like");
                            String userDislike = c.getString("user_dislike");

                            CommentModel item = new CommentModel();
                            item.setId_note(id_note);
                            item.setId_comment(id_comment);
                            item.setMyuserid(bc_user);
                            item.setUserid(uid);
                            item.setProfileName(profile_name);
                            String pPhoto = c.isNull("profile_photo") ? null : c.getString("profile_photo");
                            item.setProfile_photo(pPhoto);
                            item.setJumlahLove(amount_of_like);
                            item.setJumlahNix(amount_of_dislike);
                            item.setJumlahComment(amount_of_comment);
                            item.setContent_comment(Html.fromHtml(URLDecoder.decode(String.valueOf(Html.fromHtml(content_comment.toString())))).toString());
                            item.setTimeStamp(tgl_comment);
                            item.setParent_id(parent_id);
                            item.setUserLike(userLike);
                            item.setUserDislike(userDislike);
                            item.setFlag(personal);


                            int jKomen = c.getJSONArray("comment_branch").length();
                            if (c.getJSONArray("comment_branch").length() > 0) {
                                commentBranchJsonArr = c.getJSONArray("comment_branch");
                                for (int j = 0; j < commentBranchJsonArr.length(); j++) {
                                    JSONObject d = commentBranchJsonArr.getJSONObject(j);

                                    String pName2 = d.isNull("profile_name") ? null : d.getString("profile_name");
                                    item.setName2(pName2);
                                    String pComment2 = d.isNull("content_comment") ? null : d.getString("content_comment");
                                    item.setComment2(pComment2);
                                }
                            }
                            feedItems.add(item);
                        }
                        adapter.notifyDataSetChanged();

                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            }
        }
        ambilGambar ru = new ambilGambar();
        ru.execute(userid, id_note, id_comment, bc_user);
    }

    private void getListNotesCommentPersonal(String userid, String id_note, final String bc_user) {
        class ambilGambar extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
            String result = "";
            InputStream inputStream = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("userid", params[0]);
                data.put("id_note", params[1]);
                data.put("bc_user", params[2]);
                String result = profileSaveDescription.sendPostRequest(URL_LIST_NOTE_COMMENT_PERSONAL,data);
                return  result;
            }

            protected void onPostExecute(String s) {
                JSONArray dataJsonArr = null;
                JSONArray commentBranchJsonArr = null;
                String data = "";

                if(s.equals(null)){
                    Toast.makeText(NoteCommentActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
                }else{
                    try{
                        feedItems.clear();
                        JSONObject json = new JSONObject(s);
                        String id_note = json.getString("id_note");

                        dataJsonArr = json.getJSONArray("data");
                        for (int i = 0; i < dataJsonArr.length(); i++) {

                            JSONObject c = dataJsonArr.getJSONObject(i);

                            String id_comment = c.getString("id_comment");
                            String uid = c.getString("userid");
                            String profile_name = c.getString("profile_name");
                            String profile_photo = c.getString("profile_photo");
                            String amount_of_like = c.getString("amount_of_like");
                            String amount_of_dislike = c.getString("amount_of_dislike");
                            String amount_of_comment = c.getString("amount_of_comment");
                            String content_comment = c.getString("content_comment");
                            String tgl_comment = c.getString("tgl_comment");
                            String parent_id = c.getString("parent_id");
                            String userLike = c.getString("user_like");
                            String userDislike = c.getString("user_dislike");

                            Log.e(TAG, "id_comment: " + id_comment
                                    + ", uid: " + uid
                                    + ", profile_name: " + profile_name
                                    + ", profile_photo: " + profile_photo
                                    + ", amount_of_comment: " + amount_of_comment
                                    + ", tgl_comment: " + tgl_comment
                                    + ", parent_id: " + parent_id);

                            CommentModel item = new CommentModel();
                            item.setId_note(id_note);
                            item.setId_comment(id_comment);
                            item.setMyuserid(bc_user);
                            item.setUserid(uid);
                            item.setProfileName(profile_name);
                            String pPhoto = c.isNull("profile_photo") ? null : c.getString("profile_photo");
                            item.setProfile_photo(pPhoto);
                            item.setJumlahLove(amount_of_like);
                            item.setJumlahNix(amount_of_dislike);
                            item.setJumlahComment(amount_of_comment);
                            item.setContent_comment(content_comment);
                            item.setTimeStamp(tgl_comment);
                            item.setParent_id(parent_id);
                            item.setUserLike(userLike);
                            item.setUserDislike(userDislike);
                            item.setFlag(personal);

                            int jKomen = c.getJSONArray("comment_branch").length();
                            if (c.getJSONArray("comment_branch").length() > 0) {
                                commentBranchJsonArr = c.getJSONArray("comment_branch");
                                for (int j = 0; j < commentBranchJsonArr.length(); j++) {
                                    JSONObject d = commentBranchJsonArr.getJSONObject(j);

                                    String pName2 = d.isNull("profile_name") ? null : d.getString("profile_name");
                                    item.setName2(pName2);
                                    String pComment2 = d.isNull("content_comment") ? null : d.getString("content_comment");
                                    item.setComment2(pComment2);
                                }
                            }
                            feedItems.add(item);
                        }
                        adapter.notifyDataSetChanged();

                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            }
        }
        ambilGambar ru = new ambilGambar();
        ru.execute(userid, id_note, bc_user);
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(mWriteComment, emojicon);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (emojicons.getVisibility() == View.GONE) {
                finish();
            } else {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                mWriteComment.setFocusableInTouchMode(true);
                mWriteComment.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mWriteComment, InputMethodManager.SHOW_IMPLICIT);
                emojicons.setVisibility(View.GONE);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
