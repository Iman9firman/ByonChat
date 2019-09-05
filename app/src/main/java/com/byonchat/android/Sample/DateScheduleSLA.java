package com.byonchat.android.Sample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.R;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.byonchat.android.ui.fragment.ByonchatScheduleSLAFragment.dpToPx;


public class DateScheduleSLA extends AppCompatActivity {
    TextView title;
    LinearLayout llData;
    //    String jt,fq,fl,pr,sd,fd,tt;
    String jt,fq,pr,tt;
    //    ArrayList<String> da = new ArrayList<>();
    private ProgressDialog progressDialog;

    int colorText, backgroundText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jjt_period);
        getSupportActionBar().setTitle("Schedule SLA");

        llData = (LinearLayout) findViewById(R.id.linearData) ;
        title = (TextView) findViewById(R.id.title);

        getAllIntent();
        getAllDataListPeriode();

        title.setText(tt);
        title.setTextColor(colorText);
    }

    public void getAllIntent(){
        jt = getIntent().getStringExtra("jt");
        fq = getIntent().getStringExtra("fq");
        pr = getIntent().getStringExtra("pr");
        tt = getIntent().getStringExtra("tt");

        colorText = getResources().getColor(R.color.grayDark);
        backgroundText = getResources().getColor(R.color.tab_text_selected);
    }

    public void getAllDataListPeriode(){

        progressDialog = new ProgressDialog(DateScheduleSLA.this);
        progressDialog.setTitle("Get Data!");
        progressDialog.setMessage("Please wait a second");
        progressDialog.show();
        if (NetworkInternetConnectionStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

            String url = "https://bb.byonchat.com/bc_voucher_client/webservice/list_api/iss/schedule/schedule_data.php";
            try {
                String version = new HttpAsyncTask().execute(addLocationToUrl(url)).get();
                Log.e("Reamure DateSchedlue",version);
                JSONObject jsonObject = new JSONObject(version);
                JSONArray jsonArray = jsonObject.getJSONArray("item");
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String jjt = jsonObject1.getString("kode_jjt");
                    String floor = jsonObject1.getString("floor");
                    String date = jsonObject1.getString("date");
                    String period = jsonObject1.getString("periode");
                    String frequency = jsonObject1.getString("frequency");

                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(50));
                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(1));
                    TextView edt = new TextView(getApplicationContext());
                    edt.setText(date);
                    edt.setTextColor(colorText);
                    edt.setBackgroundColor(backgroundText);
                    edt.setTextSize(20);
                    edt.setGravity(Gravity.CENTER|Gravity.LEFT);
                    llData.addView(edt,params1);

                    View view = new View(getApplicationContext());
                    view.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.grey));
                    llData.addView(view, params2);

                    edt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            intentTo(date);
                        }
                    });
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (JSONException e){
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
        }
    }

    public void intentTo(String aa){
        Intent dw = new Intent(this, DetailAreaScheduleSLA.class);
        dw.putExtra("jt",jt);
        dw.putExtra("fq",fq);
//        dw.putExtra("fl",fl);
        dw.putExtra("pr",pr);
//        dw.putExtra("sd",sd);
//        dw.putExtra("fd",fd);
//        dw.putExtra("da",da);
        dw.putExtra("dt",aa);
        startActivity(dw);
    }

    protected String addLocationToUrl(String url){
        if(!url.endsWith("?"))
            url += "?";

        List<NameValuePair> params = new LinkedList<NameValuePair>();

        params.add(new BasicNameValuePair("action", "getData"));
        params.add(new BasicNameValuePair("kode_jjt", jt));
        params.add(new BasicNameValuePair("periode", pr));
        params.add(new BasicNameValuePair("frequency", fq));

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
            progressDialog.dismiss();
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
}
