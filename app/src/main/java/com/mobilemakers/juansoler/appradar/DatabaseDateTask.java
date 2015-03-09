package com.mobilemakers.juansoler.appradar;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.net.ContentHandler;
import java.util.Date;
import java.util.concurrent.Callable;

public class DatabaseDateTask implements Callable<Date> {

    private boolean mCloudDatase;

    public DatabaseDateTask(boolean cloudDatabase) {
        mCloudDatase = cloudDatabase;
    }

    @Override
    public Date call() throws Exception {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.RADARS_TABLE);
        ParseObject parseObject;
        query.orderByDescending(Constants.PARSE_UPDATED_AT);
        if (!mCloudDatase){
            query.fromLocalDatastore();
        }

        parseObject = query.getFirst();
        return parseObject.getUpdatedAt();
    }
}
