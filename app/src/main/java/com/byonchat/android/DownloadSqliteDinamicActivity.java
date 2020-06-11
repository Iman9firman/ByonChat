package com.byonchat.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.byonchat.android.FragmentDinamicRoom.DinamicSLATaskActivity;
import com.byonchat.android.ISSActivity.LoginDB.UserDB;
import com.byonchat.android.provider.DataBaseDropDown;
import com.byonchat.android.provider.DatabaseKodePos;
import com.byonchat.android.provider.FormSLADB;
import com.byonchat.android.utils.HttpHelper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
                File dbDownloadPath = new File(DataBaseDropDown.getDatabaseFolder());
                if (!dbDownloadPath.exists()) {
                    dbDownloadPath.mkdirs();
                }
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
                HttpConnectionParams.setSoTimeout(httpParameters, 5000);
                HttpClient client = HttpHelper.createHttpClient();
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

    private class getCars extends AsyncTask<String, String, String> {
        String error = "";
        private Activity activity;
        private Context context;
        ProgressDialog rdialog = null;


        public getCars(Activity activity) {
            this.activity = activity;
            context = activity;
            rdialog = new ProgressDialog(DownloadSqliteDinamicActivity.this);
            rdialog.setMessage("Loading Form...");
            rdialog.show();
        }

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            postData(params[0], params[1]);
            return null;
        }

        protected void onPostExecute(String result) {
            rdialog.dismiss();
            finish();

            if (error.length() > 0) {
                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Success download.", Toast.LENGTH_SHORT).show();
            }
        }

        protected void onProgressUpdate(String... string) {
        }

        public void postData(String valueIWantToSend, String kode_jjt) {
            // Create a new HttpClient and Post Header

            try {
                HttpClient httpclient = HttpHelper.createHttpClient();
                HttpPost httppost = new HttpPost(valueIWantToSend);

                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("kode_jjt", kode_jjt));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {

                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);
                    FormSLADB roomsDB = new FormSLADB(context);
                    roomsDB.open();
                    roomsDB.deleteFormSLALL();
                    roomsDB.insser(data);
                    roomsDB.close();

                } else {
                    error = "Tolong periksa koneksi internet2.";
                }

            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                error = "Tolong periksa koneksi internet3.";
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                error = "Tolong periksa koneksi internet4.";
            } catch (IOException e) {
                // TODO Auto-generated catch block
                error = "Tolong periksa koneksi internet5.";
            } catch (Exception e) {
                e.printStackTrace();
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

        if (NAME_DB.equalsIgnoreCase("")) {
            UserDB userDB = UserDB.getInstance(getApplicationContext());
            Cursor cursorJJT = userDB.getSingle();
            if (cursorJJT.getCount() > 0) {
                String contentJJT = cursorJJT.getString(cursorJJT.getColumnIndexOrThrow(UserDB.EMPLOYEE_MULTICOST));
                try {
                    JSONArray arr = new JSONArray(contentJJT);
                    ArrayList<String> duaJJt = new ArrayList<>();

                    for (int as = 0; as < arr.length(); as++) {
                        JSONObject jo = arr.getJSONObject(as);
                        String cost_center = jo.getString("costcenter");
                        duaJJt.add(arr.getString(as).substring(arr.getString(as).indexOf("[") + 1, arr.getString(as).indexOf("]")));
                    }

                    JSONArray jsonArray = new JSONArray();
                    for (String jjt : duaJJt) {
                        jsonArray.put(jjt);
                    }

                    new getCars(DownloadSqliteDinamicActivity.this).execute("https://iss.byonchat.com/list_api_iss/list_pembobotan_jjt.php", jsonArray.toString());
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Please insert memmory card 22", Toast.LENGTH_LONG).show();
                    finish();
                }
            }


        } else {
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
}
