package com.to;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.html.HtmlForm;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import com.PropertyFileReader;
import com.dao.ReportDao;
import com.dao.UserDao;
import com.dao.impl.ReportDaoImpl;
import com.dao.impl.UserDaoImpl;
import com.util.MapUtil;

@ManagedBean
@RequestScoped
public class ReportPdfBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8606023157764621343L;

	private HtmlForm init;

	public void setInit(HtmlForm init) {
		generateMapReportForPDF();
	}

	public HtmlForm getInit() {
		return init;
	}

	static HashMap<String, String> propertiesFiledata = PropertyFileReader
			.getProperties();
	private final String SIGNALSTRENGTH_GSM = propertiesFiledata
			.get("SIGNALSTRENGTH_GSM");
	private final String SIGNALSTRENGTH_GSM1 = propertiesFiledata
			.get("SIGNALSTRENGTH_GSM1");
	/*private final String SIGNALSTRENGTH_LTE = propertiesFiledata
			.get("SIGNALSTRENGTH_LTE");*/
	private final String THROUGHPUT = propertiesFiledata.get("THROUGHPUT");
	private final String ServerPort = propertiesFiledata.get("ServerPort");
	private final String QXDM_FILE_PATH = propertiesFiledata
			.get("QXDM_FILE_PATH");
	private final String ENVIRONMENT = propertiesFiledata.get("ENVIRONMENT");

	Map<String, String> testNameMap = new LinkedHashMap<String, String>();

	Map<String, String> testNameExcelMap = new LinkedHashMap<String, String>();

	private String testCaseName;
	
	private String marketId;
	
	private String testtype;
	
	public String getTesttype() {
		return testtype;
	}

	public void setTesttype(String testtype) {
		this.testtype = testtype;
	}

	public String getMarketId() {
		return marketId;
	}

	public void setMarketId(String marketId) {
		this.marketId = marketId;
	}

	private String loggedInUserName;

	private String mapReportType;

	private List<STGDevice> STGDeviceresults;

	private List<DeviceInfoTO> ThroughputDeviceresults;

	private List<STGDevice> STGDeviceInfoResults;

	private String testValue;

	Map<String, String> deviceNameMap = new LinkedHashMap<String, String>();

	private String deviceName;

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public Map<String, String> getDeviceNameMap() {
		return deviceNameMap;
	}

	public void setDeviceNameMap(Map<String, String> deviceNameMap) {
		this.deviceNameMap = deviceNameMap;
	}

	public String getTestValue() {
		return testValue;
	}

	public void setTestValue(String testValue) {
		this.testValue = testValue;
	}

	public List<DeviceInfoTO> getThroughputDeviceresults() {
		return ThroughputDeviceresults;
	}

	public void setThroughputDeviceresults(
			List<DeviceInfoTO> throughputDeviceresults) {
		ThroughputDeviceresults = throughputDeviceresults;
	}

	public Map<String, String> getTestNameMap() {
		return testNameMap;
	}

	public List<STGDevice> getSTGDeviceInfoResults() {
		return STGDeviceInfoResults;
	}

	public void setSTGDeviceInfoResults(List<STGDevice> sTGDeviceInfoResults) {
		STGDeviceInfoResults = sTGDeviceInfoResults;
	}

	public void setTestNameMap(Map<String, String> testNameMap) {
		this.testNameMap = testNameMap;
	}

	public Map<String, String> getTestNameExcelMap() {
		return testNameExcelMap;
	}

	public void setTestNameExcelMap(Map<String, String> testNameExcelMap) {
		this.testNameExcelMap = testNameExcelMap;
	}

	public String getTestCaseName() {
		return testCaseName;
	}

	public void setTestCaseName(String testCaseName) {
		this.testCaseName = testCaseName;
	}
	
	
	public String getLoggedInUserName() {
		return loggedInUserName;
	}

	public void setLoggedInUserName(String loggedInUserName) {
		this.loggedInUserName = loggedInUserName;
	}

	public List<STGDevice> getSTGDeviceresults() {
		return STGDeviceresults;
	}

	public void setSTGDeviceresults(List<STGDevice> sTGDeviceresults) {
		STGDeviceresults = sTGDeviceresults;
	}

	public String getMapReportType() {
		return mapReportType;
	}

	public void setMapReportType(String mapReportType) {
		this.mapReportType = mapReportType;
	}

	/*public String getTestCase(ValueChangeEvent event) {
		FacesContext context = FacesContext.getCurrentInstance();
		String newValue = (String) event.getNewValue();
		String oldValue = (String) event.getOldValue();
		ReportDao rDao = new ReportDaoImpl();
		testNameMap = rDao.getTestNames(newValue);
		context.getExternalContext().getSessionMap()
				.put("testNameMap", testNameMap);
		return "voiceConnectPage";
	}
*/
	public String generateMapReportForPDF() {
//		System.out.println("I am generating the pdf..export.");
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getSessionMap().put("loginName","admin");
		String url = null;
		MapUtil mapUtil = new MapUtil();
		String ipAddress = null;
		String fileName = null;
		mapReportType = "voiceQuality";

		ReportDao dao = new ReportDaoImpl();
		List STGDeviceSignalStrengthList = new ArrayList();
		List STGDeviceLatitudeList = new ArrayList();
		List<String> ratingList = new ArrayList<String>();
		boolean deviceStatus = false;
//		System.out.println("testCaseName--------"+loggedInUserName+"--------" + testCaseName);
		if (null == testCaseName) {
			testCaseName = "Nexus520";
		}
		
		STGDeviceresults = dao.getTestNameMapDetailsResults(testCaseName,marketId);
		UserDao userDao = new UserDaoImpl();
		deviceStatus = dao.getCycleTestNamesInDeviceInfo(testCaseName,marketId);
		if (deviceStatus == true) {
			STGDeviceSignalStrengthList = userDao
					.getAllVoiceQualityMultipleSignalStrengthList(testCaseName,marketId);
			STGDeviceLatitudeList = userDao
					.getAllVoiceQualityMultipleLatitudeList(testCaseName,marketId);
			ratingList = userDao.getRatingList(testCaseName,marketId);
		} else {
			STGDeviceSignalStrengthList = userDao
					.getAllVoiceQualitySignalStrengthList(testCaseName,marketId,testtype);
			STGDeviceLatitudeList = userDao
					.getAllVoiceQualityLatitudeList(testCaseName,marketId,testtype);
			ratingList = userDao.getRatingList(testCaseName,marketId);
		}
		if (STGDeviceresults.size() <= 0) {
			context.getExternalContext().getRequestMap().remove("datamessage");
			context.getExternalContext()
					.getRequestMap()
					.put("datamessage",
							"No Data Found for the Selected Test Name");
			context.getExternalContext().getSessionMap().remove("lattitudes");
			context.getExternalContext().getSessionMap().remove("longitudes");
			context.getExternalContext().getSessionMap().remove("testName");
			context.getExternalContext().getSessionMap()
					.remove("VQuadTimestamp");
			context.getExternalContext().getSessionMap()
					.remove("CallTimestamp");
			context.getExternalContext().getSessionMap()
					.remove("VQuadLocation");
			context.getExternalContext().getSessionMap().remove("VQuadPhoneID");
			context.getExternalContext().getSessionMap()
					.remove("DegradedFilename");
			context.getExternalContext().getSessionMap().remove("Rating");
			context.getExternalContext().getSessionMap().remove("PESQ");
			context.getExternalContext().getSessionMap()
					.remove("PESQAverageOffset");
			context.getExternalContext().getSessionMap()
					.remove("PESQMaxOffset");
			context.getExternalContext().getSessionMap()
					.remove("PESQMinOffset");
			context.getExternalContext().getSessionMap()
					.remove("NumberAllClipping");
			context.getExternalContext().getSessionMap()
					.remove("DurationALLClipping");
			context.getExternalContext().getSessionMap()
					.remove("mapReportType");
			context.getExternalContext().getSessionMap()
					.remove("signalStrength");
			context.getExternalContext().getSessionMap().remove("networkType");
			context.getExternalContext().getSessionMap().remove("networkData");
			context.getExternalContext().getSessionMap()
					.remove("networkRoaming");
			context.getExternalContext().getSessionMap().remove("networkMnc");
			context.getExternalContext().getSessionMap().remove("networkMcc");
			context.getExternalContext().getSessionMap()
					.remove("cellLocationLac");
			context.getExternalContext().getSessionMap()
					.remove("cellLocationCid");
			context.getExternalContext().getSessionMap()
					.remove("signalStrengthSnr");
			context.getExternalContext().getSessionMap()
					.remove("devicePhoneType");
			context.getExternalContext().getSessionMap()
					.remove("timeStampForEachSample");
			context.getExternalContext().getSessionMap()
					.remove("neighbourInfo");
			context.getExternalContext().getSessionMap()
					.remove("signalStrengthCDMACIO");
			context.getExternalContext().getSessionMap()
					.remove("signalStrengthEVDOECIO");
			context.getExternalContext().getSessionMap().remove("throughput");
			context.getExternalContext().getSessionMap()
					.remove("signalStrengthRating");
			context.getExternalContext().getSessionMap()
					.remove("STGDeviceSignalStrengthList");
			return "mapDataValidation";
		} else {
			String latitudes = "";
			String longitudes = "";
			String testName = "";
			String VQuadTimestamp = "";
			String VQuadLocation = "";
			String CallTimestamp = "";
			String VQuadPhoneID = "";
			String DegradedFilename = "";
			String Rating = "";
			String PESQ = "";
			String PESQAverageOffset = "";
			String PESQMaxOffset = "";
			String PESQMinOffset = "";
			String NumberAllClipping = "";
			String DurationALLClipping = "";
			String signalStrength = "";
			String timestamp = "";
			String deviceinfolatitude = "";
			String status = "";
			String Vquadlattitude = "";

			List<String> latlong = new ArrayList<String>();
			List lattitudeList = new ArrayList();
			List VquadlattitudeList = new ArrayList();
			List longitudeList = new ArrayList();
			List testNameList = new ArrayList();
			List VQuadTimestampList = new ArrayList();
			List CallTimestampList = new ArrayList();
			List VQuadLocationList = new ArrayList();
			List VQuadPhoneIDList = new ArrayList();
			List DegradedFilenameList = new ArrayList();
			List RatingList = new ArrayList();
			List PESQList = new ArrayList();
			List PESQAverageOffsetList = new ArrayList();
			List PESQMaxOffsetList = new ArrayList();
			List PESQMinOffsetList = new ArrayList();
			List NumberAllClippingList = new ArrayList();
			List DurationALLClippingList = new ArrayList();
			List statusList = new ArrayList();

			for (int i = 0; i < STGDeviceresults.size(); i++) {
				STGDevice deviceInfo = STGDeviceresults.get(i);
				lattitudeList.add(deviceInfo.getLattitude());
				longitudeList.add(deviceInfo.getLongitude());
				testNameList.add(deviceInfo.getTestName());
				VQuadTimestampList.add(deviceInfo.getVQuadTimestamp());
				VQuadLocationList.add(deviceInfo.getVQuadLocation());
				VQuadPhoneIDList.add(deviceInfo.getVQuadPhoneID());
				CallTimestampList.add(deviceInfo.getCallTimestamp());
				DegradedFilenameList.add(deviceInfo.getDegradedFilename());
				RatingList.add(deviceInfo.getRating());
				PESQList.add(deviceInfo.getPESQ());
				PESQAverageOffsetList.add(deviceInfo.getPESQAverageOffset());
				PESQMaxOffsetList.add(deviceInfo.getPESQMaxOffset());
				PESQMinOffsetList.add(deviceInfo.getPESQMinOffset());
				NumberAllClippingList.add(deviceInfo.getNumberAllClipping());
				DurationALLClippingList
						.add(deviceInfo.getDurationALLClipping());
				if (deviceInfo.getVQuadCallID().indexOf("O") == -1) {
					statusList.add("Incoming");
				} else if (deviceInfo.getVQuadCallID().indexOf("I") == -1) {
					statusList.add("outgoing");
				}
				VquadlattitudeList.add(deviceInfo.getLattitude());
			}

			// Collections.sort(lattitudeList);
			// Collections.sort(longitudeList);
			latitudes = lattitudeList.toString();
			longitudes = longitudeList.toString();
			testName = testNameList.toString();
			VQuadTimestamp = VQuadTimestampList.toString();
			VQuadLocation = VQuadLocationList.toString();
			VQuadPhoneID = VQuadPhoneIDList.toString();
			DegradedFilename = DegradedFilenameList.toString();
			Rating = RatingList.toString();
			PESQ = PESQList.toString();
			PESQAverageOffset = PESQAverageOffsetList.toString();
			PESQMaxOffset = PESQMaxOffsetList.toString();
			PESQMinOffset = PESQMinOffsetList.toString();
			NumberAllClipping = NumberAllClippingList.toString();
			DurationALLClipping = DurationALLClippingList.toString();
			CallTimestamp = CallTimestampList.toString();
			status = statusList.toString();
			Vquadlattitude = VquadlattitudeList.toString();

			if (STGDeviceSignalStrengthList.size() > 0) {
				for (int i = 0; i < STGDeviceSignalStrengthList.size(); i++) {
					int signalStrengthGSM = Integer
							.parseInt(STGDeviceSignalStrengthList.get(i)
									.toString());
					int signalStrengthGSMValue = -Integer
							.parseInt(SIGNALSTRENGTH_GSM)
							+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
							* signalStrengthGSM;
					signalStrength = signalStrength + ","
							+ signalStrengthGSMValue + "dBm.";
				}
			}
			if (STGDeviceLatitudeList.size() > 0) {
				for (int i = 0; i < STGDeviceLatitudeList.size(); i++) {
					deviceinfolatitude = deviceinfolatitude
							+ ","
							+ round(Double.parseDouble(STGDeviceLatitudeList
									.get(i).toString()), 5);
				}
			}

			context.getExternalContext().getSessionMap().remove("lattitudes");
			context.getExternalContext().getSessionMap().remove("longitudes");
			context.getExternalContext().getSessionMap().remove("testName");
			context.getExternalContext().getSessionMap()
					.remove("VQuadTimestamp");
			context.getExternalContext().getSessionMap()
					.remove("CallTimestamp");
			context.getExternalContext().getSessionMap()
					.remove("VQuadLocation");
			context.getExternalContext().getSessionMap().remove("VQuadPhoneID");
			context.getExternalContext().getSessionMap()
					.remove("DegradedFilename");
			context.getExternalContext().getSessionMap().remove("Rating");
			context.getExternalContext().getSessionMap().remove("PESQ");
			context.getExternalContext().getSessionMap()
					.remove("PESQAverageOffset");
			context.getExternalContext().getSessionMap()
					.remove("PESQMaxOffset");
			context.getExternalContext().getSessionMap()
					.remove("PESQMinOffset");
			context.getExternalContext().getSessionMap()
					.remove("NumberAllClipping");
			context.getExternalContext().getSessionMap()
					.remove("DurationALLClipping");
			context.getExternalContext().getSessionMap()
					.remove("mapReportType");
			context.getExternalContext().getSessionMap()
					.remove("signalStrength");
			context.getExternalContext().getSessionMap().remove("networkType");
			context.getExternalContext().getSessionMap().remove("networkData");
			context.getExternalContext().getSessionMap()
					.remove("networkRoaming");
			context.getExternalContext().getSessionMap().remove("networkMnc");
			context.getExternalContext().getSessionMap().remove("networkMcc");
			context.getExternalContext().getSessionMap()
					.remove("cellLocationLac");
			context.getExternalContext().getSessionMap()
					.remove("cellLocationCid");
			context.getExternalContext().getSessionMap()
					.remove("signalStrengthSnr");
			context.getExternalContext().getSessionMap()
					.remove("devicePhoneType");
			context.getExternalContext().getSessionMap()
					.remove("timeStampForEachSample");
			context.getExternalContext().getSessionMap()
					.remove("neighbourInfo");
			context.getExternalContext().getSessionMap()
					.remove("signalStrengthCDMACIO");
			context.getExternalContext().getSessionMap()
					.remove("signalStrengthEVDOECIO");
			context.getExternalContext().getSessionMap().remove("throughput");
			context.getExternalContext().getSessionMap()
					.remove("signalStrengthLTERSRP");
			context.getExternalContext().getSessionMap()
					.remove("signalStrengthLTERSRQ");
			context.getExternalContext().getSessionMap()
					.remove("signalStrengthLTERSSNR");
			context.getExternalContext().getSessionMap()
					.remove("signalStrengthLTECQI");
			context.getExternalContext().getSessionMap()
					.remove("signalStrengthRating");
			context.getExternalContext().getSessionMap()
					.remove("STGDeviceSignalStrengthList");
			context.getExternalContext().getSessionMap().remove("statusList");
			context.getExternalContext().getSessionMap()
					.remove("deviceinfolatitude");
			context.getExternalContext().getSessionMap()
					.remove("Vquadlattitude");

			context.getExternalContext()
					.getSessionMap()
					.put("Vquadlattitude",
							Vquadlattitude.substring(1,
									Vquadlattitude.length() - 1));
			context.getExternalContext()
					.getSessionMap()
					.put("deviceinfolatitude",
							deviceinfolatitude.substring(1,
									deviceinfolatitude.length() - 1));
			context.getExternalContext()
					.getSessionMap()
					.put("statusList", status.substring(1, status.length() - 1));
			context.getExternalContext()
					.getSessionMap()
					.put("lattitudes",
							latitudes.substring(1, latitudes.length() - 1));
			context.getExternalContext()
					.getSessionMap()
					.put("longitudes",
							longitudes.substring(1, longitudes.length() - 1));
			context.getExternalContext()
					.getSessionMap()
					.put("testName",
							testName.substring(1, testName.length() - 1));
			context.getExternalContext()
					.getSessionMap()
					.put("VQuadTimestamp",
							VQuadTimestamp.substring(1,
									VQuadTimestamp.length() - 1));
			context.getExternalContext()
					.getSessionMap()
					.put("CallTimestamp",
							CallTimestamp.substring(1,
									CallTimestamp.length() - 1));
			context.getExternalContext()
					.getSessionMap()
					.put("VQuadLocation",
							VQuadLocation.substring(1,
									VQuadLocation.length() - 1));
			context.getExternalContext()
					.getSessionMap()
					.put("VQuadPhoneID",
							VQuadPhoneID.substring(1, VQuadPhoneID.length() - 1));
			context.getExternalContext()
					.getSessionMap()
					.put("DegradedFilename",
							DegradedFilename.substring(1,
									DegradedFilename.length() - 1));
			context.getExternalContext().getSessionMap()
					.put("mapReportType", mapReportType);
			context.getExternalContext().getSessionMap()
					.put("Rating", Rating.substring(1, Rating.length() - 1));
			context.getExternalContext().getSessionMap()
					.put("PESQ", PESQ.substring(1, PESQ.length() - 1));
			context.getExternalContext()
					.getSessionMap()
					.put("PESQAverageOffset",
							PESQAverageOffset.substring(1,
									PESQAverageOffset.length() - 1));
			context.getExternalContext()
					.getSessionMap()
					.put("PESQMaxOffset",
							PESQMaxOffset.substring(1,
									PESQMaxOffset.length() - 1));
			context.getExternalContext()
					.getSessionMap()
					.put("PESQMinOffset",
							PESQMinOffset.substring(1,
									PESQMinOffset.length() - 1));
			context.getExternalContext()
					.getSessionMap()
					.put("NumberAllClipping",
							NumberAllClipping.substring(1,
									NumberAllClipping.length() - 1));
			context.getExternalContext()
					.getSessionMap()
					.put("DurationALLClipping",
							DurationALLClipping.substring(1,
									DurationALLClipping.length() - 1));

			if (STGDeviceSignalStrengthList.size() > 0) {
				context.getExternalContext()
						.getSessionMap()
						.put("STGDeviceSignalStrengthList",
								signalStrength.substring(1,
										signalStrength.length()));
			} else {
				context.getExternalContext().getSessionMap()
						.put("STGDeviceSignalStrengthList", signalStrength);
			}
		}
//		System.out.println("Completed==============="+context.getExternalContext()
//				.getSessionMap()
//				.get("testName"));
		return "pdfDataSuccess";
	}

	/**
	 * Function to round the Number based on the given number of decimlas.
	 */
	public static double round(double targetValue, int roundToDecimalPlaces) {
		int valueInTwoDecimalPlaces = (int) (targetValue * Math.pow(10,
				roundToDecimalPlaces));
		return (double) (valueInTwoDecimalPlaces / Math.pow(10,
				roundToDecimalPlaces));
	}
	
	public static void main(String[] args) {
        List l = new ArrayList();
        l.add(10);
        l.add(2);
        l.add(3);
        l.add(4);
        l.add(5);
//        System.out.println(Collections.max(l)); // 5
//        System.out.println(Collections.min(l)); // 1
    }
}
