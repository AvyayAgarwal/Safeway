package com.example.safeway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;
import android.content.Context;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.gms.maps.model.LatLngBounds;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import android.location.LocationManager;
import android.util.Log;
import android.util.LogPrinter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private UiSettings mUiSettings;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private MarkerOptions place1, place2;
    Button getDirection, btnSOS;
    private Polyline currentPolyline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        getDirection = (Button) findViewById(R.id.btnGetDirection);
        getDirection.setOnClickListener(this);

        btnSOS = (Button) findViewById(R.id.btnSOS);
        btnSOS.setOnClickListener(this);

//        getDirection.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                EditText editText = (EditText) findViewById(R.id.plain_origin_input);
//                String originStr = editText.getText().toString();
//                editText = (EditText) findViewById(R.id.plain_destination_input);
//                String destinationStr = editText.getText().toString();
//                place1 = new MarkerOptions().position(getLocationFromAddress(MapsActivity.this, originStr)).title("Origin");
//                place2 = new MarkerOptions().position(getLocationFromAddress(MapsActivity.this, destinationStr)).title("Destination");
//                mMap.addMarker(place1);
//                mMap.addMarker(place2);
//
//                GoogleDirection.withServerKey(getString(R.string.google_maps_key))
//                        .from(place1.getPosition())
//                        .to(place2.getPosition())
//                        .transportMode(TransportMode.WALKING)
//                        .execute(new DirectionCallback() {
//                            @Override
//                            public void onDirectionSuccess(Direction direction, String rawBody) {
//                                // Do something here
//                                //Snackbar.make(getDirection, "Success with status : " + direction.getStatus(), Snackbar.LENGTH_SHORT).show();
//                                if (direction.isOK()) {
////                                    mMap.addMarker(new MarkerOptions().position(origin));
////                                    mMap.addMarker(new MarkerOptions().position(destination));
//
//                                    for (int i = 0; i < direction.getRouteList().size(); i++) {
//                                        Route route = direction.getRouteList().get(i);
//                                        //String color = colors[i % colors.length];
//                                        ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
//                                        mMap.addPolyline(DirectionConverter.createPolyline(MapsActivity.this, directionPositionList, 5, Color.BLUE));
//                                    }
//                                    setCameraWithCoordinationBounds(direction.getRouteList().get(0));
//
//                                    getDirection.setVisibility(View.GONE);
//                                }
//                            }
//
//                            @Override
//                            public void onDirectionFailure(Throwable t) {
//                                // Do something here
//                            }
//                        });
//
//            }
//        });

        //MapFragment mapFragment = (MapFragment) getFragmentManager()
        //      .findFragmentById(R.id.map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        mapFragment.getMapAsync(this);
    }

    private void setCameraWithCoordinationBounds(Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    public void sos(View view){
        System.out.println("REACHEDHERE*********************");
        Intent sosActivity = new Intent(getApplicationContext(), SOSActivity.class);
        startActivity(sosActivity);
    }

    private LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {

            ex.printStackTrace();
        }
        Log.d(TAG, p1.latitude + " ... " + p1.longitude);
        return p1;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMapToolbarEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setAllGesturesEnabled(true);
        mUiSettings.setMapToolbarEnabled(true);
        //isWriteStoragePermissionGranted();
        //isReadStoragePermissionGranted();
        checkLocationPermission();
        mUiSettings.setMyLocationButtonEnabled(true);
        //setUpClusterer();
        addHeatMap();


        // Add a marker in Toronto and move the camera
        LatLng toronto = new LatLng(43.651522, -79.4054883);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(toronto));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(13));

    }

    public  boolean checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted1");
                //setUpClusterer();
                return true;
            } else {

                Log.v(TAG,"Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted1");
            return true;
        }
    }

    public  boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted2");
                //setUpClusterer();
                return true;
            } else {

                Log.v(TAG,"Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted2");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2:
                Log.d(TAG, "External storage2");
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
                    //resume tasks needing this permission

                }else{
                }
                break;

            case 3:
                Log.d(TAG, "External storage1");
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
                    //resume tasks needing this permission

                }else{
                }
                break;
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnGetDirection:
                EditText editText = (EditText) findViewById(R.id.plain_origin_input);
                String originStr = editText.getText().toString();
                editText = (EditText) findViewById(R.id.plain_destination_input);
                String destinationStr = editText.getText().toString();
                place1 = new MarkerOptions().position(getLocationFromAddress(MapsActivity.this, originStr)).title("Origin");
                place2 = new MarkerOptions().position(getLocationFromAddress(MapsActivity.this, destinationStr)).title("Destination");
                mMap.addMarker(place1);
                mMap.addMarker(place2);

                GoogleDirection.withServerKey(getString(R.string.google_maps_key))
                        .from(place1.getPosition())
                        .to(place2.getPosition())
                        .transportMode(TransportMode.WALKING)
                        .execute(new DirectionCallback() {
                            @Override
                            public void onDirectionSuccess(Direction direction, String rawBody) {
                                if (direction.isOK()) {

                                    for (int i = 0; i < direction.getRouteList().size(); i++) {
                                        Route route = direction.getRouteList().get(i);
                                        //String color = colors[i % colors.length];
                                        ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                                        mMap.addPolyline(DirectionConverter.createPolyline(MapsActivity.this, directionPositionList, 5, Color.BLUE));
                                    }
                                    setCameraWithCoordinationBounds(direction.getRouteList().get(0));

                                    //getDirection.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onDirectionFailure(Throwable t) {
                                // Do something here
                            }
                        });

                break;

            case R.id.btnSOS:
                Intent sosActivity = new Intent(getApplicationContext(), SOSActivity.class);
                startActivity(sosActivity);
                break;

            default:
                break;
        }

    }


    private void addHeatMap() {
        List<LatLng> list = Collections.emptyList();
        // Get the data: latitude/longitude positions of police stations.
        try {
            list = readItems();
        } catch (JSONException e) {
            Toast.makeText(this, "Problem reading list of locations for heatmap", Toast.LENGTH_LONG).show();
        }

        int[] colors = {
                Color.YELLOW,
                Color.RED
        };

        float[] startPoints = {
                0.2f, 1f
        };

        Gradient gradient = new Gradient(colors, startPoints);
        // Create a heat map tile provider, passing it the latlngs of the police stations.
        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                .data(list)
                .gradient(gradient)
                .build();
        // Add a tile overlay to the map, using the heat map tile provider.
        TileOverlay mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }

    private ArrayList<LatLng> readItems() throws JSONException {
        ArrayList<LatLng> list = new ArrayList<LatLng>();

        JSONArray array = new JSONArray(loadJSONFromAsset());
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            double lat = object.getDouble("lat");
            double lng = object.getDouble("long");
            list.add(new LatLng(lat, lng));
        }
        return list;
    }

