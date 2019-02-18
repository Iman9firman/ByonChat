package com.byonchat.android.tabRequest;

import android.content.Context;
import android.util.Log;

import com.byonchat.android.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.utils.BonusPackHelper;
import org.osmdroid.bonuspack.utils.PolylineEncoder;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created By Zharfan 24/01/2019
 * **/

public class ORSRoadManager extends RoadManager {

    private final Context c;
    protected String a;
    protected String i = BuildConfig.ZHAR_SER;
    protected String w = BuildConfig.ZHAR_VI;
    protected String n = BuildConfig.ZHAR_CE;

    static final HashMap<Integer, Object> DIRECTIONS;
    static {
        DIRECTIONS = new HashMap<>();
        DIRECTIONS.put(1, org.osmdroid.bonuspack.R.string.osmbonuspack_directions_1);
        DIRECTIONS.put(2, org.osmdroid.bonuspack.R.string.osmbonuspack_directions_2);
        DIRECTIONS.put(3, org.osmdroid.bonuspack.R.string.osmbonuspack_directions_3);
        DIRECTIONS.put(4, org.osmdroid.bonuspack.R.string.osmbonuspack_directions_4);
        DIRECTIONS.put(5, org.osmdroid.bonuspack.R.string.osmbonuspack_directions_5);
        DIRECTIONS.put(6, org.osmdroid.bonuspack.R.string.osmbonuspack_directions_6);
        DIRECTIONS.put(7, org.osmdroid.bonuspack.R.string.osmbonuspack_directions_7);
        DIRECTIONS.put(8, org.osmdroid.bonuspack.R.string.osmbonuspack_directions_8);
        DIRECTIONS.put(12, org.osmdroid.bonuspack.R.string.osmbonuspack_directions_12);
        DIRECTIONS.put(17, org.osmdroid.bonuspack.R.string.osmbonuspack_directions_17);
        DIRECTIONS.put(18, org.osmdroid.bonuspack.R.string.osmbonuspack_directions_18);
        DIRECTIONS.put(19, org.osmdroid.bonuspack.R.string.osmbonuspack_directions_19);
        //DIRECTIONS.put(20, R.string.osmbonuspack_directions_20);
        //DIRECTIONS.put(21, R.string.osmbonuspack_directions_21);
        //DIRECTIONS.put(22, R.string.osmbonuspack_directions_22);
        DIRECTIONS.put(24, org.osmdroid.bonuspack.R.string.osmbonuspack_directions_24);
        DIRECTIONS.put(27, org.osmdroid.bonuspack.R.string.osmbonuspack_directions_27);
        DIRECTIONS.put(28, org.osmdroid.bonuspack.R.string.osmbonuspack_directions_28);
        DIRECTIONS.put(29, org.osmdroid.bonuspack.R.string.osmbonuspack_directions_29);
        DIRECTIONS.put(30, org.osmdroid.bonuspack.R.string.osmbonuspack_directions_30);
        DIRECTIONS.put(31, org.osmdroid.bonuspack.R.string.osmbonuspack_directions_31);
        DIRECTIONS.put(32, org.osmdroid.bonuspack.R.string.osmbonuspack_directions_32);
        DIRECTIONS.put(33, org.osmdroid.bonuspack.R.string.osmbonuspack_directions_33);
        DIRECTIONS.put(34, org.osmdroid.bonuspack.R.string.osmbonuspack_directions_34);
    }

    public ORSRoadManager(Context context){
        super();
        c = context;
    }

    public void setService(String apiKey){
        a = i+apiKey+w;
    }

    private String getUrl(ArrayList<GeoPoint> waypoints) {
        StringBuilder b = new StringBuilder(a);
        for (int i=0; i<waypoints.size(); i++){
            GeoPoint p = waypoints.get(i);
            if (i>0)
                b.append("%7C");
            b.append(geoPointAsLonLatString(p));
        }
        b.append(n );
        return b.toString();
    }

    private Road[] dR(ArrayList<GeoPoint> waypoints){
        Road[] roads = new Road[1];
        roads[0] = new Road(waypoints);
        return roads;
    }

    private Road[] gR(ArrayList<GeoPoint> waypoints, boolean apalah) {
        String c = getUrl(waypoints);
        String d = BonusPackHelper.requestStringFromUrl(c, null);
        if (d == null) {
            return dR(waypoints);
        }

        try {
            JSONObject aa = new JSONObject(d);
            JSONArray aaa = aa.getJSONArray("routes");
            Road[] aaaa = new Road[aaa.length()];
            for (int i=0; i<aaa.length(); i++){
                Road aaaaa = new Road();
                aaaa[i] = aaaaa;
                aaaaa.mStatus = Road.STATUS_OK;
                JSONObject bb = aaa.getJSONObject(i);
                JSONObject cc = bb.getJSONObject("summary");
                String route_geometry = bb.getString("geometry");
                aaaaa.mRouteHigh = PolylineEncoder.decode(route_geometry, 10, false);
                aaaaa.mBoundingBox = BoundingBox.fromGeoPoints(aaaaa.mRouteHigh);
                aaaaa.mLength = cc.getDouble("distance") / 1000.0;
                aaaaa.mDuration = cc.getDouble("duration");
            }
            return aaaa;
        } catch (JSONException e) {
            e.printStackTrace();
            return dR(waypoints);
        }
    }

    @Override public Road[] getRoads(ArrayList<GeoPoint> waypoints) {
        return gR(waypoints, true);
    }

    @Override public Road getRoad(ArrayList<GeoPoint> waypoints) {
        Road[] roads = gR(waypoints, false);
        return roads[0];
    }
}
