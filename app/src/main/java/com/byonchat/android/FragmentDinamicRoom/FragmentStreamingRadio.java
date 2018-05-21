package com.byonchat.android.FragmentDinamicRoom;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.byonchat.android.R;
import com.byonchat.android.utils.StreamService;
import com.byonchat.android.utils.UtilsRadio;
import com.byonchat.android.utils.Validations;

/**
 * Created by lukma on 3/4/2016.
 */
public class FragmentStreamingRadio extends Fragment {

    private Intent serviceIntent;
    private static boolean isStreaming = false;
    TextView nameRadio;
    TextView infoRadio;
    Button buttonPlay;
    ProgressBar progressRadio;
    private boolean mBufferBroadcastIsRegistered;

    public FragmentStreamingRadio() {
        // Required empty public constructor
    }

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View sss = inflater.inflate(R.layout.room_fragment_streaming_radio, container, false);

        nameRadio = (TextView) sss.findViewById(R.id.nameRadio);
        infoRadio = (TextView) sss.findViewById(R.id.infoRadio);
        buttonPlay = (Button) sss.findViewById(R.id.buttonPlay);
        progressRadio = (ProgressBar)  sss.findViewById(R.id.progressRadio);

        serviceIntent = new Intent(getActivity(), StreamService.class);
        serviceIntent.putExtra("streaminglink", "http://live.indostreamserver.com:8800/kisfm?type=.mpeg");
        serviceIntent.putExtra("streamingName", "Radio bu");
        serviceIntent.putExtra("streamingDesc", "streaming lewat radio");
        isStreaming = UtilsRadio.getDataBooleanFromSP(getActivity(), UtilsRadio.IS_STREAM);
        if (isStreaming) {
            buttonPlay.setBackgroundResource(R.drawable.ic_radio_stop);
            if (new Validations().getInstance(getActivity()).getPlayStreaming("628158888248") == false) {
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
       // nameRadio.setText(Utility.roomName(getActivity(), "", true));
     //   infoRadio.setText("siapa ini");
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

        return sss;
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
                if (progressRadio.getVisibility()== View.VISIBLE) {
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
