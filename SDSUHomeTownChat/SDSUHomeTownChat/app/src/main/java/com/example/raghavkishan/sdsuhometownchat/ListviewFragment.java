package com.example.raghavkishan.sdsuhometownchat;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.provider.Contacts.SettingsColumns.KEY;
import static java.sql.Types.INTEGER;
import static java.text.Collator.PRIMARY;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListviewFragment extends Fragment {

    ListView peopleListView;
    String url= "http://bismarck.sdsu.edu/hometown/users?page=0&reverse=true&country=Canada";
    JSONObject JsonResponse;
    ArrayList userList = new ArrayList();
    ArrayList usersList = new ArrayList();
    DatabaseHelper peopleHelper;
    SQLiteDatabase database,databaseRead;
    int sqlYear,sqlId;
    String sqlNickName,sqlCountry,sqlState,sqlCity;
    int tempNextId,nextId,page=0,checkNextId;
    Button listViewButton;

    public ListviewFragment() {
        // Required empty public constructor
    }

     @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_listview, container, false);
        //Log.i("assign5","In listview fragment after inflation.");
        peopleListView = (ListView) fragmentView.findViewById(R.id.fragment_list_view);
        return  fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        peopleHelper = (new DatabaseHelper(getActivity()));
        database = peopleHelper.getWritableDatabase();
        databaseRead = peopleHelper.getReadableDatabase();
        onScrolll();
        //Log.i("assign5","In listview fragment before get data is called and after the SQLlite table is created");
        //getdata();
        //Log.i("assign5","In listview fragment after get data is called");
        checkerNextId();
    }

    public void checkerNextId() {
        String url = "http://bismarck.sdsu.edu/hometown/nextid";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                checkNextId = Integer.parseInt(response);
                Log.i("assign5","checkNextId has been assigned "+ checkNextId);
                intialChecker();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        singletonRequestQueue.instance(getActivity()).add(stringRequest);
    }

    public void intialChecker(){
        Cursor checkResult = databaseRead.rawQuery("select id from PEOPLE ORDER BY ID ASC LIMIT 1",null);
        checkResult.moveToFirst();
        Log.i("Assign5",""+String.valueOf(checkResult.getInt(0)));
    }

    public void onListViewButtonClick(){
        intialChecker();
    }


    public void getdata(){
        //Log.i("assign5","In listview fragment inside getdata");
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
                    }catch (JSONException error){
                        error.printStackTrace();
                    }
                }
                Toast.makeText(getActivity(),"after insertIntoSqlDb",Toast.LENGTH_LONG).show();
                //Log.i("assign5","after insertIntoSqlDb");
                //Log.i("assign5","When Json is received from server"+response.toString());
                //Log.i("assign5","before getRecords()");
                getRecords();
                display();
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.i("assign5", error.toString());
            }
        };
        JsonArrayRequest getRequest = new JsonArrayRequest(url, success, failure);
        singletonRequestQueue.instance(getActivity()).add(getRequest);

    }

    public void display(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,usersList);
        peopleListView.setAdapter(adapter);
        //Log.i("assign5","In listview fragment after adapter is set");
    }

    public class DatabaseHelper extends SQLiteOpenHelper{
        private static final String DATABASE_NAME="people.db";
        private static final int DATABASE_VERSION = 1;

        public DatabaseHelper(Context context){
            super(context,DATABASE_NAME,null,DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS "+"PEOPLE"+" ("
                    +"id"+" INTEGER PRIMARY KEY,"
                    +"nickname"+" TEXT,"
                    +"city"+" TEXT,"
                    +"longitude"+" DOUBLE,"
                    +"state"+" TEXT,"
                    +"year"+" INTEGER,"
                    +"latitude"+" DOUBLE,"
                    +"timestamp"+" TEXT,"
                    +"country"+" TEXT"
                    +");");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public void insertIntoSqlDb(int id,String nickname,String city,double longitude,String state,int year,double latitude,String timeStamp,String country){
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
        database.insert("PEOPLE",null,contentValues);
        //Log.i("assign5","In insertIntoSqlDb after inserting");
    }

    public void getRecords(){
        usersList.clear();
        Cursor result = databaseRead.rawQuery("select * from PEOPLE",null);
        while(result.moveToNext()) {
            sqlId = result.getInt(0);
            sqlYear = result.getInt(5);
            sqlNickName = result.getString(1);
            sqlCountry = result.getString(8);
            sqlState = result.getString(4);
            sqlCity = result.getString(2);
            //Log.i("assign5","inside getRecords() before adding to usersList");
            usersList.add("NickName: "+sqlNickName+"\nCountry: "+sqlCountry+"\nState: "+sqlState+"\nCity: "+sqlCity+"\nYear: "+sqlYear);
            //Log.i("assign5","inside getRecords() after adding to usersList");
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.i("assign5","In ondestroy before deleting the data");
        //database.delete("PEOPLE","1",null);
        //Log.i("assign5","In ondestroy after deleting the data");
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
        singletonRequestQueue.instance(getActivity()).add(stringRequest);
    }

    /*public void getNextId(int nextid){
        tempNextId = nextid;
    }*/

    public void getFurtherData(){
        Log.i("assign5","In getFurtherData before page is incremented "+page);
        page = page + 1;
        /*Log.i("assign5","In getFurtherData before page is incremented "+page);
        nextId();
        Log.i("assign5","In getFurtherData after next id() "+tempNextId);*/
        Log.i("assign5","In getFurtherData after page is incremented "+page+" and nextid "+tempNextId);
        String url = "http://bismarck.sdsu.edu/hometown/users?page="+page+"&reverse=true&beforeid="+tempNextId+"&country=Canada";;
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
                    }catch (JSONException error){
                        error.printStackTrace();
                    }
                }
                Log.i("assign5","response of in get further data "+page+" response length "+response.length());
                getRecords();
                display();
                //Log.i("assign5","after insertIntoSqlDb due to scroll");
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.i("assign5", error.toString());
            }
        };
        JsonArrayRequest getRequest = new JsonArrayRequest(url, success, failure);
        singletonRequestQueue.instance(getActivity()).add(getRequest);
    }

    public void onScrolll(){

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
                    //getFurtherDataNextId();
                }
            }
        });
    }



}
