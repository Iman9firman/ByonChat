package com.byonchat.android.room;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.byonchat.android.ByonChatMainRoomActivity;
import com.byonchat.android.FragmentDinamicRoom.DinamicListTaskAdapter;
import com.byonchat.android.R;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.ContentRoom;
import com.byonchat.android.provider.ContentRoomDB;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

@SuppressLint("ValidFragment")
public class FragmentRoomTask extends Fragment {

    RecyclerView mRecyclerView;
    DinamicListTaskAdapter myadapter;
    Contact contact;
    Activity mContext;
    private MessengerDatabaseHelper dbhelper;
    ContentRoom contentRoom;
    ContentRoomDB contentRoomDB;
    BotListDB botListDB;
    ArrayList<ContentRoom> listItem;
    ArrayList<RoomsDetail> listItem2;

    String urtTembak;
    String username;
    String color;
    String latLong;
    String idTab;
    String title;

    public FragmentRoomTask() {
    }

    public FragmentRoomTask(Activity ctx) {
        mContext = ctx;

    }

    // newInstance constructor for creating fragment with arguments
    public static FragmentRoomTask newInstance(String tt, String cc, String usr, String idta, String col, String latLong, Activity ctx) {
        FragmentRoomTask fragmentRoomTask = new FragmentRoomTask(ctx);
        Bundle args = new Bundle();
        args.putString("tt", tt);
        args.putString("cc", cc);
        args.putString("uu", usr);
        args.putString("ii", idta);
        args.putString("co", col);
        args.putString("ll", latLong);
        fragmentRoomTask.setArguments(args);
        return fragmentRoomTask;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getArguments().getString("tt");
        urtTembak = getArguments().getString("cc");
        username = getArguments().getString("uu");
        idTab = getArguments().getString("ii");
        color = getArguments().getString("col");
        latLong = getArguments().getString("ll");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View sss = inflater.inflate(R.layout.room_fragment_task, container, false);
        mRecyclerView = (RecyclerView) sss.findViewById(R.id.list);

        if (dbhelper == null) {
            dbhelper = MessengerDatabaseHelper.getInstance(mContext.getApplicationContext());
        }

        if (contentRoomDB == null) {
            contentRoomDB = new ContentRoomDB(mContext.getApplicationContext());
        }
        if (botListDB == null) {
            botListDB = BotListDB.getInstance(mContext.getApplicationContext());
        }

        return sss;
    }

    @Override
    public void onResume() {
        super.onResume();

        contact = dbhelper.getMyContact();
        listItem = new ArrayList<>();

        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(mContext.getApplicationContext());
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setNestedScrollingEnabled(false);

        listItem2 = botListDB.allRoomDetailFormWithFlag("", username, idTab, "parent");
        for (RoomsDetail aa : listItem2) {
            ArrayList<RoomsDetail> listItem3 = botListDB.allRoomDetailFormWithFlag(aa.getId(), username, idTab, "list");
            String title = "";
            String desc = "";
            String date = "";
            String status = "";

            for (RoomsDetail ii : listItem3) {
                if (ii.getFlag_content().equalsIgnoreCase("1")) {
                    title = ii.getContent();
                } else {
                    desc = ii.getContent();
                }
            }

            date = aa.getContent();
            status = aa.getFlag_content();
            String titLes = Message.parsedMessageText(JsonToStringKey(title));

            Log.w("2abubu", titLes);

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


        //Collections.sort(listItem, new FragmentRoomMultipleTask.Sortiran());

        if (myadapter != null) {
            myadapter = null;
        }

        myadapter = new DinamicListTaskAdapter(listItem, mContext.getApplicationContext());

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

        mRecyclerView.setAdapter(myadapter);

        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);
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

    public void onActionSearch(String args) {
        myadapter.getFilter().filter(args);
    }
}
