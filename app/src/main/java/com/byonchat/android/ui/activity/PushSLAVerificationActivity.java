package com.byonchat.android.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.byonchat.android.CaptureSignature;
import com.byonchat.android.DownloadSqliteDinamicActivity;
import com.byonchat.android.FragmentDinamicRoom.DinamicSLATaskActivity;
import com.byonchat.android.FragmentSLA.ZhFourFragment;
import com.byonchat.android.ISSActivity.LoginDB.UserDB;
import com.byonchat.android.R;
import com.byonchat.android.ZoomImageViewActivity;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.data.model.File;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.model.Photo;
import com.byonchat.android.model.SLAmodelNew;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.DataBaseDropDown;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.ui.adapter.OnPreviewItemClickListener;
import com.byonchat.android.ui.adapter.OnRequestItemClickListener;
import com.byonchat.android.ui.view.ByonchatRecyclerView;
import com.byonchat.android.utils.AllAboutUploadTask;
import com.byonchat.android.utils.AndroidMultiPartEntity;
import com.byonchat.android.utils.MediaProcessingUtil;
import com.google.android.gms.vision.L;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

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

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import zharfan.com.cameralibrary.Camera;
import zharfan.com.cameralibrary.CameraActivity;

import static com.byonchat.android.FragmentDinamicRoom.DinamicSLATaskActivity.decodeBase64;
import static com.byonchat.android.ui.activity.PustSLAFollowUpActivity.resizeAndCompressImageBeforeSend;

public class PushSLAVerificationActivity extends AppCompatActivity {
    String task_id, /*id_task,*/
            id_task_list, id_rooms_tab;
    String kode_jjt;
    String username;
    String name_title;
    LinearLayout linearLayout;
    ByonchatRecyclerView vListData;
    ArrayList<SLAmodelNew> foto = new ArrayList<>();
    PushSLAVerificationAdapter mAdapter;
    private static final int REQ_CAMERA = 1201;
    Button btnSubmit, btnCancel;
    ProgressDialog rdialog;
    Integer totalUpload = 0;
    BotListDB db;
    private String basejson;
    LinearLayout layoutForCheck;
    private static final int SIGNATURE_ACTIVITY = 1205;
    ImageView imageViewSignature, imageviewPhoto;

    String resultImage = "";
    String resultSignature = "";
    int resultType = -1;
    EditText et, et2;
    Spinner spinner;
    Bitmap imgBm, sgnBm;
    ProgressDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_repairment);

        Intent ntent = getIntent();
        if (ntent != null) {
            username = ntent.getStringExtra("username_room");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        db = BotListDB.getInstance(getApplicationContext());
        vListData = (ByonchatRecyclerView) findViewById(R.id.list_all);

        layoutForCheck = (LinearLayout) findViewById(R.id.layoutForCheck);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        basejson = getIntent().getStringExtra("data");

        TextView textTitle = (TextView) findViewById(R.id.title_arr);
        TextView textSubtitle = (TextView) findViewById(R.id.subtitle_arr);
        String ttl = getIntent().getStringExtra("title");
        String sbttl = getIntent().getStringExtra("subtitle");
        textSubtitle.setText(sbttl);
        textTitle.setText(ttl);

        TextView textView = new TextView(PushSLAVerificationActivity.this);
        textView.setText("Verified by");
        textView.setTextSize(15);

        et = (EditText) getLayoutInflater().inflate(R.layout.edit_input_layout, null);
        et.setHint("NIK");
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins(30, 10, 30, 0);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params2.setMargins(30, 10, 30, 40);

        et2 = (EditText) getLayoutInflater().inflate(R.layout.edit_input_layout, null);
        et2.setHint("Name");


//        TextView textViewDua = new TextView(PushSLAVerificationActivity.this);
//        textViewDua.setText(Html.fromHtml("Signature"));
//        textViewDua.setTextSize(15);


        LinearLayout photoSignLayout = (LinearLayout) findViewById(R.id.photo_sign_layout);
//        imageViewSignature = (ImageView) getLayoutInflater().inflate(R.layout.frame_signature_form_black, null);
        imageViewSignature = (ImageView) findViewById(R.id.get_sign);
//        imageViewSignature.setImageDrawable(getResources().getDrawable(R.drawable.ico_signature));
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        params.setMargins(5, 15, 0, 0);

//        imageViewSignature.setLayoutParams(params);
//        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        imageViewSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), CaptureSignature.class);
                startActivityForResult(intent, SIGNATURE_ACTIVITY);

            }
        });


