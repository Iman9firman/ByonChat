package com.byonchat.android.room;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
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

import com.byonchat.android.ByonChatMainRoomActivity;
import com.byonchat.android.FragmentDinamicRoom.DinamicListTaskAdapter;
import com.byonchat.android.R;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
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
public class FragmentRoomMultipleTask extends Fragment {

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

    public FragmentRoomMultipleTask(Activity ctx) {
        mContext = ctx;

    }

    // newInstance constructor for creating fragment with arguments
    public static FragmentRoomMultipleTask newInstance(String tt, String cc, String usr, String idta, String col, String latLong, Activity aa, String from) {
        FragmentRoomMultipleTask fragmentRoomTask = new FragmentRoomMultipleTask(aa);
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
                        /*((ByonChatMainRoomActivity) mContext).idLoof(position);*/
                        ((ByonChatMainRoomActivity) mContext).idLoof((ContentRoom) myadapter.getData().get(position));

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
        refreshList();
        requestKey();
    }

    private String JsonToStringKey(String title) {
        if (Message.isJSONValid(title)) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(title);
                Iterator<String> keys = jsonObject.keys();
                title = jsonObject.get(keys.next()).toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return title;
    }

    public String abs(String ctn, String type) {
        String content = ctn;
        if (type != null) {
            if (type.equalsIgnoreCase("rear_camera") || type.equalsIgnoreCase("front_camera")) {
                Random random = new SecureRandom();
                char[] result = new char[6];
                char[] CHARSET_AZ_09 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
                for (int i = 0; i < result.length; i++) {
                    int randomCharIndex = random.nextInt(CHARSET_AZ_09.length);
                    result[i] = CHARSET_AZ_09[randomCharIndex];
                }
                content = "IMG_" + new String(result);
            } else if (type.equalsIgnoreCase("map")) {
                String[] latlong = content.split(
                        Message.LOCATION_DELIMITER);
                if (latlong.length > 4) {
                    String text = "<u><b>" + (String) latlong[2] + "</b></u><br/>";
                    content = text + latlong[3];
                }
            } else if (type.equalsIgnoreCase("form_child")) {
                content = "";
            } else if (type.equalsIgnoreCase("dropdown_form")) {
                content = "";
            } else if (type.equalsIgnoreCase("input_kodepos")) {
                content = jsonResultType(content, "a");
            } else if (type.equalsIgnoreCase("dropdown_wilayah")) {
                content = jsonResultType(content, "b") + " , " + jsonResultType(content, "c") + " , " + jsonResultType(content, "d") + " , " + jsonResultType(content, "e") + " , " + jsonResultType(content, "a");
            } else if (type.equalsIgnoreCase("checkbox")) {
                if (!content.startsWith("[")) {
                    content = "[" + content + "]";
                }
                JSONArray jsA = null;
                try {
                    jsA = new JSONArray(content);
                    if (jsA.length() > 0) {
                        content = jsA.getJSONObject(0).getString("c").toString();
                    }
                } catch (JSONException e) {
                    content = "";
                    e.printStackTrace();
                }
            } else if (type.equalsIgnoreCase("image_load")) {
                content = "image load";
            } else if (type.equalsIgnoreCase("ocr")) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(content);
                    Iterator<String> keys = jsonObject.keys();
                    String aa = jsonObject.get(keys.next()).toString();
                    content = aa;
                } catch (JSONException e) {
                    e.printStackTrace();
                    content = "ocr";
                }
            } else if (type.equalsIgnoreCase("dropdown_dinamis") || type.equalsIgnoreCase("new_dropdown_dinamis") || type.equalsIgnoreCase("master_data")) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(content);

