package com.byonchat.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.collection.LruCache;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.byonchat.android.adapter.HorizontalAdapter;
import com.byonchat.android.model.Image;
import com.byonchat.android.model.ImageCompressed;
import com.byonchat.android.model.ModelPicture;
import com.byonchat.android.personalRoom.RecyclerItemClickListener;
import com.byonchat.android.personalRoom.model.NotesPhoto;
import com.byonchat.android.personalRoom.model.PictureModel;
import com.byonchat.android.personalRoom.transformer.DepthPageTransformer;
import com.byonchat.android.provider.FilesURL;
import com.byonchat.android.provider.FilesURLDatabaseHelper;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.utils.ImageLoadingUtils;
import com.byonchat.android.utils.MediaProcessingUtil;
import com.byonchat.android.utils.TouchImageView;
import com.byonchat.android.utils.UploadService;
import com.byonchat.android.utils.UtilsPD;
import com.byonchat.android.utils.Validations;
import com.byonchat.android.widget.VideoSlaceSeekBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import static com.byonchat.android.utils.Utility.reportCatch;

/**
 * Created by byonc on 4/20/2017.
 */

public class ConfirmationSendFileMultiple extends AppCompatActivity implements PictureFragment.OnFragmentInteractionListener {
    TouchImageView bigImageView;
    ImageView buttonAddImage;
    Button btnCancel;
    Button btnSend;
    RecyclerView recyclerView;
    ViewPager mViewPager;

    String uriImage, uriDecoded = "";
    String destination;
    String type;
    String title;
    String from;
    int typeChat;

    public ArrayList<PictureModel> pictureModel;
    public ArrayList<ModelPicture> modelPictures;
    public ArrayList<NotesPhoto> notesPhotos;
    private HorizontalAdapter horizontalAdapter;
    private DetailPictureAdapter adapter;
    private String pos;
    private String text;
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
    private long numberOfImages = 0;
    private String Image_path = null;
    public static final String CLOSEMEMEACTIVITY = "byonchat.meme.close.activity";
    private static final String TAG = ConfirmationSendFileMultiple.class.getSimpleName();
    private String pmyuserid, puserid, purl, purlthumb, ptitle, ptimestamp, pdesc, pid, pflag, pColor;
    public static int IMGS[] = {R.drawable.btn_plus};
    private int REQUEST_CODE_PICKER = 2000;
    private ArrayList<Image> images = new ArrayList<>();
    private ArrayList<ImageCompressed> imageCompressed = new ArrayList<>();
    boolean isFrom = false;
    public static final String EXTRA_PHOTOS = "photos";
    public static final String EXTRA_CAPTIONS = "captions";
    public final static HashMap<String, String> message = new HashMap<>();
    public static final String KEY_CONTENT = "key_content";

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmation_send_file_multiple);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new Validations().getInstance(getApplicationContext()).header(getWindow()));

        try {
            if (getIntent().getExtras().containsKey("isFrom")) {
                isFrom = true;
            }
            uriImage = getIntent().getStringExtra("file");
            destination = getIntent().getStringExtra("name");
            type = getIntent().getStringExtra("type");
            title = getIntent().getStringExtra(ConversationActivity.KEY_TITLE);
            typeChat = getIntent().getIntExtra(ConversationActivity.KEY_CONVERSATION_TYPE, 0);
            images = getIntent().getParcelableArrayListExtra("selected");

            mViewPager = (ViewPager) findViewById(R.id.viewpager);
            recyclerView = (RecyclerView) findViewById(R.id.horizontal_recyclerView);
            textMessage = (EditText) findViewById(R.id.textMessage);
            buttonAddImage = (ImageView) findViewById(R.id.buttonAddImage);
            bigImageView = (TouchImageView) findViewById(R.id.imageView);
            btnCancel = (Button) findViewById(R.id.btnCancel);
            btnSend = (Button) findViewById(R.id.btnSend);

            if (isFrom) {
                textMessage.setVisibility(View.VISIBLE);
            }
            recyclerView.setHasFixedSize(true);
            recyclerView
                    .setLayoutManager(new LinearLayoutManager(ConfirmationSendFileMultiple.this, LinearLayoutManager.HORIZONTAL, false));

            buttonAddImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    start(destination);
                }
            });

            try {
                pictureModel = new ArrayList<>();
                modelPictures = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(uriImage);
                for (int i = 0; i < jsonArray.length(); i++) {
                    PictureModel data = new PictureModel();
                    ModelPicture model = new ModelPicture();
                    String uri = jsonArray.getString(i);
                    data.setUrl(uri);
                    data.setId_photo(i + "");
                    model.setUrl(uri);
                    model.setId_photo(i + "");
                    if (i == 0) {
                        data.setSelected(true);
                        model.setSelected(true);
                    } else {
                        data.setSelected(false);
                        model.setSelected(false);
                    }
                    pictureModel.add(data);
                    modelPictures.add(model);
                }
                horizontalAdapter = new HorizontalAdapter(getApplicationContext(), pictureModel);
                recyclerView.setAdapter(horizontalAdapter);
                horizontalAdapter.notifyDataSetChanged();
            } catch (Exception e) {
            }


            mViewPager.setPageTransformer(true, new DepthPageTransformer());

            adapter = new DetailPictureAdapter(getSupportFragmentManager(), modelPictures, isFrom);
            mViewPager.setAdapter(adapter);
            mViewPager.setPageTransformer(true, new DepthPageTransformer());

            ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.UP) {

                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                /*Collections.swap(pictureModel, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                // and notify the adapter that its dataset has changed
                horizontalAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());*/

                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    final int position = viewHolder.getAdapterPosition(); //get position which is swipe

                    if (direction == ItemTouchHelper.UP) {
                        images.remove(position);
                        if (position == mViewPager.getCurrentItem()) {
                            horizontalAdapter.deletePage(position);
                            adapter.deletePage(position);
                        } else {
                            horizontalAdapter.deletePage(position);
                            adapter.deletePage(position);
                        }
                    }
                }

                @Override
                public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                    int position = viewHolder.getAdapterPosition();

                /*if (viewHolder.getAdapterPosition() == 0) {
                    if (viewHolder instanceof HorizontalAdapter.MyViewHolder) return 0;
                }*/

                    if (pictureModel.size() == 1) {
                        if (viewHolder instanceof HorizontalAdapter.MyViewHolder) return 0;
                    }

                    return super.getSwipeDirs(recyclerView, viewHolder);

