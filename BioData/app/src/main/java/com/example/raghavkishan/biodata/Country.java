package com.example.raghavkishan.biodata;


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
import android.widget.Toast;

import java.util.ArrayList;

import static java.security.AccessController.getContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class Country extends Fragment{

    ArrayList<String> Country_List = new ArrayList<String>();
    ListView country_list_view;

    public Country() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    //Using Inflater to access the resources from the fragment.
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getArguments() != null){
            Country_List = getArguments().getStringArrayList("Countries");
        }

        // Inflate the layout for this fragment
        View countryv = inflater.inflate(R.layout.fragment_country, container, false);
        country_list_view = (ListView) countryv.findViewById(R.id.country_list_fragment);

        return countryv;
    }

    //displaying the list of countries using the arraylist.
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, Country_List);
        country_list_view.setAdapter(adapter);
        country_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CountryResultListner countryListener = (CountryResultListner) getActivity();
                countryListener.printresult(parent.getItemAtPosition(position).toString());

                Bundle args = new Bundle();
                args.putString("countryname",parent.getItemAtPosition(position).toString());
                FragmentManager fragments = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragments.beginTransaction();
                State state_fragment = new State();
                state_fragment.setArguments(args);
                fragmentTransaction.replace(R.id.fragContainer, state_fragment);
                fragmentTransaction.commit();
              //  Toast.makeText(getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT ).show();
            }
        });
    }

    public interface CountryResultListner{
        public void printresult(String Country);
    }
}
