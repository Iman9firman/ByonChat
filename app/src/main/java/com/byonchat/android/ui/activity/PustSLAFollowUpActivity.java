/*package com.byonchat.android.ui.activity;

public class PustSLAFollowUpActivity {
}*/
package com.byonchat.android.ui.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.byonchat.android.R;
import com.byonchat.android.ZoomImageViewActivity;
import com.byonchat.android.data.model.File;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.model.Photo;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.provider.SLANoteDB;
import com.byonchat.android.ui.adapter.OnPreviewItemClickListener;
import com.byonchat.android.ui.adapter.OnRequestItemClickListener;
import com.byonchat.android.ui.view.ByonchatRecyclerView;
import com.byonchat.android.utils.AllAboutUploadTask;
import com.byonchat.android.utils.AndroidMultiPartEntity;
import com.byonchat.android.utils.MediaProcessingUtil;
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

import static com.byonchat.android.provider.SLANoteDB.TABLE_NAME;

public class PustSLAFollowUpActivity extends AppCompatActivity {
    String task_id, id_task, id_task_list, id_rooms_tab;
    String name_title;
    LinearLayout linearLayout;
    ByonchatRecyclerView vListData;
    ArrayList<Photo> foto = new ArrayList<>();
    ArrayList<Photo> uploadfoto = new ArrayList<>();
    PustReportRepairAdapter mAdapter;
    private static final int REQ_CAMERA = 1201;
    Button btnSubmit, btnCancel;
    ProgressDialog rdialog;
    //    private OnTaskCompleted taskCompleted;
//    ArrayList<String> prosesUpload = new ArrayList<>();
    Integer totalUpload = 0;
    BotListDB db;
    private String basejson;
    SLANoteDB NoteDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_repairment);

        db = BotListDB.getInstance(getApplicationContext());
        vListData = (ByonchatRecyclerView) findViewById(R.id.list_all);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        basejson = getIntent().getStringExtra("data");
        NoteDB = new SLANoteDB(getApplicationContext());

        resolveData();
        resolveListFile();
        resolveSend();
    }

    protected void resolveData() {
        Log.w("ini datanya apa cuk",getIntent().getStringExtra("data"));
        try {
            JSONObject gvcs = new JSONObject(getIntent().getStringExtra("data"));
            id_task = gvcs.getString("task_id");
            id_task_list = gvcs.getString("id_list_task");
            id_rooms_tab = gvcs.getString("id_rooms_tab_parent");
            name_title = gvcs.getString("title");
            JSONArray jar = gvcs.getJSONArray("value_detail");
            for (int i = 0; i < jar.length(); i++) {
                JSONObject first = jar.getJSONObject(i);
                JSONArray pembobotan = first.getJSONArray("pembobotan");
                for (int ii = 0; ii < pembobotan.length(); ii++) {
                    JSONObject second = pembobotan.getJSONObject(ii);
                    JSONArray section = second.getJSONArray("section");
                    for (int iii = 0; iii < section.length(); iii++) {
                        JSONObject third = section.getJSONObject(iii);
                        JSONArray subsection = third.getJSONArray("subsection");
                        for (int iv = 0; iv < subsection.length(); iv++) {
                            JSONObject fourth = subsection.getJSONObject(iv);
                            JSONArray pertanyaan = fourth.getJSONArray("pertanyaan");
                            for (int v = 0; v < pertanyaan.length(); v++) {
                                JSONObject fifth = pertanyaan.getJSONObject(v);
                                String valid = fifth.getString("v");
                                if(valid.equalsIgnoreCase("0")) {
                                    String id = fifth.getString("id");
                                    String fotony = fifth.getString("f");
                                    String title = fifth.getString("n");

                                    if (!fotony.contains("http://")) {
                                        fotony = "https://bb.byonchat.com/bc_voucher_client/images/list_task/" + fifth.getString("f");
                                    }

                                    Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(id_task, getIntent().getStringExtra("username_room"), getIntent().getStringExtra("id_rooms_tab"), "reportrepair", id);
                                    Photo fotonya = null;
                                    if (cursorCild.getCount() > 0) {
                                        java.io.File f = new java.io.File(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                                        fotonya = new Photo(id, title, fotony, f);
                                    }else {
                                        fotonya = new Photo(id, title, fotony, null);
                                    }
                                    foto.add(fotonya);
                                }/*else{
                                    String id = fifth.getString("id");
                                    String title = fifth.getString("n");
                                    String fotony = fifth.getString("f");

                                    if(fotony != null){
                                        if (!fotony.contains("http://")) {
                                            fotony = "https://bb.byonchat.com/bc_voucher_client/images/list_task/" + fifth.getString("f");
                                        }
                                    }

                                    Photo fotonya = new Photo(id, title, fotony, "",valid);
                                    foto.add(fotonya);
                                }*/
                            }
                        }
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

                        Bitmap result = MediaProcessingUtil.decodeSampledBitmapFromResourceMemOpt(inputStream, 800,
                                800);

                        RoomsDetail orderModel = new RoomsDetail(id_task, getIntent().getStringExtra("id_rooms_tab"), getIntent().getStringExtra("username_room"), f.toString(), task_id, null, "reportrepair");
                        db.insertRoomsDetail(orderModel);

                        for(int i = 0; i < foto.size();i++){
                            if(foto.get(i).getId().equalsIgnoreCase(task_id)){
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


        mAdapter = new PustReportRepairAdapter(this,id_task,
                getIntent().getStringExtra("username_room"), getIntent().getStringExtra("id_rooms_tab"),
                foto, new OnPreviewItemClickListener() {
            @Override
            public void onItemClick(View view, int position, File item, String type) {
                if(type.equalsIgnoreCase("before")){
                    task_id = position + "";
                    Intent intent = new Intent(PustSLAFollowUpActivity.this, ZoomImageViewActivity.class);
                    for(int i = 0; i< foto.size();i++){
                        if(foto.get(i).getId().equalsIgnoreCase(task_id)){
                            intent.putExtra(ZoomImageViewActivity.KEY_FILE, foto.get(i).getBefore());
                        }
                    }
                    startActivity(intent);
                }else if(type.equalsIgnoreCase("after")){
                    task_id = position + "";
                    CameraActivity.Builder start = new CameraActivity.Builder(PustSLAFollowUpActivity.this, REQ_CAMERA);
                    start.setLockSwitch(CameraActivity.UNLOCK_SWITCH_CAMERA);
                    start.setCameraFace(CameraActivity.CAMERA_REAR);
                    start.setFlashMode(CameraActivity.FLASH_OFF);
                    start.setQuality(CameraActivity.MEDIUM);
                    start.setRatio(CameraActivity.RATIO_4_3);
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
                }
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        vListData.setLayoutManager(mLayoutManager);
        vListData.setItemAnimator(new DefaultItemAnimator());
        vListData.setAdapter(mAdapter);
    }

    private void resolveSend(){
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Log.w("segituStart","bye");
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rdialog = new ProgressDialog(PustSLAFollowUpActivity.this);
                rdialog.setMessage("Loading...");
                rdialog.show();

                Log.w("ujuga ujuga ujuga",foto.size()+"");

                if(foto.size() == 0){
                    new UploadJSONSOn().execute("https://bb.byonchat.com/bc_voucher_client/webservice/category_tab/insert_sla.php",
                            getIntent().getStringExtra("username_room"),getIntent().getStringExtra("bc_user"),
                            getIntent().getStringExtra("id_rooms_tab"));
                }else {
                    for (int i = 0; i < foto.size(); i++) {
                        if (foto.get(i).getAfter() != null) {
                            new UploadFileToServerCild().execute("https://bb.byonchat.com/bc_voucher_client/webservice/proses/file_processing.php",
                                    getIntent().getStringExtra("username_room"),
                                    id_rooms_tab, id_task_list,
                                    foto.get(i).getAfter().toString(),
                                    foto.get(i).getId());
                        } else {
                            Toast.makeText(getApplicationContext(), "Mohon tambahkan foto update yang terkait masalah tertera!", Toast.LENGTH_SHORT).show();
                            rdialog.dismiss();
                        }
                    }
                }
            }
        });
    }

    private void getDetail(String Url, Map<String, String> params2, Boolean hide) {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest sr = new StringRequest(Request.Method.POST, Url,
                response -> {
                    rdialog.dismiss();
                    finish();
                    Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                    Log.w("Return push errorrr", response);

                },
                error -> {
                    Toast.makeText(getApplicationContext(),"Error found! Try Again",Toast.LENGTH_SHORT).show();
                    rdialog.dismiss();
                    Log.w("Return push errorrrrr2", error);
                }
        ) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return params2;
            }
        };
        queue.add(sr);
    }

    private String fileJson(){
        String stringdong = "";
        try {
            JSONObject gvcs = new JSONObject(basejson);
            JSONArray jar = gvcs.getJSONArray("value_detail");
            for (int i = 0; i < jar.length(); i++) {
                JSONObject first = jar.getJSONObject(i);
                JSONArray pembobotan = first.getJSONArray("pembobotan");
                for (int ii = 0; ii < pembobotan.length(); ii++) {
                    JSONObject second = pembobotan.getJSONObject(ii);
                    JSONArray section = second.getJSONArray("section");
                    for (int iii = 0; iii < section.length(); iii++) {
                        JSONObject third = section.getJSONObject(iii);
                        JSONArray subsection = third.getJSONArray("subsection");
                        for (int iv = 0; iv < subsection.length(); iv++) {
                            JSONObject fourth = subsection.getJSONObject(iv);
                            JSONArray pertanyaan = fourth.getJSONArray("pertanyaan");
                            for (int v = 0; v < pertanyaan.length(); v++) {
                                JSONObject fifth = pertanyaan.getJSONObject(v);
                                String id = fifth.getString("id");
                                for (int vi = 0; vi < uploadfoto.size();vi++) {
                                    Log.w("ujug3 ID", "id json : " + id + ", id photo : " + uploadfoto.get(vi).getId());
                                    if (uploadfoto.get(vi).getId().equalsIgnoreCase(id)) {
                                        Log.w("ujug2 nambah", id);
                                        fifth.put("a", uploadfoto.get(vi).getAfterString());
                                        if(checkDB(id)){
                                            fifth.put("ket",getTheDB(id));
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }

            stringdong = gvcs.toString();
        } catch (JSONException e) {
            Log.w("ujug ujug error", e.getMessage());
        }

        Log.w("Apa ujug ujug (gandi)",stringdong);
        return stringdong;
    }

    private void deleteNote(){
        try {
            JSONObject gvcs = new JSONObject(fileJson());
            JSONArray jar = gvcs.getJSONArray("value_detail");
            for (int i = 0; i < jar.length(); i++) {
                JSONObject first = jar.getJSONObject(i);
                JSONArray pembobotan = first.getJSONArray("pembobotan");
                for (int ii = 0; ii < pembobotan.length(); ii++) {
                    JSONObject second = pembobotan.getJSONObject(ii);
                    JSONArray section = second.getJSONArray("section");
                    for (int iii = 0; iii < section.length(); iii++) {
                        JSONObject third = section.getJSONObject(iii);
                        JSONArray subsection = third.getJSONArray("subsection");
                        for (int iv = 0; iv < subsection.length(); iv++) {
                            JSONObject fourth = subsection.getJSONObject(iv);
                            JSONArray pertanyaan = fourth.getJSONArray("pertanyaan");
                            for (int v = 0; v < pertanyaan.length(); v++) {
                                JSONObject fifth = pertanyaan.getJSONObject(v);
                                String id = fifth.getString("id");
                                for (int vi = 0; vi < uploadfoto.size();vi++) {
                                    if (uploadfoto.get(vi).getId().equalsIgnoreCase(id)) {
                                        Log.w("ujug2 nambah", id);
                                        fifth.put("a", uploadfoto.get(vi).getAfterString());
                                        if(checkDB(id)){
                                            deleteFromDB(id);
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }

        } catch (JSONException e) {
            Log.w("ujug ujug error", e.getMessage());
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

    private String getTheDB(String id) {
        String isExist = "";

        SQLiteDatabase db = NoteDB.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME
                + " WHERE id_detail =?", new String[]{String.valueOf(id)});
        while (cursor.moveToNext()) {
            isExist = cursor.getString(cursor.getColumnIndex("comment"));
        }
        cursor.close();
        return isExist;
    }

    private void deleteFromDB(String id) {

        SQLiteDatabase db = NoteDB.getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_NAME
                + " WHERE id_detail =?", new String[]{String.valueOf(id)});

        db.close();
    }

    private void deleAALLromDB() {

        SQLiteDatabase db = NoteDB.getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_NAME);

        db.close();
    }

    private class UploadFileToServerCild extends AsyncTask<String, Integer, String> {
        long totalSize = 0;
        String ii;
        String id;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.w("segituStart","de");
        }

        @Override
        protected String doInBackground(String... params) {
            return uploadFile(params[0], params[1], params[2], params[3], params[4], params[5]);
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        @SuppressWarnings("deprecation")
        private String uploadFile(String URL, String username, String id_room, String id_list, String value, String ids) {
            String responseString = null;
            ii = value;
            id = ids;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(URL);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                Log.w("segitu",(int) ((num / (float) totalSize) * 100)+"");
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
            Log.w("segitu@@",result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                String message = jsonObject.getString("message");
                if (message.length() == 0) {
                    Log.w("segitu@1@",result);
                    String fileNameServer = jsonObject.getString("filename");
                    String filePhott = "https://bb.byonchat.com/bc_voucher_client/images/list_task/"+fileNameServer;

                    Log.w("11111 errorre 1",fileNameServer);
                    for (int i = 0; i < foto.size(); i++){
                        if(foto.get(i).getId().equalsIgnoreCase(id)){
                            Photo fotonya = new Photo(foto.get(i).getId(), foto.get(i).getTitle(),foto.get(i).getBefore(), foto.get(i).getAfter(), filePhott);
                            uploadfoto.add(fotonya);
                        }
                    }


                } else {
                    Log.w("segitu@2@",result);
                }

                if(foto.size() == uploadfoto.size()){
                    Log.w("segitu@3@",result);
//                    Map<String, String> params = new HashMap<>();
//                    params.put("username_room",  getIntent().getStringExtra("username_room"));
//                    params.put("bc_user",  getIntent().getStringExtra("bc_user"));
//                    params.put("id_rooms_tab",  getIntent().getStringExtra("id_rooms_tab"));
//                    params.put("json",  fileJson());
//                    Log.w("buifder uewufg",getIntent().getStringExtra("id_rooms_tab")+", "+ getIntent().getStringExtra("username_room"));
//                    Log.w("nreoirgn errorre egbh",fileJson());
//                    getDetail("https://bb.byonchat.com/bc_voucher_client/webservice/category_tab/insert_tobe_repair.php",params,true);
                    new UploadJSONSOn().execute("https://bb.byonchat.com/bc_voucher_client/webservice/category_tab/insert_sla.php",
                            getIntent().getStringExtra("username_room"),getIntent().getStringExtra("bc_user"),
                            getIntent().getStringExtra("id_rooms_tab"));

//                   rdialog.dismiss();
                    Log.w("pasukan ujug ujug",fileJson());
//                   Toast.makeText(PustSLAFollowUpActivity.this,fileJson() ,Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
            super.onPostExecute(result);
        }
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

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(URL);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                Log.w("segitu",(int) ((num / (float) totalSize) * 100)+"");
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
                entity.addPart("json", new FileBody(gpxfile, contentType, gpxfile.getName()));

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
            deleteNote();
            Toast.makeText(getApplicationContext(),"Success Uploading Report",Toast.LENGTH_LONG).show();
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
}
