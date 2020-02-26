package com.byonchat.android.ui.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.util.Pair;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.byonchat.android.ConversationActivity;
import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.createMeme.FilteringImage;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.list.IconItem;
import com.byonchat.android.local.Byonchat;
import com.byonchat.android.provider.ChatParty;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.Message;
import com.byonchat.android.ui.adapter.ItemImsListHistoryAdapter;
import com.byonchat.android.ui.adapter.ListHistoryItemClickListener;
import com.byonchat.android.ui.view.ByonchatRecyclerView;
import com.byonchat.android.ui.view.ScrollListener;
import com.byonchat.android.utils.DialogUtil;
import com.byonchat.android.utils.ThrowProfileService;
import com.byonchat.android.utils.Validations;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.byonchat.android.utils.Utility.SQL_SELECT_TOTAL_MESSAGES_UNREAD;
import static com.byonchat.android.utils.Utility.dateInfoFormat;
import static com.byonchat.android.utils.Utility.hourInfoFormat;

public abstract class ImsBaseListHistoryChatActivity extends AppCompatActivity implements ActionMode.Callback {

    protected List<IconItem> iconItemList = new ArrayList<>();
    protected List<IconItem> messageItemList = new ArrayList<>();

    protected ItemImsListHistoryAdapter mAdapter;
    protected ItemImsListHistoryAdapter mMessageAdapter;

    protected LinearLayoutManager chatLayoutManager;
    protected BroadcastHandler broadcastHandler = new BroadcastHandler();
    protected ActionMode actionMode;
    protected String mColor, mColorText;

    @NonNull
    protected AppBarLayout vAppBar;

    @NonNull
    protected Toolbar vToolbar;

    @NonNull
    protected ByonchatRecyclerView vListHistory;

    @NonNull
    protected FrameLayout vFrameChatLists;

    @NonNull
    protected FrameLayout vFrameMessageLists;

    @NonNull
    protected ByonchatRecyclerView vListHistoryFind;

    @NonNull
    protected SearchView vSearchEdt;

    @NonNull
    protected LinearLayout vFrameSearch;

    @NonNull
    protected RelativeLayout vToolbarBack;

    @NonNull
    protected MaterialSearchView vSearchView;

    @NonNull
    protected TextView vToolbarTitle;

    @NonNull
    protected ImageView vImgToolbarBack;

    @NonNull
    protected FloatingActionButton vBtnCreateMessage;

    @NonNull
    protected ScrollView vScrollView;

    @NonNull
    protected NestedScrollView vNestedScroll;

    @NonNull
    protected RelativeLayout vFrameBottom;

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
        mColor = getIntent().getStringExtra(Constants.EXTRA_COLOR);
        mColorText = getIntent().getStringExtra(Constants.EXTRA_COLORTEXT);
        if (mColor == null && mColorText == null && savedInstanceState != null) {
            mColor = savedInstanceState.getString(Constants.EXTRA_COLOR);
            mColorText = savedInstanceState.getString(Constants.EXTRA_COLORTEXT);
        }

