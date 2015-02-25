package com.mobilemakers.juansoler.appradar;

import com.google.android.gms.location.Geofence;

/**
 * Spot class with geofence info.
 *
 * Created by ariel.cattaneo on 25/02/2015.
 */
public class SpotGeofence {

    private final String mId;
    private final double mLatitude;
    private final double mLongitude;
    private final float mRadius;
    private long mExpirationDuration;

    /**
     * @param geofenceId The Geofence's request ID.
     * @param latitude Latitude of the Geofence's center in degrees.
     * @param longitude Longitude of the Geofence's center in degrees.
     */
    public SpotGeofence(String geofenceId, double latitude, double longitude, float radius) {
        this.mId = geofenceId;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mRadius = radius;
        this.mExpirationDuration = Geofence.NEVER_EXPIRE;
    }

    public String getId() {
        return mId;
    }
    public double getLatitude() {
        return mLatitude;
    }
    public double getLongitude() {
        return mLongitude;
    }
    public float getRadius() {
        return mRadius;
    }
    public long getExpirationDuration() {
        return mExpirationDuration;
    }

    /**
     * Creates a Location Services Geofence object for the alert.
     * @return A Geofence object.
     */
    public Geofence toGeofence() {
        return new Geofence.Builder()
                .setRequestId(mId)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setCircularRegion(mLatitude, mLongitude, mRadius)
                .setExpirationDuration(mExpirationDuration)
                .build();
    }

}
