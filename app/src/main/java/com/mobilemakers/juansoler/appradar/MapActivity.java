package com.mobilemakers.juansoler.appradar;

import android.content.Context;
import android.location.Location;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Iterator;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class MapActivity extends ActionBarActivity implements OnMapReadyCallback, LocationListener {

    private RadarList mRadars;
    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mActionBar = getSupportActionBar();
        mActionBar.setIcon(R.mipmap.ic_launcher);
        mActionBar.setDisplayShowHomeEnabled(true);

        mRadars = getIntent().getExtras().getParcelable(Constants.RADARS_LIST);

        if (savedInstanceState == null) {
            Toast.makeText(this, getString(R.string.message_map_toast), Toast.LENGTH_LONG).show();
            SupportMapFragment mMapFragment = SupportMapFragment.newInstance();
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.container, mMapFragment);
            fragmentTransaction.commit();
            mMapFragment.getMapAsync(this);
        }

        showIconInActionBar();
        checkConnection();
    }

    private void checkConnection() {
        TelephonyManager myTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        PhoneStateListener callStateListener = new PhoneStateListener() {
            public void onDataConnectionStateChanged(int state) {
                String message = "";
                switch (state) {
                    case TelephonyManager.DATA_DISCONNECTED:
                       message ="No internet connection...";
                        break;
                    case TelephonyManager.DATA_CONNECTING:
                        message = "Reconnecting...";
                        break;
                    case TelephonyManager.DATA_CONNECTED:
                        message = "Connected";
                        break;
                }
                Crouton.makeText(MapActivity.this, message, Style.ALERT).show();
            }
        };
        myTelephonyManager.listen(callStateListener,
                PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Crouton.cancelAllCroutons();
    }

    @Override
    public void onLocationChanged(Location location) {
        refreshMap(location);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMyLocationEnabled(true);
        createLocationRequest();
        refreshMap(getLocation());
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        addMarkerForEachRadar(mGoogleMap);
    }

    private void refreshMap(Location location) {
        if (location != null) {
            LatLng latLong = new LatLng(location.getLatitude(), location.getLongitude());
            CameraPosition cameraPosition = getCameraPosition(latLong);

            // Animate the change in camera view over 2 seconds
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                    2000, null);
        }
    }

    private CameraPosition getCameraPosition(LatLng latLong) {
        return CameraPosition.builder()
                .target(latLong)
                .zoom(17)
                .bearing(90)
                .build();
    }

    private Location getLocation() {
        AppRadarApplication state = (AppRadarApplication) getApplicationContext();
        mGoogleApiClient = state.getApiClient();
        return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Constants.UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(Constants.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void addMarkerForEachRadar(GoogleMap googleMap) {
        Iterator iterator = mRadars.iterator();
        while (iterator.hasNext()) {
            Radar radar = (Radar) iterator.next();
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(radar.getLatitude(), radar.getLongitude()))
                    .title(String.format(getString(R.string.map_activity_km), radar.getName(), radar.getKm())));
        }
    }

    private void showIconInActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setDisplayShowHomeEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

