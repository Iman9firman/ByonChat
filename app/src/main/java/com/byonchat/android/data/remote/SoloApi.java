package com.byonchat.android.data.remote;

import android.os.Build;
import android.support.annotation.NonNull;

import com.byonchat.android.BuildConfig;
import com.byonchat.android.local.Byonchat;
import com.byonchat.android.sdk.BuildVersionUtil;
import com.byonchat.android.utils.ByonchatFileUtil;
import com.google.gson.JsonElement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.transform.Result;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Emitter;
import rx.Observable;
import rx.exceptions.OnErrorThrowable;

public enum SoloApi {
    INSTANCE;
    private final OkHttpClient httpClient;

    private String baseUrl;
    private String fileUrl;
    private final Api api;

    SoloApi() {
        baseUrl = Byonchat.getAppServer();
        fileUrl = Byonchat.getFileServer();

        httpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(this::headersInterceptor)
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

    private Response headersInterceptor(Interceptor.Chain chain) throws IOException {
        Request req = chain.request().newBuilder()
                .addHeader("SOLO_SDK_APP_ID", Byonchat.getAppId())
//                .addHeader("SOLO_SDK_TOKEN", Byonchat.hasSetupUser() ? Byonchat.getToken() : "")
//                .addHeader("SOLO_SDK_USER_EMAIL", Byonchat.hasSetupUser() ? Byonchat.getSoloAccount().getEmail() : "")
                .addHeader("SOLO_SDK_VERSION", "ANDROID_" + BuildConfig.VERSION_NAME)
                .addHeader("SOLO_SDK_PLATFORM", "ANDROID")
                .addHeader("SOLO_SDK_DEVICE_BRAND", Build.MANUFACTURER)
                .addHeader("SOLO_SDK_DEVICE_MODEL", Build.MODEL)
                .addHeader("SOLO_SDK_DEVICE_OS_VERSION", BuildVersionUtil.OS_VERSION_NAME)
                .build();
        return chain.proceed(req);
    }

    private HttpLoggingInterceptor makeLoggingInterceptor(boolean isDebug) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(isDebug ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        return logging;
    }

    public static SoloApi getInstance() {
        return INSTANCE;
    }

    public Observable<Void> registerTest(String args0) {
        return api.registerTest(args0)
                .map(jsonElement -> null);
    }

    public Call<Result> getVideoTube(String username_room, String id_room_tab) {
        Call<Result> call = api.getVideoTube(username_room, id_room_tab);

        return call;
    }

    public Observable<File> downloadFile(String url, String fileName, ProgressListener progressListener) {
        return Observable.create(subscriber -> {
            InputStream inputStream = null;
            FileOutputStream fos = null;
            try {
                Request request = new Request.Builder().url(url).build();

                Response response = httpClient.newCall(request).execute();

                File output = new File(ByonchatFileUtil.generateFilePath(fileName));
                fos = new FileOutputStream(output.getPath());
                if (!response.isSuccessful()) {
                    throw new IOException();
                } else {
                    ResponseBody responseBody = response.body();
                    long fileLength = responseBody.contentLength();

                    inputStream = responseBody.byteStream();
                    byte[] buffer = new byte[4096];
                    long total = 0;
                    int count;
                    while ((count = inputStream.read(buffer)) != -1) {
                        total += count;
                        long totalCurrent = total;
                        if (fileLength > 0) {
                            progressListener.onProgress((totalCurrent * 100 / fileLength));
                        }
                        fos.write(buffer, 0, count);
                    }
                    fos.flush();

                    subscriber.onNext(output);
                    subscriber.onCompleted();
                }
            } catch (Exception e) {
                throw OnErrorThrowable.from(OnErrorThrowable.addValueAsLastCause(e, url));
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException ignored) {
                    //Do nothing
                }
            }
        }, Emitter.BackpressureMode.BUFFER);
    }

    private interface Api {

        @FormUrlEncoded
        @POST("/tutorial_test/register.php")
        Call<Result> createUser(
                @Field("nama") String name);

        @FormUrlEncoded
        @POST("/bc_voucher_client/webservice/category_tab/video_local.php")
        Call<Result> getVideoTube(
                @Field("username_room") String username_room,
                @Field("id_rooms_tab ") String id_rooms_tab
        );

        @FormUrlEncoded
        @POST("/tutorial_test/register.php")
        Observable<JsonElement> registerTest(@Field("nama") String nama);

        @POST("/posts")
        Observable<JsonElement> requestNonce();

        @FormUrlEncoded
        @POST("/posts")
        Observable<JsonElement> login(@Field("identity_token") String token);

        @FormUrlEncoded
        @POST("/posts")
        Observable<JsonElement> loginOrRegister(@Field("email") String email,
                                                @Field("password") String password,
                                                @Field("username") String username,
                                                @Field("avatar_url") String avatarUrl);

        @FormUrlEncoded
//        @PATCH("/api/v2/mobile/my_profile")
        @POST("/posts")
        Observable<JsonElement> updateProfile(@Field("token") String token,
                                              @Field("name") String name,
                                              @Field("avatar_url") String avatarUrl);

        @FormUrlEncoded
        @POST("/posts")
        Observable<JsonElement> createOrGetChatRoom(@Field("token") String token,
                                                    @Field("emails[]") List<String> emails,
                                                    @Field("distinct_id") String distinctId,
                                                    @Field("options") String options);

        Observable<JsonElement> createGroupChatRoom(@Field("token") String token,
                                                    @Field("name") String name,
                                                    @Field("participants[]") List<String> emails,
                                                    @Field("avatar_url") String avatarUrl,
                                                    @Field("options") String options);

