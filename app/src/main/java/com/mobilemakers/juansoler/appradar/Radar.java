package com.mobilemakers.juansoler.appradar;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.location.Geofence;

public class Radar implements Parcelable {

    private String mId;
    private String mName;
    private Double mLatitude;
    private Double mLongitude;
    private Float mKm;
    private int mDirection;
    private int mMaxSpeed;

    public Radar() {
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(Double latitude) {
        mLatitude = latitude;
    }

    public Double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(Double longitude) {
        mLongitude = longitude;
    }

    public Float getKm() {
        return mKm;
    }

    public void setKm(Float km) {
        mKm = km;
    }

    public int getDirection() {
        return mDirection;
    }

    public void setDirection(int direction) {
        mDirection = direction;
    }

    public int getMaxSpeed() {
        return mMaxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        mMaxSpeed = maxSpeed;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeDouble(mLatitude);
        dest.writeDouble(mLongitude);
        dest.writeFloat(mKm);
        dest.writeInt(mDirection);
        dest.writeInt(mMaxSpeed);
        dest.writeString(mId);
    }

    private Radar(Parcel in){
        mName = in.readString();
        mLatitude = in.readDouble();
        mLongitude = in.readDouble();
        mKm = in.readFloat();
        mDirection = in.readInt();
        mMaxSpeed = in.readInt();
        mId = in.readString();
    }

    public static final Parcelable.Creator<Radar> CREATOR = new Parcelable.Creator<Radar>() {

        @Override
        public Radar createFromParcel(Parcel source) {
            return new Radar(source);
        }

        @Override
        public Radar[] newArray(int size) {
            return new Radar[size];
        }
    };

    /**
     * Creates a Location Services Geofence object for the alert.
     * @return A Geofence object.
     */
    public Geofence toGeofence(float radius) {
        return new Geofence.Builder()
                .setRequestId(mId)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setCircularRegion(mLatitude, mLongitude, radius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();
    }

}
