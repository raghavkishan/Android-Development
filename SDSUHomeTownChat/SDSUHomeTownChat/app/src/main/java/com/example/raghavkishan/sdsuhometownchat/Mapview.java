package com.example.raghavkishan.sdsuhometownchat;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.raghavkishan.sdsuhometownchat.FilterView.countryFlagPass;
import static com.example.raghavkishan.sdsuhometownchat.FilterView.stateFlagPass;
import static com.example.raghavkishan.sdsuhometownchat.FilterView.noneFlagPass;
import static com.example.raghavkishan.sdsuhometownchat.FilterView.yearFlag;
import static com.example.raghavkishan.sdsuhometownchat.FilterView.stateFlag;
import static com.example.raghavkishan.sdsuhometownchat.FilterView.countryFlag;
import static com.example.raghavkishan.sdsuhometownchat.FilterView.selectedYear;
import static com.example.raghavkishan.sdsuhometownchat.FilterView.selectedState;
import static com.example.raghavkishan.sdsuhometownchat.FilterView.selectedCountry;



public class Mapview extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    String unEqualBeginUrl,sqlNickName,sqlCountry,sqlState,sqlCity,currentUserLoggedIn;
    double cameraLatitude,cameraLongitude,sqlLatitude,sqlLongitude;
    int checkNextId,page=0,offset=0,tempNextId,sqlId,sqlYear,beforeId,pageE = 0;
    DatabaseHelper peopleHelper;
    SQLiteDatabase database,databaseRead;
    Boolean unEqualFlag = false,equalFlag = false;
    String url,unEqualurl,equalQueryStmt,equalUrl;
    JSONObject JsonResponse;
    MarkerOptions options = new MarkerOptions();
    CameraUpdate cameraUpdt,cameraUptZero;
    SupportMapFragment mapFragment;
    FirebaseAuth authenticationInstance;
    FirebaseDatabase firebaseDataBase;
    ArrayList<String> firebaseUsers = new ArrayList<String>();
    TextView currentUserTextViewDisp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapview);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapViewFrag);
        //checkerNextId();
        mapFragment.getMapAsync(this);
        peopleHelper = (new DatabaseHelper(this));
        database = peopleHelper.getWritableDatabase();
        databaseRead = peopleHelper.getReadableDatabase();

        currentUserTextViewDisp = (TextView) findViewById(R.id.map_view_current_user_value);

        authenticationInstance = FirebaseAuth.getInstance();
        currentUserLoggedIn = authenticationInstance.getCurrentUser().getDisplayName();
        currentUserTextViewDisp.setText(currentUserLoggedIn);

        firebaseDataBase = FirebaseDatabase.getInstance();
        DatabaseReference firebaseDatabaseReference = firebaseDataBase.getReference();

        firebaseDatabaseReference.child("people").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Iterable<DataSnapshot> childNodes = dataSnapshot.getChildren();
                for (DataSnapshot child : childNodes){
                    firebaseUsers.add(child.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_menu, menu);
        return true;

    }

    public void addUsers(View view){
        if (unEqualFlag==true){
            getFurtherDataNextId();
        }
        if (equalFlag == true){
            getRecords();
        }
    }

    public void checkerNextId() {
        String url = "http://bismarck.sdsu.edu/hometown/nextid";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                checkNextId = Integer.parseInt(response);
                Log.i("assign5","map checkNextId has been assigned "+ checkNextId);
                intialChecker();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        singletonRequestQueue.instance(this).add(stringRequest);
    }

    public void intialChecker(){
        page = 0;
        Cursor checkResult = databaseRead.rawQuery("select coalesce(max(id),0) from PEOPLE",null);
        checkResult.moveToFirst();
        int tableMaxId = checkResult.getInt(0);
        if (checkNextId-1 == tableMaxId){
            Log.i("assign5","map equal match"+tableMaxId+" "+(checkNextId-1));
            equalFlag = true;
            unEqualFlag = false;
            equalMatch();
        }
        else {
            Log.i("assign5","map un equal match"+tableMaxId+" "+(checkNextId-1));
            unEqualFlag = true;
            equalFlag = false;
            unequalMatch();

        }
    }

    public void equalMatch(){
        offset = 0;
        //equalFlag = true;
        getRecords();

    }

    public void unequalMatch(){
        //url = unEqualBeginUrl;
        getdata();
    }

    public void getdata(){
        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                for (int i=0;i<response.length();i++) {
                    try {
                        JsonResponse = (JSONObject) response.get(i);
                        int id = JsonResponse.getInt("id");
                        int year = JsonResponse.getInt("year");
                        String nickName = JsonResponse.getString("nickname");
                        String country = JsonResponse.getString("country");
                        String state = JsonResponse.getString("state");
                        String city = JsonResponse.getString("city");
                        String timeStamp = JsonResponse.getString("time-stamp");
                        double latitude = JsonResponse.getDouble("latitude");
                        double longitude = JsonResponse.getDouble("longitude");
                        insertIntoSqlDb(id,nickName,city,longitude,state,year,latitude,timeStamp,country);
                        if(latitude != 0.0 && longitude != 0.0)
                        {
                            LatLng location = new LatLng(latitude, longitude);
                            options.position(location).title(nickName);
                            mMap.addMarker(options);
                        }
                        else
                        {
                            Log.i("assign5","before entering gecoder getFurtherData nickname ="+nickName);
                            doAsyncTask(state,country,nickName);
                        }
                    }catch (JSONException error){
                        error.printStackTrace();
                    }
                }
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.i("assign5", error.toString());
            }
        };
        JsonArrayRequest getRequest = new JsonArrayRequest(unEqualBeginUrl, success, failure);
        singletonRequestQueue.instance(this).add(getRequest);
    }

    public void insertIntoSqlDb(int id,String nickname,String city,double longitude,String state,int year,double latitude,String timeStamp,String country){
        Log.i("assign5"," inside insertIntoSqlDb "+id);
        ContentValues contentValues = new ContentValues();
        contentValues.put("id",id);
        contentValues.put("nickname",nickname);
        contentValues.put("city",city);
        contentValues.put("longitude",longitude);
        contentValues.put("state",state);
        contentValues.put("year",year);
        contentValues.put("latitude",latitude);
        contentValues.put("timestamp",timeStamp);
        contentValues.put("country",country);
        database.insertWithOnConflict("PEOPLE",null,contentValues,database.CONFLICT_IGNORE);
    }

    public void getFurtherDataNextId() {
        String url = "http://bismarck.sdsu.edu/hometown/nextid";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                tempNextId = Integer.parseInt(response);
                getFurtherData();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        singletonRequestQueue.instance(this).add(stringRequest);
    }


    public void getFurtherData(){
        page = page + 1;
        unEqualSetter();
        String url = unEqualurl;
        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                for (int i=0;i<response.length();i++) {
                    try {
                        JsonResponse = (JSONObject) response.get(i);
                        int id = JsonResponse.getInt("id");
                        int year = JsonResponse.getInt("year");
                        String nickName = JsonResponse.getString("nickname");
                        String country = JsonResponse.getString("country");
                        String state = JsonResponse.getString("state");
                        String city = JsonResponse.getString("city");
                        String timeStamp = JsonResponse.getString("time-stamp");
                        double latitude = JsonResponse.getDouble("latitude");
                        double longitude = JsonResponse.getDouble("longitude");
                        insertIntoSqlDb(id,nickName,city,longitude,state,year,latitude,timeStamp,country);
                        if(latitude != 0.0 && longitude != 0.0)
                        {
                            LatLng location = new LatLng(latitude, longitude);
                            options.position(location).title(nickName);
                            mMap.addMarker(options);
                        }
                        else
                        {
                            Log.i("assign5","before entering gecoder getFurtherDataEqual nickname ="+nickName);
                            doAsyncTask(state,country,nickName);
                        }
                    }catch (JSONException error){
                        error.printStackTrace();
                    }
                }
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.i("assign5", error.toString());
            }
        };
        JsonArrayRequest getRequest = new JsonArrayRequest(url, success, failure);
        singletonRequestQueue.instance(this).add(getRequest);
    }


    //-------------------------------------------------------------------------------------------//

    public void getRecords(){
        equalQuerySetter();
        Cursor result = databaseRead.rawQuery(equalQueryStmt,null);//"select * from PEOPLE where country = \"Canada\" ORDER BY id DESC limit 25 OFFSET "+offset
        Log.i("assign5","result in getrecords"+result.getCount());
        if (result.getCount()< 100) {
            while(result.moveToNext()){
                sqlId = result.getInt(0);
                sqlYear = result.getInt(5);
                sqlNickName = result.getString(1);
                sqlCountry = result.getString(8);
                sqlState = result.getString(4);
                sqlCity = result.getString(2);
                sqlLatitude = result.getDouble(6);
                sqlLongitude = result.getDouble(3);
                Log.i("assign5","map getRecords before adding markers <100 "+sqlLatitude+" "+sqlLongitude+" "+sqlNickName);
                LatLng location = new LatLng(sqlLatitude, sqlLongitude);
                options.position(location).title(sqlNickName);
                mMap.addMarker(options);
                beforeId = result.getInt(0);
            }
            getFurtherDataEqual();
        }
        else
        {
            while(result.moveToNext()) {
                sqlId = result.getInt(0);
                sqlYear = result.getInt(5);
                sqlNickName = result.getString(1);
                sqlCountry = result.getString(8);
                sqlState = result.getString(4);
                sqlCity = result.getString(2);
                sqlLatitude = result.getDouble(6);
                sqlLongitude = result.getDouble(3);
                Log.i("assign5","map getRecords before adding markers "+sqlLatitude+" "+sqlLongitude+" "+sqlNickName);
                LatLng location = new LatLng(sqlLatitude, sqlLongitude);
                options.position(location).title(sqlNickName);
                mMap.addMarker(options);
                beforeId = result.getInt(0);
            }
        }
        Log.i("assign5","map result in getrecords after reading"+result.getCount());
        offset = offset + 100;
    }

    public void getFurtherDataEqual(){
        pageE = 0;
        equalUrlSetter();
        Log.i("assign5","In getFurtherDataEqual after page is incremented "+pageE+" and before id "+beforeId);
        String url = equalUrl;//"http://bismarck.sdsu.edu/hometown/users?page="+pageE+"&reverse=true&beforeid="+beforeId+"&country=Canada";
        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                for (int i=0;i<response.length();i++) {
                    try {
                        JsonResponse = (JSONObject) response.get(i);
                        int id = JsonResponse.getInt("id");
                        int year = JsonResponse.getInt("year");
                        String nickName = JsonResponse.getString("nickname");
                        String country = JsonResponse.getString("country");
                        String state = JsonResponse.getString("state");
                        String city = JsonResponse.getString("city");
                        String timeStamp = JsonResponse.getString("time-stamp");
                        double latitude = JsonResponse.getDouble("latitude");
                        double longitude = JsonResponse.getDouble("longitude");
                        insertIntoSqlDb(id,nickName,city,longitude,state,year,latitude,timeStamp,country);
                        beforeId = JsonResponse.getInt("id");
                        if(latitude != 0.0 && longitude != 0.0)
                        {
                            LatLng location = new LatLng(latitude, longitude);
                            options.position(location).title(nickName);
                            mMap.addMarker(options);
                        }
                        else
                        {
                            Log.i("assign5","before entering gecoder getFurtherData nickname ="+nickName);
                            doAsyncTask(state,country,nickName);
                        }
                    }catch (JSONException error){
                        error.printStackTrace();
                    }
                }
                Log.i("assign5","response of in get further data Equal "+page+" response length "+response.length());
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.i("assign5", error.toString());
            }
        };
        JsonArrayRequest getRequest = new JsonArrayRequest(url, success, failure);
        singletonRequestQueue.instance(this).add(getRequest);
        //pageE = pageE + 1;
    }

    //-------------------------------------------------------------------------------------------------------------------------------//



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
        mMap.setOnInfoWindowClickListener(this);
        mMap.clear();

        unEqualBeginUrl = getIntent().getStringExtra("unEqualBeginUrl");
        cameraLatitude = getIntent().getDoubleExtra("latitude",0.0);
        cameraLongitude = getIntent().getDoubleExtra("longitude",0.0);

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
        checkerNextId();
        //intialChecker();
        //getRecords();


    }


    //----------------------------------------------------------------------------------------------------------------

    public void doAsyncTask(String country,String state,String name){
        String[] array = {country,state,name};
        new SampleTask().execute(array);
    }

    class SampleTask extends AsyncTask<String,String,LatLng> {

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
            mMap.addMarker(options.position(location).title(userQuery));
            ContentValues args = new ContentValues();
            args.put("latitude",location.latitude);
            args.put("longitude",location.longitude);
            String[] argName = new String[]{userQuery};
            database.update("PEOPLE",args,"nickname=?",argName);
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------


    public void unEqualSetter(){
        Log.i("assign5","inside unEqualSetter");
        if (!(yearFlag)&&!(stateFlag)&&!(countryFlag)){
            unEqualurl="http://bismarck.sdsu.edu/hometown/users?page="+page+"&pagesize=100&reverse=true&beforeid="+tempNextId;
        }
        if((yearFlag)&&!(stateFlag)&&!(countryFlag)){
            unEqualurl = "http://bismarck.sdsu.edu/hometown/users?page="+page+"&pagesize=100&reverse=true&beforeid="+tempNextId+"&year="+selectedYear;
        }
        if(!(yearFlag)&&(stateFlag)&&!(countryFlag)){
            unEqualurl= "http://bismarck.sdsu.edu/hometown/users?page="+page+"&pagesize=100&reverse=true&beforeid="+tempNextId+"&state="+selectedState;
        }
        if(!(yearFlag)&&!(stateFlag)&&(countryFlag)){
            unEqualurl= "http://bismarck.sdsu.edu/hometown/users?page="+page+"&pagesize=100&reverse=true&beforeid="+tempNextId+"&country="+selectedCountry;
        }
        if((yearFlag)&&(stateFlag)&&!(countryFlag)){
            unEqualurl= "http://bismarck.sdsu.edu/hometown/users?page="+page+"&pagesize=100&reverse=true&beforeid="+tempNextId+"&year="+selectedYear+"&state="+selectedState;
        }
        if((yearFlag)&&!(stateFlag)&&(countryFlag)){
            Log.i("assign5","inside country flag settter only"+selectedCountry);
            unEqualurl= "http://bismarck.sdsu.edu/hometown/users?page="+page+"&pagesize=100&reverse=true&beforeid="+tempNextId+"&year="+selectedYear+"&country="+selectedCountry;
        }
        if(!(yearFlag)&&(stateFlag)&&(countryFlag)){
            unEqualurl= "http://bismarck.sdsu.edu/hometown/users?page="+page+"&pagesize=100&reverse=true&beforeid="+tempNextId+"&state="+selectedState+"&country="+selectedCountry;
        }
        if((yearFlag)&&(stateFlag)&&(countryFlag)){
            unEqualurl= "http://bismarck.sdsu.edu/hometown/users?page="+page+"&pagesize=100&reverse=true&beforeid="+tempNextId+"&year="+selectedYear+"&state="+selectedState+"&country="+selectedCountry;
        }
    }

    public void equalQuerySetter(){
        if (!(yearFlag)&&!(stateFlag)&&!(countryFlag)){
            equalQueryStmt = "select * from PEOPLE ORDER BY id DESC limit 100 OFFSET "+offset;
        }
        if((yearFlag)&&!(stateFlag)&&!(countryFlag)){
            equalQueryStmt = "select * from PEOPLE where year = \""+selectedYear+"\" ORDER BY id DESC limit 100 OFFSET "+offset;
        }
        if(!(yearFlag)&&(stateFlag)&&!(countryFlag)){
            equalQueryStmt = "select * from PEOPLE where state = \""+selectedState+"\" ORDER BY id DESC limit 100 OFFSET "+offset;
        }
        if(!(yearFlag)&&!(stateFlag)&&(countryFlag)){
            equalQueryStmt = "select * from PEOPLE where country = \""+selectedCountry+"\" ORDER BY id DESC limit 100 OFFSET "+offset;
        }
        if((yearFlag)&&(stateFlag)&&!(countryFlag)){
            equalQueryStmt = "select * from PEOPLE where year = "+selectedYear+" and state = \""+selectedState+"\" ORDER BY id DESC limit 100 OFFSET "+offset;
        }
        if((yearFlag)&&!(stateFlag)&&(countryFlag)){
            equalQueryStmt = "select * from PEOPLE where year = "+selectedYear+" and country = \""+selectedCountry+"\" ORDER BY id DESC limit 100 OFFSET "+offset;
        }
        if(!(yearFlag)&&(stateFlag)&&(countryFlag)){
            equalQueryStmt = "select * from PEOPLE where state = \""+selectedState+"\" and country = \""+selectedCountry+"\" ORDER BY id DESC limit 100 OFFSET "+offset;
        }
        if((yearFlag)&&(stateFlag)&&(countryFlag)){
            equalQueryStmt = "select * from PEOPLE where year = "+selectedYear+" and state = \""+selectedState+"\" and country = \""+selectedCountry+"\" ORDER BY id DESC limit 100 OFFSET "+offset;
        }
    }

    public void equalUrlSetter(){
        if (!(yearFlag)&&!(stateFlag)&&!(countryFlag)){
            equalUrl = "http://bismarck.sdsu.edu/hometown/users?page="+pageE+"&pagesize=100&reverse=true&beforeid="+beforeId;
        }
        if((yearFlag)&&!(stateFlag)&&!(countryFlag)){
            equalUrl = "http://bismarck.sdsu.edu/hometown/users?page="+pageE+"&pagesize=100&reverse=true&beforeid="+beforeId+"&year="+selectedYear;
        }
        if(!(yearFlag)&&(stateFlag)&&!(countryFlag)){
            equalUrl = "http://bismarck.sdsu.edu/hometown/users?page="+pageE+"&pagesize=100&reverse=true&beforeid="+beforeId+"&state="+selectedState;
        }
        if(!(yearFlag)&&!(stateFlag)&&(countryFlag)){
            equalUrl = "http://bismarck.sdsu.edu/hometown/users?page="+pageE+"&pagesize=100&reverse=true&beforeid="+beforeId+"&country="+selectedCountry;
        }
        if((yearFlag)&&(stateFlag)&&!(countryFlag)){
            equalUrl = "http://bismarck.sdsu.edu/hometown/users?page="+pageE+"&pagesize=100&reverse=true&beforeid="+beforeId+"&year="+selectedYear+"&state="+selectedState;
        }
        if((yearFlag)&&!(stateFlag)&&(countryFlag)){
            equalUrl = "http://bismarck.sdsu.edu/hometown/users?page="+pageE+"&pagesize=100&reverse=true&beforeid="+beforeId+"&year="+selectedYear+"&country="+selectedCountry;
        }
        if(!(yearFlag)&&(stateFlag)&&(countryFlag)){
            equalUrl = "http://bismarck.sdsu.edu/hometown/users?page="+pageE+"&pagesize=100&reverse=true&beforeid="+beforeId+"&state="+selectedState+"&country="+selectedCountry;
        }
        if((yearFlag)&&(stateFlag)&&(countryFlag)){
            equalUrl = "http://bismarck.sdsu.edu/hometown/users?page="+pageE+"&pagesize=100&reverse=true&beforeid="+beforeId+"&year="+selectedYear+"&state="+selectedState+"&country="+selectedCountry;
        }
    }

    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onInfoWindowClick(Marker marker) {
        String clickedBismarkName = marker.getTitle().toString();
        Log.i("assign5","current name"+currentUserLoggedIn);
        if (clickedBismarkName.equals(currentUserLoggedIn)){
            Toast.makeText(getBaseContext(),"Chat with self is denied!",Toast.LENGTH_SHORT).show();
        }else if(firebaseUsers.contains(clickedBismarkName)){
            startChatActivityOnClick(clickedBismarkName);
        }
        else{
            Toast.makeText(getBaseContext(),clickedBismarkName+" does not exist in the Firebase Server!",Toast.LENGTH_SHORT).show();
        }
    }

    public void startChatActivityOnClick(String clickedBismarkName){
        Intent go = new Intent(this,Chat.class);
        go.putExtra("clickedBismarkName",clickedBismarkName);
        startActivity(go);
    }

}
