package com.byonchat.android.personalRoom.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.byonchat.android.R;
import com.byonchat.android.personalRoom.model.PictureModel;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

/**
 * Created by Zey on 13/Nov/18.
 */
public class ProductCatalogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<PictureModel> data;

    public ProductCatalogAdapter(Context context, List<PictureModel> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item_picture, parent, false);
        viewHolder = new MyItemHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final PictureModel item = data.get(position);
        String url = item.getUrl_thumb();

        ((MyItemHolder) holder).vTitle.setText(item.getTitle());

        Picasso.with(context)
                .load(url)
                .noFade()
                .placeholder(R.drawable.no_image)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .into(((MyItemHolder) holder).mImg);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        Target mImg;
        TextView vTitle;

        public MyItemHolder(View itemView) {
            super(itemView);
            mImg = (Target) itemView.findViewById(R.id.item_img);
            vTitle = (TextView) itemView.findViewById(R.id.title_item_grid);

            vTitle.setVisibility(View.VISIBLE);
        }
    }
}