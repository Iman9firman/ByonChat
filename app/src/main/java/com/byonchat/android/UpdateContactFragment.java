package com.byonchat.android;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.byonchat.android.adapter.UpdateContactAdapter;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.TimeLine;
import com.byonchat.android.provider.TimeLineDB;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@SuppressLint("ValidFragment")
public class UpdateContactFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<TimeLine> timeLines = new ArrayList<TimeLine>();
    private BroadcastHandler broadcastHandler = new BroadcastHandler();
    RequestUpdates requestUpdates;
    private static String URL_UPDATES = "https://" + MessengerConnectionService.F_SERVER + "/get_update.php";
    private MessengerDatabaseHelper dbHelper;
    ArrayList<Contact> contact;
    String listJabberID = "";
    String name;
    TimeLineDB timeLineDB;
    private Context context; //this is the Context you will use

    public UpdateContactFragment(Context ctx) {
        context=ctx;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.fragment_update, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        if (dbHelper == null) {
            dbHelper = MessengerDatabaseHelper.getInstance(context);
        }

        if(timeLineDB == null) {
            timeLineDB = TimeLineDB.getInstance(context);
        }


        contact = dbHelper.getContactCount();
        String[] test = dbHelper.getAllJabberId();
        for (int i = 0; i < contact.size();i++){
            if(i==0){
                listJabberID = test[i];
            }else{
                listJabberID = listJabberID+":"+test[i];
            }
        }

        requestKey();

        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_refresh:
                swipeRefreshLayout.setRefreshing(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        requestKey();
                    }
                }
        );
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
        IntentFilter f = new IntentFilter(
                MessengerConnectionService.ACTION_STATUS_CHANGED);
        f.setPriority(1);
        getContext().registerReceiver(broadcastHandler, f);

    }

    private void requestKey() {
        new Handler(Looper.getMainLooper()).post(new Runnable() { // Tried new Handler(Looper.myLopper()) also
            @Override
            public void run() {
                RequestKeyTask testAsyncTask = new RequestKeyTask(new TaskCompleted() {
                    @Override
                    public void onTaskDone(String key) {
                        if (key.equalsIgnoreCase("null")) {
                            //Toast.makeText(getContext(), R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                        } else {
                            requestUpdates = new RequestUpdates(context);
                            requestUpdates.execute();
                        }
                    }
                }, context);

                testAsyncTask.execute();
            }
        });
    }

    @Override
    public void onPause() {
        context.unregisterReceiver(broadcastHandler);
        super.onPause();
    }

    public void refreshList() {
        new Handler(Looper.getMainLooper()).post(new Runnable() { // Tried new Handler(Looper.myLopper()) also
            @Override
            public void run() {
                timeLines.clear();
                timeLines = timeLineDB.retrive();
                mAdapter = new UpdateContactAdapter(context, timeLines);
                mRecyclerView.setAdapter(mAdapter);
            }
        });
    }

    class BroadcastHandler extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MessengerConnectionService.ACTION_STATUS_CHANGED
                    .equals(intent.getAction())) {
                Cursor c = timeLineDB.getDataByFlag();
                if (c.getCount() > 0) {
                    refreshList();
                }
            }
        }
    }

    class RequestUpdates extends AsyncTask<String, Void, String> {

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();

        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content = null;
        private boolean error = false;
        private Context mContext;

        public RequestUpdates(Context context) {
            this.mContext = context;

        }

        @Override
        protected void onPreExecute() {
        }

        protected String doInBackground(String... key) {
            try {
                HttpClient httpClient = HttpHelper
                        .createHttpClient(mContext);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);

                nameValuePairs.add(new BasicNameValuePair("owner", listJabberID));

                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                HttpPost post = new HttpPost(URL_UPDATES);
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
//                    Log.w("OWNER", content);
                    JSONArray result = new JSONArray(content);

                    timeLineDB.delete();
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject obj = result.getJSONObject(i);
                        String username = obj.getString("username");

                        String status =  Html.fromHtml(URLDecoder.decode(String.valueOf(Html.fromHtml(obj.getString("status").toString())))).toString();
                        String foto = obj.getString("foto");
                        String last_update = obj.getString("last_update");
                        String action = obj.getString("action");

                        Contact c = dbHelper.getContact(username);
                        if(username.equalsIgnoreCase(dbHelper.getMyContact().getJabberId())){
                            if(c.getRealname()==null || c.getRealname().equalsIgnoreCase("")){
                                name = username;
                            }else{
                                name = c.getRealname();
                            }
                        }else{
                            if(c.getName() == null || c.getName().equalsIgnoreCase("")){
                                name = username;
                            }else{
                                name = c.getName();
                            }
                        }

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        Date convertedDate = new Date();
                        try{
                            convertedDate = dateFormat.parse(last_update);

                            TimeLine timeLine = new TimeLine(username, toJson(action,status), convertedDate, name, "0");
                            timeLineDB.insert(timeLine);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        if (isCancelled()) break;
                    }
                } else {
                    error = true;
                    content = statusLine.getReasonPhrase();
                    response.getEntity().getContent().close();
                    throw new IOException(content);
                }

            } catch (ClientProtocolException e) {
                content = e.getMessage();
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
            /*swipeRefreshLayout.setRefreshing(false);*/
        }

        protected void onPostExecute(String content) {
            if (error) {
                if (content.contains("invalid_key")) {
                    if (NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)) {
                        String key = new ValidationsKey().getInstance(mContext).key(true);
                        if (key.equalsIgnoreCase("null")) {
                            swipeRefreshLayout.setRefreshing(false);
                           // Toast.makeText(mContext, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                        } else {
                            requestUpdates = new RequestUpdates(mContext);
                            requestUpdates.execute(key);
                        }
                    } else {
                        swipeRefreshLayout.setRefreshing(false);
//                        Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    //Toast.makeText(mContext, R.string.pleaseTryAgain, Toast.LENGTH_LONG).show();
                }
            } else {
//                Toast.makeText(NewSearchRoomActivity.this, R.string.pleaseTryAgain,Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
                refreshList();
            }
            refreshList();
        }
    }

    public static String toJson(String action, String status){
        try {
            JSONObject parent = new JSONObject();
            parent.put("action", action);
            parent.put("status", status);

            return parent.toString();
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }
}
