package com.mobilemakers.juansoler.appradar;

/**
 * Created by micaela.cavallo on 02/03/2015.
 */

public class Radar {
    private String mName;
    private Double mLatitude;
    private Double mLongitude;
    private Double mKm;
    private int mDireccion;
    private int mMaxSpeed;

    public Radar() {
    }

    public Radar(String mName, Double mLatitude, Double mLongitude, Double mKm, int mDireccion, int mMaxSpeed) {
        this.mName = mName;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        this.mKm = mKm;
        this.mDireccion = mDireccion;
        this.mMaxSpeed = mMaxSpeed;
    }

    public int getmMaxSpeed() {
        return mMaxSpeed;
    }

    public void setmMaxSpeed(int mMaxSpeed) {
        this.mMaxSpeed = mMaxSpeed;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public Double getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(Double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public Double getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(Double mLongitude) {
        this.mLongitude = mLongitude;
    }

    public Double getmKm() {
        return mKm;
    }

    public void setmKm(Double mKm) {
        this.mKm = mKm;
    }

    public int getmDireccion() {
        return mDireccion;
    }

    public void setmDireccion(int mDireccion) {
        this.mDireccion = mDireccion;
    }
}
