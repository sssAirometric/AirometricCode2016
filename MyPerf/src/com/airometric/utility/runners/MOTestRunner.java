package com.airometric.utility.runners;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.airometric.Airometric;
import com.airometric.AppActivity;
import com.airometric.TestTypeActivity;
import com.airometric.classes.MOTestConfig;
import com.airometric.classes.TestConfig;
import com.airometric.config.Constants;
import com.airometric.config.StringUtils;
import com.airometric.listeners.CallReceiver;
import com.airometric.storage.Preferences;
import com.airometric.storage.SettingsStore;
import com.airometric.utility.CallUtil;
import com.airometric.utility.DeviceUtil;
import com.airometric.utility.FileUtil;
import com.airometric.utility.L;
import com.airometric.utility.LogEvents;
import com.airometric.utility.NotificationUtil;
import com.airometric.utility.TimeUtil;

public class MOTestRunner extends Runner implements Serializable {
	private static int iteration = 0;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	AppActivity context;
	TestConfig testconfig;
	private Handler handler = new Handler();
	Preferences pref;
	SettingsStore settings;
	TelephonyManager tm;
	CallReceiver receiver = null;
	private boolean bCallStartedByApp = false, bCallDisconnectedByApp = false;
	private ServiceState currentServiceState;
	MOTestConfig MOTestConfigObj;
	Date dtCurrentCallStarted, dtCurrentCallEnded;

	long call_duration_secs, call_duration_millis, pause_time_secs,
			test_duration_secs, completed_duration_secs = 0, pass = 0;

	long current_call_duration_secs = 0;
	//ArrayList<String> abc = new ArrayList<String>();
	String abc;
	public MOTestRunner(AppActivity context, TestConfig testconfig) {
		this.context = context;
		this.testconfig = testconfig;
		MOTestConfigObj = testconfig.getMOTestConfig();
		pref = new Preferences(context);
		settings = new SettingsStore(context);
		Constants.CurrentRunner = this;
		bCallDisconnectedByApp = false;
		bCallStartedByApp = false;
	}

	public void startTest() {

		/*
		 * String sTestName = testconfig.getTestName() +
		 * StringUtils.TEST_CYCLE_APPEND_FILE + testconfig.getTestCycle();
		 */

		String sTestName = pref.getValue(Preferences.KEY_TEST_NAME, "")
				+ StringUtils.TEST_CYCLE_APPEND_FILE
				+ testconfig.getTestCycle();

		pref.setMOTestRunningState(true);
		DeviceUtil dv = new DeviceUtil(context);
		String sDeviceInfoXML = dv.getDeviceInfo(
				StringUtils.FILE_CODE_TEST_TYPE_MO, sTestName,
				pref.getUsername(),
				pref.getValue(Preferences.KEY_SELECTED_MARKET_PLACE_ID, ""));
		L.debug(sDeviceInfoXML);
		//Log.i("MOTESTRunner page", sDeviceInfoXML);
		
		String sCurrTime = TimeUtil.getCurrentTimeFilename();

		FileUtil.CURRENT_MO_TESTTIME = sCurrTime;
		String path = FileUtil.MO_LOG_DIR + "deviceinfo" + "_" + sTestName
				+ "_" + sCurrTime + ".xml";
		File fle = new File(path);
		if (fle.exists()) {
			fle.delete();
		}
		String dev_info_path = FileUtil.writeToXMLFile(path, sDeviceInfoXML);
		L.debug("MO Device info initial data written into " + dev_info_path);

		pref.putValue(Preferences.KEY_CURRENT_MO_DEV_INFO_PATH, dev_info_path);

		try {
			Airometric app = (Airometric) context.getApplication();
			app.startListeners(dev_info_path);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String logcat_path = FileUtil.MO_LOG_DIR
				+ StringUtils.FILE_CODE_TEST_TYPE_MO + "_" + dv.getIMEI() + "_"
				+ sTestName + "_" + sCurrTime + ".txt";
		File log_fle = new File(logcat_path);

		try {
			log_fle.createNewFile();
		} catch (IOException e) {
		}
		pref.putValue(Preferences.KEY_CURRENT_MO_LOGCAT_PATH,
				log_fle.getAbsolutePath());
		NotificationUtil.cancelNotification(context,
				StringUtils.TEST_TYPE_CODE_MO);
		if (receiver == null) {
			receiver = new CallReceiver() {
				@Override
				public void onCallStateChanged(int state, String incomingNumber) {
					L.debug("----------------MO iteration: " + iteration++);
					stateChanged(state, incomingNumber);
					super.onCallStateChanged(state, incomingNumber);
				}
			};

			tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);

			tm.listen(receiver, PhoneStateListener.LISTEN_NONE);
			tm.listen(MyServiceStateListener,
					PhoneStateListener.LISTEN_NONE);

			tm.listen(receiver, PhoneStateListener.LISTEN_CALL_STATE);
			tm.listen(MyServiceStateListener,
					PhoneStateListener.LISTEN_SERVICE_STATE);
		}

		bCallDisconnectedByApp = false;
		StartMOTestTask uploadTask = new StartMOTestTask();
		uploadTask.execute();
		Constants.CurrentTask = uploadTask;
		NotificationUtil.showRunningNotification(context,
				StringUtils.TEST_TYPE_CODE_MO);
		context.showActivity(TestTypeActivity.class);
	}

