package com.byonchat.android.list;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.byonchat.android.adapter.ContactAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainListFragment extends ListFragment {
    protected ArrayList items;
    protected ContactAdapter adapter;
    protected int menuId = 0;
    protected int contextMenuId = 0;

    public MainListFragment() {
        items = new ArrayList();
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setContextMenuId(int contextMenuId) {
        this.contextMenuId = contextMenuId;
    }

    public int getContextMenuId() {
        return contextMenuId;
    }

    public void updateItem(IconItem item) {
        int index = items.indexOf(item);
        if (index != -1) {
            items.set(index, item);
            refreshList();
        } else {
            addItem(item);
        }
    }

    public void moveItemToTop(IconItem item) {
        int index = items.indexOf(item);
        if (index != -1) {
            items.remove(index);
        }
        items.add(0, item);
        refreshList();
    }

    public void addItem(Object item, boolean refresh) {
        items.add(item);
        if (refresh)
            refreshList();
    }

    public void addItem(Object item) {
        addItem(item, true);
    }

    public void clearItems() {
        items.clear();
    }

    public void refreshList() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public List getItems() {
        return Collections.unmodifiableList(items);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        /*if (menuId != 0) {
            inflater.inflate(menuId, menu);
            if (menuId == R.menu.contacts_menu) {
                ProgressBar progressBar = (ProgressBar) MenuItemCompat
                        .getActionView(menu.findItem(R.id.menu_contact_refresh));
            }
            super.onCreateOptionsMenu(menu, inflater);
        }*/
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        if (contextMenuId != 0) {
            super.onCreateContextMenu(menu, v, menuInfo);
            android.view.MenuInflater inflater = getActivity()
                    .getMenuInflater();
            inflater.inflate(contextMenuId, menu);
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setRetainInstance(true);

        if (adapter == null) {
            adapter = new ContactAdapter(getActivity().getApplicationContext(), items);
        }
        setListAdapter(adapter);
        getListView().setDivider(null);
        registerForContextMenu(getListView());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (getActivity() instanceof AppCompatActivity) {
            return ((AppCompatActivity) getActivity())
                    .onOptionsItemSelected(item);
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (getActivity() instanceof AppCompatActivity) {
            return ((AppCompatActivity) getActivity())
                    .onContextItemSelected(item);
        } else {
            return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
