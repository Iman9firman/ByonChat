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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
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
import android.widget.FrameLayout;
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
import com.byonchat.android.DownloadFileByonchat;
import com.byonchat.android.DownloadSqliteDinamicActivity;
import com.byonchat.android.DownloadUtilsActivity;
import com.byonchat.android.FragmentSLA.ZhFourFragment;
import com.byonchat.android.FragmentSLA.ZhOneFragment;
import com.byonchat.android.ISSActivity.LoginDB.UserDB;
import com.byonchat.android.R;
import com.byonchat.android.ReaderOcr;
import com.byonchat.android.ZoomImageViewActivity;
import com.byonchat.android.adapter.ExpandableListAdapter;
import com.byonchat.android.adapter.SLAISSAdapter;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.communication.NotificationReceiver;
import com.byonchat.android.createMeme.FilteringImage;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.list.AttachmentAdapter;
import com.byonchat.android.list.IconItem;
import com.byonchat.android.list.utilLoadImage.ImageLoaderLarge;
import com.byonchat.android.location.ActivityDirection;
import com.byonchat.android.model.AddChildFotoExModel;
import com.byonchat.android.model.SLAISSItem;
import com.byonchat.android.personalRoom.asynctask.ProfileSaveDescription;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.ChatParty;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.DataBaseDropDown;
import com.byonchat.android.provider.DataBaseHelper;
import com.byonchat.android.provider.DatabaseKodePos;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.RadioButtonCheckDB;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.tempSchedule.MyEventDatabase;
import com.byonchat.android.utils.AllAboutUploadTask;
import com.byonchat.android.utils.AndroidMultiPartEntity;
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
import com.multilevelview.MultiLevelRecyclerView;
import com.multilevelview.models.RecyclerViewItem;
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
import java.io.FileWriter;
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

import static com.byonchat.android.provider.RadioButtonCheckDB.COLUMN_COMMENT;
import static com.byonchat.android.provider.RadioButtonCheckDB.COLUMN_ID;
import static com.byonchat.android.provider.RadioButtonCheckDB.COLUMN_ID_DETAIL;
import static com.byonchat.android.provider.RadioButtonCheckDB.COLUMN_IMG;
import static com.byonchat.android.provider.RadioButtonCheckDB.COLUMN_OK;
import static com.byonchat.android.provider.RadioButtonCheckDB.TABLE_NAME;

public class DinamicPerformanceActivity extends AppCompatActivity {

    private TextView textProgress;
    private int countCheck = 0;

    DecimalFormat df2 = new DecimalFormat("#.##");

