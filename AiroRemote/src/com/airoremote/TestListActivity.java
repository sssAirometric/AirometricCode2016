package com.airoremote;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.airoremote.R;
import com.airoremote.api.APIManager;
import com.airoremote.api.ResponseCodes;
import com.airoremote.classes.BrowserTestConfig;
import com.airoremote.classes.FTPTestConfig;
import com.airoremote.classes.LoginResponse;
import com.airoremote.classes.MOTestConfig;
import com.airoremote.classes.MTTestConfig;
import com.airoremote.classes.PingTestConfig;
import com.airoremote.classes.TestConfig;
import com.airoremote.classes.UDPTestConfig;
import com.airoremote.classes.VOIPTestConfig;
import com.airoremote.config.Messages;
import com.airoremote.config.StringUtils;
import com.airoremote.dialogs.AppAlert;
import com.airoremote.parser.ConfigXMLParser;
import com.airoremote.parser.Parser;
import com.airoremote.preferences.SettingsActivity;
import com.airoremote.storage.Preferences;
import com.airoremote.utility.DeviceUtil;
import com.airoremote.utility.FileUtil;
import com.airoremote.utility.L;
import com.airoremote.utility.Validator;
import com.airoremote.utility.runners.TestRunner;

import static com.airoremote.pushnotifications.CommonUtilities.SERVER_START_MESSAGE;

public class TestListActivity extends AppActivity {

	private Button btnTest;
	private Handler mHandler = new Handler();

	private CheckBox chkTestMO, chkTestMT, chkTestFTP, chkTestUDP, chkTestPing,
			chkTestBrowser, chkTestVoiceCall;
	private Button btnTestMO, btnTestMT, btnTestFTP, btnTestUDP, btnTestPing,
			btnTestBrowser, btnTestVoiceCall;

	private LinearLayout linearMOCall, linearMTCall, linearFTP, linearUDP;
	private LinearLayout linearPingTest, linearBrowserTest, linearVoipCallTest;
	private String sTestMode, sDownloadConfigPath = null;
	private TestConfig testconfig = null;
	private MOTestConfig objMOConfig = null;
	private MTTestConfig objMTConfig = null;
	private FTPTestConfig objFTPConfig = null;
	private UDPTestConfig objUDPConfig = null;
	private PingTestConfig objPingConfig = null;
	private BrowserTestConfig objBrowserConfig = null;
	private VOIPTestConfig objVOIPConfig = null;

	private Bundle bundle;
	private Thread thrdCheckout = null;
	private boolean isActive;

