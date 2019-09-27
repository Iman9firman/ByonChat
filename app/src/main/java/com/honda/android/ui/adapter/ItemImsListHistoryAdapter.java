package com.honda.android.ui.adapter;


import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.signature.StringSignature;
import com.honda.android.R;
import com.honda.android.communication.MessengerConnectionService;
import com.honda.android.list.IconItem;
import com.honda.android.list.utilLoadImage.TextLoader;
import com.honda.android.local.Byonchat;
import com.honda.android.provider.BotListDB;
import com.honda.android.ui.viewholder.ImsHeaderViewHolder;
import com.honda.android.ui.viewholder.ImsListHistoryFindViewHolder;
import com.honda.android.ui.viewholder.ImsListHistoryViewHolder;
import com.honda.android.utils.MediaProcessingUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ItemImsListHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    protected static final int VIEWTYPE_ITEM_HEADER = 0;
    protected static final int VIEWTYPE_ITEM_ORIGIN = 1;
    protected static final int VIEWTYPE_ITEM_MESSAGE_FIND = 2;

    protected List<IconItem> items;
    protected List<IconItem> itemsFiltered;

    protected Context context;
    protected ArrayFilter mFilter;
    protected String mSearchText;
    protected TextLoader textLoader;

    protected OnItemClickListener itemClickListener;
    protected OnLongItemClickListener longItemClickListener;
    protected ListHistoryItemClickListener onItemClickListener;

    public ItemImsListHistoryAdapter(Context context,
                                     List<IconItem> items,
                                     ListHistoryItemClickListener itemClickListener) {
        this.context = context;
        this.items = items;
        this.itemsFiltered = items;
        this.onItemClickListener = itemClickListener;
        textLoader = new TextLoader(context);
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        switch (viewType) {
            case VIEWTYPE_ITEM_MESSAGE_FIND:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ims_list_history_find, parent, false);
                return new ImsListHistoryFindViewHolder(view, itemClickListener, longItemClickListener);
            case VIEWTYPE_ITEM_HEADER:
                view = LayoutInflater.from(context).inflate(R.layout.item_ims_header, parent, false);
                return new ImsHeaderViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ims_list_history, parent, false);
                return new ImsListHistoryViewHolder(view, itemClickListener, longItemClickListener);
        }
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int i) {
        IconItem item = itemsFiltered.get(i);
        if (viewHolder instanceof ImsListHistoryViewHolder) {

            ((ImsListHistoryViewHolder) viewHolder).vTextInfo.setText(item.info);

            ((ImsListHistoryViewHolder) viewHolder).vDateInfo.setText(item.dateInfo);

            ((ImsListHistoryViewHolder) viewHolder).vTextUnread.setText(item.unread > 99 ? "99+" : item.unread + "");

            resolveContent(viewHolder, item);

            ((ImsListHistoryViewHolder) viewHolder).onCommentSelected(item);
        } else if (viewHolder instanceof ImsListHistoryFindViewHolder) {

            ((ImsListHistoryFindViewHolder) viewHolder).vTextInfo.setText(item.info);

            ((ImsListHistoryFindViewHolder) viewHolder).vDateInfo.setText(item.dateInfo);

            resolveContentFind(viewHolder, item);

            ((ImsListHistoryFindViewHolder) viewHolder).onCommentSelected(item);
        }
    }

    private void resolveContent(RecyclerView.ViewHolder viewHolder, IconItem item) {
        String regex = "[0-9]+";
        if (item.getJabberId().matches(regex)) {

            if (item.getTitle().matches(regex)) {
                String title = "fetching id...";
                ((ImsListHistoryViewHolder) viewHolder).vTextTitle.setText(title);
                textLoader.DisplayImage(item.getTitle(), ((ImsListHistoryViewHolder) viewHolder).vTextTitle);

                Cursor cur = Byonchat.getBotListDB().getRealNameByName(item.getTitle().toLowerCase());
                if (cur.getCount() > 0) {
                    title = cur.getString(cur.getColumnIndex(BotListDB.ROOMS_REALNAME));
                }
                cur.close();

                resolveSearchContent(((ImsListHistoryViewHolder) viewHolder), title);
            } else {
                ((ImsListHistoryViewHolder) viewHolder).vTextTitle.setText(Html.fromHtml(item.getTitle()));

                resolveSearchContent(((ImsListHistoryViewHolder) viewHolder), item.getTitle());
            }

            File photoFile = context.getFileStreamPath(MediaProcessingUtil
                    .getProfilePicName(item.getJabberId()));
            Drawable d = context.getResources().getDrawable(R.drawable.ic_no_photo);
            if (photoFile.exists()) {
                d = Drawable.createFromPath(photoFile.getAbsolutePath());
            }

            Glide.with(context).load("https://" + MessengerConnectionService.F_SERVER + "/toboldlygowherenoonehasgonebefore/" + item.getJabberId() + ".jpg").asBitmap()
                    .centerCrop()
                    .signature(new StringSignature(item.getSignature()))
                    .placeholder(d)
                    .animate(R.anim.fade_in_sort)
                    .into(new BitmapImageViewTarget(((ImsListHistoryViewHolder) viewHolder).vImagePhoto) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            ((ImsListHistoryViewHolder) viewHolder).vImagePhoto.setImageBitmap(resource);
                            SaveMedia task = new SaveMedia();
                            task.execute(new MyTaskParams[]{new MyTaskParams(resource, item.getJabberId())});
                        }
                    });
        } else {
            String title = "fetching room...";
            ((ImsListHistoryViewHolder) viewHolder).vTextTitle.setText(title);
            textLoader.DisplayImage(item.getTitle(), ((ImsListHistoryViewHolder) viewHolder).vTextTitle);

            Cursor cur = Byonchat.getBotListDB().getRealNameByName(item.getTitle().toLowerCase());
            if (cur.getCount() > 0) {
                title = cur.getString(cur.getColumnIndex(BotListDB.ROOMS_REALNAME));
            }
            cur.close();

            resolveSearchContent(((ImsListHistoryViewHolder) viewHolder), title);

            Glide.with(context).load("https://" + MessengerConnectionService.HTTP_SERVER + "/mediafiles/" + item.getJabberId() + "_thumb.png")
                    .asBitmap()
                    .animate(R.anim.fade_in_sort)
                    .placeholder(R.drawable.ic_room_door)
                    .into(new BitmapImageViewTarget(((ImsListHistoryViewHolder) viewHolder).vImagePhoto) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            ((ImsListHistoryViewHolder) viewHolder).vImagePhoto.setImageBitmap(resource);
                        }
                    });
        }
    }

    private void resolveContentFind(RecyclerView.ViewHolder viewHolder, IconItem item) {
        String regex = "[0-9]+";
        if (item.getJabberId().matches(regex)) {

            if (item.getTitle().matches(regex)) {
                String title = "fetching id...";
                ((ImsListHistoryFindViewHolder) viewHolder).vTextTitle.setText(title);
                textLoader.DisplayImage(item.getTitle(), ((ImsListHistoryFindViewHolder) viewHolder).vTextTitle);

                resolveSearchContentFind(((ImsListHistoryFindViewHolder) viewHolder), item.info);
            } else {
                ((ImsListHistoryFindViewHolder) viewHolder).vTextTitle.setText(Html.fromHtml(item.getTitle()));

                resolveSearchContentFind(((ImsListHistoryFindViewHolder) viewHolder), item.info);
            }

        } else {
            String title = "fetching room...";
            ((ImsListHistoryFindViewHolder) viewHolder).vTextTitle.setText(title);
            textLoader.DisplayImage(item.getTitle(), ((ImsListHistoryFindViewHolder) viewHolder).vTextTitle);

            resolveSearchContentFind(((ImsListHistoryFindViewHolder) viewHolder), item.info);
        }
    }

    protected void resolveSearchContent(ImsListHistoryViewHolder holder, String title) {
        if (mSearchText != null && !mSearchText.isEmpty()) {
            int startPos = title.toLowerCase(Locale.getDefault()).indexOf(mSearchText.toLowerCase(Locale.getDefault()));
            int endPos = startPos + mSearchText.length();

            if (startPos != -1) {
                Spannable spannable = new SpannableString(title);
                ColorStateList blueColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.BLUE});
                TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.NORMAL, -1, blueColor, null);
                spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.vTextTitle.setText(spannable);
            } else {
                holder.vTextTitle.setText(title);
            }
        } else {
            holder.vTextTitle.setText(title);
        }
    }

    protected void resolveSearchContentFind(ImsListHistoryFindViewHolder holder, String message) {
        if (mSearchText != null && !mSearchText.isEmpty()) {
            int startPos = message.toLowerCase(Locale.getDefault()).indexOf(mSearchText.toLowerCase(Locale.getDefault()));
            int endPos = startPos + mSearchText.length();

            if (startPos != -1) {
                Spannable spannable = new SpannableString(message);
                ColorStateList blueColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.BLUE});
                TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.NORMAL, -1, blueColor, null);
                spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.vTextInfo.setText(spannable);
            } else {
                holder.vTextInfo.setText(message);
            }
        } else {
            holder.vTextInfo.setText(message);
        }
    }

    private void showFileImage(RecyclerView.ViewHolder viewHolder, String jabberId) {
        Glide.with(context).load("https://" + MessengerConnectionService.F_SERVER + "/toboldlygowherenoonehasgonebefore/" + jabberId + ".jpg")
                .dontAnimate()
                .error(R.drawable.ic_no_photo)
                .into(((ImsListHistoryViewHolder) viewHolder).vImagePhoto);
    }

    public List<IconItem> getData() {
        items = itemsFiltered;
        return itemsFiltered;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public int getItemViewType(int position) {
        IconItem item = items.get(position);
        switch (item.type) {
            case IconItem.TYPE_MESSAGE_FIND:
                return VIEWTYPE_ITEM_MESSAGE_FIND;
            default:
                return VIEWTYPE_ITEM_ORIGIN;
        }
    }

    @Override
    public int getItemCount() {
        return getBasicItemCount();
    }

    public int getBasicItemCount() {
        return (null != itemsFiltered ? itemsFiltered.size() : 0);
    }

    public void setItems(List<IconItem> items) {
        this.items = items;
        this.itemsFiltered = items;
        notifyDataSetChanged();
    }

    public List<IconItem> getSelectedComments() {
        List<IconItem> selectedContacts = new ArrayList<>();
        int size = itemsFiltered.size();
        for (int i = size - 1; i >= 0; i--) {
            if (itemsFiltered.get(i).isSelected()) {
                selectedContacts.add(itemsFiltered.get(i));
            }
        }
        return selectedContacts;
    }

    public void addOrUpdate(IconItem e) {
        int i = findPosition(e);
        if (i >= 0) {
            if (!e.areContentsTheSame(itemsFiltered.get(i))) {
                e.isSelected = itemsFiltered.get(i).isSelected();
                items.set(i, e);
                itemsFiltered.set(i, e);
            }
            notifyItemChanged(i);
        } else {
            add(e);
        }
    }

    public void addOrDelete(IconItem c) {
        int i = findPosition(c);
        if (i >= 0) {
            remove(c);
        } else {
            add(c);
        }
    }

    public void add(IconItem e) {
        itemsFiltered.add(0, e);
        notifyDataSetChanged();
    }

    public void remove(IconItem e) {
        int position = findPosition(e);
        remove(position);
    }

    public int findPosition(IconItem e) {
        if (itemsFiltered == null) {
            return -1;
        }

        int size = itemsFiltered.size();
        for (int i = 0; i < size; i++) {
            if (itemsFiltered.get(i).equals(e)) {
                return i;
            }
        }

        return -1;
    }

    public void remove(int position) {
        if (position >= 0 && position < itemsFiltered.size()) {
            itemsFiltered.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clearSelectedComments() {
        int size = itemsFiltered.size();
        for (int i = size - 1; i >= 0; i--) {
            if (itemsFiltered.get(i).isSelected()) {
                itemsFiltered.get(i).isSelected = false;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    private class ArrayFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            String charString = charSequence.toString();
            if (charString.isEmpty()) {
                mSearchText = "";
                itemsFiltered = items;
            } else {
                mSearchText = charString;
                List<IconItem> filteredList = new ArrayList<>();
                for (IconItem row : items) {
                    if (row.type.equalsIgnoreCase(IconItem.TYPE_ORIGIN)) {
                        String regex = "[0-9]+";
                        if (row.getJabberId().matches(regex)) {
                            if (row.getTitle().matches(regex)) {
                                String title = "fetching id...";
                                Cursor cur = Byonchat.getBotListDB().getRealNameByName(row.getTitle().toLowerCase());
                                if (cur.getCount() > 0) {
                                    title = cur.getString(cur.getColumnIndex(BotListDB.ROOMS_REALNAME));
                                }
                                cur.close();
                                if (title.toLowerCase(Locale.getDefault()).contains(charString.toLowerCase(Locale.getDefault()))) {
                                    filteredList.add(row);
                                }
                            } else {
                                if (row.getTitle().toLowerCase(Locale.getDefault()).contains(charString.toLowerCase(Locale.getDefault()))) {
                                    filteredList.add(row);
                                }
                            }
                        } else {
                            String title = "fetching rooms...";
                            Cursor cur = Byonchat.getBotListDB().getRealNameByName(row.getTitle().toLowerCase());
                            if (cur.getCount() > 0) {
                                title = cur.getString(cur.getColumnIndex(BotListDB.ROOMS_REALNAME));
                            }
                            cur.close();
                            if (title.toLowerCase(Locale.getDefault()).contains(charString.toLowerCase(Locale.getDefault()))) {
                                filteredList.add(row);
                            }
                        }
                    /*if (row.title.toLowerCase(Locale.getDefault()).contains(charString.toLowerCase(Locale.getDefault()))
                            || row.info.toLowerCase(Locale.getDefault()).contains(charString.toLowerCase(Locale.getDefault()))) {
                        filteredList.add(row);
                    }*/
                    } else if (row.type.equalsIgnoreCase(IconItem.TYPE_MESSAGE_FIND)) {
                        if (row.info.toLowerCase(Locale.getDefault()).contains(charString.toLowerCase(Locale.getDefault()))) {
                            filteredList.add(row);
                        }
                    }
                }

                itemsFiltered = filteredList;
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = itemsFiltered;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            itemsFiltered = (ArrayList<IconItem>) filterResults.values;
            notifyDataSetChanged();

            setOnItemClickListener((view, position) -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemListClick(view, position);
                }
            });

            setOnLongItemClickListener((view, position) -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemListLongClick(view, position);
                }
            });
        }

    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setOnLongItemClickListener(OnLongItemClickListener longItemClickListener) {
        this.longItemClickListener = longItemClickListener;
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

        MyTaskParams(Bitmap bar, String contact) {
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
