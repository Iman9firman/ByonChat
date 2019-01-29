package com.byonchat.android.ISSActivity.Reliever;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.byonchat.android.R;
import com.byonchat.android.communication.MessengerConnectionService;

import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class SubmitRequestActivity extends AppCompatActivity {
    private TextView textSubmit, txtPlace, txtTime, txtDate, txtJob;
    private Button btnDirection, btnSubmit;
    private ImageView btnCall;
    private ImageView icon, back;
    private FrameLayout emblem;
    private Toolbar toolbar;
    private TextView tittle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_request);

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

    private void prepareObject() {
        textSubmit = (TextView) findViewById(R.id.kata2);
        txtPlace = (TextView) findViewById(R.id.idTempat);
        txtTime = (TextView) findViewById(R.id.idWaktu);
        txtDate = (TextView) findViewById(R.id.idTanggal);
        txtJob = (TextView) findViewById(R.id.idJob);
        btnDirection = (Button) findViewById(R.id.btnDirection);
        btnSubmit = (Button) findViewById(R.id.btnConfirm);
        btnCall = (ImageView) findViewById(R.id.btnCall);
        icon = (ImageView) findViewById(R.id.logo_toolbar);
        emblem = (FrameLayout) findViewById(R.id.frameLayoutPicasso);
        back = (ImageView) findViewById(R.id.back);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tittle = (TextView) findViewById(R.id.title_toolbar);
    }

    private void prepareText() {
        tittle.setText("Job Call");
        icon.setVisibility(View.GONE);
        emblem.setVisibility(View.GONE);
        textSubmit.setText("Mohon kedatangannya, jika berhalangan hadir di mohon konfirmasi ke pengawas yang bersangkutan atau HRD.");
        btnDirection.setText("Direction ");
    }

    private void actionButton() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SubmitRequestActivity.this, CheckInActivity.class);
                startActivity(i);
//                finish();
            }
        });
        btnDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (MessengerConnectionService.onLoc == false) {
                    MessengerConnectionService.onLoc = true;
                } else {
                    MessengerConnectionService.onLoc = false;
                }*/
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", -6.1979603,106.7457283, "Where the party is at");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:085328200060"));
                if (ActivityCompat.checkSelfPermission(SubmitRequestActivity.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SubmitRequestActivity.this, new String[] {
                            Manifest.permission.CALL_PHONE},1);
                }
                startActivity(callIntent);
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