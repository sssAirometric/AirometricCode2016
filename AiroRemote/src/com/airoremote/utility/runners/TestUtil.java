package com.airoremote.utility.runners;

import com.airoremote.AppActivity;
import com.airoremote.classes.TestConfig;
import com.airoremote.config.Constants;
import com.airoremote.config.Messages;
import com.airoremote.config.StringUtils;
import com.airoremote.dialogs.AppAlert;
import com.airoremote.storage.Preferences;
import com.airoremote.storage.SettingsStore;
import com.airoremote.utility.CallUtil;
import com.airoremote.utility.FileUtil;
import com.airoremote.utility.L;

import static com.airoremote.pushnotifications.CommonUtilities.SERVER_STOP_MESSAGE;

public class TestUtil {
	AppActivity activity;
	Preferences pref;
	SettingsStore settings;

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
			if(pref.getisTestAutoStart()==""){
				new AppAlert(activity, Messages.TEST_STOPPED) {
			
				@Override
				public void okClickListener() {
					stopCompleted();
				}
				};
			}
		}
		if (pref.isMTTestRunning()) {
			CallUtil.callDisconnect(activity);
			if (Constants.CurrentTask != null)
				Constants.CurrentTask.cancel(true);
			if (Constants.CurrentRunner != null) {
				Constants.CurrentRunner.cancelTest();
			}
			pref.setMTTestRunningState(false);
			try {
				Thread.sleep(1 * 1000); // Wait for few seconds before upload
			} catch (Exception e) {
			}
			new ResultUploader(activity, pref.getCurrentTestName(),
					StringUtils.TEST_TYPE_CODE_MT,
					settings.getMTDeviceInfoPath(), settings.getMTLogcatPath());
			if(pref.getisTestAutoStart()==""){
				new AppAlert(activity, Messages.TEST_STOPPED) {
			
				@Override
				public void okClickListener() {
					stopCompleted();
				}
				};
			}
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
			if(pref.getisTestAutoStart()==""){
			new AppAlert(activity, Messages.TEST_STOPPED) {
				@Override
				public void okClickListener() {
					stopCompleted();
				}
			};
			}
		}
		if (pref.isUDPTestRunning()) {
			pref.setUDPTestRunningState(false);
			if (Constants.CurrentTask != null)
				Constants.CurrentTask.cancel(true);
			new ResultUploader(activity, pref.getCurrentTestName(),
					StringUtils.TEST_TYPE_CODE_UDP,
					settings.getUDPDeviceInfoPath(),
					settings.getUDPLogcatPath());
			if(pref.getisTestAutoStart()==""){
			new AppAlert(activity, Messages.TEST_STOPPED) {
				@Override
				public void okClickListener() {
					stopCompleted();
				}
			};
			}
		}
		if (pref.isPingTestRunning()) {
			pref.setPingTestRunningState(false);
			if (Constants.CurrentTask != null)
				Constants.CurrentTask.cancel(true);
			new ResultUploader(activity, pref.getCurrentTestName(),
					StringUtils.TEST_TYPE_CODE_PING,
					settings.getPingDeviceInfoPath(),
					settings.getPingLogcatPath());
			if(pref.getisTestAutoStart()==""){
			new AppAlert(activity, Messages.TEST_STOPPED) {
				@Override
				public void okClickListener() {
				}
			};
			}
		}
		if (pref.isBrowserTestRunning()) {
			pref.setBrowserTestRunningState(false);
			if (Constants.CurrentTask != null)
				Constants.CurrentTask.cancel(true);
			new ResultUploader(activity, pref.getCurrentTestName(),
					StringUtils.TEST_TYPE_CODE_BROWSER,
					settings.getBrowserDeviceInfoPath(),
					settings.getBrowserLogcatPath());
			if(pref.getisTestAutoStart()==""){
			new AppAlert(activity, Messages.TEST_STOPPED) {
				@Override
				public void okClickListener() {
					stopCompleted();
				}
			};
			}
		}
		if (pref.isVOIPTestRunning()) {
			pref.setVOIPTestRunningState(false);
			new ResultUploader(activity, pref.getCurrentTestName(),
					StringUtils.TEST_TYPE_CODE_VOIP,
					settings.getVOIPDeviceInfoPath(),
					settings.getVOIPLogcatPath());
			if(pref.getisTestAutoStart()==""){
			new AppAlert(activity, Messages.TEST_STOPPED) {
				@Override
				public void okClickListener() {
					stopCompleted();
				}
			};
			}
		}
	}

	protected void stopCompleted() {
		pref.setisTestAutoStart(SERVER_STOP_MESSAGE);
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
