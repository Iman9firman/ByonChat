package com.honda.android;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.Process;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.honda.android.adapter.FolderPickerAdapter;
import com.honda.android.adapter.ImagePickerAdapter;
import com.honda.android.createMeme.FilteringImage;
import com.honda.android.helpers.Constants;
import com.honda.android.helpers.ImageUtils;
import com.honda.android.listeners.OnFolderClickListener;
import com.honda.android.listeners.OnImageClickListener;
import com.honda.android.model.Folder;
import com.honda.android.model.Image;
import com.honda.android.provider.Contact;
import com.honda.android.provider.Group;
import com.honda.android.provider.MessengerDatabaseHelper;
import com.honda.android.utils.MediaProcessingUtil;
import com.honda.android.view.GridSpacingItemDecoration;
import com.honda.android.view.ProgressWheel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by byonc on 4/18/2017.
 */

public class ImagePickerActivity extends AppCompatActivity implements OnImageClickListener {

    private static final String TAG = "ImagePickerActivity";

    public static final int MODE_SINGLE = 1;
    public static final int MODE_MULTIPLE = 2;

    public static final String INTENT_EXTRA_SELECTED_IMAGES = "selectedImages";
    public static final String INTENT_EXTRA_LIMIT = "limit";
    public static final String INTENT_EXTRA_SHOW_CAMERA = "showCamera";
    public static final String INTENT_EXTRA_MODE = "mode";
    public static final String INTENT_EXTRA_FOLDER_MODE = "folderMode";
    public static final String INTENT_EXTRA_DESTINATION = "destination_id";
    public static final String INTENT_EXTRA_IMAGE_TITLE = "imageTitle";
    public static final String INTENT_EXTRA_IMAGE_DIRECTORY = "imageDirectory";
    public static final String INTENT_EXTRA_RESET = "reset";

    private List<Folder> folders;
    private ArrayList<Image> images;
    private String currentImagePath;
    private String imageDirectory;

    private ArrayList<Image> selectedImages;
    private boolean showCamera;
    private int mode;
    private boolean folderMode = true;
    private int limit;
    private String folderTitle, imageTitle;

    private ActionBar actionBar;

    private MenuItem menuDone, menuCamera;
    private final int menuDoneId = 100;
    private final int menuCameraId = 101;

    private RelativeLayout mainLayout;
    private ProgressWheel progressBar;
    private TextView emptyTextView;
    private RecyclerView recyclerView;

    private GridLayoutManager layoutManager;
    private GridSpacingItemDecoration itemOffsetDecoration;

    private int imageColumns;
    private int folderColumns;

    private ImagePickerAdapter imageAdapter;
    private FolderPickerAdapter folderAdapter;

    private ContentObserver observer;
    private Handler handler;
    private Thread thread;
    Toolbar toolbar;

    private final String[] projection = new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

