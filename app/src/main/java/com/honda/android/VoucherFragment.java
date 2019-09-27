package com.honda.android;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.honda.android.communication.MessengerConnectionService;
import com.honda.android.communication.NetworkInternetConnectionStatus;
import com.honda.android.list.ListVoucherAdapter;
import com.honda.android.provider.Contact;
import com.honda.android.provider.MessengerDatabaseHelper;
import com.honda.android.provider.VouchersDB;
import com.honda.android.provider.VouchersModel;
import com.honda.android.utils.HttpHelper;
import com.honda.android.utils.RequestKeyTask;
import com.honda.android.utils.TaskCompleted;
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
import java.util.ArrayList;
import java.util.List;


@SuppressLint("ValidFragment")
public class VoucherFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public final static String URL_GET_VOUCHER = "https://" + MessengerConnectionService.HTTP_SERVER + "/voucher/index.php";
    private MessengerDatabaseHelper messengerHelper;
    private Contact contact;
    private Context context;
    ListVoucherAdapter adapter;
    private ListView lv;
    //Lukman+
    private ArrayList<VouchersModel> listItem;
    //Lukman-
    private SwipeRefreshLayout swipeRefreshLayout;
    VouchersDB vouchersDB;
    View rootView;
    protected ProgressDialog pdialog;
    private BroadcastHandler broadcastHandler = new BroadcastHandler();
    RequestVouchers requestVouchers;

    public VoucherFragment(Context ctx) {
        context=ctx;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_voucer, container, false);

        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(context);
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

        requestVouchers = new RequestVouchers(context);
        if (pdialog == null) {
            pdialog = new ProgressDialog(getActivity());
            pdialog.setIndeterminate(true);
            pdialog.setMessage("Please wait a moment");
            pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        adapter = new ListVoucherAdapter(getActivity(), listItem);
        contact = messengerHelper.getMyContact();

        vouchersDB.open();
        listItem = vouchersDB.retriveVouchers();
        vouchersDB.close();

        if (listItem.size() > 0) {
            refreshList();
        }else{
            requestKey();
        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Intent intent = new Intent(context, ActivityVouchersDetail.class);
                intent.putExtra("id", String.valueOf(position));
                intent.putExtra("judul", listItem.get(position).getJudul());
                intent.putExtra("bgcolor", listItem.get(position).getColor());
                intent.putExtra("textcolor", listItem.get(position).getTextcolor());
                intent.putExtra("icon", listItem.get(position).getIcon());
                startActivity(intent);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(this);

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
        getActivity().registerReceiver(broadcastHandler, f);
        refreshList();
        super.onResume();
    }

    @Override
    public void onPause() {
        requestVouchers.cancel(true);
        getActivity().unregisterReceiver(broadcastHandler);
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
                          //  Toast.makeText(context, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                        } else {
                            requestVouchers = new RequestVouchers(context);
                            requestVouchers.execute(key);
                        }
                    }
                }, getActivity());

                testAsyncTask.execute();
            }
        });
    }

    public void refreshList() {
        new Handler(Looper.getMainLooper()).post(new Runnable() { // Tried new Handler(Looper.myLopper()) also
            @Override
            public void run() {
                vouchersDB.open();
                listItem = vouchersDB.retriveVouchers();
                vouchersDB.close();
                adapter = new ListVoucherAdapter(context, listItem);
                lv.setAdapter(adapter);
            }
        });
    }

    class BroadcastHandler extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MessengerConnectionService.ACTION_REFRESH_VOUCHERS
                    .equals(intent.getAction())) {
                refreshList();
            }
        }
    }

    class RequestVouchers extends AsyncTask<String, Void, String> {

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();

        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content = null;
        private boolean error = false;
        private Context mContext;

        public RequestVouchers(Context context) {
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

                nameValuePairs.add(new BasicNameValuePair("bcid", contact.getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("key", key[0]));
                nameValuePairs.add(new BasicNameValuePair("mulai", "0"));

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
                    JSONArray result = new JSONArray(content);
                    vouchersDB.open();
                    vouchersDB.deleteVouchers();
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject obj = result.getJSONObject(i);
                        String nama = obj.getString("nama");
                        String jumlah = obj.getString("jumlah");
                        String icon = obj.getString("icon");
                        String bg_color = obj.getString("bg_color");

                        String text_color = obj.getString("text_color");
                        VouchersModel model = new VouchersModel(String.valueOf(i), nama, jumlah, "", "", "", icon, "2", bg_color, text_color);
                        vouchersDB.insertVouchers(model);
                        if (isCancelled()) break;
                    }
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
                         //   Toast.makeText(mContext, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                        } else {
                            requestVouchers = new RequestVouchers(context);
                            requestVouchers.execute(key);
                        }
                    } else {
                        swipeRefreshLayout.setRefreshing(false);
//                        Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                   // Toast.makeText(mContext,  R.string.pleaseTryAgain, Toast.LENGTH_LONG).show();
                }
            } else {
                swipeRefreshLayout.setRefreshing(false);
                refreshList();
            }
        }
    }
}