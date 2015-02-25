package com.mobilemakers.juansoler.appradar;


import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SummaryFragment extends Fragment {

    TextView mTexttViewDistance;
    TextView mTextViewRefreshTime;
    TextView mTextViewSpeedLimitValue;

    public SummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_summary, container, false);
        wireUpViews(rootView);

        int distance = 9;
        setDistance(distance);

        int refresh_time = 10;
        setRefreshTime(refresh_time);

        int speed_limit = 120;
        mTextViewSpeedLimitValue.setText(String.format(getString(R.string.text_view_speed_limit_value_text),speed_limit));
        return rootView;
    }

    private void setRefreshTime(int refresh_time) {
        mTextViewRefreshTime.setText(String.format(getString(R.string.text_view_refresh_time_text),Integer.toString(refresh_time)));
    }

    private void setDistance(int distance) {
        mTexttViewDistance.setText(String.format(getString(R.string.text_view_distance_value),Integer.toString(distance)));
    }

    private void wireUpViews(View rootView) {
        mTexttViewDistance = (TextView) rootView.findViewById(R.id.text_view_distance);
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
