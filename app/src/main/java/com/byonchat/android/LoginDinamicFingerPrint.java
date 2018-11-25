package com.byonchat.android;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.RoomsDB;
import com.byonchat.android.utils.FingerprintHandler;
import com.byonchat.android.utils.Validations;
import com.byonchat.android.utils.ValidationsKey;
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
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class LoginDinamicFingerPrint extends AppCompatActivity {

    private KeyStore keyStore;
    private static final String KEY_NAME = "ByonChatKey";
    private Cipher cipher;
    private TextView textView;
    String colorText = "";
    String description = "";
    String color = "006b9c";
    String username = "";
    MessengerDatabaseHelper messengerHelper = null;
    BotListDB botListDB = null;
    private RoomsDB roomsDB;
    String name = "";
    String content = "";
    String current = "";
    String icon = "";
    String desc = "", realname = "", link = "", type = "";
    Context context;
    ProgressDialog progressDialog;
    TextView login_roomname;
    ImageView imageView;

    private UserLoginTask mAuthTask = null;
    CardView contentMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint);

        final Intent inti = getIntent();

        username = inti.getStringExtra(ConversationActivity.KEY_JABBER_ID);
        contentMain = (CardView) findViewById(R.id.content_main);

        contentMain.setVisibility(View.GONE);
        if (roomsDB == null) {
            roomsDB = new RoomsDB(LoginDinamicFingerPrint.this);
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

        String ss = getIntent().getStringExtra(ConversationActivity.KEY_MESSAGE_FORWARD);
        if (ss == null) {
            showProgress();
            mAuthTask = new UserLoginTask();
            mAuthTask.execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + "/bc_voucher_client/webservice/list_api/login_expired.php", username);

        } else {
            showProgress();
            mAuthTask = new UserLoginTask();
            mAuthTask.execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + "/bc_voucher_client/webservice/list_api/login.php", username, "is_fingerprint");

        }


        Cursor cur = botListDB.getSingleRoom(username);

        if (cur.getCount() > 0) {
            name = cur.getString(cur.getColumnIndex(BotListDB.ROOM_REALNAME));

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

            login_roomname = (TextView) findViewById(R.id.login_roomname);
            imageView = (ImageView) findViewById(R.id.imageView);

            Picasso.with(this).load(icon).into(imageView);

            login_roomname.setText("Login to " + name);

            RelativeLayout someView = (RelativeLayout) findViewById(R.id.all_background);
            someView.setBackgroundColor(Color.parseColor("#" + color));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(Color.parseColor("#" + color));
            }

            //loginBtn.setCardBackgroundColor(Color.parseColor("#" + color));
        }


        // Initializing both Android Keyguard Manager and Fingerprint Manager
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        }

        textView = (TextView) findViewById(R.id.errorText);

        // Check whether the device has a Fingerprint sensor.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!fingerprintManager.isHardwareDetected()) {
                /**
                 * An error message will be displayed if the device does not contain the fingerprint hardware.
                 * However if you plan to implement a default authentication method,
                 * you can redirect the user to a default authentication activity from here.
                 * Example:
                 * Intent intent = new Intent(this, DefaultAuthenticationActivity.class);
                 * startActivity(intent);
                 */
                textView.setText("Your Device does not have a Fingerprint Sensor");
            } else {
                // Checks whether fingerprint permission is set on manifest
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                    textView.setText("Fingerprint authentication permission not enabled");
                } else {
                    // Check whether at least one fingerprint is registered
                    if (!fingerprintManager.hasEnrolledFingerprints()) {
                        textView.setText("Register at least one fingerprint in Settings");
                    } else {
                        // Checks whether lock screen security is enabled or not
                        if (!keyguardManager.isKeyguardSecure()) {
                            textView.setText("Lock screen security not enabled in Settings");
                        } else {
                            generateKey();

                            if (cipherInit()) {
                                FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                                FingerprintHandler helper = new FingerprintHandler(this, username, messengerHelper.getMyContact().getJabberId());
                                helper.startAuth(fingerprintManager, cryptoObject);
                                Log.w("CRYPTO", cryptoObject + "");
                            }
                        }
                    }
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }

        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            Log.w("KEY GEN", keyGenerator + "");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get KeyGenerator instance", e);
        }

        try {
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
            Log.w("Cek duluan", "mungkin");
        } catch (NoSuchAlgorithmException |
                InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            Log.w("CIPHER", cipher + "");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);

            Log.w("KEY", key + "");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
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

    public static void LoadingLogin() {
        Log.w("kimi", "lelex");
    }

    public class UserLoginTask extends AsyncTask<String, String, String> {
        String error = "";

        @Override
        protected String doInBackground(String... params) {
            if (params.length == 2) {
                postData(params[0], params[1], "");
            } else {
                postData(params[0], params[1], params[2]);
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

        public void postData(String valueIWantToSend, String username_room, String nik) {
            // Create a new HttpClient and Post Header

            try {
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
                HttpConnectionParams.setSoTimeout(httpParameters, 5000);
                HttpClient httpclient = new DefaultHttpClient(httpParameters);
                HttpPost httppost = new HttpPost(valueIWantToSend);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("username_room", username_room));
                nameValuePairs.add(new BasicNameValuePair("bc_user", messengerHelper.getMyContact().getJabberId()));

                if (nik.equalsIgnoreCase("is_fingerprint")) {
                    nameValuePairs.add(new BasicNameValuePair("is_fingerprint", "1"));
                }


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
                            if (!nik.equalsIgnoreCase("is_fingerprint")) {

                                LoginDinamicFingerPrint.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        contentMain.setVisibility(View.VISIBLE);

                                    }
                                });
                            } else {
                                final String message = jsonRootObject.getString("message");
                                LoginDinamicFingerPrint.this.runOnUiThread(new Runnable() {
                                    public void run() {

                                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        } else if (jsonRootObject.getString("success").equalsIgnoreCase("2")) {
                            final String message = jsonRootObject.getString("message");
                            LoginDinamicFingerPrint.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                }
                            });

                            finish();
                        } else {
                            new Validations().getInstance(getApplicationContext()).setTimebyId(25);
                            finish();
                            Intent intent = new Intent(getApplicationContext(), ByonChatMainRoomActivity.class);
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
                LoginDinamicFingerPrint.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Maaf gagal login, karena ada masalah pada data koneksi.  Terimakasih", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (ClientProtocolException e) {
                LoginDinamicFingerPrint.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Maaf gagal login, karena ada masalah pada data koneksi.  Terimakasih", Toast.LENGTH_LONG).show();
                    }
                });
                // TODO Auto-generated catch block
            } catch (IOException e) {
                LoginDinamicFingerPrint.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Maaf gagal login, karena ada masalah pada data koneksi.  Terimakasih", Toast.LENGTH_LONG).show();
                    }
                });
                // TODO Auto-generated catch block
            }
        }

    }

}