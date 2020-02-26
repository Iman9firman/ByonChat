package com.byonchat.android;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import com.google.android.material.tabs.TabLayout;
import androidx.multidex.MultiDex;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.communication.MyBroadcastReceiver;
import com.byonchat.android.communication.MyJobService;
import com.byonchat.android.communication.NotificationReceiver;
import com.byonchat.android.createMeme.FilteringImage;
import com.byonchat.android.provider.Contact;
import com.byonchat.android.provider.Interval;
import com.byonchat.android.provider.IntervalDB;
import com.byonchat.android.provider.Message;
import com.byonchat.android.provider.MessengerDatabaseHelper;
import com.byonchat.android.provider.Skin;
import com.byonchat.android.utils.TabsUtils;
import com.byonchat.android.utils.UploadService;

import java.util.ArrayList;
import java.util.List;

import me.leolin.shortcutbadger.ShortcutBadger;


public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener/*,ServiceConnection */ {

    private MessengerDatabaseHelper messengerHelper;
    private FragmentDrawer drawerFragment;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private static ViewPagerAdapter adapter;
    private int color = 0;
    private static final int TABTAG_CONTACT = 0;
    private static final int TABTAG_ROOMS = 1;
    private static final int TABTAG_CHAT = 2;
    private static final int TABTAG_MYMEMBERS = 3;
    private static final int TABTAG_OFFERS = 4;
    private static String SQL_SELECT_TOTAL_MESSAGES_UNREAD_ALL = "SELECT count(*) total FROM "
            + Message.TABLE_NAME
            + " WHERE status = ?";
    Context context = MainActivity.this;
    public static Uri CONTACT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    public static final String ACTION_CONTACT_REFRESHED = MainActivity.class
            .getName() + ".contactRefreshed";
    public static final String ACTION_REFRESH_BADGER = MainActivity.class
            .getName() + ".refreshBadger";
    public static final String ACTION_REFRESH_NOTIF = MainActivity.class
            .getName() + ".refreshNotif";
    private MessengerConnectionService.MessengerConnectionBinder binder;
    private BroadcastHandler broadcastHandler = new BroadcastHandler();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);


            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                ComponentName mServiceComponent = new ComponentName(context, MyJobService.class);
                JobInfo.Builder builder = null;
                int kJobId = 0;
                builder = new JobInfo.Builder(kJobId++, mServiceComponent);
                builder.setPeriodic(60 * 1000);//1 menit
                builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
                builder.setRequiresDeviceIdle(true); // device should be idle
                builder.setRequiresCharging(false); // we don't care if the device is charging or not
                JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                jobScheduler.schedule(builder.build());
            } else {
                Intent intent = new Intent(this, MyBroadcastReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        this.getApplicationContext(), 234324243, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), 1000 * 60, pendingIntent);//1 Menit
                PackageManager pm = context.getPackageManager();
                ComponentName receiver = new ComponentName(context, MyBroadcastReceiver.class);
                pm.setComponentEnabledSetting(receiver,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);

            }

            setTheme(R.style.AppTheme_NoActionBar);
            if (messengerHelper == null) {
                messengerHelper = MessengerDatabaseHelper.getInstance(this);
            }

            Intent intentStart = new Intent(this, UploadService.class);
            intentStart.putExtra(UploadService.ACTION, "startService");
            startService(intentStart);
            IntervalDB db = new IntervalDB(getApplicationContext());
            db.open();
            Cursor cursor = db.getSingleContact(12);
            if (cursor.getCount() > 0) {
                if (cursor.getString(cursor.getColumnIndexOrThrow(IntervalDB.COL_TIME)).equalsIgnoreCase("settingUp")) {
                    Intent intents = new Intent(this, LoadContactScreen.class);
                    startActivity(intents);
                    finish();
                    return;
                } else if (cursor.getString(cursor.getColumnIndexOrThrow(IntervalDB.COL_TIME)).equalsIgnoreCase("Finalizing")) {
                    Intent intents = new Intent(this, FinalizingActivity.class);
                    startActivity(intents);
                    finish();
                    return;
                }
            }
            cursor.close();


            final Contact contact = messengerHelper.getMyContact();

            if (contact == null) {
                Cursor cursorSkin = db.getCountSkin();
                ArrayList<Skin> skinArrayList = db.retriveallSkin();

                boolean insertByon = true;
                for (Skin s : skinArrayList) {
                    if (s.getTitle().equalsIgnoreCase("byonchat")) insertByon = false;
                }

                if (cursorSkin.getCount() == 0 || insertByon) {
                    Bitmap logos = BitmapFactory.decodeResource(getResources(), R.drawable.logo_byon);
                    Bitmap back = BitmapFactory.decodeResource(getResources(), R.drawable.bg_chat_baru);
                    Bitmap header = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                    Skin skin = new Skin("byonchat", "original", "#006b9c", logos, header, back);
                    db.createSkin(skin);
                }
                cursorSkin.close();

                db.close();
                Intent intent = new Intent(this, RegistrationActivity.class);
                startActivity(intent);
                finish();
                return;
            }

            setContentView(R.layout.activity_main);

            color = getResources().getColor(R.color.colorPrimary);

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


            Cursor cursorSelect = db.getSingleContact(4);
            if (cursorSelect.getCount() > 0) {
                String skin = cursorSelect.getString(cursorSelect.getColumnIndexOrThrow(IntervalDB.COL_TIME));
                Skin skins = null;
                Cursor c = db.getCountSkin();
                if (c.getCount() > 0) {
                    skins = db.retriveSkinDetails(skin);
                    color = Color.parseColor(skins.getColor());
                }
                c.close();
            } else {
                Interval interval = new Interval();
                interval.setId(4);
                interval.setTime("byonchat");
                db.createContact(interval);
            }
            cursorSelect.close();
            db.close();


            drawerFragment = (FragmentDrawer)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
            drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
            drawerFragment.setDrawerListener(this);

            viewPager = (ViewPager) findViewById(R.id.viewpager);
            setupViewPager(viewPager);
            viewPager.setOffscreenPageLimit(5);
            tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);
            tabLayout.setSelectedTabIndicatorHeight(3);
            initBackground(getResources().getString(R.string.tabtitle_rooms), color);
            setupTabIconsOnly();
            tabLayout.setOnTabSelectedListener(onTabSelectedListener(viewPager));
        } catch (Exception e) {
            Log.w("lij", e.toString());
        }
    }


    @Override
    protected void onPause() {
        unregisterReceiver(broadcastHandler);
        //  getApplicationContext().unbindService(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter f = new IntentFilter(
                MainActivity.ACTION_REFRESH_BADGER);
        f.addAction(MainActivity.ACTION_REFRESH_NOTIF);
        f.setPriority(1);
        registerReceiver(broadcastHandler, f);
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                .cancel(NotificationReceiver.NOTIFY_ID);
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                .cancel(NotificationReceiver.NOTIFY_ID_CARD);
        addShortcutBadger(getApplicationContext());
    }

    public void onDestroy() {
        super.onDestroy();
    }

    private void setupTabIconsOnly() {
        tabLayout.getTabAt(0).setCustomView(TabsUtils.renderTabView(MainActivity.this, R.drawable.tab_rooms, 0)).setTag(TABTAG_ROOMS).select();
        tabLayout.getTabAt(1).setCustomView(TabsUtils.renderTabView(MainActivity.this, R.drawable.tab_contact, 0)).setTag(TABTAG_CONTACT);
        tabLayout.getTabAt(2).setCustomView(TabsUtils.renderTabView(MainActivity.this, R.drawable.tab_chats, 0)).setTag(TABTAG_CHAT);
        tabLayout.getTabAt(3).setCustomView(TabsUtils.renderTabView(MainActivity.this, R.drawable.tab_members, 0)).setTag(TABTAG_MYMEMBERS);
        tabLayout.getTabAt(4).setCustomView(TabsUtils.renderTabView(MainActivity.this, R.drawable.tab_offers, 0)).setTag(TABTAG_OFFERS);
        updateColorTab(0, color);
    }

    private TabLayout.OnTabSelectedListener onTabSelectedListener(final ViewPager pager) {
        return new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());

                int i = ((Integer) tab.getTag()).intValue();
                switch (i) {

                    case TABTAG_CONTACT:
                        updateColorTab(1, color);
                        initBackground(getResources().getString(R.string.tabtitle_contact), color);
                        tabLayout.setSelectedTabIndicatorColor(color);
                        break;

                    case TABTAG_CHAT:
                        updateColorTab(2, color);
                        initBackground(getResources().getString(R.string.tabtitle_chat), color);
                        tabLayout.setSelectedTabIndicatorColor(color);
                        break;

                    case TABTAG_OFFERS:
                        updateColorTab(4, color);
                        initBackground(getResources().getString(R.string.tabtitle_offers_vouchers), color);
                        tabLayout.setSelectedTabIndicatorColor(color);
                        break;

                    case TABTAG_ROOMS:
                        updateColorTab(0, color);
                        initBackground(getResources().getString(R.string.tabtitle_rooms), color);
                        tabLayout.setSelectedTabIndicatorColor(color);
                        break;

                    case TABTAG_MYMEMBERS:
                        updateColorTab(3, color);
                        initBackground(getResources().getString(R.string.tabtitle_MyMembers), color);
                        tabLayout.setSelectedTabIndicatorColor(color);
                        break;

                    default:
                        updateColorTab(0, color);
                        initBackground(getResources().getString(R.string.tabtitle_rooms), color);
                        tabLayout.setSelectedTabIndicatorColor(color);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        };
    }

    private void initBackground(String title, int color) {
        toolbar.setTitle(title);
        Bitmap back_default = FilteringImage.headerColor(getWindow(), MainActivity.this, color);
        Drawable back_draw_default = new BitmapDrawable(getResources(), back_default);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            toolbar.setBackground(back_draw_default);
        } else {
            toolbar.setBackgroundDrawable(back_draw_default);
        }
    }

    public Drawable selectorBackgroundColor(Context context, int pressed) {
        GradientDrawable drawable = (GradientDrawable) context.getResources().getDrawable(R.drawable.cilcle_tab);
        drawable.setColor(pressed);
        return drawable;
    }

    private void setupViewPager(final ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new MainFragmentRoom(context), "rooms");
        adapter.addFrag(new MainFragmentContact(context), "contact");
        adapter.addFrag(new ListHistoryChatFragment(MainActivity.this), "chats");
        adapter.addFrag(new MyMembersFragment(context), "members");
        adapter.addFrag(new MainFragmentOffers(context), "offers");


        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //hanya yang
                ListHistoryChatFragment frag1 = (ListHistoryChatFragment) adapter.mFragmentList.get(2);
                frag1.finishActionMode();
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
    }

  /*  @Override
    public void onServiceConnected(ComponentName name, IBinder iBinder) {
        binder = (MessengerConnectionService.MessengerConnectionBinder) iBinder;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        binder = null;
    }
*/

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();//fragment array list
        private final List<String> mFragmentTitleList = new ArrayList<>();//title array list

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }


        //adding fragments and title method
        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }

    public void updateColorTab(int position, int color) {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            if (i == position) {
                TabsUtils.updateColor(tabLayout.getTabAt(i), selectorBackgroundColor(context, color));
            } else {
                TabsUtils.updateColor(tabLayout.getTabAt(i), selectorBackgroundColor(context, getApplicationContext().getResources().getColor(R.color.gray)));
            }
        }
    }

    public void addShortcutBadger(Context context) {

        int badgeCount = 0;
        if (messengerHelper == null) {
            messengerHelper = MessengerDatabaseHelper.getInstance(this);
        }
        Cursor cursor = messengerHelper.query(
                SQL_SELECT_TOTAL_MESSAGES_UNREAD_ALL,
                new String[]{String.valueOf(Message.STATUS_UNREAD)});
        int indexTotal = cursor.getColumnIndex("total");
        while (cursor.moveToNext()) {
            badgeCount = cursor.getInt(indexTotal);
        }
        cursor.close();

        ShortcutBadger.applyCount(context, badgeCount);
        //blm material
        if (tabLayout != null) {
            if (tabLayout.getTabAt(2) != null) {
                TabsUtils.updateTabBadge(tabLayout.getTabAt(2), badgeCount);
            }
        }
    }

    class BroadcastHandler extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MainActivity.ACTION_REFRESH_BADGER.equals(intent.getAction())) {
                addShortcutBadger(context);
            } else if (MainActivity.ACTION_REFRESH_NOTIF.equals(intent.getAction())) {
                addShortcutBadger(context);
                ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                        .cancel(NotificationReceiver.NOTIFY_ID);
                ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                        .cancel(NotificationReceiver.NOTIFY_ID_CARD);
            }
        }
    }

    @Override
    public void onBackPressed() {
        kelar();
    }

    public void kelar() {
        if (((FragmentDrawer) drawerFragment).mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            ((FragmentDrawer) drawerFragment).mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            ListHistoryChatFragment frag1 = (ListHistoryChatFragment) adapter.mFragmentList.get(2);
            if (!frag1.finishActionModeBoolean()) {
                finish();
            }
        }
    }
}

