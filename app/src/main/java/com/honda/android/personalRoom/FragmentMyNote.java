package com.honda.android.personalRoom;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.honda.android.R;
import com.honda.android.communication.MessengerConnectionService;
import com.honda.android.communication.NetworkInternetConnectionStatus;
import com.honda.android.personalRoom.adapter.NoteFeedListAdapter;
import com.honda.android.personalRoom.adapter.NoteFeedListAdapterMine;
import com.honda.android.personalRoom.asynctask.ProfileSaveDescription;
import com.honda.android.personalRoom.model.NoteFeedItem;
import com.honda.android.personalRoom.utils.Level;
import com.honda.android.provider.BotListDB;
import com.honda.android.provider.MessengerDatabaseHelper;
import com.honda.android.provider.RoomsDetail;
import com.honda.android.utils.OnLoadMoreListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lukma on 3/4/2016.
 */

@SuppressLint("ValidFragment")
public class FragmentMyNote extends Fragment {

    private static final String TAG = FragmentMyNote.class.getSimpleName();
    private ArrayList<NoteFeedItem> feedItems;
    private String URL_SAVE_STATUS = "https://" + MessengerConnectionService.HTTP_SERVER2 + "/personal_room/webservice/proses/add_new_note.php";
    private String URL_LIST_VIEW_NOTE = "https://" + MessengerConnectionService.HTTP_SERVER2 + "/personal_room/webservice/view/my_note.php";

    private String myContact;
    private String title;
    private String urlTembak;
    private String username;
    private String idRoomTab;
    private String color;
    private Boolean personal;

    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mswipeRefreshLayout;
    private NoteFeedListAdapterMine adapterMine;
    private NoteFeedListAdapter adapter;
    private FrameLayout mWriteProfileCard;
    private FrameLayout cardView;
    private ImageView buttonCreate;
    private Activity mContext ;
    protected Handler handler = new Handler();
    private int pageNumber;
    private MessengerDatabaseHelper messengerHelper;
    private BotListDB botListDB;

    public FragmentMyNote(Activity ctx) {
        mContext = ctx;

    }


    public static FragmentMyNote newInstance(String myc, String tit, String utm, String usr, String idrtab, String color, Boolean flag,Activity ctc) {
        FragmentMyNote fragmentRoomTask = new FragmentMyNote(ctc);
        Bundle args = new Bundle();
        args.putString("aa", tit);
        args.putString("bb", utm);
        args.putString("cc", usr);
        args.putString("dd", idrtab);
        args.putString("ee", myc);
        args.putString("col", color);
        args.putBoolean("fla", flag);
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
        personal = getArguments().getBoolean("fla");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_note, container, false);

        mswipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.activity_main_swipe_refresh_layout);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.list);
        mWriteProfileCard = (FrameLayout) v.findViewById(R.id.WriteProfile);
        cardView = (FrameLayout) v.findViewById(R.id.cardview);
        buttonCreate = (ImageView) v.findViewById(R.id.buttonCreate);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (feedItems == null) {
            feedItems = new ArrayList<NoteFeedItem>();
        }

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pageNumber = 1;

        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(mContext.getApplicationContext());
        }

        if (botListDB == null) {
            botListDB = BotListDB.getInstance(mContext.getApplicationContext());
        }

        if (personal) {
            if (myContact.equalsIgnoreCase(messengerHelper.getMyContact().getJabberId())) {
                mWriteProfileCard.setVisibility(View.VISIBLE);
                cardView.setVisibility(View.VISIBLE);
            } else {
                mWriteProfileCard.setVisibility(View.GONE);
                cardView.setVisibility(View.GONE);
            }
        } else {
            mWriteProfileCard.setVisibility(View.GONE);
            cardView.setVisibility(View.GONE);
        }

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext.getApplicationContext(), NoteCreateStatusActivity.class);
                mContext.startActivity(i);
            }
        });

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext.getApplicationContext(), NoteCreateStatusActivity.class);
                mContext.startActivity(i);
            }
        });

        if (null == feedItems) {
            feedItems = new ArrayList<NoteFeedItem>();
        }

        if (personal) {
            if (myContact.equalsIgnoreCase(messengerHelper.getMyContact().getJabberId())) {
                adapterMine = new NoteFeedListAdapterMine(getActivity(), feedItems);
                mRecyclerView.setAdapter(adapterMine);
            } else {
                adapter = new NoteFeedListAdapter(mContext.getApplicationContext(), feedItems, mRecyclerView);
                mRecyclerView.setAdapter(adapter);
            }
        } else {
            adapter = new NoteFeedListAdapter(mContext.getApplicationContext(), feedItems, mRecyclerView);
            mRecyclerView.setAdapter(adapter);
        }

        mswipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (personal) {
                    if (myContact.equalsIgnoreCase(messengerHelper.getMyContact().getJabberId())) {
                        new getNotePersonalMine().execute(URL_LIST_VIEW_NOTE, myContact, messengerHelper.getMyContact().getJabberId());
                    } else {
                        new getNotePersonal().execute(URL_LIST_VIEW_NOTE, myContact, messengerHelper.getMyContact().getJabberId());
                    }
                } else {
                    new getNote(pageNumber).execute(urlTembak, myContact, username, idRoomTab);
                }
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                mswipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        if (!personal) {
            adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    feedItems.add(null);
                    adapter.notifyItemInserted(feedItems.size() - 1);
                    ++pageNumber;
                    new getNoteNext(pageNumber, mRecyclerView.getAdapter().getItemCount()).execute(urlTembak, myContact, username, idRoomTab);
                }
            });
        }
    }

    @Override
    public void onResume() {
        mswipeRefreshLayout.setRefreshing(true);
        if (personal) {
            if (myContact.equalsIgnoreCase(messengerHelper.getMyContact().getJabberId())) {
                new getNotePersonalMine().execute(URL_LIST_VIEW_NOTE, myContact, messengerHelper.getMyContact().getJabberId());
            } else {
                new getNotePersonal().execute(URL_LIST_VIEW_NOTE, myContact, messengerHelper.getMyContact().getJabberId());
            }
        } else {
            Boolean refresh = true;
            ArrayList<RoomsDetail> allRoomDetailFormWithFlag = botListDB.allRoomDetailFormWithFlag("", username, idRoomTab, "value");
            if (allRoomDetailFormWithFlag.size() > 0) {
                refresh(allRoomDetailFormWithFlag, true);
            } else {
                if (NetworkInternetConnectionStatus.getInstance(mContext.getApplicationContext()).isOnline(mContext)) {
                    if (adapter.getItemCount() == 0) {
                        new getNote(pageNumber).execute(urlTembak, myContact, username, idRoomTab);
                        refresh = false;
                    }
                } else {
                    Toast.makeText(mContext.getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
            if (refresh) {
                if (NetworkInternetConnectionStatus.getInstance(mContext.getApplicationContext()).isOnline(mContext.getApplicationContext())) {
                    new getNote(pageNumber).execute(urlTembak, myContact, username, idRoomTab);
                }
            }
        }
        mswipeRefreshLayout.setRefreshing(false);
        super.onResume();
    }

    void onItemsLoadComplete() {
        mswipeRefreshLayout.setRefreshing(false);
    }

    class getNotePersonalMine extends AsyncTask<String, Void, String> {
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
            data.put("userid", params[1]);
            data.put("bc_user", params[2]);
            String result = profileSaveDescription.sendPostRequest(params[0], data);
            return result;
        }

        protected void onPostExecute(String s) {
            JSONArray dataJsonArr = null;
            JSONArray commentNoteJsonArr = null;
            String data = "";
            onItemsLoadComplete();

            if (s.equals(null)) {
                Toast.makeText(mContext.getApplicationContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject json = new JSONObject(s);
                    String json_userid = json.getString("userid");
                    String profile_photo = json.getString("profile_photo");
                    String profile_name = json.getString("profile_name");

                    dataJsonArr = json.getJSONArray("data");
                    if (dataJsonArr.length() > 0) {
                        feedItems.clear();
                        for (int i = 0; i < dataJsonArr.length() + 1; i++) {

                            if (i == 0) {
                                feedItems.add(null);
                            } else {
                                JSONObject c = dataJsonArr.getJSONObject(i - 1);
                                String id_note = c.getString("id_note");
                                String content_note = c.getString("content_note");
                                String photo_file = c.getString("photo_file");
                                int amount_of_like = c.getInt("amount_of_like");
                                int amount_of_dislike = c.getInt("amount_of_dislike");
                                int amount_of_comment = c.getInt("amount_of_comment");
                                String userLike = c.getString("user_like");
                                String userDislike = c.getString("user_dislike");
                                String tgl_post = c.getString("tgl_post");

                                NoteFeedItem item = new NoteFeedItem();
                                item.setMyuserid(messengerHelper.getMyContact().getJabberId());
                                item.setUserid(myContact);
//                            item.setProfilePic("https://b.byonchat.com/personal_room/images/achievement/"+c.getString("photo_file"));
                                item.setProfilePic(profile_photo);
                                item.setName(profile_name);
                                item.setId(id_note);

                                // Image might be null sometimes
                                String image = c.isNull("photo_file") ? null : c.getString("photo_file");
                                item.setImage(image);
                                item.setStatus(Html.fromHtml(URLDecoder.decode(String.valueOf(Html.fromHtml(content_note.toString())))).toString());
                                item.setTimeStamp(tgl_post);

                                // url might be null sometimes
                                String feedUrl = c.isNull("photo_file") ? null : "https://" + MessengerConnectionService.HTTP_SERVER + "/personal_room/images/achievement/" + c
                                        .getString("photo_file");
                                item.setUrl(feedUrl);
                                String jLove = c.isNull("amount_of_like") ? null : c.getString("amount_of_like");
                                item.setJumlahLove(jLove);
                                String jDislike = c.isNull("amount_of_dislike") ? null : c.getString("amount_of_dislike");
                                item.setJumlahNix(jDislike);
                                String jComment = c.isNull("amount_of_comment") ? null : c.getString("amount_of_comment");
                                item.setJumlahComment(jComment);
                                item.setUserLike(userLike);
                                item.setUserDislike(userDislike);
                                item.setLevel(Level.LEVEL_ONE);

                                int jKomen = c.getJSONArray("comment_note").length();
                                if (c.getJSONArray("comment_note").length() > 0) {
                                    commentNoteJsonArr = c.getJSONArray("comment_note");
                                    for (int j = 0; j < commentNoteJsonArr.length(); j++) {
                                        JSONObject d = commentNoteJsonArr.getJSONObject(j);

                                        String pName2 = d.isNull("profile_name") ? null : d.getString("profile_name");
                                        item.setName2(pName2);
                                        String pComment2 = d.isNull("content_comment") ? null : d.getString("content_comment");
                                        item.setComment2(pComment2);
                                    }
                                }
                                item.setFlag(personal);
                                feedItems.add(item);
                            }
                        }
                    }
                    adapterMine = new NoteFeedListAdapterMine(mContext, feedItems);
                    mRecyclerView.setAdapter(adapterMine);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class getNotePersonal extends AsyncTask<String, Void, String> {
        ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
        String result = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("userid", params[1]);
            data.put("bc_user", params[2]);
            String result = profileSaveDescription.sendPostRequest(params[0], data);
            return result;
        }

        protected void onPostExecute(String s) {
            JSONArray dataJsonArr = null;
            JSONArray commentNoteJsonArr = null;
            String data = "";
            onItemsLoadComplete();

            if (s.equals(null)) {
                Toast.makeText(mContext.getApplicationContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject json = new JSONObject(s);
                    String json_userid = json.getString("userid");
                    String profile_photo = json.getString("profile_photo");
                    String profile_name = json.getString("profile_name");

                    dataJsonArr = json.getJSONArray("data");
                    if (dataJsonArr.length() > 0) {
                        feedItems.clear();
                        for (int i = 0; i < dataJsonArr.length(); i++) {

                            JSONObject c = dataJsonArr.getJSONObject(i);

                            String id_note = c.getString("id_note");
                            String content_note = c.getString("content_note");
                            String photo_file = c.getString("photo_file");
                            int amount_of_like = c.getInt("amount_of_like");
                            int amount_of_dislike = c.getInt("amount_of_dislike");
                            int amount_of_comment = c.getInt("amount_of_comment");
                            String userLike = c.getString("user_like");
                            String userDislike = c.getString("user_dislike");
                            String tgl_post = c.getString("tgl_post");

                            NoteFeedItem item = new NoteFeedItem();
                            item.setMyuserid(messengerHelper.getMyContact().getJabberId());
                            item.setUserid(myContact);
//                            item.setProfilePic("https://b.byonchat.com/personal_room/images/achievement/"+c.getString("photo_file"));
                            item.setProfilePic(profile_photo);
                            item.setName(profile_name);
                            item.setId(id_note);

                            // Image might be null sometimes
                            String image = c.isNull("photo_file") ? null : c.getString("photo_file");
                            item.setImage(image);
                            item.setStatus(Html.fromHtml(URLDecoder.decode(String.valueOf(Html.fromHtml(content_note.toString())))).toString());
                            item.setTimeStamp(tgl_post);

                            // url might be null sometimes
                            String feedUrl = c.isNull("photo_file") ? null : "https://" + MessengerConnectionService.HTTP_SERVER + "/personal_room/images/achievement/" + c
                                    .getString("photo_file");
                            item.setUrl(feedUrl);
                            String jLove = c.isNull("amount_of_like") ? null : c.getString("amount_of_like");
                            item.setJumlahLove(jLove);
                            String jDislike = c.isNull("amount_of_dislike") ? null : c.getString("amount_of_dislike");
                            item.setJumlahNix(jDislike);
                            String jComment = c.isNull("amount_of_comment") ? null : c.getString("amount_of_comment");
                            item.setJumlahComment(jComment);
                            item.setUserLike(userLike);
                            item.setUserDislike(userDislike);
                            item.setLevel(Level.LEVEL_ONE);

                            int jKomen = c.getJSONArray("comment_note").length();
                            if (c.getJSONArray("comment_note").length() > 0) {
                                commentNoteJsonArr = c.getJSONArray("comment_note");
                                for (int j = 0; j < commentNoteJsonArr.length(); j++) {
                                    JSONObject d = commentNoteJsonArr.getJSONObject(j);

                                    String pName2 = d.isNull("profile_name") ? null : d.getString("profile_name");
                                    item.setName2(pName2);
                                    String pComment2 = d.isNull("content_comment") ? null : d.getString("content_comment");
                                    item.setComment2(pComment2);
                                }
                            }
                            item.setFlag(personal);
                            feedItems.add(item);
                        }
                    }
                    adapterMine = new NoteFeedListAdapterMine(mContext, feedItems);
                    mRecyclerView.setAdapter(adapterMine);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class getNote extends AsyncTask<String, Void, String> {
        ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
        String result = "";
        int pageNumber;

        public getNote(int pageNumber) {
            super();
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
            onItemsLoadComplete();

            if (s.equals(null)) {
                Toast.makeText(getContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
            } else {
                if (pageNumber > 1) {
                    feedItems.remove(feedItems.size() - 1);
                    adapter.notifyItemRemoved(feedItems.size());
                }
                JSONArray dataJsonArr = null;
                try {
                    feedItems.clear();
                    JSONObject json = new JSONObject(s);
                    botListDB.deleteRoomsDetailPtabPRoom(idRoomTab, username);
                    dataJsonArr = json.getJSONArray("data");
                    for (int i = 0; i < dataJsonArr.length(); i++) {
                        JSONObject c = dataJsonArr.getJSONObject(i);
                        String id = c.getString("id_note");
                        RoomsDetail orderModel = new RoomsDetail(id, idRoomTab, username, dataJsonArr.getJSONObject(i).toString(), "", "", "value");
                        botListDB.insertRoomsDetail(orderModel);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ArrayList<RoomsDetail> allRoomDetailFormWithFlag = botListDB.allRoomDetailFormWithFlag("", username, idRoomTab, "value");
                if (allRoomDetailFormWithFlag.size() > 0) {
                    refresh(allRoomDetailFormWithFlag, true);
                }
            }

        }
    }

    public void refresh(ArrayList<RoomsDetail> s, boolean refresh) {
        if (refresh) {
            feedItems = new ArrayList<>();
        }
        Cursor cur = botListDB.getSingleRoom(username);

        JSONArray commentNoteJsonArr = null;
        try {

            for (RoomsDetail ss : s) {

                JSONObject c = new JSONObject(ss.getContent());
                String id_note = c.getString("id_note");
                String content_note = c.getString("content_note");
                String photo_file = c.getString("photo_file");
                int amount_of_like = c.getInt("amount_of_like");
                int amount_of_dislike = c.getInt("amount_of_dislike");
                int amount_of_comment = c.getInt("amount_of_comment");
                String tgl_post = c.getString("tgl_post");
                String userLike = c.getString("user_like");
                String userDislike = c.getString("user_dislike");

                NoteFeedItem item = new NoteFeedItem();
                item.setMyuserid(myContact);
                item.setUserid(username);
                String profile_name = "";
                String profile_photo = "";

                if (cur.getCount() > 0) {
                    profile_name = cur.getString(cur.getColumnIndex(BotListDB.ROOM_REALNAME));
                    profile_photo = cur.getString(cur.getColumnIndex(BotListDB.ROOM_ICON));
                }

                item.setProfilePic(profile_photo);
                item.setName(profile_name);
                item.setId(id_note);
                item.setIdRoomTab(idRoomTab);

                String image = c.isNull("photo_file") ? null : c.getString("photo_file");
                item.setImage(image);
                item.setStatus(content_note);
                item.setTimeStamp(tgl_post);

                // url might be null sometimes
                String feedUrl = c.isNull("photo_file") ? null : c.getString("photo_file");
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
                item.setFlag(false);

                if (c.getJSONArray("comment_note").length() > 0) {
                    commentNoteJsonArr = c.getJSONArray("comment_note");
                    for (int j = 0; j < commentNoteJsonArr.length(); j++) {
                        JSONObject d = commentNoteJsonArr.getJSONObject(j);
                        String pName2 = d.isNull("profile_name") ? null : d.getString("profile_name");
                        String pId2 = d.isNull("userid") ? null : d.getString("userid");
                        String pComment2 = d.isNull("content_comment") ? null : d.getString("content_comment");
                        item.setName2(pName2 != null ? pName2 : pId2);
                        item.setComment2(pComment2);
                    }
                }

                feedItems.add(item);
            }

            adapter = new NoteFeedListAdapter(mContext);
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
            onItemsLoadComplete();

            if (s.equals(null)) {
                Toast.makeText(getContext(), "Internet Problem.", Toast.LENGTH_SHORT).show();
            } else {
                if (pageNumber > 1) {
                    feedItems.remove(feedItems.size() - 1);
                    adapter.notifyItemRemoved(feedItems.size());
                }
                JSONArray commentNoteJsonArr = null;
                JSONArray dataJsonArr = null;
                Cursor cur = botListDB.getSingleRoom(username);
                try {

                    JSONObject json = new JSONObject(s);
                    //db.deleteRoomsDetailPtabPRoom(idRoomTab, username);
                    dataJsonArr = json.getJSONArray("data");
                    if (dataJsonArr != null) {
                        for (int i = 0; i < dataJsonArr.length(); i++) {
                            JSONObject c = dataJsonArr.getJSONObject(i);
                            String id = c.getString("id_note");
                            RoomsDetail orderModel = new RoomsDetail(id, idRoomTab, username, dataJsonArr.getJSONObject(i).toString(), "", "", "value");
                            botListDB.insertRoomsDetail(orderModel);
                            String id_note = c.getString("id_note");
                            String content_note = c.getString("content_note");
                            String photo_file = c.getString("photo_file");
                            int amount_of_like = c.getInt("amount_of_like");
                            int amount_of_dislike = c.getInt("amount_of_dislike");
                            int amount_of_comment = c.getInt("amount_of_comment");
                            String tgl_post = c.getString("tgl_post");
                            String userLike = c.getString("user_like");
                            String userDislike = c.getString("user_dislike");

                            NoteFeedItem item = new NoteFeedItem();
                            item.setMyuserid(myContact);
                            item.setUserid(username);
                            String profile_name = "";
                            String profile_photo = "";

                            if (cur.getCount() > 0) {
                                profile_name = cur.getString(cur.getColumnIndex(BotListDB.ROOM_REALNAME));
                                profile_photo = cur.getString(cur.getColumnIndex(BotListDB.ROOM_ICON));
                            }

                            item.setProfilePic(profile_photo);
                            item.setName(profile_name);
                            item.setId(id_note);
                            item.setIdRoomTab(idRoomTab);

                            String image = c.isNull("photo_file") ? null : c.getString("photo_file");
                            item.setImage(image);
                            item.setStatus(content_note);
                            item.setTimeStamp(tgl_post);

                            // url might be null sometimes
                            String feedUrl = c.isNull("photo_file") ? null : c.getString("photo_file");
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
                                    String pId2 = d.isNull("userid") ? null : d.getString("userid");
                                    String pComment2 = d.isNull("content_comment") ? null : d.getString("content_comment");
                                    item.setName2(pName2 != null ? pName2 : pId2);
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

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}