package com.byonchat.android.Sample;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.byonchat.android.R;
import com.byonchat.android.Sample.Database.ScheduleSLA;
import com.byonchat.android.Sample.Database.ScheduleSLADB;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.local.Byonchat;

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

public class DetailAreaScheduleSLA extends AppCompatActivity {
    RecyclerView recyclerView;
    DetailAreaScheduleAdapter adapter;
    ArrayList<DetailArea> detarea_list = new ArrayList();
//    String jt,fq,fl,pr,sd,fd, dt;
    String jt, fq, pr, dt;
    ArrayList<String> da = new ArrayList<>();
    private static final int REQ_CAMERA = 1201;
    public static String link_pic = "https://bb.byonchat.com/bc_voucher_client/webservice/list_api/iss/schedule/files/";

    Bitmap result = null;
    Button submit;
    private String url;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_area_sla_layout);
        getSupportActionBar().setTitle("Schedule SLA");

        recyclerView = (RecyclerView) findViewById(R.id.blinded);
        submit = (Button) findViewById(R.id.button_submit);

        getAllIntent();
        adapterRecyclerView();
        submitButton();
    }

    public void getAllIntent(){
        jt = getIntent().getStringExtra("jt");
        fq = getIntent().getStringExtra("fq");
        pr = getIntent().getStringExtra("pr");
        dt = getIntent().getStringExtra("dt");
        da = getIntent().getStringArrayListExtra("da");
    }

    public void adapterRecyclerView(){

        adapter = new DetailAreaScheduleAdapter(DetailAreaScheduleSLA.this, detarea_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        prepareDataRecycle();
    }

    public void prepareDataRecycle(){
        detarea_list.clear();
        ScheduleSLADB dbA = ScheduleSLADB.getInstance(DetailAreaScheduleSLA.this);

        url = "https://bb.byonchat.com/bc_voucher_client/webservice/list_api/iss/schedule/schedule_data.php";

        if (NetworkInternetConnectionStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
            try {
                String version = new HttpAsyncTask().execute(postParameters(url)).get();
                Log.e("Reamure DetailArea",version);

                JSONObject jsonObject = new JSONObject(version);
                JSONArray jsonArray = jsonObject.getJSONArray("item");
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String id = jsonObject1.getString("id_jjt");
                    String jjt = jsonObject1.getString("kode_jjt");
                    String floor = jsonObject1.getString("floor");
                    String date = jsonObject1.getString("date");
                    String period = jsonObject1.getString("periode");
                    String frequency = jsonObject1.getString("frequency");
                    String id_detail_area = jsonObject1.getString("id_detail_area");
                    String detail_area = jsonObject1.getString("detail_area");
                    String id_detail_proses = jsonObject1.getString("id_detail_proses");
                    String start = jsonObject1.getString("start");
                    String on_proses = jsonObject1.getString("on_proses");
                    String done = jsonObject1.getString("done");

                    ScheduleSLA sch = new ScheduleSLA(id_detail_proses, id, Byonchat.getMessengerHelper().getMyContact().getJabberId(), start, on_proses, done);

                    Cursor cursorBot = dbA.getDataPicByID(id_detail_proses);
                    if(cursorBot.getCount() > 0){
                        dbA.updateImgAll(sch);
                    }else {
                        dbA.insertDataSchedule(sch);
                    }

                    DetailArea dtArea = new DetailArea(id_detail_proses, id, detail_area, period, frequency, start, on_proses, done);
                    detarea_list.add(dtArea);
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (JSONException e){
                e.printStackTrace();
                Log.e("version","error!!!   "+e.getMessage());
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
        }

        adapter.notifyDataSetChanged();
    }

    private void submitButton() {
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < detarea_list.size(); i++) {
                    if(!detarea_list.get(i).getImg_start().equalsIgnoreCase("null")){
                        if(!detarea_list.get(i).getImg_proses().equalsIgnoreCase("null")){
                            if(!detarea_list.get(i).getImg_done().equalsIgnoreCase("null")) {
                                if (detarea_list.get(i).getImg_start().startsWith("/storage")) {
                                    new PostSchedule(DetailAreaScheduleSLA.this).execute(url, detarea_list.get(i).getId(), detarea_list.get(i).getImg_start(), detarea_list.get(i).getImg_proses(), detarea_list.get(i).getImg_done());
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    protected String postParameters(String url){
        if(!url.endsWith("?"))
            url += "?";

        List<NameValuePair> params = new LinkedList<NameValuePair>();

        params.add(new BasicNameValuePair("action", "getFullDetail"));
        params.add(new BasicNameValuePair("kode_jjt", jt));
        params.add(new BasicNameValuePair("periode", pr));
        params.add(new BasicNameValuePair("frequency", fq));
        params.add(new BasicNameValuePair("date", dt));

        String paramString = URLEncodedUtils.format(params, "utf-8");

        url += paramString;
        return url;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }
        @Override
        protected void onPostExecute(String result) {
        }
    }

    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
            inputStream = httpResponse.getEntity().getContent();
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "";
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
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
            postData(params[0], params[1], params[2], params[3], params[4]);
            return null;
        }

        protected void onPostExecute(String result) {
            if (error.length() > 0) {
                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
            }
        }

        protected void onProgressUpdate(String... string) {
        }

        public void postData(String link, String id_area, String file_start, String file_proses, String file_done) {
            try {
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 13000);
                HttpConnectionParams.setSoTimeout(httpParameters, 15000);
                HttpClient httpclient = new DefaultHttpClient(httpParameters);
                HttpPost httppost = new HttpPost(link);

                MultipartEntity entities = new MultipartEntity();
                entities.addPart("action", new StringBody("updateFullDetail", Charset.forName("UTF-8")));
                entities.addPart("id", new StringBody(id_area, Charset.forName("UTF-8")));

                //Start
                File start = new File(file_start);
                FileBody fileBody1 = new FileBody(start);
                entities.addPart("start", fileBody1);

                //Proses
                File proses = new File(file_proses);
                FileBody fileBody2 = new FileBody(proses);
                entities.addPart("on_proses", fileBody2);

                //Done
                File done = new File(file_done);
                FileBody fileBody3 = new FileBody(done);
                entities.addPart("done", fileBody3);

                httppost.setEntity(entities);

                HttpResponse response = httpclient.execute(httppost);
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);

                    Log.e("freegg uploada",id_area);

                    DetailAreaScheduleSLA.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(DetailAreaScheduleSLA.this,data,Toast.LENGTH_LONG).show();
                            prepareDataRecycle();
                        }
                    });
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    error = "Tolong periksa koneksi internet.";
                    Log.e("freegg uploada",id_area);
                    DetailAreaScheduleSLA.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(DetailAreaScheduleSLA.this,"Error upload",Toast.LENGTH_LONG).show();
                        }
                    });
                }

            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                progressDialog.dismiss();
                Log.e("freegg uploada",e.getMessage());
                Toast.makeText(DetailAreaScheduleSLA.this,e.getMessage(),Toast.LENGTH_LONG).show();
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                progressDialog.dismiss();
                Log.e("freegg uploada",e.getMessage());
                Toast.makeText(DetailAreaScheduleSLA.this,e.getMessage(),Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                progressDialog.dismiss();
                Log.e("freegg uploada",e.getMessage());
                Toast.makeText(DetailAreaScheduleSLA.this,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        ScheduleSLADB dbA = ScheduleSLADB.getInstance(DetailAreaScheduleSLA.this);
        Cursor all = dbA.getAllImgSaved();
        if(all.getCount() > 0){
            if(all.moveToFirst()){
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
                }while (all.moveToNext());
            }
        }
    }
}
