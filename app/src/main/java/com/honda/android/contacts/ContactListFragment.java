package com.honda.android.contacts;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.honda.android.ConversationActivity;
import com.honda.android.CreateGroupActivity;
import com.honda.android.R;
import com.honda.android.ViewProfileActivity;
import com.honda.android.adapter.NewContactAdapter;
import com.honda.android.communication.MessengerConnectionService;
import com.honda.android.list.utilLoadImage.ImageLoaderFromSD;
import com.honda.android.personalRoom.PersonalRoomActivity;
import com.honda.android.provider.Contact;
import com.honda.android.provider.Interval;
import com.honda.android.provider.IntervalDB;
import com.honda.android.provider.MessengerDatabaseHelper;
import com.honda.android.provider.TimeLineDB;
import com.honda.android.utils.DialogUtil;
import com.honda.android.utils.MediaProcessingUtil;
import com.honda.android.utils.RefreshContactService;
import com.honda.android.utils.ThrowProfileService;
import com.honda.android.utils.Validations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import lb.library.PinnedHeaderListView;

@SuppressLint("ValidFragment")
public class ContactListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    protected int menuId = 0;
    protected int contextMenuId = 0;

    ArrayList<Contact> rowItems;
    private MessengerDatabaseHelper messengerHelper;
    NewContactAdapter adapter;
    private AdapterView.AdapterContextMenuInfo adapterContextSelected;
    private static final String SQL_SELECT_CONTACTS = "SELECT * FROM "
            + Contact.TABLE_NAME + " order by _id DESC";
    private static final String SQL_SELECT_ONE_CONTACTS = "SELECT * FROM " + Contact.TABLE_NAME + " WHERE _id = ";
    //	private static final String SQL_SELECT_CONTACTS_BY_ID = "SELECT * FROM "
//			+ Contact.TABLE_NAME+" where order by _id DESC";
    private BroadcastHandler broadcastHandler = new BroadcastHandler();
    private PinnedHeaderListView lv;
    View rootView;
    private LayoutInflater mInflater, inflater;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Context context;
    TimeLineDB timeLineDB;
    ImageLoaderFromSD imageLoaderSD;

    public ContactListFragment(Context ctx) {
        context=ctx;
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

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.contact, container, false);
        mInflater = LayoutInflater.from(getActivity().getApplicationContext());
        inflater = LayoutInflater.from(getActivity().getApplicationContext());
        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(context);
        }

        imageLoaderSD = new ImageLoaderFromSD(context);

        if (timeLineDB == null) {
            timeLineDB = TimeLineDB.getInstance(context);
        }

        lv = (PinnedHeaderListView) rootView.findViewById(android.R.id.list);

        rowItems = new ArrayList<Contact>();

        lv.setClickable(true);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, final long id) {
                if (position == 0) {
                    Contact item = (Contact) adapter.getItem(position);
                    Intent intent = new Intent(context, PersonalRoomActivity.class);
                    intent.putExtra(PersonalRoomActivity.EXTRA_ID, item.getJabberId());
                    intent.putExtra(PersonalRoomActivity.EXTRA_NAME, item.getRealname());
                    context.startActivity(intent);
                    return;
                }
                Contact item = (Contact) adapter.getItem(position);
                if (item.getJabberId().equalsIgnoreCase("") || item.getJabberId().equalsIgnoreCase(messengerHelper.getMyContact().getJabberId())) {
//						Context context = v.context;
//						Intent intent = new Intent(context, PersonalRoomActivity.class);
//						intent.putExtra(PersonalRoomActivity.EXTRA_ID, messengerHelper.getMyContact());
//						context.startActivity(intent);
                } else {


                    Intent intent = new Intent(getActivity(), ConversationActivity.class);
                    String jabberId = item.getJabberId();
                    String action = getActivity().getIntent().getAction();
                    if (Intent.ACTION_SEND.equals(action)) {
                        Bundle extras = getActivity().getIntent().getExtras();
                        if (extras.containsKey(Intent.EXTRA_STREAM)) {
                            try {
                                Uri uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);
                                String pathToSend = MediaProcessingUtil.getRealPathFromURI(
                                        getActivity().getContentResolver(), uri);
                                intent.putExtra(ConversationActivity.KEY_FILE_TO_SEND,
                                        pathToSend);
                            } catch (Exception e) {
                                Log.e(getClass().getSimpleName(),
                                        "Error getting file from action send: "
                                                + e.getMessage(), e);
                            }
                        }
                    }

                    intent.putExtra(ConversationActivity.KEY_JABBER_ID, jabberId);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        View imageView = v.findViewById(R.id.imagePhoto);
                        Pair<View, String> pair1 = Pair.create(imageView, imageView.getTransitionName());
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(getActivity(), pair1);
                        startActivity(intent, options.toBundle());
                    } else {
                        startActivity(intent);
                    }

                }
            }

        });

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

