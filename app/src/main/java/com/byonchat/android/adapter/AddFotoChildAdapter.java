package com.byonchat.android.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity;
import com.byonchat.android.R;
import com.byonchat.android.model.AddChildFotoExModel;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.utils.ImageFilePath;
import com.byonchat.android.utils.MediaProcessingUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.github.memfis19.annca.Annca;
import io.github.memfis19.annca.internal.configuration.AnncaConfiguration;

public class AddFotoChildAdapter extends RecyclerView.Adapter<AddFotoChildAdapter.ViewHolder> {
    Context context;
    Activity activity;
    private List<AddChildFotoExModel> users_models;
    private static final int REQ_CAMERA = 12011;

    public AddFotoChildAdapter(Activity activity, Context context, List<AddChildFotoExModel> users_models) {
        this.users_models = users_models;
        this.context = context;
        this.activity = activity;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_child_foto, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.w("uus", users_models.get(position).getChildListText());
        if (!users_models.get(position).getChildListText().equalsIgnoreCase("add")) {
            holder.Image.setBackground(null);
            Picasso.with(context).load("file:////storage/emulated/0/Pictures/com.byonchat.android" + users_models.get(position).getChildListText()).into(holder.Image);
        } else {
            Picasso.with(context).load(R.drawable.ic_att_photo).into(holder.Image);
            holder.Image.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("MissingPermission")
                @Override
                public void onClick(View v) {
                    Log.w("ada1", "tidak");

                    if (activity instanceof DinamicRoomTaskActivity) {
                        ((DinamicRoomTaskActivity) activity).yourActivityMethod(users_models.get(position));
                    }

                    // UserModel users_model = users_models.get(position);
                    /*if (activity instanceof InviteUserGroupActivity) {
                        InviteUserGroupActivity mainActivity = (InviteUserGroupActivity) activity;
                        mainActivity.AddedUser(users_model.getId(),
                                users_model.getCommentUser(),
                                users_model.getNamaUser(),
                                users_model.getUrlUser(), true);
                        // ketika di klik 2 kali ?
                        if (users_model.isStatus() == true) {
                            mainActivity.UpdateStatusList(users_model.getId(), false);

                        } else if (users_model.isStatus() == false) {
                            mainActivity.UpdateStatusList(users_model.getId(), true);

                        }
                    }*/
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return users_models.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView Image;

        public ViewHolder(final View itemView) {
            super(itemView);
            Image = (ImageView) itemView.findViewById(R.id.Image);
        }
    }
}
