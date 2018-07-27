package com.byonchat.android.personalRoom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Picture;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
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

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.byonchat.android.ConfirmationSendFile;
import com.byonchat.android.ConfirmationSendFileFolllowup;
import com.byonchat.android.ConfirmationSendFileMultiple;
import com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity;
import com.byonchat.android.ImagePicker;
import com.byonchat.android.ImagePickerActivity;
import com.byonchat.android.MainActivity;
import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.helpers.ImageUtils;
import com.byonchat.android.local.Byonchat;
import com.byonchat.android.local.CacheManager;
import com.byonchat.android.model.Image;
import com.byonchat.android.personalRoom.adapter.NoteCommentFollowUpListAdapter;
import com.byonchat.android.personalRoom.adapter.NoteCommentListAdapter;
import com.byonchat.android.personalRoom.adapter.NoteCommentListAdapterNew;
import com.byonchat.android.personalRoom.asynctask.ProfileSaveDescription;
import com.byonchat.android.personalRoom.model.CommentModel;
import com.byonchat.android.personalRoom.model.NotesPhoto;
import com.byonchat.android.personalRoom.model.PictureModel;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.retrofit.ByonchatAPI;
import com.byonchat.android.utils.AndroidMultiPartEntity;
import com.byonchat.android.utils.MediaProcessingUtil;
import com.byonchat.android.utils.PermissionsUtil;
import com.byonchat.android.utils.UtilsPD;
import com.byonchat.android.utils.ValidationsKey;
import com.byonchat.android.videotrimmer.utils.FileUtils;
import com.byonchat.android.view.CircleProgressBar;
import com.byonchat.android.volley.VolleyMultipartRequest;
import com.byonchat.android.volley.VolleySingleton;
import com.google.android.gms.maps.model.Circle;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Lukmanpryg on 26/7/2018.
 */
public class NoteFollowUpActivity extends Constants implements EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener, DialogInterface.OnClickListener {

    private static final String TAG = NoteFollowUpActivity.class.getSimpleName();
    RecyclerView mRecyclerView;
    private List<CommentModel> feedItems;
    private NoteCommentFollowUpListAdapter adapter;
    EmojiconEditText mWriteComment;
    Button mButtonSend;
    Toolbar toolbar;
    CircleProgressBar vCircleProgress;
    //    private String URL_SEND_COMMENT = "https://bb.byonchat.com/bc_voucher_client/webservice/proses/add_attachment_comment.php";
    private String URL_SEND_COMMENT = "https://bb.byonchat.com/bc_voucher_client/webservice/proses/repost_attachment.php";
    //    private String URL_LIST_NOTE_COMMENT = "https://bb.byonchat.com/bc_voucher_client/webservice/category_tab/list_attachment_comment.php";
    private String URL_LIST_NOTE_COMMENT = "https://bb.byonchat.com/bc_voucher_client/webservice/category_tab/list_attachment_followup.php";
    private String URL_LIST_NOTE_COMMENT_BRANCH = "https://bb.byonchat.com/bc_voucher_client/webservice/category_tab/list_attachment_followup_branch.php";
//    private String URL_LIST_NOTE_COMMENT_BRANCH = "https://bb.byonchat.com/bc_voucher_client/webservice/category_tab/list_attachment_comment_branch.php";

    private String URL_SEND_COMMENT_PERSONAL = "https://" + MessengerConnectionService.HTTP_SERVER2 + "/personal_room/webservice/proses/comment.php";
    private String URL_LIST_NOTE_COMMENT_PERSONAL = "https://" + MessengerConnectionService.HTTP_SERVER2 + "/personal_room/webservice/view/comment.php";
    private String URL_LIST_COMMENT_IN_COMMENT_PERSONAL = "https://" + MessengerConnectionService.HTTP_SERVER2 + "/personal_room/webservice/view/comment_branch.php";
    SwipeRefreshLayout mswipeRefreshLayout;
    String color;
    Boolean personal;
    private LinearLayout emojicons, contentRoot;
    private ImageButton btnMic, btn_add_emoticon, btn_attach_file;
    private ArrayList<Image> images = new ArrayList<>();
    String cameraFileOutput;
    private ProgressDialog progressDialog;

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
        vCircleProgress = (CircleProgressBar) findViewById(R.id.progress);
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

        mRecyclerView.setLayoutManager(new LinearLayoutManager(NoteFollowUpActivity.this));
        feedItems = new ArrayList<CommentModel>();
        adapter = new NoteCommentFollowUpListAdapter(NoteFollowUpActivity.this, feedItems);
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
        btn_attach_file = (ImageButton) findViewById(R.id.btn_attach_file);

