package com.honda.android.createMeme;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.honda.android.R;

/**
 * Created by Iman Firmansyah on 3/5/2015.
 */
public class OnItemClickListenerListViewItem implements AdapterView.OnItemClickListener {

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Context context = view.getContext();
        TextView textViewItem = ((TextView) view.findViewById(R.id.label));
        // get the clicked item name
        String listItemText = textViewItem.getText().toString();
        // get the clicked item ID
        ((PhotoSortrActivity) context).setStyleFont(listItemText);
       ((PhotoSortrActivity) context).alertDialogFont.cancel();

    }

}