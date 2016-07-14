package com.airometric.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.airometric.classes.SignalStrengh;
import com.airometric.config.Constants;
import com.airometric.utility.L;

/**
 * Preferences - To retrieve and save from Shared Preferences in the Android
 * device
 * 
 */
public class Preferences {

	private Activity activity = null;
	private Context context = null;
	private SharedPreferences prefsPrivate;
	public static final String PREFS_PRIVATE = Constants.APP_NAME
			+ "_PREFS_PRIVATE";

	public static final String KEY_MO_TEST_RUNNIING = "MO_TEST_RUNNING";
	public static final String KEY_MT_TEST_RUNNIING = "MT_TEST_RUNNING";
	public static final String KEY_FTP_TEST_RUNNIING = "FTP_TEST_RUNNING";
	public static final String KEY_UDP_TEST_RUNNIING = "UDP_TEST_RUNNING";
	public static final String KEY_EXT_TEST_RUNNIING = "EXT_TEST_RUNNIING";
	public static final String KEY_PING_TEST_RUNNIING = "PING_TEST_RUNNING";
	public static final String KEY_BROWSER_TEST_RUNNIING = "BROWSER_TEST_RUNNING";
	public static final String KEY_VOIP_TEST_RUNNIING = "VOIP_TEST_RUNNIING";

	public static final String KEY_EXT_TEST_NAME = "EXT_TEST_NAME";
	public static final String KEY_ACCEPTED_TERMS_USAGE = "ACCEPTED_TERMS_USAGE";
	public static final String KEY_LOGGED_IN = "LOGGED_IN";
	public static final String KEY_LOGGED_USERNAME = "LOGGED_USERNAME";
	public static final String KEY_LOGGED_PASSWORD = "LOGGED_PASSWORD";
	public static final String KEY_LOGGED_USER_LEVEL = "LOGGED_USER_LEVEL";

	public static final String KEY_SIGNAL_STRENGTH = "SIGNAL_STRENGTH";
	public static final String KEY_CDMADBM = "CDMADBM";
	public static final String KEY_CDMAECIO = "CDMAECIO";
	public static final String KEY_EVDODBM = "EVDOECIO";
	public static final String KEY_EVDOECIO = "EVDOECIO";
	public static final String KEY_EVDOSNR = "EVDOSNR";
	public static final String KEY_GSM_BITRATE_ERROR = "GSMBITRATEERROR";
	public static final String KEY_GSM_SIGNAL_STRENGTH = "GSMSIGNALSTRENGTH";
	public static final String KEY_ISGSM = "ISGSM";
	public static final String KEY_LTE_SIG_STRENGTH = "LteSignalStrength";
	public static final String KEY_LTE_RSRP = "LteRsrp";
	public static final String KEY_LTE_RSRQ = "LteRsrq";
	public static final String KEY_LTE_RSSNR = "LteRssnr";
	public static final String KEY_LTE_CQI = "LteCqi";

	public static final String KEY_GEO_LATITUDE = "GEO_LATITUDE";
	public static final String KEY_GEO_LONGITUDE = "GEO_LONGITUDE";
	public static final String KEY_BATTERY_LEVEL = "BATTERY_LEVEL";

	public static final String KEY_CURRENT_MO_DEV_INFO_PATH = "CURRENT_MO_DEV_INFO_PATH";
	public static final String KEY_CURRENT_MT_DEV_INFO_PATH = "CURRENT_MO_DEV_INFO_PATH";
	public static final String KEY_CURRENT_FTP_DEV_INFO_PATH = "CURRENT_FTP_DEV_INFO_PATH";
	public static final String KEY_CURRENT_UDP_DEV_INFO_PATH = "CURRENT_UDP_DEV_INFO_PATH";
	public static final String KEY_CURRENT_EXT_DEV_INFO_PATH = "CURRENT_EXT_DEV_INFO_PATH";
	public static final String KEY_CURRENT_PING_DEV_INFO_PATH = "CURRENT_PING_DEV_INFO_PATH";
	public static final String KEY_CURRENT_BROWSER_DEV_INFO_PATH = "CURRENT_BROWSER_DEV_INFO_PATH";
	public static final String KEY_CURRENT_VOIP_DEV_INFO_PATH = "CURRENT_VOIP_DEV_INFO_PATH";

