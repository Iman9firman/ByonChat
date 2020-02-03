package com.byonchat.android.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.byonchat.android.ConversationActivity;
import com.byonchat.android.R;

import java.io.IOException;


/**
 * Created by pratama on 4/22/14.
 */
public class StreamService extends Service implements
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnBufferingUpdateListener {

    String URL_STREAM = "";
    String NAME_STREAM= "";
    String DESC_STREAM= "";
    /**
     * for educational only
     */
     //public static final String URL_STREAM = "http://203.150.225.71:8003";
 //    public static final String URL_STREAM = "http://live.indostreamserver.com:8800/gens?type=.mpeg";
 //    public static final String URL_STREAM = "http://live.indostreamserver.com:8800/mustang";

    // radio UNISI
   //public static final String URL_STREAM = "http://79.114.11.177:8000";
    //public static final String URL_STREAM = "rtmp://cdn-edge.sim-indonesia.com:443/radio";

    // notification
    private static final int NOTIFICATION_ID = 2002;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;
    private boolean isPausedInCall = false;
    private NotificationCompat.Builder builder;

    //intent
    private Intent bufferIntent;

    public static String BROADCAST_BUFFER = "com.byonchat.android.broadcastbuffer";

    private MediaPlayer mediaPlayer = new MediaPlayer();

    @Override
    public void onCreate() {
        super.onCreate();

        bufferIntent = new Intent(BROADCAST_BUFFER);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.reset();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        URL_STREAM =  intent.getStringExtra("streaminglink");
        NAME_STREAM =  intent.getStringExtra("streamingName");
        DESC_STREAM =  intent.getStringExtra("streamingDesc");
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (mediaPlayer != null) {
                            pauseMedia();
                            isPausedInCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        if (mediaPlayer != null) {
                            if (isPausedInCall) {
                                isPausedInCall = false;
                                playMedia();
                            }
                        }
                        break;
                }
            }
        };

        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        initNotification();

        mediaPlayer.reset();

        /**
         * play media
         */
        if (!mediaPlayer.isPlaying()) {
            try {
                Log.d("streamm", "" + URL_STREAM);
                mediaPlayer.setDataSource(URL_STREAM);

                // sent to UI radio is buffer
                sendBufferingBroadcast();

                mediaPlayer.prepareAsync();
            } catch (IllegalArgumentException e) {
                Log.d("error", e.getMessage());
            } catch (IllegalStateException e) {
                Log.d("error", e.getMessage());
            } catch (IOException e) {
                Log.d("error", e.getMessage());
            }
        }

        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
    }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        stopMedia();
        stopSelf();
    }


    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Toast.makeText(this, "Error not valid playback", Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Toast.makeText(this, "Error server died", Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Toast.makeText(this, "Error unknown", Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }


    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        // sent to UI, audio has buffered
        sendBufferCompleteBroadcast();

        playMedia();
    }


    private void pauseMedia() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
    }

    private void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    private void stopMedia() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.stop();
    }

    /**
     * sent buffering
     */
    private void sendBufferingBroadcast() {
        bufferIntent.putExtra("buffering", "1");
        sendBroadcast(bufferIntent);
    }

    /**
     * sent buffering complete
     */
    private void sendBufferCompleteBroadcast() {
        bufferIntent.putExtra("buffering", "0");
        sendBroadcast(bufferIntent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("tag", "remove notification");
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }

        if (phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }

        cancelNotification();
    }

    /**
     * show notificaiton
     */
    private void initNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        /*PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);*/
        Intent destIntent = new Intent(this, ConversationActivity.class);
        destIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        destIntent.putExtra(ConversationActivity.KEY_JABBER_ID, NAME_STREAM);

        builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(Utility.roomName(getApplicationContext(),NAME_STREAM,true))
                .setContentText(DESC_STREAM);
        builder.setContentIntent(PendingIntent.getActivity(this, 0,
                destIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        builder.setOngoing(true);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    /**
     * cancel notification
     */
    private void cancelNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
        builder.setOngoing(false);
    }


}
