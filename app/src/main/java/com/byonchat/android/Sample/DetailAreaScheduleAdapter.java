package com.byonchat.android.Sample;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.R;
import com.byonchat.android.Sample.Database.ScheduleSLADB;
import com.byonchat.android.ui.adapter.OnPreviewItemClickListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import static com.byonchat.android.Sample.DetailAreaScheduleSLA.link_pic;

public class DetailAreaScheduleAdapter extends RecyclerView.Adapter<DetailAreaScheduleAdapter.MyViewHolder> {

    private ArrayList<DetailArea> detailareaList;
    private Activity mActivity;

    int colorText, backgroundText;

    protected ClickListener clickListener;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView start, proses, done;
        public ImageView cekstart, cekproses, cekdone;
        ProgressBar progressBarStart, progressBarProgress, progressBarDone;
        Button button_submit_start, button_submit_process, button_submit_done;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.txtDeatailArea);

            start = (ImageView) view.findViewById(R.id.logo_item_grid);
            proses = (ImageView) view.findViewById(R.id.logo_item_grid2);
            done = (ImageView) view.findViewById(R.id.logo_item_grid3);

            cekstart = (ImageView) view.findViewById(R.id.start_check);
            cekproses = (ImageView) view.findViewById(R.id.proses_check);
            cekdone = (ImageView) view.findViewById(R.id.done_check);

            progressBarStart = (ProgressBar) itemView.findViewById(R.id.progressBarStart);
            progressBarProgress = (ProgressBar) itemView.findViewById(R.id.progressBarProgres);
            progressBarDone = (ProgressBar) itemView.findViewById(R.id.progressBarDone);


            button_submit_start = (Button) view.findViewById(R.id.button_submit_start);
            button_submit_process = (Button) view.findViewById(R.id.button_submit_process);
            button_submit_done = (Button) view.findViewById(R.id.button_submit_done);


        }
    }

    public DetailAreaScheduleAdapter(Activity activity, ArrayList<DetailArea> detailareaList, ClickListener _clickListener) {
        this.clickListener = _clickListener;
        this.detailareaList = detailareaList;
        this.mActivity = activity;
        this.colorText = activity.getResources().getColor(R.color.grayDark);
        this.backgroundText = activity.getResources().getColor(R.color.tab_text_selected);
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
        holder.title.setTextColor(colorText);


        holder.cekdone.setVisibility(View.GONE);
        holder.cekstart.setVisibility(View.GONE);
        holder.cekproses.setVisibility(View.GONE);

        holder.button_submit_start.setVisibility(View.GONE);
        holder.button_submit_process.setVisibility(View.GONE);
        holder.button_submit_done.setVisibility(View.GONE);


        holder.progressBarStart.setVisibility(View.GONE);
        holder.progressBarProgress.setVisibility(View.GONE);
        holder.progressBarDone.setVisibility(View.GONE);


        String img_start = "null";
        String img_proses = "null";
        String img_done = "null";

        Cursor cursorBot = ScheduleSLADB.getInstance(mActivity).getDataPicByID(detailArea.getId());
        if (cursorBot.getCount() > 0) {
            img_start = cursorBot.getString(cursorBot.getColumnIndex(ScheduleSLADB.SCH_DATA_START_PIC));
            img_proses = cursorBot.getString(cursorBot.getColumnIndex(ScheduleSLADB.SCH_DATA_PROSES_PIC));
            img_done = cursorBot.getString(cursorBot.getColumnIndex(ScheduleSLADB.SCH_DATA_DONE_PIC));

        }
        cursorBot.close();

        if (detailArea.getImg_start() == "null") {
            detailArea.setImg_start(img_start);
        }

        if (detailArea.getImg_proses() == "null") {
            detailArea.setImg_proses(img_proses);
        }

        if (detailArea.getImg_done() == "null") {
            detailArea.setImg_done(img_done);
        }


        if (!detailArea.getImg_start().equalsIgnoreCase("null")) {
            if (detailArea.getImg_start().startsWith("/storage")) {
                Picasso.with(mActivity).load(new File(detailArea.getImg_start()))
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .into(holder.start);
                holder.button_submit_start.setVisibility(View.VISIBLE);
            } else {
                holder.progressBarStart.setVisibility(View.VISIBLE);
                Picasso.with(mActivity).load(link_pic + detailArea.getImg_start())
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .into(holder.start);
                holder.cekstart.setVisibility(View.VISIBLE);

            }

        }

        if (!detailArea.getImg_proses().equalsIgnoreCase("null")) {
            if (detailArea.getImg_proses().startsWith("/storage")) {
                Picasso.with(mActivity).load(new File(detailArea.getImg_proses()))
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .into(holder.proses);
                holder.button_submit_process.setVisibility(View.VISIBLE);
            } else {
                holder.progressBarProgress.setVisibility(View.VISIBLE);
                Picasso.with(mActivity).load(link_pic + detailArea.getImg_proses())
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .into(holder.proses);
                holder.cekproses.setVisibility(View.VISIBLE);

            }

        }

        if (!detailArea.getImg_done().equalsIgnoreCase("null")) {
            if (detailArea.getImg_done().startsWith("/storage")) {
                Picasso.with(mActivity).load(new File(detailArea.getImg_done()))
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .into(holder.done);
                holder.button_submit_done.setVisibility(View.VISIBLE);
            } else {
                holder.progressBarDone.setVisibility(View.VISIBLE);
                Picasso.with(mActivity).load(link_pic + detailArea.getImg_done())
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .into(holder.done);
                holder.cekdone.setVisibility(View.VISIBLE);

            }

        }

        holder.start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dsc = new Intent(mActivity, DialogAddSchedulePicture.class);
                dsc.putExtra("id_da", detailArea.getId());
                dsc.putExtra("id_jjt", detailArea.getId_jjt());
                dsc.putExtra("tt", detailArea.getTitle());
                dsc.putExtra("fq", detailArea.getFrekuensi());
                dsc.putExtra("pr", detailArea.getPeriod());
                dsc.putExtra("post", "start");
                mActivity.startActivity(dsc);
            }
        });

        holder.proses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!detailArea.getImg_start().equalsIgnoreCase("null")) {
                    Intent dsc = new Intent(mActivity, DialogAddSchedulePicture.class);
                    dsc.putExtra("id_da", detailArea.getId());
                    dsc.putExtra("id_jjt", detailArea.getId_jjt());
                    dsc.putExtra("tt", detailArea.getTitle());
                    dsc.putExtra("fq", detailArea.getFrekuensi());
                    dsc.putExtra("pr", detailArea.getPeriod());
                    dsc.putExtra("post", "proses");
                    mActivity.startActivity(dsc);
                } else {
                    Toast.makeText(mActivity, "Add The Start Image First", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!detailArea.getImg_proses().equalsIgnoreCase("null")) {
                    Intent dsc = new Intent(mActivity, DialogAddSchedulePicture.class);
                    dsc.putExtra("id_da", detailArea.getId());
                    dsc.putExtra("id_jjt", detailArea.getId_jjt());
                    dsc.putExtra("tt", detailArea.getTitle());
                    dsc.putExtra("fq", detailArea.getFrekuensi());
                    dsc.putExtra("pr", detailArea.getPeriod());
                    dsc.putExtra("post", "done");
                    mActivity.startActivity(dsc);
                } else {
                    Toast.makeText(mActivity, "Add The Process Image First", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.button_submit_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onClick("start");
                }
            }
        });
        holder.button_submit_process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onClick("on_proses");
                }
            }
        });
        holder.button_submit_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onClick("done");
                }
            }
        });

    }

    public static interface ClickListener {
        public void onClick(String pos);
    }


    @Override
    public int getItemCount() {
        return detailareaList.size();
    }
}
