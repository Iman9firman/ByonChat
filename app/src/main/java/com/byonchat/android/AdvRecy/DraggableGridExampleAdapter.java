package com.byonchat.android.AdvRecy;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.byonchat.android.R;
import com.byonchat.android.ui.adapter.OnItemClickListener;
import com.byonchat.android.ui.adapter.OnLongItemClickListener;
import com.byonchat.android.ui.viewholder.MyViewHolder;
import com.google.gson.Gson;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;

import java.util.ArrayList;
import java.util.List;

public class DraggableGridExampleAdapter extends RecyclerView.Adapter<MyViewHolder>
        implements DraggableItemAdapter<MyViewHolder>, Filterable {

    private Context context;
    private List<ItemMain> itemList;
    private List<ItemMain> filterList;
    private List<String> positionList;
    private String charString;
    private int room_id;

    private MainDbHelper database;
    private SQLiteDatabase db;

    protected OnItemClickListener itemClickListener;
    protected OnLongItemClickListener longItemClickListener;

    public DraggableGridExampleAdapter(Context context, List<ItemMain> itemList, int room_id, List<String> positionList) {
        setHasStableIds(true);

        this.context = context;
        this.itemList = itemList;
        this.filterList = itemList;
        this.room_id = room_id;
        this.positionList = positionList;
    }

    @Override
    public long getItemId(int position) {
        return filterList.get(position).getId();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_grid_item, parent, false);
        return new MyViewHolder(v, itemClickListener, longItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ItemMain im = filterList.get(position);

        holder.mTextView.setText(im.getTitle());
        holder.mImageView.setImageResource(R.drawable.logo_byon);
//        AutofitHelper.create(holder.mTextView);

    }

    @Override
    public int getItemCount() {
        return filterList.size();
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
        String inputString = gson.toJson(positionList);

        System.out.println("Input String : " + inputString);

        insertData(room_id, inputString);
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
                charString = constraint.toString().toUpperCase();

                if (charString.isEmpty()) {
                    results.values = itemList;
                    results.count = itemList.size();
                } else {
                    List<ItemMain> filteredData = new ArrayList<>();
                    for (ItemMain row : itemList) {
                        if (row.getTitle().toString().contains(charString)) {
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

    public void insertData(int room_id, String position) {

        database = new MainDbHelper(context);
        db = database.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(MainDbHelper.TAB_ROOM_ID, room_id);
        cv.put(MainDbHelper.TAB_POSITION, position);

        db.delete(MainDbHelper.TABLE_ITEM, null, null);
        long id = db.insert(MainDbHelper.TABLE_ITEM, null, cv);
        db.close();
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setOnLongItemClickListener(OnLongItemClickListener longItemClickListener) {
        this.longItemClickListener = longItemClickListener;
    }
}
