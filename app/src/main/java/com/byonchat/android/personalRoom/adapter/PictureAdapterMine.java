package com.byonchat.android.personalRoom.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.byonchat.android.R;
import com.byonchat.android.personalRoom.model.PictureModel;
import com.byonchat.android.personalRoom.view.SelectableRoundedImageView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Suleiman19 on 10/22/15.
 */
public class PictureAdapterMine extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<PictureModel> data;
    private final int VIEW_ITEM = 1;
    private final int VIEW_BUTTON = 0;
    private static MyClickListenerLongClick myClickListenerLongClick;

    public PictureAdapterMine(Context context, List<PictureModel> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        if (viewType == VIEW_ITEM) {
            v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.list_item_picture, parent, false);
            viewHolder = new MyItemHolder(v);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.button_item, parent, false);
            viewHolder = new ButtonUploadHolder(v);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyItemHolder) {
            final PictureModel item = data.get(position);

            if(item.getDrawable() == 0) {
                Picasso.with(context)
                        .load(item.getUrl())
                        .noFade()
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .placeholder(R.drawable.bt_no_image)
                        .into(((MyItemHolder) holder).mImg);
            }else{
                Picasso.with(context)
                        .load(item.getDrawable())
                        .noFade()
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .placeholder(item.getDrawable())
                        .into(((MyItemHolder) holder).mImg);
            }
        } else {
            Picasso.with(context)
                    .load(R.drawable.bt_add_image)
                    .noFade()
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .placeholder(R.drawable.bt_no_image)
                    .into(((ButtonUploadHolder) holder).mImg);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position) != null ? VIEW_ITEM : VIEW_BUTTON;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        Target mImg;

        public MyItemHolder(View itemView) {
            super(itemView);
            mImg = (Target) itemView.findViewById(R.id.item_img);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    myClickListenerLongClick.onLongClick(getPosition(), v);
                    return true;
                }
            });
        }
    }

    public static class ButtonUploadHolder extends RecyclerView.ViewHolder {
        Target mImg;

        public ButtonUploadHolder(View itemView) {
            super(itemView);

            mImg = (Target) itemView.findViewById(R.id.item_img);
        }
    }


    public void setOnLongClickListener(MyClickListenerLongClick myClickListenerLongClick) {
        this.myClickListenerLongClick = myClickListenerLongClick;
    }

    public interface MyClickListenerLongClick {
        public void onLongClick(int position, View v);
    }

    public static Bitmap getRoundedCornerBitmapBorder(Bitmap src, float round) {
        Bitmap result = null;
        if (src!=null) {
            // Source image size
            int width = src.getWidth();
            int height = src.getHeight();
            // create result bitmap output
            result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            // set canvas for painting
            Canvas canvas = new Canvas(result);
            canvas.drawARGB(0, 0, 0, 0);


            // configure paint
            final Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);

            // configure rectangle for embedding
            final Rect rect = new Rect(0, 0, width, height);
            final RectF rectF = new RectF(rect);

            // draw Round rectangle to canvas
            canvas.drawRoundRect(rectF, round, round, paint);

            // create Xfer mode
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            // draw source image to canvas
            canvas.drawBitmap(src, rect, rect, paint);
        }
        // return final image
        return result;
    }


}
