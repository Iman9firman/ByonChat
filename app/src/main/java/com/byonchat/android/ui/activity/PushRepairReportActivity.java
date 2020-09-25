package com.byonchat.android.ui.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
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
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.byonchat.android.ISSActivity.LoginDB.UserDB;
import com.byonchat.android.R;
import com.byonchat.android.ZoomImageViewActivity;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.createMeme.FilteringImage;
import com.byonchat.android.data.model.File;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.model.Photo;
import com.byonchat.android.model.SLAmodelNew;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.provider.SLANoteDB;
import com.byonchat.android.ui.adapter.OnPreviewItemClickListener;
import com.byonchat.android.ui.adapter.OnRequestItemClickListener;
import com.byonchat.android.ui.view.ByonchatRecyclerView;
import com.byonchat.android.utils.AllAboutUploadTask;
import com.byonchat.android.utils.AndroidMultiPartEntity;
import com.byonchat.android.utils.ClientSSLSocketFactory;
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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import static com.byonchat.android.provider.SLANoteDB.COLUMN_FILEUPLOAD;
import static com.byonchat.android.provider.SLANoteDB.COLUMN_ID_DETAIL;
import static com.byonchat.android.provider.SLANoteDB.TABLE_NAME;

public class PushRepairReportActivity extends AppCompatActivity {
    String task_id, id_task, id_task_list, id_rooms_tab;
    String name_title;
    LinearLayout linearLayout;
    ByonchatRecyclerView vListData;
    ArrayList<SLAmodelNew> foto = new ArrayList<>();
    ArrayList<SLAmodelNew> uploadfoto = new ArrayList<>();
    PustReportRepairAdapter mAdapter;
    private static final int REQ_CAMERA = 1201;
    Button btnSubmit, btnCancel;
    ProgressDialog rdialog;
    BotListDB db;
    SLANoteDB NoteDB;
    ToolbarWithIndicator toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_repairment);

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#" + "022B96")));
        FilteringImage.SystemBarBackground(getWindow(), Color.parseColor("#" + "022B96"));


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        db = BotListDB.getInstance(getApplicationContext());
        vListData = (ByonchatRecyclerView) findViewById(R.id.list_all);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        NoteDB = new SLANoteDB(getApplicationContext());

        TextView textTitle = (TextView) findViewById(R.id.title_arr);
        TextView textSubtitle = (TextView) findViewById(R.id.subtitle_arr);
        String ttl = getIntent().getStringExtra("title");
        String sbttl = getIntent().getStringExtra("subtitle");
        textSubtitle.setText(sbttl);
        textTitle.setText(ttl);

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
        ProgressDialog rdialog = new ProgressDialog(PushRepairReportActivity.this);
        rdialog.setMessage("Loading...");
        rdialog.show();

        String Url = "https://forward.byonchat.com:37001/1_345171158admin/bc_voucher_client/webservice/category_tab/push_tobe_repair_per_item.php";
        Map<String, String> params = new HashMap<>();
        params.put("username_room", getIntent().getStringExtra("username_room"));
        params.put("bc_user", getIntent().getStringExtra("bc_user"));
        params.put("id_rooms_tab", getIntent().getStringExtra("id_rooms_tab"));
        params.put("task_id", getIntent().getStringExtra("task_id"));


        RequestQueue queue = Volley.newRequestQueue((FragmentActivity) PushRepairReportActivity.this, new HurlStack(null, ClientSSLSocketFactory.getSocketFactory()));

        StringRequest sr = new StringRequest(Request.Method.POST, Url,
                response -> {
                    Log.w("RERE", response);
                    rdialog.dismiss();
                    try {
                        resolveData(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

    protected void resolveData(String json) throws JSONException {

        try {
            JSONObject gvcs = new JSONObject(json);
            id_task = gvcs.getString("task_id");
            id_task_list = gvcs.getString("id_list_task");
            id_rooms_tab = gvcs.getString("id_rooms_tab_parent");
            name_title = gvcs.getString("title");
            JSONArray jar = gvcs.getJSONArray("value_detail");
            for (int i = 0; i < jar.length(); i++) {
                JSONObject basefoto = jar.getJSONObject(i);
                String fotony = basefoto.getString("foto");
                String title = basefoto.getString("keterangan");
                String urutan = basefoto.getString("urutan");
                String id = id_task + "-" + urutan;

                Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(id_task, getIntent().getStringExtra("username_room"), getIntent().getStringExtra("id_rooms_tab"), "reportrepair", id);
                SLAmodelNew fotonya = null;
                if (cursorCild.getCount() > 0) {
                    java.io.File f = new java.io.File(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                    fotonya = new SLAmodelNew(id_task, "Header", id, title, fotony, f);
                } else {
                    fotonya = new SLAmodelNew(id_task, "Header", id, title, fotony, (java.io.File) null);
                }

                foto.add(fotonya);
            }
        } catch (JSONException e) {
            JSONObject gvcs = new JSONObject(json);
            id_task = gvcs.getString("task_id");
            id_task_list = gvcs.getString("id_list_task");
            id_rooms_tab = gvcs.getString("id_rooms_tab_parent");
            name_title = gvcs.getString("title");
            JSONArray jar = gvcs.getJSONArray("value_detail");
            for (int i = 0; i < jar.length(); i++) {
                JSONObject basefoto = jar.getJSONObject(i);

                String urutan = basefoto.getString("urutan");

                String id = id_task + "-" + urutan;

                JSONArray dataFoto = basefoto.getJSONArray("data");
                if (dataFoto.length() == 2) {
                    String fotony = dataFoto.getJSONObject(0).getString("foto");
                    String title = dataFoto.getJSONObject(0).getString("keterangan");
                    SLAmodelNew fotonya = new SLAmodelNew(id_task, "Header", id, title, fotony, (java.io.File) null);
                    foto.add(fotonya);
                }

            }
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

                        RoomsDetail orderModel = new RoomsDetail(id_task, getIntent().getStringExtra("id_rooms_tab"), getIntent().getStringExtra("username_room"), f.toString(), task_id, null, "reportrepair");
                        db.insertRoomsDetail(orderModel);

                        for (int i = 0; i < foto.size(); i++) {
                            if (foto.get(i).getId().equalsIgnoreCase(task_id)) {
                                foto.get(i).setAfter(f);
                            }
                        }

                        mAdapter.notifyDataSetChanged();
                    }

                } else {
                    Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
                }

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


        mAdapter = new PustReportRepairAdapter(this, id_task,
                getIntent().getStringExtra("username_room"), getIntent().getStringExtra("id_rooms_tab"),
                foto, new OnPreviewItemClickListener() {
            @Override
            public void onItemClick(View view, String position, File item, String type, String idTaskDetail) {
                if (type.equalsIgnoreCase("before")) {
                    task_id = position + "";
                    Intent intent = new Intent(PushRepairReportActivity.this, ZoomImageViewActivity.class);
                    for (int i = 0; i < foto.size(); i++) {
                        if (foto.get(i).getId().equalsIgnoreCase(task_id)) {
                            intent.putExtra(ZoomImageViewActivity.KEY_FILE, foto.get(i).getBefore());
                        }
                    }
                    startActivity(intent);
                } else if (type.equalsIgnoreCase("after")) {
                    task_id = position + "";
                    CameraActivity.Builder start = new CameraActivity.Builder(PushRepairReportActivity.this, REQ_CAMERA);
                    start.setLockSwitch(CameraActivity.UNLOCK_SWITCH_CAMERA);
                    start.setCameraFace(CameraActivity.CAMERA_REAR);
                    start.setFlashMode(CameraActivity.FLASH_OFF);
                    start.setQuality(CameraActivity.MEDIUM);
                    start.setRatio(CameraActivity.RATIO_4_3);
                    start.setNIK(new UserDB(PushRepairReportActivity.this).getColValue(UserDB.EMPLOYEE_NIK));
                    start.setFileName(new MediaProcessingUtil().createFileName("jpeg", "ROOM"));
                    if (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    new Camera(start.build()).lauchCamera();
                } else {

                    task_id = position + "";
                    if (getTheDB(position, "fileupload").length() == 0) {

                        if (getTheDB(position, "comment").length() == 0) {
                            Toast.makeText(getApplicationContext(), "Mohon tambahkan Keterangan update yang terkait masalah tertera!1", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (type != null) {

                            rdialog = new ProgressDialog(PushRepairReportActivity.this);
                            rdialog.setMessage("Upload Photo...");
                            rdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            rdialog.setCancelable(false);
                            rdialog.show();

                            new UploadFileToServerCild().execute("https://forward.byonchat.com:37001/1_345171158admin/bc_voucher_client/webservice/proses/file_processing.php",
                                    getIntent().getStringExtra("username_room"),
                                    id_rooms_tab, id_task_list,
                                    type,
                                    position, idTaskDetail);

                        } else {
                            Toast.makeText(getApplicationContext(), "Mohon tambahkan foto update yang terkait masalah tertera!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    } else {
                        if (getTheDB(position, "comment").length() == 0) {
                            Toast.makeText(getApplicationContext(), "Mohon tambahkan Keterangan update yang terkait masalah tertera!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        rdialog = new ProgressDialog(PushRepairReportActivity.this);
                        rdialog.setMessage("Upload....");
                        rdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        rdialog.setCancelable(false);
                        rdialog.setIndeterminate(true);
                        rdialog.show();

                        JSONObject jsonObject1 = new JSONObject();
                        try {
                            jsonObject1.put("id_task", idTaskDetail);
                            jsonObject1.put("urutan", position.replace(idTaskDetail + "-", ""));
                            jsonObject1.put("ket", getTheDB(position, "comment"));
                            jsonObject1.put("foto", getTheDB(position, "fileupload"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(getApplicationContext(), jsonObject1.toString(), Toast.LENGTH_SHORT).show();


                        new UploadJSONSOn().execute("https://" + MessengerConnectionService.HTTP_SERVER + "/bc_voucher_client/webservice/category_tab/insert_tobe_repair_per_item.php",
                                getIntent().getStringExtra("username_room"), getIntent().getStringExtra("bc_user"),
                                idTaskDetail, jsonObject1.toString());

                    }
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
    }

    private String fileJson() {
        String stringdong = "";

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username_room", getIntent().getStringExtra("username_room"));
            jsonObject.put("task_id", id_task);
            jsonObject.put("title", name_title);

            JSONArray value_detail = new JSONArray();
            for (int i = 0; i < uploadfoto.size(); i++) {
                JSONObject jsonObjectA = new JSONObject();
                jsonObjectA.put("urutan", uploadfoto.get(i).getId());

                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("key", "before");
                jsonObject1.put("foto", uploadfoto.get(i).getBefore());
                jsonObject1.put("keterangan", uploadfoto.get(i).getTitle());

                JSONObject jsonObject2 = new JSONObject();
                jsonObject2.put("key", "after");
                jsonObject2.put("foto", uploadfoto.get(i).getAfterString());

                if (uploadfoto.get(i).getId().equalsIgnoreCase(uploadfoto.get(i).getId())) {
                    if (checkDB(uploadfoto.get(i).getId())) {
                        //  jsonObject2.put("ket", getTheDB(uploadfoto.get(i).getId()));
                    }
                }

                jsonArray.put(jsonObject1);
                jsonArray.put(jsonObject2);

                jsonObjectA.put("data", jsonArray);
                value_detail.put(jsonObjectA);
            }

            jsonObject.put("value_detail", value_detail);

            stringdong = jsonObject.toString();
        } catch (JSONException e) {

        }

        return stringdong;
    }

    private boolean checkDB(String id) {
        boolean isExist = false;

        SQLiteDatabase db = NoteDB.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME
                + " WHERE id_detail =?", new String[]{String.valueOf(id)});
        while (cursor.moveToNext()) {
            isExist = true;
        }
        cursor.close();
        return isExist;
    }

    private String getTheDB(String id, String kolom) {
        String isExist = "";

        SQLiteDatabase db = NoteDB.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME
                + " WHERE id_detail =?", new String[]{String.valueOf(id)});
        while (cursor.moveToNext()) {
            isExist = cursor.getString(cursor.getColumnIndex(kolom));
        }
        cursor.close();

        if (isExist == null) {
            isExist = "";
        }
        return isExist;
    }

    private void deleteFromDB(String id) {

        SQLiteDatabase db = NoteDB.getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_NAME
                + " WHERE id_detail =?", new String[]{String.valueOf(id)});

        db.close();
    }

    public void insertDBFoto(String id, String fileUrl) {
        SQLiteDatabase db = NoteDB.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID_DETAIL, id);
        if (fileUrl != null) {
            values.put(COLUMN_FILEUPLOAD, fileUrl);
        }
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void updateFoto(String id, String fileUrl) {
        SQLiteDatabase db = NoteDB.getWritableDatabase();

        ContentValues values = new ContentValues();
        if (fileUrl != null) {
            values.put(COLUMN_FILEUPLOAD, fileUrl);
        }

        db.update(TABLE_NAME, values, COLUMN_ID_DETAIL + " = ?",
                new String[]{String.valueOf(id)});
    }


    private class UploadFileToServerCild extends AsyncTask<String, Integer, String> {
        long totalSize = 0;
        String ii;
        String id;
        String idTaskDetail;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return uploadFile(params[0], params[1], params[2], params[3], params[4], params[5], params[6]);
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        @SuppressWarnings("deprecation")
        private String uploadFile(String URL, String username, String id_room, String id_list, String value, String ids, String _idTaskDetail) {
            String responseString = null;
            ii = value;
            id = ids;
            idTaskDetail = _idTaskDetail;

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

                java.io.File sourceFile = new java.io.File(resizeAndCompressImageBeforeSend(getApplicationContext(), ii, "fileUploadBC_" + new Date().getTime() + ".jpg"));

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
            try {
                JSONObject jsonObject = new JSONObject(result);
                String message = jsonObject.getString("message");
                if (message.length() == 0) {
                    String fileNameServer = jsonObject.getString("filename");

                    if (checkDB(id)) {
                        updateFoto(id, fileNameServer);
                    } else {
                        insertDBFoto(id, fileNameServer);
                    }


                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("id_task", idTaskDetail);
                    jsonObject1.put("urutan", id.replace(idTaskDetail + "-", ""));
                    jsonObject1.put("ket", getTheDB(id, "comment"));
                    jsonObject1.put("foto", getTheDB(id, "fileupload"));

                    new UploadJSONSOn().execute("https://" + MessengerConnectionService.HTTP_SERVER + "/bc_voucher_client/webservice/category_tab/insert_tobe_repair_per_item.php",
                            getIntent().getStringExtra("username_room"), getIntent().getStringExtra("bc_user"),
                            idTaskDetail, jsonObject1.toString());

                } else {
                    Toast.makeText(getApplicationContext(), "Failed Uploading Report", Toast.LENGTH_LONG).show();
                    rdialog.dismiss();
                    finish();
                }


            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Failed Uploading Report", Toast.LENGTH_LONG).show();
                rdialog.dismiss();
                finish();

            }
            super.onPostExecute(result);
        }
    }

    private class UploadJSONSOn extends AsyncTask<String, Integer, String> {
        long totalSize = 0;
        String idTaskDetail = "";

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
        private String uploadFile(String URL, String username, String bc_user, String _idTaskDetail, String jason) {
            idTaskDetail = _idTaskDetail;
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


                entity.addPart("username_room", new StringBody(username));
                entity.addPart("bc_user", new StringBody(bc_user));
                entity.addPart("id_rooms_tab", new StringBody("2613"));
                entity.addPart("json", new StringBody(jason));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                Log.w("moSak", statusCode + "");
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
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            rdialog.dismiss();
            if (result.contains("Error")) {
                Toast.makeText(getApplicationContext(), "terjadi kesalahan pada koneksi internet, harap ulangi beberapa saat lagi.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                finish();
                startActivity(getIntent());

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

    @Override
    protected void onPause() {
        super.onPause();
        toolbar.stopScan();
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.startScan("forward.byonchat.com", PushRepairReportActivity.this);
    }
}
