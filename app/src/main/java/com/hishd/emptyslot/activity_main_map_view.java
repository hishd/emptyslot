package com.hishd.emptyslot;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.rubensousa.floatingtoolbar.FloatingToolbar;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.hishd.emptyslot.Util.AppConfig;

import java.util.Arrays;

import spencerstudios.com.bungeelib.Bungee;

public class activity_main_map_view extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    MarkerOptions markerOptions;

    Button btnfocus;
    LatLng unityPlaza;

    private ClusterManager<SlotMarkerItem> clusterManager;
    PlacesClient placesClient;

    AutocompleteSupportFragment autocompleteFragment;
    FloatingToolbar floatingToolbar;
    FloatingActionButton fab;

    //Location
    LocationManager locationManager;
    Criteria criteria;
    String locationProvider;
    Location location;
    LatLng latLng;
    MarkerOptions myLocationMarker;
    Marker marker;

    AppConfig appConfig;

    void setUpCluster() {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(unityPlaza, 17.5f));

        clusterManager = new ClusterManager<SlotMarkerItem>(this, mMap);
        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);

        addMarkers();
    }

    private void addMarkers() {
        double lat = 6.9063898;
        double lng = 79.8618613;

        for (int i = 0; i < 10; i++) {
            double offset = i / 60d;
            lat = lat + offset;
            lng = lng + offset;
            SlotMarkerItem offsetItem = new SlotMarkerItem(lat, lng);
            clusterManager.addItem(offsetItem);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map_view);

        appConfig = new AppConfig(this);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        unityPlaza = new LatLng(6.8935743, 79.8544723);


//        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
//                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
//
//
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                mMap.clear();
//                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()));
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12.0f));
//            }
//
//            @Override
//            public void onError(Status status) {
//
//            }
//        });

    }

    private void setListeners() {
        //Setting Listeners
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 18.0f));
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });

        btnfocus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(unityPlaza, 17.5f));
            }
        });

        floatingToolbar.setClickListener(new FloatingToolbar.ItemClickListener() {
            @Override
            public void onItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_my_account) {
                    startActivity(new Intent(activity_main_map_view.this, activity_account_settings.class));
                    Bungee.zoom(activity_main_map_view.this);
                }
            }

            @Override
            public void onItemLongClick(MenuItem item) {

            }
        });
    }

    private void initResources() {
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME));

        ((EditText)autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_input)).setHint("Search Destination");
        ((EditText)autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_input)).setTextSize(14.0f);

        btnfocus = findViewById(R.id.btnfocus);
        floatingToolbar = findViewById(R.id.floatingToolbar);
        fab = findViewById(R.id.fab);


        floatingToolbar.setMenu(R.menu.main);
        floatingToolbar.attachFab(fab);


        Places.initialize(getApplicationContext(), getResources().getString(R.string.maps_api_key));
        placesClient = Places.createClient(this);

        initMyLocationMarker();
    }

    private void initMyLocationMarker() {
        myLocationMarker = new MarkerOptions();
        myLocationMarker.draggable(false);
        myLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.drive));
        myLocationMarker.position(unityPlaza);

        marker = mMap.addMarker(myLocationMarker);
    }

    private void initLocationUpdates() {

        if (ContextCompat.checkSelfPermission(activity_main_map_view.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity_main_map_view.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(activity_main_map_view.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }else{
                ActivityCompat.requestPermissions(activity_main_map_view.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }else{
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            criteria = new Criteria();
            locationProvider = locationManager.getBestProvider(criteria, true);
            location = locationManager.getLastKnownLocation(locationProvider);

            if(location!=null){
                onLocationChanged(location);
            }

            locationManager.requestLocationUpdates(locationProvider,20000,0,activity_main_map_view.this);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1: {
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(activity_main_map_view.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
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

        // Add a marker in Sydney and move the camera
//        LatLng nibm = new LatLng(6.9063898, 79.8618613);
//
//        markerOptions = new MarkerOptions();
//        markerOptions.position(nibm).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_dot)).draggable(false);
//
//        mMap.addMarker(markerOptions);
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(nibm, 15.0f));

        setUpCluster();

        final CustomClusterRenderer renderer = new CustomClusterRenderer(this, mMap, clusterManager);

        clusterManager.setRenderer(renderer);

        //Init Resources

        initResources();

        //Set Listeners

        setListeners();

        initLocationUpdates();

    }

    @Override
    public void onLocationChanged(Location location) {
        latLng = new LatLng(location.getLatitude(),location.getLongitude());

        marker.remove();
        myLocationMarker.position(latLng);
        marker = mMap.addMarker(myLocationMarker);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.5f));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    public class CustomClusterRenderer extends DefaultClusterRenderer<SlotMarkerItem> {

        private final Context mContext;

        public CustomClusterRenderer(Context context, GoogleMap map,
                                     ClusterManager<SlotMarkerItem> clusterManager) {
            super(context, map, clusterManager);

            mContext = context;
        }

        @Override
        protected void onBeforeClusterItemRendered(SlotMarkerItem item, MarkerOptions markerOptions) {
            final BitmapDescriptor markerDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.marker_dot);

            markerOptions.icon(markerDescriptor).snippet(item.getTitle());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!appConfig.isUserLoggedIn()){
            startActivity(new Intent(activity_main_map_view.this,activity_login.class));
            Bungee.zoom(activity_main_map_view.this);
            finish();
            return;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if(!appConfig.isUserLoggedIn()){
            startActivity(new Intent(activity_main_map_view.this,activity_login.class));
            Bungee.zoom(activity_main_map_view.this);
            finish();
            return;
        }
    }
}
