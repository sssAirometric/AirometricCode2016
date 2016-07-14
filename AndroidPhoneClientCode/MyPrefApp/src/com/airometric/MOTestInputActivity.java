package com.airometric;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.airometric.classes.Contact;
import com.airometric.classes.MOTestConfig;
import com.airometric.classes.TestConfig;
import com.airometric.config.Constants;
import com.airometric.config.Messages;
import com.airometric.config.StringUtils;
import com.airometric.dialogs.AppAlert;
import com.airometric.dialogs.TestConfigAlert;
import com.airometric.storage.Preferences;
import com.airometric.utility.L;
import com.airometric.utility.Validator;

public class MOTestInputActivity extends AppActivity implements android.view.View.OnClickListener {

	private Button btnStart, btnCancel, btnGetFromContacts;
	private EditText txtTestDuration, txtTestPauseTime, txtCallDuration,
			txtTestPhonenumber;
	private String sTestPhonenumber, sCallDuration, sTestPauseTime,
			sTestDuration;
	private TestConfig TestConfigObj;

	long call_duration_secs, call_duration_millis, pause_time_secs,
			test_duration_secs, completed_duration_secs = 0, pass = 0;
	Bundle bundle;
	private static final int PICK_CONTACT = 1;
	
	private TextView lblMoSetTestConfig;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// To setup the title bar
		setAppTitle(R.layout.mo_test_input);

