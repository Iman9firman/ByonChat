package com.honda.android.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;

import com.honda.android.ConversationGalleryActivity;
import com.honda.android.DownloadFileByonchat;
import com.honda.android.provider.Message;

import java.io.File;

public class GalleryDisplayHandler implements OnClickListener {
    private String fileName;
    private String jabberId;
    private String type;
    private Activity activity;

    public GalleryDisplayHandler(Activity activity, String jabberId,
                                 String fname, String type) {
        fileName = fname;
        this.activity = activity;
        this.jabberId = jabberId;
        this.type = type;
    }

    @Override
    public void onClick(View view) {
        if (type.equalsIgnoreCase(Message.TYPE_IMAGE)) {
            Intent intent = new Intent(activity, ConversationGalleryActivity.class);
            intent.putExtra(ConversationGalleryActivity.KEY_SELECTED_FILE, fileName);
            intent.putExtra(ConversationGalleryActivity.KEY_JABBER_ID, jabberId);
            intent.putExtra(ConversationGalleryActivity.KEY_TITLE,
                    activity.getTitle());
            intent.putExtra(ConversationGalleryActivity.KEY_DISPLAY_TYPE,
                    ConversationGalleryActivity.TYPE_PROFILE_PICTURE);
            activity.startActivity(intent);
        } else {
            try {

                File oldFile = new File(fileName);

                if (oldFile.exists()) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Uri uri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", oldFile);
                        intent.setData(uri);
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        activity.startActivity(intent);

                    } else {

                        MimeTypeMap map = MimeTypeMap.getSingleton();
                        String ext = MimeTypeMap.getFileExtensionFromUrl(oldFile.getName());
                        String type = map.getMimeTypeFromExtension(ext);
                        if (type == null)
                            type = "video/*";
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri data = Uri.fromFile(oldFile);
                        intent.setDataAndType(data, type);
                        activity.startActivity(intent);
                    }

                } else {
                    Log.e("Error1", "sarti");
                }
          /*  Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse("file://"+fileName), "video/*");
            activity.startActivity(intent);*/
            } catch (Exception e) {
                Log.e("Error2", e.getMessage());
            }
        }


    }

}
  /*  Intent intent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                    intent.setDataAndType(Uri.parse("file://" + fileName), "video/*");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    } else {
//            intent.setDataAndType(FileProvider.getUriForFile(getActivity(), Solo.getProviderAuthorities(), file), mimeType);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    try {
                    activity.startActivity(intent);
                    } catch (Exception e) {
                    Toast.makeText(activity, "No handler for this type of file.", Toast.LENGTH_SHORT).show();
                    }*/