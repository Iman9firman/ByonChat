/**
 * PhotoSorterActivity.java
 * 
 * (c) Luke Hutchison (luke.hutch@mit.edu)
 * 
 * Released under the Apache License v2.
 */
package com.honda.android.createMeme;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.honda.android.ConfirmationSendFile;
import com.honda.android.ConversationActivity;
import com.honda.android.FragmentDinamicRoom.DinamicRoomTaskActivity;
import com.honda.android.R;
import com.honda.android.NewSelectContactActivity;
import com.honda.android.utils.GPSTracker;
import com.honda.android.utils.ImageFilePath;
import com.honda.android.utils.MediaProcessingUtil;
import com.honda.android.utils.TouchImageView;
import com.honda.android.utils.Validations;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.honda.android.ConfirmationSendFile.CLOSEMEMEACTIVITY;


public class PhotoSortrActivity extends AppCompatActivity implements
        DialogInterface.OnClickListener{

	PhotoSortrView photoSorter;
    TouchImageView imageBackground;
    RelativeLayout canvasSave,relativeLayout;
    Button btnAdd,btnAddImage,btnRemove,btnAddPhoto,btnFilter,btnStyle,btnSize,btnColor,btnOk;
    ImageButton btnRotate1,btnSave;
    EditText insertText;
    FrameLayout frame,frameFilter,frameMain;
    AlertDialog dialogBuble;
    AlertDialog alertDialogFont;
    AlertDialog alertDialogSize;
    AlertDialog alertDialogExit;
    AlertDialog alertDialogSave;
    Dialog alertDialogColor;
    Paint mPaint;
    InputMethodManager imm ;
    HorizontalScrollView horizontalScrollView;
    ImageButton filterBack,filter1,filter2,filter3,filter4,filter5,filter6;
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyyMMdd-HHmmss", Locale.getDefault());
    String uriImage;
    String name;
    String type;
    String title;
    int typeChat;
    Bitmap myBitmap = null;
    private static final String[] photoMenu = new String[] { "Take a photo",
            "Choose from Gallery" };
    private static final int REQ_CAMERA = 1201;
    private static final int REQ_GALLERY = 1202;
    Context context;
    int rotate = 0;
    private Uri picUri;
    private String photo;
    private BroadcastHandler broadcastHandler = new BroadcastHandler();

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.meme_main_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new Validations().getInstance(getApplicationContext()).header(getWindow()));
        Log.w("dihari","saay11");
    //    getSupportActionBar().setIcon(new Validations().getInstance(getApplicationContext()).logoCustome());
        context = getApplicationContext();
        uriImage =  getIntent().getStringExtra("file");
        name =  getIntent().getStringExtra("name");
        type = getIntent().getStringExtra("type");
        title = getIntent().getStringExtra(ConversationActivity.KEY_TITLE);
        typeChat = getIntent().getIntExtra(ConversationActivity.KEY_CONVERSATION_TYPE, 0);

        try {
            JSONObject filud = new JSONObject(uriImage);
            if (filud != null) {
                uriImage = filud.getString("s");
            }
        } catch (Exception e) {

        }

        final File f = new File(uriImage);
        if(f.exists()){
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            myBitmap = MediaProcessingUtil.decodeSampledBitmapFromResourceMemOpt(inputStream, 800,
                    800);
        }

        photo = "meme_" + dateFormat.format(new Date());

        filterBack = (ImageButton) findViewById(R.id.filterBack);
        filter1 = (ImageButton) findViewById(R.id.filter1);
        filter2 = (ImageButton) findViewById(R.id.filter2);
        filter3 = (ImageButton) findViewById(R.id.filter3);
        filter4 = (ImageButton) findViewById(R.id.filter4);
        filter5 = (ImageButton) findViewById(R.id.filter5);
        filter6 = (ImageButton) findViewById(R.id.filter6);


        mPaint = new Paint();

        imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);

        canvasSave  = (RelativeLayout) findViewById(R.id.main_canvas);
        relativeLayout  = (RelativeLayout) findViewById(R.id.canvas_view);
        imageBackground  = (TouchImageView) findViewById(R.id.image_background);
        photoSorter = new PhotoSortrView(this);
        imageBackground.setImageBitmap(myBitmap);
        imageBackground.setMaxZoom(4);
        relativeLayout.addView(photoSorter);

        insertText = (EditText) findViewById(R.id.insertText);

        frame = (FrameLayout) findViewById(R.id.frame);
        frameFilter = (FrameLayout) findViewById(R.id.frameFilter);
        frameMain = (FrameLayout) findViewById(R.id.frameMain);

        btnFilter =(Button)findViewById(R.id.btnFilter);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                frameFilter.setVisibility(View.VISIBLE);
                frameMain.setVisibility(View.GONE);
                setFilterButton(myBitmap);
            }
        });

        btnStyle = (Button)findViewById(R.id.btnStyle);
        btnStyle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(alertDialogFont==null)
                    showPopUpStyle();
                else
                    alertDialogFont.show();

            }
        });

        btnColor = (Button)findViewById(R.id.btnColor);
        btnColor.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(alertDialogColor==null)
                    showDialogPickerColor();
                else
                    alertDialogColor.show();

            }
        });

        btnOk = (Button)findViewById(R.id.btnOke);
        btnOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showCanvas(true);
                ConverterTextToImage convert = new ConverterTextToImage();
                Bitmap text = convert.textAsBitmap(insertText.getText().toString(), insertText.getTextSize(), 5, insertText.getCurrentTextColor(),
                        insertText.getTypeface());
                photoSorter.addImage(getApplicationContext(), text);
                frame.setVisibility(View.GONE);
                imm.hideSoftInputFromWindow(insertText.getWindowToken(), 0);
            }
        });

        btnSize = (Button)findViewById(R.id.btnSize);
        btnSize.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(alertDialogSize==null)
                    showPopUpSize();
                else
                    alertDialogSize.show();
            }
        });
        btnSave = (ImageButton)findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                        canvasSave.setDrawingCacheEnabled(true);
                        canvasSave.buildDrawingCache();
                        Bitmap bmp = canvasSave.getDrawingCache();
                        SaveImage saveImage = new SaveImage();
                        if (saveImage.storeImage(bmp, photo)) {
                            String iconsStoragePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                                    + "/MemeEditor";
                            File sdIconStorageDir = new File(iconsStoragePath);
                            if (!sdIconStorageDir.exists()) {
                                sdIconStorageDir.mkdirs();
                            }
                            String filePath = sdIconStorageDir.toString() + "/" + photo + ".png";
                            canvasSave.setDrawingCacheEnabled(false);

                            if(name!=null&&name.length()>0){
                                Intent intent;
                                intent = new Intent(getApplicationContext(), ConfirmationSendFile.class);
                                intent.putExtra(ConversationActivity.KEY_TITLE, title);
                                intent.putExtra(ConversationActivity.KEY_CONVERSATION_TYPE, typeChat);
                                intent.putExtra("name", name);
                                intent.putExtra("file", filePath);
                                intent.putExtra("type", type);
                                startActivity(intent);
                            }else{
                                Intent intent = new Intent(getApplicationContext(), NewSelectContactActivity.class);
                                intent.putExtra("file",filePath);
                                intent.putExtra("type",type);
                                startActivity(intent);
                            }
                        }
                    }
              /*  });
                // Set a negative/no button and create a listener
                alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });
                alertDialogSave = alertbox.create();
                alertDialogSave.show();

            }*/

        });



        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                insertText.setText("");
                if(frame.getVisibility()== View.GONE){
                    frame.setVisibility(View.VISIBLE);
                    imm.showSoftInput(insertText, 0);
                }else{
                    frame.setVisibility(View.GONE);
                }
            }
        });

        btnAddPhoto =(Button)findViewById(R.id.btnPhoto);
        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showChoosePhotoOptions();
            }
        });

        btnAddImage = (Button) findViewById(R.id.btnAddImage);
        btnAddImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                listBubble();
            }
        });

        btnRemove = (Button) findViewById(R.id.btnRemove);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if( photoSorter.count() != null){
                    photoSorter.deleteImage(getApplicationContext(),Integer.valueOf( photoSorter.count()));
                    if(photoSorter.count()==null){
                        showCanvas(false);
                    }
                }else{
                    showCanvas(false);
                }
                frame.setVisibility(View.GONE);
            }
        });

        btnRotate1 = (ImageButton) findViewById(R.id.btnRotate1);
        btnRotate1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rotate = rotate-90;
                Matrix matrix = new Matrix();
                matrix.postRotate(rotate);
                myBitmap =  Bitmap.createBitmap(myBitmap,0,0,myBitmap.getWidth(),myBitmap.getHeight(),matrix,true);
                imageBackground.setImageBitmap(myBitmap);
            }
        });


        horizontalScrollView = (HorizontalScrollView)findViewById(R.id.filterGallery);

    }

    public static Bitmap createImage(int width, int height, int color) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawRect(0F, 0F, (float) width, (float) height, paint);
        return bitmap;
    }


    public void showCanvas(boolean show){
        if (show){
            if(relativeLayout.getVisibility()==View.GONE){
                photoSorter.setCanvas(this,createImage(800,800,Color.TRANSPARENT));
                relativeLayout.setVisibility(View.VISIBLE);
            }
        }else{
            if(relativeLayout.getVisibility()==View.VISIBLE){
                relativeLayout.setVisibility(View.GONE);
            }
        }

    }
    @Override
	protected void onResume() {
		super.onResume();
	}

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastHandler);
        super.onDestroy();
    }

    @Override
	protected void onPause() {
        IntentFilter filter = new IntentFilter(CLOSEMEMEACTIVITY);
        registerReceiver(broadcastHandler, filter);
		super.onPause();
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id ==  android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
			photoSorter.trackballClicked();
			return true;
		}

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // Create the alert box
            if(frame.getVisibility()== View.GONE){
                AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
                alertbox.setMessage("Exit");

                // Set a positive/yes button and create a listener
                alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                });

                // Set a negative/no button and create a listener
                alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });
                alertDialogExit = alertbox.create();
                alertDialogExit.show();
            }else{
                frame.setVisibility(View.GONE);
            }
            return true;
        }

		return super.onKeyDown(keyCode, event);
	}



     public void setStyleFont(String styleFont){
         Typeface face = Typeface.DEFAULT;
         if(!styleFont.equalsIgnoreCase("SYSTEM_DEFAULT")){
             face = Typeface.createFromAsset(getAssets(),"fonts/"+styleFont.toUpperCase()+".TTF");
         }
         insertText.setTypeface(face);
     }

    public void setStyleSize(int sizeFont){
        insertText.setTextSize(sizeFont);
    }


    public void showPopUpStyle(){
        ObjectItem[] ObjectItemData = new ObjectItem[23];
        ObjectItemData[0] = new ObjectItem(0, "SYSTEM_DEFAULT");
        ObjectItemData[1] = new ObjectItem(1, "ABADDON");
        ObjectItemData[2] = new ObjectItem(2, "ALPHAWOOD");
        ObjectItemData[3] = new ObjectItem(3, "AMELIA");
        ObjectItemData[4] = new ObjectItem(4, "ANGIEPIERCED");
        ObjectItemData[5] = new ObjectItem(5, "APOCALYPSE1");
        ObjectItemData[6] = new ObjectItem(6, "BANGN");
        ObjectItemData[7] = new ObjectItem(7, "BLOODRAC");
        ObjectItemData[8] = new ObjectItem(8, "BRDWAYG");
        ObjectItemData[9] = new ObjectItem(9, "BRLNSR");
        ObjectItemData[10] = new ObjectItem(10, "BROUSS");
        ObjectItemData[11] = new ObjectItem(11, "CAPTUREIT");
        ObjectItemData[12] = new ObjectItem(12, "COWBOYS");
        ObjectItemData[13] = new ObjectItem(13, "DOODLE");
        ObjectItemData[14] = new ObjectItem(14, "FATBS");
        ObjectItemData[15] = new ObjectItem(15, "FLAMING");
        ObjectItemData[16] = new ObjectItem(16, "FLORES");
        ObjectItemData[17] = new ObjectItem(17, "FRADM");
        ObjectItemData[18] = new ObjectItem(18, "GASHUFFERPHAT");
        ObjectItemData[19] = new ObjectItem(19, "IMPACT");
        ObjectItemData[20] = new ObjectItem(20, "MISTRAL");
        ObjectItemData[21] = new ObjectItem(21, "TCCEB");
        ObjectItemData[22] = new ObjectItem(22, "DINENGCHRIFT");
        // our adapter instance
        AdapterTextStyle adapter = new AdapterTextStyle(this, R.layout.meme_list_text_style, ObjectItemData);
        // create a new ListView, set the adapter and item click listener
        ListView listViewItems = new ListView(this);
        listViewItems.setAdapter(adapter);
        listViewItems.setOnItemClickListener(new OnItemClickListenerListViewItem());
        // put the ListView in the pop up
        alertDialogFont = new AlertDialog.Builder(PhotoSortrActivity.this)
                .setView(listViewItems)
                .show();

    }



    public void showPopUpSize(){

        final CharSequence[] items = {" Small "," Medium "," Large "," Extra Large "};
        // Creating and Building the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Size");
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                switch (item) {
                    case 0:
                        setStyleSize(20);
                        break;
                    case 1:
                        setStyleSize(40);
                        break;
                    case 2:
                        setStyleSize(60);
                        break;
                    case 3:
                        setStyleSize(80);
                        break;

                }
                alertDialogSize.dismiss();
            }
        });
        if( insertText.getTextSize()==20){
        }
        alertDialogSize = builder.create();
        alertDialogSize.show();

    }

