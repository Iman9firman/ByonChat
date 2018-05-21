package com.byonchat.android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.adapter.GroupNoteAdapter;
import com.byonchat.android.adapter.ListViewNoteAdapter;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.communication.NotificationReceiver;
import com.byonchat.android.config.Utils;
import com.byonchat.android.config.WsConfig;
import com.byonchat.android.createMeme.FilteringImage;
import com.byonchat.android.createMeme.ObjectItem;
import com.byonchat.android.createMeme.PhotoSortrActivity;
import com.byonchat.android.list.AttachmentAdapter;
import com.byonchat.android.list.AttachmentAdapter.AttachmentMenuItem;
import com.byonchat.android.list.ConversationAdapter;
import com.byonchat.android.list.ItemCreateNoteGroup;
import com.byonchat.android.list.utilLoadImage.FileCache;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.IntervalDB;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.Skin;
import com.byonchat.android.shortcutBadger.ShortcutBadgeException;
import com.byonchat.android.shortcutBadger.ShortcutBadger;
import com.byonchat.android.utils.DynamicAlertDialogVoting;
import com.byonchat.android.utils.GPSTracker;
import com.byonchat.android.utils.ImageFilePath;
import com.byonchat.android.utils.MediaProcessingUtil;
import com.byonchat.android.utils.UploadService;
import com.byonchat.android.utils.Validations;
import com.codebutler.android_websockets.WebSocketClient;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

