package com.byonchat.android;

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
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity;
import com.byonchat.android.Manhera.Manhera;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.helpers.ImageUtils;
import com.byonchat.android.local.Byonchat;
import com.byonchat.android.local.CacheManager;
import com.byonchat.android.model.Image;
import com.byonchat.android.personalRoom.NoteFeedImageView;
import com.byonchat.android.personalRoom.adapter.NoteCommentFollowUpListAdapter;
import com.byonchat.android.personalRoom.adapter.PhotosAdapter;
import com.byonchat.android.personalRoom.asynctask.ProfileSaveDescription;
import com.byonchat.android.personalRoom.model.CommentModel;
import com.byonchat.android.personalRoom.model.NotesPhoto;
import com.byonchat.android.personalRoom.view.RecyclerViewScrollListener;
import com.byonchat.android.personalRoom.viewHolder.AttBeforeAfterViewHolder;
import com.byonchat.android.personalRoom.viewHolder.AttMultipleViewHolder;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.utils.MediaProcessingUtil;
import com.byonchat.android.utils.MyLeadingMarginSpan2;
import com.byonchat.android.utils.PermissionsUtil;
import com.byonchat.android.utils.TouchImageView;
import com.byonchat.android.utils.Utility;
import com.byonchat.android.utils.UtilsPD;
import com.byonchat.android.utils.ValidationsKey;
import com.byonchat.android.videotrimmer.utils.FileUtils;
import com.byonchat.android.view.CircleProgressBar;
import com.byonchat.android.volley.VolleyMultipartRequest;
import com.byonchat.android.volley.VolleySingleton;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.byonchat.android.ConfirmationSendFileFolllowup.EXTRA_TEXT_CAPTIONS;

