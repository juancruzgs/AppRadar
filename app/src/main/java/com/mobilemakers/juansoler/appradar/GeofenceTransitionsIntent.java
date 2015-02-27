package com.mobilemakers.juansoler.appradar;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.WindowManager;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

public class GeofenceTransitionsIntent {

    Activity mActivity;
    NotificationCompat.Builder mBuilder;
    int mId;

    public GeofenceTransitionsIntent(Activity activity) {
        this.mActivity = activity;
    }

    /**
     * Handles incoming intents.
     * @param intent The Intent sent by Location Services. This Intent is provided to Location
     * Services (inside a PendingIntent) when addGeofences() is called.
     */
    protected void handleTransition(Intent intent) {
        GeofencingEvent geoFenceEvent = GeofencingEvent.fromIntent(intent);
        if (!geoFenceEvent.hasError()) {
            int transitionType = geoFenceEvent.getGeofenceTransition();
            if (Geofence.GEOFENCE_TRANSITION_ENTER == transitionType) {
                String triggeredGeoFenceId = geoFenceEvent.getTriggeringGeofences().get(0)
                        .getRequestId();
                //Calling notifications
                //createNotification("title", "text", R.mipmap.ic_launcher);
                //showActivityAlwaysOnTop
            } else
            if (Geofence.GEOFENCE_TRANSITION_EXIT == transitionType) {
            }
        }
    }

    private void createNotification(String title,String text, int icon){
        mBuilder =  new NotificationCompat.Builder(mActivity)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(text);
        Intent resultIntent = new Intent(mActivity, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mActivity);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) mActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(mId, mBuilder.build());
    }


    private void showActivityAlwaysOnTop() {
        //Use in alert screen
        mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        //WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        //WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }
}
