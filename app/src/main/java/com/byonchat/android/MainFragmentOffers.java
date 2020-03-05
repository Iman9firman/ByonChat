package com.byonchat.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static com.byonchat.android.utils.Utility.reportCatch;

/**
 * Created by Iman Firmansyah on 11/23/2015.
 */
@SuppressLint("ValidFragment")
public class MainFragmentOffers extends Fragment {

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 2 ;
    private Context context;


    public MainFragmentOffers(Context ctx) {
        context=ctx;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View x =  inflater.inflate(R.layout.main_fragment_offers,null);
        try {
            tabLayout = (TabLayout) x.findViewById(R.id.tabs);
            viewPager = (ViewPager) x.findViewById(R.id.viewpager);

            viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
            tabLayout.post(new Runnable() {
                @Override
                public void run() {
                    tabLayout.setupWithViewPager(viewPager);
                }
            });
        } catch (Exception e) {
            reportCatch(e.getLocalizedMessage());
        }
        //setHasOptionsMenu(true);
        return x;

    }


    class MyAdapter extends FragmentPagerAdapter{

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public Fragment getItem(int position)
        {
            Fragment asd = null;
            switch (position) {
                case 0:
                    asd = new VoucherFragment(context);
                    break;

                case 1:
                    asd = new OffersFragment(context);
                    break;
            }
            return asd;
        }

        @Override
        public int getCount() {

            return int_items;

        }

        /**
         * This method returns the title of the tab according to the position.
         */

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position){
                case 0 :
                    return "Vouchers";
                case 1 :
                    return "Offers";
            }
            return null;
        }
    }
}
