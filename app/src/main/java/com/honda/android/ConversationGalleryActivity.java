package com.honda.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.honda.android.list.ConversationAdapter;
import com.honda.android.list.ImageFragment;
import com.honda.android.list.VideoFragment;
import com.honda.android.provider.Message;
import com.honda.android.provider.MessengerDatabaseHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ConversationGalleryActivity extends AppCompatActivity {
    public static final int TYPE_GALLERY = 0;
    public static final int TYPE_PROFILE_PICTURE = 1;

    public static final String KEY_SELECTED_FILE = "com.honda.android.ConversationImagesActivity.SELECTED_FILE";
    public static final String KEY_JABBER_ID = "com.honda.android.ConversationImagesActivity.JABBER_ID";
    public static final String KEY_TITLE = "com.honda.android.ConversationImagesActivity.TITLE";
    public static final String KEY_DISPLAY_FILE = "com.honda.android.ConversationImagesActivity.DISPLAY_FILE";
    public static final String KEY_DISPLAY_TYPE = "com.honda.android.ConversationImagesActivity.DISPLAY_TYPE";

    private static final String SQL_SELECT_MEDIA = "SELECT * FROM "
            + Message.TABLE_NAME + " where (" + Message.TYPE + "='"
            + Message.TYPE_IMAGE + "' OR " + Message.TYPE + "='"
            + Message.TYPE_VIDEO + "') AND (" + Message.DESTINATION + "=? OR "
            + Message.SOURCE + "=?)";
    private ViewPager viewPager;
    private ArrayList<Message> fileNames;
    private String selectedFile;
    private String jabberId;
    private int position;
    private int displayType;
    boolean start = true;
    private MessengerDatabaseHelper messengerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* this.requestWindowFeature(Window.FEATURE_NO_TITLE);
      this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        getSupportActionBar().hide();
        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(this);
        }

        if (fileNames == null) {
            fileNames = new ArrayList<Message>();
        }

        viewPager = new ViewPager(this);
        viewPager.setId(R.id.imagePager);
        setContentView(viewPager);

        selectedFile = getIntent().getStringExtra(KEY_SELECTED_FILE);
        jabberId = getIntent().getStringExtra(KEY_JABBER_ID);
        displayType = getIntent().getIntExtra(KEY_DISPLAY_TYPE, 0);

        FragmentManager fm = getSupportFragmentManager();
        viewPager.setAdapter(new FragmentStatePagerAdapter(fm) {

            @Override
            public int getCount() {
                return fileNames.size();
            }

            @Override
            public Fragment getItem(int index) {
                Message msg = fileNames.get(index);
                String urlFile="";
                String caption="";

                if(ConversationAdapter.isJSONValid(msg.getMessage())){
                    JSONObject jObject = null;
                    try {
                        jObject = new JSONObject(msg.getMessage());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (jObject != null) {
                        try {
                            urlFile = jObject.getString("s");
                            caption = jObject.getString("c");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (Message.TYPE_IMAGE.equals(msg.getType())) {
                        return getImageFragment(urlFile, caption);
                    } else {
                        return getVideoFragment(urlFile);
                    }
            }
        });
        if (displayType == TYPE_GALLERY) {
         //   new ImageLoader().execute();
        } else {
            Message m = new Message("", "", selectedFile);
            m.setType(Message.TYPE_IMAGE);
            fileNames.add(m);
            viewPager.getAdapter().notifyDataSetChanged();
            viewPager.setCurrentItem(0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            Intent intent;
            if (displayType == TYPE_GALLERY) {
                intent = new Intent(this, ConversationActivity.class);
                intent.putExtra(ConversationActivity.KEY_JABBER_ID, jabberId);
                intent.putExtra(ConversationActivity.KEY_TITLE, getTitle());
                intent.putExtra(ConversationActivity.KEY_CONVERSATION_TYPE,
                        ConversationActivity.CONVERSATION_TYPE_PRIVATE);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            } /*else {
                intent = new Intent(this, ProfileInfoActivity.class);
                intent.putExtra(ProfileInfoActivity.KEY_JABBER_ID, jabberId);
                intent.putExtra(ProfileInfoActivity.KEY_REFERENCE, getIntent()
                        .getStringExtra(ProfileInfoActivity.KEY_REFERENCE));
            }*/

            finish();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private ImageFragment getImageFragment(String fname,String caption) {
        ImageFragment fragment = new ImageFragment();
        Bundle b = new Bundle();
        b.putString(ImageFragment.KEY_BUNDLE_FILENAME, fname);
        b.putString(ImageFragment.KEY_BUNDLE_FILECAPTION, caption);
        fragment.setArguments(b);
        return fragment;
    }

    private VideoFragment getVideoFragment(String fname) {
        VideoFragment fragment = new VideoFragment();
        Bundle b = new Bundle();
        b.putBoolean(VideoFragment.KEY_BUNDLE_START_VIDEO,
                Boolean.valueOf(start));
        b.putString(VideoFragment.KEY_BUNDLE_FILENAME, fname);
        fragment.setArguments(b);
        start = false;
        return fragment;
    }

    /*class ImageLoader extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Cursor cursor = messengerHelper.query(SQL_SELECT_MEDIA,
                    new String[] { jabberId, jabberId });

            while (cursor.moveToNext()) {
                Message msg = new Message(cursor);
              *//*  String fname = msg.getMessage();
                File f = new File(fname);*//*
                String[] s = msg.getMessage().split(";");
                if (!(s[0]).equalsIgnoreCase("")) {
                    File f = new File(s[0]);
                    if (f.exists()) {
                        if (s[0].equals(selectedFile))
                            position = fileNames.size();
                        fileNames.add(msg);
                    }
                }
            }
            cursor.close();

            viewPager.getAdapter().notifyDataSetChanged();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            viewPager.setCurrentItem(position);
        }

    }*/
}
