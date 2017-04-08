package com.example.raghavkishan.sdsuhometownlocations;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class CountryFragment extends Fragment {

    ArrayList<String> countryList = new ArrayList<String>();
    ListView countryListView;

    public CountryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View countryInflater = inflater.inflate(R.layout.fragment_country, container, false);
        countryListView = (ListView) countryInflater.findViewById(R.id.country_list_fragment);

        return countryInflater;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getCountryFromServer();
    }

    public void getCountryFromServer(){
        String url = "http://bismarck.sdsu.edu/hometown/countries";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String[] eachCountry = response.replace("[","").replace("]","").replace("\"","").split(",");
                for (String each:eachCountry){
                    countryList.add(each);

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,countryList);
                    countryListView.setAdapter(adapter);
                    countryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            CountryResultListner country = (CountryResultListner) getActivity();
                            country.countryresult(parent.getItemAtPosition(position).toString());

                            Bundle args = new Bundle();
                            args.putString("countryname",parent.getItemAtPosition(position).toString());

                            FragmentManager fragment = getFragmentManager();
                            FragmentTransaction fragmentTransactions = fragment.beginTransaction();
                            StateFragment state_fragment = new StateFragment();
                            state_fragment.setArguments(args);
                            fragmentTransactions.replace(R.id.fragment_container,state_fragment);
                            fragmentTransactions.commit();
                        }
                    });
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        singletonRequestQueue.instance(getActivity()).add(stringRequest);
    }

    public interface CountryResultListner{
        public void countryresult(String country);
    }

}
