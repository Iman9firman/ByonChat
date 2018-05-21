package com.byonchat.android.list;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.list.utilLoadImage.ImageLoader;
import com.byonchat.android.provider.Group;
import com.byonchat.android.provider.Message;
import com.byonchat.android.utils.MediaProcessingUtil;
import com.byonchat.android.utils.Utility;
import com.byonchat.android.widget.BadgeView;

import java.io.File;
import java.net.URLDecoder;
import java.util.List;

public class IconAdapter extends ArrayAdapter<IconItem> {
    public ImageLoader imageLoader;
    public static final int TYPE_DATE = 0;
    public static final int TYPE_CHECKBOX = 1;
    public static final int TYPE_IMAGE = 2;

    private int listType = 0;
    private ItemClickListener clickListener = null;

    public IconAdapter(Context context, int resource, int textViewResourceId,
            List<IconItem> objects) {
        super(context, resource, textViewResourceId, objects);
        imageLoader=new ImageLoader(getContext());
    }

    public void setListType(int listType) {
        this.listType = listType;
    }

    public int getListType() {
        return listType;
    }

    public void setClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = super.getView(position, convertView, parent);
        IconViewHolder holder = (IconViewHolder) row.getTag();
        if (holder == null) {
            switch (listType) {
            case TYPE_CHECKBOX:
                holder = new IconViewWithCheckHolder(row);
                break;

            case TYPE_IMAGE:
                holder = new IconViewWithImageHolder(row);
                break;

            default:
                holder = new IconViewWithDateHolder(row);

                break;
            }

            row.setTag(holder);
        }

        final IconItem item = getItem(position);
        String title = item.getTitle();

        if (item.getImageUri() != null) {
            File photoFile = getContext().getFileStreamPath(MediaProcessingUtil
                    .getProfilePic(item.getJabberId()));

            if (photoFile.exists()) {
                Bitmap asd = BitmapFactory.decodeFile(String.valueOf(photoFile));
                holder.iconView.setImageBitmap(MediaProcessingUtil.getRoundedCornerBitmap(asd,10));
            }

        } else {
            if (item.getChatParty() instanceof Group) {
                holder.iconView.setImageResource(R.drawable.ic_group);
            } else {
                Bitmap icon = BitmapFactory.decodeResource(getContext().getResources(),
                        R.drawable.ic_no_photo);
                holder.iconView.setImageBitmap(MediaProcessingUtil.getRoundedCornerBitmap(icon,10));

                String regex = "[0-9]+";
                if(!item.getJabberId().matches(regex)){
                    title = Utility.roomName(getContext(),item.getTitle(),true);
                    imageLoader.DisplayImage("https://"+ MessengerConnectionService.HTTP_SERVER+"/mediafiles/"+item.getJabberId()+"_thumb.png",  holder.iconView);
                }
            }
        }
        holder.textTitle.setText(Html.fromHtml(title));
        String text = Html.fromHtml(item.getInfo() != null ? URLDecoder.decode(item.getInfo()):"").toString();