    private MultiLevelRecyclerView recyclerView;
    SearchableSpinner spinner;
    private SLAISSAdapter adapter;
    private List<SLAISSItem> itemList = new ArrayList<>();
    private FrameLayout container;
    public ArrayList<String> listSubmittedId;
    private String a, be, c, d;

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
    // ExpandableListView expandableListView[];
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


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dinamic_slatask);
        context = this;

        mainScrooll = (ScrollView) findViewById(R.id.mainScrooll);
        linearLayout = (LinearLayout) findViewById(R.id.linear);

        if (db == null) {
            db = BotListDB.getInstance(context);
        }

        btnSUMBIT = (LinearLayout) getLayoutInflater().inflate(R.layout.button_submit_form, null);

        spinner = (SearchableSpinner) findViewById(R.id.spinner);

        ArrayList<String> spinnerArraySla = new ArrayList<String>();
        ArrayList<String> duaJJt = new ArrayList<>();
        spinnerArraySla.add("--Please Select--");

        spinnerArraySla.add("Budi Gunawan - 66666");
        spinnerArraySla.add("Ahmad Bustomi - 77777");
        spinnerArraySla.add("Ahmad Jurfianto - 88888");
        spinnerArraySla.add("Bambang Pamungkas - 55555");

        LinearLayout masterlinearValue = (LinearLayout) findViewById(R.id.linearValue);

        LinearLayout MMlinearValue = (LinearLayout) getLayoutInflater().inflate(R.layout.list_value_form_detail, null);
        LinearLayout linearDetailValue = (LinearLayout) MMlinearValue.findViewById(R.id.linearDetailValue);

        linearDetailValue.setBackgroundResource(R.drawable.backgroud_gray_young);


        masterlinearValue.addView(MMlinearValue);
        LinearLayout.LayoutParams parampps = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parampps.setMargins(0, 0, 0, 10);
        MMlinearValue.setLayoutParams(parampps);


        LinearLayout submitLinear = (LinearLayout) MMlinearValue.findViewById(R.id.linearSubmit);

        TextView textSubmit = new TextView(DinamicPerformanceActivity.this);
        textSubmit.setText(Html.fromHtml("Submit From"));
        textSubmit.setTextSize(17);
        textSubmit.setLayoutParams(new TableRow.LayoutParams(0));

        TextView etVIn1 = (TextView) new TextView(context);
        etVIn1.setTextIsSelectable(true);
        etVIn1.setText(Html.fromHtml("no_telp"));
        TextView etVIn2 = (TextView) new TextView(context);
        etVIn2.setTextIsSelectable(true);
        etVIn2.setText(Html.fromHtml("name"));
        TextView etVIn3 = (TextView) new TextView(context);
        etVIn3.setTextIsSelectable(true);
        etVIn3.setText(Html.fromHtml("divisi"));
        TextView etVIn4 = (TextView) new TextView(context);
        etVIn4.setTextIsSelectable(true);
        etVIn4.setText(Html.fromHtml("jabatan"));
        TextView etVIn5 = (TextView) new TextView(context);
        etVIn5.setTextIsSelectable(true);
        etVIn5.setText(Html.fromHtml("lokasi"));
        TextView etVIn6 = (TextView) MMlinearValue.findViewById(R.id.dateTxt);
        etVIn6.setTextIsSelectable(true);
        etVIn6.setText("add_date");

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


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                if (linearLayout.getChildCount() > 0) {
                    linearLayout.removeAllViews();
                }

                if (spinner.getSelectedItem().toString().equalsIgnoreCase("--Please Select--")) {
                    return;
                }

                if (spinner.getSelectedItem().toString().equalsIgnoreCase("Bambang Pamungkas - 55555")) {
                    String sft[] = {"", "Pengetahuan dan kemampuan Penggunaan perangkat kerja", "Pengetahuan dan Penerapan SOP", "Pengetahuan dan Penerapan K3"};
                    for (int i = 1; i < sft.length; i++) {
                        TextView textView = new TextView(DinamicPerformanceActivity.this);
                        textView.setText(Html.fromHtml("<b>" + i + ". " + sft[i] + ".</>"));
                        textView.setTextSize(15);
                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(30, 10, 30, 0);
                        textView.setLayoutParams(params2);
                        linearLayout.addView(textView);

                        RatingBar rat = (RatingBar) getLayoutInflater().inflate(R.layout.costume_rating, null);
                        final LinearLayout.LayoutParams testLP = new LinearLayout.LayoutParams(
                                AppBarLayout.LayoutParams.WRAP_CONTENT, AppBarLayout.LayoutParams.MATCH_PARENT);
                        testLP.setMargins(10, 20, 10, 10);
                        testLP.gravity = Gravity.CENTER_HORIZONTAL;
                        rat.setLayoutParams(testLP);
                        linearLayout.addView(rat);
                    }
                } else {

                    String sft[] = {"", "Team Work", "Absensi", "Costumer Focus"};
                    for (int i = 1; i < sft.length; i++) {
                        TextView textView = new TextView(DinamicPerformanceActivity.this);
                        textView.setText(Html.fromHtml("<b>" + i + ". " + sft[i] + ".</>"));
                        textView.setTextSize(15);
                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(30, 10, 30, 0);
                        textView.setLayoutParams(params2);
                        linearLayout.addView(textView);

                        RatingBar rat = (RatingBar) getLayoutInflater().inflate(R.layout.costume_rating, null);
                        final LinearLayout.LayoutParams testLP = new LinearLayout.LayoutParams(
                                AppBarLayout.LayoutParams.WRAP_CONTENT, AppBarLayout.LayoutParams.MATCH_PARENT);
                        testLP.setMargins(10, 20, 10, 10);
                        testLP.gravity = Gravity.CENTER_HORIZONTAL;
                        rat.setLayoutParams(testLP);
                        linearLayout.addView(rat);
                    }
                }

                linearLayout.addView(btnSUMBIT);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });










/*
        TextView textView = new TextView(DinamicPerformanceActivity.this);
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

       */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            spinner.setBackground(getResources().getDrawable(R.drawable.spinner_background));
        }

        ArrayAdapter<String> spinnerArrayAdapterSLA = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArraySla); //selected item will look like a spinner set from XML
        spinnerArrayAdapterSLA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapterSLA);


        if (showButton) {


            b = (Button) btnSUMBIT.findViewById(R.id.btn_submit);

            final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(5, 15, 0, 0);
            btnSUMBIT.setLayoutParams(params);
            linearLayout.addView(btnSUMBIT);

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        } else {
            btnSUMBIT.setVisibility(View.GONE);
        }


    }

}


