package com.byonchat.android;

import android.graphics.Color;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.byonchat.android.adapter.SettingMainAdapter;
import com.byonchat.android.createMeme.FilteringImage;
import com.byonchat.android.helpers.Constants;

import static com.byonchat.android.utils.Utility.reportCatch;

public class MainSettingActivity extends AppCompatActivity {

    private String colorAttachment = "#005982";
    protected String mColor, mColorText;
    protected FrameLayout vContainerToolbar;
    protected FrameLayout vContainerTabLayout;
    protected Toolbar vToolbar;
    protected TabLayout vTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_setting);
        try {
            mColor = getIntent().getStringExtra(Constants.EXTRA_COLOR);
            mColorText = getIntent().getStringExtra(Constants.EXTRA_COLORTEXT);

            vContainerToolbar = (FrameLayout) findViewById(R.id.frame_toolbar);
            vContainerTabLayout = (FrameLayout) findViewById(R.id.frame_tablayout);
            vToolbar = (Toolbar) findViewById(R.id.toolbar);
            vTabLayout = (TabLayout) findViewById(R.id.tab_layout);

            resolveToolbar();
            resolveTabLayout();
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    protected void resolveToolbar() {
        try {
            if (mColor.equalsIgnoreCase("FFFFFF") && mColorText.equalsIgnoreCase("000000")) {
                View lytToolbarDark = getLayoutInflater().inflate(R.layout.toolbar_dark, vContainerToolbar);
                Toolbar toolbarDark = lytToolbarDark.findViewById(R.id.toolbar_dark);
                vContainerToolbar.removeView(vToolbar);
                setSupportActionBar(toolbarDark);
            } else {
                setSupportActionBar(vToolbar);
                getSupportActionBar().setTitle("Settings");

                FilteringImage.SystemBarBackground(getWindow(), Color.parseColor("#" + mColor));
                vToolbar.setBackgroundColor(Color.parseColor("#" + mColor));
                vToolbar.setTitleTextColor(Color.parseColor("#" + mColorText));
            }

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_35dp);
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
    }

    protected void resolveTabLayout() {
        try {
            if (mColor.equalsIgnoreCase("FFFFFF") && mColorText.equalsIgnoreCase("000000")) {
                View layoutTabLayoutDark = getLayoutInflater().inflate(R.layout.tablayout_dark, vContainerTabLayout);
                TabLayout tabLayout = layoutTabLayoutDark.findViewById(R.id.tab_layout_dark);
                vContainerTabLayout.removeView(vTabLayout);

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
                tabLayout.setBackgroundColor(Color.parseColor("#" + mColor));
            } else {
                vTabLayout.addTab(vTabLayout.newTab().setText("Profile"));
                //   tabLayout.addTab(tabLayout.newTab().setText("Notification"));
                vTabLayout.addTab(vTabLayout.newTab().setText("About"));
                vTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                vTabLayout.setSelectedTabIndicatorHeight(3);
                vTabLayout.setSelectedTabIndicatorColor(Color.parseColor("#" + mColorText));

                final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
                final SettingMainAdapter adapter = new SettingMainAdapter
                        (getSupportFragmentManager(), vTabLayout.getTabCount());
                viewPager.setAdapter(adapter);
                viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(vTabLayout));
                vTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
                vTabLayout.setBackgroundColor(Color.parseColor("#" + mColor));
            }
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
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