        if(text.contains("<")) {
            text = Html.fromHtml(Html.fromHtml(text).toString()).toString();
        }
        holder.textInfo.setText(Message.parsedMessageText(text));
        if (item.getValue()!=null){
            if(item.getValue().equals(Message.TYPE_BROADCAST)){
                holder.textInfo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_broadcasts, 0, 0, 0);
            }else if(item.getValue().equals(Message.TYPE_VIDEO)){
                holder.textInfo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_video, 0, 0, 0);
            }else if(item.getValue().equals(Message.TYPE_IMAGE)){
                holder.textInfo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_camera, 0, 0, 0);
            }else if(item.getValue().equals(Message.STATUS_TYPE_RECEIVE)){
               // holder.textInfo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play, 0, 0, 0);
            }else if(item.getValue().equals(Message.STATUS_TYPE_DELIVER)){
                holder.textInfo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_delivered, 0, 0, 0);
            }else if(item.getValue().equals(Message.STATUS_TYPE_FAILED)){
                holder.textInfo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_failed, 0, 0, 0);
            }else if(item.getValue().equals(Message.STATUS_TYPE_INPROSES)){
                holder.textInfo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_pending, 0, 0, 0);
            }else if(item.getValue().equals(Message.STATUS_TYPE_SEND)){
                holder.textInfo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_sent, 0, 0, 0);
            }else {
                holder.textInfo.setCompoundDrawables(null,null,null,null);
            }
        }else{
            holder.textInfo.setCompoundDrawables(null,null,null,null);
        }
        holder.badge.setBadgeBackgroundColor(Color.RED);
        holder.badge.setTextColor(Color.WHITE);
        holder.badge.setText(String.valueOf(item.getUnread()));

        if(item.getUnread()>0){
            holder.badge.show();
        }else{
            holder.badge.hide();
        }

        switch (listType) {
        case TYPE_CHECKBOX:

            if (clickListener != null) {
                IconViewWithCheckHolder cHolder = (IconViewWithCheckHolder) holder;
                cHolder.checkBox.setChecked(false);
                if (item.getValue() != null) {
                    cHolder.checkBox.setChecked(true);
                }
                cHolder.checkBox.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickListener.onClick(v, item);
                    }
                });
            }
            break;

        case TYPE_IMAGE:
            if (clickListener != null) {
                IconViewWithImageHolder iHolder = (IconViewWithImageHolder) holder;
                iHolder.imageAction.setVisibility(View.VISIBLE);
                if (item.getImageAction() < 0) {
                    iHolder.imageAction.setVisibility(View.INVISIBLE);
                } else if (item.getImageAction() > 0) {
                    iHolder.imageAction.setImageResource(item.getImageAction());
                }
                iHolder.imageAction.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickListener.onClick(v, item);
                    }
                });

            }
            break;

        default:
            IconViewWithDateHolder dHolder = (IconViewWithDateHolder) holder;

            dHolder.dateInfo.setVisibility(View.INVISIBLE);

            if (item.getDateInfo() != null) {
                dHolder.dateInfo.setVisibility(View.VISIBLE);
                dHolder.dateInfo.setText(item.getDateInfo());

            }


            break;

        }

        return row;

    }



    class IconViewHolder {
        private ImageView iconView;
        private TextView textInfo;
        private TextView textTitle;
        private BadgeView badge;
        private ImageButton roomsOpen;
        public IconViewHolder(View row) {
            iconView = (ImageView) row.findViewById(R.id.imagePhoto);
            textInfo = (TextView) row.findViewById(R.id.textInfo);
            textTitle = (TextView) row.findViewById(R.id.textTitle);
            roomsOpen = (ImageButton) row.findViewById(R.id.roomsOpen);
            badge = new BadgeView(getContext(), textInfo);

        }
    }

    class IconViewWithDateHolder extends IconViewHolder {
        private TextView dateInfo;
        public IconViewWithDateHolder(View row) {
            super(row);
            dateInfo = (TextView) row.findViewById(R.id.dateInfo);
        }
    }

    class IconViewWithCheckHolder extends IconViewHolder {
        private CheckBox checkBox;

        public IconViewWithCheckHolder(View row) {
            super(row);
            checkBox = (CheckBox) row.findViewById(R.id.checkBox);
        }
    }

    class IconViewWithImageHolder extends IconViewHolder {
        private ImageView imageAction;

        public IconViewWithImageHolder(View row) {
            super(row);
            imageAction = (ImageView) row.findViewById(R.id.imageAction);
        }
    }

    public interface ItemClickListener {
        public void onClick(View v, IconItem item);
    }


    private File resizeImage(File fileToResized, boolean deleteSource) {
        File f = MediaProcessingUtil.getOutputFile(MediaProcessingUtil
                .getFileExtension(fileToResized.getName()));
        f = MediaProcessingUtil.getResizedImage(fileToResized,
                f.getAbsolutePath());
        MediaProcessingUtil.saveFileToGallery(getContext(), Uri.fromFile(f));
        if (deleteSource) {
            fileToResized.delete();
        }

        return f;
    }


}