//                return position == 0 ? 0 : super.getSwipeDirs(recyclerView, viewHolder);
                }

            };

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);

            mViewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
                @Override
                public void transformPage(View page, float position) {
                    // do transformation here
                    final float normalizedposition = Math.abs(Math.abs(position) - 1);
                    page.setAlpha(normalizedposition);
                }
            });

            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                private int index = 0;

                @Override
                public void onPageSelected(int position) {
                    index = position;

                }

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                int width = mViewPager.getWidth();
//                recyclerView.scrollTo((int) (width * position + width * positionOffset), 0);
                    horizontalAdapter.setSelected(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (state == ViewPager.SCROLL_STATE_IDLE) {
                        mViewPager.setCurrentItem(index);
                    }

                }
            });


            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,
                    new RecyclerItemClickListener.OnItemClickListener() {

                        @Override
                        public void onItemClick(View view, int position) {
                            horizontalAdapter.setSelected(position);
                            mViewPager.setCurrentItem(position);
                        }
                    }));

        /*try {
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

        }*/


            btnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!isFrom) {
                        new sendMultiple(pictureModel).execute();
                    } else {
                        if (TextUtils.isEmpty(textMessage.getText().toString().trim())) {
                            textMessage.setError("Content is required!");
                        } else {
                            notesPhotos = new ArrayList<>();
                            for (PictureModel photo : pictureModel) {
                                File imageFile = new File(photo.getUrl());
                                NotesPhoto nphoto = new NotesPhoto(imageFile, textMessage.getText().toString());
                                notesPhotos.add(nphoto);
                            }
                            Intent data = new Intent();
                            data.putParcelableArrayListExtra(EXTRA_PHOTOS, (ArrayList<NotesPhoto>) notesPhotos);
                            setResult(RESULT_OK, data);
                            finish();
                        }
                    }
