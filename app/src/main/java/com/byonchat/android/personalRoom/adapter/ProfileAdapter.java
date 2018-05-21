package com.byonchat.android.personalRoom.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.byonchat.android.R;
import com.byonchat.android.personalRoom.controller.Profile;

import java.util.List;

/**
 * Created by lukma on 3/22/2016.
 */
public class ProfileAdapter extends ArrayAdapter<Profile> {

    private Context context;
    List<Profile> profiles;

    public ProfileAdapter(Context context, List<Profile> profiles){
        super(context, R.layout.fragment_my_profile_self, profiles);
        this.context = context;
        this.profiles = profiles;
    }

    private class ViewHolder{
        TextView profileStatus;
        TextView hashtag;
    }

    @Override
    public int getCount() {
        return profiles.size();
    }

    @Override
    public Profile getItem(int position){
        return profiles.get(position);
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder = null;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.fragment_my_profile_self, null);
            holder = new ViewHolder();

            holder.profileStatus = (TextView) convertView.findViewById(R.id.profileStatus);
            holder.hashtag = (TextView) convertView.findViewById(R.id.hashtag);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        Profile profile = (Profile) getItem(position);
        holder.profileStatus.setText(profile.getStatus() + "");
        holder.hashtag.setText(profile.getHashtag());

        return convertView;
    }

    @Override
    public void add(Profile profile){
        profiles.add(profile);
        notifyDataSetChanged();
        super.add(profile);
    }

    @Override
    public void remove(Profile profile){
        profiles.remove(profile);
        notifyDataSetChanged();
        super.remove(profile);
    }
}
