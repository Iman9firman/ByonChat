package com.byonchat.android.personalRoom.volley;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.android.volley.toolbox.NetworkImageView;
import com.byonchat.android.R;

import org.jetbrains.annotations.NotNull;

/**
 * Created by lukma on 3/29/2016.
 */
public class RoundedNetworkImageView extends NetworkImageView {
    Context mContext;

    public RoundedNetworkImageView(Context context) {
        super(context);
        mContext = context;
    }

    public RoundedNetworkImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
    }

    public RoundedNetworkImageView(Context context, AttributeSet attrs,
                                    int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        if(bm==null) return;
        setImageDrawable(new BitmapDrawable(mContext.getResources(),
                getRoundedCornerBitmapBorder(bm, 10)));
    }

    /**
     * Creates a circular bitmap and uses whichever dimension is smaller to determine the width
     * <br/>Also constrains the circle to the leftmost part of the image
     *
     * @param bitmap
     * @return bitmap
     */

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
