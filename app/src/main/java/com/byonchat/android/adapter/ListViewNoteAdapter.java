package com.byonchat.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.byonchat.android.R;
import com.byonchat.android.createMeme.ObjectItem;

/**
 * Created by Iman Firmansyah on 3/10/2016.
 */
public class ListViewNoteAdapter extends ArrayAdapter<ObjectItem> {

        Context mContext;
        int layoutResourceId;
        ObjectItem data[] = null;
        String from = null;

public ListViewNoteAdapter(Context mContext, int layoutResourceId, ObjectItem[] data,String from) {
        super(mContext, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
        this.from = from;
        }

@Override
public View getView(int position, View convertView, ViewGroup parent) {

        /*
         * The convertView argument is essentially a "ScrapView" as described is Lucas post
         * http://lucasr.org/2012/04/05/performance-tips-for-androids-listview/
         * It will have a non-null value when ListView is asking you recycle the row layout.
         * So, when convertView is not null, you should simply update its contents instead of inflating a new row layout.
         */
        if(convertView==null){
        // inflate the layout
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        convertView = inflater.inflate(layoutResourceId, parent, false);
        }

        // object item based on the position
        ObjectItem objectItem = data[position];

        // get the TextView and then set the text (item name) and tag (item ID) values
        TextView textViewTitle = (TextView) convertView.findViewById(R.id.titleNotes);
        TextView textViewContent = (TextView) convertView.findViewById(R.id.contentNotes);
        textViewTitle.setText(from+" " + objectItem.itemId+": " );
        textViewContent.setText(objectItem.itemName);

        return convertView;

        }

        }