package com.byonchat.android.AdvRecy;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.byonchat.android.Manhera.Manhera;
import com.byonchat.android.R;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.ui.adapter.OnItemClickListener;
import com.byonchat.android.ui.adapter.OnLongItemClickListener;
import com.byonchat.android.ui.viewholder.MyViewHolder;
import com.byonchat.android.utils.Fonts;
import com.byonchat.android.utils.Utility;
import com.google.gson.Gson;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.itextpdf.text.Font;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DraggableGridExampleAdapter extends RecyclerView.Adapter<MyViewHolder>
        implements DraggableItemAdapter<MyViewHolder>, Filterable {

    private Context context;
    private List<ItemMain> itemList;
    private List<ItemMain> filterList;
    private List<String> positionList;
    private String charString;
    private int resourceId;
    private int room_id;

    protected Fonts fonts;

    protected OnItemClickListener itemClickListener;
    protected OnLongItemClickListener longItemClickListener;

    public DraggableGridExampleAdapter(Context context, List<ItemMain> itemList, int resourceId, int room_id, List<String> positionList) {
        setHasStableIds(true);

        this.context = context;
        this.itemList = itemList;
        this.resourceId = resourceId;
        this.filterList = itemList;
        this.room_id = room_id;
        this.positionList = positionList;

        fonts = new Fonts();
    }

    @Override
    public long getItemId(int position) {
        return filterList.get(position).getId();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(resourceId, parent, false);
        return new MyViewHolder(v, itemClickListener, longItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ItemMain im = filterList.get(position);

        if (charString != null && !charString.isEmpty()) {
            int startPos = im.getTitle().toLowerCase(Locale.getDefault()).indexOf(charString.toLowerCase(Locale.getDefault()));
            int endPos = startPos + charString.length();

            if (startPos != -1) {
                Spannable spannable = new SpannableString(im.getTitle());
                ColorStateList blueColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.BLUE});
                TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.NORMAL, -1, blueColor, null);
                spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.mTextView.setText(spannable);
            } else {
                holder.mTextView.setText(Utility.capitalizer(im.getTitle()));
            }
        } else {
            holder.mTextView.setText(Utility.capitalizer(im.getTitle()));
        }

        holder.mTextView.setSelected(true);

        Manhera.getInstance().get()
                .load(im.icon_name.equalsIgnoreCase(null)
                        || im.icon_name.equalsIgnoreCase("null") ? im.iconTest : im.icon_name)
                .into(holder.mImageView);


        fonts.FontFamily(context.getAssets(), holder.mTextView, Fonts.FONT_ROBOTO_BOLD);

    }

    public void setItems(List<ItemMain> items, List<String> positionList) {
        this.itemList = items;
        this.filterList = items;
        this.positionList = positionList;
        notifyDataSetChanged();
    }

    public int getBasicItemCount() {
        return (null != filterList ? filterList.size() : 0);
    }

    @Override
    public int getItemCount() {
        return getBasicItemCount();
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }
        ItemMain movedItem = filterList.remove(fromPosition);
        filterList.add(toPosition, movedItem);

        String movedPosition = positionList.remove(fromPosition);
        positionList.add(toPosition, movedPosition);

        Gson gson = new Gson();
        String value = gson.toJson(filterList);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.EXTRA_TAB_MOVEMENT, value);
        editor.apply();
    }

    @Override
    public boolean onCheckCanStartDrag(@NonNull MyViewHolder holder, int position, int x, int y) {
        return true;
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(@NonNull MyViewHolder holder, int position) {
        // no drag-sortable range specified
        return null;
    }

    @Override
    public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
        return true;
    }

    @Override
    public void onItemDragStarted(int position) {
        notifyDataSetChanged();
    }

    @Override
    public void onItemDragFinished(int fromPosition, int toPosition, boolean result) {
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                charString = constraint.toString().toLowerCase(Locale.getDefault());

                if (charString.isEmpty()) {
                    results.values = itemList;
                    results.count = itemList.size();
                } else {
                    List<ItemMain> filteredData = new ArrayList<>();
                    for (ItemMain row : itemList) {
                        if (row.getTitle().toString().toLowerCase(Locale.getDefault()).contains(charString)) {
                            filteredData.add(row);
                        }
                    }
                    results.values = filteredData;
                    results.count = filteredData.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filterList = (List<ItemMain>) results.values;
                notifyDataSetChanged();

            }
        };
    }

    public List<ItemMain> getData() {
        itemList = filterList;
        return filterList;
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setOnLongItemClickListener(OnLongItemClickListener longItemClickListener) {
        this.longItemClickListener = longItemClickListener;
    }
}
