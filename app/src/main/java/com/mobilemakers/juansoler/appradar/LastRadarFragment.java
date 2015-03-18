package com.mobilemakers.juansoler.appradar;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class LastRadarFragment extends Fragment implements MainActivity.OnBackPressedListener {

    private Button mButtonEnd;

    public LastRadarFragment() {
        // Required empty public constructor
    }

    @Override
    public void doBack() {
        endTrip();
    }

    private void endTrip() {
        getActivity().getSupportFragmentManager().popBackStack(Constants.BACKSTACK_START_TO_SUMMARY,
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
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
        mButtonEnd = (Button) rootView.findViewById(R.id.button_last_radar_end);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO: Add map loading code when it's fixed at SummaryFragment
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_last_radar, container, false);

        LinearLayout layoutMain = (LinearLayout)rootView.findViewById(R.id.Layout_Main);
        Transitions.fadeIN(layoutMain, Constants.TRANSIION_DURATION_2K);
        wireUpViews(rootView);
        prepareEndButton();

        return rootView;
    }


}
