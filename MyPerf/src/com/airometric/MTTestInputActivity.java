package com.airometric;

import org.w3c.dom.Text;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.airometric.classes.MTTestConfig;
import com.airometric.classes.TestConfig;
import com.airometric.config.Constants;
import com.airometric.config.Messages;
import com.airometric.config.StringUtils;
import com.airometric.dialogs.AppAlert;
import com.airometric.dialogs.TestConfigAlert;
import com.airometric.storage.Preferences;
import com.airometric.utility.L;
import com.airometric.utility.Validator;

public class MTTestInputActivity extends AppActivity implements android.view.View.OnClickListener {

	private Button btnStart, btnCancel;
	private EditText txtCallDuration, txtTestDuration;
	private String sCallDuration, sTestDuration;
	private TestConfig TestConfigObj;

	long call_duration_secs, call_duration_millis, pause_time_secs,
			test_duration_secs, completed_duration_secs = 0, pass = 0;
	Bundle bundle;

	TextView lblMtSetTestConfig;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// To setup the title bar
		setAppTitle(R.layout.mt_test_input);

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

		txtCallDuration = (EditText) findViewById(R.id.txtCallDuration);
		txtTestDuration = (EditText) findViewById(R.id.txtTestDuration);

		btnStart = (Button) findViewById(R.id.btnStart);
		btnStart.setOnClickListener(clickListener);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(clickListener);
		
		lblMtSetTestConfig = (TextView) findViewById(R.id.lblMtSetTestConfig);		
		lblMtSetTestConfig.setMovementMethod(LinkMovementMethod.getInstance());
		lblMtSetTestConfig.setOnClickListener(this);
		
		if(pref != null && pref.getValue(Preferences.KEY_IS_TEST_CONFIG_SET, false))
			lblMtSetTestConfig.setText(Html.fromHtml("<u>Edit Test Config</u>"));
		else
			lblMtSetTestConfig.setText(Html.fromHtml("<u>Set Test Config</u>"));


		if (Constants.FILL_DATA) {
			txtCallDuration.setText(Constants.TEST_MT_CALL_DURATION);
			txtTestDuration.setText(Constants.TEST_MT_TEST_DURATION);
		}

		MTTestConfig MTTestConfigObj = TestConfigObj.getMTTestConfig();
		if (Validator.isValidMTConfig(MTTestConfigObj)) {

			txtCallDuration.setText(MTTestConfigObj.sCallDuration);
			txtTestDuration.setText(MTTestConfigObj.sTestDuration);
		}
	}

	private Button.OnClickListener clickListener = new Button.OnClickListener() {

		@Override
		public void onClick(View view) {

			if (view == btnStart) {
				validate();
			} else if (view == btnCancel) {
				goBack();
			}
		}

	};

	void validate() {

		sCallDuration = txtCallDuration.getText().toString();
		sTestDuration = txtTestDuration.getText().toString();

		if (Validator.isEmpty(sCallDuration)) { // call duration
			toast("Please enter call duration");
			return;
		} else if (!Validator.isInteger(sCallDuration)) {
			toast("Please enter valid call duration");
			return;
		} else if (Integer.parseInt(sCallDuration) < 1) {
			toast("Please enter valid call duration");
			return;
		} else if (Integer.parseInt(sCallDuration) > 90) {
			toast("Call duration should not be more than 90 minutes");
			return;
		} else if (Validator.isEmpty(sTestDuration)) { // duration time
			toast("Please enter test duration");
			return;
		} else if (!Validator.isInteger(sTestDuration)) {
			toast("Please enter valid test duration");
			return;
		} else if (Integer.parseInt(sTestDuration) < 1) {
			toast("Please enter valid test duration");
			return;
		} else if (Integer.parseInt(sTestDuration) > 90) {
			toast("Test duration should not be more than 90 minutes");
			return;
		} else {

			int mCallDuration = Integer.parseInt(sCallDuration) * 60000;
			int mTestDuration = Integer.parseInt(sTestDuration) * 60000;

			if (mTestDuration < mCallDuration) {
				toast("Test duration should be greater than call duration");
			} else {
				MTTestConfig MOTestConfigObj = new MTTestConfig(sCallDuration,
						sTestDuration);
				TestConfigObj.setMTTestConfig(MOTestConfigObj);
				if (bundle == null)
					bundle = new Bundle();
				Intent intent = new Intent(activity, TestListActivity.class);
				bundle.putSerializable(StringUtils.EXTRA_TEST_CONFIG,
						TestConfigObj);
				intent.putExtras(bundle);
				showIntent(intent);
			}
		}

	}

	@Override
	public void goBack() {
		sTestDuration = txtTestDuration.getText().toString();
		if (Validator.isEmpty(sTestDuration)) {
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