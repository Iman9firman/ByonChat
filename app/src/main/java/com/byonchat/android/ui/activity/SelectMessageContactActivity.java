package com.byonchat.android.ui.activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.MessengerDatabaseHelper;

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

public class TabContactList extends AppCompatActivity implements ContactsAdapter.ChatAdapterListener{
    private MessengerDatabaseHelper messengerHelper;
    static ContactsAdapter contactAdapter;
    private ArrayList<Contact> contactList = new ArrayList<>();
    List<Contact> contacts = new ArrayList<Contact>();
    private static String REQUEST_CONTACT_URL = "https://"+ MessengerConnectionService.UTIL_SERVER + "/v1/users/friends";
    private static final String SQL_SELECT_CONTACTS = "SELECT * FROM " + Contact.TABLE_NAME + " WHERE _id > 1 order by lower(" + Contact.NAME + ")";
    private RecyclerView listContact;
    ImageView reload;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_contact_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView backBtn = (ImageView) findViewById(R.id.backImage);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listContact = (RecyclerView) findViewById(R.id.list_contact);
        reload = (ImageView) findViewById(R.id.reload_btn);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = ProgressDialog.show(TabContactList.this, "", "Refreshing Contact List", true);
                pDialog.show();
                new CheckContact().execute();
            }
        });

        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(getApplicationContext());
        }

        kontakeDB();
        defineChatList();
    }
    private void defineChatList() {
        RecyclerView.LayoutManager cLayoutManager = new LinearLayoutManager(getApplicationContext());

        contactAdapter = new ContactsAdapter(getApplicationContext(), this, contactList);

        contactAdapter.notifyDataSetChanged();

        listContact.setLayoutManager(cLayoutManager);

        listContact.setAdapter(contactAdapter);

        TextView toolbarTitle = (TextView) findViewById(R.id.Title);
        toolbarTitle.setText("Pilih kontak");

        TextView toolbarSub = (TextView) findViewById(R.id.SubTitle);
        toolbarSub.setText(contactList.size() +" kontak");
    }

    private void kontakeDB() {

        Cursor cursor0;

        String myJabberId = messengerHelper.getMyContact().getJabberId();

        cursor0 = messengerHelper.query("SELECT * FROM contacts where not jid = " + myJabberId,  null);


        int contactName = cursor0.getColumnIndex(Contact.NAME);
        int contactJabberId = cursor0.getColumnIndex("jid");

        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DATE), 0, 0, 0);

        contactList.clear();
        while (cursor0.moveToNext()) {
            String jabberId = cursor0.getString(contactJabberId);
            String name = cursor0.getString(contactName);

            Contact contactItem = new Contact(name,jabberId,null);
            contactList.add(contactItem);

        }
    }

    public static void search(String query) {
        contactAdapter.getFilter().filter(query);
    }

    public void openConversation(IconItem chat) {
        SoloChatRoom chatRoom = new SoloChatRoom();
        chatRoom.setAvatarUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRsZp6sfPnFn_abiv9vnyzUZFno2Y5G3k3imf-rXp5SS_zprVvOKA");
        chatRoom.setChannel(true);
        chatRoom.setDistinctId("1234");
        chatRoom.setGroup(false);
        chatRoom.set_Id(chat.getJabberId());
        ArrayList<SoloRoomMember> soloRoomMemberArrayList = new ArrayList<>();
        SoloRoomMember soloMember = new SoloRoomMember();
        soloMember.setAvatar("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRsZp6sfPnFn_abiv9vnyzUZFno2Y5G3k3imf-rXp5SS_zprVvOKA");
        soloMember.setEmail(chat.getJabberId());


        Contact contact = messengerHelper.getContact(chat.getJabberId());
        if (contact == null)
            soloMember.setUsername(chat.getJabberId());
        else
            soloMember.setUsername(contact.getName());
        soloRoomMemberArrayList.add(soloMember);
        chatRoom.setMember(soloRoomMemberArrayList);


        SoloComment soloComment = new SoloComment();
        SoloContact soloContact = new SoloContact("Lulu", "asdf", "type");
        soloComment.setCommentBeforeId(123);
        soloComment.setContact(soloContact);
        soloComment.setMessage("asdf");

        chatRoom.setLastComment(soloComment);
        Intent intent = SoloChatActivity.generateIntent(getApplicationContext(), chatRoom, 0,0);
        startActivity(intent);
    }

    public void readLocalContact(){
        contactList.clear();
        contacts = messengerHelper.getAllContact();
        for (Contact contact:contacts){
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
        defineChatList();
    }


    private class CheckContact extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result){
                readLocalContact();
                pDialog.dismiss();
            }
        }
        @Override
        protected Boolean doInBackground(String... strings) {

            HashMap<Long, Contact> osMap = loadContactFromOs();
            if (osMap.size() == 0) {
                messengerHelper.execSql(getString(R.string.delete_all_contacts), null);
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
                            if (!osMap.get(curAppFriendNumber).equals(contact.getName())) {contact.setName(cOS.getName());
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

    @Override
    public void onRowClickContact(View view, String jabberID) {
        IconItem chat = new IconItem(jabberID, "pershop", "","08.00",null);
        openConversation(chat);
    }

    @Override
    public void counting(int yzou) {

    }
}
