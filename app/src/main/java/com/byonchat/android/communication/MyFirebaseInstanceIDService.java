package com.byonchat.android.communication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.byonchat.android.FinalizingActivity;
import com.byonchat.android.R;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.utils.HttpHelper;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();
    private MessengerDatabaseHelper messengerHelper;
    private static String REQUEST_VERSION = "https://"
            + MessengerConnectionService.HTTP_SERVER + "/luar/version.php?id=";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        storeRegIdInPref(refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(final String token) {
        Log.w(TAG, "sendRegistrationToServer: " + token);
    }

    private void storeRegIdInPref(String token) {

        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(this);
        }

        if (messengerHelper.getMyContact()!=null){
            try {
                new HttpAsyncTask().execute(REQUEST_VERSION + messengerHelper.getMyContact().getJabberId()+"&lama=0&req="+token+"&ver="+getString(R.string.app_version)).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();
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
            HttpClient httpclient = HttpHelper.createHttpClient();
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

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}

