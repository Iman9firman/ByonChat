package com.byonchat.android;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.createMeme.FilteringImage;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.Interval;
import com.byonchat.android.provider.IntervalDB;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.TimeLine;
import com.byonchat.android.provider.TimeLineDB;
import com.byonchat.android.utils.DialogUtil;
import com.byonchat.android.utils.HttpHelper;
import com.byonchat.android.utils.MediaProcessingUtil;
import com.byonchat.android.utils.UploadProfileService;
import com.byonchat.android.utils.Utility;
import com.byonchat.android.utils.ValidationsKey;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.soundcloud.android.crop.Crop;

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
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class UpdateProfileActivity extends ABNextServiceActivity implements
        DialogInterface.OnClickListener, EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {
    private static final String[] photoMenu = new String[]{"Take a photo",
            "Choose from Gallery",/*"Remove",*/"View"};
    public static final String UPDATE_PROFILE = UpdateProfileActivity.class.getName() + ".updateProfile";
    private static final String BUTTON_TITLE = "SAVE";
    private static final int REQ_CAMERA = 1201;
    private static final int REQ_FROM_FILE = 1202;
    private static final int REQ_GALLERY = 1203;

    String FILE_UPLOAD_URL = "https://" + MessengerConnectionService.F_SERVER + "/profile.php";
    String FILE_DELETE_FOTO = "https://" + MessengerConnectionService.F_SERVER + "/hapus_foto.php";
    private MessengerDatabaseHelper dbhelper;
    private Contact contact;
    private QuickContactBadge btnPhoto;
    private File imageOutput;
    private EditText txtStatus;
    private TextView txtPhonenumber;
    private File photoFile;
    private ImageButton btnEmoticon;
    private ListView listStatusDefault;
    Uri selectedImageUri;
    private LinearLayout emojicons;
    boolean imageDelete = false;
    protected String mColor, mColorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile);
        // getSupportActionBar().setBackgroundDrawable(new Validations().getInstance(getApplicationContext()).header());
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

        mColor = getIntent().getStringExtra(Constants.EXTRA_COLOR);
        mColorText = getIntent().getStringExtra(Constants.EXTRA_COLORTEXT);

        resolveToolbar();

        View view = findViewById(R.id.buttonAbNext);
        if (view != null && view instanceof Button) {
            ((Button) view).setTextColor(Color.parseColor("#" + mColorText));
        }

        btnPhoto = (QuickContactBadge) findViewById(R.id.registrationPhotoButton);
        btnPhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showChoosePhotoOptions();
            }
        });

        if (dbhelper == null) {
            dbhelper = MessengerDatabaseHelper.getInstance(this);
        }
        contact = dbhelper.getMyContact();
        txtStatus = (EditText) findViewById(R.id.txtStatus);
        txtStatus.setText(dbhelper.getMyContact().getStatus());
        txtStatus.setCursorVisible(false);
        txtPhonenumber = (TextView) findViewById(R.id.textViewPhoneNumber);
        txtPhonenumber.setText("+" + Utility.formatPhoneNumber(dbhelper.getMyContact().getJabberId()));
        listStatusDefault = (ListView) findViewById(R.id.listStatusDefault);
        emojicons = (LinearLayout) findViewById(R.id.emojiconsLayout);
        btnEmoticon = (ImageButton) findViewById(R.id.btn_add_emoticon);
        btnEmoticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emojicons.getVisibility() == View.GONE) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                    Animation animFade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_slide_in_bottom);
                    emojicons.setVisibility(View.VISIBLE);
                    emojicons.startAnimation(animFade);

                    txtStatus.setFocusable(false);
                } else {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    txtStatus.setFocusableInTouchMode(true);
                    txtStatus.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(txtStatus, InputMethodManager.SHOW_IMPLICIT);
                    emojicons.setVisibility(View.GONE);
                }
            }
        });

        txtStatus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                txtStatus.setCursorVisible(true);
                txtStatus.setSelection(txtStatus.getText().length());
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(txtStatus, InputMethodManager.SHOW_IMPLICIT);
                if (emojicons.getVisibility() == View.VISIBLE) {
                    emojicons.setVisibility(View.GONE);
                }
                return false;
            }
        });

        photoFile = getFileStreamPath(MediaProcessingUtil
                .getProfilePicName(contact));
        if (photoFile.exists()) {
            Bitmap asd = BitmapFactory.decodeFile(String.valueOf(photoFile));
            btnPhoto.setImageBitmap(MediaProcessingUtil.getRoundedCornerBitmap(asd, 10));

        } else {
            Bitmap icon2 = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                    R.drawable.ic_no_photo);
            btnPhoto.setImageBitmap(MediaProcessingUtil.getRoundedCornerBitmap(icon2, 10));
        }
        if (pdialog != null) {
            pdialog.setMessage("Please wait a moment ..");
        }


        String[] values = new String[]{"Available",
                "Busy",
                "At school",
                "At the movies",
                "Battery about to die",
                "I love byonchat",
                "At the gym",
                "Sleeping"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        listStatusDefault.setAdapter(adapter);
        listStatusDefault.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String itemValue = (String) listStatusDefault.getItemAtPosition(position);
                txtStatus.setText(itemValue);
            }
        });

    }

    protected void resolveToolbar() {
        if (mColor.equalsIgnoreCase("FFFFFF") && mColorText.equalsIgnoreCase("000000")) {
            View lytToolbarDark = getLayoutInflater().inflate(R.layout.toolbar_dark, null);
            Toolbar toolbarDark = lytToolbarDark.findViewById(R.id.toolbar_dark);
        } else {
            FilteringImage.SystemBarBackground(getWindow(), Color.parseColor("#" + mColor));
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#" + mColor)));
        }
    }

    @Override
    protected String getButtonTitle() {
        return BUTTON_TITLE;
    }

    private void showChoosePhotoOptions() {
        Builder builder = DialogUtil.generateItemsDialog(this,
                "Profile Picture", photoMenu, this);
        builder.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        int actId = 0;

        switch (which) {
            case 0:
                Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
                imageOutput = MediaProcessingUtil.getOutputFile("jpeg");
                i.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(imageOutput));
                selectedImageUri = Uri.fromFile(imageOutput);
                startActivityForResult(i, REQ_CAMERA);
                break;

            case 1:
                Intent intentPic;
                if (Build.VERSION.SDK_INT < 19) {
                    intentPic = new Intent();
                    intentPic.setAction(Intent.ACTION_GET_CONTENT);
                    intentPic.setType("image/*");
                    intentPic.putExtra("outputFormat",
                            Bitmap.CompressFormat.JPEG.toString());
                    startActivityForResult(intentPic, REQ_GALLERY);
                } else {
                    intentPic = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intentPic.addCategory(Intent.CATEGORY_OPENABLE);
                    intentPic.setType("image/*");
                    intentPic.putExtra("outputFormat",
                            Bitmap.CompressFormat.JPEG.toString());
                    startActivityForResult(intentPic, REQ_GALLERY);
                }

                break;

            case 2:

                if (imageDelete == false) {
                    Intent intent = new Intent(this, FullScreenUpdateProfileActivity.class);
                    intent.putExtra(FullScreenUpdateProfileActivity.JAB_ID, contact.getJabberId());
                    if (imageOutput != null) {
                        intent.putExtra(FullScreenUpdateProfileActivity.PATH, imageOutput.getAbsolutePath());
                    } else {
                        intent.putExtra(FullScreenUpdateProfileActivity.PATH, photoFile.getAbsolutePath());
                    }
                    startActivity(intent);
                }

                break;
            case 3:
                if (NetworkInternetConnectionStatus.getInstance(this).isOnline(this)) {
                    final AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
                    alertbox.setMessage("Are you sure delete photo?");
                    alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            Bitmap icon2 = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                    R.drawable.ic_no_photo);
                            btnPhoto.setImageBitmap(MediaProcessingUtil.getRoundedCornerBitmap(icon2, 10));
                            imageDelete = true;
                            String key = new ValidationsKey().getInstance(getApplicationContext()).key(true);
                            if (key.equalsIgnoreCase("null")) {
                                Toast.makeText(getApplicationContext(), R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                                pdialog.dismiss();
                            } else {
                                new requestDeleteFoto(getApplicationContext()).execute(key);
                            }
                        }
                    });
                    alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    });
                    alertbox.show();

                } else {
                    Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                }
                break;


        }
        dialog.dismiss();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //closing transition animations
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }


    private void startMainActivity() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_GALLERY) {
                imageOutput = MediaProcessingUtil.getOutputFile("jpeg");
                beginCrop(result.getData(), Uri.fromFile(imageOutput));
            } else if (requestCode == Crop.REQUEST_CROP) {
                handleCrop(resultCode, result);
            } else if (requestCode == REQ_CAMERA) {
                getContentResolver().notifyChange(selectedImageUri, null);
                if (decodeFile(imageOutput.getAbsolutePath())) {
                    beginCrop(Uri.fromFile(imageOutput));
                }
            }
        }

    }

    private void beginCrop(Uri source) {
        Crop.of(source, source).asSquare().withMaxSize(480, 480).start(this);
    }

    private void beginCrop(Uri source, Uri destination) {
        Crop.of(source, destination).asSquare().withMaxSize(480, 480).start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Crop.getOutput(result));
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageOutput.delete();
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(imageOutput);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            btnPhoto.setImageBitmap(bitmap);
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
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


    @Override
    public void onServiceConnected(ComponentName compName, IBinder iBinder) {
        super.onServiceConnected(compName, iBinder);
        if (binder.isConnected() && !photoFile.exists()) {
            try {
                photoFile = binder.loadAvatar(dbhelper.getMyContact());
                if (photoFile.exists()) {
                    btnPhoto.setImageURI(Uri.fromFile(photoFile));
                }
            } catch (XMPPException e) {
               /* Log.e(getClass().getSimpleName(),
                        "Failed loading avatar: " + e.getMessage(), e);*/
            } catch (SmackException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(txtStatus, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(txtStatus);
    }

    class PhotoUploader extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {

                if (imageOutput != null) {
                    try {
                        binder.updateProfile(contact.getJabberId(), imageOutput);
                    } catch (SmackException e) {
                        e.printStackTrace();
                    }
                }
                Contact c = dbhelper.getMyContact();
                String status = txtStatus.getText().toString();
                dbhelper.updateData(c);

                if (!c.getStatus().equals(status)) {
                    try {
                        binder.updatePresence(Presence.Type.available, status);
                        c.setStatus(status);
                        dbhelper.updateData(c);
                    } catch (SmackException e) {
                        e.printStackTrace();
                    }
                }

                publishProgress();
            } catch (XMPPException e) {
                Log.e(getClass().getSimpleName(),
                        "Error updating profile:" + e.getMessage(), e);
                pdialog.dismiss();
                showErrorDialog();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            startMainActivity();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (imageOutput != null)
                imageOutput.delete();
            pdialog.dismiss();
        }
    }


    private void showErrorDialog() {
        AlertDialog.Builder builder = DialogUtil.generateAlertDialog(this,
                "Profile", "Failed updating profile. Please try again later.");
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    @Override
    public void onClick(View arg0) {

        if (imageOutput != null) {
            FileInputStream fis = null;
            try {
                String profilePicName = MediaProcessingUtil
                        .getProfilePicName(dbhelper.getMyContact());
                FileOutputStream fos = openFileOutput(profilePicName,
                        MODE_PRIVATE);
                fos.close();

                File f = getFileStreamPath(profilePicName);
                MediaProcessingUtil.getResizedImage(imageOutput,
                        f.getAbsolutePath(), 480, 480);
                byte[] pp = new byte[(int) f.length()];
                fis = new FileInputStream(f);
                fis.read(pp);

                Intent intent = new Intent(UpdateProfileActivity.UPDATE_PROFILE).putExtra("image", imageOutput);
                sendBroadcast(intent);
                dbhelper.getMyContact().setChangeProfile(1);

            } catch (IOException ioe) {
            } finally {
                try {
                    fis.close();
                } catch (IOException e) {
                }
            }

            IntervalDB db = new IntervalDB(getApplicationContext());
            db.open();
            Cursor cursorSelect = db.getSingleContact(13);
            if (cursorSelect.getCount() > 0) {
                db.updateContact(13, imageOutput.getAbsolutePath());
            } else {
                Interval interval = new Interval();
                interval.setId(13);
                interval.setTime(imageOutput.getAbsolutePath());
                db.createContact(interval);
            }

            db.close();
            Contact c = dbhelper.getMyContact();
            String status = txtStatus.getText().toString();
            c.setStatus(status);

            String name;
            if (c.getRealname() == null || c.getRealname().equalsIgnoreCase("")) {
                name = c.getJabberId();
            } else {
                name = c.getRealname();
            }

            TimeLine timeLine = new TimeLine(c.getJabberId(), toJson("semua", c.getStatus()), new Date(), name, "0");
            TimeLineDB timeLineDB = TimeLineDB.getInstance(getApplicationContext());
            timeLineDB.insert(timeLine);

            dbhelper.updateData(c);

            if (binder.isConnected()) {
                Intent intent = new Intent(this, UploadProfileService.class);
                startService(intent);
            }

        } else {
            IntervalDB db = new IntervalDB(getApplicationContext());
            db.open();
            Cursor cursorSelect = db.getSingleContact(13);
            if (cursorSelect.getCount() > 0) {
                if (cursorSelect.getString(cursorSelect.getColumnIndexOrThrow(IntervalDB.COL_TIME)).equalsIgnoreCase("null")) {
                    db.updateContact(13, "null");
                }
            } else {
                Interval interval = new Interval();
                interval.setId(13);
                interval.setTime("null");
                db.createContact(interval);
            }

            db.close();
            Contact c = dbhelper.getMyContact();
            String status = txtStatus.getText().toString();
            c.setStatus(status);

            String name;
            if (c.getRealname() == null || c.getRealname().equalsIgnoreCase("")) {
                name = c.getJabberId();
            } else {
                name = c.getRealname();
            }

            TimeLine timeLine = new TimeLine(c.getJabberId(), toJson("status", c.getStatus()), new Date(), name, "0");
            TimeLineDB timeLineDB = TimeLineDB.getInstance(getApplicationContext());
            timeLineDB.insert(timeLine);

            dbhelper.updateData(c);

            if (binder.isConnected()) {
                Intent intent = new Intent(this, UploadProfileService.class);
                startService(intent);
            }
        }

        startMainActivity();

    }


    class requestDeleteFoto extends AsyncTask<String, Void, String> {

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();

        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content = null;
        private boolean error = false;
        private Context mContext;
        String code = "400";
        String code_text = "";
        String desc = "";
        Boolean max = false;


        public requestDeleteFoto(Context context) {
            this.mContext = context;

        }


        @Override
        protected void onPreExecute() {
            pdialog.show();
        }

        protected String doInBackground(String... key) {
            try {

                HttpClient httpClient = HttpHelper
                        .createHttpClient(getApplicationContext());
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);

                nameValuePairs.add(new BasicNameValuePair("username", contact.getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("key", key[0]));

                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                HttpPost post = new HttpPost(FILE_DELETE_FOTO);
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
                    JSONObject result = new JSONObject(content);
                    code = result.getString("code");
                    code_text = result.getString("code_text");
                    desc = result.getString("description");
                    if (!code.equalsIgnoreCase("200")) error = true;
                } else {
                    //Closes the connection.
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
            pdialog.dismiss();
        }

        protected void onPostExecute(String content) {
            pdialog.dismiss();
            if (error) {
                pdialog.dismiss();
                if (content.contains("invalid_key")) {
                    if (NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)) {

                        String key = new ValidationsKey().getInstance(mContext).key(true);
                        if (key.equalsIgnoreCase("null")) {
                            Toast.makeText(mContext, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                            pdialog.dismiss();
                        } else {
                            new requestDeleteFoto(mContext).execute(key);
                        }
                    } else {
                        Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, desc, Toast.LENGTH_SHORT).show();
                }
            } else {
                if (code.equalsIgnoreCase("200")) {
                    if (!binder.isConnected()) {
                        showErrorDialog();
                        return;
                    }
                    File a = getFileStreamPath(MediaProcessingUtil
                            .getProfilePicName(contact));
                    if (a.exists()) a.delete();
                    new PhotoUploader().execute();
                } else {
                    Toast.makeText(getApplicationContext(), desc, Toast.LENGTH_SHORT).show();
                }
            }

        }

    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (emojicons.getVisibility() == View.GONE) {
         /*       Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("from","0" );
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);*/
                finish();
            } else {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                txtStatus.setFocusableInTouchMode(true);
                txtStatus.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(txtStatus, InputMethodManager.SHOW_IMPLICIT);
                emojicons.setVisibility(View.GONE);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public static String toJson(String action, String status) {
        try {
            JSONObject parent = new JSONObject();
            parent.put("action", action);
            parent.put("status", status);

            return parent.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}