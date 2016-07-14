package com.airoremote.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;

import com.airoremote.AppActivity;
import com.airoremote.TermsOfUsageActivity;
import com.airoremote.TestTypeActivity;
import com.airoremote.classes.LoginResponse;
import com.airoremote.config.Constants;
import com.airoremote.config.Messages;
import com.airoremote.config.StringUtils;
import com.airoremote.dialogs.AppAlert;
import com.airoremote.parser.Parser;
import com.airoremote.storage.Preferences;
import com.airoremote.utility.DeviceUtil;
import com.airoremote.utility.L;
import com.airoremote.utility.Validator;

/**
 * Utitility class to send request to server and returns the response from the
 * server.
 * 
 */
public class APIManager {

	private AppActivity activity = null;
	private Context context = null;
	public static String API_SERVICE_URL = Constants.API_SERVER_URL
			+ "Airometric/rest/file/";
	private String apiMethodName, apiParams;
	private LinkedHashMap<String, String> apiParamsList;
	private String errorMsg = "", errorStackMsg = "";
	private Exception api_exception;
	private String response;
	private Status status;

	// UtilityActivity utilActivity = new UtilityActivity();

	public static int numberOfAttempts = 6;
	public static int numberOfDays = 6;
	private int intConnectionTimeOut = 60000;

	public String sResponseCode;

	// Command codes
	public static final String CMD_ACCEPT_TERMS = "updateTerms";
	public static final String CMD_LOGIN = "validate";
	public static final String CMD_UPLOAD = "uploadTest";
	public static final String CMD_DOWNLOAD = "download";
	public static final String CMD_UPDATETESTSTATUS = "updateteststatus";
	
	private Thread thrdCheckout = null;
	private boolean isActive = true;

	/**
	 * Enumeration values for the request status.
	 * 
	 */
	public enum Status {
		NONE, SUCCESS, FAILED, ERROR
	};

	public APIManager(AppActivity activity) {
		this.activity = activity;
	}

	public APIManager(Context context) {
		this.context = context;
	}

	/**
	 * Constructor to get the params and to initialize the values.
	 * 
	 * @param methodName
	 *            The type of request to be send to server.
	 * @param paramsList
	 *            The list of parameters and its values as HashMap
	 * @param activity
	 *            The Activity which is going to send request.
	 */
	public APIManager(String methodName,
			LinkedHashMap<String, String> paramsList, AppActivity activity) {

		response = "";
		this.apiMethodName = methodName;
		this.apiParamsList = paramsList;
		this.activity = activity;
		status = Status.NONE;
		errorMsg = "";
		errorStackMsg = "";

		api_exception = null;
	}

