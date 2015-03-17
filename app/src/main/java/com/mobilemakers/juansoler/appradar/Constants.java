package com.mobilemakers.juansoler.appradar;


public class Constants {

    //Application
    public static final String APPLICATION_ID = "7P8k5rZtpTzL29BqPhsIFqMrD9T0Qg7MIT1VYzfJ";
    public static final String CLIENT_KEY = "FVtaE3Ur3M4AhZuPvvkXZyiRlZhLgRAGqB0GcZt6";

    //StartScreenFragment
    public static final String STATE_RESOLVING_ERROR = "resolving_error";
    public static final int RESULT_OK = -1;
    public static final int REQUEST_RESOLVE_ERROR = 1001;
    public static final String DESTINATION = "destination";
    public static final String VISIBILITY = "visibility";

    //Notifications
    public final static int NOTIFICATION_ID = 1;
    public final static String FIRST_NOTIFICATION_DISTANCE = "first_notification_preference_distance";
    public final static String FIRST_NOTIFICATION_SOUND = "first_notification_preference_sound";
    public final static String FIRST_NOTIFICATION_LED = "first_notification_preference_led_color";
    public final static String SECOND_NOTIFICATION_DISTANCE = "second_notification_preference_distance";
    public final static String SECOND_NOTIFICATION_SOUND = "second_notification_preference_sound";
    public final static String SECOND_NOTIFICATION_LED = "second_notification_preference_led_color";
    public final static String THIRD_NOTIFICATION_SOUND = "third_notification_preference_sound";
    public final static String THIRD_NOTIFICATION_LED = "third_notification_preference_led_color";

    //LED durations
    public final static int [] LED_DURATION_ON = {0, 1000, 1000, 200};
    public final static int [] LED_DURATION_OFF = {0, 2000, 500, 200};

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
    public final static String SUMMARY_FRAGMENT_TAG = SummaryFragment.class.getSimpleName();
    public final static String ERROR_DIALOG_TAG = "errordialog";

    //Map
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    //Destinations Dialog
    public static final String TABLE_DIVIDER_NAME = "titleDivider";
    public static final String TABLE_DIVIDER_TYPE = "id";
    public static final String TABLE_DIVIDER_PACKAGE = "android";

    //Radius
    public static final float MINIMUM_RADIUS_FIRST_NOTIFICATION = 5000;
    public static final float MINIMUM_RADIUS_SECOND_NOTIFICATION = 2000;
}
