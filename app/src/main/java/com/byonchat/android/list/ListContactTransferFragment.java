package com.byonchat.android.list;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.byonchat.android.adapter.ContactAdapter;
import com.byonchat.android.list.IconItem;
import com.byonchat.android.list.MainListFragment;

import java.util.ArrayList;
import java.util.Iterator;

public class ListContactTransferFragment extends MainListFragment {

    ListFragmentItemClickListener contactItemClickListener;

    /** An interface for defining the callback method */
    public interface ListFragmentItemClickListener {
        /** This method will be invoked when an item in the ListFragment is clicked */
        void onListFragmentItemClick(int position,IconItem iconItem);
    }

    /** A callback function, executed when this fragment is attached to an activity */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            /** This statement ensures that the hosting activity implements ListFragmentItemClickListener */
            contactItemClickListener = (ListFragmentItemClickListener) activity;
        }catch(Exception e){
            Toast.makeText(activity.getBaseContext(), "Exception", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        IconItem item = (IconItem) items.get(position);
        contactItemClickListener.onListFragmentItemClick(position,item);
     /*   IconItem item = (IconItem) items.get(position);
        String jabberId = item.getJabberId().toString();
        Intent intent = new Intent(getActivity(), TransferActivity.class);
        intent.putExtra("number",jabberId);
        startActivity(intent);*/
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (items == null) {
            items = new ArrayList<IconItem>();
        }
        if (adapter == null) {
            adapter = new ContactAdapter(getActivity(), items);
        }

    }

    public void setEditMode(boolean editMode) {
        for (Iterator iterator = items.iterator(); iterator.hasNext();) {
            IconItem item = (IconItem) iterator.next();
            item.setEditMode(true);
        }
        adapter.notifyDataSetInvalidated();
    }

}
