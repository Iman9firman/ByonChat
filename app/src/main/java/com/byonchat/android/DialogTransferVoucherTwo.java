package com.byonchat.android;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.MessengerDatabaseHelper;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.byonchat.android.utils.Utility.reportCatch;

/**
 * Created by Lukmanpryg on 7/19/2016.
 */
public class DialogTransferVoucherTwo extends DialogFragment {

    public final static String URL_TRANSFER_VOUCHER = "https://" + MessengerConnectionService.HTTP_SERVER + "/voucher/transfer.php";
    private FrameLayout mlinear_name;
    private TextView mJudul, mSerialNumber, mSerialNumber2, mTanggalValid, mTanggalValid2, mAmount, mAmount2, mByonchatId;
    private Button mProceed, mCancel;
    private String id, judul, serial, tglvalid, nominal, bcid, bcuser, bgcolor, textcolor, iduser,background;
    private ImageView mBackground;
    private Contact contact;
    protected ProgressDialog pdialog;
    RequestTransferVoucher requestTransferVoucher;
    MessengerDatabaseHelper messengerHelper;
    private Context context;
    private Activity activity;

    public static DialogTransferVoucherTwo newInstance(String id, String judul, String serial, String tglvalid, String nominal, String bcid, String bcuser, String bgcolor, String textcolor, String background) {
        DialogTransferVoucherTwo f = new DialogTransferVoucherTwo();
        Bundle args = new Bundle();
        args.putString("pid", id);
        args.putString("pjudul", judul);
        args.putString("pserial", serial);
        args.putString("ptglvalid", tglvalid);
        args.putString("pnominal", nominal);
        args.putString("pbcid", bcid);
        args.putString("pbcuser", bcuser);
        args.putString("pbgcolor", bgcolor);
        args.putString("ptextcolor", textcolor);
        args.putString("pbackground", background);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialog = inflater.inflate(R.layout.dialog_voucher_transfer_2, container, false);
        try {
            mlinear_name = (FrameLayout) dialog.findViewById(R.id.linear_name);
            mJudul = (TextView) dialog.findViewById(R.id.name);
            mBackground = (ImageView) dialog.findViewById(R.id.background);
            mSerialNumber = (TextView) dialog.findViewById(R.id.serial_number);
            mSerialNumber2 = (TextView) dialog.findViewById(R.id.serial_number2);
            mTanggalValid = (TextView) dialog.findViewById(R.id.tanggal_valid);
            mTanggalValid2 = (TextView) dialog.findViewById(R.id.tanggal_valid2);
            mAmount = (TextView) dialog.findViewById(R.id.amount);
            mAmount2 = (TextView) dialog.findViewById(R.id.amount2);
            mByonchatId = (TextView) dialog.findViewById(R.id.byonchat_id);
            mProceed = (Button) dialog.findViewById(R.id.btn_proceed);
            mCancel = (Button) dialog.findViewById(R.id.btn_cancel);

            Glide.with(getContext()).load(background).into(mBackground);
            String color = "";
            if (bgcolor.equalsIgnoreCase("") || bgcolor.equalsIgnoreCase("null")) {
                color = "1e8cc4";
            } else {
                color = bgcolor;
            }

            GradientDrawable drawable = (GradientDrawable) mlinear_name.getBackground();
            drawable.setColor(Color.parseColor("#" + color));

            String txtcolor = "";
            if (textcolor.equalsIgnoreCase("") || textcolor.equalsIgnoreCase("null")) {
                txtcolor = "ffffff";
            } else {
                txtcolor = textcolor;
            }

            mJudul.setText(judul);
            mJudul.setTextColor(Color.parseColor("#" + txtcolor));
            mSerialNumber.setText(serial);
            mSerialNumber2.setText(serial);
            mTanggalValid.setText("Valid until " + tglvalid);
            mTanggalValid2.setText(tglvalid);
            NumberFormat nf = NumberFormat.getNumberInstance(new Locale("in", "ID"));
            mAmount2.setText("Rp " + String.valueOf(nf.format(Double.parseDouble(nominal))));
            mAmount.setText("Rp " + String.valueOf(nf.format(Double.parseDouble(nominal))));

            if (bcuser.equalsIgnoreCase("")) {
                mByonchatId.setText(bcid);
                iduser = bcid;
            } else {
                mByonchatId.setText(bcuser);
                iduser = bcuser;
            }

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
//        mlinear_name.setBackground(new ColorDrawable(getResources().getColor(R.color.color_primary_red_dark)));
        }catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
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
        try {
            Dialog dialog = getDialog();
            if (dialog != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Dialog);
            }
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
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
        bcid = getArguments().getString("pbcid");
        bcuser = getArguments().getString("pbcuser");
        bgcolor = getArguments().getString("pbgcolor");
        textcolor = getArguments().getString("ptextcolor");
        background = getArguments().getString("pbackground");
    }

    private void requestKey() {
        try {
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
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
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
                nameValuePairs.add(new BasicNameValuePair("ke", bcid));

                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                HttpPost post = new HttpPost(URL_TRANSFER_VOUCHER);
                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                response = httpclient.execute(post);
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    content = out.toString();

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
        }

        protected void onPostExecute(String content) {
            try {
                progressDialog.dismiss();
                if (error) {
                    if (content.contains("invalid_key")) {
                        if (NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)) {
                            String key = new ValidationsKey().getInstance(mContext).key(true);
                            if (key.equalsIgnoreCase("null")) {
                                Toast.makeText(mContext, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                            } else {
                                requestTransferVoucher = new RequestTransferVoucher(context);
                                requestTransferVoucher.execute(key);
                            }
                        } else {
                            Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mContext, R.string.pleaseTryAgain, Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (getDialog() != null) {
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        Fragment prev = getFragmentManager().findFragmentByTag("DialogTransferVoucher2");
                        if (prev != null) {
                            DialogFragment df = (DialogFragment) prev;
                            df.dismiss();
                        }
                        ft.addToBackStack(null);

                        DialogFragment newFragment = DialogTransferVoucherThree.newInstance(id, judul, serial, tglvalid, nominal, iduser, bgcolor, textcolor, background);
                        newFragment.show(ft, "DialogTransferVoucher3");
                    }
                }
            } catch (Exception e) {
                reportCatch(e.getLocalizedMessage());
            }
        }
    }
}