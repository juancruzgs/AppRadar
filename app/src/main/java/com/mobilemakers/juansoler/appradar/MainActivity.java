package com.mobilemakers.juansoler.appradar;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends ActionBarActivity implements ConnectionCallbacks,
        OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{

    private final static String TAG = MainActivity.class.getSimpleName();
    private final static String PARSE_LATITUDE = "latitud";
    private final static String PARSE_LONGITUDE = "longitud";
    private final static String PARSE_ID = "id";
    private final static String PARSE_NAME = "nombre";
    private final static String PARSE_KM = "km";
    private final static String PARSE_MAXIMUM_SPEED = "velocidad_maxima";
    private final static String PARSE_DIRECTION = "direccion";
    private final static int FIRST_FENCE = 5000;
    private final static int SECOND_FENCE = 2000;
    private final static int THIRD_FENCE = 300;
    // Stores the PendingIntent used to request geofence monitoring.
    private PendingIntent mGeofenceRequestIntent;
    private GoogleApiClient mApiClient;
    List<Geofence> mGeofenceList;
    NotificationPreference mNotification = new NotificationPreference();
    GeofenceTransitionsIntent geofenceTransition;
    ArrayList<ParseObject> mRadares;


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //Location Services Intent
        geofenceTransition.handleTransition(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prepareFragment(savedInstanceState);
        showIconInActionBar();

        geofenceTransition = new GeofenceTransitionsIntent(this);

        final ParseObject radares = new ParseObject("Radar");
        mGeofenceList = new ArrayList<Geofence>();
        final ArrayList<ParseObject> radaresOnParse;
        gettingParseObjectsFromNetwork();
        int id = 0;
        for (int i = 0; i < mRadares.size(); i++) {
            SpotGeofence spotGeofence;
            ParseObject p = mRadares.get(i);
            String latitude = p.getString(PARSE_LATITUDE);
            String longitude = p.getString(PARSE_LONGITUDE);
            String name = p.getString(PARSE_NAME);
            String km = p.getString(PARSE_KM);
            int max_speed = p.getInt(PARSE_MAXIMUM_SPEED);
            int direction = p.getInt(PARSE_DIRECTION);
            for (int j = 0; j < 3; j++) {
                switch (j){
                    case 0:
                        spotGeofence = new SpotGeofence(Integer.toString(id),
                                Double.parseDouble(latitude),
                                Double.parseDouble(longitude),
                                FIRST_FENCE);
                        break;
                    case 1:
                        spotGeofence = new SpotGeofence(Integer.toString(id),
                                Double.parseDouble(latitude),
                                Double.parseDouble(longitude),
                                SECOND_FENCE);
                        break;
                    default:
                        spotGeofence = new SpotGeofence(Integer.toString(id),
                                Double.parseDouble(latitude),
                                Double.parseDouble(longitude),
                                THIRD_FENCE);
                        break;
                }
                mGeofenceList.add(spotGeofence.toGeofence());
            }
            saveRadarOnLocalParse(id, name, km, max_speed, direction, radares);
            id++;
        }




        //initializeGooglePlayServices();
    }

    private void gettingParseObjectsFromNetwork() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Radares");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                SpotGeofence spotGeofence;
                if (parseObjects.size() > 0) {
                    int id = 0;
                    for (int i = 0; i < parseObjects.size(); i++) {
                        mRadares.add(parseObjects.get(i));
                    }
                }
            }});
    }

    private void saveRadarOnLocalParse(int id, String name, String km, int max_speed, int direction, ParseObject radares) {
        radares.put(PARSE_ID, id);
        radares.put(PARSE_NAME, name);
        radares.put(PARSE_KM, km);
        radares.put(PARSE_MAXIMUM_SPEED, max_speed);
        radares.put(PARSE_DIRECTION, direction);
        radares.pinInBackground();
    }

    private void prepareFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new StartScreenFragment())
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
            finish();
            return;
        }

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mApiClient.connect();

        // Instantiate the current List of geofences.
        mGeofenceList = new ArrayList<>();
        createGeofences();
    }

    public void createGeofences() {
        // Create internal "flattened" objects containing the geofence data.

    }

    @Override
    protected void onResume() {
        super.onResume();
        mNotification.getSharedPreferences(this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Get the PendingIntent for the geofence monitoring request.
        // Send a request to add the current geofences.
        mGeofenceRequestIntent = getGeofenceTransitionPendingIntent();
        LocationServices.GeofencingApi.addGeofences(mApiClient, mGeofenceList,
                mGeofenceRequestIntent);
        finish();
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // If the error has a resolution, start a Google Play services activity to resolve it.
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, 9000);
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
}
