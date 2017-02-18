/*
An Android application that counts the number of times it goes into the background as well
as the number of times the Click button is clicked.
Author of program: Raghav Kishan.
*/

package com.example.raghavkishan.clickcount;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private static final String TAG ="MyActivity";
    Button btn;
    TextView clickscoreText;
    TextView backscoreText;
    int clickCounter = 0;               // Using 'clickscoreText' for click counts.
    int backgroundCounter = 0;          // Using 'backscoreText' for background counts.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate");
        btn = (Button)findViewById(R.id.button);
        clickscoreText = (TextView)findViewById(R.id.buttoncountvalue);
        backscoreText = (TextView)findViewById(R.id.backgroundcountvalue);
        btn.setOnClickListener(new View.OnClickListener(){                      // Using OnClickListener to calculate the number of times the button 'Click' is clicked.
            public void onClick(View v){
                clickCounter++;
                clickscoreText.setText(Integer.toString(clickCounter));
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }
                                                        // referred from: https://developer.android.com/reference/android/app/Activity.html
    @Override                                           //referred  from: Blackboard discussion: "Thread: Background question"
    protected void onPause() {                            // Using "onPause" to calculate background count. The value is incremented when the application enters the background.
        super.onPause();
        if(!(isChangingConfigurations())){              // Using "isChangingConfigurations" to prevent increment when orientation changes in the foreground.
        backgroundCounter++;
        backscoreText.setText(Integer.toString(backgroundCounter));
        Log.d(TAG,"onPause");}
    }


    @Override
    protected void onResume() {                                     //Using OnResume to display values during orientation change.
        super.onResume();
        clickscoreText.setText(Integer.toString(clickCounter));
        backscoreText.setText(Integer.toString(backgroundCounter));
        Log.d(TAG, "onResume");
    }


   public void onSaveInstanceState(Bundle outState){               // Using "onSaveInstanceState" and "onRestoreInstanceState" to handle values during
        super.onSaveInstanceState(outState);                          // orientation change.
        outState.putInt("clickcounter",clickCounter);
        outState.putInt("backgroundcounter",backgroundCounter);
        Log.d(TAG, "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState");
        clickCounter = savedInstanceState.getInt("clickcounter");
        backgroundCounter = savedInstanceState.getInt("backgroundcounter");
        Log.d(TAG,Integer.toString(backgroundCounter));
    }
}