//        TextView textViewTiga = new TextView(PushSLAVerificationActivity.this);
//        textViewTiga.setText("Photo");
//        textViewTiga.setTextSize(15);

//        imageviewPhoto = (ImageView) getLayoutInflater().inflate(R.layout.image_view_frame, null);
        imageviewPhoto = (ImageView) findViewById(R.id.get_photo);
//        int width = getWindowManager().getDefaultDisplay().getWidth();
//        RelativeLayout.LayoutParams paramsDua = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, width / 2);
//        paramsDua.setMargins(5, 15, 0, 0);
//        imageviewPhoto.setLayoutParams(paramsDua);
//        paramsDua.addRule(RelativeLayout.CENTER_IN_PARENT);

        spinner = new Spinner(this);
        ArrayList<String> spinArr = new ArrayList<>();
        spinArr.add("-- Select Type --");
        spinArr.add("Daily");
        spinArr.add("KPI");
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinArr));
        spinner.setBackground(getResources().getDrawable(R.drawable.spinner_background));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String type = spinner.getSelectedItem().toString();
                if (type.equalsIgnoreCase("Daily")) {
                    resultType = 0;
                } else if (type.equalsIgnoreCase("KPI")) {
                    resultType = 1;
                } else {
                    resultType = -1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        layoutForCheck.addView(spinner, params1);
        layoutForCheck.addView(textView, params1);
        layoutForCheck.addView(et, params2);
        layoutForCheck.addView(et2, params2);
        photoSignLayout.setVisibility(View.VISIBLE);
//        layoutForCheck.addView(textViewDua, params1);
//        layoutForCheck.addView(imageViewSignature, params2);
//        layoutForCheck.addView(textViewTiga, params1);
//        layoutForCheck.addView(imageviewPhoto, paramsDua);


        imageviewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }


                CameraActivity.Builder start = new CameraActivity.Builder(PushSLAVerificationActivity.this, 11);
                start.setLockSwitch(CameraActivity.UNLOCK_SWITCH_CAMERA);
                start.setCameraFace(CameraActivity.CAMERA_REAR);
                start.setFlashMode(CameraActivity.FLASH_OFF);
                start.setQuality(CameraActivity.MEDIUM);
                start.setRatio(CameraActivity.RATIO_4_3);
                start.setNIK(new UserDB(PushSLAVerificationActivity.this).getColValue(UserDB.EMPLOYEE_NIK));
                String huft = new MediaProcessingUtil().createFileName("", "ROOM");
                String name = huft.substring(0, huft.length() - 1);
                start.setFileName(name);
                new Camera(start.build()).lauchCamera();

            }
        });

        resolveData();
        resolveListFile();
        resolveSend();

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

    protected void resolveData() {
        try {
            JSONObject gvcs = new JSONObject(getIntent().getStringExtra("data"));
            Log.w("OKee argus", gvcs + "");
//            id_task = gvcs.getString("task_id");
            id_task_list = gvcs.getString("id_list_task");
            id_rooms_tab = gvcs.getString("id_rooms_tab_parent");
            name_title = gvcs.getString("title");
            kode_jjt = gvcs.getString("kode_jjt");

            JSONArray jar = gvcs.getJSONArray("value_detail");
            String idSection = "";
            String idSubSection = "";
            String idPertanyaan = "";
            String idItem = "";

            String headerTwo = "";
            String headerFour = "";

            String noSatu = "1";
            String noDua = "1";
            String noTiga = "1";
            String noEmpat = "1";

            for (int i = 0; i < jar.length(); i++) {
                JSONObject first = jar.getJSONObject(i);
                JSONObject pembobotan = first.getJSONObject("pembobotan");
//                for (int ii = 0; ii < pembobotan.length(); ii++) {
                JSONObject second = pembobotan.getJSONObject("section");
//                    JSONArray section = second.getJSONArray("section");
                idSection = pembobotan.getString("id");
//                    noSatu = String.valueOf(ii + 1);
//                    for (int iii = 0; iii < section.length(); iii++) {
//                        JSONObject third = section.getJSONObject(iii);
                JSONObject subsection = second.getJSONObject("subsection");
                idSubSection = second.getString("id");
                String asiop2[] = {"title"};
                headerTwo = getNameByIdSLA("section", asiop2, idSubSection);
//                        noDua = String.valueOf(iii + 1);
//                        for (int iv = 0; iv < subsection.length(); iv++) {
//                            JSONObject fourth = subsection.getJSONObject(iv);
                JSONArray pertanyaan = subsection.getJSONArray("pertanyaan");
                idPertanyaan = subsection.getString("id");
//                            noTiga = String.valueOf(iv + 1);
                for (int v = 0; v < pertanyaan.length(); v++) {
                    JSONObject fifth = pertanyaan.getJSONObject(v);
                    idItem = fifth.getString("id_task");
                    String valid = fifth.getString("v");
                    if (valid.equalsIgnoreCase("0")) {
                        String id = idSection + "-" + idSubSection + "-" + idPertanyaan + "-" + idItem;

                        noEmpat = String.valueOf(v + 1);
                        String asiop4[] = {"pertanyaan"};
                        headerFour = getNameByIdSLA("pertanyaan", asiop4, idItem);

                        String id_task = fifth.getString("id_task");
                        String fotony = fifth.getString("f");
                        String title = fifth.getString("n");
                        String ket = "";
                        if (fifth.has("ket")) {
                            ket = fifth.getString("ket");
                        }

                        String aftera = fifth.getString("a");
                        if (!fotony.contains("http://")) {
                            fotony = "https://bb.byonchat.com/bc_voucher_client/images/list_task/" + fifth.getString("f");
                        }

                        if (!aftera.contains("http://")) {
                            aftera = "https://bb.byonchat.com/bc_voucher_client/images/list_task/" + fifth.getString("a");
                        }

                        String header = noSatu + "." + noDua + "." + noTiga + "." + noEmpat + ". " + headerTwo + " - " + headerFour;

                        SLAmodelNew fotonya = new SLAmodelNew(id_task, header, id, title, fotony, aftera, valid, ket);
                        foto.add(fotonya);
                    }
//                            }
//                        }
//                    }
                }
            }
        } catch (JSONException e) {
            Log.w("ERROR argus", e.getMessage());
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CAMERA) {
            if (resultCode == RESULT_OK) {
                String returnString = data.getStringExtra("PICTURE");
                if (decodeFile(returnString)) {
                    final java.io.File f = new java.io.File(returnString);
                    if (f.exists()) {
                        FileInputStream inputStream = null;
                        try {
                            inputStream = new FileInputStream(f);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        MediaProcessingUtil.decodeSampledBitmapFromResourceMemOpt(inputStream, 800,
                                800);


                        for (int i = 0; i < foto.size(); i++) {
                            if (foto.get(i).getId().equalsIgnoreCase(task_id)) {
                                RoomsDetail orderModel = new RoomsDetail(foto.get(i).getId_task(), getIntent().getStringExtra("id_rooms_tab"), getIntent().getStringExtra("username_room"), f.toString(), task_id, null, "reportrepair");
                                db.insertRoomsDetail(orderModel);

                                foto.get(i).setAfter(f);
                            }
                        }

                        mAdapter.notifyDataSetChanged();
                    }

                } else {
                    Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
                }

            }
        } else if (requestCode == SIGNATURE_ACTIVITY) {
            if (resultCode == RESULT_OK) {
//                resultSignature = data.getExtras().getString("status");
                java.io.File file = saveImage(getApplicationContext(), data.getExtras().getString("status"));
                sgnBm = decodeBase64(data.getExtras().getString("status"));
                if (dialog == null) {
                    dialog = new ProgressDialog(PushSLAVerificationActivity.this);
                }
                dialog.setMessage("Uploading Image ...");
                dialog.show();
                AsyncTask uploadPict = new UploadPhoto();
                ((UploadPhoto) uploadPict).setType("sign");
                ((UploadPhoto) uploadPict).execute("https://bb.byonchat.com/bc_voucher_client/webservice/proses/file_processing.php",
                        username,
                        "2613",
                        "66989",
                        file.getAbsolutePath());
                /*Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                if (cEdit.getCount() > 0) {
                    RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, result, jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                    db.updateDetailRoomWithFlagContent(orderModel);
                }*/
            } else {
                imageViewSignature.setImageDrawable(getResources().getDrawable(R.drawable.dotted_square));

               /* Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                if (cEdit.getCount() > 0) {
                    if (cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).equalsIgnoreCase("")) {
                        RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                        db.deleteDetailRoomWithFlagContent(orderModel);
                        imageView[Integer.valueOf(value.get(0).toString())].setImageDrawable(getResources().getDrawable(R.drawable.ico_signature));
                    }
                }*/

            }
        } else if (requestCode == 11) {
            if (resultCode == RESULT_OK) {
                String returnString = data.getStringExtra("PICTURE");
                if (decodeFile(returnString)) {
                    final java.io.File f = new java.io.File(returnString);
                    if (f.exists()) {
                        FileInputStream inputStream = null;
                        try {
                            inputStream = new FileInputStream(f);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        Bitmap result = MediaProcessingUtil.decodeSampledBitmapFromResourceMemOpt(inputStream, 800,
                                800);
                        imgBm = result;
                        if (dialog == null) {
                            dialog = new ProgressDialog(PushSLAVerificationActivity.this);
                        }
                        dialog.setMessage("Uploading Image ...");
                        dialog.show();
                        AsyncTask uploadPict = new UploadPhoto();
                        ((UploadPhoto) uploadPict).setType("image");
                        ((UploadPhoto) uploadPict).execute("https://bb.byonchat.com/bc_voucher_client/webservice/proses/file_processing.php",
                                username,
                                "2613",
                                "66989",
                                returnString);
//                        myBase64Image = encodeToBase64(result, Bitmap.CompressFormat.JPEG, 80);

                           /* Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                            if (cEdit.getCount() > 0) {
                                SimpleDateFormat dateFormatNew = new SimpleDateFormat(
                                        "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                                long date = System.currentTimeMillis();
                                String dateString = dateFormatNew.format(date);

                                RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, dateTaken(myBase64Image, dateString), jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                                db.updateDetailRoomWithFlagContent(orderModel);
                            }*/
                    }

                } else {
                    Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
                }

            } else if (resultCode == RESULT_CANCELED) {
/*

                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                    if (cEdit.getCount() > 0) {
                        if (cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).equalsIgnoreCase("")) {
                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                            db.deleteDetailRoomWithFlagContent(orderModel);
                        }
                    }
*/


                Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
            } else {

/*
                    Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "cild", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()));
                    if (cEdit.getCount() > 0) {
                        if (cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)).equalsIgnoreCase("")) {
                            RoomsDetail orderModel = new RoomsDetail(idDetail, idTab, username, "", jsonCreateType(String.valueOf(dummyIdDate), value.get(2).toString(), value.get(5).toString()), cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "cild");
                            db.deleteDetailRoomWithFlagContent(orderModel);
                        }
                    }*/

                Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat,
                                        int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
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
                final java.io.File f = new java.io.File(path);
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
                final java.io.File f = new java.io.File(path);
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
                final java.io.File f = new java.io.File(path);
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
            final java.io.File f = new java.io.File(path);
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

    protected void resolveListFile() {


        mAdapter = new PushSLAVerificationAdapter(getApplication(), "",
                getIntent().getStringExtra("username_room"), getIntent().getStringExtra("id_rooms_tab"),
                foto, new OnPreviewItemClickListener() {
            @Override
            public void onItemClick(View view, String position, File item, String type) {
                task_id = position + "";
                if (type.equalsIgnoreCase("changeVerif")) {
                    for (int i = 0; i < foto.size(); i++) {
                        if (foto.get(i).getId().equalsIgnoreCase(task_id)) {
                            if (foto.get(i).getVerif().equalsIgnoreCase("0")) {
                                foto.get(i).setVerif("1");
                            } else {
                                foto.get(i).setVerif("0");
                            }
                        }
                    }

                    mAdapter.notifyDataSetChanged();
                } else if (type.equalsIgnoreCase("before")) {
                    Intent intent = new Intent(PushSLAVerificationActivity.this, ZoomImageViewActivity.class);
                    for (int i = 0; i < foto.size(); i++) {
                        if (foto.get(i).getId().equalsIgnoreCase(task_id)) {
                            intent.putExtra(ZoomImageViewActivity.KEY_FILE, foto.get(i).getBefore());
                        }
                    }
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(PushSLAVerificationActivity.this, ZoomImageViewActivity.class);
                    for (int i = 0; i < foto.size(); i++) {
                        if (foto.get(i).getId().equalsIgnoreCase(task_id)) {
                            intent.putExtra(ZoomImageViewActivity.KEY_FILE, foto.get(i).getAfterString());
                        }
                    }
                    startActivity(intent);
                }
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        vListData.setLayoutManager(mLayoutManager);
        vListData.setItemAnimator(new DefaultItemAnimator());
        vListData.setAdapter(mAdapter);
    }

    private void resolveSend() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!resultImage.equalsIgnoreCase("") && !resultSignature.equalsIgnoreCase("") && resultType != -1) {
                    rdialog = new ProgressDialog(PushSLAVerificationActivity.this);
                    rdialog.setMessage("Loading...");
                    rdialog.show();
                    new UploadJSONSOn().execute("https://" + MessengerConnectionService.HTTP_SERVER + "/bc_voucher_client/webservice/category_tab/insert_verifikasi_sla_new.php",
                            getIntent().getStringExtra("username_room"), getIntent().getStringExtra("bc_user"),
                            getIntent().getStringExtra("id_rooms_tab"), kode_jjt);
                } else {
                    Toast.makeText(getApplicationContext(), "Harap isi Type, Photo, dan Signature", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String fileJson() {
        String stringdong = "";

        try {
            JSONObject gvcs = new JSONObject(basejson);
            JSONArray jar = gvcs.getJSONArray("value_detail");

            String idSection = "";
            String idSubSection = "";
            String idPertanyaan = "";
            String idItem = "";

            for (int i = 0; i < jar.length(); i++) {
                JSONObject first = jar.getJSONObject(i);
                JSONObject pembobotan = first.getJSONObject("pembobotan");

                idSection = pembobotan.getString("id");
                JSONObject second = pembobotan.getJSONObject("section");

                idSubSection = second.getString("id");
                JSONObject subsection = second.getJSONObject("subsection");

                idPertanyaan = subsection.getString("id");
                JSONArray pertanyaan = subsection.getJSONArray("pertanyaan");

                for (int v = 0; v < pertanyaan.length(); v++) {
                    JSONObject fifth = pertanyaan.getJSONObject(v);
                    idItem = fifth.getString("id");
                    String id = idSection + "-" + idSubSection + "-" + idPertanyaan + "-" + idItem;
                    for (int vi = 0; vi < foto.size(); vi++) {
                        if (foto.get(vi).getId().equalsIgnoreCase(id)) {
                            fifth.remove("v");
                            fifth.put("v", foto.get(vi).getVerif());
                        }
                    }
                }

                first.put("signature", resultSignature);
                first.put("photo", resultImage);
//                type 0 = Daily , type 1 = KPI
                if (spinner != null) {
                    first.put("type", resultType);
                }
                first.put("nik", et.getText().toString() == null ? "" : et.getText().toString());
                first.put("name", et2.getText().toString() == null ? "" : et2.getText().toString());
            }

            stringdong = gvcs.toString();
            Log.w("brurhur argus", stringdong);
        } catch (JSONException e) {
            Log.w("brorhur argus", e.getMessage());
        }

        return stringdong;
    }

    private class UploadJSONSOn extends AsyncTask<String, Integer, String> {
        long totalSize = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return uploadFile(params[0], params[1], params[2], params[3], params[4]);
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        @SuppressWarnings("deprecation")
        private String uploadFile(String URL, String username, String bc_user, String id_room, String kode_jjt) {
            String responseString = null;

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


                java.io.File gpxfile = null;
                try {
                    java.io.File root = new java.io.File(Environment.getExternalStorageDirectory(), "ByonChat_Upload");
                    if (!root.exists()) {
                        root.mkdirs();
                    }
                    gpxfile = new java.io.File(root, username + id_room + bc_user + ".json");
                    FileWriter writer = new FileWriter(gpxfile);
                    writer.append(fileJson());
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                if (!gpxfile.exists()) {
                    return "File not exists";
                }

                ContentType contentType = ContentType.create("multipart/form-data");
                entity.addPart("username_room", new StringBody(username));
                entity.addPart("bc_user", new StringBody(bc_user));
                entity.addPart("id_rooms_tab", new StringBody(id_room));
//                entity.addPart("kode_jjt", new StringBody(kode_jjt));
                entity.addPart("json", new /*FileBody(gpxfile, contentType, gpxfile.getName())*/StringBody(fileJson()));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    String _response = EntityUtils.toString(r_entity); // content will be consume only once
                    Log.w("waterews", "1. " + _response);
                    return _response;
                } else {
                    Log.w("waterews", "2. " + "error");
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                Log.w("waterews", "3. " + e.getMessage());
                responseString = e.toString();
            } catch (IOException e) {
                Log.w("waterews", "4. " + e.getMessage());
                responseString = e.toString();
            }

            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.w("waterews", "5. " + result);
            Toast.makeText(getApplicationContext(), "Success Uploading Report", Toast.LENGTH_LONG).show();
            rdialog.dismiss();
            finish();
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

    private String getNameByIdSLA(String table, String asiop[], String id) {
        String header = "";
        DataBaseDropDown mDBDquerySLA = new DataBaseDropDown(PushSLAVerificationActivity.this, "sqlite_iss");
        if (mDBDquerySLA.getWritableDatabase() != null) {


            final Cursor css = mDBDquerySLA.getWritableDatabase().query(true, table, asiop,
                    "id = '" + id + "'", null, null, null, null, null);

            if (css.moveToFirst()) {
                header = css.getString(0);
            }

        } else {
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Toast.makeText(PushSLAVerificationActivity.this, "Please insert memmory card", Toast.LENGTH_LONG).show();
                finish();
            }

            finish();
            Intent intent = new Intent(PushSLAVerificationActivity.this, DownloadSqliteDinamicActivity.class);
            intent.putExtra("name_db", "sqlite_iss");
            intent.putExtra("path_db", "https://bb.byonchat.com/bc_voucher_client/public/list_task/dropdown_dinamis/sqlite_iss.sqlite");
            startActivity(intent);
            return header;
        }
        return header;

    }

    private class UploadPhoto extends AsyncTask<String, Integer, String> {
        long totalSize = 0;
        String ii;
        String id;

        public void setType(String type) {
            this.type = type;
        }

        String type;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return uploadFile(params[0], params[1], params[2], params[3], params[4]);
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        @SuppressWarnings("deprecation")
        private String uploadFile(String URL, String username, String id_room, String id_list, String value) {
            String responseString = null;
            ii = value;

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
                java.io.File sourceFile = new java.io.File(resizeAndCompressImageBeforeSend(getBaseContext(), ii, "fileUploadBC_" + new Date().getTime() + ".jpg"));

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
                    String _response = EntityUtils.toString(r_entity); // content will be consume only once
                    return _response;
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
            if (dialog != null) {
                dialog.hide();
            }
            try {
                JSONObject jsonObject = new JSONObject(result);
                String message = jsonObject.getString("message");
                if (message.length() == 0) {
                    String fileNameServer = jsonObject.getString("filename");
                    if (type.equalsIgnoreCase("image")) {
                        imageviewPhoto.setImageBitmap(imgBm);
                        resultImage = fileNameServer;
                    } else if (type.equalsIgnoreCase("sign")) {
                        imageViewSignature.setImageBitmap(sgnBm);
                        resultSignature = fileNameServer;
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();

            }
            super.onPostExecute(result);
        }
    }

    public static java.io.File saveImage(final Context context, final String imageData) {
        java.io.File file = null;
        try {
            final byte[] imgBytesData = android.util.Base64.decode(imageData,
                    android.util.Base64.DEFAULT);

            file = java.io.File.createTempFile("image", null, context.getCacheDir());
            final FileOutputStream fileOutputStream;
            try {
                fileOutputStream = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }

            final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                    fileOutputStream);
            try {
                bufferedOutputStream.write(imgBytesData);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                try {
                    bufferedOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

}