//
    public void setFilterButton(Bitmap bitmap){

        final Bitmap tumb =  BitmapFactory.decodeResource(getResources(),R.drawable.filter_image);
        final Bitmap set = bitmap;
        //filterBack.setImageDrawable();

        filter1.setImageBitmap(tumb);
        filter2.setImageBitmap(FilteringImage.setColorMatrixBW(tumb));
        filter3.setImageBitmap(FilteringImage.setColorMatrixGrayScale(tumb));
        filter4.setImageBitmap(FilteringImage.setColorMatrixSepia(tumb));
        filter5.setImageBitmap(FilteringImage.setPopArtGradientFromBitmap3(tumb));
        filter6.setImageBitmap(FilteringImage.setColorMatrixInvert(tumb));



        filterBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               frameFilter.setVisibility(View.GONE);
               frameMain.setVisibility(View.VISIBLE);
            }
        });
        filter1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                imageBackground.setImageBitmap(set);
            }
        });
        filter2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                imageBackground.setImageBitmap(FilteringImage.setColorMatrixBW(set));
            }
        });
        filter3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                imageBackground.setImageBitmap(FilteringImage.setColorMatrixGrayScale(set));
            }
        });
        filter4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                imageBackground.setImageBitmap(FilteringImage.setColorMatrixSepia(set));
            }
        });
        filter5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                imageBackground.setImageBitmap(FilteringImage.setPopArtGradientFromBitmap3(set));
            }
        });
        filter6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                imageBackground.setImageBitmap(FilteringImage.setColorMatrixInvert(set));
            }
        });
    }

    private void showDialogPickerColor() {

        Dialog dialog = new Dialog(this);
        dialog.setTitle("Select Color");
        dialog.setContentView(R.layout.meme_dialog_color);
        alertDialogColor = dialog;

        final ColorPicker colorPicker = (ColorPicker) dialog.findViewById(R.id.colorPicker);

        Button dialogButtonOk = (Button) dialog.findViewById(R.id.dialogBtnOk);
        // if button is clicked, close the custom dialog
        dialogButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertText.setTextColor(colorPicker.getColor());
                alertDialogColor.dismiss();
            }
        });

        Button dialogButtonCancel = (Button) dialog.findViewById(R.id.dialogBtnCancel);
        // if button is clicked, close the custom dialog
        dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogColor.dismiss();
            }
        });

        alertDialogColor.show();

    }


    public void listBubble(){
        Bitmap myBitMap = ((BitmapDrawable)getResources().getDrawable(R.drawable.bubble01)).getBitmap();
        Bitmap myBitMap1 = ((BitmapDrawable)getResources().getDrawable(R.drawable.bubble02)).getBitmap();
        Bitmap myBitMap2 = ((BitmapDrawable)getResources().getDrawable(R.drawable.bubble03)).getBitmap();
        Bitmap myBitMap3 = ((BitmapDrawable)getResources().getDrawable(R.drawable.bubble04)).getBitmap();
        Bitmap myBitMap4 = ((BitmapDrawable)getResources().getDrawable(R.drawable.bubble05)).getBitmap();
        Bitmap myBitMap5 = ((BitmapDrawable)getResources().getDrawable(R.drawable.bubble06)).getBitmap();
        Bitmap myBitMap6 = ((BitmapDrawable)getResources().getDrawable(R.drawable.bubble07)).getBitmap();
        Bitmap myBitMap7 = ((BitmapDrawable)getResources().getDrawable(R.drawable.bubble08)).getBitmap();
        Bitmap myBitMap8 = ((BitmapDrawable)getResources().getDrawable(R.drawable.bubble09)).getBitmap();
        Bitmap myBitMap9 = ((BitmapDrawable)getResources().getDrawable(R.drawable.bubble10)).getBitmap();
        ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();
        bitmapArray.add(myBitMap); // Add a bitmap
        bitmapArray.add(FilteringImage.flip(myBitMap)); // Add a bitmap
        bitmapArray.add(myBitMap1); // Add a bitmap
        bitmapArray.add(FilteringImage.flip(myBitMap1)); // Add a bitmap
        bitmapArray.add(myBitMap2); // Add a bitmap
        bitmapArray.add(FilteringImage.flip(myBitMap2)); // Add a bitmap
        bitmapArray.add(myBitMap3); // Add a bitmap
        bitmapArray.add(FilteringImage.flip(myBitMap3)); // Add a bitmap
        bitmapArray.add(myBitMap4); // Add a bitmap
        bitmapArray.add(FilteringImage.flip(myBitMap4)); // Add a bitmap
        bitmapArray.add(myBitMap5); // Add a bitmap
        bitmapArray.add(myBitMap6); // Add a bitmap
        bitmapArray.add(myBitMap7); // Add a bitmap
        bitmapArray.add(myBitMap8); // Add a bitmap
        bitmapArray.add(myBitMap9); // Add a bitmap
        bitmapArray.get(0);

        AdapterBubble adapter = new AdapterBubble(this, R.layout.meme_list_bubble,bitmapArray );
        // create a new ListView, set the adapter and item click listener
        final GridView listViewItems = new GridView(this);
        listViewItems.setNumColumns(2);

        listViewItems.setAdapter(adapter);
        listViewItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                dialogBuble.dismiss();
                showCanvas(true);
                ConverterTextToImage converterTextToImage = new ConverterTextToImage();
                photoSorter.addImage(getApplicationContext(),  converterTextToImage.addBorder((Bitmap) listViewItems.getItemAtPosition(position),3,Color.TRANSPARENT)) ;
            }
        });

        dialogBuble = new AlertDialog.Builder(PhotoSortrActivity.this)
                .setView(listViewItems)
                .show();
    }

    private void showChoosePhotoOptions() {

        AlertDialog.Builder builder = new AlertDialog.Builder(PhotoSortrActivity.this);
        builder.setTitle("Select photo");
        builder.setItems(photoMenu, this);
        builder.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Intent i = new Intent();
        int actId = 0;
        switch (which) {
            case 0:
                actId = REQ_CAMERA;
                i.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                break;

            case 1:
                actId = REQ_GALLERY;
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                MediaProcessingUtil.addCropExtraFree(i);
                break;
        }


        i.putExtra("return-data", true);
        startActivityForResult(i, actId);
        dialog.dismiss();



    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_CAMERA) {
                picUri = data.getData();
                performCrop();
            }else if (requestCode == REQ_GALLERY) {
                showCanvas(true);
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);// (0 - 100)????
                    ConverterTextToImage converterTextToImage = new ConverterTextToImage();
                    photoSorter.addImage(getApplicationContext(),  converterTextToImage.addBorder(photo,3,Color.TRANSPARENT)) ;
                }else {
                    Uri selectedUri = data.getData();
                    String selectedImagePath = ImageFilePath.getPath(getApplicationContext(), selectedUri);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath, options);
                    ConverterTextToImage converterTextToImage = new ConverterTextToImage();
                    photoSorter.addImage(getApplicationContext(),  converterTextToImage.addBorder(bitmap,3,Color.TRANSPARENT)) ;
                }
            }
        }
    }


    private void performCrop() {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            MediaProcessingUtil.addCropExtraFree(cropIntent);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, REQ_GALLERY);
        }
        catch (ActivityNotFoundException anfe) {
            Toast toast = Toast
                    .makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    class BroadcastHandler extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches(CLOSEMEMEACTIVITY)) {
               finish();
            }

        }

    }
  }