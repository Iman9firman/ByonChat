package com.honda.android.ui.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.honda.android.R;
import com.honda.android.data.model.File;
import com.honda.android.ui.viewholder.ByonchatApprovalDocViewHolder;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.gujun.android.taggroup.TagGroup;

public class ByonchatApprovalDocAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    protected static final int VIEWTYPE_ITEM_TEXT = 1;

    protected List<File> items;
    protected List<File> itemsFiltered;

    protected Context context;
    protected ArrayFilter mFilter;
    protected String mSearchText;

    protected OnItemClickListener itemClickListener;
    protected OnLongItemClickListener longItemClickListener;

    protected OnRequestItemClickListener onRequestItemClickListener;
    protected OnPreviewItemClickListener onPreviewItemClickListener;

    public ByonchatApprovalDocAdapter(Context context,
                              List<File> items,
                              OnPreviewItemClickListener onPreviewItemClickListener,
                              OnRequestItemClickListener onRequestItemClickListener) {
        this.context = context;
        this.items = items;
        this.itemsFiltered = items;
        this.onPreviewItemClickListener = onPreviewItemClickListener;
        this.onRequestItemClickListener = onRequestItemClickListener;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        switch (viewType) {
            case VIEWTYPE_ITEM_TEXT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_approve_document, parent, false);
                return new ByonchatApprovalDocViewHolder(view, itemClickListener, longItemClickListener);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_approve_document, parent, false);
                return new ByonchatApprovalDocViewHolder(view, itemClickListener, longItemClickListener);
        }
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int i) {
        File item = itemsFiltered.get(i);
        if (viewHolder instanceof ByonchatApprovalDocViewHolder) {
            String title = item.title;

            if (mSearchText != null && !mSearchText.isEmpty()) {
                int startPos = title.toLowerCase(Locale.getDefault()).indexOf(mSearchText.toLowerCase(Locale.getDefault()));
                int endPos = startPos + mSearchText.length();

                if (startPos != -1) {
                    Spannable spannable = new SpannableString(title);
                    ColorStateList blueColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.BLUE});
                    TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.NORMAL, -1, blueColor, null);
                    spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ((ByonchatApprovalDocViewHolder) viewHolder).vName.setText(spannable);
                } else {
                    ((ByonchatApprovalDocViewHolder) viewHolder).vName.setText(title);
                }
            } else {
                ((ByonchatApprovalDocViewHolder) viewHolder).vName.setText(title);
            }

            showFileImage(viewHolder, item.url);
            showTagView(viewHolder);


            ((ByonchatApprovalDocViewHolder) viewHolder).vTimestamp.setText(item.timestamp);
            ((ByonchatApprovalDocViewHolder) viewHolder).vTxtStatusMsg.setText(Html.fromHtml(item.nama_requester));

            ((ByonchatApprovalDocViewHolder) viewHolder).vMainContent.setOnClickListener(view -> {
                if (onPreviewItemClickListener != null) {
                    onPreviewItemClickListener.onItemClick(view, i, (File) getData().get(i), item.type);
                }
            });
            ((ByonchatApprovalDocViewHolder) viewHolder).vFramePhoto.setOnClickListener(view -> {
                if (onRequestItemClickListener != null) {
                    onRequestItemClickListener.onItemClick(view, i, (File) getData().get(i));
                }
            });
        }
    }

    private void showFileImage(RecyclerView.ViewHolder viewHolder, String thumbnail) {
        Picasso.with(context).load("https://bb.byonchat.com/bc_voucher_client/public/list_attachment/icon-pdf.png").networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(((ByonchatApprovalDocViewHolder) viewHolder).vIconView);
    }

    private String[] getTagView() {
        List<String> tagList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            String tag = "String " + i;
            tagList.add(tag);
        }

        return tagList.toArray(new String[]{});
    }

    private void showTagView(RecyclerView.ViewHolder viewHolder) {
        String[] tags = /*getTagView()*/ null;

        if (tags != null && tags.length > 0) {
            ((ByonchatApprovalDocViewHolder) viewHolder).vTagGroup.setTags(tags);
        }

        ((ByonchatApprovalDocViewHolder) viewHolder).vTagGroup.setOnTagClickListener(s -> {
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
        });
    }

    public List<File> getData() {
        return itemsFiltered;
    }

    @Override
    public int getItemViewType(int position) {
        File item = items.get(position);
        switch (item.type) {
            case File.TYPE_TEXT:
                return VIEWTYPE_ITEM_TEXT;
            default:
                return VIEWTYPE_ITEM_TEXT;
        }
    }

    public int getItemCount() {
        return (null != itemsFiltered ? itemsFiltered.size() : 0);
    }

    public void setItems(List<File> items) {
        this.items = items;
        this.itemsFiltered = items;
        notifyDataSetChanged();
    }

    public List<File> getSelectedComments() {
        List<File> selectedContacts = new ArrayList<>();
        int size = itemsFiltered.size();
        for (int i = size - 1; i >= 0; i--) {
            if (itemsFiltered.get(i).isSelected()) {
                selectedContacts.add(itemsFiltered.get(i));
            }
        }
        return selectedContacts;
    }

    public void addOrUpdate(File e) {
        int i = findPosition(e);
        if (i >= 0) {
            if (!e.areContentsTheSame(items.get(i))) {
                e.isSelected = items.get(i).isSelected();
                items.set(i, e);
            }
            notifyItemChanged(i);
        } else {
            add(e);
        }
    }


    public void add(File e) {
        items.add(0, e);
        notifyDataSetChanged();
    }

    public void remove(File e) {
        int position = findPosition(e);
        remove(position);
    }

    public int findPosition(File e) {
        if (items == null) {
            return -1;
        }

        int size = items.size();
        for (int i = 0; i < size; i++) {
            if (items.get(i).equals(e)) {
                return i;
            }
        }

        return -1;
    }

    public void remove(int position) {
        if (position >= 0 && position < items.size()) {
            items.remove(position);
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
                List<File> filteredList = new ArrayList<>();
                for (File row : items) {
                    if (row.title.toLowerCase(Locale.getDefault()).contains(charString.toLowerCase(Locale.getDefault()))) {
                        filteredList.add(row);
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
            itemsFiltered = (ArrayList<File>) filterResults.values;
            notifyDataSetChanged();

            /*setOnItemClickListener((view, position) -> {
                if (onPreviewItemClickListener != null) {
                    File c = (File) getData().get(position);
                    onPreviewItemClickListener.onItemPreviewClick((File) getData().get(position));
                }
            });

            setOnLongItemClickListener((view, position) -> {
                if (onPreviewItemClickListener != null) {
                    File c = (File) getData().get(position);
                    onPreviewItemClickListener.onItemPreviewClick((File) getData().get(position));
                }
            });*/
        }
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setOnLongItemClickListener(OnLongItemClickListener longItemClickListener) {
        this.longItemClickListener = longItemClickListener;
    }
}

