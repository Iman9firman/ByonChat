package com.honda.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class GroupAddInfoActivity extends ABNextActivity {
    public static final String EXTRA_KEY_GROUP_NAME = "GROUP_NAME";
    public static final String EXTRA_KEY_GROUP_PICTURE_PATH = "GROUP_PICTURE_PATH";
    private EditText groupNameText;

    private void showToast() {
        Toast.makeText(this, getString(R.string.group_create_info),
                Toast.LENGTH_SHORT).show();
    }

    private void showInviteActivity(String groupName) {
        Intent i = new Intent(this, GroupAddInviteUsersActivity.class);
        i.putExtra(EXTRA_KEY_GROUP_NAME, groupName);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_create);
        groupNameText = (EditText) findViewById(R.id.creategroupName);

    }

    @Override
    public void onClick(View v) {
        String gname = groupNameText.getText().toString();
        if ("".equals(gname)) {
            showToast();
        } else {
            showInviteActivity(gname);
            finish();
        }
    }

}
