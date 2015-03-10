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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
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
  
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private boolean mResolvingError = false;
    private static final String STATE_RESOLVING_ERROR = "resolving_error";
    private static final int RESULT_OK = -1;

    private static GoogleApiClient mApiClient;
    List<SpotGeofence> mGeofenceList = new ArrayList<>();
    RadarList mRadars;

    FragmentManager mFragmentManager;
    LinearLayout mProgressLayout;
    ImageView mImageViewSS;

    Button mButtonSetDestination;
    Button mButtonStart;
    TextView mTextViewWelcome;   
    private NotificationPreference mNotification = new NotificationPreference();

    public StartScreenFragment() {
    }

    public interface onHandleTransition {
        void getGeofenceList (List<SpotGeofence> spotGeofences);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);
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
                transitionOUT(mButtonSetDestination, 1000, false);
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
                LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    showAlertDialog();
                } else {
                    mProgressLayout.setVisibility(View.VISIBLE);
                    initializeGooglePlayServices();
                    new DatabaseOperations().execute();
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
        });
    }
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
//            private void showNoLocationDialog() {
//                AlertDialogWrapper.Builder builder = new AlertDialogWrapper.Builder(getActivity());
//                builder.setMessage(getString(R.string.message_no_location_dialog))
//                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                dialog.dismiss();
//                            }
//                        });
//                final AlertDialog alert = builder.create();
//                alert.show();
//            }

    private void initializeGooglePlayServices(){
        if(!isGooglePlayServicesAvailable()){
        Log.e(Constants.START_SCREEN_FRAGMENT_TAG,"Google Play services unavailable.");
        return;
        }

        initializeGoogleApiClient();

        new DatabaseOperations().execute();

        transitionOUT(mImageViewSS,1000,false);
        transitionOUT(mButtonSetDestination,1000,true);
        transitionOUT(mButtonStart,1000,true);
        transitionOUT(mTextViewWelcome,1000,true,mProgressLayout);
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

    private void initializeGoogleApiClient() {
        mApiClient=new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        AppRadarApplication state = (AppRadarApplication)getActivity().getApplicationContext();
        state.setApiClient(mApiClient);
    }

    private class DatabaseOperations extends AsyncTask<Void, Void, RadarList> {
        @Override
        protected RadarList doInBackground(Void... params) {
            ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            ParseDatabase parseDatabase = new ParseDatabase(connectivityManager);
            return parseDatabase.getParseObjects(getDirection());
        }

        @Override
        protected void onPostExecute(RadarList radarList) {
            mRadars = radarList;
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

    @Override
    public void onFinishDialog(String destination) {
        if (!destination.equals("")){
            mButtonSetDestination.setText(destination);
        }
        transitionIN(mButtonSetDestination, 1000);
        if (mButtonStart.getVisibility() != View.VISIBLE) {
            transitionIN(mButtonStart, 1000);
        }
    }

    public void transitionIN(final View view, long duration) {

        Animation animationIn = new AlphaAnimation(Constants.ANIMATION_ALPHA_FROM, Constants.ANIMATION_ALPHA_TO);
        animationIn.setDuration(duration);
        view.startAnimation(animationIn);
        animationIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void transitionOUT(final View view, long duration,final Boolean gone) {

        Animation animationOut = new AlphaAnimation(Constants.ANIMATION_ALPHA_TO, Constants.ANIMATION_ALPHA_FROM);
        animationOut.setDuration(duration);
        view.startAnimation(animationOut);
        animationOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (gone) {
                    view.setVisibility(View.GONE);
                }else {
                    view.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void transitionOUT(final View view, long duration,final Boolean gone, final View replaceWith) {

        Animation animationOut = new AlphaAnimation(Constants.ANIMATION_ALPHA_TO, Constants.ANIMATION_ALPHA_FROM);
        animationOut.setDuration(duration);
        view.startAnimation(animationOut);
        animationOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (gone) {
                    view.setVisibility(View.GONE);
                    transitionIN(replaceWith, 1000);
                }else {
                    view.setVisibility(View.INVISIBLE);
                }
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
        PendingIntent geofenceRequestIntent = getGeofenceTransitionPendingIntent();

        List<Geofence> geoFenceListForLocationServices = new ArrayList<>();
        for (int i = 0; i < mGeofenceList.size(); i++) {
            SpotGeofence spotGeofence = mGeofenceList.get(i);
            geoFenceListForLocationServices.add(spotGeofence.toGeofence());
        }

        onHandleTransition onHandleTransition = (onHandleTransition) getActivity();
        onHandleTransition.getGeofenceList(mGeofenceList);

        LocationServices.GeofencingApi.addGeofences(mApiClient, geoFenceListForLocationServices,
                geofenceRequestIntent);

        prepareNewFragment();
    }

    private void prepareNewFragment() {
        mProgressLayout.setVisibility(View.VISIBLE);

        SummaryFragment mSummaryFragment = new SummaryFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.RADARS_LIST, mRadars);
        mSummaryFragment.setArguments(bundle);
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
        if (mResolvingError){
            // Already attempting to resolve an error.
            return;
        }
        else if (connectionResult.hasResolution()) {
            try {
                mResolvingError = true;
                connectionResult.startResolutionForResult(getActivity(), REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                Log.e(Constants.START_SCREEN_FRAGMENT_TAG, "Exception while resolving connection error.", e);
                mApiClient.connect();
            }
        } else {
            //TODO Add ErrorDialogFragment. Link: https://developer.android.com/google/auth/api-client.html
            int errorCode = connectionResult.getErrorCode();
            Log.e(Constants.START_SCREEN_FRAGMENT_TAG, "Connection to Google Play services failed with error code " + errorCode);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mApiClient.isConnecting() && !mApiClient.isConnected()) {
                    mApiClient.connect();
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
    }

    @Override
    public void onDestroy() {
        mApiClient.disconnect();
        super.onDestroy();
    }
}
