package com.airometric.utility.runners;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;

import com.airometric.Airometric;
import com.airometric.AppActivity;
import com.airometric.TestTypeActivity;
import com.airometric.api.ResponseCodes;
import com.airometric.classes.FTPTestConfig;
import com.airometric.classes.TestConfig;
import com.airometric.config.Constants;
import com.airometric.config.Messages;
import com.airometric.config.StringUtils;
import com.airometric.storage.Preferences;
import com.airometric.storage.SettingsStore;
import com.airometric.utility.DeviceUtil;
import com.airometric.utility.FTP;
import com.airometric.utility.FileUtil;
import com.airometric.utility.L;
import com.airometric.utility.NotificationUtil;
import com.airometric.utility.TimeUtil;

public class FTPTestRunner extends Runner implements Serializable {

	AppActivity context;
	TestConfig testconfig;
	private Handler handler = new Handler();
	Preferences pref;
	SettingsStore settings;
	long StartTXBytes = 0, StartTXSegments = 0, StartAllTXPackets = 0,
			StartTotalTXPackets = 0, StartMobileTXPackets = 0;
	long StartRXBytes = 0, StartRXSegments = 0, StartAllRXPackets = 0,
			StartTotalRXPackets = 0, StartMobileRXPackets = 0;
	int uid, deviceOSVersion;
	Timer timer;
	FTPTestConfig FTPTestConfigObj;
	StartTestTask uploadTask;
	FTP ftp;
	DownloadTimeTask downloadTimerTask;
	UploadTimeTask uploadTimerTask;

	public FTPTestRunner(AppActivity context, TestConfig testconfig) {
		this.context = context;
		this.testconfig = testconfig;
		pref = new Preferences(context);
		settings = new SettingsStore(context);
		Constants.CurrentRunner = this;
	}

