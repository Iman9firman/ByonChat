package com.byonchat.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import static com.byonchat.android.utils.Utility.reportCatch;

public class GroupAddInfoActivity extends ABNextActivity {
    public static final String EXTRA_KEY_GROUP_NAME = "GROUP_NAME";
    public static final String EXTRA_KEY_GROUP_PICTURE_PATH = "GROUP_PICTURE_PATH";
    private EditText groupNameText;

    private void showToast() {
        Toast.makeText(this, getString(R.string.group_create_info),
                Toast.LENGTH_SHORT).show();
    }

    private void showInviteActivity(String groupName) {
        try {
            Intent i = new Intent(this, GroupAddInviteUsersActivity.class);
            i.putExtra(EXTRA_KEY_GROUP_NAME, groupName);
            startActivity(i);
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.group_create);
            groupNameText = (EditText) findViewById(R.id.creategroupName);

        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    @Override
    public void onClick(View v) {
        try {
            String gname = groupNameText.getText().toString();
            if ("".equals(gname)) {
                showToast();
            } else {
                showInviteActivity(gname);
                finish();
            }
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

}
