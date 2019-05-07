package com.byonchat.android.ui.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.byonchat.android.R;
import java.io.File;
import com.byonchat.android.model.Photo;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.SLANoteDB;
import com.byonchat.android.ui.adapter.OnPreviewItemClickListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.byonchat.android.provider.SLANoteDB.COLUMN_COMMENT;
import static com.byonchat.android.provider.SLANoteDB.COLUMN_ID_DETAIL;
import static com.byonchat.android.provider.SLANoteDB.TABLE_NAME;

public class PustReportRepairAdapter extends RecyclerView.Adapter<PustReportRepairAdapter.MyViewHolder> {

    private List<Photo> allList;
    private Activity context;
    String idDetail, idTab, username;
    protected OnPreviewItemClickListener onPreviewItemClickListener;
    private SLANoteDB database;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView before, after;
        TextView keterangan, note;

        public MyViewHolder(View view) {
            super(view);
            before = (ImageView) view.findViewById(R.id.imageBefore);
            after = (ImageView) view.findViewById(R.id.imageAfter);
            keterangan = (TextView) view.findViewById(R.id.keterangan);
            note = (TextView) view.findViewById(R.id.notess);
        }
    }

    public PustReportRepairAdapter(Activity context, String idDetail, String username, String idTab, List<Photo> moviesList,
                              OnPreviewItemClickListener onPreviewItemClickListener) {
        this.allList = moviesList;
        this.context = context;
        this.idDetail = idDetail;
        this.idTab = idTab;
        this.username = username;
        this.onPreviewItemClickListener = onPreviewItemClickListener;
        this.database = new SLANoteDB(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_push_reportrepair_activity, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Photo foto = allList.get(position);

        Picasso.with(context).load(foto.getBefore())
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(holder.before);

        if(foto.getAfter() != null) {
            Picasso.with(context).load(foto.getAfter())
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .into(holder.after);
        }else {
            BotListDB db = BotListDB.getInstance(context);
            Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "reportrepair", foto.getId());

            if (cursorCild.getCount() > 0) {
                File f = new File(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                Picasso.with(context).load(f)
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .into(holder.after);
            }
        }
        holder.keterangan.setText(foto.getTitle());
        holder.after.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPreviewItemClickListener != null) {
                    onPreviewItemClickListener.onItemClick(v, Integer.parseInt(foto.getId()), null , "after");
                }
            }
        });
        holder.before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPreviewItemClickListener != null) {
                    onPreviewItemClickListener.onItemClick(v, Integer.parseInt(foto.getId()), null , "before");
                }
            }
        });
        holder.note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (onPreviewItemClickListener != null) {
                    onPreviewItemClickListener.onItemClick(v, Integer.parseInt(foto.getId()), null , "note");
                }*/
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                final View formsView = inflater.inflate(R.layout.dialog_edit_text, null, false);
                final EditText edit = (EditText) formsView.findViewById(R.id.edit);

                edit.setText(holder.note.getText().toString());

                new AlertDialog.Builder(context)
                        .setView(formsView)
                        .setTitle("Note")
                        .setPositiveButton("Save",
                                new DialogInterface.OnClickListener() {
                                    @TargetApi(11)
                                    public void onClick(
                                            DialogInterface dialog, int id) {
                                        holder.note.setText(edit.getText());

                                        if (checkDB(foto.getId())) {
                                            updateDB(foto.getId(), edit.getText().toString());
                                        } else {
                                            insertDB(foto.getId(), edit.getText().toString());
                                        }

                                        dialog.cancel();
                                    }
                                })
                        .show();
            }
        });


    }


    public void insertDB(String id, String comment) {
        SQLiteDatabase db = database.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID_DETAIL, id);
        if (comment != null) {
            values.put(COLUMN_COMMENT, comment);
        }
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void updateDB(String id, String comment) {
        SQLiteDatabase db = database.getWritableDatabase();

        ContentValues values = new ContentValues();
        if (comment != null) {
            values.put(COLUMN_COMMENT, comment);
        }

        db.update(TABLE_NAME, values, COLUMN_ID_DETAIL + " = ?",
                new String[]{String.valueOf(id)});
    }

    private boolean checkDB(String id) {
        boolean isExist = false;

        SQLiteDatabase db = database.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME
                + " WHERE id_detail =?", new String[]{String.valueOf(id)});
        while (cursor.moveToNext()) {
            isExist = true;
        }
        cursor.close();
        return isExist;
    }

    @Override
    public int getItemCount() {
        return allList.size();
    }
}