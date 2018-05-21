package com.byonchat.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.byonchat.android.list.BotAdapter;
import com.byonchat.android.provider.ContactBot;

import java.util.ArrayList;

/**
 * Created by Iman Firmansyah on 11/23/2015.
 */
@SuppressLint("ValidFragment")
public class MainFragmentRoom extends Fragment {

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static LinearLayout backgroundButton;
    public static int int_items = 2;
    Activity aa = getActivity();
    private ListView lv;
    BotAdapter adapter;
    private boolean show = false;
    ArrayList<ContactBot> botArrayListist;
    private Button mBtnbBrand, mBtnCelebs, mBtnPersons;

    private Context context;
    public MainFragmentRoom(Context ctx) {
        context= ctx;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View x = inflater.inflate(R.layout.main_fragment_room, null);
        backgroundButton =  (LinearLayout) x.findViewById(R.id.backgroundButton);
        tabLayout = (TabLayout) x.findViewById(R.id.tabs);
        viewPager = (ViewPager) x.findViewById(R.id.viewpager);
        mBtnbBrand = (Button) x.findViewById(R.id.btnBrand);
        mBtnCelebs = (Button) x.findViewById(R.id.btnCeleb);
        mBtnPersons = (Button) x.findViewById(R.id.btnPerson);

        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });
        setHasOptionsMenu(true);

        mBtnbBrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NewSearchRoomActivity.class);
                intent.putExtra("search", "brand");
                startActivity(intent);
            }
        });

        mBtnCelebs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NewSearchRoomActivity.class);
                intent.putExtra("search", "celeb");
                startActivity(intent);
            }
        });

        mBtnPersons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NewSearchRoomActivity.class);
                intent.putExtra("search", "person");
                startActivity(intent);
            }
        });
        return x;

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_rooms, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getContext(), NewSearchRoomActivity.class);
                intent.putExtra("search", "all");
                startActivity(intent);
                return false;
            }
        });

    }

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ListSelectedBotFragment(true,context);
                case 1:
                    return new ListTrendingBotFragment(false,context);
            }
            return null;
        }

        @Override
        public int getCount() {

            return int_items;

        }


        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return "Selected Rooms";
                case 1:
                    return "Trending";

            }
            return null;
        }
    }

}
