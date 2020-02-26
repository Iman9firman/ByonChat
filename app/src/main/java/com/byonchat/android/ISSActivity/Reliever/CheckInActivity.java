package com.byonchat.android.ISSActivity.Reliever;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.byonchat.android.ConversationActivity;
import com.byonchat.android.R;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.list.IconItem;
import com.byonchat.android.provider.ChatParty;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.MessengerDatabaseHelper;

import java.util.Locale;

public class CheckInActivity extends AppCompatActivity {
    private TextView textSubmit, txtPlace, txtTime, txtDate, txtJob;
    private Button btnDirection, btnCheckIn;
    private ImageView btnCall, btnChat;
    private MessengerDatabaseHelper databaseHelper;
    private ImageView icon, back;
    private FrameLayout emblem;
    private Toolbar toolbar;
    private TextView tittle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin_request);

        prepareObject();
        prepareText();
        actionButton();

        setSupportActionBar(toolbar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF6100")));

        if(Build.VERSION.SDK_INT >= 21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.parseColor("#FF6100"));
        }
    }

    private void prepareObject(){
        textSubmit = (TextView) findViewById(R.id.kata2);
        txtPlace = (TextView) findViewById(R.id.idTempat);
        txtTime = (TextView) findViewById(R.id.idWaktu);
        txtDate = (TextView) findViewById(R.id.idTanggal);
        txtJob = (TextView) findViewById(R.id.idJob);
        btnDirection = (Button) findViewById(R.id.btnDirection);
        btnCheckIn = (Button) findViewById(R.id.btnCheckIN);
        btnCall = (ImageView) findViewById(R.id.btnCall);
        btnChat = (ImageView) findViewById(R.id.btnChat);
        icon = (ImageView) findViewById(R.id.logo_toolbar);
        emblem = (FrameLayout) findViewById(R.id.frameLayoutPicasso);
        back = (ImageView) findViewById(R.id.back);
        icon.setVisibility(View.GONE);
        emblem.setVisibility(View.GONE);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tittle = (TextView) findViewById(R.id.title_toolbar);
    }

    private void prepareText(){
        tittle.setText("Work Schedule");
    }

    private void actionButton(){
        btnDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", -6.1979603,106.7457283, "Where the party is at");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatParty sample = new Contact("Lukluk","6287741701459","");
                IconItem item = new IconItem("6287741701459","Lukluk","","",sample);
                if (item.getJabberId().equalsIgnoreCase("")) {
                } else {
                    Intent intent = new Intent(getApplicationContext(), ConversationActivity.class);
                    String jabberId = item.getJabberId();
                    intent.putExtra(ConversationActivity.KEY_JABBER_ID, jabberId);
                    intent.putExtra(Constants.EXTRA_ITEM, item);
//                    intent.putExtra(Constants.EXTRA_COLOR, "000000");
//                    intent.putExtra(Constants.EXTRA_COLORTEXT, "000000");
                    startActivity(intent);
                }
            }
        });
        btnCall.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:085328200060"));
                startActivity(callIntent);
            }
        });
        btnCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CheckInActivity.this, CheckOutActivity.class);
                startActivity(i);
//                finish();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
