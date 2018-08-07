package com.byonchat.android.personalRoom.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.byonchat.android.Manhera.Manhera;
import com.byonchat.android.R;
import com.byonchat.android.personalRoom.model.NotesPhoto;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.CategoryHolder> {

    private List<NotesPhoto> mCategoryList;
    private Context mContext;
    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;

    public interface OnItemClickListener {
        void onItemClick(NotesPhoto category);
    }

    public interface OnItemLongClickListener {
        void onItemLongPress(NotesPhoto category);
    }

    public PhotosAdapter(Context context, List<NotesPhoto> data) {
        mContext = context;
        mCategoryList = data;
    }

    @Override
    public CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_photos, parent, false);
        return new CategoryHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryHolder holder, int position) {
        holder.bind(mContext, mCategoryList.get(position), position, getItemCount(), mItemClickListener, mItemLongClickListener);
    }

    @Override
    public int getItemCount() {
        return mCategoryList.size();
    }

    void setOpenChannelList(List<NotesPhoto> channelList) {
        mCategoryList = channelList;
        notifyDataSetChanged();
    }

    void addLast(NotesPhoto channel) {
        mCategoryList.add(channel);
        notifyDataSetChanged();
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mItemLongClickListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    static class CategoryHolder extends RecyclerView.ViewHolder {
        ImageView vImgPhoto;

        CategoryHolder(View itemView) {
            super(itemView);
            vImgPhoto = (ImageView) itemView.findViewById(R.id.img_photo);
        }

        void bind(final Context context, final NotesPhoto category, int position, int size, @Nullable final OnItemClickListener clickListener, @Nullable final OnItemLongClickListener longClickListener) {
            Log.w("itemphotolist", category.getUrl());
            Manhera.getInstance().get()
                    .load(category.getUrl())
                    .placeholder(R.drawable.no_image)
                    .dontAnimate()
                    .into(vImgPhoto);

            if (clickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickListener.onItemClick(category);
                    }
                });
            }

            if (longClickListener != null) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        longClickListener.onItemLongPress(category);

                        // return true if the callback consumed the long click
                        return true;
                    }
                });
            }
        }
    }

    public void addMessage(NotesPhoto category) {
        mCategoryList.add(category);
        notifyItemInserted(mCategoryList.size() - 1);
    }
}
