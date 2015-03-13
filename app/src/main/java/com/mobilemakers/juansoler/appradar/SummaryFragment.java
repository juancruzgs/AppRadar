package com.mobilemakers.juansoler.appradar;


import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SummaryFragment extends Fragment implements MainActivity.onHandleTransition  {

    private TextView mTextViewDistance;
    private TextView mTextViewRefreshTime;
    private TextView mTextViewSpeedLimitValue;

    private RadarList mRadars;
    private GeofenceTransitionsIntent mGeofenceTransition;

    public SummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void handleTransition(Intent intent) {
        mGeofenceTransition.handleTransition(intent, mRadars);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_summary, container, false);
        LinearLayout layoutMain = (LinearLayout)rootView.findViewById(R.id.Layout_Main);
        Transitions.fadeIN(layoutMain, Constants.TRANSIION_DURATION_2K);
        wireUpViews(rootView);
        getFragmentArguments();
//        monitorGpsStatus();
        setScreenInformation();
        mGeofenceTransition = new GeofenceTransitionsIntent(getActivity());
        return rootView;
    }

    private void wireUpViews(View rootView) {
        mTextViewDistance = (TextView) rootView.findViewById(R.id.text_view_distance);
        mTextViewRefreshTime = (TextView) rootView.findViewById(R.id.text_view_refresh_time);
        mTextViewSpeedLimitValue = (TextView) rootView.findViewById(R.id.text_view_speed_limit_value);
    }

    private void getFragmentArguments() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(Constants.RADARS_LIST)) {
            mRadars = bundle.getParcelable(Constants.RADARS_LIST);
        }
    }

//    private void monitorGpsStatus() {
//        LocationManager lm = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
//        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, Constants.MIN_TIME_UPDATES_S, Constants.MIN_DISTANCE_UPDATES_M,
//                new LocationListener() {
//                    @Override
//                    public void onLocationChanged(Location location) {
//
//                    }
//
//                    @Override
//                    public void onStatusChanged(String provider, int status, Bundle extras) {
//
//                    }
//
//                    @Override
//                    public void onProviderEnabled(String provider) {
//                    }
//
//                    @Override
//                    public void onProviderDisabled(String provider) {
//                    }
//                });
//    }

    private void setScreenInformation() {
        setDistance();
        setMaxSpeed();
        setRefreshTime();
    }

    private void setDistance() {
        float distance = calculateDistanceToNextRadar();
        mTextViewDistance.setText(String.format(getString(R.string.text_view_distance_value), Float.toString(distance)));
    }

    private float calculateDistanceToNextRadar() {
        Location currentLocation = getLastLocation();
        Location nextRadarLocation = createNextRadarLocation();
        float distance = (currentLocation.distanceTo(nextRadarLocation)/1000);
        return new BigDecimal(distance).setScale(1,BigDecimal.ROUND_HALF_UP).floatValue();
    }

    private Location getLastLocation() {
        AppRadarApplication state = (AppRadarApplication)getActivity().getApplicationContext();
        GoogleApiClient apiClient = state.getApiClient();
        return LocationServices.FusedLocationApi.getLastLocation(apiClient);
    }

    private Location createNextRadarLocation() {
        Location nextRadarLocation = new Location(Constants.NEXT_LOCATION);
        nextRadarLocation.setLongitude(mRadars.getNextRadar().getLongitude());
        nextRadarLocation.setLatitude(mRadars.getNextRadar().getLatitude());
        return nextRadarLocation;
    }

    private void setMaxSpeed() {
        mTextViewSpeedLimitValue.setText(String.format(getString(R.string.text_view_speed_limit_value_text), mRadars.getNextRadar().getMaxSpeed()));
    }

    private void setRefreshTime() {
        String refreshTime = getCurrentTime();
        mTextViewRefreshTime.setText(String.format(getString(R.string.text_view_refresh_time_text), refreshTime));
    }

    private String getCurrentTime () {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.US);
        Date date = new Date();
        return dateFormat.format(date);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_summary, menu);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Boolean handled = false;

        switch(id) {
            case R.id.action_bar:
                handled = true;
                break;
            case R.id.action_refresh:
                handled = true;
                setScreenInformation();
                break;
        }

        if (!handled) {
            handled = super.onOptionsItemSelected(item);
        }
        return handled;
    }

}
