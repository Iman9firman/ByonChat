package com.byonchat.android.FragmentDinamicRoom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
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

import com.byonchat.android.R;
import com.byonchat.android.personalRoom.FragmentMyNews;
import com.byonchat.android.personalRoom.adapter.DirectoryFeedListAdapter;
import com.byonchat.android.personalRoom.adapter.NewsFeedListAdapter;
import com.byonchat.android.personalRoom.asynctask.ProfileSaveDescription;
import com.byonchat.android.personalRoom.model.NewsFeedItem;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.RoomsDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lukma on 3/4/2016.
 */
@SuppressLint("ValidFragment")
public class FragmentDirectory extends Fragment{
    private static final String TAG = FragmentDirectory.class.getSimpleName();
    private List<NewsFeedItem> feedItems;

    private String myContact;
    private String title;
    private String urlTembak;
    private String username;
    private String idRoomTab;
    private String color;
    private Activity mContext ;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRecyclerView;
    SwipeRefreshLayout mswipeRefreshLayout;
    private DirectoryFeedListAdapter adapter;
    BotListDB botListDB;

    public FragmentDirectory(Activity ctx) {
        mContext = ctx;

    }

    // newInstance constructor for creating fragment with arguments
    public static FragmentDirectory newInstance(String myc, String tit, String utm, String usr, String idrtab,String color,Activity ctx) {
        FragmentDirectory fragmentRoomTask = new FragmentDirectory(ctx);
        Bundle args = new Bundle();
        args.putString("aa", tit);
        args.putString("bb", utm);
        args.putString("cc", usr);
        args.putString("dd", idrtab);
        args.putString("ee", myc);
        args.putString("col", color);
        fragmentRoomTask.setArguments(args);
        return fragmentRoomTask;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View sss = inflater.inflate(R.layout.room_fragment_list, container, false);
       /*NEW*/
        mswipeRefreshLayout = (SwipeRefreshLayout) sss.findViewById(R.id.activity_main_swipe_refresh_layout);
        mRecyclerView = (RecyclerView) sss.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);


        return sss;
    }


    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (botListDB == null) {
            botListDB = BotListDB.getInstance(mContext.getApplicationContext());
        }
        feedItems = new ArrayList<NewsFeedItem>();
        ArrayList<RoomsDetail> allRoomDetailFormWithFlag = botListDB.allRoomDetailFormWithFlag("",username,idRoomTab,"value");
        if(allRoomDetailFormWithFlag.size()>0){
            refresh(allRoomDetailFormWithFlag,false);
        }else{
            new getLink().execute(urlTembak, myContact, username, idRoomTab);
        }

        mswipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
      /*  if(feedItems.size()==0){
            refreshItems();
        }*/
    }

    void refreshItems() {
        new getLink().execute(urlTembak, myContact, username, idRoomTab);
    }

    void onItemsLoadComplete() {
        mswipeRefreshLayout.setRefreshing(false);
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
            data.put("username_room", params[2]);
            data.put("id_rooms_tab", params[3]);
            data.put("bc_user", params[1]);
            String result = profileSaveDescription.sendPostRequest(params[0], data);
            return result;
        }

        protected void onPostExecute(String s) {
            if (s.equals(null)) {
            } else {
                JSONArray dataJsonArr = null;
                try {
                    JSONObject json = new JSONObject(s);
                    String usr_room = json.getString("username_room");
                    String rooms_tab = json.getString("id_rooms_tab");
                    botListDB.deleteRoomsDetailPtabPRoom(idRoomTab,username);
                    dataJsonArr = json.getJSONArray("data");
                    for (int i = 0; i < dataJsonArr.length(); i++) {
                        JSONObject c = dataJsonArr.getJSONObject(i);
                        String id = c.getString("id");
                        RoomsDetail orderModel = new RoomsDetail(id, rooms_tab,  usr_room, dataJsonArr.getJSONObject(i).toString(), "", "", "value");
                        botListDB.insertRoomsDetail(orderModel);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ArrayList<RoomsDetail> allRoomDetailFormWithFlag = botListDB.allRoomDetailFormWithFlag("",username,idRoomTab,"value");
                if(allRoomDetailFormWithFlag!=null){
                    refresh(allRoomDetailFormWithFlag,true);
                }

                onItemsLoadComplete();
            }
        }
    }

    public void refresh(ArrayList<RoomsDetail>  s, boolean refresh){

        try {

            if (refresh){
                feedItems  = new ArrayList<>();
            }

            for (RoomsDetail ss : s) {
                JSONObject c = new JSONObject(ss.getContent());
                String title = c.getString("title");
                String content_note = c.getString("description");
                String image = c.getString("image");
                String url = c.getString("url");
                String type = c.getString("type");
                String create_at = c.getString("created_at");
                NewsFeedItem newsFeedItem = new NewsFeedItem();
                int foo = Integer.parseInt(type);
                newsFeedItem.setMyuserid(username);
                newsFeedItem.setLevel(foo);
                newsFeedItem.setIdRoomTab(idRoomTab);
                newsFeedItem.setTitle(title);
                newsFeedItem.setUrl(url);
                newsFeedItem.setStatus(content_note);
                newsFeedItem.setColorHeader(color);
                newsFeedItem.setImage(image);
                newsFeedItem.setTimeStamp(create_at);
                feedItems.add(newsFeedItem);
                }
                adapter = new DirectoryFeedListAdapter(mContext);
                adapter.setData(feedItems);
                mRecyclerView.setAdapter(adapter);

        }catch(JSONException e){
            e.printStackTrace();
        }

    }
}



