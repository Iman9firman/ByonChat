package com.byonchat.android.utils;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.byonchat.android.personalRoom.asynctask.ProfileSaveDescription;

import net.glxn.qrgen.android.QRCode;

import java.util.HashMap;

public class GenerateQR {

    public GenerateQR(String url, HashMap<String ,String> params, GenerateQRListener listener){
        new getTextResult(url,listener).execute(params);
    }

    class getTextResult extends AsyncTask<HashMap<String ,String> , Void , String > {

        private String urlStr;
        private String strResult = null;
        ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
        GenerateQRListener listener;

        public getTextResult(String url , GenerateQRListener listener){
            this.urlStr = url;
            this.listener = listener;
        }

        @Override
        protected String doInBackground(HashMap<String ,String>... datas) {
            String result = profileSaveDescription.sendPostRequest(urlStr, datas[0]);
            if (result != null && !result.equalsIgnoreCase("Error Registering") && !result.equalsIgnoreCase("")){
                strResult = result;
            }
            return strResult;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null){
                Bitmap bitmap = QRCode.from(result).withSize(250,250).bitmap();
                listener.onSuccess(bitmap);
            } else {
                listener.onFailure("Data belum tersedia");
            }
            super.onPostExecute(result);
        }
    }

    public interface GenerateQRListener{
        void onSuccess(Bitmap qrBitmap);
        void onFailure(String errorMsg);
    }

}

