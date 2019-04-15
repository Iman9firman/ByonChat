package com.byonchat.android.ui.adapter;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.byonchat.android.R;
import com.byonchat.android.data.model.File;
import com.byonchat.android.ui.viewholder.ByonchatPDFViewHolder;
import com.byonchat.android.utils.Utility;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.gujun.android.taggroup.TagGroup;

public class ByonchatDefectListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    protected static final int VIEWTYPE_ITEM_TEXT = 1;

    protected List<File> items;
    protected List<File> itemsFiltered;

    protected Context context;
    protected ArrayFilter mFilter;
    protected String mSearchText;

    protected OnItemClickListener itemClickListener;
    protected OnLongItemClickListener longItemClickListener;

    protected OnPreviewItemClickListener onPreviewItemClickListener;

    public ByonchatDefectListAdapter(Context context,
                              List<File> items,
                              OnPreviewItemClickListener onPreviewItemClickListener) {
        this.context = context;
        this.items = items;
        this.itemsFiltered = items;
        this.onPreviewItemClickListener = onPreviewItemClickListener;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        switch (viewType) {
            case VIEWTYPE_ITEM_TEXT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_pdf_control, parent, false);
                return new ByonchatPDFViewHolder(view, itemClickListener, longItemClickListener);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_pdf_control, parent, false);
                return new ByonchatPDFViewHolder(view, itemClickListener, longItemClickListener);
        }
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int i) {
        File item = itemsFiltered.get(i);
        if (viewHolder instanceof ByonchatPDFViewHolder) {
//            ((ByonchatPDFViewHolder) viewHolder).vName.setVisibility(View.INVISIBLE);

            showFileImage(viewHolder, item.url);
            showTagView(viewHolder, item.description);
            showDate(viewHolder, item.subtitle);

            /*((ByonchatPDFViewHolder) viewHolder).vTimestamp.setText(parseDateToddMMyyyy(item.timestamp));*/
            ((ByonchatPDFViewHolder) viewHolder).vLayoutDate.setVisibility(View.GONE);

            ((ByonchatPDFViewHolder) viewHolder).vTxtStatusMsg.setText(Html.fromHtml(item.subtitle));
            ((ByonchatPDFViewHolder) viewHolder).vTxtStatusMsg.setTypeface(null, Typeface.BOLD);

            ((ByonchatPDFViewHolder) viewHolder).vMainContent.setOnClickListener(view -> {
                if (onPreviewItemClickListener != null) {
                    onPreviewItemClickListener.onItemClick(view, i, (File) getData().get(i));
                }
            });

            ((ByonchatPDFViewHolder) viewHolder).vBtComment.setVisibility(View.GONE);
        }
    }

    private void showFileImage(RecyclerView.ViewHolder viewHolder, String thumbnail) {
        Picasso.with(context).load(R.drawable.ic_004).into(((ByonchatPDFViewHolder) viewHolder).vIconView);
    }

    private void showDate(RecyclerView.ViewHolder viewHolder, String keterangan){
        String[] ff = keterangan.split("/");
        String date = ff[2];
        ((ByonchatPDFViewHolder) viewHolder).vName.setText(parseDateToddMMyyyy(date));
        ((ByonchatPDFViewHolder) viewHolder).vName.setTypeface(null, Typeface.NORMAL);
        ((ByonchatPDFViewHolder) viewHolder).vName.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
        ((ByonchatPDFViewHolder) viewHolder).vName.setTextSize(12);
        ((ByonchatPDFViewHolder) viewHolder).vName.setPadding(0,0,10,0);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private String[] getTagView(String item) {
        List<String> tagList = new ArrayList<>();

        try {
            JSONArray items = new JSONArray(item);

            for (int i = 0; i < items.length(); i++) {
                JSONObject jsonObject = items.getJSONObject(i);
                String tag = "#" + jsonObject.getString("tag");
                tagList.add(tag);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tagList.toArray(new String[]{});
    }

    private void showTagView(RecyclerView.ViewHolder viewHolder, String item) {
        String[] tags = getTagView(item) /*null*/;

        if (tags != null && tags.length > 0) {
            ((ByonchatPDFViewHolder) viewHolder).vTagGroup.setTags(tags);
        }

        ((ByonchatPDFViewHolder) viewHolder).vTagGroup.setOnTagClickListener(s -> {
            /*Toast.makeText(context, s, Toast.LENGTH_SHORT).show();*/
            getFilter2().filter(s);
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
            String charString = charSequence.toString().toLowerCase(Locale.getDefault());
            if (charString.isEmpty()) {
                mSearchText = "";
                itemsFiltered = items;
            } else {
                mSearchText = charString;
                List<File> filteredList = new ArrayList<>();
                for (File row : items) {
                    if (row.subtitle.toLowerCase(Locale.getDefault()).contains(charString) ) {
                        Log.w("Kita ngobrol uie",row.title +", "+row.description+", "+row.timestamp);
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

    public Filter getFilter2() {
        ArrayFilter2 mFilter2 = null;
        if (mFilter2 == null) {
            mFilter2 = new ArrayFilter2();
        }
        return mFilter2;
    }


    private class ArrayFilter2 extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            String charString = charSequence.toString().toLowerCase(Locale.getDefault());
            if (charString.isEmpty()) {
                mSearchText = "";
                itemsFiltered = items;
            } else {
                mSearchText = charString;
                List<File> filteredList = new ArrayList<>();

                for (File row : items) {
                    Log.w("Aku ngobrol uie", row.description + ", " + charString);

                    try {
                        JSONArray itemi = new JSONArray(row.description);

                        for (int i = 0; i < itemi.length(); i++) {
                            JSONObject jsonObject = itemi.getJSONObject(i);
                            String tag = "#" + jsonObject.getString("tag");
                            if (tag.toLowerCase(Locale.getDefault()).contains(charString)){
                                Log.w("Kita ngobrol uie", tag + ", " + charString);
                                filteredList.add(row);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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
        }
    }

    public String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyyMMdd";
//        String outputPattern = "dd-MMM-yyyy h:mm a";
        String outputPattern = "dd-MMM-yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setOnLongItemClickListener(OnLongItemClickListener longItemClickListener) {
        this.longItemClickListener = longItemClickListener;
    }
}

