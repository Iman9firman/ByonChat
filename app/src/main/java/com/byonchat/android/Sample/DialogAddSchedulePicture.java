package com.byonchat.android.Sample;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.byonchat.android.FullScreenUpdateProfileActivity;
import com.byonchat.android.R;
import com.byonchat.android.Sample.Database.ScheduleSLA;
import com.byonchat.android.Sample.Database.ScheduleSLADB;
import com.byonchat.android.local.Byonchat;
import com.byonchat.android.utils.MediaProcessingUtil;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import zharfan.com.cameralibrary.Camera;
import zharfan.com.cameralibrary.CameraActivity;

import static com.byonchat.android.Sample.DetailAreaScheduleSLA.link_pic;

public class DialogAddSchedulePicture extends Activity implements View.OnClickListener {
    Activity activity;
    Button butCancel, butSubmit;
    ImageView imageCover;
    Bitmap result;
    private static final int REQ_CAMERA = 1201;
    ScheduleSLADB databasse;
    String id, id_jjt, post;
    String path_this;

    boolean uploaded = false;
    boolean exist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_upload_image);

        databasse = ScheduleSLADB.getInstance(getApplicationContext());
        activity = DialogAddSchedulePicture.this;
        initIntent();
        initViews();
    }

    private void initIntent(){
        id = getIntent().getStringExtra("id_da");
        id_jjt = getIntent().getStringExtra("id_jjt");
        post = getIntent().getStringExtra("post");
    }

    private void initViews() {
        butCancel = (Button) findViewById(R.id.btn_cancel);
        butSubmit = (Button) findViewById(R.id.btn_proceed);
        imageCover = (ImageView) findViewById(R.id.cover_image);

        Log.w("version","fcewvwebv Start");
        Cursor cursorBot = databasse.getDataPicByID(id);
        imageCover.setImageDrawable(getResources().getDrawable(R.drawable.ic_att_photo));
        if(cursorBot.getCount() > 0){
            String img_start = cursorBot.getString(cursorBot.getColumnIndex(ScheduleSLADB.SCH_DATA_START_PIC));
            String img_proses = cursorBot.getString(cursorBot.getColumnIndex(ScheduleSLADB.SCH_DATA_PROSES_PIC));
            String img_done = cursorBot.getString(cursorBot.getColumnIndex(ScheduleSLADB.SCH_DATA_DONE_PIC));
            if(post.equalsIgnoreCase("start")){
                if(!img_start.equalsIgnoreCase("null")) {
                    path_this = img_start;
                    if (img_start.startsWith("/storage")) {
                        Log.w("Mistakeer", "1. "+img_start);
                        Picasso.with(DialogAddSchedulePicture.this).load(new File(img_start))
                                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                                .into(imageCover);
                    } else {
                        Log.w("Mistakeer", "2. "+img_start);
                        Picasso.with(DialogAddSchedulePicture.this).load(link_pic + img_start)
                                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                                .into(imageCover);
                        uploaded = true;
                    }
                }
            } else if(post.equalsIgnoreCase("proses")){
                if (!img_proses.equalsIgnoreCase("null")) {
                    path_this = img_proses;
                    if (img_proses.startsWith("/storage")) {
                        Log.w("Mistakeer", "3. "+img_proses);
                        Picasso.with(DialogAddSchedulePicture.this).load(new File(img_proses))
                                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                                .into(imageCover);
                    } else {
                        Log.w("Mistakeer", "4. "+img_proses);
                        Picasso.with(DialogAddSchedulePicture.this).load(link_pic + img_proses)
                                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                                .into(imageCover);
                        uploaded = true;
                    }
                }
            } else {
                if(!img_done.equalsIgnoreCase("null")) {
                    path_this = img_done;
                    if (img_done.startsWith("/storage")) {
                        Log.w("Mistakeer", "5. "+img_done);
                        Picasso.with(DialogAddSchedulePicture.this).load(new File(img_done))
                                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                                .into(imageCover);
                    } else {
                        Log.w("Mistakeer", "6. "+img_done);
                        Picasso.with(DialogAddSchedulePicture.this).load(link_pic + img_done)
                                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                                .into(imageCover);
                        uploaded = true;
                    }
                }
            }
        }

        butCancel.setOnClickListener(this);
        butSubmit.setOnClickListener(this);
        imageCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("version","Start Image");
                if(uploaded) {
                    Intent intent = new Intent(getApplicationContext(), FullScreenUpdateProfileActivity.class);
                    intent.putExtra(FullScreenUpdateProfileActivity.JAB_ID, Byonchat.getMessengerHelper().getMyContact().getJabberId());
                    intent.putExtra(FullScreenUpdateProfileActivity.PATH, link_pic + path_this);
                    startActivity(intent);
                } else {
                    if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    CameraActivity.Builder start = new CameraActivity.Builder(activity, REQ_CAMERA);
                    start.setPackageName(getPackageName());
                    start.setLockSwitch(CameraActivity.UNLOCK_SWITCH_CAMERA);
                    start.setCameraFace(CameraActivity.CAMERA_REAR);
                    start.setFlashMode(CameraActivity.FLASH_OFF);
                    start.setQuality(CameraActivity.MEDIUM);
                    start.setRatio(CameraActivity.RATIO_4_3);
                    start.setFileName(new MediaProcessingUtil().createFileName("jpeg", "ROOM"));
                    new Camera(start.build()).lauchCamera();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CAMERA) {
            if (resultCode == RESULT_OK) {
                String returnString = data.getStringExtra("PICTURE");
                if (decodeFile(returnString)) {
                    final File f = new File(returnString);
                    if (f.exists()) {
                        FileInputStream inputStream = null;
                        try {
                            inputStream = new FileInputStream(f);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        path_this = returnString;
                        result = MediaProcessingUtil.decodeSampledBitmapFromResourceMemOpt(inputStream, 800,
                                800);

                        imageCover.setImageBitmap(result);
                        String myBase64Image = encodeToBase64(result, Bitmap.CompressFormat.JPEG, 80);

                        ScheduleSLA sch;
                        if (post.equalsIgnoreCase("start")) {
                            sch = new ScheduleSLA(id, id_jjt, Byonchat.getMessengerHelper().getMyContact().getJabberId(), returnString, "null", "null");
                        } else if (post.equalsIgnoreCase("proses")) {
                            sch = new ScheduleSLA(id, id_jjt, Byonchat.getMessengerHelper().getMyContact().getJabberId(), "null", returnString, "null");
                        } else {
                            sch = new ScheduleSLA(id, id_jjt, Byonchat.getMessengerHelper().getMyContact().getJabberId(), "null", "null", returnString);
                        }
                        Cursor cursorBot = databasse.getDataPicByID(id);
                        if(cursorBot.getCount() > 0){
                            databasse.updateImgNow(sch, post);
                        }else {
                            databasse.insertDataSchedule(sch);
                        }
                        exist = true;
                    }

                } else {
                    Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
                }

            } else if (resultCode == RESULT_CANCELED) {
                if (result == null) {
                    //     btnPhoto.setVisibility(View.VISIBLE);
                }

                Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
            } else {
                if (result == null) {
                    // btnPhoto.setVisibility(View.VISIBLE);
                }

                Toast.makeText(this, " Take a picture has canceled ", Toast.LENGTH_SHORT).show();
            }
        }
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

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat,
                                        int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static String encodeToBase64File(File originalFile) {
        String encodedBase64 = null;
        try {
            FileInputStream fileInputStreamReader = new FileInputStream(originalFile);
            byte[] bytes = new byte[(int) originalFile.length()];
            fileInputStreamReader.read(bytes);
            encodedBase64 = new String(Base64.encode(bytes, Base64.DEFAULT));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encodedBase64;
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    @Override
    public void onClick(View v) {
        if (v == butCancel) {
            finish();
            if(exist) {
                databasse.deleteByIdDAandJJT(id, id_jjt);
            }
        }
        if (v == butSubmit) {
            finish();
        }
    }

}
