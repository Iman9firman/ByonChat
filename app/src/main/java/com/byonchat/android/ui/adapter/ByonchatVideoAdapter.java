package com.byonchat.android.ui.adapter;

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

import com.bumptech.glide.Glide;
import com.byonchat.android.R;
import com.byonchat.android.data.model.Video;
import com.byonchat.android.ui.viewholder.ByonchatVideoTubeViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ByonchatVideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    protected static final int VIEWTYPE_ITEM_TEXT = 1;

    protected List<Video> items;
    protected List<Video> itemsFiltered;

    protected Context context;
    protected ArrayFilter mFilter;
    protected String mSearchText;

    protected OnItemClickListener itemClickListener;
    protected OnLongItemClickListener longItemClickListener;
    protected OnPopupItemClickListener popupItemClickListener;

    protected ForwardItemClickListener onVideoTubeClickListener;

    public ByonchatVideoAdapter(Context context,
                                List<Video> items,
                                ForwardItemClickListener videoTubeClickListener,
                                OnPopupItemClickListener popupItemClickListener) {
        this.context = context;
        this.items = items;
        this.itemsFiltered = items;
        this.onVideoTubeClickListener = videoTubeClickListener;
        this.popupItemClickListener = popupItemClickListener;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        switch (viewType) {
            case VIEWTYPE_ITEM_TEXT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_b_videotube, parent, false);
                return new ByonchatVideoTubeViewHolder(view, itemClickListener, longItemClickListener);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_b_videotube, parent, false);
                return new ByonchatVideoTubeViewHolder(view, itemClickListener, longItemClickListener);
        }
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int i) {
        Video item = itemsFiltered.get(i);
        if (viewHolder instanceof ByonchatVideoTubeViewHolder) {
            String title = item.title;

            if (mSearchText != null && !mSearchText.isEmpty()) {
                int startPos = title.toLowerCase(Locale.getDefault()).indexOf(mSearchText.toLowerCase(Locale.getDefault()));
                int endPos = startPos + mSearchText.length();

                if (startPos != -1) {
                    Spannable spannable = new SpannableString(title);
                    ColorStateList blueColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.BLUE});
                    TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.NORMAL, -1, blueColor, null);
                    spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ((ByonchatVideoTubeViewHolder) viewHolder).vContents.setText(spannable);
                } else {
                    ((ByonchatVideoTubeViewHolder) viewHolder).vContents.setText(title);
                }
            } else {
                ((ByonchatVideoTubeViewHolder) viewHolder).vContents.setText(title);
            }

            showFileImage(viewHolder, item.thumbnail);

            ((ByonchatVideoTubeViewHolder) viewHolder).vInfo.setText(item.size + " \u2022 " + item.length);

            ((ByonchatVideoTubeViewHolder) viewHolder).vDescription.setText(Html.fromHtml(item.description));

            ((ByonchatVideoTubeViewHolder) viewHolder).onCommentSelected(item);

            ((ByonchatVideoTubeViewHolder) viewHolder).vMore.setOnClickListener(view -> {
                if (popupItemClickListener != null) {
                    popupItemClickListener.onItemClick(view, i, (Video) getData().get(i));
                }
            });
        }
    }

    private void showFileImage(RecyclerView.ViewHolder viewHolder, String thumbnail) {
        Glide.with(context).load(thumbnail)
                .asBitmap()
                .dontAnimate()
                .error(R.drawable.no_image)
                .into(((ByonchatVideoTubeViewHolder) viewHolder).vAvatar);
    }

    public List<Video> getData() {
        return itemsFiltered;
    }

    @Override
    public int getItemViewType(int position) {
        Video item = items.get(position);
        switch (item.type) {
            case Video.TYPE_TEXT:
                return VIEWTYPE_ITEM_TEXT;
            default:
                return VIEWTYPE_ITEM_TEXT;
        }
    }

    public int getItemCount() {
        return (null != itemsFiltered ? itemsFiltered.size() : 0);
    }

    public void setItems(List<Video> items) {
        this.items = items;
        this.itemsFiltered = items;
        notifyDataSetChanged();
    }

    public List<Video> getSelectedComments() {
        List<Video> selectedContacts = new ArrayList<>();
        int size = itemsFiltered.size();
        for (int i = size - 1; i >= 0; i--) {
            if (itemsFiltered.get(i).isSelected()) {
                selectedContacts.add(itemsFiltered.get(i));
            }
        }
        return selectedContacts;
    }

    public void addOrUpdate(Video e) {
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

    public void addOrDelete(Video c) {
        int i = findPosition(c);
        if (i >= 0) {
            remove(c);
        } else {
            add(c);
        }
    }

    public void add(Video e) {
        items.add(0, e);
        notifyDataSetChanged();
    }

    public void remove(Video e) {
        int position = findPosition(e);
        remove(position);
    }

    public int findPosition(Video e) {
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
                List<Video> filteredList = new ArrayList<>();
                for (Video row : items) {
                    if (row.title.toLowerCase(Locale.getDefault()).contains(charString.toLowerCase(Locale.getDefault()))
                            || row.description.toLowerCase(Locale.getDefault()).contains(charString.toLowerCase(Locale.getDefault()))) {
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
            itemsFiltered = (ArrayList<Video>) filterResults.values;
            notifyDataSetChanged();

            setOnItemClickListener((view, position) -> {
                if (onVideoTubeClickListener != null) {
                    Video c = (Video) getData().get(position);
                    onVideoTubeClickListener.onItemVideoClick((Video) getData().get(position));
                }
            });

            setOnLongItemClickListener((view, position) -> {
                if (onVideoTubeClickListener != null) {
                    Video c = (Video) getData().get(position);
                    onVideoTubeClickListener.onItemVideoLongClick((Video) getData().get(position));
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
}
