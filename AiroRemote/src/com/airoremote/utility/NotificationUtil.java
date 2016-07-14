package com.airoremote.utility;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

import com.airoremote.R;
import com.airoremote.TestTypeActivity;
import com.airoremote.config.StringUtils;

public class NotificationUtil {
	private static int TEST_FINISHED_NOTI = R.string.test_finished;
	private static int TEST_RUNNING_NOTI = R.string.test_running;

	/**
	 * Show a notification while this service is running.
	 */
	public static void showFinishedNotification(Context context, String sType,
			String name, String msg) {
		L.event("In showFinishedNotification()... " + sType);
		NotificationManager mNM = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		CharSequence text = StringUtils.getTestTypeFromCode(sType)
				+ " Completed - " + name + " (" + msg + ")";
		Notification notification = new Notification.Builder(context)
		.setContentTitle(context.getText(R.string.app_name))
        .setContentText(text)
        .setSmallIcon(R.drawable.icon)
        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon))
        .build();
		//Notification notification = new Notification(R.drawable.icon, text,
				//System.currentTimeMillis());
		Intent intent_finished = new Intent(context.getApplicationContext(),
				TestTypeActivity.class);
		intent_finished.putExtra(StringUtils.EXTRA_TEST_NAME, name);
		intent_finished.putExtra(StringUtils.EXTRA_TEST_STATUS,
				StringUtils.EXTRA_STATUS_FINISHED);
		intent_finished.putExtra(StringUtils.EXTRA_STATUS_MSG, text);

		intent_finished.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);

		PendingIntent contentIntent = PendingIntent.getActivity(
				context.getApplicationContext(), getNotificationTypeId(sType),
				intent_finished, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		//notification.setLatestEventInfo(context.getApplicationContext(),
			//	context.getText(R.string.app_name), text, contentIntent);
		mNM.cancel(getRunningNotificationTypeId(sType));
		mNM.notify(getNotificationTypeId(sType), notification);
	}

	/**
	 * Show a notification while this service is running.
	 */
	public static void showRunningNotification(Context context, String sType) {
		L.event("In showRunningNotification()... " + sType);
		NotificationManager mNM = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		CharSequence text = StringUtils.getTestTypeFromCode(sType) + " Running";
		Notification notification = new Notification.Builder(context)
		.setContentTitle(context.getText(R.string.app_name))
        .setContentText(text)
        .setSmallIcon(R.drawable.icon)
        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon))
        .build();
		//Notification notification = new Notification(R.drawable.icon, text,
				//System.currentTimeMillis());
		Intent running = new Intent(context.getApplicationContext(),
				TestTypeActivity.class);
		running.putExtra(StringUtils.EXTRA_TEST_STATUS,
				StringUtils.EXTRA_STATUS_RUNNING);
		PendingIntent contentIntent = PendingIntent.getActivity(
				context.getApplicationContext(), 0, running, 0);
		notification.flags = Notification.FLAG_NO_CLEAR;

		//notification.setLatestEventInfo(context.getApplicationContext(),
				//context.getText(R.string.app_name), text, contentIntent);
		mNM.cancel(getNotificationTypeId(sType));
		mNM.notify(getRunningNotificationTypeId(sType), notification);
	}

	/**
	 * Show a notification while this service is running.
	 */
	public static void showNotification(Context context, String sMsg,
			boolean cancellable) {
		NotificationManager mNM = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNM.cancel(R.id.id_retry);
		CharSequence text = sMsg;
		Notification notification = new Notification.Builder(context)
		.setContentTitle(context.getText(R.string.app_name))
        .setContentText(text)
        .setSmallIcon(R.drawable.icon)
        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon))
        .build();
		//Notification notification = new Notification(R.drawable.icon, text,
				//System.currentTimeMillis());
		Intent running = new Intent(context.getApplicationContext(),
				TestTypeActivity.class);
		running.putExtra(StringUtils.EXTRA_TEST_STATUS,
				StringUtils.EXTRA_STATUS_RUNNING);
		PendingIntent contentIntent = PendingIntent.getActivity(
				context.getApplicationContext(), 0, running, 0);
		if (!cancellable)
			notification.flags = Notification.FLAG_NO_CLEAR;
		else
			notification.flags = Notification.FLAG_AUTO_CANCEL;

		//notification.setLatestEventInfo(context.getApplicationContext(),
			//	context.getText(R.string.app_name), text, contentIntent);
		mNM.notify(R.id.id_retry, notification);
	}

	/**
	 * Show a notification while this service is running.
	 */
	public static void showFailedNotification(Context context, String sMsg,
			boolean isViewable) {
		L.event("In showViewableNotification()... " + sMsg);
		NotificationManager mNM = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		CharSequence text = sMsg;
		Notification notification = new Notification.Builder(context)
		.setContentTitle(context.getText(R.string.app_name))
        .setContentText(text)
        .setSmallIcon(R.drawable.icon)
        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon))
        .build();
		//Notification notification = new Notification(R.drawable.icon, text,
			//	System.currentTimeMillis());
		Intent intent_finished = new Intent(context.getApplicationContext(),
				TestTypeActivity.class);
		intent_finished.putExtra(StringUtils.EXTRA_TEST_NAME, "");
		intent_finished.putExtra(StringUtils.EXTRA_TEST_STATUS,
				StringUtils.EXTRA_STATUS_FINISHED);
		if (isViewable)
			intent_finished.putExtra(StringUtils.EXTRA_STATUS_MSG, text);

		intent_finished.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);

		PendingIntent contentIntent = PendingIntent.getActivity(
				context.getApplicationContext(), R.id.id_failed,
				intent_finished, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		//notification.setLatestEventInfo(context.getApplicationContext(),
			//	context.getText(R.string.app_name), text, contentIntent);
		mNM.notify(R.id.id_failed, notification);
	}

	static int getNotificationTypeId(String sTestType) {
		int noti_id = 0;

		if (sTestType.equals(StringUtils.TEST_TYPE_CODE_MO))
			noti_id = R.id.id_test_mo;
		else if (sTestType.equals(StringUtils.TEST_TYPE_CODE_MT))
			noti_id = R.id.id_test_mt;
		else if (sTestType.equals(StringUtils.TEST_TYPE_CODE_FTP))
			noti_id = R.id.id_test_ftp;
		else if (sTestType.equals(StringUtils.TEST_TYPE_CODE_UDP))
			noti_id = R.id.id_test_udp;
		else if (sTestType.equals(StringUtils.TEST_TYPE_CODE_PING))
			noti_id = R.id.id_test_ping;
		else if (sTestType.equals(StringUtils.TEST_TYPE_CODE_BROWSER))
			noti_id = R.id.id_test_browser;
		else if (sTestType.equals(StringUtils.TEST_TYPE_CODE_VOIP))
			noti_id = R.id.id_test_voip;
		else if (sTestType.equals(StringUtils.TEST_TYPE_CODE_EXTERNAL))
			noti_id = R.id.id_test_external;

		return noti_id;
	}

	static int getRunningNotificationTypeId(String sTestType) {
		int noti_id = 0;

		if (sTestType.equals(StringUtils.TEST_TYPE_CODE_MO))
			noti_id = R.id.id_rn_test_mo;
		else if (sTestType.equals(StringUtils.TEST_TYPE_CODE_MT))
			noti_id = R.id.id_rn_test_mt;
		else if (sTestType.equals(StringUtils.TEST_TYPE_CODE_FTP))
			noti_id = R.id.id_rn_test_ftp;
		else if (sTestType.equals(StringUtils.TEST_TYPE_CODE_UDP))
			noti_id = R.id.id_rn_test_udp;
		else if (sTestType.equals(StringUtils.TEST_TYPE_CODE_PING))
			noti_id = R.id.id_rn_test_ping;
		else if (sTestType.equals(StringUtils.TEST_TYPE_CODE_BROWSER))
			noti_id = R.id.id_rn_test_browser;
		else if (sTestType.equals(StringUtils.TEST_TYPE_CODE_VOIP))
			noti_id = R.id.id_rn_test_voip;
		else if (sTestType.equals(StringUtils.TEST_TYPE_CODE_EXTERNAL))
			noti_id = R.id.id_rn_test_external;

		return noti_id;
	}

	public static void cancelAllNotification(Context context) {

		NotificationManager mNM = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		try {
			mNM.cancelAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void cancelNotification(Context context, String sTestType) {
		int id = getNotificationTypeId(sTestType);
		NotificationManager mNM = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		try {
			mNM.cancel(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
