package com.byonchat.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.byonchat.android.provider.FilesURL;
import com.byonchat.android.provider.FilesURLDatabaseHelper;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.utils.MediaProcessingUtil;
import com.byonchat.android.utils.UploadService;
import com.byonchat.android.utils.Validations;
import com.byonchat.android.videotrimmer.K4LVideoTrimmer;
import com.byonchat.android.videotrimmer.interfaces.OnK4LVideoListener;
import com.byonchat.android.videotrimmer.interfaces.OnTrimVideoListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;

import static com.byonchat.android.utils.Utility.reportCatch;

/**
 * Created by byonc on 5/9/2017.
 */

public class ConfirmationSendFileVideo extends AppCompatActivity implements OnTrimVideoListener, OnK4LVideoListener {
    String uriImage;
    String name;
    String type;
    String title;
    String from;
    int typeChat;

    public static final String CLOSEMEMEACTIVITY = "byonchat.meme.close.activity";
    private K4LVideoTrimmer mVideoTrimmer;
    private ProgressDialog mProgressDialog;
    String path = null, outpath = null, startpos = null, endpos = null, fileSizeInMB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmation_send_file_video);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new Validations().getInstance(getApplicationContext()).header(getWindow()));

        try {
            uriImage = getIntent().getStringExtra("file");
            name = getIntent().getStringExtra("name");
            type = getIntent().getStringExtra("type");
            from = getIntent().getStringExtra("from");
            title = getIntent().getStringExtra(ConversationActivity.KEY_TITLE);
            typeChat = getIntent().getIntExtra(ConversationActivity.KEY_CONVERSATION_TYPE, 0);

            //setting progressbar
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Trimming your video...");

            mVideoTrimmer = ((K4LVideoTrimmer) findViewById(R.id.timeLine));

            if (mVideoTrimmer != null) {

                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(getApplicationContext(), Uri.parse(uriImage));
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                File file = new File(uriImage);

                if (file.exists()) {
                    double bytes = file.length();
                    double kilobytes = (bytes / 1024);
                    double megabytes = (kilobytes / 1024);
                    if (megabytes > 16) {
                        if (Integer.parseInt(String.valueOf(time)) > (3 * 60000)) {
                            //video dipotong dan tidak di resize//169877
                            mVideoTrimmer.setMaxDuration(10, true);
                        } else {
                            //video tidak di potong dan di resize
                            mVideoTrimmer.setMaxDuration(Integer.parseInt(String.valueOf(time)), false);
                        }
                    } else {
                        //video tidak di resize
                        mVideoTrimmer.setMaxDuration(Integer.parseInt(String.valueOf(time)), false);
                    }
                } else {
                    //tidak di compress
                    mVideoTrimmer.setMaxDuration(Integer.parseInt(String.valueOf(time)), false);
                }

                mVideoTrimmer.setOnTrimVideoListener(this);
                mVideoTrimmer.setOnK4LVideoListener(this);
                mVideoTrimmer.setVideoURI(Uri.parse(uriImage));
                mVideoTrimmer.setVideoInformationVisibility(true);
            }
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    public void DoDone() {
        try {
            Intent intent = new Intent(CLOSEMEMEACTIVITY);
            sendOrderedBroadcast(intent, null);
            finish();

            if (ConversationActivity.instance != null) {
                try {
                    ConversationActivity.instance.finish();
                } catch (Exception e) {
                }
            }

            Intent i = new Intent(this, ConversationActivity.class);
            String jabberId = name;
            String action = this.getIntent().getAction();
            if (Intent.ACTION_SEND.equals(action)) {
                Bundle extras = this.getIntent().getExtras();
                if (extras.containsKey(Intent.EXTRA_STREAM)) {
                    try {
                        Uri uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);
                        String pathToSend = MediaProcessingUtil.getRealPathFromURI(
                                this.getContentResolver(), uri);
                        i.putExtra(ConversationActivity.KEY_FILE_TO_SEND,
                                pathToSend);
                    } catch (Exception e) {
                        Log.e(getClass().getSimpleName(),
                                "Error getting file from action send: "
                                        + e.getMessage(), e);
                    }
                }
            }

            i.putExtra(ConversationActivity.KEY_JABBER_ID, jabberId);
            startActivity(i);
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    public void sendFile(Message message) {
        try {
            Message vo = message;
            MessengerDatabaseHelper messengerHelper = MessengerDatabaseHelper.getInstance(getApplicationContext());
            messengerHelper.insertData(vo);

            FilesURLDatabaseHelper dbUpload = new FilesURLDatabaseHelper(this);
            FilesURL files = new FilesURL((int) vo.getId(), "1", "upload");
            dbUpload.open();
            dbUpload.insertFilesUpload(files);
            dbUpload.close();

            Intent intent = new Intent(this, UploadService.class);
            intent.putExtra(UploadService.ACTION, "getLinkUpload");
            intent.putExtra(UploadService.KEY_MESSAGE, vo);
            intent.putExtra(UploadService.KEY_STATUS_VIDEO, "1"); /* 1 = first upload, 2 = second upload when failed */
            startService(intent);
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    private Message createNewMessage(String message, String sourceAddr, String destination, int conversationType, String type) {
        Message vo = new Message(sourceAddr, destination, message);
        try {
            vo.setType(type);
            vo.setSendDate(new Date());
            vo.setStatus(Message.STATUS_INPROGRESS);
            vo.generatePacketId();
            if (conversationType == ConversationActivity.CONVERSATION_TYPE_GROUP) {
                vo.setGroupChat(true);
                vo.setSourceInfo(sourceAddr);
            }
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
        return vo;
    }

    public String jsonMessage(String uriImage, String outpath, String startpos, String endpos, String fileSizeInMB, String caption) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("s", uriImage);
            obj.put("o", outpath);
            obj.put("sp", startpos);
            obj.put("ep", endpos);
            obj.put("m", fileSizeInMB);
            obj.put("c", caption);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTrimStarted() {
        /*mProgressDialog.show();*/
    }

    @Override
//    public void getResult(final Uri uri, final String textMessage) {
    public void getResult(final String json, final String textMessage) {
        /*mProgressDialog.cancel();*/

        try {
            JSONObject j = new JSONObject(json);
            path = j.getString("p");
            outpath = j.getString("o");
            startpos = j.getString("s");
            endpos = j.getString("e");
            fileSizeInMB = j.getString("m");

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (type.equalsIgnoreCase(Message.TYPE_VIDEO)) {
                String textCaption = textMessage != null ? textMessage : "";
                MessengerDatabaseHelper messengerHelper = MessengerDatabaseHelper.getInstance(getApplicationContext());

                Message msg = createNewMessage(jsonMessage(path, outpath, startpos, endpos, fileSizeInMB, textCaption), messengerHelper.getMyContact().getJabberId(), name, typeChat, type);
                sendFile(msg);
                DoDone();
            }
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    @Override
    public void cancelAction() {
        try {
            mVideoTrimmer.destroy();
            finish();
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    @Override
    public void onError(final String message) {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ConfirmationSendFileVideo.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    @Override
    public void onVideoPrepared() {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    Toast.makeText(ConfirmationSendFileVideo.this, "onVideoPrepared", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }


}
