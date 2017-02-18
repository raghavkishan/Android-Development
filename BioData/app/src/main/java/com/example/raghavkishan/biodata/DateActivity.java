package com.example.raghavkishan.biodata;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.view.View;

public class DateActivity extends AppCompatActivity {

    DatePicker datepicker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);
        datepicker = (DatePicker) findViewById(R.id.date_picker);
        Button setbutton = (Button) findViewById(R.id.date_done_button);
        Button cancelbutton = (Button) findViewById(R.id.date_cancel_button);

        setbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int day = datepicker.getDayOfMonth();
                int month = datepicker.getMonth() + 1;
                int year = datepicker.getYear();
                Intent toPassBack = getIntent();
                toPassBack.putExtra("day",day);
                toPassBack.putExtra("month",month);
                toPassBack.putExtra("year",year);
                setResult(RESULT_OK,toPassBack);
                finish();
            }
        });

        cancelbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }

        });
    }

}
