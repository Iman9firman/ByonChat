package com.byonchat.android;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
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
import com.byonchat.android.ISSActivity.LoginDB.UserDB;
import com.byonchat.android.ui.activity.MainActivityNew;
import com.byonchat.android.utils.Validations;
import com.google.android.gms.vision.L;

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
        db = dbHelper.getWritableDatabase();
        db.delete("user", null, null);
        Button erwgv = (Button) findViewById(R.id.loginBtn);
        EditText userID = (EditText) findViewById(R.id.login_userid);
        EditText passID = (EditText) findViewById(R.id.login_password);
        EditText accID = (EditText) findViewById(R.id.login_acc);

        accID.setText("issid");
       /* userID.setText("1701793");
        passID.setText("Pass1701793");*/

        /*userID.setText("TESTING");
        passID.setText("Testing1234");*/


        String testHardcode = "{\n" +
                "    \"MESSAGE\": \"LOGIN BERHASIL\",\n" +
                "    \"URITOKENS\": \"aXNzaWQ6MTcwMTc5MzoxMTkuMTAuMTc4LjU0OkpHWkJjREZBUzJWWklWOTBiMHN6Ymc9PToyMDE5MDMwNjE1MDE0OTowLDIsMzAsMA==\",\n" +
                "    \"STATUS\": true,\n" +
                "    \"RESULT\": [\n" +
                "        {\n" +
                "            \"ATASAN1_JOBTITLE\": \"Area Head RS Ciputra, Eka Hospital, RS Pelni and Paramount\",\n" +
                "            \"USER_NAME\": \"N9992\",\n" +
                "            \"ATASAN1_EMAIL\": \"agus@iss.co.id\",\n" +
                "            \"EMPLOYEE_PHOTOS\": \"https://sf.dataon.com/sf6/index.cfm?sfid=sys.sec.getimage&img=52EEAD80B7FDB3B31AFC8737ABFF9A94E1638C7EDB7F3EC97788B9BE19CDD2D8C952632F7EB06FBFAF76B1B154B1AE65B4ABF9C2CEDB468D93568FFECD1D41C4BF9D496BB068AEB2B4B070D2A9A67C3FA8FF01ED1BBAF19FA80B769240A04010EFC30CF2658FA3369EBE9D3799B0F290AC6DABBB&fname=201806/DO17590517_13401.JPG\",\n" +
                "            \"EMPLOYEE_NIK\": \"N9992\",\n" +
                "            \"ATASAN2_NIK\": \"0054065\",\n" +
                "            \"LIST_REQUESTER_ROLE\": \"N101468,N100012\",\n" +
                "            \"EMPLOYEE_NAME\": \"AGUSTINUS IRWANTO\",\n" +
                "            \"ATASAN1_NAMA\": \"AGUSTINUS IRWANTO\",\n" +
                "            \"DIVISION_CODE\": \"19KAS1\",\n" +
                "            \"EMPLOYEE_JOBTITLE\": \"SERVICE SUPERVISOR JAKARTA EKA HOSPITAL CLN [ISS-00625F0001]\",\n" +
                "            \"LIST_APPROVER_ROLE2\": \"\",\n" +
                "            \"DEPARTEMENT_CODE\": \"19KAS0107\",\n" +
                "            \"ATASAN1_NIK\": \"N9992\",\n" +
                "            \"ATASAN1_PHONE\": \"6281808884801\",\n" +
                "            \"DIVISION_NAME\": \"Key Account Segment 1\",\n" +
                "            \"ATASAN2_PHONE\": \"6281319906930\",\n" +
                "            \"EMPLOYEE_EMAIL\": \"agus@iss.co.id\",\n" +
                "            \"EMPLOYEE_PHONE\": \"6281808884801\",\n" +
                "            \"ATASAN2_EMAIL\": \"rusbandi@iss.co.id\",\n" +
                "            \"ATASAN2_JOBTITLE\": \"Area Head RS Ciputra, Eka Hospital\",\n" +
                "            \"ATASAN1_USER_NAME\": \"N9992\",\n" +
                "            \"ATASAN2_NAMA\": \"RUSBANDI\",\n" +
                "            \"EMPLOYEE_MULTIPLECOST\": \"ISS-00625F0001-EKA HOSPITAL CLN [ISS-00625F0001]\",\n" +
                "            \"MYROLE\": \"\",\n" +
                "            \"DEPARTEMENT_NAME\": \"Key Segment Healthcare\",\n" +
                "            \"ATASAN2_USER_NAME\": \"0054065\",\n" +
                "            \"LIST_APPROVER_ROLE1\": \"\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        erwgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                goVerif(userID.getText().toString(), passID.getText().toString(), accID.getText().toString());
                parseJSON(testHardcode);
                pd = new ProgressDialog(LoginISS.this);
                pd.setMessage("Please Wait");
                pd.show();
            }
        });
    }


    public void goVerif(String user, String pass, String acc) {
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
                       /* pd.dismiss();
                        Log.e("HttpClient", "error: " + error.toString());
                        Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();*/
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
    }

    private void parseJSON(String result) {
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
//                Toast.makeText(LoginISS.this, "Atasan 1 : "+ATASAN_1_NAMA+", ATASAN 2 : "+ATASAN_2_NAMA+", Requester : "+EMPLOYEE_NAME, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), MainActivityNew.class);
                intent.putExtra(ConversationActivity.KEY_JABBER_ID, username);
                intent.putExtra("success", "oke");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
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