		bundle = this.getIntent().getExtras();
		if (bundle != null) {
			if (bundle.containsKey(StringUtils.EXTRA_TEST_CONFIG))
				TestConfigObj = (TestConfig) bundle
						.getSerializable(StringUtils.EXTRA_TEST_CONFIG);
			MOTestConfig MOTestConfigObj = TestConfigObj.getMOTestConfig();
			Log.i("MOTestInput page",MOTestConfigObj.sPhoneNumber+","+MOTestConfigObj.sCallDuration+","+MOTestConfigObj.sPauseTime+",");
		}
		if (TestConfigObj == null)
			{
			TestConfigObj = new TestConfig();
			Log.i("MOTestInput page","");
			}
		initLayouts();
	}

	/**
	 * To initialize the layout and assign listeners for that fields.
	 */

	private void initLayouts() {
		L.log("Initialize Layouts...");

		txtTestPhonenumber = (EditText) findViewById(R.id.txtTestPhonenumber);
		txtCallDuration = (EditText) findViewById(R.id.txtCallDuration);
		txtTestPauseTime = (EditText) findViewById(R.id.txtTestPauseTime);
		txtTestDuration = (EditText) findViewById(R.id.txtTestDuration);

		btnGetFromContacts = (Button) findViewById(R.id.btnGetFromContacts);
		btnGetFromContacts.setOnClickListener(clickListener);
		btnStart = (Button) findViewById(R.id.btnStart);
		btnStart.setOnClickListener(clickListener);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(clickListener);

		if (Constants.FILL_DATA) {
			txtTestPhonenumber.setText(Constants.TEST_MO_PHONE);
			txtCallDuration.setText(Constants.TEST_MO_CALL_DURATION);
			txtTestPauseTime.setText(Constants.TEST_MO_PAUSE_TIME);
			txtTestDuration.setText(Constants.TEST_MO_DURATION);
		}

		MOTestConfig MOTestConfigObj = TestConfigObj.getMOTestConfig();
		if (Validator.isValidMOConfig(MOTestConfigObj)) {
			txtTestPhonenumber.setText(MOTestConfigObj.sPhoneNumber);
			}
		txtCallDuration.setText(MOTestConfigObj.sCallDuration);
		txtTestPauseTime.setText(MOTestConfigObj.sPauseTime);
		txtTestDuration.setText(MOTestConfigObj.sTestDuration);
		
		lblMoSetTestConfig = (TextView) findViewById(R.id.lblMoSetTestConfig);		
		lblMoSetTestConfig.setMovementMethod(LinkMovementMethod.getInstance());
		lblMoSetTestConfig.setOnClickListener(this);
		
		if(pref != null && pref.getValue(Preferences.KEY_IS_TEST_CONFIG_SET, false))
			lblMoSetTestConfig.setText(Html.fromHtml("<u>Edit Test Config</u>"));
		else
			lblMoSetTestConfig.setText(Html.fromHtml("<u>Set Test Config</u>"));
		}

	private Button.OnClickListener clickListener = new Button.OnClickListener() {

		@Override
		public void onClick(View view) {

			if (view == btnStart) {
				validate();
			} else if (view == btnCancel) {
				goBack();
			} else if (view == btnGetFromContacts) {
				getFromContacts();
			}
		}

	};

	protected void getFromContacts() {
		Intent pickContactIntent = new Intent(Intent.ACTION_PICK,
				ContactsContract.Contacts.CONTENT_URI);
		pickContactIntent
				.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
		Intent intent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
		intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
		// startActivityForResult(intent, CONTACT_PICKER_RESULT);

		startActivityForResult(
				new Intent(activity, ChooseContactActivity.class), PICK_CONTACT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		debug("resultCode . " + resultCode + " :: requestCode . " + requestCode);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case PICK_CONTACT:
				Contact selectedContact = (Contact) data.getExtras().get(
						StringUtils.EXTRA_SELECTED_CONTACT);
				debug("selectedContact: " + selectedContact);
				txtTestPhonenumber.setText(selectedContact.getPhoneNumber());
				break;
			}
		} else {
			// gracefully handle failure
			debug("Warning: activity result not ok.. " + resultCode);
		}
	}

	void validate() {

		sTestPhonenumber = txtTestPhonenumber.getText().toString();
		sCallDuration = txtCallDuration.getText().toString();
		sTestPauseTime = txtTestPauseTime.getText().toString();
		sTestDuration = txtTestDuration.getText().toString();

		if (Validator.isEmpty(sTestPhonenumber)) { // phone number
			toast("Please enter phone number");
			return;
		} else if (Validator.isEmpty(sCallDuration)) { // call duration
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
		} else if (Validator.isEmpty(sTestPauseTime)) { // pause time
			toast("Please enter pause time");
			return;
		} else if (!Validator.isInteger(sTestPauseTime)) {
			toast("Please enter valid pause time");
			return;
		} else if (Integer.parseInt(sTestPauseTime) < 1) {
			toast("Please enter valid pause time");
			return;
		} else if (Validator.isEmpty(sTestDuration)) { // duration time
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

			int mCallDuration = Integer.parseInt(sCallDuration) * 60000;
			int mPauseTime = Integer.parseInt(sTestPauseTime) * 1000;
			int mDuration = Integer.parseInt(sTestDuration) * 60000;

			int validTestDuration = mCallDuration + mPauseTime;
			int validPauseTime = mCallDuration - mDuration;

			if (mCallDuration > mDuration) {
				toast("Test duration should be greater than call duration");
			} else if (mPauseTime < validPauseTime) {
				toast("Pause time should not be greater than Call duration + Test duration");
			} else if (mDuration < validTestDuration) {
				toast("Test duration can't be lesser than Call duration + Pause time ");
			} else {

				MOTestConfig MOTestConfigObj = new MOTestConfig(
						sTestPhonenumber, sCallDuration, sTestPauseTime,
						sTestDuration);
				TestConfigObj.setMOTestConfig(MOTestConfigObj);
				if (bundle == null)
					bundle = new Bundle();
				Intent intent = new Intent(activity, TestListActivity.class);
				bundle.putSerializable(StringUtils.EXTRA_CONFIG_PATH,
						TestConfigObj);
				intent.putExtras(bundle);
				showIntent(intent);
			}
		}

	}

	@Override
	public void goBack() {

		sTestPhonenumber = txtTestPhonenumber.getText().toString();
		sCallDuration = txtCallDuration.getText().toString();
		sTestPauseTime = txtTestPauseTime.getText().toString();
		sTestDuration = txtTestDuration.getText().toString();
		if (Validator.isEmpty(sTestPhonenumber)
				&& Validator.isEmpty(sCallDuration)
				&& Validator.isEmpty(sTestPauseTime)
				&& Validator.isEmpty(sTestDuration)
				&& Validator.isEmpty(sTestDuration)) {

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