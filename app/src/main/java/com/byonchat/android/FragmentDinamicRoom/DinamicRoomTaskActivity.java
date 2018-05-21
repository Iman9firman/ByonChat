package com.byonchat.android.FragmentDinamicRoom;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Typeface;
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
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
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
import com.byonchat.android.DialogFormChildMain;
import com.byonchat.android.DialogFormChildMainKompetitor;
import com.byonchat.android.DialogFormChildMainLemindo;
import com.byonchat.android.DialogFormChildMainNcal;
import com.byonchat.android.DialogFormChildMainNew;
import com.byonchat.android.DownloadFileByonchat;
import com.byonchat.android.DownloadSqliteDinamicActivity;
import com.byonchat.android.DownloadUtilsActivity;
import com.byonchat.android.R;
import com.byonchat.android.ReaderOcr;
import com.byonchat.android.ZoomImageViewActivity;
import com.byonchat.android.adapter.ExpandableListAdapter;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.communication.NotificationReceiver;
import com.byonchat.android.list.AttachmentAdapter;
import com.byonchat.android.list.utilLoadImage.ImageLoaderLarge;
import com.byonchat.android.location.ActivityDirection;
import com.byonchat.android.personalRoom.utils.AndroidMultiPartEntity;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.DataBaseDropDown;
import com.byonchat.android.provider.DataBaseHelper;
import com.byonchat.android.provider.DatabaseKodePos;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.provider.SubmitingModel;
import com.byonchat.android.provider.SubmitingRoomDB;
import com.byonchat.android.utils.DialogUtil;
import com.byonchat.android.utils.GPSTracker;
import com.byonchat.android.utils.ImageFilePath;
import com.byonchat.android.utils.LocationAssistant;
import com.byonchat.android.utils.MediaProcessingUtil;
import com.byonchat.android.utils.UploadService;
import com.byonchat.android.utils.Validations;
import com.byonchat.android.utils.ValidationsKey;
import com.byonchat.android.widget.ContactsCompletionView;
import com.example.syahrulzanuarr.aarmylibrary.VideoActivity;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.JsonArray;
import com.squareup.picasso.Picasso;
import com.tokenautocomplete.FilteredArrayAdapter;
import com.tokenautocomplete.TokenCompleteTextView;
import com.tokenautocomplete.TokenCompleteTextView.TokenListener;
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
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

public class DinamicRoomTaskActivity extends AppCompatActivity implements LocationAssistant.Listener, TokenCompleteTextView.TokenListener {

    public static String POSDETAIL = "/bc_voucher_client/webservice/proses/list_task.php";
    public static String PULLMULIPLEDETAIL = "/bc_voucher_client/webservice/proses/list_task_pull_multiple.php";
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
    private static final String MENU_GALLERY_TITLE = "Gallery";
    ProgressDialog progressDialog;
    private ArrayList<AttachmentAdapter.AttachmentMenuItem> curAttItems;
    String cameraFileOutput;
    public static final SimpleDateFormat hourFormat = new SimpleDateFormat(
            "HH:mm:ss dd/MM/yyyy", Locale.getDefault());
    double latitude, longitude;
    Bitmap result = null;
    ImageView imageView[];
    LinearLayout linearEstimasi[];
    ExpandableListView expandableListView[];
    SearchableSpinner newSpinner[];
    RatingBar rat[];
    EditText et[];
    TextView tp[];
    ArrayList<ArrayList<String>> stringAPI;
    Map<Integer, List<String>> hashMap = new HashMap<Integer, List<String>>();
    Map<Integer, List<String>> hashMapOcr = new HashMap<Integer, List<String>>();
    Map<Integer, List<String>> hashMapFormulas = new HashMap<Integer, List<String>>();
    Map<Integer, List<String>> hashMapDropForm = new HashMap<Integer, List<String>>();
    Map<Integer, List<String>> hashMapDropNew = new HashMap<Integer, List<String>>();
    HashMap<Integer, HashMap<String, String>> outerMap = new HashMap<Integer, HashMap<String, String>>();

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
    String officer = "";
    boolean deleteContent = false;
    int mYear, mMonth, mDay;
    int dummyIdDate;
    private int attCurReq = 0;
    Boolean showDialog = true;
    private BroadcastHandler broadcastHandler = new BroadcastHandler();
    static final int DATE_DIALOG_ID = 1;
    static final int TIME_DIALOG_ID = 2;
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
        assistant.start();
        IntentFilter filter = new IntentFilter("android.location.PROVIDERS_CHANGED");
        filter.addAction("bLFormulas");
        filter.addAction("refreshForm");
        filter.setPriority(1);
        focusOnView();
        registerReceiver(broadcastHandler, filter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dinamic_room_task);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity = this;
        assistant = new LocationAssistant(this, this, LocationAssistant.Accuracy.HIGH, 5000, false);
        assistant.setVerbose(true);

        context = this;

        mainScrooll = (ScrollView) findViewById(R.id.mainScrooll);
        linearLayout = (LinearLayout) findViewById(R.id.linear);

        if (db == null) {
            db = BotListDB.getInstance(context);
        }

        username = getIntent().getStringExtra("uu");
        idTab = getIntent().getStringExtra("ii");
        title = getIntent().getStringExtra("tt");
        idDetail = getIntent().getStringExtra("idTask");
        color = getIntent().getStringExtra("col");
        latLong = getIntent().getStringExtra("ll");
        fromList = getIntent().getStringExtra("from");

        if (getIntent().getStringExtra("customersId") != null) {
            customersId = getIntent().getStringExtra("customersId");
        }

        if (getIntent().getStringExtra("isReject") != null) {
            isReject = getIntent().getStringExtra("isReject");
        }


        getSupportActionBar().setTitle(title);

