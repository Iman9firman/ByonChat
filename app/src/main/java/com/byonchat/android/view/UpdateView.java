package com.byonchat.android.view;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.FragmentSetting.AboutSettingFragment;
import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.provider.IntervalDB;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.utils.HttpHelper;

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


public class UpdateView extends AppCompatActivity {
    TextView tvText;
    Button later, update;
    public final static String REQUEST_VERSION = "https://" + MessengerConnectionService.HTTP_SERVER + "/bc_voucher_client/api_luar/api_version.php";
    MessengerDatabaseHelper messengerHelper;
    private static final String SD_CARD_FOLDER = "ByonChatAPK";

    ProgressBar progressBar;
    Button btnCekUpdate;
    TextView textView2;
    searchRequest task;
    DownloadFromURL taskDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_view_layout);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.iss_default));
            getWindow().setStatusBarColor(getResources().getColor(R.color.iss_default));
        }

        tvText = (TextView) findViewById(R.id.tvText);
        later = (Button) findViewById(R.id.laterBtn);
        update = (Button) findViewById(R.id.updateBtn);

        IntervalDB db = new IntervalDB(getApplicationContext());
        db.open();
        Cursor cursor = db.getSingleContact(28);
        if(cursor.getCount()>0) {
//            db.deleteContact(12);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            try {
                Date d = sdf.parse(cursor.getString(cursor.getColumnIndexOrThrow(IntervalDB.COL_TIME)));
                Date c = Calendar.getInstance().getTime();

                long diff = c.getTime() - d.getTime();

                long jarak = 15-diff;
                if(jarak < 15){
                    tvText.setText("Update aplikasi terbaru telah tersedia, harap segera melakukan update. Anda memiliki " +
                            jarak + " hari lagi untuk segera melakukan update.");
                }

                if( jarak == 0){
                    tvText.setText("Aplikasi anda telah kadaluarsa. Harap melakukan update");
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
                finish();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task = new searchRequest(getApplicationContext());
                task.execute(getString(R.string.app_version), getString(R.string.app_company));
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


    class searchRequest extends AsyncTask<String, Void, String> {

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();

        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private Context mContext;
        private String content = null;

        public searchRequest(Context context) {
            this.mContext = context;
            progressBar.setVisibility(View.VISIBLE);
            btnCekUpdate.setVisibility(View.INVISIBLE);
            progressBar.setIndeterminate(true);
        }

        @Override
        protected void onPreExecute() {
        }

        InputStreamReader reader = null;

        protected String doInBackground(String... key) {

            try {


                HttpClient httpClient = HttpHelper
                        .createHttpClient(mContext);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);

                nameValuePairs.add(new BasicNameValuePair("username", messengerHelper.getMyContact().getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("version", key[0]));
                nameValuePairs.add(new BasicNameValuePair("company", key[1]));

                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                HttpPost post = new HttpPost(REQUEST_VERSION);
                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));


                //Response from the Http Request
                response = httpclient.execute(post);
                StatusLine statusLine = response.getStatusLine();

                //Check the Http Request for success

                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    content = out.toString();
                } else {
                    //Closes the connection.
                    content = statusLine.getReasonPhrase();
                    response.getEntity().getContent().close();
                    throw new IOException(content);
                }

            } catch (ClientProtocolException e) {
                content = e.getMessage();
            } catch (IOException e) {
                content = e.getMessage();
            } catch (Exception e) {
            }

            return content;
        }

        protected void onCancelled() {
        }

        protected void onPostExecute(String content) {
            if (content.contains("http://app.byonchat.com/download_bc/")) {
                taskDownload = new DownloadFromURL();
                taskDownload.execute(content);
            } else {
                progressBar.setVisibility(View.INVISIBLE);
                btnCekUpdate.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Ini adalah versi terbaru", Toast.LENGTH_SHORT).show();
            }
        }

    }

    class DownloadFromURL extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                    if (content != null) {
                        try {
                            content.close();
                        } catch (IOException e1) {
                            return false;
                        }
                    }
                    return false;
                }

            } catch (Exception e) {
                return false;
            }
        }

        protected void onProgressUpdate(Integer... values) {
            progressBar.setIndeterminate(false);
            progressBar.setProgress(values[0]);
        }


        @Override
        protected void onPostExecute(Boolean result) {
            btnCekUpdate.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            if (result.equals(Boolean.TRUE)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_CARD_FOLDER + "/ByonChat.apk"));
                    intent.setDataAndType(uri, "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);

                } else {

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_CARD_FOLDER + "/ByonChat.apk")),
                            "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

            } else {
                Toast.makeText(getApplicationContext(), "failed download, plese try again...", Toast.LENGTH_LONG).show();
            }
        }
    }
}
