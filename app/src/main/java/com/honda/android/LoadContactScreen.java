package com.honda.android;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.honda.android.communication.MessengerConnectionService;
import com.honda.android.communication.NetworkInternetConnectionStatus;
import com.honda.android.createMeme.FilteringImage;
import com.honda.android.provider.Configuration;
import com.honda.android.provider.Contact;
import com.honda.android.provider.Interval;
import com.honda.android.provider.IntervalDB;
import com.honda.android.provider.MessengerDatabaseHelper;
import com.honda.android.ui.activity.MainActivityNew;
import com.honda.android.utils.HttpHelper;
import com.honda.android.utils.MediaProcessingUtil;
import com.honda.android.utils.ValidationsKey;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class LoadContactScreen extends AppCompatActivity implements ServiceConnection {
    public static Uri CONTACT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    public static final String ACTION_CONTACT_REFRESHED = MainActivity.class
            .getName() + ".contactRefreshed";
    public static final String BUNDLEKEY_MSISDN = "com.example.messenger.MainActivity.msisdn";
    private static final SimpleDateFormat dateInfoFormat = new SimpleDateFormat(
            "dd/MM/yyyy", Locale.getDefault());
    private static final SimpleDateFormat hourInfoFormat = new SimpleDateFormat(
            "HH:mm", Locale.getDefault());
    private static String REQUEST_STATUS_URL = "https://"
            + MessengerConnectionService.FILE_SERVER + "/temantemanku.php";
    private static String REQUEST_CONTACT_URL = "https://"
            + MessengerConnectionService.UTIL_SERVER + "/v1/users/friends";

    private static String REQUEST_KEYS_URL = "https://"
            + MessengerConnectionService.UTIL_SERVER + "/v1/keys";

    private MessengerDatabaseHelper messengerHelper;
    private MessengerConnectionService.MessengerConnectionBinder binder;
    private HashMap<String, Contact> contacts = new HashMap<String, Contact>();
    private boolean contactIsRefereshing = false;

    private static final String SQL_SELECT_CONTACTS = "SELECT * FROM "
            + Contact.TABLE_NAME + " WHERE _id > 1 order by lower(" + Contact.NAME + ")";

    Context context;
    ProgressBar progressBar;
    TextView textHeader;
    ImageView iv;
    private String regId = "";
    private int[] images = {R.drawable.bg_membership, R.drawable.bg_sroom, R.drawable.bg_vouchers};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        if (!isNetworkConnectionAvailable()) {
            setContentView(R.layout.custom_information);
            ((TextView) findViewById(R.id.customInformationText))
                    .setText(R.string.registration_no_internet);
        } else {
            setContentView(R.layout.activity_load_contact_screen);
            FilteringImage.headerColor(getWindow(), LoadContactScreen.this, getResources().getColor(R.color.colorPrimary));
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            textHeader = (TextView) findViewById(R.id.textHeader);
            if (messengerHelper == null) {
                messengerHelper = MessengerDatabaseHelper.getInstance(this);
            }

            if (!MessengerConnectionService.started) {
                MessengerConnectionService.startService(this);
            }

            context = this;
            new ContactRefreshHandler(true).start();
            iv = (ImageView) findViewById(R.id.image_view);

            final Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);

            final Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                int i = 0;

                public void run() {
                    iv.startAnimation(animationFadeIn);
                    iv.setImageResource(images[i]);
                    i++;
                    if (i > images.length - 1) {
                        i = 0;
                    }
                    handler.postDelayed(this, 3000);
                }
            };
            handler.postDelayed(runnable, 3000);

            final Handler handlerProgresbar = new Handler();
            Runnable runnableProgressbar = new Runnable() {
                int i = 0;

                public void run() {
                    progressBar.setProgress(progressBar.getProgress() + 1);
                    handlerProgresbar.postDelayed(this, 1000);
                    if (progressBar.getProgress() == 100) {

                    }
                }
            };
            handlerProgresbar.postDelayed(runnableProgressbar, 1000);
        }
    }

    private boolean isNetworkConnectionAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null)
            return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }

    @Override
    public void onServiceConnected(ComponentName compName, IBinder iBinder) {
        binder = (MessengerConnectionService.MessengerConnectionBinder) iBinder;

    }

    @Override
    public void onServiceDisconnected(ComponentName arg0) {
        binder = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getApplicationContext().bindService(
                new Intent(this, MessengerConnectionService.class), this,
                Context.BIND_AUTO_CREATE);
    }

    class ContactRefreshHandler extends Thread {
        private static final long REFRESH_TIME = /*86400*/5000;
        boolean checkLastRefresh = false;

        public ContactRefreshHandler() {
        }

        public ContactRefreshHandler(boolean checkLastRefresh) {
            this.checkLastRefresh = checkLastRefresh;
        }

        @Override
        public void run() {
            if (checkLastRefresh) {
                long lastUpdate = 0;
                try {
                    lastUpdate = Long.parseLong(Configuration.getValue(
                            messengerHelper,
                            Configuration.LAST_CONTACT_REFRESHED));
                } catch (NumberFormatException nfe) {
                }
                if ((System.currentTimeMillis() - lastUpdate) < REFRESH_TIME) {
                    sendContactRefreshedBroadcast(false);
                    return;
                }
            }

            HashMap<Long, Contact> osMap = loadContactFromOs();
            if (osMap.size() == 0) {
                messengerHelper.execSql(
                        getString(R.string.delete_all_contacts), null);
                sendContactRefreshedBroadcast(true);
                return;
            }

            StringBuilder sbuffer = new StringBuilder();
            sbuffer.append(messengerHelper.getMyContact().getJabberId())
                    .append(",")
                    .append(messengerHelper.getMyContact().getName())
                    .append(",");
            for (Long number : osMap.keySet()) {
                sbuffer.append(number.toString()).append(",");
            }
            sbuffer.setLength(sbuffer.length() - 1);

            InputStreamReader reader = null;
            String[] arrTemp;
            try {
                HttpPost post = new HttpPost(REQUEST_CONTACT_URL);
                post.setEntity(new StringEntity(sbuffer.toString()));

                HttpClient httpClient = HttpHelper
                        .createHttpClient(context);
                HttpResponse response = httpClient.execute(post);
                reader = new InputStreamReader(response.getEntity()
                        .getContent(), "UTF-8");
                int r;
                StringBuilder buf = new StringBuilder();
                while ((r = reader.read()) != -1) {
                    buf.append((char) r);
                }

                if (response.getStatusLine().getStatusCode() != 200) {
                    Log.e(getLocalClassName(),
                            "Failed getting contact from server: "
                                    + buf.toString()
                    );
                    sendContactRefreshedBroadcast(false);
                    return;
                }
                arrTemp = buf.toString().split(",");
            } catch (Exception e) {
                Log.e(getLocalClassName(),
                        "Failed getting contact from server: " + e.getMessage(),
                        e);
                sendContactRefreshedBroadcast(false);
                return;
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                    }
                }
            }
            long[] sortedAppFriends = new long[arrTemp.length];
            int i = 0;
            for (String s : arrTemp) {
                String curNumber = s.trim();
                if (!curNumber.equals("")) {
                    try {
                        sortedAppFriends[i] = Long.parseLong(curNumber);
                        i++;
                    } catch (NumberFormatException nfe) {
                    }
                }
            }
            Arrays.sort(sortedAppFriends);

            HashMap<Long, Contact> dbMap = loadContactFromDb();
            long[] arrDb = new long[dbMap.size()];
            i = 0;
            for (Long l : dbMap.keySet()) {
                arrDb[i] = l;
                i++;
            }
            Arrays.sort(arrDb);

            while (true) {
                if (binder != null && binder.isConnected()) {
                    break;
                } else {
                    try {
                        sleep(200);
                    } catch (InterruptedException ie) {
                    }
                }
            }

            // Uncomment the following line for SMS bridge feature:
            // ArrayList<Long> deleteOrKeepBucket = new ArrayList<Long>();
            // Comment the following line for SMS bridge feature:
            ArrayList<String> deleteBucket = new ArrayList<String>();
            int indexDb = 0;
            int indexAppFriends = 0;
            // Loop through the whole app-using-friends numbers
            while (indexAppFriends < sortedAppFriends.length) {
                long curAppFriendNumber = sortedAppFriends[indexAppFriends];
                if (curAppFriendNumber == 0) {
                    indexAppFriends++;
                    continue;
                }
                boolean processed = false;
                Contact cOS = osMap.get(curAppFriendNumber);
                if (indexDb < arrDb.length) {
                    // There were contacts in local table
                    long curDbNumber = arrDb[indexDb];
                    if (curAppFriendNumber < curDbNumber) {
                        // The app-using-friend number is smaller than current
                        // row in local table
                        // This means this number is added after the last
                        // contact refresh
                        if (binder.isConnected()) {
                            try {
                                binder.addNewContact(cOS);
                            } catch (Exception e) {
                                Log.e(getClass().getSimpleName(),
                                        "Fail adding contact with jabberID: "
                                                + curAppFriendNumber, e
                                );
                            }
                        }
                        processed = true;
                    } else {
                        Contact contact = dbMap.get(curDbNumber);

                        if (curAppFriendNumber == curDbNumber) {
                            // The comment below is for SMS bridge feature:
                            // This means previous non-app-using-friend started
                            // using app
                            if (!osMap.get(curAppFriendNumber).equals(
                                    contact.getName())
                                    ) {
                                contact.setName(cOS.getName());
                                // Comment the following line for SMS bridge
                                // feature:
                                messengerHelper.updateData(contact);
                            }
                            // Uncomment the following 2 lines for SMS bridge
                            // feature:
                            // contact.setType(0);
                            // messengerHelper.updateData(contact);
                            if (binder.isConnected()) {
                                try {
                                    binder.loadAvatar(contact);
                                } catch (Exception e) {
                                    Log.e(getClass().getSimpleName(),
                                            "Failed loading avatar for jabberID: "
                                                    + curAppFriendNumber, e
                                    );
                                }
                            }
                            processed = true;
                        } else {
                            // The comment below is for SMS bridge feature:
                            // Collect for review. They are either no longer in
                            // OS or somehow the server says it's no longer
                            // using app
                            // deleteOrKeepBucket.add(Long.valueOf(contact
                            // .getJabberId()));
                            deleteBucket.add(contact.getJabberId());
                        }
                        indexDb++;
                    }
                } else {
                    // Local contacts table was empty or the new app-using
                    // numbers are bigger than the last number in DB
                    if (binder.isConnected()) {
                        try {
                            binder.addNewContact(cOS);
                        } catch (Exception e) {
                            Log.e(getClass().getSimpleName(),
                                    "Fail adding contact for jabberID: "
                                            + curAppFriendNumber, e
                            );
                        }
                    }
                    processed = true;
                }
                if (processed) {
                    // Uncomment the following line for SMS bridge feature
                    // osMap.remove(curAppFriendNumber);
                    indexAppFriends++;
                }
            }
            String sql = "DELETE FROM contacts WHERE jid IN";
            sbuffer = new StringBuilder("");
            for (int j = indexDb; j < arrDb.length; j++) {
                Contact contact = dbMap.get(Long.valueOf(arrDb[j]));
                deleteBucket.add(contact.getJabberId());
            }
            if (deleteBucket.size() > 0) {
                i = 0;
                String[] jids = new String[deleteBucket.size()];
                while (i < deleteBucket.size()) {
                    String jabberId = deleteBucket.get(i);
                    File f = getFileStreamPath(MediaProcessingUtil
                            .getProfilePicName(new Contact(jabberId, jabberId,
                                    "")));
                    if (f.exists()) {
                        f.delete();
                    }
                    sbuffer.append("?");
                    jids[i] = jabberId;
                    i++;
                    if (i != deleteBucket.size())
                        sbuffer.append(",");
                }
                sql += "(" + sbuffer.toString() + ");";
                messengerHelper.execSql(sql, jids);
            }
            sendContactRefreshedBroadcast(true);
        }

        private void sendContactRefreshedBroadcast(boolean updateLastRefresh) {
            progressBar.setProgress(30);
            if (updateLastRefresh) {
                Configuration.setValue(messengerHelper,
                        Configuration.LAST_CONTACT_REFRESHED,
                        String.valueOf(System.currentTimeMillis()));
            }
            if (NetworkInternetConnectionStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
                String key = new ValidationsKey().getInstance(getApplicationContext()).key(true);
                if (key.equalsIgnoreCase("null")) {
                    finish();
                    //       Toast.makeText(getApplicationContext(), R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                } else {
                    HashMap<Long, Contact> osMap = loadContactFromOs();
                    if (osMap.size() == 0) {

                        IntervalDB db = new IntervalDB(getApplicationContext());
                        db.open();
                        Cursor cursor = db.getSingleContact(12);
                        if (cursor.getCount() > 0) {
                            db.deleteContact(12);
                        }
                        cursor.close();
                        Interval interval = new Interval();
                        interval.setId(12);
                        interval.setTime("Finalizing");
                        db.createContact(interval);
                        db.close();

                        progressBar.setProgress(90);
                        finish();
                        Intent i = new Intent();
                        i.setClass(getApplicationContext(), MainActivityNew.class);
                        startActivity(i);
                    } else {
                        HashMap<Long, Contact> dbMap = loadContactFromDb();
                        if (dbMap.size() == 0) {
                            IntervalDB db = new IntervalDB(getApplicationContext());
                            db.open();
                            Cursor cursor = db.getSingleContact(12);
                            if (cursor.getCount() > 0) {
                                db.deleteContact(12);
                            }
                            cursor.close();
                            Interval interval = new Interval();
                            interval.setId(12);
                            interval.setTime("Finalizing");
                            db.createContact(interval);
                            db.close();
                            progressBar.setProgress(90);
                            finish();
                            Intent i = new Intent();
                            i.setClass(getApplicationContext(), MainActivityNew.class);
                            startActivity(i);
                        } else {
                            new searchThemeRequest(getApplicationContext()).execute(key);
                        }
                    }
                }
            } else {
                finish();
                //      Toast.makeText(getApplicationContext(), R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
            }
        }

    }

    private HashMap<Long, Contact> loadContactFromDb() {
        Contact contact = new Contact();
        Cursor cursor = messengerHelper.query(SQL_SELECT_CONTACTS, null);

        HashMap<Long, Contact> dbMap = new HashMap<Long, Contact>();
         /*long id = 0;
         while (cursor.moveToNext()) {
             contact = new Contact(cursor);
             dbMap.put(id, contact);
             id++;
         }*/
        while (cursor.moveToNext()) {

            contact = new Contact(cursor);
            dbMap.put(Long.valueOf(contact.getJabberId()), contact);
        }
        cursor.close();
        return dbMap;
    }

    private HashMap<Long, Contact> loadContactFromOs() {
        String[] projection = new String[]{
                ContactsContract.Contacts.LOOKUP_KEY,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor cursor = getContentResolver().query(CONTACT_URI, projection,
                null, null, null);
        int indexId = cursor.getColumnIndex(projection[0]);
        int indexName = cursor.getColumnIndex(projection[1]);
        int indexMnumber = cursor.getColumnIndex(projection[2]);
        HashMap<Long, Contact> osMap = new HashMap<Long, Contact>();
        while (cursor.moveToNext()) {
            String id = cursor.getString(indexId);
            String name = cursor.getString(indexName);
            String mnumber = cursor.getString(indexMnumber).replace(" ", "")
                    .replace("-", "");

            if (mnumber.startsWith("0")) {
                mnumber = mnumber.replaceFirst("0", "62");
            } else if (mnumber.startsWith("+")) {
                mnumber = mnumber.replaceFirst("\\+", "");
            }

            if (messengerHelper.getMyContact().getJabberId().equals(mnumber))
                continue;

            try {
                Contact c = new Contact(name, mnumber, "");
                c.setAddrbookId(id);
                osMap.put(Long.parseLong(mnumber), c);
            } catch (NumberFormatException nfe) {

            }
        }
        cursor.close();
        return osMap;
    }

    class searchThemeRequest extends AsyncTask<String, Void, String> {

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();

        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private JSONObject jObject;
        private String jsonResult = "";
        JSONArray menuitemArray;
        private Context mContext;
        private String content = null;
        private boolean error = false;

        public searchThemeRequest(Context context) {
            this.mContext = context;
            progressBar.setProgress(60);
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
                nameValuePairs.add(new BasicNameValuePair("key", key[0]));
                String name = "";
                HashMap<Long, Contact> dbMap = loadContactFromDb();
                for (Map.Entry<Long, Contact> entry : dbMap.entrySet()) {
                    name += entry.getKey() + ",";
                }
                nameValuePairs.add(new BasicNameValuePair("temanteman", name.substring(0, name.length() - 1)));

                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                HttpPost post = new HttpPost(REQUEST_STATUS_URL);
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

                    jObject = new JSONObject(content);
                    JSONArray menuitemArray = jObject.getJSONArray("status");
                    for (int i = 0; i < menuitemArray.length(); i++) {
                        String number = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("userid").toString()));
                        String status = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("status").toString()));
                        Contact contact = messengerHelper.getContact(number);
                        if (status.isEmpty()) {
                            contact.setStatus(null);
                        } else {
                            contact.setStatus(status);
                        }
                        messengerHelper.updateData(contact);
                    }
                } else {
                    //Closes the connection.
                    content = statusLine.getReasonPhrase();
                    response.getEntity().getContent().close();
                    throw new IOException(content);
                }

            } catch (ClientProtocolException e) {
                content = e.getMessage();
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
                if (content != null && content.contains("invalid_key")) {
                    if (NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)) {
                        String key = new ValidationsKey().getInstance(mContext).key(true);
                        if (key.equalsIgnoreCase("null")) {
                            finish();
                        } else {
                            new searchThemeRequest(mContext).execute(key);
                        }
                    } else {
                        finish();
                    }
                } else {
                    registerGroup();
                }
            } else {
                registerGroup();

            }
        }

    }

    public void registerGroup() {
        progressBar.setProgress(90);
        IntervalDB db = new IntervalDB(getApplicationContext());
        db.open();
        Cursor cursor = db.getSingleContact(12);
        if (cursor.getCount() > 0) {
            db.deleteContact(12);
        }
        cursor.close();
        Interval interval = new Interval();
        interval.setId(12);
        interval.setTime("Finalizing");
        db.createContact(interval);
        db.close();
        finish();
        Intent i = new Intent();
        i.setClass(getApplicationContext(), MainActivityNew.class);
        startActivity(i);

        cursor.close();
        db.close();

    }
}