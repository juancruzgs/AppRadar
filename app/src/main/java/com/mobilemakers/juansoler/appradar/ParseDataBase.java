package com.mobilemakers.juansoler.appradar;

import android.net.ConnectivityManager;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ParseDataBase {

    ConnectivityManager mConnectivityManager;

    public ParseDataBase(ConnectivityManager connectivityManager) {
        mConnectivityManager = connectivityManager;
    }

    public RadarList getParseObjects(int direction) {
        RadarList radars = new RadarList();

        try {
            if  (!existsLocalDatabase()) {
                if (NetworkConnections.isNetworkAvailable(mConnectivityManager)) {
                    radars = getParseObjectsFromNetwork(direction);
                }
                //TODO Else the user has to connect the device to internet
            }
            else {
                if (NetworkConnections.isNetworkAvailable(mConnectivityManager)){
                    ExecutorService taskExecutor = Executors.newFixedThreadPool(2);
                    Future<Date> resultLocalDate = taskExecutor.submit(new DatabaseDateTask(false));
                    Future<Date> resultCloudDate = taskExecutor.submit(new DatabaseDateTask(true));

                    Date localDatabaseDate = resultLocalDate.get();
                    Date cloudDatabaseDate = resultCloudDate.get();

                    if (localDatabaseDate.compareTo(cloudDatabaseDate) != 0){
                        radars = getParseObjectsFromNetwork(direction);
                    }
                    else {
                        radars = getParseObjectsFromLocal(direction);
                    }
                }
                else {
                    radars = getParseObjectsFromLocal(direction);
                }
            }
        } catch (InterruptedException|ExecutionException |ParseException e) {
            e.printStackTrace();
        }

        return radars;
    }

    private boolean existsLocalDatabase() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.RADARS_TABLE);
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

    private RadarList getParseObjectsFromNetwork(int direction) throws ParseException {
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
            parseObject.pinInBackground();
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
        radar.setDirection(parseObject.getNumber(Constants.PARSE_DIRECTION).intValue());
        return radar;
    }
}
