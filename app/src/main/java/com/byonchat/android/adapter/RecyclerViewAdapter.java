package com.byonchat.android.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.list.IconItem;
import com.byonchat.android.list.utilLoadImage.ImageLoader;
import com.byonchat.android.list.utilLoadImage.ImageLoaderFromSD;
import com.byonchat.android.provider.Group;
import com.byonchat.android.provider.Message;
import com.byonchat.android.utils.MediaProcessingUtil;
import com.byonchat.android.utils.Utility;
import com.byonchat.android.widget.BadgeView;
import com.github.clans.fab.FloatingActionButton;

import java.net.URLDecoder;
import java.util.ArrayList;

/**
 * Created by Iman Firmansyah on 12/1/2015.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>  implements Filterable  {

    public ImageLoader imageLoader;
    public ImageLoaderFromSD imageLoaderSD;
    Context context;
    ArrayList<IconItem> iconlist;
    ArrayList<IconItem> mStringFilterList;
    ValueFilter valueFilter;
    private static final int TYPE_IMAGE = 0;
    private static final int TYPE_GROUP = 1;


    public RecyclerViewAdapter(Context context, ArrayList<IconItem> iconlist) {
        this.context = context;
        this.iconlist = iconlist;
        mStringFilterList = iconlist;
        imageLoader = new ImageLoader(context);
        imageLoaderSD = new ImageLoaderFromSD(context);
    }

    @Override
    public int getItemViewType ( int position ) {
        int viewType;
            viewType = TYPE_GROUP;
        return viewType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());
        switch ( viewType ) {

            case TYPE_IMAGE:
                ViewGroup viewInvite = ( ViewGroup ) mInflater.inflate ( R.layout.list_item_invite, viewGroup, false );
                ViewHolder vhInvite = new ViewHolder ( viewInvite );
                return vhInvite;
            case TYPE_GROUP:
                ViewGroup viewContact = ( ViewGroup ) mInflater.inflate ( R.layout.list_item, viewGroup, false );
                ViewHolder vhContact = new ViewHolder ( viewContact );
                return vhContact;
            default:
                ViewGroup viewContacts = ( ViewGroup ) mInflater.inflate ( R.layout.list_item, viewGroup, false );
                ViewHolder vhContacts = new ViewHolder ( viewContacts );
                return vhContacts;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        switch ( viewHolder.getItemViewType () ) {

            case TYPE_GROUP:

                ViewHolder holder = ( ViewHolder ) viewHolder;
                final IconItem item = iconlist.get(position);
                String title = item.getTitle();
                if (item.getImageUri() != null) {
                    imageLoaderSD.DisplayImage(MediaProcessingUtil
                            .getProfilePic(item.getJabberId()), holder.iconView, false);
                } else {
                    if (item.getChatParty() instanceof Group) {
                        holder.iconView.setImageResource(R.drawable.ic_group);
                    } else {
                        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.ic_no_photo);
                        holder.iconView.setImageBitmap(MediaProcessingUtil.getRoundedCornerBitmap(icon, 10));

                        String regex = "[0-9]+";
                        if (!item.getJabberId().matches(regex)) {
                            title = Utility.roomName(context, item.getTitle(), true);
                            imageLoader.DisplayImage("https://" + MessengerConnectionService.HTTP_SERVER + "/mediafiles/" + item.getJabberId() + "_thumb.png", holder.iconView);
                        }
                    }
                }
                holder.textTitle.setText(Html.fromHtml(title));
                String text = Html.fromHtml(URLDecoder.decode(item.getInfo())).toString();

                if (text.contains("<")) {
                    text = Html.fromHtml(Html.fromHtml(text).toString()).toString();
                }
                holder.textInfo.setText(Message.parsedMessageText(text));
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
                        //      holder.textInfo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play, 0, 0, 0);
                    } else if (item.getValue().equals(Message.STATUS_TYPE_DELIVER)) {
                        holder.textInfo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_delivered, 0, 0, 0);
                        holder.textInfo.setCompoundDrawablePadding(5);
                    }  else if (item.getValue().equals(Message.STATUS_TYPE_READ)) {
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

                holder.roomsOpen.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                   /*     Intent intent = new Intent(context, WebViewByonActivity.class);
                        intent.putExtra(WebViewByonActivity.KEY_LINK_LOAD, "https://" + MessengerConnectionService.FILE_SERVER + "/personal_room/view/index.php?userid=" + item.getJabberId());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);*/
                    }
                });


/*                IconViewWithDateHolder dHolder = (IconViewWithDateHolder) holder;

                dHolder.dateInfo.setVisibility(View.INVISIBLE);
                holder.roomsOpen.setVisibility(View.GONE);
                // holder.roomsOpen.setVisibility(View.VISIBLE);
                if (item.getDateInfo() != null) {
                    //     holder.roomsOpen.setVisibility(View.GONE);
                    dHolder.dateInfo.setVisibility(View.VISIBLE);
                    dHolder.dateInfo.setText(item.getDateInfo());
                }*/

                break;
        }
    }

    @Override public int getItemCount() {
        return iconlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iconView;
        private TextView textInfo;
        private TextView textTitle;
        private BadgeView badge;
        private ImageButton roomsOpen;
        public ViewHolder(View row) {
            super(row);
            iconView = (ImageView) row.findViewById(R.id.imagePhoto);
            textInfo = (TextView) row.findViewById(R.id.textInfo);
            textTitle = (TextView) row.findViewById(R.id.textTitle);
            badge = new BadgeView(context, textInfo);
            roomsOpen = (ImageButton) row.findViewById(R.id.roomsOpen);
        }
    }
    class IconViewWithDateHolder extends ViewHolder {
        private TextView dateInfo;
        public IconViewWithDateHolder(View row) {
            super(row);
            dateInfo = (TextView) row.findViewById(R.id.dateInfo);
        }
    }

    class IconViewForInvite extends ViewHolder {
        private TextView title;
        private FloatingActionButton fab;

        public IconViewForInvite(View row) {
            super(row);
            title = (TextView) row.findViewById(R.id.textTitle);
            fab = (FloatingActionButton) row.findViewById(R.id.fab);
        }
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
                    if ( (mStringFilterList.get(i).getTitle().toLowerCase() )
                            .contains(constraint.toString().toLowerCase())) {

                        IconItem icon = new IconItem(mStringFilterList.get(i)
                                .getJabberId() ,  mStringFilterList.get(i)
                                .getTitle() ,  mStringFilterList.get(i)
                                .getInfo(),null,mStringFilterList.get(i).getChatParty());
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

}