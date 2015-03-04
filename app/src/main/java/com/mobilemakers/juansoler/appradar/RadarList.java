package com.mobilemakers.juansoler.appradar;


import android.os.Parcel;
import android.os.Parcelable;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class RadarList implements Parcelable {

    private List<Radar> mRadars;

    public RadarList() {
        mRadars = new ArrayList<>();
    }

    public RadarList(List<Radar> radars) {
        mRadars = radars;
    }

    public List<Radar> getRadars() {
        return mRadars;
    }

    public void setRadars(List<Radar> mRadars) {
        this.mRadars = mRadars;
    }

    public void add (Radar radar) {
        getRadars().add(radar);
    }

    public Radar get (int location) {
        return getRadars().get(0);
    }

    public int size () {
        return getRadars().size();
    }

    public Iterator iterator () {
        return getRadars().iterator();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(mRadars);
    }

     private RadarList(Parcel in){
         mRadars = in.readArrayList(Radar.class.getClassLoader());
    }

    public static final Parcelable.Creator<RadarList> CREATOR = new Parcelable.Creator<RadarList>() {

        @Override
        public RadarList createFromParcel(Parcel source) {
            return new RadarList(source);
        }

        @Override
        public RadarList[] newArray(int size) {
            return new RadarList[size];
        }
    };
}
