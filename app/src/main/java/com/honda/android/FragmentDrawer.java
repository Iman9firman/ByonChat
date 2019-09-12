package com.honda.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.signature.StringSignature;
import com.honda.android.communication.MessengerConnectionService;
import com.honda.android.list.ListMenuItem;
import com.honda.android.list.utilLoadImage.ImageLoaderFromSD;
import com.honda.android.provider.Contact;
import com.honda.android.provider.MessengerDatabaseHelper;
import com.honda.android.provider.TimeLineDB;
import com.honda.android.utils.MediaProcessingUtil;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

import java.io.File;
import java.util.ArrayList;


public class FragmentDrawer extends Fragment {

    private static String TAG = FragmentDrawer.class.getSimpleName();

    private ActionBarDrawerToggle mDrawerToggle;
    public DrawerLayout mDrawerLayout;
    private View containerView;
    private FragmentDrawerListener drawerListener;

    TextView textView ;
    ImageView  imageView ;
    LinearLayout listView ;
    ArrayList<ListMenuItem> mArrayListData ;
    private MessengerDatabaseHelper messengerHelper;
    private ImageLoaderFromSD imageLoaderFromSD;

    private BroadcastHandler broadcastHandler = new BroadcastHandler();

    public FragmentDrawer() {

    }

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(getActivity());
        }

        if (imageLoaderFromSD == null){
            imageLoaderFromSD = new ImageLoaderFromSD(getActivity());
        }

        mArrayListData = new ArrayList<ListMenuItem>();
        if(mArrayListData.size()==0){
            mArrayListData.add(new ListMenuItem(R.drawable.ico_profile, "Set Status"));
            mArrayListData.add(new ListMenuItem(R.drawable.ico_themes,  "Themes Selection"));
            mArrayListData.add(new ListMenuItem(R.drawable.ico_setting,  "Settings"));
            mArrayListData.add(new ListMenuItem(R.drawable.ico_share, "Invite Friend"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating view layout
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        textView = (TextView) layout.findViewById(R.id.name_menu_slide);
        imageView = (ImageView) layout.findViewById(R.id.photo_menu_slide);
        listView = (LinearLayout) layout.findViewById(R.id.list_menu_slide);



        for (int i = 0; i < mArrayListData.size(); i++) {
            LayoutInflater inflater2 = (LayoutInflater) getActivity().getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View mLinearView = inflater2.inflate(R.layout.list_item_slide_menu, null);
            ImageView image = (ImageView) mLinearView
                    .findViewById(R.id.image);
            final TextView title = (TextView) mLinearView
                    .findViewById(R.id.textTitle);

            image.setImageResource(mArrayListData.get(i).icon);
            title.setText(mArrayListData.get(i).title);

            listView.addView(mLinearView);
            mLinearView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = null;
                    if (title.getText().toString().equalsIgnoreCase("Set Status")) {
                        i = new Intent(getActivity().getApplicationContext(), UpdateProfileActivity.class);
                    } else if (title.getText().toString().equalsIgnoreCase("Themes Selection")) {
                        i = new Intent(getActivity().getApplicationContext(), SkinSelectorActivity.class);
                    } else if (title.getText().toString().equalsIgnoreCase("Settings")) {
                     //   i = new Intent(getActivity().getApplicationContext(), ActivityDirection.class);
                       i = new Intent(getActivity().getApplicationContext(), MainSettingActivity.class);
                    } else if (title.getText().toString().equalsIgnoreCase("Message Broadcast")) {
                        i = new Intent(getActivity().getApplicationContext(), PickUserActivity.class);
                        i.putExtra(PickUserActivity.FROMACTIVITY,"Message Broadcast");
                    } else if (title.getText().toString().equalsIgnoreCase("Refresh Contacts")) {

                            /*actionBar.setSelectedNavigationItem(0);
                            slide_me.closeRightSide();
                            progressbar.setVisibility(View.VISIBLE);
                            startService(new Intent(getBaseContext(), RefreshContactService.class));*/
                    }  else if (title.getText().toString().equalsIgnoreCase("Create Groups")) {
                        //  i = new Intent(getActivity().getApplicationContext(), GroupAddInfoActivity.class);
                    } else if (title.getText().toString().equalsIgnoreCase("Invite Friend")) {
                      /*  i=new Intent(getActivity().getApplicationContext(), MoveToFriend.class);
                        i.putExtra("frnd_lat", "-6.1908002");
                        i.putExtra("frnd_longi","106.7679434");
                        i.putExtra("frnd_name", "frnd_name");
                        i.putExtra("frnd_id", "312");
                        i.putExtra("name", "iamn");
                        i.putExtra("id", "123");
                        startActivity(i);*/
                      shareIt();
                    }
                    if (i != null)  startActivity(i);
                    mDrawerLayout.closeDrawers();
                }
            });

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Intent i = new Intent(getActivity().getApplicationContext(), UpdateProfileActivity.class);
                        startActivity(i);
                        mDrawerLayout.closeDrawers();
                    }
            });

        }


        return layout;
    }
    public void refreshMenuBroadcast(){
        Contact contact = messengerHelper.getMyContact();
        File photoFile = getActivity().getApplicationContext().getFileStreamPath(MediaProcessingUtil
                .getProfilePicName(contact));
        if (photoFile.exists()) {
            if(Integer.valueOf(messengerHelper.getMyContact().getChangeProfile()) == 1){
                Glide.with(getActivity()).load(photoFile).asBitmap().centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).signature(new StringSignature(Long.toString(System.currentTimeMillis()))).into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getActivity().getResources(), resource);
                        circularBitmapDrawable.setCornerRadius(22);
                        imageView.setImageDrawable(circularBitmapDrawable);
                    }
                });
                messengerHelper.getMyContact().setChangeProfile(0);
            }

        }else {
            imageView.setImageBitmap(MediaProcessingUtil.getRoundedCornerBitmap(BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ic_no_photo), 22));
        }
