package com.mobilemakers.juansoler.appradar;


import android.location.Location;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class SummaryFragment extends Fragment {

    private final static String NEXT_LOCATION = "nextLocation";

    TextView mTextViewDistance;
    TextView mTextViewRefreshTime;
    TextView mTextViewSpeedLimitValue;

    float mDistance;
    RadarList mRadars;

    public SummaryFragment() {
        // Required empty public constructor
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_summary, container, false);
        wireUpViews(rootView);
        getFragmentArguments();
        setScreenInformation();
        return rootView;
    }

    private void wireUpViews(View rootView) {
        mTextViewDistance = (TextView) rootView.findViewById(R.id.text_view_distance);
        mTextViewRefreshTime = (TextView) rootView.findViewById(R.id.text_view_refresh_time);
        mTextViewSpeedLimitValue = (TextView) rootView.findViewById(R.id.text_view_speed_limit_value);
    }

    private void getFragmentArguments() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(MainActivity.RADARS_LIST)) {
            mRadars = bundle.getParcelable(MainActivity.RADARS_LIST);
        }
    }

    private void setScreenInformation() {
        mDistance = calculateDistanceToTheNextRadar(mRadars.get(0).getLatitude(), mRadars.get(0).getLongitude());
        setDistance(mDistance);
        mTextViewSpeedLimitValue.setText(String.format(getString(R.string.text_view_speed_limit_value_text), mRadars.get(0).getMaxSpeed()));
        setRefreshTime(getCurrentTime());
    }

    private float calculateDistanceToTheNextRadar(Double latitude, Double longitude) {
        Location currentLocation = MainActivity.getLastLocation();
        Location nextLocation = createTheNextLocation(latitude, longitude);
        float distance = (currentLocation.distanceTo(nextLocation)/1000);
        return new BigDecimal(distance).setScale(1,BigDecimal.ROUND_HALF_UP).floatValue();
    }

    private Location createTheNextLocation(Double latitude, Double longitude) {
        Location nextLocation = new Location(NEXT_LOCATION);
        nextLocation.setLongitude(longitude);
        nextLocation.setLatitude(latitude);
        return nextLocation;
    }
    private void setDistance(float distance) {
        mTextViewDistance.setText(String.format(getString(R.string.text_view_distance_value), Float.toString(distance)));
    }

    private void setRefreshTime(String refreshTime) {
        mTextViewRefreshTime.setText(String.format(getString(R.string.text_view_refresh_time_text), refreshTime));
    }

    private String getCurrentTime () {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        return simpleDateFormat.format(date);
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
