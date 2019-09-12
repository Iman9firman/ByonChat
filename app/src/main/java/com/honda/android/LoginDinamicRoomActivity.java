package com.honda.android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.honda.android.FragmentDinamicRoom.LoginRoomActivity;
import com.honda.android.communication.MessengerConnectionService;
import com.honda.android.communication.NetworkInternetConnectionStatus;
import com.honda.android.provider.BotListDB;
import com.honda.android.provider.MessengerDatabaseHelper;
import com.honda.android.provider.RoomsDB;
import com.honda.android.ui.activity.MainActivityNew;
import com.honda.android.utils.Validations;
import com.honda.android.utils.ValidationsKey;
import com.squareup.picasso.Picasso;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * ByonChat Login to GroupRoom use Username & Password
 * Created by Aziz Fikri on 04/13/2018.
 * Status: on Process
 */

public class LoginDinamicRoomActivity extends AppCompatActivity {
    ImageView imageView;
    private RoomsDB roomsDB;
    AlertDialog alertDialogExit;
    Integer firstTab = 0;
    String colorText = "";
    String description = "";
    String color = "006b9c";
    String username = "";
    int position = 0;
    Integer shakeCount = 0;
    private UserLoginTask mAuthTask = null;

    MessengerDatabaseHelper messengerHelper = null;
    BotListDB botListDB = null;
    String name = "";
    String content = "";
    String bcakdrop = "";
    String current = "";
    String icon = "";
    String desc = "", realname = "", link = "", type = "";
    Context context;

    ProgressDialog progressDialog;

    ArrayList<HashMap<String, String>> jsonArrayList;
    private String TAG = MainActivity.class.getSimpleName();
    String usr, p, u;