        Cursor cursor = db.getSingleRoomDetailForm(username, idTab);
        if (cursor.getCount() > 0) {
            getSupportActionBar().setBackgroundDrawable(new Validations().getInstance(context).headerCostume(getWindow(), "#" + color));
            final String conBefore = cursor.getString(cursor.getColumnIndexOrThrow(BotListDB.ROOM_CONTENT));
            String content = conBefore;

            JSONObject jO = null;
            try {
                Log.w("habibi", conBefore);
                jO = new JSONObject(conBefore);
                content = jO.getString("aa");
                dbMaster = jO.getString("bb");

                if (conBefore.contains("cc")) {
                    linkGetAsignTo = jO.getString("cc");
                }

                if (conBefore.contains("dd")) {
                    String ssA = jO.getString("dd");
                    if (ssA.equalsIgnoreCase("1")) {
                        includeStatus = true;
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

                Log.w("kami2", idDetail);

                Cursor cursorValue = db.getSingleRoomDetailFormWithFlag(idDetail, username, idTab, "value");
                if (cursorValue.getCount() > 0) {
                    final String contentValue = cursorValue.getString(cursorValue.getColumnIndexOrThrow(BotListDB.ROOM_CONTENT));
                    Log.w("hasi", contentValue);

                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(contentValue);
                        for (int ii = (jsonArray.length() - 1); ii >= 0; ii--) {

                            Log.w("hus-" + ii, jsonArray.getJSONArray(ii).toString());
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


                            /* ga ada dini

                             String isApprove = "";

                            if (oContent.has("is_approve")) {
                                isApprove = oContent.getString("is_approve");
                            }


                            if (isApprove != null) {
                                Log.w("Penangsuu", "1");

                                TextView aapprove = (TextView) MMlinearValue.findViewById(R.id.approvalTxt);
                                aapprove.setVisibility(View.VISIBLE);
                                aapprove.setTextIsSelectable(true);

                                if (isApprove.equalsIgnoreCase("0")) {
                                    aapprove.setText("Reject");
                                    aapprove.setTextColor(getResources().getColor(R.color.color_primary_red));
                                } else if (isApprove.equalsIgnoreCase("1")) {
                                    aapprove.setText("Approve");
                                    aapprove.setTextColor(getResources().getColor(R.color.color_primary_green));
                                } else if (isApprove.equalsIgnoreCase("null")) {
                                    aapprove.setVisibility(View.GONE);
                                    if (oContent.has("status")) {
                                        TextView statusApprove = new TextView(DinamicRoomTaskActivity.this);
                                        statusApprove.setText(Html.fromHtml("Status"));
                                        statusApprove.setTextSize(17);
                                        statusApprove.setLayoutParams(new TableRow.LayoutParams(0));

                                        LinearLayout lineIn2 = (LinearLayout) getLayoutInflater().inflate(R.layout.line_horizontal, null);

                                        TextView eeStat = (TextView) new TextView(context);
                                        eeStat.setTextIsSelectable(true);
                                        String sSttus = "Approve";
                                        if (oContent.getString("status").equalsIgnoreCase("0")) {
                                            sSttus = "Reject";
                                        } else if (oContent.getString("status").equalsIgnoreCase("2")) {
                                            sSttus = "Done";
                                        }

                                        eeStat.setText(sSttus);


                                        submitLinear.addView(statusApprove, params11In);
                                        submitLinear.addView(lineIn2, params11In);
                                        submitLinear.addView(eeStat, params22Last);

                                    }
                                }

                            } else {*/
                            //`status = status nya current tab`, `is_approve = statusnya tab parent`
                            if (oContent.has("status")) {
                                if (!oContent.getString("status").equalsIgnoreCase("null") || oContent.getString("status") == null) {
                                    TextView statusApprove = new TextView(DinamicRoomTaskActivity.this);
                                    statusApprove.setText(Html.fromHtml("Status"));
                                    statusApprove.setTextSize(17);
                                    statusApprove.setLayoutParams(new TableRow.LayoutParams(0));

                                    LinearLayout lineIn2 = (LinearLayout) getLayoutInflater().inflate(R.layout.line_horizontal, null);

                                    TextView eeStat = (TextView) new TextView(context);
                                    eeStat.setTextIsSelectable(true);
                                    String sSttus = "Approve";

                                    if (oContent.getString("status").equalsIgnoreCase("0")) {
                                        sSttus = "Reject";
                                    } else if (oContent.getString("status").equalsIgnoreCase("2")) {
                                        sSttus = "Done";
                                    }

                                    eeStat.setText(sSttus);


                                    submitLinear.addView(statusApprove, params11In);
                                    submitLinear.addView(lineIn2, params11In);
                                    submitLinear.addView(eeStat, params22Last);
                                }


                            }

                            //}

                            LinearLayout linearValue = (LinearLayout) MMlinearValue.findViewById(R.id.linearValueDetail);

                            for (int i = 0; i < joContent.length(); i++) {
                                final String label = joContent.getJSONObject(i).getString("label").toString();
                                final String value = joContent.getJSONObject(i).getString("value").toString();
                                final String type = joContent.getJSONObject(i).getString("type").toString();

                                if (type.equalsIgnoreCase("attach_api")) {
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
                                        saveMedia.execute(new MyTaskParams(htmlTextView, progress, value.replace(" ", "%")));
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

                                    /*new DownloadImageFromInternet(imageView).execute(value);*/

                                  /*  ImageLoaderLarge imgBarcode = new ImageLoaderLarge(context, false);
                                    imgBarcode.DisplayImage(value, imageView);
*/
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

                                    if (iidd.equalsIgnoreCase("1643") || iidd.equalsIgnoreCase("1628") || iidd.equalsIgnoreCase("1632") || iidd.equalsIgnoreCase("2021")
                                            || (Integer.valueOf(iidd) >= 2092)) {
                                        line = (LinearLayout) getLayoutInflater().inflate(R.layout.form_cild_two_layout_value, null);

                                        if (Integer.valueOf(iidd) >= 2092) {
                                            TextView nama = (TextView) line.findViewById(R.id.nama);
                                            nama.setVisibility(View.GONE);
                                        }

                                        lv = (ListView) line.findViewById(R.id.listOrder);
                                        addCild = (Button) line.findViewById(R.id.btn_add_cild);
                                    } else {
                                        tQty = (TextView) line.findViewById(R.id.total_detail_order);
                                        tPrice = (TextView) line.findViewById(R.id.total_price_order);
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
                                                final String lab = jsonarrayChild.getJSONObject(bbb).getString("label").toString();

                                                titleUntuk += lab + " : " + val + "\n";
                                                if (typ.equalsIgnoreCase("front_camera") || typ.equalsIgnoreCase("rear_camera")) {
                                                    image = true;
                                                }
                                            }

                                            Log.w("cucuc", String.valueOf(image));

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
                                            }

                                            Log.w("laga", priceUntuk);
                                            rowItems.add(new ModelFormChild(urutan, titleUntuk, decsUntuk, priceUntuk));
                                            Log.w("laga", "1");
                                        }

                                    } else {
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
                                                        Log.w("vava", val);
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
                                                Log.w("laga", "2");
                                            } else if (iidd.equalsIgnoreCase("1113")) {
                                                int nilai = 0;
                                                try {
                                                    nilai = Integer.valueOf(priceUntuk != null ? priceUntuk.replace(".", "") : "0") / Integer.valueOf(decsUntuk != null ? decsUntuk.replace(".", "") : "0");
                                                } catch (Exception e) {
                                                    nilai = 0;
                                                }
                                                Log.w("laga", "3");
                                                rowItems.add(new ModelFormChild(urutan, titleUntuk, decsUntuk, String.valueOf(nilai)));
                                            } else {

                                                if (priceUntuk.equalsIgnoreCase("standart") && !fotoUntuk.equalsIgnoreCase("")) {
                                                    priceUntuk = fotoUntuk;
                                                }

                                                Log.w("laga", "4" + "==" + titleUntuk + "==" + decsUntuk + "==" + priceUntuk);

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
                                        for (ModelFormChild mfc : rowItems) {
                                            try {
                                                if (mfc.getTitle().contains(".jpg")) {
                                                    sini.add("image");
                                                } else if (mfc.getPrice().equalsIgnoreCase("image")) {
                                                    sini.add("image");
                                                } else if (mfc.getPrice().equalsIgnoreCase("standart")) {
                                                    sini.add("standart");
                                                } else {
                                                    totalQ += Integer.valueOf(mfc.getDetail());
                                                    totalP += Integer.valueOf(mfc.getPrice().replace(".", "")) * Integer.valueOf(mfc.getDetail());
                                                }

                                            } catch (Exception e) {

                                            }
                                        }

                                        if (totalQ > 0 && totalP > 0) {
                                            String totalHarga = new Validations().getInstance(context).numberToCurency(totalP + "");
                                            tQty.setText(totalQ + "");
                                            tPrice.setText(totalHarga);
                                        }

                                    }

                                    if (sini.size() > 0) {
                                        Log.w("bisa", "1");

                                        if (sini.get(0).toString().equalsIgnoreCase("image")) {
                                            Log.w("bisa", "2");
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
                                                        DialogUtil.generateAlertDialogLeftImage(DinamicRoomTaskActivity.this, modelFormChild.getTitle(), modelFormChild.getDetail(), "", labelDialog.get(0) != null ? labelDialog.get(0) : "").show();
                                                    } else if (labelDialog.size() > 1) {
                                                        DialogUtil.generateAlertDialogLeftImage(DinamicRoomTaskActivity.this, modelFormChild.getTitle(), modelFormChild.getDetail(), labelDialog.get(1) != null ? labelDialog.get(1) : "", labelDialog.get(0) != null ? labelDialog.get(0) : "").show();
                                                    } else {
                                                        DialogUtil.generateAlertDialogLeftImage(DinamicRoomTaskActivity.this, modelFormChild.getTitle(), modelFormChild.getDetail(), "", "").show();
                                                    }
                                                }
                                            });

                                        } else {
                                            Log.w("bisa", "3");
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
                                        if (Integer.valueOf(iidd) >= 2092) {
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

                                    String[] latlong = valueMap.split(
                                            Message.LOCATION_DELIMITER);
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (NetworkInternetConnectionStatus.getInstance(context).isOnline(context)) {
                        if (username != null) {
                            if (fromList.equalsIgnoreCase("hide")) {
                                Log.w("1", "satu");

                                new Refresh(DinamicRoomTaskActivity.this).execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + GETTABDETAILPULL, username, idTab, idDetail);
                            } else if (fromList.equalsIgnoreCase("hideMultiple") || fromList.equalsIgnoreCase("showMultiple")) {
                                Log.w("2", "satu");
                                if (!idDetail.equalsIgnoreCase("")) {
                                    Log.w("3", "satu");
                                    String[] ff = idDetail.split("\\|");
                                    if (ff.length == 2) {
                                        Log.w("4", "satu");
                                        new Refresh(DinamicRoomTaskActivity.this).execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + GETTABDETAILPULLMULTIPLE, username, idTab, idDetail);
                                    }
                                }
                            } else {
                                Log.w("5", "satu");
                                new Refresh(DinamicRoomTaskActivity.this).execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + GETTABDETAIL, username, idTab, "");
                            }
                        } else {
                            finish();
                        }
                    } else {
                        Toast.makeText(context, "No Internet Akses", Toast.LENGTH_SHORT).show();
                        finish();
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

                Log.w("ini", content);

                JSONArray jsonArray = new JSONArray(content);
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
                stringAPI = new ArrayList<ArrayList<String>>();
                linearEstimasi = new LinearLayout[jsonArray.length()];
                imageView = new ImageView[jsonArray.length()];
                expandableListView = new ExpandableListView[jsonArray.length()];
                newSpinner = new SearchableSpinner[jsonArray.length()];

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

                    Log.w("asd", type);

                    if (type.equalsIgnoreCase("attach_api")) {
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
                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

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

                                linearLayout.addView(tp[count], params1);
                                linearLayout.addView(linearEstimasi[count]);

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
                        hashMap.put(Integer.parseInt(idListTask), valSetOne);


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

                    } else if (type.equalsIgnoreCase("form_child")) {

                        Log.w("igni", idListTask);

                        final String formChild = jsonArray.getJSONObject(i).getString("form_child").toString();

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
                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

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

                                Log.w("end2", jsonArrayMaster.toString());

                                if (jsonArrayMaster.length() > 0) {
                                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                                    if (cEdit.getCount() > 0) {

                                        String contentValue = jsonArrayMaster.toString();
                                        Log.w("end3", contentValue);
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, contentValue, jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                                        db.updateDetailRoomWithFlagContent(orderModel);
                                    } else {

                                        String contentValue = jsonArrayMaster.toString();
                                        Log.w("end4", contentValue);
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
                                            DialogFormChildMainNew testDialog = DialogFormChildMainNew.newInstance(formChild, name, finalDbMaster, idDetail, username, idTab, idListTask, "", customersId, DinamicRoomTaskActivity.this);
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
                                            DialogFormChildMainNew testDialog = DialogFormChildMainNew.newInstance(formChild, name, finalDbMaster, idDetail, username, idTab, idListTask, item.getId(), customersId, DinamicRoomTaskActivity.this);
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
                            Log.w("segalga", "kerinda22");
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

                                    Log.w("sedi", decsUntuk + "::" + tambahan);
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
                                String a = jsonArrayCild.getJSONObject(iaa).getString("formula").toString();

                                if (jsonArrayCild.getJSONObject(iaa).toString().contains("front_camera") || jsonArrayCild.getJSONObject(iaa).toString().contains("rear_camera")) {
                                    imageForm = true;
                                }

                                if (!a.equalsIgnoreCase("")) {
                                    perhitungan = true;
                                }
                            }

                            if (perhitungan) {

                                Log.w("segalga", "kerinda");
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
                                            rowItems.add(new ModelFormChild(idchildDetail, titleUntuk, decsUntuk, priceUntuk));
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
                                                DialogFormChildMain testDialog = DialogFormChildMain.newInstance(formChild, name, finalDbMaster, idDetail, username, idTab, idListTask, item.getId(), customersId);
                                                testDialog.setRetainInstance(true);
                                                testDialog.show(fm, "Dialog");
                                            }


                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {

                                Log.w("disini", "gge");

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
                                                    titleName.setText(list.get(u).getFlag_tab());
                                                    titleUntuk = list.get(u).getContent();
                                                } else if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("dropdown")) {
                                                    titleName.setText(list.get(u).getFlag_tab());
                                                    titleUntuk = list.get(u).getContent();
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
                                            } else if (list.size() == 1) {
                                                decsUntuk = "";
                                            }
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

                                    final FormChildAdapter adapter = new FormChildAdapter(context, rowItems, "", imageForm);
                                    lv.setAdapter(adapter);

                                    Log.w("end2", jsonArrayMaster.toString());

                                    if (jsonArrayMaster.length() > 0) {
                                        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                                        if (cEdit.getCount() > 0) {

                                            String contentValue = jsonArrayMaster.toString();
                                            Log.w("end3", contentValue);
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, contentValue, jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                                            db.updateDetailRoomWithFlagContent(orderModel);
                                        } else {

                                            String contentValue = jsonArrayMaster.toString();
                                            Log.w("end4", contentValue);
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
                                                Log.w("pepe2", "satu");
                                                DialogFormChildMainNew testDialog = DialogFormChildMainNew.newInstance(formChild, name, finalDbMaster, idDetail, username, idTab, idListTask, "", customersId, DinamicRoomTaskActivity.this);
                                                testDialog.setRetainInstance(true);
                                                testDialog.show(fm, "Dialog");
                                            } else {
                                                Log.w("pepe2", "dua");
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
                                                DialogFormChildMainNew testDialog = DialogFormChildMainNew.newInstance(formChild, name, finalDbMaster, idDetail, username, idTab, idListTask, item.getId(), customersId, DinamicRoomTaskActivity.this);
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


                        linearLayout.addView(linearEstimasi[count], params3);

                    } else if (type.equalsIgnoreCase("text")) {

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
                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

                        et[count].setId(Integer.parseInt(idListTask));
                        et[count].setHint(placeHolder);


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


                        linearLayout.addView(textView, params1);
                        linearLayout.addView(et[count], params2);

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
                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

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


                        linearLayout.addView(textView, params1);
                        linearLayout.addView(et[count], params2);
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
                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

                        et[count].setId(Integer.parseInt(idListTask));
                        et[count].setHint(placeHolder);
                        et[count].setMinLines(4);
                        et[count].setMaxLines(8);
                        et[count].setScroller(new Scroller(context));
                        et[count].setVerticalScrollBarEnabled(true);
                        et[count].setSingleLine(false);
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


                        linearLayout.addView(textView, params1);
                        linearLayout.addView(et[count], params2);

                    } else if (type.equalsIgnoreCase("text_info")) {
                        TextView textView = new TextView(DinamicRoomTaskActivity.this);
                        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(20);
                        textView.setLayoutParams(new TableRow.LayoutParams(0));

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
                        linearLayout.addView(textView, params1);
                        if ((value.length() != 0) || (!value.equalsIgnoreCase(""))) {
                            linearLayout.addView(et, params2);
                        }


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
                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params1.setMargins(30, 10, 30, 0);
                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(30, 10, 30, 40);

                        linearEstimasi[count] = (LinearLayout) getLayoutInflater().inflate(R.layout.date_layout_form, null);
                        linearEstimasi[count].setLayoutParams(params2);
                        linearLayout.addView(tp[count], params1);
                        linearLayout.addView(linearEstimasi[count]);

                        final ImageButton btnOption = (ImageButton) linearEstimasi[count].findViewById(R.id.btn_browse);
                        if (type.equalsIgnoreCase("time")) {
                            btnOption.setImageResource(R.drawable.ic_time);
                        }
                        final TextView valueFile = (TextView) linearEstimasi[count].findViewById(R.id.value);
                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                        if (cursorCild.getCount() > 0) {
                            valueFile.setText(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                        } else {
                            if (!value.equalsIgnoreCase("")) {
                                valueFile.setText(value);
                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, value, jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                                db.insertRoomsDetail(orderModel);
                            }
                        }


                        final int finalI2 = i;
                        btnOption.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final Calendar c = Calendar.getInstance();
                                dummyIdDate = Integer.parseInt(idListTask);
                                mYear = c.get(Calendar.YEAR);
                                mMonth = c.get(Calendar.MONTH);
                                mDay = c.get(Calendar.DAY_OF_MONTH);

                                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI2)));
                                if (cEdit.getCount() == 0) {
                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(idListTask, type, String.valueOf(finalI2)), name, "cild");
                                    db.insertRoomsDetail(orderModel);
                                }

                                if (type.equalsIgnoreCase("time")) {
                                    showDialog(TIME_DIALOG_ID);
                                } else {
                                    showDialog(DATE_DIALOG_ID);
                                }

                                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

                            }
                        });


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
                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

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
                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, value, jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
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


                        linearLayout.addView(textView, params1);
                        linearLayout.addView(et[count], params2);

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
                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

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
                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, value, jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
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


                        linearLayout.addView(textView, params1);
                        linearLayout.addView(et[count], params2);

                    } else if (type.equalsIgnoreCase("rear_camera") || type.equalsIgnoreCase("front_camera")) {
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

                        imageView[count] = (ImageView) getLayoutInflater().inflate(R.layout.image_view_frame, null);
                        int width = getWindowManager().getDefaultDisplay().getWidth();
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, width / 2);
                        params.setMargins(5, 15, 0, 0);
                        imageView[count].setLayoutParams(params);
                        params.addRule(RelativeLayout.CENTER_IN_PARENT);
                        linearLayout.addView(imageView[count], params);

                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                        if (cursorCild.getCount() > 0) {
                            imageView[count].setImageBitmap(decodeBase64(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT))));
                        }

                        List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(String.valueOf(count));
                        valSetOne.add(required);
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));
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
                    } else if (type.equalsIgnoreCase("ocr")) {
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

                        imageView[count] = (ImageView) getLayoutInflater().inflate(R.layout.image_view_frame, null);
                        imageView[count].setImageDrawable(getResources().getDrawable(R.drawable.ico_camera_reader));
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(5, 15, 0, 0);
                        imageView[count].setLayoutParams(params);
                        params.addRule(RelativeLayout.CENTER_IN_PARENT);
                        linearLayout.addView(imageView[count], params);

                        final List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(String.valueOf(count));
                        valSetOne.add(required);
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));
                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

                        imageView[count].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dummyIdDate = Integer.parseInt(idListTask);
                                Intent i = new Intent(context, ReaderOcr.class);
                                startActivityForResult(i, OCR_REQUEST);

                            }
                        });


                        RelativeLayout child;
                        if (jsonArray.getJSONObject(i).getString("ocr") != null) {
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
                                linearLayout.addView(linearEstimasi[count]);
                                linearLayout.addView(child);
                            }
                        }
                    } else if (type.equalsIgnoreCase("upload_document")) {
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
                        hashMap.put(Integer.parseInt(idListTask), valSetOne);


                        linearEstimasi[count] = (LinearLayout) getLayoutInflater().inflate(R.layout.upload_doc_layout, null);
                        linearEstimasi[count].setLayoutParams(params2);
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


                    } else if (type.equalsIgnoreCase("signature")) {
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

                        imageView[count] = (ImageView) getLayoutInflater().inflate(R.layout.frame_signature_form_black, null);
                        imageView[count].setImageDrawable(getResources().getDrawable(R.drawable.ico_signature));
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(5, 15, 0, 0);
                        imageView[count].setLayoutParams(params);
                        params.addRule(RelativeLayout.CENTER_IN_PARENT);
                        linearLayout.addView(imageView[count], params);

                        final List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add(String.valueOf(count));
                        valSetOne.add(required);
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));
                        hashMap.put(Integer.parseInt(idListTask), valSetOne);
                        final int finalI26 = i;


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
                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

                        linearEstimasi[count] = (LinearLayout) getLayoutInflater().inflate(R.layout.estimation_layout, null);
                        linearEstimasi[count].setLayoutParams(params2);
                        linearLayout.addView(linearEstimasi[count]);


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
                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

                        rat[count] = (RatingBar) getLayoutInflater().inflate(R.layout.costume_rating, null);
                        final LinearLayout.LayoutParams testLP = new LinearLayout.LayoutParams(
                                AppBarLayout.LayoutParams.WRAP_CONTENT, AppBarLayout.LayoutParams.MATCH_PARENT);
                        testLP.setMargins(10, 20, 10, 10);
                        testLP.gravity = Gravity.CENTER_HORIZONTAL;
                        rat[count].setLayoutParams(testLP);
                        linearLayout.addView(rat[count]);


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
                        hashMap.put(Integer.parseInt(idListTask), valSetOne);
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


                        linearLayout.addView(textView, params1);
                        linearLayout.addView(et[count], params2);


                    } else if (type.equalsIgnoreCase("video")) {
                        TextView textView = new TextView(DinamicRoomTaskActivity.this);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(15);
                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(5, 15, 0, 0);
                        textView.setLayoutParams(params2);
                        linearLayout.addView(textView);

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
                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

                        imageView[count] = new ImageView(this);
                        imageView[count] = (ImageView) getLayoutInflater().inflate(R.layout.image_view_frame, null);

                        int width = getWindowManager().getDefaultDisplay().getWidth();
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, width * 50 / 100);
                        params.setMargins(5, 15, 0, 0);
                        imageView[count].setLayoutParams(params);
                        params.addRule(RelativeLayout.CENTER_IN_PARENT);
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
                                                    Intent cf = new Intent(getApplicationContext(), VideoActivity.class);
                                                    cf.putExtra("res", "Medium");
                                                    File f = MediaProcessingUtil.getOutputFile(".mp4");
                                                    cf.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                                                    cf.setAction(action);
                                                    startActivityForResult(cf, REQ_VIDEO);
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


                    } else if (type.equalsIgnoreCase("dropdown_form")) {

                        TextView textView = new TextView(DinamicRoomTaskActivity.this);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(15);
                        textView.setLayoutParams(new TableRow.LayoutParams(0));

                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params1.setMargins(30, 10, 30, 0);
                        linearLayout.addView(textView, params1);

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
                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

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


                                    final ExpandableListAdapter expandableListAdapter = new ExpandableListAdapter(this, expandableListTitle, expandableListDetail, idDetail, username, idTab, jsonCreateType(idListTask, type, String.valueOf(finalI5)), name);
                                    expandableListView[count].setAdapter(expandableListAdapter);

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

                                Log.w("sabu", url);

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
                        linearLayout.addView(linearEstimasi[count]);


                    } else if (type.equalsIgnoreCase("new_dropdown_dinamis")) {

                        String foro = jsonArray.getJSONObject(i).getString("formula").toString();

                        JSONObject jObjects = null;
                        try {
                            jObjects = new JSONObject(foro);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (jObjects != null) {
                            Log.w("masd", "sali");

                            TextView textView = new TextView(DinamicRoomTaskActivity.this);
                            if (required.equalsIgnoreCase("1")) {
                                label += "<font size=\"3\" color=\"red\">*</font>";
                            }
                            textView.setText(Html.fromHtml(label));
                            textView.setTextSize(15);
                            textView.setLayoutParams(new TableRow.LayoutParams(0));

                            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params1.setMargins(30, 10, 30, 0);
                            linearLayout.addView(textView, params1);

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
                            hashMap.put(Integer.parseInt(idListTask), valSetOne);

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
                                            String column2 = c.getString(1);
                                            hashMapss.put(column1, column2);
                                            spinnerArray.add(column1);
                                        } while (c.moveToNext());
                                    }
                                    c.close();


                                    outerMap.put(count, hashMapss);


                                    final LinearLayout spinerTitle = (LinearLayout) getLayoutInflater().inflate(R.layout.item_spiner_textview, null);
                                    TextView textViewFirst = (TextView) spinerTitle.findViewById(R.id.title);

                                    final String titlesss = title.get(0).toString();
                                    textViewFirst.setText(Html.fromHtml(titlesss));
                                    textViewFirst.setTextSize(15);

                                    newSpinner[count] = (SearchableSpinner) spinerTitle.findViewById(R.id.spinner);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        newSpinner[count].setBackground(getResources().getDrawable(R.drawable.spinner_background));
                                    }
                                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
                                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    newSpinner[count].setAdapter(spinnerArrayAdapter);

                                    final int finalI24 = i;

                                    Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                                    if (cursorCild.getCount() > 0) {

                                        Log.w("bubu", cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));

                                        try {
                                            JSONObject jsonObject = new JSONObject(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));

                                            String titl = jsonObject.getString(titlesss).split("\n\nKode")[0];
                                            Log.w("ss", titl);

                                            int spinnerPosition = spinnerArrayAdapter.getPosition(titl);
                                            if (spinnerPosition < 0) {
                                                spinnerPosition = spinnerArrayAdapter.getPosition("--Add--");
                                            }

                                            newSpinner[count].setSelection(spinnerPosition);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }


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
                            linearLayout.addView(linearEstimasi[count]);

                        } else {
                            //biasa
                            TextView textView = new TextView(DinamicRoomTaskActivity.this);
                            if (required.equalsIgnoreCase("1")) {
                                label += "<font size=\"3\" color=\"red\">*</font>";
                            }
                            textView.setText(Html.fromHtml(label));
                            textView.setTextSize(15);
                            textView.setLayoutParams(new TableRow.LayoutParams(0));

                            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params1.setMargins(30, 10, 30, 0);
                            linearLayout.addView(textView, params1);

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
                            hashMap.put(Integer.parseInt(idListTask), valSetOne);

                            linearEstimasi[count] = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_child, null);
                            final JSONObject jObject = new JSONObject(value);
                            String url = jObject.getString("url");
                            String table = jsonArray.getJSONObject(i).getString("formula").toString().replace(";SELECT kelas, COUNT(*) AS jumlah FROM siswa GROUP BY kelas ORDER BY kelas ASC", "");

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
                            Log.w("aduhai", nama.substring(0, nama.indexOf(".")));

                            mDB = new DataBaseDropDown(context, nama.substring(0, nama.indexOf(".")));
                            try {
                                if (mDB.getWritableDatabase() != null) {
                                    String namaTable = table;
                                    String where = null;

                                    if (namaTable.split(";").length > 1) {
                                        String[] ww = namaTable.split(";");
                                        namaTable = ww[0];
                                        where = ww[1];
                                        if (ww[2].startsWith("officer")) {
                                            String[] of = ww[2].split("_");
                                            where = where.replace("?", getOficer(of[1]));
                                        }

                                    }

                                    if (nama.substring(0, nama.indexOf(".")).equalsIgnoreCase("SQL_29122017_144028_Hey89n63eA")) {
                                        MessengerDatabaseHelper messengerHelper = null;
                                        if (messengerHelper == null) {
                                            messengerHelper = MessengerDatabaseHelper.getInstance(context);
                                        }
                                        Contact contact = messengerHelper.getMyContact();

                                        where = "nip  = (select nip from guru where telp = '" + contact.getJabberId() + "')";

                                        namaTable = "kelas";
                                    }

                                    final String whereDone = where;

                                    linearEstimasi[count].removeAllViews();

                                    String[] columnNames = new String[kolom.size()];
                                    columnNames = kolom.toArray(columnNames);
                                    String[] titleNames = new String[title.size()];
                                    titleNames = title.toArray(titleNames);

                                    Cursor c = mDB.getWritableDatabase().query(true, namaTable, new String[]{columnNames[0]}, where, null, columnNames[0], null, null, null);


                                    final ArrayList<String> spinnerArray = new ArrayList<String>();
                                    if (c.getCount() > 1) {
                                        spinnerArray.add("--Please Select--");
                                    }
                                    if (c.moveToFirst()) {
                                        do {
                                            String column1 = c.getString(0);
                                            spinnerArray.add(column1);
                                        } while (c.moveToNext());
                                    }
                                    c.close();

                                    final LinearLayout spinerTitle = (LinearLayout) getLayoutInflater().inflate(R.layout.item_spiner_textview, null);
                                    TextView textViewFirst = (TextView) spinerTitle.findViewById(R.id.title);

                                    final String titlesss = title.get(0).toString();
                                    textViewFirst.setText(Html.fromHtml(titlesss));
                                    textViewFirst.setTextSize(15);

                                    final SearchableSpinner spinner = (SearchableSpinner) spinerTitle.findViewById(R.id.spinner);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        spinner.setBackground(getResources().getDrawable(R.drawable.spinner_background));
                                    }
                                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
                                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spinner.setAdapter(spinnerArrayAdapter);


                                    Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                                    if (cursorCild.getCount() > 0) {

                                        try {
                                            JSONObject jsonObject = new JSONObject(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                                            int spinnerPosition = spinnerArrayAdapter.getPosition(jsonObject.getString(titlesss));

                                            if (spinnerPosition < 0) {
                                                spinnerPosition = spinnerArrayAdapter.getPosition("--Add--");
                                            }

                                            spinner.setSelection(spinnerPosition);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }


                                    final String finalNamaTable = namaTable;
                                    final int finalI24 = i;
                                    final String[] finalColumnNames = columnNames;
                                    final String finalNama = nama;
                                    final String[] finalTitleNames = titleNames;

                                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                        @Override
                                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                            dummyIdDate = Integer.parseInt(idListTask);
                                            List nilai = (List) hashMap.get(dummyIdDate);
                                            if (spinner.getSelectedItem().toString().equals("--Please Select--")) {
                                                if (kolom.size() > 1) {
                                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, spinner.getSelectedItem().toString(), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "cild");

                                                    db.deleteDetailRoomWithFlagContent(orderModel);

                                                    linearEstimasi[Integer.valueOf(nilai.get(0).toString())].removeAllViews();
                                                    linearEstimasi[Integer.valueOf(nilai.get(0).toString())].addView(spinerTitle);
                                                }
                                            } else if (spinner.getSelectedItem().toString().equals("--Add--")) {

                                                linearEstimasi[Integer.valueOf(nilai.get(0).toString())].removeAllViews();
                                                linearEstimasi[Integer.valueOf(nilai.get(0).toString())].addView(spinerTitle);

                                                JSONObject jsonObject = null;
                                                Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI24)));
                                                if (cursorCild.getCount() > 0) {
                                                    try {
                                                        jsonObject = new JSONObject(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));

                                                        boolean lanjut = false;
                                                        for (String abubu : spinnerArray) {
                                                            if (abubu.equalsIgnoreCase(jsonObject.getString(titlesss))) {
                                                                lanjut = true;
                                                            }
                                                        }

                                                        if (lanjut) {
                                                            jsonObject = null;
                                                            if (kolom.size() > 1) {
                                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, spinner.getSelectedItem().toString(), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "cild");
                                                                db.deleteDetailRoomWithFlagContent(orderModel);
                                                            }

                                                        }

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                                final EditText etFc[] = new EditText[finalTitleNames.length];
                                                int ids = 0;
                                                for (final String ahai : finalTitleNames) {

                                                    TextView textView = new TextView(DinamicRoomTaskActivity.this);
                                                    textView.setText(ahai);
                                                    textView.setTextSize(15);
                                                    textView.setLayoutParams(new TableRow.LayoutParams(0));

                                                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                                    params1.setMargins(30, 10, 30, 0);

                                                    etFc[ids] = (EditText) getLayoutInflater().inflate(R.layout.edit_input_layout, null);
                                                    try {
                                                        if (jsonObject != null) {
                                                            etFc[ids].setText(jsonObject.getString(ahai));
                                                            if (etFc.length - 1 == ids) {
                                                                customersId = etFc[ids].getText().toString();
                                                            }
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }


                                                    final int finalIds = ids;
                                                    etFc[ids].addTextChangedListener(new TextWatcher() {
                                                        @Override
                                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                                        }

                                                        @Override
                                                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                                                        }

                                                        @Override
                                                        public void afterTextChanged(Editable s) {
                                                            if (etFc.length - 1 == finalIds) {
                                                                customersId = s.toString();
                                                            }


                                                            Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI24)));

                                                            if (cEdit.getCount() > 0) {
                                                                try {
                                                                    JSONObject jsonObject = new JSONObject(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                                                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, function(jsonObject, ahai, s.toString()).toString(), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "cild");
                                                                    db.updateDetailRoomWithFlagContent(orderModel);
                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }

                                                            } else {
                                                                RoomsDetail orderModel = null;
                                                                try {
                                                                    orderModel = new RoomsDetail(idDetail, idTab, username, function(null, ahai, s.toString()).toString(), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "cild");
                                                                    db.insertRoomsDetail(orderModel);
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }

                                                            }

                                                        }
                                                    });

                                                    linearEstimasi[Integer.valueOf(nilai.get(0).toString())].addView(textView, params1);
                                                    linearEstimasi[Integer.valueOf(nilai.get(0).toString())].addView(etFc[ids], params1);
                                                    ids++;
                                                }


                                            } else {
                                                customersId = spinner.getSelectedItem().toString();
                                                if (kolom.size() > 1) {
                                                    Log.w("aaa", "21");
                                                    final int counts = linearEstimasi[Integer.valueOf(nilai.get(0).toString())].getChildCount();
                                                    linearEstimasi[Integer.valueOf(nilai.get(0).toString())].removeViews(1, counts - 1);

                                                    String mera = finalColumnNames[0] + "= '" + spinner.getSelectedItem().toString().replace("'", "''") + "'";
                                                    String next = whereDone != null ? " and " + whereDone : "";
                                                    if (mera.equalsIgnoreCase("")) {
                                                        next = whereDone != null ? whereDone : "";
                                                    }
                                                    String full = mera + next;

                                                    addSpinnerDinamics(finalTitleNames, finalNama.substring(0, finalNama.indexOf(".")), linearEstimasi[Integer.valueOf(nilai.get(0).toString())], finalNamaTable, finalColumnNames, 0, full, idListTask, type, String.valueOf(finalI24), name);

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

                                                } else {
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
                                            }
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
                                    Log.w("abasd", url);

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
                            linearLayout.addView(linearEstimasi[count]);
                        }


                    } else if (type.equalsIgnoreCase("dropdown_dinamis")) {

                        TextView textView = new TextView(DinamicRoomTaskActivity.this);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(15);
                        textView.setLayoutParams(new TableRow.LayoutParams(0));

                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params1.setMargins(30, 10, 30, 0);
                        linearLayout.addView(textView, params1);

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
                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

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
                                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
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
                        linearLayout.addView(linearEstimasi[count]);

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
                        for (int ia = 0; ia < jsonArrays.length(); ia++) {
                            String l = jsonArrays.getJSONObject(ia).getString("label_option").toString();

                            if (jsonArrays.getJSONObject(ia).has("flag")) {
                                String flagDrop = jsonArrays.getJSONObject(ia).getString("flag").toString();
                                spinnerArrayFlag.add(ia, flagDrop);
                            }
                            spinnerArray.add(l);

                        }

                        SearchableSpinner spinner = new SearchableSpinner(this);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            spinner.setBackground(getResources().getDrawable(R.drawable.spinner_background));
                        }
                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
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
                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

                        final EditText et = (EditText) getLayoutInflater().inflate(R.layout.edit_input_layout, null);
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


                        linearLayout.addView(textView, params1);
                        linearLayout.addView(spinner, params1);
                        linearLayout.addView(et, params12);

                        //untuk kasih nafas haha
                        View view = new View(this);
                        view.setVisibility(View.INVISIBLE);
                        linearLayout.addView(view, params2);


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
                            hashMap.put(Integer.parseInt(idListTask), valSetOne);

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

                            linearLayout.addView(textView, params1);
                            linearLayout.addView(tvKode, params3);
                            linearLayout.addView(et[count], params5);
                            linearLayout.addView(tvProv, params4);
                            linearLayout.addView(prov, params5);
                            linearLayout.addView(tvKota, params4);
                            linearLayout.addView(kota, params5);
                            linearLayout.addView(tvKec, params4);
                            linearLayout.addView(kec, params5);
                            linearLayout.addView(tvKel, params4);
                            linearLayout.addView(kel, params2);


                        } else {
                            if (deleteContent) {

                                db.deleteRoomsDetailbyId(idDetail, idTab, username);

                            }
                            finish();
                            Intent intent = new Intent(context, DownloadUtilsActivity.class);
                            startActivity(intent);
                            return;
                        }

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
                            hashMap.put(Integer.parseInt(idListTask), valSetOne);

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
                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArrayPropinsi); //selected item will look like a spinner set from XML
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerPropinsi.setAdapter(spinnerArrayAdapter);

                            final SearchableSpinner spinnerKota = new SearchableSpinner(this);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                spinnerKota.setBackground(getResources().getDrawable(R.drawable.spinner_background));
                            }
                            final ArrayList<String> spinnerArrayKota = new ArrayList<>();
                            spinnerArrayKota.add("Semua Kota/Kabupaten");
                            final ArrayAdapter<String> spinnerKotaArrayAdapter = new ArrayAdapter<String>(
                                    this, android.R.layout.simple_spinner_item, spinnerArrayKota);
                            spinnerKotaArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerKota.setAdapter(spinnerKotaArrayAdapter);

                            final SearchableSpinner spinnerKecamatan = new SearchableSpinner(this);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                spinnerKecamatan.setBackground(getResources().getDrawable(R.drawable.spinner_background));
                            }
                            final ArrayList<String> spinnerArrayKecamatan = new ArrayList<>();
                            spinnerArrayKecamatan.add("Semua Kecamatan");
                            final ArrayAdapter<String> spinnerKecamatanArrayAdapter = new ArrayAdapter<String>(
                                    this, android.R.layout.simple_spinner_item, spinnerArrayKecamatan);
                            spinnerKecamatanArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerKecamatan.setAdapter(spinnerKecamatanArrayAdapter);

                            final SearchableSpinner spinnerKelurahan = new SearchableSpinner(this);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                spinnerKelurahan.setBackground(getResources().getDrawable(R.drawable.spinner_background));
                            }
                            final ArrayList<String> spinnerArrayKelurahan = new ArrayList<>();
                            spinnerArrayKelurahan.add("Semua Kelurahan");
                            final ArrayAdapter<String> spinnerKelurahanArrayAdapter = new ArrayAdapter<String>(
                                    this, android.R.layout.simple_spinner_item, spinnerArrayKelurahan);
                            spinnerKelurahanArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerKelurahan.setAdapter(spinnerKelurahanArrayAdapter);


                            final TextView kodePos = (TextView) getLayoutInflater().inflate(R.layout.text_input_layout, null);
                            kodePos.setTextSize(15);


                            Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                            if (cursorCild.getCount() > 0) {
                                final String isi = cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));
                                kodePos.setText(jsonResultType(isi, "a"));


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

                                    int spinnerPositionProvinsi = spinnerArrayAdapter.getPosition(jsonResultType(isi, "b"));
                                    spinnerPropinsi.setSelection(spinnerPositionProvinsi);
                                    int spinnerPositionKota = spinnerKotaArrayAdapter.getPosition(jsonResultType(isi, "c"));
                                    spinnerKota.setSelection(spinnerPositionKota);
                                    int spinnerPositionKec = spinnerKecamatanArrayAdapter.getPosition(jsonResultType(isi, "d"));
                                    spinnerKecamatan.setSelection(spinnerPositionKec);
                                    int spinnerPositionKel = spinnerKelurahanArrayAdapter.getPosition(jsonResultType(isi, "e"));
                                    spinnerKelurahan.setSelection(spinnerPositionKel);
                                }

                            } else {
                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, jsonPosCode("-", "Semua Provinsi", "Semua Kota/Kabupaten", "Semua Kecamatan", "Semua Kelurahan"), jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                                db.insertRoomsDetail(orderModel);
                            }


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

                            linearLayout.addView(textView, params1);
                            linearLayout.addView(tvProv, params3);
                            linearLayout.addView(spinnerPropinsi, params5);
                            linearLayout.addView(tvKota, params4);
                            linearLayout.addView(spinnerKota, params5);
                            linearLayout.addView(tvKec, params4);
                            linearLayout.addView(spinnerKecamatan, params5);
                            if (flag.equalsIgnoreCase("yes")) {
                                linearLayout.addView(tvKel, params4);
                                linearLayout.addView(spinnerKelurahan, params5);
                                linearLayout.addView(tvKode, params4);
                                linearLayout.addView(kodePos, params2);
                            } else {
                                linearLayout.addView(tvKel, params4);
                                linearLayout.addView(spinnerKelurahan, params2);
                            }
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
                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(30, 10, 30, 0);
                        textView.setLayoutParams(params2);
                        linearLayout.addView(textView);

                        List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add("");
                        valSetOne.add(required);
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));
                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

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
                            linearLayout.addView(cb);
                        }
                    } else if (type.equalsIgnoreCase("radio")) {

                        TextView textView = new TextView(DinamicRoomTaskActivity.this);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(15);
                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(30, 10, 30, 0);
                        textView.setLayoutParams(params2);
                        linearLayout.addView(textView);

                        List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add("");
                        valSetOne.add(required);
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));
                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

                        String isis = jsonArray.getJSONObject(i).getString("radio").toString();
                        JSONArray jsonArrayCeks = new JSONArray(isis);
                        final RadioButton[] rb = new RadioButton[jsonArrayCeks.length()];
                        LinearLayout.LayoutParams params2b = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2b.setMargins(30, 10, 30, 0);
                        RadioGroup rg = new RadioGroup(this); //create the RadioGroup
                        rg.setOrientation(RadioGroup.VERTICAL);//or RadioGroup.VERTICAL
                        rg.setLayoutParams(params2b);
                        for (int iaa = 0; iaa < jsonArrayCeks.length(); iaa++) {
                            String l = jsonArrayCeks.getJSONObject(iaa).getString("label_radio").toString();
                            String cek = jsonArrayCeks.getJSONObject(iaa).getString("is_checked").toString();
                            rb[iaa] = new RadioButton(this);
                            rb[iaa].setText(l);
                            rb[iaa].setId(iaa);

                            if ((!showButton)) {
                                rb[iaa].setEnabled(false);
                            }

                            Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                            if (cursorCild.getCount() > 0) {
                                if (rb[iaa].getText().toString().equalsIgnoreCase(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)))) {
                                    rb[iaa].setChecked(true);
                                }
                            } else {
                                if (cek.equalsIgnoreCase("1")) {
                                    rb[iaa].setChecked(true);
                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, rb[iaa].getText().toString(), jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                                    db.insertRoomsDetail(orderModel);
                                }

                            }


                            rg.addView(rb[iaa]);
                        }

                        if ((!showButton)) {
                            rg.setEnabled(false);
                        } else {
                            final int finalI23 = i;
                            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(RadioGroup group, int checkedId) {
                                    RadioButton rb = (RadioButton) group.findViewById(checkedId);
                                    if (null != rb && checkedId > -1) {

                                        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI23)));
                                        if (cEdit.getCount() > 0) {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, rb.getText().toString(), jsonCreateType(idListTask, type, String.valueOf(finalI23)), name, "cild");
                                            db.updateDetailRoomWithFlagContent(orderModel);
                                        } else {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, rb.getText().toString(), jsonCreateType(idListTask, type, String.valueOf(finalI23)), name, "cild");
                                            db.insertRoomsDetail(orderModel);
                                        }

                                        Intent newIntent = new Intent("bLFormulas");
                                        sendBroadcast(newIntent);
                                    }
                                }
                            });
                        }
                        linearLayout.addView(rg);
                    } else if (type.equalsIgnoreCase("image_load")) {

                        TextView textView = new TextView(DinamicRoomTaskActivity.this);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(15);
                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(30, 10, 30, 0);
                        textView.setLayoutParams(params2);
                        linearLayout.addView(textView);

                        if (count == null) {
                            count = 0;
                        } else {
                            count++;
                        }

                        List<String> valSetOne = new ArrayList<String>();
                        valSetOne.add("");
                        valSetOne.add("");
                        valSetOne.add(type);
                        valSetOne.add(name);
                        valSetOne.add(label);
                        valSetOne.add(String.valueOf(i));
                        hashMap.put(Integer.parseInt(idListTask), valSetOne);

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
                        linearLayout.addView(imageView[count]);
                    } else if (type.equalsIgnoreCase("hidden")) {

                    } else if (type.equalsIgnoreCase("longlat")) {

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            if (includeStatus) {

                final ArrayList<String> spinnerArray = new ArrayList<String>();
                spinnerArray.add("--Please Select--");
                spinnerArray.add("Approve");
                spinnerArray.add("Reject");
                spinnerArray.add("Done");

                LinearLayout spinerTitle = (LinearLayout) getLayoutInflater().inflate(R.layout.item_spiner_textview, null);
                TextView textViewFirst = (TextView) spinerTitle.findViewById(R.id.title);

                textViewFirst.setText(Html.fromHtml("Status " + "<font size=\"3\" color=\"red\">*</font>"));
                textViewFirst.setTextSize(15);
                final SearchableSpinner spinner = (SearchableSpinner) spinerTitle.findViewById(R.id.spinner);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    spinner.setBackground(getResources().getDrawable(R.drawable.spinner_background));
                }

                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(spinnerArrayAdapter);

                Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "includeStatus", "");
                if (cursorCild.getCount() > 0) {
                    String cc = cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));
                    int spinnerPosition = spinnerArrayAdapter.getPosition(cc);
                    spinner.setSelection(spinnerPosition);
                }

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                        if (!linkGetAsignTo.equalsIgnoreCase("")) {
                            if (!spinnerArray.get(position).equals("Approve")) {
                                linearLayout.getChildAt(linearLayout.getChildCount() - 2).setVisibility(View.GONE);
                            } else {
                                linearLayout.getChildAt(linearLayout.getChildCount() - 2).setVisibility(View.VISIBLE);
                            }
                        }


                        if (!spinnerArray.get(position).equals("--Please Select--")) {
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
                final ContactsCompletionView completionView;
                Person[] people;
                ArrayAdapter<Person> adapter;

                LinearLayout btnRel = (LinearLayout) getLayoutInflater().inflate(R.layout.form_dinamic_asignto, null);

                DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(context);

                if (dataBaseHelper.checkTable("room")) {

                    Cursor curr = dataBaseHelper.selectAll("room", username, idTab);
                    ArrayList<Person> rere = new ArrayList<Person>();

                    rere.add(new Person("All", "All"));

                    if (curr.getCount() > 0) {

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
                        new RefreshDBAsign(DinamicRoomTaskActivity.this).execute(linkGetAsignTo, username);
                    }
                } else {
                    new RefreshDBAsign(DinamicRoomTaskActivity.this).execute(linkGetAsignTo, username);
                }


                final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(30, 10, 20, 0);
                btnRel.setLayoutParams(params);
                linearLayout.addView(btnRel);
            }


            if (showButton) {

                LinearLayout btnRel = (LinearLayout) getLayoutInflater().inflate(R.layout.button_submit_form, null);
                final Button b = (Button) btnRel.findViewById(R.id.btn_submit);
                final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(5, 15, 0, 0);
                btnRel.setLayoutParams(params);
                linearLayout.addView(btnRel);

                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        b.setEnabled(false);
                        boolean berhenti = false;

                        List<String> errorReq = new ArrayList<String>();
                        for (Integer key : hashMap.keySet()) {
                            List<String> value = hashMap.get(key);
                            if (value != null) {
                                if (value.get(1).toString().equalsIgnoreCase("1")) {

                                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(String.valueOf(key), value.get(2).toString(), value.get(5).toString()));
                                    if (cEdit.getCount() > 0) {
                                        if (cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).equalsIgnoreCase("")) {
                                            if (value.get(2).toString().equalsIgnoreCase("text") ||
                                                    value.get(2).toString().equalsIgnoreCase("textarea") ||
                                                    value.get(2).toString().equalsIgnoreCase("number") ||
                                                    value.get(2).toString().equalsIgnoreCase("currency") ||
                                                    value.get(2).toString().equalsIgnoreCase("date") ||
                                                    value.get(2).toString().equalsIgnoreCase("time")) {
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
                                    } else {
                                        if (value.get(2).toString().equalsIgnoreCase("text") ||
                                                value.get(2).toString().equalsIgnoreCase("textarea") ||
                                                value.get(2).toString().equalsIgnoreCase("number") ||
                                                value.get(2).toString().equalsIgnoreCase("currency") ||
                                                value.get(2).toString().equalsIgnoreCase("date") ||
                                                value.get(2).toString().equalsIgnoreCase("time")) {
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

                        if (!linkGetAsignTo.equalsIgnoreCase("")) {
                            if (linearLayout.getChildAt(linearLayout.getChildCount() - 2).getVisibility() == View.VISIBLE) {
                                Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "assignTo", "");
                                if (cursorCild.getCount() == 0) {
                                    b.setEnabled(true);
                                    final AlertDialog.Builder alertbox = new AlertDialog.Builder(DinamicRoomTaskActivity.this);
                                    alertbox.setTitle("required");
                                    alertbox.setMessage(Html.fromHtml("Assign To") + "\n");
                                    alertbox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg0, int arg1) {

                                        }
                                    });

                                    alertbox.show();
                                    return;
                                } else {
                                    Log.w("telo", "dadar");

                                }
                            }

                        }


                        if (includeStatus) {
                            Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "includeStatus", "");
                            if (cEdit.getCount() == 0) {
                                b.setEnabled(true);
                                final AlertDialog.Builder alertbox = new AlertDialog.Builder(DinamicRoomTaskActivity.this);
                                alertbox.setTitle("required");
                                alertbox.setMessage(Html.fromHtml("Status") + "\n");
                                alertbox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {

                                    }
                                });

                                alertbox.show();
                                return;
                            }

                        }


                        if (berhenti) {
                            b.setEnabled(true);
                            if (errorReq.size() > 0) {
                                final AlertDialog.Builder alertbox = new AlertDialog.Builder(DinamicRoomTaskActivity.this);
                                alertbox.setTitle("required");
                                String content = "";
                                for (String ss : errorReq) {
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
                            for (ArrayList<String> innerList : stringAPI) {
                                String param = "";
                                String flag = "";
                                String type = "";
                                String idContent = "";
                                String idCount = "";

                                TextView valueFile = (TextView) linearEstimasi[0].findViewById(R.id.value);
                                MessengerDatabaseHelper messengerHelper = null;
                                if (messengerHelper == null) {
                                    messengerHelper = MessengerDatabaseHelper.getInstance(context);
                                }

                                Contact contact = messengerHelper.getMyContact();
                                Date c = Calendar.getInstance().getTime();
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String formattedDate = df.format(c);
                                valueFile.setText(valueFile.getText().toString() + "?outlet_code=" + customersId + "&bc_user=" + contact.getJabberId() + "&date=" + formattedDate + "&id_task=" + idDetail);

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
                                            Log.w("satu", "20");
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
                                        /*DinamicRoomTaskActivity.this.runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(context, "Harap coba kembali dalam waktu 1 menit, karena data gps anda sedang diaktifkan", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        return;*/

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
                        showProgressDialogWithTitle("Please Wait...", "upload...");

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("in", "pertama");
                            jsonObject.put("idDetail", idDetail);
                            jsonObject.put("username", username);
                            jsonObject.put("idTab", idTab);
                            jsonObject.put("fromList", fromList);
                            jsonObject.put("customersId", customersId);
                            jsonObject.put("includeStatus", includeStatus == false ? "" : "true");
                            jsonObject.put("isReject", isReject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        long date = System.currentTimeMillis();
                        String dateString = hourFormat.format(date);
                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, dateString, "11", null, "parent");
                        db.updateDetailRoomWithFlagContentParent(orderModel);


                        if (NetworkInternetConnectionStatus.getInstance(context).isOnline(context)) {

                            SubmitingRoomDB submitingRoomDB = SubmitingRoomDB.getInstance(getApplicationContext());
                            SubmitingModel submitingModel = new SubmitingModel();
                            submitingModel.setStatus("0");
                            submitingModel.setContent(jsonObject.toString());
                            Message message = new Message();
                            message.setMessage(jsonObject.toString());
                            message.setId(submitingRoomDB.createContact(submitingModel));

                            Intent intent = new Intent(getApplicationContext(), UploadService.class);
                            intent.putExtra(UploadService.ACTION, "uploadTaskRoom");
                            intent.putExtra(UploadService.KEY_MESSAGE, message);
                            startService(intent);

                        } else {
                            SubmitingRoomDB submitingRoomDB = SubmitingRoomDB.getInstance(getApplicationContext());
                            SubmitingModel submitingModel = new SubmitingModel();
                            submitingModel.setStatus("1");
                            submitingModel.setContent(jsonObject.toString());
                            submitingRoomDB.createContact(submitingModel);

                        }

                        finish();

                    }
                });
            }

        } else {
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

        if (

                getCurrentFocus() != null)

        {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }


    }

    private void focusOnView() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mainScrooll.scrollTo(0, linearLayout.getTop());
            }
        });
    }


    private void uploadFileChild(String ainnu) {
        ArrayList<RoomsDetail> list = db.allRoomDetailFormWithFlag(idDetail, username, idTab, "cild");
        ArrayList<String> listUpload = new ArrayList<>();
        for (int u = 0; u < list.size(); u++) {
            JSONArray jsA = null;
            String content = "";

            String cc = list.get(u).getContent();

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

            } else {
                if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("form_child")) {
                    try {
                        JSONArray jsonArray = new JSONArray(list.get(u).getContent());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONArray jsnobject = new JSONArray(jsonArray.getJSONObject(i).getString("data"));
                            for (int ii = 0; ii < jsnobject.length(); ii++) {
                                JSONObject c = jsnobject.getJSONObject(ii);
                                if (c.getString("type").equalsIgnoreCase("front_camera") || c.getString("type").equalsIgnoreCase("rear_camera")) {
                                    String aa[] = c.getString("value").toString().split(";");
                                    if (aa.length == 2) {
                                        if (aa[0].length() == 0) {
                                            listUpload.add("1");
                                            String idLisTask = "";
                                            JSONObject cs = null;
                                            try {
                                                cs = new JSONObject(list.get(u).getFlag_content());
                                                idLisTask = cs.getString("a");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            new UploadFileToServer().execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + POST_FOTO, username, idTab, idLisTask, cc, c.getString("value").toString(), list.get(u).getId(), list.get(u).getParent_tab(), list.get(u).getParent_room(), aa[1], list.get(u).getFlag_content(), list.get(u).getFlag_tab(), list.get(u).getFlag_room());
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } /* nanti lagi
                else if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("rear_camera") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("rear_camera")) {
                    if (!Message.isJSONValid(list.get(u).getContent())) {
                        listUpload.add("1");
                        String idLisTask = "";
                        JSONObject c = null;
                        try {
                            c = new JSONObject(list.get(u).getFlag_content());
                            idLisTask = c.getString("a");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        new UploadFileToServerB().execute(POST_FOTO, username, idTab, idLisTask, list.get(u).getId(), list.get(u).getParent_tab(), list.get(u).getParent_room(), list.get(u).getContent(), list.get(u).getFlag_content(), list.get(u).getFlag_tab(), list.get(u).getFlag_room());
                        return;
                    }
                }*/

            }

        }

        if (listUpload.size() == 0) {
            if (fromList.equalsIgnoreCase("show")) {
                new posTask().execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + POSDETAIL, username, idTab, idDetail);
            } else if (fromList.equalsIgnoreCase("hide")) {
                new posTask().execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + PULLDETAIL, username, idTab, idDetail);
            } else {
                if (idDetail != null || !idDetail.equalsIgnoreCase("")) {
                    String[] ff = idDetail.split("\\|");
                    if (ff.length == 2) {
                        new posTask().execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + PULLMULIPLEDETAIL, username, idTab, idDetail);
                    } else {
                        new posTask().execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + POSDETAIL, username, idTab, idDetail);
                    }
                }
            }
        } else {
            uploadFileChild("looping");
        }
    }

    private String jsonCreateTypeChild(String idContent, String type, String f, String idlisttask, String idDetailss) {
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


    public void addSpinner(final String jsonValue, final String namedb, final LinearLayout view, final String table, final String[] coloum, final Integer from, final String where, final String idListTask, final String type, final String finalI24, final String name) {
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
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
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

    public void addSpinnerDinamics(final String[] jsonValue, final String namedb, final LinearLayout view, final String table, final String[] coloum, final Integer from, final String where, final String idListTask, final String type, final String finalI24, final String name) {

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


                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
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


    private void requestLocationInfo(String idDetail, String username, String idTab, String idListTask, String type, String name, String f) {
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

    public void captureGalery(String idDetail, String username, String idTab, String idListTask, String type, String name, String flag, int facing, String idii) {

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
                AnncaConfiguration.Builder photo = new AnncaConfiguration.Builder(activity, REQ_CAMERA);
                photo.setMediaAction(AnncaConfiguration.MEDIA_ACTION_PHOTO);
                photo.setMediaQuality(AnncaConfiguration.MEDIA_QUALITY_MEDIUM);
                photo.setCameraFace(AnncaConfiguration.CAMERA_FACE_FRONT);
                photo.setMediaResultBehaviour(AnncaConfiguration.PREVIEW);
                new Annca(photo.build()).launchCamera();
             /*   new Annca(photo.build()).launchCamera();
                String action = Intent.ACTION_GET_CONTENT;
                Intent cf= new Intent(context, CameraActivity.class);
                cf.putExtra("face","No");
                cf.putExtra("res","Medium");
                cf.putExtra("front","Yes");
                cf.putExtra("hide","No");
                cf.putExtra("type","16 x 9");
                File f = MediaProcessingUtil.getOutputFile("jpeg");
                cf.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                cf.setAction(action);
                startActivityForResult(cf, REQ_CAMERA);*/
            } else if (facing == 0) {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                AnncaConfiguration.Builder photo = new AnncaConfiguration.Builder(activity, REQ_CAMERA);
                photo.setMediaAction(AnncaConfiguration.MEDIA_ACTION_PHOTO);
                photo.setMediaQuality(AnncaConfiguration.MEDIA_QUALITY_MEDIUM);
                photo.setCameraFace(AnncaConfiguration.CAMERA_FACE_REAR);
                photo.setMediaResultBehaviour(AnncaConfiguration.PREVIEW);

                new Annca(photo.build()).launchCamera();

                   /* String action = Intent.ACTION_GET_CONTENT;
                    Intent cf= new Intent(context, CameraActivity.class);
                    cf.putExtra("face","No");
                    cf.putExtra("res","Medium");
                    cf.putExtra("front","No");
                    cf.putExtra("hide","No");
                    cf.putExtra("type","16 x 9");
                    File f = MediaProcessingUtil.getOutputFile("jpeg");
                    cf.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    cf.setAction(action);
                    startActivityForResult(cf, REQ_CAMERA);*/
            }
        }
        if (flag.equalsIgnoreCase("gallery")) {
            showAttachmentDialog(REQ_CAMERA);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_dinamic_detail, menu);
        return true;
    }


    private void showProgressDialogWithTitle(String title, String message) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.show();
    }


    private String jsonDuaObject(String a, String b, String c, String d) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("aa", a);
            obj.put("bb", b);
            Log.w("adabdi1", c);

            if (!c.equalsIgnoreCase("")) {
                Log.w("adabdi2", c);
                obj.put("cc", c);
            }

            if (!d.equalsIgnoreCase("")) {
                Log.w("adabdi2", d);
                obj.put("dd", d);
            }

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

    private JSONObject jsonCheckBoxDua(String cusId, String checkId, String titleId, String idT, String idS, String val, String note) {
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


    public static JSONObject function(JSONObject obj, String keyMain, String newValue) throws Exception {
        if (obj == null) {
            obj = new JSONObject();
            try {
                obj.put(keyMain, newValue);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
                    if (tt.equalsIgnoreCase("dropdown_dinamis") || tt.equalsIgnoreCase("new_dropdown_dinamis") || tt.equalsIgnoreCase("dropdown_form")) {
                        JSONObject jObject = new JSONObject(value);
                        String url = jObject.getString("url");
                        String[] aa = url.split("/");
                        final String nama = aa[aa.length - 1].toString();

                        File newDB = new File(DataBaseDropDown.getDatabaseFolder() + nama);
                        if (newDB.exists()) {
                            newDB.delete();
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
        Log.w("aa", requestCode + "");

        if (requestCode == REQ_CAMERA) {
            List value = (List) hashMap.get(dummyIdDate);
            if (resultCode == RESULT_OK) {
                String returnString = data.getStringExtra(AnncaConfiguration.Arguments.FILE_PATH);
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
                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, myBase64Image, jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
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
                    String returnString = data.getStringExtra(AnncaConfiguration.Arguments.FILE_PATH);
                    p.putExtra("data", returnString);
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

            Log.w("mae", valueIWantToSend);
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
                    Log.w("bersama", data);

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
                        String include_status_task = "0";
                        if (data.contains("include_status_task")) {
                            include_status_task = jsonRootObject.getString("include_status_task");
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
                            RoomsDetail orderModel = new RoomsDetail(idDetail, id_rooms_tab, username, jsonRootObject.getString("list_pull"), "", time_str, "value");
                            db.insertRoomsDetail(orderModel);
                        }

                        Log.w("IK : ", content);

                        String ccc = jsonDuaObject(content, attachment, api_officers, include_status_task);
                        if (include_assignto.equalsIgnoreCase("0")) {
                            ccc = jsonDuaObject(content, attachment, "", include_status_task);
                        }


                        RoomsDetail orderModel = new RoomsDetail(username, id_rooms_tab, username, ccc, "", time_str, "form");
                        db.insertRoomsDetail(orderModel);


                    } catch (JSONException e) {
                        e.printStackTrace();
                        finish();
                        error = "Tolong periksa koneksi internet.";
                    }
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


    private class posTask extends AsyncTask<String, String, String> {

        String error = "";

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            postData(params[0], params[1], params[2], params[3]);
            return null;
        }

        protected void onPostExecute(String result) {
            if (error.length() > 0) {
                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
            }
            //dialog.dismiss();
        }

        protected void onProgressUpdate(String... string) {
        }

        public void postData(String valueIWantToSend, final String usr, final String idr, final String idDetail) {
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
                nameValuePairs.add(new BasicNameValuePair("id_detail_tab", idDetail));

                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "assignTo", "");
                if (cEdit.getCount() > 0) {
                    String cc = cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));
                    String has = "";
                    if (cc.contains("All")) {
                        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(context);
                        Cursor curr = dataBaseHelper.selectAll("room", username, idTab);
                        if (curr.getCount() > 0) {
                            if (curr.moveToFirst()) {
                                do {
                                    if (has.length() == 0) {
                                        has = curr.getString(5);
                                    } else {
                                        has += "," + curr.getString(5);
                                    }

                                } while (curr.moveToNext());
                            }
                        }
                    } else {
                        String[] su = cc.split(",");
                        for (String ss : su) {
                            if (has.length() == 0) {
                                has = ss.split(" - ")[1];
                            } else {
                                has += "," + ss.split(" - ")[1];
                            }
                        }
                    }


                    nameValuePairs.add(new BasicNameValuePair("assign_to", has));

                }


                if (includeStatus) {
                    Cursor cucuTv = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "includeStatus", "");
                    if (cucuTv.getCount() > 0) {
                        String cucuTvi = cucuTv.getString(cucuTv.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));
                        String resultti = "0";
                        if (cucuTvi.equalsIgnoreCase("Approve")) {
                            resultti = "1";
                        } else if (cucuTvi.equalsIgnoreCase("Done")) {
                            resultti = "2";
                        }
                        nameValuePairs.add(new BasicNameValuePair("status_task", resultti));
                    }
                }


                if (!isReject.equalsIgnoreCase("")) {
                    nameValuePairs.add(new BasicNameValuePair("is_reject", isReject));

                }


                Cursor cursorParent = db.getSingleRoomDetailFormWithFlag(idDetail, usr, idr, "parent");


                if (cursorParent.getCount() > 0) {
                    if (!cursorParent.getString(cursorParent.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)).equalsIgnoreCase("")) {
                        nameValuePairs.add(new BasicNameValuePair("latlong_before", jsonResultType(cursorParent.getString(cursorParent.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "a")));
                        nameValuePairs.add(new BasicNameValuePair("latlong_after", jsonResultType(cursorParent.getString(cursorParent.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "b")));
                    } else {
                        nameValuePairs.add(new BasicNameValuePair("latlong_before", "null"));
                        nameValuePairs.add(new BasicNameValuePair("latlong_after", "null"));
                    }
                } else {
                    nameValuePairs.add(new BasicNameValuePair("latlong_before", "null"));
                    nameValuePairs.add(new BasicNameValuePair("latlong_after", "null"));
                }

                if (fromList.equalsIgnoreCase("hide") || fromList.equalsIgnoreCase("hideMultiple") || fromList.equalsIgnoreCase("showMultiple")) {

                    if (idDetail != null || !idDetail.equalsIgnoreCase("")) {
                        String[] ff = idDetail.split("\\|");
                        if (ff.length == 2) {
                            nameValuePairs.add(new BasicNameValuePair("parent_id", ff[1]));
                            nameValuePairs.add(new BasicNameValuePair("id_list_push", ff[0]));
                        }
                    }
                }

                MessengerDatabaseHelper messengerHelper = null;
                if (messengerHelper == null) {
                    messengerHelper = MessengerDatabaseHelper.getInstance(context);
                }

                Contact contact = messengerHelper.getMyContact();
                nameValuePairs.add(new BasicNameValuePair("bc_user", contact.getJabberId()));


                ArrayList<RoomsDetail> list = db.allRoomDetailFormWithFlag(idDetail, usr, idr, "cild");


                for (int u = 0; u < list.size(); u++) {

                    JSONArray jsA = null;
                    String content = "";

                    String cc = list.get(u).getContent();
                    Log.w("cinta", cc);

                    if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("input_kodepos") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("dropdown_wilayah")) {
                        cc = jsoncreateC(list.get(u).getContent());
                    } else if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("form_child")) {

                    } else if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("dropdown_form")) {
                        try {
                            JSONObject jsonObject = new JSONObject(cc);
                            Iterator<String> iter = jsonObject.keys();

                            JSONObject jsHead = new JSONObject();
                            JSONArray jsAU = new JSONArray();
                            while (iter.hasNext()) {
                                JSONObject joN = new JSONObject();
                                String key = iter.next();
                                try {
                                    JSONArray jsAdd = jsonObject.getJSONArray(key);
                                    JSONArray newJS = new JSONArray();
                                    for (int ic = 0; ic < jsAdd.length(); ic++) {
                                        JSONObject oContent = new JSONObject(jsAdd.get(ic).toString());
                                        String lastCusID = oContent.getString("iD");
                                        String val = oContent.getString("v");
                                        String not = oContent.getString("n");

                                        JSONObject jOdetail = new JSONObject();
                                        jOdetail.put("id", lastCusID);
                                        jOdetail.put("val", val);
                                        jOdetail.put("note", not);

                                        newJS.put(jOdetail);

                                    }
                                    joN.put("id", key.toString());
                                    joN.put("checklists", newJS);
                                    jsAU.put(joN);

                                } catch (JSONException e) {
                                    // Something went wrong!
                                }
                            }

                            jsHead.put("outlet_id", customersId);
                            jsHead.put("audit", jsAU);
                            cc = jsHead.toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                            nameValuePairs.add(new BasicNameValuePair("key[]", list.get(u).getFlag_tab()));
                            nameValuePairs.add(new BasicNameValuePair("value[]", list.get(u).getContent()));
                            nameValuePairs.add(new BasicNameValuePair("type[]", jsonResultType(list.get(u).getFlag_content(), "b")));
                        } else if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("dropdown_form")) {
                            Log.w("disini", cc);

                            nameValuePairs.add(new BasicNameValuePair("key[]", list.get(u).getFlag_tab()));
                            nameValuePairs.add(new BasicNameValuePair("value[]", cc));
                            nameValuePairs.add(new BasicNameValuePair("type[]", jsonResultType(list.get(u).getFlag_content(), "b")));
                        } else {
                            try {
                                for (int ic = 0; ic < jsA.length(); ic++) {
                                    final String icC = jsA.getJSONObject(ic).getString("c").toString();
                                    content += icC + "|";
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            nameValuePairs.add(new BasicNameValuePair("key[]", list.get(u).getFlag_tab()));
                            nameValuePairs.add(new BasicNameValuePair("value[]", content.substring(0, content.length() - 1)));
                            nameValuePairs.add(new BasicNameValuePair("type[]", jsonResultType(list.get(u).getFlag_content(), "b")));
                        }
                    } else {
                        nameValuePairs.add(new BasicNameValuePair("key[]", list.get(u).getFlag_tab()));
                        nameValuePairs.add(new BasicNameValuePair("value[]", list.get(u).getContent()));
                        nameValuePairs.add(new BasicNameValuePair("type[]", jsonResultType(list.get(u).getFlag_content(), "b")));
                    }
                }

                Log.w("harlem", nameValuePairs.toString());

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);

                    if (data.equalsIgnoreCase("1")) {
                        DinamicRoomTaskActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                db.deleteRoomsDetailbyId(idDetail, idTab, usr);
                                Toast.makeText(context, "Upload Berhasil.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        finish();
                    } else {
                        DinamicRoomTaskActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                long date = System.currentTimeMillis();
                                String dateString = hourFormat.format(date);
                                RoomsDetail orderModel = new RoomsDetail(idDetail, idr, usr, dateString, "3", null, "parent");
                                db.updateDetailRoomWithFlagContentParent(orderModel);
                                Toast.makeText(context, "Upload Gagal.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        finish();
                    }
                } else {
                    DinamicRoomTaskActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            long date = System.currentTimeMillis();
                            String dateString = hourFormat.format(date);

                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, dateString, "3", null, "parent");
                            db.updateDetailRoomWithFlagContentParent(orderModel);

                            Toast.makeText(context, "Upload Gagal.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    finish();
                    error = "Tolong periksa koneksi internet.";
                }

            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                DinamicRoomTaskActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        long date = System.currentTimeMillis();
                        String dateString = hourFormat.format(date);

                        RoomsDetail orderModel = new RoomsDetail(idDetail, idr, usr, dateString, "3", null, "parent");
                        db.updateDetailRoomWithFlagContentParent(orderModel);

                        Toast.makeText(context, "Tolong periksa koneksi internet.", Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
            } catch (ClientProtocolException e) {
                DinamicRoomTaskActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        long date = System.currentTimeMillis();
                        String dateString = hourFormat.format(date);

                        RoomsDetail orderModel = new RoomsDetail(idDetail, idr, usr, dateString, "3", null, "parent");
                        db.updateDetailRoomWithFlagContentParent(orderModel);

                        Toast.makeText(context, "Tolong periksa koneksi internet.", Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
                // TODO Auto-generated catch block
            } catch (IOException e) {
                DinamicRoomTaskActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        long date = System.currentTimeMillis();
                        String dateString = hourFormat.format(date);

                        RoomsDetail orderModel = new RoomsDetail(idDetail, idr, usr, dateString, "3", null, "parent");
                        db.updateDetailRoomWithFlagContentParent(orderModel);

                        Toast.makeText(context, "Tolong periksa koneksi internet.", Toast.LENGTH_SHORT).show();
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

    protected Dialog onCreateDialog(int id) {
        if (id == TIME_DIALOG_ID) {

            Calendar mcurrentTime = Calendar.getInstance();
            int hours = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    this, mTimePicker, hours, minute, true);
            timePickerDialog.setOnDismissListener(mOnDismissListenerTime);
            return timePickerDialog;
        }

        DatePickerDialog dialog = new DatePickerDialog(this,
                mDateSetListener
                , mYear, mMonth, mDay);

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
            aa = GET(aaa.getUrl());
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
                startActivity(getIntent());
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
                                    String column2 = c.getString(1);
                                    hashMapss.put(column1, column2);
                                    spinnerArray.add(column1);
                                } while (c.moveToNext());
                            }
                            c.close();

                            outerMap.put(Integer.valueOf(valueForms.get(0).toString()), hashMapss);

                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            newSpinner[Integer.valueOf(valueForms.get(0).toString())].setAdapter(spinnerArrayAdapter);


                            Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(String.valueOf(pair.getKey()), valueForms.get(2).toString(), valueForms.get(5).toString()));
                            if (cursorCild.getCount() > 0) {

                                Log.w("bubffu", cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));

                                try {
                                    JSONObject jsonObject = new JSONObject(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));

                                    String titl = jsonObject.getString(value.get(2).toString()).split("\n\nKode")[0];
                                    Log.w("ss", titl);


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
                    Iterator it = hashMapDropForm.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        List value = (List) hashMapDropForm.get(pair.getKey());
                        String DBmaster = value.get(0).toString();

                        String Formulamaster = value.get(1).toString();

                        JSONObject jObjectFormula = null;
                        try {
                            HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();
                            HashMap<String, List<JSONObject>> expandableListDetailJSONObject = new HashMap<String, List<JSONObject>>();
                            List<String> expandableListTitle = new ArrayList<String>();
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
                                        String column1 = c.getString(1);
                                        String column2 = c.getString(2);
                                        Integer column3 = c.getInt(3);
                                        String column4 = c.getString(4);

                                        JSONObject obj = new JSONObject();
                                        JSONObject objS = new JSONObject();
                                        try {
                                            obj.put("t", column4);
                                            obj.put("iT", String.valueOf(column3));
                                            obj.put("iD", String.valueOf(column0));

                                            objS.put("iD", String.valueOf(column0));
                                            objS.put("v", "0");
                                            objS.put("n", "");

                                        } catch (JSONException e) {
                                            // TODO Auto-generated catch block e.printStackTrace();
                                        }
                                        Item.add(obj.toString());
                                        Items.add(objS);

                                        expandableListDetail.put(title, Item);
                                        expandableListDetailJSONObject.put(titleS, Items);


                                        //  jsonArray.put(jsonCheckBoxDua(customersId, String.valueOf(column0), String.valueOf(column3), String.valueOf(t), String.valueOf(Item.size() - 1), "0", ""));
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


                            ExpandableListAdapter listAdapter = new ExpandableListAdapter(getApplicationContext(), expandableListTitle, expandableListDetail, idDetail, username, idTab, jsonCreateType(String.valueOf(pair.getKey()), valueForms.get(2).toString(), valueForms.get(5).toString()), valueForms.get(3).toString());
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
    public void onLocationPermissionPermanentlyDeclined(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {

    }

    @Override
    public void onNeedLocationSettingsChange() {

    }

    @Override
    public void onFallBackToSystemSettings(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {

    }

    @Override
    public void onNewLocationAvailable(Location location) {

    }

    @Override
    public void onMockLocationsDetected(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {
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

    private class UploadFileToServer extends AsyncTask<String, Integer, String> {
        long totalSize = 0;
        private ProgressDialog Dialog = new ProgressDialog(context);
        String valueSS, valueS, getId, getParent_tab, getParent_room, uri, getFlag_content, getFlag_tab, getFlag_room;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return uploadFile(params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7], params[8], params[9], params[10], params[11], params[12]);
        }

        @SuppressWarnings("deprecation")
        private String uploadFile(String URL, String username, String id_room, String id_list, String valueSS_, String valueS_, String getId_, String getParent_tab_, String getParent_room_, String _uri, String getFlag_content_, String getFlag_tab_, String getFlag_room_) {
            String responseString = null;
            valueSS = valueSS_;
            valueS = valueS_;
            getId = getId_;
            getParent_tab = getParent_tab_;
            getParent_room = getParent_room_;
            uri = _uri;
            getFlag_content = getFlag_content_;
            getFlag_tab = getFlag_tab_;
            getFlag_room = getFlag_room_;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(URL);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));

                            }
                        });

                File sourceFile = new File(resizeAndCompressImageBeforeSend(context, uri, "fileUploadBC_" + new Date().getTime() + ".jpg"));
                if (!sourceFile.exists()) {
                    return "File not exists";
                }

                ContentType contentType = ContentType.create("image/jpeg");
                entity.addPart("username_room", new StringBody(username));
                entity.addPart("id_rooms_tab", new StringBody(id_room));
                entity.addPart("id_list_task", new StringBody(id_list));
                entity.addPart("value", new FileBody(sourceFile, contentType, sourceFile.getName()));


                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode == 200) {
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
            try {
                JSONObject jsonObject = new JSONObject(result);
                String message = jsonObject.getString("message");
                if (message.length() == 0) {
                    String fileNameServer = jsonObject.getString("filename");
                    String aadc = valueSS.replace("\"value\":\"" + (valueS.replace("/", "\\/")) + "\"", "\"value\":" + "\"" + fileNameServer + ";" + (uri.replace("/", "\\/")) + "\"");
                    RoomsDetail orderModel = new RoomsDetail(getId, getParent_tab, getParent_room, aadc, getFlag_content, getFlag_tab, getFlag_room);
                    db.updateDetailRoomWithFlagContent(orderModel);
                    uploadFileChild("looping");
                } else {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }
    }

    public static String resizeAndCompressImageBeforeSend(Context context, String filePath, String fileName) {
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


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
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


    private class UploadFileToServerB extends AsyncTask<String, Integer, String> {
        long totalSize = 0;
        private ProgressDialog Dialog = new ProgressDialog(context);
        String valueS, getId, getParent_tab, getParent_room, uri, getFlag_content, getFlag_tab, getFlag_room;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return uploadFile(params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7], params[8], params[9], params[10]);
        }

        @SuppressWarnings("deprecation")
        private String uploadFile(String URL, String username, String id_room, String id_list, String getId_, String getParent_tab_, String getParent_room_, String _uri, String getFlag_content_, String getFlag_tab_, String getFlag_room_) {
            String responseString = null;
            getId = getId_;
            getParent_tab = getParent_tab_;
            getParent_room = getParent_room_;
            uri = _uri;
            getFlag_content = getFlag_content_;
            getFlag_tab = getFlag_tab_;
            getFlag_room = getFlag_room_;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(URL);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));

                            }
                        });


                ContentType contentType = ContentType.create("image/jpeg");
                entity.addPart("username_room", new StringBody(username));
                entity.addPart("id_rooms_tab", new StringBody(id_room));
                entity.addPart("id_list_task", new StringBody(id_list));
                entity.addPart("file_input", new StringBody("base64"));
                entity.addPart("value", new StringBody(uri));


                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode == 200) {
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
            try {
                JSONObject jsonObject = new JSONObject(result);
                String message = jsonObject.getString("message");
                if (message.length() == 0 || message.contains("null")) {
                    String fileNameServer = jsonObject.getString("filename");
                    RoomsDetail orderModel = new RoomsDetail(getId, getParent_tab, getParent_room, jsonDuaObject(uri, fileNameServer, "", ""), getFlag_content, getFlag_tab, getFlag_room);
                    db.updateDetailRoomWithFlagContent(orderModel);
                    uploadFileChild("looping");
                } else {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }

    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
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

            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

            totalHeight += groupItem.getMeasuredHeight();
            if (((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null,
                            listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                    totalHeight += listItem.getMeasuredHeight();
                }
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
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
                Toast.makeText(context, "Form success download.", Toast.LENGTH_SHORT).show();
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

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {

                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);
                    Log.w("hasil", data);
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

}