package com.byonchat.android.ui.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.byonchat.android.ConversationActivity;
import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.createMeme.FilteringImage;
import com.byonchat.android.list.IconItem;
import com.byonchat.android.local.Byonchat;
import com.byonchat.android.provider.ChatParty;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.Message;
import com.byonchat.android.ui.adapter.ItemImsListHistoryAdapter;
import com.byonchat.android.ui.adapter.ListHistoryItemClickListener;
import com.byonchat.android.ui.view.ByonchatRecyclerView;
import com.byonchat.android.ui.view.ScrollListener;
import com.byonchat.android.utils.ThrowProfileService;
import com.byonchat.android.utils.Validations;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.byonchat.android.utils.Utility.SQL_SELECT_TOTAL_MESSAGES_UNREAD;
import static com.byonchat.android.utils.Utility.dateInfoFormat;
import static com.byonchat.android.utils.Utility.hourInfoFormat;

public abstract class ImsBaseListHistoryChatActivity extends AppCompatActivity implements ActionMode.Callback {

    protected static final String ARGS = "args";

    protected List<IconItem> iconItemList = new ArrayList<>();

    protected LinearLayoutManager chatLayoutManager;
    protected ItemImsListHistoryAdapter mAdapter;
    protected BroadcastHandler broadcastHandler = new BroadcastHandler();
    protected ActionMode actionMode;
    protected String mArgs;

