package com.honda.android.personalRoom;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.honda.android.R;
import com.honda.android.communication.MessengerConnectionService;
import com.honda.android.personalRoom.utils.AndroidMultiPartEntity;
import com.honda.android.provider.Message;
import com.honda.android.widget.VideoSlaceSeekBar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by lukma on 3/17/2016.
 */
public class DialogFragmentVideo extends DialogFragment implements DialogInterface.OnDismissListener {
    private static final String TAG = FragmentMyPicture.class.getSimpleName();

    private ProgressBar progressBar;
    private String filePath = null, userid, jam, menit, detik, waktu;
    private VideoView vidPreview;
    private ImageView imagePlay, thumbnail;
    VideoSlaceSeekBar videoSliceSeekBar;
    private EditText mTitle, mDescription;
    private Button btnUpload, btnCancel;
    private DialogInterface.OnDismissListener onDismissListener;
    public static final String FILE_UPLOAD_URL = "https://" + MessengerConnectionService.HTTP_SERVER2 + "/personal_room/webservice/proses/upload_video.php";

    public static DialogFragmentVideo newInstance(String str, String uid) {
        DialogFragmentVideo f = new DialogFragmentVideo();
        Bundle args = new Bundle();
        args.putString("filePath", str);
        args.putString("userid", uid);
        f.setArguments(args);

        return f;
    }

    public void DialogFragmentVideo(String url, String uid) {
        this.filePath = url;
        this.userid = uid;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialog = inflater.inflate(R.layout.dialog_detail_video, container, false);
        mTitle = (EditText) dialog.findViewById(R.id.title);
        mDescription = (EditText) dialog.findViewById(R.id.description);
        thumbnail = (ImageView) dialog.findViewById(R.id.thumbnail);
        imagePlay = (ImageView) dialog.findViewById(R.id.imagePlay);
        videoSliceSeekBar = (VideoSlaceSeekBar) dialog.findViewById(R.id.seek_bar);
        vidPreview = (VideoView) dialog.findViewById(R.id.video_preview);
        progressBar = (ProgressBar) dialog.findViewById(R.id.progressBar);
        btnUpload = (Button) dialog.findViewById(R.id.btnSUBMIT);
        btnCancel = (Button) dialog.findViewById(R.id.btnCANCEL);

        if (filePath != null) {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(filePath);
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long timeInmillisec = Long.parseLong(time);
            long duration = timeInmillisec / 1000;
            long hours = duration / 3600;
            long minutes = (duration - hours * 3600) / 60;
            long seconds = duration - (hours * 3600 + minutes * 60);

            if (hours < 10) {
                jam = "0" + hours;
            } else if (minutes >= 10) {
                jam = String.valueOf(hours);
            }

            if (minutes < 10) {
                menit = "0" + minutes;
            } else if (minutes >= 10) {
                menit = String.valueOf(minutes);
            }

            if (seconds < 10) {
                detik = "0" + seconds;
            } else if (seconds >= 10) {
                detik = String.valueOf(seconds);
            }

            if (jam.equalsIgnoreCase("0")) {
                waktu = jam + ":" + menit + ":" + detik;
            } else {
                waktu = menit + ":" + detik;
            }

            previewMedia();
        } else {
            Toast.makeText(getActivity().getApplicationContext(),
                    "Sorry, file path is missing!", Toast.LENGTH_LONG).show();
        }


        imagePlay.setVisibility(View.VISIBLE);
        imagePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thumbnail.setVisibility(View.GONE);
                vidPreview.setVisibility(View.VISIBLE);
                performVideoViewClick();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                File f = new File(filePath);
                long length = f.length();
                length = length / 1024;
                if (length > 20000) {
                    Toast.makeText(getActivity(), "File is too large", Toast.LENGTH_SHORT).show();
                } else {
                    if (mTitle.getText().toString().trim().length() == 0) {
                        mTitle.setError("Title is required!");
                    } else if (mDescription.getText().toString().trim().length() == 0) {
                        mDescription.setError("Description is required!");
                    } else {
                        new UploadFileToServer().execute();
                    }
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (getDialog() != null) {
                    dismiss();
                }
            }
        });

        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        filePath = getArguments().getString("filePath");
//        userid = getArguments().getString("userid");
    }

    public void previewMedia() {
        vidPreview.setVisibility(View.VISIBLE);
        vidPreview.setVideoPath(filePath);
        Bitmap bMap = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MICRO_KIND);
        thumbnail.setImageBitmap(bMap);
    }

    private void performVideoViewClick() {
        if (vidPreview.isPlaying()) {
            imagePlay.setImageDrawable(getActivity().getApplicationContext().getResources().getDrawable(R.drawable.ic_play_white));
            vidPreview.pause();
            videoSliceSeekBar.setSliceBlocked(false);
            videoSliceSeekBar.removeVideoStatusThumb();
        } else {
            imagePlay.setImageBitmap(null);
            vidPreview.seekTo(videoSliceSeekBar.getLeftProgress());
            vidPreview.start();
            videoSliceSeekBar.setSliceBlocked(true);
            videoSliceSeekBar.videoPlayingProgress(videoSliceSeekBar.getLeftProgress());
            videoStateObserver.startVideoProgressObserving();
        }
    }

    private StateObserver videoStateObserver = new StateObserver();

    private class StateObserver extends Handler {

        private boolean alreadyStarted = false;

        private void startVideoProgressObserving() {
            if (!alreadyStarted) {
                alreadyStarted = true;
                sendEmptyMessage(0);
            }
        }

        private Runnable observerWork = new Runnable() {
            @Override
            public void run() {
                startVideoProgressObserving();
            }
        };

        public void handleMessage(Message msg) {
            alreadyStarted = false;
            videoSliceSeekBar.videoPlayingProgress(vidPreview.getCurrentPosition());
            if (vidPreview.isPlaying() && vidPreview.getCurrentPosition() < videoSliceSeekBar.getRightProgress()) {
                postDelayed(observerWork, 50);
            } else {

                if (vidPreview.isPlaying()) vidPreview.pause();

                videoSliceSeekBar.setSliceBlocked(false);
                videoSliceSeekBar.removeVideoStatusThumb();
            }
        }
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        long totalSize = 0;
        private ProgressDialog d = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            d.setMessage("uploading...");
            d.setIndeterminate(false);
            d.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            d.setProgress(0);
            d.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            d.setProgress(progress[0]);
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(FILE_UPLOAD_URL);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = new File(filePath);
                ContentType contentType = ContentType.create("video/mp4");
                entity.addPart("userid", new StringBody(userid));
                entity.addPart("file", new FileBody(sourceFile, contentType, sourceFile.getName()));
                entity.addPart("judul_video", new StringBody(mTitle.getText().toString()));
                entity.addPart("deskripsi_video", new StringBody(mDescription.getText().toString()));
                entity.addPart("durasi", new StringBody(waktu));
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
            d.dismiss();
            if (result.equalsIgnoreCase("1")) {
                dismiss();
            } else if (result.equalsIgnoreCase("0")) {
                Toast.makeText(getActivity(), "Failed to upload video", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                Log.w("upload_gagal", result);
            }

            super.onPostExecute(result);
        }
    }
}
