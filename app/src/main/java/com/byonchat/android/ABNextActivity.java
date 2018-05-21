package com.byonchat.android;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.widget.Button;

public abstract class ABNextActivity extends AppCompatActivity implements
        OnClickListener {
    private static final String BUTTON_TITLE = "NEXT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_next_menu, menu);
        configureActionItem(menu);
        return super.onCreateOptionsMenu(menu);
    }

    protected String getButtonTitle() {
        return BUTTON_TITLE;
    }

    private void configureActionItem(Menu menu) {
        MenuItem item = menu.findItem(R.id.menu_action_next);
        Button btn = (Button) MenuItemCompat.getActionView(item).findViewById(
                R.id.buttonAbNext);
        btn.setBackgroundColor(Color.TRANSPARENT);
        btn.setTypeface(null, Typeface.BOLD);
        btn.setText(getButtonTitle());
        btn.setTextColor(Color.WHITE);
        btn.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
             /*   Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);*/
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
