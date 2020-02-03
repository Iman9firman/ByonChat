package com.byonchat.android.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.byonchat.android.FragmentSetting.AboutSettingFragment;
import com.byonchat.android.FragmentSetting.ProfileSettingFragment;

/**
 * Created by Iman Firmansyah on 4/14/2016.
 */
public class SettingMainAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public SettingMainAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                ProfileSettingFragment tab1 = new ProfileSettingFragment();
                return tab1;
           /* case 1:
                NotificationSettingFragment tab2 = new NotificationSettingFragment();
                return tab2;*/
            case 1:
                AboutSettingFragment tab3 = new AboutSettingFragment();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
