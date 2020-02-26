package com.byonchat.android.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;

import com.byonchat.android.local.Byonchat;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.byonchat.android.helpers.Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
import static com.byonchat.android.utils.ByonchatFileUtil.FILES_PATH;

public class FilesDirectoryUtil {

    public static boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Activity activity) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission((Activity) activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) activity,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", (Activity) activity,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) activity,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public static void showDialog(final String msg, final Context context,
                                  final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                (dialogInterface, i) -> {
                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{permission},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    public ArrayList<String> getMediaFromDirectory() {
        HashSet<String> videoItemHashSet = new HashSet<>();
//        String path = Environment.getExternalStorageDirectory().toString() + "/BCVideoTube";
        String path = FILES_PATH;
        File directory = new File(path);
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {
            videoItemHashSet.add(files[i].getName());
        }
        ArrayList<String> downloadedList = new ArrayList<>(videoItemHashSet);
        return downloadedList;
    }

    public static ArrayList<String> getOnlyVideoFromDirectory(Activity activity) {
        HashSet<String> videoItemHashSet = new HashSet<>();
        String[] projection = {MediaStore.Video.VideoColumns.DATA, MediaStore.Video.Media.DISPLAY_NAME};
        String selection = MediaStore.Video.Media.DATA + " like?";
        String[] selectionArgs = new String[]{"%Byonchat%"};
        Cursor cursor = activity.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, MediaStore.Video.Media.DATE_MODIFIED);
        try {
            cursor.moveToFirst();
            do {
                videoItemHashSet.add((cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))));
            } while (cursor.moveToNext());

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<String> downloadedList = new ArrayList<>(videoItemHashSet);
        return downloadedList;
    }

    public static String getSizeLengthFile(long size) {

        DecimalFormat df = new DecimalFormat("0");

        float sizeKb = 1024;
        float sizeMo = sizeKb * sizeKb;
        float sizeGo = sizeMo * sizeKb;
        float sizeTerra = sizeGo * sizeKb;

        if (size < sizeMo)
            return df.format(size / sizeKb) + " KB";
        else if (size < sizeGo)
            return df.format(size / sizeMo) + " MB";
        else if (size < sizeTerra)
            return df.format(size / sizeGo) + " GB";

        return "";
    }

    public static String getDuration(File localPath) {
        MediaPlayer mp = MediaPlayer.create(Byonchat.getApps(), Uri.fromFile(localPath));
        int duration = mp.getDuration();
        mp.release();
        return String.format(Locale.getDefault(), "%d:%d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        );
    }

    public static boolean checkFileExist(String filename) {
//        File file = new File(ByonchatFileUtil.generateFilePath(filename));
        File file = new File(Environment.getExternalStorageDirectory().getPath(),
                FILES_PATH);
        String directory = file.getAbsolutePath() + File.separator;
        File f = new File(directory + File.separator + filename);
        Log.w("sebelumunduh", f.getAbsolutePath());
//        File file = new File(localPath);
        if (f.exists()) {
            return true;
        }
        return false;
    }
}