                    if (content.contains("Name")) {
                        content = jsonObject.getString("Name");
                    } else if (content.contains("Nama")) {
                        content = jsonObject.getString("Nama");
                    } else {
                        Iterator<String> keys = jsonObject.keys();
                        final String ke = keys.next();
                        if (ke.contains("ID")) {
                            String bodre = content.replace("{", "").replace("\"", "");
                            String bodre2 = bodre.substring(0, bodre.indexOf(":"));
                            if (!bodre2.contains("ID")) {
                                content = jsonObject.getString(bodre2);
                            } else {
                                content = jsonObject.get(keys.next()).toString();
                            }
                        } else {
                            content = jsonObject.get(keys.next()).toString();
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    content = "";
                }
            } else if (type.equalsIgnoreCase("upload_document")) {
                content = jsonResultType(content, "a");
            } else if (type.equalsIgnoreCase("signature")) {
                content = "signature";
            } else if (type.equalsIgnoreCase("distance_estimation")) {
                content = jsonResultType(content, "d");
            } else if (type.equalsIgnoreCase("rate")) {

            } else if (type.equalsIgnoreCase("form_isian")) {
                content = "";
            } else if (type.equalsIgnoreCase("remaining_budget")) {
                content = "";
            } else if (type.equalsIgnoreCase("null")) {
                content = "--";
            }
        } else {
            content = "-";
        }

        return content;
    }


    private void requestKey() {
        RequestKeyTask testAsyncTask = new RequestKeyTask(new TaskCompleted() {
            @Override
            public void onTaskDone(String key) {
                if (key.equalsIgnoreCase("null")) {
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    requestTask = new requestTask(mContext.getApplicationContext());
                    requestTask.execute(key);
                }
            }
        }, getActivity());

        testAsyncTask.execute();
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

                nameValuePairs.add(new BasicNameValuePair("username_room", username));
                nameValuePairs.add(new BasicNameValuePair("id_rooms_tab", idTab));
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
                    JSONObject result = new JSONObject(content.toString());

                    JSONArray menuitemArray = result.getJSONArray("list_pull");
                    ArrayList<String> parent = new ArrayList<String>();
                    ArrayList<String> cont = new ArrayList<String>();

                    for (int i = 0; i < menuitemArray.length(); i++) {
                        if (i % 2 == 0) {
                            parent.add(menuitemArray.getJSONObject(i).toString());
                        } else {
                            cont.add(menuitemArray.getJSONObject(i).toString());
                        }
                    }

                    List<RoomsDetail> cuc = botListDB.allRoomDetailFormWithFlag("", username, idTab, "parent");

                    if (cont.size() == parent.size()) {
                        for (int i = 0; i < parent.size(); i++) {
                            JSONObject oParent = new JSONObject(parent.get(i));
                            JSONObject oContent = new JSONObject(cont.get(i));
                            JSONArray joContent = oContent.getJSONArray("value_detail");
                            String id = oParent.getString("id");
                            String parent_id = oParent.getString("parent_id");
                            String date = oParent.getString("add_date");
                            String bc_user = oParent.getString("bc_user");
                            String is_reject = "";
                            String report_status = "4";
                            if (oParent.has("is_reject")) {
                                is_reject = oParent.getString("is_reject");
                            }
                            if (oParent.has("report_status")) {
                                report_status = oParent.getString("report_status");
                            }


                            Cursor cursorParent = botListDB.getSingleRoomDetailFormWithFlag(id + "|" + parent_id, username, idTab, "parent");

                            if (cursorParent.getCount() == 0) {
                                RoomsDetail orderModel = new RoomsDetail(id + "|" + parent_id, idTab, username, date, report_status, "", "parent");
                                botListDB.insertRoomsDetail(orderModel);
                                if (joContent.length() == 1) {
                                    RoomsDetail orderModelTitle211 = new RoomsDetail(id + "|" + parent_id, idTab, username, joContent.getJSONObject(0).getString("value").toString(), "1", joContent.getJSONObject(0).getString("type").toString(), "list");
                                    RoomsDetail orderModelTitle2 = new RoomsDetail(id + "|" + parent_id, idTab, username, jsonDuaObject(va(orderModelTitle211), is_reject), "1", joContent.getJSONObject(0).getString("type").toString(), "list");
                                    botListDB.insertRoomsDetail(orderModelTitle2);
                                } else if (joContent.length() > 1) {

                                    RoomsDetail orderModelTitle21a = new RoomsDetail(id + "|" + parent_id, idTab, username, joContent.getJSONObject(0).getString("value").toString(), "1", joContent.getJSONObject(0).getString("type").toString(), "list");
                                    RoomsDetail orderModelTitle21 = new RoomsDetail(id + "|" + parent_id, idTab, username, jsonDuaObject(va(orderModelTitle21a), is_reject), "1", joContent.getJSONObject(0).getString("type").toString(), "list");
                                    botListDB.insertRoomsDetail(orderModelTitle21);

                                    RoomsDetail orderModelTitle2a = new RoomsDetail(id + "|" + parent_id, idTab, username, joContent.getJSONObject(1).getString("value").toString(), "2", joContent.getJSONObject(1).getString("type").toString(), "list");
                                    RoomsDetail orderModelTitle2 = new RoomsDetail(id + "|" + parent_id, idTab, username, jsonDuaObject(va(orderModelTitle2a), is_reject), "2", joContent.getJSONObject(1).getString("type").toString(), "list");
                                    botListDB.insertRoomsDetail(orderModelTitle2);
                                }
                            }
                        }

                        for (RoomsDetail roomsDetail : cuc) {
                            boolean deelte = true;
                            for (int i = 0; i < parent.size(); i++) {
                                JSONObject oParent = new JSONObject(parent.get(i));
                                JSONObject oContent = new JSONObject(cont.get(i));
                                JSONArray joContent = oContent.getJSONArray("value_detail");
                                String id = oParent.getString("id");
                                String parent_id = oParent.getString("parent_id");
                                String date = oParent.getString("add_date");
                                String bc_user = oParent.getString("bc_user");
                                if (roomsDetail.getId() != null || !roomsDetail.getId().equalsIgnoreCase("")) {
                                    String[] ff = roomsDetail.getId().split("\\|");
                                    if (ff.length == 2) {
                                        if (roomsDetail.getId().equalsIgnoreCase(id + "|" + parent_id)) {
                                            deelte = false;
                                        }
                                    } else {
                                        deelte = false;
                                    }
                                }
                            }
                            if (deelte) {
                                String[] ff = roomsDetail.getId().split("\\|");
                                if (ff.length == 2) {
                                    botListDB.deleteDetailRoomWithFlagContent(roomsDetail);
                                }
                            }
                        }
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
            swipeRefreshLayout.setRefreshing(false);
        }

        protected void onPostExecute(String content) {
            swipeRefreshLayout.setRefreshing(false);
            if (error) {
                if (content != null) {
                    if (content.contains("invalid_key")) {
                        if (NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)) {
                            requestKey();
                        } else {
                            refreshList();
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        refreshList();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            } else {
                swipeRefreshLayout.setRefreshing(false);
                refreshList();
            }
        }
    }

    public String jsonResultType(String json, String type) {
        String hasil = "";
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
            hasil = "";
        }
        if (jObject != null) {
            try {
                hasil = jObject.getString(type);
            } catch (JSONException e) {
                e.printStackTrace();
                hasil = "";
            }
        }

        return hasil;
    }

    public String va(RoomsDetail roomsDetail) {
        String content = roomsDetail.getContent();
        if (roomsDetail.getFlag_tab().equalsIgnoreCase("rear_camera") || roomsDetail.getFlag_tab().equalsIgnoreCase("front_camera")) {
            Random random = new SecureRandom();
            char[] result = new char[6];
            char[] CHARSET_AZ_09 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
            for (int i = 0; i < result.length; i++) {
                int randomCharIndex = random.nextInt(CHARSET_AZ_09.length);
                result[i] = CHARSET_AZ_09[randomCharIndex];
            }
            content = "IMG_" + new String(result);
        } else if (roomsDetail.getFlag_tab().equalsIgnoreCase("map")) {
            String[] latlong = content.split(
                    Message.LOCATION_DELIMITER);
            if (latlong.length > 4) {
                String text = "<u><b>" + (String) latlong[2] + "</b></u><br/>";
                content = text + latlong[3];
            }
        } else if (roomsDetail.getFlag_tab().equalsIgnoreCase("input_kodepos")) {
            content = jsonResultType(content, "a");
        } else if (roomsDetail.getFlag_tab().equalsIgnoreCase("dropdown_wilayah")) {
            content = jsonResultType(content, "b") + " , " + jsonResultType(content, "c") + " , " + jsonResultType(content, "d") + " , " + jsonResultType(content, "e") + " , " + jsonResultType(content, "a");
        } else if (roomsDetail.getFlag_tab().equalsIgnoreCase("checkbox")) {
            if (!content.startsWith("[")) {
                content = "[" + content + "]";
            }
            JSONArray jsA = null;
            try {
                jsA = new JSONArray(content);
                if (jsA.length() > 0) {
                    content = jsA.getJSONObject(0).getString("c").toString();
                }
            } catch (JSONException e) {
                content = "";
                e.printStackTrace();
            }
        } else if (roomsDetail.getFlag_tab().equalsIgnoreCase("image_load")) {
            content = "image load";
        } else if (roomsDetail.getFlag_tab().equalsIgnoreCase("ocr")) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(content);
                Iterator<String> keys = jsonObject.keys();
                String aa = jsonObject.get(keys.next()).toString();
                content = aa;
            } catch (JSONException e) {
                e.printStackTrace();
                content = "ocr";
            }
        } else if (roomsDetail.getFlag_tab().equalsIgnoreCase("dropdown_dinamis") || jsonResultType(roomsDetail.getFlag_content(), "b").equalsIgnoreCase("new_dropdown_dinamis")) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(content);
                Iterator<String> keys = jsonObject.keys();
                String aa = jsonObject.get(keys.next()).toString();
                content = aa;
            } catch (JSONException e) {
                e.printStackTrace();
                content = "";
            }
        } else if (roomsDetail.getFlag_tab().equalsIgnoreCase("upload_document")) {
            content = jsonResultType(content, "a");
        } else if (roomsDetail.getFlag_tab().equalsIgnoreCase("signature")) {
            content = "signature";
        } else if (roomsDetail.getFlag_tab().equalsIgnoreCase("distance_estimation")) {
            content = jsonResultType(content, "d");
        } else if (roomsDetail.getFlag_tab().equalsIgnoreCase("rate")) {

        } else if (roomsDetail.getFlag_tab().equalsIgnoreCase("dropdown_form")) {
            content = "";
        } else if (roomsDetail.getFlag_tab().equalsIgnoreCase("remaining_budget")) {
            content = "";
        }


        return content;
    }

    public void refreshList() {
        if (listItem != null) {
            if (listItem.size() > 0) {
                listItem.clear();
            }
        }


        listItem2 = botListDB.allRoomDetailFormWithFlag("", username, idTab, "parent");
//coba disini hampir bisa
        int b = 0;

        for (RoomsDetail aa : listItem2) {

            b++;
            ArrayList<RoomsDetail> listItem3 = botListDB.allRoomDetailFormWithFlag(aa.getId(), username, idTab, "list");
            String title = "";
            String desc = "";
            String date = "";
            String status = "";
            String statusBaru = "";

            if (listItem3.size() == 1) {
                RoomsDetail roomsDetail = listItem3.get(0);
                JSONObject jO = null;
                try {
                    jO = new JSONObject(roomsDetail.getContent());
                    String content = "";
                    if (jO.has("aa")) {
                        content = jO.getString("aa");
                    }

                    if (jO.has("bb")) {
                        statusBaru = jO.getString("bb");
                    }

                    title = abs(content, roomsDetail.getFlag_tab());

                } catch (JSONException e) {
                    title = abs(roomsDetail.getContent(), roomsDetail.getFlag_tab());
                    e.printStackTrace();
                }


            } else if (listItem3.size() == 2) {

                RoomsDetail roomsDetail1 = listItem3.get(0);
                RoomsDetail roomsDetail2 = listItem3.get(1);

                if (roomsDetail1.getFlag_content().equalsIgnoreCase("1")) {
                    JSONObject jO = null;

                    for (int i = 0; i < listItem3.size(); i++) {
                        try {
                            jO = new JSONObject(listItem3.get(i).getContent());
                            String content = "";
                            if (jO.has("aa")) {
                                content = jO.getString("aa");
                            }

                            if (jO.has("bb")) {
                                statusBaru = jO.getString("bb");
                            }

                            if (i == 0) {
                                title = abs(content, roomsDetail1.getFlag_tab());
                            }
                            if (i == 1) {
                                desc = abs(content, roomsDetail2.getFlag_tab());
                            }

                        } catch (JSONException e) {
                            title = abs(roomsDetail1.getContent(), roomsDetail1.getFlag_tab());
                            desc = abs(roomsDetail2.getContent(), roomsDetail2.getFlag_tab());
                            e.printStackTrace();
                        }
                    }

                } else {
                    JSONObject jO = null;

                    for (int i = 0; i < listItem3.size(); i++) {
                        try {
                            jO = new JSONObject(listItem3.get(i).getContent());
                            String content = "";
                            if (jO.has("aa")) {
                                content = jO.getString("aa");
                            }

                            if (jO.has("bb")) {
                                statusBaru = jO.getString("bb");
                            }

                            if (i == 0) {
                                title = abs(content, roomsDetail1.getFlag_tab());
                            }
                            if (i == 1) {
                                desc = abs(content, roomsDetail2.getFlag_tab());
                            }

                        } catch (JSONException e) {
                            title = abs(roomsDetail1.getContent(), roomsDetail1.getFlag_tab());
                            desc = abs(roomsDetail2.getContent(), roomsDetail2.getFlag_tab());
                            e.printStackTrace();
                        }
                    }
                }
            }

            date = aa.getContent();
            status = aa.getFlag_content();

            if (!statusBaru.equalsIgnoreCase("")) {
                if (statusBaru.equalsIgnoreCase("true")) {
                    status = "Reject";
                }
            }


            String titLes = Message.parsedMessageText(JsonToStringKey(title));
            if (titLes.contains("https")) {
                titLes = desc;
                desc = "";
            }

            ContentRoom contentRoom = new ContentRoom(aa.getId(), titLes, date, desc, "", status, "");

            if (aa.getId().contains("|")) {
                if (NetworkInternetConnectionStatus.getInstance(getContext()).isOnline(getContext())) {
                    new Refresh(getActivity()).execute(aa.getId(), username, idTab);
                }
            }

            if (!status.equalsIgnoreCase("11")) {
                if (!listItem.equals(contentRoom)) {
                    listItem.add(contentRoom);
                }
            }


        }

       // Collections.sort(listItem, new Sortiran());
        myadapter.notifyDataSetChanged();
    }


    private class Refresh extends AsyncTask<String, String, String> {
        private Context context;

        public Refresh(Activity activity) {
            context = activity;
        }

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            postData(params[0], params[1], params[2]);
            return null;
        }

        protected void onPostExecute(String result) {

        }

        protected void onProgressUpdate(String... string) {
        }

        public void postData(String getId, String usr, String idTab) {


            //aa.getId(), username, idTab, "value"
            // Create a new HttpClient and Post Header

            Cursor cursorValue = BotListDB.getInstance(getContext()).getSingleRoomDetailFormWithFlag(getId, usr, idTab, "value");
            if (cursorValue.getCount() == 0) {
                if (username != null) {

                    try {
                        HttpParams httpParameters = new BasicHttpParams();
                        HttpConnectionParams.setConnectionTimeout(httpParameters, 13000);
                        HttpConnectionParams.setSoTimeout(httpParameters, 15000);
                        HttpClient httpclient = new DefaultHttpClient(httpParameters);
                        HttpPost httppost = new HttpPost(new ValidationsKey().getInstance(getContext()).getTargetUrl(usr) + GETTABDETAILPULLMULTIPLE);

                        // Add your data
                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                        nameValuePairs.add(new BasicNameValuePair("username_room", usr));
                        nameValuePairs.add(new BasicNameValuePair("id_rooms_tab", idTab));

                        if (getId != null || !getId.equalsIgnoreCase("")) {
                            String[] ff = getId.split("\\|");
                            if (ff.length == 2) {
                                nameValuePairs.add(new BasicNameValuePair("parent_id", ff[1]));
                                nameValuePairs.add(new BasicNameValuePair("id_list_push", ff[0]));
                            }
                        }

                        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                        // Execute HTTP Post Request
                        HttpResponse response = httpclient.execute(httppost);
                        int status = response.getStatusLine().getStatusCode();
                        if (status == 200) {
                            HttpEntity entity = response.getEntity();
                            String data = EntityUtils.toString(entity);

                            try {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                Calendar cal = Calendar.getInstance();
                                String time_str = dateFormat.format(cal.getTime());
                                JSONObject jsonRootObject = new JSONObject(data);
                                String username = jsonRootObject.getString("username_room");
                                String id_rooms_tab = jsonRootObject.getString("id_rooms_tab");
                                String attachment = jsonRootObject.getString("attachment");
                                String content = jsonRootObject.getString("data");
                                String include_assignto = jsonRootObject.getString("include_assignto");


                                JSONObject jsonObject = new JSONObject();
                                if (data.contains("include_status_task")) {
                                    String include_status_task = jsonRootObject.getString("include_status_task");
                                    jsonObject.put("status_task", include_status_task);
                                }


                                if (jsonRootObject.has("label_status_approve")) {
                                    String label_status_approve = jsonRootObject.getString("label_status_approve");
                                    jsonObject.put("approve", label_status_approve);
                                }

                                if (jsonRootObject.has("label_status_reject")) {
                                    String label_status_reject = jsonRootObject.getString("label_status_reject");
                                    jsonObject.put("reject", label_status_reject);
                                }

                                if (jsonRootObject.has("label_status_done")) {
                                    String label_status_done = jsonRootObject.getString("label_status_done");
                                    jsonObject.put("done", label_status_done);
                                }

                                String api_officers = jsonRootObject.getString("api_officers");


                                BotListDB db = BotListDB.getInstance(context);
                                db.deleteRoomsDetailPtabPRoomNotValue(id_rooms_tab, username, from);

                                RoomsDetail orderModel = new RoomsDetail(getId, id_rooms_tab, username, jsonRootObject.getString("list_pull"), "", time_str, "value");
                                db.insertRoomsDetail(orderModel);



                                String ccc = jsonDuaObjectW(content, attachment, api_officers, jsonObject.toString(), context.getResources().getString(R.string.app_version));
                                if (include_assignto.equalsIgnoreCase("0")) {
                                    ccc = jsonDuaObjectW(content, attachment, "", jsonObject.toString(), context.getResources().getString(R.string.app_version));
                                }


                                String bawaDariBelakang = "";
                                if (jsonRootObject.has("anothers")) {
                                    JSONObject tambahan = new JSONObject("{}");
                                    if (jsonRootObject.has("alasan_reject")) {
                                        if (jsonRootObject.has("anothers")) {
                                            String anothers = jsonRootObject.getString("anothers");
                                            if (!anothers.equalsIgnoreCase("[]")) {
                                                tambahan = new JSONObject(anothers);
                                                tambahan.put("message", jsonRootObject.getJSONObject("alasan_reject").getString("message"));
                                                bawaDariBelakang = tambahan.toString();
                                            } else {
                                                bawaDariBelakang = "{}";
                                            }
                                        }
                                    }
                                }


                                RoomsDetail orderModel2 = new RoomsDetail(username, id_rooms_tab, username, ccc, bawaDariBelakang, time_str, "form");
                                db.insertRoomsDetail(orderModel2);


                                   /* mContext.runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(mContext, "Success Download Value", Toast.LENGTH_SHORT).show();
                                        }
                                    });*/


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    } catch (ConnectTimeoutException e) {
                        e.printStackTrace();
                    } catch (ClientProtocolException e) {
                        // TODO Auto-generated catch block
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                    }

                    // new Refresh(getActivity()).execute(new ValidationsKey().getInstance(getContext()).getTargetUrl(username) + GETTABDETAILPULLMULTIPLE, username, idTab, aa.getId());
                }
            }


        }
    }

    private String jsonDuaObjectW(String a, String b, String c, String d, String ver) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("aa", a);
            obj.put("bb", b);

            if (!c.equalsIgnoreCase("")) {
                obj.put("cc", c);
            }

            if (!d.equalsIgnoreCase("")) {
                obj.put("dd", d);
            }

            obj.put("ver", ver);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }


  /*  static class Sortiran implements Comparator<ContentRoom> {

        @Override
        public int compare(ContentRoom e1, ContentRoom e2) {
            Date satu = Utility.convertStringToDate(Utility.parseDateToddMMyyyy(e1.getTime()));
            Date dua = Utility.convertStringToDate(Utility.parseDateToddMMyyyy(e2.getTime()));
            if (satu.compareTo(dua) > 0) {
                return -1;
            } else {
                return 1;
            }
        }
    }*/

    private String jsonDuaObject(String a, String b) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("aa", a);
            obj.put("bb", b);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }

    public void onActionSearch(String args) {
        myadapter.getFilter().filter(args);
    }

}