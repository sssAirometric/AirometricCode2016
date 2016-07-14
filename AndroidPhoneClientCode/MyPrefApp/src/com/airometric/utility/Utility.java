package com.airometric.utility;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.text.TextUtils;
import android.widget.EditText;

import com.airometric.AppActivity;
import com.airometric.R;
import com.airometric.dialogs.AppAlert;

public class Utility {

	public BluetoothSocket btSocket = null;
	public static boolean flagUnregisterBTCon = false;
	public static int numberOfAttempts = 6;
	public static int numberOfDays = 6;
	public static boolean flagBixolon = false;

	public static String toTitleCase(String string) {
		String result = "";
		for (int i = 0; i < string.length(); i++) {
			String next = string.substring(i, i + 1);
			if (i == 0) {
				result += next.toUpperCase();
			} else {
				result += next.toLowerCase();
			}
		}
		return result;
	}

	public static boolean in_array(String haystack[], String needle) {
		for (int i = 0; i < haystack.length; i++) {
			if (haystack[i].toString().equals(needle)) {
				return true;
			}
		}
		return false;
	}

	public void displayAlertDialog(final EditText control, Context context,
			String sMessage) {
		new AppAlert(context, sMessage) {
			@Override
			public void okClickListener() {
				control.setFocusableInTouchMode(true);
				control.requestFocusFromTouch();
			}
		};

	}

	/*
	 * public void startActivity(Activity sourceScreen, Class<?> cls) { Intent i
	 * = new Intent(sourceScreen, cls); startIntent(sourceScreen, i); }
	 * 
	 * public void startActivity(Activity sourceScreen, Class<?> cls, int
	 * direction) { Intent i = new Intent(sourceScreen, cls);
	 * startIntent(sourceScreen, i, direction); }
	 * 
	 * public void startIntent(Activity sourceScreen, Intent intent) {
	 * startIntent(sourceScreen, intent, AppActivity.DIRECTION_FRONT); }
	 */
	public void startIntent(Activity sourceScreen, Intent intent, int direction) {
		sourceScreen.startActivity(intent);
		if (direction == AppActivity.DIRECTION_BACK)
			sourceScreen.overridePendingTransition(R.anim.slide_right,
					R.anim.slide_in);
		else
			sourceScreen.overridePendingTransition(R.anim.slide_left,
					R.anim.slide_out);
		sourceScreen.finish();
	}

	public static void print(String msg) {
		new AppActivity().print(msg);
	}

	/**
	 * Method to check for the maximum volume. If device's media volume not set
	 * to maximum volume then programatically set to maximum. For milestone set
	 * to maximum minus one.
	 */
	public static void checkVolume(Activity activity) {
		try {
			// Get the AudioManager
			AudioManager audioManager = (AudioManager) activity
					.getSystemService(Context.AUDIO_SERVICE);
			int maxVol = audioManager
					.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			// Set the volume of played media to maximum.
			if (android.os.Build.MODEL.equalsIgnoreCase("milestone"))
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
						maxVol - 1, 0);
			else
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVol,
						0);

		} catch (Exception e) {
		}
	}

	public static String encode(String txt) {
		return TextUtils.htmlEncode(txt);
	}
}
