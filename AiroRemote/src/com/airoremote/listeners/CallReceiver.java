package com.airoremote.listeners;

import android.telephony.PhoneStateListener;
import android.util.Log;

import com.airoremote.config.StringUtils;

public class CallReceiver extends PhoneStateListener {
	public void onCallStateChanged(int state, String incomingNumber) {
		
	}

	void log_d(String msg) {
		Log.d(StringUtils.MO_LOG_TAG, msg);
	}
}
