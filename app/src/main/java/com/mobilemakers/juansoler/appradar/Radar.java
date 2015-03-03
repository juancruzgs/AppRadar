package com.mobilemakers.juansoler.appradar;

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

    public Double getKm() {
        return mKm;
    }

    public void setKm(Double km) {
        mKm = km;
    }

    public int getDireccion() {
        return mDireccion;
    }

    public void setDireccion(int direccion) {
        mDireccion = direccion;
    }

    public int getMaxSpeed() {
        return mMaxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        mMaxSpeed = maxSpeed;
    }
}
