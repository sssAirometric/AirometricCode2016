package com.airometric.utility.runners;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;

import com.airometric.Airometric;
import com.airometric.AppActivity;
import com.airometric.TestTypeActivity;
import com.airometric.classes.MTTestConfig;
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
import com.airometric.utility.NotificationUtil;
import com.airometric.utility.TimeUtil;
import com.airometric.utility.Validator;
import com.android.internal.telephony.ITelephony;

public class MTTestRunner extends Runner implements Serializable {
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
	MTTestConfig MTTestConfigObj;
	private boolean bCallReceivedByApp = false, bCallDisconnectedByApp = false;
	private ServiceState currentServiceState;
	TelephonyManager tm;
	CallReceiver receiver;
	long call_duration_secs, call_duration_millis, test_duration_secs,
			completed_duration_secs = 0, test_duration_mills, pass = 0,
			pause_time_secs = 20;
	Date dtCurrentCallStarted, dtCurrentCallEnded;
	long current_call_duration_secs = 0;

	public MTTestRunner(AppActivity context, TestConfig testconfig) {
		this.context = context;
		this.testconfig = testconfig;
		pref = new Preferences(context);
		settings = new SettingsStore(context);
		Constants.CurrentRunner = this;
	}

	public void startTest() {

		MTTestConfigObj = testconfig.getMTTestConfig();

		/*
		 * String sTestName = testconfig.getTestName() +
		 * StringUtils.TEST_CYCLE_APPEND_FILE + testconfig.getTestCycle();
		 */

		String sTestName = pref.getValue(Preferences.KEY_TEST_NAME, "")
				+ StringUtils.TEST_CYCLE_APPEND_FILE
				+ testconfig.getTestCycle();

		pref.setMTTestRunningState(true);

		DeviceUtil dv = new DeviceUtil(context);
		String sDeviceInfoXML = dv.getDeviceInfo(
				StringUtils.FILE_CODE_TEST_TYPE_MT, sTestName,
				pref.getUsername(),
				pref.getValue(Preferences.KEY_SELECTED_MARKET_PLACE_ID, ""));
		L.debug(sDeviceInfoXML);

		String sCurrTime = TimeUtil.getCurrentTimeFilename();

		FileUtil.CURRENT_MT_TESTTIME = sCurrTime;
		String path = FileUtil.MT_LOG_DIR + "deviceinfo" + "_" + sTestName
				+ "_" + sCurrTime + ".xml";
		File fle = new File(path);
		if (fle.exists()) {
			fle.delete();
		}
		String dev_info_path = FileUtil.writeToXMLFile(path, sDeviceInfoXML);
		L.debug("Device info initial data written into " + dev_info_path);

		pref.putValue(Preferences.KEY_CURRENT_MT_DEV_INFO_PATH, dev_info_path);

		String logcat_path = FileUtil.MT_LOG_DIR
				+ StringUtils.FILE_CODE_TEST_TYPE_MT + "_" + dv.getIMEI() + "_"
				+ sTestName + "_" + sCurrTime + ".txt";

		File log_fle = new File(logcat_path);

		try {
			log_fle.createNewFile();
		} catch (IOException e) {
		}
		pref.putValue(Preferences.KEY_CURRENT_MT_LOGCAT_PATH,
				log_fle.getAbsolutePath());
		try {
			Airometric app = (Airometric) context.getApplication();
			app.startListeners(dev_info_path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		NotificationUtil.cancelNotification(context,
				StringUtils.TEST_TYPE_CODE_MT);

		bCallDisconnectedByApp = false;
		receiver = new CallReceiver() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				L.debug("---------------------MT: " + iteration++);
				stateChanged(state, incomingNumber);
				super.onCallStateChanged(state, incomingNumber);
			}
		};
		tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		tm.listen(receiver, PhoneStateListener.LISTEN_CALL_STATE);
		tm.listen(MyServiceStateListener,
				PhoneStateListener.LISTEN_SERVICE_STATE);
		bCallReceivedByApp = false;
		StartMTTestTask uploadTask = new StartMTTestTask();
		uploadTask.execute();
		Constants.CurrentTask = uploadTask;
		NotificationUtil.showRunningNotification(context,
				StringUtils.TEST_TYPE_CODE_MT);
		L.event("MTTestRunner()...");
		context.showActivity(TestTypeActivity.class);
	}

