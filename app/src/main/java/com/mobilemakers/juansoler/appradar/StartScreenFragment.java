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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
        OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{

    private boolean mResolvingError;

    private GoogleApiClient mApiClient;
    private RadarList mRadars;

    private FragmentManager mFragmentManager;
    private LinearLayout mProgressLayout;
    private ImageView mImageViewSS;
    private Button mButtonSetDestination;
    private Button mButtonStart;
    private TextView mTextViewWelcome;
    private NotificationPreference mNotification;
    private AsyncTask<Void, Void, RadarList> mDatabaseOperations;

    public StartScreenFragment() {
        mNotification = new NotificationPreference();
        mRadars = new RadarList();
        mResolvingError = false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_start_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        prepareLandscape(savedInstanceState);
    }

    private void prepareLandscape(Bundle savedInstanceState) {
        if (savedInstanceState!=null){
            if (savedInstanceState.containsKey(Constants.DESTINATION)) {
                String destination = savedInstanceState.getString(Constants.DESTINATION);
                mButtonSetDestination.setText(destination);
                if (!destination.equals(getString(R.string.button_select_destination))) {
                    mButtonStart.setVisibility((View.VISIBLE));
                }
                else {
                    mButtonStart.setVisibility((View.INVISIBLE));
                }
            }
        }
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
                //TODO Check Network Connection. Use Class NetworkConnections , method isNetworkAvailable()
                //TODO 3 Buttons in dialog. Cancel, Wifi, Data Roaming
                //TODO Test Preference "firstrun". I has to be set the first time that you press "Start Travel"
//                AppRadarApplication app = (AppRadarApplication) getActivity().getApplication();
//                if (app.getFirstRun()){
//                    app.setRunned();
//                    new CustomAlertDialog(getString(R.string.first_time_connection),
//                            getString(R.string.ok),
//                            getString(R.string.cancel),
//                            Settings.ACTION_WIFI_SETTINGS,
//                            getActivity()).showAlertDialog();
//                }

                LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    CustomAlertDialog alertDialog = new CustomAlertDialog(getString(R.string.messageGPS_dialog),
                            getString(R.string.enableGPS_dialog),
                            getString(R.string.cancelGPS_dialog),
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS,
                            getActivity());
                    alertDialog.showAlertDialog();
                } else {
                    fadeOutViews();
                    initializeGooglePlayServices();
                    mDatabaseOperations = new DatabaseOperations().execute();
                }
            }

            private void fadeOutViews() {
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
        protected void onPostExecute(RadarList radars) {
            if (radars != null){
                mRadars = radars;
                mApiClient.connect();
            }
            else {
                CustomAlertDialog alertDialog = new CustomAlertDialog(getString(R.string.intenetDataDiagloTitle),
                        getString(R.string.internetDialogAccept),
                        getString(R.string.internetDialogCancel),
                        Settings.ACTION_DATA_ROAMING_SETTINGS,
                        getActivity());
                alertDialog.showAlertDialog();
                fadeInViews();
            }
        }
    }

    private void fadeInViews() {
        Transitions.fadeOUT(mProgressLayout, Constants.TRANSIION_DURATION_1K ,true);
        Transitions.fadeIN(mImageViewSS, Constants.TRANSIION_DURATION_1K);
        Transitions.fadeIN(mButtonStart, Constants.TRANSIION_DURATION_1K);
        Transitions.fadeIN(mButtonSetDestination, Constants.TRANSIION_DURATION_1K);
        Transitions.fadeIN(mTextViewWelcome, Constants.TRANSIION_DURATION_1K);
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

    @Override
    public void onResume() {
        super.onResume();
        mNotification.getSharedPreferences(getActivity());
    }

    @Override
    public void onFinishDialog(String destination) {
        if (!destination.isEmpty()){
            mButtonSetDestination.setText(destination);
            if (mButtonStart.getVisibility() != View.VISIBLE) {
                Transitions.fadeIN(mButtonStart, Constants.TRANSIION_DURATION_1K);
            }
        }
        Transitions.fadeIN(mButtonSetDestination, Constants.TRANSIION_DURATION_1K);
    }

    @Override
    public void onConnected(Bundle bundle) {
        PendingIntent geoFenceRequestIntent = getGeoFenceTransitionPendingIntent();

        int id = 0;
        float radius = 0;
        List<Geofence> geoFenceListForLocationServices = new ArrayList<>();
        Iterator iterator = mRadars.iterator();
        while (iterator.hasNext()) {
            Radar radar = (Radar) iterator.next();
            for (int j = 0; j < 3; j++) {
                radar.setId(Integer.toString(id));
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
                geoFenceListForLocationServices.add(radar.toGeofence(radius));
                id++;
            }
        }

        LocationServices.GeofencingApi.addGeofences(mApiClient, geoFenceListForLocationServices,
                geoFenceRequestIntent);

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

    private PendingIntent getGeoFenceTransitionPendingIntent() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(Constants.PENDING_INTENT_EXTRA_REQUEST_CODE, Constants.PENDING_INTENT_REQUEST_CODE);
        return PendingIntent.getActivity(getActivity(), Constants.PENDING_INTENT_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
                int errorCode = connectionResult.getErrorCode();
                showErrorDialog(errorCode);
                mResolvingError = true;
            }
        }
    }

    private void showErrorDialog(int errorCode) {
        ErrorDialog dialogFragment = new ErrorDialog();
        Bundle args = new Bundle();
        args.putInt(Constants.DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getFragmentManager(), Constants.ERROR_DIALOG_TAG);
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
        if(getFragmentManager().getBackStackEntryCount() == 0) {
            outState.putBoolean(Constants.STATE_RESOLVING_ERROR, mResolvingError);
            outState.putString(Constants.DESTINATION, mButtonSetDestination.getText().toString());
        }
    }

    public void onDialogDismissed() {
        mResolvingError = false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Boolean handled = false;

        switch(id) {
            case R.id.action_bar:
                handled = true;
                break;
            case R.id.action_settings:
                handled = true;
                Intent iSettings = new Intent(getActivity(), SettingsActivity.class);
                startActivity(iSettings);
                break;
        }

        if (!handled) {
            handled = super.onOptionsItemSelected(item);
        }

        return handled;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mDatabaseOperations != null && mDatabaseOperations.getStatus() == AsyncTask.Status.RUNNING) {
            mDatabaseOperations.cancel(true);
            fadeInViews();
        }
    }
}
