package com.byonchat.android.ISSActivity.Reliever;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.ConversationActivity;
import com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity;
import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.list.IconItem;
import com.byonchat.android.provider.ChatParty;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.utils.ValidationsKey;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class SubmitRequestActivity extends AppCompatActivity {
    private TextView textSubmit, txtPlace, txtTMulai, txtTselesai, txtJob, textCheckin;
    private Button btnDirection, btnSubmit, btnCheckIn;
    private ImageView btnCall, btnCallDua, btnChat;
    private ImageView icon, back;
    private FrameLayout emblem;
    private Toolbar toolbar;
    private TextView tittle;
    Double latitude;
    Double longitude;
    String telpnReq, id, status;
    LinearLayout layout_submit, layout_checkin, layout_checkout, btnSubmitCek, layout_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_request);

        prepareObject();
        prepareText();
        actionButton();

        setSupportActionBar(toolbar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#022b95")));

        status = getIntent().getStringExtra("status");

        if (status.equalsIgnoreCase("New")) {
            layout_confirm.setVisibility(View.VISIBLE);
            layout_submit.setVisibility(View.VISIBLE);
            layout_checkin.setVisibility(View.GONE);
            layout_checkout.setVisibility(View.GONE);
            btnSubmitCek.setVisibility(View.GONE);
            textSubmit.setText("Mohon kedatangannya, jika berhalangan hadir di mohon konfirmasi ke pengawas yang bersangkutan atau HRD.");
            btnDirection.setText("Direction ");
        } else if (getIntent().getStringExtra("status").equalsIgnoreCase("CheckIn")) {
            layout_confirm.setVisibility(View.VISIBLE);
            layout_submit.setVisibility(View.GONE);
            layout_checkin.setVisibility(View.VISIBLE);
            layout_checkout.setVisibility(View.GONE);
            btnSubmitCek.setVisibility(View.VISIBLE);
            textSubmit.setText("Jika sudah sampai mohon menghubungi requester anda dengan melakukan chatting atau telepon");
            textCheckin.setText("Klik tombol Check In dibawah ini");
            btnCheckIn.setText("Check In");
        } else if (getIntent().getStringExtra("status").equalsIgnoreCase("CheckOut")) {
            btnDirection.setVisibility(View.GONE);
            layout_confirm.setVisibility(View.GONE);
            layout_submit.setVisibility(View.GONE);
            layout_checkin.setVisibility(View.VISIBLE);
            layout_checkout.setVisibility(View.VISIBLE);
            btnSubmitCek.setVisibility(View.VISIBLE);
            textSubmit.setText("Hubungi Requester anda jika pekerjaan sudah selesai dengan melakukan");
            textCheckin.setText("Klik tombol Check Out dibawah ini");
            btnCheckIn.setText("Check Out");
        }

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.parseColor("#FF6100"));
        }
    }

    private void prepareObject() {
        textSubmit = (TextView) findViewById(R.id.kata2);
        txtPlace = (TextView) findViewById(R.id.idTempat);
        txtTMulai = (TextView) findViewById(R.id.idTmulai);
        txtTselesai = (TextView) findViewById(R.id.idTselesai);
        txtJob = (TextView) findViewById(R.id.idJob);
        textCheckin = (TextView) findViewById(R.id.textCheckin);

        btnCheckIn = (Button) findViewById(R.id.btnCheckIn);
        btnDirection = (Button) findViewById(R.id.btnDirection);
        btnSubmit = (Button) findViewById(R.id.btnConfirm);
        btnCall = (ImageView) findViewById(R.id.btnCall);
        btnCallDua = (ImageView) findViewById(R.id.btnCallDua);
        btnChat = (ImageView) findViewById(R.id.btnChat);
        icon = (ImageView) findViewById(R.id.logo_toolbar);
        emblem = (FrameLayout) findViewById(R.id.frameLayoutPicasso);
        back = (ImageView) findViewById(R.id.back);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tittle = (TextView) findViewById(R.id.title_toolbar);


        layout_confirm = (LinearLayout) findViewById(R.id.layout_confirm);
        layout_submit = (LinearLayout) findViewById(R.id.layout_submit);
        layout_checkin = (LinearLayout) findViewById(R.id.layout_checkin);
        layout_checkout = (LinearLayout) findViewById(R.id.layout_checkout);
        btnSubmitCek = (LinearLayout) findViewById(R.id.btnSubmitCek);


    }

    private void prepareText() {
        tittle.setText("Job Call");
        icon.setVisibility(View.GONE);
        emblem.setVisibility(View.GONE);


        getIntent().getStringExtra("idRequest");
        String content = getIntent().getStringExtra("content");
        if (content.length() > 1) {
            try {
                JSONObject jsonObject = new JSONObject(content);
                txtPlace.setText(jsonObject.getString("nama_jjt"));
                txtTMulai.setText(jsonObject.getString("waktu_mulai"));
                txtTselesai.setText(jsonObject.getString("waktu_selesai"));
                txtJob.setText(jsonObject.getString("pekerjaan") + " - " + jsonObject.getString("sub_pekerjaan"));

                latitude = Double.parseDouble(jsonObject.getString("lat_lokasi"));
                longitude = Double.parseDouble(jsonObject.getString("long_lokasi"));
                telpnReq = jsonObject.getString("requester_hp");
                id = jsonObject.getString("id");

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }

    private void actionButton() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PostUpdateStatus(SubmitRequestActivity.this).execute("https://bb.byonchat.com/ApiReliever/index.php/JobStatus", id);
            }
        });

        btnCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("masukIn", status);
                if (status.equalsIgnoreCase("CheckOut")) {
                    new PostUpdateStatus(SubmitRequestActivity.this).execute("https://bb.byonchat.com/ApiReliever/index.php/JobStatus", id);
                } else {
                    new PostUpdateStatus(SubmitRequestActivity.this).execute("https://bb.byonchat.com/ApiReliever/index.php/JobStatus", id);
                }


            }
        });


        btnDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", latitude, longitude, "Job Call");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call();
            }
        });
        btnCallDua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call();
            }
        });
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chat();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }


    private void call() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        if (telpnReq.startsWith("62")) {
            telpnReq = "+" + telpnReq;
        }
        callIntent.setData(Uri.parse("tel:" + telpnReq));
        if (ActivityCompat.checkSelfPermission(SubmitRequestActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SubmitRequestActivity.this, new String[]{
                    Manifest.permission.CALL_PHONE}, 1);
        }
        startActivity(callIntent);

    }

    private void chat() {
        ChatParty sample = null;
        sample = new Contact("", telpnReq, "");
        IconItem item = null;
        item = new IconItem(telpnReq, "", "", "", sample);

        if (item.getJabberId().equalsIgnoreCase("")) {
        } else {
            Intent intent = new Intent(getApplicationContext(), ConversationActivity.class);
            String jabberId = item.getJabberId();
            intent.putExtra(ConversationActivity.KEY_JABBER_ID, jabberId);
            intent.putExtra(Constants.EXTRA_ITEM, item);
            startActivity(intent);
        }


    }


    private class PostUpdateStatus extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        String error = "";
        private Context context;

        public PostUpdateStatus(Activity activity) {
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
            postData(params[0], params[1]);
            return null;
        }

        protected void onPostExecute(String result) {

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            
            if (getIntent().getStringExtra("status").equalsIgnoreCase("CheckOut")) {
                new PostUpdateRating(SubmitRequestActivity.this).execute("https://bb.byonchat.com/ApiReliever/index.php/Rating/requester", id);

            } else {

                finish();

                if (error.length() > 0) {
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                }
            }


        }

        protected void onProgressUpdate(String... string) {
        }

        public void postData(String valueIWantToSend, String id) {
            // Create a new HttpClient and Post Header

            try {
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 13000);
                HttpConnectionParams.setSoTimeout(httpParameters, 15000);
                HttpClient httpclient = new DefaultHttpClient(httpParameters);
                HttpPost httppost = new HttpPost(valueIWantToSend);

                Log.w("kasMUS", valueIWantToSend);

                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("id", id));

                if (status.equalsIgnoreCase("New")) {
                    nameValuePairs.add(new BasicNameValuePair("status", "1"));
                } else if (getIntent().getStringExtra("status").equalsIgnoreCase("CheckIn")) {
                    nameValuePairs.add(new BasicNameValuePair("status", "2"));
                } else if (getIntent().getStringExtra("status").equalsIgnoreCase("CheckOut")) {
                    nameValuePairs.add(new BasicNameValuePair("status", "4"));

                }


                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);
                    Log.w("kamnig", data);

                } else {
                    finish();
                    error = "Tolong periksa koneksi internet.";
                }

            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                error = "Tolong periksa koneksi internet.";
            } catch (ClientProtocolException e) {
                error = "Tolong periksa koneksi internet.";
            } catch (IOException e) {
                error = "Tolong periksa koneksi internet.";
            }
        }

    }


    private class PostUpdateRating extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        String error = "";
        private Context context;

        public PostUpdateRating(Activity activity) {
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
            postData(params[0], params[1]);
            return null;
        }

        protected void onPostExecute(String result) {

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            finish();

            if (error.length() > 0) {
                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
            }

        }

        protected void onProgressUpdate(String... string) {
        }

        public void postData(String valueIWantToSend, String id) {
            // Create a new HttpClient and Post Header

            try {
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 13000);
                HttpConnectionParams.setSoTimeout(httpParameters, 15000);
                HttpClient httpclient = new DefaultHttpClient(httpParameters);
                HttpPost httppost = new HttpPost(valueIWantToSend);

                Log.w("kasMUS", valueIWantToSend);

                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("id", id));

                if (status.equalsIgnoreCase("New")) {
                    nameValuePairs.add(new BasicNameValuePair("status", "1"));
                } else if (getIntent().getStringExtra("status").equalsIgnoreCase("CheckIn")) {
                    nameValuePairs.add(new BasicNameValuePair("status", "2"));
                } else if (getIntent().getStringExtra("status").equalsIgnoreCase("CheckOut")) {
                    nameValuePairs.add(new BasicNameValuePair("rating", "4"));
                    nameValuePairs.add(new BasicNameValuePair("note", "Saya juga"));

                }


                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);
                    Log.w("kamnig", data);

                } else {
                    finish();
                    error = "Tolong periksa koneksi internet.";
                }

            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                error = "Tolong periksa koneksi internet.";
            } catch (ClientProtocolException e) {
                error = "Tolong periksa koneksi internet.";
            } catch (IOException e) {
                error = "Tolong periksa koneksi internet.";
            }
        }

    }


}