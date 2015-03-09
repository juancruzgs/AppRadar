package com.mobilemakers.juansoler.appradar;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Date;
import java.util.concurrent.Callable;

public class DatabaseDateTask implements Callable<Date> {

    private final static String RADARS_TABLE = "Radars";
    private final static String PARSE_UPDATED_AT = "updatedAt";
    private boolean mCloudDatase;

    public DatabaseDateTask(boolean cloudDatabase) {
        mCloudDatase = cloudDatabase;
    }

    @Override
    public Date call() throws Exception {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(RADARS_TABLE);
        ParseObject parseObject;
        query.orderByDescending(PARSE_UPDATED_AT);
        if (!mCloudDatase){
            query.fromLocalDatastore();
        }

        parseObject = query.getFirst();
        return parseObject.getUpdatedAt();
    }
}
