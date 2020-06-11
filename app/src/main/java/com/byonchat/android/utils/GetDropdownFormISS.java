package com.byonchat.android.utils;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.FormSLADB;
import com.byonchat.android.provider.RealNameRoom;
import com.byonchat.android.provider.RoomsDB;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class GetDropdownFormISS {
    private static GetDropdownFormISS instance = new GetDropdownFormISS();
    static Context context;

    public GetDropdownFormISS getInstance(Context ctx) {
        context = ctx;
        return instance;
    }


    public String getName(String JJT) {

        FormSLADB roomsDB = new FormSLADB(context);
        roomsDB.open();
        List botArrayListist = roomsDB.getJJtJSON(JJT);
        if (botArrayListist.size() > 0) {
            return botArrayListist.get(0).toString();
        }
        roomsDB.close();

        return "null";

    }


    public int getALL() {

        FormSLADB roomsDB = new FormSLADB(context);
        roomsDB.open();
        List botArrayListist = roomsDB.getJJtJSONALL();
        if (botArrayListist.size() > 0) {
            return botArrayListist.size();
        }
        roomsDB.close();

        return 0;

    }


}
