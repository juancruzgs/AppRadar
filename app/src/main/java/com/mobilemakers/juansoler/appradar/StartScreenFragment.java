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

import com.afollestad.materialdialogs.AlertDialogWrapper;

import java.util.Iterator;

public class StartScreenFragment extends Fragment implements DestinationsDialog.DestinationDialogListener {

    private static final String TAG_DESTINATION_DIALOG = "destinations_dialog";
    private static final long ANIMATION_DURATION = 1000;
    private static final float ANIMATION_ALPHA_FROM = 0.0f;
    private static final float ANIMATION_ALPHA_TO = 1.0f;
    FragmentManager mFragmentManager;

    Button mButtonSetDestination;

    RadarList mRadars;

    public StartScreenFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_start_screen, container, false);
        mFragmentManager = getFragmentManager();
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
                DestinationsDialog destinationsDialog = new DestinationsDialog();
                destinationsDialog.show(mFragmentManager, TAG_DESTINATION_DIALOG);
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
                if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER )){
                    showAlertDialog();
                } else {
                    int direction = getDirection();
                    mRadars = filterRadars(direction);
                    SummaryFragment summaryFragment = new SummaryFragment();
                    setFragmentArgument(summaryFragment);
                    mFragmentManager.beginTransaction().replace(R.id.container, summaryFragment)
                            .addToBackStack(null).commit();
                }
            }

            private void showAlertDialog() {
                AlertDialogWrapper.Builder builder = new AlertDialogWrapper.Builder(getActivity());
                builder.setMessage(getString(R.string.messageGPS_dialog))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.enableGPS_dialog), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                Intent gpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(gpsIntent);
                            }
                        })
                        .setNegativeButton(getString(R.string.cancelGPS_dialog), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();
            }

            private int getDirection() {
                int direction;
                if (mButtonSetDestination.getText().equals("Mar del Plata")) {
                    direction = 0;
                }
                else {
                    direction = 1;
                }
                return direction;
            }

            private void setFragmentArgument(SummaryFragment summaryFragment) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(MainActivity.RADARS_LIST, mRadars);
                summaryFragment.setArguments(bundle);
            }

            public RadarList filterRadars (int direction) {
                Bundle bundle = getArguments();
                RadarList radarList = null;
                if (bundle != null && bundle.containsKey(MainActivity.RADARS_LIST)) {
                    radarList = bundle.getParcelable(MainActivity.RADARS_LIST);
                    Iterator iterator = radarList.getmRadars().iterator();
                    while (iterator.hasNext()) {
                        Radar radar = (Radar) iterator.next();
                        if (radar.getDireccion() == direction) {
                            radarList.getmRadars().remove(radar); // PARCEL ERROR
                        }
                    }
                }
                return radarList;
            }
        });
    }



    @Override
    public void onFinishDialog(String destination) {
        if (!destination.equals("")){
            mButtonSetDestination.setText(destination);
        }
        transitionIN();
    }

    public void transitionIN() {

        Animation animationIn = new AlphaAnimation(ANIMATION_ALPHA_FROM, ANIMATION_ALPHA_TO);
        animationIn.setDuration(ANIMATION_DURATION);
        mButtonSetDestination.startAnimation(animationIn);
        animationIn.setAnimationListener(new Animation.AnimationListener() {
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

    public void transitionOUT() {

        Animation animationOut = new AlphaAnimation(ANIMATION_ALPHA_TO, ANIMATION_ALPHA_FROM);
        animationOut.setDuration(ANIMATION_DURATION);
        mButtonSetDestination.startAnimation(animationOut);
        animationOut.setAnimationListener(new Animation.AnimationListener() {
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
