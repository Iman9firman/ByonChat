package com.honda.android.FragmentDinamicRoom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
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

import com.honda.android.R;
import com.honda.android.communication.NetworkInternetConnectionStatus;
import com.honda.android.personalRoom.FragmentMyNews;
import com.honda.android.personalRoom.adapter.NewsFeedListAdapter;
import com.honda.android.personalRoom.asynctask.ProfileSaveDescription;
import com.honda.android.personalRoom.model.NewsFeedItem;
import com.honda.android.provider.BotListDB;
import com.honda.android.provider.RoomsDetail;
import com.honda.android.utils.OnLoadMoreListener;

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
public class FragmentStreamingVideo extends Fragment{
    private static final String TAG = FragmentStreamingVideo.class.getSimpleName();
    private List<NewsFeedItem> feedItems;

    private String myContact;
    private String title;
    private String urlTembak;
    private String username;
    private String idRoomTab;

    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRecyclerView;
    SwipeRefreshLayout mswipeRefreshLayout;
    private NewsFeedListAdapter adapter;
    BotListDB db;
    protected Handler handler = new Handler();
    public static int pageNumber;
    private Activity mContext;

    public FragmentStreamingVideo(Activity ctx) {
        mContext = ctx;

    }

    // newInstance constructor for creating fragment with arguments
    public static FragmentStreamingVideo newInstance(String myc, String tit, String utm, String usr, String idrtab,Activity ctx) {
        FragmentStreamingVideo fragmentStreamingVideo = new FragmentStreamingVideo(ctx);
        Bundle args = new Bundle();
        args.putString("aa", tit);
        args.putString("bb", utm);
        args.putString("cc", usr);
        args.putString("dd", idrtab);
        args.putString("ee", myc);
        fragmentStreamingVideo.setArguments(args);
        return fragmentStreamingVideo;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View sss = inflater.inflate(R.layout.room_fragment_streaming_video, container, false);
       /*NEW*/
        mswipeRefreshLayout = (SwipeRefreshLayout) sss.findViewById(R.id.activity_main_swipe_refresh_layout);
        mRecyclerView = (RecyclerView) sss.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);


