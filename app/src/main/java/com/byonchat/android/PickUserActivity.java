package com.byonchat.android;

import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.list.IconAdapter;
import com.byonchat.android.list.IconAdapter.ItemClickListener;
import com.byonchat.android.list.IconItem;
import com.byonchat.android.model.Group;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.utils.HttpHelper;
import com.byonchat.android.utils.MediaProcessingUtil;
import com.byonchat.android.utils.Validations;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PickUserActivity extends ABNextActivity implements
        OnItemClickListener {
    private List<IconItem> items;
    private TextView textInfo;
    private IconAdapter adapter;
    private MessengerDatabaseHelper messengerHelper;
    public final static String FROMACTIVITY = "FROM_ACTIVITY";
    public final static String NAMEGROUP = "NAME_GROUP";
    public final static String INVITEGROUP = "INVITES";
    public final static String SENDERINVITEGROUP = "SENDER_INVITES";
    public final static String GROUPID = "GROUP_ID";
    private ListView listParticipants;
    private Map<String, Contact> selectedContacts = new HashMap<String, Contact>();
    String fromActivity ;
    String groupName ;
    String invite = "";
    String sender_invite ;
    String group_id ;

    ProgressDialog progress;
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_create_pick_participants);
        getSupportActionBar().setBackgroundDrawable(new Validations().getInstance(getApplicationContext()).header(getWindow()));
     //   getSupportActionBar().setIcon(new Validations().getInstance(getApplicationContext()).logoCustome());
        fromActivity = getIntent().getStringExtra(FROMACTIVITY);
        if(fromActivity.equalsIgnoreCase("Invite User")){
            invite = " and jid NOT IN ("+getIntent().getStringExtra(INVITEGROUP)+")";
            sender_invite = getIntent().getStringExtra(SENDERINVITEGROUP);
            group_id = getIntent().getStringExtra(GROUPID);
        }
        fromActivity = getIntent().getStringExtra(FROMACTIVITY);
        groupName = getIntent().getStringExtra(NAMEGROUP);
        getSupportActionBar().setTitle(fromActivity);

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

        new ContactLoader().execute();
    }

    @Override
    public void onClick(View v) {
        showSendBroadcastActivity();
    }

    private void showSendBroadcastActivity() {
        if (selectedContacts.size() == 0) {
            Toast.makeText(this, "You need to add at least one participant",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Parcelable[] participants = parseToArray(selectedContacts);
        if(fromActivity.equalsIgnoreCase("Create Group")){
            progress = ProgressDialog.show(this, null,"please wait a moment", true);
           /* SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
            sendPostReqAsyncTask.execute(new String[]{MessengerConnectionService.GROUP_SERVER + "creategroup", json(participants,groupName)});
            MessengerConnectionServicecreateGroupChat*/

        }else if(fromActivity.equalsIgnoreCase("Message Broadcast")) {
            Intent intent = new Intent(this, BroadcastSendActivity.class);
            intent.putExtra(BroadcastSendActivity.EXTRA_KEY_BROADCAST_CONTACTS,
                    participants);
            startActivity(intent);
        }else if(fromActivity.equalsIgnoreCase("Invite User")) {
            progress = ProgressDialog.show(this, null,"please wait a moment", true);
            SendPostInviteAsyncTask sendPostInviteAsyncTask = new SendPostInviteAsyncTask();
            sendPostInviteAsyncTask.execute(new String[]{MessengerConnectionService.GROUP_SERVER + "invitecontact", jsonInvite(participants, groupName, group_id)});
        }
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
        private Cursor cursor;

        @Override
        protected Void doInBackground(Void... params) {
            items.clear();
            Contact contact = new Contact();

            cursor = messengerHelper.query(
                    "SELECT * FROM " + contact.getTableName() + " WHERE _id > 1"+ invite +" order by lower("+Contact.NAME+") ",
                    null);
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

    private class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String paramUrl = params[0];
            String paramjson = params[1];

            HttpClient httpClient = null;
            try {
                httpClient = HttpHelper.createHttpClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
            HttpPost httpPost = new HttpPost(paramUrl);
            BasicNameValuePair invitesBasicNameValuePair = new BasicNameValuePair("datagroup", paramjson);
            List nameValuePairList = new ArrayList();
            nameValuePairList.add(invitesBasicNameValuePair);
            try {
                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);
                httpPost.setEntity(urlEncodedFormEntity);
                try {
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    InputStream inputStream = httpResponse.getEntity().getContent();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    StringBuilder stringBuilder = new StringBuilder();
                    String bufferedStrChunk = null;
                    while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
                        stringBuilder.append(bufferedStrChunk);
                    }
                    return stringBuilder.toString();
                } catch (ClientProtocolException cpe) {
                    System.out.println("First Exception cause of HttpResponese :" + cpe);
                    cpe.printStackTrace();
                } catch (IOException ioe) {
                    System.out.println("Second Exception cause of HttpResponse :" + ioe);
                    ioe.printStackTrace();
                }
            } catch (UnsupportedEncodingException uee) {
                System.out.println("An Exception given because of UrlEncodedFormEntity argument :" + uee);
                uee.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progress.dismiss();
            if(result.startsWith("error") || result==null){
                Toast.makeText(getApplicationContext(), "Please try again ", Toast.LENGTH_LONG).show();
            }else{

                Group group = new Group(groupName,result,Group.STATUS_OWNER);
                messengerHelper.insertData(group);

                Message msg = new Message(result,MessengerDatabaseHelper.getInstance(getApplicationContext()).getMyContact().getJabberId(),
                        "Group Created");
                msg.setSourceInfo(MessengerDatabaseHelper.getInstance(getApplicationContext()).getMyContact().getJabberId());
                msg.setGroupChat(true);
                msg.setSendDate(new Date());
                msg.setDeliveredDate(new Date());
                msg.setStatus(Message.STATUS_DELIVERED);
                msg.setType(Message.TYPE_INFO);
                messengerHelper.insertData(msg);

                finish();

                Intent tutup = new Intent();
                tutup.setAction(CreateGroupActivity.tutup);
                sendBroadcast(tutup);
                
                Intent intent = new Intent(PickUserActivity.this, ConversationGroupActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra(ConversationGroupActivity.EXTRA_KEY_GROUP_ID, result);
                intent.putExtra(ConversationGroupActivity.EXTRA_KEY_USERNAME, MessengerDatabaseHelper.getInstance(getApplicationContext()).getMyContact().getJabberId());
                intent.putExtra(ConversationGroupActivity.EXTRA_KEY_NEW_PERSON, "0");
                intent.putExtra(ConversationGroupActivity.EXTRA_KEY_STICKY, "0");
                startActivity(intent);

            }
        }
    }

    private class SendPostInviteAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String paramUrl = params[0];
            String paramjson = params[1];
            HttpClient httpClient = null;
            try {
                httpClient = HttpHelper.createHttpClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
            HttpPost httpPost = new HttpPost(paramUrl);
            BasicNameValuePair invitesBasicNameValuePair = new BasicNameValuePair("datainvites", paramjson);
            List nameValuePairList = new ArrayList();
            nameValuePairList.add(invitesBasicNameValuePair);
            try {
                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);
                httpPost.setEntity(urlEncodedFormEntity);
                try {
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    InputStream inputStream = httpResponse.getEntity().getContent();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    StringBuilder stringBuilder = new StringBuilder();
                    String bufferedStrChunk = null;
                    while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
                        stringBuilder.append(bufferedStrChunk);
                    }
                    return stringBuilder.toString();
                } catch (ClientProtocolException cpe) {
                    System.out.println("First Exception cause of HttpResponese :" + cpe);
                    cpe.printStackTrace();
                } catch (IOException ioe) {
                    System.out.println("Second Exception cause of HttpResponse :" + ioe);
                    ioe.printStackTrace();
                }
            } catch (UnsupportedEncodingException uee) {
                System.out.println("An Exception given because of UrlEncodedFormEntity argument :" + uee);
                uee.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progress.dismiss();
            if(result.startsWith("error") || result==null){
                Toast.makeText(getApplicationContext(), "Please try again ", Toast.LENGTH_LONG).show();
            }else{
                if(result.startsWith("success")){
                    Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_LONG).show();
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(), "Please try again ", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    public String json(Parcelable[] participants, String nameGroup) {
        JSONArray jsonArray = new JSONArray();

        Map<String, Contact> selectedContacts = new HashMap<String, Contact>();
        for (int i = 0; i < participants.length; i++) {
            Contact c = (Contact) participants[i];
            selectedContacts.put(c.getJabberId(), c);
        }

        for (Iterator<String> iterator = selectedContacts.keySet().iterator(); iterator
                .hasNext();) {
            Contact data = selectedContacts.get(iterator.next());

            JSONObject user = new JSONObject();
            try {
                user.put("name", data.getJabberId());

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            jsonArray.put(user);
        }

        JSONObject obj = new JSONObject();
        try {
            obj.put("groupname",nameGroup);
            obj.put("creator_group",MessengerDatabaseHelper.getInstance(this).getMyContact().getJabberId());
            obj.put("invites",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }

    public String jsonInvite(Parcelable[] participants, String nameGroup,String groupid) {
        JSONArray jsonArray = new JSONArray();

        Map<String, Contact> selectedContacts = new HashMap<String, Contact>();
        for (int i = 0; i < participants.length; i++) {
            Contact c = (Contact) participants[i];
            selectedContacts.put(c.getJabberId(), c);
        }

        for (Iterator<String> iterator = selectedContacts.keySet().iterator(); iterator
                .hasNext();) {
            Contact data = selectedContacts.get(iterator.next());

            JSONObject user = new JSONObject();
            try {
                user.put("name", data.getJabberId());

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            jsonArray.put(user);
        }

        JSONObject obj = new JSONObject();
        try {
            obj.put("groupid",groupid);
            obj.put("groupname",nameGroup);
            obj.put("sender_invites",MessengerDatabaseHelper.getInstance(this).getMyContact().getJabberId());
            obj.put("invites",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }

}
