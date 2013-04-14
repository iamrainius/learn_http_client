package jing.app.network.hc;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {
    
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Need an initialized context instance.");
        }
        
        ConnectivityManager connManager = 
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean isWiFiConnected(Context context) {
    	return isConnected(context, ConnectivityManager.TYPE_WIFI);
    }
    
    public static boolean isMobileConnected(Context context) {
    	return isConnected(context, ConnectivityManager.TYPE_MOBILE);
    }
    
    private static boolean isConnected(Context context, int connType) {
    	if (context == null) {
            throw new IllegalArgumentException("Need an initialized context instance.");
        }
    	
    	ConnectivityManager connManager = 
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo wifiInfo = connManager.getNetworkInfo(connType);
    	if (wifiInfo != null && wifiInfo.isConnected()) {
    		return true;
    	}
    	
    	return false;
    }
}
