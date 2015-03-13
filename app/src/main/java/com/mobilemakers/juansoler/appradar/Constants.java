package com.mobilemakers.juansoler.appradar;


public class Constants {

    //Application
    public static final String APPLICATION_ID = "7P8k5rZtpTzL29BqPhsIFqMrD9T0Qg7MIT1VYzfJ";
    public static final String CLIENT_KEY = "FVtaE3Ur3M4AhZuPvvkXZyiRlZhLgRAGqB0GcZt6";

    //StartScreenFragment
    public static final String STATE_RESOLVING_ERROR = "resolving_error";
    public static final int RESULT_OK = -1;
    public static final int REQUEST_RESOLVE_ERROR = 1001;

    //Notifications
    public final static int NOTIFICATION_ID = 1;
    public final static String FIRST_NOTIFICATION_DISTANCE = "first_notification_preference_distance";
    public final static String FIRST_NOTIFICATION_SOUND = "first_notification_preference_sound";
    public final static String SECOND_NOTIFICATION_DISTANCE = "second_notification_preference_distance";
    public final static String SECOND_NOTIFICATION_SOUND = "second_notification_preference_sound";
    public final static String THIRD_NOTIFICATION_SOUND = "third_notification_preference_sound";

    //Parse
    public final static String PARSE_LATITUDE = "latitude";
    public final static String PARSE_LONGITUDE = "longitude";
    public final static String PARSE_NAME = "name";
    public final static String PARSE_KM = "km";
    public final static String PARSE_MAXIMUM_SPEED = "max_speed";
    public final static String PARSE_DIRECTION = "direction";
    public final static String RADARS_LIST = "radars_list";
    public final static String RADARS_TABLE = "Radars";
    public final static String PARSE_UPDATED_AT = "updatedAt";

    //Transitions
    public final static long TRANSIION_DURATION_1K = 1000;
    public final static long TRANSIION_DURATION_2K = 2000;
    public final static float ANIMATION_ALPHA_FROM = 0.0f;
    public final static float ANIMATION_ALPHA_TO = 1.0f;

    //GPS
    public final static String NEXT_LOCATION = "nextLocation";
    public final static long MIN_TIME_UPDATES_S = 1000;
    public final static float MIN_DISTANCE_UPDATES_M = 10;
    public final static int THIRD_FENCE = 300;
    public final static int RADIUS_INDEX_THIRD_FENCE = 2;
    public final static int RADIUS_INDEX_SECOND_FENCE = 1;
    public final static int RADIUS_INDEX_FIRST_FENCE = 0;

    //ErrorDialogFragment
    public final static String DIALOG_ERROR = "dialog_error";

    public final static String TAG_DESTINATION_DIALOG = "destinations_dialog";
    public final static String START_SCREEN_FRAGMENT_TAG = StartScreenFragment.class.getSimpleName();
    public final static String ERROR_DIALOG_TAG = "errordialog";
}