//                imageCompressed.clear();
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    void onTextChanged(String text, String position) {
        try {
            this.text = text;
            this.pos = position;
            pictureModel.get(Integer.valueOf(position)).setTitle(text);
            message.clear();
            if (isFrom) {
                message.put("content", text);
            }
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    class sendMultiple extends AsyncTask<String, Void, String> {
        ProgressDialog loading;
        String result = "";
        InputStream inputStream = null;
        private ProgressDialog progressDialog;
        ArrayList<PictureModel> pictureModel;

        public sendMultiple(ArrayList<PictureModel> pictureModel) {
            this.pictureModel = pictureModel;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog == null) {
                progressDialog = UtilsPD.createProgressDialog(ConfirmationSendFileMultiple.this);
                progressDialog.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            return null;
        }

        protected void onPostExecute(String s) {
            try {
                if (pictureModel.size() > 0) {
                    for (int i = 0; i < pictureModel.size(); i++) {

//                    new NystromImageCompression(true, i).execute(uriImage);
//                    Log.w("bismillah ah", pictureModel.get(i).getUrl() + "     " + pictureModel.get(i).getTitle());
                        String textCaption = pictureModel.get(i).getTitle() != null ? pictureModel.get(i).getTitle() : "";
                        MessengerDatabaseHelper messengerHelper = MessengerDatabaseHelper.getInstance(getApplicationContext());

                        Message msg = createNewMessage(jsonMessage(compressImage(pictureModel.get(i).getUrl()), compressImage(pictureModel.get(i).getUrl()), "", "", "", textCaption), messengerHelper.getMyContact().getJabberId(), destination, typeChat, type);
                        sendFile(msg);
//                    saveImage(utils.decodeBitmapFromPath(pictureModel.get(i).getUrl()));
                    }
                }
                DoDone();
                progressDialog.dismiss();
            } catch (Exception e) {
                reportCatch(e.getLocalizedMessage());
            }
        }
    }

    @Override
    public void onFragmentCreated(String purl, String purlthumb, String ptitle, String ptimestamp, String pdesc, String pmyuserid, String puserid, String pid, String pflag, String pColor) {
        try {
            this.purl = purl;
            this.purlthumb = purlthumb;
            this.ptitle = ptitle;
            this.ptimestamp = ptimestamp;
            this.pdesc = pdesc;
            this.pmyuserid = pmyuserid;
            this.puserid = puserid;
            this.pid = pid;
            this.pflag = pflag;
            this.pColor = pColor;
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    public void onUserSelectValue(String title, String timestamp, String desc) {
        try {
            ptitle = title;
            ptimestamp = timestamp;
            pdesc = desc;

        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    class DetailPictureAdapter extends FragmentPagerAdapter {

        public ArrayList<ModelPicture> data;
        public boolean isFrom;

        public DetailPictureAdapter(FragmentManager fm, ArrayList<ModelPicture> data, boolean isFrom) {
            super(fm);

            this.data = new ArrayList<>();
            this.data = data;
            this.isFrom = isFrom;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return data.get(position).getUserid();
        }

        @Override
        public Fragment getItem(int position) {
//        Log.w("sobami",data.get(position).getColor());
            return PictureFragment.newInstance(position, data.get(position).getUrl(), data.get(position).getUrl_thumb(), data.get(position).getTitle(), data.get(position).getTgl_upload(), data.get(position).getDescription(), data.get(position).getMyuserid(), data.get(position).getUserid(), data.get(position).getId_photo(), data.get(position).getFlag(), data.get(position).getColor());
        }

        public void deletePage(int position) {
            data.remove(position);
            notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);

            if (position <= getCount()) {
                FragmentManager manager = ((Fragment) object).getFragmentManager();
                FragmentTransaction trans = manager.beginTransaction();
                trans.remove((Fragment) object);
                trans.commitAllowingStateLoss();
            }
        }

        public void removeEnd() {
            if (data.size() > 0)
                data.remove(getCount() - 1);
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
            String jabberId = destination;
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

    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

//        float maxHeight = 816.0f;
//        float maxWidth = 612.0f;
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

//      width and height values are set maintaining the aspect ratio of the image

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

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
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

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
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
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "S-Team Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + "bc-" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    private String getRealPathFromURI(String contentURI) {
        try {
            Uri contentUri = Uri.parse(contentURI);
            Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
            if (cursor == null) {
                return contentUri.getPath();
            } else {
                cursor.moveToFirst();
                int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                return cursor.getString(index);
            }
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
        return null;
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        try {
            if (height > reqHeight || width > reqWidth) {
                final int heightRatio = Math.round((float) height / (float) reqHeight);
                final int widthRatio = Math.round((float) width / (float) reqWidth);
                inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            }
            final float totalPixels = width * height;
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;
            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }

        return inSampleSize;
    }

    class NystromImageCompression extends AsyncTask<String, Void, String> {
        private boolean fromGallery;
        private int position;

        public NystromImageCompression(boolean fromGallery, int position) {
            this.fromGallery = fromGallery;
            this.position = position;
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
            File file = new File(Environment.getExternalStorageDirectory().getPath(), "S-Team Images");
            if (!file.exists()) {
                file.mkdirs();
            }
            String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
            return uriSting;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            uriDecoded = result;
//            bigImageView.setImageBitmap(utils.decodeBitmapFromPath(result));
        }

    }

    private void saveImage(Bitmap finalBitmap) {
        try {
            String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
            System.out.println(root + " Root value in saveImage Function");
            File myDir = new File(root + "/S-Team Images");
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
            MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null,
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
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_PICKER && resultCode == RESULT_OK && data != null) {
                images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
                StringBuilder sb = new StringBuilder();
                pictureModel.clear();
                modelPictures.clear();
                for (int i = 0, l = images.size(); i < l; i++) {
                    sb.append(images.get(i).getPath() + "\n");

                    PictureModel item = new PictureModel();
                    ModelPicture model = new ModelPicture();
                    item.setUrl(images.get(i).getPath());
                    item.setId_photo(i + "");
                    model.setUrl(images.get(i).getPath());
                    model.setId_photo(i + "");
                    if (i == 0) {
                        item.setSelected(true);
                        model.setSelected(true);
                    } else {
                        item.setSelected(false);
                        model.setSelected(false);
                    }
                    pictureModel.add(item);
                    modelPictures.add(model);
                }
                horizontalAdapter = new HorizontalAdapter(getApplicationContext(), pictureModel);
                recyclerView.setAdapter(horizontalAdapter);
                horizontalAdapter.notifyDataSetChanged();
                adapter = new DetailPictureAdapter(getSupportFragmentManager(), modelPictures, isFrom);
                mViewPager.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void start(String destination_id) {
        try {
            ImagePicker.create(this)
                    .folderMode(true)
                    .destination(destination_id)
                    .imageTitle("Tap to select")
                    .single()
                    .reset(false)
                    .multi()
                    .limit(10)
                    .showCamera(true)
                    .imageDirectory("Camera")
                    .origin(images)
                    .start(REQUEST_CODE_PICKER);
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

}
