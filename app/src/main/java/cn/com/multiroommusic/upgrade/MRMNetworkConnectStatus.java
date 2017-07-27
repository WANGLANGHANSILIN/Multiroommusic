
package cn.com.multiroommusic.upgrade;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class MRMNetworkConnectStatus
{
	public static boolean isWifiEnabled(Context context)
	{
		if (context != null)
		{
			WifiManager	wifiService = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
			
			return wifiService.isWifiEnabled();
		}

		return false;
	}
	
	public static boolean isWifiConnected(Context context)
	{
		if (context != null)
		{
			ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			
			if (networkInfo != null)
			{
				return (networkInfo.getState() == State.CONNECTED);
			}
		}
		
		return false;
	}
	
	public static String getWifiName(Context context)
	{
		if (context != null)
		{
			WifiManager	wifiService = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiService.getConnectionInfo();
			if (wifiInfo != null)
			{
				return wifiInfo.getSSID();
			}
		}
		return null;
	}
	
	public static boolean isNetworkConnected(Context context)
	{ 
		if (context != null)
		{
			ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = manager.getActiveNetworkInfo(); 
			if (networkInfo != null)
			{ 
				return networkInfo.isAvailable(); 
			} 
		}
		return false;
	}
	
	public static boolean isMobileConnected(Context context)
	{ 
		if (context != null) 
		{ 
			ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE); 
			NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); 
			
			if (networkInfo != null) 
			{ 
				return networkInfo.isAvailable(); 
			} 
		} 
		
		return false; 
	}
	
	public static int getConnectedType(Context context)
	{ 
		if (context != null) 
		{ 
			ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE); 
			NetworkInfo networkInfo = manager.getActiveNetworkInfo(); 
			
			if (networkInfo != null && networkInfo.isAvailable()) 
			{ 
				return networkInfo.getType(); 
			} 
		} 
		return -1;
	}
}