//        registerForContextMenu(lv);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final Dialog dialog;
                dialog = DialogUtil.customDialogConversation(getActivity());
                dialog.show();

                FrameLayout btnForwardRounded = (FrameLayout) dialog.findViewById(R.id.btnForwardRounded);
                TextView forwardTxt = (TextView) dialog.findViewById(R.id.forwardTxt);
                forwardTxt.setText("View Profile");
                btnForwardRounded.setVisibility(View.VISIBLE);

                btnForwardRounded.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                        Contact iconItem = (Contact) rowItems.get(
                                position);
                        Intent i = new Intent(context, ViewProfileActivity.class);
                        i.putExtra(ViewProfileActivity.KEY_JABBER_ID, iconItem.getJabberId());
                        i.putExtra(ViewProfileActivity.KEY_REFERENCE,
                                ViewProfileActivity.REFERENCE_MAIN);
                        startActivity(i);
                    }
                });
                return true;
            }
        });
        return rootView;
    }


    @Override
    public void onRefresh() {
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {

                        IntervalDB db = new IntervalDB(context);
                        db.open();
                        Cursor cursor2 = db.getSingleContact(19);
                        if (cursor2.getCount() > 0) {
                            db.deleteContact(19);
                        }
                        Interval interval = new Interval();
                        interval.setId(19);
                        interval.setTime("refresh");
                        db.createContact(interval);
                        cursor2.close();
                        db.close();
                        context.startService(new Intent(context, RefreshContactService.class));
                    }
                }
        );
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        adapterContextSelected = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Contact iconItem = (Contact) rowItems.get(
                adapterContextSelected.position);
        if (!messengerHelper.getMyContact().getJabberId().equalsIgnoreCase(iconItem.getJabberId())) {
            final Dialog dialog;
            dialog = DialogUtil.customDialogConversation(getActivity());
            dialog.show();

            FrameLayout btnForwardRounded = (FrameLayout) dialog.findViewById(R.id.btnForwardRounded);
            TextView forwardTxt = (TextView) dialog.findViewById(R.id.forwardTxt);
            btnForwardRounded.setVisibility(View.VISIBLE);
            forwardTxt.setText("View Profile");

            btnForwardRounded.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Contact iconItem = (Contact) rowItems.get(
                            adapterContextSelected.position);
                    Intent i = new Intent(context, ViewProfileActivity.class);
                    i.putExtra(ViewProfileActivity.KEY_JABBER_ID, iconItem.getJabberId());
                    i.putExtra(ViewProfileActivity.KEY_REFERENCE,
                            ViewProfileActivity.REFERENCE_MAIN);
                    startActivity(i);
                }
            });
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings_r:
                Intent i2 = new Intent(context, CreateGroupActivity.class);
                startActivity(i2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshContactList();
        IntentFilter f = new IntentFilter(
                MessengerConnectionService.ACTION_STATUS_CHANGED);
        f.addAction(RefreshContactService.ACTION_CONTACT_REFRESHED);
        f.setPriority(1);
        context.registerReceiver(broadcastHandler, f);
    }


    @Override
    public void onPause() {
        context.unregisterReceiver(broadcastHandler);
        super.onPause();
    }

    private void refreshContactList() {
        AsyncTask<Void, Contact, Void> contactLoader = new ContactLoader();
        contactLoader.execute();
    }

    private void refreshOneContactList() {
        String jid = "";
        Cursor c = timeLineDB.getDataByFlag();
        if (c.getCount() > 0) {
            jid = c.getString(c.getColumnIndexOrThrow(TimeLineDB.TIMELINE_JID));
        }

        for (int i = 0; i < lv.getCount(); i++) {
            if (rowItems.get(i).getJabberId().equalsIgnoreCase(jid)) {
                updateItemAtPosition(i, jid);
            }
        }
    }

    class ContactLoader extends AsyncTask<Void, Contact, Void> {
        ArrayList<Contact> arrayListContact = new ArrayList<Contact>();
        Contact arrayListContactMe = new Contact();

        @Override
        protected Void doInBackground(Void... params) {
            HashMap<Long, Contact> dbMap = loadContactFromDb();
            if (dbMap.size() > 0) {
                for (Iterator<Long> iterator = dbMap.keySet().iterator(); iterator
                        .hasNext(); ) {
                    Long l = iterator.next();
                    Contact c = dbMap.get(l);
                    publishProgress(c);
                }
            } else {
                publishProgress(new Contact[]{null});
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Contact... values) {
            Contact data = values[0];
            if (data != null) {
                if (data.getId() != 1) {
                    arrayListContact.add(data);
                } else {
                    arrayListContactMe = data;
                }
            } else {
                refreshList();
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            rowItems.clear();
            rowItems.add(arrayListContactMe);
            Collections.sort(arrayListContact, nameSortComparator);
            for (Contact itemss : arrayListContact) {
                rowItems.add(itemss);
            }

            refreshList();
        }

    }

    public static Comparator<Contact> nameSortComparator = new Comparator<Contact>() {

        public int compare(Contact s1, Contact s2) {
            String IconItem1 = s1.getName();
            String IconItem2 = s2.getName();

            return IconItem1.compareTo(IconItem2);
        }

    };

    public void refreshList() {

        new Handler(Looper.getMainLooper()).post(new Runnable() { // Tried new Handler(Looper.myLopper()) also
            @Override
            public void run() {


                adapter = new NewContactAdapter(context, rowItems, false, new Validations().getInstance(context).colorTheme(false));
                TypedValue typedValue = new TypedValue();
                Resources.Theme theme = context.getTheme();
                theme.resolveAttribute(R.attr.background, typedValue, true);
                int pinnedHeaderBackgroundColor = typedValue.data;

                adapter.setPinnedHeaderBackgroundColor(pinnedHeaderBackgroundColor);
                adapter.setPinnedHeaderTextColor(context.getResources().getColor(R.color.colorPrimary));
                lv.setPinnedHeaderView(mInflater.inflate(R.layout.pinned_header_listview_side_header_divider, lv, false));
                lv.setOnScrollListener(adapter);
                lv.setEnableHeaderTransparencyChanges(false);
                lv.setAdapter(adapter);

                timeLineDB.updateAllUserFlag();
        /*if (adapter != null) {
            adapter.notifyDataSetChanged();
		}*/
            }
        });
    }

    private HashMap<Long, Contact> loadContactFromDb() {
        Contact contact = new Contact();
        Cursor cursor = messengerHelper.query(SQL_SELECT_CONTACTS, null);

        HashMap<Long, Contact> dbMap = new HashMap<Long, Contact>();
        while (cursor.moveToNext()) {

            contact = new Contact(cursor);
            dbMap.put(Long.valueOf(contact.getJabberId()), contact);
        }
        cursor.close();
        return dbMap;
    }

    class BroadcastHandler extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MessengerConnectionService.ACTION_STATUS_CHANGED
                    .equals(intent.getAction())) {
                refreshOneContactList();

                Intent i = new Intent(getActivity(), ThrowProfileService.class);
                i.putExtra("broadcast", MessengerConnectionService.ACTION_STATUS_CHANGED_CONTACT);
                getActivity().startService(i);

            } else if (RefreshContactService.ACTION_CONTACT_REFRESHED.equals(intent.getAction())) {
                refreshContactList();
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private void updateItemAtPosition(int position, String userid) {
        Contact c = messengerHelper.getContact(userid);
        rowItems.set(position, c);
        adapter.notifyDataSetChanged();
    }
}
