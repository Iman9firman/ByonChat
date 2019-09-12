package com.honda.android.adapter;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.honda.android.Manhera.Manhera;
import com.honda.android.R;
import com.joooonho.SelectableRoundedImageView;
import com.squareup.picasso.Target;

public class RecyclerItemGalleryViewHolder extends RecyclerView.ViewHolder {

    public static OnItemClickListener mItemClickListener;
    public static OnItemLongClickListener mItemLongClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongPress(int position);
    }

    public ImageView vPhoto;
    public TextView vTitle;

    public RecyclerItemGalleryViewHolder(final View parent, ImageView vPhoto) {
        super(parent);
        this.vPhoto = vPhoto;
    }

    public static RecyclerItemGalleryViewHolder newInstance(View view) {
        ImageView vPhoto = (ImageView) view.findViewById(R.id.image_view);

        return new RecyclerItemGalleryViewHolder(view, vPhoto);
    }

    public void bind(final int position, final OnItemClickListener mItemClickListener, final OnItemLongClickListener mItemLongClickListener) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(position);
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mItemLongClickListener.onItemLongPress(position);
                return true;
            }
        });
    }

    public void setImage(Context context, CharSequence reference) {
        Manhera.getInstance().get().load(reference)
                .error(R.drawable.no_image)
                .placeholder(R.drawable.no_image)
                .dontAnimate()
                .into(vPhoto);
    }
}
