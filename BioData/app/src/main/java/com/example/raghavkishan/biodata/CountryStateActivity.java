package com.example.raghavkishan.biodata;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CountryStateActivity extends AppCompatActivity implements Country.CountryResultListner, State.StateResultListner{
    private String selectedCountry;
    private String selectedState;
    ArrayList<String> CountryList = new ArrayList<String>();
    Button candsbuttonset;
    Button candsbuttoncancel ;
    String countryname;
    String statename;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_state);
        candsbuttonset = (Button) findViewById(R.id.countrystate_done_button);
        candsbuttoncancel = (Button) findViewById(R.id.countrystate_cancel_button);
        parsecountry();
        FragmentManager fragments = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragments.beginTransaction();
        Country country_fragment = new Country();
        fragmentTransaction.replace(R.id.fragContainer, country_fragment);
        fragmentTransaction.commit();

        Bundle arguments = new Bundle();
        arguments.putStringArrayList("Countries",CountryList);
        country_fragment.setArguments(arguments);

        candsbuttonset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toPassBack = getIntent();
                toPassBack.putExtra("Countryname",selectedCountry);
                toPassBack.putExtra("Statename",selectedState);
                setResult(RESULT_OK,toPassBack);
                finish();
            }
        });

        candsbuttoncancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    // parsing the countries file.
    void parsecountry(){
        try {
            InputStream countriesFile = getAssets().open("countries");
            BufferedReader in = new BufferedReader( new InputStreamReader(countriesFile));
            while((countryname = in.readLine()) != null)
            { CountryList.add(countryname);}
            //for(int i = 0; i<CountryList.size();i++)
            //{
             //   System.out.println(CountryList.get(i));
           // }
        } catch (IOException e) {
            Log.e("rew", "read Error", e);
        }
    }


    public void printresult(String country){
        selectedCountry = country;
    }
    public void printState(String state){
        selectedState = state;
    }
}
