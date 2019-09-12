package com.honda.android;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.honda.android.list.IconAdapter;
import com.honda.android.list.IconAdapter.ItemClickListener;
import com.honda.android.list.IconItem;
import com.honda.android.provider.Contact;
import com.honda.android.provider.MessengerDatabaseHelper;
import com.honda.android.utils.MediaProcessingUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GroupAddPickUserActivity extends ABNextActivity implements
        OnItemClickListener {
    public static final String EXTRA_KEY_GROUP_PARTICIPANTS = "com.honda.android.GroupAddInviteUsersActivity.GROUP_PARTICIPANTS";
    public static final String EXTRA_KEY_GROUP_IGNORE_CONTACT = "com.honda.android.GroupAddInviteUsersActivity.GROUP_IGNORE_CONTACT";
    public static final String EXTRA_KEY_TITLE = "com.honda.android.GroupAddInviteUsersActivity.TITLE";
    private static final String BUNDLE_KEY_SELECTED_CONTACTS = "com.honda.android.GroupAddPickUserActivity.SELECTED_CONTACTS";
    private static final String BUNDLE_KEY_IGNORED_CONTACTS = "com.honda.android.GroupAddPickUserActivity.IGNORED_CONTACTS";

    private static final String BUTTON_TITLE = "DONE";
    private List<IconItem> items;
    private TextView textInfo;
    private IconAdapter adapter;
    private MessengerDatabaseHelper messengerHelper;
    private ListView listParticipants;
    private Map<String, Contact> selectedContacts;
    private Map<String, Contact> ignoredContacts;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray(BUNDLE_KEY_SELECTED_CONTACTS,
                parseToArray(selectedContacts));
    }

    @Override
    protected String getButtonTitle() {
        return BUTTON_TITLE;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        selectedContacts = parseToMap(savedInstanceState
                .getParcelableArray(BUNDLE_KEY_SELECTED_CONTACTS));
        ignoredContacts = parseToMap(savedInstanceState
                .getParcelableArray(BUNDLE_KEY_IGNORED_CONTACTS));
    }

    private Parcelable[] parseToArray(Map<String, Contact> map) {
        Parcelable[] parcelables = new Parcelable[map.size()];
        int c = 0;
        for (Iterator<String> iterator = map.keySet().iterator(); iterator
                .hasNext();) {
            String k = iterator.next();
            parcelables[c] = map.get(k);
            c++;
        }
        return parcelables;
    }

    private Map<String, Contact> parseToMap(Parcelable[] parcelables) {
        Map<String, Contact> map = new HashMap<String, Contact>();
        if (parcelables != null) {
            for (int i = 0; i < parcelables.length; i++) {
                Contact c = (Contact) parcelables[i];
                map.put(c.getJabberId(), c);
            }
        }
        return map;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setContentView(R.layout.group_create_pick_participants);
        if (selectedContacts == null) {
            selectedContacts = parseToMap(getIntent().getParcelableArrayExtra(
                    EXTRA_KEY_GROUP_PARTICIPANTS));
        }

        if (ignoredContacts == null) {
            ignoredContacts = parseToMap(getIntent().getParcelableArrayExtra(
                    EXTRA_KEY_GROUP_IGNORE_CONTACT));
        }

        String abTitle = getIntent().getStringExtra(EXTRA_KEY_TITLE);
        if (abTitle != null) {
            getSupportActionBar().setTitle(abTitle);
        }

        items = new ArrayList<IconItem>();
        adapter = new IconAdapter(this, R.layout.list_item_with_checkbox,
                R.id.textTitle, items);
        adapter.setListType(IconAdapter.TYPE_CHECKBOX);
        adapter.setClickListener(new ItemClickListener() {

            @Override
            public void onClick(View v, IconItem item) {
                if (v instanceof CheckBox) {
                    CheckBox c = (CheckBox) v;
                    if (c != null) {
                        if (c.isChecked()) {
                            addParticipant(item);
                        } else {
                            removeParticipant(item);
                        }
                    }
                }
            }
        });

        listParticipants = (ListView) findViewById(R.id.createGroupPickParticipant);
        listParticipants.setAdapter(adapter);
        listParticipants.setOnItemClickListener(this);
        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(this);
        }

        textInfo = (TextView) findViewById(R.id.createGroupTextPickInfo);
        updateParticipantInfo();
        new ContactLoader().execute();
    }

    @Override
    public void onClick(View v) {
        Intent data = new Intent();

        Parcelable[] participants = parseToArray(selectedContacts);
        data.putExtra(EXTRA_KEY_GROUP_PARTICIPANTS, participants);
        setResult(RESULT_OK, data);
        finish();
    }

    private void updateParticipantInfo() {
        if (selectedContacts.size() == 0) {
            textInfo.setText("0 contact selected");
        } else {
            textInfo.setText(selectedContacts.size() + " contact(s) selected");
        }
    }

    private void updateItem(IconItem item) {
        int index = items.indexOf(item);
        if (index != -1) {
            items.set(index, item);
            adapter.notifyDataSetChanged();
        }
    }

    private void removeParticipant(IconItem item) {
        selectedContacts.remove(item.getJabberId());
        item.setValue(null);
        updateItem(item);
        updateParticipantInfo();
    }

    private void addParticipant(IconItem item) {
        selectedContacts.put(item.getJabberId(), (Contact) item.getChatParty());
        item.setValue(item.getJabberId());
        updateItem(item);
        updateParticipantInfo();
    }

    @Override
    public void onItemClick(AdapterView adapterView, View v, int position,
            long id) {
        IconItem item = (IconItem) items.get(position);
        CheckBox c = (CheckBox) v.findViewById(R.id.checkBox);
        if (c != null) {
            if (c.isChecked()) {
                c.setChecked(false);
                removeParticipant(item);
            } else {
                c.setChecked(true);
                addParticipant(item);
            }
        }

    }

    class ContactLoader extends AsyncTask<Void, Contact, Void> {
        private static final String SQL_SELECT_CONTACT = "SELECT * FROM "
                + Contact.TABLE_NAME + " WHERE _id>1";
        private Cursor cursor;

        @Override
        protected Void doInBackground(Void... params) {
            items.clear();
            Contact contact = new Contact();
            cursor = messengerHelper.query(SQL_SELECT_CONTACT, null);
            ;
            int indexId = cursor.getColumnIndex(Contact.ID);
            int indexName = cursor.getColumnIndex(Contact.NAME);
            int indexJabberId = cursor.getColumnIndex(Contact.JABBER_ID);
            int indexStatus = cursor.getColumnIndex(Contact.STATUS);
            while (cursor.moveToNext()) {
                String name = cursor.getString(indexName);
                String jabberId = cursor.getString(indexJabberId);
                String status = cursor.getString(indexStatus);
                long id = cursor.getLong(indexId);
                if (ignoredContacts.get(jabberId) != null)
                    continue;

                contact = new Contact(id, name, jabberId, status);
                publishProgress(new Contact[] { contact });
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Contact... values) {
            Contact data = values[0];
            if (data != null) {
                IconItem item = new IconItem(data.getJabberId(),
                        data.getName(), data.getStatus(), null, data);
                if (selectedContacts.get(data.getJabberId()) != null)
                    item.setValue(data.getJabberId());
                File f = getFileStreamPath(MediaProcessingUtil
                        .getProfilePicName(data));
                if (f.exists()) {
                    item.setImageUri(Uri.fromFile(f));
                }
                items.add(item);
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            adapter.notifyDataSetChanged();
            cursor.close();
        }

    }

}
