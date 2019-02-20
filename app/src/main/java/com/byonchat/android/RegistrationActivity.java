package com.byonchat.android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.provider.Country;
import com.byonchat.android.provider.Interval;
import com.byonchat.android.provider.IntervalDB;
import com.byonchat.android.utils.DialogUtil;
import com.byonchat.android.utils.HttpHelper;
import com.byonchat.android.utils.Utility;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by Iman Firmansyah on 11/19/2015.
 */
public class RegistrationActivity extends AppCompatActivity implements
        DialogInterface.OnClickListener {
    public static final String KEY_REGISTER_MSISDN = RegistrationActivity.class
            .getName() + ".REGISTER_MSISDN";
    private static final String BUNDLE_KEY_PROGRESS_DIALOG_SHOWN = "PROGRESS_DIALOG_SHOWN";
    private static final String BUNDLE_KEY_NUMBER = "NUMBER";
    public static final String BUNDLE_KEY_NAME = "NAMECOUNTRY";
    public static final String BUNDLE_KEY_CODE = "CODECOUNTRY";

    private static final String REGISTRATION_URL = "https://"
            + MessengerConnectionService.UTIL_SERVER + "/v1/codes";
    ImageView imageEarth;
    private LinearLayout linearLayoutContent;
    private EditText textCodeContry;
    private EditText textPhoneNumber;
    private Button buttonOK;
    private Button listCountry;
    private String number = null;
    private ProgressDialog pdialog;
    private boolean progressDialogShown = false;
    public String codeContry;
    public String nameContry;
    ArrayList<Country> arraylist = new ArrayList<Country>();
    IntervalDB db;
    private Handler mUiHandler = new Handler();
    private final static int ALL_PERMISSIONS_RESULT = 107;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected;
    //   private SharedPreferences sharedPreferences;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BUNDLE_KEY_PROGRESS_DIALOG_SHOWN,
                progressDialogShown);
        outState.putString(BUNDLE_KEY_NUMBER, number);
        outState.putString(BUNDLE_KEY_CODE, codeContry);
        outState.putString(BUNDLE_KEY_NAME, nameContry);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        progressDialogShown = savedInstanceState
                .getBoolean(BUNDLE_KEY_PROGRESS_DIALOG_SHOWN);
        number = savedInstanceState.getString(BUNDLE_KEY_NUMBER);
        codeContry = savedInstanceState.getString(BUNDLE_KEY_CODE);
        nameContry = savedInstanceState.getString(BUNDLE_KEY_NAME);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        cekPermision();

        if (db == null) {
            db = new IntervalDB(getApplicationContext());
            db.open();
        }

        if (!isNetworkConnectionAvailable()) {
            setContentView(R.layout.custom_information);
            ((TextView) findViewById(R.id.customInformationText))
                    .setText(R.string.registration_no_internet);
        } else {
            Cursor cursor = db.getSingleContact(12);
            if (cursor.getCount() > 0) {
                if (cursor.getString(cursor.getColumnIndexOrThrow(IntervalDB.COL_TIME)).equalsIgnoreCase("settingUp")) {
                    Intent intent = new Intent(this, LoadContactScreen.class);
                    intent.putExtra(KEY_REGISTER_MSISDN, number);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(this, ActivationCodeActivity.class);
                    intent.putExtra(KEY_REGISTER_MSISDN, number);
                    startActivity(intent);
                    finish();
                }
            } else {

                setContentView(R.layout.registration_pnumber);

                String[] name = Country.title;
                String[] code = Country.code;

                for (int i = 0; i < name.length; i++) {
                    Country country = new Country(name[i], code[i]);
                    arraylist.add(country);
                }

                imageEarth = (ImageView) findViewById(R.id.imageEarth);
                linearLayoutContent = (LinearLayout) findViewById(R.id.linearLayoutContent);
                textPhoneNumber = (EditText) findViewById(R.id.registrationTextPhoneNumber);
                textCodeContry = (EditText) findViewById(R.id.registrationTextCode);
                listCountry = (Button) findViewById(R.id.listCountry);
                buttonOK = (Button) findViewById(R.id.registrationButton);
                final Animation sunRise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.sun_rise);
                final Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_sort);
                imageEarth.startAnimation(sunRise);

                LinearLayout layout = (LinearLayout) findViewById(R.id.image_layout);
                LinearLayout layoutAnim = (LinearLayout) findViewById(R.id.image_layout_Anim);
                LinearLayout layoutWe = (LinearLayout) findViewById(R.id.Img_we_connect);
                ImageView logo = new ImageView(this);
                logo.setImageResource(R.mipmap.ic_launcher);
                ImageView animatedByonchat = new ImageView(this);
                animatedByonchat.setImageResource(R.drawable.loading_animation_byonchat);
                ImageView weByonchat = new ImageView(this);
                weByonchat.setImageResource(R.drawable.we_connect);

                AnimationDrawable frameAnimation = (AnimationDrawable) animatedByonchat.getDrawable();
                frameAnimation.setCallback(animatedByonchat);
                frameAnimation.setVisible(true, true);
                frameAnimation.start();

                int orgWidth = logo.getDrawable().getIntrinsicWidth();
                int orgHeight = logo.getDrawable().getIntrinsicHeight();
                int newWidth = (int) (getWindowManager().getDefaultDisplay().getWidth() / 4);
                if (orgWidth == orgHeight)
                    newWidth = (int) (getWindowManager().getDefaultDisplay().getWidth() / 5);
                int newHeight = (int) Math.floor((orgHeight * newWidth) / orgWidth);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        newWidth, newHeight);
                logo.setLayoutParams(params);
                logo.setScaleType(ImageView.ScaleType.CENTER_CROP);

                int orgWidthAnim = animatedByonchat.getDrawable().getIntrinsicWidth();
                int orgHeightAnim = animatedByonchat.getDrawable().getIntrinsicHeight();
                int newWidthAnim = (int) (getWindowManager().getDefaultDisplay().getWidth() / 2);
                if (orgWidthAnim == orgHeightAnim)
                    newWidthAnim = (int) (getWindowManager().getDefaultDisplay().getWidth() / 3);
                int newHeightAnim = (int) Math.floor((orgHeightAnim * newWidthAnim) / orgWidthAnim);
                LinearLayout.LayoutParams paramsAnim = new LinearLayout.LayoutParams(
                        newWidthAnim, newHeightAnim);
                animatedByonchat.setLayoutParams(paramsAnim);
                animatedByonchat.setScaleType(ImageView.ScaleType.CENTER_CROP);

                int orgWidthWe = weByonchat.getDrawable().getIntrinsicWidth();
                int orgHeightWe = weByonchat.getDrawable().getIntrinsicHeight();
                int newWidthWe = (int) (getWindowManager().getDefaultDisplay().getWidth() / 1.5);
                if (orgWidthWe == orgHeightWe)
                    newWidthWe = (int) (getWindowManager().getDefaultDisplay().getWidth() / 2);
                int newHeightWe = (int) Math.floor((orgHeightWe * newWidthWe) / orgWidthWe);
                LinearLayout.LayoutParams paramsWe = new LinearLayout.LayoutParams(
                        newWidthWe, newHeightWe);
                weByonchat.setLayoutParams(paramsWe);
                weByonchat.setScaleType(ImageView.ScaleType.CENTER_CROP);

                layout.addView(logo);
                layoutAnim.addView(animatedByonchat);
                layoutWe.addView(weByonchat);

                linearLayoutContent.setVisibility(View.INVISIBLE);
                int delay = 1000;
                mUiHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        linearLayoutContent.setVisibility(View.VISIBLE);
                        linearLayoutContent.startAnimation(fadeIn);
                    }
                }, delay);


                buttonOK.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        confirmRegistration();
                    }
                });
                listCountry.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        getCodeCountry();
                    }
                });

                if (getIntent().getStringExtra(BUNDLE_KEY_CODE) != null) {
                    codeContry = getIntent().getStringExtra(BUNDLE_KEY_CODE);
                    nameContry = getIntent().getStringExtra(BUNDLE_KEY_NAME);
                    textCodeContry.setText(codeContry);
                    listCountry.setText(nameContry);
                } else {
                    textCodeContry.setText(GetCountryZipCode());
                    String text = textCodeContry.getText().toString().toLowerCase(Locale.getDefault());
                    if (text.length() > 0) {
                        for (Country cp : arraylist) {
                            if ((cp.getCodeContry().replaceFirst("\\+", "")).equalsIgnoreCase(text)) {
                                listCountry.setText(cp.getNameContry());
                                break;
                            }
                        }
                    }
                }

                if (pdialog == null) {
                    pdialog = new ProgressDialog(this);
                    pdialog.setIndeterminate(true);
                    pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pdialog.setMessage("Requesting activation code ...");
                }
                if (savedInstanceState != null) {
                    progressDialogShown = savedInstanceState
                            .getBoolean(BUNDLE_KEY_PROGRESS_DIALOG_SHOWN);
                    number = savedInstanceState.getString(BUNDLE_KEY_NUMBER);
                    codeContry = savedInstanceState.getString(BUNDLE_KEY_CODE);
                    nameContry = savedInstanceState.getString(BUNDLE_KEY_NAME);
                }

                if (progressDialogShown) {
                    pdialog.show();
                }

                if (number != null) {
                    confirmRegistration();
                }

                // Capture Text in EditText
                textCodeContry.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void afterTextChanged(Editable arg0) {
                        // TODO Auto-generated method stub
                        String text = textCodeContry.getText().toString().toLowerCase(Locale.getDefault());
                        String showText = "invalid country";
                        if (text.length() > 0) {
                            for (Country cp : arraylist) {
                                if ((cp.getCodeContry().replaceFirst("\\+", "")).equalsIgnoreCase(text)) {
                                    showText = cp.getNameContry();
                                    break;
                                }
                            }
                        } else {
                            showText = "Choose a country";
                        }
                        listCountry.setText(showText);
                    }

                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1,
                                                  int arg2, int arg3) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {
                        // TODO Auto-generated method stub
                    }
                });
            }
        }
    }

    private void getCodeCountry() {
        Intent intent = new Intent(this, CountryListSelectorActivity.class);
        intent.putExtra(KEY_REGISTER_MSISDN, number);
        startActivity(intent);
    }


    public String GetCountryZipCode() {
        String CountryID = "";
        String CountryZipCode = "";

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode = g[0];
                break;
            }
        }
        return CountryZipCode;
    }

    private void confirmRegistration() {
        //  number = getString(R.string.phone_code).replaceFirst("\\+", "");
        String codeNumber = textCodeContry.getText().toString();
        Boolean validcode = false;

        for (String a : Country.code) {
            if (a.replaceFirst("\\+", "").equalsIgnoreCase(codeNumber)) {
                validcode = true;
                break;
            }
        }

        if (validcode) {
            String pnum = textPhoneNumber.getText().toString();
            number = codeNumber;
            if (pnum.length() > 0) {
                if (pnum.startsWith("0")) {
                    number += pnum.substring(1, pnum.length());
                } else {
                    number += pnum;
                }
                String info = "+" + Utility.formatPhoneNumber(number)
                        + getString(R.string.registration_alert_content);

                final Dialog dialogConfirmation;
                dialogConfirmation = DialogUtil.customDialogConversationConfirmation(this);
                dialogConfirmation.show();

                TextView txtConfirmation = (TextView) dialogConfirmation.findViewById(R.id.confirmationTxt);
                TextView descConfirmation = (TextView) dialogConfirmation.findViewById(R.id.confirmationDesc);
                txtConfirmation.setText(getString(R.string.registration_alert_title));
                descConfirmation.setVisibility(View.VISIBLE);
                descConfirmation.setText(info);

                Button btnNo = (Button) dialogConfirmation.findViewById(R.id.btnNo);
                Button btnYes = (Button) dialogConfirmation.findViewById(R.id.btnYes);

                btnNo.setText("Edit");
                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogConfirmation.dismiss();
                    }
                });

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new RegistrationRequest().execute();
                        dialogConfirmation.dismiss();
                    }
                });
            } else {
                textPhoneNumber.setError(getResources().getString(R.string.error_invalid_number));
            }

        } else {
            showRegistrationError(getResources().getString(R.string.error_invalid_code));
        }
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
    public void onClick(DialogInterface dialog, int which) {
        new RegistrationRequest().execute();
        dialog.dismiss();
    }

    private void showActivationActivity() {
        pdialog.dismiss();
        Intent intent = new Intent(this, ActivationCodeActivity.class);
        intent.putExtra(KEY_REGISTER_MSISDN, number);
        startActivity(intent);
        finish();
    }

    private void showRegistrationError(String message) {
        AlertDialog.Builder builder = DialogUtil.generateAlertDialog(this,
                "Error", message);
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    class RegistrationRequest extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
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
                Cursor cursor = db.getSingleContact(12);
                if (cursor.getCount() > 0) {
                    db.deleteContact(12);
                }
                Interval interval = new Interval();
                interval.setId(12);
                interval.setTime(number);
                db.createContact(interval);
                db.close();
                showActivationActivity();
            } else {
                showRegistrationError("Registration failed. Please try again later.");
            }
        }
    }

    /**
     * This is the method that is hit after the user accepts/declines the
     * permission you requested. For the purpose of this example I am showing a "success" header
     * when the user accepts the permission and a snackbar when the user declines it.  In your application
     * you will want to handle the accept/decline in a way that makes sense.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                boolean someAccepted = false;
                boolean someRejected = false;
                for (String perms : permissionsToRequest) {
                    if (hasPermission(perms)) {
                        someAccepted = true;
                    } else {
                        someRejected = true;
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    someRejected = true;
                }

                if (someAccepted) {
                    //  permissionSuccess.setVisibility(View.VISIBLE);
                    someRejected = false;
                }
                if (someRejected) {
                    //   makePostRequestSnack();
                    permissionsRejected.clear();
                    someRejected = false;

                    Toast.makeText(this, getString(R.string.permission_request_title), Toast.LENGTH_SHORT).show();

                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        cekPermision();
                    }, 2 * 1000);
                }
                break;
        }

    }


    private void cekPermision() {
        ArrayList<String> permissions = new ArrayList<>();
        int resultCode = 0;
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        permissions.add(CAMERA);
        permissions.add(READ_CONTACTS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            permissions.add(RECORD_AUDIO);
        permissions.add(CALL_PHONE);
        permissions.add(WRITE_EXTERNAL_STORAGE);
        permissions.add(SEND_SMS);
        resultCode = ALL_PERMISSIONS_RESULT;
        permissionsToRequest = findUnAskedPermissions(permissions);
        permissionsRejected = findRejectedPermissions(permissions);

        if (permissionsToRequest.size() > 0) {//we need to ask for permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), resultCode);
            }
        }
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();
        for (String perm : wanted) {
            result.add(perm);
        }

        return result;
    }

    private ArrayList<String> findRejectedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();
        for (String perm : wanted) {
            result.add(perm);
        }
        return result;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


}
