package com.airoremote;

import java.util.Timer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.airoremote.R;
import com.airoremote.classes.TestConfig;
import com.airoremote.classes.UDPTestConfig;
import com.airoremote.config.Constants;
import com.airoremote.config.Messages;
import com.airoremote.config.StringUtils;
import com.airoremote.dialogs.AppAlert;
import com.airoremote.dialogs.TestConfigAlert;
import com.airoremote.storage.Preferences;
import com.airoremote.utility.L;
import com.airoremote.utility.Validator;

public class UDPTestInputActivity extends AppActivity implements View.OnClickListener {

	private Button btnSave, btnCancel, btnGetUploadPath;
	private EditText txtServerIP, txtServerPort, txtNoOfCycles,
			txtFileToUpload;
	private String sServerIP, sServerPort, sNoOfCycles, sFileToUpload;
	private TestConfig TestConfigObj;
	Timer timer;
	Handler handler = new Handler();

	Bundle bundle;
	private static final int PICK_FILE_TO_UPLOAD = 1;

	TextView lblUdpSetTestConfig;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// To setup the title bar
		setAppTitle(R.layout.udp_test_input);

		bundle = this.getIntent().getExtras();
		if (bundle != null) {
			if (bundle.containsKey(StringUtils.EXTRA_TEST_CONFIG))
				TestConfigObj = (TestConfig) bundle
						.getSerializable(StringUtils.EXTRA_TEST_CONFIG);
		}
		if (TestConfigObj == null)
			TestConfigObj = new TestConfig();
		initLayouts();
	}

	/**
	 * To initialize the layout and assign listeners for that fields.
	 */

	private void initLayouts() {
		L.log("Initialize Layouts...");

		txtServerIP = (EditText) findViewById(R.id.txtServerIP);
		txtServerPort = (EditText) findViewById(R.id.txtServerPort);
		txtNoOfCycles = (EditText) findViewById(R.id.txtNoOfCycles);
		txtFileToUpload = (EditText) findViewById(R.id.txtFileToUpload);

		btnGetUploadPath = (Button) findViewById(R.id.btnGetUploadPath);
		btnGetUploadPath.setOnClickListener(clickListener);

		btnSave = (Button) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(clickListener);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(clickListener);

		lblUdpSetTestConfig = (TextView) findViewById(R.id.lblUdpSetTestConfig);		
		lblUdpSetTestConfig.setMovementMethod(LinkMovementMethod.getInstance());
		lblUdpSetTestConfig.setOnClickListener(this);
		
		if(pref != null && pref.getValue(Preferences.KEY_IS_TEST_CONFIG_SET, false))
			lblUdpSetTestConfig.setText(Html.fromHtml("<u>Edit Test Config</u>"));
		else
			lblUdpSetTestConfig.setText(Html.fromHtml("<u>Set Test Config</u>"));
		
		
		if (Constants.FILL_DATA) {
			txtServerIP.setText(Constants.TEST_UDP_SERVER);
			txtServerPort.setText(Constants.TEST_UDP_SERVER_PORT);
			txtNoOfCycles.setText(Constants.TEST_UDP_CYCLES);
		}

		UDPTestConfig UDPTestConfigObj = TestConfigObj.getUDPTestConfig();
		if (Validator.isValidUDPConfig(UDPTestConfigObj)) {

			txtServerIP.setText(UDPTestConfigObj.sServerIP);
			txtServerPort.setText(UDPTestConfigObj.sServerPort);
			txtNoOfCycles.setText(UDPTestConfigObj.sTestCycles);
			txtFileToUpload.setText(UDPTestConfigObj.sFileToUpload);
		}
	}

	private Button.OnClickListener clickListener = new Button.OnClickListener() {

		@Override
		public void onClick(View view) {

			if (view == btnSave) {
				validate();
				// testUpload();
			} else if (view == btnCancel) {
				goBack();
			} else if (view == btnGetUploadPath) {

				Intent choose_intent = new Intent(activity,
						LocalFilesChooseActivity.class);
				choose_intent.putExtra(StringUtils.EXTRA_CHOOSE_TYPE,
						LocalFilesChooseActivity.CHOOSE_TYPE_FILE);
				startActivityForResult(choose_intent, PICK_FILE_TO_UPLOAD);
			}
		}

	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// debug("resultCode . " + resultCode + " :: requestCode . " +
		// requestCode);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {

			case PICK_FILE_TO_UPLOAD:
				if (data != null && data.getExtras() != null
						&& data.getExtras().containsKey("PATH")) {
					String sSelectedFile = (String) data.getExtras()
							.get("PATH");
					debug("sSelectedFile: " + sSelectedFile);
					if (sSelectedFile != null) {
						txtFileToUpload.setText(sSelectedFile);
					}
				}
				break;
			}
		} else {
			// gracefully handle failure
			// debug("Warning: activity result not ok.. " + resultCode);
		}
	}

	void validate() {

		sServerIP = txtServerIP.getText().toString();
		sServerPort = txtServerPort.getText().toString();
		sNoOfCycles = txtNoOfCycles.getText().toString();
		sFileToUpload = txtFileToUpload.getText().toString();
		String sValidCycleText = Validator.getValidCyclesText(sNoOfCycles,
				StringUtils.TEST_TYPE_CODE_UDP);

		if (Validator.isEmpty(sServerIP)) {
			toast("Please enter server url");
			return;
		} else if (Validator.isEmpty(sServerPort)) {
			toast("Please enter server port");
			return;
		} else if (sValidCycleText != null) {
			toast(sValidCycleText);
			return;
		} else if (Validator.isEmpty(sFileToUpload)) {
			toast("Please enter file to upload");
			return;
		} else {

			UDPTestConfig UDPTestConfigObj = new UDPTestConfig(sServerIP,
					sServerPort, sNoOfCycles, sFileToUpload);
			TestConfigObj.setUDPTestConfig(UDPTestConfigObj);
			if (bundle == null)
				bundle = new Bundle();
			Intent intent = new Intent(activity, TestListActivity.class);
			bundle.putSerializable(StringUtils.EXTRA_TEST_CONFIG, TestConfigObj);
			intent.putExtras(bundle);
			showIntent(intent);
		}

	}

	@Override
	public void goBack() {
		sServerIP = txtServerIP.getText().toString();
		sServerPort = txtServerPort.getText().toString();
		sNoOfCycles = txtNoOfCycles.getText().toString();
		sFileToUpload = txtFileToUpload.getText().toString();
		if (Validator.isEmpty(sServerIP) && Validator.isEmpty(sServerPort)
				&& Validator.isEmpty(sNoOfCycles)
				&& Validator.isEmpty(sFileToUpload)) {

			Intent intent = new Intent(activity, TestListActivity.class);
			bundle.putSerializable(StringUtils.EXTRA_TEST_CONFIG, TestConfigObj);
			intent.putExtras(bundle);
			showIntent(intent);
		} else {
			new AppAlert(activity, Messages.CONFIRM_DATA_LOST, true) {
				public void okClickListener() {
					Intent intent = new Intent(activity, TestListActivity.class);
					bundle.putSerializable(StringUtils.EXTRA_TEST_CONFIG,
							TestConfigObj);
					intent.putExtras(bundle);
					showIntent(intent);
				};
			};
		}
	}

	@Override
	public void onClick(View v) {
		TestConfigAlert objTestConfigAlert = new TestConfigAlert(this);
		objTestConfigAlert.show();		
	}
}