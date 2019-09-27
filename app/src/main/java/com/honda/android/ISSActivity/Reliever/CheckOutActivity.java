package com.honda.android.ISSActivity.Reliever;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.honda.android.ConversationActivity;
import com.honda.android.R;
import com.honda.android.helpers.Constants;
import com.honda.android.list.IconItem;
import com.honda.android.provider.ChatParty;
import com.honda.android.provider.Contact;

public class CheckOutActivity extends AppCompatActivity {
    private ImageView btnChat;
    private ImageView btnCall;
    private Button btnCheckOut;
    private EditText etNote;
    private RatingBar rating;
    private ImageView icon, back;
    private FrameLayout emblem;
    private Toolbar toolbar;
    private TextView tittle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_request);

        prepareObject();
        prepareText();
        prepareAction();

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
        btnCall = (ImageView) findViewById(R.id.btnCall);
        btnChat = (ImageView) findViewById(R.id.btnChat);
        etNote = (EditText) findViewById(R.id.etNote);
        rating = (RatingBar) findViewById(R.id.rating);
        btnCheckOut = (Button) findViewById(R.id.btnCheckOut);
        icon = (ImageView) findViewById(R.id.logo_toolbar);
        emblem = (FrameLayout) findViewById(R.id.frameLayoutPicasso);
        back = (ImageView) findViewById(R.id.back);
        icon.setVisibility(View.GONE);
        emblem.setVisibility(View.GONE);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tittle = (TextView) findViewById(R.id.title_toolbar);
    }

    private void prepareText(){
        tittle.setText("Check Out");
    }

    private void prepareAction() {
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
        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
