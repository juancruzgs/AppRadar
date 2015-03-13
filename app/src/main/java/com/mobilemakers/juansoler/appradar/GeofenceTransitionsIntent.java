package com.mobilemakers.juansoler.appradar;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.view.WindowManager;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;


public class GeofenceTransitionsIntent {

    private Activity mActivity;

    public GeofenceTransitionsIntent(Activity activity) {
        this.mActivity = activity;
    }

    protected void handleTransition(Intent intent, RadarList radarList) {
        NotificationPreference notification = new NotificationPreference();
        notification.getSharedPreferences(mActivity);
        GeofencingEvent geoFenceEvent = GeofencingEvent.fromIntent(intent);
        if (!geoFenceEvent.hasError()) {
            int transitionType = geoFenceEvent.getGeofenceTransition();

            int triggeredGeoFenceId = Integer.valueOf(geoFenceEvent.getTriggeringGeofences().get(0)
                    .getRequestId());
            int radarIndex = triggeredGeoFenceId / 3;
            int radiusIndex = triggeredGeoFenceId % 3;

            if (Geofence.GEOFENCE_TRANSITION_ENTER == transitionType) {

                Radar radar = radarList.get(radarIndex);

                float radius = 0;
                switch (radiusIndex){
                    case Constants.RADIUS_INDEX_FIRST_FENCE:
                        radius = Float.parseFloat(notification.getFirstNotificationDistance()) * 1000;
                        break;
                    case Constants.RADIUS_INDEX_SECOND_FENCE:
                        radius = Float.parseFloat(notification.getSecondNotificationDistance()) * 1000;
                        break;
                    case Constants.RADIUS_INDEX_THIRD_FENCE:
                        radius = Constants.THIRD_FENCE;
                        break;
                }

                //Calling notifications
                createNotification(mActivity.getString(R.string.warning_message),
                        String.format(mActivity.getString(R.string.radar_message), radius),
                        R.mipmap.ic_launcher, getNotificationId(radius));
                showActivityAlwaysOnTop();
            } else if (Geofence.GEOFENCE_TRANSITION_EXIT == transitionType &&
                        radiusIndex == Constants.RADIUS_INDEX_THIRD_FENCE) {
                radarList.incrementNextRadarIndex();
            }
        }
    }

    private Uri getSoundUri(int notification){

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

        if (soundName.equals(mActivity.getString(R.string.sub_klaxon))){
            return Uri.parse(mActivity.getString(R.string.sound_path)
                    + mActivity.getPackageName() + "/" + R.raw.sub_klaxon);
        } else {
            if (soundName.equals(mActivity.getString(R.string.factory))){
                return Uri.parse(mActivity.getString(R.string.sound_path)
                        + mActivity.getPackageName() + "/" + R.raw.factory);
            } else {
                if (soundName.equals(mActivity.getString(R.string.air_horn))){
                    return Uri.parse(mActivity.getString(R.string.sound_path)
                            + mActivity.getPackageName() + "/" + R.raw.air_horn);
                } else {
                    if (soundName.equals(mActivity.getString(R.string.beep_ping))) {
                        return Uri.parse(mActivity.getString(R.string.sound_path)
                                + mActivity.getPackageName() + "/" + R.raw.beep_ping);
                    } else {
                        if (soundName.equals(mActivity.getString(R.string.smb_flagpole))){
                            return Uri.parse(mActivity.getString(R.string.sound_path)
                                    + mActivity.getPackageName() + "/" + R.raw.smb_flagpole);
                        } else {
                            if (soundName.equals(mActivity.getString(R.string.smb_pipe))){
                                return Uri.parse(mActivity.getString(R.string.sound_path)
                                        + mActivity.getPackageName() + "/" + R.raw.smb_pipe);
                            } else {
                                if (soundName.equals(mActivity.getString(R.string.smb_vine))){
                                    return Uri.parse(mActivity.getString(R.string.sound_path)
                                            + mActivity.getPackageName() + "/" + R.raw.smb_vine);
                                } else {
                                    return Uri.parse(mActivity.getString(R.string.sound_path)
                                            + mActivity.getPackageName() + "/" + R.raw.smb_warning);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void createNotification(String title,String text, int icon, int notification){
        NotificationCompat.Builder builder =  new NotificationCompat.Builder(mActivity)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setSound(getSoundUri(notification))
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setContentIntent(PendingIntent.getActivity(mActivity, 0, new Intent(), 0));

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
