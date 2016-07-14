package com.airometric;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.airometric.classes.SignalStrengh;
import com.airometric.config.Constants;
import com.airometric.listeners.BatteryChangeBroadcastReceiver;
import com.airometric.listeners.GeoLocationListener;
import com.airometric.listeners.SignalStrengthListener;
import com.airometric.storage.Preferences;
import com.airometric.utility.DeviceUtil;
import com.airometric.utility.FileUtil;
import com.airometric.utility.L;

public class Airometric extends Application {
	SignalStrengthListener signalStrengthListener;
	GeoLocationListener locationListener;
	BatteryChangeBroadcastReceiver batteryListener;
	Timer timer;
	Airometric airometric;

	public void onCreate() {
		super.onCreate();
		airometric = this;
		FileUtil.init();
		loadPreference();
	}
	
	public void loadPreference() {
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		Constants.API_SERVER_URL = sharedPrefs.getString("key_server_ip", "");
		if (Constants.API_SERVER_URL != "")
			Constants.API_SERVER_URL = "http://" + Constants.API_SERVER_URL + "/";
		L.log("Server IP: " + Constants.API_SERVER_URL);
	}

	public void startListeners(String sXMLInfoPath) {
		try {
			stopListeners();
		} catch (Exception e) {
		}
		// Initialize Signal Strength Listener
		signalStrengthListener = new SignalStrengthListener() {
			@Override
			public void onSignalChanged(SignalStrengh sg) {
				updateWithNewSignalStrength(sg);
			}
		};

		((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).listen(
				signalStrengthListener,
				SignalStrengthListener.LISTEN_SIGNAL_STRENGTHS);

		// Initialize Geo location Listener
		locationListener = new GeoLocationListener(this.getApplicationContext()) {
			@Override
			public void onLocationFound(Location loc) {
				updateWithNewLocation(loc);
			}
		};

		batteryListener = new BatteryChangeBroadcastReceiver() {
			@Override
			public void onBatterLevelChanged(int batterylevel) {
				updateWithNewBatteryLevel(batterylevel);
			}
		};
		registerReceiver(batteryListener, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));

		timer = new Timer();
		timer.schedule(new UpdateTimeTask(0, sXMLInfoPath), 0,
				Constants.DEV_INFO_LISTEN_DURATION * 1000);
	}

	public void updateWithNewLocation(Location location) {
		if (location != null) {
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();

			Preferences pref = new Preferences(this.getApplicationContext());
			pref.saveGeoLocation(latitude, longitude);
			L.debug("Geolocation got : " + latitude + ", " + longitude);
		} else {
			L.debug("Geolocation null");
		}
	}

	void updateWithNewSignalStrength(SignalStrengh sg) {
		Preferences pref = new Preferences(this.getApplicationContext());
		pref.saveSignalStrength(sg);
	}

	void updateWithNewBatteryLevel(int batterylevel) {
		Preferences pref = new Preferences(this.getApplicationContext());
		pref.saveBatteryLevel(batterylevel);
	}

	public void stopListeners() {
		((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).listen(
				signalStrengthListener, SignalStrengthListener.LISTEN_NONE);

		locationListener.release();

		timer.cancel();
	}

	class UpdateTimeTask extends TimerTask {
		private long startTime = 0L;
		private String sXMLInfoPath = null;
		
		public UpdateTimeTask(long startTime, String sXMLInfoPath) {
			this.startTime = startTime;
			this.sXMLInfoPath = sXMLInfoPath;
		}

		public void run() {
			Preferences prefs = new Preferences(
					airometric.getApplicationContext());
//			L.debug("isTestRunning : " + prefs.isTestRunning());
			if (prefs.isTestRunning()) {
				DeviceUtil dv = new DeviceUtil(
						airometric.getApplicationContext());
				String sListenerInfo = dv.getListenerInfo();
				//prefs.saveDeviceInfoData(sListenerInfo);
				
				String dev_info_path = FileUtil.writeToXMLFile(sXMLInfoPath,
						sListenerInfo);
				//Log.i("device info AIROMEtric.java pe",dev_info_path);
				
//				L.debug("Device info listener data written into "
//						+ dev_info_path);
			} else {
				stopListeners();
			}
		}
	}
	
	
}
