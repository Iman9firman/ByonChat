package com.byonchat.android.FragmentSLA.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.FragmentDinamicRoom.DinamicSLATaskActivity;
import com.byonchat.android.FragmentSLA.model.SLAModel;
import com.byonchat.android.R;
import com.byonchat.android.ZoomImageViewActivity;
import com.byonchat.android.model.AddChildFotoExModel;
import com.byonchat.android.provider.RadioButtonCheckDB;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.byonchat.android.provider.RadioButtonCheckDB.COLUMN_COMMENT;
import static com.byonchat.android.provider.RadioButtonCheckDB.COLUMN_ID;
import static com.byonchat.android.provider.RadioButtonCheckDB.COLUMN_ID_DETAIL;
import static com.byonchat.android.provider.RadioButtonCheckDB.COLUMN_IMG;
import static com.byonchat.android.provider.RadioButtonCheckDB.COLUMN_OK;
import static com.byonchat.android.provider.RadioButtonCheckDB.TABLE_NAME;

public class SLACyclerAdapter extends RecyclerView.Adapter<SLACyclerAdapter.SLACyclerHolder> {

    private Activity activity;
    private ArrayList<SLAModel> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private RadioButtonCheckDB database;
    private String idDetailForm;

    // data is passed into the constructor
    public SLACyclerAdapter(Activity activity, ArrayList<SLAModel> data, String idDetailForm) {
        this.activity = activity;
        this.mInflater = LayoutInflater.from(activity.getBaseContext());
        this.mData = data;
        this.idDetailForm = idDetailForm;
        database = new RadioButtonCheckDB(activity);
    }

