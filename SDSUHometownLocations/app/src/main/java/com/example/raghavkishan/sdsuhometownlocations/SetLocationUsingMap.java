package com.example.raghavkishan.sdsuhometownlocations;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class SetLocationUsingMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public Marker mark;
    MarkerOptions options = new MarkerOptions();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_location_using_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        LatLng sanDiego = new LatLng(32.7, -117.1);
        options.position(sanDiego);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sanDiego));

        mark = mMap.addMarker(options);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.i("before","latitude in map:"+latLng.latitude+"longitude in map:"+latLng.longitude);
                mark.setPosition(new LatLng(latLng.latitude,latLng.longitude));

                Intent toPassBack = getIntent();
                toPassBack.putExtra("latitude",latLng.latitude);
                toPassBack.putExtra("longitude",latLng.longitude);
                setResult(RESULT_OK,toPassBack);
                finish();
            }
        });
    }
}
