package com.byonchat.android.ui.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.byonchat.android.DownloadSqliteDinamicActivity;
import com.byonchat.android.ISSActivity.LoginDB.UserDB;
import com.byonchat.android.R;
import com.byonchat.android.ZoomImageViewActivity;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.createMeme.FilteringImage;
import com.byonchat.android.data.model.File;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.local.Byonchat;
import com.byonchat.android.model.SLAmodelNew;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.DataBaseDropDown;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.provider.SLANoteDB;
import com.byonchat.android.ui.adapter.OnPreviewItemClickListener;
import com.byonchat.android.ui.adapter.OnRequestItemClickListener;
import com.byonchat.android.ui.view.ByonchatRecyclerView;
import com.byonchat.android.utils.AllAboutUploadTask;
import com.byonchat.android.utils.AndroidMultiPartEntity;
import com.byonchat.android.utils.GetDropdownFormISS;
import com.byonchat.android.utils.HttpHelper;
import com.byonchat.android.utils.MediaProcessingUtil;
import com.byonchat.android.widget.ToolbarWithIndicator;
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

public class PustSLAFollowUpActivity extends AppCompatActivity {
    String task_id, id_taskd, id_task_list, id_rooms_tab;
    String kode_jjt;
    String name_title;
    LinearLayout linearLayout;
    ByonchatRecyclerView vListData;
    ArrayList<SLAmodelNew> foto = new ArrayList<>();
    ArrayList<SLAmodelNew> uploadfoto = new ArrayList<>();
    PustReportRepairAdapter mAdapter;
    private static final int REQ_CAMERA = 1201;
    Button btnSubmit, btnCancel;
    ProgressDialog rdialog;
    Integer totalUpload = 0;
    BotListDB db;
    private String basejson;
    SLANoteDB NoteDB;

    ToolbarWithIndicator toolbar;

    // TODO: 2019-05-18 Lg genereate json bener apa engga
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

        basejson = getIntent().getStringExtra("data");

        NoteDB = new SLANoteDB(getApplicationContext());

        TextView textTitle = (TextView) findViewById(R.id.title_arr);
        TextView textSubtitle = (TextView) findViewById(R.id.subtitle_arr);
        String ttl = getIntent().getStringExtra("title");
        String sbttl = getIntent().getStringExtra("subtitle");
        textSubtitle.setText(sbttl);
        textTitle.setText(ttl);

