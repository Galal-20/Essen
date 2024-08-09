package com.example.essen.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.util.Log;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private static final String TAG = "NetworkChangeReceiver";
    private NetworkChangeListener listener;

    public NetworkChangeReceiver(NetworkChangeListener listener) {
        this.listener = listener;
    }

    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
                if (capabilities != null) {
                    return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
                }
            } else {
                android.net.NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                return activeNetwork != null && activeNetwork.isConnected();
            }
        }
        return false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isConnected = isConnectedToInternet(context);
        Log.d(TAG, "Network connectivity change detected, isConnected: " + isConnected);
        if (listener != null) {
            listener.onNetworkChange(isConnected);
        }
    }

    public interface NetworkChangeListener {
        void onNetworkChange(boolean isConnected);
    }
}

