package com.byonchat.android.ui.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.byonchat.android.DownloadSqliteDinamicActivity;
import com.byonchat.android.R;
import com.byonchat.android.ZoomImageViewActivity;
import com.byonchat.android.data.model.File;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.model.Photo;
import com.byonchat.android.model.SLAmodelNew;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.DataBaseDropDown;
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

public class PushSLAVerificationActivity extends AppCompatActivity {
    String task_id, id_task, id_task_list, id_rooms_tab;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_repairment);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        db = BotListDB.getInstance(getApplicationContext());
        vListData = (ByonchatRecyclerView) findViewById(R.id.list_all);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        basejson = getIntent().getStringExtra("data");

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
            id_task = gvcs.getString("task_id");
            id_task_list = gvcs.getString("id_list_task");
            id_rooms_tab = gvcs.getString("id_rooms_tab_parent");
            name_title = gvcs.getString("title");

            JSONArray jar = gvcs.getJSONArray("value_detail");
            String idSection = "";
            String idSubSection = "";
            String idPertanyaan = "";
            String idItem = "";

            String headerTwo = "";
            String headerFour = "";

            String noSatu = "";
            String noDua = "";
            String noTiga = "";
            String noEmpat = "";

            for (int i = 0; i < jar.length(); i++) {
                JSONObject first = jar.getJSONObject(i);
                JSONArray pembobotan = first.getJSONArray("pembobotan");
                for (int ii = 0; ii < pembobotan.length(); ii++) {
                    JSONObject second = pembobotan.getJSONObject(ii);
                    JSONArray section = second.getJSONArray("section");
                    idSection = second.getString("id");
                    noSatu = String.valueOf(ii + 1);
                    for (int iii = 0; iii < section.length(); iii++) {
                        JSONObject third = section.getJSONObject(iii);
                        JSONArray subsection = third.getJSONArray("subsection");
                        idSubSection = third.getString("id");
                        String asiop2[] = {"title"};
                        headerTwo = getNameByIdSLA("section", asiop2, idSubSection);
                        noDua = String.valueOf(iii + 1);
                        for (int iv = 0; iv < subsection.length(); iv++) {
                            JSONObject fourth = subsection.getJSONObject(iv);
                            JSONArray pertanyaan = fourth.getJSONArray("pertanyaan");
                            idPertanyaan = fourth.getString("id");
                            noTiga = String.valueOf(iv + 1);
                            for (int v = 0; v < pertanyaan.length(); v++) {
                                JSONObject fifth = pertanyaan.getJSONObject(v);
                                idItem = fifth.getString("id");
                                String valid = fifth.getString("v");
                                if (valid.equalsIgnoreCase("0")) {
                                    String id = idSection + "-" + idSubSection + "-" + idPertanyaan + "-" + idItem;

                                    noEmpat = String.valueOf(v + 1);
                                    String asiop4[] = {"pertanyaan"};
                                    headerFour = getNameByIdSLA("pertanyaan", asiop4, idItem);

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

                                    String header = noSatu + "." + noDua + "." + noTiga + "." + noEmpat + ". " + headerTwo + " - " + headerFour;

                                    SLAmodelNew fotonya = new SLAmodelNew(header, id, title, fotony, aftera, valid, ket);
                                    foto.add(fotonya);
                                }
                            }
                        }
                    }
                }
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


        mAdapter = new PushSLAVerificationAdapter(getApplication(), id_task,
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
                rdialog = new ProgressDialog(PushSLAVerificationActivity.this);
                rdialog.setMessage("Loading...");
                rdialog.show();
                new UploadJSONSOn().execute("https://bb.byonchat.com/bc_voucher_client/webservice/category_tab/insert_verifikasi_sla.php",
                        getIntent().getStringExtra("username_room"), getIntent().getStringExtra("bc_user"),
                        getIntent().getStringExtra("id_rooms_tab"));

            }
        });
    }

    private String fileJson() {
        String stringdong = "";

        try {
            JSONObject gvcs = new JSONObject(basejson);
            id_task = gvcs.getString("task_id");
            id_task_list = gvcs.getString("id_list_task");
            id_rooms_tab = gvcs.getString("id_rooms_tab_parent");
            name_title = gvcs.getString("title");

            JSONArray jar = gvcs.getJSONArray("value_detail");

            String idSection = "";
            String idSubSection = "";
            String idPertanyaan = "";
            String idItem = "";

            for (int i = 0; i < jar.length(); i++) {
                JSONObject first = jar.getJSONObject(i);
                JSONArray pembobotan = first.getJSONArray("pembobotan");
                for (int ii = 0; ii < pembobotan.length(); ii++) {
                    JSONObject second = pembobotan.getJSONObject(ii);
                    JSONArray section = second.getJSONArray("section");
                    idSection = second.getString("id");
                    for (int iii = 0; iii < section.length(); iii++) {
                        JSONObject third = section.getJSONObject(iii);
                        JSONArray subsection = third.getJSONArray("subsection");
                        idSubSection = third.getString("id");
                        for (int iv = 0; iv < subsection.length(); iv++) {
                            JSONObject fourth = subsection.getJSONObject(iv);
                            JSONArray pertanyaan = fourth.getJSONArray("pertanyaan");
                            idPertanyaan = fourth.getString("id");
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
                        }
                    }
                }
            }

            stringdong = gvcs.toString();
        } catch (JSONException e) {
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

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(URL);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                Log.w("segitu", (int) ((num / (float) totalSize) * 100) + "");
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

}