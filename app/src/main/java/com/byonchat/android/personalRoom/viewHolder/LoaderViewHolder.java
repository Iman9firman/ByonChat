package com.byonchat.android.personalRoom.viewHolder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.byonchat.android.R;

public class LoaderViewHolder extends RecyclerView.ViewHolder {

    public ProgressBar mProgressBar;

    public LoaderViewHolder(View itemView) {
        super(itemView);
        mProgressBar = (ProgressBar) itemView.findViewById(R.id.progressbar);
    }
}

