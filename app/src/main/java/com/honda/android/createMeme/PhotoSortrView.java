/**
 * PhotoSorterView.java
 *
 * (c) Luke Hutchison (luke.hutch@mit.edu)
 *
 * TODO: Add OpenGL acceleration.
 *
 * Released under the Apache License v2.
 */
package com.honda.android.createMeme;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.honda.android.createMeme.MultiTouchController.MultiTouchObjectCanvas;
import com.honda.android.createMeme.MultiTouchController.PointInfo;
import com.honda.android.createMeme.MultiTouchController.PositionAndScale;

import java.util.ArrayList;

public class PhotoSortrView extends View implements MultiTouchObjectCanvas<MultiTouchEntity> {


	private ArrayList<MultiTouchEntity> mImages = new ArrayList<MultiTouchEntity>();

	// --

	private MultiTouchController<MultiTouchEntity> multiTouchController =
        new MultiTouchController<MultiTouchEntity>(this);

	// --

	private PointInfo currTouchPoint = new PointInfo();

	private boolean mShowDebugInfo = true;

	private static final int UI_MODE_ROTATE = 1, UI_MODE_ANISOTROPIC_SCALE = 2;

	private int mUIMode = UI_MODE_ROTATE;

	// --

	private Paint mLinePaintTouchPointCircle = new Paint();
    private Paint mLinePaintTouchPointCircle2 = new Paint();

    private static final float SCREEN_MARGIN = 100;

    private int width, height, displayWidth, displayHeight;

	// ---------------------------------------------------------------------------------------------------

	public PhotoSortrView(Context context) {
		this(context, null);
	}

