package com.byonchat.android.smsSolders;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.byonchat.android.ByonChatMainRoomActivity;
import com.byonchat.android.ConversationActivity;
import com.byonchat.android.FirstFragment;
import com.byonchat.android.FragmentDinamicRoom.FragmentRoomAbout;
import com.byonchat.android.LoadingGetTabRoomActivity;
import com.byonchat.android.R;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.helpers.ImageUtils;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.Interval;
import com.byonchat.android.provider.IntervalDB;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.utils.DialogUtil;
import com.byonchat.android.utils.MediaProcessingUtil;
import com.byonchat.android.utils.RequestKeyTask;
import com.byonchat.android.utils.TaskCompleted;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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
import java.net.URLDecoder;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import carbon.widget.CardView;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class HomeSMSSolders extends AppCompatActivity {

    SwipeRefreshLayout swipeLayout;
    TextView textPoint, smsSesama, smsOperator, smsScedule;
    android.support.v7.widget.CardView smsSesamaBtn, smsOperatorBtn, smsSceduleBtn, contactBtn;
    ImageButton createSortcut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_smssolders);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.BLACK);
        }

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RequestKeyTask testAsyncTask = new RequestKeyTask(new TaskCompleted() {
                    @Override
                    public void onTaskDone(String key) {
                        new Refresh(HomeSMSSolders.this).execute("https://bb.byonchat.com/smsgateway/kuota.php", key);
                    }
                }, getApplicationContext());
                testAsyncTask.execute();

            }
        });

        createSortcut = (ImageButton) findViewById(R.id.createSortcut);
        createSortcut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createShortcut();
            }
        });

        textPoint = (TextView) findViewById(R.id.textPoint);
        smsSesama = (TextView) findViewById(R.id.smsSesama);
        smsOperator = (TextView) findViewById(R.id.smsOperator);
        smsScedule = (TextView) findViewById(R.id.smsScedule);

        smsSesamaBtn = (android.support.v7.widget.CardView) findViewById(R.id.smsSesamaBtn);
        smsOperatorBtn = (android.support.v7.widget.CardView) findViewById(R.id.smsOperatorBtn);
        smsSceduleBtn = (android.support.v7.widget.CardView) findViewById(R.id.smsSceduleBtn);
        contactBtn = (android.support.v7.widget.CardView) findViewById(R.id.contactBtn);


        smsSesamaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aa = new Intent(HomeSMSSolders.this, RegisterSMSActivity.class);
                aa.putExtra("action", "smsSesamaBtn");
                startActivity(aa);
            }
        });
        smsOperatorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aa = new Intent(HomeSMSSolders.this, RegisterSMSActivity.class);
                aa.putExtra("action", "smsOperatorBtn");
                startActivity(aa);
            }
        });
        smsSceduleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntervalDB db = new IntervalDB(getApplicationContext());
                db.open();
                Cursor cursor = db.getSingleContact(23);
                if (cursor != null) {
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
                                AlertDialog.Builder alertbox = new AlertDialog.Builder(HomeSMSSolders.this);
                                alertbox.setTitle("Schedule");
                                alertbox.setMessage("Paket " + jObject.getString("paket") + "\n" + "from " + jObject.getString("tgl_mulai") + " " + jObject.getString("jam_mulai")
                                        + " to " + jObject.getString("tgl_selesai") + " " + jObject.getString("jam_selesai"));
                                alertbox.setPositiveButton("Reschedule", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        Intent aa = new Intent(HomeSMSSolders.this, RegisterSMSActivity.class);
                                        aa.putExtra("action", "smsSceduleBtn");
                                        startActivity(aa);
                                    }
                                });
                                alertbox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {

                                    }
                                });
                                alertbox.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                db.close();


            }
        });
        contactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeSMSSolders.this, ConversationActivity.class);
                intent.putExtra(ConversationActivity.KEY_JABBER_ID, "1_351102554admin");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });


        ImageView imageView2 = (ImageView) findViewById(R.id.imageView2);

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeSMSSolders.this, HistorySmsActivity.class));
            }
        });

        swipeLayout.setRefreshing(true);
        RequestKeyTask testAsyncTask = new RequestKeyTask(new TaskCompleted() {
            @Override
            public void onTaskDone(String key) {
                new Refresh(HomeSMSSolders.this).execute("https://bb.byonchat.com/smsgateway/kuota.php", key);
            }
        }, getApplicationContext());
        testAsyncTask.execute();


    }


    private void createShortcut() {
        final Dialog dialogConfirmation;
        dialogConfirmation = DialogUtil.customDialogConversationConfirmation(this);
        dialogConfirmation.show();

        TextView txtConfirmation = (TextView) dialogConfirmation.findViewById(R.id.confirmationTxt);
        TextView descConfirmation = (TextView) dialogConfirmation.findViewById(R.id.confirmationDesc);
        txtConfirmation.setText("Create Shortcut SMS Soldier");
        descConfirmation.setVisibility(View.VISIBLE);
        descConfirmation.setText("Are you sure you want to Create Shortcut?");

        Button btnNo = (Button) dialogConfirmation.findViewById(R.id.btnNo);
        Button btnYes = (Button) dialogConfirmation.findViewById(R.id.btnYes);
        btnNo.setText("Cancel");
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogConfirmation.dismiss();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogConfirmation.dismiss();
                Toast.makeText(HomeSMSSolders.this, "Create Shortcut Success", Toast.LENGTH_SHORT).show();
                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),  R.drawable.ic_soldier);

                Intent aa = new Intent(getApplicationContext(), WelcomeActivitySMS.class);
                Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
                shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "SMS Soldier");
                shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON, MediaProcessingUtil.getRoundedCornerBitmap(largeIcon,30));
                shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, aa);
                sendBroadcast(shortcutintent);
                finish();

                /*
                Picasso.with(HomeSMSSolders.this)
                        .load(R.drawable.ic_soldier)
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .transform(new RoundedCornersTransformation(30, 0, RoundedCornersTransformation.CornerType.ALL))
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {
                                Toast.makeText(HomeSMSSolders.this, "Create Shortcut failed", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {
                                Toast.makeText(HomeSMSSolders.this, "Please Wait", Toast.LENGTH_SHORT).show();
                            }
                        });*/


            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        IntervalDB db = new IntervalDB(getApplicationContext());
        db.open();
        Cursor cursor = db.getSingleContact(23);
        if (cursor != null) {
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
                        textPoint.setText(jObject.getString("point"));
                        smsSesama.setText(jObject.getString("kuota_sesama"));
                        smsOperator.setText(jObject.getString("kuota_all"));
                        smsScedule.setText(jObject.getString("paket") + "\n" + jObject.getString("tgl_selesai"));

                        String wakt2 = jObject.getString("jam_selesai");
                        String waktu2[] = wakt2.split(":");

                        if (waktu2.length == 2) {
                            wakt2 = wakt2 + ":00";
                        }

                        if (isPackageExpired(jObject.getString("tgl_selesai") + " " + wakt2)) {
                            smsScedule.setText("Expired");
                            smsSceduleBtn.setCardBackgroundColor(Color.RED);
                        } else {
                            smsSceduleBtn.setCardBackgroundColor(smsOperatorBtn.getCardBackgroundColor());
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            db.close();
        }


    }

    private boolean isPackageExpired(String date) {
        boolean isExpired = false;

        Date expiredDate = stringToDate(date, "yyyy-MM-dd hh:mm:ss");
        if (new Date().after(expiredDate)) isExpired = true;
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
        String error = "";
        private Activity activity;
        private Context context;

        public Refresh(Activity activity) {
            this.activity = activity;
            context = activity;
        }

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            postData(params[0], params[1]);
            return null;
        }

        protected void onPostExecute(String result) {
            swipeLayout.setRefreshing(false);

            Log.w("cucu", error);
            JSONObject jObject = null;
            try {
                jObject = new JSONObject(error);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (jObject != null) {
                try {
                    if (jObject.has("code")) {
                        if (jObject.getString("code").equalsIgnoreCase("404")) {
                            IntervalDB db = new IntervalDB(getApplicationContext());
                            db.open();
                            Cursor cursor = db.getSingleContact(23);
                            if (cursor.getCount() > 0) {
                                db.deleteContact(23);
                            }
                            cursor.close();
                            db.close();
                            startActivity(new Intent(activity, WelcomeActivitySMS.class));
                            finish();
                        }
                    } else {
                        textPoint.setText(jObject.getString("point"));
                        smsSesama.setText(jObject.getString("kuota_sesama"));
                        smsOperator.setText(jObject.getString("kuota_all"));
                        smsScedule.setText(jObject.getString("paket") + "\n sd \n" + jObject.getString("tgl_selesai"));
                        String wakt2 = jObject.getString("jam_selesai");
                        String waktu2[] = wakt2.split(":");

                        if (waktu2.length == 2) {
                            wakt2 = wakt2 + ":00";
                        }

                        if (waktu2.length == 2) {
                            wakt2 = wakt2 + ":00";
                        }


                        if (isPackageExpired(jObject.getString("tgl_selesai") + " " + wakt2)) {
                            smsScedule.setText("Expired");
                            smsSceduleBtn.setCardBackgroundColor(Color.RED);
                        } else {
                            smsSceduleBtn.setCardBackgroundColor(smsOperatorBtn.getCardBackgroundColor());
                        }


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

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
                        startActivity(new Intent(activity, WelcomeActivitySMS.class));
                        finish();
                    } else {
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

        protected void onProgressUpdate(String... string) {
        }

        public void postData(String valueIWantToSend, String kye) {
            // Create a new HttpClient and Post Header

            try {
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 13000);
                HttpConnectionParams.setSoTimeout(httpParameters, 15000);
                HttpClient httpclient = new DefaultHttpClient(httpParameters);
                HttpPost httppost = new HttpPost(valueIWantToSend);

                MessengerDatabaseHelper messengerHelper = null;
                if (messengerHelper == null) {
                    messengerHelper = MessengerDatabaseHelper.getInstance(context);
                }

                Contact contact = messengerHelper.getMyContact();
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("username", contact.getJabberId()));
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
                HomeSMSSolders.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Tolong periksa koneksi internet.", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (ClientProtocolException e) {
                HomeSMSSolders.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Tolong periksa koneksi internet.", Toast.LENGTH_SHORT).show();
                    }
                });
                // TODO Auto-generated catch block
            } catch (IOException e) {
                HomeSMSSolders.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Tolong periksa koneksi internet.", Toast.LENGTH_SHORT).show();
                    }
                });
                // TODO Auto-generated catch block
            }
        }
    }
}