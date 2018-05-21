package com.byonchat.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.R;
import com.byonchat.android.list.ItemCreateNoteGroup;

import java.util.ArrayList;

/**
 * Created by Iman Firmansyah on 3/4/2016.
 */
public class GroupNoteAdapter  extends ArrayAdapter<ItemCreateNoteGroup> {
    Context context;
    int layoutResourceId;
    ArrayList<ItemCreateNoteGroup> data = new ArrayList<ItemCreateNoteGroup>();

    public GroupNoteAdapter(Context context, int layoutResourceId,
                                 ArrayList<ItemCreateNoteGroup> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RecordHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new RecordHolder();
            holder.txtTitle = (TextView) row.findViewById(R.id.textTitle);
            holder.editNote = (ImageButton) row.findViewById(R.id.editNote);
            holder.deleteNote = (ImageButton) row.findViewById(R.id.deleteNote);
            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }

        ItemCreateNoteGroup item = data.get(position);
        holder.txtTitle.setText(item.getTitle());

        holder.editNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "cumi", Toast.LENGTH_SHORT).show();

            }
        });
        holder.deleteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"cumay", Toast.LENGTH_SHORT).show();

            }
        });

        return row;

    }

    static class RecordHolder {
        TextView txtTitle;
        ImageButton editNote,deleteNote;
    }
}

