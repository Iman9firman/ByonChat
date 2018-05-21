package com.byonchat.android.personalRoom;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.byonchat.android.R;

/**
 * Created by lukma on 3/4/2016.
 */
public class FragmentMyContext extends Fragment {

    public FragmentMyContext() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_context, container, false);
//        View sss = inflater.inflate(R.layout.fragment_my_context, container, false);
//
//
//        return sss;
    }
}
