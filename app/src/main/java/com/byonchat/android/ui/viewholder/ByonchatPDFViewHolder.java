package com.byonchat.android.ui.viewholder;


import android.annotation.TargetApi;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.byonchat.android.R;
import com.byonchat.android.data.model.File;
import com.byonchat.android.ui.adapter.OnItemClickListener;
import com.byonchat.android.ui.adapter.OnLongItemClickListener;
import com.squareup.picasso.Target;

public class ByonchatPDFViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        View.OnLongClickListener {

    public LinearLayout vMainContent;
    public Target vIconView;
    public LinearLayout vLayoutComment, vBtLoves, vBtComment, vBtNix;
    public TextView vName, vTimestamp, vTxtStatusMsg, vTotalComments, vTotalLoves;
    public TextView vTxtPreview, vTxtRequest;
    public View vView, vPreviewLine, vRequestLine;
    public LinearLayout vDotA, vDotB;
    public TextSwitcher vTsLikesCounter1, vTsLikesCounter2;

    protected Drawable selectionBackground;
    protected int selectionChecked;
    protected int downloadedBackground;
    protected Drawable downloadingBackground;

    public OnItemClickListener itemClickListener;
    public OnLongItemClickListener longItemClickListener;

    protected File file;

    public ByonchatPDFViewHolder(View view,
                                 OnItemClickListener itemClickListener,
                                 OnLongItemClickListener longItemClickListener) {
        super(view);
        vName = (TextView) view.findViewById(R.id.name);
        vTimestamp = (TextView) view.findViewById(R.id.timestamp);
        vTxtStatusMsg = (TextView) view.findViewById(R.id.txtStatusMsg);
        vIconView = (Target) view.findViewById(R.id.imagePhoto);
        vMainContent = (LinearLayout) view.findViewById(R.id.main_content);
        vLayoutComment = (LinearLayout) view.findViewById(R.id.layoutComment);
        vBtLoves = (LinearLayout) view.findViewById(R.id.btLoves);
        vBtComment = (LinearLayout) view.findViewById(R.id.btComment);
        vTotalComments = (TextView) view.findViewById(R.id.totalComments);
        vTotalLoves = (TextView) view.findViewById(R.id.totalLoves);
        vBtNix = (LinearLayout) view.findViewById(R.id.btNix);
        vView = (View) view.findViewById(R.id.view);
        vPreviewLine = (View) view.findViewById(R.id.viee);
        vRequestLine = (View) view.findViewById(R.id.comments_line);
        vTxtPreview = (TextView) view.findViewById(R.id.text);
        vTxtRequest = (TextView) view.findViewById(R.id.text_btn_comment);
        vDotA = (LinearLayout) view.findViewById(R.id.dotA);
        vDotB = (LinearLayout) view.findViewById(R.id.dotB);
        vTsLikesCounter1 = (TextSwitcher) view.findViewById(R.id.tsLikesCounter1);
        vTsLikesCounter2 = (TextSwitcher) view.findViewById(R.id.tsLikesCounter2);

        loadText();
        loadConfig();

        this.itemClickListener = itemClickListener;
        this.longItemClickListener = longItemClickListener;

        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
    }

    protected void loadText() {
        vTxtPreview.setText("Preview");
        vTxtRequest.setText("Request");

        vPreviewLine.setVisibility(View.GONE);
        vRequestLine.setVisibility(View.GONE);
        vTotalComments.setVisibility(View.GONE);
        vTotalLoves.setVisibility(View.GONE);
        vBtNix.setVisibility(View.GONE);
        vDotA.setVisibility(View.GONE);
        vDotB.setVisibility(View.GONE);
        vTsLikesCounter1.setVisibility(View.GONE);
        vTsLikesCounter2.setVisibility(View.GONE);
    }

    protected void loadConfig() {
        selectionBackground = new ColorDrawable(ContextCompat.getColor(itemView.getContext(), R.color.byonchat_divider_light));
        selectionBackground.setAlpha(51);
        selectionChecked = ContextCompat.getColor(itemView.getContext(), android.R.color.black);
        downloadedBackground = ContextCompat.getColor(itemView.getContext(), android.R.color.holo_blue_dark);
        downloadingBackground = new ColorDrawable(ContextCompat.getColor(itemView.getContext(), R.color.byonchat_downloading_transparent));
    }

    @Override
    public void onClick(View v) {
        int position = getAdapterPosition();
        if (position >= 0) {
            itemClickListener.onItemClick(v, position);
        }
    }

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
