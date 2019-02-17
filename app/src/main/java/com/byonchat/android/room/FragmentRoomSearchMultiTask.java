package com.byonchat.android.room;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.ByonChatMainRoomActivity;
import com.byonchat.android.FragmentDinamicRoom.DinamicListTaskAdapter;
import com.byonchat.android.FragmentDinamicRoom.DinamicRoomSearchTaskActivity;
import com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity;
import com.byonchat.android.ISSActivity.Reliever.CheckInActivity;
import com.byonchat.android.ISSActivity.Reliever.CheckOutActivity;
import com.byonchat.android.ISSActivity.Reliever.SubmitRequestActivity;
import com.byonchat.android.ISSActivity.Requester.ByonchatMallKelapaGadingActivity;
import com.byonchat.android.R;
import com.byonchat.android.ZoomImageViewActivity;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.ContentRoom;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.utils.EndlessRecyclerOnScrollListener;
import com.byonchat.android.utils.HttpHelper;
import com.byonchat.android.utils.RequestKeyTask;
import com.byonchat.android.utils.TaskCompleted;
import com.byonchat.android.utils.Utility;
import com.byonchat.android.utils.ValidationsKey;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@SuppressLint("ValidFragment")
public class FragmentRoomSearchMultiTask extends Fragment {


    public static String GETTABDETAILPULLMULTIPLE = "/bc_voucher_client/webservice/category_tab/list_task_pull_multiple.php";
    RecyclerView mRecyclerView;
    DinamicListTaskAdapter myadapter;
    Contact contact;
    private MessengerDatabaseHelper dbhelper;
    private SwipeRefreshLayout swipeRefreshLayout;
    requestTask requestTask;
    ArrayList<ContentRoom> listItem;
    ArrayList<RoomsDetail> listItem2;

    String linkTembak;
    String username;
    String color;
    String latLong;
    String idTab;
    String title;
    String from;
    private Activity mContext;
    BotListDB botListDB;
    LinearLayoutManager llm;
    Runnable runnable;
    Handler handler = new Handler();

    public FragmentRoomSearchMultiTask(Activity ctx) {
        mContext = ctx;

    }

    // newInstance constructor for creating fragment with arguments
    public static FragmentRoomSearchMultiTask newInstance(String tt, String cc, String usr, String idta, String col, String latLong, Activity aa, String from) {
        FragmentRoomSearchMultiTask fragmentRoomTask = new FragmentRoomSearchMultiTask(aa);
        Bundle args = new Bundle();
        args.putString("tt", tt);
        args.putString("cc", cc);
        args.putString("uu", usr);
        args.putString("ii", idta);
        args.putString("co", col);
        args.putString("ll", latLong);
        args.putString("from", from);
        fragmentRoomTask.setArguments(args);
        return fragmentRoomTask;
    }


    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getArguments().getString("tt");
        linkTembak = getArguments().getString("cc");
        username = getArguments().getString("uu");
        idTab = getArguments().getString("ii");
        color = getArguments().getString("col");
        latLong = getArguments().getString("ll");
        from = getArguments().getString("from");
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


        Log.w("saya", "disini : " + title);
        if (dbhelper == null) {
            dbhelper = MessengerDatabaseHelper.getInstance(mContext.getApplicationContext());
        }

        if (botListDB == null) {
            botListDB = BotListDB.getInstance(mContext.getApplicationContext());
        }

        if (contact == null) {
            contact = dbhelper.getMyContact();
        }

        if (listItem == null) {
            listItem = new ArrayList<>();
        }

        mRecyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(llm);

        if (myadapter != null) {
            myadapter = null;
        }

        myadapter = new DinamicListTaskAdapter(listItem, getContext());
        mRecyclerView.setAdapter(myadapter);
        mRecyclerView.setNestedScrollingEnabled(false);

        myadapter.setOnLongClickListener(new DinamicListTaskAdapter.MyClickListenerLongClick() {

            @Override
            public void onLongClick(int position, View v) {
                /*((ByonChatMainRoomActivity) mContext).deleteById(position);*/
                ((ByonChatMainRoomActivity) mContext).deleteById((ContentRoom) myadapter.getData().get(position));
            }
        });


