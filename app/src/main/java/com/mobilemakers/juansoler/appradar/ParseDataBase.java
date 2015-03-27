package com.mobilemakers.juansoler.appradar;

import android.net.ConnectivityManager;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Date;
import java.util.List;

public class ParseDataBase {

    ConnectivityManager mConnectivityManager;
    RadarList mRadarList;

    public ParseDataBase(ConnectivityManager connectivityManager) {
        mConnectivityManager = connectivityManager;
        mRadarList = new RadarList();

        Radar radar;
        radar = new Radar();

        //TODO Increment ID by 1
        radar.setId("1");
        //TODO Latitude and longitude, obtain it from Google Maps
        radar.setLatitude(-35.514806);
        radar.setLongitude(-58.0054482);
        //TODO MaxSpeed, displayed in SummaryFragment and alerts
        radar.setMaxSpeed(50);
        //TODO Radar Name, displayed in SummaryFragment
        radar.setName("Chascomús");
        mRadarList.add(radar);



        radar = new Radar();
        radar.setId("2");
        radar.setLatitude(-35.514806);
        radar.setLongitude(-58.0054482);
        radar.setMaxSpeed(80);
        radar.setName("Lezama");
        mRadarList.add(radar);

        //TODO Add more radars
    }

    public RadarList getParseObjects(int direction) {
        return mRadarList;
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
