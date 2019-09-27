package com.honda.android.view;

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
import androidx.core.content.FileProvider;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.honda.android.R;
import com.honda.android.provider.UpdateListDB;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UpdateViewDialog extends Dialog {
    Context context;
    TextView tvText;
    Button later, update;
    private static final String SD_CARD_FOLDER = "S-TeamAPK";

    DownloadFromURL taskDownload;
    String link_update;
    String company, version, type_update;
    boolean permanent = false;

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
        later = (Button) findViewById(R.id.laterBtn);
        update = (Button) findViewById(R.id.updateBtn);

        UpdateListDB db = new UpdateListDB(context);
        db.open();
        Cursor cursor = db.getUnrefreshedData("0");
        if(cursor.getCount()>0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                link_update = cursor.getString(cursor.getColumnIndexOrThrow(UpdateListDB.UPD_TARGET_URL));
                Date d = sdf.parse(cursor.getString(cursor.getColumnIndexOrThrow(UpdateListDB.UPD_DATE_EXP)));
                Date c = Calendar.getInstance().getTime();
                company = cursor.getString(cursor.getColumnIndexOrThrow(UpdateListDB.UPD_APP_COMPANY));
                version = cursor.getString(cursor.getColumnIndexOrThrow(UpdateListDB.UPD_APP_VERSION));
                type_update = cursor.getString(cursor.getColumnIndexOrThrow(UpdateListDB.UPD_NAME));


                long diff = d.getTime() - c.getTime();

                if (diff <= 0) {
                    tvText.setText("Aplikasi anda telah kadaluarsa. Harap melakukan update");
                    permanent = true;
                }else {

                    tvText.setText("Update aplikasi terbaru telah tersedia, harap segera melakukan update. Anda memiliki " +
                            diff + " hari lagi untuk segera melakukan update.");
                    permanent = false;
                }
            } catch (ParseException ex) {
                Log.w("Exception", ex.getLocalizedMessage());
            }

        }
        cursor.close();
        db.close();


        later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!permanent) {
                    dismiss();
                }else {
                    Toast.makeText(context,"Update required to continue!",Toast.LENGTH_SHORT).show();
                }
            }
        });
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
            dialog.setMessage("Doing something, please wait.");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... fileUrl) {
            try {
                File oldFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_CARD_FOLDER);
                File dbDownloadPath = new File(oldFolder, "S-TeamAPK.apk");
                if (!oldFolder.exists()) {
                    oldFolder.mkdirs();
                }

                if (dbDownloadPath.exists()) {
                    dbDownloadPath.delete();
                }

                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 15000);
                HttpConnectionParams.setSoTimeout(httpParameters, 15000);
                DefaultHttpClient client = new DefaultHttpClient(httpParameters);
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
                    Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_CARD_FOLDER + "/S-TeamAPK.apk"));
                    intent.setDataAndType(uri, "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    context.startActivity(intent);

                } else {

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_CARD_FOLDER + "/S-TeamAPK.apk")),
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
}

