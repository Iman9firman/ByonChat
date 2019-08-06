package com.byonchat.android.view;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Window;
import com.byonchat.android.R;
import com.byonchat.android.Sample.Adapter.ExpandableListAdapter;
import com.byonchat.android.model.SectionSampleItem;

import java.util.ArrayList;

public class ItemDialog extends Dialog {

    Context context;

    String title;
    ArrayList<SectionSampleItem> subList = new ArrayList<>();
    RecyclerView recyclerView;
    ExpandableListAdapter adapter;

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

        recyclerView.setHasFixedSize(true);

        adapter = new ExpandableListAdapter(context, subList);

        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        recyclerView.setAdapter(adapter);
    }
}
