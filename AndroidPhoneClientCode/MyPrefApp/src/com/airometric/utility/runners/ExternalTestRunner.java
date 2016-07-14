package com.airometric.utility.runners;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.airometric.Airometric;
import com.airometric.AppActivity;
import com.airometric.GoogleMapsActivity;
import com.airometric.LoginActivity;
import com.airometric.TestTypeActivity;

import com.airometric.api.APIManager;
import com.airometric.api.ResponseCodes;
import com.airometric.classes.LoginResponse;
import com.airometric.classes.TestConfig;
import com.airometric.config.Constants;
import com.airometric.config.Messages;
import com.airometric.config.StringUtils;
import com.airometric.dialogs.AppAlert;
import com.airometric.parser.Parser;
import com.airometric.storage.Preferences;
import com.airometric.storage.SettingsStore;
import com.airometric.utility.DeviceUtil;
import com.airometric.utility.FileUtil;
import com.airometric.utility.L;
import com.airometric.utility.NotificationUtil;
import com.airometric.utility.TimeUtil;

public class ExternalTestRunner extends AppActivity {

	AppActivity activity;
	Preferences pref;
	SettingsStore settings;
	TestConfig Testconfig;
	private HashMap<String, String> data;
	private Integer size;

	public ExternalTestRunner(AppActivity context, TestConfig testconfig)
	{
		this.activity = context;
		pref = new Preferences(context);
		this.Testconfig = testconfig;
		settings = new SettingsStore(context);
	}
	
