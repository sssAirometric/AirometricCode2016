package com.airometric.utility.runners;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.airometric.Airometric;
import com.airometric.AppActivity;
import com.airometric.TestTypeActivity;
import com.airometric.api.ResponseCodes;
import com.airometric.classes.PingTestConfig;
import com.airometric.classes.TestConfig;
import com.airometric.config.Constants;
import com.airometric.config.Messages;
import com.airometric.config.StringUtils;
import com.airometric.storage.Preferences;
import com.airometric.storage.SettingsStore;
import com.airometric.utility.DeviceUtil;
import com.airometric.utility.FileUtil;
import com.airometric.utility.L;
import com.airometric.utility.NotificationUtil;
import com.airometric.utility.TimeUtil;

public class PingTestRunner {

	AppActivity context;
	TestConfig testconfig;
	private Handler handler = new Handler();
	Preferences pref;
	SettingsStore settings;
	String sServerIP, sNoOfCycles;
	PingTestConfig PingTestConfigObj;
	StartTestTask uploadTask;
	Thread thrdPing;

	public PingTestRunner(AppActivity context, TestConfig testconfig) {
		this.context = context;
		this.testconfig = testconfig;
		pref = new Preferences(context);
		settings = new SettingsStore(context);
	}

	public void startTest() {

		PingTestConfigObj = testconfig.getPingTestConfig();
		
		String sTestName1 = testconfig.getTestName()
				+ StringUtils.TEST_CYCLE_APPEND_FILE
				+ testconfig.getTestCycle();
		
		L.debug(" %%%%%%%%%% sTestName1: %%%%%%%%%% " + sTestName1);
		
		String sTestName = pref.getValue(Preferences.KEY_TEST_NAME, "")
				+ StringUtils.TEST_CYCLE_APPEND_FILE
				+ testconfig.getTestCycle();

		L.debug(" %%%%%%%%%% sTestName1: %%%%%%%%%% " + sTestName);
		
		
		pref.setPingTestRunningState(true);

		DeviceUtil dv = new DeviceUtil(context);
		String sDeviceInfoXML = dv.getDeviceInfo(
				StringUtils.FILE_CODE_TEST_TYPE_PING, sTestName,
				pref.getUsername(), pref.getValue(Preferences.KEY_SELECTED_MARKET_PLACE_ID, ""));
		L.debug(sDeviceInfoXML);

		String sCurrTime = TimeUtil.getCurrentTimeFilename();

		FileUtil.CURRENT_PING_TESTTIME = sCurrTime;
		String path = FileUtil.PING_LOG_DIR + "deviceinfo" + "_" + sTestName
				+ "_" + sCurrTime + ".xml";
		File fle = new File(path);
		if (fle.exists()) {
			fle.delete();
		}
		String dev_info_path = FileUtil.writeToXMLFile(path, sDeviceInfoXML);
		L.debug("Device info initial data written into " + dev_info_path);

		pref.putValue(Preferences.KEY_CURRENT_PING_DEV_INFO_PATH, dev_info_path);

		String logcat_path = FileUtil.PING_LOG_DIR
				+ StringUtils.FILE_CODE_TEST_TYPE_PING + "_" + dv.getIMEI()
				+ "_" + sTestName + "_" + sCurrTime + ".txt";

		File log_fle = new File(logcat_path);

		try {
			log_fle.createNewFile();
		} catch (IOException e) {
		}
		pref.putValue(Preferences.KEY_CURRENT_PING_LOGCAT_PATH,
				log_fle.getAbsolutePath());
		try {
			Airometric app = (Airometric) context.getApplication();
			app.startListeners(dev_info_path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		NotificationUtil.cancelNotification(context,
				StringUtils.TEST_TYPE_CODE_PING);

		uploadTask = new StartTestTask();
		uploadTask.execute();
		Constants.CurrentTask = uploadTask;

		NotificationUtil.showRunningNotification(context,
				StringUtils.TEST_TYPE_CODE_PING);
		context.showActivity(TestTypeActivity.class);
	}

	long no_of_cycles = 0, pass = 0;

	void startTesting() {
		sServerIP = PingTestConfigObj.sServerIP;
		sNoOfCycles = PingTestConfigObj.sCycles;
		no_of_cycles = Long.parseLong(sNoOfCycles);

		context.print("startTesting()... sServerIP  -> " + sServerIP
				+ ", NoOfCycles  -> " + sNoOfCycles);
		DeviceUtil.clearLogcat();

		thrdPing = new Thread() {
			public void run() {
				doPing();
			}
		};
		thrdPing.start();

	}

	protected void doPing() {

		for (int cyc_i = 0; cyc_i < no_of_cycles; cyc_i++) {
			context.print("doPing()... cyc_i  -> " + cyc_i);
			Message msg = new Message();
			Bundle bndle = new Bundle();

			String sResponseCode = "", sResponseDesc = "";

			try {
				String pingCmd = "ping -c 5 " + sServerIP;
				String pingResult = "";
				Runtime r = Runtime.getRuntime();
				Process p = r.exec(pingCmd);

				BufferedReader in = new BufferedReader(new InputStreamReader(
						p.getInputStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					pingResult += inputLine + "\n";
				}
				in.close();
				L.log_ping(settings.getPingLogcatPath(), pingResult);
				context.print(pingResult);
				sResponseCode = ResponseCodes.SUCCESS;

			} catch (Exception e) {
				e.printStackTrace();
				sResponseCode = StringUtils.ERROR_CODE;
				sResponseDesc = Messages.PING_FAILED;
			}

			bndle.putString(StringUtils.CODE, sResponseCode);
			bndle.putString(StringUtils.DESC, sResponseDesc);
			msg.setData(bndle);

			if (!pref.isTestCanceled() && pref.isTestRunning())
				progressHandler.sendMessage(msg);
			else
				break;

		}
		if (!pref.isTestCanceled() && pref.isTestRunning())
			PingCompletedHandler.sendEmptyMessage(0);
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

	private Handler progressHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			// ###################
			String sResponseCode = msg.getData().getString(StringUtils.CODE);
			String sResponseDesc = msg.getData().getString(StringUtils.DESC);
			L.debug("progressHandler()::code -> " + sResponseCode);
			L.debug("progressHandler()::desc -> " + sResponseDesc);

			if (sResponseCode.equals(ResponseCodes.SUCCESS)) {
				L.debug("Ping success");
			} else {
				L.debug(sResponseDesc);
			}
		}
	};

	private Handler PingCompletedHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (!pref.isTestCanceled() && pref.isTestRunning()) {
				L.debug("PingCompletedHandler...");
				pref.setPingTestRunningState(false);

				new ResultUploader(context, pref.getValue(Preferences.KEY_TEST_NAME, "")
						+ StringUtils.TEST_CYCLE_APPEND_FILE
						+ testconfig.getTestCycle(),
						StringUtils.TEST_TYPE_CODE_PING,
						settings.getPingDeviceInfoPath(),
						settings.getPingLogcatPath()) {
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
