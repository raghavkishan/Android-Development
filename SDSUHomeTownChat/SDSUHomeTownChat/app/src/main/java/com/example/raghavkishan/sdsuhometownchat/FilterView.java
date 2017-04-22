package com.example.raghavkishan.sdsuhometownchat;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class FilterView extends AppCompatActivity {


    ListView peopleListView;
    String url,currentUserLoggedIn;
    JSONObject JsonResponse;
    ArrayList userList = new ArrayList();
    ArrayList usersList = new ArrayList();
    DatabaseHelper peopleHelper;
    SQLiteDatabase database,databaseRead;
    int sqlYear,sqlId,offset = 0,beforeId;
    String sqlNickName,sqlCountry,sqlState,sqlCity;
    int tempNextId,nextId,page=0,checkNextId;
    Button listViewButton,mapViewButton;
    String countryQuery = "select * from PEOPLE where country = \"Canada\"";
    ArrayList trackId = new ArrayList();
    ArrayAdapter<String> adapter;
    Boolean unEqualFlag = false,equalFlag = false;
    String unEqualBeginUrl,unEqualurl,equalUrl,equalQueryStmt;
    int pageE;
    ArrayList<String> firebaseUsers = new ArrayList<String>();
    FirebaseAuth authenticationInstance;
    FirebaseDatabase firebaseDataBase;
    TextView currentUserTextViewDisp;

    public static boolean countryFlagPass=false,stateFlagPass=false,noneFlagPass=false;
    public Spinner spinYear,spinCountry,spinState;
    public static Integer selectedYear;
    public double latitude,longitude,mapLatitude,mapLongitude;
    public static String selectedCountry,selectedState,spinnerUrl,selectedYearString;
    public static boolean yearFlag=false,stateFlag=false,countryFlag=false;
    public static ArrayList<String> countryList = new ArrayList<String>();
    public static ArrayList<String> stateList = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_view);

        checkerNextId();
        yearSpinner();
        countrySpinner();
        listViewButton = (Button) findViewById(R.id.filter_view_list_view_button);
        peopleListView = (ListView) findViewById(R.id.list_view_view);
        currentUserTextViewDisp = (TextView) findViewById(R.id.filter_view_current_user_value);
        peopleHelper = (new DatabaseHelper(this));
        database = peopleHelper.getWritableDatabase();
        adapter = new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_list_item_1,usersList);

        databaseRead = peopleHelper.getReadableDatabase();
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

        peopleListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                this.isScrollCompleted(view,scrollState);

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }

            public void isScrollCompleted(AbsListView view, int scrollState){
                if(!view.canScrollList(1) && scrollState == SCROLL_STATE_IDLE){
                    //Log.i("Assign5","in scroll when bottom is reached");
                    if (unEqualFlag==true){
                        getFurtherDataNextId();
                    }
                    if (equalFlag == true){
                        getRecords();
                    }
                }
            }
        });

        peopleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedBismarkName = parent.getItemAtPosition(position).toString();

                if (clickedBismarkName.equals(currentUserLoggedIn)){
                    Toast.makeText(getBaseContext(),"Chat with self is denied!",Toast.LENGTH_SHORT).show();
                }
                else if(firebaseUsers.contains(clickedBismarkName)){
                    startChatActivityOnClick(clickedBismarkName);
                }
                else{
                    Toast.makeText(getBaseContext(),clickedBismarkName+" does not exist in the Firebase Server!",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void onClickSignOut(View view){
        usersList.clear();
        FirebaseAuth.getInstance().signOut();
        finish();
        Toast.makeText(this,"you have logged out successfully",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        usersList.clear();
        FirebaseAuth.getInstance().signOut();
        finish();
        Toast.makeText(this,"you have logged out successfully",Toast.LENGTH_LONG).show();
    }

    public void checkerNextId() {
        String url = "http://bismarck.sdsu.edu/hometown/nextid";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                checkNextId = Integer.parseInt(response);
                Log.i("assign5","checkNextId has been assigned "+ checkNextId);
                //intialChecker();
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
            Log.i("assign5","equal match"+tableMaxId+" "+checkNextId);
            equalFlag = true;
            unEqualFlag = false;
            equalMatch();
        }
        else {
            Log.i("assign5","un equal match"+tableMaxId+" "+checkNextId);
            unEqualFlag = true;
            equalFlag = false;
            unequalMatch();

        }
    }

    public void equalMatch(){
        offset = 0;
        equalFlag = true;
        peopleListView.setAdapter(adapter);
        getRecords();

    }

    public void unequalMatch(){
        url = unEqualBeginUrl;
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
                        //usersList.add("NickName: "+nickName+"\nCountry: "+country+"\nState: "+state+"\nCity: "+city+"\nYear: "+year);
                        usersList.add(nickName);
                        insertIntoSqlDb(id,nickName,city,longitude,state,year,latitude,timeStamp,country);
                        if(latitude == 0.0 || longitude == 0.0)
                        {
                            Log.i("assign5","before entering gecoder getdata nickname ="+nickName);
                            doAsyncTask(state,country,nickName);
                        }
                    }catch (JSONException error){
                        error.printStackTrace();
                    }
                }
                peopleListView.setAdapter(adapter);

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

    public void getRecords(){
        equalQuerySetter();
        Cursor result = databaseRead.rawQuery(equalQueryStmt,null);//"select * from PEOPLE where country = \"Canada\" ORDER BY id DESC limit 25 OFFSET "+offset
        Log.i("assign5","result in getrecords"+result.getCount());
        if (result.getCount()< 25) {
            while(result.moveToNext()){
                sqlId = result.getInt(0);
                sqlYear = result.getInt(5);
                sqlNickName = result.getString(1);
                sqlCountry = result.getString(8);
                sqlState = result.getString(4);
                sqlCity = result.getString(2);
                usersList.add(sqlNickName);
                beforeId = result.getInt(0);
                Log.i("assign5"," id's"+sqlId);
            }
            Log.i("assign5","beforeid"+beforeId);
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
                usersList.add(sqlNickName);
                beforeId = result.getInt(0);
            }
        }
        adapter.notifyDataSetChanged();
        Log.i("assign5","result in getrecords after reading"+result.getCount());
        offset = offset + 25;
    }

    public void getFurtherDataNextId() {
        String url = "http://bismarck.sdsu.edu/hometown/nextid";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                tempNextId = Integer.parseInt(response);
                Log.i("assign5","In nextid before getFurtherData() "+tempNextId);
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
        Log.i("assign5","In getFurtherData after page is incremented "+page+" and nextid "+tempNextId);
        unEqualSetter();
        String url = unEqualurl;
        Log.i("assign5",unEqualurl);
                //"http://bismarck.sdsu.edu/hometown/users?page="+page+"&reverse=true&beforeid="+tempNextId+"&country=Canada";;
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
                        usersList.add(nickName);
                        insertIntoSqlDb(id,nickName,city,longitude,state,year,latitude,timeStamp,country);
                        if(latitude == 0.0 || longitude == 0.0)
                        {
                            Log.i("assign5","before entering gecoder getFurtherData nickname ="+nickName);
                            doAsyncTask(state,country,nickName);
                        }
                    }catch (JSONException error){
                        error.printStackTrace();
                    }
                }
                Log.i("assign5","response of in get further data "+page+" response length "+response.length());
                adapter.notifyDataSetChanged();
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
                        usersList.add(nickName);
                        insertIntoSqlDb(id,nickName,city,longitude,state,year,latitude,timeStamp,country);
                        beforeId = JsonResponse.getInt("id");
                        if(latitude == 0.0 || longitude == 0.0)
                        {
                            Log.i("assign5","before entering gecoder getFurtherDataEqual nickname ="+nickName);
                            doAsyncTask(state,country,nickName);
                        }
                    }catch (JSONException error){
                        error.printStackTrace();
                    }
                }
                Log.i("assign5","response of in get further data Equal "+page+" response length "+response.length());
                adapter.notifyDataSetChanged();
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

    //-----------------------------------------------------------------------------------------------------------------------------------------

    public void yearSpinner() {
        selectedYearString = "none";
        ArrayList<String> years = new ArrayList<String>();
        years.add("None");
        for (int i = 1970; i <= 2017; i++) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, years);
        spinYear = (Spinner) findViewById(R.id.filter_view_year_spinner);
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
                spinCountry = (Spinner) findViewById(R.id.filter_view_country_spinner);
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
            spinState = (Spinner) findViewById(R.id.filter_view_state_spinner);
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
                    spinState = (Spinner) findViewById(R.id.filter_view_state_spinner);
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

    public void unEqualSetter(){
        Log.i("assign5","inside unEqualSetter");
        if (!(yearFlag)&&!(stateFlag)&&!(countryFlag)){
            unEqualurl="http://bismarck.sdsu.edu/hometown/users?page="+page+"&reverse=true&beforeid="+tempNextId;
        }
        if((yearFlag)&&!(stateFlag)&&!(countryFlag)){
            unEqualurl = "http://bismarck.sdsu.edu/hometown/users?page="+page+"&reverse=true&beforeid="+tempNextId+"&year="+selectedYear;
        }
        if(!(yearFlag)&&(stateFlag)&&!(countryFlag)){
            unEqualurl= "http://bismarck.sdsu.edu/hometown/users?page="+page+"&reverse=true&beforeid="+tempNextId+"&state="+selectedState;
        }
        if(!(yearFlag)&&!(stateFlag)&&(countryFlag)){
            unEqualurl= "http://bismarck.sdsu.edu/hometown/users?page="+page+"&reverse=true&beforeid="+tempNextId+"&country="+selectedCountry;
        }
        if((yearFlag)&&(stateFlag)&&!(countryFlag)){
            unEqualurl= "http://bismarck.sdsu.edu/hometown/users?page="+page+"&reverse=true&beforeid="+tempNextId+"&year="+selectedYear+"&state="+selectedState;
        }
        if((yearFlag)&&!(stateFlag)&&(countryFlag)){
            Log.i("assign5","inside country flag settter only"+selectedCountry);
            unEqualurl= "http://bismarck.sdsu.edu/hometown/users?page="+page+"&reverse=true&beforeid="+tempNextId+"&year="+selectedYear+"&country="+selectedCountry;
        }
        if(!(yearFlag)&&(stateFlag)&&(countryFlag)){
            unEqualurl= "http://bismarck.sdsu.edu/hometown/users?page="+page+"&reverse=true&beforeid="+tempNextId+"&state="+selectedState+"&country="+selectedCountry;
        }
        if((yearFlag)&&(stateFlag)&&(countryFlag)){
            unEqualurl= "http://bismarck.sdsu.edu/hometown/users?page="+page+"&reverse=true&beforeid="+tempNextId+"&year="+selectedYear+"&state="+selectedState+"&country="+selectedCountry;
        }
    }

    public void equalQuerySetter(){
        if (!(yearFlag)&&!(stateFlag)&&!(countryFlag)){
            equalQueryStmt = "select * from PEOPLE ORDER BY id DESC limit 25 OFFSET "+offset;
        }
        if((yearFlag)&&!(stateFlag)&&!(countryFlag)){
            equalQueryStmt = "select * from PEOPLE where year = \""+selectedYear+"\" ORDER BY id DESC limit 25 OFFSET "+offset;
        }
        if(!(yearFlag)&&(stateFlag)&&!(countryFlag)){
            equalQueryStmt = "select * from PEOPLE where state = \""+selectedState+"\" ORDER BY id DESC limit 25 OFFSET "+offset;
        }
        if(!(yearFlag)&&!(stateFlag)&&(countryFlag)){
            equalQueryStmt = "select * from PEOPLE where country = \""+selectedCountry+"\" ORDER BY id DESC limit 25 OFFSET "+offset;
        }
        if((yearFlag)&&(stateFlag)&&!(countryFlag)){
            equalQueryStmt = "select * from PEOPLE where year = "+selectedYear+" and state = \""+selectedState+"\" ORDER BY id DESC limit 25 OFFSET "+offset;
        }
        if((yearFlag)&&!(stateFlag)&&(countryFlag)){
            equalQueryStmt = "select * from PEOPLE where year = "+selectedYear+" and country = \""+selectedCountry+"\" ORDER BY id DESC limit 25 OFFSET "+offset;
        }
        if(!(yearFlag)&&(stateFlag)&&(countryFlag)){
            equalQueryStmt = "select * from PEOPLE where state = \""+selectedState+"\" and country = \""+selectedCountry+"\" ORDER BY id DESC limit 25 OFFSET "+offset;
        }
        if((yearFlag)&&(stateFlag)&&(countryFlag)){
            equalQueryStmt = "select * from PEOPLE where year = "+selectedYear+" and state = \""+selectedState+"\" and country = \""+selectedCountry+"\" ORDER BY id DESC limit 25 OFFSET "+offset;
        }
    }

    public void equalUrlSetter(){
        if (!(yearFlag)&&!(stateFlag)&&!(countryFlag)){
            equalUrl = "http://bismarck.sdsu.edu/hometown/users?page="+pageE+"&reverse=true&beforeid="+beforeId;
        }
        if((yearFlag)&&!(stateFlag)&&!(countryFlag)){
            equalUrl = "http://bismarck.sdsu.edu/hometown/users?page="+pageE+"&reverse=true&beforeid="+beforeId+"&year="+selectedYear;
        }
        if(!(yearFlag)&&(stateFlag)&&!(countryFlag)){
            equalUrl = "http://bismarck.sdsu.edu/hometown/users?page="+pageE+"&reverse=true&beforeid="+beforeId+"&state="+selectedState;
        }
        if(!(yearFlag)&&!(stateFlag)&&(countryFlag)){
            equalUrl = "http://bismarck.sdsu.edu/hometown/users?page="+pageE+"&reverse=true&beforeid="+beforeId+"&country="+selectedCountry;
        }
        if((yearFlag)&&(stateFlag)&&!(countryFlag)){
            equalUrl = "http://bismarck.sdsu.edu/hometown/users?page="+pageE+"&reverse=true&beforeid="+beforeId+"&year="+selectedYear+"&state="+selectedState;
        }
        if((yearFlag)&&!(stateFlag)&&(countryFlag)){
            equalUrl = "http://bismarck.sdsu.edu/hometown/users?page="+pageE+"&reverse=true&beforeid="+beforeId+"&year="+selectedYear+"&country="+selectedCountry;
        }
        if(!(yearFlag)&&(stateFlag)&&(countryFlag)){
            equalUrl = "http://bismarck.sdsu.edu/hometown/users?page="+pageE+"&reverse=true&beforeid="+beforeId+"&state="+selectedState+"&country="+selectedCountry;
        }
        if((yearFlag)&&(stateFlag)&&(countryFlag)){
            equalUrl = "http://bismarck.sdsu.edu/hometown/users?page="+pageE+"&reverse=true&beforeid="+beforeId+"&year="+selectedYear+"&state="+selectedState+"&country="+selectedCountry;
        }
    }
    public void onListViewButtonClick(View view){

        usersList.clear();
        try {
            selectedState = URLEncoder.encode(selectedState,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (!(yearFlag)&&!(stateFlag)&&!(countryFlag)){
            unEqualBeginUrl="http://bismarck.sdsu.edu/hometown/users?page=0&reverse=true";
            spinnerUrl="http://bismarck.sdsu.edu/hometown/users?reverse=true";
        }
        if((yearFlag)&&!(stateFlag)&&!(countryFlag)){
            unEqualBeginUrl="http://bismarck.sdsu.edu/hometown/users?page=0&reverse=true&year="+selectedYear;
            spinnerUrl = "http://bismarck.sdsu.edu/hometown/users?reverse=true&year="+selectedYear;
        }
        if(!(yearFlag)&&(stateFlag)&&!(countryFlag)){
            unEqualBeginUrl="http://bismarck.sdsu.edu/hometown/users?page=0&reverse=true&state="+selectedState;
            spinnerUrl = "http://bismarck.sdsu.edu/hometown/users?reverse=true&state="+selectedState;
        }
        if(!(yearFlag)&&!(stateFlag)&&(countryFlag)){
            unEqualBeginUrl="http://bismarck.sdsu.edu/hometown/users?page=0&reverse=true&country="+selectedCountry;
            spinnerUrl = "http://bismarck.sdsu.edu/hometown/users?reverse=true&country="+selectedCountry;
        }
        if((yearFlag)&&(stateFlag)&&!(countryFlag)){
            unEqualBeginUrl="http://bismarck.sdsu.edu/hometown/users?page=0&reverse=true&year="+selectedYear+"&state="+selectedState;
            spinnerUrl = "http://bismarck.sdsu.edu/hometown/users?reverse=true&year="+selectedYear+"&state="+selectedState;
        }
        if((yearFlag)&&!(stateFlag)&&(countryFlag)){
            Log.i("assign5","inside country flag set only"+selectedCountry);
            unEqualBeginUrl="http://bismarck.sdsu.edu/hometown/users?page=0&reverse=true&year="+selectedYear+"&country="+selectedCountry;
            spinnerUrl = "http://bismarck.sdsu.edu/hometown/users?reverse=true&year="+selectedYear+"&country="+selectedCountry;
        }
        if(!(yearFlag)&&(stateFlag)&&(countryFlag)){
            unEqualBeginUrl="http://bismarck.sdsu.edu/hometown/users?page=0&reverse=true&state="+selectedState+"&country="+selectedCountry;
            spinnerUrl = "http://bismarck.sdsu.edu/hometown/users?reverse=true&state="+selectedState+"&country="+selectedCountry;
        }
        if((yearFlag)&&(stateFlag)&&(countryFlag)){
            unEqualBeginUrl="http://bismarck.sdsu.edu/hometown/users?page=0&reverse=true&year="+selectedYear+"&state="+selectedState+"&country"+selectedCountry;
            spinnerUrl = "http://bismarck.sdsu.edu/hometown/users?reverse=true&year="+selectedYear+"&state="+selectedState+"&country="+selectedCountry;
        }

        intialChecker();

    }

    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

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
            Log.i("assign5","Inside async geo doInBackground");
            Log.i("assign5","lat= "+word[0]+" long= "+word[1] +" name= "+word[2]);
            LatLng location;
            location = latitudeLongitudeCheckerAndSetter(word[0],word[1]);
            userQuery = word[2];
            return (location);
        }

        public void onPostExecute(LatLng location){
            String strFilter = "nickname";
            ContentValues args = new ContentValues();
            args.put("latitude",location.latitude);
            args.put("longitude",location.longitude);
            String[] argName = new String[]{userQuery};
            database.update("PEOPLE",args,"nickname=?",argName);
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void onMapView(View view){

        usersList.clear();
        try {
            selectedState = URLEncoder.encode(selectedState,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (!(yearFlag)&&!(stateFlag)&&!(countryFlag)){
            unEqualBeginUrl="http://bismarck.sdsu.edu/hometown/users?page=0&pagesize=100&reverse=true";
            spinnerUrl="http://bismarck.sdsu.edu/hometown/users?reverse=true";
        }
        if((yearFlag)&&!(stateFlag)&&!(countryFlag)){
            unEqualBeginUrl="http://bismarck.sdsu.edu/hometown/users?page=0&pagesize=100&reverse=true&year="+selectedYear;
            spinnerUrl = "http://bismarck.sdsu.edu/hometown/users?reverse=true&year="+selectedYear;
        }
        if(!(yearFlag)&&(stateFlag)&&!(countryFlag)){
            try{
                latitudeLongitudeGetter(selectedState,selectedCountry);
            }catch (Exception error){

            }
            unEqualBeginUrl="http://bismarck.sdsu.edu/hometown/users?page=0&pagesize=100&reverse=true&state="+selectedState;
            spinnerUrl = "http://bismarck.sdsu.edu/hometown/users?reverse=true&state="+selectedState;
        }
        if(!(yearFlag)&&!(stateFlag)&&(countryFlag)){
            try{
                latitudeLongitudeGetter(selectedCountry);
                Log.i("before",""+latitude+" "+longitude);
            }catch (Exception error){

            }
            unEqualBeginUrl="http://bismarck.sdsu.edu/hometown/users?page=0&pagesize=100&reverse=true&country="+selectedCountry;
            spinnerUrl = "http://bismarck.sdsu.edu/hometown/users?reverse=true&country="+selectedCountry;
        }
        if((yearFlag)&&(stateFlag)&&!(countryFlag)){
            try{
                latitudeLongitudeGetter(selectedState,selectedCountry);
            }catch (Exception error){

            }
            unEqualBeginUrl="http://bismarck.sdsu.edu/hometown/users?page=0&pagesize=100&reverse=true&year="+selectedYear+"&state="+selectedState;
            spinnerUrl = "http://bismarck.sdsu.edu/hometown/users?reverse=true&year="+selectedYear+"&state="+selectedState;
        }
        if((yearFlag)&&!(stateFlag)&&(countryFlag)){
            try{
                latitudeLongitudeGetter(selectedCountry);
            }catch (Exception error){

            }
            Log.i("assign5","inside country flag set only"+selectedCountry);
            unEqualBeginUrl="http://bismarck.sdsu.edu/hometown/users?page=0&pagesize=100&reverse=true&year="+selectedYear+"&country="+selectedCountry;
            spinnerUrl = "http://bismarck.sdsu.edu/hometown/users?reverse=true&year="+selectedYear+"&country="+selectedCountry;
        }
        if(!(yearFlag)&&(stateFlag)&&(countryFlag)){
            try{
                latitudeLongitudeGetter(selectedState,selectedCountry);
            }catch (Exception error){

            }
            unEqualBeginUrl="http://bismarck.sdsu.edu/hometown/users?page=0&pagesize=100&reverse=true&state="+selectedState+"&country="+selectedCountry;
            spinnerUrl = "http://bismarck.sdsu.edu/hometown/users?reverse=true&state="+selectedState+"&country="+selectedCountry;
        }
        if((yearFlag)&&(stateFlag)&&(countryFlag)){
            try{
                latitudeLongitudeGetter(selectedState,selectedCountry);
            }catch (Exception error){

            }
            unEqualBeginUrl="http://bismarck.sdsu.edu/hometown/users?page=0&pagesize=100&reverse=true&year="+selectedYear+"&state="+selectedState+"&country"+selectedCountry;
            spinnerUrl = "http://bismarck.sdsu.edu/hometown/users?reverse=true&year="+selectedYear+"&state="+selectedState+"&country="+selectedCountry;
        }

        Intent go = new Intent(this,Mapview.class);
        go.putExtra("spinnerUrl",spinnerUrl);
        go.putExtra("unEqualBeginUrl",unEqualBeginUrl);
        go.putExtra("latitude",mapLatitude);
        go.putExtra("longitude", mapLongitude);
        startActivity(go);
    }


    public void latitudeLongitudeGetter(String country) throws IOException {
        Geocoder locator = new Geocoder(this);
        try {
            ArrayList<Address> address = (ArrayList<Address>) locator.getFromLocationName(country, 1);
            for (Address location : address) {
                mapLatitude = location.getLatitude();
                mapLongitude = location.getLongitude();
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
                mapLatitude = location.getLatitude();
                mapLongitude = location.getLongitude();
            }
        }catch(Exception error){
            Toast.makeText(getBaseContext(), "Error in latitude longitude checker", Toast.LENGTH_LONG).show();
        }
    }

    //---------------------------------------------------------------------------------------------------------------------------------

    public void startChatActivityOnClick(String clickedBismarkName){
        Intent go = new Intent(this,Chat.class);
        go.putExtra("clickedBismarkName",clickedBismarkName);
        startActivity(go);
    }

    public void displayChat(View view){
        Intent go =  new Intent (this,Chat.class);
        String fromButton = "onClickofButton";
        go.putExtra("onClickofButton",fromButton);
        startActivity(go);
    }

}
