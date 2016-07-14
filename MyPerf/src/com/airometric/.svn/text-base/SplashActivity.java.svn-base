package com.airometric;

import com.airometric.config.Constants;
import com.airometric.utility.FileUtil;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
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
					boolean login_status = pref.isLoggedIn();
					debug("login_status - " + login_status);
					if (login_status == false)
						showActivity(LoginActivity.class);
					else {
						if (pref.isAcceptedTermsOfUsage()) {
							if (Constants.STARTUP_ACTIVITY != null)
								showActivity(Constants.STARTUP_ACTIVITY);
							else
								showActivity(TestTypeActivity.class);
						} else
							showActivity(TermsOfUsageActivity.class);
					}
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