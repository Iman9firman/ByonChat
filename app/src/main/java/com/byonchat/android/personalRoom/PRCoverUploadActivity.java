package com.byonchat.android.personalRoom;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.personalRoom.utils.AndroidMultiPartEntity;
import com.byonchat.android.utils.ImageCompress;
import com.byonchat.android.utils.UtilsPD;

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
import java.io.IOException;

/**
 * Created by lukma on 3/17/2016.
 */
public class PRCoverUploadActivity extends Activity {
    private static final String TAG = FragmentMyPicture.class.getSimpleName();

    private String filePath = null, userid = null;
    private ImageView imgPreview;
    private Button btnUpload, btnCancel;
    long totalSize = 0;
    public static final String FILE_UPLOAD_URL = "https://" + MessengerConnectionService.HTTP_SERVER2 + "/personal_room/webservice/proses/cover_photo.php";
    private ImageCompress imageCompress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_cover_upload);
        imageCompress = new ImageCompress();
        imgPreview = (ImageView) findViewById(R.id.image_preview);
        btnUpload = (Button) findViewById(R.id.btnSUBMIT);
        btnCancel = (Button) findViewById(R.id.btnCANCEL);

        Intent i = getIntent();
        filePath = i.getStringExtra("filePath");
        userid = i.getStringExtra("userid");

        if (filePath != null) {
            previewMedia();
        } else {
            Toast.makeText(this.getApplicationContext(),
                    "Sorry, file path is missing!", Toast.LENGTH_LONG).show();
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new UploadFileToServer().execute();
            }
        });
    }

    public void previewMedia() {
        imgPreview.setVisibility(View.VISIBLE);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        final Bitmap bitmap = BitmapFactory.decodeFile(imageCompress.compressImage(getApplicationContext(), filePath), options);
        imgPreview.setImageBitmap(bitmap);
    }

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        long totalSize = 0;
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog == null) {
                progressDialog = UtilsPD.createProgressDialog(PRCoverUploadActivity.this);
                progressDialog.show();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(FILE_UPLOAD_URL);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = new File(imageCompress.compressImage(getApplicationContext(), filePath));
                ContentType contentType = ContentType.create("image/jpeg");

                entity.addPart("userid", new StringBody(userid));
                entity.addPart("file", new FileBody(sourceFile, contentType, sourceFile.getName()));

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
            progressDialog.dismiss();
            if (result.equals("1")) {
                finish();
            } else {
                Toast.makeText(PRCoverUploadActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
            }

            super.onPostExecute(result);
        }
    }
}
