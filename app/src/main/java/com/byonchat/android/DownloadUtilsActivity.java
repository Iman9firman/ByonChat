package com.byonchat.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.byonchat.android.provider.DatabaseKodePos;

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

public class DownloadUtilsActivity extends AppCompatActivity {
    private static final String SD_CARD_FOLDER = "ByonChatDB";
    private static final String DB_DOWNLOAD_PATH = "http://campaignmanager.asia/simple/db/daftarkodepos.sqlite";
    private DatabaseKodePos mDB = null;
    private DatabaseDownloadTask mDatabaseDownloadTask = null;
    private DatabaseOpenTask mDatabaseOpenTask = null;
    private ProgressDialog mProgressDialog;

    private class DatabaseDownloadTask extends AsyncTask<Context, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(DownloadUtilsActivity.this);
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
                File dbDownloadPath = new File(DatabaseKodePos.getDatabaseFolder());
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
                    FileOutputStream fos = new FileOutputStream(DatabaseKodePos.getDatabaseFolder() + "daftarkodepos" + ".sqlite");
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
                    Log.w("untukng", e.getMessage());
                    if (content != null) {
                        try {
                            content.close();
                        } catch (IOException e1) {
                        }
                    }
                    return false;
                }
            } catch (Exception e) {
                Log.w("untukng22", e.getMessage());
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
            if (result.equals(Boolean.TRUE)) {
                Toast.makeText(DownloadUtilsActivity.this, "Success", Toast.LENGTH_LONG).show();
                mDatabaseOpenTask = new DatabaseOpenTask();
                mDatabaseOpenTask.execute(new Context[]{DownloadUtilsActivity.this});
            } else {
                Toast.makeText(getApplicationContext(), "failed download, plese try again...", Toast.LENGTH_LONG).show();
                finish();
            }
        }

    }

    private class DatabaseOpenTask extends AsyncTask<Context, Void, DatabaseKodePos> {

        @Override
        protected DatabaseKodePos doInBackground(Context... ctx) {
            try {
                String externalBaseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
                // DELETE OLD DATABASE ANFANG
                File oldFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_CARD_FOLDER);
                File oldFile = new File(oldFolder, "daftarkodepos.sqlite");
                if (oldFile.exists()) {
                    oldFile.delete();
                }
                if (oldFolder.exists()) {
                    oldFolder.delete();
                }
                // DELETE OLD DATABASE ENDE
                File newDB = new File(DatabaseKodePos.getDatabaseFolder() + "daftarkodepos.sqlite");
                if (newDB.exists()) {
                    return new DatabaseKodePos(ctx[0]);
                } else {
                    return null;
                }
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(DownloadUtilsActivity.this, "Please wait...", "Loading the database! This may take some time ...", true);
        }

        @Override
        protected void onPostExecute(DatabaseKodePos newDB) {
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
        mDB = new DatabaseKodePos(getApplicationContext());
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

    class ModelWilayah {
        private String kodepos;
        private String prov;
        private String kab;
        private String jenis;
        private String kec;
        private String kel;

        public ModelWilayah(String kodePos, String provI, String kabU, String jen, String kecA, String kelU) {
            this.kodepos = kodePos;
            this.prov = provI;
            this.kab = kabU;
            this.jenis = jen;
            this.kec = kecA;
            this.kel = kelU;
        }

        public String getKodepos() {
            return kodepos;
        }

        public void setKodepos(String kodepos) {
            this.kodepos = kodepos;
        }

        public String getProv() {
            return prov;
        }

        public void setProv(String prov) {
            this.prov = prov;
        }

        public String getKab() {
            return kab;
        }

        public void setKab(String kab) {
            this.kab = kab;
        }

        public String getJenis() {
            return jenis;
        }

        public void setJenis(String jenis) {
            this.jenis = jenis;
        }

        public String getKec() {
            return kec;
        }

        public void setKec(String kec) {
            this.kec = kec;
        }

        public String getKel() {
            return kel;
        }

        public void setKel(String kel) {
            this.kel = kel;
        }
    }

}
