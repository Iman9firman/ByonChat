package com.byonchat.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.utils.HttpHelper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

@SuppressLint("ValidFragment")
public class DialogNewDropdown extends DialogFragment {

    private Button mProceed, mCancel;
    private LinearLayout lParent;
    private finishListener listener;
    ProgressBar loading;
    String bcUser, jjt;

    public DialogNewDropdown(finishListener listener, String bcUser, String jjt, int type) {
        super();
        this.listener = listener;
        this.bcUser = bcUser;
        this.jjt = jjt;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialog = inflater.inflate(R.layout.dialog_form_child_layout, container, false);
        try {
            loading = (ProgressBar) dialog.findViewById(R.id.progressBarDone);
            mProceed = (Button) dialog.findViewById(R.id.btn_proceed);
            mCancel = (Button) dialog.findViewById(R.id.btn_cancel);

            loading.setVisibility(View.VISIBLE);

            mProceed.setVisibility(View.GONE);
            mCancel.setVisibility(View.GONE);


        } catch (Exception e) {
        }
        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            //if("")ada data  listener.submitted(data);
            //                            dismiss();
            new getCars(getActivity()).execute("https://iss.byonchat.com/list_api_iss/list_pembobotan_jjt.php", jjt, bcUser);

        } catch (Exception e) {
        }
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
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
    }


    private class getCars extends AsyncTask<String, String, String> {
        String error = "";
        private Activity activity;
        private Context context;

        public getCars(Activity activity) {
            this.activity = activity;
            context = activity;
        }

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            postData(params[0], params[1], params[2]);
            return null;
        }

        protected void onPostExecute(String result) {

            if (error.length() > 0) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }

            dismiss();

        }

        protected void onProgressUpdate(String... string) {
        }

        public void postData(String valueIWantToSend, String kode_jjt, String bc_user) {
            // Create a new HttpClient and Post Header

            try {
                HttpClient httpclient = HttpHelper.createHttpClient();
                HttpPost httppost = new HttpPost(valueIWantToSend);

                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("kode_jjt", kode_jjt));
                nameValuePairs.add(new BasicNameValuePair("bc_user", bc_user));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {

                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);

                    byte[] decodedBytes = Base64.decode(data, 0);
                    String rr = decompress(decodedBytes);

                    listener.submitted(rr,"80");
                    dismiss();

                } else {
                    error = "Tolong periksa koneksi internet2.";
                }

            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                error = "Tolong periksa koneksi internet3.";
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                error = "Tolong periksa koneksi internet4.";
            } catch (IOException e) {
                // TODO Auto-generated catch block
                error = "Tolong periksa koneksi internet5.";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    public interface finishListener {
        public void submitted(String json, String passGrade);
    }


    public static String decompress(byte[] compressed) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
        GZIPInputStream gis = new GZIPInputStream(bis);
        BufferedReader br = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        gis.close();
        bis.close();
        return sb.toString();
    }

}