        if (mColor == null && mColorText == null) {
            finish();
            return;
        }
    }

    protected void resolveToolbar() {
        setSupportActionBar(vToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FilteringImage.SystemBarBackground(getWindow(), Color.parseColor("#" + mColor));
        vToolbar.setBackgroundColor(Color.parseColor("#" + mColor));
        vToolbar.setTitleTextColor(Color.parseColor("#" + mColorText));
        vToolbarTitle.setTextColor(Color.parseColor("#" + mColorText));

        Drawable mDrawable;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            mDrawable = getResources().getDrawable(R.drawable.ic_keyboard_arrow_left24);
        else
            mDrawable = getResources().getDrawable(R.drawable.ic_keyboard_arrow_left_black_24dp);
        mDrawable.setColorFilter(Color.parseColor("#" + mColorText), PorterDuff.Mode.SRC_ATOP);
        vImgToolbarBack.setImageDrawable(mDrawable);

        vToolbarBack.setOnClickListener(v -> {
            if (vSearchView.isSearchOpen()) {
                vSearchView.closeSearch();
            } else {
                super.onBackPressed();
            }
        });
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

    protected void resolveOriginView(boolean isVisible) {
        vFrameChatLists.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        vFrameMessageLists.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        vListHistoryFind.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    protected void resolveMaterialSearchView() {
        vSearchView.setHint("Search ...");

        vSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                View view = findViewById(R.id.main_search);
                view.setVisibility(View.GONE);

                if (query.length() == 0) {
                    resolveOriginView(false);
                } else {
                    resolveOriginView(true);
                }
                if (iconItemList.size() > 0)
                    mAdapter.getFilter().filter(query);

                if (messageItemList.size() > 0)
                    mMessageAdapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                View view = findViewById(R.id.main_search);
                view.setVisibility(View.GONE);

                if (query.length() == 0) {
                    resolveOriginView(false);
                } else {
                    resolveOriginView(true);
                }
                if (iconItemList.size() > 0)
                    mAdapter.getFilter().filter(query);

                if (messageItemList.size() > 0)
                    mMessageAdapter.getFilter().filter(query);
                return true;
            }
        });

        vSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                View view = findViewById(R.id.main_search);
                view.setVisibility(View.GONE);
            }

            @Override
            public void onSearchViewClosed() {
                View view = findViewById(R.id.main_search);
                view.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ims_menu_main_search, menu);
        MenuItem item = menu.findItem(R.id.main_search);
        Drawable yourdrawable = item.getIcon();
        yourdrawable.mutate();
        yourdrawable.setColorFilter(Color.parseColor("#" + mColorText), PorterDuff.Mode.SRC_IN);
        vSearchView.setMenuItem(item);
        if (vSearchView.isSearchOpen()) {
            resolveOriginView(false);
            item.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
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
                    IconItem item = (IconItem) mAdapter.getData().get(position);
                    if (item.getJabberId().equalsIgnoreCase("")) {
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ConversationActivity.class);
                        String jabberId = item.getJabberId();
                        intent.putExtra(ConversationActivity.KEY_JABBER_ID, jabberId);
                        intent.putExtra(Constants.EXTRA_COLOR, mColor);
                        intent.putExtra(Constants.EXTRA_COLORTEXT, mColorText);

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
                IconItem item = (IconItem) mAdapter.getData().get(position);
                if (item.getJabberId().equalsIgnoreCase("")) {
                } else {
                    Intent intent = new Intent(getApplicationContext(), ConversationActivity.class);
                    String jabberId = item.getJabberId();
                    intent.putExtra(ConversationActivity.KEY_JABBER_ID, jabberId);
                    intent.putExtra(Constants.EXTRA_COLOR, mColor);
                    intent.putExtra(Constants.EXTRA_COLORTEXT, mColorText);

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

    protected void resolveListHistoryFind() {
        messageItemList = new ArrayList<>();
        vListHistoryFind.setUpAsList();
        vListHistoryFind.setNestedScrollingEnabled(false);
        chatLayoutManager = (LinearLayoutManager) vListHistoryFind.getLayoutManager();
        mMessageAdapter = new ItemImsListHistoryAdapter(getApplicationContext(), messageItemList, new ListHistoryItemClickListener() {
            @Override
            public void onItemListClick(View view, int position) {
                if (mMessageAdapter.getSelectedComments().isEmpty()) {
                    IconItem item = (IconItem) mMessageAdapter.getData().get(position);
                    if (item.getJabberId().equalsIgnoreCase("")) {
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ConversationActivity.class);
                        String jabberId = item.getJabberId();
                        intent.putExtra(ConversationActivity.KEY_JABBER_ID, jabberId);
                        intent.putExtra(Constants.EXTRA_ITEM, item);
                        intent.putExtra(Constants.EXTRA_COLOR, mColor);
                        intent.putExtra(Constants.EXTRA_COLORTEXT, mColorText);
                        startActivity(intent);
                    }
                } else {
                    adapterSelected((IconItem) mMessageAdapter.getData().get(position));
                }
            }

            @Override
            public void onItemListLongClick(View view, int position) {
                adapterSelected((IconItem) mMessageAdapter.getData().get(position));
            }
        });

        mMessageAdapter.setOnItemClickListener((view, position) -> {
            if (mMessageAdapter.getSelectedComments().isEmpty()) {
                IconItem item = (IconItem) mMessageAdapter.getData().get(position);
                if (item.getJabberId().equalsIgnoreCase("")) {
                } else {
                    Intent intent = new Intent(getApplicationContext(), ConversationActivity.class);
                    String jabberId = item.getJabberId();
                    intent.putExtra(ConversationActivity.KEY_JABBER_ID, jabberId);
                    intent.putExtra(Constants.EXTRA_ITEM, item);
                    intent.putExtra(Constants.EXTRA_COLOR, mColor);
                    intent.putExtra(Constants.EXTRA_COLORTEXT, mColorText);
                    startActivity(intent);
                }
            } else
                adapterSelected((IconItem) mMessageAdapter.getData().get(position));
        });

        mMessageAdapter.setOnLongItemClickListener((view, position) -> {
            adapterSelected((IconItem) mMessageAdapter.getData().get(position));
        });

        vListHistoryFind.setAdapter(mMessageAdapter);

        vListHistoryFind.addOnScrollListener(new ScrollListener() {
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

        vBtnCreateMessage.hide();
    }

    protected void showViews() {
        vFrameSearch.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));

        vBtnCreateMessage.show();
    }

    protected void resolveCreateMessage() {
        onScroll();

        vBtnCreateMessage.setOnClickListener(v -> {
            Intent intent = SelectMessageContactActivity.generateIntent(this, mColor, mColorText);
            startActivity(intent);
        });
    }

    protected void resolveBottomFrame() {
        vFrameBottom.setVisibility(iconItemList.size() > 0 ? View.VISIBLE : View.GONE);
    }

    @TargetApi(23)
    protected void onScroll() {
//        vNestedScroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//
//                if (scrollY > oldScrollY) {
//                    hideViews();
//                }
//                if (scrollY < oldScrollY) {
//                    showViews();
//                }
//            }
//        });
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
        if (i == R.id.action_delete && selectedItem.size() > 0) {
            deleteMessages(selectedItem);
        }
        mode.finish();
    }

    protected void deleteMessages(List<IconItem> selectedItem) {
        final List<String> jabId = new ArrayList<>();
        for (int i = selectedItem.size() - 1; i >= 0; i--) {
            IconItem item = selectedItem.get(i);
            jabId.add(item.getJabberId());
        }

        String title = "Confirm Delete";
        String message = "Once you delete your chat history you won't be able to get it back. Delete?";

        final Dialog dialogConfirmation;
        dialogConfirmation = DialogUtil.customDialogConversationConfirmation(this);
        dialogConfirmation.show();

        TextView txtConfirmation = dialogConfirmation.findViewById(R.id.confirmationTxt);
        TextView descConfirmation = dialogConfirmation.findViewById(R.id.confirmationDesc);
        txtConfirmation.setText(title);
        descConfirmation.setVisibility(View.VISIBLE);
        descConfirmation.setText(message);

        Button btnNo = dialogConfirmation.findViewById(R.id.btnNo);
        Button btnYes = dialogConfirmation.findViewById(R.id.btnYes);

        btnNo.setOnClickListener(v -> {
            dialogConfirmation.dismiss();
        });

        btnYes.setOnClickListener(v -> {
            for (String jj : jabId) {
                Byonchat.getMessengerHelper()
                        .deleteRows(
                                Message.TABLE_NAME,
                                " destination=? OR source =? ",
                                new String[]{jj,
                                        jj}
                        );
                Intent intent = new Intent(MainBaseActivityNew.ACTION_REFRESH_NOTIF);
                sendBroadcast(intent);
            }
            resolveChatHistory();
            dialogConfirmation.dismiss();
        });
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

    protected void resolveChatHistory() {
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
        } else {
            iconItemList = new ArrayList<>();
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
            iconItem.type = IconItem.TYPE_ORIGIN;
            if (cparty instanceof Contact) {
                // setProfilePicture(iconItem, (Contact) cparty);
            }
            iconItemList.add(iconItem);
        }

        if (mAdapter != null) {
            mAdapter.setItems(iconItemList);
        }

        resolveBottomFrame();
    }

    public class LoadMessageList extends AsyncTask<String, Void, List<IconItem>> {
        List<IconItem> iconItems = new ArrayList<>();

        @Override
        protected List<IconItem> doInBackground(String... params) {
            // TODO Auto-generated method stub
            Cursor cursor;

            String myJabberId = Byonchat.getMessengerHelper().getMyContact().getJabberId();
            cursor = Byonchat.getMessengerHelper().query(
                    getString(R.string.sql_chat_list_find),
                    new String[]{myJabberId, Message.TYPE_READSTATUS, myJabberId, Message.TYPE_READSTATUS});

            int indexName = cursor.getColumnIndex(Contact.NAME);
            int indexJabberId = cursor.getColumnIndex(Message.NUMBER);
            int indexMessage = cursor.getColumnIndex(Message.MESSAGE);
            int idMessage = cursor.getColumnIndex(Message._ID);

            Calendar cal = Calendar.getInstance();
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    cal.get(Calendar.DATE), 0, 0, 0);
            if (cursor.getCount() > 0) {
                iconItems.clear();
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
                iconItem.type = IconItem.TYPE_MESSAGE_FIND;

                iconItems.add(iconItem);
            }
            return iconItems;
        }

        @Override
        protected void onPostExecute(List<IconItem> iconItems) {
            super.onPostExecute(iconItems);

            if (mMessageAdapter != null) {
                mMessageAdapter.setItems(iconItems);
            }
        }
    }


    protected void resolveChatHistorySearch() {
        new LoadMessageList().execute();
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
                resolveChatHistory();
                resolveChatHistorySearch();
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
            item.type = IconItem.TYPE_ORIGIN;
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
        int index = iconItemList.indexOf(item);
        if (index != -1) {
            iconItemList.remove(index);
        }
        iconItemList.add(0, item);
        mAdapter.notifyDataSetChanged();
    }

    @NonNull
    protected abstract AppBarLayout getAppbar();

    @NonNull
    protected abstract Toolbar getToolbar();

    @NonNull
    protected abstract ByonchatRecyclerView getListHistory();

    @NonNull
    protected abstract ByonchatRecyclerView getListHistoryFind();

    @NonNull
    protected abstract SearchView getSearchView();

    @NonNull
    protected abstract ScrollView getScrollView();

    @NonNull
    protected abstract NestedScrollView getNestedScrollView();

    @NonNull
    protected abstract RelativeLayout getBottomFrame();

    @NonNull
    protected abstract FrameLayout getFrameChatLists();

    @NonNull
    protected abstract FrameLayout getFrameMessageLists();

    @NonNull
    protected abstract MaterialSearchView getMaterialSearchView();

    @NonNull
    protected abstract LinearLayout getFrameSearch();

    @NonNull
    protected abstract RelativeLayout getToolbarBack();

    @NonNull
    protected abstract FloatingActionButton getFloatingButtonCreateMsg();

    @NonNull
    protected abstract ImageView getImgToolbarBack();

    @NonNull
    protected abstract TextView getToolbarTitle();

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.EXTRA_COLOR, mColor);
        outState.putString(Constants.EXTRA_COLORTEXT, mColorText);
    }
}
