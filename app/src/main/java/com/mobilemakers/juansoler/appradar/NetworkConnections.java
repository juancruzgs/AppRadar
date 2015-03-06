package com.mobilemakers.juansoler.appradar;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkConnections {

    public static boolean isNetworkAvailable(ConnectivityManager connectivityManager) {
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
