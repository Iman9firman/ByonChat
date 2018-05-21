package com.byonchat.android.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;
import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.list.utilLoadImage.ImageLoaderFromSD;
import com.byonchat.android.list.utilLoadImage.ImageLoaderLarge;
import com.byonchat.android.list.utilLoadImage.MemoryCache;
import com.byonchat.android.model.UpdateContactModel;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.TimeLine;
import com.byonchat.android.provider.TimeLineDB;
import com.byonchat.android.utils.MediaProcessingUtil;
import com.byonchat.android.utils.TimeAgo;
import com.byonchat.android.utils.Validations;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class UpdateContactAdapter extends RecyclerView.Adapter<UpdateContactAdapter.ViewHolder> {

    List<UpdateContactModel> mItems;
    MessengerDatabaseHelper dbhelper;
    private static final SimpleDateFormat dateInfoFormat = new SimpleDateFormat(
            "dd/MM/yyyy", Locale.getDefault());
    private static final SimpleDateFormat hourInfoFormat = new SimpleDateFormat(
            "HH:mm", Locale.getDefault());
    private Context context;
    private TimeLineDB timelineDB;

    public UpdateContactAdapter(Context context, ArrayList<TimeLine> timeLines) {
        super();
        mItems = new ArrayList<UpdateContactModel>();
        timelineDB = TimeLineDB.getInstance(context);
        this.context = context;
        if (dbhelper == null) {
            dbhelper = MessengerDatabaseHelper.getInstance(context);
        }

        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DATE), 0, 0, 0);

        for (TimeLine aa : timeLines) {
            Contact contact = dbhelper.getContact(aa.getJabberId());
            if (contact != null) {
                UpdateContactModel nature = new UpdateContactModel();
                if (aa.getJabberId() != null) {
                    nature.setJid(aa.getJabberId());
                }

                if (dbhelper.getMyContact().getJabberId().equalsIgnoreCase(aa.getJabberId())) {
                    nature.setName("Me");
                } else {
                    if (aa.getName() != null) {
                        nature.setName(aa.getName());
                    } else {
                        nature.setName(aa.getJabberId());
                    }

                }
                try {
                    JSONObject json = new JSONObject(aa.getStatus());
                    String action = json.getString("action");
                    String status = json.getString("status");

                    nature.setAction(action);

                    if (action != null) {
                        if (action.equalsIgnoreCase("status")) {
                            if (status != null) {
                                nature.setStatus("Update Status");
                                nature.setContentStatus(status);
                            }
                        } else {
                            nature.setStatus("Changed Profile Photo");
                            nature.setContentStatus(status);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (aa.getStatus() != null) {
                        nature.setStatus("New Status");
                        nature.setContentStatus(aa.getStatus());
                    } else {
                        nature.setStatus("Change Profile Pic");
                    }
                }
                Date d = aa.getSendDate();
                long time = d.getTime();

                DateFormat df = new DateFormat();
                String datetime = df.format("yyyy-MM-dd kk:mm:ss", d).toString();

                String dInfo = null;
                if (d != null) {
                    if (d.getTime() < cal.getTimeInMillis()) {
                        String dayOfTheWeek = (String) android.text.format.DateFormat.format("EEEE", d);
                        String hari = dayOfTheWeek.substring(0, Math.min(dayOfTheWeek.length(), 3));
                        Date dt = null;
                        StringTokenizer tk = new StringTokenizer(datetime);
                        String date = tk.nextToken();
                        String waktu = tk.nextToken();

                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                        SimpleDateFormat sdfs = new SimpleDateFormat("hh:mm a");
                        try {
                            dt = sdf.parse(waktu);
                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        dInfo = hari + " " + sdfs.format(dt);
                    } else {
                        dInfo = TimeAgo.getTimeAgo(time, datetime);
                    }
                }

                nature.setDate(dInfo);
                mItems.add(nature);
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_update_contact, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        UpdateContactModel nature = mItems.get(i);
        viewHolder.name.setText(nature.getName());
        viewHolder.status.setText(nature.getStatus());
        viewHolder.date.setText(nature.getDate());
        File photoFile = context.getFileStreamPath(MediaProcessingUtil
                .getProfilePicName(nature.getJid()));

        Drawable d = context.getResources().getDrawable(R.drawable.ic_no_photo);
        if (photoFile.exists()) {
            d = Drawable.createFromPath(photoFile.getAbsolutePath());
            viewHolder.contentImage.setImageDrawable(d);
        }

        if (nature.getJid().equalsIgnoreCase(dbhelper.getMyContact().getJabberId())){

            Glide.with(context).load(photoFile)
                    .asBitmap().centerCrop()
                    .signature(new StringSignature(Long.toString(System.currentTimeMillis())))
                    .into(new BitmapImageViewTarget(viewHolder.photo) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            viewHolder.photo.setImageBitmap(resource);

                        }
                    });
            if (!nature.getAction().equalsIgnoreCase("status")) {
                Glide.with(context).load(photoFile)
                        .asBitmap().centerCrop()
                        .signature(new StringSignature(Long.toString(System.currentTimeMillis())))
                        .into(new BitmapImageViewTarget(viewHolder.contentImage) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                viewHolder.contentImage.setImageBitmap(resource);

                            }
                        });
            }

        }else {

            String signature =  new Validations().getInstance(context).getSignatureProfilePicture(nature.getJid(),dbhelper);
            Glide.with(context).load("https://" + MessengerConnectionService.F_SERVER + "/toboldlygowherenoonehasgonebefore/" + nature.getJid() + ".jpg").asBitmap()
                    .centerCrop()
                    .signature(new StringSignature(signature))
                    .placeholder(d)
                    .animate(R.anim.fade_in_sort)
                    .into(new BitmapImageViewTarget(viewHolder.photo) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            viewHolder.photo.setImageBitmap(resource);

                        }
                    });

            if (!nature.getAction().equalsIgnoreCase("status")) {
                Glide.with(context).load("https://" + MessengerConnectionService.F_SERVER + "/toboldlygowherenoonehasgonebefore/" + nature.getJid() + ".jpg").asBitmap()
                        .centerCrop()
                        .signature(new StringSignature(signature))
                        .placeholder(d)
                        .animate(R.anim.fade_in_sort)
                        .into(new BitmapImageViewTarget(viewHolder.contentImage) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                viewHolder.contentImage.setImageBitmap(resource);

                            }
                        });
            }

        }


        if (nature.getAction().equalsIgnoreCase("status")) {
            viewHolder.contentImage.setVisibility(View.GONE);
            viewHolder.contentStatus.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(30, 20, 30, 0);
            viewHolder.cardView.setLayoutParams(layoutParams);
            viewHolder.contentStatus.setText(nature.getContentStatus());
        } else {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(30, 20, 30, 0);
            viewHolder.cardView.setLayoutParams(layoutParams);
            viewHolder.contentImage.setVisibility(View.VISIBLE);
            viewHolder.contentStatus.setVisibility(View.GONE);

        }

        timelineDB.updateUserFlag(nature.getJid(), "0");
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView photo;
        public ImageView contentImage;
        public TextView name;
        public TextView status;
        public TextView date;
        public TextView contentStatus;
        public CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            photo = (ImageView) itemView.findViewById(R.id.photo);
            contentImage = (ImageView) itemView.findViewById(R.id.content_image);
            name = (TextView) itemView.findViewById(R.id.txt_name);
            status = (TextView) itemView.findViewById(R.id.txt_status);
            date = (TextView) itemView.findViewById(R.id.txt_date);
            contentStatus = (TextView) itemView.findViewById(R.id.content_status);
            cardView = (CardView) itemView.findViewById(R.id.cardview);
        }
    }
}


