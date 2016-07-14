package com.airometric;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.airometric.classes.BrowserTestConfig;
import com.airometric.classes.TestConfig;
import com.airometric.config.Constants;
import com.airometric.config.Messages;
import com.airometric.config.StringUtils;
import com.airometric.dialogs.AppAlert;
import com.airometric.dialogs.TestConfigAlert;
import com.airometric.storage.Preferences;
import com.airometric.utility.L;
import com.airometric.utility.Validator;

public class BrowserTestInputActivity extends AppActivity implements
		View.OnClickListener {

	private Button btnSave, btnCancel;
	private EditText txtPageURL, txtNoOfCycles;
	private String sPageURL, sNoOfCycles;
	private TestConfig TestConfigObj;
	Handler handler = new Handler();

	Bundle bundle;

	TextView lblBrowserSetTestConfig;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// To setup the title bar
		setAppTitle(R.layout.browser_test_input);

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

		txtPageURL = (EditText) findViewById(R.id.txtPageURL);
		txtNoOfCycles = (EditText) findViewById(R.id.txtNoOfCycles);

		btnSave = (Button) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(clickListener);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(clickListener);

		lblBrowserSetTestConfig = (TextView) findViewById(R.id.lblBrowserSetTestConfig);
		lblBrowserSetTestConfig.setMovementMethod(LinkMovementMethod
				.getInstance());
		lblBrowserSetTestConfig.setOnClickListener(this);

		if (pref != null
				&& pref.getValue(Preferences.KEY_IS_TEST_CONFIG_SET, false))
			lblBrowserSetTestConfig.setText(Html
					.fromHtml("<u>Edit Test Config</u>"));
		else
			lblBrowserSetTestConfig.setText(Html
					.fromHtml("<u>Set Test Config</u>"));

		if (Constants.FILL_DATA) {
			txtPageURL.setText(Constants.TEST_BROWSER_SERVER);
			txtNoOfCycles.setText(Constants.TEST_BROWSER_CYCLES);
		}

		BrowserTestConfig BrowserTestConfigObj = TestConfigObj
				.getBrowserTestConfig();
		if (Validator.isValidBrowserConfig(BrowserTestConfigObj)) {

			txtPageURL.setText(BrowserTestConfigObj.sPageURL);
			txtNoOfCycles.setText(BrowserTestConfigObj.sCycles);
		}
	}

	private Button.OnClickListener clickListener = new Button.OnClickListener() {

		@Override
		public void onClick(View view) {

			if (view == btnSave) {
				validate();
			} else if (view == btnCancel) {
				goBack();
			}
		}

	};

	void validate() {

		sPageURL = txtPageURL.getText().toString();
		sNoOfCycles = txtNoOfCycles.getText().toString();
		String sValidCycleText = Validator.getValidCyclesText(sNoOfCycles,
				StringUtils.TEST_TYPE_CODE_BROWSER);

		if (Validator.isEmpty(sPageURL)) {
			toast("Please enter page url");
			return;
		}

		if (!sPageURL.contains("www.")) {
			sPageURL = "www." + sPageURL;
		}

		if (!sPageURL.contains("http://")) {
			sPageURL = "http://" + sPageURL;
		}

		if (!Validator.isValidURL(sPageURL)) {
			toast("Please enter valid page url");
			return;
		} else if (sValidCycleText != null) {
			toast(sValidCycleText);
			return;
		} else {

			BrowserTestConfig BrowserTestConfigObj = new BrowserTestConfig(
					sPageURL, sNoOfCycles);
			TestConfigObj.setBrowserTestConfig(BrowserTestConfigObj);
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
		sPageURL = txtPageURL.getText().toString();
		sNoOfCycles = txtNoOfCycles.getText().toString();
		if (Validator.isEmpty(sPageURL) && Validator.isEmpty(sNoOfCycles)) {
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
		if (v == lblBrowserSetTestConfig) {
			TestConfigAlert objTestConfigAlert = new TestConfigAlert(this);
			objTestConfigAlert.show();
		}
	}
}