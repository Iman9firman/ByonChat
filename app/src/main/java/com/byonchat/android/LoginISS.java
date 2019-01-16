package com.byonchat.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.byonchat.android.ui.activity.MainActivityNew;
import com.google.android.gms.vision.L;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class LoginISS extends AppCompatActivity {
    private String username;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_for_iss_room);

        final Intent inti = getIntent();

        username = inti.getStringExtra(ConversationActivity.KEY_JABBER_ID);

        Button erwgv = (Button) findViewById(R.id.loginBtn);
        EditText userID = (EditText) findViewById(R.id.login_userid) ;
        EditText passID = (EditText) findViewById(R.id.login_password) ;
        EditText accID = (EditText) findViewById(R.id.login_acc) ;

        userID.setText("TESTING");
        passID.setText("Testing123");
        accID.setText("issid");

        erwgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goVerif(userID.getText().toString(), passID.getText().toString(), accID.getText().toString());
                pd = new ProgressDialog(LoginISS.this);
                pd.setMessage("Please Wait");
                pd.show();
//                String trey = "https://issapi.dataon.com/sfapi/index.cfm?endpoint=/issid_SF_EO_cekuser/N101303/BYONCHAT";
//                new getJSONeKtp(trey, userID.getText().toString(), passID.getText().toString(), accID.getText().toString()).execute();
            }
        });
    }


    public void goVerif(String user, String pass, String acc){
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest sr = new StringRequest(Request.Method.GET, "https://issapi.dataon.com/sfapi/index.cfm?endpoint=/issid_SF_EO_cekuser/N101303/BYONCHAT",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("HttpClient", "success! response: " + response);

                        Toast.makeText(LoginISS.this,response,Toast.LENGTH_LONG).show();
                        pd.dismiss();
                        finish();
                        Intent intent = new Intent(getApplicationContext(), MainActivityNew.class);
                        intent.putExtra(ConversationActivity.KEY_JABBER_ID, username);
                        intent.putExtra("success", "oke");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       /* pd.dismiss();
                        Log.e("HttpClient", "error: " + error.toString());
                        Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();*/
                    }
                })
        {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("X-SFAPI-UserName",user);
                params.put("X-SFAPI-UserPass",pass);
                params.put("X-SFAPI-Account",acc);
                return params;
            }
        };
        sr.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                pd.dismiss();
                Log.e("HttpClient", "error: " + error.toString());
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(sr);
    }

    private class getJSONeKtp extends AsyncTask<String, Void, String> {
        private String vug, da1, da2, da3;
        private ProgressDialog dd;

        private getJSONeKtp(String text, String user, String pass, String acc) {
            this.vug = text;
            this.da1 = user;
            this.da2 = pass;
            this.da3 = acc;
            dd = new ProgressDialog(LoginISS.this);
            dd.setMessage("Please Wait");
            dd.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            return GET(vug, da1, da2, da3);
        }

        @Override
        protected void onPostExecute(String result) {
            dd.dismiss();
            Toast.makeText(LoginISS.this,result,Toast.LENGTH_LONG).show();
            finish();
            Intent intent = new Intent(getApplicationContext(), MainActivityNew.class);
            intent.putExtra(ConversationActivity.KEY_JABBER_ID, username);
            intent.putExtra("success", "oke");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);
        }
    }


    public String GET(String url, String user, String pass, String acc) {
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet request = new HttpGet(url);
            request.setHeader("X-SFAPI-UserName",user);
            request.setHeader("X-SFAPI-UserPass",pass);
            request.setHeader("X-SFAPI-Account",acc);

            HttpResponse httpResponse = httpclient.execute(request);
            inputStream = httpResponse.getEntity().getContent();
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "-";
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}
