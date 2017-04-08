package com.example.raghavkishan.sdsuhometownlocations;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.io.IOException;
import java.util.ArrayList;

public class ViewUserFilterActivity extends AppCompatActivity {

    public static boolean countryFlagPass=false,stateFlagPass=false,noneFlagPass=false;
    Spinner spinYear,spinCountry,spinState;
    Integer selectedYear;
    double latitude,longitude;
    String selectedCountry,selectedState,url,selectedYearString;
    boolean yearFlag=false,stateFlag=false,countryFlag=false;
    public static
    ArrayList<String> countryList = new ArrayList<String>();
    ArrayList<String> stateList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_filter);
        //resetFlags();
        countrySpinner();
        yearSpinner();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void yearSpinner() {
        selectedYearString = "none";
        ArrayList<String> years = new ArrayList<String>();
        years.add("None");
        for (int i = 1970; i <= 2017; i++) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, years);
        spinYear = (Spinner) findViewById(R.id.view_user_year_spinner);
        spinYear.setAdapter(adapter);

        spinYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedYearString = parent.getItemAtPosition(position).toString();
                if(selectedYearString.equalsIgnoreCase("none")){
                    yearFlag = false;
                }
                else {
                    selectedYear = Integer.parseInt(selectedYearString);
                    Log.i("before", "selected year in spinner:" + selectedYear);
                    yearFlag = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("year spinner", "Nothing selected");
            }
        });
    }

    public void countrySpinner(){
        selectedCountry = "none";
        String url = "http://bismarck.sdsu.edu/hometown/countries";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String[] eachCountry = response.replace("[","").replace("]","").replace("\"","").split(",");
                countryList.add("None");
                for (String each:eachCountry) {
                    countryList.add(each);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_spinner_dropdown_item,countryList);
                spinCountry = (Spinner) findViewById(R.id.view_user_country_spinner);
                spinCountry.setAdapter(adapter);

                spinCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Log.i("country spinner:", ""+position+"---"+id);
                        selectedCountry = parent.getItemAtPosition(position).toString();
                        if(selectedCountry.equalsIgnoreCase("none")){
                            noneFlagPass = true;
                            stateFlagPass = false;
                            countryFlagPass = false;
                        }
                        else{
                            countryFlagPass = true;
                            noneFlagPass = false;
                            stateFlagPass = false;
                        }

                        if (selectedCountry.equalsIgnoreCase("none")){
                            countryFlag = false;
                            stateFlag = false;
                        }
                        else {
                            countryFlag = true;
                        }
                        Log.i("before","selected country in spinner:"+selectedCountry);
                        stateSpinner();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Log.i("country spinner", "Nothing selected");
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        singletonRequestQueue.instance(this).add(stringRequest);
    }

    public void stateSpinner(){
        selectedState = "none";
        if (selectedCountry.equalsIgnoreCase("None")){
            countryFlag = false;
            stateFlag = false;
            stateList.clear();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, stateList);
            spinState = (Spinner) findViewById(R.id.view_user_state_spinner);
            spinState.setAdapter(adapter);
        }else {
            String url = "http://bismarck.sdsu.edu/hometown/states?country=" + selectedCountry;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String[] eachState = response.replace("[", "").replace("]", "").replace("\"", "").split(",");
                    stateList.clear();
                    stateList.add("None");

                    for (String each : eachState) {
                        stateList.add(each);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, stateList);
                    spinState = (Spinner) findViewById(R.id.view_user_state_spinner);
                    spinState.setAdapter(adapter);

                    spinState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            Log.i("year spinner:", "" + position + "---" + id);
                            selectedState = parent.getItemAtPosition(position).toString();
                            if(selectedState.equalsIgnoreCase("none")){
                                countryFlagPass = true;
                                noneFlagPass = false;
                                stateFlagPass = false;
                            }
                            else
                            {
                                countryFlagPass = false;
                                stateFlagPass = true;
                                noneFlagPass = false;
                            }
                            if (selectedState.equalsIgnoreCase("none")){
                                if(selectedCountry.equalsIgnoreCase("none")){
                                    countryFlag = false;
                                    stateFlag = false;
                                }
                                else {
                                    countryFlag = true;
                                    stateFlag = false;
                                }
                            }
                            else {
                                countryFlag = true;
                                stateFlag = true;
                            }
                            Log.i("before", "selected country in spinner:" + selectedState);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            Log.i("state spinner", "Nothing selected");
                        }
                    });

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            singletonRequestQueue.instance(this).add(stringRequest);
        }
    }

    public void onListView(View view){

        selectedState = selectedState.replace(" ","%20");
        selectedCountry = selectedCountry.replace(" ","%20");

        if (!(yearFlag)&&!(stateFlag)&&!(countryFlag)){
            url="http://bismarck.sdsu.edu/hometown/users?reverse=true";
        }
        if((yearFlag)&&!(stateFlag)&&!(countryFlag)){
            url = "http://bismarck.sdsu.edu/hometown/users?reverse=true&year="+selectedYear;
        }
        if(!(yearFlag)&&(stateFlag)&&!(countryFlag)){
            url = "http://bismarck.sdsu.edu/hometown/users?reverse=true&state="+selectedState;
        }
        if(!(yearFlag)&&!(stateFlag)&&(countryFlag)){
            url = "http://bismarck.sdsu.edu/hometown/users?reverse=true&country="+selectedCountry;
        }
        if((yearFlag)&&(stateFlag)&&!(countryFlag)){
            url = "http://bismarck.sdsu.edu/hometown/users?reverse=true&year="+selectedYear+"&state="+selectedState;
        }
        if((yearFlag)&&!(stateFlag)&&(countryFlag)){
            url = "http://bismarck.sdsu.edu/hometown/users?reverse=true&year="+selectedYear+"&country="+selectedCountry;
        }
        if(!(yearFlag)&&(stateFlag)&&(countryFlag)){
            url = "http://bismarck.sdsu.edu/hometown/users?reverse=true&state="+selectedState+"&country="+selectedCountry;
        }
        if((yearFlag)&&(stateFlag)&&(countryFlag)){
            url = "http://bismarck.sdsu.edu/hometown/users?reverse=true&year="+selectedYear+"&state="+selectedState+"&country="+selectedCountry;
        }

        Intent go = new Intent(this,ViewListActivity.class);
        go.putExtra("url",url);
        startActivity(go);

    }

    public void resetFlags(){
        Log.i("before","flags reset");
        yearFlag = false;
        stateFlag = false;
        countryFlag = false;
    }

    public void onMapView(View view){

        selectedState = selectedState.replace(" ","%20");

        if (!(yearFlag)&&!(stateFlag)&&!(countryFlag)){
            url="http://bismarck.sdsu.edu/hometown/users?reverse=true";
        }
        if((yearFlag)&&!(stateFlag)&&!(countryFlag)){
            url = "http://bismarck.sdsu.edu/hometown/users?reverse=true&year="+selectedYear;
        }
        if(!(yearFlag)&&(stateFlag)&&!(countryFlag)){
            try{
                latitudeLongitudeGetter(selectedState,selectedCountry);
            }catch (Exception error){

            }
            url = "http://bismarck.sdsu.edu/hometown/users?reverse=true&state="+selectedState;
        }
        if(!(yearFlag)&&!(stateFlag)&&(countryFlag)){
            try{
                latitudeLongitudeGetter(selectedCountry);
                Log.i("before",""+latitude+" "+longitude);
            }catch (Exception error){

            }
            url = "http://bismarck.sdsu.edu/hometown/users?reverse=true&country="+selectedCountry;
        }
        if((yearFlag)&&(stateFlag)&&!(countryFlag)){
            try{
                latitudeLongitudeGetter(selectedState,selectedCountry);
            }catch (Exception error){

            }
            url = "http://bismarck.sdsu.edu/hometown/users?reverse=true&year="+selectedYear+"&state="+selectedState;
        }
        if((yearFlag)&&!(stateFlag)&&(countryFlag)){
            try{
                latitudeLongitudeGetter(selectedCountry);
            }catch (Exception error){

            }
            url = "http://bismarck.sdsu.edu/hometown/users?reverse=true&year="+selectedYear+"&country="+selectedCountry;
        }
        if(!(yearFlag)&&(stateFlag)&&(countryFlag)){
            try{
                latitudeLongitudeGetter(selectedState,selectedCountry);
            }catch (Exception error){

            }
            url = "http://bismarck.sdsu.edu/hometown/users?reverse=true&state="+selectedState+"&country="+selectedCountry;
        }
        if((yearFlag)&&(stateFlag)&&(countryFlag)){
            try{
                latitudeLongitudeGetter(selectedState,selectedCountry);
            }catch (Exception error){

            }
            url = "http://bismarck.sdsu.edu/hometown/users?reverse=true&year="+selectedYear+"&state="+selectedState+"&country="+selectedCountry;
        }

        Intent go = new Intent(this,ViewListMap.class);
        go.putExtra("url",url);
        go.putExtra("latitude",latitude);
        go.putExtra("longitude", longitude);
        startActivity(go);
    }


    public void latitudeLongitudeGetter(String country) throws IOException {
        Geocoder locator = new Geocoder(this);
        try {
                ArrayList<Address> address = (ArrayList<Address>) locator.getFromLocationName(country, 1);
                for (Address location : address) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
            }
        }catch(Exception error){
            Toast.makeText(getBaseContext(), "Error in latitude longitude checker", Toast.LENGTH_LONG).show();
        }
    }

    public void latitudeLongitudeGetter(String country,String state) throws IOException {
        Geocoder locator = new Geocoder(this);
        try {
                ArrayList<Address> address = (ArrayList<Address>) locator.getFromLocationName(state + ", " + country, 1);
                for (Address location : address) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
            }
        }catch(Exception error){
            Toast.makeText(getBaseContext(), "Error in latitude longitude checker", Toast.LENGTH_LONG).show();
        }
    }
}