	public void startTest() {
		String sTestName = Testconfig.getTestName();

		pref.setExternalTestRunningState(true);

		DeviceUtil dv = new DeviceUtil(activity);
		String sDeviceInfoXML = dv.getDeviceInfo("externaltest", sTestName,
				pref.getUsername(), pref.getValue(Preferences.KEY_SELECTED_MARKET_PLACE_ID, ""));
		L.debug(sDeviceInfoXML);

		String sCurrTime = TimeUtil.getCurrentTimeFilename();

		FileUtil.CURRENT_EXT_TESTTIME = sCurrTime;
		String path = FileUtil.EXT_LOG_DIR + "deviceinfo" + "_" + sTestName
				+ "_" + sCurrTime + ".xml";
		File fle = new File(path);
		if (fle.exists()) {
			fle.delete();
		}
		String dev_info_path = FileUtil.writeToXMLFile(path, sDeviceInfoXML);
		

		pref.putValue(Preferences.KEY_CURRENT_EXT_DEV_INFO_PATH, dev_info_path);

		pref.setExternalTestName(sTestName);
		try {
			Airometric app = (Airometric) activity.getApplication();
			app.startListeners(dev_info_path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		NotificationUtil.cancelNotification(activity,
				StringUtils.TEST_TYPE_CODE_EXTERNAL);
		NotificationUtil.showRunningNotification(activity,
				StringUtils.TEST_TYPE_CODE_EXTERNAL);

		//activity.showActivity(TestTypeActivity.class);
	}

	protected void stopTest() {

		pref.setExternalTestRunningState(false);
		uploadResults();
	}

	void uploadResults() {
		new AppAlert(activity, Messages.TEST_STOPPED) {
			@Override
			public void okClickListener() {
				UloadTestResultTask uploadTask = new UloadTestResultTask();
				uploadTask.execute();
				showActivity(TestTypeActivity.class);
			}
		};

	}

	private class UloadTestResultTask extends AsyncTask<URL, Integer, String> {

		protected void onProgressUpdate(Integer... progress) {

		}

		protected void onPostExecute(Long result) {

		}

		@Override
		protected String doInBackground(URL... urls) {
			doUploadResult();
			return "Started";
		}
	}

	protected void doUploadResult() {
		DeviceUtil dv = new DeviceUtil(activity);
		String sEndInfo = dv.getEndInfo();

		Preferences pref = new Preferences(activity);
		String dev_info_path = FileUtil.writeToXMLFile(
				pref.getValue(Preferences.KEY_CURRENT_EXT_DEV_INFO_PATH, ""),
				sEndInfo);
		
		L.debug("Device info end data written into " + dev_info_path);
		if(pref.loadDeviceInfoData(activity).size()!=0)
		{
			Log.i("External par he","doUploadResult me data he kuch to aya hu");
			data = pref.loadDeviceInfoData(activity);
			size = Integer.valueOf(data.get("datalength"));
		}
		data = FileUtil.readxmlfilebytagname(dev_info_path, size);
		
		//Log.i("Result uploader page objPref ki value he",data.toString());
		//Preferences pref = new Preferences(context);
		pref.saveDeviceInfoData(data);
        
		doUpload(dev_info_path);
	}

	private void doUpload(final String dev_info_path) {

		L.debug("Device Info Path --> " + dev_info_path);

		Message msg = new Message();
		Bundle bndle = new Bundle();

		String sResponseCode = "", sResponseDesc = "";

		DeviceUtil dv = new DeviceUtil(activity);
		String sEndInfo = dv.getEndInfo();

		String sXMLContent = FileUtil.readFile(dev_info_path);
		if (sXMLContent.indexOf(sEndInfo) == -1) {
			FileUtil.writeToXMLFile(dev_info_path, sEndInfo);
			L.debug("Device info end data not exist and now written into "
					+ dev_info_path);
		}
		try {
			APIManager apiClient = new APIManager(activity);

			APIManager.Status status = apiClient
					.processUploadSingle(dev_info_path);

			if (status == APIManager.Status.ERROR) {
				sResponseCode = StringUtils.ERROR_CODE;
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
			sResponseCode = StringUtils.ERROR_CODE;
			sResponseDesc = Messages.err(e);
		}
		if (Constants.DELETE_FILES_AFTER_UPLOAD)
			if (sResponseCode.equals(ResponseCodes.SUCCESS)) {
				// Delete log files
				FileUtil.delete(dev_info_path);
			}
		bndle.putString(StringUtils.CODE, sResponseCode);
		bndle.putString(StringUtils.DESC, sResponseDesc);
		msg.setData(bndle);
		progressHandler.sendMessage(msg);
	}

	/**
	 * Handling the message while progressing
	 */

	private Handler progressHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			// ###################
			String sNotifiMsg = "";
			String sResponseCode = msg.getData().getString(StringUtils.CODE);
			String sResponseDesc = msg.getData().getString(StringUtils.DESC);

			if (sResponseCode.equals(ResponseCodes.SUCCESS)) {
				L.debug("File upload success");
				sNotifiMsg = "Success";
			} else if (sResponseCode.equals(ResponseCodes.FAILURE)) {
				L.debug("Error occured while uploading.");
				sNotifiMsg = "Error occured while uploading.";
			} else {
				sNotifiMsg = sResponseDesc;
				L.debug(sResponseDesc);
			}
			DeviceUtil.updateTestScreen();
			NotificationUtil.showFinishedNotification(activity,
					StringUtils.TEST_TYPE_CODE_EXTERNAL,
					pref.getExternalTestName(), sNotifiMsg);
			 showActivity(GoogleMapsActivity.class);
		}
	};
	private Thread thrdCheckout;

	@Override
	public void goBack() {
		showActivity(TestTypeActivity.class);
	}

	public void doCheckout() {

		// showLoading("Authenticating user...");

		thrdCheckout = new Thread() {
			private boolean isActive;

			@Override
			public void run() {

				Message msg = new Message();
				Bundle bndle = new Bundle();
				LoginResponse response = null;

				String sResponseCode = "", sResponseDesc = "";

				try {
					APIManager apiClient = new APIManager(activity);

					Preferences pref = new Preferences(activity);

					debug("User Info:>>>> NAME: >>>>" + pref.getUsername()
							+ "  PASSWORD: >>>>" + pref.getPassword()
							+ "  IMEI: >>>>"
							+ new DeviceUtil(activity).getIMEI());

					APIManager.Status status = apiClient.processLogin(
							pref.getUsername(), pref.getPassword(),
							new DeviceUtil(activity).getIMEI());
					if (status == APIManager.Status.ERROR) {
						sResponseCode = StringUtils.ERROR_CODE;
						sResponseDesc = apiClient.getErrorMessage();
					} else {
						String sResponse = apiClient.getResponse();
						debug("Login Response :: " + sResponse);
						response = Parser.parseLoginResponse(sResponse,
								activity);

						if (response.getStatus().equals(
								ResponseCodes.ACCESS_DENIED))
							isActive = false;

						if (response.getStatus().trim()
								.equalsIgnoreCase(ResponseCodes.SUCCESS)) {
							sResponseCode = ResponseCodes.SUCCESS;
							sResponseDesc = sResponse;
						} else {
							sResponseCode = response.getStatus();
							sResponseDesc = sResponse;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					sResponseCode = StringUtils.ERROR_CODE;
					sResponseDesc = Messages.err(e);
				}

				bndle.putString(StringUtils.CODE, sResponseCode);
				bndle.putString(StringUtils.DESC, sResponseDesc);
				bndle.putSerializable(StringUtils.RESP_LOGIN, response);
				msg.setData(bndle);
				progressHandler1.sendMessage(msg);
			}
		};

		thrdCheckout.start();

	}

	private Handler progressHandler1 = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			try {
				thrdCheckout.interrupt();
			} catch (Exception e) {
			}

			// ###################

			String sResponseCode = msg.getData().getString(StringUtils.CODE);
			String sResponseDesc = msg.getData().getString(StringUtils.DESC);
			L.log("Handler: " + sResponseCode);

			if (sResponseCode.equals(ResponseCodes.AUTH_FAILED)) {
			} else if (sResponseCode.equals(ResponseCodes.ACCESS_DENIED)) {
				//alert(Messages.ACCESS_DENIED);
				new AppAlert(activity, Messages.ACCESS_DENIED) {
					@Override
					public void okClickListener() {
						pref.setLoggedInStatus(false);
						showActivity(LoginActivity.class, DIRECTION_FRONT);
					}
				};
			} else if (sResponseCode.equals(StringUtils.ERROR_CODE)) {
				alert(sResponseDesc);
			} else {
				startTest();
			}
		}
	};
	

}
