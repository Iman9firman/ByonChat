package com.honda.android.suggest;

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
 * Created by Lukmanpryg on 2/18/2016.
 */
public class JsonParseTag {
    double current_latitude,current_longitude;
    public JsonParseTag(){}
    public JsonParseTag(double current_latitude,double current_longitude){
        this.current_latitude=current_latitude;
        this.current_longitude=current_longitude;
    }
    public List<SuggestGetSetTag> getParseJsonWCF(String sName)
    {
        List<SuggestGetSetTag> ListData = new ArrayList<SuggestGetSetTag>();
        try {
            String temp=sName.replace(" ", "%20");
            URL js = new URL("https://" + MessengerConnectionService.HTTP_SERVER + "/room/trending.php");
            URLConnection jc = js.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(jc.getInputStream()));
            String line = reader.readLine();
            JSONObject jsonResponse = new JSONObject(line);
            JSONArray jsonArray = jsonResponse.getJSONArray("Keyword");
            for(int i = 0; i < jsonArray.length(); i++){
                String id = jsonArray.getString(i);
                String name = jsonArray.getString(i);
                ListData.add(new SuggestGetSetTag(id,name));            }
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return ListData;

    }
}
