package com.example.raghavkishan.sdsuhometownlocations;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class StateFragment extends Fragment {

    ArrayList<String> stateList = new ArrayList<String>();
    ListView stateListView;
    String selectedCountry;
    public StateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View stateInflater = inflater.inflate(R.layout.fragment_state, container, false);
        stateListView = (ListView) stateInflater.findViewById(R.id.state_list_fragment);

        return stateInflater;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments()!=null){
            selectedCountry = getArguments().getString("countryname");
        }

        getStatefromServer();
    }

    public void getStatefromServer(){
        String url = "http://bismarck.sdsu.edu/hometown/states?country="+selectedCountry;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String[] eachState = response.replace("[","").replace("]","").replace("\"","").split(",");
                for (String each:eachState){
                    stateList.add(each);

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,stateList);
                    stateListView.setAdapter(adapter);
                    stateListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            StateResultListner state = (StateResultListner) getActivity();
                            state.stateresult(parent.getItemAtPosition(position).toString());
                            Toast.makeText(getActivity(), "You have selected "+selectedCountry+" and "+parent.getItemAtPosition(position).toString()+" ,click set", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("On Error Response",""+error);
            }
        });
        singletonRequestQueue.instance(getActivity()).add(stringRequest);
    }

    public interface StateResultListner{
        public void stateresult(String country);
    }
}
