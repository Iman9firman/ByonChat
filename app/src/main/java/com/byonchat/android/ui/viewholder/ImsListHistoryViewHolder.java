package com.byonchat.android.ui.viewholder;

import android.annotation.TargetApi;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.byonchat.android.R;
import com.byonchat.android.list.IconItem;
import com.byonchat.android.ui.adapter.OnItemClickListener;
import com.byonchat.android.ui.adapter.OnLongItemClickListener;
import com.rockerhieu.emojicon.EmojiconTextView;

public class ImsListHistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        View.OnLongClickListener {

    public RelativeLayout vFrameMessageContainer;
    public ImageView vImagePhoto;
    public EmojiconTextView vTextTitle;
    public TextView vDateInfo;
    public EmojiconTextView vTextInfo;
    public ImageView vMute;
    public ImageView vCheck;
    public View vSelection;
    public RelativeLayout vFrameNotification;
    public TextView vTextUnread;

    protected Drawable selectionBackground;
    protected int selectionChecked;
    protected int downloadedBackground;

    public OnItemClickListener itemClickListener;
    public OnLongItemClickListener longItemClickListener;

    protected IconItem item;

    public ImsListHistoryViewHolder(View view,
                                    OnItemClickListener itemClickListener,
                                    OnLongItemClickListener longItemClickListener) {
        super(view);
        vFrameMessageContainer = (RelativeLayout) view.findViewById(R.id.message_container);
        vImagePhoto = (ImageView) view.findViewById(R.id.imagePhoto);
        vTextTitle = (EmojiconTextView) view.findViewById(R.id.textTitle);
        vDateInfo = (TextView) view.findViewById(R.id.dateInfo);
        vTextInfo = (EmojiconTextView) view.findViewById(R.id.textInfo);
        vMute = (ImageView) view.findViewById(R.id.mute);
        vFrameNotification = (RelativeLayout) view.findViewById(R.id.frame_notification);
        vTextUnread = (TextView) view.findViewById(R.id.text_unread);
        vCheck = (ImageView) view.findViewById(R.id.check);
        vSelection = (View) view.findViewById(R.id.selection);

        loadConfig();

        this.itemClickListener = itemClickListener;
        this.longItemClickListener = longItemClickListener;

        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
    }

    protected void loadConfig() {
        selectionBackground = new ColorDrawable(ContextCompat.getColor(itemView.getContext(), R.color.transparent));
//        selectionBackground.setAlpha(51);
        selectionChecked = ContextCompat.getColor(itemView.getContext(), android.R.color.black);
        downloadedBackground = ContextCompat.getColor(itemView.getContext(), android.R.color.holo_blue_dark);
    }

    @TargetApi(16)
    public void onCommentSelected(IconItem item) {
        this.item = item;

        itemView.setBackground(item.isSelected() ? selectionBackground : null);
        if (item.isSelected())
            vCheck.setColorFilter(selectionChecked, PorterDuff.Mode.SRC_ATOP);

        vCheck.setVisibility(item.isSelected() ? View.VISIBLE : View.GONE);
        vMute.setVisibility(item.isMuted ? View.VISIBLE : View.GONE);
        vFrameNotification.setVisibility(item.unread == 0 ? View.GONE : View.VISIBLE);
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