        myadapter.setOnItemClickListener(
                new DinamicListTaskAdapter.MyClickListener() {
                    @Override
                    public void onItemClick(int position, View v) {
                        if (linkTembak.equalsIgnoreCase("https://bb.byonchat.com/ApiReliever/index.php/Request/list")) {
                            if (myadapter.getData().get(position).getStatus().equalsIgnoreCase("Close")) {
                                final AlertDialog.Builder alertbox = new AlertDialog.Builder(getContext());
                                alertbox.setTitle("Search Reliever");
                                alertbox.setMessage("CLose");
                                alertbox.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {

                                    }
                                });
                                alertbox.show();
                            } else {
                                Intent intent4 = new Intent(getContext(), ByonchatMallKelapaGadingActivity.class);
                                intent4.putExtra(Constants.EXTRA_COLOR, "022b95");
                                intent4.putExtra(Constants.EXTRA_COLORTEXT, "ffffff");
                                intent4.putExtra(Constants.EXTRA_ITEM, myadapter.getData().get(position).getIdHex() + "");
                                intent4.putExtra(Constants.EXTRA_ROOM, myadapter.getData().get(position).getMetode() + "");
                                startActivity(intent4);

                            }

                        } else if (linkTembak.equalsIgnoreCase("https://bb.byonchat.com/ApiReliever/index.php/Request/close_list")) {
                            Intent intent4 = new Intent(getContext(), ByonchatMallKelapaGadingActivity.class);
                            intent4.putExtra(Constants.EXTRA_COLOR, "022b95");
                            intent4.putExtra(Constants.EXTRA_COLORTEXT, "ffffff");
                            intent4.putExtra(Constants.EXTRA_ITEM, myadapter.getData().get(position).getIdHex() + "");
                            intent4.putExtra(Constants.EXTRA_ROOM, myadapter.getData().get(position).getMetode() + "");
                            startActivity(intent4);
                        } else {
                            if (myadapter.getData().get(position).getStatus().equalsIgnoreCase("New") ||
                                    myadapter.getData().get(position).getStatus().equalsIgnoreCase("CheckIn") ||
                                    myadapter.getData().get(position).getStatus().equalsIgnoreCase("CheckOut")) {
                                Intent intent = new Intent(getContext(), SubmitRequestActivity.class);
                                intent.putExtra("tt", title);
                                intent.putExtra("uu", username);
                                intent.putExtra("ii", idTab);
                                intent.putExtra("idRequest", myadapter.getData().get(position).getId());
                                intent.putExtra("content", myadapter.getData().get(position).getAttach());
                                intent.putExtra("status", myadapter.getData().get(position).getStatus());
                                intent.putExtra("col", color);
                                intent.putExtra("ll", latLong);
                                startActivity(intent);

                            } else {

                                final AlertDialog.Builder alertbox = new AlertDialog.Builder(getContext());
                                alertbox.setTitle("Job Call");
                                alertbox.setMessage("Waiting " + myadapter.getData().get(position).getStatus() + " Requester " + myadapter.getData().get(position).getMetode());
                                alertbox.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {

                                    }
                                });
                                alertbox.show();


                            }
                        }


                    }
                });


        mRecyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(llm) {
            @Override
            public void onLoadMore(int current_page) {
               /* listItem.add(null);
                myadapter.notifyItemInserted(listItem.size());*/

                // new requestTaskKedua(mRecyclerView.getAdapter().getItemCount()).execute(linkTembak, username, idTab, "6281513630992");
            }
        });

        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(mContext.getApplicationContext(), LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        requestKey();
                    }
                };
                runnable.run();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();

        runnable = () -> {
            requestKey();
            handler.postDelayed(runnable, 40000);
        };
        handler.post(runnable);

    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    private void requestKey() {
        requestTask = new FragmentRoomSearchMultiTask.requestTask(mContext.getApplicationContext());
        requestTask.execute("kosong");
    }

    class requestTask extends AsyncTask<String, Void, String> {

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();

        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content = null;
        private boolean error = false;
        private Context mContext;

        public requestTask(Context context) {
            this.mContext = context;

        }

        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);
        }

        protected String doInBackground(String... key) {
            try {
                HttpClient httpClient = HttpHelper
                        .createHttpClient(mContext);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);

                MessengerDatabaseHelper dbhelper = MessengerDatabaseHelper.getInstance(getActivity());

                nameValuePairs.add(new BasicNameValuePair("bc_user", dbhelper.getMyContact().getJabberId()));

                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                HttpPost post = new HttpPost(linkTembak);
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
            swipeRefreshLayout.setRefreshing(false);
        }

        protected void onPostExecute(String content) {
            Log.w("hasil", content);
            swipeRefreshLayout.setRefreshing(false);
            if (error) {
                if (content != null) {
                    if (content.contains("invalid_key")) {
                        if (NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)) {
                            requestKey();
                        } else {
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            } else {
                listItem.clear();
// TODO: 03/02/19 buat validasi waktu kirim lokasi terkini
                // 1 menit kalau idle, 3 detik kalau sedang confirm
                // mati jika checkin
                // on jika checkout
                if (linkTembak.equalsIgnoreCase("https://bb.byonchat.com/ApiReliever/index.php/Request/list")) {
                    try {
                        JSONObject jsp = new JSONObject(content);
                        JSONArray jsonArray = jsonArray = new JSONArray(jsp.getString("data"));
                        String status = "";
                        if (jsp.getString("status").equalsIgnoreCase("1")) {
                            status = "Open";
                        } else if (jsp.getString("status").equalsIgnoreCase("2")) {
                            status = "Close";
                        } else if (jsp.getString("status").equalsIgnoreCase("3")) {
                            status = "Not Found";
                        }

                        for (int i = 0; i < jsonArray.length(); i++) {

                            String id_request = jsonArray.getJSONObject(i).getString("id_request");
                            String nama_jjt = jsonArray.getJSONObject(i).getString("nama_jjt");
                            String request_status = jsonArray.getJSONObject(i).getString("request_status");
                            String nama_pekerjaan = jsonArray.getJSONObject(i).getString("nama_pekerjaan");
                            String create_at = jsonArray.getJSONObject(i).getString("create_at");
                            String jjt_lat = jsonArray.getJSONObject(i).getString("jjt_lat");
                            String jjt_long = jsonArray.getJSONObject(i).getString("jjt_long");

                            ContentRoom contentRoom = new ContentRoom(id_request, nama_jjt, create_at, nama_pekerjaan, jsonArray.getJSONObject(i).toString(), status, jjt_lat + ":" + jjt_long);

                            listItem.add(contentRoom);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else if (linkTembak.equalsIgnoreCase("https://bb.byonchat.com/ApiReliever/index.php/Request/close_list")) {
                    try {
                        JSONObject jsp = new JSONObject(content);
                        JSONArray jsonArray = jsonArray = new JSONArray(jsp.getString("data"));

                        for (int i = 0; i < jsonArray.length(); i++) {

                            String id_request = jsonArray.getJSONObject(i).getString("id_request");
                            String nama_jjt = jsonArray.getJSONObject(i).getString("nama_jjt");
                            String nama_pekerjaan = jsonArray.getJSONObject(i).getString("nama_pekerjaan");
                            String create_at = jsonArray.getJSONObject(i).getString("create_at");
                            String jjt_lat = jsonArray.getJSONObject(i).getString("jjt_lat");
                            String jjt_long = jsonArray.getJSONObject(i).getString("jjt_long");

                            ContentRoom contentRoom = new ContentRoom(id_request, nama_jjt, create_at, nama_pekerjaan, jsonArray.getJSONObject(i).toString(), "done", jjt_lat + ":" + jjt_long);

                            listItem.add(contentRoom);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    try {
                        JSONArray jsonArray = new JSONArray(content);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String id = jsonArray.getJSONObject(i).getString("id");
                            String kode_jjt = jsonArray.getJSONObject(i).getString("kode_jjt");
                            String nama_jjt = jsonArray.getJSONObject(i).getString("nama_jjt");
                            String status_request = jsonArray.getJSONObject(i).getString("status_request");
                            String pekerjaan = jsonArray.getJSONObject(i).getString("pekerjaan");
                            String sub_pekerjaan = jsonArray.getJSONObject(i).getString("sub_pekerjaan");
                            String waktu_mulai = jsonArray.getJSONObject(i).getString("waktu_mulai");
                            String waktu_selesai = jsonArray.getJSONObject(i).getString("waktu_selesai");
                            String status_request_detail = jsonArray.getJSONObject(i).getString("status_request_detail");
                            String lat_lokasi = jsonArray.getJSONObject(i).getString("lat_lokasi");
                            String long_lokasi = jsonArray.getJSONObject(i).getString("long_lokasi");
                            String requester_hp = jsonArray.getJSONObject(i).getString("requester_hp");
                            String requester_nama = jsonArray.getJSONObject(i).getString("requester_nama");

                            String status = "New";

                            if (status_request_detail.equalsIgnoreCase("1")) {
                                status = "CheckIn";
                            } else if (status_request_detail.equalsIgnoreCase("2")) {
                                status = "Confirm Checkin";
                            } else if (status_request_detail.equalsIgnoreCase("3")) {
                                status = "CheckOut";
                            } else if (status_request_detail.equalsIgnoreCase("4")) {
                                status = "Confirm Check Out";
                            } else if (status_request_detail.equalsIgnoreCase("5")) {
                                status = "Cancel";
                            }

                            ContentRoom contentRoom = new ContentRoom(id, nama_jjt, waktu_mulai, pekerjaan + "," + waktu_mulai + " - " + waktu_selesai, jsonArray.getJSONObject(i).toString(), status, requester_nama);

                            listItem.add(contentRoom);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                Collections.sort(listItem, new FragmentRoomMultipleTask.Sortiran());
                myadapter.notifyDataSetChanged();

                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }


    public void onActionSearch(String args) {
        myadapter.getFilter().filter(args);
    }

}