        jalanJalan();

    }

    private void jalanJalan() {
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
            kode_jjt = gvcs.getString("kode_jjt");
            id_task_list = gvcs.getString("id_list_task");
            id_rooms_tab = gvcs.getString("id_rooms_tab");
            name_title = gvcs.getString("title");
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
            String noEmpat = "";

            for (int i = 0; i < jar.length(); i++) {
                JSONObject first = jar.getJSONObject(i);
                JSONObject pembobotan = first.getJSONObject("pembobotan");

                idSection = pembobotan.getString("id");
                JSONObject section = pembobotan.getJSONObject("section");

                idSubSection = section.getString("id");
                String asiop2[] = {"title"};
                headerTwo = getNameByIdSLA("section", kode_jjt, idSubSection);
                JSONObject subsection = section.getJSONObject("subsection");

                JSONArray pertanyaan = subsection.getJSONArray("pertanyaan");

                for (int v = 0; v < pertanyaan.length(); v++) {
                    JSONObject fifth = pertanyaan.getJSONObject(v);
                    String valid = fifth.getString("v");

                    noEmpat = String.valueOf(v + 1);
                    if (valid.equalsIgnoreCase("0")) {
                        String asiop4[] = {"pertanyaan"};


                        idPertanyaan = fifth.getString("id");
                        String id_task = fifth.getString("id_task");
                        String fotony = fifth.getString("f");
                        String title = fifth.getString("n");

                        headerFour = getNameByIdSLA("pertanyaan", kode_jjt, idPertanyaan);

                        idItem = id_task + "-" + v;

                        if (!fotony.contains("http://")) {
                            fotony = "https://bb.byonchat.com/bc_voucher_client/images/list_task/" + fifth.getString("f");
                        }

                        String id = idSection + "-" + idSubSection + "-" + idPertanyaan + "-" + idItem;
                        String header = headerTwo + " - " + headerFour;

                        Log.w("HSIAIKS1AA", id);
                        Log.w("HSIAIKS2BB", id_task);

                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(id_task, getIntent().getStringExtra("username_room"), getIntent().getStringExtra("id_rooms_tab"), "reportrepair", id);
                        SLAmodelNew fotonya = null;
                        if (cursorCild.getCount() > 0) {
                            java.io.File f = new java.io.File(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                            fotonya = new SLAmodelNew(id_task, noSatu + "." + noDua + "." + noTiga + "." + noEmpat + ". " + header, id, title, fotony, f);
                        } else {
                            fotonya = new SLAmodelNew(id_task, noSatu + "." + noDua + "." + noTiga + "." + noEmpat + ". " + header, id, title, fotony, (java.io.File) null);
                        }
                        foto.add(fotonya);
                    }
                }
            }
        } catch (JSONException e) {
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

                        MediaProcessingUtil.decodeSampledBitmapFromResourceMemOpt(inputStream, 800, 800);


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


    protected void resolveListFile() {

        mAdapter = new PustReportRepairAdapter(this, "",
                getIntent().getStringExtra("username_room"), getIntent().getStringExtra("id_rooms_tab"),
                foto, new OnPreviewItemClickListener() {
            @Override
            public void onItemClick(View view, String position, File item, String type, String idTaskDetail) {
                if (type.equalsIgnoreCase("before")) {
                    task_id = position;
                    Intent intent = new Intent(PustSLAFollowUpActivity.this, ZoomImageViewActivity.class);
                    for (int i = 0; i < foto.size(); i++) {
                        if (foto.get(i).getId().equalsIgnoreCase(task_id)) {
                            intent.putExtra(ZoomImageViewActivity.KEY_FILE, foto.get(i).getBefore());
                        }
                    }
                    startActivity(intent);
                } else if (type.equalsIgnoreCase("after")) {
                    task_id = position;
                    CameraActivity.Builder start = new CameraActivity.Builder(PustSLAFollowUpActivity.this, REQ_CAMERA);
                    start.setLockSwitch(CameraActivity.UNLOCK_SWITCH_CAMERA);
                    start.setCameraFace(CameraActivity.CAMERA_REAR);
                    start.setFlashMode(CameraActivity.FLASH_OFF);
                    start.setNIK(new UserDB(PustSLAFollowUpActivity.this).getColValue(UserDB.EMPLOYEE_NIK));
                    start.setQuality(CameraActivity.MEDIUM);
                    start.setRatio(CameraActivity.RATIO_4_3);
                    start.setFileName(new MediaProcessingUtil().createFileName("jpeg", "ROOM"));
                    if (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    new Camera(start.build()).lauchCamera();
                } else {


                    if (getTheDB(position, "fileupload").length() == 0) {

                        if (getTheDB(position, "comment").length() == 0) {
                            Toast.makeText(getApplicationContext(), "Mohon tambahkan Keterangan update yang terkait masalah tertera!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (type != null) {

                            rdialog = new ProgressDialog(PustSLAFollowUpActivity.this);
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

                        String jsonContentItem = jsonUploadItem(idTaskDetail, position);
                        if (jsonContentItem.length() == 0) {
                            Toast.makeText(getApplicationContext(), "Mohon cek foto dan keterangan apakah sesuai?", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        rdialog = new ProgressDialog(PustSLAFollowUpActivity.this);
                        rdialog.setMessage("Upload....");
                        rdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        rdialog.setCancelable(false);
                        rdialog.setIndeterminate(true);
                        rdialog.show();
                        new UploadJSONSOn().execute("https://" + MessengerConnectionService.HTTP_SERVER + "/bc_voucher_client/webservice/category_tab/insert_sla_per_item.php",
                                getIntent().getStringExtra("username_room"), getIntent().getStringExtra("bc_user"),
                                idTaskDetail, jsonContentItem);


                    }


                }
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        vListData.setLayoutManager(mLayoutManager);
        vListData.setItemAnimator(new DefaultItemAnimator());
        vListData.setAdapter(mAdapter);
    }

    private String jsonUploadItem(String idTaskDetail, String id) {
        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("id_task", idTaskDetail);
            jsonObject1.put("ket", getTheDB(id, "comment"));
            jsonObject1.put("a", getTheDB(id, "fileupload"));
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }

        return jsonObject1.toString();
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
                rdialog = new ProgressDialog(PustSLAFollowUpActivity.this);
                rdialog.setMessage("Loading...");
                rdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                rdialog.setIndeterminate(true);
                rdialog.show();

                if (foto.size() == 0) {
                    new UploadJSONSOn().execute("https://" + MessengerConnectionService.HTTP_SERVER + "/bc_voucher_client/webservice/category_tab/insert_sla_new.php",
                            getIntent().getStringExtra("username_room"), getIntent().getStringExtra("bc_user"),
                            getIntent().getStringExtra("id_rooms_tab"));
                } else {
                    for (int ii = 0; ii < foto.size(); ii++) {
                        if (foto.get(ii).getAfter() == null) {
                            Toast.makeText(getApplicationContext(), "Mohon tambahkan foto update yang terkait masalah tertera!", Toast.LENGTH_SHORT).show();
                            rdialog.dismiss();
                            return;
                        }

                        if (getTheDB(foto.get(ii).getId(), "comment").length() == 0) {
                            Toast.makeText(getApplicationContext(), "Mohon tambahkan Keterangan update yang terkait masalah tertera!", Toast.LENGTH_SHORT).show();
                            rdialog.dismiss();
                            return;
                        }

                    }

                    for (int i = 0; i < foto.size(); i++) {
                        if (foto.get(i).getAfter() != null) {
                            new UploadFileToServerCild().execute("https://forward.byonchat.com:37001/1_345171158admin/bc_voucher_client/webservice/proses/file_processing.php",
                                    getIntent().getStringExtra("username_room"),
                                    id_rooms_tab, id_task_list,
                                    foto.get(i).getAfter().toString(),
                                    removePosFromId(foto.get(i).getId()));
                        } else {
                            Toast.makeText(getApplicationContext(), "Mohon tambahkan foto update yang terkait masalah tertera!", Toast.LENGTH_SHORT).show();
                            rdialog.dismiss();
                        }
                    }

                }
            }
        });
    }

    private String fileJson(String id_taskd) {
        String stringdong = "";
        try {
            JSONObject gvcs = new JSONObject(basejson);
            Log.w("Nangkringbocah1", gvcs.toString());
            JSONArray jar = gvcs.getJSONArray("value_detail");
            String idSection = "";
            String idSubSection = "";
            String idPertanyaan = "";

            for (int i = 0; i < jar.length(); i++) {
                JSONObject first = jar.getJSONObject(i);

                JSONObject pembobotan = first.getJSONObject("pembobotan");
                idSection = pembobotan.getString("id");

                JSONObject section = pembobotan.getJSONObject("section");
                idSubSection = section.getString("id");

                JSONObject subsection = section.getJSONObject("subsection");

                JSONArray pertanyaan = subsection.getJSONArray("pertanyaan");

                for (int ia = 0; ia < pertanyaan.length(); ia++) {
                    idPertanyaan = pertanyaan.getJSONObject(ia).getString("id");
                    String pp = pertanyaan.getJSONObject(ia).getString("id_task");
                    if (pp.equalsIgnoreCase(id_taskd)) {
                        pertanyaan.remove(ia);
                        String id_text = idSection + "-" + idSubSection + "-" + idPertanyaan + "-" + id_taskd + "-" + ia;

                        if (checkDB(id_text)) {
                            deleteFromDB(id_text);
                        }

                        Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(id_taskd, getIntent().getStringExtra("username_room"), getIntent().getStringExtra("id_rooms_tab"), "reportrepair", id_text);
                        if (cursorCild.getCount() > 0) {
                            java.io.File f = new java.io.File(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                            f.delete();
                        }

                        db.deleteNoteSLA(id_taskd, getIntent().getStringExtra("username_room"), getIntent().getStringExtra("id_rooms_tab"), "reportrepair", id_text);

                    }
                }
                if (pertanyaan.length() == 0) {
                    return "done";
                }

            }
            stringdong = gvcs.toString();
        } catch (JSONException e) {
        }
        return stringdong;
    }

    private void deleteNote() {
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
                JSONObject section = pembobotan.getJSONObject("section");

                idSubSection = section.getString("id");
                JSONObject subsection = section.getJSONObject("subsection");

                idPertanyaan = subsection.getString("id");
                JSONArray pertanyaan = subsection.getJSONArray("pertanyaan");

                for (int v = 0; v < pertanyaan.length(); v++) {
                    JSONObject fifth = pertanyaan.getJSONObject(v);
                    idItem = fifth.getString("id");
                    String id = idSection + "-" + idSubSection + "-" + idPertanyaan + "-" + idItem;
                    String id_text = idSection + "-" + idSubSection + "-" + idPertanyaan + "-" + idItem + "-" + v;
                    for (int vi = 0; vi < uploadfoto.size(); vi++) {
                        if (uploadfoto.get(vi).getId().equalsIgnoreCase(id)) {
                            fifth.put("a", uploadfoto.get(vi).getAfterString());
                            if (checkDB(id_text)) {
                                deleteFromDB(id_text);
                                // db.deleteNoteSLA(uploadfoto.get(vi).getId_task(), getIntent().getStringExtra("id_rooms_tab"), id, "reportrepair");
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
        }
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
            Log.w("kabalero33", progress[0] + "");
            rdialog.setProgress(progress[0]);
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
            Log.w("kabalero44", result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                String message = jsonObject.getString("message");
                Log.w("kabalero", message);

                if (message.length() == 0) {
                    String fileNameServer = jsonObject.getString("filename");
                    if (checkDB(id)) {
                        updateFoto(id, fileNameServer);
                    } else {
                        insertDBFoto(id, fileNameServer);
                    }

                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("id_task", idTaskDetail);
                    jsonObject1.put("ket", getTheDB(id, "comment"));
                    jsonObject1.put("a", getTheDB(id, "fileupload"));

                    new UploadJSONSOn().execute("https://" + MessengerConnectionService.HTTP_SERVER + "/bc_voucher_client/webservice/category_tab/insert_sla_per_item.php",
                            getIntent().getStringExtra("username_room"), getIntent().getStringExtra("bc_user"),
                            idTaskDetail, jsonObject1.toString());

                } else {
                    Toast.makeText(getApplicationContext(), "terjadi kesalahan pada koneksi internet, harap ulangi beberapa saat lagi.", Toast.LENGTH_LONG).show();
                    //terjadi kesalahan harap ulangi beberapa saat lagi
                    rdialog.dismiss();
                }


            } catch (JSONException e) {
                e.printStackTrace();
                Log.w("kabalero77", e.getMessage());
                Toast.makeText(getApplicationContext(), "terjadi kesalahan pada koneksi internet, harap ulangi beberapa saat lagi..", Toast.LENGTH_LONG).show();
                rdialog.dismiss();
            }
            super.onPostExecute(result);
        }
    }

    private class UploadJSONSOn extends AsyncTask<String, Integer, String> {
        long totalSize = 0;
        String idTaskDetail;

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
        private String uploadFile(String URL, String username, String bc_user, String id_room, String fileJson) {
            String responseString = null;
            idTaskDetail = id_room;

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
                entity.addPart("kode_jjt", new StringBody(kode_jjt));
                entity.addPart("json", new StringBody(fileJson));

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
            rdialog.dismiss();
            if (result.contains("Error")) {
                Toast.makeText(getApplicationContext(), "terjadi kesalahan pada koneksi internet, harap ulangi beberapa saat lagi.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Success Uploading", Toast.LENGTH_LONG).show();
                basejson = fileJson(idTaskDetail);
                if (basejson.equalsIgnoreCase("done")) {
                    finish();
                } else {
                    finish();
                    Intent iii = new Intent(PustSLAFollowUpActivity.this, PustSLAFollowUpActivity.class);
                    iii.putExtra("data", basejson);
                    iii.putExtra("username_room", getIntent().getStringExtra("username_room"));
                    iii.putExtra("bc_user", getIntent().getStringExtra("bc_user"));
                    iii.putExtra("id_rooms_tab", getIntent().getStringExtra("id_rooms_tab"));
                    iii.putExtra("title", getIntent().getStringExtra("title"));
                    iii.putExtra("subtitle", getIntent().getStringExtra("subtitle"));
                    startActivity(iii);

                }
            }


            super.onPostExecute(result);
        }
    }

    public static String resizeAndCompressImageBeforeSend(Context context, String filePath, String fileName) {
        final int MAX_IMAGE_SIZE = 100 * 1024; // max final file size in kilobytes
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(filePath, options);
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

    private String removePosFromId(String idWithPosition) {
        try {
            return idWithPosition.substring(0, idWithPosition.lastIndexOf("-"));
        } catch (Exception e) {
        }
        return idWithPosition;

    }

    @Override
    protected void onPause() {
        super.onPause();
        toolbar.stopScan();
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.startScan("forward.byonchat.com", PustSLAFollowUpActivity.this);
    }
}

