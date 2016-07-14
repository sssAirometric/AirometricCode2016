package com.airometric.utility.runners;

import java.io.DataOutputStream;
import java.io.File;
import java.net.URL;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.airometric.api.APIManager;
import com.airometric.api.ResponseCodes;
import com.airometric.config.Constants;
import com.airometric.config.Messages;
import com.airometric.config.StringUtils;
import com.airometric.storage.Preferences;
import com.airometric.utility.DeviceUtil;
import com.airometric.utility.FileUtil;
import com.airometric.utility.L;
import com.airometric.utility.NotificationUtil;

public class ResultUploader {
	Context context;
	Intent intent;
	String sTestname = "", sTestType = "", sDeviceInfoPath = "",
			sLogcatPath = "";
	Preferences objPref;
	private HashMap<String, String> data;
	private int size = 0;
	
	public ResultUploader(Context context, String sTestname, String sTestType,
			String sDeviceInfoPath, String sLogcatPath) {
		this.context = context;
		this.sTestname = sTestname;
		this.sTestType = sTestType;
		this.sDeviceInfoPath = sDeviceInfoPath;
		this.sLogcatPath = sLogcatPath;

		L.debug("In ResultUploader()...  " + sTestname);
		if (Constants.AFTER_UPLOAD_RESULTS)
			uploadResults();
	}

	void uploadResults() {

		UloadTestResultTask uploadTask = new UloadTestResultTask();
		uploadTask.execute();

	}

	private class UloadTestResultTask extends AsyncTask<URL, Integer, String> {

		protected void onProgressUpdate(Integer... progress) {

		}

		@Override
		protected String doInBackground(URL... urls) {
			collectAndUploadResult();
			return "Started";
		}
	}

	protected void collectAndUploadResult() {
		objPref = new Preferences(context);
		DeviceUtil dv = new DeviceUtil(context);
		String sEndInfo = dv.getEndInfo();

		String dev_info_path = sDeviceInfoPath;
		FileUtil.writeToXMLFile(sDeviceInfoPath, sEndInfo);
		L.debug("Device info end data written into " + dev_info_path);

		String logcat_path = sLogcatPath;

		// writeLogToFile(logcat_path);

		L.debug("Logcat data -> " + FileUtil.readFile(logcat_path)); // if logcat path is null then app get will crash while debug is true
		// Log.i("loaddevicedata ka size ",objPref.loadDeviceInfoData(context).isEmpty()+",");
		if(objPref.loadDeviceInfoData(context).size()!=0)
		{
			//Log.i("Resultuploader","loadDeviceInfoData me data he kuch to aya hu");
			data = objPref.loadDeviceInfoData(context);
			size = Integer.valueOf(data.get("datalength"));
		}
		data = FileUtil.readxmlfilebytagname(dev_info_path, size);
		
		//Log.i("Result uploader page objPref ki value he",data.toString());
		//Preferences pref = new Preferences(context);
		objPref.saveDeviceInfoData(data);
        
		doCollectAndUpload(dev_info_path, logcat_path);
	}

	File logFile;
	private void doCollectAndUpload(final String dev_info_path,final String sLogPath) 
	{
		// String sLogPath = writeLogToFile();
		L.debug("Device Info Path --> " + dev_info_path);
		L.debug("Logcat Path --> " + sLogPath);

		Message msg = new Message();
		Bundle bndle = new Bundle();

		String sResponseCode = "", sResponseDesc = "";

		DeviceUtil dv = new DeviceUtil(context);
		String sEndInfo = dv.getEndInfo();

		String sXMLContent = FileUtil.readFile(dev_info_path);
		if (sXMLContent.indexOf(sEndInfo) == -1) {
			FileUtil.writeToXMLFile(dev_info_path, sEndInfo);
			L.debug("Device info end data not exist and now written into "
					+ dev_info_path);
		}
		try {
			APIManager apiClient = new APIManager(context);

			String sIMEI = new DeviceUtil(context).getIMEI();
			Preferences pref = new Preferences(context);
			String sUsername = pref.getUsername();
			String sPwd = pref.getPassword();
			APIManager.Status status = apiClient.processUpload(sUsername, sPwd,
					sIMEI, sLogPath, dev_info_path);

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
				
				/**
				 * Testing purpose this code has been added by Bhagya
				 */
				
				/*
				try {
					InputStream in = new FileInputStream(sLogPath);
					OutputStream out = new FileOutputStream(FileUtil.APP_LOG_DIR + "/lks.txt");
					
			        byte[] buf = new byte[1024];
			        int len;
			        while ((len = in.read(buf)) > 0) {
			            out.write(buf, 0, len);
			        }
			        in.close();
			        out.close();
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				*/
				/**
				 * ===================================================================
				 */
				
				FileUtil.delete(dev_info_path);
				FileUtil.delete(sLogPath);
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
				objPref = new Preferences(context);
				if(objPref != null) {
					 
					 objPref.putValue(Preferences.KEY_IS_TEST_CONFIG_SET, false); // This is mandatory....
					/*
					 * objPref.putValue(Preferences.KEY_SELECTED_MARKET_PLACE_ID,
					 * ""); // Not mandatory
					 * objPref.putValue(Preferences.KEY_TEST_NAME, ""); // Not
					 * mandatory
					 */				}
			} else {
				L.debug("Error occured while uploading.");
				sNotifiMsg = "Error occured while uploading.";
			}

			DeviceUtil.updateTestScreen();
			NotificationUtil.showFinishedNotification(context, sTestType,
					sTestname, sNotifiMsg);
			resultUploaded(sResponseCode, sResponseDesc);
		}
	};

	public void resultUploaded(String code, String desc) {

	}

	private String writeLogToFile(String path) {
		String filname = "";
		try {
			String sLogcatTag = "";
			if (sTestType.equals(StringUtils.TEST_TYPE_CODE_MO))
				sLogcatTag = StringUtils.MO_LOG_TAG;
			else if (sTestType.equals(StringUtils.TEST_TYPE_CODE_MT))
				sLogcatTag = StringUtils.MT_LOG_TAG;
			else if (sTestType.equals(StringUtils.TEST_TYPE_CODE_FTP))
				sLogcatTag = StringUtils.FTP_LOG_TAG;
			else if (sTestType.equals(StringUtils.TEST_TYPE_CODE_UDP))
				sLogcatTag = StringUtils.UDP_LOG_TAG;
			else if (sTestType.equals(StringUtils.TEST_TYPE_CODE_PING))
				sLogcatTag = StringUtils.PING_LOG_TAG;
			else if (sTestType.equals(StringUtils.TEST_TYPE_CODE_BROWSER))
				sLogcatTag = StringUtils.BROWSER_LOG_TAG;
			else if (sTestType.equals(StringUtils.TEST_TYPE_CODE_VOIP))
				sLogcatTag = StringUtils.VOIP_LOG_TAG;
			String generateLogcatLogCommond = "logcat -v time -s " + sLogcatTag
					+ " > " + path + "";

			
			Process process = Runtime.getRuntime().exec("/system/bin/sh -");

			DataOutputStream os = new DataOutputStream(
					process.getOutputStream());
			os.writeBytes(generateLogcatLogCommond);
			os.close();

			filname = path;
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Thread.sleep(2 * 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filname;
	}

}
