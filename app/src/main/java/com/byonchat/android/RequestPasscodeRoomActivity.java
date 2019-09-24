package com.byonchat.android;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity;
import com.byonchat.android.adapter.ExpandableListAdapter;
import com.byonchat.android.communication.NotificationReceiver;
import com.byonchat.android.personalRoom.utils.AndroidMultiPartEntity;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.Rooms;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.utils.GPSTracker;
import com.byonchat.android.utils.ImageFilePath;
import com.byonchat.android.utils.MediaProcessingUtil;
import com.byonchat.android.utils.Validations;
import com.byonchat.android.utils.ValidationsKey;
import com.byonchat.android.widget.CircleProgressBar;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import zharfan.com.cameralibrary.Camera;
import zharfan.com.cameralibrary.CameraActivity;

import static com.byonchat.android.ByonChatMainRoomActivity.jsonResultType;
import static com.byonchat.android.ConversationActivity.KEY_JABBER_ID;
import static com.byonchat.android.ConversationActivity.KEY_TITLE;

public class RequestPasscodeRoomActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    private static final int REQ_CAMERA = 1201;
    String usernameRoom;
    MessengerDatabaseHelper messengerHelper = null;
    BotListDB botListDB;
    String color;
    String colorText;
    String name;
    CircleProgressBar vCircleProgress;
    TextView uploadProgress;
    TextView reset;
    CardView contentMain;
    String icon = "";
    Button loginBtn;
    private UserLoginTask mAuthTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_passcode_room);

        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(getApplicationContext());
        }

        if (botListDB == null) {
            botListDB = BotListDB.getInstance(getApplicationContext());
        }

        contentMain = (CardView) findViewById(R.id.content_main);
        vCircleProgress = (CircleProgressBar) findViewById(R.id.progress);
        uploadProgress = (TextView) findViewById(R.id.uploadProgress);
        reset = (TextView) findViewById(R.id.btn_reset);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        usernameRoom = getIntent().getStringExtra(KEY_JABBER_ID);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Validations().getInstance(RequestPasscodeRoomActivity.this).changeProtectLogin(usernameRoom, "5");
                finish();
                Intent a = new Intent(getApplicationContext(), RequestPasscodeRoomActivity.class);
                a.putExtra(ConversationActivity.KEY_JABBER_ID, usernameRoom);
                a.putExtra(ConversationActivity.KEY_TITLE, "request");
                startActivity(a);

            }
        });

        Cursor cur = botListDB.getSingleRoom(usernameRoom);

        if (cur.getCount() > 0) {
            name = cur.getString(cur.getColumnIndex(BotListDB.ROOM_REALNAME));
            icon = cur.getString(cur.getColumnIndex(BotListDB.ROOM_ICON));
            color = jsonResultType(cur.getString(cur.getColumnIndex(BotListDB.ROOM_COLOR)), "a");
            colorText = jsonResultType(cur.getString(cur.getColumnIndex(BotListDB.ROOM_COLOR)), "b");
            uploadProgress.setTextColor(Color.parseColor("#" + color));
            if (color == null || color.equalsIgnoreCase("") || color.equalsIgnoreCase("null")) {
                color = "006b9c";
            }
            if (colorText == null || colorText.equalsIgnoreCase("") || colorText.equalsIgnoreCase("null")) {
                colorText = "ffffff";
            }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(Color.parseColor("#" + color));
            }

        }

        if (getIntent().getStringExtra(KEY_TITLE).equalsIgnoreCase("request")) {
            vCircleProgress.setVisibility(View.VISIBLE);
            uploadProgress.setVisibility(View.VISIBLE);
            contentMain.setVisibility(View.GONE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            CameraActivity.Builder start = new CameraActivity.Builder(this, REQ_CAMERA);
            start.setLockSwitch(CameraActivity.UNLOCK_SWITCH_CAMERA);
            start.setCameraFace(CameraActivity.CAMERA_FRONT);
            start.setFlashMode(CameraActivity.FLASH_OFF);
            start.setQuality(CameraActivity.MEDIUM);
            start.setRatio(CameraActivity.RATIO_4_3);
            start.setFileName(new MediaProcessingUtil().createFileName("jpeg", "ROOM"));
            new Camera(start.build()).lauchCamera();

        } else {
            vCircleProgress.setVisibility(View.GONE);
            uploadProgress.setVisibility(View.GONE);
            contentMain.setVisibility(View.VISIBLE);
            RelativeLayout someView = (RelativeLayout) findViewById(R.id.all_background);
            someView.setBackgroundColor(Color.parseColor("#" + color));
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            Picasso.with(RequestPasscodeRoomActivity.this).load(icon).into(imageView);

            TextInputEditText passcode = (TextInputEditText) findViewById(R.id.passcode);
            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProgress();
                    mAuthTask = new UserLoginTask();
                    mAuthTask.execute("https://bb.byonchat.com/bc_voucher_client/webservice/list_api/api_verify_passcode.php", passcode.getText().toString());


                }
            });
        }


    }

    private void showProgress() {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CAMERA) {
            if (resultCode == RESULT_OK) {

                String returnString = data.getStringExtra("PICTURE");
                GPSTracker gps = new GPSTracker(RequestPasscodeRoomActivity.this);
                if (!gps.canGetLocation()) {
                    Toast.makeText(this, " Please Turn on GPS ", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    if (gps.canGetLocation()) {
                        new UploadFileToServerB().execute(returnString, String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()));
                    }
                }


            } else if (resultCode == RESULT_CANCELED) {
                finish();
                Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
            } else {
                finish();
                Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class UploadFileToServerB extends AsyncTask<String, Integer, String> {
        long totalSize = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return uploadFile(params[0], params[1], params[2]);
        }

        @SuppressWarnings("deprecation")
        private String uploadFile(String photo, String lat, String longi) {
            String responseString = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://bb.byonchat.com/bc_voucher_client/webservice/list_api/api_generate_passcode.php");

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                                vCircleProgress.setProgress((int) ((num / (float) totalSize) * 100));
                                uploadProgress.setText("Upload " + (int) ((num / (float) totalSize) * 100) + "%");
                            }
                        });


                File sourceFile = new File(resizeAndCompressImageBeforeSend(getApplicationContext(), photo, "fileUploadBC_" + new Date().getTime() + ".jpg"));

                if (!sourceFile.exists()) {
                    return "File not exists";
                }

                ContentType contentType = ContentType.create("image/jpeg");
                entity.addPart("username_room", new StringBody(usernameRoom));
                entity.addPart("bc_user", new StringBody(messengerHelper.getMyContact().getJabberId()));
                entity.addPart("lat", new StringBody(lat));
                entity.addPart("long", new StringBody(longi));
                entity.addPart("foto", new FileBody(sourceFile, contentType, sourceFile.getName()));


                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                Log.w("siiip3", statusCode + "");
                if (statusCode == 200) {
                    responseString = EntityUtils.toString(r_entity);
                    Log.w("garin", responseString);
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
            Log.w("res", result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                String code = jsonObject.getString("result");
                if (code.trim().equalsIgnoreCase("0")) {
                    String message = jsonObject.getString("message");

                    final AlertDialog.Builder alertbox = new AlertDialog.Builder(RequestPasscodeRoomActivity.this);
                    alertbox.setTitle("Failed");
                    alertbox.setTitle(message);
                    alertbox.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            finish();
                        }
                    });
                    alertbox.setNegativeButton("Try Again", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            if (ActivityCompat.checkSelfPermission(RequestPasscodeRoomActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            CameraActivity.Builder start = new CameraActivity.Builder(RequestPasscodeRoomActivity.this, REQ_CAMERA);
                            start.setLockSwitch(CameraActivity.UNLOCK_SWITCH_CAMERA);
                            start.setCameraFace(CameraActivity.CAMERA_FRONT);
                            start.setFlashMode(CameraActivity.FLASH_OFF);
                            start.setQuality(CameraActivity.MEDIUM);
                            start.setRatio(CameraActivity.RATIO_4_3);
                            start.setFileName(new MediaProcessingUtil().createFileName("jpeg", "ROOM"));
                            new Camera(start.build()).lauchCamera();
                        }
                    });

                    alertbox.show();

                } else {
                    finish();
                    new Validations().getInstance(RequestPasscodeRoomActivity.this).changeProtectLogin(usernameRoom, "6");
                    Toast.makeText(RequestPasscodeRoomActivity.this, "Request Passcode Success", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(RequestPasscodeRoomActivity.this, "Internal Server Error", Toast.LENGTH_SHORT).show();
                finish();
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


    public class UserLoginTask extends AsyncTask<String, String, String> {
        String error = "";

        @Override
        protected String doInBackground(String... params) {
            postData(params[0], params[1]);

            return null;
        }

        protected void onPostExecute(String result) {
            mAuthTask = null;
            progressDialog.dismiss();

            if (error.length() > 0) {
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            progressDialog.dismiss();
        }

        public void postData(String valueIWantToSend, String pass) {
            // Create a new HttpClient and Post Header

            try {
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
                HttpConnectionParams.setSoTimeout(httpParameters, 5000);
                HttpClient httpclient = new DefaultHttpClient(httpParameters);
                HttpPost httppost = new HttpPost(valueIWantToSend);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("username_room", usernameRoom));
                nameValuePairs.add(new BasicNameValuePair("bc_user", messengerHelper.getMyContact().getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("passcode", pass));

                Log.w("hasil", nameValuePairs.toString());
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                int status = response.getStatusLine().getStatusCode();
                Log.w("kask", status + "");
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);

                    Log.w("hewews", data);

                    try {
                        JSONObject jsonRootObject = new JSONObject(data);

                        String hasil = jsonRootObject.getString("result");
                        if (hasil.equalsIgnoreCase("0")) {
                            error = jsonRootObject.getString("message");
                        } else if (hasil.equalsIgnoreCase("1")) {
                            new Validations().getInstance(RequestPasscodeRoomActivity.this).changeProtectLogin(usernameRoom, "2");
                            finish();

                            Intent a = new Intent(getApplicationContext(), LoginDinamicFingerPrint.class);
                            a.putExtra(ConversationActivity.KEY_JABBER_ID, usernameRoom);
                            a.putExtra(ConversationActivity.KEY_TITLE, messengerHelper.getMyContact().getJabberId());
                            a.putExtra(ConversationActivity.KEY_MESSAGE_FORWARD, "success");
                            startActivity(a);

                        } else {
                            error = "Tolong periksa koneksi internet.";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        error = data;
                    }
                } else {
                    error = "Tolong periksa koneksi internet.";
                }

            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                RequestPasscodeRoomActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Maaf gagal login, karena ada masalah pada data koneksi.  Terimakasih", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (ClientProtocolException e) {
                RequestPasscodeRoomActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Maaf gagal login, karena ada masalah pada data koneksi.  Terimakasih", Toast.LENGTH_LONG).show();
                    }
                });
                // TODO Auto-generated catch block
            } catch (IOException e) {
                RequestPasscodeRoomActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Maaf gagal login, karena ada masalah pada data koneksi.  Terimakasih", Toast.LENGTH_LONG).show();
                    }
                });
                // TODO Auto-generated catch block
            }
        }

    }


}
