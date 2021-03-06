package com.airoremote.utility.runners;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.airoremote.Airometric;
import com.airoremote.AppActivity;
import com.airoremote.TestTypeActivity;
import com.airoremote.api.ResponseCodes;
import com.airoremote.classes.FileEvent;
import com.airoremote.classes.TestConfig;
import com.airoremote.classes.UDPTestConfig;
import com.airoremote.config.Constants;
import com.airoremote.config.Messages;
import com.airoremote.config.StringUtils;
import com.airoremote.storage.Preferences;
import com.airoremote.storage.SettingsStore;
import com.airoremote.utility.DeviceUtil;
import com.airoremote.utility.FileUtil;
import com.airoremote.utility.L;
import com.airoremote.utility.NotificationUtil;
import com.airoremote.utility.TimeUtil;
import com.airoremote.utility.UDPSender;

public class UDPTestRunner {

	AppActivity context;
	TestConfig testconfig;
	private Handler handler = new Handler();
	Preferences pref;
	SettingsStore settings;
	long StartTXBytes = 0, StartTXPackets = 0;
	int uid;
	Timer timer;
	UDPTestConfig UDPTestConfigObj;
	Handler TimeoutHandler = new Handler();
	StartTestTask uploadTask;
	DatagramSocket socket;
	Thread thrdUpload;

	public UDPTestRunner(AppActivity context, TestConfig testconfig) {
		this.context = context;
		this.testconfig = testconfig;
		pref = new Preferences(context);
		settings = new SettingsStore(context);
	}

