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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PostUser extends AppCompatActivity {

    EditText nickNameValue,passwordValue,cityValue;
    String nickName,nameExists,country,state,cityName,password;
    public boolean hasfocuss;
    int year=1970;
    JSONObject data;
    Button setCountryAndStateButton,setLocationOnMapButton;
    Double defaultValue=0.0,latitude=0.0,longitude=0.0;
    TextView countryView,stateView;

    private static final int INTENT_REQUEST_COUNTRY_STATE_CODE = 123;
    private static final int INTENT_REQUEST_SET_LOCATION_USING_MAP_CODE = 12;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_user);
        nickNameValue = (EditText)findViewById(R.id.nick_name_value);
        setCountryAndStateButton = (Button)findViewById(R.id.set_country_state_button);
        countryView = (TextView) findViewById(R.id.country_value);
        Log.i("before","inside oncreate between contryview and stateview"+country+" "+state);
        stateView = (TextView) findViewById(R.id.state_value);
        passwordValue = (EditText)findViewById(R.id.password_value);
        cityValue = (EditText)findViewById(R.id.city_value);
        //cityName = cityValue.getText().toString();
        //password = passwordValue.getText().toString();
        setLocationOnMapButton = (Button)findViewById(R.id.set_location_on_map_button);
        yearSpinner();
    }

    public void onClickSetCountryAndStateButton(View view){
        Intent go = new Intent(this,CountryStateActivity.class);
        startActivityForResult(go,INTENT_REQUEST_COUNTRY_STATE_CODE);
    }

    public void onClickLocationButton(View view){
        Intent go = new Intent(this,SetLocationUsingMap.class);
        startActivityForResult(go,INTENT_REQUEST_SET_LOCATION_USING_MAP_CODE);
    }

    public void yearSpinner() {
        ArrayList<String> years = new ArrayList<String>();
        for (int i = 1970; i <= 2017; i++) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, years);
        Spinner spinYear = (Spinner) findViewById(R.id.year_spinner);
        spinYear.setAdapter(adapter);

        spinYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("year spinner:", ""+position+"---"+id);
                year = year+position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("year spinner", "Nothing selected");
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        nickNameValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {;
                hasfocuss = hasFocus;
                nickNameChecker();
            }
        });

        passwordValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                passwordChecker();
            }
        });


    }

    public void nickNameChecker(){
        nickName = nickNameValue.getText().toString();
        String url = "http://bismarck.sdsu.edu/hometown/nicknameexists?name="+nickName;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                nameExists = response;
                if (!hasfocuss){
                    if (nameExists.equalsIgnoreCase("true")){
                        nickNameValue.setError("NickName Already Exists");
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        if (nickName.contains(" ")){
            nickNameValue.setError("NickName must not contain spaces");
        }
        singletonRequestQueue.instance(this).add(stringRequest);
    }

    public void passwordChecker(){

        password = passwordValue.getText().toString();
        if(password.length() < 3)
        {
            passwordValue.setError("Passwords must be at least three characters long");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == INTENT_REQUEST_COUNTRY_STATE_CODE){
            switch(resultCode){

                case RESULT_OK:
                    country = data.getStringExtra("countryname");
                    state = data.getStringExtra("statename");
                    countryView.setText(country);
                    stateView.setText(state);
                    break;
                case RESULT_CANCELED:
                    Toast.makeText(getBaseContext(), "You have not set the country and state!", Toast.LENGTH_LONG).show();

            }
        }

        if(requestCode == INTENT_REQUEST_SET_LOCATION_USING_MAP_CODE){
            switch (resultCode){

                case RESULT_OK:
                    latitude = data.getDoubleExtra("latitude",defaultValue);
                    longitude = data.getDoubleExtra("longitude",defaultValue);
                    Log.i("before","latitude success:"+latitude+"longitude success"+longitude);
                    Toast.makeText(getBaseContext(), "Latitude success: "+latitude+" Longitude success: "+longitude, Toast.LENGTH_LONG).show();
                    break;
                case RESULT_CANCELED:
                    Toast.makeText(getBaseContext(), "You have not set the Location", Toast.LENGTH_LONG).show();

            }
        }
    }

    public void latitudeLongitudeCheckerAndSetter() throws IOException{
        Geocoder locator = new Geocoder(this);
        try {
            if (latitude == 0.0 || longitude == 0.0) {
                ArrayList<Address> address = (ArrayList<Address>) locator.getFromLocationName(state + ", " + country, 1);
                for (Address location : address) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    Toast.makeText(getBaseContext(), "Default value: Latitude: "+latitude+" Longitude: "+longitude, Toast.LENGTH_LONG).show();
                }
            }
        }catch(Exception error){
            Toast.makeText(getBaseContext(), "Error in latitude longitude checker", Toast.LENGTH_LONG).show();
        }
    }

    public void postToServer(View view){
        Log.i("before","going inside validation");
        cityName = cityValue.getText().toString();

        if(latitude == 0 && longitude ==0){
            try {
                latitudeLongitudeCheckerAndSetter();
            }catch (Exception e){

            }
        }

        String nickNameValidate = nickNameValue.getText().toString();
        String passwordValidate = passwordValue.getText().toString();
        String countryValidate = countryView.getText().toString();
        String stateValidate = stateView.getText().toString();
        String cityValidate = cityValue.getText().toString();

        boolean validationFlag = valueValidation(nickNameValidate,passwordValidate,countryValidate,stateValidate,cityValidate);
        if(validationFlag) {
            data = new JSONObject();
            try {
                data.put("nickname", nickName);
                data.put("city", cityName);
                data.put("longitude", longitude);
                data.put("state", state);
                data.put("year", year);
                data.put("latitude", latitude);
                data.put("country", country);
                data.put("password", password);
            } catch (JSONException error) {
                Log.e("rew", "JSON eorror", error);
            }

            Response.Listener<JSONObject> success = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(getBaseContext(), "Post Done", Toast.LENGTH_LONG).show();
                    year = 1970;
                }
            };

            Response.ErrorListener failure = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getBaseContext(), "Post fail", Toast.LENGTH_LONG).show();
                    Log.i("rew", "post fail " + new String(error.networkResponse.data));
                }
            };

            String url = "http://bismarck.sdsu.edu/hometown/adduser";
            JsonObjectRequest postRequest = new JsonObjectRequest(url, data, success, failure);
            singletonRequestQueue.instance(this).add(postRequest);
        }
    }

    public boolean valueValidation(String nickName,String password,String country,String state,String cityName){
        boolean validationFlag = true;
        if (nickName.length()==0 || nickName.contains(" ")){
            validationFlag = false;
            Toast.makeText(getBaseContext(), "Please enter nickname/nickname must not contains spaces", Toast.LENGTH_SHORT).show();
        }
        if (password.length()<3){
            validationFlag = false;
            Toast.makeText(getBaseContext(), "Please enter password", Toast.LENGTH_SHORT).show();
        }
        if(country.length()==0){
            validationFlag = false;
            Toast.makeText(getBaseContext(), "Please select country", Toast.LENGTH_SHORT).show();
        }
        if(state.length()==0){
            validationFlag = false;
            Toast.makeText(getBaseContext(), "Please select state", Toast.LENGTH_SHORT).show();
        }
        Log.i("before","going inside validation cityName length "+cityName.length());
        if(cityName.length()==0){
            validationFlag = false;
            Toast.makeText(getBaseContext(), "Please select city", Toast.LENGTH_SHORT).show();
        }
        return validationFlag;
    }

    public void clearAllFields(View View){
        nickNameValue.setText("");
        passwordValue.setText("");
        cityValue.setText("");
        stateView.setText("");
        countryView.setText("");
    }
}
