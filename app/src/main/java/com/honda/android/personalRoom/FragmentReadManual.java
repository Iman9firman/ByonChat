package com.honda.android.personalRoom;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.honda.android.R;
import com.honda.android.personalRoom.adapter.ReadManualListAdapter;
import com.honda.android.provider.BotListDB;
import com.honda.android.provider.ContentRoom;
import com.honda.android.provider.RoomsDetail;
import com.github.ybq.endless.Endless;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lukma on 3/4/2016.
 */
public class FragmentReadManual extends Fragment {

    private static final String TAG = FragmentReadManual.class.getSimpleName();
    private ArrayList<ContentRoom> feedItems;

    private String myContact ;
    private String title ;
    private String urlTembak ;
    private String username ;
    private String idRoomTab ;
    private String color ;
    private Endless endless;
    RecyclerView listView;
    private SwipeRefreshLayout mswipeRefreshLayout;
    private ReadManualListAdapter adapter;

    protected Handler handler = new Handler();
    private int pageNumber;
    private Activity activity;
    private View loadingView;

    BotListDB db;

    // newInstance constructor for creating fragment with arguments
    public static FragmentReadManual newInstance(String myc, String tit, String utm, String usr, String idrtab, String color) {
        FragmentReadManual fragmentRoomTask = new FragmentReadManual();
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
        View sss = inflater.inflate(R.layout.fragment_read_manual, container, false);
        /*NEW*/
        mswipeRefreshLayout = (SwipeRefreshLayout) sss.findViewById(R.id.activity_main_swipe_refresh_layout);
        listView = (RecyclerView)   sss.findViewById(R.id.listview);
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));

        loadingView= inflater.inflate(R.layout.load_more_footer, null, false);

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            listView.setNestedScrollingEnabled(true);
        }*/
        endless = Endless.applyTo(listView,
                loadingView);
        endless.setLoadMoreListener(new Endless.LoadMoreListener() {
            @Override
            public void onLoadMore(int page) {
                ArrayList<RoomsDetail> allRoomDetailFormWithFlag = db.allRoomDetailFormWithFlag("", username, idRoomTab, "value");
                refresh(allRoomDetailFormWithFlag, false);
            }
        });
        return sss;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        ArrayList<RoomsDetail> allRoomDetailFormWithFlag = db.allRoomDetailFormWithFlag("", username, idRoomTab, "value");
        refresh(allRoomDetailFormWithFlag, true);
    }

    public void refresh(ArrayList<RoomsDetail>  s, boolean refresh){
        try {

            if (refresh){
                feedItems  = new ArrayList<>();
            }else{
                endless.loadMoreComplete();
            }
            JSONArray commentNoteJsonArr = null;

            for (RoomsDetail ss : s) {

                JSONObject c = new JSONObject(ss.getContent());
                String titlr = c.getString("title");
                String id_note = c.getString("id_note");
                String content_note = c.getString("content_note");
                String photo_file = c.getString("photo_file");
                int amount_of_like = c.getInt("amount_of_like");
                int amount_of_dislike = c.getInt("amount_of_dislike");
                int amount_of_comment = c.getInt("amount_of_comment");
                String tgl_post = c.getString("tgl_post");
                String userLike = c.getString("user_like");
                String userDislike = c.getString("user_dislike");

                ContentRoom item = new ContentRoom();
                item.setTitle(myContact);
                item.setStatus(username);
                item.setTitle(titlr);
                item.setStatus(id_note);
                feedItems.add(item);

            }
            adapter = new ReadManualListAdapter( feedItems);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

}