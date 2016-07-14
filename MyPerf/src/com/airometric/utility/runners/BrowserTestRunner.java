package com.airometric.utility.runners;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.airometric.Airometrics;
import com.airometric.AppActivity;
import com.airometric.TestTypeActivity;
import com.airometric.api.ResponseCodes;
import com.airometric.classes.BrowserTestConfig;
import com.airometric.classes.TestConfig;
import com.airometric.config.Constants;
import com.airometric.config.Messages;
import com.airometric.config.StringUtils;
import com.airometric.storage.Preferences;
import com.airometric.storage.SettingsStore;
import com.airometric.utility.DeviceUtil;
import com.airometric.utility.FileUtil;
import com.airometric.utility.L;
import com.airometric.utility.NotificationUtil;
import com.airometric.utility.TimeUtil;

public class BrowserTestRunner {

	AppActivity context;
	TestConfig testconfig;
	private Handler handler = new Handler();
	Preferences pref;
	SettingsStore settings;
	String sPageURL, sNoOfCycles;
	BrowserTestConfig BrowserTestConfigObj;
	long StartRXBytes = 0, StartRXSegments = 0;
	int uid;
	Timer timer;
	StartTestTask uploadTask;
	
	public BrowserTestRunner(AppActivity context, TestConfig testconfig) {
		this.context = context;
		this.testconfig = testconfig;
		pref = new Preferences(context);
		settings = new SettingsStore(context);
	}

