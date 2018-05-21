package com.byonchat.android.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.byonchat.android.ConversationGalleryActivity;
import com.byonchat.android.provider.Message;

public class GalleryDisplayHandler implements OnClickListener {
    private String fileName;
    private String jabberId;
    private String type;
    private Activity activity;

    public GalleryDisplayHandler(Activity activity, String jabberId,
            String fname,String type) {
        fileName = fname;
        this.activity = activity;
        this.jabberId = jabberId;
        this.type = type;
    }

    @Override
    public void onClick(View view) {
        if(type.equalsIgnoreCase(Message.TYPE_IMAGE)){
            Intent intent = new Intent(activity, ConversationGalleryActivity.class);
            intent.putExtra(ConversationGalleryActivity.KEY_SELECTED_FILE, fileName);
            intent.putExtra(ConversationGalleryActivity.KEY_JABBER_ID, jabberId);
            intent.putExtra(ConversationGalleryActivity.KEY_TITLE,
                    activity.getTitle());
            intent.putExtra(ConversationGalleryActivity.KEY_DISPLAY_TYPE,
                    ConversationGalleryActivity.TYPE_PROFILE_PICTURE);
            activity.startActivity(intent);
        }else{
            try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse("file://"+fileName), "video/*");
            activity.startActivity(intent);
            }
            catch(Exception e)
            {
            Log.e("", e.getMessage());
            }
        }


    }

}
