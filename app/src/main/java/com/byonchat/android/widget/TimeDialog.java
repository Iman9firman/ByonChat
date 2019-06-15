package com.byonchat.android.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.byonchat.android.R;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.blackbox_vision.wheelview.view.WheelView;

public class
TimeDialog extends Dialog implements View.OnClickListener {

    WheelView wheelViewHour, wheelViewMinutes;
    Button butCancel, butSubmit;
    TextView textTitle;
    MyTimeDialogListener listener;
    String title;

    Context context;


    public TimeDialog(Activity activity, String title) {
        super(activity);
        this.title = title;
        context = activity;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.timedialog_layout);

        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.80);
        int height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.45);
        getWindow().setLayout(width, height);

        textTitle = (TextView) findViewById(R.id.tv_picktime_td);
        wheelViewHour = (WheelView) findViewById(R.id.hour_td);
        wheelViewMinutes = (WheelView) findViewById(R.id.minutes_td);
        butCancel = (Button) findViewById(R.id.btn_cancel_td);
        butSubmit = (Button) findViewById(R.id.btn_set_td);

        if (title != null) {
            textTitle.setText(Html.fromHtml(title));
        }
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

            String value = jam + ":" + menit;
            listener.userSelectedAValue(value);
            dismiss();
        }
    }

    public static interface MyTimeDialogListener {
        public void userSelectedAValue(String value);

        public void userCanceled();
    }

    public MyTimeDialogListener getListener() {
        return listener;
    }

    public void setListener(MyTimeDialogListener listener) {
        this.listener = listener;
    }
}
