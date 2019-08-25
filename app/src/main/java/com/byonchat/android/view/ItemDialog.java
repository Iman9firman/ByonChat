package com.byonchat.android.view;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.byonchat.android.AdvRecy.DraggableGridExampleAdapter;
import com.byonchat.android.AdvRecy.ItemMain;
import com.byonchat.android.ByonChatMainRoomActivity;
import com.byonchat.android.R;
import com.byonchat.android.Sample.Adapter.ExpandableListAdapter;
import com.byonchat.android.model.SectionSampleItem;
import com.byonchat.android.ui.adapter.OnItemClickListener;

import java.util.ArrayList;

import carbon.widget.LinearLayout;

public class ItemDialog extends Dialog {

    Context context;

    String title;
    ArrayList<SectionSampleItem> subList = new ArrayList<>();
    RecyclerView recyclerView;
    ExpandableListAdapter adapter;
    TextView titleName;

    public ItemDialog(Context context, String title, ArrayList<SectionSampleItem> subList) {
        super(context);
        this.context = context;
        this.title = title;
        this.subList = subList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_item_dialog);

        recyclerView = findViewById(R.id.recycler_dialog);
        titleName = findViewById(R.id.titleList);

        if(subList.size() > 1) {
            titleName.setVisibility(View.GONE);
            recyclerView.setHasFixedSize(true);

            adapter = new ExpandableListAdapter(context, subList);

            recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

            recyclerView.setAdapter(adapter);
        }else {
            titleName.setVisibility(View.VISIBLE);


            final String sectionName = subList.get(0).getHeaderTitle();

            ArrayList<ItemMain> singleSectionItems = subList.get(0).getAllItemsInSection();

            titleName.setText(sectionName);
            GridLayoutManager layoutManager = new GridLayoutManager(context,3, LinearLayout.VERTICAL,false);

            DraggableGridExampleAdapter itemListDataAdapter = new DraggableGridExampleAdapter(context, singleSectionItems, R.layout.list_grid_item);
            itemListDataAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = ByonChatMainRoomActivity.generateIntent(context, (ItemMain) itemListDataAdapter.getData().get(position));
                    context.startActivity(intent);
                }
            });

            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(itemListDataAdapter);

            recyclerView.setNestedScrollingEnabled(false);
        }
    }
}
