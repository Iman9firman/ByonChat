package com.byonchat.android;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Intents.Insert;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.communication.MessengerConnectionService.MessengerConnectionBinder;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.communication.NotificationReceiver;
import com.byonchat.android.createMeme.FilteringImage;
import com.byonchat.android.createMeme.PhotoSortrActivity;
import com.byonchat.android.list.AttachmentAdapter;
import com.byonchat.android.list.AttachmentAdapter.AttachmentMenuItem;
import com.byonchat.android.list.ConversationAdapter;
import com.byonchat.android.list.utilLoadImage.ImageLoaderFromSD;
import com.byonchat.android.list.utilLoadImage.TextLoader;
import com.byonchat.android.model.Image;
import com.byonchat.android.provider.BlockListDB;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.ChatParty;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.ContactBot;
import com.byonchat.android.provider.Group;
import com.byonchat.android.provider.Interval;
import com.byonchat.android.provider.IntervalDB;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.Skin;
import com.byonchat.android.provider.TimeLineDB;
import com.byonchat.android.shortcutBadger.ShortcutBadgeException;
import com.byonchat.android.shortcutBadger.ShortcutBadger;
import com.byonchat.android.utils.DialogUtil;
import com.byonchat.android.utils.GPSTracker;
import com.byonchat.android.utils.HttpHelper;
import com.byonchat.android.utils.ImageFilePath;
import com.byonchat.android.utils.MediaProcessingUtil;
import com.byonchat.android.utils.StreamService;
import com.byonchat.android.utils.UploadService;
import com.byonchat.android.utils.Utility;
import com.byonchat.android.utils.UtilsRadio;
import com.byonchat.android.utils.Validations;
import com.byonchat.android.utils.ValidationsKey;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.vdopia.ads.lw.LVDOAd;
import com.vdopia.ads.lw.LVDOAdListener;
import com.vdopia.ads.lw.LVDOAdRequest;
import com.vdopia.ads.lw.LVDOAdSize;
import com.vdopia.ads.lw.LVDOAdView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

import static com.byonchat.android.communication.NotificationReceiver.NOTIFY_ID_CONV;

