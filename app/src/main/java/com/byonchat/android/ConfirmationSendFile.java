package com.byonchat.android;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.personalRoom.model.NotesPhoto;
import com.byonchat.android.personalRoom.model.PictureModel;
import com.byonchat.android.provider.Files;
import com.byonchat.android.provider.FilesDatabaseHelper;
import com.byonchat.android.provider.FilesURL;
import com.byonchat.android.provider.FilesURLDatabaseHelper;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.utils.ImageLoadingUtils;
import com.byonchat.android.utils.ImageUtil;
import com.byonchat.android.utils.MediaProcessingUtil;
import com.byonchat.android.utils.TouchImageView;
import com.byonchat.android.utils.UploadService;
import com.byonchat.android.utils.Validations;
import com.byonchat.android.widget.VideoSlaceSeekBar;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import static com.byonchat.android.ConfirmationSendFileMultiple.EXTRA_PHOTOS;

/*import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;*/


public class ConfirmationSendFile extends AppCompatActivity {
    TouchImageView imageView;
    ImageView imagePlay;
    Button btnCancel;
    Button btnSend;
    String uriImage, uriDecoded;
    String name;
    String type;
    String title;
    String from;
    int typeChat;
    Bitmap myBitmap = null;
    EditText textMessage;

    TextView textViewLeft, textViewRight;
    VideoSlaceSeekBar videoSliceSeekBar;
    VideoView videoView;
    View videoControlBtn;
    View videoSabeBtn;
    private ImageLoadingUtils utils;
    LruCache<String, Bitmap> memoryCache;
    String iname;
    boolean isFrom = false;
    private long numberOfImages = 0;
    private String Image_path = null;
    public static final String CLOSEMEMEACTIVITY = "byonchat.meme.close.activity";
    private List<NotesPhoto> photos;
    File compressedFile;
    public static final String EXTRA_PHOTOS = "photos";
    public static final String EXTRA_CAPTIONS = "captions";
    public ArrayList<NotesPhoto> notesPhotos;

