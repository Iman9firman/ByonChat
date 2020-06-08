package com.byonchat.android.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.UpdateListDB;
import com.byonchat.android.utils.HttpHelper;
import com.byonchat.android.utils.Validations;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UpdateViewDialog extends Dialog {
    Context context;
    TextView tvText;
    Button update;
    private static final String SD_CARD_FOLDER = "ByonChatAPK";

    DownloadFromURL taskDownload;
    String link_update;
    String company, version, type_update;

    public UpdateViewDialog(Context context) {
        super(context);
        this.context = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_view_layout);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(context.getResources().getColor(R.color.iss_default));
            getWindow().setStatusBarColor(context.getResources().getColor(R.color.iss_default));
        }

        tvText = (TextView) findViewById(R.id.tvText);
        update = (Button) findViewById(R.id.updateBtn);

        UpdateListDB db = new UpdateListDB(context);
        db.open();
        Cursor cursor = db.getUnrefreshedData("0");
        if(cursor.getCount()>0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                link_update = cursor.getString(cursor.getColumnIndexOrThrow(UpdateListDB.UPD_TARGET_URL));
                Date d = sdf.parse(cursor.getString(cursor.getColumnIndexOrThrow(UpdateListDB.UPD_DATE_EXP)));
//                Date c = Calendar.getInstance().getTime();
                company = cursor.getString(cursor.getColumnIndexOrThrow(UpdateListDB.UPD_APP_COMPANY));
                version = cursor.getString(cursor.getColumnIndexOrThrow(UpdateListDB.UPD_APP_VERSION));
                type_update = cursor.getString(cursor.getColumnIndexOrThrow(UpdateListDB.UPD_NAME));

                tvText.setText("New version is exist. Please update your application");
            } catch (ParseException ex) {
                Log.w("Exception", ex.getLocalizedMessage());
            }

        }
        cursor.close();
        db.close();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskDownload = new DownloadFromURL();
                taskDownload.execute(link_update);

            }
        });
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    class DownloadFromURL extends AsyncTask<String, Integer, Boolean> {
        String error = "";
        private ProgressDialog dialog = new ProgressDialog(context);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setTitle("Downloading new version!");
            dialog.setMessage("Please wait and make sure you stay in this page");
            dialog.setIndeterminate(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setCancelable(false);
            dialog.setMax(100);
            dialog.show();

            changes cs = (changes) context;
            cs.onproses(false);

        }

        @Override
        protected Boolean doInBackground(String... fileUrl) {
            try {
                File oldFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_CARD_FOLDER);
                File dbDownloadPath = new File(oldFolder, "ByonChat.apk");
                if (!oldFolder.exists()) {
                    oldFolder.mkdirs();
                }

                if (dbDownloadPath.exists()) {
                    dbDownloadPath.delete();
                }

                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 15000);
                HttpConnectionParams.setSoTimeout(httpParameters, 15000);
                HttpClient client = HttpHelper.createHttpClient();
                HttpGet httpGet = new HttpGet(fileUrl[0]);
                InputStream content = null;
                try {
                    HttpResponse execute = client.execute(httpGet);
                    if (execute.getStatusLine().getStatusCode() != 200) {
                        return null;
                    }
                    content = execute.getEntity().getContent();
                    long downloadSize = execute.getEntity().getContentLength();
                    FileOutputStream fos = new FileOutputStream(dbDownloadPath.getAbsolutePath());
                    byte[] buffer = new byte[256];
                    int read;
                    long downloadedAlready = 0;
                    while ((read = content.read(buffer)) != -1) {
                        fos.write(buffer, 0, read);
                        downloadedAlready += read;
                        publishProgress((int) (downloadedAlready * 100 / downloadSize));
                    }
                    fos.flush();
                    fos.close();
                    content.close();
                    return true;
                } catch (Exception e) {
                    error = e.getMessage();
                    if (content != null) {
                        try {
                            content.close();
                        } catch (IOException e1) {
                            error = e.getMessage();
                            return false;
                        }
                    }
                    return false;
                }

            } catch (Exception e) {
                error = e.getMessage();
                return false;
            }
        }

        protected void onProgressUpdate(Integer... values) {
            dialog.setProgress(values[0]);
        }


        @Override
        protected void onPostExecute(Boolean result) {
            if (result.equals(Boolean.TRUE)) {
                UpdateListDB dtbs = new UpdateListDB(context);
                dtbs.open();
                dtbs.updateVersionByCompany(company, version, type_update);
                dtbs.close();

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_CARD_FOLDER + "/ByonChat.apk"));
                    intent.setDataAndType(uri, "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    context.startActivity(intent);

                } else {

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_CARD_FOLDER + "/ByonChat.apk")),
                            "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
                dismiss();

            } else {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Toast.makeText(context, "Download Failed, Please try again!" + error, Toast.LENGTH_LONG).show();
            }
        }
    }

    public interface changes{
        void onproses(boolean proses);
    }
}

