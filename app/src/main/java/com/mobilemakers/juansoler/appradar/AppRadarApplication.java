package com.mobilemakers.juansoler.appradar;

import android.app.Application;

import com.google.android.gms.common.api.GoogleApiClient;
import com.parse.Parse;

public class AppRadarApplication  extends Application {

    private GoogleApiClient mApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeParse();
    }

    private void initializeParse() {
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, Constants.APPLICATION_ID, Constants.CLIENT_KEY);
    }

    public GoogleApiClient getApiClient() {
        return mApiClient;
    }

    public void setApiClient(GoogleApiClient apiClient) {
        mApiClient = apiClient;
    }
}
