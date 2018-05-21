package com.byonchat.android.FragmentSetting;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
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
import java.util.ArrayList;
import java.util.List;

public class AboutSettingFragment extends Fragment {

    public final static String REQUEST_VERSION = "https://" + MessengerConnectionService.HTTP_SERVER + "/bc_voucher_client/api_luar/api_version.php";
    MessengerDatabaseHelper messengerHelper;
    private static final String SD_CARD_FOLDER = "ByonChatAPK";

    ProgressBar progressBar;
    Button btnCekUpdate;
    TextView textView2;
    searchRequest task;
    DownloadFromURL taskDownload;

    public AboutSettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_setting, container, false);
        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(getContext());
        }


        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        textView2 = (TextView) view.findViewById(R.id.textView2);
        textView2.setText("Version " + getString(R.string.app_version));
        btnCekUpdate = (Button) view.findViewById(R.id.btnCekUpdate);
        btnCekUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task = new searchRequest(getContext());
                task.execute(getString(R.string.app_version));
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(task!=null){
            task.cancel(true);
        }
        if (taskDownload!=null){
            taskDownload.cancel(true);
        }


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
                Toast.makeText(getContext(), "Ini adalah versi terbaru", Toast.LENGTH_SHORT).show();
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

                if (dbDownloadPath.exists()){
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
                        publishProgress((int) (downloadedAlready*100/downloadSize));
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
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SD_CARD_FOLDER + "/ByonChat.apk")),
                        "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            else {
                Toast.makeText(getContext(),"failed download, plese try again...", Toast.LENGTH_LONG).show();
            }
        }
    }
}
