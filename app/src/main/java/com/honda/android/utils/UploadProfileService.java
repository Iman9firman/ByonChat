package com.honda.android.utils;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.honda.android.R;
import com.honda.android.communication.MessengerConnectionService;
import com.honda.android.communication.NetworkInternetConnectionStatus;
import com.honda.android.provider.IntervalDB;
import com.honda.android.provider.MessengerDatabaseHelper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by pratama on 4/22/14.
 */
public class UploadProfileService extends IntentService implements
        ServiceConnection
{
    public static final String USERNAME = "username";
    public static final String KEY = "key";
    public static final String STATUS = "status";
    public static final String ACTION = "action";
    public static final String PHOTO = "photo";

    String username;
    String status;
    String action;
    String phot_loc;
    String date;

    int count;
    int lastPercent = 0;
    protected MessengerConnectionService.MessengerConnectionBinder binder;
    private File imageOutput;
    //boolean imageDelete =  false;
    String FILE_UPLOAD_URL =  "https://" + MessengerConnectionService.F_SERVER + "/byonchat_update.php";
    private MessengerDatabaseHelper dbhelper;

    private Intent      serviceIntent;

    public UploadProfileService(String name) {
        super(name);
    }

    public UploadProfileService(){
        super("UploadServiceProfile");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        serviceIntent = new Intent(this, UploadService.class);

        if(intent!=null)
        {
            getApplicationContext().bindService(
                    new Intent(this, MessengerConnectionService.class), this,
                    Context.BIND_AUTO_CREATE);

            if (dbhelper == null) {
                dbhelper = MessengerDatabaseHelper.getInstance(this);
            }

            username = dbhelper.getMyContact().getJabberId();
            status = dbhelper.getMyContact().getStatus();

            IntervalDB db = new IntervalDB(getApplicationContext());
            db.open();
            Cursor c = db.getSingleContact(13);
            if (c.getCount()>0) {
                phot_loc = c.getString(c.getColumnIndexOrThrow(IntervalDB.COL_TIME));
            }
            db.close();


            if (phot_loc.equalsIgnoreCase("null")){
                action = "status";
            }else{
                action = "semua";
                imageOutput = new File (phot_loc);
            }

            Thread t = new Thread(new BackgroundThreadUpdate(this,action));
            t.start();

        }

    }

    @Override
    public void onServiceConnected(ComponentName compName, IBinder iBinder) {
        binder = (MessengerConnectionService.MessengerConnectionBinder) iBinder;
    }

    @Override
    public void onServiceDisconnected(ComponentName compName) {

    }

    private class BackgroundThreadUpdate implements Runnable {

        Context context;
        String act;

        public BackgroundThreadUpdate( Context ctx,String acts) {
            this.context = ctx;
            this.act = acts;
        }

        @Override
        public void run() {
            try {
                if (NetworkInternetConnectionStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
                    String key = new ValidationsKey().getInstance(getApplicationContext()).key(false);
                    if (!key.equalsIgnoreCase("null")) {
                        if(act.equalsIgnoreCase("status")){
                            new requestUpdateStatus(getApplicationContext()).execute(key);
                        }else {
                            new PhotoUploaderHttp(getApplicationContext()).execute(key);
                        }

                    }
                }

            } catch (Exception e) {

            }

        }
    }

    class PhotoUploaderHttp extends AsyncTask<String, Void, String> {

        String code = "400";
        String code_text = "";
        String desc = "";
        private Context mContext;
        private String content = "";
        private boolean error = false;

        public PhotoUploaderHttp(Context context) {
            this.mContext = context;

        }
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(FILE_UPLOAD_URL);
                InputStreamReader reader = null;
                ContentType contentType = ContentType.create("image/jpeg");

                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress();
                            }
                        });

                entity.addPart("foto", new FileBody( imageOutput, contentType, imageOutput.getName()));
                // Extra parameters if you want to pass to server
                entity.addPart("key", new StringBody(params[0]));
                entity.addPart("username", new StringBody(dbhelper.getMyContact().getJabberId()));
                entity.addPart("status", new StringBody(URLEncoder.encode(status,"UTF-8")));
                entity.addPart("action", new StringBody("semua"));

                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    reader = new InputStreamReader(response.getEntity().getContent(), "UTF-8");
                    int r;
                    StringBuilder buf = new StringBuilder();
                    while ((r = reader.read()) != -1) {
                        buf.append((char) r);
                    }
                    content = buf.toString();
                    JSONObject result = new JSONObject(content);
                    code = result.getString("code");
                    code_text = result.getString("code_text");
                    desc = result.getString("description");
                    date = result.getString("date");
                    if(!code.equalsIgnoreCase("200")) error=true;
                } else {
                    error=true;
                }

            } catch (ClientProtocolException e) {
                e.printStackTrace();
                content = e.getMessage();
                error = true;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                content = e.getMessage();
                error = true;
            } catch (IOException e) {
                content = e.getMessage();
                e.printStackTrace();
                error = true;
            } catch (JSONException e) {
                content = e.getMessage();
                e.printStackTrace();
                error = true;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            //  startMainActivity();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (error) {
              //  pdialog.dismiss();
                if(content.contains("invalid_key")){
                    if(NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)){
                        String key = new ValidationsKey().getInstance(mContext).key(true);
                        if (key.equalsIgnoreCase("null")){
                            Toast.makeText(mContext,R.string.pleaseTryAgain,Toast.LENGTH_SHORT).show();
                          //  pdialog.dismiss();
                        }else{
                            new PhotoUploaderHttp(mContext).execute(key);
                        }
                    }else{
                        Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(mContext, desc, Toast.LENGTH_SHORT).show();
                }
            } else {
                if(code.equalsIgnoreCase("200")){
                    if (!binder.isConnected()) {
                       // showErrorDialog();
                        return;
                    }
                    new PhotoUploader().execute();
                }else{
                    Toast.makeText(getApplicationContext(), desc, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    class requestUpdateStatus extends AsyncTask<String, Void, String> {

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();

        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content = null;
        private boolean error = false;
        private Context mContext;
        String code = "400";
        String code_text = "";
        String desc = "";
        Boolean max = false;


        public requestUpdateStatus(Context context) {
            this.mContext = context;
        }


        @Override
        protected void onPreExecute() {
        }

        protected String doInBackground(String... key) {
            try {

                HttpClient httpClient = HttpHelper
                        .createHttpClient(getApplicationContext());
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);

                nameValuePairs.add(new BasicNameValuePair("username", username));
                nameValuePairs.add(new BasicNameValuePair("key",key[0]));
                nameValuePairs.add(new BasicNameValuePair("status",URLEncoder.encode(status,"UTF-8")));
                nameValuePairs.add(new BasicNameValuePair("action", "status"));

                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                HttpPost post = new HttpPost(FILE_UPLOAD_URL);
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
                    JSONObject result = new JSONObject(content);
                    code = result.getString("code");
                    code_text = result.getString("code_text");
                    desc = result.getString("description");
                    date = result.getString("date");
                    if(!code.equalsIgnoreCase("200")) error=true;
                } else {
                    //Closes the connection.
                    error = true;
                    content = statusLine.getReasonPhrase();
                    response.getEntity().getContent().close();
                    throw new IOException(content);
                }

            } catch (ClientProtocolException e) {
                content =  e.getMessage();
                error = true;
            } catch (IOException e) {
                content = e.getMessage();
                error = true;
            } catch (Exception e) {
                error = true;
            }

            return content;
        }

        protected void onCancelled() {
        }

        protected void onPostExecute(String content) {
            if (error) {
                if(content.contains("invalid_key")){
                    if(NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)){

                        String key = new ValidationsKey().getInstance(mContext).key(true);
                        if (key.equalsIgnoreCase("null")){
                            Toast.makeText(mContext, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                        }else{
                            new requestUpdateStatus(mContext).execute(key);
                        }
                    }else{
                        Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(mContext, desc, Toast.LENGTH_SHORT).show();
                }
            } else {
                if(code.equalsIgnoreCase("200")){
                    if (!binder.isConnected()) {
                        //showErrorDialog();
                        return;
                    }
                    new PhotoUploader().execute();
                }else{
                    Toast.makeText(getApplicationContext(), desc, Toast.LENGTH_SHORT).show();
                }
            }

        }

    }

    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    class PhotoUploader extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (imageOutput != null) {
                    binder.updateProfile(username, imageOutput);
                }
                binder.updatePresence(Presence.Type.available, toJson(action, status, date));
                publishProgress();
            } catch (XMPPException e) {
                Log.e(getClass().getSimpleName(),
                        "Error updating profile:" + e.getMessage(), e);
                //showErrorDialog();
            } catch (SmackException e) {
                Log.e(getClass().getSimpleName(),
                        "Error updating profile:" + e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        //    startMainActivity();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (imageOutput != null)
                imageOutput.delete();
          //  pdialog.dismiss();

            IntervalDB	db = new IntervalDB(getApplicationContext());
            db.open();
            Cursor c = db.getSingleContact(13);
            if (c.getCount()>0) {
                db.deleteContact(13);
            }
            db.close();

        }
    }

    public static String toJson(String action, String status, String date){
        try {
            JSONObject parent = new JSONObject();
            parent.put("action", action);
            parent.put("status", status);
            parent.put("date", date);
            parent.put("flag", "1");

            return parent.toString();
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }
}