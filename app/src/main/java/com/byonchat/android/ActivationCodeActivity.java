package com.byonchat.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.appcompat.app.AppCompatActivity;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.createMeme.FilteringImage;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.Interval;
import com.byonchat.android.provider.IntervalDB;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.utils.DialogUtil;
import com.byonchat.android.utils.HttpHelper;
import com.byonchat.android.utils.Utility;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.byonchat.android.utils.Utility.reportCatch;

public class ActivationCodeActivity extends AppCompatActivity {
    private static final String ACTIVATION_URL = "https://"
            + MessengerConnectionService.UTIL_SERVER + "/v1/codes/verify";
    private static final String BUNDLE_KEY_DIALOG_SHOWN = "DIALOG_SHOWN";
    private static final String REGISTRATION_URL = "https://"
            + MessengerConnectionService.UTIL_SERVER + "/v1/codes";
    private EditText textActivationCode;
    private TextView textTimer;
    private TextView textResend;
    private TextView textTimeRemaning;
    private Button buttonOK;
    private Button buttonResend;
    private String number;
    private String code;
    private ProgressDialog pdialog;
    private MessengerDatabaseHelper dbHelper;
    private boolean dialogShown = false;
    BroadcastReceiver receiver = new SmsListener();

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BUNDLE_KEY_DIALOG_SHOWN, dialogShown);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        dialogShown = savedInstanceState.getBoolean(BUNDLE_KEY_DIALOG_SHOWN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        try {
            FilteringImage.headerColor(getWindow(), ActivationCodeActivity.this, getResources().getColor(R.color.colorPrimary));
            if (!isNetworkConnectionAvailable()) {
                setContentView(R.layout.custom_information);
                ((TextView) findViewById(R.id.customInformationText))
                        .setText(R.string.registration_no_internet);
            } else {
                setContentView(R.layout.registration_activation_code);
                textTimer = (TextView) findViewById(R.id.textTimer);
                textTimeRemaning = (TextView) findViewById(R.id.textTimeRemaning);
                buttonResend = (Button) findViewById(R.id.activationButtonResend);
                textResend = (TextView) findViewById(R.id.textResend);

                number = getIntent().getStringExtra(
                        RegistrationActivity.KEY_REGISTER_MSISDN);

                IntervalDB db = new IntervalDB(getApplicationContext());
                db.open();
                Cursor cursor = db.getSingleContact(12);
                if (cursor.getCount() > 0) {
                    number = cursor.getString(cursor.getColumnIndexOrThrow(IntervalDB.COL_TIME));
                }
                cursor.close();
                db.close();
                setTimer();
                EditText textPnumber = (EditText) findViewById(R.id.activationPhoneNumber);
                textPnumber.setText("+" + Utility.formatPhoneNumber(number));
                ImageButton buttonEditNumber = (ImageButton) findViewById(R.id.activationEditBtn);
                buttonEditNumber.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        doStartActivity(RegistrationActivity.class);
                    }
                });

                buttonResend.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        new RegistrationRequest().execute();
                    }
                });

                buttonEditNumber.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        doStartActivity(RegistrationActivity.class);
                    }
                });

                textActivationCode = (EditText) findViewById(R.id.activationTextCode);
                buttonOK = (Button) findViewById(R.id.activationButton);
                buttonOK.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        confirmActivation();
                    }
                });

            }

            if (pdialog == null) {
                pdialog = new ProgressDialog(this);
                pdialog.setIndeterminate(true);
                pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            }

            if (savedInstanceState != null) {
                dialogShown = savedInstanceState
                        .getBoolean(BUNDLE_KEY_DIALOG_SHOWN);
            }

            if (dialogShown) {
                pdialog.show();
            }

            if (dbHelper == null) {
                dbHelper = MessengerDatabaseHelper.getInstance(this);
            }
        }catch (Exception e){
            reportCatch(e.getLocalizedMessage());
        }
    }

    @Override
    protected void onPause() {
        try {
            unregisterReceiver(receiver);
            overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
        }catch (Exception e){
            reportCatch(e.getLocalizedMessage());
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            IntentFilter filter = new IntentFilter(SmsListener.SMS_RECEIVED);
            registerReceiver(receiver, filter);
        }catch (Exception e){
            reportCatch(e.getLocalizedMessage());
        }
    }

    public void setTimer() {
        try {
            new CountDownTimer(300000, 1000) {//300000 5 menit
                public void onTick(long millisUntilFinished) {
                    String FORMAT = "%02d:%02d";
                    textTimer.setText("" + String.format(FORMAT,
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                }

                public void onFinish() {
                    textTimer.setVisibility(View.INVISIBLE);
                    textTimeRemaning.setVisibility(View.GONE);
                    buttonOK.setVisibility(View.GONE);
                    buttonResend.setVisibility(View.VISIBLE);
                    textResend.setVisibility(View.VISIBLE);
                }
            }.start();
        }catch (Exception e){
            reportCatch(e.getLocalizedMessage());
        }
    }

    private void confirmActivation() {
        try {
            code = textActivationCode.getText().toString();
            if (!code.equals("")) {
                new ActivationRequestHelper().execute();
            }
        }catch (Exception e){
            reportCatch(e.getLocalizedMessage());
        }
    }

    private void confirmActivation(String codeAuto) {
        textActivationCode.setText(codeAuto);
        code = codeAuto;
        if (!code.equals("")) {
            new ActivationRequestHelper().execute();
        }
    }

    private void doStartActivity(Class theClass) {
        try {
            if (theClass.equals(LoadContactScreen.class)) {
                IntervalDB db = new IntervalDB(getApplicationContext());
                db.open();
                Cursor cursor = db.getSingleContact(12);
                if (cursor.getCount() > 0) {
                    db.deleteContact(12);
                }
                cursor.close();
                Interval interval = new Interval();
                interval.setId(12);
                interval.setTime("settingUp");
                db.createContact(interval);
                db.close();
            } else {
                IntervalDB db = new IntervalDB(getApplicationContext());
                db.open();
                Cursor cursor = db.getSingleContact(12);
                if (cursor.getCount() > 0) {
                    db.deleteContact(12);
                }
                cursor.close();
                db.close();
            }


            Intent intent = new Intent(this, theClass);
            startActivity(intent);
            finish();
        }catch (Exception e){
            reportCatch(e.getLocalizedMessage());
        }
    }

    private void onActivationSuccess() {
        try {
            doStartActivity(LoadContactScreen.class);
        }catch (Exception e){
            reportCatch(e.getLocalizedMessage());
        }
    }

    private void showErrorDialog(String message) {
        try {
            AlertDialog.Builder dlg = DialogUtil.generateAlertDialog(this, "Error",
                    message);
            dlg.setPositiveButton("OK", null);
            dlg.show();
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    private boolean isNetworkConnectionAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null)
            return false;
        State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }

    public class SmsListener extends BroadcastReceiver {

        private SharedPreferences preferences;
        private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";


        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                // TODO Auto-generated method stub

                if (intent.getAction().equals(SMS_RECEIVED)) {
                    Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
                    SmsMessage[] msgs = null;
                    if (bundle != null) {
                        try {
                            Object[] pdus = (Object[]) bundle.get("pdus");
                            msgs = new SmsMessage[pdus.length];
                            for (int i = 0; i < msgs.length; i++) {
                                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                                String msgBody = msgs[i].getMessageBody();
                                int start = msgBody.indexOf(":") + 2;
                                if (isInteger(msgBody.substring(start, start + 6))) {
                                    confirmActivation(msgBody.substring(start, start + 6));
                                }
                            }
                        }catch (Exception e){
                            reportCatch(e.getLocalizedMessage());
                        }
                    }
                }
            } catch (Exception e) {
                reportCatch(e.getLocalizedMessage());
            }
        }
    }

    public static boolean isInteger(String s) {
        return isInteger(s, 10);
    }

    public static boolean isInteger(String s, int radix) {
        if (s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) return false;
                else continue;
            }
            if (Character.digit(s.charAt(i), radix) < 0) return false;
        }
        return true;
    }

    class RegistrationRequest extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            pdialog.setMessage("Requesting new activation code ...");
            pdialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                HttpClient httpClient = HttpHelper
                        .createHttpClient(getApplicationContext());
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);
                nameValuePairs.add(new BasicNameValuePair("msisdn", number));
                HttpPost post = new HttpPost(REGISTRATION_URL);
                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpClient.execute(post);
                publishProgress(new Integer[]{Integer.valueOf(response
                        .getStatusLine().getStatusCode())});
            } catch (Exception e) {
                reportCatch(e.getLocalizedMessage());
                Log.e(getLocalClassName(), "Error requesting activation code: "
                        + e.getMessage(), e);
                publishProgress(new Integer[]{Integer.valueOf(0)});
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            pdialog.dismiss();
            if (values[0].intValue() == 202) {
                textTimer.setVisibility(View.VISIBLE);
                buttonResend.setVisibility(View.INVISIBLE);
                textResend.setVisibility(View.INVISIBLE);
                setTimer();
            } else {
                showRegistrationError("Registration failed. Please try again later.");
            }
        }
    }

    private void showRegistrationError(String message) {
        try {
            AlertDialog.Builder builder = DialogUtil.generateAlertDialog(this,
                    "Error", message);
            builder.setPositiveButton("OK", null);
            builder.show();
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    class ActivationRequestHelper extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            pdialog.setMessage("Activating code ...");
            pdialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            InputStreamReader reader = null;
            int result = 2;
            try {
                HttpClient httpClient = HttpHelper
                        .createHttpClient(getApplicationContext());

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        3);
                nameValuePairs.add(new BasicNameValuePair("msisdn", number));
                nameValuePairs.add(new BasicNameValuePair("code", code));
                HttpPost post = new HttpPost(ACTIVATION_URL);
                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpClient.execute(post);

                reader = new InputStreamReader(response.getEntity()
                        .getContent(), "UTF-8");
                StringBuilder buf = new StringBuilder();
                int r;
                while ((r = reader.read()) != -1) {
                    buf.append((char) r);

                }

                if (response.getStatusLine().getStatusCode() == 200) {
                    Contact contact = new Contact(1, buf.toString(), number, "");
                    dbHelper.insertData(contact);
                    result = 0;
                } else {
                    if (response.getStatusLine().getStatusCode() == 400) {
                        result = 1;
                    }
                }
            } catch (Exception e) {
                reportCatch(e.getLocalizedMessage());
                Log.e(getLocalClassName(), "Error requesting activation code: "
                        + e.getMessage(), e);
            } finally {
                if (reader != null)
                    try {
                        reader.close();
                    } catch (IOException e) {
                        reportCatch(e.getLocalizedMessage());
                    }
            }
            publishProgress(new Integer[]{Integer.valueOf(result)});
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            try {
                pdialog.dismiss();
                int res = values[0].intValue();
                if (res == 0) {
                    onActivationSuccess();
                } else if (res == 1) {
                    showErrorDialog("Activation failed. Please verify your activation code.");
                } else if (res == 2) {
                    showErrorDialog("Activation failed. Please try again later.");
                }
            } catch (Exception e) {
                reportCatch(e.getLocalizedMessage());
            }
        }
    }

}
