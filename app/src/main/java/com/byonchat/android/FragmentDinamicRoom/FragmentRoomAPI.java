package com.byonchat.android.FragmentDinamicRoom;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.byonchat.android.CaptureSignature;
import com.byonchat.android.DownloadSqliteDinamicActivity;
import com.byonchat.android.DownloadUtilsActivity;
import com.byonchat.android.R;
import com.byonchat.android.ReaderOcr;
import com.byonchat.android.WebViewByonActivity;
import com.byonchat.android.ZoomImageViewActivity;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.list.AttachmentAdapter;
import com.byonchat.android.list.utilLoadImage.ImageLoaderLarge;
import com.byonchat.android.location.ActivityDirection;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.DataBaseDropDown;
import com.byonchat.android.provider.DataBaseHelper;
import com.byonchat.android.provider.DatabaseKodePos;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.utils.GPSTracker;
import com.byonchat.android.utils.ImageFilePath;
import com.byonchat.android.utils.MediaProcessingUtil;
import com.byonchat.android.widget.ContactsCompletionView;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.tokenautocomplete.FilteredArrayAdapter;
import com.tokenautocomplete.TokenCompleteTextView;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by lukma on 3/4/2016.
 */
@SuppressLint("ValidFragment")
public class FragmentRoomAPI extends Fragment {

    String title;
    String urlTembak;
    String username;
    String idRoomTab;
    BotListDB db;
    String latLong;

    LinearLayout linearLayout;
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
    private static final String MENU_GALLERY_TITLE = "Gallery";
    private ProgressDialog progressDialog;
    private ArrayList<AttachmentAdapter.AttachmentMenuItem> curAttItems;
    String cameraFileOutput;
    public static final SimpleDateFormat hourFormat = new SimpleDateFormat(
            "HH:mm:ss dd/MM/yyyy", Locale.getDefault());
    ImageView imageView[];
    LinearLayout linearEstimasi[];
    RatingBar rat[];
    EditText et[];
    TextView tp[];
    SearchableSpinner newSpinner[];
    Map<Integer, List<String>> hashMap = new HashMap<Integer, List<String>>();
    Map<Integer, List<String>> hashMapOcr = new HashMap<Integer, List<String>>();
    Integer count;
    boolean showButton = true;
    double latitude, longitude;
    GPSTracker gps;
    String idDetail = "11";
    int mYear, mMonth, mDay;
    int dummyIdDate;
    Bitmap result = null;
    boolean deleteContent = false;
    private int attCurReq = 0;
    private String urlPost;
    Boolean showDialog = true;
    private static ArrayList<AttachmentAdapter.AttachmentMenuItem> attCameraItems;
    private static ArrayList<AttachmentAdapter.AttachmentMenuItem> attVideoItems;
    private String[] arrMonth = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private View sss;
    private Activity mContext;

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

    public FragmentRoomAPI(Activity ctx) {
        mContext = ctx;

    }