	public static final String KEY_CURRENT_MO_LOGCAT_PATH = "CURRENT_MO_LOGCAT_PATH";
	public static final String KEY_CURRENT_MT_LOGCAT_PATH = "CURRENT_MO_LOGCAT_PATH";
	public static final String KEY_CURRENT_FTP_LOGCAT_PATH = "CURRENT_FTP_LOGCAT_PATH";
	public static final String KEY_CURRENT_UDP_LOGCAT_PATH = "CURRENT_UDP_LOGCAT_PATH";
	public static final String KEY_CURRENT_PING_LOGCAT_PATH = "CURRENT_PING_LOGCAT_PATH";
	public static final String KEY_CURRENT_BROWSER_LOGCAT_PATH = "CURRENT_BROWSER_LOGCAT_PATH";
	public static final String KEY_CURRENT_VOIP_LOGCAT_PATH = "CURRENT_VOIP_LOGCAT_PATH";

	public static final String KEY_CURRENT_TEST_NAME = "CURRENT_TEST_NAME";
	public static final String KEY_CURRENT_TEST_NUMBER = "CURRENT_TEST_NUMBER";
	public static final String KEY_CURRENT_TEST_CANCELLED = "CURRENT_TEST_CANCELLED";
	
	public static final String KEY_MARKETPLACE_LIST = "MARKETPLACE_LIST";
	
	public static final String KEY_SELECTED_MARKET_PALCE = "KEY_SELECTED_MARKET_PALCE";
	public static final String KEY_SELECTED_MARKET_PLACE_ID = "KEY_SELECTED_MARKET_PLACE_ID";
	public static final String KEY_TEST_NAME = "KEY_TEST_NAME";
	public static final String KEY_IS_TEST_CONFIG_SET = "KEY_IS_TEST_CONFIG_SET";
	
	public static final String KEY_SPINNER_SELECTED_ITEM_POS = "KEY_SPINNER_SELECTED_ITEM_POS";
	
	public static final String KEY_USER_NAME = "KEY_USER_NAME";
	public static final String KEY_PASSWORD = "KEY_PASSWORD";
	public static final String KEY_TEST_COUNTER = "TEST_COUNTER";
	public static final String KEY_DEVICE_INFO_DATA = "DEVICE_INFO_DATA";
	
	public static final String KEY_CALL_DROP_TIME = "CALL_DROP_TIME";
	public static final String KEY_CALL_DROP_TIMEINGS = "CALL_DROP_TIMEINGS";
	
	
	public Preferences(Activity thisActivity) {
		activity = thisActivity;
		prefsPrivate = activity.getSharedPreferences(PREFS_PRIVATE,
				Context.MODE_PRIVATE);
	}

	public Preferences(PreferenceActivity thisActivity) {
		activity = thisActivity;
		prefsPrivate = activity.getSharedPreferences(PREFS_PRIVATE,
				Context.MODE_PRIVATE);
	}

	public Preferences(Context context) {
		this.context = context;
		prefsPrivate = this.context.getSharedPreferences(PREFS_PRIVATE,
				Context.MODE_PRIVATE);
	}

	/**
	 * Method to save the values with that key in SharedPreferences.
	 * 
	 * @param keyValue
	 *            Key name to be stored.
	 * @param strValue
	 *            Value to be stored with that key name.
	 */

	public void putValue(String keyValue, String strValue) {
		Editor prefsPrivateEditor = prefsPrivate.edit();
		prefsPrivateEditor.putString(keyValue, strValue);
		prefsPrivateEditor.commit();
	}

	/**
	 * Method to save the values with that key in SharedPreferences.
	 * 
	 * @param keyValue
	 *            Key name to be stored.
	 * @param strValue
	 *            Value to be stored with that key name.
	 */

	public void putValue(String keyValue, float dValue) {
		Editor prefsPrivateEditor = prefsPrivate.edit();
		prefsPrivateEditor.putFloat(keyValue, dValue);
		prefsPrivateEditor.commit();
	}

