package com.mobilemakers.juansoler.appradar;

import android.net.ConnectivityManager;

import java.util.concurrent.Callable;

public class ParseTask implements Callable<RadarList> {

    private ParseDataBase mParseDataBase;

    public ParseTask(ConnectivityManager connectivityManager) {
        mParseDataBase = new ParseDataBase(connectivityManager);
    }

    @Override
    public RadarList call() throws Exception {
        RadarList radarList = mParseDataBase.getParseObjects();
        return radarList;
    }
}
