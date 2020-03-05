package com.byonchat.android;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.byonchat.android.adapter.HorizontalAdapter;
import com.byonchat.android.model.Image;
import com.byonchat.android.model.ModelPicture;
import com.byonchat.android.personalRoom.RecyclerItemClickListener;
import com.byonchat.android.personalRoom.model.NotesPhoto;
import com.byonchat.android.personalRoom.model.PictureModel;
import com.byonchat.android.personalRoom.transformer.DepthPageTransformer;
import com.byonchat.android.utils.ImageLoadingUtils;
import com.byonchat.android.utils.TouchImageView;
import com.byonchat.android.utils.Validations;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.byonchat.android.utils.Utility.reportCatch;

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

        try {
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

        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
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
        super.onActivityResult(requestCode, resultCode, data);
        try {
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
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
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