	/**
	 * Method to save the values with that key in SharedPreferences.
	 * 
	 * @param keyValue
	 *            Key name to be stored.
	 * @param strValue
	 *            Value to be stored with that key name.
	 */

	public void putValue(String keyValue, int dValue) {
		Editor prefsPrivateEditor = prefsPrivate.edit();
		prefsPrivateEditor.putInt(keyValue, dValue);
		prefsPrivateEditor.commit();
	}

	/**
	 * Method to save the boolean value with that key in SharedPreferences.
	 * 
	 * @param keyValue
	 *            Key name to be stored.
	 * @param bValue
	 *            Value to be stored with that key name.
	 */

	public void putValue(String keyValue, boolean bValue) {
		Editor prefsPrivateEditor = prefsPrivate.edit();
		prefsPrivateEditor.putBoolean(keyValue, bValue);
		prefsPrivateEditor.commit();
	}

	/**
	 * Method to get the value from SharedPreferences.
	 * 
	 * @param keyValue
	 *            The key name to be retrieved.
	 * @param defualtValue
	 *            Default value if key not found.
	 * @return Returns the stored value of the key
	 */

	public boolean getValue(String keyValue, boolean bValue) {
		return prefsPrivate.getBoolean(keyValue, bValue);
	}

	/**
	 * Method to get the value from SharedPreferences.
	 * 
	 * @param keyValue
	 *            The key name to be retrieved.
	 * @param defualtValue
	 *            Default value if key not found.
	 * @return Returns the stored value of the key
	 */

	public float getValue(String keyValue, float val) {
		return prefsPrivate.getFloat(keyValue, val);
	}

	/**
	 * Method to get the value from SharedPreferences.
	 * 
	 * @param keyValue
	 *            The key name to be retrieved.
	 * @param defualtValue
	 *            Default value if key not found.
	 * @return Returns the stored value of the key
	 */

	public int getValue(String keyValue, int val) {
		return prefsPrivate.getInt(keyValue, val);
	}

	/**
	 * Method to get the value from SharedPreferences.
	 * 
	 * @param keyValue
	 *            The key name to be retrieved.
	 * @param defualtValue
	 *            Default value if key not found.
	 * @return Returns the stored value of the key
	 */

	public String getValue(String keyValue, String defualtValue) {
		return prefsPrivate.getString(keyValue, defualtValue);
	}

	public boolean isTestRunning() {
		boolean isTestRunning = false;
		if (isMOTestRunning())
			isTestRunning = true;
		if (isMTTestRunning())
			isTestRunning = true;
		if (isFTPTestRunning())
			isTestRunning = true;
		if (isUDPTestRunning())
			isTestRunning = true;
		if (isPingTestRunning())
			isTestRunning = true;
		if (isBrowserTestRunning())
			isTestRunning = true;
		if (isVOIPTestRunning())
			isTestRunning = true;
		if (isExternalTestRunning())
			isTestRunning = true;
		return isTestRunning;
	}

	public void setMOTestRunningState(boolean sRunState) {
		putValue(KEY_MO_TEST_RUNNIING, sRunState);
	}

	public boolean isMOTestRunning() {
		return getValue(KEY_MO_TEST_RUNNIING, false);
	}

	public void setMTTestRunningState(boolean sRunState) {
		putValue(KEY_MT_TEST_RUNNIING, sRunState);
	}

	public boolean isMTTestRunning() {
		return getValue(KEY_MT_TEST_RUNNIING, false);
	}

	public void setFTPTestRunningState(boolean sRunState) {
		putValue(KEY_FTP_TEST_RUNNIING, sRunState);
	}

	public boolean isFTPTestRunning() {
		return getValue(KEY_FTP_TEST_RUNNIING, false);
	}

	public void setUDPTestRunningState(boolean sRunState) {
		putValue(KEY_UDP_TEST_RUNNIING, sRunState);
	}

	public boolean isUDPTestRunning() {
		return getValue(KEY_UDP_TEST_RUNNIING, false);
	}

