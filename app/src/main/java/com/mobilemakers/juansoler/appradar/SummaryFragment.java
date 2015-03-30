package com.mobilemakers.juansoler.appradar;


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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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

    private Location mLocation;
    private LocationListener mLocationListener;
    private LocationManager mLocationManager;

    public SummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void handleTransition(Intent intent) {
        mGeofenceTransition.handleTransition(intent, mRadars);
    }

    @Override
    public void doBack() {
        endTrip();
    }

    private void endTrip() {
        AlertDialogWrapper.Builder builder = new AlertDialogWrapper.Builder(getActivity());
        builder.setMessage(getResources().getString(R.string.end_trip))
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
                });
        final android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_summary, container, false);
        LinearLayout layoutMain = (LinearLayout)rootView.findViewById(R.id.Layout_Main);
        Transitions.fadeIN(layoutMain, Constants.TRANSITION_DURATION_2K);
        wireUpViews(rootView);
        refreshSpeed();
        mGeofenceTransition = new GeofenceTransitionsIntent(getActivity());
        return rootView;
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
        setScreenInformation();
    }

    private void wireUpViews(View rootView) {
        mTextViewDistance = (TextView) rootView.findViewById(R.id.text_view_distance);
        mTextViewSpeedLimitValue = (TextView) rootView.findViewById(R.id.text_view_speed_limit_value);
        mTextViewNameRadar = (TextView) rootView.findViewById(R.id.text_view_radar_name);
        mTextViewKmRadar = (TextView) rootView.findViewById(R.id.text_view_radar_km);
        mTextViewSpeedValue = (TextView)rootView.findViewById(R.id.text_view_speed_value);
        mTextViewSpeedValue.setText(String.format(getString(R.string.text_view_speed_value), 0f));
    }

    private void getFragmentArguments() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(Constants.RADARS_LIST)) {
            mRadars = bundle.getParcelable(Constants.RADARS_LIST);
        }
    }
    
    private void refreshSpeed() {
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
                float speed;
                if (mLocation == null) {
                    speed = 0;
                } else {
                    speed = getSpeed(location);
                }
                setScreenInformation();
                speedNotification(speed);
                mTextViewSpeedValue.setText(String.format(getString(R.string.text_view_speed_value), speed));
                mLocation = location;
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

            private float getSpeed(Location endLoc) {
                float distM;
                long timeS;
                //Use provided speed, if it exists
                if (endLoc.hasSpeed()) {
                    return endLoc.getSpeed() * Constants.SPEED_CONVERSION;
                }
                //Get time difference is seconds
                timeS = getTimeDifference(endLoc);
                //Get distance traveled in meters
                distM = mLocation.distanceTo(endLoc);

                return (distM / timeS) * Constants.SPEED_CONVERSION;
            }

            private long getTimeDifference(Location endLoc) {
                long timeMS;
                timeMS = TimeUnit.NANOSECONDS.toSeconds(mLocation.getTime() - endLoc.getTime());
                return timeMS;
            }

            private void speedNotification(float currentSpeed) {
                float distanceNotification = NotificationPreference.getSecondNotificationDistance(getActivity());
                if ((currentSpeed > mMaxSpeed) && (mDistance <= distanceNotification)){
                    MediaPlayer player = MediaPlayer.create(getActivity(), R.raw.heal);
                    player.start();
                }
            }
        };
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.RADARS_LIST, mRadars);
        outState.putFloat(Constants.MAX_SPEED, mMaxSpeed);
        outState.putFloat(Constants.DISTANCE, mDistance);
    }

    private void setScreenInformation() {
        try {
            Radar nextRadar = mRadars.getNextRadar();
            setNameAndKilometer(nextRadar);
            setDistance(nextRadar);
            setMaxSpeed(nextRadar);
            setRefreshTime();
        } catch (NullPointerException e){
            mTextViewDistance.setText(getString(R.string.distance_error_no_gps));
            mTextViewSpeedLimitValue.setText(getString(R.string.speed_error_no_gps));
        }
    }

    private void setNameAndKilometer(Radar nextRadar){
        mTextViewNameRadar.setText(nextRadar.getName());
        mTextViewKmRadar.setText(String.format(getString(R.string.text_view_info_radar),nextRadar.getKm()));
    }

    private void setRefreshTime(){
        String time = getCurrentTime();
        setActionBarSubtitle(String.format(getString(R.string.text_view_refresh_time_text), time));
    }

    private void setActionBarSubtitle(String subtitle){
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setSubtitle(subtitle);
    }

    private void setDistance(Radar nextRadar) throws NullPointerException{
        mDistance = calculateDistanceToNextRadar(nextRadar);
        float distance = mDistance/1000;
        mTextViewDistance.setText(String.format(getString(R.string.text_view_distance_value), distance));
    }

    private float calculateDistanceToNextRadar(Radar nextRadar) throws NullPointerException {
        Location currentLocation = getLastLocation();
        Location nextRadarLocation = createNextRadarLocation(nextRadar);
        return currentLocation.distanceTo(nextRadarLocation);
    }

    private Location getLastLocation() {
        AppRadarApplication state = (AppRadarApplication)getActivity().getApplicationContext();
        GoogleApiClient apiClient = state.getApiClient();
        return LocationServices.FusedLocationApi.getLastLocation(apiClient);
    }

    private Location createNextRadarLocation(Radar nextRadar) {
        Location nextRadarLocation = new Location(Constants.NEXT_LOCATION);
        nextRadarLocation.setLongitude(nextRadar.getLongitude());
        nextRadarLocation.setLatitude(nextRadar.getLatitude());
        return nextRadarLocation;
    }

    private void setMaxSpeed(Radar nextRadar) {
        mTextViewSpeedLimitValue.setText(String.format(getString(R.string.text_view_speed_limit_value_text), nextRadar.getMaxSpeed()));
        mMaxSpeed = nextRadar.getMaxSpeed();
    }

    private String getCurrentTime () {
        DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.US);
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
                //Inexact speed calculation
                mTextViewSpeedValue.setText(getString(R.string.speed_error_no_gps));
                break;
            case R.id.action_show_map:
                Intent intent = new Intent(getActivity(), MapActivity.class);
                intent.putExtra(Constants.RADARS_LIST, mRadars);
                startActivity(intent);
                handled = true;
                break;
        }

        if (!handled) {
            handled = super.onOptionsItemSelected(item);
        }
        return handled;
    }

}
