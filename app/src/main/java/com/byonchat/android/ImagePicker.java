package com.byonchat.android;

import android.app.Activity;
import android.content.Intent;
import androidx.fragment.app.Fragment;

import com.byonchat.android.helpers.Constants;
import com.byonchat.android.model.Image;

import java.util.ArrayList;

/**
 * Created by byonc on 4/18/2017.
 */

public abstract class ImagePicker {

    private int mode;
    private int limit;
    private boolean showCamera;
    private String imageTitle;
    private ArrayList<Image> selectedImages;
    private boolean folderMode;
    private String imageDirectory;
    private String destination_id;
    private boolean reset;

    public abstract void start(int requestCode);

    public static class ImagePickerWithActivity extends ImagePicker {

        private Activity activity;

        public ImagePickerWithActivity(Activity activity) {
            this.activity = activity;
            init(activity);
        }

        @Override
        public void start(int requestCode) {
            Intent intent = getIntent(activity);
            activity.startActivityForResult(intent, requestCode);
        }
    }

    public static class ImagePickerWithFragment extends ImagePicker {

        private Fragment fragment;

        public ImagePickerWithFragment(Fragment fragment) {
            this.fragment = fragment;
            init(fragment.getActivity());
        }

        @Override
        public void start(int requestCode) {
            Intent intent = getIntent(fragment.getActivity());
            fragment.startActivityForResult(intent, requestCode);
        }
    }


    public void init(Activity activity) {
        this.mode = ImagePickerActivity.MODE_MULTIPLE;
        this.limit = Constants.MAX_LIMIT;
        this.showCamera = true;
        this.imageTitle = activity.getString(R.string.title_select_image);
        this.selectedImages = new ArrayList<>();
        this.folderMode = false;
        this.imageDirectory = activity.getString(R.string.image_directory);
    }


    public static ImagePickerWithActivity create(Activity activity) {
        return new ImagePickerWithActivity(activity);
    }

    public static ImagePickerWithFragment create(Fragment fragment) {
        return new ImagePickerWithFragment(fragment);
    }

    public ImagePicker single() {
        mode = ImagePickerActivity.MODE_SINGLE;
        return this;
    }

    public ImagePicker destination(String id) {
        this.destination_id = id;
        return this;
    }

    public ImagePicker multi() {
        mode = ImagePickerActivity.MODE_MULTIPLE;
        return this;
    }


    public ImagePicker limit(int count) {
        limit = count;
        return this;
    }

    public ImagePicker showCamera(boolean show) {
        showCamera = show;
        return this;
    }

    public ImagePicker imageTitle(String title) {
        this.imageTitle = title;
        return this;
    }

    public ImagePicker origin(ArrayList<Image> images) {
        selectedImages = images;
        return this;
    }

    public ImagePicker folderMode(boolean folderMode) {
        this.folderMode = folderMode;
        return this;
    }

    public ImagePicker imageDirectory(String directory) {
        this.imageDirectory = directory;
        return this;
    }

    public ImagePicker reset(boolean reset){
        this.reset = reset;
        return this;
    }

    public Intent getIntent(Activity activity) {
        Intent intent = new Intent(activity, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_MODE, mode);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_LIMIT, limit);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_SHOW_CAMERA, showCamera);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_IMAGE_TITLE, imageTitle);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_DESTINATION, destination_id);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES, selectedImages);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_FOLDER_MODE, folderMode);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_IMAGE_DIRECTORY, imageDirectory);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_RESET, reset);
        return intent;
    }


}
