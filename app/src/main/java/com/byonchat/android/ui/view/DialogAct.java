package com.byonchat.android.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.*;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.ConversationActivity;
import com.byonchat.android.R;
import com.byonchat.android.local.Byonchat;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.ui.activity.MainActivityNew;
import com.byonchat.android.ui.viewholder.ByonchatApprovalDocViewHolder;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Date;

/*
 * Created by Zharfan on 20/05/2019
 * */
public class DialogAct extends Activity {

    /*
     * Default TAG for Zharfan :)
     * */
    private final String TAG = "ivana";
    /*
     * Key value for putExtra intent counter (time).
     * */
    public static final String COUNTER_TIME = "COUNTER_TIME_ZHARFAN";
    /*
     * Key value for putExtra intent counter (progress).
     * */
    public static final String COUNTER_PROG = "COUNTER_PROG_ZHARFAN";
    /*
     * Key value for putExtra intent username room.
     * */
    public static final String USERNAME_CIRCLE = "USNAME_BUKAN_ZHARFAN";
    /*
     * Key value for putExtra intent id_tab.
     * */
    public static final String ID_TAB_CIRCLE = "ID_TAB_BUKAN_ZHARFAN";
    /*
     * Key value for putExtra intent message.
     * */
    public static final String MSG_CIRCLE = "MSG_BUKAN_ZHARFAN";

    //Views.
    CircleProgressBar2 progressBar;
    CountDownTimer countDownTimer;
    ImageButton butClose;
    TextView textNote;
    TextView textTitle;
    Button openMessage;
    ImageView logoDialog;

    //Counter utility.
    Vibrator v;
    long[] pattern;
    int counter = 0;
    Handler handler;
    Runnable runnable;
    long counterForBack = 0;
    long getCounterFromIntent = 0;
    String id_tab;
    String username;
    BotListDB database;
    String message;

    //Data From DB
    String urlImageLogo;
    String tabName;

    public static void startDialog(Context context, long counterWhenBack, String username, String id_tab, String message, int counterForProgress) {
        /*
         * Static method for starting this dialog.
         * */
        Intent intent = new Intent(context, DialogAct.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra(COUNTER_TIME, counterWhenBack);
        intent.putExtra(USERNAME_CIRCLE, username);
        intent.putExtra(ID_TAB_CIRCLE, id_tab);
        intent.putExtra(COUNTER_PROG, counterForProgress);
        intent.putExtra(MSG_CIRCLE, message);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_dialog_activity);

        //Setting views.
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.70);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.35);
        getWindow().setLayout(width, height);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );

        //Setting counter.
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        pattern = new long[]{0, 250, 0, 250};
        setFinishOnTouchOutside(false);
        handler = new Handler();
        if (getIntent() != null) {
            getCounterFromIntent = getIntent().getLongExtra(COUNTER_TIME, 0);
            counter = getIntent().getIntExtra(COUNTER_PROG, 0);
            id_tab = getIntent().getStringExtra(ID_TAB_CIRCLE);
            username = getIntent().getStringExtra(USERNAME_CIRCLE);
            message = getIntent().getStringExtra(MSG_CIRCLE);
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        vibratePlease(v, pattern);

        //database
        database = BotListDB.getInstance(getBaseContext());
        getDataFromDB();

        //Initial views.
        progressBar = findViewById(R.id.progbar_urgent);
        butClose = findViewById(R.id.cancel_urgent);
        openMessage = findViewById(R.id.but_open_urgent);
        textNote = findViewById(R.id.note_urgent);
        textTitle = findViewById(R.id.title_urgent);
        logoDialog = findViewById(R.id.logo_urgent);

        //SetData
        Picasso.with(getBaseContext()).load(urlImageLogo).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(logoDialog);
        textTitle.setText(tabName);
        textNote.setText(message);

        //OnClick Action
        butClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                finish();
            }
        });
        openMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Submit listener here.
                 * */
                Contact contact = Byonchat.getMessengerHelper().getMyContact();

                Intent intent = new Intent(getBaseContext(), MainActivityNew.class);
                intent.putExtra(ConversationActivity.KEY_JABBER_ID, contact.getJabberId());
                intent.putExtra("success", "oke");
                startActivity(intent);

                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                finish();
            }
        });
        countDownTimer = new CountDownTimer(getCounterFromIntent, 1000) {

            public void onTick(long millisUntilFinished) {
                /*
                 * If counter divide by 10 equals 0 or counter < 10 , then vibrate the device.
                 * If dialog was removed by home or back button, start another dialog.
                 * */
                if (((int) (millisUntilFinished / 1000) % 10 == 0) || (int) (millisUntilFinished / 1000) < 10) {
                    if (isFinishing()) {
                        countDownTimer.cancel();
                        startDialog(getApplicationContext(), counterForBack, username, id_tab, message, counter);
                    } else {
                        vibratePlease(v, pattern);
                    }
                }
                /*
                 * Saving the value of counter time and progress for another dialog if current dialog removed.
                 * */
                String dateString = DateFormat.format("ss", new Date(millisUntilFinished)).toString();
                counterForBack = millisUntilFinished;
                openMessage.setText("Open (" + dateString + ")");
                counter++;
                /*
                 * Update circle progress bar with animation.
                 * */
                progressBar.setProgressWithAnimation((float) counter * 100 / (60000 / 1000));
                Log.w(TAG, "\nTick : " + (int) (millisUntilFinished / 1000) + "\nCounter : " + counter + "\nProgress : " + (float) counter * 100 / (60000 / 1000) + "\nIntent : " + getCounterFromIntent);
            }

            public void onFinish() {
                counter++;
                openMessage.setText("Open (00)");
                progressBar.setProgressWithAnimation(100);
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        /*
                         * Wait for 2 sec until dialog dismiss itself.
                         * */
                        finish();
                        handler.postDelayed(runnable, 2000);
                    }
                };
                handler.postDelayed(runnable, 2000);
            }

        }.start();
    }

    private void getDataFromDB() {
        Cursor cur = Byonchat.getBotListDB().getSingleRoom(username);
        if (cur.getCount() > 0) {
            String content = cur.getString(cur.getColumnIndex(BotListDB.ROOM_CONTENT));

            if (content.startsWith("JSONnnye")) {
                content = content.replace("JSONnnye", "");
                String[] bagibagi = content.split("@@@");
                content = bagibagi[0];
            }

            try {
                JSONArray jsonArray = new JSONArray(content);
                for (int i = 0; i < jsonArray.length(); i++) {
                    String category = jsonArray.getJSONObject(i).getString("category_tab").toString();
                    tabName = jsonArray.getJSONObject(i).getString("tab_name").toString();
                    String include_latlong = jsonArray.getJSONObject(i).getString("include_latlong").toString();
                    String include_pull = jsonArray.getJSONObject(i).getString("include_pull").toString();
                    String url_tembak = jsonArray.getJSONObject(i).getString("url_tembak").toString();
                    String id_rooms_tab = jsonArray.getJSONObject(i).getString("id_rooms_tab").toString();
                    String status = jsonArray.getJSONObject(i).getString("status").toString();
                    if (jsonArray.getJSONObject(i).has("icon_name")) {
                        urlImageLogo = jsonArray.getJSONObject(i).getString("icon_name").toString();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void vibratePlease(Vibrator v, long[] pattern) {
        /*
         * Method for vibrating the device with custom pattern , support all build version android.
         * */
        if (v != null) {
            if (v.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createWaveform(pattern, -1));
                } else {
                    v.vibrate(pattern, -1);
                }
            }
        }
    }
}