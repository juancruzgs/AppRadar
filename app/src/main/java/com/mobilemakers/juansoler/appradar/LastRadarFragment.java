package com.mobilemakers.juansoler.appradar;


import android.content.Intent;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class LastRadarFragment extends Fragment implements MainActivity.OnBackPressedListener {

    private RadarList mRadars;

    public LastRadarFragment() {
        // Required empty public constructor
    }

    @Override
    public void doBack() {
        endTrip();
    }

    private void endTrip() {
        setActionBarSubtitle("");
        getActivity().getSupportFragmentManager().popBackStack(Constants.BACKSTACK_START_TO_SUMMARY,
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private void setActionBarSubtitle(String subtitle){
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setSubtitle(subtitle);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState!=null){
            mRadars = savedInstanceState.getParcelable(Constants.RADARS_LIST);
        } else {
            getFragmentArguments();
        }
    }

    private void getFragmentArguments() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(Constants.RADARS_LIST)) {
            mRadars = bundle.getParcelable(Constants.RADARS_LIST);
        }
    }

    private void prepareOkButton(View rootView) {
        Button buttonOk = (Button)rootView.findViewById(R.id.button_ok);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doBack();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_last_radar, container, false);

        LinearLayout layoutMain = (LinearLayout)rootView.findViewById(R.id.Layout_Main);
        Transitions.fadeIN(layoutMain, Constants.TRANSIION_DURATION_2K);
        prepareOkButton(rootView);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_last_radar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Boolean handled = false;

        switch(id) {
            case R.id.action_bar:
                handled = true;
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
