package com.byonchat.android.createMeme;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.byonchat.android.R;

// here's our beautiful adapter
public class AdapterTextStyle extends ArrayAdapter<ObjectItem> {

    Context mContext;
    int layoutResourceId;
    ObjectItem data[] = null;

    public AdapterTextStyle(Context mContext, int layoutResourceId, ObjectItem[] data) {

        super(mContext, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
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
        TextView textViewItem = (TextView) convertView.findViewById(R.id.label);
        Typeface face = Typeface.DEFAULT;
        if(!objectItem.itemName.equalsIgnoreCase("SYSTEM_DEFAULT")){
            face = Typeface.createFromAsset(mContext.getAssets(),"fonts/"+objectItem.itemName.toUpperCase()+".TTF");
        }
        textViewItem.setTypeface(face);
        textViewItem.setText(objectItem.itemName.toLowerCase());
        textViewItem.setTag(objectItem.itemId);

        return convertView;

    }


}
