package com.honda.android;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.honda.android.communication.MessengerConnectionService;
import com.honda.android.communication.NetworkInternetConnectionStatus;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Lukmanpryg on 7/19/2016.
 */
public class DialogUseVoucherTwo extends DialogFragment {

    public final static String URL_TRANSFER_VOUCHER = "https://" + MessengerConnectionService.HTTP_SERVER + "/voucher/transfer.php";
    FrameLayout mlinear_name;
    TextView mSerialNumber, mSerialNumber2, mTanggalValid, mTanggalValid2, mAmount, mAmount2, mOutlet_id, mJudul;
    private Button mParticipantOutlets, mProceed, mCancel;
    String id, judul, serial, tglvalid, nominal, outletID, bgcolor, textcolor,background;
    private ImageView mBackground;
    private Contact contact;
    protected ProgressDialog pdialog;
    RequestTransferVoucher requestTransferVoucher;
    MessengerDatabaseHelper messengerHelper;
    private Context context;
    private Activity activity;

    public static DialogUseVoucherTwo newInstance(String id, String judul, String serial, String tglvalid, String nominal, String outletID, String bgcolor, String textcolor, String background) {
        DialogUseVoucherTwo f = new DialogUseVoucherTwo();
        Bundle args = new Bundle();
        args.putString("pid", id);
        args.putString("pjudul", judul);
        args.putString("pserial", serial);
        args.putString("ptglvalid", tglvalid);
        args.putString("pnominal", nominal);
        args.putString("poutletID", outletID);
        args.putString("pbgcolor", bgcolor);
        args.putString("ptextcolor", textcolor);
        args.putString("pbackground", background);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialog = inflater.inflate(R.layout.dialog_voucher_use_2, container, false);
        mlinear_name = (FrameLayout) dialog.findViewById(R.id.linear_name);
        mJudul = (TextView) dialog.findViewById(R.id.name) ;
        mBackground = (ImageView) dialog.findViewById(R.id.background);
        mSerialNumber = (TextView) dialog.findViewById(R.id.serial_number);
        mSerialNumber2 = (TextView) dialog.findViewById(R.id.serial_number2);
        mTanggalValid = (TextView) dialog.findViewById(R.id.tanggal_valid);
        mTanggalValid2 = (TextView) dialog.findViewById(R.id.tanggal_valid2);
        mAmount = (TextView) dialog.findViewById(R.id.amount);
        mAmount2 = (TextView) dialog.findViewById(R.id.amount2);
        mParticipantOutlets = (Button) dialog.findViewById(R.id.participant_outlets);
        mOutlet_id = (TextView) dialog.findViewById(R.id.outlet_id);
        mProceed = (Button) dialog.findViewById(R.id.btn_proceed);
        mCancel = (Button) dialog.findViewById(R.id.btn_cancel);

        Glide.with(getContext()).load(background).into(mBackground);
        String color = "";
        if(bgcolor.equalsIgnoreCase("") || bgcolor.equalsIgnoreCase("null")){
            color = "1e8cc4";
        }else{
            color = bgcolor;
        }

        GradientDrawable drawable = (GradientDrawable) mlinear_name.getBackground();
        drawable.setColor(Color.parseColor("#"+color));
        String txtcolor = "";
        if(textcolor.equalsIgnoreCase("") || textcolor.equalsIgnoreCase("null")){
            txtcolor = "ffffff";
        }else{
            txtcolor = textcolor;
        }

        mJudul.setText(judul);
        mJudul.setTextColor(Color.parseColor("#"+txtcolor));
        mSerialNumber.setText(serial);
        mSerialNumber2.setText(serial);
        mTanggalValid.setText("Valid until "+tglvalid);
        mTanggalValid2.setText(tglvalid);
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("in", "ID"));
        mAmount2.setText("Rp " + String.valueOf(nf.format(Double.parseDouble(nominal))));
        mAmount.setText("Rp " + String.valueOf(nf.format(Double.parseDouble(nominal))));
        mOutlet_id.setText(outletID);

