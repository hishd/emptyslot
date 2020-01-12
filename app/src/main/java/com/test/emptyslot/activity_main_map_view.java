package com.test.emptyslot;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.rubensousa.floatingtoolbar.FloatingToolbar;
import com.github.rubensousa.floatingtoolbar.FloatingToolbarMenuBuilder;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.Arrays;

public class activity_main_map_view extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    MarkerOptions markerOptions;

    Button btnfocus;
    LatLng unityPlaza;

    private ClusterManager<SlotMarkerItem> clusterManager;
    PlacesClient placesClient;

    AutocompleteSupportFragment autocompleteFragment;
    FloatingToolbar floatingToolbar;
    FloatingActionButton fab;


    void setUpCluster(){
        unityPlaza = new LatLng(6.8935743, 79.8544723);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(unityPlaza, 17.5f));

        clusterManager = new ClusterManager<SlotMarkerItem>(this,mMap);
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


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME));

        btnfocus = findViewById(R.id.btnfocus);
        floatingToolbar = findViewById(R.id.floatingToolbar);
        fab = findViewById(R.id.fab);



        floatingToolbar.setMenu(R.menu.main);

        floatingToolbar.attachFab(fab);


        Places.initialize(getApplicationContext(),"AIzaSyDYGbLz0v0yFEOhio4dFZmt8G1q4frJ2d8");
        placesClient = Places.createClient(this);


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

}
