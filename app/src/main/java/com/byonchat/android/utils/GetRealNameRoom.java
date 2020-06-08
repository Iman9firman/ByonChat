package com.byonchat.android.utils;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.byonchat.android.communication.MessengerConnectionService;
import com.byonchat.android.communication.NetworkInternetConnectionStatus;
import com.byonchat.android.provider.BotListDB;
import com.byonchat.android.provider.RealNameRoom;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;


public class GetRealNameRoom {
	private static GetRealNameRoom instance = new GetRealNameRoom();
	static Context context;
    private static String REQUEST_REAL_NAME = "https://"
            + MessengerConnectionService.HTTP_SERVER + "/alias/?r=";
    private static String REQUEST_REAL_NAME_CONTACT = "https://"
            + MessengerConnectionService.HTTP_SERVER + "/memberships/aka.php?b=";

    private BotListDB botListDB;
    public GetRealNameRoom getInstance(Context ctx) {
        context = ctx;
        return instance;
    }

    public void deleteRoomName(String name){
        if(botListDB==null){
            botListDB = BotListDB.getInstance(context);
        }
        Cursor cur = botListDB.getRealNameByName(name);
        if (cur.getCount()>0) {
            botListDB.deleteRoomsName(name);
        }
        cur.close();
    }

    public String getName(String name){
        String nameRooms = "";
        if(botListDB==null){
            botListDB = BotListDB.getInstance(context);
        }
        Cursor cur = botListDB.getRealNameByName(name.toLowerCase());
        if (cur.getCount()>0) {
            nameRooms =  cur.getString(cur.getColumnIndex(BotListDB.ROOMS_REALNAME));
            if(nameRooms.equalsIgnoreCase("")){
                if (NetworkInternetConnectionStatus.getInstance(context).isOnline(context)) {
                    try {
                        botListDB.deleteRoomsName(name);
                        String regex = "[0-9]+";
                        if (!name.toLowerCase().matches(regex)) {
                            nameRooms =  new HttpAsyncTask().execute(REQUEST_REAL_NAME+name.toLowerCase()).get();
                        }else {
                            nameRooms =  new HttpAsyncTask().execute(REQUEST_REAL_NAME_CONTACT+name.toLowerCase()).get();

                        }
                        RealNameRoom realNameRoom = new RealNameRoom(name.toLowerCase(),nameRooms);
                        botListDB.insertRoomsName(realNameRoom);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        }else{
            if (NetworkInternetConnectionStatus.getInstance(context).isOnline(context)) {
                try {
                    String regex = "[0-9]+";
                    if (!name.toLowerCase().matches(regex)) {
                        nameRooms =  new HttpAsyncTask().execute(REQUEST_REAL_NAME+name.toLowerCase()).get();
                    }else {
                        nameRooms =  new HttpAsyncTask().execute(REQUEST_REAL_NAME_CONTACT+name.toLowerCase()).get();
                    }
                    RealNameRoom realNameRoom = new RealNameRoom(name.toLowerCase(),nameRooms);
                    botListDB.insertRoomsName(realNameRoom);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
        cur.close();
        return nameRooms;
    }

    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = HttpHelper.createHttpClient();
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
            inputStream = httpResponse.getEntity().getContent();
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "";
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }
        @Override
        protected void onPostExecute(String result) {
        }
    }
}
