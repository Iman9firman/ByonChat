package com.byonchat.android.FragmentDinamicRoom;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.core.view.MenuItemCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.personalRoom.asynctask.ProfileSaveDescription;
import com.byonchat.android.provider.BotListDB;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RoomPOSdetail extends AppCompatActivity  implements SwipeRefreshLayout.OnRefreshListener  {

    private ListView listView;
    List<ItemRoomDetail> mItems;
    private SwipeRefreshLayout swipeRefreshLayout;

    String username;
    String title;
    String idTab;
    String color;
    String urlTembak;

    BotListDB db ;
    ArrayList<RoomsDetail> udin ;
    RoomDetailAdapter adapter;

    private UserLoginTask mAuthTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_posdetail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = getIntent().getStringExtra("uu");
        idTab = getIntent().getStringExtra("ii");
        title = getIntent().getStringExtra("tt");
        color = getIntent().getStringExtra("col");
        urlTembak = getIntent().getStringExtra("urlTembak");

        ColorDrawable cd = new ColorDrawable(Color.parseColor("#"+color));
        getSupportActionBar().setBackgroundDrawable(cd);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.parseColor("#"+color));
        }
        new getLink().execute(urlTembak,idTab);

    }

    class getLink extends AsyncTask<String, Void, String> {
        ProgressDialog loading;
        ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
        String result = "";
        InputStream inputStream = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("id_rooms_tab", params[1]);
            String result = profileSaveDescription.sendPostRequest(params[0], data);
            return result;
        }

        protected void onPostExecute(String s) {
            if (s.equals(null)) {
            } else {
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rooms_pos_detail, menu);
        configureActionItem(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        if ( id== android.R.id.home){
            finish();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void configureActionItem(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_next);
        Button btn = (Button) MenuItemCompat.getActionView(item).findViewById(
                R.id.buttonAbNext);
        btn.setBackgroundColor(Color.TRANSPARENT);
        btn.setTypeface(null, Typeface.BOLD);
        btn.setText("NEXT");
        btn.setTextColor(Color.WHITE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryText) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                RoomPOSdetail.this.adapter.getFilter().filter(newText);
                return true;
            }
        });
    }



    @Override
    public void onRefresh() {
        //syc menu
        attemptLogin();

    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
        //    showProgress(true);
            mAuthTask = new UserLoginTask("251");
            mAuthTask.execute("https://"+ MessengerConnectionService.HTTP_SERVER+"/bc_voucher_client/webservice/byonpos/api_menu.php");
    }


    public class UserLoginTask extends AsyncTask<String, String, String> {
        String error = "";
        private final String roomId;

        UserLoginTask(String id) {
            roomId = id;
        }

        @Override
        protected String doInBackground(String... params) {
            postData(params[0]);
            return null;
        }
        protected void onPostExecute(String result) {
            mAuthTask = null;
            swipeRefreshLayout.setRefreshing(false);
            adapter.notifyDataSetChanged();
            if (error.length() > 0) {
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            swipeRefreshLayout.setRefreshing(false);
        }

        public void postData(String valueIWantToSend) {
            // Create a new HttpClient and Post Header

            try {
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
                HttpConnectionParams.setSoTimeout(httpParameters, 5000);
                HttpClient httpclient = new DefaultHttpClient(httpParameters);
                HttpPost httppost = new HttpPost(valueIWantToSend);

                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("id_rooms_tab", roomId));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);

                    try {
                        JSONObject result = new JSONObject(data);
                        JSONArray menuitemArray = result.getJSONArray("menu");
                        mItems.clear();
                        db.deleteRoomsDetailAllItemSku("11MENU");
                        for (int i = 0; i < menuitemArray.length(); i++) {
                            String id = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("sku_code").toString()));
                            String namaMenu = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("nama_menu").toString()));
                            String deskripsi = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("deskripsi").toString()));
                            String harga = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("harga").toString()));

                            ItemRoomDetail aa = new ItemRoomDetail(namaMenu,harga,0,id);

                            RoomsDetail orderModel = new RoomsDetail(String.valueOf(i+12), "","11MENU",jsonQ(String.valueOf(i+2),namaMenu,"0", harga,String.valueOf(id)),"","","menu");
                            db.insertRoomsDetail(orderModel);

                            mItems.add(aa);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        error = "Tolong periksa koneksi internet.";
                    }
                } else {
                    error = "Tolong periksa koneksi internet.";
                }

            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                RoomPOSdetail.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Maaf gagal login, karena ada masalah pada data koneksi.  Terimakasih", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (ClientProtocolException e) {
                RoomPOSdetail.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Maaf gagal login, karena ada masalah pada data koneksi.  Terimakasih", Toast.LENGTH_LONG).show();
                    }
                });
                // TODO Auto-generated catch block
            } catch (IOException e) {
                RoomPOSdetail.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Maaf gagal login, karena ada masalah pada data koneksi.  Terimakasih", Toast.LENGTH_LONG).show();
                    }
                });
                // TODO Auto-generated catch block
            }
        }

    }

    public String jsonQ(String id,String name,String qty,String price,String sku) {

        JSONObject obj = new JSONObject();
        try {
            obj.put("id",id);
            obj.put("name",name);
            obj.put("qty",qty);
            obj.put("price",price);
            obj.put("sku",sku);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }

}
