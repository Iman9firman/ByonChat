package com.honda.android.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by byonc on 3/30/2017.
 */

public class JsonUtil {

    public static String toJsonRoom(String type, String tipe_room,String path){
        try {
            JSONObject parent = new JSONObject();
            parent.put("type", type);
            parent.put("tipe_room", tipe_room);
            parent.put("path", path);

            return parent.toString();
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    public static String toJsonDescription(String desc, String apps, String url){
        try {
            JSONObject parent = new JSONObject();
            parent.put("desc", desc);
            parent.put("apps", apps);
            parent.put("url", url);

            return parent.toString();
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }
}
