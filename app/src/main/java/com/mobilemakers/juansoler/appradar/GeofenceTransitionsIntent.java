package com.mobilemakers.juansoler.appradar;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

    private void createNotification(String title,String text, int icon, int notification){
        mBuilder =  new NotificationCompat.Builder(mActivity)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(text);
        Intent resultIntent = new Intent(mActivity, MainActivity.class);

        //Getting sound
        mBuilder.setSound(getSoundUri(notification));

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mActivity);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) mActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(mId, mBuilder.build());
    }

    private Uri getSoundUri(int notification){
        //Creating Uris
        Uri airhornPath = Uri.parse("android.resource://"
                + mActivity.getPackageName() + "/" + R.raw.air_horn);
        Uri subklaxonPath = Uri.parse("android.resource://"
                + mActivity.getPackageName() + "/" + R.raw.sub_klaxon);
        Uri beeppingPath = Uri.parse("android.resource://"
                + mActivity.getPackageName() + "/" + R.raw.beep_ping);
        Uri factoryPath = Uri.parse("android.resource://"
                + mActivity.getPackageName() + "/" + R.raw.factory);

        //Getting SharedPreference
        NotificationPreference notificationPreference = new NotificationPreference();
        notificationPreference.getSharedPreferences(mActivity);

        //Checking notification number
        String soundName;
        switch (notification){
            case 1:
                soundName = notificationPreference.getFirstNotificationSound();
                break;
            case 2:
                soundName = notificationPreference.getSecondNotificationSound();
                break;
            default:
                soundName = notificationPreference.getThirdNotificationSound();
                break;
        }

        if (soundName.equals("Bocina de submarino")){
            return subklaxonPath;
        } else {
            if (soundName.equals("Fábrica")){
                return factoryPath;
            } else {
                if (soundName.equals("Sirena de aire")){
                    return airhornPath;
                } else {
                    return beeppingPath;
                }
            }
        }
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
