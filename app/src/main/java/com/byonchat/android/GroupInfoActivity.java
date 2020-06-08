package com.byonchat.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.list.IconItem;
import com.byonchat.android.list.utilLoadImage.ImageLoader;
import com.byonchat.android.list.utilLoadImage.ImageLoaderFromSD;
import com.byonchat.android.list.utilLoadImage.TextLoader;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.Group;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.utils.ColorUtils;
import com.byonchat.android.utils.DialogUtil;
import com.byonchat.android.utils.HttpHelper;
import com.byonchat.android.utils.MediaProcessingUtil;

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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroupInfoActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbarLayout = null;
   // private Contact contact;
    private MessengerDatabaseHelper dbhelper;
    Bitmap PhotoProfile = null;
    private ImageView profile_id;
    public final static String REFERENCE_MAIN = "main";
    public final static String REFERENCE_CONVERSATION = "conversation";
    public final static String KEY_REFERENCE = "com.byonchat.android.ViewProfileActivity.REFERENCE";
    public final static String EXTRA_KEY_GROUP_JID = "com.byonchat.android.ViewProfileActivity.GROUP_ID";
    String iconsStoragePath ;
    String groupId = "";
    protected ProgressDialog pdialog;

    ArrayList<IconItem> rowItems;
    private static final String SQL_SELECT_CONTACTS = "SELECT * FROM "
            + Contact.TABLE_NAME + " WHERE _id > 1 order by lower(" + Contact.NAME + ")";

    private LinearLayout linearLayout;
    private Button btnExit;
    String memberList = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        groupId = getIntent().getStringExtra(EXTRA_KEY_GROUP_JID);

