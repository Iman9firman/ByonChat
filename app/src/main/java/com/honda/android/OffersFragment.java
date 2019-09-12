package com.honda.android;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.honda.android.communication.MessengerConnectionService;
import com.honda.android.communication.NetworkInternetConnectionStatus;
import com.honda.android.list.ListOffersAdapter;
import com.honda.android.provider.BotListDB;
import com.honda.android.provider.Contact;
import com.honda.android.provider.ContactBot;
import com.honda.android.provider.MessengerDatabaseHelper;
import com.honda.android.provider.VouchersDB;
import com.honda.android.provider.VouchersModel;
import com.honda.android.utils.HttpHelper;
import com.honda.android.utils.RequestKeyTask;
import com.honda.android.utils.TaskCompleted;
import com.honda.android.utils.UploadService;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;


@SuppressLint("ValidFragment")
//1 chat
//2 add
//3 apps
public class OffersFragment extends Fragment implements OnRefreshListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public final static String URL_LAPOR_OFFERS = "https://"+ MessengerConnectionService.HTTP_SERVER+"/offers/";
    public static final String ARG_SECTION_NUMBER = "section_number";
    public final static String URL_GET_VOUCHER = "https://" + MessengerConnectionService.HTTP_SERVER + "/offers/index.php";
    private MessengerDatabaseHelper messengerHelper;
    private Contact contact;
    private Context context;
    ListOffersAdapter adapter;
    private ListView lv;
    private ArrayList<VouchersModel> listItem;
    private SwipeRefreshLayout swipeRefreshLayout;
    VouchersDB vouchersDB;
    BotListDB botListDB;
    View rootView;
    protected ProgressDialog pdialog;
    private BroadcastHandler broadcastHandler = new BroadcastHandler();
    RequestOffers requestOffers;

    public OffersFragment(Context ctx) {
        context = ctx;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.offers_fragment, container, false);

        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(context);
        }
        if (botListDB == null) {
            botListDB = BotListDB.getInstance(context);
        }
        if (vouchersDB == null) {
            vouchersDB = new VouchersDB(context);
        }

        lv = (ListView) rootView.findViewById(R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(
                Color.GRAY, //This method will rotate
                Color.GRAY, //colors given to it when
                Color.GRAY,//loader continues to
                Color.GRAY);//refresh.
        //  swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.gray);

        requestOffers = new RequestOffers(context);
        if (pdialog == null) {
            pdialog = new ProgressDialog(context);
            pdialog.setIndeterminate(true);
            pdialog.setMessage("Please wait a moment");
            pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        adapter = new ListOffersAdapter(context, listItem);
        contact = messengerHelper.getMyContact();

        vouchersDB.open();
        listItem = vouchersDB.retriveOffers();
        vouchersDB.close();

        if (listItem.size() > 0) {
            refreshList();
        }else{
            requestKey();
        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, final int position, final long id) {
                final String idList = listItem.get(position).getId();
                if (!idList.equalsIgnoreCase("")) {
                    if (listItem.get(position).getType().equalsIgnoreCase("1")) {
                        final android.app.AlertDialog.Builder alertbox = new android.app.AlertDialog.Builder(context);
                        alertbox.setTitle(listItem.get(position).getJudul());
                        alertbox.setMessage(listItem.get(position).getDetail());
                        alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {

                                vouchersDB.open();
                                vouchersDB.deletebyIdOffers(idList);
                                vouchersDB.close();

                                Intent intent = new Intent(context, ConversationActivity.class);
                                intent.putExtra(ConversationActivity.KEY_JABBER_ID, listItem.get(position).getSub());
                                startActivity(intent);
                            }
                        });
                        alertbox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        });
                        alertbox.show();


                    } else if (listItem.get(position).getType().equalsIgnoreCase("2")) {
                        final android.app.AlertDialog.Builder alertbox = new android.app.AlertDialog.Builder(context);
                        alertbox.setMessage(listItem.get(position).getJudul());
                        alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                pdialog.show();
                                vouchersDB.open();
                                vouchersDB.deletebyIdOffers(idList);
                                vouchersDB.close();
                                String key = new ValidationsKey().getInstance(context).key(true);
                                if (!key.equalsIgnoreCase("null")) {
                                    new laporOffers(context).execute(key, idList);
                                } else {
                                  //  Toast.makeText(context, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                                    pdialog.dismiss();
                                }
                            }
                        });
                        alertbox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        });
                        alertbox.show();

                    } else if (listItem.get(position).getType().equalsIgnoreCase("3")) {

                        final android.app.AlertDialog.Builder alertbox = new android.app.AlertDialog.Builder(context);
                        alertbox.setMessage(listItem.get(position).getJudul());
                        alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {

                                vouchersDB.open();
                                vouchersDB.deletebyId(idList);
                                vouchersDB.close();
                                final String appPackageName = listItem.get(position).getSub().trim(); // getPackageName() from Context or Activity object
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                                }

                                Intent intent = new Intent(context, UploadService.class);
                                intent.putExtra(UploadService.ACTION, "cekApps");
                                intent.putExtra(UploadService.ID_OFFERS, listItem.get(position).getId());
                                intent.putExtra("package", appPackageName);
                                context.startService(intent);

                            }
                        });
                        alertbox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        });
                        alertbox.show();

                    }

                }
                refreshList();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(this);
        //     setHasOptionsMenu(true);
        return rootView;
    }
    @Override
    public void onRefresh() {
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        requestKey();
                        swipeRefreshLayout.setRefreshing(true);
                    }
                }
        );
    }

    @Override
    public void onResume() {

        IntentFilter f = new IntentFilter(MessengerConnectionService.ACTION_REFRESH_OFFERS);
        f.setPriority(1);
        context.registerReceiver(broadcastHandler, f);
        refreshList();
        super.onResume();
    }

    @Override
    public void onPause() {
        requestOffers.cancel(true);
        context.unregisterReceiver(broadcastHandler);
        super.onPause();
    }

    private void requestKey() {
        new Handler(Looper.getMainLooper()).post(new Runnable() { // Tried new Handler(Looper.myLopper()) also
            @Override
            public void run() {
                RequestKeyTask testAsyncTask = new RequestKeyTask(new TaskCompleted() {
                    @Override
                    public void onTaskDone(String key) {
                        if (key.equalsIgnoreCase("null")) {
                            swipeRefreshLayout.setRefreshing(false);
                            //Toast.makeText(context, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                        } else {
                            requestOffers = new RequestOffers(context);
                            requestOffers.execute(key);
                        }
                    }
                }, context);

                testAsyncTask.execute();
            }
        });
    }

    public void refreshList() {
        new Handler(Looper.getMainLooper()).post(new Runnable() { // Tried new Handler(Looper.myLopper()) also
            @Override
            public void run() {
                vouchersDB.open();
                listItem = vouchersDB.retriveOffers();
                vouchersDB.close();
                adapter = new ListOffersAdapter(context, listItem);
                lv.setAdapter(adapter);
            }
        });
    }

    class BroadcastHandler extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MessengerConnectionService.ACTION_REFRESH_OFFERS
                    .equals(intent.getAction())) {
                refreshList();
            }
        }
    }

    class RequestOffers extends AsyncTask<String, Void, String> {

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();

        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content = null;
        private boolean error = false;
        private Context mContext;

        public RequestOffers(Context context) {
            this.mContext = context;

        }


        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);
        }

        protected String doInBackground(String... key) {
            try {
                HttpClient httpClient = HttpHelper
                        .createHttpClient(mContext);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);

                nameValuePairs.add(new BasicNameValuePair("username", contact.getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("key", key[0]));
                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                HttpPost post = new HttpPost(URL_GET_VOUCHER);
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

                    JSONObject result = new JSONObject(content.toString());

                    JSONArray menuitemArray = result.getJSONArray("offers");
                    vouchersDB.open();
                    vouchersDB.deleteOffers();
                    for (int i = 0; i < menuitemArray.length(); i++) {
                        String id = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("id").toString()));
                        String judul = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("judul").toString()));
                        String value = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("value").toString()));
                        String type = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("type").toString()));
                        String sub = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("sub").toString()));
                        String detail = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("detail").toString()));
                        String bg_color = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("bg_color").toString()));
                        String text_color = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("text_color").toString()));
                        String exp = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("exp").toString()));

                        VouchersModel model = new VouchersModel(judul, value, type, sub, detail, exp, "1", bg_color, text_color);
                        vouchersDB.insertVouchers(model);
                        if (isCancelled()) break;
                    }
                    vouchersDB.close();
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
            swipeRefreshLayout.setRefreshing(false);
        }

        protected void onPostExecute(String content) {
            if (error) {
                if (content.contains("invalid_key")) {
                    if (NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)) {
                        String key = new ValidationsKey().getInstance(mContext).key(true);
                        if (key.equalsIgnoreCase("null")) {
                            swipeRefreshLayout.setRefreshing(false);
                           // Toast.makeText(mContext, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                        } else {
                            requestOffers = new RequestOffers(context);
                            requestOffers.execute(key);
                        }
                    } else {
                        swipeRefreshLayout.setRefreshing(false);
//                        Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    swipeRefreshLayout.setRefreshing(false);
//                    Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_LONG).show();
                }
            } else {
                swipeRefreshLayout.setRefreshing(false);
                refreshList();
            }
        }
    }

    public  boolean isPackageInstalled(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        if (intent == null) {
            return false;
        }
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    class laporOffers extends AsyncTask<String, Void, String> {

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 3 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();

        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private JSONObject jObject;
        private Context mContext;
        private String content = null;
        private boolean error = false;
        String code;
        String themesName;
        private MessengerDatabaseHelper messengerHelper;

        public laporOffers(Context context) {
            this.mContext = context;
            pdialog.show();
        }

        @Override
        protected void onPreExecute() {
        }

        InputStreamReader reader = null;

        protected String doInBackground(String... key) {

            try {
                themesName = key[1];
                HttpClient httpClient = HttpHelper
                        .createHttpClient(mContext);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);

                if (messengerHelper == null) {
                    messengerHelper = MessengerDatabaseHelper.getInstance(mContext);
                }

                nameValuePairs.add(new BasicNameValuePair("username", messengerHelper.getMyContact().getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("key", key[0]));
                nameValuePairs.add(new BasicNameValuePair("id_offer",key[1]));

                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                HttpPost post = new HttpPost(URL_LAPOR_OFFERS);
                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                response = httpclient.execute(post);
                StatusLine statusLine = response.getStatusLine();

                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    content = out.toString();
                    jObject = new JSONObject(content);
                    code = jObject.getString("code");
                    if (code.equalsIgnoreCase("t")) {
                        JSONArray menuitemArray = jObject.getJSONArray("result");
                        for (int i = 0; i < menuitemArray.length(); i++) {
                            String a = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("name").toString()));
                            String b = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("deskripsi").toString()));
                            String c = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("alias").toString()));
                            String d = String.valueOf(Html.fromHtml(menuitemArray.getJSONObject(i).getString("url").toString()));


                            ContactBot contactBot = null;
                            if(d != null || !d.equalsIgnoreCase("")){
                                contactBot = new ContactBot(a,b,c,d,"");
                            }else {
                                contactBot = new ContactBot(a,b,c,"","");
                            }
                            botListDB.insertScrDetails(contactBot);
                        }

                    }

                } else {
                    //Closes the connection.
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
            if (error) {
                if(content.contains("invalid_key")){
                    if(NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)){
                        String key = new ValidationsKey().getInstance(mContext).key(true);
                        if (!key.equalsIgnoreCase("null")){
                            new laporOffers(mContext).execute(key, themesName);
                        }else {
                            pdialog.dismiss();
                        }
                    }
                }else{
                    pdialog.dismiss();
                }
            } else {
                pdialog.dismiss();
                Toast.makeText(mContext, "Terima kasih , tunggu pesan bc untuk pemberitahuan voucher", Toast.LENGTH_SHORT).show();
                refreshList();
            }
        }

    }
}