	public void startTest() {

		BrowserTestConfigObj = testconfig.getBrowserTestConfig();
		String sTestName = testconfig.getTestName();

		pref.setBrowserTestRunningState(true);

		DeviceUtil dv = new DeviceUtil(context);
		String sDeviceInfoXML = dv.getDeviceInfo(
				StringUtils.FILE_CODE_TEST_TYPE_BROWSER, sTestName,
				pref.getUsername(),"");//that gives me error coz only 3 parameters passed but it needs four so i add last one.
		L.debug(sDeviceInfoXML);

		String sCurrTime = TimeUtil.getCurrentTimeFilename();

		FileUtil.CURRENT_BROWSER_TESTTIME = sCurrTime;
		String path = FileUtil.BROWSER_LOG_DIR + "deviceinfo" + "_" + sTestName
				+ "_" + sCurrTime + ".xml";
		File fle = new File(path);
		if (fle.exists()) {
			fle.delete();
		}
		L.debug("Device info path -> " + path);
		String dev_info_path = FileUtil.writeToXMLFile(path, sDeviceInfoXML);
		L.debug("Device info initial data written into " + dev_info_path);

		pref.putValue(Preferences.KEY_CURRENT_BROWSER_DEV_INFO_PATH,
				dev_info_path);

		String logcat_path = FileUtil.BROWSER_LOG_DIR
				+ StringUtils.FILE_CODE_TEST_TYPE_BROWSER + "_" + dv.getIMEI()
				+ "_" + sTestName + "_" + sCurrTime + ".txt";

		File log_fle = new File(logcat_path);

		if (log_fle.exists()) {
			log_fle.delete();
		}
		pref.putValue(Preferences.KEY_CURRENT_BROWSER_LOGCAT_PATH,
				log_fle.getAbsolutePath());
		try {
			Airometrics app = (Airometrics) context.getApplication();
			app.startListeners(dev_info_path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		NotificationUtil.cancelNotification(context, StringUtils.TEST_TYPE_CODE_BROWSER);

		uploadTask = new StartTestTask();
		uploadTask.execute();
		Constants.CurrentTask = uploadTask;
		
		NotificationUtil.showRunningNotification(context,
				StringUtils.TEST_TYPE_CODE_BROWSER);
		context.showActivity(TestTypeActivity.class);
	}

	long no_of_cycles = 0, pass = 0;

	void startTesting() {
		sPageURL = BrowserTestConfigObj.sPageURL;
		sNoOfCycles = BrowserTestConfigObj.sCycles;
		no_of_cycles = Long.parseLong(sNoOfCycles);

		context.print("startTesting()... sPageURL  -> " + sPageURL
				+ ", NoOfCycles  -> " + sNoOfCycles);
		DeviceUtil.clearLogcat();

		// new Thread() {
		// public void run() {
		// doLoad();
		// }
		// }.start();

		handler.post(new Runnable() {

			@Override
			public void run() {
				doLoad();
			}
		});
	}

	int getBrowserId() {
		final PackageManager pm = context.getPackageManager();
		List<ApplicationInfo> packages = pm
				.getInstalledApplications(PackageManager.GET_META_DATA);
		int UID = 0;
		for (ApplicationInfo packageInfo : packages) {
			L.debug(packageInfo.name + ";; package -->"
					+ packageInfo.packageName);
			if (packageInfo.packageName.indexOf("com.android.browser") != -1
					|| packageInfo.packageName
							.indexOf("com.google.android.browse") != -1) {
				UID = packageInfo.uid;
			}
		}
		return UID;
	}

	String sResponseCode = "", sResponseDesc = "";

	boolean isPageLoaded = false;

	protected void doLoad() {

		final Message msg = new Message();
		final Bundle bndle = new Bundle();
		sResponseCode = "";
		sResponseDesc = "";
		isPageLoaded = false;
		try {

			timer = new Timer();
			uid = android.os.Process.myUid();
			// uid = getBrowserId();
			StartRXBytes = TrafficStats.getUidTcpRxBytes(uid);
			StartRXSegments = TrafficStats.getUidTcpRxSegments(uid);
			context.debug("Initial RX bytes - " + StartRXBytes
					+ ", Initial RX Segments - " + StartRXSegments);
			timer.schedule(new PageLoadTimeTask(), 0,
					Constants.BROWSER_LOG_INTERVAL * 1000);

			WebView webview = new WebView(context);
			webview.getSettings().setJavaScriptEnabled(true);
			webview.getSettings().setPluginState(PluginState.ON);
			webview.getSettings().setLoadWithOverviewMode(true);
			webview.getSettings().setUseWideViewPort(true);
			webview.loadUrl(sPageURL);
			boolean timeout = true;
			webview.setWebChromeClient(new MyWebChromeClient());
			webview.setWebViewClient(new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					view.loadUrl(url);
					L.debug("shouldOverrideUrlLoading -> " + url);
					return true;
				}

				@Override
				public void onReceivedError(WebView view, int errorCode,
						String description, String failingUrl) {
					L.debug("Oh no! " + description);
				}

				@Override
				public void onPageStarted(WebView view, String url,
						Bitmap favicon) {
					super.onPageStarted(view, url, favicon);
					L.debug("onPageStarted -> " + url);
				}

				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
					L.debug("onPageFinished -> " + url);
					// if (!isPageLoaded)
					{
						L.debug("URL Load Finished -> " + url);
						sResponseCode = ResponseCodes.SUCCESS;
						bndle.putString(StringUtils.CODE, sResponseCode);
						bndle.putString(StringUtils.DESC, sResponseDesc);
						msg.setData(bndle);
						progressHandler.sendMessage(msg);
						isPageLoaded = true;
					}
				}
			});
			// context.showActivity(WebviewActivity.class);
		} catch (Exception e) {
			e.printStackTrace();
			sResponseCode = StringUtils.ERROR_CODE;
			sResponseDesc = Messages.BROWER_TEST_FAILED;
			bndle.putString(StringUtils.CODE, sResponseCode);
			bndle.putString(StringUtils.DESC, sResponseDesc);
			msg.setData(bndle);
			progressHandler.sendMessage(msg);
		}

	}

	private class MyWebChromeClient extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			super.onProgressChanged(view, newProgress);
			L.debug("onProgressChanged -> " + newProgress);
		}

	}

	class PageLoadTimeTask extends TimerTask {

		public PageLoadTimeTask() {
		}

		public void run() {
			if (pref.isBrowserTestRunning()) {
				long CurrRXBytes = TrafficStats.getUidTcpRxBytes(uid);
				long CurrRXSegments = TrafficStats.getUidTcpRxSegments(uid);
				context.debug("StartRXBytes bytes - " + StartRXBytes
						+ ", CurrRXBytes - " + CurrRXBytes
						+ ", CurrRXSegments - " + CurrRXSegments);
				long RXBytes = CurrRXBytes - StartRXBytes;
				long RXSegments = CurrRXSegments - StartRXSegments;
				L.log_browser("Current RX bytes - " + RXBytes);
				L.log_browser("Current RXSegments - " + RXSegments);
			} else
				timer.cancel();
		}
	}

	private class StartTestTask extends AsyncTask<URL, Integer, String> {

		public StartTestTask() {
		}

		protected void onProgressUpdate(Integer... progress) {

		}

		protected void onPostExecute(Long result) {

		}

		@Override
		protected String doInBackground(URL... urls) {
			startTesting();
			return "Started";
		}
	}

	private Handler progressHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			timer.cancel();

			// ###################
			String sResponseCode = msg.getData().getString(StringUtils.CODE);
			String sResponseDesc = msg.getData().getString(StringUtils.DESC);

			if (sResponseCode.equals(ResponseCodes.SUCCESS)) {
				L.debug("Page load success");
				doLoadCompleted();
			} else {
				L.debug(sResponseDesc);
				pref.setBrowserTestRunningState(false);

				NotificationUtil.showFinishedNotification(context,
						StringUtils.TEST_TYPE_CODE_BROWSER,
						testconfig.getTestName(), sResponseDesc);

				DeviceUtil.updateTestScreen();
				doResultUploaded(sResponseCode, sResponseDesc);
			}
		}
	};

	void doLoadCompleted() {
		if (!pref.isTestCanceled() && pref.isTestRunning()) {
			L.debug("doLoadCompleted...");
			pref.setBrowserTestRunningState(false);

			new ResultUploader(context, testconfig.getTestName(),
					StringUtils.TEST_TYPE_CODE_BROWSER,
					settings.getBrowserDeviceInfoPath(),
					settings.getBrowserLogcatPath()) {
				public void resultUploaded(String code, String desc) {
					doResultUploaded(code, desc);
				};
			};

		}
	}

	public void doResultUploaded(String code, String desc) {

	};
}
