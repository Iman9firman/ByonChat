package com.byonchat.android;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.communication.NotificationReceiver;
import com.byonchat.android.list.ItemListMemberCard;
import com.byonchat.android.list.ListMemberCard;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.MembersDB;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.utils.HttpHelper;
import com.byonchat.android.utils.RequestKeyTask;
import com.byonchat.android.utils.TaskCompleted;
import com.byonchat.android.utils.ValidationsKey;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Iman Firmansyah on 1/5/2015.
 */
@SuppressLint("ValidFragment")
public class MyMembersFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public final static String URL_GET_MEMBERS = "https://"+ MessengerConnectionService.HTTP_SERVER+"/memberships/daftar_kartu.php";
    View rootView;
    private LayoutInflater inflater;
    ListMemberCard adapter;
    private MessengerDatabaseHelper messengerHelper;
    private ListView lv;
    ArrayList<ItemListMemberCard> listItem ;
    ProgressBar progressBar;
    Contact contact;
    Context mContext;
    private MessengerDatabaseHelper dbhelper;
    @SuppressLint("ValidFragment")
    public  MyMembersFragment(Context ctx){
        mContext = ctx;

    }
    ItemListMemberCard itemListMemberCard;
    MembersDB membersDB;
    private BroadcastHandler broadcastHandler = new BroadcastHandler();
    private SwipeRefreshLayout swipeRefreshLayout;

    RequestMemberCard requestMemberCard;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.my_members_main, container, false);
        lv = (ListView) rootView.findViewById(R.id.list);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        requestMemberCard = new RequestMemberCard(mContext);

        if (dbhelper == null) {
            dbhelper = MessengerDatabaseHelper.getInstance(mContext);
        }
        if (membersDB == null) {
            membersDB = new MembersDB(mContext);
        }
        contact = dbhelper.getMyContact();

        membersDB.open();
        listItem = membersDB.retriveallMembers();
        membersDB.close();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, final long id) {
                String idList = listItem.get(position).getId_card();
                if (!idList.equalsIgnoreCase("")) {
                    Intent intent = new Intent(mContext, MemberDetailActivity.class);
                    intent.putExtra(MemberDetailActivity.KEY_MEMBERS_ID, idList);
                    intent.putExtra(MemberDetailActivity.KEY_MEMBERS_NAME, listItem.get(position).getName());
                    intent.putExtra(MemberDetailActivity.KEY_MEMBERS_COLOR, "#" + listItem.get(position).getColor_code());
                    startActivity(intent);
                }
            }
        });


        if (listItem.size()==0){
            /*Thread splashTread = new Thread() {
                @Override
                public void run() {
                    try {
                        synchronized (this) {
                            wait(200);
                        }
                    } catch (InterruptedException e) {
                    } finally {

                    }
                }
            };

            splashTread.start();*/
           /*material
            String key = new ValidationsKey().getInstance(mContext).key(false);
            if (key.equalsIgnoreCase("null")){
                Toast.makeText(mContext, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
            }else{
                requestMemberCard.execute(key);
            }*/

        }else{
            refreshList();
        }
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onResume() {
        refreshList();
        IntentFilter f = new IntentFilter(MessengerConnectionService.ACTION_ADD_CARD);
        f.setPriority(1);
        mContext.registerReceiver(broadcastHandler, f);
        ((NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE))
                .cancel(NotificationReceiver.NOTIFY_ID_CARD);
        super.onResume();
    }

    @Override
    public void onPause() {
        mContext.unregisterReceiver(broadcastHandler);
        requestMemberCard.cancel(true);
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_members, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_refresh:
             requestKey();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void requestKey() {
        RequestKeyTask testAsyncTask = new RequestKeyTask(new TaskCompleted() {
            @Override
            public void onTaskDone(String key) {
                if (key.equalsIgnoreCase("null")){
                    swipeRefreshLayout.setRefreshing(false);
                   // Toast.makeText(mContext, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                }else{
                    requestMemberCard = new RequestMemberCard(mContext);
                    requestMemberCard.execute(key);
                }
            }
        },mContext);

        testAsyncTask.execute();
    }
    public void refreshList(){
        adapter = new ListMemberCard(mContext);
        membersDB.open();
        listItem = membersDB.retriveallMembers();
        membersDB.close();
        int count  = 5 - listItem.size();
        if (listItem.size() <= 5){
            for (int i = 0; i < count; i++) {
                ItemListMemberCard itemListMemberCard = new ItemListMemberCard("","","");
                listItem.add(itemListMemberCard);
            }
        }
        adapter.add(listItem);
        adapter.notifyDataSetChanged();
        lv.setAdapter(adapter);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        requestKey();
                        /*String key = new ValidationsKey().getInstance(mContext).key(false);
                        if (key.equalsIgnoreCase("null")){
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(mContext, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                        }else{
                            requestMemberCard = new RequestMemberCard(mContext);
                            requestMemberCard.execute(key);
                        }*/
                    }
                }
        );
    }

    class RequestMemberCard extends AsyncTask<String, Void, String> {

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();

        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content = null;
        private boolean error = false;
        private Context mContext;

        public RequestMemberCard(Context context) {
            this.mContext = context;

        }


        @Override
        protected void onPreExecute() {
           // ((ProgressBar)rootView.findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(true);
        }

        protected String doInBackground(String... key) {
            try {

                HttpClient httpClient = HttpHelper
                        .createHttpClient(mContext);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);

                nameValuePairs.add(new BasicNameValuePair("username", contact.getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("key",key[0]));

                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                HttpPost post = new HttpPost(URL_GET_MEMBERS);
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
                    JSONObject result = new JSONObject(content);
                    JSONArray menuitemArray = result.getJSONArray("items");

                    membersDB.open();
                    membersDB.delete();
                    for (int i = 0; i < menuitemArray.length(); i++)
                    {
                        String id = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("id").toString()));
                        String name = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("nama").toString()));
                        String color = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("warna").toString()));
                        itemListMemberCard = new ItemListMemberCard(id,name,color);
                        membersDB.insertMembers(itemListMemberCard);
                        if (isCancelled()) break;
                    }
                    membersDB.close();
                } else {
                    //Closes the connection.
                    error = true;
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
          //  ((ProgressBar)rootView.findViewById(R.id.progressBar)).setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }

        protected void onPostExecute(String content) {
           /* progressBar.setVisibility(View.GONE);*/
            if (error) {
                if(content.contains("invalid_key")){
                    if(NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)){
                        String key = new ValidationsKey().getInstance(mContext).key(true);
                        if (key.equalsIgnoreCase("null")){
                            swipeRefreshLayout.setRefreshing(false);
                         //   ((ProgressBar)rootView.findViewById(R.id.progressBar)).setVisibility(View.GONE);
                           // Toast.makeText(mContext, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                        }else{
                            requestMemberCard = new RequestMemberCard(mContext);
                            requestMemberCard.execute(key);
                        }
                    }else{
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    swipeRefreshLayout.setRefreshing(false);
                  //  Toast.makeText(mContext, R.string.pleaseTryAgain,Toast.LENGTH_SHORT).show();
                }
            } else {
                swipeRefreshLayout.setRefreshing(false);
              //  ((ProgressBar)rootView.findViewById(R.id.progressBar)).setVisibility(View.GONE);
                refreshList();

            }
        }
    }

    class BroadcastHandler extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MessengerConnectionService.ACTION_ADD_CARD
                    .equals(intent.getAction())) {
                refreshList();
            }
        }
    }

}
