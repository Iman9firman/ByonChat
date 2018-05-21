package com.byonchat.android.smsSolders;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.R;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.Interval;
import com.byonchat.android.provider.IntervalDB;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.utils.RequestKeyTask;
import com.byonchat.android.utils.TaskCompleted;
import com.innodroid.expandablerecycler.ExpandableRecyclerAdapter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HistorySmsActivity extends AppCompatActivity {

    SwipeRefreshLayout swipeLayout;
    ReportingAdapter adapter;
    Context mContext;
    RecyclerView recycler;

    private Calendar calendar;
    private int year, month, day;
    TextView start, end;
    private static final int DATE_DIALOG_ID = 1;
    private static final int DATE_DIALOG_END = 2;
    ImageButton btnMorph1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_sms);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("History");
        toolbar.setBackgroundColor(Color.RED);
        setSupportActionBar(toolbar);
        mContext = HistorySmsActivity.this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.RED);
        }

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        start = (TextView) findViewById(R.id.start);
        end = (TextView) findViewById(R.id.end_date);
        btnMorph1 = (ImageButton) findViewById(R.id.btnMorph1);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recycler = (RecyclerView) findViewById(R.id.main_recycler);

        adapter = new ReportingAdapter(this);
        adapter.setMode(ExpandableRecyclerAdapter.MODE_ACCORDION);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (start.getText().toString().length() == 0) {
                    start.setError("harap di isi");
                    swipeLayout.setRefreshing(false);
                    return;
                }
                if (end.getText().toString().length() == 0) {
                    end.setError("harap di isi");
                    swipeLayout.setRefreshing(false);
                    return;
                }

                RequestKeyTask testAsyncTask = new RequestKeyTask(new TaskCompleted() {
                    @Override
                    public void onTaskDone(String key) {
                        new Refresh(HistorySmsActivity.this).execute("https://bb.byonchat.com/smsgateway/sejarah.php", key, start.getText().toString(), end.getText().toString());
                    }
                }, getApplicationContext());
                testAsyncTask.execute();

            }
        });

        btnMorph1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (start.getText().toString().length() == 0) {
                    start.setError("harap di isi");
                    return;
                }
                if (end.getText().toString().length() == 0) {
                    end.setError("harap di isi");
                    return;
                }

                RequestKeyTask testAsyncTask = new RequestKeyTask(new TaskCompleted() {
                    @Override
                    public void onTaskDone(String key) {
                        new Refresh(HistorySmsActivity.this).execute("https://bb.byonchat.com/smsgateway/sejarah.php", key, start.getText().toString(), end.getText().toString());
                    }
                }, getApplicationContext());
                testAsyncTask.execute();

            }
        });


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
                showDialog(DATE_DIALOG_ID);
            }
        });

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
                showDialog(DATE_DIALOG_END);
            }
        });
    }

    DatePickerDialog.OnDateSetListener myDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker datePicker, int i, int j, int k) {

            year = i;
            month = j;
            day = k;
            String c = new StringBuilder().append(year).append("-")
                    .append(month + 1).append("-").append(day).toString();
            start.setError(null);
            start.setText(c);
        }
    };

    DatePickerDialog.OnDateSetListener myDateSetListenerEnd = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker datePicker, int i, int j, int k) {

            year = i;
            month = j;
            day = k;
            String c = new StringBuilder().append(year).append("-")
                    .append(month + 1).append("-").append(day).toString();
            end.setError(null);
            end.setText(c);
        }
    };


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, myDateSetListener, year, month, day);
            case DATE_DIALOG_END:
                return new DatePickerDialog(this, myDateSetListenerEnd, year, month, day);
        }
        return null;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class Refresh extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        String error = "";
        private Activity activity;
        private Context context;

        public Refresh(Activity activity) {
            this.activity = activity;
            context = activity;
            dialog = new ProgressDialog(activity);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Loading...");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            postData(params[0], params[1], params[2], params[3]);
            return null;
        }

        protected void onPostExecute(String result) {
            swipeLayout.setRefreshing(false);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            Log.w("ico","1");
            JSONObject jObject = null;
            try {
                jObject = new JSONObject(error);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (jObject != null) {
                Log.w("ico","2");
                if (jObject.has("code")) {
                    Log.w("ico","3");
                    String buka = null;
                    try {
                        Log.w("ico","4");
                        buka = jObject.getString("code");
                        String desc = jObject.getString("description");
                        if (buka.equalsIgnoreCase("404")) {
                            Toast.makeText(context, desc, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.w("ico","5");
                    JSONArray jsonArray;
                    try {
                        Log.w("ico","6");
                        // {"jumlah_sms":26,"history":[{"kirim_ke":"6287888822274","pesan":"test aja. ga usah dikirim","tgl_pesan":"2017-12-18 15:28:52","tgl_kirim":null,"tgl_sampai":null,"status":"SMS Baru","point":10},{"kirim_ke":"628818121478","pesan":"test kirim. jangan dikirim bener2","tgl_pesan":"2017-12-18 15:28:52","tgl_kirim":null,"tgl_sampai":null,"status":"SMS Baru","point":15},{"kirim_ke":"6287888822274","pesan":"test aja. ga usah dikirim","tgl_pesan":"2017-12-18 16:42:50","tgl_kirim":null,"tgl_sampai":null,"status":"SMS Baru","point":10},{"kirim_ke":"628818121478","pesan":"test kirim. jangan dikirim bener2","tgl_pesan":"2017-12-18 16:42:50","tgl_kirim":null,"tgl_sampai":null,"status":"SMS Baru","point":15},{"kirim_ke":"628977364026","pesan":"test kirim. jangan dikirim bener2","tgl_pesan":"2017-12-18 16:56:51","tgl_kirim":null,"tgl_sampai":null,"status":"SMS Baru","point":15},{"kirim_ke":"6287888822274","pesan":"test aja. ga usah dikirim","tgl_pesan":"2017-12-18 16:56:51","tgl_kirim":null,"tgl_sampai":null,"status":"SMS Baru","point":10},{"kirim_ke":"6287888822274","pesan":"test aja. ga usah dikirim","tgl_pesan":"2017-12-18 16:56:51","tgl_kirim":null,"tgl_sampai":null,"status":"SMS Baru","point":10},{"kirim_ke":"6287888822274","pesan":"test aja. ga usah dikirim","tgl_pesan":"2017-12-18 16:56:51","tgl_kirim":null,"tgl_sampai":null,"status":"SMS Baru","point":10},{"kirim_ke":"628818121478","pesan":"test kirim. jangan dikirim bener2","tgl_pesan":"2017-12-18 16:56:51","tgl_kirim":null,"tgl_sampai":null,"status":"SMS Baru","point":15},{"kirim_ke":"6287888822274","pesan":"test aja. ga usah dikirim","tgl_pesan":"2017-12-18 16:56:51","tgl_kirim":null,"tgl_sampai":null,"status":"SMS Baru","point":10},{"kirim_ke":"628977364026","pesan":"test kirim. jangan dikirim bener2","tgl_pesan":"2017-12-18 16:56:51","tgl_kirim":null,"tgl_sampai":null,"status":"SMS Baru","point":15},{"kirim_ke":"6287888822274","pesan":"test aja. ga usah dikirim","tgl_pesan":"2017-12-18 16:56:51","tgl_kirim":null,"tgl_sampai":null,"status":"SMS Baru","point":10},{"kirim_ke":"6287888822274","pesan":"test aja. ga usah dikirim","tgl_pesan":"2017-12-18 16:56:51","tgl_kirim":null,"tgl_sampai":null,"status":"SMS Baru","point":10},{"kirim_ke":"628818121478","pesan":"test kirim. jangan dikirim bener2","tgl_pesan":"2017-12-18 16:56:51","tgl_kirim":null,"tgl_sampai":null,"status":"SMS Baru","point":15},{"kirim_ke":"6287888822274","pesan":"test aja. ga usah dikirim","tgl_pesan":"2017-12-18 16:56:51","tgl_kirim":null,"tgl_sampai":null,"status":"SMS Baru","point":10},{"kirim_ke":"6287888822274","pesan":"test aja. ga usah dikirim","tgl_pesan":"2017-12-18 16:56:51","tgl_kirim":null,"tgl_sampai":null,"status":"SMS Baru","point":10},{"kirim_ke":"628977364026","pesan":"test kirim. jangan dikirim bener2","tgl_pesan":"2017-12-18 16:56:51","tgl_kirim":null,"tgl_sampai":null,"status":"SMS Baru","point":15},{"kirim_ke":"628977364026","pesan":"test kirim. jangan dikirim bener2","tgl_pesan":"2017-12-18 16:56:51","tgl_kirim":null,"tgl_sampai":null,"status":"SMS Baru","point":15},{"kirim_ke":"6287888822274","pesan":"test aja. ga usah dikirim","tgl_pesan":"2017-12-18 16:56:51","tgl_kirim":null,"tgl_sampai":null,"status":"SMS Baru","point":10},{"kirim_ke":"6287888822274","pesan":"test aja. ga usah dikirim","tgl_pesan":"2017-12-18 16:56:51","tgl_kirim":null,"tgl_sampai":null,"status":"SMS Baru","point":10},{"kirim_ke":"628818121478","pesan":"test kirim. jangan dikirim bener2","tgl_pesan":"2017-12-18 16:56:51","tgl_kirim":null,"tgl_sampai":null,"status":"SMS Baru","point":15},{"kirim_ke":"628977364026","pesan":"test kirim. jangan dikirim bener2","tgl_pesan":"2017-12-18 16:56:51","tgl_kirim":null,"tgl_sampai":null,"status":"SMS Baru","point":15},{"kirim_ke":"628977364026","pesan":"test kirim. jangan dikirim bener2","tgl_pesan":"2017-12-18 16:56:51","tgl_kirim":null,"tgl_sampai":null,"status":"SMS Baru","point":15},{"kirim_ke":"6287888822274","pesan":"test aja. ga usah dikirim","tgl_pesan":"2017-12-18 16:56:51","tgl_kirim":null,"tgl_sampai":null,"status":"SMS Baru","point":10},{"
                        jsonArray = jObject.getJSONArray("history");
                        Log.w("never",jsonArray.length()+"");
                        List<ReportingAdapter.PeopleListItem> items = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String kirim_ke = jsonArray.getJSONObject(i).getString("kirim_ke");
                            String pesan = jsonArray.getJSONObject(i).getString("pesan");
                            String tgl_pesan = jsonArray.getJSONObject(i).getString("tgl_pesan");
                            String tgl_kirim = jsonArray.getJSONObject(i).getString("tgl_kirim");
                            String tgl_sampai = jsonArray.getJSONObject(i).getString("tgl_sampai");
                            String status = jsonArray.getJSONObject(i).getString("status");
                            String harga = jsonArray.getJSONObject(i).getString("point");

                            items.add(new ReportingAdapter.PeopleListItem(String.valueOf(i + 1), tgl_pesan, status, harga));
                            items.add(new ReportingAdapter.PeopleListItem(tgl_kirim + "  -->  " + tgl_sampai + "  -->  \n dikirim ke = " + kirim_ke));
                        }
                        adapter.addData(items);
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                }
            } else {
                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
            }

        }

        protected void onProgressUpdate(String... string) {
        }

        public void postData(String valueIWantToSend, String kye, String tangMu, String tangSe) {
            try {
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 13000);
                HttpConnectionParams.setSoTimeout(httpParameters, 15000);
                HttpClient httpclient = new DefaultHttpClient(httpParameters);
                HttpPost httppost = new HttpPost(valueIWantToSend);

                MessengerDatabaseHelper messengerHelper = null;
                if (messengerHelper == null) {
                    messengerHelper = MessengerDatabaseHelper.getInstance(context);
                }

                Contact contact = messengerHelper.getMyContact();
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("username", contact.getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("tgl_mulai", tangMu));
                nameValuePairs.add(new BasicNameValuePair("tgl_selesai", tangSe));
                nameValuePairs.add(new BasicNameValuePair("key", kye));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                int status = response.getStatusLine().getStatusCode();
                Log.w("hasil1", status + "");
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);
                    Log.w("hasil2", data + "");
                    error = data;
                } else {
                    error = "Tolong periksa koneksi internet.";
                }

            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                HistorySmsActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Tolong periksa koneksi internet.", Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
            } catch (ClientProtocolException e) {
                HistorySmsActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Tolong periksa koneksi internet.", Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
                // TODO Auto-generated catch block
            } catch (IOException e) {
                HistorySmsActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Tolong periksa koneksi internet.", Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
                // TODO Auto-generated catch block
            }
        }

    }


}
