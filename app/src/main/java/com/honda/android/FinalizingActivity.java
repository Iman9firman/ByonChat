package com.honda.android;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.honda.android.communication.MessengerConnectionService;
import com.honda.android.createMeme.FilteringImage;
import com.honda.android.provider.IntervalDB;
import com.honda.android.provider.MessengerDatabaseHelper;
import com.honda.android.ui.activity.MainActivityNew;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

public class FinalizingActivity extends AppCompatActivity {

    private static final String TAG = "FinalizingActivity";
    private static String REQUEST_VERSION = "https://"
            + MessengerConnectionService.HTTP_SERVER + "/luar/version.php?id=";
    private MessengerDatabaseHelper messengerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalizing);
        FilteringImage.headerColor(getWindow(),FinalizingActivity.this,getResources().getColor(R.color.colorPrimary));
        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(this);
        }
        IntervalDB db = new IntervalDB(getApplicationContext());
        db.open();

        Cursor cursor = db.getSingleContact(12);
        if(cursor.getCount()>0) {
            db.deleteContact(12);
        }
        cursor.close();
        db.close();
        getVersion();

    }

    public void getVersion(){
        try {
            String version = new HttpAsyncTask().execute(REQUEST_VERSION + messengerHelper.getMyContact().getJabberId()+"&lama=0&ver="+getString(R.string.app_version)).get();
            if(version.length()<10){
                IntervalDB db = new IntervalDB(getApplicationContext());
                db.open();
                Cursor cursor = db.getSingleContact(12);
                if(cursor.getCount()>0) {
                    db.deleteContact(12);
                }
                cursor.close();
                db.close();
                finish();
                Intent i = new Intent();
                i.setClass(getApplicationContext(), MainActivityNew.class);
                startActivity(i);
            }else{
                Toast.makeText(getApplicationContext(), "Please try again later", Toast.LENGTH_LONG).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }
        @Override
        protected void onPostExecute(String result) {
        }
    }
    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
            inputStream = httpResponse.getEntity().getContent();
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "";
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}
