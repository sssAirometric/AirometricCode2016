package com.airometric.utility.runners;

import java.io.File;
import java.util.ArrayList;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.airometric.AppActivity;
import com.airometric.api.APIManager;
import com.airometric.api.ResponseCodes;
import com.airometric.classes.BrowserTestConfig;
import com.airometric.classes.FTPTestConfig;
import com.airometric.classes.MOTestConfig;
import com.airometric.classes.MTTestConfig;
import com.airometric.classes.PingTestConfig;
import com.airometric.classes.TestConfig;
import com.airometric.classes.UDPTestConfig;
import com.airometric.classes.VOIPTestConfig;
import com.airometric.config.Constants;
import com.airometric.config.Messages;
import com.airometric.config.StringUtils;
import com.airometric.storage.Preferences;
import com.airometric.storage.SettingsStore;
import com.airometric.utility.DeviceUtil;
import com.airometric.utility.FileUtil;
import com.airometric.utility.L;
import com.airometric.utility.NotificationUtil;

public class TestRunner {
	private TestConfig testconfig;
	private MOTestConfig objMOConfig = null;
	private MTTestConfig objMTConfig = null;
	private FTPTestConfig objFTPConfig = null;
	private UDPTestConfig objUDPConfig = null;
	private PingTestConfig objPingConfig = null;
	private BrowserTestConfig objBrowserConfig = null;
	private VOIPTestConfig objVOIPConfig = null;

	private AppActivity activity;
	private int curr_test_number, iCurrCycle;
	private ArrayList<String> arrLstSelectedTestsCodes;
	private Preferences pref;
	private String sCurrTestName;

	public TestRunner(AppActivity activity, TestConfig testconfig,
			ArrayList<String> arrLstSelectedTestsCodes) {
		this.activity = activity;
		this.testconfig = testconfig;
		this.arrLstSelectedTestsCodes = arrLstSelectedTestsCodes;

		objMOConfig = testconfig.getMOTestConfig();
		objMTConfig = testconfig.getMTTestConfig();
		objFTPConfig = testconfig.getFTPTestConfig();
		objUDPConfig = testconfig.getUDPTestConfig();
		objPingConfig = testconfig.getPingTestConfig();
		objBrowserConfig = testconfig.getBrowserTestConfig();
		objVOIPConfig = testconfig.getVOIPTestConfig();
		Constants.CurrentTestRunner = this;
	}

	public void startTests() {
		iCurrCycle++;
		L.event("startTests()... " + " iCurrCycle = " + iCurrCycle);
		testconfig.setTestCycle(iCurrCycle);
		pref = new Preferences(activity);

		pref.clearFilePaths();
		// sCurrTestName = testconfig.getTestName() + "_" + iCurrCycle;
		sCurrTestName = pref.getValue(Preferences.KEY_TEST_NAME, "");
		L.event("startTests()::sCurrTestName -> " + sCurrTestName);
		pref.saveCurrentTestName(sCurrTestName);
		curr_test_number = pref.getCurrentTestNumber();
		pref.setAsTestCanceled(false);
		curr_test_number++;
		pref.saveCurrentTestNumber(curr_test_number);

		if (arrLstSelectedTestsCodes.contains(StringUtils.TEST_TYPE_CODE_MO))
			startMOTest();
		else if (arrLstSelectedTestsCodes
				.contains(StringUtils.TEST_TYPE_CODE_MT))
			startMTTest();
		else if (arrLstSelectedTestsCodes
				.contains(StringUtils.TEST_TYPE_CODE_FTP))
			startFTPTest();
		else if (arrLstSelectedTestsCodes
				.contains(StringUtils.TEST_TYPE_CODE_UDP))
			startUDPTest();
		else if (arrLstSelectedTestsCodes
				.contains(StringUtils.TEST_TYPE_CODE_PING))
			startPingTest();
		else if (arrLstSelectedTestsCodes
				.contains(StringUtils.TEST_TYPE_CODE_BROWSER))
			startBrwoserTest();
		else if (arrLstSelectedTestsCodes
				.contains(StringUtils.TEST_TYPE_CODE_VOIP))
			startVOIPTest();
	}

