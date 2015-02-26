package com.mobilemakers.juansoler.appradar;

import android.app.Application;

import com.parse.Parse;

public class AppRadarApplication  extends Application {

    public static final String APPLICATION_ID = "7P8k5rZtpTzL29BqPhsIFqMrD9T0Qg7MIT1VYzfJ";
    public static final String CLIENT_KEY = "FVtaE3Ur3M4AhZuPvvkXZyiRlZhLgRAGqB0GcZt6";


    @Override
    public void onCreate() {
        super.onCreate();
        initializeParse();
    }

    private void initializeParse() {
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);
    }
}
