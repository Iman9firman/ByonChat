package com.byonchat.android.utils;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.byonchat.android.ConversationGroupActivity;
import com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity;
import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.config.Utils;
import com.byonchat.android.config.WsConfig;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.DataBaseHelper;
import com.byonchat.android.provider.Files;
import com.byonchat.android.provider.FilesDatabaseHelper;
import com.byonchat.android.provider.FilesURL;
import com.byonchat.android.provider.FilesURLDatabaseHelper;
import com.byonchat.android.provider.Interval;
import com.byonchat.android.provider.IntervalDB;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.provider.Skin;
import com.byonchat.android.provider.SubmitingRoomDB;
import com.byonchat.android.ui.activity.MainActivityNew;
import com.byonchat.android.videotrimmer.interfaces.ConvertTaskCompleted;
import com.byonchat.android.videotrimmer.utils.RequestConvertTask;
import com.byonchat.android.videotrimmer.videocompressor.MediaController;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity.GETTABDETAILPULLMULTIPLE;
import static com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity.POSDETAIL;
import static com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity.POST_FOTO;
import static com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity.PULLDETAIL;
import static com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity.PULLMULIPLEDETAIL;

public class UploadService extends IntentService {

    private NotificationManager notificationManager;
    private Notification notification;
    public static final String ACTION_MESSAGE_RECEIVED = MessengerConnectionService.class
            .getName() + ".messageReceived";
    public static final String KEY_MESSAGE_OBJECT = MessengerConnectionService.class
            .getName() + ".messageObject";
    public static final String KEY_CONTACT_NAME = MessengerConnectionService.class
            .getName() + ".contactName";

    public static final String KEY_MESSAGE = UploadService.class
            .getName() + ".message";
    public static final String KEY_STATUS_VIDEO = UploadService.class
            .getName() + ".status_video";
    public static final String KEY_MESSAGE_LINK_UPLOAD = UploadService.class
            .getName() + ".getLinkUpload";
    public static final String KEY_MESSAGE_LINK_DOWNLOAD = UploadService.class
            .getName() + ".getLinkDownload";
    public static final String KEY_MESSAGE_SESSION_UPLOAD = UploadService.class
            .getName() + ".getSessionUpload";
    public static final String KEY_UPDATE_BAR = UploadService.class
            .getName() + ".bar";
    public static final String KEY_UPDATE_UPLOAD_BAR = UploadService.class
            .getName() + ".uploadBar";
    public static final String SEND_FILE = UploadService.class
            .getName() + ".sendFileMessage";
    public static final String FILE_SEND_INTENT = UploadService.class.getName() + ".sendFile";
    public static final String ACTION = "action";
    public static final String ID_OFFERS = "idOffers";
    /*  public final static String FILE_UPLOAD_URL = "https://" + MessengerConnectionService.FILE_SERVER + "/v1/upload";
      public final static String FILE_DOWNLOAD_URL = "https://" + MessengerConnectionService.FILE_SERVER + "/v1/download/rd/";*/
    public final static String URL_GET_REQUEST = "https://" + MessengerConnectionService.HTTP_SERVER + "/themes/narik.php";
    public final static String URL_CEK_APPLY = "https://" + MessengerConnectionService.HTTP_SERVER + "/themes/boleh.php";
    public final static String URL_LAPOR_OFFERS = "https://" + MessengerConnectionService.HTTP_SERVER + "/offers/lapor.php";
    private MessengerDatabaseHelper messengerHelper;
    NotificationManager mNotificationManager;

    public static final SimpleDateFormat hourFormat = new SimpleDateFormat(
            "HH:mm:ss dd/MM/yyyy", Locale.getDefault());

    public UploadService(String name) {
        super(name);
    }

    public UploadService() {
        super("UploadService");
    }

