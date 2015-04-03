package com.mobilemakers.juansoler.appradar;

import android.net.ConnectivityManager;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Date;
import java.util.List;

public class ParseDataBase {

    ConnectivityManager mConnectivityManager;

    public ParseDataBase(ConnectivityManager connectivityManager) {
        mConnectivityManager = connectivityManager;
    }

    public RadarList getParseObjects(int direction) {
        RadarList radars = new RadarList();
        Date localDatabaseDate = getDatabaseDate(false);

        try {
            if  (localDatabaseDate == null) {
                //LocalDatabase does not exist
                if (NetworkConnections.isNetworkAvailable(mConnectivityManager)) {
                    radars = getParseObjectsFromCloud(direction);
                }
                else {
                    radars = null;
                }
            }
            else {
                if (NetworkConnections.isNetworkAvailable(mConnectivityManager)){
                    Date cloudDatabaseDate = getDatabaseDate(true);

                    if (cloudDatabaseDate != null && cloudDatabaseDate.compareTo(localDatabaseDate) != 0){
                        radars = getParseObjectsFromCloud(direction);
                    }
                    else {
                        radars = getParseObjectsFromLocal(direction);
                    }
                }
                else {
                    radars = getParseObjectsFromLocal(direction);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return radars;
    }

    private Date getDatabaseDate(boolean cloudDatabase){
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.RADARS_TABLE);
        ParseObject parseObject;
        query.orderByDescending(Constants.PARSE_UPDATED_AT);
        if (!cloudDatabase){
            query.fromLocalDatastore();
        }

        try {
            parseObject = query.getFirst();
        } catch (ParseException e) {
            return null;
        }
        return parseObject.getUpdatedAt();
    }

    private RadarList getParseObjectsFromCloud(int direction) throws ParseException {
        List<ParseObject> parseObjects;
        ParseObject parseObject;
        RadarList radars = new RadarList();
        Radar radar;

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.RADARS_TABLE);
//        query.whereEqualTo(Constants.PARSE_DIRECTION, direction);
        if (direction == 1) {
            query.orderByDescending(Constants.PARSE_KM); }
        else {
            query.orderByAscending(Constants.PARSE_KM);
        }

        parseObjects = query.find();
        for (int i = 0; i < parseObjects.size(); i++) {
            parseObject = parseObjects.get(i);
            int parseObjectDirection = parseObject.getNumber(Constants.PARSE_DIRECTION).intValue();
            if (parseObjectDirection == direction) {
                radar = createRadarFromParse(parseObject);
                radars.add(radar);
            }
            //Save to local database
            parseObject.pin();
        }
        return radars;
    }

    private RadarList getParseObjectsFromLocal(int direction) throws ParseException {
        List<ParseObject> parseObjects;
        ParseObject parseObject;
        RadarList radars = new RadarList();
        Radar radar;

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.RADARS_TABLE);
        query.fromLocalDatastore();
        query.whereEqualTo(Constants.PARSE_DIRECTION, direction);
        if (direction == 1) {
            query.orderByDescending(Constants.PARSE_KM); }
        else {
            query.orderByAscending(Constants.PARSE_KM);
        }

        parseObjects = query.find();
        for (int i = 0; i < parseObjects.size(); i++) {
            parseObject = parseObjects.get(i);
            radar = createRadarFromParse(parseObject);
            radars.add(radar);
        }
        return radars;
    }

    private Radar createRadarFromParse(ParseObject parseObject) {
        Radar radar = new Radar();
        radar.setLatitude(parseObject.getNumber(Constants.PARSE_LATITUDE).doubleValue());
        radar.setLongitude(parseObject.getNumber(Constants.PARSE_LONGITUDE).doubleValue());
        radar.setName(parseObject.getString(Constants.PARSE_NAME));
        radar.setKm(parseObject.getNumber(Constants.PARSE_KM).floatValue());
        radar.setMaxSpeed(parseObject.getNumber(Constants.PARSE_MAXIMUM_SPEED).intValue());
        return radar;
    }
}