	void startTesting() {
		DeviceUtil.clearLogcat();
		bCallReceivedByApp = false;

		call_duration_secs = Long.parseLong(MTTestConfigObj.sCallDuration) * 60; // secs
		call_duration_millis = call_duration_secs * 1000; // converted to milli
		test_duration_secs = Long.parseLong(MTTestConfigObj.sTestDuration) * 60; // secs
		test_duration_mills = test_duration_secs * 1000; // converted to
															// milli
		L.log("Call duration: " + call_duration_millis);
		L.log("Test duration: " + test_duration_mills);

		handler.postDelayed(RunnableCompleted, test_duration_mills);
		// handler.postDelayed(DisconnectRunnable, call_duration_millis);
	}

	void startRunnables() {
		L.event("startRunnables()...");
		handler.removeCallbacks(RunnableCompleted);
		handler.postDelayed(RunnableCompleted,
				((test_duration_secs - completed_duration_secs) * 1000));
		// handler.postDelayed(RunnableCompleted, ((test_duration_secs -
		// completed_duration_secs) * 1000));

		handler.postDelayed(DisconnectRunnable, call_duration_millis);
		dtCurrentCallStarted = new Date();
	}

	Runnable DisconnectRunnable = new Runnable() {
		public void run() {
			L.log("Call disconnect");
			if (!pref.isTestCanceled() && pref.isTestRunning()) {
				callDisconnect();
				handler.removeCallbacks(DisconnectRunnable);
			} else {
				//handler.removeCallbacks(RunnableCompleted);
			}
		}
	};

	private void callDisconnect() {
		L.event("callDisconnect()...");
		try {
			CallUtil.callDisconnect(context);
			bCallDisconnectedByApp = true;
			callDisconnected();
		} catch (Exception e) {
			e.printStackTrace();
			L.debug(e.getMessage());
		}
	}

	private void callDisconnected() {
		completed_duration_secs += current_call_duration_secs;
		pass++;

		if (completed_duration_secs < (test_duration_secs
				- current_call_duration_secs - pause_time_secs)) {
			L.debug("Pausing...");
			handler.postDelayed(new Runnable() {
				public void run() {
					L.debug("Redialing()::pause[" + pass + "] finished..");
					completed_duration_secs += pause_time_secs;
					// startRunnables();

				}
			}, pause_time_secs * 1000);

		} else {
			// Test Ends
			L.debug("callDisconnected()::Test Ends[" + pass + "]."
					+ completed_duration_secs);
		}

	}

	private Runnable answerRunnable = new Runnable() {

		@Override
		public void run() {
			answerPhoneHeadsethook();
		}
	};

