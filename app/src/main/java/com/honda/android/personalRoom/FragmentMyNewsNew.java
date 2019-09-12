package com.honda.android.personalRoom;


import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.honda.android.personalRoom.adapter.NewsFeedListAdapter;
import com.honda.android.personalRoom.adapter.NewsFeedListAdapterNew;
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
public class FragmentMyNewsNew extends Fragment {

    private static final String TAG = FragmentMyNewsNew.class.getSimpleName();
    private List<NewsFeedItem> feedItems;

    private String myContact;
    private String title;
    private String urlTembak;
    private String username;
    private String idRoomTab;
    private String color;

    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mswipeRefreshLayout;
    private NewsFeedListAdapterNew adapter;

    BotListDB db;
    protected Handler handler = new Handler();
    private int pageNumber;
    private Activity mContext;


    public FragmentMyNewsNew(Activity ctx) {
        mContext = ctx;

    }

    // newInstance constructor for creating fragment with arguments
    public static FragmentMyNewsNew newInstance(String myc, String tit, String utm, String usr, String idrtab, String color, Activity ctcx) {
        FragmentMyNewsNew fragmentRoomTask = new FragmentMyNewsNew(ctcx);
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
        View sss = inflater.inflate(R.layout.fragment_my_news, container, false);
        /*NEW*/
        mswipeRefreshLayout = (SwipeRefreshLayout) sss.findViewById(R.id.activity_main_swipe_refresh_layout);
        mRecyclerView = (RecyclerView) sss.findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        return sss;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pageNumber = 1;

        if (db == null) {
            db = BotListDB.getInstance(mContext.getApplicationContext());
        }

        if (null == feedItems) {
            feedItems = new ArrayList<NewsFeedItem>();
        }

        adapter = new NewsFeedListAdapterNew(mContext, feedItems, mRecyclerView);
        mRecyclerView.setAdapter(adapter);

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                feedItems.add(null);
                adapter.notifyItemInserted(feedItems.size() - 1);
                ++pageNumber;

                new getNoteNext(pageNumber, mRecyclerView.getAdapter().getItemCount()).execute(urlTembak, myContact, username, idRoomTab);
            }
        });

        mswipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });
    }


    @Override
    public void onResume() {
        Boolean refresh = true;
        ArrayList<RoomsDetail> allRoomDetailFormWithFlag = db.allRoomDetailFormWithFlag("", username, idRoomTab, "value");
        if (allRoomDetailFormWithFlag.size() > 0) {
            refresh(allRoomDetailFormWithFlag, true);
        } else {
            if (NetworkInternetConnectionStatus.getInstance(mContext.getApplicationContext()).isOnline(mContext)) {
                if (adapter.getItemCount() == 0) {
                    Log.w("urltembak", urlTembak);
                    new getNote(pageNumber).execute(urlTembak, myContact, username, idRoomTab);
                    refresh = false;
                }
            } else {
                Toast.makeText(mContext.getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }
        if (refresh) {
            if (NetworkInternetConnectionStatus.getInstance(mContext.getApplicationContext()).isOnline(mContext.getApplicationContext())) {
                Log.w("urltembak", urlTembak);
                new getNote(pageNumber).execute(urlTembak, myContact, username, idRoomTab);
            }
        }

        super.onResume();


    }

    void refreshItems() {
        if (NetworkInternetConnectionStatus.getInstance(mContext.getApplicationContext()).isOnline(mContext.getApplicationContext())) {
            Log.w("urltembak", urlTembak);
            new getNote(pageNumber).execute(urlTembak, myContact, username, idRoomTab);
        } else {
            Toast.makeText(mContext.getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            ArrayList<RoomsDetail> allRoomDetailFormWithFlag = db.allRoomDetailFormWithFlag("", username, idRoomTab, "value");
            if (allRoomDetailFormWithFlag.size() > 0) {
                refresh(allRoomDetailFormWithFlag, false);
                onItemsLoadComplete();
            } else {
                if (NetworkInternetConnectionStatus.getInstance(mContext.getApplicationContext()).isOnline(mContext.getApplicationContext())) {
                    Log.w("urltembak", urlTembak);
                    new getNote(pageNumber).execute(urlTembak, myContact, username, idRoomTab);
                } else {
                    Toast.makeText(mContext.getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    void onItemsLoadComplete() {
        mswipeRefreshLayout.setRefreshing(false);
    }


    class getNote extends AsyncTask<String, Void, String> {
        ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
        String result = "";
        InputStream inputStream = null;
        int pageNumber;

        public getNote(int pageNumber) {
            super();
            this.pageNumber = pageNumber;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mswipeRefreshLayout.setRefreshing(true);
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
            Log.w("disinikayanya", s.toString());
            onItemsLoadComplete();
            if (s.equals(null)) {
                Toast.makeText(mContext, "Internet Problem.", Toast.LENGTH_SHORT).show();
            } else {
                if (pageNumber > 1) {
                    feedItems.remove(feedItems.size() - 1);
                    adapter.notifyItemRemoved(feedItems.size());
                }
                JSONArray dataJsonArr = null;
                try {
                    feedItems.clear();
                    Log.w("sasd", s);
                    JSONObject json = new JSONObject(s);
                    db.deleteRoomsDetailPtabPRoom(idRoomTab, username);
                    dataJsonArr = json.getJSONArray("data");
                    for (int i = 0; i < dataJsonArr.length(); i++) {
                        JSONObject c = dataJsonArr.getJSONObject(i);
                        String id = c.getString("attachment_id");
                        RoomsDetail orderModel = new RoomsDetail(id, idRoomTab, username, dataJsonArr.getJSONObject(i).toString(), "", "", "value");
                        db.insertRoomsDetail(orderModel);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ArrayList<RoomsDetail> allRoomDetailFormWithFlag = db.allRoomDetailFormWithFlag("", username, idRoomTab, "value");
                if (allRoomDetailFormWithFlag.size() > 0) {
                    refresh(allRoomDetailFormWithFlag, true);
                }
            }

        }
    }

    //{"username_room":"1_44103259admin","id_rooms_tab":"2272","data":[{"attachment_id":"9","title":"Attachment","attachment":"https:\/\/bb.byonchat.com\/bc_voucher_client\/public\/list_attachment\/DOC_19072018_182705_QPjNX01XOp.pdf","content":"<p>Tes isi content attachment<\/p>\r\n","photo_file":"https:\/\/bb.byonchat.com\/bc_voucher_client\/public\/list_attachment\/format_file","amount_of_comment":0,"comment_note":[],"tgl_post":"2018-07-19 18:27:05"}]}
    public void refresh(ArrayList<RoomsDetail> s, boolean refresh) {
        try {

            if (refresh) {
                feedItems = new ArrayList<>();
            }
            JSONArray commentNoteJsonArr = null;

            for (RoomsDetail ss : s) {
                Log.w("disinikayanya", ss.getContent().toString());

                JSONObject c = new JSONObject(ss.getContent());
                String titlr = c.getString("title");
                String id_note = c.getString("attachment_id");
                String task_id = c.getString("task_id");
                String content_note = c.getString("content");
                String photo_file = c.getString("photo_file");
                String attachment = c.getString("attachment");
                int amount_of_comment = c.getInt("amount_of_comment");
                String tgl_post = c.getString("tgl_post");
                String api_url = c.getString("api_url");

                NewsFeedItem item = new NewsFeedItem();
                item.setMyuserid(myContact);
                item.setUserid(username);
                item.setTitle(titlr);
                item.setId(id_note);
                item.setTaskid(task_id);
                item.setIdRoomTab(idRoomTab);
                item.setProfilePic(photo_file);
                item.setApi_url(api_url);

                String image = c.isNull("photo_file") ? null : c.getString("photo_file");
                item.setImge(image);
                item.setStatus(content_note);
                item.setTimeStamp(tgl_post);

                // url might be null sometimes
                item.setUrl(attachment);
                item.setJumlahLove("asdasd");
                item.setJumlahNix("asdasd");
                item.setJumlahComment(amount_of_comment + "");
                item.setUserLike("asdasd");
                item.setUserDislike("asdasd");
                item.setColorHeader(color);

                if (c.getJSONArray("comment_note").length() > 0) {
                    commentNoteJsonArr = c.getJSONArray("comment_note");
                    for (int j = 0; j < commentNoteJsonArr.length(); j++) {
                        JSONObject d = commentNoteJsonArr.getJSONObject(j);
                        String pName2 = d.isNull("profile_name") ? null : d.getString("profile_name");
                        String pComment2 = d.isNull("content_comment") ? null : d.getString("content_comment");
                        item.setName2(pName2);
                        item.setComment2(pComment2);
                    }
                }

                feedItems.add(item);

            }

            adapter = new NewsFeedListAdapterNew(mContext);
            adapter.setData(feedItems);
            adapter.setLoaded();
            mRecyclerView.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class getNoteNext extends AsyncTask<String, Void, String> {
        ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
        String result = "";
        InputStream inputStream = null;
        int pageNumber;
        int totalItem;

        public getNoteNext(int pageNumber, int totalItem) {
            super();
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
            data.put("last_page", totalItem + "");
            String result = profileSaveDescription.sendPostRequest(params[0], data);
            return result;
        }

        protected void onPostExecute(String s) {
            Log.w("disinikayanya", s);
            if (s.equals(null)) {
                Toast.makeText(mContext, "Internet Problem.", Toast.LENGTH_SHORT).show();
            } else {

                if (pageNumber > 1) {
                    feedItems.remove(feedItems.size() - 1);
                    adapter.notifyItemRemoved(feedItems.size());
                }

                JSONArray dataJsonArr = null;
                JSONArray commentNoteJsonArr = null;
                try {
                    JSONObject json = new JSONObject(s);
                    String json_userId = json.getString("id_rooms_tab");
                    String json_userRoom = json.getString("username_room");
                    dataJsonArr = json.getJSONArray("data");

                    if (dataJsonArr != null) {

                        for (int i = 0; i < dataJsonArr.length(); i++) {
                            JSONObject c = dataJsonArr.getJSONObject(i);
                            String id = c.getString("id_note");
                            RoomsDetail orderModel = new RoomsDetail(id, json_userId, json_userRoom, dataJsonArr.getJSONObject(i).toString(), "", "", "value");
                            db.insertRoomsDetail(orderModel);
                            String titlr = c.getString("title");
                            String id_note = c.getString("id_note");
                            String task_id = "";
                            if (c.has("task_id")) {
                                task_id = c.getString("task_id");
                            }
                            String content_note = c.getString("content_note");
                            String photo_file = c.getString("photo_file");
                            int amount_of_like = c.getInt("amount_of_like");
                            int amount_of_dislike = c.getInt("amount_of_dislike");
                            int amount_of_comment = c.getInt("amount_of_comment");
                            String tgl_post = c.getString("tgl_post");
                            String userLike = c.getString("user_like");
                            String userDislike = c.getString("user_dislike");
                            String api_url = c.getString("api_url");

                            NewsFeedItem item = new NewsFeedItem();
                            item.setMyuserid(myContact);
                            item.setUserid(username);
                            item.setTitle(titlr);
                            item.setId(id_note);
                            item.setTaskid(task_id);
                            item.setIdRoomTab(idRoomTab);
                            item.setProfilePic(photo_file);
                            item.setApi_url(api_url);

                            String image = c.isNull("photo_file") ? null : c.getString("photo_file");
                            item.setImge(image);
                            item.setStatus(content_note);
                            item.setTimeStamp(tgl_post);

                            // url might be null sometimes
                            final String feedUrl = c.isNull("photo_file") ? null : c.getString("photo_file");
                            item.setUrl(feedUrl);
                            String jLove = c.isNull("amount_of_like") ? null : c.getString("amount_of_like");
                            item.setJumlahLove(jLove);
                            String jDislike = c.isNull("amount_of_dislike") ? null : c.getString("amount_of_dislike");
                            item.setJumlahNix(jDislike);
                            String jComment = c.isNull("amount_of_comment") ? null : c.getString("amount_of_comment");
                            item.setJumlahComment(jComment);
                            item.setUserLike(userLike);
                            item.setUserDislike(userDislike);
                            item.setColorHeader(color);

                            if (c.getJSONArray("comment_note").length() > 0) {
                                commentNoteJsonArr = c.getJSONArray("comment_note");
                                for (int j = 0; j < commentNoteJsonArr.length(); j++) {
                                    JSONObject d = commentNoteJsonArr.getJSONObject(j);
                                    String pName2 = d.isNull("profile_name") ? null : d.getString("profile_name");
                                    String pComment2 = d.isNull("content_comment") ? null : d.getString("content_comment");
                                    String pTanggalComment = d.isNull("tgl_comment") ? null : d.getString("tgl_comment");
                                    int pAmount_of_like = c.getInt("amount_of_like");
                                    int pAmount_of_dislike = c.getInt("amount_of_dislike");
                                    int pAmount_of_comment = c.getInt("amount_of_comment");
                                    item.setName2(pName2);
                                    item.setComment2(pComment2);
                                }
                            }

                            feedItems.add(item);

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyItemInserted(feedItems.size());
                                }
                            });
                        }
                        adapter.notifyDataSetChanged();
                        adapter.setLoaded();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onActionSearch(String args) {
        adapter.getFilter().filter(args);
    }

}