package com.byonchat.android.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import android.widget.CheckBox;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.ISSActivity.Requester.RequesterBaseRatingActivity;
import com.byonchat.android.R;
import com.byonchat.android.data.model.MkgServices;
import com.byonchat.android.tabRequest.RelieverDetailActivity;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

import static com.byonchat.android.utils.Utility.reportCatch;

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

    @View(R.id.child_checkbox)
    CheckBox child_checkbox;

    @View(R.id.clicked)
    RelativeLayout frame_content;

    private Context mContext;
    private MkgServices data;
    private int position;
//    ArrayList<String> daftar_checked = new ArrayList<>();

    private OnCheckedChangeListener itemClickListener;

    public ChildRatingRecyclerView(RequesterBaseRatingActivity mContext, int j, MkgServices data, OnCheckedChangeListener checkedChangeListener) {
        this.mContext = mContext;
        this.data = data;
        this.position = position;
        this.itemClickListener = checkedChangeListener;
    }

    @RequiresApi(21)
    @Resolve
    private void onResolve() {
        try {
            child_rating.setRating(Float.parseFloat(data.child_rating));
            child_rating.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.orange_300)));
            child_text_name.setText(data.child_name);
            child_text_distance.setText(data.child_distance);
            if (data.total_kerja.equalsIgnoreCase("0") || data.total_kerja.equalsIgnoreCase("")) {
                child_text_total.setText("pekerja baru");
            } else {
                child_text_total.setText(data.total_kerja + " x bekerja");
            }

            if (data.isChecked) {
                child_checkbox.setChecked(true);
            } else {
                child_checkbox.setChecked(false);
            }

            child_checkbox.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(android.view.View v) {
                    int jjsss = ((RequesterBaseRatingActivity) mContext).getCountCheck(data.header_id);
                    if (jjsss > 0 && child_checkbox.isChecked()) {
                        Toast.makeText(mContext, "Hanya dapat memilih " + jjsss + " reliever.", Toast.LENGTH_SHORT).show();
                        child_checkbox.setChecked(false);
                    } else {
                        if (itemClickListener != null) {
                            itemClickListener.onItemClick(position, data, child_checkbox.isChecked());
                        }
                    }
                }
            });


            frame_content.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(android.view.View v) {
                    Intent goToDetail = new Intent(mContext, RelieverDetailActivity.class);
                    goToDetail.putExtra("IDRELIEVER", data.id_reliever);
                    mContext.startActivity(goToDetail);
                }
            });
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    public interface OnCheckedChangeListener {
        void onItemClick(int position, MkgServices data, Boolean check);
    }
}