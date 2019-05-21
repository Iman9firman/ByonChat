package com.byonchat.android.view;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.AdvRecy.DraggableGridExampleAdapter;
import com.byonchat.android.AdvRecy.ItemMain;
import com.byonchat.android.ByonChatMainRoomActivity;
import com.byonchat.android.R;
import com.byonchat.android.ui.adapter.OnItemClickListener;
import com.byonchat.android.ui.adapter.OnLongItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class ItemDialog extends Dialog {

    Context context;

    String title;
    List<ItemMain> subList = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    //    MainAdapter adapter;
    DraggableGridExampleAdapter adapter;
    TextView titleView;

    public ItemDialog(Context context, String title, List<ItemMain> subList) {
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

        titleView = findViewById(R.id.title_dialog);
        titleView.setText(title);
        recyclerView = findViewById(R.id.recycler_dialog);
        layoutManager = new GridLayoutManager(context,3,RecyclerView.VERTICAL,false);

        adapter = new DraggableGridExampleAdapter(context,subList, R.layout.list_grid_item);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = ByonChatMainRoomActivity.generateIntent(context, (ItemMain) adapter.getData().get(position));
                context.startActivity(intent);
                dismiss();
            }
        });

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}
