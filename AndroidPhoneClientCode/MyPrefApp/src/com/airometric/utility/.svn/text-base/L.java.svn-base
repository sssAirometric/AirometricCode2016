package com.airometric.utility;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Set;

import android.os.Bundle;
import android.util.Log;

import com.airometric.config.Constants;
import com.airometric.config.StringUtils;

/**
 * Log Utility
 * <p/>
 * 
 */
public class L {
	public static String TAG = Constants.APP_NAME;
	private static boolean CAN_LOG = Constants.DEBUG;

	private L() {
	}

	/**
	 * Log a message object with the DEBUG level.
	 * 
	 * @param clazz
	 *            The name of clazz will be used as the name of the logger to
	 *            retrieve.
	 * @param message
	 *            the message object to log.
	 */
	public static void debug(Class<?> clazz, String message) {
		if (CAN_LOG)
			Log.d(clazz.getName(), message);
	}

	/**
	 * Log a message object with the INFO Level.
	 * 
	 * @param clazz
	 *            The name of clazz will be used as the name of the logger to
	 *            retrieve.
	 * @param message
	 *            the message object to log.
	 */
	public static void info(Class<?> clazz, String message) {
		if (CAN_LOG)
			Log.i(clazz.getName(), message);
	}

	/**
	 * Log a message object with the WARN Level.
	 * 
	 * @param clazz
	 *            The name of clazz will be used as the name of the logger to
	 *            retrieve.
	 * @param message
	 *            the message object to log.
	 */
	public static void warn(Class<?> clazz, String message) {
		if (CAN_LOG)
			Log.w(clazz.getName(), message);
	}

	/**
	 * Log a message with the WARN level including the stack trace of the
	 * Throwable t passed as parameter.
	 * 
	 * @param clazz
	 *            The name of clazz will be used as the name of the logger to
	 *            retrieve.
	 * @param message
	 *            the message object to log.
	 * @param t
	 *            the exception to log, including its stack trace.
	 */
	public static void warn(Class<?> clazz, String message, Throwable t) {
		if (CAN_LOG)
			Log.w(clazz.getName(), message, t);
	}

	/**
	 * Log a message object with the ERROR Level.
	 * 
	 * @param clazz
	 *            The name of clazz will be used as the name of the logger to
	 *            retrieve.
	 * @param message
	 *            the message object to log.
	 */
	public static void error(Class<?> clazz, String message) {
		if (CAN_LOG)
			Log.e(clazz.getName(), message);
	}

	/**
	 * Log a message object with the ERROR level including the stack trace of
	 * the Throwable t passed as parameter.
	 * 
	 * @param clazz
	 *            The name of clazz will be used as the name of the logger to
	 *            retrieve.
	 * @param message
	 *            the message object to log.
	 * @param t
	 *            the exception to log, including its stack trace.
	 */
	public static void error(Class<?> clazz, String message, Throwable t) {
		if (CAN_LOG)
			Log.e(clazz.getName(), message, t);
	}

