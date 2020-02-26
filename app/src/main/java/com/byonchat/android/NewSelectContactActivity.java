package com.byonchat.android;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.core.util.LogWriter;
import androidx.core.view.MenuItemCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.byonchat.android.adapter.NewContactAdapter;
import com.byonchat.android.createMeme.FilteringImage;
import com.byonchat.android.personalRoom.PersonalRoomActivity;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.IntervalDB;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.Skin;
import com.byonchat.android.utils.ImageFilePath;
import com.byonchat.android.utils.Validations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import lb.library.PinnedHeaderListView;

public class NewSelectContactActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    Context context;
    Toolbar toolbar;
    TextView emptyList;
    EditText searchEditText;
    private ListView goneLv;
    private PinnedHeaderListView lv;
    IntervalDB intervalDB;
    private int color = 0;
    private Contact contact;
    SearchView mSearchView;
    private String colorAttachment = "#005982";
    private MessengerDatabaseHelper messengerHelper;
    private LinearLayout linearTrending;
    private SwipeRefreshLayout swipeRefreshLayout;

    NewContactAdapter adapter;
    private AdapterView.AdapterContextMenuInfo adapterContextSelected;
    private static final String SQL_SELECT_CONTACTS = "SELECT * FROM "
            + Contact.TABLE_NAME + " order by _id DESC";
    ArrayList<Contact> rowItems;

    private LayoutInflater mInflater;

    private String action;
    private String type;
    Intent getintent;
    String memeFile;
    String messageText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_search_room);

        getintent = getIntent();
        action = getintent.getAction();
        type = getintent.getType();
        memeFile = getintent.getStringExtra("file") != null ? getintent.getStringExtra("file") : "";
        messageText = getintent.getStringExtra("messageText") != null ? getintent.getStringExtra("messageText") : "";

        if (type == null) {
            type = getintent.getStringExtra("type") != null ? getintent.getStringExtra("type") : "";
        }


        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(context);
        }

        if (context != null) {
            context = getApplicationContext();
        }

        contact = messengerHelper.getMyContact();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        IntervalDB db = new IntervalDB(this);
        db.open();
        Cursor cursorSelect = db.getSingleContact(4);
        if (cursorSelect.getCount() > 0) {
            String skin = cursorSelect.getString(cursorSelect.getColumnIndexOrThrow(IntervalDB.COL_TIME));
            Skin skins = null;
            Cursor c = db.getCountSkin();
            if (c.getCount() > 0) {
                skins = db.retriveSkinDetails(skin);
                colorAttachment = skins.getColor();
                initBackground(Color.parseColor(colorAttachment));
            }
            c.close();
        }
        cursorSelect.close();
        db.close();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.parseColor(colorAttachment));
        }

        goneLv = (ListView) findViewById(R.id.gonelist);
        goneLv.setVisibility(View.GONE);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        mInflater = LayoutInflater.from(getApplicationContext());
        lv = (PinnedHeaderListView) findViewById(R.id.list);

        emptyList = (TextView) findViewById(R.id.emptyList);
        linearTrending = (LinearLayout) findViewById(R.id.linearTrending);
        linearTrending.setVisibility(View.GONE);
        lv.setClickable(true);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                finish();
                Contact item = (Contact) adapter.getItem(position);
                if (item.getJabberId().equalsIgnoreCase("") || item.getJabberId().equalsIgnoreCase(messengerHelper.getMyContact().getJabberId())) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, PersonalRoomActivity.class);
                    intent.putExtra(PersonalRoomActivity.EXTRA_ID, messengerHelper.getMyContact());
                    context.startActivity(intent);
                } else {
                    if (Intent.ACTION_SEND.equals(action) && type != null) {
                        if ("text/plain".equals(type)) {
                            // handleSendText(intent); // Handle text being sent
                        } else if (type.startsWith("image/")) {
                            handleSendImage(getintent, Message.TYPE_IMAGE, item.getJabberId());
                        } else if (type.startsWith("video/")) {
                            handleSendImage(getintent, Message.TYPE_VIDEO, item.getJabberId());
                        }
                    } else if (!memeFile.equalsIgnoreCase("")) {

                        String jabberId = item.getJabberId().toString();
                        Intent intent = new Intent(getApplicationContext(), ConfirmationSendFile.class);
                        intent.putExtra("name", jabberId);
                        intent.putExtra("file", memeFile);
                        intent.putExtra("type", type);
                        startActivity(intent);

                    } else if ("text/plain".equals(type) || "text/select".equals(type)) {
                        String jabberId = item.getJabberId().toString();
                        Intent intent;
                        intent = new Intent(getApplicationContext(), ConversationActivity.class);
                        intent.putExtra(ConversationActivity.KEY_TITLE, "");
                        intent.putExtra(ConversationActivity.KEY_CONVERSATION_TYPE, 0);
                        intent.putExtra(ConversationActivity.KEY_JABBER_ID, jabberId);
                        intent.putExtra(ConversationActivity.KEY_MESSAGE_FORWARD, messageText);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                        finish();

                    } else {
             /*blm material
             String jabberId = item.getJabberId().toString();
              Intent intent = new Intent(this, TransferActivity.class);
              intent.putExtra("number",jabberId);
              startActivity(intent);*/
                    }
                }
            }
        });

        rowItems = new ArrayList<Contact>();

        refreshContactList();
        registerForContextMenu(lv);
    }

    public void handleSendImage(Intent intent, String type, String jabber) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            String selectedImagePath = ImageFilePath.getPath(getApplicationContext(), imageUri);
            Intent intent2 = new Intent(getApplicationContext(), ConfirmationSendFile.class);
            String jabberId = jabber;
            intent2.putExtra("file", selectedImagePath);
            intent2.putExtra("name", jabberId);
            intent2.putExtra("type", type);
            startActivity(intent2);
        }
    }


    @Override
    public void onRefresh() {
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
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
            menu.add(Menu.NONE, 0, 1, "View Profile");
            menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int menuindex = item.getItemId();

                    switch (menuindex) {
                        case 0:
                            Contact iconItem = (Contact) rowItems.get(
                                    adapterContextSelected.position);
                            Intent i = new Intent(getApplicationContext(), ViewProfileActivity.class);
                            i.putExtra(ViewProfileActivity.KEY_JABBER_ID, iconItem.getJabberId());
                            i.putExtra(ViewProfileActivity.KEY_REFERENCE,
                                    ViewProfileActivity.REFERENCE_MAIN);
                            startActivity(i);
                            break;
                        default:
                            break;
                    }
                    return false;
                }
            });
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.activity_slide_in_down, R.anim.activity_slide_out_down);
    }

    private void refreshContactList() {
        AsyncTask<Void, Contact, Void> contactLoader = new ContactLoader();
        contactLoader.execute();
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
                    //arrayListContactMe = data;
                }
            } else {
                refreshList();
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            rowItems.clear();
            // rowItems.add(arrayListContactMe);
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
                boolean a = "text/select".equals(type) ? false : true;
                adapter = new NewContactAdapter(NewSelectContactActivity.this, rowItems, a, new Validations().getInstance(getApplicationContext()).colorTheme(false));
                TypedValue typedValue = new TypedValue();
                Resources.Theme theme = getApplicationContext().getTheme();
                theme.resolveAttribute(R.attr.background, typedValue, true);
                int pinnedHeaderBackgroundColor = typedValue.data;

                adapter.setPinnedHeaderBackgroundColor(pinnedHeaderBackgroundColor);
                adapter.setPinnedHeaderTextColor(getResources().getColor(R.color.colorPrimary));
                lv.setPinnedHeaderView(mInflater.inflate(R.layout.pinned_header_listview_side_header_divider, lv, false));
                lv.setOnScrollListener(adapter);
                lv.setEnableHeaderTransparencyChanges(false);
                lv.setAdapter(adapter);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search_rooms, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setTitle("");

        MenuItemCompat.setOnActionExpandListener(searchItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem menuItem) {
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                        finish();
                        return true;
                    }
                });

        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchItem.expandActionView();
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchEditText = (EditText) mSearchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setFocusable(true);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 0) {
                    refreshContactList();
                } else {
                    adapter.getFilter().filter(s.toString());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return true;
    }

    private void initBackground(int color) {
        Bitmap back_default = FilteringImage.headerColor(getWindow(), this, color);
        Drawable back_draw_default = new BitmapDrawable(getResources(), back_default);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            toolbar.setBackground(back_draw_default);
        } else {
            toolbar.setBackgroundDrawable(back_draw_default);
        }
    }
}