    // FFmpeg ffmpeg;
    private static final String TAG = ConfirmationSendFile.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmation_send_file);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new Validations().getInstance(getApplicationContext()).header(getWindow()));
        //   getSupportActionBar().setIcon(new Validations().getInstance(getApplicationContext()).logoCustome());
        uriImage = getIntent().getStringExtra("file");
        name = getIntent().getStringExtra("name");

        ConfirmationSendFileMultiple.message.clear();
        if (getIntent().getExtras().containsKey("isFrom")) {
            isFrom = true;
        }
        type = getIntent().getStringExtra("type");
        from = getIntent().getStringExtra("from");
        title = getIntent().getStringExtra(ConversationActivity.KEY_TITLE);
        typeChat = getIntent().getIntExtra(ConversationActivity.KEY_CONVERSATION_TYPE, 0);

        textMessage = (EditText) findViewById(R.id.textMessage);
        imageView = (TouchImageView) findViewById(R.id.imageView);
        imagePlay = (ImageView) findViewById(R.id.imagePlay);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnSend = (Button) findViewById(R.id.btnSend);


        try {
            JSONObject filud = new JSONObject(uriImage);
            if (filud != null) {
                uriImage = filud.getString("s");
                if (filud.getString("c") != null) {
                    Pattern htmlPattern = Pattern.compile(".*\\<[^>]+>.*", Pattern.DOTALL);
                    boolean isHTML = htmlPattern.matcher(filud.getString("c")).matches();
                    if (isHTML) {
                        if (filud.getString("c").contains("<")) {
                            textMessage.setText(Html.fromHtml(Html.fromHtml(filud.getString("c")).toString()));
                        } else {
                            textMessage.setText(Html.fromHtml(filud.getString("c")));
                        }
                    } else {
                        textMessage.setText(Html.fromHtml(filud.getString("c")));
                    }

                }
            }
        } catch (Exception e) {

        }


        if (type.equalsIgnoreCase(Message.TYPE_VIDEO)) {
            imageView.setVisibility(View.GONE);
            textViewLeft = (TextView) findViewById(R.id.left_pointer);
            textViewRight = (TextView) findViewById(R.id.right_pointer);

            videoSliceSeekBar = (VideoSlaceSeekBar) findViewById(R.id.seek_bar);
            videoView = (VideoView) findViewById(R.id.video);
            videoControlBtn = findViewById(R.id.video_control_btn);
            videoSabeBtn = findViewById(R.id.saveButton);
            initVideoView(uriImage);
            loadFFMpegBinary();

            imagePlay.setVisibility(View.VISIBLE);
            imagePlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    performVideoViewClick();
                }
            });

        } else {
            if (!isFrom) {
                utils = new ImageLoadingUtils(this);
                imagePlay.setVisibility(View.GONE);
                new NystromImageCompression(true).execute(uriImage);
            } else {
                imagePlay.setVisibility(View.GONE);
                photos = getIntent().getParcelableArrayListExtra("photos");
                if (photos != null) {
                    initPhotos();
                } else {
                    finish();
                    return;
                }
            }
        }


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (type.equalsIgnoreCase(Message.TYPE_VIDEO)) {

                    boolean send = true;

                    if (type.equalsIgnoreCase(Message.TYPE_VIDEO)) {
                        final File file = new File(uriImage);
                        double bytes = file.length();
                        double kilobytes = (bytes / 1024);
                        double megabytes = (kilobytes / 1024);
                        if (megabytes > 16) {
                            send = false;
                        }
                    }

                    if (send) {
                        btnSend.setEnabled(false);
                        if (type.equalsIgnoreCase(Message.TYPE_VIDEO)) {
                            String textCaption = textMessage.getText().toString() != null ? textMessage.getText().toString() : "";
                            MessengerDatabaseHelper messengerHelper = MessengerDatabaseHelper.getInstance(getApplicationContext());

                            Message msg = createNewMessage(jsonMessage(uriImage, uriImage, "", "", "", textCaption), messengerHelper.getMyContact().getJabberId(), name, typeChat, type);
                            sendFile(msg);
                            DoDone();
                        }


               /*     String textCaption = textMessage.getText().toString() != null ? textMessage.getText().toString().replaceAll(";", ",") : "";
                    Intent intent;
                    intent = new Intent(getApplicationContext(), ConversationActivity.class);
                    if(from!=null){
                        intent = new Intent(getApplicationContext(), ConversationGroupActivity.class);
                        intent.putExtra(ConversationGroupActivity.EXTRA_KEY_NEW_PERSON, "0");
                        intent.putExtra(ConversationGroupActivity.EXTRA_KEY_STICKY, "0");
                    }
                    intent.putExtra(ConversationActivity.KEY_TITLE, title);
                    intent.putExtra(ConversationActivity.KEY_CONVERSATION_TYPE, typeChat);
                    intent.putExtra(ConversationActivity.KEY_JABBER_ID, name);
                    intent.putExtra(ConversationActivity.KEY_FILE_CONFIRMATION, uriImage + ";" + type + ";" + textCaption);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    finish();*/


                    } else {
                        Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getText(R.string.file_too_large), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (!isFrom) {
                        String textCaption = textMessage.getText().toString() != null ? textMessage.getText().toString() : "";
                        MessengerDatabaseHelper messengerHelper = MessengerDatabaseHelper.getInstance(getApplicationContext());

                        Message msg = createNewMessage(jsonMessage(uriImage, uriImage, "", "", "", textCaption), messengerHelper.getMyContact().getJabberId(), name, typeChat, type);
                        sendFile(msg);
                        DoDone();
//                    saveImage(utils.decodeBitmapFromPath(uriDecoded));
                    } else {
                        if (TextUtils.isEmpty(textMessage.getText().toString().trim())) {
                            textMessage.setError("Content is required!");
                        } else {
                            notesPhotos = new ArrayList<>();
                            NotesPhoto nphoto = new NotesPhoto(compressedFile, textMessage.getText().toString().trim());
                            notesPhotos.add(nphoto);

                            Intent data = new Intent();
                            data.putExtra(EXTRA_CAPTIONS, textMessage.getText().toString().trim());
                            data.putParcelableArrayListExtra(EXTRA_PHOTOS, (ArrayList<NotesPhoto>) notesPhotos);
                            setResult(RESULT_OK, data);
                            finish();
                        }
                    }
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    void initPhotos() {
        NotesPhoto photo = photos.get(0);
        CompressFile(photo.getPhotoFile());
    }

    void CompressFile(File file) {
        compressedFile = file;
        if (ImageUtil.isImage(file)) {
            try {
                compressedFile = ImageUtil.compressImage(file);
            } catch (NullPointerException e) {
                showError(getString(R.string.corrupted_file));
                return;
            }
        }

        if (!file.exists()) { //File have been removed, so we can not upload it anymore
            showError(getString(R.string.corrupted_file));
            return;
        }
        Picasso.with(getApplicationContext()).load(compressedFile).into(imageView);
    }

    public void DoDone() {
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
    }

    public void sendFile(Message message) {

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
        if (type.equals(Message.TYPE_VIDEO))
            intent.putExtra(UploadService.KEY_STATUS_VIDEO, "2"); /* 1 = first upload, 2 = second upload when failed */
        startService(intent);
    }

    private Message createNewMessage(String message, String sourceAddr, String destination, int conversationType, String type) {
        Message vo = new Message(sourceAddr, destination, message);
        vo.setType(type);
        vo.setSendDate(new Date());
        vo.setStatus(Message.STATUS_INPROGRESS);
        vo.generatePacketId();
        if (conversationType == ConversationActivity.CONVERSATION_TYPE_GROUP) {
            vo.setGroupChat(true);
            vo.setSourceInfo(sourceAddr);
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
            obj.put("c", caption);
            obj.put("m", fileSizeInMB);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadFFMpegBinary() {
      /*  try {
            if(ffmpeg == null) {
                Log.d(TAG, "ffmpeg : era nulo");
                ffmpeg = FFmpeg.getInstance(this);
            }
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    showUnsupportedExceptionDialog();
                }
                @Override
                public void onSuccess() {
                    Log.d(TAG, "ffmpeg : correct Loaded");
                }
            });
        } catch (FFmpegNotSupportedException e) {
            showUnsupportedExceptionDialog();
        } catch (Exception e) {
            Log.d(TAG, "EXception no controlada : "  + e);
        }*/
    }

    private void showUnsupportedExceptionDialog() {
      /*  new AlertDialog.Builder(ConfirmationSendFile.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.device_not_supported))
                .setMessage(getString(R.string.device_not_supported_message))
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ConfirmationSendFile.this.finish();
                    }
                })
                .create()
                .show();
*/
    }

    private void initVideoView(String path) {
        Log.w("ada", "disini");
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mp) {
                videoSliceSeekBar.setSeekBarChangeListener(new VideoSlaceSeekBar.SeekBarChangeListener() {
                    @Override
                    public void SeekBarValueChanged(int leftThumb, int rightThumb) {
                        textViewLeft.setText(getTimeForTrackFormat(leftThumb, true));
                        textViewRight.setText(getTimeForTrackFormat(rightThumb, true));
                    }
                });

                videoSliceSeekBar.setMaxValue(mp.getDuration());
                videoSliceSeekBar.setLeftProgress(0);
                //videoSliceSeekBar.setRightProgress(mp.getDuration());
                videoSliceSeekBar.setRightProgress(10000); //10 segundos como máximo de entrada
                videoSliceSeekBar.setProgressMinDiff((5000 * 100) / mp.getDuration()); //Diferencia mínima de 5 segundos
                videoSliceSeekBar.setProgressMaxDiff((10000 * 100) / mp.getDuration());//Diferencia máxima de 10 segundos

                videoControlBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        performVideoViewClick();
                    }
                });

                videoSabeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Left progress : " + videoSliceSeekBar.getLeftProgress() / 1000);
                        Log.d(TAG, "Right progress : " + videoSliceSeekBar.getRightProgress() / 1000);

                        Log.d(TAG, "Total Duration : " + mp.getDuration() / 1000);
                        executeTrimCommand(videoSliceSeekBar.getLeftProgress(), videoSliceSeekBar.getRightProgress());
                    }

                });

            }
        });
        videoView.setVideoPath(path);
    }

    public static String getTimeForTrackFormat(int timeInMills, boolean display2DigitsInMinsSection) {
        int minutes = (timeInMills / (60 * 1000));
        int seconds = (timeInMills - minutes * 60 * 1000) / 1000;
        String result = display2DigitsInMinsSection && minutes < 10 ? "0" : "";
        result += minutes + ":";
        if (seconds < 10) {
            result += "0" + seconds;
        } else {
            result += seconds;
        }
        return result;
    }

    private void performVideoViewClick() {
        if (videoView.isPlaying()) {
            imagePlay.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_play_white));
            videoView.pause();
            videoSliceSeekBar.setSliceBlocked(false);
            videoSliceSeekBar.removeVideoStatusThumb();
        } else {
            imagePlay.setImageBitmap(null);
            videoView.seekTo(videoSliceSeekBar.getLeftProgress());
            videoView.start();
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
            videoSliceSeekBar.videoPlayingProgress(videoView.getCurrentPosition());
            if (videoView.isPlaying() && videoView.getCurrentPosition() < videoSliceSeekBar.getRightProgress()) {
                postDelayed(observerWork, 50);
            } else {

                if (videoView.isPlaying()) videoView.pause();

                videoSliceSeekBar.setSliceBlocked(false);
                videoSliceSeekBar.removeVideoStatusThumb();
            }
        }
    }

    private void executeTrimCommand(int startMs, int endMs) {
        File moviesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES
        );

        String filePrefix = "make_your_song";
        String fileExtn = ".mp4";
        String fileName = filePrefix + fileExtn;

        try {
            InputStream inputStream = getAssets().open(fileName);
            File src = new File(moviesDir, fileName);

            storeFile(inputStream, src);


            File dest = new File(moviesDir, filePrefix + "_1" + fileExtn);
            if (dest.exists()) {
                dest.delete();
            }


            Log.d(TAG, "startTrim: src: " + src.getAbsolutePath());
            Log.d(TAG, "startTrim: dest: " + dest.getAbsolutePath());
            Log.d(TAG, "startTrim: startMs: " + startMs);
            Log.d(TAG, "startTrim: endMs: " + endMs);

            //execFFmpegBinary("-i " + src.getAbsolutePath() + " -ss "+ startMs/1000 + " -to " + endMs/1000 + " -strict -2 -async 1 "+ dest.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void storeFile(InputStream input, File file) {
        try {
            final OutputStream output = new FileOutputStream(file);
            try {
                try {
                    final byte[] buffer = new byte[1024];
                    int read;

                    while ((read = input.read(buffer)) != -1)
                        output.write(buffer, 0, read);

                    output.flush();
                } finally {
                    output.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    class NystromImageCompression extends AsyncTask<String, Void, String> {
        private boolean fromGallery;

        public NystromImageCompression(boolean fromGallery) {
            this.fromGallery = fromGallery;
        }

        @Override
        protected String doInBackground(String... params) {
            String filePath = compressImage(params[0]);
            return filePath;
        }

        public String compressImage(String imageUri) {

            String filePath = getRealPathFromURI(imageUri);
            Bitmap scaledBitmap = null;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;
            float maxHeight = 0, maxWidth = 0;
            if (actualWidth > actualHeight) {
                maxWidth = 1024;
                maxHeight = 720;
            } else if (actualWidth < actualHeight) {
                maxWidth = 720;
                maxHeight = 1024;
            } else if (actualWidth == actualHeight) {
                maxWidth = 800;
                maxHeight = 800;
            }
            float imgRatio = actualWidth / actualHeight;
            float maxRatio = maxWidth / maxHeight;

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;

                }
            }

            options.inSampleSize = utils.calculateInSampleSize(options, actualWidth, actualHeight);
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];

            try {
                bmp = BitmapFactory.decodeFile(filePath, options);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();

            }
            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }

            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;
            float middleX = actualWidth / 2.0f;
            float middleY = actualHeight / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));


            ExifInterface exif;
            try {
                exif = new ExifInterface(filePath);

                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                Log.d("EXIF", "Exif: " + orientation);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                    Log.d("EXIF", "Exif: " + orientation);
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileOutputStream out = null;
            String filename = getFilename();
            try {
                out = new FileOutputStream(filename);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return filename;

        }

        private String getRealPathFromURI(String contentURI) {
            Uri contentUri = Uri.parse(contentURI);
            Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
            if (cursor == null) {
                return contentUri.getPath();
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                return cursor.getString(idx);
            }
        }

        public String getFilename() {
            File file = new File(Environment.getExternalStorageDirectory().getPath(), "Sinarmas Images");
            if (!file.exists()) {
                file.mkdirs();
            }
            String uriSting = (file.getAbsolutePath() + "/" + "bc-" + System.currentTimeMillis() + ".jpg");
            return uriSting;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            uriDecoded = result;
            imageView.setImageBitmap(utils.decodeBitmapFromPath(result));
        }

    }

    public void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void saveImage(Bitmap finalBitmap) {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        System.out.println(root + " Root value in saveImage Function");
        File myDir = new File(root + "/Sinarmas Images");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        iname = "bc-" + n + ".jpg";
        File file = new File(myDir, iname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(ConfirmationSendFile.this, new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
        Image_path = Environment.getExternalStorageDirectory() + "/Pictures/folder_name/" + iname;

        File[] files = myDir.listFiles();
        numberOfImages = files.length;
        System.out.println("Total images in Folder " + numberOfImages);
    }
    /*private void execFFmpegBinary(final String command) {
        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                    Log.d(TAG, "FAILED with output : "+s);
                }

                @Override
                public void onSuccess(String s) {
                    Log.d(TAG, "SUCCESS with output : "+s);
                }

                @Override
                public void onProgress(String s) {
                    Log.d(TAG, "Started command : ffmpeg "+command);
                    Log.d(TAG, "progress : " + s);
                }

                @Override
                public void onStart() {
                    Log.d(TAG, "Started command : ffmpeg " + command);
                   *//* progressDialog.setMessage("Processing...");
                    progressDialog.show();*//*
                }

                @Override
                public void onFinish() {
                    Log.d(TAG, "Finished command : ffmpeg " + command);
                   *//* progressDialog.dismiss();*//*
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
        }
    }*/
}