    public static FragmentRoomAPI newInstance(String tit, String utm, String usr, String idrtab, String latLong, Activity ctx) {
        FragmentRoomAPI fragmentRoomTask = new FragmentRoomAPI(ctx);
        Bundle args = new Bundle();
        args.putString("aa", tit);
        args.putString("bb", utm);
        args.putString("cc", usr);
        args.putString("dd", idrtab);
        args.putString("ll", latLong);
        fragmentRoomTask.setArguments(args);
        return fragmentRoomTask;
    }

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getArguments().getString("aa");
        urlTembak = getArguments().getString("bb");
        username = getArguments().getString("cc");
        idRoomTab = getArguments().getString("dd");
        latLong = getArguments().getString("ll");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sss = inflater.inflate(R.layout.room_fragment_api, container, false);
        generateLayut();
        return sss;
    }

    public void generateLayut() {
        linearLayout = (LinearLayout) sss.findViewById(R.id.linear);
        linearLayout.removeAllViewsInLayout();
        if (db == null) {
            db = BotListDB.getInstance(mContext);
        }


        RelativeLayout relativeLayout = (RelativeLayout) mContext.getLayoutInflater().inflate(R.layout.btn_refresh_layout, null);
        ImageButton btn_refresh = (ImageButton) relativeLayout.findViewById(R.id.btn_refresh_api);

        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder alertbox = new AlertDialog.Builder(mContext);
                alertbox.setMessage("Are you sure you want to refresh this form?");
                alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        new Refresh(mContext).execute(urlTembak, username, idRoomTab);
                    }
                });
                alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
                alertbox.show();


            }
        });

        linearLayout.addView(relativeLayout);

        Cursor cursor = db.getSingleRoomDetailForm(username, idRoomTab);


        if (cursor.getCount() > 0) {
            final String content = cursor.getString(cursor.getColumnIndexOrThrow(BotListDB.ROOM_CONTENT));

            long date = System.currentTimeMillis();
            String dateString = hourFormat.format(date);
            String latLongs = "0.0|0.0";


            if (latLong.equalsIgnoreCase("1")) {
                gps = new GPSTracker(mContext);
                if (!gps.canGetLocation()) {
                    startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQ_LOCATION_SETTING);
                } else {
                    if (gps.canGetLocation()) {
                        latitude = gps.getLatitude();
                        longitude = gps.getLongitude();
                        if (latitude == 0.0 && longitude == 0.0) {
                            mContext.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getContext(), "Harap coba kembali dalam waktu 1 menit, karena data gps anda sedang diaktifkan", Toast.LENGTH_LONG).show();
                                }
                            });
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

            RoomsDetail orderModelM = new RoomsDetail(idDetail, idRoomTab, username, dateString, "0", firstLat, "parent");

            db.insertRoomsDetail(orderModelM);

            try {
                if (count != null) {
                    count = null;
                }


                JSONObject jsonRootObject = new JSONObject(content);
                urlPost = jsonRootObject.getString("api_url");
                String data = jsonRootObject.getString("data");
                JSONArray jsonArray = new JSONArray(data);


                et = new EditText[jsonArray.length()];
                tp = new TextView[jsonArray.length()];
                rat = new RatingBar[jsonArray.length()];
                linearEstimasi = new LinearLayout[jsonArray.length()];
                imageView = new ImageView[jsonArray.length()];
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

                    if (type.equalsIgnoreCase("dropdown_api")) {

                        TextView textView = new TextView(mContext);
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

                        linearEstimasi[count] = (LinearLayout) mContext.getLayoutInflater().inflate(R.layout.layout_child, null);

                        final ArrayList<String> spinnerArray = new ArrayList<String>();
                        final ArrayList<String> spinnerArrayNumb = new ArrayList<String>();
                        spinnerArray.add("--Please Select--");
                        spinnerArrayNumb.add("0");

                        final LinearLayout spinerTitle = (LinearLayout) mContext.getLayoutInflater().inflate(R.layout.item_spiner_textview, null);
                        TextView textViewFirst = (TextView) spinerTitle.findViewById(R.id.title);
                        textViewFirst.setVisibility(View.GONE);

                        textViewFirst.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });

                        final SearchableSpinner spinner = (SearchableSpinner) spinerTitle.findViewById(R.id.spinner);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            spinner.setBackground(getResources().getDrawable(R.drawable.spinner_background));
                        }
                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
                        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(spinnerArrayAdapter);

                        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(getContext());
                        if (dataBaseHelper.checkTable("room_" + idRoomTab)) {
                            Cursor curr = dataBaseHelper.selectAll("room_" + idRoomTab, username, idRoomTab);
                            if (curr.getCount() > 0) {

                                if (curr.moveToFirst()) {
                                    do {
                                        spinnerArray.add(curr.getString(6) + " (" + curr.getString(5) + ")");
                                        spinnerArrayNumb.add(curr.getString(5));
                                    } while (curr.moveToNext());
                                }
                            } else {
                                Toast.makeText(mContext, Html.fromHtml(label) + " not found", Toast.LENGTH_LONG).show();
                            }

                        } else {
                            new RefreshDBAsign(mContext, spinnerArrayAdapter).execute(value, username);
                        }


                        spinnerArrayAdapter.notifyDataSetChanged();


                        final int finalI24 = i;
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                dummyIdDate = Integer.parseInt(idListTask);
                                List nilai = (List) hashMap.get(dummyIdDate);
                                if (!spinner.getSelectedItem().toString().replace("'", "''").equals("--Please Select--")) {
                                    final int counts = linearEstimasi[Integer.valueOf(nilai.get(0).toString())].getChildCount();
                                    linearEstimasi[Integer.valueOf(nilai.get(0).toString())].removeViews(1, counts - 1);

                                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI24)));

                                    if (cEdit.getCount() > 0) {
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, spinnerArrayNumb.get(position), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "cild");
                                        db.updateDetailRoomWithFlagContent(orderModel);
                                    } else {
                                        RoomsDetail orderModel = null;
                                        try {
                                            orderModel = new RoomsDetail(idDetail, idRoomTab, username, spinnerArrayNumb.get(position), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "cild");

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

                        linearEstimasi[count].addView(spinerTitle);

                        TableRow.LayoutParams params2 = new TableRow.LayoutParams(1);
                        params2.setMargins(60, 10, 30, 0);
                        linearEstimasi[count].setLayoutParams(params2);
                        linearLayout.addView(linearEstimasi[count]);


                    } else if (type.equalsIgnoreCase("text")) {
                        TextView textView = new TextView(mContext);
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
                        et[count] = (EditText) mContext.getLayoutInflater().inflate(R.layout.edit_input_layout, null);
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


                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                        if (cursorCild.getCount() > 0) {
                            et[count].setText(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                            et[count].setSelection(et[count].length());
                        } else {
                            if (!value.equalsIgnoreCase("")) {
                                et[count].setText(value);
                                et[count].setSelection(et[count].length());
                                RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, value, jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
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

                                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI)));
                                    if (cEdit.getCount() > 0) {
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI)), name, "cild");
                                        db.updateDetailRoomWithFlagContent(orderModel);

                                    } else {
                                        if (String.valueOf(s).length() > 0) {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI)), name, "cild");
                                            db.insertRoomsDetail(orderModel);
                                        } else {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI)), name, "cild");
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

                        TextView textView = new TextView(mContext);
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
                        et[count] = (EditText) mContext.getLayoutInflater().inflate(R.layout.edit_input_layout, null);

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
                        et[count].setScroller(new Scroller(mContext));
                        et[count].setVerticalScrollBarEnabled(true);
                        et[count].setSingleLine(false);
                        et[count].setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                        et[count].setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(maxlength))});

                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                        if (cursorCild.getCount() > 0) {
                            et[count].setText(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                        } else {
                            if (!value.equalsIgnoreCase("")) {
                                et[count].setText(value);
                                RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, value, jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
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

                                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI1)));
                                    if (cEdit.getCount() > 0) {
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI1)), name, "cild");
                                        db.updateDetailRoomWithFlagContent(orderModel);

                                    } else {
                                        if (String.valueOf(s).length() > 0) {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI1)), name, "cild");
                                            db.insertRoomsDetail(orderModel);
                                        } else {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI1)), name, "cild");
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
                        TextView textView = new TextView(mContext);
                        textView.setText(Html.fromHtml(label));
                        textView.setTextSize(15);
                        textView.setLayoutParams(new TableRow.LayoutParams(0));

                        TextView et;
                        et = (TextView) mContext.getLayoutInflater().inflate(R.layout.text_view_layout, null);
                        et.setScroller(new Scroller(mContext));
                        et.setVerticalScrollBarEnabled(true);
                        et.setSingleLine(false);
                        et.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                        et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(maxlength))});
                        et.setText(value);


                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
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
                    } else if (type.equalsIgnoreCase("date")) {
                        if (count == null) {
                            count = 0;
                        } else {
                            count++;
                        }
                        tp[count] = (TextView) mContext.getLayoutInflater().inflate(R.layout.text_view_layout, null);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        tp[count].setText(Html.fromHtml(label));
                        tp[count].setTextSize(15);
                        tp[count].setLayoutParams(new TableRow.LayoutParams(0));

                        et[count] = (EditText) mContext.getLayoutInflater().inflate(R.layout.edit_input_layout, null);

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
                        et[count].setSingleLine(true);
                        et[count].setFocusable(false);
                        et[count].setFocusableInTouchMode(false); // user touches widget on phone with touch screen


                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                        if (cursorCild.getCount() > 0) {
                            et[count].setText(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                        } else {
                            if (!value.equalsIgnoreCase("")) {
                                et[count].setText(value);
                                RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, value, jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                                db.insertRoomsDetail(orderModel);
                            }
                        }


                        if ((!showButton)) {
                            et[count].setEnabled(false);
                        } else {
                            final int finalI2 = i;
                            et[count].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final Calendar c = Calendar.getInstance();
                                    dummyIdDate = Integer.parseInt(idListTask);
                                    mYear = c.get(Calendar.YEAR);


                                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI2)));
                                    if (cEdit.getCount() == 0) {
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, "", jsonCreateType(idListTask, type, String.valueOf(finalI2)), name, "cild");
                                        db.insertRoomsDetail(orderModel);
                                    }


                                    DatePickerDialog dialog = new DatePickerDialog(mContext,
                                            mDateSetListener
                                            , mYear, mMonth, mDay);

                                    dialog.setOnDismissListener(mOnDismissListenerDate);
                                    dialog.show();
                                    mContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

                                }
                            });
                        }
                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params1.setMargins(30, 10, 30, 0);
                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(30, 10, 30, 40);


                        linearLayout.addView(tp[count], params1);
                        linearLayout.addView(et[count], params2);
                    } else if (type.equalsIgnoreCase("time")) {
                        if (count == null) {
                            count = 0;
                        } else {
                            count++;
                        }
                        tp[count] = (TextView) mContext.getLayoutInflater().inflate(R.layout.text_view_layout, null);
                        if (required.equalsIgnoreCase("1")) {
                            label += "<font size=\"3\" color=\"red\">*</font>";
                        }
                        tp[count].setText(Html.fromHtml(label));
                        tp[count].setTextSize(15);
                        tp[count].setLayoutParams(new TableRow.LayoutParams(0));

                        et[count] = (EditText) mContext.getLayoutInflater().inflate(R.layout.edit_input_layout, null);
                        et[count].setId(Integer.parseInt(idListTask));
                        et[count].setHint(placeHolder);
                        et[count].setSingleLine(true);

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

                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                        if (cursorCild.getCount() > 0) {
                            et[count].setText(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                        } else {
                            if (!value.equalsIgnoreCase("")) {
                                et[count].setText(value);
                                RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, value, jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                                db.insertRoomsDetail(orderModel);
                            }
                        }
                        final int finalI2 = i;

                        if ((!showButton)) {
                            et[count].setEnabled(false);
                        } else {
                            et[count].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dummyIdDate = Integer.parseInt(idListTask);

                                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, "time", String.valueOf(finalI2)));
                                    if (cEdit.getCount() == 0) {
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, "", jsonCreateType(idListTask, "time", String.valueOf(finalI2)), name, "cild");
                                        db.insertRoomsDetail(orderModel);
                                    }

                                    Calendar mcurrentTime = Calendar.getInstance();
                                    int hours = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                                    int minute = mcurrentTime.get(Calendar.MINUTE);
                                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                                            mContext, mTimePicker, hours, minute, true);
                                    timePickerDialog.setOnDismissListener(mOnDismissListenerTime);
                                    timePickerDialog.show();
                                    mContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                                }
                            });
                        }


                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params1.setMargins(30, 10, 30, 0);
                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.setMargins(30, 10, 30, 40);


                        linearLayout.addView(tp[count], params1);
                        linearLayout.addView(et[count], params2);

                    } else if (type.equalsIgnoreCase("number")) {
                        TextView textView = new TextView(mContext);
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

                        et[count] = (EditText) mContext.getLayoutInflater().inflate(R.layout.edit_input_layout, null);

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


                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                        if (cursorCild.getCount() > 0) {
                            et[count].setText(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                        } else {
                            if (!value.equalsIgnoreCase("")) {
                                et[count].setText(value);
                                RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, value, jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
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

                                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI3)));
                                    if (cEdit.getCount() > 0) {
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI3)), name, "cild");
                                        db.updateDetailRoomWithFlagContent(orderModel);
                                    } else {
                                        if (String.valueOf(s).length() > 0) {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI3)), name, "cild");
                                            db.insertRoomsDetail(orderModel);
                                        } else {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, String.valueOf(s), jsonCreateType(idListTask, type, String.valueOf(finalI3)), name, "cild");
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

                    } else if (type.equalsIgnoreCase("rear_camera") || type.equalsIgnoreCase("front_camera")) {
                        TextView textView = new TextView(mContext);
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

                        imageView[count] = (ImageView) mContext.getLayoutInflater().inflate(R.layout.image_view_frame, null);
                        int width = mContext.getWindowManager().getDefaultDisplay().getWidth();
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, width / 2);
                        params.setMargins(5, 15, 0, 0);
                        imageView[count].setLayoutParams(params);
                        params.addRule(RelativeLayout.CENTER_IN_PARENT);
                        linearLayout.addView(imageView[count], params);


                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
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

                                Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI4)));
                                if (cursorCild.getCount() > 0) {
                                    Intent intent = new Intent(getContext(), ZoomImageViewActivity.class);
                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE, ZoomImageViewActivity.FROM);
                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE_BASE_A, idDetail);
                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE_BASE_B, username);
                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE_BASE_C, idRoomTab);
                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE_BASE_D, "cild");
                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE_BASE_E, jsonCreateType(idListTask, type, String.valueOf(finalI4)));
                                    startActivity(intent);
                                } else {
                                    int facing = 0;
                                    if (type.equalsIgnoreCase("front_camera")) {
                                        facing = 1;
                                    }
                                    captureGalery(idDetail, username, idRoomTab, idListTask, type, name, flag, facing, String.valueOf(finalI4));
                                }

                            }
                        });

                        final String finalLabel = label;
                        final int finalI5 = i;
                        imageView[count].setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                                        mContext);
                                builderSingle.setTitle("Select an action " + Html.fromHtml(finalLabel));
                                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                        mContext,
                                        android.R.layout.simple_list_item_1);


                                final Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI5)));
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
                                                    captureGalery(idDetail, username, idRoomTab, idListTask, type, name, flag, facing, String.valueOf(finalI5));
                                                } else if (listName.equalsIgnoreCase("Delete")) {
                                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, "", jsonCreateType(idListTask, type, String.valueOf(finalI5)), name, "cild");

                                                    db.deleteDetailRoomWithFlagContent(orderModel);

                                                    generateLayut();
                                                   /* finish();
                                                    startActivity(getIntent());*/
                                                } else if (listName.equalsIgnoreCase("View")) {
                                                    Intent intent = new Intent(mContext, ZoomImageViewActivity.class);
                                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE, ZoomImageViewActivity.FROM);
                                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE_BASE_A, idDetail);
                                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE_BASE_B, username);
                                                    intent.putExtra(ZoomImageViewActivity.KEY_FILE_BASE_C, idRoomTab);
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
                        TextView textView = new TextView(mContext);
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

                        imageView[count] = (ImageView) mContext.getLayoutInflater().inflate(R.layout.image_view_frame, null);
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
                                Intent i = new Intent(getContext(), ReaderOcr.class);
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
                                linearEstimasi[count] = (LinearLayout) mContext.getLayoutInflater().inflate(R.layout.layout_child, null);
                                child = (RelativeLayout) mContext.getLayoutInflater().inflate(R.layout.item_cild_ocr, null);
                                ImageButton btnOption = (ImageButton) child.findViewById(R.id.btnOption);
                                ImageButton btnCancel = (ImageButton) child.findViewById(R.id.btnCancel);
                                final TextView namePickup = (TextView) child.findViewById(R.id.namePickup);
                                namePickup.setText(ll);
                                final EditText valuePickup = (EditText) child.findViewById(R.id.valuePickup);


                                final Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
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

                                        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI25)));
                                        if (cEdit.getCount() > 0) {
                                            JSONObject jsonObj = null;
                                            try {
                                                JSONObject jsonObject = new JSONObject(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, function(jsonObject, nn, s.toString()).toString(), jsonCreateType(idListTask, type, String.valueOf(finalI25)), name, "cild");
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
                                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, function(null, nn, s.toString()).toString(), jsonCreateType(idListTask, type, String.valueOf(finalI25)), name, "cild");
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
                                        final AlertDialog.Builder alertbox = new AlertDialog.Builder(mContext);
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
                                                getContext());
                                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                                getContext(),
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
                        TextView textView = new TextView(mContext);
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


                        linearEstimasi[count] = (LinearLayout) mContext.getLayoutInflater().inflate(R.layout.upload_doc_layout, null);
                        linearEstimasi[count].setLayoutParams(params2);
                        linearLayout.addView(linearEstimasi[count]);

                        final Button btnOption = (Button) linearEstimasi[count].findViewById(R.id.btn_browse);
                        final TextView valueFile = (TextView) linearEstimasi[count].findViewById(R.id.value);

                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                        if (cursorCild.getCount() > 0) {
                            valueFile.setText(jsonResultType(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)), "a"));
                        }


                        final int finalI25 = i;
                        btnOption.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dummyIdDate = Integer.parseInt(idListTask);

                                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI25)));
                                if (cEdit.getCount() == 0) {
                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, "", jsonCreateType(idListTask, type, String.valueOf(finalI25)), name, "cild");
                                    db.insertRoomsDetail(orderModel);
                                }


                                Intent intent = new Intent();
                                intent.setType("file");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Choose File to Upload.."), PICK_FILE_REQUEST);

                            }
                        });
                    } else if (type.equalsIgnoreCase("signature")) {
                        TextView textView = new TextView(mContext);
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

                        imageView[count] = (ImageView) mContext.getLayoutInflater().inflate(R.layout.frame_signature_form_black, null);
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


                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                        if (cursorCild.getCount() > 0) {
                            //  imageView[count] = (ImageView) getLayoutInflater().inflate(R.layout.frame_signature_form, null);
                            imageView[count].setImageBitmap(decodeBase64(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT))));
                        }


                        imageView[count].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dummyIdDate = Integer.parseInt(idListTask);

                                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI26)));
                                if (cEdit.getCount() == 0) {
                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, "", jsonCreateType(idListTask, type, String.valueOf(finalI26)), name, "cild");
                                    db.insertRoomsDetail(orderModel);
                                }

                                Intent intent = new Intent(getContext(), CaptureSignature.class);
                                startActivityForResult(intent, SIGNATURE_ACTIVITY);

                            }
                        });
                    } else if (type.equalsIgnoreCase("distance_estimation")) {
                        TextView textView = new TextView(mContext);
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

                        linearEstimasi[count] = (LinearLayout) mContext.getLayoutInflater().inflate(R.layout.estimation_layout, null);
                        linearEstimasi[count].setLayoutParams(params2);
                        linearLayout.addView(linearEstimasi[count]);


                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
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

                                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI27)));
                                if (cEdit.getCount() == 0) {
                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, "", jsonCreateType(idListTask, type, String.valueOf(finalI27)), name, "cild");
                                    db.insertRoomsDetail(orderModel);
                                }

                                Intent i = new Intent(getContext(), ActivityDirection.class);
                                startActivityForResult(i, PICK_ESTIMATION);
                            }
                        });
                    } else if (type.equalsIgnoreCase("rate")) {
                        TextView textView = new TextView(mContext);
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

                        rat[count] = (RatingBar) mContext.getLayoutInflater().inflate(R.layout.costume_rating, null);
                        final LinearLayout.LayoutParams testLP = new LinearLayout.LayoutParams(
                                AppBarLayout.LayoutParams.WRAP_CONTENT, AppBarLayout.LayoutParams.MATCH_PARENT);
                        testLP.setMargins(10, 20, 10, 10);
                        testLP.gravity = Gravity.CENTER_HORIZONTAL;
                        rat[count].setLayoutParams(testLP);
                        linearLayout.addView(rat[count]);


                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                        if (cursorCild.getCount() > 0) {
                            rat[count].setRating(Float.parseFloat(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT))));
                        } else {
                            if (!value.equalsIgnoreCase("")) {
                                rat[count].setRating(Integer.parseInt(value));
                                RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, value, jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
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

                                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI24)));
                                    if (cEdit.getCount() > 0) {
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, conten, jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "cild");
                                        db.updateDetailRoomWithFlagContent(orderModel);
                                    } else {
                                        if (conten.length() > 0) {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, conten, jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "cild");
                                            db.insertRoomsDetail(orderModel);
                                        } else {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, conten, jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "cild");
                                            db.deleteDetailRoomWithFlagContent(orderModel);
                                        }
                                    }


                                }
                            });

                        }
                    } else if (type.equalsIgnoreCase("map")) {
                        TextView textView = new TextView(mContext);
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

                        et[count] = (EditText) mContext.getLayoutInflater().inflate(R.layout.edit_input_layout, null);
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

                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
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

                                    final Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI6)));

                                    if (cursorCild.getCount() > 0) {
                                        AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                                                getContext());
                                        builderSingle.setTitle("Select an action " + Html.fromHtml(finalLabel1));
                                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                                getContext(),
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
                                                            requestLocationInfo(idDetail, username, idRoomTab, idListTask, type, name, String.valueOf(finalI6));
                                                        } else if (listName.equalsIgnoreCase("Delete")) {
                                                            showDialog = true;
                                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, "", jsonCreateType(idListTask, type, String.valueOf(finalI6)), name, "cild");

                                                            db.deleteDetailRoomWithFlagContent(orderModel);

                                                            generateLayut();
                                                         /*   finish();
                                                            startActivity(getIntent());*/
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
                                            requestLocationInfo(idDetail, username, idRoomTab, idListTask, type, name, String.valueOf(finalI6));
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
                        TextView textView = new TextView(mContext);
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

                        imageView[count] = new ImageView(mContext);
                        imageView[count].setImageDrawable(getResources().getDrawable(R.drawable.bt_camera));
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
                        params.setMargins(5, 15, 0, 0);
                        imageView[count].setLayoutParams(params);
                        params.addRule(RelativeLayout.CENTER_IN_PARENT);
                        linearLayout.addView(imageView[count], params);

                        imageView[count].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                gps = new GPSTracker(getContext());
                                if (!gps.canGetLocation()) {
                                    gps.showSettingsAlert();
                                } else {
                                    String action = Intent.ACTION_GET_CONTENT;
                                    Intent i = new Intent();
                                    action = MediaStore.ACTION_VIDEO_CAPTURE;
                                    i.setAction(action);
                                    startActivityForResult(i, REQ_VIDEO);
                                }
                            }
                        });
                    } else if (type.equalsIgnoreCase("dropdown_dinamis")) {

                        TextView textView = new TextView(mContext);
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

                        linearEstimasi[count] = (LinearLayout) mContext.getLayoutInflater().inflate(R.layout.layout_child, null);

                        JSONObject jObject = new JSONObject(value);
                        String url = jObject.getString("url");
                        String[] aa = url.split("/");
                        final String nama = aa[aa.length - 1].toString();
                        DataBaseDropDown mDB = new DataBaseDropDown(mContext, nama.substring(0, nama.indexOf(".")));
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

                            final LinearLayout spinerTitle = (LinearLayout) mContext.getLayoutInflater().inflate(R.layout.item_spiner_textview, null);
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
                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(spinnerArrayAdapter);


                            Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
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
                                    if (!spinner.getSelectedItem().toString().replace("'", "''").equals("--Please Select--")) {
                                        if (columnNames.length > 1) {
                                            final int counts = linearEstimasi[Integer.valueOf(nilai.get(0).toString())].getChildCount();
                                            linearEstimasi[Integer.valueOf(nilai.get(0).toString())].removeViews(1, counts - 1);
                                            addSpinner(value, nama.substring(0, nama.indexOf(".")), linearEstimasi[Integer.valueOf(nilai.get(0).toString())], finalNamaTable, columnNames, 0, columnNames[0] + "= '" + spinner.getSelectedItem().toString().replace("'", "''") + "'", idListTask, type, String.valueOf(finalI24), name);

                                            Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI24)));

                                            if (cEdit.getCount() > 0) {
                                                try {
                                                    JSONObject jsonObject = new JSONObject(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, function(jsonObject, titlesss, spinner.getSelectedItem().toString().replace("'", "''")).toString(), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "cild");

                                                    db.updateDetailRoomWithFlagContent(orderModel);

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            } else {
                                                RoomsDetail orderModel = null;
                                                try {
                                                    orderModel = new RoomsDetail(idDetail, idRoomTab, username, function(null, titlesss, spinner.getSelectedItem().toString().replace("'", "''")).toString(), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "cild");

                                                    db.insertRoomsDetail(orderModel);

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            }

                                        }
                                    } else {
                                        if (columnNames.length > 1) {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, spinner.getSelectedItem().toString().replace("'", "''"), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "cild");

                                            db.deleteDetailRoomWithFlagContent(orderModel);

                                            linearEstimasi[Integer.valueOf(nilai.get(0).toString())].removeAllViews();
                                            linearEstimasi[Integer.valueOf(nilai.get(0).toString())].addView(spinerTitle);
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

                                db.deleteRoomsDetailbyId(idDetail, idListTask, username);

                            }
                            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                                Toast.makeText(mContext, "Please insert memmory card", Toast.LENGTH_LONG).show();
                                generateLayut();
                            }
                            generateLayut();
                            Intent intent = new Intent(getContext(), DownloadSqliteDinamicActivity.class);
                            intent.putExtra("name_db", nama.substring(0, nama.indexOf(".")));
                            intent.putExtra("path_db", url);
                            startActivity(intent);
                            return;
                        }
                        TableRow.LayoutParams params2 = new TableRow.LayoutParams(1);
                        params2.setMargins(60, 10, 30, 0);
                        linearEstimasi[count].setLayoutParams(params2);
                        linearLayout.addView(linearEstimasi[count]);

                    } else if (type.equalsIgnoreCase("dropdown")) {
                        TextView textView = new TextView(mContext);
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
                        for (int ia = 0; ia < jsonArrays.length(); ia++) {
                            String l = jsonArrays.getJSONObject(ia).getString("label_option").toString();
                            spinnerArray.add(l);
                        }

                        Spinner spinner = new Spinner(mContext);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            spinner.setBackground(getResources().getDrawable(R.drawable.spinner_background));
                        }
                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
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


                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                        if (cursorCild.getCount() > 0) {
                            int spinnerPosition = spinnerArrayAdapter.getPosition(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                            spinner.setSelection(spinnerPosition);
                        } else {
                            RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, spinner.getSelectedItem().toString().replace("'", "''"), jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
                            db.insertRoomsDetail(orderModel);
                        }


                        if ((!showButton)) {
                            spinner.setEnabled(false);
                        } else {
                            final int finalI7 = i;
                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                @Override
                                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int myPosition, long myID) {

                                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI7)));
                                    if (cEdit.getCount() > 0) {
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, spinnerArray.get(myPosition), jsonCreateType(idListTask, type, String.valueOf(finalI7)), name, "cild");
                                        db.updateDetailRoomWithFlagContent(orderModel);
                                    } else {
                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, spinnerArray.get(myPosition), jsonCreateType(idListTask, type, String.valueOf(finalI7)), name, "cild");
                                        db.insertRoomsDetail(orderModel);
                                    }


                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parentView) {
                                }

                            });
                        }
                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params1.setMargins(30, 10, 30, 0);


                        linearLayout.addView(textView, params1);
                        linearLayout.addView(spinner, params2);
                    } else if (type.equalsIgnoreCase("input_kodepos")) {
                        final DatabaseKodePos mDB = new DatabaseKodePos(mContext);
                        if (mDB.getWritableDatabase() != null) {
                            TextView textView = new TextView(mContext);
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
                            et[count] = (EditText) mContext.getLayoutInflater().inflate(R.layout.edit_input_layout, null);

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

                            final TextView prov = (TextView) mContext.getLayoutInflater().inflate(R.layout.text_input_layout, null);
                            prov.setTextSize(15);
                            prov.setText("-");
                            final TextView kota = (TextView) mContext.getLayoutInflater().inflate(R.layout.text_input_layout, null);
                            kota.setTextSize(15);
                            kota.setText("-");
                            final TextView kec = (TextView) mContext.getLayoutInflater().inflate(R.layout.text_input_layout, null);
                            kec.setTextSize(15);
                            kec.setText("-");
                            final TextView kel = (TextView) mContext.getLayoutInflater().inflate(R.layout.text_input_layout, null);
                            kel.setTextSize(15);
                            kel.setText("-");

                            et[count].setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(maxlength))});

                            Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
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
                                                    mContext,
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
                                                android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(mContext);
                                                alertDialogBuilder.setMessage("Kode Pos not valid");

                                                alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface arg0, int arg1) {
                                                        et[count].setError("not valid");
                                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, "", jsonCreateType(idListTask, type, String.valueOf(finalI8)), name, "cild");

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


                                                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI8)));
                                                if (cEdit.getCount() > 0) {
                                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, jsonPosCode(strKode, strProv, strJen + " " + strKab, strKec, strKel), jsonCreateType(idListTask, type, String.valueOf(finalI8)), name, "cild");
                                                    db.updateDetailRoomWithFlagContent(orderModel);
                                                } else {
                                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, jsonPosCode(strKode, strProv, strJen + " " + strKab, strKec, strKel), jsonCreateType(idListTask, type, String.valueOf(finalI8)), name, "cild");
                                                    db.insertRoomsDetail(orderModel);
                                                }

                                            } else {

                                                android.support.v7.app.AlertDialog.Builder builderSingle = new android.support.v7.app.AlertDialog.Builder(mContext);
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


                                                                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI9)));
                                                                if (cEdit.getCount() > 0) {
                                                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, jsonPosCode(strKode, strProv, strJen + " " + strKab, strKec, strKel), jsonCreateType(idListTask, type, String.valueOf(finalI9)), name, "cild");
                                                                    db.updateDetailRoomWithFlagContent(orderModel);
                                                                } else {
                                                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, jsonPosCode(strKode, strProv, strJen + " " + strKab, strKec, strKel), jsonCreateType(idListTask, type, String.valueOf(finalI9)), name, "cild");
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
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, "", jsonCreateType(idListTask, type, String.valueOf(finalI20)), name, "cild");

                                            db.deleteDetailRoomWithFlagContent(orderModel);

                                        }
                                    }
                                });
                            }
                            TextView tvProv = new TextView(mContext);
                            tvProv.setText("Provinsi");
                            tvProv.setTextSize(15);
                            tvProv.setLayoutParams(new TableRow.LayoutParams(0));
                            TextView tvKota = new TextView(mContext);
                            tvKota.setText("Kota / Kabupaten");
                            tvKota.setTextSize(15);
                            tvKota.setLayoutParams(new TableRow.LayoutParams(0));
                            TextView tvKec = new TextView(mContext);
                            tvKec.setText("Kecamatan");
                            tvKec.setTextSize(15);
                            tvKec.setLayoutParams(new TableRow.LayoutParams(0));
                            TextView tvKel = new TextView(mContext);
                            tvKel.setText("Kelurahan");
                            tvKel.setTextSize(15);
                            tvKel.setLayoutParams(new TableRow.LayoutParams(0));
                            TextView tvKode = new TextView(mContext);
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
                            generateLayut();
                            /*getActivity().finish();*/
                            Intent intent = new Intent(getContext(), DownloadUtilsActivity.class);
                            startActivity(intent);
                        }
                    } else if (type.equalsIgnoreCase("dropdown_wilayah")) {
                        final DatabaseKodePos mDB = new DatabaseKodePos(mContext);
                        if (mDB.getWritableDatabase() != null) {
                            TextView textView = new TextView(mContext);
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


                            final Spinner spinnerPropinsi = new Spinner(mContext);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                spinnerPropinsi.setBackground(getResources().getDrawable(R.drawable.spinner_background));
                            }
                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, spinnerArrayPropinsi); //selected item will look like a spinner set from XML
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerPropinsi.setAdapter(spinnerArrayAdapter);

                            final Spinner spinnerKota = new Spinner(mContext);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                spinnerKota.setBackground(getResources().getDrawable(R.drawable.spinner_background));
                            }
                            final ArrayList<String> spinnerArrayKota = new ArrayList<>();
                            spinnerArrayKota.add("Semua Kota/Kabupaten");
                            final ArrayAdapter<String> spinnerKotaArrayAdapter = new ArrayAdapter<String>(
                                    mContext, android.R.layout.simple_spinner_item, spinnerArrayKota);
                            spinnerKotaArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerKota.setAdapter(spinnerKotaArrayAdapter);

                            final Spinner spinnerKecamatan = new Spinner(mContext);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                spinnerKecamatan.setBackground(getResources().getDrawable(R.drawable.spinner_background));
                            }
                            final ArrayList<String> spinnerArrayKecamatan = new ArrayList<>();
                            spinnerArrayKecamatan.add("Semua Kecamatan");
                            final ArrayAdapter<String> spinnerKecamatanArrayAdapter = new ArrayAdapter<String>(
                                    mContext, android.R.layout.simple_spinner_item, spinnerArrayKecamatan);
                            spinnerKecamatanArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerKecamatan.setAdapter(spinnerKecamatanArrayAdapter);

                            final Spinner spinnerKelurahan = new Spinner(mContext);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                spinnerKelurahan.setBackground(getResources().getDrawable(R.drawable.spinner_background));
                            }
                            final ArrayList<String> spinnerArrayKelurahan = new ArrayList<>();
                            spinnerArrayKelurahan.add("Semua Kelurahan");
                            final ArrayAdapter<String> spinnerKelurahanArrayAdapter = new ArrayAdapter<String>(
                                    mContext, android.R.layout.simple_spinner_item, spinnerArrayKelurahan);
                            spinnerKelurahanArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerKelurahan.setAdapter(spinnerKelurahanArrayAdapter);


                            final TextView kodePos = (TextView) mContext.getLayoutInflater().inflate(R.layout.text_input_layout, null);
                            kodePos.setTextSize(15);


                            Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                            if (cursorCild.getCount() > 0) {
                                final String isi = cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));
                                kodePos.setText(jsonResultType(isi, "a"));


                                if (!jsonResultType(isi, "b").equalsIgnoreCase("Semua Provinsi")) {
                                    Cursor cKota = mDB.getWritableDatabase().query(true, "wilayah", new String[]{"jenis", "kabupaten"}, "propinsi = '" + jsonResultType(isi, "b") + "'", null, "kabupaten", null, null, null);
                                    if (cKota.moveToFirst()) {
                                        do {
                                            String column1 = cKota.getString(0);
                                            String column2 = cKota.getString(1);
                                            spinnerArrayKota.add(column1 + " " + column2);

                                        } while (cKota.moveToNext());
                                        spinnerKotaArrayAdapter.notifyDataSetChanged();
                                    }

                                    if (!jsonResultType(isi, "c").equalsIgnoreCase("Semua Kota/Kabupaten")) {
                                        Cursor cKecamatan = mDB.getWritableDatabase().query(true, "wilayah", new String[]{"kecamatan"}, "propinsi = '" + jsonResultType(isi, "b") + "' and kabupaten = '" + jsonResultType(isi, "c").substring(5, jsonResultType(isi, "c").length()) + "'", null, "kecamatan", null, null, null);
                                        if (cKecamatan.moveToFirst()) {
                                            do {
                                                String column1 = cKecamatan.getString(0);
                                                spinnerArrayKecamatan.add(column1);

                                            } while (cKecamatan.moveToNext());
                                            spinnerKecamatanArrayAdapter.notifyDataSetChanged();
                                        }

                                        if (!jsonResultType(isi, "d").equalsIgnoreCase("Semua Kecamatan")) {
                                            Cursor ckelurahan = mDB.getWritableDatabase().query(true, "wilayah", new String[]{"kelurahan"}, "propinsi = '" + jsonResultType(isi, "b") + "' and kabupaten = '" + jsonResultType(isi, "c").substring(5, jsonResultType(isi, "c").length()) + "' and kecamatan ='" + jsonResultType(isi, "d") + "'", null,
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
                                RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, jsonPosCode("-", "Semua Provinsi", "Semua Kota/Kabupaten", "Semua Kecamatan", "Semua Kelurahan"), jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
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

                                        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI10)));
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
                                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, jsonPosCode("-", spinnerArrayPropinsi.get(position), "Semua Kota/Kabupaten", "Semua Kecamatan", "Semua Kelurahan"), jsonCreateType(idListTask, type, String.valueOf(finalI11)), name, "cild");
                                                    db.updateDetailRoomWithFlagContent(orderModel);
                                                }
                                            } else {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, jsonPosCode("-", spinnerArrayPropinsi.get(position), "Semua Kota/Kabupaten", "Semua Kecamatan", "Semua Kelurahan"), jsonCreateType(idListTask, type, String.valueOf(finalI11)), name, "cild");
                                                db.insertRoomsDetail(orderModel);
                                            }


                                            Cursor c = mDB.getWritableDatabase().query(true, "wilayah", new String[]{"jenis", "kabupaten"}, "propinsi = '" + spinnerArrayPropinsi.get(position) + "'", null, "kabupaten", null, null, null);
                                            if (c.moveToFirst()) {
                                                do {
                                                    String column1 = c.getString(0);
                                                    String column2 = c.getString(1);
                                                    spinnerArrayKota.add(column1 + " " + column2);

                                                } while (c.moveToNext());
                                            }
                                            c.close();
                                        } else {

                                            Cursor cEdit2 = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI12)));
                                            if (cEdit2.getCount() > 0) {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, jsonPosCode("-", spinnerArrayPropinsi.get(position), "Semua Kota/Kabupaten", "Semua Kecamatan", "Semua Kelurahan"), jsonCreateType(idListTask, type, String.valueOf(finalI12)), name, "cild");
                                                db.updateDetailRoomWithFlagContent(orderModel);
                                            } else {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, jsonPosCode("-", spinnerArrayPropinsi.get(position), "Semua Kota/Kabupaten", "Semua Kecamatan", "Semua Kelurahan"), jsonCreateType(idListTask, type, String.valueOf(finalI12)), name, "cild");
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

                                        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI13)));
                                        if (cEdit.getCount() > 0) {
                                            if (jsonResultType(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)), "c").equalsIgnoreCase(spinnerArrayKota.get(position))) {
                                                return;
                                            }
                                        }

                                        spinnerArrayKecamatan.clear();
                                        spinnerArrayKecamatan.add("Semua Kecamatan");

                                        if (position > 0) {
                                            String kota = spinnerArrayKota.get(position).toString().substring(5, spinnerArrayKota.get(position).toString().length());

                                            Cursor cEdit2 = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI13)));
                                            if (cEdit2.getCount() > 0) {
                                                if (!jsonResultType(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)), "c").equalsIgnoreCase(spinnerArrayKota.get(position))) {
                                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, jsonPosCode("-", spinnerPropinsi.getSelectedItem().toString(), spinnerArrayKota.get(position).toString(), "Semua Kecamatan", "Semua Kelurahan"), jsonCreateType(idListTask, type, String.valueOf(finalI13)), name, "cild");
                                                    db.updateDetailRoomWithFlagContent(orderModel);
                                                }
                                            } else {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, jsonPosCode("-", spinnerPropinsi.getSelectedItem().toString(), spinnerArrayKota.get(position).toString(), "Semua Kecamatan", "Semua Kelurahan"), jsonCreateType(idListTask, type, String.valueOf(finalI13)), name, "cild");
                                                db.insertRoomsDetail(orderModel);
                                            }


                                            Cursor c = mDB.getWritableDatabase().query(true, "wilayah", new String[]{"kecamatan"}, "propinsi = '" + spinnerPropinsi.getSelectedItem() + "' and kabupaten = '" + kota + "'", null, "kecamatan", null, null, null);
                                            if (c.moveToFirst()) {
                                                do {
                                                    String column1 = c.getString(0);
                                                    spinnerArrayKecamatan.add(column1);
                                                } while (c.moveToNext());
                                            }
                                            c.close();
                                        } else {

                                            Cursor cEdit3 = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI14)));
                                            if (cEdit3.getCount() > 0) {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, jsonPosCode("-", spinnerPropinsi.getSelectedItem().toString(), spinnerArrayKota.get(position).toString(), "Semua Kecamatan", "Semua Kelurahan"), jsonCreateType(idListTask, type, String.valueOf(finalI14)), name, "cild");
                                                db.updateDetailRoomWithFlagContent(orderModel);
                                            } else {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, jsonPosCode("-", spinnerPropinsi.getSelectedItem().toString(), spinnerArrayKota.get(position).toString(), "Semua Kecamatan", "Semua Kelurahan"), jsonCreateType(idListTask, type, String.valueOf(finalI14)), name, "cild");
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

                                        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI15)));
                                        if (cEdit.getCount() > 0) {
                                            if (jsonResultType(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)), "d").equalsIgnoreCase(spinnerArrayKecamatan.get(position))) {
                                                return;
                                            }
                                        }


                                        spinnerArrayKelurahan.clear();
                                        spinnerArrayKelurahan.add("Semua Kelurahan");

                                        if (position > 0) {
                                            String kota = spinnerKota.getSelectedItem().toString().substring(5, spinnerKota.getSelectedItem().toString().length());

                                            Cursor cEdit2 = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI15)));
                                            if (cEdit2.getCount() > 0) {
                                                if (!jsonResultType(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)), "d").equalsIgnoreCase(spinnerArrayKecamatan.get(position))) {
                                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, jsonPosCode("-", spinnerPropinsi.getSelectedItem().toString(), spinnerKota.getSelectedItem().toString(), spinnerArrayKecamatan.get(position), "Semua Kelurahan"), jsonCreateType(idListTask, type, String.valueOf(finalI15)), name, "cild");
                                                    db.updateDetailRoomWithFlagContent(orderModel);
                                                }


                                            } else {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, jsonPosCode("-", spinnerPropinsi.getSelectedItem().toString(), spinnerKota.getSelectedItem().toString(), spinnerArrayKecamatan.get(position), "Semua Kelurahan"), jsonCreateType(idListTask, type, String.valueOf(finalI15)), name, "cild");
                                                db.insertRoomsDetail(orderModel);
                                            }


                                            Cursor c = mDB.getWritableDatabase().query(true, "wilayah", new String[]{"kelurahan"}, "propinsi = '" + spinnerPropinsi.getSelectedItem() + "' and kabupaten = '" + kota + "'" + " and kecamatan = '" + spinnerArrayKecamatan.get(position) + "'", null, null, null, null, null);
                                            if (c.moveToFirst()) {
                                                do {
                                                    String column1 = c.getString(0);
                                                    spinnerArrayKelurahan.add(column1);
                                                } while (c.moveToNext());
                                            }
                                            c.close();
                                        } else {

                                            Cursor cEdit3 = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI16)));
                                            if (cEdit3.getCount() > 0) {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, jsonPosCode("-", spinnerPropinsi.getSelectedItem().toString(), spinnerKota.getSelectedItem().toString(), spinnerArrayKecamatan.get(position), "Semua Kelurahan"), jsonCreateType(idListTask, type, String.valueOf(finalI16)), name, "cild");
                                                db.updateDetailRoomWithFlagContent(orderModel);
                                            } else {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, jsonPosCode("-", spinnerPropinsi.getSelectedItem().toString(), spinnerKota.getSelectedItem().toString(), spinnerArrayKecamatan.get(position), "Semua Kelurahan"), jsonCreateType(idListTask, type, String.valueOf(finalI16)), name, "cild");
                                                db.insertRoomsDetail(orderModel);
                                            }

                                        }
                                        kodePos.setText("-");
                                        spinnerKelurahanArrayAdapter.notifyDataSetChanged();
                                        spinnerKelurahan.setSelection(0);
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

                                        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI17)));
                                        if (cEdit.getCount() > 0) {
                                            if (jsonResultType(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)), "e").equalsIgnoreCase(spinnerArrayKelurahan.get(position))) {
                                                return;
                                            }
                                        }

                                        if (position > 0) {

                                            Cursor cEdit2 = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI17)));
                                            if (cEdit2.getCount() > 0) {
                                                if (!jsonResultType(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)), "e").equalsIgnoreCase(spinnerArrayKelurahan.get(position))) {
                                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, jsonPosCode("-", spinnerPropinsi.getSelectedItem().toString(), spinnerKota.getSelectedItem().toString(), spinnerKecamatan.getSelectedItem().toString(), spinnerArrayKelurahan.get(position)), jsonCreateType(idListTask, type, String.valueOf(finalI17)), name, "cild");
                                                    db.updateDetailRoomWithFlagContent(orderModel);
                                                }

                                            } else {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, jsonPosCode("-", spinnerPropinsi.getSelectedItem().toString(), spinnerKota.getSelectedItem().toString(), spinnerKecamatan.getSelectedItem().toString(), spinnerArrayKelurahan.get(position)), jsonCreateType(idListTask, type, String.valueOf(finalI17)), name, "cild");
                                                db.insertRoomsDetail(orderModel);
                                            }


                                            String kota = spinnerKota.getSelectedItem().toString().substring(5, spinnerKota.getSelectedItem().toString().length());
                                            Cursor c = mDB.getWritableDatabase().query(true, "wilayah", new String[]{"kode_pos"}, "propinsi = '" + spinnerPropinsi.getSelectedItem() + "' and kabupaten = '" + kota + "'" + " and kecamatan = '" + spinnerKecamatan.getSelectedItem() + "' and kelurahan = '" + spinnerArrayKelurahan.get(position) + "'", null, null, null, null, null);
                                            if (c.moveToFirst()) {
                                                do {
                                                    String column1 = c.getString(0);
                                                    kodePos.setText(column1);

                                                    Cursor cEdit3 = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI18)));
                                                    if (cEdit3.getCount() > 0) {
                                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, jsonPosCode(column1, spinnerPropinsi.getSelectedItem().toString(), spinnerKota.getSelectedItem().toString(), spinnerKecamatan.getSelectedItem().toString(), spinnerArrayKelurahan.get(position)), jsonCreateType(idListTask, type, String.valueOf(finalI18)), name, "cild");
                                                        db.updateDetailRoomWithFlagContent(orderModel);
                                                    } else {
                                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, jsonPosCode(column1, spinnerPropinsi.getSelectedItem().toString(), spinnerKota.getSelectedItem().toString(), spinnerKecamatan.getSelectedItem().toString(), spinnerArrayKelurahan.get(position)), jsonCreateType(idListTask, type, String.valueOf(finalI18)), name, "cild");
                                                        db.insertRoomsDetail(orderModel);
                                                    }


                                                } while (c.moveToNext());
                                            }
                                            c.close();
                                        } else {

                                            Cursor cEdit2 = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI19)));
                                            if (cEdit2.getCount() > 0) {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, jsonPosCode("-", spinnerPropinsi.getSelectedItem().toString(), spinnerKota.getSelectedItem().toString(), spinnerKecamatan.getSelectedItem().toString(), spinnerArrayKelurahan.get(position)), jsonCreateType(idListTask, type, String.valueOf(finalI19)), name, "cild");
                                                db.updateDetailRoomWithFlagContent(orderModel);
                                            } else {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, jsonPosCode("-", spinnerPropinsi.getSelectedItem().toString(), spinnerKota.getSelectedItem().toString(), spinnerKecamatan.getSelectedItem().toString(), spinnerArrayKelurahan.get(position)), jsonCreateType(idListTask, type, String.valueOf(finalI19)), name, "cild");
                                                db.insertRoomsDetail(orderModel);
                                            }

                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parentView) {
                                        // your code here
                                    }

                                });
                            }


                            TextView tvProv = new TextView(mContext);
                            tvProv.setText("Provinsi");
                            tvProv.setTextSize(15);
                            tvProv.setLayoutParams(new TableRow.LayoutParams(0));
                            TextView tvKota = new TextView(mContext);
                            tvKota.setText("Kota / Kabupaten");
                            tvKota.setTextSize(15);
                            tvKota.setLayoutParams(new TableRow.LayoutParams(0));
                            TextView tvKec = new TextView(mContext);
                            tvKec.setText("Kecamatan");
                            tvKec.setTextSize(15);
                            tvKec.setLayoutParams(new TableRow.LayoutParams(0));
                            TextView tvKel = new TextView(mContext);
                            tvKel.setText("Kelurahan");
                            tvKel.setTextSize(15);
                            tvKel.setLayoutParams(new TableRow.LayoutParams(0));
                            TextView tvKode = new TextView(mContext);
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

                            /*getActivity().finish();*/
                            Intent intent = new Intent(getContext(), DownloadUtilsActivity.class);
                            startActivity(intent);
                        }
                    } else if (type.equalsIgnoreCase("checkbox")) {
                        //add checkboxes
                        TextView textView = new TextView(mContext);
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

                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
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
                            final CheckBox cb = new CheckBox(mContext);
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
                                            Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI21)));
                                            if (cEdit.getCount() > 0) {
                                                String text = cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));

                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, text + "," + jsonCheckBox(String.valueOf(cb.getId()), cb.getText().toString()), jsonCreateType(idListTask, type, String.valueOf(finalI21)), name, "cild");
                                                db.updateDetailRoomWithFlagContent(orderModel);
                                            } else {
                                                RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, jsonCheckBox(String.valueOf(cb.getId()), cb.getText().toString()), jsonCreateType(idListTask, type, String.valueOf(finalI21)), name, "cild");
                                                db.insertRoomsDetail(orderModel);
                                            }
                                        } else {
                                            Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI21)));
                                            if (cursorCild.getCount() > 0) {
                                                String cc = cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));
                                                if (!cc.startsWith("[")) {
                                                    cc = "[" + cc + "]";
                                                }
                                                JSONArray jsA = null;
                                                try {
                                                    jsA = new JSONArray(cc);
                                                    if (jsA.length() > 1) {
                                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, String.valueOf(removeJson(cb.getId(), jsA)), jsonCreateType(idListTask, type, String.valueOf(finalI22)), name, "cild");
                                                        db.updateDetailRoomWithFlagContent(orderModel);
                                                    } else {
                                                        RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, String.valueOf(cb.getText()), jsonCreateType(idListTask, type, String.valueOf(finalI22)), name, "cild");
                                                        db.deleteDetailRoomWithFlagContent(orderModel);
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }

                                    }

                                });
                            }
                            linearLayout.addView(cb);
                        }
                    } else if (type.equalsIgnoreCase("radio")) {
                        TextView textView = new TextView(mContext);
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
                        RadioGroup rg = new RadioGroup(mContext); //create the RadioGroup
                        rg.setOrientation(RadioGroup.VERTICAL);//or RadioGroup.VERTICAL
                        rg.setLayoutParams(params2b);
                        for (int iaa = 0; iaa < jsonArrayCeks.length(); iaa++) {
                            String l = jsonArrayCeks.getJSONObject(iaa).getString("label_radio").toString();
                            String cek = jsonArrayCeks.getJSONObject(iaa).getString("is_checked").toString();
                            rb[iaa] = new RadioButton(mContext);
                            rb[iaa].setText(l);
                            rb[iaa].setId(iaa);

                            if ((!showButton)) {
                                rb[iaa].setEnabled(false);
                            }

                            Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(i)));
                            if (cursorCild.getCount() > 0) {
                                if (rb[iaa].getText().toString().equalsIgnoreCase(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)))) {
                                    rb[iaa].setChecked(true);
                                }
                            } else {
                                if (cek.equalsIgnoreCase("1")) {
                                    rb[iaa].setChecked(true);
                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, rb[iaa].getText().toString(), jsonCreateType(idListTask, type, String.valueOf(i)), name, "cild");
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

                                        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI23)));
                                        if (cEdit.getCount() > 0) {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, rb.getText().toString(), jsonCreateType(idListTask, type, String.valueOf(finalI23)), name, "cild");
                                            db.updateDetailRoomWithFlagContent(orderModel);
                                        } else {
                                            RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, rb.getText().toString(), jsonCreateType(idListTask, type, String.valueOf(finalI23)), name, "cild");
                                            db.insertRoomsDetail(orderModel);
                                        }

                                    }
                                }
                            });
                        }
                        linearLayout.addView(rg);
                    } else if (type.equalsIgnoreCase("image_load")) {
                        TextView textView = new TextView(mContext);
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

                        imageView[count] = new ImageView(mContext);

                        imageView[count].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent intent = new Intent(mContext, ZoomImageViewActivity.class);
                                intent.putExtra(ZoomImageViewActivity.KEY_FILE, value);
                                startActivity(intent);
                            }
                        });


                        ImageLoaderLarge imgCard = new ImageLoaderLarge(mContext, true);
                        imgCard.DisplayImage(value, imageView[count]);
                        int width = mContext.getWindowManager().getDefaultDisplay().getWidth();
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

            LinearLayout btnRel = (LinearLayout) mContext.getLayoutInflater().inflate(R.layout.button_submit_form, null);
            final Button b = (Button) btnRel.findViewById(R.id.btn_submit);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(5, 15, 0, 0);
            btnRel.setLayoutParams(params);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean berhenti = false;
                    List<String> errorReq = new ArrayList<String>();
                    for (Integer key : hashMap.keySet()) {
                        List<String> value = hashMap.get(key);
                        if (value != null) {
                            if (value.get(1).toString().equalsIgnoreCase("1")) {

                                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(String.valueOf(key), value.get(2).toString(), value.get(5).toString()));
                                if (cEdit.getCount() > 0) {
                                    if (cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).equalsIgnoreCase("")) {
                                        if (value.get(2).toString().equalsIgnoreCase("text") ||
                                                value.get(2).toString().equalsIgnoreCase("textarea") ||
                                                value.get(2).toString().equalsIgnoreCase("number") ||
                                                value.get(2).toString().equalsIgnoreCase("date") ||
                                                value.get(2).toString().equalsIgnoreCase("time")) {
                                            String aa = value.get(0).toString();
                                            et[Integer.valueOf(aa)].setError("required");
                                            berhenti = true;
                                        } else {
                                            berhenti = true;
                                            errorReq.add(value.get(4).toString());
                                        }
                                    }
                                } else {
                                    if (value.get(2).toString().equalsIgnoreCase("text") ||
                                            value.get(2).toString().equalsIgnoreCase("textarea") ||
                                            value.get(2).toString().equalsIgnoreCase("number") ||
                                            value.get(2).toString().equalsIgnoreCase("date") ||
                                            value.get(2).toString().equalsIgnoreCase("time")) {
                                        String aa = value.get(0).toString();
                                        et[Integer.valueOf(aa)].setError("required");
                                        berhenti = true;
                                    } else {
                                        berhenti = true;
                                        errorReq.add(value.get(4).toString());
                                    }
                                }

                            }
                        }
                    }

                    if (berhenti) {
                        if (errorReq.size() > 0) {
                            final AlertDialog.Builder alertbox = new AlertDialog.Builder(mContext);
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
                    }

                    showProgressDialogWithTitle("Pease Wait...", "upload...");

                    if (latLong.equalsIgnoreCase("1")) {
                        gps = new GPSTracker(mContext);
                        if (!gps.canGetLocation()) {
                            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQ_LOCATION_SETTING);
                            return;
                        } else {
                            if (gps.canGetLocation()) {
                                latitude = gps.getLatitude();
                                longitude = gps.getLongitude();
                                if (latitude == 0.0 && longitude == 0.0) {
                                    mContext.runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getContext(), "Harap coba kembali dalam waktu 1 menit, karena data gps anda sedang diaktifkan", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    return;
                                } else {

                                    Cursor cursorParent = db.getSingleRoomDetailFormWithFlag(idDetail, username, idRoomTab, "parent");

                                    String latLongResult = "";
                                    if (cursorParent.getCount() > 0) {
                                        latLongResult = jsonCreateType(jsonResultType(cursorParent.getString(cursorParent.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "a"), String.valueOf(latitude) + "|" + String.valueOf(longitude), "");

                                    }

                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, null, "1", latLongResult, "parent");

                                    db.updateDetailRoomWithFlagContentParent(orderModel);


                                }
                            }
                        }

                    } else {
                        String latLongsAfter = "0.0|0.0";

                        Cursor cursorParent = db.getSingleRoomDetailFormWithFlag(idDetail, username, idRoomTab, "parent");

                        String latLongResult = "";
                        if (cursorParent.getCount() > 0) {
                            latLongResult = jsonCreateType(jsonResultType(cursorParent.getString(cursorParent.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "a"), latLongsAfter, "");

                        }

                        RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, null, "1", latLongResult, "parent");

                        db.updateDetailRoomWithFlagContentParent(orderModel);

                    }


                    if (NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)) {
              /*          long date = System.currentTimeMillis();
                        String dateString = hourFormat.format(date);
                        
                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, dateString, "1", null, "parent");
                        db.updateDetailRoomWithFlagContentParent(orderModel);
                        */

                        new posTask().execute(urlPost, username, idRoomTab);
                    } else {
                        Toast.makeText(getContext(), "No Internet Akses", Toast.LENGTH_SHORT).show();
                    }


                }
            });
            linearLayout.addView(btnRel);

        } else {
            new Refresh(mContext).execute(urlTembak, username, idRoomTab);
        }


    }

    private void showProgressDialogWithTitle(String title, String message) {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public void showInputMethod() {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
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


    private class Refresh extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        String error = "";
        private Activity activity;
        private Context context;

        public Refresh(Activity activity) {
            this.activity = activity;
            context = activity;
            dialog = new ProgressDialog(context);

            DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(getContext());
            if (dataBaseHelper.checkTable("room_" + idRoomTab)) {
                dataBaseHelper.deleteAllrow("room_" + idRoomTab, username, idRoomTab);
            }
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Loading...");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            postData(params[0], params[1], params[2]);

            return null;
        }

        protected void onPostExecute(String result) {

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (error.length() > 0) {
                Toast.makeText(mContext, error, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(mContext, "Form success download.", Toast.LENGTH_SHORT).show();
                generateLayut();
            }

        }

        protected void onProgressUpdate(String... string) {
        }

        public void postData(String valueIWantToSend, String usr, String idr) {
            // Create a new HttpClient and Post Header

            try {
                //Log.w("a1a2",valueIWantToSend+".:."+usr);
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 13000);
                HttpConnectionParams.setSoTimeout(httpParameters, 15000);
                HttpClient httpclient = new DefaultHttpClient(httpParameters);
                HttpPost httppost = new HttpPost(valueIWantToSend);

                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("username_room", usr));
                nameValuePairs.add(new BasicNameValuePair("id_rooms_tab", idr));

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
                        String content = jsonRootObject.getString("data");


                        db.deleteRoomsDetailPtabPRoomNotValue(id_rooms_tab, username, "show");
                        /*if(fromList.equalsIgnoreCase("hide")) {
                            RoomsDetail orderModel = new RoomsDetail(idDetail, id_rooms_tab, username, jsonRootObject.getString("list_pull"), "", time_str, "value");
                            db.insertRoomsDetail(orderModel);
                        }*/
                        RoomsDetail orderModel = new RoomsDetail(username, id_rooms_tab, username, data, "", time_str, "form");
                        db.insertRoomsDetail(orderModel);


                    } catch (JSONException e) {
                        e.printStackTrace();
                        error = "Tolong periksa koneksi internet.";
                    }
                } else {
                    error = "Tolong periksa koneksi internet.";
                }

            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                mContext.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(mContext, "Tolong periksa koneksi internet.", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (ClientProtocolException e) {
                mContext.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(mContext, "Tolong periksa koneksi internet.", Toast.LENGTH_SHORT).show();
                    }
                });
                // TODO Auto-generated catch block
            } catch (IOException e) {
                mContext.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(mContext, "Tolong periksa koneksi internet.", Toast.LENGTH_SHORT).show();
                    }
                });
                // TODO Auto-generated catch block
            }
        }

    }

    private void requestLocationInfo(String idDetail, String username, String idTab, String idListTask, String type, String name, String f) {
        dummyIdDate = Integer.parseInt(idListTask);

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION},
                        100);
            }
        } else {
            gps = new GPSTracker(mContext);
            LocationManager locManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                try {

                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, f));
                    if (cEdit.getCount() == 0) {
                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(idListTask, type, f), name, "cild");
                        db.insertRoomsDetail(orderModel);
                    }


                    PlacePicker.IntentBuilder intentBuilder =
                            new PlacePicker.IntentBuilder();
                    Intent intent = intentBuilder.build(mContext);
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

    // Get Result Back
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CAMERA) {
            List value = (List) hashMap.get(dummyIdDate);
            if (resultCode == mContext.RESULT_OK) {
                Toast.makeText(mContext, " Picture OKE not taken ", Toast.LENGTH_SHORT).show();
                //  btnPhoto.setVisibility(View.GONE);
                if (decodeFile(cameraFileOutput)) {
                    final File f = new File(cameraFileOutput);
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


                        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                        if (cEdit.getCount() > 0) {
                            RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, myBase64Image, jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                            db.updateDetailRoomWithFlagContent(orderModel);
                        }


                    }

                }
            } else if (resultCode == mContext.RESULT_CANCELED) {
                if (result == null) {
                    //     btnPhoto.setVisibility(View.VISIBLE);
                }


                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                if (cEdit.getCount() > 0) {
                    if (cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).equalsIgnoreCase("")) {
                        RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, "", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                        db.deleteDetailRoomWithFlagContent(orderModel);
                    }
                }


                Toast.makeText(mContext, " Picture was not taken ", Toast.LENGTH_SHORT).show();
            } else {
                if (result == null) {
                    // btnPhoto.setVisibility(View.VISIBLE);
                }


                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                if (cEdit.getCount() > 0) {
                    if (cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).equalsIgnoreCase("")) {
                        RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, "", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                        db.deleteDetailRoomWithFlagContent(orderModel);
                    }
                }


                Toast.makeText(mContext, " Picture was not taken ", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == SIGNATURE_ACTIVITY) {
            List value = (List) hashMap.get(dummyIdDate);
            if (resultCode == mContext.RESULT_OK) {
                String result = data.getExtras().getString("status");

                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                if (cEdit.getCount() > 0) {
                    RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, result, jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                    db.updateDetailRoomWithFlagContent(orderModel);
                }

                imageView[Integer.valueOf(value.get(0).toString())].setImageBitmap(decodeBase64(result));
            } else {


                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                if (cEdit.getCount() > 0) {
                    if (cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).equalsIgnoreCase("")) {
                        RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, "", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                        db.deleteDetailRoomWithFlagContent(orderModel);
                        imageView[Integer.valueOf(value.get(0).toString())].setImageDrawable(getResources().getDrawable(R.drawable.ico_signature));
                    }
                }

            }
        } else if (requestCode == PICK_FILE_REQUEST) {
            List value = (List) hashMap.get(dummyIdDate);
            if (resultCode == mContext.RESULT_OK) {
                if (data == null) {
                    return;
                }
                Uri selectedFileUri = data.getData();

                File ee = new File(ImageFilePath.getPath(mContext, selectedFileUri));

                String fileName = selectedFileUri.toString().substring(selectedFileUri.toString().lastIndexOf('/'), selectedFileUri.toString().length());
                if (ee.exists()) {
                    String myBase64Image = encodeToBase64File(ee);
                    TextView textView = (TextView) linearEstimasi[Integer.valueOf(value.get(0).toString())].findViewById(R.id.value);
                    textView.setText(fileName.replace("/", ""));

                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                    if (cEdit.getCount() > 0) {
                        RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, jsonCreateDocument(fileName.replace("/", ""), myBase64Image), jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                        db.updateDetailRoomWithFlagContent(orderModel);
                    }

                } else {
                    Toast.makeText(mContext, "No such file or directory", Toast.LENGTH_SHORT).show();
                }

            }
        } else if (requestCode == OCR_REQUEST) {
            if (data.hasExtra("result")) {
                final List value = (List) hashMap.get(dummyIdDate);
                String result = data.getExtras().getString("result");
                Toast.makeText(mContext, "Success scan", Toast.LENGTH_SHORT).show();
                String lines[] = result.split("\\r?\\n");
                List<String> valSetOne = new ArrayList<String>();
                for (String isi : lines) {
                    valSetOne.add(isi);
                }
                hashMapOcr.put(Integer.valueOf(value.get(0).toString()), valSetOne);
            }
        } else if (requestCode == PICK_ESTIMATION) {
            List value = (List) hashMap.get(dummyIdDate);
            if (resultCode == mContext.RESULT_OK) {
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

                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                    if (cEdit.getCount() > 0) {
                        RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, result, jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                        db.updateDetailRoomWithFlagContent(orderModel);
                    }

                }
            } else {

                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                if (cEdit.getCount() > 0) {
                    if (cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).equalsIgnoreCase("")) {
                        RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, "", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                        db.deleteDetailRoomWithFlagContent(orderModel);
                    }
                }

            }
        } else if (requestCode == PLACE_PICKER_REQUEST) {
            showDialog = true;
            final List value = (List) hashMap.get(dummyIdDate);
            if (resultCode == Activity.RESULT_OK) {
                final Place place = PlacePicker.getPlace(data, mContext);
                final String name = place.getName() != null ? (String) place.getName() : " ";
                final String address = place.getAddress() != null ? (String) place.getAddress() : " ";
                final String web = String.valueOf(place.getWebsiteUri() != null ? place.getWebsiteUri() : " ");


                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                if (cEdit.getCount() > 0) {
                    RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, place.getLatLng().latitude + ";" + place.getLatLng().longitude + ";" + name + ";" + address + ";" + web + ";", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                    db.updateDetailRoomWithFlagContent(orderModel);
                }


                String text = "<u><b>" + name + "</b></u><br/>";
                et[Integer.valueOf(value.get(0).toString())].setText(Html.fromHtml(text + address));

            } else {

                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                if (cEdit.getCount() > 0) {
                    if (cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).equalsIgnoreCase("")) {
                        RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, "", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                        db.deleteDetailRoomWithFlagContent(orderModel);
                    }
                }

            }


        } else {
            if (resultCode == mContext.RESULT_OK) {
                Uri selectedUri = data.getData();
                String selectedImagePath = ImageFilePath.getPath(mContext, selectedUri);
                File fileOutput = new File(selectedImagePath);
                if (requestCode == REQ_VIDEO) {
                } else if (requestCode == REQ_GALLERY
                        || requestCode == REQ_GALLERY_VIDEO) {
                    String type = Message.TYPE_VIDEO;
                    if (requestCode == REQ_GALLERY) {
                        type = Message.TYPE_IMAGE;
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


                        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                        if (cEdit.getCount() > 0) {
                            RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, myBase64Image, jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                            db.updateDetailRoomWithFlagContent(orderModel);
                        }


                    }
                }
            } else {
                Toast.makeText(mContext, " Picture was not taken ", Toast.LENGTH_SHORT).show();
            }
        }
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


    private void captureGalery(String idDetail, String username, String idTab, String idListTask, String type, String name, String flag, int facing, String idii) {

        Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(idListTask, type, idii));
        if (cEdit.getCount() == 0) {
            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(idListTask, type, idii), name, "cild");
            db.insertRoomsDetail(orderModel);
        }

        dummyIdDate = Integer.parseInt(idListTask);

        if (flag.equalsIgnoreCase("live_camera")) {
            String action = Intent.ACTION_GET_CONTENT;
            Intent i = new Intent();
            action = MediaStore.ACTION_IMAGE_CAPTURE;

            File f = MediaProcessingUtil
                    .getOutputFile("jpeg");
            cameraFileOutput = f.getAbsolutePath();
            i.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(f));
            i.putExtra(
                    "android.intent.extras.CAMERA_FACING",
                    facing);
            i.setAction(action);
            startActivityForResult(i, REQ_CAMERA);
        }
        if (flag.equalsIgnoreCase("gallery")) {
            showAttachmentDialog(REQ_CAMERA);
        }
    }

    private void showAttachmentDialog(int req) {
        if (req == REQ_CAMERA) {
            curAttItems = attCameraItems;
        } else if (req == REQ_VIDEO) {
            curAttItems = attVideoItems;
        }
        attCurReq = req;

        AttachmentAdapter adapter = new AttachmentAdapter(mContext,
                R.layout.menu_item, R.id.textMenu, curAttItems);

        final Dialog dialog = new Dialog(mContext);
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

    //onDismiss handler
    private DialogInterface.OnDismissListener mOnDismissListenerDate =
            new DialogInterface.OnDismissListener() {
                public void onDismiss(DialogInterface dialog) {

                    List value = (List) hashMap.get(dummyIdDate);
                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                    if (cEdit.getCount() > 0) {
                        if (cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_CONTENT)).equalsIgnoreCase("")) {
                            RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, "", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                            db.deleteDetailRoomWithFlagContent(orderModel);
                        }
                    }

                }
            }; //onDismiss handler
    private DialogInterface.OnDismissListener mOnDismissListenerTime =
            new DialogInterface.OnDismissListener() {
                public void onDismiss(DialogInterface dialog) {

                    List value = (List) hashMap.get(dummyIdDate);
                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                    if (cEdit.getCount() > 0) {
                        if (cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_CONTENT)).equalsIgnoreCase("")) {
                            RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, "", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                            db.deleteDetailRoomWithFlagContent(orderModel);
                        }
                    }

                }
            };

    private TimePickerDialog.OnTimeSetListener mTimePicker =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, final int selectedHour, final int selectedMinute) {
                    mContext.runOnUiThread(new Runnable() {
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
                            et[Integer.valueOf(value.get(0).toString())].setText(sdate);
                            et[Integer.valueOf(value.get(0).toString())].setError(null);

                            Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                            if (cEdit.getCount() > 0) {
                                RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, String.valueOf(sdate), jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                                db.updateDetailRoomWithFlagContent(orderModel);
                            }

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
                    mContext.runOnUiThread(new Runnable() {
                        public void run() {
                            String sdate = LPad(mDay + "", "0", 2) + " " + arrMonth[mMonth] + " " + mYear;
                            List value = (List) hashMap.get(dummyIdDate);
                            et[Integer.valueOf(value.get(0).toString())].setText(sdate);
                            et[Integer.valueOf(value.get(0).toString())].setError(null);

                            Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                            if (cEdit.getCount() > 0) {
                                RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, String.valueOf(sdate), jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                                db.updateDetailRoomWithFlagContent(orderModel);
                            }

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

    public void addSpinner(final String jsonValue, final String namedb, final LinearLayout view, final String table, final String[] coloum, final Integer from, final String where, final String idListTask, final String type, final String finalI24, final String name) {
        DataBaseDropDown mDB = new DataBaseDropDown(mContext, namedb);
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

                LinearLayout spinerTitle = (LinearLayout) mContext.getLayoutInflater().inflate(R.layout.item_spiner_textview, null);
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
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(spinnerArrayAdapter);
                final String finalTitlle = titlle;


                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI24)));
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
                        if (!spinner.getSelectedItem().toString().replace("'", "''").equals("--Please Select--")) {
                            addSpinner(jsonValue, namedb, view, table, coloum, asIs, where + " and " + coloum[asIs] + "= '" + spinner.getSelectedItem().toString().replace("'", "''") + "'", idListTask, type, finalI24, name);

                            Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idRoomTab, "cild", jsonCreateType(idListTask, type, String.valueOf(finalI24)));
                            if (cEdit.getCount() > 0) {
                                try {
                                    JSONObject jsonObject = new JSONObject(cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                                    RoomsDetail orderModel = new RoomsDetail(idDetail, idRoomTab, username, function(jsonObject, finalTitlle, spinner.getSelectedItem().toString().replace("'", "''")).toString(), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "cild");
                                    db.updateDetailRoomWithFlagContent(orderModel);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else {
                                RoomsDetail orderModel = null;
                                try {
                                    orderModel = new RoomsDetail(idDetail, idRoomTab, username, function(null, finalTitlle, spinner.getSelectedItem().toString().replace("'", "''")).toString(), jsonCreateType(idListTask, type, String.valueOf(finalI24)), name, "cild");
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
    }


    private class posTask extends AsyncTask<String, String, String> {

        String error = "";

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            postData(params[0], params[1], params[2]);
            return null;
        }

        protected void onPostExecute(String result) {
            if (error.length() > 0) {
                Toast.makeText(mContext, error, Toast.LENGTH_LONG).show();
            }
            //dialog.dismiss();
        }

        protected void onProgressUpdate(String... string) {
        }

        public void postData(String valueIWantToSend, final String usr, final String idr) {
            // Create a new HttpClient and Post Header

            try {

                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 13000);
                HttpConnectionParams.setSoTimeout(httpParameters, 15000);
                HttpClient httpclient = new DefaultHttpClient(httpParameters);
                HttpPost httppost = new HttpPost(valueIWantToSend);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();


                Cursor cursorParent = db.getSingleRoomDetailFormWithFlag(idDetail, usr, idr, "parent");


                if (cursorParent.getCount() > 0) {
                    if (!cursorParent.getString(cursorParent.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)).equalsIgnoreCase("")) {
                        nameValuePairs.add(new BasicNameValuePair("latlong_before", jsonResultType(cursorParent.getString(cursorParent.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "a")));
                        nameValuePairs.add(new BasicNameValuePair("latlong_after", jsonResultType(cursorParent.getString(cursorParent.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "b")));
                    } else {
                        nameValuePairs.add(new BasicNameValuePair("latlong_before", "0.0"));
                        nameValuePairs.add(new BasicNameValuePair("latlong_after", "0.0"));
                    }
                } else {
                    nameValuePairs.add(new BasicNameValuePair("latlong_before", "0.0"));
                    nameValuePairs.add(new BasicNameValuePair("latlong_after", "0.0"));
                }

                MessengerDatabaseHelper messengerHelper = null;
                if (messengerHelper == null) {
                    messengerHelper = MessengerDatabaseHelper.getInstance(getContext());
                }

                Contact contact = messengerHelper.getMyContact();
                nameValuePairs.add(new BasicNameValuePair("bc_user", contact.getJabberId()));


                ArrayList<RoomsDetail> list = db.allRoomDetailFormWithFlag(idDetail, usr, idr, "cild");


                for (int u = 0; u < list.size(); u++) {

                    JSONArray jsA = null;
                    String content = "";
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
                        if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("distance_estimation") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("dropdown_dinamis") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("ocr") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("upload_document")) {
                            nameValuePairs.add(new BasicNameValuePair(list.get(u).getFlag_tab(), list.get(u).getContent()));
                            //   nameValuePairs.add(new BasicNameValuePair("key[]", list.get(u).getFlag_tab()));
                            //  nameValuePairs.add(new BasicNameValuePair("value[]", list.get(u).getContent()));
                            //  nameValuePairs.add(new BasicNameValuePair("type[]", jsonResultType(list.get(u).getFlag_content(), "b")));
                        } else {
                            try {
                                for (int ic = 0; ic < jsA.length(); ic++) {
                                    final String icC = jsA.getJSONObject(ic).getString("c").toString();
                                    content += icC + "|";
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            nameValuePairs.add(new BasicNameValuePair(list.get(u).getFlag_tab(), content.substring(0, content.length() - 1)));
                            //  nameValuePairs.add(new BasicNameValuePair("key[]", list.get(u).getFlag_tab()));
                            //   nameValuePairs.add(new BasicNameValuePair("value[]", content.substring(0, content.length() - 1)));
                            //  nameValuePairs.add(new BasicNameValuePair("type[]", jsonResultType(list.get(u).getFlag_content(), "b")));
                        }
                    } else {

                        nameValuePairs.add(new BasicNameValuePair(list.get(u).getFlag_tab(), list.get(u).getContent()));
                        //  nameValuePairs.add(new BasicNameValuePair("key[]", list.get(u).getFlag_tab()));
                        //  nameValuePairs.add(new BasicNameValuePair("value[]", list.get(u).getContent()));
                        //   nameValuePairs.add(new BasicNameValuePair("type[]", jsonResultType(list.get(u).getFlag_content(), "b")));
                    }

                }


                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                int status = response.getStatusLine().getStatusCode();


                if (status == 200) {
                    fifis();
                    HttpEntity entity = response.getEntity();
                    final String data = EntityUtils.toString(entity);

                    JSONObject jsonRootObject = null;
                    try {

                        jsonRootObject = new JSONObject(data);
                        String tipe = jsonRootObject.getString("type");
                        final String result = jsonRootObject.getString("result");

                        if (tipe.equalsIgnoreCase("webview")) {
                            Intent intent = new Intent(mContext, WebViewByonActivity.class);
                            intent.putExtra(WebViewByonActivity.KEY_LINK_LOAD, result);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else if (tipe.equalsIgnoreCase("dialog")) {
                            mContext.runOnUiThread(new Runnable() {
                                public void run() {
                                    final AlertDialog.Builder alertbox = new AlertDialog.Builder(mContext);
                                    alertbox.setMessage(Html.fromHtml(result) + "\n");
                                    alertbox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg0, int arg1) {

                                        }
                                    });

                                    alertbox.show();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {
                    fifis();
                    mContext.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(mContext, "Upload Gagal.", Toast.LENGTH_SHORT).show();
                            // TODO Auto-generated catch block
                        }
                    });
                }

            } catch (ConnectTimeoutException e) {
                fifis();
                e.printStackTrace();
                mContext.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(mContext, "Upload Gagal.", Toast.LENGTH_SHORT).show();
                        // TODO Auto-generated catch block
                    }
                });
            } catch (ClientProtocolException e) {
                fifis();
                mContext.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(mContext, "Upload Gagal.", Toast.LENGTH_SHORT).show();
                        // TODO Auto-generated catch block
                    }
                });
                // TODO Auto-generated catch block
            } catch (IOException e) {
                fifis();
                mContext.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(mContext, "Upload Gagal.", Toast.LENGTH_SHORT).show();
                        // TODO Auto-generated catch block
                    }
                });
            }
        }
    }

    private void fifis() {

        db.deleteRoomsDetailbyId(idDetail, idRoomTab, username);

        progressDialog.dismiss();
        mContext.runOnUiThread(new Runnable() {
            public void run() {
                generateLayut();
            }
        });
    }


    private class RefreshDBAsign extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        String error = "";
        private Activity activity;
        private Context context;
        ArrayAdapter<String> spinnerArrayAdapterNew;

        public RefreshDBAsign(Activity activity, ArrayAdapter<String> spinnerArrayAdapter) {
            this.activity = activity;
            context = activity;
            dialog = new ProgressDialog(activity);
            spinnerArrayAdapterNew = spinnerArrayAdapter;

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
            if (error.length() > 0) {
                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "DB success download.", Toast.LENGTH_SHORT).show();
                generateLayut();
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
                nameValuePairs.add(new BasicNameValuePair("id_rooms_tab", idRoomTab));
                MessengerDatabaseHelper messengerHelper = null;
                if (messengerHelper == null) {
                    messengerHelper = MessengerDatabaseHelper.getInstance(getContext());
                }

                Contact contact = messengerHelper.getMyContact();
                nameValuePairs.add(new BasicNameValuePair("bc_user", contact.getJabberId()));


                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {

                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);

                    DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(context);
                    dataBaseHelper.createUserTableDua("room_" + idRoomTab, data, username, idRoomTab);

                } else {
                    error = "Tolong periksa koneksi internet.";
                }

            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Tolong periksa koneksi internet.", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (ClientProtocolException e) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Tolong periksa koneksi internet.", Toast.LENGTH_SHORT).show();
                    }
                });
                // TODO Auto-generated catch block
            } catch (IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Tolong periksa koneksi internet.", Toast.LENGTH_SHORT).show();
                    }
                });
                // TODO Auto-generated catch block
            }
        }

    }


}
