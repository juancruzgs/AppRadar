package com.mobilemakers.juansoler.appradar;


import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
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

public class SummaryFragment extends Fragment implements MainActivity.onHandleTransition, MainActivity.OnBackPressedListener{

    private TextView mTextViewDistance;
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
        Transitions.fadeIN(layoutMain, Constants.TRANSIION_DURATION_2K);
        wireUpViews(rootView);
//        monitorGpsStatus();
        mGeofenceTransition = new GeofenceTransitionsIntent(getActivity());
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState!=null){
            mRadars = savedInstanceState.getParcelable(Constants.RADARS_LIST);
        } else {
            getFragmentArguments();
        }
        setScreenInformation();
    }

    private void wireUpViews(View rootView) {
        mTextViewDistance = (TextView) rootView.findViewById(R.id.text_view_distance);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.RADARS_LIST, mRadars);
    }

    private void setScreenInformation() {
        try {
            Radar nextRadar = mRadars.getNextRadar();
            setDistance(nextRadar);
            setMaxSpeed(nextRadar);
            setRefreshTime();
        } catch (NullPointerException e){
            mTextViewDistance.setText(getString(R.string.distance_error_no_gps));
            mTextViewSpeedLimitValue.setText(getString(R.string.speed_error_no_gps));
        }
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
        float distance = calculateDistanceToNextRadar(nextRadar);
        mTextViewDistance.setText(String.format(getString(R.string.text_view_distance_value), Float.toString(distance)));
    }

    private float calculateDistanceToNextRadar(Radar nextRadar) throws NullPointerException {
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
                break;
            case R.id.action_show_map:
                Intent intent = new Intent(getActivity(), MapActivity.class);
                intent.putExtra(Constants.RADARS_LIST, mRadars);
                startActivity(intent);
                handled = true;
                break;
            case R.id.action_end_trip:
                endTrip();
                handled = true;
                break;
        }

        if (!handled) {
            handled = super.onOptionsItemSelected(item);
        }
        return handled;
    }

}
