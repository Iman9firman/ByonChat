package com.honda.android;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.honda.android.communication.MessengerConnectionService;
import com.honda.android.communication.NetworkInternetConnectionStatus;
import com.honda.android.list.BotAdapter;
import com.honda.android.provider.BotListDB;
import com.honda.android.provider.Contact;
import com.honda.android.provider.ContactBot;
import com.honda.android.provider.MessengerDatabaseHelper;
import com.honda.android.utils.HttpHelper;
import com.honda.android.utils.Utility;
import com.honda.android.utils.ValidationsKey;

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
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class ListContactBotFragment extends Fragment {

    protected int menuId = 0;
    protected int contextMenuId = 0;
    private Context context;
    protected ProgressDialog pdialog;
    View rootView;
    public final static String roomSticky = "REWARD";
    private ListView lv;
    BotAdapter adapter;

    public final static String URL_GET_DETAIL = "https://"+ MessengerConnectionService.HTTP_SERVER+"/botsearch/detail.php";
    public final static String URL_REMOVE_ROOM = "https://"+ MessengerConnectionService.HTTP_SERVER+"/room/";

    ArrayList<ContactBot> botArrayListist = new ArrayList<ContactBot>();
    private MessengerDatabaseHelper dbhelper;
    BotListDB botListDB ;
    private BroadcastHandler broadcastHandler = new BroadcastHandler();

    public ListContactBotFragment() {
    }

    @Override
    public void onResume() {
     //   refreshList();
        IntentFilter f = new IntentFilter(
               MessengerConnectionService.ACTION_REFRESH_CHAT_HISTORY);
        f.setPriority(2);
        context.registerReceiver(broadcastHandler, f);
        super.onResume();
    }

    @Override
    public void onPause() {
        context.unregisterReceiver(broadcastHandler);
        super.onPause();
    }
    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setContextMenuId(int contextMenuId) {
        this.contextMenuId = contextMenuId;
    }

    public int getContextMenuId() {
        return contextMenuId;
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.list_contact_bot, container, false);

        if(context==null){
            context = getActivity().getApplicationContext();
        }

        if (dbhelper == null) {
            dbhelper = MessengerDatabaseHelper.getInstance(context);
        }
        if (botListDB == null) {
            botListDB = BotListDB.getInstance(context);
        }

        lv = (ListView) rootView.findViewById(R.id.list_view);

        if (pdialog == null) {
            pdialog = new ProgressDialog(getActivity());
            pdialog.setIndeterminate(true);
            pdialog.setMessage("Loading ");
            pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }

        // Adding items to listview
        adapter = new BotAdapter(getActivity(),botArrayListist,true);
        lv.setAdapter(adapter);
        lv.setClickable(true);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, final long id) {
            String idList = adapter.newList().get(position).getId();

            Cursor cur = botListDB.getSingleById(idList);
            String jbId = "";
            if (cur.getCount() > 0) {
                jbId = cur.getString(cur.getColumnIndex(BotListDB.BOT_NAME));
            }
            cur.close();
            if (jbId.startsWith("https://")) {
                Intent intent = new Intent(context, WebViewByonActivity.class);
                intent.putExtra(WebViewByonActivity.KEY_LINK_LOAD, jbId);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                Intent intent = new Intent(context, ConversationActivity.class);
                intent.putExtra(ConversationActivity.KEY_JABBER_ID, jbId.toLowerCase());
                startActivity(intent);
            }
            }
        });

