package com.mobilemakers.juansoler.appradar;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

public class GeofenceTransitionsIntentService extends IntentService
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private final static String TAG = GeofenceTransitionsIntentService.class.getSimpleName();

    NotificationCompat.Builder mBuilder;
    int mId;

    public GeofenceTransitionsIntentService() {
        super(GeofenceTransitionsIntentService.class.getSimpleName());
    }

    /**
     * Handles incoming intents.
     * @param intent The Intent sent by Location Services. This Intent is provided to Location
     * Services (inside a PendingIntent) when addGeofences() is called.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geoFenceEvent = GeofencingEvent.fromIntent(intent);
        if (geoFenceEvent.hasError()) {
            int errorCode = geoFenceEvent.getErrorCode();
            Log.e(TAG, "Location Services error: " + errorCode);
        } else {

            int transitionType = geoFenceEvent.getGeofenceTransition();
            if (Geofence.GEOFENCE_TRANSITION_ENTER == transitionType) {
                String triggeredGeoFenceId = geoFenceEvent.getTriggeringGeofences().get(0)
                        .getRequestId();
                //Calling notifications
                //createNotification("title", "text", R.mipmap.ic_launcher);
                Log.d(TAG, "Entering GeoFence");
            } else if (Geofence.GEOFENCE_TRANSITION_EXIT == transitionType) {
                Log.d(TAG, "Exiting GeoFence");
            }

        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void createNotification(String title,String text, int icon){
        mBuilder =  new NotificationCompat.Builder(this)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(text);
        Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(mId, mBuilder.build());
    }
}
