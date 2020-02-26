package com.byonchat.android;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.createMeme.FilteringImage;
import com.byonchat.android.list.ItemListSearchTheme;
import com.byonchat.android.list.ListSearchThemesAdapter;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.IntervalDB;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.Skin;
import com.byonchat.android.utils.HttpHelper;
import com.byonchat.android.utils.Validations;
import com.byonchat.android.utils.ValidationsKey;
import com.byonchat.android.widget.LoadMoreListView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class SearchThemesActivity extends AppCompatActivity {
    public final static String REFERENCE_MAIN = "main";
    public static final String URL_SEARCH_ROOMS = "https://"+ MessengerConnectionService.HTTP_SERVER+"/themes/cari.php";
    public final static String KEY_JABBER_ID = "com.byonchat.android.ProfileInfoActivity.JABBER_ID";
    public final static String KEY_REFERENCE = "com.byonchat.android.ProfileInfoActivity.REFERENCE";
    private MessengerDatabaseHelper dbhelper;
    protected ProgressDialog pdialog;
    Contact contact;
    LoadMoreListView listView;
    ListSearchThemesAdapter listSearchThemesAdapter;
    ArrayList<ItemListSearchTheme> catArr;
    ArrayList<Skin> skinArrayList = new ArrayList<Skin>();
    AlertDialog.Builder alertDialogBuilder;
    ItemListSearchTheme itemListSearchTheme;
    String bot;
    TextView txtEmpty;
    EditText inputSearch;
    ImageButton btn_search;
    ImageButton btn_all_search;
    String search = "";
    String suggested  = "";
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_themes);
        overridePendingTransition(R.anim.activity_open_translate,R.anim.activity_close_scale);
        getSupportActionBar().setBackgroundDrawable(new Validations().getInstance(getApplicationContext()).header(getWindow()));
    //    getSupportActionBar().setIcon(new Validations().getInstance(getApplicationContext()).logoCustome());
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        if (dbhelper == null) {
            dbhelper = MessengerDatabaseHelper.getInstance(getApplicationContext());
        }
        contact = dbhelper.getMyContact();
        search =  getIntent().getStringExtra("search");
        suggested =  getIntent().getStringExtra("suggested");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // if(!search.equalsIgnoreCase("")) getSupportActionBar().setTitle(search);

        if (pdialog == null) {
            pdialog = new ProgressDialog(this);
            pdialog.setIndeterminate(true);
            pdialog.setMessage("Loading ...");
            pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }


        txtEmpty = (TextView) findViewById(R.id.txtEmpty);
        listView = (LoadMoreListView)findViewById(R.id.listCatalog);

        listView.setAdapter(listSearchThemesAdapter);

        IntervalDB db = new IntervalDB(getApplicationContext());
        db.open();
        skinArrayList = db.retriveallSkin();
        db.close();

        listSearchThemesAdapter = new ListSearchThemesAdapter(SearchThemesActivity.this,skinArrayList);
        listView.setAdapter(listSearchThemesAdapter);
        listView.setTextFilterEnabled(true);

        btn_search = (ImageButton) findViewById(R.id.btn_search);
        btn_all_search = (ImageButton) findViewById(R.id.btn_all_search);
        inputSearch = (EditText) findViewById(R.id.inputSearch);
        inputSearch.setText(search);
        btn_search.setBackground(new BitmapDrawable(getResources(), FilteringImage.viewAll(getApplicationContext(), Color.parseColor(new Validations().getInstance(getApplicationContext()).colorTheme(false)),R.drawable.ic_search)));
        btn_search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (inputSearch.getText().toString().length() > 0) {
                    finish();
                    Intent intent = new Intent(getApplicationContext(), SearchThemesActivity.class);
                    intent.putExtra("search", inputSearch.getText().toString());
                    intent.putExtra("suggested", "0");
                    startActivity(intent);
                }

            }
        });
        btn_all_search.setBackground(new BitmapDrawable(getResources(), FilteringImage.viewAll(getApplicationContext(), Color.parseColor(new Validations().getInstance(getApplicationContext()).colorTheme(false)),R.drawable.view_all)));
        btn_all_search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                    finish();
                    Intent intent = new Intent(getApplicationContext(), SearchThemesActivity.class);
                    intent.putExtra("search","");
                    intent.putExtra("suggested", "0");
                    startActivity(intent);

            }
        });

        if(NetworkInternetConnectionStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())){
            String key = new ValidationsKey().getInstance(getApplicationContext()).key(false);
            if (key.equalsIgnoreCase("null")){
                Toast.makeText(getApplicationContext(), R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
            }else{
                new searchThemeRequest(getApplicationContext()).execute(key);
            }

        }else{
            Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
        }

        listView.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            public void onLoadMore() {
                String key = new ValidationsKey().getInstance(getApplicationContext()).key(false);
                if (key.equalsIgnoreCase("null")){
                    Toast.makeText(getApplicationContext(),R.string.pleaseTryAgain,Toast.LENGTH_SHORT).show();
                }else{
                    new searchThemeRequest(getApplicationContext()).execute(key);
                }
            }
        });


    }


    @Override
    protected void onPause()
    {
        super.onPause();
        //closing transition animations
        overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
               /* Intent intent;
                intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);*/
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
    /*    getMenuInflater().inflate(R.menu.action_next_menu, menu);
        configureActionItem(menu);*/
        return true;
    }

    private void configureActionItem(Menu menu) {
        MenuItem item = menu.findItem(R.id.menu_action_next);
        Button btn = (Button) MenuItemCompat.getActionView(item).findViewById(
                R.id.buttonAbNext);
        btn.setBackgroundColor(Color.TRANSPARENT);
        btn.setTypeface(null, Typeface.BOLD);
        btn.setText("Search");
        btn.setTextColor(Color.WHITE);
        btn.setCompoundDrawables(null, null, null, null);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SkinSelectorActivity.class);
                startActivity(intent);
            }
        });
    }

    class searchThemeRequest extends AsyncTask<String, Void, String> {

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();

        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private JSONObject jObject;
        private String jsonResult ="";
        JSONArray menuitemArray;
        int position1 = listView.getCount() - 1;
        Boolean max = false;
        private Context mContext;
        private String content = null;
        private boolean error = false;

        public searchThemeRequest(Context context) {
            this.mContext = context;

        }

        @Override
        protected void onPreExecute() {
            if(position1==0){
                pdialog.show();
            }
        }

        InputStreamReader reader = null;

        protected String doInBackground(String... key) {

            try {

                HttpClient httpClient = HttpHelper
                        .createHttpClient(mContext);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);

                nameValuePairs.add(new BasicNameValuePair("username", contact.getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("key", key[0]));
                if(!search.equalsIgnoreCase("")){
                    nameValuePairs.add(new BasicNameValuePair("nama_theme", search));
                }

                if (suggested.equals("1")){
                    nameValuePairs.add(new BasicNameValuePair("suggested", "1"));
                }

                if(position1>0){
                    nameValuePairs.add(new BasicNameValuePair("mulai",String.valueOf(position1)));
                }

                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                HttpPost post = new HttpPost(URL_SEARCH_ROOMS);
                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));


                //Response from the Http Request
                response = httpclient.execute(post);
                StatusLine statusLine = response.getStatusLine();

                //Check the Http Request for success

                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    content = out.toString();
                    jObject = new JSONObject(content);
                    JSONArray menuitemArray = jObject.getJSONArray("themes_list");
                    catArr = new ArrayList<ItemListSearchTheme>();
                    if(menuitemArray.length()==0) max=true;
                    for (int i = 0; i < menuitemArray.length(); i++)
                    {
                        String name = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("nama").toString()));
                        String desc = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("deskripsi").toString()));
                        String logo = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("logo").toString()));
                        String logo2 = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("logo2").toString()));
                        String logoHeader = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("logo_chat").toString()));
                        String color = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("color_code").toString()));
                        String reward = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("reward").toString()));
                        String background = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("background").toString()));
                        itemListSearchTheme = new ItemListSearchTheme(name,desc,logo,logo2,logoHeader,color,background,reward);
                        catArr.add(itemListSearchTheme);
                    }

                } else {
                    //Closes the connection.
                    content = statusLine.getReasonPhrase();
                    response.getEntity().getContent().close();
                    throw new IOException(content);
                }

            } catch (ClientProtocolException e) {
                content =  e.getMessage();
                error = true;
            } catch (IOException e) {
                content = e.getMessage();
                error = true;
            } catch (Exception e) {
                error = true;
            }

            return content;
        }

        protected void onCancelled() {
            pdialog.dismiss();
        }

        protected void onPostExecute(String content) {
            pdialog.dismiss();
            if (error) {
                if(content.contains("invalid_key")){
                    if(NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)){
                        pdialog.show();
                        String key = new ValidationsKey().getInstance(mContext).key(true);
                        if (key.equalsIgnoreCase("null")){
                            Toast.makeText(mContext,R.string.pleaseTryAgain,Toast.LENGTH_SHORT).show();
                            pdialog.dismiss();
                        }else{
                            new searchThemeRequest(mContext).execute(key);
                        }
                    }else{
                        Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    txtEmpty.setVisibility(View.VISIBLE);
                }
            } else {
                listView.onLoadMoreComplete();
                listSearchThemesAdapter.add(catArr);
                listSearchThemesAdapter.notifyDataSetChanged();
                pdialog.dismiss();
                if (listSearchThemesAdapter.getCount()==0){
                    txtEmpty.setVisibility(View.VISIBLE);
                }
            }
        }

    }
}
