package com.byonchat.android.Listener;

import android.content.Context;
import android.os.Build;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.view.ActionMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.byonchat.android.R;
import com.byonchat.android.adapter.HistoryChatAdapter;
import com.byonchat.android.list.IconItem;

import java.util.ArrayList;

/**
 * Created by imanfirmansyah on 4/18/17.
 */

public class Toolbar_ActionMode_Callback  implements ActionMode.Callback {

    private Context context;
    private HistoryChatAdapter contactAdapter;
    private ArrayList<IconItem> contactArrayList;
    private boolean isListViewFragment;


    public Toolbar_ActionMode_Callback(Context context, HistoryChatAdapter cttAdapter, ArrayList<IconItem> contacts) {
        this.context = context;
        this.contactAdapter = cttAdapter;
        this.contactArrayList = contacts;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.menu_history_chat, menu);//Inflate the menu over action mode
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        if (Build.VERSION.SDK_INT < 11) {
            MenuItemCompat.setShowAsAction(menu.findItem(R.id.action_delete), MenuItemCompat.SHOW_AS_ACTION_NEVER);
        } else {
            menu.findItem(R.id.action_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                //action Delete
                Log.w("ini","Delete");
                break;
        }
        return false;
    }


    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }
}