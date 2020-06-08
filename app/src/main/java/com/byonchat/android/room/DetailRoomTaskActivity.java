package com.byonchat.android.room;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.provider.ContentRoom;
import com.byonchat.android.provider.ContentRoomDB;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.utils.AndroidMultiPartEntity;
import com.byonchat.android.utils.GPSTracker;
import com.byonchat.android.utils.HttpHelper;
import com.byonchat.android.utils.MediaProcessingUtil;
import com.byonchat.android.utils.TouchImageView;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailRoomTaskActivity extends AppCompatActivity {

    TouchImageView photo;
    Button btnPhoto;
    TextView titleText;
    TextView descText;
    TextView more;
    EditText textMessage;
    private static final int REQ_CAMERA = 1201;
    String cameraFileOutput;
    String status;
    String date;
    Bitmap result = null;
    final Context context = this;
    GPSTracker gps;
    String idTask;
    ContentRoomDB  contentRoomDB;
    private MessengerDatabaseHelper dbhelper;
    public static final SimpleDateFormat hourFormat = new SimpleDateFormat(
            "yyyy-MM-dd hh:mm:ss", Locale.getDefault());
    double latitude,longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_room_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("TASK DETAIL");
        idTask =  getIntent().getStringExtra("idTask");

        descText = (TextView)findViewById(R.id.descText);
        titleText = (TextView)findViewById(R.id.titleText);
        more = (TextView)findViewById(R.id.more);
        btnPhoto = (Button)findViewById(R.id.btnPhoto);
        textMessage = (EditText) findViewById(R.id.textMessage);

        photo = (TouchImageView)findViewById(R.id.photo);
        photo.setMaxZoom(4);

        if (dbhelper == null) {
            dbhelper = MessengerDatabaseHelper.getInstance(getApplicationContext());
        }

        if (contentRoomDB == null) {
            contentRoomDB = new ContentRoomDB(getApplicationContext());
        }


        contentRoomDB.open();

        Cursor cursor = contentRoomDB.getContentRoom(idTask);
        if(cursor.getCount()>0) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow(contentRoomDB.CONTENTROOM_TITTLE));
            date = cursor.getString(cursor.getColumnIndexOrThrow(contentRoomDB.CONTENTROOM_TIME));
            status = cursor.getString(cursor.getColumnIndexOrThrow(contentRoomDB.CONTENTROOM_STATUS));
            String attach = cursor.getString(cursor.getColumnIndexOrThrow(contentRoomDB.CONTENTROOM_ATTACH));
            String content = cursor.getString(cursor.getColumnIndexOrThrow(contentRoomDB.CONTENTROOM_CONTENT));
            String[] isi = content.split("<:>");
            titleText.setText(title);
            if (isi.length>1){
                String longLat[] = isi[0].split(",");
                descText.setText("Lat : " +longLat[1] + "," + "Long : " + longLat[0]);
                longitude = Double.parseDouble(longLat[0]);
                latitude = Double.parseDouble(longLat[1]);

                if (isi.length==2){
                    textMessage.setText(isi[1]);
                }
            }

            if (attach!=""){
                btnPhoto.setVisibility(View.GONE);
                cameraFileOutput = attach;
                final File f = new File(cameraFileOutput);
                if(f.exists()){
                    FileInputStream inputStream = null;
                    try {
                        inputStream = new FileInputStream(f);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    result = MediaProcessingUtil.decodeSampledBitmapFromResourceMemOpt(inputStream, 800,
                            800);
                    photo.setImageBitmap(result);
                }
            }
        }
        contentRoomDB.close();


        if(result==null){
            gps = new GPSTracker(DetailRoomTaskActivity.this);
            if (!gps.canGetLocation()) {
                btnPhoto.setVisibility(View.VISIBLE);
                gps.showSettingsAlert();
            }else{
                String action = Intent.ACTION_GET_CONTENT;
                Intent i = new Intent();
                action = MediaStore.ACTION_IMAGE_CAPTURE;
                File f = MediaProcessingUtil
                        .getOutputFile("jpeg");
                cameraFileOutput = f.getAbsolutePath();
                i.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(f));
                i.setAction(action);
                startActivityForResult(i, REQ_CAMERA);
            }



        }


        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gps = new GPSTracker(DetailRoomTaskActivity.this);
                if (!gps.canGetLocation()) {
                    gps.showSettingsAlert();
                }else{
                    String action = Intent.ACTION_GET_CONTENT;
                    Intent i = new Intent();
                    action = MediaStore.ACTION_IMAGE_CAPTURE;
                    File f = MediaProcessingUtil
                            .getOutputFile("jpeg");
                    cameraFileOutput = f.getAbsolutePath();
                    i.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(f));
                    i.setAction(action);
                    startActivityForResult(i, REQ_CAMERA);
                }
            }
        });

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlert("TASK DETAIL",titleText.getText().toString()+"\n"+descText.getText().toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_upload_menu, menu);
        configureActionItem(menu);
        return true;
    }

    private void configureActionItem(Menu menu) {

            MenuItem item = menu.findItem(R.id.menu_action_upload);
        if (status.equalsIgnoreCase("done")) {
            item.setVisible(false);
        }
            Button btn = (Button) MenuItemCompat.getActionView(item).findViewById(
                    R.id.buttonAbSave);
            btn.setBackgroundColor(Color.TRANSPARENT);
            btn.setTypeface(null, Typeface.BOLD);
            btn.setText("Upload");
            btn.setTextColor(Color.WHITE);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (result != null) {
                        contentRoomDB.open();
                        Date date = new Date();
                        contentRoomDB.updateDetail(new ContentRoom(Long.parseLong(idTask), hourFormat.format(date).toString(), longitude + "," + latitude+"<:>"+textMessage.getText().toString(), cameraFileOutput, "Send", "1"));
                        contentRoomDB.close();
                        if (isNetworkConnectionAvailable()) {
                            new UploadFileToServer().execute();
                        } else {
                            sendSMSMessage();
                            finish();
                            Toast.makeText(getApplicationContext(), "No internet connection, SMS send", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "No photo, please take a photo", Toast.LENGTH_SHORT).show();
                    }

                }
            });
    }

    protected void sendSMSMessage() {
        String phoneNo = "+6281283418812";
        String message = "uadh8qhek98e13r"+longitude+"naiousoduqj,"+latitude+"a"+date;

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
        }
        catch (Exception e) {
            Toast.makeText(context, "SMS faild, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id ==  android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private boolean isNetworkConnectionAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null)
            return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( requestCode == REQ_CAMERA) {
            if ( resultCode == RESULT_OK) {
                btnPhoto.setVisibility(View.GONE);
                if(decodeFile(cameraFileOutput)){
                    final File f = new File(cameraFileOutput);
                    if(f.exists()){
                        FileInputStream inputStream = null;
                        try {
                            inputStream = new FileInputStream(f);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        result = MediaProcessingUtil.decodeSampledBitmapFromResourceMemOpt(inputStream, 800,
                                800);
                        photo.setImageBitmap(result);
                        getgps();
                    }

                }
            } else if ( resultCode == RESULT_CANCELED) {
                if(result==null){
                    btnPhoto.setVisibility(View.VISIBLE);
                }
                Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
            } else {
                if(result==null){
                    btnPhoto.setVisibility(View.VISIBLE);
                }
                Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getgps(){
        gps = new GPSTracker(DetailRoomTaskActivity.this);
        if(gps.canGetLocation()) {
             latitude = gps.getLatitude();
             longitude = gps.getLongitude();
            if (latitude == 0.0 && longitude == 0.0) {
                String action = Intent.ACTION_GET_CONTENT;
                Intent i = new Intent();
                action = MediaStore.ACTION_IMAGE_CAPTURE;
                File f = MediaProcessingUtil
                        .getOutputFile("jpeg");
                cameraFileOutput = f.getAbsolutePath();
                i.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(f));
                i.setAction(action);
                startActivityForResult(i, REQ_CAMERA);
                Toast.makeText(context, "Harap coba kembali dalam waktu 1 menit, karena data gps anda sedang diaktifkan", Toast.LENGTH_LONG).show();
            } else {
                descText.setText("Lat : " + String.valueOf(latitude) + "," + "Long : " + String.valueOf(longitude));
            }
        }
    }


    public  boolean decodeFile(String path) {
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
                if (f.exists ()) f.delete ();
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
                if (f.exists ()) f.delete ();
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
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                m.postRotate(270);
                bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);
                final File f = new File(path);
                if (f.exists ()) f.delete ();
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
            if (f.exists ()) f.delete ();
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

    /**
     * Uploading the file to server
     * */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        long totalSize = 0;
        private String filePath = cameraFileOutput;
        private ProgressDialog Dialog = new ProgressDialog(DetailRoomTaskActivity.this);
        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Please wait a moment");
            Dialog.show();
           // progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            //progressBar.setVisibility(View.VISIBLE);
            //progressBar.setProgress(progress[0]);
            //txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                return uploadFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() throws Exception {
            String responseString = null;

            HttpClient httpclient = HttpHelper.createHttpClient();
            HttpPost httppost = new HttpPost("https://"+ MessengerConnectionService.HTTP_SERVER+"/demo_pamjaya/terima_upload.php");

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = new File(filePath);
                ContentType contentType = ContentType.create("image/jpeg");
                entity.addPart("foto1",  new FileBody( sourceFile, contentType, sourceFile.getName()));
                entity.addPart("note1",new StringBody(textMessage.getText().toString()));
                entity.addPart("koordinat", new StringBody(longitude+","+latitude));
                Date date = new Date();
                entity.addPart("waktu", new StringBody(hourFormat.format(date).toString()));
                entity.addPart("bcid", new StringBody(dbhelper.getMyContact().getJabberId()));
                entity.addPart("room", new StringBody("b6542sdr"));
                entity.addPart("job", new StringBody(idTask));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
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
            Dialog.dismiss();
            if(result.equalsIgnoreCase("1")){
                contentRoomDB.open();
                contentRoomDB.updateStatus(new ContentRoom(Long.parseLong(idTask), "done"));
                contentRoomDB.close();
                Toast.makeText(getApplicationContext(),"success",Toast.LENGTH_SHORT).show();
                finish();
            }else{
               /* sendSMSMessage();
                showAlert("SMS","send by sms");*/
            }

            super.onPostExecute(result);
        }

    }

    /**
     * Method to show alert dialog
     * */
    private void showAlert(final String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle(title)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (title.equalsIgnoreCase("SMS")){
                            finish();
                        }

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }



}
