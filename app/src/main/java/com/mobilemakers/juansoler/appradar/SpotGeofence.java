package com.mobilemakers.juansoler.appradar;

import com.google.android.gms.location.Geofence;

/**
 * Spot class with geofence info.
 */
public class SpotGeofence {

    private String mId;
    private double mLatitude;
    private double mLongitude;
    private float mRadius;
    private long mExpirationDuration;
    private String mName;
    private Double mKm;
    private int mMaxSpeed;
    private int mDirection;

    public SpotGeofence() {
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

    public void setId(String id) {
        mId = id;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public void setRadius(float radius) {
        mRadius = radius;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Double getKm() {
        return mKm;
    }

    public void setKm(Double km) {
        mKm = km;
    }

    public int getMaxSpeed() {
        return mMaxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        mMaxSpeed = maxSpeed;
    }

    public int getDirection() {
        return mDirection;
    }

    public void setDirection(int direction) {
        mDirection = direction;
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