/* String jabberId = getIntent().getStringExtra(KEY_JABBER_ID);
        reference = getIntent().getStringExtra(KEY_REFERENCE);*/
        if (dbhelper == null) {
            dbhelper = MessengerDatabaseHelper.getInstance(this);
        }
       /* contact = dbhelper.getContact(jabberId);
        if (contact == null) {
            contact = new Contact("+" + jabberId, jabberId, "");
        }
*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }

        profile_id = (ImageView) findViewById(R.id.profile_id);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        linearLayout  = (LinearLayout) findViewById(R.id.linearLayout);
        btnExit  = (Button) findViewById(R.id.btn_exit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String delMessage = "Exit '" + "gropu" + "' group?";
                AlertDialog.Builder builder = DialogUtil.generateAlertDialog(GroupInfoActivity.this,
                        "Confirm Exit Group", delMessage);
                builder.setPositiveButton("Exit",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                pdialog = ProgressDialog.show(GroupInfoActivity.this, null,"please wait a moment", true);
                                PostExitGroup postExitGroup = new PostExitGroup();
                                postExitGroup.execute(new String[]{MessengerConnectionService.GROUP_SERVER + "disconnect", dbhelper.getMyContact().getJabberId()});
                            }
                        });
                builder.setNegativeButton("No", null);
                builder.show();
            }
        });


        rowItems = new ArrayList<IconItem>();
       /* lv = (ListView) findViewById(R.id.list_participant);
        rowItems = new ArrayList<IconItem>();
        adapter = new ContactAdapter(getApplicationContext(), rowItems);
        lv.setAdapter(adapter);
        lv.setClickable(true);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, final long id) {

            }
        });*/


     //   collapsingToolbarLayout.setTitle(contact.getName());
     //   contact = dbhelper.getContact(jabberId);


        //btnCall = (ImageButton) findViewById(R.id.btnCall);
       /* btnCall.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                try {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:+" + contact.getJabberId()));
                    startActivity(callIntent);
                } catch (ActivityNotFoundException activityException) {
                }
            }
        });
*/
       // btnChat = (ImageButton) findViewById(R.id.btnChat);
        /*btnChat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
                Intent intent = new Intent(getApplicationContext(), ConversationActivity.class);
                intent.putExtra(ConversationActivity.KEY_JABBER_ID, contact.getJabberId());
                startActivity(intent);
            }
        });*/

        /*txtStatus = (TextView) findViewById(R.id.textProfileDataStatus);
        txtStatus.setText(URLDecoder.decode(contact.getStatus() != null ? contact.getStatus() : "I love byonchat"));

        txtMobile = (TextView) findViewById(R.id.textProfileDataMobile);
        txtMobile.setText("+" + Utility.formatPhoneNumber(contact.getJabberId()));


        final File f = getFileStreamPath(MediaProcessingUtil
                .getProfilePicName(contact));*/

        /*if (f.exists()) {
            PhotoProfile = BitmapFactory.decodeFile(String.valueOf(f));
            profile_id.setImageBitmap(PhotoProfile);

            iconsStoragePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                    + "/ByonChat/Photo Profile/";
            File sdIconStorageDir = new File(iconsStoragePath);
            if (!sdIconStorageDir.exists()) {
                sdIconStorageDir.mkdirs();
            }
           *//* iconsStoragePath += jabberId+".jpg";
            File yourFile = new File(sdIconStorageDir, jabberId+".jpg");
            if (yourFile.exists()) {
                profile_id.setImageDrawable(Drawable.createFromPath(iconsStoragePath));
            }else{
                if(NetworkInternetConnectionStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
                    new DownloadFileFromURL().execute("https://" + MessengerConnectionService.F_SERVER + "/toboldlygowherenoonehasgonebefore/" + jabberId + ".jpg");
                }
            }*//*
            profile_id.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    displayPhoto(f);

                }
            });
        }else{
            PhotoProfile = BitmapFactory.decodeResource(getResources(), R.drawable.ic_no_photo);
        }*/
        //dynamicToolbarColor();
        //toolbarTextAppernce();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //refreshContactList();
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(new String[]{MessengerConnectionService.GROUP_SERVER + "get_profile_group", groupId});
    }

    private void showParticipan(ArrayList<IconItem> list, final boolean admin){

        if (admin){
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayoutAdd);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), PickUserActivity.class);
                    intent.putExtra(PickUserActivity.FROMACTIVITY, "Invite User");
                    intent.putExtra(PickUserActivity.INVITEGROUP, memberList.substring(0,memberList.length()-1));
                    intent.putExtra(PickUserActivity.SENDERINVITEGROUP, dbhelper.getMyContact().getJabberId());
                    intent.putExtra(PickUserActivity.GROUPID, groupId);
                    intent.putExtra(PickUserActivity.NAMEGROUP, dbhelper.getGroup(groupId).getName());
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                }


            });
        }

        this.linearLayout.removeAllViews();
        for (IconItem iconItem : list) {
            IconItem e = list.get(list.size() - 1);
            ImageLoader imageLoader;
            ImageLoaderFromSD imageLoaderSD;
            TextLoader textLoader;

            imageLoader = new ImageLoader(getApplicationContext());
            imageLoaderSD = new ImageLoaderFromSD(getApplicationContext());
            textLoader = new TextLoader(getApplicationContext());

            LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = vi.inflate(R.layout.list_item, null);
            TextView textTitle = (TextView) v.findViewById(R.id.textTitle);
            ImageView iconView = (ImageView) v.findViewById(R.id.imagePhoto);
            TextView textInfo = (TextView) v.findViewById(R.id.textInfo);
            TextView textDate = (TextView) v.findViewById(R.id.dateInfo);
            ImageButton roomsOpen = (ImageButton) v.findViewById(R.id.roomsOpen);
            View line = (View) v.findViewById(R.id.relativeLayout2);

            if(iconItem.equals(e)){
                line.setVisibility(View.GONE);
            }

            roomsOpen.setVisibility(View.GONE);
            String text = Html.fromHtml(URLDecoder.decode(iconItem.getInfo())).toString();
            if (text.contains("<")) {
                text = Html.fromHtml(Html.fromHtml(text).toString()).toString();
            }
            textTitle.setText(iconItem.getTitle());
            textDate.setText(iconItem.getDateInfo());
            textInfo.setText(text);
            if (iconItem.getChatParty() instanceof Group) {
                iconView.setImageResource(R.drawable.ic_group);
            } else {
                String regex = "[0-9]+";
                if (!iconItem.getJabberId().matches(regex)) {
                    textLoader.DisplayImage(iconItem.getTitle(), textTitle);
                    imageLoader.DisplayImage("https://" + MessengerConnectionService.HTTP_SERVER + "/mediafiles/" + iconItem.getJabberId() + "_thumb.png", iconView);
                }else{
                    imageLoaderSD.DisplayImage(MediaProcessingUtil
                            .getProfilePic(iconItem.getJabberId()), iconView, false);
                }
            }

            v.setBackground(getResources().getDrawable(R.drawable.selector_no_stroke));
            final String finalI = iconItem.getJabberId();
            final String finalName = iconItem.getTitle();
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!finalName.equalsIgnoreCase("You")) {
                        AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                                GroupInfoActivity.this);
                        builderSingle.setTitle("Select an action");
                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                GroupInfoActivity.this,
                                android.R.layout.simple_list_item_1);
                        arrayAdapter.add("Message " + finalName);
                        arrayAdapter.add("View " + finalName);
                        if (admin) arrayAdapter.add("Remove " + finalName);
                        builderSingle.setAdapter(arrayAdapter,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String listName = arrayAdapter.getItem(which);
                                        if (listName.equalsIgnoreCase("Message " + finalName)) {
                                            Intent intent = new Intent(GroupInfoActivity.this, ConversationActivity.class);
                                            intent.putExtra(ConversationActivity.KEY_JABBER_ID, finalI);
                                            startActivity(intent);
                                        } else if (listName.equalsIgnoreCase("View " + finalName)) {
                                            Intent i = new Intent(GroupInfoActivity.this, ViewProfileActivity.class);
                                            i.putExtra(ViewProfileActivity.KEY_JABBER_ID, finalI);
                                            i.putExtra(ViewProfileActivity.KEY_REFERENCE,
                                                    ViewProfileActivity.REFERENCE_GROUP);
                                            startActivity(i);

                                        }
                                        if (listName.equalsIgnoreCase("Remove " + finalName)) {
                                            String delMessage = "Remove " + finalName + "from '" + "gropu" + "' group?";
                                            AlertDialog.Builder builder = DialogUtil.generateAlertDialog(GroupInfoActivity.this,
                                                    "Confirm Remove", delMessage);
                                            builder.setPositiveButton("Yes",
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            pdialog = ProgressDialog.show(GroupInfoActivity.this, null,"please wait a moment", true);
                                                            PostRemoveMembers removeMembers = new PostRemoveMembers();
                                                            removeMembers.execute(new String[]{MessengerConnectionService.GROUP_SERVER + "removeperson", groupId, finalI,dbhelper.getMyContact().getJabberId()});
                                                        }
                                                    });
                                            builder.setNegativeButton("No", null);
                                            builder.show();
                                        }
                                    }
                                });
                        builderSingle.show();
                    }
                }


            });
            this.linearLayout.addView(v);
        }
    }


    private void displayPhoto(File f) {
       /* Intent intent = new Intent(this, FullScreenUpdateProfileActivity.class);
        intent.putExtra(FullScreenUpdateProfileActivity.PHOTO,
                f.getAbsolutePath());
        intent.putExtra(FullScreenUpdateProfileActivity.JABBER_ID,KEY_JABBER_ID);
        startActivity(intent);*/
    }

    private void dynamicToolbarColor() {

        final int color = ColorUtils.getDominantColor1(PhotoProfile);
        Palette.from(PhotoProfile).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(color));
                collapsingToolbarLayout.setStatusBarScrimColor(palette.getMutedColor(color));
            }
        });
    }

    private void toolbarTextAppernce() {
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandedappbar);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
               /* Intent intent;
                if (REFERENCE_MAIN.equals(reference)) {
                    intent = new Intent(this, MainActivity.class);
                } else {
                    intent = new Intent(this, ConversationActivity.class);
                    intent.putExtra(ConversationActivity.KEY_JABBER_ID,
                            contact.getJabberId());
                    intent.putExtra(ConversationActivity.KEY_TITLE,
                            contact.getName());
                    intent.putExtra(ConversationActivity.KEY_CONVERSATION_TYPE,
                            ConversationActivity.CONVERSATION_TYPE_PRIVATE);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);*/
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //   showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream to write file


                OutputStream output = new FileOutputStream(iconsStoragePath);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(""+(int)((total*100)/lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            File imgFile = new  File(iconsStoragePath);
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                int currentapiVersion = Build.VERSION.SDK_INT;
                if (currentapiVersion >= Build.VERSION_CODES.KITKAT){
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file:/" + imgFile.getAbsolutePath())));
                } else{
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file:/" + imgFile.getAbsolutePath())));
                }

                profile_id.setImageBitmap(myBitmap);

            }
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
            BasicNameValuePair invitesBasicNameValuePair = new BasicNameValuePair("groupid", paramjson);
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
                    cpe.printStackTrace();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            } catch (UnsupportedEncodingException uee) {
                uee.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.startsWith("error") || result==null){
                Toast.makeText(getApplicationContext(), "terjadi kesalahan jaringan ", Toast.LENGTH_LONG).show();
            }else{
                JSONObject jObject = null;
                try {
                    jObject = new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (jObject != null) {
                    try {
                        String creatorGroup = jObject.getString("creator_group");
                        String createTimestamp = jObject.getString("create_timestamp");
                        String groupIcon = jObject.getString("icon");
                        String groupname = jObject.getString("groupname");

                        ArrayList<IconItem> arrayListContact = new ArrayList<IconItem>();
                        JSONArray cast = jObject.getJSONArray("member_list");
                        for (int i=0; i<cast.length(); i++) {
                            JSONObject actor = cast.getJSONObject(i);
                            String name = actor.getString("name");
                            memberList+=name+",";
                            Contact contact = dbhelper.getContact(name);
                            IconItem item ;
                            contact = dbhelper.getContact(name);
                            if (contact == null) {
                                contact = new Contact("+" + name, name, "");
                                item = new IconItem(contact.getJabberId(),
                                        contact.getName(), "", creatorGroup.equalsIgnoreCase(contact.getJabberId())?"Admin":"", contact,0,null,true);
                            }else {
                                if (contact.getJabberId().equals(dbhelper.getMyContact().getJabberId())){
                                    item = new IconItem(contact.getJabberId(),
                                            "You", contact.getStatus() != null ? contact.getStatus() : "I love byonchat", creatorGroup.equalsIgnoreCase(contact.getJabberId())?"Admin":"", contact,0,null,true);
                                }else {
                                    item = new IconItem(contact.getJabberId(),
                                            contact.getName(), contact.getStatus() != null ? contact.getStatus() : "I love byonchat", creatorGroup.equalsIgnoreCase(contact.getJabberId())?"Admin":"", contact,0,null,true);
                                }

                            }
                            arrayListContact.add(item);
                        }
                        rowItems.clear();
                        Collections.sort(arrayListContact, IconItem.nameSortComparator);
                        for(IconItem itemss: arrayListContact){
                            rowItems.add(itemss);
                        }
                        showParticipan(rowItems,true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    class PostRemoveMembers extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String paramUrl = params[0];
            String paramjson = params[1];
            String paramName = params[2];
            String paramAdmin = params[3];

            HttpClient httpClient = null;
            try {
                httpClient = HttpHelper.createHttpClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
            HttpPost httpPost = new HttpPost(paramUrl);
            BasicNameValuePair basicNameValuePair = new BasicNameValuePair("groupid", paramjson);
            BasicNameValuePair basicNameValuePair1 = new BasicNameValuePair("name", paramName);
            BasicNameValuePair basicNameValuePair2 = new BasicNameValuePair("admin", paramName);
            List nameValuePairList = new ArrayList();
            nameValuePairList.add(basicNameValuePair);
            nameValuePairList.add(basicNameValuePair1);
            nameValuePairList.add(basicNameValuePair2);
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
                    cpe.printStackTrace();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            } catch (UnsupportedEncodingException uee) {
                uee.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pdialog.dismiss();
            if(result.startsWith("error") || result==null){
                Toast.makeText(getApplicationContext(), "terjadi kesalahan jaringan ", Toast.LENGTH_LONG).show();
            }else{
                if (result.equalsIgnoreCase("1")){
                    SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
                    sendPostReqAsyncTask.execute(new String[]{MessengerConnectionService.GROUP_SERVER + "get_profile_group", groupId});
                }else{
                    Toast.makeText(getApplicationContext(), "terjadi kesalahan jaringan ", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    class PostExitGroup extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String paramUrl = params[0];
            String paramName = params[1];

            HttpClient httpClient = null;
            try {
                httpClient = HttpHelper.createHttpClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
            HttpPost httpPost = new HttpPost(paramUrl);
            BasicNameValuePair basicNameValuePair1 = new BasicNameValuePair("name", paramName);
            List nameValuePairList = new ArrayList();
            nameValuePairList.add(basicNameValuePair1);
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
                    cpe.printStackTrace();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            } catch (UnsupportedEncodingException uee) {
                uee.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pdialog.dismiss();
            if(result.startsWith("error") || result==null){
                Toast.makeText(getApplicationContext(), "terjadi kesalahan jaringan ", Toast.LENGTH_LONG).show();
            }else{
                if (result.equalsIgnoreCase("1")){

                    com.byonchat.android.provider.Group g = dbhelper.getGroup(groupId);
                    g.setStatus(com.byonchat.android.model.Group.STATUS_INACTIVE);
                    dbhelper.updateData(g);
                    dbhelper
                            .deleteRows(
                                    Message.TABLE_NAME,
                                    " destination=? OR source =? ",
                                    new String[]{groupId,
                                            groupId}
                            );
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "terjadi kesalahan jaringan ", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


}
