package com.mobilemakers.juansoler.appradar;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity implements ConnectionCallbacks,
        OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{

    private final static String TAG = MainActivity.class.getSimpleName();
    private final static int CONNECTION_TIMEOUT = 9000;
    private final static String RADARS_TABLE = "Radars";
    private final static String PARSE_LATITUDE = "latitude";
    private final static String PARSE_LONGITUDE = "longitude";
    private final static String PARSE_NAME = "name";
    private final static String PARSE_KM = "km";
    private final static String PARSE_MAXIMUM_SPEED = "max_speed";
    private final static String PARSE_DIRECTION = "direction";
    private final static int FIRST_FENCE = 5000;
    private final static int SECOND_FENCE = 2000;
    private final static int THIRD_FENCE = 300;
    public final static String RADARS_LIST = "radars_list";

    // Stores the PendingIntent used to request geofence monitoring.
    private PendingIntent mGeofenceRequestIntent;
    private GoogleApiClient mApiClient;

    NotificationPreference mNotification = new NotificationPreference();
    GeofenceTransitionsIntent mGeofenceTransition;
    List<SpotGeofence> mGeofenceList;
    RadarList mRadars = new RadarList();
    public static Location mLastLocation;
    StartScreenFragment mStartScreenFragment = new StartScreenFragment();

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //Location Services Intent
        mGeofenceTransition.handleTransition(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prepareFragment(savedInstanceState);
        showIconInActionBar();
        mGeofenceTransition = new GeofenceTransitionsIntent(this);
        initializeGooglePlayServices();
    }

    private void prepareFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mStartScreenFragment)
                    .commit();
        }
    }

    private void showIconInActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_color)));
    }
    
    private void initializeGooglePlayServices() {
        if (!isGooglePlayServicesAvailable()) {
            Log.e(TAG, "Google Play services unavailable.");
            return;
        }

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // Instantiate the current List of geofences.
        mGeofenceList = new ArrayList<>();
//        gettingParseObjectsFromNetwork();
        createGeofences();
        mApiClient.connect();
    }

    private void createGeofences() {
        gettingParseObjectsFromNetwork();
        gettingParseObjectsFromLocal();
        setFragmentArguments();
        preparingGeofenceList();
    }

    private void gettingParseObjectsFromNetwork() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(RADARS_TABLE);
        List<ParseObject> parseObjects;
        try {
            parseObjects = query.find();
            if (parseObjects.size() > 0) {
                for (int i = 0; i < parseObjects.size(); i++) {
                    try {
                        parseObjects.get(i).pin();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void gettingParseObjectsFromLocal() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(RADARS_TABLE);
        query.fromLocalDatastore();
        List<ParseObject> parseObjects;
        try {
            parseObjects = query.find();
            Radar radar;
            for (int i = 0; i < parseObjects.size(); i++){
                radar = new Radar();
                radar.setLatitude(parseObjects.get(i).getNumber(PARSE_LATITUDE).doubleValue());
                radar.setLongitude(parseObjects.get(i).getNumber(PARSE_LONGITUDE).doubleValue());
                radar.setName(parseObjects.get(i).getString(PARSE_NAME));
                radar.setKm(parseObjects.get(i).getNumber(PARSE_KM).floatValue());
                radar.setMaxSpeed(parseObjects.get(i).getNumber(PARSE_MAXIMUM_SPEED).intValue());
                radar.setDirection(parseObjects.get(i).getNumber(PARSE_DIRECTION).intValue());
                mRadars.add(radar);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setFragmentArguments() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(RADARS_LIST, mRadars);
        mStartScreenFragment.setArguments(bundle);
    }

    private void preparingGeofenceList() {
        int id = 0;
        float radius = 0;
        SpotGeofence spotGeofence;
        for (int i = 0; i < mRadars.size(); i++) {
            Radar radar = mRadars.get(i);
            Double latitude = radar.getLatitude();
            Double longitude = radar.getLongitude();
            String name = radar.getName();
            Float km = radar.getKm();
            int maxSpeed = radar.getMaxSpeed();
            int direction = radar.getDirection();
            for (int j = 0; j < 3; j++) {
                spotGeofence = new SpotGeofence();
                spotGeofence.setId(Integer.toString(id));
                spotGeofence.setLatitude(latitude);
                spotGeofence.setLongitude(longitude);
                spotGeofence.setName(name);
                spotGeofence.setKm(km);
                spotGeofence.setMaxSpeed(maxSpeed);
                spotGeofence.setDirection(direction);
                switch (j){
                    case 0:
                        radius = FIRST_FENCE;
                        break;
                    case 1:
                        radius = SECOND_FENCE;
                        break;
                    case 2:
                        radius = THIRD_FENCE;
                        break;
                }
                spotGeofence.setRadius(radius);
                mGeofenceList.add(spotGeofence);
                id++;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNotification.getSharedPreferences(this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mApiClient);
        // Get the PendingIntent for the geofence monitoring request.
        // Send a request to add the current geofences.
        mGeofenceRequestIntent = getGeofenceTransitionPendingIntent();

        List<Geofence> geoFenceListForLocationServices = new ArrayList<>();
        for (int i = 0; i < mGeofenceList.size(); i++) {
            SpotGeofence spotGeofence = mGeofenceList.get(i);
            geoFenceListForLocationServices.add(spotGeofence.toGeofence());
        }

        LocationServices.GeofencingApi.addGeofences(mApiClient, geoFenceListForLocationServices,
                mGeofenceRequestIntent);

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // If the error has a resolution, start a Google Play services activity to resolve it.
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_TIMEOUT);
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, "Exception while resolving connection error.", e);
            }
        } else {
            int errorCode = connectionResult.getErrorCode();
            Log.e(TAG, "Connection to Google Play services failed with error code " + errorCode);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (null != mGeofenceRequestIntent) {
            LocationServices.GeofencingApi.removeGeofences(mApiClient, mGeofenceRequestIntent);
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == resultCode) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Google Play services is available.");
            }
            return true;
        } else {
            Log.e(TAG, "Google Play services is unavailable.");
            return false;
        }
    }

    private PendingIntent getGeofenceTransitionPendingIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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

    @Override
    protected void onDestroy() {
        mApiClient.disconnect();
        super.onDestroy();
    }
}
