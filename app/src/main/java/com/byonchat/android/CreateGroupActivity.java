package com.byonchat.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import com.github.clans.fab.FloatingActionButton;

import static com.byonchat.android.utils.Utility.reportCatch;

public class CreateGroupActivity extends AppCompatActivity {

    EditText editTextNameGroup;
    FloatingActionButton fab;
    public static final String tutup = CreateGroupActivity.class.getName()+"tutup";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        try {
            editTextNameGroup = (EditText) findViewById(R.id.editTextNameGroup);
            fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.show(false);
            editTextNameGroup.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    // When user changed the Text
                    if (cs.toString().length() > 1) {
                        if (fab.isHidden()) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    fab.show(true);
                                    fab.setShowAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_from_bottom));
                                    fab.setHideAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_to_bottom));
                                }
                            }, 300);

                        }
                    } else if (cs.length() == 0) {
                        fab.show(false);
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    // TODO Auto-generated method stub
                }
            });
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = editTextNameGroup.getText().toString();
                    if (name.length() > 0) {
                        Intent i2 = new Intent(getApplicationContext(), PickUserActivity.class);
                        i2.putExtra(PickUserActivity.FROMACTIVITY, "Create Group");
                        i2.putExtra(PickUserActivity.NAMEGROUP, name);
                        startActivity(i2);
                    } else {
                        editTextNameGroup.setError("your group need name");
                    }

                }
            });

            IntentFilter filter = new IntentFilter();
            filter.addAction(tutup);
            registerReceiver(receiver, filter);

        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
    BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            finish();

        }
    };

    public void finish() {
        super.finish();
    };

}
