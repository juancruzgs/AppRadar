package com.mobilemakers.juansoler.appradar;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.util.Date;
import java.util.List;


public class ParseDataBase {

    private final static String PARSE_LATITUDE = "latitude";
    private final static String PARSE_LONGITUDE = "longitude";
    private final static String PARSE_NAME = "name";
    private final static String PARSE_KM = "km";
    private final static String PARSE_MAXIMUM_SPEED = "max_speed";
    private final static String PARSE_DIRECTION = "direction";
    private final static String PARSE_UPDATED_AT = "updatedAt";
    private final static String RADARS_TABLE = "Radars";

    RadarList mRadars;
    public ParseDataBase() {
        mRadars = new RadarList();
    }

    public RadarList gettingParseObjects(Activity activity) {
        try {
            if (NetworkConnections.isNetworkAvailable((ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE))
                    && (!existsLocalDatabase() || !isLocalDatabaseUpdated())) {
                gettingParseObjectsFromNetwork();
            } else {
                gettingParseObjectsFromLocal();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return mRadars;
    }

    private boolean existsLocalDatabase() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(RADARS_TABLE);
        query.fromLocalDatastore();
        ParseObject parseObject;
        Boolean exists = false;
        try {
            parseObject = query.getFirst();
            if (parseObject != null) {
                exists = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return exists;
    }

    private boolean isLocalDatabaseUpdated() throws ParseException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(RADARS_TABLE);
        ParseObject parseObject;
        query.orderByDescending(PARSE_UPDATED_AT);

        parseObject = query.getFirst();
        Date cloudDate = parseObject.getUpdatedAt();

        query.fromLocalDatastore();
        parseObject = query.getFirst();
        Date localDate = parseObject.getUpdatedAt();

        return localDate.compareTo(cloudDate) == 0;
    }

    private void gettingParseObjectsFromNetwork() throws ParseException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(RADARS_TABLE);
        query.orderByAscending(PARSE_KM);
        List<ParseObject> parseObjects;
        parseObjects = query.find();
        Radar radar;
        ParseObject parseObject;
        for (int i = 0; i < parseObjects.size(); i++) {
            parseObject = parseObjects.get(i);
            radar = createRadarFromParse(parseObject);
            mRadars.add(radar);
            //Save to local database
            parseObject.pin();
        }
    }

    private void gettingParseObjectsFromLocal() throws ParseException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(RADARS_TABLE);
        query.fromLocalDatastore();
        query.orderByAscending(PARSE_KM);
        List<ParseObject> parseObjects;
        parseObjects = query.find();
        Radar radar;
        ParseObject parseObject;
        for (int i = 0; i < parseObjects.size(); i++) {
            parseObject = parseObjects.get(i);
            radar = createRadarFromParse(parseObject);
            mRadars.add(radar);
        }
    }

    private Radar createRadarFromParse(ParseObject parseObject) {
        Radar radar = new Radar();
        radar.setLatitude(parseObject.getNumber(PARSE_LATITUDE).doubleValue());
        radar.setLongitude(parseObject.getNumber(PARSE_LONGITUDE).doubleValue());
        radar.setName(parseObject.getString(PARSE_NAME));
        radar.setKm(parseObject.getNumber(PARSE_KM).floatValue());
        radar.setMaxSpeed(parseObject.getNumber(PARSE_MAXIMUM_SPEED).intValue());
        radar.setDirection(parseObject.getNumber(PARSE_DIRECTION).intValue());
        return radar;
    }
}
