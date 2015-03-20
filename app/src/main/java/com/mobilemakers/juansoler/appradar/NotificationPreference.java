package com.mobilemakers.juansoler.appradar;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class NotificationPreference {

    private String mFirstNotificationDistance;
    private String mFirstNotificationSound;
    private String mFirstNotificationLed;
    private String mSecondNotificationDistance;
    private String mSecondNotificationSound;
    private String mSecondNotificationLed;
    private String mThirdNotificationSound;
    private String mThirdNotificationLed;

    public NotificationPreference() {
    }

    public String getFirstNotificationDistance() {
        return mFirstNotificationDistance;
    }

    public String getFirstNotificationSound() {
        return mFirstNotificationSound;
    }

    public String getFirstNotificationLed() {
        return mFirstNotificationLed;
    }

    public String getSecondNotificationDistance() {
        return mSecondNotificationDistance;
    }

    public String getSecondNotificationSound() {
        return mSecondNotificationSound;
    }

    public String getSecondNotificationLed() {
        return mSecondNotificationLed;
    }

    public String getThirdNotificationSound() {
        return mThirdNotificationSound;
    }

    public String getThirdNotificationLed() {
        return mThirdNotificationLed;
    }

    public void getSharedPreferences (Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mFirstNotificationDistance = sharedPreferences.getString(Constants.FIRST_NOTIFICATION_DISTANCE, "10");
        mFirstNotificationSound = sharedPreferences.getString(Constants.FIRST_NOTIFICATION_SOUND, context.getResources().getStringArray(R.array.sound_values)[0]);
        mFirstNotificationLed = sharedPreferences.getString(Constants.FIRST_NOTIFICATION_LED, "00FF00");
        mSecondNotificationDistance = sharedPreferences.getString(Constants.SECOND_NOTIFICATION_DISTANCE, "4");
        mSecondNotificationSound = sharedPreferences.getString(Constants.SECOND_NOTIFICATION_SOUND, context.getResources().getStringArray(R.array.sound_values)[0]);
        mSecondNotificationLed = sharedPreferences.getString(Constants.SECOND_NOTIFICATION_LED, "FFFF00");
        mThirdNotificationSound = sharedPreferences.getString(Constants.THIRD_NOTIFICATION_SOUND, context.getResources().getStringArray(R.array.sound_values)[0]);
        mThirdNotificationLed = sharedPreferences.getString(Constants.THIRD_NOTIFICATION_LED, "FF0000");
    }
}
