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
    private String mRefreshTime;

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

    public String getRefreshTime() {
        return mRefreshTime;
    }

    public void getSharedPreferences (Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mRefreshTime = sharedPreferences.getString(
                context.getString(R.string.preference_refresh_time), context.getResources().getStringArray(R.array.refresh_time_values)[1]);
        mFirstNotificationDistance = sharedPreferences.getString(
                context.getString(R.string.first_notification_preference_distance), context.getResources().getStringArray(R.array.first_values)[0]);
        mFirstNotificationSound = sharedPreferences.getString(
                context.getString(R.string.first_notification_preference_sound), context.getResources().getStringArray(R.array.sound_values)[0]);
        mFirstNotificationLed = sharedPreferences.getString(
                context.getString(R.string.first_notification_preference_led_color), context.getResources().getStringArray(R.array.led_color_values)[2]);
        mSecondNotificationDistance = sharedPreferences.getString(
                context.getString(R.string.second_notification_preference_distance), context.getResources().getStringArray(R.array.second_values)[0]);
        mSecondNotificationSound = sharedPreferences.getString(
                context.getString(R.string.second_notification_preference_sound), context.getResources().getStringArray(R.array.sound_values)[0]);
        mSecondNotificationLed = sharedPreferences.getString(
                context.getString(R.string.second_notification_preference_led_color), context.getResources().getStringArray(R.array.led_color_values)[4]);
        mThirdNotificationSound = sharedPreferences.getString(
                context.getString(R.string.third_notification_preference_sound), context.getResources().getStringArray(R.array.sound_values)[0]);
        mThirdNotificationLed = sharedPreferences.getString(
                context.getString(R.string.third_notification_preference_led_color), context.getResources().getStringArray(R.array.led_color_values)[1]);
    }
}
