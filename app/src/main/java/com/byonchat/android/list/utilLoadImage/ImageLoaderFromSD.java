package com.byonchat.android.list.utilLoadImage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.byonchat.android.R;
import com.byonchat.android.utils.MediaProcessingUtil;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoaderFromSD {

    MemoryCache memoryCache = new MemoryCache();
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService;
    Handler handler = new Handler();//handler to display images in UI thread
    Context mContext;
    Boolean mine;

    public ImageLoaderFromSD(Context context) {
        this.mContext = context;
        executorService = Executors.newFixedThreadPool(5);
    }

    final int stub_id = 0;

    public void DeleteImage(String url, ImageView imageView) {
        imageViews.put(imageView, url);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null) {
            deletePhoto(url, imageView);
        }
    }

    public Bitmap showBitmap(String url){
        Bitmap bitmap = memoryCache.get(url);
        return bitmap;
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int wantedWidth, int wantedHeight) {
        Bitmap output = Bitmap.createBitmap(wantedWidth, wantedHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Matrix m = new Matrix();
        m.setScale((float) wantedWidth / bitmap.getWidth(), (float) wantedHeight / bitmap.getHeight());
        canvas.drawBitmap(bitmap, m, new Paint());

        return output;
    }

    public void DisplayImage(String url, ImageView imageView, boolean mine) {
        imageViews.put(imageView, url);
        Bitmap bitmap = memoryCache.get(url);
        this.mine = mine;
        if (bitmap != null) {
            Bitmap b = null;
            if (mine) {
                b = MediaProcessingUtil.getRoundedCornerBitmap(scaleBitmap(bitmap, 96, 96), 22);
            } else {
                b = MediaProcessingUtil.getRoundedCornerBitmap(bitmap, 22);
            }
            imageView.setImageBitmap(b);
            imageView.setBackgroundResource(0);
        } else {
            imageView.setImageBitmap(getBitmapNoPhoto());
            imageView.setBackgroundResource(0);
            queuePhoto(url, imageView);
        }

    }

    private void deletePhoto(String url, ImageView imageView) {
        PhotoToLoad p = new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosRemover(p));
    }

    private void queuePhoto(String url, ImageView imageView) {
        PhotoToLoad p = new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p));
    }

    private Bitmap getBitmap(String url) {
        File photoFile = mContext.getFileStreamPath(url);
        if (photoFile.exists()) {
            Bitmap b = MediaProcessingUtil.getRoundedCornerBitmap(BitmapFactory.decodeFile(String.valueOf(photoFile)), 22);
            if (b != null)
                return b;
        }
        return null;
    }

    private Bitmap getBitmapNoPhoto() {
        Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.ic_no_photo);
        Bitmap b = MediaProcessingUtil.getRoundedCornerBitmap(icon, 22);
        if (b != null)
            return b;
        return null;
    }

    //Task for the queue
    private class PhotoToLoad {
        public String url;
        public ImageView imageView;

        public PhotoToLoad(String u, ImageView i) {
            url = u;
            imageView = i;
        }
    }

    class PhotosRemover implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosRemover(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            try {
                if (imageViewReused(photoToLoad)) {
                    return;
                }
                Bitmap bmp = getBitmap(photoToLoad.url);
                memoryCache.deleteCache(photoToLoad.url, bmp);
                if (imageViewReused(photoToLoad)) {
                    return;
                }
                BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
                handler.post(bd);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            try {
                if (imageViewReused(photoToLoad)) {
                    return;
                }

                Bitmap bmp = getBitmap(photoToLoad.url);
                if (mine) {
                    memoryCache.put(photoToLoad.url, scaleBitmap(bmp, 96, 96));
                } else {
                    memoryCache.put(photoToLoad.url, bmp);
                }
                if (imageViewReused(photoToLoad)) {
                    return;
                }

                BitmapDisplayer bd = null;

                if (mine) {
                    bd = new BitmapDisplayer(scaleBitmap(bmp, 96, 96), photoToLoad);
                } else {
                    bd = new BitmapDisplayer(bmp, photoToLoad);
                }
                handler.post(bd);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);
        if (tag == null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }

    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad)) {
                return;
            }
            if (bitmap != null) {
                Bitmap b = MediaProcessingUtil.getRoundedCornerBitmap(bitmap, 22);
                photoToLoad.imageView.setImageBitmap(b);
                photoToLoad.imageView.setBackgroundResource(0);
            } else {
                /*photoToLoad.imageView.setImageResource(R.drawable.ic_no_photo);*/
                photoToLoad.imageView.setImageBitmap(getBitmapNoPhoto());
                photoToLoad.imageView.setBackgroundResource(0);
            }
        }
    }


}
