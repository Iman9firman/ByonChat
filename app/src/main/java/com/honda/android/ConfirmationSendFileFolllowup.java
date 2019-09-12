package com.honda.android;

import android.app.ProgressDialog;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.LruCache;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.honda.android.adapter.HorizontalAdapter;
import com.honda.android.local.CacheManager;
import com.honda.android.model.Image;
import com.honda.android.model.ImageCompressed;
import com.honda.android.model.ModelPicture;
import com.honda.android.personalRoom.RecyclerItemClickListener;
import com.honda.android.personalRoom.model.NotesPhoto;
import com.honda.android.personalRoom.model.PictureModel;
import com.honda.android.personalRoom.transformer.DepthPageTransformer;
import com.honda.android.provider.FilesURL;
import com.honda.android.provider.FilesURLDatabaseHelper;
import com.honda.android.provider.Group;
import com.honda.android.provider.Message;
import com.honda.android.provider.MessengerDatabaseHelper;
import com.honda.android.utils.ImageFilePath;
import com.honda.android.utils.ImageLoadingUtils;
import com.honda.android.utils.MediaProcessingUtil;
import com.honda.android.utils.TouchImageView;
import com.honda.android.utils.UploadService;
import com.honda.android.utils.UtilsPD;
import com.honda.android.utils.Validations;
import com.honda.android.videotrimmer.utils.FileUtils;
import com.honda.android.widget.VideoSlaceSeekBar;

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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * Created by byonc on 4/20/2017.
 */

public class ConfirmationSendFileFolllowup extends AppCompatActivity implements FollowupFragment.OnFragmentInteractionListener {
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
    EditText textMessage;
    private ImageLoadingUtils utils;
    String iname;
    private long numberOfImages = 0;
    private String Image_path = null;
    public static final String CLOSEMEMEACTIVITY = "byonchat.meme.close.activity";
    private static final String TAG = ConfirmationSendFileFolllowup.class.getSimpleName();
    private String pmyuserid, puserid, purl, purlthumb, ptitle, ptimestamp, pdesc, pid, pflag, pColor;
    private int REQUEST_CODE_PICKER = 2000;
    private ArrayList<Image> images = new ArrayList<>();
    boolean isFrom = false;
    public static final String EXTRA_PHOTOS = "photos";
    public static final String EXTRA_CAPTIONS = "captions";
    public static final String EXTRA_TEXT_CAPTIONS = "text_captions";
    public final static HashMap<String, String> message = new HashMap<>();
    private Map<String, String> captions;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("saved_captions", (HashMap<String, String>) captions);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmation_send_file_multiple);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new Validations().getInstance(getApplicationContext()).header(getWindow()));

        ConfirmationSendFileMultiple.message.clear();
        if (getIntent().getExtras().containsKey("isFrom")) {
            isFrom = true;
        }

        if (savedInstanceState != null) {
            captions = (Map<String, String>) savedInstanceState.getSerializable("saved_captions");
        }

        if (captions == null) {
            captions = new HashMap<>();
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

        textMessage.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        recyclerView
                .setLayoutManager(new LinearLayoutManager(ConfirmationSendFileFolllowup.this, LinearLayoutManager.HORIZONTAL, false));

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
                if (pictureModel.size() == 1) {
                    if (viewHolder instanceof HorizontalAdapter.MyViewHolder) return 0;
                }

                return super.getSwipeDirs(recyclerView, viewHolder);
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

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(textMessage.getText().toString())) {
                    textMessage.setError("Message is required!");
                } else {
                    notesPhotos = new ArrayList<>();
                    for (PictureModel photo : pictureModel) {
                        File imageFile = new File(photo.getUrl());
                        NotesPhoto nphoto = new NotesPhoto(imageFile, textMessage.getText().toString());
                        captions.put(EXTRA_TEXT_CAPTIONS,
                                textMessage.getText().toString().trim());
                        notesPhotos.add(nphoto);
                    }
                    Intent data = new Intent();
                    data.putParcelableArrayListExtra(EXTRA_PHOTOS, (ArrayList<NotesPhoto>) notesPhotos);
                    data.putExtra(EXTRA_CAPTIONS, (HashMap<String, String>) captions);
                    setResult(RESULT_OK, data);
                    finish();
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

    @Override
    public void onFragmentCreated(String purl, String purlthumb, String ptitle, String ptimestamp, String pdesc, String pmyuserid, String puserid, String pid, String pflag, String pColor) {
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
            return FollowupFragment.newInstance(position, data.get(position).getUrl(), data.get(position).getUrl_thumb(), data.get(position).getTitle(), data.get(position).getTgl_upload(), data.get(position).getDescription(), data.get(position).getMyuserid(), data.get(position).getUserid(), data.get(position).getId_photo(), data.get(position).getFlag(), data.get(position).getColor());
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
    }

}