	public void setPingTestRunningState(boolean sRunState) {
		putValue(KEY_PING_TEST_RUNNIING, sRunState);
	}

	public boolean isPingTestRunning() {
		return getValue(KEY_PING_TEST_RUNNIING, false);
	}

	public void setBrowserTestRunningState(boolean sRunState) {
		putValue(KEY_BROWSER_TEST_RUNNIING, sRunState);
	}

	public boolean isBrowserTestRunning() {
		return getValue(KEY_BROWSER_TEST_RUNNIING, false);
	}

	public void setVOIPTestRunningState(boolean sRunState) {
		putValue(KEY_VOIP_TEST_RUNNIING, sRunState);
	}

	public boolean isVOIPTestRunning() {
		return getValue(KEY_VOIP_TEST_RUNNIING, false);
	}

	public void setExternalTestRunningState(boolean sRunState) {
		putValue(KEY_EXT_TEST_RUNNIING, sRunState);
	}

	public boolean isExternalTestRunning() {
		return getValue(KEY_EXT_TEST_RUNNIING, false);
	}

	public void setExternalTestName(String sTestName) {
		putValue(KEY_EXT_TEST_NAME, sTestName);
	}

	public String getExternalTestName() {
		return getValue(KEY_EXT_TEST_NAME, "");
	}

	public void setAsTermsOfUsageAccepted(boolean status) {
		putValue(KEY_ACCEPTED_TERMS_USAGE, status);
	}

	public boolean isAcceptedTermsOfUsage() {
		return getValue(KEY_ACCEPTED_TERMS_USAGE, false);
	}

	public void setLoggedInStatus(boolean status) {
		putValue(KEY_LOGGED_IN, status);
	}

	public boolean isLoggedIn() {
		return getValue(KEY_LOGGED_IN, false);
	}

	public void saveUsername(String sUsername) {
		putValue(KEY_LOGGED_USERNAME, sUsername);
	}

	public String getUsername() {
		return getValue(KEY_LOGGED_USERNAME, "");
	}

	public void savePassword(String sPassword) {
		putValue(KEY_LOGGED_PASSWORD, sPassword);
	}

	public String getPassword() {
		return getValue(KEY_LOGGED_PASSWORD, "");
	}

	public void saveUserLevel(String sUserLevel) {
		putValue(KEY_LOGGED_USER_LEVEL, sUserLevel);
	}

	public String getUserLevel() {
		return getValue(KEY_LOGGED_USER_LEVEL, "");
	}

	public void saveSignalStrength(SignalStrengh signalStrength) {
		putValue(KEY_SIGNAL_STRENGTH,String.valueOf(signalStrength.strsignal_strength));
		putValue(KEY_CDMADBM, signalStrength.CDMADbm);
		putValue(KEY_CDMAECIO, signalStrength.CDMAEcio);
		putValue(KEY_EVDODBM, signalStrength.EvdoDbm);
		putValue(KEY_EVDOECIO, signalStrength.EvdoEcio);
		putValue(KEY_EVDOSNR, signalStrength.EvdoSnr);
		putValue(KEY_GSM_BITRATE_ERROR, signalStrength.GSMBitRateError);
		putValue(KEY_GSM_SIGNAL_STRENGTH, signalStrength.GSMSignalStrength);
		putValue(KEY_ISGSM, signalStrength.blisGSM);
		putValue(KEY_LTE_SIG_STRENGTH, signalStrength.LteSignalStrength);
		putValue(KEY_LTE_RSRP, signalStrength.LteRsrp);
		putValue(KEY_LTE_RSRQ, signalStrength.LteRsrq);
		putValue(KEY_LTE_RSSNR, signalStrength.LteRssnr);
		putValue(KEY_LTE_CQI, signalStrength.LteCqi);

		// log("Saved Signal Strength : " + signalStrength.toString());
	}

	public SignalStrengh getSignalStrengthObj() {
		SignalStrengh sg = new SignalStrengh(getCDMADbm(), getCdmaEcio(),
				getEvdoDbm(), getEvdoEcio(), getEvdoSnr(), getGCMBitError(),
				getGSMSignalStrength(), getSignalStrength(), getIsGSM(),
				getLTESignalStrength(), getLTERSRP(), getLTERSRQ(),
				getLTERSSNR(), getLTECQI());

		return sg;
	}

