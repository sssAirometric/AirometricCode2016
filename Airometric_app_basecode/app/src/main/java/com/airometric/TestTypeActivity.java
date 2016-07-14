package com.airometric;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.airometric.R.color;
import com.airometric.api.APIManager;
import com.airometric.api.ResponseCodes;
import com.airometric.classes.TestConfig;
import com.airometric.config.Constants;
import com.airometric.config.Messages;
import com.airometric.config.StringUtils;
import com.airometric.dialogs.AppAlert;
import com.airometric.dialogs.ExitAlert;
import com.airometric.storage.Preferences;
import com.airometric.utility.DeviceUtil;
import com.airometric.utility.FileUtil;
import com.airometric.utility.L;
import com.airometric.utility.NotificationUtil;
import com.airometric.utility.Validator;
import com.airometric.utility.runners.TestUtil;
import com.airometric.parser.ConfigXMLParser;
import com.airometric.preferences.SettingsActivity;

public class TestTypeActivity extends AppActivity {

	private RadioGroup rbTestTypeGroup;
	private RadioButton rbDownloadConfigurationTest, rbManualConfigurationTest,
			rbExternalTest, rbTestType;
	private Thread thrdDownload;
	private Button btnStopTest, btnUploadFailedResults;
	private boolean isAnyOfTestRunning = false;
	private String msg = null;
	private TestConfig objTestConfig = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAppTitle(R.layout.displaytesttype);