	protected void stateChanged(int state, String incomingNumber) {
		switch (state) {
		case TelephonyManager.CALL_STATE_RINGING:
		L.log_mo(settings.getMOLogcatPath(), LogEvents.EVENT_NEW_OUTGOING_CALL
				+ " - " + incomingNumber);
			break;
		
		case TelephonyManager.CALL_STATE_OFFHOOK:
			L.log_mo(settings.getMOLogcatPath(), LogEvents.EVENT_OFFHOOK
					+ " - " + incomingNumber);
			break;

		case TelephonyManager.CALL_STATE_IDLE:
			if (bCallStartedByApp) {
				String sServiceState = "";
				if (currentServiceState != null) {
					int service_state = currentServiceState.getState();
					sServiceState = DeviceUtil.getServiceString(service_state);
				}
				if (bCallDisconnectedByApp) {
					L.log_mo(settings.getMOLogcatPath(),
							LogEvents.EVENT_CALL_END);
				} else {
					//String call_drop_time = TimeUtil.getCurrentTime();
					
					/*L.log_mo(settings.getMOLogcatPath(),
							LogEvents.EVENT_CALL_DROP + " - " + sServiceState
									+ " - " + pref.getCurrentSignalStrength());*/
					String call_drop_time = L.log_mo1(settings.getMOLogcatPath(),
							LogEvents.EVENT_CALL_DROP + " - " + sServiceState
									+ " - " + pref.getCurrentSignalStrength());
//					System.out.println("call_drop_time "+ call_drop_time);
					
					if(pref.loadCallDropTimeData(context).isEmpty())
					{	abc = call_drop_time;
					}else{
						abc = pref.loadCallDropTimeData(context)+","+call_drop_time;
						}
					pref.saveCallDropTimeData(context,abc);	
				}

				if (!pref.isTestCanceled() && pref.isTestRunning()) {
					dtCurrentCallEnded = new Date();
					current_call_duration_secs = (dtCurrentCallEnded.getTime() - dtCurrentCallStarted
							.getTime()) / 1000;
					L.debug("current_call_duration_secs ==> "
							+ current_call_duration_secs);
					callDisconnected(incomingNumber);
					handler.removeCallbacks(DisconnectRunnable);
				}
			}
			break;
		default:
			L.debug("Unknown phone state=" + state);
		}
	}

	public void callStarted(String sPhoneNumber) {
		L.log_mo(settings.getMOLogcatPath(), LogEvents.EVENT_NEW_OUTGOING_CALL
				+ " - " + sPhoneNumber);
		bCallStartedByApp = true;

		/**
		 * Changed on 09 Jan 2015
		 */
		//bCallDisconnectedByApp = false;
	}

	PhoneStateListener MyServiceStateListener = new PhoneStateListener() {
		public void onServiceStateChanged(ServiceState serviceState) {
			currentServiceState = serviceState;
		};
	};

