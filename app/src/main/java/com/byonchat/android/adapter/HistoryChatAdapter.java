package com.byonchat.android.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.signature.StringSignature;
import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.list.IconItem;
import com.byonchat.android.list.utilLoadImage.TextLoader;
import com.byonchat.android.provider.Message;
import com.byonchat.android.utils.MediaProcessingUtil;
import com.byonchat.android.widget.BadgeView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by imanfirmansyah on 4/21/17.
 */

public class HistoryChatAdapter extends RecyclerView.Adapter<HistoryChatAdapter.MyViewHolder> {

    private ArrayList<IconItem> contactList;
    private SparseBooleanArray mSelectedItemsIds;
    private ContactAdapterListener listener;
    Context context;
    TextLoader textLoader;


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        public TextView textTitle, textInfo, dateInfo;
        RelativeLayout messageContainer;
        ImageView imagePhoto;
        BadgeView badge;


        public MyViewHolder(View view) {
            super(view);
            messageContainer = (RelativeLayout) view.findViewById(R.id.message_container);
            textTitle = (TextView) view.findViewById(R.id.textTitle);
            textInfo = (TextView) view.findViewById(R.id.textInfo);
            dateInfo = (TextView) view.findViewById(R.id.dateInfo);
            imagePhoto = (ImageView) view.findViewById(R.id.imagePhoto);
            badge = new BadgeView(context, textInfo);
            badge.setTextSize(12);
            view.setOnLongClickListener(this);
        }



