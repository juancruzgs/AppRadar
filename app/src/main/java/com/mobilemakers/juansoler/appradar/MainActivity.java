package com.mobilemakers.juansoler.appradar;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import java.util.Date;
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
    private final static String PARSE_UPDATED_AT = "updatedAt";
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
        gettingParseObjects();
        setFragmentArguments();
        preparingGeofenceList();
    }

    private void gettingParseObjects() {
        try {
            if (isNetworkAvailable() && !isLocalDatabaseUpdated()) {
                gettingParseObjectsFromNetwork();
            } else {
                gettingParseObjectsFromLocal();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private boolean isLocalDatabaseUpdated() throws ParseException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(RADARS_TABLE);
        ParseObject parseObject;
        query.orderByDescending(PARSE_UPDATED_AT);

        parseObject = query.getFirst();
        Date cloudDate = parseObject.getUpdatedAt();

        query.fromLocalDatastore();
        parseObject = query.getFirst();
        Date localDate = parseObject.getUpdatedAt();

        return localDate.compareTo(cloudDate) == 0;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void gettingParseObjectsFromNetwork() throws ParseException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(RADARS_TABLE);
        List<ParseObject> parseObjects;
            parseObjects = query.find();
            Radar radar;
            ParseObject parseObject;
            for (int i = 0; i < parseObjects.size(); i++) {
                parseObject = parseObjects.get(i);
                radar = createRadarFromParse(parseObject);
                mRadars.add(radar);
                //Save to local database
                parseObject.pin();
            }
        }

    private void gettingParseObjectsFromLocal() throws ParseException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(RADARS_TABLE);
        query.fromLocalDatastore();
        List<ParseObject> parseObjects;
        parseObjects = query.find();
        Radar radar;
        ParseObject parseObject;
        for (int i = 0; i < parseObjects.size(); i++) {
            parseObject = parseObjects.get(i);
            radar = createRadarFromParse(parseObject);
            mRadars.add(radar);
        }
    }

    private Radar createRadarFromParse(ParseObject parseObject) {
        Radar radar = new Radar();
        radar.setLatitude(parseObject.getNumber(PARSE_LATITUDE).doubleValue());
        radar.setLongitude(parseObject.getNumber(PARSE_LONGITUDE).doubleValue());
        radar.setName(parseObject.getString(PARSE_NAME));
        radar.setKm(parseObject.getNumber(PARSE_KM).floatValue());
        radar.setMaxSpeed(parseObject.getNumber(PARSE_MAXIMUM_SPEED).intValue());
        radar.setDirection(parseObject.getNumber(PARSE_DIRECTION).intValue());
        return radar;
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
