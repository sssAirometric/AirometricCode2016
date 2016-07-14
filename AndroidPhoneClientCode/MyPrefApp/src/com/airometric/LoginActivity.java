package com.airometric;

import java.util.HashMap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;

import com.airometric.api.APIManager;
import com.airometric.api.ResponseCodes;
import com.airometric.classes.LoginResponse;
import com.airometric.config.Constants;
import com.airometric.config.Messages;
import com.airometric.config.StringUtils;
import com.airometric.dialogs.AppAlert;
import com.airometric.dialogs.ExitAlert;
import com.airometric.parser.ConfigXMLParser;
import com.airometric.parser.Parser;
import com.airometric.preferences.SettingsActivity;
import com.airometric.storage.Preferences;
import com.airometric.utility.DeviceUtil;
import com.airometric.utility.FileUtil;
import com.airometric.utility.L;
import com.airometric.utility.Validator;

public class LoginActivity extends AppActivity {

	private EditText txtKey;
	private Thread thrdLogin;
	private String sUsername, sPassword,skey;
	Preferences perf;
	private HashMap<String, String> data;
	private LocationManager locationManager;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// To setup the title bar
		setAppTitle(R.layout.login);
		activity = this;
		initLayouts();
		
	}

	/**
	 * To initialize the layout and assign listeners for that fields.
	 */

	private void initLayouts() {
		L.log("Initialize Layouts...");

		((TextView) findViewById(R.id.txt_settings))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent i = new Intent(LoginActivity.this,
								SettingsActivity.class);
						startActivityForResult(i, 1);
					}
				});
		txtKey = (EditText) findViewById(R.id.txtKey);
		
		txtKey.setSingleLine();
		txtKey.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				 if (event.getAction()!=KeyEvent.ACTION_DOWN)
			            return false;
			        if(keyCode == KeyEvent.KEYCODE_ENTER ){
			            //your necessary codes...
			        	skey = txtKey.getText().toString();
			        	gpsstatus();//Enable that line for opening gps
			        	//doSignin();
						return true;
			        }
			        return false;
			}
		});
		
}
public void gpsstatus(){
	if(!checkgps()){
		new AppAlert(activity,Messages.EnableGps) {
		public void okClickListener() {
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		    activity.startActivity(intent);
		    }
		};
	}
	else doSignin();
	}
	
