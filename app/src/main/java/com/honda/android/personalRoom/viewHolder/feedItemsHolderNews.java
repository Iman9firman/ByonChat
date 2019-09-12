package com.honda.android.personalRoom.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.honda.android.R;
import com.squareup.picasso.Target;

/**
 * Created by lukma on 3/28/2016.
 */
public class feedItemsHolderNews extends RecyclerView.ViewHolder {
    public LinearLayout main_content,layoutComment,btLoves,btComment;
    public TextView name,timestamp,txtStatusMsg,totalComments,totalLoves;
    public Target iconView;

    public feedItemsHolderNews(View view) {
        super(view);
        this.name = (TextView) view.findViewById(R.id.name);
        this.timestamp = (TextView) view.findViewById(R.id.timestamp);
        this.txtStatusMsg = (TextView) view.findViewById(R.id.txtStatusMsg);
        this.iconView=(Target) view.findViewById(R.id.imagePhoto);
        this.main_content=(LinearLayout) view.findViewById(R.id.main_content);
        this.layoutComment=(LinearLayout) view.findViewById(R.id.layoutComment);
        this.btLoves =(LinearLayout) view.findViewById(R.id.btLoves);
        this.btComment =(LinearLayout) view.findViewById(R.id.btComment);
        this.totalComments =(TextView) view.findViewById(R.id.totalComments);
        this.totalLoves =(TextView) view.findViewById(R.id.totalLoves);
    }
}