	void startMOTest() {
		L.event("startMOTest()");
		if (objMOConfig != null) {
			MOTestRunner moTestRunner = new MOTestRunner(activity, testconfig) {
				@Override
				public void doResultUploaded(String code, String desc) {
					MOResultUploaded(code, desc);
				}
			};
			moTestRunner.startTest();
		}
	}

	void startMTTest() {
		if (!pref.isTestCanceled()) {
			L.event("startMTTest()...");
			if (objMTConfig != null) {
				MTTestRunner mtTestRunner = new MTTestRunner(activity,
						testconfig) {
					@Override
					public void doResultUploaded(String code, String desc) {
						MTResultUploaded(code, desc);
					}
				};
				mtTestRunner.startTest();
			}
		}
	}

	void startFTPTest() {
		if (!pref.isTestCanceled()) {
			L.event("startFTPTest()");
			if (objFTPConfig != null) {
				FTPTestRunner ftpTestRunner = new FTPTestRunner(activity,
						testconfig) {
					@Override
					public void doResultUploaded(String code, String desc) {
						FTPResultUploaded(code, desc);
					}
				};
				ftpTestRunner.startTest();
			}
		}
	}

	void startUDPTest() {
		if (!pref.isTestCanceled()) {
			L.event("startUDPTest()");
			if (objUDPConfig != null) {
				UDPTestRunner udpTestRunner = new UDPTestRunner(activity,
						testconfig) {
					@Override
					public void doResultUploaded(String code, String desc) {
						UDPResultUploaded(code, desc);
					}
				};
				udpTestRunner.startTest();
			}
		}
	}

	void startPingTest() {
		if (!pref.isTestCanceled()) {
			L.event("startPingTest()");
			if (objPingConfig != null) {
				PingTestRunner pingTestRunner = new PingTestRunner(activity,
						testconfig) {
					@Override
					public void doResultUploaded(String code, String desc) {
						PingResultUploaded(code, desc);
					}
				};
				pingTestRunner.startTest();
			}
		}
	}

