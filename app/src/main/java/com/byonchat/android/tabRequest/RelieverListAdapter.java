package com.byonchat.android.tabRequest;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class RelieverListAdapter extends RecyclerView.Adapter<RelieverListAdapter.RelieverHolder> {

    ArrayList<Reliever> relievers;
    Context context;
    int maxSelect;
    int z = 0;

    public RelieverListAdapter(Context context , ArrayList<Reliever> relievers , int maxSelect){
        this.relievers = relievers;
        this.context = context;
        this.maxSelect = maxSelect;
    }

    @NonNull
    @Override
    public RelieverHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.reliever_list_layout,parent,false);
        return new RelieverHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RelieverHolder holder, int pos) {

        Reliever r = relievers.get(pos);
        DecimalFormat df = new DecimalFormat("#.00");
        holder.textName.setText(r.getRelieverName());
        holder.textDistance.setText(df.format(Float.valueOf(r.getRelieverDistance()))+" km");
        holder.rateBar.setRating(Float.valueOf(r.getRelieverDistance()));
        holder.checkBox.setChecked(r.isRelieverSelected());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToDetail = new Intent(context,RelieverDetailActivity.class);
                context.startActivity(goToDetail);
            }
        });

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton checkboxView, boolean isChecked) {
                Log.w("Proudly prestice 1","ok");
                if (isChecked){
                    Log.w("Proudly prestice 13",z+"");
                    z++;
                } else {
                    Log.w("Proudly prestice 12",z+"");
                    z--;
                }
                if (z > maxSelect){
                    Log.w("Proudly prestice 10",maxSelect+"");
                    Log.w("Proudly prestice 11",z+"");
                    Toast.makeText(context,"Hanya dapat memilih "+maxSelect+" reliever.", Toast.LENGTH_SHORT).show();
                    checkboxView.setChecked(false);
                    z--;
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return relievers.size();
    }

    class RelieverHolder extends RecyclerView.ViewHolder{
        TextView textName;
        TextView textDistance;
        RatingBar rateBar;
        CheckBox checkBox;
        private RelieverHolder(View v) {
            super(v);
            textName = (TextView)  v.findViewById(R.id.text_name_reliever);
            textDistance = (TextView) v.findViewById(R.id.text_distance_reliever);
            rateBar = (RatingBar) v.findViewById(R.id.rating_reliever);
            checkBox = (CheckBox) v.findViewById(R.id.checkbox_reliever);
        }
    }
}