public class NoteCommentActivityV2 extends Constants implements EmojiconGridFragment.OnEmojiconClickedListener,
        EmojiconsFragment.OnEmojiconBackspaceClickedListener, DialogInterface.OnClickListener {

    private static final String TAG = NoteCommentActivityV2.class.getSimpleName();
    RecyclerView mRecyclerView;
    private List<CommentModel> feedItems;
    private NoteCommentFollowUpListAdapter adapter;
    EmojiconEditText mWriteComment;
    Button mButtonSend;
    Toolbar toolbar;
    CircleProgressBar vCircleProgress;
    private String URL_SEND_COMMENT = "https://bb.byonchat.com/bc_voucher_client/webservice/proses/repost_attachment.php";
    public final static String URL_LIST_NOTE_COMMENT = "https://bb.byonchat.com/bc_voucher_client/webservice/category_tab/list_attachment_comment.php";
    public final static String URL_LIST_NOTE_COMMENT_BRANCH = "https://bb.byonchat.com/bc_voucher_client/webservice/category_tab/list_attachment_comment_branch.php";

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
    private RelativeLayout vFrameHighlight;
    private ImageView vButtonGallery;
    private TextView mExpandButton;
    private ExpandableRelativeLayout mExpandLayout;
    private TextView mOverlayText;
    private TextView name, timestamp, statusMsg, mTotalLoves, mTotalComments, mHiddenComment, mLabelLoves;
    private NoteFeedImageView feedImageView;
    private LinearLayout mLoves, mComments, mLinearHiddenComment, mLoading, vBtNix, vBtLoves, dotA, dotB;
    private Target profilePic;
    public LinearLayout vFrameBeforeAfter;
    public ImageView vPhotoBefore, vPhotoAfter;
    private RecyclerView vRvPhotos;
    private PhotosAdapter photosAdapter;
    private TouchImageView vImgPreview;
    public ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener;
    private static int leftMargin = 0;
    private ArrayList<CommentModel> parentItem = new ArrayList<>();
    private ArrayList<NotesPhoto> notesPhotos = new ArrayList<>();
    protected Map<String, String> isJumlahComment;
    protected Map<String, String> isPosition;
    private boolean isPostComment = false;
    private String contentComment;
    public final static HashMap<String, String> scrolltobottom = new HashMap<>();
    public final static String EXTRA_SCROLL = "scroll_to_bottom";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pr_mynote_comment);

        if (savedInstanceState != null) {
            isJumlahComment = (Map<String, String>) savedInstanceState.getSerializable("saved_isJumlahComment");
            isPosition = (Map<String, String>) savedInstanceState.getSerializable("saved_isPosition");
        }

        if (isPosition == null) {
            isPosition = new HashMap<>();
        }

        if (isJumlahComment == null) {
            isJumlahComment = new HashMap<>();
        }

        toolbar = (Toolbar) findViewById(R.id.abMain);
        toolbar.setTitleTextColor(Color.WHITE);

        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        contentRoot = (LinearLayout) findViewById(R.id.contentRoot);
        mWriteComment = (EmojiconEditText) findViewById(R.id.writeComment);
        mButtonSend = (Button) findViewById(R.id.btnSend);
        mswipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        vCircleProgress = (CircleProgressBar) findViewById(R.id.progress);
        vFrameHighlight = (RelativeLayout) findViewById(R.id.frame_highlight);
        vButtonGallery = (ImageView) findViewById(R.id.button_gallery);

        vButtonGallery.setVisibility(View.VISIBLE);
        vButtonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getIntent().getExtras().containsKey("id_comment")) {
                    String userid = getIntent().getStringExtra("userid");
                    String id_note = getIntent().getStringExtra("id_note");
                    String id_comment = getIntent().getStringExtra("id_comment");
                    String id_task = getIntent().getStringExtra("id_task");
                    String bc_user = getIntent().getStringExtra("bc_user");
                    String idRoomTab = "";

                    if (getIntent().getExtras().containsKey("id_room_tab")) {
                        idRoomTab = getIntent().getStringExtra("id_room_tab");
                    }

                    Intent intent = new Intent(getApplicationContext(), GalleryTaskActivity.class);
                    intent.putExtra("userid", userid);
                    intent.putExtra("id_note", id_note);
                    intent.putExtra("id_comment", id_comment);
                    intent.putExtra("id_task", id_task);
                    intent.putExtra("bc_user", bc_user);
                    intent.putExtra("id_room_tab", idRoomTab);
                    intent.putExtra("color", getIntent().getStringExtra("color"));
                    startActivityForResult(intent, POST_BEFORE_AFTER);
                } else {
                    String userid = getIntent().getStringExtra("userid");
                    String id_note = getIntent().getStringExtra("id_note");
                    String bc_user = getIntent().getStringExtra("bc_user");
                    String id_task = getIntent().getStringExtra("id_task");
                    String idRoomTab = "";
                    if (getIntent().getExtras().containsKey("id_room_tab")) {
                        idRoomTab = getIntent().getStringExtra("id_room_tab");
                    }

                    Intent intent = new Intent(getApplicationContext(), GalleryTaskActivity.class);
                    intent.putExtra("userid", userid);
                    intent.putExtra("id_note", id_note);
                    intent.putExtra("id_task", id_task);
                    intent.putExtra("bc_user", bc_user);
                    intent.putExtra("id_room_tab", idRoomTab);
                    intent.putExtra("color", getIntent().getStringExtra("color"));
                    startActivityForResult(intent, POST_BEFORE_AFTER);
                }
            }
        });

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

        mRecyclerView.setLayoutManager(new LinearLayoutManager(NoteCommentActivityV2.this));
        feedItems = new ArrayList<CommentModel>();
        adapter = new NoteCommentFollowUpListAdapter(NoteCommentActivityV2.this, feedItems, getIntent().getStringExtra("id_task"));
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);

        adapter.setOnButtonClickListener(new NoteCommentFollowUpListAdapter.OnButtonClick() {
            @Override
            public void onButtonClick(int position) {
                CommentModel item = feedItems.get(position);
                if (item.getFlag()) {
                    Intent intent = new Intent(getApplicationContext(), NoteCommentActivityV2.class);
                    intent.putExtra("userid", item.getUserid());
                    intent.putExtra("id_note", item.getId_note());
                    intent.putExtra("id_comment", item.getId_comment());
                    intent.putExtra("bc_user", item.getMyuserid());
                    intent.putExtra("flag", item.getFlag());
                    intent.putExtra("position", position + "");
                    startActivityForResult(intent, COMMENT_TREE);
                } else {
                    Intent intent = new Intent(getApplicationContext(), NoteCommentActivityV2.class);
                    intent.putExtra("userid", item.getUserid());
                    intent.putExtra("id_note", item.getId_note());
                    intent.putExtra("id_comment", item.getId_comment());
                    intent.putExtra("id_task", getIntent().getStringExtra("id_task"));
                    intent.putExtra("bc_user", item.getMyuserid());
                    intent.putExtra("id_room_tab", item.getIdRoomTab());
                    intent.putExtra("color", item.getHeaderColor());
                    intent.putExtra("flag", item.getFlag());
                    intent.putExtra("position", position + "");
//                    intent.putParcelableArrayListExtra(EXTRA_PARENT, parentItems);
                    startActivityForResult(intent, COMMENT_TREE);
                }
            }
        });

        refreshItems(false);

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
                feedItems = new ArrayList<>();
                refreshItems(false);
            }
        });

        emojicons = (LinearLayout) findViewById(R.id.emojiconsLayout);
        btn_add_emoticon = (ImageButton) findViewById(R.id.btn_add_emoticon);
        btn_attach_file = (ImageButton) findViewById(R.id.btn_attach_file);

        btn_attach_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(NoteCommentActivityV2.this)
                        .setItems(R.array.attach_file, NoteCommentActivityV2.this)
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

    private boolean validateComment() {
        if (TextUtils.isEmpty(mWriteComment.getText())) {
            mButtonSend.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_error));
            return false;
        }
        return true;
    }


    private void loadMoreData(boolean isScroll) {

        adapter.showLoading(true);
        adapter.notifyDataSetChanged();

        if (getIntent().getExtras().containsKey("id_comment")) {
            String userid = getIntent().getStringExtra("userid");
            String id_note = getIntent().getStringExtra("id_note");
            String id_comment = getIntent().getStringExtra("id_comment");
            String id_task = getIntent().getStringExtra("id_task");
            String bc_user = getIntent().getStringExtra("bc_user");
            String idRoomTab = "";
            if (getIntent().getExtras().containsKey("id_room_tab")) {
                idRoomTab = getIntent().getStringExtra("id_room_tab");
            }

            Log.w("loadmoretree", feedItems.size() + "");
            getListCommentinCommentMore(userid, id_note, id_comment, bc_user, idRoomTab, isScroll);
        } else {
            String userid = getIntent().getStringExtra("userid");
            String id_note = getIntent().getStringExtra("id_note");
            String bc_user = getIntent().getStringExtra("bc_user");
            String id_task = getIntent().getStringExtra("id_task");
            String idRoomTab = "";
            if (getIntent().getExtras().containsKey("id_room_tab")) {
                idRoomTab = getIntent().getStringExtra("id_room_tab");
            }

            Log.w("loadmore", feedItems.size() + "");
            getListCommentMore(userid, idRoomTab, id_note, bc_user, URL_LIST_NOTE_COMMENT, isScroll);
        }
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

    public void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    void setupHighlight() {
        parentItem = new ArrayList<>();
        parentItem = getIntent().getParcelableArrayListExtra(EXTRA_PARENT);

        final CommentModel intentData = parentItem.get(0);

        CommentModel item = new CommentModel();
        item.setIdRoomTab(intentData.getIdRoomTab());
        item.setHeaderColor(intentData.getHeaderColor());
        item.setId_note(intentData.getId_note());
        item.setId_comment(intentData.getId_comment());
        item.setMyuserid(intentData.getMyuserid());
        item.setUserid(intentData.getUserid());
        item.setProfileName(intentData.getProfileName());
        item.setPhotos(intentData.getPhotos());
        item.setPhotoBefore(intentData.getPhotoBefore());
        item.setPhotoAfter(intentData.getPhotoAfter());

        item.setType(CommentModel.TYPE_HEADER);

        item.setProfile_photo(intentData.getProfile_photo());
        item.setJumlahLove("");
        item.setJumlahNix("");
        item.setJumlahComment(intentData.getJumlahComment());
        item.setContent_comment(intentData.getContent_comment());
        item.setTimeStamp(intentData.getTimeStamp());
        item.setParent_id(intentData.getParent_id());
        item.setUserLike("");
        item.setUserDislike("");
        item.setFlag(false);
        feedItems.add(item);
        adapter.notifyDataSetChanged();

        /*vFrameHighlight.setVisibility(View.VISIBLE);

        timestamp = (TextView) findViewById(R.id.timestamp);
        statusMsg = (TextView) findViewById(R.id.txtStatusMsg);
        profilePic = (Target) findViewById(R.id.profilePic);
        mTotalLoves = (TextView) findViewById(R.id.totalLoves);
        mTotalComments = (TextView) findViewById(R.id.totalComments);
        mHiddenComment = (TextView) findViewById(R.id.hiddenComment);
        feedImageView = (NoteFeedImageView) findViewById(R.id.feedImage1);
        mLoves = (LinearLayout) findViewById(R.id.btLoves);
        mComments = (LinearLayout) findViewById(R.id.btComment);
        mLinearHiddenComment = (LinearLayout) findViewById(R.id.LinearHiddenComment);
        mLoading = (LinearLayout) findViewById(R.id.LinearLoading);
        mLabelLoves = (TextView) findViewById(R.id.labelLoves);
        vBtNix = (LinearLayout) findViewById(R.id.btNix);
        vBtLoves = (LinearLayout) findViewById(R.id.btLoves);
        dotA = (LinearLayout) findViewById(R.id.dotA);
        dotB = (LinearLayout) findViewById(R.id.dotB);
        name = (TextView) findViewById(R.id.name);
        vRvPhotos = (RecyclerView) findViewById(R.id.rv_photos);
        vImgPreview = (TouchImageView) findViewById(R.id.img_preview);
        mExpandButton = (TextView) findViewById(R.id.expandButton);
        mExpandLayout = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout);
        mOverlayText = (TextView) findViewById(R.id.overlayText);
        vFrameBeforeAfter = (LinearLayout) findViewById(R.id.frame_BeforeAfter);
        vPhotoBefore = (ImageView) findViewById(R.id.photo_before);
        vPhotoAfter = (ImageView) findViewById(R.id.photo_after);

        mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mExpandLayout.move(mOverlayText.getHeight(), 0, null);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    mOverlayText.getViewTreeObserver().removeGlobalOnLayoutListener(mGlobalLayoutListener);
                } else {
                    mOverlayText.getViewTreeObserver().removeOnGlobalLayoutListener(mGlobalLayoutListener);
                }
            }
        };

        vBtNix.setVisibility(View.GONE);
        vBtLoves.setVisibility(View.GONE);
        dotA.setVisibility(View.GONE);
        dotB.setVisibility(View.GONE);

        if (parentItem.size() > 0) {
            final CommentModel item = parentItem.get(0);

            if (!item.getPhotoBefore().equalsIgnoreCase("null")) {
                vFrameBeforeAfter.setVisibility(View.VISIBLE);

                Manhera.getInstance().get()
                        .load(item.getPhotoBefore())
                        .dontAnimate()
                        .into(vPhotoBefore);

                Manhera.getInstance().get()
                        .load(item.getPhotoAfter())
                        .dontAnimate()
                        .into(vPhotoAfter);


                vPhotoBefore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!item.getPhotoBefore().equalsIgnoreCase("")) {
                            Intent intent = new Intent(getApplicationContext(), ZoomImageViewActivity.class);
                            intent.putExtra(ZoomImageViewActivity.KEY_FILE, item.getPhotoBefore());
                            getApplicationContext().startActivity(intent);
                        }
                    }
                });

                vPhotoAfter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!item.getPhotoAfter().equalsIgnoreCase("")) {
                            Intent intent = new Intent(getApplicationContext(), ZoomImageViewActivity.class);
                            intent.putExtra(ZoomImageViewActivity.KEY_FILE, item.getPhotoAfter());
                            getApplicationContext().startActivity(intent);
                        }
                    }
                });

            } else if (!item.getPhotos().equalsIgnoreCase("[]")) {
                try {
                    JSONArray jPhotos = new JSONArray(item.getPhotos());
                    if (jPhotos.length() == 1) {
                        try {
                            JSONArray json = new JSONArray(item.getPhotos());
                            if (json.length() == 1) {
                                vImgPreview.setVisibility(View.VISIBLE);
                                JSONObject jData = json.getJSONObject(0);
                                Picasso.with(getApplicationContext()).load(jData.getString("photo")).into(vImgPreview);
                            } else {
                                vImgPreview.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (jPhotos.length() > 1) {
                        vImgPreview.setVisibility(View.GONE);
                        notesPhotos = new ArrayList<>();
                        mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                        vRvPhotos.setLayoutManager(mLayoutManager);
                        vRvPhotos.setItemAnimator(new DefaultItemAnimator());

                        try {
                            JSONArray json = new JSONArray(item.getPhotos());
                            if (json.length() > 1) {

                                for (int j = 0; j < json.length(); j++) {
                                    JSONObject jData = json.getJSONObject(j);
                                    File file = new File(jData.getString("photo"));
                                    NotesPhoto phot = new NotesPhoto(file, "", jData.getString("photo"));
                                    notesPhotos.add(phot);
                                }

                                photosAdapter = new PhotosAdapter(getApplicationContext(), notesPhotos);
                                vRvPhotos.setAdapter(photosAdapter);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (jPhotos.length() == 0) {
                        item.setType(CommentModel.TYPE_TEXT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                vFrameBeforeAfter.setVisibility(View.GONE);
            }

            mLinearHiddenComment.setVisibility(View.GONE);

            mTotalLoves.setText(item.getJumlahLove());
            mTotalComments.setText(item.getJumlahComment());

            Drawable dIcon = getApplicationContext().getResources().getDrawable(R.drawable.news_top_left);
            leftMargin = dIcon.getIntrinsicWidth();

            String nama;
            if (item.getProfileName().equalsIgnoreCase(null) || item.getProfileName().equalsIgnoreCase("")) {
                nama = item.getUserid();
            } else {
                nama = item.getProfileName();
            }
            SpannableString titleHeader = new SpannableString(Html.fromHtml("<b>" + nama + "</b>"));
            titleHeader.setSpan(new MyLeadingMarginSpan2(1, leftMargin), 0, titleHeader.length(), 0);
            name.setText(titleHeader);

            SpannableString update = new SpannableString(Html.fromHtml("Updates on: " + item.getTimeStamp()));
            update.setSpan(new MyLeadingMarginSpan2(1, leftMargin), 0, update.length(), 0);
            timestamp.setText(update);

            SpannableString pesan = new SpannableString(Html.fromHtml(item.getContent_comment()));
            pesan.setSpan(new MyLeadingMarginSpan2(1, leftMargin), 0, pesan.length(), 0);
            statusMsg.setText(pesan);
            mOverlayText.setText(pesan);

            mOverlayText.getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);

            mExpandButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    statusMsg.setVisibility(View.VISIBLE);
                    mExpandLayout.expand();
                    mExpandButton.setVisibility(View.GONE);
                    mOverlayText.setVisibility(View.GONE);
                }
            });

            mExpandButton.post(new Runnable() {
                @Override
                public void run() {
                    if (statusMsg.getLineCount() > 3) {
                        mExpandLayout.setVisibility(View.VISIBLE);
                        mExpandButton.setVisibility(View.VISIBLE);
                    } else {
                        mExpandLayout.setVisibility(View.GONE);
                        mExpandButton.setVisibility(View.GONE);
                    }
                }
            });

            if (item.getProfile_photo() != null) {
                Picasso.with(getApplicationContext()).load(item.getProfile_photo())
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .into(profilePic);
            } else {
                Picasso.with(getApplicationContext()).load(R.drawable.ic_no_photo)
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .into(profilePic);
            }

            feedImageView.setVisibility(View.GONE);
        }*/
    }

    protected void takeImage() {
        String action = Intent.ACTION_GET_CONTENT;
        action = MediaStore.ACTION_IMAGE_CAPTURE;
        File f = MediaProcessingUtil
                .getOutputFile("jpeg");
        cameraFileOutput = f.getAbsolutePath();

        Intent i = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            i.putExtra(MediaStore.EXTRA_OUTPUT,
                    FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", f));
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            i.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(f));
        }
        int req = REQ_CAMERA;
        i.setAction(action);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        startActivityForResult(i, req);

        /*if (PermissionsUtil.hasPermissions(this, CAMERA_PERMISSION)) {
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
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", photoFile));
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } else {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                FileProvider.getUriForFile(this, Byonchat.getProviderAuthorities(), photoFile));
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder.build());
                    }
                    startActivityForResult(intent, REQ_CAMERA);
                }
            }
        } else {
            requestCameraPermission();
        }*/
    }

    protected void requestCameraPermission() {
        if (!PermissionsUtil.hasPermissions(this, CAMERA_PERMISSION)) {
            PermissionsUtil.requestPermissions(this, getString(R.string.permission_request_title),
                    RC_CAMERA_PERMISSION, CAMERA_PERMISSION);
        }
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

    private void scrollListConversationToBottom() {
        mRecyclerView.scrollToPosition(feedItems.size() - 1);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(mWriteComment);
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(mWriteComment, emojicon);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (emojicons.getVisibility() == View.GONE) {
                if (isPostComment) {
                    Intent data = new Intent();
                    if (getIntent().getExtras().containsKey("id_comment")) {
//                        isJumlahComment.put(EXTRA_TEXT_JUMLAH_COMMENT, String.valueOf(feedItems.size()));
                        data.putExtra("userid", getIntent().getStringExtra("userid"));
                        data.putExtra("id_note", getIntent().getStringExtra("id_note"));
                        data.putExtra("id_task", getIntent().getStringExtra("id_task"));
                        data.putExtra("bc_user", getIntent().getStringExtra("bc_user"));
                        String idRoomTab = "", position = "";
                        if (getIntent().getExtras().containsKey("id_room_tab")) {
                            idRoomTab = getIntent().getStringExtra("id_room_tab");
                            data.putExtra("id_room_tab", getIntent().getStringExtra("id_room_tab"));
                        }
                        if (getIntent().getExtras().containsKey("position")) {
                            data.putExtra("position", getIntent().getStringExtra("position"));
                        }
                        data.putExtra(EXTRA_TEXT_JUMLAH_COMMENT, feedItems.size() + "");
                        data.putExtra(EXTRA_TEXT_CONTENT_COMMENT, contentComment);
//                        data.putExtra(EXTRA_JUMLAH_COMMENT, (HashMap<String, String>) isJumlahComment);
                        setResult(RESULT_OK, data);
                    }
                }

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("saved_isPosition", (HashMap<String, String>) isPosition);
        outState.putSerializable("saved_isJumlahComment", (HashMap<String, String>) isJumlahComment);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CAMERA && resultCode == Activity.RESULT_OK) {
            /*try {
                File imageFile = FileUtils.from(Uri.parse(CacheManager.getInstance().getLastImagePath()));
                List<NotesPhoto> photos = new ArrayList<>();
                photos.add(new NotesPhoto(imageFile));

                String jabberId = getIntent().getStringExtra("userid");

                startActivityForResult(ConfirmationSendFileV2.generateIntent(this,
                        jabberId, photos, Message.TYPE_IMAGE),
                        SEND_PICTURE_SINGLE_CONFIRMATION_REQUEST);
            } catch (Exception e) {
                showError(getString(R.string.comment_error_failed_read_picture));
                e.printStackTrace();
            }*/
            if (decodeFile(cameraFileOutput)) {
                final File f = new File(cameraFileOutput);
                if (f.exists()) {
                    List<NotesPhoto> photos = new ArrayList<>();
                    photos.add(new NotesPhoto(f));
                    String jabberId = getIntent().getStringExtra("userid");
//                        Intent intent = new Intent(getApplicationContext(), ConfirmationSendFile.class);
//                        intent.putParcelableArrayListExtra("photos", (ArrayList<NotesPhoto>) photos);
//                        intent.putExtra("name", jabberId);
//                        intent.putExtra("type", Message.TYPE_IMAGE);
//                        intent.putExtra("isFrom", "comment");
//                        startActivityForResult(intent, SEND_PICTURE_SINGLE_CONFIRMATION_REQUEST);
                    startActivityForResult(ConfirmationSendFileV2.generateIntent(this,
                            jabberId, photos, Message.TYPE_IMAGE),
                            SEND_PICTURE_SINGLE_CONFIRMATION_REQUEST);
                }
            } else {
                Toast.makeText(this, R.string.comment_error_failed_read_picture, Toast.LENGTH_SHORT).show();
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
            Utility.hideKeyboard(getApplicationContext(), mWriteComment);
            if (data == null) {
                showError(getString(R.string.comment_error_failed_open_picture));
                return;
            }

            Map<String, String> captions = (Map<String, String>)
                    data.getSerializableExtra(ConfirmationSendFileV2.EXTRA_CAPTIONS);
            List<NotesPhoto> photos = data.getParcelableArrayListExtra(ConfirmationSendFileFolllowup.EXTRA_PHOTOS);
            postComment(captions, photos);
            /*if (photos != null) {
                for (NotesPhoto photo : photos) {
//                    Log.w("uploadMultiple", photo.getPhotoFile() + " -- " + photo.getContent() + " -- " +);
                }
                Log.w("uploadMultiple", photos.size() + " -- " + captions.get(EXTRA_TEXT_CAPTIONS));
            } else {
                showError(getString(R.string.comment_error_failed_read_picture));
            }*/
        } else if (requestCode == SEND_PICTURE_SINGLE_CONFIRMATION_REQUEST && resultCode == Activity.RESULT_OK) {
            Utility.hideKeyboard(getApplicationContext(), mWriteComment);
            if (data == null) {
                showError(getString(R.string.comment_error_failed_open_picture));
                return;
            }

            Map<String, String> captions = (Map<String, String>)
                    data.getSerializableExtra(ConfirmationSendFileV2.EXTRA_CAPTIONS);
            List<NotesPhoto> photos = data.getParcelableArrayListExtra(ConfirmationSendFileMultiple.EXTRA_PHOTOS);
            postComment(captions, photos);
            /*if (photos != null) {
                for (NotesPhoto photo : photos) {
                    Log.w("uploadSingle", photos.size() + " --" + captions.get(photo.getPhotoFile().getAbsolutePath()));
                }
            } else {
                showError(getString(R.string.comment_error_failed_read_picture));
            }*/
        } else if (requestCode == POST_BEFORE_AFTER && resultCode == Activity.RESULT_OK) {
            Utility.hideKeyboard(getApplicationContext(), mWriteComment);
            refreshItems(false);

//            if (data.getExtras().containsKey("id_comment")) {
//                String userid = data.getStringExtra("userid");
//                String id_note = data.getStringExtra("id_note");
//                String id_comment = data.getStringExtra("id_comment");
//                String id_task = data.getStringExtra("id_task");
//                String bc_user = data.getStringExtra("bc_user");
//                String idRoomTab = "";
//                if (data.getExtras().containsKey("id_room_tab")) {
//                    idRoomTab = data.getStringExtra("id_room_tab");
//                }
//                getListCommentinComment(userid, id_note, id_comment, bc_user, idRoomTab, false);
//
//            } else {
//                String userid = data.getStringExtra("userid");
//                String id_note = data.getStringExtra("id_note");
//                String bc_user = data.getStringExtra("bc_user");
//                String id_task = data.getStringExtra("id_task");
//                String idRoomTab = "";
//                if (data.getExtras().containsKey("id_room_tab")) {
//                    idRoomTab = data.getStringExtra("id_room_tab");
//                }
//                getListComment(userid, idRoomTab, id_note, bc_user, URL_LIST_NOTE_COMMENT, false);
//
//            }
        } else if (requestCode == COMMENT_TREE && resultCode == Activity.RESULT_OK) {
            Utility.hideKeyboard(getApplicationContext(), mWriteComment);
//            CommentModel model = feedItems.get(Integer.valueOf(position));
//            model.setJumlahComment(jumlahcomment);
//            adapter.update(model);

            int position = Integer.valueOf(data.getStringExtra("position"));
            String jumlahcomment = data.getStringExtra(EXTRA_TEXT_JUMLAH_COMMENT);
            String contentcomment = data.getStringExtra(EXTRA_TEXT_CONTENT_COMMENT);

            CommentModel item = feedItems.get(position);
            item.setJumlahComment(jumlahcomment);
            item.setComment2(contentcomment);
            feedItems.set(position, item);

            adapter.notifyDataSetChanged();

//            refreshItems(false);

            /*Log.w("balikkomen", "jalan");
            if (data.getExtras().containsKey("id_comment")) {
                String userid = data.getStringExtra("userid");
                String id_note = data.getStringExtra("id_note");
                String id_comment = data.getStringExtra("id_comment");
                String id_task = data.getStringExtra("id_task");
                String bc_user = data.getStringExtra("bc_user");
                String idRoomTab = "";
                if (data.getExtras().containsKey("id_room_tab")) {
                    idRoomTab = data.getStringExtra("id_room_tab");
                }
                getListCommentinComment(userid, id_note, id_comment, bc_user, idRoomTab, true);

            } else {
                String userid = data.getStringExtra("userid");
                String id_note = data.getStringExtra("id_note");
                String bc_user = data.getStringExtra("bc_user");
                String id_task = data.getStringExtra("id_task");
                String idRoomTab = "";
                if (data.getExtras().containsKey("id_room_tab")) {
                    idRoomTab = data.getStringExtra("id_room_tab");
                }
                getListComment(userid, idRoomTab, id_note, bc_user, URL_LIST_NOTE_COMMENT, true);
            }*/
        } else if (resultCode == Activity.RESULT_CANCELED) {

        }
    }

    void postComment(Map<String, String> captions, List<NotesPhoto> photos) {
        if (getIntent().getExtras().containsKey("id_comment")) {
            String userid = getIntent().getStringExtra("userid");
            String id_note = getIntent().getStringExtra("id_note");
            String id_comment = getIntent().getStringExtra("id_comment");
            String bc_user = getIntent().getStringExtra("bc_user");
            String idRoomTab = "";
            if (getIntent().getExtras().containsKey("id_room_tab")) {
                idRoomTab = getIntent().getStringExtra("id_room_tab");
            }
            progressDialog = UtilsPD.createProgressDialog(NoteCommentActivityV2.this);
            progressDialog.show();
            postComment(userid, captions, id_note, bc_user, idRoomTab, id_comment, photos);
        } else {
            String userid = getIntent().getStringExtra("userid");
            String id_note = getIntent().getStringExtra("id_note");
            String bc_user = getIntent().getStringExtra("bc_user");
            String idRoomTab = "";
            if (getIntent().getExtras().containsKey("id_room_tab")) {
                idRoomTab = getIntent().getStringExtra("id_room_tab");
            }
            progressDialog = UtilsPD.createProgressDialog(NoteCommentActivityV2.this);
            progressDialog.show();
            postComment(userid, captions, id_note, bc_user, idRoomTab, "", photos);
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    void onItemsLoadComplete() {
        mswipeRefreshLayout.setRefreshing(false);
        contentRoot.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                if (isPostComment) {
                    Intent data = new Intent();
                    if (getIntent().getExtras().containsKey("id_comment")) {
//                        isJumlahComment.put(EXTRA_JUMLAH_COMMENT, String.valueOf(feedItems.size()));
                        data.putExtra("userid", getIntent().getStringExtra("userid"));
                        data.putExtra("id_note", getIntent().getStringExtra("id_note"));
                        data.putExtra("id_task", getIntent().getStringExtra("id_task"));
                        data.putExtra("bc_user", getIntent().getStringExtra("bc_user"));
                        String idRoomTab = "", position = "";
                        if (getIntent().getExtras().containsKey("id_room_tab")) {
                            idRoomTab = getIntent().getStringExtra("id_room_tab");
                            data.putExtra("id_room_tab", getIntent().getExtras().containsKey("id_room_tab"));
                        }
                        if (getIntent().getExtras().containsKey("position")) {
                            data.putExtra("position", getIntent().getStringExtra("position"));
                        }
                        data.putExtra(EXTRA_TEXT_JUMLAH_COMMENT, feedItems.size() + "");
                        data.putExtra(EXTRA_TEXT_CONTENT_COMMENT, contentComment);
                        setResult(RESULT_OK, data);
                    }/* else {
                        data.putExtra("userid", getIntent().getStringExtra("userid"));
                        data.putExtra("id_note", getIntent().getStringExtra("id_note"));
                        data.putExtra("id_task", getIntent().getStringExtra("id_task"));
                        data.putExtra("bc_user", getIntent().getStringExtra("bc_user"));
                        if (getIntent().getExtras().containsKey("id_room_tab")) {
                            data.putExtra("id_room_tab", getIntent().getExtras().containsKey("id_room_tab"));
                        }
                        if (getIntent().getExtras().containsKey("position")) {
                            data.putExtra("position", getIntent().getExtras().containsKey("position"));
                        }
                        data.putExtra("jumlahComment", feedItems.size());
                        if (isPostComment)
                            setResult(RESULT_OK, data);
                    }*/
                    finish();
                } else {
                    onBackPressed();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void refreshItems(boolean isScroll) {
        if (adapter.getItemCount() > 0) {
            feedItems.clear();
            adapter.notifyDataSetChanged();
        }
        if (!personal) {
            if (getIntent().getExtras().containsKey("id_comment")) {
                String userid = getIntent().getStringExtra("userid");
                String id_note = getIntent().getStringExtra("id_note");
                String id_comment = getIntent().getStringExtra("id_comment");
                String id_task = getIntent().getStringExtra("id_task");
                String bc_user = getIntent().getStringExtra("bc_user");
                String idRoomTab = "", position = "";
                if (getIntent().getExtras().containsKey("id_room_tab")) {
                    idRoomTab = getIntent().getStringExtra("id_room_tab");
                }
                if (getIntent().getExtras().containsKey("position")) {
                    position = getIntent().getStringExtra("position");
                }

//                setupHighlight();

                getListCommentinComment(userid, id_note, id_comment, bc_user, idRoomTab, isScroll);
            } else {
                String userid = getIntent().getStringExtra("userid");
                String id_note = getIntent().getStringExtra("id_note");
                String bc_user = getIntent().getStringExtra("bc_user");
                String id_task = getIntent().getStringExtra("id_task");
                String idRoomTab = "", position = "";
                if (getIntent().getExtras().containsKey("id_room_tab")) {
                    idRoomTab = getIntent().getStringExtra("id_room_tab");
                }
                if (getIntent().getExtras().containsKey("position")) {
                    position = getIntent().getStringExtra("position");
                }

                getListComment(userid, idRoomTab, id_note, bc_user, URL_LIST_NOTE_COMMENT, isScroll);
            }
        } else {
            if (getIntent().getExtras().containsKey("id_comment")) {
                String userid = getIntent().getStringExtra("userid");
                String id_note = getIntent().getStringExtra("id_note");
                String id_comment = getIntent().getStringExtra("id_comment");
                String bc_user = getIntent().getStringExtra("bc_user");
//                getListNotesCommentinCommentPersonal(userid, id_note, id_comment, bc_user);
            } else {
                String userid = getIntent().getStringExtra("userid");
                String id_note = getIntent().getStringExtra("id_note");
                String bc_user = getIntent().getStringExtra("bc_user");
//                getListNotesCommentPersonal(userid, id_note, bc_user);
            }
        }
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
                sendCommentinComment(userid, textComment, id_note, id_comment, bc_user, idRoomTab);
            } else {
                String userid = getIntent().getStringExtra("userid");
                String id_note = getIntent().getStringExtra("id_note");
                String bc_user = getIntent().getStringExtra("bc_user");
                String idRoomTab = "";
                if (getIntent().getExtras().containsKey("id_room_tab")) {
                    idRoomTab = getIntent().getStringExtra("id_room_tab");
                }
                sendCommentSubmit(userid, textComment, id_note, bc_user, idRoomTab);
            }
        } else {
            if (getIntent().getExtras().containsKey("id_comment")) {
                String userid = getIntent().getStringExtra("userid");
                String id_note = getIntent().getStringExtra("id_note");
                String id_comment = getIntent().getStringExtra("id_comment");
                String bc_user = getIntent().getStringExtra("bc_user");
//                sendCommentTreePersonal(userid, textComment, id_note, id_comment, bc_user);
            } else {
                String userid = getIntent().getStringExtra("userid");
                String id_note = getIntent().getStringExtra("id_note");
                String bc_user = getIntent().getStringExtra("bc_user");
//                sendCommentTestPersonal(userid, textComment, id_note, bc_user);
            }
        }

    }

    private void sendCommentSubmit(final String userid, final String textComment, final String id_note, final String bc_user, final String idRoomTab) {
        class ambilGambar extends AsyncTask<String, Void, String> {
            ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
            String result = "";
            private ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                if (progressDialog == null) {
                    progressDialog = UtilsPD.createProgressDialog(NoteCommentActivityV2.this);
                    progressDialog.show();
                }
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("attachment_id", params[2]);
                data.put("bc_user", params[3]);
                data.put("content", params[4]);

                String result = profileSaveDescription.sendPostRequest(URL_SEND_COMMENT, data);
                return result;
            }

            protected void onPostExecute(String s) {
                Utility.hideKeyboard(getApplicationContext(), mWriteComment);
                getListCommentSubmit(userid, id_note, bc_user, idRoomTab);
                progressDialog.dismiss();
                if (!s.equals("1")) {
                    Toast.makeText(NoteCommentActivityV2.this, "Check your internet problem.", Toast.LENGTH_LONG).show();
                } else {
                    scrolltobottom.put(EXTRA_SCROLL, "scroll");
                }
                super.onPostExecute(s);
            }
        }
        ambilGambar ru = new ambilGambar();
        ru.execute(userid, idRoomTab, id_note, bc_user, textComment);
    }

    private void sendCommentinComment(final String userid, final String textComment, final String id_note, final String id_comment, final String bc_user, final String id_room_tab) {
        class ambilGambar extends AsyncTask<String, Void, String> {
            ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
            String result = "";
            private ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                if (progressDialog == null) {
                    progressDialog = UtilsPD.createProgressDialog(NoteCommentActivityV2.this);
                    progressDialog.show();
                }
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("attachment_id", params[2]);
                data.put("bc_user", params[3]);
                data.put("parent_id", params[4]);
                data.put("content", params[5]);
                String result = profileSaveDescription.sendPostRequest(URL_SEND_COMMENT, data);
                return result;
            }

            protected void onPostExecute(String s) {
                Utility.hideKeyboard(getApplicationContext(), mWriteComment);
                getListCommentinCommentSubmit(userid, id_note, id_comment, bc_user, id_room_tab);
                progressDialog.dismiss();
                if (!s.equals("1")) {
                    Toast.makeText(NoteCommentActivityV2.this, "Check your internet problem.", Toast.LENGTH_LONG).show();
                } else {
                    scrolltobottom.put(EXTRA_SCROLL, "scroll");
                }
                super.onPostExecute(s);
            }
        }
        ambilGambar ru = new ambilGambar();
        ru.execute(userid, id_room_tab, id_note, bc_user, id_comment, textComment);
    }

    private void getListCommentSubmit(String userid, String id_note, final String bc_user, final String idRoomTab) {
        class ambilGambar extends AsyncTask<String, Void, String> {
            ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
            String result = "";

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                Log.w("jalancommentsubmit", "jalan");
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("username_room", params[0]);
                data.put("id_rooms_tab", params[1]);
                data.put("attachment_id", params[2]);
                data.put("bc_user", params[3]);
                String result = profileSaveDescription.sendPostRequest(URL_LIST_NOTE_COMMENT, data);
                return result;
            }

            protected void onPostExecute(String s) {
                onItemsLoadComplete();
                JSONArray dataJsonArr = null;
                String data = "";

                if (s.equals(null)) {
                    Toast.makeText(NoteCommentActivityV2.this, "Internet Problem.", Toast.LENGTH_SHORT).show();
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
                            String photo_before = "", photo_after = "", photos = "";
                            if (c.has("photo_before")) {
                                photo_before = c.getString("photo_before");
                            }
                            if (c.has("photo_after")) {
                                photo_after = c.getString("photo_after");
                            }
                            if (c.has("photos")) {
                                photos = c.getString("photos");
                            }
                            CommentModel item = new CommentModel();
                            item.setIdRoomTab(idRoomTab);
                            item.setHeaderColor(color);

                            item.setId_note(id_note);
                            item.setId_comment(id_comment);
                            item.setMyuserid(bc_user);
                            item.setIdRoomTab(idRoomTab);
                            item.setUserid(uid);
                            item.setProfileName(profile_name);
                            item.setPhotos(photos);
                            item.setPhotoBefore(photo_before);
                            item.setPhotoAfter(photo_after);
                            Log.w("tipeget", photo_before + " -- " + photos);

                            if (!photo_before.equalsIgnoreCase("null")) {
                                item.setType(CommentModel.TYPE_ATT_BEFORE_AFTER);
                            } else if (!photos.equalsIgnoreCase("[]")) {
                                try {
                                    JSONArray jPhotos = new JSONArray(c.getString("photos"));
                                    if (jPhotos.length() == 1) {
                                        item.setType(CommentModel.TYPE_ATT_SINGLE);
                                    } else if (jPhotos.length() > 1) {
                                        item.setType(CommentModel.TYPE_ATT_MULTIPLE);
                                    } else if (jPhotos.length() == 0) {
                                        item.setType(CommentModel.TYPE_TEXT);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                item.setType(CommentModel.TYPE_TEXT);
                            }

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

                            feedItems.add(item);
                        }
                        adapter.notifyDataSetChanged();
                        if (feedItems.size() <= 20)
                            mRecyclerView.smoothScrollToPosition(feedItems.size());

                        mRecyclerView.addOnScrollListener(new RecyclerViewScrollListener() {
                            @Override
                            public void onScrollUp() {

                            }

                            @Override
                            public void onScrollDown() {

                            }

                            @Override
                            public void onLoadMore() {
                                loadMoreData(true);
                            }
                        });

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

    private void getListCommentinCommentSubmit(String userid, String id_note, String id_comment, final String bc_user, final String tab_id) {
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
                data.put("attachment_id", params[2]);
                data.put("parent_id", params[4]);
                data.put("bc_user", params[3]);

                String result = profileSaveDescription.sendPostRequest(URL_LIST_NOTE_COMMENT_BRANCH, data);
                return result;
            }

            protected void onPostExecute(String s) {
                onItemsLoadComplete();
                JSONArray dataJsonArr = null;
                JSONArray commentBranchJsonArr = null;
                String data = "";
                if (s.equals(null)) {
                    Toast.makeText(NoteCommentActivityV2.this, "Internet Problem.", Toast.LENGTH_SHORT).show();
                } else {
                    isPostComment = true;
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
                            String amount_of_like = "", amount_of_dislike = "", amount_of_comment = "", userLike = "", userDislike = "";
                            if (c.has("amount_of_like"))
                                amount_of_like = c.getString("amount_of_like");
                            if (c.has("amount_of_dislike"))
                                amount_of_dislike = c.getString("amount_of_dislike");
                            if (c.has("amount_of_comment"))
                                amount_of_comment = c.getString("amount_of_comment");
                            String content_comment = c.getString("content_comment");
                            String tgl_comment = c.getString("tgl_comment");
                            String parent_id = c.getString("parent_id");
                            if (c.has("user_like"))
                                userLike = c.getString("user_like");
                            if (c.has("user_dislike"))
                                userDislike = c.getString("user_dislike");
                            String photo_before = "", photo_after = "", photos = "";
                            if (c.has("photo_before")) {
                                photo_before = c.getString("photo_before");
                            }
                            if (c.has("photo_after")) {
                                photo_after = c.getString("photo_after");
                            }
                            if (c.has("photos")) {
                                photos = c.getString("photos");
                            }
                            CommentModel item = new CommentModel();
                            item.setIdRoomTab(tab_id);
                            item.setHeaderColor(color);
                            item.setId_note(id_note);
                            item.setId_comment(id_comment);
                            item.setMyuserid(bc_user);
                            item.setUserid(uid);
                            item.setProfileName(profile_name);
                            item.setPhotos(photos);
                            item.setPhotoBefore(photo_before);
                            item.setPhotoAfter(photo_after);

                            if (!photo_before.equalsIgnoreCase("null")) {
                                item.setType(CommentModel.TYPE_ATT_BEFORE_AFTER);
                            } else if (!photos.equalsIgnoreCase("[]")) {
                                try {
                                    JSONArray jPhotos = new JSONArray(c.getString("photos"));
                                    if (jPhotos.length() == 1) {
                                        item.setType(CommentModel.TYPE_ATT_SINGLE);
                                    } else if (jPhotos.length() > 1) {
                                        item.setType(CommentModel.TYPE_ATT_MULTIPLE);
                                    } else if (jPhotos.length() == 0) {
                                        item.setType(CommentModel.TYPE_TEXT);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                item.setType(CommentModel.TYPE_TEXT);
                            }
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
                            contentComment = content_comment;
                            feedItems.add(item);
                        }
                        adapter.notifyDataSetChanged();
                        if (feedItems.size() <= 20)
                            mRecyclerView.smoothScrollToPosition(feedItems.size());

                        mRecyclerView.addOnScrollListener(new RecyclerViewScrollListener() {
                            @Override
                            public void onScrollUp() {

                            }

                            @Override
                            public void onScrollDown() {

                            }

                            @Override
                            public void onLoadMore() {
                                loadMoreData(true);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        ambilGambar ru = new ambilGambar();
        ru.execute(userid, tab_id, id_note, bc_user, id_comment);
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

    public void getListComment(String username_room, String id_rooms_tab, String id_note, final String bc_user, String url, final boolean isScroll) {
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
                data.put("attachment_id", params[2]);
                data.put("bc_user", params[3]);
                String result = profileSaveDescription.sendPostRequest(params[4], data);
                return result;
            }

            protected void onPostExecute(String s) {
                onItemsLoadComplete();
                Log.w("resultComment", s);
                JSONArray dataJsonArr = null;
                JSONArray commentBranchJsonArr = null;
                String data = "";
                if (s.equals(null)) {
                    Toast.makeText(NoteCommentActivityV2.this, "Internet Problem.", Toast.LENGTH_SHORT).show();
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
                            String photo_before = "", photo_after = "", photos = "";
                            if (c.has("photo_before")) {
                                photo_before = c.getString("photo_before");
                            }
                            if (c.has("photo_after")) {
                                photo_after = c.getString("photo_after");
                            }
                            if (c.has("photos")) {
                                photos = c.getString("photos");
                            }

                            CommentModel item = new CommentModel();
                            item.setIdRoomTab(idRoomTab);
                            item.setHeaderColor(color);
                            item.setId_note(id_note);
                            item.setId_comment(id_comment);
                            item.setMyuserid(bc_user);
                            item.setUserid(uid);
                            item.setProfileName(profile_name);
                            item.setPhotos(photos);
                            item.setPhotoBefore(photo_before);
                            item.setPhotoAfter(photo_after);

                            if (!photo_before.equalsIgnoreCase("null")) {
                                item.setType(CommentModel.TYPE_ATT_BEFORE_AFTER);
                            } else if (!photos.equalsIgnoreCase("[]")) {
                                try {
                                    JSONArray jPhotos = new JSONArray(c.getString("photos"));
                                    if (jPhotos.length() == 1) {
                                        item.setType(CommentModel.TYPE_ATT_SINGLE);
                                    } else if (jPhotos.length() > 1) {
                                        item.setType(CommentModel.TYPE_ATT_MULTIPLE);
                                    } else if (jPhotos.length() == 0) {
                                        item.setType(CommentModel.TYPE_TEXT);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                item.setType(CommentModel.TYPE_TEXT);
                            }

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
                        if (isScroll) {
                            mRecyclerView.scrollToPosition(feedItems.size());
                        }

                        mRecyclerView.addOnScrollListener(new RecyclerViewScrollListener() {
                            @Override
                            public void onScrollUp() {

                            }

                            @Override
                            public void onScrollDown() {

                            }

                            @Override
                            public void onLoadMore() {
                                loadMoreData(isScroll);
                            }
                        });

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

    public void getListCommentinComment(String userid, String id_note, String id_comment, final String bc_user,
                                        final String idRoomTab, final boolean isScroll) {
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
                data.put("attachment_id", params[1]);
                data.put("parent_id", params[2]);
                data.put("bc_user", params[3]);
                data.put("id_rooms_tab", params[4]);
                String result = profileSaveDescription.sendPostRequest(URL_LIST_NOTE_COMMENT_BRANCH, data);
                return result;
            }

            protected void onPostExecute(String s) {
                onItemsLoadComplete();
                Log.w("resultCommentBranch", s);
                JSONArray dataJsonArr = null;
                JSONArray commentBranchJsonArr = null;
                String data = "";

                if (s.equals(null)) {
                    Toast.makeText(NoteCommentActivityV2.this, "Internet Problem.", Toast.LENGTH_SHORT).show();
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
                            String photo_before = "", photo_after = "", photos = "";
                            if (c.has("photo_before")) {
                                photo_before = c.getString("photo_before");
                            }
                            if (c.has("photo_after")) {
                                photo_after = c.getString("photo_after");
                            }
                            if (c.has("photos")) {
                                photos = c.getString("photos");
                            }

                            CommentModel item = new CommentModel();
                            item.setIdRoomTab(idRoomTab);
                            item.setHeaderColor(color);
                            item.setId_note(id_note);
                            item.setId_comment(id_comment);
                            item.setMyuserid(bc_user);
                            item.setUserid(uid);
                            item.setProfileName(profile_name);
                            item.setPhotos(photos);
                            item.setPhotoBefore(photo_before);
                            item.setPhotoAfter(photo_after);

                            if (!photo_before.equalsIgnoreCase("null")) {
                                item.setType(CommentModel.TYPE_ATT_BEFORE_AFTER);
                            } else if (!photos.equalsIgnoreCase("[]")) {
                                try {
                                    JSONArray jPhotos = new JSONArray(c.getString("photos"));
                                    if (jPhotos.length() == 1) {
                                        item.setType(CommentModel.TYPE_ATT_SINGLE);
                                    } else if (jPhotos.length() > 1) {
                                        item.setType(CommentModel.TYPE_ATT_MULTIPLE);
                                    } else if (jPhotos.length() == 0) {
                                        item.setType(CommentModel.TYPE_TEXT);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                item.setType(CommentModel.TYPE_TEXT);
                            }

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

                        if (isScroll) {
                            mRecyclerView.scrollToPosition(feedItems.size());
                        }

                        mRecyclerView.addOnScrollListener(new RecyclerViewScrollListener() {
                            @Override
                            public void onScrollUp() {

                            }

                            @Override
                            public void onScrollDown() {

                            }

                            @Override
                            public void onLoadMore() {
                                loadMoreData(isScroll);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        ambilGambar ru = new ambilGambar();
        ru.execute(userid, id_note, id_comment, bc_user, idRoomTab);
    }

    void postComment(final String userid, final Map<String, String> captions, final String id_note,
                     final String bc_user, final String idRoomTab, final String parent_id, final List<NotesPhoto> photos) {

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, URL_SEND_COMMENT, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                if (!parent_id.equalsIgnoreCase(""))
                    getListCommentinCommentSubmit(userid, id_note, parent_id, bc_user, idRoomTab);
                else
                    getListCommentSubmit(userid, id_note, bc_user, idRoomTab);
                progressDialog.dismiss();
                String resultResponse = new String(response.data);
                Log.w("resultSuccess", resultResponse);
                if (!resultResponse.equals("1"))
                    Toast.makeText(getApplicationContext(), "Check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utility.hideKeyboard(getApplicationContext(), mWriteComment);
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
                        Log.w("resultError", response.toString());
                        String sks = response.optString("message");
                        if (sks.equalsIgnoreCase("Failed")) {
                            Toast.makeText(NoteCommentActivityV2.this, "Upload Failed", Toast.LENGTH_LONG).show();
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
                params.put("content", captions.get(EXTRA_TEXT_CAPTIONS));
                if (!parent_id.equalsIgnoreCase(""))
                    params.put("parent_id", parent_id);
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
                        if (!photo.getPhotoFile().exists()) {
                            return null;
                        }

                        int size = (int) photo.getPhotoFile().length();

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

    public void getListCommentMore(String username_room, String id_rooms_tab, String id_note, final String bc_user, String url, final boolean isScroll) {
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
                data.put("attachment_id", params[2]);
                data.put("bc_user", params[3]);
                data.put("last_page", feedItems.size() + "");
                String result = profileSaveDescription.sendPostRequest(params[4], data);
                return result;
            }

            protected void onPostExecute(String s) {
                Log.w("resultComment", s);
                JSONArray dataJsonArr = null;
                JSONArray commentBranchJsonArr = null;
                String data = "";
                if (s.equals(null)) {
                    adapter.showLoading(false);
                    Toast.makeText(NoteCommentActivityV2.this, "Internet Problem.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
//                        feedItems.clear();
                        JSONObject json = new JSONObject(s);
                        String id_note = json.getString("attachment_id");

                        dataJsonArr = json.getJSONArray("data");
                        if (dataJsonArr.length() > 0) {
                            List<CommentModel> items = new ArrayList<>();
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
                                String photo_before = "", photo_after = "", photos = "";
                                if (c.has("photo_before")) {
                                    photo_before = c.getString("photo_before");
                                }
                                if (c.has("photo_after")) {
                                    photo_after = c.getString("photo_after");
                                }
                                if (c.has("photos")) {
                                    photos = c.getString("photos");
                                }

                                CommentModel item = new CommentModel();
                                item.setIdRoomTab(idRoomTab);
                                item.setHeaderColor(color);
                                item.setId_note(id_note);
                                item.setId_comment(id_comment);
                                item.setMyuserid(bc_user);
                                item.setUserid(uid);
                                item.setProfileName(profile_name);
                                item.setPhotos(photos);
                                item.setPhotoBefore(photo_before);
                                item.setPhotoAfter(photo_after);

                                if (!photo_before.equalsIgnoreCase("null")) {
                                    item.setType(CommentModel.TYPE_ATT_BEFORE_AFTER);
                                } else if (!photos.equalsIgnoreCase("[]")) {
                                    try {
                                        JSONArray jPhotos = new JSONArray(c.getString("photos"));
                                        if (jPhotos.length() == 1) {
                                            item.setType(CommentModel.TYPE_ATT_SINGLE);
                                        } else if (jPhotos.length() > 1) {
                                            item.setType(CommentModel.TYPE_ATT_MULTIPLE);
                                        } else if (jPhotos.length() == 0) {
                                            item.setType(CommentModel.TYPE_TEXT);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    item.setType(CommentModel.TYPE_TEXT);
                                }

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
                                items.add(item);
                            }
                            feedItems.addAll(items);
                            adapter.setItems(feedItems);
                            adapter.showLoading(false);
                        } else {
                            adapter.showLoading(false);
                        }

                        JSONArray ja = new JSONArray();
                        JSONObject last = dataJsonArr.getJSONObject(dataJsonArr.length() - 1);
                        ja.put(last);
                        updateData(feedItems.size() + "", ja);


                    } catch (JSONException e) {
                        adapter.showLoading(false);
                        e.printStackTrace();
                    }
                }

                runOnUiThread(new Runnable() {
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });

                if (isScroll) {
                    mRecyclerView.scrollToPosition(feedItems.size());
                }
                mRecyclerView.addOnScrollListener(new RecyclerViewScrollListener() {
                    @Override
                    public void onScrollUp() {

                    }

                    @Override
                    public void onScrollDown() {

                    }

                    @Override
                    public void onLoadMore() {
                        loadMoreData(isScroll);
                    }
                });
            }
        }
        ambilGambarSatu ru = new ambilGambarSatu();
        ru.execute(username_room, id_rooms_tab, id_note, bc_user, url);
    }

    public void getListCommentinCommentMore(String userid, String id_note, String id_comment, final String bc_user,
                                            final String idRoomTab, final boolean isScroll) {
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
                data.put("attachment_id", params[1]);
                data.put("parent_id", params[2]);
                data.put("bc_user", params[3]);
                data.put("id_rooms_tab", params[4]);
                data.put("last_page", feedItems.size() + "");
                String result = profileSaveDescription.sendPostRequest(URL_LIST_NOTE_COMMENT_BRANCH, data);
                return result;
            }

            protected void onPostExecute(String s) {
                adapter.showLoading(false);
                Log.w("resultCommentBranch", s);
                JSONArray dataJsonArr = null;
                JSONArray commentBranchJsonArr = null;
                String data = "";

                if (s.equals(null)) {
                    Toast.makeText(NoteCommentActivityV2.this, "Internet Problem.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
//                        feedItems.clear();
                        JSONObject json = new JSONObject(s);
                        String id_note = json.getString("attachment_id");

                        dataJsonArr = json.getJSONArray("data");
                        if (dataJsonArr.length() > 0) {
                            List<CommentModel> items = new ArrayList<>();
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
                                String photo_before = "", photo_after = "", photos = "";
                                if (c.has("photo_before")) {
                                    photo_before = c.getString("photo_before");
                                }
                                if (c.has("photo_after")) {
                                    photo_after = c.getString("photo_after");
                                }
                                if (c.has("photos")) {
                                    photos = c.getString("photos");
                                }

                                CommentModel item = new CommentModel();
                                item.setIdRoomTab(idRoomTab);
                                item.setHeaderColor(color);
                                item.setId_note(id_note);
                                item.setId_comment(id_comment);
                                item.setMyuserid(bc_user);
                                item.setUserid(uid);
                                item.setProfileName(profile_name);
                                item.setPhotos(photos);
                                item.setPhotoBefore(photo_before);
                                item.setPhotoAfter(photo_after);

                                if (!photo_before.equalsIgnoreCase("null")) {
                                    item.setType(CommentModel.TYPE_ATT_BEFORE_AFTER);
                                } else if (!photos.equalsIgnoreCase("[]")) {
                                    try {
                                        JSONArray jPhotos = new JSONArray(c.getString("photos"));
                                        if (jPhotos.length() == 1) {
                                            item.setType(CommentModel.TYPE_ATT_SINGLE);
                                        } else if (jPhotos.length() > 1) {
                                            item.setType(CommentModel.TYPE_ATT_MULTIPLE);
                                        } else if (jPhotos.length() == 0) {
                                            item.setType(CommentModel.TYPE_TEXT);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    item.setType(CommentModel.TYPE_TEXT);
                                }

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
                                items.add(item);
                            }
                            feedItems.addAll(items);
                            adapter.setItems(feedItems);
                            adapter.showLoading(false);
                        } else {
                            adapter.showLoading(false);
                        }

//                        mRecyclerView.smoothScrollToPosition(feedItems.size());
                    } catch (JSONException e) {
                        adapter.showLoading(false);
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });

                if (isScroll) {
                    mRecyclerView.scrollToPosition(feedItems.size());
                }

                mRecyclerView.addOnScrollListener(new RecyclerViewScrollListener() {
                    @Override
                    public void onScrollUp() {

                    }

                    @Override
                    public void onScrollDown() {

                    }

                    @Override
                    public void onLoadMore() {
                        loadMoreData(isScroll);
                    }
                });
            }
        }
        ambilGambar ru = new ambilGambar();
        ru.execute(userid, id_note, id_comment, bc_user, idRoomTab);
    }
}
