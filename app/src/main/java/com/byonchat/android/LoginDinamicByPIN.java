package com.byonchat.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.RoomsDB;
import com.chaos.view.PinView;
import com.squareup.picasso.Picasso;
import java.security.KeyStore;
import javax.crypto.Cipher;

import static com.byonchat.android.ByonChatMainRoomActivity.jsonResultType;

public class LoginDinamicByPIN  extends AppCompatActivity {

    private static final String KEY_NAME = "ByonChatKey";

    MessengerDatabaseHelper messengerHelper = null;
    BotListDB botListDB = null;
    private RoomsDB roomsDB;
    Context context;

    String colorText = "";
    String description = "";
    String color = "006b9c";
    String username = "";
    String name = "";
    String content = "";
    String current = "";
    String icon = "";
    String desc = "", realname = "", link = "", type = "";

    ProgressDialog progressDialog;
    TextView login_roomname, login_pin, reset_pin, text_info;
    PinView pinView;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_login);

        final Intent inti = getIntent();

        username = inti.getStringExtra(ConversationActivity.KEY_JABBER_ID);
        login_pin = (TextView) findViewById(R.id.login_pin);
        reset_pin = (TextView) findViewById(R.id.reset_pin);
        text_info = (TextView) findViewById(R.id.text_info);
        pinView = (PinView) findViewById(R.id.firstPinView);
        submit = (Button) findViewById(R.id.submit);

        text_info.setText(getResources().getString(R.string.input_pin));

        //Setting Pin View
        pinView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        pinView.setAnimationEnable(true);// start animation when adding text

        if (roomsDB == null) {
            roomsDB = new RoomsDB(LoginDinamicByPIN.this);
        }
        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(getApplicationContext());
        }
        if (botListDB == null) {
            botListDB = BotListDB.getInstance(getApplicationContext());
        }

        //Show Hide Button reset
        reset_pin.setVisibility(View.VISIBLE);

        Cursor cur = botListDB.getSingleRoom(username);

        if (cur.getCount() > 0) {
            name = cur.getString(cur.getColumnIndex(BotListDB.ROOM_REALNAME));

            color = jsonResultType(cur.getString(cur.getColumnIndex(BotListDB.ROOM_COLOR)), "a");
            colorText = jsonResultType(cur.getString(cur.getColumnIndex(BotListDB.ROOM_COLOR)), "b");
            content = cur.getString(cur.getColumnIndex(BotListDB.ROOM_CONTENT));
            icon = cur.getString(cur.getColumnIndex(BotListDB.ROOM_ICON));

            if (current.equalsIgnoreCase("")) {
                current = cur.getString(cur.getColumnIndex(BotListDB.ROOM_FIRST_TAB));
            }
            if (color == null || color.equalsIgnoreCase("") || color.equalsIgnoreCase("null")) {
                color = "006b9c";
            }
            if (colorText == null || colorText.equalsIgnoreCase("") || colorText.equalsIgnoreCase("null")) {
                colorText = "ffffff";
            }

            login_roomname = (TextView) findViewById(R.id.login_roomname);

            login_roomname.setText("Login to " + name);

            ConstraintLayout someView = (ConstraintLayout) findViewById(R.id.all_background);
            someView.setBackgroundColor(Color.parseColor("#" + color));
            pinView.setTextColor(Color.parseColor("#" + color));
            submit.setTextColor(Color.parseColor("#" + color));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(Color.parseColor("#" + color));
            }
        }

        reset_pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset_pin.setVisibility(View.GONE);
                text_info.setText(getResources().getString(R.string.set_pin));
            }
        });
    }
}