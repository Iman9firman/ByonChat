package com.honda.android.FragmentDinamicRoom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import com.honda.android.R;
import com.honda.android.provider.BotListDB;
import com.honda.android.provider.RoomsDetail;

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
import java.util.List;

/**
 * Created by lukma on 3/4/2016.
 */
@SuppressLint("ValidFragment")
public class FragmentRoomAbout extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private WebView description;
    BotListDB botListDB;
    private String myContact ;
    private String title ;
    private String urlTembak ;
    private String username ;
    private String idRoomTab ;
    private String color ;
    private Activity mContext ;

    SwipeRefreshLayout mSwipeRefreshLayout;
    @SuppressLint("ValidFragment")

    public FragmentRoomAbout(Activity ctx) {
        mContext = ctx;

    }

    public static FragmentRoomAbout newInstance(String myc, String tit, String utm, String usr, String idrtab, String color,Activity ctcx) {
        FragmentRoomAbout fragmentRoomAbout = new FragmentRoomAbout(ctcx);
        Bundle args = new Bundle();
        args.putString("aa", tit);
        args.putString("bb", utm);
        args.putString("cc", usr);
        args.putString("dd", idrtab);
        args.putString("ee", myc);
        args.putString("col", color);
        fragmentRoomAbout.setArguments(args);
        return fragmentRoomAbout;
    }

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getArguments().getString("aa");
        urlTembak = getArguments().getString("bb");
        username = getArguments().getString("cc");
        idRoomTab = getArguments().getString("dd");
        myContact = getArguments().getString("ee");
        color = getArguments().getString("col");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("aa", title);
        outState.putString("bb", urlTembak);
        outState.putString("cc", username);
        outState.putString("dd", idRoomTab);
        outState.putString("ee", myContact);
        outState.putString("col", color);
        super.onSaveInstanceState(outState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View sss = inflater.inflate(R.layout.room_fragment_about, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) sss.findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        description = (WebView) sss.findViewById(R.id.webView);
        description.getSettings().setJavaScriptEnabled(true);
        description.setBackgroundColor(0x00000000);

        return sss;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (botListDB == null) {
            botListDB = BotListDB.getInstance(mContext);
        }

        Cursor cursorValue = botListDB.getSingleRoomDetailFormWithFlag("", username, idRoomTab, "value");

        if (cursorValue.getCount() > 0) {
            final String contentValue = cursorValue.getString(cursorValue.getColumnIndexOrThrow(BotListDB.ROOM_CONTENT));
            if(!contentValue.equalsIgnoreCase("")){
                description.loadDataWithBaseURL(null,contentValue, "text/html", "utf-8", null);
            }else {
                new Refresh().execute(urlTembak,username,idRoomTab);
            }
        }else {
            new Refresh().execute(urlTembak,username,idRoomTab);
        }
    }

    @Override
    public void onRefresh() {
        new Refresh().execute(urlTembak,username,idRoomTab);
    }

    private class Refresh extends AsyncTask<String, String, String> {

        String error = "";

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            postData(params[0],params[1],params[2]);
            return null;
        }

        protected void onPostExecute(String result) {
            if (error.length() > 0) {
               // Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
            //dialog.dismiss();
            mSwipeRefreshLayout.setRefreshing(false);
        }

        protected void onProgressUpdate(String... string) {
        }

        public void postData(String valueIWantToSend,String usr,String idr) {
            // Create a new HttpClient and Post Header

            try {
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
                HttpConnectionParams.setSoTimeout(httpParameters, 5000);
                HttpClient httpclient = new DefaultHttpClient(httpParameters);
                HttpPost httppost = new HttpPost(valueIWantToSend);
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("username_room", usr));
                nameValuePairs.add(new BasicNameValuePair("id_rooms_tab", idr));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);
                    try {
                        JSONObject jsonRootObject = new JSONObject(data);
                        String aaa = jsonRootObject.getString("data_about");
                        JSONObject jsonRootObject2 = new JSONObject(aaa);
                        final String l = jsonRootObject2.getString("about_us").toString();
                        final String l2 = jsonRootObject2.getString("add_date").toString();

                        RoomsDetail orderModel = new RoomsDetail("", idRoomTab,  username, l, "", l2, "value");

                        Cursor cursorValue = botListDB.getSingleRoomDetailFormWithFlag("", username, idRoomTab, "value");
                        if (cursorValue.getCount() > 0) {
                            botListDB.updateDetailRoomWithFlagContent(orderModel);
                        }else {
                            botListDB.insertRoomsDetail(orderModel);
                        }


                        mContext.runOnUiThread(new Runnable() {
                            public void run() {
                                description.loadDataWithBaseURL(null,l, "html/css", "utf-8", null);
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        error = "Tolong periksa koneksi internet.";
                    }
                } else {
                    error = "Tolong periksa koneksi internet.";
                }

            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
        }

    }
}
