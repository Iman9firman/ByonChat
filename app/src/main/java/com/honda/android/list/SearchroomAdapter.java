package com.honda.android.list;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.appcompat.widget.PopupMenu;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.honda.android.ByonChatMainRoomActivity;
import com.honda.android.ConversationActivity;
import com.honda.android.R;
import com.honda.android.communication.MessengerConnectionService;
import com.honda.android.communication.NetworkInternetConnectionStatus;
import com.honda.android.list.utilLoadImage.ImageLoader;
import com.honda.android.provider.Contact;
import com.honda.android.provider.ContactBot;
import com.honda.android.provider.Message;
import com.honda.android.provider.MessengerDatabaseHelper;
import com.honda.android.provider.RoomsDB;
import com.honda.android.ui.adapter.OnItemClickListener;
import com.honda.android.utils.HttpHelper;
import com.honda.android.utils.RequestKeyTask;
import com.honda.android.utils.TaskCompleted;
import com.honda.android.utils.UtilsPD;
import com.honda.android.utils.ValidationsKey;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class SearchroomAdapter extends BaseAdapter implements Filterable {

    String URLLAPORSELECTED = "https://" + MessengerConnectionService.HTTP_SERVER + "/room/selectapop.php";
    private static ArrayList<ContactBot> catArrayList = new ArrayList<ContactBot>();
    private static ArrayList<ContactBot> catArray = new ArrayList<ContactBot>();
    private LayoutInflater inflater;
    public Context context;
    ContactsFilter mContactsFilter;
    public ImageLoader imageLoader;
    public boolean showTitle = false;
    String bold;
    private RoomsDB roomsDB;
    LaporSelectedRoom laporSelectedRoom;
    MessengerDatabaseHelper messengerHelper = null;
    Contact contact;
    String id = "", name = "", desc = "", realname = "", link = "", type = "", tipe_room = "";
    public OnItemClickListener itemClickListener;

    public SearchroomAdapter(Context ctx, ArrayList<ContactBot> contactBot, boolean showTitle) {
        context = ctx;
        this.showTitle = showTitle;
        this.catArrayList = contactBot;
        this.catArray = contactBot;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(context);
    }

    public SearchroomAdapter(Context ctx,
                             ArrayList<ContactBot> contactBot,
                             boolean showTitle, String b,
                             Contact contact, MessengerDatabaseHelper messengerHelper,
                             OnItemClickListener itemClickListener) {
        bold = b;
        context = ctx;
        this.showTitle = showTitle;
        this.catArrayList = contactBot;
        this.catArray = contactBot;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(context);
        this.contact = contact;
        this.messengerHelper = messengerHelper;
        this.itemClickListener = itemClickListener;
    }

    public int getCount() {
        return catArrayList.size();
    }

    public Object getItem(int position) {
        return catArrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public ArrayList<ContactBot> newList() {
        ArrayList<ContactBot> list = catArrayList;
        return (list);
    }


    public class ViewHolder {
        TextView txtName;
        TextView txtInfo;
        ImageView btnPopup;
        Target imagePhoto;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_item_search_room, null);

            holder.btnPopup = (ImageView) convertView.findViewById(R.id.button_popup);
            holder.imagePhoto = (Target) convertView.findViewById(R.id.imagePhoto);
            holder.txtName = (TextView) convertView.findViewById(R.id.textTitle);
            holder.txtInfo = (TextView) convertView.findViewById(R.id.textInfo);
            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();

        if (catArrayList.get(position).getLink().equalsIgnoreCase("") || catArrayList.get(position).getLink().equalsIgnoreCase("null")) {
            Picasso.with(context).load(R.drawable.ic_no_photo).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(holder.imagePhoto);
        } else {
            Picasso.with(context).load(catArrayList.get(position).getLink()).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(holder.imagePhoto);
        }
        holder.txtName.setText(Message.highlightText(bold, catArrayList.get(position).getRealname()));
        String desc = catArrayList.get(position).getDesc();
        if (Message.isJSONValid(catArrayList.get(position).getDesc())) {
            JSONObject jObject = null;
            try {
                jObject = new JSONObject(catArrayList.get(position).getDesc());
                desc = jObject.getString("desc");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String text = Html.fromHtml(desc).toString();
        if (text.contains("<")) {
            text = Html.fromHtml(Html.fromHtml(text).toString()).toString();
        }
        holder.txtInfo.setText(Message.highlightText(bold, Message.parsedMessageText(text)));

        holder.btnPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showPopup(v, position);
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(v, position);
                }
            }
        });

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (mContactsFilter == null)
            mContactsFilter = new ContactsFilter();

        return mContactsFilter;
    }

    private class ContactsFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                results.values = catArray;
                results.count = catArray.size();
            } else {
                ArrayList<ContactBot> filteredContacts = new ArrayList<ContactBot>();
                for (ContactBot c : catArray) {
                    if (c.getRealname().contains(constraint.toString().replace(" ", "_"))) {
                        filteredContacts.add(c);
                    }
                }
                results.values = filteredContacts;
                results.count = filteredContacts.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            catArrayList = (ArrayList<ContactBot>) results.values;
            notifyDataSetChanged();
        }
    }

    public synchronized void refresAdapter(ArrayList<ContactBot> list) {
        catArrayList.clear();
        catArrayList.addAll(list);
        catArray.clear();
        catArray.addAll(list);
        notifyDataSetChanged();
    }

    public synchronized void refresAdapter() {
        catArrayList.clear();
        catArray.clear();
        notifyDataSetChanged();
    }

    private void showPopup(View view, final int position) {
        // pass the imageview id
        View menuItemView = view.findViewById(R.id.button_popup);
        final PopupMenu popup = new PopupMenu(context, menuItemView);
        MenuInflater inflate = popup.getMenuInflater();
        try {
            JSONObject jObj = new JSONObject(catArrayList.get(position).getType());
            type = jObj.getString("type");
            tipe_room = jObj.getString("tipe_room");

            if (Integer.valueOf(tipe_room) == 1) {
                inflate.inflate(R.menu.menu_search_room_personal, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_chat:
                                Intent intent = new Intent(context, ConversationActivity.class);
                                intent.putExtra(ConversationActivity.KEY_JABBER_ID, catArrayList.get(position).getName());
                                context.startActivity(intent);
                                break;
                            default:
                                return false;
                        }
                        return false;
                    }
                });

            } else if (Integer.valueOf(tipe_room) == 2) {
                inflate.inflate(R.menu.menu_search_room, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_chat:
                                Intent intent = new Intent(context, ConversationActivity.class);
                                intent.putExtra(ConversationActivity.KEY_JABBER_ID, catArrayList.get(position).getName());
                                context.startActivity(intent);
                                break;
                            case R.id.action_addToSelected:
                                name = catArrayList.get(position).getName();
                                desc = catArrayList.get(position).getDesc();
                                realname = catArrayList.get(position).getRealname();
                                link = catArrayList.get(position).getLink();
                                String targetURL = "";

                                try {
                                    JSONObject jObj = new JSONObject(catArrayList.get(position).getType());
                                    targetURL = jObj.getString("path");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                type = "2";
                                insertToDB(String.valueOf(position + 1), catArrayList.get(position).getName(), catArrayList.get(position).getDesc(), catArrayList.get(position).getRealname(), catArrayList.get(position).getLink(), catArrayList.get(position).getType(), "2", targetURL);
                                popup.dismiss();
                                break;
                            default:
                                return false;
                        }
                        return false;
                    }
                });
            } else if (Integer.valueOf(tipe_room) == 3) {
                inflate.inflate(R.menu.menu_search_room, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_chat:
                                Intent intent = new Intent(context, ConversationActivity.class);
                                intent.putExtra(ConversationActivity.KEY_JABBER_ID, catArrayList.get(position).getName());
                                context.startActivity(intent);
                                break;
                            case R.id.action_addToSelected:
                                name = catArrayList.get(position).getName();
                                desc = catArrayList.get(position).getDesc();
                                realname = catArrayList.get(position).getRealname();
                                link = catArrayList.get(position).getLink();
                                type = "2";
                                String targetURL = "";

                                try {
                                    JSONObject jObj = new JSONObject(catArrayList.get(position).getType());
                                    targetURL = jObj.getString("path");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                insertToDB(String.valueOf(position + 1), catArrayList.get(position).getName(), catArrayList.get(position).getDesc(), catArrayList.get(position).getRealname(), catArrayList.get(position).getLink(), catArrayList.get(position).getType(), "2", targetURL);
                                popup.dismiss();
                                break;
                            default:
                                return false;
                        }
                        return false;
                    }
                });
            }
        } catch (Exception e) {
        }


        popup.show();
    }

    public void insertToDB(String id, String name, String desc, String realname, String link, String json, String type, String path) {
        if (roomsDB == null) {
            roomsDB = new RoomsDB(context);
        }

        roomsDB.open();
        catArray = roomsDB.retrieveRoomsByName(name, "2");
        roomsDB.close();
        if (catArray.size() > 0) {
            Toast.makeText(context, realname + " is already added to selected rooms", Toast.LENGTH_SHORT).show();
        } else {
            requestKey(path, json);
        }
    }

    private void requestKey(final String path, final String json) {
        RequestKeyTask testAsyncTask = new RequestKeyTask(new TaskCompleted() {
            @Override
            public void onTaskDone(String key) {
                if (key.equalsIgnoreCase("null")) {
                    Toast.makeText(context, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                } else {
                    laporSelectedRoom = new LaporSelectedRoom(context, json);
                    laporSelectedRoom.execute(key, path);
                }
            }
        }, context);

        testAsyncTask.execute();
    }

    class LaporSelectedRoom extends AsyncTask<String, Void, String> {

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();

        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content = null;
        private boolean error = false;
        private Context mContext;
        private ProgressDialog progressDialog;

        String path = "";
        String json = "";

        public LaporSelectedRoom(Context context, String json) {
            this.mContext = context;
            this.json = json;

        }

        @Override
        protected void onPreExecute() {
            if (progressDialog == null) {
                progressDialog = UtilsPD.createProgressDialog(context);
                progressDialog.show();
            }
        }

        protected String doInBackground(String... key) {
            try {
                HttpClient httpClient = HttpHelper
                        .createHttpClient(mContext);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);

                nameValuePairs.add(new BasicNameValuePair("username", messengerHelper.getMyContact().getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("key", key[0]));
                nameValuePairs.add(new BasicNameValuePair("room_id", name));
                nameValuePairs.add(new BasicNameValuePair("aksi", "1"));

                path = key[1];

                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                HttpPost post = new HttpPost(URLLAPORSELECTED);
                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                //Response from the Http Request
                response = httpclient.execute(post);
                StatusLine statusLine = response.getStatusLine();
                //Check the Http Request for success
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    content = out.toString();


                } else {
                    //Closes the connection.
                    error = true;
                    content = statusLine.getReasonPhrase();
                    response.getEntity().getContent().close();
                    throw new IOException(content);
                }

            } catch (ClientProtocolException e) {
                content = e.getMessage();
                error = true;
            } catch (IOException e) {
                content = e.getMessage();
                error = true;
            } catch (Exception e) {
                error = true;
            }

            return content;
        }

        protected void onCancelled() {
        }

        protected void onPostExecute(String content) {
            progressDialog.dismiss();
            if (error) {
                if (content.contains("invalid_key")) {
                    if (NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)) {
                        String key = new ValidationsKey().getInstance(mContext).key(true);
                        if (key.equalsIgnoreCase("null")) {
                            Toast.makeText(mContext, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                        } else {
                            laporSelectedRoom = new LaporSelectedRoom(context, json);
                            laporSelectedRoom.execute(key, path);
                        }
                    } else {
                        Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, content, Toast.LENGTH_LONG).show();
                }
            } else {

                roomsDB.open();
                boolean isActived = true;
                ArrayList<ContactBot> botArrayListist = roomsDB.retrieveRooms("2");
                if (botArrayListist.size() > 0) {
                    isActived = false;
                }
                ContactBot contactBot = new ContactBot("", name, desc, realname, link, type, isActived, json);
                roomsDB.insertRooms(contactBot);
                roomsDB.close();

                Intent intent = new Intent(context, ByonChatMainRoomActivity.class);
                intent.putExtra(ConversationActivity.KEY_JABBER_ID, name);
                intent.putExtra(ConversationActivity.KEY_TITLE, path);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

                Toast.makeText(context, realname + " has been added to selected rooms", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
