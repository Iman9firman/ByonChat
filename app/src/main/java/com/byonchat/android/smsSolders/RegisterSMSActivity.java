package com.byonchat.android.smsSolders;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity;
import com.byonchat.android.R;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.FilesURL;
import com.byonchat.android.provider.FilesURLDatabaseHelper;
import com.byonchat.android.provider.Interval;
import com.byonchat.android.provider.IntervalDB;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.utils.HttpHelper;
import com.byonchat.android.utils.RequestKeyTask;
import com.byonchat.android.utils.RequestUploadSite;
import com.byonchat.android.utils.TaskCompleted;
import com.byonchat.android.utils.UploadService;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RegisterSMSActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    private DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day;
    Button button;
    TableLayout viewShow;
    EditText quotaSesama, quotaLain, tanggalMulai, tanggalSelesai, waktuMulai, waktuSelesai;
    RadioGroup radioGroup;
    String paket = "0";

    Boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_sms);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.BLACK);
        }

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);


        viewShow = (TableLayout) findViewById(R.id.viewShow);
        quotaSesama = (EditText) findViewById(R.id.editQuotaSesama);
        quotaLain = (EditText) findViewById(R.id.editQuotaLain);
        tanggalMulai = (EditText) findViewById(R.id.tanggalMulai);
        tanggalSelesai = (EditText) findViewById(R.id.tanggalSelesai);
        waktuMulai = (EditText) findViewById(R.id.waktuMulai);
        waktuSelesai = (EditText) findViewById(R.id.waktuSelesai);
        button = (Button) findViewById(R.id.submit);

        quotaSesama.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length()>1){
                    if(s.toString().startsWith("0")){
                        quotaSesama.setText((String) s.toString().subSequence(1,s.toString().length()));
                        quotaSesama.setSelection(quotaSesama.getText().length());
                    }
                }

            }
        });

        quotaLain.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length()>1){
                    if(s.toString().startsWith("0")){
                        quotaLain.setText((String) s.toString().subSequence(1,s.toString().length()));
                        quotaLain.setSelection(quotaLain.getText().length());
                    }
                }

            }
        });


        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) findViewById(checkedId);
                if (rb.getText().toString().equalsIgnoreCase("Daily")) {
                    viewShow.setVisibility(View.GONE);
                } else if (rb.getText().toString().equalsIgnoreCase("Weekly")) {
                    viewShow.setVisibility(View.GONE);
                } else if (rb.getText().toString().equalsIgnoreCase("Monthly")) {
                    viewShow.setVisibility(View.GONE);
                } else if (rb.getText().toString().equalsIgnoreCase("Custom")) {
                    viewShow.setVisibility(View.VISIBLE);
                }
            }
        });


        Intent intent = getIntent();

        IntervalDB db = new IntervalDB(getApplicationContext());
        db.open();
        Cursor cursor = db.getSingleContact(23);
        if (cursor != null && cursor.moveToFirst()) {
            JSONObject jObject = null;
            try {
                jObject = new JSONObject(cursor.getString(cursor.getColumnIndexOrThrow(IntervalDB.COL_TIME)));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (jObject != null) {
                try {
                    if (jObject.has("code")) {
                        return;
                    } else {
                        button.setText("UPDATE");
                        if (jObject.getString("paket").equalsIgnoreCase("Mingguan")) {
                            RadioButton rb = (RadioButton) findViewById(R.id.radioMingguan);
                            rb.setChecked(true);
                        } else if (jObject.getString("paket").equalsIgnoreCase("Bulanan")) {
                            RadioButton rb = (RadioButton) findViewById(R.id.radioBulanan);
                            rb.setChecked(true);
                        } else if (jObject.getString("paket").equalsIgnoreCase("Custom")) {
                            RadioButton rb = (RadioButton) findViewById(R.id.radioCostume);
                            rb.setChecked(true);
                        } else if (jObject.getString("paket").equalsIgnoreCase("Harian")) {
                            RadioButton rb = (RadioButton) findViewById(R.id.radioHarian);
                            rb.setChecked(true);
                        }
                        quotaSesama.setText(jObject.getString("kuota_sesama"));
                        quotaLain.setText(jObject.getString("kuota_all"));
                        tanggalMulai.setText(jObject.getString("tgl_mulai"));
                        tanggalSelesai.setText(jObject.getString("tgl_selesai"));
                        waktuMulai.setText(jObject.getString("jam_mulai"));
                        waktuSelesai.setText(jObject.getString("jam_selesai"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if (intent.getStringExtra("action") != null) {
            if (intent.getStringExtra("action").equalsIgnoreCase("smsSesamaBtn")) {
                exit = true;
                quotaSesama.requestFocus();
                quotaSesama.setSelection(quotaSesama.getText().length());
            } else if (intent.getStringExtra("action").equalsIgnoreCase("smsOperatorBtn")) {
                exit = true;
                quotaLain.requestFocus();
                quotaLain.setSelection(quotaLain.getText().length());
            } else if (intent.getStringExtra("action").equalsIgnoreCase("smsSceduleBtn")) {
                exit = true;
                tanggalMulai.requestFocus();
            }
        }


        tanggalMulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(999);
            }
        });
        tanggalSelesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(9999);
            }
        });
        waktuMulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(9991);
            }
        });
        waktuSelesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(9992);
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (quotaLain.getText().toString().equalsIgnoreCase("") && quotaSesama.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(RegisterSMSActivity.this, "Harap isi quota ", Toast.LENGTH_LONG).show();
                    return;
                }

                if (quotaSesama.getText().toString().equalsIgnoreCase("")) {
                    quotaSesama.setText("0");
                }

                if (quotaLain.getText().toString().equalsIgnoreCase("")) {
                    quotaLain.setText("0");
                }


                int selectedRadioButtonID = radioGroup.getCheckedRadioButtonId();
                if (selectedRadioButtonID != -1) {
                    RadioButton selectedRadioButton = (RadioButton) findViewById(selectedRadioButtonID);
                    String selectedRadioButtonText = selectedRadioButton.getText().toString();
                    if (selectedRadioButtonText.equalsIgnoreCase("Daily")) {
                        paket = "1";
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        Calendar calendasr = Calendar.getInstance();
                        calendasr.add(Calendar.DATE, 1);
                        String tadanjamS[] = dateFormat.format(new Date()).split(" ");
                        String tadanjamE[] = dateFormat.format(calendasr.getTime()).split(" ");

                        tanggalMulai.setText(tadanjamS[0]);
                        waktuMulai.setText(tadanjamS[1]);

                        tanggalSelesai.setText(tadanjamE[0]);
                        waktuSelesai.setText(tadanjamE[1]);


                    } else if (selectedRadioButtonText.equalsIgnoreCase("Weekly")) {
                        paket = "2";
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        Calendar calendasr = Calendar.getInstance();
                        calendasr.add(Calendar.DATE, 7);
                        String tadanjamS[] = dateFormat.format(new Date()).split(" ");
                        String tadanjamE[] = dateFormat.format(calendasr.getTime()).split(" ");

                        tanggalMulai.setText(tadanjamS[0]);
                        waktuMulai.setText(tadanjamS[1]);

                        tanggalSelesai.setText(tadanjamE[0]);
                        waktuSelesai.setText(tadanjamE[1]);
                    } else if (selectedRadioButtonText.equalsIgnoreCase("Monthly")) {
                        paket = "3";
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        Calendar calendasr = Calendar.getInstance();
                        calendasr.add(Calendar.DATE, 30);
                        String tadanjamS[] = dateFormat.format(new Date()).split(" ");
                        String tadanjamE[] = dateFormat.format(calendasr.getTime()).split(" ");

                        tanggalMulai.setText(tadanjamS[0]);
                        waktuMulai.setText(tadanjamS[1]);

                        tanggalSelesai.setText(tadanjamE[0]);
                        waktuSelesai.setText(tadanjamE[1]);
                    } else if (selectedRadioButtonText.equalsIgnoreCase("Costume")) {

                        if (tanggalMulai.getText().toString().equalsIgnoreCase("")) {
                            tanggalMulai.setError("harap di pilih");
                            return;
                        }
                        if (tanggalSelesai.getText().toString().equalsIgnoreCase("")) {
                            tanggalSelesai.setError("harap di pilih");
                            return;
                        }

                        if (waktuMulai.getText().toString().equalsIgnoreCase("")) {
                            waktuMulai.setError("harap di pilih");
                            return;
                        }
                        if (waktuSelesai.getText().toString().equalsIgnoreCase("")) {
                            waktuSelesai.setError("harap di pilih");
                            return;
                        }


                        String wakt = waktuMulai.getText().toString();
                        String waktu[] = wakt.split(":");

                        String wakt2 = waktuSelesai.getText().toString();
                        String waktu2[] = wakt2.split(":");

                        if (waktu.length == 2) {
                            wakt = wakt + ":00";
                        }
                        if (waktu2.length == 2) {
                            wakt2 = wakt2 + ":00";
                        }

                        if (isPackageExpired(tanggalMulai.getText().toString() + " " + wakt, null)) {
                            Toast.makeText(RegisterSMSActivity.this, "Waktu mulai tidak boleh kurang dari jam sekarang", Toast.LENGTH_LONG).show();
                            return;
                        }


                        if (isPackageExpired(tanggalMulai.getText().toString() + " " + wakt, tanggalSelesai.getText().toString() + " " + wakt2)) {
                            Toast.makeText(RegisterSMSActivity.this, "Waktu Selesai tidak boleh kurang dari Waktu Mulai", Toast.LENGTH_LONG).show();
                            return;
                        }

                    }
                } else {
                    Toast.makeText(RegisterSMSActivity.this, "Harap Pilih Paket", Toast.LENGTH_LONG).show();
                    return;
                }


                progressDialog = new ProgressDialog(RegisterSMSActivity.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.setTitle("LOADING");
                progressDialog.setMessage("PLEASE WAIT...");
                progressDialog.show();


                RequestKeyTask testAsyncTask = new RequestKeyTask(new TaskCompleted() {
                    @Override
                    public void onTaskDone(String key) {
                        progressDialog.dismiss();
                        new Refresh(RegisterSMSActivity.this).execute("https://bb.byonchat.com/smsgateway/register.php", key
                                , quotaSesama.getText().toString()
                                , quotaLain.getText().toString()
                                , tanggalMulai.getText().toString()
                                , tanggalSelesai.getText().toString()
                                , waktuMulai.getText().toString()
                                , waktuSelesai.getText().toString(), paket);
                    }
                }, getApplicationContext());
                testAsyncTask.execute();

            }
        });
    }

    private boolean isPackageExpired(String date, String date2) {
        boolean isExpired = false;
        Date expiredDate1 = stringToDate(date, "yyyy-MM-dd hh:mm:ss");
        Date expiredDate2 = stringToDate(date2, "yyyy-MM-dd hh:mm:ss");
        if (date2 != null) {
            if (expiredDate1.after(expiredDate2)) isExpired = true;
        } else {
            if (new Date().after(expiredDate1)) isExpired = true;
        }
        return isExpired;
    }

    private Date stringToDate(String aDate, String aFormat) {

        if (aDate == null) return null;
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat simpledateformat = new SimpleDateFormat(aFormat);
        Date stringDate = simpledateformat.parse(aDate, pos);
        return stringDate;

    }


    private class Refresh extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        String error = "";
        private Activity activity;
        private Context context;

        public Refresh(Activity activity) {
            this.activity = activity;
            context = activity;
            dialog = new ProgressDialog(activity);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Loading...");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            postData(params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7], params[8]);
            return null;
        }

        protected void onPostExecute(String result) {
            this.dialog.dismiss();
            Log.w("ada", error);
            JSONObject jObject = null;
            try {
                jObject = new JSONObject(error);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (jObject != null) {
                if (jObject.has("code")) {
                    try {
                        if (jObject.getString("code").equalsIgnoreCase("404")) {
                            IntervalDB db = new IntervalDB(getApplicationContext());
                            db.open();
                            Cursor cursor = db.getSingleContact(23);
                            if (cursor.getCount() > 0) {
                                db.deleteContact(23);
                            }
                            cursor.close();
                            db.close();
                            if (exit == false) {
                                startActivity(new Intent(activity, WelcomeActivitySMS.class));
                            }

                            finish();
                        } else {
                            Toast.makeText(context, jObject.getString("description"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    IntervalDB db = new IntervalDB(getApplicationContext());
                    db.open();
                    Cursor cursor = db.getSingleContact(23);
                    if (cursor.getCount() == 0) {
                        Interval interval = new Interval();
                        interval.setId(23);
                        interval.setTime(error);
                        db.createContact(interval);
                        db.close();
                    } else {
                        db.deleteContact(23);
                        Interval interval = new Interval();
                        interval.setId(23);
                        interval.setTime(error);
                        db.createContact(interval);
                        db.close();
                    }
                    cursor.close();
                    db.close();

                    if (exit == false) {
                        startActivity(new Intent(activity, WelcomeActivitySMS.class));
                    }
                    finish();
                }
            } else {
                if (error.length() > 0) {
                    if (error.equalsIgnoreCase("404")) {
                        IntervalDB db = new IntervalDB(getApplicationContext());
                        db.open();
                        Cursor cursor = db.getSingleContact(23);
                        if (cursor.getCount() > 0) {
                            db.deleteContact(23);
                        }
                        cursor.close();
                        db.close();
                        if (exit == false) {
                            startActivity(new Intent(activity, WelcomeActivitySMS.class));
                        }
                        finish();
                    } else {
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                    }
                }
            }

        }

        protected void onProgressUpdate(String... string) {
        }

        public void postData(String valueIWantToSend, String kye, String quotaAwal, String quotaLain, String tangMu, String tangSe, String wakMu, String wakSe, String paket) {
            // Create a new HttpClient and Post Header

            try {
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 13000);
                HttpConnectionParams.setSoTimeout(httpParameters, 15000);
                HttpClient httpclient = HttpHelper.createHttpClient();
                HttpPost httppost = new HttpPost(valueIWantToSend);

                MessengerDatabaseHelper messengerHelper = null;
                if (messengerHelper == null) {
                    messengerHelper = MessengerDatabaseHelper.getInstance(context);
                }

                Contact contact = messengerHelper.getMyContact();
                Log.w("quota1", quotaAwal);
                Log.w("quota1", quotaLain);
                // Add your data
                if (quotaAwal.length() == 0) {
                    quotaAwal = "0";
                }

                if (quotaLain.length() == 0) {
                    quotaLain = "0";
                }
                Log.w("quota1a", quotaAwal);
                Log.w("quota1b", quotaLain);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("no_bc", contact.getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("kuota_sesama", quotaAwal));
                nameValuePairs.add(new BasicNameValuePair("kuota_other", quotaLain));
                nameValuePairs.add(new BasicNameValuePair("tgl_mulai", tangMu));
                nameValuePairs.add(new BasicNameValuePair("tgl_selesai", tangSe));
                nameValuePairs.add(new BasicNameValuePair("waktu_mulai", wakMu));
                nameValuePairs.add(new BasicNameValuePair("waktu_selesai", wakSe));
                nameValuePairs.add(new BasicNameValuePair("username", contact.getJabberId()));
                nameValuePairs.add(new BasicNameValuePair("paket", paket));
                nameValuePairs.add(new BasicNameValuePair("key", kye));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);
                    error = data;
                } else {
                    error = "Tolong periksa koneksi internet.";
                }

            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                RegisterSMSActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Tolong periksa koneksi internet.", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (ClientProtocolException e) {
                RegisterSMSActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Tolong periksa koneksi internet.", Toast.LENGTH_SHORT).show();
                    }
                });
                // TODO Auto-generated catch block
            } catch (IOException e) {
                RegisterSMSActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Tolong periksa koneksi internet.", Toast.LENGTH_SHORT).show();
                    }
                });
                // TODO Auto-generated catch block
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            DatePickerDialog datePicker = new DatePickerDialog(this, myDateListenerMulai, year, month, day);
            datePicker.getDatePicker().setMinDate(System.currentTimeMillis());
            return datePicker;
        } else if (id == 9999) {
            DatePickerDialog datePicker = new DatePickerDialog(this, myDateListenerAkhir, year, month, day);
            datePicker.getDatePicker().setMinDate(System.currentTimeMillis());
            return datePicker;
        } else if (id == 9991) {
            Calendar mcurrentTime = Calendar.getInstance();
            int hours = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    this, mTimePickerAwal, hours, minute, true);
            return timePickerDialog;
        } else if (id == 9992) {
            Calendar mcurrentTime = Calendar.getInstance();
            int hours = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    this, mTimePickerAkhir, hours, minute, true);
            return timePickerDialog;

        }
        return null;
    }

    private TimePickerDialog.OnTimeSetListener mTimePickerAwal =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, final int selectedHour, final int selectedMinute) {
                    waktuMulai.setText(selectedHour + ":" + selectedMinute);
                }
            };

    private TimePickerDialog.OnTimeSetListener mTimePickerAkhir =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, final int selectedHour, final int selectedMinute) {
                    waktuSelesai.setText(selectedHour + ":" + selectedMinute);
                }
            };

    private DatePickerDialog.OnDateSetListener myDateListenerMulai = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            showDateMulai(arg1, arg2 + 1, arg3);
        }
    };

    private DatePickerDialog.OnDateSetListener myDateListenerAkhir = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            showDateAkhir(arg1, arg2 + 1, arg3);
        }
    };

    private void showDateMulai(int year, int month, int day) {
        tanggalMulai.setText(new StringBuilder().append(year).append("-")
                .append(month).append("-").append(day));
    }

    private void showDateAkhir(int year, int month, int day) {
        tanggalSelesai.setText(new StringBuilder().append(year).append("-")
                .append(month).append("-").append(day));
    }


}
