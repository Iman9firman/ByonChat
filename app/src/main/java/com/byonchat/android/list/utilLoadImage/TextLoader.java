package com.byonchat.android.list.utilLoadImage;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.utils.GetRealNameRoom;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TextLoader {

    private Map<TextView, String> textView= Collections.synchronizedMap(new WeakHashMap<TextView, String>());
    ExecutorService executorService;
    Handler handler=new Handler();
    Context mContext;
    BotListDB botListDB ;
    public TextLoader(Context context){
        this.mContext = context;
        executorService= Executors.newFixedThreadPool(5);
        if(botListDB==null){
           botListDB = BotListDB.getInstance(mContext);
        }
    }
    
    final int stub_id = 0;
    public void DisplayImage(String url, TextView textview) {
        textView.put(textview, url);

        Cursor cur = botListDB.getRealNameByName(url.toLowerCase());
        if (cur.getCount()>0) {
            textview.setText(cur.getString(cur.getColumnIndex(BotListDB.ROOMS_REALNAME)));
        }else{
            queuePhoto(url, textview);
        }
        cur.close();

    }


    //Task for the queue
    private class PhotoToLoad
    {
        public String url;
        public TextView imageView;
        public PhotoToLoad(String u, TextView i){
            url=u;
            imageView=i;
        }
    }


    private void queuePhoto(String url, TextView imageView)
    {
        PhotoToLoad p=new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p));
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        PhotosLoader(PhotoToLoad photoToLoad){
            this.photoToLoad=photoToLoad;
        }

        @Override
        public void run() {
            try{
                BitmapDisplayer bd=new BitmapDisplayer(new GetRealNameRoom().getInstance(mContext).getName(photoToLoad.url), photoToLoad);
                handler.post(bd);
            }catch(Throwable th){
                th.printStackTrace();
            }
        }
    }

    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable
    {
        String bitmap;
        PhotoToLoad photoToLoad;
        public BitmapDisplayer(String b, PhotoToLoad p){bitmap=b;photoToLoad=p;}
        public void run()
        {
            if(bitmap!=null){
                photoToLoad.imageView.setText(bitmap);
            }else {
                photoToLoad.imageView.setText("name rooms");
            }
        }
    }
}
