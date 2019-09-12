package com.honda.android.createMeme;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.honda.android.R;

import java.util.ArrayList;

// here's our beautiful adapter
public class AdapterBubble extends ArrayAdapter<Bitmap> {

    Context mContext;
    int layoutResourceId;
    ArrayList<Bitmap> data = new ArrayList<Bitmap>();

    public AdapterBubble(Context mContext, int layoutResourceId, ArrayList<Bitmap> data) {

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
        Bitmap objectItem = data.get(position);

        // get the TextView and then set the text (item name) and tag (item ID) values
        ImageView imageView = (ImageView) convertView.findViewById(R.id.label);
        imageView.setImageBitmap(objectItem);


        return convertView;

    }


}
