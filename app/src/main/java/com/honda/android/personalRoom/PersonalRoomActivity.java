package com.honda.android.personalRoom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.honda.android.ConversationActivity;
import com.honda.android.R;
import com.honda.android.communication.MessengerConnectionService;
import com.honda.android.communication.NetworkInternetConnectionStatus;
import com.honda.android.list.utilLoadImage.ImageLoaderFromSD;
import com.honda.android.personalRoom.asynctask.ProfileSaveDescription;
import com.honda.android.personalRoom.database.DBPersonal;
import com.honda.android.personalRoom.database.Profile;
import com.honda.android.personalRoom.model.ProfileModel;
import com.honda.android.personalRoom.view.SelectableRoundedImageView;
import com.honda.android.provider.Contact;
import com.honda.android.provider.MessengerDatabaseHelper;
import com.honda.android.utils.BlurBuilder;
import com.honda.android.utils.HttpHelper;
import com.honda.android.utils.ImageFilePath;
import com.honda.android.utils.MediaProcessingUtil;
import com.honda.android.utils.UtilsPD;
import com.honda.android.utils.ValidationsKey;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import carbon.widget.FrameLayout;

/**
 * Created by lukma on 4/4/2016.
 */
public class PersonalRoomActivity extends AppCompatActivity {
    private ImageView profilePicCode;
    private TabLayout tabLayout;
    private ImageView mImageCover;
    private ViewPager viewPager;
    Toolbar toolbar;
    AppBarLayout appbar;
    public static ImageView mBtChat, mBtAddRoom;
    Target imageView;
    Target profilePic;
    FrameLayout frameImage;
    ImageView backdropBlur;
    LinearLayout backButton, framebackdrop;
    public ImageLoaderFromSD imageLoaderSD;
    private DBPersonal db;
    private ArrayList<Profile> profile;
    public static String is_cover_upload = "";
    public static final String EXTRA_ID = "id";
    public static final String EXTRA_NAME = "name";
    private MessengerDatabaseHelper messengerHelper;
    public static String userid, username;
    public static SQLiteDatabase dbb;
    private List<ProfileModel> profileItems;
    Context context;
    //    public static PersonalRoomActivity activity;
    private Uri fileUri;
    private String mine;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int RESULT_LOAD_IMAGE_REQUEST_CODE = 200;
    public static final String IMAGE_DIRECTORY_NAME = "Byonchat Room";
    private static final String TAG = PersonalRoomActivity.class.getSimpleName();
    private static String URL_GET_DATA_PROFILE = "https://" + MessengerConnectionService.HTTP_SERVER2 + "/personal_room/webservice/view/api_view_profile.php";
    private static String URL_DELETE_COVER = "https://" + MessengerConnectionService.HTTP_SERVER2 + "/personal_room/webservice/proses/cover_photo_delete.php";
    TextView title;
    RequestPersonal requestPersonal;
    Activity aa = PersonalRoomActivity.this;
    private Contact contact;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= 20) {
            setTheme(R.style.HeaderTransparent);
        }
        setContentView(R.layout.activity_personal_room);

        appbar = (AppBarLayout) findViewById(R.id.appbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        title = (TextView) findViewById(R.id.titleToolbar);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        mBtChat = (ImageView) findViewById(R.id.buttonChat);
        backButton = (LinearLayout) findViewById(R.id.layout_back_button);
        imageView = (Target) findViewById(R.id.backdrop);
        backdropBlur = (ImageView) findViewById(R.id.backdropblur);
        profilePic = (Target) findViewById(R.id.imagePhoto);
        frameImage = (FrameLayout) findViewById(R.id.frameImage);
        framebackdrop = (LinearLayout) findViewById(R.id.framebackdrop);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(this);
        }

        if (db == null) {
            db = new DBPersonal(PersonalRoomActivity.this);
        }

        if (null == profile) {
            profile = new ArrayList<Profile>();
        }

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        userid = i.getStringExtra(EXTRA_ID);
        username = i.getStringExtra(EXTRA_NAME);
        mine = messengerHelper.getMyContact().getJabberId();

        if (contact == null) {
            contact = messengerHelper.getContact(userid);
        }

        if (!userid.matches(messengerHelper.getMyContact().getJabberId())) {
            if (contact == null) {
                title.setText(Html.fromHtml(username!=null?username:userid));
            } else {
                if (contact.getName().equalsIgnoreCase("") || contact.getName() == null) {
                    title.setText(userid);
                } else {
                    title.setText(contact.getName());
                }
            }
        } else {
            if (TextUtils.isEmpty(messengerHelper.getMyContact().getRealname())) {
                title.setText(userid);
            } else {
                title.setText(messengerHelper.getMyContact().getRealname());
            }
        }

        if (NetworkInternetConnectionStatus.getInstance(aa.getApplicationContext()).isOnline(aa.getApplicationContext())) {
            Picasso.with(aa).invalidate("https://" + MessengerConnectionService.F_SERVER + "/toboldlygowherenoonehasgonebefore/" + userid + ".jpg");

            Picasso.with(aa).load("https://" + MessengerConnectionService.F_SERVER + "/toboldlygowherenoonehasgonebefore/" + userid + ".jpg")
                    .error(R.drawable.ic_no_photo)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .into(profilePic);
        } else {
            Picasso.with(aa).load("https://" + MessengerConnectionService.F_SERVER + "/toboldlygowherenoonehasgonebefore/" + userid + ".jpg")
                    .error(R.drawable.ic_no_photo)
                    .into(profilePic);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setupViewPager(viewPager);
        viewPager.setOffscreenPageLimit(1);
        tabLayout.setupWithViewPager(viewPager);

        if (!userid.equalsIgnoreCase(messengerHelper.getMyContact().getJabberId())) {
            mBtChat.setVisibility(View.VISIBLE);
            mBtChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PersonalRoomActivity.this, ConversationActivity.class);
                    String jabberId = userid;
                    String action = PersonalRoomActivity.this.getIntent().getAction();
                    if (Intent.ACTION_SEND.equals(action)) {
                        Bundle extras = PersonalRoomActivity.this.getIntent().getExtras();
                        if (extras.containsKey(Intent.EXTRA_STREAM)) {
                            try {
                                Uri uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);
                                String pathToSend = MediaProcessingUtil.getRealPathFromURI(
                                        PersonalRoomActivity.this.getContentResolver(), uri);
                                intent.putExtra(ConversationActivity.KEY_FILE_TO_SEND,
                                        pathToSend);
                            } catch (Exception e) {
                                Log.e(getClass().getSimpleName(),
                                        "Error getting file from action send: "
                                                + e.getMessage(), e);
                            }
                        }
                    }

                    intent.putExtra(ConversationActivity.KEY_JABBER_ID, jabberId);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        db.open();
        Cursor c = db.getCoverPersonal(userid);
        if (c.getCount() > 0) {
            requestPersonal = new RequestPersonal(PersonalRoomActivity.this);
            requestPersonal.execute(userid);
//            if (c.getString(c.getColumnIndexOrThrow(DBPersonal.PERSONAL_COVER)).equalsIgnoreCase(null)) {
//                if (NetworkInternetConnectionStatus.getInstance(aa.getApplicationContext()).isOnline(aa.getApplicationContext())) {
//                    requestPersonal = new RequestPersonal(PersonalRoomActivity.this);
//                    requestPersonal.execute(userid);
//                }
//            } else {
//                refreshPersonal();
//            }
        } else {
            if (NetworkInternetConnectionStatus.getInstance(aa.getApplicationContext()).isOnline(aa.getApplicationContext())) {
                requestPersonal = new RequestPersonal(PersonalRoomActivity.this);
                requestPersonal.execute(userid);
            }
        }
        db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (userid.matches(messengerHelper.getMyContact().getJabberId())) {
            getMenuInflater().inflate(R.menu.sample_actions, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (userid.equalsIgnoreCase(messengerHelper.getMyContact().getJabberId())) {
            switch (id) {
                case R.id.btn_camera:
                    ShowAlertDialogWithListview(is_cover_upload);
                    return true;
                default:
                    if (id == R.id.btn_camera) {
                        ShowAlertDialogWithListview(is_cover_upload);
                        return true;
                    }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshPersonal() {
        db.open();
        Cursor c = db.getCoverPersonal(userid);
        if (c.getCount() > 0) {
            if (c.getString(c.getColumnIndexOrThrow(DBPersonal.PERSONAL_COVER)).equalsIgnoreCase("")) {

                backdropBlur.setVisibility(View.VISIBLE);
                framebackdrop.setVisibility(View.VISIBLE);
                Picasso.with(this).invalidate("https://scontent-sin6-1.xx.fbcdn.net/v/t1.0-9/22495_4211261274315_991734598_n.jpg?oh=441903ddef78ea17bda3d9cb8b7f720f&oe=5904D7CB");
                Picasso.with(aa).load("https://scontent-sin6-1.xx.fbcdn.net/v/t1.0-9/22495_4211261274315_991734598_n.jpg?oh=441903ddef78ea17bda3d9cb8b7f720f&oe=5904D7CB"
                ).into(imageView);

                new LoadImageFromURL(backdropBlur).execute(c.getString(c.getColumnIndexOrThrow(DBPersonal.PERSONAL_COVER)));
            } else {
                backdropBlur.setVisibility(View.VISIBLE);
                framebackdrop.setVisibility(View.VISIBLE);
                Picasso.with(this).invalidate(c.getString(c.getColumnIndexOrThrow(DBPersonal.PERSONAL_COVER)));
                Picasso.with(aa).load(c.getString(c.getColumnIndexOrThrow(DBPersonal.PERSONAL_COVER))).into(imageView);

                new LoadImageFromURL(backdropBlur).execute(c.getString(c.getColumnIndexOrThrow(DBPersonal.PERSONAL_COVER)));
            }
        }
        db.close();
    }

    public void toolbarCollapse() {
        if (appbar.getTop() < 0)
            appbar.setExpanded(false);
        else
            appbar.setExpanded(false);

    }

    public void toolbarOpen() {
        if (appbar.getTop() < 0)
            appbar.setExpanded(true);
    }

    public class LoadImageFromURL extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        public LoadImageFromURL(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            // TODO Auto-generated method stub
            Bitmap bitmap = null;
            Bitmap blurredBitmap = null;
            try {
                URL url = new URL(params[0]);
                bitmap = BitmapFactory.decodeStream((InputStream) url.getContent());
                blurredBitmap = BlurBuilder.blur(aa, bitmap);

                return blurredBitmap;

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            backdropBlur.setBackgroundDrawable(new BitmapDrawable(getResources(), result));
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        SpannableString profiles = new SpannableString(Html.fromHtml(" My <br/> Profiles "));
        SpannableString notes = new SpannableString(Html.fromHtml(" My <br/> Notes "));
        SpannableString pictures = new SpannableString(Html.fromHtml(" My <br/> Pictures "));
        SpannableString videos = new SpannableString(Html.fromHtml(" My <br/> Videos "));

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        if (mine.equalsIgnoreCase(userid)) {
            adapter.addFragment(FragmentMyProfileMine.newInstance(userid, username, "", "", "", "", true), profiles.toString());
        } else {
            adapter.addFragment(FragmentMyProfile.newInstance(userid, username, "", "", "", "", true), profiles.toString());
        }
        adapter.addFragment(FragmentMyNote.newInstance(userid, username, "", "", "", "", true,PersonalRoomActivity.this), notes.toString());
        adapter.addFragment(FragmentMyPicture.newInstance(userid, username, "", "", "", "004a6d", true,PersonalRoomActivity.this), pictures.toString());
        adapter.addFragment(FragmentMyVideo.newInstance(userid, username, "", "", "", "004a6d", true,PersonalRoomActivity.this), videos.toString());
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void ShowAlertDialogWithListview(String is_cover_upload) {
        List<String> mAnimals = new ArrayList<String>();
        mAnimals.add("Take a photo");
        mAnimals.add("Choose from gallery");
        if (is_cover_upload.equals("1")) {
            mAnimals.add("Delete a cover photo");
        }
        final CharSequence[] Animals = mAnimals.toArray(new String[mAnimals.size()]);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setItems(Animals, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    captureImage();
                } else if (item == 1) {
                    loadImage();
                } else if (item == 2) {
                    deleteCover();
                }
                String selectedText = Animals[item].toString();
            }
        });
        AlertDialog alertDialogObject = dialogBuilder.create();
        alertDialogObject.show();
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    private void loadImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, RESULT_LOAD_IMAGE_REQUEST_CODE);
    }

    private void deleteCover() {
        String picturename = "";
        db.open();
        Cursor c = db.getCoverPersonal(userid);
        if (c.getCount() > 0) {
            if (!c.getString(c.getColumnIndexOrThrow(DBPersonal.PERSONAL_COVER)).equalsIgnoreCase("") || c.getString(c.getColumnIndexOrThrow(DBPersonal.PERSONAL_COVER)) != null) {
                picturename = c.getString(c.getColumnIndexOrThrow(DBPersonal.PERSONAL_COVER));
            }
        }
        db.close();
        new deleteCover().execute(URL_DELETE_COVER, messengerHelper.getMyContact().getJabberId(), picturename);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == this.RESULT_OK) {
                launchUploadActivity();
            } else if (resultCode == this.RESULT_CANCELED) {
            } else {
                Toast.makeText(this.getApplicationContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == RESULT_LOAD_IMAGE_REQUEST_CODE) {
            if (resultCode == this.RESULT_OK) {
                Uri selectedUri = data.getData();
                String selectedImagePath = ImageFilePath.getPath(this, selectedUri);

                launchUploadActivityFromImgSelected(selectedImagePath);

            } else if (resultCode == this.RESULT_CANCELED) {
            } else {
                Toast.makeText(this.getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void launchUploadActivity() {
        Intent i = new Intent(PersonalRoomActivity.this, PRCoverUploadActivity.class);
        i.putExtra("filePath", fileUri.getPath());
        i.putExtra("userid", userid);
        startActivity(i);
    }

    private void launchUploadActivityFromImgSelected(String sourceFileUri) {
        int day, month, year;
        int second, minute, hour;
        GregorianCalendar date = new GregorianCalendar();

        day = date.get(Calendar.DAY_OF_MONTH);
        month = date.get(Calendar.MONTH);
        year = date.get(Calendar.YEAR);

        second = date.get(Calendar.SECOND);
        minute = date.get(Calendar.MINUTE);
        hour = date.get(Calendar.HOUR);

        String name = (hour + "" + minute + "" + second + "" + day + "" + (month + 1) + "" + year);
        String tag = name + ".jpg";
        String fileName = sourceFileUri.replace(sourceFileUri, tag);

//        DialogFragment newFragment =  DialogFragmentPicture.newInstance(sourceFileUri , userid);
//        newFragment.show(getFragmentManager(), "dialog");

        Intent i = new Intent(PersonalRoomActivity.this, PRCoverUploadActivity.class);
        i.putExtra("filePath", sourceFileUri);
        i.putExtra("userid", userid);
        startActivity(i);
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {
        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    public static Bitmap getRoundedCornerBitmapBorder(Bitmap src, float round) {
        Bitmap result = null;
        if (src != null) {
            // Source image size
            int width = src.getWidth();
            int height = src.getHeight();
            // create result bitmap output
            result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            // set canvas for painting
            Canvas canvas = new Canvas(result);
            canvas.drawARGB(0, 0, 0, 0);


            // configure paint
            final Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);

            // configure rectangle for embedding
            final Rect rect = new Rect(0, 0, width, height);
            final RectF rectF = new RectF(rect);

            // draw Round rectangle to canvas
            canvas.drawRoundRect(rectF, round, round, paint);

            // create Xfer mode
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            // draw source image to canvas
            canvas.drawBitmap(src, rect, rect, paint);
        }
        // return final image
        return result;
    }

    class RequestPersonal extends AsyncTask<String, Void, String> {

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();

        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content = null;
        private boolean error = false;
        private Context mContext;

        public RequestPersonal(Context context) {
            this.mContext = context;
        }

        @Override
        protected void onPreExecute() {
        }

        protected String doInBackground(String... key) {
            JSONArray json_tag_room = null;
            JSONObject json_tag_name = null;
            try {
                HttpClient httpClient = HttpHelper
                        .createHttpClient(mContext);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);

                nameValuePairs.add(new BasicNameValuePair("userid", key[0]));

                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                HttpPost post = new HttpPost(URL_GET_DATA_PROFILE);
                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                //Response from the Http Request
                response = httpclient.execute(post);
                StatusLine statusLine = response.getStatusLine();
                //Check the Http Request for success
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    content = out.toString();

                    db.open();
                    profile = db.retrieveProfile(userid);
                    if (profile.size() > 0) {
                        db.deletePersonal(userid);
                    }
                    db.close();

                    JSONObject json = new JSONObject(content);
                    JSONObject json_data = json.getJSONObject("data");
                    json_tag_room = json_data.getJSONArray("tag_room");

                    is_cover_upload = json_data.getString("is_cover_upload");

                    String photo;
                    if (json_data.getString("cover_photo").equalsIgnoreCase("")) {
                        photo = "";
                    } else {
                        photo = json_data.getString("cover_photo");
                    }

                    Profile profile = new Profile(json.getString("userid"), username, json_data.getString("desc_profile"), photo, "");
                    db.open();
                    db.insertProfile(profile);
                    db.close();

                    /*if (json.getString("userid").equalsIgnoreCase(null)) {
//                            Toast.makeText(PersonalRoomActivity.this, "cover kosong", Toast.LENGTH_SHORT).show();
//                            loadBackdrop();
                    } else {
                        //        Glide.with(activity.getApplicationContext()).load(json_cover_photo).centerCrop().into(imageView);
//                        Toast.makeText(getApplicationContext(),json_cover_photo,Toast.LENGTH_LONG).show();
                    }*/

                } else {
                    error = true;
                    content = statusLine.getReasonPhrase();
                    response.getEntity().getContent().close();
                    throw new IOException(content);
                }

            } catch (ClientProtocolException e) {
                content = e.getMessage();
                error = true;
            } catch (IOException e) {
                content = e.getMessage();
                error = true;
            } catch (Exception e) {
                error = true;
            }

            return content;
        }

        protected void onCancelled() {
        }

        protected void onPostExecute(String content) {
//            if (error) {
//                if (content.contains("invalid_key")) {
//                    if (NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)) {
//                        String key = new ValidationsKey().getInstance(mContext).key(true);
//                        if (key.equalsIgnoreCase("null")) {
//                            Toast.makeText(mContext, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
//                        } else {
//                            requestPersonal = new RequestPersonal(PersonalRoomActivity.this);
//                            requestPersonal.execute(key);
//                        }
//                    } else {
//                        Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(mContext, R.string.pleaseTryAgain + "tess", Toast.LENGTH_LONG).show();
//                }
//            } else {
            refreshPersonal();
//            }
        }
    }

    class deleteCover extends AsyncTask<String, Void, String> {
        ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
        String result = "";
        String picturename;
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog == null) {
                progressDialog = UtilsPD.createProgressDialog(aa);
                progressDialog.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            picturename = params[2];
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("userid", params[1]);
            String result = profileSaveDescription.sendPostRequest(params[0], data);
            return result;
        }

        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            if (s.equals("1")) {
                onResume();
                return;
            } else {
                Toast.makeText(aa, "Failed to delete photo", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
