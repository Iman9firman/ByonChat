package com.honda.android.personalRoom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.honda.android.R;

/**
 * Created by Lukmanpryg on 12/29/2016.
 */

public class DetailPictureFragment extends Fragment {

    String ptitle, ptimestamp, purl, purlthumb, pdesc, pmyuserid, puserid, pid, pflag,pColor;
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

    public static DetailPictureFragment newInstance(int sectionNumber, String purl, String purlthumb, String ptitle, String ptimestamp, String pdesc, String pmyuserid, String puserid, String pid, String pflag,String pColor) {
        DetailPictureFragment fragment = new DetailPictureFragment();
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
        public void onFragmentCreated(String purl, String purlthumb, String ptitle, String ptimestamp, String pdesc, String myuserid, String puserid, String pid, String pflag,String pColor);
    }

    private View view;
    private ImageView mImageView, mPlay;
    private OnFragmentInteractionListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pr_picture_detail, container, false);

        mImageView = (ImageView) view.findViewById(R.id.detail_image);
        mPlay = (ImageView) view.findViewById(R.id.btn_play);

        if (pflag.equalsIgnoreCase("true")) {
            Glide.with(getActivity()).load(purl).thumbnail(0.1f).into(mImageView);
        } else {
            Glide.with(getActivity()).load(purlthumb).thumbnail(0.1f).into(mImageView);
            mPlay.setVisibility(View.VISIBLE);

            mPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), PlayVideoActivity.class);
                    intent.putExtra("url", purl);
                    getActivity().startActivity(intent);
                }
            });
        }

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pflag.equalsIgnoreCase("true")) {
                    Intent intent = new Intent(getActivity(), FullScreenDetailPicture.class);
                    intent.putExtra(FullScreenDetailPicture.PHOTO, purl);
                    intent.putExtra(FullScreenDetailPicture.JABBER_ID, puserid);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), PlayVideoActivity.class);
                    intent.putExtra("url", purl);
                    getActivity().startActivity(intent);
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            this.listener = (OnFragmentInteractionListener) context;
            Log.w("ujadn","hend");
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (getView()!=null){
                listener.onFragmentCreated(purl, purlthumb, ptitle, ptimestamp, pdesc, pmyuserid, puserid, pid, pflag, pColor);
            }

        }
    }
}
