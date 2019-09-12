package com.honda.android.location;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.honda.android.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Iman Firmansyah on 9/21/2016.
 */
public class ActivityDirection extends AppCompatActivity  implements OnMapReadyCallback {

    GoogleMap map;
    ArrayList<LatLng> markerPoints = new ArrayList<>();
    ArrayList<String> start = null;
    ArrayList<String> end = null;
    String id;
    TextView tvDistanceDuration;
    TextView editText, editText2;
    LinearLayout pick, dropOff;
    ImageButton cancelDrop;
    private FloatingActionButton imgMyLocation;
    private final int PLACE_PICKER_REQUEST_PICK = 1209;
    private final int PLACE_PICKER_REQUEST_DROP = 1210;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_direction);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        this.tvDistanceDuration = (TextView) this.findViewById(R.id.tv_distance_time);
        this.editText = (TextView) this.findViewById(R.id.editText);
        String pic = getIntent().getBundleExtra("loc_pic") != null ? getIntent().getBundleExtra("loc_pic").toString() : "";
        editText.setText(pic);
        this.editText2 = (TextView) this.findViewById(R.id.editText2);
        String drop = getIntent().getBundleExtra("loc_pic") != null ? getIntent().getBundleExtra("loc_pic").toString() : "";
        editText2.setText(drop);
        imgMyLocation = (FloatingActionButton) findViewById(R.id.imgMyLocation);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        cancelDrop = (ImageButton) findViewById(R.id.cancelDrop);
        pick = (LinearLayout) findViewById(R.id.pick);
        dropOff = (LinearLayout) findViewById(R.id.dropOff);
        id = getIntent().getStringExtra("id");

        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder intentBuilder =
                        new PlacePicker.IntentBuilder();
                Intent intent = null;
                try {
                    intent = intentBuilder.build(ActivityDirection.this);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                startActivityForResult(intent, PLACE_PICKER_REQUEST_PICK);
            }
        });

        cancelDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                end = null;
                editText2.setText("");
                if (markerPoints.size() >= 2) {
                    if (markerPoints.get(1) != null) {
                        markerPoints.remove(1);
                    }
                }
                map.clear();
                refreshMarker();
                tvDistanceDuration.setText("Distance");
            }
        });

        dropOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder intentBuilder =
                        new PlacePicker.IntentBuilder();
                Intent intent = null;
                try {
                    intent = intentBuilder.build(ActivityDirection.this);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                startActivityForResult(intent, PLACE_PICKER_REQUEST_DROP);
            }
        });


        imgMyLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Location loc = map.getMyLocation();
                if (loc != null) {
                    // map.clear();
                    LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14.0f);
                    map.animateCamera(cameraUpdate);

                } else {
                    LocationManager mgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (!mgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        Toast.makeText(getApplicationContext(), "GPS is disabled!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        this.map.setMyLocationEnabled(true);
        this.map.getUiSettings().setMyLocationButtonEnabled(false);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();

                return true;
            case R.id.btn_camera:
                if (end.size() > 0 && start.size() > 0) {
                    String aa = "";
                    for (String ah : start) {
                        aa += ah + ";";
                    }

                    String bb = "";
                    for (String ah : end) {
                        bb += ah + ";";
                    }
                    String content = jsonCreate(tvDistanceDuration.getText().toString(), aa, bb);
                    Bundle b = new Bundle();
                    b.putString("result", content);
                    Intent intent2 = new Intent();
                    intent2.putExtras(b);
                    setResult(RESULT_OK, intent2);
                    finish();
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String jsonCreate(String distance, String start, String end) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("d", distance);
            obj.put("s", start);
            obj.put("e", end);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (editText.getText().toString().equalsIgnoreCase("") || editText.length() == 0) {
            PlacePicker.IntentBuilder intentBuilder =
                    new PlacePicker.IntentBuilder();
            Intent intent = null;
            try {
                intent = intentBuilder.build(ActivityDirection.this);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
            startActivityForResult(intent, PLACE_PICKER_REQUEST_PICK);
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST_PICK) {
            if (resultCode == RESULT_OK) {
                final Place place = PlacePicker.getPlace(data, this);
                final String name = place.getName() != null ? (String) place.getName() : " ";
                final String address = place.getAddress() != null ? (String) place.getAddress() : " ";
                final String web = String.valueOf(place.getWebsiteUri() != null ? place.getWebsiteUri() : " ");
                final String lot = place.getLatLng().latitude + ";" + place.getLatLng().longitude;

                start = new ArrayList<>();
                start.add(0, lot);
                start.add(1, name);
                start.add(2, address);
                start.add(3, web);

                editText.setText(place.getName() != null ? place.getName() : place.getAddress() != null ? place.getAddress() : place.getLatLng().toString());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 14.0f);
                map.animateCamera(cameraUpdate);
                if (markerPoints.size() >= 1) {
                    if (markerPoints.get(0) != null) {
                        markerPoints.remove(0);
                    }
                }
                markerPoints.add(0, place.getLatLng());
                map.clear();
                refreshMarker();
            } else {
                if (editText.getText().toString().equalsIgnoreCase("") || editText.length() == 0) {
                    finish();
                }
            }
        } else if (requestCode == PLACE_PICKER_REQUEST_DROP) {
            if (resultCode == RESULT_OK) {
                final Place place = PlacePicker.getPlace(data, this);
                final String name = place.getName() != null ? (String) place.getName() : " ";
                final String address = place.getAddress() != null ? (String) place.getAddress() : " ";
                final String web = String.valueOf(place.getWebsiteUri() != null ? place.getWebsiteUri() : " ");
                final String lot = place.getLatLng().latitude + ";" + place.getLatLng().longitude;

                end = new ArrayList<>();
                end.add(0, lot);
                end.add(1, name);
                end.add(2, address);
                end.add(3, web);

                editText2.setText(place.getName() != null ? place.getName() : place.getAddress() != null ? place.getAddress() : place.getLatLng().toString());
                if (markerPoints.size() >= 2) {
                    if (markerPoints.get(1) != null) {
                        markerPoints.remove(1);
                    }
                }
                markerPoints.add(1, place.getLatLng());
                map.clear();
                refreshMarker();
            }
        }
    }

    public void refreshMarker() {
        if (markerPoints.get(0) != null) {
            MarkerOptions options = new MarkerOptions();
            options.position(markerPoints.get(0));
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            map.addMarker(options);
        }

        if (markerPoints.size() >= 2) {
            if (markerPoints.get(1) != null) {
                MarkerOptions options = new MarkerOptions();
                options.position(markerPoints.get(1));
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                map.addMarker(options);
            }
            if (markerPoints.size() >= 2) {
                LatLng origin = markerPoints.get(0);
                LatLng dest = markerPoints.get(1);
                LatLngBounds.Builder bc = new LatLngBounds.Builder();

                bc.include(dest);
                bc.include(origin);
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 200));

                String url = getDirectionsUrl(origin, dest);
                DownloadTask downloadTask = new DownloadTask();
                downloadTask.execute(url);
            }
        }


    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        String url = "https://bb.byonchat.com/bc_voucher_client/webservice/category_tab/dita-fare-api.php?" + parameters + "&id=" + id;
        return url;
    }


    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception while url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = ActivityDirection.this.downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);

        }
    }


    private class ParserTask extends AsyncTask<String, Integer, JSONObject> {

        // Parsing the data in non-ui thread
        @Override
        protected JSONObject doInBackground(String... jsonData) {
            JSONObject jObject = null;

            try {
                jObject = new JSONObject(jsonData[0]);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return jObject;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(JSONObject result) {


            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            if (result == null) {
                Toast.makeText(ActivityDirection.this.getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                String distance1 = result.getString("distance");
                String duration1 = result.getString("duration");
                String step = result.getString("steps");
                // String endLocation = result.getString("end_location");
                // String start_location = result.getString("start_location");
                String fareEstimate = result.getString("fare_estimate");

                /*JSONObject js = new JSONObject(start_location);
                double latS = Double.parseDouble(js.getString("lat"));
                double lngS = Double.parseDouble(js.getString("lng"));

                JSONObject je = new JSONObject(endLocation);
                double latE = Double.parseDouble(je.getString("lat"));
                double lngE = Double.parseDouble(je.getString("lng"));

**/

                JSONArray jsonarray = new JSONArray(step);

                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);

                    String el = jsonobject.getString("end_location");
                    String sl = jsonobject.getString("start_location");

                    JSONObject js1 = new JSONObject(sl);
                    double latS1 = Double.parseDouble(js1.getString("lat"));
                    double lngS1 = Double.parseDouble(js1.getString("lng"));

                    JSONObject je1 = new JSONObject(el);
                    double latE1 = Double.parseDouble(je1.getString("lat"));
                    double lngE1 = Double.parseDouble(je1.getString("lng"));

                    LatLng positionStart1 = new LatLng(latS1, lngS1);
                    LatLng positionEnd1 = new LatLng(latE1, lngE1);

                    points.add(positionStart1);
                    points.add(positionEnd1);

                }

                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(Color.RED);

                String dist = new JSONObject(distance1).getString("text");
                String dur = new JSONObject(duration1).getString("text");
                String far = new JSONObject(fareEstimate).getString("text");
                String resultFar = "";
                if (!far.equalsIgnoreCase("")) {
                    resultFar = " \n Fare Estimate:" + far;
                }


                ActivityDirection.this.tvDistanceDuration.setText("Distance:" + dist + ", Duration:" + dur + resultFar);
                ActivityDirection.this.map.addPolyline(lineOptions);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.menu_direction, menu);
        return true;
    }
}