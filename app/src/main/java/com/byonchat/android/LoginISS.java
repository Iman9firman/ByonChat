package com.byonchat.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.byonchat.android.ISSActivity.LoginDB.UserDB;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.utils.Validations;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class LoginISS extends AppCompatActivity {
    private String username;
    private ProgressDialog pd;
    String sukses;
    UserDB dbHelper;
    SQLiteDatabase db;
    private MessengerDatabaseHelper dbhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_for_iss_room);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.iss_default));
            getWindow().setStatusBarColor(getResources().getColor(R.color.iss_default));
        }

        final Intent inti = getIntent();

        username = inti.getStringExtra(ConversationActivity.KEY_JABBER_ID);
        dbHelper = new UserDB(this);
        dbhelper = MessengerDatabaseHelper.getInstance(this);
        db = dbHelper.getWritableDatabase();
        db.delete("user", null, null);
        Button erwgv = (Button) findViewById(R.id.loginBtn);
        EditText userID = (EditText) findViewById(R.id.login_userid);
        EditText passID = (EditText) findViewById(R.id.login_password);
        EditText accID = (EditText) findViewById(R.id.login_acc);

        accID.setText("issid");
        accID.setVisibility(View.INVISIBLE);
        erwgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userID.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(getApplicationContext(), "Please enter your username!",Toast.LENGTH_SHORT).show();
                    userID.setError("Can't Empty");
                }else if(passID.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(getApplicationContext(), "Please enter your password!",Toast.LENGTH_SHORT).show();
                    passID.setError("Can't Empty");
                }else {
                    pd = new ProgressDialog(LoginISS.this);
                    pd.setMessage("Please Wait");
                    pd.show();
                    Map<String, String> params = new HashMap<>();
                    params.put("username", userID.getText().toString());
                    params.put("password", passID.getText().toString());
                    params.put("bc_user", dbhelper.getMyContact().getJabberId());

                    LoginThis("https://bb.byonchat.com/bc_voucher_client/webservice/get_tab_rooms_iss.php", params, true);
                }
            }
        });
    }

    private void LoginThis(String Url, Map<String, String> params2, Boolean hide) {

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        StringRequest sr = new StringRequest(Request.Method.POST, Url,
                response -> {
                    if (hide) {
                        Log.w("sukses harusee", response);
                        try {
                            JSONObject jsonRootObject = new JSONObject(response);
                            parseJSON(response, jsonRootObject.getString("json_iss"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                },
                error -> {
                    Log.w("Erroe harusee",error);
                    pd.dismiss();
                }
        ) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return params2;
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
                Log.e("HttpClient", "error: " + error.toString());
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(sr);
    }

    /*public void goVerif(String user, String pass, String acc) {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest sr = new StringRequest(Request.Method.GET, "https://issapi.dataon.com/sfapi/index.cfm?endpoint=/issid_SF_EO_cekuser/" + user + "/BYONCHAT",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("HttpClient", "success! response: " + response);
//                        Toast.makeText(LoginISS.this,response,Toast.LENGTH_LONG).show();
                        pd.dismiss();
                        parseJSON(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       *//* pd.dismiss();
                        Log.e("HttpClient", "error: " + error.toString());
                        Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();*//*
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("X-SFAPI-UserName", user);
                params.put("X-SFAPI-UserPass", pass);
                params.put("X-SFAPI-Account", acc);
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
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(sr);
    }*/

    private void parseJSON(String allres, String result) {
        Log.w("Res LOgs harusee",result);
        String[] dataLOG = new String[0];
        try {
            JSONObject start = new JSONObject(result);
            sukses = start.getString("MESSAGE");
            String token = start.getString("URITOKENS");
            String status = start.get("STATUS") + "";
            JSONArray data = start.getJSONArray("RESULT");
            JSONObject jsonObject = data.getJSONObject(0);
            String USERNAME = jsonObject.getString("USER_NAME");
            String EMPLOYEE_NAME = jsonObject.getString("EMPLOYEE_NAME");
            String EMPLOYEE_EMAIL = jsonObject.getString("EMPLOYEE_EMAIL");
            String EMPLOYEE_NIK = jsonObject.getString("EMPLOYEE_NIK");
            String EMPLOYEE_JT = jsonObject.getString("EMPLOYEE_JOBTITLE");
            String EMPLOYEE_MULTICOST = jsonObject.getString("EMPLOYEE_MULTIPLECOST");
            String EMPLOYEE_PHONE = jsonObject.getString("EMPLOYEE_PHONE");
            String EMPLOYEE_PHOTOS = jsonObject.getString("EMPLOYEE_PHOTOS");
            String ATASAN_1_USERNAME = jsonObject.getString("ATASAN1_USER_NAME");
            String ATASAN_1_EMAIL = jsonObject.getString("ATASAN1_EMAIL");
            String ATASAN_1_NIK = jsonObject.getString("ATASAN1_NIK");
            String ATASAN_1_JT = jsonObject.getString("ATASAN1_JOBTITLE");
            String ATASAN_1_NAMA = jsonObject.getString("ATASAN1_NAMA");
            String ATASAN_1_PHONE = jsonObject.getString("ATASAN1_PHONE");
            String DIVISION_CODE = jsonObject.getString("DIVISION_CODE");
            String DIVISION_NAME = jsonObject.getString("DIVISION_NAME");
            String DEPARTEMEN_CODE = jsonObject.getString("DEPARTEMENT_CODE");
            String DEPARTEMEN_NAME = jsonObject.getString("DEPARTEMENT_NAME");
            String ATASAN_2_USERNAME = jsonObject.getString("ATASAN2_USER_NAME");
            String ATASAN_2_EMAIL = jsonObject.getString("ATASAN2_EMAIL");
            String ATASAN_2_NIK = jsonObject.getString("ATASAN2_NIK");
            String ATASAN_2_JT = jsonObject.getString("ATASAN2_JOBTITLE");
            String ATASAN_2_NAMA = jsonObject.getString("ATASAN2_NAMA");
            String ATASAN_2_PHONE = jsonObject.getString("ATASAN2_PHONE");
            String LIST_APPROVE_ROLE1 = jsonObject.getString("LIST_APPROVER_ROLE1");
            String LIST_APPROVE_ROLE2 = jsonObject.getString("LIST_APPROVER_ROLE2");
            String LIST_REQ_ROLE = jsonObject.getString("LIST_REQUESTER_ROLE");
            String MY_ROLE = jsonObject.getString("MYROLE");

            dataLOG = new String[]{token, status, USERNAME, EMPLOYEE_NAME, EMPLOYEE_EMAIL, EMPLOYEE_NIK,
                    EMPLOYEE_JT, EMPLOYEE_MULTICOST, EMPLOYEE_PHONE, EMPLOYEE_PHOTOS, ATASAN_1_USERNAME,
                    ATASAN_1_EMAIL, ATASAN_1_NIK, ATASAN_1_JT, ATASAN_1_NAMA, ATASAN_1_PHONE, DIVISION_CODE,
                    DIVISION_NAME, DEPARTEMEN_CODE, DEPARTEMEN_NAME, ATASAN_2_USERNAME, ATASAN_2_EMAIL,
                    ATASAN_2_NIK, ATASAN_2_JT, ATASAN_2_NAMA, ATASAN_2_PHONE, LIST_APPROVE_ROLE1, LIST_APPROVE_ROLE2,
                    LIST_REQ_ROLE, MY_ROLE};

            db.execSQL(getString(R.string.sql_insert_log_iss), dataLOG);


            if (sukses.equalsIgnoreCase("LOGIN BERHASIL")) {
                new Validations().getInstance(getApplicationContext()).setTimebyId(26);
                //Not fix!!! Change how to detect as reliever with another ways
                JSONObject jsonRootObject = new JSONObject(allres);
                JSONArray tab = jsonRootObject.getJSONArray("tab_room");
                for (int i = 0; i < tab.length(); i++ ){
                    String name = tab.getJSONObject(i).getString("tab_name");
                    if (name.equalsIgnoreCase("Job Call")){
                        new Validations().getInstance(getApplicationContext()).setShareLocOnOff(true);
                    }
                }
//                Toast.makeText(LoginISS.this, "Atasan 1 : "+ATASAN_1_NAMA+", ATASAN 2 : "+ATASAN_2_NAMA+", Requester : "+EMPLOYEE_NAME, Toast.LENGTH_LONG).show();
                /*Intent intent = new Intent(getApplicationContext(), MainActivityNew.class);
                intent.putExtra(ConversationActivity.KEY_JABBER_ID, username);
                intent.putExtra("success", "oke");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);*/
                Intent ii = LoadingGetTabRoomActivity.generateISS(getApplicationContext(),allres,username);
                startActivity(ii);
                pd.dismiss();
                finish();
            } else {
                Toast.makeText(LoginISS.this, "Username dan password anda salah", Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(LoginISS.this, "Terjadi Kesalah di Server ISS. Terimakasih", Toast.LENGTH_LONG).show();
        }


    }


    public String GET(String url, String user, String pass, String acc) {
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet request = new HttpGet(url);
            request.setHeader("X-SFAPI-UserName", user);
            request.setHeader("X-SFAPI-UserPass", pass);
            request.setHeader("X-SFAPI-Account", acc);

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
