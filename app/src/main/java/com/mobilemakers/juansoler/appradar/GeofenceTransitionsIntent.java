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
        GeofencingEvent geoFenceEvent = GeofencingEvent.fromIntent(intent);
        if (geoFenceEvent != null && !geoFenceEvent.hasError()) {
            int transitionType = geoFenceEvent.getGeofenceTransition();

            int triggeredGeoFenceId = Integer.valueOf(geoFenceEvent.getTriggeringGeofences().get(0)
                    .getRequestId());
            int radarIndex = triggeredGeoFenceId / 3;
            int radiusIndex = triggeredGeoFenceId % 3;

            if (Geofence.GEOFENCE_TRANSITION_ENTER == transitionType) {

                showActivityAlwaysOnTop();

                //Calling notifications
                Radar radar = radarList.get(radarIndex);
                Float km = radar.getKm();
                float radius = getRadiusMeters(radiusIndex);

                createNotification(mActivity.getString(R.string.warning_message),
                        String.format(mActivity.getString(R.string.radar_message), radius, km),
                        R.mipmap.ic_launcher, getNotificationId(radius));
            } else if (Geofence.GEOFENCE_TRANSITION_EXIT == transitionType &&
                        radiusIndex == Constants.RADIUS_INDEX_THIRD_FENCE) {
                    radarList.setNextRadarIndex(radarIndex+1);
            }
        }
    }

    private void showActivityAlwaysOnTop() {
        mActivity.getWindow().setFlags(
                        //WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        //WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                        //WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        //WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    private float getRadiusMeters(int radiusIndex) {
        float radius = 0;
        switch (radiusIndex){
            case Constants.RADIUS_INDEX_FIRST_FENCE:
                radius = NotificationPreference.getFirstNotificationDistance(mActivity);
                break;
            case Constants.RADIUS_INDEX_SECOND_FENCE:
                radius = NotificationPreference.getSecondNotificationDistance(mActivity);
                break;
            case Constants.RADIUS_INDEX_THIRD_FENCE:
                radius = Constants.THIRD_FENCE;
                break;
        }
        return radius;
    }

    private int getNotificationId(float radius) {
        if (radius >= Constants.MINIMUM_RADIUS_FIRST_NOTIFICATION){
            return 1;
        }
        else if (radius >= Constants.MINIMUM_RADIUS_SECOND_NOTIFICATION){
            return 2;
        }
        else {
            return 3;
        }
    }

    private void createNotification(String title,String text, int icon, int notificationId){
        NotificationCompat.Builder builder =  new NotificationCompat.Builder(mActivity)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setSound(getSoundUri(notificationId))
                .setDefaults(0)
                .setLights(getLedColor(notificationId) + 0xFF000000, Constants.LED_DURATION_ON[notificationId],
                        Constants.LED_DURATION_OFF[notificationId])
                .setContentIntent(PendingIntent.getActivity(mActivity, 0, new Intent(), 0));

        NotificationManager mNotificationManager = (NotificationManager) mActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        mNotificationManager.notify(Constants.NOTIFICATION_ID, notification);
    }

    private Uri getSoundUri(int notification){
        //Checking notification number
        String soundName;
        switch (notification){
            case 1:
                soundName = NotificationPreference.getFirstNotificationSound(mActivity);
                break;
            case 2:
                soundName = NotificationPreference.getSecondNotificationSound(mActivity);
                break;
            default:
                soundName = NotificationPreference.getThirdNotificationSound(mActivity);
                break;
        }

        return Uri.parse(mActivity.getString(R.string.sound_path)
                + mActivity.getPackageName() + "/" + SoundListPreference.getSound(soundName));

/*
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
*/
    }

    private int getLedColor(int notificationId) {
        //Checking notification number
        int ledColor;
        switch (notificationId){
            case 1:
                ledColor = NotificationPreference.getFirstNotificationLed(mActivity);
                break;
            case 2:
                ledColor = NotificationPreference.getSecondNotificationLed(mActivity);
                break;
            default:
                ledColor = NotificationPreference.getThirdNotificationLed(mActivity);
                break;
        }

        return ledColor;
    }
}
