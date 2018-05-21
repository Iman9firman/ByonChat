package com.byonchat.android;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.utils.GPSTracker;
import com.byonchat.android.utils.UploadService;

import java.util.Date;

/**
 * Created by Iman Firmansyah on 2/16/2016.
 */
public class DialogPopUpActivity extends Activity {
     GPSTracker gps;
Button submitBtn,cancelBtn;
    MessengerDatabaseHelper messengerHelper ;
String pesan = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_popup_activity);
        gps = new GPSTracker(DialogPopUpActivity.this);

        pesan = getIntent().getStringExtra("pesan");

        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(getApplicationContext());
        }

        submitBtn = (Button)findViewById(R.id.submitBtn);
        cancelBtn = (Button)findViewById(R.id.cancelBtn);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            finish();
            }
        });



    }

    @Override
    public void onResume() {
        super.onResume();
        if (!gps.canGetLocation()) {
        }else{
            String pesan2[] = pesan.split(";");
            double  latitude = gps.getLatitude();
            double  longitude = gps.getLongitude();
            String planText = "location;"+pesan2[1] + ";"+latitude+","+longitude+";"+pesan2[2];
            if (NetworkInternetConnectionStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
                Message report = new Message(messengerHelper.getMyContact().getJabberId(), "x_byonchatbackground", planText);
                report.setType("text");
                report.setSendDate(new Date());
                report.setStatus(Message.STATUS_INPROGRESS);
                report.generatePacketId();

                Intent i = new Intent();
                i.setAction(UploadService.FILE_SEND_INTENT);
                i.putExtra(MessengerConnectionService.KEY_MESSAGE_OBJECT, report);
                sendBroadcast(i);
            }else{
                sendSMSMessage(planText);
            }
            finish();
        }
    }

    protected void sendSMSMessage(String content) {
        String phoneNo = "+6288977669999";
        String message = content;

        try {
            SmsManager sms = SmsManager.getDefault();
            String sent = "android.telephony.SmsManager.STATUS_ON_ICC_SENT";
            PendingIntent piSent = PendingIntent.getBroadcast(getApplicationContext(), 0,new Intent(sent), 0);
            sms.sendTextMessage(phoneNo, null, message, piSent, null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
