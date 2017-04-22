package com.example.raghavkishan.sdsuhometownchat;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class setLocationUsingMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public Marker mark;
    MarkerOptions options = new MarkerOptions();
    String selectedCountry,selectedState;
    LatLng countryStateLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_location_using_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        selectedCountry = getIntent().getStringExtra("Country");
        selectedState = getIntent().getStringExtra("State");

        countryStateLatLng = latitudeLongitudeCheckerAndSetter(selectedState,selectedCountry);

    }

    public LatLng latitudeLongitudeCheckerAndSetter(String state, String country){
        double latitude = 0.0;
        double longitude = 0.0;
        Geocoder locator = new Geocoder(getBaseContext());
        try {
            List<Address> address = locator.getFromLocationName(state + ", " + country, 1);
            for (Address location: address) {
                if (location.hasLatitude())
                    latitude = location.getLatitude();
                if (location.hasLongitude())
                    longitude = location.getLongitude();
            }
        } catch (Exception error) {
            Log.i("before","Error in latitude longitude checker");
        }
        LatLng stateLatLng = new LatLng(latitude, longitude);
        return stateLatLng;
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
        //LatLng sanDiego = new LatLng(32.7, -117.1);
        //options.position(sanDiego);
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sanDiego));

        //mark = mMap.addMarker(options);
        CameraUpdate classRoom = CameraUpdateFactory.newLatLngZoom(countryStateLatLng, 6);
        mMap.moveCamera(classRoom);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.i("before","latitude in map:"+latLng.latitude+"longitude in map:"+latLng.longitude);
                //mark.setPosition(new LatLng(latLng.latitude,latLng.longitude));
                LatLng selectedLocation = new LatLng(latLng.latitude, latLng.longitude);
                options.position(selectedLocation);
                mMap.addMarker(options);

                Intent toPassBack = getIntent();
                toPassBack.putExtra("latitude",latLng.latitude);
                toPassBack.putExtra("longitude",latLng.longitude);
                setResult(RESULT_OK,toPassBack);
                finish();
            }
        });
    }
}
