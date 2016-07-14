package com.airometric.listeners;

import java.lang.reflect.Method;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;

import com.airometric.config.Constants;
import com.airometric.storage.Preferences;
import com.airometric.storage.SettingsStore;
import com.airometric.utility.DeviceUtil;
import com.airometric.utility.L;
import com.android.internal.telephony.ITelephony;

public class OutgoingCallReceiver extends BroadcastReceiver {

	private static final String OUTGOING_CALL_ACTION = "android.intent.action.NEW_OUTGOING_CALL";
	private static final String PHONE_STATE_ACTION = TelephonyManager.ACTION_PHONE_STATE_CHANGED;
	private static final String INTENT_PHONE_NUMBER = "android.intent.extra.PHONE_NUMBER";
	private static final String INTENT_INCOMING_PHONE_NUMBER = "incoming_number";
	private static final String STATE_RINGING = TelephonyManager.EXTRA_STATE_RINGING;
	private static final String STATE_OFFHOOK = TelephonyManager.EXTRA_STATE_OFFHOOK;
	private static final String STATE_IDLE = TelephonyManager.EXTRA_STATE_IDLE;

	Context context;
	private boolean isMOTestRunning, isMTTestRunning;

	@Override
	public void onReceive(final Context context, final Intent intent) {
		this.context = context;

		Preferences pref = new Preferences(context);
		isMOTestRunning = pref.isMOTestRunning();
		isMTTestRunning = pref.isMTTestRunning();
		SettingsStore settings = new SettingsStore(context);

		if (isMOTestRunning) {
			String action = intent.getAction();
			if (action.equals(OUTGOING_CALL_ACTION)) {

				String phoneNumber = intent.getExtras().getString(
						INTENT_PHONE_NUMBER);
				if ((phoneNumber != null)) {
					DeviceUtil.updateCallStarted(phoneNumber);
					this.abortBroadcast();
				}
			}
		}
		if (isMTTestRunning) {
			String action = intent.getAction();
			if (action.equals(OUTGOING_CALL_ACTION)) {

				String phoneNumber = intent.getExtras().getString(
						INTENT_PHONE_NUMBER);
				if ((phoneNumber != null)) {
					L.log_mt(settings.getMTLogcatPath(), "NEW_OUTGOING_CALL - "
							+ phoneNumber);
					this.abortBroadcast();
				}
			}

			if (action.equals(PHONE_STATE_ACTION)) {

				L.log(">>>>>>>>>> MT TEST IS RUNNING  && PHONE_STATE_ACTION >>>>>>>");

				String state = intent.getExtras().getString(
						TelephonyManager.EXTRA_STATE);
				if (state.equals(STATE_RINGING)) {
					// get phone number from bundle
					String phoneNumber = intent.getExtras().getString(
							INTENT_INCOMING_PHONE_NUMBER);
					if ((phoneNumber != null)) {
						L.log_mt(settings.getMTLogcatPath(),
								"NEW_INCOMING_CALL - " + phoneNumber);
					}
					L.log_mt(settings.getMTLogcatPath(), "RINGING");

					/*
					 * Intent myIntent = new Intent(Intent.ACTION_ANSWER);
					 * myIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP // |
					 * Intent.FLAG_DEBUG_LOG_RESOLUTION // |
					 * Intent.FLAG_FROM_BACKGROUND // |
					 * Intent.FLAG_ACTIVITY_NEW_TASK).addCategory(
					 * Intent.CATEGORY_DEFAULT);
					 * context.startActivity(myIntent);
					 */

					int deviceOSVersion = android.os.Build.VERSION.SDK_INT;
					L.debug("DEVICE OS VERSION: " + deviceOSVersion);

					if (deviceOSVersion == Constants.OS_VERSION_LOLLIPOP) {
						answeringCall();
					} else {
						answerPhoneHeadsethook(context);
					}
					// callAnswer();
				}
			}
		}
	}

	private void callAnswer() {
		try {
			L.debug("inside callAnswer().......");
			TelephonyManager manager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			Class c = Class.forName(manager.getClass().getName());
			Method m = c.getDeclaredMethod("getITelephony");
			m.setAccessible(true);
			ITelephony telephony = (ITelephony) m.invoke(manager);

			telephony.silenceRinger();
			telephony.answerRingingCall();

		} catch (RemoteException rEx) {
			L.debug("Remote Exception Raised!!!");
			rEx.printStackTrace();
			L.debug(rEx.getMessage());
		}

		catch (Exception e) {
			L.debug("Exception Raised!!!");
			e.printStackTrace();
			L.debug(e.getMessage());
		}
	}

	void answerfix() {
		try {
			Intent buttonDown = new Intent(Intent.ACTION_MEDIA_BUTTON);
			buttonDown.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(
					KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK));
			try {
				context.sendOrderedBroadcast(buttonDown,
						"android.permission.CALL_PRIVILEGED");
			} catch (Exception e) {
				e.printStackTrace();
			}

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
			headSetUnPluggedintent
					.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
			headSetUnPluggedintent.putExtra("state", 1); // 0 = unplugged 1 =
															// Headset with
															// microphone 2 =
															// Headset without
															// microphone
			headSetUnPluggedintent.putExtra("name", "Headset");
			// TODO: Should we require a permission?
			context.sendOrderedBroadcast(headSetUnPluggedintent1, null);

		} catch (Exception e) {

		}
	}

	public void answerPhoneHeadsethook(Context context) {
		answerfix();

		// Simulate a press of the headset button to pick up the call
		// SettingsClass.logMe(tag, "Simulating headset button");
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
	}

	private void answeringCall() {

		try {
			L.debug("	input keyevent in OutGoing Receiver!!");
			Runtime.getRuntime().exec(
					"input keyevent " + KeyEvent.KEYCODE_HEADSETHOOK);
			
			/*Runtime.getRuntime().exec(
					"input keyevent " + KeyEvent.KEYCODE_CALL);*/
		} catch (Exception e) {
			L.debug(e.getMessage());
			e.printStackTrace();
		}
	}
	/*
	 * new Thread(new Runnable() {
	 * 
	 * @Override public void run() { try {
	 * L.debug("########## answeringCall ##########");
	 * Runtime.getRuntime().exec( "input keyevent " +
	 * KeyEvent.KEYCODE_HEADSETHOOK ); } catch (Throwable t) { // do something
	 * proper here. L.debug("Err while answering call: " + t.getMessage()); } }
	 * }).start(); }
	 */

}
