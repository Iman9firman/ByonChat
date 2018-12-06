package com.byonchat.android;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.list.BotTrendingAdapter;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.ContactBot;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.RoomsDB;
import com.byonchat.android.utils.HttpHelper;
import com.byonchat.android.utils.RequestKeyTask;
import com.byonchat.android.utils.TaskCompleted;
import com.byonchat.android.utils.ValidationsKey;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class ListTrendingBotFragment extends Fragment {

    protected int menuId = 0;
    protected int contextMenuId = 0;
    private Contact contact;
    protected ProgressDialog pdialog;
    View rootView;
    String URLTRENDING = "https://" + MessengerConnectionService.HTTP_SERVER + "/room/trending_rooms.php";
    private ListView lv;
    private TextView emptyList;
    BotTrendingAdapter adapter;
    private boolean show = false;
    ArrayList<ContactBot> botArrayListist = new ArrayList<ContactBot>();
    RequestSearchResult requestSearchResult;
    MessengerDatabaseHelper messengerHelper;
    private SwipeRefreshLayout swipeRefreshLayout;
    RoomsDB roomsDB;
    private Context context;

    public ListTrendingBotFragment(boolean s, Context ctx) {
        this.show = s;
        context = ctx;
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


        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(context);
        }

        if (roomsDB == null) {
            roomsDB = new RoomsDB(context);
        }

        contact = messengerHelper.getMyContact();

        lv = (ListView) rootView.findViewById(R.id.list_view);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        emptyList = (TextView) rootView.findViewById(R.id.emptyList);
        emptyList.setText("No trending rooms available.");

        swipeRefreshLayout.setColorSchemeColors(
                Color.GRAY, //This method will rotate
                Color.GRAY, //colors given to it when
                Color.GRAY,//loader continues to
                Color.GRAY);//refresh.
        //  swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.gray);

        if (pdialog == null) {
            pdialog = new ProgressDialog(context);
            pdialog.setIndeterminate(true);
            pdialog.setMessage("Loading ");
            pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }

        roomsDB.open();
        botArrayListist = roomsDB.retrieveRooms("1");
        roomsDB.close();

        if (botArrayListist.size() > 0) {
            refreshList();
        } else {
            requestKey();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                requestKey();
                            }
                        }
                );
            }
        });

        lv.setClickable(true);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, final long id) {
                if (Message.isJSONValid(botArrayListist.get(position).getDesc())) {
                    JSONObject jObject = null;
                    try {
                        jObject = new JSONObject(botArrayListist.get(position).getDesc());
                        String desc = jObject.getString("desc");
                        String classs = jObject.getString("apps");
                        final String url = jObject.getString("url");

                        boolean isAppInstalled = appInstalledOrNot(classs);

                        if (isAppInstalled) {
                            Intent LaunchIntent = context.getPackageManager()
                                    .getLaunchIntentForPackage(classs);
                            startActivity(LaunchIntent);
                        } else {
                            AlertDialog.Builder alertbox = new AlertDialog.Builder(context);
                            alertbox.setMessage("Please Install the Plug-in first ");
                            alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    openWebPage(url);
                                }

                            });
                            alertbox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                }
                            });
                            alertbox.show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Intent intent = new Intent(context, ByonChatMainRoomActivity.class);
                    intent.putExtra(ConversationActivity.KEY_JABBER_ID, botArrayListist.get(position).getName());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

        registerForContextMenu(lv);
        setHasOptionsMenu(true);
        return rootView;
    }

    public void openWebPage(String url) {
        try {
            Uri webpage = Uri.parse(url);
            Intent myIntent = new Intent(Intent.ACTION_VIEW, webpage);
            startActivity(myIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No application can handle this request. Please install a web browser or check your URL.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    public void refreshList() {
        new Handler(Looper.getMainLooper()).post(new Runnable() { // Tried new Handler(Looper.myLopper()) also
            @Override
            public void run() {
                roomsDB.open();
                botArrayListist = roomsDB.retrieveRooms("1");
                if (botArrayListist.size() > 0) {
                    lv.setVisibility(View.VISIBLE);
                    emptyList.setVisibility(View.GONE);
                } else {
                    lv.setVisibility(View.GONE);
                    emptyList.setVisibility(View.VISIBLE);
                }
                roomsDB.close();
                adapter = new BotTrendingAdapter(context, botArrayListist, show);
                lv.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void requestKey() {
        new Handler(Looper.getMainLooper()).post(new Runnable() { // Tried new Handler(Looper.myLopper()) also
            @Override
            public void run() {
                RequestKeyTask testAsyncTask = new RequestKeyTask(new TaskCompleted() {
                    @Override
                    public void onTaskDone(String key) {
                        if (key.equalsIgnoreCase("null")) {
                            // Toast.makeText(context, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                        } else {
                            requestSearchResult = new RequestSearchResult(context);
                            requestSearchResult.execute(key);
                        }
                    }
                }, context);

                testAsyncTask.execute();
            }
        });
    }

    class RequestSearchResult extends AsyncTask<String, Void, String> {

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();

        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content = null;
        private boolean error = false;
        private Context mContext;

        public RequestSearchResult(Context context) {
            this.mContext = context;
        }

        @Override
        protected void onPreExecute() {
        }

        protected String doInBackground(String... key) {
            try {
                HttpClient httpClient = HttpHelper
                        .createHttpClient(mContext);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);

                nameValuePairs.add(new BasicNameValuePair("userid", contact.getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("key", key[0]));

                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                HttpPost post = new HttpPost(URLTRENDING);
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
                    Object json = new JSONTokener(content).nextValue();
                    if (json instanceof JSONObject) {
                        if (((JSONObject) json).getString("code_text").equalsIgnoreCase("not_found")) {
                            roomsDB.open();
                            roomsDB.deleteRoomsTrending();
                            roomsDB.close();
                        }
                    } else {
                        JSONArray result = new JSONArray(content);
                        roomsDB.open();
                        roomsDB.deleteRoomsTrending();
                        for (int i = 0; i < result.length(); i++) {
                            JSONObject obj = result.getJSONObject(i);
                            botArrayListist = new ArrayList<ContactBot>();
                            String username = obj.getString("byonchat_id");
                            String description = obj.getString("description");
                            String nama_display = obj.getString("nama_display");
                            String foto = obj.getString("foto");
                            ContactBot aa = new ContactBot(String.valueOf(i + 1), username, description, nama_display, foto, "1", false, "");
                            roomsDB.insertRooms(aa);
                            if (isCancelled()) break;
                        }
                        roomsDB.close();
                    }
                    roomsDB.close();
                } else {
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
            /*swipeRefreshLayout.setRefreshing(false);*/
        }

        protected void onPostExecute(String content) {
            if (error) {
                if (content.contains("invalid_key")) {
                    if (NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)) {
                        String key = new ValidationsKey().getInstance(mContext).key(true);
                        if (key.equalsIgnoreCase("null")) {
                            swipeRefreshLayout.setRefreshing(false);
                            //Toast.makeText(mContext, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                        } else {
                            requestSearchResult = new RequestSearchResult(context);
                            requestSearchResult.execute(key);
                        }
                    } else {
                        swipeRefreshLayout.setRefreshing(false);
                        // Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    //Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT).show();
                }
            } else {
                //Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT).show();
                refreshList();
            }
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}