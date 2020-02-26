package com.byonchat.android.FragmentDinamicRoom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.byonchat.android.R;
import com.byonchat.android.personalRoom.FragmentMyNews;
import com.byonchat.android.personalRoom.adapter.NewsFeedListAdapter;
import com.byonchat.android.personalRoom.model.POSFeedItem;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.RoomsDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukma on 3/4/2016.
 */
@SuppressLint("ValidFragment")
public class FragmentRoomPOS extends Fragment {


    private static final String TAG = FragmentMyNews.class.getSimpleName();
    private List<POSFeedItem> feedItems;

    private String myContact ;
    private String title ;
    private String urlTembak ;
    private String username ;
    private String idRoomTab ;
    private String color ;
    private Activity mContext ;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRecyclerView;
    private NewsFeedListAdapter adapter;

    BotListDB db;

    public FragmentRoomPOS(Activity ctx) {
        mContext = ctx;

    }

    // newInstance constructor for creating fragment with arguments
    public static FragmentRoomPOS newInstance(String myc, String tit, String utm, String usr, String idrtab, String color,Activity ctx) {
        FragmentRoomPOS fragmentRoomTask = new FragmentRoomPOS(ctx);
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
        View sss = inflater.inflate(R.layout.room_fragment_main_pos, container, false);
        /*NEW*/
        mRecyclerView = (RecyclerView) sss.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);

        return sss;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if (db == null) {
            db =  BotListDB.getInstance(mContext.getApplicationContext());
        }

        feedItems = new ArrayList<POSFeedItem>();
        ArrayList<RoomsDetail> allRoomDetailFormWithFlag = db.allRoomDetailFormWithFlag("",username,idRoomTab,"value");
        if(allRoomDetailFormWithFlag.size()>0){
          //  refresh(allRoomDetailFormWithFlag,false);
        }
    }




}