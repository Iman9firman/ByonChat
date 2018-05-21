/*
 * Code based off the PhotoSortrView from Luke Hutchinson's MTPhotoSortr
 * example (http://code.google.com/p/android-multitouch-controller/)
 *
 * License:
 *   Dual-licensed under the Apache License v2 and the GPL v2.
 */
package com.byonchat.android.createMeme;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;


public class ImageEntity extends MultiTouchEntity {

   // private static final double INITIAL_SCALE_FACTOR = 0.15;
    private static final double INITIAL_SCALE_FACTOR = 0.50;

    private Bitmap mResourceId;

    public ImageEntity(Bitmap resourceId, Resources res)  {
        super(res);

        mResourceId = resourceId;
    }

    /*public ImageEntity(ImageEntity e, Resources res) {
        super(res);

        mDrawable = e.mDrawable;
        mResourceId = e.mResourceId;
        mScaleX = e.mScaleX;
        mScaleY = e.mScaleY;
        mCenterX = e.mCenterX;
        mCenterY = e.mCenterY;
        mAngle = e.mAngle;
    }*/

    public void draw(Canvas canvas) {
        canvas.save();

        Paint itemPaint = new Paint();
        itemPaint.setAntiAlias(true);
        itemPaint.setFilterBitmap(true);

        float dx = (mMaxX + mMinX) / 2;
        float dy = (mMaxY + mMinY) / 2;

        canvas.save();

        canvas.translate(dx, dy);
        canvas.rotate(mAngle * 180.0f / (float) Math.PI);
        canvas.translate(-dx, -dy);

        if(mResourceId == null) return;
        Rect srcRect = new Rect(0, 0, mResourceId.getWidth(), mResourceId.getHeight());
        Rect dstRect = new Rect((int) mMinX, (int) mMinY, (int) mMaxX, (int) mMaxY);
        canvas.drawBitmap(mResourceId, srcRect, dstRect, null);

        canvas.restore();
    }

    /**
     * Called by activity's onPause() method to free memory used for loading the images
     */
    @Override
    public void unload() {
        this.mResourceId = null;
    }

    /** Called by activity's onResume() method to load the images */
    @Override
    public void load(Context context, float startMidX, float startMidY) {
        Resources res = context.getResources();
        getMetrics(res);

        mStartMidX = startMidX;
        mStartMidY = startMidY;
        mWidth = mResourceId.getWidth();
        mHeight = mResourceId.getHeight();
      /*  mDrawable = res.getDrawable(mResourceId);

        mWidth = mDrawable.getIntrinsicWidth();
        mHeight = mDrawable.getIntrinsicHeight();*/

        float centerX;
        float centerY;
        float scaleX;
        float scaleY;
        float angle;
        if (mFirstLoad) {
            centerX = startMidX;
            centerY = startMidY;

            float scaleFactor = (float) (Math.max(mDisplayWidth, mDisplayHeight) /
                    (float) Math.max(mWidth, mHeight) * INITIAL_SCALE_FACTOR);
            scaleX = scaleY = scaleFactor;
            angle = 0.0f;

            mFirstLoad = false;
        } else {
            centerX = mCenterX;
            centerY = mCenterY;
            scaleX = mScaleX;
            scaleY = mScaleY;
            angle = mAngle;
        }
        setPos(centerX, centerY, scaleX, scaleY, mAngle);
    }
}
