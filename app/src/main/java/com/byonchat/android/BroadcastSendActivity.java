package com.byonchat.android;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.byonchat.android.list.IconAdapter;
import com.byonchat.android.list.IconAdapter.ItemClickListener;
import com.byonchat.android.list.IconItem;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.utils.MediaProcessingUtil;
import com.byonchat.android.utils.Validations;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.byonchat.android.utils.Utility.reportCatch;

public class BroadcastSendActivity extends ABNextServiceActivity {
    public static final String EXTRA_KEY_BROADCAST_CONTACTS = "com.byonchat.android.BroadcastSendActivity.BROADCAST_CONTACTS";
    private static final String BUTTON_TITLE = "SEND";
    private ListView listContacts;
    private EditText txtMessage;
    private List<IconItem> items;
    private IconAdapter adapter;
    private Map<String, Contact> selectedContacts;
    private String source;

    @Override
    protected String getButtonTitle() {
        return BUTTON_TITLE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.broadcast_message);
        getSupportActionBar().setBackgroundDrawable(new Validations().getInstance(getApplicationContext()).header(getWindow()));

        try {
            txtMessage = (EditText) findViewById(R.id.txtBroadcastMessage);
            if (selectedContacts == null)
                selectedContacts = new HashMap<String, Contact>();
            Parcelable[] participants = getIntent().getParcelableArrayExtra(
                    EXTRA_KEY_BROADCAST_CONTACTS);
            for (int i = 0; i < participants.length; i++) {
                Contact c = (Contact) participants[i];
                selectedContacts.put(c.getJabberId(), c);
            }

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
            refreshList();

            listContacts = (ListView) findViewById(R.id.listBroadcastContacts);
            listContacts.setAdapter(adapter);

            source = MessengerDatabaseHelper.getInstance(this).getMyContact()
                    .getJabberId();
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    @Override
    public void onClick(View view) {
        try {
            if (items.size() == 0) {
                Toast.makeText(this, "You need to add at least one participant",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (!binder.isConnected()) {
                Toast.makeText(this,
                        "Not connected to server. Please try again later.",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            new BroadcastMessageSender().execute();
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    private void refreshList() {
        try {
            items.clear();
            for (Iterator<String> iterator = selectedContacts.keySet().iterator(); iterator
                    .hasNext(); ) {
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
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            Intent intent = new Intent(this, PickUserActivity.class);
            intent.putExtra(PickUserActivity.FROMACTIVITY, "Message Broadcast");
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void onSendingDone() {
        try {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    class BroadcastMessageSender extends AsyncTask<Void, String, Void> {
        private int count;

        @Override
        protected void onPreExecute() {
            pdialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            count = 0;
                        String message = txtMessage.getText().toString();
                for (Iterator<String> iterator = selectedContacts.keySet()
                        .iterator(); iterator.hasNext();) {
                    try {
                        Contact data = selectedContacts.get(iterator.next());
                        Message vo = new Message(source, data.getJabberId(), message);
                        vo.setStatus(Message.STATUS_INPROGRESS);
                        vo.setType(Message.TYPE_BROADCAST);
                        vo.generatePacketId();
                        binder.sendBroadCast(vo);
                        publishProgress(data.getName());
                    }catch (Exception e){
                        reportCatch(e.getLocalizedMessage());
                    }
                }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            count++;
            pdialog.setTitle("Sending " + count + " of "
                    + selectedContacts.size() + " message(s)");
        }

        @Override
        protected void onPostExecute(Void result) {
            pdialog.dismiss();
            onSendingDone();
        }

    }
}
