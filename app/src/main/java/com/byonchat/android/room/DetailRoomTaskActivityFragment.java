package com.byonchat.android.room;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.byonchat.android.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailRoomTaskActivityFragment extends Fragment {

    public DetailRoomTaskActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail_room_task, container, false);
    }
}
