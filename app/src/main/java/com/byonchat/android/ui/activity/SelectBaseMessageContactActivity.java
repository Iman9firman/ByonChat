package com.byonchat.android.ui.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.ConversationActivity;
import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.createMeme.FilteringImage;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.list.IconItem;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.ui.adapter.ContactsAdapter;
import com.byonchat.android.utils.RefreshContactService;
import com.byonchat.android.utils.ThrowProfileService;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static com.byonchat.android.communication.MessengerConnectionService.CONTACT_URI;

public abstract class SelectBaseMessageContactActivity extends AppCompatActivity implements ContactsAdapter.ChatAdapterListener {

    protected String REQUEST_CONTACT_URL = "https://" + MessengerConnectionService.UTIL_SERVER + "/v1/users/friends";
    protected String SQL_SELECT_CONTACTS = "SELECT * FROM " + Contact.TABLE_NAME + " WHERE _id > 1 order by lower(" + Contact.NAME + ")";

    protected ArrayList<Contact> contactList = new ArrayList<>();
    protected List<Contact> contacts = new ArrayList<Contact>();

    protected BroadcastHandler broadcastHandler = new BroadcastHandler();
    protected MessengerDatabaseHelper messengerHelper;
    protected ContactsAdapter contactAdapter;

    protected String mColor, mColorText;
    protected ProgressDialog pDialog;
    protected RecyclerView.LayoutManager cLayoutManager;

    protected RecyclerView listContact;
    protected ImageView reload;
    protected Toolbar toolbar;
    protected ImageView backBtn;
    protected TextView toolbarTitle;
    protected TextView toolbarSub;

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
        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(getApplicationContext());
        }
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
        setSupportActionBar(toolbar);

        toolbarTitle.setText("Pilih kontak");
        toolbarSub.setText(contactList.size() + " kontak");

        FilteringImage.SystemBarBackground(getWindow(), Color.parseColor("#" + mColor));
