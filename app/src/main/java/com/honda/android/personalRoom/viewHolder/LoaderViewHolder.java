package com.honda.android.personalRoom.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.honda.android.R;

public class LoaderViewHolder extends RecyclerView.ViewHolder {

    public ProgressBar mProgressBar;

    public LoaderViewHolder(View itemView) {
        super(itemView);
        mProgressBar = (ProgressBar) itemView.findViewById(R.id.progressbar);
    }
}

