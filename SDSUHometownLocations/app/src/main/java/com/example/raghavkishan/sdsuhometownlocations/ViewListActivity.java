package com.example.raghavkishan.sdsuhometownlocations;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewListActivity extends AppCompatActivity {

    ListView listView;
    String url;
    JSONObject JsonResponse;
    ArrayList userList = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list);

        listView = (ListView) findViewById(R.id.list_view_in_view_user);
        url = getIntent().getStringExtra("url");
        Log.i("before","url just after recieving"+url);
        getdata();

    }

    public void getdata(){

        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                for (int i=0;i<response.length();i++) {
                    try {
                        JsonResponse = (JSONObject) response.get(i);
                        userList.add("NickName: "+" "+JsonResponse.optString("nickname")+"\nCountry: "+JsonResponse.optString("country").toString()+"\nState: "+JsonResponse.optString("state").toString()+"\nCity: "+JsonResponse.optString("city").toString()+"\nYear: "+JsonResponse.optString("year"));
                        display();

                    }catch (JSONException error){
                        error.printStackTrace();
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

    public void display(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_list_item_1,userList);
        listView.setAdapter(adapter);
    }
}
