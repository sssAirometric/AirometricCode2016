package com.airoremote;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.telephony.TelephonyManager;

import com.airoremote.classes.SignalStrengh;
import com.airoremote.config.Constants;
import com.airoremote.listeners.BatteryChangeBroadcastReceiver;
import com.airoremote.listeners.GeoLocationListener;
import com.airoremote.listeners.SignalStrengthListener;
import com.airoremote.storage.Preferences;
import com.airoremote.utility.DeviceUtil;
import com.airoremote.utility.FileUtil;
import com.airoremote.utility.L;

public class Airometrics extends Application {
	SignalStrengthListener signalStrengthListener;
	GeoLocationListener locationListener;
	BatteryChangeBroadcastReceiver batteryListener;
	Timer timer;
	Airometrics airometrics;

	public void onCreate() {
		super.onCreate();
		airometrics = this;
		FileUtil.init();
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
					airometrics.getApplicationContext());
			L.debug("isTestRunning : " + prefs.isTestRunning());
			if (prefs.isTestRunning()) {
				DeviceUtil dv = new DeviceUtil(
						airometrics.getApplicationContext());
				String sListenerInfo = dv.getListenerInfo();

				String dev_info_path = FileUtil.writeToXMLFile(sXMLInfoPath,
						sListenerInfo);
				L.debug("Device info listener data written into "
						+ dev_info_path);
			} else {
				stopListeners();
			}
		}
	}
}
