package com.honda.android.personalRoom.adapter;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.honda.android.R;
import com.honda.android.adapter.CircularContactView;
import com.honda.android.provider.ContentRoom;

import java.util.List;

/**
 * Created by lukma on 3/7/2016.
 */
public class ReadManualListAdapter extends RecyclerView.Adapter<ReadManualListAdapter.MyViewHolder> {

    private List<ContentRoom> moviesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView label;
        CircularContactView imageText;
        TextView status;
        ImageView button_popup;

        public MyViewHolder(View view) {
            super(view);

            imageText = (CircularContactView) view.findViewById(R.id.imageText);
            label = (TextView) view.findViewById(R.id.titleText);
            status = (TextView) view.findViewById(R.id.textInfo);
            button_popup = (ImageView) view.findViewById(R.id.button_popup);

        }
    }


    public ReadManualListAdapter(List<ContentRoom> moviesList) {
        this.moviesList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_read_manual, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ContentRoom movie = moviesList.get(position);
        holder.label.setText(movie.getTitle());

        holder.button_popup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }

        });

    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}


