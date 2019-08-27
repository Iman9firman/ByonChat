package com.byonchat.android.Sample;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.byonchat.android.R;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import static com.byonchat.android.Sample.DetailAreaScheduleSLA.link_pic;

public class DetailAreaScheduleAdapter extends RecyclerView.Adapter<DetailAreaScheduleAdapter.MyViewHolder> {

    private ArrayList<DetailArea> detailareaList;
    private Activity mActivity;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView start, proses, done;
        public ImageView cekstart, cekproses, cekdone;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.txtDeatailArea);

            start = (ImageView) view.findViewById(R.id.logo_item_grid);
            proses = (ImageView) view.findViewById(R.id.logo_item_grid2);
            done = (ImageView) view.findViewById(R.id.logo_item_grid3);

            cekstart = (ImageView) view.findViewById(R.id.start_check);
            cekproses = (ImageView) view.findViewById(R.id.proses_check);
            cekdone = (ImageView) view.findViewById(R.id.done_check);
        }
    }

    public DetailAreaScheduleAdapter(Activity activity, ArrayList<DetailArea> detailareaList) {
        this.detailareaList = detailareaList;
        this.mActivity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detail_area_sla_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DetailArea detailArea = detailareaList.get(position);
        holder.title.setText(detailArea.getTitle());

        holder.cekdone.setVisibility(View.GONE);
        holder.cekstart.setVisibility(View.GONE);
        holder.cekproses.setVisibility(View.GONE);
        setImagePicasso(holder, position, detailArea);

        holder.start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dsc = new Intent(mActivity, DialogAddSchedulePicture.class);
                dsc.putExtra("id_da",detailArea.getId());
                dsc.putExtra("id_jjt",detailArea.getId_jjt());
                dsc.putExtra("tt",detailArea.getTitle());
                dsc.putExtra("fq",detailArea.getFrekuensi());
                dsc.putExtra("pr",detailArea.getPeriod());
                dsc.putExtra("post","start");
                mActivity.startActivity(dsc);
            }
        });

        holder.proses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dsc = new Intent(mActivity, DialogAddSchedulePicture.class);
                dsc.putExtra("id_da",detailArea.getId());
                dsc.putExtra("id_jjt",detailArea.getId_jjt());
                dsc.putExtra("tt",detailArea.getTitle());
                dsc.putExtra("fq",detailArea.getFrekuensi());
                dsc.putExtra("pr",detailArea.getPeriod());
                dsc.putExtra("post","proses");
                mActivity.startActivity(dsc);
            }
        });

        holder.done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dsc = new Intent(mActivity, DialogAddSchedulePicture.class);
                dsc.putExtra("id_da",detailArea.getId());
                dsc.putExtra("id_jjt",detailArea.getId_jjt());
                dsc.putExtra("tt",detailArea.getTitle());
                dsc.putExtra("fq",detailArea.getFrekuensi());
                dsc.putExtra("pr",detailArea.getPeriod());
                dsc.putExtra("post","done");
                mActivity.startActivity(dsc);
            }
        });
    }

    public void setImagePicasso(MyViewHolder holder, int position, DetailArea detailArea){
        Log.w("Ezaa",detailArea.getImg_start());
        Log.w("Ezaa",detailArea.getImg_proses());
        Log.w("Ezaa",detailArea.getImg_done());
        if(!detailArea.getImg_start().equalsIgnoreCase("null")) {
            if(detailArea.getImg_start().startsWith("/storage")) {
                Picasso.with(mActivity).load(new File(detailArea.getImg_start()))
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .into(holder.start);
            }else {
                Picasso.with(mActivity).load(link_pic + detailArea.getImg_start())
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .into(holder.start);
                holder.cekstart.setVisibility(View.VISIBLE);
            }
        } if(!detailArea.getImg_proses().equalsIgnoreCase("null")) {
            if(detailArea.getImg_proses().startsWith("/storage")) {
                Picasso.with(mActivity).load(new File(detailArea.getImg_proses()))
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .into(holder.proses);
            }else {
                Picasso.with(mActivity).load(link_pic + detailArea.getImg_proses())
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .into(holder.proses);
                holder.cekproses.setVisibility(View.VISIBLE);
            }
        } if(!detailArea.getImg_done().equalsIgnoreCase("null")) {
            if(detailArea.getImg_done().startsWith("/storage")) {
                Picasso.with(mActivity).load(new File(detailArea.getImg_done()))
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .into(holder.done);
            }else {
                Picasso.with(mActivity).load(link_pic + detailArea.getImg_done())
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .into(holder.done);
                holder.cekdone.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return detailareaList.size();
    }
}
