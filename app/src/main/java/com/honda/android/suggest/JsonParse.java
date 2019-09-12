package com.honda.android.suggest;

import android.util.Log;

import com.honda.android.communication.MessengerConnectionService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by Lukmanpryg on 2/17/2016.
 */
public class JsonParse {

    double current_latitude,current_longitude;
    public JsonParse(){}
    public JsonParse(double current_latitude,double current_longitude){
        this.current_latitude=current_latitude;
        this.current_longitude=current_longitude;
    }
    public List<SuggestGetSet> getParseJsonWCF(String sName)
    {
        List<SuggestGetSet> ListData = new ArrayList<SuggestGetSet>();
        try {
            String temp=sName.replace(" ", "%20");
            URL js = new URL("https://"+MessengerConnectionService.HTTP_SERVER+"/room/tags.php?t="+temp);
            URLConnection jc = js.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(jc.getInputStream()));
            String line = reader.readLine();
            JSONObject jsonResponse = new JSONObject(line);
            JSONArray jsonArray = jsonResponse.getJSONArray("keyword");
            for(int i = 0; i < jsonArray.length(); i++){

                String tag = jsonArray.getString(i);
                ListData.add(new SuggestGetSet(String.valueOf(i+1), tag));
            }
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return ListData;

    }
}
