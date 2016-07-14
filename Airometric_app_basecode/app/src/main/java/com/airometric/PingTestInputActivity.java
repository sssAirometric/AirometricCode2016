package com.airometric;

import java.util.Timer;

import org.apache.commons.validator.routines.DomainValidator;
import org.apache.commons.validator.routines.InetAddressValidator;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.airometric.classes.PingTestConfig;
import com.airometric.classes.TestConfig;
import com.airometric.config.Constants;
import com.airometric.config.Messages;
import com.airometric.config.StringUtils;
import com.airometric.dialogs.AppAlert;
import com.airometric.dialogs.TestConfigAlert;
import com.airometric.storage.Preferences;
import com.airometric.utility.L;
import com.airometric.utility.Validator;

public class PingTestInputActivity extends AppActivity implements View.OnClickListener {

	private Button btnSave, btnCancel;
	private EditText txtServerIP, txtNoOfCycles;
	private String sServerIP, sNoOfCycles;
	private TestConfig TestConfigObj;
	Timer timer;
	Handler handler = new Handler();

	Bundle bundle;
	TextView lblPingSetTestConfig;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// To setup the title bar
		setAppTitle(R.layout.ping_test_input);

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
		txtNoOfCycles = (EditText) findViewById(R.id.txtNoOfCycles);

		btnSave = (Button) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(clickListener);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(clickListener);
		
		lblPingSetTestConfig = (TextView) findViewById(R.id.lblPingSetTestConfig);		
		lblPingSetTestConfig.setMovementMethod(LinkMovementMethod.getInstance());
		lblPingSetTestConfig.setOnClickListener(this);
		
		if(pref != null && pref.getValue(Preferences.KEY_IS_TEST_CONFIG_SET, false))
			lblPingSetTestConfig.setText(Html.fromHtml("<u>Edit Test Config</u>"));
		else
			lblPingSetTestConfig.setText(Html.fromHtml("<u>Set Test Config</u>"));

		
		if (Constants.FILL_DATA) {
			txtServerIP.setText(Constants.TEST_PING_SERVER);
			txtNoOfCycles.setText(Constants.TEST_UDP_CYCLES);
		}

		PingTestConfig PingTestConfigObj = TestConfigObj.getPingTestConfig();
		if (Validator.isValidPingConfig(PingTestConfigObj)) {
			txtServerIP.setText(PingTestConfigObj.sServerIP);
			txtNoOfCycles.setText(PingTestConfigObj.sCycles);
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
			}
		}

	};

	void validate() {

		sServerIP = txtServerIP.getText().toString();
		sNoOfCycles = txtNoOfCycles.getText().toString();
		String sValidCycleText = Validator.getValidCyclesText(sNoOfCycles,
				StringUtils.TEST_TYPE_CODE_PING);

		if (Validator.isEmpty(sServerIP)) {
			toast("Please enter server url");
			return;
		} else if (!DomainValidator.getInstance().isValid(sServerIP)
				&& !InetAddressValidator.getInstance().isValid(sServerIP)) {
			toast("Please enter valid server url/ip");
			return;
		} else if (sValidCycleText != null) {
			toast(sValidCycleText);
			return;
		} else {

			PingTestConfig PingTestConfigObj = new PingTestConfig(sServerIP,
					sNoOfCycles);
			TestConfigObj.setPingTestConfig(PingTestConfigObj);
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
		sNoOfCycles = txtNoOfCycles.getText().toString();
		if (Validator.isEmpty(sServerIP) && Validator.isEmpty(sNoOfCycles)) {
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