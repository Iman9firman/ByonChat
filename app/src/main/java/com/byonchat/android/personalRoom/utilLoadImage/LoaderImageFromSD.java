package com.byonchat.android.personalRoom.utilLoadImage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ImageView;

import com.byonchat.android.R;
import com.byonchat.android.list.utilLoadImage.MemoryCache;
import com.byonchat.android.personalRoom.view.SelectableRoundedImageView;
import com.byonchat.android.utils.MediaProcessingUtil;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ImageView;

import com.byonchat.android.R;
import com.byonchat.android.utils.MediaProcessingUtil;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoaderImageFromSD {

    MemoryCache memoryCache=new MemoryCache();
    private Map<SelectableRoundedImageView, String> imageViews= Collections.synchronizedMap(new WeakHashMap<SelectableRoundedImageView, String>());
    ExecutorService executorService;
    Handler handler=new Handler();//handler to display images in UI thread
    Context mContext;
    public LoaderImageFromSD(Context context){
        this.mContext = context;
        executorService= Executors.newFixedThreadPool(5);
    }

    final int stub_id = 0;
    public void DisplayImage(String url, SelectableRoundedImageView SelectableRoundedImageView) {
        imageViews.put(SelectableRoundedImageView, url);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null) {
            SelectableRoundedImageView.setImageBitmap(bitmap);
            SelectableRoundedImageView.setBackgroundResource(0);
        }else{
            SelectableRoundedImageView.setImageResource(R.drawable.ic_no_photo);
            queuePhoto(url, SelectableRoundedImageView);
        }
    }

    private void queuePhoto(String url, SelectableRoundedImageView SelectableRoundedImageView)
    {
        PhotoToLoad p=new PhotoToLoad(url, SelectableRoundedImageView);
        executorService.submit(new PhotosLoader(p));
    }

    private Bitmap getBitmap(String url)
    {
        File photoFile = mContext.getFileStreamPath(url);
        if (photoFile.exists()) {
            Bitmap b = MediaProcessingUtil.getRoundedCornerBitmap(BitmapFactory.decodeFile(String.valueOf(photoFile)), 10);
            if(b!=null)
                return b;
        }
        return null;
    }

    //Task for the queue
    private class PhotoToLoad
    {
        public String url;
        public SelectableRoundedImageView SelectableRoundedImageView;
        public PhotoToLoad(String u, SelectableRoundedImageView i){
            url=u;
            SelectableRoundedImageView=i;
        }
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        PhotosLoader(PhotoToLoad photoToLoad){
            this.photoToLoad=photoToLoad;
        }

        @Override
        public void run() {
            try{
                if(imageViewReused(photoToLoad))
                    return;
                Bitmap bmp=getBitmap(photoToLoad.url);
                memoryCache.put(photoToLoad.url, bmp);
                if(imageViewReused(photoToLoad))
                    return;
                BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad);
                handler.post(bd);
            }catch(Throwable th){
                th.printStackTrace();
            }
        }
    }

    boolean imageViewReused(PhotoToLoad photoToLoad){
        String tag=imageViews.get(photoToLoad.SelectableRoundedImageView);
        if(tag==null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }

    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        public BitmapDisplayer(Bitmap b, PhotoToLoad p){bitmap=b;photoToLoad=p;}
        public void run()
        {
            if(imageViewReused(photoToLoad))
                return;
            if(bitmap!=null){
                photoToLoad.SelectableRoundedImageView.setImageBitmap(bitmap);
                photoToLoad.SelectableRoundedImageView.setBackgroundResource(0);
            }else {
                photoToLoad.SelectableRoundedImageView.setImageResource(R.drawable.ic_no_photo);
            }
        }
    }


}
