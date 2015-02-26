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

    private static final String MESSAGE = "Your GPS seems to be disabled, do you want to enable it?";
    private static final String POSITIVE_BUTTON_TEXT = "Enable GPS";
    private static final String NEGATIVE_BUTTON_TEXT = "Cancel";
    private static final String TAG_DESTINATION_DIALOG = "destinations_dialog";

    Button mButtonSetDestination;

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
        mButtonSetDestination = (Button)rootView.findViewById(R.id.button_select_desntination);
        mButtonSetDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transitionOUT();
                FragmentManager fragmentManager = getFragmentManager();
                DestinationsDialog destinationsDialog = new DestinationsDialog();
                destinationsDialog.show(fragmentManager, TAG_DESTINATION_DIALOG);
            }
        });
    }

    private void prepareButtonStart(View rootView) {
        Button buttonStart = (Button)rootView.findViewById(R.id.button_start_travel);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkGPSStatus();
            }

            private void checkGPSStatus() {
                LocationManager locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
                if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER ))
                {
                    showAlertDialog();
                }
            }

            private void showAlertDialog() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(MESSAGE)
                        .setCancelable(false)
                        .setPositiveButton(POSITIVE_BUTTON_TEXT, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                Intent gpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(gpsIntent);
                            }
                        })
                        .setNegativeButton(NEGATIVE_BUTTON_TEXT, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();
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