    @NonNull
    protected AppBarLayout vAppBar;
    @NonNull
    protected Toolbar vToolbar;
    @NonNull
    protected ByonchatRecyclerView vListHistory;
    @NonNull
    protected SearchView vSearchEdt;
    @NonNull
    protected LinearLayout vFrameSearch;
    @NonNull
    protected RelativeLayout vToolbarBack;
    @NonNull
    protected TextView vToolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onSetStatusBarColor();
        setContentView(getResourceLayout());
        onLoadView();
        onViewReady(savedInstanceState);
    }

    protected void onSetStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
    }

    protected abstract int getResourceLayout();

    protected abstract void onLoadView();

    protected void applyChatConfig() {

    }

    protected void onViewReady(Bundle savedInstanceState) {
        resolveChatRoom(savedInstanceState);

        applyChatConfig();
    }

    protected void resolveChatRoom(Bundle savedInstanceState) {
        mArgs = getIntent().getStringExtra(ARGS);
        if (mArgs == null && savedInstanceState != null) {
            mArgs = savedInstanceState.getString(ARGS);
        }

        if (mArgs == null) {
            finish();
            return;
        }
    }

    protected void resolveToolbar() {
        setSupportActionBar(vToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FilteringImage.SystemBarBackground(getWindow(), Color.parseColor("#a4a4a4"));

        vToolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
        vToolbar.setTitleTextColor(getResources().getColor(R.color.ims_chat_list_primary_color));

        vToolbarBack.setOnClickListener(v -> onBackPressed());
        vToolbarTitle.setText("Chat");
    }

    protected void resolveSearchView() {
        vSearchEdt.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                mAdapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
    }

    protected void resolveListHistory() {
        iconItemList = new ArrayList<>();
        vListHistory.setUpAsList();
        vListHistory.setNestedScrollingEnabled(false);
        chatLayoutManager = (LinearLayoutManager) vListHistory.getLayoutManager();
        mAdapter = new ItemImsListHistoryAdapter(getApplicationContext(), iconItemList, new ListHistoryItemClickListener() {
            @Override
            public void onItemListClick(View view, int position) {
                if (mAdapter.getSelectedComments().isEmpty()) {
                    IconItem item = iconItemList.get(position);
                    if (item.getJabberId().equalsIgnoreCase("")) {
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ConversationActivity.class);
                        String jabberId = item.getJabberId();
                        intent.putExtra(ConversationActivity.KEY_JABBER_ID, jabberId);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            View imageView = view.findViewById(R.id.imagePhoto);
                            Pair<View, String> pair1 = Pair.create(imageView, imageView.getTransitionName());
                            ActivityOptionsCompat options = ActivityOptionsCompat.
                                    makeSceneTransitionAnimation(ImsBaseListHistoryChatActivity.this, pair1);
                            startActivity(intent, options.toBundle());
                        } else {
                            startActivity(intent);
                        }

                    }
                } else {
                    adapterSelected((IconItem) mAdapter.getData().get(position));
                }
            }

            @Override
            public void onItemListLongClick(View view, int position) {
                adapterSelected((IconItem) mAdapter.getData().get(position));
            }
        });

        mAdapter.setOnItemClickListener((view, position) -> {
            if (mAdapter.getSelectedComments().isEmpty()) {
                IconItem item = iconItemList.get(position);
                if (item.getJabberId().equalsIgnoreCase("")) {
                } else {
                    Intent intent = new Intent(getApplicationContext(), ConversationActivity.class);
                    String jabberId = item.getJabberId();
                    intent.putExtra(ConversationActivity.KEY_JABBER_ID, jabberId);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        View imageView = view.findViewById(R.id.imagePhoto);
                        Pair<View, String> pair1 = Pair.create(imageView, imageView.getTransitionName());
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(ImsBaseListHistoryChatActivity.this, pair1);
                        startActivity(intent, options.toBundle());
                    } else {
                        startActivity(intent);
                    }

                }
            } else
                adapterSelected((IconItem) mAdapter.getData().get(position));
        });

        mAdapter.setOnLongItemClickListener((view, position) -> {
            adapterSelected((IconItem) mAdapter.getData().get(position));
        });

        vListHistory.setAdapter(mAdapter);

        vListHistory.addOnScrollListener(new ScrollListener() {
            @Override
            public void onHide() {
                hideViews();
            }

            @Override
            public void onShow() {
                showViews();
            }
        });
    }

    protected void resolveSearchMenu(Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                mAdapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
    }

    protected void hideViews() {
        vFrameSearch.animate().translationY(-vFrameSearch.getHeight()).setInterpolator(new AccelerateInterpolator(2));
    }

    protected void showViews() {
        vFrameSearch.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }

    protected void adapterSelected(IconItem item) {
        item.setSelected(!item.isSelected());
        mAdapter.addOrUpdate(item);
        onContactSelected(mAdapter.getSelectedComments());
    }

    public void onContactSelected(List<IconItem> selectedItem) {
        int total = selectedItem.size();
        boolean hasCheckedItems = total > 0;
        if (hasCheckedItems && actionMode == null) {
            actionMode = startSupportActionMode(this);
        } else if (!hasCheckedItems && actionMode != null) {
            actionMode.finish();
        }
        if (actionMode != null) {
            actionMode.setTitle(String.valueOf(selectedItem.size()));
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.menu_history_chat, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        menu.findItem(R.id.action_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        mode.finish();
        onSelectedCommentsAction(mode, item, mAdapter.getSelectedComments());
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mAdapter.clearSelectedComments();
        actionMode = null;
    }

    protected void onSelectedCommentsAction(ActionMode mode, MenuItem item, List<IconItem> selectedItem) {
        int i = item.getItemId();
        mode.finish();
    }

    protected void updateMessage(Message vo) {

        if (!vo.getType().equalsIgnoreCase(Message.TYPE_READSTATUS)) {
            String jaberId = vo.getDestination() != Byonchat.getMessengerHelper().getMyContact().getJabberId() ? vo.getDestination() : vo.getSource();
            for (int i = 0; i < iconItemList.size(); i++) {
                if (iconItemList.get(i).getJabberId().equalsIgnoreCase(jaberId)) {
                    IconItem baru = iconItemList.get(i);
                    baru.setValue(Message.getStatusMessage(vo, Byonchat.getMessengerHelper().getMyContact().getJabberId()));
                    iconItemList.set(i, baru);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    protected void listChatHistory() {
        Cursor cursor;

        String myJabberId = Byonchat.getMessengerHelper().getMyContact().getJabberId();
        cursor = Byonchat.getMessengerHelper().query(
                getString(R.string.sql_chat_list),
                new String[]{myJabberId, Message.TYPE_READSTATUS, myJabberId, Message.TYPE_READSTATUS});

        int indexName = cursor.getColumnIndex(Contact.NAME);
        int indexJabberId = cursor.getColumnIndex("number");
        int indexMessage = cursor.getColumnIndex(Message.MESSAGE);

        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DATE), 0, 0, 0);
        if (cursor.getCount() > 0) {
            iconItemList.clear();
        }
        while (cursor.moveToNext()) {
            String jabberId = cursor.getString(indexJabberId);
            String name = cursor.getString(indexName);

            String message = cursor.getString(indexMessage);
            if (message == null)
                continue;

            Message vo = new Message(cursor);
            message = Message.parsedMessageBodyHtmlCode(vo, getApplicationContext());
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
                cparty = Byonchat.getMessengerHelper().getGroup(jabberId);
            } else {
                if (name != null) {
                    cparty = new Contact(name, jabberId, "");
                } else {
                    name = jabberId;
                }
            }


            long total = 0;
            Cursor cursor2 = Byonchat.getMessengerHelper().query(
                    SQL_SELECT_TOTAL_MESSAGES_UNREAD,
                    new String[]{jabberId,
                            jabberId});
            int indexTotal = cursor2.getColumnIndex("total");
            while (cursor2.moveToNext()) {
                total = cursor2.getLong(indexTotal);
            }
            cursor2.close();

            String signature = new Validations().getInstance(getApplicationContext()).getSignatureProfilePicture(jabberId, Byonchat.getMessengerHelper());

            IconItem iconItem = new IconItem(jabberId, name, message,
                    dInfo, cparty, total, Message.getStatusMessage(vo, myJabberId), signature);
            iconItem.type = IconItem.TYPE_TEXT;
            if (cparty instanceof Contact) {
                // setProfilePicture(iconItem, (Contact) cparty);
            }
            iconItemList.add(iconItem);
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
                Message vo = intent.getParcelableExtra(MessengerConnectionService.KEY_MESSAGE_OBJECT);

                Intent intRefresh = new Intent(MainBaseActivityNew.ACTION_REFRESH_BADGER);
                sendOrderedBroadcast(intRefresh, null);

                new MessageReceivedHandler().execute(new Message[]{vo});

            } else if (MessengerConnectionService.ACTION_MESSAGE_SENT
                    .equals(intent.getAction()) ||
                    MessengerConnectionService.ACTION_MESSAGE_FAILED
                            .equals(intent.getAction()) ||
                    MessengerConnectionService.ACTION_MESSAGE_DELIVERED
                            .equals(intent.getAction())) {

                Message vo = intent
                        .getParcelableExtra(MessengerConnectionService.KEY_MESSAGE_OBJECT);
                if (!vo.getType().equalsIgnoreCase(Message.TYPE_READSTATUS)) {
                    updateMessage(vo);
                    Contact c = Byonchat.getMessengerHelper().getContact(vo.getSource());
                }
            } else if (MessengerConnectionService.ACTION_REFRESH_CHAT_HISTORY
                    .equals(intent.getAction())) {
                listChatHistory();
            } else if (MessengerConnectionService.ACTION_STATUS_CHANGED_CONTACT
                    .equals(intent.getAction())) {
                refreshOneContactList();
                Intent i = new Intent(getApplicationContext(), ThrowProfileService.class);
                i.putExtra("broadcast", MessengerConnectionService.ACTION_STATUS_CHANGED_CONVERSATION);
                startService(i);
            }
        }
    }

    protected void refreshOneContactList() {
        for (IconItem iconItem : iconItemList) {
            String signature = new Validations().getInstance(getApplicationContext()).getSignatureProfilePicture(iconItem.getJabberId(), Byonchat.getMessengerHelper());
            if (!signature.equalsIgnoreCase(iconItem.getSignature())) {
                iconItem.setSignature(signature);
                iconItemList.set(iconItemList.indexOf(iconItem), iconItem);
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
                cparty = Byonchat.getMessengerHelper().getGroup(msg.getSource());
            } else {
                cparty = Byonchat.getMessengerHelper().getContact(msg.getSource());
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
            Cursor cursor = Byonchat.getMessengerHelper().query(
                    SQL_SELECT_TOTAL_MESSAGES_UNREAD,
                    new String[]{msg.getSource(),
                            msg.getSource()});
            int indexTotal = cursor.getColumnIndex("total");
            while (cursor.moveToNext()) {
                total = cursor.getLong(indexTotal);
            }
            cursor.close();

            String signature = new Validations().getInstance(getApplicationContext()).getSignatureProfilePicture(msg.getSource(), Byonchat.getMessengerHelper());
            IconItem item = new IconItem(msg.getSource(), from,
                    Message.parsedMessageBodyHtmlCode(msg, getApplicationContext()), hourInfoFormat.format(msg
                    .getSendDate()), cparty, total, Message.getStatusMessage(msg, Byonchat.getMessengerHelper().getMyContact().getJabberId()), signature
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
                int index = iconItemList.indexOf(item);
                if (index != -1) {
                    iconItemList.remove(index);
                }
                iconItemList.add(0, item);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @NonNull
    protected abstract AppBarLayout getAppbar();

    @NonNull
    protected abstract Toolbar getToolbar();

    @NonNull
    protected abstract ByonchatRecyclerView getListHistory();

    @NonNull
    protected abstract SearchView getSearchView();

    @NonNull
    protected abstract LinearLayout getFrameSearch();

    @NonNull
    protected abstract RelativeLayout getToolbarBack();

    @NonNull
    protected abstract TextView getToolbarTitle();

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARGS, mArgs);
    }
}
