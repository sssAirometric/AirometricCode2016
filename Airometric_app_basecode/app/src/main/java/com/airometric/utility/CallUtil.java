package com.airometric.utility;

import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;

public class CallUtil {

	public  static void callDisconnect(Activity activity) {
		try {
			L.debug("callDisconnect()::Ending the call..");
			TelephonyManager manager = (TelephonyManager) activity
					.getSystemService(Context.TELEPHONY_SERVICE);
			// Class c = Class.forName(manager.getClass().getName());
			Class c = TelephonyManager.class;
			Method m = c.getDeclaredMethod("getITelephony");
			m.setAccessible(true);
			ITelephony telephony = (ITelephony) m.invoke(manager);
			telephony.endCall();
			//telephony.answerRingingCall();
					

		} catch (Exception e) {
			e.printStackTrace();
			L.debug(e.getMessage());
		}
	}
}
