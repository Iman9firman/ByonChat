package com.byonchat.android;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Calendar;
import java.util.List;

import static com.byonchat.android.utils.Utility.reportCatch;


public class AccountSettingActivity extends AppCompatActivity {

    private MessengerDatabaseHelper dbhelper;
    private Contact contact;
    public static final String URL_PROFILE = "https://" + MessengerConnectionService.HTTP_SERVER + "/profile/";
    private static final String TXTNAME = "Display Name";
    private static final String TXTEMAIL = "Email";
    private static final String TXTFACEBOOK = "Facebook";
    private EditText textName;
    private Spinner textGender;
    private EditText textBirth;
    private EditText textEmail;
    private EditText textFacebook;
    private EditText textCity;
    private EditText bcUser_txt;
    private TextView textCountName;
    private Switch switchBanner;
    protected ProgressDialog pdialog;
    int yearDefault, mYear, mMonth, mDay;
    static final int DATE_DIALOG_ID = 1;
    private String[] arrMonth = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    String[] gender = {"Male", "Female"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //  getSupportActionBar().setBackgroundDrawable(new Validations().getInstance(getApplicationContext()).header());
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }


    public void initView() {
        try {
            setContentView(R.layout.account_setting);
            textGender = (Spinner) findViewById(R.id.textGender);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    R.layout.spinner_gender, gender);

            textGender.setAdapter(adapter);

            bcUser_txt = (EditText) findViewById(R.id.bcUser_txt);
            bcUser_txt.setVisibility(View.GONE);
            textName = (EditText) findViewById(R.id.txt_name);
            textCountName = (TextView) findViewById(R.id.txt_count_name);
            textBirth = (EditText) findViewById(R.id.txt_birth);
            textEmail = (EditText) findViewById(R.id.txt_email);
            textCity = (EditText) findViewById(R.id.txt_city);
            textFacebook = (EditText) findViewById(R.id.txt_facebook);
            switchBanner = (Switch) findViewById(R.id.switchBanner);

            textName.addTextChangedListener(mTextEditorWatcher);


            // get the current date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            yearDefault = c.get(Calendar.YEAR) - 8;

            textBirth.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    showDialog(DATE_DIALOG_ID);
                    textBirth.setError(null);
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    return false;
                }
            });
        }catch (Exception e){
            reportCatch(e.getLocalizedMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            if (dbhelper == null) {
                dbhelper = MessengerDatabaseHelper.getInstance(this);
            }
            contact = dbhelper.getMyContact();

            if (pdialog == null) {
                pdialog = new ProgressDialog(this);
                pdialog.setIndeterminate(true);
                pdialog.setMessage("Please wait a moment ..");
                pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            }
            initView();
            if (cekLoad()) {
                if (isNetworkConnectionAvailable()) {
                    setContentView(R.layout.loading_screen);
                    if (NetworkInternetConnectionStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
                        String key = new ValidationsKey().getInstance(getApplicationContext()).key(false);
                        if (!key.equalsIgnoreCase("null")) {
                            new getProfile(getApplicationContext()).execute(key);
                        }
                    }
                }
            } else {
                setSetting();
            }
        }catch (Exception e){
            reportCatch(e.getLocalizedMessage());
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void setSetting() {
        try {
            bcUser_txt.setText(contact.getJabberId());
            textName.setText(contact.getRealname());
            textName.setSelection(textName.length());
            textCountName.setText(String.valueOf(30 - textName.length()));
            setDate(contact.getBirthdate());
            textEmail.setText(contact.getEmail());
            textFacebook.setText(contact.getFacebookid());
            textCity.setText(contact.getCity());
            if (new Validations().getInstance(getApplicationContext()).getShow(8)) {
                switchBanner.setChecked(true);
            } else {
                switchBanner.setChecked(false);
            }
            if (contact.getGender() != null) {
                if (contact.getGender().equals("Male")) {
                    textGender.setSelection(0);
                } else {
                    textGender.setSelection(1);
                }
            }
        }catch (Exception e){
            reportCatch(e.getLocalizedMessage());
        }
    }

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            textCountName.setText(String.valueOf(30 - s.length()));
        }
    };

    public boolean cekLoad() {
        boolean load = false;
        if ((contact.getRealname() == null) && (contact.getGender() == null) && (contact.getBirthdate() == null) && (contact.getEmail() == null) && (contact.getFacebookid() == null)) {
            load = true;
        }
        return load;
    }

    private boolean isNetworkConnectionAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null)
            return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_save_menu, menu);
        configureActionItem(menu);
        return true;
    }

    private void configureActionItem(Menu menu) {
        try {
            MenuItem item = menu.findItem(R.id.menu_action_save);
            Button btn = (Button) MenuItemCompat.getActionView(item).findViewById(
                    R.id.buttonAbSave);
            btn.setBackgroundColor(Color.TRANSPARENT);
            btn.setTypeface(null, Typeface.BOLD);
            btn.setText("Save");
            btn.setTextColor(Color.WHITE);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isNetworkConnectionAvailable()) {
                        textEmail.setError(null);
                        textBirth.setError(null);
                        String a = textName.getText().toString();
                        String b = textGender.getSelectedItem().toString();
                        String c = textBirth.getText().toString();
                        String d = textEmail.getText().toString();
                        String e = textFacebook.getText().toString();
                        String f = textCity.getText().toString();
                        if (cekUpdate(contact, a, b, c, d, e, f)) {
                            if (validDate(textBirth.getText().toString(), yearDefault)) {
                                if (validEmail(textEmail.getText().toString())) {
                                    pdialog.show();
                                    String key = new ValidationsKey().getInstance(getApplicationContext()).key(false);
                                    if (key.equalsIgnoreCase("null")) {
                                        //    Toast.makeText(getApplicationContext(),R.string.pleaseTryAgain,Toast.LENGTH_SHORT).show();
                                        pdialog.dismiss();
                                    } else {
                                        new updateProfile(getApplicationContext()).execute(key);
                                    }
                                } else {
                                    textEmail.setError("Email not valid");
                                }
                            } else {
                                textBirth.setError("Birth date not valid");
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                            if (switchBanner.isChecked()) {
                                new Validations().getInstance(getApplicationContext()).setShow(8);
                            } else {
                                new Validations().getInstance(getApplicationContext()).setNotShow(8);
                            }
                            finish();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }catch (Exception e){
            reportCatch(e.getLocalizedMessage());
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;
        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    protected Dialog onCreateDialog(int id) {
        return new DatePickerDialog(
                this, mDateSetListener, mYear, mMonth, mDay);

    }

    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    try {
                        mYear = year;
                        mMonth = monthOfYear;
                        mDay = dayOfMonth;
                        String sdate = LPad(mDay + "", "0", 2) + " " + arrMonth[mMonth] + " " + mYear;
                        if (year > yearDefault) {
                            textBirth.setError("Birth date not valid");
                        }
                        textBirth.setText(sdate);
                    }catch (Exception e){
                        reportCatch(e.getLocalizedMessage());
                    }
                }
            };

    private static String LPad(String schar, String spad, int len) {
        String sret = schar;
        for (int i = sret.length(); i < len; i++) {
            sret = spad + sret;
        }
        return new String(sret);
    }

    public static String getDate(String date) {
        String mDate = date;
        if (date != null && date.length() > 0) {
            String tglLahir[] = date.split(" ");
            String bulan = "1";
            String[] arr = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            int a = 1;
            for (String b : arr) {
                if (b.equalsIgnoreCase(tglLahir[1])) {
                    bulan = String.valueOf(a);
                }
                a++;
            }
            mDate = tglLahir[2] + "-" + bulan + "-" + tglLahir[0];
        }
        return mDate;
    }

    public void setDate(String date) {
        try {
            if (date != null && date.length() > 0) {
                String tglLahir[] = date.split("-");
                String sdate = date;
                if (tglLahir.length == 3) {
                    sdate = LPad(tglLahir[2] + "", "0", 2) + " " + arrMonth[Integer.valueOf(tglLahir[1]) - 1] + " " + tglLahir[0];
                }
                textBirth.setText(sdate);
            }
        }catch (Exception e){
            reportCatch(e.getLocalizedMessage());
        }
    }

    public static Boolean validDate(String date, int yearDefault) {
        boolean valid = true;
        if (date != null && date.length() > 0) {
            String tglLahir[] = date.split(" ");
            if (Integer.valueOf(tglLahir[2]) > yearDefault) {
                valid = false;
            }
        } else {
            valid = false;
        }
        return valid;
    }

    public static Boolean cekUpdate(Contact contact, String name, String gender, String birth, String email, String facebookId, String city) {
        boolean valid = false;
        if (!name.equals(contact.getRealname())) valid = true;
        if (!gender.equals(contact.getGender())) valid = true;
        if (!birth.equals(contact.getBirthdate())) valid = true;
        if (!email.equals(contact.getEmail())) valid = true;
        if (!facebookId.equals(contact.getFacebookid())) valid = true;
        if (!city.equals(contact.getCity())) valid = true;
        return valid;
    }

    public static Boolean validEmail(String email) {
        boolean valid = true;
        if (email != null && email.length() > 0) {
            valid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
        return valid;
    }

    class updateProfile extends AsyncTask<String, Void, String> {

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();

        final HttpParams params = httpclient.getParams();
        HttpResponse response;
        private JSONObject jObject;
        private Context mContext;
        private String content = null;
        private boolean error = false;
        String gender;
        String email;
        String facebookId;
        String city;
        String realname;
        String tanggal;
        String code2;
        String desc;

        public updateProfile(Context context) {
            this.mContext = context;

        }

        @Override
        protected void onPreExecute() {
        }

        InputStreamReader reader = null;

        protected String doInBackground(String... key) {

            try {

                HttpClient httpClient = HttpHelper
                        .createHttpClient(getApplicationContext());
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);

                realname = textName.getText().toString();
                gender = textGender.getSelectedItem().toString();
                tanggal = textBirth.getText().toString();
                email = textEmail.getText().toString();
                facebookId = textFacebook.getText().toString();
                city = textCity.getText().toString();

                nameValuePairs.add(new BasicNameValuePair("username", contact.getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("key", key[0]));
                nameValuePairs.add(new BasicNameValuePair("action", "create"));


                nameValuePairs.add(new BasicNameValuePair("tgl_lahir", getDate(tanggal)));
                nameValuePairs.add(new BasicNameValuePair("email", email));
                nameValuePairs.add(new BasicNameValuePair("realname", realname));
                nameValuePairs.add(new BasicNameValuePair("facebook_id", facebookId));
                nameValuePairs.add(new BasicNameValuePair("gender", gender));
                nameValuePairs.add(new BasicNameValuePair("kota", city));

                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                HttpPost post = new HttpPost(URL_PROFILE);
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
                    jObject = new JSONObject(content);

                    code2 = jObject.getString("code");
                    desc = jObject.getString("description");
                    if (!code2.equalsIgnoreCase("200")) error = true;
                } else {
                    content = statusLine.getReasonPhrase();
                    response.getEntity().getContent().close();
                    throw new IOException(content);
                }

            } catch (ClientProtocolException e) {
                reportCatch(e.getLocalizedMessage());
                content = e.getMessage();
                error = true;
            } catch (IOException e) {
                reportCatch(e.getLocalizedMessage());
                content = e.getMessage();
                error = true;
            } catch (Exception e) {
                reportCatch(e.getLocalizedMessage());
                error = true;
            }

            return content;
        }

        protected void onCancelled() {
            pdialog.dismiss();
        }

        protected void onPostExecute(String content) {
            try {
                pdialog.dismiss();
                if (error) {
                    if (content.contains("invalid_key")) {
                        if (NetworkInternetConnectionStatus.getInstance(mContext).isOnline(mContext)) {
                            pdialog.show();
                            String key = new ValidationsKey().getInstance(mContext).key(true);
                            if (key.equalsIgnoreCase("null")) {
                                // Toast.makeText(getApplicationContext(),R.string.pleaseTryAgain,Toast.LENGTH_SHORT).show();
                                pdialog.dismiss();
                            } else {
                                new updateProfile(mContext).execute(key);
                            }
                        } else {
                            Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mContext, R.string.pleaseTryAgain, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (code2.equalsIgnoreCase("200")) {
                        Contact c = dbhelper.getMyContact();
                        c.setRealname(realname);
                        c.setGender(gender);
                        c.setBirthdate(textBirth.getText().toString());
                        c.setEmail(email);
                        c.setFacebookid(facebookId);
                        c.setCity(city);
                        dbhelper.updateData(c);
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                        if (switchBanner.isChecked()) {
                            new Validations().getInstance(mContext).setShow(8);
                        } else {
                            new Validations().getInstance(mContext).setNotShow(8);
                        }
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), desc, Toast.LENGTH_SHORT).show();
                    }
                }
            }catch (Exception e){
                reportCatch(e.getLocalizedMessage());
            }
        }

    }

    class getProfile extends AsyncTask<String, Void, String> {

        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;
        private final HttpClient httpclient = new DefaultHttpClient();

        final HttpParams params = httpclient.getParams();
        HttpResponse response;
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
                Contact contact = dbhelper.getMyContact();
                HttpClient httpClient = HttpHelper
                        .createHttpClient(getApplicationContext());
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);

                nameValuePairs.add(new BasicNameValuePair("username", contact.getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("key", key[0]));

                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpClient.getParams(), WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(httpClient.getParams(), WAIT_TIMEOUT);

                HttpPost post = new HttpPost(URL_PROFILE);
                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));


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

                            Contact c = dbhelper.getMyContact();
                            c.setRealname(realname);
                            c.setGender(gender);
                            c.setBirthdate(birth);
                            c.setEmail(email);
                            c.setFacebookid(facebookId);
                            c.setCity(city);
                            dbhelper.updateData(c);
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
                reportCatch(e.getLocalizedMessage());
                desc = e.getMessage();
                content = e.getMessage();
                error = true;
            } catch (IOException e) {
                reportCatch(e.getLocalizedMessage());
                desc = e.getMessage();
                content = e.getMessage();
                error = true;
            } catch (Exception e) {
                reportCatch(e.getLocalizedMessage());
                error = true;
            }

            return content;
        }

        protected void onCancelled() {
        }

        protected void onPostExecute(String content) {
            try {
                initView();
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
            }catch (Exception e){
                reportCatch(e.getLocalizedMessage());
            }
        }

    }
}

