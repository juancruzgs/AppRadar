package com.mobilemakers.juansoler.appradar;


import android.location.Location;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SummaryFragment extends Fragment {

    private final static int DISTANCE = 9;
    private final static int REFRESH_TIME = 10;
    private final static int SPEED_LIMIT = 120;

    TextView mTextViewDistance;
    TextView mTextViewRefreshTime;
    TextView mTextViewSpeedLimitValue;

    int mDistance;
    RadarList mRadars;

    public SummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_summary, container, false);
        wireUpViews(rootView);
        mDistance = calculateDistanceToTheNextRadar();
        setDistance(mDistance);
        setRefreshTime(REFRESH_TIME);
        mTextViewSpeedLimitValue.setText(String.format(getString(R.string.text_view_speed_limit_value_text), SPEED_LIMIT));

        return rootView;
    }

    private int calculateDistanceToTheNextRadar() {
        Location currentLocation = MainActivity.mLastLocation;
        mRadars = getArguments().getParcelable(MainActivity.RADARS_LIST);
        Location nextLocation = new Location("nextLocation");
        nextLocation.setLongitude(mRadars.getmRadars().get(0).getmLongitude());
        nextLocation.setLatitude(mRadars.getmRadars().get(0).getmLatitude());
        float result = (distanceTo(currentLocation, nextLocation)/1000);
        return Math.round(result);
    }


    private void setRefreshTime(int refresh_time) {
        mTextViewRefreshTime.setText(String.format(getString(R.string.text_view_refresh_time_text),Integer.toString(refresh_time)));
    }

    private void setDistance(int distance) {
        mTextViewDistance.setText(String.format(getString(R.string.text_view_distance_value), Integer.toString(distance)));
    }

    private void wireUpViews(View rootView) {
        mTextViewDistance = (TextView) rootView.findViewById(R.id.text_view_distance);
        mTextViewRefreshTime = (TextView) rootView.findViewById(R.id.text_view_refresh_time);
        mTextViewSpeedLimitValue = (TextView) rootView.findViewById(R.id.text_view_speed_limit_value);
    }

    private float distanceTo(Location currentLocation, Location nextLocation) {
        float distance = currentLocation.distanceTo(nextLocation);
        return distance;
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
}
