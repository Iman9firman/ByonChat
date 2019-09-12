package com.honda.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.honda.android.personalRoom.model.PictureModel;

/**
 * Created by byonc on 4/25/2017.
 */

public class FollowupFragment extends Fragment {

    String ptitle, ptimestamp, purl, purlthumb, pdesc, pmyuserid, puserid, pid, pflag, pColor;
    int pos;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_IMG_URL = "image_url";
    private static final String ARG_IMG_URL_THUMB = "image_url_thumb";
    private static final String ARG_IMG_TITLE = "image_title";
    private static final String ARG_IMG_TIMESTAMP = "image_timestamp";
    private static final String ARG_IMG_DESC = "image_description";
    private static final String ARG_MYUSERID = "myuserid";
    private static final String ARG_USERID = "userid";
    private static final String ARG_IMG_ID = "image_id";
    private static final String ARG_FLAG = "flag";
    private static final String ARG_COLOR = "color";

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        this.pos = args.getInt(ARG_SECTION_NUMBER);
        this.purl = args.getString(ARG_IMG_URL);
        this.purlthumb = args.getString(ARG_IMG_URL_THUMB);
        this.ptitle = args.getString(ARG_IMG_TITLE);
        this.ptimestamp = args.getString(ARG_IMG_TIMESTAMP);
        this.pdesc = args.getString(ARG_IMG_DESC);
        this.pmyuserid = args.getString(ARG_MYUSERID);
        this.puserid = args.getString(ARG_USERID);
        this.pid = args.getString(ARG_IMG_ID);
        this.pflag = args.getString(ARG_FLAG);
        this.pColor = args.getString(ARG_COLOR);
    }

    public static FollowupFragment newInstance(int sectionNumber, String purl, String purlthumb, String ptitle, String ptimestamp, String pdesc, String pmyuserid, String puserid, String pid, String pflag, String pColor) {
        FollowupFragment fragment = new FollowupFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(ARG_IMG_URL, purl);
        args.putString(ARG_IMG_URL_THUMB, purlthumb);
        args.putString(ARG_IMG_TITLE, ptitle);
        args.putString(ARG_IMG_TIMESTAMP, ptimestamp);
        args.putString(ARG_IMG_DESC, pdesc);
        args.putString(ARG_MYUSERID, pmyuserid);
        args.putString(ARG_USERID, puserid);
        args.putString(ARG_IMG_ID, pid);
        args.putString(ARG_FLAG, pflag);
        args.putString(ARG_COLOR, pColor);
        fragment.setArguments(args);
        return fragment;
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentCreated(String purl, String purlthumb, String ptitle, String ptimestamp, String pdesc, String myuserid, String puserid, String pid, String pflag, String pColor);
    }

    private View view;
    private ImageView mImageView, mPlay;
    private EditText mtextMessage;
    private OnFragmentInteractionListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_follow_picture_multiple, container, false);

        mImageView = (ImageView) view.findViewById(R.id.detail_image);
        mtextMessage = (EditText) view.findViewById(R.id.textMessage);
        mPlay = (ImageView) view.findViewById(R.id.btn_play);

        Glide.with(getActivity()).load(purl).thumbnail(0.1f).into(mImageView);
        mtextMessage.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        try {
            listener = (OnFragmentInteractionListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
        super.onAttach(activity);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (getView() != null) {
                listener.onFragmentCreated(purl, purlthumb, ptitle, ptimestamp, pdesc, pmyuserid, puserid, pid, pflag, pColor);
            }

        }
    }
}
