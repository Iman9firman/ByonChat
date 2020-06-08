package com.byonchat.android;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.createMeme.FilteringImage;
import com.byonchat.android.list.ListVoucherDetailAdapter;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.VouchersDB;
import com.byonchat.android.provider.VouchersDetailModel;
import com.byonchat.android.utils.HttpHelper;
import com.byonchat.android.utils.RequestKeyTask;
import com.byonchat.android.utils.TaskCompleted;
import com.byonchat.android.utils.ValidationsKey;

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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lukmanpryg on 6/30/2016.
 */
public class ActivityVouchersDetail extends AppCompatActivity {

    public final static String URL_GET_VOUCHER_DETAIL = "https://" + MessengerConnectionService.HTTP_SERVER + "/voucher/level2.php";
    Toolbar toolbar;
    TextView titleToolbar;
    private int color = 0;
    private Contact contact;
    private MessengerDatabaseHelper messengerHelper;
    ListVoucherDetailAdapter adapter;
    private Context context;
    private ListView lv;
    private ActivityVouchersDetail activity;
    private ArrayList<VouchersDetailModel> listItem;
    private SwipeRefreshLayout swipeRefreshLayout;
    VouchersDB vouchersDB;
    protected ProgressDialog pdialog;
    private BroadcastHandler broadcastHandler = new BroadcastHandler();
    RequestVouchersDetail requestVouchersDetail;
    String id, judul, bgcolor, textcolor, icon, background;
    String idd, judull, seriall, tgll, nominall, colorr, txtcolorr;

    //Lukman+
//    public static String TITLE[] = {
//            "Byonchat Voucher",
//            "MKG Vouchers",
//            "Alfamart Voucher",
//            "Planet Ban Service...",
//            "Nawilis Parts",
//            "Kids Meal Voucher",
//            "XXI Saturday..."
//    };
//
//    public static String COLOR[] = {
//            "#fbf7c3",
//            "#3bc93d",
//            "#3ba6c9",
//            "#c9543b",
//            "#3325ff",
//            "#fbf7c3",
//            "#3bc93d"
//    };
//
//    public static String VALUE[] = {
//            "15000",
//            "25000",
//            "10000",
//            "15000",
//            "25000",
//            "10000",
//            "15000"
//    };
    //Lukman-

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vouchers_detail_activity);

