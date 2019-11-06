package com.byonchat.android.Sample.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.byonchat.android.R;
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

        holder.click_field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentTo(fileee.kode_jjt, fileee.description, fileee.title, fileee.timestamp, fileee.id_detail_area);
            }
        });

        /*try {

            JSONObject jsonObject = new JSONObject(jjt);
            String jjt_loc = jsonObject.getString("jjt_location");
            holder.title_jjt.setText(jjt_loc);
            holder.title_jjt.setTextColor(colorText);
            JSONArray jsonArray = jsonObject.getJSONArray("periode");
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String id = jsonObject1.getString("id");
                String jt = jsonObject1.getString("kode_jjt");
                String period = jsonObject1.getString("periode");
                String ketrgn = "";
                if(jsonObject1.has("keterangan")) {
                    ketrgn = jsonObject1.getString("keterangan");
                }
                String floor = jsonObject1.getString("floor");
                String sd = jsonObject1.getString("start_date");
                String ed = jsonObject1.getString("end_date");
                String jjt_loc2 = jsonObject1.getString("jjt_location");

                int idd = Integer.parseInt(id);

                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(50));
                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(1));
                edt = new TextView(context);
                edt.setText(period);
                edt.setTextColor(colorText);
                edt.setBackgroundColor(backgroundText);
                edt.setTextSize(20);
                edt.setGravity(Gravity.CENTER|Gravity.LEFT);
                holder.lineR_period.addView(edt,params1);

                View view = new View(context);
                view.setBackgroundColor(context.getResources().getColor(R.color.grey));
                holder.lineR_period.addView(view, params2);

                String finalFreq = ketrgn;
                edt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intentTo(jt, finalFreq, period, jjt_loc2);
                    }
                });
            }
        } catch (JSONException e){
            e.printStackTrace();
        }*/
    }

    public void intentTo(String jt, String ketrgn, String period, String date, String id_da){
        Intent dw = new Intent(context, DetailAreaScheduleSLA.class);
        dw.putExtra("jt",jt);
        dw.putExtra("fq",ketrgn);
        dw.putExtra("pr",period);
        dw.putExtra("dt",date);
        dw.putExtra("id",id_da);
        dw.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(dw);
    }
    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        LinearLayout lineR_period, lineR_jjt;
//        TextView title_jjt;
            RelativeLayout click_field;
            TextView jjt, date, period, job;

        ViewHolder(View itemView) {
            super(itemView);
//            lineR_jjt = (LinearLayout) itemView.findViewById(R.id.nonRecycler);
//            title_jjt = (TextView) itemView.findViewById(R.id.title);
//            lineR_period = (LinearLayout) itemView.findViewById(R.id.linearData);
            click_field = (RelativeLayout)  itemView.findViewById(R.id.clickField);
            jjt = (TextView)  itemView.findViewById(R.id.sch_jjt);
            date = (TextView)  itemView.findViewById(R.id.sch_date);
            period = (TextView)  itemView.findViewById(R.id.sch_perio);
            job = (TextView)  itemView.findViewById(R.id.sch_job);
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