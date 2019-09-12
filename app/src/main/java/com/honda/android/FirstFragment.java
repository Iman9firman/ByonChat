package com.honda.android;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.honda.android.adapter.RecyclerViewAdapter;
import com.honda.android.list.IconItem;
import com.honda.android.provider.Contact;
import com.honda.android.provider.MessengerDatabaseHelper;

import java.util.ArrayList;

public class FirstFragment extends Fragment {

        RecyclerViewAdapter adapter;
        RecyclerView recyclerView;
        private static ArrayList<IconItem> demoData;
        private MessengerDatabaseHelper messengerHelper;
        private static final String SQL_SELECT_CONTACTS = "SELECT * FROM "
            + Contact.TABLE_NAME + " WHERE _id > 1 order by lower("+Contact.NAME+")";
        private boolean contactIsRefereshing = false;

        public FirstFragment() {
            if (messengerHelper == null) {
                messengerHelper = MessengerDatabaseHelper.getInstance(getActivity());
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.first_fragment, container, false);
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            recyclerView = (RecyclerView) getView().findViewById(R.id.list);
            recyclerView.setHasFixedSize(true);

            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(llm);

            demoData = new ArrayList<IconItem>();
            char c = 'A';
            for (byte i = 0; i < 100; i++) {
                IconItem item = new IconItem(String.valueOf(i),
                        "name", "I love byonchat", null, null);
                demoData.add(item);
            }

            adapter = new RecyclerViewAdapter(getContext(),demoData);
            recyclerView.setAdapter(adapter);
        }


}

