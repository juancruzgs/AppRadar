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
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    // Stores the PendingIntent used to request geofence monitoring.
    private PendingIntent mGeofenceRequestIntent;
    private static GoogleApiClient mApiClient;

    List<SpotGeofence> mGeofenceList = new ArrayList<>();
    RadarList mRadars;
    public static Location mLastLocation;

    FragmentManager mFragmentManager;
    LinearLayout mProgressLayout;
    ImageView mImageViewSS;

    Button mButtonSetDestination;
    Button mButtonStart;
    TextView mTextViewWelcome;
    SummaryFragment mSummaryFragment = new SummaryFragment();
    private NotificationPreference mNotification = new NotificationPreference();

    public StartScreenFragment() {
    }

    public interface onHandleTransition {
        void getGeofenceList (List<SpotGeofence> spotGeofences);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_start_screen, container, false);
        mFragmentManager = getFragmentManager();
        wireUpViews(rootView);
        prepareButtonDestination(rootView);
        prepareButtonStart(rootView);
        return rootView;
    }

    private void wireUpViews(View rootView) {
        mProgressLayout = (LinearLayout)rootView.findViewById(R.id.loadingPanel);
        mTextViewWelcome = (TextView)rootView.findViewById(R.id.textView_welcome);
        mImageViewSS = (ImageView)rootView.findViewById(R.id.imageView_start_screen_image);
    }


    private void prepareButtonDestination(View rootView) {
        mButtonSetDestination = (Button)rootView.findViewById(R.id.button_select_desntination);
        mButtonSetDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Transitions.fadeOUT(mButtonSetDestination, 1000, false);
                DestinationsDialog destinationsDialog = new DestinationsDialog();
                destinationsDialog.show(mFragmentManager, Constants.TAG_DESTINATION_DIALOG);
            }
        });
    }

    private void prepareButtonStart(View rootView) {
        mButtonStart = (Button)rootView.findViewById(R.id.button_start_travel);
        mButtonStart.setOnClickListener(new View.OnClickListener() {
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
                Transitions.fadeOUT(mProgressLayout, 2000, false);
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
            Log.e(Constants.START_SCREEN_FRAGMENT_TAG, "Google Play services unavailable.");
            return;
        }

        mApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        new LongOperation().execute();
        Transitions.fadeOUT(mImageViewSS, 1000, false);
        Transitions.fadeOUT(mButtonSetDestination, 1000, true);
        Transitions.fadeOUT(mButtonStart, 1000, true);
        Transitions.fadeOUT(mTextViewWelcome, 1000, true, mProgressLayout);
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
        if (mButtonSetDestination.getText().equals(Constants.CITY)) {
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
            if (Log.isLoggable(Constants.START_SCREEN_FRAGMENT_TAG, Log.DEBUG)) {
                Log.d(Constants.START_SCREEN_FRAGMENT_TAG, "Google Play services is available.");
            }
            return true;
        } else {
            Log.e(Constants.START_SCREEN_FRAGMENT_TAG, "Google Play services is unavailable.");
            return false;
        }
    }

    private void setFragmentArguments() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.RADARS_LIST, mRadars);
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
                        radius = Float.parseFloat(mNotification.getFirstNotificationDistance()) * 1000;
                        break;
                    case 1:
                        radius = Float.parseFloat(mNotification.getSecondNotificationDistance()) * 1000;
                        break;
                    case 2:
                        radius = Constants.THIRD_FENCE;
                        break;
                }
                spotGeofence.setRadius(radius);
                mGeofenceList.add(spotGeofence);
                id++;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mNotification.getSharedPreferences(getActivity());
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
        Transitions.fadeIN(mButtonSetDestination, 1000);
        if (mButtonStart.getVisibility() != View.VISIBLE) {
            Transitions.fadeIN(mButtonStart, 1000);
        }
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

        onHandleTransition onHandleTransition = (onHandleTransition) getActivity();
        onHandleTransition.getGeofenceList(mGeofenceList);

        LocationServices.GeofencingApi.addGeofences(mApiClient, geoFenceListForLocationServices,
                mGeofenceRequestIntent);

        prepareNewFragment();
    }

    private void prepareNewFragment() {
        mFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
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
                connectionResult.startResolutionForResult(getActivity(), Constants.CONNECTION_TIMEOUT);
            } catch (IntentSender.SendIntentException e) {
                Log.e(Constants.START_SCREEN_FRAGMENT_TAG, "Exception while resolving connection error.", e);
            }
        } else {
            int errorCode = connectionResult.getErrorCode();
            Log.e(Constants.START_SCREEN_FRAGMENT_TAG, "Connection to Google Play services failed with error code " + errorCode);
        }
    }

    @Override
    public void onDestroy() {
        mApiClient.disconnect();
        super.onDestroy();
    }
}
