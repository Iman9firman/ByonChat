package com.byonchat.android.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.byonchat.android.R;
import com.byonchat.android.model.Image;
import com.byonchat.android.personalRoom.model.PictureModel;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<PictureModel> images;
    private Context context;
    private static RecyclerItemGalleryViewHolder.OnItemClickListener mItemClickListener;
    private static RecyclerItemGalleryViewHolder.OnItemLongClickListener mItemLongClickListener;

    public GalleryAdapter(Context context, List<PictureModel> item,
                          RecyclerItemGalleryViewHolder.OnItemClickListener mItemClickListener,
                          RecyclerItemGalleryViewHolder.OnItemLongClickListener mItemLongClickListener) {
        this.context = context;
        this.images = item;
        this.mItemClickListener = mItemClickListener;
        this.mItemLongClickListener = mItemLongClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.list_item_gallery, parent, false);
        return RecyclerItemGalleryViewHolder.newInstance(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        RecyclerItemGalleryViewHolder holder = (RecyclerItemGalleryViewHolder) viewHolder;
        PictureModel data = images.get(position);
        holder.setImage(context, data.getUrl());

        holder.bind(position, mItemClickListener, mItemLongClickListener);
    }

    public int getBasicItemCount() {
        return images == null ? 0 : images.size();
    }

    @Override
    public int getItemCount() {
        return getBasicItemCount();
    }

    public void setData(List<PictureModel> pictureModels) {
        this.images.clear();
        this.images.addAll(images);
    }
}