	/**
	 * Log a message object with the ERROR level including the stack trace of
	 * the Throwable t passed as parameter.
	 * 
	 * @param clazz
	 *            The name of clazz will be used as the name of the logger to
	 *            retrieve.
	 * @param message
	 *            the message object to log.
	 * @param t
	 *            the exception to log, including its stack trace.
	 */
	public static void error(Class<?> clazz, String message, Exception ex) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		ex.printStackTrace(printWriter);
		String stacktrace = result.toString();
		printWriter.close();
		error(clazz, stacktrace);
	}

	/**
	 * Log a message object with the INFO Level.
	 * 
	 * @param clazz
	 *            The name of clazz will be used as the name of the logger to
	 *            retrieve.
	 * @param message
	 *            the message object to log.
	 */
	public static void log(String message) {
		if (CAN_LOG)
			Log.i(TAG, "" + message);
	}

	/**
	 * Log a message object with the DEBUG level.
	 * 
	 * @param clazz
	 *            The name of clazz will be used as the name of the logger to
	 *            retrieve.
	 * @param message
	 *            the message object to log.
	 */
	public static void debug(String message) {
		if (CAN_LOG)
			Log.d(TAG, "" + message);
	}

	/**
	 * Log a message object with the INFO Level.
	 * 
	 * @param clazz
	 *            The name of clazz will be used as the name of the logger to
	 *            retrieve.
	 * @param message
	 *            the message object to log.
	 */
	public static void info(String message) {
		if (CAN_LOG)
			Log.i(TAG, message);
	}

	/**
	 * Log a message object with the WARN Level.
	 * 
	 * @param clazz
	 *            The name of clazz will be used as the name of the logger to
	 *            retrieve.
	 * @param message
	 *            the message object to log.
	 */
	public static void warn(String message) {
		if (CAN_LOG)
			Log.w(TAG, message);
	}

	/**
	 * Log a message with the WARN level including the stack trace of the
	 * Throwable t passed as parameter.
	 * 
	 * @param clazz
	 *            The name of clazz will be used as the name of the logger to
	 *            retrieve.
	 * @param message
	 *            the message object to log.
	 * @param t
	 *            the exception to log, including its stack trace.
	 */
	public static void warn(String message, Throwable t) {
		if (CAN_LOG)
			Log.w(TAG, message, t);
	}

	/**
	 * To print the messages for debugging
	 */
	public static void event(String msg) {
		if (Constants.DEBUG)
			Log.v(TAG, msg);
	}

	/**
	 * Log a message object with the ERROR Level.
	 * 
	 * @param clazz
	 *            The name of clazz will be used as the name of the logger to
	 *            retrieve.
	 * @param message
	 *            the message object to log.
	 */
	public static void error(String message) {
		if (CAN_LOG)
			Log.e(TAG, message);
	}

	/**
	 * Log a message object with the ERROR level including the stack trace of
	 * the Throwable t passed as parameter.
	 * 
	 * @param clazz
	 *            The name of clazz will be used as the name of the logger to
	 *            retrieve.
	 * @param message
	 *            the message object to log.
	 * @param t
	 *            the exception to log, including its stack trace.
	 */
	public static void error(String message, Throwable t) {
		if (CAN_LOG)
			Log.e(TAG, message, t);
	}

	/**
	 * Log a message object with the ERROR level including the stack trace of
	 * the Throwable t passed as parameter.
	 * 
	 * @param clazz
	 *            The name of clazz will be used as the name of the logger to
	 *            retrieve.
	 * @param message
	 *            the message object to log.
	 * @param t
	 *            the exception to log, including its stack trace.
	 */
	public static void error(String message, Exception ex) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		ex.printStackTrace(printWriter);
		String stacktrace = result.toString();
		printWriter.close();
		error(stacktrace);
	}

	public static void error(Exception ex) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		ex.printStackTrace(printWriter);
		String stacktrace = result.toString();
		printWriter.close();
		error(stacktrace);
	}

	public static void log_mo(String msg) {
		Log.d(StringUtils.MO_LOG_TAG, msg);
	}

	public static void log_mo(String path, String msg) {
		Log.d(StringUtils.MO_LOG_TAG, msg);
		FileUtil.writeToLogcatFile(path, msg, StringUtils.MO_LOG_TAG);
	}

	public static void log_mt(String msg) {
		Log.d(StringUtils.MT_LOG_TAG, msg);
	}

	public static void log_mt(String path, String msg) {
		Log.d(StringUtils.MT_LOG_TAG, msg);
		FileUtil.writeToLogcatFile(path, msg, StringUtils.MT_LOG_TAG);
	}

	public static void log_ftp(String msg) {
		Log.d(StringUtils.FTP_LOG_TAG, msg);
	}

	public static void log_ftp_rx(String path, long RXBytes, long RXSegments) {

		String msg = "Current RX bytes - " + RXBytes;
		Log.d(StringUtils.FTP_LOG_TAG, msg);
		FileUtil.writeToLogcatFile(path, msg, StringUtils.FTP_LOG_TAG);
		msg = "Current RXSegments - " + RXSegments;
		Log.d(StringUtils.FTP_LOG_TAG, msg);
		FileUtil.writeToLogcatFile(path, msg, StringUtils.FTP_LOG_TAG);
	}

	public static void log_ftp_rx(String ftpLogcatPath, long RXBytes,
			long RXSegments, long AllRXPackets, long TotalRXPackets,
			long MobileRXPackets) {

		String msg = "Current RX bytes - " + RXBytes;
		Log.d(StringUtils.FTP_LOG_TAG, msg);
		FileUtil.writeToLogcatFile(ftpLogcatPath, msg, StringUtils.FTP_LOG_TAG);

		msg = "Current RXSegments - " + RXSegments;
		Log.d(StringUtils.FTP_LOG_TAG, msg);
		FileUtil.writeToLogcatFile(ftpLogcatPath, msg, StringUtils.FTP_LOG_TAG);

		msg = "All RX Packets - " + AllRXPackets;
		Log.d(StringUtils.FTP_LOG_TAG, msg);
		FileUtil.writeToLogcatFile(ftpLogcatPath, msg, StringUtils.FTP_LOG_TAG);

		msg = "Total RX Packets - " + TotalRXPackets;
		Log.d(StringUtils.FTP_LOG_TAG, msg);
		FileUtil.writeToLogcatFile(ftpLogcatPath, msg, StringUtils.FTP_LOG_TAG);

		msg = "Mobile RX Packets - " + MobileRXPackets;
		Log.d(StringUtils.FTP_LOG_TAG, msg);
		FileUtil.writeToLogcatFile(ftpLogcatPath, msg, StringUtils.FTP_LOG_TAG);
	}

	public static void log_ftp_tx(String path, long TXBytes, long TXSegments) {

		String msg = "Current TX bytes - " + TXBytes;
		Log.d(StringUtils.FTP_LOG_TAG, msg);
		FileUtil.writeToLogcatFile(path, msg, StringUtils.FTP_LOG_TAG);
		msg = "Current TXSegments - " + TXSegments;
		Log.d(StringUtils.FTP_LOG_TAG, msg);
		FileUtil.writeToLogcatFile(path, msg, StringUtils.FTP_LOG_TAG);
	}

	public static void log_ftp_tx(String ftpLogcatPath, long TXBytes,
			long TXSegments, long AllTXPackets, long TotalTXPackets,
			long MobileTXPackets) {
		String msg = "Current TX bytes - " + TXBytes;
		Log.d(StringUtils.FTP_LOG_TAG, msg);
		FileUtil.writeToLogcatFile(ftpLogcatPath, msg, StringUtils.FTP_LOG_TAG);

		msg = "Current TXSegments - " + TXSegments;
		Log.d(StringUtils.FTP_LOG_TAG, msg);
		FileUtil.writeToLogcatFile(ftpLogcatPath, msg, StringUtils.FTP_LOG_TAG);

		msg = "All TX Packets - " + AllTXPackets;
		Log.d(StringUtils.FTP_LOG_TAG, msg);
		FileUtil.writeToLogcatFile(ftpLogcatPath, msg, StringUtils.FTP_LOG_TAG);

		msg = "Total TX Packets - " + TotalTXPackets;
		Log.d(StringUtils.FTP_LOG_TAG, msg);
		FileUtil.writeToLogcatFile(ftpLogcatPath, msg, StringUtils.FTP_LOG_TAG);

		msg = "Mobile TX Packets - " + MobileTXPackets;
		Log.d(StringUtils.FTP_LOG_TAG, msg);
		FileUtil.writeToLogcatFile(ftpLogcatPath, msg, StringUtils.FTP_LOG_TAG);

	}

	public static void log_udp(String msg) {
		Log.d(StringUtils.UDP_LOG_TAG, msg);
	}

	public static void log_udp(String path, long TXBytes, long TXPackets) {

		String msg = "Current TX bytes - " + TXBytes;
		Log.d(StringUtils.UDP_LOG_TAG, msg);
		FileUtil.writeToLogcatFile(path, msg, StringUtils.UDP_LOG_TAG);
		msg = "Current TXPackets - " + TXPackets;
		Log.d(StringUtils.UDP_LOG_TAG, msg);
		FileUtil.writeToLogcatFile(path, msg, StringUtils.UDP_LOG_TAG);
	}

	public static void log_ping(String msg) {
		Log.d(StringUtils.PING_LOG_TAG, msg);
	}

	public static void log_ping(String path, String msg) {
		if (!Validator.isEmpty(msg)) {
			if (msg.indexOf("\n") != -1) {
				String[] splts = msg.split("\n");
				int len = splts.length;
				Log.d(StringUtils.PING_LOG_TAG, "splt length => " + len);

				for (int spt_i = 0; spt_i < len; spt_i++) {
					if (!Validator.isEmpty(splts[spt_i])) {
						Log.d(StringUtils.PING_LOG_TAG, " spt -> "
								+ splts[spt_i]);
						FileUtil.writeToLogcatFile(path, splts[spt_i],
								StringUtils.PING_LOG_TAG);
					}
				}
			} else {
				FileUtil.writeToLogcatFile(path, msg, StringUtils.PING_LOG_TAG);
			}
		}
	}

	public static void log_browser(String msg) {
		Log.d(StringUtils.BROWSER_LOG_TAG, msg);
	}

	public static void log_browser(String path, String msg) {
		Log.d(StringUtils.BROWSER_LOG_TAG, msg);
		FileUtil.writeToLogcatFile(path, msg, StringUtils.BROWSER_LOG_TAG);
	}

	public static void log_browser(String path, long RXBytes, long RXSegments) {

		String msg = "Current RX bytes - " + RXBytes;
		Log.d(StringUtils.BROWSER_LOG_TAG, msg);
		FileUtil.writeToLogcatFile(path, msg, StringUtils.BROWSER_LOG_TAG);
		msg = "Current RXSegments - " + RXSegments;
		Log.d(StringUtils.BROWSER_LOG_TAG, msg);
		FileUtil.writeToLogcatFile(path, msg, StringUtils.BROWSER_LOG_TAG);
	}

	public static void log_voip(String msg) {
		Log.d(StringUtils.VOIP_LOG_TAG, msg);
	}

	public static void log_voip_rx(String path, long RXBytes, long RXSegments) {

		String msg = "Current RX bytes - " + RXBytes;
		Log.d(StringUtils.VOIP_LOG_TAG, msg);
		FileUtil.writeToLogcatFile(path, msg, StringUtils.VOIP_LOG_TAG);
		msg = "Current RXSegments - " + RXSegments;
		Log.d(StringUtils.VOIP_LOG_TAG, msg);
		FileUtil.writeToLogcatFile(path, msg, StringUtils.VOIP_LOG_TAG);
	}

	public static void log_voip_tx(String path, long TXBytes, long TXSegments) {

		String msg = "Current TX bytes - " + TXBytes;
		Log.d(StringUtils.VOIP_LOG_TAG, msg);
		FileUtil.writeToLogcatFile(path, msg, StringUtils.VOIP_LOG_TAG);
		msg = "Current TXSegments - " + TXSegments;
		Log.d(StringUtils.VOIP_LOG_TAG, msg);
		FileUtil.writeToLogcatFile(path, msg, StringUtils.VOIP_LOG_TAG);
	}

	public static void printBundleValues(Bundle bundle) {
		event("printBundleValues()");

		try {
			debug("Bundle Values:\n---------------\n");
			if (bundle != null) {
				Set<String> keys = bundle.keySet();
				if (keys != null) {
					Iterator<?> itr = keys.iterator();
					while (itr.hasNext()) {
						String key = (String) itr.next();
						String val = (String) bundle.get(key);
						debug("Bundle Value[" + key + "]-->" + val);
					}
				}
			}
		} catch (Exception e) {

		}

	}
}