package com.honda.android.tempSchedule;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.honda.android.FragmentDinamicRoom.DinamicListTaskAdapter;
import com.honda.android.FragmentDinamicRoom.DinamicRoomTaskActivity;
import com.honda.android.R;
import com.honda.android.provider.BotListDB;
import com.honda.android.provider.RoomsDetail;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NotePreviewAdapter extends RecyclerView.Adapter<MyHolder> {

    Activity activity;
    private ArrayList<Note> noteList;
    private Context c;
    AlertDialog.Builder dialog;
    View dialogView;
    String status;

    //punya activity
    private String title;
    private String username;
    private String idTab;
    private String color;
    private String latLong;
    private String from;
    private String calendar;
    private String startDate;

    public NotePreviewAdapter (Activity activity , Context c, ArrayList<Note> noteList, String title, String username, String idTab, String color, String latLong, String from, String calendar, String startDate){
        this.activity = activity;
        this.c = c;
        this.noteList = noteList;
        this.title = title;
        this.username = username;
        this.idTab = idTab;
        this.color = color;
        this.latLong = latLong;
        this.from = from;
        this.calendar = calendar;
        this.startDate = startDate;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.note_layout,parent,false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int pos) {
        Note note = noteList.get(pos);

        holder.lokasi.setText(note.getTitle());
        holder.keterangan.setText(note.getKeterangan());
        holder.startTime.setText(note.getStartTime());
        holder.endTime.setText(note.getEndTime());
        holder.status.setText(note.getStatus());

        Drawable mDrawableLetf = c.getResources().getDrawable(R.drawable.status_work);
        mDrawableLetf.setColorFilter(Color.parseColor(note.getWarna()), PorterDuff.Mode.SRC_ATOP);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            holder.status.setBackground(mDrawableLetf);
        }


        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!note.isSubmit()){
                    if (!checkDate(startDate)){

//                        dialogEvent(note);
                        new NoteDetailDialog(activity,note).show();

                    } else {
                        Intent intent = new Intent(c, DinamicRoomTaskActivity.class);
                        intent.putExtra("tt", title);
                        intent.putExtra("uu", username);
                        intent.putExtra("ii", idTab);
                        intent.putExtra("col", color);
                        intent.putExtra("ll", latLong);
                        intent.putExtra("from", from);
                        intent.putExtra("idTask", note.getId_detail());
                        intent.putExtra("clndr",calendar);
                        intent.putExtra("strtdt",startDate);
                        c.startActivity(intent);
                    }
                } else {
//                    dialogEvent(note);
                    new NoteDetailDialog(activity,note).show();
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return noteList.size();
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
}

class MyHolder extends RecyclerView.ViewHolder{

    TextView lokasi;
    TextView keterangan;
    TextView startTime;
    TextView endTime;
    TextView status;
    ConstraintLayout view;

    public MyHolder (View v){
        super(v);
        lokasi = (TextView) v.findViewById(R.id.title_note);
        keterangan = (TextView) v.findViewById(R.id.desc_note);
        startTime = (TextView) v.findViewById(R.id.startTime_note);
        endTime = (TextView) v.findViewById(R.id.endTime_note);
        status = (TextView) v.findViewById(R.id.status_note);
        view = (ConstraintLayout) v.findViewById(R.id.view_note);

    }

}