        return sss;
    }


    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pageNumber = 1;

        if (db == null) {
            db = BotListDB.getInstance(mContext.getApplicationContext());
        }

        if (null == feedItems) {
            feedItems = new ArrayList<NewsFeedItem>();
        }

        adapter = new NewsFeedListAdapter(mContext, feedItems, mRecyclerView);
        mRecyclerView.setAdapter(adapter);

        if (NetworkInternetConnectionStatus.getInstance(mContext.getApplicationContext()).isOnline(mContext.getApplicationContext())) {
            if (adapter.getItemCount() == 0) {
                new getLink(pageNumber).execute(urlTembak, myContact, username, idRoomTab);
            }
        } else {
            ArrayList<RoomsDetail> allRoomDetailFormWithFlag = db.allRoomDetailFormWithFlag("", username, idRoomTab, "value");
            if (allRoomDetailFormWithFlag.size() > 0) {
                refresh(allRoomDetailFormWithFlag, false);
            } else {
                if (NetworkInternetConnectionStatus.getInstance(mContext.getApplicationContext()).isOnline(mContext.getApplicationContext())) {
                    new getLink(pageNumber).execute(urlTembak, myContact, username, idRoomTab);
                }
            }
        }

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                feedItems.add(null);
                adapter.notifyItemInserted(feedItems.size() - 1);
                ++pageNumber;

                new getLinkNext(pageNumber, mRecyclerView.getAdapter().getItemCount()).execute(urlTembak, myContact, username, idRoomTab);
            }
        });

        /*db.open();
        Cursor cursorValue = db.getSingleRoomDetailFormWithFlag("", username, idRoomTab, "value");
        db.close();


        if (cursorValue.getCount() > 0) {
            final String contentValue = cursorValue.getString(cursorValue.getColumnIndexOrThrow(BotListDB.ROOM_CONTENT));
            Log.w("ada1","hmmm"+contentValue);
            if(!contentValue.equalsIgnoreCase("")){
                refresh(contentValue);
            }else {
                new getLink().execute(urlTembak, myContact, username, idRoomTab);
            }
        }else {

            new getLink().execute(urlTembak, myContact, username, idRoomTab);
        }
*/


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
        if (NetworkInternetConnectionStatus.getInstance(mContext.getApplicationContext()).isOnline(mContext.getApplicationContext())) {
            if (adapter.getItemCount() == 0) {
                new getLink(pageNumber).execute(urlTembak, myContact, username, idRoomTab);
            }
        }else{
            Toast.makeText(mContext.getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
        onItemsLoadComplete();
    }

    void onItemsLoadComplete() {
        mswipeRefreshLayout.setRefreshing(false);
    }


    class getLink extends AsyncTask<String, Void, String> {
        ProgressDialog loading;
        ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
        String result = "";
        InputStream inputStream = null;
        int pageNumber;

        public getLink(int pageNumber) {
            this.pageNumber = pageNumber;
        }

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

                if (pageNumber > 1) {
                    feedItems.remove(feedItems.size() - 1);
                    adapter.notifyItemRemoved(feedItems.size());
                }
                JSONArray dataJsonArr = null;
                try {
                    feedItems.clear();
                    JSONObject json = new JSONObject(s);
                    String usr_room = json.getString("username_room");
                    String rooms_tab = json.getString("id_rooms_tab");
                    db.deleteRoomsDetailPtabPRoom(idRoomTab,username);
                    dataJsonArr = json.getJSONArray("data");
                    for (int i = 0; i < dataJsonArr.length(); i++) {
                        JSONObject c = dataJsonArr.getJSONObject(i);
                        RoomsDetail orderModel = new RoomsDetail("", rooms_tab,  usr_room, dataJsonArr.getJSONObject(i).toString(), "", "", "value");
                        db.insertRoomsDetail(orderModel);
                        String title = c.getString("title");
                        String content_note = c.getString("description");
                        String image = c.getString("url_tembak");
                        String create_at = c.getString("created_at");
                        NewsFeedItem newsFeedItem = new NewsFeedItem();

                        newsFeedItem.setMyuserid(username);
                        newsFeedItem.setIdRoomTab(idRoomTab);
                        newsFeedItem.setTitle(title);
                        newsFeedItem.setStatus(content_note);
                        newsFeedItem.setImage(image);
                        newsFeedItem.setTimeStamp(create_at);

                        feedItems.add(newsFeedItem);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyItemInserted(feedItems.size());
                            }
                        });
                    }
                    adapter.notifyDataSetChanged();
                    adapter.setLoaded();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ArrayList<RoomsDetail> allRoomDetailFormWithFlag = db.allRoomDetailFormWithFlag("",username,idRoomTab,"value");
                if(allRoomDetailFormWithFlag!=null){
                    refresh(allRoomDetailFormWithFlag,true);
                }
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
                String image = c.getString("url_tembak");
                String create_at = c.getString("created_at");
                NewsFeedItem newsFeedItem = new NewsFeedItem();

                newsFeedItem.setMyuserid(username);
                newsFeedItem.setIdRoomTab(idRoomTab);
                newsFeedItem.setTitle(title);
                newsFeedItem.setStatus(content_note);
                newsFeedItem.setImage(image);
                newsFeedItem.setTimeStamp(create_at);
                feedItems.add(newsFeedItem);
                }
                adapter = new NewsFeedListAdapter(getActivity());
                adapter.setData(feedItems);
                mRecyclerView.setAdapter(adapter);

        }catch(JSONException e){
            e.printStackTrace();
        }

    }


    class getLinkNext extends AsyncTask<String, Void, String> {
        ProgressDialog loading;
        ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
        String result = "";
        InputStream inputStream = null;
        int pageNumber;
        int totalItem;

        public getLinkNext(int pageNumber, int totalItem) {
            this.pageNumber = pageNumber;
            this.totalItem = totalItem;
        }

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
            data.put("last_page", totalItem+"");
            String result = profileSaveDescription.sendPostRequest(params[0], data);
            return result;
        }

        protected void onPostExecute(String s) {
            if (s.equals(null)) {
            } else {
                if (pageNumber > 1) {
                    feedItems.remove(feedItems.size() - 1);
                    adapter.notifyItemRemoved(feedItems.size());
                }
                JSONArray dataJsonArr = null;
                try {
                    JSONObject json = new JSONObject(s);
                    String usr_room = json.getString("username_room");
                    String rooms_tab = json.getString("id_rooms_tab");
                    dataJsonArr = json.getJSONArray("data");
                    for (int i = 0; i < dataJsonArr.length(); i++) {
                        JSONObject c = dataJsonArr.getJSONObject(i);
                        RoomsDetail orderModel = new RoomsDetail("", rooms_tab,  usr_room, dataJsonArr.getJSONObject(i).toString(), "", "", "value");
                        db.insertRoomsDetail(orderModel);
                        String title = c.getString("title");
                        String content_note = c.getString("description");
                        String image = c.getString("url_tembak");
                        String create_at = c.getString("created_at");
                        NewsFeedItem newsFeedItem = new NewsFeedItem();

                        newsFeedItem.setMyuserid(username);
                        newsFeedItem.setIdRoomTab(idRoomTab);
                        newsFeedItem.setTitle(title);
                        newsFeedItem.setStatus(content_note);
                        newsFeedItem.setImage(image);
                        newsFeedItem.setTimeStamp(create_at);

                        feedItems.add(newsFeedItem);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyItemInserted(feedItems.size());
                            }
                        });
                    }
                    adapter.notifyDataSetChanged();
                    adapter.setLoaded();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}



