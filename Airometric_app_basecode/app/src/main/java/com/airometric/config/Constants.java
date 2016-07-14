package com.airometric.config;

import android.app.Activity;
import android.os.AsyncTask;

import com.airometric.AppActivity;
import com.airometric.utility.runners.Runner;
import com.airometric.utility.runners.TestRunner;

public class Constants {

	public static final String APP_NAME = "Airometric";
	public static final boolean DEBUG = false;
	public static final boolean TESTING = false;
	public static final boolean FILL_DATA = false;
	public static final Class STARTUP_ACTIVITY = null;

	public static final boolean isProdBuild = false;
	public static final boolean isQABuild = true;
	public static final boolean isDevBuild = false;

	public static final String CRASH_REPORT_EMAIL_ID = "ankitjain@ssism.org";
	public static final boolean DELETE_FILES_AFTER_UPLOAD = true; 
	public static final boolean AFTER_UPLOAD_RESULTS = true;
	public static final int MT_END_BUFFER_TIME_SECONDS = 15;
	public static final int TEST_CYCLES_MAX_COUNT = 20;
	public static final int TEST_CYCLES_MAX_COUNT_FTP_UDP = 100;
	public static final int DEV_INFO_LISTEN_DURATION = 10; // secs
	public static final int FTP_LOG_INTERVAL = 1;
	public static final int UDP_LOG_INTERVAL = 1;
	public static final int BROWSER_LOG_INTERVAL = 1;
	public static boolean IS_FAILED_UPLOAD_RUNNING = false;

	public static String API_SERVER_URL = "";

	/*public static String API_SERVER_PROD_URL = "http://50.112.27.46:8080/";
	public static String API_SERVER_QA_URL = "http://122.181.151.118:8280/";   
	public static String API_SERVER_DEV_URL = "http://122.181.151.113:8080/";
	*/
	
	// Bangalore local network equivalent
	/*public static String API_SERVER_PROD_URL = "http://50.112.27.46:8080/";
	public static String API_SERVER_QA_URL = "http://192.168.1.148:8280/";
	public static String API_SERVER_DEV_URL = "http://192.168.1.40:8080/";*/

	public static Activity root;
	public static AppActivity CurrentActivity;
	public static Runner CurrentRunner;
	public static AsyncTask CurrentTask;
	public static TestRunner CurrentTestRunner;

	/*static {
		if (isProdBuild)
			API_SERVER_URL = API_SERVER_PROD_URL;
		else if (isQABuild)
			API_SERVER_URL = API_SERVER_QA_URL;
		else if (isDevBuild)
			API_SERVER_URL = API_SERVER_DEV_URL;
		L.debug("API_SERVER_URL = " + API_SERVER_URL);
	}*/
	public static final String DOWNLOAD_CONFIG_FILENAME = "config.xml";

	public static final String TEST_USERNAME = "qa123"; // syed
	public static final String TEST_PASSWORD = "qa123"; // syed

	// MO
	public static final String TEST_MO_NAME = "test1min10sec2min";
	public static final String TEST_MO_PHONE = "+919943920092"; //919710759409 - 8939634056
	public static final String TEST_MO_CALL_DURATION = "1";
	public static final String TEST_MO_PAUSE_TIME = "10";
	public static final String TEST_MO_DURATION = "2";
	// MT
	public static final String TEST_MT_TEST_DURATION = "2";
	public static final String TEST_MT_CALL_DURATION = "1";

	/*public static final String TEST_FTP_SERVER = "122.181.151.105";
	public static final String TEST_FTP_USER = "Airometric";
	public static final String TEST_FTP_PWD = "@!r0Metr!c";
	public static final String TEST_FTP_UPLOAD_PATH = "/UploadTo";
	public static final String TEST_FTP_DOWNLOAD_PATH = "/DownloadFrom/download.jpg";
	public static final String TEST_FTP_CYCLES = "1"; 
	*/
	
	public static final String TEST_FTP_SERVER = "74.203.63.10";	
	public static final String TEST_FTP_USER = "sleepy";
	public static final String TEST_FTP_PWD = "password123";
	public static final String TEST_FTP_UPLOAD_PATH = "/UPLOAD_1";
	public static final String TEST_FTP_DOWNLOAD_PATH = "//500KB.zip";
	public static final String TEST_FTP_CYCLES = "1";
	
	public static final String TEST_UDP_SERVER = "192.168.100.111"; // 122.181.151.114
																	// //192.168.100.111
	public static final String TEST_UDP_SERVER_PORT = "7701";
	public static final String TEST_UDP_CYCLES = "1";

	public static final String TEST_BROWSER_SERVER = "http://www.yahoo.co.in";
	public static final String TEST_BROWSER_CYCLES = "1";

	public static final String TEST_PING_SERVER = "www.ssism.org";
	public static final String TEST_PING_CYCLES = "1";

	public static final String TEST_VOIP_DURATION = "2";
	
	public static final int OS_VERSION_JELLY_BEAN = 16;
	public static final int OS_VERSION_JELLY_BEAN_MR1 = 17;
	public static final int OS_VERSION_JELLY_BEAN_MR2 = 18;
	public static final int OS_VERSION_KIT_KAT = 19;	
	public static final int OS_VERSION_LOLLIPOP = 21;
	public static final int OS_VERSION_LOLLIPOP_MR2 = 22;
	public static final int OS_VERSION_MARSHMALLOW = 23;

}