    Button loginBtn;
    CardView contentMain;
    TextInputEditText loginUsr;
    TextInputEditText loginPass;
    TextInputLayout etNikLayout, etPasswordLayout;
    TextView login_roomname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_login);

        login_roomname = (TextView) findViewById(R.id.login_roomname);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        contentMain = (CardView) findViewById(R.id.content_main);

        contentMain.setVisibility(View.GONE);

        loginUsr = (TextInputEditText) findViewById(R.id.login_userid);
        loginPass = (TextInputEditText) findViewById(R.id.login_password);
        etNikLayout = (TextInputLayout) findViewById(R.id.etNikLayout);
        etPasswordLayout = (TextInputLayout) findViewById(R.id.etPasswordLayout);


        imageView = (ImageView) findViewById(R.id.imageView);
        jsonArrayList = new ArrayList<>();

        final Intent inti = getIntent();

        username = inti.getStringExtra(ConversationActivity.KEY_JABBER_ID);

        if (inti.getStringExtra("firstTab") != null) {
            current = inti.getStringExtra("firstTab");
        }

        if (roomsDB == null) {
            roomsDB = new RoomsDB(LoginDinamicRoomActivity.this);
        }
        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(getApplicationContext());
        }
        if (botListDB == null) {
            botListDB = BotListDB.getInstance(getApplicationContext());
        }

        if (mAuthTask != null) {
            mAuthTask = null;
        }

        showProgress();
        mAuthTask = new UserLoginTask();
        mAuthTask.execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + "/bc_voucher_client/webservice/list_api/login_expired.php", username);

        Cursor cur = botListDB.getSingleRoom(username);

        if (cur.getCount() > 0) {
            name = cur.getString(cur.getColumnIndex(BotListDB.ROOM_REALNAME));
            if (jsonResultType(cur.getString(cur.getColumnIndex(BotListDB.ROOM_COLOR)), "a").equalsIgnoreCase("error")) {
                if (NetworkInternetConnectionStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
                    finish();
                    Intent ii = new Intent(LoginDinamicRoomActivity.this, LoadingGetTabRoomActivity.class);
                    ii.putExtra(ConversationActivity.KEY_JABBER_ID, username);
                    ii.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(ii);
                } else {
                    Toast.makeText(LoginDinamicRoomActivity.this, "No Internet Akses", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }

            color = jsonResultType(cur.getString(cur.getColumnIndex(BotListDB.ROOM_COLOR)), "a");
            colorText = jsonResultType(cur.getString(cur.getColumnIndex(BotListDB.ROOM_COLOR)), "b");
            content = cur.getString(cur.getColumnIndex(BotListDB.ROOM_CONTENT));
            icon = cur.getString(cur.getColumnIndex(BotListDB.ROOM_ICON));

            if (current.equalsIgnoreCase("")) {
                current = cur.getString(cur.getColumnIndex(BotListDB.ROOM_FIRST_TAB));
            }
            if (color == null || color.equalsIgnoreCase("") || color.equalsIgnoreCase("null")) {
                color = "006b9c";
            }
            if (colorText == null || colorText.equalsIgnoreCase("") || colorText.equalsIgnoreCase("null")) {
                colorText = "ffffff";
            }
            Picasso.with(LoginDinamicRoomActivity.this).load(icon).into(imageView);

            login_roomname.setText(name);

            RelativeLayout someView = (RelativeLayout) findViewById(R.id.all_background);
            someView.setBackgroundColor(Color.parseColor("#" + color));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(Color.parseColor("#" + color));
            }

            //loginBtn.setCardBackgroundColor(Color.parseColor("#" + color));
        }

        final Intent intent = getIntent();
        usr = intent.getStringExtra(ConversationActivity.KEY_JABBER_ID);
        p = intent.getStringExtra("p");
        u = intent.getStringExtra("u");

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (loginUsr.getText().toString().equalsIgnoreCase("")) {
                    etNikLayout.setError("Please input NIK");
                    return;
                }
                if (loginPass.getText().toString().equalsIgnoreCase("")) {
                    etPasswordLayout.setError("Please input Password");
                    return;
                }

                if (mAuthTask != null) {
                    mAuthTask = null;
                }
                showProgress();
                mAuthTask = new UserLoginTask();
                mAuthTask.execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + "/bc_voucher_client/webservice/list_api/login.php", username, loginUsr.getText().toString(), loginPass.getText().toString());

            }

        });
    }

    private String jsonResultType(String json, String type) {
        String hasil = "";
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(json);
        } catch (JSONException e) {
            hasil = "error";
            e.printStackTrace();
        }
        if (jObject != null) {
            try {
                hasil = jObject.getString(type);
            } catch (JSONException e) {
                hasil = "error";
                e.printStackTrace();
            }
        }

        return hasil;
    }


    private void showProgress() {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();


    }


    public class UserLoginTask extends AsyncTask<String, String, String> {
        String error = "";

        @Override
        protected String doInBackground(String... params) {
            if (params.length == 2) {
                postData(params[0], params[1], "", "");
            } else {
                postData(params[0], params[1], params[2], params[3]);
            }


            return null;
        }

        protected void onPostExecute(String result) {
            mAuthTask = null;
            progressDialog.dismiss();

            if (error.length() > 0) {
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            progressDialog.dismiss();
        }

        public void postData(String valueIWantToSend, String username_room, String nik, String pass) {
            // Create a new HttpClient and Post Header

            try {
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
                HttpConnectionParams.setSoTimeout(httpParameters, 5000);
                HttpClient httpclient = new DefaultHttpClient(httpParameters);
                HttpPost httppost = new HttpPost(valueIWantToSend);

                Log.e(TAG, "postData: " + valueIWantToSend);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("username_room", username_room));
                nameValuePairs.add(new BasicNameValuePair("bc_user", messengerHelper.getMyContact().getJabberId()));

                if (!nik.equalsIgnoreCase("") && !pass.equalsIgnoreCase("")) {
                    nameValuePairs.add(new BasicNameValuePair("nik", nik));
                    nameValuePairs.add(new BasicNameValuePair("password", pass));
                    nameValuePairs.add(new BasicNameValuePair("is_fingerprint", "0"));
                }



                /*String deviceSerial = android.os.Build.SERIAL;
                String deviceMan = android.os.Build.MANUFACTURER;
                String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                nameValuePairs.add(new BasicNameValuePair("sn_device", deviceMan + "|" + deviceSerial + "|" + android_id));*/

                Log.w("hasil", nameValuePairs.toString());
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                int status = response.getStatusLine().getStatusCode();
                Log.w("kask", status + "");
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);

                    Log.w("hewews", data);

                    try {
                        JSONObject jsonRootObject = new JSONObject(data);
                        if (jsonRootObject.getString("success").equalsIgnoreCase("0")) {
                            if (!nik.equalsIgnoreCase("") && !pass.equalsIgnoreCase("")) {
                                final String message = jsonRootObject.getString("message");
                                LoginDinamicRoomActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                LoginDinamicRoomActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        contentMain.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        } else if (jsonRootObject.getString("success").equalsIgnoreCase("2")) {
                            final String message = jsonRootObject.getString("message");
                            LoginDinamicRoomActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                }
                            });

                            finish();
                        } else {
                            new Validations().getInstance(getApplicationContext()).setTimebyId(25);
                            finish();
                            Intent intent = new Intent(getApplicationContext(), MainActivityNew.class);
                            intent.putExtra(ConversationActivity.KEY_JABBER_ID, username_room);
                            intent.putExtra("success", "");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getApplicationContext().startActivity(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        error = data;
                    }
                } else {
                    error = "Tolong periksa koneksi internet.";
                }

            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                LoginDinamicRoomActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Maaf gagal login, karena ada masalah pada data koneksi.  Terimakasih", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (ClientProtocolException e) {
                LoginDinamicRoomActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Maaf gagal login, karena ada masalah pada data koneksi.  Terimakasih", Toast.LENGTH_LONG).show();
                    }
                });
                // TODO Auto-generated catch block
            } catch (IOException e) {
                LoginDinamicRoomActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Maaf gagal login, karena ada masalah pada data koneksi.  Terimakasih", Toast.LENGTH_LONG).show();
                    }
                });
                // TODO Auto-generated catch block
            }
        }

    }

}