        @FormUrlEncoded
        @POST("/posts")
        Observable<JsonElement> createOrGetGroupChatRoom(@Field("token") String token,
                                                         @Field("unique_id") String uniqueId,
                                                         @Field("name") String name,
                                                         @Field("avatar_url") String avatarUrl,
                                                         @Field("options") String options);

        //        @GET("/api/v2/mobile/get_room_by_id")
        @POST("/posts")
        Observable<JsonElement> getChatRoom(@Query("token") String token,
                                            @Query("id") long roomId);

        //        @GET("/api/v2/mobile/load_comments")
        @POST("/posts")
        Observable<JsonElement> getComments(@Query("token") String token,
                                            @Query("topic_id") long roomId,
                                            @Query("last_comment_id") long lastCommentId,
                                            @Query("after") boolean after);

        @FormUrlEncoded
        @POST("/posts")
        Observable<JsonElement> postComment(@Field("token") String token,
                                            @Field("comment") String message,
                                            @Field("topic_id") long roomId,
                                            @Field("unique_temp_id") String uniqueId,
                                            @Field("type") String type,
                                            @Field("payload") String payload,
                                            @Field("extras") String extras);

        //        @GET("/api/v2/mobile/sync")
        @POST("/posts")
        Observable<JsonElement> sync(@Query("token") String token,
                                     @Query("last_received_comment_id") long lastCommentId);

        @FormUrlEncoded
        @POST("/posts")
        Observable<JsonElement> updateChatRoom(@Field("token") String token,
                                               @Field("id") long id,
                                               @Field("room_name") String name,
                                               @Field("avatar_url") String avatarUrl,
                                               @Field("options") String options);

        @FormUrlEncoded
        @POST("/posts")
        Observable<JsonElement> updateCommentStatus(@Field("token") String token,
                                                    @Field("room_id") long roomId,
                                                    @Field("last_comment_read_id") long lastReadId,
                                                    @Field("last_comment_received_id") long lastReceivedId);

        @FormUrlEncoded
        @POST("/posts")
        Observable<JsonElement> registerFcmToken(@Field("token") String token,
                                                 @Field("device_platform") String devicePlatform,
                                                 @Field("device_token") String fcmToken);

        @POST("/posts")
        Observable<JsonElement> searchComments(@Query("token") String token,
                                               @Query("query") String query,
                                               @Query("room_id") long roomId,
                                               @Query("last_comment_id") long lastCommentId);

        //        @GET("/api/v2/mobile/user_rooms")
        @POST("/posts")
        Observable<JsonElement> getChatRooms(@Query("token") String token,
                                             @Query("page") int page,
                                             @Query("limit") int limit,
                                             @Query("show_participants") boolean showParticipants);

        @FormUrlEncoded
        @POST("/posts")
        Observable<JsonElement> getChatRooms(@Field("token") String token,
                                             @Field("room_id[]") List<Long> roomIds,
                                             @Field("room_unique_id[]") List<String> roomUniqueIds,
                                             @Field("show_participants") boolean showParticipants);

        //        @DELETE("/api/v2/mobile/clear_room_messages")
        @POST("/posts")
        Observable<JsonElement> clearChatRoomMessages(@Query("token") String token,
                                                      @Query("room_channel_ids[]") List<String> roomUniqueIds);

        //        @DELETE("/api/v2/mobile/delete_messages")
        @POST("/posts")
        Observable<JsonElement> deleteComments(@Query("token") String token,
                                               @Query("unique_ids[]") List<String> commentUniqueIds,
                                               @Query("is_delete_for_everyone") boolean isDeleteForEveryone,
                                               @Query("is_hard_delete") boolean isHardDelete);

        //        @GET("/api/v2/mobile/sync_event")
        @POST("/posts")
        Observable<JsonElement> getEvents(@Query("token") String token,
                                          @Query("start_event_id") long startEventId);

        //        @GET("/api/v2/sdk/total_unread_count")
        @POST("/posts")
        Observable<JsonElement> getTotalUnreadCount(@Query("token") String token);

        @FormUrlEncoded
        @POST("/posts")
        Observable<JsonElement> addRoomMember(@Field("token") String token,
                                              @Field("room_id") long roomId,
                                              @Field("emails[]") List<String> emails);

        @FormUrlEncoded
        @POST("/posts")
        Observable<JsonElement> removeRoomMember(@Field("token") String token,
                                                 @Field("room_id") long roomId,
                                                 @Field("emails[]") List<String> emails);
    }

    private static class CountingFileRequestBody extends RequestBody {
        private final File file;
        private final ProgressListener progressListener;
        private static final int SEGMENT_SIZE = 2048;

        private CountingFileRequestBody(File file, ProgressListener progressListener) {
            this.file = file;
            this.progressListener = progressListener;
        }

        @Override
        public MediaType contentType() {
            return MediaType.parse("application/octet-stream");
        }

        @Override
        public long contentLength() throws IOException {
            return file.length();
        }

        @Override
        public void writeTo(@NonNull BufferedSink sink) throws IOException {
            Source source = null;
            try {
                source = Okio.source(file);
                long total = 0;
                long read;

                while ((read = source.read(sink.buffer(), SEGMENT_SIZE)) != -1) {
                    total += read;
                    sink.flush();
                    progressListener.onProgress(total);

                }
            } finally {
                Util.closeQuietly(source);
            }
        }

    }

    public interface ProgressListener {
        void onProgress(long total);
    }
}
