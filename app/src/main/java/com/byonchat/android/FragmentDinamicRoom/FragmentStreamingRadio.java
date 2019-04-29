package com.byonchat.android.FragmentDinamicRoom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.R;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.personalRoom.asynctask.ProfileSaveDescription;
import com.byonchat.android.personalRoom.model.NewsFeedItem;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.RoomsDetail;
import com.byonchat.android.utils.StreamService;
import com.byonchat.android.utils.UtilsRadio;
import com.byonchat.android.utils.Validations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lukma on 3/4/2016.
 */
@SuppressLint("ValidFragment")
public class FragmentStreamingRadio extends Fragment {

    private static final String TAG = FragmentStreamingVideo.class.getSimpleName();
    private List<NewsFeedItem> feedItems;
    private Activity mContext;
    private String myContact;
    private String title;
    private String urlTembak;
    private String username;
    private String idRoomTab;


    private Intent serviceIntent;
    private static boolean isStreaming = false;
    TextView nameRadio;
    TextView infoRadio;
    Button buttonPlay;
    ProgressBar progressRadio;
    private boolean mBufferBroadcastIsRegistered;
    SwipeRefreshLayout mswipeRefreshLayout;

    BotListDB db;

    public FragmentStreamingRadio(Activity ctx) {
        mContext = ctx;

    }

    // newInstance constructor for creating fragment with arguments
    public static FragmentStreamingRadio newInstance(String myc, String tit, String utm, String usr, String idrtab, Activity ctx) {
        FragmentStreamingRadio fragmentStreamingVideo = new FragmentStreamingRadio(ctx);
        Bundle args = new Bundle();
        args.putString("aa", tit);
        args.putString("bb", utm);
        args.putString("cc", usr);
        args.putString("dd", idrtab);
        args.putString("ee", myc);
        fragmentStreamingVideo.setArguments(args);
        return fragmentStreamingVideo;
    }

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getArguments().getString("aa");
        urlTembak = getArguments().getString("bb");
        username = getArguments().getString("cc");
        idRoomTab = getArguments().getString("dd");
        myContact = getArguments().getString("ee");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View sss = inflater.inflate(R.layout.room_fragment_streaming_radio, container, false);

        mswipeRefreshLayout = (SwipeRefreshLayout) sss.findViewById(R.id.activity_main_swipe_refresh_layout);

        if (db == null) {
            db = BotListDB.getInstance(mContext.getApplicationContext());
        }


        nameRadio = (TextView) sss.findViewById(R.id.nameRadio);
        infoRadio = (TextView) sss.findViewById(R.id.infoRadio);
        buttonPlay = (Button) sss.findViewById(R.id.buttonPlay);
        progressRadio = (ProgressBar) sss.findViewById(R.id.progressRadio);

        Cursor cursorValue = db.getSingleRoomDetailFormWithFlag("", username, idRoomTab, "value");
        if (cursorValue.getCount() > 0) {
            db.deleteRoomsDetailPtabPRoom(idRoomTab, username);
        }

        btnPress();

        mswipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkInternetConnectionStatus.getInstance(mContext.getApplicationContext()).isOnline(mContext.getApplicationContext())) {
                    new getLink().execute(urlTembak, myContact, username, idRoomTab);
                }
            }
        });


        return sss;
    }


    public void btnPress() {
        Cursor cursorValue = db.getSingleRoomDetailFormWithFlag("", username, idRoomTab, "value");
        if (cursorValue.getCount() > 0) {
            try {
                JSONObject jsonObject = new JSONObject(cursorValue.getString(cursorValue.getColumnIndexOrThrow(BotListDB.ROOM_CONTENT)));
                JSONArray data = jsonObject.getJSONArray("data");
                if (data.length() > 0) {

                    serviceIntent = new Intent(getActivity(), StreamService.class);
                    serviceIntent.putExtra("streaminglink", data.getJSONObject(0).getString("url_tembak"));
                    serviceIntent.putExtra("streamingName", "Radio");
                    serviceIntent.putExtra("streamingDesc", "streaming radio");
                    isStreaming = UtilsRadio.getDataBooleanFromSP(getActivity(), UtilsRadio.IS_STREAM);
                    if (isStreaming) {
                        buttonPlay.setBackgroundResource(R.drawable.ic_radio_stop);
                        if (new Validations().getInstance(getActivity()).getPlayStreaming("radio") == false) {
                            buttonPlay.setBackgroundResource(R.drawable.ic_radio_play);
                            //  Toast.makeText(getApplicationContext(), "Stop Streaming..", Toast.LENGTH_SHORT).show();
                            stopStreaming();
                            isStreaming = false;
                            progressRadio.setVisibility(View.GONE);
                            UtilsRadio.setDataBooleanToSP(getActivity(), UtilsRadio.IS_STREAM, false);
                        }
                    }
                    progressRadio.setIndeterminate(true);
                    progressRadio.setVisibility(View.GONE);

                    nameRadio.setText(title);
                    infoRadio.setText("radio");

                    buttonPlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!isStreaming) {
                                isStreaming = true;
                                startStreaming();
                                UtilsRadio.setDataBooleanToSP(getActivity(), UtilsRadio.IS_STREAM, true);
                                buttonPlay.setBackgroundResource(R.drawable.ic_radio_stop);
                            } else {
                                if (isStreaming) {
                                    buttonPlay.setBackgroundResource(R.drawable.ic_radio_play);
                                    // Toast.makeText(getApplicationContext(), "Stop Streaming..", Toast.LENGTH_SHORT).show();
                                    stopStreaming();
                                    isStreaming = false;
                                    progressRadio.setVisibility(View.GONE);
                                    UtilsRadio.setDataBooleanToSP(getActivity(), UtilsRadio.IS_STREAM, false);
                                }
                            }
                        }
                    });

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {

            if (NetworkInternetConnectionStatus.getInstance(mContext.getApplicationContext()).isOnline(mContext.getApplicationContext())) {
                new getLink().execute(urlTembak, myContact, username, idRoomTab);
            }
        }
    }

    class getLink extends AsyncTask<String, Void, String> {
        ProgressDialog loading;
        ProfileSaveDescription profileSaveDescription = new ProfileSaveDescription();
        String result = "";
        InputStream inputStream = null;

        public getLink() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("username_room", params[2]);
            data.put("id_rooms_tab", params[3]);
            data.put("bc_user", params[1]);
            String result = profileSaveDescription.sendPostRequest(params[0], data);
            return result;
        }

        protected void onPostExecute(String s) {
            if (s.equals(null)) {
            } else {
                mswipeRefreshLayout.setRefreshing(false);
                Cursor cursorValue = db.getSingleRoomDetailFormWithFlag("", username, idRoomTab, "value");
                if (cursorValue.getCount() > 0) {
                    db.deleteRoomsDetailPtabPRoom(idRoomTab, username);
                }

                RoomsDetail orderModel = new RoomsDetail("", idRoomTab, username, s, "", "", "value");
                db.insertRoomsDetail(orderModel);

                btnPress();
            }
        }
    }


    private void startStreaming() {
        Toast.makeText(getContext(), "Start Streaming..", Toast.LENGTH_SHORT).show();
        stopStreaming();
        try {
            getActivity().startService(serviceIntent);
        } catch (Exception e) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mBufferBroadcastIsRegistered) {
            getActivity().registerReceiver(broadcastBufferReceiver, new IntentFilter(
                    StreamService.BROADCAST_BUFFER));
            mBufferBroadcastIsRegistered = true;
        }


    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBufferBroadcastIsRegistered) {
            getActivity().unregisterReceiver(broadcastBufferReceiver);
            mBufferBroadcastIsRegistered = false;
        }

    }

    private BroadcastReceiver broadcastBufferReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent bufferIntent) {
            showProgressDialog(bufferIntent);
        }
    };

    private void stopStreaming() {
        try {
            getActivity().stopService(serviceIntent);
        } catch (Exception e) {
        }
    }

    private void showProgressDialog(Intent bufferIntent) {
        String bufferValue = bufferIntent.getStringExtra("buffering");
        int bufferIntValue = Integer.parseInt(bufferValue);
        switch (bufferIntValue) {
            case 0:
                if (progressRadio.getVisibility() == View.VISIBLE) {
                    progressRadio.setVisibility(View.GONE);
                    buttonPlay.setBackgroundResource(R.drawable.ic_radio_stop);
                }
                break;
            case 1:
                progressRadio.setVisibility(View.VISIBLE);
                buttonPlay.setBackgroundResource(R.drawable.ic_radio_play);
                break;
        }
    }

}
