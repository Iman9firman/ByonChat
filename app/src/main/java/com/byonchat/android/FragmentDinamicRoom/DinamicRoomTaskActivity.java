package com.byonchat.android.FragmentDinamicRoom;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.byonchat.android.CaptureSignature;
import com.byonchat.android.ConversationActivity;
import com.byonchat.android.DialogFormChildMain;
import com.byonchat.android.DialogFormChildMainKompetitor;
import com.byonchat.android.DialogFormChildMainLemindo;
import com.byonchat.android.DialogFormChildMainNcal;
import com.byonchat.android.DialogFormChildMainNew;
import com.byonchat.android.DialogNewDropdown;
import com.byonchat.android.DownloadFileByonchat;
import com.byonchat.android.DownloadSqliteDinamicActivity;
import com.byonchat.android.DownloadUtilsActivity;
import com.byonchat.android.R;
import com.byonchat.android.ReaderOcr;
import com.byonchat.android.ZoomImageViewActivity;
import com.byonchat.android.adapter.ExpandableListAdapter;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.communication.NotificationReceiver;
import com.byonchat.android.createMeme.FilteringImage;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.list.AttachmentAdapter;
import com.byonchat.android.list.IconItem;
import com.byonchat.android.list.utilLoadImage.ImageLoaderLarge;
import com.byonchat.android.location.ActivityDirection;
import com.byonchat.android.model.AddChildFotoExModel;
import com.byonchat.android.personalRoom.asynctask.ProfileSaveDescription;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.ChatParty;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.DataBaseDropDown;
import com.byonchat.android.provider.DataBaseHelper;
import com.byonchat.android.provider.DatabaseKodePos;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.tempSchedule.MyEventDatabase;
import com.byonchat.android.utils.AllAboutUploadTask;
import com.byonchat.android.utils.DialogUtil;
import com.byonchat.android.utils.GPSTracker;
import com.byonchat.android.utils.GenerateQR;
import com.byonchat.android.utils.ImageFilePath;
import com.byonchat.android.utils.LocationAssistant;
import com.byonchat.android.utils.MediaProcessingUtil;
import com.byonchat.android.utils.Validations;
import com.byonchat.android.utils.ValidationsKey;
import com.byonchat.android.widget.ContactsCompletionView;
import com.byonchat.android.widget.SpinnerCustomAdapter;
import com.byonchat.android.widget.TimeDialog;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.guna.ocrlibrary.OCRCapture;
import com.squareup.picasso.Picasso;
import com.tokenautocomplete.FilteredArrayAdapter;
import com.tokenautocomplete.TokenCompleteTextView;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.wang.avi.AVLoadingIndicatorView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.sufficientlysecure.htmltextview.HtmlResImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import io.github.memfis19.annca.Annca;
import io.github.memfis19.annca.internal.configuration.AnncaConfiguration;
import zharfan.com.cameralibrary.Camera;
import zharfan.com.cameralibrary.CameraActivity;

public class DinamicRoomTaskActivity extends AppCompatActivity implements LocationAssistant.Listener, TokenCompleteTextView.TokenListener, AllAboutUploadTask.OnTaskCompleted {

    public static String POSDETAIL = "/bc_voucher_client/webservice/proses/list_task_json.php";
    public static String PULLMULIPLEDETAIL = "/bc_voucher_client/webservice/proses/list_task_pull_multiple_json.php";
    public static String PULLMULIPLEDETAILUPDATE = "/bc_voucher_client/webservice/proses/update_list_task_pull_multiple_json.php";
/*
    public static String POSDETAIL = "/bc_voucher_client/webservice/proses/list_task.php";
    public static String PULLMULIPLEDETAIL = "/bc_voucher_client/webservice/proses/list_task_pull_multiple.php";
*/

// TODO: 21/10/18 disini masih error untuk form child

    public static String PULLDETAIL = "/bc_voucher_client/webservice/proses/list_task_pull.php";

    public static String GETTABDETAIL = "/bc_voucher_client/webservice/category_tab/list_task.php";
    public static String GETTABDETAILPULL = "/bc_voucher_client/webservice/category_tab/list_task_pull.php";
    public static String GETTABDETAILPULLMULTIPLE = "/bc_voucher_client/webservice/category_tab/list_task_pull_multiple.php";
    public static String POST_FOTO = "/bc_voucher_client/webservice/proses/file_processing.php";
    BotListDB db;
    LinearLayout linearLayout;
    ScrollView mainScrooll;
    private static final int REQ_LOCATION_SETTING = 1200;
    private static final int REQ_CAMERA = 1201;
    private static final int REQ_GALLERY = 1202;
    private static final int REQ_VIDEO = 1203;
    private final int PLACE_PICKER_REQUEST = 1209;
    private static final int REQ_GALLERY_VIDEO = 1204;
    private static final int SIGNATURE_ACTIVITY = 1205;
    private static final int PICK_FILE_REQUEST = 1206;
    private static final int PICK_ESTIMATION = 1208;
    private static final int OCR_REQUEST = 1211;
    private static final int QRCODE_REQUEST = 1212;
    private static final int CAMERA_SCAN_TEXT = 1213;

    private static final String MENU_GALLERY_TITLE = "Gallery";
    Integer totalUpload = 0;
    ArrayList<String> prosesUpload = new ArrayList<>();
    ProgressDialog progressDialog;
    private ArrayList<AttachmentAdapter.AttachmentMenuItem> curAttItems;
    String cameraFileOutput;
    public static final SimpleDateFormat hourFormat = new SimpleDateFormat(
            "HH:mm:ss dd/MM/yyyy", Locale.getDefault());

    double latitude, longitude;
    Bitmap result = null;
    Button b;
    ImageView imageView[];
    LinearLayout linearEstimasi[];
    ExpandableListView expandableListView[];
    SearchableSpinner newSpinner[];
    RatingBar rat[];
    EditText et[];
    TextView tp[];
    RadioGroup rg[];
    ArrayList<String> banyakDropdown;
    ArrayList<ArrayList<String>> stringAPI;
    Map<Integer, String> idFormChildParent = new HashMap<Integer, String>();
    Map<Integer, String> hashMapOcrKTP = new HashMap<Integer, String>();
    Map<Integer, List<String>> hashMap = new HashMap<Integer, List<String>>();
    Map<Integer, List<String>> hashMapOcr = new HashMap<Integer, List<String>>();
    Map<Integer, List<String>> hashMapFormulas = new HashMap<Integer, List<String>>();
    Map<Integer, List<String>> hashMapDropForm = new HashMap<Integer, List<String>>();
    Map<Integer, List<String>> hashMapDropNew = new HashMap<Integer, List<String>>();
    HashMap<Integer, HashMap<String, String>> outerMap = new HashMap<Integer, HashMap<String, String>>();
    HashMap<Integer, HashMap<String, ArrayList<String>>> newDropdownViews = new HashMap<Integer, HashMap<String, ArrayList<String>>>();
    ArrayList<String> lolosReq = new ArrayList<>();
    LinearLayout btnSUMBIT;
    AddChildFotoExModel valueIdValue;
    Integer count;
    boolean showButton = true;
    GPSTracker gps;
    EditText langlot;
    String username;
    String title;
    String idTab;
    String idDetail;
    String color;
    String latLong;
    String fromList;
    String dbMaster = "";
    String linkGetAsignTo = "";
    boolean includeStatus = false;
    boolean denganCheck = true;
    String typeStatus = "0";
    String labelApprove = "Approve";
    String labelReject = "Reject";
    String labelDone = "Done";


    String officer = "";
    boolean deleteContent = false;
    int mYear, mMonth, mDay;
    Calendar dummyCalendar;
    int dummyIdDate;
    String dummyFlagDate;
    String dummyFormulaDate;
    private int attCurReq = 0;
    Boolean showDialog = true;
    private BroadcastHandler broadcastHandler = new BroadcastHandler();
    static final int DATE_DIALOG_ID = 1000;
    static final int TIME_DIALOG_ID = 2000;
    private static final ArrayList<AttachmentAdapter.AttachmentMenuItem> attCameraItems;
    private static final ArrayList<AttachmentAdapter.AttachmentMenuItem> attVideoItems;

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    private LocationAssistant assistant;
    //sementara
    String customersId = "";
    String isReject = "";
    String dumpCusId = "";
    String cusNewDrop = null;
    String idListTaskMasterForm = "";
    private Activity activity;
    Context context;

    //rombak zharfan
    String calendar;
    String startDate;
    String dropdownViewIdParent;
    Boolean call = false;
    boolean validateTime = false;
    String JcontentBawaanReject = "";

    private ArrayList<Kunjungan> kunjunganList = new ArrayList<>();
    private ArrayList<String> valuesKnjngnOne = new ArrayList<>();
    private ArrayList<String> valuesKnjngnTwo = new ArrayList<>();

    JSONArray ar = new JSONArray();

    static {
        attCameraItems = new ArrayList<AttachmentAdapter.AttachmentMenuItem>();
        attCameraItems.add(new AttachmentAdapter.AttachmentMenuItem(R.drawable.ic_att_photo,
                "Camera"));
        attCameraItems.add(new AttachmentAdapter.AttachmentMenuItem(R.drawable.ic_att_gallery,
                MENU_GALLERY_TITLE));

        attVideoItems = new ArrayList<AttachmentAdapter.AttachmentMenuItem>();
        attVideoItems.add(new AttachmentAdapter.AttachmentMenuItem(R.drawable.ic_att_video,
                "Camcorder"));
        attVideoItems.add(new AttachmentAdapter.AttachmentMenuItem(R.drawable.ic_att_gallery,
                MENU_GALLERY_TITLE));


    }

    private String[] arrMonth = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    private String getRandomString() {
        long currentTimeMillis = System.currentTimeMillis();
        SecureRandom random = new SecureRandom();

        char[] CHARSET_AZ_09 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        char[] result = new char[15];
        for (int i = 0; i < result.length; i++) {
            int randomCharIndex = random.nextInt(CHARSET_AZ_09.length);
            result[i] = CHARSET_AZ_09[randomCharIndex];
        }

        String resRandom = String.valueOf(currentTimeMillis) + new String(result);

        return resRandom;
    }
//

    @Override
    protected void onPause() {
        unregisterReceiver(broadcastHandler);
        assistant.stop();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (idTab.equalsIgnoreCase("2644")) {
            //tab mandiri testing
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            PhoneStateListener callStateListener = new PhoneStateListener() {
                public void onCallStateChanged(int state, String incomingNumber) {
                    if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                        call = true;
                    }

                    if (state == TelephonyManager.CALL_STATE_IDLE) {
                        if (call) {
                            call = false;
                            String strUriCalls = "content://call_log/calls";
                            Uri uriCalls = Uri.parse(strUriCalls);
                            Cursor c;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                c = getBaseContext().getContentResolver().query(uriCalls, null, null, null);
                            } else {
                                c = getBaseContext().getContentResolver().query(uriCalls, null, null, null, null);
                            }
                            if (c.getCount() == 0) {

                            } else {
                                while (c.moveToNext()) {
                                    int number = c.getColumnIndex(CallLog.Calls.NUMBER);
                                    int type = c.getColumnIndex(CallLog.Calls.TYPE);
                                    int date = c.getColumnIndex(CallLog.Calls.DATE);
                                    int duration = c.getColumnIndex(CallLog.Calls.DURATION);
                                    String phNumber = c.getString(number);
                                    String callType = c.getString(type);
                                    String callDate = c.getString(date);
                                    Date callDayTime = new Date(Long.valueOf(callDate));
                                    String callDuration = c.getString(duration);
                                    String dir = null;
                                    int dircode = Integer.parseInt(callType);

                                    et[1].setText(String.format("%02d  minutes %02d second", Integer.valueOf(callDuration) / 60, Integer.valueOf(callDuration) % 60));


                                    getBaseContext().getContentResolver().delete(uriCalls, "NUMBER = ?", new String[]{phNumber});
                                    break;
                                }
                            }
                            c.close();
                        }
                    }
                }
            };
            telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }


        assistant.start();
        IntentFilter filter = new IntentFilter("android.location.PROVIDERS_CHANGED");
        filter.addAction("bLFormulas");
        filter.addAction("refreshForm");
        filter.setPriority(1);


        registerReceiver(broadcastHandler, filter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dinamic_room_task);

        activity = this;
        assistant = new LocationAssistant(this, this, LocationAssistant.Accuracy.HIGH, 5000, false);
        assistant.setVerbose(true);

        context = this;

        mainScrooll = (ScrollView) findViewById(R.id.mainScrooll);
        linearLayout = (LinearLayout) findViewById(R.id.linear);

        if (db == null) {
            db = BotListDB.getInstance(context);
        }

        btnSUMBIT = (LinearLayout) getLayoutInflater().inflate(R.layout.button_submit_form, null);

        username = getIntent().getStringExtra("uu");
        idTab = getIntent().getStringExtra("ii");
        title = getIntent().getStringExtra("tt");
        idDetail = getIntent().getStringExtra("idTask");
        color = getIntent().getStringExtra("col");
        latLong = getIntent().getStringExtra("ll");
        fromList = getIntent().getStringExtra("from");

        //zharfan
        calendar = getIntent().getStringExtra("clndr");
        startDate = getIntent().getStringExtra("strtdt");

        if (getIntent().getStringExtra("customersId") != null) {
            customersId = getIntent().getStringExtra("customersId");
        }

        if (getIntent().getStringExtra("isReject") != null) {
            isReject = getIntent().getStringExtra("isReject");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_35dp);
        getSupportActionBar().setTitle(title);

        Cursor cursor = db.getSingleRoomDetailForm(username, idTab);

        if (cursor.getCount() > 0) {
            /*getSupportActionBar().setBackgroundDrawable(new Validations().getInstance(context).headerCostume(getWindow(), "#" + color));*/

            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#" + color)));
            FilteringImage.SystemBarBackground(getWindow(), Color.parseColor("#" + color));
            final String conBefore = cursor.getString(cursor.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));
            String content = conBefore;
            JSONObject JcontentBawaan = null;
            try {
                JcontentBawaan = new JSONObject("{}");
            } catch (JSONException e) {
                e.printStackTrace();
            }


            JSONObject jO = null;
            try {
                jO = new JSONObject(conBefore);

                if (!jO.has("ver")) {
                    refreshMethod();
                    return;
                } else {
                    if (!jO.getString("ver").equalsIgnoreCase(context.getResources().getString(R.string.app_version))) {
                        refreshForm();
                        return;
                    }
                }


                content = jO.getString("aa");
                dbMaster = jO.getString("bb");

                if (conBefore.contains("cc")) {
                    linkGetAsignTo = jO.getString("cc");
                }

                if (conBefore.contains("dd")) {
                    String ssA = jO.getString("dd");
                    JSONObject jObject = null;
                    try {
                        jObject = new JSONObject(ssA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (jObject != null) {
                        try {

                            typeStatus = jObject.getString("status_task");

                            if (!typeStatus.equalsIgnoreCase("0")) {
                                includeStatus = true;
                                labelApprove = jObject.getString("approve");
                                labelReject = jObject.getString("reject");
                                labelDone = jObject.getString("done");
                            }


                        } catch (JSONException e) {
                            includeStatus = false;
                            e.printStackTrace();
                        }
                    } else {
                        if (ssA.equalsIgnoreCase("1")) {
                            includeStatus = true;
                        } else {
                            includeStatus = false;
                        }
                    }


                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            Cursor cursorParent = db.getSingleRoomDetailFormWithFlag(idDetail, username, idTab, "parent");

            if (fromList.equalsIgnoreCase("hide") || fromList.equalsIgnoreCase("hideMultiple") || fromList.equalsIgnoreCase("showMultiple")) {
                String latLongsPull = "0.0|0.0";
                if (latLong.equalsIgnoreCase("1")) {
                    gps = new GPSTracker(DinamicRoomTaskActivity.this);
                    if (!gps.canGetLocation()) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQ_LOCATION_SETTING);
                        finish();
                    } else {
                        if (gps.canGetLocation()) {
                            latitude = gps.getLatitude();
                            longitude = gps.getLongitude();
                            if (latitude == 0.0 && longitude == 0.0) {
                                latLongsPull = new NotificationReceiver().simInfo();
                               /* DinamicRoomTaskActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        //

                                       // finish();
                                       // Toast.makeText(context, "Harap coba kembali dalam waktu 1 menit, karena data gps anda sedang diaktifkan", Toast.LENGTH_LONG).show();
                                    }
                                });*/
                            } else {
                                latLongsPull = String.valueOf(latitude) + "|" + String.valueOf(longitude);
                            }
                        }
                    }

                }

                String firstLat = "";
                if (!latLongsPull.equalsIgnoreCase("")) {
                    firstLat = jsonCreateType(latLongsPull, "", "");
                }

                //di disable dlu
                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, null, "0", firstLat, "parent");
                db.updateDetailRoomWithFlagContentParent(orderModel);

                LinearLayout masterlinearValue = (LinearLayout) findViewById(R.id.linearValue);
                masterlinearValue.setVisibility(View.VISIBLE);


                Cursor cursorValue = db.getSingleRoomDetailFormWithFlag(idDetail, username, idTab, "value");
                if (cursorValue.getCount() > 0) {
                    final String contentValue = cursorValue.getString(cursorValue.getColumnIndexOrThrow(BotListDB.ROOM_CONTENT));

                    JcontentBawaanReject = cursorValue.getString(cursorValue.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_CONTENT));

                    if (JcontentBawaanReject.length() == 0) {
                        db.deleteRoomsDetailbyId(idDetail, idTab, username);
                        refreshMethod();
                        return;
                    }
                    try {
                        JcontentBawaan = new JSONObject(JcontentBawaanReject);

                    } catch (JSONException e) {
                        try {
                            JcontentBawaan = new JSONObject("{}");
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        e.printStackTrace();
                    }

                    JSONArray jsonArray = null;

                    try {
                        jsonArray = new JSONArray(contentValue);
                        for (int ii = (jsonArray.length() - 1); ii >= 0; ii--) {

                            JSONArray magic = new JSONArray(jsonArray.getJSONArray(ii).toString());

                            JSONObject oContent = new JSONObject(magic.get(0).toString());
                            JSONObject oContent2 = new JSONObject(magic.get(1).toString());
                            JSONArray joContent = oContent2.getJSONArray("value_detail");
                            String bc_user = oContent.getString("bc_user");


                            JSONObject oUser = new JSONObject(bc_user);

                            LinearLayout MMlinearValue = (LinearLayout) getLayoutInflater().inflate(R.layout.list_value_form_detail, null);
                            LinearLayout linearDetailValue = (LinearLayout) MMlinearValue.findViewById(R.id.linearDetailValue);

                            if (ii % 2 == 0) {
                                linearDetailValue.setBackgroundResource(R.drawable.backgroud_gray_young);
                            } else {
                                linearDetailValue.setBackgroundResource(R.drawable.backgroud_gray);
                            }


                            masterlinearValue.addView(MMlinearValue);
                            LinearLayout.LayoutParams parampps = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                            parampps.setMargins(0, 0, 0, 10);
                            MMlinearValue.setLayoutParams(parampps);

                            if (magic.get(0).toString().contains("tab_name")) {
                                String tb = oContent.getString("tab_name");
                                //TextView tabFormName = (TextView) MMlinearValue.findViewById(R.id.tab_form_name);
                                LinearLayout linearLayoutTabName = (LinearLayout) MMlinearValue.findViewById(R.id.linearLayoutTabName);
                                TextView tabName = (TextView) MMlinearValue.findViewById(R.id.tabName);
                                tabName.setText(tb);
                                linearLayoutTabName.setVisibility(View.VISIBLE);
                                // tabFormName.setVisibility(View.VISIBLE);
                            }


                            LinearLayout submitLinear = (LinearLayout) MMlinearValue.findViewById(R.id.linearSubmit);

                            TextView textSubmit = new TextView(DinamicRoomTaskActivity.this);
                            textSubmit.setText(Html.fromHtml("Submit From"));
                            textSubmit.setTextSize(17);
                            textSubmit.setLayoutParams(new TableRow.LayoutParams(0));

                            TextView etVIn1 = (TextView) new TextView(context);
                            etVIn1.setTextIsSelectable(true);
                            etVIn1.setText(Html.fromHtml(oUser.getString("no_telp")));
                            TextView etVIn2 = (TextView) new TextView(context);
                            etVIn2.setTextIsSelectable(true);
                            etVIn2.setText(Html.fromHtml(oUser.getString("name")));
                            TextView etVIn3 = (TextView) new TextView(context);
                            etVIn3.setTextIsSelectable(true);
                            etVIn3.setText(Html.fromHtml(oUser.getString("divisi")));
                            TextView etVIn4 = (TextView) new TextView(context);
                            etVIn4.setTextIsSelectable(true);
                            etVIn4.setText(Html.fromHtml(oUser.getString("jabatan")));
                            TextView etVIn5 = (TextView) new TextView(context);
                            etVIn5.setTextIsSelectable(true);
                            etVIn5.setText(Html.fromHtml(oUser.getString("lokasi")));
                            TextView etVIn6 = (TextView) MMlinearValue.findViewById(R.id.dateTxt);
                            etVIn6.setTextIsSelectable(true);
                            etVIn6.setText(oContent.getString("add_date"));

                            LinearLayout lineIn = (LinearLayout) getLayoutInflater().inflate(R.layout.line_horizontal, null);
                            LinearLayout.LayoutParams params11In = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params11In.setMargins(10, 10, 30, 0);
                            LinearLayout.LayoutParams params22in = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params22in.setMargins(50, 0, 30, 0);
                            LinearLayout.LayoutParams params22Last = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params22Last.setMargins(50, 10, 30, 30);

                            submitLinear.addView(textSubmit, params11In);
                            submitLinear.addView(lineIn, params11In);
                            submitLinear.addView(etVIn1, params22in);
                            submitLinear.addView(etVIn2, params22in);
                            submitLinear.addView(etVIn3, params22in);
                            submitLinear.addView(etVIn4, params22in);
                            submitLinear.addView(etVIn5, params22Last);

                            if (oContent.has("status")) {
                                if (!oContent.getString("status").equalsIgnoreCase("null") || oContent.getString("status") == null) {
                                    TextView statusApprove = new TextView(DinamicRoomTaskActivity.this);
                                    statusApprove.setText(Html.fromHtml("Status"));
                                    statusApprove.setTextSize(17);
                                    statusApprove.setLayoutParams(new TableRow.LayoutParams(0));

                                    LinearLayout lineIn2 = (LinearLayout) getLayoutInflater().inflate(R.layout.line_horizontal, null);

                                    TextView eeStat = (TextView) new TextView(context);
                                    eeStat.setTextIsSelectable(true);
                                    String sSttus = oContent.getString("status");


                                    if (oContent.getString("status").equalsIgnoreCase("0")) {
                                        sSttus = "Reject";
                                    } else if (oContent.getString("status").equalsIgnoreCase("1")) {
                                        sSttus = "Approve";
                                    } else if (oContent.getString("status").equalsIgnoreCase("2")) {
                                        sSttus = "Done";
                                    }

                                    eeStat.setText(sSttus);


                                    submitLinear.addView(statusApprove, params11In);
                                    submitLinear.addView(lineIn2, params11In);
                                    submitLinear.addView(eeStat, params22Last);
                                }


                            }

                            LinearLayout linearValue = (LinearLayout) MMlinearValue.findViewById(R.id.linearValueDetail);

                            for (int i = 0; i < joContent.length(); i++) {
                                final String label = joContent.getJSONObject(i).getString("label").toString();
                                final String value = joContent.getJSONObject(i).getString("value").toString();
                                final String type = joContent.getJSONObject(i).getString("type").toString();
                                final String idValue = joContent.getJSONObject(i).getString("id").toString();

                                if (showParentView(joContent, idValue, label)) {

                                    if (!value.equalsIgnoreCase("-")) {

                                        if (type.equalsIgnoreCase("dropdown_views")) {
                                            TextView textV = new TextView(DinamicRoomTaskActivity.this);
                                            textV.setText(Html.fromHtml(label));
                                            textV.setTextSize(17);
                                            textV.setLayoutParams(new TableRow.LayoutParams(0));

                                            JSONObject jObject = new JSONObject(value);
                                            Log.w("valuePairs", value);

                                            String vl = jObject.getString("value");

                                            if (jObject.has("dropdown_view_id")) {
                                                dropdownViewIdParent = jObject.getString("dropdown_view_id");
                                            }

                                            TextView etV = (TextView) new TextView(context);
                                            etV.setTextIsSelectable(true);
                                            etV.setText(Html.fromHtml(vl));
                                            LinearLayout line = (LinearLayout) getLayoutInflater().inflate(R.layout.line_horizontal, null);
                                            LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                            params11.setMargins(10, 10, 30, 0);
                                            LinearLayout.LayoutParams params22 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                            params22.setMargins(50, 10, 30, 30);
                                            linearValue.addView(textV, params11);
                                            linearValue.addView(line, params11);
                                            linearValue.addView(etV, params22);


                                        } else if (type.equalsIgnoreCase("attach_api")) {

                                            TextView textV = new TextView(DinamicRoomTaskActivity.this);
                                            textV.setText(Html.fromHtml(label));
                                            textV.setTextSize(17);
                                            textV.setLayoutParams(new TableRow.LayoutParams(0));


                                            LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.api_text_layout_form, null);
                                            //final TextView valueFile = (TextView) linearLayout.findViewById(R.id.value);
                                            HtmlTextView htmlTextView = (HtmlTextView) linearLayout.findViewById(R.id.value);
                                            htmlTextView.setTextIsSelectable(true);

                                            AVLoadingIndicatorView progress = (AVLoadingIndicatorView) linearLayout.findViewById(R.id.loader_progress);

                                            if (!value.equalsIgnoreCase("")) {
                                                SaveMedia saveMedia = new SaveMedia();
                                                if (value.startsWith("Rp.")) {
                                                    saveMedia.execute(new MyTaskParams(htmlTextView, progress, value));
                                                } else {
                                                    saveMedia.execute(new MyTaskParams(htmlTextView, progress, value.replace(" ", "%")));
                                                }

                                            }


                                            LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                            params11.setMargins(10, 10, 30, 0);
                                            LinearLayout.LayoutParams params22 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                            params22.setMargins(50, 10, 30, 30);
                                            linearValue.addView(textV, params11);
                                            linearValue.addView(linearLayout, params22);


                                        } else if (type.equalsIgnoreCase("rear_camera") || type.equalsIgnoreCase("front_camera") || type.equalsIgnoreCase("signature")) {
                                            TextView textView = new TextView(DinamicRoomTaskActivity.this);
                                            textView.setText(Html.fromHtml(label));
                                            textView.setTextSize(17);
                                            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                            params2.setMargins(10, 10, 30, 0);
                                            textView.setLayoutParams(params2);
                                            linearValue.addView(textView);
                                            LinearLayout line = (LinearLayout) getLayoutInflater().inflate(R.layout.line_horizontal, null);
                                            linearValue.addView(line, params2);
                                            LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.image_loader_layout_form, null);

                                            final ImageView imageView = (ImageView) linearLayout.findViewById(R.id.value);
                                            final AVLoadingIndicatorView progress = (AVLoadingIndicatorView) linearLayout.findViewById(R.id.loader_progress);

                                            Picasso.with(context).load(value).into(imageView);

                                            imageView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                    Intent intent = new Intent(context, ZoomImageViewActivity.class);
                                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE, value);
                                                    startActivity(intent);
                                                }
                                            });


                                            int width = getWindowManager().getDefaultDisplay().getWidth();
                                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, width / 2);
                                            params.setMargins(5, 15, 0, 30);

                                            linearLayout.setLayoutParams(params);
                                            linearValue.addView(linearLayout);


                                        } else if (type.equalsIgnoreCase("form_child")) {


                                            JSONObject nn = new JSONObject(joContent.getJSONObject(i).toString());

                                            idFormChildParent.put(Integer.valueOf(nn.getString("id")), joContent.getJSONObject(i).toString());

                                            TextView textV = new TextView(DinamicRoomTaskActivity.this);
                                            textV.setText(Html.fromHtml(label));
                                            textV.setTextSize(17);
                                            textV.setLayoutParams(new TableRow.LayoutParams(0));

                                            String[] ff = idDetail.split("\\|");
                                            String iidd = "";
                                            if (ff.length == 2) {
                                                iidd = ff[1];
                                            }

                                            LinearLayout line = (LinearLayout) getLayoutInflater().inflate(R.layout.from_cild_layout_value, null);
                                            Button addCild = (Button) line.findViewById(R.id.btn_add_cild);
                                            ListView lv = (ListView) line.findViewById(R.id.listOrder);
                                            TextView tQty = null;
                                            TextView tPrice = null;
                                            TableRow tableRow2 = null;

                                            if (iidd.equalsIgnoreCase("1643") || iidd.equalsIgnoreCase("1628") || iidd.equalsIgnoreCase("1632") || iidd.equalsIgnoreCase("2021")
                                                    || (Integer.valueOf(iidd) >= 2092)) {
                                                line = (LinearLayout) getLayoutInflater().inflate(R.layout.form_cild_two_layout_value, null);
                                                if (Integer.valueOf(iidd) >= 2092) {
                                                    TextView nama = (TextView) line.findViewById(R.id.nama);
                                                    nama.setVisibility(View.GONE);
                                                }
                                                lv = (ListView) line.findViewById(R.id.listOrder);
                                                addCild = (Button) line.findViewById(R.id.btn_add_cild);

                                                tQty = (TextView) line.findViewById(R.id.total_detail_order);
                                                tPrice = (TextView) line.findViewById(R.id.total_price_order);
                                                tableRow2 = (TableRow) line.findViewById(R.id.tableRow2);

                                            } else {
                                                tQty = (TextView) line.findViewById(R.id.total_detail_order);
                                                tPrice = (TextView) line.findViewById(R.id.total_price_order);
                                                tableRow2 = (TableRow) line.findViewById(R.id.tableRow2);
                                            }

                                            lv.setOnTouchListener(new View.OnTouchListener() {
                                                // Setting on Touch Listener for handling the touch inside ScrollView
                                                @Override
                                                public boolean onTouch(View v, MotionEvent event) {
                                                    v.getParent().requestDisallowInterceptTouchEvent(true);
                                                    return false;
                                                }
                                            });

                                            addCild.setVisibility(View.GONE);

                                            final ArrayList<ModelFormChild> rowItems = new ArrayList<ModelFormChild>();
                                            final ArrayList<String> labelDialog = new ArrayList<String>();
                                            JSONArray jsonarray = new JSONArray(value);

                                            if (Integer.valueOf(iidd) >= 2092) {
                                                Log.w("POHO", "kumaha1");
                                                for (int aaa = 0; aaa < jsonarray.length(); aaa++) {
                                                    JSONObject jsonobject = jsonarray.getJSONObject(aaa);
                                                    String urutan = jsonobject.getString("urutan");
                                                    String titleUntuk = "";
                                                    String decsUntuk = "";
                                                    String priceUntuk = "";
                                                    String data = jsonobject.getString("data");

                                                    JSONArray jsonarrayChild = new JSONArray(data);
                                                    boolean image = false;
                                                    for (int bbb = 0; bbb < jsonarrayChild.length(); bbb++) {
                                                        final String key = jsonarrayChild.getJSONObject(bbb).getString("key").toString();
                                                        final String val = jsonarrayChild.getJSONObject(bbb).getString("value").toString();
                                                        final String typ = jsonarrayChild.getJSONObject(bbb).getString("type").toString();
                                                        String lab = "";
                                                        if (jsonarrayChild.getJSONObject(bbb).has("label")) {
                                                            lab = jsonarrayChild.getJSONObject(bbb).getString("label").toString();
                                                        }


                                                        String valUs = "";
                                                        if (Message.isJSONValid(val)) {
                                                            JSONObject jObject = null;
                                                            try {
                                                                jObject = new JSONObject(val);

                                                                Iterator<String> keys = jObject.keys();
                                                                while (keys.hasNext()) {
                                                                    String keyValue = (String) keys.next();
                                                                    String valueString = jObject.getString(keyValue);
                                                                    valUs += keyValue + " : " + valueString + "\n";
                                                                }

                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }

                                                        }

                                                        if (valUs.length() == 0) {

                                                            titleUntuk += lab + " : " + val + "\n";
                                                        } else {
                                                            titleUntuk += lab + "\n" + valUs;
                                                        }


                                                        if (typ.equalsIgnoreCase("front_camera") || typ.equalsIgnoreCase("rear_camera")) {
                                                            image = true;
                                                        }
                                                    }
                                                    if (image) {
                                                        JSONArray jsonarrayChild2 = new JSONArray(data);
                                                        for (int bbb = 0; bbb < jsonarrayChild.length(); bbb++) {
                                                            String key = jsonarrayChild2.getJSONObject(bbb).getString("key").toString();
                                                            String val = jsonarrayChild2.getJSONObject(bbb).getString("value").toString();
                                                            String typ = jsonarrayChild2.getJSONObject(bbb).getString("type").toString();

                                                            if (typ.equalsIgnoreCase("front_camera") || typ.equalsIgnoreCase("rear_camera")) {
                                                                titleUntuk = val;
                                                                priceUntuk = "image";
                                                                labelDialog.add(0, key);
                                                            }

                                                            if (typ.equalsIgnoreCase("dropdown")) {
                                                                titleUntuk = val;
                                                                priceUntuk = "standart";
                                                                labelDialog.add(0, key);
                                                            }

                                                            if (typ.equalsIgnoreCase("textarea")) {
                                                                decsUntuk = val;
                                                                if (labelDialog.size() > 1) {
                                                                    labelDialog.add(1, key);
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        if (iidd.equalsIgnoreCase("2207") || iidd.equalsIgnoreCase("2206") || iidd.equalsIgnoreCase("2209") || iidd.equalsIgnoreCase("2397")) {

                                                            JSONArray jsonarrayChild2 = new JSONArray(data);
                                                            JSONObject ks0 = jsonarrayChild2.getJSONObject(0);
                                                            JSONObject ks1 = jsonarrayChild2.getJSONObject(1);
                                                            JSONObject ks2 = jsonarrayChild2.getJSONObject(2);

                                                            JSONObject alas = ks1.getJSONObject("value");
                                                            titleUntuk = alas.getString("Part ID") + " " + alas.getString("Nama Part") + "(" + ks0.getString("value") + ")";
                                                            decsUntuk = ks2.getString("value");
                                                            priceUntuk = alas.getString("AVE");


                                                            line = (LinearLayout) getLayoutInflater().inflate(R.layout.from_cild_layout_value, null);
                                                            lv = (ListView) line.findViewById(R.id.listOrder);
                                                            addCild = (Button) line.findViewById(R.id.btn_add_cild);
                                                            tQty = (TextView) line.findViewById(R.id.total_detail_order);
                                                            tPrice = (TextView) line.findViewById(R.id.total_price_order);
                                                            addCild.setVisibility(View.GONE);
                                                        }

                                                    }

                                                    rowItems.add(new ModelFormChild(urutan, titleUntuk, decsUntuk, priceUntuk));
                                                }

                                            } else {
                                                Log.w("POHO", "kumaha2");
                                                for (int aaa = 0; aaa < jsonarray.length(); aaa++) {
                                                    JSONObject jsonobject = jsonarray.getJSONObject(aaa);
                                                    String urutan = jsonobject.getString("urutan");
                                                    String titleUntuk = "";
                                                    String fotoUntuk = "";
                                                    String decsUntuk = "";
                                                    String priceUntuk = "";
                                                    String data = jsonobject.getString("data");
                                                    JSONArray jsonarrayChild = new JSONArray(data);
                                                    for (int bbb = 0; bbb < jsonarrayChild.length(); bbb++) {
                                                        final String key = jsonarrayChild.getJSONObject(bbb).getString("key").toString();
                                                        final String val = jsonarrayChild.getJSONObject(bbb).getString("value").toString();
                                                        final String typ = jsonarrayChild.getJSONObject(bbb).getString("type").toString();

                                                        JSONArray jsA = null;
                                                        String contentS = "";

                                                        String cc = val;
                                                        if (typ.equalsIgnoreCase("input_kodepos") || typ.equalsIgnoreCase("dropdown_wilayah")) {
                                                            cc = jsoncreateC(val);
                                                        }

                                                        try {
                                                            if (cc.startsWith("{")) {
                                                                if (!cc.startsWith("[")) {
                                                                    cc = "[" + cc + "]";
                                                                }
                                                                jsA = new JSONArray(cc);
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }

                                                        if (jsA != null) {
                                                            if (typ.equalsIgnoreCase("distance_estimation") || typ.equalsIgnoreCase("dropdown_dinamis") || typ.equalsIgnoreCase("new_dropdown_dinamis") || typ.equalsIgnoreCase("ocr") || typ.equalsIgnoreCase("upload_document")) {
                                                                if (iidd.equalsIgnoreCase("1643")) {
                                                                    titleUntuk = jsonResultType(val, "Nama");
                                                                } else {
                                                                    titleUntuk = jsonResultType(val, "Name Detail");
                                                                    if (titleUntuk.equalsIgnoreCase("")) {
                                                                        titleUntuk = jsonResultType(val, "SKU");
                                                                    }
                                                                    if (titleUntuk.equalsIgnoreCase("")) {
                                                                        titleUntuk = jsonResultType(val, "Nama");
                                                                    }
                                                                    if (titleUntuk.equalsIgnoreCase("")) {
                                                                        titleUntuk = jsonResultType(val, "Nama Siswa");
                                                                    }
                                                                }

                                                                fotoUntuk = jsonResultType(val, "Foto Siswa");

                                                            } else {
                                                                try {
                                                                    for (int ic = 0; ic < jsA.length(); ic++) {
                                                                        final String icC = jsA.getJSONObject(ic).getString("c").toString();
                                                                        contentS += icC + "|";
                                                                    }
                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }

                                                            }
                                                        } else {
                                                            if (iidd.equalsIgnoreCase("1643") || iidd.equalsIgnoreCase("1628") || iidd.equalsIgnoreCase("1632")) {
                                                                decsUntuk = val;
                                                                priceUntuk = "";
                                                            } else {
                                                                if (idListTaskMasterForm.equalsIgnoreCase("62483")) {
                                                                    if (key.equalsIgnoreCase("qty")) {
                                                                        decsUntuk = val;
                                                                    } else if (key.equalsIgnoreCase("total")) {
                                                                        priceUntuk = val;
                                                                    }
                                                                } else {

                                                                    if (typ.equalsIgnoreCase("number") || typ.equalsIgnoreCase("currency")) {
                                                                        decsUntuk = val;
                                                                        labelDialog.add(1, key);
                                                                    } else if (typ.equalsIgnoreCase("formula")) {
                                                                        priceUntuk = val;
                                                                    }

                                                                    if (typ.equalsIgnoreCase("front_camera") || typ.equalsIgnoreCase("rear_camera")) {
                                                                        titleUntuk = val;
                                                                        priceUntuk = "image";
                                                                        labelDialog.add(0, key);
                                                                    }

                                                                    if (typ.equalsIgnoreCase("dropdown")) {
                                                                        titleUntuk = val;
                                                                        decsUntuk = val;

                                                                        priceUntuk = "standart";
                                                                        labelDialog.add(0, key);
                                                                    }

                                                                    if (typ.equalsIgnoreCase("textarea")) {
                                                                        decsUntuk = val;
                                                                        if (labelDialog.size() > 1) {
                                                                            labelDialog.add(1, key);
                                                                        }
                                                                    }
                                                                }
                                                            }

                                                        }
                                                    }

                                                    if (ff.length == 2) {
                                                        iidd = ff[1];
                                                    }

                                                    if (iidd.equalsIgnoreCase("1643") || iidd.equalsIgnoreCase("1628") || iidd.equalsIgnoreCase("1632")) {
                                                        rowItems.add(new ModelFormChild(urutan, titleUntuk, decsUntuk, ""));
                                                    } else if (iidd.equalsIgnoreCase("1113")) {
                                                        int nilai = 0;
                                                        try {
                                                            nilai = Integer.valueOf(priceUntuk != null ? priceUntuk.replace(".", "") : "0") / Integer.valueOf(decsUntuk != null ? decsUntuk.replace(".", "") : "0");
                                                        } catch (Exception e) {
                                                            nilai = 0;
                                                        }
                                                        rowItems.add(new ModelFormChild(urutan, titleUntuk, decsUntuk, String.valueOf(nilai)));
                                                    } else {

                                                        if (priceUntuk.equalsIgnoreCase("standart") && !fotoUntuk.equalsIgnoreCase("")) {
                                                            priceUntuk = fotoUntuk;
                                                        }
                                                        rowItems.add(new ModelFormChild(urutan, titleUntuk, decsUntuk, priceUntuk));
                                                    }
                                                }
                                            }


                                            if (ff.length == 2) {
                                                iidd = ff[1];
                                            }

                                            ArrayList<String> sini = new ArrayList();

                                            if (iidd.equalsIgnoreCase("1643") || iidd.equalsIgnoreCase("1628") || iidd.equalsIgnoreCase("1632")) {
                                            } else {
                                                Integer totalQ = 0;
                                                Integer totalP = 0;
                                                Double temp = 0.0;
                                                Boolean showTotal = false;
                                                for (ModelFormChild mfc : rowItems) {
                                                    try {
                                                        if (mfc.getTitle().contains(".jpg")) {
                                                            sini.add("image");
                                                        } else if (mfc.getPrice().equalsIgnoreCase("image")) {
                                                            sini.add("image");
                                                        } else if (mfc.getPrice().equalsIgnoreCase("standart")) {
                                                            sini.add("standart");
                                                        } else {
                                                            if (mfc.getTitle().contains("Rincian")) {
                                                                showTotal = true;
                                                                tableRow2.setVisibility(View.VISIBLE);
                                                                String sambut[] = mfc.getTitle().split("Nominal :");
                                                                totalP = totalP + Integer.valueOf(sambut[1].trim().replace(",", ""));
                                                            } else {
                                                                if (iidd.equalsIgnoreCase("2207") || iidd.equalsIgnoreCase("2206") || iidd.equalsIgnoreCase("2209") || iidd.equalsIgnoreCase("2397")) {
                                                                    totalQ += Integer.valueOf(mfc.getDetail());
                                                                    temp += Float.valueOf(mfc.getPrice()) * Integer.valueOf(mfc.getDetail());

                                                                    tQty.setText(totalQ + "");
                                                                    String totalHarga = new Validations().getInstance(context).numberToCurency(temp + "");
                                                                    tPrice.setText(totalHarga);

                                                                } else {
                                                                    totalQ += Integer.valueOf(mfc.getDetail());
                                                                    totalP += Integer.valueOf(mfc.getPrice().replace(".", "")) * Integer.valueOf(mfc.getDetail());
                                                                }

                                                            }


                                                        }

                                                    } catch (Exception e) {
                                                    }
                                                }

                                                if (totalQ > 0 && totalP > 0) {
                                                    String totalHarga = new Validations().getInstance(context).numberToCurency(totalP + "");
                                                    tQty.setText(totalQ + "");
                                                    tPrice.setText(totalHarga);
                                                }
                                                if (showTotal) {
                                                    String totalHarga = new Validations().getInstance(context).numberToCurency(totalP + "");
                                                    tQty.setText("");
                                                    tPrice.setText(totalHarga);
                                                }


                                            }

                                            if (sini.size() > 0) {
                                                if (sini.get(0).toString().equalsIgnoreCase("image")) {
                                                    line = (LinearLayout) getLayoutInflater().inflate(R.layout.form_cild_two_layout_value, null);
                                                    lv = (ListView) line.findViewById(R.id.listOrder);
                                                    TextView nama = (TextView) line.findViewById(R.id.nama);
                                                    addCild = (Button) line.findViewById(R.id.btn_add_cild);
                                                    addCild.setVisibility(View.GONE);
                                                    nama.setVisibility(View.GONE);

                                                    lv.setOnTouchListener(new View.OnTouchListener() {
                                                        // Setting on Touch Listener for handling the touch inside ScrollView
                                                        @Override
                                                        public boolean onTouch(View v, MotionEvent event) {
                                                            v.getParent().requestDisallowInterceptTouchEvent(true);
                                                            return false;
                                                        }
                                                    });

                                                    final FormChildAdapter adapter = new FormChildAdapter(context, rowItems, "value", true);
                                                    lv.setAdapter(adapter);
                                                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                        @Override
                                                        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                                            ModelFormChild modelFormChild = rowItems.get(position);
                                                            if (labelDialog.size() == 1) {
                                                                DialogUtil.generateAlertDialogLeftImage(DinamicRoomTaskActivity.this, modelFormChild.getTitle(), modelFormChild.getDetail(), "", labelDialog.get(0) != null ? labelDialog.get(0) : "", username).show();
                                                            } else if (labelDialog.size() > 1) {
                                                                DialogUtil.generateAlertDialogLeftImage(DinamicRoomTaskActivity.this, modelFormChild.getTitle(), modelFormChild.getDetail(), labelDialog.get(1) != null ? labelDialog.get(1) : "", labelDialog.get(0) != null ? labelDialog.get(0) : "", username).show();
                                                            } else {
                                                                DialogUtil.generateAlertDialogLeftImage(DinamicRoomTaskActivity.this, modelFormChild.getTitle(), modelFormChild.getDetail(), "", "", username).show();
                                                            }
                                                        }
                                                    });

                                                } else {
                                                    line = (LinearLayout) getLayoutInflater().inflate(R.layout.form_cild_two_layout_value, null);
                                                    lv = (ListView) line.findViewById(R.id.listOrder);

                                                    lv.setOnTouchListener(new View.OnTouchListener() {
                                                        // Setting on Touch Listener for handling the touch inside ScrollView
                                                        @Override
                                                        public boolean onTouch(View v, MotionEvent event) {
                                                            v.getParent().requestDisallowInterceptTouchEvent(true);
                                                            return false;
                                                        }
                                                    });

                                                    addCild = (Button) line.findViewById(R.id.btn_add_cild);
                                                    addCild.setVisibility(View.GONE);
                                                    TextView nama = (TextView) line.findViewById(R.id.nama);
                                                    nama.setVisibility(View.GONE);

                                                    final FormChildAdapter adapter = new FormChildAdapter(context, rowItems, "value", false);
                                                    lv.setAdapter(adapter);
                                                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                        @Override
                                                        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                                            if (labelDialog.size() > 1) {
                                                                ModelFormChild modelFormChild = rowItems.get(position);
                                                                DialogUtil.generateAlertDialogLeftBOBO(DinamicRoomTaskActivity.this, modelFormChild.getTitle(), modelFormChild.getDetail(), labelDialog.get(0) != null ? labelDialog.get(0) : "", labelDialog.get(1) != null ? labelDialog.get(1) : "", modelFormChild.getPrice()).show();
                                                            } else {
                                                                ModelFormChild modelFormChild = rowItems.get(position);
                                                                DialogUtil.generateAlertDialogLeftNoImage(DinamicRoomTaskActivity.this, modelFormChild.getTitle(), modelFormChild.getDetail(), "", "Nama").show();
                                                            }
                                                        }
                                                    });
                                                }


                                            } else {
                                                if (Integer.valueOf(iidd) >= 2092 && Integer.valueOf(iidd) != 2207 && Integer.valueOf(iidd) != 2206 && Integer.valueOf(iidd) != 2209 && Integer.valueOf(iidd) != 2397) {
                                                    final FormChildAdapter adapter = new FormChildAdapter(context, rowItems, "multiple", false);
                                                    lv.setAdapter(adapter);
                                                } else {
                                                    final FormChildAdapter adapter = new FormChildAdapter(context, rowItems, "value", false);
                                                    lv.setAdapter(adapter);
                                                }


                                                if (iidd.equalsIgnoreCase("1643") || iidd.equalsIgnoreCase("1628") || iidd.equalsIgnoreCase("1632")) {
                                                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                        @Override
                                                        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                                            ModelFormChild modelFormChild = rowItems.get(position);
                                                            DialogUtil.generateAlertDialogLeft(DinamicRoomTaskActivity.this, modelFormChild.getTitle(), modelFormChild.getDetail()).show();
                                                        }
                                                    });
                                                }
                                            }


                                            LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                            params11.setMargins(20, 10, 30, 0);
                                            LinearLayout.LayoutParams params22 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                            params22.setMargins(20, 10, 30, 30);
                                            linearValue.addView(textV, params11);
                                            LinearLayout lineview = (LinearLayout) getLayoutInflater().inflate(R.layout.line_horizontal, null);
                                            linearValue.addView(lineview, params11);
                                            linearValue.addView(line, params22);


                                        } else if (type.equalsIgnoreCase("input_kodepos") || type.equalsIgnoreCase("dropdown_wilayah")) {
                                            TextView textV = new TextView(DinamicRoomTaskActivity.this);
                                            textV.setText(Html.fromHtml(label));
                                            textV.setTextSize(17);
                                            textV.setLayoutParams(new TableRow.LayoutParams(0));


                                            TextView etV = (TextView) new TextView(context);
                                            etV.setTextIsSelectable(true);

                                            String aa = "Kode Pos = " + jsonResultType(value, "a") + "\nProvinsi = " + jsonResultType(value, "b") + "\nKota/Kab. = " + jsonResultType(value, "c") + "\nKecamatan = " + jsonResultType(value, "d") + "\nKelurahan = " + jsonResultType(value, "e");
                                            etV.setText(aa);

                                            LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                            params11.setMargins(10, 10, 30, 0);
                                            LinearLayout.LayoutParams params22 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                            params22.setMargins(50, 10, 30, 30);
                                            linearValue.addView(textV, params11);
                                            LinearLayout line = (LinearLayout) getLayoutInflater().inflate(R.layout.line_horizontal, null);
                                            linearValue.addView(line, params11);
                                            linearValue.addView(etV, params22);

                                        } else if (type.equalsIgnoreCase("dropdown_dinamis") || type.equalsIgnoreCase("new_dropdown_dinamis") || type.equalsIgnoreCase("master_data")) {


                                            if (!value.equalsIgnoreCase("-")) {

                                                TextView textV = new TextView(DinamicRoomTaskActivity.this);
                                                textV.setText(Html.fromHtml(label));
                                                textV.setTextSize(17);
                                                textV.setLayoutParams(new TableRow.LayoutParams(0));

                                                JSONObject jsonObject = new JSONObject(value);
                                                Iterator<String> keys = jsonObject.keys();


                                                LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                                params11.setMargins(10, 10, 30, 0);
                                                LinearLayout.LayoutParams params22 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                                params22.setMargins(50, 10, 30, 30);
                                                linearValue.addView(textV, params11);
                                                LinearLayout line = (LinearLayout) getLayoutInflater().inflate(R.layout.line_horizontal, null);
                                                linearValue.addView(line, params11);

                                                List<String> keysList = new ArrayList<String>();
                                                while (keys.hasNext()) {
                                                    keysList.add(keys.next());
                                                }

                                                String longi = null, lanti = null;
                                                for (String aa : keysList) {
                                                    if (jsonObject.getString(aa).startsWith("https://bb.byonchat.com/") && jsonObject.getString(aa).endsWith(".png")) {
                                                        TextView etV = (TextView) new TextView(context);
                                                        etV.setTextIsSelectable(true);
                                                        etV.setText(String.valueOf(aa));
                                                        linearValue.addView(etV, params22);

                                                        LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.image_loader_layout_form, null);
                                                        final ImageView imageView = (ImageView) linearLayout.findViewById(R.id.value);
                                                        Picasso.with(context).load(jsonObject.getString(aa)).into(imageView);
                                                        final String abab = jsonObject.getString(aa);
                                                        linearValue.addView(linearLayout, params22);
                                                        imageView.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {

                                                                Intent intent = new Intent(context, ZoomImageViewActivity.class);
                                                                intent.putExtra(ZoomImageViewActivity.KEY_FILE, abab);
                                                                startActivity(intent);
                                                            }
                                                        });


                                                    } else {
                                                        TextView etV = (TextView) new TextView(context);
                                                        etV.setTextIsSelectable(true);
                                                        etV.setText(String.valueOf(aa) + " = " + jsonObject.getString(aa));
                                                        linearValue.addView(etV, params22);

                                                    }

                                                    if (String.valueOf(aa).equalsIgnoreCase("Longitude")) {
                                                        longi = jsonObject.getString(aa);
                                                    } else if (String.valueOf(aa).equalsIgnoreCase("Latitude")) {
                                                        lanti = jsonObject.getString(aa);
                                                    }
                                                }


                                                if (longi != null && lanti != null) {
                                                    final Double l1 = Double.parseDouble(lanti);
                                                    final Double l2 = Double.parseDouble(longi);

                                                    ImageView imageView = new ImageView(this);
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                        imageView.setImageDrawable(getDrawable(R.drawable.ic_att_location));
                                                    } else {
                                                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_att_location));
                                                    }


                                                    imageView.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + l1 + ","
                                                                    + l2 + "(" + "Location" + ")");

                                                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                                            mapIntent.setPackage("com.google.android.apps.maps");
                                                            startActivity(mapIntent);
                                                        }
                                                    });

                                                    TextView textMap = new TextView(DinamicRoomTaskActivity.this);
                                                    textMap.setText("Location");
                                                    textMap.setTextSize(18);
                                                    textMap.setLayoutParams(new TableRow.LayoutParams(0));
                                                    linearValue.addView(textMap, params11);
                                                    LinearLayout line2 = (LinearLayout) getLayoutInflater().inflate(R.layout.line_horizontal, null);
                                                    linearValue.addView(line2, params11);
                                                    int width = getWindowManager().getDefaultDisplay().getWidth();
                                                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, width / 5);
                                                    params.setMargins(5, 15, 0, 30);
                                                    imageView.setLayoutParams(params);
                                                    linearValue.addView(imageView);
                                                }
                                            }


                                        } else if (type.equalsIgnoreCase("checkbox")) {

                                            TextView textV = new TextView(DinamicRoomTaskActivity.this);
                                            textV.setText(Html.fromHtml(label));
                                            textV.setTextSize(17);
                                            textV.setLayoutParams(new TableRow.LayoutParams(0));


                                            TextView etV = new TextView(context);
                                            etV.setTextIsSelectable(true);

                                            String aa = "";
                                            JSONArray jsA = new JSONArray(value);
                                            for (int ic = 0; ic < jsA.length(); ic++) {
                                                aa += "• " + jsA.getString(ic);
                                                if (ic < jsA.length() - 1) {
                                                    aa += "\n";
                                                }
                                            }
                                            etV.setText(aa);

                                            LinearLayout line = (LinearLayout) getLayoutInflater().inflate(R.layout.line_horizontal, null);

                                            LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                            params11.setMargins(10, 10, 30, 0);
                                            LinearLayout.LayoutParams params22 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                            params22.setMargins(50, 10, 30, 30);
                                            linearValue.addView(textV, params11);
                                            linearValue.addView(line, params11);
                                            linearValue.addView(etV, params22);

                                        } else if (type.equalsIgnoreCase("upload_document")) {
                                            TextView textView = new TextView(DinamicRoomTaskActivity.this);
                                            textView.setText(Html.fromHtml(label));
                                            textView.setTextSize(17);
                                            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                            params2.setMargins(30, 10, 30, 0);
                                            textView.setLayoutParams(params2);
                                            linearValue.addView(textView);

                                            LinearLayout lilin = (LinearLayout) getLayoutInflater().inflate(R.layout.upload_doc_layout, null);
                                            lilin.setLayoutParams(params2);
                                            linearValue.addView(lilin);

                                            final Button btnOption = (Button) lilin.findViewById(R.id.btn_browse);
                                            final TextView valueFile = (TextView) lilin.findViewById(R.id.value);

                                            valueFile.setText(value.substring(value.toString().lastIndexOf('/'), value.toString().length()));
                                            btnOption.setText("View");
                                            final String cc = value;
                                            btnOption.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(context, DownloadFileByonchat.class);
                                                    intent.putExtra("path", cc);
                                                    intent.putExtra("nama_file", valueFile.getText().toString());
                                                    startActivity(intent);
                                                }
                                            });

                                        } else if (type.equalsIgnoreCase("distance_estimation")) {
                                            TextView textView = new TextView(DinamicRoomTaskActivity.this);
                                            textView.setText(Html.fromHtml(label));
                                            textView.setTextSize(17);
                                            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                            LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                            params2.setMargins(30, 10, 30, 0);
                                            params3.setMargins(30, 10, 30, 30);
                                            textView.setLayoutParams(params2);
                                            linearValue.addView(textView);

                                            LinearLayout linearEstimasi = (LinearLayout) getLayoutInflater().inflate(R.layout.estimation_layout, null);
                                            linearEstimasi.setLayoutParams(params2);
                                            linearValue.addView(linearEstimasi);


                                            TextView start = (TextView) linearEstimasi.findViewById(R.id.valuePickup);
                                            TextView end = (TextView) linearEstimasi.findViewById(R.id.valueEnd);
                                            TextView jarak = (TextView) linearEstimasi.findViewById(R.id.valueJarak);


                                            final String[] latlongS = jsonResultType(value, "s").split(
                                                    Message.LOCATION_DELIMITER);
                                            if (latlongS.length > 4) {
                                                String text = "<u><b>" + (String) latlongS[2] + "</b></u><br/>";
                                                start.setText(Html.fromHtml(text + latlongS[3]));
                                            }

                                            final String[] latlongE = jsonResultType(value, "e").split(
                                                    Message.LOCATION_DELIMITER);
                                            if (latlongE.length > 4) {
                                                String text = "<u><b>" + (String) latlongE[2] + "</b></u><br/>";
                                                end.setText(Html.fromHtml(text + latlongE[3]));
                                            }
                                            jarak.setText(jsonResultType(value, "d"));

                                            linearEstimasi.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(Intent.ACTION_VIEW,
                                                            Uri.parse("http://maps.google.com/maps?saddr=" +/*latlongS[0].toString()+","+latlongS[1].toString()*/latlongS[2] + "&daddr=" +/*latlongE[0].toString()+","+latlongE[1].toString()*/latlongE[2]));
                                                    startActivity(intent);
                                                }
                                            });

                                        } else if (type.equalsIgnoreCase("map")) {
                                            TextView textV = new TextView(DinamicRoomTaskActivity.this);
                                            textV.setText(Html.fromHtml(label));
                                            textV.setTextSize(17);
                                            textV.setLayoutParams(new TableRow.LayoutParams(0));

                                            EditText etV = (EditText) getLayoutInflater().inflate(R.layout.edit_input_layout, null);
                                            etV.setFocusable(false);
                                            etV.setFocusableInTouchMode(false);

                                            String valueMap = "";
                                            try {
                                                valueMap = URLDecoder.decode(value, "UTF-8");
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            }

                                            if (valueMap.contains("%20")) {

                                                try {
                                                    valueMap = URLDecoder.decode(valueMap, "UTF-8");
                                                } catch (UnsupportedEncodingException e) {
                                                    e.printStackTrace();
                                                }

                                                String[] latlong = valueMap.split(" ");

                                                if (latlong.length > 4) {
                                                    etV.setText(Html.fromHtml(valueMap.substring(latlong[0].length() + latlong[1].length() + 2, valueMap.length())));
                                                }

                                                final String finalValueMap = valueMap;
                                                etV.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        if (latlong.length > 3) {
                                                            Uri gmmIntentUri = null;
                                                            if (latlong[3].equalsIgnoreCase("")) {
                                                                gmmIntentUri = Uri.parse("geo:0,0?q=" + Double
                                                                        .parseDouble(latlong[0]) + ","
                                                                        + Double.parseDouble(latlong[1]) + "(" + "You" + ")");
                                                            } else {
                                                                String loc = latlong[2] + "+,+" + latlong[3];
                                                                gmmIntentUri = Uri.parse("geo:0,0?q=" + Double.parseDouble(latlong[0]) + ","
                                                                        + Double.parseDouble(latlong[1]) + "(" + loc + ")");
                                                            }

                                                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                                            mapIntent.setPackage("com.google.android.apps.maps");
                                                            startActivity(mapIntent);
                                                        }
                                                    }
                                                });

                                                LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                                params11.setMargins(20, 10, 30, 0);
                                                LinearLayout.LayoutParams params22 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                                params22.setMargins(20, 10, 30, 30);
                                                linearValue.addView(textV, params11);
                                                linearValue.addView(etV, params22);

                                            } else {

                                                String[] latlong = valueMap.split(
                                                        Message.LOCATION_DELIMITER);

                                                Log.w("Hau3", latlong.length + "");

                                                if (latlong.length > 4) {
                                                    etV.setText(Html.fromHtml(latlong[3]));
                                                }

                                                final String finalValueMap = valueMap;
                                                etV.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        String[] latlong = finalValueMap.split(
                                                                Message.LOCATION_DELIMITER);

                                                        if (latlong.length > 3) {
                                                            Uri gmmIntentUri = null;
                                                            if (latlong[3].equalsIgnoreCase("")) {
                                                                gmmIntentUri = Uri.parse("geo:0,0?q=" + Double
                                                                        .parseDouble(latlong[0]) + ","
                                                                        + Double.parseDouble(latlong[1]) + "(" + "You" + ")");
                                                            } else {
                                                                String loc = latlong[2] + "+,+" + latlong[3];
                                                                gmmIntentUri = Uri.parse("geo:0,0?q=" + Double.parseDouble(latlong[0]) + ","
                                                                        + Double.parseDouble(latlong[1]) + "(" + loc + ")");
                                                            }

                                                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                                            mapIntent.setPackage("com.google.android.apps.maps");
                                                            startActivity(mapIntent);
                                                        }
                                                    }
                                                });

                                                LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                                params11.setMargins(20, 10, 30, 0);
                                                LinearLayout.LayoutParams params22 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                                params22.setMargins(20, 10, 30, 30);
                                                linearValue.addView(textV, params11);
                                                linearValue.addView(etV, params22);

                                            }

                                        } else if (type.equalsIgnoreCase("number")) {

                                            TextView textV = new TextView(DinamicRoomTaskActivity.this);
                                            textV.setText(Html.fromHtml(label));
                                            textV.setTextSize(17);
                                            textV.setLayoutParams(new TableRow.LayoutParams(0));


                                            TextView etV = (TextView) new TextView(context);
                                            etV.setTextIsSelectable(true);
                                            etV.setText(Html.fromHtml(value));
                                            LinearLayout line = (LinearLayout) getLayoutInflater().inflate(R.layout.line_horizontal, null);
                                            LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                            params11.setMargins(10, 10, 30, 0);
                                            LinearLayout.LayoutParams params22 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                            params22.setMargins(50, 10, 30, 30);
                                            if (!idTab.equalsIgnoreCase("2644")) {
                                                linearValue.addView(textV, params11);
                                                linearValue.addView(line, params11);
                                                linearValue.addView(etV, params22);
                                            }
                                        } else {
                                            TextView textV = new TextView(DinamicRoomTaskActivity.this);
                                            textV.setText(Html.fromHtml(label));
                                            textV.setTextSize(17);
                                            textV.setLayoutParams(new TableRow.LayoutParams(0));


                                            TextView etV = (TextView) new TextView(context);
                                            etV.setTextIsSelectable(true);
                                            etV.setText(Html.fromHtml(value));
                                            LinearLayout line = (LinearLayout) getLayoutInflater().inflate(R.layout.line_horizontal, null);
                                            LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                            params11.setMargins(10, 10, 30, 0);
                                            LinearLayout.LayoutParams params22 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                            params22.setMargins(50, 10, 30, 30);
                                            linearValue.addView(textV, params11);
                                            linearValue.addView(line, params11);
                                            linearValue.addView(etV, params22);
                                        }
                                    }
                                }
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }


                } else {

                    if (NetworkInternetConnectionStatus.getInstance(context).isOnline(context)) {
                        if (username != null) {
                            if (fromList.equalsIgnoreCase("hide")) {
                                new Refresh(DinamicRoomTaskActivity.this).execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + GETTABDETAILPULL, username, idTab, idDetail);
                            } else if (fromList.equalsIgnoreCase("hideMultiple") || fromList.equalsIgnoreCase("showMultiple")) {
                                if (!idDetail.equalsIgnoreCase("")) {
                                    String[] ff = idDetail.split("\\|");
                                    if (ff.length == 2) {
                                        new Refresh(DinamicRoomTaskActivity.this).execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + GETTABDETAILPULLMULTIPLE, username, idTab, idDetail);
                                    }
                                }
                            } else {
                                new Refresh(DinamicRoomTaskActivity.this).execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + GETTABDETAIL, username, idTab, "");
                            }
                        } else {
                            finish();
                        }
                    } else {

                        if (idDetail.contains("|")) {
                            Toast.makeText(context, "No Internet Akses", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    }
                }

            }

            if (idDetail == null || idDetail.equalsIgnoreCase("")) {
                idDetail = getRandomString();
                deleteContent = true;
            }

            if (cursorParent.getCount() > 0) {
                if (cursorParent.getString(cursorParent.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_CONTENT)).equalsIgnoreCase("2") || cursorParent.getString(cursorParent.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_CONTENT)).equalsIgnoreCase("3")) {
                    showButton = false;
                }
            } else {
                long date = System.currentTimeMillis();
                String dateString = hourFormat.format(date);
                String latLongs = "0.0|0.0";


                if (latLong.equalsIgnoreCase("1")) {
                    gps = new GPSTracker(DinamicRoomTaskActivity.this);
                    if (!gps.canGetLocation()) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQ_LOCATION_SETTING);
                        finish();
                    } else {
                        if (gps.canGetLocation()) {
                            latitude = gps.getLatitude();
                            longitude = gps.getLongitude();
                            if (latitude == 0.0 && longitude == 0.0) {
                               /* DinamicRoomTaskActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        finish();
                                        Toast.makeText(context, "Harap coba kembali dalam waktu 1 menit, karena data gps anda sedang diaktifkan", Toast.LENGTH_LONG).show();
                                    }
                                });*/
                                latLongs = new NotificationReceiver().simInfo();
                            } else {
                                latLongs = String.valueOf(latitude) + "|" + String.valueOf(longitude);
                            }
                        }
                    }

                }
                String firstLat = "";
                if (!latLongs.equalsIgnoreCase("")) {
                    firstLat = jsonCreateType(latLongs, "", "");
                }

                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, dateString, "0", firstLat, "parent");
                db.insertRoomsDetail(orderModel);
            }

            try {


                if (!dbMaster.equalsIgnoreCase("")) {
                    String[] aa = dbMaster.split("/");
                    final String nama = aa[aa.length - 1].toString();
                    DataBaseDropDown mDB = new DataBaseDropDown(context, nama.substring(0, nama.indexOf(".")));
                    if (mDB.getWritableDatabase() == null) {
                        Intent intent = new Intent(context, DownloadSqliteDinamicActivity.class);
                        intent.putExtra("name_db", nama.substring(0, nama.indexOf(".")));
                        intent.putExtra("path_db", dbMaster);
                        startActivity(intent);
                    }
                }

                final JSONArray jsonArray = new JSONArray(content);
                if (jsonArray.length() == 0) {

                    //di disable dlu
                    RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, null, "", "", "parent");
                    db.updateDetailRoomWithFlagContentParent(orderModel);

                    linearLayout.setVisibility(View.GONE);
                    return;
                }
                et = new EditText[jsonArray.length()];
                tp = new TextView[jsonArray.length()];
                rat = new RatingBar[jsonArray.length()];
                rg = new RadioGroup[jsonArray.length()];
                banyakDropdown = new ArrayList<String>();
                stringAPI = new ArrayList<ArrayList<String>>();
                linearEstimasi = new LinearLayout[jsonArray.length()];
                imageView = new ImageView[jsonArray.length()];
                expandableListView = new ExpandableListView[jsonArray.length()];
                newSpinner = new SearchableSpinner[jsonArray.length()];

                if (JcontentBawaanReject.length() > 3) {
                    LinearLayout.LayoutParams paramsReject = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramsReject.setMargins(30, 10, 30, 20);

                    JSONObject jsonMessageReject = new JSONObject(new JSONObject(JcontentBawaanReject).getString("message"));

                    TextView rejectSaha = new TextView(DinamicRoomTaskActivity.this);
                    rejectSaha.setText(Html.fromHtml("<font size=\"3\" color=\"red\"><b>Reject<b></font>" + "<br>By: " + jsonMessageReject.getString("nama_tukang_reject") + "<br>Phone : " + jsonMessageReject.getString("hp_tukang_reject") + "<br>From : " + jsonMessageReject.getString("reject_from") + "<br>Note :  " + jsonMessageReject.getString("alasan") + "<br>"));
                    rejectSaha.setTextSize(15);
                    rejectSaha.setLayoutParams(paramsReject);
                    linearLayout.addView(rejectSaha);

                }


                for (int i = 0; i < jsonArray.length(); i++) {

                    final String idListTask = jsonArray.getJSONObject(i).getString("id_list_task").toString();
                    String label = jsonArray.getJSONObject(i).getString("label").toString();
                    final String name = jsonArray.getJSONObject(i).getString("name").toString();
                    String placeHolder = jsonArray.getJSONObject(i).getString("placeholder").toString();
                    String maxlength = jsonArray.getJSONObject(i).getString("maxlength").toString();
                    if (maxlength.equalsIgnoreCase("")) maxlength = "99";
                    String required = jsonArray.getJSONObject(i).getString("required").toString();
                    final String value = jsonArray.getJSONObject(i).getString("value").toString();
                    final String type = jsonArray.getJSONObject(i).getString("type").toString();
                    final String flag = jsonArray.getJSONObject(i).getString("flag").toString();

                    //zharfan
                    JSONArray dropdownViewId = null;
                    if (jsonArray.getJSONObject(i).has("dropdown_view_parents")) {
                        dropdownViewId = jsonArray.getJSONObject(i).getJSONArray("dropdown_view_parents");
                    }

                    if (type.equalsIgnoreCase("call_chat")) {
                        Log.w("kamar2", "madni");

                        TextView textView = new TextView(DinamicRoomTaskActivity.this);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(15);


                        if (count == null) {
                            count = 0;
                        } else {
                            count++;
                        }


                        final List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(String.valueOf(count));
                        valSetOne.add(required);
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));

                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(30, 10, 30, 0);
                        textView.setLayoutParams(params2);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(textView);

                        linearEstimasi[count] = (LinearLayout) getLayoutInflater().inflate(R.layout.call_chat_layout, null);
                        linearEstimasi[count].setLayoutParams(params2);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(linearEstimasi[count]);

                        ImageView btnCall = (ImageView) linearEstimasi[count].findViewById(R.id.btnCall);
                        ImageView btnChat = (ImageView) linearEstimasi[count].findViewById(R.id.btnChat);
                        TextView orText = (TextView) linearEstimasi[count].findViewById(R.id.textOr);
                        String formulas = jsonArray.getJSONObject(i).getString("formula").toString();
                        JSONObject jjs = new JSONObject(formulas);

                        if (jjs.has("call")) {

                            btnCall.setVisibility(View.VISIBLE);
                            btnCall.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                                    try {
                                        callIntent.setData(Uri.parse("tel:+" + jjs.getString("call")));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    startActivity(callIntent);
                                }
                            });

                        }
                        if (jjs.has("chat")) {

                            btnChat.setVisibility(View.VISIBLE);
                            btnChat.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ChatParty sample = null;
                                    try {
                                        sample = new Contact("", jjs.getString("chat"), "");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    IconItem item = null;
                                    try {
                                        item = new IconItem(jjs.getString("chat"), "", "", "", sample);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    if (item.getJabberId().equalsIgnoreCase("")) {
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), ConversationActivity.class);
                                        String jabberId = item.getJabberId();
                                        intent.putExtra(ConversationActivity.KEY_JABBER_ID, jabberId);
                                        intent.putExtra(Constants.EXTRA_ITEM, item);
                                        startActivity(intent);
                                    }
                                }
                            });

                        }

                        if (jjs.has("call") && jjs.has("chat")) {
                            orText.setVisibility(View.VISIBLE);
                        }

                        hashMap.put(Integer.parseInt(idListTask), valSetOne);
                        lolosReq.add(idListTask);


                    } else if (type.equalsIgnoreCase("preview_document")) {

                        TextView textView = new TextView(DinamicRoomTaskActivity.this);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(15);


                        if (count == null) {
                            count = 0;
                        } else {
                            count++;
                        }


                        final List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(String.valueOf(count));
                        valSetOne.add(required);
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));

                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(30, 10, 30, 0);
                        textView.setLayoutParams(params2);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(textView);

                        linearEstimasi[count] = (LinearLayout) getLayoutInflater().inflate(R.layout.upload_doc_layout, null);
                        linearEstimasi[count].setLayoutParams(params2);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(linearEstimasi[count]);

                        final JSONObject jObject = new JSONObject(value);
                        String urlP = jObject.getString("url");
                        String valueP = jObject.getString("value");

                        final Button btnOption = (Button) linearEstimasi[count].findViewById(R.id.btn_browse);
                        btnOption.setText("Open");
                        final TextView valueFile = (TextView) linearEstimasi[count].findViewById(R.id.value);
                        valueFile.setText(valueP);
                        if (urlP.contains("Document_27122018_164006_dyhW8uilXa.pdf")) {
                            valueFile.setText(getOficer("lokasi").replace(" ", "_") + ".pdf");
                        }

                        final int finalI25 = i;
                        String finalLabel4 = label;
                        btnOption.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, DownloadFileByonchat.class);
                                if (urlP.contains("Document_27122018_164006_dyhW8uilXa.pdf")) {
                                    String urlPaa = new ValidationsKey().getInstance(activity).getTargetUrl(username) + "/bc_voucher_client/public/list_task/document_preview/";

                                    intent.putExtra("path", urlPaa + getOficer("lokasi").replace(" ", "_") + ".pdf");
                                    intent.putExtra("nama_file", getOficer("lokasi").replace(" ", "_") + ".pdf");
                                    intent.putExtra("remove", "true");

                                } else {
                                    intent.putExtra("path", urlP);
                                    intent.putExtra("nama_file", finalLabel4);
                                }


                                startActivity(intent);
                            }
                        });

                        hashMap.put(Integer.parseInt(idListTask), valSetOne);
                        lolosReq.add(idListTask);

                    } else if (type.equalsIgnoreCase("dropdown_views")) {

                        if (count == null) {
                            count = 0;
                        } else {
                            count++;
                        }

                        TextView textView = new TextView(DinamicRoomTaskActivity.this);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(15);
                        textView.setLayoutParams(new TableRow.LayoutParams(0));

                        TableRow.LayoutParams params2 = new TableRow.LayoutParams(1);
                        final String isi = jsonArray.getJSONObject(i).getString("dropdown_views").toString();

                        final JSONArray jsonArrays = new JSONArray(isi);


                        final ArrayList<String> spinnerArray = new ArrayList<String>();

                        spinnerArray.add("--Please Select--");
                        HashMap<String, ArrayList<String>> hashMapss = new HashMap<>();

                        for (int ia = 0; ia < jsonArrays.length(); ia++) {
                            String l = jsonArrays.getJSONObject(ia).getString("label").toString();
                            JSONArray pairs = jsonArrays.getJSONObject(ia).getJSONArray("pairs");
                            final ArrayList<String> arrayPair = new ArrayList<String>();
                            for (int iaa = 0; iaa < pairs.length(); iaa++) {
                                arrayPair.add(pairs.get(iaa).toString());
                            }
                            hashMapss.put(l, arrayPair);
                            spinnerArray.add(l);

                        }

                        newDropdownViews.put(Integer.parseInt(idListTask), hashMapss);

                        SearchableSpinner spinner = new SearchableSpinner(this);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            spinner.setBackground(getResources().getDrawable(R.drawable.spinner_background));
                        }
                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
                        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(spinnerArrayAdapter);
                        params2.setMargins(30, 10, 30, 40);
                        spinner.setLayoutParams(params2);

                        String hasilDariDB = "";
                        if (JcontentBawaan.has(name)) {
                            if (!JcontentBawaan.getString(name).equalsIgnoreCase("null")) {
                                JSONObject values = new JSONObject(JcontentBawaan.getString(name));
                                if (values.has("value")) {
                                    hasilDariDB = values.getString("value");
                                }
                            }
                        }

                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                        if (cursorCild.getCount() > 0) {
                            hasilDariDB = cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));
                        }


                        if (hasilDariDB.length() > 0) {
                            int spinnerPosition = spinnerArrayAdapter.getPosition(hasilDariDB);
                            spinner.setSelection(spinnerPosition);
                        } else {
                            if (spinner.getSelectedItem() != null) {
                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, spinner.getSelectedItem().toString(), jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                                db.insertRoomsDetail(orderModel);
                            }

                        }


                        if ((!showButton)) {
                            spinner.setEnabled(false);
                        } else {
                            final int finalI7 = i;
                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                @Override
                                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int myPosition, long myID) {

                                    if (!spinnerArray.get(myPosition).equalsIgnoreCase("--Please Select--")) {
                                        HashMap<String, ArrayList<String>> hashMapL = newDropdownViews.get(Integer.parseInt(idListTask));
                                        ArrayList<String> udah = new ArrayList<>();

                                        for (int asik = 1; asik < (spinnerArray.size()); asik++) {
                                            String slip = spinnerArray.get(asik);
                                            ArrayList<String> sss = hashMapL.get(slip);
                                            if (slip.equalsIgnoreCase(spinnerArray.get(myPosition))) {
                                                for (int ia = 0; ia < sss.size(); ia++) {
                                                    udah.add(sss.get(ia));
                                                    List value = (List) hashMap.get(Integer.parseInt(sss.get(ia)));
                                                    if (value != null) {
                                                        for (int ii = 0; ii < (value.size() - 6); ii++) {
                                                            lolosReq.remove(sss.get(ia));
                                                            linearLayout.getChildAt(Integer.valueOf(value.get(6 + ii).toString())).setVisibility(View.VISIBLE);
                                                        }
                                                    }
                                                }
                                            } else {
                                                //false
                                                for (int ia = 0; ia < sss.size(); ia++) {
                                                    List value = (List) hashMap.get(Integer.parseInt(sss.get(ia)));
                                                    if (value != null) {
                                                        for (int ii = 0; ii < (value.size() - 6); ii++) {
                                                            if (!udah.contains(sss.get(ia))) {
                                                                lolosReq.add(sss.get(ia));
                                                                linearLayout.getChildAt(Integer.valueOf(value.get(6 + ii).toString())).setVisibility(View.GONE);
                                                            }
                                                        }
                                                    }
                                                }

                                            }
                                        }

                                    } else {

                                        HashMap<String, ArrayList<String>> hashMapL = newDropdownViews.get(Integer.parseInt(idListTask));
                                        Iterator iterator = hashMapL.entrySet().iterator();

                                        while (iterator.hasNext()) {
                                            Map.Entry me2 = (Map.Entry) iterator.next();
                                            ArrayList<String> sss = (ArrayList<String>) me2.getValue();
                                            for (String salam : sss) {
                                                List value = (List) hashMap.get(Integer.parseInt(salam));
                                                if (value != null) {
                                                    for (int ii = 0; ii < (value.size() - 6); ii++) {
                                                        lolosReq.remove(salam);
                                                        linearLayout.getChildAt(Integer.valueOf(value.get(6 + ii).toString())).setVisibility(View.GONE);
                                                    }
                                                }
                                            }
                                        }

                                    }


                                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI7)));
                                    if (cEdit.getCount() > 0) {
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(spinnerArray.get(myPosition)), jsonCreateType(idListTask, type, String.valueOf(finalI7)), name, "cild");
                                        db.updateDetailRoomWithFlagContent(orderModel);
                                    } else {
                                        if (String.valueOf(spinnerArray.get(myPosition)).length() > 0) {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(spinnerArray.get(myPosition)), jsonCreateType(idListTask, type, String.valueOf(finalI7)), name, "cild");
                                            db.insertRoomsDetail(orderModel);
                                        } else {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(spinnerArray.get(myPosition)), jsonCreateType(idListTask, type, String.valueOf(finalI7)), name, "cild");
                                            db.deleteDetailRoomWithFlagContent(orderModel);
                                        }
                                    }


                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parentView) {
                                }

                            });
                        }
                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params1.setMargins(30, 10, 30, 0);

                        LinearLayout.LayoutParams params12 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params12.setMargins(50, 10, 30, 0);

                        List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add("");
                        valSetOne.add(required);
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));

                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(textView, params1);

                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(spinner, params1);
                        View view = new View(this);
                        view.setVisibility(View.INVISIBLE);

                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(view, params2);

                        hashMap.put(Integer.parseInt(idListTask), valSetOne);


                    } else if (type.equalsIgnoreCase("attach_api")) {
                        if (JcontentBawaanReject.length() == 0) {
                            if (count == null) {
                                count = 0;
                            } else {
                                count++;
                            }
                            tp[count] = (TextView) getLayoutInflater().inflate(R.layout.text_view_layout, null);
                            if (required.equalsIgnoreCase("1")) {
                                label += "<font size=\"3\" color=\"red\">*</font>";
                            }
                            tp[count].setText(Html.fromHtml(label));
                            tp[count].setTextSize(15);
                            tp[count].setLayoutParams(new TableRow.LayoutParams(0));


                            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params1.setMargins(30, 10, 30, 0);
                            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params2.setMargins(30, 10, 30, 40);

                            linearEstimasi[count] = (LinearLayout) getLayoutInflater().inflate(R.layout.api_text_layout_form, null);
                            linearEstimasi[count].setLayoutParams(params2);


                            final HtmlTextView valueFile = (HtmlTextView) linearEstimasi[count].findViewById(R.id.value);
                            AVLoadingIndicatorView progress = (AVLoadingIndicatorView) linearEstimasi[count].findViewById(R.id.loader_progress);

                            final int finalI = i;
                            valueFile.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    Intent newIntent = new Intent("bLFormulas");
                                    sendBroadcast(newIntent);

                                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI)));
                                    if (cEdit.getCount() > 0) {
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI)), name, "cild");
                                        db.updateDetailRoomWithFlagContent(orderModel);
                                    } else {
                                        if (String.valueOf(s).length() > 0) {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI)), name, "cild");
                                            db.insertRoomsDetail(orderModel);
                                        } else {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI)), name, "cild");
                                            db.deleteDetailRoomWithFlagContent(orderModel);
                                        }
                                    }
                                }
                            });

                            String jsonData = jsonArray.getJSONObject(i).getString("attach_api");
                            JSONArray ja = null;
                            try {
                                ja = new JSONArray(jsonData);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (ja != null) {

                                if (ja.length() == 0) {
                                    Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                                    if (cursorCild.getCount() > 0) {
                                        progress.setVisibility(View.GONE);
                                        Pattern htmlPattern = Pattern.compile(".*\\<[^>]+>.*", Pattern.DOTALL);
                                        boolean isHTML = htmlPattern.matcher(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT))).matches();
                                        String message = cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).replace("\\r\\n", "<br>").replace("\\n", "<br>");

                                        if (isHTML) {
                                            if (message.contains("<")) {
                                                valueFile.setText(Html.fromHtml(Html.fromHtml(message).toString()));
                                            } else {
                                                valueFile.setText(Html.fromHtml(message));
                                            }
                                        } else {
                                            String a = Html.fromHtml(message).toString();
                                            if (a.contains("<")) {
                                                valueFile.setText(Html.fromHtml(a));
                                            } else {
                                                valueFile.setText(a);
                                            }
                                        }
                                    } else {
                                        if (!value.equalsIgnoreCase("")) {
                                            SaveMedia saveMedia = new SaveMedia();
                                            saveMedia.execute(new MyTaskParams(valueFile, progress, value));
                                        }

                                    }

                                    List<String> valSetOne = new ArrayList<String>();
                                    valSetOne.add(String.valueOf(count));
                                    valSetOne.add(required);
                                    valSetOne.add(type);
                                    valSetOne.add(name);
                                    valSetOne.add(label);
                                    valSetOne.add(String.valueOf(i));

                                    valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                                    linearLayout.addView(tp[count], params1);

                                    valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                                    linearLayout.addView(linearEstimasi[count]);


                                    hashMap.put(Integer.parseInt(idListTask), valSetOne);


                                } else {
                                    valueFile.setText(value);
                                    ArrayList<String> banker = new ArrayList<String>();

                                    banker.add(0, jsonPosCode(idListTask, String.valueOf(count), type, String.valueOf(i), value));
                                    for (int ii = 0; ii < ja.length(); ii++) {
                                        banker.add(ii + 1, ja.getString(ii));
                                    }

                                    stringAPI.add(banker);
                                }

                            }
                        }

                    } else if (type.equalsIgnoreCase("qrcode")) {
                        TextView textView = new TextView(DinamicRoomTaskActivity.this);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(15);
                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(30, 10, 30, 0);
                        textView.setLayoutParams(params2);
                        linearLayout.addView(textView);

                        if (count == null) {
                            count = 0;
                        } else {
                            count++;
                        }

                        final List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(String.valueOf(count));
                        valSetOne.add(required);
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));


                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearEstimasi[count] = (LinearLayout) getLayoutInflater().inflate(R.layout.qr_scan_layout, null);
                        linearEstimasi[count].setLayoutParams(params2);
                        linearLayout.addView(linearEstimasi[count]);


                        final Button btnOption = (Button) linearEstimasi[count].findViewById(R.id.btn_browse);
                        final EditText valueFile = (EditText) linearEstimasi[count].findViewById(R.id.value);
                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                        if (cursorCild.getCount() > 0) {
                            valueFile.setText(jsonResultType(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)), "a"));
                        }

                        final int finalI25 = i;
                        btnOption.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dummyIdDate = Integer.parseInt(idListTask);
                                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI25)));
                                if (cEdit.getCount() == 0) {
                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(idListTask, type, String.valueOf(finalI25)), name, "cild");
                                    db.insertRoomsDetail(orderModel);
                                }

                                try {
                                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes

                                    startActivityForResult(intent, QRCODE_REQUEST);
                                } catch (Exception e) {
                                    Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
                                    Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                                    startActivity(marketIntent);
                                }


                            }
                        });
                        final int finalI = i;
                        valueFile.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                Intent newIntent = new Intent("bLFormulas");
                                sendBroadcast(newIntent);

                                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI)));
                                if (cEdit.getCount() > 0) {
                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI)), name, "cild");
                                    db.updateDetailRoomWithFlagContent(orderModel);
                                } else {
                                    if (String.valueOf(s).length() > 0) {
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI)), name, "cild");
                                        db.insertRoomsDetail(orderModel);
                                    } else {
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI)), name, "cild");
                                        db.deleteDetailRoomWithFlagContent(orderModel);
                                    }
                                }
                            }
                        });

                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

                    } else if (type.equalsIgnoreCase("form_child")) {

                        boolean spk = false;

                        final String formChild = jsonArray.getJSONObject(i).getString("form_child").toString();
                        String asal = "";
                        ArrayList<String> asall = new ArrayList<String>();

                        if (jsonArray.getJSONObject(i).has("parent_child")) {
                            String parent_child = jsonArray.getJSONObject(i).getString("parent_child");
                            if (parent_child.equalsIgnoreCase("null")) {
                            } else {

                                asal = idFormChildParent.get(Integer.valueOf(parent_child));
                                JSONArray jsonArrayCildWOO = new JSONArray(formChild);
                                asall.add(0, "");
                                for (int iasal = 0; iasal < jsonArrayCildWOO.length(); iasal++) {
                                    asall.add(Integer.valueOf(jsonArrayCildWOO.getJSONObject(iasal).getString("order")), jsonArrayCildWOO.getJSONObject(iasal).getString("type"));
                                }
                            }
                        }


                        TextView textView = new TextView(DinamicRoomTaskActivity.this);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(15);
                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(30, 10, 30, 0);
                        params3.setMargins(30, 10, 30, 30);
                        textView.setLayoutParams(params2);
                        linearLayout.addView(textView);

                        if (count == null) {
                            count = 0;
                        } else {
                            count++;
                        }

                        final List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(String.valueOf(count));
                        valSetOne.add(required);
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));


                        if (idListTask.equalsIgnoreCase("65128")) {

                            linearEstimasi[count] = (LinearLayout) getLayoutInflater().inflate(R.layout.form_cild_two_layout, null);
                            TableRow tableLayout = (TableRow) linearEstimasi[count].findViewById(R.id.tableRow2);
                            Button addCild = (Button) linearEstimasi[count].findViewById(R.id.btn_add_cild);
                            TextView titleName = (TextView) linearEstimasi[count].findViewById(R.id.titleName);
                            TextView total_price_order = (TextView) linearEstimasi[count].findViewById(R.id.total_price_order);
                            ListView lv = (ListView) linearEstimasi[count].findViewById(R.id.listOrder);


                            lv.setOnTouchListener(new View.OnTouchListener() {
                                // Setting on Touch Listener for handling the touch inside ScrollView
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    v.getParent().requestDisallowInterceptTouchEvent(true);
                                    return false;
                                }
                            });

                            idListTaskMasterForm = idListTask;
                            List<String> listId = db.getAllRoomDetailFormWithFlagContentWithOutId(DialogFormChildMain.jsonCreateIdTabNUsrName(idTab, username), DialogFormChildMain.jsonCreateIdDetailNIdListTaskOld(idDetail, idListTask), "child_detail");

                            JSONArray jsonArrayMaster = new JSONArray();
                            ArrayList<ModelFormChild> rowItems = new ArrayList<ModelFormChild>();

                            try {
                                int asd = 0;
                                for (String idchildDetail : listId) {
                                    String titleUntuk = "";
                                    String decsUntuk = "";
                                    JSONObject objData = new JSONObject();
                                    objData.put("urutan", asd);
                                    asd++;
                                    List<RoomsDetail> list = db.getAllRoomDetailFormWithFlagContent(idchildDetail, DialogFormChildMain.jsonCreateIdTabNUsrName(idTab, username), DialogFormChildMain.jsonCreateIdDetailNIdListTaskOld(idDetail, idListTask), "child_detail");
                                    JSONArray jsonArrayHUHU = new JSONArray();
                                    for (int u = 0; u < list.size(); u++) {
                                        JSONObject objVV = new JSONObject();
                                        JSONArray jsA = null;
                                        String contentS = "";

                                        String cc = list.get(u).getContent();
                                        if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("input_kodepos") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("dropdown_wilayah")) {
                                            cc = jsoncreateC(list.get(u).getContent());
                                        }

                                        try {
                                            if (cc.startsWith("{")) {
                                                if (!cc.startsWith("[")) {
                                                    cc = "[" + cc + "]";
                                                }
                                                jsA = new JSONArray(cc);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        if (jsA != null) {
                                            if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("distance_estimation") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("dropdown_dinamis") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("new_dropdown_dinamis") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("ocr") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("upload_document")) {
                                                try {
                                                    objVV.put("key", list.get(u).getFlag_tab());
                                                    JSONObject jObject = null;
                                                    try {
                                                        jObject = new JSONObject(list.get(u).getContent());
                                                        titleUntuk = jsonResultType(list.get(u).getContent(), "Nama");
                                                        if (titleUntuk.equalsIgnoreCase("")) {
                                                            titleUntuk = jsonResultType(list.get(u).getContent(), "Name Detail");

                                                        }
                                                        if (titleUntuk.equalsIgnoreCase("")) {
                                                            titleUntuk = jsonResultType(list.get(u).getContent(), "Nama Siswa");
                                                        }


                                                        objVV.put("value", jObject);
                                                    } catch (JSONException e) {
                                                        objVV.put("value", list.get(u).getContent());
                                                        e.printStackTrace();
                                                    }

                                                    objVV.put("type", jsonResultType(list.get(u).getFlag_content(), "b"));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                            } else {
                                                try {
                                                    for (int ic = 0; ic < jsA.length(); ic++) {
                                                        final String icC = jsA.getJSONObject(ic).getString("c").toString();
                                                        contentS += icC + "|";
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                try {
                                                    objVV.put("key", list.get(u).getFlag_tab());
                                                    objVV.put("value", contentS.substring(0, contentS.length() - 1));
                                                    objVV.put("type", jsonResultType(list.get(u).getFlag_content(), "b"));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        } else {
                                            if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("front_camera") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("rear_camera")) {
                                                titleUntuk = list.get(u).getContent();
                                            } else if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("dropdown")) {
                                                decsUntuk = list.get(u).getContent();

                                            } else {
                                                decsUntuk = list.get(u).getContent();
                                            }

                                            try {
                                                objVV.put("key", list.get(u).getFlag_tab());
                                                objVV.put("value", list.get(u).getContent());
                                                objVV.put("type", jsonResultType(list.get(u).getFlag_content(), "b"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            if (list.get(u).getFlag_tab().equalsIgnoreCase("biaya")) {
                                                tableLayout.setVisibility(View.VISIBLE);
                                            }

                                        }
                                        jsonArrayHUHU.put(objVV);
                                    }


                                    rowItems.add(new ModelFormChild(idchildDetail, titleUntuk, decsUntuk, ""));
                                    objData.put("data", jsonArrayHUHU);
                                    jsonArrayMaster.put(objData);
                                }

                                Integer totalQ = 0;
                                for (ModelFormChild mfc : rowItems) {
                                    try {
                                        totalQ += Integer.valueOf(mfc.getDetail().replace(",", ""));
                                    } catch (Exception e) {
                                    }
                                }

                                DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                                formatter.applyPattern("#,###,###,###");
                                String formattedString = formatter.format(totalQ);

                                total_price_order.setText(formattedString);

                                final FormChildAdapter adapter = new FormChildAdapter(context, rowItems, "", false);
                                lv.setAdapter(adapter);


                                if (jsonArrayMaster.length() > 0) {
                                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                                    if (cEdit.getCount() > 0) {

                                        String contentValue = jsonArrayMaster.toString();
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, contentValue, jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                                        db.updateDetailRoomWithFlagContent(orderModel);
                                    } else {

                                        String contentValue = jsonArrayMaster.toString();
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, contentValue, jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                                        db.insertRoomsDetail(orderModel);
                                    }
                                } else {
                                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                                    if (cEdit.getCount() > 0) {
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                                        db.deleteDetailRoomWithFlagContent(orderModel);
                                    }
                                }


                                final String finalDbMaster = dbMaster;
                                addCild.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        FragmentManager fm = getSupportFragmentManager();
                                        ArrayList<String> ada = new ArrayList<>();
                                        JSONArray jsonArrayCild = null;
                                        try {
                                            jsonArrayCild = new JSONArray(formChild);
                                            for (int i = 0; i < jsonArrayCild.length(); i++) {
                                                final String type = jsonArrayCild.getJSONObject(i).getString("type").toString();
                                                if (type.equalsIgnoreCase("rear_camera") || type.equalsIgnoreCase("front_camera")) {
                                                    ada.add("1");
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        if (ada.size() == 0) {
                                            String alau = "0";
                                            if (title.equalsIgnoreCase("SPK")) {
                                                alau = "80";
                                            }

                                            DialogFormChildMainNew testDialog = DialogFormChildMainNew.newInstance(formChild, name, finalDbMaster, idDetail, username, idTab, idListTask, "", customersId, DinamicRoomTaskActivity.this, String.valueOf(linearLayout.getChildAt(Integer.valueOf(alau)).getTop()));
                                            testDialog.setRetainInstance(true);
                                            testDialog.show(fm, "Dialog");
                                        } else {

                                            DialogFormChildMainNcal testDialog = DialogFormChildMainNcal.newInstance(formChild, name, finalDbMaster, idDetail, username, idTab, idListTask, "", customersId, DinamicRoomTaskActivity.this);
                                            testDialog.setRetainInstance(true);
                                            testDialog.show(fm, "Dialog");
                                        }

                                    }
                                });


                                lv.setClickable(true);
                                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                        ModelFormChild item = (ModelFormChild) adapter.getItem(position);
                                        FragmentManager fm = getSupportFragmentManager();
                                        ArrayList<String> ada = new ArrayList<>();
                                        JSONArray jsonArrayCild = null;
                                        try {
                                            jsonArrayCild = new JSONArray(formChild);
                                            for (int i = 0; i < jsonArrayCild.length(); i++) {
                                                final String type = jsonArrayCild.getJSONObject(i).getString("type").toString();
                                                if (type.equalsIgnoreCase("rear_camera") || type.equalsIgnoreCase("front_camera")) {
                                                    ada.add("1");
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                        if (ada.size() == 0) {
                                            String alau = "0";
                                            if (title.equalsIgnoreCase("SPK")) {
                                                alau = "80";
                                            }

                                            DialogFormChildMainNew testDialog = DialogFormChildMainNew.newInstance(formChild, name, finalDbMaster, idDetail, username, idTab, idListTask, item.getId(), customersId, DinamicRoomTaskActivity.this, String.valueOf(linearLayout.getChildAt(Integer.valueOf(alau)).getTop()));

                                            testDialog.setRetainInstance(true);
                                            testDialog.show(fm, "Dialog");
                                        } else {
                                            DialogFormChildMainNcal testDialog = DialogFormChildMainNcal.newInstance(formChild, name, finalDbMaster, idDetail, username, idTab, idListTask, item.getId(), customersId, DinamicRoomTaskActivity.this);
                                            testDialog.setRetainInstance(true);
                                            testDialog.show(fm, "Dialog");
                                        }

                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else if (idListTask.equalsIgnoreCase("63837") || idListTask.equalsIgnoreCase("64305") || idListTask.equalsIgnoreCase("64306") || idListTask.equalsIgnoreCase("64307") || idListTask.equalsIgnoreCase("64308")) {
                            linearEstimasi[count] = (LinearLayout) getLayoutInflater().inflate(R.layout.form_cild_two_layout, null);

                            Button addCild = (Button) linearEstimasi[count].findViewById(R.id.btn_add_cild);
                            ListView lv = (ListView) linearEstimasi[count].findViewById(R.id.listOrder);

                            lv.setOnTouchListener(new View.OnTouchListener() {
                                // Setting on Touch Listener for handling the touch inside ScrollView
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    v.getParent().requestDisallowInterceptTouchEvent(true);
                                    return false;
                                }
                            });

                            idListTaskMasterForm = idListTask;
                            List<String> listId = db.getAllRoomDetailFormWithFlagContentWithOutId(DialogFormChildMain.jsonCreateIdTabNUsrName(idTab, username), DialogFormChildMain.jsonCreateIdDetailNIdListTaskOld(idDetail, idListTask), "child_detail");

                            JSONArray jsonArrayMaster = new JSONArray();
                            ArrayList<ModelFormChild> rowItems = new ArrayList<ModelFormChild>();

                            try {
                                int asd = 0;
                                for (String idchildDetail : listId) {
                                    String titleUntuk = "";
                                    String decsUntuk = "";
                                    String tambahan = "";
                                    JSONObject objData = new JSONObject();
                                    objData.put("urutan", asd);
                                    asd++;
                                    List<RoomsDetail> list = db.getAllRoomDetailFormWithFlagContent(idchildDetail, DialogFormChildMain.jsonCreateIdTabNUsrName(idTab, username), DialogFormChildMain.jsonCreateIdDetailNIdListTaskOld(idDetail, idListTask), "child_detail");
                                    JSONArray jsonArrayHUHU = new JSONArray();
                                    for (int u = 0; u < list.size(); u++) {
                                        JSONObject objVV = new JSONObject();
                                        JSONArray jsA = null;
                                        String contentS = "";

                                        String cc = list.get(u).getContent();
                                        if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("input_kodepos") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("dropdown_wilayah")) {
                                            cc = jsoncreateC(list.get(u).getContent());
                                        }

                                        try {
                                            if (cc.startsWith("{")) {
                                                if (!cc.startsWith("[")) {
                                                    cc = "[" + cc + "]";
                                                }
                                                jsA = new JSONArray(cc);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        if (jsA != null) {
                                            if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("distance_estimation") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("dropdown_dinamis") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("new_dropdown_dinamis") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("ocr") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("upload_document")) {
                                                try {
                                                    objVV.put("key", list.get(u).getFlag_tab());
                                                    JSONObject jObject = null;
                                                    try {
                                                        jObject = new JSONObject(list.get(u).getContent());
                                                        titleUntuk = jsonResultType(list.get(u).getContent(), "Nama");
                                                        tambahan = " " + jsonResultType(list.get(u).getContent(), "Unit");
                                                        if (titleUntuk.equalsIgnoreCase("")) {
                                                            titleUntuk = jsonResultType(list.get(u).getContent(), "Name Detail");
                                                        }
                                                        if (titleUntuk.equalsIgnoreCase("")) {
                                                            titleUntuk = jsonResultType(list.get(u).getContent(), "Nama Siswa");
                                                        }
                                                        objVV.put("value", jObject);
                                                    } catch (JSONException e) {
                                                        objVV.put("value", list.get(u).getContent());
                                                        e.printStackTrace();
                                                    }

                                                    objVV.put("type", jsonResultType(list.get(u).getFlag_content(), "b"));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                            } else {
                                                try {
                                                    for (int ic = 0; ic < jsA.length(); ic++) {
                                                        final String icC = jsA.getJSONObject(ic).getString("c").toString();
                                                        contentS += icC + "|";
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                try {
                                                    objVV.put("key", list.get(u).getFlag_tab());
                                                    objVV.put("value", contentS.substring(0, contentS.length() - 1));
                                                    objVV.put("type", jsonResultType(list.get(u).getFlag_content(), "b"));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        } else {

                                            decsUntuk = list.get(u).getContent();

                                            try {
                                                objVV.put("key", list.get(u).getFlag_tab());
                                                objVV.put("value", list.get(u).getContent());
                                                objVV.put("type", jsonResultType(list.get(u).getFlag_content(), "b"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                        jsonArrayHUHU.put(objVV);
                                    }

                                    rowItems.add(new ModelFormChild(idchildDetail, titleUntuk, decsUntuk + tambahan, ""));
                                    objData.put("data", jsonArrayHUHU);
                                    jsonArrayMaster.put(objData);
                                }


                                final FormChildAdapter adapter = new FormChildAdapter(context, rowItems, "form", false);
                                lv.setAdapter(adapter);

                                if (jsonArrayMaster.length() > 0) {
                                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                                    if (cEdit.getCount() > 0) {
                                        String contentValue = jsonArrayMaster.toString();
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, contentValue, jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                                        db.updateDetailRoomWithFlagContent(orderModel);
                                    } else {
                                        String contentValue = jsonArrayMaster.toString();
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, contentValue, jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                                        db.insertRoomsDetail(orderModel);
                                    }
                                } else {
                                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                                    if (cEdit.getCount() > 0) {
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                                        db.deleteDetailRoomWithFlagContent(orderModel);
                                    }
                                }


                                final String finalDbMaster = dbMaster;
                                addCild.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (customersId.equalsIgnoreCase("")) {
                                            final AlertDialog.Builder alertbox = new AlertDialog.Builder(DinamicRoomTaskActivity.this);
                                            alertbox.setTitle("required");
                                            String content = "Please Select Customer ";
                                            alertbox.setTitle(content);
                                            alertbox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface arg0, int arg1) {

                                                }
                                            });

                                            alertbox.show();
                                            return;
                                        }
                                        if (idListTask.equalsIgnoreCase("62483")) {
                                            //talking Order lemindo
                                            FragmentManager fm = getSupportFragmentManager();
                                            DialogFormChildMainLemindo testDialog = DialogFormChildMainLemindo.newInstance(formChild, name, finalDbMaster, idDetail, username, idTab, idListTask, "", customersId);
                                            testDialog.setRetainInstance(true);
                                            testDialog.show(fm, "Dialog");

                                        } else {
                                            FragmentManager fm = getSupportFragmentManager();
                                            DialogFormChildMainKompetitor testDialog = DialogFormChildMainKompetitor.newInstance(formChild, name, finalDbMaster, idDetail, username, idTab, idListTask, "", customersId);
                                            testDialog.setRetainInstance(true);
                                            testDialog.show(fm, "Dialog");
                                        }
                                    }
                                });


                                lv.setClickable(true);
                                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                        ModelFormChild item = (ModelFormChild) adapter.getItem(position);
                                        FragmentManager fm = getSupportFragmentManager();
                                        DialogFormChildMainKompetitor testDialog = DialogFormChildMainKompetitor.newInstance(formChild, name, finalDbMaster, idDetail, username, idTab, idListTask, item.getId(), customersId);
                                        testDialog.setRetainInstance(true);
                                        testDialog.show(fm, "Dialog");
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            JSONArray jsonArrayCild = new JSONArray(formChild);

                            boolean perhitungan = false;
                            boolean imageForm = false;
                            for (int iaa = 0; iaa < jsonArrayCild.length(); iaa++) {

                                String a = jsonArrayCild.getJSONObject(iaa).getString("formula");
                                String b = jsonArrayCild.getJSONObject(iaa).getString("type");

                                if (jsonArrayCild.getJSONObject(iaa).toString().contains("front_camera") || jsonArrayCild.getJSONObject(iaa).toString().contains("rear_camera")) {
                                    imageForm = true;
                                }

                                if (!a.equalsIgnoreCase("") && !b.equalsIgnoreCase("new_dropdown_dinamis")) {
                                    perhitungan = true;
                                }


                            }


                            if (imageForm == false) {
                                if (idListTask.equalsIgnoreCase("66083") || idListTask.equalsIgnoreCase("66098") || idListTask.equalsIgnoreCase("66100")) {
                                    perhitungan = true;
                                }
                            }

                            if (perhitungan) {
                                linearEstimasi[count] = (LinearLayout) getLayoutInflater().inflate(R.layout.from_cild_layout, null);

                                Button addCild = (Button) linearEstimasi[count].findViewById(R.id.btn_add_cild);
                                ListView lv = (ListView) linearEstimasi[count].findViewById(R.id.listOrder);
                                TextView tQty = (TextView) linearEstimasi[count].findViewById(R.id.total_detail_order);
                                TextView tPrice = (TextView) linearEstimasi[count].findViewById(R.id.total_price_order);

                                lv.setOnTouchListener(new View.OnTouchListener() {
                                    // Setting on Touch Listener for handling the touch inside ScrollView
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        v.getParent().requestDisallowInterceptTouchEvent(true);
                                        return false;
                                    }
                                });

                                tQty.setText("");
                                tPrice.setText("");
                                idListTaskMasterForm = idListTask;
                                List<String> listId = db.getAllRoomDetailFormWithFlagContentWithOutId(DialogFormChildMain.jsonCreateIdTabNUsrName(idTab, username), DialogFormChildMain.jsonCreateIdDetailNIdListTaskOld(idDetail, idListTask), "child_detail");

                                if (listId.size() == 0) {
                                    if (asal.length() > 0) {
                                        try {
                                            JSONObject js1 = new JSONObject(asal);
                                            JSONArray jsonArrayV = js1.getJSONArray("value");
                                            for (int iz = 0; iz < jsonArrayV.length(); iz++) {

                                                JSONArray jsonArrayVv = jsonArrayV.getJSONObject(iz).getJSONArray("data");
                                                String resRandom = DialogFormChildMainNew.getRandomString();


                                                for (int iasd = 0; iasd < jsonArrayVv.length(); iasd++) {
                                                    String a = jsonArrayVv.getJSONObject(iasd).getString("key");
                                                    String b = jsonArrayVv.getJSONObject(iasd).getString("value");
                                                    String c = jsonArrayVv.getJSONObject(iasd).getString("type");

                                                    RoomsDetail orderModel2 = new RoomsDetail(resRandom, DialogFormChildMain.jsonCreateIdTabNUsrName(idTab, username), DialogFormChildMain.jsonCreateIdDetailNIdListTaskOld(idDetail, idListTask), b, jsonCreateType(String.valueOf(asall.indexOf(c)), c, String.valueOf(asall.indexOf(c) - 1)), a, "child_detail");
                                                    db.insertRoomsDetail(orderModel2);
                                                }
                                                listId.add(resRandom);
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                }


                                JSONArray jsonArrayMaster = new JSONArray();
                                ArrayList<ModelFormChild> rowItems = new ArrayList<ModelFormChild>();
                                try {
                                    int asd = 0;
                                    for (String idchildDetail : listId) {
                                        String titleUntuk = "";
                                        String decsUntuk = "";
                                        String priceUntuk = "";
                                        String tambahan = "";
                                        JSONObject objData = new JSONObject();
                                        objData.put("urutan", asd);
                                        asd++;
                                        List<RoomsDetail> list = db.getAllRoomDetailFormWithFlagContent(idchildDetail, DialogFormChildMain.jsonCreateIdTabNUsrName(idTab, username), DialogFormChildMain.jsonCreateIdDetailNIdListTaskOld(idDetail, idListTask), "child_detail");

                                        JSONArray jsonArrayHUHU = new JSONArray();
                                        for (int u = 0; u < list.size(); u++) {


                                            JSONObject objVV = new JSONObject();
                                            JSONArray jsA = null;
                                            String contentS = "";

                                            String cc = list.get(u).getContent();
                                            if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("input_kodepos") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("dropdown_wilayah")) {
                                                cc = jsoncreateC(list.get(u).getContent());
                                            }

                                            try {
                                                if (cc.startsWith("{")) {
                                                    if (!cc.startsWith("[")) {
                                                        cc = "[" + cc + "]";
                                                    }
                                                    jsA = new JSONArray(cc);
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            if (jsA != null) {
                                                if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("distance_estimation") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("dropdown_dinamis") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("new_dropdown_dinamis") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("ocr") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("upload_document")) {
                                                    try {
                                                        objVV.put("key", list.get(u).getFlag_tab());
                                                        JSONObject jObject = null;
                                                        try {
                                                            jObject = new JSONObject(list.get(u).getContent());
                                                            titleUntuk = jsonResultType(list.get(u).getContent(), "Name Detail");
                                                            tambahan = jsonResultType(list.get(u).getContent(), "Unit");
                                                            if (titleUntuk.equalsIgnoreCase("")) {
                                                                titleUntuk = jsonResultType(list.get(u).getContent(), "SKU");
                                                            }
                                                            objVV.put("value", jObject);
                                                        } catch (JSONException e) {
                                                            objVV.put("value", list.get(u).getContent());
                                                            e.printStackTrace();
                                                        }

                                                        objVV.put("type", jsonResultType(list.get(u).getFlag_content(), "b"));
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                } else {
                                                    try {
                                                        for (int ic = 0; ic < jsA.length(); ic++) {
                                                            final String icC = jsA.getJSONObject(ic).getString("c").toString();
                                                            contentS += icC + "|";
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    try {
                                                        objVV.put("key", list.get(u).getFlag_tab());
                                                        objVV.put("value", contentS.substring(0, contentS.length() - 1));
                                                        objVV.put("type", jsonResultType(list.get(u).getFlag_content(), "b"));
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                }
                                            } else {
                                                if (idListTaskMasterForm.equalsIgnoreCase("62483")) {
                                                    if (list.get(u).getFlag_tab().equalsIgnoreCase("qty")) {
                                                        decsUntuk = list.get(u).getContent();
                                                    } else if (list.get(u).getFlag_tab().equalsIgnoreCase("total")) {
                                                        priceUntuk = list.get(u).getContent();
                                                    }
                                                } else {
                                                    if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("number") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("currency")) {
                                                        decsUntuk = list.get(u).getContent();
                                                    } else if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("formula")) {
                                                        priceUntuk = list.get(u).getContent();
                                                    }
                                                }


                                                try {
                                                    objVV.put("key", list.get(u).getFlag_tab());
                                                    objVV.put("value", list.get(u).getContent());
                                                    objVV.put("type", jsonResultType(list.get(u).getFlag_content(), "b"));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                            jsonArrayHUHU.put(objVV);
                                        }

                                        if (idListTaskMasterForm.equalsIgnoreCase("62483")) {
                                            Double nilai = 0.00;
                                            try {
                                                nilai = Double.parseDouble(priceUntuk != null ? priceUntuk.replace(",", "") : "0") / Double.parseDouble(decsUntuk != null ? decsUntuk.replace(",", "") : "0");
                                            } catch (Exception e) {
                                                nilai = 0.00;
                                            }
                                            rowItems.add(new ModelFormChild(idchildDetail, titleUntuk, decsUntuk + " " + tambahan, String.valueOf(nilai)));
                                        } else {
// TODO: 28/10/18 impelria maintenance
                                            if (idListTask.equalsIgnoreCase("66083") || idListTask.equalsIgnoreCase("66098") || idListTask.equalsIgnoreCase("66100")) {
                                                if (list.size() > 1) {
                                                    if (Message.isJSONValid(list.get(1).getContent())) {
                                                        JSONObject jObject = null;
                                                        try {
                                                            jObject = new JSONObject(list.get(1).getContent());
                                                            String tPP = jObject.get("Part ID").toString() + " " + jObject.get("Nama Part").toString() + " (" + list.get(0).getContent() + ")";
                                                            decsUntuk = list.get(2).getContent();
                                                            priceUntuk = jObject.get("AVE").toString();

                                                            rowItems.add(new ModelFormChild(idchildDetail, tPP, decsUntuk, priceUntuk));

                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }

                                                    }
                                                }
                                            } else {
                                                rowItems.add(new ModelFormChild(idchildDetail, titleUntuk, decsUntuk, priceUntuk));
                                            }


                                        }
                                        objData.put("data", jsonArrayHUHU);
                                        jsonArrayMaster.put(objData);
                                    }

                                    Integer totalQ = 0;
                                    Double totalP = 0.0;
                                    Double nilai = 0.0;
                                    for (ModelFormChild mfc : rowItems) {
                                        try {
                                            String dodo = mfc.getDetail();
                                            if (dodo.contains(" ")) {
                                                dodo = dodo.substring(0, dodo.indexOf(" "));
                                            }
                                            String ssss = new Validations().getInstance(context).numberToCurency(String.valueOf(Double.parseDouble(mfc.getPrice().replace(",", "")) * Double.parseDouble(dodo.replace(",", ""))));
                                            totalP += Double.parseDouble(ssss.replace(",", ""));
                                            totalQ += Integer.valueOf(dodo);
                                        } catch (Exception e) {

                                        }
                                    }

                                    String totalHarga = new Validations().getInstance(context).numberToCurency(totalP + "");

                                    tQty.setText(totalQ + "");
                                    tPrice.setText(totalHarga);

                                    final FormChildAdapter adapter = new FormChildAdapter(context, rowItems, "form", false);
                                    lv.setAdapter(adapter);

                                    if (jsonArrayMaster.length() > 0) {
                                        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                                        if (cEdit.getCount() > 0) {
                                            String contentValue = jsonArrayMaster.toString();
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, contentValue, jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                                            db.updateDetailRoomWithFlagContent(orderModel);
                                        } else {
                                            String contentValue = jsonArrayMaster.toString();
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, contentValue, jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                                            db.insertRoomsDetail(orderModel);
                                        }
                                    } else {
                                        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                                        if (cEdit.getCount() > 0) {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                                            db.deleteDetailRoomWithFlagContent(orderModel);
                                        }
                                    }


                                    final String finalDbMaster = dbMaster;
                                    addCild.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (idListTask.equalsIgnoreCase("66083") || idListTask.equalsIgnoreCase("66098") || idListTask.equalsIgnoreCase("66100")) {
                                                FragmentManager fm = getSupportFragmentManager();
                                                DialogFormChildMainNew testDialog = DialogFormChildMainNew.newInstance(formChild, name, finalDbMaster, idDetail, username, idTab, idListTask, "", customersId, DinamicRoomTaskActivity.this, String.valueOf(linearLayout.getChildAt(0).getTop()));
                                                testDialog.setRetainInstance(true);
                                                testDialog.show(fm, "Dialog");
                                            } else {
                                                if (customersId.equalsIgnoreCase("")) {
                                                    final AlertDialog.Builder alertbox = new AlertDialog.Builder(DinamicRoomTaskActivity.this);
                                                    alertbox.setTitle("required");
                                                    String content = "Please Select Customer ";
                                                    alertbox.setTitle(content);
                                                    alertbox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface arg0, int arg1) {

                                                        }
                                                    });

                                                    alertbox.show();
                                                    return;
                                                }
                                                if (idListTask.equalsIgnoreCase("62483")) {
                                                    //talking Order lemindo
                                                    FragmentManager fm = getSupportFragmentManager();
                                                    DialogFormChildMainLemindo testDialog = DialogFormChildMainLemindo.newInstance(formChild, name, finalDbMaster, idDetail, username, idTab, idListTask, "", customersId);
                                                    testDialog.setRetainInstance(true);
                                                    testDialog.show(fm, "Dialog");

                                                } else {
                                                    FragmentManager fm = getSupportFragmentManager();
                                                    DialogFormChildMain testDialog = DialogFormChildMain.newInstance(formChild, name, finalDbMaster, idDetail, username, idTab, idListTask, "", customersId);
                                                    testDialog.setRetainInstance(true);
                                                    testDialog.show(fm, "Dialog");
                                                }
                                            }
                                        }
                                    });


                                    lv.setClickable(true);
                                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                            if (idListTaskMasterForm.equalsIgnoreCase("62483")) {
                                                //talking Order lemindo
                                                ModelFormChild item = (ModelFormChild) adapter.getItem(position);
                                                FragmentManager fm = getSupportFragmentManager();
                                                DialogFormChildMainLemindo testDialog = DialogFormChildMainLemindo.newInstance(formChild, name, finalDbMaster, idDetail, username, idTab, idListTask, item.getId(), customersId);
                                                testDialog.setRetainInstance(true);
                                                testDialog.show(fm, "Dialog");

                                            } else {
                                                ModelFormChild item = (ModelFormChild) adapter.getItem(position);
                                                FragmentManager fm = getSupportFragmentManager();

                                                if (idListTask.equalsIgnoreCase("66083") || idListTask.equalsIgnoreCase("66098") || idListTask.equalsIgnoreCase("66100")) {

                                                    DialogFormChildMainNew testDialog = DialogFormChildMainNew.newInstance(formChild, name, finalDbMaster, idDetail, username, idTab, idListTask, item.getId(), customersId, DinamicRoomTaskActivity.this, String.valueOf(linearLayout.getChildAt(0).getTop()));
                                                    testDialog.setRetainInstance(true);
                                                    testDialog.show(fm, "Dialog");

                                                } else {

                                                    DialogFormChildMain testDialog = DialogFormChildMain.newInstance(formChild, name, finalDbMaster, idDetail, username, idTab, idListTask, item.getId(), customersId);
                                                    testDialog.setRetainInstance(true);
                                                    testDialog.show(fm, "Dialog");
                                                }
                                            }
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if (idListTaskMasterForm.equalsIgnoreCase("66900")) {
                                    spk = true;
                                }

                                linearEstimasi[count] = (LinearLayout) getLayoutInflater().inflate(R.layout.form_cild_two_layout, null);
                                TableRow tableLayout = (TableRow) linearEstimasi[count].findViewById(R.id.tableRow2);
                                Button addCild = (Button) linearEstimasi[count].findViewById(R.id.btn_add_cild);
                                TextView titleName = (TextView) linearEstimasi[count].findViewById(R.id.titleName);
                                TextView total_price_order = (TextView) linearEstimasi[count].findViewById(R.id.total_price_order);
                                ListView lv = (ListView) linearEstimasi[count].findViewById(R.id.listOrder);


                                lv.setOnTouchListener(new View.OnTouchListener() {
                                    // Setting on Touch Listener for handling the touch inside ScrollView
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        v.getParent().requestDisallowInterceptTouchEvent(true);
                                        return false;
                                    }
                                });

                                idListTaskMasterForm = idListTask;
                                List<String> listId = db.getAllRoomDetailFormWithFlagContentWithOutId(DialogFormChildMain.jsonCreateIdTabNUsrName(idTab, username), DialogFormChildMain.jsonCreateIdDetailNIdListTaskOld(idDetail, idListTask), "child_detail");

                                JSONArray jsonArrayMaster = new JSONArray();
                                ArrayList<ModelFormChild> rowItems = new ArrayList<ModelFormChild>();

                                try {
                                    int asd = 0;
                                    for (String idchildDetail : listId) {
                                        String titleUntuk = "";
                                        String decsUntuk = "";
                                        String priceUntuk = "";
                                        JSONObject objData = new JSONObject();
                                        objData.put("urutan", asd);
                                        asd++;
                                        List<RoomsDetail> list = db.getAllRoomDetailFormWithFlagContent(idchildDetail, DialogFormChildMain.jsonCreateIdTabNUsrName(idTab, username), DialogFormChildMain.jsonCreateIdDetailNIdListTaskOld(idDetail, idListTask), "child_detail");
                                        JSONArray jsonArrayHUHU = new JSONArray();
                                        for (int u = 0; u < list.size(); u++) {
                                            JSONObject objVV = new JSONObject();
                                            JSONArray jsA = null;
                                            String contentS = "";

                                            String cc = list.get(u).getContent();
                                            if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("input_kodepos") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("dropdown_wilayah")) {
                                                cc = jsoncreateC(list.get(u).getContent());
                                            }

                                            try {
                                                if (cc.startsWith("{")) {
                                                    if (!cc.startsWith("[")) {
                                                        cc = "[" + cc + "]";
                                                    }
                                                    jsA = new JSONArray(cc);
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            if (jsA != null) {
                                                if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("distance_estimation") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("dropdown_dinamis") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("new_dropdown_dinamis") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("ocr") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("upload_document")) {

                                                    try {
                                                        objVV.put("key", list.get(u).getFlag_tab());
                                                        JSONObject jObject = null;
                                                        try {
                                                            jObject = new JSONObject(list.get(u).getContent());
                                                            titleUntuk = jsonResultType(list.get(u).getContent(), "Nama");
                                                            if (titleUntuk.equalsIgnoreCase("")) {
                                                                titleUntuk = jsonResultType(list.get(u).getContent(), "Name Detail");
                                                            }
                                                            if (titleUntuk.equalsIgnoreCase("")) {
                                                                titleUntuk = jsonResultType(list.get(u).getContent(), "Nama Siswa");
                                                            }
                                                            objVV.put("value", jObject);
                                                        } catch (JSONException e) {
                                                            objVV.put("value", list.get(u).getContent());
                                                            e.printStackTrace();
                                                        }

                                                        objVV.put("type", jsonResultType(list.get(u).getFlag_content(), "b"));

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                } else {
                                                    try {
                                                        for (int ic = 0; ic < jsA.length(); ic++) {
                                                            final String icC = jsA.getJSONObject(ic).getString("c").toString();
                                                            contentS += icC + "|";
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    try {
                                                        objVV.put("key", list.get(u).getFlag_tab());
                                                        objVV.put("value", contentS.substring(0, contentS.length() - 1));
                                                        objVV.put("type", jsonResultType(list.get(u).getFlag_content(), "b"));
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                }
                                            } else {
                                                if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("front_camera") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("rear_camera")) {
                                                    titleName.setText(list.get(u).getFlag_tab());
                                                    titleUntuk = list.get(u).getContent();
                                                } else if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("dropdown")) {
                                                    titleName.setText(list.get(u).getFlag_tab());
                                                    titleUntuk = list.get(u).getContent();
                                                } else {
                                                    decsUntuk = list.get(u).getContent();
                                                    if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("number") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("currency")) {
                                                        priceUntuk = list.get(u).getContent();
                                                    }

                                                }

                                                try {
                                                    objVV.put("key", list.get(u).getFlag_tab());
                                                    objVV.put("value", list.get(u).getContent());
                                                    objVV.put("type", jsonResultType(list.get(u).getFlag_content(), "b"));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                                if (list.get(u).getFlag_tab().equalsIgnoreCase("biaya") || list.get(u).getFlag_tab().equalsIgnoreCase("nomnom")) {
                                                    tableLayout.setVisibility(View.VISIBLE);
                                                }

                                            }

                                            JSONArray jsonArrayCildQQ = new JSONArray(formChild);
                                            for (int jsonArrayCildQQi = 0; jsonArrayCildQQi < jsonArrayCildQQ.length(); jsonArrayCildQQi++) {
                                                String idd = jsonArrayCildQQ.getJSONObject(jsonArrayCildQQi).getString("order").toString();
                                                if (jsonResultType(list.get(u).getFlag_content(), "a").equalsIgnoreCase(idd)) {
                                                    objVV.put("label", jsonArrayCildQQ.getJSONObject(jsonArrayCildQQi).getString("label").toString());
                                                }
                                            }

                                            jsonArrayHUHU.put(objVV);
                                        }

                                        Integer start = Integer.valueOf(idListTask);

                                        if (start >= 65480) {
                                            titleUntuk = list.get(0).getContent().toString();
                                            if (list.size() > 1) {

                                                decsUntuk = list.get(1).getContent().toString();
                                                String valUs = "";
                                                if (Message.isJSONValid(decsUntuk)) {
                                                    JSONObject jObject = null;
                                                    try {
                                                        jObject = new JSONObject(decsUntuk);

                                                        Iterator<String> keys = jObject.keys();
                                                        while (keys.hasNext()) {
                                                            String keyValue = (String) keys.next();
                                                            String valueString = jObject.getString(keyValue);
                                                            valUs += keyValue + " : " + valueString + "\n";
                                                        }

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                }

                                                if (valUs.length() != 0) {
                                                    titleUntuk += "\n" + valUs.substring(0, (valUs.length() - 1));
                                                    decsUntuk = "";
                                                    if (list.size() == 2) {
                                                        decsUntuk = list.get(1).getContent().toString();

                                                    }

                                                }


                                            } else if (list.size() == 1) {
                                                decsUntuk = "";
                                            }
                                        }

                                        if (idListTaskMasterForm.equalsIgnoreCase("66900")) {
                                            spk = true;
                                        }

                                        rowItems.add(new ModelFormChild(idchildDetail, titleUntuk, decsUntuk, priceUntuk, spk));
                                        objData.put("data", jsonArrayHUHU);

                                        jsonArrayMaster.put(objData);
                                    }

                                    Integer totalQ = 0;

                                    if (idListTaskMasterForm.equalsIgnoreCase("66900")) {
                                        titleName.setVisibility(View.GONE);
                                        for (ModelFormChild mfc : rowItems) {
                                            try {
                                                totalQ += Integer.valueOf(mfc.getPrice().replace(",", ""));
                                            } catch (Exception e) {
                                            }
                                        }

                                        total_price_order.setText(new Validations().getInstance(context).numberToCurency(String.valueOf(totalQ)));

                                    } else {
                                        for (ModelFormChild mfc : rowItems) {
                                            try {
                                                totalQ += Integer.valueOf(mfc.getDetail().replace(",", ""));
                                            } catch (Exception e) {
                                            }
                                        }
                                        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                                        formatter.applyPattern("#,###,###,###");
                                        String formattedString = formatter.format(totalQ);

                                        total_price_order.setText(formattedString);

                                    }


                                    final FormChildAdapter adapter = new FormChildAdapter(context, rowItems, "", imageForm);
                                    lv.setAdapter(adapter);

                                    if (jsonArrayMaster.length() > 0) {
                                        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                                        if (cEdit.getCount() > 0) {

                                            String contentValue = jsonArrayMaster.toString();
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, contentValue, jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                                            db.updateDetailRoomWithFlagContent(orderModel);
                                        } else {

                                            String contentValue = jsonArrayMaster.toString();
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, contentValue, jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                                            db.insertRoomsDetail(orderModel);
                                        }
                                    } else {
                                        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                                        if (cEdit.getCount() > 0) {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                                            db.deleteDetailRoomWithFlagContent(orderModel);
                                        }
                                    }


                                    final String finalDbMaster = dbMaster;
                                    addCild.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            FragmentManager fm = getSupportFragmentManager();
                                            ArrayList<String> ada = new ArrayList<>();
                                            JSONArray jsonArrayCild = null;
                                            try {
                                                jsonArrayCild = new JSONArray(formChild);
                                                for (int i = 0; i < jsonArrayCild.length(); i++) {
                                                    final String type = jsonArrayCild.getJSONObject(i).getString("type").toString();
                                                    if (type.equalsIgnoreCase("rear_camera") || type.equalsIgnoreCase("front_camera")) {
                                                        ada.add("1");
                                                    }
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            if (ada.size() == 0) {
                                                String alau = "0";
                                                if (title.equalsIgnoreCase("SPK")) {
                                                    alau = "78";
                                                }

                                                DialogFormChildMainNew testDialog = DialogFormChildMainNew.newInstance(formChild, name, finalDbMaster, idDetail, username, idTab, idListTask, "", customersId, DinamicRoomTaskActivity.this, String.valueOf(linearLayout.getChildAt(Integer.valueOf(alau)).getTop()));
                                                testDialog.setRetainInstance(true);
                                                testDialog.show(fm, "Dialog");
                                            } else {
                                                DialogFormChildMainNcal testDialog = DialogFormChildMainNcal.newInstance(formChild, name, finalDbMaster, idDetail, username, idTab, idListTask, "", customersId, DinamicRoomTaskActivity.this);
                                                testDialog.setRetainInstance(true);
                                                testDialog.show(fm, "Dialog");
                                            }

                                        }
                                    });


                                    lv.setClickable(true);
                                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                            ModelFormChild item = (ModelFormChild) adapter.getItem(position);
                                            FragmentManager fm = getSupportFragmentManager();
                                            ArrayList<String> ada = new ArrayList<>();
                                            JSONArray jsonArrayCild = null;
                                            try {
                                                jsonArrayCild = new JSONArray(formChild);
                                                for (int i = 0; i < jsonArrayCild.length(); i++) {
                                                    final String type = jsonArrayCild.getJSONObject(i).getString("type").toString();
                                                    if (type.equalsIgnoreCase("rear_camera") || type.equalsIgnoreCase("front_camera")) {
                                                        ada.add("1");
                                                    }
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }


                                            if (ada.size() == 0) {
                                                String alau = "0";
                                                if (title.equalsIgnoreCase("SPK")) {
                                                    alau = "77";
                                                }

                                                DialogFormChildMainNew testDialog = DialogFormChildMainNew.newInstance(formChild, name, finalDbMaster, idDetail, username, idTab, idListTask, item.getId(), customersId, DinamicRoomTaskActivity.this, String.valueOf(linearLayout.getChildAt(Integer.valueOf(alau)).getTop()));
                                                testDialog.setRetainInstance(true);
                                                testDialog.show(fm, "Dialog");
                                            } else {
                                                DialogFormChildMainNcal testDialog = DialogFormChildMainNcal.newInstance(formChild, name, finalDbMaster, idDetail, username, idTab, idListTask, item.getId(), customersId, DinamicRoomTaskActivity.this);
                                                testDialog.setRetainInstance(true);
                                                testDialog.show(fm, "Dialog");
                                            }

                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }


                        }

                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(linearEstimasi[count], params3);

                        hashMap.put(Integer.parseInt(idListTask), valSetOne);
                    } else if (type.equalsIgnoreCase("copy_field")) {

                        TextView textView = new TextView(DinamicRoomTaskActivity.this);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(15);

                        if (count == null) {
                            count = 0;
                        } else {
                            count++;
                        }

                        et[count] = (EditText) getLayoutInflater().inflate(R.layout.edit_input_layout, null);
                        List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(String.valueOf(count));
                        valSetOne.add(required);
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));

                        et[count].setId(Integer.parseInt(idListTask));
                        et[count].setHint(placeHolder);


                        et[count].setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(maxlength))});
                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                        Boolean copyDari = false;


                        if (cursorCild.getCount() > 0) {
                            if (cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).length() > 0) {
                                et[count].setText(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                            } else {
                                if (JcontentBawaan.has(name)) {
                                    if (!JcontentBawaan.getString(name).equalsIgnoreCase("null")) {
                                        JSONObject values = new JSONObject(JcontentBawaan.getString(name));
                                        if (values.has("value")) {
                                            et[count].setText(values.getString("value"));
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, values.getString("value"), jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                                            db.insertRoomsDetail(orderModel);
                                        }
                                    }
                                } else {
                                    copyDari = true;
                                }
                            }


                        } else {
                            if (!value.equalsIgnoreCase("")) {
                                et[count].setText(value);
                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, value, jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                                db.insertRoomsDetail(orderModel);
                            } else {
                                if (JcontentBawaan.has(name)) {
                                    if (!JcontentBawaan.getString(name).equalsIgnoreCase("null")) {
                                        JSONObject values = new JSONObject(JcontentBawaan.getString(name));
                                        if (values.has("value")) {
                                            et[count].setText(values.getString("value"));
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, values.getString("value"), jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                                            db.insertRoomsDetail(orderModel);
                                        }
                                    }
                                } else {
                                    copyDari = true;
                                }
                            }
                        }

                        if (copyDari) {
                            Cursor cursorValue = db.getSingleRoomDetailFormWithFlag(idDetail, username, idTab, "value");
                            if (cursorValue.getCount() > 0) {
                                String valUEParent = "";
                                final String contentValue = cursorValue.getString(cursorValue.getColumnIndexOrThrow(BotListDB.ROOM_CONTENT));
                                JSONArray jsonArrayYes = null;
                                try {
                                    jsonArrayYes = new JSONArray(contentValue);
                                    for (int ii = (jsonArrayYes.length() - 1); ii >= 0; ii--) {

                                        JSONArray magic = new JSONArray(jsonArrayYes.getJSONArray(ii).toString());
                                        JSONObject oContent2 = new JSONObject(magic.get(1).toString());
                                        JSONArray joContent = oContent2.getJSONArray("value_detail");

                                        for (int iff = 0; iff < joContent.length(); iff++) {
                                            final String idValue = joContent.getJSONObject(iff).getString("id").toString();
                                            String pareen = jsonArray.getJSONObject(i).getString("copy_from").toString();
                                            if (idValue.equalsIgnoreCase(pareen)) {
                                                valUEParent = joContent.getJSONObject(iff).getString("value").toString();
                                            }
                                        }

                                    }
                                } catch (Exception c) {
                                    Log.w("YaAmpun", c.toString());
                                }

                                et[count].setText(valUEParent);
                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, valUEParent, jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                                db.insertRoomsDetail(orderModel);
                            }
                        }


                        if ((!showButton)) {
                            et[count].setEnabled(false);
                        } else {
                            final int finalI = i;
                            et[count].addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    Intent newIntent = new Intent("bLFormulas");
                                    sendBroadcast(newIntent);

                                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI)));
                                    if (cEdit.getCount() > 0) {
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI)), name, "cild");
                                        db.updateDetailRoomWithFlagContent(orderModel);
                                    } else {
                                        if (String.valueOf(s).length() > 0) {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI)), name, "cild");
                                            db.insertRoomsDetail(orderModel);
                                        } else {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI)), name, "cild");
                                            db.deleteDetailRoomWithFlagContent(orderModel);
                                        }
                                    }
                                }
                            });
                        }

                        String formulas = jsonArray.getJSONObject(i).getString("formula").toString();
                        if (formulas.equalsIgnoreCase("disable")) {
                            et[count].setEnabled(false);
                        }
                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params1.setMargins(30, 10, 30, 0);
                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(30, 10, 30, 40);


                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(textView, params1);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(et[count], params2);
                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

                    } else if (type.equalsIgnoreCase("text") || type.equalsIgnoreCase("email")) {

                        TextView textView = new TextView(DinamicRoomTaskActivity.this);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(15);

                        if (count == null) {
                            count = 0;
                        } else {
                            count++;
                        }

                        et[count] = (EditText) getLayoutInflater().inflate(R.layout.edit_input_layout, null);
                        List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(String.valueOf(count));
                        valSetOne.add(required);
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));


                        if (flag.equalsIgnoreCase("2")) {
                            et[count].setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                        }
                        et[count].setId(Integer.parseInt(idListTask));
                        et[count].setHint(placeHolder);

                        et[count].setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(maxlength))});
                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                        if (cursorCild.getCount() > 0) {
                            et[count].setText(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                        } else {
                            if (!value.equalsIgnoreCase("")) {
                                et[count].setText(value);

                            }

                            if (JcontentBawaan.has(name)) {
                                if (!JcontentBawaan.getString(name).equalsIgnoreCase("null")) {
                                    JSONObject values = new JSONObject(JcontentBawaan.getString(name));
                                    if (values.has("value")) {
                                        et[count].setText(values.getString("value"));
                                    }
                                }
                            }

                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, et[count].getText().toString(), jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                            db.insertRoomsDetail(orderModel);

                        }

                        if ((!showButton)) {
                            et[count].setEnabled(false);
                        } else {
                            final int finalI = i;
                            et[count].addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    Intent newIntent = new Intent("bLFormulas");
                                    sendBroadcast(newIntent);

                                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI)));
                                    if (cEdit.getCount() > 0) {
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI)), name, "cild");
                                        db.updateDetailRoomWithFlagContent(orderModel);
                                    } else {
                                        if (String.valueOf(s).length() > 0) {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI)), name, "cild");
                                            db.insertRoomsDetail(orderModel);
                                        } else {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI)), name, "cild");
                                            db.deleteDetailRoomWithFlagContent(orderModel);
                                        }
                                    }
                                }
                            });
                        }

                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params1.setMargins(30, 10, 30, 0);
                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(30, 10, 30, 40);


                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(textView, params1);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(et[count], params2);
                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

                    } else if (type.equalsIgnoreCase("formula")) {
                        TextView textView = new TextView(DinamicRoomTaskActivity.this);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(15);

                        if (count == null) {
                            count = 0;
                        } else {
                            count++;
                        }

                        et[count] = (EditText) getLayoutInflater().inflate(R.layout.edit_input_layout, null);
                        List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(String.valueOf(count));
                        valSetOne.add(required);
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));


                        et[count].setId(Integer.parseInt(idListTask));
                        et[count].setHint(placeHolder);
                        et[count].setEnabled(false);

                        et[count].setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(maxlength))});

                        String formulas = jsonArray.getJSONObject(i).getString("formula").toString();

                        if (formulas.toUpperCase().startsWith("=SELECT")) {
                            String valueRa = formulas.substring(1, formulas.length());

                            String vv[] = valueRa.split(";");
                            List<String> valFormula = new ArrayList<String>();
                            valFormula.add(dbMaster);
                            for (String vvv : vv) {
                                valFormula.add(vvv);
                            }
                            hashMapFormulas.put(Integer.parseInt(idListTask), valFormula);
                        }

                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                        if (cursorCild.getCount() > 0) {
                            et[count].setText(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                        } else {
                            /*if (!value.equalsIgnoreCase("")) {
                                et[count].setText(value);
                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, value, jsonCreateType(idListTask, type,String.valueOf(i)), name, "cild");
                                db.insertRoomsDetail(orderModel);
                            }*/
                        }


                        if ((!showButton)) {
                            et[count].setEnabled(false);
                        } else {
                            final int finalI = i;
                            et[count].addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI)));
                                    if (cEdit.getCount() > 0) {
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI)), name, "cild");
                                        db.updateDetailRoomWithFlagContent(orderModel);
                                    } else {
                                        if (String.valueOf(s).length() > 0) {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI)), name, "cild");
                                            db.insertRoomsDetail(orderModel);
                                        } else {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI)), name, "cild");
                                            db.deleteDetailRoomWithFlagContent(orderModel);
                                        }
                                    }
                                }
                            });
                        }
                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params1.setMargins(30, 10, 30, 0);
                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(30, 10, 30, 40);

                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(textView, params1);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(et[count], params2);

                        hashMap.put(Integer.parseInt(idListTask), valSetOne);
                    } else if (type.equalsIgnoreCase("call_direct")) {

                        LinearLayout btnRel2 = (LinearLayout) getLayoutInflater().inflate(R.layout.button_call_direct, null);
                        ImageButton b2 = (ImageButton) btnRel2.findViewById(R.id.btn_submit);
                        final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(5, 15, 0, 0);
                        btnRel2.setLayoutParams(params);
                        linearLayout.addView(btnRel2);

                        b2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (ActivityCompat.checkSelfPermission(DinamicRoomTaskActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    return;
                                }
                                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:+" + et[0].getText().toString())));
                            }
                        });

                        TextView textView = new TextView(DinamicRoomTaskActivity.this);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(15);
                        textView.setLayoutParams(new TableRow.LayoutParams(0));

                        if (count == null) {
                            count = 0;
                        } else {
                            count++;
                        }
                        et[count] = (EditText) getLayoutInflater().inflate(R.layout.edit_input_layout, null);

                        List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(String.valueOf(count));
                        valSetOne.add(required);
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));


                        et[count].setId(Integer.parseInt(idListTask));
                        et[count].setHint(placeHolder);
                        et[count].setSingleLine(true);
                        et[count].setEnabled(false);
                        et[count].setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                        et[count].setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(maxlength))});

                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                        if (cursorCild.getCount() > 0) {
                            et[count].setText(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                        } else {
                            if (!value.equalsIgnoreCase("")) {
                                et[count].setText(value);
                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, value, jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                                db.insertRoomsDetail(orderModel);
                            }
                        }

                        if ((!showButton)) {
                            et[count].setEnabled(false);
                        } else {
                            final int finalI1 = i;
                            et[count].addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    Intent newIntent = new Intent("bLFormulas");
                                    sendBroadcast(newIntent);

                                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI1)));
                                    if (cEdit.getCount() > 0) {
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI1)), name, "cild");
                                        db.updateDetailRoomWithFlagContent(orderModel);

                                    } else {
                                        if (String.valueOf(s).length() > 0) {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI1)), name, "cild");
                                            db.insertRoomsDetail(orderModel);
                                        } else {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI1)), name, "cild");
                                            db.deleteDetailRoomWithFlagContent(orderModel);
                                        }
                                    }
                                }
                            });
                        }

                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params1.setMargins(30, 10, 30, 0);
                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(30, 10, 30, 40);

                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(textView, params1);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(et[count], params2);

                        hashMap.put(Integer.parseInt(idListTask), valSetOne);


                    } else if (type.equalsIgnoreCase("textarea")) {

                        TextView textView = new TextView(DinamicRoomTaskActivity.this);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(15);
                        textView.setLayoutParams(new TableRow.LayoutParams(0));

                        if (count == null) {
                            count = 0;
                        } else {
                            count++;
                        }
                        et[count] = (EditText) getLayoutInflater().inflate(R.layout.edit_input_layout, null);

                        List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(String.valueOf(count));
                        valSetOne.add(required);
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));


                        et[count].setId(Integer.parseInt(idListTask));
                        et[count].setHint(placeHolder);
                        et[count].setMinLines(4);
                        et[count].setMaxLines(8);
                        et[count].setScroller(new Scroller(context));
                        et[count].setVerticalScrollBarEnabled(true);
                        et[count].setSingleLine(false);
                        et[count].setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                        et[count].setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(maxlength))});

                        if (flag.equalsIgnoreCase("2")) {
                            et[count].setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                        }

                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                        if (cursorCild.getCount() > 0) {
                            et[count].setText(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                        } else {
                            if (!value.equalsIgnoreCase("")) {
                                et[count].setText(value);

                            }

                            if (JcontentBawaan.has(name)) {
                                if (!JcontentBawaan.getString(name).equalsIgnoreCase("null")) {
                                    JSONObject values = new JSONObject(JcontentBawaan.getString(name));
                                    if (values.has("value")) {
                                        et[count].setText(values.getString("value"));
                                    }
                                }
                            }

                            if (et[count].getText().toString().length() > 0) {
                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, et[count].getText().toString(), jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                                db.insertRoomsDetail(orderModel);

                            }


                        }

                        if ((!showButton)) {
                            et[count].setEnabled(false);
                        } else {
                            final int finalI1 = i;
                            et[count].addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    Intent newIntent = new Intent("bLFormulas");
                                    sendBroadcast(newIntent);

                                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI1)));
                                    if (cEdit.getCount() > 0) {
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI1)), name, "cild");
                                        db.updateDetailRoomWithFlagContent(orderModel);

                                    } else {
                                        if (String.valueOf(s).length() > 0) {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI1)), name, "cild");
                                            db.insertRoomsDetail(orderModel);
                                        } else {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI1)), name, "cild");
                                            db.deleteDetailRoomWithFlagContent(orderModel);
                                        }
                                    }
                                }
                            });
                        }

                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params1.setMargins(30, 10, 30, 0);
                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(30, 10, 30, 40);

                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(textView, params1);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(et[count], params2);

                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

                    } else if (type.equalsIgnoreCase("text_info")) {

                        if (count == null) {
                            count = 0;
                        } else {
                            count++;
                        }

                        TextView textView = new TextView(DinamicRoomTaskActivity.this);
                        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(20);
                        textView.setLayoutParams(new TableRow.LayoutParams(0));

                        List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(String.valueOf(count));
                        valSetOne.add(required);
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));

                        TextView et;
                        et = (TextView) getLayoutInflater().inflate(R.layout.text_view_layout, null);
                        et.setScroller(new Scroller(context));
                        et.setVerticalScrollBarEnabled(true);
                        et.setSingleLine(false);
                        et.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                        et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(maxlength))});
                        et.setText(value);
                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                        if (cursorCild.getCount() > 0) {
                            et.setText(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                        }

                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params1.setMargins(30, 10, 30, 0);
                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(30, 10, 30, 40);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));

                        linearLayout.addView(textView, params1);
                        if ((value.length() != 0) || (!value.equalsIgnoreCase(""))) {
                            valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                            linearLayout.addView(et, params2);
                        }


                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

                    } else if (type.equalsIgnoreCase("date") || type.equalsIgnoreCase("time")) {

                        if (count == null) {
                            count = 0;
                        } else {
                            count++;
                        }
                        tp[count] = (TextView) getLayoutInflater().inflate(R.layout.text_view_layout, null);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        tp[count].setText(Html.fromHtml(label));
                        tp[count].setTextSize(15);
                        tp[count].setLayoutParams(new TableRow.LayoutParams(0));


                        List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(String.valueOf(count));
                        valSetOne.add(required);
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));

                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params1.setMargins(30, 10, 30, 0);
                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(30, 10, 30, 40);

                        linearEstimasi[count] = (LinearLayout) getLayoutInflater().inflate(R.layout.date_layout_form, null);
                        linearEstimasi[count].setLayoutParams(params2);

                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(tp[count], params1);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(linearEstimasi[count]);

                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

                        final ImageButton btnOption = (ImageButton) linearEstimasi[count].findViewById(R.id.btn_browse);
                        if (type.equalsIgnoreCase("time")) {
                            btnOption.setImageResource(R.drawable.ic_time);
                        }
                        final TextView valueFile = (TextView) linearEstimasi[count].findViewById(R.id.value);

                        String hasilDariDB = "";
                        if (JcontentBawaan.has(name)) {
                            if (!JcontentBawaan.getString(name).equalsIgnoreCase("null")) {
                                JSONObject values = new JSONObject(JcontentBawaan.getString(name));
                                if (values.has("value")) {
                                    hasilDariDB = values.getString("value");
                                }
                            }
                        }

                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                        if (cursorCild.getCount() > 0) {
                            hasilDariDB = cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));
                        } else {
                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, hasilDariDB, jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                            db.insertRoomsDetail(orderModel);
                        }

                        if (hasilDariDB.length() > 0) {
                            valueFile.setText(hasilDariDB);
                        } else {
                            if (!value.equalsIgnoreCase("")) {
                                valueFile.setText(value);
                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, value, jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                                db.insertRoomsDetail(orderModel);
                            }
                        }

                        final String formula = jsonArray.getJSONObject(i).getString("formula").toString();

                        final int finalI2 = i;
                        String finalLabel2 = label;
                        String finalLabel3 = flag.equalsIgnoreCase("null") ? "" : flag;
                        btnOption.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (type.equalsIgnoreCase("time")) {
                                    TimeDialog timeDialog = new TimeDialog(DinamicRoomTaskActivity.this, finalLabel2);
                                    timeDialog.setListener(new TimeDialog.MyTimeDialogListener() {
                                        @Override
                                        public void userSelectedAValue(String value) {
                                            Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI2)));
                                            if (cEdit.getCount() == 0) {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, value, jsonCreateType(idListTask, type, String.valueOf(finalI2)), name, "cild");
                                                db.insertRoomsDetail(orderModel);
                                            } else {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, value, jsonCreateType(idListTask, type, String.valueOf(finalI2)), name, "cild");
                                                db.updateDetailRoomWithFlagContent(orderModel);
                                            }
                                            valueFile.setText(value);

                                        }

                                        @Override
                                        public void userCanceled() {
                                            Toast.makeText(getBaseContext(), "Canceled", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    timeDialog.show();

                                } else {
                                    if (finalLabel3.length() > 0) {
                                        dummyFlagDate = finalLabel3;
                                        dummyFormulaDate = formula;

                                    }

                                    dummyCalendar = Calendar.getInstance();
                                    dummyIdDate = Integer.parseInt(idListTask);

                                    mYear = dummyCalendar.get(Calendar.YEAR);
                                    mMonth = dummyCalendar.get(Calendar.MONTH);
                                    mDay = dummyCalendar.get(Calendar.DAY_OF_MONTH);

                                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI2)));
                                    if (cEdit.getCount() == 0) {
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(idListTask, type, String.valueOf(finalI2)), name, "cild");
                                        db.insertRoomsDetail(orderModel);
                                    }

                                    showDialog(Integer.valueOf(String.valueOf(DATE_DIALOG_ID) + finalLabel3));
                                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                                }
                            }
                        });

                    } else if (type.equalsIgnoreCase("phone_number")) {
                        TextView textView = new TextView(DinamicRoomTaskActivity.this);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(15);

                        if (count == null) {
                            count = 0;
                        } else {
                            count++;
                        }

                        et[count] = (EditText) getLayoutInflater().inflate(R.layout.edit_input_layout, null);

                        List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(String.valueOf(count));
                        valSetOne.add(required);
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));

                        et[count].setId(Integer.parseInt(idListTask));
                        et[count].setHint(placeHolder);
                        et[count].setInputType(InputType.TYPE_CLASS_NUMBER);
                        et[count].setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt("15"))});

                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                        if (cursorCild.getCount() > 0) {
                            et[count].setText(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                        } else {
                            if (!value.equalsIgnoreCase("")) {
                                et[count].setText(value);

                            }

                            if (JcontentBawaan.has(name)) {
                                if (!JcontentBawaan.getString(name).equalsIgnoreCase("null")) {
                                    JSONObject values = new JSONObject(JcontentBawaan.getString(name));
                                    if (values.has("value")) {
                                        et[count].setText(values.getString("value"));
                                    }
                                }
                            }

                            if (et[count].getText().toString().length() > 0) {
                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, et[count].getText().toString(), jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                                db.insertRoomsDetail(orderModel);

                            }
                        }

                        if ((!showButton)) {
                            et[count].setEnabled(false);
                        } else {
                            final int finalI3 = i;

                            et[count].addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {

                                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI3)));
                                    if (cEdit.getCount() > 0) {
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI3)), name, "cild");
                                        db.updateDetailRoomWithFlagContent(orderModel);
                                    } else {
                                        if (String.valueOf(s).length() > 0) {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI3)), name, "cild");
                                            db.insertRoomsDetail(orderModel);

                                        } else {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI3)), name, "cild");
                                            db.deleteDetailRoomWithFlagContent(orderModel);
                                        }
                                        Intent newIntent = new Intent("bLFormulas");
                                        sendBroadcast(newIntent);
                                    }
                                }
                            });
                        }
                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params1.setMargins(30, 10, 30, 0);
                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(30, 10, 30, 40);

                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(textView, params1);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(et[count], params2);

                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

                    } else if (type.equalsIgnoreCase("number")) {

                        TextView textView = new TextView(DinamicRoomTaskActivity.this);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(15);

                        if (count == null) {
                            count = 0;
                        } else {
                            count++;
                        }

                        et[count] = (EditText) getLayoutInflater().inflate(R.layout.edit_input_layout, null);

                        List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(String.valueOf(count));
                        valSetOne.add(required);
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));

                        et[count].setId(Integer.parseInt(idListTask));
                        et[count].setHint(placeHolder);
                        et[count].setInputType(InputType.TYPE_CLASS_NUMBER);
                        et[count].setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(maxlength))});


                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                        if (cursorCild.getCount() > 0) {
                            et[count].setText(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                        } else {
                            if (!value.equalsIgnoreCase("")) {
                                et[count].setText(value);
                            } else {
                                Cursor cursorValue = db.getSingleRoomDetailFormWithFlag(idDetail, username, idTab, "value");
                                if (cursorValue.getCount() > 0) {
                                    String valUEParent = "";
                                    final String contentValue = cursorValue.getString(cursorValue.getColumnIndexOrThrow(BotListDB.ROOM_CONTENT));
                                    JSONArray jsonArrayYes = null;
                                    try {
                                        jsonArrayYes = new JSONArray(contentValue);
                                        for (int ii = (jsonArrayYes.length() - 1); ii >= 0; ii--) {

                                            JSONArray magic = new JSONArray(jsonArrayYes.getJSONArray(ii).toString());
                                            JSONObject oContent2 = new JSONObject(magic.get(1).toString());
                                            JSONArray joContent = oContent2.getJSONArray("value_detail");
                                            for (int iff = 0; iff < joContent.length(); iff++) {
                                                String idValue = joContent.getJSONObject(iff).getString("id").toString();
                                                String pareen = jsonArray.getJSONObject(i).getString("copy_from").toString();
                                                if (idValue.equalsIgnoreCase(pareen)) {
                                                    valUEParent = joContent.getJSONObject(iff).getString("value").toString();
                                                }
                                            }
                                        }
                                    } catch (Exception c) {

                                    }

                                    et[count].setText(valUEParent);
                                }
                            }

                            if (JcontentBawaan.has(name)) {
                                if (!JcontentBawaan.getString(name).equalsIgnoreCase("null")) {
                                    JSONObject values = new JSONObject(JcontentBawaan.getString(name));
                                    if (values.has("value")) {
                                        et[count].setText(values.getString("value"));
                                    }
                                }
                            }

                            if (et[count].getText().toString().length() > 0) {
                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, et[count].getText().toString(), jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                                db.insertRoomsDetail(orderModel);

                            }
                        }

                        if ((!showButton)) {
                            et[count].setEnabled(false);
                        } else {
                            final int finalI3 = i;

                            et[count].addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI3)));
                                    if (cEdit.getCount() > 0) {
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI3)), name, "cild");
                                        db.updateDetailRoomWithFlagContent(orderModel);
                                    } else {
                                        if (String.valueOf(s).length() > 0) {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI3)), name, "cild");
                                            db.insertRoomsDetail(orderModel);

                                        } else {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI3)), name, "cild");
                                            db.deleteDetailRoomWithFlagContent(orderModel);
                                        }
                                        Intent newIntent = new Intent("bLFormulas");
                                        sendBroadcast(newIntent);
                                    }
                                }
                            });
                        }
                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params1.setMargins(30, 10, 30, 0);
                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(30, 10, 30, 40);

                        if (!idTab.equalsIgnoreCase("2644")) {
                            valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                            linearLayout.addView(textView, params1);
                            valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                            linearLayout.addView(et[count], params2);


                            hashMap.put(Integer.parseInt(idListTask), valSetOne);
                        }


                    } else if (type.equalsIgnoreCase("currency")) {

                        TextView textView = new TextView(DinamicRoomTaskActivity.this);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(15);

                        if (count == null) {
                            count = 0;
                        } else {
                            count++;
                        }

                        et[count] = (EditText) getLayoutInflater().inflate(R.layout.edit_input_layout, null);

                        List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(String.valueOf(count));
                        valSetOne.add(required);
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));


                        et[count].setId(Integer.parseInt(idListTask));
                        et[count].setHint(placeHolder);
                        et[count].setInputType(InputType.TYPE_CLASS_NUMBER);
                        et[count].setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(maxlength))});


                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                        if (cursorCild.getCount() > 0) {
                            et[count].setText(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                        } else {
                            if (!value.equalsIgnoreCase("")) {
                                et[count].setText(value);

                            }

                            if (JcontentBawaan.has(name)) {
                                if (!JcontentBawaan.getString(name).equalsIgnoreCase("null")) {
                                    JSONObject values = new JSONObject(JcontentBawaan.getString(name));
                                    if (values.has("value")) {
                                        et[count].setText(values.getString("value"));
                                    }
                                }
                            }

                            if (et[count].getText().toString().length() > 0) {
                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, et[count].getText().toString(), jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                                db.insertRoomsDetail(orderModel);

                            }
                        }

                        if ((!showButton)) {
                            et[count].setEnabled(false);
                        } else {
                            final int finalI3 = i;

                            et[count].addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    List value = (List) hashMap.get(Integer.parseInt(idListTask));

                                    et[Integer.valueOf(value.get(0).toString())].removeTextChangedListener(this);

                                    try {

                                        String originalString = s.toString();

                                        Long longval;
                                        if (originalString.contains(",")) {
                                            originalString = originalString.replaceAll(",", "");
                                        }
                                        longval = Long.parseLong(originalString);

                                        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                                        formatter.applyPattern("#,###,###,###");
                                        String formattedString = formatter.format(longval);

                                        //setting text after format to EditText
                                        et[Integer.valueOf(value.get(0).toString())].setText(formattedString);
                                        et[Integer.valueOf(value.get(0).toString())].setSelection(et[Integer.valueOf(value.get(0).toString())].getText().length());
                                        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI3)));
                                        if (cEdit.getCount() > 0) {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(formattedString), jsonCreateType(idListTask, type, String.valueOf(finalI3)), name, "cild");
                                            db.updateDetailRoomWithFlagContent(orderModel);
                                        } else {
                                            if (String.valueOf(s).length() > 0) {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(formattedString), jsonCreateType(idListTask, type, String.valueOf(finalI3)), name, "cild");
                                                db.insertRoomsDetail(orderModel);
                                            } else {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(formattedString), jsonCreateType(idListTask, type, String.valueOf(finalI3)), name, "cild");
                                                db.deleteDetailRoomWithFlagContent(orderModel);
                                            }
                                        }
                                        Intent newIntent = new Intent("bLFormulas");
                                        sendBroadcast(newIntent);

                                    } catch (NumberFormatException nfe) {
                                        nfe.printStackTrace();
                                    }

                                    et[Integer.valueOf(value.get(0).toString())].addTextChangedListener(this);

                                }
                            });
                        }
                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params1.setMargins(30, 10, 30, 0);
                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(30, 10, 30, 40);

                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(textView, params1);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(et[count], params2);


                        hashMap.put(Integer.parseInt(idListTask), valSetOne);
                    } else if (type.equalsIgnoreCase("rear_camera") || type.equalsIgnoreCase("front_camera")) {
                        TextView textView = new TextView(DinamicRoomTaskActivity.this);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(15);

                        if (count == null) {
                            count = 0;
                        } else {
                            count++;
                        }

                        List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(String.valueOf(count));
                        valSetOne.add(required);
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));


                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(30, 10, 30, 0);
                        textView.setLayoutParams(params2);
                        linearLayout.addView(textView);


                        imageView[count] = (ImageView) getLayoutInflater().inflate(R.layout.image_view_frame, null);
                        int width = getWindowManager().getDefaultDisplay().getWidth();
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, width / 2);
                        params.setMargins(5, 15, 0, 0);
                        imageView[count].setLayoutParams(params);
                        params.addRule(RelativeLayout.CENTER_IN_PARENT);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(imageView[count], params);

                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                        if (cursorCild.getCount() > 0) {

                            if (Message.isJSONValid(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)))) {
                                JSONObject jObject = null;
                                try {
                                    jObject = new JSONObject(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                if (jObject != null) {
                                    try {
                                        String a = jObject.getString("a");
                                        imageView[count].setImageBitmap(decodeBase64(a));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                imageView[count].setImageBitmap(decodeBase64(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT))));
                            }
                        }


                        hashMap.put(Integer.parseInt(idListTask), valSetOne);


                        final int finalI4 = i;
                        imageView[count].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI4)));
                                if (cursorCild.getCount() > 0) {
                                    Intent intent = new Intent(context, ZoomImageViewActivity.class);
                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE, ZoomImageViewActivity.FROM);
                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE_BASE_A, idDetail);
                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE_BASE_B, username);
                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE_BASE_C, idTab);
                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE_BASE_D, "cild");
                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE_BASE_E, jsonCreateType(idListTask, type, String.valueOf(finalI4)));
                                    startActivity(intent);
                                } else {
                                    int facing = 0;
                                    if (type.equalsIgnoreCase("front_camera")) {
                                        facing = 1;
                                    }
                                    captureGalery(idDetail, username, idTab, idListTask, type, name, flag, facing, String.valueOf(finalI4));
                                }

                            }
                        });

                        final String finalLabel = label;
                        final int finalI5 = i;
                        imageView[count].setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                                        DinamicRoomTaskActivity.this);
                                builderSingle.setTitle("Select an action " + Html.fromHtml(finalLabel));
                                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                        DinamicRoomTaskActivity.this,
                                        android.R.layout.simple_list_item_1);


                                final Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI5)));
                                if (cursorCild.getCount() > 0) {
                                    arrayAdapter.add("Retake");
                                    arrayAdapter.add("Delete");
                                    arrayAdapter.add("View");
                                } else {
                                    arrayAdapter.add("Capture");
                                }
                                builderSingle.setAdapter(arrayAdapter,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String listName = arrayAdapter.getItem(which);
                                                if (listName.equalsIgnoreCase("Retake") || listName.equalsIgnoreCase("Capture")) {
                                                    int facing = 0;
                                                    if (type.equalsIgnoreCase("front_camera")) {
                                                        facing = 1;
                                                    }
                                                    captureGalery(idDetail, username, idTab, idListTask, type, name, flag, facing, String.valueOf(finalI5));
                                                } else if (listName.equalsIgnoreCase("Delete")) {
                                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(idListTask, type, String.valueOf(finalI5)), name, "cild");

                                                    db.deleteDetailRoomWithFlagContent(orderModel);
                                                    finish();
                                                    Intent aa = getIntent();
                                                    aa.putExtra("idTask", idDetail);
                                                    startActivity(getIntent());
                                                } else if (listName.equalsIgnoreCase("View")) {
                                                    Intent intent = new Intent(context, ZoomImageViewActivity.class);
                                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE, ZoomImageViewActivity.FROM);
                                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE_BASE_A, idDetail);
                                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE_BASE_B, username);
                                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE_BASE_C, idTab);
                                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE_BASE_D, "cild");
                                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE_BASE_E, jsonCreateType(idListTask, type, String.valueOf(finalI5)));
                                                    startActivity(intent);
                                                }
                                            }
                                        });
                                builderSingle.show();
                                return true;
                            }
                        });
                    } else if (type.equalsIgnoreCase("ocr_ktp")) {
                        TextView textView = new TextView(DinamicRoomTaskActivity.this);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(15);


                        if (count == null) {
                            count = 0;
                        } else {
                            count++;
                        }

                        final List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(String.valueOf(count));
                        valSetOne.add(required);
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));

                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(30, 10, 30, 0);
                        textView.setLayoutParams(params2);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(textView);

                        imageView[count] = (ImageView) getLayoutInflater().inflate(R.layout.image_view_frame, null);
                        imageView[count].setImageDrawable(getResources().getDrawable(R.drawable.ico_camera_reader));
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(5, 15, 0, 0);
                        imageView[count].setLayoutParams(params);
                        params.addRule(RelativeLayout.CENTER_IN_PARENT);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(imageView[count], params);

                        et[count] = (EditText) getLayoutInflater().inflate(R.layout.edit_input_layout, null);


                        et[count].setId(Integer.parseInt(idListTask));
                        et[count].setHint("NIK");
                        et[count].setInputType(InputType.TYPE_CLASS_NUMBER);
                        et[count].setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(maxlength))});

                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                        if (cursorCild.getCount() > 0) {
                            et[count].setText(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                        } else {
                            if (!value.equalsIgnoreCase("")) {
                                et[count].setText(value);
                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, value, jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                                db.insertRoomsDetail(orderModel);
                            } else {
                                Cursor cursorValue = db.getSingleRoomDetailFormWithFlag(idDetail, username, idTab, "value");
                                if (cursorValue.getCount() > 0) {
                                    String valUEParent = "";
                                    final String contentValue = cursorValue.getString(cursorValue.getColumnIndexOrThrow(BotListDB.ROOM_CONTENT));
                                    JSONArray jsonArrayYes = null;
                                    try {
                                        jsonArrayYes = new JSONArray(contentValue);
                                        for (int ii = (jsonArrayYes.length() - 1); ii >= 0; ii--) {
                                            JSONArray magic = new JSONArray(jsonArrayYes.getJSONArray(ii).toString());
                                            JSONObject oContent2 = new JSONObject(magic.get(1).toString());
                                            JSONArray joContent = oContent2.getJSONArray("value_detail");
                                            for (int iff = 0; iff < joContent.length(); iff++) {
                                                final String idValue = joContent.getJSONObject(iff).getString("id").toString();
                                                String pareen = jsonArray.getJSONObject(i).getString("copy_from").toString();
                                                if (idValue.equalsIgnoreCase(pareen)) {
                                                    valUEParent = joContent.getJSONObject(iff).getString("value").toString();
                                                }
                                            }
                                        }
                                    } catch (Exception c) {

                                    }

                                    et[count].setText(valUEParent);
                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, value, jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                                    db.insertRoomsDetail(orderModel);
                                }
                            }
                        }

                        if ((!showButton)) {
                            et[count].setEnabled(false);
                        } else {
                            final int finalI3 = i;

                            et[count].addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI3)));
                                    if (cEdit.getCount() > 0) {
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI3)), name, "cild");
                                        db.updateDetailRoomWithFlagContent(orderModel);
                                    } else {
                                        if (String.valueOf(s).length() > 0) {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI3)), name, "cild");
                                            db.insertRoomsDetail(orderModel);

                                        } else {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI3)), name, "cild");
                                            db.deleteDetailRoomWithFlagContent(orderModel);
                                        }
                                        Intent newIntent = new Intent("bLFormulas");
                                        sendBroadcast(newIntent);
                                    }

                                }
                            });
                        }

                        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params3.setMargins(30, 10, 30, 10);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(et[count], params3);


                        if (jsonArray.getJSONObject(i).getString("ocr_ktp") != null) {

                            imageView[count].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dummyIdDate = Integer.parseInt(idListTask);

                                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                        return;
                                    }
                                    CameraActivity.Builder start = new CameraActivity.Builder(activity, CAMERA_SCAN_TEXT);
                                    start.setLockSwitch(CameraActivity.UNLOCK_SWITCH_CAMERA);
                                    start.setCameraFace(CameraActivity.CAMERA_REAR);
                                    start.setFlashMode(CameraActivity.FLASH_OFF);
                                    start.setQuality(CameraActivity.MEDIUM);
                                    start.setRatio(CameraActivity.RATIO_4_3);
                                    start.setFileName(new MediaProcessingUtil().createFileName("jpeg", "ROOM"));
                                    new Camera(start.build()).lauchCamera();
                                }
                            });

                            String isi = jsonArray.getJSONObject(i).getString("ocr_ktp").toString();

                            hashMapOcrKTP.put(Integer.parseInt(idListTask), isi);
                        }


                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

                    } else if (type.equalsIgnoreCase("ocr")) {
                        TextView textView = new TextView(DinamicRoomTaskActivity.this);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(15);


                        if (count == null) {
                            count = 0;
                        } else {
                            count++;
                        }

                        final List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(String.valueOf(count));
                        valSetOne.add(required);
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));

                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(30, 10, 30, 0);
                        textView.setLayoutParams(params2);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(textView);

                        imageView[count] = (ImageView) getLayoutInflater().inflate(R.layout.image_view_frame, null);
                        imageView[count].setImageDrawable(getResources().getDrawable(R.drawable.ico_camera_reader));
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(5, 15, 0, 0);
                        imageView[count].setLayoutParams(params);
                        params.addRule(RelativeLayout.CENTER_IN_PARENT);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(imageView[count], params);

                        RelativeLayout child;
                        if (jsonArray.getJSONObject(i).getString("ocr") != null) {

                            imageView[count].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dummyIdDate = Integer.parseInt(idListTask);
                                    Intent i = new Intent(context, ReaderOcr.class);
                                    startActivityForResult(i, OCR_REQUEST);

                                }
                            });

                            String isi = jsonArray.getJSONObject(i).getString("ocr").toString();
                            JSONArray jsonArrays = new JSONArray(isi);
                            for (int ia = 0; ia < jsonArrays.length(); ia++) {
                                String ll = jsonArrays.getJSONObject(ia).getString("label").toString();
                                final String nn = jsonArrays.getJSONObject(ia).getString("name").toString();
                                linearEstimasi[count] = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_child, null);
                                child = (RelativeLayout) getLayoutInflater().inflate(R.layout.item_cild_ocr, null);
                                ImageButton btnOption = (ImageButton) child.findViewById(R.id.btnOption);
                                ImageButton btnCancel = (ImageButton) child.findViewById(R.id.btnCancel);
                                final TextView namePickup = (TextView) child.findViewById(R.id.namePickup);
                                namePickup.setText(ll);
                                final EditText valuePickup = (EditText) child.findViewById(R.id.valuePickup);


                                final Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                                if (cursorCild.getCount() > 0) {
                                    valuePickup.setText(jsonResultType(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)), nn) != null ? jsonResultType(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)), nn) : "");
                                }


                                final int finalI25 = i;
                                valuePickup.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI25)));
                                        if (cEdit.getCount() > 0) {
                                            JSONObject jsonObj = null;
                                            try {
                                                JSONObject jsonObject = new JSONObject(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, function(jsonObject, nn, s.toString()).toString(), jsonCreateType(idListTask, type, String.valueOf(finalI25)), name, "cild");
                                                db.updateDetailRoomWithFlagContent(orderModel);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        } else {
                                            if (String.valueOf(s).length() > 0) {
                                                JSONObject jsonObj = null;
                                                try {
                                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, function(null, nn, s.toString()).toString(), jsonCreateType(idListTask, type, String.valueOf(finalI25)), name, "cild");
                                                    db.insertRoomsDetail(orderModel);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }

                                    }
                                });

                                btnCancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final AlertDialog.Builder alertbox = new AlertDialog.Builder(DinamicRoomTaskActivity.this);
                                        alertbox.setMessage("Are you sure clear " + namePickup.getText() + "?");
                                        alertbox.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                valuePickup.setText("");
                                            }
                                        });
                                        alertbox.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface arg0, int arg1) {
                                            }
                                        });
                                        alertbox.show();

                                    }
                                });

                                btnOption.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                                                DinamicRoomTaskActivity.this);
                                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                                DinamicRoomTaskActivity.this,
                                                android.R.layout.simple_list_item_1);
                                        List value = (List) hashMap.get(Integer.parseInt(idListTask));
                                        List valueOcr = (List) hashMapOcr.get(Integer.valueOf(value.get(0).toString()));
                                        if (valueOcr != null) {
                                            for (Object element : valueOcr) {
                                                if (element != null) {
                                                    if (element.toString().trim().length() > 0)
                                                        arrayAdapter.add(element.toString().trim());
                                                }
                                            }
                                        }
                                        builderSingle.setAdapter(arrayAdapter,
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        String listName = arrayAdapter.getItem(which);
                                                        String space = "";
                                                        if (valuePickup.getText().toString().length() > 0) {
                                                            space = "\n";
                                                        }
                                                        valuePickup.setText(valuePickup.getText() + space + listName);
                                                        valuePickup.setSelection(valuePickup.getText().length());
                                                    }
                                                });
                                        builderSingle.show();
                                    }
                                });
                                child.setLayoutParams(params2);
                                valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                                linearLayout.addView(linearEstimasi[count]);
                                valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                                linearLayout.addView(child);
                            }
                        }

                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

                    } else if (type.equalsIgnoreCase("upload_document")) {
                        TextView textView = new TextView(DinamicRoomTaskActivity.this);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(15);


                        if (count == null) {
                            count = 0;
                        } else {
                            count++;
                        }


                        final List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(String.valueOf(count));
                        valSetOne.add(required);
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));

                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(30, 10, 30, 0);
                        textView.setLayoutParams(params2);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(textView);

                        linearEstimasi[count] = (LinearLayout) getLayoutInflater().inflate(R.layout.upload_doc_layout, null);
                        linearEstimasi[count].setLayoutParams(params2);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(linearEstimasi[count]);

                        final Button btnOption = (Button) linearEstimasi[count].findViewById(R.id.btn_browse);
                        final TextView valueFile = (TextView) linearEstimasi[count].findViewById(R.id.value);

                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                        if (cursorCild.getCount() > 0) {
                            valueFile.setText(jsonResultType(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)), "a"));
                        }


                        final int finalI25 = i;
                        btnOption.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dummyIdDate = Integer.parseInt(idListTask);

                                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI25)));
                                if (cEdit.getCount() == 0) {
                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(idListTask, type, String.valueOf(finalI25)), name, "cild");
                                    db.insertRoomsDetail(orderModel);
                                }


                                Intent intent = new Intent();
                                intent.setType("file/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Choose File to Upload.."), PICK_FILE_REQUEST);

                            }
                        });

                        hashMap.put(Integer.parseInt(idListTask), valSetOne);


                    } else if (type.equalsIgnoreCase("signature")) {
                        TextView textView = new TextView(DinamicRoomTaskActivity.this);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(15);


                        if (count == null) {
                            count = 0;
                        } else {
                            count++;
                        }


                        final List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(String.valueOf(count));//0
                        final int finalI26 = i;


                        valSetOne.add(required);//3
                        valSetOne.add(type);//4
                        valSetOne.add(name);//5
                        valSetOne.add(label);//6
                        valSetOne.add(String.valueOf(i));//7

                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(30, 10, 30, 0);
                        textView.setLayoutParams(params2);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));//1
                        linearLayout.addView(textView);

                        imageView[count] = (ImageView) getLayoutInflater().inflate(R.layout.frame_signature_form_black, null);
                        imageView[count].setImageDrawable(getResources().getDrawable(R.drawable.ico_signature));
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(5, 15, 0, 0);
                        imageView[count].setLayoutParams(params);
                        params.addRule(RelativeLayout.CENTER_IN_PARENT);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));//2
                        linearLayout.addView(imageView[count], params);

                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

                        if (JcontentBawaan.has(name)) {
                            if (!JcontentBawaan.getString(name).equalsIgnoreCase("null")) {
                                JSONObject values = new JSONObject(JcontentBawaan.getString(name));
                                if (values.has("value")) {
                                    if (required.equalsIgnoreCase("1")) {
                                        required = "0";
                                    }
                                    //,username
                                    Picasso.with(context).load(new ValidationsKey().getInstance(activity).getTargetUrl(username) + "/bc_voucher_client/images/list_task/signature/" + values.getString("value")).into(imageView[count]);
                                }
                            }
                        }

                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                        if (cursorCild.getCount() > 0) {
                            //  imageView[count] = (ImageView) getLayoutInflater().inflate(R.layout.frame_signature_form, null);
                            imageView[count].setImageBitmap(decodeBase64(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT))));
                        }


                        imageView[count].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dummyIdDate = Integer.parseInt(idListTask);

                                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI26)));
                                if (cEdit.getCount() == 0) {
                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(idListTask, type, String.valueOf(finalI26)), name, "cild");
                                    db.insertRoomsDetail(orderModel);
                                }

                                Intent intent = new Intent(context, CaptureSignature.class);
                                startActivityForResult(intent, SIGNATURE_ACTIVITY);

                            }
                        });


                    } else if (type.equalsIgnoreCase("distance_estimation")) {
                        TextView textView = new TextView(DinamicRoomTaskActivity.this);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(15);


                        if (count == null) {
                            count = 0;
                        } else {
                            count++;
                        }

                        final List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(String.valueOf(count));
                        valSetOne.add(required);
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));


                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(30, 10, 30, 0);
                        params3.setMargins(30, 10, 30, 30);
                        textView.setLayoutParams(params2);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(textView);

                        linearEstimasi[count] = (LinearLayout) getLayoutInflater().inflate(R.layout.estimation_layout, null);
                        linearEstimasi[count].setLayoutParams(params2);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(linearEstimasi[count]);

                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                        if (cursorCild.getCount() > 0) {
                            TextView start = (TextView) linearEstimasi[count].findViewById(R.id.valuePickup);
                            TextView end = (TextView) linearEstimasi[count].findViewById(R.id.valueEnd);
                            TextView jarak = (TextView) linearEstimasi[count].findViewById(R.id.valueJarak);


                            String[] latlongS = jsonResultType(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)), "s").split(
                                    Message.LOCATION_DELIMITER);
                            if (latlongS.length > 4) {
                                String text = "<u><b>" + (String) latlongS[2] + "</b></u><br/>";
                                start.setText(Html.fromHtml(text + latlongS[3]));
                            }

                            String[] latlongE = jsonResultType(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)), "e").split(
                                    Message.LOCATION_DELIMITER);
                            if (latlongE.length > 4) {
                                String text = "<u><b>" + (String) latlongE[2] + "</b></u><br/>";
                                end.setText(Html.fromHtml(text + latlongE[3]));
                            }
                            jarak.setText(jsonResultType(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)), "d"));
                        }


                        final int finalI27 = i;
                        linearEstimasi[count].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dummyIdDate = Integer.parseInt(idListTask);

                                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI27)));
                                if (cEdit.getCount() == 0) {
                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(idListTask, type, String.valueOf(finalI27)), name, "cild");
                                    db.insertRoomsDetail(orderModel);
                                }

                                Intent i = new Intent(context, ActivityDirection.class);
                                i.putExtra("id", idListTask);
                                startActivityForResult(i, PICK_ESTIMATION);
                            }
                        });

                    } else if (type.equalsIgnoreCase("rate")) {
                        TextView textView = new TextView(DinamicRoomTaskActivity.this);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(15);
                        final List<String> valSetOne = new ArrayList<String>();

                        if (count == null) {
                            count = 0;
                        } else {
                            count++;
                        }

                        valSetOne.add(String.valueOf(count));
                        valSetOne.add(required);
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));

                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(30, 10, 30, 0);
                        textView.setLayoutParams(params2);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(textView);

                        rat[count] = (RatingBar) getLayoutInflater().inflate(R.layout.costume_rating, null);
                        final LinearLayout.LayoutParams testLP = new LinearLayout.LayoutParams(
                                AppBarLayout.LayoutParams.WRAP_CONTENT, AppBarLayout.LayoutParams.MATCH_PARENT);
                        testLP.setMargins(10, 20, 10, 10);
                        testLP.gravity = Gravity.CENTER_HORIZONTAL;
                        rat[count].setLayoutParams(testLP);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(rat[count]);

                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                        if (cursorCild.getCount() > 0) {
                            rat[count].setRating(Float.parseFloat(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT))));
                        } else {
                            if (!value.equalsIgnoreCase("")) {
                                rat[count].setRating(Integer.parseInt(value));
                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, value, jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                                db.insertRoomsDetail(orderModel);
                            }
                        }
                        final int finalI24 = i;

                        if ((!showButton)) {
                            rat[count].setEnabled(false);
                        } else {
                            rat[count].setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                                @Override
                                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                                    List valueList = (List) hashMap.get(Integer.parseInt(idListTask));
                                    String conten = String.valueOf(rat[Integer.valueOf(valueList.get(0).toString())].getRating());

                                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI24)));
                                    if (cEdit.getCount() > 0) {
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, conten, jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "cild");
                                        db.updateDetailRoomWithFlagContent(orderModel);
                                    } else {
                                        if (conten.length() > 0) {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, conten, jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "cild");
                                            db.insertRoomsDetail(orderModel);
                                        } else {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, conten, jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "cild");
                                            db.deleteDetailRoomWithFlagContent(orderModel);
                                        }
                                    }

                                    Intent newIntent = new Intent("bLFormulas");
                                    sendBroadcast(newIntent);

                                }
                            });

                        }

                    } else if (type.equalsIgnoreCase("map")) {

                        TextView textView = new TextView(DinamicRoomTaskActivity.this);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(15);

                        if (count == null) {
                            count = 0;
                        } else {

                            count++;
                        }

                        et[count] = (EditText) getLayoutInflater().inflate(R.layout.edit_input_layout, null);
                        et[count].setId(Integer.parseInt(idListTask));
                        et[count].setHint(placeHolder);

                        List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(String.valueOf(count));
                        valSetOne.add(required);
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));

                        et[count].setFocusable(false);
                        et[count].setFocusableInTouchMode(false); // user touches widget on phone with touch screen

                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                        if (cursorCild.getCount() > 0) {
                            String[] latlong = cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).split(
                                    Message.LOCATION_DELIMITER);
                            if (latlong.length > 4) {
                                String text = "<u><b>" + (String) latlong[2] + "</b></u><br/>";
                                et[count].setText(Html.fromHtml(text + latlong[3]));
                            }
                        }


                        final String finalLabel1 = label;
                        final int finalI6 = i;
                        et[count].setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(final View view, MotionEvent motionEvent) {
                                if (motionEvent.getAction() == MotionEvent.ACTION_UP && showDialog) {
                                    showDialog = false;

                                    final Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI6)));

                                    if (cursorCild.getCount() > 0) {
                                        AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                                                DinamicRoomTaskActivity.this);
                                        builderSingle.setTitle("Select an action " + Html.fromHtml(finalLabel1));
                                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                                DinamicRoomTaskActivity.this,
                                                android.R.layout.simple_list_item_1);
                                        if ((showButton)) {
                                            arrayAdapter.add("Retake");
                                            arrayAdapter.add("Delete");
                                        }
                                        arrayAdapter.add("View");

                                        builderSingle.setAdapter(arrayAdapter,
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        String listName = arrayAdapter.getItem(which);
                                                        if (listName.equalsIgnoreCase("Retake")) {
                                                            requestLocationInfo(idDetail, username, idTab, idListTask, type, name, String.valueOf(finalI6));
                                                        } else if (listName.equalsIgnoreCase("Delete")) {
                                                            showDialog = true;
                                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(idListTask, type, String.valueOf(finalI6)), name, "cild");

                                                            db.deleteDetailRoomWithFlagContent(orderModel);
                                                            finish();
                                                            Intent aa = getIntent();
                                                            aa.putExtra("idTask", idDetail);
                                                            startActivity(getIntent());
                                                        } else if (listName.equalsIgnoreCase("View")) {
                                                            showDialog = true;
                                                            String[] latlong = cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).split(
                                                                    Message.LOCATION_DELIMITER);

                                                            if (latlong.length > 3) {
                                                                Uri gmmIntentUri = null;
                                                                if (latlong[3].equalsIgnoreCase("")) {
                                                                    gmmIntentUri = Uri.parse("geo:0,0?q=" + Double
                                                                            .parseDouble(latlong[0]) + ","
                                                                            + Double.parseDouble(latlong[1]) + "(" + "You" + ")");
                                                                } else {
                                                                    String loc = latlong[2] + "+,+" + latlong[3];
                                                                    gmmIntentUri = Uri.parse("geo:0,0?q=" + Double.parseDouble(latlong[0]) + ","
                                                                            + Double.parseDouble(latlong[1]) + "(" + loc + ")");
                                                                }

                                                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                                                mapIntent.setPackage("com.google.android.apps.maps");
                                                                startActivity(mapIntent);
                                                            }

                                                        }
                                                    }
                                                });
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                            builderSingle.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                @Override
                                                public void onDismiss(DialogInterface dialog) {
                                                    showDialog = true;
                                                }
                                            });
                                        }
                                        builderSingle.setCancelable(false);
                                        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                showDialog = true;
                                            }
                                        });
                                        builderSingle.show();
                                        return true;
                                    } else {
                                        if ((showButton)) {
                                            requestLocationInfo(idDetail, username, idTab, idListTask, type, name, String.valueOf(finalI6));
                                        }

                                    }
                                }

                                return false;
                            }

                        });

                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params1.setMargins(30, 10, 30, 0);
                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(30, 10, 30, 40);


                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(textView, params1);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(et[count], params2);


                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

                    } else if (type.equalsIgnoreCase("video")) {
                        TextView textView = new TextView(DinamicRoomTaskActivity.this);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(15);

                        if (count == null) {
                            count = 0;
                        } else {
                            count++;
                        }


                        List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(String.valueOf(count));
                        valSetOne.add(required);
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));

                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(5, 15, 0, 0);
                        textView.setLayoutParams(params2);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(textView);

                        imageView[count] = new ImageView(this);
                        imageView[count] = (ImageView) getLayoutInflater().inflate(R.layout.image_view_frame, null);

                        int width = getWindowManager().getDefaultDisplay().getWidth();
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, width * 50 / 100);
                        params.setMargins(5, 15, 0, 0);
                        imageView[count].setLayoutParams(params);
                        params.addRule(RelativeLayout.CENTER_IN_PARENT);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(imageView[count], params);

                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));

                        if (cursorCild.getCount() > 0) {
                            Bitmap thumb2 = BitmapFactory.decodeResource(getResources(), R.drawable.play_button);
                            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)), MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
                            imageView[count].setImageBitmap(overlay(thumb, thumb2));
                        }

                        final int finalI4 = i;

                        imageView[count].setOnClickListener(new View.OnClickListener() {
                            @SuppressLint("MissingPermission")
                            @Override
                            public void onClick(View view) {
                                dummyIdDate = Integer.parseInt(idListTask);
                                gps = new GPSTracker(DinamicRoomTaskActivity.this);
                                if (!gps.canGetLocation()) {
                                    gps.showSettingsAlert();
                                } else {
                                    final Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI4)));
                                    if (cursorCild.getCount() > 0) {
                                        Uri vv = Uri.parse(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                                        File myFile = new File(vv.toString());
                                        myFile.getAbsolutePath();

                                        if (myFile.exists()) {
                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.setDataAndType(vv, "video/*");
                                            startActivity(intent);
                                        }

                                    } else {
                                        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI4)));
                                        if (cEdit.getCount() == 0) {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(idListTask, type, String.valueOf(finalI4)), name, "cild");
                                            db.insertRoomsDetail(orderModel);
                                        }
                                        String action = Intent.ACTION_GET_CONTENT;
                                        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                            return;
                                        }
                                        AnncaConfiguration.Builder photo = new AnncaConfiguration.Builder(activity, REQ_VIDEO);
                                        photo.setMediaAction(AnncaConfiguration.MEDIA_ACTION_VIDEO);
                                        photo.setMediaQuality(AnncaConfiguration.MEDIA_QUALITY_MEDIUM);
                                        photo.setMediaResultBehaviour(AnncaConfiguration.PREVIEW);
                                        new Annca(photo.build()).launchCamera();
                                    }
                                }
                            }
                        });

                        final String finalLabel = label;
                        final int finalI5 = i;
                        imageView[count].setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                dummyIdDate = Integer.parseInt(idListTask);
                                AlertDialog.Builder builderSingle = new AlertDialog.Builder(DinamicRoomTaskActivity.this);
                                builderSingle.setTitle("Select an action " + Html.fromHtml(finalLabel));

                                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(DinamicRoomTaskActivity.this, android.R.layout.simple_list_item_1);
                                final Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI5)));

                                if (cursorCild.getCount() > 0) {
                                    arrayAdapter.add("Retake");
                                    arrayAdapter.add("Delete");
                                    arrayAdapter.add("View");
                                } else {
                                    arrayAdapter.add("Record");
                                }
                                builderSingle.setAdapter(arrayAdapter,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String listName = arrayAdapter.getItem(which);
                                                if (listName.equalsIgnoreCase("Retake") || listName.equalsIgnoreCase("Record")) {

                                                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI5)));
                                                    if (cEdit.getCount() == 0) {
                                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(idListTask, type, String.valueOf(finalI5)), name, "cild");
                                                        db.insertRoomsDetail(orderModel);
                                                    }
                                                    String action = Intent.ACTION_GET_CONTENT;
                                                    /*videoblm ada
                                                    Intent cf = new Intent(getApplicationContext(), VideoActivity.class);
                                                    cf.putExtra("res", "Medium");
                                                    File f = MediaProcessingUtil.getOutputFile(".mp4");
                                                    cf.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                                                    cf.setAction(action);
                                                    startActivityForResult(cf, REQ_VIDEO);*/
                                                } else if (listName.equalsIgnoreCase("Delete")) {

                                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(idListTask, type, String.valueOf(finalI5)), name, "cild");
                                                    db.deleteDetailRoomWithFlagContent(orderModel);


                                                    finish();
                                                    Intent aa = getIntent();
                                                    aa.putExtra("idTask", idDetail);
                                                    startActivity(getIntent());
                                                } else if (listName.equalsIgnoreCase("View")) {

                                                    Uri vv = Uri.parse(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                                                    File myFile = new File(vv.toString());
                                                    myFile.getAbsolutePath();

                                                    if (myFile.exists()) {
                                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                                        intent.setDataAndType(vv, "video/*");
                                                        startActivity(intent);
                                                    }
                                                }
                                            }
                                        });
                                builderSingle.show();
                                return true;
                            }
                        });

                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

                    } else if (type.equalsIgnoreCase("dropdown_form")) {
                        Log.w("happy:1:>", idListTask);
                        if (idListTask.equalsIgnoreCase("66989")) {
                            Log.w("happy:2:>", "noew");
                            TextView textView = new TextView(DinamicRoomTaskActivity.this);
                            if (required.equalsIgnoreCase("1")) {
                                label += "<font size=\"3\" color=\"red\">*</font>";
                            }
                            textView.setText(Html.fromHtml(label));
                            textView.setTextSize(15);
                            textView.setLayoutParams(new TableRow.LayoutParams(0));

                            if (count == null) {
                                count = 0;
                            } else {
                                count++;
                            }

                            List<String> valSetOne = new ArrayList<String>();
                            valSetOne.add(String.valueOf(count));
                            valSetOne.add(required);
                            valSetOne.add(type);
                            valSetOne.add(name);
                            valSetOne.add(label);
                            valSetOne.add(String.valueOf(i));

                            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params1.setMargins(30, 10, 30, 0);
                            valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                            linearLayout.addView(textView, params1);

                            idListTaskMasterForm = idListTask;

                            linearEstimasi[count] = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_child, null);
                            final JSONObject jObject = new JSONObject(value);
                            String url = jObject.getString("url");
                            DataBaseDropDown mDB = null;

                            String[] aa = url.split("/");
                            String nama = aa[aa.length - 1].toString();
                            if (!nama.contains(".")) {
                                if (!dbMaster.equalsIgnoreCase("")) {
                                    String[] aaBB = dbMaster.split("/");
                                    nama = aaBB[aaBB.length - 1].toString();
                                }
                            }

                            final int finalI5 = i;
                            mDB = new DataBaseDropDown(context, nama.substring(0, nama.indexOf(".")));
                            try {


                                final HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();
                                final List<String> expandableListTitle = new ArrayList<String>();

                                if (mDB.getWritableDatabase() != null) {
                                    String table = jsonArray.getJSONObject(i).getString("formula").toString();
                                    if (table != null) {

                                        List<String> valFormula = new ArrayList<String>();
                                        valFormula.add(nama.substring(0, nama.indexOf(".")));
                                        valFormula.add(table);
                                        hashMapDropForm.put(Integer.parseInt(idListTask), valFormula);

                                        RelativeLayout relativeLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.expandable_listview, null);
                                        expandableListView[count] = (ExpandableListView) relativeLayout.findViewById(R.id.expandableView);


                                        final ExpandableListAdapter expandableListAdapter = new ExpandableListAdapter(this, getApplicationContext(), expandableListTitle, expandableListDetail, idDetail, username, idTab, jsonCreateType(idListTask, type, String.valueOf(finalI5)), name);
                                        expandableListView[count].setAdapter(expandableListAdapter);
                                        expandableListView[count].setGroupIndicator(null);

                                        setListViewHeightBasedOnChildren(expandableListView[count]);

                                        expandableListView[count].setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

                                            @Override
                                            public boolean onGroupClick(ExpandableListView parent, View v,
                                                                        int groupPosition, long id) {

                                                setListViewHeight(parent, groupPosition);

                                                return false;
                                            }
                                        });

                                        TableRow.LayoutParams params2 = new TableRow.LayoutParams(1);
                                        params2.setMargins(40, 10, 10, 0);
                                        relativeLayout.setLayoutParams(params2);

                                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                                        linearLayout.addView(relativeLayout);

                                    }


                                } else {
                                    if (deleteContent) {
                                        db.deleteRoomsDetailbyId(idDetail, idTab, username);
                                    }
                                    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                                        Toast.makeText(context, "Please insert memmory card", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                    finish();
                                    Intent intent = new Intent(context, DownloadSqliteDinamicActivity.class);
                                    intent.putExtra("name_db", nama.substring(0, nama.indexOf(".")));
                                    intent.putExtra("path_db", url);
                                    startActivity(intent);
                                    return;
                                }
                            } catch (Exception e) {

                            }

                            TableRow.LayoutParams params2 = new TableRow.LayoutParams(1);
                            params2.setMargins(60, 10, 30, 0);
                            linearEstimasi[count].setLayoutParams(params2);
                            valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                            linearLayout.addView(linearEstimasi[count]);

                            hashMap.put(Integer.parseInt(idListTask), valSetOne);

                            if (hashMapDropForm.size() > 0) {
                                customersId = "BNDSH";

                                Iterator it = hashMapDropForm.entrySet().iterator();
                                while (it.hasNext()) {
                                    Map.Entry pair = (Map.Entry) it.next();
                                    List valuess = (List) hashMapDropForm.get(pair.getKey());
                                    String DBmaster = valuess.get(0).toString();

                                    String Formulamaster = valuess.get(1).toString();

                                    JSONObject jObjectFormula = null;
                                    try {
                                        List<String> expandableListTitle = new ArrayList<String>();
                                        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

                                        HashMap<String, List<JSONObject>> expandableListDetailJSONObject = new HashMap<String, List<JSONObject>>();
                                        List<String> expandableListTitleJSON = new ArrayList<String>();

                                        jObjectFormula = new JSONObject(Formulamaster);

                                        String data = jObjectFormula.getString("data");
                                        JSONObject jObjectFormula2 = new JSONObject(data);
                                        JSONArray jsonArraySelect = jObjectFormula2.getJSONArray("select");

                                        String aass[] = new String[jsonArraySelect.length()];

                                        for (int ia = 0; ia < jsonArraySelect.length(); ia++) {
                                            String ll = jsonArraySelect.getString(ia);
                                            aass[ia] = ll;
                                        }

                                        String from = jObjectFormula2.getString("from");
                                        String where = "jas.kode = 'BNDSH'";//jObjectFormula2.getString("where");

                                        String defaultValue = "";
                                        List valueForms = (List) hashMap.get(pair.getKey());

                                        DataBaseDropDown mDBss = new DataBaseDropDown(context, DBmaster);
                                        if (mDBss.getWritableDatabase() != null) {

                                            final Cursor css = mDBss.getWritableDatabase().query(true, from, aass, where, null, null, null, null, null);
                                            if (css.moveToFirst()) {
                                                String titleOld = "";
                                                List<String> Item = null;

                                                List<JSONObject> Items = null;
                                                int t = -1;
                                                do {
                                                    String title = css.getString(2);
                                                    String titleS = String.valueOf(css.getInt(4));
                                                    if (!titleOld.equalsIgnoreCase(title)) {
                                                        Item = new ArrayList<String>();
                                                        Items = new ArrayList<JSONObject>();
                                                        titleOld = title;
                                                        expandableListTitle.add(title);
                                                        expandableListTitleJSON.add(titleS);
                                                        t++;
                                                    }
                                                    Integer column0 = css.getInt(0);
                                                    Integer column3 = css.getInt(4);
                                                    String column4 = css.getString(5);

                                                    JSONObject obj = new JSONObject();
                                                    JSONObject objS = new JSONObject();
                                                    try {
                                                        obj.put("t", column4);
                                                        obj.put("iT", String.valueOf(column3));
                                                        obj.put("iD", String.valueOf(css.getInt(0)) + "|" + String.valueOf(css.getInt(1) + "|" + String.valueOf(css.getInt(4))));

                                                        objS.put("iD", String.valueOf(css.getInt(0)) + "|" + String.valueOf(css.getInt(1) + "|" + String.valueOf(css.getInt(4))));
                                                        objS.put("v", "");
                                                        objS.put("n", "");

                                                    } catch (JSONException e) {
                                                        // TODO Auto-generated catch block e.printStackTrace();
                                                    }
                                                    Item.add(obj.toString());
                                                    Items.add(objS);

                                                    expandableListDetail.put(title, Item);
                                                    expandableListDetailJSONObject.put(titleS, Items);


                                                } while (css.moveToNext());

                                                JSONObject jsonObject = new JSONObject();
                                                for (String title : expandableListTitleJSON) {
                                                    List<JSONObject> ala = expandableListDetailJSONObject.get(title);
                                                    JSONArray JsArray = new JSONArray();
                                                    for (JSONObject aha : ala) {
                                                        JsArray.put(aha);
                                                    }
                                                    jsonObject.put(title, JsArray);
                                                }
                                                jsonObject.put("customersId", "BNDSH");

                                                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(String.valueOf(pair.getKey()), valueForms.get(2).toString(), valueForms.get(5).toString()));
                                                if (cEdit.getCount() == 0) {
                                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, jsonObject.toString(), jsonCreateType(String.valueOf(pair.getKey()), valueForms.get(2).toString(), valueForms.get(5).toString()), valueForms.get(3).toString(), "cild");
                                                    db.insertRoomsDetail(orderModel);
                                                } else {
                                                    String text = cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));
                                                    JSONObject lala = new JSONObject(text);
                                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, jsonObject.toString(), jsonCreateType(String.valueOf(pair.getKey()), valueForms.get(2).toString(), valueForms.get(5).toString()), valueForms.get(3).toString(), "cild");
                                                    db.updateDetailRoomWithFlagContent(orderModel);
                                                }
                                            }
                                            css.close();
                                        } else {

                                        }

                                        ExpandableListAdapter listAdapter = new ExpandableListAdapter(activity, getApplicationContext(), expandableListTitle, expandableListDetail, idDetail, username, idTab, jsonCreateType(String.valueOf(pair.getKey()), valueForms.get(2).toString(), valueForms.get(5).toString()), valueForms.get(3).toString());
                                        expandableListView[Integer.valueOf(valueForms.get(0).toString())].setAdapter(listAdapter);
                                        setListViewHeightBasedOnChildren(expandableListView[Integer.valueOf(valueForms.get(0).toString())]);

                                    } catch (Exception e) {
                                        Log.d("InputStream", e.getLocalizedMessage());
                                    }
                                }
                            }

                        } else {
                            TextView textView = new TextView(DinamicRoomTaskActivity.this);
                            if (required.equalsIgnoreCase("1")) {
                                label += "<font size=\"3\" color=\"red\">*</font>";
                            }
                            textView.setText(Html.fromHtml(label));
                            textView.setTextSize(15);
                            textView.setLayoutParams(new TableRow.LayoutParams(0));

                            if (count == null) {
                                count = 0;
                            } else {
                                count++;
                            }

                            List<String> valSetOne = new ArrayList<String>();
                            valSetOne.add(String.valueOf(count));
                            valSetOne.add(required);
                            valSetOne.add(type);
                            valSetOne.add(name);
                            valSetOne.add(label);
                            valSetOne.add(String.valueOf(i));

                            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params1.setMargins(30, 10, 30, 0);
                            valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                            linearLayout.addView(textView, params1);

                            idListTaskMasterForm = idListTask;

                            linearEstimasi[count] = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_child, null);
                            final JSONObject jObject = new JSONObject(value);
                            String url = jObject.getString("url");
                            DataBaseDropDown mDB = null;

                            String[] aa = url.split("/");
                            String nama = aa[aa.length - 1].toString();
                            if (!nama.contains(".")) {
                                if (!dbMaster.equalsIgnoreCase("")) {
                                    String[] aaBB = dbMaster.split("/");
                                    nama = aaBB[aaBB.length - 1].toString();
                                }
                            }

                            final int finalI5 = i;
                            mDB = new DataBaseDropDown(context, nama.substring(0, nama.indexOf(".")));
                            try {


                                final HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();
                                final List<String> expandableListTitle = new ArrayList<String>();

                                if (mDB.getWritableDatabase() != null) {
                                    String table = jsonArray.getJSONObject(i).getString("formula").toString();
                                    if (table != null) {

                                        List<String> valFormula = new ArrayList<String>();
                                        valFormula.add(nama.substring(0, nama.indexOf(".")));
                                        valFormula.add(table);
                                        hashMapDropForm.put(Integer.parseInt(idListTask), valFormula);

                                        RelativeLayout relativeLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.expandable_listview, null);
                                        expandableListView[count] = (ExpandableListView) relativeLayout.findViewById(R.id.expandableView);


                                        final ExpandableListAdapter expandableListAdapter = new ExpandableListAdapter(this, getApplicationContext(), expandableListTitle, expandableListDetail, idDetail, username, idTab, jsonCreateType(idListTask, type, String.valueOf(finalI5)), name);
                                        expandableListView[count].setAdapter(expandableListAdapter);
                                        expandableListView[count].setGroupIndicator(null);

                                        setListViewHeightBasedOnChildren(expandableListView[count]);

                                        expandableListView[count].setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

                                            @Override
                                            public boolean onGroupClick(ExpandableListView parent, View v,
                                                                        int groupPosition, long id) {

                                                setListViewHeight(parent, groupPosition);

                                                return false;
                                            }
                                        });

                                        TableRow.LayoutParams params2 = new TableRow.LayoutParams(1);
                                        params2.setMargins(40, 10, 10, 0);
                                        relativeLayout.setLayoutParams(params2);

                                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                                        linearLayout.addView(relativeLayout);

                                    }

                                } else {
                                    if (deleteContent) {
                                        db.deleteRoomsDetailbyId(idDetail, idTab, username);
                                    }
                                    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                                        Toast.makeText(context, "Please insert memmory card", Toast.LENGTH_LONG).show();
                                        finish();
                                    }

                                    finish();
                                    Intent intent = new Intent(context, DownloadSqliteDinamicActivity.class);
                                    intent.putExtra("name_db", nama.substring(0, nama.indexOf(".")));
                                    intent.putExtra("path_db", url);
                                    startActivity(intent);
                                    return;
                                }
                            } catch (Exception e) {

                            }

                            TableRow.LayoutParams params2 = new TableRow.LayoutParams(1);
                            params2.setMargins(60, 10, 30, 0);
                            linearEstimasi[count].setLayoutParams(params2);
                            valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                            linearLayout.addView(linearEstimasi[count]);

                            hashMap.put(Integer.parseInt(idListTask), valSetOne);
                        }


                    } else if (type.equalsIgnoreCase("load_dropdown_k")) {

                        // TODO: 3/8/19 untuk pilihan dari api , blm bisa auto select saat back save 

                        if (count == null) {
                            count = 0;
                        } else {
                            count++;
                        }

                        TextView textView = new TextView(DinamicRoomTaskActivity.this);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(15);
                        textView.setLayoutParams(new TableRow.LayoutParams(0));

                        TableRow.LayoutParams params2 = new TableRow.LayoutParams(1);

                        String downloadForm = jsonArray.getJSONObject(i).getString("formula");

                        valuesKnjngnOne.add("--Please Select--");

                        String downloadValue = jsonArray.getJSONObject(i).getString("value");

                        if (downloadValue.length() > 0 && downloadValue.split(";").length > 0) {
                            String hasilLoop[] = downloadValue.split(";");
                            for (String asa : hasilLoop) {
                                valuesKnjngnOne.add(asa);
                            }
                        }


                        MessengerDatabaseHelper messengerHelper = null;
                        if (messengerHelper == null) {
                            messengerHelper = MessengerDatabaseHelper.getInstance(context);
                        }

                        Contact contact = messengerHelper.getMyContact();

                        String bcUser = contact.getJabberId();

                        SearchableSpinner spinner = new SearchableSpinner(this);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            spinner.setBackground(getResources().getDrawable(R.drawable.spinner_background));
                        }

                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, valuesKnjngnOne);
                        new getKnjngnJson(downloadForm, spinnerArrayAdapter).execute(bcUser);

                        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(spinnerArrayAdapter);
                        params2.setMargins(30, 10, 30, 40);
                        spinner.setLayoutParams(params2);

                        SearchableSpinner spinner2 = new SearchableSpinner(this);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            spinner2.setBackground(getResources().getDrawable(R.drawable.spinner_background));
                        }
                        spinner2.setLayoutParams(params2);
                        spinner2.setVisibility(View.INVISIBLE);

                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                        if (cursorCild.getCount() > 0) {
                            int spinnerPosition = spinnerArrayAdapter.getPosition(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                            spinner.setSelection(spinnerPosition);
                        } else {
                            if (spinner.getSelectedItem() != null) {
                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, spinner.getSelectedItem().toString(), jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                                db.insertRoomsDetail(orderModel);
                            }

                        }


                        if ((!showButton)) {
                            spinner.setEnabled(false);
                        } else {
                            final int finalI7 = i;
                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                @Override
                                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int myPosition, long myID) {

                                    if (myPosition > 3) {
                                        String awalAn = spinnerArrayAdapter.getItem(myPosition);
                                        valuesKnjngnTwo = kunjunganList.get(myPosition - 4).getDaleman();
                                        ArrayAdapter<String> spinnerArrayAdapter2 = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_spinner_item_black, valuesKnjngnTwo);
                                        spinnerArrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        spinner2.setAdapter(spinnerArrayAdapter2);
                                        spinnerArrayAdapter2.notifyDataSetChanged();
                                        spinner2.setVisibility(View.VISIBLE);
                                        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI7)));
                                                if (cEdit.getCount() > 0) {
                                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, awalAn + " - " + spinnerArrayAdapter2.getItem(position), jsonCreateType(idListTask, type, String.valueOf(finalI7)), name, "cild");
                                                    db.updateDetailRoomWithFlagContent(orderModel);
                                                } else {
                                                    if (spinnerArrayAdapter.getItem(myPosition).length() > 0) {
                                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, awalAn + " - " + spinnerArrayAdapter2.getItem(position), jsonCreateType(idListTask, type, String.valueOf(finalI7)), name, "cild");
                                                        db.insertRoomsDetail(orderModel);
                                                    } else {
                                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, awalAn + " - " + spinnerArrayAdapter2.getItem(position), jsonCreateType(idListTask, type, String.valueOf(finalI7)), name, "cild");
                                                        db.deleteDetailRoomWithFlagContent(orderModel);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> parent) {

                                            }
                                        });
                                    } else {
                                        spinner2.setVisibility(View.INVISIBLE);
                                        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI7)));
                                        if (cEdit.getCount() > 0) {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, spinnerArrayAdapter.getItem(myPosition), jsonCreateType(idListTask, type, String.valueOf(finalI7)), name, "cild");
                                            db.updateDetailRoomWithFlagContent(orderModel);
                                        } else {
                                            if (spinnerArrayAdapter.getItem(myPosition).length() > 0) {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, spinnerArrayAdapter.getItem(myPosition), jsonCreateType(idListTask, type, String.valueOf(finalI7)), name, "cild");
                                                db.insertRoomsDetail(orderModel);
                                            } else {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, spinnerArrayAdapter.getItem(myPosition), jsonCreateType(idListTask, type, String.valueOf(finalI7)), name, "cild");
                                                db.deleteDetailRoomWithFlagContent(orderModel);
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parentView) {
                                }

                            });
                        }

                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params1.setMargins(30, 10, 30, 0);

                        LinearLayout.LayoutParams params12 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params12.setMargins(50, 10, 30, 0);

                        List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add("");
                        valSetOne.add(required);
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));

                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(textView, params1);

                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(spinner, params1);

                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(spinner2, params1);

                        View view = new View(this);
                        view.setVisibility(View.INVISIBLE);

                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(view, params2);

                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

                    } else if (type.equalsIgnoreCase("load_dropdown")) {

                        if (count == null) {
                            count = 0;
                        } else {
                            count++;
                        }

                        TextView textView = new TextView(DinamicRoomTaskActivity.this);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(15);
                        textView.setLayoutParams(new TableRow.LayoutParams(0));

                        TableRow.LayoutParams params2 = new TableRow.LayoutParams(1);

                        String downloadForm = jsonArray.getJSONObject(i).getString("formula").toString();
                        final ArrayList<String> spinnerArray = new ArrayList<String>();
                        spinnerArray.add("-");

                        SearchableSpinner spinner = new SearchableSpinner(this);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            spinner.setBackground(getResources().getDrawable(R.drawable.spinner_background));
                        }
                        MessengerDatabaseHelper messengerHelper = null;
                        if (messengerHelper == null) {
                            messengerHelper = MessengerDatabaseHelper.getInstance(context);
                        }

                        Contact contact = messengerHelper.getMyContact();

                        SpinnerCustomAdapter spinnerArrayAdapter = new SpinnerCustomAdapter(this, R.layout.simple_spinner_item_black, downloadForm, contact.getJabberId(), spinnerArray);
                        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(spinnerArrayAdapter);
                        params2.setMargins(30, 10, 30, 40);
                        spinner.setLayoutParams(params2);


                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                        if (cursorCild.getCount() > 0) {
                            int spinnerPosition = spinnerArrayAdapter.getPosition(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                            spinner.setSelection(spinnerPosition);
                        } else {
                            if (spinner.getSelectedItem() != null) {
                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, spinner.getSelectedItem().toString(), jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                                db.insertRoomsDetail(orderModel);
                            }

                        }


                        if ((!showButton)) {
                            spinner.setEnabled(false);
                        } else {
                            final int finalI7 = i;
                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                @Override
                                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int myPosition, long myID) {

/*
                                    HashMap<String, ArrayList<String>> hashMapL = newDropdownViews.get(Integer.parseInt(idListTask));
                                    ArrayList<String> udah = new ArrayList<>();
                                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI7)));
                                    if (cEdit.getCount() > 0) {
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(spinnerArray.get(myPosition)), jsonCreateType(idListTask, type, String.valueOf(finalI7)), name, "cild");
                                        db.updateDetailRoomWithFlagContent(orderModel);
                                    } else {
                                        if (String.valueOf(spinnerArray.get(myPosition)).length() > 0) {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(spinnerArray.get(myPosition)), jsonCreateType(idListTask, type, String.valueOf(finalI7)), name, "cild");
                                            db.insertRoomsDetail(orderModel);
                                        } else {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(spinnerArray.get(myPosition)), jsonCreateType(idListTask, type, String.valueOf(finalI7)), name, "cild");
                                            db.deleteDetailRoomWithFlagContent(orderModel);
                                        }
                                    }
*/

                                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI7)));
                                    if (cEdit.getCount() > 0) {
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(spinnerArrayAdapter.getValues().get(myPosition)), jsonCreateType(idListTask, type, String.valueOf(finalI7)), name, "cild");
                                        db.updateDetailRoomWithFlagContent(orderModel);
                                    } else {
                                        if (String.valueOf(spinnerArrayAdapter.getValues().get(myPosition)).length() > 0) {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(spinnerArrayAdapter.getValues().get(myPosition)), jsonCreateType(idListTask, type, String.valueOf(finalI7)), name, "cild");
                                            db.insertRoomsDetail(orderModel);
                                        } else {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(spinnerArrayAdapter.getValues().get(myPosition)), jsonCreateType(idListTask, type, String.valueOf(finalI7)), name, "cild");
                                            db.deleteDetailRoomWithFlagContent(orderModel);
                                        }
                                    }

                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parentView) {
                                }

                            });
                        }
                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params1.setMargins(30, 10, 30, 0);

                        LinearLayout.LayoutParams params12 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params12.setMargins(50, 10, 30, 0);

                        List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add("");
                        valSetOne.add(required);
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));

                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(textView, params1);

                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(spinner, params1);
                        View view = new View(this);
                        view.setVisibility(View.INVISIBLE);

                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(view, params2);

                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

                    } else if (type.equalsIgnoreCase("new_dropdown_dinamis")) {

                        banyakDropdown.add("1");

                        String foro = jsonArray.getJSONObject(i).getString("formula").toString();

                        JSONObject jObjects = null;
                        try {
                            jObjects = new JSONObject(foro);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        if (count == null) {
                            count = 0;
                        } else {
                            count++;
                        }

                        List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(String.valueOf(count));
                        valSetOne.add(required);
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));


                        if (jObjects != null) {
                            Log.w("mcD", "sini");
                            TextView textView = new TextView(DinamicRoomTaskActivity.this);
                            if (required.equalsIgnoreCase("1")) {
                                label += "<font size=\"3\" color=\"red\">*</font>";
                            }
                            textView.setText(Html.fromHtml(label));
                            textView.setTextSize(15);
                            textView.setLayoutParams(new TableRow.LayoutParams(0));

                            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params1.setMargins(30, 10, 30, 0);
                            valSetOne.add(String.valueOf(linearLayout.getChildCount()));

                            linearLayout.addView(textView, params1);

                            linearEstimasi[count] = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_child, null);
                            final JSONObject jObject = new JSONObject(value);
                            String url = jObject.getString("url");
                            String table = jsonArray.getJSONObject(i).getString("formula").toString();

                            final ArrayList<String> kolom = new ArrayList<>();
                            ArrayList<String> title = new ArrayList<>();

                            JSONArray jsonData = jObject.getJSONArray("data");
                            try {
                                for (int ii = 0; ii < jsonData.length(); ii++) {
                                    JSONObject oContent = new JSONObject(jsonData.getString(ii));
                                    kolom.add(oContent.getString("value").toString());
                                    title.add(oContent.getString("title").toString());
                                }
                            } catch (Exception e) {

                            }

                            DataBaseDropDown mDB = null;

                            String[] aa = url.split("/");
                            String nama = aa[aa.length - 1].toString();
                            if (!nama.contains(".")) {
                                if (!dbMaster.equalsIgnoreCase("")) {
                                    String[] aaBB = dbMaster.split("/");
                                    nama = aaBB[aaBB.length - 1].toString();
                                }
                            }

                            List<String> valFormula = new ArrayList<String>();
                            valFormula.add(nama.substring(0, nama.indexOf(".")));
                            valFormula.add(table);
                            valFormula.add(title.get(0).toString());
                            hashMapDropNew.put(Integer.parseInt(idListTask), valFormula);

                            mDB = new DataBaseDropDown(context, nama.substring(0, nama.indexOf(".")));
                            try {
                                if (mDB.getWritableDatabase() != null) {

                                    linearEstimasi[count].removeAllViews();

                                    String[] columnNames = new String[kolom.size()];
                                    columnNames = kolom.toArray(columnNames);
                                    String[] titleNames = new String[title.size()];
                                    titleNames = title.toArray(titleNames);


                                    JSONObject jObjectFormula = new JSONObject(table);
                                    String data = jObjectFormula.getString("data");
                                    JSONObject jObjectFormula2 = new JSONObject(data);
                                    JSONArray jsonArraySelect = jObjectFormula2.getJSONArray("select");

                                    String aass[] = new String[jsonArraySelect.length()];

                                    for (int ia = 0; ia < jsonArraySelect.length(); ia++) {
                                        String ll = jsonArraySelect.getString(ia);
                                        aass[ia] = ll;
                                    }

                                    String q = jObjectFormula2.getString("from");
                                    String qw = jObjectFormula2.getString("where");


                                    final Cursor c = mDB.getWritableDatabase().query(true, q, aass, qw, new String[]{customersId}, null, null, null, null);


                                    HashMap<String, String> hashMapss = new HashMap<>();
                                    final ArrayList<String> spinnerArray = new ArrayList<String>();
                                    if (c.getCount() > 1) {
                                        spinnerArray.add("--Please Select--");
                                    }

                                    if (c.moveToFirst()) {
                                        do {
                                            String column1 = c.getString(0);
                                            if (aass.length > 1) {
                                                String column2 = c.getString(1);
                                                hashMapss.put(column1, column2);
                                            }
                                            spinnerArray.add(column1);
                                        } while (c.moveToNext());
                                    }
                                    c.close();


                                    if (hashMapss.size() > 0) {
                                        outerMap.put(count, hashMapss);
                                    }


                                    final LinearLayout spinerTitle = (LinearLayout) getLayoutInflater().inflate(R.layout.item_spiner_textview, null);
                                    TextView textViewFirst = (TextView) spinerTitle.findViewById(R.id.title);

                                    final String titlesss = title.get(0).toString();
                                    textViewFirst.setText(Html.fromHtml(titlesss));
                                    textViewFirst.setTextSize(15);

                                    newSpinner[count] = (SearchableSpinner) spinerTitle.findViewById(R.id.spinner);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        newSpinner[count].setBackground(getResources().getDrawable(R.drawable.spinner_background));
                                    }
                                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item_black, spinnerArray); //selected item will look like a spinner set from XML
                                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    newSpinner[count].setAdapter(spinnerArrayAdapter);

                                    final int finalI24 = i;

                                    Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                                    if (cursorCild.getCount() > 0) {


                                        try {
                                            JSONObject jsonObject = new JSONObject(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));

                                            String titl = jsonObject.getString(titlesss).split("\n\nKode")[0];

                                            int spinnerPosition = spinnerArrayAdapter.getPosition(titl);
                                            if (spinnerPosition < 0) {
                                                spinnerPosition = spinnerArrayAdapter.getPosition("--Add--");
                                            }

                                            newSpinner[count].setSelection(spinnerPosition);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    if (hashMapss.size() > 0) {

                                        newSpinner[count].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                                List value = (List) hashMap.get(Integer.parseInt(idListTask));

                                                final TextView tes = (TextView) spinerTitle.findViewById(R.id.lastSpinner);
                                                final TextView titleKode = (TextView) spinerTitle.findViewById(R.id.titleKode);

                                                if (!newSpinner[Integer.valueOf(value.get(0).toString())].getSelectedItem().toString().equalsIgnoreCase("--Please Select--")) {
                                                    String values = ((HashMap<String, String>) outerMap.get(Integer.valueOf(value.get(0).toString()))).get(newSpinner[Integer.valueOf(value.get(0).toString())].getSelectedItem().toString()).toString();
                                                    tes.setVisibility(View.VISIBLE);
                                                    titleKode.setVisibility(View.VISIBLE);
                                                    titleKode.setText("Kode Unit");
                                                    tes.setText(values);
                                                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI24)));

                                                    if (cEdit.getCount() > 0) {
                                                        try {
                                                            JSONObject jsonObject = new JSONObject(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, function(jsonObject, titlesss, newSpinner[Integer.valueOf(value.get(0).toString())].getSelectedItem().toString() + "\n\nKode Unit =  " + values).toString(), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "cild");
                                                            db.updateDetailRoomWithFlagContent(orderModel);

                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }

                                                    } else {
                                                        RoomsDetail orderModel = null;
                                                        try {
                                                            orderModel = new RoomsDetail(idDetail, idTab, username, function(null, titlesss, newSpinner[Integer.valueOf(value.get(0).toString())].getSelectedItem().toString() + "\n\nKode Unit =  " + values).toString(), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "cild");

                                                            db.insertRoomsDetail(orderModel);

                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }

                                                } else {
                                                    tes.setVisibility(View.GONE);
                                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "cild");
                                                    db.deleteDetailRoomWithFlagContent(orderModel);

                                                }
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> parentView) {
                                                // your code here
                                            }

                                        });
                                    } else {

                                        newSpinner[count].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                                List value = (List) hashMap.get(Integer.parseInt(idListTask));

                                                final TextView tes = (TextView) spinerTitle.findViewById(R.id.lastSpinner);
                                                final TextView titleKode = (TextView) spinerTitle.findViewById(R.id.titleKode);

                                                if (!newSpinner[Integer.valueOf(value.get(0).toString())].getSelectedItem().toString().equalsIgnoreCase("--Please Select--")) {

                                                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI24)));

                                                    if (cEdit.getCount() > 0) {
                                                        try {
                                                            JSONObject jsonObject = new JSONObject(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, function(jsonObject, titlesss, newSpinner[Integer.valueOf(value.get(0).toString())].getSelectedItem().toString()).toString(), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "cild");
                                                            db.updateDetailRoomWithFlagContent(orderModel);

                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }

                                                    } else {
                                                        RoomsDetail orderModel = null;
                                                        try {
                                                            orderModel = new RoomsDetail(idDetail, idTab, username, function(null, titlesss, newSpinner[Integer.valueOf(value.get(0).toString())].getSelectedItem().toString()).toString(), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "cild");

                                                            db.insertRoomsDetail(orderModel);

                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }

                                                } else {
                                                    tes.setVisibility(View.GONE);
                                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "cild");
                                                    db.deleteDetailRoomWithFlagContent(orderModel);

                                                }
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> parentView) {
                                                // your code here
                                            }

                                        });
                                    }


                                    linearEstimasi[count].addView(spinerTitle);

                                } else {

                                    if (deleteContent) {

                                        db.deleteRoomsDetailbyId(idDetail, idTab, username);

                                    }
                                    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                                        Toast.makeText(context, "Please insert memmory card", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                    finish();

                                    Intent intent = new Intent(context, DownloadSqliteDinamicActivity.class);
                                    intent.putExtra("name_db", nama.substring(0, nama.indexOf(".")));
                                    Log.w("IMANDANU", url);
                                    intent.putExtra("path_db", url);
                                    startActivity(intent);
                                    return;
                                }
                            } catch (Exception e) {

                            }

                            TableRow.LayoutParams params2 = new TableRow.LayoutParams(1);
                            params2.setMargins(60, 10, 30, 0);
                            linearEstimasi[count].setLayoutParams(params2);
                            valSetOne.add(String.valueOf(linearLayout.getChildCount()));


                            linearLayout.addView(linearEstimasi[count]);


                        } else {
                            Log.w("maulana", "depol");
                            //biasa
                            TextView textView = new TextView(DinamicRoomTaskActivity.this);
                            if (required.equalsIgnoreCase("1")) {
                                label += "<font size=\"3\" color=\"red\">*</font>";
                            }
                            textView.setText(Html.fromHtml(label));
                            textView.setTextSize(15);
                            textView.setLayoutParams(new TableRow.LayoutParams(0));


                            tp[count] = (TextView) getLayoutInflater().inflate(R.layout.text_input_layout, null);
                            tp[count].setId(Integer.parseInt(idListTask));
                            tp[count].setHint(placeHolder);
                            tp[count].setMinLines(4);


                            tp[count].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FragmentManager fm = getSupportFragmentManager();
                                    DialogNewDropdown testDialog = new DialogNewDropdown(new DialogNewDropdown.finishListener() {
                                        @Override
                                        public void submitted(String json) {
                                            tp[count].setText(json);
                                        }
                                    });
                                    testDialog.setRetainInstance(true);
                                    testDialog.show(fm, "Dialog");
                                }
                            });

                            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params1.setMargins(30, 10, 30, 0);
                            valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                            linearLayout.addView(textView, params1);


                            TableRow.LayoutParams params2 = new TableRow.LayoutParams(1);
                            params2.setMargins(60, 10, 30, 0);
                            valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                            linearLayout.addView(tp[count], params2);
                            hashMap.put(Integer.parseInt(idListTask), valSetOne);
                        }

                        hashMap.put(Integer.parseInt(idListTask), valSetOne);


                    } else if (type.equalsIgnoreCase("dropdown_dinamis")) {


                        TextView textView = new TextView(DinamicRoomTaskActivity.this);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(15);
                        textView.setLayoutParams(new TableRow.LayoutParams(0));

                        if (count == null) {
                            count = 0;
                        } else {
                            count++;
                        }

                        List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(String.valueOf(count));
                        valSetOne.add(required);
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));
                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params1.setMargins(30, 10, 30, 0);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(textView, params1);


                        linearEstimasi[count] = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_child, null);
                        JSONObject jObject = new JSONObject(value);
                        String url = jObject.getString("url");

                        DataBaseDropDown mDB = null;

                        String[] aa = url.split("/");
                        final String nama = aa[aa.length - 1].toString();
                        if (nama.contains(".")) {
                            mDB = new DataBaseDropDown(context, nama.substring(0, nama.indexOf(".")));
                        } else {
                            if (!dbMaster.equalsIgnoreCase("")) {
                                String[] aaBB = dbMaster.split("/");
                                final String namaBB = aaBB[aaBB.length - 1].toString();
                                mDB = new DataBaseDropDown(context, namaBB.substring(0, namaBB.indexOf(".")));
                                if (mDB.getWritableDatabase() != null) {

                                } else {
                                    finish();
                                 /*   Intent intent = new Intent(context, DownloadSqliteDinamicActivity.class);
                                    intent.putExtra("name_db",nama.substring(0,nama.indexOf(".")));
                                    intent.putExtra("path_db",dbMaster);
                                    startActivity(intent);*/
                                }
                            }
                        }

                        try {
                            if (mDB.getWritableDatabase() != null) {
                                String namaTable = "";
                                Cursor c2 = mDB.selectAll();
                                if (c2.moveToFirst()) {
                                    while (!c2.isAfterLast()) {
                                        if (namaTable.equalsIgnoreCase("")) {
                                            namaTable = c2.getString(0);
                                        }
                                        c2.moveToNext();
                                    }
                                }

                                linearEstimasi[count].removeAllViews();

                                Cursor dbCursor = mDB.getWritableDatabase().query(namaTable, null, null, null, null, null, null);
                                final String[] columnNames = dbCursor.getColumnNames();
                                final Cursor c = mDB.getWritableDatabase().query(true, namaTable, new String[]{columnNames[0]}, null, null, columnNames[0], null, null, null);

                                final ArrayList<String> spinnerArray = new ArrayList<String>();
                                spinnerArray.add("--Please Select--");
                                if (c.moveToFirst()) {
                                    do {
                                        String column1 = c.getString(0);
                                        spinnerArray.add(column1);
                                    } while (c.moveToNext());
                                }
                                c.close();


                                final LinearLayout spinerTitle = (LinearLayout) getLayoutInflater().inflate(R.layout.item_spiner_textview, null);
                                TextView textViewFirst = (TextView) spinerTitle.findViewById(R.id.title);
                                JSONObject jObjectFirs = new JSONObject(value);
                                final JSONArray titleDropdown = new JSONArray(jObjectFirs.getString("title"));
                                final String titlesss = titleDropdown.get(0).toString();
                                textViewFirst.setText(Html.fromHtml(titlesss));
                                textViewFirst.setTextSize(15);

                                final SearchableSpinner spinner = (SearchableSpinner) spinerTitle.findViewById(R.id.spinner);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    spinner.setBackground(getResources().getDrawable(R.drawable.spinner_background));
                                }
                                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item_black, spinnerArray); //selected item will look like a spinner set from XML
                                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner.setAdapter(spinnerArrayAdapter);


                                Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                                if (cursorCild.getCount() > 0) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                                        int spinnerPosition = spinnerArrayAdapter.getPosition(jsonObject.getString(titlesss));
                                        spinner.setSelection(spinnerPosition);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }


                                final String finalNamaTable = namaTable;
                                final int finalI24 = i;
                                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                    @Override
                                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                        dummyIdDate = Integer.parseInt(idListTask);
                                        List nilai = (List) hashMap.get(dummyIdDate);
                                        if (!spinner.getSelectedItem().toString().equals("--Please Select--")) {
                                            if (columnNames.length > 1) {
                                                final int counts = linearEstimasi[Integer.valueOf(nilai.get(0).toString())].getChildCount();
                                                linearEstimasi[Integer.valueOf(nilai.get(0).toString())].removeViews(1, counts - 1);
                                                addSpinner(value, nama.substring(0, nama.indexOf(".")), linearEstimasi[Integer.valueOf(nilai.get(0).toString())], finalNamaTable, columnNames, 0, columnNames[0] + "= '" + spinner.getSelectedItem().toString() + "'", idListTask, type, String.valueOf(finalI24), name);

                                                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI24)));

                                                if (cEdit.getCount() > 0) {
                                                    try {
                                                        JSONObject jsonObject = new JSONObject(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, function(jsonObject, titlesss, spinner.getSelectedItem().toString()).toString(), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "cild");

                                                        db.updateDetailRoomWithFlagContent(orderModel);

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                } else {
                                                    RoomsDetail orderModel = null;
                                                    try {
                                                        orderModel = new RoomsDetail(idDetail, idTab, username, function(null, titlesss, spinner.getSelectedItem().toString()).toString(), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "cild");

                                                        db.insertRoomsDetail(orderModel);

                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                }

                                            }
                                        } else {
                                            if (columnNames.length > 1) {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, spinner.getSelectedItem().toString(), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "cild");

                                                db.deleteDetailRoomWithFlagContent(orderModel);

                                                linearEstimasi[Integer.valueOf(nilai.get(0).toString())].removeAllViews();
                                                linearEstimasi[Integer.valueOf(nilai.get(0).toString())].addView(spinerTitle);
                                            }

                                        }
                                        Intent newIntent = new Intent("bLFormulas");
                                        sendBroadcast(newIntent);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parentView) {
                                        // your code here
                                    }

                                });

                                linearEstimasi[count].addView(spinerTitle);

                            } else {
                                if (deleteContent) {

                                    db.deleteRoomsDetailbyId(idDetail, idTab, username);

                                }
                                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                                    Toast.makeText(context, "Please insert memmory card", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                finish();
                                Intent intent = new Intent(context, DownloadSqliteDinamicActivity.class);
                                intent.putExtra("name_db", nama.substring(0, nama.indexOf(".")));
                                intent.putExtra("path_db", url);
                                startActivity(intent);
                                return;
                            }
                        } catch (Exception e) {

                        }

                        TableRow.LayoutParams params2 = new TableRow.LayoutParams(1);
                        params2.setMargins(60, 10, 30, 0);
                        linearEstimasi[count].setLayoutParams(params2);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(linearEstimasi[count]);
                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

                    } else if (type.equalsIgnoreCase("dropdown")) {
                        //manual_input


                        TextView textView = new TextView(DinamicRoomTaskActivity.this);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(15);
                        textView.setLayoutParams(new TableRow.LayoutParams(0));

                        TableRow.LayoutParams params2 = new TableRow.LayoutParams(1);
                        String isi = jsonArray.getJSONObject(i).getString("dropdown").toString();
                        JSONArray jsonArrays = new JSONArray(isi);
                        final ArrayList<String> spinnerArray = new ArrayList<String>();
                        final ArrayList<String> spinnerArrayFlag = new ArrayList<String>();
                        //cuma iss

                        spinnerArray.add("--Please Select--");

                        for (int ia = 0; ia < jsonArrays.length(); ia++) {
                            String l = jsonArrays.getJSONObject(ia).getString("label_option").toString();

                            if (jsonArrays.getJSONObject(ia).has("flag")) {
                                String flagDrop = jsonArrays.getJSONObject(ia).getString("flag").toString();
                                spinnerArrayFlag.add(ia, flagDrop);
                            }
                            spinnerArray.add(l);

                        }

                        LinearLayout spinerTitle = (LinearLayout) getLayoutInflater().inflate(R.layout.item_spiner_edit_text, null);

                        final SearchableSpinner spinner = (SearchableSpinner) spinerTitle.findViewById(R.id.spinner);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            spinner.setBackground(getResources().getDrawable(R.drawable.spinner_background));
                        }
                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item_black, spinnerArray); //selected item will look like a spinner set from XML
                        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(spinnerArrayAdapter);
                        params2.setMargins(30, 10, 30, 40);
                        spinner.setLayoutParams(params2);

                        List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add("");
                        valSetOne.add(required);
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));


                        final EditText et = (EditText) spinerTitle.findViewById(R.id.editTextOther);
                        et.setVisibility(View.GONE);


                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                        if (cursorCild.getCount() > 0) {
                            int spinnerPosition = spinnerArrayAdapter.getPosition(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                            if (spinnerPosition < 0) {
                                et.setText(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                                spinnerPosition = spinnerArrayFlag.indexOf("manual_input");
                            }
                            spinner.setSelection(spinnerPosition);
                        } else {
                            if (JcontentBawaan.has(name)) {
                                if (!JcontentBawaan.getString(name).equalsIgnoreCase("null")) {
                                    JSONObject values = new JSONObject(JcontentBawaan.getString(name));
                                    if (values.has("value")) {
                                        int spinnerPosition = spinnerArrayAdapter.getPosition(values.getString("value"));
                                        spinner.setSelection(spinnerPosition);
                                    }
                                }
                            }

                            if (spinner.getSelectedItem() != null) {
                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, spinner.getSelectedItem().toString(), jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                                db.insertRoomsDetail(orderModel);
                            }

                        }


                        if ((!showButton)) {
                            spinner.setEnabled(false);
                        } else {
                            final int finalI7 = i;
                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                @Override
                                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int myPosition, long myID) {
                                    et.setVisibility(View.GONE);
                                    if (spinnerArrayFlag.size() == spinnerArray.size() && spinnerArrayFlag.get(myPosition).equalsIgnoreCase("manual_input")) {
                                        et.setVisibility(View.VISIBLE);
                                        et.addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                            }

                                            @Override
                                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                                            }

                                            @Override
                                            public void afterTextChanged(Editable s) {
                                                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI7)));
                                                if (cEdit.getCount() > 0) {
                                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI7)), name, "cild");
                                                    db.updateDetailRoomWithFlagContent(orderModel);
                                                } else {
                                                    if (String.valueOf(s).length() > 0) {
                                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI7)), name, "cild");
                                                        db.insertRoomsDetail(orderModel);
                                                    } else {
                                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI7)), name, "cild");
                                                        db.deleteDetailRoomWithFlagContent(orderModel);
                                                    }
                                                }
                                            }
                                        });
                                    } else {
                                        et.setVisibility(View.GONE);
                                        et.setText("");

                                        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI7)));
                                        if (cEdit.getCount() > 0) {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, spinnerArray.get(myPosition), jsonCreateType(idListTask, type, String.valueOf(finalI7)), name, "cild");
                                            db.updateDetailRoomWithFlagContent(orderModel);
                                        } else {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, spinnerArray.get(myPosition), jsonCreateType(idListTask, type, String.valueOf(finalI7)), name, "cild");
                                            db.insertRoomsDetail(orderModel);
                                        }
                                    }


                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parentView) {
                                }

                            });
                        }
                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params1.setMargins(30, 10, 30, 0);

                        LinearLayout.LayoutParams params12 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params12.setMargins(50, 10, 30, 0);

                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(textView, params1);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(spinerTitle, params1);

                        //untuk kasih nafas haha
                        View view = new View(this);
                        view.setVisibility(View.INVISIBLE);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(view, params2);
                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

                    } else if (type.equalsIgnoreCase("input_kodepos")) {
                        final DatabaseKodePos mDB = new DatabaseKodePos(context);
                        if (mDB.getWritableDatabase() != null) {
                            TextView textView = new TextView(DinamicRoomTaskActivity.this);
                            if (required.equalsIgnoreCase("1")) {
                                label += "<font size=\"3\" color=\"red\">*</font>";
                            }
                            textView.setText(Html.fromHtml(label));
                            textView.setTextSize(15);

                            if (count == null) {
                                count = 0;
                            } else {
                                count++;
                            }
                            et[count] = (EditText) getLayoutInflater().inflate(R.layout.edit_input_layout, null);

                            List<String> valSetOne = new ArrayList<String>();
                            valSetOne.add(String.valueOf(count));
                            valSetOne.add(required);
                            valSetOne.add(type);
                            valSetOne.add(name);
                            valSetOne.add(label);
                            valSetOne.add(String.valueOf(i));


                            et[count].setId(Integer.parseInt(idListTask));
                            et[count].setHint(placeHolder);

                            final TextView prov = (TextView) getLayoutInflater().inflate(R.layout.text_input_layout, null);
                            prov.setTextSize(15);
                            prov.setText("-");
                            final TextView kota = (TextView) getLayoutInflater().inflate(R.layout.text_input_layout, null);
                            kota.setTextSize(15);
                            kota.setText("-");
                            final TextView kec = (TextView) getLayoutInflater().inflate(R.layout.text_input_layout, null);
                            kec.setTextSize(15);
                            kec.setText("-");
                            final TextView kel = (TextView) getLayoutInflater().inflate(R.layout.text_input_layout, null);
                            kel.setTextSize(15);
                            kel.setText("-");

                            et[count].setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(maxlength))});

                            Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                            if (cursorCild.getCount() > 0) {
                                String isi = cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));
                                et[count].setText(jsonResultType(isi, "a"));
                                prov.setText(jsonResultType(isi, "b"));
                                kota.setText(jsonResultType(isi, "c"));
                                kec.setText(jsonResultType(isi, "d"));
                                kel.setText(jsonResultType(isi, "e"));
                            } else {
                                if (!value.equalsIgnoreCase("")) {
                                    et[count].setText(value);
                                }
                            }


                            if ((!showButton)) {
                                et[count].setEnabled(false);
                            } else {
                                final int finalI8 = i;
                                final int finalI9 = i;
                                final int finalI20 = i;
                                et[count].addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {
                                        if (s.length() > 4) {
                                            Cursor c = mDB.getWritableDatabase().query(true, "wilayah", new String[]{"propinsi,jenis,kabupaten,kecamatan,kelurahan"}, "kode_pos = '" + s + "'", null, null, null, null, null);
                                            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                                                    DinamicRoomTaskActivity.this,
                                                    android.R.layout.simple_list_item_1);
                                            final ArrayList<ModelWilayah> modelWilayahs = new ArrayList<ModelWilayah>();
                                            if (c.moveToFirst()) {
                                                do {
                                                    String prov = c.getString(0);
                                                    String jenis = c.getString(1);
                                                    String kab = c.getString(2);
                                                    String kec = c.getString(3);
                                                    String kel = c.getString(4);

                                                    ModelWilayah modelWilayah = new ModelWilayah(s.toString(), prov, kab, jenis, kec, kel);
                                                    modelWilayahs.add(modelWilayah);
                                                    arrayAdapter.add(prov + "," + jenis + " " + kab + "," + kec + "," + kel);
                                                } while (c.moveToNext());
                                            }

                                            if (arrayAdapter.getCount() == 0) {
                                                android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(DinamicRoomTaskActivity.this);
                                                alertDialogBuilder.setMessage("Kode Pos not valid");

                                                alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface arg0, int arg1) {
                                                        et[count].setError("not valid");
                                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(idListTask, type, String.valueOf(finalI8)), name, "cild");

                                                        db.deleteDetailRoomWithFlagContent(orderModel);

                                                    }
                                                });

                                                android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                                alertDialog.show();
                                            } else if (arrayAdapter.getCount() == 1) {

                                                final String strProv = modelWilayahs.get(0).getProv();
                                                final String strKode = modelWilayahs.get(0).getKodepos();
                                                final String strJen = modelWilayahs.get(0).getJenis();
                                                final String strKab = modelWilayahs.get(0).getKab();
                                                final String strKec = modelWilayahs.get(0).getKec();
                                                final String strKel = modelWilayahs.get(0).getKel();

                                                prov.setText(strProv);
                                                kota.setText(strJen + " " + strKab);
                                                kec.setText(strKec);
                                                kel.setText(strKel);


                                                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI8)));
                                                if (cEdit.getCount() > 0) {
                                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, jsonPosCode(strKode, strProv, strJen + " " + strKab, strKec, strKel), jsonCreateType(idListTask, type, String.valueOf(finalI8)), name, "cild");
                                                    db.updateDetailRoomWithFlagContent(orderModel);
                                                } else {
                                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, jsonPosCode(strKode, strProv, strJen + " " + strKab, strKec, strKel), jsonCreateType(idListTask, type, String.valueOf(finalI8)), name, "cild");
                                                    db.insertRoomsDetail(orderModel);
                                                }


                                            } else {

                                                android.support.v7.app.AlertDialog.Builder builderSingle = new android.support.v7.app.AlertDialog.Builder(DinamicRoomTaskActivity.this);
                                                builderSingle.setTitle("Pilih kelurahan ");

                                                builderSingle.setNegativeButton(
                                                        "cancel",
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                            }
                                                        });

                                                builderSingle.setAdapter(
                                                        arrayAdapter,
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                final String strProv = modelWilayahs.get(which).getProv();
                                                                final String strKode = modelWilayahs.get(which).getKodepos();
                                                                final String strJen = modelWilayahs.get(which).getJenis();
                                                                final String strKab = modelWilayahs.get(which).getKab();
                                                                final String strKec = modelWilayahs.get(which).getKec();
                                                                final String strKel = modelWilayahs.get(which).getKel();

                                                                prov.setText(strProv);
                                                                kota.setText(strJen + " " + strKab);
                                                                kec.setText(strKec);
                                                                kel.setText(strKel);


                                                                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI9)));
                                                                if (cEdit.getCount() > 0) {
                                                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, jsonPosCode(strKode, strProv, strJen + " " + strKab, strKec, strKel), jsonCreateType(idListTask, type, String.valueOf(finalI9)), name, "cild");
                                                                    db.updateDetailRoomWithFlagContent(orderModel);
                                                                } else {
                                                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, jsonPosCode(strKode, strProv, strJen + " " + strKab, strKec, strKel), jsonCreateType(idListTask, type, String.valueOf(finalI9)), name, "cild");
                                                                    db.insertRoomsDetail(orderModel);
                                                                }

                                                            }
                                                        });
                                                builderSingle.show();

                                                c.close();
                                            }
                                        } else {
                                            prov.setText("-");
                                            kota.setText("-");
                                            kec.setText("-");
                                            kel.setText("-");
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(idListTask, type, String.valueOf(finalI20)), name, "cild");

                                            db.deleteDetailRoomWithFlagContent(orderModel);

                                        }
                                    }

                                });
                                Intent newIntent = new Intent("bLFormulas");
                                sendBroadcast(newIntent);
                            }
                            TextView tvProv = new TextView(DinamicRoomTaskActivity.this);
                            tvProv.setText("Provinsi");
                            tvProv.setTextSize(15);
                            tvProv.setLayoutParams(new TableRow.LayoutParams(0));
                            TextView tvKota = new TextView(DinamicRoomTaskActivity.this);
                            tvKota.setText("Kota / Kabupaten");
                            tvKota.setTextSize(15);
                            tvKota.setLayoutParams(new TableRow.LayoutParams(0));
                            TextView tvKec = new TextView(DinamicRoomTaskActivity.this);
                            tvKec.setText("Kecamatan");
                            tvKec.setTextSize(15);
                            tvKec.setLayoutParams(new TableRow.LayoutParams(0));
                            TextView tvKel = new TextView(DinamicRoomTaskActivity.this);
                            tvKel.setText("Kelurahan");
                            tvKel.setTextSize(15);
                            tvKel.setLayoutParams(new TableRow.LayoutParams(0));
                            TextView tvKode = new TextView(DinamicRoomTaskActivity.this);
                            tvKode.setText("Kode Pos");
                            tvKode.setTextSize(15);
                            tvKode.setLayoutParams(new TableRow.LayoutParams(0));


                            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params1.setMargins(30, 10, 30, 0);
                            LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params3.setMargins(60, 20, 30, 0);
                            LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params4.setMargins(60, 10, 30, 0);
                            LinearLayout.LayoutParams params5 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params5.setMargins(60, 10, 30, 10);
                            TableRow.LayoutParams params2 = new TableRow.LayoutParams(1);
                            params2.setMargins(60, 10, 30, 40);

                            valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                            linearLayout.addView(textView, params1);
                            valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                            linearLayout.addView(tvKode, params3);
                            valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                            linearLayout.addView(et[count], params5);
                            valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                            linearLayout.addView(tvProv, params4);
                            valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                            linearLayout.addView(prov, params5);
                            valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                            linearLayout.addView(tvKota, params4);
                            valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                            linearLayout.addView(kota, params5);
                            valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                            linearLayout.addView(tvKec, params4);
                            valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                            linearLayout.addView(kec, params5);
                            valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                            linearLayout.addView(tvKel, params4);
                            valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                            linearLayout.addView(kel, params2);


                            hashMap.put(Integer.parseInt(idListTask), valSetOne);

                        } else {
                            if (deleteContent) {

                                db.deleteRoomsDetailbyId(idDetail, idTab, username);

                            }
                            finish();
                            Intent intent = new Intent(context, DownloadUtilsActivity.class);
                            startActivity(intent);
                            return;
                        }

                    } else if (type.equalsIgnoreCase("date_terselect")) {

                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, startDate, jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                        db.insertRoomsDetail(orderModel);

                    } else if (type.equalsIgnoreCase("dropdown_wilayah")) {
                        final DatabaseKodePos mDB = new DatabaseKodePos(context);
                        if (mDB.getWritableDatabase() != null) {
                            TextView textView = new TextView(DinamicRoomTaskActivity.this);
                            if (required.equalsIgnoreCase("1")) {
                                label += "<font size=\"3\" color=\"red\">*</font>";
                            }
                            textView.setText(Html.fromHtml(label));
                            textView.setTextSize(15);
                            textView.setLayoutParams(new TableRow.LayoutParams(0));

                            List<String> valSetOne = new ArrayList<String>();
                            valSetOne.add("");
                            valSetOne.add(required);
                            valSetOne.add(type);
                            valSetOne.add(name);
                            valSetOne.add(label);
                            valSetOne.add(String.valueOf(i));


                            TableRow.LayoutParams params2 = new TableRow.LayoutParams(1);
                            final Cursor c = mDB.getWritableDatabase().query(true, "wilayah", new String[]{"propinsi"}, null, null, "propinsi", null, null, null);
                            final ArrayList<String> spinnerArrayPropinsi = new ArrayList<>();
                            spinnerArrayPropinsi.add("Semua Provinsi");
                            if (c.moveToFirst()) {
                                do {
                                    String column1 = c.getString(0);
                                    spinnerArrayPropinsi.add(column1);
                                } while (c.moveToNext());
                            }
                            c.close();


                            final SearchableSpinner spinnerPropinsi = new SearchableSpinner(this);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                spinnerPropinsi.setBackground(getResources().getDrawable(R.drawable.spinner_background));
                            }
                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item_black, spinnerArrayPropinsi); //selected item will look like a spinner set from XML
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerPropinsi.setAdapter(spinnerArrayAdapter);

                            final SearchableSpinner spinnerKota = new SearchableSpinner(this);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                spinnerKota.setBackground(getResources().getDrawable(R.drawable.spinner_background));
                            }
                            final ArrayList<String> spinnerArrayKota = new ArrayList<>();
                            spinnerArrayKota.add("Semua Kota/Kabupaten");
                            final ArrayAdapter<String> spinnerKotaArrayAdapter = new ArrayAdapter<String>(
                                    this, R.layout.simple_spinner_item_black, spinnerArrayKota);
                            spinnerKotaArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerKota.setAdapter(spinnerKotaArrayAdapter);

                            final SearchableSpinner spinnerKecamatan = new SearchableSpinner(this);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                spinnerKecamatan.setBackground(getResources().getDrawable(R.drawable.spinner_background));
                            }
                            final ArrayList<String> spinnerArrayKecamatan = new ArrayList<>();
                            spinnerArrayKecamatan.add("Semua Kecamatan");
                            final ArrayAdapter<String> spinnerKecamatanArrayAdapter = new ArrayAdapter<String>(
                                    this, R.layout.simple_spinner_item_black, spinnerArrayKecamatan);
                            spinnerKecamatanArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerKecamatan.setAdapter(spinnerKecamatanArrayAdapter);

                            final SearchableSpinner spinnerKelurahan = new SearchableSpinner(this);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                spinnerKelurahan.setBackground(getResources().getDrawable(R.drawable.spinner_background));
                            }
                            final ArrayList<String> spinnerArrayKelurahan = new ArrayList<>();
                            spinnerArrayKelurahan.add("Semua Kelurahan");
                            final ArrayAdapter<String> spinnerKelurahanArrayAdapter = new ArrayAdapter<String>(
                                    this, R.layout.simple_spinner_item_black, spinnerArrayKelurahan);
                            spinnerKelurahanArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerKelurahan.setAdapter(spinnerKelurahanArrayAdapter);

                            final TextView kodePos = (TextView) getLayoutInflater().inflate(R.layout.text_input_layout, null);
                            kodePos.setTextSize(15);

                            int spinnerPositionProvinsi = 0;
                            int spinnerPositionKota = 0;
                            int spinnerPositionKec = 0;
                            int spinnerPositionKel = 0;
                            String isi = "";


                            if (JcontentBawaan.has(name)) {
                                if (!JcontentBawaan.getString(name).equalsIgnoreCase("null")) {
                                    JSONObject values = new JSONObject(JcontentBawaan.getString(name));
                                    if (values.has("value")) {
                                        isi = values.getString("value");
                                    }
                                }

                            }

                            Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));

                            if (cursorCild.getCount() > 0) {
                                isi = cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));
                            } else {
                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, isi, jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                                db.insertRoomsDetail(orderModel);
                            }


                            if (!jsonResultType(isi, "b").equalsIgnoreCase("Semua Provinsi")) {
                                Cursor cKota = mDB.getWritableDatabase().query(true, "wilayah", new String[]{"jenis", "kabupaten"}, "propinsi = '" + jsonResultType(isi, "b") + "'", null, null, null, null, null);

                                if (cKota.moveToFirst()) {
                                    do {
                                        String column1 = cKota.getString(0);
                                        String column2 = cKota.getString(1);
                                        spinnerArrayKota.add(column1 + " " + column2);

                                    } while (cKota.moveToNext());
                                    spinnerKotaArrayAdapter.notifyDataSetChanged();
                                }

                                if (!jsonResultType(isi, "c").equalsIgnoreCase("Semua Kota/Kabupaten")) {

                                    String[] contoh = new String[2];
                                    if (jsonResultType(isi, "c").startsWith("Kabupaten ")) {
                                        contoh[0] = "Kabupaten";
                                        contoh[1] = jsonResultType(isi, "c").toString().split("Kabupaten")[1];

                                    } else if (jsonResultType(isi, "c").startsWith("Kota ")) {
                                        contoh[0] = "Kota";
                                        contoh[1] = jsonResultType(isi, "c").toString().split("Kota ")[1];
                                    }

                                    if (contoh[0] != null && contoh[0] != null) {
                                        Cursor cKecamatan = mDB.getWritableDatabase().query(true, "wilayah", new String[]{"kecamatan"}, "propinsi = '" + jsonResultType(isi, "b") + "' and jenis = '" + contoh[0] + "' and kabupaten = '" + contoh[1].trim() + "'", null, "kecamatan", null, null, null);
                                        if (cKecamatan.moveToFirst()) {
                                            do {
                                                String column1 = cKecamatan.getString(0);
                                                spinnerArrayKecamatan.add(column1);

                                            } while (cKecamatan.moveToNext());
                                            spinnerKecamatanArrayAdapter.notifyDataSetChanged();
                                        }

                                        if (!jsonResultType(isi, "d").equalsIgnoreCase("Semua Kecamatan")) {
                                            Cursor ckelurahan = mDB.getWritableDatabase().query(true, "wilayah", new String[]{"kelurahan"}, "propinsi = '" + jsonResultType(isi, "b") + "' and jenis = '" + contoh[0] + "' and kabupaten = '" + contoh[1].trim() + "' and kecamatan ='" + jsonResultType(isi, "d") + "'", null,
                                                    null, null, null, null);
                                            if (ckelurahan.moveToFirst()) {
                                                do {
                                                    String column1 = ckelurahan.getString(0);
                                                    spinnerArrayKelurahan.add(column1);

                                                } while (ckelurahan.moveToNext());
                                                spinnerKelurahanArrayAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                }
                            }

                            kodePos.setText(jsonResultType(isi, "a"));

                            spinnerPositionProvinsi = spinnerArrayAdapter.getPosition(jsonResultType(isi, "b"));
                            spinnerPropinsi.setSelection(spinnerPositionProvinsi);

                            spinnerPositionKota = spinnerKotaArrayAdapter.getPosition(jsonResultType(isi, "c"));
                            spinnerKota.setSelection(spinnerPositionKota);

                            spinnerPositionKec = spinnerKecamatanArrayAdapter.getPosition(jsonResultType(isi, "d"));
                            spinnerKecamatan.setSelection(spinnerPositionKec);

                            spinnerPositionKel = spinnerKelurahanArrayAdapter.getPosition(jsonResultType(isi, "e"));
                            spinnerKelurahan.setSelection(spinnerPositionKel);


                            if ((!showButton)) {
                                spinnerPropinsi.setEnabled(false);
                                spinnerKota.setEnabled(false);
                                spinnerKecamatan.setEnabled(false);
                                spinnerKelurahan.setEnabled(false);
                            } else {
                                final int finalI10 = i;
                                final int finalI11 = i;
                                final int finalI12 = i;
                                spinnerPropinsi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                                        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI10)));
                                        if (cEdit.getCount() > 0) {
                                            if (jsonResultType(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)), "b").equalsIgnoreCase(spinnerArrayPropinsi.get(position))) {
                                                return;
                                            }
                                        }

                                        spinnerArrayKota.clear();
                                        spinnerArrayKota.add("Semua Kota/Kabupaten");
                                        if (position > 0) {

                                            if (cEdit.getCount() > 0) {
                                                if (!jsonResultType(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)), "b").equalsIgnoreCase(spinnerArrayPropinsi.get(position))) {
                                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, jsonPosCode("-", spinnerArrayPropinsi.get(position), "Semua Kota/Kabupaten", "Semua Kecamatan", "Semua Kelurahan"), jsonCreateType(idListTask, type, String.valueOf(finalI11)), name, "cild");
                                                    db.updateDetailRoomWithFlagContent(orderModel);
                                                }
                                            } else {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, jsonPosCode("-", spinnerArrayPropinsi.get(position), "Semua Kota/Kabupaten", "Semua Kecamatan", "Semua Kelurahan"), jsonCreateType(idListTask, type, String.valueOf(finalI11)), name, "cild");
                                                db.insertRoomsDetail(orderModel);
                                            }


                                            Cursor c = mDB.getWritableDatabase().query(true, "wilayah", new String[]{"jenis", "kabupaten"}, "propinsi = '" + spinnerArrayPropinsi.get(position) + "'", null, null, null, null, null);
                                            if (c.moveToFirst()) {
                                                do {
                                                    String column1 = c.getString(0);
                                                    String column2 = c.getString(1);
                                                    spinnerArrayKota.add(column1 + " " + column2);

                                                } while (c.moveToNext());
                                            }
                                            c.close();
                                        } else {

                                            Cursor cEdit2 = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI12)));
                                            if (cEdit2.getCount() > 0) {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, jsonPosCode("-", spinnerArrayPropinsi.get(position), "Semua Kota/Kabupaten", "Semua Kecamatan", "Semua Kelurahan"), jsonCreateType(idListTask, type, String.valueOf(finalI12)), name, "cild");
                                                db.updateDetailRoomWithFlagContent(orderModel);
                                            } else {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, jsonPosCode("-", spinnerArrayPropinsi.get(position), "Semua Kota/Kabupaten", "Semua Kecamatan", "Semua Kelurahan"), jsonCreateType(idListTask, type, String.valueOf(finalI12)), name, "cild");
                                                db.insertRoomsDetail(orderModel);
                                            }


                                        }
                                        kodePos.setText("-");
                                        spinnerArrayKecamatan.clear();
                                        spinnerArrayKecamatan.add("Semua Kecamatan");
                                        spinnerKecamatanArrayAdapter.notifyDataSetChanged();
                                        spinnerArrayKelurahan.clear();
                                        spinnerArrayKelurahan.add("Semua Kelurahan");
                                        spinnerKelurahanArrayAdapter.notifyDataSetChanged();
                                        spinnerKotaArrayAdapter.notifyDataSetChanged();
                                        spinnerKota.setSelection(0);
                                        Intent newIntent = new Intent("bLFormulas");
                                        sendBroadcast(newIntent);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parentView) {
                                        // your code here
                                    }

                                });

                                final int finalI13 = i;
                                final int finalI14 = i;
                                spinnerKota.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                                        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI13)));
                                        if (cEdit.getCount() > 0) {
                                            if (jsonResultType(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)), "c").equalsIgnoreCase(spinnerArrayKota.get(position))) {
                                                return;
                                            }
                                        }

                                        spinnerArrayKecamatan.clear();
                                        spinnerArrayKecamatan.add("Semua Kecamatan");

                                        if (position > 0) {
                                            String[] contoh = new String[2];
                                            if (spinnerArrayKota.get(position).toString().startsWith("Kabupaten ")) {
                                                contoh[0] = "Kabupaten";
                                                contoh[1] = spinnerArrayKota.get(position).toString().split("Kabupaten ")[1];

                                            } else if (spinnerArrayKota.get(position).toString().startsWith("Kota ")) {
                                                contoh[0] = "Kota";
                                                contoh[1] = spinnerArrayKota.get(position).toString().split("Kota ")[1];
                                            }

                                            Cursor cEdit2 = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI13)));
                                            if (cEdit2.getCount() > 0) {
                                                if (!jsonResultType(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)), "c").equalsIgnoreCase(spinnerArrayKota.get(position))) {
                                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, jsonPosCode("-", spinnerPropinsi.getSelectedItem().toString(), spinnerArrayKota.get(position).toString(), "Semua Kecamatan", "Semua Kelurahan"), jsonCreateType(idListTask, type, String.valueOf(finalI13)), name, "cild");
                                                    db.updateDetailRoomWithFlagContent(orderModel);
                                                }
                                            } else {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, jsonPosCode("-", spinnerPropinsi.getSelectedItem().toString(), spinnerArrayKota.get(position).toString(), "Semua Kecamatan", "Semua Kelurahan"), jsonCreateType(idListTask, type, String.valueOf(finalI13)), name, "cild");
                                                db.insertRoomsDetail(orderModel);
                                            }


                                            Cursor c = mDB.getWritableDatabase().query(true, "wilayah", new String[]{"kecamatan"}, "propinsi = '" + spinnerPropinsi.getSelectedItem() + "' and jenis = '" + contoh[0] + "' and kabupaten = '" + contoh[1].trim() + "'", null, "kecamatan", null, null, null);
                                            if (c.moveToFirst()) {
                                                do {
                                                    String column1 = c.getString(0);
                                                    spinnerArrayKecamatan.add(column1);
                                                } while (c.moveToNext());
                                            }
                                            c.close();
                                        } else {

                                            Cursor cEdit3 = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI14)));
                                            if (cEdit3.getCount() > 0) {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, jsonPosCode("-", spinnerPropinsi.getSelectedItem().toString(), spinnerArrayKota.get(position).toString(), "Semua Kecamatan", "Semua Kelurahan"), jsonCreateType(idListTask, type, String.valueOf(finalI14)), name, "cild");
                                                db.updateDetailRoomWithFlagContent(orderModel);
                                            } else {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, jsonPosCode("-", spinnerPropinsi.getSelectedItem().toString(), spinnerArrayKota.get(position).toString(), "Semua Kecamatan", "Semua Kelurahan"), jsonCreateType(idListTask, type, String.valueOf(finalI14)), name, "cild");
                                                db.insertRoomsDetail(orderModel);
                                            }


                                        }
                                        kodePos.setText("-");
                                        spinnerArrayKelurahan.clear();
                                        spinnerArrayKelurahan.add("Semua Kelurahan");
                                        spinnerKelurahanArrayAdapter.notifyDataSetChanged();
                                        spinnerKotaArrayAdapter.notifyDataSetChanged();
                                        spinnerKecamatanArrayAdapter.notifyDataSetChanged();
                                        spinnerKecamatan.setSelection(0);
                                        Intent newIntent = new Intent("bLFormulas");
                                        sendBroadcast(newIntent);

                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parentView) {
                                        // your code here
                                    }

                                });

                                final int finalI15 = i;
                                final int finalI16 = i;
                                spinnerKecamatan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                                        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI15)));
                                        if (cEdit.getCount() > 0) {
                                            if (jsonResultType(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)), "d").equalsIgnoreCase(spinnerArrayKecamatan.get(position))) {
                                                return;
                                            }
                                        }


                                        spinnerArrayKelurahan.clear();
                                        spinnerArrayKelurahan.add("Semua Kelurahan");

                                        if (position > 0) {
                                            String[] contoh = new String[2];
                                            if (spinnerKota.getSelectedItem().toString().startsWith("Kabupaten ")) {
                                                contoh[0] = "Kabupaten";
                                                contoh[1] = spinnerKota.getSelectedItem().toString().split("Kabupaten ")[1];

                                            } else if (spinnerKota.getSelectedItem().toString().startsWith("Kota ")) {
                                                contoh[0] = "Kota";
                                                contoh[1] = spinnerKota.getSelectedItem().toString().split("Kota ")[1];
                                            }

                                            Cursor cEdit2 = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI15)));
                                            if (cEdit2.getCount() > 0) {
                                                if (!jsonResultType(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)), "d").equalsIgnoreCase(spinnerArrayKecamatan.get(position))) {
                                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, jsonPosCode("-", spinnerPropinsi.getSelectedItem().toString(), spinnerKota.getSelectedItem().toString(), spinnerArrayKecamatan.get(position), "Semua Kelurahan"), jsonCreateType(idListTask, type, String.valueOf(finalI15)), name, "cild");
                                                    db.updateDetailRoomWithFlagContent(orderModel);
                                                }


                                            } else {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, jsonPosCode("-", spinnerPropinsi.getSelectedItem().toString(), spinnerKota.getSelectedItem().toString(), spinnerArrayKecamatan.get(position), "Semua Kelurahan"), jsonCreateType(idListTask, type, String.valueOf(finalI15)), name, "cild");
                                                db.insertRoomsDetail(orderModel);
                                            }


                                            Cursor c = mDB.getWritableDatabase().query(true, "wilayah", new String[]{"kelurahan"}, "propinsi = '" + spinnerPropinsi.getSelectedItem() + "' and jenis = '" + contoh[0] + "' and kabupaten = '" + contoh[1].trim() + "'" + " and kecamatan = '" + spinnerArrayKecamatan.get(position) + "'", null, null, null, null, null);
                                            if (c.moveToFirst()) {
                                                do {
                                                    String column1 = c.getString(0);
                                                    spinnerArrayKelurahan.add(column1);
                                                } while (c.moveToNext());
                                            }
                                            c.close();
                                        } else {

                                            Cursor cEdit3 = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI16)));
                                            if (cEdit3.getCount() > 0) {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, jsonPosCode("-", spinnerPropinsi.getSelectedItem().toString(), spinnerKota.getSelectedItem().toString(), spinnerArrayKecamatan.get(position), "Semua Kelurahan"), jsonCreateType(idListTask, type, String.valueOf(finalI16)), name, "cild");
                                                db.updateDetailRoomWithFlagContent(orderModel);
                                            } else {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, jsonPosCode("-", spinnerPropinsi.getSelectedItem().toString(), spinnerKota.getSelectedItem().toString(), spinnerArrayKecamatan.get(position), "Semua Kelurahan"), jsonCreateType(idListTask, type, String.valueOf(finalI16)), name, "cild");
                                                db.insertRoomsDetail(orderModel);
                                            }

                                        }
                                        kodePos.setText("-");
                                        spinnerKelurahanArrayAdapter.notifyDataSetChanged();
                                        spinnerKelurahan.setSelection(0);
                                        Intent newIntent = new Intent("bLFormulas");
                                        sendBroadcast(newIntent);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parentView) {
                                    }

                                });

                                final int finalI17 = i;
                                final int finalI18 = i;
                                final int finalI19 = i;
                                spinnerKelurahan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                                        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI17)));
                                        if (cEdit.getCount() > 0) {
                                            if (jsonResultType(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)), "e").equalsIgnoreCase(spinnerArrayKelurahan.get(position))) {
                                                return;
                                            }
                                        }

                                        if (position > 0) {

                                            Cursor cEdit2 = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI17)));
                                            if (cEdit2.getCount() > 0) {
                                                if (!jsonResultType(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)), "e").equalsIgnoreCase(spinnerArrayKelurahan.get(position))) {
                                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, jsonPosCode("-", spinnerPropinsi.getSelectedItem().toString(), spinnerKota.getSelectedItem().toString(), spinnerKecamatan.getSelectedItem().toString(), spinnerArrayKelurahan.get(position)), jsonCreateType(idListTask, type, String.valueOf(finalI17)), name, "cild");
                                                    db.updateDetailRoomWithFlagContent(orderModel);
                                                }

                                            } else {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, jsonPosCode("-", spinnerPropinsi.getSelectedItem().toString(), spinnerKota.getSelectedItem().toString(), spinnerKecamatan.getSelectedItem().toString(), spinnerArrayKelurahan.get(position)), jsonCreateType(idListTask, type, String.valueOf(finalI17)), name, "cild");
                                                db.insertRoomsDetail(orderModel);
                                            }


                                            // String kota = spinnerKota.getSelectedItem().toString().substring(5, spinnerKota.getSelectedItem().toString().length());
                                            String[] contoh = new String[2];
                                            if (spinnerKota.getSelectedItem().toString().startsWith("Kabupaten ")) {
                                                contoh[0] = "Kabupaten";
                                                contoh[1] = spinnerKota.getSelectedItem().toString().split("Kabupaten ")[1];

                                            } else if (spinnerKota.getSelectedItem().toString().startsWith("Kota ")) {
                                                contoh[0] = "Kota";
                                                contoh[1] = spinnerKota.getSelectedItem().toString().split("Kota ")[1];
                                            }

                                            Cursor c = mDB.getWritableDatabase().query(true, "wilayah", new String[]{"kode_pos"}, "propinsi = '" + spinnerPropinsi.getSelectedItem() + "' and jenis = '" + contoh[0] + "' and kabupaten = '" + contoh[1].trim() + "'" + " and kecamatan = '" + spinnerKecamatan.getSelectedItem() + "' and kelurahan = '" + spinnerArrayKelurahan.get(position) + "'", null, null, null, null, null);

                                            if (c.moveToFirst()) {
                                                do {
                                                    String column1 = c.getString(0);
                                                    kodePos.setText(column1);

                                                    Cursor cEdit3 = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI18)));
                                                    if (cEdit3.getCount() > 0) {
                                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, jsonPosCode(column1, spinnerPropinsi.getSelectedItem().toString(), spinnerKota.getSelectedItem().toString(), spinnerKecamatan.getSelectedItem().toString(), spinnerArrayKelurahan.get(position)), jsonCreateType(idListTask, type, String.valueOf(finalI18)), name, "cild");
                                                        db.updateDetailRoomWithFlagContent(orderModel);
                                                    } else {
                                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, jsonPosCode(column1, spinnerPropinsi.getSelectedItem().toString(), spinnerKota.getSelectedItem().toString(), spinnerKecamatan.getSelectedItem().toString(), spinnerArrayKelurahan.get(position)), jsonCreateType(idListTask, type, String.valueOf(finalI18)), name, "cild");
                                                        db.insertRoomsDetail(orderModel);
                                                    }


                                                } while (c.moveToNext());
                                            }
                                            c.close();
                                        } else {

                                            Cursor cEdit2 = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI19)));
                                            if (cEdit2.getCount() > 0) {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, jsonPosCode("-", spinnerPropinsi.getSelectedItem().toString(), spinnerKota.getSelectedItem().toString(), spinnerKecamatan.getSelectedItem().toString(), spinnerArrayKelurahan.get(position)), jsonCreateType(idListTask, type, String.valueOf(finalI19)), name, "cild");
                                                db.updateDetailRoomWithFlagContent(orderModel);
                                            } else {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, jsonPosCode("-", spinnerPropinsi.getSelectedItem().toString(), spinnerKota.getSelectedItem().toString(), spinnerKecamatan.getSelectedItem().toString(), spinnerArrayKelurahan.get(position)), jsonCreateType(idListTask, type, String.valueOf(finalI19)), name, "cild");
                                                db.insertRoomsDetail(orderModel);
                                            }

                                        }
                                        Intent newIntent = new Intent("bLFormulas");
                                        sendBroadcast(newIntent);

                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parentView) {
                                        // your code here
                                    }

                                });
                            }


                            TextView tvProv = new TextView(DinamicRoomTaskActivity.this);
                            tvProv.setText("Provinsi");
                            tvProv.setTextSize(15);
                            tvProv.setLayoutParams(new TableRow.LayoutParams(0));
                            TextView tvKota = new TextView(DinamicRoomTaskActivity.this);
                            tvKota.setText("Kota / Kabupaten");
                            tvKota.setTextSize(15);
                            tvKota.setLayoutParams(new TableRow.LayoutParams(0));
                            TextView tvKec = new TextView(DinamicRoomTaskActivity.this);
                            tvKec.setText("Kecamatan");
                            tvKec.setTextSize(15);
                            tvKec.setLayoutParams(new TableRow.LayoutParams(0));
                            TextView tvKel = new TextView(DinamicRoomTaskActivity.this);
                            tvKel.setText("Kelurahan");
                            tvKel.setTextSize(15);
                            tvKel.setLayoutParams(new TableRow.LayoutParams(0));
                            TextView tvKode = new TextView(DinamicRoomTaskActivity.this);
                            tvKode.setText("Kode Pos");
                            tvKode.setTextSize(15);
                            tvKode.setLayoutParams(new TableRow.LayoutParams(0));


                            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params1.setMargins(30, 10, 30, 0);
                            LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params3.setMargins(60, 20, 30, 0);
                            LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params4.setMargins(60, 10, 30, 0);
                            params2.setMargins(60, 10, 30, 40);
                            LinearLayout.LayoutParams params5 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params5.setMargins(60, 10, 30, 10);

                            spinnerPropinsi.setLayoutParams(params4);
                            spinnerKota.setLayoutParams(params4);
                            spinnerKecamatan.setLayoutParams(params4);
                            spinnerKelurahan.setLayoutParams(params4);

                            valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                            linearLayout.addView(textView, params1);
                            valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                            linearLayout.addView(tvProv, params3);
                            valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                            linearLayout.addView(spinnerPropinsi, params5);
                            valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                            linearLayout.addView(tvKota, params4);
                            valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                            linearLayout.addView(spinnerKota, params5);
                            valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                            linearLayout.addView(tvKec, params4);
                            valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                            linearLayout.addView(spinnerKecamatan, params5);

                            if (flag.equalsIgnoreCase("yes")) {
                                valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                                linearLayout.addView(tvKel, params4);
                                valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                                linearLayout.addView(spinnerKelurahan, params5);
                                valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                                linearLayout.addView(tvKode, params4);
                                valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                                linearLayout.addView(kodePos, params2);
                            } else {
                                valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                                linearLayout.addView(tvKel, params4);
                                valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                                linearLayout.addView(spinnerKelurahan, params2);
                            }
                            hashMap.put(Integer.parseInt(idListTask), valSetOne);

                        } else {
                            if (deleteContent) {

                                db.deleteRoomsDetailbyId(idDetail, idTab, username);

                            }
                            finish();
                            Intent intent = new Intent(context, DownloadUtilsActivity.class);
                            startActivity(intent);
                            return;
                        }

                    } else if (type.equalsIgnoreCase("checkbox")) {
                        //add checkboxes
                        TextView textView = new TextView(DinamicRoomTaskActivity.this);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(15);
                        List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add("");
                        valSetOne.add(required);
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));
                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(30, 10, 30, 0);
                        textView.setLayoutParams(params2);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(textView);


                        HashMap hashMapCheck = new HashMap();
                        String isis = jsonArray.getJSONObject(i).getString("checkbox").toString();
                        Boolean setCheck = false;
                        JSONArray jsonArrayCeks = new JSONArray(isis);

                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                        if (cursorCild.getCount() > 0) {
                            String cc = cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));
                            if (!cc.startsWith("[")) {
                                cc = "[" + cc + "]";
                            }
                            JSONArray jsA = new JSONArray(cc);
                            for (int ic = 0; ic < jsA.length(); ic++) {
                                setCheck = true;
                                final String icD = jsA.getJSONObject(ic).getString("id").toString();
                                final String icC = jsA.getJSONObject(ic).getString("c").toString();
                                hashMapCheck.put(icD, icC);
                            }

                        }


                        for (int iaa = 0; iaa < jsonArrayCeks.length(); iaa++) {
                            String l = jsonArrayCeks.getJSONObject(iaa).getString("label_checkbox").toString();
                            String v = jsonArrayCeks.getJSONObject(iaa).getString("val_name").toString();
                            String is = jsonArrayCeks.getJSONObject(iaa).getString("is_checked").toString();
                            final CheckBox cb = new CheckBox(this);
                            LinearLayout.LayoutParams params2b = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params2b.setMargins(30, 10, 30, 0);
                            cb.setLayoutParams(params2b);
                            cb.setText(l);
                            cb.setId(iaa);
                            Iterator it = hashMapCheck.entrySet().iterator();
                            while (it.hasNext()) {
                                Map.Entry pair = (Map.Entry) it.next();
                                if (pair.getValue().toString().equalsIgnoreCase(l)) {
                                    cb.setChecked(true);
                                }
                            }

                            if (setCheck && is.equalsIgnoreCase("1")) {
                                cb.setChecked(true);
                            }
                            if ((!showButton)) {
                                cb.setEnabled(false);
                            } else {
                                final int finalI21 = i;
                                final int finalI22 = i;
                                cb.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub

                                        if (cb.isChecked()) {
                                            Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI21)));
                                            if (cEdit.getCount() > 0) {
                                                String text = cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));

                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, text + "," + jsonCheckBox(String.valueOf(cb.getId()), cb.getText().toString()), jsonCreateType(idListTask, type, String.valueOf(finalI21)), name, "cild");
                                                db.updateDetailRoomWithFlagContent(orderModel);
                                            } else {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, jsonCheckBox(String.valueOf(cb.getId()), cb.getText().toString()), jsonCreateType(idListTask, type, String.valueOf(finalI21)), name, "cild");
                                                db.insertRoomsDetail(orderModel);
                                            }
                                        } else {
                                            Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI21)));
                                            if (cursorCild.getCount() > 0) {
                                                String cc = cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));
                                                if (!cc.startsWith("[")) {
                                                    cc = "[" + cc + "]";
                                                }
                                                JSONArray jsA = null;
                                                try {
                                                    jsA = new JSONArray(cc);
                                                    if (jsA.length() > 1) {
                                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(removeJson(cb.getId(), jsA)), jsonCreateType(idListTask, type, String.valueOf(finalI22)), name, "cild");
                                                        db.updateDetailRoomWithFlagContent(orderModel);
                                                    } else {
                                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(cb.getText()), jsonCreateType(idListTask, type, String.valueOf(finalI22)), name, "cild");
                                                        db.deleteDetailRoomWithFlagContent(orderModel);
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }

                                        Intent newIntent = new Intent("bLFormulas");
                                        sendBroadcast(newIntent);
                                    }

                                });
                            }
                            valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                            linearLayout.addView(cb);
                            hashMap.put(Integer.parseInt(idListTask), valSetOne);
                        }
                    } else if (type.equalsIgnoreCase("radio")) {
                        Log.w("buehasd1", "Gampang");
//sendiri
                        // TODO: 07/09/18 lakukan penambahan edit text di other disamakan dengan dropdown
                        TextView textView = new TextView(DinamicRoomTaskActivity.this);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        if (count == null) {
                            count = 0;
                        } else {
                            count++;
                        }

                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(15);
                        List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(String.valueOf(count));
                        valSetOne.add(required);
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));
                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(30, 10, 30, 0);
                        textView.setLayoutParams(params2);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(textView);


                        String isis = jsonArray.getJSONObject(i).getString("radio").toString();


                        JSONArray jsonArrayCeks = new JSONArray(isis);
                        final RadioButton[] rb = new RadioButton[jsonArrayCeks.length()];
                        LinearLayout.LayoutParams params2b = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2b.setMargins(30, 10, 30, 0);
                        rg[count] = new RadioGroup(this); //create the RadioGroup
                        rg[count].setOrientation(RadioGroup.VERTICAL);//or RadioGroup.VERTICAL
                        rg[count].setLayoutParams(params2b);

                        final EditText et = (EditText) getLayoutInflater().inflate(R.layout.edit_input_layout, null);
                        et.setVisibility(View.GONE);

                        final ArrayList<String> spinnerArrayFlag = new ArrayList<String>();
                        Boolean manul = true;

                        String hasilDariDB = "";
                        String hasilDariEditValue = "";
                        if (JcontentBawaan.has(name)) {
                            if (!JcontentBawaan.getString(name).equalsIgnoreCase("null")) {
                                JSONObject values = new JSONObject(JcontentBawaan.getString(name));
                                if (values.has("value")) {
                                    hasilDariDB = values.getString("value");
                                }
                            }
                        }

                        Cursor cursorC = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                        if (cursorC.getCount() > 0) {
                            hasilDariDB = cursorC.getString(cursorC.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));
                        }

                        if (hasilDariDB.split(" = ").length == 2) {
                            hasilDariEditValue = hasilDariDB.split(" = ")[1];
                            hasilDariDB = hasilDariDB.split(" = ")[0];

                        }

                        int idchecked = -1;

                        for (int iaa = 0; iaa < jsonArrayCeks.length(); iaa++) {
                            String l = jsonArrayCeks.getJSONObject(iaa).getString("label_radio").toString();
                            String cek = jsonArrayCeks.getJSONObject(iaa).getString("is_checked").toString();

                            String flagDrop = "";
                            if (jsonArrayCeks.getJSONObject(iaa).has("flag")) {
                                flagDrop = jsonArrayCeks.getJSONObject(iaa).getString("flag").toString();
                                spinnerArrayFlag.add(iaa, flagDrop);

                            }


                            rb[iaa] = new RadioButton(this);
                            rb[iaa].setText(l);
                            rb[iaa].setId(iaa);

                            if (!cek.equalsIgnoreCase("null")) {
                                rb[iaa].setChecked(true);
                            }


                            if (hasilDariDB.equalsIgnoreCase(l)) {
//                                rb[iaa].setChecked(true);
                                idchecked = iaa;
                            }


                            if ((!showButton)) {
                                rb[iaa].setEnabled(false);
                            }

                            if (rb[iaa].isChecked() && flagDrop.equalsIgnoreCase("manual_input")) {
                                et.setText(hasilDariEditValue);
                                et.setVisibility(View.VISIBLE);
                            } else {
                                et.setVisibility(View.GONE);
                                et.setText("");
                            }

                            rg[count].addView(rb[iaa]);
                        }

                        if (idchecked != -1) {
                            rg[count].check(idchecked);
                        }

                        if ((!showButton)) {
                            rg[count].setEnabled(false);
                        } else {
                            final int finalI23 = i;
                            rg[count].setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(RadioGroup group, int checkedId) {

                                    RadioButton rbs = (RadioButton) group.findViewById(checkedId);
                                    if (null != rbs && checkedId > -1) {
                                        if (spinnerArrayFlag.get(checkedId).equalsIgnoreCase("manual_input")) {

                                            Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI23)));
                                            if (cEdit.getCount() > 0) {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, rbs.getText().toString(), jsonCreateType(idListTask, type, String.valueOf(finalI23)), name, "cild");
                                                db.updateDetailRoomWithFlagContent(orderModel);
                                            } else {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, rbs.getText().toString(), jsonCreateType(idListTask, type, String.valueOf(finalI23)), name, "cild");
                                                db.insertRoomsDetail(orderModel);
                                            }

                                            et.setVisibility(View.VISIBLE);
                                            et.addTextChangedListener(new TextWatcher() {
                                                @Override
                                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                                }

                                                @Override
                                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                                }

                                                @Override
                                                public void afterTextChanged(Editable s) {
                                                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI23)));
                                                    if (cEdit.getCount() > 0) {
                                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, rbs.getText().toString() + " = " + String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI23)), name, "cild");
                                                        db.updateDetailRoomWithFlagContent(orderModel);
                                                    } else {
                                                        if (String.valueOf(s).length() > 0) {
                                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, rbs.getText().toString() + " = " + String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI23)), name, "cild");
                                                            db.insertRoomsDetail(orderModel);
                                                        } else {
                                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, rbs.getText().toString() + " = " + String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI23)), name, "cild");
                                                            db.deleteDetailRoomWithFlagContent(orderModel);
                                                        }
                                                    }
                                                }
                                            });
                                        } else {
                                            et.setVisibility(View.GONE);
                                            et.setText("");

                                            Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI23)));
                                            if (cEdit.getCount() > 0) {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, rbs.getText().toString(), jsonCreateType(idListTask, type, String.valueOf(finalI23)), name, "cild");
                                                db.updateDetailRoomWithFlagContent(orderModel);
                                            } else {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, rbs.getText().toString(), jsonCreateType(idListTask, type, String.valueOf(finalI23)), name, "cild");
                                                db.insertRoomsDetail(orderModel);
                                            }

                                        }


                                        Intent newIntent = new Intent("bLFormulas");
                                        sendBroadcast(newIntent);
                                    }
                                }
                            });
                        }

                        LinearLayout.LayoutParams params12 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params12.setMargins(50, 10, 30, 0);

                        LinearLayout.LayoutParams params12a = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params12a.setMargins(50, 10, 30, 40);

                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(rg[count]);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(et, params12);
                        View view = new View(this);
                        view.setVisibility(View.INVISIBLE);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(view, params12a);


                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

                    } else if (type.equalsIgnoreCase("qr_generate")) {

                        TextView textView = new TextView(DinamicRoomTaskActivity.this);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(15);
                        List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add("");
                        valSetOne.add("");
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));

                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(30, 10, 30, 0);
                        textView.setLayoutParams(params2);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(textView);

                        if (count == null) {
                            count = 0;
                        } else {
                            count++;
                        }

                        String downloadForm = jsonArray.getJSONObject(i).getString("value");
                        String strParams = jsonArray.getJSONObject(i).getString("formula");

                        LinearLayout imgLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.image_loader_layout_form, null);
                        int width = getWindowManager().getDefaultDisplay().getWidth();
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, width / 2);
                        params.setMargins(5, 15, 0, 0);
                        final ImageView imageView = (ImageView) imgLayout.findViewById(R.id.value);
                        imageView.setLayoutParams(params);
                        final AVLoadingIndicatorView progress = (AVLoadingIndicatorView) imgLayout.findViewById(R.id.loader_progress);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(imgLayout);
                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

                        MessengerDatabaseHelper messengerHelper = null;
                        if (messengerHelper == null) {
                            messengerHelper = MessengerDatabaseHelper.getInstance(context);
                        }

                        HashMap<String, String> data = new HashMap<>();
                        Contact contact = messengerHelper.getMyContact();
                        if (strParams.equalsIgnoreCase("bc_user")) {
                            data.put(strParams, contact.getJabberId());
                        } else if (strParams.equalsIgnoreCase("spk")) {
                            data.put(strParams, "073021050200045");
                        }

                        new GenerateQR(downloadForm, data, new GenerateQR.GenerateQRListener() {
                            @Override
                            public void onSuccess(Bitmap qrBitmap) {
                                imageView.setImageBitmap(qrBitmap);
                            }

                            @Override
                            public void onFailure(String errorMsg) {
                                Toast.makeText(getBaseContext(), errorMsg, Toast.LENGTH_SHORT).show();
                                progress.setVisibility(View.INVISIBLE);
                            }
                        });
                    } else if (type.equalsIgnoreCase("image_load")) {

                        TextView textView = new TextView(DinamicRoomTaskActivity.this);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(15);
                        List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(String.valueOf(count));
                        valSetOne.add("");
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));

                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(30, 10, 30, 0);
                        textView.setLayoutParams(params2);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(textView);

                        if (count == null) {
                            count = 0;
                        } else {
                            count++;
                        }


                        imageView[count] = new ImageView(this);

                        imageView[count].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent intent = new Intent(context, ZoomImageViewActivity.class);
                                intent.putExtra(ZoomImageViewActivity.KEY_FILE, value);
                                startActivity(intent);
                            }
                        });


                        ImageLoaderLarge imgCard = new ImageLoaderLarge(context, true);
                        imgCard.DisplayImage(value, imageView[count]);
                        int width = getWindowManager().getDefaultDisplay().getWidth();
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, width / 2);
                        params.setMargins(5, 15, 0, 0);
                        imageView[count].setLayoutParams(params);
                        valSetOne.add(String.valueOf(linearLayout.getChildCount()));
                        linearLayout.addView(imageView[count]);
                        hashMap.put(Integer.parseInt(idListTask), valSetOne);
                    } else if (type.equalsIgnoreCase("hidden")) {

                    } else if (type.equalsIgnoreCase("longlat")) {

                    }

                    // TODO: 11/26/18

                    if (dropdownViewId != null) {
                        if (dropdownViewId.length() > 0 && dropdownViewIdParent != null) {
                            Boolean shooww = false;
                            for (int z = 0; z < dropdownViewId.length(); z++) {

                                if (dropdownViewIdParent.equalsIgnoreCase(dropdownViewId.getString(z))) {
                                    shooww = true;
                                }
                            }
                            if (!shooww) {
                                List valuesss = (List) hashMap.get(Integer.parseInt(idListTask));
                                for (int ii = 6; ii < valuesss.size(); ii++) {
                                    lolosReq.add(idListTask);
                                    linearLayout.getChildAt(Integer.valueOf(valuesss.get(ii).toString())).setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            if (includeStatus) {

                final ArrayList<String> spinnerArray = new ArrayList<String>();
                spinnerArray.add("--Please Select--");
                spinnerArray.add(labelApprove);


                if (typeStatus.equalsIgnoreCase("2")) {
                    spinnerArray.add(labelDone);
                    spinnerArray.add(labelReject);
                } else if (typeStatus.equalsIgnoreCase("3")) {
                    spinnerArray.add(labelDone);
                } else if (typeStatus.equalsIgnoreCase("4")) {
                    spinnerArray.add(labelDone);
                } else {
                    spinnerArray.add(labelReject);
                }


                LinearLayout spinerTitle = (LinearLayout) getLayoutInflater().inflate(R.layout.item_spiner_textview, null);
                TextView textViewFirst = (TextView) spinerTitle.findViewById(R.id.title);

                textViewFirst.setText(Html.fromHtml("Status " + "<font size=\"3\" color=\"red\">*</font>"));
                textViewFirst.setTextSize(15);
                final SearchableSpinner spinner = (SearchableSpinner) spinerTitle.findViewById(R.id.spinner);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    spinner.setBackground(getResources().getDrawable(R.drawable.spinner_background));
                }

                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.text_view_layout, spinnerArray); //selected item will look like a spinner set from XML
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(spinnerArrayAdapter);

                Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "includeStatus", "");
                if (cursorCild.getCount() > 0) {
                    String cc = cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));
                    int spinnerPosition = spinnerArrayAdapter.getPosition(cc);
                    spinner.setSelection(spinnerPosition);
                }


/*
                if (!linkGetAsignTo.equalsIgnoreCase("")) {
                    linearLayout.getChildAt(linearLayout.getChildCount() - 2).getVisibility()
                    Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "assignTo", "");
                    if (cursorCild.getCount() == 0) {
                        berhenti = true;
                        b.setEnabled(true);
                        errorReq.add("Assign To");
                    }
                }
*/

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                        if (!linkGetAsignTo.equalsIgnoreCase("")) {
                            if (!spinnerArray.get(position).equals(labelApprove)) {
                                linearLayout.getChildAt(linearLayout.getChildCount() - 2).setVisibility(View.GONE);
                            } else {
                                linearLayout.getChildAt(linearLayout.getChildCount() - 2).setVisibility(View.VISIBLE);
                            }
                        }


                        if (!spinnerArray.get(position).equals("--Please Select--")) {
                            //cumahonda
                            if (spinnerArray.get(position).equalsIgnoreCase("suspect")) {
                                btnSUMBIT.setVisibility(View.INVISIBLE);
                            } else if (spinnerArray.get(position).equalsIgnoreCase("Hot Prospect")) {
                                btnSUMBIT.setVisibility(View.VISIBLE);
                            } else if (spinnerArray.get(position).equalsIgnoreCase("deal")) {
                                denganCheck = true;
                            } else if (spinnerArray.get(position).equalsIgnoreCase("no deal")) {
                                denganCheck = false;
                            } else if (spinnerArray.get(position).equalsIgnoreCase("valid")) {
                                denganCheck = true;
                            } else if (spinnerArray.get(position).equalsIgnoreCase("reject")) {
                                denganCheck = false;
                            }

                            Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "includeStatus", "");
                            if (cEdit.getCount() > 0) {

                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, spinnerArray.get(position), "", "", "includeStatus");
                                db.updateDetailRoomWithFlagContentNew(orderModel);
                            } else {
                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, spinnerArray.get(position), "", "", "includeStatus");
                                db.insertRoomsDetail(orderModel);
                            }
                        } else {

                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", "", "", "includeStatus");
                            db.deleteDetailRoomWithFlagContentNew(orderModel);
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // your code here
                    }

                });

                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params2.setMargins(30, 30, 30, 40);
                spinerTitle.setLayoutParams(params2);

                linearLayout.addView(spinerTitle);
            }


            if (!linkGetAsignTo.equalsIgnoreCase("")) {
                Log.w("coki1", "Parle1");
                final ContactsCompletionView completionView;
                Person[] people;
                ArrayAdapter<Person> adapter;

                LinearLayout btnRel = (LinearLayout) getLayoutInflater().inflate(R.layout.form_dinamic_asignto, null);

                DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(context);

                if (dataBaseHelper.checkTable("room")) {
                    Log.w("coki2", "Parle1");
                    Cursor curr = dataBaseHelper.selectAll("room", username, idTab);
                    ArrayList<Person> rere = new ArrayList<Person>();

                    rere.add(new Person("All", "All"));

                    if (curr.getCount() > 0) {
                        Log.w("coki3", "Parle1");
                        if (curr.moveToFirst()) {
                            do {
                                rere.add(new Person(curr.getString(6) + " - " + curr.getString(5), "@ " + curr.getString(6) + " (" + curr.getString(2) + ")"));
                            } while (curr.moveToNext());
                        }

                        people = new Person[rere.size()];

                        adapter = new FilteredArrayAdapter<Person>(getApplicationContext(), R.layout.person_layout, rere.toArray(people)) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                if (convertView == null) {

                                    LayoutInflater l = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                                    convertView = l.inflate(R.layout.person_layout, parent, false);
                                }

                                Person p = getItem(position);
                                ((TextView) convertView.findViewById(R.id.name)).setText(p.getName());
                                ((TextView) convertView.findViewById(R.id.email)).setText(p.getEmail());

                                return convertView;
                            }

                            @Override
                            protected boolean keepObject(Person person, String mask) {
                                mask = mask.toLowerCase();
                                return person.getName().toLowerCase().startsWith(mask) || person.getEmail().toLowerCase().startsWith(mask);
                            }
                        };


                        completionView = (ContactsCompletionView) btnRel.findViewById(R.id.searchView);
                        completionView.setAdapter(adapter);
                        completionView.setTokenListener(this);
                        if (savedInstanceState == null) {
                            completionView.setPrefix("Assign To : ");
                        }


                        completionView.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Select);

                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "assignTo", "");
                        if (cursorCild.getCount() > 0) {
                            Log.w("coki4", "Parle1");
                            String cc = cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));
                            String[] loop = cc.split(",");
                            for (String cu : loop) {
                                for (Person pp : people) {
                                    if (pp.getName().equalsIgnoreCase(cu)) {
                                        completionView.addObject(pp);
                                    }
                                }
                            }
                        }
                    } else {
                        Log.w("coki5", "Parle1");
                        new RefreshDBAsign(DinamicRoomTaskActivity.this).execute(linkGetAsignTo, username);
                    }
                } else {
                    Log.w("coki6", "Parle1");
                    new RefreshDBAsign(DinamicRoomTaskActivity.this).execute(linkGetAsignTo, username);
                }


                final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(30, 10, 20, 0);
                btnRel.setLayoutParams(params);
                linearLayout.addView(btnRel);
            }

            if (showButton) {


                b = (Button) btnSUMBIT.findViewById(R.id.btn_submit);

                final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(5, 15, 0, 0);
                btnSUMBIT.setLayoutParams(params);
                linearLayout.addView(btnSUMBIT);

                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        b.setEnabled(false);
                        boolean berhenti = false;

                        List<String> errorReq = new ArrayList<String>();

                        if (denganCheck) {
                            for (Integer key : hashMap.keySet()) {
                                List<String> value = hashMap.get(key);
                                if (value != null) {
                                    Boolean lolos = false;
                                    if (!lolosReq.isEmpty()) {
                                        if (lolosReq.contains(String.valueOf(key))) {
                                            lolos = true;
                                            Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(String.valueOf(key), value.get(2).toString(), value.get(5).toString()));
                                            if (cEdit.getCount() > 0) {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(String.valueOf(key), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                                                db.deleteDetailRoomWithFlagContent(orderModel);
                                            }
                                        }
                                    }


                                    if (value.get(1).toString().equalsIgnoreCase("1") && !lolos) {
                                        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(String.valueOf(key), value.get(2).toString(), value.get(5).toString()));
                                        if (cEdit.getCount() > 0) {
                                            if (cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).equalsIgnoreCase("")) {
                                                if (value.get(2).toString().equalsIgnoreCase("text") ||
                                                        value.get(2).toString().equalsIgnoreCase("textarea") ||
                                                        value.get(2).toString().equalsIgnoreCase("email") ||
                                                        value.get(2).toString().equalsIgnoreCase("number") ||
                                                        value.get(2).toString().equalsIgnoreCase("phone_number") ||
                                                        value.get(2).toString().equalsIgnoreCase("currency")) {
                                                    String aa = value.get(0).toString();
                                                    et[Integer.valueOf(aa)].setError("required");
                                                    berhenti = true;
                                                } else if (value.get(2).toString().equalsIgnoreCase("attach_api")) {
                                                    //tidak ada action
                                                } else {
                                                    berhenti = true;
                                                    errorReq.add(value.get(4).toString());
                                                }
                                            } else {
                                                if (value.get(2).toString().equalsIgnoreCase("dropdown_form")) {

                                                    JSONObject jsonObject = null;
                                                    try {
                                                        jsonObject = new JSONObject(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                                                        Iterator<String> iter = jsonObject.keys();
                                                        int mas = 0;
                                                        while (iter.hasNext()) {
                                                            mas = mas + 1;
                                                            String keys = iter.next();
                                                            try {
                                                                JSONArray jsAdd = jsonObject.getJSONArray(keys);

                                                                for (int ic = 0; ic < jsAdd.length(); ic++) {

                                                                    JSONObject oContent = new JSONObject(jsAdd.get(ic).toString());

                                                                    String val = oContent.getString("v");
                                                                    if (val.length() == 1) {
                                                                        if (val.equalsIgnoreCase("0")) {
                                                                            String not = oContent.getString("n");
                                                                            if (not.length() == 0) {
                                                                                JSONArray aa = new JSONArray();
                                                                                if (oContent.has("f")) {
                                                                                    aa = oContent.getJSONArray("f");
                                                                                }

                                                                                if (aa.length() == 0) {
                                                                                    //diisi 0 dan harus ada salah satu yang di isi
                                                                                    errorReq.add("Mohon isi Note Atau Foto pada Point " + mas + " - " + (ic + 1));
                                                                                    berhenti = true;
                                                                                }
                                                                            }
                                                                        }
                                                                    } else {
                                                                        String not = oContent.getString("n");
                                                                        if (not.length() == 0) {
                                                                            JSONArray aa = new JSONArray();
                                                                            if (oContent.has("f")) {
                                                                                aa = oContent.getJSONArray("f");
                                                                            }

                                                                            if (aa.length() == 0) {
                                                                                errorReq.add("Harap Pilih Cheklist Pada Point " + mas + " - " + (ic + 1));
                                                                                berhenti = true;
                                                                            }
                                                                        }

                                                                    }
                                                                }
                                                            } catch (JSONException e) {
                                                                // Something went wrong!
                                                            }
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                } else if (value.get(2).toString().equalsIgnoreCase("phone_number")) {
                                                    String saya = cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));
                                                    if (saya.length() > 9 && saya.length() < 15) {
                                                        String sss = test_Nom(saya);
                                                        if (sss.equalsIgnoreCase("Nomor anda salah")) {
                                                            berhenti = true;
                                                            String aa = value.get(0).toString();
                                                            et[Integer.valueOf(aa)].setError("Phone not valid");
                                                        }
                                                    } else {
                                                        berhenti = true;
                                                        String aa = value.get(0).toString();
                                                        et[Integer.valueOf(aa)].setError("Phone not valid");
                                                    }
                                                } else if (value.get(2).toString().equalsIgnoreCase("email")) {
                                                    String aa = value.get(0).toString();
                                                    if (!cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).contains("@")) {
                                                        berhenti = true;
                                                        et[Integer.valueOf(aa)].setError("Email not valid");
                                                    }

                                                } else if (value.get(2).toString().equalsIgnoreCase("new_dropdown_dinamis")) {
                                                    if (cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).contains("--Please Select--")) {
                                                        berhenti = true;
                                                        errorReq.add(value.get(4).toString());
                                                    }

                                                } else if (value.get(2).toString().equalsIgnoreCase("load_dropdown")) {
                                                    if (cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).contains("--Please Select--")) {
                                                        berhenti = true;
                                                        errorReq.add(value.get(4).toString());
                                                    }

                                                } else if (value.get(2).toString().equalsIgnoreCase("load_dropdown_k")) {
                                                    if (cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).contains("--Please Select--")) {
                                                        berhenti = true;
                                                        errorReq.add(value.get(4).toString());
                                                    }

                                                } else if (value.get(2).toString().equalsIgnoreCase("dropdown_views")) {
                                                    if (cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).contains("--Please Select--")) {
                                                        berhenti = true;
                                                        errorReq.add(value.get(4).toString());
                                                    }

                                                } else if (value.get(2).toString().equalsIgnoreCase("dropdown")) {
                                                    if (cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).contains("--Please Select--")) {
                                                        berhenti = true;
                                                        errorReq.add(value.get(4).toString());
                                                    }
                                                } else if (value.get(2).toString().equalsIgnoreCase("time")) {
                                                    String aa = value.get(3).toString();
                                                    try {
                                                        JSONObject rt = new JSONObject();
                                                        rt.put("id", aa);
                                                        rt.put("jam", cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)) + "");
                                                        ar.put(rt);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                    validateTime = true;
                                                }
                                            }
                                        } else {
                                            if (value.get(2).toString().equalsIgnoreCase("text") ||
                                                    value.get(2).toString().equalsIgnoreCase("textarea") ||
                                                    value.get(2).toString().equalsIgnoreCase("number") ||
                                                    value.get(2).toString().equalsIgnoreCase("email") ||
                                                    value.get(2).toString().equalsIgnoreCase("phone_number") ||
                                                    value.get(2).toString().equalsIgnoreCase("currency")) {
                                                String aa = value.get(0).toString();
                                                et[Integer.valueOf(aa)].setError("required");
                                                berhenti = true;
                                            } else if (value.get(2).toString().equalsIgnoreCase("attach_api")) {
                                                //tidak ada action
                                            } else {
                                                berhenti = true;
                                                errorReq.add(value.get(4).toString());
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (denganCheck) {
                            if (!linkGetAsignTo.equalsIgnoreCase("")) {
                                if (linearLayout.getChildAt(linearLayout.getChildCount() - 2).getVisibility() == View.VISIBLE) {
                                    Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "assignTo", "");
                                    if (cursorCild.getCount() == 0) {
                                        berhenti = true;
                                        b.setEnabled(true);
                                        errorReq.add("Assign To");
                                    }
                                }
                            }
                        }

                        if (includeStatus) {
                            Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "includeStatus", "");
                            if (cEdit.getCount() == 0) {
                                berhenti = true;
                                b.setEnabled(true);
                                errorReq.add("Status");
                            }

                        }

                        if (validateTime) {
                            if (ar != null) {
                                if (ar.length() == 2) {
                                    String jamMulai = null;
                                    String jamSlsai = null;
                                    for (int o = 0; o < ar.length(); o++) {
                                        try {
                                            String idtem = ar.getJSONObject(o).getString("id");
                                            if (idtem.equalsIgnoreCase("jam_selesai")) {
                                                jamSlsai = ar.getJSONObject(o).getString("jam");
                                            } else {
                                                jamMulai = ar.getJSONObject(o).getString("jam");
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    if (jamMulai != null) {
                                        if (jamSlsai != null) {
                                            if (checkTime(jamMulai, jamSlsai, idDetail) != true) {
                                                ar = new JSONArray();
                                                final AlertDialog.Builder alertbox = new AlertDialog.Builder(DinamicRoomTaskActivity.this);
                                                alertbox.setTitle("Error Input");
                                                alertbox.setMessage("Sudah terdapat aktifitas pada jam tersebut" + "\n");
                                                alertbox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface arg0, int arg1) {
                                                        b.setEnabled(true);
                                                    }
                                                });

                                                alertbox.show();
                                                return;
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (berhenti) {
                            b.setEnabled(true);
                            if (errorReq.size() > 0) {
                                final AlertDialog.Builder alertbox = new AlertDialog.Builder(DinamicRoomTaskActivity.this);
                                alertbox.setTitle("Required");
                                String content = "";
                                for (String ss : errorReq) {
                                    Log.w("samaSaj", ss);
                                    content += ss + "<br/>";
                                }
                                alertbox.setMessage(Html.fromHtml(content) + "\n");
                                alertbox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {

                                    }
                                });

                                alertbox.show();
                            }
                            return;
                        } else {
                            int nom = 0;
                            for (ArrayList<String> innerList : stringAPI) {
                                String param = "";
                                String flag = "";
                                String type = "";
                                String idContent = "";
                                String idCount = "";

                                TextView valueFile = (TextView) linearEstimasi[nom].findViewById(R.id.value);
                                nom++;
                                MessengerDatabaseHelper messengerHelper = null;
                                if (messengerHelper == null) {
                                    messengerHelper = MessengerDatabaseHelper.getInstance(context);
                                }

                                Contact contact = messengerHelper.getMyContact();
                                Date c = Calendar.getInstance().getTime();
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String formattedDate = df.format(c);
                                valueFile.setText(valueFile.getText().toString() + "?outlet_code=" + customersId + "&bc_user=" + contact.getJabberId() + "&date=" + formattedDate.replace(" ", "%20") + "&id_task=" + idDetail);
                                for (int j = 0; j < innerList.size(); j++) {

                                    if (j == 0) {
                                        type = jsonResultType(innerList.get(j), "c");
                                        flag = jsonResultType(innerList.get(j), "b");
                                        idContent = jsonResultType(innerList.get(j), "a");
                                        idCount = jsonResultType(innerList.get(j), "d");
                                    } else {
                                        JSONObject oContent = null;
                                        try {
                                            oContent = new JSONObject(innerList.get(j));
                                            for (Integer keyA : hashMap.keySet()) {
                                                List<String> valueA = hashMap.get(keyA);
                                                if (valueA != null) {
                                                    if (valueA.get(3).toString().equalsIgnoreCase(oContent.getString("key_name").toString())) {
                                                        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(String.valueOf(keyA), valueA.get(2).toString(), valueA.get(5).toString()));
                                                        if (cEdit.getCount() > 0) {
                                                            if (cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).length() > 0) {
                                                                String gaki = cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));
                                                                param += oContent.getString("parameter").toString() + "=" + gaki;
                                                                Cursor cEditA = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idContent, type, flag));
                                                                if (cEditA.getCount() > 0) {
                                                                    if (cEditA.getString(cEditA.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).length() > 0) {
                                                                        valueFile = (TextView) linearEstimasi[Integer.valueOf(idCount)].findViewById(R.id.value);
                                                                        try {
                                                                            if (innerList.size() > 2) {
                                                                                if (j == 1) {
                                                                                    valueFile.setText(valueFile.getText().toString() + "?" + URLEncoder.encode(param, "utf-8"));
                                                                                } else {
                                                                                    valueFile.setText(valueFile.getText().toString() + "&" + URLEncoder.encode(param, "utf-8"));
                                                                                }
                                                                            } else {
                                                                                valueFile.setText(valueFile.getText().toString() + "?" + URLEncoder.encode(param, "utf-8"));
                                                                            }
                                                                        } catch (UnsupportedEncodingException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }
                            }
                        }


                        if (latLong.equalsIgnoreCase("1")) {
                            gps = new GPSTracker(DinamicRoomTaskActivity.this);
                            if (!gps.canGetLocation()) {
                                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQ_LOCATION_SETTING);
                                return;
                            } else {
                                if (gps.canGetLocation()) {
                                    latitude = gps.getLatitude();
                                    longitude = gps.getLongitude();
                                    if (latitude == 0.0 && longitude == 0.0) {
                                        Cursor cursorParent = db.getSingleRoomDetailFormWithFlag(idDetail, username, idTab, "parent");
                                        String latLongResult = "";
                                        if (cursorParent.getCount() > 0) {
                                            latLongResult = jsonCreateType(jsonResultType(cursorParent.getString(cursorParent.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "a"), new NotificationReceiver().simInfo(), "");

                                        }
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, null, "1", latLongResult, "parent");
                                        db.updateDetailRoomWithFlagContentParent(orderModel);

                                    } else {
                                        Cursor cursorParent = db.getSingleRoomDetailFormWithFlag(idDetail, username, idTab, "parent");
                                        String latLongResult = "";
                                        if (cursorParent.getCount() > 0) {
                                            latLongResult = jsonCreateType(jsonResultType(cursorParent.getString(cursorParent.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "a"), String.valueOf(latitude) + "|" + String.valueOf(longitude), "");

                                        }
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, null, "1", latLongResult, "parent");
                                        db.updateDetailRoomWithFlagContentParent(orderModel);
                                    }
                                }
                            }
                        }

                        Cursor cEdit2 = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "utility", "");

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("fromList", fromList);
                            jsonObject.put("calendar", calendar);
                            jsonObject.put("isReject", isReject);
                            jsonObject.put("customersId", customersId);
                            jsonObject.put("startDate", startDate);
                            jsonObject.put("idListTaskMasterForm", idListTaskMasterForm);
                            if (includeStatus) {
                                jsonObject.put("includeStatus", includeStatus);
                                jsonObject.put("labelDone", labelDone);
                                jsonObject.put("labelApprove", labelApprove);
                            }
                            if (!linkGetAsignTo.equalsIgnoreCase("")) {
                                jsonObject.put("linkGetAsignTo", linkGetAsignTo);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (cEdit2.getCount() > 0) {
                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, jsonObject.toString(), "", "", "utility");
                            db.updateDetailRoomWithFlagContent(orderModel);

                        } else {
                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, jsonObject.toString(), "", "", "utility");
                            db.insertRoomsDetail(orderModel);
                        }


                        if (NetworkInternetConnectionStatus.getInstance(context).isOnline(context)) {
                            long date = System.currentTimeMillis();
                            String dateString = hourFormat.format(date);
                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, dateString, "1", null, "parent");
                            db.updateDetailRoomWithFlagContentParent(orderModel);

                            Log.w("disini::", JcontentBawaanReject.length() + "");

                            new AllAboutUploadTask().getInstance(getApplicationContext()).UploadTask(DinamicRoomTaskActivity.this, idDetail, username, idTab, JcontentBawaanReject);
                        } else {
                            long date = System.currentTimeMillis();
                            String dateString = hourFormat.format(date);
                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, dateString, "3", null, "parent");
                            db.updateDetailRoomWithFlagContentParent(orderModel);
                            Toast.makeText(context, "No Internet Akses", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            } else {
                btnSUMBIT.setVisibility(View.GONE);
            }

        } else {

            refreshMethod();

        }

        if (

                getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        focusOnView();

    }

    private void refreshMethod() {

        if (username != null) {
            if (fromList.equalsIgnoreCase("hide")) {
                new Refresh(DinamicRoomTaskActivity.this).execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + GETTABDETAILPULL, username, idTab, idDetail);
            } else if (fromList.equalsIgnoreCase("hideMultiple") || fromList.equalsIgnoreCase("showMultiple")) {
                if (!idDetail.equalsIgnoreCase("")) {
                    String[] ff = idDetail.split("\\|");
                    if (ff.length == 2) {
                        new Refresh(DinamicRoomTaskActivity.this).execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + GETTABDETAILPULLMULTIPLE, username, idTab, idDetail);
                    } else {
                        new Refresh(DinamicRoomTaskActivity.this).execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + GETTABDETAIL, username, idTab, "");
                    }
                } else {
                    if (fromList.equalsIgnoreCase("showMultiple")) {
                        new Refresh(DinamicRoomTaskActivity.this).execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + GETTABDETAIL, username, idTab, "");
                    }
                }
            } else {
                new Refresh(DinamicRoomTaskActivity.this).execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + GETTABDETAIL, username, idTab, "");
            }
        } else {
            finish();
        }

    }

    private static String test_Nom(String notlp) {
        String cekProvider;
        String hasil = "";
        String[] telkomsel = {"62811", "62812", "62813", "62821", "62822", "62823", "62851", "62852", "62853"};
        String[] indosat = {"62814", "62815", "62816", "62855", "62856", "62857", "62858"};
        String[] xl_axis = {"62817", "62818", "62819", "62859", "62877", "62878", "62879", "62831", "62833", "62835", "62836", "62837", "62838", "62839"};
        String[] smartfren = {"6288"};
        String[] three = {"62899", "62898", "62897", "62896", "62895", "62894", "62893"};
        if (notlp.length() > 9 && notlp.length() < 15) {
            if (notlp.matches("0(.*)")) {
                notlp = notlp.replace("0", "62");
                cekProvider = notlp.substring(0, 5);
                for (int i = 0; i < telkomsel.length; i++) {
                    if (cekProvider.equalsIgnoreCase(telkomsel[i])) {
                        hasil = "TELKOMSEL";
                    }
                }
                for (int i = 0; i < indosat.length; i++) {
                    if (cekProvider.equalsIgnoreCase(indosat[i])) {
                        hasil = "INDOSAT";
                    }
                }
                for (int i = 0; i < xl_axis.length; i++) {
                    if (cekProvider.equalsIgnoreCase(xl_axis[i])) {
                        hasil = "XL AXIATA";
                    }
                }
                for (int i = 0; i < three.length; i++) {
                    if (cekProvider.equalsIgnoreCase(three[i])) {
                        hasil = "THREE";
                    }
                }
                for (int i = 0; i < smartfren.length; i++) {
                    if (cekProvider.matches(smartfren[i] + "(.*)")) {
                        hasil = "SMARTFREN";
                    }
                }
            } else if (notlp.matches("62(.*)")) {
                cekProvider = notlp.substring(0, 5);
                for (int i = 0; i < telkomsel.length; i++) {
                    if (cekProvider.equalsIgnoreCase(telkomsel[i])) {
                        hasil = "TELKOMSEL";
                    }
                }
                for (int i = 0; i < indosat.length; i++) {
                    if (cekProvider.equalsIgnoreCase(indosat[i])) {
                        hasil = "INDOSAT";
                    }
                }
                for (int i = 0; i < xl_axis.length; i++) {
                    if (cekProvider.equalsIgnoreCase(xl_axis[i])) {
                        hasil = "XL AXIATA";
                    }
                }
                for (int i = 0; i < three.length; i++) {
                    if (cekProvider.equalsIgnoreCase(three[i])) {
                        hasil = "THREE";
                    }
                }
                for (int i = 0; i < smartfren.length; i++) {
                    if (cekProvider.matches(smartfren[i] + "(.*)")) {
                        hasil = "SMARTFREN";
                    }
                }
            } else {
                hasil = "Nomor anda salah";
            }
        } else {
            hasil = "Nomor anda salah";
        }
        return hasil;
    }

    private void focusOnView() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                String pos = "0";
                if (getIntent().getStringExtra("posisi") != null) {
                    pos = getIntent().getStringExtra("posisi");
                    Log.e("sunnguh errorslow", pos);
                }
                mainScrooll.scrollTo(0, linearLayout.getTop() + Integer.valueOf(pos));
            }
        });
    }

    private String jsonCreateTypeChild(String idContent, String type, String f, String
            idlisttask, String idDetailss) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("a", idContent);
            obj.put("b", type);
            obj.put("c", f);
            obj.put("d", idlisttask);
            obj.put("e", idDetailss);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }


    public void addSpinner(final String jsonValue, final String namedb,
                           final LinearLayout view, final String table, final String[] coloum, final Integer from,
                           final String where, final String idListTask, final String type, final String finalI24,
                           final String name) {
        DataBaseDropDown mDB = new DataBaseDropDown(context, namedb);
        if (mDB.getWritableDatabase() != null) {
            final Integer asIs = from + 1;
            if (asIs < coloum.length) {
                final Cursor c = mDB.getWritableDatabase().query(true, table, new String[]{coloum[asIs]}, where, null, coloum[asIs], null, null, null);
                final ArrayList<String> spinnerArray = new ArrayList<String>();
                if (coloum.length - 1 != asIs) {
                    spinnerArray.add("--Please Select--");
                }
                if (c.moveToFirst()) {
                    do {
                        String column1 = c.getString(0);
                        spinnerArray.add(column1);
                    } while (c.moveToNext());
                }
                c.close();

                LinearLayout spinerTitle = (LinearLayout) getLayoutInflater().inflate(R.layout.item_spiner_textview, null);
                TextView textViewFirst = (TextView) spinerTitle.findViewById(R.id.title);

                String titlle = null;
                try {
                    JSONObject jObjectFirs = new JSONObject(jsonValue);
                    JSONArray titleDropdown = new JSONArray(jObjectFirs.getString("title"));
                    titlle = titleDropdown.get(asIs).toString();
                    textViewFirst.setText(Html.fromHtml(titlle));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                textViewFirst.setTextSize(15);
                final SearchableSpinner spinner = (SearchableSpinner) spinerTitle.findViewById(R.id.spinner);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    spinner.setBackground(getResources().getDrawable(R.drawable.spinner_background));
                }
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item_black, spinnerArray); //selected item will look like a spinner set from XML
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(spinnerArrayAdapter);
                final String finalTitlle = titlle;


                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI24)));
                if (cEdit.getCount() > 0) {
                    try {
                        JSONObject jsonObject = new JSONObject(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                        int spinnerPosition = spinnerArrayAdapter.getPosition(jsonObject.getString(finalTitlle));
                        spinner.setSelection(spinnerPosition);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        final int count = view.getChildCount();
                        view.removeViews(asIs + 1, count - (asIs + 1));
                        if (!spinner.getSelectedItem().toString().equals("--Please Select--")) {
                            addSpinner(jsonValue, namedb, view, table, coloum, asIs, where + " and " + coloum[asIs] + "= '" + spinner.getSelectedItem().toString() + "'", idListTask, type, finalI24, name);

                            Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI24)));
                            if (cEdit.getCount() > 0) {
                                try {
                                    JSONObject jsonObject = new JSONObject(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, function(jsonObject, finalTitlle, spinner.getSelectedItem().toString()).toString(), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "cild");
                                    db.updateDetailRoomWithFlagContent(orderModel);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else {
                                RoomsDetail orderModel = null;
                                try {
                                    orderModel = new RoomsDetail(idDetail, idTab, username, function(null, finalTitlle, spinner.getSelectedItem().toString()).toString(), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "cild");
                                    db.insertRoomsDetail(orderModel);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // your code here
                    }

                });

                view.addView(spinerTitle);
            }
        }
        Intent newIntent = new Intent("bLFormulas");
        sendBroadcast(newIntent);
    }

    public void addSpinnerDinamics(final String[] jsonValue, final String namedb,
                                   final LinearLayout view, final String table, final String[] coloum, final Integer from,
                                   final String where, final String idListTask, final String type, final String finalI24,
                                   final String name) {

        boolean showSpinner = true;
        DataBaseDropDown mDB = new DataBaseDropDown(context, namedb);
        if (mDB.getWritableDatabase() != null) {
            final Integer asIs = from + 1;
            if (asIs < coloum.length) {
                String titlle = jsonValue[asIs];
                final Cursor c = mDB.getWritableDatabase().query(true, table, new String[]{coloum[asIs]}, where, null, coloum[asIs], null, null, null);
                final ArrayList<String> spinnerArray = new ArrayList<String>();

                if (coloum.length - 1 != asIs) {
                    if (c.getCount() > 1) {
                        spinnerArray.add("--Please Select--");
                    }
                    customersId = "";

                } else {
                    showSpinner = false;
                }


                if (c.moveToFirst()) {
                    do {
                        String column1 = c.getString(0);

                        if (column1 != null) {
                            spinnerArray.add(column1);
                            if (!showSpinner) {

                                customersId = column1;


                                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI24)));
                                if (cEdit.getCount() > 0) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, function(jsonObject, titlle, column1).toString(), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "cild");
                                        db.updateDetailRoomWithFlagContent(orderModel);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    RoomsDetail orderModel = null;
                                    try {
                                        orderModel = new RoomsDetail(idDetail, idTab, username, function(null, titlle, column1).toString(), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "cild");
                                        db.insertRoomsDetail(orderModel);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }


                            }
                        } else {
                            Log.w("prefect", c.getCount() + "");
                        }

                    } while (c.moveToNext());
                }
                c.close();

                LinearLayout spinerTitle = (LinearLayout) getLayoutInflater().inflate(R.layout.item_spiner_textview, null);
                TextView textViewFirst = (TextView) spinerTitle.findViewById(R.id.title);


                textViewFirst.setText(Html.fromHtml(titlle));
                textViewFirst.setTextSize(15);
                final SearchableSpinner spinner = (SearchableSpinner) spinerTitle.findViewById(R.id.spinner);
                final TextView textView = (TextView) spinerTitle.findViewById(R.id.lastSpinner);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    spinner.setBackground(getResources().getDrawable(R.drawable.spinner_background));
                }


                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item_black, spinnerArray); //selected item will look like a spinner set from XML
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(spinnerArrayAdapter);
                final String finalTitlle = titlle;


                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI24)));
                if (cEdit.getCount() > 0) {
                    try {
                        JSONObject jsonObject = new JSONObject(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                        int spinnerPosition = spinnerArrayAdapter.getPosition(jsonObject.getString(finalTitlle));
                        spinner.setSelection(spinnerPosition);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        final int count = view.getChildCount();
                        view.removeViews(asIs + 1, count - (asIs + 1));
                        if (!spinner.getSelectedItem().toString().equals("--Please Select--")) {
                            addSpinnerDinamics(jsonValue, namedb, view, table, coloum, asIs, where + " and " + coloum[asIs] + "= '" + spinner.getSelectedItem().toString().replace("'", "''") + "'", idListTask, type, finalI24, name);
                        }

                        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI24)));
                        if (cEdit.getCount() > 0) {
                            try {
                                JSONObject jsonObject = new JSONObject(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, function(jsonObject, finalTitlle, spinner.getSelectedItem().toString()).toString(), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "cild");
                                db.updateDetailRoomWithFlagContent(orderModel);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            RoomsDetail orderModel = null;
                            try {
                                orderModel = new RoomsDetail(idDetail, idTab, username, function(null, finalTitlle, spinner.getSelectedItem().toString()).toString(), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "cild");
                                db.insertRoomsDetail(orderModel);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // your code here
                    }

                });

                if (showSpinner) {
                    spinner.setVisibility(View.VISIBLE);
                } else {
                    textView.setVisibility(View.VISIBLE);
                    spinner.setVisibility(View.GONE);
                    textView.setText(spinnerArray.get(0));
                }
                Intent newIntent = new Intent("bLFormulas");
                sendBroadcast(newIntent);
                view.addView(spinerTitle);
            }
        }
    }


    private void requestLocationInfo(String idDetail, String username, String idTab, String
            idListTask, String type, String name, String f) {
        dummyIdDate = Integer.parseInt(idListTask);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION},
                        100);
            }
        } else {
            gps = new GPSTracker(DinamicRoomTaskActivity.this);
            LocationManager locManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                try {

                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, f));
                    if (cEdit.getCount() == 0) {
                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(idListTask, type, f), name, "cild");
                        db.insertRoomsDetail(orderModel);
                    }


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

    public void captureGalery(String idDetail, String username, String idTab, String
            idListTask, String type, String name, String flag, int facing, String idii) {

        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, idii));
        if (cEdit.getCount() == 0) {
            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(idListTask, type, idii), name, "cild");
            db.insertRoomsDetail(orderModel);
        }

        dummyIdDate = Integer.parseInt(idListTask);

        if (flag.equalsIgnoreCase("live_camera")) {
            if (facing == 1) {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                CameraActivity.Builder start = new CameraActivity.Builder(activity, REQ_CAMERA);
                start.setLockSwitch(CameraActivity.UNLOCK_SWITCH_CAMERA);
                start.setCameraFace(CameraActivity.CAMERA_FRONT);
                start.setFlashMode(CameraActivity.FLASH_OFF);
                start.setQuality(CameraActivity.MEDIUM);
                start.setRatio(CameraActivity.RATIO_4_3);
                start.setFileName(new MediaProcessingUtil().createFileName("jpeg", "ROOM"));
                new Camera(start.build()).lauchCamera();

            } else if (facing == 0) {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                CameraActivity.Builder start = new CameraActivity.Builder(activity, REQ_CAMERA);
                start.setLockSwitch(CameraActivity.UNLOCK_SWITCH_CAMERA);
                start.setCameraFace(CameraActivity.CAMERA_REAR);
                start.setFlashMode(CameraActivity.FLASH_OFF);
                start.setQuality(CameraActivity.MEDIUM);
                start.setRatio(CameraActivity.RATIO_4_3);
                start.setFileName(new MediaProcessingUtil().createFileName("jpeg", "ROOM"));
                new Camera(start.build()).lauchCamera();
            }
        } else if (flag.equalsIgnoreCase("gallery")) {
            showAttachmentDialog(REQ_CAMERA);
        }
        /*else if (flag.equalsIgnoreCase("gallery_camera")) {
            Log.w("luMasihk", "sini");

            showAttachmentDialog(REQ_CAMERA);
        }*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_dinamic_detail, menu);
        return true;
    }


    private void showProgressDialogWithTitle() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("In progress...");
        progressDialog.setMessage("Uploading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(true);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.show();

    }


    private String jsonDuaObject(String a, String b, String c, String d, String ver) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("aa", a);
            obj.put("bb", b);

            if (!c.equalsIgnoreCase("")) {
                obj.put("cc", c);
            }

            if (!d.equalsIgnoreCase("")) {
                obj.put("dd", d);
            }

            obj.put("ver", ver);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }

    private String jsonCheckBox(String id, String content) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", id);
            obj.put("c", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }

    private JSONObject jsonCheckBoxDua(String cusId, String checkId, String titleId, String
            idT, String idS, String val, String note) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", cusId + "|" + titleId + "|" + checkId + "|" + idT + "|" + idS);
            obj.put("val", val);
            obj.put("not", note);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj;
    }


    private String jsoncreateC(String content) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("c", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }

    private String jsonPosCode(String a, String b, String c, String d, String e) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("a", a);
            obj.put("b", b);
            obj.put("c", c);
            obj.put("d", d);
            obj.put("e", e);
        } catch (JSONException error) {
            error.printStackTrace();
        }
        return obj.toString();
    }

    private String jsonCreateType(String idContent, String type, String f) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("a", idContent);
            obj.put("b", type);
            obj.put("c", f);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }

    private String jsonCreateDocument(String idContent, String value) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("a", idContent);
            obj.put("binary", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }

    private String dateTaken(String image, String date) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("a", image);
            obj.put("b", date);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }


    public static JSONObject function(JSONObject obj, String keyMain, String newValue) throws
            Exception {
        if (obj == null) {
            obj = new JSONObject();
            try {
                obj.put(keyMain, newValue);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.w("objeKK", obj.toString());
            return obj;
        } else {
            boolean insert = false;
            JSONObject json = new JSONObject();
            Iterator iterator = obj.keys();
            String key = null;
            while (iterator.hasNext()) {
                key = (String) iterator.next();
                if ((obj.optJSONArray(key) == null) && (obj.optJSONObject(key) == null)) {
                    if ((key.equals(keyMain))) {
                        insert = true;
                        try {
                            obj.put(key, newValue);
                            if (newValue.equalsIgnoreCase("")) {
                                obj.remove(newValue);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.w("objeKK", obj.toString());
                        return obj;
                    }
                }
                if (obj.optJSONObject(key) != null) {
                    function(obj.getJSONObject(key), keyMain, newValue);
                }

                if (insert == false) {
                    try {
                        obj.put(keyMain, newValue);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.w("objeKK", obj.toString());
                    return obj;
                }
            }
        }


        return obj;
    }

    private String jsonResultType(String json, String type) {
        String hasil = "";
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jObject != null) {
            try {
                hasil = jObject.getString(type);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return hasil;
    }

    private void refreshForm() {

        Cursor cursor = db.getSingleRoomDetailForm(username, idTab);
        if (cursor.getCount() > 0) {
            if (!linkGetAsignTo.equalsIgnoreCase("")) {
                DataBaseHelper.getInstance(context).deleteAllrow("room", username, idTab);
            }


            final String conBefore = cursor.getString(cursor.getColumnIndexOrThrow(BotListDB.ROOM_CONTENT));
            String contentDetail = conBefore;
            JSONObject jO = null;
            try {
                jO = new JSONObject(conBefore);
                contentDetail = jO.getString("aa");
                dbMaster = jO.getString("bb");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONArray jsonArrayDetail = null;
            try {
                if (!dbMaster.equalsIgnoreCase("")) {
                    String[] aa = dbMaster.split("/");
                    final String nama = aa[aa.length - 1].toString();
                    File newDB = new File(DataBaseDropDown.getDatabaseFolder() + nama);
                    if (newDB.exists()) {
                        newDB.delete();
                    }
                }

                jsonArrayDetail = new JSONArray(contentDetail);

                for (int ii = 0; ii < jsonArrayDetail.length(); ii++) {
                    String value = jsonArrayDetail.getJSONObject(ii).getString("value").toString();
                    String tt = jsonArrayDetail.getJSONObject(ii).getString("type").toString();
                    if (tt.equalsIgnoreCase("dropdown_dinamis") || tt.equalsIgnoreCase("new_dropdown_dinamis") || tt.equalsIgnoreCase("dropdown_form") || tt.equalsIgnoreCase("form_child")) {
                        if (value.length() > 0) {
                            JSONObject jObject = new JSONObject(value);
                            if (jObject.has("url")) {
                                String url = jObject.getString("url");
                                String[] aa = url.split("/");
                                final String nama = aa[aa.length - 1].toString();

                                File newDB = new File(DataBaseDropDown.getDatabaseFolder() + nama);
                                if (newDB.exists()) {
                                    newDB.delete();
                                }
                            }
                        }

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        db.deleteFormDetail(username, idTab);


        if (username != null) {
            linearLayout.setVisibility(View.GONE);
            if (fromList.equalsIgnoreCase("hide")) {

                new Refresh(DinamicRoomTaskActivity.this).execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + GETTABDETAILPULL, username, idTab, idDetail);
            } else if (fromList.equalsIgnoreCase("hideMultiple") || fromList.equalsIgnoreCase("showMultiple")) {
                if (!idDetail.equalsIgnoreCase("")) {
                    String[] ff = idDetail.split("\\|");
                    if (ff.length == 2) {
                        new Refresh(DinamicRoomTaskActivity.this).execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + GETTABDETAILPULLMULTIPLE, username, idTab, idDetail);
                    } else {
                        new Refresh(DinamicRoomTaskActivity.this).execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + GETTABDETAIL, username, idTab, "");
                    }
                } else {
                    if (fromList.equalsIgnoreCase("showMultiple")) {
                        new Refresh(DinamicRoomTaskActivity.this).execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + GETTABDETAIL, username, idTab, "");
                    }
                }
            } else {
                new Refresh(DinamicRoomTaskActivity.this).execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + GETTABDETAIL, username, idTab, "");
            }
        } else {
            finish();
        }

    }

    public static String removeJson(final int idx, final JSONArray from) {
        String hasil = "";
        final List<JSONObject> objs = asList(from);
        final JSONArray ja = new JSONArray();
        for (final JSONObject obj : objs) {
            try {
                if (!obj.getString("id").equalsIgnoreCase(String.valueOf(idx))) {
                    ja.put(obj);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        hasil = ja.toString().substring(1, ja.toString().length() - 1);

        return hasil;
    }

    public static List<JSONObject> asList(final JSONArray ja) {
        final int len = ja.length();
        final ArrayList<JSONObject> result = new ArrayList<JSONObject>(len);
        for (int i = 0; i < len; i++) {
            final JSONObject obj = ja.optJSONObject(i);
            if (obj != null) {
                result.add(obj);
            }
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 11) {
            if (resultCode == RESULT_OK) {
                String returnString = data.getStringExtra("PICTURE");
                if (decodeFile(returnString)) {
                    final File f = new File(returnString);
                    if (f.exists()) {
                        FileInputStream inputStream = null;
                        try {
                            inputStream = new FileInputStream(f);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", valueIdValue.getTypes());
                        if (cEdit.getCount() > 0) {
                            String text = cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));

                            JSONObject lala = null;
                            try {
                                lala = new JSONObject(text);
                                JSONObject jsonObject = new JSONObject(valueIdValue.getExpandedListText());
                                JSONArray jj = lala.getJSONArray(jsonObject.getString("iT"));

                                JSONObject oContent = jj.getJSONObject(valueIdValue.getExpandedListPosition());

                                if (oContent.has("f")) {
                                    JSONArray jsonArray = oContent.getJSONArray("f");
                                    JSONObject jjl = new JSONObject();
                                    jjl.put("r", returnString.substring(returnString.toString().lastIndexOf('/'), returnString.toString().length()));
                                    jjl.put("u", "");
                                    jsonArray.put(jjl);
                                    oContent.put("f", jsonArray);
                                } else {
                                    JSONArray jsonArray = new JSONArray();
                                    JSONObject jjl = new JSONObject();
                                    jjl.put("r", returnString.substring(returnString.toString().lastIndexOf('/'), returnString.toString().length()));
                                    jjl.put("u", "");
                                    jsonArray.put(jjl);
                                    oContent.put("f", jsonArray);
                                }
                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, lala.toString(), valueIdValue.getTypes(), valueIdValue.getName(), "cild");
                                db.updateDetailRoomWithFlagContent(orderModel);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if (idListTaskMasterForm.equalsIgnoreCase("66986")) {
                            ExpandableListAdapter ancur = (ExpandableListAdapter) expandableListView[0].getExpandableListAdapter();
                            ancur.notifyDataSetChanged();
                        } else {
                            ExpandableListAdapter ancur = (ExpandableListAdapter) expandableListView[1].getExpandableListAdapter();
                            ancur.notifyDataSetChanged();
                        }

                    }

                } else {
                    Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
                }

            } else if (resultCode == RESULT_CANCELED) {
                if (result == null) {
                    //     btnPhoto.setVisibility(View.VISIBLE);
                }
                Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
            } else {
                if (result == null) {
                    // btnPhoto.setVisibility(View.VISIBLE);
                }

                Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == 12) {
            if (resultCode == RESULT_OK) {
                Uri selectedUri = data.getData();
                String selectedImagePath = ImageFilePath.getPath(getApplicationContext(), selectedUri);

                if (decodeFile(selectedImagePath)) {
                    final File f = new File(selectedImagePath);

                    InputStream in = null;
                    OutputStream out = null;
                    try {

                        //create output directory if it doesn't exist
                        File dir = new File("storage/emulated/0/Pictures/com.byonchat.android");
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }


                        in = new FileInputStream(selectedImagePath);
                        out = new FileOutputStream("storage/emulated/0/Pictures/com.byonchat.android/" + selectedImagePath.substring(selectedImagePath.toString().lastIndexOf('/'), selectedImagePath.toString().length()));

                        byte[] buffer = new byte[1024];
                        int read;
                        while ((read = in.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                        }
                        in.close();
                        in = null;

                        // write the output file (You have now copied the file)
                        out.flush();
                        out.close();
                        out = null;

                        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", valueIdValue.getTypes());
                        if (cEdit.getCount() > 0) {
                            String text = cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));

                            JSONObject lala = null;
                            try {
                                lala = new JSONObject(text);
                                JSONObject jsonObject = new JSONObject(valueIdValue.getExpandedListText());
                                JSONArray jj = lala.getJSONArray(jsonObject.getString("iT"));

                                JSONObject oContent = jj.getJSONObject(valueIdValue.getExpandedListPosition());

                                if (oContent.has("f")) {
                                    JSONArray jsonArray = oContent.getJSONArray("f");
                                    JSONObject jjl = new JSONObject();
                                    jjl.put("r", selectedImagePath.substring(selectedImagePath.toString().lastIndexOf('/'), selectedImagePath.toString().length()));
                                    jjl.put("u", "");
                                    jsonArray.put(jjl);
                                    oContent.put("f", jsonArray);
                                } else {
                                    JSONArray jsonArray = new JSONArray();
                                    JSONObject jjl = new JSONObject();
                                    jjl.put("r", selectedImagePath.substring(selectedImagePath.toString().lastIndexOf('/'), selectedImagePath.toString().length()));
                                    jjl.put("u", "");
                                    jsonArray.put(jjl);
                                    oContent.put("f", jsonArray);
                                }
                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, lala.toString(), valueIdValue.getTypes(), valueIdValue.getName(), "cild");
                                db.updateDetailRoomWithFlagContent(orderModel);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    } catch (FileNotFoundException fnfe1) {
                        Log.e("tag", fnfe1.getMessage());
                    } catch (Exception e) {
                        Log.e("tag", e.getMessage());
                    }

                    ExpandableListAdapter ancur = (ExpandableListAdapter) expandableListView[1].getExpandableListAdapter();
                    ancur.notifyDataSetChanged();

                } else {
                    Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
                }

            } else if (resultCode == RESULT_CANCELED) {
                if (result == null) {
                    //     btnPhoto.setVisibility(View.VISIBLE);
                }
                Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
            } else {
                if (result == null) {
                    // btnPhoto.setVisibility(View.VISIBLE);
                }

                Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == REQ_CAMERA) {
            List value = (List) hashMap.get(dummyIdDate);
            if (resultCode == RESULT_OK) {
                String returnString = data.getStringExtra("PICTURE");
                if (decodeFile(returnString)) {
                    final File f = new File(returnString);
                    if (f.exists()) {
                        FileInputStream inputStream = null;
                        try {
                            inputStream = new FileInputStream(f);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        result = MediaProcessingUtil.decodeSampledBitmapFromResourceMemOpt(inputStream, 800,
                                800);

                        imageView[Integer.valueOf(value.get(0).toString())].setImageBitmap(result);
                        String myBase64Image = encodeToBase64(result, Bitmap.CompressFormat.JPEG, 80);

                        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                        if (cEdit.getCount() > 0) {
                            SimpleDateFormat dateFormatNew = new SimpleDateFormat(
                                    "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                            long date = System.currentTimeMillis();
                            String dateString = dateFormatNew.format(date);

                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, dateTaken(myBase64Image, dateString), jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                            db.updateDetailRoomWithFlagContent(orderModel);
                        }

                        f.delete();
                    }

                } else {
                    Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
                }

            } else if (resultCode == RESULT_CANCELED) {
                if (result == null) {
                    //     btnPhoto.setVisibility(View.VISIBLE);
                }


                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                if (cEdit.getCount() > 0) {
                    if (cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).equalsIgnoreCase("")) {
                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                        db.deleteDetailRoomWithFlagContent(orderModel);
                    }
                }


                Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
            } else {
                if (result == null) {
                    // btnPhoto.setVisibility(View.VISIBLE);
                }


                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                if (cEdit.getCount() > 0) {
                    if (cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).equalsIgnoreCase("")) {
                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                        db.deleteDetailRoomWithFlagContent(orderModel);
                    }
                }

                Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == SIGNATURE_ACTIVITY) {
            List value = (List) hashMap.get(dummyIdDate);
            if (resultCode == RESULT_OK) {
                String result = data.getExtras().getString("status");

                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                if (cEdit.getCount() > 0) {
                    RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, result, jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                    db.updateDetailRoomWithFlagContent(orderModel);
                }

                imageView[Integer.valueOf(value.get(0).toString())].setImageBitmap(decodeBase64(result));
            } else {


                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                if (cEdit.getCount() > 0) {
                    if (cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).equalsIgnoreCase("")) {
                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                        db.deleteDetailRoomWithFlagContent(orderModel);
                        imageView[Integer.valueOf(value.get(0).toString())].setImageDrawable(getResources().getDrawable(R.drawable.ico_signature));
                    }
                }

            }
        } else if (requestCode == QRCODE_REQUEST) {

            List value = (List) hashMap.get(dummyIdDate);
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    return;
                }
                String rrrr = data.getStringExtra("SCAN_RESULT");

                EditText textView = (EditText) linearEstimasi[Integer.valueOf(value.get(0).toString())].findViewById(R.id.value);
                textView.setText(rrrr);

                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                if (cEdit.getCount() > 0) {
                    RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, rrrr, jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                    db.updateDetailRoomWithFlagContent(orderModel);
                }


            }
            rrr();
        } else if (requestCode == PICK_FILE_REQUEST) {
            List value = (List) hashMap.get(dummyIdDate);
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    return;
                }
                Uri selectedFileUri = data.getData();

                File ee = new File(ImageFilePath.getPath(this, selectedFileUri));

                String fileName = selectedFileUri.toString().substring(selectedFileUri.toString().lastIndexOf('/'), selectedFileUri.toString().length());
                if (ee.exists()) {
                    String myBase64Image = encodeToBase64File(ee);
                    TextView textView = (TextView) linearEstimasi[Integer.valueOf(value.get(0).toString())].findViewById(R.id.value);
                    textView.setText(fileName.replace("/", ""));

                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                    if (cEdit.getCount() > 0) {
                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, jsonCreateDocument(fileName.replace("/", ""), myBase64Image), jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                        db.updateDetailRoomWithFlagContent(orderModel);
                    }

                } else {
                    Toast.makeText(this, "No such file or directory", Toast.LENGTH_SHORT).show();
                }

            }/*else{
                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                if (cEdit.getCount() > 0) {
                    if(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).equalsIgnoreCase("")){
                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                        db.deleteDetailRoomWithFlagContent(orderModel);
                    }
                }

            }*/


        } else if (requestCode == CAMERA_SCAN_TEXT) {
            if (resultCode == CommonStatusCodes.SUCCESS_CACHE) {
                String returnString = data.getStringExtra("PICTURE");
                if (decodeFile(returnString)) {
                    final File f = new File(returnString);
                    if (f.exists()) {
                        FileInputStream inputStream = null;
                        try {
                            inputStream = new FileInputStream(f);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        result = MediaProcessingUtil.decodeSampledBitmapFromResourceMemOpt(inputStream, 1200,
                                900);

                        String text = OCRCapture.Builder(this).getTextFromBitmap(result);
                        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
                        try {
                            List value = (List) hashMap.get(dummyIdDate);
                            String lala = getNIKKTP(text);
                            et[Integer.valueOf(value.get(0).toString())].setText(lala);
                            int pos = 0;
                            if (et[Integer.valueOf(value.get(0).toString())].length() > 1) {
                                pos = et[Integer.valueOf(value.get(0).toString())].length() - 1;
                            }

                            et[Integer.valueOf(value.get(0).toString())].setSelection(pos);
                            String urlString = "https://infopemilu.kpu.go.id/pilkada2018/pemilih/dps/1/hasil-cari/resultDps.json?nik=" + lala
                                    + "&nama=&namaPropinsi=&namaKabKota=&namaKecamatan=&namaKelurahan=&notificationType=";

                            new getJSONeKtp(urlString).execute();
                        } catch (Exception e) {

                        }

                        SimpleDateFormat dateFormatNew = new SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                        long date = System.currentTimeMillis();
                        String dateString = dateFormatNew.format(date);

                        saveToInternalStorage(result, dateString);

                        f.delete();
                    }

                } else {
                    Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, " We Can't found Your NIK, Please retry scanning process or Input your NIK manually ", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == OCR_REQUEST) {
            if (data.hasExtra("result")) {
                final List value = (List) hashMap.get(dummyIdDate);
                String result = data.getExtras().getString("result");
                Toast.makeText(this, "Success scan", Toast.LENGTH_SHORT).show();
                String lines[] = result.split("\\r?\\n");
                List<String> valSetOne = new ArrayList<String>();
                for (String isi : lines) {
                    valSetOne.add(isi);
                }
                hashMapOcr.put(Integer.valueOf(value.get(0).toString()), valSetOne);
            }
        } else if (requestCode == PICK_ESTIMATION) {
            List value = (List) hashMap.get(dummyIdDate);
            if (resultCode == RESULT_OK) {
                if (data.hasExtra("result")) {
                    String result = data.getExtras().getString("result");

                    TextView start = (TextView) linearEstimasi[Integer.valueOf(value.get(0).toString())].findViewById(R.id.valuePickup);
                    TextView end = (TextView) linearEstimasi[Integer.valueOf(value.get(0).toString())].findViewById(R.id.valueEnd);
                    TextView jarak = (TextView) linearEstimasi[Integer.valueOf(value.get(0).toString())].findViewById(R.id.valueJarak);


                    String[] latlongS = jsonResultType(result, "s").split(
                            Message.LOCATION_DELIMITER);
                    if (latlongS.length > 4) {
                        String text = "<u><b>" + (String) latlongS[2] + "</b></u><br/>";
                        start.setText(Html.fromHtml(text + latlongS[3]));
                    }

                    String[] latlongE = jsonResultType(result, "e").split(
                            Message.LOCATION_DELIMITER);
                    if (latlongE.length > 4) {
                        String text = "<u><b>" + (String) latlongE[2] + "</b></u><br/>";
                        end.setText(Html.fromHtml(text + latlongE[3]));
                    }
                    jarak.setText(jsonResultType(result, "d"));

                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                    if (cEdit.getCount() > 0) {
                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, result, jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                        db.updateDetailRoomWithFlagContent(orderModel);
                    }

                    Intent newIntent = new Intent("bLFormulas");
                    sendBroadcast(newIntent);
                }
            } else {

                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                if (cEdit.getCount() > 0) {
                    if (cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).equalsIgnoreCase("")) {
                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                        db.deleteDetailRoomWithFlagContent(orderModel);
                    }
                }

            }

        } else if (requestCode == REQ_VIDEO) {
            List value = (List) hashMap.get(dummyIdDate);
            if (resultCode == RESULT_OK) {

                String videoFile = data.getStringExtra("Video");
                String filePath = (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + "ByonChat"
                        + File.separator + "Videos" + File.separator + videoFile);
                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
                imageView[Integer.valueOf(value.get(0).toString())].setImageBitmap(thumb);

                Bitmap bmp1 = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
                Bitmap bmp2 = BitmapFactory.decodeResource(getResources(), R.drawable.play_button);
                imageView[count].setImageBitmap(overlay(bmp1, bmp2));

                File f = new File(filePath);
                if (f.exists()) {
                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                    if (cEdit.getCount() > 0) {
                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, f.getAbsolutePath(), jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                        db.updateDetailRoomWithFlagContent(orderModel);
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                if (result == null) {
                }
                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                if (cEdit.getCount() > 0) {
                    if (cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).equalsIgnoreCase("")) {
                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                        db.deleteDetailRoomWithFlagContent(orderModel);
                    }
                }
                Toast.makeText(this, " Video was not taken ", Toast.LENGTH_SHORT).show();
            } else {
                if (result == null) {
                }
                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                if (cEdit.getCount() > 0) {
                    if (cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).equalsIgnoreCase("")) {
                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                        db.deleteDetailRoomWithFlagContent(orderModel);
                    }
                }
                Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PLACE_PICKER_REQUEST) {
            showDialog = true;
            final List value = (List) hashMap.get(dummyIdDate);
            if (resultCode == Activity.RESULT_OK) {
                final Place place = PlacePicker.getPlace(data, this);
                final String name = place.getName() != null ? (String) place.getName() : " ";
                final String address = place.getAddress() != null ? (String) place.getAddress() : " ";
                final String web = String.valueOf(place.getWebsiteUri() != null ? place.getWebsiteUri() : " ");


                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                if (cEdit.getCount() > 0) {
                    RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, place.getLatLng().latitude + ";" + place.getLatLng().longitude + ";" + name + ";" + address + ";" + web + ";", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                    db.updateDetailRoomWithFlagContent(orderModel);
                }


                String text = "<u><b>" + name + "</b></u><br/>";
                et[Integer.valueOf(value.get(0).toString())].setText(Html.fromHtml(text + address));

                Intent newIntent = new Intent("bLFormulas");
                sendBroadcast(newIntent);

            } else {

                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                if (cEdit.getCount() > 0) {
                    if (cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).equalsIgnoreCase("")) {
                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                        db.deleteDetailRoomWithFlagContent(orderModel);
                    }
                }

            }


        } else if (requestCode == 12011) {
            Intent p = new Intent("SOME_ACTION");
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    String returnString = data.getStringExtra("PICTURE");
                    p.putExtra("data", returnString);
                }
            } else {
                p.putExtra("data", "");
            }
            LocalBroadcastManager.getInstance(context).sendBroadcast(p);
        } else if (requestCode == 77558 || requestCode == 12022) {
            Intent p = new Intent("SOME_ACTION");
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Uri selectedUri = data.getData();
                    String selectedImagePath = ImageFilePath.getPath(getApplicationContext(), selectedUri);
                    p.putExtra("data", selectedImagePath);


                }
            } else {
                p.putExtra("data", "");
            }
            LocalBroadcastManager.getInstance(context).sendBroadcast(p);

        } else {
            /*if (resultCode == RESULT_OK) {

                Uri selectedUri = data.getData();
                String selectedImagePath = ImageFilePath.getPath(context, selectedUri);
                File fileOutput = new File(selectedImagePath);
                if (requestCode == REQ_VIDEO) {

                } else if (requestCode == REQ_GALLERY
                        || requestCode == REQ_GALLERY_VIDEO) {
                    String type = Message.TYPE_VIDEO;
                    if (requestCode == REQ_GALLERY) {
                        type = Message.TYPE_IMAGE;
                        *//*if (fileOutput.length() > 1000000L) {
                            File f = resizeImage(fileOutput, true);
                            fileOutput = f;
                        }*//*
                    }
                    if (fileOutput.exists()) {
                        FileInputStream inputStream = null;
                        try {
                            inputStream = new FileInputStream(fileOutput);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        result = MediaProcessingUtil.decodeSampledBitmapFromResourceMemOpt(inputStream, 800,
                                800);
                        List value = (List) hashMap.get(dummyIdDate);
                        imageView[Integer.valueOf(value.get(0).toString())].setImageBitmap(result);
                        String myBase64Image = encodeToBase64(result, Bitmap.CompressFormat.JPEG, 80);


                        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                        if (cEdit.getCount() > 0) {
                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, myBase64Image, jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                            db.updateDetailRoomWithFlagContent(orderModel);
                        }


                    }
                }
            } else {
                Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
            }*/
            if (resultCode == RESULT_OK) {
                if (requestCode == REQ_GALLERY || requestCode == REQ_GALLERY_VIDEO) {
                    String type = Message.TYPE_VIDEO;
                    Uri selectedUri = data.getData();
                    String selectedImagePath = ImageFilePath.getPath(getApplicationContext(), selectedUri);
                    File fileOutput = new File(selectedImagePath);
                    if (requestCode == REQ_GALLERY) {
                        type = Message.TYPE_IMAGE;
                        /*if (fileOutput.length() > 1000000L) {
                            File f = resizeImage(fileOutput, true);
                            fileOutput = f;
                        }*/
                    }
                    if (fileOutput.exists()) {
                        FileInputStream inputStream = null;
                        try {
                            inputStream = new FileInputStream(fileOutput);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        result = MediaProcessingUtil.decodeSampledBitmapFromResourceMemOpt(inputStream, 800,
                                800);
                        List value = (List) hashMap.get(dummyIdDate);
                        imageView[Integer.valueOf(value.get(0).toString())].setImageBitmap(result);
                        String myBase64Image = encodeToBase64(result, Bitmap.CompressFormat.JPEG, 80);

                        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                        if (cEdit.getCount() > 0) {
                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, myBase64Image, jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                            db.updateDetailRoomWithFlagContent(orderModel);
                        }
                    }
                }
            } else {
                Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onTaskProses(String response) {
        showProgressDialogWithTitle();
    }

    @Override
    public void onTaskUpdate(int progres, String message) {
        if (progressDialog.isIndeterminate()) {
            progressDialog.setIndeterminate(false);
        }
        progressDialog.setProgress(progres);
        if (!message.equalsIgnoreCase("")) {
            progressDialog.setMessage(message);
        }
    }

    @Override
    public void onTaskCompleted(int response, String message) {
        DinamicRoomTaskActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                progressDialog.dismiss();
                if (response == 0) {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    finish();
                } else if (response == 1) {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    finish();
                } else if (response == 20) {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    finish();
                } else if (response == 50) {
                    AlertDialog.Builder builder = DialogUtil.generateAlertDialog(DinamicRoomTaskActivity.this,
                            "Warning", message);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @TargetApi(11)
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    b.setEnabled(true);
                                    dialog.cancel();
                                }
                            }
                    );
                    builder.show();
                }
            }
        });
    }

    class getKnjngnJson extends AsyncTask<String, Void, String> {

        private String urlStr;
        private ArrayAdapter<String> adapter;

        ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();

        public getKnjngnJson(String url, ArrayAdapter<String> adapter) {
            this.urlStr = url;
            this.adapter = adapter;
        }

        @Override
        protected String doInBackground(String... strings) {
            kunjunganList.clear();
            HashMap<String, String> data = new HashMap<>();
            data.put("bc_user", strings[0]);

            String result = profileSaveDescription.sendPostRequest(urlStr, data);
            try {

                JSONArray parentArr = new JSONArray(result);
                for (int iv = 0; iv < parentArr.length(); iv++) {
                    JSONObject objParent = parentArr.getJSONObject(iv);
                    Iterator<String> iter = objParent.keys();
                    while (iter.hasNext()) {
                        String key = iter.next();
                        valuesKnjngnOne.add(key);
                        try {
                            ArrayList<String> temp = new ArrayList<>();
                            temp.add("--Please Select--");
                            JSONArray arrChild = objParent.getJSONArray(key);
                            for (int an = 0; an < arrChild.length(); an++) {
                                String child = arrChild.getString(an);
                                temp.add(child);
                            }
                            kunjunganList.add(new Kunjungan(key, temp));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            adapter.notifyDataSetChanged();
            super.onPostExecute(result);
        }
    }

    class Kunjungan {

        String name;
        ArrayList<String> daleman;

        public Kunjungan(String name, ArrayList<String> daleman) {
            this.name = name;
            this.daleman = daleman;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ArrayList<String> getDaleman() {
            return daleman;
        }

        public void setDaleman(ArrayList<String> daleman) {
            this.daleman = daleman;
        }
    }


    private class getJSONeKtp extends AsyncTask<String, Void, String> {
        private String vug;
        private ProgressDialog dd;

        private getJSONeKtp(String text) {
            this.vug = text;
            dd = new ProgressDialog(activity);
            dd.setMessage("Please Wait");
            dd.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            return GET(vug);
        }

        @Override
        protected void onPostExecute(String result) {
            dd.dismiss();
            setData(result);
        }
    }

    public void setData(String result) {
        try {
            JSONObject start = new JSONObject(result);
            JSONArray data = start.getJSONArray("aaData");
            JSONObject jsonObject = data.getJSONObject(0);

            String nik = jsonObject.getString("nik");
            String tanggal = nik.substring(6, 8);
            String bulan = nik.substring(8, 10);
            String tahun = nik.substring(10, 12);

            String a = jsonObject.getString("nama");
            String b = jsonObject.getString("jenisKelamin");
            String c = jsonObject.getString("namaKelurahan");
            String d = jsonObject.getString("namaKecamatan");
            String e = jsonObject.getString("namaKabKota");
            String g = jsonObject.getString("namaPropinsi");
            String f = tanggal + "-" + bulan + "-" + tahun;

            String isi = hashMapOcrKTP.get(dummyIdDate);

            JSONArray jsonArrays = new JSONArray(isi);

            for (int ia = 0; ia < jsonArrays.length(); ia++) {
                String valueKTP = jsonArrays.getJSONObject(ia).getString("value").toString();
                final String pairKTP = jsonArrays.getJSONObject(ia).getString("pairs").toString();

                for (Integer key : hashMap.keySet()) {
                    List<String> va = hashMap.get(key);
                    if (va.get(3).equalsIgnoreCase(pairKTP)) {
                        String lala = "";
                        if (valueKTP.equalsIgnoreCase("nama")) {
                            lala = a;
                        } else if (valueKTP.equalsIgnoreCase("jenis_kelamin")) {
                            lala = b;
                        } else if (valueKTP.equalsIgnoreCase("provinsi")) {
                            lala = g;
                        } else if (valueKTP.equalsIgnoreCase("kota")) {
                            lala = e;
                        } else if (valueKTP.equalsIgnoreCase("kelurahan")) {
                            lala = c;
                        } else if (valueKTP.equalsIgnoreCase("kecamatan")) {
                            lala = d;
                        } else if (valueKTP.equalsIgnoreCase("tanggal")) {
                            lala = f;
                        }
                        if (va.get(2).equalsIgnoreCase("radio")) {

                            int radio_button_Id = 0;
                            if (lala.contains("L")) {
                                radio_button_Id = rg[Integer.valueOf(va.get(0).toString())].getChildAt(0).getId();
                            } else {
                                radio_button_Id = rg[Integer.valueOf(va.get(0).toString())].getChildAt(1).getId();
                            }
                            rg[Integer.valueOf(va.get(0).toString())].check(radio_button_Id);

                        } else {
                            et[Integer.valueOf(va.get(0).toString())].setText(lala);
                        }

                    }

                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(activity, "KTP input manual", Toast.LENGTH_SHORT).show();
        }
    }


    public String getNIKKTP(String text) {
        String[] array = text.split("\n");
        boolean error = true;
        for (int oi = 0; oi < array.length; oi++) {
            if (array[oi].matches(": (.*)")) {
                array[oi] = array[oi].replace(": ", "");
            }
            if (array[oi].matches(":(.*)")) {
                array[oi] = array[oi].replace(":", "");
            }
            if (array[oi].matches("[0-9](.*)[0-9]")) {
                if (!array[oi].contains("-")) {
                    if (array[oi].length() > 10) {
                        String NIK = array[oi];
                        error = false;
                        if (NIK.contains(",")) {
                            NIK = NIK.replace(",", "");
                        }
                        if (NIK.contains(".")) {
                            NIK = NIK.replace(".", "");
                        }
                        if (NIK.contains(" ")) {
                            NIK = NIK.replace(" ", "");
                        }
                        if (NIK.contains("S")) {
                            NIK = NIK.replace("S", "5");
                        }
                        if (NIK.contains("L")) {
                            NIK = NIK.replace("L", "6");
                        }
                        if (NIK.contains("v")) {
                            NIK = NIK.replace("v", "4");
                        }
                        if (NIK.contains("O")) {
                            NIK = NIK.replace("O", "0");
                        }
                        if (NIK.contains("b")) {
                            NIK = NIK.replace("b", "6");
                        }
                        if (NIK.contains("G")) {
                            NIK = NIK.replace("G", "6");
                        }
                        if (NIK.contains("T")) {
                            NIK = NIK.replace("T", "9");
                        }
                        if (NIK.contains("o")) {
                            NIK = NIK.replace("o", "0");
                        }
                        if (NIK.contains("?")) {
                            NIK = NIK.replace("?", "7");
                        }
                        if (NIK.contains("%")) {
                            NIK = NIK.replace("%", "6");
                        }
                        if (NIK.contains("D")) {
                            NIK = NIK.replace("D", "0");
                        }
                        return NIK;
                    }
                }
            }
        }
        return "";
    }


    public static Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        try {
            int maxWidth = (bmp1.getWidth() > bmp2.getWidth() ? bmp1.getWidth() : bmp2.getWidth());
            int maxHeight = (bmp1.getHeight() > bmp2.getHeight() ? bmp1.getHeight() : bmp2.getHeight());

            Bitmap bmOverlay = Bitmap.createBitmap(maxWidth, maxHeight, bmp1.getConfig());
            Canvas canvas = new Canvas(bmOverlay);
            canvas.drawBitmap(bmp1, 0, 0, null);
            canvas.drawBitmap(bmp2, 135, 400, null);
            return bmOverlay;

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_refresh:
                final AlertDialog.Builder alertbox = new AlertDialog.Builder(DinamicRoomTaskActivity.this);
                alertbox.setMessage("Are you sure you want to refresh this form?");
                alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        refreshForm();
                    }
                });
                alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
                alertbox.show();


                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
    public void onBackPressed() {
        if (showButton) {
            final AlertDialog.Builder alertbox = new AlertDialog.Builder(DinamicRoomTaskActivity.this);
            alertbox.setMessage("Are you sure you want to save?");
            alertbox.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    finish();
                }
            });
            alertbox.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    db.deleteRoomsDetailbyId(idDetail, idTab, username);
                    if (calendar != null) {
                        if (calendar.equalsIgnoreCase("true boi")) {
                            MyEventDatabase database = new MyEventDatabase(context);
                            SQLiteDatabase db;
                            db = database.getWritableDatabase();
                            String[] args = {idDetail};
                            db.delete(MyEventDatabase.TABLE_EVENT, MyEventDatabase.EVENT_ID_DETAIL + "=?", args);
                            db.close();
                        }
                    }
                    finish();
                }
            });
            alertbox.show();
        } else {
            finish();
        }
    }

    @Override
    public void onTokenAdded(Object o) {
        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "assignTo", "");
        if (cEdit.getCount() > 0) {
            String cc = cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));
            if (!cc.contains(o.toString())) {
                cc += "," + o.toString();
                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, cc, "", "", "assignTo");
                db.updateDetailRoomWithFlagContentNew(orderModel);
            }

        } else {
            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, o.toString(), "", "", "assignTo");
            db.insertRoomsDetail(orderModel);
        }

    }

    @Override
    public void onTokenRemoved(Object o) {
        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "assignTo", "");
        if (cEdit.getCount() > 0) {
            String cc = cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));
            String[] su = cc.split(",");

            if (su.length == 1) {
                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, o.toString(), "", "", "assignTo");
                db.deleteDetailRoomWithFlagContentNew(orderModel);
            } else {
                String has = "";
                for (String ss : su) {
                    if (!ss.equalsIgnoreCase(o.toString())) {
                        if (has.length() == 0) {
                            has = ss;
                        } else {
                            has += "," + ss;
                        }
                    }
                }
                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, has, "", "", "assignTo");
                db.updateDetailRoomWithFlagContentNew(orderModel);
            }
        }

    }

    public void yourActivityMethod(AddChildFotoExModel ahla) {
        //mainan disini
        valueIdValue = ahla;

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

       /* AnncaConfiguration.Builder photo = new AnncaConfiguration.Builder(activity, 11);
        photo.setMediaAction(AnncaConfiguration.MEDIA_ACTION_PHOTO);
        photo.setMediaQuality(AnncaConfiguration.MEDIA_QUALITY_MEDIUM);
        photo.setCameraFace(AnncaConfiguration.CAMERA_FACE_REAR);
        photo.setMediaResultBehaviour(AnncaConfiguration.PREVIEW);
        new Annca(photo.build()).launchCamera();*/

        showAttachmentDialogNew(11);
    }


    private class Refresh extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        String error = "";
        private Activity activity;
        private Context context;

        public Refresh(Activity activity) {
            this.activity = activity;
            context = activity;
            dialog = new ProgressDialog(activity);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Loading...");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            postData(params[0], params[1], params[2], params[3]);
            return null;
        }

        protected void onPostExecute(String result) {

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            finish();
            if (error.length() > 0) {
                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
            } else {
                startActivity(getIntent());
                Toast.makeText(context, "Form success download.", Toast.LENGTH_SHORT).show();
            }

        }

        protected void onProgressUpdate(String... string) {
        }

        public void postData(String valueIWantToSend, String usr, String idr, String pId) {
            // Create a new HttpClient and Post Header

            try {
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 13000);
                HttpConnectionParams.setSoTimeout(httpParameters, 15000);
                HttpClient httpclient = new DefaultHttpClient(httpParameters);
                HttpPost httppost = new HttpPost(valueIWantToSend);

                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("username_room", usr));
                nameValuePairs.add(new BasicNameValuePair("id_rooms_tab", idr));

                if (pId != null || !pId.equalsIgnoreCase("")) {
                    String[] ff = pId.split("\\|");
                    if (ff.length == 2) {
                        nameValuePairs.add(new BasicNameValuePair("parent_id", ff[1]));
                        nameValuePairs.add(new BasicNameValuePair("id_list_push", ff[0]));
                    }
                }

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);

                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Calendar cal = Calendar.getInstance();
                        String time_str = dateFormat.format(cal.getTime());
                        JSONObject jsonRootObject = new JSONObject(data);
                        String username = jsonRootObject.getString("username_room");
                        String id_rooms_tab = jsonRootObject.getString("id_rooms_tab");

                        String attachment = jsonRootObject.getString("attachment");
                        String content = jsonRootObject.getString("data");
                        String include_assignto = jsonRootObject.getString("include_assignto");

                        JSONObject jsonObject = new JSONObject();
                        if (data.contains("include_status_task")) {
                            String include_status_task = jsonRootObject.getString("include_status_task");
                            jsonObject.put("status_task", include_status_task);
                        }


                        if (jsonRootObject.has("label_status_approve")) {
                            String label_status_approve = jsonRootObject.getString("label_status_approve");
                            jsonObject.put("approve", label_status_approve);
                        }

                        if (jsonRootObject.has("label_status_reject")) {
                            String label_status_reject = jsonRootObject.getString("label_status_reject");
                            jsonObject.put("reject", label_status_reject);
                        }

                        if (jsonRootObject.has("label_status_done")) {
                            String label_status_done = jsonRootObject.getString("label_status_done");
                            jsonObject.put("done", label_status_done);
                        }


                        String api_officers = jsonRootObject.getString("api_officers");

                        db.deleteRoomsDetailPtabPRoomNotValue(id_rooms_tab, username, fromList);
                        boolean loadListPull = false;
                        if (!idDetail.equalsIgnoreCase("")) {
                            if (fromList.equalsIgnoreCase("hide") || fromList.equalsIgnoreCase("hideMultiple")) {
                                loadListPull = true;
                            } else if (fromList.equalsIgnoreCase("showMultiple")) {
                                String[] ff = idDetail.split("\\|");
                                if (ff.length == 2) {
                                    loadListPull = true;
                                }
                            }
                        }

                        if (loadListPull) {
                            String bawaDariBelakang = "{}";
                            if (jsonRootObject.has("anothers")) {
                                JSONObject tambahan = new JSONObject("{}");
                                if (jsonRootObject.has("alasan_reject")) {
                                    if (jsonRootObject.has("anothers")) {
                                        String anothers = jsonRootObject.getString("anothers");
                                        if (!anothers.equalsIgnoreCase("[]")) {

                                            Object json = new JSONTokener(jsonRootObject.getString("alasan_reject")).nextValue();
                                            if (json instanceof JSONObject) {
                                                if (jsonRootObject.getJSONObject("alasan_reject").has("message")) {
                                                    tambahan = new JSONObject(anothers);
                                                    tambahan.put("message", jsonRootObject.getJSONObject("alasan_reject").getString("message"));
                                                    bawaDariBelakang = tambahan.toString();
                                                } else {
                                                    bawaDariBelakang = "{}";
                                                }
                                            } else {
                                                bawaDariBelakang = "{}";
                                            }
                                        } else {
                                            bawaDariBelakang = "{}";
                                        }
                                    }
                                }
                            }

                            RoomsDetail orderModel = new RoomsDetail(idDetail, id_rooms_tab, username, jsonRootObject.getString("list_pull"), bawaDariBelakang, time_str, "value");
                            db.insertRoomsDetail(orderModel);
                        }

                        String ccc = jsonDuaObject(content, attachment, api_officers, jsonObject.toString(), context.getResources().getString(R.string.app_version));
                        if (include_assignto.equalsIgnoreCase("0")) {
                            ccc = jsonDuaObject(content, attachment, "", jsonObject.toString(), context.getResources().getString(R.string.app_version));
                        }


                        RoomsDetail orderModel = new RoomsDetail(username, id_rooms_tab, username, ccc, "", time_str, "form");
                        db.insertRoomsDetail(orderModel);


                    } catch (JSONException e) {
                        Log.w("Jojoh", e.getMessage() + "");
                        finish();
                        error = "Tolong periksa koneksi internet.1";
                    }
                } else {
                    finish();
                    error = "Tolong periksa koneksi internet.2";
                }

            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                DinamicRoomTaskActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Tolong periksa koneksi internet3.", Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
            } catch (ClientProtocolException e) {
                DinamicRoomTaskActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Tolong periksa koneksi internet.4", Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
                // TODO Auto-generated catch block
            } catch (IOException e) {
                DinamicRoomTaskActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Tolong periksa koneksi internet5.", Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
                // TODO Auto-generated catch block
            }
        }

    }


    public String getStringFile(File f) {
        InputStream inputStream = null;
        String encodedFile = "", lastVal;
        try {
            inputStream = new FileInputStream(f.getAbsolutePath());

            byte[] buffer = new byte[10240];//specify the size to allow
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Base64OutputStream output64 = new Base64OutputStream(output, Base64.DEFAULT);

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output64.write(buffer, 0, bytesRead);
            }
            output64.close();
            encodedFile = output.toString();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        lastVal = encodedFile;
        return lastVal;
    }


    private void showAttachmentDialogNew(int req) {
        if (req == 11) {
            curAttItems = attCameraItems;
        } else if (req == REQ_VIDEO) {
            curAttItems = attVideoItems;
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
                    } else {
                        action = MediaStore.ACTION_VIDEO_CAPTURE;
                        req = REQ_VIDEO;
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
                        req = 12;
                        i.setAction(action);
                        startActivityForResult(i, req);

                        attCurReq = 0;
                    } else {
                        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }

                        CameraActivity.Builder start = new CameraActivity.Builder(activity, 11);
                        start.setLockSwitch(CameraActivity.UNLOCK_SWITCH_CAMERA);
                        start.setCameraFace(CameraActivity.CAMERA_REAR);
                        start.setFlashMode(CameraActivity.FLASH_OFF);
                        start.setQuality(CameraActivity.MEDIUM);
                        start.setRatio(CameraActivity.RATIO_4_3);
                        start.setFileName(new MediaProcessingUtil().createFileName("jpeg", "ROOM"));
                        new Camera(start.build()).lauchCamera();
                    }
                }
                dialog.dismiss();

            }
        });
        dialog.show();
    }


    private void showAttachmentDialog(int req) {
        if (req == REQ_CAMERA) {
            curAttItems = attCameraItems;
        } else if (req == REQ_VIDEO) {
            curAttItems = attVideoItems;
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
                    } else {
                        action = MediaStore.ACTION_VIDEO_CAPTURE;
                        req = REQ_VIDEO;
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
                        i.setAction(action);
                        startActivityForResult(i, req);

                        attCurReq = 0;
                    } else {
                        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }

                        CameraActivity.Builder start = new CameraActivity.Builder(activity, REQ_CAMERA);
                        start.setLockSwitch(CameraActivity.UNLOCK_SWITCH_CAMERA);
                        start.setCameraFace(CameraActivity.CAMERA_REAR);
                        start.setFlashMode(CameraActivity.FLASH_OFF);
                        start.setQuality(CameraActivity.MEDIUM);
                        start.setRatio(CameraActivity.RATIO_4_3);
                        start.setFileName(new MediaProcessingUtil().createFileName("jpeg", "ROOM"));
                        new Camera(start.build()).lauchCamera();

                    }
                }
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    protected Dialog onCreateDialog(int id) {
        DatePickerDialog dialog = new DatePickerDialog(this,
                mDateSetListener
                , mYear, mMonth, mDay);

        if (id == TIME_DIALOG_ID) {

            Calendar mcurrentTime = Calendar.getInstance();
            int hours = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    this, mTimePicker, hours, minute, true);
            timePickerDialog.setOnDismissListener(mOnDismissListenerTime);
            return timePickerDialog;

        } else {
            int iid = id - DATE_DIALOG_ID;
            Calendar calendar = Calendar.getInstance();
            int today = calendar.get(Calendar.DAY_OF_WEEK);

            if (iid == 9001) {
                //lock backdate

                calendar.add(Calendar.DATE, 0);
                dialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

            } else if (iid == 9002) {
                //lock backdate dan hari ini

                calendar.add(Calendar.DATE, +1);
                dialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

            } else if (iid == 9003) {
                //bisa setting start dan max date load from formula
                //rumus start date = -/+ dari new Date
                // end date =  start date + "enddate"

            } else if (iid == 9004) {
                //bisa setting this week

                calendar.add(Calendar.DATE, 1 - today);
                dialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, 7 - today);
                dialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

            } else if (iid == 9005) {
                //bisa setting this week lock backdate

                calendar.add(Calendar.DATE, 0);
                dialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, 7 - today);
                dialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

            } else if (iid == 9006) {
                //bisa setting next week

                int nextWeek = 7 - (today - 1);
                calendar.add(Calendar.DATE, nextWeek);
                dialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, nextWeek + 6);
                dialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
            }
        }
        dialog.setOnDismissListener(mOnDismissListenerDate);

        return dialog;

    }

    //onDismiss handler
    private DialogInterface.OnDismissListener mOnDismissListenerDate =
            new DialogInterface.OnDismissListener() {
                public void onDismiss(DialogInterface dialog) {

                    List value = (List) hashMap.get(dummyIdDate);
                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                    if (cEdit.getCount() > 0) {
                        if (cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_CONTENT)).equalsIgnoreCase("")) {
                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                            db.deleteDetailRoomWithFlagContent(orderModel);
                        }
                    }

                }
            }; //onDismiss handler
    private DialogInterface.OnDismissListener mOnDismissListenerTime =
            new DialogInterface.OnDismissListener() {
                public void onDismiss(DialogInterface dialog) {

                    List value = (List) hashMap.get(dummyIdDate);
                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                    if (cEdit.getCount() > 0) {
                        if (cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_CONTENT)).equalsIgnoreCase("")) {
                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                            db.deleteDetailRoomWithFlagContent(orderModel);
                        }
                    }

                }
            };

    private TimePickerDialog.OnTimeSetListener mTimePicker =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, final int selectedHour, final int selectedMinute) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            String menit = String.valueOf(selectedMinute);
                            String jam = String.valueOf(selectedHour);
                            if (selectedMinute <= 9) {
                                menit = "0" + selectedMinute;
                            }
                            if (selectedHour <= 9) {
                                jam = "0" + selectedHour;
                            }
                            String sdate = jam + ":" + menit;
                            List value = (List) hashMap.get(dummyIdDate);
                            TextView textView = (TextView) linearEstimasi[Integer.valueOf(value.get(0).toString())].findViewById(R.id.value);
                            textView.setText(sdate);
                            textView.setError(null);

                            Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                            if (cEdit.getCount() > 0) {
                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(sdate), jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                                db.updateDetailRoomWithFlagContent(orderModel);
                            }

                            Intent newIntent = new Intent("bLFormulas");
                            sendBroadcast(newIntent);
                        }
                    });
                }
            };

    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    runOnUiThread(new Runnable() {
                        public void run() {
                            String sdate = LPad(mDay + "", "0", 2) + " " + arrMonth[mMonth] + " " + mYear;
                            List value = (List) hashMap.get(dummyIdDate);

                            TextView textView = (TextView) linearEstimasi[Integer.valueOf(value.get(0).toString())].findViewById(R.id.value);
                            textView.setText(sdate);
                            textView.setError(null);

                            Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                            if (cEdit.getCount() > 0) {
                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, String.valueOf(sdate), jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                                db.updateDetailRoomWithFlagContent(orderModel);
                            }

                            Intent newIntent = new Intent("bLFormulas");
                            sendBroadcast(newIntent);
                        }
                    });
                }
            };


    private static String LPad(String schar, String spad, int len) {
        String sret = schar;
        for (int i = sret.length(); i < len; i++) {
            sret = spad + sret;
        }
        return new String(sret);
    }


    public void rrr() {
        if (!idListTaskMasterForm.equalsIgnoreCase("")) {
            if (!customersId.equalsIgnoreCase("")) {
                if (dumpCusId.equalsIgnoreCase("")) {
                    dumpCusId = customersId;
                } else {
                    if (dumpCusId != customersId) {
                        dumpCusId = customersId;

                        db.removeRoomDetailFormWithFlagContentWithOutId(DialogFormChildMain.jsonCreateIdTabNUsrName(idTab, username), DialogFormChildMain.jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMasterForm), "child_detail");
                        finish();
                        Intent aa = getIntent();
                        aa.putExtra("idTask", idDetail);
                        startActivity(getIntent());
                    }
                }
            } else {
                if (!dumpCusId.equalsIgnoreCase("")) {

                    db.removeRoomDetailFormWithFlagContentWithOutId(DialogFormChildMain.jsonCreateIdTabNUsrName(idTab, username), DialogFormChildMain.jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMasterForm), "child_detail");
                    finish();
                    Intent aa = getIntent();
                    aa.putExtra("idTask", idDetail);
                    startActivity(getIntent());
                }

            }
        }


        if (hashMapFormulas.size() > 0) {

            Iterator it = hashMapFormulas.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                List value = (List) hashMapFormulas.get(pair.getKey());
                List valueForm = (List) hashMap.get(pair.getKey());

                String DBmaster = value.get(0).toString();
                if (DBmaster != null) {
                    Log.w("IMANDANU@@", DBmaster);
                }

                String Formulamaster = value.get(1).toString();


                String[] aa = DBmaster.split("/");
                final String namaDB = aa[aa.length - 1].toString();

                List<String> stockList = new ArrayList<String>();

                for (int i = 2; i < value.size(); i++) {
                    for (Integer key : hashMap.keySet()) {
                        List<String> va = hashMap.get(key);

                        if (value.get(i).toString().replace(" ", "").equalsIgnoreCase(va.get(3).toString())) {

                            Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(String.valueOf(key), va.get(2).toString(), va.get(5).toString()));
                            if (cEdit.getCount() > 0) {
                                String con = cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_CONTENT));
                                JSONObject jObject = null;
                                try {
                                    jObject = new JSONObject(con);
                                    Iterator<String> keys = jObject.keys();
                                    while (keys.hasNext()) {
                                        String keyValue = (String) keys.next();
                                        String valueString = jObject.getString(keyValue);
                                        con = valueString;
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                stockList.add(con);
                            } else {
                                stockList.add("");
                            }

                        }

                    }
                }

                String[] stockArr = new String[stockList.size()];
                stockArr = stockList.toArray(stockArr);

                et[Integer.valueOf(valueForm.get(0).toString())].setText("");

                DataBaseDropDown mDB = new DataBaseDropDown(context, namaDB.substring(0, namaDB.indexOf(".")));
                if (mDB.getWritableDatabase() != null) {
                    Cursor c2 = mDB.getWritableDatabase().rawQuery(Formulamaster, stockArr);
                    if (c2.moveToFirst()) {
                        while (!c2.isAfterLast()) {
                            et[Integer.valueOf(valueForm.get(0).toString())].setText(c2.getString(0));
                            c2.moveToNext();
                        }
                    } else {
                        et[Integer.valueOf(valueForm.get(0).toString())].setText("");
                    }
                } else {
                    et[Integer.valueOf(valueForm.get(0).toString())].setText("");
                }

            }
        }
    }


    private static class MyTaskParams {
        HtmlTextView textView;
        AVLoadingIndicatorView progress;
        String url;

        MyTaskParams(HtmlTextView _bar, AVLoadingIndicatorView _progress, String _url) {
            this.textView = _bar;
            this.progress = _progress;
            this.url = _url;
        }

        public HtmlTextView getTextView() {
            return textView;
        }

        public void setTextView(HtmlTextView textView) {
            this.textView = textView;
        }

        public AVLoadingIndicatorView getProgress() {
            return progress;
        }

        public void setProgress(AVLoadingIndicatorView progress) {
            this.progress = progress;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    private class SaveMedia extends AsyncTask<MyTaskParams, Void, MyTaskParams> {
        String aa = "";

        @Override
        protected MyTaskParams doInBackground(MyTaskParams... urls) {
            MyTaskParams aaa = new MyTaskParams(urls[0].getTextView(), urls[0].getProgress(), urls[0].getUrl());

            if (aaa.getUrl().startsWith("http")) {
                aa = GET(aaa.getUrl());
            } else {
                aa = aaa.getUrl();
            }


            return aaa;
        }

        @Override
        protected void onPostExecute(MyTaskParams result) {
            result.getProgress().setVisibility(View.GONE);
            HtmlTextView textView = result.getTextView();

            textView.setHtml(aa,
                    new HtmlResImageGetter(textView));

            /*Pattern htmlPattern = Pattern.compile(".*\\<[^>]+>.*", Pattern.DOTALL);
            boolean isHTML = htmlPattern.matcher(aa).matches();
            String message = aa.replace ("\\r\\n", "<br>").replace ("\\n", "<br>");

            if (isHTML) {
                if (message.contains("<")) {
                    textView.setText(Html.fromHtml(Html.fromHtml(message).toString()));
                } else {
                    textView.setText(Html.fromHtml(message));
                }
            } else {
                String a = Html.fromHtml(message).toString();
                if (a.contains("<")) {
                    textView.setText(Html.fromHtml(a));
                }else{
                    textView.setText(a);
                }
            }*/
        }
    }


    public String GET(String url) {
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
            inputStream = httpResponse.getEntity().getContent();
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "-";
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    class BroadcastHandler extends BroadcastReceiver {
        @Override
        public void onReceive(Context mc, Intent intent) {
            if (intent.getAction().matches("refreshForm")) {
                //disini
                finish();
                Intent aa = getIntent();
                aa.putExtra("idTask", idDetail);
                aa.putExtra("posisi", intent.getStringExtra("posisi"));
                startActivity(aa);

            } else if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
                if (latLong.equalsIgnoreCase("1")) {
                    Toast.makeText(context, "GPS harus tetap aktif", Toast.LENGTH_LONG).show();
                    gps = new GPSTracker(DinamicRoomTaskActivity.this);
                    if (!gps.canGetLocation()) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQ_LOCATION_SETTING);
                    } else {
                        if (gps.canGetLocation()) {
                            latitude = gps.getLatitude();
                            longitude = gps.getLongitude();
                            if (latitude == 0.0 && longitude == 0.0) {
                                /*DinamicRoomTaskActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        finish();
                                        Toast.makeText(context, "Harap coba kembali dalam waktu 1 menit, karena data gps anda sedang diaktifkan", Toast.LENGTH_LONG).show();
                                    }
                                });*/
                                progressDialog.dismiss();
                            } else {
                                progressDialog.dismiss();
                            }
                        }
                    }
                }
            } else if (intent.getAction().matches("bLFormulas")) {
                if (!idListTaskMasterForm.equalsIgnoreCase("")) {
                    if (!customersId.equalsIgnoreCase("")) {
                        if (dumpCusId.equalsIgnoreCase("")) {
                            dumpCusId = customersId;
                        } else {
                            if (banyakDropdown.size() == 1) {
                                if (dumpCusId != customersId) {
                                    dumpCusId = customersId;
                                    db.removeRoomDetailFormWithFlagContentWithOutId(DialogFormChildMain.jsonCreateIdTabNUsrName(idTab, username), DialogFormChildMain.jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMasterForm), "child_detail");
                                    finish();
                                    Intent aa = getIntent();
                                    aa.putExtra("idTask", idDetail);
                                    startActivity(getIntent());
                                }
                            }
                        }
                    } else {
                        if (!dumpCusId.equalsIgnoreCase("")) {

                            db.removeRoomDetailFormWithFlagContentWithOutId(DialogFormChildMain.jsonCreateIdTabNUsrName(idTab, username), DialogFormChildMain.jsonCreateIdDetailNIdListTaskOld(idDetail, idListTaskMasterForm), "child_detail");
                            finish();
                            Intent aa = getIntent();
                            aa.putExtra("idTask", idDetail);
                            startActivity(getIntent());
                        }

                    }
                }


                if (hashMapDropNew.size() > 0) {

                    Iterator it = hashMapDropNew.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        List value = (List) hashMapDropNew.get(pair.getKey());
                        List valueForms = (List) hashMap.get(pair.getKey());

                        String DBmaster = value.get(0).toString();

                        String Formulamaster = value.get(1).toString();

                        JSONObject jObjectFormula = null;
                        try {


                            jObjectFormula = new JSONObject(Formulamaster);
                            String data = jObjectFormula.getString("data");
                            JSONObject jObjectFormula2 = new JSONObject(data);
                            JSONArray jsonArraySelect = jObjectFormula2.getJSONArray("select");

                            String aass[] = new String[jsonArraySelect.length()];

                            for (int ia = 0; ia < jsonArraySelect.length(); ia++) {
                                String ll = jsonArraySelect.getString(ia);
                                aass[ia] = ll;
                            }

                            String q = jObjectFormula2.getString("from");
                            String qw = jObjectFormula2.getString("where");

                            DataBaseDropDown mDB = new DataBaseDropDown(context, DBmaster);

                            final Cursor c = mDB.getWritableDatabase().query(true, q, aass, qw, new String[]{customersId}, null, null, null, null);

                            HashMap<String, String> hashMapss = new HashMap<>();

                            final ArrayList<String> spinnerArray = new ArrayList<String>();
                            if (c.getCount() > 1) {
                                spinnerArray.add("--Please Select--");
                            }

                            if (c.moveToFirst()) {
                                do {
                                    String column1 = c.getString(0);
                                    if (aass.length > 1) {
                                        String column2 = c.getString(1);
                                        hashMapss.put(column1, column2);
                                    }
                                    spinnerArray.add(column1);
                                } while (c.moveToNext());
                            }
                            c.close();

                            outerMap.put(Integer.valueOf(valueForms.get(0).toString()), hashMapss);

                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, R.layout.simple_spinner_item_black, spinnerArray); //selected item will look like a spinner set from XML
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            newSpinner[Integer.valueOf(valueForms.get(0).toString())].setAdapter(spinnerArrayAdapter);


                            Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(String.valueOf(pair.getKey()), valueForms.get(2).toString(), valueForms.get(5).toString()));
                            if (cursorCild.getCount() > 0) {


                                try {
                                    JSONObject jsonObject = new JSONObject(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));

                                    String titl = jsonObject.getString(value.get(2).toString()).split("\n\nKode")[0];

                                    int spinnerPosition = spinnerArrayAdapter.getPosition(titl);
                                    if (spinnerPosition < 0) {
                                        if (!customersId.equalsIgnoreCase("")) {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(String.valueOf(pair.getKey()), valueForms.get(2).toString(), valueForms.get(5).toString()), valueForms.get(3).toString(), "cild");
                                            db.deleteDetailRoomWithFlagContent(orderModel);
                                        }
                                        spinnerPosition = spinnerArrayAdapter.getPosition("--Add--");

                                    }


                                    newSpinner[Integer.valueOf(valueForms.get(0).toString())].setSelection(spinnerPosition);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }


                        } catch (Exception e) {
                            Log.d("InputStream", e.getLocalizedMessage());
                        }
                    }

                }

                if (hashMapDropForm.size() > 0) {
                    // Iterator it = hashMapDropForm.entrySet().iterator();
                    Log.w("doremi", "doew");
                    /*while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        List value = (List) hashMapDropForm.get(pair.getKey());
                        String DBmaster = value.get(0).toString();

                        String Formulamaster = value.get(1).toString();

                        JSONObject jObjectFormula = null;
                        try {
                            List<String> expandableListTitle = new ArrayList<String>();
                            HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

                            HashMap<String, List<JSONObject>> expandableListDetailJSONObject = new HashMap<String, List<JSONObject>>();
                            List<String> expandableListTitleJSON = new ArrayList<String>();

                            jObjectFormula = new JSONObject(Formulamaster);

                            String data = jObjectFormula.getString("data");
                            JSONObject jObjectFormula2 = new JSONObject(data);
                            JSONArray jsonArraySelect = jObjectFormula2.getJSONArray("select");

                            String aass[] = new String[jsonArraySelect.length()];

                            for (int ia = 0; ia < jsonArraySelect.length(); ia++) {
                                String ll = jsonArraySelect.getString(ia);
                                aass[ia] = ll;
                            }

                            String from = jObjectFormula2.getString("from");
                            String where = jObjectFormula2.getString("where");

                            String defaultValue = "";
                            List valueForms = (List) hashMap.get(pair.getKey());

                            DataBaseDropDown mDB = new DataBaseDropDown(context, DBmaster);
                            if (mDB.getWritableDatabase() != null) {

                                final Cursor c = mDB.getWritableDatabase().query(true, from, aass, where, new String[]{customersId}, null, null, null, null);

                                if (c.moveToFirst()) {
                                    String titleOld = "";
                                    List<String> Item = null;
                                    List<JSONObject> Items = null;
                                    int t = -1;
                                    do {
                                        String title = c.getString(1);
                                        String titleS = String.valueOf(c.getInt(3));
                                        if (!titleOld.equalsIgnoreCase(title)) {
                                            Item = new ArrayList<String>();
                                            Items = new ArrayList<JSONObject>();
                                            titleOld = title;
                                            expandableListTitle.add(title);
                                            expandableListTitleJSON.add(titleS);
                                            t++;
                                        }
                                        Integer column0 = c.getInt(0);
                                        Integer column3 = c.getInt(3);
                                        String column4 = c.getString(4);

                                        JSONObject obj = new JSONObject();
                                        JSONObject objS = new JSONObject();
                                        try {
                                            obj.put("t", column4);
                                            obj.put("iT", String.valueOf(column3));
                                            obj.put("iD", String.valueOf(column0));

                                            objS.put("iD", String.valueOf(column0));
                                            objS.put("v", "");
                                            objS.put("n", "");

                                        } catch (JSONException e) {
                                            // TODO Auto-generated catch block e.printStackTrace();
                                        }
                                        Item.add(obj.toString());
                                        Items.add(objS);

                                        expandableListDetail.put(title, Item);
                                        expandableListDetailJSONObject.put(titleS, Items);


                                    } while (c.moveToNext());

                                    JSONObject jsonObject = new JSONObject();
                                    for (String title : expandableListTitleJSON) {
                                        List<JSONObject> ala = expandableListDetailJSONObject.get(title);
                                        JSONArray JsArray = new JSONArray();
                                        for (JSONObject aha : ala) {
                                            JsArray.put(aha);
                                        }
                                        jsonObject.put(title, JsArray);
                                    }
                                    jsonObject.put("customersId", customersId);

                                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(String.valueOf(pair.getKey()), valueForms.get(2).toString(), valueForms.get(5).toString()));
                                    if (cEdit.getCount() == 0) {
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, jsonObject.toString(), jsonCreateType(String.valueOf(pair.getKey()), valueForms.get(2).toString(), valueForms.get(5).toString()), valueForms.get(3).toString(), "cild");
                                        db.insertRoomsDetail(orderModel);
                                    } else {
                                        String text = cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));
                                        JSONObject lala = new JSONObject(text);
                                        if (!lala.getString("customersId").equalsIgnoreCase(customersId)) {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, jsonObject.toString(), jsonCreateType(String.valueOf(pair.getKey()), valueForms.get(2).toString(), valueForms.get(5).toString()), valueForms.get(3).toString(), "cild");
                                            db.updateDetailRoomWithFlagContent(orderModel);
                                        }
                                    }
                                }
                                c.close();
                            }


                            ExpandableListAdapter listAdapter = new ExpandableListAdapter(activity, getApplicationContext(), expandableListTitle, expandableListDetail, idDetail, username, idTab, jsonCreateType(String.valueOf(pair.getKey()), valueForms.get(2).toString(), valueForms.get(5).toString()), valueForms.get(3).toString());
                            expandableListView[Integer.valueOf(valueForms.get(0).toString())].setAdapter(listAdapter);
                            setListViewHeightBasedOnChildren(expandableListView[Integer.valueOf(valueForms.get(0).toString())]);

                        } catch (Exception e) {
                            Log.d("InputStream", e.getLocalizedMessage());
                        }
                    }*/
                    Iterator it = hashMapDropForm.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        List valuess = (List) hashMapDropForm.get(pair.getKey());
                        String DBmaster = valuess.get(0).toString();

                        String Formulamaster = valuess.get(1).toString();

                        JSONObject jObjectFormula = null;
                        try {
                            List<String> expandableListTitle = new ArrayList<String>();
                            HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

                            HashMap<String, List<JSONObject>> expandableListDetailJSONObject = new HashMap<String, List<JSONObject>>();
                            List<String> expandableListTitleJSON = new ArrayList<String>();

                            jObjectFormula = new JSONObject(Formulamaster);

                            String data = jObjectFormula.getString("data");
                            JSONObject jObjectFormula2 = new JSONObject(data);
                            JSONArray jsonArraySelect = jObjectFormula2.getJSONArray("select");

                            String aass[] = new String[jsonArraySelect.length()];

                            for (int ia = 0; ia < jsonArraySelect.length(); ia++) {
                                String ll = jsonArraySelect.getString(ia);
                                aass[ia] = ll;
                            }

                            String from = jObjectFormula2.getString("from");
                            String where = "jas.kode = 'BNDSH'";//jObjectFormula2.getString("where");

                            String defaultValue = "";
                            List valueForms = (List) hashMap.get(pair.getKey());

                            DataBaseDropDown mDBss = new DataBaseDropDown(context, DBmaster);
                            if (mDBss.getWritableDatabase() != null) {

                                final Cursor css = mDBss.getWritableDatabase().query(true, from, aass, where, null, null, null, null, null);
                                if (css.moveToFirst()) {
                                    String titleOld = "";
                                    List<String> Item = null;

                                    List<JSONObject> Items = null;
                                    int t = -1;
                                    do {
                                        String title = css.getString(2);
                                        String titleS = String.valueOf(css.getInt(4));
                                        if (!titleOld.equalsIgnoreCase(title)) {
                                            Item = new ArrayList<String>();
                                            Items = new ArrayList<JSONObject>();
                                            titleOld = title;
                                            expandableListTitle.add(title);
                                            expandableListTitleJSON.add(titleS);
                                            t++;
                                        }
                                        Integer column0 = css.getInt(0);
                                        Integer column3 = css.getInt(4);
                                        String column4 = css.getString(5);

                                        JSONObject obj = new JSONObject();
                                        JSONObject objS = new JSONObject();
                                        try {
                                            obj.put("t", column4);
                                            obj.put("iT", String.valueOf(column3));
                                            obj.put("iD", String.valueOf(css.getInt(0)) + "|" + String.valueOf(css.getInt(1) + "|" + String.valueOf(css.getInt(4))));

                                            objS.put("iD", String.valueOf(css.getInt(0)) + "|" + String.valueOf(css.getInt(1) + "|" + String.valueOf(css.getInt(4))));
                                            objS.put("v", "");
                                            objS.put("n", "");

                                        } catch (JSONException e) {
                                            // TODO Auto-generated catch block e.printStackTrace();
                                        }
                                        Item.add(obj.toString());
                                        Items.add(objS);

                                        expandableListDetail.put(title, Item);
                                        expandableListDetailJSONObject.put(titleS, Items);


                                    } while (css.moveToNext());

                                    JSONObject jsonObject = new JSONObject();
                                    for (String title : expandableListTitleJSON) {
                                        List<JSONObject> ala = expandableListDetailJSONObject.get(title);
                                        JSONArray JsArray = new JSONArray();
                                        for (JSONObject aha : ala) {
                                            JsArray.put(aha);
                                        }
                                        jsonObject.put(title, JsArray);
                                    }
                                    jsonObject.put("customersId", "BNDSH");

                                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(String.valueOf(pair.getKey()), valueForms.get(2).toString(), valueForms.get(5).toString()));
                                    if (cEdit.getCount() == 0) {
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, jsonObject.toString(), jsonCreateType(String.valueOf(pair.getKey()), valueForms.get(2).toString(), valueForms.get(5).toString()), valueForms.get(3).toString(), "cild");
                                        db.insertRoomsDetail(orderModel);
                                    } else {
                                        String text = cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, jsonObject.toString(), jsonCreateType(String.valueOf(pair.getKey()), valueForms.get(2).toString(), valueForms.get(5).toString()), valueForms.get(3).toString(), "cild");
                                        db.updateDetailRoomWithFlagContent(orderModel);
                                    }
                                }
                                css.close();
                            } else {

                            }

                            ExpandableListAdapter listAdapter = new ExpandableListAdapter(activity, getApplicationContext(), expandableListTitle, expandableListDetail, idDetail, username, idTab, jsonCreateType(String.valueOf(pair.getKey()), valueForms.get(2).toString(), valueForms.get(5).toString()), valueForms.get(3).toString());
                            expandableListView[Integer.valueOf(valueForms.get(0).toString())].setAdapter(listAdapter);
                            setListViewHeightBasedOnChildren(expandableListView[Integer.valueOf(valueForms.get(0).toString())]);

                        } catch (Exception e) {
                            Log.d("InputStream", e.getLocalizedMessage());
                        }
                    }

                }

                if (hashMapFormulas.size() > 0) {

                    Iterator it = hashMapFormulas.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        List value = (List) hashMapFormulas.get(pair.getKey());
                        List valueForm = (List) hashMap.get(pair.getKey());

                        String DBmaster = value.get(0).toString();
                        String Formulamaster = value.get(1).toString();


                        String[] aa = DBmaster.split("/");
                        final String namaDB = aa[aa.length - 1].toString();

                        List<String> stockList = new ArrayList<String>();

                        for (int i = 2; i < value.size(); i++) {
                            for (Integer key : hashMap.keySet()) {
                                List<String> va = hashMap.get(key);

                                if (value.get(i).toString().replace(" ", "").equalsIgnoreCase(va.get(3).toString())) {

                                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(String.valueOf(key), va.get(2).toString(), va.get(5).toString()));
                                    if (cEdit.getCount() > 0) {
                                        String con = cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_CONTENT));
                                        JSONObject jObject = null;
                                        try {
                                            jObject = new JSONObject(con);
                                            Iterator<String> keys = jObject.keys();
                                            while (keys.hasNext()) {
                                                String keyValue = (String) keys.next();
                                                String valueString = jObject.getString(keyValue);
                                                con = valueString;
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        stockList.add(con);
                                    } else {
                                        stockList.add("");
                                    }

                                }

                            }
                        }

                        String[] stockArr = new String[stockList.size()];
                        stockArr = stockList.toArray(stockArr);

                        et[Integer.valueOf(valueForm.get(0).toString())].setText("");

                        DataBaseDropDown mDB = new DataBaseDropDown(context, namaDB.substring(0, namaDB.indexOf(".")));
                        if (mDB.getWritableDatabase() != null) {
                            Cursor c2 = mDB.getWritableDatabase().rawQuery(Formulamaster, stockArr);
                            if (c2.moveToFirst()) {
                                while (!c2.isAfterLast()) {
                                    et[Integer.valueOf(valueForm.get(0).toString())].setText(c2.getString(0));
                                    c2.moveToNext();
                                }
                            } else {
                                et[Integer.valueOf(valueForm.get(0).toString())].setText("");
                            }
                        } else {
                            et[Integer.valueOf(valueForm.get(0).toString())].setText("");
                        }

                    }
                }
            }

        }
    }

    @Override
    public void onNeedLocationPermission() {

    }

    @Override
    public void onExplainLocationPermission() {

    }

    @Override
    public void onLocationPermissionPermanentlyDeclined(View.OnClickListener
                                                                fromView, DialogInterface.OnClickListener fromDialog) {

    }

    @Override
    public void onNeedLocationSettingsChange() {

    }

    @Override
    public void onFallBackToSystemSettings(View.OnClickListener
                                                   fromView, DialogInterface.OnClickListener fromDialog) {

    }

    @Override
    public void onNewLocationAvailable(Location location) {

    }

    @Override
    public void onMockLocationsDetected(View.OnClickListener
                                                fromView, DialogInterface.OnClickListener fromDialog) {
        Toast.makeText(DinamicRoomTaskActivity.this, "Please Turn Off Allow Mock Location", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onError(LocationAssistant.ErrorType type, String message) {

    }

    private String getOficer(String type) {
        Cursor cur = db.getSingleRoom(username);
        if (cur.getCount() > 0) {
            final String officer = jsonResultType(cur.getString(cur.getColumnIndex(BotListDB.ROOM_COLOR)), "d");
            JSONObject jsonOfficer = null;
            try {
                jsonOfficer = new JSONObject(officer);
                type = jsonOfficer.getString(type);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            type = "";
        }
        return type;
    }


    public static String resizeAndCompressImageBeforeSend(Context context, String
            filePath, String fileName) {
        final int MAX_IMAGE_SIZE = 100 * 1024; // max final file size in kilobytes
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, 400, 400);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap bmpPic = BitmapFactory.decodeFile(filePath, options);


        int compressQuality = 100; // quality decreasing by 5 every loop.
        int streamLength;
        do {
            ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream);
            byte[] bmpPicByteArray = bmpStream.toByteArray();
            streamLength = bmpPicByteArray.length;
            compressQuality -= 5;
        } while (streamLength >= MAX_IMAGE_SIZE);

        try {
            //save the resized and compressed file to disk cache
            FileOutputStream bmpFile = new FileOutputStream(context.getCacheDir() + fileName);
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpFile);
            bmpFile.flush();
            bmpFile.close();
        } catch (Exception e) {
        }
        //return the path of resized and compressed file
        return context.getCacheDir() + fileName;
    }


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
                                            int reqHeight) {
        String debugTag = "MemoryInformation";
        // Image nin islenmeden onceki genislik ve yuksekligi
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat,
                                        int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static String encodeToBase64File(File originalFile) {
        String encodedBase64 = null;
        try {
            FileInputStream fileInputStreamReader = new FileInputStream(originalFile);
            byte[] bytes = new byte[(int) originalFile.length()];
            fileInputStreamReader.read(bytes);
            encodedBase64 = new String(Base64.encode(bytes, Base64.DEFAULT));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encodedBase64;
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


    private void setListViewHeight(ExpandableListView listView,
                                   int group) {

        ExpandableListAdapter listAdapter = (ExpandableListAdapter) listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.EXACTLY);

        for (int i = 0; i < listAdapter.getGroupCount(); i++) {

            LayoutInflater layoutInflater2 = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View groupItem = layoutInflater2.inflate(R.layout.expandable_parent, null);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

            totalHeight += groupItem.getMeasuredHeight();
            if (((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    LayoutInflater layoutInflater = (LayoutInflater) this.context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View listItem = layoutInflater.inflate(R.layout.expandable_cild, null);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                    totalHeight += listItem.getMeasuredHeight();
                }
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = 135 + totalHeight + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10)
            height = 200;
        params.height = height + 50;
        listView.setLayoutParams(params);
        listView.requestLayout();


    }


    private class RefreshDBAsign extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        String error = "";
        private Activity activity;
        private Context context;

        public RefreshDBAsign(Activity activity) {
            this.activity = activity;
            context = activity;
            dialog = new ProgressDialog(activity);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Loading...");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            postData(params[0], params[1]);
            return null;
        }

        protected void onPostExecute(String result) {

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            finish();
            if (error.length() > 0) {
                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
            } else {
                startActivity(getIntent());
                Toast.makeText(context, "Form success download2.", Toast.LENGTH_SHORT).show();
            }

        }

        protected void onProgressUpdate(String... string) {
        }

        public void postData(String valueIWantToSend, String usr) {
            // Create a new HttpClient and Post Header

            try {
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 13000);
                HttpConnectionParams.setSoTimeout(httpParameters, 15000);
                HttpClient httpclient = new DefaultHttpClient(httpParameters);
                HttpPost httppost = new HttpPost(valueIWantToSend);

                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("username_room", usr));
                nameValuePairs.add(new BasicNameValuePair("id_tab", idTab));
                Log.w("webToo1n", usr);
                Log.w("webToo2n", idTab);
                Log.w("webToo3n", valueIWantToSend);

                MessengerDatabaseHelper messengerHelper = null;
                if (messengerHelper == null) {
                    messengerHelper = MessengerDatabaseHelper.getInstance(context);
                }

                Contact contact = messengerHelper.getMyContact();

                Log.w("webToo4n", contact.getJabberId());

                nameValuePairs.add(new BasicNameValuePair("bc_user", contact.getJabberId()));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {

                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);
                    Log.w("Wagu", data);
                    DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(context);
                    dataBaseHelper.createUserTable("room", data, username, idTab);

                } else {
                    finish();
                    error = "Tolong periksa koneksi internet.";
                }

            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                DinamicRoomTaskActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Tolong periksa koneksi internet.", Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
            } catch (ClientProtocolException e) {
                DinamicRoomTaskActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Tolong periksa koneksi internet.", Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
                // TODO Auto-generated catch block
            } catch (IOException e) {
                DinamicRoomTaskActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Tolong periksa koneksi internet.", Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
                // TODO Auto-generated catch block
            }
        }

    }

    private String saveToInternalStorage(Bitmap bitmapImage, String name) {

        File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "ByonchatKTP");
        // Create imageDir
        File mypath = new File(directory, "KTP-" + name + ".jpg");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private boolean checkTime(String stTime, String enTime, String idDetail) {
        boolean loop = false;
        boolean bisa = true;
        try {
            List<String> starts = new ArrayList<>();
            List<String> ends = new ArrayList<>();
            Date stDate, enDate;
            SimpleDateFormat f = new SimpleDateFormat("HH:mm");

            MyEventDatabase eventDatabase = new MyEventDatabase(getApplicationContext());
            SQLiteDatabase db = eventDatabase.getReadableDatabase();
            String[] args = {startDate, idTab, idDetail, "Reject"};
            Cursor c = db.rawQuery("SELECT value_event FROM event" + " WHERE startDate_event = ? AND id_tab_event = ? AND id_detail_event != ? AND status_event != ?", args);
            while (c.moveToNext()) {
                String value = c.getString(0);
                if (value != null && !value.equalsIgnoreCase("")) {
                    JSONArray arrayValue = new JSONArray(value);
                    JSONObject startObj = arrayValue.getJSONObject(4);
                    JSONObject endObj = arrayValue.getJSONObject(5);

                    starts.add(startObj.getString("value"));
                    ends.add(endObj.getString("value"));
                }
            }
            stDate = f.parse(stTime);
            enDate = f.parse(enTime);

            if (stDate.before(enDate)) {
                if (starts.size() > 0 | ends.size() > 0) {
                    for (int i = 0; i < starts.size(); i++) {
                        Date strt = f.parse(starts.get(i));
                        if (stDate.before(strt)) {
                            if (enDate.before(strt)) {
                                loop = true;
                            } else {
                                loop = false;
                                bisa = false;
                            }
                        } else {
                            Date ed = f.parse(ends.get(i));
                            if (ed.before(stDate)) {
                                loop = true;
                            } else {
                                loop = false;
                                bisa = false;
                            }
                        }
                    }
                } else {
                    loop = true;
                }
            } else {
                loop = false;
                bisa = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!bisa) {
            loop = false;
        }
        return loop;
    }


    private boolean showParentView(JSONArray joContent, String idValue, String labelSS) {
        boolean show = true;

        try {
            for (int sangar = 0; sangar < joContent.length(); sangar++) {
                final String sangarType = joContent.getJSONObject(sangar).getString("type").toString();
                String sangarValue = joContent.getJSONObject(sangar).getString("value").toString();

                if (sangarType.equalsIgnoreCase("dropdown_views")) {
                    JSONObject jsso = new JSONObject(sangarValue);
                    if (jsso.has("pairs")) {
                        JSONObject jObjecValuet = new JSONObject(sangarValue);
                        JSONArray jsonArray1 = new JSONArray(jObjecValuet.getString("pairs"));
                        for (int iii = 0; iii < jsonArray1.length(); iii++) {
                            if (jsonArray1.get(iii).toString().equalsIgnoreCase(idValue)) {
                                show = false;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {

        }


        return show;
    }
}