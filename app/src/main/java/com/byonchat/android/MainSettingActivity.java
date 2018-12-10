package com.byonchat.android;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;

import com.byonchat.android.adapter.SettingMainAdapter;
import com.byonchat.android.createMeme.FilteringImage;
import com.byonchat.android.helpers.Constants;
import com.byonchat.android.provider.IntervalDB;
import com.byonchat.android.provider.Skin;

public class MainSettingActivity extends AppCompatActivity {

    private String colorAttachment = "#005982";
    protected String mColor, mColorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_setting);

        mColor = getIntent().getStringExtra(Constants.EXTRA_COLOR);
        mColorText = getIntent().getStringExtra(Constants.EXTRA_COLORTEXT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Profile"));
        //   tabLayout.addTab(tabLayout.newTab().setText("Notification"));
        tabLayout.addTab(tabLayout.newTab().setText("About"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setSelectedTabIndicatorHeight(3);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#" + mColorText));

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final SettingMainAdapter adapter = new SettingMainAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        IntervalDB db = new IntervalDB(this);
        /*db.open();
        Cursor cursorSelect = db.getSingleContact(4);
        if (cursorSelect.getCount() > 0) {
            String skin = cursorSelect.getString(cursorSelect.getColumnIndexOrThrow(IntervalDB.COL_TIME));
            Skin skins = null;
            Cursor c = db.getCountSkin();
            if (c.getCount() > 0) {
                skins = db.retriveSkinDetails(skin);
                colorAttachment = skins.getColor();
                toolbar.setBackgroundColor(Color.parseColor(colorAttachment));
                tabLayout.setBackgroundColor(Color.parseColor(colorAttachment));
            }
            c.close();
        }
        cursorSelect.close();
        db.close();*/

        FilteringImage.SystemBarBackground(getWindow(), Color.parseColor("#" + mColor));
        toolbar.setBackgroundColor(Color.parseColor("#" + mColor));
        toolbar.setTitleTextColor(Color.parseColor("#" + mColorText));
        tabLayout.setBackgroundColor(Color.parseColor("#" + mColor));

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.parseColor(colorAttachment));
        }*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}