	void startBrwoserTest() {
		if (!pref.isTestCanceled()) {
			L.event("startBrwoserTest()");
			if (objBrowserConfig != null) {

				Intent browser = new Intent(activity,
						WebviewActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(StringUtils.EXTRA_TEST_CONFIG,
						testconfig);
				bundle.putStringArrayList(
						StringUtils.EXTRA_TEST_SELECTED_CODES,
						arrLstSelectedTestsCodes);
				browser.putExtras(bundle);
				activity.showIntent(browser);

			/*	final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						Intent browser = new Intent(activity,
								WebviewActivity.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable(StringUtils.EXTRA_TEST_CONFIG,
								testconfig);
						bundle.putStringArrayList(
								StringUtils.EXTRA_TEST_SELECTED_CODES,
								arrLstSelectedTestsCodes);
						browser.putExtras(bundle);
						activity.showIntent(browser);
					}
				}, 5000);*/

			}
		}
	}

	void startVOIPTest() {
		if (!pref.isTestCanceled()) {
			L.event("startVOIPTest()");
			if (objVOIPConfig != null) {
				VOIPTestRunner voipTestRunner = new VOIPTestRunner(activity,
						testconfig) {
					@Override
					public void doResultUploaded(String code, String desc) {
						VOIPResultUploaded(code, desc);
					}
				};
				voipTestRunner.startTest();
			}
		}
	}

	public void MOResultUploaded(String code, String desc) {
		L.event("MOResultUploaded()::code -> " + code);
		L.event("MOResultUploaded()::desc -> " + desc);
		L.event("MOResultUploaded()::arrLstSelectedTestsCodes -> "
				+ arrLstSelectedTestsCodes);

		if (arrLstSelectedTestsCodes.contains(StringUtils.TEST_TYPE_CODE_MT))
			startMTTest();
		else if (arrLstSelectedTestsCodes
				.contains(StringUtils.TEST_TYPE_CODE_FTP))
			startFTPTest();
		else if (arrLstSelectedTestsCodes
				.contains(StringUtils.TEST_TYPE_CODE_UDP))
			startUDPTest();
		else if (arrLstSelectedTestsCodes
				.contains(StringUtils.TEST_TYPE_CODE_PING))
			startPingTest();
		else if (arrLstSelectedTestsCodes
				.contains(StringUtils.TEST_TYPE_CODE_BROWSER))
			startBrwoserTest();
		else if (arrLstSelectedTestsCodes
				.contains(StringUtils.TEST_TYPE_CODE_VOIP))
			startVOIPTest();
		else
			testCompleted();
	}

	public void MTResultUploaded(String code, String desc) {
		L.event("MTResultUploaded()::code -> " + code);
		L.event("MTResultUploaded()::desc -> " + desc);
		if (arrLstSelectedTestsCodes.contains(StringUtils.TEST_TYPE_CODE_FTP))
			startFTPTest();
		else if (arrLstSelectedTestsCodes
				.contains(StringUtils.TEST_TYPE_CODE_UDP))
			startUDPTest();
		else if (arrLstSelectedTestsCodes
				.contains(StringUtils.TEST_TYPE_CODE_PING))
			startPingTest();
		else if (arrLstSelectedTestsCodes
				.contains(StringUtils.TEST_TYPE_CODE_BROWSER))
			startBrwoserTest();
		else if (arrLstSelectedTestsCodes
				.contains(StringUtils.TEST_TYPE_CODE_VOIP))
			startVOIPTest();
		else
			testCompleted();
	}

	public void FTPResultUploaded(String code, String desc) {
		L.event("FTPResultUploaded()::code -> " + code);
		L.event("FTPResultUploaded()::desc -> " + desc);
		if (arrLstSelectedTestsCodes.contains(StringUtils.TEST_TYPE_CODE_UDP))
			startUDPTest();
		else if (arrLstSelectedTestsCodes
				.contains(StringUtils.TEST_TYPE_CODE_PING))
			startPingTest();
		else if (arrLstSelectedTestsCodes
				.contains(StringUtils.TEST_TYPE_CODE_BROWSER))
			startBrwoserTest();
		else if (arrLstSelectedTestsCodes
				.contains(StringUtils.TEST_TYPE_CODE_VOIP))
			startVOIPTest();
		else
			testCompleted();

	}

	public void UDPResultUploaded(String code, String desc) {
		L.event("UDPResultUploaded()::code -> " + code);
		L.event("UDPResultUploaded()::desc -> " + desc);
		if (arrLstSelectedTestsCodes.contains(StringUtils.TEST_TYPE_CODE_PING))
			startPingTest();
		else if (arrLstSelectedTestsCodes
				.contains(StringUtils.TEST_TYPE_CODE_BROWSER))
			startBrwoserTest();
		else if (arrLstSelectedTestsCodes
				.contains(StringUtils.TEST_TYPE_CODE_VOIP))
			startVOIPTest();
		else
			testCompleted();

	}

	public void PingResultUploaded(String code, String desc) {
		L.event("PingResultUploaded()::code -> " + code);
		L.event("PingResultUploaded()::desc -> " + desc);
		if (arrLstSelectedTestsCodes
				.contains(StringUtils.TEST_TYPE_CODE_BROWSER))
			startBrwoserTest();
		else if (arrLstSelectedTestsCodes
				.contains(StringUtils.TEST_TYPE_CODE_VOIP))
			startVOIPTest();
		else
			testCompleted();

	}

	public void BrowserResultUploaded(String code, String desc) {
		L.event("BrowserResultUploaded()::code -> " + code);
		L.event("BrowserResultUploaded()::desc -> " + desc);
		if (arrLstSelectedTestsCodes.contains(StringUtils.TEST_TYPE_CODE_VOIP))
			startVOIPTest();
		else
			testCompleted();
	}

	public void VOIPResultUploaded(String code, String desc) {
		L.event("VOIPResultUploaded()::code -> " + code);
		L.event("VOIPResultUploaded()::desc -> " + desc);
		testCompleted();

	}

	private void testCompleted() {
		L.event("testCompleted()...");
		checkUploadedFiles();

	}

	void checkUploadedFiles() {
		ArrayList<String> arrLstInfoResultFiles = new ArrayList<String>();
		ArrayList<String> arrLstLogcatResultFiles = new ArrayList<String>();
		SettingsStore settings = new SettingsStore(activity);

		String sMODevInfoPath, sMOLogcatPath, sMTDevInfoPath, sMTLogcatPath, sFTPDevInfoPath, sFTPLogcatPath, sUDPDevInfoPath, sUDPLogcatPath, sPingDevInfoPath, sPingLogcatPath, sBrowserDevInfoPath, sBrowserLogcatPath, sVOIPDevInfoPath, sVOIPLogcatPath;

		sMODevInfoPath = settings.getMODeviceInfoPath();
		sMOLogcatPath = settings.getMOLogcatPath();
		L.debug("MO Files :: \n Dev Info => " + sMODevInfoPath
				+ "\n Logcat => " + sMOLogcatPath);

		File flMODevInfo = new File(sMODevInfoPath);
		File flMOLogcat = new File(sMOLogcatPath);
		boolean isMODevInfoFileExists, isMOLogcatFileExists;
		isMODevInfoFileExists = flMODevInfo.exists();
		isMOLogcatFileExists = flMOLogcat.exists();

		L.debug("MO File Exist? \n Dev Info => " + isMODevInfoFileExists
				+ "\n Logcat => " + isMOLogcatFileExists);
		if (isMODevInfoFileExists || isMOLogcatFileExists) {
			arrLstInfoResultFiles.add(sMODevInfoPath);
			arrLstLogcatResultFiles.add(sMOLogcatPath);
		}

		sMTDevInfoPath = settings.getMTDeviceInfoPath();
		sMTLogcatPath = settings.getMTLogcatPath();
		L.debug("MT Files :: \n Dev Info => " + sMTDevInfoPath
				+ "\n Logcat => " + sMTLogcatPath);

		File flMTDevInfo = new File(sMTDevInfoPath);
		File flMTLogcat = new File(sMTLogcatPath);
		boolean isMTDevInfoFileExists, isMTLogcatFileExists;
		isMTDevInfoFileExists = flMTDevInfo.exists();
		isMTLogcatFileExists = flMTLogcat.exists();

		L.debug("MT File Exist? \n Dev Info => " + isMTDevInfoFileExists
				+ "\n Logcat => " + isMTLogcatFileExists);
		if (isMTDevInfoFileExists || isMTLogcatFileExists) {
			arrLstInfoResultFiles.add(sMTDevInfoPath);
			arrLstLogcatResultFiles.add(sMTLogcatPath);
		}

		sFTPDevInfoPath = settings.getFTPDeviceInfoPath();
		sFTPLogcatPath = settings.getFTPLogcatPath();
		L.debug("FTP Files :: \n Dev Info => " + sFTPDevInfoPath
				+ "\n Logcat => " + sFTPLogcatPath);

		File flFTPDevInfo = new File(sFTPDevInfoPath);
		File flFTPLogcat = new File(sFTPLogcatPath);
		boolean isFTPDevInfoFileExists, isFTPLogcatFileExists;
		isFTPDevInfoFileExists = flFTPDevInfo.exists();
		isFTPLogcatFileExists = flFTPLogcat.exists();

		L.debug("FTP File Exist? \n Dev Info => " + isFTPDevInfoFileExists
				+ "\n Logcat => " + isFTPLogcatFileExists);
		if (isFTPDevInfoFileExists || isFTPLogcatFileExists) {
			arrLstInfoResultFiles.add(sFTPDevInfoPath);
			arrLstLogcatResultFiles.add(sFTPLogcatPath);
		}

		sUDPDevInfoPath = settings.getUDPDeviceInfoPath();
		sUDPLogcatPath = settings.getUDPLogcatPath();
		L.debug("UDP Files :: \n Dev Info => " + sUDPDevInfoPath
				+ "\n Logcat => " + sUDPLogcatPath);

		File flUDPDevInfo = new File(sUDPDevInfoPath);
		File flUDPLogcat = new File(sUDPLogcatPath);
		boolean isUDPDevInfoFileExists, isUDPLogcatFileExists;
		isUDPDevInfoFileExists = flUDPDevInfo.exists();
		isUDPLogcatFileExists = flUDPLogcat.exists();

		L.debug("UDP File Exist? \n Dev Info => " + isUDPDevInfoFileExists
				+ "\n Logcat => " + isUDPLogcatFileExists);
		if (isUDPDevInfoFileExists || isUDPLogcatFileExists) {
			arrLstInfoResultFiles.add(sUDPDevInfoPath);
			arrLstLogcatResultFiles.add(sUDPLogcatPath);
		}

		sPingDevInfoPath = settings.getPingDeviceInfoPath();
		sPingLogcatPath = settings.getPingLogcatPath();
		L.debug("Ping Files :: \n Dev Info => " + sPingDevInfoPath
				+ "\n Logcat => " + sPingLogcatPath);

		File flPingDevInfo = new File(sPingLogcatPath);
		File flPingLogcat = new File(sPingLogcatPath);
		boolean isPingDevInfoFileExists, isPingLogcatFileExists;
		isPingDevInfoFileExists = flPingDevInfo.exists();
		isPingLogcatFileExists = flPingLogcat.exists();

		L.debug("Ping File Exist? \n Dev Info => " + isPingDevInfoFileExists
				+ "\n Logcat => " + isPingLogcatFileExists);
		if (isPingDevInfoFileExists || isPingLogcatFileExists) {
			arrLstInfoResultFiles.add(sPingDevInfoPath);
			arrLstLogcatResultFiles.add(sPingLogcatPath);
		}

		sBrowserDevInfoPath = settings.getBrowserDeviceInfoPath();
		sBrowserLogcatPath = settings.getBrowserLogcatPath();
		L.debug("Browser Files :: \n Dev Info => " + sBrowserDevInfoPath
				+ "\n Logcat => " + sBrowserLogcatPath);

		File flBrowserDevInfo = new File(sBrowserLogcatPath);
		File flBrowserLogcat = new File(sBrowserLogcatPath);
		boolean isBrowserDevInfoFileExists, isBrowserLogcatFileExists;
		isBrowserDevInfoFileExists = flBrowserDevInfo.exists();
		isBrowserLogcatFileExists = flBrowserLogcat.exists();

		L.debug("Browser File Exist? \n Dev Info => "
				+ isBrowserDevInfoFileExists + "\n Logcat => "
				+ isBrowserLogcatFileExists);
		if (isBrowserDevInfoFileExists || isBrowserLogcatFileExists) {
			arrLstInfoResultFiles.add(sBrowserDevInfoPath);
			arrLstLogcatResultFiles.add(sBrowserLogcatPath);
		}

		sVOIPDevInfoPath = settings.getVOIPDeviceInfoPath();
		sVOIPLogcatPath = settings.getVOIPLogcatPath();
		L.debug("VOIP Files :: \n Dev Info => " + sVOIPDevInfoPath
				+ "\n Logcat => " + sVOIPLogcatPath);

		File flVOIPDevInfo = new File(sVOIPLogcatPath);
		File flVOIPLogcat = new File(sVOIPLogcatPath);
		boolean isVOIPDevInfoFileExists, isVOIPLogcatFileExists;
		isVOIPDevInfoFileExists = flVOIPDevInfo.exists();
		isVOIPLogcatFileExists = flVOIPLogcat.exists();

		L.debug("VOIP File Exist? \n Dev Info => " + isVOIPDevInfoFileExists
				+ "\n Logcat => " + isVOIPLogcatFileExists);
		if (isVOIPDevInfoFileExists || isVOIPLogcatFileExists) {
			arrLstInfoResultFiles.add(sVOIPDevInfoPath);
			arrLstLogcatResultFiles.add(sVOIPLogcatPath);
		}

		if (arrLstInfoResultFiles.size() > 0) {
			// retry
			retryResultUpload(activity, sCurrTestName, arrLstInfoResultFiles,
					arrLstLogcatResultFiles, (byte) 0);
		} else {
			startTests();
		}
	}

	void retryResultUpload(AppActivity activity, String sCurrTestName,
			ArrayList<String> lstXMLFilePath,
			ArrayList<String> lstLogcatFilesPath, byte iIterationCount) {
		L.event(" Retrying file upload - " + iIterationCount);
		NotificationUtil.showNotification(activity, sCurrTestName
				+ " Retrying file upload - " + (iIterationCount + 1), false);
		RetryUploadTask retryTask = new RetryUploadTask(activity,
				sCurrTestName, lstXMLFilePath, lstLogcatFilesPath,
				iIterationCount);
		retryTask.execute();
	}

	private class RetryUploadTask extends AsyncTask<String, Integer, Message> {
		private String sTestName;
		byte iRetryIteration;
		private AppActivity activity;
		ArrayList<String> arrLstDevInfoXMLFilesPath, arrLstLogcatFilesPath;

		public RetryUploadTask(AppActivity activity, String sCurrTestName,
				ArrayList<String> lstXMLFilePath,
				ArrayList<String> lstLogcatFilesPath, byte count) {
			this.activity = activity;
			this.sTestName = sCurrTestName;
			arrLstDevInfoXMLFilesPath = lstXMLFilePath;
			arrLstLogcatFilesPath = lstLogcatFilesPath;
			iRetryIteration = count;
			L.event("RetryUploadTask()... \n DevInfoXMLFilesPath ==> "
					+ arrLstDevInfoXMLFilesPath + "\n LogcatFilesPath ==> "
					+ arrLstLogcatFilesPath + "\n RetryIteration ==> "
					+ iRetryIteration);
		}

		protected void onProgressUpdate(Integer... progress) {

		}

		@Override
		protected void onPostExecute(Message msg) {
			String sResponseCode = msg.getData().getString(StringUtils.CODE);
			String sResponseDesc = msg.getData().getString(StringUtils.DESC);
			String sNotifiMsg = "";
			if (sResponseCode.equals(ResponseCodes.SUCCESS)) {
				L.debug("Retry file upload success");
				sNotifiMsg = "Retry file upload success";
				DeviceUtil.updateTestScreen();
				retryResult(sResponseCode, sResponseDesc);
				NotificationUtil.showNotification(activity, sTestName + " - "
						+ sNotifiMsg, true);
			} else if (sResponseCode.equals(ResponseCodes.FAILURE)) {

				if (iRetryIteration == 3) {
					sNotifiMsg = "Error occured while retry uploading.";

					DeviceUtil.updateTestScreen();
					retryResult(sResponseCode, sResponseDesc);
					NotificationUtil.showNotification(activity, sTestName
							+ " - " + sNotifiMsg, true);
				} else {
					L.debug("Retry Upload Failed.... " + (iRetryIteration + 1));
					try {
						Thread.sleep(10 * 1000);
					} catch (Exception e) {

					}
					L.debug("Retrying.... " + iRetryIteration);
					retryResultUpload(activity, sTestName,
							arrLstDevInfoXMLFilesPath, arrLstLogcatFilesPath,
							iRetryIteration);
				}
			}

		}

		@Override
		protected Message doInBackground(String... files) {
			iRetryIteration++;
			Message msg = new Message();

			String sResponseCode = "", sResponseDesc = "";
			int filesCount = arrLstDevInfoXMLFilesPath.size();
			for (int file_i = 0; file_i < filesCount; file_i++) {
				String sLogcatFilePath = arrLstLogcatFilesPath.get(file_i);
				String sDevInfoXMLFilePath = arrLstDevInfoXMLFilesPath
						.get(file_i);
				Message lc_msg = doUpload(sLogcatFilePath, sDevInfoXMLFilePath);
				sResponseCode = lc_msg.getData().getString(StringUtils.CODE);
				sResponseDesc = lc_msg.getData().getString(StringUtils.DESC);
				if (sResponseCode.equals(ResponseCodes.SUCCESS)) {
					Bundle bndle = new Bundle();
					bndle.putString(StringUtils.CODE, sResponseCode);
					bndle.putString(StringUtils.DESC, sResponseDesc);
					lc_msg.setData(bndle);
					return lc_msg;
				}
			}
			Bundle bndle = new Bundle();
			bndle.putString(StringUtils.CODE, sResponseCode);
			bndle.putString(StringUtils.DESC, sResponseDesc);
			msg.setData(bndle);
			return msg;

		}

		private Message doUpload(String sLogcatFilePath,
				String sDevInfoXMLFilePath) {

			Message msg = new Message();
			Bundle bndle = new Bundle();

			String sResponseCode = "", sResponseDesc = "";

			try {
				APIManager apiClient = new APIManager(activity);

				String sIMEI = new DeviceUtil(activity).getIMEI();
				Preferences pref = new Preferences(activity);
				String sUsername = pref.getUsername();
				String sPwd = pref.getPassword();

				// ==============================================================================

				/**
				 * Changed by Bhagya on 26th Nov 2014. Checking whether
				 * </deviceinfo> tag is there or not. If it is not, appending
				 * </deviceinfo>
				 */

				String sXMLContent = FileUtil.readFile(sDevInfoXMLFilePath);
				DeviceUtil objDeviceUtil = new DeviceUtil(activity);
				String sEndInfo = objDeviceUtil.getEndInfo();
				if (sXMLContent.indexOf(sEndInfo) == -1) {
					FileUtil.writeToXMLFile(sDevInfoXMLFilePath, sEndInfo);
					L.debug("Appending </deviceinfo>" + sDevInfoXMLFilePath);
				}

				L.debug("DEVICE INFO: XML Content: " + sXMLContent);

				// ==============================================================================

				APIManager.Status status = apiClient.processUpload(sUsername,
						sPwd, sIMEI, sLogcatFilePath, sDevInfoXMLFilePath);

				if (status == APIManager.Status.ERROR) {
					sResponseCode = ResponseCodes.FAILURE;
					sResponseDesc = apiClient.getErrorMessage();
				} else {
					String sResponse = apiClient.getResponse();
					L.debug("Upload Response :: " + sResponse);
					if (sResponse != null
							&& sResponse.trim().equalsIgnoreCase(
									ResponseCodes.SUCCESS_UPLOAD)) {
						sResponseCode = ResponseCodes.SUCCESS;
						sResponseDesc = "Result uploaded!";
					} else {
						sResponseCode = ResponseCodes.FAILURE;
						sResponseDesc = sResponse;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				sResponseCode = ResponseCodes.FAILURE;
				sResponseDesc = Messages.err(e);
			}
			if (Constants.DELETE_FILES_AFTER_UPLOAD)
				if (sResponseCode.equals(ResponseCodes.SUCCESS)) {
					// Delete log files
					FileUtil.delete(sDevInfoXMLFilePath);
					FileUtil.delete(sLogcatFilePath);
				}
			bndle.putString(StringUtils.CODE, sResponseCode);
			bndle.putString(StringUtils.DESC, sResponseDesc);
			msg.setData(bndle);
			return msg;
		}
	}

	public void retryResult(String sResponseCode, String sResponseDesc) {
		L.event("In retryResult()... sResponseCode ==> " + sResponseCode
				+ ", sResponseDesc ==> " + sResponseDesc);
		startTests();
	}
}
