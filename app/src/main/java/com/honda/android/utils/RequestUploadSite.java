package com.honda.android.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.honda.android.communication.MessengerConnectionService;
import com.honda.android.provider.Contact;
import com.honda.android.provider.MessengerDatabaseHelper;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Iman Firmansyah on 12/2/2015.
 */
public class RequestUploadSite extends AsyncTask<String, String, String> {

        private MessengerDatabaseHelper messengerHelper;
        private TaskCompleted mFragmentCallback;
        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();
        private static String REQUEST_KEYS = "https://"+ MessengerConnectionService.FILE_SERVER+":"+MessengerConnectionService.SERVER_PORT_MN+"/v2/upload/file/";
        private static String REQUEST_KEYS_URL = "https://"+ MessengerConnectionService.FILE_SERVER+":"+MessengerConnectionService.SERVER_PORT_MN+"/v2/upload/file/";
        public static String REQUEST_KEYS_URL_Thum = "https://"+ MessengerConnectionService.FILE_SERVER+":"+MessengerConnectionService.SERVER_PORT_MN+"/v2/download/thb/";
        public static String REQUEST_KEYS_URL_REAL = "https://"+ MessengerConnectionService.FILE_SERVER+":"+MessengerConnectionService.SERVER_PORT_MN+"/v2/download/file/";

        HttpResponse response;
        private String content = null;
        private String mKey;
        private String mFileName;
        private Context mContext;

        public RequestUploadSite(TaskCompleted fragmentCallback, Context ctx,String key,String fileName) {
                REQUEST_KEYS_URL = "https://"+ MessengerConnectionService.FILE_SERVER+":"+MessengerConnectionService.SERVER_PORT_MN+"/v2/upload/file/";
                mFragmentCallback = fragmentCallback;
                mContext = ctx;
                mKey = key;
                mFileName = fileName;
        }

        public RequestUploadSite(TaskCompleted fragmentCallback, Context ctx,String key,String fileName,String url) {
                    REQUEST_KEYS_URL = url;
                    mFragmentCallback = fragmentCallback;
                    mContext = ctx;
                    mKey = key;
                    mFileName = fileName;
        }

        @Override
        public void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(String... values) {
        }

        @Override
        protected String doInBackground(String... urls) {
            InputStream inputStream = null;
        if (messengerHelper == null) {
                messengerHelper = MessengerDatabaseHelper.getInstance(mContext);
        }

        Contact contact = messengerHelper.getMyContact();
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(REQUEST_KEYS_URL+mFileName);
            httpGet.addHeader("u",contact.getJabberId());
            httpGet.addHeader("k", mKey);
            response = httpclient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            inputStream = response.getEntity().getContent();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                if(inputStream != null){
                   String jsonm = convertInputStreamToString(inputStream);
                    JSONObject jObject = null;
                    try {
                        jObject = new JSONObject(jsonm);
                    } catch (JSONException e) {
                        content = "null1";
                        e.printStackTrace();
                    }
                    if (jObject != null) {
                        try {
                            String s = jObject.getString("s");
                            if(s.equals("0")){
                                if(REQUEST_KEYS_URL.equalsIgnoreCase(REQUEST_KEYS_URL_Thum)){
                                    content = jObject.getString("u");
                                }else if (REQUEST_KEYS_URL.equalsIgnoreCase(REQUEST_KEYS_URL_REAL)){
                                    content = jObject.getString("u");
                                }else if(REQUEST_KEYS_URL.equalsIgnoreCase(REQUEST_KEYS)){
                                    content = jObject.getString("u")+" "+jObject.getString("i");
                                }

                            }else {
                                content = "null2";
                                response.getEntity().getContent().close();
                                throw new IOException(statusLine.getReasonPhrase());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            content = "null3";
                            response.getEntity().getContent().close();
                            throw new IOException(statusLine.getReasonPhrase());
                        }
                    }
                }else{
                    content = "null";
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } else {
                content = "null4";
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }

        } catch (ClientProtocolException e) {
                content = "null5";
        } catch (IOException e) {
                content = "null6";
        } catch (Exception e) {
                content = "null7";
        }
        return content;
        }

        @Override
        protected void onPostExecute(String results) {
             mFragmentCallback.onTaskDone(results);
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