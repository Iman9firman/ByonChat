package com.byonchat.android;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.list.BotAdapter;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.ContactBot;
import com.byonchat.android.provider.DataBaseDropDown;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.RoomsDB;
import com.byonchat.android.utils.DialogUtil;
import com.byonchat.android.utils.HttpHelper;
import com.byonchat.android.utils.RequestKeyTask;
import com.byonchat.android.utils.TaskCompleted;
import com.byonchat.android.utils.UtilsPD;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class ListSelectedBotFragment extends Fragment {

    protected int menuId = 0;
    protected int contextMenuId = 0;
    private Context context;
    protected ProgressDialog pdialog;
    View rootView;
    private ListView lv;
    private TextView emptyList;
    BotAdapter adapter;
    RoomsDB roomsDB;
    BotListDB botListDB;
    private boolean show = false;
    ArrayList<ContactBot> botArrayListist = new ArrayList<ContactBot>();
    private SwipeRefreshLayout swipeRefreshLayout;
    String URLLAPORSELECTED = "https://" + MessengerConnectionService.HTTP_SERVER + "/room/selectapop.php";
    private MessengerDatabaseHelper messengerHelper;
    private LaporSelectedRoom laporSelectedRoom;
    String roomid = "";
    private ProgressDialog progressDialog;

    public ListSelectedBotFragment(boolean s, Context ctx) {
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

        if (roomsDB == null) {
            roomsDB = new RoomsDB(context);
        }
        if (botListDB == null) {
            botListDB = BotListDB.getInstance(context);
        }

        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(context);
        }

        lv = (ListView) rootView.findViewById(R.id.list_view);
        emptyList = (TextView) rootView.findViewById(R.id.emptyList);
        emptyList.setText("You have not selected any room.");
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setColorSchemeColors(
                Color.GRAY, //This method will rotate
                Color.GRAY, //colors given to it when
                Color.GRAY,//loader continues to
                Color.GRAY);//refresh.
        //  swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.gray);

        roomsDB.open();
        botArrayListist = roomsDB.retrieveRooms("2");
        roomsDB.close();

        if (botArrayListist.size() > 0) {
            refreshList();
        } else {
            lv.setVisibility(View.GONE);
            emptyList.setVisibility(View.VISIBLE);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                roomsDB.open();
                                botArrayListist = roomsDB.retrieveRooms("2");
                                roomsDB.close();

                                if (botArrayListist.size() > 0) {
                                    refreshList();
                                } else {
                                    lv.setVisibility(View.GONE);
                                    emptyList.setVisibility(View.VISIBLE);
                                }
                                swipeRefreshLayout.setRefreshing(false);
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

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView parent, View view, int position, long id) {
                roomid = botArrayListist.get(position).getName();
                final Dialog dialogConfirmation;
                dialogConfirmation = DialogUtil.customDialogConversationConfirmation((Activity) context);
                dialogConfirmation.show();

                TextView txtConfirmation = (TextView) dialogConfirmation.findViewById(R.id.confirmationTxt);
                TextView descConfirmation = (TextView) dialogConfirmation.findViewById(R.id.confirmationDesc);
                txtConfirmation.setText("Delete Confirmation");
                descConfirmation.setVisibility(View.VISIBLE);
                descConfirmation.setText("Do you want to delete this room?");

                Button btnNo = (Button) dialogConfirmation.findViewById(R.id.btnNo);
                Button btnYes = (Button) dialogConfirmation.findViewById(R.id.btnYes);

                btnNo.setText("Cancel");
                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogConfirmation.dismiss();
                    }
                });

                btnYes.setText("Delete");
                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestKey();
                        dialogConfirmation.dismiss();
                    }
                });
                return true;
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


    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = UtilsPD.createProgressDialog(context);
        }
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        roomsDB.open();
        botArrayListist = roomsDB.retrieveRooms("2");
        roomsDB.close();

        if (botArrayListist.size() > 0) {
            refreshList();
        } else {
            lv.setVisibility(View.GONE);
            emptyList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }

    public void refreshList() {
        new Handler(Looper.getMainLooper()).post(new Runnable() { // Tried new Handler(Looper.myLopper()) also
            @Override
            public void run() {
                emptyList.setVisibility(View.GONE);
                lv.setVisibility(View.VISIBLE);
                adapter = new BotAdapter(context, botArrayListist, show);
                roomsDB.open();
                botArrayListist = roomsDB.retrieveRooms("2");
                roomsDB.close();
                adapter = new BotAdapter(context, botArrayListist, show);
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
                            laporSelectedRoom = new LaporSelectedRoom(context);
                            laporSelectedRoom.execute(key);
                        }
                    }
                }, context);

                testAsyncTask.execute();
            }
        });
    }

    class LaporSelectedRoom extends AsyncTask<String, Void, String> {

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private String content = null;
        private boolean error = false;
        private Context mContext;

        public LaporSelectedRoom(Context context) {
            this.mContext = context;

        }

        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        protected String doInBackground(String... key) {
            try {
                HttpClient httpClient = HttpHelper
                        .createHttpClient(mContext);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);

                nameValuePairs.add(new BasicNameValuePair("username", messengerHelper.getMyContact().getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("key", key[0]));
                nameValuePairs.add(new BasicNameValuePair("room_id", roomid));
                nameValuePairs.add(new BasicNameValuePair("aksi", "2"));

                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                HttpPost post = new HttpPost(URLLAPORSELECTED);
                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response;
                response = httpClient.execute(post);
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
            dismissProgressDialog();
            if (error) {
                if (content.contains("invalid_key")) {
                    if (NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)) {
                        String key = new ValidationsKey().getInstance(mContext).key(true);
                        if (key.equalsIgnoreCase("null")) {
                            // Toast.makeText(mContext, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                        } else {
                            laporSelectedRoom = new LaporSelectedRoom(context);
                            laporSelectedRoom.execute(key);
                        }
                    } else {
                        // Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Toast.makeText(mContext, content, Toast.LENGTH_LONG).show();
                }
            } else {

                Cursor cur = botListDB.getSingleRoom(roomid);

                if (cur.getCount() > 0) {
                    String aaContent = cur.getString(cur.getColumnIndex(BotListDB.ROOM_CONTENT));
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(aaContent);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String aaId = jsonArray.getJSONObject(i).getString("id_rooms_tab").toString();
                            String category = jsonArray.getJSONObject(i).getString("category_tab").toString();
                            if (category.equalsIgnoreCase("4")) {
                                Cursor cursor = botListDB.getSingleRoomDetailForm(roomid, aaId);
                                if (cursor.getCount() > 0) {
                                    String contentDetail = cursor.getString(cursor.getColumnIndexOrThrow(BotListDB.ROOM_CONTENT));
                                    JSONArray jsonArrayDetail = new JSONArray(contentDetail);
                                    for (int ii = 0; ii < jsonArrayDetail.length(); ii++) {
                                        String value = jsonArrayDetail.getJSONObject(ii).getString("value").toString();
                                        String tt = jsonArrayDetail.getJSONObject(ii).getString("type").toString();
                                        if (tt.equalsIgnoreCase("dropdown_dinamis")) {
                                            JSONObject jObject = new JSONObject(value);
                                            String url = jObject.getString("url");
                                            String[] aa = url.split("/");
                                            final String nama = aa[aa.length - 1].toString();

                                            File newDB = new File(DataBaseDropDown.getDatabaseFolder() + nama);
                                            if (newDB.exists()) {
                                                newDB.delete();
                                            }

                                        }
                                        /*kodepos delete otomatis by system
                                         else if (tt.equalsIgnoreCase("dropdown_wilayah") || tt.equalsIgnoreCase("input_kodepos")) {
                                            File newDB = new File(DataBaseDropDown.getDatabaseFolder() + "daftarkodepos.sqlite");
                                            if (newDB.exists()) {
                                                newDB.delete();
                                            }

                                        }*/
                                    }

                                }

                            }
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                botListDB.deleteRoomsbyTAB(roomid);
                botListDB.deleteRoomsDetailAllItemSku(roomid);

                roomsDB.open();
                roomsDB.deletebyName(roomid);
                roomsDB.close();

                roomsDB.open();
                botArrayListist = roomsDB.retrieveRooms("2");
                roomsDB.close();
                if (botArrayListist.size() > 0) {
                    refreshList();
                } else {
                    lv.setVisibility(View.GONE);
                    emptyList.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}