void doSignin() {
		
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		pref = new Preferences(this);
		if (sharedPrefs.getString("key_server_ip", "").toString()!= null 
				&& sharedPrefs.getString("key_server_ip", "").toString().isEmpty()) {
			alert("Server IP is not configured!");
		} else if (Validator.isEmpty(skey)) {
			alert("Please enter valid Key");
		} else if (pref.getTestCounterValue()>=2) {
				alert("For further use of this tool please contact sales@airometricwireless.com."+
				 "You may also re-install the app for 2 more tests.");
			}else {
			//Constants.API_SERVER_URL = "http://"+sharedPrefs.getString("key_server_ip", "").toString()+"/";
			data = pref.loadDeviceInfoData(getApplicationContext());
			//Log.i("GooglemapsActivity page data",data.get("datalength")+","+data);
			sUsername = Constants.defaultUserName+skey;
			sPassword = Constants.defaultPassword+skey;
			String sIMEI = new DeviceUtil(activity).getIMEI();
			doLogin(sUsername, sPassword, sIMEI);
		}
	}

	private void doLogin(final String strUserName, final String strPassword,
			final String strIMEI) {

		showLoading(Messages.PLZ_WAIT_AUTHENTICATE);

		thrdLogin = new Thread() {
			@Override
			public void run() {

				Message msg = new Message();
				Bundle bndle = new Bundle();
				LoginResponse response = null;
				String sResponseCode = "", sResponseDesc = "";
				try {
					APIManager apiClient = new APIManager(activity);
					APIManager.Status status = apiClient.processLogin(
							strUserName, strPassword, strIMEI);
					if (status == APIManager.Status.ERROR) {
						sResponseCode = StringUtils.ERROR_CODE;
						sResponseDesc = apiClient.getErrorMessage();
					} else {
						
						String sResponse = apiClient.getResponse();
						debug("Login Response :: " + sResponse);
						response = Parser.parseLoginResponse(sResponse, activity);
						Preferences preferences = new Preferences(activity);
						preferences.saveMarketplaces(StringUtils.MARKET_PLACES_MAP);
						debug("Login Parsed Response :: " + response.toString());
						if (response.getStatus().trim()
								.equalsIgnoreCase(ResponseCodes.SUCCESS)) {
							try {
								APIManager.Status status1 = apiClient.processDownload(
										strUserName, strPassword, strIMEI, Constants.DOWNLOAD_CONFIG_FILENAME);

								if (status1 == APIManager.Status.ERROR) {
									sResponseCode = StringUtils.ERROR_CODE;
									sResponseDesc = apiClient.getErrorMessage();
								} else {
									String sResponse1 = apiClient.getResponse();
									debug("Download Response :: " + sResponse1);
									if (sResponse1.trim().equalsIgnoreCase(
											ResponseCodes.SUCCESS)) {
										sResponseCode = ResponseCodes.SUCCESS;
										sResponseDesc = "Configuration file downloaded!";

										String sConfigFilePath = activity
												.getFileStreamPath(
														Constants.DOWNLOAD_CONFIG_FILENAME)
												.getAbsolutePath();
										debug("Config Path :: " + sConfigFilePath);
										Log.i("config file path",sConfigFilePath);
										String sConfigContent = FileUtil
												.readFile(sConfigFilePath);
										ConfigXMLParser parser = new ConfigXMLParser(pref);
										debug("sConfigContent ==> " + sConfigContent);
										parser.parseXML(sConfigContent);

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
							sResponseCode = ResponseCodes.SUCCESS;
							sResponseDesc = "Start Test!";
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

		thrdLogin.start();
	}

	/**
	 * Handling the message while progressing
	 */

	private Handler progressHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			try {
				thrdLogin.interrupt();
			} catch (Exception e) {
			}

			// ###################

			String sResponseCode = msg.getData().getString(StringUtils.CODE);
			String sResponseDesc = msg.getData().getString(StringUtils.DESC);
			LoginResponse response = (LoginResponse) msg.getData()
					.getSerializable(StringUtils.RESP_LOGIN);
			L.log("Handler: " + sResponseCode);
			if (sResponseCode.equals(ResponseCodes.SUCCESS)) {
				pref.setLoggedInStatus(true);
				settings.saveLogin(sUsername, sPassword);
				pref.saveUserLevel(response.getLevel());
				
				new AppAlert(activity, sResponseDesc) {
					public void okClickListener() {
						
							String sConfigFilePath = activity.getFileStreamPath(
											Constants.DOWNLOAD_CONFIG_FILENAME)
									.getAbsolutePath();
							debug("Config Path :: " + sConfigFilePath);

							Intent intent = new Intent(activity,TestListActivity.class);
							intent.putExtra(StringUtils.EXTRA_CONFIG_PATH,
									sConfigFilePath);
							intent.putExtra(StringUtils.EXTRA_TEST_MODE,
									StringUtils.TEST_MODE_CONFIG);
							showIntent(intent);
					}
				};

			} else if (sResponseCode.equals(ResponseCodes.AUTH_FAILED)) {
				alert(Messages.INVALID_USERNAME_PWD);
			} else if (sResponseCode.equals(ResponseCodes.ACCESS_DENIED)) {
				alert(Messages.ACCESS_DENIED);
			} else if (sResponseCode.equals(ResponseCodes.FAILURE)) {
				alert("Error while downloading config file...");
			}
			else {
				if (sResponseCode.equals(StringUtils.ERROR_CODE))
					error(sResponseDesc);
				else
					new AppAlert(activity, sResponseCode, sResponseDesc);
			}
			hideLoading();
		}
	};

	public Boolean checkgps(){
		
		locationManager = (LocationManager) activity
                .getSystemService(LOCATION_SERVICE);

		 // getting GPS status
       Boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
       //System.out.print("isGPSEnabled"+isGPSEnabled);
       return isGPSEnabled;
    }
	
	@Override
	public void goBack() {
		new ExitAlert(activity);
	}

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

}