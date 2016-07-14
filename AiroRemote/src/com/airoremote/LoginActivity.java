package com.airoremote;

import static com.airoremote.pushnotifications.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.airoremote.pushnotifications.CommonUtilities.EXTRA_MESSAGE;
import static com.airoremote.pushnotifications.CommonUtilities.SENDER_ID;
import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.test.PerformanceTestCase;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.airoremote.R;
import com.airoremote.api.APIManager;
import com.airoremote.api.ResponseCodes;
import com.airoremote.classes.LoginResponse;
import com.airoremote.config.Constants;
import com.airoremote.config.Messages;
import com.airoremote.config.StringUtils;
import com.airoremote.dialogs.AppAlert;
import com.airoremote.dialogs.ExitAlert;
import com.airoremote.parser.Parser;
import com.airoremote.preferences.SettingsActivity;
import com.airoremote.pushnotifications.MainActivity;
import com.airoremote.pushnotifications.ServerUtilities;
import com.airoremote.pushnotifications.WakeLocker;
import com.airoremote.storage.Preferences;
import com.airoremote.utility.DeviceUtil;
import com.airoremote.utility.L;
import com.airoremote.utility.Validator;
import com.google.android.gcm.GCMRegistrar;

public class LoginActivity extends AppActivity {

	private Button btnSignIn, btnSignUp;
	private EditText txtUsername, txtPassword;
	private TextView lblForgotPassword;
	private Thread thrdLogin;
	private String sUsername, sPassword,sIMEI;
	//protected Preferences pref;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// To setup the title bar
		setAppTitle(R.layout.login);
		activity = this;
		pref = new Preferences(this);
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

		btnSignIn = (Button) findViewById(R.id.linearSubContent);
		btnSignUp = (Button) findViewById(R.id.btnSignUp);

		btnSignIn.setOnClickListener(clickListener);
		// btnSignUp.setOnClickListener(clickListener);

		txtUsername = (EditText) findViewById(R.id.txtUsername);
		txtPassword = (EditText) findViewById(R.id.txtPassword);
		lblForgotPassword = (TextView) findViewById(R.id.lblForgotPassword);

		lblForgotPassword
				.setText(Html
						.fromHtml("<font color=\"#0000FF\"><u>Forgot Password</u></font>"));
		lblForgotPassword.setOnClickListener(clickListener);

