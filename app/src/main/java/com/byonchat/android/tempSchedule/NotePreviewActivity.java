package com.byonchat.android.tempSchedule;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity;
import com.byonchat.android.FragmentDinamicRoom.RoomPOSdetail;
import com.byonchat.android.R;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.room.DividerItemDecoration;
import com.byonchat.android.utils.Validations;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NotePreviewActivity extends AppCompatActivity {

    RecyclerView recyclerView;
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
    ArrayList<RoomsDetail> listItem2 = new ArrayList<>();

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
        if (!checkDate(startDate)){
            fab.setVisibility(View.GONE);
        } else {
            fab.setVisibility(View.VISIBLE);
        }
        viewMethod();
        fabMethod();
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
                generateDB(startDate,id_detail,idTab);
                startActivity(intent);
            }
        });
    }
    private void viewMethod(){
        manager = new LinearLayoutManager(this);
        DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(),manager.getOrientation());
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(divider);

        adapter = new NotePreviewAdapter(NotePreviewActivity.this,getInside(),title,username,idTab,color,latLong,from,calendar,startDate);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        viewMethod();
        super.onResume();
    }
    private ArrayList<Note> getInside(){
        ArrayList<Note> noteList = new ArrayList<>();
        ArrayList<RoomsDetail> listItem3 = new ArrayList<>();

        //Database
        BotListDB botListDB = BotListDB.getInstance(getApplicationContext());
        eventDatabase = new MyEventDatabase(getApplicationContext());
        db = eventDatabase.getReadableDatabase();

//        listItem2 = botListDB.allRoomDetailFormWithFlag("", username, idTab, "parent");

        try {
            String[] args = {startDate , idTab};
            Cursor c = db.rawQuery("SELECT id_detail_event FROM event"+" WHERE startdate_event = ? AND id_tab_event = ? ",args);
            while (c.moveToNext()){
                String id_detail = c.getString(0);
                String title = null;
                String desc = null;
                String startTime = null;
                String endTime = null;

                listItem3 = botListDB.allRoomDetailFormWithFlag(id_detail, username, idTab, "cild");
                for (int j = 0 ; j <listItem3.size() ; j++){
                    String flag_tab = listItem3.get(j).getFlag_tab();
                    String content = listItem3.get(j).getContent();
                    if (flag_tab.equalsIgnoreCase("jam_mulai")){
                        startTime = content;
                    } else if (flag_tab.equalsIgnoreCase("jam_selesai")){
                        endTime = content;
                    } else if (flag_tab.equalsIgnoreCase("lokasi")){
                        title = content;
                    } else if (flag_tab.equalsIgnoreCase("keterangan")){
                        desc = content;
                    }
                }
                Note n = new Note(title,desc,startTime,endTime,id_detail);
                noteList.add(n);
            }
        } catch (Exception e){}

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
    private void generateDB(String date,String id_detail,String id_tab){
        eventDatabase = new MyEventDatabase(getApplicationContext());
        db = eventDatabase.getWritableDatabase();

        ContentValues v = new ContentValues();
        v.put(MyEventDatabase.EVENT_START_DATE,date);
        v.put(MyEventDatabase.EVENT_ID_DETAIL,id_detail);
        v.put(MyEventDatabase.EVENT_ID_TAB,id_tab);

        long id = db.insert(MyEventDatabase.TABLE_EVENT,null,v);
        db.close();
    }
}
