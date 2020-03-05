package com.byonchat.android;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.byonchat.android.adapter.RecyclerViewAdapter;
import com.byonchat.android.list.IconItem;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.MessengerDatabaseHelper;

import java.util.ArrayList;

import static com.byonchat.android.utils.Utility.reportCatch;

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
            try {
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

                adapter = new RecyclerViewAdapter(getContext(), demoData);
                recyclerView.setAdapter(adapter);
            } catch (Exception e) {
                reportCatch(e.getLocalizedMessage());
            }
        }
}

