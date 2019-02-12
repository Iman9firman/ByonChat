package com.byonchat.android.ui.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.byonchat.android.R;
import com.byonchat.android.data.model.MkgServices;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

@Layout(R.layout.mkg_child_rating_layout)
public class ChildRatingRecyclerView {

    @View(R.id.child_name)
    TextView child_text_name;

    @View(R.id.child_rating)
    RatingBar child_rating;

    @View(R.id.child_distance)
    TextView child_text_distance;

    @View(R.id.child_total)
    TextView child_text_total;

    private Context mContext;
    private MkgServices data;
    private int position;

    private OnItemClickListener itemClickListener;

    public ChildRatingRecyclerView(Context mContext, int position, MkgServices data, OnItemClickListener itemClickListener) {
        this.mContext = mContext;
        this.data = data;
        this.position = position;
        this.itemClickListener = itemClickListener;
    }

    @RequiresApi(21)
    @Resolve
    private void onResolve() {
        child_rating.setRating(Float.parseFloat(data.child_rating));
        child_rating.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.orange_300)));
        child_text_name.setText(data.child_name);
        child_text_distance.setText(data.child_distance);
        if(data.total_kerja.equalsIgnoreCase("0")){
            child_text_total.setText("pekerja baru");
        }else {
            child_text_total.setText(data.total_kerja + " X bekerja");
        }
    }

    @Click(R.id.child_checkbox)
    public void onItemClick() {
        if (itemClickListener != null) {
            itemClickListener.onItemClick(position, data);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, MkgServices data);
    }
}