//    private ClusterManager<MyItem> mClusterManager;
//
//    private void setUpClusterer() {
//        Log.d(TAG, "000ddddebug");
//        // Position the map.
//        // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 10));
//
//        // Initialize the manager with the context and the map.
//        // (Activity extends context, so we can pass 'this' in the constructor.)
//        mClusterManager = new ClusterManager<MyItem>(this, mMap);
//
//        // Point the map's listeners at the listeners implemented by the cluster
//        // manager.
//        mMap.setOnCameraIdleListener(mClusterManager);
//        mMap.setOnMarkerClickListener(mClusterManager);
//
//        // Add cluster items (markers) to the cluster manager.
//        try {
//            addItems();
//        } catch (JSONException e) {
//            Toast.makeText(this, "Problem reading list of locations for marker clusterer", Toast.LENGTH_LONG).show();
//        }
//    }

//    private void addItems() throws JSONException {
//
////         Set some lat/lng coordinates to start with.
//        double lat = 51.5145160;
//        double lng = -0.1270060;
//
////        // Add ten cluster items in close proximity, for purposes of this example.
////        for (int i = 0; i < 10; i++) {
////            double offset = i / 60d;
////            lat = lat + offset;
////            lng = lng + offset;
////            MyItem offsetItem = new MyItem(lat, lng);
////            mClusterManager.addItem(offsetItem);
////        }
//
//        //InputStream inputStream = getResources().openRawResource(R.raw.crime_data);
//        //String json = new Scanner(inputStream).useDelimiter("\\A").next();
//        JSONArray array = new JSONArray(loadJSONFromAsset());
//        for (int i = 0; i < array.length(); i++) {
//            JSONObject object = array.getJSONObject(i);
//             lat = object.getDouble("lat");
//             lng = object.getDouble("long");
//            MyItem offsetItem = new MyItem(lat, lng);
//            mClusterManager.addItem(offsetItem);
//
//        }
//    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = this.getAssets().open("crime_data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            is.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        //Log.d(TAG, json + "debug");
        return json;
    }

//    public void getFireData() {
//        // Access a Cloud Firestore instance from your Activity
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        db.collection("points")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//                            }
//                        } else {
//                            Log.w(TAG, "Error getting documents.", task.getException());
//                        }
//                    }
//                });
//
//    }
}

