package com.byonchat.android.retrofit;

import android.util.Log;

import com.byonchat.android.local.Byonchat;
import com.google.android.gms.common.api.Api;

import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public enum ByonchatAPI {
    INSTANCE;
    private final OkHttpClient httpClient;

    private String baseUrl;
    private final Api api;

    ByonchatAPI() {
        baseUrl = Byonchat.getAppServer();

        httpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(makeLoggingInterceptor(Byonchat.isEnableLog()))
                .build();

        api = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(Api.class);
    }

    private HttpLoggingInterceptor makeLoggingInterceptor(boolean isDebug) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(isDebug ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        return logging;
    }

    public static ByonchatAPI getInstance() {
        return INSTANCE;
    }

    public Call<ResponseBody> uploadMultiFile(RequestBody qiscusComment) {
        Log.w("jalan1", qiscusComment.toString() + " --- " + api.uploadMultiFile(qiscusComment));
        return api.uploadMultiFile(qiscusComment);
    }

    private interface Api {

        @POST("/bc_voucher_client/webservice/webservice/proses/repost_attachment.php")
        Call<ResponseBody> uploadMultiFile(@Body RequestBody file);
    }
}
