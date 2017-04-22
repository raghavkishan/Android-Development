package com.example.raghavkishan.sdsuhometownchat;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class RegisterUser extends AppCompatActivity {

    EditText nickNameView,cityView,passwordView,emailIdView,passwordValueView,nickNameValueView;
    Spinner countrySpinner,stateSpinner,yearSpinner;
    ArrayList<String> countryList = new ArrayList<String>();
    ArrayList<String> stateList = new ArrayList<String>();
    String selectedCountry,selectedState,enteredPassword,enteredNickName,enteredCityName,enterdEmailId;
    private static final int INTENT_REQUEST_SET_LOCATION_USING_MAP_CODE = 12;
    public boolean hasfocuss,postOnBismarkSuccess;
    Double defaultValue=0.0,selectedLatitude=0.0,selectedLongitude=0.0;
    JSONObject data;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase dataBase;
    Person person;

    int selectedYear=1970;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        nickNameView = (EditText)findViewById(R.id.nick_name_value);
        cityView = (EditText)findViewById(R.id.city_value);
        passwordView = (EditText) findViewById(R.id.register_user_password_value);
        emailIdView = (EditText) findViewById(R.id.register_user_email_id_value);
        countrySpinner = (Spinner) findViewById(R.id.register_user_country_spinner);
        stateSpinner = (Spinner) findViewById(R.id.register_user_state_spinner);
        yearSpinner = (Spinner) findViewById(R.id.register_user_year_spinner);
        passwordValueView = (EditText)findViewById(R.id.register_user_password_value);
        nickNameValueView = (EditText)findViewById(R.id.nick_name_value);
        mAuth = FirebaseAuth.getInstance();

        yearSpinner();
        countrySpinner();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.i("assign5", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.i("assign5", "onAuthStateChanged:signed_out");
                }
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    public void yearSpinner() {
        ArrayList<String> years = new ArrayList<String>();
        for (int i = 1970; i <= 2017; i++) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, years);
        yearSpinner.setAdapter(adapter);

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Log.i("year spinner:", ""+position+"---"+id);
                selectedYear = selectedYear+position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Log.i("year spinner", "Nothing selected");
            }
        });
    }

    public void countrySpinner(){
        selectedCountry = "none";
        //Log.i("assign5","entered countyr spinner");
        String url = "http://bismarck.sdsu.edu/hometown/countries";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String[] eachCountry = response.replace("[","").replace("]","").replace("\"","").split(",");
                for (String each:eachCountry) {
                    countryList.add(each);
                }
                //Log.i("assign5","obtained country list"+countryList);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_spinner_dropdown_item,countryList);
                countrySpinner.setAdapter(adapter);

                countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //Log.i("country spinner:", ""+position+"---"+id);
                        selectedCountry = parent.getItemAtPosition(position).toString();
                        //Log.i("assign5","selected country in spinner:"+selectedCountry);
                        stateSpinner();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        //Log.i("country spinner", "Nothing selected");
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
        String url = "http://bismarck.sdsu.edu/hometown/states?country=" + selectedCountry;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String[] eachState = response.replace("[", "").replace("]", "").replace("\"", "").split(",");
                stateList.clear();
                for (String each : eachState) {
                    stateList.add(each);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, stateList);
                stateSpinner.setAdapter(adapter);

                stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        //Log.i("year spinner:", "" + position + "---" + id);
                        selectedState = parent.getItemAtPosition(position).toString();
                        //Log.i("before", "selected country in spinner:" + selectedState);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        //Log.i("state spinner", "Nothing selected");
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

    public void onClickLocationButton(View view){
        Intent go = new Intent(this,setLocationUsingMap.class);
        go.putExtra("Country",selectedCountry);
        go.putExtra("State",selectedState);
        startActivityForResult(go,INTENT_REQUEST_SET_LOCATION_USING_MAP_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == INTENT_REQUEST_SET_LOCATION_USING_MAP_CODE){
            switch (resultCode){

                case RESULT_OK:
                    selectedLatitude = data.getDoubleExtra("latitude",defaultValue);
                    selectedLongitude = data.getDoubleExtra("longitude",defaultValue);
                    //Log.i("before","latitude success:"+selectedLatitude+"longitude success"+selectedLongitude);
                    Toast.makeText(getBaseContext(), "Latitude: "+selectedLatitude+" Longitude: "+selectedLongitude, Toast.LENGTH_LONG).show();
                    break;
                case RESULT_CANCELED:
                    Toast.makeText(getBaseContext(), "You have not set the Location", Toast.LENGTH_LONG).show();

            }
        }
    }

    public void latitudeLongitudeCheckerAndSetter() throws IOException {
        Geocoder locator = new Geocoder(this);
        try {
            if (selectedLatitude == 0.0 || selectedLongitude == 0.0) {
                ArrayList<Address> address = (ArrayList<Address>) locator.getFromLocationName(selectedState + ", " + selectedCountry, 1);
                for (Address location : address) {
                    selectedLatitude = location.getLatitude();
                    selectedLongitude = location.getLongitude();
                    Toast.makeText(getBaseContext(), "Default value: Latitude: "+selectedLatitude+" Longitude: "+selectedLongitude, Toast.LENGTH_LONG).show();
                }
            }
        }catch(Exception error){
            Toast.makeText(getBaseContext(), "Error in latitude longitude checker", Toast.LENGTH_LONG).show();
        }
    }

    public void nickNameChecker(){
        enteredNickName = nickNameValueView.getText().toString();
        String url = "http://bismarck.sdsu.edu/hometown/nicknameexists?name="+enteredNickName;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!hasfocuss){
                    if (response.equalsIgnoreCase("true")){
                        nickNameValueView.setError("NickName Already Exists");
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        if (enteredNickName.contains(" ")){
            nickNameValueView.setError("NickName must not contain spaces");
        }
        singletonRequestQueue.instance(this).add(stringRequest);
    }

    public void passwordChecker(){
        enteredPassword = passwordValueView.getText().toString();
        if(enteredPassword.length() < 8)
        {
            passwordValueView.setError("Passwords must be at least three characters long");
        }
    }

    protected void onResume() {
        super.onResume();
        nickNameValueView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                hasfocuss = hasFocus;
                nickNameChecker();
            }
        });

        passwordValueView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                passwordChecker();
            }
        });

    }

    public void postToServer(View view){
        Log.i("before","going inside validation");
        enteredCityName = cityView.getText().toString();

        if(selectedLatitude == 0 && selectedLongitude ==0){
            try {
                latitudeLongitudeCheckerAndSetter();
            }catch (Exception e){

            }
        }

        String nickNameValidate = nickNameValueView.getText().toString();
        String passwordValidate = passwordValueView.getText().toString();
        String cityValidate = cityView.getText().toString();

        boolean validationFlag = valueValidation(nickNameValidate,passwordValidate,selectedCountry,selectedState,cityValidate);
        if(validationFlag) {
            data = new JSONObject();
            try {
                data.put("nickname", enteredNickName);
                data.put("city", enteredCityName);
                data.put("longitude", selectedLongitude);
                data.put("state", selectedState);
                data.put("year", selectedYear);
                data.put("latitude", selectedLatitude);
                data.put("country", selectedCountry);
                data.put("password", passwordValidate);
            } catch (JSONException error) {
                Log.e("rew", "JSON eorror", error);
            }

            Response.Listener<JSONObject> success = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(getBaseContext(), "Post Done", Toast.LENGTH_LONG).show();
                    Log.i("assign5", "post successful on bismark"+response.toString());
                    //postOnBismarkSuccess = true;
                    //Log.i("assign5", "post successful on bismark and postOnBismarkSuccess is set to true, "+postOnBismarkSuccess);
                    postToFirebaseServer();
                    selectedYear = 1970;
                }
            };

            Response.ErrorListener failure = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getBaseContext(), "Post fail", Toast.LENGTH_LONG).show();
                    Log.i("assign5", "post fail " + new String(error.networkResponse.data));
                }
            };

            String url = "http://bismarck.sdsu.edu/hometown/adduser";
            JsonObjectRequest postRequest = new JsonObjectRequest(url, data, success, failure);
            singletonRequestQueue.instance(this).add(postRequest);
        }

        /*if(postOnBismarkSuccess){
            Log.i("assign5", "Entering postToFirebaseServer()");
            postToFirebaseServer();
        }*/
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
        nickNameValueView.setText("");
        passwordValueView.setText("");
        cityView.setText("");
        emailIdView.setText("");
    }

    public void insertIntoAccount(){
        Log.i("assign5", "Entering after createAccount() before entering data to the database");
        dataBase = FirebaseDatabase.getInstance();
        DatabaseReference peopleTable = dataBase.getReference("people");

        person = new Person(enteredNickName,selectedCountry,selectedState,enteredCityName,enterdEmailId,selectedYear);

        //String personKey = peopleTable.push().getKey();
        peopleTable.child(enteredNickName).setValue(person);
        Toast.makeText(this, "post to FireBase server successful", Toast.LENGTH_SHORT).show();

        Intent go = new Intent(this,FilterView.class);
        startActivity(go);
    }

    private void postToFirebaseServer(){
        Log.i("assign5", "Entering createAccount()");
        enterdEmailId = emailIdView.getText().toString();
        enteredPassword = passwordValueView.getText().toString();
        mAuth.createUserWithEmailAndPassword(enterdEmailId,enteredPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.i("assign5", "createUserWithEmail:onComplete:" + task.isSuccessful());
                if(task.isSuccessful()){
                    FirebaseUser user = task.getResult().getUser();
                    final UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(enteredNickName).build();
                    user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RegisterUser.this,"User Registration Successful with display name!",Toast.LENGTH_LONG).show();
                                insertIntoAccount();
                            }
                        }
                    });
                }
                else
                if (!task.isSuccessful()) {
                    Toast.makeText(getBaseContext(), R.string.create_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
