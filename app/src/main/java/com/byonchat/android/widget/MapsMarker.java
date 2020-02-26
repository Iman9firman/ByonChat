package com.byonchat.android.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;

import com.byonchat.android.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MapsMarker extends FragmentActivity implements OnMapReadyCallback {

    public static final String LATITUDE = "extra_latitude";
    public static final String LONGITUDE = "extra_longitude";
    public static final String SHORTADDRESS = "extra_shortaddress";
    public static final String FULLADDRESS = "extra_fulladdress";
    public static final int RESULT_CODE = 627;

    private ImageView imgCurLoc;
    private CardView cardView;
    private TextView placeNameTextView;
    private TextView placeAddressTextView;
    private TextView placeCoordinatesTextView;
    private ProgressBar placeProgressBar;
    private GoogleMap mMap;
    private String shortAddress = "";
    private String fullAddress = "";
    ImageView markerImage;
    double latitude, longitude;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private LocationCallback locationCallback;
    String locationGot;
    //    String apiKeyPlaces = "AIzaSyAhOmhz7BjEXDkuHEfj1oTkdq4ZTiK3wx8";
    String apiKeyPlaces = "AIzaSyCtfNNv951dLHJ-VllJN_bDPEF3yiQqDI0";
    View mapView;
    PlacesClient placesClient;
    double addressLongitude, addressLatitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_marker);

        imgCurLoc = findViewById(R.id.imgCurLoc);
        cardView = findViewById(R.id.cardView);
        markerImage = findViewById(R.id.marker_image_view);
        placeNameTextView = findViewById(R.id.text_view_place_name);
        placeAddressTextView = findViewById(R.id.text_view_place_address);
        placeCoordinatesTextView = findViewById(R.id.text_view_place_coordinates);
        placeProgressBar = findViewById(R.id.progress_bar_place);


        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsMarker.this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (!Places.isInitialized()) {
            Places.initialize(this, apiKeyPlaces);
        }

        placesClient = Places.createClient(this);

        setupAutoComplete();
        if (isConnectedToInternet(this)) {
            isGPSEnabled();
        } else if (!isConnectedToInternet(this)) {
            Toast.makeText(this, "No internet Connection", Toast.LENGTH_SHORT).show();
        }

        cardView.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(LATITUDE, latitude);
            resultIntent.putExtra(LONGITUDE, longitude);
            resultIntent.putExtra(SHORTADDRESS, shortAddress);
            resultIntent.putExtra(FULLADDRESS, fullAddress);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });


        imgCurLoc.setOnClickListener(v -> {
            isGPSEnabled();
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (isConnectedToInternet(this)) {
            mMap = googleMap;
            mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
                @Override
                public void onCameraMoveStarted(int i) {
                    showLoadingBottomDetails();

//                    if (markerImage.getTranslationY() == 5f) {
                    markerImage.animate()
                            .translationY(-20f)
                            .translationX(0f)
                            .setDuration(250)
                            .start();
//                    }
                }
            });


            mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                @Override
                public void onCameraIdle() {

                    markerImage.animate()
                            .translationY(13f)
                            .translationX(0f)
                            .setDuration(250)
                            .start();

                    LatLng latLng = mMap.getCameraPosition().target;
                    latitude = latLng.latitude;
                    longitude = latLng.longitude;

                    getAddress(latitude, longitude);

                    setPlaceDetails(latitude, longitude, shortAddress, fullAddress);

//                    Toast.makeText(MapsMarker.this, "" + getAddress(latitude, longitude), Toast.LENGTH_SHORT).show();


                }
            });
        } else if (!isConnectedToInternet(this)) {
            Toast.makeText(this, "No internet Connection", Toast.LENGTH_SHORT).show();
        }


    }


    // this is th function for converting the locatio latlng to address


    private String getAddress(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                result.append(address.getAddressLine(0)).append(" ");

                fullAddress = result.toString();
                Log.w("Garmin", fullAddress);
                shortAddress = generateFinalAddress(fullAddress).trim();
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        return result.toString();
    }


    private String generateFinalAddress(
            String address) {
        String[] s;
        s = address.split(",");
        if (s.length >= 3) {
            return s[1] + "," + s[2];
        } else if (s.length == 2) {

            return s[1];
        } else
            return s[0];


    }

    //this is the function for setting up the search bar

    public void setupAutoComplete() {
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);


        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                locationGot = place.getName();
                addMarker(place);

            }

            @Override
            public void onError(Status status) {
            }
        });

    }


    //function for getting user location


    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {


                                locationGot = getAddress(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), 18));
                            } else {
                                final LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval(10000);
                                locationRequest.setFastestInterval(5000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationCallback = new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        if (locationResult == null) {
                                            return;
                                        }
                                        mLastKnownLocation = locationResult.getLastLocation();
                                        locationGot = getAddress(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), 18));
                                        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                    }
                                };


                                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

                            }
                        } else {
                            Toast.makeText(MapsMarker.this, "unable to get last location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    //function for checking if the gps is enabled or not

    private void isGPSEnabled() {
        LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = false;
        if (manager != null) {
            statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        if (statusOfGPS) {

            getDeviceLocation();

        } else {


            new AlertDialog.Builder(this)
                    .setTitle("GPS NOT ENABLED")  // GPS not found
                    .setMessage("Wants to Enable location services to provide you Water service at your DoorStep.")
                    .setPositiveButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(MapsMarker.this, "Enalbe Location From Settings Or Restart App To Give Your Exact Address", Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            MapsMarker.this.startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1000);

                        }
                    }).setCancelable(false)
                    .show();
        }
    }

    //function for adding the marker once the user select the location from search baar

    public void addMarker(Place p) {
        if (p.getLatLng() != null) {
            addressLatitude = p.getLatLng().latitude;
            addressLongitude = p.getLatLng().longitude;
            mMap.clear();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(p.getLatLng(), 18));
        }

    }


    //function to show the location details

    private void showLoadingBottomDetails() {
        cardView.setClickable(false);
        placeNameTextView.setText("");
        placeAddressTextView.setText("");
        placeCoordinatesTextView.setText("");
        placeProgressBar.setVisibility(View.VISIBLE);
    }

    private void setPlaceDetails(double latitude, double longitude, String shortAddress, String fullAddress) {

        if (latitude == -1.0 || longitude == -1.0) {
            cardView.setClickable(false);
            placeNameTextView.setText("");
            placeAddressTextView.setText("");
            placeProgressBar.setVisibility(View.VISIBLE);
            return;
        }
        cardView.setClickable(true);
        placeProgressBar.setVisibility(View.GONE);
        if (shortAddress.isEmpty()) {
            placeNameTextView.setText("Dropped Pin");
        } else {
            placeNameTextView.setText(shortAddress);
        }
        placeAddressTextView.setText(fullAddress);
        placeCoordinatesTextView.setText(Location.convert(latitude, Location.FORMAT_DEGREES) + ", " + Location.convert(longitude, Location.FORMAT_DEGREES));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1000) {

            isGPSEnabled();

        }

    }

    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null)
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
        }
        return false;
    }

}