	ArrayList<String> arrLstSelectedTestsCodes;
	ArrayList<CheckBox> arrLstTests, arrLstSelectedTests;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// To setup the title bar
		setAppTitle(R.layout.test_list);
		sDownloadConfigPath = null;
		bundle = this.getIntent().getExtras();
		if (bundle != null) {

			if (bundle.containsKey(StringUtils.EXTRA_CONFIG_PATH))

				sDownloadConfigPath = bundle
						.getString(StringUtils.EXTRA_CONFIG_PATH);

			if (bundle.containsKey(StringUtils.EXTRA_TEST_MODE))
				sTestMode = bundle.getString(StringUtils.EXTRA_TEST_MODE);

			if (bundle.containsKey(StringUtils.EXTRA_TEST_CONFIG))
				testconfig = (TestConfig) bundle
						.getSerializable(StringUtils.EXTRA_TEST_CONFIG);

		}
		// sDownloadConfigPath = "/data/data/com.airometric/files/config.xml";
		initLayouts();

	}

	/**
	 * To initialize the layout and assign listeners for that fields.
	 */

	private void initLayouts() {

		L.log("Initialize Layouts...");

		btnTest = (Button) findViewById(R.id.btnTestAll);
		btnTest.setOnClickListener(clickListener);
		btnTest.setEnabled(false);

		chkTestMO = (CheckBox) findViewById(R.id.chkMOCall);
		btnTestMO = (Button) findViewById(R.id.btnTestMO);

		chkTestMT = (CheckBox) findViewById(R.id.chkMTCall);
		btnTestMT = (Button) findViewById(R.id.btnTestMT);

		chkTestFTP = (CheckBox) findViewById(R.id.chkFTP);
		btnTestFTP = (Button) findViewById(R.id.btnTestFTP);

		/*Ankit Jain remove these tests from the test list we also remove these ones from 
		 * back end (web code) 15-02-16 Permitted by Annie
		 * chkTestUDP = (CheckBox) findViewById(R.id.chkUDP);
		chkTestUDP.setEnabled(false);
		btnTestUDP = (Button) findViewById(R.id.btnTestUDP);

		chkTestPing = (CheckBox) findViewById(R.id.chkPing);
		btnTestPing = (Button) findViewById(R.id.btnTestPing);

		chkTestBrowser = (CheckBox) findViewById(R.id.chkBrowser);
		btnTestBrowser = (Button) findViewById(R.id.btnTestBrowser);*/

		chkTestVoiceCall = (CheckBox) findViewById(R.id.chkVoiceCall);
		btnTestVoiceCall = (Button) findViewById(R.id.btnTestVoiceCall);

		linearMOCall = (LinearLayout) findViewById(R.id.linearMOCall);
		linearMTCall = (LinearLayout) findViewById(R.id.linearMTCall);
		linearFTP = (LinearLayout) findViewById(R.id.linearFTP);
		/*linearUDP = (LinearLayout) findViewById(R.id.linearUDP);
		linearPingTest = (LinearLayout) findViewById(R.id.linearPingTest);
		linearBrowserTest = (LinearLayout) findViewById(R.id.linearBrowserTest);*/
		linearVoipCallTest = (LinearLayout) findViewById(R.id.linearVoipCallTest);

		chkTestMO.setTag(StringUtils.TEST_TYPE_CODE_MO);
		chkTestMT.setTag(StringUtils.TEST_TYPE_CODE_MT);
		chkTestFTP.setTag(StringUtils.TEST_TYPE_CODE_FTP);
		/*chkTestUDP.setTag(StringUtils.TEST_TYPE_CODE_UDP);
		chkTestPing.setTag(StringUtils.TEST_TYPE_CODE_PING);
		chkTestBrowser.setTag(StringUtils.TEST_TYPE_CODE_BROWSER);*/
		chkTestVoiceCall.setTag(StringUtils.TEST_TYPE_CODE_VOIP);

		arrLstSelectedTests = new ArrayList<CheckBox>();
		arrLstTests = new ArrayList<CheckBox>();

		arrLstTests.add(chkTestMO);
		arrLstTests.add(chkTestMT);
		arrLstTests.add(chkTestFTP);
		/*arrLstTests.add(chkTestUDP);
		arrLstTests.add(chkTestPing);
		arrLstTests.add(chkTestBrowser);*/
		arrLstTests.add(chkTestVoiceCall);

		linearMOCall.setOnClickListener(clickListener);
		btnTestMO.setOnClickListener(clickListener);
		linearMTCall.setOnClickListener(clickListener);
		btnTestMT.setOnClickListener(clickListener);
		linearFTP.setOnClickListener(clickListener);
		btnTestMO.setOnClickListener(clickListener);
		
		//linearUDP.setOnClickListener(clickListener);
		//btnTestUDP.setOnClickListener(clickListener);
		
		/*linearPingTest.setOnClickListener(clickListener);
		btnTestPing.setOnClickListener(clickListener);
		linearBrowserTest.setOnClickListener(clickListener);
		btnTestBrowser.setOnClickListener(clickListener);*/
		linearVoipCallTest.setOnClickListener(clickListener);
		btnTestVoiceCall.setOnClickListener(clickListener);

		chkTestMO.setOnCheckedChangeListener(CheckChangeLstnr);
		chkTestMT.setOnCheckedChangeListener(CheckChangeLstnr);
		chkTestFTP.setOnCheckedChangeListener(CheckChangeLstnr);
		
		//chkTestUDP.setOnCheckedChangeListener(CheckChangeLstnr);
		
		/*chkTestPing.setOnCheckedChangeListener(CheckChangeLstnr);
		chkTestBrowser.setOnCheckedChangeListener(CheckChangeLstnr);*/
		chkTestVoiceCall.setOnCheckedChangeListener(CheckChangeLstnr);

		if (!Validator.isEmpty(sDownloadConfigPath)) {
			try {
				doParseAndPopulateConfig(sDownloadConfigPath);
			} catch (Exception e) {
				e.printStackTrace();
				new AppAlert(activity, Messages.ERROR_ON_PARSING_CONFIG) {
					@Override
					public void okClickListener() {
						showActivity(TestTypeActivity.class);
					}
				};
			}
		} else if (testconfig != null)
			doPopulateConfig();
	}

	OnCheckedChangeListener CheckChangeLstnr = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			boolean isAnyCheckBoxSelected = false;

			int tst_cnt = arrLstTests.size();
			for (int tst_i = 0; tst_i < tst_cnt; tst_i++) {
				CheckBox chk = arrLstTests.get(tst_i);
				if (chk.isChecked()) {
					isAnyCheckBoxSelected = true;
					break;
				}
			}
			if (isAnyCheckBoxSelected)
				btnTest.setEnabled(true);
			else
				btnTest.setEnabled(false);
		}
	};

	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		getMenuInflater().inflate(R.menu.menu_settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.mnSettings:
			Intent i = new Intent(this, SettingsActivity.class);
			startActivityForResult(i, 1);
			break;

		default:
			break;
		}
		return true;
	}

	private void doParseAndPopulateConfig(String sConfigPath) {

		chkTestMO.setChecked(false);
		chkTestMT.setChecked(false);
		chkTestFTP.setChecked(false);
		/*chkTestUDP.setChecked(false);
		chkTestPing.setChecked(false);
		chkTestBrowser.setChecked(false);*/
		chkTestVoiceCall.setChecked(false);

		chkTestMO.setEnabled(false);
		chkTestMT.setEnabled(false);
		chkTestFTP.setEnabled(false);
		/*chkTestUDP.setEnabled(false);
		chkTestPing.setEnabled(false);
		chkTestBrowser.setEnabled(false);*/
		chkTestVoiceCall.setEnabled(false);

		linearMOCall.setEnabled(false);
		btnTestMO.setEnabled(false);
		linearMTCall.setEnabled(false);
		btnTestMO.setEnabled(false);
		linearFTP.setEnabled(false);
		btnTestMO.setEnabled(false);
		/*linearUDP.setEnabled(false);
		btnTestMO.setEnabled(false);
		linearPingTest.setEnabled(false);
		btnTestMO.setEnabled(false);
		linearBrowserTest.setEnabled(false);
		btnTestMO.setEnabled(false);*/
		linearVoipCallTest.setEnabled(false);
		btnTestMO.setEnabled(false);

		boolean isAnyOfTestEnabled = false;
		String sConfigContent = FileUtil.readFile(sConfigPath);

		ConfigXMLParser parser = new ConfigXMLParser(pref);
		debug("sConfigContent ==> " + sConfigContent);
		testconfig = parser.parseXML(sConfigContent);

		objMOConfig = testconfig.getMOTestConfig();
		objMTConfig = testconfig.getMTTestConfig();
		objFTPConfig = testconfig.getFTPTestConfig();
		objUDPConfig = testconfig.getUDPTestConfig();
		objPingConfig = testconfig.getPingTestConfig();
		objBrowserConfig = testconfig.getBrowserTestConfig();
		objVOIPConfig = testconfig.getVOIPTestConfig();

		if (objMOConfig != null) {
			chkTestMO.setChecked(true);
			chkTestMO.setEnabled(false);
			isAnyOfTestEnabled = true;
		}

		if (objMTConfig != null) {
			chkTestMT.setChecked(true);
			isAnyOfTestEnabled = true;
		}

		if (objFTPConfig != null) {
			chkTestFTP.setChecked(true);
			isAnyOfTestEnabled = true;
		}

		if (objUDPConfig != null) {
			chkTestUDP.setChecked(true);
			isAnyOfTestEnabled = true;
		}

		if (objPingConfig != null) {
			chkTestPing.setChecked(true);
			isAnyOfTestEnabled = true;
		}
		if (objBrowserConfig != null) {
			chkTestBrowser.setChecked(true);
			isAnyOfTestEnabled = true;
		}
		if (objVOIPConfig != null) {
			chkTestVoiceCall.setChecked(true);
			isAnyOfTestEnabled = true;
		}

		if (isAnyOfTestEnabled)
			btnTest.setVisibility(View.VISIBLE);
		
		if(pref.getisTestAutoStart().equals(SERVER_START_MESSAGE))doCheckout();
		
	}

	private void doPopulateConfig() {

		chkTestMO.setChecked(false);
		chkTestMT.setChecked(false);
		chkTestFTP.setChecked(false);
		/*chkTestUDP.setChecked(false);
		chkTestPing.setChecked(false);
		chkTestBrowser.setChecked(false);*/
		chkTestVoiceCall.setChecked(false);

		chkTestMO.setEnabled(true);
		chkTestMT.setEnabled(true);
		chkTestFTP.setEnabled(true);
		/*chkTestUDP.setEnabled(true);
		chkTestPing.setEnabled(true);
		chkTestBrowser.setEnabled(true);*/
		chkTestVoiceCall.setEnabled(true);

		boolean isAnyOfTestEnabled = false;
		objMOConfig = testconfig.getMOTestConfig();
		objMTConfig = testconfig.getMTTestConfig();
		objFTPConfig = testconfig.getFTPTestConfig();
		objUDPConfig = testconfig.getUDPTestConfig();
		objPingConfig = testconfig.getPingTestConfig();
		objBrowserConfig = testconfig.getBrowserTestConfig();
		objVOIPConfig = testconfig.getVOIPTestConfig();

		if (objMOConfig != null) {
			chkTestMO.setChecked(true);
			isAnyOfTestEnabled = true;
		}

		if (objMTConfig != null) {
			chkTestMT.setChecked(true);
			isAnyOfTestEnabled = true;
		}

		if (objFTPConfig != null) {
			chkTestFTP.setChecked(true);
			isAnyOfTestEnabled = true;
		}
		if (objUDPConfig != null) {
			chkTestUDP.setChecked(true);
			isAnyOfTestEnabled = true;
		}
		if (objPingConfig != null) {
			chkTestPing.setChecked(true);
			isAnyOfTestEnabled = true;
		}

		if (objBrowserConfig != null) {
			chkTestBrowser.setChecked(true);
			isAnyOfTestEnabled = true;
		}
		if (objVOIPConfig != null) {
			chkTestVoiceCall.setChecked(true);
			isAnyOfTestEnabled = true;
		}

		if (isAnyOfTestEnabled)
			btnTest.setVisibility(View.VISIBLE);
	}

	private View.OnClickListener clickListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			if (view == linearMOCall || view == btnTestMO) {
				Intent intent = new Intent(activity, MOTestInputActivity.class);
				if (bundle == null)
					bundle = new Bundle();
				bundle.putSerializable(StringUtils.EXTRA_TEST_CONFIG,
						testconfig);
				intent.putExtras(bundle);
				showIntent(intent);
			} else if (view == linearMTCall || view == btnTestMT) {
				Intent intent = new Intent(activity, MTTestInputActivity.class);
				if (bundle == null)
					bundle = new Bundle();
				bundle.putSerializable(StringUtils.EXTRA_TEST_CONFIG,
						testconfig);
				intent.putExtras(bundle);
				showIntent(intent);
			} else if (view == linearFTP || view == btnTestFTP) {
				Intent intent = new Intent(activity, FTPTestInputActivity.class);
				if (bundle == null)
					bundle = new Bundle();
				bundle.putSerializable(StringUtils.EXTRA_TEST_CONFIG,
						testconfig);
				intent.putExtras(bundle);
				showIntent(intent);
			} else if (view == linearUDP || view == btnTestUDP) {
				Intent intent = new Intent(activity, UDPTestInputActivity.class);
				if (bundle == null)
					bundle = new Bundle();
				bundle.putSerializable(StringUtils.EXTRA_TEST_CONFIG,
						testconfig);
				intent.putExtras(bundle);
				showIntent(intent);
			} else if (view == linearPingTest || view == btnTestPing) {
				Intent intent = new Intent(activity,
						PingTestInputActivity.class);
				if (bundle == null)
					bundle = new Bundle();
				bundle.putSerializable(StringUtils.EXTRA_TEST_CONFIG,
						testconfig);
				intent.putExtras(bundle);
				showIntent(intent);
			} else if (view == linearBrowserTest || view == btnTestBrowser) {
				Intent intent = new Intent(activity,
						BrowserTestInputActivity.class);
				if (bundle == null)
					bundle = new Bundle();
				bundle.putSerializable(StringUtils.EXTRA_TEST_CONFIG,
						testconfig);
				intent.putExtras(bundle);
				showIntent(intent);
			} else if (view == linearVoipCallTest || view == btnTestVoiceCall) {
				Intent intent = new Intent(activity,
						VOIPTestInputActivity.class);
				if (bundle == null)
					bundle = new Bundle();
				bundle.putSerializable(StringUtils.EXTRA_TEST_CONFIG,
						testconfig);
				intent.putExtras(bundle);
				showIntent(intent);
			} else if (view == btnTest) {
				// Check whether user is active / inactive

				doCheckout();

				/*
				 * if (isActive) validate(); else { new AppAlert(activity,
				 * Messages.ACCESS_DENIED){
				 * 
				 * @Override public void okClickListener() { // re-direct to
				 * login screen showActivity(LoginActivity.class,
				 * DIRECTION_FRONT); } }; }
				 */

			}
		}
	};

	protected void validate() {
		debug("validating...");
		boolean isAnyTestSelected = false;
		arrLstSelectedTests = new ArrayList<CheckBox>();
		arrLstSelectedTestsCodes = new ArrayList<String>();
		int tst_cnt = arrLstTests.size();
		for (int tst_i = 0; tst_i < tst_cnt; tst_i++) {
			CheckBox chk = arrLstTests.get(tst_i);
			if (chk.isChecked()) {
				arrLstSelectedTests.add(chk);
				arrLstSelectedTestsCodes.add(chk.getTag().toString());
			}
		}
		event("validate()::arrLstSelectedTestsCodes - "
				+ arrLstSelectedTestsCodes);

		L.debug("validate()::arrLstSelectedTestsCodes - "
				+ arrLstSelectedTestsCodes);

		if (!arrLstSelectedTests.isEmpty())
			isAnyTestSelected = true;
		if (!isAnyTestSelected) {
			alert(Messages.SELECT_ATLST_ONE_TEST);
			return;
		} else {

			if (arrLstSelectedTestsCodes
					.contains(StringUtils.TEST_TYPE_CODE_MO)
					&& !Validator.isValidMOConfig(objMOConfig)) {
				alert(Messages.MO_TEST_NOT_CONFIGURED_PRP);
				return;
			} else if (arrLstSelectedTestsCodes
					.contains(StringUtils.TEST_TYPE_CODE_MT)
					&& !Validator.isValidMTConfig(objMTConfig)) {
				alert(Messages.MT_TEST_NOT_CONFIGURED_PRP);
				return;
			} else if (arrLstSelectedTestsCodes
					.contains(StringUtils.TEST_TYPE_CODE_FTP)
					&& !Validator.isValidFTPConfig(objFTPConfig)) {
				alert(Messages.FTP_TEST_NOT_CONFIGURED_PRP);
				return;
			} else if (arrLstSelectedTestsCodes
					.contains(StringUtils.TEST_TYPE_CODE_UDP)
					&& !Validator.isValidUDPConfig(objUDPConfig)) {
				alert(Messages.UDP_TEST_NOT_CONFIGURED_PRP);
				return;
			} else if (arrLstSelectedTestsCodes
					.contains(StringUtils.TEST_TYPE_CODE_PING)
					&& !Validator.isValidPingConfig(objPingConfig)) {
				alert(Messages.getInvalidConfigText("Ping"));
				return;
			} else if (arrLstSelectedTestsCodes
					.contains(StringUtils.TEST_TYPE_CODE_BROWSER)
					&& !Validator.isValidBrowserConfig(objBrowserConfig)) {
				alert(Messages.getInvalidConfigText("Browser"));
				return;
			} else if (arrLstSelectedTestsCodes
					.contains(StringUtils.TEST_TYPE_CODE_VOIP)
					&& !Validator.isValidVOIPConfig(objVOIPConfig)) {
				alert(Messages.getInvalidConfigText("VOIP"));
				return;
			} else {

				/*
				 * TestConfigAlert objTestConfigAlert = new
				 * TestConfigAlert(this); objTestConfigAlert.show();
				 */

				L.debug("validate(): 1111111111111");

				if (pref.getValue(Preferences.KEY_IS_TEST_CONFIG_SET, false))
					startTestRunner();
				else {

					L.debug("validate(): 22222222222222222");

					LayoutInflater li = LayoutInflater.from(activity);
					View promptsView = li.inflate(R.layout.input_dialog, null);
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							activity);
					alertDialogBuilder.setView(promptsView);

					final EditText userInput = (EditText) promptsView
							.findViewById(R.id.txtTestName);

					final Spinner marketplaceSpinner = (Spinner) promptsView
							.findViewById(R.id.spinner_marketplace);
					if (StringUtils.MARKET_PLACES_MAP == null) {
						Preferences preferences = new Preferences(activity);
						StringUtils.MARKET_PLACES_MAP = new LinkedHashMap<String, String>(
								preferences.loadMarketplaces());
					}
					ArrayAdapter<String> marketplaceAdapter = new ArrayAdapter<String>(
							this,
							android.R.layout.simple_spinner_item,
							StringUtils
									.getStringArray(StringUtils.MARKET_PLACES_MAP
											.keySet()));
					marketplaceAdapter
							.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					marketplaceSpinner.setAdapter(marketplaceAdapter);

					alertDialogBuilder
							.setCancelable(false)
							.setPositiveButton("Save",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											String str = userInput.getText()
													.toString();
											L.log(marketplaceSpinner
													.getSelectedItem()
													.toString());
											L.log(StringUtils.MARKET_PLACES_MAP
													.get(marketplaceSpinner
															.getSelectedItem()
															.toString()));
											if (Validator.isEmpty(str)) {
												toast(Messages.ENTER_TEST_NAME);
												return;
											}

											pref.putValue(
													Preferences.KEY_TEST_NAME,
													str);
											pref.putValue(
													Preferences.KEY_SELECTED_MARKET_PLACE_ID,
													StringUtils.MARKET_PLACES_MAP
															.get(marketplaceSpinner
																	.getSelectedItem()
																	.toString()));
											pref.putValue(
													Preferences.KEY_IS_TEST_CONFIG_SET,
													true);

											testconfig.setTestName(str);
											testconfig
													.setMarketId(StringUtils.MARKET_PLACES_MAP
															.get(marketplaceSpinner
																	.getSelectedItem()
																	.toString()));
											startTestRunner();
										}
									})
							.setNegativeButton("Cancel",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
										}
									});

					// create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();

					// show it
					alertDialog.show();
				}
				// toast("Test name and market place is not set!!");

				// Start Test

				/*
				 * LayoutInflater li = LayoutInflater.from(activity); View
				 * promptsView = li.inflate(R.layout.input_dialog, null);
				 * AlertDialog.Builder alertDialogBuilder = new
				 * AlertDialog.Builder( activity);
				 * alertDialogBuilder.setView(promptsView);
				 * 
				 * final EditText userInput = (EditText) promptsView
				 * .findViewById(R.id.txtTestName);
				 * 
				 * final Spinner marketplaceSpinner = (Spinner)
				 * promptsView.findViewById(R.id.spinner_marketplace); if
				 * (StringUtils.MARKET_PLACES_MAP == null) { Preferences
				 * preferences = new Preferences(activity);
				 * StringUtils.MARKET_PLACES_MAP = new LinkedHashMap<String,
				 * String>(preferences.loadMarketplaces()); }
				 * ArrayAdapter<String> marketplaceAdapter = new
				 * ArrayAdapter<String> (this,
				 * android.R.layout.simple_spinner_item,
				 * StringUtils.getStringArray
				 * (StringUtils.MARKET_PLACES_MAP.keySet()));
				 * marketplaceAdapter.setDropDownViewResource(android.R.layout.
				 * simple_spinner_dropdown_item);
				 * marketplaceSpinner.setAdapter(marketplaceAdapter);
				 * 
				 * 
				 * alertDialogBuilder .setCancelable(false)
				 * .setPositiveButton("Save", new
				 * DialogInterface.OnClickListener() { public void
				 * onClick(DialogInterface dialog, int id) { String str =
				 * userInput.getText() .toString();
				 * L.log(marketplaceSpinner.getSelectedItem().toString());
				 * L.log(StringUtils.MARKET_PLACES_MAP.get(marketplaceSpinner.
				 * getSelectedItem().toString())); if (Validator.isEmpty(str)) {
				 * toast(Messages.ENTER_TEST_NAME); return; }
				 * testconfig.setTestName(str);
				 * testconfig.setMarketId(StringUtils
				 * .MARKET_PLACES_MAP.get(marketplaceSpinner
				 * .getSelectedItem().toString())); startTestRunner(); } })
				 * .setNegativeButton("Cancel", new
				 * DialogInterface.OnClickListener() { public void
				 * onClick(DialogInterface dialog, int id) { dialog.cancel(); }
				 * });
				 * 
				 * // create alert dialog AlertDialog alertDialog =
				 * alertDialogBuilder.create();
				 * 
				 * // show it alertDialog.show();
				 */
			}
		}
	}

	protected void startTestRunner() {
		event("startTestRunner()");
		TestRunner testRunner = new TestRunner(activity, testconfig,
				arrLstSelectedTestsCodes);
		testRunner.startTests(); // Test starting here
	}

	@Override
	public void goBack() {
		event("goBack()");

		// if testConfig has set, need to clear the flag.

		if (pref.getValue(Preferences.KEY_IS_TEST_CONFIG_SET, false)) {
			pref.putValue(Preferences.KEY_IS_TEST_CONFIG_SET, false);
			pref.putValue(Preferences.KEY_TEST_NAME, "");
			pref.putValue(Preferences.KEY_SELECTED_MARKET_PLACE_ID, "");
			pref.putValue(Preferences.KEY_SELECTED_MARKET_PALCE, "");
		}
		showActivity(TestTypeActivity.class);
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
				progressHandler.sendMessage(msg);
			}
		};

		thrdCheckout.start();

	}

	private Handler progressHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			try {
				thrdCheckout.interrupt();
			} catch (Exception e) {
			}

			// ###################

			String sResponseCode = msg.getData().getString(StringUtils.CODE);
			String sResponseDesc = msg.getData().getString(StringUtils.DESC);
			LoginResponse response = (LoginResponse) msg.getData()
					.getSerializable(StringUtils.RESP_LOGIN);
			L.log("Handler: " + sResponseCode);
			if (sResponseCode.equals(ResponseCodes.AUTH_FAILED)) {
			} else if (sResponseCode.equals(ResponseCodes.ACCESS_DENIED)) {
				new AppAlert(activity, Messages.ACCESS_DENIED) {
					@Override
					public void okClickListener() {
						pref.setLoggedInStatus(false);
						showActivity(LoginActivity.class, DIRECTION_FRONT);
					}
				};
			} else if (sResponseCode.equals(StringUtils.ERROR_CODE)) {
				alert(sResponseDesc);
			}
			else {
				validate();
			}
		}
	};

}