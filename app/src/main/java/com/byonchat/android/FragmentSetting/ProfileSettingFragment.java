package com.byonchat.android.FragmentSetting;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.byonchat.android.AccountSettingActivity;
import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.utils.HttpHelper;
import com.byonchat.android.utils.Validations;
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
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ProfileSettingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    public static final String URL_PROFILE = "https://" + MessengerConnectionService.HTTP_SERVER + "/profile/";
    Contact contact;
    private Context context;
    private MessengerDatabaseHelper messengerHelper;
    private FloatingActionButton btnEdit;
    private EditText textName;
    private EditText textGender;
    private EditText textBirth;
    private EditText textEmail;
    private EditText textFacebook;
    private EditText textCity;
    private Switch switchBanner;
    protected ProgressDialog pdialog;
    private RelativeLayout layoutLoading;
    private LinearLayout layoutProfile;
    public ProfileSettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_setting, container, false);

        if (context == null) {
            context = getActivity().getApplicationContext();
        }

        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(context);
        }

        contact = messengerHelper.getMyContact();

        initView(view);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AccountSettingActivity.class);
//                intent.putExtra(KEY_REGISTER_MSISDN, messengerHelper.getMyContact().getJabberId());
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(context);
        }
        contact = messengerHelper.getMyContact();

        if (pdialog == null) {
            pdialog = new ProgressDialog(context);
            pdialog.setIndeterminate(true);
            pdialog.setMessage("Please wait a moment ..");
            pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        initView(getView());
        if (cekLoad()) {
            if (isNetworkConnectionAvailable()) {
                btnEdit.setVisibility(View.GONE);
                layoutProfile.setVisibility(View.GONE);
                layoutLoading.setVisibility(View.VISIBLE);
                if (NetworkInternetConnectionStatus.getInstance(context.getApplicationContext()).isOnline(context.getApplicationContext())) {
                    String key = new ValidationsKey().getInstance(context.getApplicationContext()).key(false);
                    if (!key.equalsIgnoreCase("null")) {
                        new getProfile(context.getApplicationContext()).execute(key);
                    }
                }
            }
        } else {
            setSetting();
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void setSetting() {
        textName.setText(contact.getRealname());
        textName.setSelection(textName.length());
        textBirth.setText(contact.getBirthdate());
        textEmail.setText(contact.getEmail());
        textFacebook.setText(contact.getFacebookid());
        textCity.setText(contact.getCity());
        if (new Validations().getInstance(context.getApplicationContext()).getShow(8)) {
            switchBanner.setChecked(true);
        } else {
            switchBanner.setChecked(false);
        }
        if (contact.getGender() != null) {
            if (contact.getGender().equals("Male")) {
                textGender.setText("Male");
            } else {
                textGender.setText("Female");
            }
        }
    }

    public void initView(View view) {
        layoutProfile = (LinearLayout) view.findViewById(R.id.profileLayout);
        layoutLoading = (RelativeLayout) view.findViewById(R.id.loadingLayout);
        btnEdit = (FloatingActionButton) view.findViewById(R.id.btn_edit);
        textName = (EditText) view.findViewById(R.id.txt_name);
        textBirth = (EditText) view.findViewById(R.id.txt_birth);
        textEmail = (EditText) view.findViewById(R.id.txt_email);
        textCity = (EditText) view.findViewById(R.id.txt_city);
        textFacebook = (EditText) view.findViewById(R.id.txt_facebook);
        switchBanner = (Switch) view.findViewById(R.id.switchBanner);
        textGender = (EditText) view.findViewById(R.id.textGender);

        btnEdit.setVisibility(View.VISIBLE);
        layoutProfile.setVisibility(View.VISIBLE);
        layoutLoading.setVisibility(View.GONE);
    }

    public boolean cekLoad() {
        boolean load = false;
        if ((contact.getRealname() == null) && (contact.getGender() == null) && (contact.getBirthdate() == null) && (contact.getEmail() == null) && (contact.getFacebookid() == null)) {
            load = true;
        }
        return load;
    }

    private boolean isNetworkConnectionAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null)
            return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }


    class getProfile extends AsyncTask<String, Void, String> {

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;

        private JSONObject jObject;
        private Context mContext;
        private String content = null;
        private boolean error = false;
        String code2 = "";
        String desc = "";

        public getProfile(Context context) {
            this.mContext = context;

        }

        @Override
        protected void onPreExecute() {
        }

        InputStreamReader reader = null;

        protected String doInBackground(String... key) {

            try {
                Contact contact = messengerHelper.getMyContact();
                HttpClient httpClient = HttpHelper
                        .createHttpClient(getActivity().getApplicationContext());
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);

                nameValuePairs.add(new BasicNameValuePair("username", contact.getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("key", key[0]));

                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                HttpPost post = new HttpPost(URL_PROFILE);
                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpClient httpclient = HttpHelper.createHttpClient();
                HttpParams params = httpclient.getParams();
                HttpResponse response;

                //Response from the Http Request
                response = httpclient.execute(post);
                StatusLine statusLine = response.getStatusLine();

                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    content = out.toString();
                    jObject = new JSONObject(content);

                    if (content.contains("\"code\":404")) {
                        code2 = jObject.getString("code");
                        desc = jObject.getString("description");
                        error = true;
                    } else {
                        if (content.contains("\"username\":")) {
                            String realname = "";
                            String gender = "";
                            String email = "";
                            String facebookId = "";
                            String birth = "";
                            String city = "";

                            realname = jObject.getString("realname").toString();
                            gender = jObject.getString("gender").toString();
                            email = jObject.getString("email").toString();
                            facebookId = jObject.getString("facebook_id").toString();
                            birth = jObject.getString("tgl_lahir").toString();
                            city = jObject.getString("kota").toString();

                            Contact c = messengerHelper.getMyContact();
                            c.setRealname(realname);
                            c.setGender(gender);
                            c.setBirthdate(birth);
                            c.setEmail(email);
                            c.setFacebookid(facebookId);
                            c.setCity(city);
                            messengerHelper.updateData(c);
                        }
                    }

                } else {
                    //Closes the connection.
                    error = true;
                    desc = statusLine.getReasonPhrase();
                    content = statusLine.getReasonPhrase();
                    response.getEntity().getContent().close();
                    throw new IOException(content);
                }

            } catch (ClientProtocolException e) {
                desc = e.getMessage();
                content = e.getMessage();
                error = true;
            } catch (IOException e) {
                desc = e.getMessage();
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
            initView(getView());
            if (error) {
                if (desc.equalsIgnoreCase("Invalid Login Key")) {
                    if (NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)) {
                        String key = new ValidationsKey().getInstance(mContext).key(true);
                        if (!key.equalsIgnoreCase("null")) {
                            new getProfile(mContext).execute(key);
                        }
                    }
                } else {
                    Toast.makeText(mContext, desc, Toast.LENGTH_SHORT).show();
                }
            } else {
                setSetting();
            }
        }

    }

}