public class ConversationGroupActivity extends AppCompatActivity implements EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "MMM dd, yyyy", Locale.getDefault());
    private static final String MENU_GALLERY_TITLE = "Gallery";
    private static final int LIMIT_MESSAGE_LOAD = 15;
    private static final int REQ_CAMERA = 1201;
    private static final int REQ_GALLERY = 1202;
    private static final int REQ_VIDEO = 1203;
    private static final int REQ_GALLERY_VIDEO = 1204;
    private static final int REQ_MEME = 1206;
    private static final int REQ_GALLERY_MEME = 1207;
    private final int REQ_CODE_SPEECH_INPUT = 1208;
    private final int PLACE_PICKER_REQUEST = 1209;

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
            + Message.SOURCE + "=? ) AND "+ Message.TYPE +" != '"+ Message.TYPE_READSTATUS + "' ORDER BY " + Message.ID + " desc, " + Message.SEND_DATE
            + " LIMIT ? OFFSET ?;";
    private static final String SQL_UPDATE_MESSAGES = "UPDATE "
            + Message.TABLE_NAME +" SET status = "+ Message.STATUS_DELIVERED + " WHERE " + Message.ID +" =?;";
    private static String SQL_SELECT_TOTAL_MESSAGES_UNREAD_ALL = "SELECT count(*) total FROM "
            + Message.TABLE_NAME
            + " WHERE status = ?";
    private static String SQL_SELECT_TOTAL_MESSAGES_UNREAD = "SELECT * FROM "
            + Message.TABLE_NAME
            + " WHERE status = ? AND " + Message.SOURCE + "=?;" ;

    private BroadcastHandler broadcastHandler = new BroadcastHandler();
    private static final ArrayList<AttachmentMenuItem> attCameraItems ;
    private static final ArrayList<AttachmentMenuItem> attVideoItems ;
    private static final ArrayList<AttachmentMenuItem> attMemeItems;
    private ConversationAdapter adapter;
    public FileCache fileCache;
    private MessengerDatabaseHelper messengerHelper;
    private List<Object> conversations;
    private EditText textMessage;
    private Button btnAttachmentCamera;
    private Button btnAttachmentMapMarker;
    private Button btnAttachmentVideo;
    private Button btnAttachmentMeme;
    private LinearLayout emojicons;
    private String colorAttachment = "#005982";
    private ImageButton btnMic,btn_add_emoticon;
    private String lastDate = "";
    private AdapterView.AdapterContextMenuInfo itemContextSelected;
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
    private ArrayList<AttachmentAdapter.AttachmentMenuItem> curAttItems;
    private int attCurReq = 0;
    private String cameraFileOutput;
    private String fileToSend;
    protected ProgressDialog pdialog;
    private Thread splashTread;
    private Thread fileendTread;
    long totalSize = 0;
    GPSTracker gps;
    String titleTheme = "";

    private Intent serviceIntent;
    private static boolean isStreaming = false;
    private boolean mBufferBroadcastIsRegistered;
    Toolbar toolbar;
    ImageView logoToolbar;
    TextView titleToolbar;
    LinearLayout mRevealView;
    boolean hidden = true;
    boolean hiddenNote = false;
    boolean hiddenVoting = false;


    // LogCat tag
    private static final String TAG = ConversationGroupActivity.class.getSimpleName();

    private Button btnSend;
    private RelativeLayout headerButtonNote;
    private RelativeLayout headerButtonVoting;
    // private ImageButton btnViewNote;
    private ListView listViewNote;
    private EditText inputMsg;
    private View footerView;

    ListViewNoteAdapter adapterNotes,adapterVoting;

    // private WebSocketClient client;


    private Utils utils;

    // Client name
    private String name = null, groupId = null, newPerson = null, sticky = null;
    private String title = null;

    // JSON flags to identify the kind of JSON response
    private static final String TAG_SELF = "self", TAG_NEW = "new",
            TAG_MESSAGE = "message", TAG_EXIT = "exit";

    public static final String EXTRA_KEY_GROUP_ID = "JABBER_ID";
    public static final String EXTRA_KEY_USERNAME = "name";
    public static final String EXTRA_KEY_NEW_PERSON = "newPerson";
    public static final String EXTRA_KEY_STICKY = "sticky";
    private String checkMessageid = "";
    private ListView listConversation;
    ProgressDialog progress;

    private static final int DLG_EXAMPLE1 = 0;
    private static final int TEXT_ID = 0;
    GridView gridView;
    ArrayList<ItemCreateNoteGroup> gridArray;
    GroupNoteAdapter customGridAdapter;

    static {
        attCameraItems = new ArrayList<AttachmentAdapter.AttachmentMenuItem>();
        attCameraItems.add(new AttachmentMenuItem(R.drawable.ic_att_photo,
                "Camera"));
        attCameraItems.add(new AttachmentMenuItem(R.drawable.ic_att_gallery,
                MENU_GALLERY_TITLE));

        attVideoItems = new ArrayList<AttachmentAdapter.AttachmentMenuItem>();
        attVideoItems.add(new AttachmentMenuItem(R.drawable.ic_att_video,
                "Camcorder"));
        attVideoItems.add(new AttachmentMenuItem(R.drawable.ic_att_gallery,
                MENU_GALLERY_TITLE));

        attMemeItems = new ArrayList<AttachmentAdapter.AttachmentMenuItem>();
        attMemeItems.add(new AttachmentMenuItem(R.drawable.ic_att_photo,
                "Camera Meme"));
        attMemeItems.add(new AttachmentMenuItem(R.drawable.ic_att_gallery,
                MENU_GALLERY_TITLE));
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(textMessage, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(textMessage);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_group);
        // Getting the person name from previous screen
        Intent i = getIntent();
        groupId = i.getStringExtra(EXTRA_KEY_GROUP_ID);
        name = i.getStringExtra(EXTRA_KEY_USERNAME);
        newPerson = i.getStringExtra(EXTRA_KEY_NEW_PERSON);
        sticky = i.getStringExtra(EXTRA_KEY_STICKY); // stiki 1/0 untuk menandakan yang stiki mana

        if (messengerHelper==null){
            messengerHelper = MessengerDatabaseHelper.getInstance(this);
        }

        if(title==null){
            title = messengerHelper.getGroup(groupId).getName();
        }
        if(name==null){
            name = messengerHelper.getMyContact().getJabberId();
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

        toolbar = (Toolbar)findViewById(R.id.abMain);
        setSupportActionBar(toolbar);
        logoToolbar = (ImageView)findViewById(R.id.logo_toolbar);
        View view = findViewById(R.id.layout_back_button);
        titleToolbar = (TextView)findViewById(R.id.title_toolbar);
        mRevealView = (LinearLayout) findViewById(R.id.reveal_items);
        mRevealView.setVisibility(View.INVISIBLE);
        listConversation = (ListView) findViewById(R.id.listConversation);
        btnSend = (Button) findViewById(R.id.btnSend);

        headerButtonNote = (RelativeLayout) findViewById(R.id.headerButtonNote);
        headerButtonVoting = (RelativeLayout) findViewById(R.id.headerButtonVoting);
        listViewNote = (ListView) findViewById(R.id.listViewNote);

        ObjectItem[] ObjectItemData = new ObjectItem[8];
        ObjectItemData[0] = new ObjectItem(1, "laperasdasdasda sdasd as d asd a sd as da sd asd a sdadasdasdasdasdasdasdasd ");
        ObjectItemData[1] = new ObjectItem(2, "Watson ipsum dolor sit amet consecte laperasd");
        ObjectItemData[2] = new ObjectItem(3, "laperasdasdasda sdasd as d asd a sd as da sd asd a sdadasdasdasdasdasdasdasd ");
        ObjectItemData[3] = new ObjectItem(4, "Puregold asd a sd as da sd asd");
        ObjectItemData[4] = new ObjectItem(5, "lorem ipsum dolor sit amet consecte laperasdasdasda sdasd as d asd a sd as da sd asd a sdadasdasdasdasdasdasdasd ");
        ObjectItemData[5] = new ObjectItem(6, "SManaa msdmasdjknasd aksdmasodmasd asd");
        ObjectItemData[6] = new ObjectItem(7, "SM asdasd asdiasdkasdaldslalsd ");
        ObjectItemData[7] = new ObjectItem(8, "SMaaa asdasdkaokdsadsokasd asdkaosdkoasdkasd");
        adapterNotes = new ListViewNoteAdapter(this, R.layout.row_list_note, ObjectItemData,"NOTE");
        adapterVoting = new ListViewNoteAdapter(this, R.layout.row_list_voting, ObjectItemData,"VOTING");

        footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_button_up, null, false);
        listViewNote.addFooterView(footerView);
        listViewNote.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(hiddenNote){
                    Intent intentOpen = new Intent(getApplicationContext(), DetailGroupNote.class);
                    startActivity(intentOpen);
                }else{
                    Intent intentOpen = new Intent(getApplicationContext(), DetailGroupVoting.class);
                    startActivity(intentOpen);
                }
            }
        });
        footerView.setOnClickListener(btnClickListener);

        utils = new Utils(getApplicationContext());
        btnSend.setOnClickListener(btnClickListener);
        headerButtonVoting.setOnClickListener(btnClickListener);
        headerButtonNote.setOnClickListener(btnClickListener);
        btnMic =  (ImageButton) findViewById(R.id.btnMic);
        btnMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        convLayout = (LinearLayout) findViewById(R.id.conversation_layout);
        mainconLayout = (LinearLayout) findViewById(R.id.conversation_layout_main);
        mainRadioStreaming = (LinearLayout) findViewById(R.id.mainRadioStreaming);
        mainVideoStreaming = (LinearLayout) findViewById(R.id.mainVideoStreaming);
        converVdovia = (LinearLayout) findViewById(R.id.converVdovia);
        converStreaming = (LinearLayout) findViewById(R.id.converStreaming);
        linearBanner = (LinearLayout) findViewById(R.id.linearBanner);
        mainVdovia = (RelativeLayout) findViewById(R.id.mainVdovia);
        mainStreaming = (RelativeLayout) findViewById(R.id.mainStreaming);
        nameRadio = (TextView)findViewById(R.id.nameRadio);
        infoRadio = (TextView)findViewById(R.id.infoRadio);
        buttonPlay = (Button)findViewById(R.id.buttonPlay);
        progressRadio = (ProgressBar) findViewById(R.id.progressRadio);
        buttonNext = (Button)findViewById(R.id.buttonNext);
        progresVdopia = (ProgressBar)findViewById(R.id.progresVdopia);

        initText();
        if (attCurReq > 0) {
            showAttachmentDialog(attCurReq);
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setBarTitle();

        IntervalDB db = new IntervalDB(this);
        db.open();
        Cursor cursorSelect = db.getSingleContact(4);
        if(cursorSelect.getCount()>0){
            String skin = cursorSelect.getString(cursorSelect.getColumnIndexOrThrow(IntervalDB.COL_TIME));
            Skin skins = null;
            Cursor c =  db.getCountSkin();
            if(c.getCount()>0) {
                skins =  db.retriveSkinDetails(skin);
                colorAttachment = skins.getColor();
                BitmapDrawable bitmapDrawable = new BitmapDrawable(skins.getBackground());
                bitmapDrawable.setTileModeXY(android.graphics.Shader.TileMode.REPEAT, android.graphics.Shader.TileMode.REPEAT);
                //background bitu
                // convLayout.setBackground(bitmapDrawable);
                Bitmap back_bitmap = FilteringImage.headerColor(getWindow(), ConversationGroupActivity.this, Color.parseColor(colorAttachment));
                BitmapDrawable back_draw = new BitmapDrawable(getResources(), back_bitmap);
                toolbar.setBackground(back_draw);

            }
            c.close();
        }
        cursorSelect.close();
        db.close();


        if (conversations == null) {
            conversations = new ArrayList();
            adapter = new ConversationAdapter(this, this.getApplicationContext(), name,
                    groupId, conversations);
            adapter.setBtnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //  loadLimit += LIMIT_MESSAGE_LOAD;
                    // loadOffset += LIMIT_MESSAGE_LOAD;
                    //   refreshConversation();
                    //   scrollListConversationToTop();
                    // if(conversations.get(conversations.size()-1))
                    //  new ConversationLoadEarlier().execute();

                    if(listConversation.getHeaderViewsCount()==1){
                        Object obj = listConversation.getAdapter().getItem(2);
                        String value = obj.toString();
                        if (isValidDate(value)){
                            conversations.remove(0);
                        }
                    }else{
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

      /*kirim pesan
       //inputMsg = (EditText) findViewById(R.id.inputMsg);
        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
              *//*  planetList.add(inputMsg.getText().toString());
                adapter.notifyDataSetChanged();*//*
                if (stickyon) order_sticky++;
                int status = 0;
                String messageid = getRandomString();
                checkMessageid = messageid;
                String useropenmessage = "0";
                sendMessageToServer(
                        utils.getSendMessageGroupJSON(
                                inputMsg.getText().toString(), "text",
                                vbolsticky, order_sticky, groupId,
                                status, messageid, useropenmessage),
                        inputMsg.getText().toString(),
                        "text", vbolsticky, order_sticky,
                        groupId,
                        status, messageid, useropenmessage,
                        name);
                inputMsg.setText("");
            }
        });*/



        /**
         * Creating web socket client. This will have callback methods
         * */
        String[] params = {URLEncoder.encode(name),
                URLEncoder.encode(groupId),
                URLEncoder.encode(newPerson)};
        client = new WebSocketClient(URI.create(constructUri(params)),
                new WebSocketClient.Listener() {
                    @Override
                    public void onConnect() {
                        Log.d(TAG, "hore bisa connect");
                    }

                    @Override
                    public void onMessage(String message) {
                        Log.d(TAG, String.format("Got string message! %s", message));
                        parseMessage(message);
                    }

                    @Override
                    public void onMessage(byte[] data) {
                        Log.d(TAG, String.format("Got binary message! %s",
                                bytesToHex(data)));

                        // Message will be in JSON format
                        parseMessage(bytesToHex(data));
                    }

                    @Override
                    public void onDisconnect(int code, String reason) {

                        String message = String.format(Locale.US,
                                "Disconnected! Code: %d Reason: %s", code, reason);

                        Log.e(TAG, "DISCONNECT SOCKET! : " + message);

                        //  showToast("Anda sekarang keluar dari group.");
                    }

                    @Override
                    public void onError(Exception error) {
                        Log.e(TAG, "################# On Error! : " + error);
                    }

                }, null);

        client.connect();

        /*final String confirmation = getIntent().getStringExtra(ConversationActivity.KEY_FILE_CONFIRMATION);
        if (confirmation != null) {
            final String fileSend[] = confirmation.split(";");
            String caption= "";
            if (fileSend.length==3){
                caption =  fileSend[2];
            }
            Message vo = createNewMessage(fileSend[0] + ";linkUrl;"+caption, fileSend[1]);
            messengerHelper.insertData(vo);
            FilesDatabaseHelper dbUpload = new FilesDatabaseHelper(this);
            Files files = new Files((int) vo.getId(),"0","upload");
            dbUpload.open();
            dbUpload.insertFilesUpload(files);
            dbUpload.close();
            addConversation(vo);

            Intent intent = new Intent(this, UploadService.class);
            intent.putExtra(UploadService.ACTION,"getLinkUpload");
            intent.putExtra(UploadService.KEY_MESSAGE, vo);
            startService(intent);

            *//*adapter.refreshList();*//*
        }
*/
    }

    public void updateMessageUnread(){

        Cursor countMessageUnread = messengerHelper.query(
                SQL_SELECT_TOTAL_MESSAGES_UNREAD,
                new String[] {String.valueOf(Message.STATUS_UNREAD),groupId});

        while (countMessageUnread.moveToNext()) {
            messengerHelper.execSql(SQL_UPDATE_MESSAGES,new String[] { countMessageUnread.getString(countMessageUnread.getColumnIndex("_id"))});
            /*String regex = "[0-9]+";
            if (destination.getJabberId().matches(regex)) {
                if(!countMessageUnread.getString(countMessageUnread.getColumnIndex(Message.TYPE)).equalsIgnoreCase(Message.TYPE_READSTATUS)){
                    readFunction( countMessageUnread.getString(countMessageUnread.getColumnIndex("packet_id")));
                }
            }*/

        }
        countMessageUnread.close();

        try {
            int badgeCount = 0;
            Cursor cursor = messengerHelper.query(
                    SQL_SELECT_TOTAL_MESSAGES_UNREAD_ALL,
                    new String[] {String.valueOf(Message.STATUS_UNREAD)});
            int indexTotal = cursor.getColumnIndex("total");
            while (cursor.moveToNext()) {
                badgeCount = cursor.getInt(indexTotal);
            }
            cursor.close();

            ShortcutBadger.setBadge(getApplicationContext(), badgeCount);
        } catch (ShortcutBadgeException e) {
        }

        gridArray = new ArrayList<ItemCreateNoteGroup>();

        gridArray.add(new ItemCreateNoteGroup("NOTE 1"));
        gridArray.add(new ItemCreateNoteGroup("NOTE 2"));
        gridArray.add(new ItemCreateNoteGroup("NOTE 3"));
        gridArray.add(new ItemCreateNoteGroup("NOTE 4"));
        gridArray.add(new ItemCreateNoteGroup("NOTE 5"));
        gridArray.add(new ItemCreateNoteGroup("NOTE 6"));
        gridArray.add(new ItemCreateNoteGroup("NOTE 7"));
        gridArray.add(new ItemCreateNoteGroup("NOTE 8"));


        gridView = (GridView) findViewById(R.id.gridView1);
        customGridAdapter = new GroupNoteAdapter(this, R.layout.row_note_create, gridArray);
        gridView.setAdapter(customGridAdapter);
        gridView.setVisibility(View.GONE);

    }

    private Message createNewMessage(String message, String type) {
        String messageid = getRandomString();
        Message vo = new Message(messengerHelper.getMyContact().getJabberId(), groupId, message);
        vo.setType(type);
        vo.setSendDate(new Date());
        vo.setStatus(Message.STATUS_INPROGRESS);
        vo.setPacketId(messageid);
        vo.setGroupChat(true);
        vo.setSourceInfo(messengerHelper.getMyContact().getJabberId());
        return vo;
    }


    private void setBarTitle() {
        if(title.length()>0){
            titleToolbar.setText(title);
        }
        Bitmap icon2 = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.drawable.ic_group);
        logoToolbar.setImageBitmap(icon2);
        titleToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*harusnya ke info
                Contact c = (Contact) destination;
                Intent intent = new Intent(getApplicationContext(), ProfileInfoActivity.class);
                intent.putExtra(ProfileInfoActivity.KEY_JABBER_ID,
                        c.getJabberId());
                intent.putExtra(ProfileInfoActivity.KEY_REFERENCE,
                        ProfileInfoActivity.REFERENCE_CONVERSATION);
                startActivity(intent);*/
            }
        });
    }

    private String constructUri(String[] params){

        String s = WsConfig.URL_WEBSOCKET;
        String[] p = s.split("\\|");
        s = p[0];
        for(int i=0; i<params.length; i++){
            p[i+1] = p[i+1]+"="+params[i];
            s = s+p[i+1];
        }
        Log.i(TAG, "CONSTRUCT URI : " + s);
        return s;
    }

    /**
     * Method to send message to web socket server
     * */
    private void sendMessageToServer(String messageToServer,
                                     String msg, String type,
                                     int sticky, int order_sticky,
                                     String groupId,
                                     int status, String messageid, String useropenmessage,
                                     String name) {
        Date date= new Date();
        /*if (client != null && client.isConnected()) {
            client.send(messageToServer);
            Message vo = new Message(name, groupId,msg);
            vo.setPacketId(messageid);
            vo.setType(Message.TYPE_TEXT);
            vo.setSendDate(date);
            vo.setGroupChat(true);
            messengerHelper.insertData(vo);
            addConversation(vo);
        }*/
    }
    private synchronized void addConversation(Message vo) {
        addConversation(vo, true, false);
    }
    private synchronized void updateConversationProgressBar(Message vo) {
        updateMessageUnread();
        Object obj = vo;
        int index = conversations.indexOf(vo);
        if(vo.getType().equals(Message.TYPE_IMAGE) || vo.getType().equals(Message.TYPE_VIDEO)){
            groupMessages = new ArrayList<Object>();
            groupMessages.add("");
            groupMessages.add(vo);
            index = conversations.indexOf(groupMessages);
            obj = groupMessages;
        }

        if (index != -1) {
            conversations.set(index, obj);
            adapter.refreshList();
        } else {
            addConversation(vo, true, true);
        }
    }

    private synchronized void  updateConversation(Message vo) {
        //blm ada updateMessageUnread();
        int index = conversations.indexOf(vo);
        if (index != -1) {
            conversations.set(index, vo);
            adapter.refreshList();
        } else {
            addConversation(vo, true, true);
        }
    }


    /**
     * Parsing the JSON message received from server The intent of message will
     * be identified by JSON node 'flag'. flag = self, message belongs to the
     * person. flag = new, a new person joined the conversation. flag = message,
     * a new message received from server. flag = exit, somebody left the
     * conversation.
     * */
    private void parseMessage(final String msg) {

        try {
            JSONObject jObj = new JSONObject(msg);

            // JSON node 'flag'
            String flag = jObj.getString("flag");
            String message = jObj.getString("message");
            String groupid = "";

            // if flag is 'self', this JSON contains session id
            if (flag.equalsIgnoreCase(TAG_SELF)) {

                // Save the session id in shared preferences
                String sessionId = jObj.getString("sessionId");
                utils.storeSessionId(sessionId);
                groupid = jObj.getString("groupId");

            } else if (flag.equalsIgnoreCase(TAG_NEW)) {
                // If the flag is 'new', new person joined the room
                groupid = jObj.getString("groupId");
                boolean isSelf = true;
                String name = jObj.getString("name");
                String fromName = name;
                String type = jObj.getString("type");
                boolean sticky = jObj.getBoolean("sticky");
                int order_sticky = jObj.getInt("order_sticky");
                int status = jObj.getInt("status");
                String messageid = jObj.getString("messageid");
                String useropenmessage = jObj.getString("useropenmessage");
                String onlineCount = jObj.getString("onlineCount");
                String dateTimestamp = jObj.getString("timestamp");


            } else if (flag.equalsIgnoreCase(TAG_MESSAGE)) {
                // if the flag is 'message', new message received
                groupid = jObj.getString("groupId");
                boolean isSelf = true;
                String name = jObj.getString("name");
                String fromName = name;
                String type = jObj.getString("type");
                boolean sticky = jObj.getBoolean("sticky");
                int order_sticky = jObj.getInt("order_sticky");
                int status = jObj.getInt("status");
                String messageid = jObj.getString("messageid");
                String useropenmessage = jObj.getString("useropenmessage");
                String dateTimestamp = jObj.getString("timestamp");
                // Checking if the message was sent by you
                String sessionId = jObj.getString("sessionId");
                if (!sessionId.equals(utils.getSessionId())) {
                    fromName = jObj.getString("name");
                    isSelf = false;
                }
                Date date= new Date();
                String sId = jObj.getString("sid");

                //  addList(name,message,messageid,dateTimestamp);
            }
            else if (flag.equalsIgnoreCase(TAG_EXIT)) {
                // If the flag is 'exit', somebody left the conversation
                groupid = jObj.getString("groupId");
                boolean isSelf = true;
                String name = jObj.getString("name");
                String fromName = name;
                String type = jObj.getString("type");
                boolean sticky = jObj.getBoolean("sticky");
                int order_sticky = jObj.getInt("order_sticky");
                int status = jObj.getInt("status");
                String messageid = jObj.getString("messageid");
                String useropenmessage = jObj.getString("useropenmessage");
                // number of people online
                String onlineCount = jObj.getString("onlineCount");
                String dateTimestamp = jObj.getString("timestamp");
                String sessionId = jObj.getString("sessionId");


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void addList(final String fromName,final String message,final String msgId,final String msgDate) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (!fromName.equalsIgnoreCase(messengerHelper.getMyContact().getJabberId())) {
                    Message vo = new Message(groupId, messengerHelper.getMyContact().getJabberId(), message);
                    vo.setSourceInfo(fromName);
                    vo.setPacketId(msgId);
                    vo.setType(Message.TYPE_TEXT);
                    vo.setSendDate(new Date());
                    vo.setGroupChat(true);
                    messengerHelper.insertData(vo);
                    updateConversation(vo);
                }

             /*   planetList.add(message);
                adapter.notifyDataSetChanged();
*/
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_CODE_SPEECH_INPUT) {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = result.get(0);
                    if(textMessage.getText().length()>0) text= textMessage.getText().toString()+ " "+ result.get(0);
                    textMessage.setText(text);
                    textMessage.setSelection(textMessage.getText().length());
                }
            }else if (requestCode == REQ_CAMERA) {
                if(decodeFile(cameraFileOutput)){
                    Intent intent = new Intent(getApplicationContext(), ConfirmationSendFile.class);
                    intent.putExtra("file", cameraFileOutput);
                    intent.putExtra("name", groupId);
                    intent.putExtra("type", Message.TYPE_IMAGE);
                    intent.putExtra("from", "group");
                    startActivity(intent);

                }

            }else if
                    (requestCode == REQ_MEME) {
                if(decodeFile(cameraFileOutput)){
                    Intent intent = new Intent(getApplicationContext(), PhotoSortrActivity.class);
                    intent.putExtra("file",cameraFileOutput);
                    intent.putExtra("name",groupId);
                    intent.putExtra("type", Message.TYPE_IMAGE);
                    startActivity(intent);
                }

            } else if (requestCode == PLACE_PICKER_REQUEST
                    && resultCode == Activity.RESULT_OK) {
             /*   final Place place = PlacePicker.getPlace(data, this);
                final String name = place.getName()!=null? (String) place.getName() :" ";
                final String address = place.getAddress()!=null? (String) place.getAddress() :" ";
                final String web = String.valueOf(place.getWebsiteUri() != null ? place.getWebsiteUri() : " ");
                new CountDownTimer(300, 1000) {

                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        sendLocation(place.getLatLng().latitude + ";" + place.getLatLng().longitude+";"+name +";"+address+";"+web);

                    }
                }.start();*/
            }else{
                Uri selectedUri = data.getData();
                String selectedImagePath = ImageFilePath.getPath(getApplicationContext(), selectedUri);
                File fileOutput = new File(selectedImagePath);
                if (requestCode == REQ_VIDEO) {
                    Intent intent = new Intent(getApplicationContext(), ConfirmationSendFile.class);
                    intent.putExtra("file",fileOutput.getAbsolutePath());
                    intent.putExtra("name",groupId);
                    intent.putExtra("type", Message.TYPE_VIDEO);
                    startActivity(intent);

                } else if (requestCode == REQ_GALLERY
                        || requestCode == REQ_GALLERY_VIDEO||requestCode == REQ_GALLERY_MEME) {
                    Intent intent = new Intent(getApplicationContext(), ConfirmationSendFile.class);

                    String type = Message.TYPE_VIDEO;
                    if (requestCode == REQ_GALLERY) {
                        type = Message.TYPE_IMAGE;
                        if (fileOutput.length() > 1000000L) {
                          /*  File f = resizeImage(fileOutput, true);
                            fileOutput = f;*/
                        }
                    }else if (requestCode == REQ_GALLERY_MEME){
                        intent = new Intent(getApplicationContext(), PhotoSortrActivity.class);
                        type = Message.TYPE_IMAGE;
                    }
                    intent.putExtra("file",fileOutput.getAbsolutePath());
                    intent.putExtra("name",groupId);
                    intent.putExtra("type",type);
                    intent.putExtra("from", "group");
                    startActivity(intent);
                }
            }

        }else if(resultCode==RESULT_CANCELED) {

        }
    }

    private void sendLocation(String message) {
        Message vo = createNewMessage(message, Message.TYPE_LOC);
        addConversation(vo);

        messengerHelper.insertData(vo);
        Intent intent = new Intent(this, UploadService.class);
        intent.putExtra(UploadService.ACTION, "sendTextGroup");
        intent.putExtra(UploadService.KEY_MESSAGE, vo);
        startService(intent);

    }

    public  boolean decodeFile(String path) {
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
                if (f.exists ()) f.delete ();
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
                if (f.exists ()) f.delete ();
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
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                m.postRotate(270);
                bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);
                final File f = new File(path);
                if (f.exists ()) f.delete ();
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
            if (f.exists ()) f.delete ();
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
    protected void onDestroy() {
        super.onDestroy();
        finish();

    }

    @Override
    protected void onPause() {
        unregisterReceiver(broadcastHandler);
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMessageUnread();
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                .cancel(NotificationReceiver.NOTIFY_ID);
        IntentFilter filter = new IntentFilter(
                MessengerConnectionService.ACTION_MESSAGE_RECEIVED);
        filter.addAction(MessengerConnectionService.ACTION_MESSAGE_DELIVERED);
        filter.addAction(MessengerConnectionService.ACTION_MESSAGE_SENT);
        filter.addAction(MessengerConnectionService.ACTION_MESSAGE_FAILED);
        filter.addAction(MessengerConnectionService.ACTION_CONNECTED);
        filter.addAction(MessengerConnectionService.ACTION_DISCONNECTED);
        filter.addAction(UploadService.KEY_UPDATE_BAR);
        filter.addAction(UploadService.KEY_UPDATE_UPLOAD_BAR);
        filter.setPriority(1);
        registerReceiver(broadcastHandler, filter);
        refreshConversation();

    }



    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private String getRandomString(){
        long currentTimeMillis = System.currentTimeMillis();
        SecureRandom random = new SecureRandom();

        char[] CHARSET_AZ_09 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        char[] result = new char[15];
        for (int i = 0; i < result.length; i++) {
            int randomCharIndex = random.nextInt(CHARSET_AZ_09.length);
            result[i] = CHARSET_AZ_09[randomCharIndex];
        }

        String resRandom = String.valueOf(currentTimeMillis)+ new String(result);

        return resRandom;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.conversation_group_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void showAttachmentDialog(int req) {
        if (req == REQ_CAMERA) {
            curAttItems = attCameraItems;
        } else if(req == REQ_VIDEO){
            curAttItems = attVideoItems;
        }else{
            curAttItems = attMemeItems;
        }
        attCurReq = req;

        AttachmentAdapter adapter = new AttachmentAdapter(this,
                R.layout.menu_item, R.id.textMenu, curAttItems);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.attachment_gridview);
        GridView gridview = (GridView)dialog.findViewById(R.id.gridview);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                String iTitle = curAttItems.get(pos).getTitle();
                String action = Intent.ACTION_GET_CONTENT;
                int req;
                Intent i ;
                if (R.drawable.ic_att_video == curAttItems.get(0)
                        .getResourceIcon()) {
                    i = new Intent();
                    if (MENU_GALLERY_TITLE.equals(iTitle)) {
                        req = REQ_GALLERY_VIDEO;
                        i.setType("video/*");
                    } else {
                        action = MediaStore.ACTION_VIDEO_CAPTURE;
                        req = REQ_VIDEO;
                    }
                } else if(curAttItems.get(0).getTitle()=="Camera Meme") {
                    i = new Intent();
                    if (MENU_GALLERY_TITLE.equals(iTitle)) {
                        req = REQ_GALLERY_MEME;
                        i.setType("image/*");
                    } else {
                        action = MediaStore.ACTION_IMAGE_CAPTURE;
                        File f = MediaProcessingUtil
                                .getOutputFile("jpeg");
                        cameraFileOutput = f.getAbsolutePath();
                        i.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(f));
                        req = REQ_MEME;
                    }

                } else {
                    i = new Intent();
                    if (MENU_GALLERY_TITLE.equals(iTitle)) {
                        if (Build.VERSION.SDK_INT < 19){
                            i = new Intent();
                            i.setAction(Intent.ACTION_GET_CONTENT);
                            i.setType("image/*");
                        } else {
                            i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            i.addCategory(Intent.CATEGORY_OPENABLE);
                            i.setType("image/*");
                        }
                        req = REQ_GALLERY;
                    } else {
                        action = MediaStore.ACTION_IMAGE_CAPTURE;
                        File f = MediaProcessingUtil
                                .getOutputFile("jpeg");
                        cameraFileOutput = f.getAbsolutePath();
                        i.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(f));
                        req = REQ_CAMERA;
                    }
                }
                i.setAction(action);
                startActivityForResult(i, req);
                dialog.dismiss();
                attCurReq = 0;

            }
        });
        dialog.show();
    }

    private void refreshConversation() {
        new ConversationLoader().execute();
    }

    private void promptSpeechInput() {
        showAttc(false);

        Contact contact = messengerHelper.getMyContact();
        String language =  String.valueOf(Locale.getDefault());
        if(contact.getJabberId().substring(0,2).equalsIgnoreCase("62")) language =  "id";

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
    public static WebSocketClient client;

    private void sendMessage(String message,boolean sendTextGroupSticky) {
        int status = 0;
        String messageid = getRandomString();
        checkMessageid = messageid;
        String useropenmessage = "0";
       /* sendMessageToServer(
                utils.getSendMessageGroupJSON(
                        message, "text",
                        vbolsticky, order_sticky, groupId,
                        status, messageid, useropenmessage),
                message,
                "text", vbolsticky, order_sticky,
                groupId,
                status, messageid, useropenmessage,
                name);
*/

        Message vo = new Message( messengerHelper.getMyContact().getJabberId(),groupId, message);
        vo.setSourceInfo(messengerHelper.getMyContact().getJabberId());
        vo.setPacketId(messageid);
        if(sendTextGroupSticky){
            vo.setType(Message.TYPE_STICKY);
        }else {
            vo.setType(Message.TYPE_TEXT);
        }

        vo.setSendDate(new Date());
        vo.setStatus(Message.STATUS_INPROGRESS);
        vo.setGroupChat(true);
        messengerHelper.insertData(vo);
        addConversation(vo);

        Intent intent = new Intent(this, UploadService.class);
        intent.putExtra(UploadService.ACTION, "sendTextGroup");
        intent.putExtra(UploadService.KEY_MESSAGE, vo);
        startService(intent);


    /* String message
      Message vo = createNewMessage(message, Message.TYPE_TEXT);
        addConversation(vo);

        messengerHelper.insertData(vo);
        Intent intent = new Intent(this, UploadService.class);
        intent.putExtra(UploadService.ACTION, "sendText");
        intent.putExtra(UploadService.KEY_MESSAGE, vo);
        startService(intent);*/

    }
    private void initText() {
        textMessage = (EditText) findViewById(R.id.textMessage);
        textMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendMessage(textMessage.getText().toString(), false);
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
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(textMessage, InputMethodManager.SHOW_IMPLICIT);
                if (emojicons.getVisibility() == View.VISIBLE) {
                    emojicons.setVisibility(View.GONE);
                }

                return false;
            }
        });

        textMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                showAttc(false);
                if (hasFocus) {
                    scrollListConversationToBottom(true);
                }
            }
        });

    }

    private void scrollListConversationToBottom(boolean scroll) {
        if(scroll){
            listConversation.postDelayed(new Runnable() {
                @Override
                public void run() {
                    listConversation.setSelection(adapter.getCount() - 1);
                }
            }, 300);
        }else{
            listConversation.setSelection(adapter.getCount());
        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
           /*     intent = new Intent(this, MainActivity.class);
                intent.putExtra("from","2" );
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);*/
                if(gridView.getVisibility()==View.VISIBLE){
                    gridView.setVisibility(View.GONE);
                }else{
                    finish();
                }

                return true;
            case R.id.menu_conversation_group:
                intent = new Intent(this, GroupInfoActivity.class);
                intent.putExtra(GroupInfoActivity.EXTRA_KEY_GROUP_JID,
                        groupId);
                startActivity(intent);

                return true;
            case R.id.menu_conversation_attachment:
                showAttc(true);
                return true;
            case R.id.menu_create_note:
                createExampleDialog();
                // showDialog(DLG_EXAMPLE1);
            /*    intent = new Intent(this, GroupNoteCreateActivity.class);
                intent.putExtra(GroupInfoActivity.EXTRA_KEY_GROUP_JID,
                        groupId);
                startActivity(intent);*/

                return true;
            case R.id.menu_create_voting:
                //  create voting
              /*  DynamicDialogSelfLayoutVoting apalah = DynamicDialogSelfLayoutVoting.newInstance(this,groupId,"","terserah");
                apalah.show(this.getSupportFragmentManager(),"balala");*/

                //{"voting" : "yuhu","name" : "628158888248","groupid" : "1456303104388UZ4E3MHWTOYA03W","dateTime" : "2016-02-26 16:02:49.428","messageid" : "","voting_id" : "111222333","choice_answers" : "qqqq|wwww","total_choice_answer" : "2","sifat_voting" : "0","voting_timer" : "0"}
                String aa = (groupId+"_#_"+name+"_#_"+/*voting_id*/"111222333"+"_#_"+/*choice_answers*/"qqqq|wwww"+"_#_"+2+"_#_"+/*datetime*/"2016-02-26 16:02:49.428"+"_#_"+/*sifat_voting*/"0"+"_#_"+/*voting_timer*/"0");

                DynamicAlertDialogVoting apalah = DynamicAlertDialogVoting.newInstance(this,"pertanyaan" ,R.drawable.ic_notif ,"yuhu", true, true,aa);
                apalah.show(this.getSupportFragmentManager(),"balala");
                return true;
            case R.id.menu_exit:
                SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
                sendPostReqAsyncTask.execute(new String[]{MessengerConnectionService.GROUP_SERVER + "disconnect", messengerHelper.getMyContact().getJabberId()});
                return true;
            case R.id.menu_edit_note:
                gridView.setVisibility(View.VISIBLE);
                return true;
            case R.id.menu_edit_voting:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called to create a dialog to be shown.
     */
    @Override
    protected Dialog onCreateDialog(int id) {

        switch (id) {
            case DLG_EXAMPLE1:
                return createExampleDialog();
            default:
                return null;
        }
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {

        switch (id) {
            case DLG_EXAMPLE1:
                EditText text = (EditText) dialog.findViewById(TEXT_ID);
                text.setText("");
                break;
        }
    }

    private Dialog createExampleDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create Sticky Note");
        builder.setMessage("What is your note:");

        // Use an EditText view to get user input.
        final EditText input = new EditText(this);
        input.setId(TEXT_ID);
        builder.setView(input);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                sendMessage(value,true);
                return;
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        return builder.create();
    }

    private void requestLocationInfo() {
        gps = new GPSTracker(ConversationGroupActivity.this);

        LocationManager locManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if(locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (gps.canGetLocation()) {
                // final Message vo = createNewMessage("", Message.TYPE_LOC);
                //addConversation(vo);
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();
                if (latitude == 0 && longitude == 0){
                    //  showToast("Location not available");
                } else {
                    // vo.setMessage(latitude + ";" + longitude);
                    // binder.sendLocation(vo);
                    /*try {
                        LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
                                new LatLng(latitude,longitude), new LatLng(latitude, longitude));
                        PlacePicker.IntentBuilder intentBuilder =
                                new PlacePicker.IntentBuilder();
                        intentBuilder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);

                        Intent intent = intentBuilder.build(this);
                        startActivityForResult(intent, PLACE_PICKER_REQUEST);

                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }*/
                }

            }else{
                gps.showSettingsAlert();
            }
        }else{
            gps.showSettingsAlert();
        }
    }

    public void showAttc(boolean att){
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
        }else{
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
            }else  if(v.equals(btnAttachmentMeme)){
                showAttachmentDialog(REQ_MEME);
            }else if (v.equals(btn_add_emoticon)) {
                if(emojicons.getVisibility()== View.GONE){
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                    Animation animFade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_slide_in_bottom);
                    emojicons.setVisibility(View.VISIBLE);
                    emojicons.startAnimation(animFade);

                    textMessage.setFocusable(false);
                }else {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    textMessage.setFocusableInTouchMode(true);
                    textMessage.requestFocus();
                    InputMethodManager imm =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(textMessage, InputMethodManager.SHOW_IMPLICIT);
                    emojicons.setVisibility(View.GONE);
                }
            } else if (v.equals(btnSend)) {
                String msg = textMessage.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(msg.toString(),false);
                    textMessage.setText("");
                }
            }else if (v.equals(headerButtonNote)) {
                if(hiddenNote){
                    listViewNote.setAdapter(null);
                    listViewNote.setVisibility(View.GONE);
                    hiddenNote=false;
                    hiddenVoting=false;
                    return;
                }else{
                    listViewNote.setAdapter(null);
                    listViewNote.setAdapter(adapterNotes);
                    listViewNote.setVisibility(View.VISIBLE);
                    hiddenNote=true;
                    hiddenVoting=false;
                    return;
                }
            }else if (v.equals(footerView)) {
                listViewNote.setAdapter(null);
                listViewNote.setVisibility(View.GONE);
                hiddenNote=false;
                hiddenVoting=false;
                return;
            }else if (v.equals(headerButtonVoting)) {
                if(hiddenVoting){
                    listViewNote.setAdapter(null);
                    listViewNote.setVisibility(View.GONE);
                    hiddenVoting=false;
                    hiddenNote=false;
                    return;
                }else{
                    listViewNote.setAdapter(null);
                    listViewNote.setAdapter(adapterVoting);
                    listViewNote.setVisibility(View.VISIBLE);
                    hiddenVoting=true;
                    hiddenNote=false;
                    return;
                }
            }
        }

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

    /*private int getStatusMessage(int status){
        int notifStatus = R.drawable.ic_item_message_sent;
        if(status == 0){
            notifStatus = R.drawable.ic_item_message_sent;
        }else if(status == 1){
            notifStatus = R.drawable.ic_item_message_delivered;
        }else if(status == 2){
            notifStatus = R.drawable.ic_item_message_r;
        }else if(status == 3){
            notifStatus = R.drawable.ic_item_message_read;
        }

        return notifStatus;
    }*/

    class ConversationLoader extends AsyncTask<Void, Message, Void> {
        private Cursor cursor;

        @Override
        protected Void doInBackground(Void... params) {
            lastDate = "";
            long lastTotalMessage = totalMessages;
            cursor = messengerHelper.query(
                    SQL_SELECT_TOTAL_MESSAGES,
                    new String[] { groupId,
                            groupId });
            int indexTotal = cursor.getColumnIndex("total");
            while (cursor.moveToNext()) {
                totalMessages = cursor.getLong(indexTotal);
            }
            cursor.close();

            if (lastTotalMessage < totalMessages){
                clearConversations();
                cursor = messengerHelper.query(SQL_SELECT_MESSAGES, new String[] {
                        groupId, groupId,
                        String.valueOf(loadLimit), String.valueOf(0)});

                ArrayList<Message> messages = new ArrayList<Message>();

                while (cursor.moveToNext()) {
                    Message vo = new Message(cursor);
                    messages.add(0, vo);
                }
                for (Iterator<Message> iterator = messages.iterator(); iterator
                        .hasNext();) {
                    Message vo = iterator.next();
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
            addConversation(values[0], false,false);
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
        int pos = (v2 == null ? 0 :  v2.getBottom());

        @Override
        protected Void doInBackground(Void... params) {
            cursor = messengerHelper.query(SQL_SELECT_MESSAGES, new String[] {
                    groupId, groupId,
                    String.valueOf(loadLimit), String.valueOf(loadOffset)});

            ArrayList<Message> messages = new ArrayList<Message>();

            while (cursor.moveToNext()) {
                Message vo = new Message(cursor);
                messages.add(vo);
            }
            for (Iterator<Message> iterator = messages.iterator(); iterator
                    .hasNext();) {
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
          /*  listConversation.clearFocus();
            listConversation.post(new Runnable() {
                @Override
                public void run() {
                    listConversation.setSelection((conversations.size()-count));
                }
            });*/
        }
    }

    private synchronized void addConversation(Message vo, boolean notify, boolean receive) {
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
        if (!lastDate.equals(theDate)) {
            lastDate = theDate;
            if(!new Validations().getInstance(getApplicationContext()).cekRoom(groupId)){
                String iklan = new Validations().getInstance(getApplicationContext()).getContentValidation(14);
                if (iklan.length()>0){
                    theDate+="\n"+iklan;
                }
            }
            conversations.add(theDate);
        }

        if (vo.isGroupChat()) {
            if (Message.TYPE_INFO.equals(vo.getType())) {
                conversations.add(vo.getMessage());
            } else if (name.equals(vo.getSource())) {
                conversations.add(vo);
                groupMessages = null;
            } else {
                boolean added = false;
                boolean pisah = false;
                String sinfo = "+" + vo.getSourceInfo();
                String lastSinfo = "";
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
                    lastSinfo = m.getSourceInfo();
                    if (m.getSourceInfo().equals(vo.getSourceInfo())) {
                        if ((!(Message.TYPE_IMAGE.equals(vo.getType()) || Message.TYPE_LOC.equals(vo.getType())
                                || Message.TYPE_VIDEO.equals(vo.getType()))) && (!(Message.TYPE_IMAGE.equals(m.getType())
                                || Message.TYPE_LOC.equals(m.getType()) || Message.TYPE_VIDEO.equals(m.getType())))) {
                            createnew = false;
                        }

                        long diffInMs = vo.getDeliveredDate().getTime() - m.getDeliveredDate().getTime();
                        long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);

                        if(diffInSec>60){
                            added = false;
                            createnew = true;
                        }else {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(vo.getDeliveredDate());
                            int minutesVO = calendar.get(Calendar.MINUTE);
                            calendar.setTime(m.getDeliveredDate());
                            int minutesM = calendar.get(Calendar.MINUTE);
                            if(minutesM != minutesVO){
                                added = false;
                                createnew = true;
                            }
                        }
                        pisah = true;
                    }

                }


                if ((groupMessages != null && !createnew)) {
                    if (vo.getSourceInfo().equalsIgnoreCase(lastSinfo)) {
                        groupMessages.add(vo);
                        added = true;
                    }
                }

                if (!added) {
                    groupMessages = new ArrayList<Object>();
                    if (pisah) {
                        groupMessages.add("");
                    }else{
                        groupMessages.add(sinfo);
                    }
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
        if (receive){
            adapter.refreshList();
        }
    }

    private synchronized void clearConversations() {
        conversations.clear();
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
        if(lastDate.equals(theDate)){
            conversations.remove(0);
        }


        if (vo.isGroupChat()) {
            if (Message.TYPE_INFO.equals(vo.getType())) {
                conversations.add(vo.getMessage());
            } else if (name.equals(vo.getSource())) {
                conversations.add(vo);
                groupMessages = null;
            } else {
                boolean added = false;
                boolean pisah = false;
                String sinfo = "+" + vo.getSourceInfo();
                String lastSinfo = "";
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
                    long diffInMs = vo.getDeliveredDate().getTime() - m.getDeliveredDate().getTime();
                    long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);

                    if(diffInSec>60){
                        added = false;
                        createnew = true;
                    }else {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(vo.getDeliveredDate());
                        int minutesVO = calendar.get(Calendar.MINUTE);
                        calendar.setTime(m.getDeliveredDate());
                        int minutesM = calendar.get(Calendar.MINUTE);
                        if(minutesM != minutesVO){
                            added = false;
                            createnew = true;
                        }
                    }
                    pisah = true;

                }

                if ((groupMessages != null && !createnew)) {
                    if (vo.getSourceInfo().equalsIgnoreCase(lastSinfo)) {
                        groupMessages.add(vo);
                        added = true;
                    }
                }

                if (!added) {
                    groupMessages = new ArrayList<Object>();
                    if (pisah) {
                        groupMessages.add("");
                    }else{
                        groupMessages.add(sinfo);
                    }
                    groupMessages.add(vo);
                    conversations.add(0,groupMessages);
                }

            }
        } else {
            conversations.add(0,vo);
        }


        lastDate = theDate;
        if(!new Validations().getInstance(getApplicationContext()).cekRoom(groupId)){
            String iklan = new Validations().getInstance(getApplicationContext()).getContentValidation(14);
            if (iklan.length()>0){
                theDate+="\n"+iklan;
            }
        }
        conversations.add(0,theDate);
        //((ConversationAdapter) listConversation.getAdapter()).notifyDataSetChanged();
    }

    class BroadcastHandler extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MessengerConnectionService.ACTION_MESSAGE_RECEIVED
                    .equals(intent.getAction())) {
                Message vo = intent
                        .getParcelableExtra(MessengerConnectionService.KEY_MESSAGE_OBJECT);
                if (groupId.equals(vo.getSource())) {
                    updateConversation(vo);
                    MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.song1);
                    mediaPlayer.start();
                   /*blm material design
                   if (Message.TYPE_INFO.equals(vo.getType())) {
                        groupId = messengerHelper.getGroup(destination
                                .getJabberId());
                        setBarTitle(destination.getName(),true);
                        Group g = messengerHelper.getGroup(vo.getSource());
                        if (Group.STATUS_INACTIVE.equals(g.getStatus())) {
                            btnSend.setEnabled(false);
                        } else {
                            btnSend.setEnabled(true);
                        }
                    }*/
                    abortBroadcast();
                }
            } else if (MessengerConnectionService.ACTION_MESSAGE_DELIVERED
                    .equals(intent.getAction())) {
                Message vo = intent
                        .getParcelableExtra(MessengerConnectionService.KEY_MESSAGE_OBJECT);
                if(!vo.getType().equalsIgnoreCase(Message.TYPE_READSTATUS)){
                    if (groupId.equals(vo.getDestination())) {
                        updateConversation(vo);

                    }
                }
            } else if (MessengerConnectionService.ACTION_MESSAGE_FAILED
                    .equals(intent.getAction())) {
                Message vo = intent
                        .getParcelableExtra(MessengerConnectionService.KEY_MESSAGE_OBJECT);
                if(!vo.getType().equalsIgnoreCase(Message.TYPE_READSTATUS)){
                    if (groupId.equals(vo.getDestination())) {
                        updateConversation(vo);
                    }
                }
            } else if (MessengerConnectionService.ACTION_MESSAGE_SENT
                    .equals(intent.getAction())) {
                Message vo = intent
                        .getParcelableExtra(MessengerConnectionService.KEY_MESSAGE_OBJECT);
                if(!vo.getType().equalsIgnoreCase(Message.TYPE_READSTATUS)) {
                    if (groupId.equals(vo.getDestination())) {
                        updateConversation(vo);
                    }
                }
            } else if (MessengerConnectionService.ACTION_DISCONNECTED
                    .equals(intent.getAction())) {
                //    setMessageFormEnabled(false);
            } else if (MessengerConnectionService.ACTION_CONNECTED
                    .equals(intent.getAction())) {
                // setMessageFormEnabled(true);
            }else if(UploadService.KEY_UPDATE_BAR .equals(intent.getAction())) {
                Message vo = intent
                        .getParcelableExtra(MessengerConnectionService.KEY_MESSAGE_OBJECT);
                if(!vo.getType().equalsIgnoreCase(Message.TYPE_READSTATUS)){
                    if(groupId.equals(vo.getSource())){
                        updateConversationProgressBar(vo);
                    }
                }

            }else if(UploadService.KEY_UPDATE_UPLOAD_BAR .equals(intent.getAction())) {
                Message vo = intent
                        .getParcelableExtra(MessengerConnectionService.KEY_MESSAGE_OBJECT);
                if(!vo.getType().equalsIgnoreCase(Message.TYPE_READSTATUS)) {
                    if (groupId.equals(vo.getDestination())) {
                        updateConversation(vo);
                    }
                }
            }

        }

    }

    private class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String paramUrl = params[0];
            String paramjson = params[1];
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(paramUrl);
            BasicNameValuePair invitesBasicNameValuePair = new BasicNameValuePair("name", paramjson);
            List nameValuePairList = new ArrayList();
            nameValuePairList.add(invitesBasicNameValuePair);
            try {
                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);
                httpPost.setEntity(urlEncodedFormEntity);
                try {
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    InputStream inputStream = httpResponse.getEntity().getContent();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    StringBuilder stringBuilder = new StringBuilder();
                    String bufferedStrChunk = null;
                    while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
                        stringBuilder.append(bufferedStrChunk);
                    }
                    return stringBuilder.toString();
                } catch (ClientProtocolException cpe) {
                    System.out.println("First Exception cause of HttpResponese :" + cpe);
                    cpe.printStackTrace();
                } catch (IOException ioe) {
                    System.out.println("Second Exception cause of HttpResponse :" + ioe);
                    ioe.printStackTrace();
                }
            } catch (UnsupportedEncodingException uee) {
                System.out.println("An Exception given because of UrlEncodedFormEntity argument :" + uee);
                uee.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.startsWith("error") || result==null){
                Toast.makeText(getApplicationContext(), "terjadi kesalahan jaringan ", Toast.LENGTH_LONG).show();
            }else{
                if(result.equalsIgnoreCase("1")){
                    client.disconnect();
                    com.byonchat.android.provider.Group g = messengerHelper.getGroup(groupId);
                    messengerHelper.deleteData(g);
                    messengerHelper
                            .deleteRows(
                                    Message.TABLE_NAME,
                                    " destination=? OR source =? ",
                                    new String[]{groupId,
                                            groupId}
                            );
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Please try again later", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


}
