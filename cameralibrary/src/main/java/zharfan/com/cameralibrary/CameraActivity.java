package zharfan.com.cameralibrary;

import android.app.Activity;
import android.app.AlertDialog;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.otaliastudios.cameraview.AspectRatio;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Facing;
import com.otaliastudios.cameraview.Flash;
import com.otaliastudios.cameraview.SizeSelector;
import com.otaliastudios.cameraview.SizeSelectors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import zharfan.com.cameralibrary.R;

/*
String for putExtra :
- Lock face camera = LOCK_FACE_CAMERA
(LOCK_SWITCH_CAMERA , UNLOCK_SWITCH_CAMERA)

- Camera when opened = CAMERA_FACE
(CAMERA_REAR , CAMERA_FRONT)

- Flash when opened = FLASH
(FLASH_OFF , FLASH_ON , FLASH_AUTO)

- Quality Jpeg = QUALITY
(LOW , MEDIUM , HIGH)

- Path file = FILE_NAME
*/
public class CameraActivity extends AppCompatActivity implements LifecycleOwner {

    public CameraActivity(){}

    private Activity activity = null;
    private int requestCode = -1;

    private CameraView camera;
    private RelativeLayout cameraTools1;
    private RelativeLayout cameraTools2;
    private RelativeLayout columnPickAsk;
    private ImageButton capturePhoto;
    private SeekBar bar;
    private ImageSwitcher flash;
    private ImageSwitcher switcher;
    private ImageView aspectRatioDialog;
    private ImageView preview;
    private TextView askCancel;
    private TextView askRetry;
    private TextView askSave;
    AlertDialog ratioDialog;
    String size;
    private String path;
    Bitmap picture;
    byte[] pict;

    private int lockFaceCamera;
    private int aspectRatio;
    private int quality;
    private int flashDefault;
    private int cameraFace;

    SizeSelector dimensions;
    SizeSelector ratio;
    SizeSelector result;

