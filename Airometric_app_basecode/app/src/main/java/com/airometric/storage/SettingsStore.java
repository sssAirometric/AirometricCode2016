package com.airometric.storage;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceActivity;

/**
 * SettingsStore - To retrieve and save from Settings
 */
public class SettingsStore {

	private Activity activity = null;
	private Context context = null;
	private Preferences prefs;

	public SettingsStore(Activity thisActivity) {
		activity = thisActivity;
		prefs = new Preferences(activity);
	}


	public SettingsStore(Context context) {
		this.context = context;
		prefs = new Preferences(this.context);
	}

	public SettingsStore(PreferenceActivity thisActivity) {
		activity = thisActivity;
		prefs = new Preferences(activity);
	}

	public void saveLogin(String sUsername, String sPassword) {
		prefs.saveUsername(sUsername);
		prefs.savePassword(sPassword);		 
	}

	public String getMODeviceInfoPath() {
		return prefs.getValue(Preferences.KEY_CURRENT_MO_DEV_INFO_PATH, "");
	}

	public String getMOLogcatPath() {
		return prefs.getValue(Preferences.KEY_CURRENT_MO_LOGCAT_PATH, "");
	}

	public String getMTDeviceInfoPath() {
		return prefs.getValue(Preferences.KEY_CURRENT_MT_DEV_INFO_PATH, "");
	}

	public String getMTLogcatPath() {
		return prefs.getValue(Preferences.KEY_CURRENT_MT_LOGCAT_PATH, "");
	}

	public String getFTPDeviceInfoPath() {
		return prefs.getValue(Preferences.KEY_CURRENT_FTP_DEV_INFO_PATH, "");
	}

	public String getFTPLogcatPath() {
		return prefs.getValue(Preferences.KEY_CURRENT_FTP_LOGCAT_PATH, "");
	}

	public String getUDPDeviceInfoPath() {
		return prefs.getValue(Preferences.KEY_CURRENT_UDP_DEV_INFO_PATH, "");
	}

	public String getUDPLogcatPath() {
		return prefs.getValue(Preferences.KEY_CURRENT_UDP_LOGCAT_PATH, "");
	}

	public String getPingDeviceInfoPath() {
		return prefs.getValue(Preferences.KEY_CURRENT_PING_DEV_INFO_PATH, "");
	}

	public String getPingLogcatPath() {
		return prefs.getValue(Preferences.KEY_CURRENT_PING_LOGCAT_PATH, "");
	}

	public String getBrowserDeviceInfoPath() {
		return prefs
				.getValue(Preferences.KEY_CURRENT_BROWSER_DEV_INFO_PATH, "");
	}

	public String getBrowserLogcatPath() {
		return prefs.getValue(Preferences.KEY_CURRENT_BROWSER_LOGCAT_PATH, "");
	}

	public String getVOIPDeviceInfoPath() {
		return prefs.getValue(Preferences.KEY_CURRENT_VOIP_DEV_INFO_PATH, "");
	}

	public String getVOIPLogcatPath() {
		return prefs.getValue(Preferences.KEY_CURRENT_VOIP_LOGCAT_PATH, "");
	}
	public String getExternalDeviceInfoPath() {
		return prefs.getValue(Preferences.KEY_CURRENT_EXT_DEV_INFO_PATH, "");
	}
	

}
