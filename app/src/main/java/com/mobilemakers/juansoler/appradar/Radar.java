package com.mobilemakers.juansoler.appradar;

import android.os.Parcel;
import android.os.Parcelable;

public class Radar implements Parcelable {
    private String mName;
    private Double mLatitude;
    private Double mLongitude;
    private Float mKm;
    private int mDireccion;
    private int mMaxSpeed;

    public Radar() {
    }

    public Radar(String mName, Double mLatitude, Double mLongitude, Float mKm, int mDireccion, int mMaxSpeed) {
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

    public Float getKm() {
        return mKm;
    }

    public void setKm(Float km) {
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
        dest.writeInt(mDireccion);
        dest.writeInt(mMaxSpeed);
    }

    private Radar(Parcel in){
        mName = in.readString();
        mLatitude = in.readDouble();
        mLongitude = in.readDouble();
        mKm = in.readFloat();
        mDireccion = in.readInt();
        mMaxSpeed = in.readInt();
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
}
