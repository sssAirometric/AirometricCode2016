package com.airometric.utility.runners;

import java.util.HashMap;

import android.content.Intent;
import android.util.Log;

import com.airometric.AppActivity;
import com.airometric.GoogleMapsActivity;
import com.airometric.config.Constants;
import com.airometric.config.Messages;
import com.airometric.config.StringUtils;
import com.airometric.dialogs.AppAlert;
import com.airometric.storage.Preferences;
import com.airometric.storage.SettingsStore;
import com.airometric.utility.CallUtil;
import com.airometric.utility.FileUtil;
import com.airometric.utility.L;

public class TestUtil {
	AppActivity activity;
	Preferences pref;
	SettingsStore settings;
	HashMap<String,String> data;
	
	public TestUtil(AppActivity activity) {
		this.activity = activity;
		pref = new Preferences(activity);
		settings = new SettingsStore(activity);
	}

	public void stopTest() {
		pref.setAsTestCanceled(true);

		if (pref.isMOTestRunning()) {
			pref.setMOTestRunningState(false);
			CallUtil.callDisconnect(activity);
			if (Constants.CurrentTask != null) {
				boolean isCanceled = Constants.CurrentTask.cancel(true);
				L.debug("Constants.CurrentTask:: isCanceled - " + isCanceled);
			}
			if (Constants.CurrentRunner != null) {
				Constants.CurrentRunner.cancelTest();
			}
			new ResultUploader(activity, pref.getCurrentTestName(),
					StringUtils.TEST_TYPE_CODE_MO,
					settings.getMODeviceInfoPath(), settings.getMOLogcatPath());
			new AppAlert(activity, Messages.TEST_STOPPED) {
				@Override
				public void okClickListener() {
					stoptesttoGooglemap();
					//Log.i("test util","page stop completed");
					/*pref.saveTestCounterValue(pref.getTestCounterValue()+1);
					//FileUtil.delete(settings.getMODeviceInfoPath());
					//FileUtil.delete(settings.getMOLogcatPath());	
					Intent i = new Intent(activity,GoogleMapsActivity.class);//TaskCompleteedActivity.class
					activity.startActivity(i);
					activity.finish();*/
				}
			};
		}
		
		if (pref.isFTPTestRunning()) {
			pref.setFTPTestRunningState(false);
			if (Constants.CurrentRunner != null) {
				Constants.CurrentRunner.cancelTest();
			}
			if (Constants.CurrentTask != null)
				Constants.CurrentTask.cancel(true);
			new ResultUploader(activity, pref.getCurrentTestName(),
					StringUtils.TEST_TYPE_CODE_FTP,
					settings.getFTPDeviceInfoPath(),
					settings.getFTPLogcatPath());
			new AppAlert(activity, Messages.TEST_STOPPED) {
				@Override
				public void okClickListener() {
					stoptesttoGooglemap();
					/*Log.i("test util","page stop completed");
					pref.saveTestCounterValue(pref.getTestCounterValue()+1);
					//FileUtil.delete(settings.getMODeviceInfoPath());
					//FileUtil.delete(settings.getMOLogcatPath());	
					Intent i = new Intent(activity,GoogleMapsActivity.class);//TaskCompleteedActivity.class
					activity.startActivity(i);
					activity.finish();*/
				}
			};
		}
		/*if(pref.isExternalTestRunning())
		{Log.i("hello m der test util","in ext test");
			pref.setExternalTestRunningState(false);
			new ResultUploader(activity, pref.getCurrentTestName(),
					StringUtils.TEST_TYPE_CODE_EXTERNAL,
					settings.getExternalDeviceInfoPath(),
					settings.getExternalDeviceInfoPath());
			new AppAlert(activity, Messages.TEST_STOPPED) {
				@Override
				public void okClickListener() {
					stoptesttoGooglemap();
				}
			};
		}*/
		
	}

	protected void stopCompleted() {
		
	}

	public void stoptesttoGooglemap(){
		//Log.i("test util","page stop completed");
		pref.saveTestCounterValue(pref.getTestCounterValue()+1);
		//FileUtil.delete(settings.getMODeviceInfoPath());
		//FileUtil.delete(settings.getMOLogcatPath());	
		activity.showActivity(GoogleMapsActivity.class);//TaskCompleteedActivity.class
		activity.finish();
	} 
	public void stopTestForcely() {

		pref.setAsTestCanceled(true);
		if (Constants.CurrentTask != null)
			Constants.CurrentTask.cancel(true);

		if (pref.isMOTestRunning()) {
			CallUtil.callDisconnect(activity);
			pref.setMOTestRunningState(false);
			FileUtil.delete(settings.getMODeviceInfoPath());
		}
		if (pref.isMTTestRunning()) {
			CallUtil.callDisconnect(activity);
			pref.setMTTestRunningState(false);
			FileUtil.delete(settings.getMTDeviceInfoPath());
			FileUtil.delete(settings.getMTLogcatPath());
		}

		if (pref.isFTPTestRunning()) {
			pref.setFTPTestRunningState(false);
			FileUtil.delete(settings.getFTPDeviceInfoPath());
			FileUtil.delete(settings.getFTPLogcatPath());
		}
		if (pref.isUDPTestRunning()) {
			pref.setUDPTestRunningState(false);
			FileUtil.delete(settings.getUDPDeviceInfoPath());
			FileUtil.delete(settings.getUDPLogcatPath());
		}
		if (pref.isPingTestRunning()) {
			pref.setPingTestRunningState(false);
			FileUtil.delete(settings.getPingDeviceInfoPath());
			FileUtil.delete(settings.getPingLogcatPath());
		}
		if (pref.isBrowserTestRunning()) {
			pref.setBrowserTestRunningState(false);
			FileUtil.delete(settings.getBrowserDeviceInfoPath());
			FileUtil.delete(settings.getBrowserLogcatPath());
		}
		if (pref.isVOIPTestRunning()) {
			pref.setVOIPTestRunningState(false);
			FileUtil.delete(settings.getVOIPDeviceInfoPath());
			FileUtil.delete(settings.getVOIPLogcatPath());
		}
		if (pref.isExternalTestRunning()) {
			pref.setExternalTestRunningState(false);
			FileUtil.delete(settings.getExternalDeviceInfoPath());
		}
	}

}
