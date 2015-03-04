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

    private final static int REFRESH_TIME = 10;
    private final static String NEXT_LOCATION = "nextLocation";

    TextView mTextViewDistance;
    TextView mTextViewRefreshTime;
    TextView mTextViewSpeedLimitValue;

    int mDistance;
    RadarList mRadars;

    public SummaryFragment() {
        // Required empty public constructor
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_summary, container, false);
        wireUpViews(rootView);
        getFragmentArguments();
        mDistance = calculateDistanceToTheNextRadar(mRadars.get(0).getLatitude(), mRadars.get(0).getLongitude());
        setDistance(mDistance);
        mTextViewSpeedLimitValue.setText(String.format(getString(R.string.text_view_speed_limit_value_text), mRadars.get(0).getMaxSpeed()));
        setRefreshTime(REFRESH_TIME);
        return rootView;
    }

    private int calculateDistanceToTheNextRadar(Double latitude, Double longitude) {
        Location currentLocation = MainActivity.getLastLocation();
        getFragmentArguments();
        Location nextLocation = createTheNextLocation(latitude, longitude);
        float distance = (currentLocation.distanceTo(nextLocation)/1000);
        return Math.round(distance);
    }

    private Location createTheNextLocation(Double latitude, Double longitude) {
        Location nextLocation = new Location(NEXT_LOCATION);
        nextLocation.setLongitude(longitude);
        nextLocation.setLatitude(latitude);
        return nextLocation;
    }

    private void getFragmentArguments() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(MainActivity.RADARS_LIST)) {
            mRadars = bundle.getParcelable(MainActivity.RADARS_LIST);
        }
    }

    private void setRefreshTime(int refreshTime) {
        mTextViewRefreshTime.setText(String.format(getString(R.string.text_view_refresh_time_text),Integer.toString(refreshTime)));
    }

    private void setDistance(int distance) {
        mTextViewDistance.setText(String.format(getString(R.string.text_view_distance_value), Integer.toString(distance)));
    }

    private void wireUpViews(View rootView) {
        mTextViewDistance = (TextView) rootView.findViewById(R.id.text_view_distance);
        mTextViewRefreshTime = (TextView) rootView.findViewById(R.id.text_view_refresh_time);
        mTextViewSpeedLimitValue = (TextView) rootView.findViewById(R.id.text_view_speed_limit_value);
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
