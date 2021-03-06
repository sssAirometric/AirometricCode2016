package com.airometric;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.airometric.classes.TestConfig;
import com.airometric.classes.VOIPTestConfig;
import com.airometric.config.Constants;
import com.airometric.config.Messages;
import com.airometric.config.StringUtils;
import com.airometric.dialogs.AppAlert;
import com.airometric.dialogs.TestConfigAlert;
import com.airometric.storage.Preferences;
import com.airometric.utility.L;
import com.airometric.utility.Validator;

public class VOIPTestInputActivity extends AppActivity implements View.OnClickListener {

	private Button btnStart, btnCancel;
	private EditText txtTestDuration;
	private String sTestDuration;
	private TestConfig TestConfigObj;

	long test_duration_secs, completed_duration_secs = 0, pass = 0;
	Bundle bundle;

	TextView lblVoipSetTestConfig;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// To setup the title bar
		setAppTitle(R.layout.voip_test_input);

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

		txtTestDuration = (EditText) findViewById(R.id.txtTestDuration);

		btnStart = (Button) findViewById(R.id.btnStart);
		btnStart.setOnClickListener(clickListener);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(clickListener);


		lblVoipSetTestConfig = (TextView) findViewById(R.id.lblVoipSetTestConfig);		
		lblVoipSetTestConfig.setMovementMethod(LinkMovementMethod.getInstance());
		lblVoipSetTestConfig.setOnClickListener(this);
		
		if(pref != null && pref.getValue(Preferences.KEY_IS_TEST_CONFIG_SET, false))
			lblVoipSetTestConfig.setText(Html.fromHtml("<u>Edit Test Config</u>"));
		else
			lblVoipSetTestConfig.setText(Html.fromHtml("<u>Set Test Config</u>"));
		
		if (Constants.FILL_DATA) {
			txtTestDuration.setText(Constants.TEST_VOIP_DURATION);
		}

		VOIPTestConfig VOIPTestConfigObj = TestConfigObj.getVOIPTestConfig();
		if (Validator.isValidVOIPConfig(VOIPTestConfigObj)) {
			txtTestDuration.setText(VOIPTestConfigObj.sTestDuration);
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

		sTestDuration = txtTestDuration.getText().toString();

		if (Validator.isEmpty(sTestDuration)) { // duration time
			toast("Please enter duration");
			return;
		} else if (!Validator.isInteger(sTestDuration)) {
			toast("Please enter valid duration");
			return;
		} else if (Integer.parseInt(sTestDuration) < 1) {
			toast("Please enter valid duration");
			return;
		} else if (Integer.parseInt(sTestDuration) > 90) {
			toast("Test duration should not be more than 90 minutes");
			return;
		} else {

			VOIPTestConfig VOIPTestConfigObj = new VOIPTestConfig(sTestDuration);
			TestConfigObj.setVOIPTestConfig(VOIPTestConfigObj);
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