package com.byonchat.android.tempSchedule;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity;
import com.byonchat.android.R;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.RoomsDetail;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NotePreviewAdapter extends RecyclerView.Adapter<MyHolder> {

    private ArrayList<Note> noteList;
    private Context c;
    AlertDialog.Builder dialog;
    View dialogView;

    //punya activity
    private String title;
    private String username;
    private String idTab;
    private String color;
    private String latLong;
    private String from;
    private String calendar;
    private String startDate;

    public NotePreviewAdapter (Context c, ArrayList<Note> noteList,String title,String username,String idTab,String color,String latLong,String from,String calendar,String startDate){
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



        holder.title.setText(note.getTitle());
        holder.desc.setText(note.getDesc());
        holder.startTime.setText(note.getStartTime());
        holder.endTime.setText(note.getEndTime());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkDate(startDate)){

                    dialogEvent(note);

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
                    c.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }
    private void dialogEvent(Note note){
        dialog = new AlertDialog.Builder(c);
        dialogView = LayoutInflater.from(c).inflate(R.layout.note_dialog_layout,null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setTitle(note.getTitle());
        TextView time = (TextView) dialogView.findViewById(R.id.time_dialognote);
        TextView content = (TextView) dialogView.findViewById(R.id.content_dialognote);

        time.setText(note.getStartTime()+" > "+note.getEndTime());
        content.setText(note.getDesc());

        dialog.setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        dialog.show();
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

    TextView title;
    TextView desc;
    TextView startTime;
    TextView endTime;
    ConstraintLayout view;

    public MyHolder (View v){
        super(v);
        title = (TextView) v.findViewById(R.id.title_note);
        desc = (TextView) v.findViewById(R.id.desc_note);
        startTime = (TextView) v.findViewById(R.id.startTime_note);
        endTime = (TextView) v.findViewById(R.id.endTime_note);
        view = (ConstraintLayout) v.findViewById(R.id.view_note);

    }
}
