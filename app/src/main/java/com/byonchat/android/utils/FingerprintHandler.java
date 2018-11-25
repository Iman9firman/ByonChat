package com.byonchat.android.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.ByonChatMainRoomActivity;
import com.byonchat.android.ConversationActivity;
import com.byonchat.android.FragmentDinamicRoom.DinamicRoomTaskActivity;
import com.byonchat.android.LoginDinamicFingerPrint;
import com.byonchat.android.R;
import com.byonchat.android.RequestPasscodeRoomActivity;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.Rooms;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by whit3hawks on 11/16/16.
 */
@android.support.annotation.RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private Context context;
    private String username_room;
    private String bc_user;

    // Constructor
    public FingerprintHandler(Context mContext, String usr_room, String bc_uer) {
        context = mContext;
        username_room = usr_room;
        bc_user = bc_uer;
    }

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        CancellationSignal cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        this.update("Fingerprint Authentication error\n" + errString);
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        this.update("Fingerprint Authentication help\n" + helpString);
    }


    @Override
    public void onAuthenticationFailed() {
        this.update("Fingerprint Authentication failed.");
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
       /* Log.w("RESULT", result + "");
        ((Activity) context).finish();
        Intent intent = new Intent(context, HomeActivity.class);
        context.startActivity(intent);*/

        new Validations().getInstance(context).changeProtectLogin(username_room, "2");
        ((Activity) context).finish();

        Intent a = new Intent(context, LoginDinamicFingerPrint.class);
        a.putExtra(ConversationActivity.KEY_JABBER_ID, username_room);
        a.putExtra(ConversationActivity.KEY_TITLE, bc_user);
        a.putExtra(ConversationActivity.KEY_MESSAGE_FORWARD, "success");
        context.startActivity(a);
    }

    private void update(String e) {
        if (e.contains("Too many attempts")) {
            final AlertDialog.Builder alertbox = new AlertDialog.Builder(context);
            alertbox.setTitle("Error");
            String content = e;
            alertbox.setTitle(content);
            alertbox.setPositiveButton("Request Passcode", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    // TODO: 22/11/18 buat disini activity untuk request passcode dengan hendry dan simpan didb status pemesanan pascodenya

                    new Validations().getInstance(context).changeProtectLogin(username_room, "5");

                    ((Activity) context).finish();
                    Intent intent = new Intent(context, RequestPasscodeRoomActivity.class);
                    intent.putExtra(ConversationActivity.KEY_JABBER_ID, username_room);
                    intent.putExtra(ConversationActivity.KEY_TITLE, "request");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ((Activity) context).startActivity(intent);
                }
            });
            alertbox.setNegativeButton("Try Again", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    ((Activity) context).finish();
                }
            });

            alertbox.show();


        } else {
            TextView textView = (TextView) ((Activity) context).findViewById(R.id.errorText);
            textView.setText(e);
        }


    }

}
