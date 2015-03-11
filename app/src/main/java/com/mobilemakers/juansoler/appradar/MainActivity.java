package com.mobilemakers.juansoler.appradar;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import java.util.List;

public class MainActivity extends ActionBarActivity implements StartScreenFragment.onHandleTransition{

    private GeofenceTransitionsIntent mGeofenceTransition;
    private List<SpotGeofence> mGeofenceList;

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
        mGeofenceTransition.handleTransition(intent, mGeofenceList);
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
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_color)));
    }

    @Override
    public void getGeofenceList(List<SpotGeofence> spotGeofences) {
        mGeofenceList = spotGeofences;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent iSettings = new Intent(this, SettingsActivity.class);
            startActivity(iSettings);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