    private Intent serviceIntent;
    IntervalDB db;
    Bitmap logoSkin;
    Bitmap backSkin;
    String path = "";
    String outpath = "";
    String startpos = "";
    String endpos = "";
    String fileSizeInMB;
    String caption = "";
    private final Handler mHandler = new Handler();

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelId, String channelName) {
        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.createNotificationChannel(chan);
        return channelId;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Override
    protected void onHandleIntent(Intent intent) {
        Intent notificationIntent = new Intent(this, MainActivityNew.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.logo_byon);

        String channelId = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = createNotificationChannel("S-Team", "Connected");

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setContentTitle("S-Team")
                    .setContentText(NetworkInternetConnectionStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext()) ? "Connected" : "No Connectivity")
                    .setSmallIcon(R.drawable.logo_byon)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .build();
            startForeground(101,
                    notification);
        }

        Message vo = intent.getParcelableExtra(UploadService.KEY_MESSAGE);

        String action = intent.getStringExtra(UploadService.ACTION);
        Log.d("HIDUP", "onHandleIntent UploadService " + action);

        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(this);
        }
        Thread t = null;
        if (action.equalsIgnoreCase("uploadTaskRoom")) {
            t = new Thread(new BackgroundThreadUploadTaskRoom(this, vo));
        } else if (action.equalsIgnoreCase("downloadValueForm")) {
            t = new Thread(new BackgroundThreadDownloadValueForm(this, vo));
        } else if (action.equalsIgnoreCase("downloadListForm")) {
            t = new Thread(new BackgroundThreadDownloadValueForm(this, vo));
        } else if (action.equalsIgnoreCase("download")) {
            t = new Thread(new BackgroundThreadDownload(this, vo));
        } else if (action.equalsIgnoreCase("downloadLocation")) {
            t = new Thread(new BackgroundThreadDownloadLocation(this, vo));
        } else if (action.equalsIgnoreCase("sendText")) {
            t = new Thread(new BackgroundThreadSendText(this, vo));
        } else if (action.equalsIgnoreCase("startService")) {
            t = new Thread(new BackgroundThreadStart(this));
        } else if (action.equalsIgnoreCase("cekTheme")) {
            t = new Thread(new BackgroundThreadCekTheme(this));
        } else if (action.equalsIgnoreCase("cekIklan")) {
            t = new Thread(new BackgroundThreadGetIklan(this));
        } else if (action.equalsIgnoreCase("cekApps")) {
            t = new Thread(new BackgroundThreadCekApps(this, intent.getStringExtra("package"), intent.getStringExtra(ID_OFFERS)));
        } else if (action.equalsIgnoreCase("sendTextGroup")) {
            new BackgroundThreadSendTextGroup(this, vo);
        } else if (action.equalsIgnoreCase("getLinkUpload")) {
            String statusVideo = "";
            if (intent.getExtras().containsKey(UploadService.KEY_STATUS_VIDEO))
                statusVideo = intent.getStringExtra(UploadService.KEY_STATUS_VIDEO);
            t = new Thread(new BackgroundThreadGetLinkUpload(this, vo, statusVideo));
        } else if (action.equalsIgnoreCase("downloadFileP2P")) {
            String linkDownload = intent.getStringExtra(UploadService.KEY_MESSAGE_LINK_DOWNLOAD);
            t = new Thread(new BackgroundThreadGetLinkDownload(this, vo, linkDownload));
        } else {
            String linkUpload = intent.getStringExtra(UploadService.KEY_MESSAGE_LINK_UPLOAD);
            String sessionId = intent.getStringExtra(UploadService.KEY_MESSAGE_SESSION_UPLOAD);
            t = new Thread(new BackgroundThread(this, vo, linkUpload, sessionId));
        }

        if (t != null) {
            t.start();
        }
    }


    private class BackgroundThreadGetLinkUpload implements Runnable {

        Context context;
        File fileToSend;
        int lastPercent = 0;
        long totalSize = 0;
        private String type;
        private ContentType contentType;
        Message vo;
        String statusVideo;

        public BackgroundThreadGetLinkUpload(Context context, Message message, String statusVideo) {
            this.vo = message;
            this.context = context;
            String urlFile = "";
            this.statusVideo = statusVideo;

            JSONObject jObject = null;
            try {
                jObject = new JSONObject(message.getMessage());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (jObject != null) {
                try {
                    urlFile = jObject.getString("s");
                    path = jObject.getString("s");
                    outpath = jObject.getString("o");
                    startpos = jObject.getString("sp");
                    endpos = jObject.getString("ep");
                    fileSizeInMB = jObject.getString("m");
                    caption = jObject.getString("c");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            this.type = vo.getType();
            if (this.type.equals(Message.TYPE_IMAGE)) {
                contentType = ContentType.create("image/jpeg");
                this.fileToSend = new File(urlFile);
            } else if (this.type.equals(Message.TYPE_VIDEO)) {
                this.fileToSend = new File(outpath);
                contentType = ContentType.create("video/mpeg");
            }

        }

        @Override
        public void run() {
            if (this.type.equals(Message.TYPE_VIDEO)) {
                if (Integer.parseInt(statusVideo) == 1) {
                    RequestKeyTask testAsyncTask = new RequestKeyTask(new TaskCompleted() {
                        @Override
                        public void onTaskDone(String key) {
                            if (key.equalsIgnoreCase("null")) {
                                FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(context);
                                db.open();
                                db.updateFiles(new FilesURL((int) vo.getId(), "0", "failed", ""));
                                db.close();
                                vo.setStatus(Message.STATUS_NOTSEND);
                                messengerHelper.updateData(vo);
                                Intent intent = new Intent(UploadService.KEY_UPDATE_UPLOAD_BAR);
                                intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                                sendBroadcast(intent);
                            } else {
                                RequestUploadSite testAsyncTask = new RequestUploadSite(new TaskCompleted() {
                                    @Override
                                    public void onTaskDone(final String key) {
                                        if (key.equalsIgnoreCase("null") || key.equalsIgnoreCase("https://upload.com")) {
                                            FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(context);
                                            db.open();
                                            db.updateFiles(new FilesURL((int) vo.getId(), "0", "failed", ""));
                                            db.close();
                                            vo.setStatus(Message.STATUS_NOTSEND);
                                            messengerHelper.updateData(vo);
                                            Intent intent = new Intent(UploadService.KEY_UPDATE_UPLOAD_BAR);
                                            intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                                            sendBroadcast(intent);
                                        } else {

                                            RequestConvertTask convertAsynctask = new RequestConvertTask(new ConvertTaskCompleted() {
                                                @Override
                                                public void onConvertStart() {

                                                }

                                                @Override
                                                public void onConvertDone(Boolean result) {
                                                    FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(context);
                                                    db.open();
                                                    db.updateFiles(new FilesURL((int) vo.getId(), "2", "upload", ""));
                                                    db.close();
                                                    Intent intent2 = new Intent(UploadService.KEY_UPDATE_UPLOAD_BAR);
                                                    intent2.putExtra(KEY_MESSAGE_OBJECT, vo);
                                                    sendBroadcast(intent2);

                                                    String aa[] = key.split(" ");
                                                    if (aa.length == 2) {
                                                        Intent intent = new Intent(getApplicationContext(), UploadService.class);
                                                        intent.putExtra(UploadService.ACTION, "upload");
                                                        intent.putExtra(UploadService.KEY_MESSAGE, vo);
                                                        intent.putExtra(UploadService.KEY_MESSAGE_LINK_UPLOAD, aa[0]);
                                                        intent.putExtra(UploadService.KEY_MESSAGE_SESSION_UPLOAD, aa[1]);
                                                        startService(intent);
                                                    } else {
                                                        FilesURLDatabaseHelper db2 = new FilesURLDatabaseHelper(context);
                                                        db2.open();
                                                        db2.updateFiles(new FilesURL((int) vo.getId(), "0", "failed", ""));
                                                        db2.close();
                                                        vo.setStatus(Message.STATUS_NOTSEND);
                                                        messengerHelper.updateData(vo);
                                                        Intent intent = new Intent(UploadService.KEY_UPDATE_UPLOAD_BAR);
                                                        intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                                                        sendBroadcast(intent);
                                                    }
                                                }
                                            }, getApplicationContext(), path, outpath, startpos, endpos, fileSizeInMB);
                                            convertAsynctask.execute();


                                        }
                                    }
                                }, getApplicationContext(), key, fileToSend.getName());
                                testAsyncTask.execute();
                            }
                        }
                    }, getApplicationContext());
                    testAsyncTask.execute();
                } else if (Integer.parseInt(statusVideo) == 2) {
                    RequestKeyTask testAsyncTask = new RequestKeyTask(new TaskCompleted() {
                        @Override
                        public void onTaskDone(String key) {
                            if (key.equalsIgnoreCase("null")) {
                                FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(context);
                                db.open();
                                db.updateFiles(new FilesURL((int) vo.getId(), "0", "failed", ""));
                                db.close();
                                vo.setStatus(Message.STATUS_NOTSEND);
                                messengerHelper.updateData(vo);
                                Intent intent = new Intent(UploadService.KEY_UPDATE_UPLOAD_BAR);
                                intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                                sendBroadcast(intent);
                            } else {
                                RequestUploadSite testAsyncTask = new RequestUploadSite(new TaskCompleted() {
                                    @Override
                                    public void onTaskDone(final String key) {
                                        if (key.equalsIgnoreCase("null") || key.equalsIgnoreCase("https://upload.com")) {
                                            FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(context);
                                            db.open();
                                            db.updateFiles(new FilesURL((int) vo.getId(), "0", "failed", ""));
                                            db.close();
                                            vo.setStatus(Message.STATUS_NOTSEND);
                                            messengerHelper.updateData(vo);
                                            Intent intent = new Intent(UploadService.KEY_UPDATE_UPLOAD_BAR);
                                            intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                                            sendBroadcast(intent);
                                        } else {

                                            FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(context);
                                            db.open();
                                            db.updateFiles(new FilesURL((int) vo.getId(), "2", "upload", ""));
                                            db.close();
                                            Intent intent2 = new Intent(UploadService.KEY_UPDATE_UPLOAD_BAR);
                                            intent2.putExtra(KEY_MESSAGE_OBJECT, vo);
                                            sendBroadcast(intent2);

                                            String aa[] = key.split(" ");
                                            if (aa.length == 2) {
                                                Intent intent = new Intent(getApplicationContext(), UploadService.class);
                                                intent.putExtra(UploadService.ACTION, "upload");
                                                intent.putExtra(UploadService.KEY_MESSAGE, vo);
                                                intent.putExtra(UploadService.KEY_MESSAGE_LINK_UPLOAD, aa[0]);
                                                intent.putExtra(UploadService.KEY_MESSAGE_SESSION_UPLOAD, aa[1]);
                                                startService(intent);
                                            } else {
                                                FilesURLDatabaseHelper db2 = new FilesURLDatabaseHelper(context);
                                                db2.open();
                                                db2.updateFiles(new FilesURL((int) vo.getId(), "0", "failed", ""));
                                                db2.close();
                                                vo.setStatus(Message.STATUS_NOTSEND);
                                                messengerHelper.updateData(vo);
                                                Intent intent = new Intent(UploadService.KEY_UPDATE_UPLOAD_BAR);
                                                intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                                                sendBroadcast(intent);
                                            }
                                        }
                                    }
                                }, getApplicationContext(), key, fileToSend.getName());
                                testAsyncTask.execute();
                            }
                        }
                    }, getApplicationContext());
                    testAsyncTask.execute();
                }

            } else {
                RequestKeyTask testAsyncTask = new RequestKeyTask(new TaskCompleted() {
                    @Override
                    public void onTaskDone(String key) {
                        if (key.equalsIgnoreCase("null")) {
                            FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(context);
                            db.open();
                            db.updateFiles(new FilesURL((int) vo.getId(), "0", "failed", ""));
                            db.close();
                            vo.setStatus(Message.STATUS_NOTSEND);
                            messengerHelper.updateData(vo);
                            Intent intent = new Intent(UploadService.KEY_UPDATE_UPLOAD_BAR);
                            intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                            sendBroadcast(intent);
                        } else {
                            RequestUploadSite testAsyncTask = new RequestUploadSite(new TaskCompleted() {
                                @Override
                                public void onTaskDone(final String key) {
                                    if (key.equalsIgnoreCase("null") || key.equalsIgnoreCase("https://upload.com")) {
                                        FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(context);
                                        db.open();
                                        db.updateFiles(new FilesURL((int) vo.getId(), "0", "failed", ""));
                                        db.close();
                                        vo.setStatus(Message.STATUS_NOTSEND);
                                        messengerHelper.updateData(vo);
                                        Intent intent = new Intent(UploadService.KEY_UPDATE_UPLOAD_BAR);
                                        intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                                        sendBroadcast(intent);
                                    } else {
                                        FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(context);
                                        db.open();
                                        db.updateFiles(new FilesURL((int) vo.getId(), "2", "upload", ""));
                                        db.close();
                                        Intent intent2 = new Intent(UploadService.KEY_UPDATE_UPLOAD_BAR);
                                        intent2.putExtra(KEY_MESSAGE_OBJECT, vo);
                                        sendBroadcast(intent2);

                                        String aa[] = key.split(" ");
                                        if (aa.length == 2) {
                                            Intent intent = new Intent(getApplicationContext(), UploadService.class);
                                            intent.putExtra(UploadService.ACTION, "upload");
                                            intent.putExtra(UploadService.KEY_MESSAGE, vo);
                                            intent.putExtra(UploadService.KEY_MESSAGE_LINK_UPLOAD, aa[0]);
                                            intent.putExtra(UploadService.KEY_MESSAGE_SESSION_UPLOAD, aa[1]);
                                            startService(intent);
                                        } else {
                                            FilesURLDatabaseHelper db2 = new FilesURLDatabaseHelper(context);
                                            db2.open();
                                            db2.updateFiles(new FilesURL((int) vo.getId(), "0", "failed", ""));
                                            db2.close();
                                            vo.setStatus(Message.STATUS_NOTSEND);
                                            messengerHelper.updateData(vo);
                                            Intent intent = new Intent(UploadService.KEY_UPDATE_UPLOAD_BAR);
                                            intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                                            sendBroadcast(intent);
                                        }
                                    }
                                }
                            }, getApplicationContext(), key, fileToSend.getName());
                            testAsyncTask.execute();
                        }
                    }
                }, getApplicationContext());
                testAsyncTask.execute();
            }

        }
    }

    private class BackgroundThreadGetLinkDownload implements Runnable {

        Context context;
        int lastPercent = 0;
        int count;
        private String type;
        private ContentType contentType;
        Message vo;
        String urlFile;
        String filename = "";

        public BackgroundThreadGetLinkDownload(Context context, Message message, String urlF) {
            this.vo = message;
            this.urlFile = urlF;
            this.context = context;
            this.type = vo.getType();
            if (this.type.equals(Message.TYPE_IMAGE)) {
                contentType = ContentType.create("image/jpeg");
                filename = MediaProcessingUtil.createFileNameAll(context, "bc.jpg");
            } else if (this.type.equals(Message.TYPE_VIDEO)) {
                contentType = ContentType.create("video/mpeg");
                filename = MediaProcessingUtil.createFileNameAll(context, "bc.mp4");
            }
        }

        @Override
        public void run() {
            try {
                String aa[] = urlFile.split("/");
                String link = aa[aa.length - 1];
                String encode_url = link.replace(" ", "%20");
                URL url = new URL(urlFile.replace(link, encode_url));
                URLConnection conection = url.openConnection();
                conection.connect();
                conection.setConnectTimeout(120000);//set 2 menit

                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                File file = new File(MediaProcessingUtil.getDirectory(), "/" + filename);
                // Output stream to write file
                OutputStream output = new FileOutputStream(file);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;

                    if (((int) ((total * 100) / lenghtOfFile)) > lastPercent) {
                        if (((int) ((total * 100) / lenghtOfFile)) % 10 == 0) {

                            FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(context);
                            db.open();
                            final FilesURL files = db.retriveFiles(vo.getId());
                            if (files != null) {
                                if (files.getCache() != null) {
                                    final File f = new File(PicassoOwnCache.fullCacheDir, files.getCache());
                                    if (f.exists()) {
                                        f.delete();
                                    }
                                }
                            }
                            db.updateFiles(new FilesURL((int) vo.getId(), String.valueOf((total * 100) / lenghtOfFile), "download", ""));
                            db.close();
                            Intent intent = new Intent(UploadService.KEY_UPDATE_BAR);
                            intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                            sendBroadcast(intent);
                        }
                    }
                    lastPercent = (int) ((total * 100) / lenghtOfFile);

                    // writing data to file
                    output.write(data, 0, count);
                }

                if ((total * 100) / lenghtOfFile == 100) {

                    File f = new File(MediaProcessingUtil.getDirectory(), "/" + filename);
                    String a = "";
                    String b = "";
                    String c = "";
                    JSONObject jObject = null;
                    try {
                        jObject = new JSONObject(vo.getMessage());
                    } catch (JSONException e) {
                        FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(context);
                        db.open();
                        db.updateFiles(new FilesURL((int) vo.getId(), String.valueOf(0), "failed", ""));
                        db.close();
                        Intent intent = new Intent(UploadService.KEY_UPDATE_BAR);
                        intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                        sendBroadcast(intent);
                        e.printStackTrace();
                    }
                    if (jObject != null) {
                        try {
                            a = jObject.getString("s");
                            b = jObject.getString("c");
                            c = jObject.getString("u");

                        } catch (JSONException e) {
                            FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(context);
                            db.open();
                            db.updateFiles(new FilesURL((int) vo.getId(), String.valueOf(0), "failed", ""));
                            db.close();
                            Intent intent = new Intent(UploadService.KEY_UPDATE_BAR);
                            intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                            sendBroadcast(intent);
                            e.printStackTrace();
                        }
                    }

                    if (!a.equalsIgnoreCase("")) {
                        File delete = new File(a);
                        if (delete.exists()) {
                            delete.delete();
                        }
                    }
                    vo.setMessage(jsonMessage(f.getAbsolutePath(), b, c));
                    int currentapiVersion = Build.VERSION.SDK_INT;
                    if (currentapiVersion >= Build.VERSION_CODES.KITKAT) {
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file:/" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/S-Team/" + filename)));
                    } else {
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file:/" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/S-Team/" + filename)));
                    }

                    vo.setStatus(Message.STATUS_DELIVERED);
                    MessengerDatabaseHelper.getInstance(getApplicationContext()).updateData(vo);
                    FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(context);
                    db.open();
                    db.updateFiles(new FilesURL((int) vo.getId(), "100", "success", ""));
                    db.close();
                    Intent intent = new Intent(UploadService.KEY_UPDATE_BAR);
                    intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                    sendBroadcast(intent);
                } else {
                    FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(context);
                    db.open();
                    db.updateFiles(new FilesURL((int) vo.getId(), String.valueOf(0), "failed", ""));
                    db.close();
                    Intent intent = new Intent(UploadService.KEY_UPDATE_BAR);
                    intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                    sendBroadcast(intent);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: dari gambar ", e.getMessage());
                FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(context);
                db.open();
                db.updateFiles(new FilesURL((int) vo.getId(), "0", "failed", ""));
                db.close();
                Intent intent = new Intent(UploadService.KEY_UPDATE_BAR);
                intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                sendBroadcast(intent);
            }

        }
    }

    private String jsonMessage(String path, String caption, String mUrlServer) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("s", path);
            obj.put("c", caption);
            obj.put("u", mUrlServer);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }


    private class BackgroundThread implements Runnable {

        Context context;
        String mUrl;
        String sessionId;
        File fileToSend;
        int lastPercent = 0;
        long totalSize = 0;
        private String type;
        private ContentType contentType;
        Message vo;
        String path = "";
        String outpath = "";
        String startpos = "";
        String endpos = "";
        String fileSizeInMB = "";
        String urlFile = "";
        String caption = "";

        public BackgroundThread(Context context, Message message, String url, String sessionId) {
            this.mUrl = url;
            this.vo = message;
            this.sessionId = sessionId;
            this.context = context;


            JSONObject jObject = null;
            try {
                jObject = new JSONObject(message.getMessage());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (jObject != null) {
                try {
                    urlFile = jObject.getString("s");
                    path = jObject.getString("s");
                    outpath = jObject.getString("o");
                    startpos = jObject.getString("sp");
                    endpos = jObject.getString("ep");
                    fileSizeInMB = jObject.getString("m");
                    caption = jObject.getString("c");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(context);
                db.open();
                db.updateFiles(new FilesURL((int) vo.getId(), "0", "failed", ""));
                db.close();
                vo.setStatus(Message.STATUS_NOTSEND);
                messengerHelper.updateData(vo);
                Intent intent = new Intent(UploadService.KEY_UPDATE_UPLOAD_BAR);
                intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                sendBroadcast(intent);
                return;
            }

            this.type = vo.getType();
            if (this.type.equals(Message.TYPE_IMAGE)) {
                this.fileToSend = new File(urlFile);
                contentType = ContentType.create("image/jpeg");
            } else if (this.type.equals(Message.TYPE_VIDEO)) {
                this.fileToSend = new File(outpath);
                contentType = ContentType.create("video/mpeg");
            }

        }

        @Override
        public void run() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(mUrl);
            InputStreamReader reader = null;
            try {
                final AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                if ((int) ((num / (float) totalSize) * 100) > lastPercent) {
                                    lastPercent = (int) ((num / (float) totalSize) * 100);

                                    FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(context);
                                    db.open();
                                    db.updateFiles(new FilesURL((int) vo.getId(), String.valueOf(lastPercent), "upload", ""));
                                    db.close();
                                    vo.setStatus(Message.STATUS_INPROGRESS);
                                    Intent intent = new Intent(UploadService.KEY_UPDATE_UPLOAD_BAR);
                                    intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                                    sendBroadcast(intent);
                                }
                            }
                        });
                if (this.type.equals(Message.TYPE_IMAGE)) {
                    Bitmap bp = resize(fileToSend.getPath());
                    if (bp != null) {
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                        FormBodyPart fbp = new FormBodyPart("file", new ByteArrayBody(bos.toByteArray(), contentType, fileToSend.getName()));
                        entity.addPart(fbp);
                    } else {
                        FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(context);
                        db.open();
                        db.updateFiles(new FilesURL((int) vo.getId(), "0", "failed", ""));
                        db.close();
                        vo.setStatus(Message.STATUS_NOTSEND);
                        messengerHelper.updateData(vo);
                        Intent intent = new Intent(UploadService.KEY_UPDATE_UPLOAD_BAR);
                        intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                        sendBroadcast(intent);
                    }
                } else if (this.type.equals(Message.TYPE_VIDEO)) {
                    entity.addPart("file", new FileBody(fileToSend, contentType, fileToSend.getName()));
                }

                entity.addPart("session", new StringBody(sessionId));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    reader = new InputStreamReader(response.getEntity().getContent(), "UTF-8");
                    int r;
                    StringBuilder buf = new StringBuilder();
                    while ((r = reader.read()) != -1) {
                        buf.append((char) r);
                    }

                    JSONObject jObject = null;
                    try {
                        jObject = new JSONObject(buf.toString());
                    } catch (JSONException e) {
                        FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(context);
                        db.open();
                        db.updateFiles(new FilesURL((int) vo.getId(), "0", "failed", ""));
                        db.close();
                        vo.setStatus(Message.STATUS_NOTSEND);
                        messengerHelper.updateData(vo);
                        Intent intent = new Intent(UploadService.KEY_UPDATE_UPLOAD_BAR);
                        intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                        sendBroadcast(intent);
                        e.printStackTrace();
                    }
                    if (jObject != null) {
                        try {
                            String s = jObject.getString("s");
                            if (s.equalsIgnoreCase("0")) {
                                String downloadURL = jObject.getString("u");
                                if (downloadURL.length() > 0) {

                                    String pesanNya = jsonMessage(outpath, caption, downloadURL);
                                    JSONObject jObjectUrl = null;
                                    try {
                                        jObjectUrl = new JSONObject(pesanNya);
                                        if (jObjectUrl != null) {
                                            urlFile = jObjectUrl.getString("u");
                                            caption = jObjectUrl.getString("c");
                                            if (urlFile != null) {
                                                FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(context);
                                                db.open();
                                                db.updateFiles(new FilesURL((int) vo.getId(), "100", "success", ""));
                                                db.close();
                                                vo.setMessage(pesanNya);
                                                if (vo.isGroupChat()) {
                                                    Intent intent = new Intent(getApplicationContext(), UploadService.class);
                                                    intent.putExtra(UploadService.ACTION, "sendTextGroup");
                                                    intent.putExtra(UploadService.KEY_MESSAGE, vo);
                                                    startService(intent);
                                                } else {
                                                    Intent i = new Intent();
                                                    i.setAction(FILE_SEND_INTENT);
                                                    i.putExtra(KEY_MESSAGE_OBJECT, vo);
                                                    sendBroadcast(i);
                                                }
                                            } else {
                                                FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(context);
                                                db.open();
                                                db.updateFiles(new FilesURL((int) vo.getId(), "0", "failed", ""));
                                                db.close();
                                                vo.setStatus(Message.STATUS_NOTSEND);
                                                messengerHelper.updateData(vo);
                                                Intent intent = new Intent(UploadService.KEY_UPDATE_UPLOAD_BAR);
                                                intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                                                sendBroadcast(intent);
                                            }
                                        } else {
                                            FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(context);
                                            db.open();
                                            db.updateFiles(new FilesURL((int) vo.getId(), "0", "failed", ""));
                                            db.close();
                                            vo.setStatus(Message.STATUS_NOTSEND);
                                            messengerHelper.updateData(vo);
                                            Intent intent = new Intent(UploadService.KEY_UPDATE_UPLOAD_BAR);
                                            intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                                            sendBroadcast(intent);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(context);
                                        db.open();
                                        db.updateFiles(new FilesURL((int) vo.getId(), "0", "failed", ""));
                                        db.close();
                                        vo.setStatus(Message.STATUS_NOTSEND);
                                        messengerHelper.updateData(vo);
                                        Intent intent = new Intent(UploadService.KEY_UPDATE_UPLOAD_BAR);
                                        intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                                        sendBroadcast(intent);
                                    }

                                } else {
                                    FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(context);
                                    db.open();
                                    db.updateFiles(new FilesURL((int) vo.getId(), "0", "failed", ""));
                                    db.close();
                                    vo.setStatus(Message.STATUS_NOTSEND);
                                    messengerHelper.updateData(vo);
                                    Intent intent = new Intent(UploadService.KEY_UPDATE_UPLOAD_BAR);
                                    intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                                    sendBroadcast(intent);
                                }
                            } else {
                                FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(context);
                                db.open();
                                db.updateFiles(new FilesURL((int) vo.getId(), "0", "failed", ""));
                                db.close();
                                vo.setStatus(Message.STATUS_NOTSEND);
                                messengerHelper.updateData(vo);
                                Intent intent = new Intent(UploadService.KEY_UPDATE_UPLOAD_BAR);
                                intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                                sendBroadcast(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(context);
                            db.open();
                            db.updateFiles(new FilesURL((int) vo.getId(), "0", "failed", ""));
                            db.close();
                            vo.setStatus(Message.STATUS_NOTSEND);
                            messengerHelper.updateData(vo);
                            Intent intent = new Intent(UploadService.KEY_UPDATE_UPLOAD_BAR);
                            intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                            sendBroadcast(intent);
                        }
                    } else {
                        FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(context);
                        db.open();
                        db.updateFiles(new FilesURL((int) vo.getId(), "0", "failed", ""));
                        db.close();
                        vo.setStatus(Message.STATUS_NOTSEND);
                        messengerHelper.updateData(vo);
                        Intent intent = new Intent(UploadService.KEY_UPDATE_UPLOAD_BAR);
                        intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                        sendBroadcast(intent);
                    }
                } else {
                    FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(context);
                    db.open();
                    db.updateFiles(new FilesURL((int) vo.getId(), "0", "failed", ""));
                    db.close();
                    vo.setStatus(Message.STATUS_NOTSEND);
                    messengerHelper.updateData(vo);
                    Intent intent = new Intent(UploadService.KEY_UPDATE_UPLOAD_BAR);
                    intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                    sendBroadcast(intent);
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(context);
                db.open();
                db.updateFiles(new FilesURL((int) vo.getId(), "0", "failed", ""));
                db.close();
                vo.setStatus(Message.STATUS_NOTSEND);
                messengerHelper.updateData(vo);
                Intent intent = new Intent(UploadService.KEY_UPDATE_UPLOAD_BAR);
                intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                sendBroadcast(intent);
                responseString = e.toString();
            } catch (IOException e) {
                FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(context);
                db.open();
                db.updateFiles(new FilesURL((int) vo.getId(), "0", "failed", ""));
                db.close();
                vo.setStatus(Message.STATUS_NOTSEND);
                messengerHelper.updateData(vo);
                Intent intent = new Intent(UploadService.KEY_UPDATE_UPLOAD_BAR);
                intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                sendBroadcast(intent);
                responseString = e.toString();
            }
        }

        private String jsonMessage(String uriImage, String caption, String mUrlServer) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("s", uriImage);
                obj.put("c", caption);
                obj.put("u", mUrlServer);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return obj.toString();
        }

        private Bitmap resize(String path) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            Bitmap bp = null;
            int orignalHeight = opts.outHeight;
            int orignalWidth = opts.outWidth;
            float maxHeight = (float) 600.0;
            float maxWidth = (float) 800.0;
            int resizeScale = 1;
            if (orignalWidth > maxWidth || orignalHeight > maxHeight) {
                final int heightRatio = Math.round((float) orignalHeight / maxHeight);
                final int widthRatio = Math.round((float) orignalWidth / maxWidth);
                resizeScale = heightRatio < widthRatio ? heightRatio : widthRatio;
            }
            opts.inSampleSize = resizeScale;
            opts.inJustDecodeBounds = false;
            int bmSize = (orignalWidth / resizeScale) * (orignalHeight / resizeScale) * 4;
            if (Runtime.getRuntime().freeMemory() > bmSize) {
                bp = BitmapFactory.decodeFile(path, opts);
            } else
                return null;
            return bp;
        }
    }

    private class BackgroundThreadDownloadValueForm implements Runnable {

        Context context;
        String idDetail, username, idTab, idNotif;

        public BackgroundThreadDownloadValueForm(Context ctx, Message message) {


            JSONObject jObject = null;
            try {
                jObject = new JSONObject(message.getMessage());
                idDetail = jObject.getString("idDetail");
                username = jObject.getString("username");
                idTab = jObject.getString("idTab");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            idNotif = String.valueOf(message.getId());
            context = ctx;
        }

        @Override
        public void run() {
            if (!idDetail.equalsIgnoreCase("")) {
                String[] ff = idDetail.split("\\|");
                if (ff.length == 2) {
                    // new downloadValueForm().execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + GETTABDETAILPULLMULTIPLE, username, idTab, idDetail, idNotif);
                }
            }
        }
    }

    private class BackgroundThreadDownloadListForm implements Runnable {

        Context context;
        String idDetail, username, idTab, idNotif;

        public BackgroundThreadDownloadListForm(Context ctx, Message message) {


            JSONObject jObject = null;
            try {
                jObject = new JSONObject(message.getMessage());
                idDetail = jObject.getString("idDetail");
                username = jObject.getString("username");
                idTab = jObject.getString("idTab");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            idNotif = String.valueOf(message.getId());
            context = ctx;
        }

        @Override
        public void run() {
            if (!idDetail.equalsIgnoreCase("")) {
                String[] ff = idDetail.split("\\|");
                if (ff.length == 2) {
                    //  new downloadListForm().execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + GETTABDETAILPULLMULTIPLE, username, idTab, idDetail, idNotif);
                }
            }
        }
    }


    private class BackgroundThreadUploadTaskRoom implements Runnable {

        Context context;
        String ainnu, idDetail, username, idTab, fromList, customersId, includeStatus, isReject, idNotif;

        public BackgroundThreadUploadTaskRoom(Context ctx, Message message) {

            JSONObject jObject = null;
            try {
                jObject = new JSONObject(message.getMessage());
                ainnu = jObject.getString("in");
                idDetail = jObject.getString("idDetail");
                username = jObject.getString("username");
                idTab = jObject.getString("idTab");
                fromList = jObject.getString("fromList");
                customersId = jObject.getString("customersId");
                includeStatus = jObject.getString("includeStatus");
                isReject = jObject.getString("isReject");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            idNotif = String.valueOf(message.getId());

            context = ctx;
        }

        @Override
        public void run() {
            // uploadFileChild(context, ainnu, idDetail, username, idTab, fromList, customersId, includeStatus, isReject, idNotif);

        }

    }

    private void uploadFileChild(Context context, String ainnu, String idDetail, String username, String idTab, String fromList, String customersId, String includeStatus, String isReject, String idNotif) {

        Log.w("oke", idNotif);
        BotListDB db = BotListDB.getInstance(context);


        ArrayList<RoomsDetail> list = db.allRoomDetailFormWithFlag(idDetail, username, idTab, "cild");
        ArrayList<String> listUpload = new ArrayList<>();
        for (
                int u = 0; u < list.size(); u++) {
            JSONArray jsA = null;
            String content = "";

            String cc = list.get(u).getContent();

            try {
                if (cc.startsWith("{")) {
                    if (!cc.startsWith("[")) {
                        cc = "[" + cc + "]";
                    }
                    jsA = new JSONArray(cc);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            if (jsA != null) {

            } else {
                if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("form_child")) {
                    try {
                        JSONArray jsonArray = new JSONArray(list.get(u).getContent());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONArray jsnobject = new JSONArray(jsonArray.getJSONObject(i).getString("data"));
                            for (int ii = 0; ii < jsnobject.length(); ii++) {
                                JSONObject c = jsnobject.getJSONObject(ii);
                                if (c.getString("type").equalsIgnoreCase("front_camera") || c.getString("type").equalsIgnoreCase("rear_camera")) {
                                    String aa[] = c.getString("value").toString().split(";");
                                    if (aa.length == 2) {
                                        if (aa[0].length() == 0) {
                                            listUpload.add("1");
                                            String idLisTask = "";
                                            JSONObject cs = null;
                                            try {
                                                cs = new JSONObject(list.get(u).getFlag_content());
                                                idLisTask = cs.getString("a");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            new UploadFileToServer().execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + POST_FOTO, username, idTab, idLisTask, cc, c.getString("value").toString(), list.get(u).getId(), list.get(u).getParent_tab(), list.get(u).getParent_room(), aa[1], list.get(u).getFlag_content(), list.get(u).getFlag_tab(), list.get(u).getFlag_room(), fromList, customersId, includeStatus, isReject, idNotif);
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        if (listUpload.size() == 0) {
            if (fromList.equalsIgnoreCase("show")) {
                new posTask().execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + POSDETAIL, username, idTab, idDetail, fromList, customersId, includeStatus, isReject, idNotif);
            } else if (fromList.equalsIgnoreCase("hide")) {
                new posTask().execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + PULLDETAIL, username, idTab, idDetail, fromList, customersId, includeStatus, isReject, idNotif);
            } else {
                if (idDetail != null || !idDetail.equalsIgnoreCase("")) {
                    String[] ff = idDetail.split("\\|");
                    if (ff.length == 2) {
                        new posTask().execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + PULLMULIPLEDETAIL, username, idTab, idDetail, fromList, customersId, includeStatus, isReject, idNotif);
                    } else {
                        new posTask().execute(new ValidationsKey().getInstance(context).getTargetUrl(username) + POSDETAIL, username, idTab, idDetail, fromList, customersId, includeStatus, isReject, idNotif);
                    }
                }
            }
        } else {
            uploadFileChild(context, "looping", idDetail, username, idTab, fromList, customersId, includeStatus, isReject, idNotif);
        }
    }


    private class UploadFileToServer extends AsyncTask<String, Integer, String> {
        long totalSize = 0;
        String valueSS, valueS, getId, getParent_tab, getParent_room, uri, getFlag_content, getFlag_tab, getFlag_room, username, fromList, customersId, includeStatus, isReject, idNotif;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return uploadFile(params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7], params[8], params[9], params[10], params[11], params[12], params[13], params[14], params[15], params[16], params[17]);
        }

        @SuppressWarnings("deprecation")
        private String uploadFile(String URL, String user, String id_room, String id_list, String valueSS_, String valueS_, String getId_, String getParent_tab_, String getParent_room_, String _uri, String getFlag_content_, String getFlag_tab_, String getFlag_room_, String from_list, String customers_Id, String include_Status, String is_Reject, String id_notif) {
            String responseString = null;
            valueSS = valueSS_;
            valueS = valueS_;
            getId = getId_;
            getParent_tab = getParent_tab_;
            getParent_room = getParent_room_;
            uri = _uri;
            getFlag_content = getFlag_content_;
            getFlag_tab = getFlag_tab_;
            getFlag_room = getFlag_room_;
            username = user;
            fromList = from_list;
            customersId = customers_Id;
            includeStatus = include_Status;
            isReject = is_Reject;
            idNotif = id_notif;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(URL);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));

                                NotificationManager manager =
                                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());

                                builder.setContentTitle("submit Form");
                                builder.setContentText(new GetRealNameRoom().getInstance(getApplicationContext()).getName(username));
                                builder.setSmallIcon(R.drawable.ic_notif);
                                builder.setProgress(100, (int) ((num / (float) totalSize) * 100), true);
                                manager.notify(Integer.parseInt(idNotif), builder.build());
                            }
                        });

                File sourceFile = new File(resizeAndCompressImageBeforeSend(getApplicationContext(), uri, "fileUploadBC_" + new Date().getTime() + ".jpg"));
                if (!sourceFile.exists()) {
                    return "File not exists";
                }

                ContentType contentType = ContentType.create("image/jpeg");
                entity.addPart("username_room", new StringBody(username));
                entity.addPart("id_rooms_tab", new StringBody(id_room));
                entity.addPart("id_list_task", new StringBody(id_list));
                entity.addPart("value", new FileBody(sourceFile, contentType, sourceFile.getName()));


                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode == 200) {
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String message = jsonObject.getString("message");
                if (message.length() == 0) {
                    String fileNameServer = jsonObject.getString("filename");
                    String aadc = valueSS.replace("\"value\":\"" + (valueS.replace("/", "\\/")) + "\"", "\"value\":" + "\"" + fileNameServer + ";" + (uri.replace("/", "\\/")) + "\"");
                    RoomsDetail orderModel = new RoomsDetail(getId, getParent_tab, getParent_room, aadc, getFlag_content, getFlag_tab, getFlag_room);
                    BotListDB db = BotListDB.getInstance(getApplicationContext());
                    db.updateDetailRoomWithFlagContent(orderModel);
                    uploadFileChild(getApplicationContext(), "looping", getId, username, getParent_tab, fromList, customersId, includeStatus, isReject, idNotif);
                }
            } catch (JSONException e) {

                NotificationManager manager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());

                builder.setContentTitle("submit Form");
                builder.setContentText(new GetRealNameRoom().getInstance(getApplicationContext()).getName(username));
                builder.setSmallIcon(R.drawable.ic_notif);
                builder.setContentText("Upload failed Foto")
                        .setProgress(0, 0, false);
                manager.notify(Integer.parseInt(idNotif), builder.build());

                SubmitingRoomDB submitingRoomDB = SubmitingRoomDB.getInstance(getApplicationContext());
                submitingRoomDB.deleteContact(Long.parseLong("999"));

                e.printStackTrace();
            }
            super.onPostExecute(result);
        }
    }

    private class posTask extends AsyncTask<String, Integer, String> {
        long totalSize = 0;
        String responseString = null;

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            postData(params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7], params[8]);
            return null;
        }

        protected void onPostExecute(String result) {

        }

        protected void onProgressUpdate(String... string) {
        }

        public void postData(String valueIWantToSend, final String usr, final String idr, final String idDetail, final String fromList, final String customersId, String includeStatus, String isReject, final String idNotif) {
            // Create a new HttpClient and Post Header

            try {
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 13000);
                HttpConnectionParams.setSoTimeout(httpParameters, 15000);
                HttpClient httpclient = new DefaultHttpClient(httpParameters);
                HttpPost httppost = new HttpPost(valueIWantToSend);
                InputStreamReader reader = null;

                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));

                                NotificationManager manager =
                                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());

                                builder.setContentTitle("submit Form");
                                builder.setContentText(new GetRealNameRoom().getInstance(getApplicationContext()).getName(usr));
                                builder.setSmallIcon(R.drawable.ic_notif);
                                builder.setProgress(100, (int) ((num / (float) totalSize) * 100), false);
                                manager.notify(Integer.parseInt(idNotif), builder.build());

                            }
                        });

                entity.addPart("username_room", new StringBody(usr));
                entity.addPart("id_rooms_tab", new StringBody(idr));
                entity.addPart("id_detail_tab", new StringBody(idDetail));

                BotListDB db = BotListDB.getInstance(getApplicationContext());
                Cursor cEdit = db.getSingleRoomDetailFormWithFlagContent(idDetail, usr, idr, "assignTo", "");
                if (cEdit.getCount() > 0) {
                    String cc = cEdit.getString(cEdit.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));
                    String has = "";
                    if (cc.contains("All")) {
                        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(getApplicationContext());
                        Cursor curr = dataBaseHelper.selectAll("room", usr, idr);
                        if (curr.getCount() > 0) {
                            if (curr.moveToFirst()) {
                                do {
                                    if (has.length() == 0) {
                                        has = curr.getString(5);
                                    } else {
                                        has += "," + curr.getString(5);
                                    }

                                } while (curr.moveToNext());
                            }
                        }
                    } else {
                        String[] su = cc.split(",");
                        for (String ss : su) {
                            if (has.length() == 0) {
                                has = ss.split(" - ")[1];
                            } else {
                                has += "," + ss.split(" - ")[1];
                            }
                        }
                    }
                    entity.addPart("assign_to", new StringBody(has));
                }


                if (!includeStatus.equalsIgnoreCase("")) {
                    Cursor cucuTv = db.getSingleRoomDetailFormWithFlagContent(idDetail, usr, idr, "includeStatus", "");
                    if (cucuTv.getCount() > 0) {
                        String cucuTvi = cucuTv.getString(cucuTv.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_CONTENT));
                        String resultti = "0";
                        if (cucuTvi.equalsIgnoreCase("Approve")) {
                            resultti = "1";
                        } else if (cucuTvi.equalsIgnoreCase("Done")) {
                            resultti = "2";
                        }
                        entity.addPart("status_task", new StringBody(resultti));
                    }
                }


                if (!isReject.equalsIgnoreCase("")) {
                    entity.addPart("is_reject", new StringBody(isReject));
                }


                Cursor cursorParent = db.getSingleRoomDetailFormWithFlag(idDetail, usr, idr, "parent");


                if (cursorParent.getCount() > 0) {
                    if (!cursorParent.getString(cursorParent.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)).equalsIgnoreCase("")) {
                        entity.addPart("latlong_before", new StringBody(jsonResultType(cursorParent.getString(cursorParent.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "a")));
                        entity.addPart("latlong_after", new StringBody(jsonResultType(cursorParent.getString(cursorParent.getColumnIndexOrThrow(BotListDB.ROOM_DETAIL_FLAG_TAB)), "b")));
                    } else {
                        entity.addPart("latlong_before", new StringBody("null"));
                        entity.addPart("latlong_after", new StringBody("null"));
                    }
                } else {
                    entity.addPart("latlong_before", new StringBody("null"));
                    entity.addPart("latlong_after", new StringBody("null"));
                }

                if (fromList.equalsIgnoreCase("hide") || fromList.equalsIgnoreCase("hideMultiple") || fromList.equalsIgnoreCase("showMultiple")) {

                    if (idDetail != null || !idDetail.equalsIgnoreCase("")) {
                        String[] ff = idDetail.split("\\|");
                        if (ff.length == 2) {
                            entity.addPart("parent_id", new StringBody(ff[1]));
                            entity.addPart("id_list_push", new StringBody(ff[0]));
                        }
                    }
                }

                MessengerDatabaseHelper messengerHelper = null;
                if (messengerHelper == null) {
                    messengerHelper = MessengerDatabaseHelper.getInstance(getApplicationContext());
                }

                Contact contact = messengerHelper.getMyContact();
                entity.addPart("bc_user", new StringBody(contact.getJabberId()));


                ArrayList<RoomsDetail> list = db.allRoomDetailFormWithFlag(idDetail, usr, idr, "cild");


                for (int u = 0; u < list.size(); u++) {

                    JSONArray jsA = null;
                    String content = "";

                    String cc = list.get(u).getContent();
                    Log.w("cinta", cc);

                    if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("input_kodepos") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("dropdown_wilayah")) {
                        cc = jsoncreateC(list.get(u).getContent());
                    } else if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("form_child")) {

                    } else if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("dropdown_form")) {
                        try {
                            JSONObject jsonObject = new JSONObject(cc);
                            Iterator<String> iter = jsonObject.keys();

                            JSONObject jsHead = new JSONObject();
                            JSONArray jsAU = new JSONArray();
                            while (iter.hasNext()) {
                                JSONObject joN = new JSONObject();
                                String key = iter.next();
                                try {
                                    JSONArray jsAdd = jsonObject.getJSONArray(key);
                                    JSONArray newJS = new JSONArray();
                                    for (int ic = 0; ic < jsAdd.length(); ic++) {
                                        JSONObject oContent = new JSONObject(jsAdd.get(ic).toString());
                                        String lastCusID = oContent.getString("iD");
                                        String val = oContent.getString("v");
                                        String not = oContent.getString("n");

                                        JSONObject jOdetail = new JSONObject();
                                        jOdetail.put("id", lastCusID);
                                        jOdetail.put("val", val);
                                        jOdetail.put("note", not);

                                        newJS.put(jOdetail);

                                    }
                                    joN.put("id", key.toString());
                                    joN.put("checklists", newJS);
                                    jsAU.put(joN);

                                } catch (JSONException e) {
                                    // Something went wrong!
                                }
                            }

                            jsHead.put("outlet_id", customersId);
                            jsHead.put("audit", jsAU);
                            cc = jsHead.toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        if (cc.startsWith("{")) {
                            if (!cc.startsWith("[")) {
                                cc = "[" + cc + "]";
                            }
                            jsA = new JSONArray(cc);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    if (jsA != null) {
                        if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("distance_estimation") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("dropdown_dinamis") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("new_dropdown_dinamis") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("ocr") || jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("upload_document")) {
                            entity.addPart("key[]", new StringBody(list.get(u).getFlag_tab()));
                            entity.addPart("value[]", new StringBody(list.get(u).getContent()));
                            entity.addPart("type[]", new StringBody(jsonResultType(list.get(u).getFlag_content(), "b")));
                        } else if (jsonResultType(list.get(u).getFlag_content(), "b").equalsIgnoreCase("dropdown_form")) {
                            entity.addPart("key[]", new StringBody(list.get(u).getFlag_tab()));
                            entity.addPart("value[]", new StringBody(cc));
                            entity.addPart("type[]", new StringBody(jsonResultType(list.get(u).getFlag_content(), "b")));
                        } else {
                            try {
                                for (int ic = 0; ic < jsA.length(); ic++) {
                                    final String icC = jsA.getJSONObject(ic).getString("c").toString();
                                    content += icC + "|";
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            entity.addPart("key[]", new StringBody(list.get(u).getFlag_tab()));
                            entity.addPart("value[]", new StringBody(content.substring(0, content.length() - 1)));
                            entity.addPart("type[]", new StringBody(jsonResultType(list.get(u).getFlag_content(), "b")));

                        }
                    } else {

                        entity.addPart("key[]", new StringBody(list.get(u).getFlag_tab()));
                        entity.addPart("value[]", new StringBody(list.get(u).getContent()));
                        entity.addPart("type[]", new StringBody(jsonResultType(list.get(u).getFlag_content(), "b")));

                    }
                }

                Log.w("harlem", entity.toString());


                totalSize = entity.getContentLength();


                Log.w("totalSize", totalSize + "");


                httppost.setEntity(entity);

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();


                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    reader = new InputStreamReader(response.getEntity().getContent(), "UTF-8");
                    int r;
                    StringBuilder buf = new StringBuilder();
                    while ((r = reader.read()) != -1) {
                        buf.append((char) r);
                    }

                    if (buf.toString().equalsIgnoreCase("1")) {

                        NotificationManager manager =
                                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());

                        builder.setContentTitle("Submit Form");
                        builder.setContentText(new GetRealNameRoom().getInstance(getApplicationContext()).getName(usr));
                        builder.setSmallIcon(R.drawable.ic_notif);
                        builder.setContentText("Upload complete")
                                .setProgress(0, 0, false);
                        manager.notify(Integer.parseInt(idNotif), builder.build());

                        db.deleteRoomsDetailbyId(idDetail, idr, usr);

                        SubmitingRoomDB submitingRoomDB = SubmitingRoomDB.getInstance(getApplicationContext());
                        submitingRoomDB.deleteContact(Long.parseLong(idNotif));

                    } else {

                        NotificationManager manager =
                                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());

                        builder.setContentTitle("submit Form");
                        builder.setContentText(new GetRealNameRoom().getInstance(getApplicationContext()).getName(usr));
                        builder.setSmallIcon(R.drawable.ic_notif);
                        builder.setContentText("Upload failed2")
                                .setProgress(0, 0, false);
                        manager.notify(Integer.parseInt(idNotif), builder.build());

                        long date = System.currentTimeMillis();
                        String dateString = hourFormat.format(date);
                        RoomsDetail orderModel = new RoomsDetail(idDetail, idr, usr, dateString, "3", null, "parent");
                        db.updateDetailRoomWithFlagContentParent(orderModel);

                        SubmitingRoomDB submitingRoomDB = SubmitingRoomDB.getInstance(getApplicationContext());
                        submitingRoomDB.deleteContact(Long.parseLong(idNotif));

                    }
                } else {

                    NotificationManager manager =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());

                    builder.setContentTitle("submit Form");
                    builder.setContentText(new GetRealNameRoom().getInstance(getApplicationContext()).getName(usr));
                    builder.setSmallIcon(R.drawable.ic_notif);
                    builder.setContentText("Upload failed3")
                            .setProgress(0, 0, false);
                    manager.notify(Integer.parseInt(idNotif), builder.build());

                    long date = System.currentTimeMillis();
                    String dateString = hourFormat.format(date);
                    RoomsDetail orderModel = new RoomsDetail(idDetail, idr, usr, dateString, "3", null, "parent");
                    db.updateDetailRoomWithFlagContentParent(orderModel);

                    SubmitingRoomDB submitingRoomDB = SubmitingRoomDB.getInstance(getApplicationContext());
                    submitingRoomDB.deleteContact(Long.parseLong(idNotif));
                }
            } catch (IOException e) {

                NotificationManager manager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());

                builder.setContentTitle("submit Form");
                builder.setContentText(new GetRealNameRoom().getInstance(getApplicationContext()).getName(usr));
                builder.setSmallIcon(R.drawable.ic_notif);
                builder.setContentText("Upload failed4")
                        .setProgress(0, 0, false);
                manager.notify(Integer.parseInt(idNotif), builder.build());


                long date = System.currentTimeMillis();
                String dateString = hourFormat.format(date);
                BotListDB db = BotListDB.getInstance(getApplicationContext());
                RoomsDetail orderModel = new RoomsDetail(idDetail, idr, usr, dateString, "3", null, "parent");
                db.updateDetailRoomWithFlagContentParent(orderModel);

                SubmitingRoomDB submitingRoomDB = SubmitingRoomDB.getInstance(getApplicationContext());
                submitingRoomDB.deleteContact(Long.parseLong(idNotif));


                // TODO Auto-generated catch block
            }
        }
    }

    private String jsoncreateC(String content) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("c", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }

    public static String resizeAndCompressImageBeforeSend(Context context, String filePath, String fileName) {
        final int MAX_IMAGE_SIZE = 100 * 1024; // max final file size in kilobytes
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, 400, 400);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap bmpPic = BitmapFactory.decodeFile(filePath, options);


        int compressQuality = 100; // quality decreasing by 5 every loop.
        int streamLength;
        do {
            ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream);
            byte[] bmpPicByteArray = bmpStream.toByteArray();
            streamLength = bmpPicByteArray.length;
            compressQuality -= 5;
        } while (streamLength >= MAX_IMAGE_SIZE);

        try {
            //save the resized and compressed file to disk cache
            FileOutputStream bmpFile = new FileOutputStream(context.getCacheDir() + fileName);
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpFile);
            bmpFile.flush();
            bmpFile.close();
        } catch (Exception e) {
        }
        //return the path of resized and compressed file
        return context.getCacheDir() + fileName;
    }


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        String debugTag = "MemoryInformation";
        // Image nin islenmeden onceki genislik ve yuksekligi
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


    private String jsonResultType(String json, String type) {
        String hasil = "";
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jObject != null) {
            try {
                hasil = jObject.getString(type);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return hasil;
    }


    private class BackgroundThreadDownload implements Runnable {

        Context context;
        int lastPercent = 0;
        int count;
        private String type;
        private ContentType contentType;

        Message vo;
        String urlFile;

        public BackgroundThreadDownload(Context context, Message message) {
            this.vo = message;
            String link = "";

            JSONObject jObject = null;
            try {
                jObject = new JSONObject(vo.getMessage());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (jObject != null) {
                try {
                    link = jObject.getString("u");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            urlFile = link;
            this.context = context;
        }

        @Override
        public void run() {
            if (urlFile.startsWith("http")) {
                Intent intent = new Intent(getApplicationContext(), UploadService.class);
                intent.putExtra(UploadService.ACTION, "downloadFileP2P");
                intent.putExtra(UploadService.KEY_MESSAGE, vo);
                intent.putExtra(UploadService.KEY_MESSAGE_LINK_DOWNLOAD, urlFile);
                startService(intent);
            } else {
                RequestKeyTask testAsyncTask = new RequestKeyTask(new TaskCompleted() {
                    @Override
                    public void onTaskDone(String key) {
                        if (!key.equalsIgnoreCase("null")) {
                            RequestUploadSite testAsyncTask = new RequestUploadSite(new TaskCompleted() {
                                @Override
                                public void onTaskDone(String key) {
                                    if (key.contains("byonchat.com")) {
                                        String urlFile = key.replace("/v2/uploaded/file/", "/media/images/");
                                        Intent intent = new Intent(getApplicationContext(), UploadService.class);
                                        intent.putExtra(UploadService.ACTION, "downloadFileP2P");
                                        intent.putExtra(UploadService.KEY_MESSAGE, vo);
                                        intent.putExtra(UploadService.KEY_MESSAGE_LINK_DOWNLOAD, urlFile);
                                        startService(intent);
                                    } else {
                                        FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(context);
                                        db.open();
                                        db.updateFiles(new FilesURL((int) vo.getId(), String.valueOf(0), "failed", ""));
                                        db.close();
                                        Intent intent = new Intent(UploadService.KEY_UPDATE_BAR);
                                        intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                                        sendBroadcast(intent);
                                    }
                                }
                            }, context, key, urlFile, RequestUploadSite.REQUEST_KEYS_URL_REAL);
                            testAsyncTask.execute();
                        } else {
                            FilesURLDatabaseHelper db = new FilesURLDatabaseHelper(context);
                            db.open();
                            db.updateFiles(new FilesURL((int) vo.getId(), String.valueOf(0), "failed", ""));
                            db.close();
                            Intent intent = new Intent(UploadService.KEY_UPDATE_BAR);
                            intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                            sendBroadcast(intent);
                        }
                    }
                }, context);
                testAsyncTask.execute();
            }
        }

    }

    private class BackgroundThreadDownloadLocation implements Runnable {

        Message vo;
        Context context;
        String loc;
        String[] latlong;

        public BackgroundThreadDownloadLocation(Context ctx, Message message) {
            this.context = ctx;
            this.vo = message;
            latlong = vo.getMessage().split(
                    Message.LOCATION_DELIMITER);
            loc = latlong[0] + "," + latlong[1];
        }

        @Override
        public void run() {
            try {
                String url = context.getString(R.string.googlemap_static_url)
                        + "?zoom=18&size=400x400&sensor=false&markers=color:red%7C"
                        + loc + "&center=" + loc;
                String fname = MediaProcessingUtil.getFileFromUrl(context,
                        url, "location", "png");

                vo.setMessage(latlong[0] + Message.LOCATION_DELIMITER
                        + latlong[1] + Message.LOCATION_DELIMITER + latlong[2] + Message.LOCATION_DELIMITER
                        + latlong[3] + Message.LOCATION_DELIMITER + latlong[4] + Message.LOCATION_DELIMITER + fname);
                messengerHelper.updateData(vo);
                Intent intent = new Intent(UploadService.KEY_UPDATE_UPLOAD_BAR);
                intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                sendBroadcast(intent);

            } catch (Exception e) {
                Intent intent = new Intent(UploadService.KEY_UPDATE_UPLOAD_BAR);
                intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                sendBroadcast(intent);
            }

        }
    }

    private class BackgroundThreadSendText implements Runnable {

        Message vo;
        Context context;

        public BackgroundThreadSendText(Context ctx, Message message) {
            this.context = ctx;
            this.vo = message;
        }

        @Override
        public void run() {
            try {
                Intent i = new Intent();
                i.setAction(FILE_SEND_INTENT);
                i.putExtra(KEY_MESSAGE_OBJECT, vo);
                sendBroadcast(i);
            } catch (Exception e) {

            }

        }
    }

    private class BackgroundThreadStart implements Runnable {

        Context context;

        public BackgroundThreadStart(Context ctx) {
            this.context = ctx;
        }

        @Override
        public void run() {
            try {
                if (!MessengerConnectionService.started) {
                    MessengerConnectionService.startService(context);
                }
            } catch (Exception e) {

            }

        }
    }


    private String constructUri(String[] params) {

        String s = WsConfig.URL_WEBSOCKET;
        String[] p = s.split("\\|");
        s = p[0];
        for (int i = 0; i < params.length; i++) {
            p[i + 1] = p[i + 1] + "=" + params[i];
            s = s + p[i + 1];
        }
        return s;
    }

    private class BackgroundThreadSendTextGroup {
        // private WebSocketClient client;
        Message vo;
        Context context;
        String[] params;
        private Utils utils;

        public BackgroundThreadSendTextGroup(Context ctx, Message message) {
            this.context = ctx;
            this.vo = message;
            utils = new Utils(ctx);
            if (NetworkInternetConnectionStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
                if (ConversationGroupActivity.client != null && ConversationGroupActivity.client.isConnected()) {
                    String messageToSend = vo.getMessage();
                    if (vo.getType().equals(Message.TYPE_VIDEO) || vo.getType().equals(Message.TYPE_IMAGE)) {
                        String[] s = messageToSend.split(";");
                        String caption = "";
                        if (s.length == 3) {
                            caption = s[2];
                        }
                        messageToSend = ";" + s[1] + ";" + caption;
                    }
                    String messageid = getRandomString();
                    ConversationGroupActivity.client.send(utils.getSendMessageGroupJSON(
                            utils.messageC(vo.getType(), messageToSend, vo.getPacketId(), vo.getSourceInfo()), vo.getType(),
                            vo.getType() == Message.TYPE_TEXT ? 0 : 1, 0, vo.getDestination(),
                            0, messageid, "0"));

                    vo.setStatus(Message.STATUS_SENT);
                    messengerHelper.updateData(vo);
                    if (!vo.getType().equalsIgnoreCase(Message.STATUS_TYPE_DELIVER)) {
                        Intent intent = new Intent(MessengerConnectionService.ACTION_MESSAGE_DELIVERED);
                        intent.putExtra(KEY_MESSAGE_OBJECT, vo);
                        sendBroadcast(intent);
                    }
                }
            }
        }
    }

    private String getRandomString() {
        long currentTimeMillis = System.currentTimeMillis();
        SecureRandom random = new SecureRandom();

        char[] CHARSET_AZ_09 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        char[] result = new char[15];
        for (int i = 0; i < result.length; i++) {
            int randomCharIndex = random.nextInt(CHARSET_AZ_09.length);
            result[i] = CHARSET_AZ_09[randomCharIndex];
        }

        String resRandom = String.valueOf(currentTimeMillis) + new String(result);

        return resRandom;
    }

    private class BackgroundThreadCekTheme implements Runnable {

        Message vo;
        Context context;

        public BackgroundThreadCekTheme(Context ctx) {
            this.context = ctx;
        }

        @Override
        public void run() {
            try {
                if (NetworkInternetConnectionStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
                    String key = new ValidationsKey().getInstance(getApplicationContext()).key(false);
                    if (!key.equalsIgnoreCase("null")) {
                        new requestTheme(getApplicationContext()).execute(key);
                    }
                }

            } catch (Exception e) {

            }

        }
    }

    class requestTheme extends AsyncTask<String, Void, String> {

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 3 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();

        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content = null;
        private boolean error = false;
        private Context mContext;
        String code2;
        String desc;
        String title = "";
        String descripsi;
        String backgroud;
        String color;
        String logo;
        String logo2;
        String logoHeader;

        public requestTheme(Context context) {
            this.mContext = context;

        }


        @Override
        protected void onPreExecute() {

        }

        protected String doInBackground(String... key) {
            try {
                Contact contact = messengerHelper.getMyContact();
                HttpClient httpClient = HttpHelper
                        .createHttpClient(getApplicationContext());
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);

                nameValuePairs.add(new BasicNameValuePair("username", contact.getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("key", key[0]));
                nameValuePairs.add(new BasicNameValuePair("nama_theme", new Validations().getInstance(getApplicationContext()).getTitle()));
                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                HttpPost post = new HttpPost(URL_GET_REQUEST);
                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));


                //Response from the Http Request
                response = httpclient.execute(post);
                StatusLine statusLine = response.getStatusLine();

                //Check the Http Request for success

                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    content = out.toString();
                    JSONObject result = new JSONObject(content);
                    if (content.contains("\"code\"")) {
                        desc = result.getString("description");
                        error = true;
                    } else {
                        error = false;
                        if (content.contains("\"jumlah\"")) {
                            JSONArray menuitemArray = result.getJSONArray("themes_list");
                            for (int i = 0; i < menuitemArray.length(); i++) {
                                title = menuitemArray.getJSONObject(i).getString("nama").toString();
                                descripsi = menuitemArray.getJSONObject(i).getString("deskripsi").toString();
                                color = menuitemArray.getJSONObject(i).getString("color_code").toString();
                                backgroud = menuitemArray.getJSONObject(i).getString("background").toString();
                                logo = menuitemArray.getJSONObject(i).getString("logo").toString();
                                logo2 = menuitemArray.getJSONObject(i).getString("logo2").toString();
                                logoHeader = menuitemArray.getJSONObject(i).getString("logo_chat").toString();
                                logo2 = menuitemArray.getJSONObject(i).getString("logo2").toString();
                            }
                        }
                    }
                } else {
                    //Closes the connection.
                    error = true;
                    content = statusLine.getReasonPhrase();
                    response.getEntity().getContent().close();
                    throw new IOException(content);
                }

            } catch (ClientProtocolException e) {
                content = e.getMessage();
                error = true;
            } catch (IOException e) {
                content = e.getMessage();
                error = true;
            } catch (Exception e) {
                error = true;
            }

            return content;
        }

        protected void onCancelled() {
        }

        protected void onPostExecute(String content) {
            if (error) {
                if (content.contains("invalid_key")) {
                    if (NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)) {
                        String key = new ValidationsKey().getInstance(getApplicationContext()).key(true);
                        if (!key.equalsIgnoreCase("null")) {
                            new requestTheme(mContext).execute(key);
                        }
                    }
                }

            } else {
                if (!title.equalsIgnoreCase("") || title != null) {
                    ArrayList<String> skinArrayList = new ArrayList<String>();
                    db = new IntervalDB(getApplicationContext());
                    db.open();
                    skinArrayList = db.retriveallSkinTitle();
                    db.close();
                    boolean downloadSkin = true;
                    for (String a : skinArrayList) {
                        if (a.equalsIgnoreCase(title)) {
                            downloadSkin = false;
                        }
                    }
                    if (downloadSkin) {
                        new DownloadImages(getApplicationContext(),
                                title,
                                descripsi,
                                color,
                                "https://" + MessengerConnectionService.HTTP_SERVER + "/uploads/skins/" + logo,
                                "https://" + MessengerConnectionService.HTTP_SERVER + "/uploads/skins/" + logoHeader,
                                "https://" + MessengerConnectionService.HTTP_SERVER + "/uploads/skins/" + logo2)
                                .execute();
                    } else {
                        if (NetworkInternetConnectionStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
                            String key = new ValidationsKey().getInstance(getApplicationContext()).key(true);
                            if (!key.equalsIgnoreCase("null")) {
                                new cekApply(getApplicationContext()).execute(key, title);
                            }
                        }
                    }
                }
            }
        }
    }


    class DownloadImages extends AsyncTask {
        String[] URLs;
        private Context mContext;
        String title, descripsi, color;
        ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();

        public DownloadImages(Context context, String title, String descripsi, String color, String data1, String data2, String data3) {
            URLs = new String[]{data1, data2, data3};
            mContext = context;
            this.title = title;
            this.descripsi = descripsi;
            this.color = color;
        }

        @Override
        protected Object doInBackground(Object... objects) {
            for (int i = 0; i < URLs.length; i++) {
                try {
                    URL url = new URL(URLs[i]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    bitmapArray.add(BitmapFactory.decodeStream(input));
                    publishProgress(i);
                } catch (Exception e) {
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            if (bitmapArray.size() == 3) {
                db.open();
                Cursor cursor = db.getCountSkin();
                Skin skin = new Skin(title, descripsi, "#" + color, bitmapArray.get(0), bitmapArray.get(1), bitmapArray.get(2));
                cursor.close();
                db.createSkin(skin);
                db.close();

                if (NetworkInternetConnectionStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
                    String key = new ValidationsKey().getInstance(getApplicationContext()).key(true);
                    if (!key.equalsIgnoreCase("null")) {
                        new cekApply(getApplicationContext()).execute(key, title);
                    }
                }
            }
        }
    }


    class cekApply extends AsyncTask<String, Void, String> {

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 3 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();

        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private JSONObject jObject;
        private Context mContext;
        private String content = null;
        private boolean error = false;
        String code2 = "400";
        String desc;
        String themesName;
        private MessengerDatabaseHelper messengerHelper;

        public cekApply(Context context) {
            this.mContext = context;

        }

        @Override
        protected void onPreExecute() {
        }

        InputStreamReader reader = null;

        protected String doInBackground(String... key) {

            try {
                themesName = key[1];
                HttpClient httpClient = HttpHelper
                        .createHttpClient(mContext);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);

                if (messengerHelper == null) {
                    messengerHelper = MessengerDatabaseHelper.getInstance(mContext);
                }

                nameValuePairs.add(new BasicNameValuePair("username", messengerHelper.getMyContact().getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("key", key[0]));
                nameValuePairs.add(new BasicNameValuePair("nama_theme", new Validations().getInstance(mContext).getTitle()));
                nameValuePairs.add(new BasicNameValuePair("nama_theme_baru", key[1]));

                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                HttpPost post = new HttpPost(URL_CEK_APPLY);
                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));


                //Response from the Http Request
                response = httpclient.execute(post);
                StatusLine statusLine = response.getStatusLine();

                //Check the Http Request for success


                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    content = out.toString();
                    jObject = new JSONObject(content);
                    if (content.contains("\"code\"")) {
                        code2 = jObject.getString("code");
                        desc = jObject.getString("description");
                        error = true;
                    } else {
                        if (content.contains("\"boleh_ganti\"")) {
                            code2 = jObject.getString("boleh_ganti");
                            desc = "Your Theme is locked until " + jObject.getString("tgl_habis");
                        }
                    }
                } else {
                    //Closes the connection.
                    error = true;
                    content = statusLine.getReasonPhrase();
                    response.getEntity().getContent().close();
                    throw new IOException(content);
                }

            } catch (ClientProtocolException e) {
                content = e.getMessage();
                error = true;
            } catch (IOException e) {
                content = e.getMessage();
                error = true;
            } catch (Exception e) {
                error = true;
            }

            return content;
        }

        protected void onCancelled() {

        }

        protected void onPostExecute(String content) {
            if (error) {
                if (content.contains("invalid_key")) {
                    if (NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)) {
                        String key = new ValidationsKey().getInstance(mContext).key(true);
                        if (!key.equalsIgnoreCase("null")) {
                            new cekApply(mContext).execute(key, themesName);
                        }
                    }
                }
            } else {
                if (code2.equalsIgnoreCase("true")) {
                    IntervalDB db = new IntervalDB(mContext);
                    db.open();
                    Cursor cursor2 = db.getSingleContact(4);
                    if (cursor2.getCount() > 0) {
                        db.deleteContact(4);
                    }
                    Interval interval = new Interval();
                    interval.setId(4);
                    interval.setTime(themesName);
                    db.createContact(interval);
                    cursor2.close();
                    db.close();
                }
            }
        }

    }

    private class BackgroundThreadGetIklan implements Runnable {

        Message vo;
        Context context;

        public BackgroundThreadGetIklan(Context ctx) {
            this.context = ctx;
        }

        @Override
        public void run() {
            try {
                if (NetworkInternetConnectionStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
                    DownloadIkanTask task = new DownloadIkanTask(context);
                    task.execute(new String[]{"https://" + MessengerConnectionService.HTTP_SERVER + "/iklan/text.php?id="});
                }

            } catch (Exception e) {

            }

        }
    }

    private class DownloadIkanTask extends AsyncTask<String, Void, String> {
        Context mContext;

        public DownloadIkanTask(Context context) {
            this.mContext = context;

        }

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            for (String url : urls) {

                if (messengerHelper == null) {
                    messengerHelper = MessengerDatabaseHelper.getInstance(mContext);
                }

                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url + messengerHelper.getMyContact().getJabberId());
                try {
                    HttpResponse execute = client.execute(httpGet);
                    InputStream content = execute.getEntity().getContent();

                    BufferedReader buffer = new BufferedReader(
                            new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            new Validations().getInstance(mContext).setContentValidation(14, result);
        }
    }

    private class BackgroundThreadCekApps implements Runnable {

        String paket;
        String id;
        Context context;
        private Timer timer = new Timer();
        int timeLeft = 60;

        public BackgroundThreadCekApps(Context ctx, String paket, String idOffers) {
            this.context = ctx;
            this.paket = paket;
            this.id = idOffers;
        }

        @Override
        public void run() {
            try {
                doSomethingRepeatedly();
            } catch (Exception e) {

            }

        }

        public boolean isPackageInstalled(Context context, String packageName) {
            final PackageManager packageManager = context.getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(packageName);
            if (intent == null) {
                return false;
            }
            List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            return list.size() > 0;
        }

        private void doSomethingRepeatedly() {
            timer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    boolean installed = isPackageInstalled(context, paket);

                    if (installed) {

                        if (NetworkInternetConnectionStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
                            String key = new ValidationsKey().getInstance(getApplicationContext()).key(true);
                            if (!key.equalsIgnoreCase("null")) {
                                new laporOffers(getApplicationContext()).execute(key, id);
                            }
                        }
                        timer.cancel();
                    }

                    if (timeLeft >= 1) {
                        timeLeft--;
                    } else {
                        timer.cancel();
                    }
                }
            }, 0, (1 * 60) * 1000);
        }


    }

    class laporOffers extends AsyncTask<String, Void, String> {

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 3 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();

        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private JSONObject jObject;
        private Context mContext;
        private String content = null;
        private boolean error = false;
        String code2 = "400";
        String desc;
        String themesName;
        private MessengerDatabaseHelper messengerHelper;

        public laporOffers(Context context) {
            this.mContext = context;

        }

        @Override
        protected void onPreExecute() {
        }

        InputStreamReader reader = null;

        protected String doInBackground(String... key) {

            try {
                themesName = key[1];
                HttpClient httpClient = HttpHelper
                        .createHttpClient(mContext);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);

                if (messengerHelper == null) {
                    messengerHelper = MessengerDatabaseHelper.getInstance(mContext);
                }

                nameValuePairs.add(new BasicNameValuePair("username", messengerHelper.getMyContact().getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("key", key[0]));
                nameValuePairs.add(new BasicNameValuePair("id_offer", key[1]));

                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                HttpPost post = new HttpPost(URL_LAPOR_OFFERS);
                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                response = httpclient.execute(post);
                StatusLine statusLine = response.getStatusLine();

                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    content = out.toString();
                } else {
                    //Closes the connection.
                    error = true;
                    content = statusLine.getReasonPhrase();
                    response.getEntity().getContent().close();
                    throw new IOException(content);
                }

            } catch (ClientProtocolException e) {
                content = e.getMessage();
                error = true;
            } catch (IOException e) {
                content = e.getMessage();
                error = true;
            } catch (Exception e) {
                error = true;
            }

            return content;
        }

        protected void onCancelled() {

        }

        protected void onPostExecute(String content) {
            if (error) {
                if (content.contains("invalid_key")) {
                    if (NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)) {
                        String key = new ValidationsKey().getInstance(mContext).key(true);
                        if (!key.equalsIgnoreCase("null")) {
                            new laporOffers(mContext).execute(key, themesName);
                        }
                    }
                }
            } else {
                /*if(code2.equalsIgnoreCase("true")){
                    IntervalDB  db = new IntervalDB(mContext);
                    db.open();
                    Cursor cursor2 = db.getSingleContact(4);
                    if (cursor2.getCount()>0) {
                        db.deleteContact(4);
                    }
                    Interval interval = new Interval();
                    interval.setId(4);
                    interval.setTime(themesName);
                    db.createContact(interval);
                    cursor2.close();
                    db.close();
                }*/
            }
        }

    }


    private class downloadValueForm extends AsyncTask<String, Integer, String> {
        long totalSize = 0;
        String pId = "";
        String idNotif = "";
        String username = "";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return downloadForm(params[0], params[1], params[2], params[3], params[4]);
        }

        @SuppressWarnings("deprecation")
        private String downloadForm(String URL, final String user, String id_room, String pId_, final String id_notif) {
            String responseString = null;
            pId = pId_;
            username = user;
            idNotif = id_notif;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(URL);

            try {
                ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username_room", user));
                params.add(new BasicNameValuePair("id_rooms_tab", id_room));

                if (pId != null || !pId.equalsIgnoreCase("")) {
                    String[] ff = pId.split("\\|");
                    if (ff.length == 2) {
                        params.add(new BasicNameValuePair("parent_id", ff[1]));
                        params.add(new BasicNameValuePair("id_list_push", ff[0]));
                    }
                }

                // totalSize = entity.getContentLength();

                httppost.setEntity(new UrlEncodedFormEntity(params));

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode == 200) {
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Calendar cal = Calendar.getInstance();
                String time_str = dateFormat.format(cal.getTime());
                JSONObject jsonRootObject = new JSONObject(result);
                String username = jsonRootObject.getString("username_room");
                String id_rooms_tab = jsonRootObject.getString("id_rooms_tab");

                BotListDB botListDB = BotListDB.getInstance(getApplicationContext());

                RoomsDetail orderModel = new RoomsDetail(pId, id_rooms_tab, username, jsonRootObject.getString("list_pull"), "", time_str, "value");
                botListDB.insertRoomsDetail(orderModel);

                NotificationManager manager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());

                builder.setContentTitle("Download value");
                builder.setContentText(new GetRealNameRoom().getInstance(getApplicationContext()).getName(username));
                builder.setSmallIcon(R.drawable.ic_notif);
                builder.setContentText("complete")
                        .setProgress(0, 0, false);
                manager.notify(Integer.parseInt(idNotif), builder.build());

                SubmitingRoomDB submitingRoomDB = SubmitingRoomDB.getInstance(getApplicationContext());
                submitingRoomDB.deleteContact(Long.parseLong(idNotif));


            } catch (JSONException e) {
                NotificationManager manager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());

                builder.setContentTitle("Download value");
                builder.setContentText(new GetRealNameRoom().getInstance(getApplicationContext()).getName(username));
                builder.setSmallIcon(R.drawable.ic_notif);
                builder.setContentText("failed")
                        .setProgress(0, 0, false);
                manager.notify(Integer.parseInt(idNotif), builder.build());

                SubmitingRoomDB submitingRoomDB = SubmitingRoomDB.getInstance(getApplicationContext());
                submitingRoomDB.deleteContact(Long.parseLong(idNotif));


                e.printStackTrace();
            }

        }
    }

    private class downloadListForm extends AsyncTask<String, Integer, String> {
        long totalSize = 0;
        String pId = "";
        String idNotif = "";
        String username = "";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return downloadForm(params[0], params[1], params[2], params[3], params[4]);
        }

        @SuppressWarnings("deprecation")
        private String downloadForm(String URL, final String user, String id_room, String pId_, final String id_notif) {
            String responseString = null;
            pId = pId_;
            username = user;
            idNotif = id_notif;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(URL);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));

                                NotificationManager manager =
                                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());

                                builder.setContentTitle("Download value");
                                builder.setContentText(new GetRealNameRoom().getInstance(getApplicationContext()).getName(user));
                                builder.setSmallIcon(R.drawable.ic_notif);
                                builder.setProgress(100, (int) ((num / (float) totalSize) * 100), true);
                                manager.notify(Integer.parseInt(idNotif), builder.build());
                            }
                        });

                entity.addPart("username_room", new StringBody(user));
                entity.addPart("id_rooms_tab", new StringBody(id_room));

                if (pId != null || !pId.equalsIgnoreCase("")) {
                    String[] ff = pId.split("\\|");
                    if (ff.length == 2) {
                        entity.addPart("parent_id", new StringBody(ff[1]));
                        entity.addPart("id_list_push", new StringBody(ff[0]));
                    }
                }


                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();


                if (statusCode == 200) {
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Calendar cal = Calendar.getInstance();
                String time_str = dateFormat.format(cal.getTime());
                JSONObject jsonRootObject = new JSONObject(result);
                String username = jsonRootObject.getString("username_room");
                String id_rooms_tab = jsonRootObject.getString("id_rooms_tab");

                BotListDB botListDB = BotListDB.getInstance(getApplicationContext());

                RoomsDetail orderModel = new RoomsDetail(pId, id_rooms_tab, username, jsonRootObject.getString("list_pull"), "", time_str, "value");
                botListDB.insertRoomsDetail(orderModel);

                NotificationManager manager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());

                builder.setContentTitle("Download value");
                builder.setContentText(new GetRealNameRoom().getInstance(getApplicationContext()).getName(username));
                builder.setSmallIcon(R.drawable.ic_notif);
                builder.setContentText("complete")
                        .setProgress(0, 0, false);
                manager.notify(Integer.parseInt(idNotif), builder.build());

                SubmitingRoomDB submitingRoomDB = SubmitingRoomDB.getInstance(getApplicationContext());
                submitingRoomDB.deleteContact(Long.parseLong(idNotif));


            } catch (JSONException e) {
                NotificationManager manager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());

                builder.setContentTitle("Download value");
                builder.setContentText(new GetRealNameRoom().getInstance(getApplicationContext()).getName(username));
                builder.setSmallIcon(R.drawable.ic_notif);
                builder.setContentText("failed")
                        .setProgress(0, 0, false);
                manager.notify(Integer.parseInt(idNotif), builder.build());

                SubmitingRoomDB submitingRoomDB = SubmitingRoomDB.getInstance(getApplicationContext());
                submitingRoomDB.deleteContact(Long.parseLong(idNotif));


                e.printStackTrace();
            }

        }
    }


}