//        toolbar.setBackgroundColor(Color.parseColor("#" + mColor));
//        toolbar.setTitleTextColor(Color.parseColor("#" + mColorText));

        backBtn.setColorFilter(Color.parseColor("#" + mColorText), PorterDuff.Mode.SRC_IN);
        reload.setColorFilter(Color.parseColor("#" + mColorText), PorterDuff.Mode.SRC_IN);
    }

    protected void resolveView() {
        backBtn.setOnClickListener(v -> {
            finish();
        });

        reload.setOnClickListener(v -> {
            pDialog = ProgressDialog.show(SelectBaseMessageContactActivity.this, "", "Refreshing Contact List", true);
            pDialog.show();
            new CheckContact().execute();
        });
    }

    protected void resolveContactDB() {
        Cursor cursor0 = messengerHelper.query("SELECT * FROM contacts where not jid = " + messengerHelper.getMyContact().getJabberId(), null);

        int contactName = cursor0.getColumnIndex(Contact.NAME);
        int contactJabberId = cursor0.getColumnIndex("jid");

        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DATE), 0, 0, 0);

        contactList.clear();
        while (cursor0.moveToNext()) {
            String jabberId = cursor0.getString(contactJabberId);
            String name = cursor0.getString(contactName);

            Contact contactItem = new Contact(name, jabberId, null);
            contactList.add(contactItem);
        }
    }

    protected class BroadcastHandler extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MessengerConnectionService.ACTION_STATUS_CHANGED
                    .equals(intent.getAction())) {
//                refreshOneContactList();

                Intent i = new Intent(getApplicationContext(), ThrowProfileService.class);
                i.putExtra("broadcast", MessengerConnectionService.ACTION_STATUS_CHANGED_CONTACT);
                startService(i);

            } else if (RefreshContactService.ACTION_CONTACT_REFRESHED.equals(intent.getAction())) {
//                refreshContactList();
                Toast.makeText(context, "refresh contact", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }
    }

    protected void resolveList() {
        cLayoutManager = new LinearLayoutManager(getApplicationContext());
        contactAdapter = new ContactsAdapter(getApplicationContext(), this, contactList);
        contactAdapter.notifyDataSetChanged();
        listContact.setLayoutManager(cLayoutManager);
        listContact.setAdapter(contactAdapter);
    }

    public void search(String query) {
        contactAdapter.getFilter().filter(query);
    }

    public void openConversation(View view, IconItem item) {
        Intent intent = new Intent(getApplicationContext(), ConversationActivity.class);
        String jabberId = item.getJabberId();
        intent.putExtra(ConversationActivity.KEY_JABBER_ID, jabberId);
        intent.putExtra(Constants.EXTRA_COLOR, mColor);
        intent.putExtra(Constants.EXTRA_COLORTEXT, mColorText);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View imageView = view.findViewById(R.id.imagePhoto);
            Pair<View, String> pair1 = Pair.create(imageView, imageView.getTransitionName());
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(this, pair1);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    public void readLocalContact() {
        contactList.clear();
        contacts = messengerHelper.getAllContact();
        for (Contact contact : contacts) {
/*            UserModel userModel = new UserModel();
            userModel.setId(contact.getJabberId());
            userModel.setNamaUser(contact.getName());
            userModel.setCommentUser("+"+contact.getJabberId());*/

            Contact uM = new Contact();
            uM.setName(contact.getName());
            uM.setJabberId(contact.getJabberId());
            uM.setStatus("");
            contactList.add(uM);
        }
        resolveList();
    }

    private class CheckContact extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                readLocalContact();
                pDialog.dismiss();
            }
        }

        @Override
        protected Boolean doInBackground(String... strings) {

            HashMap<Long, Contact> osMap = loadContactFromOs();
            if (osMap.size() == 0) {
                Log.w("kesinidong?", "delete all contact");
//                messengerHelper.execSql(getString(R.string.delete_all_contacts), null);
            }
            StringBuilder sbuffer = new StringBuilder();
            sbuffer.append(messengerHelper.getMyContact().getJabberId())
                    .append(",")
                    .append(messengerHelper.getMyContact().getName())
                    .append(",");

            for (Long number : osMap.keySet()) {
                sbuffer.append(number.toString()).append(",");
            }

            sbuffer.setLength(sbuffer.length() - 1);
            InputStreamReader reader = null;
            String[] arrTemp = new String[0];
            try {
                HttpPost post = new HttpPost(REQUEST_CONTACT_URL);
                Log.w("kepoah", sbuffer.toString());
                post.setEntity(new StringEntity(sbuffer.toString()));
                HttpClient httpClient = new DefaultHttpClient();
                HttpResponse response = httpClient.execute(post);
                reader = new InputStreamReader(response.getEntity()
                        .getContent(), "UTF-8");
                int r;
                StringBuilder buf = new StringBuilder();
                while ((r = reader.read()) != -1) {
                    buf.append((char) r);
                }

                if (response.getStatusLine().getStatusCode() != 200) {
                    Log.e(getLocalClassName(),
                            "Failed getting contact from server: "
                                    + buf.toString()
                    );
                }
                arrTemp = buf.toString().split(",");
            } catch (Exception e) {
                Log.e(getLocalClassName(),
                        "Failed getting contact from server: " + e.getMessage(),
                        e);
            }
            long[] sortedAppFriends = new long[arrTemp.length];
            int i = 0;
            for (String s : arrTemp) {
                String curNumber = s.trim();
                if (!curNumber.equals("")) {
                    try {
                        sortedAppFriends[i] = Long.parseLong(curNumber);
                        i++;
                    } catch (NumberFormatException nfe) {
                    }
                }
            }
            Arrays.sort(sortedAppFriends);
            HashMap<Long, Contact> dbMap = loadContactFromDb();
            long[] arrDb = new long[dbMap.size()];
            i = 0;
            for (Long l : dbMap.keySet()) {
                arrDb[i] = l;
                i++;
            }
            Arrays.sort(arrDb);

            ArrayList<String> deleteBucket = new ArrayList<String>();
            int indexDb = 0;
            int indexAppFriends = 0;
            while (indexAppFriends < sortedAppFriends.length) {
                long curAppFriendNumber = sortedAppFriends[indexAppFriends];
                if (curAppFriendNumber == 0) {
                    indexAppFriends++;
                    continue;
                }
                boolean processed = false;
                Contact cOS = osMap.get(curAppFriendNumber);
                if (indexDb < arrDb.length) {
                    long curDbNumber = arrDb[indexDb];
                    if (curAppFriendNumber < curDbNumber) {
                        messengerHelper.insertData(cOS);
                        processed = true;
                    } else {
                        Contact contact = dbMap.get(curDbNumber);

                        if (curAppFriendNumber == curDbNumber) {
                            if (!osMap.get(curAppFriendNumber).equals(contact.getName())) {
                                contact.setName(cOS.getName());
                                messengerHelper.updateData(contact);
                            }
                            processed = true;
                        } else {
                            deleteBucket.add(contact.getJabberId());
                        }
                        indexDb++;
                    }
                } else {
                    messengerHelper.insertData(cOS);
                    processed = true;
                }
                if (processed) {
                    indexAppFriends++;
                }
            }
            String sql = "DELETE FROM contacts WHERE jid IN";
            sbuffer = new StringBuilder("");
            for (int j = indexDb; j < arrDb.length; j++) {
                Contact contact = dbMap.get(Long.valueOf(arrDb[j]));
                deleteBucket.add(contact.getJabberId());
            }

            if (deleteBucket.size() > 0) {
                i = 0;
                String[] jids = new String[deleteBucket.size()];
                while (i < deleteBucket.size()) {
                    String jabberId = deleteBucket.get(i);
                    sbuffer.append("?");
                    jids[i] = jabberId;
                    i++;
                    if (i != deleteBucket.size())
                        sbuffer.append(",");
                }
                sql += "(" + sbuffer.toString() + ");";
                messengerHelper.execSql(sql, jids);
            }

            return true;
        }
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

    private HashMap<Long, Contact> loadContactFromOs() {
        String[] projection = new String[]{
                ContactsContract.Contacts.LOOKUP_KEY,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor cursor = getContentResolver().query(CONTACT_URI, projection,
                null, null, null);
        int indexId = cursor.getColumnIndex(projection[0]);
        int indexName = cursor.getColumnIndex(projection[1]);
        int indexMnumber = cursor.getColumnIndex(projection[2]);
        HashMap<Long, Contact> osMap = new HashMap<Long, Contact>();
        while (cursor.moveToNext()) {
            String id = cursor.getString(indexId);
            String name = cursor.getString(indexName);
            String mnumber = cursor.getString(indexMnumber).replace(" ", "")
                    .replace("-", "");

            if (mnumber.startsWith("0")) {
                mnumber = mnumber.replaceFirst("0", "62");
            } else if (mnumber.startsWith("+")) {
                mnumber = mnumber.replaceFirst("\\+", "");
            }

            if (messengerHelper.getMyContact().getJabberId().equals(mnumber))
                continue;

            try {
                Contact c = new Contact(name, mnumber, "");
                c.setAddrbookId(id);
                osMap.put(Long.parseLong(mnumber), c);
            } catch (NumberFormatException nfe) {

            }
        }
        cursor.close();
        return osMap;
    }

}
