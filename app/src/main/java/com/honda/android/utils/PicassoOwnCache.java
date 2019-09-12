package com.honda.android.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Random;

//import com.squareup.picasso.LruCache;

/**
 * Created by Lukmanpryg on 10/6/2016.
 */
public class PicassoOwnCache {
    private static PicassoOwnCache INSTANCE = null;
    private HashMap<String, String> cacheMap;
    private HashMap<String, Bitmap> bitmapMap;
    public static final String cacheDir = "/Android/data/com.honda/cache/";
    private static final String CACHE_FILENAME = ".cache";
    public static File fullCacheDir = new File(Environment.getExternalStorageDirectory().toString(), cacheDir);

    @SuppressWarnings("unchecked")
    private PicassoOwnCache() {
        cacheMap = new HashMap<String, String>();
        bitmapMap = new HashMap<String, Bitmap>();
        File fullCacheDir = new File(Environment.getExternalStorageDirectory().toString(), cacheDir);
        if (!fullCacheDir.exists()) {
            cleanCacheStart();
            return;
        }
        try {
            ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File(fullCacheDir.toString(), CACHE_FILENAME))));
            cacheMap = (HashMap<String, String>) is.readObject();
            is.close();
        } catch (StreamCorruptedException e) {
            cleanCacheStart();
        } catch (FileNotFoundException e) {
            cleanCacheStart();
        } catch (IOException e) {
            cleanCacheStart();
        } catch (ClassNotFoundException e) {
            cleanCacheStart();
        }
    }

    private void cleanCacheStart() {
        cacheMap = new HashMap<String, String>();
        File fullCacheDir = new File(Environment.getExternalStorageDirectory().toString(), cacheDir);
        fullCacheDir.mkdirs();
        File noMedia = new File(fullCacheDir.toString(), ".nomedia");
        try {
            noMedia.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized static void createInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PicassoOwnCache();
        }
    }

    public static PicassoOwnCache getInstance() {
        if (INSTANCE == null) createInstance();
        return INSTANCE;
    }

    public void saveCacheFile(String cacheUri, Bitmap image) {
        File fullCacheDir = new File(Environment.getExternalStorageDirectory().toString(), cacheDir);
        String fileLocalName = new SimpleDateFormat("ddMMyyhhmmssSSS").format(new java.util.Date()) + ".PNG";
        File fileUri = new File(fullCacheDir.toString(), fileLocalName);
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(fileUri);
            image.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
            cacheMap.put(cacheUri, fileLocalName);
            bitmapMap.put(cacheUri, image);
            ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(
                    new FileOutputStream(new File(fullCacheDir.toString(), CACHE_FILENAME))));
            os.writeObject(cacheMap);
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveCacheBitmap(Context context, Bitmap finalBitmap) {
        File fullCacheDir = new File(Environment.getExternalStorageDirectory().toString(), cacheDir);
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String iname = "bc-" + n + ".png";
        File myDir = new File(fullCacheDir.toString(), iname);
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        File file = new File(myDir, iname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap getCacheFile(String cacheUri) {
        if (bitmapMap.containsKey(cacheUri)) return (Bitmap) bitmapMap.get(cacheUri);

        if (!cacheMap.containsKey(cacheUri)) return null;
        String fileLocalName = cacheMap.get(cacheUri).toString();
        File fullCacheDir = new File(Environment.getExternalStorageDirectory().toString(), cacheDir);
        File fileUri = new File(fullCacheDir.toString(), fileLocalName);
        if (!fileUri.exists()) return null;

        Bitmap bm = BitmapFactory.decodeFile(fileUri.toString());
        bitmapMap.put(cacheUri, bm);
        return bm;
    }
}
