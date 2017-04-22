package com.example.raghavkishan.sdsuhometownchat;

import android.app.Application;

import com.firebase.client.Firebase;


/**
 * Created by raghavkishan on 4/16/2017.
 */

public class setContextFirebase extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
