package com.airoremote.utility.runners;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Handler;

import com.airoremote.Airometric;
import com.airoremote.AppActivity;
import com.airoremote.TestTypeActivity;
import com.airoremote.classes.TestConfig;
import com.airoremote.classes.VOIPTestConfig;
import com.airoremote.config.Constants;
import com.airoremote.config.StringUtils;
import com.airoremote.storage.Preferences;
import com.airoremote.storage.SettingsStore;
import com.airoremote.utility.DeviceUtil;
import com.airoremote.utility.FileUtil;
import com.airoremote.utility.L;
import com.airoremote.utility.NotificationUtil;
import com.airoremote.utility.TimeUtil;

public class VOIPTestRunner {

	AppActivity context;
	TestConfig testconfig;
	private Handler handler = new Handler();
	Preferences pref;
	SettingsStore settings;
	VOIPTestConfig VOIPTestConfigObj;
	long StartRXBytes = 0, StartRXSegments = 0;
	long StartTXBytes = 0, StartTXSegments = 0;
	int uid, deviceOSVersion;
	Timer timer;
	StartTestTask uploadTask;

	public VOIPTestRunner(AppActivity context, TestConfig testconfig) {
		this.context = context;
		this.testconfig = testconfig;
		pref = new Preferences(context);
		settings = new SettingsStore(context);
	}

