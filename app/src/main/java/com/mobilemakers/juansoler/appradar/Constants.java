package com.mobilemakers.juansoler.appradar;


public class Constants {

    //Application
    public static final String APPLICATION_ID = "7P8k5rZtpTzL29BqPhsIFqMrD9T0Qg7MIT1VYzfJ";
    public static final String CLIENT_KEY = "FVtaE3Ur3M4AhZuPvvkXZyiRlZhLgRAGqB0GcZt6";

    //MainActivity
    public static final String FRAGMENT_SAVED = "mContent";

    //StartScreenFragment
    public static final String STATE_RESOLVING_ERROR = "resolving_error";
    public static final int RESULT_OK = -1;
    public static final int REQUEST_RESOLVE_ERROR = 1001;
    public static final String DESTINATION = "destination";
    public static final int PENDING_INTENT_REQUEST_CODE = 0;
    public static final String PENDING_INTENT_EXTRA_REQUEST_CODE = "requestCode";

    //SummaryFragment
    public static final String DATE_FORMAT = "HH:mm";
    public static final String MAX_SPEED = "maxSpeed";

    //Notifications
    public final static int NOTIFICATION_ID = 1;

    //LED durations
    public final static int [] LED_DURATION_ON = {0, 1000, 1000, 200};
    public final static int [] LED_DURATION_OFF = {0, 2000, 500, 200};
    public final static int HEXADECIMAL_BASE = 16;

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
    public final static long TRANSITION_DURATION_1K = 1000;
    public final static long TRANSITION_DURATION_2K = 2000;
    public final static float ANIMATION_ALPHA_FROM = 0.0f;
    public final static float ANIMATION_ALPHA_TO = 1.0f;

    //GPS
    public final static String NEXT_LOCATION = "nextLocation";
    public final static float MIN_DISTANCE_UPDATES = 100;
    //TODO Third notification distance
    public final static int THIRD_FENCE = 50;
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
    //TODO Change to the notification distances
    public static final float MINIMUM_RADIUS_FIRST_NOTIFICATION = 500;
    public static final float MINIMUM_RADIUS_SECOND_NOTIFICATION = 200;

    //Speed
    public static final float SPEED_CONVERSION = 3.6f;

    //Fragment replacements - back stack names
    public static final String BACKSTACK_START_TO_SUMMARY = "START_TO_SUMMARY";
    public static final String BACKSTACK_SUMMARY_TO_END = "SUMMARY_TO_END";
}
