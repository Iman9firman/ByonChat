package com.byonchat.android.personalRoom.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.byonchat.android.personalRoom.DetailPictureFragment;
import com.byonchat.android.personalRoom.model.PictureModel;

import java.util.ArrayList;

/**
 * Created by Lukmanpryg on 12/29/2016.
 */

public class DetailPictureAdapter extends FragmentPagerAdapter {

    public ArrayList<PictureModel> data = new ArrayList<>();

    public DetailPictureAdapter(FragmentManager fm,  ArrayList<PictureModel> data) {
        super(fm);
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return data.get(position).getUserid();
    }

    @Override
    public Fragment getItem(int position) {
//        Log.w("sobami",data.get(position).getColor());
        return data.get(position) != null ? DetailPictureFragment.newInstance(position, data.get(position).getUrl(), data.get(position).getUrl_thumb(), data.get(position).getTitle(), data.get(position).getTgl_upload(), data.get(position).getDescription(), data.get(position).getMyuserid(), data.get(position).getUserid(), data.get(position).getId_photo(), data.get(position).getFlag(),data.get(position).getColor()) : null;
    }

    public void removeFirst()
    {
        if(data.size() > 0)
            data.remove(getCount() - getCount());
    }
}