//        imageLoaderFromSD.DisplayImage(MediaProcessingUtil.getProfilePic(messengerHelper.getMyContact().getJabberId()), imageView, true);
        textView.setText(contact.getRealname());
    }

    public void refreshMenu(){
        Contact contact = messengerHelper.getMyContact();
        /*File photoFile = getActivity().getApplicationContext().getFileStreamPath(MediaProcessingUtil
                .getProfilePicName(contact));
        if (photoFile.exists()) {
            Bitmap photo = BitmapFactory.decodeFile(String.valueOf(photoFile));
            imageView.setImageBitmap(MediaProcessingUtil.getRoundedCornerBitmap(photo, 22));
        }else {
            imageView.setImageBitmap(MediaProcessingUtil.getRoundedCornerBitmap(BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ic_no_photo), 22));
        }*/

        File photoFile = getActivity().getApplicationContext().getFileStreamPath(MediaProcessingUtil
                .getProfilePicName(contact));
        if (photoFile.exists()) {
                Glide.with(getActivity()).load(photoFile).asBitmap().centerCrop().signature(new StringSignature(Long.toString(System.currentTimeMillis()))).into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getActivity().getResources(), resource);
                        circularBitmapDrawable.setCornerRadius(22);
                        imageView.setImageDrawable(circularBitmapDrawable);
                    }
                });
            }
        textView.setText(contact.getRealname());
    }

    @Override
    public void onResume() {
        refreshMenu();
        IntentFilter f = new IntentFilter(UpdateProfileActivity.UPDATE_PROFILE);
        f.addAction(MessengerConnectionService.ACTION_STATUS_CHANGED);
        f.setPriority(2);
        getActivity().registerReceiver(broadcastHandler, f);
        super.onResume();

    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(broadcastHandler);
        super.onPause();
    }

    class BroadcastHandler extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UpdateProfileActivity.UPDATE_PROFILE.equals(intent.getAction())) {
                refreshMenu();
            } else if (MessengerConnectionService.ACTION_STATUS_CHANGED
                    .equals(intent.getAction())) {
                refreshMenuBroadcast();
            }
        }
    }

    private void shareIt() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareSubject = getResources().getString(R.string.share_subject) ;
        String shareTitle = getResources().getString(R.string.share_title) ;
        String shareBody = getResources().getString(R.string.share_body) ;
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSubject);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, shareTitle));
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }

    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    public interface FragmentDrawerListener {
        public void onDrawerItemSelected(View view, int position);
    }

    class ClearMemory extends AsyncTask<String, Void, String> {
        private String content = null;
        private boolean error = false;
        private Context mContext;

        public ClearMemory(Context context) {
            this.mContext = context;
        }

        protected String doInBackground(String... key) {
            try {
                Glide.get(getActivity()).clearDiskCache();
                Glide.get(getActivity()).clearMemory();
            } catch (Exception e) {
                error = true;
            }

            return content;
        }
    }
}