	public void startTest() {

		deviceOSVersion = android.os.Build.VERSION.SDK_INT;		
		L.debug("DEVICE OS VERSION: " + deviceOSVersion);
		
		VOIPTestConfigObj = testconfig.getVOIPTestConfig();

		/*
		 * String sTestName = testconfig.getTestName() +
		 * StringUtils.TEST_CYCLE_APPEND_FILE + testconfig.getTestCycle();
		 */
		
		String sTestName = pref.getValue(Preferences.KEY_TEST_NAME, "")
				+ StringUtils.TEST_CYCLE_APPEND_FILE
				+ testconfig.getTestCycle();

		pref.setVOIPTestRunningState(true);

		DeviceUtil dv = new DeviceUtil(context);
		String sDeviceInfoXML = dv.getDeviceInfo(
				StringUtils.FILE_CODE_TEST_TYPE_VOIP, sTestName,
				pref.getUsername(),
				pref.getValue(Preferences.KEY_SELECTED_MARKET_PLACE_ID, ""));
		L.debug(sDeviceInfoXML);

		String sCurrTime = TimeUtil.getCurrentTimeFilename();

		FileUtil.CURRENT_VOIP_TESTTIME = sCurrTime;
		String path = FileUtil.VOIP_LOG_DIR + "deviceinfo" + "_" + sTestName
				+ "_" + sCurrTime + ".xml";
		File fle = new File(path);
		if (fle.exists()) {
			fle.delete();
		}
		String dev_info_path = FileUtil.writeToXMLFile(path, sDeviceInfoXML);
		L.debug("Device info initial data written into " + dev_info_path);

		pref.putValue(Preferences.KEY_CURRENT_VOIP_DEV_INFO_PATH, dev_info_path);

		String logcat_path = FileUtil.VOIP_LOG_DIR
				+ StringUtils.FILE_CODE_TEST_TYPE_VOIP + "_" + dv.getIMEI()
				+ "_" + sTestName + "_" + sCurrTime + ".txt";

		File log_fle = new File(logcat_path);

		try {
			log_fle.createNewFile();
		} catch (IOException e) {
		}
		pref.putValue(Preferences.KEY_CURRENT_VOIP_LOGCAT_PATH,
				log_fle.getAbsolutePath());
		try {
			Airometric app = (Airometric) context.getApplication();
			app.startListeners(dev_info_path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		NotificationUtil.cancelNotification(context,
				StringUtils.TEST_TYPE_CODE_VOIP);

		uploadTask = new StartTestTask();
		uploadTask.execute();
		Constants.CurrentTask = uploadTask;

		NotificationUtil.showRunningNotification(context,
				StringUtils.TEST_TYPE_CODE_VOIP);
		context.showActivity(TestTypeActivity.class);
	}

	long test_duration_secs, completed_duration_secs = 0, pass = 0;
	public long CurrRXBytes;
	public long CurrRXSegments;

	void startTesting() {
		test_duration_secs = Long.parseLong(VOIPTestConfigObj.sTestDuration) * 60; // secs

		DeviceUtil.clearLogcat();

		timer = new Timer();
		uid = android.os.Process.myUid();
		uid = getSkypeId();
		if (Constants.OS_VERSION_JELLY_BEAN_MR2 <= deviceOSVersion) {
			//StartRXBytes = DeviceUtil.getStats(uid).getReceiveCount(); 
			 StartRXBytes = TrafficStats.getUidRxBytes(uid);
		} else {
			StartRXBytes = TrafficStats.getUidTcpRxBytes(uid);
		}

		StartRXSegments = TrafficStats.getUidTcpRxSegments(uid);
		context.debug("Initial RX bytes - " + StartRXBytes
				+ ", Initial RX Segments - " + StartRXSegments);
/*Constants.OS_VERSION_JELLY_BEAN_MR2 == android.os.Build.VERSION.SDK_INT
				|| Constants.OS_VERSION_KIT_KAT == android.os.Build.VERSION.SDK_INT 
				|| Constants.OS_VERSION_LOLLIPOP == android.os.Build.VERSION.SDK_INT
				|| Constants.OS_VERSION_MARSHMALLOW == android.os.Build.VERSION.SDK_INT*/
		if (Constants.OS_VERSION_JELLY_BEAN_MR2 <= deviceOSVersion) {
			//StartTXBytes = DeviceUtil.getStats(uid).getSendCount();
			StartTXBytes = TrafficStats.getUidTxBytes(uid);
		} else {
			StartTXBytes = TrafficStats.getUidTcpTxBytes(uid);
		}

		StartTXSegments = TrafficStats.getUidTcpTxSegments(uid);

		context.debug("Initial TX bytes - " + StartTXBytes
				+ ", Initial TX segments - " + StartTXSegments);
		timer.schedule(new PageLoadTimeTask(), 0,
				Constants.BROWSER_LOG_INTERVAL * 1000);

		handler.postDelayed(RunnableCompleted, test_duration_secs * 1000);

	}

	int getSkypeId() {
		final PackageManager pm = context.getPackageManager();
		List<ApplicationInfo> packages = pm
				.getInstalledApplications(PackageManager.GET_META_DATA);
		int UID = 0;
		for (ApplicationInfo packageInfo : packages) {
			// L.debug(packageInfo.name + ";; package -->"
			// + packageInfo.packageName);
			if (packageInfo.packageName.indexOf("com.skype.raider") != -1
					|| packageInfo.packageName.indexOf("com.skype") != -1) {
				UID = packageInfo.uid;
			}
		}
		return UID;
	}

	class PageLoadTimeTask extends TimerTask {

		long CurrTXBytes, CurrTXSegments;
		public PageLoadTimeTask() {
		}

		public void run() {
			if (pref.isVOIPTestRunning()) {
				/*long CurrRXBytes = TrafficStats.getUidTcpRxBytes(uid);
				long CurrRXSegments = TrafficStats.getUidTcpRxSegments(uid);*/
				//Code Added by ankit on 08/06/16 for version.
				if (Constants.OS_VERSION_JELLY_BEAN_MR2 <= deviceOSVersion) {
					CurrRXBytes = TrafficStats.getUidRxBytes(uid);
					CurrRXSegments = TrafficStats.getUidTcpRxSegments(uid);
				} else {
					CurrRXBytes = TrafficStats.getUidTcpRxBytes(uid);
					CurrRXSegments = TrafficStats.getUidTcpRxSegments(uid);
				}
				context.debug("StartRXBytes bytes - " + StartRXBytes
						+ ", CurrRXBytes - " + CurrRXBytes
						+ ", CurrRXSegments - " + CurrRXSegments);
				long RXBytes = CurrRXBytes - StartRXBytes;
				long RXSegments = CurrRXSegments - StartRXSegments;
				L.log_voip_rx(settings.getVOIPLogcatPath(), RXBytes, RXSegments);

				/*long CurrTXBytes = TrafficStats.getUidTcpTxBytes(uid);
				long CurrTXSegments = TrafficStats.getUidTcpTxSegments(uid);*/
				//Code Added by ankit on 08/06/16 for version. 
				if (Constants.OS_VERSION_JELLY_BEAN_MR2 <= deviceOSVersion) {
					CurrTXBytes = DeviceUtil.getStats(uid).getSendCount();
					CurrTXSegments = TrafficStats.getUidTcpTxSegments(uid);
				} else {
					CurrTXBytes = TrafficStats.getUidTcpTxBytes(uid);
					CurrTXSegments = TrafficStats.getUidTcpTxSegments(uid);
				}
				
				context.debug("StartTXBytes bytes - " + StartTXBytes
						+ ", CurrTXBytes - " + CurrTXBytes
						+ ", CurrTXSegments - " + CurrTXSegments);
				long TXBytes = CurrTXBytes - StartTXBytes;
				long TXSegments = CurrTXSegments - StartTXSegments;
				L.log_voip_tx(settings.getVOIPLogcatPath(), TXBytes, TXSegments);
			} else
				timer.cancel();
		}
	}

	private class StartTestTask extends AsyncTask<URL, Integer, String> {

		public StartTestTask() {
		}

		protected void onProgressUpdate(Integer... progress) {

		}

		protected void onPostExecute(Long result) {

		}

		@Override
		protected String doInBackground(URL... urls) {
			startTesting();
			return "Started";
		}
	}

	Runnable RunnableCompleted = new Runnable() {
		public void run() {
			if (!pref.isTestCanceled() && pref.isTestRunning()) {
				L.debug("RunnableCompleted...");
				pref.setVOIPTestRunningState(false);
				new ResultUploader(context, pref.getValue(Preferences.KEY_TEST_NAME, "")
						+ StringUtils.TEST_CYCLE_APPEND_FILE
						+ testconfig.getTestCycle(),
						StringUtils.TEST_TYPE_CODE_VOIP,
						settings.getVOIPDeviceInfoPath(),
						settings.getVOIPLogcatPath()) {
					public void resultUploaded(String code, String desc) {
						doResultUploaded(code, desc);
					};
				};
			}
		}
	};

	public void doResultUploaded(String code, String desc) {

	};
}
