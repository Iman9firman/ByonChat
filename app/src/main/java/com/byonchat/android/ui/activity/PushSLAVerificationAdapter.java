package com.byonchat.android.ui.activity;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.byonchat.android.R;

import java.io.File;

import com.byonchat.android.model.Photo;
import com.byonchat.android.model.SLAmodelNew;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.ui.adapter.OnPreviewItemClickListener;
import com.byonchat.android.ui.adapter.OnRequestItemClickListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PushSLAVerificationAdapter extends RecyclerView.Adapter<PushSLAVerificationAdapter.MyViewHolder> {

    private List<SLAmodelNew> allList;
    private Context context;
    String idDetail, idTab, username;
    protected OnPreviewItemClickListener onPreviewItemClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView before, after;
        ImageButton btnVerif;
        TextView keterangan, note, header;

        public MyViewHolder(View view) {
            super(view);
            before = (ImageView) view.findViewById(R.id.imageBefore);
            after = (ImageView) view.findViewById(R.id.imageAfter);
            keterangan = (TextView) view.findViewById(R.id.keterangan);
            btnVerif = (ImageButton) view.findViewById(R.id.imgVerif);
            keterangan = (TextView) view.findViewById(R.id.keterangan);
            note = (TextView) view.findViewById(R.id.notess);
            header = (TextView) view.findViewById(R.id.header);
        }
    }

    public PushSLAVerificationAdapter(Context context, String idDetail, String username, String idTab, List<SLAmodelNew> moviesList,
                                      OnPreviewItemClickListener onPreviewItemClickListener) {
        this.allList = moviesList;
        this.context = context;
        this.idDetail = idDetail;
        this.idTab = idTab;
        this.username = username;
        this.onPreviewItemClickListener = onPreviewItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sla_verification, parent, false);

        return new MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        SLAmodelNew foto = allList.get(position);

        Picasso.with(context).load(foto.getBefore())
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(holder.before);

        Picasso.with(context).load(foto.getAfterString())
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(holder.after);

        if (foto.getVerif().equalsIgnoreCase("1")) {
            holder.btnVerif.setBackground(context.getDrawable(R.drawable.check_ok));
        } else {
            holder.btnVerif.setBackground(context.getDrawable(R.drawable.check_no));
        }

        holder.keterangan.setText(foto.getTitle());
        holder.note.setText(foto.getKet());
        holder.header.setText(foto.getHeader());

        holder.btnVerif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPreviewItemClickListener != null) {
                    onPreviewItemClickListener.onItemClick(v, foto.getId(), null, "changeVerif");
                }
            }
        });
        holder.after.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPreviewItemClickListener != null) {
                    onPreviewItemClickListener.onItemClick(v, foto.getId(), null, "after");
                }
            }
        });
        holder.before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPreviewItemClickListener != null) {
                    onPreviewItemClickListener.onItemClick(v, foto.getId(), null, "before");
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return allList.size();
    }
}
