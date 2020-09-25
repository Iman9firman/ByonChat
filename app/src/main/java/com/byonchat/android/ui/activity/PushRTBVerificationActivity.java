package com.byonchat.android.ui.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.byonchat.android.CaptureSignature;
import com.byonchat.android.ISSActivity.LoginDB.UserDB;
import com.byonchat.android.R;
import com.byonchat.android.ZoomImageViewActivity;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.createMeme.FilteringImage;
import com.byonchat.android.data.model.File;
import com.byonchat.android.model.SLAmodelNew;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.ui.adapter.OnPreviewItemClickListener;
import com.byonchat.android.ui.view.ByonchatRecyclerView;
import com.byonchat.android.utils.AndroidMultiPartEntity;
import com.byonchat.android.utils.ClientSSLSocketFactory;
import com.byonchat.android.utils.GetDropdownFormISS;
import com.byonchat.android.utils.HttpHelper;
import com.byonchat.android.utils.MediaProcessingUtil;
import com.byonchat.android.widget.ToolbarWithIndicator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import zharfan.com.cameralibrary.Camera;
import zharfan.com.cameralibrary.CameraActivity;

import static com.byonchat.android.FragmentDinamicRoom.DinamicSLATaskActivity.decodeBase64;

public class PushRTBVerificationActivity extends AppCompatActivity {
    String username;
    String name_title, task_id, id_list_task, id_rooms_tab_parent;

