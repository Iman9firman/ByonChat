package com.honda.android;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.honda.android.communication.MessengerConnectionService;
import com.honda.android.communication.NetworkInternetConnectionStatus;
import com.honda.android.list.ItemListVoucherTermsAndConditions;
import com.honda.android.list.ListVoucherTermsAndConditionsAdapter;
import com.honda.android.provider.Contact;
import com.honda.android.provider.MessengerDatabaseHelper;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lukmanpryg on 7/19/2016.
 */
public class DialogVoucherTermsAndConditions extends DialogFragment {

    public final static String URL_GET_VOUCHER = "https://" + MessengerConnectionService.HTTP_SERVER + "/voucher/tnc.php";
    private MessengerDatabaseHelper messengerHelper;
    private Context context;
    private Contact contact;
    private FrameLayout mlinear_name;
    private TextView mTitle;
    private Button mClose;
    String id, judul, serial, tglvalid, nominal, bgcolor, textcolor;
    private BroadcastHandler broadcastHandler = new BroadcastHandler();
    RequestParticipantOutlets requestParticipantOutlets;
    private TextView mListTerms;
    LinearLayout linlaHeaderProgress;

    public static DialogVoucherTermsAndConditions newInstance(String id, String judul, String serial, String tglvalid, String nominal, String bgcolor, String textcolor) {
        DialogVoucherTermsAndConditions f = new DialogVoucherTermsAndConditions();
        Bundle args = new Bundle();
        args.putString("pid", id);
        args.putString("pjudul", judul);
        args.putString("pserial", serial);
        args.putString("ptglvalid", tglvalid);
        args.putString("pnominal", nominal);
        args.putString("pbgcolor", bgcolor);
        args.putString("ptextcolor", textcolor);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialog = inflater.inflate(R.layout.dialog_voucher_terms_and_conditions, container, false);
        mlinear_name = (FrameLayout) dialog.findViewById(R.id.linear_name);
        mTitle = (TextView) dialog.findViewById(R.id.name);
        mClose = (Button) dialog.findViewById(R.id.btn_cancel);
        mListTerms = (TextView) dialog.findViewById(R.id.list_html_terms);
        linlaHeaderProgress = (LinearLayout) dialog.findViewById(R.id.linlaHeaderProgress);

        String color = "";
        if(bgcolor.equalsIgnoreCase("") || bgcolor.equalsIgnoreCase("null")){
            color = "1e8cc4";
        }else{
            color = bgcolor;
        }

        String txtcolor = "";
        if(textcolor.equalsIgnoreCase("") || textcolor.equalsIgnoreCase("null")){
            txtcolor = "ffffff";
        }else{
            txtcolor = textcolor;
        }

        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(context);
        }
        if (context == null) {
            context = getActivity().getApplicationContext();
        }
        contact = messengerHelper.getMyContact();

        GradientDrawable drawable = (GradientDrawable) mlinear_name.getBackground();
        drawable.setColor(Color.parseColor("#"+color));
        mTitle.setText("Terms and Conditions");
        mTitle.setTextColor(Color.parseColor("#"+txtcolor));
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getDialog() != null) {
                    getDialog().dismiss();
                }
            }
        });

        requestParticipantOutlets = new RequestParticipantOutlets(context);
//        mlinear_name.setBackground(new ColorDrawable(getResources().getColor(R.color.color_primary_red_dark)));

        requestKey();

        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
//            dialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            dialog.getWindow().setLayout(500, 750);
//            setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MyDialog);
            setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Dialog);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getArguments().getString("pid");
        judul = getArguments().getString("pjudul");
        serial = getArguments().getString("pserial");
        tglvalid = getArguments().getString("ptglvalid");
        nominal = getArguments().getString("pnominal");
        bgcolor = getArguments().getString("pbgcolor");
        textcolor = getArguments().getString("ptextcolor");
    }

    private void requestKey() {
        RequestKeyTask testAsyncTask = new RequestKeyTask(new TaskCompleted() {
            @Override
            public void onTaskDone(String key) {
                if (key.equalsIgnoreCase("null")) {
//                    swipeRrefreshLayout.setRefreshing(false);
//                    Toast.makeText(context, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                } else {
                    requestParticipantOutlets = new RequestParticipantOutlets(context);
                    requestParticipantOutlets.execute(key);
                }
            }
        }, getActivity());

        testAsyncTask.execute();
    }

    class BroadcastHandler extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MessengerConnectionService.ACTION_REFRESH_VOUCHERS
                    .equals(intent.getAction())) {
//                setUpExpList();
            }
        }
    }

    class RequestParticipantOutlets extends AsyncTask<String, Void, String> {

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();

        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content = null;
        private boolean error = false;
        private Context mContext;

        public RequestParticipantOutlets(Context context) {
            this.mContext = context;

        }


        @Override
        protected void onPreExecute() {
//            swipeRefreshLayout.setRefreshing(true);
            linlaHeaderProgress.setVisibility(View.VISIBLE);
            mListTerms.setVisibility(View.GONE);
        }

        protected String doInBackground(String... key) {
            try {

//                Log.w("pocer", key[0]+" "+key[1]);
                HttpClient httpClient = HttpHelper
                        .createHttpClient(mContext);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);

                nameValuePairs.add(new BasicNameValuePair("bcid", contact.getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("key", key[0]));
                nameValuePairs.add(new BasicNameValuePair("sernum", "ar20000183000547"));
//                Log.w("test", contact.getJabberId() + " " + key[0] + " " + serial);

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

                    JSONObject result = new JSONObject(content);
                    String tnc = result.optString("tnc");
                    String extra = result.optString("extra");

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
//        swipeRefreshLayout.setRefreshing(false);
        }

        protected void onPostExecute(String content) {
            linlaHeaderProgress.setVisibility(View.GONE);
            if (error) {
                if (content.toString().contains("invalid_key")) {
                    if (NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)) {
                        String key = new ValidationsKey().getInstance(mContext).key(true);
                        if (key.equalsIgnoreCase("null")) {
                            Toast.makeText(mContext, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                        } else {
                            requestParticipantOutlets = new RequestParticipantOutlets(getContext());
                            requestParticipantOutlets.execute(key);
                        }
                    } else {
                        Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, content.toString(), Toast.LENGTH_LONG).show();
                }
            } else {
                try {
                    mListTerms.setVisibility(View.VISIBLE);
                    JSONObject result = new JSONObject(content);
                    String tnc = result.optString("tnc");
                    String extra = result.optString("extra");

                    mListTerms.setText(Html.fromHtml(tnc));
                } catch (JSONException E) {
                    E.printStackTrace();
                }
            }
        }
    }

}