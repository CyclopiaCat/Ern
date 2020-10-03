package com.example.ern.LearnMode;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

// Not really needed.
//TODO: remove this
public abstract class LearnConnection {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
