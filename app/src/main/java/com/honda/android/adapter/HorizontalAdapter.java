package com.honda.android.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.honda.android.R;
import com.honda.android.personalRoom.model.PictureModel;

import java.io.File;
import java.util.List;

/**
 * Created by byonc on 4/25/2017.
 */

public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {

    private List<PictureModel> horizontalList;
    private Context context;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        private FrameLayout frame;

        public MyViewHolder(View view) {
            super(view);
            frame = (FrameLayout) view.findViewById(R.id.frame);
            imageView = (ImageView) view.findViewById(R.id.imageView);
        }
    }

    public HorizontalAdapter(Context context, List<PictureModel> horizontalList) {
        this.context = context;
        this.horizontalList = horizontalList;
        mPref = context.getSharedPreferences("person", Context.MODE_PRIVATE);
        mEditor = mPref.edit();

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.horizontal_item_view, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        if (horizontalList.get(position).getDrawable() != null) {
            Glide.with(context).load(horizontalList.get(position).getDrawable())
                    .centerCrop()
                    .into(holder.imageView);
        } else {
            final File f = new File(horizontalList.get(position).getUrl());
            Glide.with(context).load(f)
                    .centerCrop()
                    .into(holder.imageView);
        }


        /*holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.imageView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.border));
                    *//*Glide.with(getApplicationContext()).load(f)
                            .centerCrop()
                            .into(bigImageView);*//*
            }
        });*/

        if (horizontalList.get(position).isSelected()) {
            holder.frame.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.border));
        } else {
            holder.frame.setBackgroundColor(context.getResources().getColor(R.color.full_transparent));
        }

    }

    public void setSelected(int pos) {
        try {
            if (horizontalList.size() > 1) {
                horizontalList.get(mPref.getInt("position", 0)).setSelected(false);
                mEditor.putInt("position", pos);
                mEditor.commit();
            }
            horizontalList.get(pos).setSelected(true);
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deletePage(int position){
        horizontalList.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return horizontalList.size();
    }

}