    public static final int CAMERA_CODE = 707;
    public static final int CAMERA_REAR = 0;
    public static final int CAMERA_FRONT = 1;
    public static final int FLASH_OFF = 0;
    public static final int FLASH_ON = 1;
    public static final int FLASH_AUTO = 2;
    public static final int UNLOCK_SWITCH_CAMERA = 0;
    public static final int LOCK_SWITCH_CAMERA = 1;
    public static final int LOW = 50;
    public static final int MEDIUM = 70;
    public static final int HIGH = 100;
    public static final int RATIO_4_3 = 0;
    public static final int RATIO_16_9 = 1;

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return null;
    }

    @IntDef({CAMERA_REAR,CAMERA_FRONT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CameraFace{}

    @IntDef({FLASH_OFF,FLASH_ON,FLASH_AUTO})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FlashMode{}

    @IntDef({UNLOCK_SWITCH_CAMERA,LOCK_SWITCH_CAMERA})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SwitchMode{}

    @IntDef({LOW,MEDIUM,HIGH})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Quality{}

    @IntDef({RATIO_4_3,RATIO_16_9})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Ratio{}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        hideStatusBar();
        getSupportActionBar().hide();

        camera = (CameraView) findViewById(R.id.req_camera);
        cameraTools1 = (RelativeLayout) findViewById(R.id.cameraTools1);
        cameraTools2 = (RelativeLayout) findViewById(R.id.cameraTools2);
        columnPickAsk = (RelativeLayout) findViewById(R.id.column_pict_ask);
        capturePhoto = (ImageButton) findViewById(R.id.buttonCapture);
        bar = (SeekBar) findViewById(R.id.seekbar);
        flash = (ImageSwitcher) findViewById(R.id.flash);
        switcher = (ImageSwitcher) findViewById(R.id.switcher);
        aspectRatioDialog = (ImageView) findViewById(R.id.aspectRatio);
        preview = (ImageView) findViewById(R.id.image_preview);
        askCancel = (TextView) findViewById(R.id.ask_cancel);
        askRetry = (TextView) findViewById(R.id.ask_retry);
        askSave = (TextView) findViewById(R.id.ask_save);

        cameraView();
        imageButton();
        seekBar();
        imageSwithcer();
    }

    @Override
    public void onBackPressed() {
        if (preview.getVisibility()==View.VISIBLE){
            preview.setVisibility(View.INVISIBLE);
            columnPickAsk.setVisibility(View.INVISIBLE);
            cameraTools1.setVisibility(View.VISIBLE);
            cameraTools2.setVisibility(View.VISIBLE);
            camera.setVisibility(View.VISIBLE);

        } else {
            super.onBackPressed();
        }

    }

    private void cameraView(){
        int flagnya = ShrdPref.getFlag(this);
        switch (flagnya){
            case 0:
                quality = getIntent().getIntExtra("QUALITY",0);
                aspectRatio = getIntent().getIntExtra("RATIO",0);
                setSizenya();
                ShrdPref.removeFlag(this);
                break;
            case 1:
                quality = HIGH;
                aspectRatio = 0;
                setSizenya();
                ShrdPref.removeFlag(this);
                break;
            case 2:
                quality = HIGH;
                aspectRatio = 1;
                setSizenya();
                ShrdPref.removeFlag(this);
                break;
            case 3:
                quality = MEDIUM;
                aspectRatio = 0;
                setSizenya();
                ShrdPref.removeFlag(this);
                break;
            case 4:
                quality = MEDIUM;
                aspectRatio = 1;
                setSizenya();
                ShrdPref.removeFlag(this);
                break;
            case 5:
                quality = LOW;
                aspectRatio = 0;
                setSizenya();
                ShrdPref.removeFlag(this);
                break;
        }
        camera.setLifecycleOwner(this);
        camera.addCameraListener(new CameraListener() {
            @Override
            public void onCameraOpened(CameraOptions options) {
                super.onCameraOpened(options);
                lockFaceCamera = getIntent().getIntExtra("LOCK_FACE_CAMERA",0);
                cameraFace = getIntent().getIntExtra("CAMERA_FACE",0);
                flashDefault = getIntent().getIntExtra("FLASH",0);
                path = getIntent().getStringExtra("FILE_NAME");
                if (lockFaceCamera != 0){
                    switcher.setVisibility(View.INVISIBLE);
                    camera.setFacing(Facing.FRONT);
                }
                if (cameraFace != 0){
                    camera.setFacing(Facing.FRONT);
                }
                switch (flashDefault){
                    case 0:
                        camera.setFlash(Flash.OFF);
                        break;
                    case 1:
                        camera.setFlash(Flash.ON);
                        flash.setImageResource(R.drawable.outline_flash_on_white_18dp);
                        break;
                    case 2:
                        camera.setFlash(Flash.AUTO);
                        flash.setImageResource(R.drawable.outline_flash_auto_white_18dp);
                        break;
                }
            }

            @Override
            public void onPictureTaken(final byte[] jpeg) {
                super.onPictureTaken(jpeg);
                Log.w("fotonya", "Picture size : "+ camera.getPictureSize().toString() );
                pict = jpeg;
                picture = BitmapFactory.decodeByteArray(jpeg,0,jpeg.length);
                preview.setImageBitmap(picture);
                preview.setVisibility(View.VISIBLE);
                columnPickAsk.setVisibility(View.VISIBLE);
                if (aspectRatio != 0){
                    columnPickAsk.setBackgroundColor(Color.parseColor("#40000000"));
                    askCancel.setTextColor(Color.parseColor("#FFFFFF"));
                    askRetry.setTextColor(Color.parseColor("#FFFFFF"));
                    askSave.setTextColor(Color.parseColor("#FFFFFF"));
                }
                cameraTools1.setVisibility(View.INVISIBLE);
                cameraTools2.setVisibility(View.INVISIBLE);
                camera.setVisibility(View.INVISIBLE);
            }
        });

    }
    private void imageButton(){
        capturePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.capturePicture();
            }
        });
        askCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent putPhoto = new Intent();
                setResult(Activity.RESULT_CANCELED,putPhoto);
                finish();
            }
        });
        askRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        askSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w("fotonya", "Byte : " + picture.toString());
                File pictures = new File(Environment.getExternalStorageDirectory(),"Pictures");
                if (!pictures.exists()){
                    pictures.mkdir();
                }
                File byonchat = new File(Environment.getExternalStorageDirectory(),"Pictures/com.honda.android");
                if (!byonchat.exists()){
                    byonchat.mkdir();
                }
                File photo = new File(Environment.getExternalStorageDirectory() ,"Pictures/com.honda.android/"+path);
                Log.w("fotonya", "Path : "+photo.getAbsolutePath()+" | Quality : "+camera.getJpegQuality() );
                /*if (photo.exists()){
                    photo.delete();
                }*/
                try {
                    FileOutputStream test = new FileOutputStream(photo.getAbsolutePath());
                    test.write(pict);
                    test.close();
                    Intent putPhoto = new Intent();
                    putPhoto.putExtra("PICTURE",photo.getAbsolutePath());
                    setResult(Activity.RESULT_OK,putPhoto);
                    finish();
                } catch (IOException e){
                    e.printStackTrace();
                    Intent putPhoto = new Intent();
                    setResult(Activity.RESULT_CANCELED,putPhoto);
                    finish();
                }
                //byte to file , absolute file path

            }
        });

        aspectRatioDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAspectRatio();
            }
        });

    }
    private void seekBar(){
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float level = i - 3;
                camera.setExposureCorrection(level);
                Log.w("brightnessnya", "brighhtness : "+level );
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }
    private void imageSwithcer(){
        flash.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView flashView = new ImageView(getApplicationContext());
                flashView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                flashView.setLayoutParams(new
                        ImageSwitcher.LayoutParams(60,
                        60));
                return flashView;
            }
        });
        flash.setImageResource(R.drawable.outline_flash_off_white_18dp);
        flash.setOnClickListener(new View.OnClickListener() {
            int fl = 0;
            @Override
            public void onClick(View view) {
                if (fl == 0){
                    flash.setImageResource(R.drawable.outline_flash_on_white_18dp);
                    camera.setFlash(Flash.ON);
                    fl = 1;
                } else if (fl == 1){
                    flash.setImageResource(R.drawable.outline_flash_auto_white_18dp);
                    camera.setFlash(Flash.AUTO);
                    fl = 2;
                } else if (fl == 2){
                    flash.setImageResource(R.drawable.outline_flash_off_white_18dp);
                    camera.setFlash(Flash.OFF);
                    fl = 0;
                }

            }
        });

        /*---------------------------------------------------------------------*/

        switcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView switcherView = new ImageView(getApplicationContext());
                switcherView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                switcherView.setLayoutParams(new
                        ImageSwitcher.LayoutParams(60,
                        60));
                return switcherView;
            }
        });
        switcher.setImageResource(R.drawable.baseline_camera_front_white_18dp);
        switcher.setOnClickListener(new View.OnClickListener() {
            int sw = 0;
            @Override
            public void onClick(View view) {
                if (sw == 0){
                    switcher.setImageResource(R.drawable.baseline_camera_rear_white_18dp);
                    camera.setFacing(Facing.FRONT);
                    sw = 1;
                } else if (sw == 1){
                    switcher.setImageResource(R.drawable.baseline_camera_front_white_18dp);
                    camera.setFacing(Facing.BACK);
                    sw = 0;
                }
            }
        });
    }
    private void showAspectRatio(){
        size = camera.getPictureSize().toString();
        int choice = -1;
        switch (size){
            case "1024x768":
                choice = 0;
                break;
            case "1280x720":
                choice = 1;
                break;
            case "800x600":
                choice = 2;
                break;
            case "640x360":
                if (quality == 70){
                    choice = 3;
                }
                if (quality == 50){
                    choice = 5;
                }
                break;
            case "640x480":
                choice = 4;
        }
        final CharSequence[] ratioList = {"4:3 (High)","16:9 (High)","4:3 (Medium)","16:9 (Medium)","4:3 (Low)"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose picture size");
        builder.setSingleChoiceItems(ratioList, choice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int choice) {
                switch (choice){
                    case 0:
                        ShrdPref.setFlag(getBaseContext(),1);
                        recreate();
                        break;
                    case 1:
                        ShrdPref.setFlag(getBaseContext(),2);
                        recreate();
                        break;
                    case 2:
                        ShrdPref.setFlag(getBaseContext(),3);
                        recreate();
                        break;
                    case 3:
                        ShrdPref.setFlag(getBaseContext(),4);
                        recreate();
                        break;
                    case 4:
                        ShrdPref.setFlag(getBaseContext(),5);
                        recreate();
                        break;
                }
                ratioDialog.dismiss();
                hideStatusBar();
            }
        });
        ratioDialog = builder.create();
        ratioDialog.setCancelable(false);
        ratioDialog.show();
        hideStatusBar();
    }
    private void hideStatusBar(){
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
    private void setSizenya(){
        switch (quality){
            case 50:
                camera.setJpegQuality(LOW);
                if (aspectRatio == 0){
                    dimensions = SizeSelectors.and(SizeSelectors.maxWidth(750),SizeSelectors.maxHeight(750));}
                break;
            case 70:
                camera.setJpegQuality(MEDIUM);
                if (aspectRatio != 0){
                    //16:9
                    dimensions = SizeSelectors.and(SizeSelectors.maxWidth(1250),SizeSelectors.maxHeight(1250));
                } else {
                    //3:4
                    dimensions = SizeSelectors.and(SizeSelectors.maxWidth(1000),SizeSelectors.maxHeight(1000));
                }
                break;
            case 100:
                camera.setJpegQuality(HIGH);
                if (aspectRatio != 0){
                    //16:9
                    dimensions = SizeSelectors.and(SizeSelectors.maxWidth(1500),SizeSelectors.maxHeight(1500));
                } else {
                    //3:4
                    dimensions = SizeSelectors.and(SizeSelectors.maxWidth(1250),SizeSelectors.maxHeight(1250));
                    Log.w("fotonya", "sini: 1");
                }
                break;
        }
        switch (aspectRatio){
            case 0:
                ratio = SizeSelectors.aspectRatio(AspectRatio.of(3,4),0);
                Log.w("fotonya", "sini: 2");
                break;
            case 1:
                ratio = SizeSelectors.aspectRatio(AspectRatio.of(9,16),0);
                break;
        }
        result = SizeSelectors.and(dimensions,ratio,SizeSelectors.biggest());
        camera.setPictureSize(result);
        Log.w("fotonya", "sini: 3");
    }

    private CameraActivity(Activity activity , int requestCode){
        this.activity = activity;
        this.requestCode = requestCode;
    }

    public static class Builder {

        private CameraActivity cameraActivity;

        public Builder(Activity activity,int requestCode){
            cameraActivity = new CameraActivity(activity,requestCode);
        }

        public Builder setCameraFace(@CameraFace int cameraFace){
            cameraActivity.cameraFace = cameraFace;
            return this;
        }
        public Builder setFlashMode(@FlashMode int flashMode){
            cameraActivity.flashDefault = flashMode;
            return this;
        }
        public Builder setLockSwitch(@SwitchMode int lockSwitch){
            cameraActivity.lockFaceCamera = lockSwitch;
            return this;
        }
        public Builder setQuality(@Quality int quality){
            cameraActivity.quality = quality;
            return this;
        }
        public Builder setRatio(@Ratio int ratio){
            cameraActivity.aspectRatio = ratio;
            return this;
        }
        public Builder setFileName(String fileName){
            cameraActivity.path = fileName;
            return this;
        }

        public CameraActivity build() throws IllegalArgumentException{
            if (cameraActivity.requestCode < 0)
                throw new IllegalArgumentException("Wrong request code. Please set the value > 0");
            return cameraActivity;
        }
    }

    public Activity getActivity() {
        return activity;
    }
    public int getRequestCode(){
        return requestCode;
    }
    public int getLockFaceCamera(){
        return lockFaceCamera;
    }
    public int getAspectRatio(){
        return aspectRatio;
    }
    public int getQuality(){
        return quality;
    }
    public int getFlashDefault(){
        return flashDefault;
    }
    public int getCameraFace(){
        return cameraFace;
    }
    public String getFileName(){
        return path;
    }
}
