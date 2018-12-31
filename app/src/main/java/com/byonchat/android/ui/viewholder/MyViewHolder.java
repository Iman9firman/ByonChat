package com.byonchat.android.ui.viewholder;

import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.byonchat.android.R;
import com.byonchat.android.ui.adapter.OnItemClickListener;
import com.byonchat.android.ui.adapter.OnLongItemClickListener;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

public class MyViewHolder extends AbstractDraggableItemViewHolder implements View.OnClickListener,
        View.OnLongClickListener {

    public OnItemClickListener itemClickListener;
    public OnLongItemClickListener longItemClickListener;
    public TextView mTextView;
    public ImageView mImageView;
    public CardView mCardview;

    public MyViewHolder(View v,
                        OnItemClickListener itemClickListener,
                        OnLongItemClickListener longItemClickListener) {
        super(v);
        mTextView = v.findViewById(R.id.title_item_grid);
        mImageView = v.findViewById(R.id.logo_item_grid);
        mCardview = v.findViewById(R.id.cardview);

        this.itemClickListener = itemClickListener;
        this.longItemClickListener = longItemClickListener;

        v.setOnClickListener(this);
        v.setOnLongClickListener(this);
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