	public String getSignalStrength() {
		return getValue(KEY_SIGNAL_STRENGTH, "");
	}

	public String getCDMADbm() {
		return getValue(KEY_CDMADBM, "");
	}

	public String getCdmaEcio() {
		return getValue(KEY_CDMAECIO, "");
	}

	public String getEvdoDbm() {
		return getValue(KEY_EVDODBM, "");
	}

	public String getEvdoEcio() {
		return getValue(KEY_EVDOECIO, "");
	}

	public String getEvdoSnr() {
		return getValue(KEY_EVDOSNR, "");
	}

	public String getGCMBitError() {
		return getValue(KEY_GSM_BITRATE_ERROR, "");
	}

	public String getGSMSignalStrength() {
		return getValue(KEY_GSM_SIGNAL_STRENGTH, "");
	}

	public boolean getIsGSM() {
		return getValue(KEY_ISGSM, false);
	}

	public String getLTESignalStrength() {
		return getValue(KEY_LTE_SIG_STRENGTH, "");
	}

	public String getLTERSRP() {
		return getValue(KEY_LTE_RSRP, "");
	}

	public String getLTERSRQ() {
		return getValue(KEY_LTE_RSRQ, "");
	}

	public String getLTERSSNR() {
		return getValue(KEY_LTE_RSSNR, "");
	}

	public String getLTECQI() {
		return getValue(KEY_LTE_CQI, "");
	}

	public void saveGeoLocation(double dLat, double dLong) {
		putValue(KEY_GEO_LATITUDE, Double.valueOf(dLat).toString());
		putValue(KEY_GEO_LONGITUDE, Double.valueOf(dLong).toString());
	}

	public double getLatitude() {
		return Double.parseDouble(getValue(KEY_GEO_LATITUDE, "0"));
	}

	public double getLongitude() {
		return Double.parseDouble(getValue(KEY_GEO_LONGITUDE, "0"));
	}

	public void saveBatteryLevel(int batterylevel) {
		putValue(KEY_BATTERY_LEVEL, "" + batterylevel);
	}

	public int getBatteryLevel() {
		return Integer.parseInt(getValue(KEY_BATTERY_LEVEL, "0"));
	}

	public void saveCurrentTestName(String sTestName) {
		putValue(KEY_CURRENT_TEST_NAME, sTestName);
	}

	public String getCurrentTestName() {
		return getValue(KEY_CURRENT_TEST_NAME, "");
	}

	public void saveCurrentTestNumber(int test_number) {
		putValue(KEY_CURRENT_TEST_NUMBER, test_number);
	}

	public int getCurrentTestNumber() {
		return getValue(KEY_CURRENT_TEST_NUMBER, 0);
	}

	public void setAsTestCanceled(boolean state) {
		putValue(KEY_CURRENT_TEST_CANCELLED, state);
	}

	public boolean isTestCanceled() {
		return getValue(KEY_CURRENT_TEST_CANCELLED, false);
	}

	void log(String msg) {
		L.debug(msg);
	}

	public String getCurrentSignalStrength() {
		if (getIsGSM())
			return getGSMSignalStrength();
		else
			return getLTESignalStrength();
	}

