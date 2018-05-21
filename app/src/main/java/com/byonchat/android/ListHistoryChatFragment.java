package com.byonchat.android;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.Listener.RecyclerClick_Listener;
import com.byonchat.android.Listener.RecyclerTouchListener;
import com.byonchat.android.Listener.Toolbar_ActionMode_Callback;
import com.byonchat.android.adapter.ContactAdapter;
import com.byonchat.android.adapter.HistoryChatAdapter;
import com.byonchat.android.adapter.UpdateContactAdapter;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.communication.NotificationReceiver;
import com.byonchat.android.list.IconItem;
import com.byonchat.android.personalRoom.FragmentMyNews;
import com.byonchat.android.personalRoom.adapter.NewsFeedListAdapter;
import com.byonchat.android.personalRoom.model.NewsFeedItem;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.ChatParty;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.Group;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.TimeLine;
import com.byonchat.android.provider.TimeLineDB;
import com.byonchat.android.utils.DialogUtil;
import com.byonchat.android.utils.MediaProcessingUtil;
import com.byonchat.android.utils.OnLoadMoreListener;
import com.byonchat.android.utils.RequestKeyTask;
import com.byonchat.android.utils.TaskCompleted;
import com.byonchat.android.utils.ThrowProfileService;
import com.byonchat.android.utils.Validations;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@SuppressLint("ValidFragment")
public class ListHistoryChatFragment extends Fragment implements HistoryChatAdapter.ContactAdapterListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    Activity activity;
    Context context;
    private ArrayList<IconItem> contactList;
    private RecyclerView recyclerView;
    private HistoryChatAdapter mAdapter;
    private ActionMode actionMode;
    private ActionModeCallback actionModeCallback;

    private static final SimpleDateFormat dateInfoFormat = new SimpleDateFormat(
            "dd/MM/yyyy", Locale.getDefault());
    private static final SimpleDateFormat hourInfoFormat = new SimpleDateFormat(
            "HH:mm", Locale.getDefault());
    private static String SQL_SELECT_TOTAL_MESSAGES_UNREAD = "SELECT count(*) total FROM "
            + Message.TABLE_NAME
            + " WHERE ("
            + Message.DESTINATION
            + "=? OR "
            + Message.SOURCE + "=? )"
            + " AND status = 12";

    private BroadcastHandler broadcastHandler = new BroadcastHandler();
    private MessengerDatabaseHelper messengerHelper;


    @Override
    public void onResume() {
        super.onResume();
        listChatHistory();
        ((NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE))
                .cancel(NotificationReceiver.NOTIFY_ID);

        IntentFilter f = new IntentFilter(
                MessengerConnectionService.ACTION_MESSAGE_RECEIVED);
        f.addAction(MessengerConnectionService.ACTION_MESSAGE_DELIVERED);
        f.addAction(MessengerConnectionService.ACTION_MESSAGE_SENT);
        f.addAction(MessengerConnectionService.ACTION_MESSAGE_FAILED);
        f.addAction(MessengerConnectionService.ACTION_REFRESH_CHAT_HISTORY);
        f.addAction(MessengerConnectionService.ACTION_STATUS_CHANGED_CONTACT);
        f.setPriority(2);
        context.registerReceiver(broadcastHandler, f);
    }

    @Override
    public void onPause() {
        super.onPause();
        context.unregisterReceiver(broadcastHandler);

    }

    public ListHistoryChatFragment(Activity activ) {
        context = activ.getApplicationContext();
        activity = activ;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_history_chat, container, false);


        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(context);
        }

        contactList = new ArrayList<>();
        mAdapter = new HistoryChatAdapter(context, contactList, this);
        recyclerView.setAdapter(mAdapter);
        actionModeCallback = new ActionModeCallback();
        return rootView;
    }

    private void enableActionMode(int position) {
        if (actionMode == null) {
            actionMode = ((AppCompatActivity) activity).startSupportActionMode(actionModeCallback);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                activity.getWindow().setStatusBarColor(getResources().getColor(R.color.grayOffTab));
            }
        }
        toggleSelection(position);
    }


    private void toggleSelection(int position) {
        mAdapter.toggleSelection(position);
        int count = mAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
            new Validations().getInstance(context).header(activity.getWindow());
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    public void finishActionMode() {
        if (this.actionMode != null){
            this.actionMode.finish();
            new Validations().getInstance(context).header(activity.getWindow());
        }
    }

    public Boolean finishActionModeBoolean() {
        Boolean tutupActionMode = false;
        if (this.actionMode != null){
            tutupActionMode = true;
            this.actionMode.finish();
            new Validations().getInstance(context).header(activity.getWindow());
        }
        return tutupActionMode;
    }


    @Override
    public void onMessageRowClicked(int position, View view) {
        // verify whether action mode is enabled or not
        // if enabled, change the row state to activated
        if (mAdapter.getSelectedItemCount() > 0) {
            enableActionMode(position);
        } else {

            IconItem item = contactList.get(position);
            if (item.getJabberId().equalsIgnoreCase("")) {
            } else {
                Intent intent = new Intent(context, ConversationActivity.class);
                String jabberId = item.getJabberId();
                intent.putExtra(ConversationActivity.KEY_JABBER_ID, jabberId);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    View imageView = view.findViewById(R.id.imagePhoto);
                    Pair<View, String> pair1 = Pair.create(imageView, imageView.getTransitionName());
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(activity, pair1);
                    startActivity(intent, options.toBundle());
                } else {
                    startActivity(intent);
                }

            }
        }
    }

    @Override
    public void onRowLongClicked(int position) {
        // long press is performed, enable action mode
        enableActionMode(position);
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_history_chat, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    // delete all the selected messages
                    deleteMessages();
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearSelections();
            actionMode = null;
            new Validations().getInstance(context).header(activity.getWindow());
        }
    }

    private void deleteMessages() {
        List<Integer> selectedItemPositions = mAdapter.getSelectedItems();
        final List<String> jabId = new ArrayList<>();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            IconItem item = contactList.get(selectedItemPositions.get(i));
            jabId.add(item.getJabberId());
        }

        String title = "Confirm Delete";
        String message = "Once you delete your chat history you won't be able to get it back. Delete?";

        final Dialog dialogConfirmation;
        dialogConfirmation = DialogUtil.customDialogConversationConfirmation(activity);
        dialogConfirmation.show();

        TextView txtConfirmation = (TextView) dialogConfirmation.findViewById(R.id.confirmationTxt);
        TextView descConfirmation = (TextView) dialogConfirmation.findViewById(R.id.confirmationDesc);
        txtConfirmation.setText(title);
        descConfirmation.setVisibility(View.VISIBLE);
        descConfirmation.setText(message);

        Button btnNo = (Button) dialogConfirmation.findViewById(R.id.btnNo);
        Button btnYes = (Button) dialogConfirmation.findViewById(R.id.btnYes);

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogConfirmation.dismiss();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (String jj : jabId){

                    messengerHelper
                            .deleteRows(
                                    Message.TABLE_NAME,
                                    " destination=? OR source =? ",
                                    new String[]{jj,
                                            jj}
                            );
                    Intent intent = new Intent(MainActivity.ACTION_REFRESH_NOTIF);
                    getContext().sendBroadcast(intent);
                }
                listChatHistory();
                dialogConfirmation.dismiss();
            }
        });


    }

    //Implement item click and long click over recycler view
    private void implementRecyclerViewClickListeners() {
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerClick_Listener() {
            @Override
            public void onClick(View view, int position) {
                //If ActionMode not null select item
                if (actionMode != null)
                    onListItemSelect(position);
            }

            @Override
            public void onLongClick(View view, int position) {
                //Select item on long click
                onListItemSelect(position);
            }
        }));
    }

    //List item select method
    private void onListItemSelect(int position) {
        mAdapter.toggleSelection(position);//Toggle the selection
        boolean hasCheckedItems = mAdapter.getSelectedCount() > 0;//Check if any items are already selected or not
        if (hasCheckedItems && actionMode == null)
            // there are some selected items, start the actionMode
            actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(new Toolbar_ActionMode_Callback(context, mAdapter, contactList));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                activity.getWindow().setStatusBarColor(getResources().getColor(R.color.grayOffTab));
            }
        else if (!hasCheckedItems && actionMode != null)
            // there no selected items, finish the actionMode
            actionMode.finish();
            new Validations().getInstance(context).header(activity.getWindow());
        if (actionMode != null)
            //set action mode title on item selection
            actionMode.setTitle(String.valueOf(mAdapter
                    .getSelectedCount()) + " selected");
    }

    private void listChatHistory() {
        Cursor cursor;

        String myJabberId = messengerHelper.getMyContact().getJabberId();
        cursor = messengerHelper.query(
                context.getString(R.string.sql_chat_list),
                new String[]{myJabberId, Message.TYPE_READSTATUS, myJabberId, Message.TYPE_READSTATUS});

        int indexName = cursor.getColumnIndex(Contact.NAME);
        int indexJabberId = cursor.getColumnIndex("number");
        int indexMessage = cursor.getColumnIndex(Message.MESSAGE);

        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DATE), 0, 0, 0);
        if (cursor.getCount() > 0) {
            contactList.clear();
        }
        while (cursor.moveToNext()) {
            String jabberId = cursor.getString(indexJabberId);
            String name = cursor.getString(indexName);

            String message = cursor.getString(indexMessage);
            if (message == null)
                continue;

            Message vo = new Message(cursor);
            message = Message.parsedMessageBodyHtmlCode(vo, context);
            Date d = null;

            d = vo.getSendDate();

            String dInfo = null;
            if (d != null) {
                if (d.getTime() < cal.getTimeInMillis()) {
                    dInfo = dateInfoFormat.format(d);
                } else {
                    dInfo = hourInfoFormat.format(d);
                }
            }
            ChatParty cparty = null;
            if (vo.isGroupChat()) {
                cparty = messengerHelper.getGroup(jabberId);
            } else {
                if (name != null) {
                    cparty = new Contact(name, jabberId, "");
                } else {
                    name = jabberId;
                }
            }


            long total = 0;
            Cursor cursor2 = messengerHelper.query(
                    SQL_SELECT_TOTAL_MESSAGES_UNREAD,
                    new String[]{jabberId,
                            jabberId});
            int indexTotal = cursor2.getColumnIndex("total");
            while (cursor2.moveToNext()) {
                total = cursor2.getLong(indexTotal);
            }
            cursor2.close();

            String signature =  new Validations().getInstance(getActivity()).getSignatureProfilePicture(jabberId,messengerHelper);

            IconItem iconItem = new IconItem(jabberId, name, message,
                    dInfo, cparty, total, Message.getStatusMessage(vo, myJabberId), signature);
            if (cparty instanceof Contact) {
                // setProfilePicture(iconItem, (Contact) cparty);
            }
            contactList.add(iconItem);
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() { // Tried new Handler(Looper.myLopper()) also
            @Override
            public void run() {

                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    class BroadcastHandler extends BroadcastReceiver {

        @Override
        public void onReceive(Context ctx, Intent intent) {
            if (MessengerConnectionService.ACTION_MESSAGE_RECEIVED
                    .equals(intent.getAction())) {
                Message vo = intent .getParcelableExtra(MessengerConnectionService.KEY_MESSAGE_OBJECT);

                Intent intRefresh = new Intent(MainActivity.ACTION_REFRESH_BADGER);
                activity.sendOrderedBroadcast(intRefresh, null);

                new MessageReceivedHandler().execute(new Message[]{vo});

            } else if (MessengerConnectionService.ACTION_MESSAGE_SENT
                    .equals(intent.getAction())||
                    MessengerConnectionService.ACTION_MESSAGE_FAILED
                    .equals(intent.getAction()) ||
                    MessengerConnectionService.ACTION_MESSAGE_DELIVERED
                    .equals(intent.getAction())) {

                Message vo = intent
                        .getParcelableExtra(MessengerConnectionService.KEY_MESSAGE_OBJECT);
                if (!vo.getType().equalsIgnoreCase(Message.TYPE_READSTATUS)) {
                    updateMessage(vo);
                    Contact c = messengerHelper.getContact(vo.getSource());
                }
            } else if (MessengerConnectionService.ACTION_REFRESH_CHAT_HISTORY
                    .equals(intent.getAction())) {
                listChatHistory();
            } else if (MessengerConnectionService.ACTION_STATUS_CHANGED_CONTACT
                    .equals(intent.getAction())) {
                 refreshOneContactList();
                Intent i = new Intent(context, ThrowProfileService.class);
                i.putExtra("broadcast", MessengerConnectionService.ACTION_STATUS_CHANGED_CONVERSATION);
                context.startService(i);
            }
        }
    }


    private void updateMessage(Message vo){

        if (!vo.getType().equalsIgnoreCase(Message.TYPE_READSTATUS)) {
            String jaberId = vo.getDestination() != messengerHelper.getMyContact().getJabberId() ? vo.getDestination() : vo.getSource();
            for (int i = 0; i < contactList.size(); i++) {
                if (contactList.get(i).getJabberId().equalsIgnoreCase(jaberId)) {
                    IconItem baru = contactList.get(i);
                    baru.setValue(Message.getStatusMessage(vo, messengerHelper.getMyContact().getJabberId()));
                    contactList.set(i,baru);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }
    private void refreshOneContactList() {
        for (IconItem iconItem :contactList){
            String signature =  new Validations().getInstance(getActivity()).getSignatureProfilePicture(iconItem.getJabberId(),messengerHelper);
            if (!signature.equalsIgnoreCase(iconItem.getSignature())){
                iconItem.setSignature(signature);
                contactList.set(contactList.indexOf(iconItem),iconItem);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    class MessageReceivedHandler extends AsyncTask<Message, IconItem, Void> {

        @Override
        protected Void doInBackground(Message... params) {
            Message msg = params[0];
            ChatParty cparty = null;
            String from = msg.getSource();
            if (msg.isGroupChat()) {
                cparty = messengerHelper.getGroup(msg.getSource());
            } else {
                cparty = messengerHelper.getContact(msg.getSource());
                String regex = "[0-9]+";
                //disini ditutup
                // from = "+" + Utility.formatPhoneNumber(msg.getSource());
               /* if (!msg.getSource().matches(regex))*/
                from = msg.getSource();
            }
            if (cparty != null) {
                from = cparty.getName();
            }

            long total = 0;
            Cursor cursor = messengerHelper.query(
                    SQL_SELECT_TOTAL_MESSAGES_UNREAD,
                    new String[]{msg.getSource(),
                            msg.getSource()});
            int indexTotal = cursor.getColumnIndex("total");
            while (cursor.moveToNext()) {
                total = cursor.getLong(indexTotal);
            }
            cursor.close();

            String signature =  new Validations().getInstance(getActivity()).getSignatureProfilePicture(msg.getSource(),messengerHelper);
            IconItem item = new IconItem(msg.getSource(), from,
                    Message.parsedMessageBodyHtmlCode(msg, context), hourInfoFormat.format(msg
                    .getSendDate()), cparty, total, Message.getStatusMessage(msg, messengerHelper.getMyContact().getJabberId()),signature
            );
            if (cparty instanceof Contact) {
                //  setProfilePicture(item, (Contact) cparty);
            }
            publishProgress(item);
            return null;
        }

        @Override
        protected void onProgressUpdate(IconItem... values) {
            super.onProgressUpdate(values);
            moveItemToTop(values[0]);
        }

    }

    public void moveItemToTop(final IconItem item) {

        new Handler(Looper.getMainLooper()).post(new Runnable() { // Tried new Handler(Looper.myLopper()) also
            @Override
            public void run() {
                int index = contactList.indexOf(item);
                if (index != -1) {
                    contactList.remove(index);
                }
                contactList.add(0, item);
                mAdapter.notifyDataSetChanged();
            }
        });
    }



}
