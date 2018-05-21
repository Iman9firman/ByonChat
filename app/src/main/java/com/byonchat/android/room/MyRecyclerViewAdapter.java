package com.byonchat.android.room;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.byonchat.android.R;
import com.byonchat.android.provider.ContentRoom;

import java.util.ArrayList;

/**
 * Created by Iman Firmansyah on 3/21/2016.
 */
public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.DataObjectHolder> {
private static String LOG_TAG = "MyRecyclerViewAdapter";
private ArrayList<ContentRoom> mDataset;
private static MyClickListener myClickListener;
    public Context context;
public static class DataObjectHolder extends RecyclerView.ViewHolder
        implements View
        .OnClickListener {
    TextView label;
    TextView status;

    public DataObjectHolder(View itemView) {
        super(itemView);
        label = (TextView) itemView.findViewById(R.id.titleTxt);
        status = (TextView) itemView.findViewById(R.id.statusTxt);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        myClickListener.onItemClick(getPosition(), v);
    }
}

    
    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public MyRecyclerViewAdapter(ArrayList<ContentRoom> myDataset,Context ctx) {
        mDataset = myDataset;
        context  = ctx;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout_adapter, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.label.setText(mDataset.get(position).getTitle());
        holder.status.setText(String.valueOf(mDataset.get(position).getStatus()));
        Drawable mDrawableLetf = context.getResources().getDrawable(R.drawable.status_work);
        if(!String.valueOf(mDataset.get(position).getStatus()).equalsIgnoreCase("new")){
            mDrawableLetf.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
        }
        holder.status.setBackground(mDrawableLetf);
    }

    public void addItem(ContentRoom dataObj, int index) {
        mDataset.add(dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

public interface MyClickListener {
    public void onItemClick(int position, View v);
}
}