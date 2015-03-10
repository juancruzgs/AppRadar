package com.mobilemakers.juansoler.appradar;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class RadarList implements Parcelable {

    private List<Radar> mRadars;

    public RadarList() {
        mRadars = new ArrayList<>();
    }

    public void add (Radar radar) {
        mRadars.add(radar);
    }

    public Radar get (int index) {
        return mRadars.get(index);
    }

    public Iterator iterator () {
        return mRadars.iterator();
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
