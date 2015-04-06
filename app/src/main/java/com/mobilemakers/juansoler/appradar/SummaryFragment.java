package com.mobilemakers.juansoler.appradar;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SummaryFragment extends Fragment implements MainActivity.onHandleTransition, MainActivity.OnBackPressedListener{

    private TextView mTextViewDistance;
    private TextView mTextViewSpeedLimitValue;
    private TextView mTextViewNameRadar;
    private TextView mTextViewKmRadar;
    private TextView mTextViewSpeedValue;

    private float mMaxSpeed;
    private float mDistance;

    private RadarList mRadars;
    private GeofenceTransitionsIntent mGeofenceTransition;

    private LocationListener mLocationListener;
    private LocationManager mLocationManager;

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
        Transitions.fadeIN(layoutMain, Constants.TRANSITION_DURATION_2K);
        wireUpViews(rootView);
        prepareButtons(rootView);
        setLocationUpdates();
        mGeofenceTransition = new GeofenceTransitionsIntent(getActivity());
        return rootView;
    }

    private void wireUpViews(View rootView) {
        mTextViewDistance = (TextView) rootView.findViewById(R.id.text_view_distance);
        mTextViewSpeedLimitValue = (TextView) rootView.findViewById(R.id.text_view_speed_limit_value);
        mTextViewNameRadar = (TextView) rootView.findViewById(R.id.text_view_radar_name);
        mTextViewKmRadar = (TextView) rootView.findViewById(R.id.text_view_radar_km);
        mTextViewSpeedValue = (TextView)rootView.findViewById(R.id.text_view_speed_value);
        mTextViewSpeedValue.setText(String.format(getString(R.string.text_view_speed_value), 0f));
    }

    private void prepareButtons(View rootView) {
        Button buttonRefresh = (Button) rootView.findViewById(R.id.button_refresh);
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location lastLocation = getLastLocation();
                updateDisplayedInformation(lastLocation);
            }
        });

        Button buttonMap = (Button) rootView.findViewById(R.id.button_map);
        buttonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapActivity.class);
                intent.putExtra(Constants.RADARS_LIST, mRadars);
                startActivity(intent);
            }
        });
    }

    private Location getLastLocation() {
        AppRadarApplication state = (AppRadarApplication)getActivity().getApplicationContext();
        GoogleApiClient apiClient = state.getApiClient();
        return LocationServices.FusedLocationApi.getLastLocation(apiClient);
    }

    private void updateDisplayedInformation(Location location) {
        if (location != null) {
            Radar nextRadar = mRadars.getNextRadar();
            setNameAndKilometer(nextRadar);
            setDistance(nextRadar, location);
            setCurrentSpeed(location);
            setMaxSpeed(nextRadar);
            setRefreshTime();
        }
        else {
            setActionBarSubtitle(getString(R.string.error_no_gps));
            mTextViewDistance.setText(getString(R.string.speed_error_no_gps));
            mTextViewSpeedValue.setText(getString(R.string.speed_error_no_gps));
            mTextViewSpeedLimitValue.setText(getString(R.string.speed_error_no_gps));
        }
    }

    private void setNameAndKilometer(Radar nextRadar){
        mTextViewNameRadar.setText(nextRadar.getName());
        mTextViewKmRadar.setText(String.format(getString(R.string.text_view_info_radar), nextRadar.getKm()));
    }

    private void setDistance(Radar nextRadar, Location location){
        mDistance = calculateDistanceToNextRadar(nextRadar, location);
        float distance = mDistance/1000;
        mTextViewDistance.setText(String.format(getString(R.string.text_view_distance_value), distance));
    }

    private float calculateDistanceToNextRadar(Radar nextRadar, Location currentLocation){
        Location nextRadarLocation = createNextRadarLocation(nextRadar);
        return currentLocation.distanceTo(nextRadarLocation);
    }

    private void setCurrentSpeed(Location location) {
        float speed = location.getSpeed() * Constants.SPEED_CONVERSION;
        speedNotification(speed);
        mTextViewSpeedValue.setText(String.format(getString(R.string.text_view_speed_value), speed));
    }

    private void setMaxSpeed(Radar nextRadar) {
        mTextViewSpeedLimitValue.setText(String.format(getString(R.string.text_view_speed_limit_value_text), nextRadar.getMaxSpeed()));
        mMaxSpeed = nextRadar.getMaxSpeed();
    }

    private void setRefreshTime(){
        String time = getCurrentTime();
        setActionBarSubtitle(String.format(getString(R.string.text_view_refresh_time_text), time));
    }

    private String getCurrentTime () {
        DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.US);
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void setActionBarSubtitle(String subtitle){
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setSubtitle(subtitle);
    }

    private Location createNextRadarLocation(Radar nextRadar) {
        Location nextRadarLocation = new Location(Constants.NEXT_LOCATION);
        nextRadarLocation.setLongitude(nextRadar.getLongitude());
        nextRadarLocation.setLatitude(nextRadar.getLatitude());
        return nextRadarLocation;
    }

    private void speedNotification(float currentSpeed) {
        float distanceNotification = NotificationPreference.getSecondNotificationDistance(getActivity());
        if ((currentSpeed > mMaxSpeed) && (mDistance <= distanceNotification)){
            MediaPlayer player = MediaPlayer.create(getActivity(), R.raw.heal);
            player.start();
        }
    }

    private void setLocationUpdates() {
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (mLocationListener == null) {
            setLocationListener();
        }
        long refreshTime = NotificationPreference.getRefreshTime(getActivity());
        if (refreshTime != 0) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    refreshTime,
                    Constants.MIN_DISTANCE_UPDATES,
                    mLocationListener);
        }
    }

    public void setLocationListener() {
        mLocationListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateDisplayedInformation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState!=null){
            mRadars = savedInstanceState.getParcelable(Constants.RADARS_LIST);
            mMaxSpeed = savedInstanceState.getFloat(Constants.MAX_SPEED);
            mDistance = savedInstanceState.getFloat(Constants.DISTANCE);
        } else {
            getFragmentArguments();
        }
        Location lastLocation = getLastLocation();
        updateDisplayedInformation(lastLocation);
    }

    private void getFragmentArguments() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(Constants.RADARS_LIST)) {
            mRadars = bundle.getParcelable(Constants.RADARS_LIST);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.RADARS_LIST, mRadars);
        outState.putFloat(Constants.MAX_SPEED, mMaxSpeed);
        outState.putFloat(Constants.DISTANCE, mDistance);
        //TODO Save Actual Speed
    }

    @Override
    public void doBack() {
        endTrip();
    }

    private void endTrip() {
        AlertDialog.Builder endTripDialogBuilder = new AlertDialog.Builder(getActivity());
        endTripDialogBuilder.setMessage(getResources().getString(R.string.end_trip))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int id) {
                        mLocationManager.removeUpdates(mLocationListener);
                        setActionBarSubtitle("");
                        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationManager.removeUpdates(mLocationListener);
    }
}
