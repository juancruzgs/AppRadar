package com.mobilemakers.juansoler.appradar;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;

public class StartScreenFragment extends Fragment implements DestinationsDialog.DestinationDialogListener {

    public StartScreenFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_start_screen, container, false);
        prepareButtonDestination(rootView);
        prepareButtonStart(rootView);
        return rootView;
    }

    private void prepareButtonDestination(View rootView) {
        Button buttonSetDestination = (Button)rootView.findViewById(R.id.button_select_desntination);
        buttonSetDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transitionOUT();
                FragmentManager fm = getFragmentManager();
                DestinationsDialog destinationsDialog = new DestinationsDialog();
                destinationsDialog.show(fm, "destinations_dialog");
            }
        });
    }

    private void prepareButtonStart(View rootView) {
        Button buttonStart = (Button)rootView.findViewById(R.id.button_start_travel);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkGPSEnabled();
            }

            private void checkGPSEnabled() {
                LocationManager locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
                if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER ))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                            .setCancelable(false)
                            .setPositiveButton("Enable GPS", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    Intent gpsIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS );
                                    startActivity(gpsIntent);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    final AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });
    }

    @Override
    public void onFinishDialog(String destination) {
        mButtonSetDestination.setText(destination);
        transitionIN();
    }

    public void transitionIN(){

        Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(1000);
        mButtonSetDestination.startAnimation(in);
        in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mButtonSetDestination.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void transitionOUT(){

        Animation out = new AlphaAnimation(1.0f, 0.0f);
        out.setDuration(1000);
        mButtonSetDestination.startAnimation(out);
        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mButtonSetDestination.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