	/**
	 * Method which initialize the request to server and process. It will
	 * returns the status whether the request success of error.F
	 * 
	 * @return Status value.
	 */
	public Status processAcceptTermsOfUsage(String userName) {
		this.status = Status.NONE;

		if (!isInternetAvailable()) {
			this.status = Status.ERROR;
			this.errorMsg = Messages.NO_INTERNET;
			return this.status;
		}

		String strLoginUrl = API_SERVICE_URL + CMD_ACCEPT_TERMS;
		log("Accept Terms URL: " + strLoginUrl);
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(strLoginUrl);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("userName", userName));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse responseObj = httpclient.execute(httppost);
			int status_code = responseObj.getStatusLine().getStatusCode();
			log("status_code : " + status_code);
			if (status_code == 200) {
				HttpEntity resEntity = responseObj.getEntity();
				response = EntityUtils.toString(resEntity);
			}
			log("RESPONSE:");
			log("$$$$$$$$$$$$$$$$$$$$");
			log(response);
			log("$$$$$$$$$$$$$$$$$$$$");
			if (Validator.isEmpty(response)) {

				this.status = Status.ERROR;
				this.errorMsg = Messages.SERVER_NOT_AVAILABLE;
			} else {
				this.status = Status.SUCCESS;
			}
		} catch (UnknownHostException uhEx) {
			L.error(uhEx);
			this.status = Status.ERROR;
			this.errorMsg = Messages.NO_INTERNET;
		} catch (HttpHostConnectException hhcEx) {
			L.error(hhcEx);
			this.status = Status.ERROR;
			this.errorMsg = Messages.SERVER_NOT_AVAILABLE;
		} catch (HttpResponseException httpResEx) {
			L.error(httpResEx);
			this.status = Status.ERROR;
			this.errorMsg = Messages.SERVER_NOT_AVAILABLE;
		} catch (SocketException exTO) {
			L.error(exTO);
			this.status = Status.ERROR;
			this.errorMsg = Messages.ERROR_PRECEDE
					+ Messages.SERVER_NOT_AVAILABLE;
		} catch (SocketTimeoutException exTO) {
			L.error(exTO);
			this.status = Status.ERROR;
			this.errorMsg = Messages.ERROR_PRECEDE + Messages.CONN_TIMEDOUT;
		} catch (ConnectTimeoutException exTO) {
			L.error(exTO);
			this.status = Status.ERROR;
			this.errorMsg = Messages.ERROR_PRECEDE + Messages.CONN_TIMEDOUT;
		} catch (FileNotFoundException ex) {
			L.error(ex);
			this.status = Status.ERROR;
			this.errorMsg = Messages.ERROR_PRECEDE + Messages.NO_API;
		} catch (Exception ex) {
			L.error(ex);
			this.status = Status.ERROR;
			this.errorMsg = Messages.ERROR_GENERAL;
		}
		log("status = " + status);
		return status;
	}

	/**
	 * Method which initialize the request to server and process. It will
	 * returns the status whether the request success of error.F
	 * 
	 * @return Status value.
	 */
	public Status processLogin(String sUserId, String sPassword, String sIMEI) {
		this.status = Status.NONE;

		if (!isInternetAvailable()) {
			this.status = Status.ERROR;
			this.errorMsg = Messages.NO_INTERNET;
			return this.status;
		}

		String strLoginUrl = API_SERVICE_URL +  CMD_LOGIN;
		log("Login URL: " + strLoginUrl);
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(strLoginUrl);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("userName", sUserId));
			nameValuePairs.add(new BasicNameValuePair("passWord", sPassword));
			nameValuePairs.add(new BasicNameValuePair("imei", sIMEI));
			
			L.log(sIMEI);

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse responseObj = httpclient.execute(httppost);
			int status_code = responseObj.getStatusLine().getStatusCode();
			log("status_code : " + status_code);
			if (status_code == 200) {
				HttpEntity resEntity = responseObj.getEntity();
				response = EntityUtils.toString(resEntity);
			}
			log("RESPONSE:");
			log("$$$$$$$$$$$$$$$$$$$$");
			log(response);
			log("$$$$$$$$$$$$$$$$$$$$");
			if (Validator.isEmpty(response)) {

				this.status = Status.ERROR;
				this.errorMsg = Messages.SERVER_NOT_AVAILABLE;
			} else {
				this.status = Status.SUCCESS;
			}
		} catch (UnknownHostException uhEx) {
			L.error(uhEx);
			this.status = Status.ERROR;
			this.errorMsg = Messages.NO_INTERNET;
		} catch (HttpHostConnectException hhcEx) {
			L.error(hhcEx);
			this.status = Status.ERROR;
			this.errorMsg = Messages.SERVER_NOT_AVAILABLE;
		} catch (HttpResponseException httpResEx) {
			L.error(httpResEx);
			this.status = Status.ERROR;
			this.errorMsg = Messages.SERVER_NOT_AVAILABLE;
		} catch (SocketException exTO) {
			L.error(exTO);
			this.status = Status.ERROR;
			this.errorMsg = Messages.ERROR_PRECEDE
					+ Messages.SERVER_NOT_AVAILABLE;
		} catch (SocketTimeoutException exTO) {
			L.error(exTO);
			this.status = Status.ERROR;
			this.errorMsg = Messages.ERROR_PRECEDE + Messages.CONN_TIMEDOUT;
		} catch (ConnectTimeoutException exTO) {
			L.error(exTO);
			this.status = Status.ERROR;
			this.errorMsg = Messages.ERROR_PRECEDE + Messages.CONN_TIMEDOUT;
		} catch (FileNotFoundException ex) {
			L.error(ex);
			this.status = Status.ERROR;
			this.errorMsg = Messages.ERROR_PRECEDE + Messages.NO_API;
		} catch (Exception ex) {
			L.error(ex);
			this.status = Status.ERROR;
			this.errorMsg = Messages.ERROR_GENERAL;
		}
		log("status = " + status);
		return status;
	}

	/**
	 * Method which initialize the request to server and process. It will
	 * returns the status whether the request success of error.F
	 * 
	 * @return Staus value.
	 */
	public Status processDownload(String sUsername, String sPassword,
			String sIMEI, String sOutfilename) {
		this.status = Status.NONE;

		if (!isInternetAvailable()) {
			this.status = Status.ERROR;
			this.errorMsg = Messages.NO_INTERNET;
			return this.status;
		}

		// Testing
		// sPassword = "123456787";

		String strDownloadUrl = API_SERVICE_URL + CMD_DOWNLOAD + "?imei="
				+ sIMEI + "&userName=" + sUsername + "&passWord=" + sPassword;
		log("Download URL: " + strDownloadUrl);
		try {
			HttpClient httpclient = new DefaultHttpClient();

			// Prepare a request object
			HttpGet httpget = new HttpGet(strDownloadUrl);

			// Execute the request
			HttpResponse responseObj = httpclient.execute(httpget);
			int status_code = responseObj.getStatusLine().getStatusCode();
			log("status_code : " + status_code);
			if (status_code == 200) {
				HttpEntity resEntity = responseObj.getEntity();
				if (resEntity != null) {
					long file_length = resEntity.getContentLength();
					if (file_length > 0) {
						InputStream instream = resEntity.getContent();
						OutputStream output = activity.openFileOutput(
								sOutfilename, Context.MODE_PRIVATE);
						int count;
						byte data[] = new byte[1024];

						while ((count = instream.read(data)) != -1) {
							output.write(data, 0, count);
						}

						httpclient.getConnectionManager().shutdown();
						response = ResponseCodes.SUCCESS;
					}
				}
			}

			log("RESPONSE:");
			log("$$$$$$$$$$$$$$$$$$$$");
			log(response);
			log("$$$$$$$$$$$$$$$$$$$$");
			if (Validator.isEmpty(response)) {
//				if (status_code == 204) {
//					this.status = Status.ERROR;
//					this.errorMsg = Messages.CONFIG_FILE_NOT_AVAIL;
//				} else {
//					this.status = Status.ERROR;
//					this.errorMsg = Messages.SERVER_NOT_AVAILABLE;
//				}
				this.status = Status.ERROR;
				this.errorMsg = Messages.CONFIG_FILE_NOT_AVAIL;
			} else {
				this.status = Status.SUCCESS;
			}
		} catch (UnknownHostException uhEx) {
			L.error(uhEx);
			this.status = Status.ERROR;
			this.errorMsg = Messages.NO_INTERNET;
		} catch (HttpHostConnectException hhcEx) {
			L.error(hhcEx);
			this.status = Status.ERROR;
			this.errorMsg = Messages.SERVER_NOT_AVAILABLE;
		} catch (HttpResponseException httpResEx) {
			L.error(httpResEx);
			this.status = Status.ERROR;
			this.errorMsg = Messages.SERVER_NOT_AVAILABLE;
		} catch (SocketException exTO) {
			L.error(exTO);
			this.status = Status.ERROR;
			this.errorMsg = Messages.ERROR_PRECEDE
					+ Messages.SERVER_NOT_AVAILABLE;
		} catch (SocketTimeoutException exTO) {
			L.error(exTO);
			this.status = Status.ERROR;
			this.errorMsg = Messages.ERROR_PRECEDE + Messages.CONN_TIMEDOUT;
		} catch (ConnectTimeoutException exTO) {
			L.error(exTO);
			this.status = Status.ERROR;
			this.errorMsg = Messages.ERROR_PRECEDE + Messages.CONN_TIMEDOUT;
		} catch (FileNotFoundException ex) {
			L.error(ex);
			this.status = Status.ERROR;
			this.errorMsg = Messages.ERROR_PRECEDE + Messages.NO_API;
		} catch (Exception ex) {
			L.error(ex);
			this.status = Status.ERROR;
			this.errorMsg = Messages.ERROR_GENERAL;
		}
		log("status = " + status);
		return status;
	}

	/**
	 * Method which initialize the request to server and process. It will
	 * returns the status whether the request success of error.F
	 * 
	 * @return Status value.
	 */
	public Status processUpload(String sUsername, String sPassword,
			String sIMEI, String sourceFile1, String sourceFile2) {
		this.status = Status.NONE;

		if (!isInternetAvailable()) {
			this.status = Status.ERROR;
			this.errorMsg = Messages.NO_INTERNET;
			return this.status;
		}

		// sPassword = "1231231";

		String logMsg = "";

		File file1 = new File(sourceFile1);
		File file2 = new File(sourceFile2);
		log("Upload File1 : " + sourceFile1);
		log("Upload File2 : " + sourceFile2);

		logMsg += "\n" + "Upload File1 : " + sourceFile1;
		logMsg += "\n" + "Upload File2 : " + sourceFile2;

		String strUploadUrl = API_SERVICE_URL + CMD_UPLOAD;
		log("Upload URL: " + strUploadUrl);
		logMsg += "\n" + "Upload URL: " + strUploadUrl;
		try {

			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(strUploadUrl);
			
			FileBody bin1 = new FileBody(file1, "uploadedfile1");
			FileBody bin2 = new FileBody(file2, "uploadedfile2");
			MultipartEntity reqEntity = new MultipartEntity();

			// reqEntity.addPart("userName", new StringBody(sUsername));
			// reqEntity.addPart("passWord", new StringBody(sPassword));
			// reqEntity.addPart("imei", new StringBody(sIMEI));

			reqEntity.addPart("uploadedfile1", bin1);
			reqEntity.addPart("uploadedfile2", bin2);
			

			post.setEntity(reqEntity);
			HttpResponse response_obj = client.execute(post);
			HttpEntity resEntity = response_obj.getEntity();
			response = EntityUtils.toString(resEntity);

			log("RESPONSE:");
			log("$$$$$$$$$$$$$$$$$$$$");
			log(response);
			log("$$$$$$$$$$$$$$$$$$$$");
			logMsg += "\n" + "RESPONSE: " + response;
			if (Validator.isEmpty(response)) {
				this.status = Status.ERROR;
				this.errorMsg = Messages.SERVER_NOT_AVAILABLE;
				if (Constants.TESTING) {
					this.errorMsg += "\n\n Empty response from server!";
					this.errorMsg += "\n\n-----\nConsole Msg:\n logMsg";
				}
			} else {
				this.status = Status.SUCCESS;
			}
		} catch (UnknownHostException uhEx) {
			L.error(uhEx);
			this.status = Status.ERROR;
			this.errorMsg = Messages.NO_INTERNET;
		} catch (HttpHostConnectException hhcEx) {
			L.error(hhcEx);
			this.status = Status.ERROR;
			this.errorMsg = Messages.SERVER_NOT_AVAILABLE;
			if (Constants.TESTING) {
				this.errorMsg += "\n\n" + Messages.errMsg(hhcEx);
				this.errorMsg += "\n\n-----\nConsole Msg:\n logMsg";
			}
		} catch (HttpResponseException httpResEx) {
			L.error(httpResEx);
			this.status = Status.ERROR;
			this.errorMsg = Messages.SERVER_NOT_AVAILABLE;
			if (Constants.TESTING) {
				this.errorMsg += "\n\n" + Messages.errMsg(httpResEx);
				this.errorMsg += "\n\n-----\nConsole Msg:\n logMsg";
			}
		} catch (SocketException exTO) {
			L.error(exTO);
			this.status = Status.ERROR;
			this.errorMsg = Messages.ERROR_PRECEDE
					+ Messages.SERVER_NOT_AVAILABLE;
			if (Constants.TESTING){
				this.errorMsg += "\n\n" + Messages.errMsg(exTO);
				this.errorMsg += "\n\n-----\nConsole Msg:\n logMsg";
			}
		} catch (SocketTimeoutException exTO) {
			L.error(exTO);
			this.status = Status.ERROR;
			this.errorMsg = Messages.ERROR_PRECEDE + Messages.CONN_TIMEDOUT;
		} catch (ConnectTimeoutException exTO) {
			L.error(exTO);
			this.status = Status.ERROR;
			this.errorMsg = Messages.ERROR_PRECEDE + Messages.CONN_TIMEDOUT;
		} catch (FileNotFoundException ex) {
			L.error(ex);
			this.status = Status.ERROR;
			this.errorMsg = Messages.ERROR_PRECEDE + Messages.NO_API;
			if (Constants.TESTING){
				this.errorMsg += "\n\n" + Messages.errMsg(ex);
				this.errorMsg += "\n\n-----\nConsole Msg:\n logMsg";
			}
		} catch (Exception ex) {
			L.error(ex);
			this.status = Status.ERROR;
			this.errorMsg = Messages.ERROR_GENERAL;
		}
		log("status = " + status);
		return status;
	}

	/**
	 * Method which initialize the request to server and process. It will
	 * returns the status whether the request success of error.F
	 * 
	 * @return Status value.
	 */
	public Status processUploadSingle(String sourceFile1) {
		this.status = Status.NONE;

		if (!isInternetAvailable()) {
			this.status = Status.ERROR;
			this.errorMsg = Messages.NO_INTERNET;
			return this.status;
		}
		File file1 = new File(sourceFile1);
		log("Upload File1 : " + sourceFile1);

		String strUploadUrl = API_SERVICE_URL + CMD_UPLOAD;
		log("Upload URL: " + strUploadUrl);
		try {

			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(strUploadUrl);
			FileBody bin1 = new FileBody(file1, "uploadedfile1");
			MultipartEntity reqEntity = new MultipartEntity();
			reqEntity.addPart("uploadedfile1", bin1);
			post.setEntity(reqEntity);
			HttpResponse response_obj = client.execute(post);
			HttpEntity resEntity = response_obj.getEntity();
			response = EntityUtils.toString(resEntity);

			log("RESPONSE:");
			log("$$$$$$$$$$$$$$$$$$$$");
			log(response);
			log("$$$$$$$$$$$$$$$$$$$$");
			if (Validator.isEmpty(response)) {
				this.status = Status.ERROR;
				this.errorMsg = Messages.SERVER_NOT_AVAILABLE;
			} else {
				this.status = Status.SUCCESS;
			}
		} catch (UnknownHostException uhEx) {
			L.error(uhEx);
			this.status = Status.ERROR;
			this.errorMsg = Messages.NO_INTERNET;
		} catch (HttpHostConnectException hhcEx) {
			L.error(hhcEx);
			this.status = Status.ERROR;
			this.errorMsg = Messages.SERVER_NOT_AVAILABLE;
		} catch (HttpResponseException httpResEx) {
			L.error(httpResEx);
			this.status = Status.ERROR;
			this.errorMsg = Messages.SERVER_NOT_AVAILABLE;
		} catch (SocketException exTO) {
			L.error(exTO);
			this.status = Status.ERROR;
			this.errorMsg = Messages.ERROR_PRECEDE
					+ Messages.SERVER_NOT_AVAILABLE;
		} catch (SocketTimeoutException exTO) {
			L.error(exTO);
			this.status = Status.ERROR;
			this.errorMsg = Messages.ERROR_PRECEDE + Messages.CONN_TIMEDOUT;
		} catch (ConnectTimeoutException exTO) {
			L.error(exTO);
			this.status = Status.ERROR;
			this.errorMsg = Messages.ERROR_PRECEDE + Messages.CONN_TIMEDOUT;
		} catch (FileNotFoundException ex) {
			L.error(ex);
			this.status = Status.ERROR;
			this.errorMsg = Messages.ERROR_PRECEDE + Messages.NO_API;
		} catch (Exception ex) {
			L.error(ex);
			this.status = Status.ERROR;
			this.errorMsg = Messages.ERROR_GENERAL;
		}
		log("status = " + status);
		return status;
	}

	boolean isInternetAvailable() {
		NetworkInfo info = null;
		if (activity != null)
			info = ((ConnectivityManager) activity
					.getSystemService(Context.CONNECTIVITY_SERVICE))
					.getActiveNetworkInfo();
		else
			info = ((ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE))
					.getActiveNetworkInfo();
		if (info == null || !info.isConnected())
			return false;
		else
			return true;
	}

	/**
	 * To get the device identification number.
	 * 
	 * @return Returns the device id.
	 */
	public String getDeviceID() {
		TelephonyManager TelephonyMgr = (TelephonyManager) activity
				.getSystemService(Context.TELEPHONY_SERVICE);
		return TelephonyMgr.getDeviceId();
	}

	/**
	 * To get the status of the request.
	 * 
	 * @return Returns the Status value of request.
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * To get the full error stack.
	 * 
	 * @return Returns the error stack.
	 */
	public String getErrorStack() {
		return errorStackMsg;
	}

	/**
	 * To get the expection occured while sending request.
	 * 
	 * @return Exception of request.
	 */
	public Exception getException() {
		return api_exception;
	}

	/**
	 * To get the response returned from the server.
	 * 
	 * @return Returns the response string.
	 */
	public String getResponse() {
		return response;
	}

	/**
	 * To get the error message.
	 * 
	 * @return Error message.
	 */
	public String getErrorMessage() {
		return errorMsg;
	}

	/**
	 * Method to make the secured HTTP request and process.
	 * 
	 * @param sRequestURL
	 *            URL to be requested.
	 * @param sRequestXML
	 *            Input XML data.
	 * @param context
	 *            The activity which sending request.
	 * @return Returns the response.
	 * @throws Exception
	 *             Throws exception if
	 */
	public String getHttpSecureResponse(String sRequestURL, String sRequestXML,
			Context context) throws Exception {
		String sResponseXML = null;
		try {

			NetworkInfo info = ((ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE))
					.getActiveNetworkInfo();
			if (info == null || !info.isConnected()) {
				return Messages.NO_INTERNET;
			}
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, new TrustManager[] { new MyTrustManager() },
					new SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection
					.setDefaultHostnameVerifier(new MyHostnameVerifier());

			HttpsURLConnection con = (HttpsURLConnection) new URL(sRequestURL)
					.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.connect();

			OutputStreamWriter out = new OutputStreamWriter(
					con.getOutputStream());
			out.write(sRequestXML);
			out.flush();
			out.close();

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = "";
			while ((line = rd.readLine()) != null) {
				sb.append(line + '\n');
			}

			sResponseXML = sb.toString();
			// System.out.println("Response xml is  :"+sResponseXML);

		} catch (Exception ex) {
			L.error(ex);
			System.out.println("Exception");
			ex.printStackTrace();
			throw ex;
		}
		return sResponseXML;
	}

	/**
	 * Method to make the normal HTTP request and process.
	 * 
	 * @param sRequestURL
	 *            URL to be requested.
	 * @param sRequestXML
	 *            Input XML data.
	 * @param context
	 *            The activity which sending request.
	 * @return Returns the response.
	 * @throws Exception
	 *             Throws exception if
	 */
	public String getHttpNormalResponse(String sRequestURL, String sRequestXML,
			Context context) throws Exception {
		String sResponseXML = null;
		try {

			NetworkInfo info = ((ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE))
					.getActiveNetworkInfo();
			if (info == null || !info.isConnected()) {
				return Messages.NO_INTERNET;
			}

			URL url = new URL(sRequestURL);
			HttpURLConnection http = null;

			http = (HttpURLConnection) url.openConnection();

			http.setDoOutput(true);
			http.setConnectTimeout(intConnectionTimeOut);
			http.connect();

			OutputStreamWriter out = new OutputStreamWriter(
					http.getOutputStream());
			out.write(sRequestXML);
			out.flush();
			out.close();

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					http.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = "";
			while ((line = rd.readLine()) != null) {
				sb.append(line + '\n');
			}

			sResponseXML = sb.toString();
			// System.out.println("Response xml is  :"+sResponseXML);

		} catch (Exception ex) {
			L.error(ex);
			System.out.println("Exception");
			ex.printStackTrace();
			throw ex;
		}
		return sResponseXML;
	}

	/**
	 * Method to make the normal HTTP request and process.
	 * 
	 * @param sRequestURL
	 *            URL to be requested.
	 * @param sRequestXML
	 *            Input XML data.
	 * @param context
	 *            The activity which sending request.
	 * @return Returns the response.
	 * @throws Exception
	 *             Throws exception if
	 */
	public String getHttpResponse(String sRequestURL, String sRequestXML,
			Context context) throws Exception {
		String sResponseXML = "";
		if (sRequestURL.toLowerCase().trim().substring(0, 5).equals("https")) {
			sResponseXML = getHttpSecureResponse(sRequestURL, sRequestXML,
					context);
		} else {
			sResponseXML = getHttpNormalResponse(sRequestURL, sRequestXML,
					context);
		}
		return sResponseXML;
	}

	// always verify the host - dont check for certificate
	final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

	/**
	 * Method which initialize the request to server and process. It will
	 * returns the status whether the request success of error.F
	 * 
	 * @return Status value.
	 */
	public Status processUpdateTestStatus(String sIMEI, String sUsername, String testName, boolean sStatus) {
		this.status = Status.NONE;

		if (!isInternetAvailable()) {
			this.status = Status.ERROR;
			this.errorMsg = Messages.NO_INTERNET;
			return this.status;
		}
		String Status1 = String.valueOf(sStatus);
		
		String strLoginUrl = API_SERVICE_URL +  CMD_UPDATETESTSTATUS;
		/*String strLoginUrl = API_SERVICE_URL +  CMD_UPDATETESTSTATUS "http://192.168.137.110:8080/Airometric/rest/file/"
				+ "?imei="+ sIMEI + "&userName=" + sUsername + "&status=" + Status1 + "&testName=" +testName;*/
//		System.out.println("Login URL: " + strLoginUrl);
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(strLoginUrl);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("userName", sUsername));
			nameValuePairs.add(new BasicNameValuePair("status", Status1));
			nameValuePairs.add(new BasicNameValuePair("imei", sIMEI));
			nameValuePairs.add(new BasicNameValuePair("testName", testName));
			
			L.log(sIMEI);

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse responseObj = httpclient.execute(httppost);
			int status_code = responseObj.getStatusLine().getStatusCode();
			System.out.println("status_code : " + status_code);
			if (status_code == 200) {
				HttpEntity resEntity = responseObj.getEntity();
				response = EntityUtils.toString(resEntity);
			}
			log("RESPONSE:");
			log("$$$$$$$$$$$$$$$$$$$$");
			log(response);
			log("$$$$$$$$$$$$$$$$$$$$");
			if (Validator.isEmpty(response)) {

				this.status = Status.ERROR;
				this.errorMsg = Messages.SERVER_NOT_AVAILABLE;
			} else {
				this.status = Status.SUCCESS;
			}
		} catch (UnknownHostException uhEx) {
			L.error(uhEx);
			this.status = Status.ERROR;
			this.errorMsg = Messages.NO_INTERNET;
		} catch (HttpHostConnectException hhcEx) {
			L.error(hhcEx);
			this.status = Status.ERROR;
			this.errorMsg = Messages.SERVER_NOT_AVAILABLE;
		} catch (HttpResponseException httpResEx) {
			L.error(httpResEx);
			this.status = Status.ERROR;
			this.errorMsg = Messages.SERVER_NOT_AVAILABLE;
		} catch (SocketException exTO) {
			L.error(exTO);
			this.status = Status.ERROR;
			this.errorMsg = Messages.ERROR_PRECEDE
					+ Messages.SERVER_NOT_AVAILABLE;
		} catch (SocketTimeoutException exTO) {
			L.error(exTO);
			this.status = Status.ERROR;
			this.errorMsg = Messages.ERROR_PRECEDE + Messages.CONN_TIMEDOUT;
		} catch (FileNotFoundException ex) {
			L.error(ex);
			this.status = Status.ERROR;
			this.errorMsg = Messages.ERROR_PRECEDE + Messages.NO_API;
		} catch (Exception ex) {
			L.error(ex);
			this.status = Status.ERROR;
			this.errorMsg = Messages.ERROR_GENERAL;
		}
		log("status = " + status);
		return status;
	}
	
	// public String[] getNormalRespone() {
	// String strResp[] = new String[2];
	// try {
	// DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
	// .newInstance();
	// DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	// docBuilder.isValidating();
	// DataInputStream in3 = new DataInputStream(new ByteArrayInputStream(
	// response.getBytes()));
	// Document doc = docBuilder.parse(in3);
	// doc.getDocumentElement().normalize();
	//
	// NodeList codeNode = doc.getElementsByTagName(APIParser.REPONSECODE);
	// NodeList msgNode = doc.getElementsByTagName(APIParser.REPONSEMSG);
	//
	// strResp[0] = codeNode.item(0).getChildNodes().item(0)
	// .getNodeValue();
	// strResp[1] = msgNode.item(0).getChildNodes().item(0).getNodeValue();
	// } catch (Exception e) {
	// L.error(e);
	// e.printStackTrace();
	// }
	// return strResp;
	// }

	public void log(String str) {
		L.debug(str);
	}

	public void Login(String strURL) {

	}
	
	
}
