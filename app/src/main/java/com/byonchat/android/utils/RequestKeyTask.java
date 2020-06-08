package com.byonchat.android.utils;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.Interval;
import com.byonchat.android.provider.IntervalDB;
import com.byonchat.android.provider.MessengerDatabaseHelper;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Iman Firmansyah on 12/2/2015.
 */
public class RequestKeyTask extends AsyncTask<String, String, String> {

    private MessengerDatabaseHelper messengerHelper;
    private TaskCompleted mFragmentCallback;
    private static final int REGISTRATION_TIMEOUT = 3 * 1000;
    private static final int WAIT_TIMEOUT = 30 * 1000;
    private static String REQUEST_KEYS_URL = "https://"
            + MessengerConnectionService.UTIL_SERVER + "/v1/ckeys";
    private String content = null;
    private Context mContext;

    public RequestKeyTask(TaskCompleted fragmentCallback, Context ctx) {
        mFragmentCallback = fragmentCallback;
        mContext = ctx;
    }

    @Override
    public void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(String... values) {
    }

    @Override
    protected String doInBackground(String... urls) {
        String URL = null;
        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(mContext);
        }

        IntervalDB db = new IntervalDB(mContext);
        db.open();
        Cursor cursor = db.getSingleContact(3);
        if (cursor.getCount() > 0 && getValidationKey() == 0) {
            content = cursor.getString(cursor.getColumnIndexOrThrow(IntervalDB.COL_TIME));
            cursor.close();
            db.close();
        } else {
            Contact contact = messengerHelper.getMyContact();
            try {
                URL = REQUEST_KEYS_URL;
                HttpClient httpclient = HttpHelper.createHttpClient();
                HttpParams params = httpclient.getParams();
                HttpResponse response;

                HttpConnectionParams.setConnectionTimeout(params, REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(params, WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(params, WAIT_TIMEOUT);

                HttpPost httpPost = new HttpPost(URL);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("username", contact.getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("password", contact.getName()));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                response = httpclient.execute(httpPost);
                StatusLine statusLine = response.getStatusLine();

                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    content = out.toString();
                } else {
                    content = "null";
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }

            } catch (ClientProtocolException e) {
                content = "null";
            } catch (IOException e) {
                content = "null";
            } catch (Exception e) {
                content = "null";
            }
        }
        if (!content.equalsIgnoreCase("null") || content == null) {
            setKey(content);
        }

        return content;
    }

    @Override
    protected void onPostExecute(String results) {
        mFragmentCallback.onTaskDone(results);
    }

    public void setKey(String key) {
        IntervalDB db = new IntervalDB(mContext);
        db.open();

        Cursor cursor = db.getSingleContact(3);
        if (cursor.getCount() > 0) {
            db.deleteContact(3);
        }

        Interval interval = new Interval();
        interval.setId(3);
        interval.setTime(key);
        db.createContact(interval);
        db.close();
    }

    public int getValidationKey() {
        int error = 0;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String time_str = dateFormat.format(cal.getTime());
        String[] s = time_str.split(" ");

        int year_sys = Integer.parseInt(s[0].split("/")[0]);
        int month_sys = Integer.parseInt(s[0].split("/")[1]);
        int day_sys = Integer.parseInt(s[0].split("/")[2]);
        int hour_sys = Integer.parseInt(s[1].split(":")[0]);
        int min_sys = Integer.parseInt(s[1].split(":")[1]);

        IntervalDB db = new IntervalDB(mContext);
        db.open();
        Cursor cursor = db.getSingleContact(10);
        if (cursor.getCount() > 0) {
            String time_strDB = cursor.getString(cursor.getColumnIndexOrThrow(IntervalDB.COL_TIME));
            String[] sDB = time_strDB.split(" ");
            int year_sysDB = Integer.parseInt(sDB[0].split("/")[0]);
            int month_sysDB = Integer.parseInt(sDB[0].split("/")[1]);
            int day_sysDB = Integer.parseInt(sDB[0].split("/")[2]);
            int hour_sysDB = Integer.parseInt(sDB[1].split(":")[0]);
            int min_sysDB = Integer.parseInt(sDB[1].split(":")[1]);

            if (year_sysDB == year_sys) {
                if (month_sysDB == month_sys) {
                    if (day_sysDB == day_sys) {
                        if (hour_sysDB == hour_sys) {
                            if ((min_sys - min_sysDB) > 15) {
                                error = 1;
                            } else {
                                error = 0;
                            }
                        } else {
                            error = 1;
                        }
                    } else {
                        error = 1;
                    }
                } else {
                    error = 1;
                }
            } else {
                error = 1;
            }

        } else {
            Interval interval = new Interval();
            interval.setId(10);
            interval.setTime(time_str);
            db.createContact(interval);
            error = 1;
        }

        db.close();

        if (error == 1) {
            setTime();
        }

        return error;
    }

    public void setTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String time_str = dateFormat.format(cal.getTime());
        IntervalDB db = new IntervalDB(mContext);
        db.open();
        Cursor cursor = db.getSingleContact(10);
        if (cursor.getCount() > 0) {
            db.deleteContact(10);
        }
        Interval interval = new Interval();
        interval.setId(10);
        interval.setTime(time_str);
        db.createContact(interval);
        db.close();
    }

}