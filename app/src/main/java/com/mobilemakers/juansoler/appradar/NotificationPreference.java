package com.mobilemakers.juansoler.appradar;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by micaela.cavallo on 26/02/2015.
 */
public class NotificationPreference {
    private final static String FIRST_NOTIFICATION_DISTANCE = "first_notification_preference_distance";
    private final static String FIRST_NOTIFICATION_SOUND = "first_notification_preference_sound";
    private final static String SECOND_NOTIFICATION_DISTANCE = "second_notification_preference_distance";
    private final static String SECOND_NOTIFICATION_SOUND = "second_notification_preference_sound";
    private final static String THIRD_NOTIFICATION_SOUND = "third_notification_preference_sound";

    private String mFirstNotificationDistance;
    private String mFirstNotificationSound;
    private String mSecondNotificationDistance;
    private String mSecondNotificationSound;
    private String mThirdNotificationSound;

    public NotificationPreference() {
    }

    public String getmFirstNotificationDistance() {
        return mFirstNotificationDistance;
    }

    public void setmFirstNotificationDistance(String mFirstNotificationDistance) {
        this.mFirstNotificationDistance = mFirstNotificationDistance;
    }

    public String getmFirstNotificationSound() {
        return mFirstNotificationSound;
    }

    public void setmFirstNotificationSound(String mFirstNotificationSound) {
        this.mFirstNotificationSound = mFirstNotificationSound;
    }

    public String getmSecondNotificationDistance() {
        return mSecondNotificationDistance;
    }

    public void setmSecondNotificationDistance(String mSecondNotificationDistance) {
        this.mSecondNotificationDistance = mSecondNotificationDistance;
    }

    public String getmSecondNotificationSound() {
        return mSecondNotificationSound;
    }

    public void setmSecondNotificationSound(String mSecondNotificationSound) {
        this.mSecondNotificationSound = mSecondNotificationSound;
    }

    public String getmThirdNotificationSound() {
        return mThirdNotificationSound;
    }

    public void setmThirdNotificationSound(String mThirdNotificationSound) {
        this.mThirdNotificationSound = mThirdNotificationSound;
    }

    public void getSharedPreferences (Activity activity) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        setmFirstNotificationDistance(sharedPreferences.getString(FIRST_NOTIFICATION_DISTANCE, ""));
        setmFirstNotificationSound(sharedPreferences.getString(FIRST_NOTIFICATION_SOUND, ""));
        setmSecondNotificationDistance(sharedPreferences.getString(SECOND_NOTIFICATION_DISTANCE, ""));
        setmSecondNotificationSound(sharedPreferences.getString(SECOND_NOTIFICATION_SOUND, ""));
        setmThirdNotificationSound(sharedPreferences.getString(THIRD_NOTIFICATION_SOUND, ""));
    }
}
