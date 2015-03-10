package com.mobilemakers.juansoler.appradar;

import android.app.Application;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.parse.Parse;

public class AppRadarApplication  extends Application {

    static final String APPLICATION_ID = "7P8k5rZtpTzL29BqPhsIFqMrD9T0Qg7MIT1VYzfJ";
    static final String CLIENT_KEY = "FVtaE3Ur3M4AhZuPvvkXZyiRlZhLgRAGqB0GcZt6";

    private GoogleApiClient mApiClient;

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

    public GoogleApiClient getApiClient() {
        return mApiClient;
    }

    public void setApiClient(GoogleApiClient apiClient) {
        mApiClient = apiClient;
    }
}