//        toolbar = (Toolbar) findViewById(R.id.toolbar);
        titleToolbar = (TextView) findViewById(R.id.title_toolbar);
        View view = findViewById(R.id.layout_back_button);

        if (getIntent().getExtras().containsKey("id")) {
            id = getIntent().getStringExtra("id");
            judul = getIntent().getStringExtra("judul");
            bgcolor = getIntent().getStringExtra("bgcolor");
            textcolor = getIntent().getStringExtra("textcolor");
            icon = getIntent().getStringExtra("icon");
//            Toast.makeText(ActivityVouchersDetail.this, id, Toast.LENGTH_SHORT).show();
        }

        color = getResources().getColor(R.color.colorPrimary);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        initBackground(judul, color);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (activity == null) {
            activity = ActivityVouchersDetail.this;
        }

        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(activity);
        }

        if (vouchersDB == null) {
            vouchersDB = new VouchersDB(activity);
        }

        lv = (ListView) findViewById(R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setColorSchemeColors(
                Color.GRAY, //This method will rotate
                Color.GRAY, //colors given to it when
                Color.GRAY,//loader continues to
                Color.GRAY);//refresh.
        //  swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.gray);

        requestVouchersDetail = new RequestVouchersDetail(context);

        if (pdialog == null) {
            pdialog = new ProgressDialog(activity);
            pdialog.setIndeterminate(true);
            pdialog.setMessage("Please wait a moment");
            pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }

        adapter = new ListVoucherDetailAdapter(activity, listItem);
        contact = messengerHelper.getMyContact();

        vouchersDB.open();
        listItem = vouchersDB.retriveVouchersDetail();
        vouchersDB.close();

        if (listItem.size() > 0) {
            refreshList();
        }else{
            requestKey();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                requestKey();
                                swipeRefreshLayout.setRefreshing(true);
                            }
                        }
                );
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                idd = String.valueOf(position);
                judull = listItem.get(position).getJudul();
                background = listItem.get(position).getBackground();
                seriall = listItem.get(position).getSerial_number();
                tgll = listItem.get(position).getTgl_valid();
                nominall = listItem.get(position).getValue();
                colorr = listItem.get(position).getColor();
                txtcolorr = listItem.get(position).getTextcolor();

                if (NetworkInternetConnectionStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
                    LoadImageFromURL loadImage = new LoadImageFromURL();
                    loadImage.execute();
                }else{
                    Toast.makeText(ActivityVouchersDetail.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        IntentFilter f = new IntentFilter(MessengerConnectionService.ACTION_REFRESH_VOUCHERS);
        f.setPriority(1);
        activity.registerReceiver(broadcastHandler, f);
        refreshList();
        super.onResume();
    }

    @Override
    public void onPause() {
        requestVouchersDetail.cancel(true);
        activity.unregisterReceiver(broadcastHandler);
        super.onPause();
    }

    private void requestKey() {
        RequestKeyTask testAsyncTask = new RequestKeyTask(new TaskCompleted() {
            @Override
            public void onTaskDone(String key) {
                if (key.equalsIgnoreCase("null")) {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(activity, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                } else {
                    requestVouchersDetail = new RequestVouchersDetail(activity);
                    requestVouchersDetail.execute(key);
                }
            }
        }, activity);

        testAsyncTask.execute();
    }

    public void refreshList() {

        vouchersDB.open();
        listItem = vouchersDB.retriveVouchersDetail();
        vouchersDB.close();
        adapter = new ListVoucherDetailAdapter(activity, listItem);
        lv.setAdapter(adapter);
    }

    class BroadcastHandler extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MessengerConnectionService.ACTION_REFRESH_VOUCHERS
                    .equals(intent.getAction())) {
                refreshList();
            }
        }
    }

    class RequestVouchersDetail extends AsyncTask<String, Void, String> {

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private String content = null;
        private boolean error = false;
        private Context mContext;

        public RequestVouchersDetail(Context context) {
            this.mContext = context;
        }


        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);
        }

        protected String doInBackground(String... key) {
            try {

//                Log.w("pocer", key[0]+" "+key[1]);
                HttpClient httpClient = HttpHelper
                        .createHttpClient(mContext);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);

                nameValuePairs.add(new BasicNameValuePair("bcid", contact.getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("key", key[0]));
                nameValuePairs.add(new BasicNameValuePair("pemilik", judul));
                nameValuePairs.add(new BasicNameValuePair("mulai", "0"));

                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                HttpPost post = new HttpPost(URL_GET_VOUCHER_DETAIL);
                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                //Response from the Http Request
                HttpResponse response = httpClient.execute(post);
                StatusLine statusLine = response.getStatusLine();
                //Check the Http Request for success
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    content = out.toString();
                    JSONArray result = new JSONArray(content);
                    vouchersDB.open();
                    vouchersDB.deleteVouchersDetail();
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject obj = result.getJSONObject(i);
                        String serial_number = obj.getString("serial_number");
                        String pemilik = obj.getString("pemilik");
                        String judul = obj.getString("judul");
                        String nominal = obj.getString("nominal");
                        String tgl_valid = obj.getString("tgl_valid");
                        String backg = obj.getString("background");
                        VouchersDetailModel model = new VouchersDetailModel(String.valueOf(i), serial_number, pemilik, judul, nominal, tgl_valid, icon, "2", bgcolor, textcolor, backg);
                        vouchersDB.insertVouchersDetail(model);
                        if (isCancelled()) break;
                    }
                } else {
                    //Closes the connection.
                    error = true;
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
            swipeRefreshLayout.setRefreshing(false);
        }

        protected void onPostExecute(String content) {
            if (error) {
                if (content.contains("invalid_key")) {
                    if (NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)) {
                        String key = new ValidationsKey().getInstance(mContext).key(true);
                        if (key.equalsIgnoreCase("null")) {
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(mContext, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                        } else {
                            requestVouchersDetail = new RequestVouchersDetail(context);
                            requestVouchersDetail.execute(key);
                        }
                    } else {
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(mContext, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                }
            } else {
                swipeRefreshLayout.setRefreshing(false);
                refreshList();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initBackground(String title, int color) {
        toolbar.setTitle(title);
        Bitmap back_default = FilteringImage.headerColor(getWindow(), ActivityVouchersDetail.this, color);
        Drawable back_draw_default = new BitmapDrawable(getResources(), back_default);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            toolbar.setBackground(back_draw_default);
        } else {
            toolbar.setBackgroundDrawable(back_draw_default);
        }
    }

    public class LoadImageFromURL extends AsyncTask<String, Void, Bitmap> {

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private String content = null;
        private boolean error = false;
        private Context mContext;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ActivityVouchersDetail.this);
            progressDialog.setCancelable(true);
            progressDialog.setMessage("Please wait...");
            progressDialog.setProgress(0);
            progressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            // TODO Auto-generated method stub

            try {
                HttpClient httpClient = HttpHelper
                        .createHttpClient(mContext);
                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                URL url = new URL(background);
                InputStream is = url.openConnection().getInputStream();
                Bitmap bitMap = BitmapFactory.decodeStream(is);

                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap rotated = Bitmap.createBitmap(bitMap, 0, 0, bitMap.getWidth(), bitMap.getHeight(),
                        matrix, true);

                return rotated;

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            FragmentManager fm = getSupportFragmentManager();
            DialogFragment testDialog = DialogVouchersPic.newInstance(idd, judull, seriall, tgll, nominall, colorr, txtcolorr, background, result);
            testDialog.setRetainInstance(true);
            testDialog.show(fm, "DialogVouchersPic");
            progressDialog.dismiss();
        }

    }
}
