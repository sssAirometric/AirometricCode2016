package com.airometric.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.airometric.classes.BrowserTestConfig;
import com.airometric.classes.FTPTestConfig;
import com.airometric.classes.MOTestConfig;
import com.airometric.classes.MTTestConfig;
import com.airometric.classes.PingTestConfig;
import com.airometric.classes.TestConfig;
import com.airometric.classes.UDPTestConfig;
import com.airometric.classes.VOIPTestConfig;
import com.airometric.storage.Preferences;
import com.airometric.utility.DeviceUtil;
import com.airometric.utility.L;

public class ConfigXMLParser {
	
	private Preferences pref;
	private Activity activity;
	public ConfigXMLParser(Preferences objPref) {
		this.pref = objPref;
	}
	
	public ConfigXMLParser(Activity objActivity) {
		this.activity = objActivity;
	}


	// XML node keys
	static final String KEY_ITEM = "testconfig"; // parent node
	static final String KEY_CONFIG_TEST_NAME = "testname";
	static final String KEY_TEST = "test";
	static final String KEY_TEST_NAME = "testname";
	static final String KEY_MARKET = "market";
	static final String KEY_IS_EXTERNAL = "external";
	
	// MO
	static final String KEY_PHONE_NUMBER = "phonenumber";
	static final String KEY_CALL_DURATION = "callduration";
	static final String KEY_PAUSE_TIME = "pausetime";
	static final String KEY_TEST_DURATION = "testduration";

	// FTP
	static final String KEY_FTP_SERVER_URL = "ftpserverurl";
	static final String KEY_FTP_USERNAME = "username";
	static final String KEY_FTP_PASSWORD = "password";
	static final String KEY_FTP_CYCLES = "numberofrepeatcycles";
	static final String KEY_FTP_UPLOAD_PATH = "filepathtoupload";
	static final String KEY_FTP_DOWNLOAD_FILE = "filepathtodownload";
	static final String KEY_FTP_FILE_TO_UPLOAD = "filetoupload";
	static final String KEY_MANUAL_UPLOAD_STATUS = "manualluploadstatus"; 

	// UDP
	static final String KEY_UDP_SERVER_URL = "udpserverurl";
	static final String KEY_UDP_PORT = "udpserverport";
	static final String KEY_UDP_CYCLES = "numberofrepeatcycles";
	static final String KEY_UDP_UPLOAD_PATH = "filepathtoupload";

	// Ping
	static final String KEY_PING_SERVER_URL = "pingserverurl";
	static final String KEY_PING_CYCLES = "numberofrepeatcycles";

	// Browser
	static final String KEY_BROWSER_PAGE_URL = "pageurl";
	static final String KEY_BROWSER_CYCLES = "numberofrepeatcycles";
		
