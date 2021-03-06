package com.byonchat.android.widget;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;

import com.byonchat.android.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.blackbox_vision.wheelview.view.WheelView;

public class CalendarDialog extends Dialog implements View.OnClickListener {

    Activity activity;
    Dialog dialog;
    MyDialogListener listener;
    WheelView wheelViewHour, wheelViewMinutes;
    MaterialCalendarView calendarView1;
    Button butCancel, butSubmit;
    String today;

    public CalendarDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.calendar_layout);

        initViews();
    }

    private void initViews() {
        wheelViewHour = (WheelView) findViewById(R.id.hour);
        wheelViewMinutes = (WheelView) findViewById(R.id.minutes);
        calendarView1 = (MaterialCalendarView) findViewById(R.id.calendarView1);
        butCancel = (Button) findViewById(R.id.btn_cancel);
        butSubmit = (Button) findViewById(R.id.btn_set);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Date c = Calendar.getInstance().getTime();
        today = format.format(c);

        calendarView1.state().edit()
                .setFirstDayOfWeek(Calendar.MONDAY)
                .setMinimumDate(c)
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        calendarView1.setDateSelected(c, true);
        calendarView1.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                today = String.valueOf(date.getYear()) + "-" + String.format("%02d", date.getMonth() + 1) + "-" + String.format("%02d", date.getDay());
            }
        });


        SimpleDateFormat hour = new SimpleDateFormat("HH");
        SimpleDateFormat minute = new SimpleDateFormat("mm");
        String currentHour = hour.format(new Date());
        String currentMinute = minute.format(new Date());

        //Time Hour
        wheelViewHour.setInitPosition(Integer.valueOf(currentHour));
        wheelViewHour.setCanLoop(false);
        wheelViewHour.setLoopListener(item -> {
        });
        wheelViewHour.setTextSize(25);
        wheelViewHour.setItems(getList());
        //Time Minutes
        wheelViewMinutes.setInitPosition(Integer.valueOf(currentMinute));
        wheelViewMinutes.setCanLoop(false);
        wheelViewMinutes.setLoopListener(item -> {
        });
        wheelViewMinutes.setTextSize(25);
        wheelViewMinutes.setItems(getListDua());

        butCancel.setOnClickListener(this);
        butSubmit.setOnClickListener(this);
    }

    private List getList() {
        ArrayList<String> list = new ArrayList<>();

        for (int i = 0; i < 24; i++) {
            if (i < 10) {
                list.add("0" + i);
            } else {
                list.add(i + "");
            }

        }

        return list;
    }

    private List getListDua() {
        ArrayList<String> list = new ArrayList<>();

        for (int i = 0; i < 60; i++) {
            if (i < 10) {
                list.add("0" + i);
            } else {
                list.add(i + "");
            }
        }

        return list;
    }

    @Override
    public void onClick(View v) {
        if (v == butCancel) {
            listener.userCanceled();
            dismiss();
        }
        if (v == butSubmit) {
            String jam = "0";
            String menit = "0";
            if (wheelViewHour.getSelectedItem() < 10) {
                jam = jam + wheelViewHour.getSelectedItem();
            } else {
                jam = wheelViewHour.getSelectedItem() + "";
            }

            if (wheelViewMinutes.getSelectedItem() < 10) {
                menit = menit + wheelViewMinutes.getSelectedItem();
            } else {
                menit = wheelViewMinutes.getSelectedItem() + "";
            }

            String value = today + " " + jam + ":" + menit + ":" + "00";
            listener.userSelectedAValue(value);
            dismiss();
        }
    }

    public MyDialogListener getListener() {
        return listener;
    }

    public void setListener(MyDialogListener listener) {
        this.listener = listener;
    }

    public static interface MyDialogListener {
        public void userSelectedAValue(String value);

        public void userCanceled();
    }
}

