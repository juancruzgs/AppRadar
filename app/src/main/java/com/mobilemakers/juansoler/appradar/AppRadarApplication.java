package com.mobilemakers.juansoler.appradar;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.common.api.GoogleApiClient;
import com.parse.Parse;

public class AppRadarApplication  extends Application {

    private GoogleApiClient mApiClient;
    private SharedPreferences mPrefs;

    @Override
    public void onCreate() {
        super.onCreate();
        Context context = this.getApplicationContext();
        //0 = mode private. only this app can read these preferences
        mPrefs = context.getSharedPreferences("myAppPrefs", 0);
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

    public boolean getFirstRun() {
        return mPrefs.getBoolean("firstRun", true);
    }

    public void setRunned() {
        SharedPreferences.Editor edit = mPrefs.edit();
        edit.putBoolean("firstRun", false);
        edit.commit();
    }
}
