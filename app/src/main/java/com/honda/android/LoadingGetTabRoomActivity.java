package com.honda.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.honda.android.ISSActivity.LoginDB.UserDB;
import com.honda.android.communication.MessengerConnectionService;
import com.honda.android.provider.BotListDB;
import com.honda.android.provider.Contact;
import com.honda.android.provider.MessengerDatabaseHelper;
import com.honda.android.provider.Rooms;
import com.honda.android.provider.RoomsDetail;
import com.honda.android.ui.activity.MainActivityNew;
import com.honda.android.ui.activity.MainBaseActivityNew;
import com.honda.android.utils.Validations;
import com.googlecode.mp4parser.srt.SrtParser;
import com.scottyab.rootbeer.RootBeer;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadingGetTabRoomActivity extends AppCompatActivity {

    public static Intent generateIntent(Context context, String username, String targetUrl) {
        Intent intent = new Intent(context, LoadingGetTabRoomActivity.class);
        intent.putExtra(ConversationActivity.KEY_JABBER_ID, username);
        if (targetUrl != null)
            intent.putExtra(ConversationActivity.KEY_TITLE, targetUrl);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static Intent generateISS(Context context, String result, String bc_user) {
        Intent intent = new Intent(context, LoadingGetTabRoomActivity.class);
        intent.putExtra("newday", result);
        intent.putExtra("bcUser", bc_user);
        intent.putExtra("iss", "ya");
        return intent;
    }

    String finalPath = "/bc_voucher_client/webservice/get_tab_rooms.php";
    String linkPath = "https://" + MessengerConnectionService.HTTP_SERVER;
    String GETTAB = linkPath + finalPath;
    BotListDB botListDB;
    String targetUrl;
    String bc_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);
        if (botListDB == null) {
            botListDB = BotListDB.getInstance(this);
        }

        String iss = getIntent().getStringExtra("iss");
        if (iss == null) {
            String room_name = getIntent().getStringExtra(ConversationActivity.KEY_JABBER_ID);
            targetUrl = getIntent().getStringExtra(ConversationActivity.KEY_TITLE);

            if (targetUrl != null) {
                GETTAB = targetUrl + finalPath;
            } else {
                targetUrl = linkPath;
            }

            new Refresh().execute(GETTAB, room_name);
        } else {
            targetUrl = "https://bb.byonchat.com/bc_voucher_client/webservice/get_tab_rooms_iss.php";
            String newday = getIntent().getStringExtra("newday");
            extractResult(newday);
            bc_user = getIntent().getStringExtra("bcUser");
        }
    }

    private void extractResult(String result) {
        try {
            JSONObject jsonRootObject = new JSONObject(result);
            String username = jsonRootObject.getString("username_room");
            String content = jsonRootObject.getString("tab_room");
            String realname = jsonRootObject.getString("nama_display");
            String icon = jsonRootObject.getString("icon"); // byonchat
            String backdrop = jsonRootObject.getString("backdrop")
                    .equalsIgnoreCase("https://" + MessengerConnectionService.HTTP_SERVER + "/mediafiles/") ? ""
                    : jsonRootObject.getString("backdrop"); //null default kita
            String color = jsonRootObject.getString("color")
                    .equalsIgnoreCase("null") ? "FFFFFF"
                    : jsonRootObject.getString("color");  //null putih
            String lastUpdate = jsonRootObject.getString("last_update");
            String firstTab = jsonRootObject.getString("current_tab");
            String textColor = jsonRootObject.getString("color_text")
                    .equalsIgnoreCase("null") ? "000000"
                    : jsonRootObject.getString("color_text"); //null hitam
            String description = jsonRootObject.getString("description");
            String officer = jsonRootObject.getString("officer");
            String protect = "0";

            if (jsonRootObject.has("password_protected")) {
                protect = jsonRootObject.getString("password_protected");
            }

            //ini untuk delete
            botListDB.deleteRoomsbyTAB(username);
            String lu = "";
            Cursor cursor = botListDB.getSingleRoom(username);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            String time_str = dateFormat.format(cal.getTime());
            if (cursor.getCount() > 0) {
                lu = cursor.getString(cursor.getColumnIndexOrThrow(BotListDB.ROOM_LASTUPDATE));
                if (!lu.equalsIgnoreCase(lastUpdate)) {
                    botListDB.deleteRoomsbyTAB(username);
                    Rooms rooms = new Rooms(username, realname, content, jsonCreateType(color, textColor, description, officer, targetUrl, protect), backdrop, lastUpdate, icon, firstTab, time_str);
                    botListDB.insertRooms(rooms);
                }
            } else {
                Rooms rooms = new Rooms(username, realname, content, jsonCreateType(color, textColor, description, officer, targetUrl, protect), backdrop, lastUpdate, icon, firstTab, time_str);
                botListDB.insertRooms(rooms);
            }
            cursor.close();

            new Validations().getInstance(getApplicationContext()).removeById(26);
            new Validations().getInstance(getApplicationContext()).removeById(25);

            Intent intent = new Intent(getApplicationContext(), MainActivityNew.class);
            intent.putExtra(ConversationActivity.KEY_JABBER_ID, bc_user);
            intent.putExtra("success", "oke");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
            Intent intent = new Intent(getApplicationContext(), MainActivityNew.class);
            intent.putExtra(ConversationActivity.KEY_JABBER_ID, bc_user);
            intent.putExtra("success", "oke");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);
            finish();
        }
    }

    private class Refresh extends AsyncTask<String, String, String> {

        String error = "";

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            postData(params[0], params[1]);
            return null;
        }

        protected void onPostExecute(String result) {
            if (error.length() > 0) {
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
            }
            //dialog.dismiss();
        }

        protected void onProgressUpdate(String... string) {
        }

        public void postData(String valueIWantToSend, String usr) {
            // Create a new HttpClient and Post Header

            try {
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
                HttpConnectionParams.setSoTimeout(httpParameters, 20000);
                HttpClient httpclient = new DefaultHttpClient(httpParameters);
                HttpPost httppost = new HttpPost(valueIWantToSend);

                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("username_room", usr));
                MessengerDatabaseHelper messengerHelper = null;
                if (messengerHelper == null) {
                    messengerHelper = MessengerDatabaseHelper.getInstance(getApplicationContext());
                }


                Contact contact = messengerHelper.getMyContact();
                RootBeer rootBeer = new RootBeer(getApplicationContext());
                String roor = "";
                if (rootBeer.isRooted()) {
                    roor = " (ROOTED)";
                }
                nameValuePairs.add(new BasicNameValuePair("app_version", getString(R.string.app_version) + roor));
                nameValuePairs.add(new BasicNameValuePair("app_company", getString(R.string.app_company)));
                nameValuePairs.add(new BasicNameValuePair("bc_user", contact.getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("id_client", getString(R.string.id_client)));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                int status = response.getStatusLine().getStatusCode();
                Log.w("gansdhi", status + "");
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);

                    Log.w("BukanSaya", data);
                    new Validations().getInstance(getApplicationContext()).setLastVersion(getApplicationContext().getResources().getString(R.string.app_version));

                    try {
                        JSONObject jsonRootObject = new JSONObject(data);
                        String username = jsonRootObject.getString("username_room");
                        String content = jsonRootObject.getString("tab_room");
                        String realname = jsonRootObject.getString("nama_display");
                        String icon = jsonRootObject.getString("icon"); // byonchat
                        String backdrop = jsonRootObject.getString("backdrop")
                                .equalsIgnoreCase("https://" + MessengerConnectionService.HTTP_SERVER + "/mediafiles/") ? ""
                                : jsonRootObject.getString("backdrop"); //null default kita
                        String color = jsonRootObject.getString("color")
                                .equalsIgnoreCase("null") ? "FFFFFF"
                                : jsonRootObject.getString("color");  //null putih
                        String lastUpdate = jsonRootObject.getString("last_update");
                        String firstTab = jsonRootObject.getString("current_tab");
                        String textColor = jsonRootObject.getString("color_text")
                                .equalsIgnoreCase("null") ? "000000"
                                : jsonRootObject.getString("color_text"); //null hitam
                        String description = jsonRootObject.getString("description");
                        String officer = jsonRootObject.getString("officer");
                        String protect = "0";

                        Log.w("skidrow", color + " -- " + icon + " -- " + textColor + " -- " + backdrop);
                        if (jsonRootObject.has("password_protected")) {
                            protect = jsonRootObject.getString("password_protected");
                        }


                        //ini untuk delete
                        botListDB.deleteRoomsbyTAB(username);
                        String lu = "";
                        Cursor cursor = botListDB.getSingleRoom(username);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Calendar cal = Calendar.getInstance();
                        String time_str = dateFormat.format(cal.getTime());
                        if (cursor.getCount() > 0) {
                            lu = cursor.getString(cursor.getColumnIndexOrThrow(BotListDB.ROOM_LASTUPDATE));
                            if (!lu.equalsIgnoreCase(lastUpdate)) {
                                botListDB.deleteRoomsbyTAB(username);
                                Rooms rooms = new Rooms(username, realname, content, jsonCreateType(color, textColor, description, officer, targetUrl, protect), backdrop, lastUpdate, icon, firstTab, time_str);
                                botListDB.insertRooms(rooms);
                            }
                        } else {
                            Rooms rooms = new Rooms(username, realname, content, jsonCreateType(color, textColor, description, officer, targetUrl, protect), backdrop, lastUpdate, icon, firstTab, time_str);
                            botListDB.insertRooms(rooms);
                        }
                        cursor.close();


                        new Validations().getInstance(getApplicationContext()).removeById(26);
                        new Validations().getInstance(getApplicationContext()).removeById(25);

                        Intent intent = new Intent(getApplicationContext(), MainActivityNew.class);
                        intent.putExtra(ConversationActivity.KEY_JABBER_ID, usr);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(ConversationActivity.KEY_TITLE, targetUrl);
                        getApplicationContext().startActivity(intent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Intent intent = new Intent(getApplicationContext(), MainActivityNew.class);
                        intent.putExtra(ConversationActivity.KEY_JABBER_ID, usr);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(ConversationActivity.KEY_TITLE, targetUrl);
                        getApplicationContext().startActivity(intent);
                        error = "Tolong periksa koneksi internet2.";
                        finish();
                    }
                } else {
                    Intent intent = new Intent(getApplicationContext(), MainActivityNew.class);
                    intent.putExtra(ConversationActivity.KEY_JABBER_ID, usr);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(ConversationActivity.KEY_TITLE, targetUrl);
                    getApplicationContext().startActivity(intent);
                    error = "Tolong periksa koneksi internet3.";
                    finish();
                }

            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                LoadingGetTabRoomActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Tolong periksa koneksi internet4.", Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
            } catch (ClientProtocolException e) {
                LoadingGetTabRoomActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Tolong periksa koneksi internet5.", Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
                // TODO Auto-generated catch block
            } catch (IOException e) {
                LoadingGetTabRoomActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Tolong periksa koneksi internet7.", Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
                // TODO Auto-generated catch block
            }
        }

    }

    private String jsonCreateType(String idContent, String type, String desc, String of, String ss, String pro) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("a", idContent);
            obj.put("b", type);
            obj.put("c", desc);
            obj.put("d", of);
            obj.put("e", ss);
            obj.put("p", pro);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }

}
