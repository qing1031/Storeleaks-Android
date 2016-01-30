package com.sarahproto.storeleaks;

import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.sarahproto.storeleaks.Api.StoreleaksAPIClient;

public class StoreleaksApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        new Runnable() {
            @Override
            public void run() {
                StoreleaksAPIClient.init();

                Log.d("OK", "Ok");
            }
        }.run();
    }
}