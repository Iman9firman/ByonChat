package com.byonchat.android.Sample.Adapter;

import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import com.byonchat.android.AdvRecy.DraggableGridExampleAdapter;
import com.byonchat.android.ByonChatMainRoomActivity;
import com.byonchat.android.ui.adapter.OnItemClickListener;
import com.byonchat.android.AdvRecy.ItemMain;
import com.byonchat.android.R;
import com.byonchat.android.model.SectionSampleItem;

import carbon.widget.LinearLayout;

public class ExpandableListAdapter extends RecyclerView.Adapter<ExpandableListAdapter.ItemRowHolder> {

    private ArrayList<SectionSampleItem> dataList;
    private Context mContext;

    public ExpandableListAdapter(Context context, ArrayList<SectionSampleItem> dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contoh_satu, null);
        ItemRowHolder mh = new ItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(ItemRowHolder itemRowHolder, int i) {

        final String sectionName = dataList.get(i).getHeaderTitle();

        ArrayList<ItemMain> singleSectionItems = dataList.get(i).getAllItemsInSection();

        itemRowHolder.itemTitle.setText(sectionName);
        GridLayoutManager layoutManager = new GridLayoutManager(mContext,3, LinearLayout.VERTICAL,false);

        DraggableGridExampleAdapter itemListDataAdapter = new DraggableGridExampleAdapter(mContext, singleSectionItems, R.layout.list_grid_item);
        itemListDataAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = ByonChatMainRoomActivity.generateIntent(mContext, (ItemMain) itemListDataAdapter.getData().get(position));
                mContext.startActivity(intent);
            }
        });

        itemRowHolder.recycler_view_list.setHasFixedSize(true);
        itemRowHolder.recycler_view_list.setLayoutManager(layoutManager);
        itemRowHolder.recycler_view_list.setAdapter(itemListDataAdapter);

        itemRowHolder.recycler_view_list.setNestedScrollingEnabled(false);

    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView itemTitle;

        protected RecyclerView recycler_view_list;

        public ItemRowHolder(View view) {
            super(view);

            this.itemTitle = (TextView) view.findViewById(R.id.lblListHeader);
            this.recycler_view_list = (RecyclerView) view.findViewById(R.id.recycler_view_list);


        }

    }

}