	public void clearFilePaths() {
		Editor prefsEditor = prefsPrivate.edit();

		prefsEditor.putString(Preferences.KEY_CURRENT_MO_DEV_INFO_PATH, "");
		prefsEditor.putString(Preferences.KEY_CURRENT_MO_LOGCAT_PATH, "");

		prefsEditor.putString(Preferences.KEY_CURRENT_MT_DEV_INFO_PATH, "");
		prefsEditor.putString(Preferences.KEY_CURRENT_MT_LOGCAT_PATH, "");

		prefsEditor.putString(Preferences.KEY_CURRENT_FTP_DEV_INFO_PATH, "");
		prefsEditor.putString(Preferences.KEY_CURRENT_FTP_LOGCAT_PATH, "");

		prefsEditor.putString(Preferences.KEY_CURRENT_UDP_DEV_INFO_PATH, "");
		prefsEditor.putString(Preferences.KEY_CURRENT_UDP_LOGCAT_PATH, "");

		prefsEditor.putString(Preferences.KEY_CURRENT_PING_DEV_INFO_PATH, "");
		prefsEditor.putString(Preferences.KEY_CURRENT_PING_LOGCAT_PATH, "");

		prefsEditor
				.putString(Preferences.KEY_CURRENT_BROWSER_DEV_INFO_PATH, "");
		prefsEditor.putString(Preferences.KEY_CURRENT_BROWSER_LOGCAT_PATH, "");

		prefsEditor.putString(Preferences.KEY_CURRENT_VOIP_DEV_INFO_PATH, "");
		prefsEditor.putString(Preferences.KEY_CURRENT_VOIP_LOGCAT_PATH, "");

		prefsEditor.putString(Preferences.KEY_CURRENT_EXT_DEV_INFO_PATH, "");

		prefsEditor.commit();
	}
	
	public void saveMarketplaces(Map<String, String> stringMap) {
		if (stringMap != null) {
			SharedPreferences keys = activity.getSharedPreferences(KEY_MARKETPLACE_LIST, Context.MODE_PRIVATE);
			Editor editor = keys.edit();
			for (String s : stringMap.keySet()) {
				editor.putString(s, stringMap.get(s));
			}
			editor.commit();
		}
	}
	
	public Map<String, String> loadMarketplaces() {
		SharedPreferences keys = activity.getSharedPreferences(KEY_MARKETPLACE_LIST, Context.MODE_PRIVATE);
		return (Map<String, String>) keys.getAll();
	}
	
	//that code save device info file data into preference till the logout not clicked.
	public void saveDeviceInfoData(HashMap<String, String> stringMap) {
		
		if (stringMap != null) {
			SharedPreferences keys = context.getSharedPreferences(KEY_DEVICE_INFO_DATA, Context.MODE_PRIVATE);
			Editor editor = keys.edit();
			for (String s : stringMap.keySet()) {
				editor.putString(s, stringMap.get(s));
			}
			editor.commit();
		}
		
	}
	
	public HashMap<String, String> loadDeviceInfoData(Context context) {
		SharedPreferences keys = context.getSharedPreferences(KEY_DEVICE_INFO_DATA, Context.MODE_PRIVATE);
		return (HashMap<String, String>) keys.getAll();
	}
	public void emptyDeviceInfoData(Context context) {
		SharedPreferences keys = context.getSharedPreferences(KEY_DEVICE_INFO_DATA, Context.MODE_PRIVATE);
		Editor editor = keys.edit();
		editor.clear();
		editor.commit();
	}
	public void saveTestCounterValue(int iCounter) {
		putValue(KEY_TEST_COUNTER, iCounter);
	}

	public int getTestCounterValue() {
		return getValue(KEY_TEST_COUNTER, 0);
	}
	
	//Store,retrive call drop timings 
	public void saveCallDropTimeData(Context context,String stringMap) {
		
		if (stringMap != null) {
			SharedPreferences keys = activity.getSharedPreferences(KEY_CALL_DROP_TIMEINGS, Context.MODE_PRIVATE);
			Editor editor = keys.edit();
			//for(int i=0;i>stringMap.size();i++) {
				editor.putString(KEY_CALL_DROP_TIME, stringMap);
			//}
			editor.commit();
		}
	}
	public String loadCallDropTimeData(Context context) {
		SharedPreferences keys = activity.getSharedPreferences(KEY_CALL_DROP_TIMEINGS, Context.MODE_PRIVATE);
		return keys.getString(KEY_CALL_DROP_TIME, "");
	}
	public void emptyCallDropTimeData(Context context) {
		SharedPreferences keys = activity.getSharedPreferences(KEY_CALL_DROP_TIMEINGS, Context.MODE_PRIVATE);
		Editor editor = keys.edit();
		editor.clear();
		editor.commit();
	}
	
	
}
