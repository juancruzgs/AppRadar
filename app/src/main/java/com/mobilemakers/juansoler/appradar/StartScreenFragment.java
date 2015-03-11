package com.mobilemakers.juansoler.appradar;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StartScreenFragment extends Fragment implements DestinationsDialog.DestinationDialogListener,
        OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private boolean mResolvingError = false;

    private GoogleApiClient mApiClient;
    private List<SpotGeofence> mGeofenceList = new ArrayList<>();
    private RadarList mRadars;

    private FragmentManager mFragmentManager;
    private LinearLayout mProgressLayout;
    private ImageView mImageViewSS;
    private Button mButtonSetDestination;
    private Button mButtonStart;
    private TextView mTextViewWelcome;
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
                && savedInstanceState.getBoolean(Constants.STATE_RESOLVING_ERROR, false);
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
                Transitions.fadeOUT(mButtonSetDestination, Constants.TRANSIION_DURATION_1K, false);
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
                    CustomAlertDialog alertDialog = new CustomAlertDialog(getString(R.string.messageGPS_dialog),
                                                                          getString(R.string.enableGPS_dialog),
                                                                          getString(R.string.cancelGPS_dialog),
                                                                          Settings.ACTION_LOCATION_SOURCE_SETTINGS,
                                                                          getActivity());
                    alertDialog.showAlertDialog();
                } else {
                    transitionToLoadingScreen();
                    initializeGooglePlayServices();
                    new DatabaseOperations().execute();
                }
            }

    private void transitionToLoadingScreen() {
        Transitions.fadeOUT(mImageViewSS, Constants.TRANSIION_DURATION_1K, false);
        Transitions.fadeOUT(mButtonSetDestination, Constants.TRANSIION_DURATION_1K, true);
        Transitions.fadeOUT(mButtonStart, Constants.TRANSIION_DURATION_1K, true);
        Transitions.fadeOUT(mTextViewWelcome, Constants.TRANSIION_DURATION_1K, true, mProgressLayout);
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
        if(isGooglePlayServicesAvailable()){
            initializeGoogleApiClient();
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        return ConnectionResult.SUCCESS == resultCode;
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
            ParseDataBase parseDatabase = new ParseDataBase(connectivityManager);
            return parseDatabase.getParseObjects(getDirection());
        }

        @Override
        protected void onPostExecute(RadarList radarList) {
            if (radarList != null){
                mRadars = radarList;
                preparingGeofenceList();
                mApiClient.connect();
            }
            else {
                CustomAlertDialog alertDialog = new CustomAlertDialog("sdfsddfs",
                        "Activar",
                        "Cancelar",
                        Settings.ACTION_DATA_ROAMING_SETTINGS,
                        getActivity());
                alertDialog.showAlertDialog();
            }

            // TODO Hide the Loading screen
        }
    }

    private int getDirection() {
        int direction;
        if (mButtonSetDestination.getText().equals(getString(R.string.mdq))) {
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
        Iterator iterator = mRadars.iterator();
        while (iterator.hasNext()){
            Radar radar = (Radar) iterator.next();
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
        Transitions.fadeIN(mButtonSetDestination, Constants.TRANSIION_DURATION_1K);
        if (mButtonStart.getVisibility() != View.VISIBLE) {
            Transitions.fadeIN(mButtonStart, Constants.TRANSIION_DURATION_1K);
        }
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
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!mResolvingError) {
            if (connectionResult.hasResolution()) {
                try {
                    mResolvingError = true;
                    connectionResult.startResolutionForResult(getActivity(), Constants.REQUEST_RESOLVE_ERROR);
                } catch (IntentSender.SendIntentException e) {
                    mApiClient.connect();
                }
            } else {
                //TODO Add ErrorDialogFragment. Link: https://developer.android.com/google/auth/api-client.html
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == Constants.RESULT_OK) {
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
        outState.putBoolean(Constants.STATE_RESOLVING_ERROR, mResolvingError);
    }

}