/*
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text

                ListContactBotFragment.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
*/

        registerForContextMenu(lv);
        setHasOptionsMenu(true);
        return rootView;
    }

    class BroadcastHandler extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
           if (MessengerConnectionService.ACTION_REFRESH_CHAT_HISTORY
                    .equals(intent.getAction())) {
              // refreshList();
           }
        }
    }

    /*Intent intent = new Intent(context, WebViewByonActivity.class);
                        intent.putExtra(WebViewByonActivity.KEY_LINK_LOAD, "https://" + MessengerConnectionService.FILE_SERVER + "/personal_room/index.php?userid=" + dbhelper.getMyContact().getJabberId());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        return true;
                    case R.id.menu_other_room:
                        Intent intent2 = new Intent(context, WebViewByonActivity.class);
                        intent2.putExtra(WebViewByonActivity.KEY_LINK_LOAD, "https://" + MessengerConnectionService.FILE_SERVER + "/other_room/index.php");
                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent2);*/



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,  ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo aInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
        final Integer idList = Integer.valueOf(adapter.newList().get(aInfo.position).getId());
        if (!adapter.newList().get(aInfo.position).getName().equalsIgnoreCase(roomSticky)){
            menu.add(1, idList, 1, "Delete");
        }
        menu.add(2, idList, 1, "Info");
        menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int menuindex = item.getItemId();

                switch (menuindex) {
                    case 1:
                        if(NetworkInternetConnectionStatus.getInstance(context).isOnline(context)){
                            pdialog.show();

                            Cursor cur = botListDB.getSingleById(String.valueOf(item.getItemId()));
                            String jbId = "";
                            if (cur.getCount()>0) {
                                jbId =  cur.getString(cur.getColumnIndex(BotListDB.BOT_NAME));
                            }
                            cur.close();

                            String key = new ValidationsKey().getInstance(context).key(true);
                            if (key.equalsIgnoreCase("null")){
                                //Toast.makeText(context,R.string.pleaseTryAgain,Toast.LENGTH_SHORT).show();
                                pdialog.dismiss();
                            }else{
                                new removeBotRequest(context).execute(key,jbId,String.valueOf(item.getItemId()));
                            }
                        }else{
                            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 2:
                        if(NetworkInternetConnectionStatus.getInstance(context).isOnline(context)){

                            Cursor cur = botListDB.getSingleById(String.valueOf(item.getItemId()));
                            String jbId = "";
                            if (cur.getCount()>0) {
                                jbId =  cur.getString(cur.getColumnIndex(BotListDB.BOT_NAME));
                            }
                            cur.close();

                            String key = new ValidationsKey().getInstance(context).key(false);
                            if (key.equalsIgnoreCase("null")){
                                //Toast.makeText(context, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                            }else{
                                new requestDetail(context).execute(key,jbId);
                            }

                        }else{
                            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        Toast.makeText(getView().getContext(), "invalid option!", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_rooms, menu);
        final SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        // Retrieves the SearchView from the search menu item
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));

        // Assign searchable info to SearchView
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryText) {
                // Nothing needs to happen when the user submits the search string
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ListContactBotFragment.this.adapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            /*case R.id.action_goto:
                Intent intent = new Intent(context, WebViewByonActivity.class);
                intent.putExtra(WebViewByonActivity.KEY_LINK_LOAD, "https://" + MessengerConnectionService.FILE_SERVER + "/personal_room/index.php?userid=" + dbhelper.getMyContact().getJabberId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return true;
            case R.id.action_other:
                Intent intent2 = new Intent(context, WebViewByonActivity.class);
                intent2.putExtra(WebViewByonActivity.KEY_LINK_LOAD, "https://" + MessengerConnectionService.FILE_SERVER + "/other_room/index.php?userid=" + dbhelper.getMyContact().getJabberId());
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent2);
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void refreshList(){

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                botArrayListist = botListDB.retriveallList();
                adapter.refresAdapter(botArrayListist);
            }
        });

    }

    public void showInfo(String bot,String desc){

        AlertDialog.Builder alertbox = new AlertDialog.Builder(getActivity());
        alertbox.setTitle(Utility.roomName(getActivity().getApplicationContext(),bot,true));
        alertbox.setMessage(desc);
        alertbox.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });

        alertbox.show();
    }


    class requestDetail extends AsyncTask<String, Void, String> {

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();

        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content = null;
        private boolean error = false;
        private Context mContext;

        String bot = "";
        String desc = "";


        public requestDetail(Context context) {
            this.mContext = context;

        }


        @Override
        protected void onPreExecute() {
            pdialog.show();
        }

        protected String doInBackground(String... key) {
            try {
                bot = key[1];
                HttpClient httpClient = HttpHelper
                        .createHttpClient(context);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);

                Contact contact = dbhelper.getMyContact();

                nameValuePairs.add(new BasicNameValuePair("username", contact.getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("key",key[0]));
                nameValuePairs.add(new BasicNameValuePair("namabot",bot));

                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                HttpPost post = new HttpPost(URL_GET_DETAIL);
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
                    JSONObject result = new JSONObject(content);
                    desc = result.getString("deskripsi");

                } else {
                    //Closes the connection.
                    content = statusLine.getReasonPhrase();
                    response.getEntity().getContent().close();
                    throw new IOException(content);
                }

            } catch (ClientProtocolException e) {
                content =  e.getMessage();
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
            pdialog.dismiss();
        }

        protected void onPostExecute(String content) {
            pdialog.dismiss();
            if (error) {
                if(content.contains("invalid_key")){
                    if(NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)){
                        pdialog.show();
                        String key = new ValidationsKey().getInstance(mContext).key(true);
                        if (key.equalsIgnoreCase("null")){
                          //  Toast.makeText(mContext, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                            pdialog.dismiss();
                        }else{
                            new requestDetail(mContext).execute(key,bot);
                        }
                    }else{
                        Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, desc, Toast.LENGTH_SHORT).show();
                }
            } else {
                showInfo( bot, desc);
            }
        }

    }

    class removeBotRequest extends AsyncTask<String, Void, String> {

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();

        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content = null;
        private boolean error = false;
        private Context mContext;

        private MessengerDatabaseHelper messengerHelper;
        String code = "";
        String desc = "";
        String nameBot = "";
        String id = "";

        public removeBotRequest(Context context) {
            this.mContext = context;

        }


        @Override
        protected void onPreExecute() {
            pdialog.show();
        }

        protected String doInBackground(String... key) {
            try {

                if (messengerHelper == null) {
                    messengerHelper = MessengerDatabaseHelper.getInstance(context);
                }

                HttpClient httpClient = HttpHelper
                        .createHttpClient(mContext);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);

                Contact contact = messengerHelper.getMyContact();
                nameBot = key[1];
                id = key[2];
                nameValuePairs.add(new BasicNameValuePair("username", contact.getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("key",key[0]));
                nameValuePairs.add(new BasicNameValuePair("namaroom",nameBot));
                nameValuePairs.add(new BasicNameValuePair("action","0"));

                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                HttpPost post = new HttpPost(URL_REMOVE_ROOM);
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

                    if(!content.startsWith("<")){
                        JSONObject jObject = new JSONObject(content);
                        code = jObject.getString("code");
                        desc = jObject.getString("description");
                        if(!code.equalsIgnoreCase("200")) error=true;
                    }else{
                        code = "400";
                        error = true;
                    }

                } else {
                    error = true;
                    content = statusLine.getReasonPhrase();
                    response.getEntity().getContent().close();
                    throw new IOException(content);
                }

            } catch (ClientProtocolException e) {
                content =  e.getMessage();
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
            pdialog.dismiss();
        }

        protected void onPostExecute(String content) {
            pdialog.dismiss();
            if (error) {
                if(content.contains("invalid_key")){
                    if(NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)){
                        pdialog.show();
                        String key = new ValidationsKey().getInstance(mContext).key(true);
                        if (key.equalsIgnoreCase("null")){
                           // Toast.makeText(context,R.string.pleaseTryAgain,Toast.LENGTH_SHORT).show();
                            pdialog.dismiss();
                        }else{
                            new removeBotRequest(mContext).execute(key,nameBot,id);
                        }
                    }else{
                        Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(mContext, desc, Toast.LENGTH_SHORT).show();
                }
            } else {
                if(code.equalsIgnoreCase("200")){
                    botListDB.deletebyId(id);
                    refreshList();
                }else{
                    Toast.makeText(context, desc, Toast.LENGTH_SHORT);
                }
            }
        }

    }



 }