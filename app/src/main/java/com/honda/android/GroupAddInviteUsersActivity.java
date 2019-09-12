package com.honda.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.honda.android.list.IconAdapter;
import com.honda.android.list.IconAdapter.ItemClickListener;
import com.honda.android.list.IconItem;
import com.honda.android.provider.Contact;
import com.honda.android.utils.DialogUtil;
import com.honda.android.utils.MediaProcessingUtil;

import org.jivesoftware.smack.XMPPException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GroupAddInviteUsersActivity extends ABNextServiceActivity {

    private static final int ACTION_PICK_PARTICIPANT = 1301;
    private static final String BUTTON_TITLE = "CREATE";
    private String groupName;
    private ListView listParticipants;
    private Button btnAddParticipant;
    private List<IconItem> items;
    private IconAdapter adapter;
    private Map<String, Contact> selectedContacts;

    @Override
    protected String getButtonTitle() {
        return BUTTON_TITLE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.group_create_add_participants);

        btnAddParticipant = (Button) findViewById(R.id.btnCreateGroupAddParticipant);
        btnAddParticipant.setOnClickListener(this);

        groupName = getIntent().getStringExtra(
                GroupAddInfoActivity.EXTRA_KEY_GROUP_NAME);

        items = new ArrayList<IconItem>();
        adapter = new IconAdapter(this, R.layout.list_item_with_image,
                R.id.textTitle, items);
        adapter.setListType(IconAdapter.TYPE_IMAGE);
        adapter.setClickListener(new ItemClickListener() {

            @Override
            public void onClick(View v, IconItem item) {
                selectedContacts.remove(item.getJabberId());
                refreshList();
            }
        });

        listParticipants = (ListView) findViewById(R.id.createGroupParticipantList);
        listParticipants.setAdapter(adapter);

        if (selectedContacts == null)
            selectedContacts = new HashMap<String, Contact>();
    }

    private void showActionPick() {
        Intent i = new Intent(this, GroupAddPickUserActivity.class);
        Parcelable[] participants = new Parcelable[selectedContacts.size()];
        int c = 0;
        for (Iterator<String> iterator = selectedContacts.keySet().iterator(); iterator
                .hasNext();) {
            String k = iterator.next();
            participants[c] = selectedContacts.get(k);
            c++;
        }
        i.putExtra(GroupAddPickUserActivity.EXTRA_KEY_GROUP_PARTICIPANTS,
                participants);
        startActivityForResult(i, ACTION_PICK_PARTICIPANT);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            Intent intent = new Intent(this, GroupAddInfoActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        if (requestCode == ACTION_PICK_PARTICIPANT) {
            selectedContacts.clear();
            Parcelable[] participants = data
                    .getParcelableArrayExtra(GroupAddPickUserActivity.EXTRA_KEY_GROUP_PARTICIPANTS);
            for (int i = 0; i < participants.length; i++) {
                Contact c = (Contact) participants[i];
                selectedContacts.put(c.getJabberId(), c);
            }
            refreshList();
        }

    }

    private void refreshList() {
        items.clear();
        for (Iterator<String> iterator = selectedContacts.keySet().iterator(); iterator
                .hasNext();) {
            Contact data = selectedContacts.get(iterator.next());
            IconItem item = new IconItem(data.getJabberId(), data.getName(),
                    data.getStatus(), null, data);
            File f = getFileStreamPath(MediaProcessingUtil
                    .getProfilePicName(data));
            if (f.exists()) {
                item.setImageUri(Uri.fromFile(f));
            }
            items.add(item);
        }
        adapter.notifyDataSetChanged();
    }

    private void createGroup() {
        if (selectedContacts.size() == 0) {
            Toast.makeText(this, "You need to add at least one participant",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedContacts.size() > 20) {
            Toast.makeText(this, "Only 20 participants at maximum.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        pdialog = new ProgressDialog(this);
        pdialog.setIndeterminate(true);
        pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdialog.setMessage("Creating group...");
        pdialog.show();
        new GroupCreateHandler().execute();

    }

    private void onGroupCreated(String groupId) {
        pdialog.dismiss();
        finish();
    }

    private void onGroupCreateError() {
        pdialog.dismiss();
        AlertDialog.Builder dlg = DialogUtil.generateAlertDialog(this, "Error",
                "Failed creating group. Please try again later.");
        dlg.setPositiveButton("OK", null);
        dlg.show();
    }

    class GroupCreateHandler extends AsyncTask<Void, String, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {

            int c = 0;
            while (!binder.isConnected() && c < 5) {
                SystemClock.sleep(200);
                c++;
            }
            if (c == 5) {
                publishProgress(new String[] { null });
                return null;
            }

            List<Contact> participants = new ArrayList<Contact>();
            for (Iterator<String> iterator = selectedContacts.keySet()
                    .iterator(); iterator.hasNext();) {
                Contact contact = selectedContacts.get(iterator.next());
                participants.add(contact);
            }
            try {
                String groupId = binder.createGroupChatD(participants);
                binder.changeGroupChatSubject(groupId, groupName);
                publishProgress(new String[] { groupId });
            } catch (XMPPException e) {
                publishProgress(new String[] { null });
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            String s = values[0];
            if (s != null) {
                onGroupCreated(s);
            } else {
                onGroupCreateError();
            }
        }

    }

    @Override
    public void onClick(View v) {

        if (v.equals(btnAddParticipant)) {
            showActionPick();
        } else {
            createGroup();
        }
    }

}