    // inflates the row layout from xml when needed
    @Override
    public SLACyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.layout_sla_cycler, parent, false);
        return new SLACyclerHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(SLACyclerHolder holder, int position) {
        SLAModel item = mData.get(position);
        holder.textCount.setText(""+item.getCount());
        if (item.isItemToBeCheck()){
            holder.textContent.setText(item.getTitle());
            holder.textId.setText(String.valueOf(item.getDaleman()));
            holder.picturePicker.setVisibility(View.VISIBLE);
            holder.itemLayout.setVisibility(View.VISIBLE);
            holder.nextLayout.setVisibility(View.GONE);
            holder.note.setVisibility(View.VISIBLE);
            holder.next.setVisibility(View.GONE);
            holder.textCount.setVisibility(View.GONE);

            holder.picturePicker.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_att_gallery));

            if (checkDB(idDetailForm, holder.textId.getText().toString())) {
                if (getOkFromDB(idDetailForm, holder.textId.getText().toString()) == 1) {
                    holder.yes.setBackgroundColor(activity.getResources().getColor(R.color.color_primary_green));
                    holder.no.setBackgroundColor(activity.getResources().getColor(R.color.grey));
                } else {
                    holder.no.setBackgroundColor(activity.getResources().getColor(R.color.color_primary_red));
                    holder.yes.setBackgroundColor(activity.getResources().getColor(R.color.grey));
                }

                if (getImgeB(idDetailForm, holder.textId.getText().toString()) != null) {
                    if (holder.textId.getText().toString().split("\\|").length > 1) {
                        Picasso.with(activity).load("file:////" + getImgeB(idDetailForm, holder.textId.getText().toString())).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(holder.picturePicker);
                    } else {
                        Picasso.with(activity).load("file:////" + getImgeB(idDetailForm, holder.textId.getText().toString())).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(holder.picturePicker);
                    }

                }
                holder.note.setText(getComment(idDetailForm, holder.textId.getText().toString()));

            } else {
                holder.yes.setBackgroundColor(activity.getResources().getColor(R.color.grey));
                holder.no.setBackgroundColor(activity.getResources().getColor(R.color.grey));
            }
        } else {
            holder.textTitle.setText(item.getTitle());
            holder.next.setVisibility(View.VISIBLE);
            holder.textCount.setVisibility(View.VISIBLE);
            holder.picturePicker.setVisibility(View.INVISIBLE);
            holder.itemLayout.setVisibility(View.GONE);
            holder.nextLayout.setVisibility(View.VISIBLE);
            holder.note.setVisibility(View.GONE);
        }
        if (item.getCount().equalsIgnoreCase("0") || item.getCount().equalsIgnoreCase("0/0")){
            holder.textCount.setVisibility(View.INVISIBLE);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class SLACyclerHolder extends RecyclerView.ViewHolder{
        TextView textTitle,textContent,textCount,note,textId;
        ImageView next;
        ConstraintLayout itemLayout,nextLayout;
        Button yes,no;
        ImageButton picturePicker;

        SLACyclerHolder(View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.title_slacyc);
            textContent = itemView.findViewById(R.id.content_slacyc);
            textCount = itemView.findViewById(R.id.count_slacyc);
            textId = itemView.findViewById(R.id.id_slacyc);
            next = itemView.findViewById(R.id.next_slacyc);
            itemLayout = itemView.findViewById(R.id.layout_item);
            nextLayout = itemView.findViewById(R.id.layout_next);
            yes = itemView.findViewById(R.id.yes_slacyc);
            no = itemView.findViewById(R.id.no_slacyc);
            picturePicker = itemView.findViewById(R.id.pict_slacyc);
            note = itemView.findViewById(R.id.note_slacyc);

            yes.setOnClickListener(v -> {
                if (checkDB(idDetailForm, textId.getText().toString())) {
                    updateDB(idDetailForm, textId.getText().toString(), 1, null, null);
                    notifyDataSetChanged();
                } else {
                    insertDB(idDetailForm, textId.getText().toString(), 1, null, null);
                    notifyDataSetChanged();
                }
            });
            no.setOnClickListener(v -> {
                if (checkDB(idDetailForm, textId.getText().toString())) {
                    updateDB(idDetailForm, textId.getText().toString(), 0, null, null);
                    notifyDataSetChanged();
                } else {
                    insertDB(idDetailForm, textId.getText().toString(), 0, null, null);
                    notifyDataSetChanged();
                }
                if (getImgeB(idDetailForm, textId.getText().toString()) == null){
                    AddChildFotoExModel aaa = new AddChildFotoExModel(idDetailForm + ";" + textId.getText().toString(), "", "", "cild", "update", getAdapterPosition(), String.valueOf(getOkFromDB(idDetailForm, textId.getText().toString())), 0, "add", "");
                    ((DinamicSLATaskActivity) activity).yourActivityMethod(aaa);
                }

            });
            picturePicker.setOnClickListener(v -> {
                if (checkDB(idDetailForm, textId.getText().toString())) {
                    if (getImgeB(idDetailForm, textId.getText().toString()) != null) {
                        Intent intent = new Intent(activity, ZoomImageViewActivity.class);
                        intent.putExtra(ZoomImageViewActivity.KEY_FILE, "file:////" + getImgeB(idDetailForm, textId.getText().toString()));
                        activity.startActivity(intent);

                    } else {
                        AddChildFotoExModel aaa = new AddChildFotoExModel(idDetailForm + ";" + textId.getText().toString(), "", "", "cild", "update", getAdapterPosition(), String.valueOf(getOkFromDB(idDetailForm, textId.getText().toString())), 0, "add", "");
                        ((DinamicSLATaskActivity) activity).yourActivityMethod(aaa);
                    }


                } else {
                    AddChildFotoExModel aaa = new AddChildFotoExModel(idDetailForm + ";" + textId.getText().toString(), "", "", "cild", "insert", getAdapterPosition(), "", 0, "add", "");
                    ((DinamicSLATaskActivity) activity).yourActivityMethod(aaa);
                }
            });
            picturePicker.setOnLongClickListener(v -> {
                AddChildFotoExModel aaa = new AddChildFotoExModel(idDetailForm + ";" + textId.getText().toString(), "", "", "cild", "update", getAdapterPosition(), String.valueOf(getOkFromDB(idDetailForm, textId.getText().toString())), 0, "add", "");
                ((DinamicSLATaskActivity) activity).yourActivityMethod(aaa);
                return false;
            });
            note.setOnClickListener(v -> {
                LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                final View formsView = inflater.inflate(R.layout.dialog_edit_text, null, false);
                final EditText edit = (EditText) formsView.findViewById(R.id.edit);

                edit.setText(note.getText().toString());

                new AlertDialog.Builder(activity)
                        .setView(formsView)
                        .setTitle("Note")
                        .setPositiveButton("Save",
                                new DialogInterface.OnClickListener() {
                                    @TargetApi(11)
                                    public void onClick(
                                            DialogInterface dialog, int id) {
                                        // textComment.setText(edit.getText());

                                        if (checkDB(idDetailForm, textId.getText().toString())) {
                                            updateDB(idDetailForm, textId.getText().toString(), 0, null, edit.getText().toString());
                                        } else {
                                            insertDB(idDetailForm, textId.getText().toString(), 0, null, edit.getText().toString());
                                        }

                                        notifyDataSetChanged();
                                        dialog.cancel();
                                    }
                                })
                        .show();
            });
            itemView.setOnClickListener(v -> {
                if (mClickListener != null) mClickListener.onItemClick(mData.get(getAdapterPosition()),getAdapterPosition());
            });
        }
    }

    public void insertDB(String idDetail, String id, int value, String image, String comment) {
        SQLiteDatabase db = database.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, id);
        values.put(COLUMN_ID_DETAIL, idDetail);
        values.put(COLUMN_OK, value);
        if (image != null) {
            values.put(COLUMN_IMG, image);
        }
        if (comment != null) {
            values.put(COLUMN_COMMENT, comment);
        }
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void updateDB(String idDetail, String id, int value, String image, String comment) {
        SQLiteDatabase db = database.getWritableDatabase();

        ContentValues values = new ContentValues();
        if (image != null) {
            values.put(COLUMN_IMG, image);
        }

        if (comment != null) {
            values.put(COLUMN_COMMENT, comment);
        }

        if (image == null && comment == null) {
            values.put(COLUMN_OK, value);
        }

        db.update(TABLE_NAME, values, COLUMN_ID_DETAIL + " = '" + idDetail + "' AND " + COLUMN_ID + " =?",
                new String[]{String.valueOf(id)});
    }

    private boolean checkDB(String idDetail, String id) {
        boolean isExist = false;

        SQLiteDatabase db = database.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME
                + " WHERE id_detail = '" + idDetail + "' AND id_item =?", new String[]{String.valueOf(id)});
        while (cursor.moveToNext()) {
            isExist = true;
        }
        cursor.close();
        return isExist;
    }

    private int getOkFromDB(String idDetail, String id) {
        int ok = 0;
        SQLiteDatabase db = database.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_ID, COLUMN_OK},
                COLUMN_ID_DETAIL + " = '" + idDetail + "' AND " + COLUMN_ID + " =?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        while (cursor.moveToNext()) {
            ok = cursor.getInt(cursor.getColumnIndex(COLUMN_OK));
        }
        cursor.close();

        return ok;
    }

    private String getImgeB(String idDetail, String id) {
        String ok = "";
        SQLiteDatabase db = database.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_ID_DETAIL, COLUMN_IMG},
                COLUMN_ID_DETAIL + " = '" + idDetail + "' AND " + COLUMN_ID + " =?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        while (cursor.moveToNext()) {
            ok = cursor.getString(cursor.getColumnIndex(COLUMN_IMG));
        }
        cursor.close();

        if (ok != null) {
            if (!ok.equalsIgnoreCase("")) {
                if (ok.split("\\|").length > 1) {
                    ok = ok.split("\\|")[0];
                }
            }

        }


        return ok;
    }

    private String getComment(String idDetail, String id) {
        String ok = "";
        SQLiteDatabase db = database.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_ID_DETAIL, COLUMN_COMMENT},
                COLUMN_ID_DETAIL + " = '" + idDetail + "' AND " + COLUMN_ID + " =?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        while (cursor.moveToNext()) {
            ok = cursor.getString(cursor.getColumnIndex(COLUMN_COMMENT));
        }
        cursor.close();

        return ok;
    }


    public void removeDB() {
        database.getWritableDatabase().delete(TABLE_NAME, null, null);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(SLAModel item, int position);
    }
}
