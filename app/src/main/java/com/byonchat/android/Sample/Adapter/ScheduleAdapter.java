package com.byonchat.android.Sample.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.byonchat.android.R;
import com.byonchat.android.Sample.DateScheduleSLA;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.byonchat.android.ui.fragment.ByonchatScheduleSLAFragment.dpToPx;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private ArrayList<String> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    Context context;
    TextView edt;

    // data is passed into the constructor
    public ScheduleAdapter(Context context, ArrayList<String> data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_layout_schedule_list, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String jjt = mData.get(position);

        try {

            JSONObject jsonObject = new JSONObject(jjt);
            String jjt_loc = jsonObject.getString("jjt_location");
            holder.title_jjt.setText(jjt_loc);
            JSONArray jsonArray = jsonObject.getJSONArray("periode");
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String id = jsonObject1.getString("id");
                String jt = jsonObject1.getString("kode_jjt");
                String period = jsonObject1.getString("periode");
                String freq = "";
                if(jsonObject1.has("frequency")) {
                    freq = jsonObject1.getString("frequency");
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
                edt.setTextSize(20);
                edt.setGravity(Gravity.CENTER|Gravity.LEFT);
                holder.lineR_period.addView(edt,params1);

                View view = new View(context);
                view.setBackgroundColor(context.getResources().getColor(R.color.grey));
                holder.lineR_period.addView(view, params2);

                String finalFreq = freq;
                edt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intentTo(jt, finalFreq, period, jjt_loc2);
                    }
                });
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void intentTo(String jt, String freq, String period, String jjt_loc2){
        Intent dw = new Intent(context, DateScheduleSLA.class);
        dw.putExtra("jt",jt);
        dw.putExtra("fq",freq);
        dw.putExtra("pr",period);
        dw.putExtra("tt",jjt_loc2);
        context.startActivity(dw);
    }
    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout lineR_period, lineR_jjt;
        TextView title_jjt;

        ViewHolder(View itemView) {
            super(itemView);
            lineR_jjt = (LinearLayout) itemView.findViewById(R.id.nonRecycler);
            title_jjt = (TextView) itemView.findViewById(R.id.title);
            lineR_period = (LinearLayout) itemView.findViewById(R.id.linearData);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
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