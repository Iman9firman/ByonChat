package com.byonchat.android.videotrimmer.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.byonchat.android.utils.HttpHelper;
import com.byonchat.android.videotrimmer.interfaces.ConvertTaskCompleted;
import com.byonchat.android.videotrimmer.videocompressor.MediaController;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
/**
 * Created by byonc on 5/24/2017.
 */

public class RequestConvertTask extends AsyncTask<String, String, Boolean> {
    private ConvertTaskCompleted mFragmentCallback;
    private static final int REGISTRATION_TIMEOUT = 3 * 1000;
    private static final int WAIT_TIMEOUT = 30 * 1000;
    private String content = null;
    private Context mContext;
    String path, outpath, startpos, endpos, fileSizeInMB;
    private static final String TAG = RequestConvertTask.class.getSimpleName();

    public RequestConvertTask(ConvertTaskCompleted fragmentCallback, Context ctx, String path, String outpath, String startpos, String endpos, String fileSizeInMB)  {
        mFragmentCallback = fragmentCallback;
        mContext = ctx;
        this.path = path;
        this.outpath = outpath;
        this.startpos = startpos;
        this.endpos = endpos;
        this.fileSizeInMB = fileSizeInMB;
    }

    @Override
    public void onPreExecute() {
        mFragmentCallback.onConvertStart();
    }

    @Override
    protected Boolean doInBackground(String... urls) {
        return MediaController.getInstance().convertVideo(path, outpath, Integer.parseInt(startpos), Integer.parseInt(endpos), Integer.parseInt(fileSizeInMB));
    }

    @Override
    protected void onPostExecute(Boolean results) {
        mFragmentCallback.onConvertDone(results);
    }

}
