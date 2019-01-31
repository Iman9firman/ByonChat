package com.byonchat.android.tabRequest;

import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.byonchat.android.R;

import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsViewActivity extends AppCompatActivity {

    private String API_KEY = "5b3ce3597851110001cf6248a2149dbe2eaa4535a2ef344dc5889891";

    GeoPoint locationPoint = null;
    MyLocationNewOverlay locOverlay = null;
    Double LAT,LNG;
    int flagOsm = 0;
    String jsonReliever;
    ArrayList<Reliever> relieverList;
    MapView mapView;
    ArrayList<GeoPoint> wayPoints = new ArrayList<>();

    Handler handler;
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_osm_maps);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        getSupportActionBar().setTitle("Location");

        flagOsm = getIntent().getIntExtra("FLAG_OSM",0);

        mapFunction();
    }

    public void mapFunction(){

        mapView = (MapView) findViewById(R.id.map_detail);
        OnlineTileSourceBase tileSourceBase = new XYTileSource("MyOSM",
                1,
                20,
                256,
                ".png",
                new String[]{"http://osm.byonchat.com:8080/styles/osm-bright/"});
        mapView.setTileSource(tileSourceBase);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        IMapController mapController = mapView.getController();
        mapController.setZoom(14);

        RotationGestureOverlay rotationGestureOverlay = new RotationGestureOverlay(this, mapView);
        rotationGestureOverlay.setEnabled(true);
        mapView.getOverlays().add(rotationGestureOverlay);

        if (flagOsm != 888){
            jsonReliever = getIntent().getStringExtra(RelieverListActivity.XTRA_RELIEVER_JSON);
            LAT = Double.valueOf(getIntent().getStringExtra(RelieverListActivity.XTRA_LATITUDE));
            LNG = Double.valueOf(getIntent().getStringExtra(RelieverListActivity.XTRA_LONGITUDE));

            relieverList = RelieverListActivity.getList(jsonReliever,relieverList);
            locationPoint = new GeoPoint(LAT,LNG);
            mapController.setCenter(locationPoint);

            AccuracyOverlay accuracyOverlay = new AccuracyOverlay(locationPoint,5000);
            Marker marker = new Marker(mapView);
            marker.setPosition(locationPoint);
            mapView.getOverlays().add(accuracyOverlay);
            mapView.getOverlays().add(marker);

            //reliever
            DecimalFormat df = new DecimalFormat("#.00");
            for (int a = 0 ; a < relieverList.size() ; a ++){
                Marker m = new Marker(mapView);
                m.setPosition(new GeoPoint(Double.valueOf(relieverList.get(a).getRelieverLastLat()),Double.valueOf(relieverList.get(a).getRelieverLastLng())));
                m.setIcon(getResources().getDrawable(R.drawable.person));
                m.setTitle(relieverList.get(a).getRelieverName()+"\n"+df.format(Float.valueOf(relieverList.get(a).getRelieverDistance()))+" km");
                mapView.getOverlays().add(m);
            }
            //reliever over
        } else {

            RequestQueue queue = Volley.newRequestQueue(this);
            ORSRoadManager roadManager = new ORSRoadManager(this);
            roadManager.setService(API_KEY);

            wayPoints.add(new GeoPoint(-6.207304,106.787733));
            wayPoints.add(new GeoPoint(-6.197709,106.760676));

            Road road = roadManager.getRoad(wayPoints);
            Log.w("ivana","Distance : "+road.mLength+" , Duration : "+road.mDuration);
            Polyline roadOverlays = RoadManager.buildRoadOverlay(road);
            roadOverlays.setWidth(10f);

            mapView.getOverlays().add(roadOverlays);

            handler = new Handler();
            Marker m = new Marker(mapView);

            runnable = () -> {
                getLocation(m,mapController,queue);
                handler.postDelayed(runnable,3000);
            };
            handler.post(runnable);
        }

        GpsMyLocationProvider provider = new GpsMyLocationProvider(this);
        provider.addLocationSource(LocationManager.GPS_PROVIDER);
    }

    private void getLocation(Marker m , IMapController mapController , RequestQueue queue){
        StringRequest postRequest = new StringRequest(Request.Method.POST, "https://bb.byonchat.com/luar/tanya_lokasi.php",
                response -> {
                    try {
                        JSONObject jObj = new JSONObject(response);
                        GeoPoint nowLoc = new GeoPoint(Double.valueOf(jObj.getString("lat")),Double.valueOf(jObj.getString("long")));
                        m.setPosition(nowLoc);
                        mapView.getOverlays().add(m);
                        mapView.invalidate();
                        mapController.setCenter(nowLoc);
                    } catch (Exception e){}
                },
                error -> {
                    // error
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();
                params.put("user", "628589111111");
                return params;
            }
        };
        queue.add(postRequest);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        Configuration.getInstance().load(this,PreferenceManager.getDefaultSharedPreferences(this));
    }
}