/*


package com.byonchat.android.widget;

        import android.app.Activity;
        import android.app.Dialog;
        import android.os.Bundle;
        import android.view.View;
        import android.view.Window;
        import android.widget.Button;
        import android.widget.CalendarView;
        import android.widget.DatePicker;

        import com.byonchat.android.R;

        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.Date;
        import java.util.List;

        import io.blackbox_vision.wheelview.view.WheelView;

public class CalendarDialog extends Dialog implements View.OnClickListener {

    Activity activity;
    Dialog dialog;
    MyDialogListener listener;
    WheelView wheelViewHour, wheelViewMinutes;
    DatePicker calendarView1;
    Button butCancel, butSubmit;

    public CalendarDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.calendar_layout);

        initViews();
    }

    private void initViews() {
        wheelViewHour = (WheelView) findViewById(R.id.hour);
        wheelViewMinutes = (WheelView) findViewById(R.id.minutes);
        calendarView1 = (DatePicker) findViewById(R.id.calendarView1);
        butCancel = (Button) findViewById(R.id.btn_cancel);
        butSubmit = (Button) findViewById(R.id.btn_set);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        calendarView1.setSpinnersShown(false);

        final Calendar c = Calendar.getInstance();
        calendarView1.setMinDate(c.getTimeInMillis());


      */
/*  today = format.format(calendarView1.getDate());
        calendarView1.setMinDate(calendarView1.getDate());
        calendarView1.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                today = String.valueOf(year) + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth);
            }
        });*//*


        SimpleDateFormat hour = new SimpleDateFormat("HH");
        SimpleDateFormat minute = new SimpleDateFormat("mm");
        String currentHour = hour.format(new Date());
        String currentMinute = minute.format(new Date());

        //Time Hour
        wheelViewHour.setInitPosition(Integer.valueOf(currentHour));
        wheelViewHour.setCanLoop(false);
        wheelViewHour.setLoopListener(item -> {
        });
        wheelViewHour.setTextSize(25);
        wheelViewHour.setItems(getList());
        //Time Minutes
        wheelViewMinutes.setInitPosition(Integer.valueOf(currentMinute));
        wheelViewMinutes.setCanLoop(false);
        wheelViewMinutes.setLoopListener(item -> {
        });
        wheelViewMinutes.setTextSize(25);
        wheelViewMinutes.setItems(getListDua());

        butCancel.setOnClickListener(this);
        butSubmit.setOnClickListener(this);
    }

    private List getList() {
        ArrayList<String> list = new ArrayList<>();

        for (int i = 0; i < 24; i++) {
            if (i < 10) {
                list.add("0" + i);
            } else {
                list.add(i + "");
            }

        }

        return list;
    }

    private List getListDua() {
        ArrayList<String> list = new ArrayList<>();

        for (int i = 0; i < 60; i++) {
            if (i < 10) {
                list.add("0" + i);
            } else {
                list.add(i + "");
            }
        }

        return list;
    }

    @Override
    public void onClick(View v) {
        if (v == butCancel) {
            listener.userCanceled();
            dismiss();
        }
        if (v == butSubmit) {
            String jam = "0";
            String menit = "0";
            if (wheelViewHour.getSelectedItem() < 10) {
                jam = jam + wheelViewHour.getSelectedItem();
            } else {
                jam = wheelViewHour.getSelectedItem() + "";
            }

            if (wheelViewMinutes.getSelectedItem() < 10) {
                menit = menit + wheelViewMinutes.getSelectedItem();
            } else {
                menit = wheelViewMinutes.getSelectedItem() + "";
            }

            String today = String.valueOf(calendarView1.getYear()) + "-" + String.format("%02d", calendarView1.getMonth() + 1) + "-" + String.format("%02d", calendarView1.getDayOfMonth());
            String value = today + " " + jam + ":" + menit + ":" + "00";
            listener.userSelectedAValue(value);
            dismiss();
        }
    }

    public MyDialogListener getListener() {
        return listener;
    }

    public void setListener(MyDialogListener listener) {
        this.listener = listener;
    }

    public static interface MyDialogListener {
        public void userSelectedAValue(String value);

        public void userCanceled();
    }
}

// awal dan akhir tidak boleh lebih*/
