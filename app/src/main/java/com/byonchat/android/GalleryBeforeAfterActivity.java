package com.byonchat.android;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.byonchat.android.Manhera.Manhera;
import com.byonchat.android.adapter.ExpandableListAdapter;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.personalRoom.model.NotesPhoto;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.utils.AndroidMultiPartEntity;
import com.byonchat.android.utils.MediaProcessingUtil;
import com.byonchat.android.utils.UtilsPD;
import com.byonchat.android.utils.ValidationsKey;
import com.byonchat.android.volley.VolleyMultipartRequest;
import com.byonchat.android.volley.VolleySinglepartRequest;
import com.byonchat.android.volley.VolleySingleton;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.memfis19.annca.Annca;
import io.github.memfis19.annca.internal.configuration.AnncaConfiguration;

public class GalleryBeforeAfterActivity extends Constants implements EmojiconGridFragment.OnEmojiconClickedListener,
        EmojiconsFragment.OnEmojiconBackspaceClickedListener {
    Toolbar toolbar;
    TextView vTitle;
    ImageView vImageBefore, vImageAfter;
    EmojiconEditText vWriteComment;
    Button vButtonSend;
    ImageButton btn_add_emoticon;
    private LinearLayout emojicons;

    String color;
    String cameraFileOutput;
    private ProgressDialog progressDialog;
    List<NotesPhoto> photos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_before_after);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.BLACK);
        }

        toolbar = (Toolbar) findViewById(R.id.abMain);
        vTitle = (TextView) findViewById(R.id.title_toolbar);
        vImageBefore = (ImageView) findViewById(R.id.image_before);
        vImageAfter = (ImageView) findViewById(R.id.image_after);
        vWriteComment = (EmojiconEditText) findViewById(R.id.writeComment);
        emojicons = (LinearLayout) findViewById(R.id.emojiconsLayout);
        btn_add_emoticon = (ImageButton) findViewById(R.id.btn_add_emoticon);
        vButtonSend = (Button) findViewById(R.id.btnSend);
        toolbar.setTitleTextColor(Color.WHITE);
        vTitle.setText("Follow Up");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        color = getIntent().getStringExtra("color");
        if (color != null) {
            toolbar.setBackgroundColor(Color.parseColor("#" + color));
        }

        refreshItem();

        vImageBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!getIntent().getStringExtra("url_before").equalsIgnoreCase("")) {
                    Intent intent = new Intent(getApplicationContext(), ZoomImageViewActivity.class);
                    intent.putExtra(ZoomImageViewActivity.KEY_FILE, getIntent().getStringExtra("url_before"));
                    startActivity(intent);
                }
            }
        });

        vImageAfter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeImage();
            }
        });

        vButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateComment()) {
                    progressDialog = UtilsPD.createProgressDialog(GalleryBeforeAfterActivity.this);
                    progressDialog.show();
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

                    postParam();
                }
            }
        });

        btn_add_emoticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emojicons.getVisibility() == View.GONE) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                    Animation animFade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_slide_in_bottom);
                    emojicons.setVisibility(View.VISIBLE);
                    emojicons.startAnimation(animFade);

                    vWriteComment.setFocusable(false);
                } else {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    vWriteComment.setFocusableInTouchMode(true);
                    vWriteComment.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(vWriteComment, InputMethodManager.SHOW_IMPLICIT);
                    emojicons.setVisibility(View.GONE);
                }
            }
        });


        vWriteComment.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                vWriteComment.setFocusableInTouchMode(true);
                vWriteComment.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(vWriteComment, InputMethodManager.SHOW_IMPLICIT);
                if (emojicons.getVisibility() == View.VISIBLE) {
                    emojicons.setVisibility(View.GONE);
                }

                return false;
            }
        });
    }

    protected void takeImage() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        AnncaConfiguration.Builder photo = new AnncaConfiguration.Builder(this, REQ_CAMERA);
        photo.setMediaAction(AnncaConfiguration.MEDIA_ACTION_PHOTO);
        photo.setMediaQuality(AnncaConfiguration.MEDIA_QUALITY_MEDIUM);
        photo.setCameraFace(AnncaConfiguration.CAMERA_FACE_REAR);
        photo.setMediaResultBehaviour(AnncaConfiguration.PREVIEW);
        new Annca(photo.build()).launchCamera();
    }

    private boolean validateComment() {
        if (TextUtils.isEmpty(vWriteComment.getText()) || TextUtils.isEmpty(cameraFileOutput)) {
            vButtonSend.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_error));
            return false;
        }
        return true;
    }

    void refreshItem() {
        if (getIntent().getExtras().containsKey("id_comment")) {
            String userid = getIntent().getStringExtra("userid");
            String id_note = getIntent().getStringExtra("id_note");
            String id_comment = getIntent().getStringExtra("id_comment");
            String id_task = getIntent().getStringExtra("id_task");
            String bc_user = getIntent().getStringExtra("bc_user");
            String idRoomTab = "";
            if (getIntent().getExtras().containsKey("id_room_tab")) {
                idRoomTab = getIntent().getStringExtra("id_room_tab");
            }
        } else {
            String userid = getIntent().getStringExtra("userid");
            String id_note = getIntent().getStringExtra("id_note");
            String bc_user = getIntent().getStringExtra("bc_user");
            String id_task = getIntent().getStringExtra("id_task");
            String idRoomTab = "";
            if (getIntent().getExtras().containsKey("id_room_tab")) {
                idRoomTab = getIntent().getStringExtra("id_room_tab");
            }
        }

        Manhera.getInstance().get().load(getIntent().getStringExtra("url_before"))
                .error(R.drawable.no_image)
                .placeholder(R.drawable.no_image)
                .dontAnimate()
                .into(vImageBefore);
    }

    void postParam() {
        if (getIntent().getExtras().containsKey("id_comment")) {
            String userid = getIntent().getStringExtra("userid");
            String id_note = getIntent().getStringExtra("id_note");
            String id_comment = getIntent().getStringExtra("id_comment");
            String id_task = getIntent().getStringExtra("id_task");
            String bc_user = getIntent().getStringExtra("bc_user");
            String idRoomTab = "";
            if (getIntent().getExtras().containsKey("id_room_tab")) {
                idRoomTab = getIntent().getStringExtra("id_room_tab");
            }
            new UploadFile(userid, vWriteComment.getText().toString(), id_note, bc_user, idRoomTab, id_comment, getIntent().getStringExtra("url_before"), photos).execute();

        } else {
            String userid = getIntent().getStringExtra("userid");
            String id_note = getIntent().getStringExtra("id_note");
            String bc_user = getIntent().getStringExtra("bc_user");
            String id_task = getIntent().getStringExtra("id_task");
            String idRoomTab = "";
            if (getIntent().getExtras().containsKey("id_room_tab")) {
                idRoomTab = getIntent().getStringExtra("id_room_tab");
            }

            new UploadFile(userid, vWriteComment.getText().toString(), id_note, bc_user, idRoomTab, "", getIntent().getStringExtra("url_before"), photos).execute();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CAMERA && resultCode == Activity.RESULT_OK) {
            cameraFileOutput = data.getStringExtra(AnncaConfiguration.Arguments.FILE_PATH);
            if (decodeFile(cameraFileOutput)) {
                final File f = new File(cameraFileOutput);
                photos.add(new NotesPhoto(f));
                if (f.exists()) {
                    Manhera.getInstance().get().load(f)
                            .error(R.drawable.no_image)
                            .dontAnimate()
                            .into(vImageAfter);
                } else {
                    Toast.makeText(this, R.string.corrupted_file, Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(this, R.string.comment_error_failed_read_picture, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(vWriteComment);
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(vWriteComment, emojicon);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class UploadFile extends AsyncTask<Void, Integer, String> {
        long totalSize = 0;
        String userid, textComment, id_note, bc_user, idRoomTab, parent_id, photo_before;
        private List<NotesPhoto> photo_after;

        private UploadFile(final String userid, final String textComment, final String id_note,
                           final String bc_user, final String idRoomTab, final String parent_id,
                           final String photo_before, List<NotesPhoto> photo_after) {
            this.userid = userid;
            this.textComment = textComment;
            this.id_note = id_note;
            this.bc_user = bc_user;
            this.idRoomTab = idRoomTab;
            this.parent_id = parent_id;
            this.photo_before = photo_before;
            this.photo_after = photo_after;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(URL_POST);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                Log.w("kitalog", id_note + " -- " + bc_user + " -- " + textComment + " --" + photo_before);
                ContentType contentType = ContentType.create("image/jpeg");
                entity.addPart("attachment_id", new StringBody(id_note));
                entity.addPart("bc_user", new StringBody(bc_user));
                entity.addPart("content", new StringBody(textComment));
                entity.addPart("photo_before", new StringBody(photo_before));
                if (!parent_id.equalsIgnoreCase(""))
                    entity.addPart("parent_id", new StringBody(parent_id));
                if (photos != null) {
                    for (NotesPhoto photo : photos) {
                        File file = new File(resizeAndCompressImageBeforeSend(getApplicationContext(), photo.getPhotoFile().getPath(), "fileUploadBC_" + new Date().getTime() + ".jpg"));
                        if (!file.exists()) {
                            return null;
                        }

                        int size = (int) file.length();

                        byte[] bytes = new byte[size];
                        try {
                            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                            buf.read(bytes, 0, bytes.length);
                            buf.close();

                            entity.addPart("photo_after", new FileBody(file, contentType, photo.getPhotoFile().getName()));
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                } else {
                    showError(getString(R.string.comment_error_failed_read_picture));
                }

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
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
            progressDialog.dismiss();
            if (result.equalsIgnoreCase("1")) {
                Intent data = new Intent();
                if (getIntent().getExtras().containsKey("id_comment")) {
                    data.putExtra("userid", getIntent().getStringExtra("userid"));
                    data.putExtra("id_note", getIntent().getStringExtra("id_note"));
                    data.putExtra("id_comment", getIntent().getStringExtra("id_comment"));
                    data.putExtra("id_task", getIntent().getStringExtra("id_task"));
                    data.putExtra("bc_user", getIntent().getStringExtra("bc_user"));
                    String idRoomTab = "";
                    if (getIntent().getExtras().containsKey("id_room_tab")) {
                        idRoomTab = getIntent().getStringExtra("id_room_tab");
                        data.putExtra("id_room_tab", getIntent().getExtras().containsKey("id_room_tab"));
                    }
                } else {
                    data.putExtra("userid", getIntent().getStringExtra("userid"));
                    data.putExtra("id_note", getIntent().getStringExtra("id_note"));
                    data.putExtra("id_task", getIntent().getStringExtra("id_task"));
                    data.putExtra("bc_user", getIntent().getStringExtra("bc_user"));
                    String idRoomTab = "";
                    if (getIntent().getExtras().containsKey("id_room_tab")) {
                        idRoomTab = getIntent().getStringExtra("id_room_tab");
                        data.putExtra("id_room_tab", getIntent().getExtras().containsKey("id_room_tab"));
                    }
                }

                setResult(RESULT_OK, data);
                finish();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
//            vo.setProgress((int) values[0]);
//            updateList(vo);
        }

    }

    public void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    public boolean decodeFile(String path) {
        int orientation;
        try {
            if (path == null) {
                return false;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            Bitmap bm = BitmapFactory.decodeFile(path, o2);
            ExifInterface exif = new ExifInterface(path);
            orientation = exif
                    .getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Matrix m = new Matrix();
            if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
                m.postRotate(180);
                bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);
                final File f = new File(path);
                if (f.exists()) f.delete();
                try {
                    FileOutputStream out = new FileOutputStream(f);
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                m.postRotate(90);
                bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);
                final File f = new File(path);
                if (f.exists()) f.delete();
                try {
                    FileOutputStream out = new FileOutputStream(f);
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                m.postRotate(270);
                bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);
                final File f = new File(path);
                if (f.exists()) f.delete();
                try {
                    FileOutputStream out = new FileOutputStream(f);
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
            final File f = new File(path);
            if (f.exists()) f.delete();
            try {
                FileOutputStream out = new FileOutputStream(f);
                bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;

        } catch (Exception e) {
            return false;
        }

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
}
