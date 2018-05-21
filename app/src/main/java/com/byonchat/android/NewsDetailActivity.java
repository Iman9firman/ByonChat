package com.byonchat.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.personalRoom.NoteCommentActivity;
import com.byonchat.android.personalRoom.adapter.NewsFeedListAdapter;
import com.byonchat.android.personalRoom.adapter.NoteFeedListAdapter;
import com.byonchat.android.personalRoom.asynctask.ProfileSaveDescription;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.utils.MyLeadingMarginSpan2;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;

public class NewsDetailActivity extends AppCompatActivity  implements AppBarLayout.OnOffsetChangedListener {

    private CollapsingToolbarLayout collapsingToolbarLayout = null;
    String color;
    String title;
    public Target profilePic;
    LinearLayout btNix,btLoves,btComment,mLinearHiddenComment;
    TextView hiddenComment,totalComments,totalLoves;
    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR  = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS     = 0.3f;
    private boolean mIsTheTitleVisible          = false;
    private boolean mIsTheTitleContainerVisible = true;
    TextView titles;
    LinearLayout linearLayout;
    Target iconView;
    Toolbar toolbar;
    private AppBarLayout mAppBarLayout;
    private static final String URL_SAVE_LOVES = "https://"+ MessengerConnectionService.HTTP_SERVER+"/bc_voucher_client/webservice/proses/list_note_like.php";
    private static final String URL_SAVE_DISLIKE = "https://"+ MessengerConnectionService.HTTP_SERVER+"/bc_voucher_client/webservice/proses/list_note_dislike.php";
    Context mContext = this;
    BotListDB botListDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.BLACK);
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        titles = (TextView) findViewById(R.id.titleNews);
        linearLayout = (LinearLayout) findViewById(R.id.linear);
        mAppBarLayout   = (AppBarLayout) findViewById(R.id.main_appbar);
        iconView =(Target) findViewById(R.id.backdrop);
        btLoves =(LinearLayout) findViewById(R.id.btLoves);
        btNix =(LinearLayout) findViewById(R.id.btNix);
        btComment =(LinearLayout) findViewById(R.id.btComment);
        totalComments =(TextView) findViewById(R.id.totalComments);
        totalLoves =(TextView) findViewById(R.id.totalLoves);
        hiddenComment =(TextView) findViewById(R.id.hiddenComment);
        mLinearHiddenComment =(LinearLayout) findViewById(R.id.LinearHiddenComment);
        profilePic=(Target) findViewById(R.id.imagePhoto);
    }

    @Override
    protected void onResume() {

        if (botListDB==null){
            botListDB = BotListDB.getInstance(this);
        }

        title = getIntent().getStringExtra("title");
        String time = getIntent().getStringExtra("time");
        String content = getIntent().getStringExtra("content");
        final String image = getIntent().getStringExtra("image");
        final String totalComment = getIntent().getStringExtra("totalComment");
        String userLike = getIntent().getStringExtra("userLike");
        String userDislike = getIntent().getStringExtra("userDislike");
        String totalLove = getIntent().getStringExtra("totalLove");
        final String userid = getIntent().getStringExtra("userid");
        final String id_note = getIntent().getStringExtra("id_note");
        final String bc_user = getIntent().getStringExtra("bc_user");
        final String id_room_tab = getIntent().getStringExtra("id_room_tab");
        final String coment = getIntent().getStringExtra("coment");
        final String comentName = getIntent().getStringExtra("comentName");
        final String foto_file = getIntent().getStringExtra("foto_file");
        color = getIntent().getStringExtra("color");



        Cursor cur = botListDB.getSingleRoom(userid);
        if (cur.getCount() > 0) {
            Picasso.with(getApplicationContext())
                    .load(cur.getString(cur.getColumnIndex(BotListDB.ROOM_ICON)).toString())
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .into(profilePic);
        } else {
            Picasso.with(getApplicationContext()).load(R.drawable.ic_no_photo)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .into(profilePic);
        }

        if(!comentName.equalsIgnoreCase("")){
            hiddenComment.setText(comentName+" : "+ coment);
            int jComment = Integer.parseInt(totalComment);
            if (jComment > 0) {
                hiddenComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), NoteCommentActivity.class);
                        intent.putExtra("userid", userid);
                        intent.putExtra("id_note", id_note);
                        intent.putExtra("bc_user", bc_user);
                        intent.putExtra("id_room_tab",id_room_tab);
                        intent.putExtra("color",color);
                        startActivity(intent);
                    }
                });
            }
        }else{
            mLinearHiddenComment.setVisibility(View.GONE);
        }


        Cursor cursorValue = botListDB.getSingleRoomDetailFormWithFlag(id_note, userid, id_room_tab, "value");
        if (cursorValue.getCount() > 0) {
            final String contentValue = cursorValue.getString(cursorValue.getColumnIndexOrThrow(BotListDB.ROOM_CONTENT));
            try {
                JSONObject c = new JSONObject(contentValue);
                String jLove = c.isNull("amount_of_like") ? null : c.getString("amount_of_like");
                totalLoves.setText(jLove);
                String jComment = c.isNull("amount_of_comment") ? null : c.getString("amount_of_comment");
                totalComments.setText(jComment);

                if (c.getJSONArray("comment_note").length() > 0) {
                    mLinearHiddenComment.setVisibility(View.VISIBLE);
                    JSONArray commentNoteJsonArr = c.getJSONArray("comment_note");
                    for (int j = 0; j < commentNoteJsonArr.length(); j++) {
                        JSONObject d = commentNoteJsonArr.getJSONObject(j);

                        String pName2 = d.isNull("profile_name") ? null : d.getString("profile_name");
                        String pId2 = d.isNull("userid") ? null : d.getString("userid");
                        String pComment2 = d.isNull("content_comment") ? null : d.getString("content_comment");
                        Log.w("cuciKaki1",(pName2 != null ? pName2 : pId2));
                        Log.w("cuciKaki2",pComment2);
                        hiddenComment.setText((pName2 != null ? pName2 : pId2) + " : " + pComment2);
                    }
                }else{
                    mLinearHiddenComment.setVisibility(View.GONE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            Log.w("buntut","kuving");
        }


        if (userLike.equals("false")) {
            btLoves.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Cursor cursorValue = botListDB.getSingleRoomDetailFormWithFlag(id_note, userid,id_room_tab, "value");

                    if (cursorValue.getCount() > 0) {
                        final String contentValue = cursorValue.getString(cursorValue.getColumnIndexOrThrow(BotListDB.ROOM_CONTENT));
                        if(!contentValue.equalsIgnoreCase("")){
                            try {
                                JSONObject c = new JSONObject(contentValue);
                                if (c.getString("user_like").equalsIgnoreCase("false")){
                                    new saveLikeNotes().execute(bc_user,userid,id_note,totalComment,id_room_tab,URL_SAVE_LOVES);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                btLoves.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.button_timeline_background));
            }

        } else if (userLike.equals("true")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                btLoves.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.button_timeline_unfocused_pressed));
                btLoves.setEnabled(false);
            }
        }

        if (userDislike.equals("false")) {
            btNix.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Cursor cursorValue = botListDB.getSingleRoomDetailFormWithFlag(id_note, userid,id_room_tab, "value");

                    if (cursorValue.getCount() > 0) {
                        final String contentValue = cursorValue.getString(cursorValue.getColumnIndexOrThrow(BotListDB.ROOM_CONTENT));
                        if(!contentValue.equalsIgnoreCase("")){
                            try {
                                JSONObject c = new JSONObject(contentValue);
                                if (c.getString("user_like").equalsIgnoreCase("false")){
                                    new saveLikeNotes().execute(bc_user,userid,id_note,totalComment,id_room_tab,URL_SAVE_DISLIKE);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });

        } else if (userDislike.equals("true")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                btNix.setEnabled(false);
            }
        }


        btComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NoteCommentActivity.class);
                intent.putExtra("userid", userid);
                intent.putExtra("id_note", id_note);
                intent.putExtra("bc_user",bc_user);
                intent.putExtra("id_room_tab",id_room_tab);
                intent.putExtra("color",color);
                startActivity(intent);
            }
        });
        Picasso.with(getApplicationContext())
                .load(image)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .into(iconView);
        titles.setText(title);
        linearLayout.setBackgroundColor(Color.parseColor("#"+color));

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        toolbar.setTitle(" ");
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        collapsingToolbarLayout.setTitle(" ");

        collapsingToolbarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ZoomImageViewActivity.class);
                intent.putExtra(ZoomImageViewActivity.KEY_FILE,image);
                startActivity(intent);
            }
        });

        dynamicToolbarColor();
        toolbarTextAppernce();

        mAppBarLayout.addOnOffsetChangedListener(this);

        TextView messageInfo = (TextView) findViewById(R.id.message_info);

        Drawable dIcon = getResources().getDrawable(R.drawable.news_top_left);
        int leftMargin = dIcon.getIntrinsicWidth();

        SpannableString ssInfo = new SpannableString("Updates on : 10-08-2016");
        ssInfo.setSpan(new MyLeadingMarginSpan2(1, leftMargin), 0, ssInfo.length(), 0);
        messageInfo.setText(ssInfo);

        String news = "<img src=\"file:///android_res/drawable/news_top_left_bagi.png\" style=\"float:left; visibility: hidden\">"+
                "<div style=\"font-family: sans-serif-light; font-style: normal; font-size: 12pt\">"+
                content +"</div>\n";

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setBackgroundColor(0x00000000);
        webView.loadDataWithBaseURL(null,news, "text/html", "utf-8", null);

        if(title.equalsIgnoreCase("")||title==null){
            toolbar.setVisibility(View.GONE);
        }


        super.onResume();
    }

    class saveLikeNotes extends AsyncTask<String, Void, String> {
        ProgressDialog pDialog;
        ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
        String url = "";
        String myUser = "";
        String userName = "";
        String idNote = "";
        String lastCount = "";
        String roomTab = "";
        RecyclerView.ViewHolder holder;
        InputStream inputStream = null;

        public saveLikeNotes() {
            super();
        }

        @Override
        protected void onPreExecute() {
            pDialog = ProgressDialog.show(mContext, null,
                    "please wait...", false);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            this.myUser = params[0];
            this.userName = params[1];
            this.idNote = params[2];
            this.lastCount = params[3];
            this.roomTab = params[4];
            this.url = params[5];

            HashMap<String, String> data = new HashMap<String, String>();
            data.put("bc_user", myUser);
            data.put("id_note", idNote);
            String result = profileSaveDescription.sendPostRequest(url, data);
            return result;
        }

        protected void onPostExecute(String s) {
            pDialog.dismiss();
            if (s.equals(null)) {
                Toast.makeText(mContext, "Internet Problem.", Toast.LENGTH_SHORT).show();
            } else {
                if (s.equalsIgnoreCase("1")) {
                    Cursor cursorValue = botListDB.getSingleRoomDetailFormWithFlag(idNote, userName, roomTab, "value");

                    if (cursorValue.getCount() > 0) {
                        final String contentValue = cursorValue.getString(cursorValue.getColumnIndexOrThrow(BotListDB.ROOM_CONTENT));
                        if (!contentValue.equalsIgnoreCase("")) {
                            try {
                                JSONObject c = new JSONObject(contentValue);
                                if(url.equalsIgnoreCase(URL_SAVE_LOVES)){
                                    if (c.getString("user_like").equalsIgnoreCase("false")) {
                                        try {
                                            JSONObject j = DinamicRoomTaskActivity.function(c, "amount_of_like", String.valueOf(Integer.valueOf(c.getString("amount_of_like")) + 1));
                                            JSONObject own = DinamicRoomTaskActivity.function(j, "user_like", "true");
                                            RoomsDetail orderModel = new RoomsDetail(idNote, roomTab, userName, String.valueOf(own), "", "", "value");
                                            botListDB.updateDetailRoomWithFlagContent(orderModel);
                                            totalLoves.setText(String.valueOf(Integer.valueOf(lastCount) + 1));
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                             btLoves.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.button_timeline_unfocused_pressed));
                                            }
                                            btLoves.setEnabled(false);
                                            Toast.makeText(mContext, "Like Success", Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }else if(url.equalsIgnoreCase(URL_SAVE_DISLIKE)){
                                    if (c.getString("user_dislike").equalsIgnoreCase("false")) {
                                        try {
                                            JSONObject j = DinamicRoomTaskActivity.function(c, "amount_of_dislike", String.valueOf(Integer.valueOf(c.getString("amount_of_dislike")) + 1));
                                            JSONObject own = DinamicRoomTaskActivity.function(j, "user_dislike", "true");
                                            RoomsDetail orderModel = new RoomsDetail(idNote, roomTab, userName, String.valueOf(own), "", "", "value");
                                            botListDB.updateDetailRoomWithFlagContent(orderModel);
                                             btNix.setEnabled(false);
                                            Toast.makeText(mContext, "Dislike Success", Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                } else {
                    Toast.makeText(mContext, "Please Try Again ...", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }



    private void dynamicToolbarColor() {
        collapsingToolbarLayout.setContentScrimColor(Color.parseColor("#"+color));
        collapsingToolbarLayout.setStatusBarScrimColor(Color.parseColor("#"+color));
    }

    private void toolbarTextAppernce() {
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandedappbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if(!mIsTheTitleVisible) {
                mIsTheTitleVisible = true;
                collapsingToolbarLayout.setTitle(title);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    getWindow().setStatusBarColor(Color.parseColor("#"+color));
                }
            }

        } else {

            if (mIsTheTitleVisible) {
                mIsTheTitleVisible = false;
                collapsingToolbarLayout.setTitle(" ");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    getWindow().setStatusBarColor(Color.BLACK);
                }

            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if(mIsTheTitleContainerVisible) {
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                mIsTheTitleContainerVisible = true;
            }
        }
    }
}