	public void startTest() {

		deviceOSVersion = android.os.Build.VERSION.SDK_INT;		
		L.debug("DEVICE OS VERSION: " + deviceOSVersion);

		FTPTestConfigObj = testconfig.getFTPTestConfig();

		/*
		 * 
		 * String sTestName = testconfig.getTestName() +
		 * StringUtils.TEST_CYCLE_APPEND_FILE + testconfig.getTestCycle();
		 */

		String sTestName = pref.getValue(Preferences.KEY_TEST_NAME, "")
				+ StringUtils.TEST_CYCLE_APPEND_FILE
				+ testconfig.getTestCycle();

		pref.setFTPTestRunningState(true);

		DeviceUtil dv = new DeviceUtil(context);

		String sDeviceInfoXML = dv.getDeviceInfo(
				StringUtils.FILE_CODE_TEST_TYPE_FTP, sTestName,
				pref.getUsername(),
				pref.getValue(Preferences.KEY_SELECTED_MARKET_PLACE_ID, ""));
		L.debug(sDeviceInfoXML);

		String sCurrTime = TimeUtil.getCurrentTimeFilename();

		FileUtil.CURRENT_FTP_TESTTIME = sCurrTime;
		String path = FileUtil.FTP_LOG_DIR + "deviceinfo" + "_" + sTestName
				+ "_" + sCurrTime + ".xml";
		File fle = new File(path);
		if (fle.exists()) {
			fle.delete();
		}
		String dev_info_path = FileUtil.writeToXMLFile(path, sDeviceInfoXML);
		L.debug("Device info initial data written into " + dev_info_path);

		pref.putValue(Preferences.KEY_CURRENT_FTP_DEV_INFO_PATH, dev_info_path);

		String logcat_path = FileUtil.FTP_LOG_DIR
				+ StringUtils.FILE_CODE_TEST_TYPE_FTP + "_" + dv.getIMEI()
				+ "_" + sTestName + "_" + sCurrTime + ".txt";
		
		L.debug("!!! logcat_path !!!!: " + logcat_path);
		

		File log_fle = new File(logcat_path);

		try {
			log_fle.createNewFile();
		} catch (IOException e) {
		}
		pref.putValue(Preferences.KEY_CURRENT_FTP_LOGCAT_PATH,
				log_fle.getAbsolutePath());
		try {
			Airometric app = (Airometric) context.getApplication();
			app.startListeners(dev_info_path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		NotificationUtil.cancelNotification(context,
				StringUtils.TEST_TYPE_CODE_FTP);

		uploadTask = new StartTestTask();
		uploadTask.execute();
		Constants.CurrentTask = uploadTask;

		NotificationUtil.showRunningNotification(context,
				StringUtils.TEST_TYPE_CODE_FTP);
		context.showActivity(TestTypeActivity.class);
	}

	long no_of_cycles = 0, pass = 0;
	String sHostURL, sUsername, sPassword, sDownloadSrcFilePath,
			sDownloadDestFilePath, sUploadSrcFilePath, sUploadDestDirPath;

	boolean bManuallUploadStatus;

	int port = 21;

	void startTesting() {
		no_of_cycles = Long.parseLong(FTPTestConfigObj.sTestCyles);

		DeviceUtil.clearLogcat();

		//context.print("FTPTestConfigObj -> " + FTPTestConfigObj.toString());
		sHostURL = FTPTestConfigObj.sServerURL;
		sUsername = FTPTestConfigObj.sUsername;
		sPassword = FTPTestConfigObj.sPassword;
		sDownloadSrcFilePath = FTPTestConfigObj.sFileToDownload;
		sUploadSrcFilePath = FTPTestConfigObj.sManuallUploadFile;

		bManuallUploadStatus = FTPTestConfigObj.bManuallUploadStatus;

		sDownloadDestFilePath = FileUtil.APP_TMP_DIR + "/"
				+ new File(FTPTestConfigObj.sFileToDownload).getName();

		context.print("sDownloadSrcFilePath - " + sDownloadSrcFilePath
				+ ", sDownloadDestFilePath - " + sDownloadDestFilePath);

		if (bManuallUploadStatus) {
			sUploadSrcFilePath = FTPTestConfigObj.sManuallUploadFile;
			L.debug("FTP TEST RUNNER: Manuall Upload File Path: "
					+ sUploadSrcFilePath);
		} else {
			sUploadSrcFilePath = sDownloadDestFilePath;

			L.debug("FTP TEST RUNNER: Upload File Path: " + sUploadSrcFilePath);
		}

		sUploadDestDirPath = FTPTestConfigObj.sUploadPath;

		for (int cyc_i = 0; cyc_i < no_of_cycles; cyc_i++) {
			context.print("startTesting()... cyc_i  -> " + cyc_i);
			L.debug("*********** FTP TEST STARTED ***********");
			L.debug("FTP Cycle's count: " + cyc_i);
			doDownloadAndUpload();
		}

		// doDownloadAndUpload();
		DownloadAndUploadCompletedHandler.sendEmptyMessage(0);
	}

	protected void doDownloadAndUpload() {
		context.print("doDownloadAndUpload()...");
		Message msg = new Message();
		Bundle bndle = new Bundle();

		String sResponseCode = "", sResponseDesc = "";
		try {

			ftp = new FTP(handler);
			if (ftp.ftpConnect(sHostURL, sUsername, sPassword, port)) {

				timer = new Timer();
				uid = android.os.Process.myUid();
				if (Constants.OS_VERSION_JELLY_BEAN_MR2 == deviceOSVersion
						|| Constants.OS_VERSION_KIT_KAT == deviceOSVersion || Constants.OS_VERSION_LOLLIPOP == deviceOSVersion) {
					StartRXBytes = DeviceUtil.getStats(uid).getReceiveCount();
					// StartRXBytes = TrafficStats.getUidRxBytes(uid);
					StartRXSegments = TrafficStats.getUidTcpRxSegments(uid);
				} else {
					StartRXBytes = TrafficStats.getUidTcpRxBytes(uid);
					StartRXSegments = TrafficStats.getUidTcpRxSegments(uid);
				}
				context.debug("Initial RX bytes - " + StartRXBytes
						+ ", Initial RX Segments - " + StartRXSegments);
				downloadTimerTask = new DownloadTimeTask();
				timer.schedule(downloadTimerTask, 0,
						Constants.FTP_LOG_INTERVAL * 1000);
				
				// Download
				
				if (ftp.ftpDownload(sDownloadSrcFilePath, sDownloadDestFilePath)) {
					context.print("File downloaded");
					sResponseCode = ResponseCodes.SUCCESS;
					timer.cancel();

					try {

						timer = new Timer();

						if (Constants.OS_VERSION_JELLY_BEAN_MR2 == deviceOSVersion
								|| Constants.OS_VERSION_KIT_KAT == deviceOSVersion || Constants.OS_VERSION_LOLLIPOP == deviceOSVersion) {
							StartTXBytes = TrafficStats.getUidTxBytes(uid);
							StartTXSegments = TrafficStats
									.getUidTcpTxSegments(uid);
						}

						else {
							StartTXBytes = TrafficStats.getUidTcpTxBytes(uid);
							StartTXSegments = TrafficStats
									.getUidTcpTxSegments(uid);
						}

						context.debug("Initial TX bytes - " + StartTXBytes
								+ ", Initial TX segments - " + StartTXSegments);
						uploadTimerTask = new UploadTimeTask();
						timer.schedule(uploadTimerTask, 0,
								Constants.FTP_LOG_INTERVAL * 1000);

						L.debug("Upload Section- src file path: "
								+ sUploadSrcFilePath);
						L.debug("Upload Section- dest file path: "
								+ sUploadDestDirPath);

						// Upload
						
						if (ftp.ftpUpload(sUploadSrcFilePath, new File(
								sUploadSrcFilePath).getName(),
								sUploadDestDirPath)) {
							context.print("File uploaded");
							sResponseCode = ResponseCodes.SUCCESS;
							L.debug("********* FTP UPLOADING SUCCESS **********");
						} else {
							context.print("File upload failed - "
									+ ftp.getServerError());
							sResponseCode = StringUtils.ERROR_CODE;
							sResponseDesc = Messages.FTP_UPLOAD_FAILED + "("
									+ ftp.getServerError() + ")";
						}
					} catch (Exception e) {
						e.printStackTrace();
						sResponseCode = StringUtils.ERROR_CODE;
						sResponseDesc = Messages.FTP_UPLOAD_FAILED + "("
								+ Messages.err(e) + ")";
					}

				} else {
					context.print("File download failed - "
							+ ftp.getServerError());
					sResponseCode = StringUtils.ERROR_CODE;
					sResponseDesc = Messages.FTP_DOWNLOAD_FAILED;
				}
				timer.cancel();
			} else {
				sResponseCode = StringUtils.ERROR_CODE;
				sResponseDesc = Messages.FTP_LOGIN_FAILED;
			}
			try {
				if (ftp.getClient().isConnected())
					ftp.ftpDisconnect();
			} catch (Exception e) {

			}
		} catch (Exception e) {
			e.printStackTrace();
			sResponseCode = StringUtils.ERROR_CODE;
			sResponseDesc = Messages.err(e);
		}

		bndle.putString(StringUtils.CODE, sResponseCode);
		bndle.putString(StringUtils.DESC, sResponseDesc);
		msg.setData(bndle);
		if (!pref.isTestCanceled() && pref.isTestRunning())
			DownloadAndUploadHandler.sendMessage(msg);
	}

	private Handler DownloadAndUploadHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			// ###################
			String sResponseCode = msg.getData().getString(StringUtils.CODE);
			String sResponseDesc = msg.getData().getString(StringUtils.DESC);

			clearFTPFiles();

			if (sResponseCode.equals(ResponseCodes.SUCCESS)) {
				L.debug("File download and upload success");
			} else {
				L.debug(sResponseDesc);

				if (!pref.isTestCanceled() && pref.isTestRunning()) {
					L.debug("RunnableFailed...");
					pref.setFTPTestRunningState(false);

					/*
					 * NotificationUtil.showFinishedNotification( context,
					 * StringUtils.TEST_TYPE_CODE_FTP, testconfig.getTestName()
					 * + "_" + testconfig.getTestCycle(),
					 * Messages.FTP_DOWNLOAD_FAILED);
					 */

					NotificationUtil.showFinishedNotification(context,
							StringUtils.TEST_TYPE_CODE_FTP,
							pref.getValue(Preferences.KEY_TEST_NAME, "") + "_"
									+ testconfig.getTestCycle(),
							Messages.FTP_DOWNLOAD_FAILED);

					DeviceUtil.updateTestScreen();
					doResultUploaded(sResponseCode, sResponseDesc);
				}
			}
		}
	};

	private Handler DownloadAndUploadCompletedHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (!pref.isTestCanceled() && pref.isTestRunning()) {
				L.debug("DownloadAndUploadCompletedHandler...");
				pref.setFTPTestRunningState(false);

				// testconfig.getTestName()

				new ResultUploader(context, pref.getValue(
						Preferences.KEY_TEST_NAME, "")
						+ StringUtils.TEST_CYCLE_APPEND_FILE
						+ testconfig.getTestCycle(),
						StringUtils.TEST_TYPE_CODE_FTP,
						settings.getFTPDeviceInfoPath(),
						settings.getFTPLogcatPath()) {
					public void resultUploaded(String code, String desc) {
						doResultUploaded(code, desc);
					};
				};
			}

		}
	};

	private void clearFTPFiles() {
		if (sDownloadDestFilePath != null) {
			File downFile = new File(sDownloadDestFilePath);
			if (downFile.exists() && downFile.isFile())
				downFile.delete();
		}
	}

	class DownloadTimeTask extends TimerTask {
		long CurrRXBytes, CurrRXSegments;

		public DownloadTimeTask() {
		}

		public void run() {
			if (pref.isFTPTestRunning()) {

				if (Constants.OS_VERSION_JELLY_BEAN_MR2 == deviceOSVersion
						|| Constants.OS_VERSION_KIT_KAT == deviceOSVersion || Constants.OS_VERSION_LOLLIPOP == deviceOSVersion) {
					CurrRXBytes = TrafficStats.getUidRxBytes(uid);
					CurrRXSegments = TrafficStats.getUidTcpRxSegments(uid);
				} else {
					CurrRXBytes = TrafficStats.getUidTcpRxBytes(uid);
					CurrRXSegments = TrafficStats.getUidTcpRxSegments(uid);
				}

				long CurrAllRXPackets = TrafficStats.getUidRxPackets(uid);
				long CurrTotalRXPackets = TrafficStats.getTotalRxPackets();
				long CurrMobileRXPackets = TrafficStats.getMobileRxPackets();

				/*context.debug("StartRXBytes bytes - " + StartRXBytes
						+ ", CurrRXBytes - " + CurrRXBytes
						+ ", CurrRXSegments - " + CurrRXSegments);
				*/
				
				long RXBytes = CurrRXBytes - StartRXBytes;
				long RXSegments = CurrRXSegments - StartRXSegments;
				
				context.debug("Start :" + StartAllRXPackets + "RXPackets :" + CurrAllRXPackets);

				long AllRXPackets = CurrAllRXPackets - StartAllRXPackets;
				
				context.debug("AllRXPackets :" + AllRXPackets);
				
				long TotalRXPackets = CurrTotalRXPackets - StartTotalRXPackets;
				long MobileRXPackets = CurrMobileRXPackets
						- StartMobileRXPackets;

				L.log_ftp_rx(settings.getFTPLogcatPath(), RXBytes, RXSegments,
						AllRXPackets, TotalRXPackets, MobileRXPackets);
			} else
				timer.cancel();
		}
	}

	class UploadTimeTask extends TimerTask {
		long CurrTXBytes, CurrTXSegments;

		public UploadTimeTask() {
		}

		public void run() {
			if (pref.isFTPTestRunning()) {

				if (Constants.OS_VERSION_JELLY_BEAN_MR2 == deviceOSVersion
						|| Constants.OS_VERSION_KIT_KAT == deviceOSVersion || Constants.OS_VERSION_LOLLIPOP == android.os.Build.VERSION.SDK_INT) {
					CurrTXBytes = DeviceUtil.getStats(uid).getSendCount();
					CurrTXSegments = TrafficStats.getUidTcpTxSegments(uid);
				} else {
					CurrTXBytes = TrafficStats.getUidTcpTxBytes(uid);
					CurrTXSegments = TrafficStats.getUidTcpTxSegments(uid);
				}

				context.debug("StartTXBytes bytes - " + StartTXBytes
						+ ", CurrTXBytes - " + CurrTXBytes
						+ ", CurrTXSegments - " + CurrTXSegments);

				long CurrAllTXPackets = TrafficStats.getUidTxPackets(uid);
				long CurrTotalTXPackets = TrafficStats.getTotalTxPackets();
				long CurrMobileTXPackets = TrafficStats.getMobileTxPackets();

				long TXBytes = CurrTXBytes - StartTXBytes;
				long TXSegments = CurrTXSegments - StartTXSegments;
				long AllTXPackets = CurrAllTXPackets - StartAllTXPackets;
				long TotalTXPackets = CurrTotalTXPackets - StartTotalTXPackets;
				long MobileTXPackets = CurrMobileTXPackets
						- StartMobileTXPackets;

				L.log_ftp_tx(settings.getFTPLogcatPath(), TXBytes, TXSegments,
						AllTXPackets, TotalTXPackets, MobileTXPackets);
			} else {
				timer.cancel();
				try {
					if (ftp != null && ftp.getClient() != null)
						ftp.getClient().abort();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
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

		@Override
		protected void onCancelled() {
			context.debug("onCancelled()");
			// try {
			// if (ftp != null && ftp.getClient() != null
			// && ftp.getClient().isConnected())
			// ftp.getClient().abort();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			super.onCancelled();
		}
	}

	public void doResultUploaded(String code, String desc) {

	}

	@Override
	protected void cancelTest() {

		try {
			uploadTask.cancel(true);
		} catch (Exception e) {
		}
		try {
			uploadTimerTask.cancel();
		} catch (Exception e) {
		}
		try {
			downloadTimerTask.cancel();
		} catch (Exception e) {
		}
		clearFTPFiles();
	};
}