    private Parcelable foldersState;
    private String destination;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);

        Intent intent = getIntent();
        if (intent == null) {
            finish();
        }

        mainLayout = (RelativeLayout) findViewById(R.id.main);
        emptyTextView = (TextView) findViewById(R.id.tv_empty_images);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        Bitmap back_default = FilteringImage.headerColor(getWindow(), ImagePickerActivity.this, getResources().getColor(R.color.colorPrimary));
        Drawable back_draw_default = new BitmapDrawable(getResources(), back_default);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            toolbar.setBackground(back_draw_default);
        } else {
            toolbar.setBackgroundDrawable(back_draw_default);
        }

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
            actionBar.setDisplayShowTitleEnabled(true);
        }

        limit = intent.getIntExtra(ImagePickerActivity.INTENT_EXTRA_LIMIT, Constants.MAX_LIMIT);
        mode = intent.getIntExtra(ImagePickerActivity.INTENT_EXTRA_MODE, ImagePickerActivity.MODE_MULTIPLE);
        folderMode = intent.getBooleanExtra(ImagePickerActivity.INTENT_EXTRA_FOLDER_MODE, false);

        if (intent.hasExtra(INTENT_EXTRA_DESTINATION)) {
            folderTitle = getString(R.string.send_to) + " " + intent.getStringExtra(ImagePickerActivity.INTENT_EXTRA_DESTINATION);
            destination = intent.getStringExtra(ImagePickerActivity.INTENT_EXTRA_DESTINATION);
        }

        if (intent.hasExtra(INTENT_EXTRA_IMAGE_TITLE)) {
            imageTitle = intent.getStringExtra(ImagePickerActivity.INTENT_EXTRA_IMAGE_TITLE);
        } else {
            imageTitle = getString(R.string.title_select_image);
        }

        imageDirectory = intent.getStringExtra(ImagePickerActivity.INTENT_EXTRA_IMAGE_DIRECTORY);
        if (imageDirectory == null || TextUtils.isEmpty(imageDirectory)) {
            imageDirectory = getString(R.string.image_directory);
        }

        showCamera = intent.getBooleanExtra(ImagePickerActivity.INTENT_EXTRA_SHOW_CAMERA, true);
        if (mode == ImagePickerActivity.MODE_MULTIPLE && intent.hasExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES)) {
            selectedImages = intent.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
        }

        if (intent.getBooleanExtra(ImagePickerActivity.INTENT_EXTRA_RESET, true)) {
            selectedImages = new ArrayList<>();
        } else {
            if (selectedImages == null)
                selectedImages = new ArrayList<>();
        }
        images = new ArrayList<>();

        if (actionBar != null) {
            actionBar.setTitle(folderMode ? folderTitle : imageTitle);
        }

        imageAdapter = new ImagePickerAdapter(this, images, selectedImages, this);
        folderAdapter = new FolderPickerAdapter(this, new OnFolderClickListener() {
            @Override
            public void onFolderClick(Folder bucket) {
                foldersState = recyclerView.getLayoutManager().onSaveInstanceState();
                setImageAdapter(bucket.getImages());
            }
        });

        orientationBasedUI(getResources().getConfiguration().orientation);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataWithPermission();
    }

    private void setImageAdapter(ArrayList<Image> images) {
        imageAdapter.setData(images);
        setItemDecoration(imageColumns);
        recyclerView.setAdapter(imageAdapter);
        updateTitle();
    }

    private void setFolderAdapter() {
        folderAdapter.setData(folders);
        setItemDecoration(folderColumns);
        recyclerView.setAdapter(folderAdapter);

        if (foldersState != null) {
            layoutManager.setSpanCount(folderColumns);
            recyclerView.getLayoutManager().onRestoreInstanceState(foldersState);
        }
        updateTitle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (menu.findItem(menuCameraId) == null) {
            menuCamera = menu.add(Menu.NONE, menuCameraId, 1, getString(R.string.camera));
            menuCamera.setIcon(R.drawable.ic_camera_white);
            menuCamera.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menuCamera.setVisible(showCamera);
        }

        if (menu.findItem(menuDoneId) == null) {
            menuDone = menu.add(Menu.NONE, menuDoneId, 3, getString(R.string.done));
            menuDone.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }

        updateTitle();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (id == menuDoneId) {
            if (selectedImages != null && selectedImages.size() > 0) {

                /** Scan selected images which not existed */
                for (int i = 0; i < selectedImages.size(); i++) {
                    Image image = selectedImages.get(i);
                    File file = new File(image.getPath());
                    if (!file.exists()) {
                        selectedImages.remove(i);
                        i--;
                    }
                }

                Intent data = new Intent();
                data.putParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES, selectedImages);
                setResult(RESULT_OK, data);
                finish();
            }
            return true;
        }
        if (id == menuCameraId) {
            //captureImage();
            captureImageWithPermission();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        orientationBasedUI(newConfig.orientation);
    }

    private void orientationBasedUI(int orientation) {
        imageColumns = orientation == Configuration.ORIENTATION_PORTRAIT ? 3 : 5;
        folderColumns = orientation == Configuration.ORIENTATION_PORTRAIT ? 2 : 4;

        int columns = isDisplayingFolderView() ? folderColumns : imageColumns;
        layoutManager = new GridLayoutManager(this, columns);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        setItemDecoration(columns);
    }

    private void setItemDecoration(int columns) {
        layoutManager.setSpanCount(columns);
        if (itemOffsetDecoration != null)
            recyclerView.removeItemDecoration(itemOffsetDecoration);
        itemOffsetDecoration = new GridSpacingItemDecoration(columns, getResources().getDimensionPixelSize(R.dimen.item_padding), false);
        recyclerView.addItemDecoration(itemOffsetDecoration);
    }


    private void getDataWithPermission() {
        int rc = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (rc == PackageManager.PERMISSION_GRANTED)
            getData();
        else
            requestWriteExternalPermission();
    }

    private void getData() {
        abortLoading();

        ImageLoaderRunnable runnable = new ImageLoaderRunnable();
        thread = new Thread(runnable);
        thread.start();
    }

    private void requestWriteExternalPermission() {
        Log.w(TAG, "Write External permission is not granted. Requesting permission");

        final String[] permissions = new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, permissions, Constants.PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            if (!isPermissionRequested(Constants.PREF_WRITE_EXTERNAL_STORAGE_REQUESTED)) {
                ActivityCompat.requestPermissions(this, permissions, Constants.PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
                setPermissionRequested(Constants.PREF_WRITE_EXTERNAL_STORAGE_REQUESTED);
            } else {
                Snackbar snackbar = Snackbar.make(mainLayout, R.string.msg_no_write_external_permission,
                        Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openAppSettings();
                    }
                });
                snackbar.show();
            }
        }

    }


    private void requestCameraPermission() {
        Log.w(TAG, "Write External permission is not granted. Requesting permission");

        final String[] permissions = new String[]{android.Manifest.permission.CAMERA};

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, Constants.PERMISSION_REQUEST_CAMERA);
        } else {
            if (!isPermissionRequested(Constants.PREF_CAMERA_REQUESTED)) {
                ActivityCompat.requestPermissions(this, permissions, Constants.PERMISSION_REQUEST_CAMERA);
                setPermissionRequested(Constants.PREF_CAMERA_REQUESTED);
            } else {
                Snackbar snackbar = Snackbar.make(mainLayout, R.string.msg_no_camera_permission,
                        Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openAppSettings();
                    }
                });
                snackbar.show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case Constants.PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Write External permission granted");
                    getData();
                    return;
                }
                Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                        " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));
                finish();
            }
            case Constants.PERMISSION_REQUEST_CAMERA: {
                if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Camera permission granted");
                    captureImage();
                    return;
                }
                Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                        " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));
                break;
            }
            default: {
                Log.d(TAG, "Got unexpected permission result: " + requestCode);
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
            }
        }
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void setPermissionRequested(String permission) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(permission, true);
        editor.apply();
    }

    private boolean isPermissionRequested(String permission) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getBoolean(permission, false);
    }

    @Override
    public void onClick(View view, int position) {
        clickImage(position);
    }

    private void clickImage(int position) {
        int selectedItemPosition = selectedImagePosition(images.get(position));
        if (mode == ImagePickerActivity.MODE_MULTIPLE) {
            if (selectedItemPosition == -1) {
                if (selectedImages.size() < limit) {
                    imageAdapter.addSelected(images.get(position));
                } else {
//                    Toast.makeText(this, R.string.msg_limit_images, Toast.LENGTH_SHORT).show();
                }
            } else {
                imageAdapter.removeSelectedPosition(selectedItemPosition, position);
            }
        } else {
            if (selectedItemPosition != -1)
                imageAdapter.removeSelectedPosition(selectedItemPosition, position);
            else {
                if (selectedImages.size() > 0) {
                    imageAdapter.removeAllSelectedSingleClick();
                }
                imageAdapter.addSelected(images.get(position));
            }
        }
        updateTitle();
    }

    private int selectedImagePosition(Image image) {
        for (int i = 0; i < selectedImages.size(); i++) {
            if (selectedImages.get(i).getPath().equals(image.getPath())) {
                return i;
            }
        }

        return -1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_CAPTURE) {
            if (resultCode == RESULT_OK && currentImagePath != null) {
                Uri imageUri = Uri.parse(currentImagePath);
                /*if (imageUri != null) {
                    MediaScannerConnection.scanFile(this,
                            new String[]{imageUri.getPath()}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.v(TAG, "File " + path + " was scanned successfully: " + uri);
                                    getDataWithPermission();
                                }
                            });
                }*/
                Intent intent = new Intent(getApplicationContext(), ConfirmationSendFile.class);
                String jabberId = destination;
                intent.putExtra("file", imageUri.getPath());
                intent.putExtra("name", jabberId);
                intent.putExtra("type", com.honda.android.provider.Message.TYPE_IMAGE);
                startActivity(intent);
            }
        }
    }

    private void captureImageWithPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (rc == PackageManager.PERMISSION_GRANTED) {
                captureImage();
            } else {
                Log.w(TAG, "Camera permission is not granted. Requesting permission");
                requestCameraPermission();
            }
        } else {
            captureImage();
        }
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File imageFile = ImageUtils.createImageFile(imageDirectory);
            if (imageFile != null) {
                String authority = getPackageName() + ".fileprovider";
                Uri uri = FileProvider.getUriForFile(this, authority, imageFile);
                currentImagePath = "file:" + imageFile.getAbsolutePath();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, Constants.REQUEST_CODE_CAPTURE);
            } else {
                Toast.makeText(this, getString(R.string.error_create_image_file), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.error_no_camera), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Constants.FETCH_STARTED: {
                        showLoading();
                        break;
                    }
                    case Constants.FETCH_COMPLETED: {
                        ArrayList<Image> temps = new ArrayList<>();
                        temps.addAll(selectedImages);

                        ArrayList<Image> newImages = new ArrayList<>();
                        newImages.addAll(images);


                        if (folderMode) {
                            setFolderAdapter();
                            if (folders.size() != 0)
                                hideLoading();
                            else
                                showEmpty();

                        } else {
                            setImageAdapter(newImages);
                            if (images.size() != 0)
                                hideLoading();
                            else
                                showEmpty();
                        }

                        break;
                    }
                    default: {
                        super.handleMessage(msg);
                    }
                }
            }
        };
        observer = new ContentObserver(handler) {
            @Override
            public void onChange(boolean selfChange) {
                getData();
            }
        };
        getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, observer);
    }

    private void abortLoading() {
        if (thread == null)
            return;
        if (thread.isAlive()) {
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isDisplayingFolderView() {
        return (folderMode &&
                (recyclerView.getAdapter() == null || recyclerView.getAdapter() instanceof FolderPickerAdapter));
    }

    private void updateTitle() {
        if (menuDone != null && menuCamera != null) {
            if (isDisplayingFolderView()) {
                actionBar.setTitle(folderTitle);
                menuDone.setVisible(false);
            } else {
                if (selectedImages.size() == 0) {
                    actionBar.setTitle(imageTitle);
                    if (menuDone != null)
                        menuDone.setVisible(false);
                } else {
                    if (mode == ImagePickerActivity.MODE_MULTIPLE) {
                        if (limit == Constants.MAX_LIMIT)
                            actionBar.setTitle(String.format(getString(R.string.selected), selectedImages.size()));
                        else
                            actionBar.setTitle(String.format(getString(R.string.selected_with_limit), selectedImages.size(), limit));
                    }
                    if (menuDone != null)
                        menuDone.setVisible(true);
                }
            }
        }
    }

    private void showLoading() {
//        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyTextView.setVisibility(View.GONE);
    }

    private void hideLoading() {
//        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        emptyTextView.setVisibility(View.GONE);
    }

    private void showEmpty() {
//        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        emptyTextView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        abortLoading();

        getContentResolver().unregisterContentObserver(observer);

        observer = null;

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    private class ImageLoaderRunnable implements Runnable {

        @Override
        public void run() {
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

            Message message;
            if (recyclerView.getAdapter() == null) {
                message = handler.obtainMessage();
                message.what = Constants.FETCH_STARTED;
                message.sendToTarget();
            }

            if (Thread.interrupted()) {
                return;
            }

            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                    null, null, MediaStore.Images.Media.DATE_ADDED);

            if (cursor == null) {
                message = handler.obtainMessage();
                message.what = Constants.ERROR;
                message.sendToTarget();
                return;
            }

            ArrayList<Image> temp = new ArrayList<>(cursor.getCount());
            File file;
            folders = new ArrayList<>();

            if (cursor.moveToLast()) {
                do {
                    if (Thread.interrupted()) {
                        return;
                    }

                    long id = cursor.getLong(cursor.getColumnIndex(projection[0]));
                    String name = cursor.getString(cursor.getColumnIndex(projection[1]));
                    String path = cursor.getString(cursor.getColumnIndex(projection[2]));
                    String bucket = cursor.getString(cursor.getColumnIndex(projection[3]));

                    file = new File(path);
                    if (file.exists()) {
                        Image image = new Image(id, name, path, false);
                        temp.add(image);


                        if (folderMode) {
                            Folder folder = getFolder(bucket);
                            if (folder == null) {
                                folder = new Folder(bucket);
                                folders.add(folder);
                            }

                            folder.getImages().add(image);
                        }
                    }

                } while (cursor.moveToPrevious());
            }
            cursor.close();
            if (images == null) {
                images = new ArrayList<>();
            }
            images.clear();
            images.addAll(temp);

            if (handler != null) {
                message = handler.obtainMessage();
                message.what = Constants.FETCH_COMPLETED;
                message.sendToTarget();
            }

            Thread.interrupted();

        }
    }

    public Folder getFolder(String name) {
        for (Folder folder : folders) {
            if (folder.getFolderName().equals(name)) {
                return folder;
            }
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        if (folderMode && !isDisplayingFolderView()) {
            setFolderAdapter();
            return;
        }

        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