		if (Constants.FILL_DATA) {
			txtUsername.setText(Constants.TEST_USERNAME);
			txtPassword.setText(Constants.TEST_PASSWORD);
		}
		txtPassword
				.setOnEditorActionListener(new EditText.OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView arg0, int actionId,
							KeyEvent arg2) {
						if (actionId == EditorInfo.IME_ACTION_DONE
								|| actionId == EditorInfo.IME_ACTION_GO
								|| actionId == EditorInfo.IME_ACTION_SEND) {
							doSignin();
						}
						return false;
					}
				});
	}

	private View.OnClickListener clickListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {

			if (view == btnSignIn) {
				doSignin();
			} else if (view == btnSignUp) {
				showActivity(TermsOfUsageActivity.class);
			} else if (view == lblForgotPassword) {
				showActivity(ForgotPasswordActivity.class);
			}

		}

	};

	void doSignin() {

		sUsername = txtUsername.getText().toString();
		sPassword = txtPassword.getText().toString();

		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		if (sharedPrefs.getString("key_server_ip", "").toString()!= null && sharedPrefs.getString("key_server_ip", "").toString().isEmpty()) {
			alert("Server IP is not configured!");
		} else if (Validator.isEmpty(sUsername)) {
			alert("Please enter valid Username");
		} else if (Validator.isEmpty(sPassword)) {
			alert("Please enter valid Password");
		} else {
			sIMEI = new DeviceUtil(activity).getIMEI();
			//System.out.println("API_SERVER_URL "+Constants.API_SERVER_URL.replace(":8080", ""));
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
							sResponseCode = ResponseCodes.SUCCESS;
							sResponseDesc = "Files uploaded!";
							// Code written by Ankit to register on GCM server & generate regid
							//System.out.println("pref.getisRegisteredwithGCM() "+!pref.getisRegisteredwithGCM());
							if(!pref.getisRegisteredwithGCM()){
								//System.out.println("pref.getisRegisteredwithGCM()111 "+!pref.getisRegisteredwithGCM());	
								RegisterOnGCM();
							/*Intent i = new Intent(activity, MainActivity.class);
							// Registering user on our server					
							// Sending registraiton details to MainActivity
							i.putExtra("name", pref.getUsername());
							i.putExtra("Password", pref.getPassword());
							i.putExtra("Imei", strIMEI);
							startActivity(i);
							finish();*/
							}
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
				pref.setisTestAutoStart("");
				pref.setLoggedInStatus(true);
				settings.saveLogin(sUsername, sPassword);
				pref.saveUserLevel(response.getLevel());

				if (response.getAcceptedTerms().equals(
						ResponseCodes.TERMS_ACCEPTED_YES))
					pref.setAsTermsOfUsageAccepted(true);
				else
					pref.setAsTermsOfUsageAccepted(false);

				if (pref.isAcceptedTermsOfUsage())
					showActivity(TestTypeActivity.class);
				else
					showActivity(TermsOfUsageActivity.class);
				
			} else if (sResponseCode.equals(ResponseCodes.AUTH_FAILED)) {
				alert(Messages.INVALID_USERNAME_PWD);
			} else if (sResponseCode.equals(ResponseCodes.ACCESS_DENIED)) {
				alert(Messages.ACCESS_DENIED);
			} else if (sResponseCode.equals(ResponseCodes.FAILURE)) {
				alert("Invalid username/password!");
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

	public void RegisterOnGCM()
	{
		// Make sure the device has the proper dependencies.
				GCMRegistrar.checkDevice(this);
				
				// Make sure the manifest was properly set - comment out this line
				// while developing the app, then uncomment it when it's ready.
				GCMRegistrar.checkManifest(this);

				registerReceiver(mHandleMessageReceiver, new IntentFilter(
						DISPLAY_MESSAGE_ACTION));
				
				// Get GCM registration id
				String regId = GCMRegistrar.getRegistrationId(this);
				System.out.println("regid status "+regId.equals(""));
				// Check if regid already presents
				if (regId.equals("")) {
					// Registration is not present, register now with GCM			
					GCMRegistrar.register(this, SENDER_ID);
				} else {
					// Device is already registered on GCM
					System.out.println("GCMRegistrar.isRegisteredOnServer(this) "+ GCMRegistrar.isRegisteredOnServer(this) +":"+regId);
					if (GCMRegistrar.isRegisteredOnServer(this)) {
						// Skips registration.				
						//pref.setisRegisteredwithGCM(true);
						//Toast.makeText(this, "Already registered with GCM", Toast.LENGTH_LONG).show();
					} else {
						ServerUtilities.register(this, pref.getUsername(), pref.getPassword(), regId, sIMEI);
						//pref.setisRegisteredwithGCM(true);
					}
				}
				pref.setisRegisteredwithGCM(true);
	}
	/**
	 * Receiving push messages
	 * */
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			// Waking up mobile if it is sleeping
			WakeLocker.acquire(activity);
			
			/**
			 * Take appropriate action on this message
			 * depending upon your app requirement
			 * For now i am just displaying it on the screen
			 * */
			// Showing received message
			//lblMessage.append(newMessage + "\n");			
			//Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();
			System.out.println("newMessage :"+newMessage);
			// Releasing wake lock
			WakeLocker.release();
		}
	};
	
	@Override
	protected void onDestroy() {
		/*if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}*/
		try {
			unregisterReceiver(mHandleMessageReceiver);
			GCMRegistrar.onDestroy(this);
		} catch (Exception e) {
			Log.e("UnRegister Receiver Error", "> " + e.getMessage());
		}
		super.onDestroy();
	}
}