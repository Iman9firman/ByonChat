package com.byonchat.android.communication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Iman Firmansyah on 10/17/2014.
 */
public class NetworkInternetConnectionStatus {
    private static NetworkInternetConnectionStatus instance = new NetworkInternetConnectionStatus();
    static Context context;
    ConnectivityManager connectivityManager;
    NetworkInfo wifiInfo, mobileInfo;
    boolean connected = false;


    public static NetworkInternetConnectionStatus getInstance(Context ctx) {
        context = ctx;
        return instance;
    }


    public boolean isOnline(Context con) {
        try {
            connectivityManager = (ConnectivityManager) con
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            // ARE WE CONNECTED TO THE NET
            if (connectivityManager.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING ||
                    connectivityManager.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING ||
                    connectivityManager.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {
                // MESSAGE TO SCREEN FOR TESTING (IF REQ)
                //Toast.makeText(this, connectionType + ” connected”, Toast.LENGTH_SHORT).show();
                connected = true;
            } else if (connectivityManager.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED || connectivityManager.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED) {
                //System.out.println(“Not Connected”);
                connected = false;
            }

            return connected;


        } catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
        }
        return connected;
    }
}