    LinearLayout layoutSC;
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
    ToolbarWithIndicator toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_repairment);

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#" + "022B96")));
        FilteringImage.SystemBarBackground(getWindow(), Color.parseColor("#" + "022B96"));

        Intent ntent = getIntent();
        if (ntent != null) {
            username = ntent.getStringExtra("username_room");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        db = BotListDB.getInstance(getApplicationContext());
        vListData = (ByonchatRecyclerView) findViewById(R.id.list_all);

        layoutForCheck = (LinearLayout) findViewById(R.id.layoutForCheck);
        layoutSC = (LinearLayout) findViewById(R.id.layoutSC);
        layoutSC.setVisibility(View.VISIBLE);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnCancel = (Button) findViewById(R.id.btn_cancel);


        TextView textTitle = (TextView) findViewById(R.id.title_arr);
        TextView textSubtitle = (TextView) findViewById(R.id.subtitle_arr);
        String ttl = getIntent().getStringExtra("title");
        String sbttl = getIntent().getStringExtra("subtitle");
        textSubtitle.setText(sbttl);
        textTitle.setText(ttl);


        TextView textView0 = new TextView(PushRTBVerificationActivity.this);
        textView0.setText("Type");
        textView0.setTextSize(15);

        TextView textView = new TextView(PushRTBVerificationActivity.this);
        textView.setText("Verified by");
        textView.setTextSize(20);
        textView.setTextColor(Color.BLACK);

        TextView textView1 = new TextView(PushRTBVerificationActivity.this);
        textView1.setText("NIK");
        textView1.setTextSize(15);

        TextView textView2 = new TextView(PushRTBVerificationActivity.this);
        textView2.setText("NAME");
        textView2.setTextSize(15);


        et = (EditText) getLayoutInflater().inflate(R.layout.edit_input_layout, null);
        et.setHint("NIK");
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins(30, 10, 30, 10);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params2.setMargins(30, 10, 30, 40);

        et2 = (EditText) getLayoutInflater().inflate(R.layout.edit_input_layout, null);
        et2.setHint("Name");


        LinearLayout photoSignLayout = (LinearLayout) findViewById(R.id.photo_sign_layout);
        imageViewSignature = (ImageView) findViewById(R.id.get_sign);
        imageViewSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), CaptureSignature.class);
                startActivityForResult(intent, SIGNATURE_ACTIVITY);

            }
        });
        imageviewPhoto = (ImageView) findViewById(R.id.get_photo);
        spinner = new Spinner(this);
        ArrayList<String> spinArr = new ArrayList<>();
        spinArr.add("-- Select Type --");
        spinArr.add("Report to be repair");
        spinArr.add("Messanger");
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinArr));
        spinner.setBackground(getResources().getDrawable(R.drawable.spinner_background));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String type = spinner.getSelectedItem().toString();
                if (type.equalsIgnoreCase("Report to be repair")) {
                    resultType = 0;
                } else if (type.equalsIgnoreCase("Messanger")) {
                    resultType = 1;
                } else {
                    resultType = -1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        layoutForCheck.addView(textView, params1);
        layoutForCheck.addView(textView1, params1);
        layoutForCheck.addView(et, params2);
        layoutForCheck.addView(textView2, params1);
        layoutForCheck.addView(et2, params2);
        layoutForCheck.addView(textView0, params1);
        layoutForCheck.addView(spinner, params1);
        photoSignLayout.setVisibility(View.VISIBLE);

        imageviewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }


                CameraActivity.Builder start = new CameraActivity.Builder(PushRTBVerificationActivity.this, 11);
                start.setLockSwitch(CameraActivity.UNLOCK_SWITCH_CAMERA);
                start.setCameraFace(CameraActivity.CAMERA_REAR);
                start.setFlashMode(CameraActivity.FLASH_OFF);
                start.setQuality(CameraActivity.MEDIUM);
                start.setRatio(CameraActivity.RATIO_4_3);
                start.setNIK(new UserDB(PushRTBVerificationActivity.this).getColValue(UserDB.EMPLOYEE_NIK));
                String huft = new MediaProcessingUtil().createFileName("", "ROOM");
                String name = huft.substring(0, huft.length() - 1);
                start.setFileName(name);
                new Camera(start.build()).lauchCamera();

            }
        });


        getMoreDetail();

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

    private void getMoreDetail() {
        ProgressDialog rdialog = new ProgressDialog(PushRTBVerificationActivity.this);
        rdialog.setMessage("Loading...");
        rdialog.show();

        String Url = "https://bb.byonchat.com/bc_voucher_client/webservice/category_tab/push_verifikasi_tobe_repair.php";
        Map<String, String> params = new HashMap<>();
        params.put("username_room", getIntent().getStringExtra("username_room"));
        params.put("bc_user", getIntent().getStringExtra("bc_user"));
        params.put("id_rooms_tab", getIntent().getStringExtra("id_rooms_tab"));
        params.put("task_id", getIntent().getStringExtra("task_id"));


        RequestQueue queue = Volley.newRequestQueue(PushRTBVerificationActivity.this, new HurlStack(null, ClientSSLSocketFactory.getSocketFactory()));

        StringRequest sr = new StringRequest(Request.Method.POST, Url,
                response -> {
                    rdialog.dismiss();
                    resolveData(response);
                    basejson = response;
                    resolveListFile();
                    resolveSend();

                },
                error ->
                {
                    rdialog.dismiss();
                    Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        )

        {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return params;
            }
        };
        sr.setRetryPolicy(new

                DefaultRetryPolicy(180000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }


    protected void resolveData(String dataResponse) {
        try {
            JSONObject gvcs = new JSONObject(dataResponse);
            name_title = gvcs.getString("title");
            task_id = gvcs.getString("task_id");
            id_list_task = gvcs.getString("id_list_task");
            id_rooms_tab_parent = gvcs.getString("id_rooms_tab_parent");

            JSONArray jar = gvcs.getJSONArray("value_detail");

            for (int i = 0; i < jar.length(); i++) {
                JSONObject first = jar.getJSONObject(i);
                String urutan = first.getString("urutan");
                JSONObject dataCa = first.getJSONObject("data");

                String before = dataCa.getString("foto_before");
                String after = dataCa.getString("foto_after");
                String ketAdter = dataCa.getString("ket_after");
                String ketBefore = dataCa.getString("ket_before");

                String id = urutan + "-" + task_id;

                SLAmodelNew fotonya = new SLAmodelNew(id_list_task, name_title, id, ketBefore, before, after, "", ketAdter);
                foto.add(fotonya);

            }
        } catch (JSONException e) {
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
                    dialog = new ProgressDialog(PushRTBVerificationActivity.this);
                }
                dialog.setMessage("Uploading Image ...");
                dialog.show();
                AsyncTask uploadPict = new UploadPhoto();
                ((UploadPhoto) uploadPict).setType("sign");
                ((UploadPhoto) uploadPict).execute("https://forward.byonchat.com:37001/1_345171158admin/bc_voucher_client/webservice/proses/file_processing.php",
                        username,
                        "3043",
                        "68351",
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
                            dialog = new ProgressDialog(PushRTBVerificationActivity.this);
                        }
                        dialog.setMessage("Uploading Image ...");
                        dialog.show();
                        AsyncTask uploadPict = new UploadPhoto();
                        ((UploadPhoto) uploadPict).setType("image");
                        ((UploadPhoto) uploadPict).execute("https://forward.byonchat.com:37001/1_345171158admin/bc_voucher_client/webservice/proses/file_processing.php",
                                username,
                                "3043",
                                "68351",
                                returnString);
                    }

                } else {
                    Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
                }

            } else if (resultCode == RESULT_CANCELED) {


                Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
            } else {

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


        mAdapter = new PushSLAVerificationAdapter(PushRTBVerificationActivity.this, "",
                getIntent().getStringExtra("username_room"), getIntent().getStringExtra("id_rooms_tab"),
                foto, new OnPreviewItemClickListener() {
            @Override
            public void onItemClick(View view, String position, File item, String type, String idts) {
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
                    Intent intent = new Intent(PushRTBVerificationActivity.this, ZoomImageViewActivity.class);
                    for (int i = 0; i < foto.size(); i++) {
                        if (foto.get(i).getId().equalsIgnoreCase(task_id)) {
                            intent.putExtra(ZoomImageViewActivity.KEY_FILE, foto.get(i).getBefore());
                        }
                    }
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(PushRTBVerificationActivity.this, ZoomImageViewActivity.class);
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
                    rdialog = new ProgressDialog(PushRTBVerificationActivity.this);
                    rdialog.setMessage("Loading...");
                    rdialog.show();

                    new UploadJSONSOn().execute("https://" + MessengerConnectionService.HTTP_SERVER + "/bc_voucher_client/webservice/category_tab/insert_verifikasi_tobe_repair.php",
                            getIntent().getStringExtra("username_room"), getIntent().getStringExtra("bc_user"),
                            getIntent().getStringExtra("id_rooms_tab"));
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
            name_title = gvcs.getString("title");
            task_id = gvcs.getString("task_id");
            id_list_task = gvcs.getString("id_list_task");
            id_rooms_tab_parent = gvcs.getString("id_rooms_tab_parent");

            JSONArray jar = gvcs.getJSONArray("value_detail");

            for (int i = 0; i < jar.length(); i++) {
                Log.w("YaIni1", i + "--");

                JSONObject first = jar.getJSONObject(i);
                String urutan = first.getString("urutan");
                JSONObject dataCa = first.getJSONObject("data");

                String id = urutan + "-" + task_id;

                for (int vi = 0; vi < foto.size(); vi++) {
                    Log.w("YaIni2", foto.get(vi).getId() + "--" + id);
                    if (foto.get(vi).getId().equalsIgnoreCase(id)) {
                        dataCa.put("v", foto.get(vi).getVerif() != "" ? "1" : "0");
                    }
                }


            }

            gvcs.put("signature", resultSignature);
            gvcs.put("photo", resultImage);
            if (spinner != null) {
                gvcs.put("type", resultType);
            }
            gvcs.put("nik", et.getText().toString() == null ? "" : et.getText().toString());
            gvcs.put("name", et2.getText().toString() == null ? "" : et2.getText().toString());

            stringdong = gvcs.toString();
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
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
            return uploadFile(params[0], params[1], params[2], params[3]);
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        @SuppressWarnings("deprecation")
        private String uploadFile(String URL, String username, String bc_user, String id_room) {
            String responseString = null;

            HttpClient httpclient = null;
            try {
                httpclient = HttpHelper.createHttpClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
                entity.addPart("json", new /*FileBody(gpxfile, contentType, gpxfile.getName())*/StringBody(fileJson()));

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

    private String getNameByIdSLA(String table, String kode_jjt, String id) {
        int jsonCek = new GetDropdownFormISS().getInstance(getApplicationContext()).getALL();
        if (jsonCek == 0) {
            Toast.makeText(getBaseContext(), "Harap Refresh di tab SLA", Toast.LENGTH_SHORT).show();
            return "-";
        }

        String header = " - ";
        String jsonForm = new GetDropdownFormISS().getInstance(getApplicationContext()).getName(kode_jjt);
        JSONObject levelBobot1 = null;
        if (table.equalsIgnoreCase("section")) {
            try {
                levelBobot1 = new JSONObject(jsonForm);
                JSONArray level1Array = levelBobot1.getJSONArray("data");
                for (int oneL = 0; oneL < level1Array.length(); oneL++) {
                    JSONObject objectOne = level1Array.getJSONObject(oneL);
                    JSONArray level2Array = objectOne.getJSONArray("data");
                    for (int twoL = 0; twoL < level2Array.length(); twoL++) {
                        JSONObject objectTwo = level2Array.getJSONObject(twoL);
                        String _id = objectTwo.getString("id");
                        String label = objectTwo.getString("label");
                        if (id.equalsIgnoreCase(_id)) {
                            return label;

                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (table.equalsIgnoreCase("pertanyaan")) {
            try {
                levelBobot1 = new JSONObject(jsonForm);
                JSONArray level1Array = levelBobot1.getJSONArray("data");
                for (int oneL = 0; oneL < level1Array.length(); oneL++) {
                    JSONObject objectOne = level1Array.getJSONObject(oneL);
                    JSONArray level2Array = objectOne.getJSONArray("data");
                    for (int twoL = 0; twoL < level2Array.length(); twoL++) {
                        JSONObject objectTwo = level2Array.getJSONObject(twoL);
                        JSONArray level3Array = objectTwo.getJSONArray("data");
                        for (int threeL = 0; threeL < level3Array.length(); threeL++) {
                            JSONObject objectThree = level3Array.getJSONObject(threeL);
                            JSONArray level4Array = objectThree.getJSONArray("data");
                            for (int fourL = 0; fourL < level4Array.length(); fourL++) {
                                JSONObject objectFour = level4Array.getJSONObject(fourL);

                                String id4 = objectFour.getString("id");
                                String lb4 = objectFour.getString("label");

                                if (id.equalsIgnoreCase(id4)) {
                                    return lb4;

                                }

                            }
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Log.w("Ingin2", e.getMessage());
            }
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

            HttpClient httpclient = null;
            try {
                httpclient = HttpHelper.createHttpClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            final byte[] imgBytesData = Base64.decode(imageData,
                    Base64.DEFAULT);

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

    @Override
    protected void onPause() {
        super.onPause();
        toolbar.stopScan();
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.startScan("forward.byonchat.com", PushRTBVerificationActivity.this);
    }

}