	public TestConfig parseXML(String xml) {
		Document doc = getDomElement(xml); // getting DOM element

		NodeList test_config = doc.getElementsByTagName(KEY_ITEM);
		Element p_e = (Element) test_config.item(0);
		NodeEntry p_entry = new NodeEntry(p_e);
		String sTestConfigName = p_entry.getValue(KEY_CONFIG_TEST_NAME);
		String sMarketID = p_entry.getValue(KEY_MARKET);
		Boolean bIsExternal = Boolean.valueOf(p_entry.getValue(KEY_IS_EXTERNAL));
		
		TestConfig configObj = new TestConfig(sTestConfigName);		
		configObj.setIsExternalTest(bIsExternal);
		L.debug("sTestConfigName = " + sTestConfigName);
		L.debug("Market ID = " + sMarketID);
		L.debug("External  = " + " "+ bIsExternal);
		//Log.i("Test Name",sTestConfigName);
		pref.putValue(Preferences.KEY_TEST_NAME, sTestConfigName);
		pref.putValue(Preferences.KEY_SELECTED_MARKET_PLACE_ID, sMarketID);
		pref.putValue(Preferences.KEY_IS_TEST_CONFIG_SET, true);

		NodeList test_nl = doc.getElementsByTagName(KEY_TEST);
		// looping through all item nodes <item>
		int len = test_nl.getLength();
		for (int i = 0; i < len; i++) {
			Element e = (Element) test_nl.item(i);

			String sTestType = e.getAttribute("type");

			NodeEntry entry = new NodeEntry(e);

			if (sTestType.equalsIgnoreCase("mo")) {
				String sPhoneNumber = entry.getValue(KEY_PHONE_NUMBER);
				String sCallDuration = entry.getValue(KEY_CALL_DURATION);
				String sPauseTime = entry.getValue(KEY_PAUSE_TIME);
				String sTestDuration = entry.getValue(KEY_TEST_DURATION);
				
				MOTestConfig MOConfigTestObj = new MOTestConfig(sPhoneNumber,
						sCallDuration, sPauseTime, sTestDuration);
				configObj.setMOTestConfig(MOConfigTestObj);
				L.debug("MOConfigTestObj = " + MOConfigTestObj.toString());
			}
			if (sTestType.equalsIgnoreCase("mt")) {
				String sTestDuration = entry.getValue(KEY_TEST_DURATION);
				String sCallDuration = entry.getValue(KEY_CALL_DURATION);

				MTTestConfig MTConfigTestObj = new MTTestConfig(sCallDuration, sTestDuration);
				configObj.setMTTestConfig(MTConfigTestObj);
				L.debug("MTConfigTestObj = " + MTConfigTestObj.toString());
			}

			if (sTestType.equalsIgnoreCase("ftp")) {
				String sServerURL = entry.getValue(KEY_FTP_SERVER_URL);
				String sUsername = entry.getValue(KEY_FTP_USERNAME);
				String sPassowrd = entry.getValue(KEY_FTP_PASSWORD);
				String sCycles = entry.getValue(KEY_FTP_CYCLES);
				String sUploadPath = entry.getValue(KEY_FTP_UPLOAD_PATH);
				String sDownloadFile = entry.getValue(KEY_FTP_DOWNLOAD_FILE);
				String sManuallUploadFilePath = entry.getValue(KEY_FTP_FILE_TO_UPLOAD);
				boolean bManuallUploadStatus = Boolean.valueOf(entry.getValue((KEY_MANUAL_UPLOAD_STATUS)));

				FTPTestConfig FTPTestConfigObj = new FTPTestConfig(sServerURL,
						sUsername, sPassowrd, sCycles, sUploadPath,
						sDownloadFile, sManuallUploadFilePath, bManuallUploadStatus);
				
				configObj.setFTPTestConfig(FTPTestConfigObj);
				L.debug("FTPTestConfigObj = " + FTPTestConfigObj.toString());
			}
			if (sTestType.equalsIgnoreCase("udp")) {
				String sServerURL = entry.getValue(KEY_UDP_SERVER_URL);
				String sPort = entry.getValue(KEY_UDP_PORT);
				String sCycles = entry.getValue(KEY_UDP_CYCLES);
				String sUploadFilePath = entry.getValue(KEY_UDP_UPLOAD_PATH);

				UDPTestConfig UDPTestConfigObj = new UDPTestConfig(sServerURL,
						sPort, sCycles, sUploadFilePath);
				configObj.setUDPTestConfig(UDPTestConfigObj);
				L.debug("UDPTestConfigObj = " + UDPTestConfigObj.toString());
			}
			if (sTestType.equalsIgnoreCase("pingtest")) {
				String sServerURL = entry.getValue(KEY_PING_SERVER_URL);
				String sCycles = entry.getValue(KEY_PING_CYCLES);

				PingTestConfig PingTestConfigObj = new PingTestConfig(
						sServerURL, sCycles);
				configObj.setPingTestConfig(PingTestConfigObj);
				L.debug("PingTestConfigObj = " + PingTestConfigObj.toString());
			}
			if (sTestType.equalsIgnoreCase("browser")) {
				String sPageURL = entry.getValue(KEY_BROWSER_PAGE_URL);
				String sCycles = entry.getValue(KEY_BROWSER_CYCLES);

				BrowserTestConfig BrowserTestConfigObj = new BrowserTestConfig(
						sPageURL, sCycles);
				configObj.setBrowserTestConfig(BrowserTestConfigObj);
				L.debug("BrowserTestConfigObj = "
						+ BrowserTestConfigObj.toString());
			}
			if (sTestType.equalsIgnoreCase("voip")) {
				String sTestDuration = entry.getValue(KEY_TEST_DURATION);

				VOIPTestConfig VOIPConfigTestObj = new VOIPTestConfig(sTestDuration);
				configObj.setVOIPTestConfig(VOIPConfigTestObj);
				L.debug("VOIPConfigTestObj = " + VOIPConfigTestObj.toString());
			}

		}
		return configObj;
	}

	String getVal(Node node) {
		String val = "";
		if (node != null && node.getFirstChild() != null)
			val = node.getFirstChild().getNodeValue();
		return val;
	}

	class NodeEntry {
		Element ele;

		public NodeEntry(Element ele) {
			this.ele = ele;
		}

		public String getValue(String str) {

			NodeList n = ele.getElementsByTagName(str);
			return this.getElementValue(n.item(0));
		}

		public final String getElementValue(Node elem) {
			Node child;
			if (elem != null) {
				if (elem.hasChildNodes()) {
					for (child = elem.getFirstChild(); child != null; child = child
							.getNextSibling()) {
						if (child.getNodeType() == Node.TEXT_NODE) {
							return child.getNodeValue();
						}
					}
				}
			}
			return "";
		}
	}

	public Document getDomElement(String xml) {
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {

			DocumentBuilder db = dbf.newDocumentBuilder();

			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			doc = db.parse(is);

		} catch (ParserConfigurationException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		} catch (SAXException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		} catch (IOException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		}
		// return DOM
		return doc;
	}

	public static void parser(String response) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(new ByteArrayInputStream(response
					.getBytes("UTF-8")));
			Element root = dom.getDocumentElement();
			NodeList items = root.getElementsByTagName(KEY_ITEM);

			for (int i = 0; i < items.getLength(); i++) {

				Node item = items.item(i);
				System.out.println("name " + item.getLocalName());
				NodeList properties = item.getChildNodes();
				for (int j = 0; j < properties.getLength(); j++) {
					Node property = properties.item(j);
					String name = property.getNodeName();
					System.out.println("name " + name);
					String value = property.getFirstChild().getNodeValue();

					System.out.println(" value " + value);

				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