	public PhotoSortrView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PhotoSortrView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}


    /** Called by activity's onResume() method to load the images */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setCanvas(Context context,Bitmap background) {

		mLinePaintTouchPointCircle.setColor(Color.TRANSPARENT);
		mLinePaintTouchPointCircle2.setColor(Color.TRANSPARENT);

        Resources res = context.getResources();
      /*  Drawable myIcon = getResources().getDrawable( R.drawable.iman );*/
        BitmapDrawable ob = new BitmapDrawable(getResources(), background);
        setBackground(ob);

        DisplayMetrics metrics = res.getDisplayMetrics();
        this.displayWidth = res.getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE ? Math.max(metrics.widthPixels,
                metrics.heightPixels) : Math.min(metrics.widthPixels, metrics.heightPixels);
        this.displayHeight = res.getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE ? Math.min(metrics.widthPixels,
                metrics.heightPixels) : Math.max(metrics.widthPixels, metrics.heightPixels);
    }



    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void changeBackground(Bitmap background){
        BitmapDrawable ob = new BitmapDrawable(getResources(), background);
       setBackground(ob);
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void changeBackgroundDefault(Bitmap background){
        BitmapDrawable ob = new BitmapDrawable(getResources(),background);
       setBackground(ob);
    }




	/** Called by activity's onResume() method to load the images */
	public void loadImages(Context context) {
    /*    int n = mImages.size();
		for (int i = 0; i < n; i++) {
            float cx = SCREEN_MARGIN + (float)
                (Math.random() * (displayWidth - 2 * SCREEN_MARGIN));
            float cy = SCREEN_MARGIN + (float)
                (Math.random() * (displayHeight - 2 * SCREEN_MARGIN));
			mImages.get(i).load(context, cx, cy);
        }*/
        int n = mImages.size();
        for (int i = 0; i < n; i++) {
            float cx = (float)
                    (((displayWidth/2)));
            float cy = (float)
                    (((displayWidth/2)));
            mImages.get(i).load(context, cx, cy);
        }

	}

	/** Called by activity's onPause() method to free memory used for loading the images */
	public void unloadImages() {
		int n = mImages.size();
		for (int i = 0; i < n; i++)
			mImages.get(i).unload();
	}

	// ---------------------------------------------------------------------------------------------------

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int n = mImages.size();
		for (int i = 0; i < n; i++)
			mImages.get(i).draw(canvas);
		if (mShowDebugInfo)
			drawMultitouchDebugMarks(canvas);

	}



	// ---------------------------------------------------------------------------------------------------

    public void addImage(Context context,Bitmap image){
        Resources res = this.getContext().getResources();
        mImages.add(new ImageEntity(image, res));
        loadImages(context);
        invalidate();
    }


    public void deleteImage(Context context,int imageId){
        mImages.remove(imageId);
        loadImages(context);
        invalidate();
    }

    public String count(){
        String count = null;
        if(mImages.size()>0){
            count = String.valueOf(mImages.size()-1);
        }
        return count;
    }

    public void trackballClicked() {
		mUIMode = (mUIMode + 1) % 3;
		invalidate();
	}

	private void drawMultitouchDebugMarks(Canvas canvas) {
		if (currTouchPoint.isDown()) {
			float[] xs = currTouchPoint.getXs();
			float[] ys = currTouchPoint.getYs();
			float[] pressures = currTouchPoint.getPressures();
			int numPoints = Math.min(currTouchPoint.getNumTouchPoints(), 2);
			for (int i = 0; i < numPoints; i++)
				canvas.drawCircle(xs[i], ys[i], 50 + pressures[i] * 80, mLinePaintTouchPointCircle2);
			if (numPoints == 2)
				canvas.drawLine(xs[0], ys[0], xs[1], ys[1], mLinePaintTouchPointCircle);
		}
	}

	// ---------------------------------------------------------------------------------------------------

	/** Pass touch events to the MT controller */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return multiTouchController.onTouchEvent(event);
	}

	/** Get the image that is under the single-touch point, or return null (canceling the drag op) if none */
	public MultiTouchEntity getDraggableObjectAtPoint(PointInfo pt) {
		float x = pt.getX(), y = pt.getY();
		int n = mImages.size();
		for (int i = n - 1; i >= 0; i--) {
			ImageEntity im = (ImageEntity) mImages.get(i);
			if (im.containsPoint(x, y))
				return im;
		}
		return null;
	}

	/**
	 * Select an object for dragging. Called whenever an object is found to be under the point (non-null is returned by getDraggableObjectAtPoint())
	 * and a drag operation is starting. Called with null when drag op ends.
	 */
	public void selectObject(MultiTouchEntity img, PointInfo touchPoint) {
		currTouchPoint.set(touchPoint);
		if (img != null) {
			// Move image to the top of the stack when selected
			mImages.remove(img);
			mImages.add(img);
		} else {
			// Called with img == null when drag stops.
		}
		invalidate();
	}

	/** Get the current position and scale of the selected image. Called whenever a drag starts or is reset. */
	public void getPositionAndScale(MultiTouchEntity img, PositionAndScale objPosAndScaleOut) {
		// FIXME affine-izem (and fix the fact that the anisotropic_scale part requires averaging the two scale factors)
		objPosAndScaleOut.set(img.getCenterX(), img.getCenterY(), (mUIMode & UI_MODE_ANISOTROPIC_SCALE) == 0,
				(img.getScaleX() + img.getScaleY()) / 2, (mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0, img.getScaleX(), img.getScaleY(),
				(mUIMode & UI_MODE_ROTATE) != 0, img.getAngle());
	}

	/** Set the position and scale of the dragged/stretched image. */
	public boolean setPositionAndScale(MultiTouchEntity img,
            PositionAndScale newImgPosAndScale, PointInfo touchPoint) {
		currTouchPoint.set(touchPoint);
		boolean ok = ((ImageEntity)img).setPos(newImgPosAndScale);
		if (ok)
			invalidate();
		return ok;
	}

    public boolean pointInObjectGrabArea(PointInfo pt, MultiTouchEntity img) {
        return false;
    }
}
