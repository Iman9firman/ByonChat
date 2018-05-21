package com.byonchat.android.room;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity;
import com.byonchat.android.R;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.personalRoom.asynctask.ProfileSaveDescription;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.ContentRoom;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.provider.SubmitingModel;
import com.byonchat.android.provider.SubmitingRoomDB;
import com.byonchat.android.utils.EndlessRecyclerOnScrollListener;
import com.byonchat.android.utils.HttpHelper;
import com.byonchat.android.utils.RequestKeyTask;
import com.byonchat.android.utils.TaskCompleted;
import com.byonchat.android.utils.UploadService;
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
import java.io.InputStream;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@SuppressLint("ValidFragment")
public class FragmentRoomMultipleTask extends Fragment {

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
                ((ByonChatMainRoomActivity) mContext).deleteById(position);
            }
        });


        myadapter.setOnItemClickListener(
                new DinamicListTaskAdapter.MyClickListener() {
                    @Override
                    public void onItemClick(int position, View v) {
                        ((ByonChatMainRoomActivity) mContext).idLoof(position);

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

        /*SubmitingRoomDB submitingRoomDB = SubmitingRoomDB.getInstance(getApplicationContext());
        SubmitingModel submitingModel = new SubmitingModel();
        submitingModel.setStatus("0");
        submitingModel.setContent(jsonObject.toString());
        Message message = new Message();
        message.setMessage(jsonObject.toString());
        message.setId(submitingRoomDB.createContact(submitingModel));

        Intent intent = new Intent(getApplicationContext(), UploadService.class);
        intent.putExtra(UploadService.ACTION, "uploadTaskRoom");
        intent.putExtra(UploadService.KEY_MESSAGE, message);
        startService(intent);
        */
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
                            if (oParent.has("is_reject")) {
                                is_reject = oParent.getString("is_reject");
                            }

                            Log.w("subami", is_reject);

                            Cursor cursorParent = botListDB.getSingleRoomDetailFormWithFlag(id + "|" + parent_id, username, idTab, "parent");

                            if (cursorParent.getCount() == 0) {
                                RoomsDetail orderModel = new RoomsDetail(id + "|" + parent_id, idTab, username, date, "4", "", "parent");
                                botListDB.insertRoomsDetail(orderModel);
                                if (joContent.length() == 1) {
                                    RoomsDetail orderModelTitle211 = new RoomsDetail(id + "|" + parent_id, idTab, username, joContent.getJSONObject(0).getString("value").toString(), "1", joContent.getJSONObject(0).getString("type").toString(), "list");
                                    RoomsDetail orderModelTitle2 = new RoomsDetail(id + "|" + parent_id, idTab, username, jsonDuaObject(va(orderModelTitle211), is_reject), "1", joContent.getJSONObject(0).getString("type").toString(), "list");
                                    botListDB.insertRoomsDetail(orderModelTitle2);
                                } else if (joContent.length() > 1) {
                                    RoomsDetail orderModelTitle21a = new RoomsDetail(id + "|" + parent_id, idTab, username, joContent.getJSONObject(0).getString("value").toString(), "1", joContent.getJSONObject(0).getString("type").toString(), "list");
                                    RoomsDetail orderModelTitle21 = new RoomsDetail(id + "|" + parent_id, idTab, username, jsonDuaObject(va(orderModelTitle21a), is_reject), "1", joContent.getJSONObject(0).getString("type").toString(), "list");
                                    botListDB.insertRoomsDetail(orderModelTitle21);
                                    RoomsDetail orderModelTitle2a = new RoomsDetail(id + "|" + parent_id, idTab, username, joContent.getJSONObject(0).getString("value").toString(), "2", joContent.getJSONObject(1).getString("type").toString(), "list");
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
                            }else {
                                Log.w("disini", "1");
                                if (NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)) {
                                    Log.w("disini", "2");
                                    if (!roomsDetail.getId().equalsIgnoreCase("")) {
                                        Log.w("disini", "3");
                                        String[] ff = roomsDetail.getId().split("\\|");
                                        if (ff.length == 2) {
                                            BotListDB db = BotListDB.getInstance(getContext());
                                            Cursor cursorValue = db.getSingleRoomDetailFormWithFlag(roomsDetail.getId(), username, idTab, "value");
                                            if (cursorValue.getCount() == 0) {

                                                JSONObject jsonObject = new JSONObject();
                                                try {
                                                    jsonObject.put("idDetail", roomsDetail.getId());
                                                    jsonObject.put("username", username);
                                                    jsonObject.put("idTab", idTab);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }


                                                SubmitingRoomDB submitingRoomDB = SubmitingRoomDB.getInstance(mContext);

                                                Cursor cc = submitingRoomDB.getSingleContactByContent(jsonObject.toString());

                                                if (cc.getCount() == 0) {

                                                    SubmitingModel submitingModel = new SubmitingModel();
                                                    submitingModel.setStatus("0");
                                                    submitingModel.setContent(jsonObject.toString());

                                                    Message message = new Message();
                                                    message.setMessage(jsonObject.toString());
                                                    message.setId(submitingRoomDB.createContact(submitingModel));

                                                    Intent intent = new Intent(mContext, UploadService.class);
                                                    intent.putExtra(UploadService.ACTION, "downloadValueForm");
                                                    intent.putExtra(UploadService.KEY_MESSAGE, message);
                                                    mContext.startService(intent);

                                                }
                                            }
                                        }
                                    }
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

        }


        return content;
    }

    public void refreshList() {
        listItem.clear();
        listItem2 = botListDB.allRoomDetailFormWithFlag("", username, idTab, "parent");
        for (RoomsDetail aa : listItem2) {
            ArrayList<RoomsDetail> listItem3 = botListDB.allRoomDetailFormWithFlag(aa.getId(), username, idTab, "list");
            String title = "";
            String desc = "";
            String date = "";
            String status = "";
            String statusBaru = "";

            for (RoomsDetail ii : listItem3) {
                if (ii.getFlag_content().equalsIgnoreCase("1")) {

                    JSONObject jO = null;
                    try {
                        jO = new JSONObject(ii.getContent());
                        String content = jO.getString("aa");
                        statusBaru = jO.getString("bb");
                        title = abs(content, ii.getFlag_tab());

                    } catch (JSONException e) {
                        title = abs(ii.getContent(), ii.getFlag_tab());
                        e.printStackTrace();
                    }


                } else {
                    desc = abs(ii.getContent(), ii.getFlag_tab());
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




            if (!status.equalsIgnoreCase("11")) {
                if (!listItem.equals(contentRoom)) {
                    listItem.add(contentRoom);
                }
            }
        }


        Collections.sort(listItem, new Sortiran());


        myadapter.notifyDataSetChanged();
    }

    static class Sortiran implements Comparator<ContentRoom> {

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
    }

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


}