package com.airometric;

import com.airometric.api.APIManager;
import com.airometric.api.ResponseCodes;
import com.airometric.config.Constants;
import com.airometric.config.StringUtils;
import com.airometric.parser.ConfigXMLParser;
import com.airometric.utility.DBUtil;
import com.airometric.utility.DeviceUtil;
import com.airometric.utility.FileUtil;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;

public class SplashActivity extends AppActivity {

	protected int _splashTime = 3000;
	Handler mHandler = new Handler();
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		
		
		if (savedInstanceState == null) {
			try {

				FileUtil.init();
			} catch (Exception e) {

			}
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					showActivity(LoginActivity.class);
					/*boolean login_status = pref.isLoggedIn();
					debug("login_status - " + login_status);
					if (login_status == false)
						showActivity(LoginActivity.class);
					else {
						String sIMEI = new DeviceUtil(activity).getIMEI();
						String sUserid = pref.getUsername();
						String sPwd = pref.getPassword();
						/*if (pref.isAcceptedTermsOfUsage()) {
							if (Constants.STARTUP_ACTIVITY != null)
								showActivity(Constants.STARTUP_ACTIVITY);
							else*/
								//showActivity(TestListActivity.class);
						/*} else
							showActivity(TermsOfUsageActivity.class);
					}*/
				}
			}, 3000);
		}
	}

	@Override
	public void goBack() {
		return;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}
}