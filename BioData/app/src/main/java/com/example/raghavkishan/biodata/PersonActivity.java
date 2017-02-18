/*
An Android application that counts the number of times it goes into the background as well
as the number of times the Click button is clicked.
Author of program: Raghav Kishan.
*/
/* reference for the entore application:
1)from: https://developer.android.com
2)Class slides
*/

//Launcher activity.
package com.example.raghavkishan.biodata;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class PersonActivity extends AppCompatActivity {
    EditText fnamevalue;
    EditText flnamevalue;
    EditText agevalue;
    EditText emailvalue;
    EditText phonevalue;
    TextView birthdatedisplay;
    TextView countryandstatedisplay;


    private static final int INTENT_REQUEST = 123;
    private static final int INTENT_REQUEST_CS = 124;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
         fnamevalue = (EditText) findViewById(R.id.fnamevalue);
         flnamevalue = (EditText) findViewById(R.id.flnamevalue);
         agevalue = (EditText) findViewById(R.id.agevalue);
         emailvalue = (EditText) findViewById(R.id.emailvalue);
         phonevalue = (EditText) findViewById(R.id.phonevalue);
         birthdatedisplay = (TextView) findViewById(R.id.birthdatedisplay);
         countryandstatedisplay = (TextView) findViewById(R.id.countryandstatedisplay);

        //Using getstring and putstring of shared preferences to save and restore data when application is killed.
        SharedPreferences sharedPreferences = getSharedPreferences("BioData", Context.MODE_PRIVATE);
        if (sharedPreferences != null) {
            fnamevalue.setText(sharedPreferences.getString("firstname", null));
            flnamevalue.setText(sharedPreferences.getString("Lastname", null));
            agevalue.setText(sharedPreferences.getString("age", null));
            emailvalue.setText(sharedPreferences.getString("email",null));
            phonevalue.setText(sharedPreferences.getString("phone",null));
            birthdatedisplay.setText(sharedPreferences.getString("birthday",null));
            countryandstatedisplay.setText(sharedPreferences.getString("countryandstate",null));
        }
        if (savedInstanceState!=null){
            fnamevalue.setText(savedInstanceState.getString("firstname"));
            flnamevalue.setText(savedInstanceState.getString("Lastname"));
            agevalue.setText(savedInstanceState.getString("age"));
            emailvalue.setText(savedInstanceState.getString("email"));
            phonevalue.setText(savedInstanceState.getString("phone"));
            birthdatedisplay.setText(savedInstanceState.getString("birthday"));
            countryandstatedisplay.setText(savedInstanceState.getString("countryandstate"));
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("firstname", fnamevalue.getText().toString());
        outState.putString("Lastname", flnamevalue.getText().toString());
        outState.putString("age", agevalue.getText().toString());
        outState.putString("email", emailvalue.getText().toString());
        outState.putString("phone", phonevalue.getText().toString());
        outState.putString("birthday", birthdatedisplay.getText().toString());
        outState.putString("countryandstate", countryandstatedisplay.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    //calling DateActivity
    public void dateset(View button){
        Intent set = new Intent(this,DateActivity.class);
        startActivityForResult(set,INTENT_REQUEST);
    }

    //called when the result is returned from either DateActivity or CountryStateActivity.
    protected void onActivityResult(int requestCode,int resultCode, Intent data){
        TextView birthdatedisplay = (TextView) findViewById(R.id.birthdatedisplay);
        TextView countryandstatedisplay = (TextView) findViewById(R.id.countryandstatedisplay);
        if(requestCode == INTENT_REQUEST) {


            switch (resultCode) {

                case RESULT_OK:
                    int day = data.getIntExtra("day", 0);
                    int month = data.getIntExtra("month", 0);
                    int year = data.getIntExtra("year", 0);
                    birthdatedisplay.setText(Integer.toString(month) + "/" + Integer.toString(day) + "/" + Integer.toString(year));
                    break;
                case RESULT_CANCELED:
                    Toast.makeText(getBaseContext(), "You have not set the date!", Toast.LENGTH_LONG).show();
            }
        }

        if(requestCode == INTENT_REQUEST_CS){

            switch (resultCode) {

                case RESULT_OK:
                    String country = data.getStringExtra("Countryname");
                    String state = data.getStringExtra("Statename");
                    countryandstatedisplay.setText(country+" , "+state);
                    break;
                case RESULT_CANCELED:
                    Toast.makeText(getBaseContext(), "You have not set the country and state!", Toast.LENGTH_LONG).show();
            }
        }
    }

    //calling CountryStateActivity
    public void countryandstateset(View Button){
        Intent set = new Intent(this,CountryStateActivity.class);
        startActivityForResult(set,INTENT_REQUEST_CS);
    }

    public void save(View v){
        SharedPreferences sharedPreferences = getSharedPreferences("BioData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("firstname", fnamevalue.getText().toString());
        editor.putString("Lastname", flnamevalue.getText().toString());
        editor.putString("age", agevalue.getText().toString());
        editor.putString("email", emailvalue.getText().toString());
        editor.putString("phone", phonevalue.getText().toString());
        editor.putString("birthday", birthdatedisplay.getText().toString());
        editor.putString("countryandstate", countryandstatedisplay.getText().toString());

        editor.commit();
    }

}
