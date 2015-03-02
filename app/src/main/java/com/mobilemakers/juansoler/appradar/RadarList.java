package com.mobilemakers.juansoler.appradar;


import android.os.Parcel;
import android.os.Parcelable;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;


public class RadarList implements Parcelable {

    private List<Radar> mRadars;

    public RadarList() {
        mRadars = new ArrayList<>();
    }

    public RadarList(List<Radar> radars) {
        mRadars = radars;
    }

    public List<Radar> getmRadars() {
        return mRadars;
    }

    public void setmRadars(List<Radar> mRadars) {
        this.mRadars = mRadars;
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
         mRadars = in.readArrayList(ClassLoader.getSystemClassLoader());
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
