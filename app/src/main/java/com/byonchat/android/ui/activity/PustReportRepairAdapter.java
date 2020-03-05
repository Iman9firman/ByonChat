package com.byonchat.android.ui.activity;

import android.content.Context;
import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.byonchat.android.R;
import java.io.File;
import com.byonchat.android.model.Photo;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.ui.adapter.OnPreviewItemClickListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.byonchat.android.utils.Utility.reportCatch;

public class PustReportRepairAdapter extends RecyclerView.Adapter<PustReportRepairAdapter.MyViewHolder> {

    private List<Photo> allList;
    private Context context;
    String idDetail, idTab, username;
    protected OnPreviewItemClickListener onPreviewItemClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView before, after;
        TextView keterangan;

        public MyViewHolder(View view) {
            super(view);
            before = (ImageView) view.findViewById(R.id.imageBefore);
            after = (ImageView) view.findViewById(R.id.imageAfter);
            keterangan = (TextView) view.findViewById(R.id.keterangan);
        }
    }

    public PustReportRepairAdapter(Context context, String idDetail, String username, String idTab, List<Photo> moviesList,
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
                .inflate(R.layout.layout_push_reportrepair_activity, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            Photo foto = allList.get(position);

            Picasso.with(context).load(foto.getBefore())
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(holder.before);

            if (foto.getAfter() != null) {
                Picasso.with(context).load(foto.getAfter())
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .into(holder.after);
            } else {
                BotListDB db = BotListDB.getInstance(context);
                Cursor cursorCild = db.getSingleRoomDetailFormWithFlagContent(idDetail, username, idTab, "reportrepair", foto.getId());

                if (cursorCild.getCount() > 0) {
                    File f = new File(cursorCild.getString(cursorCild.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT)));
                    Picasso.with(context).load(f)
                            .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                            .into(holder.after);
                }
            }
            holder.keterangan.setText(foto.getTitle());
            holder.after.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onPreviewItemClickListener != null) {
                        onPreviewItemClickListener.onItemClick(v, Integer.parseInt(foto.getId()), null, "after");
                    }
                }
            });
            holder.before.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onPreviewItemClickListener != null) {
                        onPreviewItemClickListener.onItemClick(v, Integer.parseInt(foto.getId()), null, "before");
                    }
                }
            });
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    @Override
    public int getItemCount() {
        return allList.size();
    }
}