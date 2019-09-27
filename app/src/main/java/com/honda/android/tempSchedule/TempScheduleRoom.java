package com.honda.android.tempSchedule;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.honda.android.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

@SuppressLint("ValidFragment")
public class TempScheduleRoom extends Fragment {

    public static final String RESULT = "result";
    public static final String EVENT = "event";
    private static final int ADD_NOTE = 44;
    CalendarView calendarView;
    private List<EventDay> mEventDays;
    MyEventDatabase eventDatabase;
    SQLiteDatabase db;
    List<String> date;

    private Activity mContext;
    String linkTembak;
    String username;
    String color;
    String latLong;
    String idTab;
    String title;
    String from;

    public TempScheduleRoom(Activity ctx) {
        mContext = ctx;
    }

    public static TempScheduleRoom newInstance(String tt, String cc, String usr, String idta, String col, String latLong, Activity aa, String from) {
        TempScheduleRoom fragmentTempSchedule = new TempScheduleRoom(aa);
        Bundle args = new Bundle();
        args.putString("tt", tt);
        args.putString("cc", cc);
        args.putString("uu", usr);
        args.putString("ii", idta);
        args.putString("col", col);
        args.putString("ll", latLong);
        args.putString("from", from);
        fragmentTempSchedule.setArguments(args);
        return fragmentTempSchedule;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getArguments().getString("tt");
        linkTembak = getArguments().getString("cc");
        username = getArguments().getString("uu");
        idTab = getArguments().getString("ii");
        color = getArguments().getString("col");
        latLong = getArguments().getString("ll");
        from = getArguments().getString("from");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_temp_schedule_room, container, false);
        setView(v);
        caledarMethod();
        return v;
    }

    @Override
    public void onResume() {
        caledarMethod();
        super.onResume();
    }

    private void setView(View v){
        calendarView = (CalendarView) v.findViewById(R.id.calendarView_schedule);
        eventDatabase = new MyEventDatabase(getContext());
    }
    private void caledarMethod(){
        try {
            mEventDays = new ArrayList<>();
            date = new ArrayList<>();
            db = eventDatabase.getReadableDatabase();
            String[] args = {idTab};
            Cursor c = db.rawQuery("SELECT startDate_event FROM event WHERE id_tab_event = ?",args);
            while (c.moveToNext()){
                date.add(c.getString(0));
            }
            c.close();
            db.close();

            for (int a = 0 ; a < date.size() ; a++){
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
                cal.setTime(f.parse(date.get(a)));
                mEventDays.add(new EventDay(cal,R.drawable.baseline_star_black_48));
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        calendarView.setEvents(mEventDays);
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                previewNote(eventDay);
            }
        });
    }
    private void previewNote(EventDay eventDay) {
        Date date = eventDay.getCalendar().getTime();
        SimpleDateFormat f = new SimpleDateFormat("E , dd MMM yyyy");
        SimpleDateFormat d = new SimpleDateFormat("dd-MM-yyyy");
        String titleBar = f.format(date);
        String startDate = d.format(date);

        Intent intent = new Intent(getContext(), NotePreviewActivity.class);
        intent.putExtra("TITLE",titleBar);
        intent.putExtra(EVENT, startDate);
        intent.putExtra("tt",title);
        intent.putExtra("cc",linkTembak);
        intent.putExtra("uu",username);
        intent.putExtra("ii",idTab);
        intent.putExtra("col",color);
        intent.putExtra("ll",latLong);
        intent.putExtra("from",from);
        startActivity(intent);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_NOTE && resultCode == RESULT_OK) {
            MyEventDay myEventDay = data.getParcelableExtra(RESULT);
            Log.w("sini", "onActivityResult: masuk"+myEventDay);
            calendarView.setDate(myEventDay.getCalendar());
            mEventDays.add(myEventDay);
            calendarView.setEvents(mEventDays);
        }
    }
}
