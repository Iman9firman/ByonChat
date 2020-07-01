package com.byonchat.android.Sample;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.byonchat.android.R;
import com.byonchat.android.Sample.Database.ScheduleSLA;
import com.byonchat.android.Sample.Database.ScheduleSLADB;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.createMeme.FilteringImage;
import com.byonchat.android.local.Byonchat;
import com.byonchat.android.utils.HttpHelper;
import com.byonchat.android.widget.ToolbarWithIndicator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.byonchat.android.ui.fragment.ByonchatScheduleSLAFragment.perioRes;

public class DetailAreaScheduleSLA extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    RecyclerView recyclerView;
    DetailAreaScheduleAdapter adapter;
    ArrayList<DetailArea> detarea_list = new ArrayList();
    //    String jt,fq,fl,pr,sd,fd, dt;
    String id, jt, ktrgn, pr, dt;
    private static final int REQ_CAMERA = 1201;
    public static String link_pic = "https://bb.byonchat.com/bc_voucher_client/webservice/list_api/iss/schedule/files/";

    Bitmap result = null;
    Button submit;
    boolean hideSubmit;
    private String url;
    private SwipeRefreshLayout swipeRefreshLayout;
    ToolbarWithIndicator toolbar;
    protected ProgressDialog pdialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_area_sla_layout);
        getAllIntent();

        toolbar = findViewById(R.id.toolbar);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(true);
        setSupportActionBar(toolbar);

        resolveToolbar("022b95", "ffffff");

        recyclerView = (RecyclerView) findViewById(R.id.blinded);
        submit = (Button) findViewById(R.id.button_submit);
        submit.setVisibility(View.GONE);

        if (pdialog == null) {
            pdialog = new ProgressDialog(this);
            pdialog.setIndeterminate(true);
            pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }


        adapterRecyclerView();
        // submitButton();
    }

    @Override
    protected void onPause() {
        super.onPause();
        toolbar.stopScan();
    }


    public void getAllIntent() {
        id = getIntent().getStringExtra("id");
        jt = getIntent().getStringExtra("jt");
        ktrgn = getIntent().getStringExtra("fq");
        pr = getIntent().getStringExtra("pr");
        dt = getIntent().getStringExtra("dt");
    }

    protected void resolveToolbar(String mColor, String title) {
        getSupportActionBar().setTitle("Schedule SLA");
        getSupportActionBar().setSubtitle(jt);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FilteringImage.SystemBarBackground(getWindow(), Color.parseColor("#" + mColor));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#" + mColor)));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_35dp);
    }

    public void adapterRecyclerView() {

        adapter = new DetailAreaScheduleAdapter(DetailAreaScheduleSLA.this, detarea_list, new DetailAreaScheduleAdapter.ClickListener() {
            @Override
            public void onClick(String pos) {
                if (pos.equalsIgnoreCase("start")) {
                    for (int i = 0; i < detarea_list.size(); i++) {
                        if (!detarea_list.get(i).getImg_start().equalsIgnoreCase("null")) {
                            if (detarea_list.get(i).getImg_start().startsWith("/storage")) {
                                Toast.makeText(DetailAreaScheduleSLA.this, "Submit Successfully! : " + detarea_list.get(i).getImg_start(), Toast.LENGTH_LONG).show();
                                new PostSchedule(DetailAreaScheduleSLA.this).execute(url, detarea_list.get(i).getId(), pos, detarea_list.get(i).getImg_start());
                            }
                        }
                    }
                } else if (pos.equalsIgnoreCase("on_proses")) {

                    for (int i = 0; i < detarea_list.size(); i++) {
                        if (!detarea_list.get(i).getImg_proses().equalsIgnoreCase("null")) {

                            if (detarea_list.get(i).getImg_start().startsWith("/storage")) {
                                Toast.makeText(DetailAreaScheduleSLA.this, "Harap di submit foto Start, terlebih dahulu", Toast.LENGTH_LONG).show();
                                return;
                            }

                            if (detarea_list.get(i).getImg_proses().startsWith("/storage")) {
                                new PostSchedule(DetailAreaScheduleSLA.this).execute(url, detarea_list.get(i).getId(), pos, detarea_list.get(i).getImg_proses());
                            }
                        }
                    }

                } else if (pos.equalsIgnoreCase("done")) {
                    for (int i = 0; i < detarea_list.size(); i++) {
                        if (!detarea_list.get(i).getImg_done().equalsIgnoreCase("null")) {
                            if (detarea_list.get(i).getImg_start().startsWith("/storage")) {
                                Toast.makeText(DetailAreaScheduleSLA.this, "Harap di submit foto Start, terlebih dahulu", Toast.LENGTH_LONG).show();
                                return;
                            }
                            if (detarea_list.get(i).getImg_proses().startsWith("/storage")) {
                                Toast.makeText(DetailAreaScheduleSLA.this, "Harap di submit foto On Process, terlebih dahulu", Toast.LENGTH_LONG).show();
                                return;
                            }

                            if (detarea_list.get(i).getImg_done().startsWith("/storage")) {
                                new PostSchedule(DetailAreaScheduleSLA.this).execute(url, detarea_list.get(i).getId(), pos, detarea_list.get(i).getImg_done());
                            }
                        }
                    }
                }


            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        prepareDataRecycle();

    }

    public void prepareDataRecycle() {
        submit.setVisibility(View.GONE);

        detarea_list.clear();
        url = "https://forward.byonchat.com:37001/1_345171158admin/bc_voucher_client/webservice/list_api/iss/schedule/schedule_data_new.php";
        if (NetworkInternetConnectionStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
            new HttpAsyncTask().execute(postParameters(url));
        } else {
            Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
        }
        Log.w("Showow TERIMA", detarea_list.size() + "");
        adapter.notifyDataSetChanged();
    }


    protected String postParameters(String url) {
        if (!url.endsWith("?"))
            url += "?";

        List<NameValuePair> params = new LinkedList<NameValuePair>();

        params.add(new BasicNameValuePair("action", "getFullDetail"));
        params.add(new BasicNameValuePair("id_detail_area", id));
        params.add(new BasicNameValuePair("kode_jjt", jt));
        params.add(new BasicNameValuePair("periode", perioRes(pr)));
        params.add(new BasicNameValuePair("keterangan", perioRes(ktrgn)));
        params.add(new BasicNameValuePair("date", dt));

        String paramString = URLEncodedUtils.format(params, "utf-8");

        url += paramString;
        return url;
    }

    @Override
    public void onRefresh() {
        prepareDataRecycle();
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            showAuthProgressDialog();
            return GET(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.w("Showow", result);
            dismissAuthProgressDialog();
            try {
                Log.w("Showow", "error!!!   1");
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("item");
                for (int i = 0; i < jsonArray.length(); i++) {
                    Log.w("Showow", "error!!!   2");
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String id = jsonObject1.getString("id_jjt");
                    String jjt = jsonObject1.getString("kode_jjt");
                    String floor = jsonObject1.getString("floor");
                    String date = jsonObject1.getString("date");
                    String period = jsonObject1.getString("periode");
                    String ketrgn = jsonObject1.getString("keterangan");
                    String id_detail_area = jsonObject1.getString("id_detail_area");
                    String detail_area = jsonObject1.getString("detail_area");
                    String id_detail_proses = jsonObject1.getString("id_detail_proses");
                    String start = jsonObject1.getString("start");
                    String on_proses = jsonObject1.getString("on_proses");
                    String done = jsonObject1.getString("done");

                    if (done.equalsIgnoreCase("null")) {
                        submit.setVisibility(View.VISIBLE);
                        Log.w("Showow", "error!!!   2");
                    }
                    ScheduleSLA sch = new ScheduleSLA(id_detail_proses, id, Byonchat.getMessengerHelper().getMyContact().getJabberId(), start, on_proses, done);
                    ScheduleSLADB dbA = ScheduleSLADB.getInstance(DetailAreaScheduleSLA.this);
                    Cursor cursorBot = dbA.getDataPicByID(id_detail_proses);
                    if (cursorBot.getCount() > 0) {
                        dbA.updateImgAll(sch);
                        Log.w("Showow", "error!!!   3");
                    } else {
                        dbA.insertDataSchedule(sch);
                        Log.w("Showow", "error!!!   4");

                    }

                    DetailArea dtArea = new DetailArea(id_detail_proses, id, detail_area + "  (" + dt + ")", period, ketrgn, start, on_proses, done);
                    detarea_list.add(dtArea);
                    Log.w("Showow", "error!!!   " + "BOKU" + detarea_list.size());
                }
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
                Log.w("Showow", "error!!!   " + e.getMessage());
            }
        }
    }

    public static String GET(String url) {
        InputStream inputStream = null;
        String result = "";
        try {
            Log.w("Showow1", "11");
            HttpClient httpclient = HttpHelper.createHttpClient();
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
            inputStream = httpResponse.getEntity().getContent();
            if (inputStream != null) {
                Log.w("Showow2", "2");
                result = convertInputStreamToString(inputStream);
            } else {
                Log.w("Showow3", "3");
                result = "";
            }
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        Log.w("Showow", result);

        return result;
    }

    public void showAuthProgressDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    public void dismissAuthProgressDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    private class PostSchedule extends AsyncTask<String, String, String> {
        String error = "";
        private Context context;
        ProgressDialog progressDialog;

        public PostSchedule(Context activity) {
            context = activity;
        }

        protected void onPreExecute() {
            progressDialog = new ProgressDialog(DetailAreaScheduleSLA.this);
            progressDialog.setTitle("Uploading!");
            progressDialog.setMessage("Please wait a second");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            postData(params[0], params[1], params[2], params[3]);
            return null;
        }

        protected void onPostExecute(String result) {
            if (error.length() > 0) {
                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
            }
        }

        protected void onProgressUpdate(String... string) {
        }

        public void postData(String link, String id_area, String nama, String file) {
            try {
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 13000);
                HttpConnectionParams.setSoTimeout(httpParameters, 15000);
                HttpClient httpclient = HttpHelper.createHttpClient();
                HttpPost httppost = new HttpPost(link);

                MultipartEntity entities = new MultipartEntity();
                entities.addPart("action", new StringBody("updateItemDetail", Charset.forName("UTF-8")));
                entities.addPart("id", new StringBody(id_area, Charset.forName("UTF-8")));

                File start = new File(file);
                FileBody fileBody1 = new FileBody(start);
                entities.addPart(nama, fileBody1);


                httppost.setEntity(entities);

                HttpResponse response = httpclient.execute(httppost);
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);


                    DetailAreaScheduleSLA.this.runOnUiThread(new Runnable() {
                        public void run() {
                            File start = new File(file);
                            if (start.exists()) {
                                start.delete();
                            }

                            Toast.makeText(DetailAreaScheduleSLA.this, "Submit Successfully!", Toast.LENGTH_LONG).show();
                            finish();
                            startActivity(getIntent());

                        }
                    });
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    error = "Tolong periksa koneksi internet.";
                    Log.e("freegg uploada", id_area);
                    DetailAreaScheduleSLA.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(DetailAreaScheduleSLA.this, "Error upload", Toast.LENGTH_LONG).show();
                        }
                    });
                }

            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                progressDialog.dismiss();
                Log.e("freegg uploada", e.getMessage());
                Toast.makeText(DetailAreaScheduleSLA.this, e.getMessage(), Toast.LENGTH_LONG).show();
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                progressDialog.dismiss();
                Log.e("freegg uploada", e.getMessage());
                Toast.makeText(DetailAreaScheduleSLA.this, e.getMessage(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                progressDialog.dismiss();
                Log.e("freegg uploada", e.getMessage());
                Toast.makeText(DetailAreaScheduleSLA.this, e.getMessage(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.startScan("forward.byonchat.com", DetailAreaScheduleSLA.this);
        ScheduleSLADB dbA = ScheduleSLADB.getInstance(DetailAreaScheduleSLA.this);
        Cursor all = dbA.getAllImgSaved();
        if (all.getCount() > 0) {
            if (all.moveToFirst()) {
                do {
                    String id_da = all.getString(all.getColumnIndex(ScheduleSLADB.SCH_DATA_ID_AREA));
                    String id_jjt = all.getString(all.getColumnIndex(ScheduleSLADB.SCH_DATA_JJT));
                    String img_start = all.getString(all.getColumnIndex(ScheduleSLADB.SCH_DATA_START_PIC));
                    String img_proses = all.getString(all.getColumnIndex(ScheduleSLADB.SCH_DATA_PROSES_PIC));
                    String img_done = all.getString(all.getColumnIndex(ScheduleSLADB.SCH_DATA_DONE_PIC));

                    for (int i = 0; i < detarea_list.size(); i++) {
                        if (detarea_list.get(i).getId().equalsIgnoreCase(id_da)) {
                            if (detarea_list.get(i).getId_jjt().equalsIgnoreCase(id_jjt)) {
                                detarea_list.get(i).setImg_start(img_start);
                                detarea_list.get(i).setImg_proses(img_proses);
                                detarea_list.get(i).setImg_done(img_done);
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                } while (all.moveToNext());
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        return true;
    }
}
