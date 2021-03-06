package com.airoremote.listeners;

import com.airoremote.utility.L;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class GeoLocationListener implements LocationListener {

	Activity activity;
	Context context;

	public GeoLocationListener(Activity act) {
		activity = act;
		LocationManager lm = (LocationManager) activity
				.getSystemService("location");
		if (lm.isProviderEnabled("gps")) {
			lm.requestLocationUpdates("gps", 250L, 10F, this,
					activity.getMainLooper());
		}
		if (lm.isProviderEnabled("network")) {
			lm.requestLocationUpdates("network", 250L, 10F, this,
					activity.getMainLooper());
		}
	}

	public GeoLocationListener(Context context) {
		this.context = context;
		LocationManager lm = (LocationManager) context
				.getSystemService("location");
		if (lm.isProviderEnabled("gps")) {
			lm.requestLocationUpdates("gps", 250L, 10F, this,
					context.getMainLooper());
		}
		if (lm.isProviderEnabled("network")) {
			lm.requestLocationUpdates("network", 250L, 10F, this,
					context.getMainLooper());
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		onLocationFound(location);
	}

	@Override
	public void onProviderDisabled(String provider) {
//		L.debug("Geolocation onProviderDisabled");
	}

	@Override
	public void onProviderEnabled(String provider) {
//		L.debug("Geolocation onProviderEnabled");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
//		L.debug("Geolocation onStatusChanged");
	}

	public void onLocationFound(Location loc) {
	}

	public void release() {
		LocationManager lm = null;

		if (activity != null)
			lm = (LocationManager) activity.getSystemService("location");
		else if (context != null)
			lm = (LocationManager) context.getSystemService("location");

		lm.removeUpdates(this);
	}
}