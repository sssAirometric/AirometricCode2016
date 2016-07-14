package com.airometric;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.airometric.utility.DeviceUtil;
import com.airometric.utility.FileUtil;
import com.airometric.utility.L;
import com.airometric.utility.NotificationUtil;
import com.airometric.utility.TimeUtil;
import com.airometric.utility.Validator;

public class ExternalTestActivity extends AppActivity {

	private Button btnStartTest, btnStopTest;
	Context context = null;
	private TextView lblStatus;
	File logFile;
	boolean isTestRunning = false, isActive = true;
	String sTestname = "";
	EditText txtTestName;
	private Spinner marketplaceSpinner;
	private String sTestMode = "";
	private Thread thrdCheckout = null;
	private HashMap<String, String> data;
	private int size = 0;
	private TestConfig TestConfigObj;

	// private Preferences pref;

	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// To setup the title bar
		setAppTitle(R.layout.external_test);
		context = getApplication();

		Bundle bndle = this.getIntent().getExtras();
		if (bndle != null) {
			if (bndle.containsKey(StringUtils.TEST_MODE_CONFIG))
				//sTestMode = bndle.getString(StringUtils.EXTRA_TEST_MODE);
			TestConfigObj = (TestConfig) bndle
					.getSerializable(StringUtils.TEST_MODE_CONFIG);
			L.debug("TEST MODE :>>>>> " + sTestMode);
		}
		initLayouts();
	}

	/**
	 * To initialize the layout and assign listeners for that fields.
	 */

	private void initLayouts() {
		L.log("Initialize Layouts...");

		//isTestRunning = pref.isExternalTestRunning();
		Log.i("iski value kya he???",pref.isExternalTestRunning()+"");
		//txtTestName = (EditText) findViewById(R.id.txtTestName);
		// if(pref != null && pref.getValue(Preferences.KEY_TEST_NAME, "")!= "")

	/*	if (sTestMode.equals("MODE_MANUAL")) {
			txtTestName.setText(pref.getValue(Preferences.KEY_TEST_NAME, ""));
			txtTestName.setSelection(txtTestName.getText().length());
		}*/

		if(!pref.isExternalTestRunning())
			{
			doCheckout();
			}
		/*btnStartTest = (Button) findViewById(R.id.btnStartTest);

		btnStartTest.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

				String sTestName = txtTestName.getText().toString();
				if (Validator.isEmpty(sTestName)) {
					alert(Messages.ENTER_TEST_NAME);
					return;
				}
				doCheckout();

				// Check whether user is active / inactive

				
				 * APIManager apiClient = new APIManager(activity); isActive =
				 * apiClient.doCheckout(); /*if (isActive) startTest(); else {
				 * new AppAlert(getApplicationContext(),
				 * Messages.MSG_INVALID_USER) {
				 * 
				 * @Override public void okClickListener() { // re-direct to
				 * login screen showActivity(LoginActivity.class,
				 * DIRECTION_FRONT); } }; }
				 
			}
		});*/
		btnStopTest = (Button) findViewById(R.id.btnStopTest);
		btnStopTest.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				stopTest();
			}
		});

		//lblStatus = (TextView) findViewById(R.id.lblTestStatus);

		/*marketplaceSpinner = (Spinner) findViewById(R.id.spinner_marketplace);
		if (StringUtils.MARKET_PLACES_MAP == null) {
			Preferences preferences = new Preferences(activity);
			StringUtils.MARKET_PLACES_MAP = new LinkedHashMap<String, String>(
					preferences.loadMarketplaces());
		}
		ArrayAdapter<String> marketplaceAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item,
				StringUtils.getStringArray(StringUtils.MARKET_PLACES_MAP
						.keySet()));
		marketplaceAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		marketplaceSpinner.setAdapter(marketplaceAdapter);
*/
		/*if (isTestRunning) {
			// txtTestName.setVisibility(View.INVISIBLE);
			txtTestName.setEnabled(false);
			marketplaceSpinner.setEnabled(false);
			lblStatus.setText("Test Running");
			btnStartTest.setEnabled(false);
			btnStopTest.setEnabled(true);
		} else {
			lblStatus.setText("");
			btnStartTest.setEnabled(true);
			btnStopTest.setEnabled(false);

			txtTestName.setEnabled(true);
			marketplaceSpinner.setEnabled(true);
		}*/
	}

	protected void startTest() {
		String sTestName = TestConfigObj.getTestName();

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
		debug("Device info initial data written into " + dev_info_path);

		pref.putValue(Preferences.KEY_CURRENT_EXT_DEV_INFO_PATH, dev_info_path);

		pref.setExternalTestName(sTestName);
		try {
			Airometric app = (Airometric) getApplication();
			app.startListeners(dev_info_path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		NotificationUtil.cancelNotification(activity,
				StringUtils.TEST_TYPE_CODE_EXTERNAL);
		NotificationUtil.showRunningNotification(context,
				StringUtils.TEST_TYPE_CODE_EXTERNAL);

		new AppAlert(activity, Messages.EXT_TEST_STARTED) {
			@Override
			public void okClickListener() {
				initLayouts();
				
			}
		};
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
				//showActivity(GoogleMapsActivity.class);
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
		DeviceUtil dv = new DeviceUtil(context);
		String sEndInfo = dv.getEndInfo();

		Preferences pref = new Preferences(context);
		String dev_info_path = FileUtil.writeToXMLFile(
				pref.getValue(Preferences.KEY_CURRENT_EXT_DEV_INFO_PATH, ""),
				sEndInfo);
		
		L.debug("Device info end data written into " + dev_info_path);
		
		doUpload(dev_info_path);
	}

	private void doUpload(final String dev_info_path) {

		L.debug("Device Info Path --> " + dev_info_path);

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
				pref  = new Preferences(context);
				//here we check the data and stores it into the sharedperference 
				if(pref.loadDeviceInfoData(context).size()!=0)
				{
					Log.i("External par he","doUploadResult me data he kuch to aya hu");
					data = pref.loadDeviceInfoData(context);
					size = Integer.valueOf(data.get("datalength"));
				}
				
				data = FileUtil.readxmlfilebytagname(dev_info_path, size);
				pref.saveDeviceInfoData(data);
				//increase test counter 
				pref.saveTestCounterValue(pref.getTestCounterValue()+1);
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
			NotificationUtil.showFinishedNotification(context,
					StringUtils.TEST_TYPE_CODE_EXTERNAL,
					pref.getExternalTestName(), sNotifiMsg);
			 showActivity(GoogleMapsActivity.class);
		}
	};

	@Override
	public void goBack() {
		showActivity(TestListActivity.class);
	}

	public void doCheckout() {

		// showLoading("Authenticating user...");

		thrdCheckout = new Thread() {
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