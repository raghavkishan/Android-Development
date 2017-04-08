package com.example.raghavkishan.sdsuhometownlocations;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import static android.R.attr.fragment;
import static com.example.raghavkishan.sdsuhometownlocations.R.layout.fragment_country;

public class CountryStateActivity extends AppCompatActivity implements CountryFragment.CountryResultListner,StateFragment.StateResultListner{

    String selectedCountry,selectedState;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_state);
        Button setButton = (Button) findViewById(R.id.set_button);

        FragmentManager fragment = getSupportFragmentManager();
        FragmentTransaction fragmentTransactions = fragment.beginTransaction();
        CountryFragment country_fragment = new CountryFragment();
        fragmentTransactions.replace(R.id.fragment_container,country_fragment);
        fragmentTransactions.commit();

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toPassBack = getIntent();
                toPassBack.putExtra("countryname",selectedCountry);
                toPassBack.putExtra("statename",selectedState);
                setResult(RESULT_OK,toPassBack);
                finish();
            }
        });

    }

    public void countryresult(String country){selectedCountry = country;}
    public void stateresult(String state){selectedState = state;}

}
