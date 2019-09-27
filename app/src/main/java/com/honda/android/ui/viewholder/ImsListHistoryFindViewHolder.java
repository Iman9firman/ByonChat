package com.honda.android.ui.viewholder;

import android.annotation.TargetApi;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.honda.android.R;
import com.honda.android.list.IconItem;
import com.honda.android.ui.adapter.OnItemClickListener;
import com.honda.android.ui.adapter.OnLongItemClickListener;
import com.rockerhieu.emojicon.EmojiconTextView;

public class ImsListHistoryFindViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        View.OnLongClickListener {

    public RelativeLayout vFrameMessageContainer;
    public EmojiconTextView vTextTitle;
    public TextView vDateInfo;
    public EmojiconTextView vTextInfo;
    public View vSelection;

    protected Drawable selectionBackground;
    protected int selectionChecked;
    protected int downloadedBackground;

    public OnItemClickListener itemClickListener;
    public OnLongItemClickListener longItemClickListener;

    protected IconItem item;

    public ImsListHistoryFindViewHolder(View view,
                                        OnItemClickListener itemClickListener,
                                        OnLongItemClickListener longItemClickListener) {
        super(view);
        vFrameMessageContainer = (RelativeLayout) view.findViewById(R.id.message_container);
        vTextTitle = (EmojiconTextView) view.findViewById(R.id.textTitle);
        vDateInfo = (TextView) view.findViewById(R.id.dateInfo);
        vTextInfo = (EmojiconTextView) view.findViewById(R.id.textInfo);
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