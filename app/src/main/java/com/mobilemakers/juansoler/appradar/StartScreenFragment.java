package com.mobilemakers.juansoler.appradar;


import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class StartScreenFragment extends Fragment implements DestinationsDialog.DestinationDialogListener, ConnectionCallbacks,
        OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private final static String TAG = StartScreenFragment.class.getSimpleName();
    private final static int CONNECTION_TIMEOUT = 9000;
    private final static int FIRST_FENCE = 5000;
    private final static int SECOND_FENCE = 2000;
    private final static int THIRD_FENCE = 300;
    public final static String RADARS_LIST = "radars_list";

    // Stores the PendingIntent used to request geofence monitoring.
    private PendingIntent mGeofenceRequestIntent;
    private static GoogleApiClient mApiClient;

    List<SpotGeofence> mGeofenceList = new ArrayList<>();
    RadarList mRadars;
    public static Location mLastLocation;

    private static final String TAG_DESTINATION_DIALOG = "destinations_dialog";
    private static final long ANIMATION_DURATION = 1000;
    private static final float ANIMATION_ALPHA_FROM = 0.0f;
    private static final float ANIMATION_ALPHA_TO = 1.0f;
    private static final String CITY = "Mar del Plata";
    FragmentManager mFragmentManager;
    LinearLayout mProgressLayout;

    Button mButtonSetDestination;
    SummaryFragment mSummaryFragment = new SummaryFragment();

    public StartScreenFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_start_screen, container, false);
        mFragmentManager = getFragmentManager();
        mProgressLayout = (LinearLayout)rootView.findViewById(R.id.loadingPanel);
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
                LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    showAlertDialog();
                } else {
                    initializeGooglePlayServices();
//                    Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (getLastLocation() == null) {
//                                showNoLocationDialog();
//                            }
//                            else {
//                                    mFragmentManager.beginTransaction().replace(R.id.container, mSummaryFragment)
//                                            .addToBackStack(null).commit();
//                            }
//                        }
//                    }, 1000);
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

            private void showNoLocationDialog() {
                AlertDialogWrapper.Builder builder = new AlertDialogWrapper.Builder(getActivity());
                builder.setMessage(getString(R.string.message_no_location_dialog))
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();
            }

        });
    }

    private void initializeGooglePlayServices() {
        if (!isGooglePlayServicesAvailable()) {
            Log.e(TAG, "Google Play services unavailable.");
            return;
        }

        mApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        //TODO Show loading icon
        new LongOperation().execute();
        mProgressLayout.setVisibility(View.VISIBLE);
    }

    private class LongOperation extends AsyncTask<Void, Void, RadarList> {
        @Override
        protected RadarList doInBackground(Void... params) {
            ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            ParseDataBase parseDataBase = new ParseDataBase(connectivityManager);
            return parseDataBase.getParseObjects(getDirection());
        }

        @Override
        protected void onPostExecute(RadarList radarList) {
            mRadars = radarList;
            setFragmentArguments();
            preparingGeofenceList();
            mApiClient.connect();
        }
    }

    private int getDirection() {
        int direction;
        if (mButtonSetDestination.getText().equals(CITY)) {
            direction = 0;
        }
        else {
            direction = 1;
        }
        return direction;
    }

    private boolean isGooglePlayServicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (ConnectionResult.SUCCESS == resultCode) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Google Play services is available.");
            }
            return true;
        } else {
            Log.e(TAG, "Google Play services is unavailable.");
            return false;
        }
    }

    private void setFragmentArguments() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(RADARS_LIST, mRadars);
        mSummaryFragment.setArguments(bundle);
    }

    private void preparingGeofenceList() {
        int id = 0;
        float radius = 0;
        SpotGeofence spotGeofence;
        for (int i = 0; i < mRadars.size(); i++) {
            Radar radar = mRadars.get(i);
            Double latitude = radar.getLatitude();
            Double longitude = radar.getLongitude();
            String name = radar.getName();
            Float km = radar.getKm();
            int maxSpeed = radar.getMaxSpeed();
            int direction = radar.getDirection();
            for (int j = 0; j < 3; j++) {
                spotGeofence = new SpotGeofence();
                spotGeofence.setId(Integer.toString(id));
                spotGeofence.setLatitude(latitude);
                spotGeofence.setLongitude(longitude);
                spotGeofence.setName(name);
                spotGeofence.setKm(km);
                spotGeofence.setMaxSpeed(maxSpeed);
                spotGeofence.setDirection(direction);
                switch (j){
                    case 0:
                        radius = FIRST_FENCE;
                        break;
                    case 1:
                        radius = SECOND_FENCE;
                        break;
                    case 2:
                        radius = THIRD_FENCE;
                        break;
                }
                spotGeofence.setRadius(radius);
                mGeofenceList.add(spotGeofence);
                id++;
            }
        }
    }

    public static Location getLastLocation() {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mApiClient);
        return mLastLocation;
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

    @Override
    public void onConnected(Bundle bundle) {
        // Get the PendingIntent for the geofence monitoring request.
        // Send a request to add the current geofences.
        mGeofenceRequestIntent = getGeofenceTransitionPendingIntent();

        List<Geofence> geoFenceListForLocationServices = new ArrayList<>();
        for (int i = 0; i < mGeofenceList.size(); i++) {
            SpotGeofence spotGeofence = mGeofenceList.get(i);
            geoFenceListForLocationServices.add(spotGeofence.toGeofence());
        }

        LocationServices.GeofencingApi.addGeofences(mApiClient, geoFenceListForLocationServices,
                mGeofenceRequestIntent);

        prepareNewFragment();
    }

    private void prepareNewFragment() {
        mProgressLayout.setVisibility(View.VISIBLE);
        mFragmentManager.beginTransaction()
                .replace(R.id.container, mSummaryFragment)
                .addToBackStack(null)
                .commit();
    }

    private PendingIntent getGeofenceTransitionPendingIntent() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // If the error has a resolution, start a Google Play services activity to resolve it.
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_TIMEOUT);
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, "Exception while resolving connection error.", e);
            }
        } else {
            int errorCode = connectionResult.getErrorCode();
            Log.e(TAG, "Connection to Google Play services failed with error code " + errorCode);
        }
    }

    @Override
    public void onDestroy() {
        mApiClient.disconnect();
        super.onDestroy();
    }
}
