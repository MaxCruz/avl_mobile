package com.jaragua.avlmobile.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class DeviceProperties {

	Context context;

	public DeviceProperties(Context context) {
		this.context = context;
	}

	public boolean checkConnectivity() {
		ConnectivityManager manager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean isConnected = false;
		if (manager != null) {
			// TODO: Temp deprecation to support old version of Android
			@SuppressWarnings("deprecation")
			NetworkInfo[] info = manager.getAllNetworkInfo();
			if (info != null) {
				for (NetworkInfo anInfo : info) {
					if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
						isConnected = true;
					}
				}
			}
		}
		return isConnected;
	}

	public String getImei() {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}

}