	public void startTest() {

		UDPTestConfigObj = testconfig.getUDPTestConfig();

		/*
		 * String sTestName = testconfig.getTestName() +
		 * StringUtils.TEST_CYCLE_APPEND_FILE + testconfig.getTestCycle();
		 */

		String sTestName = pref.getValue(Preferences.KEY_TEST_NAME, "")
				+ StringUtils.TEST_CYCLE_APPEND_FILE
				+ testconfig.getTestCycle();

		pref.setUDPTestRunningState(true);

		DeviceUtil dv = new DeviceUtil(context);
		String sDeviceInfoXML = dv.getDeviceInfo(
				StringUtils.FILE_CODE_TEST_TYPE_UDP, sTestName,
				pref.getUsername(),
				pref.getValue(Preferences.KEY_SELECTED_MARKET_PLACE_ID, ""));
		L.debug(sDeviceInfoXML);

		String sCurrTime = TimeUtil.getCurrentTimeFilename();

		FileUtil.CURRENT_UDP_TESTTIME = sCurrTime;
		String path = FileUtil.UDP_LOG_DIR + "deviceinfo" + "_" + sTestName
				+ "_" + sCurrTime + ".xml";
		File fle = new File(path);
		if (fle.exists()) {
			fle.delete();
		}
		String dev_info_path = FileUtil.writeToXMLFile(path, sDeviceInfoXML);
		L.debug("Device info initial data written into " + dev_info_path);

		pref.putValue(Preferences.KEY_CURRENT_UDP_DEV_INFO_PATH, dev_info_path);

		String logcat_path = FileUtil.UDP_LOG_DIR
				+ StringUtils.FILE_CODE_TEST_TYPE_UDP + "_" + dv.getIMEI()
				+ "_" + sTestName + "_" + sCurrTime + ".txt";

		File log_fle = new File(logcat_path);

		try {
			log_fle.createNewFile();
		} catch (IOException e) {
		}
		pref.putValue(Preferences.KEY_CURRENT_UDP_LOGCAT_PATH,
				log_fle.getAbsolutePath());
		try {
			Airometric app = (Airometric) context.getApplication();
			app.startListeners(dev_info_path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		NotificationUtil.cancelNotification(context,
				StringUtils.TEST_TYPE_CODE_UDP);

		uploadTask = new StartTestTask();
		uploadTask.execute();
		Constants.CurrentTask = uploadTask;

		NotificationUtil.showRunningNotification(context,
				StringUtils.TEST_TYPE_CODE_UDP);
		context.showActivity(TestTypeActivity.class);
	}

	long no_of_cycles = 0, pass = 0;
	String sServerIP, sFileToUpload;
	int iServerPort;

	void startTesting() {
		no_of_cycles = Long.parseLong(UDPTestConfigObj.sTestCycles);

		sServerIP = UDPTestConfigObj.sServerIP;
		iServerPort = Integer.parseInt(UDPTestConfigObj.sServerPort);
		sFileToUpload = UDPTestConfigObj.sFileToUpload;
		DeviceUtil.clearLogcat();

		thrdUpload = new Thread() {
			public void run() {
				for (int cyc_i = 0; cyc_i < no_of_cycles; cyc_i++) {
					context.print("startTesting()... cyc_i  -> " + cyc_i);
					doStartUpload();
				}
				CompletedHandler.sendEmptyMessage(0);
			}
		};
		thrdUpload.start();

	}

	protected void doStartUpload() {

		Message msg = new Message();
		Bundle bndle = new Bundle();

		String sResponseCode = "", sResponseDesc = "";

		try {

			context.print("doUpload()... sFileToUpload  -> " + sFileToUpload);

			timer = new Timer();
			uid = android.os.Process.myUid();
			StartTXBytes = TrafficStats.getUidUdpTxBytes(uid);
			StartTXPackets = TrafficStats.getUidUdpTxPackets(uid);
			context.debug("Initial TX bytes - " + StartTXBytes
					+ ", Initial TX Packets - " + StartTXPackets);
			timer.schedule(new UploadTimeTask(), 0,
					Constants.UDP_LOG_INTERVAL * 1000);
			createConnection();
			sResponseCode = ResponseCodes.SUCCESS;

		} catch (UnknownHostException e) {

			e.printStackTrace();
			sResponseCode = StringUtils.ERROR_CODE;
			sResponseDesc = Messages.UDP_CONNECTION_FAILED;

		} catch (SocketException e) {

			e.printStackTrace();
			sResponseCode = StringUtils.ERROR_CODE;
			sResponseDesc = Messages.UDP_UPLOAD_FAILED + "-"
					+ e.getLocalizedMessage();

		} catch (SocketTimeoutException e) {

			e.printStackTrace();
			sResponseCode = StringUtils.ERROR_CODE;
			sResponseDesc = Messages.UDP_CONNECTION_TIMEOUT;

		} catch (Exception e) {
			e.printStackTrace();
			sResponseCode = StringUtils.ERROR_CODE;
			sResponseDesc = Messages.UDP_UPLOAD_FAILED + "-"
					+ e.getLocalizedMessage();
		}

		bndle.putString(StringUtils.CODE, sResponseCode);
		bndle.putString(StringUtils.DESC, sResponseDesc);
		msg.setData(bndle);
		UploadHandler.sendMessage(msg);
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

	class UploadTimeTask extends TimerTask {

		public UploadTimeTask() {
		}

		public void run() {
			if (pref.isUDPTestRunning()) {
				long CurrTXBytes = TrafficStats.getUidUdpTxBytes(uid);
				long CurrTXPackets = TrafficStats.getUidUdpTxPackets(uid);
				context.debug("StartTXBytes bytes - " + StartTXBytes
						+ ", CurrTXBytes - " + CurrTXBytes
						+ ", CurrTXPackets - " + CurrTXPackets);
				long TXBytes = CurrTXBytes - StartTXBytes;
				long TXPackets = CurrTXPackets - StartTXPackets;
				L.log_udp(settings.getUDPLogcatPath(), TXBytes, TXPackets);

			} else {
				timer.cancel();
				try {
					if (socket != null && !socket.isConnected())
						socket.disconnect();
				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					if (socket != null && !socket.isClosed())
						socket.close();
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
	}

	private Handler UploadHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			// ###################
			String sResponseCode = msg.getData().getString(StringUtils.CODE);
			String sResponseDesc = msg.getData().getString(StringUtils.DESC);

			if (sResponseCode.equals(ResponseCodes.SUCCESS)) {
				L.debug("File upload success");
			} else {
				L.debug(sResponseDesc);
				try {
					thrdUpload.interrupt();
				} catch (Exception e) {
				}
				timer.cancel();
				if (!pref.isTestCanceled() && pref.isTestRunning()) {

					pref.setUDPTestRunningState(false);

					/*
					 * NotificationUtil.showFinishedNotification(context,
					 * StringUtils.TEST_TYPE_CODE_UDP, testconfig.getTestName(),
					 * sResponseDesc);
					 */

					
					NotificationUtil.showFinishedNotification(context,
							StringUtils.TEST_TYPE_CODE_UDP, pref.getValue(Preferences.KEY_TEST_NAME, "")
							 , sResponseDesc);
					
					DeviceUtil.updateTestScreen();
					doResultUploaded(sResponseCode, sResponseDesc);
				}
			}
		}
	};

	private Handler CompletedHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			if (!pref.isTestCanceled() && pref.isTestRunning()) {
				L.debug("CompletedHandler");

				pref.setUDPTestRunningState(false);

				new ResultUploader(context, pref.getValue(Preferences.KEY_TEST_NAME, "")
						+ StringUtils.TEST_CYCLE_APPEND_FILE
						+ testconfig.getTestCycle(),
						StringUtils.TEST_TYPE_CODE_UDP,
						settings.getUDPDeviceInfoPath(),
						settings.getUDPLogcatPath()) {
					public void resultUploaded(String code, String desc) {
						doResultUploaded(code, desc);
					};
				};
			}
		}
	};

	void send() throws Exception {
		UDPSender sender = null;
		sender = new UDPSender(InetAddress.getByName(sServerIP), iServerPort);

		String path = sFileToUpload;
		sender.sendFile(new File(path));

	}

	FileEvent event;

	public void createConnection() throws Exception {

		L.debug("UDP.CreateConnection():: Server IP: " + sServerIP
				+ ", Port : " + iServerPort);
		socket = new DatagramSocket();

		InetAddress IPAddress = InetAddress.getByName(sServerIP);

		byte[] incomingData = new byte[1024];

		event = getFileEvent();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		ObjectOutputStream os = new ObjectOutputStream(outputStream);

		os.writeObject(event);

		byte[] data = outputStream.toByteArray();

		DatagramPacket sendPacket = new DatagramPacket(data, data.length,
				IPAddress, iServerPort);

		socket.setSoTimeout(60 * 1000);
		socket.send(sendPacket);

		System.out.println("File sent from client");

		DatagramPacket incomingPacket = new DatagramPacket(incomingData,
				incomingData.length);

		// socket.setSoTimeout(60 * 1000);
		socket.receive(incomingPacket);

		String response = new String(incomingPacket.getData());

		System.out.println("Response from server:" + response);

		Thread.sleep(2000);

	}

	Runnable TimeoutRunnable = new Runnable() {

		@Override
		public void run() {
			// Timeout occured
			L.debug("Timeout occurred");
		}
	};

	public FileEvent getFileEvent() throws Exception {

		FileEvent fileEvent = new FileEvent();

		String fileName = sFileToUpload.substring(
				sFileToUpload.lastIndexOf("/") + 1, sFileToUpload.length());

		fileEvent.setFilename(fileName);

		fileEvent.setSourceDirectory(sFileToUpload);

		File file = new File(sFileToUpload);

		if (file.isFile()) {

			DataInputStream diStream = new DataInputStream(new FileInputStream(
					file));

			long len = (int) file.length();

			byte[] fileBytes = new byte[(int) len];

			int read = 0;

			int numRead = 0;

			while (read < fileBytes.length
					&& (numRead = diStream.read(fileBytes, read,

					fileBytes.length - read)) >= 0) {

				read = read + numRead;

			}

			fileEvent.setFileSize(len);

			fileEvent.setFileData(fileBytes);

			fileEvent.setStatus("Success");

		} else {
			fileEvent.setStatus("Error");

			throw new Exception("Upload file not available");

		}

		return fileEvent;

	}

	public void doResultUploaded(String code, String desc) {

	};
}
