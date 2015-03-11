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

import java.util.Iterator;
import java.util.List;

public class GeofenceTransitionsIntent {

    private Activity mActivity;

    public GeofenceTransitionsIntent(Activity activity) {
        this.mActivity = activity;
    }

    protected void handleTransition(Intent intent, RadarList radars) {
        NotificationPreference notification = new NotificationPreference();
        notification.getSharedPreferences(mActivity);
        GeofencingEvent geoFenceEvent = GeofencingEvent.fromIntent(intent);
        if (!geoFenceEvent.hasError()) {
            int transitionType = geoFenceEvent.getGeofenceTransition();
            if (Geofence.GEOFENCE_TRANSITION_ENTER == transitionType) {
                int triggeredGeoFenceId = Integer.valueOf(geoFenceEvent.getTriggeringGeofences().get(0)
                        .getRequestId());

                int radarIndex = triggeredGeoFenceId / 3;
                int radiusIndex = triggeredGeoFenceId % 3;

                Radar radar = radars.get(radarIndex);

                float radius = 0;
                switch (radiusIndex){
                    case 0:
                        radius = Float.parseFloat(notification.getFirstNotificationDistance()) * 1000;
                        break;
                    case 1:
                        radius = Float.parseFloat(notification.getSecondNotificationDistance()) * 1000;
                        break;
                    case 2:
                        radius = Constants.THIRD_FENCE;
                        break;
                }

                //Calling notifications
                createNotification(mActivity.getString(R.string.warning_message),
                        String.format(mActivity.getString(R.string.radar_message), radius),
                        R.mipmap.ic_launcher, getNotificationId(radius));
                showActivityAlwaysOnTop();
//            } else
//            if (Geofence.GEOFENCE_TRANSITION_EXIT == transitionType) {
//            }
            }
        }
    }

    private Uri getSoundUri(int notification){
/*
        //Creating Uris
        Uri airhornPath = Uri.parse(mActivity.getString(R.string.sound_path)
                + mActivity.getPackageName() + "/" + R.raw.air_horn);
        Uri subklaxonPath = Uri.parse(mActivity.getString(R.string.sound_path)
                + mActivity.getPackageName() + "/" + R.raw.sub_klaxon);
        Uri beeppingPath = Uri.parse(mActivity.getString(R.string.sound_path)
                + mActivity.getPackageName() + "/" + R.raw.beep_ping);
        Uri factoryPath = Uri.parse(mActivity.getString(R.string.sound_path)
                + mActivity.getPackageName() + "/" + R.raw.factory);
*/

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

        return Uri.parse(mActivity.getString(R.string.sound_path)
                + mActivity.getPackageName() + "/" + soundName + ".ogg");

/*
        if (soundName.equals(mActivity.getString(R.string.sub_klaxon))){
            return subklaxonPath;
        } else {
            if (soundName.equals(mActivity.getString(R.string.factory))){
                return factoryPath;
            } else {
                if (soundName.equals(mActivity.getString(R.string.air_horn))){
                    return airhornPath;
                } else {
                    return beeppingPath;
                }
            }
        }
*/
    }

    private void createNotification(String title,String text, int icon, int notification){
        NotificationCompat.Builder builder =  new NotificationCompat.Builder(mActivity)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(text);
        Intent resultIntent = new Intent(mActivity, MainActivity.class);

        //Getting sound
        builder.setSound(getSoundUri(notification));

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mActivity);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) mActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(Constants.NOTIFICATION_ID, builder.build());
    }

    private int getNotificationId(float radar) {
        int radius = Math.round(radar);
        switch (radius){
            case 10000:
            case 7000:
            case 5000:
                return 1;
            case 4000:
            case 3000:
            case 2000:
                return 2;
            default:
                return 3;
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
