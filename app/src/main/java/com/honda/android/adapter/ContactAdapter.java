package com.honda.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.honda.android.R;
import com.honda.android.communication.MessengerConnectionService;
import com.honda.android.communication.NetworkInternetConnectionStatus;
import com.honda.android.list.IconItem;
import com.honda.android.list.utilLoadImage.ImageLoaderFromSD;
import com.honda.android.list.utilLoadImage.TextLoader;
import com.honda.android.provider.Group;
import com.honda.android.provider.Message;
import com.honda.android.provider.TimeLineDB;
import com.honda.android.utils.MediaProcessingUtil;
import com.honda.android.widget.BadgeView;
import com.github.clans.fab.FloatingActionButton;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

/**
 * Created by Iman Firmansyah on 7/8/2015.
 */
public class ContactAdapter extends BaseAdapter implements Filterable {
    public TextLoader textLoader;
    Context context;
    ArrayList<IconItem> iconlist;
    ArrayList<IconItem> mStringFilterList;
    ValueFilter valueFilter;
    public ImageLoaderFromSD imageLoaderSD;
    TimeLineDB timeLineDB;
    boolean badgePicasso = false;

    public ContactAdapter(Context context, ArrayList<IconItem> iconlist) {
        imageLoaderSD = new ImageLoaderFromSD(context);
        timeLineDB = TimeLineDB.getInstance(context);
        this.context = context;
        this.iconlist = iconlist;
        mStringFilterList = iconlist;
        textLoader = new TextLoader(context);
    }

    @Override
    public int getCount() {
        return iconlist.size();
    }

    @Override
    public Object getItem(int position) {
        return iconlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return iconlist.indexOf(getItem(position));
    }

