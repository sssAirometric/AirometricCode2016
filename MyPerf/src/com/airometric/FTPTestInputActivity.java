package com.airometric;

import java.io.File;
import java.util.Timer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airometric.classes.FTPTestConfig;
import com.airometric.classes.TestConfig;
import com.airometric.config.Constants;
import com.airometric.config.Messages;
import com.airometric.config.StringUtils;
import com.airometric.dialogs.AppAlert;
import com.airometric.dialogs.TestConfigAlert;
import com.airometric.storage.Preferences;
import com.airometric.utility.FTP;
import com.airometric.utility.L;
import com.airometric.utility.Validator;

public class FTPTestInputActivity extends AppActivity implements
		android.view.View.OnClickListener {

	private Button btnSave, btnCancel, btnGetUploadPath, btnGetDownloadPath,
			btnUploadFile;
	private EditText txtServerURL, txtServerUsername, txtServerPassword,
			txtNoOfCycles, txtUploadPath, txtFileToDownload,
			txtMauallUploadFilePath;
	private String sServerURL, sServerUsername, sServerPassword, sNoOfCycles,
			sUploadPath, sFileToDownload, sManuallUploadFilePath;
	private TestConfig TestConfigObj;
	Timer timer;
	private CheckBox chkManuall;
	Handler handler = new Handler();

	private LinearLayout linearManuall;
	Bundle bundle;
	private static final int PICK_PATH_TO_UPLOAD = 2;
	private static final int PICK_FILE_TO_DOWNLOAD = 3;
	private static final int PICK_UPLOAD_FILE = 4;
	private boolean manuallUploadState = false;

	TextView lblFtpSetTestConfig;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// To setup the title bar
		setAppTitle(R.layout.ftp_test_input);

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

		txtServerURL = (EditText) findViewById(R.id.txtServerURL);
		txtServerUsername = (EditText) findViewById(R.id.txtServerUsername);
		txtServerPassword = (EditText) findViewById(R.id.txtServerPassword);
		txtNoOfCycles = (EditText) findViewById(R.id.txtNoOfCycles);
		txtUploadPath = (EditText) findViewById(R.id.txtUploadPath);
		txtFileToDownload = (EditText) findViewById(R.id.txtFileToDownload);
		txtMauallUploadFilePath = (EditText) findViewById(R.id.txtManuallUploadFilePath);
		linearManuall = (LinearLayout) findViewById(R.id.linearManuall);
		chkManuall = (CheckBox) findViewById(R.id.chkManuall);

		btnGetUploadPath = (Button) findViewById(R.id.btnGetUploadPath);
		btnGetUploadPath.setOnClickListener(clickListener);
		btnGetDownloadPath = (Button) findViewById(R.id.btnGetDownloadPath);
		btnGetDownloadPath.setOnClickListener(clickListener);

		btnUploadFile = (Button) findViewById(R.id.btnUploadFile);
		btnUploadFile.setOnClickListener(clickListener);

		btnSave = (Button) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(clickListener);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(clickListener);

		lblFtpSetTestConfig = (TextView) findViewById(R.id.lblFtpSetTestConfig);		
		lblFtpSetTestConfig.setMovementMethod(LinkMovementMethod.getInstance());
		lblFtpSetTestConfig.setOnClickListener(this);
		
		if(pref != null && pref.getValue(Preferences.KEY_IS_TEST_CONFIG_SET, false))		 
			lblFtpSetTestConfig.setText(Html.fromHtml("<u>Edit Test Config</u>"));
		else 
			lblFtpSetTestConfig.setText(Html.fromHtml("<u>Set Test Config</u>"));
		

		if (Constants.FILL_DATA) {
			txtServerURL.setText(Constants.TEST_FTP_SERVER);
			txtServerUsername.setText(Constants.TEST_FTP_USER);
			txtServerPassword.setText(Constants.TEST_FTP_PWD);
			txtUploadPath.setText(Constants.TEST_FTP_UPLOAD_PATH);
			txtFileToDownload.setText(Constants.TEST_FTP_DOWNLOAD_PATH);
			txtNoOfCycles.setText(Constants.TEST_FTP_CYCLES);
		}

		FTPTestConfig FTPTestConfigObj = TestConfigObj.getFTPTestConfig();

		if (Validator.isValidFTPConfig(FTPTestConfigObj)) {

			txtServerURL.setText(FTPTestConfigObj.sServerURL);
			txtServerUsername.setText(FTPTestConfigObj.sUsername);
			txtServerPassword.setText(FTPTestConfigObj.sPassword);
			txtNoOfCycles.setText(FTPTestConfigObj.sTestCyles);
			txtUploadPath.setText(FTPTestConfigObj.sUploadPath);
			txtFileToDownload.setText(FTPTestConfigObj.sFileToDownload);
			txtMauallUploadFilePath
					.setText(FTPTestConfigObj.sManuallUploadFile);

			chkManuall.setChecked(FTPTestConfigObj.bManuallUploadStatus);
			if (chkManuall.isChecked())
				linearManuall.setVisibility(View.VISIBLE);
		}
	}

	public void itemClicked(View v) {
		if (((CheckBox) v).isChecked()) {
			linearManuall.setVisibility(View.VISIBLE);
			manuallUploadState = true;
		} else {
			linearManuall.setVisibility(View.GONE);
			manuallUploadState = false;
			txtMauallUploadFilePath.setText("");
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
			} else if (view == btnGetDownloadPath) {
				validate_ftp(1);
			} else if (view == btnGetUploadPath) {
				validate_ftp(2);
			} else if (view == btnUploadFile) {
				validate_ftp(3);
			}
		}

	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		debug("resultCode . " + resultCode + " :: requestCode . " + requestCode);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {

			case PICK_PATH_TO_UPLOAD:
				if (data != null && data.getExtras() != null
						&& data.getExtras().containsKey("PATH")) {
					String sSelectedFtpFile = (String) data.getExtras().get(
							"PATH");
					String sSelectedFtpFileName = (String) data.getExtras()
							.get("NAME");
					debug("sSelectedFtpFile: " + sSelectedFtpFile);
					if (sSelectedFtpFile != null) {
						txtUploadPath.setText(sSelectedFtpFile);
					}
				}
				break;
			case PICK_FILE_TO_DOWNLOAD:
				if (data != null && data.getExtras() != null
						&& data.getExtras().containsKey("PATH")) {
					String sSelectedFtpFile = (String) data.getExtras().get(
							"PATH");
					String sSelectedFtpFileName = (String) data.getExtras()
							.get("NAME");
					debug("sSelectedFtpFile: " + sSelectedFtpFile);
					if (sSelectedFtpFile != null) {
						txtFileToDownload.setText(sSelectedFtpFile);
					}
				}
				break;

			case PICK_UPLOAD_FILE:
				if (data != null) {
					txtMauallUploadFilePath.setText(data.getData().getPath());
					L.debug("**** Switch Case:"
							+ txtMauallUploadFilePath.getText().toString());
				}
				break;
			}
		} else {
			// gracefully handle failure
			debug("Warning: activity result not ok.. " + resultCode);
		}
	}

	void validate_ftp(int type) {
		// type = 1 means download, 2 means upload, 3 means manually choosing
		// upload file
		sServerURL = txtServerURL.getText().toString();
		sServerUsername = txtServerUsername.getText().toString();
		sServerPassword = txtServerPassword.getText().toString();

		if (Validator.isEmpty(sServerURL)) {
			toast("Please enter server url");
			return;
		} else if (Validator.isEmpty(sServerUsername)) {
			toast("Please enter username");
			return;
		} else if (Validator.isEmpty(sServerPassword)) {
			toast("Please enter password");
			return;
		} else {
			Intent choose_intent = new Intent(activity,
					FTPFileChooseActivity.class);
			if (type == 1) {
				choose_intent.putExtra(StringUtils.EXTRA_CHOOSE_TYPE,
						FTPFileChooseActivity.CHOOSE_TYPE_FILE);
			} else if (type == 2) {
				choose_intent.putExtra(StringUtils.EXTRA_CHOOSE_TYPE,
						FTPFileChooseActivity.CHOOSE_TYPE_DIR);
			} else if (type == 3) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_GET_CONTENT);
				intent.setType("file/*");
				startActivityForResult(intent, PICK_UPLOAD_FILE);
			}
			choose_intent.putExtra(StringUtils.EXTRA_FTP_SERVER, sServerURL);
			choose_intent.putExtra(StringUtils.EXTRA_FTP_USER, sServerUsername);
			choose_intent.putExtra(StringUtils.EXTRA_FTP_PWD, sServerPassword);
			if (type == 1)
				startActivityForResult(choose_intent, PICK_FILE_TO_DOWNLOAD);
			if (type == 2)
				startActivityForResult(choose_intent, PICK_PATH_TO_UPLOAD);
		}
	}

	void validate() {

		sServerURL = txtServerURL.getText().toString();
		sServerUsername = txtServerUsername.getText().toString();
		sServerPassword = txtServerPassword.getText().toString();
		sNoOfCycles = txtNoOfCycles.getText().toString();
		sUploadPath = txtUploadPath.getText().toString();
		sFileToDownload = txtFileToDownload.getText().toString();
		sManuallUploadFilePath = txtMauallUploadFilePath.getText().toString();

		String sValidCycleText = Validator.getValidCyclesText(sNoOfCycles,
				StringUtils.TEST_TYPE_CODE_FTP);
		if (Validator.isEmpty(sServerURL)) {
			toast("Please enter server url");
			return;
		} else if (Validator.isEmpty(sServerUsername)) {
			toast("Please enter username");
			return;
		} else if (Validator.isEmpty(sServerPassword)) {
			toast("Please enter password");
			return;
		} else if (sValidCycleText != null) {
			toast(sValidCycleText);
			return;
		} else if (Validator.isEmpty(sUploadPath)) {
			toast("Please enter upload path");
			return;
		} else if (Validator.isEmpty(sFileToDownload)) {
			toast("Please enter file to download");
			return;
		} else if (manuallUploadState
				&& Validator.isEmpty(txtMauallUploadFilePath.getText()
						.toString())) {
			toast("Please choose file to upload");
			return;
		} else {

			FTPTestConfig FTPTestConfigObj = new FTPTestConfig(sServerURL,
					sServerUsername, sServerPassword, sNoOfCycles, sUploadPath,
					sFileToDownload, sManuallUploadFilePath, manuallUploadState);
			TestConfigObj.setFTPTestConfig(FTPTestConfigObj);
			if (bundle == null)
				bundle = new Bundle();
			Intent intent = new Intent(activity, TestListActivity.class);
			bundle.putSerializable(StringUtils.EXTRA_TEST_CONFIG, TestConfigObj);
			intent.putExtras(bundle);
			showIntent(intent);
		}

	}

	void testUpload() {
		sServerURL = txtServerURL.getText().toString();
		sServerUsername = txtServerUsername.getText().toString();
		sServerPassword = txtServerPassword.getText().toString();
		sNoOfCycles = txtNoOfCycles.getText().toString();
		sUploadPath = txtUploadPath.getText().toString();
	}

	protected void doUpload(String sHostURL, String sUsername,
			String sPassword, int port, String sSrcPath, String sDestPath) {

		print("doUpload()...");
		String status = "";
		try {

			FTP ftp = new FTP(handler);
			if (ftp.ftpConnect(sHostURL, sUsername, sPassword, port)) {
				status = "Connected";
				print("status - " + status);

				print("sSrcPath - " + sSrcPath);
				if (ftp.ftpUpload(sSrcPath, new File(sSrcPath).getName(),
						sDestPath)) {

					print("File uploaded");
				} else {
					print("File upload failed - " + ftp.getServerError());

				}
			} else {
				status = "Not-Connected";
			}
			ftp.ftpDisconnect();
		} catch (Exception e) {
			status = "Error while connecting - " + e.getLocalizedMessage();
			e.printStackTrace();
		}

		print("Status - " + status);
	}

	@Override
	public void goBack() {
		sServerURL = txtServerURL.getText().toString();
		sServerUsername = txtServerUsername.getText().toString();
		sServerPassword = txtServerPassword.getText().toString();
		sNoOfCycles = txtNoOfCycles.getText().toString();
		sUploadPath = txtUploadPath.getText().toString();
		sFileToDownload = txtFileToDownload.getText().toString();
		if (Validator.isEmpty(sServerURL) && Validator.isEmpty(sServerUsername)
				&& Validator.isEmpty(sServerPassword)
				&& Validator.isEmpty(sNoOfCycles)
				&& Validator.isEmpty(sUploadPath)
				&& Validator.isEmpty(sFileToDownload)) {
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
		if (v == lblFtpSetTestConfig) {
			TestConfigAlert objTestConfigAlert = new TestConfigAlert(this);
			objTestConfigAlert.show();
		}
	}
}