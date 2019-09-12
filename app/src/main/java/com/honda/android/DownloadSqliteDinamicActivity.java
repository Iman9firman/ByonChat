package com.honda.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.honda.android.provider.DataBaseDropDown;
import com.honda.android.provider.DatabaseKodePos;

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

public class DownloadSqliteDinamicActivity extends AppCompatActivity {
    private static final String SD_CARD_FOLDER = "DB";
    private String DB_DOWNLOAD_PATH;
    private DataBaseDropDown mDB = null;
    private DatabaseDownloadTask mDatabaseDownloadTask = null;
    private DatabaseOpenTask mDatabaseOpenTask = null;
    private ProgressDialog mProgressDialog;
    private String NAME_DB;

    private class DatabaseDownloadTask extends AsyncTask<Context, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(DownloadSqliteDinamicActivity.this);
            mProgressDialog.setTitle("downloading...");
            mProgressDialog.setMessage("Please wait a moment");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        @Override
        protected Boolean doInBackground(Context... params) {
            try {
                Log.w("JAYAD", DB_DOWNLOAD_PATH);
                File dbDownloadPath = new File(DataBaseDropDown.getDatabaseFolder());
                if (!dbDownloadPath.exists()) {
                    dbDownloadPath.mkdirs();
                }
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
                HttpConnectionParams.setSoTimeout(httpParameters, 5000);
                DefaultHttpClient client = new DefaultHttpClient(httpParameters);
                HttpGet httpGet = new HttpGet(DB_DOWNLOAD_PATH);
                InputStream content = null;
                try {
                    HttpResponse execute = client.execute(httpGet);
                    if (execute.getStatusLine().getStatusCode() != 200) {
                        return null;
                    }
                    content = execute.getEntity().getContent();
                    long downloadSize = execute.getEntity().getContentLength();
                    FileOutputStream fos = new FileOutputStream(DataBaseDropDown.getDatabaseFolder() + NAME_DB + ".sqlite");
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
                    if (content != null) {
                        try {
                            content.close();
                        } catch (IOException e1) {
                        }
                    }
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }

        protected void onProgressUpdate(Integer... values) {
            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.setProgress(values[0]);
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
            if (result != null) {
                if (result.equals(Boolean.TRUE)) {
                    Toast.makeText(DownloadSqliteDinamicActivity.this, "Success", Toast.LENGTH_LONG).show();
                    mDatabaseOpenTask = new DatabaseOpenTask();
                    mDatabaseOpenTask.execute(new Context[]{DownloadSqliteDinamicActivity.this});
                } else {
                    Toast.makeText(getApplicationContext(), "failed download, plese try again...", Toast.LENGTH_LONG).show();
                    finish();
                }
            } else {
                finish();
            }

        }

    }

    private class DatabaseOpenTask extends AsyncTask<Context, Void, DataBaseDropDown> {

        @Override
        protected DataBaseDropDown doInBackground(Context... ctx) {
            try {
                File oldFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_CARD_FOLDER);
                File oldFile = new File(oldFolder, NAME_DB + ".sqlite");
                if (oldFile.exists()) {
                    oldFile.delete();
                }
                if (oldFolder.exists()) {
                    oldFolder.delete();
                }
                // DELETE OLD DATABASE ENDE
                File newDB = new File(DataBaseDropDown.getDatabaseFolder() + NAME_DB + ".sqlite");
                if (newDB.exists()) {
                    return new DataBaseDropDown(ctx[0], NAME_DB);
                } else {
                    return null;
                }
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(DownloadSqliteDinamicActivity.this, "Please wait...", "Loading the database! This may take some time ...", true);
        }

        @Override
        protected void onPostExecute(DataBaseDropDown newDB) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
            if (newDB == null) {
                mDB = null;
                mDatabaseDownloadTask = new DatabaseDownloadTask();
                mDatabaseDownloadTask.execute();
            } else {
                mDB = newDB;
                finish();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDatabaseDownloadTask != null) {
            if (mDatabaseDownloadTask.getStatus() != AsyncTask.Status.FINISHED) {
                mDatabaseDownloadTask.cancel(true);
            }
        }
        if (mDatabaseOpenTask != null) {
            if (mDatabaseOpenTask.getStatus() != AsyncTask.Status.FINISHED) {
                mDatabaseOpenTask.cancel(true);
            }
        }
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        if (mDB != null) {
            mDB.close();
            mDB = null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_pnumber);
        LinearLayout linearLayoutContent = (LinearLayout) findViewById(R.id.linearLayoutContent);
        linearLayoutContent.setVisibility(View.GONE);

        NAME_DB = getIntent().getStringExtra("name_db");
        DB_DOWNLOAD_PATH = getIntent().getStringExtra("path_db");

        mDB = new DataBaseDropDown(getApplicationContext(), NAME_DB);
        if (mDB.getWritableDatabase() != null) {
            finish();
        } else {
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Toast.makeText(getApplicationContext(), "Please insert memmory card", Toast.LENGTH_LONG).show();
                finish();
            }
            mDatabaseOpenTask = new DatabaseOpenTask();
            mDatabaseOpenTask.execute(new Context[]{this});
        }

    }
}
