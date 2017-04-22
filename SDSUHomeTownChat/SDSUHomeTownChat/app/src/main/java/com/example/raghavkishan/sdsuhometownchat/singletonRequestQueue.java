package com.example.raghavkishan.sdsuhometownchat;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


/**
 * Created by raghavkishan on 4/6/2017.
 */


public class singletonRequestQueue {

    private static singletonRequestQueue mInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;

    private singletonRequestQueue(Context context) {
        mContext = context;
        mRequestQueue = queue();
    }
    public static synchronized singletonRequestQueue instance(Context context) {
        if ( mInstance == null ) {
            mInstance = new singletonRequestQueue(context);
        }
        return mInstance;
    }

    public RequestQueue queue() {
        if ( mRequestQueue == null ) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void add(Request<T> req) {
        queue().add(req);
    }

}
