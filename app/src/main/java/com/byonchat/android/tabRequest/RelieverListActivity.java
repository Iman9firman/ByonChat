package com.byonchat.android.tabRequest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v4.util.LogWriter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.byonchat.android.FragmentDinamicRoom.DinamicRoomSearchTaskActivity;
import com.byonchat.android.R;
import com.byonchat.android.application.Application;
import com.byonchat.android.createMeme.FilteringImage;
import com.byonchat.android.listeners.RecyclerItemClickListener;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.utils.AndroidMultiPartEntity;
import com.byonchat.android.utils.HttpHelper;
import com.byonchat.android.utils.ValidationsKey;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.StringBody;
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

import static com.byonchat.android.ByonChatMainRoomActivity.jsonResultType;

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
                    getVolley();
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

        StringRequest sr = new StringRequest(Request.Method.POST, "https://forward.byonchat.com:37001/1_345171158admin/bc_voucher_client/webservice/list_api/api_submit_relever.php",
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
        sr.setRetryPolicy(new DefaultRetryPolicy(180000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
            HttpClient httpclient = null;
            try {
                httpclient = HttpHelper.createHttpClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
            HttpPost httppost = new HttpPost("https://forward.byonchat.com:37001/1_345171158admin/bc_voucher_client/webservice/list_api/api_submit_realiver_iss.php");

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });


                ContentType contentType = ContentType.create("multipart/form-data");
                BotListDB db = BotListDB.getInstance(getApplicationContext());
                ArrayList<RoomsDetail> list = db.allRoomDetailFormWithFlag(idDetail, username, idTab, "cild");

                entity.addPart("id_rooms_tab", new StringBody("2775"));
                entity.addPart("bc_user", new StringBody("628589111111"));
                entity.addPart("assign_to", new StringBody("628588888892"));

                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("value", "lokasi");
                jsonObject1.put("type", "text");

                JSONObject jsonObject2 = new JSONObject();
                jsonObject2.put("value", "15:00");
                jsonObject2.put("type", "text");

                JSONObject jsonObject3 = new JSONObject();
                jsonObject3.put("value", "15:00");
                jsonObject3.put("type", "text");

                JSONObject jsonObject4 = new JSONObject();
                jsonObject4.put("value", "15:00");
                jsonObject4.put("type", "text");


                JSONObject jsonObject5 = new JSONObject();
                jsonObject5.put("value", "15:00");
                jsonObject5.put("type", "text");


                JSONObject jsonObject6 = new JSONObject();
                jsonObject6.put("value", "15:00");
                jsonObject6.put("type", "text");


                JSONObject jsonObject7 = new JSONObject();
                jsonObject7.put("value", "15:00");
                jsonObject7.put("type", "text");

                JSONObject jsonObject8 = new JSONObject();
                jsonObject8.put("value", "15:00");
                jsonObject8.put("type", "number");

                JSONObject jsonObject9 = new JSONObject();
                jsonObject9.put("value", "15:00");
                jsonObject9.put("type", "textarea");


                entity.addPart("lokasi", new StringBody(jsonObject1.toString()));
                entity.addPart("jenis_pekerjaan", new StringBody(jsonObject2.toString()));
                entity.addPart("tanggal_mulai", new StringBody(jsonObject3.toString()));
                entity.addPart("tanggal_selesai", new StringBody(jsonObject4.toString()));
                entity.addPart("jam_mulai", new StringBody(jsonObject5.toString()));
                entity.addPart("jam_selesai", new StringBody(jsonObject6.toString()));
                entity.addPart("jumlah", new StringBody(jsonObject7.toString()));
                entity.addPart("keterangan", new StringBody(jsonObject8.toString()));
                entity.addPart("value", new StringBody(jsonObject9.toString()));


                /*for (int u = 0; u < list.size(); u++) {

                    JSONArray jsA = null;
                    String content = "";

                    String cc = list.get(u).getContent();

                    try {
                        if (cc.startsWith("{")) {
                            if (!cc.startsWith("[")) {
                                cc = "[" + cc + "]";
                            }
                            jsA = new JSONArray(cc);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    if (jsA != null) {
                        if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("new_dropdown_dinamis")) {

                            if (list.get(u).getFlag_tab().equalsIgnoreCase("lokasi")) {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("key", list.get(u).getFlag_tab());
                                jsonObject.put("value", list.get(u).getContent());
                                jsonObject.put("type", jsonResultType(list.get(u).getFlag_content(), "b"));

                                entity.addPart("lokasi", new StringBody(jsonObject.toString()));
                            }

                        }
                    } else {
                        if (list.get(u).getFlag_tab().equalsIgnoreCase("tanggal_mulai")) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("key", list.get(u).getFlag_tab());
                            jsonObject.put("value", list.get(u).getContent());
                            jsonObject.put("type", jsonResultType(list.get(u).getFlag_content(), "b"));

                            entity.addPart("tanggal_mulai", new StringBody(jsonObject.toString()));
                        }

                        if (list.get(u).getFlag_tab().equalsIgnoreCase("tanggal_selesai")) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("key", list.get(u).getFlag_tab());
                            jsonObject.put("value", list.get(u).getContent());
                            jsonObject.put("type", jsonResultType(list.get(u).getFlag_content(), "b"));

                            entity.addPart("tanggal_selesai", new StringBody(jsonObject.toString()));
                        }

                        if (list.get(u).getFlag_tab().equalsIgnoreCase("jam_mulai")) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("key", list.get(u).getFlag_tab());
                            jsonObject.put("value", list.get(u).getContent());
                            jsonObject.put("type", jsonResultType(list.get(u).getFlag_content(), "b"));

                            entity.addPart("jam_mulai", new StringBody(jsonObject.toString()));
                        }

                        if (list.get(u).getFlag_tab().equalsIgnoreCase("jam_selesai")) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("key", list.get(u).getFlag_tab());
                            jsonObject.put("value", list.get(u).getContent());
                            jsonObject.put("type", jsonResultType(list.get(u).getFlag_content(), "b"));

                            entity.addPart("jam_selesai", new StringBody(jsonObject.toString()));
                        }

                        if (list.get(u).getFlag_tab().equalsIgnoreCase("jumlah")) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("key", list.get(u).getFlag_tab());
                            jsonObject.put("value", list.get(u).getContent());
                            jsonObject.put("type", jsonResultType(list.get(u).getFlag_content(), "b"));

                            entity.addPart("jumlah", new StringBody(jsonObject.toString()));
                        }

                        if (list.get(u).getFlag_tab().equalsIgnoreCase("jenis_pekerjaan")) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("key", list.get(u).getFlag_tab());
                            jsonObject.put("value", list.get(u).getContent());
                            jsonObject.put("type", jsonResultType(list.get(u).getFlag_content(), "b"));

                            entity.addPart("jenis_pekerjaan", new StringBody(jsonObject.toString()));
                        }

                        if (list.get(u).getFlag_tab().equalsIgnoreCase("keterangan")) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("key", list.get(u).getFlag_tab());
                            jsonObject.put("value", list.get(u).getContent());
                            jsonObject.put("type", jsonResultType(list.get(u).getFlag_content(), "b"));

                            entity.addPart("keterangan", new StringBody(jsonObject.toString()));
                        }
                    }
                }*/


                Log.w("siiiip", entity.toString());

                totalSize = entity.getContentLength();
                Log.w("siiiip", totalSize + "");
                httppost.setEntity(entity);

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                Log.w("tabuR", statusCode + "");
                if (statusCode == 200) {

                    final String data = EntityUtils.toString(r_entity);
                    Log.w("tabuR", data);
                    //{"messages":"succes","datas":[{"id_officer":"1956","id_client":"117","name":"Yozia Josephine","telp_number":"6285719845956","last_lat":"-6.18950319","last_long":"106.76643372","distance":"1.4389605798435736","rating":"1.5"}]}

                } else {
                    error = "Tolong periksa koneksi internet.1";
                }

            } catch (ClientProtocolException e) {
                return null;
            } catch (IOException e) {
                return null;
            } catch (Exception e) {
                return null;
            }

            return null;
        }
    }


}
