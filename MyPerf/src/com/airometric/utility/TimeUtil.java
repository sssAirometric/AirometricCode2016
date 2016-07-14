package com.airometric.utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtil {
	public static String getCurrentTimeInString() {
		String sCurrentTime = "";

		Calendar c = Calendar.getInstance();
		int cDate = c.get(Calendar.DAY_OF_MONTH);
		int cMonth = c.get(Calendar.MONTH);
		int cYear = c.get(Calendar.YEAR);
		int mHours = c.get(Calendar.HOUR);
		int mMinutes = c.get(Calendar.MINUTE);

		String sDate = cDate + "/";
		String sMonth = (cMonth + 1) + "/";
		String sYear = cYear + "";
		String sHours = mHours + ":";
		String sMinutes = mMinutes + ":";

		if (cDate < 10) {
			sDate = "0" + cDate + "/";
		}
		if (cMonth < 10) {
			sMonth = "0" + (cMonth + 1) + "/";
		}
		if (sHours.length() == 1) {
			sHours = "0" + sHours + ":";
		}
		if (mMinutes < 10) {
			sMinutes = "0" + mMinutes + ":";
		}

		String am_pm;
		if (c.get(Calendar.AM_PM) == 0) {
			am_pm = "AM";
		} else {
			am_pm = "PM";
		}

		String time = " " + sHours + ":" + sMinutes + " " + am_pm;

		sCurrentTime = sDate + sMonth + sYear + time;
		return sCurrentTime;
	}

	public static String getCurrentTime() {
		final Date currentTime = new Date();

		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String time = sdf.format(currentTime);

		return time;
	}

	public static String getCurrentTimeFilename() {
		final Date currentTime = new Date();

		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		String time = sdf.format(currentTime);

		return "" + currentTime.getTime();
	}
	public static String getCurrentDateAndTime() {
		final Date currentTime = new Date();

		final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
		String time = sdf.format(currentTime);

		return time;
	}
	public static String getCurrentYear() {
		final Date currentTime = new Date();

		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		String time = sdf.format(currentTime);

		return time;
	}
}
