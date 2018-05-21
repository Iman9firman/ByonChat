package com.byonchat.android.FragmentDinamicRoom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.ByonChatMainRoomActivity;
import com.byonchat.android.ConversationActivity;
import com.byonchat.android.R;
import com.byonchat.android.personalRoom.FragmentMyNews;
import com.byonchat.android.personalRoom.adapter.NewsFeedListAdapter;
import com.byonchat.android.personalRoom.model.NewsFeedItem;
import com.byonchat.android.personalRoom.model.POSFeedItem;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.RoomsDetail;
import com.imagpay.BTCommands;
import com.imagpay.BTPrinter;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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