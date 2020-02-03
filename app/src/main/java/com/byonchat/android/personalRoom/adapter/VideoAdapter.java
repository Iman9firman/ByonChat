package com.byonchat.android.personalRoom.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.byonchat.android.R;
import com.byonchat.android.personalRoom.model.PictureModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukma on 3/17/2016.
 */
public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<PictureModel> data = new ArrayList<>();

    public VideoAdapter(Context context, List<PictureModel> data) {
        this.context = context;
        this.data = data;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item_video, parent, false);
        viewHolder = new MyItemHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final PictureModel item = data.get(position);

        Glide.with(context).load(item.getUrl_thumb())
                .override(500, 200)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(((MyItemHolder) holder).mImg);

        ((MyItemHolder) holder).mDuration.setText(item.getDuration());
        ((MyItemHolder) holder).mDuration.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg_my_vidio_list));
    }

    @Override
    public int getItemCount() {
        return data.size();

    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        ImageView mImg;
        TextView mDuration;

        public MyItemHolder(View itemView) {
            super(itemView);

            mImg = (ImageView) itemView.findViewById(R.id.item_img);
            mDuration = (TextView) itemView.findViewById(R.id.duration_item_img);

//            Bitmap bm = ((BitmapDrawable) mImg.getDrawable()).getBitmap();
//            mImg.setImageBitmap(getRoundedCornerBitmapBorder(bm,100));
        }

    }

    public static Bitmap getRoundedCornerBitmapBorder(Bitmap src, float round) {
        Bitmap result = null;
        if (src != null) {
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