        if (activity == null) {
            activity = getActivity();
        }

        if (context == null) {
            context = getContext();
        }

        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(activity);
        }

        contact = messengerHelper.getMyContact();
        requestTransferVoucher = new RequestTransferVoucher(context);

        mParticipantOutlets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getDialog() != null){
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Fragment prev = getFragmentManager().findFragmentByTag("DialogUseVoucher1");
                    if(prev != null){
                        prev.isRemoving();
                    }
                    ft.addToBackStack(null);

                    DialogFragment newFragment = DialogVoucherParticipantOutlets.newInstance(id,judul,serial,tglvalid,nominal,bgcolor,textcolor);
                    newFragment.show(ft, "DialogVoucherParticipantOutlets");
                }
            }
        });

        mProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestKey();
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getDialog() != null) {
                    getDialog().dismiss();
                }
            }
        });
//        mlinear_name.setBackgroundColor(getResources().getColor(R.color.color_primary_red_dark));

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
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
        outletID = getArguments().getString("poutletID");
        bgcolor = getArguments().getString("pbgcolor");
        textcolor = getArguments().getString("ptextcolor");
        background = getArguments().getString("pbackground");
    }

    private void requestKey() {
        RequestKeyTask testAsyncTask = new RequestKeyTask(new TaskCompleted() {
            @Override
            public void onTaskDone(String key) {
                if (key.equalsIgnoreCase("null")) {
                    Toast.makeText(activity, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                } else {
                    requestTransferVoucher = new RequestTransferVoucher(activity);
                    requestTransferVoucher.execute(key);
                }
            }
        }, activity);

        testAsyncTask.execute();
    }

    class RequestTransferVoucher extends AsyncTask<String, Void, String> {

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();

        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private String content = null;
        private boolean error = false;
        private Context mContext;
        ProgressDialog progressDialog;

        public RequestTransferVoucher(Context context) {
            this.mContext = context;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setCancelable(true);
            progressDialog.setMessage("Please wait...");
            progressDialog.setProgressStyle(android.R.attr.progressBarStyleSmall);
            progressDialog.setProgress(0);
            progressDialog.show();
        }

        protected String doInBackground(String... key) {
            try {

                HttpClient httpClient = HttpHelper
                        .createHttpClient(mContext);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);

                nameValuePairs.add(new BasicNameValuePair("bcid", contact.getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("key", key[0]));
                nameValuePairs.add(new BasicNameValuePair("serial", serial));
                nameValuePairs.add(new BasicNameValuePair("dari", messengerHelper.getMyContact().getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("ke", outletID));

                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                HttpPost post = new HttpPost(URL_TRANSFER_VOUCHER);
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
//            swipeRefreshLayout.setRefreshing(false);
        }

        protected void onPostExecute(String content) {
            progressDialog.dismiss();
            if (error) {
                if(content.contains("invalid_key")){
                    if(NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)){
                        String key = new ValidationsKey().getInstance(mContext).key(true);
                        if (key.equalsIgnoreCase("null")){
//                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(mContext, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                        }else{
                            requestTransferVoucher = new RequestTransferVoucher(context);
                            requestTransferVoucher.execute(key);
                        }
                    }else{
//                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    }
                }else{
//                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(mContext,  R.string.pleaseTryAgain, Toast.LENGTH_LONG).show();
                }
            } else {
                if (getDialog() != null) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Fragment prev = getFragmentManager().findFragmentByTag("DialogUseVoucher2");
                    if (prev != null) {
                        DialogFragment df = (DialogFragment) prev;
                        df.dismiss();
                    }
                    ft.addToBackStack(null);

                    DialogFragment newFragment = DialogUseVoucherThree.newInstance(id, judul, serial, tglvalid, nominal, outletID,bgcolor,textcolor,background);
                    newFragment.show(ft, "DialogUseVoucher3");
                }
            }
        }
    }
}
