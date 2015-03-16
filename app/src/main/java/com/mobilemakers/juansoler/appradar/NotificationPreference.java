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

    public void setFirstNotificationDistance(String mFirstNotificationDistance) {
        this.mFirstNotificationDistance = mFirstNotificationDistance;
    }

    public String getFirstNotificationSound() {
        return mFirstNotificationSound;
    }

    public void setFirstNotificationSound(String mFirstNotificationSound) {
        this.mFirstNotificationSound = mFirstNotificationSound;
    }

    public String getFirstNotificationLed() {
        return mFirstNotificationLed;
    }

    public void setFirstNotificationLed(String firstNotificationLed) {
        mFirstNotificationLed = firstNotificationLed;
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

    public String getSecondNotificationLed() {
        return mSecondNotificationLed;
    }

    public void setSecondNotificationLed(String secondNotificationLed) {
        mSecondNotificationLed = secondNotificationLed;
    }

    public String getThirdNotificationSound() {
        return mThirdNotificationSound;
    }

    public void setThirdNotificationSound(String mThirdNotificationSound) {
        this.mThirdNotificationSound = mThirdNotificationSound;
    }

    public String getThirdNotificationLed() {
        return mThirdNotificationLed;
    }

    public void setThirdNotificationLed(String thirdNotificationLed) {
        mThirdNotificationLed = thirdNotificationLed;
    }

    public void getSharedPreferences (Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        setFirstNotificationDistance(sharedPreferences.getString(Constants.FIRST_NOTIFICATION_DISTANCE, "10"));
        setFirstNotificationSound(sharedPreferences.getString(Constants.FIRST_NOTIFICATION_SOUND, context.getResources().getStringArray(R.array.sound_options)[0]));
        setFirstNotificationLed(sharedPreferences.getString(Constants.FIRST_NOTIFICATION_LED, "FFFFFF"));
        setSecondNotificationDistance(sharedPreferences.getString(Constants.SECOND_NOTIFICATION_DISTANCE, "4"));
        setSecondNotificationSound(sharedPreferences.getString(Constants.SECOND_NOTIFICATION_SOUND, context.getResources().getStringArray(R.array.sound_options)[0]));
        setSecondNotificationLed(sharedPreferences.getString(Constants.SECOND_NOTIFICATION_LED, "FFFFFF"));
        setThirdNotificationSound(sharedPreferences.getString(Constants.THIRD_NOTIFICATION_SOUND, context.getResources().getStringArray(R.array.sound_options)[0]));
        setThirdNotificationLed(sharedPreferences.getString(Constants.THIRD_NOTIFICATION_LED, "FFFFFF"));
    }
}
