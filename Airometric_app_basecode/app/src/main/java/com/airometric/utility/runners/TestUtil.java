package com.airometric.utility.runners;

import com.airometric.AppActivity;
import com.airometric.classes.TestConfig;
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
					stopCompleted();
				}
			};
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
			new AppAlert(activity, Messages.TEST_STOPPED) {
				@Override
				public void okClickListener() {
					stopCompleted();
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
					stopCompleted();
				}
			};
		}
		if (pref.isUDPTestRunning()) {
			pref.setUDPTestRunningState(false);
			if (Constants.CurrentTask != null)
				Constants.CurrentTask.cancel(true);
			new ResultUploader(activity, pref.getCurrentTestName(),
					StringUtils.TEST_TYPE_CODE_UDP,
					settings.getUDPDeviceInfoPath(),
					settings.getUDPLogcatPath());
			new AppAlert(activity, Messages.TEST_STOPPED) {
				@Override
				public void okClickListener() {
					stopCompleted();
				}
			};
		}
		if (pref.isPingTestRunning()) {
			pref.setPingTestRunningState(false);
			if (Constants.CurrentTask != null)
				Constants.CurrentTask.cancel(true);
			new ResultUploader(activity, pref.getCurrentTestName(),
					StringUtils.TEST_TYPE_CODE_PING,
					settings.getPingDeviceInfoPath(),
					settings.getPingLogcatPath());
			new AppAlert(activity, Messages.TEST_STOPPED) {
				@Override
				public void okClickListener() {
				}
			};
		}
		if (pref.isBrowserTestRunning()) {
			pref.setBrowserTestRunningState(false);
			if (Constants.CurrentTask != null)
				Constants.CurrentTask.cancel(true);
			new ResultUploader(activity, pref.getCurrentTestName(),
					StringUtils.TEST_TYPE_CODE_BROWSER,
					settings.getBrowserDeviceInfoPath(),
					settings.getBrowserLogcatPath());
			new AppAlert(activity, Messages.TEST_STOPPED) {
				@Override
				public void okClickListener() {
					stopCompleted();
				}
			};
		}
		if (pref.isVOIPTestRunning()) {
			pref.setVOIPTestRunningState(false);
			new ResultUploader(activity, pref.getCurrentTestName(),
					StringUtils.TEST_TYPE_CODE_VOIP,
					settings.getVOIPDeviceInfoPath(),
					settings.getVOIPLogcatPath());
			new AppAlert(activity, Messages.TEST_STOPPED) {
				@Override
				public void okClickListener() {
					stopCompleted();
				}
			};
		}
	}

	protected void stopCompleted() {

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