	protected void stateChanged(int state, String incomingNumber) {
		switch (state) {
		case TelephonyManager.CALL_STATE_RINGING:
			if (!Validator.isEmpty(incomingNumber))
				L.log_mt(settings.getMTLogcatPath(), "NEW_INCOMING_CALL - "
						+ incomingNumber);
			L.log_mt(settings.getMTLogcatPath(), "RINGING");

			L.debug("*****************************");

			/*
			 * Intent myIntent = new Intent(Intent.ACTION_ANSWER);
			 * myIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP // |
			 * Intent.FLAG_DEBUG_LOG_RESOLUTION // | Intent.FLAG_FROM_BACKGROUND
			 * // | Intent.FLAG_ACTIVITY_NEW_TASK).addCategory(
			 * Intent.CATEGORY_DEFAULT); context.startActivity(myIntent);
			 */

			// L.debug("CALL ANSWERED!!!!!!!!!!!!!!!!!!!!");
			// handler.postDelayed(answerRunnable, 3 * 1000);
			// answerPhoneHeadsethook();
			
			int id = android.os.Process.myPid();
			int deviceOSVersion = android.os.Build.VERSION.SDK_INT;		
			L.debug("DEVICE OS VERSION: " + deviceOSVersion);
			L.debug("Process id: "+ id); 
			
			if(deviceOSVersion == Constants.OS_VERSION_LOLLIPOP) {
				answeringCall();	
			} else {
				answerPhoneHeadsethook();
			}		
			break;
			
		case TelephonyManager.CALL_STATE_OFFHOOK:
			L.log_mt(settings.getMTLogcatPath(), "OFFHOOK");
			startRunnables();
			bCallReceivedByApp = true;
			break;

		case TelephonyManager.CALL_STATE_IDLE:
			L.debug("CALL_STATE_IDLE...");
			if (bCallReceivedByApp) {
				dtCurrentCallEnded = new Date();
				current_call_duration_secs = (dtCurrentCallEnded.getTime() - dtCurrentCallStarted
						.getTime()) / 1000;
				L.debug("current_call_duration_secs ==> "
						+ current_call_duration_secs);
				String sServiceState = "";
				if (currentServiceState != null) {
					int service_state = currentServiceState.getState();
					sServiceState = DeviceUtil.getServiceString(service_state);
				}

				long actual_call_dur_incl_buff = call_duration_secs
						- Constants.MT_END_BUFFER_TIME_SECONDS; // 45
				if (current_call_duration_secs < actual_call_dur_incl_buff) {

					if (bCallDisconnectedByApp) {
						L.log_mt(settings.getMTLogcatPath(), "CALL_END");
					} else {
						L.log_mt(
								settings.getMTLogcatPath(),
								"CALL_DROP - " + sServiceState + " - "
										+ pref.getCurrentSignalStrength());
					}
				} else {
					L.log_mt(settings.getMTLogcatPath(), "CALL_END");
				}
				callDisconnected();

			}
			break;
		default:
			L.debug("Unknown phone state=" + state);
		}
	}

	PhoneStateListener MyServiceStateListener = new PhoneStateListener() {
		public void onServiceStateChanged(ServiceState serviceState) {
			currentServiceState = serviceState;
		};
	};

	public void cancelTest() {
		L.debug("cancelTest()...");
		handler.removeCallbacks(RunnableCompleted);
		tm.listen(receiver, PhoneStateListener.LISTEN_NONE);
		tm.listen(MyServiceStateListener, PhoneStateListener.LISTEN_NONE);
		pref.setMTTestRunningState(false);
	}

	private class StartMTTestTask extends AsyncTask<URL, Integer, String> {