        @Override
        public boolean onLongClick(View view) {
            listener.onRowLongClicked(getAdapterPosition());
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return false;
        }

    }

    public HistoryChatAdapter(Context mContext, ArrayList<IconItem> ctcList, ContactAdapterListener listen) {
        context = mContext;
        this.contactList = ctcList;
        this.listener = listen;
        textLoader = new TextLoader(context);
        mSelectedItemsIds = new SparseBooleanArray();
    }

    public int getSelectedItemCount() {
        return mSelectedItemsIds.size();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)  {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_history_chat_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final IconItem item = contactList.get(position);

        String regex = "[0-9]+";
        if (item.getJabberId().matches(regex)) {

            if (item.getTitle().matches(regex)) {
                holder.textTitle.setText("fetching id...");
                textLoader.DisplayImage(item.getTitle(), holder.textTitle);
            } else {
                holder.textTitle.setText(Html.fromHtml(item.getTitle()));
            }

            File photoFile = context.getFileStreamPath(MediaProcessingUtil
                    .getProfilePicName(item.getJabberId()));
            Drawable d = context.getResources().getDrawable(R.drawable.ic_no_photo);
            if (photoFile.exists()) {
                d = Drawable.createFromPath(photoFile.getAbsolutePath());
            }

            Glide.with(context).load("https://"+MessengerConnectionService.F_SERVER+"/toboldlygowherenoonehasgonebefore/"+item.getJabberId()+".jpg").asBitmap()
                    .centerCrop()
                    .signature(new StringSignature(item.getSignature()))
                    .placeholder(d)
                    .animate(R.anim.fade_in_sort)
                    .into(new BitmapImageViewTarget(holder.imagePhoto) {
                @Override
                protected void setResource(Bitmap resource) {
                    holder.imagePhoto.setImageBitmap(resource);
                    SaveMedia task = new SaveMedia();
                    task.execute(new MyTaskParams[] {new MyTaskParams(resource,item.getJabberId())});
                }
            });
        }else{
            holder.textTitle.setText("fetching room...");
            textLoader.DisplayImage(item.getTitle(), holder.textTitle);
            Glide.with(context).load("https://" + MessengerConnectionService.HTTP_SERVER + "/mediafiles/" + item.getJabberId() + "_thumb.png")
                    .asBitmap()
                    .animate(R.anim.fade_in_sort)
                    .placeholder(R.drawable.ic_room_door)
                    .into(new BitmapImageViewTarget(holder.imagePhoto) {
                @Override
                protected void setResource(Bitmap resource) {
                    holder.imagePhoto.setImageBitmap(resource);
                }
            });
        }

        holder.textInfo.setText(item.getInfo());
        holder.dateInfo.setText(item.getDateInfo());

        if (item.getValue() != null) {
            if (item.getValue().equals(Message.TYPE_BROADCAST)) {
                holder.textInfo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_broadcasts, 0, 0, 0);
                holder.textInfo.setCompoundDrawablePadding(5);
            } else if (item.getValue().equals(Message.TYPE_VIDEO)) {
                holder.textInfo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_video, 0, 0, 0);
                holder.textInfo.setCompoundDrawablePadding(5);
            } else if (item.getValue().equals(Message.TYPE_IMAGE)) {
                holder.textInfo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_camera, 0, 0, 0);
                holder.textInfo.setCompoundDrawablePadding(5);
            } else if (item.getValue().equals(Message.STATUS_TYPE_RECEIVE)) {
                holder.textInfo.setCompoundDrawables(null, null, null, null);
            } else if (item.getValue().equals(Message.STATUS_TYPE_DELIVER)) {
                holder.textInfo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_delivered, 0, 0, 0);
                holder.textInfo.setCompoundDrawablePadding(5);
            } else if (item.getValue().equals(Message.STATUS_TYPE_READ)) {
                holder.textInfo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_read, 0, 0, 0);
                holder.textInfo.setCompoundDrawablePadding(5);
            } else if (item.getValue().equals(Message.STATUS_TYPE_FAILED)) {
                holder.textInfo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_failed, 0, 0, 0);
                holder.textInfo.setCompoundDrawablePadding(5);
            } else if (item.getValue().equals(Message.STATUS_TYPE_INPROSES)) {
                holder.textInfo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_pending, 0, 0, 0);
                holder.textInfo.setCompoundDrawablePadding(5);
            } else if (item.getValue().equals(Message.STATUS_TYPE_SEND)) {
                holder.textInfo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_sent, 0, 0, 0);
                holder.textInfo.setCompoundDrawablePadding(5);
            } else {
                holder.textInfo.setCompoundDrawables(null, null, null, null);
            }
        } else {
            holder.textInfo.setCompoundDrawables(null, null, null, null);
        }

        holder.badge.setBadgeBackgroundColor(Color.RED);
        holder.badge.setTextColor(Color.WHITE);
        holder.badge.setText(String.valueOf(item.getUnread()));
        if (item.getUnread() > 0) {
            holder.badge.show();
        } else {
            holder.badge.hide();
        }

        applyClickEvents(holder, position);
        applyIconAnimation(holder, position);
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    /***
     * Methods required for do selections, remove selections, etc.
     */

    //Toggle selection methods
    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }


    //Remove selected selections
    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }


    //Put or delete selected position into SparseBooleanArray
    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);

        notifyDataSetChanged();
    }

    //Get total selected count
    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    //Return all selected ids
    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    public void clearSelections() {
        mSelectedItemsIds.clear();
        notifyDataSetChanged();
    }

    public interface ContactAdapterListener {
        void onMessageRowClicked(int position,View view);
        void onRowLongClicked(int position);
    }

    private void applyIconAnimation(MyViewHolder holder, int position) {
        if (mSelectedItemsIds.get(position, false)) {
            holder.messageContainer.setBackgroundColor(context.getResources().getColor(R.color.grayOffTab));
        } else {
            TypedValue outValue = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            holder.messageContainer.setBackgroundResource(outValue.resourceId);
        }
    }

    private void applyClickEvents(final MyViewHolder holder, final int position) {
        holder.messageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageRowClicked(position,holder.itemView);
            }
        });

        holder.messageContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onRowLongClicked(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            }
        });
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<>(mSelectedItemsIds.size());
        for (int i = 0; i < mSelectedItemsIds.size(); i++) {
            items.add(mSelectedItemsIds.keyAt(i));
        }
        return items;
    }

    public void removeData(int position) {
        contactList.remove(position);
    }


    private class SaveMedia extends AsyncTask<MyTaskParams, Void, MyTaskParams> {
        @Override
        protected MyTaskParams doInBackground(MyTaskParams... resource) {
            MediaProcessingUtil.saveProfilePic(context, resource[0].getContact(), resource[0].getBar());
            return null;
        }
    }

    private static class MyTaskParams {
        Bitmap bar;
        String contact;

        MyTaskParams(Bitmap bar, String  contact) {
            this.bar = bar;
            this.contact = contact;
        }

        public Bitmap getBar() {
            return bar;
        }

        public void setBar(Bitmap bar) {
            this.bar = bar;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }
    }
}
