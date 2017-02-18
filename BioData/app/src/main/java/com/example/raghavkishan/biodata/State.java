package com.example.raghavkishan.biodata;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class State extends Fragment {

    ArrayList<String> StateList = new ArrayList<String>();
    String countryname;
    String statename;
    ListView state_list_fragment;

    public State() {
        // Required empty public constructor
    }


    @Override
    //Using Inflater to access the resources from the fragment.
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View statev = inflater.inflate(R.layout.fragment_state, container, false);
        state_list_fragment = (ListView) statev.findViewById(R.id.state_list_fragment);

        return statev;
    }

    @Override
    //Identified the state list using the country.
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments()!=null){
            countryname = getArguments().getString("countryname");
        }
        parsestate(countryname);
    }

    // parsing the state from the asset file and displaying it in the listview.
    void parsestate(String country){
        try {
            InputStream stetfile = getContext().getAssets().open(country);
            BufferedReader in = new BufferedReader( new InputStreamReader(stetfile));
            while((statename = in.readLine()) != null)
            { StateList.add(statename);}

            ArrayAdapter<String> adapter =
                    new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, StateList);
            state_list_fragment.setAdapter(adapter);
            state_list_fragment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    StateResultListner sState = (StateResultListner) getActivity();
                    sState.printState(parent.getItemAtPosition(position).toString());
                }
            });
        }

        catch (IOException e) {
            Log.e("rew", "read Error", e);
        }
    }

    public interface StateResultListner {

        public void printState(String state);
    }

}