		public StartMTTestTask() {
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

	void uploadResult() {
		L.debug("uploadResult...");
		tm.listen(receiver, PhoneStateListener.LISTEN_NONE);
		tm.listen(MyServiceStateListener, PhoneStateListener.LISTEN_NONE);
		new ResultUploader(context,
				pref.getValue(Preferences.KEY_TEST_NAME, "")
						+ StringUtils.TEST_CYCLE_APPEND_FILE
						+ testconfig.getTestCycle(),
				StringUtils.TEST_TYPE_CODE_MT, settings.getMTDeviceInfoPath(),
				settings.getMTLogcatPath()) {
			public void resultUploaded(String code, String desc) {
				doResultUploaded(code, desc);
			};
		};
	}

	Runnable RunnableCompleted = new Runnable() {
		public void run() {
			if (!pref.isTestCanceled() && pref.isTestRunning()) {
				L.debug("RunnableCompleted...");
				CallUtil.callDisconnect(context);
				bCallDisconnectedByApp = true;
				pref.setMTTestRunningState(false);
				try {
					Thread.sleep(1 * 1000); // Wait for few seconds before
											// upload
				} catch (Exception e) {
				}

				uploadResult();
				handler.removeCallbacks(RunnableCompleted);
			}
		}
	};

	void answerfix() {
		try {
			
			L.debug("******** answerfix() ******** ");
			
			Intent buttonDown = new Intent(Intent.ACTION_MEDIA_BUTTON);
			buttonDown.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(
					KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK));
			context.sendOrderedBroadcast(buttonDown,
					"android.permission.CALL_PRIVILEGED");

			// froyo and beyond trigger on buttonUp instead of buttonDown
			Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
			buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(
					KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
			context.sendOrderedBroadcast(buttonUp,
					"android.permission.CALL_PRIVILEGED");

			Intent headSetUnPluggedintent = new Intent(
					Intent.ACTION_HEADSET_PLUG);
			headSetUnPluggedintent
					.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
			headSetUnPluggedintent.putExtra("state", 0); // 0 = unplugged 1 =
															// Headset with
															// microphone 2 =
															// Headset without
															// microphone
			headSetUnPluggedintent.putExtra("name", "Headset");
			// TODO: Should we require a permission?
			context.sendOrderedBroadcast(headSetUnPluggedintent, null);

			// Added on 23rd Feb 2014
			Intent headSetUnPluggedintent1 = new Intent(
					Intent.ACTION_HEADSET_PLUG);
			headSetUnPluggedintent1
					.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
			headSetUnPluggedintent1.putExtra("state", 1); // 0 = unplugged 1 =
															// Headset with
															// microphone 2 =
															// Headset without
															// microphone
			headSetUnPluggedintent1.putExtra("name", "Headset");
			context.sendOrderedBroadcast(headSetUnPluggedintent1, null);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void answerPhoneHeadsethook() {
		handler.removeCallbacks(answerRunnable);
		int id = android.os.Process.myPid();
		L.debug("Process id: "+ id); 
		answerfix();

		// Simulate a press of the headset button to pick up the call
		// SettingsClass.logMe(tag, "Simulating headset button");
		Intent buttonDown = new Intent(Intent.ACTION_MEDIA_BUTTON);
		buttonDown.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(
				KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK));
		try {
			context.sendOrderedBroadcast(buttonDown,
					"android.permission.CALL_PRIVILEGED");
		} catch (Exception e) {
			L.debug(">>>> Exception Raised 1111111111111!!!");
			e.printStackTrace();
		}

		// froyo and beyond trigger on buttonUp instead of buttonDown
		Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
		buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(
				KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
		try {
			context.sendOrderedBroadcast(buttonUp,
					"android.permission.CALL_PRIVILEGED");
			// context.sendOrderedBroadcast(buttonUp,
			// "android.intent.action.HEADSET_PLUG");
		} catch (Exception e) {
			L.debug(">>>> Exception Raised 222222222222!!!");
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void callAnswer() {
		try {
			L.debug(">>>> callAnswer() in MTTest Runner >>>>>>>>>>");
			L.debug("callAnswer()::Answer the call..");
			TelephonyManager manager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			Class c = Class.forName(manager.getClass().getName());
			Method m = c.getDeclaredMethod("getITelephony");
			m.setAccessible(true);
			ITelephony telephony = (ITelephony) m.invoke(manager);
			//telephony.silenceRinger(); // not supported
			// telephony.endCall();
			telephony.answerRingingCall();

			/*
			 * telephony.showCallScreen();
			 * telephony.showCallScreenWithDialpad(true);
			 */

		} catch (RemoteException rEx) {
			L.debug("Remote Exception Raised!!!!");
			rEx.printStackTrace();
			L.debug(rEx.getMessage());
		} catch (Exception e) {

			e.printStackTrace();
			L.debug(e.getMessage());
		}
	}
	
	private void answeringCall() {
		
		try {
			L.debug("	input keyevent in  MT Test Runnner!!");
			int id= android.os.Process.myPid();
			L.debug("Process id: "+ id); 
			
			Runtime.getRuntime().exec(
					"input keyevent " + KeyEvent.KEYCODE_HEADSETHOOK);
			
			/*Runtime.getRuntime().exec(
					"input keyevent " + KeyEvent.KEYCODE_CALL);
			*/
			 L.debug("Pressed key with code: "+ KeyEvent.KEYCODE_HEADSETHOOK); 
			
		} catch (Exception e) {
			L.debug(e.getMessage());
			e.printStackTrace();
		}
		
		
	}
/*	new Thread(new Runnable() {
        @Override
        public void run() {
            try {
            	L.debug("########## Answering Call ##########");
                Runtime.getRuntime().exec( "input keyevent " + KeyEvent.KEYCODE_HEADSETHOOK );
            }
            catch (Throwable t) {
                // do something proper here.
            	L.debug("Err while answering call: "  + t.getMessage());
            }
        }
    }).start();
	}
*/
	public void doResultUploaded(String code, String desc) {

	};
}
