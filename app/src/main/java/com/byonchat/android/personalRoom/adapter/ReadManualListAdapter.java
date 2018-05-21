package com.byonchat.android.personalRoom.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity;
import com.byonchat.android.MediaPlayCatalogActivity;
import com.byonchat.android.NewsDetailActivity;
import com.byonchat.android.R;
import com.byonchat.android.SearchThemesActivity;
import com.byonchat.android.SkinSelectorActivity;
import com.byonchat.android.adapter.CircularContactView;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.list.ItemListSearchTheme;
import com.byonchat.android.list.ListSearchThemesAdapter;
import com.byonchat.android.personalRoom.NoteCommentActivity;
import com.byonchat.android.personalRoom.asynctask.ProfileSaveDescription;
import com.byonchat.android.personalRoom.model.NewsFeedItem;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.ContentRoom;
import com.byonchat.android.provider.IntervalDB;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.provider.Skin;
import com.byonchat.android.utils.OnLoadMoreListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

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