public class ConversationActivity extends AppCompatActivity implements
        ServiceConnection, EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener, LVDOAdListener {
    public final static String KEY_TITLE = "TITLE";
    public final static String KEY_CONVERSATION_TYPE = "CONVERSATION_TYPE";
    public final static String KEY_JABBER_ID = "JABBER_ID";
    public final static String KEY_FILE_TO_SEND = "FILE_TO_SEND";
    public final static String KEY_MESSAGE_FORWARD = "KEY_MESSAGE_FORWARD";
    public final static String FILE_UPLOAD_URL = "https://" + MessengerConnectionService.FILE_SERVER + "/v1/upload";
    public final static String FILE_DOWNLOAD_URL = "https://" + MessengerConnectionService.FILE_SERVER + "/v1/download/rd/";
    public final static String FILE_DOWNLOAD_TUMB_URL = "https://" + MessengerConnectionService.FILE_SERVER + "/v1/download/th/";
    public final static String URL_ADD_BLOCK = "https://" + MessengerConnectionService.HTTP_SERVER + "/blocklist/add.php";
    public final static String URL_ADD_ROOM = "https://" + MessengerConnectionService.HTTP_SERVER + "/room/";
    public static final int CONVERSATION_TYPE_PRIVATE = 0;
    public static final int CONVERSATION_TYPE_GROUP = 1;

    private static final String BUNDLE_KEY_FILE_TO_SEND = "BUNDLE_FILE_TO_SEND";
    private static final String BUNDLE_KEY_CURREQ = "CURREQ";
    private static final String BUNDLE_KEY_CAMERA_FILEOUTPUT = "CAMERA_FILEOUTPUT";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "MMM dd, yyyy", Locale.getDefault());
    private static final String MENU_GALLERY_TITLE = "Gallery";
    private static final int LIMIT_MESSAGE_LOAD = 15;
    private static final int REQ_CAMERA = 1201;
    private static final int REQ_GALLERY = 1202;
    private static final int REQ_VIDEO = 1203;
    private static final int REQ_GALLERY_VIDEO = 1204;
    private static final int REQ_CREATE_CONTACT = 1205;
    private static final int REQ_MEME = 1206;
    private static final int REQ_GALLERY_MEME = 1207;
    private final int REQ_CODE_SPEECH_INPUT = 1208;
    private final int PLACE_PICKER_REQUEST = 1209;
    private int REQUEST_CODE_PICKER = 2000;

    private static final String SQL_SELECT_FAILED_MESSAGES = "SELECT * FROM "
            + Message.TABLE_NAME + " WHERE " + Message.DESTINATION
            + "=? AND status=" + Message.STATUS_FAILED;
    private static final String SQL_SELECT_TOTAL_MESSAGES = "SELECT count(*) total FROM "
            + Message.TABLE_NAME
            + " WHERE "
            + Message.DESTINATION
            + "=? OR "
            + Message.SOURCE + "=? ";
    private static final String SQL_SELECT_MESSAGES = "SELECT * FROM "
            + Message.TABLE_NAME + " WHERE (" + Message.DESTINATION + "=? OR "
            + Message.SOURCE + "=? ) AND " + Message.TYPE + " NOT IN ('" + Message.TYPE_READSTATUS /*+ "','"+Message.TYPE_TARIK+"*/ + "') ORDER BY " + Message.ID + " desc, " + Message.SEND_DATE
            + " LIMIT ? OFFSET ?;";
    private static final String SQL_UPDATE_MESSAGES = "UPDATE "
            + Message.TABLE_NAME + " SET status = " + Message.STATUS_DELIVERED + " WHERE " + Message.ID + " =?;";
    private static String SQL_SELECT_TOTAL_MESSAGES_UNREAD_ALL = "SELECT count(*) total FROM "
            + Message.TABLE_NAME
            + " WHERE status = ?";
    private static String SQL_SELECT_TOTAL_MESSAGES_UNREAD = "SELECT * FROM "
            + Message.TABLE_NAME
            + " WHERE status = ? AND " + Message.SOURCE + "=?;";
    private static final ArrayList<AttachmentMenuItem> attCameraItems;
    private static final ArrayList<AttachmentMenuItem> attVideoItems;
    private static final ArrayList<AttachmentMenuItem> attMemeItems;
    private MessengerConnectionBinder binder;
    private ChatParty destination;
    private String sourceAddr;
    private MessengerDatabaseHelper messengerHelper;
    private List<Object> conversations;
    private ConversationAdapter adapter;
    private EditText textMessage;
    private BroadcastHandler broadcastHandler = new BroadcastHandler();
    private ListView listConversation;
    private ImageButton btnSend;
    private Button btnAttachmentCamera;
    private Button btnAttachmentMapMarker;
    private Button btnAttachmentVideo;
    private Button btnAttachmentMeme;
    private LinearLayout emojicons;
    private String colorAttachment = "#005982";
    private ImageButton btnMic, btn_add_emoticon;
    private String lastDate = "";
    private AdapterContextMenuInfo itemContextSelected;
    private int loadOffset = 0;
    private int loadLimit = 15;
    private long totalMessages = 0;
    private LinearLayout convLayout;
    private LinearLayout linearBanner;
    private LinearLayout mainconLayout;
    private LinearLayout converVdovia;
    private LinearLayout converStreaming;
    private LinearLayout mainRadioStreaming;
    private LinearLayout mainVideoStreaming;
    private RelativeLayout mainVdovia;
    private RelativeLayout mainStreaming;
    private Button buttonNext;
    private ProgressBar progresVdopia;
    TextView nameRadio;
    TextView infoRadio;
    Button buttonPlay;
    ProgressBar progressRadio;
    private int conversationType;
    private List<Object> groupMessages;
    private ArrayList<AttachmentMenuItem> curAttItems;
    private int attCurReq = 0;
    private String cameraFileOutput;
    private String fileToSend;
    private String title = "", judul;
    BlockListDB blockListDB;
    ViewGroup header;
    private Button headerBtnLock, headerBtnAdd;
    protected ProgressDialog pdialog;
    private Thread splashTread;
    private Thread fileendTread;
    long totalSize = 0;
    LVDOAdView adView;
    LVDOAdView adViewBanner;
    LVDOAdView adViewBannerSmall;
    String titleTheme = "";
    GPSTracker gps;
    private Intent serviceIntent;
    private static boolean isStreaming = false;
    private boolean mBufferBroadcastIsRegistered;
    Toolbar toolbar;
    FrameLayout frameLayoutPicasso;
    Target logoToolbarPicasso;
    ImageView logoToolbar;
    TextView titleToolbar;
    LinearLayout mRevealView;
    boolean hidden = true;
    public TextLoader textLoader;
    TimeLineDB timeLineDB;
    ImageLoaderFromSD imageLoaderFromSD;
    public static ConversationActivity instance = null;
    private ArrayList<Image> images = new ArrayList<>();
    BotListDB botListDB = null;

    static {
        attCameraItems = new ArrayList<AttachmentMenuItem>();
        attCameraItems.add(new AttachmentMenuItem(R.drawable.ic_att_photo,
                "Camera"));
        attCameraItems.add(new AttachmentMenuItem(R.drawable.ic_att_gallery,
                MENU_GALLERY_TITLE));

        attVideoItems = new ArrayList<AttachmentMenuItem>();
        attVideoItems.add(new AttachmentMenuItem(R.drawable.ic_att_video,
                "Camcorder"));
        attVideoItems.add(new AttachmentMenuItem(R.drawable.ic_att_gallery,
                MENU_GALLERY_TITLE));

        attMemeItems = new ArrayList<AttachmentMenuItem>();
        attMemeItems.add(new AttachmentMenuItem(R.drawable.ic_att_photo,
                "Camera Meme"));
        attMemeItems.add(new AttachmentMenuItem(R.drawable.ic_att_gallery,
                MENU_GALLERY_TITLE));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_KEY_FILE_TO_SEND, fileToSend);
        outState.putInt(BUNDLE_KEY_CURREQ, attCurReq);
        outState.putString(BUNDLE_KEY_CAMERA_FILEOUTPUT, cameraFileOutput);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fileToSend = savedInstanceState.getString(BUNDLE_KEY_FILE_TO_SEND);
        attCurReq = savedInstanceState.getInt(BUNDLE_KEY_CURREQ);
        cameraFileOutput = savedInstanceState
                .getString(BUNDLE_KEY_CAMERA_FILEOUTPUT);
    }

    private void setBarTitle(String title, boolean show) {
        if (title.length() > 0) {
            JSONObject jObject = null;
            try {
                jObject = new JSONObject(title);
                String aa = jObject.getString("a");
                String bb = jObject.getString("b");
                judul = aa;
                titleToolbar.setText(aa);
                textLoader.DisplayImage(bb, titleToolbar);
                frameLayoutPicasso.setVisibility(View.GONE);
                logoToolbar.setVisibility(View.VISIBLE);
                imageLoaderFromSD.DeleteImage(MediaProcessingUtil.getProfilePic(bb), logoToolbar);

                Animation fadeOut = new AlphaAnimation(0, 1);
                fadeOut.setInterpolator(new AccelerateInterpolator());
                fadeOut.setDuration(500);
                logoToolbar.startAnimation(fadeOut);

                imageLoaderFromSD.DisplayImage(MediaProcessingUtil.getProfilePic(bb), logoToolbar, false);
                /*Picasso.with(this)
                        .load("https://"+MessengerConnectionService.F_SERVER+"/toboldlygowherenoonehasgonebefore/"+bb+".jpg")
                        .placeholder(R.drawable.ic_no_photo)
                        .into(logoToolbar);*/
            } catch (JSONException e) {
                titleToolbar.setText(title);
                judul = title;
            }
        }

        if (show) {
            titleToolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Contact c = (Contact) destination;
                    Intent intent = new Intent(getApplicationContext(), ViewProfileActivity.class);
                    intent.putExtra(ViewProfileActivity.KEY_JABBER_ID,
                            c.getJabberId());
                    intent.putExtra(ViewProfileActivity.KEY_REFERENCE,
                            ViewProfileActivity.REFERENCE_CONVERSATION);
                    startActivity(intent);
                }
            });
        }
    }

    private void setBarTitleAndImage(Contact c) {
        setBarTitle(c.getName(), true);
        judul = c.getName();
        /*Picasso.with(this)
                .load("https://"+MessengerConnectionService.F_SERVER+"/toboldlygowherenoonehasgonebefore/"+c.getJabberId()+".jpg")
                .placeholder(R.drawable.ic_no_photo)
                .into(logoToolbar);*/
        frameLayoutPicasso.setVisibility(View.GONE);
        logoToolbar.setVisibility(View.VISIBLE);
        imageLoaderFromSD.DeleteImage(MediaProcessingUtil.getProfilePic(c.getJabberId()), logoToolbar);

        Animation fadeOut = new AlphaAnimation(0, 1);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(500);
        logoToolbar.startAnimation(fadeOut);

        imageLoaderFromSD.DisplayImage(MediaProcessingUtil.getProfilePic(c.getJabberId()), logoToolbar, false);
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(textMessage, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(textMessage);
    }

    @Override
    public void finish() {
        super.finish();
        instance = null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

        instance = this;
       /*material desgin ga ada cek version
       if (!new Validations().getInstance(getApplicationContext()).getContentValidation(16).equalsIgnoreCase(getResources().getString(R.string.version))) {
            finish();
            Intent i = new Intent();
            i.setClass(getApplicationContext(), SplashScreen.class);
            startActivity(i);
        }*/
        if (timeLineDB == null) {
            timeLineDB = TimeLineDB.getInstance(getApplicationContext());
        }
        if (botListDB == null) {
            botListDB = BotListDB.getInstance(getApplicationContext());
        }

        imageLoaderFromSD = new ImageLoaderFromSD(getApplicationContext());
        conversationType = getIntent().getIntExtra(KEY_CONVERSATION_TYPE, 0);
        title = getIntent().getStringExtra(KEY_TITLE);
        final String destinationAddr = getIntent().getStringExtra(KEY_JABBER_ID);
        final String messageForward = getIntent().getStringExtra(KEY_MESSAGE_FORWARD);
        textLoader = new TextLoader(getApplicationContext());

        if (pdialog == null) {
            pdialog = new ProgressDialog(this);
            pdialog.setIndeterminate(true);
            pdialog.setMessage("Please wait a moment ..");
            pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }

        setContentView(R.layout.conversation);

        toolbar = (Toolbar) findViewById(R.id.abMain);
        setSupportActionBar(toolbar);
        frameLayoutPicasso = (FrameLayout) findViewById(R.id.frameLayoutPicasso);
        logoToolbarPicasso = (Target) findViewById(R.id.logo_toolbarPicasso);
        logoToolbar = (ImageView) findViewById(R.id.logo_toolbar);
        View view = findViewById(R.id.layout_back_button);
        titleToolbar = (TextView) findViewById(R.id.title_toolbar);
        mRevealView = (LinearLayout) findViewById(R.id.reveal_items);
        mRevealView.setVisibility(View.INVISIBLE);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        LayoutInflater inflater = getLayoutInflater();
        header = (ViewGroup) inflater.inflate(R.layout.conversation_view_addblock, listConversation,
                false);

        listConversation = (ListView) findViewById(R.id.listConversation);
        headerBtnLock = (Button) header.findViewById(R.id.headerBtnLock);
        headerBtnAdd = (Button) header.findViewById(R.id.headerBtnAdd);
        blockListDB = new BlockListDB(this);

        convLayout = (LinearLayout) findViewById(R.id.conversation_layout);
        mainconLayout = (LinearLayout) findViewById(R.id.conversation_layout_main);
        mainRadioStreaming = (LinearLayout) findViewById(R.id.mainRadioStreaming);
        mainVideoStreaming = (LinearLayout) findViewById(R.id.mainVideoStreaming);
        converVdovia = (LinearLayout) findViewById(R.id.converVdovia);
        converStreaming = (LinearLayout) findViewById(R.id.converStreaming);
        linearBanner = (LinearLayout) findViewById(R.id.linearBanner);
        mainVdovia = (RelativeLayout) findViewById(R.id.mainVdovia);
        mainStreaming = (RelativeLayout) findViewById(R.id.mainStreaming);
        nameRadio = (TextView) findViewById(R.id.nameRadio);
        infoRadio = (TextView) findViewById(R.id.infoRadio);
        buttonPlay = (Button) findViewById(R.id.buttonPlay);
        progressRadio = (ProgressBar) findViewById(R.id.progressRadio);
        buttonNext = (Button) findViewById(R.id.buttonNext);
        progresVdopia = (ProgressBar) findViewById(R.id.progresVdopia);

        IntervalDB db = new IntervalDB(this);
        db.open();
        Cursor cursorSelect = db.getSingleContact(4);
        if (cursorSelect.getCount() > 0) {
            String skin = cursorSelect.getString(cursorSelect.getColumnIndexOrThrow(IntervalDB.COL_TIME));
            Skin skins = null;
            Cursor c = db.getCountSkin();
            if (c.getCount() > 0) {
                skins = db.retriveSkinDetails(skin);
                colorAttachment = skins.getColor();
                BitmapDrawable bitmapDrawable = new BitmapDrawable(skins.getBackground());
                bitmapDrawable.setTileModeXY(android.graphics.Shader.TileMode.REPEAT, android.graphics.Shader.TileMode.REPEAT);
                convLayout.setBackground(bitmapDrawable);
                titleTheme = skins.getTitle();
                Bitmap back_bitmap = FilteringImage.headerColor(getWindow(), ConversationActivity.this, Color.parseColor(colorAttachment));
                BitmapDrawable back_draw = new BitmapDrawable(getResources(), back_bitmap);
//                logoToolbar.setImageDrawable(back_draw);
                logoToolbar.setVisibility(View.GONE);
                frameLayoutPicasso.setVisibility(View.VISIBLE);
                Picasso.with(this).load(Color.parseColor(colorAttachment))
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(logoToolbar);
                toolbar.setBackground(back_draw);

            }
            c.close();
        }
        cursorSelect.close();
        db.close();

        if (conversationType == CONVERSATION_TYPE_PRIVATE) {
            fileToSend = getIntent().getStringExtra(KEY_FILE_TO_SEND);
        }
        if (state != null) {
            fileToSend = state.getString(BUNDLE_KEY_FILE_TO_SEND);
            attCurReq = state.getInt(BUNDLE_KEY_CURREQ);
            cameraFileOutput = state.getString(BUNDLE_KEY_CAMERA_FILEOUTPUT);
        }

        /*Picasso.with(this).load(R.drawable.ic_no_photo).into(logoToolbar);*/
        /*frameLayoutPicasso.setVisibility(View.GONE);
        logoToolbar.setVisibility(View.VISIBLE);*/
        /*Picasso.with(this).load(R.drawable.ic_no_photo).into(logoToolbarPicasso);*/

        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(this);
        }
        sourceAddr = messengerHelper.getMyContact().getJabberId();
        if (conversationType == CONVERSATION_TYPE_PRIVATE) {
            destination = messengerHelper.getContact(destinationAddr);
            title = createAKA("fetching room... ", destinationAddr);


            String regex = "[0-9]+";

            if (new Validations().getInstance(getApplicationContext()).getShow(8) && destinationAddr.matches(regex)) {
                linearBanner.setVisibility(View.VISIBLE);
                adViewBannerSmall = new LVDOAdView(this, LVDOAdSize.BANNER, "3b471f77e0ad2dc65c375ab8f2f9eaec");
                LVDOAdRequest adRequest = new LVDOAdRequest();
                adViewBannerSmall.loadAd(adRequest);
                linearBanner.addView(adViewBannerSmall);
            }

            if (destination != null) {
                title = "";
                setBarTitleAndImage((Contact) destination);

            } else {
                if (!destinationAddr.matches(regex)) {
                    if (!destinationAddr.contains(titleTheme)) convLayout.setBackground(null);

                    title = Utility.roomName(getApplicationContext(), destinationAddr, true);
                    headerBtnLock.setVisibility(View.GONE);

                    Cursor cursor2 = botListDB.getSingle(destinationAddr.toLowerCase());
                    Bitmap iconBot = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                            R.drawable.ic_broadcasts);
                    if (cursor2.getCount() < 1) {
                        //disini
                        // listConversation.addHeaderView(header, null, false);
                        //    listConversation.removeHeaderView(header);
                    }

                    Picasso.with(this)
                            .load("https://" + MessengerConnectionService.HTTP_SERVER + "/mediafiles/" + destinationAddr + "_thumb.png") // image url goes here
                            .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                            .placeholder(R.drawable.ic_broadcasts)
                            .into(logoToolbarPicasso);
                    cursor2.close();
                } else {
                    listConversation.addHeaderView(header, null, false);
                }
                destination = new Contact("", destinationAddr, "");

            }

        } else {
            destination = messengerHelper.getGroup(destinationAddr);
            if (destination == null) {
                destination = new Group(title, destinationAddr,
                        Group.STATUS_OWNER);
            } else {
                setBarTitle(destination.getName(), false);
            }
            Bitmap iconGroup = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                    R.drawable.ic_group);
            Drawable iconGroup2 = new BitmapDrawable(getResources(), MediaProcessingUtil.getRoundedCornerBitmap(iconGroup, 10));
            Picasso.with(this).load(R.drawable.ic_group).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE);
        }

        setBarTitle(title, false);

        btnMic = (ImageButton) findViewById(R.id.btnMic);
        btnMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        if (conversations == null) {
            conversations = new ArrayList();

            adapter = new ConversationAdapter(this, this.getApplicationContext(), sourceAddr,
                    destinationAddr, conversations);
            adapter.setBtnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //  loadLimit += LIMIT_MESSAGE_LOAD;
                    // loadOffset += LIMIT_MESSAGE_LOAD;
                    //   refreshConversation();
                    //   scrollListConversationToTop();
                    // if(conversations.get(conversations.size()-1))
                    //  new ConversationLoadEarlier().execute();

                    if (listConversation.getHeaderViewsCount() == 1) {
                        Object obj = listConversation.getAdapter().getItem(2);
                        String value = obj.toString();
                        if (isValidDate(value)) {
                            conversations.remove(0);
                        }
                    } else {
                        Object obj = listConversation.getAdapter().getItem(1);
                        String value = obj.toString();
                        if (isValidDate(value)) {
                            conversations.remove(0);
                        }
                    }

                    loadOffset += LIMIT_MESSAGE_LOAD;
                    new ConversationLoadEarlier().execute();
                }
            });
        }

        ButtonClickListener btnClickListener = new ButtonClickListener();
        btnAttachmentCamera = (Button) findViewById(R.id.btn_addatt_camera);
        btnAttachmentCamera.setOnClickListener(btnClickListener);

        btnAttachmentMapMarker = (Button) findViewById(R.id.btn_addatt_location);
        btnAttachmentMapMarker.setOnClickListener(btnClickListener);

        btnAttachmentVideo = (Button) findViewById(R.id.btn_addatt_video);
        btnAttachmentVideo.setOnClickListener(btnClickListener);

        btnAttachmentMeme = (Button) findViewById(R.id.btn_addatt_meme);
        btnAttachmentMeme.setOnClickListener(btnClickListener);

        emojicons = (LinearLayout) findViewById(R.id.emojiconsLayout);
        btn_add_emoticon = (ImageButton) findViewById(R.id.btn_add_emoticon);
        btn_add_emoticon.setOnClickListener(btnClickListener);

        ArrayList<String> listblock = new ArrayList<String>();
        boolean block = false;

        blockListDB = new BlockListDB(this);
        blockListDB.open();
        listblock = blockListDB.getBlockList();
        blockListDB.close();

        for (String a : listblock) {
            if (destination.getJabberId().equalsIgnoreCase(a)) {
                block = true;
            }
        }

        if (block) {
            headerBtnLock.setText("Unblock");
        }

        headerBtnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAttc(false);
                blockContact();
            }
        });


        headerBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAttc(false);
                // My Click Stuff
                String regex = "[0-9]+";
                if (destination.getJabberId().matches(regex)) {

                    Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
                    intent.setType(Contacts.CONTENT_ITEM_TYPE);
                    intent.putExtra(Insert.PHONE, "+" + destination.getJabberId());
                    startActivityForResult(intent, REQ_CREATE_CONTACT);

                } else {
                    if (NetworkInternetConnectionStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
                        saveBot();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        listConversation.setAdapter(adapter);
        listConversation.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                showAttc(false);
                return false;
            }
        });
        toolbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                showAttc(false);
                return false;
            }
        });

        registerForContextMenu(listConversation);

        btnSend = (ImageButton) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(btnClickListener);
        if (attCurReq > 0) {
            showAttachmentDialog(attCurReq);
        }

        initText();
        //setMessageFormEnabled(false);

        if (messageForward != null) {
            textMessage.setText(messageForward);
        }

        String typeRooms = Utility.roomType(destinationAddr);
        if (typeRooms.length() > 0) {
            if (typeRooms.equalsIgnoreCase("U")) {

                splashTread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            synchronized (this) {
                                wait(200);
                            }

                        } catch (InterruptedException e) {
                        } finally {
                            long total = 0;
                            Cursor cursor = messengerHelper.query(
                                    SQL_SELECT_TOTAL_MESSAGES,
                                    new String[]{destination.getJabberId(),
                                            destination.getJabberId()});
                            int indexTotal = cursor.getColumnIndex("total");
                            while (cursor.moveToNext()) {
                                total = cursor.getLong(indexTotal);
                            }
                            cursor.close();

                            if (total == 0) {
                                if (isNetworkConnectionAvailable()) {
                                    if (btnSend.isEnabled()) {
                                        sendMessageMenu("99");
                                    }
                                }
                            }
                        }
                    }
                };

                splashTread.start();
            } else if (typeRooms.equalsIgnoreCase("V")) {
                mainVdovia.setVisibility(View.VISIBLE);
                mainconLayout.setVisibility(View.GONE);
                buttonNext = (Button) findViewById(R.id.buttonNext);
                progresVdopia = (ProgressBar) findViewById(R.id.progresVdopia);
                adView = new LVDOAdView(this, LVDOAdSize.IAB_MRECT, "f3e11705262aa8bba69cb58571772cf3");
                LVDOAdRequest adRequest = new LVDOAdRequest();
                adView.loadAd(adRequest);

                buttonNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // My Click Stuff
                        converVdovia.removeAllViews();
                        LVDOAdRequest adRequest = new LVDOAdRequest();
                        adView.loadAd(adRequest);
                        converVdovia.addView(adView);
                    }
                });
                converVdovia.addView(adView);
            } else if (typeRooms.equalsIgnoreCase("R")) {

                linearBanner.setVisibility(View.VISIBLE);
                adViewBannerSmall = new LVDOAdView(this, LVDOAdSize.BANNER, "3b471f77e0ad2dc65c375ab8f2f9eaec");
                LVDOAdRequest adRequest = new LVDOAdRequest();
                adViewBannerSmall.loadAd(adRequest);
                linearBanner.addView(adViewBannerSmall);

                Cursor cursorBot = botListDB.getSingle(destinationAddr);
                String link = "";
                String name = "";
                String desc = "";
                if (cursorBot.getCount() > 0) {
                    link = cursorBot.getString(cursorBot.getColumnIndexOrThrow(BotListDB.BOT_LINK));
                    name = cursorBot.getString(cursorBot.getColumnIndexOrThrow(BotListDB.BOT_NAME));
                    desc = cursorBot.getString(cursorBot.getColumnIndexOrThrow(BotListDB.BOT_DESC));
                } else {
                    finish();
                   /* showToast((String) getResources().getText(R.string.add_room_first));
                    Intent intent = new Intent(getApplicationContext(), SearchBotActivity.class);
                    intent.putExtra("search", title );
                    startActivity(intent);*/
                }
                cursorBot.close();

                serviceIntent = new Intent(this, StreamService.class);
                serviceIntent.putExtra("streaminglink", link);
                serviceIntent.putExtra("streamingName", name);
                serviceIntent.putExtra("streamingDesc", desc);
                isStreaming = UtilsRadio.getDataBooleanFromSP(this, UtilsRadio.IS_STREAM);
                if (isStreaming) {
                    buttonPlay.setBackgroundResource(R.drawable.ic_radio_stop);
                    if (new Validations().getInstance(getApplicationContext()).getPlayStreaming(destinationAddr) == false) {
                        buttonPlay.setBackgroundResource(R.drawable.ic_radio_play);
                        //  Toast.makeText(getApplicationContext(), "Stop Streaming..", Toast.LENGTH_SHORT).show();
                        stopStreaming();
                        isStreaming = false;
                        progressRadio.setVisibility(View.GONE);
                        UtilsRadio.setDataBooleanToSP(getApplicationContext(), UtilsRadio.IS_STREAM, false);
                    }
                }
                mainRadioStreaming.setVisibility(View.VISIBLE);
                progressRadio.setIndeterminate(true);
                progressRadio.setVisibility(View.GONE);
                nameRadio.setText(Utility.roomName(getApplicationContext(), name, true));
                infoRadio.setText(desc);
                buttonPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isStreaming) {
                            isStreaming = true;
                            startStreaming();
                            UtilsRadio.setDataBooleanToSP(getApplicationContext(), UtilsRadio.IS_STREAM, true);
                            buttonPlay.setBackgroundResource(R.drawable.ic_radio_stop);
                        } else {
                            if (isStreaming) {
                                buttonPlay.setBackgroundResource(R.drawable.ic_radio_play);
                                // Toast.makeText(getApplicationContext(), "Stop Streaming..", Toast.LENGTH_SHORT).show();
                                stopStreaming();
                                isStreaming = false;
                                progressRadio.setVisibility(View.GONE);
                                UtilsRadio.setDataBooleanToSP(getApplicationContext(), UtilsRadio.IS_STREAM, false);
                            }
                        }
                    }
                });
            } else if (typeRooms.equalsIgnoreCase("B")) {
                mainStreaming.setVisibility(View.VISIBLE);
                adView = new LVDOAdView(this, LVDOAdSize.IAB_MRECT, "f3e11705262aa8bba69cb58571772cf3");
                LVDOAdRequest adRequest = new LVDOAdRequest();
                adView.loadAd(adRequest);
                converStreaming.addView(adView);
            } else if (typeRooms.equalsIgnoreCase("S")) {
                mainVideoStreaming.setVisibility(View.VISIBLE);
                Cursor cursorBot = botListDB.getSingle(destinationAddr);
                String link = "";
                if (cursorBot.getCount() > 0) {
                    link = cursorBot.getString(cursorBot.getColumnIndexOrThrow(BotListDB.BOT_LINK));
                } else {
                   /*masih belum material design
                    finish();
                    showToast((String) getResources().getText(R.string.add_room_first));
                    Intent intent = new Intent(getApplicationContext(), SearchBotActivity.class);
                    intent.putExtra("search", title );
                    startActivity(intent);*/
                }
                cursorBot.close();
                VideoFragment videoFragment = VideoFragment.newInstance(link);
                getSupportFragmentManager().beginTransaction().replace(R.id.mainVideoStreaming, videoFragment).commit();
            } else if (typeRooms.equalsIgnoreCase("1")) {

            } else {
                showToast("byonchat version not support this rooms, please update");
                finish();
            }
        }

    }

    private String createAKA(String aa, String bb) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("a", aa);
            obj.put("b", bb);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }


    public static boolean isValidDate(String inDate) {
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    public void saveBot() {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(ConversationActivity.this);
        alertbox.setMessage("Are you sure you want to add this room ?");
        alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                String key = new ValidationsKey().getInstance(getApplicationContext()).key(false);
                if (key.equalsIgnoreCase("null")) {
                    Toast.makeText(getApplicationContext(), R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                    pdialog.dismiss();
                } else {
                    new addBotRequest(getApplicationContext()).execute(key);
                }
            }

        });
        alertbox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        alertbox.show();
    }

    public void updateMessageUnread() {

        Cursor countMessageUnread = messengerHelper.query(
                SQL_SELECT_TOTAL_MESSAGES_UNREAD,
                new String[]{String.valueOf(Message.STATUS_UNREAD), destination.getJabberId()});

        while (countMessageUnread.moveToNext()) {
            messengerHelper.execSql(SQL_UPDATE_MESSAGES, new String[]{countMessageUnread.getString(countMessageUnread.getColumnIndex("_id"))});
            if (!countMessageUnread.getString(countMessageUnread.getColumnIndex(Message.TYPE)).equalsIgnoreCase(Message.TYPE_READSTATUS)) {
                readFunction(countMessageUnread.getString(countMessageUnread.getColumnIndex("packet_id")));
                Log.w("dinin", countMessageUnread.getString(countMessageUnread.getColumnIndex("message")));
            }
        }
        countMessageUnread.close();

        try {
            int badgeCount = 0;
            Cursor cursor = messengerHelper.query(
                    SQL_SELECT_TOTAL_MESSAGES_UNREAD_ALL,
                    new String[]{String.valueOf(Message.STATUS_UNREAD)});
            int indexTotal = cursor.getColumnIndex("total");
            while (cursor.moveToNext()) {
                badgeCount = cursor.getInt(indexTotal);
            }
            cursor.close();

            ShortcutBadger.setBadge(getApplicationContext(), badgeCount);
        } catch (ShortcutBadgeException e) {
        }

    }

    private boolean isNetworkConnectionAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null)
            return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }


    public void blockContact() {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(ConversationActivity.this);
        alertbox.setMessage("Are you sure you want to " + headerBtnLock.getText().toString() + " this contact?");
        alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                String key = new ValidationsKey().getInstance(getApplicationContext()).key(false);
                if (key.equalsIgnoreCase("null")) {
                    Toast.makeText(getApplicationContext(), R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                    pdialog.dismiss();
                } else {
                    new blockRequest(getApplicationContext()).execute(key);
                }
            }

        });
        alertbox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        alertbox.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.conversation_menu, menu);

        if (conversationType == CONVERSATION_TYPE_PRIVATE) {
            MenuItem item = menu.getItem(1);
            Contact c = (Contact) destination;
            if (c.getId() == 0) {
                item.setTitle("Add To Contact");
            } else {
                item.setTitle("View Profile");
            }


            // Uncomment the following if for SMS bridge feature
            // if (c.getType() == 0) {
            // menu.getItem(0).setVisible(true);
            // } else {
            // menu.getItem(0).setVisible(false);
            // }

            // Comment the following 1 line for SMS bridge feature:
            IntervalDB db = new IntervalDB(getApplicationContext());
            db.open();

            Cursor cursorSelect = db.getSingleContact(24);
            if (cursorSelect.getCount() > 0) {
                menu.getItem(0).setVisible(false);
            } else {
                menu.getItem(0).setVisible(true);
            }

            db.close();

            String regex = "[0-9]+";
            if (!c.getJabberId().matches(regex)) {
                if (Utility.roomType(c.getJabberId()).equalsIgnoreCase("V")) {
                    menu.getItem(0).setVisible(false);
                }
                menu.getItem(1).setVisible(false);
                menu.getItem(2).setVisible(false);
                menu.getItem(3).setVisible(false);
                menu.getItem(4).setVisible(false);
                menu.getItem(5).setVisible(false);
            } else {
                menu.getItem(1).setVisible(false);
                menu.getItem(2).setVisible(false);
                menu.getItem(3).setVisible(false);
                menu.getItem(4).setVisible(false);
                menu.getItem(5).setVisible(false);
            }
        } else {
            IntervalDB db = new IntervalDB(getApplicationContext());
            db.open();

            Cursor cursorSelect = db.getSingleContact(24);
            if (cursorSelect.getCount() > 0) {
                menu.getItem(0).setVisible(false);
            } else {
                menu.getItem(0).setVisible(true);
            }

            db.close();
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(false);
            menu.getItem(4).setVisible(false);
            menu.getItem(5).setVisible(false);
        }


        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
           /*     intent = new Intent(this, MainActivity.class);
                intent.putExtra("from","2" );
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);*/
                finish();
                return true;
            case R.id.menu_conversation_group:
            /* group blm material design
               intent = new Intent(this, GroupInfoActivity.class);
                intent.putExtra(GroupInfoActivity.EXTRA_KEY_GROUP_JID,
                        destination.getJabberId());
                startActivity(intent);
                return true;*/

            case R.id.menu_conversation_contact:
                Contact c = (Contact) destination;
                if (c.getId() == 0) {
                    finish();
                    intent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
                    intent.setType(Contacts.CONTENT_ITEM_TYPE);
                    intent.putExtra(Insert.PHONE, "+" + destination.getJabberId());
                    startActivityForResult(intent, REQ_CREATE_CONTACT);
                } else {
                    intent = new Intent(this, ViewProfileActivity.class);
                    intent.putExtra(ViewProfileActivity.KEY_JABBER_ID,
                            c.getJabberId());
                    intent.putExtra(ViewProfileActivity.KEY_REFERENCE,
                            ViewProfileActivity.REFERENCE_CONVERSATION);
                    startActivity(intent);
                }
                return true;

            case R.id.menu_conversation_attachment:
                showAttc(true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showAttc(boolean att) {
        int cx = (mRevealView.getLeft() + mRevealView.getRight());
        int cy = mRevealView.getTop();
        int radius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());
        if (att) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                SupportAnimator animator =
                        ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.setDuration(300);

                SupportAnimator animator_reverse = animator.reverse();

                if (hidden) {
                    mRevealView.setVisibility(View.VISIBLE);
                    animator.start();
                    hidden = false;
                } else {
                    animator_reverse.addListener(new SupportAnimator.AnimatorListener() {
                        @Override
                        public void onAnimationStart() {

                        }

                        @Override
                        public void onAnimationEnd() {
                            mRevealView.setVisibility(View.INVISIBLE);
                            hidden = true;

                        }

                        @Override
                        public void onAnimationCancel() {

                        }

                        @Override
                        public void onAnimationRepeat() {

                        }
                    });
                    animator_reverse.start();

                }
            } else {
                if (hidden) {
                    Animator anim = android.view.ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
                    mRevealView.setVisibility(View.VISIBLE);
                    anim.start();
                    hidden = false;

                } else {
                    Animator anim = android.view.ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, radius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mRevealView.setVisibility(View.INVISIBLE);
                            hidden = true;
                        }
                    });
                    anim.start();

                }
            }
        } else {
            if (!hidden) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    SupportAnimator animator =
                            ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
                    animator.setInterpolator(new AccelerateDecelerateInterpolator());
                    animator.setDuration(300);

                    SupportAnimator animator_reverse = animator.reverse();
                    animator_reverse.addListener(new SupportAnimator.AnimatorListener() {
                        @Override
                        public void onAnimationStart() {

                        }

                        @Override
                        public void onAnimationEnd() {
                            mRevealView.setVisibility(View.INVISIBLE);
                            hidden = true;

                        }

                        @Override
                        public void onAnimationCancel() {

                        }

                        @Override
                        public void onAnimationRepeat() {

                        }
                    });
                    animator_reverse.start();

                } else {
                    Animator anim = android.view.ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, radius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mRevealView.setVisibility(View.INVISIBLE);
                            hidden = true;
                        }
                    });
                    anim.start();
                }
            }
        }
    }

    private void initText() {
        textMessage = (EditText) findViewById(R.id.textMessage);
        textMessage.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    /*sendMessage(textMessage.getText().toString());*/
                    Editable msg = textMessage.getText();
                    String emsg = Html.toHtml(msg);
                    if (textMessage.getText().toString().contains("\n") == true) {
                        sendMessage(emsg);
                    } else {
                        sendMessage(textMessage.getText().toString());
                    }
                    textMessage.setText("");
                    handled = true;
                }
                return handled;
            }
        });

        textMessage.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                showAttc(false);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                textMessage.setFocusableInTouchMode(true);
                textMessage.requestFocus();
                scrollListConversationToBottom(true);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(textMessage, InputMethodManager.SHOW_IMPLICIT);
                if (emojicons.getVisibility() == View.VISIBLE) {
                    emojicons.setVisibility(View.GONE);
                }

                return false;
            }
        });

        textMessage.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                showAttc(false);
                if (hasFocus) {
                    scrollListConversationToBottom(true);
                }
            }
        });

        textMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                if (textMessage.getText().toString().trim().length() > 0) {
                    btnMic.setVisibility(View.GONE);
                    btnSend.setVisibility(View.VISIBLE);
                } else {
                    btnMic.setVisibility(View.VISIBLE);
                    btnSend.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    private Message createNewMessage(String message, String type) {
        Message vo = new Message(sourceAddr, destination.getJabberId(), message);
        vo.setType(type);
        vo.setSendDate(new Date());
        vo.setStatus(Message.STATUS_INPROGRESS);
        vo.generatePacketId();
        if (conversationType == CONVERSATION_TYPE_GROUP) {
            vo.setGroupChat(true);
            vo.setSourceInfo(sourceAddr);
        }
        return vo;
    }

    private void sendMessage(String message) {
        Message vo = createNewMessage(message, Message.TYPE_TEXT);
        addConversation(vo);
        messengerHelper.insertData(vo);
        Intent intent = new Intent(this, UploadService.class);
        intent.putExtra(UploadService.ACTION, "sendText");
        intent.putExtra(UploadService.KEY_MESSAGE, vo);
        startService(intent);
    }

    private void sendLocation(String message) {
        Message vo = createNewMessage(message, Message.TYPE_LOC);
        addConversation(vo);

        messengerHelper.insertData(vo);
        Intent intent = new Intent(this, UploadService.class);
        intent.putExtra(UploadService.ACTION, "sendText");
        intent.putExtra(UploadService.KEY_MESSAGE, vo);
        startService(intent);

    }

    private void readFunction(String message) {
        Message vo = createNewMessage(message, Message.TYPE_READSTATUS);
        messengerHelper.insertData(vo);
        Intent intent = new Intent(this, UploadService.class);
        intent.putExtra(UploadService.ACTION, "sendText");
        intent.putExtra(UploadService.KEY_MESSAGE, vo);
        startService(intent);
    }

    public void tarikPesan(Message msg) {
        Message voReport = msg;
        voReport.setType(Message.TYPE_REPORT_TARIK);
        voReport.setMessage("Your message is being recalled");
        messengerHelper.updateData(voReport);
        updateConversation(voReport);

        Message vo = createNewMessage(msg.getPacketId(), Message.TYPE_TARIK);
        messengerHelper.insertData(vo);
        Intent intent = new Intent(this, UploadService.class);
        intent.putExtra(UploadService.ACTION, "sendText");
        intent.putExtra(UploadService.KEY_MESSAGE, vo);
        startService(intent);
    }

    private void sendMessageMenu(String message) {
        Message vo = createNewMessage(message, Message.TYPE_TEXT);
        /* new MessageSender().execute(vo);*/
        Intent intent = new Intent(this, UploadService.class);
        intent.putExtra(UploadService.ACTION, "sendText");
        intent.putExtra(UploadService.KEY_MESSAGE, vo);
        startService(intent);
    }

    private void sendMessageFile(String message) {
        Message vo = createNewMessage(message, Message.TYPE_TEXT);
        new MessageSender().execute(vo);
    }

    private void retryMessage(Message msg) {
        msg.setStatus(Message.STATUS_INPROGRESS);
        updateConversation(msg);
        new MessageSender().execute(msg);
    }

    private synchronized void clearConversations() {
        conversations.clear();
    }

    private synchronized void updateConversation(Message vo) {
        updateMessageUnread();
        int index = conversations.indexOf(vo);
        if (index != -1) {
            conversations.set(index, vo);
            adapter.refreshList();
        } else {
            addConversation(vo, true, true);
        }
    }

    private synchronized void addConversation(Message vo, boolean notify, boolean receive) {
        Date d = vo.getDeliveredDate();
        if (d == null) {
            d = vo.getSendDate();
        }
        Date cdate = new Date(System.currentTimeMillis());
        if (d != null) {
            cdate = d;
        }


        String theDate = dateFormat.format(cdate);
        if (!lastDate.equals(theDate)) {
            lastDate = theDate;
            Contact c = (Contact) destination;
            if (!new Validations().getInstance(getApplicationContext()).cekRoom(c.getJabberId())) {
                String iklan = new Validations().getInstance(getApplicationContext()).getContentValidation(14);
                if (iklan.length() > 0) {
                    theDate += "\n" + iklan;
                }
            }
            conversations.add(theDate);
        }

        if (vo.isGroupChat()) {
            if (Message.TYPE_INFO.equals(vo.getType())) {
                conversations.add(vo.getMessage());
            } else if (sourceAddr.equals(vo.getSource())) {
                conversations.add(vo);
                groupMessages = null;
            } else {
                boolean added = false;
                String sinfo = "+" + vo.getSourceInfo();
                Contact c = messengerHelper.getContact(vo.getSourceInfo());
                if (c != null)
                    sinfo = c.getName();

                Object lastMessage = conversations
                        .get((conversations.size() - 1));
                boolean createnew = true;
                if (lastMessage instanceof ArrayList) {
                    ArrayList listTmp = ((ArrayList) lastMessage);
                    Object objTmp = ((ArrayList) lastMessage).get((listTmp
                            .size() - 1));
                    Message m = (Message) objTmp;
                    if (m.getSourceInfo().equals(vo.getSourceInfo())) {
                        if ((!(Message.TYPE_IMAGE.equals(vo.getType()) || Message.TYPE_LOC.equals(vo.getType())
                                || Message.TYPE_VIDEO.equals(vo.getType()))) && (!(Message.TYPE_IMAGE.equals(m.getType())
                                || Message.TYPE_LOC.equals(m.getType()) || Message.TYPE_VIDEO.equals(m.getType())))) {
                            createnew = false;
                        }
                    }
                }


                if ((groupMessages != null && !createnew)) {
                    if (sinfo.equals(groupMessages.get(0))) {
                        groupMessages.add(vo);
                        added = true;
                    }
                }

                if (!added) {
                    groupMessages = new ArrayList<Object>();
                    groupMessages.add(sinfo);
                    groupMessages.add(vo);
                    conversations.add(groupMessages);
                }
            }
        } else {
            conversations.add(vo);
        }

        if (notify) {
            adapter.refreshList();
            scrollListConversationToBottom(true);
        }
        if (receive) {
            adapter.refreshList();
        }
    }

    private synchronized void addConversationEarlier(Message vo, boolean notify) {
        //  updateMessageUnread();
        Date d = vo.getDeliveredDate();
        if (d == null) {
            d = vo.getSendDate();
        }
        Date cdate = new Date(System.currentTimeMillis());
        if (d != null) {
            cdate = d;
        }


        String theDate = dateFormat.format(cdate);
        if (lastDate.equals(theDate)) {
            conversations.remove(0);
        }


        if (vo.isGroupChat()) {
            if (Message.TYPE_INFO.equals(vo.getType())) {
                conversations.add(vo.getMessage());
            } else if (sourceAddr.equals(vo.getSource())) {
                conversations.add(vo);
                groupMessages = null;
            } else {
                boolean added = false;
                String sinfo = "+" + vo.getSourceInfo();
                Contact c = messengerHelper.getContact(vo.getSourceInfo());
                if (c != null)
                    sinfo = c.getName();

                Object lastMessage = conversations
                        .get((conversations.size() - 1));
                boolean createnew = true;
                if (lastMessage instanceof ArrayList) {
                    ArrayList listTmp = ((ArrayList) lastMessage);
                    Object objTmp = ((ArrayList) lastMessage).get((listTmp
                            .size() - 1));
                    Message m = (Message) objTmp;
                    if (m.getSourceInfo().equals(vo.getSourceInfo())) {
                        if ((!(Message.TYPE_IMAGE.equals(vo.getType()) || Message.TYPE_LOC.equals(vo.getType())
                                || Message.TYPE_VIDEO.equals(vo.getType()))) && (!(Message.TYPE_IMAGE.equals(m.getType())
                                || Message.TYPE_LOC.equals(m.getType()) || Message.TYPE_VIDEO.equals(m.getType())))) {
                            createnew = false;
                        }
                    }
                }


                if ((groupMessages != null && !createnew)) {
                    if (sinfo.equals(groupMessages.get(0))) {
                        groupMessages.add(vo);
                        added = true;
                    }
                }

                if (!added) {
                    groupMessages = new ArrayList<Object>();
                    groupMessages.add(sinfo);
                    groupMessages.add(vo);
                    conversations.add(0, groupMessages);
                }
            }
        } else {
            conversations.add(0, vo);
        }


        lastDate = theDate;
        Contact c = (Contact) destination;
        if (!new Validations().getInstance(getApplicationContext()).cekRoom(c.getJabberId())) {
            String iklan = new Validations().getInstance(getApplicationContext()).getContentValidation(14);
            if (iklan.length() > 0) {
                theDate += "\n" + iklan;
            }
        }
        conversations.add(0, theDate);
        //((ConversationAdapter) listConversation.getAdapter()).notifyDataSetChanged();
    }


    private synchronized void addConversation(Message vo) {
        addConversation(vo, true, false);
    }

    private void scrollListConversationToBottom(boolean scroll) {
        if (scroll) {
            listConversation.postDelayed(new Runnable() {
                @Override
                public void run() {
                    listConversation.setSelection(adapter.getCount() - 1);
                }
            }, 300);
        } else {
            listConversation.setSelection(adapter.getCount());
        }
    }

    private void scrollListConversationToTop() {

        listConversation.postDelayed(new Runnable() {
            @Override
            public void run() {
                listConversation.setSelection(0);
            }
        }, 300);

    }

    private void scrollListConversationTo(final int position) {

        listConversation.postDelayed(new Runnable() {
            @Override
            public void run() {
                listConversation.setSelection(position);
            }
        }, 300);

    }


    @Override
    protected void onPause() {
        getApplicationContext().unbindService(this);
        unregisterReceiver(broadcastHandler);
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
        super.onPause();
        if (mBufferBroadcastIsRegistered) {
            unregisterReceiver(broadcastBufferReceiver);
            mBufferBroadcastIsRegistered = false;
        }
    }

    private void refreshConversation() {
        new ConversationLoader().execute();
    }


    @Override
    protected void onResume() {
        super.onResume();

        getApplicationContext().bindService(
                new Intent(this, MessengerConnectionService.class), this,
                Context.BIND_AUTO_CREATE);
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                .cancel(NotificationReceiver.NOTIFY_ID);
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                .cancel(NOTIFY_ID_CONV);
        IntentFilter filter = new IntentFilter(
                MessengerConnectionService.ACTION_MESSAGE_RECEIVED);
        filter.addAction(MessengerConnectionService.ACTION_MESSAGE_DELIVERED);
        filter.addAction(MessengerConnectionService.ACTION_MESSAGE_SENT);
        filter.addAction(MessengerConnectionService.ACTION_MESSAGE_FAILED);
        filter.addAction(MessengerConnectionService.ACTION_CONNECTED);
        filter.addAction(MessengerConnectionService.ACTION_DISCONNECTED);
        filter.addAction(MessengerConnectionService.ACTION_REFRESH_CHAT_HISTORY);
        filter.addAction(MessengerConnectionService.ACTION_CHAT_OFF);
        filter.addAction(UploadService.KEY_UPDATE_BAR);
        filter.addAction(UploadService.KEY_UPDATE_UPLOAD_BAR);
        filter.setPriority(1);
        registerReceiver(broadcastHandler, filter);
        updateMessageUnread();
        refreshConversation();
        if (!mBufferBroadcastIsRegistered) {
            registerReceiver(broadcastBufferReceiver, new IntentFilter(
                    StreamService.BROADCAST_BUFFER));
            mBufferBroadcastIsRegistered = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

   /* protected void setMessageFormEnabled(boolean enable) {
        btnSend.setEnabled(enable);
    }
*/


    /**
     * Showing google speech input dialog
     */
    private void promptSpeechInput() {
        showAttc(false);

        Contact contact = messengerHelper.getMyContact();
        String language = String.valueOf(Locale.getDefault());
        if (contact.getJabberId().substring(0, 2).equalsIgnoreCase("62")) language = "id";

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, language);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language);
        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, language);
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        final AdapterContextMenuInfo ctxSelected = (AdapterContextMenuInfo) menuInfo;
        Object o;
        if (listConversation.getHeaderViewsCount() > 0) {
            o = conversations.get(ctxSelected.position - 1);
        } else {
            o = conversations.get(ctxSelected.position);
        }

        if (o instanceof Message) {
            Log.w("masuk", "kesiniGa");
            final Dialog dialog;
            dialog = DialogUtil.customDialogConversation(this);
            dialog.show();
            final Activity aa = this;

            FrameLayout btnDelete = (FrameLayout) dialog.findViewById(R.id.btnDelete);
            FrameLayout btnCopy = (FrameLayout) dialog.findViewById(R.id.btnCopy);
            FrameLayout btnResend = (FrameLayout) dialog.findViewById(R.id.btnResend);
            FrameLayout btnForward = (FrameLayout) dialog.findViewById(R.id.btnForward);
            FrameLayout btnForwardRounded = (FrameLayout) dialog.findViewById(R.id.btnForwardRounded);
            FrameLayout btnShare = (FrameLayout) dialog.findViewById(R.id.btnShare);
            FrameLayout btnEditMeme = (FrameLayout) dialog.findViewById(R.id.btnEditMeme);
            FrameLayout btnRecall = (FrameLayout) dialog.findViewById(R.id.btnRecall);
            View garis1 = (View) dialog.findViewById(R.id.garis1);
            View garis2 = (View) dialog.findViewById(R.id.garis2);
            View garis3 = (View) dialog.findViewById(R.id.garis3);
            View garis4 = (View) dialog.findViewById(R.id.garis4);
            View garis5 = (View) dialog.findViewById(R.id.garis5);
            View garis6 = (View) dialog.findViewById(R.id.garis6);

            final Message msg = (Message) o;
            final int position;
            if (listConversation.getHeaderViewsCount() > 0) {
                position = ctxSelected.position - 1;
            } else {
                position = ctxSelected.position;
            }

            btnDelete.setVisibility(View.VISIBLE);

            if (msg.getStatus() == Message.STATUS_FAILED) {
                btnResend.setVisibility(View.VISIBLE);
            }

            if (Message.TYPE_TEXT.equals(msg.getType()) || Message.TYPE_BROADCAST.equals(msg.getType())) {
                Contact c = (Contact) destination;
                if (new Validations().getInstance(getApplicationContext()).cekRoom(c.getJabberId())) {
                    JSONObject jObject = null;
                    try {
                        jObject = new JSONObject(msg.getMessage());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (jObject == null) {
                        garis1.setVisibility(View.VISIBLE);
                        btnCopy.setVisibility(View.VISIBLE);
                        garis2.setVisibility(View.VISIBLE);
                        btnResend.setVisibility(View.VISIBLE);
                        garis3.setVisibility(View.VISIBLE);
                        btnForward.setVisibility(View.GONE);
                        btnForwardRounded.setVisibility(View.VISIBLE);
                    }

                } else {
                    garis1.setVisibility(View.VISIBLE);
                    btnCopy.setVisibility(View.VISIBLE);
                    garis2.setVisibility(View.GONE);
                    btnResend.setVisibility(View.GONE);
                    garis3.setVisibility(View.VISIBLE);
                    btnForward.setVisibility(View.GONE);
                    btnForwardRounded.setVisibility(View.VISIBLE);
                }
            }
            if (msg.getSource().equalsIgnoreCase(messengerHelper.getMyContact().getJabberId()) && !msg.getType().equalsIgnoreCase(Message.TYPE_REPORT_TARIK)) {
                btnForwardRounded.setVisibility(View.GONE);
                btnForward.setVisibility(View.VISIBLE);
                garis6.setVisibility(View.VISIBLE);
                btnRecall.setVisibility(View.VISIBLE);
            }

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                    String delMessage = "Delete message '"
                            + Message.parsedMessageBody(msg, 10, getApplicationContext()) + "'?";

                    final Dialog dialogConfirmation;
                    dialogConfirmation = DialogUtil.customDialogConversationConfirmation(aa);
                    dialogConfirmation.show();

                    TextView txtConfirmation = (TextView) dialogConfirmation.findViewById(R.id.confirmationTxt);
                    TextView descConfirmation = (TextView) dialogConfirmation.findViewById(R.id.confirmationDesc);
                    txtConfirmation.setText("Confirmation");
                    descConfirmation.setVisibility(View.VISIBLE);
                    descConfirmation.setText(delMessage);

                    Button btnNo = (Button) dialogConfirmation.findViewById(R.id.btnNo);
                    Button btnYes = (Button) dialogConfirmation.findViewById(R.id.btnYes);

                    btnNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogConfirmation.dismiss();
                        }
                    });

                    btnYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteMessage(msg, position);
                            dialogConfirmation.dismiss();
                        }
                    });
                }
            });

            btnCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (Build.VERSION.SDK_INT >= 11) {
                        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        cm.setPrimaryClip(ClipData.newPlainText("ochat-message",
                                msg.getMessage()));
                    } else {
                        android.text.ClipboardManager cm = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        cm.setText(msg.getMessage());
                    }
                    showToast("Copied");
                }
            });

            btnResend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    retryMessage((Message) adapter
                            .getItem(ctxSelected.position));
                }
            });

            btnForward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent intent = new Intent(getApplicationContext(), NewSelectContactActivity.class);
                    intent.putExtra("messageText", msg.getMessage());
                    intent.putExtra("type", "text/plain");
                    startActivity(intent);
                }
            });

            btnForwardRounded.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent intent = new Intent(getApplicationContext(), NewSelectContactActivity.class);
                    intent.putExtra("messageText", msg.getMessage());
                    intent.putExtra("type", "text/plain");
                    startActivity(intent);
                }
            });

            btnRecall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                    String recallMessage = "Are you sure you want to " + getResources().getString(R.string.menu_conversation_tarik) + " '" +
                            Message.parsedMessageBody(msg, 10, getApplicationContext()) + "'?";

                    final Dialog dialogConfirmation;
                    dialogConfirmation = DialogUtil.customDialogConversationConfirmation(aa);
                    dialogConfirmation.show();

                    TextView txtConfirmation = (TextView) dialogConfirmation.findViewById(R.id.confirmationTxt);
                    TextView descConfirmation = (TextView) dialogConfirmation.findViewById(R.id.confirmationDesc);
                    txtConfirmation.setText("Confirmation");
                    descConfirmation.setVisibility(View.VISIBLE);
                    descConfirmation.setText(recallMessage);

                    Button btnNo = (Button) dialogConfirmation.findViewById(R.id.btnNo);
                    Button btnYes = (Button) dialogConfirmation.findViewById(R.id.btnYes);

                    btnNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogConfirmation.dismiss();
                        }
                    });

                    btnYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogConfirmation.dismiss();
                            tarikPesan(msg);
                        }
                    });
                }
            });
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void deleteMessage(Message msg, int position) {
        messengerHelper.deleteData(msg);
        conversations.remove(msg);
        adapter.add(conversations);
        adapter.notifyDataSetChanged();
        scrollListConversationTo(position);
    }


    @SuppressLint("NewApi")
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        itemContextSelected = (AdapterContextMenuInfo) item.getMenuInfo();
        Message msg;
        final int position;
        if (listConversation.getHeaderViewsCount() > 0) {
            position = itemContextSelected.position - 1;
            msg = (Message) conversations.get(itemContextSelected.position - 1);
        } else {
            position = itemContextSelected.position;
            msg = (Message) adapter.getItem(itemContextSelected.position);
        }

        switch (item.getItemId()) {
            case R.id.menu_conversation_share:
                File f = new File(msg.getMessage());
                if (f.exists()) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));

                    String title = "Share";
                    if (Message.TYPE_IMAGE.equals(msg.getType())) {
                        sendIntent.setType("image/*");
                        title += " Image";
                    } else if (Message.TYPE_VIDEO.equals(msg.getType())) {
                        sendIntent.setType("video/*");
                        title += " Video";
                    }
                    startActivity(Intent.createChooser(sendIntent, title));
                }
                return true;

            case R.id.menu_conversation_delete:

                String delMessage = "Delete message '"
                        + Message.parsedMessageBody(msg, 10, getApplicationContext()) + "'?";
                AlertDialog.Builder builder = DialogUtil.generateAlertDialog(this,
                        "Confirm Delete", delMessage);
                final Message finalMsg = msg;
                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteMessage(finalMsg, position);
                            }
                        });
                builder.setNegativeButton("No", null);
                builder.show();
                return true;
            case R.id.menu_conversation_copy:

                if (Build.VERSION.SDK_INT >= 11) {
                    ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    cm.setPrimaryClip(ClipData.newPlainText("ochat-message",
                            msg.getMessage()));
                } else {
                    android.text.ClipboardManager cm = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    cm.setText(msg.getMessage());
                }
                showToast("Copied");
                return true;

            case R.id.menu_failed_retry:
                retryMessage((Message) adapter
                        .getItem(itemContextSelected.position));
                return true;
            case R.id.menu_conversation_resend:
              /*  retryMessage((Message) adapter
                        .getItem(itemContextSelected.position));*/
                showToast("Resend");
                sendMessage(msg.getMessage());
                return true;
            case R.id.menu_conversation_forwad:
                Intent intent = new Intent(getApplicationContext(), NewSelectContactActivity.class);
                intent.putExtra("messageText", msg.getMessage());
                intent.putExtra("type", "text/plain");
                startActivity(intent);
                return true;
            case R.id.menu_conversation_tarik:

                String recallMessage = "Are you sure you want to  " + getResources().getString(R.string.menu_conversation_tarik) + " '" +
                        Message.parsedMessageBody(msg, 10, getApplicationContext()) + "'?";
                AlertDialog.Builder builderRecall = DialogUtil.generateAlertDialog(this,
                        "Confirm Recall Message", recallMessage);
                final Message finalMsgRecall = msg;
                builderRecall.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tarikPesan(finalMsgRecall);
                            }
                        });
                builderRecall.setNegativeButton("No", null);
                builderRecall.show();
                return true;
        }
        return super.onContextItemSelected(item);

    }

    @Override
    public void onServiceConnected(ComponentName className, IBinder iBinder) {
        binder = (MessengerConnectionBinder) iBinder;
        try {
            if (binder.isConnected()) {
                boolean enabled = true;
                if (CONVERSATION_TYPE_GROUP == conversationType) {
                    if (Group.STATUS_INACTIVE.equals(((Group) destination)
                            .getStatus())) {
                        enabled = false;
                    }
                }
            }
        } catch (Exception e) {
        }

    }

    @Override
    public void onServiceDisconnected(ComponentName className) {
        binder = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_CREATE_CONTACT) {
                String[] projection = new String[]{
                        Contacts.LOOKUP_KEY,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER};
                String dt[] = data.getData().toString().split("/");
                Cursor cursor = getContentResolver().query(
                        MainActivity.CONTACT_URI, projection,
                        Contacts.LOOKUP_KEY + "=?",
                        new String[]{dt[(dt.length - 2)]}, null);

                if (cursor.moveToNext()) {
                    String id = cursor.getString(cursor
                            .getColumnIndex(projection[0]));
                    String name = cursor.getString(cursor
                            .getColumnIndex(projection[1]));
                    String mnumber = cursor
                            .getString(cursor.getColumnIndex(projection[2]))
                            .replace(" ", "").replace("-", "");

                    if (mnumber.startsWith("0")) {
                        mnumber = mnumber.replaceFirst("0", "62");
                    } else if (mnumber.startsWith("+")) {
                        mnumber = mnumber.replaceFirst("\\+", "");
                    }

                    if (destination.getJabberId().equals(mnumber)
                            && !"".equals(name)) {
                        destination = new Contact(name, mnumber, "");
                        Contact c = (Contact) destination;
                        c.setAddrbookId(id);
                        if (binder.isConnected()) {
                            try {
                                binder.addNewContact(c);
                                setBarTitleAndImage(c);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listConversation.removeHeaderView(header);

                                    }
                                });

                            } catch (Exception e) {
                              /*  Log.e(getClass().getSimpleName(),
                                        "Failed adding contact for jabberID: "
                                                + destination.getJabberId(), e);*/
                            }
                        }
                    }

                }
            } else if (requestCode == REQ_CODE_SPEECH_INPUT) {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = result.get(0);
                    if (textMessage.getText().length() > 0)
                        text = textMessage.getText().toString() + " " + result.get(0);
                    textMessage.setText(text);
                    textMessage.setSelection(textMessage.getText().length());
                }
            } else if (requestCode == REQ_CAMERA) {
                if (decodeFile(cameraFileOutput)) {
                    Intent intent = new Intent(getApplicationContext(), ConfirmationSendFile.class);
                    String jabberId = destination.getJabberId();
                    intent.putExtra("file", cameraFileOutput);
                    if (destination != null) {
                        jabberId = destination.getJabberId();
                        if (destination instanceof Group) {
                            Group g = (Group) destination;
                            intent.putExtra(ConversationActivity.KEY_TITLE, g.getName());
                            intent.putExtra(ConversationActivity.KEY_CONVERSATION_TYPE,
                                    ConversationActivity.CONVERSATION_TYPE_GROUP);
                        }
                    }
                    intent.putExtra("name", jabberId);
                    intent.putExtra("type", Message.TYPE_IMAGE);
                    startActivity(intent);

                }

            } else if
                    (requestCode == REQ_MEME) {
                if (decodeFile(cameraFileOutput)) {
                    Intent intent = new Intent(getApplicationContext(), PhotoSortrActivity.class);
                    String jabberId = destination.getJabberId();
                    intent.putExtra("file", cameraFileOutput);
                    if (destination != null) {
                        jabberId = destination.getJabberId();
                        if (destination instanceof Group) {
                            Group g = (Group) destination;
                            intent.putExtra(ConversationActivity.KEY_TITLE, g.getName());
                            intent.putExtra(ConversationActivity.KEY_CONVERSATION_TYPE,
                                    ConversationActivity.CONVERSATION_TYPE_GROUP);
                        }
                    }
                    intent.putExtra("name", jabberId);
                    intent.putExtra("type", Message.TYPE_IMAGE);
                    startActivity(intent);
                }

            } else if (requestCode == PLACE_PICKER_REQUEST
                    && resultCode == Activity.RESULT_OK) {
                final Place place = PlacePicker.getPlace(data, this);
                final String name = place.getName() != null ? (String) place.getName() : " ";
                final String address = place.getAddress() != null ? (String) place.getAddress() : " ";
                final String web = String.valueOf(place.getWebsiteUri() != null ? place.getWebsiteUri() : " ");
                new CountDownTimer(300, 1000) {

                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        sendLocation(place.getLatLng().latitude + ";" + place.getLatLng().longitude + ";" + name + ";" + address + ";" + web);

                    }
                }.start();

            } else if (requestCode == REQUEST_CODE_PICKER && resultCode == RESULT_OK && data != null) {
                images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
                StringBuilder sb = new StringBuilder();
                JSONArray jsonArray = new JSONArray();
                for (int i = 0, l = images.size(); i < l; i++) {
                    sb.append(images.get(i).getPath() + "\n");
                    jsonArray.put(images.get(i).getPath());
                }

                Intent i = new Intent(getApplicationContext(), ConfirmationSendFileMultiple.class);
                i.putParcelableArrayListExtra("selected", images);
                i.putExtra("file", jsonArray.toString());

                String jabberId = destination.getJabberId();
                if (destination != null) {
                    jabberId = destination.getJabberId();
                    if (destination instanceof Group) {
                        Group g = (Group) destination;
                        i.putExtra(ConversationActivity.KEY_TITLE, g.getName());
                        i.putExtra(ConversationActivity.KEY_CONVERSATION_TYPE,
                                ConversationActivity.CONVERSATION_TYPE_GROUP);
                    }
                }
                i.putExtra("name", jabberId);
                i.putExtra("type", Message.TYPE_IMAGE);
                startActivity(i);
            } else {
                Uri selectedUri = data.getData();
                String selectedImagePath = ImageFilePath.getPath(getApplicationContext(), selectedUri);
                File fileOutput = new File(selectedImagePath);
                if (requestCode == REQ_VIDEO) {
                    Intent intent = new Intent(getApplicationContext(), ConfirmationSendFileVideo.class);
                    intent.putExtra("file", fileOutput.getAbsolutePath());
                    String jabberId = destination.getJabberId();
                    if (destination != null) {
                        jabberId = destination.getJabberId();
                        if (destination instanceof Group) {
                            Group g = (Group) destination;
                            intent.putExtra(ConversationActivity.KEY_TITLE, g.getName());
                            intent.putExtra(ConversationActivity.KEY_CONVERSATION_TYPE,
                                    ConversationActivity.CONVERSATION_TYPE_GROUP);
                        }
                    }
                    intent.putExtra("name", jabberId);
                    intent.putExtra("type", Message.TYPE_VIDEO);
                    startActivity(intent);

                } else if (requestCode == REQ_GALLERY
                        || requestCode == REQ_GALLERY_VIDEO || requestCode == REQ_GALLERY_MEME) {
                    Intent intent = new Intent(getApplicationContext(), ConfirmationSendFileVideo.class);

                    String type = Message.TYPE_VIDEO;
                    if (requestCode == REQ_GALLERY) {
                        type = Message.TYPE_IMAGE;
                        if (fileOutput.length() > 1000000L) {
                          /*  File f = resizeImage(fileOutput, true);
                            fileOutput = f;*/
                        }
                    } else if (requestCode == REQ_GALLERY_MEME) {
                        intent = new Intent(getApplicationContext(), PhotoSortrActivity.class);
                        type = Message.TYPE_IMAGE;
                    }


                    intent.putExtra("file", fileOutput.getAbsolutePath());
                    String jabberId = destination.getJabberId();
                    if (destination != null) {
                        jabberId = destination.getJabberId();
                        if (destination instanceof Group) {
                            Group g = (Group) destination;
                            intent.putExtra(ConversationActivity.KEY_TITLE, g.getName());
                            intent.putExtra(ConversationActivity.KEY_CONVERSATION_TYPE,
                                    ConversationActivity.CONVERSATION_TYPE_GROUP);
                        }
                    }
                    intent.putExtra("name", jabberId);
                    intent.putExtra("type", type);
                    startActivity(intent);
                }
            }

        } else if (resultCode == RESULT_CANCELED) {
            if (requestCode == REQ_CREATE_CONTACT) {
                Intent intent;
                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("from", "0");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
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


    private void showAttachmentDialog(int req) {
        if (req == REQ_CAMERA) {
            curAttItems = attCameraItems;
        } else if (req == REQ_VIDEO) {
            curAttItems = attVideoItems;
        } else {
            curAttItems = attMemeItems;
        }
        attCurReq = req;

        AttachmentAdapter adapter = new AttachmentAdapter(this,
                R.layout.menu_item, R.id.textMenu, curAttItems);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.attachment_gridview);
        GridView gridview = (GridView) dialog.findViewById(R.id.gridview);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                String iTitle = curAttItems.get(pos).getTitle();
                String action = Intent.ACTION_GET_CONTENT;
                int req;
                Intent i;
                if (R.drawable.ic_att_video == curAttItems.get(0)
                        .getResourceIcon()) {
                    i = new Intent();
                    if (MENU_GALLERY_TITLE.equals(iTitle)) {
                        req = REQ_GALLERY_VIDEO;
                        i.setType("video/*");
                        i.setAction(action);
                        startActivityForResult(i, req);
                        dialog.dismiss();
                        attCurReq = 0;
                    } else {
                        action = MediaStore.ACTION_VIDEO_CAPTURE;
                        req = REQ_VIDEO;
                        i.setAction(action);
                        startActivityForResult(i, req);
                        dialog.dismiss();
                        attCurReq = 0;
                    }
                } else if (curAttItems.get(0).getTitle() == "Camera Meme") {
                    i = new Intent();
                    if (MENU_GALLERY_TITLE.equals(iTitle)) {
                        req = REQ_GALLERY_MEME;
                        i.setType("image/*");
                        i.setAction(action);
                        startActivityForResult(i, req);
                        dialog.dismiss();
                        attCurReq = 0;
                    } else {
                        action = MediaStore.ACTION_IMAGE_CAPTURE;
                        File f = MediaProcessingUtil
                                .getOutputFile("jpeg");
                        cameraFileOutput = f.getAbsolutePath();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            i.putExtra(MediaStore.EXTRA_OUTPUT,
                                    FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", f));
                            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } else {
                            i.putExtra(MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(f));
                        }
                        req = REQ_MEME;
                        i.setAction(action);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                            StrictMode.setVmPolicy(builder.build());
                        }
                        startActivityForResult(i, req);
                        dialog.dismiss();
                        attCurReq = 0;
                    }

                } else {
                    i = new Intent();
                    if (MENU_GALLERY_TITLE.equals(iTitle)) {
                        if (Build.VERSION.SDK_INT < 19) {
                            i = new Intent();
                            i.setAction(Intent.ACTION_GET_CONTENT);
                            i.setType("image/*");
                        } else {
                            i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            i.addCategory(Intent.CATEGORY_OPENABLE);
                            i.setType("image/*");
                        }
                        req = REQ_GALLERY;

                        Contact c = (Contact) destination;
                        start(c.getJabberId());
                    } else {
                        action = MediaStore.ACTION_IMAGE_CAPTURE;
                        File f = MediaProcessingUtil
                                .getOutputFile("jpeg");
                        cameraFileOutput = f.getAbsolutePath();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            i.putExtra(MediaStore.EXTRA_OUTPUT,
                                    FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", f));
                            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } else {
                            i.putExtra(MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(f));
                        }
                        req = REQ_CAMERA;
                        i.setAction(action);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                            StrictMode.setVmPolicy(builder.build());
                        }
                        startActivityForResult(i, req);
                        dialog.dismiss();
                        attCurReq = 0;
                    }
                }

            }
        });
        dialog.show();
    }

    private void requestLocationInfo() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION},
                        100);
            }
        } else {
            gps = new GPSTracker(ConversationActivity.this);
            LocationManager locManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                try {
                    PlacePicker.IntentBuilder intentBuilder =
                            new PlacePicker.IntentBuilder();
                    Intent intent = intentBuilder.build(this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);

                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            } else {
                gps.showSettingsAlert();
            }
        }


    }

    @Override
    public void onReceiveAd(LVDOAd ad) {
        setSupportProgressBarVisibility(false);
    }

    @Override
    public void onFailedToReceiveAd(LVDOAd ad, LVDOAdRequest.LVDOErrorCode errorcode) {
    }

    @Override
    public void onPresentScreen(LVDOAd ad) {
    }

    @Override
    public void onDismissScreen(LVDOAd ad) {
    }

    @Override
    public void onLeaveApplication(LVDOAd ad) {
    }


    class ButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            showAttc(false);
            if (v.equals(btnAttachmentMapMarker)) {
                requestLocationInfo();
            } else if (v.equals(btnAttachmentCamera)
                    || v.equals(btnAttachmentVideo)) {
                if (v.equals(btnAttachmentCamera)) {
                    showAttachmentDialog(REQ_CAMERA);
                } else {
                    showAttachmentDialog(REQ_VIDEO);
                }
            } else if (v.equals(btnAttachmentMeme)) {
                showAttachmentDialog(REQ_MEME);
            } else if (v.equals(btn_add_emoticon)) {
                if (emojicons.getVisibility() == View.GONE) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                    Animation animFade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_slide_in_bottom);
                    emojicons.setVisibility(View.VISIBLE);
                    emojicons.startAnimation(animFade);

                    textMessage.setFocusable(false);
                } else {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    textMessage.setFocusableInTouchMode(true);
                    textMessage.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(textMessage, InputMethodManager.SHOW_IMPLICIT);
                    emojicons.setVisibility(View.GONE);
                }
            } else if (v.equals(btnSend)) {
                Editable msg = textMessage.getText();
                if (msg.length() > 0) {
                    if (!msg.equals("")) {
                        if (msg.toString().trim().length() > 0) {
                            if (textMessage.getText().toString().contains("\n") == true) {
                                String emsg = Html.toHtml(msg);
                                sendMessage(emsg);
                            } else {
                                sendMessage(textMessage.getText().toString());
                            }
                            textMessage.setText("");
                        }
                    }
                }
            }
        }

    }


    class BroadcastHandler extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MessengerConnectionService.ACTION_MESSAGE_RECEIVED
                    .equals(intent.getAction())) {

                Message vo = intent
                        .getParcelableExtra(MessengerConnectionService.KEY_MESSAGE_OBJECT);
                if (destination.getJabberId().equals(vo.getSource())) {
                    updateConversation(vo);

                    AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    int volume_levelq = am.getStreamVolume(AudioManager.STREAM_NOTIFICATION);

                    MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.song1);
                    mediaPlayer.setVolume(volume_levelq, volume_levelq);
                    mediaPlayer.start();

                    if (Message.TYPE_INFO.equals(vo.getType())) {
                        destination = messengerHelper.getGroup(destination
                                .getJabberId());
                        setBarTitle(destination.getName(), true);
                        Group g = messengerHelper.getGroup(vo.getSource());
                        if (Group.STATUS_INACTIVE.equals(g.getStatus())) {
                            btnSend.setEnabled(false);
                        } else {
                            btnSend.setEnabled(true);
                        }
                    }

                    if (vo.getMessage().equalsIgnoreCase("bc://1_340113808admin;Work Schedule") || vo.getMessage().equalsIgnoreCase("bc://u_341114250arlandi;Work Schedule")) {
                        finish();
                        intent = new Intent(getApplicationContext(), ConversationActivity.class);
                        intent.putExtra(ConversationActivity.KEY_JABBER_ID, destination.getJabberId().toLowerCase());
                        startActivity(intent);
                    }
                    abortBroadcast();
                }
            } else if (MessengerConnectionService.ACTION_MESSAGE_DELIVERED
                    .equals(intent.getAction())) {
                Message vo = intent
                        .getParcelableExtra(MessengerConnectionService.KEY_MESSAGE_OBJECT);

                if (destination.getJabberId().equals(vo.getDestination())) {
                    boolean change = true;
                    if (vo.getType().equalsIgnoreCase(Message.TYPE_READSTATUS)) {
                        change = false;
                    } else if (vo.getType().equalsIgnoreCase(Message.TYPE_TARIK)) {
                        change = false;
                    }
                    if (change) {
                        updateConversation(vo);
                    }
                }
/*
                  //  atas dikirim jika bawah sudah dikim
                    String SQL_SELECT_MESSAGES =  "SELECT *  FROM "
                            + Message.TABLE_NAME + " WHERE (" + Message.DESTINATION + "=? AND "
                            + Message.SOURCE + "=? ) and status = 2 order by packet_id desc";

                    Cursor  cursor = messengerHelper.query(SQL_SELECT_MESSAGES, new String[] {
                            Contact.parseJabberId(destination.getJabberId()), Contact.parseJabberId(sourceAddr)});

                    ArrayList<Message> messages = new ArrayList<Message>();

                    while (cursor.moveToNext()) {
                        Message vo1 = new Message(cursor);
                        messages.add(0, vo1);
                    }
                    cursor.close();
                    for (Iterator<Message> iterator = messages.iterator(); iterator
                            .hasNext();) {
                        Message vo1 = iterator.next();
                        if(vo1.getType().equals(Message.TYPE_VIDEO) || vo1.getType().equals(Message.TYPE_IMAGE)) {
                            binder.sendFile(vo1);
                        }else if (vo1.getType().equalsIgnoreCase(Message.TYPE_LOC)){
                            binder.sendLocation(vo1);
                        }else if (vo1.getType().equalsIgnoreCase(Message.TYPE_BROADCAST)){
                           binder.sendBroadCast(vo1);
                        }else{
                          binder.sendMessage(vo1);
                        }
                    }
                */
            } else if (MessengerConnectionService.ACTION_MESSAGE_FAILED
                    .equals(intent.getAction())) {
                Message vo = intent
                        .getParcelableExtra(MessengerConnectionService.KEY_MESSAGE_OBJECT);
                if (destination.getJabberId().equals(vo.getDestination())) {
                    boolean change = true;
                    if (vo.getType().equalsIgnoreCase(Message.TYPE_READSTATUS)) {
                        change = false;
                    } else if (vo.getType().equalsIgnoreCase(Message.TYPE_TARIK)) {
                        change = false;
                    }

                    if (change) {
                        updateConversation(vo);
                    }
                }

            } else if (MessengerConnectionService.ACTION_CHAT_OFF
                    .equals(intent.getAction())) {
                Log.w("berjalan", "siap");

                startActivity(getIntent());
                finish();
            } else if (MessengerConnectionService.ACTION_MESSAGE_SENT
                    .equals(intent.getAction())) {
                Message vo = intent
                        .getParcelableExtra(MessengerConnectionService.KEY_MESSAGE_OBJECT);
                if (destination.getJabberId().equals(vo.getDestination())) {
                    boolean change = true;
                    if (vo.getType().equalsIgnoreCase(Message.TYPE_READSTATUS)) {
                        change = false;
                    } else if (vo.getType().equalsIgnoreCase(Message.TYPE_TARIK)) {
                        change = false;
                    }

                    if (change) {
                        updateConversation(vo);
                    }
                }
            } else if (MessengerConnectionService.ACTION_DISCONNECTED
                    .equals(intent.getAction())) {
            } else if (MessengerConnectionService.ACTION_CONNECTED
                    .equals(intent.getAction())) {
            } else if (UploadService.KEY_UPDATE_BAR.equals(intent.getAction())) {
                Message vo = intent
                        .getParcelableExtra(MessengerConnectionService.KEY_MESSAGE_OBJECT);
                if (destination.getJabberId().equalsIgnoreCase(vo.getSource())) {
                    boolean change = true;
                    if (vo.getType().equalsIgnoreCase(Message.TYPE_READSTATUS)) {
                        change = false;
                    } else if (vo.getType().equalsIgnoreCase(Message.TYPE_TARIK)) {
                        change = false;
                    }

                    if (change) {
                        updateConversation(vo);
                    }
                }

            } else if (UploadService.KEY_UPDATE_UPLOAD_BAR.equals(intent.getAction())) {
                Message vo = intent
                        .getParcelableExtra(MessengerConnectionService.KEY_MESSAGE_OBJECT);
                if (destination.getJabberId().equals(vo.getDestination())) {
                    boolean change = true;
                    if (vo.getType().equalsIgnoreCase(Message.TYPE_READSTATUS)) {
                        change = false;
                    } else if (vo.getType().equalsIgnoreCase(Message.TYPE_TARIK)) {
                        change = false;
                    }

                    if (change) {
                        updateConversation(vo);
                    }
                }
            } else if (MessengerConnectionService.ACTION_REFRESH_CHAT_HISTORY
                    .equals(intent.getAction())) {
                Message vo = intent
                        .getParcelableExtra(MessengerConnectionService.KEY_MESSAGE_OBJECT);
                if (vo != null) {
                    if (destination.getJabberId().equals(vo.getSource())) {
                        conversations.remove(vo);
                        adapter.add(conversations);
                        adapter.notifyDataSetChanged();
                    }
                }
            } else if (MessengerConnectionService.ACTION_STATUS_CHANGED.equals(intent.getAction())) {
                refreshProfileContact();
            }

        }

    }

    class MessageSender extends AsyncTask<Message, Message, Void> {
        @Override
        protected Void doInBackground(Message... params) {

            Message vo = params[0];
            if (vo.getType().equals(Message.TYPE_TEXT)) {
                binder.sendMessage(vo);
            } else if (vo.getType().equals(Message.TYPE_LOC)) {
                String[] loc = vo.getMessage()
                        .split(Message.LOCATION_DELIMITER);
                vo.setMessage(loc[0] + Message.LOCATION_DELIMITER + loc[1]);
                binder.sendLocation(vo);
            } else {
                binder.sendFile(vo);
            }
            publishProgress(vo);
            return null;
        }

        @Override
        protected void onProgressUpdate(Message... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

    }

    // Uncomment for SMS bridge feature
    // class SMSSender extends AsyncTask<Message, Message, Void> {
    //
    // @Override
    // protected Void doInBackground(Message... params) {
    // Message vo = params[0];
    // InputStreamReader reader = null;
    // try {
    // HttpClient httpClient = HttpHelper
    // .createHttpClient(getApplicationContext());
    // } catch (Exception e) {
    // Log.e(getLocalClassName(),
    // "Error sending SMS: " + e.getMessage(), e);
    // } finally {
    // if (reader != null) {
    // try {
    // reader.close();
    // } catch (IOException e) {
    // }
    // }
    // }
    // return null;
    // }
    //
    // }

    class MessageRetryAllHelper extends AsyncTask<Void, Message, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            Cursor cursor = messengerHelper.query(SQL_SELECT_FAILED_MESSAGES,
                    new String[]{destination.getJabberId()});
            while (cursor.moveToNext()) {
                Message msg = new Message(cursor);
                retryMessage(msg);
            }
            cursor.close();
            return null;
        }

    }

    class ConversationLoader extends AsyncTask<Void, Message, Void> {
        private Cursor cursor;

        @Override
        protected Void doInBackground(Void... params) {
            lastDate = "";
            long lastTotalMessage = totalMessages;
            cursor = messengerHelper.query(
                    SQL_SELECT_TOTAL_MESSAGES,
                    new String[]{destination.getJabberId(),
                            destination.getJabberId()});
            int indexTotal = cursor.getColumnIndex("total");
            while (cursor.moveToNext()) {
                totalMessages = cursor.getLong(indexTotal);
            }
            cursor.close();

            if (lastTotalMessage < totalMessages) {
                clearConversations();
                cursor = messengerHelper.query(SQL_SELECT_MESSAGES, new String[]{
                        destination.getJabberId(), destination.getJabberId(),
                        String.valueOf(loadLimit), String.valueOf(0)});

                ArrayList<Message> messages = new ArrayList<Message>();

                while (cursor.moveToNext()) {
                    Message vo = new Message(cursor);
                    messages.add(0, vo);
                }
                for (Iterator<Message> iterator = messages.iterator(); iterator
                        .hasNext(); ) {
                    Message vo = iterator.next();
                    // Log.w("gufang",vo.getMessage());
                    publishProgress(vo);
                }

                if (loadLimit < totalMessages) {
                    conversations.add(0, Integer.valueOf((int) totalMessages));
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Message... values) {
            addConversation(values[0], false, false);
        }

        @Override
        protected void onPostExecute(Void result) {
            cursor.close();
            adapter.refreshList();
            scrollListConversationToBottom(false);
        }
    }

    class ConversationLoadEarlier extends AsyncTask<Void, Message, Void> {
        private Cursor cursor;
        int count = conversations.size();
        int index = listConversation.getFirstVisiblePosition();
        View v2 = listConversation.getChildAt(0);
        int oldCount = adapter.getCount();
        int pos = (v2 == null ? 0 : v2.getBottom());

        @Override
        protected Void doInBackground(Void... params) {
            cursor = messengerHelper.query(SQL_SELECT_MESSAGES, new String[]{
                    destination.getJabberId(), destination.getJabberId(),
                    String.valueOf(loadLimit), String.valueOf(loadOffset)});

            ArrayList<Message> messages = new ArrayList<Message>();

            while (cursor.moveToNext()) {
                Message vo = new Message(cursor);
                messages.add(vo);
            }
            for (Iterator<Message> iterator = messages.iterator(); iterator
                    .hasNext(); ) {
                Message vo = iterator.next();
                publishProgress(vo);
            }


            return null;
        }

        @Override
        protected void onProgressUpdate(Message... values) {
            addConversationEarlier(values[0], false);
        }

        @Override
        protected void onPostExecute(Void result) {
            cursor.close();
            if ((loadOffset + loadLimit) < totalMessages) {
                conversations.add(0, Integer.valueOf((int) totalMessages));
            }

            adapter.notifyDataSetChanged();
            listConversation.setSelectionFromTop(index + adapter.getCount() - oldCount, pos);
        }
    }

    class blockRequest extends AsyncTask<String, Void, String> {

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();

        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content = null;
        private boolean error = false;
        private Context mContext;

        private MessengerDatabaseHelper messengerHelper;
        String status = "";
        String action;


        public blockRequest(Context context) {
            this.mContext = context;

        }


        @Override
        protected void onPreExecute() {
            pdialog.show();
        }

        protected String doInBackground(String... key) {
            try {

                if (messengerHelper == null) {
                    messengerHelper = MessengerDatabaseHelper.getInstance(getApplicationContext());
                }

                HttpClient httpClient = HttpHelper
                        .createHttpClient(mContext);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);

                Contact contact = messengerHelper.getMyContact();

                String tambah = "0";
                if (headerBtnLock.getText().toString().equalsIgnoreCase("block")) tambah = "1";

                nameValuePairs.add(new BasicNameValuePair("username", contact.getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("key", key[0]));
                nameValuePairs.add(new BasicNameValuePair("tambah", tambah));
                nameValuePairs.add(new BasicNameValuePair("blockme", destination.getJabberId()));

                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                HttpPost post = new HttpPost(URL_ADD_BLOCK);
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

                    if (!content.startsWith("<")) {
                        JSONObject jObject = new JSONObject(content);
                        JSONObject menuitemArray = jObject.getJSONObject("blockrequest");
                        status = menuitemArray.getString("status").toString();
                        action = menuitemArray.getString("action").toString();
                    } else {
                        status = "error";
                    }

                } else {
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
            pdialog.dismiss();
        }

        protected void onPostExecute(String content) {
            pdialog.dismiss();
            if (error) {
                if (content.contains("invalid_key")) {
                    if (NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)) {
                        pdialog.show();
                        String key = new ValidationsKey().getInstance(mContext).key(true);
                        if (key.equalsIgnoreCase("null")) {
                            Toast.makeText(getApplicationContext(), R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                            pdialog.dismiss();
                        } else {
                            new blockRequest(mContext).execute(key);
                        }
                    } else {
                        Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                }
            } else {
                if (status.equalsIgnoreCase("done")) {
                    if (action.equalsIgnoreCase("add")) {
                        blockListDB.open();
                        blockListDB.insertListBlock(destination.getJabberId());
                        blockListDB.close();
                        headerBtnLock.setText("Unblock");
                    } else {
                        blockListDB.open();
                        blockListDB.deleteListBlock(destination.getJabberId());
                        blockListDB.close();
                        headerBtnLock.setText("Block");
                    }
                    showToast("Success");
                } else {
                    showToast("Server Error " + status);
                }
            }
        }

    }


    class addBotRequest extends AsyncTask<String, Void, String> {

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();

        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content = null;
        private boolean error = false;
        private Context mContext;

        private MessengerDatabaseHelper messengerHelper;
        String code = "";
        String desc = "";


        public addBotRequest(Context context) {
            this.mContext = context;

        }


        @Override
        protected void onPreExecute() {
            pdialog.show();
        }

        protected String doInBackground(String... key) {
            try {

                if (messengerHelper == null) {
                    messengerHelper = MessengerDatabaseHelper.getInstance(getApplicationContext());
                }

                HttpClient httpClient = HttpHelper
                        .createHttpClient(mContext);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);

                Contact contact = messengerHelper.getMyContact();

                nameValuePairs.add(new BasicNameValuePair("username", contact.getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("key", key[0]));
                nameValuePairs.add(new BasicNameValuePair("namaroom", destination.getJabberId().toLowerCase()));
                nameValuePairs.add(new BasicNameValuePair("action", "1"));

                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                HttpPost post = new HttpPost(URL_ADD_ROOM);
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

                    if (!content.startsWith("<")) {
                        JSONObject jObject = new JSONObject(content);
                        code = jObject.getString("code");
                        desc = jObject.getString("description");
                        if (!code.equalsIgnoreCase("200")) error = true;
                    } else {
                        code = "400";
                        error = true;
                    }

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
            pdialog.dismiss();
        }

        protected void onPostExecute(String content) {
            pdialog.dismiss();
            if (error) {
                if (content.contains("invalid_key")) {
                    if (NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)) {
                        pdialog.show();
                        String key = new ValidationsKey().getInstance(mContext).key(true);
                        if (key.equalsIgnoreCase("null")) {
                            Toast.makeText(getApplicationContext(), R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                            pdialog.dismiss();
                        } else {
                            new addBotRequest(mContext).execute(key);
                        }
                    } else {
                        Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, desc, Toast.LENGTH_SHORT).show();
                }
            } else {
                if (code.equalsIgnoreCase("200")) {
                    String bot[] = desc.split(";");
                    ContactBot a = null;
                    if (bot.length == 2) {
                        a = new ContactBot(destination.getJabberId().toLowerCase(), bot[0], bot[1], "", "");
                    } else if (bot.length == 3) {
                        a = new ContactBot(destination.getJabberId().toLowerCase(), bot[0], bot[1], bot[2], "");
                    }

                    botListDB.insertScrDetails(a);
                    finish();
                    Intent intent;
                    intent = new Intent(mContext, ConversationActivity.class);
                    intent.putExtra(ConversationActivity.KEY_JABBER_ID, destination.getJabberId().toLowerCase());
                    startActivity(intent);

                    Toast.makeText(mContext, "Success", Toast.LENGTH_LONG);
                } else {
                    showToast(desc);
                }
            }
        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (emojicons.getVisibility() == View.GONE) {
                finish();
            } else {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                textMessage.setFocusableInTouchMode(true);
                textMessage.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(textMessage, InputMethodManager.SHOW_IMPLICIT);
                emojicons.setVisibility(View.GONE);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void startStreaming() {
        Toast.makeText(this, "Start Streaming..", Toast.LENGTH_SHORT).show();
        stopStreaming();
        try {
            startService(serviceIntent);
        } catch (Exception e) {
        }
    }


    private void stopStreaming() {
        try {
            stopService(serviceIntent);
        } catch (Exception e) {
        }
    }

    private BroadcastReceiver broadcastBufferReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent bufferIntent) {
            showProgressDialog(bufferIntent);
        }
    };

    private void showProgressDialog(Intent bufferIntent) {
        String bufferValue = bufferIntent.getStringExtra("buffering");
        int bufferIntValue = Integer.parseInt(bufferValue);
        switch (bufferIntValue) {
            case 0:
                if (progressRadio.getVisibility() == View.VISIBLE) {
                    progressRadio.setVisibility(View.GONE);
                    buttonPlay.setBackgroundResource(R.drawable.ic_radio_stop);
                }
                break;
            case 1:
                progressRadio.setVisibility(View.VISIBLE);
                buttonPlay.setBackgroundResource(R.drawable.ic_radio_play);
                break;
        }
    }

    public static class VideoFragment extends YouTubePlayerSupportFragment {

        public VideoFragment() {
        }

        public static VideoFragment newInstance(String url) {

            VideoFragment f = new VideoFragment();

            Bundle b = new Bundle();
            b.putString("url", url);

            f.setArguments(b);
            f.init();

            return f;
        }

        private void init() {

            initialize("AIzaSyDap-7B5rDb6ckg1q1vLdxEsCGUfWbcs7E", new YouTubePlayer.OnInitializedListener() {

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {
                }

                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                    if (!wasRestored) {
                        player.cueVideo(getArguments().getString("url"));
                    }
                }
            });
        }
    }

    private void refreshProfileContact() {
        String jid = "";
        Cursor c = timeLineDB.getDataByFlag();
        if (c.getCount() > 0) {
            jid = c.getString(c.getColumnIndexOrThrow(TimeLineDB.TIMELINE_JID));
        }

        frameLayoutPicasso.setVisibility(View.GONE);
        logoToolbar.setVisibility(View.VISIBLE);
        imageLoaderFromSD.DeleteImage(MediaProcessingUtil.getProfilePic(jid), logoToolbar);

        Animation fadeOut = new AlphaAnimation(0, 1);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(500);
        logoToolbar.startAnimation(fadeOut);

        imageLoaderFromSD.DisplayImage(MediaProcessingUtil.getProfilePic(jid), logoToolbar, false);

    }

    public void start(String destination_id) {
        ImagePicker.create(this)
                .folderMode(true)
                .reset(true)
                .destination(judul)
                .imageTitle("Tap to select")
                .single()
                .multi()
                .limit(10)
                .showCamera(true)
                .imageDirectory("Camera")
                .origin(images)
                .start(REQUEST_CODE_PICKER);
    }
}