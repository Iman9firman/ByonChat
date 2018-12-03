package com.byonchat.android.tempSchedule;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity;
import com.byonchat.android.FragmentDinamicRoom.RoomPOSdetail;
import com.byonchat.android.R;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.room.DividerItemDecoration;
import com.byonchat.android.room.FragmentRoomMultipleTask;
import com.byonchat.android.utils.HttpHelper;
import com.byonchat.android.utils.RequestKeyTask;
import com.byonchat.android.utils.TaskCompleted;
import com.byonchat.android.utils.Validations;

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
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NotePreviewActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton fab;
    LinearLayoutManager manager;
    NotePreviewAdapter adapter;
    MyEventDatabase eventDatabase;
    SQLiteDatabase db;

    String titleBar;
    String startDate;
    String linkTembak;
    String username;
    String color;
    String latLong;
    String idTab;
    String title;
    String from;
    String calendar;

    NotePreviewActivity.requestTask requestTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_preview);
        //Pref.setFlag(this,3);

        Intent intent = getIntent();
        if (intent != null) {
            titleBar = intent.getStringExtra("TITLE");
            startDate = intent.getStringExtra(TempScheduleRoom.EVENT);
            title = intent.getStringExtra("tt");
            linkTembak = intent.getStringExtra("cc");
            username = intent.getStringExtra("uu");
            idTab = intent.getStringExtra("ii");
            color = intent.getStringExtra("col");
            latLong = intent.getStringExtra("ll");
            from = intent.getStringExtra("from");
            calendar = "true boi";
            setTitle(titleBar);
            getSupportActionBar().setBackgroundDrawable(new Validations().getInstance(getBaseContext()).headerCostume(getWindow(), "#" + color));
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_notepreview);
        fab = (FloatingActionButton) findViewById(R.id.fab_notepreview);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_notepreview);
         if (!checkDate(startDate)){
            fab.setVisibility(View.GONE);
        } else {
            fab.setVisibility(View.VISIBLE);
        }
        requestKey();
        fabMethod();
    }
    private void requestKey() {
        RequestKeyTask testAsyncTask = new RequestKeyTask(new TaskCompleted() {
            @Override
            public void onTaskDone(String key) {
                if (key.equalsIgnoreCase("null")) {
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    requestTask = new requestTask(getApplicationContext());
                    requestTask.execute(key);
                }
            }
        }, getBaseContext());

        testAsyncTask.execute();
    }
    private void fabMethod(){
        fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#" + color)));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                intent.putExtra("DATE",startDate);
                Intent intent = new Intent(getApplicationContext(), DinamicRoomTaskActivity.class);
                String id_detail = getRandomString();
                intent.putExtra("tt", title);
                intent.putExtra("uu", username);
                intent.putExtra("ii", idTab);
                intent.putExtra("col", color);
                intent.putExtra("ll", latLong);
                intent.putExtra("from", from);
                intent.putExtra("idTask", id_detail);
                intent.putExtra("clndr",calendar);
                intent.putExtra("strtdt",startDate);
                generateDB(startDate,id_detail,idTab,"","Draft","#"+color,0);
                startActivity(intent);
            }
        });
    }
    private void viewMethod(String content) throws Exception{
        manager = new LinearLayoutManager(this);
        DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(),manager.getOrientation());
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(divider);

        adapter = new NotePreviewAdapter(NotePreviewActivity.this,getInside(content),title,username,idTab,color,latLong,from,calendar,startDate);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

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
    protected void onResume() {
        requestKey();
        super.onResume();
    }
    private ArrayList<Note> getInside(String content){
        ArrayList<Note> noteList = new ArrayList<>();
        ArrayList<RoomsDetail> listItem3 = new ArrayList<>();

//        Database
        BotListDB botListDB = BotListDB.getInstance(getApplicationContext());

        try {
            JSONObject joAwal = new JSONObject(content);
            JSONArray jaAwal = joAwal.getJSONArray("tasks");
            for (int ba = 0 ; ba<jaAwal.length();ba++){

                JSONObject joKedua = jaAwal.getJSONObject(ba);
                JSONArray jaKedua = joKedua.getJSONArray("value");
                String selected_date = joKedua.getString("selected_date");
                String id = joKedua.getString("id");
                String status = joKedua.getString("report_status");
                String warna = joKedua.getString("bg_status");

                generateDB(dateFormat(selected_date),id,idTab,jaKedua.toString(),status,warna,1);

            }
        } catch (JSONException e){}

        eventDatabase = new MyEventDatabase(getApplicationContext());
        db = eventDatabase.getReadableDatabase();
        String[] args = {startDate , idTab};
        Cursor c = db.rawQuery("SELECT id_detail_event,value_event,status_event,warna_event,startDate_event FROM event"+" WHERE startDate_event = ? AND id_tab_event = ? ",args);
        while (c.moveToNext()){
            List<String> nampan2 = new ArrayList<>();
            String id_detail = c.getString(0);
            String lokasi = null;
            String keterangan = null;
            String startTime = null;
            String endTime = null;
            String alasan = null;
            String value = c.getString(1);
            String status = c.getString(2);
            String warna = c.getString(3);
            String selected_date = c.getString(4);


            listItem3 = botListDB.allRoomDetailFormWithFlag(id_detail, username, idTab, "cild");
            Note n = null;
            if (value.equalsIgnoreCase("")){
                for (int j = 0 ; j <listItem3.size() ; j++){
                    String flag_tab = listItem3.get(j).getFlag_tab();
                    String cnt = listItem3.get(j).getContent();
                    if (flag_tab.equalsIgnoreCase("jam_mulai")){
                        startTime = cnt;
                    } else if (flag_tab.equalsIgnoreCase("jam_selesai")){
                        endTime = cnt;
                    } else if (flag_tab.equalsIgnoreCase("lokasi")){
                        lokasi = cnt;
                    } else if (flag_tab.equalsIgnoreCase("keterangan")){
                        keterangan = cnt;
                    } else if (flag_tab.equalsIgnoreCase("alasan")){
                        alasan = cnt;
                    }
                }
                n = new Note(lokasi,startTime,endTime,keterangan,alasan,id_detail,status,false,warna);
            } else {
                /*
                0 = lokasi
                1 = detail lokasi
                2 = jam mulai
                3 = jam selesai
                4 = keterangan
                5 = alasan
                */
                try {
                    JSONArray arrayValue = new JSONArray(value);
                    for (int iv = 0 ; iv<arrayValue.length();iv++){
                        JSONObject a = arrayValue.getJSONObject(iv);
                        String values = a.getString("value");
                        nampan2.add(values);
                    }
                    if (selected_date.equalsIgnoreCase(startDate)) {
                        n = new Note(nampan2.get(0), nampan2.get(2), nampan2.get(3), nampan2.get(4), nampan2.get(5), id_detail, status,true,warna);
                    }

                } catch (JSONException e){}

            }
            noteList.add(n);
            db.close();
        }

        return noteList;
    }
    private boolean checkDate(String eventDate){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = f.format(cal.getTime());
        try {
            Date curDate =f.parse(currentDate);
            Date evDate = f.parse(eventDate);

            if (evDate.before(curDate)){
                if (currentDate.equalsIgnoreCase(eventDate)){
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    private String getRandomString() {
        long currentTimeMillis = System.currentTimeMillis();
        SecureRandom random = new SecureRandom();

        char[] CHARSET_AZ_09 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        char[] result = new char[15];
        for (int i = 0; i < result.length; i++) {
            int randomCharIndex = random.nextInt(CHARSET_AZ_09.length);
            result[i] = CHARSET_AZ_09[randomCharIndex];
        }

        String resRandom = String.valueOf(currentTimeMillis) + new String(result);

        return resRandom;
    }
    private void generateDB(String date,String id_detail,String id_tab,String value,String status,String warna,int isSubmit){
        eventDatabase = new MyEventDatabase(getApplicationContext());
        db = eventDatabase.getWritableDatabase();

        ContentValues v = new ContentValues();
        v.put(MyEventDatabase.EVENT_START_DATE,date);
        v.put(MyEventDatabase.EVENT_ID_DETAIL,id_detail);
        v.put(MyEventDatabase.EVENT_ID_TAB,id_tab);
        v.put(MyEventDatabase.EVENT_STATUS,status);
        v.put(MyEventDatabase.EVENT_VALUE,value);
        v.put(MyEventDatabase.EVENT_WARNA,warna);
        v.put(MyEventDatabase.EVENT_ISSUBMIT,isSubmit);

        long id = db.insert(MyEventDatabase.TABLE_EVENT,null,v);
        db.close();
    }
    private void deleteDB(int satu){
        eventDatabase = new MyEventDatabase(getApplicationContext());
        db = eventDatabase.getWritableDatabase();

        db.delete(MyEventDatabase.TABLE_EVENT,MyEventDatabase.EVENT_ISSUBMIT+"="+satu,null);
        db.close();

    }
    private String dateFormat(String date){
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date datenya = format.parse(date);

            SimpleDateFormat ubah = new SimpleDateFormat("dd-MM-yyyy");
            date = ubah.format(datenya);
        } catch (Exception e){
        }
        return date;
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

                MessengerDatabaseHelper dbhelper = MessengerDatabaseHelper.getInstance(getBaseContext());

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
//                    JSONObject result = new JSONObject(content.toString());


                    /*JSONArray menuitemArray = result.getJSONArray("list_pull");
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
                            }
                        }
                    }*/

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
            Log.w("zharfan", "Content : "+content );
            try {
                swipeRefreshLayout.setRefreshing(false);
                if (error) {
                    if (content != null) {
                        if (content.contains("bb.byonchat.com")) {
                            if (NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)) {
                                requestKey();
                            } else {
//                                deleteDB(1);
                                viewMethod(content);
                                swipeRefreshLayout.setRefreshing(false);
                                Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            deleteDB(1);
                            viewMethod(content);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    } else {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    deleteDB(1);
                    viewMethod(content);
                }
            } catch (Exception e){
                Log.w("zharfan", "Exception : "+e.getMessage() );
            }

        }
    }
}