		initLayouts();

	}

	private void initLayouts() {
		L.log("Initialize Layouts...");

		rbTestTypeGroup = (RadioGroup) findViewById(R.id.rbTestTypeGroup);
		rbDownloadConfigurationTest = (RadioButton) findViewById(R.id.rbDownloadConfigurationTest);
		rbManualConfigurationTest = (RadioButton) findViewById(R.id.rbManualConfigurationTest);
		rbExternalTest = (RadioButton) findViewById(R.id.rbExternalTest);

		rbDownloadConfigurationTest.setChecked(false);
		rbManualConfigurationTest.setChecked(false);
		rbExternalTest.setChecked(false);

		String sLevel = pref.getUserLevel();
		debug("Logged in user -> + " + pref.getUsername() + "; Level -> "
				+ sLevel);
		if (sLevel.equalsIgnoreCase(StringUtils.USER_LEVEL_TECHNICIAN)) {
			rbManualConfigurationTest.setEnabled(false);
			rbManualConfigurationTest.setTextColor(color.gray_light);
		}

		((TextView) findViewById(R.id.txt_settings))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent i = new Intent(TestTypeActivity.this,
								SettingsActivity.class);
						startActivityForResult(i, 1);
					}
				});

		// rbTestTypeGroup.setSelected(false);

		rbTestTypeGroup.setOnCheckedChangeListener(chkListener);

		isAnyOfTestRunning = false;
		if (pref.isTestRunning())
			isAnyOfTestRunning = true;
		btnStopTest = (Button) findViewById(R.id.btnStopTest);
		btnStopTest.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				stopTest();
			}
		});
		btnUploadFailedResults = (Button) findViewById(R.id.btnUploadFailedResults);
		btnUploadFailedResults.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				doUploadFailedResults();
			}
		});
		if (isAnyOfTestRunning) {
			boolean ext_tst_runng = pref.isExternalTestRunning();

			if (ext_tst_runng) 
				
				btnStopTest.setVisibility(View.GONE);	
			else
				btnStopTest.setVisibility(View.VISIBLE);
		} else{
			btnStopTest.setVisibility(View.GONE);
			
			/*new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
						
					activity.runOnUiThread(new Runnable() {
				        @Override
				        public void run() {
				        	btnStopTest.setVisibility(View.GONE);
				        } 
				});					
				}
			}, 5000);*/
		}
	}

	@Override
	protected void onResume() {
		event("onResume()");
		checkBundleValues();
		super.onResume();
	};

	public void checkBundleValues() {
		event("checkBundleValues()");

		Bundle bndle = this.getIntent().getExtras();
		if (bndle != null)
			if (bndle.containsKey(StringUtils.EXTRA_STATUS_MSG))
				msg = bndle.getString(StringUtils.EXTRA_STATUS_MSG);
		L.debug("MESSAGE in TEST TYPE ACTIVITITY: " + msg);

		if (!Validator.isEmpty(msg)) {
			new AppAlert(activity, msg) {

				@Override
				public void okClickListener() {
					L.debug("Message != empty");
					final Intent intent = new Intent(activity,
							TestTypeActivity.class);

					new Timer().schedule(new TimerTask() {
						@Override
						public void run() {
							finish();
							startActivity(intent);
						}
					}, 20000);

				}
			};
		}
		if (pref.isTestRunning())
			isAnyOfTestRunning = true;
		else
			isAnyOfTestRunning = false;

		btnStopTest.setVisibility(View.GONE);

		if (isAnyOfTestRunning) {
			boolean ext_tst_runng = pref.isExternalTestRunning();

			if (ext_tst_runng) {
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						//btnStopTest.setVisibility(View.GONE);						
						activity.runOnUiThread(new Runnable() {
					        @Override
					        public void run() {
					        	btnStopTest.setVisibility(View.GONE);
					        } 
					});					
						
					}
				}, 10000);
			} else
				btnStopTest.setVisibility(View.VISIBLE);
		} else {
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					//btnStopTest.setVisibility(View.GONE);						
					activity.runOnUiThread(new Runnable() {
				        @Override
				        public void run() {
				        	btnStopTest.setVisibility(View.GONE);
				        } 
				});					
				}
			}, 10000);
		}

	}

	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Constants.CurrentActivity = this;
		event("onNewIntent()");
		try {
			Bundle bndle = intent.getExtras();
			L.printBundleValues(bndle);
		} catch (Exception e) {
			// TODO: handle exception
		}
		setIntent(intent);
	};

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		event("onSaveInstanceState()");
		super.onSaveInstanceState(outState);
	}

	RadioGroup.OnCheckedChangeListener chkListener = new RadioGroup.OnCheckedChangeListener() {
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			rbTestType = (RadioButton) findViewById(checkedId);
			if (rbTestType != null) {
				if (rbTestType.isChecked()) {
					if (isAnyOfTestRunning) {
						if (rbTestType != rbExternalTest) {
							toast(Messages.TEST_ALRDY_RUNNING);
							rbTestTypeGroup.clearCheck();
							return;
						} else {
							if (!pref.isExternalTestRunning()) {
								toast(Messages.TEST_ALRDY_RUNNING);
								rbTestTypeGroup.clearCheck();
								return;
							}

						}
					}
					if (Constants.IS_FAILED_UPLOAD_RUNNING) {
						toast(Messages.FAILED_UPLOAD_ALRDY_RUNNING);
						rbTestTypeGroup.clearCheck();
						return;
					}
					if (rbTestType == rbDownloadConfigurationTest) {
						startDownloadConfig();
					} else if (rbTestType == rbManualConfigurationTest) {
						// showExistingList();
						Intent intent = new Intent(activity,
								TestListActivity.class);
						intent.putExtra(StringUtils.EXTRA_TEST_MODE,
								StringUtils.TEST_MODE_MANUAL);
						if (pref != null
								&& pref.getValue(
										Preferences.KEY_IS_TEST_CONFIG_SET,
										false))
							pref.putValue(Preferences.KEY_IS_TEST_CONFIG_SET,
									false);
						showIntent(intent);
					} else if (rbTestType == rbExternalTest) {
						showActivity(ExternalTestActivity.class);
					}
				}
			}
		}
	};

	protected void stopTest() {
		new AppAlert(this, Messages.CONFIRM_STOP_TEST, true) {
			@Override
			public void okClickListener() {
				if (!pref.isTestCanceled() && pref.isTestRunning()) {
					TestUtil testUtil = new TestUtil(activity) {
						@Override
						protected void stopCompleted() {
							btnStopTest.setVisibility(View.GONE);
						}
					};
					testUtil.stopTest();
				} else {
					new AppAlert(activity, Messages.TEST_ALREADY_COMPLETED) {
						@Override
						public void okClickListener() {
							checkBundleValues();
						}
					};
				}
				super.okClickListener();
			}

			@Override
			public void cancelClickListener() {

			}
		};
	}

	void showExistingList() {

		String[] strpercentagelist = { "New test" }; // ,
														// "Load Test - Chennai","Browser Test - Bangalore"

		new AppAlert(this, "Select Test to run", strpercentagelist) {

			@Override
			public void okClickListener(int item) {

				if (item == 0) {
					Intent intent = new Intent(activity, TestListActivity.class);
					intent.putExtra(StringUtils.EXTRA_TEST_MODE,
							StringUtils.TEST_MODE_MANUAL);
					showIntent(intent);
				}

			}

			@Override
			public void backKeyListener() {
				rbTestTypeGroup.clearCheck();
			}
		};

	}

	private void startDownloadConfig() {

		new AppAlert(activity, Messages.CONFIRM_DOWNLOAD_CONFIG, true) {
			@Override
			public void okClickListener() {
				String sIMEI = new DeviceUtil(activity).getIMEI();
				String sUserid = pref.getUsername();
				String sPwd = pref.getPassword();

				doDownload(sUserid, sPwd, sIMEI,
						Constants.DOWNLOAD_CONFIG_FILENAME);
			}

			@Override
			public void cancelClickListener() {
				rbTestTypeGroup.clearCheck();
			}
		};
	}

	private void doDownload(final String sUsername, final String sPassword,
			final String sIMEI, final String sOutFilepath) {

		showLoading(Messages.PLZ_WAIT_DOWNLOADING);

		thrdDownload = new Thread() {
			@Override
			public void run() {

				Message msg = new Message();
				Bundle bndle = new Bundle();

				String sResponseCode = "", sResponseDesc = "";

				try {
					APIManager apiClient = new APIManager(activity);

					APIManager.Status status = apiClient.processDownload(
							sUsername, sPassword, sIMEI, sOutFilepath);

					if (status == APIManager.Status.ERROR) {
						sResponseCode = StringUtils.ERROR_CODE;
						sResponseDesc = apiClient.getErrorMessage();
					} else {
						String sResponse = apiClient.getResponse();
						debug("Download Response :: " + sResponse);
						if (sResponse.trim().equalsIgnoreCase(
								ResponseCodes.SUCCESS)) {
							sResponseCode = ResponseCodes.SUCCESS;
							sResponseDesc = "Configuration file downloaded!";

							String sConfigFilePath = activity
									.getFileStreamPath(
											Constants.DOWNLOAD_CONFIG_FILENAME)
									.getAbsolutePath();
							debug("Config Path :: " + sConfigFilePath);

							String sConfigContent = FileUtil
									.readFile(sConfigFilePath);
							ConfigXMLParser parser = new ConfigXMLParser(pref);
							debug("sConfigContent ==> " + sConfigContent);
							objTestConfig = parser.parseXML(sConfigContent);
							// objTestConfig.getIsExternalTest();

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

				bndle.putString(StringUtils.CODE, sResponseCode);
				bndle.putString(StringUtils.DESC, sResponseDesc);
				msg.setData(bndle);
				progressHandler.sendMessage(msg);
			}
		};

		thrdDownload.start();
	}

	/**
	 * Handling the message while progressing
	 */

	private Handler progressHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			try {
				thrdDownload.interrupt();
			} catch (Exception e) {
			}

			rbTestTypeGroup.clearCheck();

			// ###################

			String sResponseCode = msg.getData().getString(StringUtils.CODE);
			String sResponseDesc = msg.getData().getString(StringUtils.DESC);

			if (sResponseCode.equals(ResponseCodes.SUCCESS)) {
				new AppAlert(activity, sResponseDesc) {
					public void okClickListener() {
						if (objTestConfig.getIsExternalTest()) {
							L.debug("objTestConfig >>>>>>>:" + objTestConfig);
							Intent intent = new Intent(activity,
									ExternalTestActivity.class);
							intent.putExtra(StringUtils.EXTRA_TEST_MODE,
									StringUtils.TEST_MODE_MANUAL);
							showIntent(intent);

						} else {
							String sConfigFilePath = activity
									.getFileStreamPath(
											Constants.DOWNLOAD_CONFIG_FILENAME)
									.getAbsolutePath();
							debug("Config Path :: " + sConfigFilePath);

							Intent intent = new Intent(activity,
									TestListActivity.class);
							intent.putExtra(StringUtils.EXTRA_CONFIG_PATH,
									sConfigFilePath);
							intent.putExtra(StringUtils.EXTRA_TEST_MODE,
									StringUtils.TEST_MODE_CONFIG);
							showIntent(intent);
						}
						;
					}
				};

			} else if (sResponseCode.equals(ResponseCodes.FAILURE)) {
				alert("Error while downloading...");
			} else {

				if (sResponseCode.equals(StringUtils.ERROR_CODE))
					error(sResponseDesc);
				else
					new AppAlert(activity, sResponseCode, sResponseDesc);
			}
			hideLoading();
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

	@Override
	public void goBack() {
		if (pref.isTestRunning()) {
			new AppAlert(this, Messages.EXIT_WHILE_TEST_RUNNING, true) {
				@Override
				public void okClickListener() {
					stopTest();
					pref.setLoggedInStatus(false);

					NotificationUtil.cancelAllNotification(activity);
					System.exit(0);
				}
			};
		} else {
			new ExitAlert(activity) {
				@Override
				public void okClickListener() {
					NotificationUtil.cancelAllNotification(activity);
				}
			};
		}
	}

	protected void doUploadFailedResults() {
		if (pref.isTestRunning()) {
			alert("You cannot upload failed results while test running.");
			return;
		}

		ArrayList<String> arrLstLogcatFilesPath = new ArrayList<String>();
		ArrayList<String> arrLstDevInfoFilesPath = new ArrayList<String>();
		FilenameFilter logcatFlter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".txt");
			}
		};

		FilenameFilter devinfoFlter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".xml");
			}
		};

		// Failed MO result files
		File fldrMO = new File(FileUtil.MO_LOG_DIR);
		File[] failedLogcatFlsMO = fldrMO.listFiles(logcatFlter);
		int fldMOLogcatFilesCount = failedLogcatFlsMO.length;
		File[] failedDevInfoFlsMO = fldrMO.listFiles(devinfoFlter);
		int fldMODevInfoFilesCount = failedDevInfoFlsMO.length;
		debug("fldMOLogcatFilesCount => " + fldMOLogcatFilesCount
				+ ", fldMODevInfoFilesCount ==> " + fldMODevInfoFilesCount);
		for (int file_i = 0; file_i < fldMOLogcatFilesCount
				&& file_i < fldMODevInfoFilesCount; file_i++) {
			File MOFailedLogcatFle = failedLogcatFlsMO[file_i];
			File MOFailedDevInfoFle = failedDevInfoFlsMO[file_i];
			debug("MOFailedLogcatFle => " + MOFailedLogcatFle.getAbsolutePath()
					+ "\n MOFailedDevInfoFle ==> "
					+ MOFailedDevInfoFle.getAbsolutePath());
			arrLstLogcatFilesPath.add(MOFailedLogcatFle.getAbsolutePath());
			arrLstDevInfoFilesPath.add(MOFailedDevInfoFle.getAbsolutePath());
		}

		// Failed MT result files
		File fldrMT = new File(FileUtil.MT_LOG_DIR);
		File[] failedLogcatFlsMT = fldrMT.listFiles(logcatFlter);
		int fldMTLogcatFilesCount = failedLogcatFlsMT.length;
		File[] failedDevInfoFlsMT = fldrMT.listFiles(devinfoFlter);
		int fldMTDevInfoFilesCount = failedDevInfoFlsMT.length;
		debug("fldMTLogcatFilesCount => " + fldMTLogcatFilesCount
				+ ", fldMTDevInfoFilesCount ==> " + fldMTDevInfoFilesCount);
		for (int file_i = 0; file_i < fldMTLogcatFilesCount
				&& file_i < fldMTDevInfoFilesCount; file_i++) {
			File MTFailedLogcatFle = failedLogcatFlsMT[file_i];
			File MTFailedDevInfoFle = failedDevInfoFlsMT[file_i];
			debug("MTFailedLogcatFle => " + MTFailedLogcatFle.getAbsolutePath()
					+ "\n MTFailedDevInfoFle ==> "
					+ MTFailedDevInfoFle.getAbsolutePath());
			arrLstLogcatFilesPath.add(MTFailedLogcatFle.getAbsolutePath());
			arrLstDevInfoFilesPath.add(MTFailedDevInfoFle.getAbsolutePath());
		}

		// Failed FTP result files
		File fldrFTP = new File(FileUtil.FTP_LOG_DIR);
		File[] failedLogcatFlsFTP = fldrFTP.listFiles(logcatFlter);
		int fldFTPLogcatFilesCount = failedLogcatFlsFTP.length;
		File[] failedDevInfoFlsFTP = fldrFTP.listFiles(devinfoFlter);
		int fldFTPDevInfoFilesCount = failedDevInfoFlsFTP.length;
		debug("fldFTPLogcatFilesCount => " + fldFTPLogcatFilesCount
				+ ", fldFTPDevInfoFilesCount ==> " + fldFTPDevInfoFilesCount);
		for (int file_i = 0; file_i < fldFTPLogcatFilesCount
				&& file_i < fldFTPDevInfoFilesCount; file_i++) {
			File FTPFailedLogcatFle = failedLogcatFlsFTP[file_i];
			File FTPFailedDevInfoFle = failedDevInfoFlsFTP[file_i];
			debug("FTPFailedLogcatFle => "
					+ FTPFailedLogcatFle.getAbsolutePath()
					+ "\n FTPFailedDevInfoFle ==> "
					+ FTPFailedDevInfoFle.getAbsolutePath());
			arrLstLogcatFilesPath.add(FTPFailedLogcatFle.getAbsolutePath());
			arrLstDevInfoFilesPath.add(FTPFailedDevInfoFle.getAbsolutePath());
		}

		// Failed UDP result files
		File fldrUDP = new File(FileUtil.UDP_LOG_DIR);
		File[] failedLogcatFlsUDP = fldrUDP.listFiles(logcatFlter);
		int fldUDPLogcatFilesCount = failedLogcatFlsUDP.length;
		File[] failedDevInfoFlsUDP = fldrUDP.listFiles(devinfoFlter);
		int fldUDPDevInfoFilesCount = failedDevInfoFlsUDP.length;
		debug("fldUDPLogcatFilesCount => " + fldUDPLogcatFilesCount
				+ ", fldUDPDevInfoFilesCount ==> " + fldUDPDevInfoFilesCount);
		for (int file_i = 0; file_i < fldUDPLogcatFilesCount
				&& file_i < fldUDPDevInfoFilesCount; file_i++) {
			File UDPFailedLogcatFle = failedLogcatFlsUDP[file_i];
			File UDPFailedDevInfoFle = failedDevInfoFlsUDP[file_i];
			debug("UDPFailedLogcatFle => "
					+ UDPFailedLogcatFle.getAbsolutePath()
					+ "\n UDPFailedDevInfoFle ==> "
					+ UDPFailedDevInfoFle.getAbsolutePath());
			arrLstLogcatFilesPath.add(UDPFailedLogcatFle.getAbsolutePath());
			arrLstDevInfoFilesPath.add(UDPFailedDevInfoFle.getAbsolutePath());
		}

		// Failed Ping result files
		File fldrPing = new File(FileUtil.PING_LOG_DIR);
		File[] failedLogcatFlsPing = fldrPing.listFiles(logcatFlter);
		int fldPingLogcatFilesCount = failedLogcatFlsPing.length;
		File[] failedDevInfoFlsPing = fldrPing.listFiles(devinfoFlter);
		int fldPingDevInfoFilesCount = failedDevInfoFlsPing.length;
		debug("fldPingLogcatFilesCount => " + fldPingLogcatFilesCount
				+ ", fldPingDevInfoFilesCount ==> " + fldPingDevInfoFilesCount);
		for (int file_i = 0; file_i < fldPingLogcatFilesCount
				&& file_i < fldPingDevInfoFilesCount; file_i++) {
			File PingFailedLogcatFle = failedLogcatFlsPing[file_i];
			File PingFailedDevInfoFle = failedDevInfoFlsPing[file_i];
			debug("PingFailedLogcatFle => "
					+ PingFailedLogcatFle.getAbsolutePath()
					+ "\n PingFailedDevInfoFle ==> "
					+ PingFailedDevInfoFle.getAbsolutePath());
			arrLstLogcatFilesPath.add(PingFailedLogcatFle.getAbsolutePath());
			arrLstDevInfoFilesPath.add(PingFailedDevInfoFle.getAbsolutePath());
		}

		// Failed Browser result files
		File fldrBrowser = new File(FileUtil.BROWSER_LOG_DIR);
		File[] failedLogcatFlsBrowser = fldrBrowser.listFiles(logcatFlter);
		int fldBrowserLogcatFilesCount = failedLogcatFlsBrowser.length;
		File[] failedDevInfoFlsBrowser = fldrBrowser.listFiles(devinfoFlter);
		int fldBrowserDevInfoFilesCount = failedDevInfoFlsBrowser.length;
		debug("fldBrowserLogcatFilesCount => " + fldBrowserLogcatFilesCount
				+ ", fldBrowserDevInfoFilesCount ==> "
				+ fldBrowserDevInfoFilesCount);
		for (int file_i = 0; file_i < fldBrowserLogcatFilesCount
				&& file_i < fldBrowserDevInfoFilesCount; file_i++) {
			File BrowserFailedLogcatFle = failedLogcatFlsBrowser[file_i];
			File BrowserFailedDevInfoFle = failedDevInfoFlsBrowser[file_i];
			debug("BrowserFailedLogcatFle => "
					+ BrowserFailedLogcatFle.getAbsolutePath()
					+ "\n BrowserFailedDevInfoFle ==> "
					+ BrowserFailedDevInfoFle.getAbsolutePath());
			arrLstLogcatFilesPath.add(BrowserFailedLogcatFle.getAbsolutePath());
			arrLstDevInfoFilesPath.add(BrowserFailedDevInfoFle
					.getAbsolutePath());
		}

		// Failed VOIP result files
		File fldrVOIP = new File(FileUtil.VOIP_LOG_DIR);
		File[] failedLogcatFlsVOIP = fldrVOIP.listFiles(logcatFlter);
		int fldVOIPLogcatFilesCount = failedLogcatFlsVOIP.length;
		File[] failedDevInfoFlsVOIP = fldrVOIP.listFiles(devinfoFlter);
		int fldVOIPDevInfoFilesCount = failedDevInfoFlsVOIP.length;
		debug("fldVOIPLogcatFilesCount => " + fldVOIPLogcatFilesCount
				+ ", fldVOIPDevInfoFilesCount ==> " + fldVOIPDevInfoFilesCount);
		for (int file_i = 0; file_i < fldVOIPLogcatFilesCount
				&& file_i < fldVOIPDevInfoFilesCount; file_i++) {
			File VOIPFailedLogcatFle = failedLogcatFlsVOIP[file_i];
			File VOIPFailedDevInfoFle = failedDevInfoFlsVOIP[file_i];
			debug("VOIPFailedLogcatFle => "
					+ VOIPFailedLogcatFle.getAbsolutePath()
					+ "\n VOIPFailedDevInfoFle ==> "
					+ VOIPFailedDevInfoFle.getAbsolutePath());
			arrLstLogcatFilesPath.add(VOIPFailedLogcatFle.getAbsolutePath());
			arrLstDevInfoFilesPath.add(VOIPFailedDevInfoFle.getAbsolutePath());
		}

		// Failed External result files
		File fldrEXT = new File(FileUtil.EXT_LOG_DIR);
		File[] failedDevInfoFlsEXT = fldrEXT.listFiles();
		int fldEXTDevInfoFilesCount = failedDevInfoFlsEXT.length;
		debug("fldEXTDevInfoFilesCount => " + fldEXTDevInfoFilesCount);
		for (int file_i = 0; file_i < fldEXTDevInfoFilesCount; file_i++) {
			File EXTFailedDevInfoFle = failedDevInfoFlsEXT[file_i];
			debug("EXTFailedDevInfoFle => "
					+ EXTFailedDevInfoFle.getAbsolutePath());
			arrLstLogcatFilesPath.add("");
			arrLstDevInfoFilesPath.add(EXTFailedDevInfoFle.getAbsolutePath());
		}
		int totalFailedFiles = arrLstLogcatFilesPath.size();
		debug("totalFailedFiles => " + totalFailedFiles);
		debug("arrLstLogcatFilesPath => " + arrLstLogcatFilesPath.toString());
		if (totalFailedFiles > 0) {
			doUploadFailedResults(activity, arrLstDevInfoFilesPath,
					arrLstLogcatFilesPath);
		} else {
			alert(Messages.NO_FAILED_FILES_TO_UPLOAD);
		}
	}

	void doUploadFailedResults(AppActivity activity,
			ArrayList<String> lstXMLFilePath,
			ArrayList<String> lstLogcatFilesPath) {
		L.event(" Uploading failed files ");
		NotificationUtil.showFailedNotification(activity,
				" Uploading failed files ", true);
		ResultUploadTask retryTask = new ResultUploadTask(activity,
				lstXMLFilePath, lstLogcatFilesPath);
		retryTask.execute();
		Constants.IS_FAILED_UPLOAD_RUNNING = true;
	}

	private class ResultUploadTask extends AsyncTask<String, Integer, Message> {
		private AppActivity activity;
		ArrayList<String> arrLstDevInfoXMLFilesPath, arrLstLogcatFilesPath;

		public ResultUploadTask(AppActivity activity,
				ArrayList<String> lstXMLFilePath,
				ArrayList<String> lstLogcatFilesPath) {
			this.activity = activity;
			arrLstDevInfoXMLFilesPath = lstXMLFilePath;
			arrLstLogcatFilesPath = lstLogcatFilesPath;
			L.event("RetryUploadTask()... \n DevInfoXMLFilesPath ==> "
					+ arrLstDevInfoXMLFilesPath + "\n LogcatFilesPath ==> "
					+ arrLstLogcatFilesPath);
		}

		protected void onProgressUpdate(Integer... progress) {

		}

		@Override
		protected void onPostExecute(Message msg) {
			String sResponseCode = msg.getData().getString(StringUtils.CODE);
			String sResponseDesc = msg.getData().getString(StringUtils.DESC);
			L.debug("onPostExecute()... " + sResponseDesc);
			DeviceUtil.updateTestScreen();
			NotificationUtil.showFailedNotification(activity, sResponseDesc,
					true);
			retryResult(sResponseCode, sResponseDesc);
		}

		@Override
		protected Message doInBackground(String... files) {
			Message msg = new Message();

			String sResponseCode = "", sResponseDesc = "";
			int iTotalToUpload, iSuccessUpload = 0, iFailedUpload = 0;
			int filesCount = arrLstDevInfoXMLFilesPath.size();
			iTotalToUpload = filesCount;
			for (int file_i = 0; file_i < filesCount; file_i++) {
				String sLogcatFilePath = arrLstLogcatFilesPath.get(file_i);
				String sDevInfoXMLFilePath = arrLstDevInfoXMLFilesPath
						.get(file_i);
				Message lc_msg = doUpload(sLogcatFilePath, sDevInfoXMLFilePath);
				sResponseCode = lc_msg.getData().getString(StringUtils.CODE);
				if (sResponseCode.equals(ResponseCodes.SUCCESS))
					iSuccessUpload++;
				else
					iFailedUpload++;
			}
			if (iSuccessUpload == iTotalToUpload) {

				L.debug("@@@@@ Retry Upload Task @@@@");

				sResponseDesc = "All failed results has been uploaded";
				pref.putValue(Preferences.KEY_IS_TEST_CONFIG_SET, false);
				pref.putValue(Preferences.KEY_TEST_NAME, "");
				pref.putValue(Preferences.KEY_SELECTED_MARKET_PALCE, "");

				L.debug("Test config data cleared!!");
			}

			else {
				sResponseDesc = String
						.format("Failed results upload completed. %d failed of %d results",
								iFailedUpload, iTotalToUpload);
				pref.putValue(Preferences.KEY_IS_TEST_CONFIG_SET, false);
				pref.putValue(Preferences.KEY_TEST_NAME, "");
				pref.putValue(Preferences.KEY_SELECTED_MARKET_PALCE, "");

				L.debug("Test config data cleared!!");
			}
			Bundle bndle = new Bundle();
			bndle.putString(StringUtils.CODE, sResponseCode);
			bndle.putString(StringUtils.DESC, sResponseDesc);
			msg.setData(bndle);
			return msg;
		}

		private Message doUpload(String sLogcatFilePath,
				String sDevInfoXMLFilePath) {

			Message msg = new Message();
			Bundle bndle = new Bundle();

			String sResponseCode = "", sResponseDesc = "";

			try {
				APIManager apiClient = new APIManager(activity);

				String sIMEI = new DeviceUtil(activity).getIMEI();
				Preferences pref = new Preferences(activity);
				String sUsername = pref.getUsername();
				String sPwd = pref.getPassword();
				APIManager.Status status = APIManager.Status.NONE;

				if (Validator.isEmpty(sLogcatFilePath)) {
					status = apiClient.processUploadSingle(sDevInfoXMLFilePath);
				} else {
					status = apiClient.processUpload(sUsername, sPwd, sIMEI,
							sLogcatFilePath, sDevInfoXMLFilePath);
				}

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
					// Delete log files
					FileUtil.delete(sDevInfoXMLFilePath);
					FileUtil.delete(sLogcatFilePath);
				}
			bndle.putString(StringUtils.CODE, sResponseCode);
			bndle.putString(StringUtils.DESC, sResponseDesc);
			msg.setData(bndle);
			return msg;
		}
	}

	public void retryResult(String sResponseCode, String sResponseDesc) {
		L.event("In retryResult()... sResponseCode ==> " + sResponseCode
				+ ", sResponseDesc ==> " + sResponseDesc);
		Constants.IS_FAILED_UPLOAD_RUNNING = false;
	}
}