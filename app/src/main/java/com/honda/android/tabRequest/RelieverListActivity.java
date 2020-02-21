package com.honda.android.tabRequest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.honda.android.R;
import com.honda.android.createMeme.FilteringImage;
import com.honda.android.provider.BotListDB;
import com.honda.android.provider.RoomsDetail;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RelieverListActivity extends AppCompatActivity {

    public static final String XTRA_RELIEVER_JSON = "XTRA_RELIEVER_JSON";
    public static final String XTRA_DETAILS_JSON = "XTRA_DETAILS_JSON";
    public static final String XTRA_LATITUDE = "XTRA_LATITUDE";
    public static final String XTRA_LONGITUDE = "XTRA_LONGITUDE";
    public static final String XTRA_MAX = "XTRA_MAX";
    public static final String idDetail = "idDetail";
    public static final String username = "username";
    public static final String idTab = "idTab";

    private String resultJson = null;
    private String resultDetail = null;
    private String resultLatitude = null;
    private String resultLongitude = null;
    private int resultMax = 0;
    private ArrayList<Reliever> relievers;

    private TextView textDetail;
    private Button butMap, btn_submit;
    private TextView textMaxSelect;
    private ConstraintLayout layoutEmpty;
    private TextView textNoResult;
    private ImageView imageCall;
    private RecyclerView recyclerReliever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reliever_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Request");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#022B96")));
        FilteringImage.SystemBarBackground(getWindow(), Color.parseColor("#022B96"));

        setView();
        methodRecycler();
        methodMap();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                final AlertDialog.Builder alertbox = new AlertDialog.Builder(RelieverListActivity.this);
                alertbox.setMessage("Are you sure you want to exit?");
                alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        onBackPressed();
                    }
                });
                alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
                alertbox.show();


                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setView() {
        textDetail = (TextView) findViewById(R.id.text_detail_relieverList);
        butMap = (Button) findViewById(R.id.but_map_relieverList);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        textMaxSelect = (TextView) findViewById(R.id.text_maxSelect_relieverList);
        layoutEmpty = (ConstraintLayout) findViewById(R.id.container_01_relieverList);
        textNoResult = (TextView) findViewById(R.id.text_noResult_relieverList);
        imageCall = (ImageView) findViewById(R.id.image_call_relieverList);
        recyclerReliever = (RecyclerView) findViewById(R.id.recyler_relieverList);

        resultJson = getIntent().getStringExtra(XTRA_RELIEVER_JSON);
        resultDetail = getIntent().getStringExtra(XTRA_DETAILS_JSON);
        resultLatitude = getIntent().getStringExtra(XTRA_LATITUDE);
        resultLongitude = getIntent().getStringExtra(XTRA_LONGITUDE);
        resultMax = Integer.valueOf(getIntent().getStringExtra(XTRA_MAX));

        textDetail.setText(resultDetail);
        textMaxSelect.setText("Max Selection : " + resultMax);
    }

    private void methodRecycler() {
        if (getList(resultJson, relievers).size() != 0) {

            recyclerReliever.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            recyclerReliever.setLayoutManager(new LinearLayoutManager(this));
            recyclerReliever.setAdapter(new RelieverListAdapter(this, getList(resultJson, relievers), resultMax));
            recyclerReliever.getAdapter().notifyDataSetChanged();
            btn_submit.setVisibility(View.VISIBLE);
            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.w("malam", "ini");
                    getVolley();
                    /*try {
                        String url = "https://bb.byonchat.com/bc_voucher_client/webservice/list_api/api_submit_realiver_iss.php";
                        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.w("cariLL", response);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.w("cariLL22", "Jowh");
                                    }
                                }
                        ) {
                            @Override
                            protected Map<String, String> getParams() {
                                JSONObject jsonObject7 = new JSONObject();
                                JSONObject jsonObject1 = new JSONObject();
                                JSONObject jsonObject3 = new JSONObject();
                                JSONObject jsonObject4 = new JSONObject();
                                JSONObject jsonObject2 = new JSONObject();
                                JSONObject jsonObject5 = new JSONObject();
                                JSONObject jsonObject6 = new JSONObject();
                                JSONObject jsonObject8 = new JSONObject();
                                JSONObject jsonObject9 = new JSONObject();
                                try {

                                    jsonObject1.put("value", "lokasi");
                                    jsonObject1.put("type", "text");


                                    jsonObject2.put("value", "15:00");
                                    jsonObject2.put("type", "text");


                                    jsonObject3.put("value", "15:00");
                                    jsonObject3.put("type", "text");


                                    jsonObject4.put("value", "15:00");
                                    jsonObject4.put("type", "text");



                                    jsonObject5.put("value", "15:00");
                                    jsonObject5.put("type", "text");



                                    jsonObject6.put("value", "15:00");
                                    jsonObject6.put("type", "text");



                                    jsonObject7.put("value", "15:00");
                                    jsonObject7.put("type", "text");


                                    jsonObject8.put("value", "15:00");
                                    jsonObject8.put("type", "number");

                                    jsonObject9.put("value", "15:00");
                                    jsonObject9.put("type", "textarea");



                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Map<String, String> params = new HashMap<String, String>();

                                params.put("lokasi", jsonObject1.toString());
                                params.put("jenis_pekerjaan", jsonObject2.toString());
                                params.put("tanggal_mulai",jsonObject3.toString());
                                params.put("tanggal_selesai", jsonObject4.toString());
                                params.put("jam_mulai",jsonObject5.toString());
                                params.put("jam_selesai", jsonObject6.toString());
                                params.put("jumlah", jsonObject7.toString());
                                params.put("keterangan",jsonObject8.toString());
                                params.put("value",jsonObject9.toString());

                                return params;
                            }
                        };

                        Application.getInstance().addToRequestQueue(postRequest);

                    } catch (Exception e) {
                        Log.w("kardus", e.getMessage());
                    }*/


                }
            });
        } else {
            btn_submit.setVisibility(View.GONE);
            textNoResult.setText("No result found , please call ISS");
            recyclerReliever.setVisibility(View.GONE);
            textNoResult.setVisibility(View.VISIBLE);
            imageCall.setVisibility(View.VISIBLE);
            imageCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:+6285891307575"));
                    startActivity(callIntent);

                }
            });
        }
    }

    private void getVolley() {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest sr = new StringRequest(Request.Method.POST, "https://bb.byonchat.com/bc_voucher_client/webservice/list_api/api_submit_relever.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("HttpClient", "success! response: " + response);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("HttpClient", "error: " + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                JSONObject jsonObject7 = new JSONObject();
                JSONObject jsonObject1 = new JSONObject();
                JSONObject jsonObject3 = new JSONObject();
                JSONObject jsonObject4 = new JSONObject();
                JSONObject jsonObject2 = new JSONObject();
                JSONObject jsonObject5 = new JSONObject();
                JSONObject jsonObject6 = new JSONObject();
                JSONObject jsonObject8 = new JSONObject();
                JSONObject jsonObject9 = new JSONObject();
                try {

                    jsonObject1.put("value", "lokasi");
                    jsonObject1.put("type", "text");


                    jsonObject2.put("value", "15:00");
                    jsonObject2.put("type", "text");


                    jsonObject3.put("value", "15:00");
                    jsonObject3.put("type", "text");


                    jsonObject4.put("value", "15:00");
                    jsonObject4.put("type", "text");



                    jsonObject5.put("value", "15:00");
                    jsonObject5.put("type", "text");



                    jsonObject6.put("value", "15:00");
                    jsonObject6.put("type", "text");



                    jsonObject7.put("value", "15:00");
                    jsonObject7.put("type", "text");


                    jsonObject8.put("value", "15:00");
                    jsonObject8.put("type", "number");

                    jsonObject9.put("value", "15:00");
                    jsonObject9.put("type", "textarea");



                } catch (JSONException e) {
                    e.printStackTrace();
                }

                params.put("lokasi", jsonObject1.toString());
                params.put("jenis_pekerjaan", jsonObject2.toString());
                params.put("tanggal_mulai",jsonObject3.toString());
                params.put("tanggal_selesai", jsonObject4.toString());
                params.put("jam_mulai",jsonObject5.toString());
                params.put("jam_selesai", jsonObject6.toString());
                params.put("jumlah", jsonObject7.toString());
                params.put("keterangan",jsonObject8.toString());
                params.put("value",jsonObject9.toString());

                params.put("id_rooms_tab", "2775");
                params.put("bc_user", "6285881172097");
                params.put("assign_to", "6281588888892");

                return params;
            }
        };
        queue.add(sr);
    }


    private void methodMap() {
        butMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent maps = new Intent(RelieverListActivity.this, MapsViewActivity.class);
                maps.putExtra(XTRA_RELIEVER_JSON, resultJson);
                maps.putExtra(XTRA_LATITUDE, resultLatitude);
                maps.putExtra(XTRA_LONGITUDE, resultLongitude);
                startActivity(maps);
            }
        });
        butMap.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent maps = new Intent(RelieverListActivity.this, MapsViewActivity.class);
                maps.putExtra("FLAG_OSM", 888);
                maps.putExtra("POSISI_AWAL", "");
                maps.putExtra("NOMER_BC", "");
                startActivity(maps);
                return true;
            }
        });
    }

    public static ArrayList<Reliever> getList(String jsonresult, ArrayList<Reliever> relievers) {
        relievers = new ArrayList<>();
        try {
            JSONArray result = new JSONArray(jsonresult);
            for (int i = 0; i < result.length(); i++) {
                JSONObject relieverJson = result.getJSONObject(i);
                Reliever reliever = new Reliever(
                        relieverJson.getString("id_officer"),
                        relieverJson.getString("id_client"),
                        relieverJson.getString("name"),
                        relieverJson.getString("telp_number"),
                        relieverJson.getString("last_lat"),
                        relieverJson.getString("last_long"),
                        relieverJson.getString("distance"),
                        relieverJson.getString("rating"),
                        false
                );
                relievers.add(reliever);
            }
            Collections.sort(relievers);
            return relievers;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return relievers;
    }


    private class posTask extends AsyncTask<String, Integer, String> {

        String error = "";
        long totalSize = 0;

        @Override
        protected String doInBackground(String... params) {
            return postData();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected void onPostExecute(String result) {
            finish();
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        public String postData() {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://bb.byonchat.com/bc_voucher_client/webservice/list_api/api_submit_realiver_iss.php");


            return null;
        }
    }


}
