package com.byonchat.android.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.byonchat.android.R;
import com.byonchat.android.Sample.Adapter.ScheduleAdapter;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.local.Byonchat;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.room.DividerItemDecoration;
import com.byonchat.android.ui.activity.MainByonchatRoomBaseActivity;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@SuppressLint("ValidFragment")
public class ByonchatListScheduleFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener  {

    RecyclerView mRecyclerView;
    ScheduleAdapter myadapter;
    Contact contact;
    Activity mContext;
    private MessengerDatabaseHelper dbhelper;
    private SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<String> dataJson = new ArrayList<>();

    String username;

    BotListDB botListDB;
    LinearLayoutManager llm;

    public ByonchatListScheduleFragment() {
    }

    @SuppressLint("ValidFragment")
    public ByonchatListScheduleFragment(MainByonchatRoomBaseActivity activity) {
        this.mContext = activity;
    }

    // newInstance constructor for creating fragment with arguments
    public static ByonchatListScheduleFragment newInstance(String myc, String tit, String utm, String usr, String idrtab, String color, MainByonchatRoomBaseActivity activity) {
        ByonchatListScheduleFragment fragment = new ByonchatListScheduleFragment(activity);
        Bundle args = new Bundle();
        args.putString("aa", tit);
        args.putString("bb", utm);
        args.putString("cc", usr);
        args.putString("dd", idrtab);
        args.putString("ee", myc);
        args.putString("col", color);
        fragment.setArguments(args);
        return fragment;
    }


    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        username = Byonchat.getMessengerHelper().getMyContact().getJabberId();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View sss = inflater.inflate(R.layout.room_fragment_task_pull, container, false);

        mRecyclerView = (RecyclerView) sss.findViewById(R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout) sss.findViewById(R.id.activity_main_swipe_refresh_layout);

        return sss;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (dbhelper == null) {
            dbhelper = MessengerDatabaseHelper.getInstance(mContext.getApplicationContext());
        }

        if (botListDB == null) {
            botListDB = BotListDB.getInstance(mContext.getApplicationContext());
        }

        if (contact == null) {
            contact = dbhelper.getMyContact();
        }

        mRecyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(mContext.getApplicationContext());
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setNestedScrollingEnabled(false);

        if (myadapter != null) {
            myadapter = null;
        }

        myadapter = new ScheduleAdapter(mContext.getApplicationContext(), dataJson);
        mRecyclerView.setAdapter(myadapter);

        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);

        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onResume() {
        refreshList();
        super.onResume();
    }

    @Override
    public void onRefresh() {
        refreshList();
    }

    public void refreshList() {
        dataJson.clear();
        swipeRefreshLayout.setRefreshing(true);
        if (NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)) {
            String url = "https://bb.byonchat.com/bc_voucher_client/webservice/list_api/iss/schedule/schedule_data.php";
            new startGetData(addParamsToUrl(url)).execute();

        } else {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getContext(), "Please check your internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    protected String addParamsToUrl(String url){
        if(!url.endsWith("?"))
            url += "?";

        List<NameValuePair> params = new LinkedList<NameValuePair>();

        Log.e("Reamure",username);
        params.add(new BasicNameValuePair("action", "getPeriodeByBcUser"));
        params.add(new BasicNameValuePair("bc_user", username));

        String paramString = URLEncodedUtils.format(params, "utf-8");

        url += paramString;
        return url;
    }

    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
            inputStream = httpResponse.getEntity().getContent();
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "";
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    private class startGetData extends AsyncTask<String, Void, String> {
        private String vug;

        private startGetData(String text) {
            this.vug = text;
        }

        @Override
        protected String doInBackground(String... urls) {
            return GET(vug);
        }

        @Override
        protected void onPostExecute(String result) {
            swipeRefreshLayout.setRefreshing(false);
            setData(result);
        }
    }

    public void setData(String response) {
        try {
            if(response.startsWith("<pre>")){
                response = response.replace("<pre>","");
            }

            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("item");
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                dataJson.add(jsonObject1.toString());
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        myadapter.notifyDataSetChanged();
    }
}