        btn_attach_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(NoteFollowUpActivity.this)
                        .setItems(R.array.attach_file, NoteFollowUpActivity.this)
                        .show();
            }
        });

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

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        switch (i) {
            case 0:
                takeImage();
                break;
            case 1:
                start(getIntent().getStringExtra("userid"));
                break;
        }
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
//                sendCommentTest(userid, textComment, id_note, bc_user, idRoomTab, "");
                new sendCommentFirst(userid, textComment, id_note, bc_user, idRoomTab, null).execute();
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

    private void registerUser(String... args) {
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
//                sendCommentTest(userid, args[1], id_note, bc_user, idRoomTab, args[0]);
                File file = new File(args[0]);
                new sendCommentFirst(userid, args[1], id_note, bc_user, idRoomTab, file).execute();
//                uploadMultiFile(userid, args[1], id_note, bc_user, idRoomTab, file);
//                sendComment1st(userid, args[1], id_note, bc_user, idRoomTab, file);
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

    private void registerUser(String content, List<NotesPhoto> photos) {
        Log.w("hahaha", content);
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
//                sendCommentTree(userid, textComment, id_note, id_comment, bc_user, idRoomTab);
                if (progressDialog == null) {
                    progressDialog = UtilsPD.createProgressDialog(NoteFollowUpActivity.this);
                    progressDialog.show();
                }
                sendCommentInComment(userid, textComment, id_note, id_comment, bc_user, idRoomTab, photos);
            } else {
                String userid = getIntent().getStringExtra("userid");
                String id_note = getIntent().getStringExtra("id_note");
                String bc_user = getIntent().getStringExtra("bc_user");
                String idRoomTab = "";
                if (getIntent().getExtras().containsKey("id_room_tab")) {
                    idRoomTab = getIntent().getStringExtra("id_room_tab");
                }
                if (progressDialog == null) {
                    progressDialog = UtilsPD.createProgressDialog(NoteFollowUpActivity.this);
                    progressDialog.show();
                }
                if (!content.equalsIgnoreCase(""))
                    sendComment1st(userid, content, id_note, bc_user, idRoomTab, photos);
                else
                    sendComment1st(userid, textComment, id_note, bc_user, idRoomTab, photos);
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

    private void uploadMultiFile(final String userid, final String textComment, final String id_note,
                                 final String bc_user, final String idRoomTab, final File photos) {

        ArrayList<String> filePaths = new ArrayList<>();
        filePaths.add(photos.getPath());
        filePaths.add(photos.getPath());
        filePaths.add(photos.getPath());

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        builder.addFormDataPart("attachment_id", id_note);
        builder.addFormDataPart("bc_user", bc_user);
        builder.addFormDataPart("content", textComment);
        Log.w("parameternya", id_note + " -- " + bc_user + " -- " + textComment + " -- " + filePaths.size());

        // Map is used to multipart the file using okhttp3.RequestBody
        // Multiple Images
        for (int i = 0; i < filePaths.size(); i++) {
            File file = new File(filePaths.get(i));
            builder.addFormDataPart("photos[]", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
        }

        MultipartBody requestBody = builder.build();
        Call<ResponseBody> call = ByonchatAPI.getInstance().uploadMultiFile(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

                Toast.makeText(NoteFollowUpActivity.this, "Success " + response.message(), Toast.LENGTH_LONG).show();

                getListNotesCommentSubmit(userid, id_note, bc_user, idRoomTab);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Log.d(TAG, "Error " + t.getMessage());
            }
        });


    }

    private class sendCommentFirst extends AsyncTask<Void, Integer, String> {
        long totalSize = 0;
        private String userid, textComment, id_note, bc_user, idRoomTab;
        private File file;

        private sendCommentFirst(String userid, String textComment, String id_note,
                                 String bc_user, String idRoomTab, File file) {
            this.userid = userid;
            this.textComment = textComment;
            this.id_note = id_note;
            this.bc_user = bc_user;
            this.idRoomTab = idRoomTab;
            this.file = file;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            vCircleProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(URL_SEND_COMMENT);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                ContentType contentType = ContentType.create("image/jpeg");

                ArrayList<File> filePaths = new ArrayList<>();
                if (file != null) {
                    filePaths.add(file);
                    filePaths.add(file);
                    filePaths.add(file);
                }

                entity.addPart("attachment_id", new StringBody(id_note));
                entity.addPart("bc_user", new StringBody(bc_user));
                entity.addPart("content", new StringBody(textComment));

                for (int i = 0; i < filePaths.size(); i++) {
                    File file = filePaths.get(i);
                    entity.addPart("photos[" + i + "]", new FileBody(file, contentType, file.getName()));
                }

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.w("apahasilnya", result);
            getListNotesCommentSubmit(userid, id_note, bc_user, idRoomTab);
            vCircleProgress.setVisibility(View.GONE);
            if (!result.equals("1")) {
                Toast.makeText(NoteFollowUpActivity.this, result + " : Check your internet problem.", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            vCircleProgress.setProgress((int) values[0]);
        }

    }

    private void sendCommentTest(final String userid, String textComment, final String id_note, final String bc_user, final String idRoomTab, final String photos) {
        class ambilGambar extends AsyncTask<String, Void, String> {
            ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
            String result = "";
            private ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                if (progressDialog == null) {
                    progressDialog = UtilsPD.createProgressDialog(NoteFollowUpActivity.this);
                    progressDialog.show();
                }
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("attachment_id", params[2]);
                data.put("bc_user", params[3]);
                data.put("comment_content", params[4]);

                String result = profileSaveDescription.sendPostRequest(URL_SEND_COMMENT, data);
                return result;
            }

            protected void onPostExecute(String s) {
                getListNotesCommentSubmit(userid, id_note, bc_user, idRoomTab);
                progressDialog.dismiss();
                if (!s.equals("1")) {
                    Toast.makeText(NoteFollowUpActivity.this, "Check your internet problem.", Toast.LENGTH_LONG).show();
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
//                data.put("username_room", params[0]);
//                data.put("id_rooms_tab", params[1]);
                data.put("attachment_id", params[2]);
                data.put("bc_user", params[3]);
                String result = profileSaveDescription.sendPostRequest(URL_LIST_NOTE_COMMENT, data);
                return result;
            }

            protected void onPostExecute(String s) {
                JSONArray dataJsonArr = null;
                String data = "";

                if (s.equals(null)) {
                    Toast.makeText(NoteFollowUpActivity.this, "Internet Problem.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        feedItems.clear();
                        JSONObject json = new JSONObject(s);
                        String id_note = json.getString("attachment_id");

                        dataJsonArr = json.getJSONArray("data");
                        for (int i = 0; i < dataJsonArr.length(); i++) {

                            JSONObject c = dataJsonArr.getJSONObject(i);

                            String id_comment = c.getString("id_comment");
                            String uid = c.getString("userid");
                            String profile_name = c.getString("profile_name");
                            String profile_photo = c.getString("profile_photo");
                            String amount_of_comment = c.getString("amount_of_comment");
                            String content_comment = c.getString("content_comment");
                            String tgl_comment = c.getString("tgl_comment");
                            String parent_id = c.getString("parent_id");
                            String photos = c.getString("photos");

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
                            item.setJumlahLove("");
                            item.setJumlahNix("");
                            item.setJumlahComment(amount_of_comment);
                            item.setContent_comment(content_comment);
                            item.setTimeStamp(tgl_comment);
                            item.setParent_id(parent_id);
                            item.setUserLike("");
                            item.setUserDislike("");
                            item.setFlag(false);
                            item.setPhotos(photos);

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
//                data.put("username_room", params[0]);
//                data.put("id_rooms_tab", params[1]);
                data.put("attachment_id", params[2]);
                data.put("bc_user", params[3]);
                String result = profileSaveDescription.sendPostRequest(params[4], data);
                return result;
            }

            protected void onPostExecute(String s) {
                Log.w("hasilget", s);
                JSONArray dataJsonArr = null;
                JSONArray commentBranchJsonArr = null;
                String data = "";
                if (s.equals(null)) {
                    Toast.makeText(NoteFollowUpActivity.this, "Internet Problem.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        feedItems.clear();
                        JSONObject json = new JSONObject(s);
                        String id_note = json.getString("attachment_id");

                        dataJsonArr = json.getJSONArray("data");
                        for (int i = 0; i < dataJsonArr.length(); i++) {

                            JSONObject c = dataJsonArr.getJSONObject(i);

                            String id_comment = c.getString("id_comment");
                            String uid = c.getString("userid");
                            String profile_name = c.getString("profile_name");
                            String profile_photo = c.getString("profile_photo");
                            String amount_of_comment = c.getString("amount_of_comment");
                            String content_comment = c.getString("content_comment");
                            String tgl_comment = c.getString("tgl_comment");
                            String parent_id = c.getString("parent_id");
                            String photos = c.getString("photos");

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
                            item.setJumlahLove("");
                            item.setJumlahNix("");
                            item.setJumlahComment(amount_of_comment);
                            item.setContent_comment(content_comment);
                            item.setTimeStamp(tgl_comment);
                            item.setParent_id(parent_id);
                            item.setUserLike("");
                            item.setUserDislike("");
                            item.setFlag(false);
                            item.setPhotos(photos);

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
        String id_note = getIntent().getStringExtra("attachment_id");
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
                    progressDialog = UtilsPD.createProgressDialog(NoteFollowUpActivity.this);
                    progressDialog.show();
                }
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("attachment_id", params[2]);
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
                    Toast.makeText(NoteFollowUpActivity.this, "Check your internet problem.", Toast.LENGTH_LONG).show();
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
                data.put("attachment_id", params[2]);
                data.put("bc_user", params[3]);
                data.put("parent_id", params[4]);

                String result = profileSaveDescription.sendPostRequest(URL_LIST_NOTE_COMMENT_BRANCH, data);
                return result;
            }

            protected void onPostExecute(String s) {
                JSONArray dataJsonArr = null;
                JSONArray commentBranchJsonArr = null;
                String data = "";
                if (s.equals(null)) {
                    Toast.makeText(NoteFollowUpActivity.this, "Internet Problem.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        feedItems.clear();
                        JSONObject json = new JSONObject(s);
                        String id_note = json.getString("attachment_id");

                        dataJsonArr = json.getJSONArray("data");
                        for (int i = 0; i < dataJsonArr.length(); i++) {

                            JSONObject c = dataJsonArr.getJSONObject(i);
                            String id_comment = c.getString("id_comment");
                            String uid = c.getString("userid");
                            String profile_name = c.getString("profile_name");
                            String profile_photo = c.getString("profile_photo");
                            String amount_of_like = "";
                            if (c.has("amount_of_like"))
                                amount_of_like = c.getString("amount_of_like");
                            String amount_of_dislike = "";
                            if (c.has("amount_of_dislike"))
                                amount_of_dislike = c.getString("amount_of_dislike");
                            String amount_of_comment = c.getString("amount_of_comment");
                            String content_comment = c.getString("content_comment");
                            String tgl_comment = c.getString("tgl_comment");
                            String parent_id = c.getString("parent_id");
                            String userLike = "";
                            if (c.has("user_like"))
                                userLike = c.getString("user_like");
                            String userDislike = "";
                            if (c.has("user_dislike"))
                                userDislike = c.getString("user_dislike");
                            String photos = "[]";
                            if (c.has("photos"))
                                photos = c.getString("photos");

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
                            item.setPhotos(photos);


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
                    progressDialog = UtilsPD.createProgressDialog(NoteFollowUpActivity.this);
                    progressDialog.show();
                }
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("userid", params[4]);
                data.put("attachment_id", params[2]);
                data.put("comment_content", params[1]);
                data.put("id_comment", params[3]);
                String result = profileSaveDescription.sendPostRequest(URL_SEND_COMMENT_PERSONAL, data);
                return result;
            }

            protected void onPostExecute(String s) {
                progressDialog.dismiss();
                if (!s.equals("1")) {
                    Toast.makeText(NoteFollowUpActivity.this, "Check your internet connection", Toast.LENGTH_LONG).show();
                } else {
                    getListNotesCommentinCommentPersonal(userid, id_note, id_comment, bc_user);
                }
                super.onPostExecute(s);
            }
        }
        ambilGambar ru = new ambilGambar();
        ru.execute(userid, textComment, id_note, id_comment, bc_user);
    }

    private void sendCommentTestPersonal(final String userid, String textComment, final String id_note, final String bc_user) {
        class ambilGambar extends AsyncTask<String, Void, String> {
            ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
            String result = "";
            private ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                if (progressDialog == null) {
                    progressDialog = UtilsPD.createProgressDialog(NoteFollowUpActivity.this);
                    progressDialog.show();
                }
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("userid", params[3]);
                data.put("comment_content", params[1]);
                data.put("attachment_id", params[2]);
                String result = profileSaveDescription.sendPostRequest(URL_SEND_COMMENT_PERSONAL, data);
                return result;
            }

            protected void onPostExecute(String s) {
                progressDialog.dismiss();
                if (!s.equals("1")) {
                    Toast.makeText(NoteFollowUpActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
                } else {
                    getListNotesCommentSubmitPersonal(userid, id_note, bc_user);
                }
                super.onPostExecute(s);
            }
        }
        ambilGambar ru = new ambilGambar();
        ru.execute(userid, textComment, id_note, bc_user);
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
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("userid", params[0]);
                data.put("attachment_id", params[1]);
                data.put("bc_user", params[2]);
                String result = profileSaveDescription.sendPostRequest(URL_LIST_NOTE_COMMENT_PERSONAL, data);
                return result;
            }

            protected void onPostExecute(String s) {
                JSONArray dataJsonArr = null;
                String data = "";

                if (s.equals(null)) {
                    Toast.makeText(NoteFollowUpActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        feedItems.clear();
                        JSONObject json = new JSONObject(s);
                        String id_note = json.getString("attachment_id");

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

                    } catch (JSONException e) {
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
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("userid", params[0]);
                data.put("attachment_id", params[1]);
                data.put("id_comment", params[2]);
                String result = profileSaveDescription.sendPostRequest(URL_LIST_COMMENT_IN_COMMENT_PERSONAL, data);
                return result;
            }

            protected void onPostExecute(String s) {
                JSONArray dataJsonArr = null;
                JSONArray commentBranchJsonArr = null;
                String data = "";

                if (s.equals(null)) {
                    Toast.makeText(NoteFollowUpActivity.this, "Internet Problem.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        feedItems.clear();
                        JSONObject json = new JSONObject(s);
                        String id_note = json.getString("attachment_id");

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

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        ambilGambar ru = new ambilGambar();
        ru.execute(userid, id_note, id_comment);
    }

    private void getListNotesCommentinComment(String userid, final String id_note, final String id_comment, final String bc_user, final String idRoomTab) {
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
//                data.put("username_room", params[0]);
                data.put("attachment_id", params[1]);
                data.put("parent_id", params[2]);
                data.put("bc_user", params[3]);
                Log.w("isinya", params[2] + " -- " + params[1]);
//                data.put("id_rooms_tab", params[4]);
                String result = profileSaveDescription.sendPostRequest(URL_LIST_NOTE_COMMENT_BRANCH, data);
                return result;
            }

            protected void onPostExecute(String s) {
                Log.w("kenapaya", s);

                JSONArray dataJsonArr = null;
                JSONArray commentBranchJsonArr = null;
                String data = "";

                if (s.equals(null)) {
                    Toast.makeText(NoteFollowUpActivity.this, "Internet Problem.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        feedItems.clear();
                        JSONObject json = new JSONObject(s);
                        String id_note = json.getString("attachment_id");

                        dataJsonArr = json.getJSONArray("data");
                        for (int i = 0; i < dataJsonArr.length(); i++) {

                            JSONObject c = dataJsonArr.getJSONObject(i);

                            String id_comment = c.getString("id_comment");
                            String uid = c.getString("userid");
                            String profile_name = c.getString("profile_name");
                            String profile_photo = c.getString("profile_photo");
                            String amount_of_comment = c.getString("amount_of_comment");
                            String content_comment = c.getString("content_comment");
                            String tgl_comment = c.getString("tgl_comment");
                            String parent_id = c.getString("parent_id");
                            String photos = "[]";
                            if (c.has("photos"))
                                photos = c.getString("photos");


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
                            item.setJumlahLove("");
                            item.setJumlahNix("");
                            item.setJumlahComment(amount_of_comment);
                            item.setContent_comment(content_comment);
                            item.setTimeStamp(tgl_comment);
                            item.setParent_id(parent_id);
                            item.setUserLike("");
                            item.setUserDislike("");
                            item.setFlag(false);
                            item.setPhotos(photos);


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
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("userid", params[0]);
                data.put("attachment_id", params[1]);
                data.put("id_comment", params[2]);
                data.put("bc_user", params[3]);
                String result = profileSaveDescription.sendPostRequest(URL_LIST_COMMENT_IN_COMMENT_PERSONAL, data);
                return result;
            }

            protected void onPostExecute(String s) {
                JSONArray dataJsonArr = null;
                JSONArray commentBranchJsonArr = null;
                String data = "";
                if (s.equals(null)) {
                    Toast.makeText(NoteFollowUpActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        feedItems.clear();
                        JSONObject json = new JSONObject(s);
                        String id_note = json.getString("attachment_id");

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

                    } catch (JSONException e) {
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
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("userid", params[0]);
                data.put("attachment_id", params[1]);
                data.put("bc_user", params[2]);
                String result = profileSaveDescription.sendPostRequest(URL_LIST_NOTE_COMMENT_PERSONAL, data);
                return result;
            }

            protected void onPostExecute(String s) {
                JSONArray dataJsonArr = null;
                JSONArray commentBranchJsonArr = null;
                String data = "";

                if (s.equals(null)) {
                    Toast.makeText(NoteFollowUpActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        feedItems.clear();
                        JSONObject json = new JSONObject(s);
                        String id_note = json.getString("attachment_id");

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

                    } catch (JSONException e) {
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

    protected void takeImage() {
        /*String action = Intent.ACTION_GET_CONTENT;
        action = MediaStore.ACTION_IMAGE_CAPTURE;
        File f = MediaProcessingUtil
                .getOutputFile("jpeg");
        cameraFileOutput = f.getAbsolutePath();

        Intent i = new Intent();
        i.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(f));
        int req = REQ_CAMERA;
        i.setAction(action);
        startActivityForResult(i, req);*/

        if (PermissionsUtil.hasPermissions(this, CAMERA_PERMISSION)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(this.getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = ImageUtils.createImageFileManhera();
                } catch (IOException ex) {
                    showError(getString(R.string.comment_error_failed_write));
                }

                if (photoFile != null) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    } else {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                FileProvider.getUriForFile(this, Byonchat.getProviderAuthorities(), photoFile));
                    }
                    startActivityForResult(intent, REQ_CAMERA);
                }
            }
        } else {
            requestCameraPermission();
        }


        /*String action = Intent.ACTION_GET_CONTENT;
        Intent i = new Intent();
        action = MediaStore.ACTION_IMAGE_CAPTURE;
        File f = MediaProcessingUtil
                .getOutputFile("jpeg");
        cameraFileOutput = f.getAbsolutePath();
        i.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(f));
        i.setAction(action);
        startActivityForResult(i, REQ_CAMERA);*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CAMERA && resultCode == Activity.RESULT_OK) {
            try {
                File imageFile = FileUtils.from(Uri.parse(CacheManager.getInstance().getLastImagePath()));
                List<NotesPhoto> photos = new ArrayList<>();
                photos.add(new NotesPhoto(imageFile));

                Intent intent = new Intent(getApplicationContext(), ConfirmationSendFile.class);
                String jabberId = getIntent().getStringExtra("userid");
                intent.putParcelableArrayListExtra("photos", (ArrayList<NotesPhoto>) photos);
                intent.putExtra("name", jabberId);
                intent.putExtra("type", Message.TYPE_IMAGE);
                intent.putExtra("isFrom", "comment");
                startActivityForResult(intent, SEND_PICTURE_SINGLE_CONFIRMATION_REQUEST);

                /*if (decodeFile(cameraFileOutput)) {
                    final File f = new File(cameraFileOutput);
                    if (f.exists()) {
                        List<NotesPhoto> photos = new ArrayList<>();
                        photos.add(new NotesPhoto(f));
                        Intent intent = new Intent(getApplicationContext(), ConfirmationSendFile.class);
                        String jabberId = getIntent().getStringExtra("userid");
                        intent.putParcelableArrayListExtra("photos", (ArrayList<NotesPhoto>) photos);
                        intent.putExtra("name", jabberId);
                        intent.putExtra("type", Message.TYPE_IMAGE);
                        intent.putExtra("isFrom", "comment");
                        startActivityForResult(intent, SEND_PICTURE_SINGLE_CONFIRMATION_REQUEST);
                    }
                } else {
                    Toast.makeText(this, R.string.comment_error_failed_read_picture, Toast.LENGTH_SHORT).show();
                }*/
            } catch (Exception e) {
                showError(getString(R.string.comment_error_failed_read_picture));
                e.printStackTrace();
            }
        } else if (requestCode == REQ_GALLERY && resultCode == RESULT_OK && data != null) {
            images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
            StringBuilder sb = new StringBuilder();
            JSONArray jsonArray = new JSONArray();
            for (int i = 0, l = images.size(); i < l; i++) {
                sb.append(images.get(i).getPath() + "\n");
                jsonArray.put(images.get(i).getPath());
            }

            Intent i = new Intent(getApplicationContext(), ConfirmationSendFileFolllowup.class);
            i.putParcelableArrayListExtra("selected", images);
            i.putExtra("file", jsonArray.toString());
            i.putExtra("name", getIntent().getStringExtra("userid"));
            i.putExtra("type", Message.TYPE_IMAGE);
            i.putExtra("isFrom", "comment");
            startActivityForResult(i, SEND_PICTURE_CONFIRMATION_REQUEST);
        } else if (requestCode == SEND_PICTURE_CONFIRMATION_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                showError(getString(R.string.comment_error_failed_open_picture));
                return;
            }

            String captions = data.getStringExtra(ConfirmationSendFileFolllowup.EXTRA_CAPTIONS);
            List<NotesPhoto> photos = data.getParcelableArrayListExtra(ConfirmationSendFileFolllowup.EXTRA_PHOTOS);
            if (photos != null) {
                String text = "";
                for (NotesPhoto photo : photos) {
                    Log.w("berhasil", photo.getPhotoFile() + " -- " + photo.getContent());
                    text = photo.getContent();
//                    sendFile(qiscusPhoto.getPhotoFile(), captions.get(qiscusPhoto.getPhotoFile().getAbsolutePath()));
                }
                registerUser(text, photos);
            } else {
                showError(getString(R.string.comment_error_failed_read_picture));
            }
        } else if (requestCode == SEND_PICTURE_SINGLE_CONFIRMATION_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                showError(getString(R.string.comment_error_failed_open_picture));
                return;
            }

            String uri = data.getStringExtra(ConfirmationSendFile.EXTRA_PHOTOS);
            String captions = data.getStringExtra(ConfirmationSendFile.EXTRA_CAPTIONS);
            List<NotesPhoto> photos = data.getParcelableArrayListExtra(ConfirmationSendFileMultiple.EXTRA_PHOTOS);
            if (photos != null) {
                for (NotesPhoto photo : photos) {
                    Log.w("berhasil", photo.getPhotoFile() + " -- " + photo.getContent());
                }
                registerUser(captions, photos);
            } else {
                showError(getString(R.string.comment_error_failed_read_picture));
            }
        }
    }

    public boolean decodeFile(String path) {
        int orientation;
        try {
            if (path == null) {
                return false;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            Bitmap bm = BitmapFactory.decodeFile(path, o2);
            ExifInterface exif = new ExifInterface(path);
            orientation = exif
                    .getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Matrix m = new Matrix();
            if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
                m.postRotate(180);
                bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);
                final File f = new File(path);
                if (f.exists()) f.delete();
                try {
                    FileOutputStream out = new FileOutputStream(f);
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                m.postRotate(90);
                bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);
                final File f = new File(path);
                if (f.exists()) f.delete();
                try {
                    FileOutputStream out = new FileOutputStream(f);
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                m.postRotate(270);
                bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);
                final File f = new File(path);
                if (f.exists()) f.delete();
                try {
                    FileOutputStream out = new FileOutputStream(f);
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
            final File f = new File(path);
            if (f.exists()) f.delete();
            try {
                FileOutputStream out = new FileOutputStream(f);
                bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;

        } catch (Exception e) {
            return false;
        }

    }

    protected void requestCameraPermission() {
        if (!PermissionsUtil.hasPermissions(this, CAMERA_PERMISSION)) {
            PermissionsUtil.requestPermissions(this, getString(R.string.permission_request_title),
                    RC_CAMERA_PERMISSION, CAMERA_PERMISSION);
        }
    }

    public void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    public void start(String destination_id) {
        ImagePicker.create(this)
                .folderMode(true)
                .reset(true)
                .destination(destination_id)
                .imageTitle("Tap to select")
                .single()
                .multi()
                .limit(10)
                .showCamera(true)
                .imageDirectory("Camera")
                .origin(images)
                .start(REQ_GALLERY);
    }

    void sendComment1st(final String userid, final String textComment, final String id_note,
                        final String bc_user, final String idRoomTab, final List<NotesPhoto> photos) {

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, URL_SEND_COMMENT, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                getListNotesCommentSubmit(userid, id_note, bc_user, idRoomTab);
                progressDialog.dismiss();
                String resultResponse = new String(response.data);
                if (!resultResponse.equals("1"))
                    Toast.makeText(getApplicationContext(), "Check your internet connection.", Toast.LENGTH_SHORT).show();
                mRecyclerView.smoothScrollToPosition(0);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();

                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        Log.w("lol", response.toString());
                        String sks = response.optString("message");
                        //Log.w("fer",sks);
                        if (sks.equalsIgnoreCase("Failed")) {
                            //Log.w("waw","gagal");
                            Toast.makeText(NoteFollowUpActivity.this, "Upload Failed", Toast.LENGTH_LONG).show();
                        }
                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = /*message +*/ " Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            Toast.makeText(getApplicationContext(), "Upload Success", Toast.LENGTH_SHORT).show();
                            errorMessage = /*message + */" Check your inputs";
                            finish();
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = /*message + */" Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("attachment_id", id_note);
                params.put("bc_user", bc_user);
                if (textComment.equalsIgnoreCase("")) {
                    params.put("content", textComment);
                } else {
                    String content = textComment;
                    for (NotesPhoto photo : photos) {
                        if (!photo.getContent().equalsIgnoreCase("")) {
                            content = photo.getContent();
                        }
                    }
                    params.put("content", content);
                }

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                String key = new ValidationsKey().getInstance(getApplicationContext()).key(true);
                if (key == null) {
                    return null;
                }

                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + key);
                return params;
            }

            @Override
            protected Map<String, DataPart[]> getByteData() {
                Map<String, DataPart[]> params = new HashMap<>();

                if (photos != null) {
                    DataPart[] list = new DataPart[photos.size()];
                    int k = 0;
                    for (NotesPhoto photo : photos) {
                        Log.w("berhasil", photo.getPhotoFile() + " -- " + photo.getContent());

                        if (!photo.getPhotoFile().exists()) {
                            return null;
                        }

                        int size = (int) photo.getPhotoFile().length();
                        Log.w("hall", size + "");

                        byte[] bytes = new byte[size];
                        try {
                            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(photo.getPhotoFile()));
                            buf.read(bytes, 0, bytes.length);
                            buf.close();

                            list[k] = new DataPart(bytes.toString() + "_img.jpg", bytes, "image/jpeg");

                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        k++;
                    }
                    params.put("photos[]", list);
                } else {
                    showError(getString(R.string.comment_error_failed_read_picture));
                }

                return params;
            }
        };

        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
    }

    void sendCommentInComment(final String userid, final String textComment, final String id_note, final String id_comment,
                              final String bc_user, final String idRoomTab, final List<NotesPhoto> photos) {
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, URL_SEND_COMMENT, new com.android.volley.Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                getListNotesCommentTreeSubmit(userid, id_note, id_comment, bc_user, idRoomTab);
                progressDialog.dismiss();
                if (!resultResponse.equals("1"))
                    Toast.makeText(NoteFollowUpActivity.this, "Check your internet connection.", Toast.LENGTH_LONG).show();
                mRecyclerView.smoothScrollToPosition(0);
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();

                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        Log.w("lol", response.toString());
                        String sks = response.optString("message");
                        //Log.w("fer",sks);
                        if (sks.equalsIgnoreCase("Failed")) {
                            //Log.w("waw","gagal");
                            Toast.makeText(NoteFollowUpActivity.this, "Upload Failed", Toast.LENGTH_LONG).show();
                        }
                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = /*message +*/ " Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            Toast.makeText(getApplicationContext(), "Upload Success", Toast.LENGTH_SHORT).show();
                            errorMessage = /*message + */" Check your inputs";
                            finish();
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = /*message + */" Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("attachment_id", id_note);
                params.put("bc_user", bc_user);
                params.put("content", textComment);
                params.put("parent_id", id_comment);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                String key = new ValidationsKey().getInstance(getApplicationContext()).key(true);
                if (key == null) {
                    return null;
                }

                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + key);
                return params;
            }

            @Override
            protected Map<String, DataPart[]> getByteData() {
                Map<String, DataPart[]> params = new HashMap<>();

                if (photos != null) {
                    DataPart[] list = new DataPart[photos.size()];
                    int k = 0;
                    for (NotesPhoto photo : photos) {
                        Log.w("berhasil", photo.getPhotoFile() + " -- " + photo.getContent());

                        if (!photo.getPhotoFile().exists()) {
                            return null;
                        }

                        int size = (int) photo.getPhotoFile().length();
                        Log.w("hall", size + "");

                        byte[] bytes = new byte[size];
                        try {
                            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(photo.getPhotoFile()));
                            buf.read(bytes, 0, bytes.length);
                            buf.close();

                            list[k] = new DataPart(bytes.toString() + "_img.jpg", bytes, "image/jpeg");

                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        k++;
                    }
                    params.put("photos[]", list);
                } else {
                    showError(getString(R.string.comment_error_failed_read_picture));
                }
                return params;
            }
        };

        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
    }
}
