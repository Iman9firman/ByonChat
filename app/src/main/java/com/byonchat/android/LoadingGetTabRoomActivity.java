package com.byonchat.android;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.Rooms;
import com.byonchat.android.provider.RoomsDetail;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LoadingGetTabRoomActivity extends AppCompatActivity {

    String finalPath = "/bc_voucher_client/webservice/get_tab_rooms.php";
    String linkPath = "https://" + MessengerConnectionService.HTTP_SERVER;
    String GETTAB = linkPath + finalPath;
    BotListDB botListDB;
    String targetUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);
        if (botListDB == null) {
            botListDB = BotListDB.getInstance(this);
        }

        String room_name = getIntent().getStringExtra(ConversationActivity.KEY_JABBER_ID);
        targetUrl = getIntent().getStringExtra(ConversationActivity.KEY_TITLE);

        if (targetUrl != null) {
            GETTAB = targetUrl + finalPath;
            Log.w("papa1",targetUrl);
        } else {
            targetUrl = linkPath;
            Log.w("papa2",targetUrl);
        }

        new Refresh().execute(GETTAB, room_name);
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
                nameValuePairs.add(new BasicNameValuePair("bc_user", contact.getJabberId()));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);

                    Log.w("ww", data);

                    try {
                        JSONObject jsonRootObject = new JSONObject(data);
                        String username = jsonRootObject.getString("username_room");
                        String content = jsonRootObject.getString("tab_room");
                        String realname = jsonRootObject.getString("nama_display");
                        String icon = jsonRootObject.getString("icon");
                        String backdrop = jsonRootObject.getString("backdrop");
                        String color = jsonRootObject.getString("color");
                        String lastUpdate = jsonRootObject.getString("last_update");
                        String firstTab = jsonRootObject.getString("current_tab");
                        String textColor = jsonRootObject.getString("color_text");
                        String description = jsonRootObject.getString("description");
                        String officer = jsonRootObject.getString("officer");

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
                                Rooms rooms = new Rooms(username, realname, content, jsonCreateType(color, textColor, description, officer, targetUrl), backdrop, lastUpdate, icon, firstTab, time_str);
                                botListDB.insertRooms(rooms);
                            }
                        } else {
                            Rooms rooms = new Rooms(username, realname, content, jsonCreateType(color, textColor, description, officer, targetUrl), backdrop, lastUpdate, icon, firstTab, time_str);
                            botListDB.insertRooms(rooms);
                        }
                        cursor.close();

                        Log.w("gg",jsonCreateType(color, textColor, description, officer, targetUrl));

                        Intent intent = new Intent(getApplicationContext(), ByonChatMainRoomActivity.class);
                        intent.putExtra(ConversationActivity.KEY_JABBER_ID, usr);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(ConversationActivity.KEY_TITLE, targetUrl);
                        getApplicationContext().startActivity(intent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Intent intent = new Intent(getApplicationContext(), ByonChatMainRoomActivity.class);
                        intent.putExtra(ConversationActivity.KEY_JABBER_ID, usr);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(ConversationActivity.KEY_TITLE, targetUrl);
                        getApplicationContext().startActivity(intent);
                        error = "Tolong periksa koneksi internet.";
                        finish();
                    }
                } else {
                    Intent intent = new Intent(getApplicationContext(), ByonChatMainRoomActivity.class);
                    intent.putExtra(ConversationActivity.KEY_JABBER_ID, usr);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(ConversationActivity.KEY_TITLE, targetUrl);
                    getApplicationContext().startActivity(intent);
                    error = "Tolong periksa koneksi internet.";
                    finish();
                }

            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                LoadingGetTabRoomActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Tolong periksa koneksi internet.", Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
            } catch (ClientProtocolException e) {
                LoadingGetTabRoomActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Tolong periksa koneksi internet.", Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
                // TODO Auto-generated catch block
            } catch (IOException e) {
                LoadingGetTabRoomActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Tolong periksa koneksi internet.", Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
                // TODO Auto-generated catch block
            }
        }

    }

    private String jsonCreateType(String idContent, String type, String desc, String of, String ss) {
        JSONObject obj = new JSONObject();
        try {
            Log.w("mausk1", ss);
            obj.put("a", idContent);
            obj.put("b", type);
            obj.put("c", desc);
            obj.put("d", of);
            obj.put("e", ss);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }

}
