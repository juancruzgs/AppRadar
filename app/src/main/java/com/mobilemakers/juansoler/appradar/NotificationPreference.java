package com.mobilemakers.juansoler.appradar;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class NotificationPreference {

    private NotificationPreference() {
    }

    public static float getFirstNotificationDistance(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return Float.parseFloat(sharedPreferences.getString(
                context.getString(R.string.first_notification_preference_distance), context.getResources().getStringArray(R.array.first_distance_values)[0]));
    }

    public static String getFirstNotificationSound(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(
                context.getString(R.string.first_notification_preference_sound), context.getResources().getStringArray(R.array.sound_values)[0]);
    }

    public static int getFirstNotificationLed(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(sharedPreferences.getString(
                context.getString(R.string.first_notification_preference_led_color), context.getResources().getStringArray(R.array.led_color_values)[2])
                , Constants.HEXADECIMAL_BASE);
    }

    public static float getSecondNotificationDistance(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return Float.parseFloat(sharedPreferences.getString(
                context.getString(R.string.second_notification_preference_distance), context.getResources().getStringArray(R.array.second_distance_values)[0]));
    }

    public static String getSecondNotificationSound(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(
                context.getString(R.string.second_notification_preference_sound), context.getResources().getStringArray(R.array.sound_values)[0]);
    }

    public static int getSecondNotificationLed(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(sharedPreferences.getString(
                context.getString(R.string.second_notification_preference_led_color), context.getResources().getStringArray(R.array.led_color_values)[4])
                , Constants.HEXADECIMAL_BASE);
    }

    public static String getThirdNotificationSound(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(
                context.getString(R.string.third_notification_preference_sound), context.getResources().getStringArray(R.array.sound_values)[0]);
    }

    public static int getThirdNotificationLed(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(sharedPreferences.getString(
                context.getString(R.string.third_notification_preference_led_color), context.getResources().getStringArray(R.array.led_color_values)[1])
                , Constants.HEXADECIMAL_BASE);
    }

    public static long getRefreshTime(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return Long.parseLong(sharedPreferences.getString(
                context.getString(R.string.preference_refresh_time), context.getResources().getStringArray(R.array.refresh_time_values)[3]));
    }
}
