package com.byonchat.android.tempSchedule;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.byonchat.android.R;

public class NoteDetailDialog extends Dialog implements View.OnClickListener{

    Button butClose;
    Note note;
    Activity activity;

    public NoteDetailDialog(Activity activity , Note note){
        super(activity);
        this.activity = activity;
        this.note = note;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_detail_layout);

        initViews();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.but_ndd){
            dismiss();
        }
    }

    private void initViews(){

        TextView textStatus = (TextView) findViewById(R.id.tv_status_ndd);
        TextView textJobDesk = (TextView) findViewById(R.id.tv_jobdesk_ndd);
        TextView textTitle = (TextView) findViewById(R.id.tv_title_value_ndd);
        TextView textTime = (TextView) findViewById(R.id.tv_time_value_ndd);
        TextView textReason = (TextView) findViewById(R.id.tv_reason_value_ndd);
        TextView textKet = (TextView) findViewById(R.id.tv_ket_value_ndd);

        textStatus.setText(note.getStatus());
        Drawable mDrawableLetf = activity.getBaseContext().getResources().getDrawable(R.drawable.status_work);
        mDrawableLetf.setColorFilter(Color.parseColor(note.getWarna()), PorterDuff.Mode.SRC_ATOP);
        textStatus.setBackground(mDrawableLetf);

        textJobDesk.setText(note.getKeterangan());
        textTitle.setText(note.getTitle());
        textTime.setText(note.getStartTime()+" - "+note.getEndTime());
        textReason.setText(note.getAlasan());
        textKet.setText(note.getKet_status());

        butClose = (Button) findViewById(R.id.but_ndd);
        butClose.setOnClickListener(this);
    }
}
