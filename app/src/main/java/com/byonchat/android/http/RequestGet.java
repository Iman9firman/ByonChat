package com.byonchat.android.http;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.byonchat.android.local.Byonchat;
import com.byonchat.android.utils.HttpHelper;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class RequestGet extends AsyncTask<String, String, String> {
    private final static String TAG = RequestGet.class.getSimpleName();
    final int REGISTRATION_TIMEOUT = 3 * 1000;
    final int WAIT_TIMEOUT = 30 * 1000;
    final HttpClient httpclient = HttpHelper.createHttpClient();
    private JobCompleted mFragmentCallback;

    final HttpParams params = httpclient.getParams();
    HttpResponse httpResponse;
    String result = null;
    boolean error = false;
    InputStream inputStream = null;
    private Context mContext;

    public RequestGet(JobCompleted fragmentCallback, Context ctx) throws Exception {
        mFragmentCallback = fragmentCallback;
        mContext = ctx;
    }

    @Override
    protected void onPreExecute() {
        mFragmentCallback.onTaskBegin();
    }

    @Override
    protected String doInBackground(String... params) {

        return GET(params[0], mContext);
    }

    public String GET(String url, Context context) {

        try {
            HttpClient httpClient = HttpHelper
                    .createHttpClient(context);
            HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
            ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

            HttpGet httpGet = new HttpGet(Byonchat.getAppServer() + url);

            httpResponse = httpclient.execute(httpGet);
            StatusLine statusLine = httpResponse.getStatusLine();

            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                httpResponse.getEntity().writeTo(out);
                out.close();
                result = out.toString();
            } else {
                error = true;
                result = statusLine.getReasonPhrase();
                httpResponse.getEntity().getContent().close();
                throw new IOException(result);
            }

            Log.w(TAG, url + " ; " + result.toString());

        } catch (ClientProtocolException e) {
            result = e.getMessage();
            error = true;
        } catch (IOException e) {
            result = e.getMessage();
            error = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        mFragmentCallback.onTaskDone(result);
    }
}
