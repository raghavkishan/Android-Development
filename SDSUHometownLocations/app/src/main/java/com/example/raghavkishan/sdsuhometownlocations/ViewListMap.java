package com.example.raghavkishan.sdsuhometownlocations;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.raghavkishan.sdsuhometownlocations.ViewUserFilterActivity.countryFlagPass;
import static com.example.raghavkishan.sdsuhometownlocations.ViewUserFilterActivity.noneFlagPass;
import static com.example.raghavkishan.sdsuhometownlocations.ViewUserFilterActivity.stateFlagPass;

public class ViewListMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String url,state,country,selectedCountry,selectedState;
    JSONObject JsonResponse,jsonNormal,jsonZero;
    JSONArray geoArray = new JSONArray();
    JSONArray normalArray = new JSONArray();
    double longitude,latitude,cameraLatitude,cameraLongitude,zeroLatitude,zeroLongitude;
    CameraUpdate cameraUpdt,cameraUptZero;
    ArrayList userList = new ArrayList();
    MarkerOptions options = new MarkerOptions();
    public Marker mark;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        url = getIntent().getStringExtra("url");
        cameraLatitude = getIntent().getDoubleExtra("latitude",0.0);
        cameraLongitude = getIntent().getDoubleExtra("longitude",0.0);
    }

    public void getData(){

        if (noneFlagPass){
            LatLng cameraLatLng = new LatLng(0.0,0.0);
            cameraUptZero = CameraUpdateFactory.newLatLngZoom(cameraLatLng,1);
            mMap.moveCamera(cameraUptZero);
        }else if(stateFlagPass){
            LatLng cameraLatLng = new LatLng(cameraLatitude, cameraLongitude);
            cameraUpdt = CameraUpdateFactory.newLatLngZoom(cameraLatLng, 5);
            mMap.moveCamera(cameraUpdt);
        }else if(countryFlagPass){

            LatLng cameraLatLng = new LatLng(cameraLatitude,cameraLongitude);
            cameraUpdt = CameraUpdateFactory.newLatLngZoom(cameraLatLng,3);
            mMap.moveCamera(cameraUpdt);
        }


        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                for (int i=0;i<response.length();i++) {
                    try {
                        JsonResponse = (JSONObject) response.get(i);
                        latitude = JsonResponse.getDouble("latitude");
                        longitude = JsonResponse.getDouble("longitude");
                        if (latitude == 0.0 && longitude == 00){
                            geoArray.put(JsonResponse);
                        }
                        else{
                            normalArray.put(JsonResponse);
                        }
                    }catch (JSONException error){
                        error.printStackTrace();
                    }
                }

                for (int i =0;i<normalArray.length();i++){
                    try {
                        jsonNormal = (JSONObject) normalArray.get(i);
                        latitude = jsonNormal.getDouble("latitude");
                        longitude = jsonNormal.getDouble("longitude");
                        LatLng location = new LatLng(latitude, longitude);
                        options.position(location).title("Name: " + jsonNormal.getString("nickname"));
                        mMap.addMarker(options);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                for (int i =0;i<geoArray.length();i++){
                    try {
                        jsonZero = (JSONObject) geoArray.get(i);
                        latitude = jsonZero.getDouble("latitude");
                        longitude = jsonZero.getDouble("longitude");
                        doAsyncTask(jsonZero.getString("state"),jsonZero.getString("country"),jsonZero.getString("nickname"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                Log.d("rew", response.toString());
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.d("rew", error.toString());
            }
        };
        JsonArrayRequest getRequest = new JsonArrayRequest(url, success, failure);
        singletonRequestQueue.instance(this).add(getRequest);

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
        mMap.clear();
        getData();
    }


    public void doAsyncTask(String country,String state,String name){
        String[] array = {country,state,name};
        new SampleTask().execute(array);
    }

    class SampleTask extends AsyncTask<String,String,LatLng>{

        public LatLng latitudeLongitudeCheckerAndSetter(String state, String country  ){
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

        String userQuery;

        public LatLng doInBackground(String... word){
            LatLng location;
                location = latitudeLongitudeCheckerAndSetter(word[0],word[1]);
                userQuery = word[2];
                return (location);
        }

        public void onPostExecute(LatLng location){
            mMap.addMarker(options.position(location).title("Name:"+ userQuery));
        }
    }


}
