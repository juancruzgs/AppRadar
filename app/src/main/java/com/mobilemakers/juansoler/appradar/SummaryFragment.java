package com.mobilemakers.juansoler.appradar;



import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SummaryFragment extends Fragment implements MainActivity.onHandleTransition, MainActivity.OnBackPressedListener{

    private TextView mTextViewDistance;
    private TextView mTextViewSpeedLimitValue;

    private RadarList mRadars;
    private GeofenceTransitionsIntent mGeofenceTransition;
    
    private Button mButtonEnd;
    private Button mButtonMap;

    private Location mLocation;

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
        Transitions.fadeIN(layoutMain, Constants.TRANSIION_DURATION_2K);
        wireUpViews(rootView);
        getFragmentArguments();
//        monitorGpsStatus();
        setScreenInformation();
        mGeofenceTransition = new GeofenceTransitionsIntent(getActivity());
        prepareEndButton();
        return rootView;
    }

    private void prepareEndButton() {
        mButtonEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTrip();
            }
        });
    }

    private void wireUpViews(View rootView) {
        mTextViewDistance = (TextView) rootView.findViewById(R.id.text_view_distance);
        mTextViewSpeedLimitValue = (TextView) rootView.findViewById(R.id.text_view_speed_limit_value);
        prepareButtonMap(rootView);
        mButtonEnd = (Button) rootView.findViewById(R.id.button_end);
    }

    private void prepareButtonMap(View rootView) {
        mButtonMap = (Button) rootView.findViewById(R.id.button_map);
        mButtonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (getActivity(), MapActivity.class);
                intent.putExtra(Constants.RADARS_LIST, mRadars);
                startActivity(intent);
            }
        });
    }

    private void getFragmentArguments() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(Constants.RADARS_LIST)) {
            mRadars = bundle.getParcelable(Constants.RADARS_LIST);
        }
    }

    private void refreshSpeed() {
        LocationManager lm = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                100,
                                100,
                                new android.location.LocationListener() {
                                    @Override
                                    public void onLocationChanged(Location location) {
                                        float speed;
                                        if (mLocation == null){
                                            speed = 0;
                                        } else {
                                            speed = getSpeed(mLocation, location);
                                        }
                                        ((ActionBarActivity)getActivity()).getSupportActionBar()
                                                .setSubtitle(String.valueOf(speed));
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

                                    private float getSpeed(Location startLoc, Location endLoc)
                                    {
                                        float distM;
                                        long timeS;
                                        //Use provided speed, if it exists
                                        if(endLoc.hasSpeed())
                                        {
                                            return endLoc.getSpeed();
                                        }
                                        //Get time difference is seconds
                                        timeS = getTimeDifference(startLoc, endLoc);
                                        //Get distance traveled in meters
                                        distM = startLoc.distanceTo(endLoc);

                                        return distM / timeS;
                                    }

                                    private long getTimeDifference(Location startLoc, Location endLoc)
                                    {
                                        long timeMS;
                                        timeMS = TimeUnit.NANOSECONDS.toSeconds(startLoc.getElapsedRealtimeNanos() -
                                                endLoc.getElapsedRealtimeNanos());
//                                        timeMS = (long)0.000001 * Math.abs((startLoc.getElapsedRealtimeNanos() -
//                                                endLoc.getElapsedRealtimeNanos()));

//                                        timeMS = startLoc.getTime() - endLoc.getTime();
//                                        (long)0.001 converts milliseconds to seconds

//                                        return (long)0.001 * timeMS;

//                                        return (long)0.001 * (startLoc.getTime() - endLoc.getTime());
                                        return timeMS;
                                    }
                                });
    }

    private void setScreenInformation() {
        Radar nextRadar = mRadars.getNextRadar();
        setDistance(nextRadar);
        setMaxSpeed(nextRadar);
        refreshSpeed();
        //setRefreshTime();
    }

    private void setDistance(Radar nextRadar) {
        float distance = calculateDistanceToNextRadar(nextRadar);
        mTextViewDistance.setText(String.format(getString(R.string.text_view_distance_value), Float.toString(distance)));
    }

    private float calculateDistanceToNextRadar(Radar nextRadar) {
        Location currentLocation = getLastLocation();
        Location nextRadarLocation = createNextRadarLocation(nextRadar);
        float distance = (currentLocation.distanceTo(nextRadarLocation)/1000);
        return new BigDecimal(distance).setScale(1,BigDecimal.ROUND_HALF_UP).floatValue();
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