    public ArrayList<IconItem> newList() {
        ArrayList<IconItem> list = iconlist;
        return (list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = null;

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_item, parent, false);

            if (iconlist.size() > 0) {
                final IconItem item = iconlist.get(position);
                if (item.getJabberId().equalsIgnoreCase("")) {
                    convertView = mInflater.inflate(R.layout.list_item_invite, parent, false);
                    IconViewForInvite holder = (IconViewForInvite) convertView.getTag();
                    holder = new IconViewForInvite(convertView);
                    holder.title.setText("Invite Friends");
                } else {
                    IconViewHolder holder = (IconViewHolder) convertView.getTag();
                    holder = new IconViewWithDateHolder(convertView);

                    String title = item.getTitle();


                    if (item.getChatParty() instanceof Group) {
                        Picasso.with(context).load(R.drawable.ic_group).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(holder.iconView);
                        holder.textTitle.setText(Html.fromHtml(title));
                    } else {
                        String regex = "[0-9]+";
                        if (!item.getJabberId().matches(regex)) {
                            holder.textTitle.setText("fetching room...");
                            textLoader.DisplayImage(item.getTitle(), holder.textTitle);

                           /* if(NetworkInternetConnectionStatus.getInstance(context).isOnline(context)){
                                Picasso.with(context).invalidate("https://" + MessengerConnectionService.HTTP_SERVER + "/mediafiles/" + item.getJabberId() + "_thumb.png");

                                Picasso.with(context).load("https://" + MessengerConnectionService.HTTP_SERVER + "/mediafiles/" + item.getJabberId() + "_thumb.png")
                                        .error(R.drawable.ic_no_photo)
                                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                        .into(holder.iconView);
                            }else{

                            }*/
                            holder.iconView.setVisibility(View.GONE);
                            holder.imagePhotoFrame.setVisibility(View.VISIBLE);
                            holder.textTitleBiasa.setVisibility(View.GONE);
                            holder.textTitle.setVisibility(View.VISIBLE);
                            holder.textInfoBiasa.setVisibility(View.GONE);
                            holder.textInfo.setVisibility(View.VISIBLE);
                            badgePicasso = false;

                            Picasso.with(context).load("https://" + MessengerConnectionService.HTTP_SERVER + "/mediafiles/" + item.getJabberId() + "_thumb.png")
                                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                                    .error(R.drawable.ic_room_door)
                                    .into(holder.imagePhotoPicasso);

                            /*RelativeLayout.LayoutParams paramTitle = (RelativeLayout.LayoutParams) holder.textTitle.getLayoutParams();
                            paramTitle.addRule(RelativeLayout.RIGHT_OF, R.id.imagePhotoFrame);
                            paramTitle.addRule(RelativeLayout.LEFT_OF, R.id.dateInfo);
                            holder.textTitle.setLayoutParams(paramTitle);*/

                            /*RelativeLayout.LayoutParams llp = (RelativeLayout.LayoutParams) holder.textTitle.getLayoutParams();
                            llp.setMargins(0, 10, 0, 0); // llp.setMargins(left, top, right, bottom);
                            holder.textTitle.setLayoutParams(llp);*/

                        } else {
                            if (item.getTitle().matches(regex)) {
                                holder.textTitle.setText("fetching id...");
                                holder.textTitleBiasa.setText("fetching id...");
                                textLoader.DisplayImage(item.getTitle(), holder.textTitle);
                                textLoader.DisplayImage(item.getTitle(), holder.textTitleBiasa);
                            } else {
                                holder.textTitle.setText(Html.fromHtml(title));
                                holder.textTitleBiasa.setText(Html.fromHtml(title));
                            }

                            final IconViewHolder finalHolder = holder;

                            holder.iconView.setVisibility(View.VISIBLE);
                            holder.imagePhotoFrame.setVisibility(View.GONE);
                            holder.textTitleBiasa.setVisibility(View.VISIBLE);
                            holder.textTitle.setVisibility(View.GONE);
                            holder.textInfoBiasa.setVisibility(View.VISIBLE);
                            holder.textInfo.setVisibility(View.GONE);
                            badgePicasso = true;

                            Cursor c = timeLineDB.getDataByFlag();
                            if (c.getCount() > 0) {
                                if (c.getString(c.getColumnIndexOrThrow(TimeLineDB.TIMELINE_JID)).equalsIgnoreCase(item.getJabberId())) {
                                    imageLoaderSD.DeleteImage(MediaProcessingUtil
                                            .getProfilePic(item.getJabberId()), holder.iconView);

                                    Animation fadeOut = new AlphaAnimation(0, 1);
                                    fadeOut.setInterpolator(new AccelerateInterpolator());
                                    fadeOut.setDuration(500);
                                    holder.iconView.startAnimation(fadeOut);

                                    timeLineDB.updateUserFlag(item.getJabberId(), "0");
                                }
                            }
                            imageLoaderSD.DisplayImage(MediaProcessingUtil
                                    .getProfilePic(item.getJabberId()), holder.iconView, false);
                        }
                    }

                    String text = Html.fromHtml(item.getInfo()).toString();
                    holder.textInfo.setText(text);
                    holder.textInfoBiasa.setText(text);

                    if (item.getValue() != null) {
                        if (item.getValue().equals(Message.TYPE_BROADCAST)) {
                            holder.textInfo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_broadcasts, 0, 0, 0);
                            holder.textInfo.setCompoundDrawablePadding(5);
                            holder.textInfoBiasa.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_broadcasts, 0, 0, 0);
                            holder.textInfoBiasa.setCompoundDrawablePadding(5);
                        } else if (item.getValue().equals(Message.TYPE_VIDEO)) {
                            holder.textInfo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_video, 0, 0, 0);
                            holder.textInfo.setCompoundDrawablePadding(5);
                            holder.textInfoBiasa.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_video, 0, 0, 0);
                            holder.textInfoBiasa.setCompoundDrawablePadding(5);
                        } else if (item.getValue().equals(Message.TYPE_IMAGE)) {
                            holder.textInfo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_camera, 0, 0, 0);
                            holder.textInfo.setCompoundDrawablePadding(5);
                            holder.textInfoBiasa.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_camera, 0, 0, 0);
                            holder.textInfoBiasa.setCompoundDrawablePadding(5);
                        } else if (item.getValue().equals(Message.STATUS_TYPE_RECEIVE)) {
                            //      holder.textInfo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play, 0, 0, 0);
                        } else if (item.getValue().equals(Message.STATUS_TYPE_DELIVER)) {
                            holder.textInfo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_delivered, 0, 0, 0);
                            holder.textInfo.setCompoundDrawablePadding(5);
                            holder.textInfoBiasa.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_delivered, 0, 0, 0);
                            holder.textInfoBiasa.setCompoundDrawablePadding(5);
                        } else if (item.getValue().equals(Message.STATUS_TYPE_READ)) {
                            holder.textInfo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_read, 0, 0, 0);
                            holder.textInfo.setCompoundDrawablePadding(5);
                            holder.textInfoBiasa.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_read, 0, 0, 0);
                            holder.textInfoBiasa.setCompoundDrawablePadding(5);
                        } else if (item.getValue().equals(Message.STATUS_TYPE_FAILED)) {
                            holder.textInfo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_failed, 0, 0, 0);
                            holder.textInfo.setCompoundDrawablePadding(5);
                            holder.textInfoBiasa.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_failed, 0, 0, 0);
                            holder.textInfoBiasa.setCompoundDrawablePadding(5);
                        } else if (item.getValue().equals(Message.STATUS_TYPE_INPROSES)) {
                            holder.textInfo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_pending, 0, 0, 0);
                            holder.textInfo.setCompoundDrawablePadding(5);
                            holder.textInfoBiasa.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_pending, 0, 0, 0);
                            holder.textInfoBiasa.setCompoundDrawablePadding(5);
                        } else if (item.getValue().equals(Message.STATUS_TYPE_SEND)) {
                            holder.textInfo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_sent, 0, 0, 0);
                            holder.textInfo.setCompoundDrawablePadding(5);
                            holder.textInfoBiasa.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_sent, 0, 0, 0);
                            holder.textInfoBiasa.setCompoundDrawablePadding(5);
                        } else {
                            holder.textInfo.setCompoundDrawables(null, null, null, null);
                            holder.textInfoBiasa.setCompoundDrawables(null, null, null, null);
                        }
                    } else {
                        holder.textInfo.setCompoundDrawables(null, null, null, null);
                        holder.textInfoBiasa.setCompoundDrawables(null, null, null, null);
                    }

                    if (badgePicasso) {
                        holder.badgeBiasa.setBadgeBackgroundColor(Color.RED);
                        holder.badgeBiasa.setTextColor(Color.WHITE);
                        holder.badgeBiasa.setText(String.valueOf(item.getUnread()));
                        if (item.getUnread() > 0) {
                            holder.badgeBiasa.show();
                        } else {
                            holder.badgeBiasa.hide();
                        }
                    } else {
                        holder.badge.setBadgeBackgroundColor(Color.RED);
                        holder.badge.setTextColor(Color.WHITE);
                        holder.badge.setText(String.valueOf(item.getUnread()));
                        if (item.getUnread() > 0) {
                            holder.badge.show();
                        } else {
                            holder.badge.hide();
                        }
                    }

                    holder.roomsOpen.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                             /*blm material design
                              Intent intent = new Intent(context, WebViewByonActivity.class);
                                intent.putExtra(WebViewByonActivity.KEY_LINK_LOAD, "https://" + MessengerConnectionService.FILE_SERVER + "/personal_room/view/index.php?userid=" + item.getJabberId());
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);*/
                        }
                    });


                    IconViewWithDateHolder dHolder = (IconViewWithDateHolder) holder;

                    dHolder.dateInfo.setVisibility(View.INVISIBLE);
                    holder.roomsOpen.setVisibility(View.GONE);
                    // holder.roomsOpen.setVisibility(View.VISIBLE);
                    if (item.getDateInfo() != null) {
                        //     holder.roomsOpen.setVisibility(View.GONE);
                        dHolder.dateInfo.setVisibility(View.VISIBLE);
                        dHolder.dateInfo.setText(item.getDateInfo());
                    }
                }

            }
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                ArrayList<IconItem> filterList = new ArrayList<IconItem>();
                for (int i = 0; i < mStringFilterList.size(); i++) {
                    if ((mStringFilterList.get(i).getTitle().toLowerCase())
                            .contains(constraint.toString().toLowerCase())) {

                        IconItem icon = new IconItem(mStringFilterList.get(i)
                                .getJabberId(), mStringFilterList.get(i)
                                .getTitle(), mStringFilterList.get(i)
                                .getInfo(), null, mStringFilterList.get(i).getChatParty());
                        icon.setImageUri(mStringFilterList.get(i).getImageUri());

                        filterList.add(icon);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = mStringFilterList.size();
                results.values = mStringFilterList;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            iconlist = (ArrayList<IconItem>) results.values;
            notifyDataSetChanged();
        }

    }

    class IconViewHolder {
        private View relativeLayout2;
        private ImageView iconView;
        private FrameLayout imagePhotoFrame;
        private Target imagePhotoPicasso;
        private TextView textInfo;
        private TextView textTitle;
        private TextView textInfoBiasa;
        private TextView textTitleBiasa;
        private BadgeView badge;
        private BadgeView badgeBiasa;
        private ImageButton roomsOpen;

        public IconViewHolder(View row) {
            relativeLayout2 = (View) row.findViewById(R.id.relativeLayout2);
            imagePhotoFrame = (FrameLayout) row.findViewById(R.id.imagePhotoFrame);
            imagePhotoPicasso = (Target) row.findViewById(R.id.imagePhotoPicasso);
            iconView = (ImageView) row.findViewById(R.id.imagePhoto);
            textInfo = (TextView) row.findViewById(R.id.textInfo);
            textTitle = (TextView) row.findViewById(R.id.textTitle);
            textInfoBiasa = (TextView) row.findViewById(R.id.textInfoBiasa);
            textTitleBiasa = (TextView) row.findViewById(R.id.textTitleBiasa);
            badge = new BadgeView(context, textInfo);
            badgeBiasa = new BadgeView(context, textInfoBiasa);
            roomsOpen = (ImageButton) row.findViewById(R.id.roomsOpen);
        }
    }

    class IconViewWithDateHolder extends IconViewHolder {
        private TextView dateInfo;

        public IconViewWithDateHolder(View row) {
            super(row);
            dateInfo = (TextView) row.findViewById(R.id.dateInfo);
        }
    }

    class IconViewForInvite extends IconViewHolder {
        private TextView title;
        private FloatingActionButton fab;

        public IconViewForInvite(View row) {
            super(row);
            title = (TextView) row.findViewById(R.id.textTitle);
            fab = (FloatingActionButton) row.findViewById(R.id.fab);
        }
    }

}