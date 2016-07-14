package com.airometric.listeners;

import com.airometric.utility.L;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

public class GeoLocationListener extends Service implements LocationListener {

	Activity activity;
	Context context;
	
    // flag for GPS status
    boolean isGPSEnabled = false;
 
    // flag for network status
    boolean isNetworkEnabled = false;
 
    // flag for GPS status
    boolean canGetLocation = false;
 
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
 
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
 
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
 
    // Declaring a Location Manager
    protected LocationManager locationManager;

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

     
    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }
         
        // return latitude
        return latitude;
    }
     
    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }
         
        // return longitude
        return longitude;
    }
     
    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }
     
    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
      
        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");
  
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
  
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivity(intent);
            }
        });
  
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
  
        // Showing Alert Message
        alertDialog.show();
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
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
}