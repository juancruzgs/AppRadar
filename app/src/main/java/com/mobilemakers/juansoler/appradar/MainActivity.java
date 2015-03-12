package com.mobilemakers.juansoler.appradar;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity implements StartScreenFragment.onHandleTransition{

    private GeofenceTransitionsIntent mGeofenceTransition;
    private RadarList mRadars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGeofenceTransition = new GeofenceTransitionsIntent(this);
        prepareFragment(savedInstanceState);
        showIconInActionBar();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //Location Services Intent
        mGeofenceTransition.handleTransition(intent, mRadars);
    }

    private void prepareFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new StartScreenFragment(),Constants.START_SCREEN_FRAGMENT_TAG)
                    .commit();
        }
    }

    private void showIconInActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    public void getGeofenceList(RadarList radars) {
        mRadars = radars;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