	void startTesting() {
		call_duration_secs = Long.parseLong(MOTestConfigObj.sCallDuration) * 60; // secs
		call_duration_millis = call_duration_secs * 1000; // converted to milli

		pause_time_secs = Long.parseLong(MOTestConfigObj.sPauseTime); // secs
		test_duration_secs = Long.parseLong(MOTestConfigObj.sTestDuration) * 60; // secs

		DeviceUtil.clearLogcat();

		// handler.postDelayed(RunnableCompleted, test_duration_secs * 1000);
		completed_duration_secs = 0;
		current_call_duration_secs = 0;
		L.debug("Dialing...");
		call();
	}

	public void call() {
		L.debug("call()::placing call.." + MOTestConfigObj.sPhoneNumber);
		bCallDisconnectedByApp = false;
		bCallStartedByApp = false;
		Intent callIntent = new Intent(Intent.ACTION_CALL);
		callIntent.setData(Uri.parse("tel:" + MOTestConfigObj.sPhoneNumber));
		context.startActivityForResult(callIntent, 0);
		dtCurrentCallStarted = new Date();
		handler.postDelayed(DisconnectRunnable, call_duration_millis);
	}

	Runnable DisconnectRunnable = new Runnable() {
		public void run() {
			if (!pref.isTestCanceled() && pref.isTestRunning())
				callDisconnect(MOTestConfigObj.sPhoneNumber);
			else {
				handler.removeCallbacks(RunnableCompleted);
			}
		}
	};

	private void callDisconnected(final String sPhoneNumber) {
		completed_duration_secs += current_call_duration_secs;
		pass++;
		L.debug("callDisconnected... completed_duration_secs ==> "
				+ completed_duration_secs);

		if (completed_duration_secs < (test_duration_secs - call_duration_secs - pause_time_secs)) {

			L.debug("Pausing...");
			handler.postDelayed(pauseRunnable, pause_time_secs * 1000);

		} else {
			// Test Ends
			L.debug("callDisconnected()::Test Ends[" + pass + "]."
					+ completed_duration_secs);
			handler.post(RunnableCompleted);
		}

	}

	Runnable pauseRunnable = new Runnable() {
		public void run() {
			L.debug("Redialing()::pause[" + pass + "] finished..");
			completed_duration_secs += pause_time_secs;
			call();
		}
	};

	private void callDisconnect(String phonenumber) {
		try {
			L.debug("callDisconnect()...");
			L.log_mo(settings.getMOLogcatPath(),
					LogEvents.EVENT_CALL_DISCONNECT);
			bCallDisconnectedByApp = true;

			CallUtil.callDisconnect(context);
		} catch (Exception e) {
			e.printStackTrace();
			L.debug(e.getMessage());
		}
	}

	private class StartMOTestTask extends AsyncTask<URL, Integer, String> {

		public StartMOTestTask() {
		}

		protected void onProgressUpdate(Integer... progress) {

		}

		protected void onPostExecute(Long result) {
			L.debug("StartMOTestTask::onPostExecute()...");
		}

		@Override
		protected String doInBackground(URL... urls) {
			startTesting();
			return "Started";
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onCancelled(String result) {
			super.onCancelled(result);
		}

	}

	public void cancelTest() {
		L.debug("cancelTest()...");
		handler.removeCallbacks(RunnableCompleted);
		handler.removeCallbacks(DisconnectRunnable);
		handler.removeCallbacks(pauseRunnable);
		CallUtil.callDisconnect(context);
		tm.listen(receiver, PhoneStateListener.LISTEN_NONE);
		receiver = null;
		tm.listen(MyServiceStateListener,
				PhoneStateListener.LISTEN_NONE);
		pref.setMOTestRunningState(false);
	}

	Runnable RunnableCompleted = new Runnable() {
		public void run() {
			L.debug("RunnableCompleted...");
			if (!pref.isTestCanceled() && pref.isTestRunning()) {
				CallUtil.callDisconnect(context);
				pref.setMOTestRunningState(false);
				tm.listen(receiver, PhoneStateListener.LISTEN_NONE);
				receiver = null;
				tm.listen(MyServiceStateListener,
						PhoneStateListener.LISTEN_NONE);
				new ResultUploader(context, pref.getValue(
						Preferences.KEY_TEST_NAME, "")
						+ StringUtils.TEST_CYCLE_APPEND_FILE
						+ testconfig.getTestCycle(),
						StringUtils.TEST_TYPE_CODE_MO,
						settings.getMODeviceInfoPath(),
						settings.getMOLogcatPath()) {
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
