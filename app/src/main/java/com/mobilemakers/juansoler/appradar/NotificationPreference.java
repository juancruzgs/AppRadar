package com.mobilemakers.juansoler.appradar;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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

    public String getFirstNotificationDistance() {
        return mFirstNotificationDistance;
    }

    public void setFirstNotificationDistance(String mFirstNotificationDistance) {
        this.mFirstNotificationDistance = mFirstNotificationDistance;
    }

    public String getFirstNotificationSound() {
        return mFirstNotificationSound;
    }

    public void setFirstNotificationSound(String mFirstNotificationSound) {
        this.mFirstNotificationSound = mFirstNotificationSound;
    }

    public String getSecondNotificationDistance() {
        return mSecondNotificationDistance;
    }

    public void setSecondNotificationDistance(String mSecondNotificationDistance) {
        this.mSecondNotificationDistance = mSecondNotificationDistance;
    }

    public String getSecondNotificationSound() {
        return mSecondNotificationSound;
    }

    public void setSecondNotificationSound(String mSecondNotificationSound) {
        this.mSecondNotificationSound = mSecondNotificationSound;
    }

    public String getThirdNotificationSound() {
        return mThirdNotificationSound;
    }

    public void setThirdNotificationSound(String mThirdNotificationSound) {
        this.mThirdNotificationSound = mThirdNotificationSound;
    }

    public void getSharedPreferences (Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        setFirstNotificationDistance(sharedPreferences.getString(FIRST_NOTIFICATION_DISTANCE, ""));
        setFirstNotificationSound(sharedPreferences.getString(FIRST_NOTIFICATION_SOUND, ""));
        setSecondNotificationDistance(sharedPreferences.getString(SECOND_NOTIFICATION_DISTANCE, ""));
        setSecondNotificationSound(sharedPreferences.getString(SECOND_NOTIFICATION_SOUND, ""));
        setThirdNotificationSound(sharedPreferences.getString(THIRD_NOTIFICATION_SOUND, ""));
    }
}
