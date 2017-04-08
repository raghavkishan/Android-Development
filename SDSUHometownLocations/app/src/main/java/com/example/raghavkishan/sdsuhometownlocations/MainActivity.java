package com.example.raghavkishan.sdsuhometownlocations;
/* reference for the entore application:
1)from: https://developer.android.com
2)Class slides
*/
/*
The application begins with this activity. The buttons should be used to post and view users.
 */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button postBtn,viewBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        postBtn = (Button) findViewById(R.id.post_user_button);
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPostActivity();
            }
        });

        viewBtn = (Button) findViewById(R.id.view_user_button);
        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToViewActivity();
            }
        });
    }

    public void goToPostActivity(){
        Intent set = new Intent(this,PostUser.class);
        startActivity(set);
    }

    public void goToViewActivity(){
        Intent set = new Intent(this,ViewUserFilterActivity.class);
        startActivity(set);
    }




}
