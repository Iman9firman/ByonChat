package com.byonchat.android.Sample.Adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.byonchat.android.FragmentDinamicRoom.DinamicListTaskAdapter;
import com.byonchat.android.R;
import com.byonchat.android.Sample.Database.ScheduleSLADB;
import com.byonchat.android.Sample.DateScheduleSLA;
import com.byonchat.android.Sample.DetailAreaScheduleSLA;
import com.byonchat.android.data.model.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.byonchat.android.ui.fragment.ByonchatScheduleSLAFragment.dpToPx;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private ArrayList<File> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    Context context;
    TextView edt;
    int colorText;
    int backgroundText;

    // data is passed into the constructor
    public ScheduleAdapter(Context context, ArrayList<File> data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.colorText = context.getResources().getColor(R.color.grayDark);
        this.backgroundText = context.getResources().getColor(R.color.tab_text_selected);
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_schedule_sla, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        File fileee = mData.get(position);

        holder.job.setText(fileee.subtitle);
        holder.date.setText(fileee.timestamp);
        holder.jjt.setText(fileee.kode_jjt);
        holder.period.setText(fileee.description);

        Drawable mDrawableLetf = context.getResources().getDrawable(R.drawable.status_work);

        if (fileee.type.equalsIgnoreCase("1")) {
            mDrawableLetf.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.status.setBackground(mDrawableLetf);
                holder.status.setText(" Done ");
                holder.status.setVisibility(View.VISIBLE);

            }
        } else if (fileee.type.equalsIgnoreCase("9")) {
            mDrawableLetf.setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.status.setBackground(mDrawableLetf);
                holder.status.setText("  On Process  ");
                holder.status.setVisibility(View.VISIBLE);
            }

        } else {
            holder.status.setVisibility(View.GONE);
        }


        holder.click_field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentTo(fileee.kode_jjt, fileee.description, fileee.title, fileee.timestamp, fileee.id_detail_area);
            }
        });

    }

    public void intentTo(String jt, String ketrgn, String period, String date, String id_da) {
        Intent dw = new Intent(context, DetailAreaScheduleSLA.class);
        dw.putExtra("jt", jt);
        dw.putExtra("fq", ketrgn);
        dw.putExtra("pr", period);
        dw.putExtra("dt", date);
        dw.putExtra("id", id_da);
        dw.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(dw);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RelativeLayout click_field;
        TextView jjt, date, period, job;
        TextView status;

        ViewHolder(View itemView) {
            super(itemView);
            click_field = (RelativeLayout) itemView.findViewById(R.id.clickField);
            jjt = (TextView) itemView.findViewById(R.id.sch_jjt);
            date = (TextView) itemView.findViewById(R.id.sch_date);
            period = (TextView) itemView.findViewById(R.id.sch_perio);
            job = (TextView) itemView.findViewById(R.id.sch_job);
            status = (TextView) itemView.findViewById(R.id.status);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id).kode_jjt;
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}