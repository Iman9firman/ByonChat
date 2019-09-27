package com.honda.android.ui.viewholder;


import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.honda.android.R;
import com.honda.android.data.model.File;
import com.honda.android.ui.adapter.OnLongItemClickListener;
import com.squareup.picasso.Target;

import me.gujun.android.taggroup.TagGroup;

public class ByonchatStatusViewHolder extends RecyclerView.ViewHolder implements /*View.OnClickListener,*/
        View.OnLongClickListener {

    public LinearLayout vMainContent;
    public Target vIconView;
    public LinearLayout vLayoutComment, vBtComment;
    public TextView vName, vTimestamp, vTxtStatusMsg;
    public TextView vTextStatus;
    public Button vTextDownload;
    public View vView;
    public TagGroup vTagGroup;
    public carbon.widget.LinearLayout vFramePhoto;

    protected Drawable selectionBackground;
    protected int selectionChecked;
    protected int downloadedBackground;
    protected Drawable downloadingBackground;

    /*public OnItemClickListener itemClickListener;*/
    public OnLongItemClickListener longItemClickListener;

    protected File file;

    public ByonchatStatusViewHolder(View view,
                                 /*OnItemClickListener itemClickListener,*/
                                 OnLongItemClickListener longItemClickListener) {
        super(view);
        vName = (TextView) view.findViewById(R.id.name);
        vTimestamp = (TextView) view.findViewById(R.id.timestamp);
        vTxtStatusMsg = (TextView) view.findViewById(R.id.txtStatusMsg);
        vIconView = (Target) view.findViewById(R.id.imagePhoto);
        vMainContent = (LinearLayout) view.findViewById(R.id.main_content);
        vLayoutComment = (LinearLayout) view.findViewById(R.id.layoutComment);
        vBtComment = (LinearLayout) view.findViewById(R.id.btComment);
        vView = (View) view.findViewById(R.id.view);
        vTagGroup = (TagGroup) view.findViewById(R.id.tag_group);
        vTextStatus = (TextView) view.findViewById(R.id.status_request);
        vTextDownload = (Button) view.findViewById(R.id.btDownload);
        vFramePhoto = (carbon.widget.LinearLayout) view.findViewById(R.id.frame_photo);

        loadText();
        loadConfig();

        /*this.itemClickListener = itemClickListener;*/
        this.longItemClickListener = longItemClickListener;

//        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
    }

    protected void loadText() {
        vTextStatus.setText("waiting");
    }

    protected void loadConfig() {
        selectionBackground = new ColorDrawable(ContextCompat.getColor(itemView.getContext(), R.color.byonchat_divider_light));
        selectionBackground.setAlpha(51);
        selectionChecked = ContextCompat.getColor(itemView.getContext(), android.R.color.black);
        downloadedBackground = ContextCompat.getColor(itemView.getContext(), android.R.color.holo_blue_dark);
        downloadingBackground = new ColorDrawable(ContextCompat.getColor(itemView.getContext(), R.color.byonchat_downloading_transparent));
    }

//    @Override
//    public void onClick(View v) {
//        int position = getAdapterPosition();
//        if (position >= 0) {
//            itemClickListener.onItemClick(v, position);
//        }
//    }

    @Override
    public boolean onLongClick(View v) {
        if (longItemClickListener != null) {
            int position = getAdapterPosition();
            if (position >= 0) {
                longItemClickListener.onLongItemClick(v, position);
            }
            return true;
        }
        return false;
    }
}

