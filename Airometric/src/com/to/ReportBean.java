package com.to;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sf.json.JSONArray;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.util.HttpURLConnection;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import org.json.simple.parser.JSONParser;
import org.primefaces.context.RequestContext;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.PropertyFileReader;
import com.VoiceQualityHealth;
import com.dao.MarketPlaceDao;
import com.dao.ReportDao;
import com.dao.TestConfigDao;
import com.dao.UserDao;
import com.dao.impl.MarketPlaceDaoImpl;
import com.dao.impl.ReportDaoImpl;
import com.dao.impl.TestConfigDaoImpl;
import com.dao.impl.UserDaoImpl;
import com.helper.Misc;
import com.migratedata.ExportColumns;
import com.model.DBUtil;
import com.preprocessing.PreprocessorTrigger;
import com.preprocessing.reports.PreprocessExport;
import com.preprocessing.reports.summary.PreprocessorSummaryReportGenerator;
import com.preprocessorhelpers.FTPReportHelper;
import com.preprocessorhelpers.PreCalculationDBHelper;
import com.report.dao.DataConnectivityDao;
import com.report.dao.Data_VoiceKpiDao;
import com.report.dao.KpiDao;
import com.report.dao.QxdmDao;
import com.report.dao.VoiceConnectivityDao;
import com.report.dao.Voice_DataDao;
import com.report.dao.impl.DataConnectivityDaoImpl;
import com.report.dao.impl.Data_VoiceKpiDaoImpl;
import com.report.dao.impl.KpiDaoImpl;
import com.report.dao.impl.VoiceConnectivityDaoImpl;
import com.report.dao.impl.VoiceQualityDaoImpl;
import com.report.dao.impl.Voice_DataDaoImpl;
import com.report.helper.ChartCategoryHelper;
import com.report.helper.MapReportHelper;
import com.report.helper.QualityRangeHelper;
import com.report.helper.ReportConfigHelper;
import com.report.result.to.TCPChartTO;
import com.report.result.to.UDPChartTO;
import com.report.to.ThrougputDataTO;
import com.util.ExcelVQTReport;
import com.util.MapUtil;
/* We will use this dataset class to populate data for our bar chart */
/* used to create a chart object */
/* We will use this class to convert the chart to a PNG image file */


@ManagedBean
@RequestScoped
public class ReportBean implements Serializable {

	/**
	 * 
	 */
	final static Logger logger = Logger.getLogger(PreprocessorTrigger.class);
	public static final long serialVersionUID = 8606023157764621343L;
	static HashMap<String, String> propertiesFiledata = PropertyFileReader
			.getProperties();
	public final static String SIGNALSTRENGTH_GSM = propertiesFiledata
			.get("SIGNALSTRENGTH_GSM");
	public final static String SIGNALSTRENGTH_GSM1 = propertiesFiledata
			.get("SIGNALSTRENGTH_GSM1");
	// public final static String SIGNALSTRENGTH_LTE =
	// propertiesFiledata.get("SIGNALSTRENGTH_LTE");
	public final static String THROUGHPUT = propertiesFiledata
			.get("THROUGHPUT");
	public final static String ServerPort = propertiesFiledata
			.get("ServerPort");
	public final static String QXDM_FILE_PATH = propertiesFiledata
			.get("QXDM_FILE_PATH");
	public final static String ENVIRONMENT = propertiesFiledata
			.get("ENVIRONMENT");
	public final static String ANALYTICS_REPORTS_FOLDER_PATH = propertiesFiledata
			.get("ANALYTICS_REPORTS_FOLDER_PATH");
	
	public static Connection conn = null;
	public static Statement st = null;

	Map<String, String> testNameMap = new LinkedHashMap<String, String>();

	Map<String, String> frequencyNameMap = new LinkedHashMap<String, String>();

	Map<String, String> marketPlaceMap = new LinkedHashMap<String, String>();

	public Map<String, String> getMarketPlaceMap() {
		return marketPlaceMap;
	}

	public void setMarketPlaceMap(Map<String, String> marketPlaceMap) {
		this.marketPlaceMap = marketPlaceMap;
	}

	public Map<String, String> getFrequencyNameMap() {
		return frequencyNameMap;
	}

	public void setFrequencyNameMap(Map<String, String> frequencyNameMap) {
		this.frequencyNameMap = frequencyNameMap;
	}

	String testCaseNametoprocess;

	public String getTestCaseNametoprocess() {
		return testCaseNametoprocess;
	}

	public void setTestCaseNametoprocess(String testCaseNametoprocess) {
		this.testCaseNametoprocess = testCaseNametoprocess;
	}

	String frequencyToProcess;

	public String getFrequencyToProcess() {
		return frequencyToProcess;
	}

	public void setFrequencyToProcess(String frequencyToProcess) {
		this.frequencyToProcess = frequencyToProcess;
	}

	String rangeName;

	Map<String, String> testNameExcelMap = new LinkedHashMap<String, String>();

	Map<String, String> deviceNameMap = new LinkedHashMap<String, String>();

	Map<String, String> configurationNameMap = new LinkedHashMap<String, String>();

	public Map<String, String> getConfigurationNameMap() {
		return configurationNameMap;
	}

	public void setConfigurationNameMap(Map<String, String> configurationNameMap) {
		this.configurationNameMap = configurationNameMap;
	}

	public String getRangeName() {
		return rangeName;
	}

	public void setRangeName(String rangeName) {
		this.rangeName = rangeName;
	}

	private String testCaseName;
	String neighbourtStr = null;
	String networkName = "";

	private String mapReportType;

	private String reportType;

	public void openConn() {
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public void closeConn() {
		try {
			st.close();
			conn.close();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	private static List<STGDevice> stgDeviceresults;
	private static List<String> stgVqtresults;

	private List<STGNetTest> stgNetTestresults;

	private List<DeviceInfoTO> ThroughputDeviceresults;

	private List<STGDevice> STGDeviceInfoResults;

	private String deviceName;

	private String marketmapId;

	private String testValue;

	private String testCommentKpi1;

	private String testCommentKpi2;

	private String testCommentKpi3;

	private String testCommentKpi4;

	private String testCommentKpi5;

	private String testCommentKpi6;

	private String testCommentKpi7;

	private String[] marketId;

	private String[] filesIds;

	private String testSummaryComment;
	private String filesOpt;

	private String ajaxResponseStr = "";

	private String reportingParams;

	private String buttonLable;

	private static String fileName;

	private String test_type;

	private String activationMessage = "";

	Map<String, String> fileNameMap = new LinkedHashMap<String, String>();

	Map<String, String> fileNameExcelMap = new LinkedHashMap<String, String>();

	private String errorMessage;

	private String configId;

	private String configName;

	private List<String> configurationNameList;

	private String reportConfigName;

	private String sucessMessage;

	private String generatedMapCountLabel;

	private Integer generatedMapCount;

	private String imageUrl;
	
	private String ajsdata;

	private boolean isDownload = false;

	private boolean technology;
	private boolean stateChange;
	private String frequencyPlan;

	public String getfrequencyPlan() {
		return frequencyPlan;
	}

	public void setfrequencyPlan(String frequencyPlan) {
		this.frequencyPlan = frequencyPlan;
	}

	public boolean isTechnology() {
		return technology;
	}

	public void setTechnology(boolean technology) {
		this.technology = technology;
	}

	public boolean isStateChange() {
		return stateChange;
	}

	public void setStateChange(boolean stateChange) {
		this.stateChange = stateChange;
	}

	public boolean isDownload() {
		return isDownload;
	}

	public void setDownload(boolean isDownload) {
		this.isDownload = isDownload;
	}

	public String getGeneratedMapCountLabel() {
		return generatedMapCountLabel;
	}

	public void setGeneratedMapCountLabel(String generatedMapCountLabel) {
		this.generatedMapCountLabel = generatedMapCountLabel;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Integer getGeneratedMapCount() {
		return generatedMapCount;
	}

	public void setGeneratedMapCount(Integer generatedMapCount) {
		this.generatedMapCount = generatedMapCount;
	}

	public String getTestCommentKpi1() {
		return testCommentKpi1;
	}

	public void setTestCommentKpi1(String testCommentKpi1) {
		this.testCommentKpi1 = testCommentKpi1;
	}

	public String getTestCommentKpi2() {
		return testCommentKpi2;
	}

	public void setTestCommentKpi2(String testCommentKpi2) {
		this.testCommentKpi2 = testCommentKpi2;
	}

	public String getTestCommentKpi3() {
		return testCommentKpi3;
	}

	public void setTestCommentKpi3(String testCommentKpi3) {
		this.testCommentKpi3 = testCommentKpi3;
	}

	public String getTestCommentKpi4() {
		return testCommentKpi4;
	}

	public String getMarketmapId() {
		return marketmapId;
	}

	public void setMarketmapId(String marketmapId) {
		this.marketmapId = marketmapId;
	}

	public void setTestCommentKpi4(String testCommentKpi4) {
		this.testCommentKpi4 = testCommentKpi4;
	}

	public String getTestCommentKpi5() {
		return testCommentKpi5;
	}

	public void setTestCommentKpi5(String testCommentKpi5) {
		this.testCommentKpi5 = testCommentKpi5;
	}

	public String getTestCommentKpi6() {
		return testCommentKpi6;
	}

	public void setTestCommentKpi6(String testCommentKpi6) {
		this.testCommentKpi6 = testCommentKpi6;
	}

	public String getTestCommentKpi7() {
		return testCommentKpi7;
	}

	public void setTestCommentKpi7(String testCommentKpi7) {
		this.testCommentKpi7 = testCommentKpi7;
	}

	public String getSucessMessage() {
		return sucessMessage;
	}

	public void setSucessMessage(String sucessMessage) {
		this.sucessMessage = sucessMessage;
	}

	public String getReportConfigName() {
		return reportConfigName;
	}

	public void setReportConfigName(String reportConfigName) {
		this.reportConfigName = reportConfigName;
	}

	public String getConfigId() {
		return configId;
	}

	public void setConfigId(String configId) {
		this.configId = configId;
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Map<String, String> getFileNameMap() {
		return fileNameMap;
	}

	public void setFileNameMap(Map<String, String> fileNameMap) {
		this.fileNameMap = fileNameMap;
	}

	public Map<String, String> getFileNameExcelMap() {
		return fileNameExcelMap;
	}

	public void setFileNameExcelMap(Map<String, String> fileNameExcelMap) {
		this.fileNameExcelMap = fileNameExcelMap;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getButtonLable() {
		return buttonLable;
	}

	public void setButtonLable(String buttonLable) {
		this.buttonLable = buttonLable;
	}

	public String getActivationMessage() {
		return activationMessage;
	}

	public void setActivationMessage(String activationMessage) {
		this.activationMessage = activationMessage;
	}

	public String getReportingParams() {
		return reportingParams;
	}

	public void setReportingParams(String reportingParams) {
		this.reportingParams = reportingParams;
	}

	public String getFilesOpt() {
		return filesOpt;
	}

	public void setFilesOpt(String filesOpt) {
		this.filesOpt = filesOpt;
	}

	public String[] getFilesIds() {
		return filesIds;
	}

	public void setFilesIds(String[] filesIds) {
		this.filesIds = filesIds;
	}

	public String getTestSummaryComment() {
		return testSummaryComment;
	}

	public void setTestSummaryComment(String testSummaryComment) {
		this.testSummaryComment = testSummaryComment;
	}

	public String getAjaxResponseStr() {
		return ajaxResponseStr;
	}

	public void setAjaxResponseStr(String ajaxResponseStr) {
		this.ajaxResponseStr = ajaxResponseStr;
	}

	public String[] getMarketId() {
		return marketId;
	}

	public void setMarketId(String[] marketId) {
		this.marketId = marketId;
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

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public List<STGNetTest> getSTGNetTestresults() {
		return stgNetTestresults;
	}

	public void setSTGNetTestresults(List<STGNetTest> sTGNetTestresults) {
		stgNetTestresults = sTGNetTestresults;
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

	public List<STGDevice> getSTGDeviceresults() {
		return stgDeviceresults;
	}

	public void setSTGDeviceresults(List<STGDevice> sTGDeviceresults) {
		stgDeviceresults = sTGDeviceresults;
	}

	public String getMapReportType() {
		return mapReportType;
	}

	public void setMapReportType(String mapReportType) {
		this.mapReportType = mapReportType;
	}

	public String getTest_type() {
		return test_type;
	}

	public void setTest_type(String testType) {
		test_type = testType;
	}
	
	/**
	 * Navigation From Same map page
	 */
	public String mapsUser() {
		TestConfigDao testConfigDao = new TestConfigDaoImpl();
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		context.getExternalContext().getSessionMap().remove("testNameMap");
		String roleName = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		String userId = context.getExternalContext().getSessionMap().get(
				"loggedInUserID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserID",
				userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				roleName);
		if (roleName.equals("superadmin")) {
			testNameMap = testConfigDao.getTestNamesInMapInStgDevice();
		} else {
			Set<String> userNameList = new UserDaoImpl().getUsersOfSuperior(
					userId, roleName);
			testNameMap = new TestConfigDaoImpl()
					.getnTestNamesInMapInHierarchy(userNameList.toString()
							.substring(1, userNameList.toString().length() - 1));
			// testNameMap = testConfigDao.getTestNamesInMapInStgDevice(userId);
		}
		context.getExternalContext().getSessionMap().remove("testNameMap");
		context.getExternalContext().getSessionMap().put("testNameMap",
				testNameMap);
		context.getExternalContext().getSessionMap().remove("lattitudes");
		context.getExternalContext().getSessionMap().remove("longitudes");
		context.getExternalContext().getSessionMap().remove("testName");
		context.getExternalContext().getSessionMap().remove("signalStrength");
		context.getExternalContext().getSessionMap().remove("networkType");
		context.getExternalContext().getSessionMap().remove("testSelectNameMap");
		context.getExternalContext().getSessionMap().remove("freqBandMap");
		context.getExternalContext().getSessionMap().remove("testTypeNameMap");
		context.getExternalContext().getSessionMap().remove("networkData");
		context.getExternalContext().getSessionMap().remove("networkRoaming");
		context.getExternalContext().getSessionMap().remove("networkMnc");
		context.getExternalContext().getSessionMap().remove("networkMcc");
		context.getExternalContext().getSessionMap().remove("cellLocationLac");
		context.getExternalContext().getSessionMap().remove("cellLocationCid");
		context.getExternalContext().getSessionMap()
				.remove("signalStrengthSnr");
		context.getExternalContext().getSessionMap().remove("devicePhoneType");
		context.getExternalContext().getSessionMap().remove(
				"timeStampForEachSample");
		context.getExternalContext().getSessionMap().remove("reportPage");
		ReportBean reportBean = (ReportBean) context.getExternalContext()
				.getSessionMap().get("reportBean");
		reportBean.setTestCaseName("");
		reportBean.setMapReportType("signalStrength");
		context.getExternalContext().getSessionMap().put("reportBean",
				reportBean);
		return "welcomeMapsUserPage";
	}

	/**
	 * Navigation From map page to Dashboard Page.
	 */
	/* public void voiceConnectivity(){ */
	public String voiceConnectivity() {
		FacesContext context = FacesContext.getCurrentInstance();
		for (int i = 0; i < 3; i++) {
			context.getExternalContext().getSessionMap().put(
					"jsonUplinkArr_" + i, new JSONArray());
			context.getExternalContext().getSessionMap().put(
					"vqDownlinkcategories_" + i, new JSONArray());
		}
		context.getExternalContext().getSessionMap().remove(
				"primaryDeviceVoice");
		context.getExternalContext().getSessionMap().remove("configJsonArray");
		context.getExternalContext().getSessionMap().remove("allMarketsList");
		context.getExternalContext().getSessionMap().remove("allDevicesList");
		context.getExternalContext().getSessionMap().remove("allTestsList");
		context.getExternalContext().getSessionMap().remove("allFilesList");
		context.getExternalContext().getSessionMap().remove("configObj_0");
		context.getExternalContext().getSessionMap().remove("configObj_1");
		context.getExternalContext().getSessionMap().remove("configObj_2");
		context.getExternalContext().getSessionMap().remove("configObj_3");
		context.getExternalContext().getSessionMap().remove("reportBean");
		context.getExternalContext().getSessionMap().remove("deviceNameMap");
		context.getExternalContext().getSessionMap().remove("testNameMap");
		context.getExternalContext().getSessionMap().remove("filesOpts");
		context.getExternalContext().getSessionMap().remove("marketIdMap");
		context.getExternalContext().getSessionMap().remove("filesIdsMap");
		context.getExternalContext().getSessionMap().remove("jsoArrList");
		context.getExternalContext().getSessionMap().remove("vqtTosList");
		context.getExternalContext().getSessionMap().remove(
				"configurationNameMap");
		// KPI 1
		context.getExternalContext().getSessionMap().put(
				"multipleMarketsUplinkCat", new JSONArray());
		context.getExternalContext().getSessionMap().put(
				"multipleMarketsUplinkVal", "");
		context.getExternalContext().getSessionMap().put("isBothPesqPolqa",
				"false");

		for (int j = 0; j < 4; j++) {
			context.getExternalContext().getSessionMap().put(
					"voiceQualityUplinkChart_Market_" + j, new JSONArray());
			context.getExternalContext().getSessionMap()
					.put("voiceQualityUplinkChartValue_Market_" + j,
							new JSONArray());

			context.getExternalContext().getSessionMap().put(
					"voiceQualityDownLinkChart_Market_" + j, new JSONArray());
			context.getExternalContext().getSessionMap().put(
					"voiceQualityDownLinkVal_Market_" + j, new JSONArray());

			context.getExternalContext().getSessionMap().put(
					"vqSignalStrengthCat_" + j, new JSONArray());
			context.getExternalContext().getSessionMap().put(
					"vqSignalStrengthvals_" + j, new JSONArray());

			context.getExternalContext().getSessionMap().put(
					"vqSignalStrengthCatLte_" + j, new JSONArray());
			context.getExternalContext().getSessionMap().put(
					"vqSignalStrengthvalsLte_" + j, new JSONArray());

			// VOICE QUALITY
			context.getExternalContext().getSessionMap().put(
					"callSetupUplinkVal_" + j, new JSONArray());

			context.getExternalContext().getSessionMap().put(
					"callDropJsoArr_" + j, new JSONArray());
			context.getExternalContext().getSessionMap().put(
					"callDropJsoDownlinkValArr_" + j, new JSONArray());

			context.getExternalContext().getSessionMap().put(
					"idleModeValArr_" + j, new JSONArray());
			context.getExternalContext().getSessionMap().put(
					"callTearDownValArr_" + j, new JSONArray());

			context.getExternalContext().getSessionMap().put(
					"dataConnectivitytcpDownloadArr_" + j, new JSONArray());
			context.getExternalContext().getSessionMap().put(
					"dataConnectivitytcpValArr_" + j, new JSONArray());

			context.getExternalContext().getSessionMap().put(
					"dataConnectivityudpDownloadArr_" + j, new JSONArray());
			context.getExternalContext().getSessionMap().put(
					"dataConnectivityudpValArr_" + j, new JSONArray());

			context.getExternalContext().getSessionMap().put(
					"dataConnectivitydnsDownloadArr_" + j, new JSONArray());
			context.getExternalContext().getSessionMap().put(
					"dataConnectivitydnsValArr_" + j, new JSONArray());

			context.getExternalContext().getSessionMap().put(
					"dataConnectivityftpDownloadArr_" + j, new JSONArray());
			context.getExternalContext().getSessionMap().put(
					"dataConnectivityftpValArr_" + j, new JSONArray());

			context.getExternalContext().getSessionMap().put(
					"dataConnectivityftpUploadArr_" + j, new JSONArray());
			context.getExternalContext().getSessionMap().put(
					"dataConnectivityftpUploadValArr_" + j, new JSONArray());

			context.getExternalContext().getSessionMap().put(
					"dataConnectivityVoipDownloadArr_" + j, new JSONArray());
			context.getExternalContext().getSessionMap().put(
					"dataConnectivityVoipValArr_" + j, new JSONArray());

			context.getExternalContext().getSessionMap()
					.put("dataConnectivitytcpDriveDownloadArr_" + j,
							new JSONArray());
			context.getExternalContext().getSessionMap().put(
					"dataConnectivitytcpDriveValArr_" + j, new JSONArray());

			context.getExternalContext().getSessionMap().put(
					"dataConnectivitytcpDownloadArrLte_" + j, new JSONArray());
			context.getExternalContext().getSessionMap().put(
					"dataConnectivitytcpValArrLte_" + j, new JSONArray());

			context.getExternalContext().getSessionMap().put(
					"dataConnectivityudpDownloadArrLte_" + j, new JSONArray());
			context.getExternalContext().getSessionMap().put(
					"dataConnectivityudpValArrLte_" + j, new JSONArray());

			context.getExternalContext().getSessionMap().put(
					"dataConnectivitydnsDownloadArrLte_" + j, new JSONArray());
			context.getExternalContext().getSessionMap().put(
					"dataConnectivitydnsValArrLte_" + j, new JSONArray());

			context.getExternalContext().getSessionMap().put(
					"dataConnectivityftpDownloadArrLte_" + j, new JSONArray());
			context.getExternalContext().getSessionMap().put(
					"dataConnectivityftpValArrLte_" + j, new JSONArray());

			context.getExternalContext().getSessionMap().put(
					"vqSignalStrengthCatLte_" + j, new JSONArray());
			// //logger.info("vqSignalStrengthCatLte-------------"+rangeListLte);
			// //logger.info("vqSignalStrengthvalsLte---------------"+vqSignalStrengthvalsLte);
			context.getExternalContext().getSessionMap().put(
					"vqSignalStrengthvalsLte_" + j, new JSONArray());

			context.getExternalContext().getSessionMap().put(
					"graphMarket_" + j, "");

			context.getExternalContext().getSessionMap().put(
					"callSetupUplinkValLte_" + j, new JSONArray());

			context.getExternalContext().getSessionMap().put(
					"callDropJsoArrLte_" + j, new JSONArray());
			context.getExternalContext().getSessionMap().put(
					"callDropJsoDownlinkValArrLte_" + j, new JSONArray());

			context.getExternalContext().getSessionMap().put(
					"callSetupDownlinkCat_" + j, new JSONArray());
			context.getExternalContext().getSessionMap().put(
					"callSetupUplinkCat_" + j, new JSONArray());

			context.getExternalContext().getSessionMap().put(
					"graphMarket_" + j, new net.sf.json.JSONObject());

		}
		context.getExternalContext().getSessionMap().remove("isVoiceData");
		context.getExternalContext().getSessionMap().remove("isExternalData");

		context.getExternalContext().getSessionMap()
				.put("isVoiceData", "false");
		context.getExternalContext().getSessionMap().put("isExternalData",
				"false");

		context.getExternalContext().getSessionMap().remove("isTestDetailData");
		context.getExternalContext().getSessionMap().put("isTestDetailData",
				"false");
		//code added by ankit 19-02-16
		context.getExternalContext().getSessionMap().remove("isNetTestData");
		context.getExternalContext().getSessionMap().put("isNetTestData", "false");
		//end code by ank
		context.getExternalContext().getSessionMap().remove("isDownload");
		context.getExternalContext().getSessionMap().put("isDownload", "false");

		context.getExternalContext().getSessionMap().remove("retrievedConfig");
		context.getExternalContext().getSessionMap().put("retrievedConfig",
				"false");

		context.getExternalContext().getSessionMap().put(
				"multipleMarketsDownlinkCat", new JSONArray());
		context.getExternalContext().getSessionMap().put(
				"multipleMarketsDownlinkVal", new JSONArray());

		context.getExternalContext().getSessionMap().put("vqSignalStrengthCat",
				new ArrayList());
		context.getExternalContext().getSessionMap().put(
				"vqSignalStrengthvals", new JSONArray());

		// KPI 1

		// RVP
		context.getExternalContext().getSessionMap().put("callDropJsoArr",
				new JSONArray());
		context.getExternalContext().getSessionMap().put(
				"callDropJsoDownlinkValArr", new JSONArray());
		//

		context.getExternalContext().getSessionMap().put(
				"dataConnectivitytcpDownloadArr", new JSONArray());
		context.getExternalContext().getSessionMap().put(
				"dataConnectivitytcpValArr", new JSONArray());
		context.getExternalContext().getSessionMap().put(
				"dataConnectivityftpDownloadArr", new JSONArray());
		context.getExternalContext().getSessionMap().put(
				"dataConnectivityftpValArr", new JSONArray());

		context.getExternalContext().getSessionMap().put(
				"dataConnectivityudpDownloadArr", new JSONArray());
		context.getExternalContext().getSessionMap().put(
				"dataConnectivityudpValArr", new JSONArray());
		context.getExternalContext().getSessionMap().put(
				"dataConnectivitydnsDownloadArr", new JSONArray());
		context.getExternalContext().getSessionMap().put(
				"dataConnectivitydnsValArr", new JSONArray());
		//
		context.getExternalContext().getSessionMap().put(
				"dataConnectivityftpDownloadArrLte", new JSONArray());
		context.getExternalContext().getSessionMap().put(
				"dataConnectivityftpValArrLte", new JSONArray());
		//
		context.getExternalContext().getSessionMap().put(
				"dataConnectivityVoipDownloadArr", new JSONArray());
		context.getExternalContext().getSessionMap().put(
				"dataConnectivityVoipValArr", new JSONArray());

		// kpi 4

		context.getExternalContext().getSessionMap().put(
				"dataConnectivitytcpDriveDownloadArr", new JSONArray());
		context.getExternalContext().getSessionMap().put(
				"dataConnectivitytcpDriveValArr", new JSONArray());
		//
		context.getExternalContext().getSessionMap().put("callSetupUplinkCat",
				new JSONArray());
		context.getExternalContext().getSessionMap().put("callSetupUplinkVal",
				new JSONArray());

		context.getExternalContext().getSessionMap().put(
				"callSetupDownlinkCat", new JSONArray());
		context.getExternalContext().getSessionMap().put("callRetentionValArr",
				new JSONArray());
		context.getExternalContext().getSessionMap().put("idleModeValArr",
				new JSONArray());
		context.getExternalContext().getSessionMap().put(
				"callSetupSignalStrengthCat", new ArrayList());
		context.getExternalContext().getSessionMap().put(
				"callSetupSignalStrengthvals", new JSONArray());

		context.getExternalContext().getSessionMap().put(
				"dataConnectivitytcpDownloadArr", new JSONArray());
		context.getExternalContext().getSessionMap().put(
				"dataConnectivitytcpValArr", new JSONArray());
		//

		context.getExternalContext().getSessionMap().put(
				"dataConnectivitytcpDriveDownloadArr", new JSONArray());
		context.getExternalContext().getSessionMap().put(
				"dataConnectivitytcpDriveValArr", new JSONArray());

		//
		context.getExternalContext().getSessionMap().put(
				"dataConnectivityftpDownloadArr", new JSONArray());
		context.getExternalContext().getSessionMap().put(
				"dataConnectivityftpValArr", new JSONArray());

		context.getExternalContext().getSessionMap().put(
				"dataConnectivityudpDownloadArr", new JSONArray());
		context.getExternalContext().getSessionMap().put(
				"dataConnectivityudpValArr", new JSONArray());

		context.getExternalContext().getSessionMap().put(
				"multipleMarketsUplinkCatLte", new JSONArray());
		context.getExternalContext().getSessionMap().put(
				"multipleMarketsUplinkValLte", new JSONArray());
		context.getExternalContext().getSessionMap().put(
				"multipleMarketsDownlinkCatLte", new JSONArray());
		context.getExternalContext().getSessionMap().put(
				"multipleMarketsDownlinkValLte", new JSONArray());
		context.getExternalContext().getSessionMap().put(
				"vqSignalStrengthCatLte", new ArrayList());
		context.getExternalContext().getSessionMap().put(
				"vqSignalStrengthvalsLte", new JSONArray());
		context.getExternalContext().getSessionMap().put(
				"dataConnectivitytcpDownloadArrLte", new JSONArray());
		context.getExternalContext().getSessionMap().put(
				"dataConnectivitytcpValArrLte", new JSONArray());
		context.getExternalContext().getSessionMap().put(
				"dataConnectivityudpDownloadArrLte", new JSONArray());
		context.getExternalContext().getSessionMap().put(
				"dataConnectivityudpValArrLte", new JSONArray());
		context.getExternalContext().getSessionMap().put(
				"dataConnectivitydnsDownloadArrLte", new JSONArray());
		context.getExternalContext().getSessionMap().put(
				"dataConnectivitydnsValArrLte", new JSONArray());

		// call setup lte starts
		context.getExternalContext().getSessionMap().put(
				"callSetupUplinkCatLte", new JSONArray());
		context.getExternalContext().getSessionMap().put(
				"callSetupUplinkValLte", new JSONArray());

		context.getExternalContext().getSessionMap().put(
				"callSetupDownlinkCatLte", new JSONArray());
		context.getExternalContext().getSessionMap().put(
				"callRetentionValArrLte", new JSONArray());

		context.getExternalContext().getSessionMap().put(
				"callSetupSignalStrengthCatLte", new ArrayList());
		context.getExternalContext().getSessionMap().put(
				"callSetupSignalStrengthvalsLte", new JSONArray());
		// call setup lte ends

		context.getExternalContext().getSessionMap().put(
				"dataConnectivitydnsDownloadArr", new JSONArray());
		context.getExternalContext().getSessionMap().put(
				"dataConnectivitydnsValArr", new JSONArray());
		context.getExternalContext().getSessionMap()
				.put("upperRangeForGood", 0);
		context.getExternalContext().getSessionMap()
				.put("lowerRangeForGood", 0);
		context.getExternalContext().getSessionMap()
				.put("upperRangeForPoor", 0);
		context.getExternalContext().getSessionMap()
				.put("lowerRangeForPoor", 0);
		context.getExternalContext().getSessionMap().put(
				"upperRangeForAverage", 0);
		context.getExternalContext().getSessionMap().put(
				"lowerRangeForAverage", 0);

		context.getExternalContext().getSessionMap().put(
				"lteupperRangeForGood", 0);
		context.getExternalContext().getSessionMap().put(
				"ltelowerRangeForGood", 0);
		context.getExternalContext().getSessionMap().put(
				"lteupperRangeForPoor", 0);
		context.getExternalContext().getSessionMap().put(
				"ltelowerRangeForPoor", 0);
		context.getExternalContext().getSessionMap().put(
				"lteupperRangeForAverage", 0);
		context.getExternalContext().getSessionMap().put(
				"ltelowerRangeForAverage", 0);

		context.getExternalContext().getSessionMap().remove(
				"marketWiseDcTcpSUmmary");
		context.getExternalContext().getSessionMap().remove(
				"marketWiseDcUdpSUmmary");
		context.getExternalContext().getSessionMap().remove(
				"marketWiseDcDnsSUmmary");
		context.getExternalContext().getSessionMap().remove(
				"marketWiseDcVoipSUmmary");
		context.getExternalContext().getSessionMap().remove(
				"marketWiseDcTcpLteSUmmary");
		context.getExternalContext().getSessionMap().remove(
				"marketWiseDcUdpLteSUmmary");
		context.getExternalContext().getSessionMap().remove(
				"marketWiseDcDnsLteSUmmary");
		context.getExternalContext().getSessionMap()
				.remove("avgHealthIndexMap");
		context.getExternalContext().getSessionMap()
				.remove("testWiseMarketMap");
		/*
		 * marketWiseDcTcpSUmmary
		 */
		String userId = (String) context.getExternalContext().getSessionMap()
				.get("loggedInUserID");
		String roleName = (String) context.getExternalContext().getSessionMap()
				.get("loggedInUserRoleID");

		Set<String> userNameList = new UserDaoImpl().getUsersOfSuperior(userId,
				roleName);
		ReportDao rDao = new ReportDaoImpl();
		deviceNameMap = rDao.getModelNames(userNameList.toString().substring(1,
				userNameList.toString().length() - 1), roleName);
		configurationNameList = rDao.getConfigurationNames();
		configurationNameMap = rDao.getConfigurations();
		context.getExternalContext().getSessionMap().put("customRangeMap",
				new QualityRangeHelper().getUserRanges());
		context.getExternalContext().getSessionMap().remove("testNameMap");
		context.getExternalContext().getSessionMap().put("deviceNameMap",
				deviceNameMap);
		context.getExternalContext().getSessionMap().put(
				"configurationNameList", configurationNameList);
		context.getExternalContext().getSessionMap().put(
				"configurationNameMap", configurationNameMap);
		//context.getExternalContext().getSessionMap().put("ReportTypeForActive",'0');
		// multipleMarkets();
		return "voiceConnectPage";
	}

	// public void ExceltovoiceConnect(){
	public String ExceltovoiceConnect() {
		return "ExceltovoiceConnectPage";
	}

	/**
	 * Navigation From Same Dashboard page
	 */
	// public void voiceConnect(){
	public String voiceConnect() {
		return "voiceConnectionPage";
	}

	public String voiceConnectAnalytics() {
		return "voiceConnectAnalyticsPage";
	}

	public String maptoDashboard() {
		return "maptoDashboardPage";
	}

	public String maptoExcelReport() {
		TestConfigDao testConfigDao = new TestConfigDaoImpl();
		FacesContext context = FacesContext.getCurrentInstance();
		testNameExcelMap = testConfigDao.getTestNamesInMapInVqtDevice();
		context.getExternalContext().getSessionMap().remove("stringfullpath");
		context.getExternalContext().getSessionMap().remove("testNameExcelMap");
		context.getExternalContext().getSessionMap().put("testNameExcelMap",
				testNameExcelMap);
		context.getExternalContext().getSessionMap().remove(
				"STGVQTReportListSize");
		context.getExternalContext().getSessionMap().put(
				"STGVQTReportListSize", 0);
		return "maptoExcelReportPage";
	}

	public String dashboardtoExcelReport() {
		TestConfigDao testConfigDao = new TestConfigDaoImpl();
		FacesContext context = FacesContext.getCurrentInstance();
		testNameExcelMap = testConfigDao.getTestNamesInMapInVqtDevice();
		context.getExternalContext().getSessionMap().remove("stringfullpath");
		context.getExternalContext().getSessionMap().remove("testNameExcelMap");
		context.getExternalContext().getSessionMap().put("testNameExcelMap",
				testNameExcelMap);
		context.getExternalContext().getSessionMap().remove(
				"STGVQTReportListSize");
		context.getExternalContext().getSessionMap().put(
				"STGVQTReportListSize", 0);
		return "dashboardtoExcelReportPage";
	}

	public String voicetoExcelReport() {
		return "voicetoExcelReportPage";
	}

	public String excelReportdetails() {
		TestConfigDao testConfigDao = new TestConfigDaoImpl();
		FacesContext context = FacesContext.getCurrentInstance();
		testNameExcelMap = testConfigDao.getTestNamesInMapInVqtDevice();
		context.getExternalContext().getSessionMap().remove("reportBean");
		context.getExternalContext().getSessionMap().remove("stringfullpath");
		context.getExternalContext().getSessionMap().remove("testNameExcelMap");
		context.getExternalContext().getSessionMap().put("testNameExcelMap",
				testNameExcelMap);
		context.getExternalContext().getSessionMap().remove(
				"STGVQTReportListSize");
		context.getExternalContext().getSessionMap().put(
				"STGVQTReportListSize", 0);
		return "excelReportdetailsPage";
	}

	public String voiceConnectDashboard() {
		return "voiceConnectDashboardPage";
	}

	public String exceltoDashboard() {
		return "exceltoDashboardPage";
	}

	public String dashboardVoice() {
		return "dashboardVoicePage";
	}

	public String dashboardMapsUser() {
		TestConfigDao testConfigDao = new TestConfigDaoImpl();
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		context.getExternalContext().getSessionMap().remove("testNameMap");
		String roleName = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		String userId = context.getExternalContext().getSessionMap().get(
				"loggedInUserID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserID",
				userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				roleName);
		if (roleName.equals("superadmin")) {
			testNameMap = testConfigDao.getTestNamesInMapInStgDevice();
		} else {
			Set<String> userNameList = new UserDaoImpl().getUsersOfSuperior(
					userId, roleName);
			testNameMap = new TestConfigDaoImpl()
					.getnTestNamesInMapInHierarchy(userNameList.toString()
							.substring(1, userNameList.toString().length() - 1));
			// testNameMap = testConfigDao.getTestNamesInMapInStgDevice(userId);
		}
		context.getExternalContext().getSessionMap().remove("testNameMap");
		context.getExternalContext().getSessionMap().put("testNameMap",
				testNameMap);
		context.getExternalContext().getSessionMap().remove("lattitudes");
		context.getExternalContext().getSessionMap().remove("longitudes");
		context.getExternalContext().getSessionMap().remove("testName");
		context.getExternalContext().getSessionMap().remove("signalStrength");
		context.getExternalContext().getSessionMap().remove("networkType");
		context.getExternalContext().getSessionMap().remove("networkData");
		context.getExternalContext().getSessionMap().remove("networkRoaming");
		context.getExternalContext().getSessionMap().remove("networkMnc");
		context.getExternalContext().getSessionMap().remove("networkMcc");
		context.getExternalContext().getSessionMap().remove("cellLocationLac");
		context.getExternalContext().getSessionMap().remove("cellLocationCid");
		context.getExternalContext().getSessionMap()
				.remove("signalStrengthSnr");
		context.getExternalContext().getSessionMap().remove("devicePhoneType");
		context.getExternalContext().getSessionMap().remove(
				"timeStampForEachSample");
		context.getExternalContext().getSessionMap().remove("reportPage");
		ReportBean reportBean = (ReportBean) context.getExternalContext()
				.getSessionMap().get("reportBean");
		reportBean.setTestCaseName("");
		reportBean.setMapReportType("signalStrength");
		context.getExternalContext().getSessionMap().put("reportBean",
				reportBean);

		return "dashboardMapsUserPage";
	}

	/**
	 * Navigation From Dashboard page to Map Page
	 */

	public String voiceMapsUser() {
		TestConfigDao testConfigDao = new TestConfigDaoImpl();
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		context.getExternalContext().getSessionMap().remove("testNameMap");
		String roleName = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		String userId = context.getExternalContext().getSessionMap().get(
				"loggedInUserID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserID",
				userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				roleName);
		if (roleName.equals("superadmin")) {
			testNameMap = testConfigDao.getTestNamesInMapInStgDevice();
		} else {
			Set<String> userNameList = new UserDaoImpl().getUsersOfSuperior(
					userId, roleName);
			testNameMap = new TestConfigDaoImpl()
					.getnTestNamesInMapInHierarchy(userNameList.toString()
							.substring(1, userNameList.toString().length() - 1));
			// testNameMap = testConfigDao.getTestNamesInMapInStgDevice(userId);
		}
		context.getExternalContext().getSessionMap().remove("testNameMap");
		context.getExternalContext().getSessionMap().put("testNameMap",
				testNameMap);
		context.getExternalContext().getSessionMap().remove("lattitudes");
		context.getExternalContext().getSessionMap().remove("longitudes");
		context.getExternalContext().getSessionMap().remove("testName");
		context.getExternalContext().getSessionMap().remove("signalStrength");
		context.getExternalContext().getSessionMap().remove("networkType");
		context.getExternalContext().getSessionMap().remove("networkData");
		context.getExternalContext().getSessionMap().remove("networkRoaming");
		context.getExternalContext().getSessionMap().remove("networkMnc");
		context.getExternalContext().getSessionMap().remove("networkMcc");
		context.getExternalContext().getSessionMap().remove("cellLocationLac");
		context.getExternalContext().getSessionMap().remove("cellLocationCid");
		context.getExternalContext().getSessionMap()
				.remove("signalStrengthSnr");
		context.getExternalContext().getSessionMap().remove("devicePhoneType");
		context.getExternalContext().getSessionMap().remove(
				"timeStampForEachSample");
		context.getExternalContext().getSessionMap().remove("reportPage");
		ReportBean reportBean = (ReportBean) context.getExternalContext()
				.getSessionMap().get("reportBean");
		reportBean.setTestCaseName("");
		reportBean.setMapReportType("signalStrength");
		context.getExternalContext().getSessionMap().put("reportBean",
				reportBean);

		return "voiceMapsUserPage";
	}

	public String excelvoiceMapsUser() {
		TestConfigDao testConfigDao = new TestConfigDaoImpl();
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		context.getExternalContext().getSessionMap().remove("testNameMap");
		String roleName = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		String userId = context.getExternalContext().getSessionMap().get(
				"loggedInUserID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserID",
				userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				roleName);
		if (roleName.equals("superadmin")) {
			testNameMap = testConfigDao.getTestNamesInMapInStgDevice();
		} else {
			Set<String> userNameList = new UserDaoImpl().getUsersOfSuperior(
					userId, roleName);
			testNameMap = new TestConfigDaoImpl()
					.getnTestNamesInMapInHierarchy(userNameList.toString()
							.substring(1, userNameList.toString().length() - 1));
			// testNameMap = testConfigDao.getTestNamesInMapInStgDevice(userId);
		}
		context.getExternalContext().getSessionMap().remove("testNameMap");
		context.getExternalContext().getSessionMap().put("testNameMap",
				testNameMap);
		context.getExternalContext().getSessionMap().remove("lattitudes");
		context.getExternalContext().getSessionMap().remove("longitudes");
		context.getExternalContext().getSessionMap().remove("testName");
		context.getExternalContext().getSessionMap().remove("signalStrength");
		context.getExternalContext().getSessionMap().remove("networkType");
		context.getExternalContext().getSessionMap().remove("networkData");
		context.getExternalContext().getSessionMap().remove("networkRoaming");
		context.getExternalContext().getSessionMap().remove("networkMnc");
		context.getExternalContext().getSessionMap().remove("networkMcc");
		context.getExternalContext().getSessionMap().remove("cellLocationLac");
		context.getExternalContext().getSessionMap().remove("cellLocationCid");
		context.getExternalContext().getSessionMap()
				.remove("signalStrengthSnr");
		context.getExternalContext().getSessionMap().remove("devicePhoneType");
		context.getExternalContext().getSessionMap().remove(
				"timeStampForEachSample");
		context.getExternalContext().getSessionMap().remove("reportPage");
		ReportBean reportBean = (ReportBean) context.getExternalContext()
				.getSessionMap().get("reportBean");
		reportBean.setTestCaseName("");
		reportBean.setMapReportType("signalStrength");
		context.getExternalContext().getSessionMap().put("reportBean",
				reportBean);

		return "excelvoiceMapsUserPage";
	}

	public String welcomeDashboard() {
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				userrole);
		context.getExternalContext().getSessionMap().remove("reportBean");
		return "welcomeDashboardPage";
	}

	/**
	 * Navigation From Dashboard page to Welcome Page
	 */
	public String welcomeVoiceConnectivity() {
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				userrole);
		context.getExternalContext().getSessionMap().remove("reportBean");
		return "welcomeVoiceConnectivityPage";
	}

	public String welcomeExcel() {
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				userrole);
		context.getExternalContext().getSessionMap().remove("reportBean");
		return "welcomeExcelPage";
	}

	public String welcomeDashbordtoConfig() {
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		String roleName = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		String userId = context.getExternalContext().getSessionMap().get(
				"loggedInUserID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserID",
				userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				roleName);
		context.getExternalContext().getSessionMap().remove("reportBean");
		return "welcomeDashbordtoConfigPage";
	}

	/**
	 * Navigation From Dashboard page to Create Configuration Page
	 */
	public String welcomeVoiceConnecttoConfig() {
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		String roleName = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		String userId = context.getExternalContext().getSessionMap().get(
				"loggedInUserID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserID",
				userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				roleName);
		context.getExternalContext().getSessionMap().remove("reportBean");
		return "welcomeVoiceConnecttoConfigPage";
	}

	public String welcomeExceltoConfig() {
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		String roleName = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		String userId = context.getExternalContext().getSessionMap().get(
				"loggedInUserID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserID",
				userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				roleName);
		context.getExternalContext().getSessionMap().remove("reportBean");
		return "welcomeExceltoConfigPage";
	}

	/**
	 * Navigation From Dashboard page to Map Main Page
	 */
	public String welcomeVoiceConnectConfig() {
		TestConfigDao testConfigDao = new TestConfigDaoImpl();
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		context.getExternalContext().getSessionMap().remove("testNameMap");
		String roleName = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		String userId = context.getExternalContext().getSessionMap().get(
				"loggedInUserID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserID",
				userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				roleName);
		if (roleName.equals("superadmin")) {
			testNameMap = testConfigDao.getTestNamesInMapInStgDevice();
		} else {
			Set<String> userNameList = new UserDaoImpl().getUsersOfSuperior(
					userId, roleName);
			testNameMap = new TestConfigDaoImpl()
					.getnTestNamesInMapInHierarchy(userNameList.toString()
							.substring(1, userNameList.toString().length() - 1));
			// testNameMap = testConfigDao.getTestNamesInMapInStgDevice(userId);
		}
		context.getExternalContext().getSessionMap().remove("testNameMap");
		context.getExternalContext().getSessionMap().put("testNameMap",
				testNameMap);
		context.getExternalContext().getSessionMap().remove("reportPage");
		context.getExternalContext().getSessionMap().remove("reportBean");
		return "welcomeVoiceConnectConfigPage";
	}

	public String welcomeExcelConfig() {
		TestConfigDao testConfigDao = new TestConfigDaoImpl();
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		context.getExternalContext().getSessionMap().remove("testNameMap");
		String roleName = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		String userId = context.getExternalContext().getSessionMap().get(
				"loggedInUserID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserID",
				userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				roleName);
		if (roleName.equals("superadmin")) {
			testNameMap = testConfigDao.getTestNamesInMapInStgDevice();
		} else {
			Set<String> userNameList = new UserDaoImpl().getUsersOfSuperior(
					userId, roleName);
			testNameMap = new TestConfigDaoImpl()
					.getnTestNamesInMapInHierarchy(userNameList.toString()
							.substring(1, userNameList.toString().length() - 1));
			// testNameMap = testConfigDao.getTestNamesInMapInStgDevice(userId);
		}
		context.getExternalContext().getSessionMap().remove("testNameMap");
		context.getExternalContext().getSessionMap().put("testNameMap",
				testNameMap);
		context.getExternalContext().getSessionMap().remove("reportPage");
		context.getExternalContext().getSessionMap().remove("reportBean");
		return "welcomeExcelConfigPage";
	}

	public String welcomeDashboardConfig() {
		TestConfigDao testConfigDao = new TestConfigDaoImpl();
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		context.getExternalContext().getSessionMap().remove("testNameMap");
		String roleName = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		String userId = context.getExternalContext().getSessionMap().get(
				"loggedInUserID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserID",
				userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				roleName);
		if (roleName.equals("superadmin")) {
			testNameMap = testConfigDao.getTestNamesInMapInStgDevice();
		} else {
			Set<String> userNameList = new UserDaoImpl().getUsersOfSuperior(
					userId, roleName);
			testNameMap = new TestConfigDaoImpl()
					.getnTestNamesInMapInHierarchy(userNameList.toString()
							.substring(1, userNameList.toString().length() - 1));
			// testNameMap = testConfigDao.getTestNamesInMapInStgDevice(userId);
		}
		context.getExternalContext().getSessionMap().remove("testNameMap");
		context.getExternalContext().getSessionMap().put("testNameMap",
				testNameMap);
		context.getExternalContext().getSessionMap().remove("reportPage");
		context.getExternalContext().getSessionMap().remove("reportBean");
		return "welcomeDashboardConfigPage";
	}

	/**
	 * Navigation From same Map Main Page
	 */
	public String welcomeReportTabConfig() {
		TestConfigDao testConfigDao = new TestConfigDaoImpl();
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		context.getExternalContext().getSessionMap().remove("testNameMap");
		String roleName = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		String userId = context.getExternalContext().getSessionMap().get(
				"loggedInUserID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserID",
				userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				roleName);
		if (roleName.equals("superadmin")) {
			testNameMap = testConfigDao.getTestNamesInMapInStgDevice();
		} else {
			Set<String> userNameList = new UserDaoImpl().getUsersOfSuperior(
					userId, roleName);
			testNameMap = new TestConfigDaoImpl()
					.getnTestNamesInMapInHierarchy(userNameList.toString()
							.substring(1, userNameList.toString().length() - 1));
			// testNameMap = testConfigDao.getTestNamesInMapInStgDevice(userId);
		}
		context.getExternalContext().getSessionMap().remove("testNameMap");
		context.getExternalContext().getSessionMap().put("testNameMap",
				testNameMap);
		context.getExternalContext().getSessionMap().remove("reportPage");
		ReportBean reportBean = (ReportBean) context.getExternalContext()
				.getSessionMap().get("reportBean");
		reportBean.setTestCaseName("");
		reportBean.setMapReportType("signalStrength");
		context.getExternalContext().getSessionMap().put("reportBean",
				reportBean);
		return "welcomeReportTabConfigPage";
	}

	/**
	 * Implemented the Functionality to generate the Dashboard
	 */
	public String generateDashboard() {
		FacesContext context = FacesContext.getCurrentInstance();
		String timeStampSample = "";
		String PesqSample = "";
		String signalStrengthGSMsample = "";
		String signalStrengthGSM15to20sample = "";
		String signalStrengthGSM20abovesample = "";
		ReportDao dao = new ReportDaoImpl();
		STGDeviceInfoResults = dao.getTestNameDashboardDetails(testCaseName);
		for (int i = 0; i < STGDeviceInfoResults.size(); i++) {
			STGDevice deviceInfo = STGDeviceInfoResults.get(i);
			String signalStrengthGSM = deviceInfo.getSignalStrengthGSM();
			String Pesq = deviceInfo.getPESQ();
			double latitude = deviceInfo.getLattitude();
			double longitude = deviceInfo.getLongitude();
			String color1 = "AFD8F8";
			String color2 = "F6BD0F";
			String color3 = "D64646";

			if (Double.parseDouble(signalStrengthGSM) < 15) {
				if (signalStrengthGSMsample.equals("")) {
					signalStrengthGSMsample = "<set value='"
							+ signalStrengthGSM + "' color='" + color1 + "' />";
				} else {
					signalStrengthGSMsample = signalStrengthGSMsample
							+ "<set value='" + signalStrengthGSM + "' color='"
							+ color1 + "' />";
				}
			} else if (Double.parseDouble(signalStrengthGSM) >= 15
					&& Double.parseDouble(signalStrengthGSM) <= 20) {
				if (signalStrengthGSMsample.equals("")) {
					signalStrengthGSMsample = "<set value='"
							+ signalStrengthGSM + "' color='" + color2 + "' />";
				} else {
					signalStrengthGSMsample = signalStrengthGSMsample
							+ "<set value='" + signalStrengthGSM + "' color='"
							+ color2 + "' />";
				}
			} else if (Double.parseDouble(signalStrengthGSM) > 20) {
				if (signalStrengthGSMsample.equals("")) {
					signalStrengthGSMsample = "<set value='"
							+ signalStrengthGSM + "' color='" + color3 + "' />";
				} else {
					signalStrengthGSMsample = signalStrengthGSMsample
							+ "<set value='" + signalStrengthGSM + "' color='"
							+ color3 + "' />";
				}
			}

			if (PesqSample.equals("")) {
				PesqSample = "<set value='" + Pesq + "'/>";
			} else {
				PesqSample = PesqSample + "<set value='" + Pesq + "'/>";
			}

			if (timeStampSample.equals("")) {
				timeStampSample = "<category name='" + latitude + "-"
						+ longitude + "'/>";
			} else {
				timeStampSample = timeStampSample + "<category name='"
						+ latitude + "-" + longitude + "'/>";
			}
		}

		String xmlFileData = "<graph xaxisname='Location' yaxisname='Signal Strength/PESQ' hovercapbg='DEDEBE' hovercapborder='889E6D' rotateNames='0' yAxisMaxValue='50' numdivlines='9' divLineColor='CCCCCC' divLineAlpha='80' decimalPrecision='0'  showAlternateHGridColor='1' AlternateHGridAlpha='30' AlternateHGridColor='CCCCCC' caption='' subcaption=''  > "
				+ "<categories font='Arial' fontSize='11' fontColor='000000'>"
				+ timeStampSample
				+ "</categories> "
				+ "<dataset seriesname='Signal Strength' color='FDC12E'>"
				+ signalStrengthGSMsample
				+ "</dataset>"
				+ "<dataset seriesname='PESQ' color='56B9F9'>"
				+ PesqSample
				+ "</dataset></graph>";

		String salesXmlFileData = "<graph numdivlines='4' lineThickness='3' showValues='0' numVDivLines='10' formatNumberScale='1' "
				+ "rotateNames='1' decimalPrecision='1' anchorRadius='2' anchorBgAlpha='0' numberPrefix='' divLineAlpha='30' "
				+ "showAlternateHGridColor='1' yAxisMaxValue='50' shadowAlpha='50'>"
				+ "<categories>"
				+ timeStampSample
				+ "</categories>"
				+ "<dataset seriesName='Signal Strength' color='A66EDD' anchorBorderColor='A66EDD' anchorRadius='4'>"
				+ signalStrengthGSMsample
				+ "</dataset>"
				+ "<dataset seriesName='PESQ' color='F6BD0F' anchorBorderColor='F6BD0F' anchorRadius='4'>"
				+ PesqSample + "</dataset>" + "</graph>";

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(new StringReader(
					xmlFileData)));
			TransformerFactory tranFactory = TransformerFactory.newInstance();
			Transformer aTransformer = tranFactory.newTransformer();
			Source src = new DOMSource(document);
			String s1 = ((ServletContext) context.getExternalContext()
					.getContext()).getRealPath("/");
			String tempFile = s1 + "resources/Data/ProductSales.xml";
			File fileTemp = new File(tempFile);
			if (fileTemp.exists()) {
				fileTemp.delete();
			}
			Result dest = new StreamResult(new File(s1
					+ "\\resources\\Data\\ProductSales.xml"));
			aTransformer.transform(src, dest);

			Document document1 = builder.parse(new InputSource(
					new StringReader(salesXmlFileData)));
			TransformerFactory tranFactory1 = TransformerFactory.newInstance();
			Transformer aTransformer1 = tranFactory1.newTransformer();
			Source src1 = new DOMSource(document1);
			String tempFile1 = s1 + "resources/Data/SalesCompare.xml";
			File fileTemp1 = new File(tempFile1);
			if (fileTemp1.exists()) {
				fileTemp1.delete();
			}
			Result dest1 = new StreamResult(new File(s1
					+ "\\resources\\Data\\SalesCompare.xml"));
			aTransformer1.transform(src1, dest1);

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		context.getExternalContext().getSessionMap().remove("reportPage");
		context.getExternalContext().getSessionMap().put("reportPage",
				"Dashboard");
		return "generateDashboardMapPage";
	}

	public String excelReport() {

		FacesContext context = FacesContext.getCurrentInstance();
		ReportDao dao = new ReportDaoImpl();
		String userrole = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				userrole);
		if (testCaseName == "" || testCaseName.equals("0")) {
			context.getExternalContext().getRequestMap().put("datamessage",
					"Please Select Test Name");
			return "reportDataValidation";
		}
		List<DeviceInfoTO> STGDeviceInfoTimeStampList = dao
				.getTestNameThroughputDetailsResults(testCaseName, marketmapId,
						test_type);
		if (STGDeviceInfoTimeStampList.size() == 0) {
			List<DeviceInfoTO> STGDeviceInfoMultipleTimeStampList = dao
					.getTestNameMultipleThroughputDetailsResults(testCaseName,
							marketmapId, test_type);
			if (STGDeviceInfoMultipleTimeStampList.size() > 0) {
				STGDeviceInfoTimeStampList = STGDeviceInfoMultipleTimeStampList;
			} else {
				STGDeviceInfoTimeStampList = STGDeviceInfoMultipleTimeStampList;
			}
		} else {
			STGDeviceInfoTimeStampList = STGDeviceInfoTimeStampList;
		}
		List<DeviceInfoTO> STGVQTReportList = new ArrayList<DeviceInfoTO>();
		String testName = "";
		String signalStrength = "";
		String signalStrengthRating = "";
		String networkType = "";
		String timeStampForEachSample = "";
		String eventName = "";
		String eventValue = "";
		String throughputTX = "";
		String throughputRX = "";
		String throughputMain = "";
		String currTxBytes = "";
		String prevTxBytes = "";
		String currRxBytes = "";
		String prevRxBytes = "";
		String throughput = "";

		List testNameList = new ArrayList();
		List signalStrengthList = new ArrayList();
		List networkTypeList = new ArrayList();
		List timeStampForEachSampleList = new ArrayList();
		List throughputList = new ArrayList();
		List signalStrengthListRating = new ArrayList();
		String networkName = "";
		for (int i = 0; i < STGDeviceInfoTimeStampList.size(); i++) {
			DeviceInfoTO deviceInfo = STGDeviceInfoTimeStampList.get(i);
			networkName = deviceInfo.getNetworkType();
			testNameList.add(deviceInfo.getTestName());
			networkTypeList.add(deviceInfo.getNetworkType());
			timeStampForEachSampleList.add(deviceInfo
					.getTimeStampForEachSample());
			eventName = deviceInfo.getEventName();
			if (networkName.matches("GSM")) {
				if (deviceInfo.getSignalStrength().equals("Empty")
						|| deviceInfo.getSignalStrength().equals("")) {
					int signalStrengthGSM = Integer.parseInt("0");
					int signalStrengthGSMValue = -Integer
							.parseInt(SIGNALSTRENGTH_GSM)
							+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
							* signalStrengthGSM;
					signalStrengthList.add(String
							.valueOf(signalStrengthGSMValue + "dBm."));
				} else {
					int signalStrengthGSM = Integer.parseInt(deviceInfo
							.getSignalStrength());
					int signalStrengthGSMValue = -Integer
							.parseInt(SIGNALSTRENGTH_GSM)
							+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
							* signalStrengthGSM;
					signalStrengthList.add(String
							.valueOf(signalStrengthGSMValue + "dBm."));

				}
				signalStrengthListRating.add(deviceInfo.getSignalStrength());
			} else if (networkName.matches("CDMA")) {
				if (deviceInfo.getSignalStrengthCDMA().equals("Empty")
						|| deviceInfo.getSignalStrengthCDMA().equals("")) {
					signalStrengthList.add("0");
				} else {
					signalStrengthList.add(deviceInfo.getSignalStrengthCDMA());
				}
				signalStrengthListRating
						.add(deviceInfo.getSignalStrengthCDMA());
			} else if (networkName.matches("EVDO")) {
				if (deviceInfo.getSignalStrengthEVDO().equals("Empty")
						|| deviceInfo.getSignalStrengthEVDO().equals("")) {
					signalStrengthList.add("0");
				} else {
					signalStrengthList.add(deviceInfo.getSignalStrengthEVDO());
				}
				signalStrengthListRating
						.add(deviceInfo.getSignalStrengthEVDO());
			} else if (networkName.equalsIgnoreCase("LTE (4G)")) {
				if (deviceInfo.getSignalStrengthLTE().equals("Empty")
						|| deviceInfo.getSignalStrengthLTE().equals("")) {
					int signalStrengthLTE = Integer.parseInt("0");
					int signalStrengthLTEValue = signalStrengthLTE;
					signalStrengthList.add(String
							.valueOf(signalStrengthLTEValue + "dBm."));
				} else {
					int signalStrengthLTE = Integer.parseInt(deviceInfo
							.getSignalStrengthLTE());
					int signalStrengthLTEValue = signalStrengthLTE;
					signalStrengthList.add(String
							.valueOf(signalStrengthLTEValue + "dBm."));
				}
				signalStrengthListRating.add(deviceInfo.getSignalStrengthLTE());
			} else {
				if (deviceInfo.getSignalStrength().equals("Empty")
						|| deviceInfo.getSignalStrength().equals("")) {
					int signalStrengthGSM = Integer.parseInt("0");
					int signalStrengthGSMValue = -Integer
							.parseInt(SIGNALSTRENGTH_GSM)
							+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
							* signalStrengthGSM;
					signalStrengthList.add(String
							.valueOf(signalStrengthGSMValue + "dBm."));
				} else {
					int signalStrengthGSM = Integer.parseInt(deviceInfo
							.getSignalStrength());
					int signalStrengthGSMValue = -Integer
							.parseInt(SIGNALSTRENGTH_GSM)
							+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
							* signalStrengthGSM;
					signalStrengthList.add(String
							.valueOf(signalStrengthGSMValue + "dBm."));
				}
				signalStrengthListRating.add(deviceInfo.getSignalStrength());
			}
			double txvalue = 0;
			double rxvalue = 0;
			double mainValue = 0;

			if (i == 0) {
				if (eventName.equalsIgnoreCase("Current TX bytes")) {
					currTxBytes = deviceInfo.getEventValue();
					double h = Double.parseDouble(currTxBytes);
					// double k=30*1024;
					double k = Double.parseDouble(THROUGHPUT);
					txvalue = h * k;
					throughputTX = String.valueOf(txvalue);
					prevTxBytes = currTxBytes;
				}
				if (eventName.equalsIgnoreCase("Current RX bytes")) {
					currRxBytes = deviceInfo.getEventValue();
					double h = Double.parseDouble(currRxBytes);
					// double k=30*1024;
					double k = Double.parseDouble(THROUGHPUT);
					rxvalue = h * k;
					throughputRX = String.valueOf(rxvalue);
					prevRxBytes = currRxBytes;
				}
				if (throughputTX == "") {
					throughputTX = "0";
				}
				if (throughputRX == "") {
					throughputRX = "0";
				}
				mainValue = txvalue + rxvalue;
				mainValue = round(mainValue, 2);
				if (mainValue < 0) {
					mainValue = 0;
				}
				throughputMain = String.valueOf(mainValue);
			} else {
				if (eventName.equalsIgnoreCase("Current TX bytes")) {
					currTxBytes = deviceInfo.getEventValue();
					if (currTxBytes == "") {
						currTxBytes = "0";
					}
					if (prevTxBytes == "") {
						prevTxBytes = "0";
					}
					double h = Double.parseDouble(currTxBytes);
					double d = Double.parseDouble(prevTxBytes);
					// double k=30*1024;
					double k = Double.parseDouble(THROUGHPUT);
					txvalue = (h - d) * k;
					throughputTX = String.valueOf(txvalue);
					prevTxBytes = currTxBytes;
				}
				if (eventName.equalsIgnoreCase("Current RX bytes")) {
					currRxBytes = deviceInfo.getEventValue();
					if (currRxBytes == "") {
						currRxBytes = "0";
					}
					if (prevRxBytes == "") {
						prevRxBytes = "0";
					}
					double h = Double.parseDouble(currRxBytes);
					double d = Double.parseDouble(prevRxBytes);
					// double k=30*1024;
					double k = Double.parseDouble(THROUGHPUT);
					rxvalue = (h - d) * k;
					prevRxBytes = currRxBytes;
				}
				if (throughputTX == "") {
					throughputTX = "0";
				}
				if (throughputRX == "") {
					throughputRX = "0";
				}
				mainValue = txvalue + rxvalue;
				mainValue = round(mainValue, 2);
				if (mainValue < 0) {
					mainValue = 0;
				}
				throughputMain = String.valueOf(mainValue);
			}

			throughputList.add(throughputMain);
		}
		if (timeStampForEachSampleList.size() == 0) {
			context.getExternalContext().getRequestMap().put("datamessage",
					"No Data found for the Particular TestName");
			return "reportDataValidation";
		} else {
			stgDeviceresults = dao.getTestNameMapDetailsResults(testCaseName,
					marketmapId);
			SimpleDateFormat outFormatter = new SimpleDateFormat(
					"MM/dd/yyyy HH:mm:ss");
			SimpleDateFormat outFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			DeviceInfoTO Info2 = null;
			try {
				int j = 0;
				int k = 1;
				for (int i = 0; i < stgDeviceresults.size(); i++) {
					STGDevice deviceInfo = stgDeviceresults.get(i);
					Date date1 = outFormatter.parse(deviceInfo
							.getCallTimestamp());
					if (j < timeStampForEachSampleList.size()) {
						Date date2 = outFormat
								.parse((String) timeStampForEachSampleList
										.get(j));
						if (date1.compareTo(date2) > 0) {
							Info2 = new DeviceInfoTO();
							Info2.setSlno("" + k);
							Info2
									.setTime_stamp((String) timeStampForEachSampleList
											.get(j));
							Info2.setSignalStrength(signalStrengthList.get(j)
									.toString());
							Info2.setThroughput(throughputList.get(j)
									.toString());
							STGVQTReportList.add(Info2);
							j = j + 1;
							k++;
						}
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			if (STGVQTReportList.size() == 0) {
				context.getExternalContext().getRequestMap().put("datamessage",
						"No Data found for the Particular TestName");
				return "reportDataValidation";
			}

		}

		ExcelVQTReport vqtReport = new ExcelVQTReport();
		StringBuffer fmain_fullpath = new StringBuffer();
		fmain_fullpath.append(vqtReport.getVQTReport(STGVQTReportList,
				testCaseName));
		fmain_fullpath = get_the_fullpath_for_jsp(fmain_fullpath);
		String stringfullpath = fmain_fullpath.toString();
		java.util.regex.Pattern pattern = java.util.regex.Pattern
				.compile(propertiesFiledata.get("REPORTS_GENERATE_URL"));
		Matcher matcher = pattern.matcher(stringfullpath);
		String ipAddress = null;
		try {
			InetAddress ip = InetAddress.getLocalHost();
			ipAddress = ip.getHostAddress();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		if (ENVIRONMENT.equals("PROD")) {
			ipAddress = "50.112.27.46";
			stringfullpath = matcher.replaceAll("http://" + ipAddress + ":"
					+ ServerPort + "/");
		} else {
			stringfullpath = matcher.replaceAll("http://" + ipAddress + ":"
					+ ServerPort + "/");
		}
		context.getExternalContext().getSessionMap().remove("stringfullpath");
		context.getExternalContext().getSessionMap().put("stringfullpath",
				stringfullpath);
		return "excelReportPage";
	}

	public static StringBuffer get_the_fullpath_for_jsp(
			StringBuffer stringBuffer) {

		for (int i = 0; i < stringBuffer.length(); i++) {
			if (stringBuffer.charAt(i) == '\\') {
				stringBuffer.replace(i, i + 1, "/");
			}
		}
		return stringBuffer;
	}

	public String generateExcelReport() {
		FacesContext context = FacesContext.getCurrentInstance();
		ReportDao dao = new ReportDaoImpl();
		String userrole = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				userrole);
		if (testCaseName == "" || testCaseName.equals("0")) {
			context.getExternalContext().getRequestMap().put("datamessage",
					"Please Select Test Name");
			return "reportDataValidation";
		}
		List<DeviceInfoTO> STGDeviceInfoTimeStampList = dao
				.getTestNameThroughputDetailsResults(testCaseName, marketmapId,
						test_type);
		if (STGDeviceInfoTimeStampList.size() == 0) {
			List<DeviceInfoTO> STGDeviceInfoMultipleTimeStampList = dao
					.getTestNameMultipleThroughputDetailsResults(testCaseName,
							marketmapId, test_type);
			if (STGDeviceInfoMultipleTimeStampList.size() > 0) {
				STGDeviceInfoTimeStampList = STGDeviceInfoMultipleTimeStampList;
			} else {
				STGDeviceInfoTimeStampList = STGDeviceInfoMultipleTimeStampList;
			}
		} else {
			STGDeviceInfoTimeStampList = STGDeviceInfoTimeStampList;
		}

		List<DeviceInfoTO> STGVQTReportList = new ArrayList<DeviceInfoTO>();
		String testName = "";
		String signalStrength = "";
		String signalStrengthRating = "";
		String networkType = "";
		String timeStampForEachSample = "";
		String eventName = "";
		String eventValue = "";
		String throughputTX = "";
		String throughputRX = "";
		String throughputMain = "";
		String currTxBytes = "";
		String prevTxBytes = "";
		String currRxBytes = "";
		String prevRxBytes = "";
		String throughput = "";

		List testNameList = new ArrayList();
		List signalStrengthList = new ArrayList();
		List networkTypeList = new ArrayList();
		List timeStampForEachSampleList = new ArrayList();
		List throughputList = new ArrayList();
		List signalStrengthListRating = new ArrayList();
		String networkName = "";
		for (int i = 0; i < STGDeviceInfoTimeStampList.size(); i++) {
			DeviceInfoTO deviceInfo = STGDeviceInfoTimeStampList.get(i);
			networkName = deviceInfo.getNetworkType();
			testNameList.add(deviceInfo.getTestName());
			networkTypeList.add(deviceInfo.getNetworkType());
			timeStampForEachSampleList.add(deviceInfo
					.getTimeStampForEachSample());
			eventName = deviceInfo.getEventName();
			if (networkName.matches("GSM")) {
				if (deviceInfo.getSignalStrength().equals("Empty")
						|| deviceInfo.getSignalStrength().equals("")) {
					int signalStrengthGSM = Integer.parseInt("0");
					int signalStrengthGSMValue = -Integer
							.parseInt(SIGNALSTRENGTH_GSM)
							+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
							* signalStrengthGSM;
					signalStrengthList.add(String
							.valueOf(signalStrengthGSMValue + "dBm."));
				} else {
					int signalStrengthGSM = Integer.parseInt(deviceInfo
							.getSignalStrength());
					int signalStrengthGSMValue = -Integer
							.parseInt(SIGNALSTRENGTH_GSM)
							+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
							* signalStrengthGSM;
					signalStrengthList.add(String
							.valueOf(signalStrengthGSMValue + "dBm."));

				}
				signalStrengthListRating.add(deviceInfo.getSignalStrength());
			} else if (networkName.matches("CDMA")) {
				if (deviceInfo.getSignalStrengthCDMA().equals("Empty")
						|| deviceInfo.getSignalStrengthCDMA().equals("")) {
					signalStrengthList.add("0");
				} else {
					signalStrengthList.add(deviceInfo.getSignalStrengthCDMA());
				}
				signalStrengthListRating
						.add(deviceInfo.getSignalStrengthCDMA());
			} else if (networkName.matches("EVDO")) {
				if (deviceInfo.getSignalStrengthEVDO().equals("Empty")
						|| deviceInfo.getSignalStrengthEVDO().equals("")) {
					signalStrengthList.add("0");
				} else {
					signalStrengthList.add(deviceInfo.getSignalStrengthEVDO());
				}
				signalStrengthListRating
						.add(deviceInfo.getSignalStrengthEVDO());
			} else if (networkName.equalsIgnoreCase("LTE (4G)")) {
				if (deviceInfo.getSignalStrengthLTE().equals("Empty")
						|| deviceInfo.getSignalStrengthLTE().equals("")) {
					int signalStrengthLTE = Integer.parseInt("0");
					int signalStrengthLTEValue = signalStrengthLTE;
					signalStrengthList.add(String
							.valueOf(signalStrengthLTEValue + "dBm."));
				} else {
					int signalStrengthLTE = Integer.parseInt(deviceInfo
							.getSignalStrengthLTE());
					int signalStrengthLTEValue = -140 + signalStrengthLTE;
					signalStrengthList.add(String
							.valueOf(signalStrengthLTEValue + "dBm."));
				}
				signalStrengthListRating.add(deviceInfo.getSignalStrengthLTE());
			} else {
				if (deviceInfo.getSignalStrength().equals("Empty")
						|| deviceInfo.getSignalStrength().equals("")) {
					int signalStrengthGSM = Integer.parseInt("0");
					int signalStrengthGSMValue = -Integer
							.parseInt(SIGNALSTRENGTH_GSM)
							+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
							* signalStrengthGSM;
					signalStrengthList.add(String
							.valueOf(signalStrengthGSMValue + "dBm."));
				} else {
					int signalStrengthGSM = Integer.parseInt(deviceInfo
							.getSignalStrength());
					int signalStrengthGSMValue = -Integer
							.parseInt(SIGNALSTRENGTH_GSM)
							+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
							* signalStrengthGSM;
					signalStrengthList.add(String
							.valueOf(signalStrengthGSMValue + "dBm."));
				}
				signalStrengthListRating.add(deviceInfo.getSignalStrength());
			}
			double txvalue = 0;
			double rxvalue = 0;
			double mainValue = 0;

			if (i == 0) {
				if (eventName.equalsIgnoreCase("Current TX bytes")) {
					currTxBytes = deviceInfo.getEventValue();
					double h = Double.parseDouble(currTxBytes);
					// double k=30*1024;
					double k = Double.parseDouble(THROUGHPUT);
					txvalue = h * k;
					throughputTX = String.valueOf(txvalue);
					prevTxBytes = currTxBytes;
				}
				if (eventName.equalsIgnoreCase("Current RX bytes")) {
					currRxBytes = deviceInfo.getEventValue();
					double h = Double.parseDouble(currRxBytes);
					// double k=30*1024;
					double k = Double.parseDouble(THROUGHPUT);
					rxvalue = h * k;
					throughputRX = String.valueOf(rxvalue);
					prevRxBytes = currRxBytes;
				}
				if (throughputTX == "") {
					throughputTX = "0";
				}
				if (throughputRX == "") {
					throughputRX = "0";
				}
				mainValue = txvalue + rxvalue;
				mainValue = round(mainValue, 2);
				if (mainValue < 0) {
					mainValue = 0;
				}
				throughputMain = String.valueOf(mainValue);
			} else {
				if (eventName.equalsIgnoreCase("Current TX bytes")) {
					currTxBytes = deviceInfo.getEventValue();
					if (currTxBytes == "") {
						currTxBytes = "0";
					}
					if (prevTxBytes == "") {
						prevTxBytes = "0";
					}
					double h = Double.parseDouble(currTxBytes);
					double d = Double.parseDouble(prevTxBytes);
					// double k=30*1024;
					double k = Double.parseDouble(THROUGHPUT);
					txvalue = (h - d) * k;
					throughputTX = String.valueOf(txvalue);
					prevTxBytes = currTxBytes;
				}
				if (eventName.equalsIgnoreCase("Current RX bytes")) {
					currRxBytes = deviceInfo.getEventValue();
					if (currRxBytes == "") {
						currRxBytes = "0";
					}
					if (prevRxBytes == "") {
						prevRxBytes = "0";
					}
					double h = Double.parseDouble(currRxBytes);
					double d = Double.parseDouble(prevRxBytes);
					// double k=30*1024;
					double k = Double.parseDouble(THROUGHPUT);
					rxvalue = (h - d) * k;
					prevRxBytes = currRxBytes;
				}
				if (throughputTX == "") {
					throughputTX = "0";
				}
				if (throughputRX == "") {
					throughputRX = "0";
				}
				mainValue = txvalue + rxvalue;
				mainValue = round(mainValue, 2);
				if (mainValue < 0) {
					mainValue = 0;
				}
				throughputMain = String.valueOf(mainValue);
			}

			throughputList.add(throughputMain);
		}
		if (timeStampForEachSampleList.size() == 0) {
			context.getExternalContext().getRequestMap().put("datamessage",
					"No Data found for the Particular TestName");
			return "reportDataValidation";
		} else {
			stgDeviceresults = dao.getTestNameMapDetailsResults(testCaseName,
					marketmapId);
			SimpleDateFormat outFormatter = new SimpleDateFormat(
					"MM/dd/yyyy HH:mm:ss");
			SimpleDateFormat outFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			DeviceInfoTO Info2 = null;
			try {
				int j = 0;
				int k = 1;
				for (int i = 0; i < stgDeviceresults.size(); i++) {
					STGDevice deviceInfo = stgDeviceresults.get(i);
					Date date1 = outFormatter.parse(deviceInfo
							.getCallTimestamp());
					if (j < timeStampForEachSampleList.size()) {
						Date date2 = outFormat
								.parse((String) timeStampForEachSampleList
										.get(j));
						if (date1.compareTo(date2) > 0) {
							Info2 = new DeviceInfoTO();
							Info2.setSlno("" + k);
							Info2
									.setTime_stamp((String) timeStampForEachSampleList
											.get(j));
							Info2.setSignalStrength(signalStrengthList.get(j)
									.toString());
							Info2.setThroughput(throughputList.get(j)
									.toString());
							STGVQTReportList.add(Info2);
							j = j + 1;
							k++;
						}
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			if (STGVQTReportList.size() == 0) {
				context.getExternalContext().getRequestMap().put("datamessage",
						"No Data found for the Particular TestName");
				return "reportDataValidation";
			}

		}
		context.getExternalContext().getSessionMap().remove("stringfullpath");
		context.getExternalContext().getSessionMap().remove("STGVQTReportList");
		context.getExternalContext().getSessionMap().put("STGVQTReportList",
				STGVQTReportList);
		context.getExternalContext().getSessionMap().remove(
				"STGVQTReportListSize");
		context.getExternalContext().getSessionMap().put(
				"STGVQTReportListSize", STGVQTReportList.size());
		return "reportDataSuccess";
	}

	/**
	 * Implemented the Functionality to generate the Map for the Airometric
	 * reports.
	 * 
	 * @throws Exception
	 */
	public String generateMapReport() throws Exception {
		FacesContext context = FacesContext.getCurrentInstance();
		ReportBean reportBean = (ReportBean) context.getExternalContext()
				.getSessionMap().get("reportBean");
		// logger.info("rangename----------"+rangeName);
		// context.getExternalContext().getSessionMap().remove("reportBean");
		// reportBean.setMarketId("");
		ReportDao dao = new ReportDaoImpl();
		int count = 0;
		VQTsTo vqTo = new VQTsTo();
		List<FileHistoryTO> filedetails = new ArrayList<FileHistoryTO>();
		HashMap<String, List<FileHistoryTO>> fileHistory = new HashMap<String, List<FileHistoryTO>>();
		context.getExternalContext().getSessionMap().put("isTestDetailData",
				"false");
		String jsoStr = context.getExternalContext().getRequestParameterMap()
				.get("reportingParams");
		// logger.info("jsoStr "+context.getExternalContext().getRequestParameterMap().get("reportingParams"));
		// logger.info("context.getExternalContext().getRequestParameterMap()---------"+context.getExternalContext().getRequestParameterMap());
		// logger.info("jsoStr length "+jsoStr.length());
		if (jsoStr.length() > 0) {
			String allMarketsStr = context.getExternalContext()
					.getRequestParameterMap().get("allMarkets");
			String allDevicesStr = context.getExternalContext()
					.getRequestParameterMap().get("allDevices");
			String allTestStr = context.getExternalContext()
					.getRequestParameterMap().get("hiddenTestcaseArr");
			String allFilesStr = context.getExternalContext()
					.getRequestParameterMap().get("filesSelectedArr");
			Set<String> marketSet = new HashSet<String>(Arrays
					.asList(allMarketsStr.split(",")));
			Set<String> deviceSet = new HashSet<String>(Arrays
					.asList(allDevicesStr.split(",")));
			// Set<String> testSet = new
			// HashSet<String>(Arrays.asList(allTestStr
			// .split(",")));
			Set<String> fileSet = new HashSet<String>(Arrays.asList(allFilesStr
					.split(",")));
			List<String> allMarketsList = new ArrayList<String>();
			List<String> allDevicesList = new ArrayList<String>();
			List<String> allTestsList = new ArrayList<String>();
			List<String> allFilesList = new ArrayList<String>();

			allMarketsList = Arrays.asList(allMarketsStr.split(","));
			allDevicesList = Arrays.asList(allDevicesStr.split(","));
			allTestsList = Arrays.asList(allTestStr.split(","));
			allFilesList = Arrays.asList(allFilesStr.split(","));

			JSONObject jsnobject = new JSONObject(jsoStr);
			org.json.me.JSONArray jsoArr = jsnobject.getJSONArray("finalJso");
			org.json.me.JSONArray jsoArrToManipulate = jsnobject
					.getJSONArray("finalJso");
			List<VQTsTo> vqtTosList = new ArrayList<VQTsTo>();
			context.getExternalContext().getSessionMap().remove("configObj_0");
			context.getExternalContext().getSessionMap().remove("configObj_1");
			context.getExternalContext().getSessionMap().remove("configObj_2");
			context.getExternalContext().getSessionMap().remove("configObj_3");
			// logger.info("jsoArr.length() :"+jsoArr.length());
			if (jsoArr.length() > 0) {
				// Update Counter table for each generate map report & save
				// configuration.
				// counterType for generate map report is 0 and save
				// configuration is 1.
				Integer counterType = 0;
				Integer tUserId = Integer.valueOf(context.getExternalContext()
						.getSessionMap().get("loggedInUserID").toString());
				dao.manageCounter(tUserId, counterType);
				setGeneratedMapCountLabel("Generated  Count :");
				generatedMapCount = dao.getGeneratedMapCount(counterType,
						tUserId);
				setGeneratedMapCount(generatedMapCount);
				// logger.info("generatedMapCount :"+generatedMapCount);
				for (int i = 0; i < jsoArr.length(); i++) {
					org.json.me.JSONObject configObi = new org.json.me.JSONObject(
							jsoArr.get(i).toString());
					List<String> myList = new ArrayList<String>(Arrays
							.asList(configObi.get("filesName").toString()
									.substring(
											1,
											configObi.get("filesName")
													.toString().length() - 1)
									.split(",")));
					List<String> marketsList = new ArrayList<String>(Arrays
							.asList(configObi.get("filesName").toString()
									.substring(
											1,
											configObi.get("filesName")
													.toString().length() - 1)
									.split(",")));
					String deviceName = configObi.getString("deviceName");
					filedetails = dao.getMarketFiledetails(myList);
					fileHistory.put("device" + (i + 1), filedetails);
					String testRoutrName = configObi.getString("testName");
					String markets = configObi.getString("marketName");
					String mrktId = markets.substring(1, markets.length() - 1);
					String marketName = new ReportDaoImpl()
							.getMarketName(mrktId);
					String imeiNum = configObi
							.getString("filesName")
							.substring(
									configObi.getString("filesName")
											.lastIndexOf("_") + 1,
									configObi.getString("filesName").length() - 2);
					context.getExternalContext().getSessionMap().put(
							"configObj_" + i, jsoArr.get(i));
					vqTo = new VQTsTo();
					vqTo.setDeviceName(deviceName);
					vqTo.setTestname(testRoutrName);
					vqTo.setMarketName(marketName);
					vqtTosList.add(vqTo);
				}
				for (int cnt = 0; cnt < 4; cnt++) {
					removeSession(context, cnt);
				}

				/*---------------Summary Map Code Begins--------------------*/
				String marketName = "";
				String marketId = "";
				for (int k = 0; k < allMarketsList.size(); k++) {
					if (marketName == "") {
						marketName = new ReportDaoImpl()
								.getMarketName(allMarketsList.get(k));
						marketId = allMarketsList.get(k);
					} else {
						marketName = marketName
								+ ","
								+ new ReportDaoImpl()
										.getMarketName(allMarketsList.get(k));
						marketId = marketId + "," + allMarketsList.get(k);
					}
				}
				String testNames = "";
				for (int o = 0; o < allTestsList.size(); o++) {
					// String masterTesName = allTestsList.get(b);
					if (testNames == "") {
						testNames = allTestsList.get(o);
					} else {
						testNames = testNames + "," + allTestsList.get(o);
					}
				}
				generateSummaryMap(marketName, marketId, testNames);
				/*---------------Summary Map Code End--------------------*/

				HashMap<String, List<String>> marketFileMap = (HashMap<String, List<String>>) context
						.getExternalContext().getSessionMap().get(
								"marketFileListMap");// new HashMap<String,
				// List<String>>();
				Map<String, List<String>> marketFiles = (Map<String, List<String>>) context
						.getExternalContext().getSessionMap().get(
								"marketwiseFiles");
				int VQTCount = 0;
				int MOCount = 0;
				int FTPCount = 0;
				int ExternalCount = 0;
				int FinalVQTCount = 0;
				int FinalMOCount = 0;
				int FinalFTPCount = 0;
				int FinalExternalCount = 0;
				for (int c = 0; c < allFilesList.size(); c++) {
					String masterFileName = allFilesList.get(c);
					if (masterFileName.contains("VQTResults")) {
						VQTCount = VQTCount + 1;
					} else if (masterFileName.contains("mo")) {
						MOCount = MOCount + 1;
					} else if (masterFileName.contains("NetTest")
							|| masterFileName.contains("ftp")) {
						FTPCount = FTPCount + 1;
					} else if (masterFileName.contains("externaltest")) {
						ExternalCount = ExternalCount + 1;
					}
					if (c == allFilesList.size() - 1) {
						FinalVQTCount = VQTCount;
						FinalMOCount = MOCount;
						FinalFTPCount = FTPCount;
						FinalExternalCount = ExternalCount;
					}
				}

				context.getExternalContext().getSessionMap().remove("VQTCount");
				context.getExternalContext().getSessionMap().put("VQTCount",
						FinalVQTCount);
				context.getExternalContext().getSessionMap().remove(
						"FinalMOCount");
				context.getExternalContext().getSessionMap().put(
						"FinalMOCount", FinalMOCount);
				context.getExternalContext().getSessionMap().remove(
						"FinalFTPCount");
				context.getExternalContext().getSessionMap().put(
						"FinalFTPCount", FinalFTPCount);
				context.getExternalContext().getSessionMap().remove(
						"FinalExternalCount");
				context.getExternalContext().getSessionMap().put(
						"FinalExternalCount", FinalExternalCount);

				String[] deviceArr = null;
				// testCaseName = "g2";
				// logger.info("jsoStr------------"+jsoStr);
				// logger.info("allDevicesList------------"+allDevicesList);
				// logger.info("testCaseName------------"+testCaseName);
				// logger.info("jsoArrToManipulate------------"+jsoArrToManipulate);
				// logger.info("allMarketsList------------"+allMarketsList);
				// logger.info("allTestsList------------"+allDevicesList);
				// logger.info("allDevicesList------------"+allTestsList);
				// logger.info("allFilesList------------"+allFilesList);
				// logger.info("marketFileMap------------"+marketFileMap);
				context.getExternalContext().getSessionMap().put("isVoip",
						"false");
				context.getExternalContext().getSessionMap().put("isBrowser",
						"false");

				populateKpiDataForCharts(allDevicesList, getMarketId(),
						testCaseName, jsoArrToManipulate, allMarketsList,
						allTestsList, allFilesList, marketFileMap, VQTCount,
						FinalMOCount, FinalFTPCount, FinalExternalCount);
				/*
				 * vQmultipleMarketsMaps(allDevicesList, getMarketId(),
				 * testCaseName, jsoArrToManipulate, allMarketsList,
				 * allTestsList, allFilesList,marketFileMap);
				 */
				String deviceId = new ReportDaoImpl()
						.getDeviceId(getDeviceName());
				int primaryDeviceVoice = 10;
				ReportDao rdao = new ReportDaoImpl();
				String voiceQualityAvg = "0";// rdao.getDeviceAvgVoiceQty(getDeviceName(),getMarketId()[0],testCaseName);
				String marketNamevalue = "marketName";// new
				// ReportDaoImpl().getMarketName(getMarketId()[0]);
				context.getExternalContext().getSessionMap().put(
						"primaryDeviceVoice", voiceQualityAvg);
				context.getExternalContext().getSessionMap().put(
						"reportDevice", getDeviceName());
				context.getExternalContext().getSessionMap().put(
						"reportMarket", getMarketId());
				context.getExternalContext().getSessionMap().put(
						"reportTestName", testCaseName);
				context.getExternalContext().getSessionMap().put("marketName",
						marketNamevalue);
				context.getExternalContext().getSessionMap().remove(
						"jsonArrayLength");
				context.getExternalContext().getSessionMap().put(
						"jsonArrayLength", jsoArr.length());
				context.getExternalContext().getSessionMap().remove(
						"jsoArrList");

				context.getExternalContext().getSessionMap().remove(
						"configJsonArray");
				context.getExternalContext().getSessionMap().put(
						"configJsonArray", jsoStr);

				context.getExternalContext().getSessionMap().remove(
						"allMarketsList");
				context.getExternalContext().getSessionMap().put(
						"allMarketsList", allMarketsStr);

				context.getExternalContext().getSessionMap().remove(
						"allDevicesList");
				context.getExternalContext().getSessionMap().put(
						"allDevicesList", allDevicesStr);

				context.getExternalContext().getSessionMap().remove(
						"allTestsList");
				context.getExternalContext().getSessionMap().put(
						"allTestsList", allTestStr);

				context.getExternalContext().getSessionMap().remove(
						"allFilesList");
				context.getExternalContext().getSessionMap().put(
						"allFilesList", allFilesStr);

				context.getExternalContext().getSessionMap().remove(
						"vqtTosList");
				context.getExternalContext().getSessionMap().put("vqtTosList",
						vqtTosList);

				context.getExternalContext().getSessionMap().remove(
						"marketLength");
				context.getExternalContext().getSessionMap().put(
						"marketLength", getMarketId().length);
				context.getExternalContext().getSessionMap().remove(
						"filesIdsMap");
				setDeviceName("Select Device Type");
				setTestCaseName("Select Test Name");
				setReportType("Select Report");
				setMarketId("Select Market".split(","));
				setFilesIds("Select Files".split(","));
				setSucessMessage("");
				setErrorMessage("");

				context.getExternalContext().getSessionMap().put(
						"isTestDetailData", "true");
			} else {
				context.getExternalContext().getSessionMap().put(
						"isTestDetailData", "false");
				errorMessage = "Please select atleast one combination.";
				reportBean.setErrorMessage(errorMessage);
				context.getExternalContext().getSessionMap().put("reportBean",
						reportBean);
			}
		}
		//generateReportBarChart();
		return "mapDataSuccess";
	}

	public static String generateGoogleSummaryVoiceMap(
			List<FileHistoryTO> fileHistory, String device,
			List<String> summaryMaplatitudeList,
			List<String> summaryMaplongititudeList,
			List<String> summaryMarketList, List<String> summaryMapDeviceList,
			List<String> summaryMapTestList) {
		String test_name = "";
		String test_type = "";
		String market_id = "";
		String marketName = "";
		String marketIdList = "";
		String devicemodel = "";
		String testname[] = null;
		String device_model[] = null;
		String mapListDetails = null;
		for (int i = 0; i < fileHistory.size(); i++) {
			FileHistoryTO fileHistoryTO = fileHistory.get(i);
			if (test_name == "") {
				test_name = fileHistoryTO.getTest_name();
				test_type = fileHistoryTO.getTest_type();
				market_id = fileHistoryTO.getMarket_Id();
				devicemodel = fileHistoryTO.getDevice_model();
			} else {
				test_type = test_type + "," + fileHistoryTO.getTest_type();
				market_id = market_id + "," + fileHistoryTO.getMarket_Id();
			}
		}
		List<String> tmpList2 = Arrays.asList(market_id);
		TreeSet<String> Uniquemarket_id = new TreeSet<String>(tmpList2);
		ReportDao reportDao = new ReportDaoImpl();
		marketName = reportDao.getMarketNameList(getFormatStr(Uniquemarket_id));
		marketIdList = reportDao.getMarketIdList(getFormatStr(Uniquemarket_id));

		mapListDetails = generateSummaryGoogleMap(devicemodel, test_name,
				marketIdList, marketName, summaryMaplatitudeList,
				summaryMaplongititudeList, summaryMarketList,
				summaryMapDeviceList, summaryMapTestList);
		return mapListDetails;
	}

	/**
	 * Implemented the Functionality to generate the Summary Map .
	 */
	public static String generateSummaryGoogleMap(String devicemodel,
			String test_name, String marketIdList, String marketNam,
			List<String> summaryMaplatitudeList,
			List<String> summaryMaplongititudeList,
			List<String> summaryMarketList, List<String> summaryMapDeviceList,
			List<String> summaryMapTestList) {
		List<String> latloglist = new ArrayList<String>();
		KpiDao dao = new KpiDaoImpl();
		if (devicemodel != null && test_name != null && marketNam != null) {
			String marketList[] = marketIdList.split(",");
			String marketNameList[] = marketNam.split(",");
			for (int k = 0; k < marketList.length; k++) {
				latloglist = dao.getGeoLocationParameters(devicemodel,
						test_name, marketList[k]);
				if (latloglist.size() > 0) {
					summaryMaplatitudeList.add(String
							.valueOf(latloglist.get(0)));
					summaryMaplongititudeList.add(String.valueOf(latloglist
							.get(1)));
					summaryMarketList.add(marketNameList[k]);
					summaryMapDeviceList.add(devicemodel);
					summaryMapTestList.add(test_name);
				}
			}
		}
		return summaryMaplatitudeList + "&&" + summaryMaplongititudeList + "&&"
				+ summaryMarketList + "&&" + summaryMapDeviceList + "&&"
				+ summaryMapTestList;
	}

	/**
	 * Implemented the Functionality to generate the Summary Map .
	 */
	public String generateSummaryGoogMap(List<String> summarylatitudeList,
			List<String> summarylongitiudeList, List<String> summaryMarketList,
			List<String> summaryDeviceList, List<String> summaryTestList) {
		FacesContext context = FacesContext.getCurrentInstance();
		if (summarylatitudeList.size() > 0) {
			String latitudes = "";
			String longitudes = "";
			String marketName = "";
			String device = "";
			String test_name = "";

			for (int k = 0; k < summarylatitudeList.size(); k++) {
				if (latitudes == "") {
					latitudes = summarylatitudeList.get(k);
					longitudes = summarylongitiudeList.get(k);
					marketName = summaryMarketList.get(k);
					device = summaryDeviceList.get(k);
					test_name = summaryTestList.get(k);
				} else {
					latitudes = latitudes + "," + summarylatitudeList.get(k);
					longitudes = longitudes + ","
							+ summarylongitiudeList.get(k);
					marketName = marketName + "," + summaryMarketList.get(k);
					device = device + "," + summaryDeviceList.get(k);
					test_name = test_name + "," + summaryTestList.get(k);
				}
			}

			context.getExternalContext().getSessionMap().remove(
					"Summarylattitudes");
			context.getExternalContext().getSessionMap().remove(
					"Summarylongitudes");
			context.getExternalContext().getSessionMap().remove(
					"SummaryMarkets");
			context.getExternalContext().getSessionMap()
					.remove("SummaryDevice");
			context.getExternalContext().getSessionMap().remove(
					"Summarytest_name");

			context.getExternalContext().getSessionMap().put(
					"Summarylattitudes",
					latitudes.substring(1, latitudes.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"Summarylongitudes",
					longitudes.substring(1, longitudes.length() - 1));
			context.getExternalContext().getSessionMap().put("SummaryMarkets",
					marketName.substring(1, marketName.length() - 1));
			context.getExternalContext().getSessionMap().put("SummaryDevice",
					device.substring(1, device.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"Summarytest_name",
					test_name.substring(1, test_name.length() - 1));
		}
		return "Success";
	}

	/**
	 * Implemented the Functionality to generate the Summary Map .
	 */
	public String generateSummaryMap(String market, String marketId,
			String testNames) {
		FacesContext context = FacesContext.getCurrentInstance();
		KpiDao dao = new KpiDaoImpl();
		List<String> latloglist = new ArrayList<String>();
		List<String> summarylatitudeList = new ArrayList<String>();
		List<String> summarylongitiudeList = new ArrayList<String>();
		List<String> summaryMarketList = new ArrayList<String>();
		if (market != null) {
			String latitudes = "";
			String longitudes = "";
			String marketName = "";
			String marketList[] = marketId.split(",");
			String marketNameList[] = market.split(",");
			String testNamesList[] = testNames.split(",");

			for (int k = 0; k < marketList.length; k++) {
				latloglist = dao.getGeoLocationParametersForMarket(
						marketList[k], testNamesList[k]);//,1
				if(latloglist.size()==0)
				{
					latloglist = dao.getGeoLocationParametersForMarket(
							marketList[k], testNamesList[k]);//,0
				}
				summarylatitudeList.add(String.valueOf(latloglist.get(0)));
				summarylongitiudeList.add(String.valueOf(latloglist.get(1)));
				summaryMarketList.add(marketNameList[k]);
			}
			latitudes = summarylatitudeList.toString();
			longitudes = summarylongitiudeList.toString();
			marketName = summaryMarketList.toString();

			context.getExternalContext().getSessionMap().remove(
					"Summarylattitudes");
			context.getExternalContext().getSessionMap().remove(
					"Summarylongitudes");
			context.getExternalContext().getSessionMap().remove(
					"SummaryMarkets");

			context.getExternalContext().getSessionMap().put(
					"Summarylattitudes",
					latitudes.substring(1, latitudes.length() - 1).trim());
			context.getExternalContext().getSessionMap().put(
					"Summarylongitudes",
					longitudes.substring(1, longitudes.length() - 1).trim());
			context.getExternalContext().getSessionMap().put("SummaryMarkets",
					marketName.substring(1, marketName.length() - 1).trim());
		}
		return "Success";
	}

	/**
	 * Implemented the Functionality to generate the Map .
	 */
	public String generateGoogleMapReport() {
		// logger.info("generate map Rep-++--" + frequencyPlan);
		FacesContext context = FacesContext.getCurrentInstance();
		String url = null;
		MapUtil mapUtil = new MapUtil();
		String ipAddress = null;
		String fileName = null;
		String isQxdmPresent = "false";

		QualityRangeTo qualityTo = new QualityRangeTo();
		QualityRangeTo qualityLteTo = new QualityRangeTo();
		// logger.info("reportConfigName-------------"+reportConfigName);

		qualityTo = new QualityRangeHelper().getNonLteQualityrange(rangeName);
		qualityLteTo = new QualityRangeHelper().getLteQualityrange(rangeName);

		context.getExternalContext().getSessionMap().put("upperRangeForGood",
				qualityTo.getUpperRangeForGood());
		context.getExternalContext().getSessionMap().put("lowerRangeForGood",
				qualityTo.getLowerRangeForGood());
		context.getExternalContext().getSessionMap().put("upperRangeForPoor",
				qualityTo.getUpperRangeForPoor());
		context.getExternalContext().getSessionMap().put("lowerRangeForPoor",
				qualityTo.getLowerRangeForPoor());
		context.getExternalContext().getSessionMap().put(
				"upperRangeForAverage", qualityTo.getUpperRangeForAvg());
		context.getExternalContext().getSessionMap().put(
				"lowerRangeForAverage", qualityTo.getLowerRangeForAvg());

		context.getExternalContext().getSessionMap().put(
				"lteupperRangeForGood", qualityLteTo.getUpperRangeForGood());
		context.getExternalContext().getSessionMap().put(
				"ltelowerRangeForGood", qualityLteTo.getLowerRangeForGood());
		context.getExternalContext().getSessionMap().put(
				"lteupperRangeForPoor", qualityLteTo.getUpperRangeForPoor());
		context.getExternalContext().getSessionMap().put(
				"ltelowerRangeForPoor", qualityLteTo.getLowerRangeForPoor());
		context.getExternalContext().getSessionMap().put(
				"lteupperRangeForAverage", qualityLteTo.getUpperRangeForAvg());
		context.getExternalContext().getSessionMap().put(
				"ltelowerRangeForAverage", qualityLteTo.getLowerRangeForAvg());
		if (testCaseName == "" || testCaseName.equals("0")) {
			context.getExternalContext().getRequestMap().put("datamessage",
					"Please Select Test Name");
			return "mapDataValidation";
		}
		if (marketmapId == "" || marketmapId.equals("0")) {
			context.getExternalContext().getRequestMap().put("datamessage",
					"Please Select Market");
			return "mapDataValidation";
		}
		if (test_type == "" || test_type.equals("0")) {
			context.getExternalContext().getRequestMap().put("datamessage",
					"Please Select TestType");
			return "mapDataValidation";
		}
		System.out.println("Report type "+mapReportType);
		if (mapReportType.equals("signalStrength")) {
			// logger.info();
			// logger.info(context.getExternalContext().getSessionMap());
			String userrole = context.getExternalContext().getSessionMap().get(
					"loggedInUserRoleID").toString();
			context.getExternalContext().getSessionMap().put(
					"loggedInUserRoleID", userrole);
			UserDao userDao = new UserDaoImpl();
			List<DeviceInfoTO> deviceInfoCallList = userDao.getAllDeviceInfo(
					testCaseName, marketmapId, test_type, frequencyPlan);
			List<DeviceInfoTO> deviceInfoList = userDao.getAllDeviceInfo(
					testCaseName, marketmapId, test_type, frequencyPlan);
			// List<String>deviceInfoCallDropList =
			// connectivityDao.getAllDeviceInfoCallDropList(test_name,market_id,test_type);
			List<String> deviceInfoCallDropList = userDao
					.getAllDeviceInfoCallDropList(testCaseName, marketmapId,
							test_type);//, 1

			if (deviceInfoCallDropList.size() == 0) {
				  deviceInfoCallDropList = userDao
				  .getAllDeviceInfoCallDropList(testCaseName, marketmapId,
				  test_type);//,2
			}
			System.out.println();
			if (deviceInfoList.size() == 0) {
				List<DeviceInfoTO> deviceMultipleInfoList = userDao
						.getAllMultiplDeviceInfo(testCaseName, marketmapId,
								test_type);
				if (deviceMultipleInfoList.size() > 0) {
					deviceInfoList = deviceMultipleInfoList;
				} else {
					deviceInfoList = deviceMultipleInfoList;
				}
			}

			if (deviceInfoCallDropList.size() == 0
					&& deviceInfoCallList.size() == 0) {
				List<String> deviceMultipleCallDropList = userDao
						.getAllMultipleDeviceInfoCallDropList(testCaseName,
								marketmapId, test_type);
				if (deviceMultipleCallDropList.size() > 0) {
					deviceInfoCallDropList = deviceMultipleCallDropList;
				} else {
					deviceInfoCallDropList = deviceMultipleCallDropList;
				}
			}
			try {
				InetAddress ip = InetAddress.getLocalHost();
				ipAddress = ip.getHostName();

				File f1 = new File(QXDM_FILE_PATH);
				File file1[] = f1.listFiles();
				String filename = null;// f.getName();
				for (File f : file1) {
					String tempFileName = f.getName();
					if ((tempFileName.startsWith("QXDM_" + testCaseName))
							&& (tempFileName.endsWith(".txt"))) {
						isQxdmPresent = "true";
						fileName = f.getName();
					}
				}
			} catch (UnknownHostException e) {
				logger.error(e.getMessage());
			}
			if (deviceInfoList.size() <= 0) {
				context.getExternalContext().getRequestMap().remove(
						"datamessage");
				context.getExternalContext().getRequestMap().put("datamessage",
						"No Data Found for the Selected Test Name");
				removeSession(context);
				return "mapDataValidation";
			} else {
				String latitudes = "";
				String longitudes = "";
				String testName = "";
				String networkMnc = "";
				String signalStrength = "";
				String signalStrengthLTT = "";
				String signalStrengthRating = "";
				String networkType = "";
				String networkData = "";
				String networkRoaming = "";
				String networkMcc = "";
				String cellLocationLac = "";
				String cellLocationCid = "";
				String signalStrengthSnr = "";
				String signalStrengthCDMA = "";
				String signalStrengthEVDO = "";
				String devicePhoneType = "";
				String timeStampForEachSample = "";
				String timeStampForSample = "";
				String neighbourInfo = "";
				String neighbourMainString = "";
				String signalStrengthCDMACIO = "";
				String signalStrengthEVDOECIO = "";

				String signalStrengthLTERSRP = "";
				String signalStrengthLTERSRQ = "";
				String signalStrengthLTERSSNR = "";
				String signalStrengthLTECQI = "";
				String callDrop = "";
				String signalStrengthGSMStr="";
				String network = "";
				String imei = "";
				// String details = "";
				List<String> freqList = new ArrayList<String>();
				List lattitudeList = new ArrayList();
				List longitudeList = new ArrayList();
				List testNameList = new ArrayList();
				List callDropStatusList = new ArrayList();
				List signalStrengthList = new ArrayList();
				List signalStrengthListRating = new ArrayList();
				List signalStrengthLt = new ArrayList();
				List networkTypeList = new ArrayList();
				List networkDataList = new ArrayList();
				List networkRoamingList = new ArrayList();
				List networkMncList = new ArrayList();
				List networkMccList = new ArrayList();
				List cellLocationCidList = new ArrayList();
				List cellLocationLacList = new ArrayList();
				List signalStrengthSnrList = new ArrayList();
				List devicePhoneTypeList = new ArrayList();
				List timeStampForEachSampleList = new ArrayList();
				List timeStampForSampleList = new ArrayList();
				List signalStrengthCDMACIOList = new ArrayList();
				List signalStrengthGSMList = new ArrayList();
				List signalStrengthEVDOECIOList = new ArrayList();
				List signalStrengthCDMAList = new ArrayList();
				List signalStrengthEVDOList = new ArrayList();
				List signalStrengthLTERSRPList = new ArrayList();
				List signalStrengthLTERSRQList = new ArrayList();
				List signalStrengthLTERSSNRList = new ArrayList();
				List signalStrengthLTECQIList = new ArrayList();
				List wifiInfoBssid = new ArrayList();
				List wifiInfoRssi = new ArrayList();
				List wifiInfoLinkSpeed = new ArrayList();

				List neighbourMainList = new ArrayList();
				List imeiList = new ArrayList();
				String neighbourtStr = null;
				String networkName = "";

				if (deviceInfoCallDropList.size() > 0) {
					for (int i = 0; i < deviceInfoCallDropList.size(); i++) {
						callDrop = callDrop + ","
								+ deviceInfoCallDropList.get(i);
					}
				}
				for (int i = 0; i < deviceInfoList.size(); i++) {
					List neighbourInfoList = new ArrayList();
					List neighboursubInfoList = new ArrayList();
					DeviceInfoTO deviceInfo = deviceInfoList.get(i);
					networkName = deviceInfo.getNetworkType();
					lattitudeList.add(deviceInfo.getLattitude());
					longitudeList.add(deviceInfo.getLongitude());
					testNameList.add(deviceInfo.getTestName());
					//Added by Ankit on 29/03/16
					imeiList.add(deviceInfo.getImei());
					if (deviceInfo.getFreqBand().trim().length() < 1) {
						freqList.add("");
					} else {
						freqList.add(deviceInfo.getFreqBand().trim());
					}

					// logger.info("deviceInfo.getFreqBand().trim()----"
					// + freqList.toString());
					wifiInfoBssid.add("");
					wifiInfoLinkSpeed.add("");
					wifiInfoRssi.add("");

					if (networkName.matches("GSM")) {
						if (deviceInfo.getSignalStrength().equals("Empty")
								|| deviceInfo.getSignalStrength().equals("")) {
							int signalStrengthGSM = Integer.parseInt("0");
							int signalStrengthGSMValue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthList.add(String
									.valueOf(signalStrengthGSMValue + "dBm."));
							int signalStrengthLtvalue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthLt.add(String.valueOf(
									signalStrengthLtvalue).substring(
									1,
									String.valueOf(signalStrengthLtvalue)
											.length()));
							signalStrengthCDMACIOList.add("null");
							signalStrengthEVDOECIOList.add("null");
							signalStrengthSnrList.add("null");
							signalStrengthLTECQIList.add("null");
							signalStrengthLTERSSNRList.add("null");
							signalStrengthLTERSRQList.add("null");
							signalStrengthLTERSRPList.add("null");
						} else {
							int signalStrengthGSM = Integer.parseInt(deviceInfo
									.getSignalStrength());
							int signalStrengthGSMValue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthList.add(String
									.valueOf(signalStrengthGSMValue + "dBm."));
							int signalStrengthLtvalue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthLt.add(String.valueOf(
									signalStrengthLtvalue).substring(
									1,
									String.valueOf(signalStrengthLtvalue)
											.length()));
							signalStrengthCDMACIOList.add("null");
							signalStrengthEVDOECIOList.add("null");
							signalStrengthSnrList.add("null");
							signalStrengthLTECQIList.add("null");
							signalStrengthLTERSSNRList.add("null");
							signalStrengthLTERSRQList.add("null");
							signalStrengthLTERSRPList.add("null");
						}
						signalStrengthListRating.add(deviceInfo
								.getSignalStrength());
					} else if (networkName.toUpperCase().trim().contains("CDMA")) {
						if (deviceInfo.getSignalStrengthCDMA().equals("Empty")
								|| deviceInfo.getSignalStrengthCDMA()
										.equals("")) {
							signalStrengthList.add("0");
							signalStrengthLt.add("0");
							
							signalStrengthLTECQIList.add("null");
							signalStrengthLTERSSNRList.add("null");
							signalStrengthLTERSRQList.add("null");
							signalStrengthLTERSRPList.add("null");
						} else {
							signalStrengthList.add(deviceInfo
									.getSignalStrengthCDMA());
							signalStrengthLt.add(deviceInfo
									.getSignalStrengthCDMA());
							signalStrengthCDMACIOList.add(deviceInfo
									.getSignalStrengthCDMACIO());
							signalStrengthEVDOECIOList.add("null");
							signalStrengthSnrList.add("null");
							signalStrengthLTECQIList.add("null");
							signalStrengthLTERSSNRList.add("null");
							signalStrengthLTERSRQList.add("null");
							signalStrengthLTERSRPList.add("null");
						}
						if(deviceInfo.getSignalStrengthGSM().equalsIgnoreCase("false"))
						{
							signalStrengthGSMList.add(deviceInfo.getSignalStrengthGSM());
							signalStrengthCDMACIOList.add(deviceInfo.getSignalStrengthCDMACIO());
							signalStrengthSnrList.add(deviceInfo.getSignalStrengthSnr());
							signalStrengthEVDOECIOList.add(deviceInfo.getSignalStrengthEVDOECIO());
							signalStrengthCDMAList.add(deviceInfo.getSignalStrengthCDMA());
							signalStrengthEVDOList.add(deviceInfo.getSignalStrengthEVDO());
						}	
						signalStrengthListRating.add(deviceInfo
								.getSignalStrengthCDMA());
					} else if (networkName.toUpperCase().trim().contains("EVDO") || networkName.toUpperCase().trim().contains("eHRPD")
							|| networkName.toUpperCase().trim().contains("EHRPD (3G)") || networkName.toUpperCase().trim().contains("EVDO-A")
						    || networkName.toUpperCase().trim().contains("EVDO-B")) {
						if (deviceInfo.getSignalStrengthEVDO().equals("Empty")
								|| deviceInfo.getSignalStrengthEVDO()
										.equals("")) {
							signalStrengthList.add("0");
							signalStrengthLt.add("0");
							signalStrengthEVDOECIOList.add(deviceInfo
									.getSignalStrengthEVDOECIO());
							signalStrengthSnrList.add(deviceInfo
									.getSignalStrengthSnr());
							signalStrengthCDMACIOList.add("null");
							signalStrengthLTECQIList.add("null");
							signalStrengthLTERSSNRList.add("null");
							signalStrengthLTERSRQList.add("null");
							signalStrengthLTERSRPList.add("null");
						} else {
							signalStrengthList.add(deviceInfo
									.getSignalStrengthEVDO());
							signalStrengthLt.add(deviceInfo
									.getSignalStrengthEVDO());
							signalStrengthEVDOECIOList.add(deviceInfo
									.getSignalStrengthEVDOECIO());
							signalStrengthSnrList.add(deviceInfo
									.getSignalStrengthSnr());
							signalStrengthCDMACIOList.add("null");
							signalStrengthLTECQIList.add("null");
							signalStrengthLTERSSNRList.add("null");
							signalStrengthLTERSRQList.add("null");
							signalStrengthLTERSRPList.add("null");
						}
						if(deviceInfo.getSignalStrengthGSM().equalsIgnoreCase("false"))
						{
							signalStrengthGSMList.add(deviceInfo.getSignalStrengthGSM());
							signalStrengthCDMACIOList.add(deviceInfo.getSignalStrengthCDMACIO());
							signalStrengthSnrList.add(deviceInfo.getSignalStrengthSnr());
							signalStrengthEVDOECIOList.add(deviceInfo.getSignalStrengthEVDOECIO());
							signalStrengthCDMAList.add(deviceInfo.getSignalStrengthCDMA());
							signalStrengthEVDOList.add(deviceInfo.getSignalStrengthEVDO());
						}	
						signalStrengthListRating.add(deviceInfo
								.getSignalStrengthEVDO());
					} else if (networkName.equalsIgnoreCase("LTE (4G)")) {
						if (deviceInfo.getSignalStrengthLTE().equals("Empty")
								|| deviceInfo.getSignalStrengthLTE().equals("")) {
							int signalStrengthLTE = Integer.parseInt("0");
							int signalStrengthLTEValue = signalStrengthLTE;
							signalStrengthList.add(String
									.valueOf(signalStrengthLTEValue + "dBm."));
							int signalStrengthLTValue = signalStrengthLTE;
							signalStrengthLt.add(String.valueOf(
									signalStrengthLTValue).substring(
									1,
									String.valueOf(signalStrengthLTValue)
											.length()));
							// signalStrengthLt.add(String.valueOf(deviceInfo.getSignalStrengthLTERSRP()).substring(1,
							// String.valueOf(deviceInfo.getSignalStrengthLTERSRP()).length()));
							signalStrengthLTERSRPList.add(deviceInfo
									.getSignalStrengthLTERSRP());
							signalStrengthLTERSRQList.add(deviceInfo
									.getSignalStrengthLTERSRQ());
							signalStrengthLTERSSNRList.add(deviceInfo
									.getSignalStrengthLTERSSNR());
							signalStrengthLTECQIList.add(deviceInfo
									.getSignalStrengthLTECQI());
							signalStrengthCDMACIOList.add("null");
							signalStrengthEVDOECIOList.add("null");
							signalStrengthSnrList.add("null");
						} else {
							// System.out
							// .println("deviceInfo.getSignalStrengthLTE()--------"
							// + deviceInfo.getSignalStrengthLTE());
							int signalStrengthLTE = Integer.parseInt(deviceInfo
									.getSignalStrengthLTE());
							int signalStrengthLTEValue = -140
									+ signalStrengthLTE;
							signalStrengthLTE = Integer.parseInt("0");
							signalStrengthList.add(String
									.valueOf(signalStrengthLTEValue + "dBm."));
							int signalStrengthLTValue = signalStrengthLTE;
							signalStrengthLt.add(String.valueOf(
									signalStrengthLTValue).substring(
									1,
									String.valueOf(signalStrengthLTValue)
											.length()));
							// signalStrengthLt.add(String.valueOf(deviceInfo.getSignalStrengthLTERSRP()).substring(1,
							// String.valueOf(deviceInfo.getSignalStrengthLTERSRP()).length()));
							signalStrengthLTERSRPList.add(deviceInfo
									.getSignalStrengthLTERSRP());
							signalStrengthLTERSRQList.add(deviceInfo
									.getSignalStrengthLTERSRQ());
							signalStrengthLTERSSNRList.add(deviceInfo
									.getSignalStrengthLTERSSNR());
							signalStrengthLTECQIList.add(deviceInfo
									.getSignalStrengthLTECQI());
							signalStrengthCDMACIOList.add("null");
							signalStrengthEVDOECIOList.add("null");
							signalStrengthSnrList.add("null");
						}
						signalStrengthListRating.add(deviceInfo
								.getSignalStrengthLTE());
					} else if (networkName.matches("wifi")) {
						if (deviceInfo.getSignalStrength().equals("Empty")
								|| deviceInfo.getSignalStrength().equals("")) {
							int signalStrengthGSM = Integer.parseInt("0");
							int signalStrengthGSMValue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthList.add(String
									.valueOf(signalStrengthGSMValue + "dBm."));
							int signalStrengthLtvalue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthLt.add(String.valueOf(
									signalStrengthLtvalue).substring(
									1,
									String.valueOf(signalStrengthLtvalue)
											.length()));
							signalStrengthCDMACIOList.add("null");
							signalStrengthEVDOECIOList.add("null");
							signalStrengthSnrList.add("null");
							signalStrengthLTECQIList.add("null");
							signalStrengthLTERSSNRList.add("null");
							signalStrengthLTERSRQList.add("null");
							signalStrengthLTERSRPList.add("null");
						} else {
							int signalStrengthGSM = Integer.parseInt(deviceInfo
									.getSignalStrength());
							int signalStrengthGSMValue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthList.add(String
									.valueOf(signalStrengthGSMValue + "dBm."));
							int signalStrengthLtvalue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthLt.add(String.valueOf(
									signalStrengthLtvalue).substring(
									1,
									String.valueOf(signalStrengthLtvalue)
											.length()));
							signalStrengthCDMACIOList.add("null");
							signalStrengthEVDOECIOList.add("null");
							signalStrengthSnrList.add("null");
							signalStrengthLTECQIList.add("null");
							signalStrengthLTERSSNRList.add("null");
							signalStrengthLTERSRQList.add("null");
							signalStrengthLTERSRPList.add("null");
						}
						// System.out
						// .println("getWifiInfoLinkSpeed()----------------------"
						// + deviceInfo.getWifiInfoLinkSpeed());
						wifiInfoBssid.set(i, deviceInfo.getWifiInfoBssid());
						wifiInfoLinkSpeed.set(i, deviceInfo
								.getWifiInfoLinkSpeed());
						wifiInfoRssi.set(i, deviceInfo.getWifiRssi());
						signalStrengthListRating.add(deviceInfo
								.getSignalStrength());
					} else {
						if (deviceInfo.getSignalStrength().equals("Empty")
								|| deviceInfo.getSignalStrength().equals("")) {
							int signalStrengthGSM = Integer.parseInt("0");
							int signalStrengthGSMValue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthList.add(String
									.valueOf(signalStrengthGSMValue + "dBm."));
							int signalStrengthLtvalue = Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthLt.add(String.valueOf(
									signalStrengthLtvalue).substring(
									1,
									String.valueOf(signalStrengthLtvalue)
											.length()));
							signalStrengthCDMACIOList.add("null");
							signalStrengthEVDOECIOList.add("null");
							signalStrengthSnrList.add("null");
							signalStrengthLTECQIList.add("null");
							signalStrengthLTERSSNRList.add("null");
							signalStrengthLTERSRQList.add("null");
							signalStrengthLTERSRPList.add("null");
						} else {
							int signalStrengthGSM = Integer.parseInt(deviceInfo
									.getSignalStrength());
							int signalStrengthGSMValue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthList.add(String
									.valueOf(signalStrengthGSMValue + "dBm."));
							int signalStrengthLtvalue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthLt.add(String.valueOf(
									signalStrengthLtvalue).substring(
									1,
									String.valueOf(signalStrengthLtvalue)
											.length()));
							signalStrengthCDMACIOList.add("null");
							signalStrengthEVDOECIOList.add("null");
							signalStrengthSnrList.add("null");
							signalStrengthLTECQIList.add("null");
							signalStrengthLTERSSNRList.add("null");
							signalStrengthLTERSRQList.add("null");
							signalStrengthLTERSRPList.add("null");
						}
						signalStrengthListRating.add(deviceInfo
								.getSignalStrength());
					}
					network = deviceInfo.getNetworkType().trim();
					networkTypeList.add(network);
					networkDataList.add(deviceInfo.getNetworkDataState());
					networkRoamingList.add(deviceInfo.getNetworkRoaming());
					networkMccList.add(deviceInfo.getNetworkMCC());
					networkMncList.add(deviceInfo.getNetworkMNC());
					cellLocationCidList.add(deviceInfo.getCellLocationCID());
					cellLocationLacList.add(deviceInfo.getCellLocationLAC());
					devicePhoneTypeList.add(deviceInfo.getDevicePhoneType());
					if (fileName != null) {
						if (ENVIRONMENT.equals("PROD")) {
							ipAddress = "50.112.27.46";
							url = "http://" + ipAddress + ":" + ServerPort
									+ "/FileService/" + fileName;
						} else {
							url = "http://" + ipAddress + ":" + ServerPort
									+ "/FileService/" + fileName;
						}
						System.out.println("url on reports bean page :"+url);
						String dateValue = "'--"
								+ deviceInfo.getTimeStampForEachSample() + "'";
						timeStampForEachSampleList.add("<b>"
								+ deviceInfo.getTimeStampForEachSample()
								+ "</b>");
						timeStampForSampleList.add(deviceInfo
								.getTimeStampForEachSample());
					} else {
						timeStampForEachSampleList.add("<b>Test Times:</b>"
								+ deviceInfo.getTimeStampForEachSample());
						timeStampForSampleList.add(deviceInfo
								.getTimeStampForEachSample());
					}

					neighbourInfoList.add(deviceInfo.getNeighbourInfo());
					for (int j = 0; j < neighbourInfoList.size(); j++) {
						if (null != neighbourInfoList.get(j)
								&& !neighbourInfoList.get(j).toString().equals(
										"Empty")) {
							String neighbourInfoArray[] = neighbourInfoList
									.get(j).toString().split("\\$");
							for (int a = 0; a < neighbourInfoArray.length; a++) {
								String neighbourSubInfoArray[] = neighbourInfoArray[a]
										.split("\\^");
								if (a == 0) {
									if (neighbourtStr == null) {
										if (neighbourSubInfoArray[0]
												.matches("-1")
												&& neighbourSubInfoArray[1]
														.matches("-1")) {
											neighbourtStr = "Cell: Empty" + ","
													+ "Lac:Empty" + ","
													+ "PSC:"
													+ neighbourSubInfoArray[3]
													+ "," + "RSSI:"
													+ neighbourSubInfoArray[4];
										} else if (neighbourSubInfoArray[0]
												.matches("-1")) {
											neighbourtStr = "Cell: Empty" + ","
													+ "Lac:"
													+ neighbourSubInfoArray[1]
													+ "," + "PSC:"
													+ neighbourSubInfoArray[3]
													+ "," + "RSSI:"
													+ neighbourSubInfoArray[4];
										} else if (neighbourSubInfoArray[1]
												.matches("-1")) {
											neighbourtStr = "Cell:"
													+ neighbourSubInfoArray[0]
													+ "," + "Lac:Empty" + ","
													+ "PSC:"
													+ neighbourSubInfoArray[3]
													+ "," + "RSSI:"
													+ neighbourSubInfoArray[4];
										} else {
											neighbourtStr = "Cell:"
													+ neighbourSubInfoArray[0]
													+ "," + "Lac:"
													+ neighbourSubInfoArray[1]
													+ "," + "PSC:"
													+ neighbourSubInfoArray[3]
													+ "," + "RSSI:"
													+ neighbourSubInfoArray[4];
										}
									} else {
										if (neighbourSubInfoArray[0]
												.matches("-1")
												&& neighbourSubInfoArray[1]
														.matches("-1")) {
											neighbourtStr = neighbourtStr
													+ "|||Cell:Empty" + ","
													+ "Lac:Empty" + ","
													+ "PSC:"
													+ neighbourSubInfoArray[3]
													+ "," + "RSSI:"
													+ neighbourSubInfoArray[4];
										} else if (neighbourSubInfoArray[0]
												.matches("-1")) {
											neighbourtStr = neighbourtStr
													+ "|||Cell:Empty" + ","
													+ "Lac:"
													+ neighbourSubInfoArray[1]
													+ "," + "PSC:"
													+ neighbourSubInfoArray[3]
													+ "," + "RSSI:"
													+ neighbourSubInfoArray[4];
										} else if (neighbourSubInfoArray[1]
												.matches("-1")) {
											neighbourtStr = neighbourtStr
													+ "|||Cell:"
													+ neighbourSubInfoArray[0]
													+ "," + "Lac:Empty" + ","
													+ "PSC:"
													+ neighbourSubInfoArray[3]
													+ "," + "RSSI:"
													+ neighbourSubInfoArray[4];
										} else {
											neighbourtStr = neighbourtStr
													+ "|||Cell:"
													+ neighbourSubInfoArray[0]
													+ "," + "Lac:"
													+ neighbourSubInfoArray[1]
													+ "," + "PSC:"
													+ neighbourSubInfoArray[3]
													+ "," + "RSSI:"
													+ neighbourSubInfoArray[4];
										}

									}
								} else if (a == (neighbourInfoArray.length - 1)) {
									if (neighbourSubInfoArray[0].matches("-1")
											&& neighbourSubInfoArray[1]
													.matches("-1")) {
										neighbourtStr = neighbourtStr
												+ "|||Cell:Empty" + ","
												+ "Lac:Empty" + "," + "PSC:"
												+ neighbourSubInfoArray[3]
												+ "," + "RSSI:"
												+ neighbourSubInfoArray[4]
												+ "||";
									} else if (neighbourSubInfoArray[0]
											.matches("-1")) {
										neighbourtStr = neighbourtStr
												+ "|||Cell:Empty" + ","
												+ "Lac:"
												+ neighbourSubInfoArray[1]
												+ "," + "PSC:"
												+ neighbourSubInfoArray[3]
												+ "," + "RSSI:"
												+ neighbourSubInfoArray[4]
												+ "||";
									} else if (neighbourSubInfoArray[1]
											.matches("-1")) {
										neighbourtStr = neighbourtStr
												+ "|||Cell:"
												+ neighbourSubInfoArray[0]
												+ "," + "Lac:Empty" + ","
												+ "PSC:"
												+ neighbourSubInfoArray[3]
												+ "," + "RSSI:"
												+ neighbourSubInfoArray[4]
												+ "||";
									} else {
										neighbourtStr = neighbourtStr
												+ "|||Cell:"
												+ neighbourSubInfoArray[0]
												+ "," + "Lac:"
												+ neighbourSubInfoArray[1]
												+ "," + "PSC:"
												+ neighbourSubInfoArray[3]
												+ "," + "RSSI:"
												+ neighbourSubInfoArray[4]
												+ "||";
									}

								} else {
									if (neighbourSubInfoArray[0].matches("-1")
											&& neighbourSubInfoArray[1]
													.matches("-1")) {
										neighbourtStr = neighbourtStr
												+ "|||Cell:Empty" + ","
												+ "Lac:Empty" + "," + "PSC:"
												+ neighbourSubInfoArray[3]
												+ "," + "RSSI:"
												+ neighbourSubInfoArray[4];
									} else if (neighbourSubInfoArray[0]
											.matches("-1")) {
										neighbourtStr = neighbourtStr
												+ "|||Cell:Empty" + ","
												+ "Lac:"
												+ neighbourSubInfoArray[1]
												+ "," + "PSC:"
												+ neighbourSubInfoArray[3]
												+ "," + "RSSI:"
												+ neighbourSubInfoArray[4];
									} else if (neighbourSubInfoArray[1]
											.matches("-1")) {
										neighbourtStr = neighbourtStr
												+ "|||Cell:"
												+ neighbourSubInfoArray[0]
												+ "," + "Lac:Empty" + ","
												+ "PSC:"
												+ neighbourSubInfoArray[3]
												+ "," + "RSSI:"
												+ neighbourSubInfoArray[4];
									} else {
										neighbourtStr = neighbourtStr
												+ "|||Cell:"
												+ neighbourSubInfoArray[0]
												+ "," + "Lac:"
												+ neighbourSubInfoArray[1]
												+ "," + "PSC:"
												+ neighbourSubInfoArray[3]
												+ "," + "RSSI:"
												+ neighbourSubInfoArray[4];
									}
								}
							}
						} else {
							neighbourtStr = neighbourtStr
									+ "|||Cell:Empty,Lac:Empty,PSC:Empty,RSSI:Empty||";
						}
					}
					neighbourInfoList.clear();
					if (neighboursubInfoList.size() > 0) {
						boolean single = true;
						for (int x = 0; x < neighboursubInfoList.size(); x++) {
							if (neighbourtStr == null) {
								neighbourtStr = neighboursubInfoList.get(x)
										.toString();
							} else {
								neighbourtStr = neighbourtStr
										+ ","
										+ neighboursubInfoList.get(x)
												.toString();
							}
						}
					}

				}
				// logger.info("lattitudeList----" +
				// lattitudeList.size());
				if (null != neighbourtStr) {
					neighbourtStr = neighbourtStr.substring(0, neighbourtStr
							.length() - 1);
					neighbourtStr = neighbourtStr.substring(0, neighbourtStr
							.length() - 1);
				}

				// Collections.sort(lattitudeList);
				// Collections.sort(longitudeList);
				latitudes = lattitudeList.toString();
				longitudes = longitudeList.toString();
				String freqBand = freqList.toString();
				testName = testNameList.toString();
				//added by Ankit on 29/03/16
				imei = imeiList.toString();
				signalStrength = signalStrengthList.toString();
				signalStrengthLTT = signalStrengthLt.toString();
				signalStrengthRating = signalStrengthListRating.toString();
				networkType = networkTypeList.toString();
				networkData = networkDataList.toString();
				networkRoaming = networkRoamingList.toString();
				networkMnc = networkMncList.toString();
				networkMcc = networkMccList.toString();
				cellLocationLac = cellLocationLacList.toString();
				cellLocationCid = cellLocationCidList.toString();
				
				devicePhoneType = devicePhoneTypeList.toString();
				timeStampForEachSample = timeStampForEachSampleList.toString();
				timeStampForSample = timeStampForSampleList.toString();
				neighbourMainString = neighbourMainList.toString();
				
				signalStrengthCDMACIO = signalStrengthCDMACIOList.toString();
				signalStrengthEVDOECIO = signalStrengthEVDOECIOList.toString();
				signalStrengthSnr = signalStrengthSnrList.toString();
				signalStrengthCDMA = signalStrengthCDMAList.toString();
				signalStrengthEVDO = signalStrengthEVDOList.toString();
				
				signalStrengthLTERSRP = signalStrengthLTERSRPList.toString();
				signalStrengthLTERSRQ = signalStrengthLTERSRQList.toString();
				signalStrengthLTERSSNR = signalStrengthLTERSSNRList.toString();
				signalStrengthLTECQI = signalStrengthLTECQIList.toString();
				signalStrengthGSMStr = signalStrengthGSMList.toString();
				
				
				removeSession(context);
				// logger.info("freqBand-----------" + freqBand);
				context.getExternalContext().getSessionMap().put("signalStrengthGSMStr", 
						signalStrengthGSMStr.substring(1,signalStrengthGSMStr.length()-1));
				context.getExternalContext().getSessionMap().put(
						"mapReportType", mapReportType);
				context.getExternalContext().getSessionMap().put("lattitudes",
						latitudes.substring(1, latitudes.length() - 1));
				context.getExternalContext().getSessionMap().put("longitudes",
						longitudes.substring(1, longitudes.length() - 1));
				context.getExternalContext().getSessionMap().put("freqBand",
						freqBand.substring(1, freqBand.length() - 1));
				context.getExternalContext().getSessionMap().put("testName",
						testName.substring(1, testName.length() - 1));
				//added by Ankit on 29/03/16
				context.getExternalContext().getSessionMap().put("Imei",
						imei.substring(1, imei.length() - 1));
				
				context.getExternalContext().getSessionMap().put(
						"signalStrength",
						signalStrength
								.substring(1, signalStrength.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthLTT",
						signalStrengthLTT.substring(1, signalStrengthLTT
								.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthRating",
						signalStrengthRating.substring(1, signalStrengthRating
								.length() - 1));
				context.getExternalContext().getSessionMap().put("networkType",
						networkType.substring(1, networkType.length() - 1));
				context.getExternalContext().getSessionMap().put("networkData",
						networkData.substring(1, networkData.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"networkRoaming",
						networkRoaming
								.substring(1, networkRoaming.length() - 1));
				context.getExternalContext().getSessionMap().put("networkMnc",
						networkMnc.substring(1, networkMnc.length() - 1));
				context.getExternalContext().getSessionMap().put("networkMcc",
						networkMcc.substring(1, networkMcc.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"cellLocationLac",
						cellLocationLac.substring(1,
								cellLocationLac.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"cellLocationCid",
						cellLocationCid.substring(1,
								cellLocationCid.length() - 1));
				
				
				context.getExternalContext().getSessionMap().put(
						"signalStrengthCDMACIO",
						signalStrengthCDMACIO.substring(1, signalStrengthCDMACIO
								.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthEVDOECIO",
						signalStrengthEVDOECIO.substring(1, signalStrengthEVDOECIO
								.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthCDMA",
						signalStrengthCDMA.substring(1, signalStrengthCDMA
								.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthEVDO",
						signalStrengthEVDO.substring(1, signalStrengthEVDO
								.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthSnr",
						signalStrengthSnr.substring(1, signalStrengthSnr
								.length() - 1));
				
				
				context.getExternalContext().getSessionMap().put(
						"devicePhoneType",
						devicePhoneType.substring(1,
								devicePhoneType.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"timeStampForEachSample",
						timeStampForEachSample.substring(1,
								timeStampForEachSample.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"timeStampForSample",
						timeStampForSample.substring(1, timeStampForSample
								.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"neighbourInfo", neighbourtStr);
				context.getExternalContext().getSessionMap().put(
						"signalStrengthCDMACIO",
						signalStrengthCDMACIO.substring(1,
								signalStrengthCDMACIO.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthEVDOECIO",
						signalStrengthEVDOECIO.substring(1,
								signalStrengthEVDOECIO.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthLTERSRP",
						signalStrengthLTERSRP.substring(1,
								signalStrengthLTERSRP.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthLTERSRQ",
						signalStrengthLTERSRQ.substring(1,
								signalStrengthLTERSRQ.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthLTERSSNR",
						signalStrengthLTERSSNR.substring(1,
								signalStrengthLTERSSNR.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthLTECQI",
						signalStrengthLTECQI.substring(1, signalStrengthLTECQI
								.length() - 1));

				if (wifiInfoRssi.toString().length() > 2) {
					context.getExternalContext().getSessionMap().put(
							"wifiinforssi",
							wifiInfoRssi.toString().substring(1,
									wifiInfoRssi.toString().length() - 1));
				}
				context.getExternalContext().getSessionMap().put(
						"wifiInfoLinkSpeed",
						wifiInfoLinkSpeed.toString().substring(1,
								wifiInfoLinkSpeed.toString().length() - 1));
				context.getExternalContext().getSessionMap().put(
						"wifiInfoBssid",
						wifiInfoBssid.toString().substring(1,
								wifiInfoBssid.toString().length() - 1));
				// logger.info("wifiInfoLinkSpeed---------"
				// + wifiInfoLinkSpeed);
				if (deviceInfoCallDropList.size() > 0) {
					context.getExternalContext().getSessionMap().put(
							"deviceInfoCallDropList",
							callDrop.substring(1, callDrop.length()));
				} else {
					context.getExternalContext().getSessionMap().put(
							"deviceInfoCallDropList", callDrop);
				}
				// System.out
				// .println(context.getExternalContext().getSessionMap());
			}
		} else if (mapReportType.equals("voiceQuality")
				|| mapReportType.equals("global")) 
		{
			if (mapReportType.equalsIgnoreCase("global")) {
				mapReportType = "signalStrength";
			} else {
				mapReportType = "voiceQuality";
			}

			ReportDao dao = new ReportDaoImpl();
			UserDao userDao = new UserDaoImpl();
			List<DeviceInfoTO> deviceInfoList = new ArrayList<DeviceInfoTO>();
			boolean deviceStatus = false;
			stgVqtresults = dao.getMinandMaxTimestampforVQT(testCaseName,
					marketmapId);
			if (stgVqtresults.size() < 0) {
				context.getExternalContext().getRequestMap().remove(
						"datamessage");
				context.getExternalContext().getRequestMap().put("datamessage",
						"No Data Found for the Selected Test Name");
				removeVoiceQtySession(context);
				return "mapDataValidation";
			}
			stgDeviceresults = dao.getTestNameMapDetailsResultsForVQT(
					testCaseName, marketmapId, stgVqtresults);

			if (test_type.equals("externaltest")) {
				deviceInfoList = userDao.getAllDeviceInfoForVQT(testCaseName,
						marketmapId, test_type, stgVqtresults);
			} else {
				context.getExternalContext().getRequestMap().remove(
						"datamessage");
				context.getExternalContext().getRequestMap().put("datamessage",
						"No Data Found for the Selected Test Name");
				removeVoiceQtySession(context);
				return "mapDataValidation";
			}
			if (deviceInfoList.size() < 0) {
				context.getExternalContext().getRequestMap().remove(
						"datamessage");
				context.getExternalContext().getRequestMap().put("datamessage",
						"No Data Found for the Selected Test Name");
				removeVoiceQtySession(context);
				return "mapDataValidation";
			}
			if (stgDeviceresults.size() <= 0) {
				context.getExternalContext().getRequestMap().remove(
						"datamessage");
				context.getExternalContext().getRequestMap().put("datamessage",
						"No Data Found for the Selected Test Name");
				removeVoiceQtySession(context);
				return "mapDataValidation";
			} else {
				String imei = "";
				String latitudes = "";
				String VoiceStatusList = "";
				String longitudes = "";
				String testName = "";
				String CallTimestamp = "";
				String Rating = "";
				String PESQ = "";
				String TEMPSTATUS = "";
				Date date = null;
				String PESQAverageOffset = "";
				String NumberAllClipping = "";
				String DurationALLClipping = "";
				String signalStrength = "";
				String networkType = "";
				String SourcellId = "";
				String timestamp = "";
				String status = "";
				String network = "";
				String VQTlatlong = "";
				String networkMnc = "";
				String signalStrengthLTT = "";
				String signalStrengthRating = "";
				String networkData = "";
				String networkRoaming = "";
				String networkMcc = "";
				String cellLocationLac = "";
				String cellLocationCid = "";
				String signalStrengthSnr = "";
				String signalStrengthCDMA = "";
				String signalStrengthEVDO = "";
				String devicePhoneType = "";
				String timeStampForEachSample = "";
				String timeStampForSample = "";
				String neighbourInfo = "";
				String neighbourMainString = "";
				String signalStrengthCDMACIO = "";
				String signalStrengthEVDOECIO = "";
				String signalStrengthGSMStr="";

				String signalStrengthLTERSRP = "";
				String signalStrengthLTERSRQ = "";
				String signalStrengthLTERSSNR = "";
				String signalStrengthLTECQI = "";
				List VoiceStatus = new ArrayList();
				List latlong = new ArrayList();
				List lattitudeList = new ArrayList();
				List longitudeList = new ArrayList();
				List testNameList = new ArrayList();
				List CallTimestampList = new ArrayList();
				List RatingList = new ArrayList();
				List PESQList = new ArrayList();
				List PESQAverageOffsetList = new ArrayList();
				List NumberAllClippingList = new ArrayList();
				List tempStatusList = new ArrayList();
				List DurationALLClippingList = new ArrayList();
				List STGDeviceSignalStrengthList = new ArrayList();
				List STGDeviceNetworkTypeList = new ArrayList();
				List STGDeviceSourceCellIdList = new ArrayList();
				List STGDeviceLatitudeList = new ArrayList();
				List timeStampForSampleList = new ArrayList();
				List statusList = new ArrayList();
				List callDropStatusList = new ArrayList();
				List signalStrengthList = new ArrayList();
				List vqSignalStrengthList = new ArrayList();
				List signalStrengthListRating = new ArrayList();
				List signalStrengthCDMAList = new ArrayList();
				List signalStrengthEVDOList = new ArrayList();
				List signalStrengthLt = new ArrayList();
				List networkTypeList = new ArrayList();
				List networkDataList = new ArrayList();
				List networkRoamingList = new ArrayList();
				List networkMncList = new ArrayList();
				List networkMccList = new ArrayList();
				List cellLocationCidList = new ArrayList();
				List cellLocationLacList = new ArrayList();
				List signalStrengthSnrList = new ArrayList();
				List devicePhoneTypeList = new ArrayList();
				List timeStampForEachSampleList = new ArrayList();
				List signalStrengthCDMACIOList = new ArrayList();
				List signalStrengthEVDOECIOList = new ArrayList();
				List signalStrengthLTERSRPList = new ArrayList();
				List signalStrengthLTERSRQList = new ArrayList();
				List signalStrengthLTERSSNRList = new ArrayList();
				List signalStrengthLTECQIList = new ArrayList();
				List signalStrengthGSMList = new ArrayList();

				List neighbourMainList = new ArrayList();
				List imeiList = new ArrayList();
				String neighbourtStr = null;
				String networkName = "";
				boolean TimeStampstatus = true;
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				SimpleDateFormat newSdf = new SimpleDateFormat(
						"dd/MM/yyyy HH:mm:ss.SSS");
				int k = 0;
				int matched = 0;
				// logger.info("test time --"+deviceInfoList.size());
				// logger.info("STGDeviceresults --"+STGDeviceresults.size());
				for (int i = 0; i < deviceInfoList.size(); i++) {
					List neighbourInfoList = new ArrayList();
					List neighboursubInfoList = new ArrayList();
					DeviceInfoTO deviceInfo = deviceInfoList.get(i);
					networkName = deviceInfo.getNetworkType();
					lattitudeList.add(deviceInfo.getLattitude());
					longitudeList.add(deviceInfo.getLongitude());
					testNameList.add(deviceInfo.getTestName());
					imeiList.add(deviceInfo.getImei());
					STGDeviceSignalStrengthList.add(deviceInfo
							.getSignalStrength());
					if (true) {
						STGDevice devInfo = null;
						// TimeStampstatus=matchCallTimestamp(devInfo.getCallTimestamp(),deviceInfo.getTimeStampForEachSample());
						for (int s = 0; s < stgDeviceresults.size(); s++) {
							devInfo = stgDeviceresults.get(s);
							TimeStampstatus = matchCallTimestamp(devInfo
									.getCallTimestamp(), deviceInfo
									.getTimeStampForEachSample());
							if (TimeStampstatus) {
								break;
							}
						}
						if (deviceInfo.getTimeStampForEachSample().equals(
								"2015-03-18 18:38:26.511")) {
							// logger.info("networkName--------"
							// + networkName);
							;
						}
						// System.out
						// .println("deviceInfo.getTimeStampForEachSample()--------"
						// + deviceInfo
						// .getTimeStampForEachSample());
						// ;

						// logger.info("TimeStampstatus------"+TimeStampstatus);
						if (TimeStampstatus == true) {
							tempStatusList.add(TimeStampstatus);
							CallTimestampList.add(devInfo.getCallTimestamp());
							matched++;
							// logger.info("matched--------"+matched);
							RatingList.add(devInfo.getRating());
							PESQList.add(devInfo.getPESQ());
							PESQAverageOffsetList.add(devInfo
									.getPESQAverageOffset());
							NumberAllClippingList.add(devInfo
									.getNumberAllClipping());
							DurationALLClippingList.add(devInfo
									.getDurationALLClipping());
							if (devInfo.getVQuadCallID().contains("O_")) {
								statusList.add("outgoing");
							}
							if (devInfo.getVQuadCallID().contains("I_")) {
								statusList.add("Incoming");
							}
							latlong.add(deviceInfo.getLattitude());
							VoiceStatus.add("True");
							// k++;
						} else {
							VoiceStatus.add("False");
							tempStatusList.add(TimeStampstatus);
							CallTimestampList.add("");
							RatingList.add("");
							PESQList.add("");
							PESQAverageOffsetList.add("");
							NumberAllClippingList.add("");
							DurationALLClippingList.add("");
							statusList.add("");
							latlong.add("");
						}
					} else {
						TimeStampstatus = false;
						VoiceStatus.add("False");
						tempStatusList.add(TimeStampstatus);
						CallTimestampList.add("");
						RatingList.add("");
						PESQList.add("");
						PESQAverageOffsetList.add("");
						NumberAllClippingList.add("");
						DurationALLClippingList.add("");
						statusList.add("");
						latlong.add("");
					}
					if (networkName.matches("GSM")) {
						if (deviceInfo.getSignalStrength().equals("Empty")
								|| deviceInfo.getSignalStrength().equals("")) {
							int signalStrengthGSM = Integer.parseInt("0");
							int signalStrengthGSMValue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthList.add(String
									.valueOf(signalStrengthGSMValue + "dBm."));
							vqSignalStrengthList.add(String
									.valueOf(signalStrengthGSMValue));
							int signalStrengthLtvalue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthLt.add(String.valueOf(
									signalStrengthLtvalue).substring(
									1,
									String.valueOf(signalStrengthLtvalue)
											.length()));
							signalStrengthCDMACIOList.add("null");
							signalStrengthEVDOECIOList.add("null");
							signalStrengthSnrList.add("null");
							signalStrengthLTECQIList.add("null");
							signalStrengthLTERSSNRList.add("null");
							signalStrengthLTERSRQList.add("null");
							signalStrengthLTERSRPList.add("null");
						} else {
							int signalStrengthGSM = Integer.parseInt(deviceInfo
									.getSignalStrength());
							int signalStrengthGSMValue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthList.add(String
									.valueOf(signalStrengthGSMValue + "dBm."));
							vqSignalStrengthList.add(String
									.valueOf(signalStrengthGSMValue));
							int signalStrengthLtvalue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthLt.add(String.valueOf(
									signalStrengthLtvalue).substring(
									1,
									String.valueOf(signalStrengthLtvalue)
											.length()));
							signalStrengthCDMACIOList.add("null");
							signalStrengthEVDOECIOList.add("null");
							signalStrengthSnrList.add("null");
							signalStrengthLTECQIList.add("null");
							signalStrengthLTERSSNRList.add("null");
							signalStrengthLTERSRQList.add("null");
							signalStrengthLTERSRPList.add("null");
						}
						signalStrengthListRating.add(deviceInfo
								.getSignalStrength());
					} else if (networkName.matches("CDMA")) {
						if (deviceInfo.getSignalStrengthCDMA().equals("Empty")
								|| deviceInfo.getSignalStrengthCDMA()
										.equals("")) {
							signalStrengthList.add("0");
							vqSignalStrengthList.add(String.valueOf("0"));
							signalStrengthLt.add("0");
							signalStrengthCDMACIOList.add(deviceInfo
									.getSignalStrengthCDMACIO());
							signalStrengthLTECQIList.add("null");
							signalStrengthLTERSSNRList.add("null");
							signalStrengthLTERSRQList.add("null");
							signalStrengthLTERSRPList.add("null");
						} else {
							signalStrengthList.add(deviceInfo
									.getSignalStrengthCDMA());
							vqSignalStrengthList.add(deviceInfo
									.getSignalStrengthCDMA());
							signalStrengthLt.add(deviceInfo
									.getSignalStrengthCDMA());
							signalStrengthCDMACIOList.add(deviceInfo
									.getSignalStrengthCDMACIO());
							signalStrengthEVDOECIOList.add("null");
							signalStrengthSnrList.add("null");
							signalStrengthLTECQIList.add("null");
							signalStrengthLTERSSNRList.add("null");
							signalStrengthLTERSRQList.add("null");
							signalStrengthLTERSRPList.add("null");
						}
						
						if(deviceInfo.getSignalStrengthGSM().equalsIgnoreCase("false"))
						{
							signalStrengthGSMList.add(deviceInfo.getSignalStrengthGSM());
							signalStrengthCDMACIOList.add(deviceInfo.getSignalStrengthCDMACIO());
							signalStrengthSnrList.add(deviceInfo.getSignalStrengthSnr());
							signalStrengthEVDOECIOList.add(deviceInfo.getSignalStrengthEVDOECIO());
							signalStrengthCDMAList.add(deviceInfo.getSignalStrengthCDMA());
							signalStrengthEVDOList.add(deviceInfo.getSignalStrengthEVDO());
						}
						
						
						signalStrengthListRating.add(deviceInfo
								.getSignalStrengthCDMA());
					} else if (networkName.matches("EVDO") || networkName.matches("eHRPD") || networkName.matches("EHRPD (3G)")
							|| networkName.matches("EVDO-A") || networkName.matches("EVDO-B")) {
						if (deviceInfo.getSignalStrengthEVDO().equals("Empty")
								|| deviceInfo.getSignalStrengthEVDO()
										.equals("")) {
							signalStrengthList.add("0");
							signalStrengthLt.add("0");
							signalStrengthEVDOECIOList.add(deviceInfo
									.getSignalStrengthEVDOECIO());
							signalStrengthSnrList.add(deviceInfo
									.getSignalStrengthSnr());
							signalStrengthCDMACIOList.add("null");
							signalStrengthLTECQIList.add("null");
							signalStrengthLTERSSNRList.add("null");
							signalStrengthLTERSRQList.add("null");
							signalStrengthLTERSRPList.add("null");
						} else {
							signalStrengthList.add(deviceInfo
									.getSignalStrengthEVDO());
							signalStrengthLt.add(deviceInfo
									.getSignalStrengthEVDO());
							signalStrengthEVDOECIOList.add(deviceInfo
									.getSignalStrengthEVDOECIO());
							signalStrengthSnrList.add(deviceInfo
									.getSignalStrengthSnr());
							signalStrengthCDMACIOList.add("null");
							signalStrengthLTECQIList.add("null");
							signalStrengthLTERSSNRList.add("null");
							signalStrengthLTERSRQList.add("null");
							signalStrengthLTERSRPList.add("null");
						}
						signalStrengthListRating.add(deviceInfo
								.getSignalStrengthEVDO());
					} else if (networkName.equalsIgnoreCase("LTE (4G)")) {
						if (deviceInfo.getSignalStrengthLTE().equals("Empty")
								|| deviceInfo.getSignalStrengthLTE().equals("")) {
							int signalStrengthLTE = Integer.parseInt("0");
							int signalStrengthLTEValue = signalStrengthLTE;
							signalStrengthList.add(String
									.valueOf(signalStrengthLTEValue + "dBm."));
							vqSignalStrengthList.add(signalStrengthLTEValue);
							int signalStrengthLTValue = signalStrengthLTE;
							signalStrengthLt.add(String.valueOf(
									signalStrengthLTValue).substring(
									1,
									String.valueOf(signalStrengthLTValue)
											.length()));
							// signalStrengthLt.add(String.valueOf(deviceInfo.getSignalStrengthLTERSRP()).substring(1,
							// String.valueOf(deviceInfo.getSignalStrengthLTERSRP()).length()));
							signalStrengthLTERSRPList.add(deviceInfo
									.getSignalStrengthLTERSRP());
							signalStrengthLTERSRQList.add(deviceInfo
									.getSignalStrengthLTERSRQ());
							signalStrengthLTERSSNRList.add(deviceInfo
									.getSignalStrengthLTERSSNR());
							signalStrengthLTECQIList.add(deviceInfo
									.getSignalStrengthLTECQI());
							signalStrengthCDMACIOList.add("null");
							signalStrengthEVDOECIOList.add("null");
							signalStrengthSnrList.add("null");
						} else {
							int signalStrengthLTE = Integer.parseInt(deviceInfo
									.getSignalStrengthLTE());
							int signalStrengthLTEValue = -140
									+ signalStrengthLTE;
							signalStrengthList.add(String
									.valueOf(signalStrengthLTEValue + "dBm."));
							vqSignalStrengthList.add(String
									.valueOf(signalStrengthLTEValue));
							int signalStrengthLTValue = signalStrengthLTE;
							signalStrengthLt.add(String.valueOf(
									signalStrengthLTValue).substring(
									1,
									String.valueOf(signalStrengthLTValue)
											.length()));
							// signalStrengthLt.add(String.valueOf(deviceInfo.getSignalStrengthLTERSRP()).substring(1,
							// String.valueOf(deviceInfo.getSignalStrengthLTERSRP()).length()));
							signalStrengthLTERSRPList.add(deviceInfo
									.getSignalStrengthLTERSRP());
							signalStrengthLTERSRQList.add(deviceInfo
									.getSignalStrengthLTERSRQ());
							signalStrengthLTERSSNRList.add(deviceInfo
									.getSignalStrengthLTERSSNR());
							signalStrengthLTECQIList.add(deviceInfo
									.getSignalStrengthLTECQI());
							signalStrengthCDMACIOList.add("null");
							signalStrengthEVDOECIOList.add("null");
							signalStrengthSnrList.add("null");
						}
						signalStrengthListRating.add(deviceInfo
								.getSignalStrengthLTE());
					} else {
						if (deviceInfo.getSignalStrength().equals("Empty")
								|| deviceInfo.getSignalStrength().equals("")) {
							int signalStrengthGSM = Integer.parseInt("0");
							int signalStrengthGSMValue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthList.add(String
									.valueOf(signalStrengthGSMValue + "dBm."));
							vqSignalStrengthList.add(String
									.valueOf(signalStrengthGSMValue));
							int signalStrengthLtvalue = Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthLt.add(String.valueOf(
									signalStrengthLtvalue).substring(
									1,
									String.valueOf(signalStrengthLtvalue)
											.length()));
							signalStrengthCDMACIOList.add("null");
							signalStrengthEVDOECIOList.add("null");
							signalStrengthSnrList.add("null");
							signalStrengthLTECQIList.add("null");
							signalStrengthLTERSSNRList.add("null");
							signalStrengthLTERSRQList.add("null");
							signalStrengthLTERSRPList.add("null");
						} else {
							int signalStrengthGSM = Integer.parseInt(deviceInfo
									.getSignalStrength());
							int signalStrengthGSMValue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthList.add(String
									.valueOf(signalStrengthGSMValue + "dBm."));
							vqSignalStrengthList.add(String
									.valueOf(signalStrengthGSMValue));
							int signalStrengthLtvalue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthLt.add(String.valueOf(
									signalStrengthLtvalue).substring(
									1,
									String.valueOf(signalStrengthLtvalue)
											.length()));
							signalStrengthCDMACIOList.add("null");
							signalStrengthEVDOECIOList.add("null");
							signalStrengthSnrList.add("null");
							signalStrengthLTECQIList.add("null");
							signalStrengthLTERSSNRList.add("null");
							signalStrengthLTERSRQList.add("null");
							signalStrengthLTERSRPList.add("null");
						}
						signalStrengthListRating.add(deviceInfo
								.getSignalStrength());
					}
					network = deviceInfo.getNetworkType().trim();
					// logger.info("before adding--"
					// + deviceInfo.getNetworkType());
					networkTypeList.add(network);
					networkDataList.add(deviceInfo.getNetworkDataState());
					networkRoamingList.add(deviceInfo.getNetworkRoaming());
					networkMccList.add(deviceInfo.getNetworkMCC());
					networkMncList.add(deviceInfo.getNetworkMNC());
					cellLocationCidList.add(deviceInfo.getCellLocationCID());
					cellLocationLacList.add(deviceInfo.getCellLocationLAC());
					devicePhoneTypeList.add(deviceInfo.getDevicePhoneType());
					// logger.info("333333333333fileName---"+fileName);
					if (fileName != null) {
						if (ENVIRONMENT.equals("PROD")) {
							ipAddress = "50.112.27.46";
							url = "http://" + ipAddress + ":" + ServerPort
									+ "/FileService/" + fileName;
						} else {
							url = "http://" + ipAddress + ":" + ServerPort
									+ "/FileService/" + fileName;
						}
						timeStampForEachSampleList.add("<b>"
								+ deviceInfo.getTimeStampForEachSample()
								+ "</b>" + " <a  onclick='renderQxdmPopup("
								+ deviceInfo.getTimeStampForEachSample()
								+ ")' target='blank'>Qxdm Info1</a>");
						timeStampForSampleList.add(deviceInfo
								.getTimeStampForEachSample());
					} else {
						timeStampForEachSampleList.add("<b>Test Times:</b>"
								+ deviceInfo.getTimeStampForEachSample());
						timeStampForSampleList.add(deviceInfo
								.getTimeStampForEachSample());
					}

					neighbourInfoList.add(deviceInfo.getNeighbourInfo());
					for (int j = 0; j < neighbourInfoList.size(); j++) {
						if (null != neighbourInfoList.get(j)
								&& !neighbourInfoList.get(j).toString().equals(
										"Empty")) {
							String neighbourInfoArray[] = neighbourInfoList
									.get(j).toString().split("\\$");
							for (int a = 0; a < neighbourInfoArray.length; a++) {
								String neighbourSubInfoArray[] = neighbourInfoArray[a]
										.split("\\^");
								if (a == 0) {
									if (neighbourtStr == null) {
										if (neighbourSubInfoArray[0]
												.matches("-1")
												&& neighbourSubInfoArray[1]
														.matches("-1")) {
											neighbourtStr = "Cell: Empty" + ","
													+ "Lac:Empty" + ","
													+ "PSC:"
													+ neighbourSubInfoArray[3]
													+ "," + "RSSI:"
													+ neighbourSubInfoArray[4];
										} else if (neighbourSubInfoArray[0]
												.matches("-1")) {
											neighbourtStr = "Cell: Empty" + ","
													+ "Lac:"
													+ neighbourSubInfoArray[1]
													+ "," + "PSC:"
													+ neighbourSubInfoArray[3]
													+ "," + "RSSI:"
													+ neighbourSubInfoArray[4];
										} else if (neighbourSubInfoArray[1]
												.matches("-1")) {
											neighbourtStr = "Cell:"
													+ neighbourSubInfoArray[0]
													+ "," + "Lac:Empty" + ","
													+ "PSC:"
													+ neighbourSubInfoArray[3]
													+ "," + "RSSI:"
													+ neighbourSubInfoArray[4];
										} else {
											neighbourtStr = "Cell:"
													+ neighbourSubInfoArray[0]
													+ "," + "Lac:"
													+ neighbourSubInfoArray[1]
													+ "," + "PSC:"
													+ neighbourSubInfoArray[3]
													+ "," + "RSSI:"
													+ neighbourSubInfoArray[4];
										}
									} else {
										if (neighbourSubInfoArray[0]
												.matches("-1")
												&& neighbourSubInfoArray[1]
														.matches("-1")) {
											neighbourtStr = neighbourtStr
													+ "|||Cell:Empty" + ","
													+ "Lac:Empty" + ","
													+ "PSC:"
													+ neighbourSubInfoArray[3]
													+ "," + "RSSI:"
													+ neighbourSubInfoArray[4];
										} else if (neighbourSubInfoArray[0]
												.matches("-1")) {
											neighbourtStr = neighbourtStr
													+ "|||Cell:Empty" + ","
													+ "Lac:"
													+ neighbourSubInfoArray[1]
													+ "," + "PSC:"
													+ neighbourSubInfoArray[3]
													+ "," + "RSSI:"
													+ neighbourSubInfoArray[4];
										} else if (neighbourSubInfoArray[1]
												.matches("-1")) {
											neighbourtStr = neighbourtStr
													+ "|||Cell:"
													+ neighbourSubInfoArray[0]
													+ "," + "Lac:Empty" + ","
													+ "PSC:"
													+ neighbourSubInfoArray[3]
													+ "," + "RSSI:"
													+ neighbourSubInfoArray[4];
										} else {
											neighbourtStr = neighbourtStr
													+ "|||Cell:"
													+ neighbourSubInfoArray[0]
													+ "," + "Lac:"
													+ neighbourSubInfoArray[1]
													+ "," + "PSC:"
													+ neighbourSubInfoArray[3]
													+ "," + "RSSI:"
													+ neighbourSubInfoArray[4];
										}

									}
								} else if (a == (neighbourInfoArray.length - 1)) {
									if (neighbourSubInfoArray[0].matches("-1")
											&& neighbourSubInfoArray[1]
													.matches("-1")) {
										neighbourtStr = neighbourtStr
												+ "|||Cell:Empty" + ","
												+ "Lac:Empty" + "," + "PSC:"
												+ neighbourSubInfoArray[3]
												+ "," + "RSSI:"
												+ neighbourSubInfoArray[4]
												+ "||";
									} else if (neighbourSubInfoArray[0]
											.matches("-1")) {
										neighbourtStr = neighbourtStr
												+ "|||Cell:Empty" + ","
												+ "Lac:"
												+ neighbourSubInfoArray[1]
												+ "," + "PSC:"
												+ neighbourSubInfoArray[3]
												+ "," + "RSSI:"
												+ neighbourSubInfoArray[4]
												+ "||";
									} else if (neighbourSubInfoArray[1]
											.matches("-1")) {
										neighbourtStr = neighbourtStr
												+ "|||Cell:"
												+ neighbourSubInfoArray[0]
												+ "," + "Lac:Empty" + ","
												+ "PSC:"
												+ neighbourSubInfoArray[3]
												+ "," + "RSSI:"
												+ neighbourSubInfoArray[4]
												+ "||";
									} else {
										neighbourtStr = neighbourtStr
												+ "|||Cell:"
												+ neighbourSubInfoArray[0]
												+ "," + "Lac:"
												+ neighbourSubInfoArray[1]
												+ "," + "PSC:"
												+ neighbourSubInfoArray[3]
												+ "," + "RSSI:"
												+ neighbourSubInfoArray[4]
												+ "||";
									}

								} else {
									if (neighbourSubInfoArray[0].matches("-1")
											&& neighbourSubInfoArray[1]
													.matches("-1")) {
										neighbourtStr = neighbourtStr
												+ "|||Cell:Empty" + ","
												+ "Lac:Empty" + "," + "PSC:"
												+ neighbourSubInfoArray[3]
												+ "," + "RSSI:"
												+ neighbourSubInfoArray[4];
									} else if (neighbourSubInfoArray[0]
											.matches("-1")) {
										neighbourtStr = neighbourtStr
												+ "|||Cell:Empty" + ","
												+ "Lac:"
												+ neighbourSubInfoArray[1]
												+ "," + "PSC:"
												+ neighbourSubInfoArray[3]
												+ "," + "RSSI:"
												+ neighbourSubInfoArray[4];
									} else if (neighbourSubInfoArray[1]
											.matches("-1")) {
										neighbourtStr = neighbourtStr
												+ "|||Cell:"
												+ neighbourSubInfoArray[0]
												+ "," + "Lac:Empty" + ","
												+ "PSC:"
												+ neighbourSubInfoArray[3]
												+ "," + "RSSI:"
												+ neighbourSubInfoArray[4];
									} else {
										neighbourtStr = neighbourtStr
												+ "|||Cell:"
												+ neighbourSubInfoArray[0]
												+ "," + "Lac:"
												+ neighbourSubInfoArray[1]
												+ "," + "PSC:"
												+ neighbourSubInfoArray[3]
												+ "," + "RSSI:"
												+ neighbourSubInfoArray[4];
									}
								}
							}
						} else {
							neighbourtStr = neighbourtStr
									+ "|||Cell:Empty,Lac:Empty,PSC:Empty,RSSI:Empty||";
						}
					}
					neighbourInfoList.clear();
					if (neighboursubInfoList.size() > 0) {
						boolean single = true;
						for (int x = 0; x < neighboursubInfoList.size(); x++) {
							if (neighbourtStr == null) {
								neighbourtStr = neighboursubInfoList.get(x)
										.toString();
							} else {
								neighbourtStr = neighbourtStr
										+ ","
										+ neighboursubInfoList.get(x)
												.toString();
							}
						}
					}

				}
				VoiceStatusList = VoiceStatus.toString();
				latitudes = lattitudeList.toString();
				longitudes = longitudeList.toString();
				testName = testNameList.toString();
				imei = imeiList.toString();
				Rating = RatingList.toString();
				PESQ = PESQList.toString();
				TEMPSTATUS = tempStatusList.toString().trim();
				PESQAverageOffset = PESQAverageOffsetList.toString();
				NumberAllClipping = NumberAllClippingList.toString();
				DurationALLClipping = DurationALLClippingList.toString();
				TreeSet<String> CallTimestampListValue = new TreeSet<String>(
						CallTimestampList);
				CallTimestamp = CallTimestampListValue.toString();
				status = statusList.toString();
				timestamp = timeStampForSampleList.toString();
				VQTlatlong = latlong.toString();
				signalStrengthLTT = signalStrengthLt.toString();
				signalStrengthRating = signalStrengthListRating.toString();
				networkData = networkDataList.toString();
				networkRoaming = networkRoamingList.toString();
				networkMnc = networkMncList.toString();
				networkMcc = networkMccList.toString();
				cellLocationLac = cellLocationLacList.toString();
				cellLocationCid = cellLocationCidList.toString();
				signalStrengthSnr = signalStrengthSnrList.toString();
				devicePhoneType = devicePhoneTypeList.toString();
				timeStampForEachSample = timeStampForEachSampleList.toString();
				timeStampForSample = timeStampForSampleList.toString();
				neighbourMainString = neighbourMainList.toString();
				
				signalStrengthGSMStr = signalStrengthGSMList.toString();
				signalStrengthCDMACIO = signalStrengthCDMACIOList.toString();
				signalStrengthEVDOECIO = signalStrengthEVDOECIOList.toString();
				signalStrengthSnr = signalStrengthSnrList.toString();
				signalStrengthCDMA = signalStrengthCDMAList.toString();
				signalStrengthEVDO = signalStrengthEVDOList.toString();
				
				signalStrengthLTERSRP = signalStrengthLTERSRPList.toString();
				signalStrengthLTERSRQ = signalStrengthLTERSRQList.toString();
				signalStrengthLTERSSNR = signalStrengthLTERSSNRList.toString();
				signalStrengthLTECQI = signalStrengthLTECQIList.toString();

				if (STGDeviceSignalStrengthList.size() > 0) {
					for (int i = 0; i < STGDeviceSignalStrengthList.size(); i++) {
						int signalStrengthGSM = 0;
						if (STGDeviceSignalStrengthList.get(i).toString()
								.equals("Empty")) {
							signalStrengthGSM = 0;
						} else {
							signalStrengthGSM = Integer
									.parseInt(STGDeviceSignalStrengthList
											.get(i).toString());
						}
						int signalStrengthGSMValue = -Integer
								.parseInt(SIGNALSTRENGTH_GSM)
								+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
								* signalStrengthGSM;
						signalStrength = signalStrength + ","
								+ signalStrengthGSMValue + "dBm.";
					}
				}
				// System.out
				// .println("networkTypeList---------" + networkTypeList);
				if (networkTypeList.size() > 0) {
					for (int i = 0; i < networkTypeList.size(); i++) {
						networkType = networkType + ","
								+ networkTypeList.get(i).toString();
					}
				}
				// logger.info("networkType--------------" +
				// networkType);
				if (cellLocationCidList.size() > 0) {
					for (int i = 0; i < cellLocationCidList.size(); i++) {
						SourcellId = SourcellId + ","
								+ cellLocationCidList.get(i).toString();
					}
				}
				removeVoiceQtySession(context);
				signalStrength = signalStrengthList.toString();
				String vqSignalStrength = vqSignalStrengthList.toString();
				context.getExternalContext().getSessionMap().put(
						"VoiceStatusList",
						VoiceStatusList.substring(1,
								VoiceStatusList.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthLTT",
						signalStrengthLTT.substring(1, signalStrengthLTT
								.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthRating",
						signalStrengthRating.substring(1, signalStrengthRating
								.length() - 1));
				context.getExternalContext().getSessionMap().put("networkMnc",
						networkMnc.substring(1, networkMnc.length() - 1));
				context.getExternalContext().getSessionMap().put("networkMcc",
						networkMcc.substring(1, networkMcc.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"cellLocationLac",
						cellLocationLac.substring(1,
								cellLocationLac.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"cellLocationCid",
						cellLocationCid.substring(1,
								cellLocationCid.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthSnr",
						signalStrengthSnr.substring(1, signalStrengthSnr
								.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"neighbourInfo", neighbourtStr);
				context.getExternalContext().getSessionMap().put("VQTlatlong",
						VQTlatlong.substring(1, VQTlatlong.length() - 1));
				context.getExternalContext().getSessionMap().put("statusList",
						status.substring(1, status.length() - 1));
				context.getExternalContext().getSessionMap().put("lattitudes",
						latitudes.substring(1, latitudes.length() - 1));
				context.getExternalContext().getSessionMap().put("longitudes",
						longitudes.substring(1, longitudes.length() - 1));
				context.getExternalContext().getSessionMap().put("testName",
						testName.substring(1, testName.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"CallTimestamp",
						CallTimestamp.substring(1, CallTimestamp.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"mapReportType", mapReportType);
				context.getExternalContext().getSessionMap().put("Rating",
						Rating.substring(1, Rating.length() - 1));
				/* New Code added by Shesha */
				context.getExternalContext().getSessionMap()
						.put("TEMPSTATUS",
								TEMPSTATUS.substring(1, TEMPSTATUS.trim()
										.length() - 1));
				context.getExternalContext().getSessionMap().put("PESQ",
						PESQ.substring(1, PESQ.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"PESQAverageOffset",
						PESQAverageOffset.substring(1, PESQAverageOffset
								.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"NumberAllClipping",
						NumberAllClipping.substring(1, NumberAllClipping
								.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"DurationALLClipping",
						DurationALLClipping.substring(1, DurationALLClipping
								.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"timeStampForEachSample",
						timestamp.substring(1, timestamp.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthLTERSRP",
						signalStrengthLTERSRP.substring(1,
								signalStrengthLTERSRP.length() - 1));

				context.getExternalContext().getSessionMap().put(
						"signalStrengthLTERSRQ",
						signalStrengthLTERSRQ.substring(1,
								signalStrengthLTERSRQ.length() - 1));

				context.getExternalContext().getSessionMap().put(
						"signalStrengthLTERSSNR",
						signalStrengthLTERSSNR.substring(1,
								signalStrengthLTERSSNR.length() - 1));

				context.getExternalContext().getSessionMap().put(
						"signalStrengthLTECQI",
						signalStrengthLTECQI.substring(1, signalStrengthLTECQI
								.length() - 1));

				
				context.getExternalContext().getSessionMap().put(
						"signalStrengthCDMACIO",
						signalStrengthCDMACIO.substring(1, signalStrengthCDMACIO
								.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthEVDOECIO",
						signalStrengthEVDOECIO.substring(1, signalStrengthEVDOECIO
								.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthCDMA",
						signalStrengthCDMA.substring(1, signalStrengthCDMA
								.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthEVDO",
						signalStrengthEVDO.substring(1, signalStrengthEVDO
								.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthSnr",
						signalStrengthSnr.substring(1, signalStrengthSnr
								.length() - 1));
				context.getExternalContext().getSessionMap().put("signalStrengthGSMStr", 
						signalStrengthGSMStr.substring(1,signalStrengthGSMStr.length()-1));
				context.getExternalContext().getSessionMap().put("Imei", 
						imei.substring(1,imei.length()-1));
				if (STGDeviceSignalStrengthList.size() > 0) {
					context.getExternalContext().getSessionMap().put(
							"STGDeviceSignalStrengthList",
							signalStrength
									.substring(1, signalStrength.length()));
					context.getExternalContext().getSessionMap().put(
							"vqSignalStrength",
							vqSignalStrength.substring(1, vqSignalStrength
									.length()));
				} else {
					context.getExternalContext().getSessionMap().put(
							"STGDeviceSignalStrengthList", signalStrength);
					context.getExternalContext().getSessionMap().put(
							"vqSignalStrength", vqSignalStrength);
				}
				if (STGDeviceNetworkTypeList.size() > 0) {
					context.getExternalContext().getSessionMap().put(
							"STGDeviceNetworkTypeList",
							networkTypeList.toString().substring(1,
									networkTypeList.toString().length()));
				} else {
					context.getExternalContext().getSessionMap().put(
							"STGDeviceNetworkTypeList",
							networkTypeList.toString().substring(1,
									networkTypeList.toString().length()));
				}
				if (STGDeviceSourceCellIdList.size() > 0) {
					context.getExternalContext().getSessionMap().put(
							"STGDeviceSourceCellIdList",
							SourcellId.substring(1, SourcellId.length()));
				} else {
					context.getExternalContext().getSessionMap().put(
							"STGDeviceSourceCellIdList", SourcellId);
				}
			}
			System.out
					.println("context.getExternalContext().getSessionMap()----"
							+ context.getExternalContext().getSessionMap());
		}
		else if (mapReportType.equals("Frequency_Band")) 
		{

			// logger.info();
			// logger.info(context.getExternalContext().getSessionMap());
			String userrole = context.getExternalContext().getSessionMap().get(
					"loggedInUserRoleID").toString();
			context.getExternalContext().getSessionMap().put(
					"loggedInUserRoleID", userrole);
			UserDao userDao = new UserDaoImpl();
			List<DeviceInfoTO> deviceInfoCallList = userDao.getAllDeviceInfo(
					testCaseName, marketmapId, test_type, frequencyPlan);
			List<DeviceInfoTO> deviceInfoList = userDao.getAllDeviceInfo(
					testCaseName, marketmapId, test_type, frequencyPlan);
			// List<String>deviceInfoCallDropList =
			// connectivityDao.getAllDeviceInfoCallDropList(test_name,market_id,test_type);
			List<String> deviceInfoCallDropList = userDao
					.getAllDeviceInfoCallDropList(testCaseName, marketmapId,
							test_type);//, 1

			if (deviceInfoCallDropList.size() == 0) {
				  deviceInfoCallDropList = userDao
				  .getAllDeviceInfoCallDropList(testCaseName, marketmapId,
				  test_type);//,2
			}
			System.out.println();
			if (deviceInfoList.size() == 0) {
				List<DeviceInfoTO> deviceMultipleInfoList = userDao
						.getAllMultiplDeviceInfo(testCaseName, marketmapId,
								test_type);
				if (deviceMultipleInfoList.size() > 0) {
					deviceInfoList = deviceMultipleInfoList;
				} else {
					deviceInfoList = deviceMultipleInfoList;
				}
			}

			if (deviceInfoCallDropList.size() == 0
					&& deviceInfoCallList.size() == 0) {
				List<String> deviceMultipleCallDropList = userDao
						.getAllMultipleDeviceInfoCallDropList(testCaseName,
								marketmapId, test_type);
				if (deviceMultipleCallDropList.size() > 0) {
					deviceInfoCallDropList = deviceMultipleCallDropList;
				} else {
					deviceInfoCallDropList = deviceMultipleCallDropList;
				}
			}
			try {
				InetAddress ip = InetAddress.getLocalHost();
				ipAddress = ip.getHostName();

				File f1 = new File(QXDM_FILE_PATH);
				File file1[] = f1.listFiles();
				String filename = null;// f.getName();
				for (File f : file1) {
					String tempFileName = f.getName();
					if ((tempFileName.startsWith("QXDM_" + testCaseName))
							&& (tempFileName.endsWith(".txt"))) {
						isQxdmPresent = "true";
						fileName = f.getName();
					}
				}
			} catch (UnknownHostException e) {
				logger.error(e.getMessage());
			}
			if (deviceInfoList.size() <= 0) {
				context.getExternalContext().getRequestMap().remove(
						"datamessage");
				context.getExternalContext().getRequestMap().put("datamessage",
						"No Data Found for the Selected Test Name");
				removeSession(context);
				return "mapDataValidation";
			} else {
				String imei = "";
				String latitudes = "";
				String longitudes = "";
				String testName = "";
				String networkMnc = "";
				String signalStrength = "";
				String signalStrengthLTT = "";
				String signalStrengthRating = "";
				String networkType = "";
				String networkData = "";
				String networkRoaming = "";
				String networkMcc = "";
				String cellLocationLac = "";
				String cellLocationCid = "";
				String signalStrengthSnr = "";
				String signalStrengthCDMA = "";
				String signalStrengthEVDO = "";
				String devicePhoneType = "";
				String timeStampForEachSample = "";
				String timeStampForSample = "";
				String neighbourInfo = "";
				String neighbourMainString = "";
				String signalStrengthCDMACIO = "";
				String signalStrengthEVDOECIO = "";

				String signalStrengthLTERSRP = "";
				String signalStrengthLTERSRQ = "";
				String signalStrengthLTERSSNR = "";
				String signalStrengthLTECQI = "";
				String callDrop = "";
				String signalStrengthGSMStr="";
				String network = "";

				// String details = "";
				List<String> freqList = new ArrayList<String>();
				List lattitudeList = new ArrayList();
				List longitudeList = new ArrayList();
				List testNameList = new ArrayList();
				List callDropStatusList = new ArrayList();
				List signalStrengthList = new ArrayList();
				List signalStrengthListRating = new ArrayList();
				List signalStrengthLt = new ArrayList();
				List networkTypeList = new ArrayList();
				List networkDataList = new ArrayList();
				List networkRoamingList = new ArrayList();
				List networkMncList = new ArrayList();
				List networkMccList = new ArrayList();
				List cellLocationCidList = new ArrayList();
				List cellLocationLacList = new ArrayList();
				List signalStrengthSnrList = new ArrayList();
				List devicePhoneTypeList = new ArrayList();
				List timeStampForEachSampleList = new ArrayList();
				List timeStampForSampleList = new ArrayList();
				List signalStrengthCDMACIOList = new ArrayList();
				List signalStrengthGSMList = new ArrayList();
				List signalStrengthEVDOECIOList = new ArrayList();
				List signalStrengthCDMAList = new ArrayList();
				List signalStrengthEVDOList = new ArrayList();
				List signalStrengthLTERSRPList = new ArrayList();
				List signalStrengthLTERSRQList = new ArrayList();
				List signalStrengthLTERSSNRList = new ArrayList();
				List signalStrengthLTECQIList = new ArrayList();
				List wifiInfoBssid = new ArrayList();
				List wifiInfoRssi = new ArrayList();
				List wifiInfoLinkSpeed = new ArrayList();
				List imeiList = new ArrayList();
				List neighbourMainList = new ArrayList();
				String neighbourtStr = null;
				String networkName = "";

				if (deviceInfoCallDropList.size() > 0) {
					for (int i = 0; i < deviceInfoCallDropList.size(); i++) {
						callDrop = callDrop + ","
								+ deviceInfoCallDropList.get(i);
					}
				}
				for (int i = 0; i < deviceInfoList.size(); i++) {
					List neighbourInfoList = new ArrayList();
					List neighboursubInfoList = new ArrayList();
					DeviceInfoTO deviceInfo = deviceInfoList.get(i);
					networkName = deviceInfo.getNetworkType();
					lattitudeList.add(deviceInfo.getLattitude());
					longitudeList.add(deviceInfo.getLongitude());
					testNameList.add(deviceInfo.getTestName());
					imeiList.add(deviceInfo.getImei());
					if (deviceInfo.getFreqBand().trim().length() < 1) {
						freqList.add("");
					} else {
						freqList.add(deviceInfo.getFreqBand().trim());
					}

					// logger.info("deviceInfo.getFreqBand().trim()----"
					// + freqList.toString());
					wifiInfoBssid.add("");
					wifiInfoLinkSpeed.add("");
					wifiInfoRssi.add("");

					if (networkName.matches("GSM")) {
						if (deviceInfo.getSignalStrength().equals("Empty")
								|| deviceInfo.getSignalStrength().equals("")) {
							int signalStrengthGSM = Integer.parseInt("0");
							int signalStrengthGSMValue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthList.add(String
									.valueOf(signalStrengthGSMValue + "dBm."));
							int signalStrengthLtvalue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthLt.add(String.valueOf(
									signalStrengthLtvalue).substring(
									1,
									String.valueOf(signalStrengthLtvalue)
											.length()));
							signalStrengthCDMACIOList.add("null");
							signalStrengthEVDOECIOList.add("null");
							signalStrengthSnrList.add("null");
							signalStrengthLTECQIList.add("null");
							signalStrengthLTERSSNRList.add("null");
							signalStrengthLTERSRQList.add("null");
							signalStrengthLTERSRPList.add("null");
						} else {
							int signalStrengthGSM = Integer.parseInt(deviceInfo
									.getSignalStrength());
							int signalStrengthGSMValue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthList.add(String
									.valueOf(signalStrengthGSMValue + "dBm."));
							int signalStrengthLtvalue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthLt.add(String.valueOf(
									signalStrengthLtvalue).substring(
									1,
									String.valueOf(signalStrengthLtvalue)
											.length()));
							signalStrengthCDMACIOList.add("null");
							signalStrengthEVDOECIOList.add("null");
							signalStrengthSnrList.add("null");
							signalStrengthLTECQIList.add("null");
							signalStrengthLTERSSNRList.add("null");
							signalStrengthLTERSRQList.add("null");
							signalStrengthLTERSRPList.add("null");
						}
						signalStrengthListRating.add(deviceInfo
								.getSignalStrength());
					} else if (networkName.toUpperCase().trim().contains("CDMA")) {
						if (deviceInfo.getSignalStrengthCDMA().equals("Empty")
								|| deviceInfo.getSignalStrengthCDMA()
										.equals("")) {
							signalStrengthList.add("0");
							signalStrengthLt.add("0");
							
							signalStrengthLTECQIList.add("null");
							signalStrengthLTERSSNRList.add("null");
							signalStrengthLTERSRQList.add("null");
							signalStrengthLTERSRPList.add("null");
						} else {
							signalStrengthList.add(deviceInfo
									.getSignalStrengthCDMA());
							signalStrengthLt.add(deviceInfo
									.getSignalStrengthCDMA());
							signalStrengthCDMACIOList.add(deviceInfo
									.getSignalStrengthCDMACIO());
							signalStrengthEVDOECIOList.add("null");
							signalStrengthSnrList.add("null");
							signalStrengthLTECQIList.add("null");
							signalStrengthLTERSSNRList.add("null");
							signalStrengthLTERSRQList.add("null");
							signalStrengthLTERSRPList.add("null");
						}
						if(deviceInfo.getSignalStrengthGSM().equalsIgnoreCase("false"))
						{
							signalStrengthGSMList.add(deviceInfo.getSignalStrengthGSM());
							signalStrengthCDMACIOList.add(deviceInfo.getSignalStrengthCDMACIO());
							signalStrengthSnrList.add(deviceInfo.getSignalStrengthSnr());
							signalStrengthEVDOECIOList.add(deviceInfo.getSignalStrengthEVDOECIO());
							signalStrengthCDMAList.add(deviceInfo.getSignalStrengthCDMA());
							signalStrengthEVDOList.add(deviceInfo.getSignalStrengthEVDO());
						}	
						signalStrengthListRating.add(deviceInfo
								.getSignalStrengthCDMA());
					} else if (networkName.toUpperCase().trim().contains("EVDO") || networkName.toUpperCase().trim().contains("eHRPD")
							|| networkName.toUpperCase().trim().contains("EHRPD (3G)") || networkName.toUpperCase().trim().contains("EVDO-A")
							|| networkName.toUpperCase().trim().contains("EVDO-B")) {
						if (deviceInfo.getSignalStrengthEVDO().equals("Empty")
								|| deviceInfo.getSignalStrengthEVDO()
										.equals("")) {
							signalStrengthList.add("0");
							signalStrengthLt.add("0");
							signalStrengthEVDOECIOList.add(deviceInfo
									.getSignalStrengthEVDOECIO());
							signalStrengthSnrList.add(deviceInfo
									.getSignalStrengthSnr());
							signalStrengthCDMACIOList.add("null");
							signalStrengthLTECQIList.add("null");
							signalStrengthLTERSSNRList.add("null");
							signalStrengthLTERSRQList.add("null");
							signalStrengthLTERSRPList.add("null");
						} else {
							signalStrengthList.add(deviceInfo
									.getSignalStrengthEVDO());
							signalStrengthLt.add(deviceInfo
									.getSignalStrengthEVDO());
							signalStrengthEVDOECIOList.add(deviceInfo
									.getSignalStrengthEVDOECIO());
							signalStrengthSnrList.add(deviceInfo
									.getSignalStrengthSnr());
							signalStrengthCDMACIOList.add("null");
							signalStrengthLTECQIList.add("null");
							signalStrengthLTERSSNRList.add("null");
							signalStrengthLTERSRQList.add("null");
							signalStrengthLTERSRPList.add("null");
						}
						if(deviceInfo.getSignalStrengthGSM().equalsIgnoreCase("false"))
						{
							signalStrengthGSMList.add(deviceInfo.getSignalStrengthGSM());
							signalStrengthCDMACIOList.add(deviceInfo.getSignalStrengthCDMACIO());
							signalStrengthSnrList.add(deviceInfo.getSignalStrengthSnr());
							signalStrengthEVDOECIOList.add(deviceInfo.getSignalStrengthEVDOECIO());
							signalStrengthCDMAList.add(deviceInfo.getSignalStrengthCDMA());
							signalStrengthEVDOList.add(deviceInfo.getSignalStrengthEVDO());
						}	
						signalStrengthListRating.add(deviceInfo
								.getSignalStrengthEVDO());
					} else if (networkName.equalsIgnoreCase("LTE (4G)")) {
						if (deviceInfo.getSignalStrengthLTE().equals("Empty")
								|| deviceInfo.getSignalStrengthLTE().equals("")) {
							int signalStrengthLTE = Integer.parseInt("0");
							int signalStrengthLTEValue = signalStrengthLTE;
							signalStrengthList.add(String
									.valueOf(signalStrengthLTEValue + "dBm."));
							int signalStrengthLTValue = signalStrengthLTE;
							signalStrengthLt.add(String.valueOf(
									signalStrengthLTValue).substring(
									1,
									String.valueOf(signalStrengthLTValue)
											.length()));
							// signalStrengthLt.add(String.valueOf(deviceInfo.getSignalStrengthLTERSRP()).substring(1,
							// String.valueOf(deviceInfo.getSignalStrengthLTERSRP()).length()));
							signalStrengthLTERSRPList.add(deviceInfo
									.getSignalStrengthLTERSRP());
							signalStrengthLTERSRQList.add(deviceInfo
									.getSignalStrengthLTERSRQ());
							signalStrengthLTERSSNRList.add(deviceInfo
									.getSignalStrengthLTERSSNR());
							signalStrengthLTECQIList.add(deviceInfo
									.getSignalStrengthLTECQI());
							signalStrengthCDMACIOList.add("null");
							signalStrengthEVDOECIOList.add("null");
							signalStrengthSnrList.add("null");
						} else {
							// System.out
							// .println("deviceInfo.getSignalStrengthLTE()--------"
							// + deviceInfo.getSignalStrengthLTE());
							int signalStrengthLTE = Integer.parseInt(deviceInfo
									.getSignalStrengthLTE());
							int signalStrengthLTEValue = -140
									+ signalStrengthLTE;
							signalStrengthLTE = Integer.parseInt("0");
							signalStrengthList.add(String
									.valueOf(signalStrengthLTEValue + "dBm."));
							int signalStrengthLTValue = signalStrengthLTE;
							signalStrengthLt.add(String.valueOf(
									signalStrengthLTValue).substring(
									1,
									String.valueOf(signalStrengthLTValue)
											.length()));
							// signalStrengthLt.add(String.valueOf(deviceInfo.getSignalStrengthLTERSRP()).substring(1,
							// String.valueOf(deviceInfo.getSignalStrengthLTERSRP()).length()));
							signalStrengthLTERSRPList.add(deviceInfo
									.getSignalStrengthLTERSRP());
							signalStrengthLTERSRQList.add(deviceInfo
									.getSignalStrengthLTERSRQ());
							signalStrengthLTERSSNRList.add(deviceInfo
									.getSignalStrengthLTERSSNR());
							signalStrengthLTECQIList.add(deviceInfo
									.getSignalStrengthLTECQI());
							signalStrengthCDMACIOList.add("null");
							signalStrengthEVDOECIOList.add("null");
							signalStrengthSnrList.add("null");
						}
						signalStrengthListRating.add(deviceInfo
								.getSignalStrengthLTE());
					} else if (networkName.matches("wifi")) {
						if (deviceInfo.getSignalStrength().equals("Empty")
								|| deviceInfo.getSignalStrength().equals("")) {
							int signalStrengthGSM = Integer.parseInt("0");
							int signalStrengthGSMValue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthList.add(String
									.valueOf(signalStrengthGSMValue + "dBm."));
							int signalStrengthLtvalue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthLt.add(String.valueOf(
									signalStrengthLtvalue).substring(
									1,
									String.valueOf(signalStrengthLtvalue)
											.length()));
							signalStrengthCDMACIOList.add("null");
							signalStrengthEVDOECIOList.add("null");
							signalStrengthSnrList.add("null");
							signalStrengthLTECQIList.add("null");
							signalStrengthLTERSSNRList.add("null");
							signalStrengthLTERSRQList.add("null");
							signalStrengthLTERSRPList.add("null");
						} else {
							int signalStrengthGSM = Integer.parseInt(deviceInfo
									.getSignalStrength());
							int signalStrengthGSMValue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthList.add(String
									.valueOf(signalStrengthGSMValue + "dBm."));
							int signalStrengthLtvalue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthLt.add(String.valueOf(
									signalStrengthLtvalue).substring(
									1,
									String.valueOf(signalStrengthLtvalue)
											.length()));
							signalStrengthCDMACIOList.add("null");
							signalStrengthEVDOECIOList.add("null");
							signalStrengthSnrList.add("null");
							signalStrengthLTECQIList.add("null");
							signalStrengthLTERSSNRList.add("null");
							signalStrengthLTERSRQList.add("null");
							signalStrengthLTERSRPList.add("null");
						}
						// System.out
						// .println("getWifiInfoLinkSpeed()----------------------"
						// + deviceInfo.getWifiInfoLinkSpeed());
						wifiInfoBssid.set(i, deviceInfo.getWifiInfoBssid());
						wifiInfoLinkSpeed.set(i, deviceInfo
								.getWifiInfoLinkSpeed());
						wifiInfoRssi.set(i, deviceInfo.getWifiRssi());
						signalStrengthListRating.add(deviceInfo
								.getSignalStrength());
					} else {
						if (deviceInfo.getSignalStrength().equals("Empty")
								|| deviceInfo.getSignalStrength().equals("")) {
							int signalStrengthGSM = Integer.parseInt("0");
							int signalStrengthGSMValue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthList.add(String
									.valueOf(signalStrengthGSMValue + "dBm."));
							int signalStrengthLtvalue = Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthLt.add(String.valueOf(
									signalStrengthLtvalue).substring(
									1,
									String.valueOf(signalStrengthLtvalue)
											.length()));
							signalStrengthCDMACIOList.add("null");
							signalStrengthEVDOECIOList.add("null");
							signalStrengthSnrList.add("null");
							signalStrengthLTECQIList.add("null");
							signalStrengthLTERSSNRList.add("null");
							signalStrengthLTERSRQList.add("null");
							signalStrengthLTERSRPList.add("null");
						} else {
							int signalStrengthGSM = Integer.parseInt(deviceInfo
									.getSignalStrength());
							int signalStrengthGSMValue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthList.add(String
									.valueOf(signalStrengthGSMValue + "dBm."));
							int signalStrengthLtvalue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthLt.add(String.valueOf(
									signalStrengthLtvalue).substring(
									1,
									String.valueOf(signalStrengthLtvalue)
											.length()));
							signalStrengthCDMACIOList.add("null");
							signalStrengthEVDOECIOList.add("null");
							signalStrengthSnrList.add("null");
							signalStrengthLTECQIList.add("null");
							signalStrengthLTERSSNRList.add("null");
							signalStrengthLTERSRQList.add("null");
							signalStrengthLTERSRPList.add("null");
						}
						signalStrengthListRating.add(deviceInfo
								.getSignalStrength());
					}
					network = deviceInfo.getNetworkType().trim();
					networkTypeList.add(network);
					networkDataList.add(deviceInfo.getNetworkDataState());
					networkRoamingList.add(deviceInfo.getNetworkRoaming());
					networkMccList.add(deviceInfo.getNetworkMCC());
					networkMncList.add(deviceInfo.getNetworkMNC());
					cellLocationCidList.add(deviceInfo.getCellLocationCID());
					cellLocationLacList.add(deviceInfo.getCellLocationLAC());
					devicePhoneTypeList.add(deviceInfo.getDevicePhoneType());
					if (fileName != null) {
						if (ENVIRONMENT.equals("PROD")) {
							ipAddress = "50.112.27.46";
							url = "http://" + ipAddress + ":" + ServerPort
									+ "/FileService/" + fileName;
						} else {
							url = "http://" + ipAddress + ":" + ServerPort
									+ "/FileService/" + fileName;
						}
						System.out.println("url on reports bean page :"+url);
						String dateValue = "'--"
								+ deviceInfo.getTimeStampForEachSample() + "'";
						timeStampForEachSampleList.add("<b>"
								+ deviceInfo.getTimeStampForEachSample()
								+ "</b>");
						timeStampForSampleList.add(deviceInfo
								.getTimeStampForEachSample());
					} else {
						timeStampForEachSampleList.add("<b>Test Times:</b>"
								+ deviceInfo.getTimeStampForEachSample());
						timeStampForSampleList.add(deviceInfo
								.getTimeStampForEachSample());
					}

					neighbourInfoList.add(deviceInfo.getNeighbourInfo());
					for (int j = 0; j < neighbourInfoList.size(); j++) {
						if (null != neighbourInfoList.get(j)
								&& !neighbourInfoList.get(j).toString().equals(
										"Empty")) {
							String neighbourInfoArray[] = neighbourInfoList
									.get(j).toString().split("\\$");
							for (int a = 0; a < neighbourInfoArray.length; a++) {
								String neighbourSubInfoArray[] = neighbourInfoArray[a]
										.split("\\^");
								if (a == 0) {
									if (neighbourtStr == null) {
										if (neighbourSubInfoArray[0]
												.matches("-1")
												&& neighbourSubInfoArray[1]
														.matches("-1")) {
											neighbourtStr = "Cell: Empty" + ","
													+ "Lac:Empty" + ","
													+ "PSC:"
													+ neighbourSubInfoArray[3]
													+ "," + "RSSI:"
													+ neighbourSubInfoArray[4];
										} else if (neighbourSubInfoArray[0]
												.matches("-1")) {
											neighbourtStr = "Cell: Empty" + ","
													+ "Lac:"
													+ neighbourSubInfoArray[1]
													+ "," + "PSC:"
													+ neighbourSubInfoArray[3]
													+ "," + "RSSI:"
													+ neighbourSubInfoArray[4];
										} else if (neighbourSubInfoArray[1]
												.matches("-1")) {
											neighbourtStr = "Cell:"
													+ neighbourSubInfoArray[0]
													+ "," + "Lac:Empty" + ","
													+ "PSC:"
													+ neighbourSubInfoArray[3]
													+ "," + "RSSI:"
													+ neighbourSubInfoArray[4];
										} else {
											neighbourtStr = "Cell:"
													+ neighbourSubInfoArray[0]
													+ "," + "Lac:"
													+ neighbourSubInfoArray[1]
													+ "," + "PSC:"
													+ neighbourSubInfoArray[3]
													+ "," + "RSSI:"
													+ neighbourSubInfoArray[4];
										}
									} else {
										if (neighbourSubInfoArray[0]
												.matches("-1")
												&& neighbourSubInfoArray[1]
														.matches("-1")) {
											neighbourtStr = neighbourtStr
													+ "|||Cell:Empty" + ","
													+ "Lac:Empty" + ","
													+ "PSC:"
													+ neighbourSubInfoArray[3]
													+ "," + "RSSI:"
													+ neighbourSubInfoArray[4];
										} else if (neighbourSubInfoArray[0]
												.matches("-1")) {
											neighbourtStr = neighbourtStr
													+ "|||Cell:Empty" + ","
													+ "Lac:"
													+ neighbourSubInfoArray[1]
													+ "," + "PSC:"
													+ neighbourSubInfoArray[3]
													+ "," + "RSSI:"
													+ neighbourSubInfoArray[4];
										} else if (neighbourSubInfoArray[1]
												.matches("-1")) {
											neighbourtStr = neighbourtStr
													+ "|||Cell:"
													+ neighbourSubInfoArray[0]
													+ "," + "Lac:Empty" + ","
													+ "PSC:"
													+ neighbourSubInfoArray[3]
													+ "," + "RSSI:"
													+ neighbourSubInfoArray[4];
										} else {
											neighbourtStr = neighbourtStr
													+ "|||Cell:"
													+ neighbourSubInfoArray[0]
													+ "," + "Lac:"
													+ neighbourSubInfoArray[1]
													+ "," + "PSC:"
													+ neighbourSubInfoArray[3]
													+ "," + "RSSI:"
													+ neighbourSubInfoArray[4];
										}

									}
								} else if (a == (neighbourInfoArray.length - 1)) {
									if (neighbourSubInfoArray[0].matches("-1")
											&& neighbourSubInfoArray[1]
													.matches("-1")) {
										neighbourtStr = neighbourtStr
												+ "|||Cell:Empty" + ","
												+ "Lac:Empty" + "," + "PSC:"
												+ neighbourSubInfoArray[3]
												+ "," + "RSSI:"
												+ neighbourSubInfoArray[4]
												+ "||";
									} else if (neighbourSubInfoArray[0]
											.matches("-1")) {
										neighbourtStr = neighbourtStr
												+ "|||Cell:Empty" + ","
												+ "Lac:"
												+ neighbourSubInfoArray[1]
												+ "," + "PSC:"
												+ neighbourSubInfoArray[3]
												+ "," + "RSSI:"
												+ neighbourSubInfoArray[4]
												+ "||";
									} else if (neighbourSubInfoArray[1]
											.matches("-1")) {
										neighbourtStr = neighbourtStr
												+ "|||Cell:"
												+ neighbourSubInfoArray[0]
												+ "," + "Lac:Empty" + ","
												+ "PSC:"
												+ neighbourSubInfoArray[3]
												+ "," + "RSSI:"
												+ neighbourSubInfoArray[4]
												+ "||";
									} else {
										neighbourtStr = neighbourtStr
												+ "|||Cell:"
												+ neighbourSubInfoArray[0]
												+ "," + "Lac:"
												+ neighbourSubInfoArray[1]
												+ "," + "PSC:"
												+ neighbourSubInfoArray[3]
												+ "," + "RSSI:"
												+ neighbourSubInfoArray[4]
												+ "||";
									}

								} else {
									if (neighbourSubInfoArray[0].matches("-1")
											&& neighbourSubInfoArray[1]
													.matches("-1")) {
										neighbourtStr = neighbourtStr
												+ "|||Cell:Empty" + ","
												+ "Lac:Empty" + "," + "PSC:"
												+ neighbourSubInfoArray[3]
												+ "," + "RSSI:"
												+ neighbourSubInfoArray[4];
									} else if (neighbourSubInfoArray[0]
											.matches("-1")) {
										neighbourtStr = neighbourtStr
												+ "|||Cell:Empty" + ","
												+ "Lac:"
												+ neighbourSubInfoArray[1]
												+ "," + "PSC:"
												+ neighbourSubInfoArray[3]
												+ "," + "RSSI:"
												+ neighbourSubInfoArray[4];
									} else if (neighbourSubInfoArray[1]
											.matches("-1")) {
										neighbourtStr = neighbourtStr
												+ "|||Cell:"
												+ neighbourSubInfoArray[0]
												+ "," + "Lac:Empty" + ","
												+ "PSC:"
												+ neighbourSubInfoArray[3]
												+ "," + "RSSI:"
												+ neighbourSubInfoArray[4];
									} else {
										neighbourtStr = neighbourtStr
												+ "|||Cell:"
												+ neighbourSubInfoArray[0]
												+ "," + "Lac:"
												+ neighbourSubInfoArray[1]
												+ "," + "PSC:"
												+ neighbourSubInfoArray[3]
												+ "," + "RSSI:"
												+ neighbourSubInfoArray[4];
									}
								}
							}
						} else {
							neighbourtStr = neighbourtStr
									+ "|||Cell:Empty,Lac:Empty,PSC:Empty,RSSI:Empty||";
						}
					}
					neighbourInfoList.clear();
					if (neighboursubInfoList.size() > 0) {
						boolean single = true;
						for (int x = 0; x < neighboursubInfoList.size(); x++) {
							if (neighbourtStr == null) {
								neighbourtStr = neighboursubInfoList.get(x)
										.toString();
							} else {
								neighbourtStr = neighbourtStr
										+ ","
										+ neighboursubInfoList.get(x)
												.toString();
							}
						}
					}

				}
				// logger.info("lattitudeList----" +
				// lattitudeList.size());
				if (null != neighbourtStr) {
					neighbourtStr = neighbourtStr.substring(0, neighbourtStr
							.length() - 1);
					neighbourtStr = neighbourtStr.substring(0, neighbourtStr
							.length() - 1);
				}

				// Collections.sort(lattitudeList);
				// Collections.sort(longitudeList);
				latitudes = lattitudeList.toString();
				longitudes = longitudeList.toString();
				String freqBand = freqList.toString();
				testName = testNameList.toString();
				signalStrength = signalStrengthList.toString();
				signalStrengthLTT = signalStrengthLt.toString();
				signalStrengthRating = signalStrengthListRating.toString();
				networkType = networkTypeList.toString();
				networkData = networkDataList.toString();
				networkRoaming = networkRoamingList.toString();
				networkMnc = networkMncList.toString();
				networkMcc = networkMccList.toString();
				cellLocationLac = cellLocationLacList.toString();
				cellLocationCid = cellLocationCidList.toString();
				
				devicePhoneType = devicePhoneTypeList.toString();
				timeStampForEachSample = timeStampForEachSampleList.toString();
				timeStampForSample = timeStampForSampleList.toString();
				neighbourMainString = neighbourMainList.toString();
				
				signalStrengthCDMACIO = signalStrengthCDMACIOList.toString();
				signalStrengthEVDOECIO = signalStrengthEVDOECIOList.toString();
				signalStrengthSnr = signalStrengthSnrList.toString();
				signalStrengthCDMA = signalStrengthCDMAList.toString();
				signalStrengthEVDO = signalStrengthEVDOList.toString();
				
				signalStrengthLTERSRP = signalStrengthLTERSRPList.toString();
				signalStrengthLTERSRQ = signalStrengthLTERSRQList.toString();
				signalStrengthLTERSSNR = signalStrengthLTERSSNRList.toString();
				signalStrengthLTECQI = signalStrengthLTECQIList.toString();
				signalStrengthGSMStr = signalStrengthGSMList.toString();
				imei = imeiList.toString();
				
				removeSession(context);
				//System.out.println("freqBand-----------" + freqBand);
				context.getExternalContext().getSessionMap().put("signalStrengthGSMStr", 
						signalStrengthGSMStr.substring(1,signalStrengthGSMStr.length()-1));
				context.getExternalContext().getSessionMap().put(
						"mapReportType", mapReportType);
				context.getExternalContext().getSessionMap().put("lattitudes",
						latitudes.substring(1, latitudes.length() - 1));
				context.getExternalContext().getSessionMap().put("longitudes",
						longitudes.substring(1, longitudes.length() - 1));
				context.getExternalContext().getSessionMap().put("freqBand",
						freqBand.substring(1, freqBand.length() - 1));
				context.getExternalContext().getSessionMap().put("testName",
						testName.substring(1, testName.length() - 1));
				
				context.getExternalContext().getSessionMap().put("Imei",
						imei.substring(1, imei.length() - 1));
				
				context.getExternalContext().getSessionMap().put(
						"signalStrength",
						signalStrength
								.substring(1, signalStrength.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthLTT",
						signalStrengthLTT.substring(1, signalStrengthLTT
								.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthRating",
						signalStrengthRating.substring(1, signalStrengthRating
								.length() - 1));
				context.getExternalContext().getSessionMap().put("networkType",
						networkType.substring(1, networkType.length() - 1));
				context.getExternalContext().getSessionMap().put("networkData",
						networkData.substring(1, networkData.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"networkRoaming",
						networkRoaming
								.substring(1, networkRoaming.length() - 1));
				context.getExternalContext().getSessionMap().put("networkMnc",
						networkMnc.substring(1, networkMnc.length() - 1));
				context.getExternalContext().getSessionMap().put("networkMcc",
						networkMcc.substring(1, networkMcc.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"cellLocationLac",
						cellLocationLac.substring(1,
								cellLocationLac.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"cellLocationCid",
						cellLocationCid.substring(1,
								cellLocationCid.length() - 1));
				
				
				context.getExternalContext().getSessionMap().put(
						"signalStrengthCDMACIO",
						signalStrengthCDMACIO.substring(1, signalStrengthCDMACIO
								.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthEVDOECIO",
						signalStrengthEVDOECIO.substring(1, signalStrengthEVDOECIO
								.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthCDMA",
						signalStrengthCDMA.substring(1, signalStrengthCDMA
								.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthEVDO",
						signalStrengthEVDO.substring(1, signalStrengthEVDO
								.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthSnr",
						signalStrengthSnr.substring(1, signalStrengthSnr
								.length() - 1));
				
				
				context.getExternalContext().getSessionMap().put(
						"devicePhoneType",
						devicePhoneType.substring(1,
								devicePhoneType.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"timeStampForEachSample",
						timeStampForEachSample.substring(1,
								timeStampForEachSample.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"timeStampForSample",
						timeStampForSample.substring(1, timeStampForSample
								.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"neighbourInfo", neighbourtStr);
				context.getExternalContext().getSessionMap().put(
						"signalStrengthCDMACIO",
						signalStrengthCDMACIO.substring(1,
								signalStrengthCDMACIO.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthEVDOECIO",
						signalStrengthEVDOECIO.substring(1,
								signalStrengthEVDOECIO.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthLTERSRP",
						signalStrengthLTERSRP.substring(1,
								signalStrengthLTERSRP.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthLTERSRQ",
						signalStrengthLTERSRQ.substring(1,
								signalStrengthLTERSRQ.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthLTERSSNR",
						signalStrengthLTERSSNR.substring(1,
								signalStrengthLTERSSNR.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthLTECQI",
						signalStrengthLTECQI.substring(1, signalStrengthLTECQI
								.length() - 1));

				if (wifiInfoRssi.toString().length() > 2) {
					context.getExternalContext().getSessionMap().put(
							"wifiinforssi",
							wifiInfoRssi.toString().substring(1,
									wifiInfoRssi.toString().length() - 1));
				}
				context.getExternalContext().getSessionMap().put(
						"wifiInfoLinkSpeed",
						wifiInfoLinkSpeed.toString().substring(1,
								wifiInfoLinkSpeed.toString().length() - 1));
				context.getExternalContext().getSessionMap().put(
						"wifiInfoBssid",
						wifiInfoBssid.toString().substring(1,
								wifiInfoBssid.toString().length() - 1));
				// logger.info("wifiInfoLinkSpeed---------"
				// + wifiInfoLinkSpeed);
				if (deviceInfoCallDropList.size() > 0) {
					context.getExternalContext().getSessionMap().put(
							"deviceInfoCallDropList",
							callDrop.substring(1, callDrop.length()));
				} else {
					context.getExternalContext().getSessionMap().put(
							"deviceInfoCallDropList", callDrop);
				}
				// System.out
				// .println(context.getExternalContext().getSessionMap());
			}
		} else if (mapReportType.equals("throughput")) {
			ReportDao dao = new ReportDaoImpl();
			boolean status = false;
			FTPReportHelper ftpReportHelper = new FTPReportHelper();
			ThroughputDeviceresults = ftpReportHelper.getThroughputForMaps(
					testCaseName, "Current TX bytes", marketmapId);// getTestNameThroughputDetailsResults(testCaseName,marketmapId,test_type);
			ThroughputDeviceresults.addAll(ftpReportHelper
					.getThroughputForMaps(testCaseName, "Current RX bytes",
							marketmapId));
			List<DeviceInfoTO> allFtpPoints = new UserDaoImpl()
					.getAllDeviceInfo(testCaseName, marketmapId, test_type,
							frequencyPlan);

			String pattern = "\\-%";
			Pattern r = Pattern.compile(pattern);
			Matcher m = r.matcher(testCaseName);
			// status=dao.getCycleTestNames(testCaseName);
			if (status == true) {
				if (ThroughputDeviceresults.size() == 0) {
					List<DeviceInfoTO> ThroughputDeviceMultipleresultsMultiple = dao
							.getTestNameMultipleThroughputDetailsResults(
									testCaseName, marketmapId, test_type);
					if (ThroughputDeviceMultipleresultsMultiple.size() > 0) {
						ThroughputDeviceresults = ThroughputDeviceMultipleresultsMultiple;
					} else {
						ThroughputDeviceresults = ThroughputDeviceMultipleresultsMultiple;
					}
				} else {
					ThroughputDeviceresults = ThroughputDeviceresults;
				}
			}

			String userrole = context.getExternalContext().getSessionMap().get(
					"loggedInUserRoleID").toString();
			context.getExternalContext().getSessionMap().put(
					"loggedInUserRoleID", userrole);
			if (ThroughputDeviceresults.size() <= 0) {
				context.getExternalContext().getRequestMap().remove(
						"datamessage");
				context.getExternalContext().getRequestMap().put("datamessage",
						"No Data Found for the Selected Test Name");
				removeThroughputSession(context);
				return "mapDataValidation";
			} else {
				String latitudes = "";
				String longitudes = "";
				String testName = "";
				String Imei = "";
				String signalStrength = "";
				String signalStrengthRating = "";
				String networkType = "";
				String cellLocationCID = "";
				String timeStampForEachSample = "";
				String signalStrengthLTT = "";
				String network = "";
				String eventName = "";
				List lattitudeList = new ArrayList();
				List longitudeList = new ArrayList();
				List testNameList = new ArrayList();
				List imeiList = new ArrayList();//New code
				List signalStrengthList = new ArrayList();
				List networkTypeList = new ArrayList();
				List cellLocationList = new ArrayList();
				List timeStampForEachSampleList = new ArrayList();
				List throughputList = new ArrayList();
				List signalStrengthListRating = new ArrayList();
				List signalStrengthLt = new ArrayList();
				List snapShotList = new ArrayList();
				List cellLocationLacList = new ArrayList();
				List devicePhoneTypeList = new ArrayList();
				List signalStrengthLTERSRPList = new ArrayList();
				List signalStrengthGSMList = new ArrayList();				
				
				List signalStrengthCDMACIOList=new ArrayList();
				List signalStrengthSnrList=new ArrayList();
				List signalStrengthEVDOECIOList=new ArrayList();
				List signalStrengthCDMAList=new ArrayList();
				List signalStrengthEVDOList=new ArrayList();
				
				List networkMccList = new ArrayList();
				List networkMncList = new ArrayList();
				String networkName = "";
				for (int i = 0; i < allFtpPoints.size(); i++) {
					DeviceInfoTO deviceInfo = allFtpPoints.get(i);
					networkName = deviceInfo.getNetworkType();
					lattitudeList.add(deviceInfo.getLattitude());
					longitudeList.add(deviceInfo.getLongitude());
					testNameList.add(deviceInfo.getTestName());
					imeiList.add(deviceInfo.getImei());
					snapShotList.add(deviceInfo.getSnapShotId());
					network = deviceInfo.getNetworkType().trim();

					networkTypeList.add(network);
					cellLocationList.add(deviceInfo.getCellLocationCID());
					cellLocationLacList.add(deviceInfo.getCellLocationLAC());
					devicePhoneTypeList.add(deviceInfo.getDevicePhoneType());
					timeStampForEachSampleList.add(deviceInfo
							.getTimeStampForEachSample());
					networkMccList.add(deviceInfo.getNetworkMCC());
					networkMncList.add(deviceInfo.getNetworkMNC());
					eventName = deviceInfo.getEventName();

					if (networkName.matches("GSM")) {
						if (deviceInfo.getSignalStrength().equals("Empty")
								|| deviceInfo.getSignalStrength().equals("")) {
							int signalStrengthGSM = Integer.parseInt("0");
							int signalStrengthGSMValue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthList.add(String
									.valueOf(signalStrengthGSMValue + "dBm."));
							int signalStrengthLtvalue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthLt.add(String.valueOf(
									signalStrengthLtvalue).substring(
									1,
									String.valueOf(signalStrengthLtvalue)
											.length()));
							signalStrengthLTERSRPList.add("null");
						} else {
							int signalStrengthGSM = Integer.parseInt(deviceInfo
									.getSignalStrength());
							int signalStrengthGSMValue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthList.add(String
									.valueOf(signalStrengthGSMValue + "dBm."));
							int signalStrengthLtvalue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthLt.add(String.valueOf(
									signalStrengthLtvalue).substring(
									1,
									String.valueOf(signalStrengthLtvalue)
											.length()));
							signalStrengthLTERSRPList.add("null");
						}
						signalStrengthListRating.add(deviceInfo
								.getSignalStrength());
					} else if (networkName.matches("CDMA")) {
						if (deviceInfo.getSignalStrengthCDMA().equals("Empty")
								|| deviceInfo.getSignalStrengthCDMA()
										.equals("")) {
							signalStrengthList.add("0");
							signalStrengthLt.add("0");
							signalStrengthLTERSRPList.add("null");
						} else {
							signalStrengthList.add(deviceInfo
									.getSignalStrengthCDMA());
							signalStrengthLt.add(deviceInfo
									.getSignalStrengthCDMA());
							signalStrengthLTERSRPList.add("null");
						}
						signalStrengthListRating.add(deviceInfo
								.getSignalStrengthCDMA());
						if(deviceInfo.getSignalStrengthGSM().equalsIgnoreCase("false"))
						{
							signalStrengthGSMList.add(deviceInfo.getSignalStrengthGSM());
							signalStrengthCDMACIOList.add(deviceInfo.getSignalStrengthCDMACIO());
							signalStrengthSnrList.add(deviceInfo.getSignalStrengthSnr());
							signalStrengthEVDOECIOList.add(deviceInfo.getSignalStrengthEVDOECIO());
							signalStrengthCDMAList.add(deviceInfo.getSignalStrengthCDMA());
							signalStrengthEVDOList.add(deviceInfo.getSignalStrengthEVDO());
						}
					} else if (networkName.matches("EVDO") || networkName.matches("eHRPD") || networkName.matches("EHRPD (3G)")
							|| networkName.matches("EVDO-A") || networkName.matches("EVDO-B")) {
						if (deviceInfo.getSignalStrengthEVDO().equals("Empty")
								|| deviceInfo.getSignalStrengthEVDO()
										.equals("")) {
							signalStrengthList.add("0");
							signalStrengthLt.add("0");
							signalStrengthLTERSRPList.add("null");
						} else {
							signalStrengthList.add(deviceInfo
									.getSignalStrengthEVDO());
							signalStrengthLt.add(deviceInfo
									.getSignalStrengthEVDO());
							signalStrengthLTERSRPList.add("null");
						}
						signalStrengthListRating.add(deviceInfo
								.getSignalStrengthEVDO());
					} else if (networkName.equalsIgnoreCase("LTE (4G)")) {
						if (deviceInfo.getSignalStrengthLTE().equals("Empty")
								|| deviceInfo.getSignalStrengthLTE().equals("")) {
							int signalStrengthLTE = Integer.parseInt("0");
							int signalStrengthLTEValue = signalStrengthLTE;
							signalStrengthList.add(String
									.valueOf(signalStrengthLTEValue + "dBm."));
							int signalStrengthLTValue = signalStrengthLTE;
							signalStrengthLt.add(String.valueOf(
									signalStrengthLTValue).substring(
									1,
									String.valueOf(signalStrengthLTValue)
											.length()));
							// signalStrengthLt.add(String.valueOf(deviceInfo.getSignalStrengthLTERSRP()).substring(1,
							// String.valueOf(deviceInfo.getSignalStrengthLTERSRP()).length()));
							signalStrengthLTERSRPList.add(deviceInfo
									.getSignalStrengthLTERSRP());
						} else {
							// System.out
							// .println("deviceInfo.getSignalStrengthLTE()--------"
							// + deviceInfo.getSignalStrengthLTE());
							int signalStrengthLTE = Integer.parseInt(deviceInfo
									.getSignalStrengthLTE());
							int signalStrengthLTEValue = -140
									+ signalStrengthLTE;
							signalStrengthLTE = Integer.parseInt("0");
							signalStrengthList.add(String
									.valueOf(signalStrengthLTEValue + "dBm."));
							int signalStrengthLTValue = signalStrengthLTE;
							signalStrengthLt.add(String.valueOf(
									signalStrengthLTValue).substring(
									1,
									String.valueOf(signalStrengthLTValue)
											.length()));
							// signalStrengthLt.add(String.valueOf(deviceInfo.getSignalStrengthLTERSRP()).substring(1,
							// String.valueOf(deviceInfo.getSignalStrengthLTERSRP()).length()));
							signalStrengthLTERSRPList.add(deviceInfo
									.getSignalStrengthLTERSRP());
						}
						signalStrengthListRating.add(deviceInfo
								.getSignalStrengthLTE());
					} else if (networkName.matches("wifi")) {
						if (deviceInfo.getSignalStrength().equals("Empty")
								|| deviceInfo.getSignalStrength().equals("")) {
							int signalStrengthGSM = Integer.parseInt("0");
							int signalStrengthGSMValue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthList.add(String
									.valueOf(signalStrengthGSMValue + "dBm."));
							int signalStrengthLtvalue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthLt.add(String.valueOf(
									signalStrengthLtvalue).substring(
									1,
									String.valueOf(signalStrengthLtvalue)
											.length()));
							signalStrengthLTERSRPList.add("null");
						} else {
							int signalStrengthGSM = Integer.parseInt(deviceInfo
									.getSignalStrength());
							int signalStrengthGSMValue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthList.add(String
									.valueOf(signalStrengthGSMValue + "dBm."));
							int signalStrengthLtvalue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthLt.add(String.valueOf(
									signalStrengthLtvalue).substring(
									1,
									String.valueOf(signalStrengthLtvalue)
											.length()));
							signalStrengthLTERSRPList.add("null");
						}
						// System.out
						// .println("getWifiInfoLinkSpeed()----------------------"
						// + deviceInfo.getWifiInfoLinkSpeed());

						signalStrengthListRating.add(deviceInfo
								.getSignalStrength());
					} else {
						if (deviceInfo.getSignalStrength().equals("Empty")
								|| deviceInfo.getSignalStrength().equals("")) {
							int signalStrengthGSM = Integer.parseInt("0");
							int signalStrengthGSMValue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthList.add(String
									.valueOf(signalStrengthGSMValue + "dBm."));
							int signalStrengthLtvalue = Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthLt.add(String.valueOf(
									signalStrengthLtvalue).substring(
									1,
									String.valueOf(signalStrengthLtvalue)
											.length()));
							signalStrengthLTERSRPList.add("null");
						} else {
							int signalStrengthGSM = Integer.parseInt(deviceInfo
									.getSignalStrength());
							int signalStrengthGSMValue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthList.add(String
									.valueOf(signalStrengthGSMValue + "dBm."));
							int signalStrengthLtvalue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthLt.add(String.valueOf(
									signalStrengthLtvalue).substring(
									1,
									String.valueOf(signalStrengthLtvalue)
											.length()));
							signalStrengthLTERSRPList.add("null");
						}
						signalStrengthListRating.add(deviceInfo
								.getSignalStrength());
					}
					throughputList.add(0);

				}

				latitudes = lattitudeList.toString();
				longitudes = longitudeList.toString();
				testName = testNameList.toString();
				Imei = imeiList.toString();//New code...
				signalStrength = signalStrengthList.toString();
				signalStrengthRating = signalStrengthListRating.toString();
				networkType = networkTypeList.toString();
				timeStampForEachSample = timeStampForEachSampleList.toString();
				String throughput = throughputList.toString();
				cellLocationCID = cellLocationList.toString();
				signalStrengthLTT = signalStrengthLt.toString();
				String cellLocationLac = cellLocationLacList.toString();
				String snapShot = snapShotList.toString();
				String networkMnc = networkMncList.toString();
				String networkMcc = networkMccList.toString();
				String signalStrengthLTERSRP = signalStrengthLTERSRPList
						.toString();
				String signalStrengthGSMStr = signalStrengthGSMList.toString();
				String signalStrengthCDMACIO = signalStrengthCDMACIOList.toString();
				String signalStrengthEVDOECIO = signalStrengthEVDOECIOList.toString();
				String signalStrengthSnr = signalStrengthSnrList.toString();
				String signalStrengthCDMA = signalStrengthCDMAList.toString();
				String signalStrengthEVDO = signalStrengthEVDOList.toString();
				
				// removeThroughputSession(context);

				context.getExternalContext().getSessionMap().put(
						"deviceInfomapReportType", mapReportType);
				context.getExternalContext().getSessionMap().put(
						"deviceInfosignalStrengthLTT",
						signalStrengthLTT.substring(1, signalStrengthLTT
								.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthLTERSRP",
						signalStrengthLTERSRP.substring(1,
								signalStrengthLTERSRP.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"deviceInfolattitudes",
						latitudes.substring(1, latitudes.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"deviceInfolongitudes",
						longitudes.substring(1, longitudes.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"deviceInfotestName",
						testName.substring(1, testName.length() - 1));
				
				context.getExternalContext().getSessionMap().put(
						"Imei",
						Imei.substring(1, Imei.length() - 1));
				
				context.getExternalContext().getSessionMap().put(
						"deviceInfosignalStrength",
						signalStrength
								.substring(1, signalStrength.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"deviceInfosignalStrengthRating",
						signalStrengthRating.substring(1, signalStrengthRating
								.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"deviceInfonetworkType",
						networkType.substring(1, networkType.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"deviceInfotimeStampForEachSample",
						timeStampForEachSample.substring(1,
								timeStampForEachSample.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"deviceInfothroughput",
						throughput.substring(1, throughput.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"deviceInfocellLocationCID",
						cellLocationCID.substring(1,
								cellLocationCID.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"deviceInfoSnapShot",
						snapShot.substring(1, snapShot.length() - 1));
				context.getExternalContext().getSessionMap().put("networkMnc",
						networkMnc.substring(1, networkMnc.length() - 1));
				context.getExternalContext().getSessionMap().put("networkMcc",
						networkMcc.substring(1, networkMcc.length() - 1));

				ThrougputDataTO throughputTo = new MapReportHelper()
						.getThroughtPutDataForMap(ThroughputDeviceresults,
								timeStampForEachSampleList);
				latitudes = throughputTo.getLattitudes();// lattitudeList.toString();
				longitudes = throughputTo.getLongitudes();// longitudeList.toString();
				testName = throughputTo.getTestName();
				signalStrength = throughputTo.getSignalStrength();// signalStrengthList.toString();
				signalStrengthRating = throughputTo.getSignalStrengthRating();// signalStrengthListRating.toString();
				networkType = throughputTo.getNetworkType();// networkTypeList.toString();
				timeStampForEachSample = throughputTo
						.getTimeStampForEachSample();// timeStampForEachSampleList.toString();
				throughput = throughputTo.getThroughput();// throughputList.toString();
				// cellLocationCID = throughputTo.getCellLocationCID();//
				// cellLocationList.toString();
				// signalStrengthLTT = throughputTo.getSignalStrengthLTT();//
				// signalStrengthLt.toString();
				eventName = throughputTo.getEventName();
				removeThroughputSession(context);
				context.getExternalContext().getSessionMap().put("eventName",
						eventName.substring(1, eventName.length() - 1));
				context.getExternalContext().getSessionMap().put("networkMnc",
						networkMnc.substring(1, networkMnc.length() - 1));
				context.getExternalContext().getSessionMap().put("networkMcc",
						networkMcc.substring(1, networkMcc.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthLTT",
						signalStrengthLTT.substring(1, signalStrengthLTT
								.length() - 1));
				context.getExternalContext().getSessionMap().put("lattitudes",
						latitudes.substring(1, latitudes.length() - 1));
				context.getExternalContext().getSessionMap().put("longitudes",
						longitudes.substring(1, longitudes.length() - 1));
				context.getExternalContext().getSessionMap().put("testName",
						testName.substring(1, testName.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrength",
						signalStrength
								.substring(1, signalStrength.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthRating",
						signalStrengthRating.substring(1, signalStrengthRating
								.length() - 1));
				context.getExternalContext().getSessionMap().put("networkType",
						networkType.substring(1, networkType.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"timeStampForEachSample",
						timeStampForEachSample.substring(1,
								timeStampForEachSample.length() - 1));
				context.getExternalContext().getSessionMap().put("throughput",
						throughput.substring(1, throughput.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"cellLocationCID",
						cellLocationCID.substring(1,
								cellLocationCID.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"mapReportType", mapReportType);
				context.getExternalContext().getSessionMap().put(
						"cellLocationLac",
						cellLocationLac.substring(1,
								cellLocationLac.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthLTERSRP",
						signalStrengthLTERSRP.substring(1,
								signalStrengthLTERSRP.length() - 1));
				
				
				context.getExternalContext().getSessionMap().put(
						"signalStrengthCDMACIO",
						signalStrengthCDMACIO.substring(1, signalStrengthCDMACIO
								.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthEVDOECIO",
						signalStrengthEVDOECIO.substring(1, signalStrengthEVDOECIO
								.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthCDMA",
						signalStrengthCDMA.substring(1, signalStrengthCDMA
								.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthEVDO",
						signalStrengthEVDO.substring(1, signalStrengthEVDO
								.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthSnr",
						signalStrengthSnr.substring(1, signalStrengthSnr
								.length() - 1));
				context.getExternalContext().getSessionMap().put("signalStrengthGSMStr", 
						signalStrengthGSMStr.substring(1,signalStrengthGSMStr.length()-1));
				
				// logger.info("mapReportType----------------"
				// + context.getExternalContext().getSessionMap());
			}
		} else if (mapReportType.equals("LTE_Voice_Data")) {
			String selectedDevice = null;
			UserDao userDao = new UserDaoImpl();
			Voice_DataDao connectivityDao = new Voice_DataDaoImpl();
			ReportDao dao = new ReportDaoImpl();
			List<Voice_DataTO> deviceInfoList = null;
			List<String> timestamp = dao.getMinandMaxTimestampforNetCall(
					testCaseName, marketmapId);
			if (timestamp.size() < 0) {
				context.getExternalContext().getRequestMap().remove(
						"datamessage");
				context.getExternalContext().getRequestMap().put("datamessage",
						"No Data Found for the Selected Test Name");
				return "mapDataValidation";
			}
			if (test_type.equals("externaltest")) {
				deviceInfoList = connectivityDao.getAllDeviceInfo(testCaseName,
						marketmapId, test_type, timestamp);
			} else {
				context.getExternalContext().getRequestMap().remove(
						"datamessage");
				context.getExternalContext().getRequestMap().put("datamessage",
						"No Data Found for the Selected Test Name");
				return "mapDataValidation";
			}
			List<String> deviceInfoCallDropList = connectivityDao
					.getExternal_CallDropList(testCaseName, marketmapId);
			List<String> deviceInfoThroughputList = connectivityDao
					.getExternal_througputList(testCaseName, marketmapId);
			List<String> deviceInfoThroughput = connectivityDao
					.getExternal_througput(testCaseName, marketmapId);
			if (deviceInfoCallDropList.size() < 0
					&& deviceInfoThroughputList.size() < 0) {
				context.getExternalContext().getRequestMap().remove(
						"datamessage");
				context.getExternalContext().getRequestMap().put("datamessage",
						"No Data Found for the Selected Test Name");
				return "mapDataValidation";
			}

			try {
				InetAddress ip = InetAddress.getLocalHost();
				ipAddress = ip.getHostName();

				File f1 = new File(QXDM_FILE_PATH);
				File file1[] = f1.listFiles();
				String filename = null;// f.getName();
				for (File f : file1) {
					String tempFileName = f.getName();
					if ((tempFileName.startsWith("QXDM_" + testCaseName))
							&& (tempFileName.endsWith(".txt"))) {
						isQxdmPresent = "true";
						fileName = f.getName();
					}
				}
			} catch (UnknownHostException e) {
				logger.error(e.getMessage());
			}

			if (deviceInfoList.size() <= 0) {
				context.getExternalContext().getRequestMap().remove(
						"datamessage");
				context.getExternalContext().getRequestMap().put("datamessage",
						"No Data Found for the Selected Test Name");
				return "mapDataValidation";
			} else {
				String latitudes = "";
				String longitudes = "";
				String testName = "";
				String imei = "";
				String signalStrength = "";
				String signalStrengthLTT = "";
				String signalStrengthRating = "";
				String networkType = "";
				String cellLocationCid = "";
				String timeStampForEachSample = "";
				String timeStampForSample = "";
				String neighbourInfo = "";
				String neighbourMainString = "";
				String signalStrengthCDMACIO = "";
				String signalStrengthEVDOECIO = "";

				String signalStrengthLTERSRP = "";
				String signalStrengthLTERSRQ = "";
				String signalStrengthLTERSSNR = "";
				String signalStrengthLTECQI = "";
				String callDrop = "";
				String network = "";
				String througput = "";
				String througputTimestamp = "";

				// String details = "";
				List lattitudeList = new ArrayList();
				List longitudeList = new ArrayList();
				List testNameList = new ArrayList();
				List imeiList = new ArrayList();
				List callDropStatusList = new ArrayList();
				List signalStrengthList = new ArrayList();
				List signalStrengthListRating = new ArrayList();
				List signalStrengthLt = new ArrayList();
				List networkTypeList = new ArrayList();
				List cellLocationCidList = new ArrayList();
				List signalStrengthSnrList = new ArrayList();
				List timeStampForEachSampleList = new ArrayList();
				List timeStampForSampleList = new ArrayList();
				List signalStrengthCDMACIOList = new ArrayList();
				List signalStrengthEVDOECIOList = new ArrayList();
				List signalStrengthLTERSRPList = new ArrayList();
				List signalStrengthLTERSRQList = new ArrayList();
				List signalStrengthLTERSSNRList = new ArrayList();
				List signalStrengthLTECQIList = new ArrayList();

				List neighbourMainList = new ArrayList();
				String neighbourtStr = null;
				String networkName = "";

				for (int i = 0; i < deviceInfoList.size(); i++) {
					Voice_DataTO deviceInfo = deviceInfoList.get(i);
					networkName = deviceInfo.getNetworkType();
					lattitudeList.add(deviceInfo.getLattitude());
					longitudeList.add(deviceInfo.getLongitude());
					testNameList.add(deviceInfo.getTestName());
					imeiList.add(deviceInfo.getImei());
					cellLocationCidList.add(deviceInfo.getCellLocationCID());

					if (networkName.matches("GSM")) {
						if (deviceInfo.getSignalStrength().equals("Empty")
								|| deviceInfo.getSignalStrength().equals("")) {
							int signalStrengthGSM = Integer.parseInt("0");
							int signalStrengthGSMValue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthList.add(String
									.valueOf(signalStrengthGSMValue + "dBm."));
							int signalStrengthLtvalue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthLt.add(String.valueOf(
									signalStrengthLtvalue).substring(
									1,
									String.valueOf(signalStrengthLtvalue)
											.length()));
							signalStrengthCDMACIOList.add("null");
							signalStrengthEVDOECIOList.add("null");
							signalStrengthSnrList.add("null");
							signalStrengthLTECQIList.add("null");
							signalStrengthLTERSSNRList.add("null");
							signalStrengthLTERSRQList.add("null");
							signalStrengthLTERSRPList.add("null");
						} else {
							int signalStrengthGSM = Integer.parseInt(deviceInfo
									.getSignalStrength());
							int signalStrengthGSMValue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthList.add(String
									.valueOf(signalStrengthGSMValue + "dBm."));
							int signalStrengthLtvalue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthLt.add(String.valueOf(
									signalStrengthLtvalue).substring(
									1,
									String.valueOf(signalStrengthLtvalue)
											.length()));
							signalStrengthCDMACIOList.add("null");
							signalStrengthEVDOECIOList.add("null");
							signalStrengthSnrList.add("null");
							signalStrengthLTECQIList.add("null");
							signalStrengthLTERSSNRList.add("null");
							signalStrengthLTERSRQList.add("null");
							signalStrengthLTERSRPList.add("null");
						}
						signalStrengthListRating.add(deviceInfo
								.getSignalStrength());
					} else if (networkName.matches("CDMA")) {
						if (deviceInfo.getSignalStrengthCDMA().equals("Empty")
								|| deviceInfo.getSignalStrengthCDMA()
										.equals("")) {
							signalStrengthList.add("0");
							signalStrengthLt.add("0");
							signalStrengthCDMACIOList.add(deviceInfo
									.getSignalStrengthCDMACIO());
							signalStrengthEVDOECIOList.add("null");
							signalStrengthSnrList.add("null");
							signalStrengthLTECQIList.add("null");
							signalStrengthLTERSSNRList.add("null");
							signalStrengthLTERSRQList.add("null");
							signalStrengthLTERSRPList.add("null");
						} else {
							signalStrengthList.add(deviceInfo
									.getSignalStrengthCDMA());
							signalStrengthLt.add(deviceInfo
									.getSignalStrengthCDMA());
							signalStrengthCDMACIOList.add(deviceInfo
									.getSignalStrengthCDMACIO());
							signalStrengthEVDOECIOList.add("null");
							signalStrengthSnrList.add("null");
							signalStrengthLTECQIList.add("null");
							signalStrengthLTERSSNRList.add("null");
							signalStrengthLTERSRQList.add("null");
							signalStrengthLTERSRPList.add("null");
						}
						signalStrengthListRating.add(deviceInfo
								.getSignalStrengthCDMA());
					} else if (networkName.matches("EVDO") ||
							networkName.matches("eHRPD") || networkName.matches("EHRPD (3G)")
							|| networkName.matches("EVDO-A") || networkName.matches("EVDO-B")) {
						if (deviceInfo.getSignalStrengthEVDO().equals("Empty")
								|| deviceInfo.getSignalStrengthEVDO()
										.equals("")) {
							signalStrengthList.add("0");
							signalStrengthLt.add("0");
							signalStrengthEVDOECIOList.add(deviceInfo
									.getSignalStrengthEVDOECIO());
							signalStrengthSnrList.add(deviceInfo
									.getSignalStrengthSnr());
							signalStrengthCDMACIOList.add("null");
							signalStrengthLTECQIList.add("null");
							signalStrengthLTERSSNRList.add("null");
							signalStrengthLTERSRQList.add("null");
							signalStrengthLTERSRPList.add("null");
						} else {
							signalStrengthList.add(deviceInfo
									.getSignalStrengthEVDO());
							signalStrengthLt.add(deviceInfo
									.getSignalStrengthEVDO());
							signalStrengthEVDOECIOList.add(deviceInfo
									.getSignalStrengthEVDOECIO());
							signalStrengthSnrList.add(deviceInfo
									.getSignalStrengthSnr());
							signalStrengthCDMACIOList.add("null");
							signalStrengthLTECQIList.add("null");
							signalStrengthLTERSSNRList.add("null");
							signalStrengthLTERSRQList.add("null");
							signalStrengthLTERSRPList.add("null");
						}
						signalStrengthListRating.add(deviceInfo
								.getSignalStrengthEVDO());
					} else if (networkName.equalsIgnoreCase("LTE (4G)")) {
						if (deviceInfo.getSignalStrengthLTE().equals("Empty")
								|| deviceInfo.getSignalStrengthLTE().equals("")) {
							int signalStrengthLTE = Integer.parseInt("0");
							int signalStrengthLTEValue = signalStrengthLTE;
							signalStrengthList.add(String
									.valueOf(signalStrengthLTEValue + "dBm."));
							int signalStrengthLTValue = signalStrengthLTE;
							signalStrengthLt
									.add(String
											.valueOf(
													deviceInfo
															.getSignalStrengthLTERSRP())
											.substring(
													1,
													String
															.valueOf(
																	deviceInfo
																			.getSignalStrengthLTERSRP())
															.length()));
							signalStrengthLTERSRPList.add(deviceInfo
									.getSignalStrengthLTERSRP());
							signalStrengthLTERSRQList.add(deviceInfo
									.getSignalStrengthLTERSRQ());
							signalStrengthLTERSSNRList.add(deviceInfo
									.getSignalStrengthLTERSSNR());
							signalStrengthLTECQIList.add(deviceInfo
									.getSignalStrengthLTECQI());
							signalStrengthCDMACIOList.add("null");
							signalStrengthEVDOECIOList.add("null");
							signalStrengthSnrList.add("null");
						} else {
							int signalStrengthLTE = Integer.parseInt(deviceInfo
									.getSignalStrengthLTE());
							int signalStrengthLTEValue = -140
									+ signalStrengthLTE;
							signalStrengthList.add(String
									.valueOf(signalStrengthLTEValue + "dBm."));
							int signalStrengthLTValue = signalStrengthLTE;
							// signalStrengthLt.add(String.valueOf(signalStrengthLTValue).substring(1,
							// String.valueOf(signalStrengthLTValue).length()));
							signalStrengthLt
									.add(String
											.valueOf(
													deviceInfo
															.getSignalStrengthLTERSRP())
											.substring(
													1,
													String
															.valueOf(
																	deviceInfo
																			.getSignalStrengthLTERSRP())
															.length()));
							signalStrengthLTERSRPList.add(deviceInfo
									.getSignalStrengthLTERSRP());
							signalStrengthLTERSRQList.add(deviceInfo
									.getSignalStrengthLTERSRQ());
							signalStrengthLTERSSNRList.add(deviceInfo
									.getSignalStrengthLTERSSNR());
							signalStrengthLTECQIList.add(deviceInfo
									.getSignalStrengthLTECQI());
							signalStrengthCDMACIOList.add("null");
							signalStrengthEVDOECIOList.add("null");
							signalStrengthSnrList.add("null");
						}
						signalStrengthListRating.add(deviceInfo
								.getSignalStrengthLTE());
					} else {
						if (deviceInfo.getSignalStrength().equals("Empty")
								|| deviceInfo.getSignalStrength().equals("")) {
							int signalStrengthGSM = Integer.parseInt("0");
							int signalStrengthGSMValue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthList.add(String
									.valueOf(signalStrengthGSMValue + "dBm."));
							int signalStrengthLtvalue = Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthLt.add(String.valueOf(
									signalStrengthLtvalue).substring(
									1,
									String.valueOf(signalStrengthLtvalue)
											.length()));
							signalStrengthCDMACIOList.add("null");
							signalStrengthEVDOECIOList.add("null");
							signalStrengthSnrList.add("null");
							signalStrengthLTECQIList.add("null");
							signalStrengthLTERSSNRList.add("null");
							signalStrengthLTERSRQList.add("null");
							signalStrengthLTERSRPList.add("null");
						} else {
							int signalStrengthGSM = Integer.parseInt(deviceInfo
									.getSignalStrength());
							int signalStrengthGSMValue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthList.add(String
									.valueOf(signalStrengthGSMValue + "dBm."));
							int signalStrengthLtvalue = -Integer
									.parseInt(SIGNALSTRENGTH_GSM)
									+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
									* signalStrengthGSM;
							signalStrengthLt.add(String.valueOf(
									signalStrengthLtvalue).substring(
									1,
									String.valueOf(signalStrengthLtvalue)
											.length()));
							signalStrengthCDMACIOList.add("null");
							signalStrengthEVDOECIOList.add("null");
							signalStrengthSnrList.add("null");
							signalStrengthLTECQIList.add("null");
							signalStrengthLTERSSNRList.add("null");
							signalStrengthLTERSRQList.add("null");
							signalStrengthLTERSRPList.add("null");
						}
						signalStrengthListRating.add(deviceInfo
								.getSignalStrength());
					}
					network = deviceInfo.getNetworkType().trim();
					networkTypeList.add(network);
					timeStampForEachSampleList.add("<b>Test Times:</b>"
							+ deviceInfo.getTimeStampForEachSample());
					timeStampForSampleList.add(deviceInfo
							.getTimeStampForEachSample());

				}

				if (deviceInfoCallDropList.size() > 0) {
					for (int j = 0; j < deviceInfoCallDropList.size(); j++) {
						callDrop = callDrop + ","
								+ deviceInfoCallDropList.get(j);
					}
				}

				if (deviceInfoThroughputList.size() > 0) {
					for (int k = 0; k < deviceInfoThroughputList.size(); k++) {
						througput = througput + ","
								+ deviceInfoThroughputList.get(k);
					}
				}

				if (deviceInfoThroughput.size() > 0) {
					for (int k = 0; k < deviceInfoThroughput.size(); k++) {
						througputTimestamp = througputTimestamp + ","
								+ deviceInfoThroughput.get(k);
					}
				}

				latitudes = lattitudeList.toString();
				longitudes = longitudeList.toString();
				testName = testNameList.toString();
				imei = imeiList.toString();
				signalStrength = signalStrengthList.toString();
				signalStrengthLTT = signalStrengthLt.toString();
				signalStrengthRating = signalStrengthListRating.toString();
				networkType = networkTypeList.toString();
				timeStampForEachSample = timeStampForEachSampleList.toString();
				timeStampForSample = timeStampForSampleList.toString();
				signalStrengthCDMACIO = signalStrengthCDMACIOList.toString();
				signalStrengthEVDOECIO = signalStrengthEVDOECIOList.toString();
				signalStrengthLTERSRP = signalStrengthLTERSRPList.toString();
				signalStrengthLTERSRQ = signalStrengthLTERSRQList.toString();
				signalStrengthLTERSSNR = signalStrengthLTERSSNRList.toString();
				signalStrengthLTECQI = signalStrengthLTECQIList.toString();
				cellLocationCid = cellLocationCidList.toString();

				removeSession(context);
				context.getExternalContext().getSessionMap().put(
						"througputTimestamp",
						througputTimestamp.substring(1, througputTimestamp
								.length() - 1));
				context.getExternalContext().getSessionMap().put("througput",
						througput.substring(1, througput.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"VoiceandData", selectedDevice);
				context.getExternalContext().getSessionMap().put(
						"mapReportType", mapReportType);
				context.getExternalContext().getSessionMap().put("lattitudes",
						latitudes.substring(1, latitudes.length() - 1));
				context.getExternalContext().getSessionMap().put("longitudes",
						longitudes.substring(1, longitudes.length() - 1));
				context.getExternalContext().getSessionMap().put("testName",
						testName.substring(1, testName.length() - 1));
				
				context.getExternalContext().getSessionMap().put("Imei",
						imei.substring(1, imei.length() - 1));
				
				context.getExternalContext().getSessionMap().put(
						"signalStrength",
						signalStrength
								.substring(1, signalStrength.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthLTT",
						signalStrengthLTT.substring(1, signalStrengthLTT
								.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthRating",
						signalStrengthRating.substring(1, signalStrengthRating
								.length() - 1));
				context.getExternalContext().getSessionMap().put("networkType",
						networkType.substring(1, networkType.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"timeStampForEachSample",
						timeStampForEachSample.substring(1,
								timeStampForEachSample.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"timeStampForSample",
						timeStampForSample.substring(1, timeStampForSample
								.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthCDMACIO",
						signalStrengthCDMACIO.substring(1,
								signalStrengthCDMACIO.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthEVDOECIO",
						signalStrengthEVDOECIO.substring(1,
								signalStrengthEVDOECIO.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthLTERSRP",
						signalStrengthLTERSRP.substring(1,
								signalStrengthLTERSRP.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthLTERSRQ",
						signalStrengthLTERSRQ.substring(1,
								signalStrengthLTERSRQ.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthLTERSSNR",
						signalStrengthLTERSSNR.substring(1,
								signalStrengthLTERSSNR.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"signalStrengthLTECQI",
						signalStrengthLTECQI.substring(1, signalStrengthLTECQI
								.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"cellLocationCID",
						cellLocationCid.substring(1,
								cellLocationCid.length() - 1));

				if (deviceInfoCallDropList.size() > 0) {
					context.getExternalContext().getSessionMap().put(
							"deviceInfoCallDropList",
							callDrop.substring(1, callDrop.length()));
				} else {
					context.getExternalContext().getSessionMap().put(
							"deviceInfoCallDropList", callDrop);
				}
			}
			return "SUCCESS";
		}
		context.getExternalContext().getSessionMap().put("isQxdmPresent",
				isQxdmPresent);
		return "mapGoogleDataSuccess";
	}


	public static void generateGoogleVoiceMap(List<FileHistoryTO> fileHistory,
			int count) {
		FacesContext context = FacesContext.getCurrentInstance();
		String test_name = "";
		String test_type = "";
		String market_id = "";
		String marketName = "";
		String devicemodel = "";
		String file_name = "";
		String testtypevalue = null;
		String test_typeValue = null;
		String testname[] = null;
		String testtype[] = null;
		String marketid[] = null;
		String device_model[] = null;
		List<FileHistoryTO> filelist = new ArrayList<FileHistoryTO>();
		for (int i = 0; i < fileHistory.size(); i++) {
			FileHistoryTO fileHistoryTO = fileHistory.get(i);
			if (test_name == "") {
				test_name = fileHistoryTO.getTest_name();
				test_type = fileHistoryTO.getTest_type();
				market_id = fileHistoryTO.getMarket_Id();
				devicemodel = fileHistoryTO.getDevice_model();
				file_name = fileHistoryTO.getFile_name();
			} else {
				test_name = test_name + "," + fileHistoryTO.getTest_name();
				test_type = test_type + "," + fileHistoryTO.getTest_type();
				market_id = market_id + "," + fileHistoryTO.getMarket_Id();
				devicemodel = devicemodel + ","
						+ fileHistoryTO.getDevice_model();
				file_name = file_name + "," + fileHistoryTO.getFile_name();
			}
		}
		testname = test_name.split(",");
		List<String> tmpList = Arrays.asList(testname);
		TreeSet<String> Uniquetest_name = new TreeSet<String>(tmpList);
		testtype = test_type.split(",");
		List<String> tmpList1 = Arrays.asList(testtype);
		TreeSet<String> Uniquetest_type = new TreeSet<String>(tmpList1);
		marketid = market_id.split(",");
		List<String> tmpList2 = Arrays.asList(market_id);
		TreeSet<String> Uniquemarket_id = new TreeSet<String>(tmpList2);
		device_model = devicemodel.split(",");
		List<String> tmpList3 = Arrays.asList(device_model);
		TreeSet<String> Uniquedevicemodel = new TreeSet<String>(tmpList3);
		List<String> tmpList4 = Arrays.asList(file_name);
		TreeSet<String> Uniquefile_name = new TreeSet<String>(tmpList4);
		ReportDao reportDao = new ReportDaoImpl();
		marketName = reportDao.getMarketNameList(getFormatStr(Uniquemarket_id));

		if (getFormatStr(Uniquetest_type).contains("VQTResults")) {
			context.getExternalContext().getSessionMap().remove(
					"reportSelectionType");
			context.getExternalContext().getSessionMap().put(
					"reportSelectionType", "VoiceQuality");
			generateVoiceQualityMap(getFormatStr(Uniquetest_name),
					getFormatStr(Uniquetest_type),
					getFormatStr(Uniquemarket_id),
					getFormatStr(Uniquedevicemodel),
					getFormatStr(Uniquefile_name), marketName, count);
		}
		if (getFormatStr(Uniquetest_type).contains("mo")) {
			context.getExternalContext().getSessionMap().remove(
					"reportSelectionType");
			context.getExternalContext().getSessionMap().put(
					"reportSelectionType", "VoiceQuality");
			if (getFormatStr(Uniquetest_type).contains("mo")) {
				testtypevalue = "'mo'";
				test_typeValue = "mo";
			}
			generateVoiceConnectivityMap(getFormatStr(Uniquetest_name),
					testtypevalue, getFormatStr(Uniquemarket_id),
					getFormatStr(Uniquedevicemodel),
					getFormatStr(Uniquefile_name), marketName, test_typeValue,
					count);
		}
		if (getFormatStr(Uniquetest_type).contains("ftp")) {
			context.getExternalContext().getSessionMap().remove(
					"reportSelectionType");
			context.getExternalContext().getSessionMap().put(
					"reportSelectionType", "Data");
			generateDataConnectivityMap(getFormatStr(Uniquetest_name), "ftp",
					getFormatStr(Uniquemarket_id),
					getFormatStr(Uniquedevicemodel),
					getFormatStr(Uniquefile_name), marketName, count);
		}
		if (getFormatStr(Uniquetest_type).contains("externaltest")) {
			context.getExternalContext().getSessionMap().remove(
					"reportSelectionType");
			context.getExternalContext().getSessionMap().put(
					"reportSelectionType", "Data");
			generateSignalStrenghtVoice_DataMap(getFormatStr(Uniquetest_name),
					"externaltest", getFormatStr(Uniquemarket_id),
					getFormatStr(Uniquedevicemodel),
					getFormatStr(Uniquefile_name), marketName, count);
		}
	}

	public static String generateSignalStrenghtVoice_DataMap(String test_name,
			String test_type, String market_id, String device_model,
			String filename, String marketName, int count) {
		FacesContext context = FacesContext.getCurrentInstance();
		String selectedDevice = null;
		UserDao userDao = new UserDaoImpl();
		Voice_DataDao connectivityDao = new Voice_DataDaoImpl();
		ReportDao dao = new ReportDaoImpl();
		List<Voice_DataTO> deviceInfoList = null;
		List<String> timestamp = dao.getMinandMaxTimestampforNetCall(test_name,
				market_id);
		selectedDevice = test_name + "_" + marketName + "_" + test_type;
		if (timestamp.size() < 0) {
			context.getExternalContext().getRequestMap().remove("datamessage");
			context.getExternalContext().getRequestMap().put("datamessage",
					"No Data Found for the Selected Test Name");
			return "mapDataValidation";
		}
		if (test_type.equals("externaltest")) {
			deviceInfoList = connectivityDao.getAllDeviceInfo(test_name,
					market_id, test_type, timestamp);
		} else {
			context.getExternalContext().getRequestMap().remove("datamessage");
			context.getExternalContext().getRequestMap().put("datamessage",
					"No Data Found for the Selected Test Name");
			return "mapDataValidation";
		}
		List<String> deviceInfoCallDropList = connectivityDao
				.getExternal_CallDropList(test_name, market_id);
		List<String> deviceInfoThroughputList = connectivityDao
				.getExternal_througputList(test_name, market_id);
		List<String> deviceInfoThroughput = connectivityDao
				.getExternal_througput(test_name, market_id);
		if (deviceInfoCallDropList.size() < 0
				&& deviceInfoThroughputList.size() < 0) {
			context.getExternalContext().getRequestMap().remove("datamessage");
			context.getExternalContext().getRequestMap().put("datamessage",
					"No Data Found for the Selected Test Name");
			return "mapDataValidation";
		}

		if (deviceInfoList.size() <= 0) {
			context.getExternalContext().getRequestMap().remove("datamessage");
			context.getExternalContext().getRequestMap().put("datamessage",
					"No Data Found for the Selected Test Name");
			return "mapDataValidation";
		} else {
			String latitudes = "";
			String longitudes = "";
			String testName = "";
			String signalStrength = "";
			String signalStrengthLTT = "";
			String signalStrengthRating = "";
			String networkType = "";
			String cellLocationCid = "";
			String timeStampForEachSample = "";
			String timeStampForSample = "";
			String neighbourInfo = "";
			String neighbourMainString = "";
			String signalStrengthCDMACIO = "";
			String signalStrengthEVDOECIO = "";

			String signalStrengthLTERSRP = "";
			String signalStrengthLTERSRQ = "";
			String signalStrengthLTERSSNR = "";
			String signalStrengthLTECQI = "";
			String callDrop = "";
			String network = "";
			String througput = "";
			String througputTimestamp = "";

			// String details = "";
			List lattitudeList = new ArrayList();
			List longitudeList = new ArrayList();
			List testNameList = new ArrayList();
			List callDropStatusList = new ArrayList();
			List signalStrengthList = new ArrayList();
			List signalStrengthListRating = new ArrayList();
			List signalStrengthLt = new ArrayList();
			List networkTypeList = new ArrayList();
			List cellLocationCidList = new ArrayList();
			List signalStrengthSnrList = new ArrayList();
			List timeStampForEachSampleList = new ArrayList();
			List timeStampForSampleList = new ArrayList();
			List signalStrengthCDMACIOList = new ArrayList();
			List signalStrengthEVDOECIOList = new ArrayList();
			List signalStrengthLTERSRPList = new ArrayList();
			List signalStrengthLTERSRQList = new ArrayList();
			List signalStrengthLTERSSNRList = new ArrayList();
			List signalStrengthLTECQIList = new ArrayList();

			List neighbourMainList = new ArrayList();
			String neighbourtStr = null;
			String networkName = "";

			for (int i = 0; i < deviceInfoList.size(); i++) {
				Voice_DataTO deviceInfo = deviceInfoList.get(i);
				networkName = deviceInfo.getNetworkType();
				lattitudeList.add(deviceInfo.getLattitude());
				longitudeList.add(deviceInfo.getLongitude());
				testNameList.add(deviceInfo.getTestName());
				cellLocationCidList.add(deviceInfo.getCellLocationCID());

				if (networkName.matches("GSM")) {
					if (deviceInfo.getSignalStrength().equals("Empty")
							|| deviceInfo.getSignalStrength().equals("")) {
						int signalStrengthGSM = Integer.parseInt("0");
						int signalStrengthGSMValue = -Integer
								.parseInt(SIGNALSTRENGTH_GSM)
								+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
								* signalStrengthGSM;
						signalStrengthList.add(String
								.valueOf(signalStrengthGSMValue + "dBm."));
						int signalStrengthLtvalue = -Integer
								.parseInt(SIGNALSTRENGTH_GSM)
								+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
								* signalStrengthGSM;
						signalStrengthLt
								.add(String.valueOf(signalStrengthLtvalue)
										.substring(
												1,
												String.valueOf(
														signalStrengthLtvalue)
														.length()));
						signalStrengthCDMACIOList.add("null");
						signalStrengthEVDOECIOList.add("null");
						signalStrengthSnrList.add("null");
						signalStrengthLTECQIList.add("null");
						signalStrengthLTERSSNRList.add("null");
						signalStrengthLTERSRQList.add("null");
						signalStrengthLTERSRPList.add("null");
					} else {
						int signalStrengthGSM = Integer.parseInt(deviceInfo
								.getSignalStrength());
						int signalStrengthGSMValue = -Integer
								.parseInt(SIGNALSTRENGTH_GSM)
								+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
								* signalStrengthGSM;
						signalStrengthList.add(String
								.valueOf(signalStrengthGSMValue + "dBm."));
						int signalStrengthLtvalue = -Integer
								.parseInt(SIGNALSTRENGTH_GSM)
								+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
								* signalStrengthGSM;
						signalStrengthLt
								.add(String.valueOf(signalStrengthLtvalue)
										.substring(
												1,
												String.valueOf(
														signalStrengthLtvalue)
														.length()));
						signalStrengthCDMACIOList.add("null");
						signalStrengthEVDOECIOList.add("null");
						signalStrengthSnrList.add("null");
						signalStrengthLTECQIList.add("null");
						signalStrengthLTERSSNRList.add("null");
						signalStrengthLTERSRQList.add("null");
						signalStrengthLTERSRPList.add("null");
					}
					signalStrengthListRating
							.add(deviceInfo.getSignalStrength());
				} else if (networkName.matches("CDMA")) {
					if (deviceInfo.getSignalStrengthCDMA().equals("Empty")
							|| deviceInfo.getSignalStrengthCDMA().equals("")) {
						signalStrengthList.add("0");
						signalStrengthLt.add("0");
						signalStrengthCDMACIOList.add(deviceInfo
								.getSignalStrengthCDMACIO());
						signalStrengthEVDOECIOList.add("null");
						signalStrengthSnrList.add("null");
						signalStrengthLTECQIList.add("null");
						signalStrengthLTERSSNRList.add("null");
						signalStrengthLTERSRQList.add("null");
						signalStrengthLTERSRPList.add("null");
					} else {
						signalStrengthList.add(deviceInfo
								.getSignalStrengthCDMA());
						signalStrengthLt
								.add(deviceInfo.getSignalStrengthCDMA());
						signalStrengthCDMACIOList.add(deviceInfo
								.getSignalStrengthCDMACIO());
						signalStrengthEVDOECIOList.add("null");
						signalStrengthSnrList.add("null");
						signalStrengthLTECQIList.add("null");
						signalStrengthLTERSSNRList.add("null");
						signalStrengthLTERSRQList.add("null");
						signalStrengthLTERSRPList.add("null");
					}
					signalStrengthListRating.add(deviceInfo
							.getSignalStrengthCDMA());
				} else if (networkName.matches("EVDO") || networkName.matches("EVDO-A") || networkName.matches("EVDO-B")
						|| networkName.matches("eHRPD") || networkName.matches("EHRPD (3G)")) {
					if (deviceInfo.getSignalStrengthEVDO().equals("Empty")
							|| deviceInfo.getSignalStrengthEVDO().equals("")) {
						signalStrengthList.add("0");
						signalStrengthLt.add("0");
						signalStrengthEVDOECIOList.add(deviceInfo
								.getSignalStrengthEVDOECIO());
						signalStrengthSnrList.add(deviceInfo
								.getSignalStrengthSnr());
						signalStrengthCDMACIOList.add("null");
						signalStrengthLTECQIList.add("null");
						signalStrengthLTERSSNRList.add("null");
						signalStrengthLTERSRQList.add("null");
						signalStrengthLTERSRPList.add("null");
					} else {
						signalStrengthList.add(deviceInfo
								.getSignalStrengthEVDO());
						signalStrengthLt
								.add(deviceInfo.getSignalStrengthEVDO());
						signalStrengthEVDOECIOList.add(deviceInfo
								.getSignalStrengthEVDOECIO());
						signalStrengthSnrList.add(deviceInfo
								.getSignalStrengthSnr());
						signalStrengthCDMACIOList.add("null");
						signalStrengthLTECQIList.add("null");
						signalStrengthLTERSSNRList.add("null");
						signalStrengthLTERSRQList.add("null");
						signalStrengthLTERSRPList.add("null");
					}
					signalStrengthListRating.add(deviceInfo
							.getSignalStrengthEVDO());
				} else if (networkName.equalsIgnoreCase("LTE (4G)")) {
					if (deviceInfo.getSignalStrengthLTE().equals("Empty")
							|| deviceInfo.getSignalStrengthLTE().equals("")) {
						int signalStrengthLTE = Integer.parseInt("0");
						int signalStrengthLTEValue = signalStrengthLTE;
						signalStrengthList.add(String
								.valueOf(signalStrengthLTEValue + "dBm."));
						int signalStrengthLTValue = signalStrengthLTE;
						signalStrengthLt
								.add(String
										.valueOf(
												deviceInfo
														.getSignalStrengthLTERSRP())
										.substring(
												1,
												String
														.valueOf(
																deviceInfo
																		.getSignalStrengthLTERSRP())
														.length()));
						signalStrengthLTERSRPList.add(deviceInfo
								.getSignalStrengthLTERSRP());
						signalStrengthLTERSRQList.add(deviceInfo
								.getSignalStrengthLTERSRQ());
						signalStrengthLTERSSNRList.add(deviceInfo
								.getSignalStrengthLTERSSNR());
						signalStrengthLTECQIList.add(deviceInfo
								.getSignalStrengthLTECQI());
						signalStrengthCDMACIOList.add("null");
						signalStrengthEVDOECIOList.add("null");
						signalStrengthSnrList.add("null");
					} else {
						int signalStrengthLTE = Integer.parseInt(deviceInfo
								.getSignalStrengthLTE());
						int signalStrengthLTEValue = -140 + signalStrengthLTE;
						signalStrengthList.add(String
								.valueOf(signalStrengthLTEValue + "dBm."));
						int signalStrengthLTValue = signalStrengthLTE;
						// signalStrengthLt.add(String.valueOf(signalStrengthLTValue).substring(1,
						// String.valueOf(signalStrengthLTValue).length()));
						signalStrengthLt
								.add(String
										.valueOf(
												deviceInfo
														.getSignalStrengthLTERSRP())
										.substring(
												1,
												String
														.valueOf(
																deviceInfo
																		.getSignalStrengthLTERSRP())
														.length()));
						signalStrengthLTERSRPList.add(deviceInfo
								.getSignalStrengthLTERSRP());
						signalStrengthLTERSRQList.add(deviceInfo
								.getSignalStrengthLTERSRQ());
						signalStrengthLTERSSNRList.add(deviceInfo
								.getSignalStrengthLTERSSNR());
						signalStrengthLTECQIList.add(deviceInfo
								.getSignalStrengthLTECQI());
						signalStrengthCDMACIOList.add("null");
						signalStrengthEVDOECIOList.add("null");
						signalStrengthSnrList.add("null");
					}
					signalStrengthListRating.add(deviceInfo
							.getSignalStrengthLTE());
				} else {
					if (deviceInfo.getSignalStrength().equals("Empty")
							|| deviceInfo.getSignalStrength().equals("")) {
						int signalStrengthGSM = Integer.parseInt("0");
						int signalStrengthGSMValue = -Integer
								.parseInt(SIGNALSTRENGTH_GSM)
								+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
								* signalStrengthGSM;
						signalStrengthList.add(String
								.valueOf(signalStrengthGSMValue + "dBm."));
						int signalStrengthLtvalue = Integer
								.parseInt(SIGNALSTRENGTH_GSM)
								+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
								* signalStrengthGSM;
						signalStrengthLt
								.add(String.valueOf(signalStrengthLtvalue)
										.substring(
												1,
												String.valueOf(
														signalStrengthLtvalue)
														.length()));
						signalStrengthCDMACIOList.add("null");
						signalStrengthEVDOECIOList.add("null");
						signalStrengthSnrList.add("null");
						signalStrengthLTECQIList.add("null");
						signalStrengthLTERSSNRList.add("null");
						signalStrengthLTERSRQList.add("null");
						signalStrengthLTERSRPList.add("null");
					} else {
						int signalStrengthGSM = Integer.parseInt(deviceInfo
								.getSignalStrength());
						int signalStrengthGSMValue = -Integer
								.parseInt(SIGNALSTRENGTH_GSM)
								+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
								* signalStrengthGSM;
						signalStrengthList.add(String
								.valueOf(signalStrengthGSMValue + "dBm."));
						int signalStrengthLtvalue = -Integer
								.parseInt(SIGNALSTRENGTH_GSM)
								+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
								* signalStrengthGSM;
						signalStrengthLt
								.add(String.valueOf(signalStrengthLtvalue)
										.substring(
												1,
												String.valueOf(
														signalStrengthLtvalue)
														.length()));
						signalStrengthCDMACIOList.add("null");
						signalStrengthEVDOECIOList.add("null");
						signalStrengthSnrList.add("null");
						signalStrengthLTECQIList.add("null");
						signalStrengthLTERSSNRList.add("null");
						signalStrengthLTERSRQList.add("null");
						signalStrengthLTERSRPList.add("null");
					}
					signalStrengthListRating
							.add(deviceInfo.getSignalStrength());
				}
				network = deviceInfo.getNetworkType().trim();
				networkTypeList.add(network);
				timeStampForEachSampleList.add("<b>Test Times:</b>"
						+ deviceInfo.getTimeStampForEachSample());
				timeStampForSampleList.add(deviceInfo
						.getTimeStampForEachSample());

			}

			if (deviceInfoCallDropList.size() > 0) {
				for (int j = 0; j < deviceInfoCallDropList.size(); j++) {
					callDrop = callDrop + "," + deviceInfoCallDropList.get(j);
				}
			}
			// logger.info("deviceInfoThroughputList-----------"+deviceInfoThroughputList);
			if (deviceInfoThroughputList.size() > 0) {
				for (int k = 0; k < deviceInfoThroughputList.size(); k++) {
					througput = througput + ","
							+ deviceInfoThroughputList.get(k);
				}
			}

			if (deviceInfoThroughput.size() > 0) {
				for (int k = 0; k < deviceInfoThroughput.size(); k++) {
					througputTimestamp = througputTimestamp + ","
							+ deviceInfoThroughput.get(k);
				}
			}

			latitudes = lattitudeList.toString();
			longitudes = longitudeList.toString();
			testName = testNameList.toString();
			signalStrength = signalStrengthList.toString();
			signalStrengthLTT = signalStrengthLt.toString();
			signalStrengthRating = signalStrengthListRating.toString();
			networkType = networkTypeList.toString();
			timeStampForEachSample = timeStampForEachSampleList.toString();
			timeStampForSample = timeStampForSampleList.toString();
			signalStrengthCDMACIO = signalStrengthCDMACIOList.toString();
			signalStrengthEVDOECIO = signalStrengthEVDOECIOList.toString();
			signalStrengthLTERSRP = signalStrengthLTERSRPList.toString();
			signalStrengthLTERSRQ = signalStrengthLTERSRQList.toString();
			signalStrengthLTERSSNR = signalStrengthLTERSSNRList.toString();
			signalStrengthLTECQI = signalStrengthLTECQIList.toString();
			cellLocationCid = cellLocationCidList.toString();
			// logger.info();
			// removeVoiceDataSession(context,count);
			context.getExternalContext().getSessionMap().put(
					"VoiceandData" + count, selectedDevice);
			context.getExternalContext().getSessionMap().put(
					"VDlattitudes" + count,
					latitudes.substring(1, latitudes.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"VDlongitudes" + count,
					longitudes.substring(1, longitudes.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"VDtestName" + count,
					testName.substring(1, testName.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"VDsignalStrength" + count,
					signalStrength.substring(1, signalStrength.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"VDsignalStrengthLTT" + count,
					signalStrengthLTT.substring(1,
							signalStrengthLTT.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"VDsignalStrengthRating" + count,
					signalStrengthRating.substring(1, signalStrengthRating
							.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"VDnetworkType" + count,
					networkType.substring(1, networkType.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"VDtimeStampForEachSample" + count,
					timeStampForEachSample.substring(1, timeStampForEachSample
							.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"VDtimeStampForSample" + count,
					timeStampForSample.substring(1,
							timeStampForSample.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"VDsignalStrengthCDMACIO" + count,
					signalStrengthCDMACIO.substring(1, signalStrengthCDMACIO
							.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"VDsignalStrengthEVDOECIO" + count,
					signalStrengthEVDOECIO.substring(1, signalStrengthEVDOECIO
							.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"VDsignalStrengthLTERSRP" + count,
					signalStrengthLTERSRP.substring(1, signalStrengthLTERSRP
							.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"VDsignalStrengthLTERSRQ" + count,
					signalStrengthLTERSRQ.substring(1, signalStrengthLTERSRQ
							.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"VDsignalStrengthLTERSSNR" + count,
					signalStrengthLTERSSNR.substring(1, signalStrengthLTERSSNR
							.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"VDsignalStrengthLTECQI" + count,
					signalStrengthLTECQI.substring(1, signalStrengthLTECQI
							.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"VDcellLocationCID" + count,
					cellLocationCid.substring(1, cellLocationCid.length() - 1));

			if (deviceInfoThroughput.size() > 0) {
				String[] througputTimestampArr = througputTimestamp.substring(
						1, througputTimestamp.length() - 1).split(",");
				context.getExternalContext().getSessionMap().put(
						"VDthrougputTimestamp" + count,
						througputTimestamp.substring(1, througputTimestamp
								.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"VDthrougput" + count,
						througput.substring(1, througput.length() - 1));
			} else {
				context.getExternalContext().getSessionMap().put(
						"VDthrougputTimestamp" + count, througputTimestamp);
				context.getExternalContext().getSessionMap().put(
						"VDthrougput" + count, througput);
			}

			if (deviceInfoCallDropList.size() > 0) {
				context.getExternalContext().getSessionMap().put(
						"VDdeviceInfoCallDropList" + count,
						callDrop.substring(1, callDrop.length()));
			} else {
				context.getExternalContext().getSessionMap().put(
						"VDdeviceInfoCallDropList" + count, callDrop);
			}
		}
		return "SUCCESS";
	}

	public static String generateDataConnectivityMap(String test_name,
			String test_type, String market_id, String device_model,
			String filename, String marketName, int count) {
		try{
		FacesContext context = FacesContext.getCurrentInstance();
		DataConnectivityDao dataConnectivityDao = new DataConnectivityDaoImpl();
		boolean status = false;
		String selectedDevice = null;
		selectedDevice = test_name + "_" + marketName + "_" + test_type;
		List<DeviceInfoTO> ThroughputDeviceresults = new ArrayList<DeviceInfoTO>();
		// srikanth
		// ThroughputDeviceresults=dataConnectivityDao.getTestNameThroughputDetailsResults(test_name,market_id);
		FTPReportHelper ftpReportHelper = new FTPReportHelper();
		ThroughputDeviceresults = ftpReportHelper.getThroughputForMaps(
				test_name, "Current TX bytes", market_id);// getTestNameThroughputDetailsResults(testCaseName,marketmapId,test_type);
		ThroughputDeviceresults.addAll(ftpReportHelper.getThroughputForMaps(
				test_name, "Current RX bytes", market_id));
		List<DeviceInfoTO> allFtpPoints = new UserDaoImpl().getAllDeviceInfo(
				test_name, market_id, test_type, "");
		if (ThroughputDeviceresults.size() <= 0) {
			context.getExternalContext().getRequestMap().remove("datamessage");
			context.getExternalContext().getRequestMap().put("datamessage",
					"No Data Found for the Selected Test Name");
			return "mapDataValidation";
		} else {
			String latitudes = "";
			String longitudes = "";
			String testName = "";
			String signalStrength = "";
			String signalStrengthRating = "";
			String networkType = "";
			String cellLocationCID = "";
			String timeStampForEachSample = "";
			String eventName = "";
			String eventValue = "";
			String throughputTX = "";
			String throughputRX = "";
			String throughputMain = "";
			String currTxBytes = "";
			String prevTxBytes = "";
			String currRxBytes = "";
			String prevRxBytes = "";
			// String throughput="";
			String signalStrengthLTT = "";
			String network = "";
			List lattitudeList = new ArrayList();
			List longitudeList = new ArrayList();
			List testNameList = new ArrayList();
			List signalStrengthList = new ArrayList();
			List networkTypeList = new ArrayList();
			List cellLocationList = new ArrayList();
			List timeStampForEachSampleList = new ArrayList();
			List throughputList = new ArrayList();
			List signalStrengthListRating = new ArrayList();
			List signalStrengthLt = new ArrayList();
			String networkName = "";
			for (int i = 0; i < allFtpPoints.size(); i++) {
				DeviceInfoTO deviceInfo = allFtpPoints.get(i);
				networkName = deviceInfo.getNetworkType();
				lattitudeList.add(deviceInfo.getLattitude());
				longitudeList.add(deviceInfo.getLongitude());
				testNameList.add(deviceInfo.getTestName());
				network = deviceInfo.getNetworkType().trim();
				networkTypeList.add(network);
				cellLocationList.add(deviceInfo.getCellLocationCID());
				timeStampForEachSampleList.add(deviceInfo
						.getTimeStampForEachSample());
				eventName = deviceInfo.getEventName();
				if (networkName.matches("GSM")) {
					if (deviceInfo.getSignalStrength().equals("Empty")
							|| deviceInfo.getSignalStrength().equals("")) {
						int signalStrengthGSM = Integer.parseInt("0");
						int signalStrengthGSMValue = -Integer
								.parseInt(SIGNALSTRENGTH_GSM)
								+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
								* signalStrengthGSM;
						signalStrengthList.add(String
								.valueOf(signalStrengthGSMValue + "dBm."));
						int signalStrengthLtvalue = -Integer
								.parseInt(SIGNALSTRENGTH_GSM)
								+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
								* signalStrengthGSM;
						signalStrengthLt
								.add(String.valueOf(signalStrengthLtvalue)
										.substring(
												1,
												String.valueOf(
														signalStrengthLtvalue)
														.length()));
					} else {
						int signalStrengthGSM = Integer.parseInt(deviceInfo
								.getSignalStrength());
						int signalStrengthGSMValue = -Integer
								.parseInt(SIGNALSTRENGTH_GSM)
								+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
								* signalStrengthGSM;
						signalStrengthList.add(String
								.valueOf(signalStrengthGSMValue + "dBm."));
						int signalStrengthLtvalue = -Integer
								.parseInt(SIGNALSTRENGTH_GSM)
								+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
								* signalStrengthGSM;
						signalStrengthLt
								.add(String.valueOf(signalStrengthLtvalue)
										.substring(
												1,
												String.valueOf(
														signalStrengthLtvalue)
														.length()));
					}
					signalStrengthListRating
							.add(deviceInfo.getSignalStrength());
				} else if (networkName.matches("CDMA")) {
					if (deviceInfo.getSignalStrengthCDMA().equals("Empty")
							|| deviceInfo.getSignalStrengthCDMA().equals("")) {
						signalStrengthList.add("0");
						signalStrengthLt.add("0");
					} else {
						signalStrengthList.add(deviceInfo
								.getSignalStrengthCDMA());
						signalStrengthLt
								.add(deviceInfo.getSignalStrengthCDMA());
					}
					signalStrengthListRating.add(deviceInfo
							.getSignalStrengthCDMA());
				} else if (networkName.matches("EVDO") || networkName.matches("eHRPD") || networkName.matches("EHRPD (3G)")
						|| networkName.matches("EVDO-A") || networkName.matches("EVDO-B")) {
					if (deviceInfo.getSignalStrengthEVDO().equals("Empty")
							|| deviceInfo.getSignalStrengthEVDO().equals("")) {
						signalStrengthList.add("0");
						signalStrengthLt.add("0");
					} else {
						signalStrengthLt
								.add(deviceInfo.getSignalStrengthEVDO());
						signalStrengthList.add(deviceInfo
								.getSignalStrengthEVDO());
					}
					signalStrengthListRating.add(deviceInfo
							.getSignalStrengthEVDO());
				} else if (networkName.equalsIgnoreCase("LTE (4G)")) {
					if (deviceInfo.getSignalStrengthLTE().equals("Empty")
							|| deviceInfo.getSignalStrengthLTE().equals("")) {
						int signalStrengthLTE = Integer.parseInt("0");
						int signalStrengthLTEValue = signalStrengthLTE;
						signalStrengthList.add(String
								.valueOf(signalStrengthLTEValue + "dBm."));
						signalStrengthLt.add(String.valueOf(
								signalStrengthLTEValue)
								.substring(
										1,
										String.valueOf(signalStrengthLTEValue)
												.length()));
					} else {
						int signalStrengthLTE = Integer.parseInt(deviceInfo
								.getSignalStrengthLTE());
						int signalStrengthLTEValue = -140 + signalStrengthLTE;
						signalStrengthList.add(String
								.valueOf(signalStrengthLTEValue + "dBm."));
						signalStrengthLt.add(String.valueOf(
								signalStrengthLTEValue)
								.substring(
										1,
										String.valueOf(signalStrengthLTEValue)
												.length()));
					}
					signalStrengthListRating.add(deviceInfo
							.getSignalStrengthLTE());
				} else {
					if (deviceInfo.getSignalStrength().equals("Empty")
							|| deviceInfo.getSignalStrength().equals("")) {
						int signalStrengthGSM = Integer.parseInt("0");
						int signalStrengthGSMValue = -Integer
								.parseInt(SIGNALSTRENGTH_GSM)
								+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
								* signalStrengthGSM;
						signalStrengthList.add(String
								.valueOf(signalStrengthGSMValue + "dBm."));
						signalStrengthLt.add(String.valueOf(
								signalStrengthGSMValue)
								.substring(
										1,
										String.valueOf(signalStrengthGSMValue)
												.length()));
					} else {
						int signalStrengthGSM = Integer.parseInt(deviceInfo
								.getSignalStrength());
						int signalStrengthGSMValue = -Integer
								.parseInt(SIGNALSTRENGTH_GSM)
								+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
								* signalStrengthGSM;
						signalStrengthList.add(String
								.valueOf(signalStrengthGSMValue + "dBm."));
						signalStrengthLt.add(String.valueOf(
								signalStrengthGSMValue)
								.substring(
										1,
										String.valueOf(signalStrengthGSMValue)
												.length()));
					}
					signalStrengthListRating
							.add(deviceInfo.getSignalStrength());
				}
				double txvalue = 0;
				double rxvalue = 0;
				double mainValue = 0;
				/*
				 * throughputMain=dataConnectivityDao.getThroughput(i,txvalue,rxvalue
				 * ,mainValue,
				 * eventName,currTxBytes,currTxBytes,prevRxBytes,prevTxBytes
				 * ,throughputRX,throughputTX,deviceInfo,THROUGHPUT);
				 */

				// throughputList.add(new
				// Float(deviceInfo.getThroughputmain())*8);
			}
			longitudes = longitudeList.toString();
			latitudes = lattitudeList.toString();
			testName = testNameList.toString();
			signalStrength = signalStrengthList.toString();
			signalStrengthRating = signalStrengthListRating.toString();
			networkType = networkTypeList.toString();
			timeStampForEachSample = timeStampForEachSampleList.toString();
			String throughput = throughputList.toString();
			cellLocationCID = cellLocationList.toString();
			signalStrengthLTT = signalStrengthLt.toString();

			context.getExternalContext().getSessionMap().put(
					"deviceInfosignalStrengthLTT" + count,
					signalStrengthLTT.substring(1,
							signalStrengthLTT.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"deviceInfolattitudes" + count,
					latitudes.substring(1, latitudes.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"deviceInfolongitudes" + count,
					longitudes.substring(1, longitudes.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"deviceInfotestName" + count,
					testName.substring(1, testName.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"deviceInfosignalStrength" + count,
					signalStrength.substring(1, signalStrength.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"deviceInfosignalStrengthRating" + count,
					signalStrengthRating.substring(1, signalStrengthRating
							.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"deviceInfonetworkType" + count,
					networkType.substring(1, networkType.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"deviceInfotimeStampForEachSample" + count,
					timeStampForEachSample.substring(1, timeStampForEachSample
							.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"deviceInfocellLocationCID" + count,
					cellLocationCID.substring(1, cellLocationCID.length() - 1));
			ThrougputDataTO throughputTo = new MapReportHelper()
					.getThroughtPutDataForMap(ThroughputDeviceresults,
							timeStampForEachSampleList);
			latitudes = throughputTo.getLattitudes();// lattitudeList.toString();
			longitudes = throughputTo.getLongitudes();// longitudeList.toString();
			testName = testNameList.toString();
			signalStrength = throughputTo.getSignalStrength();// signalStrengthList.toString();
			signalStrengthRating = throughputTo.getSignalStrengthRating();// signalStrengthListRating.toString();
			networkType = throughputTo.getNetworkType();// networkTypeList.toString();
			timeStampForEachSample = throughputTo.getTimeStampForEachSample();// timeStampForEachSampleList.toString();
			throughput = throughputTo.getThroughput();// throughputList.toString();
			cellLocationCID = throughputTo.getCellLocationCID();// cellLocationList.toString();
			signalStrengthLTT = throughputTo.getSignalStrengthLTT();// signalStrengthLt.toString();

			context.getExternalContext().getSessionMap().put(
					"DataConnectivityDevice" + count, selectedDevice);
			context.getExternalContext().getSessionMap().put(
					"DatasignalStrengthLTT" + count,
					signalStrengthLTT.substring(1,
							signalStrengthLTT.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"Datalattitudes" + count,
					latitudes.substring(1, latitudes.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"Datalongitudes" + count,
					longitudes.substring(1, longitudes.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"DatatestName" + count,
					testName.substring(1, testName.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"DatasignalStrength" + count,
					signalStrength.substring(1, signalStrength.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"DatasignalStrengthRating" + count,
					signalStrengthRating.substring(1, signalStrengthRating
							.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"DatanetworkType" + count,
					networkType.substring(1, networkType.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"DatatimeStampForEachSample" + count,
					timeStampForEachSample.substring(1, timeStampForEachSample
							.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"Datathroughput" + count,
					throughput.substring(1, throughput.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"DatacellLocationCID" + count,
					cellLocationCID.substring(1, cellLocationCID.length() - 1));

		}
		return "SUCCESS";
		}catch(Exception e){
			e.printStackTrace();
			return "FAIL";
		}

	}

	private static void removeVoiceDataSession(FacesContext context, int count) {
		context.getExternalContext().getSessionMap().remove(
				"VDthrougputTimestamp" + count);
		context.getExternalContext().getSessionMap().remove(
				"VDthrougput" + count);
		context.getExternalContext().getSessionMap().remove(
				"VDVoiceandData" + count);
		context.getExternalContext().getSessionMap().remove(
				"VDlattitudes" + count);
		context.getExternalContext().getSessionMap().remove(
				"VDlongitudes" + count);
		context.getExternalContext().getSessionMap().remove(
				"VDtestName" + count);
		context.getExternalContext().getSessionMap().remove(
				"VDsignalStrength" + count);
		context.getExternalContext().getSessionMap().remove(
				"VDsignalStrengthLTT" + count);
		context.getExternalContext().getSessionMap().remove(
				"VDsignalStrengthRating" + count);
		context.getExternalContext().getSessionMap().remove(
				"VDnetworkType" + count);
		context.getExternalContext().getSessionMap().remove(
				"VDtimeStampForEachSample" + count);
		context.getExternalContext().getSessionMap().remove(
				"VDtimeStampForSample" + count);
		context.getExternalContext().getSessionMap().remove(
				"VDsignalStrengthCDMACIO" + count);
		context.getExternalContext().getSessionMap().remove(
				"VDsignalStrengthEVDOECIO" + count);
		context.getExternalContext().getSessionMap().remove(
				"VDsignalStrengthLTERSRP" + count);
		context.getExternalContext().getSessionMap().remove(
				"VDsignalStrengthLTERSRQ" + count);
		context.getExternalContext().getSessionMap().remove(
				"VDsignalStrengthLTERSSNR" + count);
		context.getExternalContext().getSessionMap().remove(
				"VDsignalStrengthLTECQI" + count);
		context.getExternalContext().getSessionMap().remove(
				"VDcellLocationCID" + count);
		context.getExternalContext().getSessionMap().remove(
				"VDdeviceInfoCallDropList");
	}

	private static void removeDataSession(FacesContext context, int count) {
		context.getExternalContext().getSessionMap().remove(
				"DataConnectivityDevice" + count);
		context.getExternalContext().getSessionMap().remove(
				"DatasignalStrengthLTT" + count);
		context.getExternalContext().getSessionMap().remove(
				"Datalattitudes" + count);
		context.getExternalContext().getSessionMap().remove(
				"Datalongitudes" + count);
		context.getExternalContext().getSessionMap().remove(
				"DatatestName" + count);
		context.getExternalContext().getSessionMap().remove(
				"DatasignalStrength" + count);
		context.getExternalContext().getSessionMap().remove(
				"DatasignalStrengthRating" + count);
		context.getExternalContext().getSessionMap().remove(
				"DatanetworkType" + count);
		context.getExternalContext().getSessionMap().remove(
				"DatatimeStampForEachSample" + count);
		context.getExternalContext().getSessionMap().remove(
				"Datathroughput" + count);
		context.getExternalContext().getSessionMap().remove(
				"DatacellLocationCID" + count);
	}

	private static void VQTremoveSession(FacesContext context, int count) {
		context.getExternalContext().getSessionMap().remove(
				"VoiceStatusList" + count);
		context.getExternalContext().getSessionMap().remove(
				"VoiceQtydevice" + count);
		context.getExternalContext().getSessionMap().remove(
				"VQTlatlong" + count);
		context.getExternalContext().getSessionMap().remove(
				"VQTstatusList" + count);
		context.getExternalContext().getSessionMap().remove(
				"VQTlattitudes" + count);
		context.getExternalContext().getSessionMap().remove(
				"VQTlongitudes" + count);
		context.getExternalContext().getSessionMap().remove(
				"VQTtestName" + count);
		context.getExternalContext().getSessionMap().remove(
				"VQTCallTimestamp" + count);
		context.getExternalContext().getSessionMap()
				.remove("VQTRating" + count);
		context.getExternalContext().getSessionMap().remove("VQTPESQ" + count);
		context.getExternalContext().getSessionMap().remove(
				"VQTPESQAverageOffset" + count);
		context.getExternalContext().getSessionMap().remove(
				"VQTNumberAllClipping" + count);
		context.getExternalContext().getSessionMap().remove(
				"VQTDurationALLClipping" + count);
		context.getExternalContext().getSessionMap().remove(
				"VQTtimeStampForEachSample" + count);
		context.getExternalContext().getSessionMap().remove(
				"VQTSTGDeviceSignalStrengthList" + count);
		context.getExternalContext().getSessionMap().remove(
				"VQTSTGDeviceNetworkTypeList" + count);
		context.getExternalContext().getSessionMap().remove(
				"VQTSTGDeviceSourceCellIdList" + count);
	}

	private static void removeVCSession(FacesContext context, int count) {
		context.getExternalContext().getSessionMap().remove(
				"VoiceConnectivityDevice" + count);
		context.getExternalContext().getSessionMap().remove(
				"VClattitudes" + count);
		context.getExternalContext().getSessionMap().remove(
				"VClongitudes" + count);
		context.getExternalContext().getSessionMap().remove(
				"VCtestName" + count);
		context.getExternalContext().getSessionMap().remove(
				"VCsignalStrength" + count);
		context.getExternalContext().getSessionMap().remove(
				"VCsignalStrengthLTT" + count);
		context.getExternalContext().getSessionMap().remove(
				"VCsignalStrengthRating" + count);
		context.getExternalContext().getSessionMap().remove(
				"VCnetworkType" + count);
		context.getExternalContext().getSessionMap().remove(
				"VCnetworkData" + count);
		context.getExternalContext().getSessionMap().remove(
				"VCnetworkRoaming" + count);
		context.getExternalContext().getSessionMap().remove(
				"VCnetworkMnc" + count);
		context.getExternalContext().getSessionMap().remove(
				"VCnetworkMcc" + count);
		context.getExternalContext().getSessionMap().remove(
				"VCcellLocationLac" + count);
		context.getExternalContext().getSessionMap().remove(
				"VCcellLocationCid" + count);
		context.getExternalContext().getSessionMap().remove(
				"VCsignalStrengthSnr" + count);
		context.getExternalContext().getSessionMap().remove(
				"VCdevicePhoneType" + count);
		context.getExternalContext().getSessionMap().remove(
				"VCtimeStampForEachSample" + count);
		context.getExternalContext().getSessionMap().remove(
				"VCtimeStampForSample" + count);
		context.getExternalContext().getSessionMap().remove(
				"VCneighbourInfo" + count);
		context.getExternalContext().getSessionMap().remove(
				"VCsignalStrengthCDMACIO" + count);
		context.getExternalContext().getSessionMap().remove(
				"VCsignalStrengthEVDOECIO" + count);
		context.getExternalContext().getSessionMap().remove(
				"VCsignalStrengthLTERSRP" + count);
		context.getExternalContext().getSessionMap().remove(
				"VCsignalStrengthLTERSRQ" + count);
		context.getExternalContext().getSessionMap().remove(
				"VCsignalStrengthLTERSSNR" + count);
		context.getExternalContext().getSessionMap().remove(
				"VCsignalStrengthLTECQI" + count);
		context.getExternalContext().getSessionMap().remove(
				"VCdeviceInfoCallDropList" + count);
	}

	private static void removeSession(FacesContext context, int count) {
		context.getExternalContext().getSessionMap().remove("callDrop" + count);
		context.getExternalContext().getSessionMap().remove(
				"signalStrengthLTT" + count);
		context.getExternalContext().getSessionMap().remove(
				"lattitudes" + count);
		context.getExternalContext().getSessionMap().remove(
				"longitudes" + count);
		context.getExternalContext().getSessionMap().remove("testName" + count);
		context.getExternalContext().getSessionMap().remove(
				"signalStrength" + count);
		context.getExternalContext().getSessionMap().remove(
				"signalStrengthRating" + count);
		context.getExternalContext().getSessionMap().remove(
				"networkType" + count);
		context.getExternalContext().getSessionMap().remove(
				"timeStampForEachSample" + count);
		context.getExternalContext().getSessionMap().remove(
				"throughput" + count);
		context.getExternalContext().getSessionMap().remove(
				"cellLocationCID" + count);
		context.getExternalContext().getSessionMap().remove(
				"reportType" + count);
		context.getExternalContext().getSessionMap().remove(
				"DataConnectivityDevice" + count);
		context.getExternalContext().getSessionMap().remove(
				"reportType" + count);
		context.getExternalContext().getSessionMap().remove(
				"lattitudes" + count);
		context.getExternalContext().getSessionMap().remove(
				"longitudes" + count);
		context.getExternalContext().getSessionMap().remove("testName" + count);
		context.getExternalContext().getSessionMap().remove(
				"signalStrength" + count);
		context.getExternalContext().getSessionMap().remove(
				"signalStrengthLTT" + count);
		context.getExternalContext().getSessionMap().remove(
				"signalStrengthRating" + count);
		context.getExternalContext().getSessionMap().remove(
				"networkType" + count);
		context.getExternalContext().getSessionMap().remove(
				"networkData" + count);
		context.getExternalContext().getSessionMap().remove(
				"networkRoaming" + count);
		context.getExternalContext().getSessionMap().remove(
				"networkMnc" + count);
		context.getExternalContext().getSessionMap().remove(
				"networkMcc" + count);
		context.getExternalContext().getSessionMap().remove(
				"cellLocationLac" + count);
		context.getExternalContext().getSessionMap().remove(
				"cellLocationCid" + count);
		context.getExternalContext().getSessionMap().remove(
				"signalStrengthSnr" + count);
		context.getExternalContext().getSessionMap().remove(
				"devicePhoneType" + count);
		context.getExternalContext().getSessionMap().remove(
				"timeStampForEachSample" + count);
		context.getExternalContext().getSessionMap().remove(
				"timeStampForSample" + count);
		context.getExternalContext().getSessionMap().remove(
				"neighbourInfo" + count);
		context.getExternalContext().getSessionMap().remove(
				"signalStrengthCDMACIO" + count);
		context.getExternalContext().getSessionMap().remove(
				"signalStrengthEVDOECIO" + count);
		context.getExternalContext().getSessionMap().remove(
				"signalStrengthLTERSRP" + count);
		context.getExternalContext().getSessionMap().remove(
				"signalStrengthLTERSRQ" + count);
		context.getExternalContext().getSessionMap().remove(
				"signalStrengthLTERSSNR" + count);
		context.getExternalContext().getSessionMap().remove(
				"signalStrengthLTECQI" + count);
		context.getExternalContext().getSessionMap().remove(
				"deviceInfoCallDropList" + count);
		context.getExternalContext().getSessionMap().remove(
				"VoiceConnectivityDevice" + count);
		context.getExternalContext().getSessionMap().remove(
				"lattitudes" + count);
		context.getExternalContext().getSessionMap().remove(
				"longitudes" + count);
		context.getExternalContext().getSessionMap().remove("testName" + count);
		context.getExternalContext().getSessionMap().remove(
				"VQuadTimestamp" + count);
		context.getExternalContext().getSessionMap().remove(
				"CallTimestamp" + count);
		context.getExternalContext().getSessionMap().remove(
				"VQuadLocation" + count);
		context.getExternalContext().getSessionMap().remove(
				"VQuadPhoneID" + count);
		context.getExternalContext().getSessionMap().remove(
				"DegradedFilename" + count);
		context.getExternalContext().getSessionMap().remove("Rating" + count);
		context.getExternalContext().getSessionMap().remove("PESQ" + count);
		context.getExternalContext().getSessionMap().remove(
				"TEMPSTATUS" + count);
		context.getExternalContext().getSessionMap().remove(
				"PESQAverageOffset" + count);
		context.getExternalContext().getSessionMap().remove(
				"PESQMaxOffset" + count);
		context.getExternalContext().getSessionMap().remove(
				"PESQMinOffset" + count);
		context.getExternalContext().getSessionMap().remove(
				"reportType" + count);
		context.getExternalContext().getSessionMap().remove(
				"signalStrength" + count);
		context.getExternalContext().getSessionMap().remove(
				"signalStrengthSnr" + count);
		context.getExternalContext().getSessionMap().remove(
				"devicePhoneType" + count);
		context.getExternalContext().getSessionMap().remove(
				"timeStampForEachSample" + count);
		context.getExternalContext().getSessionMap().remove(
				"neighbourInfo" + count);
		context.getExternalContext().getSessionMap().remove(
				"signalStrengthRating" + count);
		context.getExternalContext().getSessionMap().remove(
				"statusList" + count);
		context.getExternalContext().getSessionMap().remove(
				"deviceinfolatitude" + count);
		context.getExternalContext().getSessionMap().remove(
				"Vquadlattitude" + count);
		context.getExternalContext().getSessionMap().remove(
				"VoiceQtydevice" + count);
		context.getExternalContext().getSessionMap().remove(
				"VoiceandData" + count);
	}

	public static String generateVoiceConnectivityMap(String test_name,
			String test_type, String market_id, String device_model,
			String filename, String marketName, String testtype, int count) {
		
		FacesContext context = FacesContext.getCurrentInstance();
		String url = null;
		MapUtil mapUtil = new MapUtil();
		String ipAddress = null;
		String fileName = null;
		String selectedDevice = null;
		UserDao userDao = new UserDaoImpl();
		selectedDevice = test_name + "_" + marketName + "_" + testtype;
		List<DeviceInfoTO> deviceInfoList = userDao.getAllDeviceInfo(test_name,
				market_id, test_type, "");
		VoiceConnectivityDao connectivityDao = new VoiceConnectivityDaoImpl();
		// List<String>deviceInfoCallDropList =
		// connectivityDao.getAllDeviceInfoCallDropList(test_name,market_id,test_type);
//		List<String> deviceInfoCallDropList = userDao
//				.getAllDeviceInfoCallDropList(test_name, market_id, test_type,
//						0);
		List<String> deviceInfoCallDropList = userDao
		.getAllDeviceInfoCallDropList(test_name, market_id,
				test_type);//, 1

			if (deviceInfoCallDropList.size() == 0) {
				  deviceInfoCallDropList = userDao
				  .getAllDeviceInfoCallDropList(test_name, market_id,
				  test_type);//,2
			}
		try {
			InetAddress ip = InetAddress.getLocalHost();
			ipAddress = ip.getHostName();

			File f1 = new File(QXDM_FILE_PATH);
			File file1[] = f1.listFiles();
			for (File f : file1) {
				String tempFilename = f.getName();
				if ((tempFilename.startsWith("QXDM_" + test_name))
						&& (tempFilename.endsWith(".txt"))) {
					fileName = f.getName();
				}
			}
		} catch (UnknownHostException e) {
			logger.error(e.getMessage());
		}
		// logger.info("deviceInfoCallDropList-----"
		// + deviceInfoCallDropList.size());
		// logger.info("deviceInfoList.size()------------"
		// + deviceInfoList.size());
		if (deviceInfoList.size() <= 0) {
			context.getExternalContext().getRequestMap().remove("datamessage");
			context.getExternalContext().getRequestMap().put("datamessage",
					"No Data Found for the Selected Test Name");
			return "mapDataValidation";
		} else {
			// logger.info("deviceInfoCallDropList---------"
			// + deviceInfoCallDropList.size());
			String latitudes = "";
			String longitudes = "";
			String testName = "";
			String networkMnc = "";
			String signalStrength = "";
			String signalStrengthLTT = "";
			String signalStrengthRating = "";
			String networkType = "";
			String networkData = "";
			String networkRoaming = "";
			String networkMcc = "";
			String cellLocationLac = "";
			String cellLocationCid = "";
			String signalStrengthSnr = "";
			String devicePhoneType = "";
			String timeStampForEachSample = "";
			String timeStampForSample = "";
			String neighbourInfo = "";
			String neighbourMainString = "";
			String signalStrengthCDMACIO = "";
			String signalStrengthEVDOECIO = "";

			String signalStrengthLTERSRP = "";
			String signalStrengthLTERSRQ = "";
			String signalStrengthLTERSSNR = "";
			String signalStrengthLTECQI = "";
			String callDrop = "";
			String network = "";

			// String details = "";
			List lattitudeList = new ArrayList();
			List longitudeList = new ArrayList();
			List testNameList = new ArrayList();
			List callDropStatusList = new ArrayList();
			List signalStrengthList = new ArrayList();
			List signalStrengthListRating = new ArrayList();
			List signalStrengthLt = new ArrayList();
			List networkTypeList = new ArrayList();
			List networkDataList = new ArrayList();
			List networkRoamingList = new ArrayList();
			List networkMncList = new ArrayList();
			List networkMccList = new ArrayList();
			List cellLocationCidList = new ArrayList();
			List cellLocationLacList = new ArrayList();
			List signalStrengthSnrList = new ArrayList();
			List devicePhoneTypeList = new ArrayList();
			List timeStampForEachSampleList = new ArrayList();
			List timeStampForSampleList = new ArrayList();
			List signalStrengthCDMACIOList = new ArrayList();
			List signalStrengthEVDOECIOList = new ArrayList();
			List signalStrengthLTERSRPList = new ArrayList();
			List signalStrengthLTERSRQList = new ArrayList();
			List signalStrengthLTERSSNRList = new ArrayList();
			List signalStrengthLTECQIList = new ArrayList();

			List neighbourMainList = new ArrayList();
			String neighbourtStr = null;
			String networkName = "";

			if (deviceInfoCallDropList.size() > 0) {
				for (int i = 0; i < deviceInfoCallDropList.size(); i++) {
					callDrop = callDrop + "," + deviceInfoCallDropList.get(i);
				}
			}
			// logger.info("callDrop-----------" + callDrop);
			// logger.info("deviceInfoList----" + deviceInfoList.size());
			for (int i = 0; i < deviceInfoList.size(); i++) {
				List neighbourInfoList = new ArrayList();
				List neighboursubInfoList = new ArrayList();
				DeviceInfoTO deviceInfo = deviceInfoList.get(i);
				networkName = deviceInfo.getNetworkType();
				lattitudeList.add(deviceInfo.getLattitude());
				longitudeList.add(deviceInfo.getLongitude());
				testNameList.add(deviceInfo.getTestName());

				if (networkName.matches("GSM")) {
					if (deviceInfo.getSignalStrength().equals("Empty")
							|| deviceInfo.getSignalStrength().equals("")) {
						int signalStrengthGSM = Integer.parseInt("0");
						int signalStrengthGSMValue = -Integer
								.parseInt(SIGNALSTRENGTH_GSM)
								+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
								* signalStrengthGSM;
						signalStrengthList.add(String
								.valueOf(signalStrengthGSMValue + "dBm."));
						int signalStrengthLtvalue = -Integer
								.parseInt(SIGNALSTRENGTH_GSM)
								+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
								* signalStrengthGSM;
						signalStrengthLt
								.add(String.valueOf(signalStrengthLtvalue)
										.substring(
												1,
												String.valueOf(
														signalStrengthLtvalue)
														.length()));
						signalStrengthCDMACIOList.add("null");
						signalStrengthEVDOECIOList.add("null");
						signalStrengthSnrList.add("null");
						signalStrengthLTECQIList.add("null");
						signalStrengthLTERSSNRList.add("null");
						signalStrengthLTERSRQList.add("null");
						signalStrengthLTERSRPList.add("null");
					} else {
						int signalStrengthGSM = Integer.parseInt(deviceInfo
								.getSignalStrength());
						int signalStrengthGSMValue = -Integer
								.parseInt(SIGNALSTRENGTH_GSM)
								+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
								* signalStrengthGSM;
						signalStrengthList.add(String
								.valueOf(signalStrengthGSMValue + "dBm."));
						int signalStrengthLtvalue = -Integer
								.parseInt(SIGNALSTRENGTH_GSM)
								+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
								* signalStrengthGSM;
						signalStrengthLt
								.add(String.valueOf(signalStrengthLtvalue)
										.substring(
												1,
												String.valueOf(
														signalStrengthLtvalue)
														.length()));
						signalStrengthCDMACIOList.add("null");
						signalStrengthEVDOECIOList.add("null");
						signalStrengthSnrList.add("null");
						signalStrengthLTECQIList.add("null");
						signalStrengthLTERSSNRList.add("null");
						signalStrengthLTERSRQList.add("null");
						signalStrengthLTERSRPList.add("null");
					}
					signalStrengthListRating
							.add(deviceInfo.getSignalStrength());
				} else if (networkName.matches("CDMA")) {
					if (deviceInfo.getSignalStrengthCDMA().equals("Empty")
							|| deviceInfo.getSignalStrengthCDMA().equals("")) {
						signalStrengthList.add("0");
						signalStrengthLt.add("0");
						signalStrengthCDMACIOList.add(deviceInfo
								.getSignalStrengthCDMACIO());
						signalStrengthEVDOECIOList.add("null");
						signalStrengthSnrList.add("null");
						signalStrengthLTECQIList.add("null");
						signalStrengthLTERSSNRList.add("null");
						signalStrengthLTERSRQList.add("null");
						signalStrengthLTERSRPList.add("null");
					} else {
						signalStrengthList.add(deviceInfo
								.getSignalStrengthCDMA());
						signalStrengthLt
								.add(deviceInfo.getSignalStrengthCDMA());
						signalStrengthCDMACIOList.add(deviceInfo
								.getSignalStrengthCDMACIO());
						signalStrengthEVDOECIOList.add("null");
						signalStrengthSnrList.add("null");
						signalStrengthLTECQIList.add("null");
						signalStrengthLTERSSNRList.add("null");
						signalStrengthLTERSRQList.add("null");
						signalStrengthLTERSRPList.add("null");
					}
					signalStrengthListRating.add(deviceInfo
							.getSignalStrengthCDMA());
				} else if (networkName.matches("EVDO")) {
					if (deviceInfo.getSignalStrengthEVDO().equals("Empty")
							|| deviceInfo.getSignalStrengthEVDO().equals("")) {
						signalStrengthList.add("0");
						signalStrengthLt.add("0");
						signalStrengthEVDOECIOList.add(deviceInfo
								.getSignalStrengthEVDOECIO());
						signalStrengthSnrList.add(deviceInfo
								.getSignalStrengthSnr());
						signalStrengthCDMACIOList.add("null");
						signalStrengthLTECQIList.add("null");
						signalStrengthLTERSSNRList.add("null");
						signalStrengthLTERSRQList.add("null");
						signalStrengthLTERSRPList.add("null");
					} else {
						signalStrengthList.add(deviceInfo
								.getSignalStrengthEVDO());
						signalStrengthLt
								.add(deviceInfo.getSignalStrengthEVDO());
						signalStrengthEVDOECIOList.add(deviceInfo
								.getSignalStrengthEVDOECIO());
						signalStrengthSnrList.add(deviceInfo
								.getSignalStrengthSnr());
						signalStrengthCDMACIOList.add("null");
						signalStrengthLTECQIList.add("null");
						signalStrengthLTERSSNRList.add("null");
						signalStrengthLTERSRQList.add("null");
						signalStrengthLTERSRPList.add("null");
					}
					signalStrengthListRating.add(deviceInfo
							.getSignalStrengthEVDO());
				} else if (networkName.equalsIgnoreCase("LTE (4G)")) {
					if (deviceInfo.getSignalStrengthLTE().equals("Empty")
							|| deviceInfo.getSignalStrengthLTE().equals("")) {
						int signalStrengthLTE = Integer.parseInt("0");
						int signalStrengthLTEValue = signalStrengthLTE;
						signalStrengthList.add(String
								.valueOf(signalStrengthLTEValue + "dBm."));
						int signalStrengthLTValue = signalStrengthLTE;
						signalStrengthLt.add(String.valueOf(
								signalStrengthLTEValue)
								.substring(
										1,
										String.valueOf(signalStrengthLTEValue)
												.length()));
						signalStrengthLTERSRPList.add(deviceInfo
								.getSignalStrengthLTERSRP());
						signalStrengthLTERSRQList.add(deviceInfo
								.getSignalStrengthLTERSRQ());
						signalStrengthLTERSSNRList.add(deviceInfo
								.getSignalStrengthLTERSSNR());
						signalStrengthLTECQIList.add(deviceInfo
								.getSignalStrengthLTECQI());
						signalStrengthCDMACIOList.add("null");
						signalStrengthEVDOECIOList.add("null");
						signalStrengthSnrList.add("null");
					} else {
						int signalStrengthLTE = Integer.parseInt(deviceInfo
								.getSignalStrengthLTE());
						int signalStrengthLTEValue = -140 + signalStrengthLTE;
						signalStrengthList.add(String
								.valueOf(signalStrengthLTEValue + "dBm."));
						int signalStrengthLTValue = signalStrengthLTE;
						// signalStrengthLt.add(String.valueOf(signalStrengthLTValue).substring(1,
						// String.valueOf(signalStrengthLTValue).length()));
						signalStrengthLt.add(String.valueOf(
								signalStrengthLTEValue)
								.substring(
										1,
										String.valueOf(signalStrengthLTEValue)
												.length()));
						signalStrengthLTERSRPList.add(deviceInfo
								.getSignalStrengthLTERSRP());
						signalStrengthLTERSRQList.add(deviceInfo
								.getSignalStrengthLTERSRQ());
						signalStrengthLTERSSNRList.add(deviceInfo
								.getSignalStrengthLTERSSNR());
						signalStrengthLTECQIList.add(deviceInfo
								.getSignalStrengthLTECQI());
						signalStrengthCDMACIOList.add("null");
						signalStrengthEVDOECIOList.add("null");
						signalStrengthSnrList.add("null");
					}
					signalStrengthListRating.add(deviceInfo
							.getSignalStrengthLTE());
				} else {
					if (deviceInfo.getSignalStrength().equals("Empty")
							|| deviceInfo.getSignalStrength().equals("")) {
						int signalStrengthGSM = Integer.parseInt("0");
						int signalStrengthGSMValue = -Integer
								.parseInt(SIGNALSTRENGTH_GSM)
								+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
								* signalStrengthGSM;
						signalStrengthList.add(String
								.valueOf(signalStrengthGSMValue + "dBm."));
						int signalStrengthLtvalue = Integer
								.parseInt(SIGNALSTRENGTH_GSM)
								+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
								* signalStrengthGSM;
						signalStrengthLt
								.add(String.valueOf(signalStrengthLtvalue)
										.substring(
												1,
												String.valueOf(
														signalStrengthLtvalue)
														.length()));
						signalStrengthCDMACIOList.add("null");
						signalStrengthEVDOECIOList.add("null");
						signalStrengthSnrList.add("null");
						signalStrengthLTECQIList.add("null");
						signalStrengthLTERSSNRList.add("null");
						signalStrengthLTERSRQList.add("null");
						signalStrengthLTERSRPList.add("null");
					} else {
						int signalStrengthGSM = Integer.parseInt(deviceInfo
								.getSignalStrength());
						int signalStrengthGSMValue = -Integer
								.parseInt(SIGNALSTRENGTH_GSM)
								+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
								* signalStrengthGSM;
						signalStrengthList.add(String
								.valueOf(signalStrengthGSMValue + "dBm."));
						int signalStrengthLtvalue = -Integer
								.parseInt(SIGNALSTRENGTH_GSM)
								+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
								* signalStrengthGSM;
						signalStrengthLt
								.add(String.valueOf(signalStrengthLtvalue)
										.substring(
												1,
												String.valueOf(
														signalStrengthLtvalue)
														.length()));
						signalStrengthCDMACIOList.add("null");
						signalStrengthEVDOECIOList.add("null");
						signalStrengthSnrList.add("null");
						signalStrengthLTECQIList.add("null");
						signalStrengthLTERSSNRList.add("null");
						signalStrengthLTERSRQList.add("null");
						signalStrengthLTERSRPList.add("null");
					}
					signalStrengthListRating
							.add(deviceInfo.getSignalStrength());
				}
				network = deviceInfo.getNetworkType().trim();
				networkTypeList.add(network);
				networkDataList.add(deviceInfo.getNetworkDataState());
				networkRoamingList.add(deviceInfo.getNetworkRoaming());
				networkMccList.add(deviceInfo.getNetworkMCC());
				networkMncList.add(deviceInfo.getNetworkMNC());
				cellLocationCidList.add(deviceInfo.getCellLocationCID());
				cellLocationLacList.add(deviceInfo.getCellLocationLAC());
				devicePhoneTypeList.add(deviceInfo.getDevicePhoneType());
				// logger.info("1111111111fileName---"+fileName);
				if (fileName != null) {
					if (ENVIRONMENT.equals("PROD")) {
						ipAddress = "50.112.27.46";
						url = "http://" + ipAddress + ":" + ServerPort
								+ "/FileService/" + fileName;
					} else {
						url = "http://" + ipAddress + ":" + ServerPort
								+ "/FileService/" + fileName;
					}
					timeStampForEachSampleList.add("<b>"
							+ deviceInfo.getTimeStampForEachSample() + "</b>"
							+ " <a  onclick='renderQxdmPopup("
							+ deviceInfo.getTimeStampForEachSample()
							+ ")' target='blank'>Qxdm Info2</a>");
					timeStampForSampleList.add(deviceInfo
							.getTimeStampForEachSample());
				} else {
					timeStampForEachSampleList.add("<b>Test Times:</b>"
							+ deviceInfo.getTimeStampForEachSample());
					timeStampForSampleList.add(deviceInfo
							.getTimeStampForEachSample());
				}

				neighbourInfoList.add(deviceInfo.getNeighbourInfo());
				neighbourtStr = connectivityDao
						.getNeighbourInfo(neighbourInfoList);
				if (null != neighbourtStr) {
					neighbourtStr = neighbourtStr.substring(0, neighbourtStr
							.length() - 1);
					neighbourtStr = neighbourtStr.substring(0, neighbourtStr
							.length() - 1);
				}

				// Collections.sort(lattitudeList);
				// Collections.sort(longitudeList);
				latitudes = lattitudeList.toString();
				longitudes = longitudeList.toString();
				testName = testNameList.toString();
				signalStrength = signalStrengthList.toString();
				signalStrengthLTT = signalStrengthLt.toString();
				signalStrengthRating = signalStrengthListRating.toString();
				networkType = networkTypeList.toString();
				networkData = networkDataList.toString();
				networkRoaming = networkRoamingList.toString();
				networkMnc = networkMncList.toString();
				networkMcc = networkMccList.toString();
				cellLocationLac = cellLocationLacList.toString();
				cellLocationCid = cellLocationCidList.toString();
				signalStrengthSnr = signalStrengthSnrList.toString();
				devicePhoneType = devicePhoneTypeList.toString();
				timeStampForEachSample = timeStampForEachSampleList.toString();
				timeStampForSample = timeStampForSampleList.toString();
				neighbourMainString = neighbourMainList.toString();
				signalStrengthCDMACIO = signalStrengthCDMACIOList.toString();
				signalStrengthEVDOECIO = signalStrengthEVDOECIOList.toString();
				signalStrengthLTERSRP = signalStrengthLTERSRPList.toString();
				signalStrengthLTERSRQ = signalStrengthLTERSRQList.toString();
				signalStrengthLTERSSNR = signalStrengthLTERSSNRList.toString();
				signalStrengthLTECQI = signalStrengthLTECQIList.toString();

				removeVCSession(context, count);

				context.getExternalContext().getSessionMap().put(
						"VoiceConnectivityDevice" + count, selectedDevice);
				context.getExternalContext().getSessionMap().put(
						"VClattitudes" + count,
						latitudes.substring(1, latitudes.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"VClongitudes" + count,
						longitudes.substring(1, longitudes.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"VCtestName" + count,
						testName.substring(1, testName.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"VCsignalStrength" + count,
						signalStrength
								.substring(1, signalStrength.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"VCsignalStrengthLTT" + count,
						signalStrengthLTT.substring(1, signalStrengthLTT
								.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"VCsignalStrengthRating" + count,
						signalStrengthRating.substring(1, signalStrengthRating
								.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"VCnetworkType" + count,
						networkType.substring(1, networkType.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"VCnetworkData" + count,
						networkData.substring(1, networkData.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"VCnetworkRoaming" + count,
						networkRoaming
								.substring(1, networkRoaming.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"VCnetworkMnc" + count,
						networkMnc.substring(1, networkMnc.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"VCnetworkMcc" + count,
						networkMcc.substring(1, networkMcc.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"VCcellLocationLac" + count,
						cellLocationLac.substring(1,
								cellLocationLac.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"VCcellLocationCid" + count,
						cellLocationCid.substring(1,
								cellLocationCid.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"VCsignalStrengthSnr" + count,
						signalStrengthSnr.substring(1, signalStrengthSnr
								.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"VCdevicePhoneType" + count,
						devicePhoneType.substring(1,
								devicePhoneType.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"VCtimeStampForEachSample" + count,
						timeStampForEachSample.substring(1,
								timeStampForEachSample.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"VCtimeStampForSample" + count,
						timeStampForSample.substring(1, timeStampForSample
								.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"VCneighbourInfo" + count, neighbourtStr);
				context.getExternalContext().getSessionMap().put(
						"VCsignalStrengthCDMACIO" + count,
						signalStrengthCDMACIO.substring(1,
								signalStrengthCDMACIO.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"VCsignalStrengthEVDOECIO" + count,
						signalStrengthEVDOECIO.substring(1,
								signalStrengthEVDOECIO.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"VCsignalStrengthLTERSRP" + count,
						signalStrengthLTERSRP.substring(1,
								signalStrengthLTERSRP.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"VCsignalStrengthLTERSRQ" + count,
						signalStrengthLTERSRQ.substring(1,
								signalStrengthLTERSRQ.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"VCsignalStrengthLTERSSNR" + count,
						signalStrengthLTERSSNR.substring(1,
								signalStrengthLTERSSNR.length() - 1));
				context.getExternalContext().getSessionMap().put(
						"VCsignalStrengthLTECQI" + count,
						signalStrengthLTECQI.substring(1, signalStrengthLTECQI
								.length() - 1));
				//System.out.println("deviceInfoCallDropList.size()>>>>>>>"+deviceInfoCallDropList.size());
				//System.out.println("Count>>>>>"+count);
				if (deviceInfoCallDropList.size() > 0) {
					context.getExternalContext().getSessionMap().put(
							"VCdeviceInfoCallDropList" + count,
							callDrop.substring(1, callDrop.length()));
				} else {
					context.getExternalContext().getSessionMap().put(
							"VCdeviceInfoCallDropList" + count, callDrop);
				}
			}

			// logger.info("timeStampForSample----------------"
			// + timeStampForSample);
			// logger.info("callDrop--++--" + callDrop);
		}

		return "SUCCESS";
	}

	public static String generateVoiceQualityMap(String test_name,
			String test_type, String market_id, String device_model,
			String filename, String marketName, int count) {
		FacesContext context = FacesContext.getCurrentInstance();
		ReportDao dao = new ReportDaoImpl();
		List<DeviceInfoTO> deviceInfoList = new ArrayList<DeviceInfoTO>();
		boolean deviceStatus = false;
		UserDao userDao = new UserDaoImpl();
		String selectedDevice = null;

		stgDeviceresults = dao.getTestNameMapDetailsResultsForVQT(test_name,
				market_id, stgVqtresults);
		if (stgDeviceresults.size() > 0) {
			stgVqtresults = dao.getMinandMaxTimestampforVQT(test_name,
					market_id);
			if (stgVqtresults.size() < 0) {
				context.getExternalContext().getRequestMap().remove(
						"datamessage");
				context.getExternalContext().getRequestMap().put("datamessage",
						"No Data Found for the Selected Test Name");
				removeVoiceQtySession(context);
				return "mapDataValidation";
			}
			deviceInfoList = userDao.getAllDeviceInfoForVQT(test_name,
					market_id, "externaltest", stgVqtresults);

			// logger.info();
			int signalStrengthvalue = 0;
		}
		selectedDevice = test_name + "_" + marketName + "_" + test_type;
		if (stgDeviceresults.size() <= 0) {
			context.getExternalContext().getRequestMap().remove("datamessage");
			context.getExternalContext().getRequestMap().put("datamessage",
					"No Data Found for the Selected Test Name");
			removeVoiceQtySession(context);
			return "mapDataValidation";
		} else {
			String latitudes = "";
			String VoiceStatusList = "";
			String longitudes = "";
			String testName = "";
			String CallTimestamp = "";
			String Rating = "";
			String PESQ = "";
			Date date = null;
			String PESQAverageOffset = "";
			String NumberAllClipping = "";
			String DurationALLClipping = "";
			String signalStrength = "";
			String networkType = "";
			String SourcellId = "";
			String timestamp = "";
			String status = "";
			String network = "";
			String VQTlatlong = "";
			String networkMnc = "";
			String signalStrengthLTT = "";
			String signalStrengthRating = "";
			String networkData = "";
			String networkRoaming = "";

			String wifiinforssi="";
			String wifiinfobssid="";
			String wifiinfoLinkSpeed="";
			
			String networkMcc = "";
			String cellLocationLac = "";
			String cellLocationCid = "";
			String signalStrengthSnr = "";
			String devicePhoneType = "";
			String timeStampForEachSample = "";
			String timeStampForSample = "";
			String neighbourInfo = "";
			String neighbourMainString = "";
			String signalStrengthCDMACIO = "";
			String signalStrengthEVDOECIO = "";

			String signalStrengthLTERSRP = "";
			String signalStrengthLTERSRQ = "";
			String signalStrengthLTERSSNR = "";
			String signalStrengthLTECQI = "";
			List VoiceStatus = new ArrayList();
			List latlong = new ArrayList();
			List lattitudeList = new ArrayList();
			List longitudeList = new ArrayList();
			List testNameList = new ArrayList();
			List CallTimestampList = new ArrayList();
			List RatingList = new ArrayList();
			List PESQList = new ArrayList();
			List PESQAverageOffsetList = new ArrayList();
			List NumberAllClippingList = new ArrayList();
			List DurationALLClippingList = new ArrayList();
			List STGDeviceSignalStrengthList = new ArrayList();
			List STGDeviceNetworkTypeList = new ArrayList();
			List STGDeviceSourceCellIdList = new ArrayList();
			List STGDeviceLatitudeList = new ArrayList();
			List timeStampForSampleList = new ArrayList();
			List statusList = new ArrayList();
			List callDropStatusList = new ArrayList();
			List signalStrengthList = new ArrayList();
			List signalStrengthListRating = new ArrayList();
			List signalStrengthLt = new ArrayList();
			List networkTypeList = new ArrayList();
			List networkDataList = new ArrayList();
			List networkRoamingList = new ArrayList();
			List networkMncList = new ArrayList();
			List networkMccList = new ArrayList();
			List cellLocationCidList = new ArrayList();
			List cellLocationLacList = new ArrayList();
			List signalStrengthSnrList = new ArrayList();
			List devicePhoneTypeList = new ArrayList();
			List timeStampForEachSampleList = new ArrayList();
			List signalStrengthCDMACIOList = new ArrayList();
			List signalStrengthEVDOECIOList = new ArrayList();
			List signalStrengthLTERSRPList = new ArrayList();
			List signalStrengthLTERSRQList = new ArrayList();
			List signalStrengthLTERSSNRList = new ArrayList();
			List signalStrengthLTECQIList = new ArrayList();
			List vqSignalStrengthList = new ArrayList();
			
			List wifiinforssiList = new ArrayList();
			List wifiinfobssidList = new ArrayList();
			List wifiinfoLinkSpeedList = new ArrayList();
			

			List neighbourMainList = new ArrayList();
			String neighbourtStr = null;
			String networkName = "";
			boolean TimeStampstatus = true;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat newSdf = new SimpleDateFormat(
					"dd/MM/yyyy HH:mm:ss.SSS");
			int k = 0;
			for (int i = 0; i < deviceInfoList.size(); i++) {
				List neighbourInfoList = new ArrayList();
				List neighboursubInfoList = new ArrayList();
				DeviceInfoTO deviceInfo = deviceInfoList.get(i);
				networkName = deviceInfo.getNetworkType();
				lattitudeList.add(deviceInfo.getLattitude());
				longitudeList.add(deviceInfo.getLongitude());
				testNameList.add(deviceInfo.getTestName());
				STGDeviceSignalStrengthList.add(deviceInfo.getSignalStrength());
				if (true) {
					STGDevice devInfo = null;
					// TimeStampstatus=matchCallTimestamp(devInfo.getCallTimestamp(),deviceInfo.getTimeStampForEachSample());
					for (int s = 0; s < stgDeviceresults.size(); s++) {
						devInfo = stgDeviceresults.get(s);
						TimeStampstatus = matchCallTimestamp(devInfo
								.getCallTimestamp(), deviceInfo
								.getTimeStampForEachSample());
						logger.info(TimeStampstatus);
						if (TimeStampstatus) {
							break;
						}
					}
					// TimeStampstatus = true;

					// logger.info("TimeStampstatus------"
					// + TimeStampstatus);
					if (TimeStampstatus == true) {
						CallTimestampList.add(devInfo.getCallTimestamp());
						// logger.info("matched--------"+matched);
						RatingList.add(devInfo.getRating());
						PESQList.add(devInfo.getPESQ());
						PESQAverageOffsetList.add(devInfo
								.getPESQAverageOffset());
						NumberAllClippingList.add(devInfo
								.getNumberAllClipping());
						DurationALLClippingList.add(devInfo
								.getDurationALLClipping());
						if (devInfo.getVQuadCallID().contains("O_")) {
							statusList.add("outgoing");
						}
						if (devInfo.getVQuadCallID().contains("I_")) {
							statusList.add("Incoming");
						}
						latlong.add(deviceInfo.getLattitude());
						VoiceStatus.add("True");
						// k++;
					} else {
						VoiceStatus.add("False");
						CallTimestampList.add("");
						RatingList.add("");
						PESQList.add("");
						PESQAverageOffsetList.add("");
						NumberAllClippingList.add("");
						DurationALLClippingList.add("");
						statusList.add("");
						latlong.add("");
					}
				} else {
					TimeStampstatus = false;
					VoiceStatus.add("False");
					CallTimestampList.add("");
					RatingList.add("");
					PESQList.add("");
					PESQAverageOffsetList.add("");
					NumberAllClippingList.add("");
					DurationALLClippingList.add("");
					statusList.add("");
					latlong.add("");
				}
				
				wifiinfobssidList.add("");
				wifiinfoLinkSpeedList.add("");
				wifiinforssiList.add("");
				
				if (networkName.matches("GSM")) {
					if (deviceInfo.getSignalStrength().equals("Empty")
							|| deviceInfo.getSignalStrength().equals("")) {
						int signalStrengthGSM = Integer.parseInt("0");
						int signalStrengthGSMValue = -Integer
								.parseInt(SIGNALSTRENGTH_GSM)
								+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
								* signalStrengthGSM;
						signalStrengthList.add(String
								.valueOf(signalStrengthGSMValue + "dBm."));
						int signalStrengthLtvalue = -Integer
								.parseInt(SIGNALSTRENGTH_GSM)
								+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
								* signalStrengthGSM;
						signalStrengthLt
								.add(String.valueOf(signalStrengthLtvalue)
										.substring(
												1,
												String.valueOf(
														signalStrengthLtvalue)
														.length()));
						signalStrengthCDMACIOList.add("null");
						signalStrengthEVDOECIOList.add("null");
						signalStrengthSnrList.add("null");
						vqSignalStrengthList.add(String
								.valueOf(signalStrengthGSMValue));
						signalStrengthLTECQIList.add("null");
						signalStrengthLTERSSNRList.add("null");
						signalStrengthLTERSRQList.add("null");
						signalStrengthLTERSRPList.add("null");
					} else {
						int signalStrengthGSM = Integer.parseInt(deviceInfo
								.getSignalStrength());
						int signalStrengthGSMValue = -Integer
								.parseInt(SIGNALSTRENGTH_GSM)
								+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
								* signalStrengthGSM;
						signalStrengthList.add(String
								.valueOf(signalStrengthGSMValue + "dBm."));
						int signalStrengthLtvalue = -Integer
								.parseInt(SIGNALSTRENGTH_GSM)
								+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
								* signalStrengthGSM;
						signalStrengthLt
								.add(String.valueOf(signalStrengthLtvalue)
										.substring(
												1,
												String.valueOf(
														signalStrengthLtvalue)
														.length()));
						signalStrengthCDMACIOList.add("null");
						vqSignalStrengthList.add(String
								.valueOf(signalStrengthGSMValue));
						signalStrengthEVDOECIOList.add("null");
						signalStrengthSnrList.add("null");
						signalStrengthLTERSRPList.add(deviceInfo
								.getSignalStrengthLTERSRP());
						signalStrengthLTERSRQList.add(deviceInfo
								.getSignalStrengthLTERSRQ());
						signalStrengthLTERSSNRList.add(deviceInfo
								.getSignalStrengthLTERSSNR());
						signalStrengthLTECQIList.add(deviceInfo
								.getSignalStrengthLTECQI());
					}
					signalStrengthListRating
							.add(deviceInfo.getSignalStrength());
				} else if (networkName.matches("CDMA")) {
					if (deviceInfo.getSignalStrengthCDMA().equals("Empty")
							|| deviceInfo.getSignalStrengthCDMA().equals("")) {
						signalStrengthList.add("0");
						signalStrengthLt.add("0");
						vqSignalStrengthList.add(String.valueOf("0"));
						signalStrengthCDMACIOList.add(deviceInfo
								.getSignalStrengthCDMACIO());
						signalStrengthEVDOECIOList.add("null");
						signalStrengthSnrList.add("null");
						signalStrengthLTECQIList.add("null");
						signalStrengthLTERSSNRList.add("null");
						signalStrengthLTERSRQList.add("null");
						signalStrengthLTERSRPList.add("null");
					} else {
						signalStrengthList.add(deviceInfo
								.getSignalStrengthCDMA());
						signalStrengthLt
								.add(deviceInfo.getSignalStrengthCDMA());
						signalStrengthCDMACIOList.add(deviceInfo
								.getSignalStrengthCDMACIO());
						vqSignalStrengthList.add(deviceInfo
								.getSignalStrengthCDMA());
						signalStrengthEVDOECIOList.add("null");
						signalStrengthSnrList.add("null");
						signalStrengthLTERSRPList.add(deviceInfo
								.getSignalStrengthLTERSRP());
						signalStrengthLTERSRQList.add(deviceInfo
								.getSignalStrengthLTERSRQ());
						signalStrengthLTERSSNRList.add(deviceInfo
								.getSignalStrengthLTERSSNR());
						signalStrengthLTECQIList.add(deviceInfo
								.getSignalStrengthLTECQI());
					}
					signalStrengthListRating.add(deviceInfo
							.getSignalStrengthCDMA());
				} else if (networkName.matches("EVDO")) {
					if (deviceInfo.getSignalStrengthEVDO().equals("Empty")
							|| deviceInfo.getSignalStrengthEVDO().equals("")) {
						signalStrengthList.add("0");
						signalStrengthLt.add("0");
						signalStrengthEVDOECIOList.add(deviceInfo
								.getSignalStrengthEVDOECIO());
						signalStrengthSnrList.add(deviceInfo
								.getSignalStrengthSnr());
						signalStrengthCDMACIOList.add("null");
						signalStrengthLTECQIList.add("null");
						signalStrengthLTERSSNRList.add("null");
						signalStrengthLTERSRQList.add("null");
						signalStrengthLTERSRPList.add("null");
					} else {
						signalStrengthList.add(deviceInfo
								.getSignalStrengthEVDO());
						signalStrengthLt
								.add(deviceInfo.getSignalStrengthEVDO());
						signalStrengthEVDOECIOList.add(deviceInfo
								.getSignalStrengthEVDOECIO());
						signalStrengthSnrList.add(deviceInfo
								.getSignalStrengthSnr());
						signalStrengthCDMACIOList.add("null");
						signalStrengthLTERSRPList.add(deviceInfo
								.getSignalStrengthLTERSRP());
						signalStrengthLTERSRQList.add(deviceInfo
								.getSignalStrengthLTERSRQ());
						signalStrengthLTERSSNRList.add(deviceInfo
								.getSignalStrengthLTERSSNR());
						signalStrengthLTECQIList.add(deviceInfo
								.getSignalStrengthLTECQI());
					}
					signalStrengthListRating.add(deviceInfo
							.getSignalStrengthEVDO());
				} else if (networkName.equalsIgnoreCase("LTE (4G)")) {
					if (deviceInfo.getSignalStrengthLTE().equals("Empty")
							|| deviceInfo.getSignalStrengthLTE().equals("")) {
						int signalStrengthLTE = Integer.parseInt("0");
						int signalStrengthLTEValue = signalStrengthLTE;
						vqSignalStrengthList.add(signalStrengthLTEValue);
						signalStrengthList.add(String
								.valueOf(signalStrengthLTEValue + "dBm."));
						int signalStrengthLTValue = signalStrengthLTE;
						signalStrengthLt
								.add(String.valueOf(signalStrengthLTValue)
										.substring(
												1,
												String.valueOf(
														signalStrengthLTValue)
														.length()));
						// signalStrengthLt.add(String.valueOf(deviceInfo.getSignalStrengthLTERSRP()).substring(1,
						// String.valueOf(deviceInfo.getSignalStrengthLTERSRP()).length()));
						signalStrengthLTERSRPList.add(deviceInfo
								.getSignalStrengthLTERSRP());
						signalStrengthLTERSRQList.add(deviceInfo
								.getSignalStrengthLTERSRQ());
						signalStrengthLTERSSNRList.add(deviceInfo
								.getSignalStrengthLTERSSNR());
						signalStrengthLTECQIList.add(deviceInfo
								.getSignalStrengthLTECQI());
						signalStrengthCDMACIOList.add("null");
						signalStrengthEVDOECIOList.add("null");
						signalStrengthSnrList.add("null");
					} else {
						int signalStrengthLTE = Integer.parseInt(deviceInfo
								.getSignalStrengthLTE());
						int signalStrengthLTEValue = -140 + signalStrengthLTE;
						signalStrengthList.add(String
								.valueOf(signalStrengthLTEValue + "dBm."));
						vqSignalStrengthList.add(String
								.valueOf(signalStrengthLTEValue));
						int signalStrengthLTValue = signalStrengthLTE;
						signalStrengthLt
								.add(String.valueOf(signalStrengthLTValue)
										.substring(
												1,
												String.valueOf(
														signalStrengthLTValue)
														.length()));
						// signalStrengthLt.add(String.valueOf(deviceInfo.getSignalStrengthLTERSRP()).substring(1,
						// String.valueOf(deviceInfo.getSignalStrengthLTERSRP()).length()));
						signalStrengthLTERSRPList.add(deviceInfo
								.getSignalStrengthLTERSRP());
						signalStrengthLTERSRQList.add(deviceInfo
								.getSignalStrengthLTERSRQ());
						signalStrengthLTERSSNRList.add(deviceInfo
								.getSignalStrengthLTERSSNR());
						signalStrengthLTECQIList.add(deviceInfo
								.getSignalStrengthLTECQI());
						signalStrengthCDMACIOList.add("null");
						signalStrengthEVDOECIOList.add("null");
						signalStrengthSnrList.add("null");
					}
					signalStrengthListRating.add(deviceInfo
							.getSignalStrengthLTE());
				} else {
					
					wifiinfobssidList.add(deviceInfo.getWifiInfoBssid());
					wifiinfoLinkSpeedList.add(deviceInfo
							.getWifiInfoLinkSpeed());
					wifiinforssiList.add(deviceInfo.getWifiRssi());
					
					if (deviceInfo.getSignalStrength().equals("Empty")
							|| deviceInfo.getSignalStrength().equals("")) {
						int signalStrengthGSM = Integer.parseInt("0");
						int signalStrengthGSMValue = -Integer
								.parseInt(SIGNALSTRENGTH_GSM)
								+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
								* signalStrengthGSM;
						signalStrengthList.add(String
								.valueOf(signalStrengthGSMValue + "dBm."));
						int signalStrengthLtvalue = Integer
								.parseInt(SIGNALSTRENGTH_GSM)
								+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
								* signalStrengthGSM;
						signalStrengthLt
								.add(String.valueOf(signalStrengthLtvalue)
										.substring(
												1,
												String.valueOf(
														signalStrengthLtvalue)
														.length()));
						signalStrengthCDMACIOList.add("null");
						vqSignalStrengthList.add(String
								.valueOf(signalStrengthGSMValue));
						signalStrengthEVDOECIOList.add("null");
						signalStrengthSnrList.add("null");
						signalStrengthLTERSRPList.add(deviceInfo
								.getSignalStrengthLTERSRP());
						signalStrengthLTERSRQList.add(deviceInfo
								.getSignalStrengthLTERSRQ());
						signalStrengthLTERSSNRList.add(deviceInfo
								.getSignalStrengthLTERSSNR());
						signalStrengthLTECQIList.add(deviceInfo
								.getSignalStrengthLTECQI());
					} else {
						int signalStrengthGSM = Integer.parseInt(deviceInfo
								.getSignalStrength());
						int signalStrengthGSMValue = -Integer
								.parseInt(SIGNALSTRENGTH_GSM)
								+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
								* signalStrengthGSM;
						signalStrengthList.add(String
								.valueOf(signalStrengthGSMValue + "dBm."));
						int signalStrengthLtvalue = -Integer
								.parseInt(SIGNALSTRENGTH_GSM)
								+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
								* signalStrengthGSM;
						signalStrengthLt
								.add(String.valueOf(signalStrengthLtvalue)
										.substring(
												1,
												String.valueOf(
														signalStrengthLtvalue)
														.length()));
						signalStrengthCDMACIOList.add("null");
						vqSignalStrengthList.add(String
								.valueOf(signalStrengthGSMValue));
						signalStrengthEVDOECIOList.add("null");
						signalStrengthSnrList.add("null");
						signalStrengthLTERSRPList.add(deviceInfo
								.getSignalStrengthLTERSRP());
						signalStrengthLTERSRQList.add(deviceInfo
								.getSignalStrengthLTERSRQ());
						signalStrengthLTERSSNRList.add(deviceInfo
								.getSignalStrengthLTERSSNR());
						signalStrengthLTECQIList.add(deviceInfo
								.getSignalStrengthLTECQI());
					}
					signalStrengthListRating
							.add(deviceInfo.getSignalStrength());
				}
				network = deviceInfo.getNetworkType().trim();
				networkTypeList.add(network);
				networkDataList.add(deviceInfo.getNetworkDataState());
				networkRoamingList.add(deviceInfo.getNetworkRoaming());
				networkMccList.add(deviceInfo.getNetworkMCC());
				networkMncList.add(deviceInfo.getNetworkMNC());
				cellLocationCidList.add(deviceInfo.getCellLocationCID());
				cellLocationLacList.add(deviceInfo.getCellLocationLAC());
				devicePhoneTypeList.add(deviceInfo.getDevicePhoneType());
				// logger.info("333333333333fileName---"+fileName);
				/*
				 * if(fileName!=null){ if(ENVIRONMENT.equals("PROD")){
				 * ipAddress="50.112.27.46";
				 * url="http://"+ipAddress+":"+ServerPort
				 * +"/FileService/"+fileName; }else{
				 * url="http://"+ipAddress+":"+
				 * ServerPort+"/FileService/"+fileName; }
				 * timeStampForEachSampleList
				 * .add("<b>"+deviceInfo.getTimeStampForEachSample
				 * ()+"</b>"+" <a  onclick='renderQxdmPopup("
				 * +deviceInfo.getTimeStampForEachSample
				 * ()+")' target='blank'>Qxdm Info1</a>");
				 * timeStampForSampleList
				 * .add(deviceInfo.getTimeStampForEachSample()); }else{
				 */
				timeStampForEachSampleList.add("<b>Test Times:</b>"
						+ deviceInfo.getTimeStampForEachSample());
				timeStampForSampleList.add(deviceInfo
						.getTimeStampForEachSample());
				// }

				neighbourInfoList.add(deviceInfo.getNeighbourInfo());
				for (int j = 0; j < neighbourInfoList.size(); j++) {
					if (null != neighbourInfoList.get(j)
							&& !neighbourInfoList.get(j).toString().equals(
									"Empty")) {
						String neighbourInfoArray[] = neighbourInfoList.get(j)
								.toString().split("\\$");
						for (int a = 0; a < neighbourInfoArray.length; a++) {
							String neighbourSubInfoArray[] = neighbourInfoArray[a]
									.split("\\^");
							if (a == 0) {
								if (neighbourtStr == null) {
									if (neighbourSubInfoArray[0].matches("-1")
											&& neighbourSubInfoArray[1]
													.matches("-1")) {
										neighbourtStr = "Cell: Empty" + ","
												+ "Lac:Empty" + "," + "PSC:"
												+ neighbourSubInfoArray[3]
												+ "," + "RSSI:"
												+ neighbourSubInfoArray[4];
									} else if (neighbourSubInfoArray[0]
											.matches("-1")) {
										neighbourtStr = "Cell: Empty" + ","
												+ "Lac:"
												+ neighbourSubInfoArray[1]
												+ "," + "PSC:"
												+ neighbourSubInfoArray[3]
												+ "," + "RSSI:"
												+ neighbourSubInfoArray[4];
									} else if (neighbourSubInfoArray[1]
											.matches("-1")) {
										neighbourtStr = "Cell:"
												+ neighbourSubInfoArray[0]
												+ "," + "Lac:Empty" + ","
												+ "PSC:"
												+ neighbourSubInfoArray[3]
												+ "," + "RSSI:"
												+ neighbourSubInfoArray[4];
									} else {
										neighbourtStr = "Cell:"
												+ neighbourSubInfoArray[0]
												+ "," + "Lac:"
												+ neighbourSubInfoArray[1]
												+ "," + "PSC:"
												+ neighbourSubInfoArray[3]
												+ "," + "RSSI:"
												+ neighbourSubInfoArray[4];
									}
								} else {
									if (neighbourSubInfoArray[0].matches("-1")
											&& neighbourSubInfoArray[1]
													.matches("-1")) {
										neighbourtStr = neighbourtStr
												+ "|||Cell:Empty" + ","
												+ "Lac:Empty" + "," + "PSC:"
												+ neighbourSubInfoArray[3]
												+ "," + "RSSI:"
												+ neighbourSubInfoArray[4];
									} else if (neighbourSubInfoArray[0]
											.matches("-1")) {
										neighbourtStr = neighbourtStr
												+ "|||Cell:Empty" + ","
												+ "Lac:"
												+ neighbourSubInfoArray[1]
												+ "," + "PSC:"
												+ neighbourSubInfoArray[3]
												+ "," + "RSSI:"
												+ neighbourSubInfoArray[4];
									} else if (neighbourSubInfoArray[1]
											.matches("-1")) {
										neighbourtStr = neighbourtStr
												+ "|||Cell:"
												+ neighbourSubInfoArray[0]
												+ "," + "Lac:Empty" + ","
												+ "PSC:"
												+ neighbourSubInfoArray[3]
												+ "," + "RSSI:"
												+ neighbourSubInfoArray[4];
									} else {
										neighbourtStr = neighbourtStr
												+ "|||Cell:"
												+ neighbourSubInfoArray[0]
												+ "," + "Lac:"
												+ neighbourSubInfoArray[1]
												+ "," + "PSC:"
												+ neighbourSubInfoArray[3]
												+ "," + "RSSI:"
												+ neighbourSubInfoArray[4];
									}

								}
							} else if (a == (neighbourInfoArray.length - 1)) {
								if (neighbourSubInfoArray[0].matches("-1")
										&& neighbourSubInfoArray[1]
												.matches("-1")) {
									neighbourtStr = neighbourtStr
											+ "|||Cell:Empty" + ","
											+ "Lac:Empty" + "," + "PSC:"
											+ neighbourSubInfoArray[3] + ","
											+ "RSSI:"
											+ neighbourSubInfoArray[4] + "||";
								} else if (neighbourSubInfoArray[0]
										.matches("-1")) {
									neighbourtStr = neighbourtStr
											+ "|||Cell:Empty" + "," + "Lac:"
											+ neighbourSubInfoArray[1] + ","
											+ "PSC:" + neighbourSubInfoArray[3]
											+ "," + "RSSI:"
											+ neighbourSubInfoArray[4] + "||";
								} else if (neighbourSubInfoArray[1]
										.matches("-1")) {
									neighbourtStr = neighbourtStr + "|||Cell:"
											+ neighbourSubInfoArray[0] + ","
											+ "Lac:Empty" + "," + "PSC:"
											+ neighbourSubInfoArray[3] + ","
											+ "RSSI:"
											+ neighbourSubInfoArray[4] + "||";
								} else {
									neighbourtStr = neighbourtStr + "|||Cell:"
											+ neighbourSubInfoArray[0] + ","
											+ "Lac:" + neighbourSubInfoArray[1]
											+ "," + "PSC:"
											+ neighbourSubInfoArray[3] + ","
											+ "RSSI:"
											+ neighbourSubInfoArray[4] + "||";
								}

							} else {
								if (neighbourSubInfoArray[0].matches("-1")
										&& neighbourSubInfoArray[1]
												.matches("-1")) {
									neighbourtStr = neighbourtStr
											+ "|||Cell:Empty" + ","
											+ "Lac:Empty" + "," + "PSC:"
											+ neighbourSubInfoArray[3] + ","
											+ "RSSI:"
											+ neighbourSubInfoArray[4];
								} else if (neighbourSubInfoArray[0]
										.matches("-1")) {
									neighbourtStr = neighbourtStr
											+ "|||Cell:Empty" + "," + "Lac:"
											+ neighbourSubInfoArray[1] + ","
											+ "PSC:" + neighbourSubInfoArray[3]
											+ "," + "RSSI:"
											+ neighbourSubInfoArray[4];
								} else if (neighbourSubInfoArray[1]
										.matches("-1")) {
									neighbourtStr = neighbourtStr + "|||Cell:"
											+ neighbourSubInfoArray[0] + ","
											+ "Lac:Empty" + "," + "PSC:"
											+ neighbourSubInfoArray[3] + ","
											+ "RSSI:"
											+ neighbourSubInfoArray[4];
								} else {
									neighbourtStr = neighbourtStr + "|||Cell:"
											+ neighbourSubInfoArray[0] + ","
											+ "Lac:" + neighbourSubInfoArray[1]
											+ "," + "PSC:"
											+ neighbourSubInfoArray[3] + ","
											+ "RSSI:"
											+ neighbourSubInfoArray[4];
								}
							}
						}
					} else {
						neighbourtStr = neighbourtStr
								+ "|||Cell:Empty,Lac:Empty,PSC:Empty,RSSI:Empty||";
					}
				}
				neighbourInfoList.clear();
				if (neighboursubInfoList.size() > 0) {
					boolean single = true;
					for (int x = 0; x < neighboursubInfoList.size(); x++) {
						if (neighbourtStr == null) {
							neighbourtStr = neighboursubInfoList.get(x)
									.toString();
						} else {
							neighbourtStr = neighbourtStr + ","
									+ neighboursubInfoList.get(x).toString();
						}
					}
				}

			}
			VoiceStatusList = VoiceStatus.toString();
			latitudes = lattitudeList.toString();
			longitudes = longitudeList.toString();
			testName = testNameList.toString();
			Rating = RatingList.toString();
			PESQ = PESQList.toString();
			PESQAverageOffset = PESQAverageOffsetList.toString();
			NumberAllClipping = NumberAllClippingList.toString();
			DurationALLClipping = DurationALLClippingList.toString();
			TreeSet<String> CallTimestampListValue = new TreeSet<String>(
					CallTimestampList);
			CallTimestamp = CallTimestampListValue.toString();
			status = statusList.toString();
			timestamp = timeStampForSampleList.toString();
			VQTlatlong = latlong.toString();
			signalStrengthLTT = signalStrengthLt.toString();
			signalStrengthRating = signalStrengthListRating.toString();
			networkData = networkDataList.toString();
			networkRoaming = networkRoamingList.toString();
			networkMnc = networkMncList.toString();
			networkMcc = networkMccList.toString();
			cellLocationLac = cellLocationLacList.toString();
			cellLocationCid = cellLocationCidList.toString();
			signalStrengthSnr = signalStrengthSnrList.toString();
			devicePhoneType = devicePhoneTypeList.toString();
			timeStampForEachSample = timeStampForEachSampleList.toString();
			timeStampForSample = timeStampForSampleList.toString();
			neighbourMainString = neighbourMainList.toString();
			signalStrengthCDMACIO = signalStrengthCDMACIOList.toString();
			signalStrengthEVDOECIO = signalStrengthEVDOECIOList.toString();
			signalStrengthLTERSRP = signalStrengthLTERSRPList.toString();
			signalStrengthLTERSRQ = signalStrengthLTERSRQList.toString();
			signalStrengthLTERSSNR = signalStrengthLTERSSNRList.toString();
			signalStrengthLTECQI = signalStrengthLTECQIList.toString();
			wifiinfobssid = wifiinfobssidList.toString();
			wifiinfoLinkSpeed = wifiinfoLinkSpeedList.toString();
			wifiinforssi = wifiinforssiList.toString();

			if (STGDeviceSignalStrengthList.size() > 0) {
				for (int i = 0; i < STGDeviceSignalStrengthList.size(); i++) {
					int signalStrengthGSM = 0;
					if (STGDeviceSignalStrengthList.get(i).toString().equals(
							"Empty")) {
						signalStrengthGSM = 0;
					} else {
						signalStrengthGSM = Integer
								.parseInt(STGDeviceSignalStrengthList.get(i)
										.toString());
					}
					int signalStrengthGSMValue = -Integer
							.parseInt(SIGNALSTRENGTH_GSM)
							+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
							* signalStrengthGSM;
					signalStrength = signalStrength + ","
							+ signalStrengthGSMValue + "dBm.";
				}
			}
			if (networkTypeList.size() > 0) {
				for (int i = 0; i < networkTypeList.size(); i++) {
					networkType = networkType + ","
							+ networkTypeList.get(i).toString();
				}
			}
			if (cellLocationCidList.size() > 0) {
				for (int i = 0; i < cellLocationCidList.size(); i++) {
					SourcellId = SourcellId + ","
							+ cellLocationCidList.get(i).toString();
				}
			}
			VQTremoveSession(context, count);
			String vqSignalStrength = vqSignalStrengthList.toString();
			context.getExternalContext().getSessionMap().put(
					"VoiceStatusList" + count,
					VoiceStatusList.substring(1, VoiceStatusList.length() - 1));

			/* New Code changes */
			context.getExternalContext().getSessionMap().put(
					"VQTsignalStrengthLTERSRP" + count,
					signalStrengthLTERSRP.substring(1, signalStrengthLTERSRP
							.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"VQTsignalStrengthLTERSRQ" + count,
					signalStrengthLTERSRQ.substring(1, signalStrengthLTERSRQ
							.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"VQTsignalStrengthLTERSSNR" + count,
					signalStrengthLTERSSNR.substring(1, signalStrengthLTERSSNR
							.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"VQTsignalStrengthLTECQI" + count,
					signalStrengthLTECQI.substring(1, signalStrengthLTECQI
							.length() - 1));

			if (wifiinforssi.toString().length() > 2) {
				context.getExternalContext().getSessionMap().put(
						"wifiinforssi" + count,
						wifiinforssi.toString().substring(1,
								wifiinforssi.toString().length() - 1));
			}
			context.getExternalContext().getSessionMap().put(
					"wifiInfoLinkSpeed" + count,
					wifiinfoLinkSpeed.toString().substring(1,
							wifiinfoLinkSpeed.toString().length() - 1));
			context.getExternalContext().getSessionMap().put(
					"wifiInfoBssid" + count,
					wifiinfobssid.toString().substring(1,
							wifiinfobssid.toString().length() - 1));
			
			context.getExternalContext().getSessionMap().put(
					"VoiceQtydevice" + count, selectedDevice);
			context.getExternalContext().getSessionMap().put(
					"VQTsignalStrengthLTT" + count,
					signalStrengthLTT.substring(1,
							signalStrengthLTT.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"VQTsignalStrengthRating" + count,
					signalStrengthRating.substring(1, signalStrengthRating
							.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"VQTnetworkMnc" + count,
					networkMnc.substring(1, networkMnc.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"VQTnetworkMcc" + count,
					networkMcc.substring(1, networkMcc.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"VQTcellLocationLac" + count,
					cellLocationLac.substring(1, cellLocationLac.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"VQTcellLocationCid" + count,
					cellLocationCid.substring(1, cellLocationCid.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"VQTsignalStrengthSnr" + count,
					signalStrengthSnr.substring(1,
							signalStrengthSnr.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"VQTneighbourInfo" + count, neighbourtStr);
			context.getExternalContext().getSessionMap().put(
					"VQTlatlong" + count,
					VQTlatlong.substring(1, VQTlatlong.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"VQTstatusList" + count,
					status.substring(1, status.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"VQTlattitudes" + count,
					latitudes.substring(1, latitudes.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"VQTlongitudes" + count,
					longitudes.substring(1, longitudes.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"VQTtestName" + count,
					testName.substring(1, testName.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"VQTCallTimestamp" + count,
					CallTimestamp.substring(1, CallTimestamp.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"VQTRating" + count,
					Rating.substring(1, Rating.length() - 1));
			context.getExternalContext().getSessionMap().put("VQTPESQ" + count,
					PESQ.substring(1, PESQ.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"VQTPESQAverageOffset" + count,
					PESQAverageOffset.substring(1,
							PESQAverageOffset.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"VQTNumberAllClipping" + count,
					NumberAllClipping.substring(1,
							NumberAllClipping.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"VQTDurationALLClipping" + count,
					DurationALLClipping.substring(1, DurationALLClipping
							.length() - 1));
			context.getExternalContext().getSessionMap().put(
					"VQTtimeStampForEachSample" + count,
					timestamp.substring(1, timestamp.length() - 1));

			if (STGDeviceSignalStrengthList.size() > 0) {
				context.getExternalContext().getSessionMap().put(
						"VQTSTGDeviceSignalStrengthList" + count,
						signalStrength.substring(1, signalStrength.length()));
				context.getExternalContext().getSessionMap().put(
						"vqSignalStrength" + count,
						vqSignalStrength
								.substring(1, vqSignalStrength.length()));
			} else {
				context.getExternalContext().getSessionMap().put(
						"VQTSTGDeviceSignalStrengthList" + count,
						signalStrength);
				context.getExternalContext().getSessionMap().put(
						"vqSignalStrength" + count, vqSignalStrength);
			}
			if (STGDeviceNetworkTypeList.size() > 0) {
				context.getExternalContext().getSessionMap().put(
						"VQTSTGDeviceNetworkTypeList" + count,
						networkType.substring(1, networkType.length()));
			} else {
				context.getExternalContext().getSessionMap().put(
						"VQTSTGDeviceNetworkTypeList" + count, networkType);
			}
			if (STGDeviceSourceCellIdList.size() > 0) {
				context.getExternalContext().getSessionMap().put(
						"VQTSTGDeviceSourceCellIdList" + count,
						SourcellId.substring(1, SourcellId.length()));
			} else {
				context.getExternalContext().getSessionMap().put(
						"VQTSTGDeviceSourceCellIdList" + count, SourcellId);
			}
		}

		return "SUCCESS";
	}

	public static String getFormatStr(TreeSet<String> tmpList3) {
		String value = "";
		TreeSet<String> Uniquedevicemodel = new TreeSet<String>(tmpList3);
		java.util.Iterator<String> itr = Uniquedevicemodel.iterator();
		while (itr.hasNext()) {
			if (value == "") {
				value = itr.next();
			} else {
				value = value + "," + itr.next();
			}
		}
		return value;
	}

	public static String formStaticUrl(String zoomIndex) {
		String staticUrl = "";
		String latitudes = "";
		String longitudes = "";
		HttpServletRequest origRequest = (HttpServletRequest) FacesContext
				.getCurrentInstance().getExternalContext().getRequest();
		FacesContext context = FacesContext.getCurrentInstance();
		latitudes = context.getExternalContext().getSessionMap().get(
				"lattitudes").toString();
		longitudes = context.getExternalContext().getSessionMap().get(
				"longitudes").toString();
		String[] latitudesArr = latitudes.split(",");
		String[] longitudesArr = longitudes.split(",");
		String googleUrl = " http://maps.googleapis.com/maps/api/staticmap?"
				+ "center=" + latitudesArr[0].trim().substring(0, 5) + ","
				+ longitudesArr[0].trim().substring(0, 5) + "&zoom="
				+ zoomIndex + "&size=1024x1024&maptype=roadmap&";
		for (int i = 1; i < latitudesArr.length; i++) {
			if (googleUrl.length() < 2000) {
				String markers = "&markers=color:green|"
						+ latitudesArr[i].trim().substring(0, 5) + ","
						+ longitudesArr[i].trim().substring(0, 5) + "&";
				googleUrl = googleUrl + markers;
			}

		}
		googleUrl = googleUrl + "sensor=false".trim();
		staticUrl = "http://122.181.151.113:"
				+ origRequest.getServerPort()
				+ "/Airometric/Pages/exportPdf.jsf?loggedInUserName=admin&testCaseName=Nexus520";
		return googleUrl;
	}

	/**
	 * Navigation From Welcome Page to Map Page.
	 */

	public String welcomeReports() {
		setMapReportType("signalStrength");
		TestConfigDao testConfigDao = new TestConfigDaoImpl();
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		context.getExternalContext().getSessionMap().remove("testNameMap");
		String roleName = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		String userId = context.getExternalContext().getSessionMap().get(
				"loggedInUserID").toString();
		String userName = context.getExternalContext().getSessionMap().get(
				"userName").toString();
		// logger.info("context.getExternalContext().getSessionMap()---+-------"+context.getExternalContext().getSessionMap());
		context.getExternalContext().getSessionMap().put("loggedInUserID",
				userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				roleName);
		// logger.info("roleName-----------"+roleName);
		if (roleName.equals("superadmin")) {
			testNameMap = testConfigDao.getTestNamesInMapInStgDevice();
		} else {
			Set<String> userNameList = new UserDaoImpl().getUsersOfSuperior(
					userId, roleName);
			testNameMap = new TestConfigDaoImpl()
					.getnTestNamesInMapInHierarchy(userNameList.toString()
							.substring(1, userNameList.toString().length() - 1));
			// testNameMap = testConfigDao.getTestNamesInMapInStgDevice(userId);
		}
		context.getExternalContext().getSessionMap().put("customRangeMap",
				new QualityRangeHelper().getUserRanges());
		context.getExternalContext().getSessionMap().remove("testNameMap");
		context.getExternalContext().getSessionMap().put("testNameMap",
				testNameMap);
		context.getExternalContext().getSessionMap().remove("lattitudes");
		context.getExternalContext().getSessionMap().remove("longitudes");
		context.getExternalContext().getSessionMap().remove("testName");
		context.getExternalContext().getSessionMap().remove("signalStrength");
		context.getExternalContext().getSessionMap().remove("networkType");
		context.getExternalContext().getSessionMap().remove("networkData");
		context.getExternalContext().getSessionMap().remove("networkRoaming");
		context.getExternalContext().getSessionMap().remove("networkMnc");
		context.getExternalContext().getSessionMap().remove("networkMcc");
		context.getExternalContext().getSessionMap().remove("cellLocationLac");
		context.getExternalContext().getSessionMap().remove("cellLocationCid");
		context.getExternalContext().getSessionMap()
				.remove("signalStrengthSnr");
		context.getExternalContext().getSessionMap().remove("devicePhoneType");
		context.getExternalContext().getSessionMap().remove(
				"timeStampForEachSample");
		context.getExternalContext().getSessionMap()
				.put("upperRangeForGood", 0);
		context.getExternalContext().getSessionMap()
				.put("lowerRangeForGood", 0);
		context.getExternalContext().getSessionMap()
				.put("upperRangeForPoor", 0);
		context.getExternalContext().getSessionMap()
				.put("lowerRangeForPoor", 0);
		context.getExternalContext().getSessionMap().put(
				"upperRangeForAverage", 0);
		context.getExternalContext().getSessionMap().put(
				"lowerRangeForAverage", 0);

		context.getExternalContext().getSessionMap().put(
				"lteupperRangeForGood", 0);
		context.getExternalContext().getSessionMap().put(
				"ltelowerRangeForGood", 0);
		context.getExternalContext().getSessionMap().put(
				"lteupperRangeForPoor", 0);
		context.getExternalContext().getSessionMap().put(
				"ltelowerRangeForPoor", 0);
		context.getExternalContext().getSessionMap().put(
				"lteupperRangeForAverage", 0);
		context.getExternalContext().getSessionMap().put(
				"ltelowerRangeForAverage", 0);
		// logger.info("hiiiiiiiiiiiiiiiiiiiiiii  map ");
		return "welcomeMapsPage";
	}

	public String welcomeOperatorReports() {
		setMapReportType("signalStrength");
		TestConfigDao testConfigDao = new TestConfigDaoImpl();
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		context.getExternalContext().getSessionMap().remove("testNameMap");
		String roleName = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		String userId = context.getExternalContext().getSessionMap().get(
				"loggedInUserID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserID",
				userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				roleName);
		if (roleName.equals("superadmin")) {
			testNameMap = testConfigDao.getTestNamesInMapInStgDevice();
		} else {
			Set<String> userNameList = new UserDaoImpl().getUsersOfSuperior(
					userId, roleName);
			testNameMap = new TestConfigDaoImpl()
					.getnTestNamesInMapInHierarchy(userNameList.toString()
							.substring(1, userNameList.toString().length() - 1));
			// testNameMap = testConfigDao.getTestNamesInMapInStgDevice(userId);
		}
		context.getExternalContext().getSessionMap().remove("testNameMap");
		context.getExternalContext().getSessionMap().put("testNameMap",
				testNameMap);
		context.getExternalContext().getSessionMap().remove("lattitudes");
		context.getExternalContext().getSessionMap().remove("longitudes");
		context.getExternalContext().getSessionMap().remove("testName");
		context.getExternalContext().getSessionMap().remove("signalStrength");
		context.getExternalContext().getSessionMap().remove("networkType");
		context.getExternalContext().getSessionMap().remove("networkData");
		context.getExternalContext().getSessionMap().remove("networkRoaming");
		context.getExternalContext().getSessionMap().remove("networkMnc");
		context.getExternalContext().getSessionMap().remove("networkMcc");
		context.getExternalContext().getSessionMap().remove("cellLocationLac");
		context.getExternalContext().getSessionMap().remove("cellLocationCid");
		context.getExternalContext().getSessionMap()
				.remove("signalStrengthSnr");
		context.getExternalContext().getSessionMap().remove("devicePhoneType");
		context.getExternalContext().getSessionMap().remove(
				"timeStampForEachSample");

		return "welcomeOperatorReportsPage";
	}

	/**
	 * Navigation From Create Configuration Page to Map Page.
	 */
	public String createConfigToReports() {
		setMapReportType("signalStrength");
		TestConfigDao testConfigDao = new TestConfigDaoImpl();
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		context.getExternalContext().getSessionMap().remove("testNameMap");
		String roleName = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		String userId = context.getExternalContext().getSessionMap().get(
				"loggedInUserID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserID",
				userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				roleName);
		if (roleName.equals("superadmin")) {
			testNameMap = testConfigDao.getTestNamesInMapInStgDevice();
		} else {
			Set<String> userNameList = new UserDaoImpl().getUsersOfSuperior(
					userId, roleName);
			testNameMap = new TestConfigDaoImpl()
					.getnTestNamesInMapInHierarchy(userNameList.toString()
							.substring(1, userNameList.toString().length() - 1));
			// testNameMap = testConfigDao.getTestNamesInMapInStgDevice(userId);
		}
		context.getExternalContext().getSessionMap().remove("testNameMap");
		context.getExternalContext().getSessionMap().put("testNameMap",
				testNameMap);
		context.getExternalContext().getSessionMap().remove("lattitudes");
		context.getExternalContext().getSessionMap().remove("longitudes");
		context.getExternalContext().getSessionMap().remove("testName");
		context.getExternalContext().getSessionMap().remove("signalStrength");
		context.getExternalContext().getSessionMap().remove("networkType");
		context.getExternalContext().getSessionMap().remove("networkData");
		context.getExternalContext().getSessionMap().remove("networkRoaming");
		context.getExternalContext().getSessionMap().remove("networkMnc");
		context.getExternalContext().getSessionMap().remove("networkMcc");
		context.getExternalContext().getSessionMap().remove("cellLocationLac");
		context.getExternalContext().getSessionMap().remove("cellLocationCid");
		context.getExternalContext().getSessionMap()
				.remove("signalStrengthSnr");
		context.getExternalContext().getSessionMap().remove("devicePhoneType");
		context.getExternalContext().getSessionMap().remove(
				"timeStampForEachSample");

		return "createConfigToReportsPage";
	}

	/**
	 * Navigation From Assign Configuration Page to Map Page.
	 */
	public String assignConfigToReports() {
		setMapReportType("signalStrength");
		TestConfigDao testConfigDao = new TestConfigDaoImpl();
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		context.getExternalContext().getSessionMap().remove("testNameMap");
		String roleName = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		String userId = context.getExternalContext().getSessionMap().get(
				"loggedInUserID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserID",
				userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				roleName);
		if (roleName.equals("superadmin")) {
			testNameMap = testConfigDao.getTestNamesInMapInStgDevice();
		} else {
			Set<String> userNameList = new UserDaoImpl().getUsersOfSuperior(
					userId, roleName);
			testNameMap = new TestConfigDaoImpl()
					.getnTestNamesInMapInHierarchy(userNameList.toString()
							.substring(1, userNameList.toString().length() - 1));
			// testNameMap = testConfigDao.getTestNamesInMapInStgDevice(userId);
		}
		context.getExternalContext().getSessionMap().remove("testNameMap");
		context.getExternalContext().getSessionMap().put("testNameMap",
				testNameMap);
		context.getExternalContext().getSessionMap().remove("lattitudes");
		context.getExternalContext().getSessionMap().remove("longitudes");
		context.getExternalContext().getSessionMap().remove("testName");
		context.getExternalContext().getSessionMap().remove("signalStrength");
		context.getExternalContext().getSessionMap().remove("networkType");
		context.getExternalContext().getSessionMap().remove("networkData");
		context.getExternalContext().getSessionMap().remove("networkRoaming");
		context.getExternalContext().getSessionMap().remove("networkMnc");
		context.getExternalContext().getSessionMap().remove("networkMcc");
		context.getExternalContext().getSessionMap().remove("cellLocationLac");
		context.getExternalContext().getSessionMap().remove("cellLocationCid");
		context.getExternalContext().getSessionMap()
				.remove("signalStrengthSnr");
		context.getExternalContext().getSessionMap().remove("devicePhoneType");
		context.getExternalContext().getSessionMap().remove(
				"timeStampForEachSample");
		return "assignConfigToReportsPage";
	}

	/**
	 * Navigation From Edit Configuration Page to Map Page.
	 */
	public String editConfigToReports() {
		setMapReportType("signalStrength");
		TestConfigDao testConfigDao = new TestConfigDaoImpl();
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		context.getExternalContext().getSessionMap().remove("testNameMap");
		String roleName = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		String userId = context.getExternalContext().getSessionMap().get(
				"loggedInUserID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserID",
				userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				roleName);
		if (roleName.equals("superadmin")) {
			testNameMap = testConfigDao.getTestNamesInMapInStgDevice();
		} else {
			Set<String> userNameList = new UserDaoImpl().getUsersOfSuperior(
					userId, roleName);
			testNameMap = new TestConfigDaoImpl()
					.getnTestNamesInMapInHierarchy(userNameList.toString()
							.substring(1, userNameList.toString().length() - 1));
			// testNameMap = testConfigDao.getTestNamesInMapInStgDevice(userId);
		}
		context.getExternalContext().getSessionMap().remove("testNameMap");
		context.getExternalContext().getSessionMap().put("testNameMap",
				testNameMap);
		context.getExternalContext().getSessionMap().remove("lattitudes");
		context.getExternalContext().getSessionMap().remove("longitudes");
		context.getExternalContext().getSessionMap().remove("testName");
		context.getExternalContext().getSessionMap().remove("signalStrength");
		context.getExternalContext().getSessionMap().remove("networkType");
		context.getExternalContext().getSessionMap().remove("networkData");
		context.getExternalContext().getSessionMap().remove("networkRoaming");
		context.getExternalContext().getSessionMap().remove("networkMnc");
		context.getExternalContext().getSessionMap().remove("networkMcc");
		context.getExternalContext().getSessionMap().remove("cellLocationLac");
		context.getExternalContext().getSessionMap().remove("cellLocationCid");
		context.getExternalContext().getSessionMap()
				.remove("signalStrengthSnr");
		context.getExternalContext().getSessionMap().remove("devicePhoneType");
		context.getExternalContext().getSessionMap().remove(
				"timeStampForEachSample");
		return "editConfigToReportsPage";
	}

	/**
	 * Navigation From Upload Page to Map Page.
	 */
	public String uploadConfigToReports() {
		setMapReportType("signalStrength");
		TestConfigDao testConfigDao = new TestConfigDaoImpl();
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		context.getExternalContext().getSessionMap().remove("testNameMap");
		String roleName = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		String userId = context.getExternalContext().getSessionMap().get(
				"loggedInUserID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserID",
				userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				roleName);
		if (roleName.equals("superadmin")) {
			testNameMap = testConfigDao.getTestNamesInMapInStgDevice();
		} else {
			Set<String> userNameList = new UserDaoImpl().getUsersOfSuperior(
					userId, roleName);
			testNameMap = new TestConfigDaoImpl()
					.getnTestNamesInMapInHierarchy(userNameList.toString()
							.substring(1, userNameList.toString().length() - 1));
			// testNameMap = testConfigDao.getTestNamesInMapInStgDevice(userId);
		}
		context.getExternalContext().getSessionMap().remove("testNameMap");
		context.getExternalContext().getSessionMap().put("testNameMap",
				testNameMap);
		context.getExternalContext().getSessionMap().remove("lattitudes");
		context.getExternalContext().getSessionMap().remove("longitudes");
		context.getExternalContext().getSessionMap().remove("testName");
		context.getExternalContext().getSessionMap().remove("signalStrength");
		context.getExternalContext().getSessionMap().remove("networkType");
		context.getExternalContext().getSessionMap().remove("networkData");
		context.getExternalContext().getSessionMap().remove("networkRoaming");
		context.getExternalContext().getSessionMap().remove("networkMnc");
		context.getExternalContext().getSessionMap().remove("networkMcc");
		context.getExternalContext().getSessionMap().remove("cellLocationLac");
		context.getExternalContext().getSessionMap().remove("cellLocationCid");
		context.getExternalContext().getSessionMap()
				.remove("signalStrengthSnr");
		context.getExternalContext().getSessionMap().remove("devicePhoneType");
		context.getExternalContext().getSessionMap().remove(
				"timeStampForEachSample");
		return "uploadConfigToReportsPage";
	}

	/**
	 * Navigation From Create User Page to Map Page.
	 */
	public String createUsertoReports() {
		TestConfigDao testConfigDao = new TestConfigDaoImpl();
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		context.getExternalContext().getSessionMap().remove("testNameMap");
		String roleName = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		String userId = context.getExternalContext().getSessionMap().get(
				"loggedInUserID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserID",
				userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				roleName);
		if (roleName.equals("superadmin")) {
			testNameMap = testConfigDao.getTestNamesInMapInStgDevice();
		} else {
			Set<String> userNameList = new UserDaoImpl().getUsersOfSuperior(
					userId, roleName);
			testNameMap = new TestConfigDaoImpl()
					.getnTestNamesInMapInHierarchy(userNameList.toString()
							.substring(1, userNameList.toString().length() - 1));
			// testNameMap = testConfigDao.getTestNamesInMapInStgDevice(userId);
		}
		context.getExternalContext().getSessionMap().remove("testNameMap");
		context.getExternalContext().getSessionMap().put("testNameMap",
				testNameMap);
		context.getExternalContext().getSessionMap().remove("lattitudes");
		context.getExternalContext().getSessionMap().remove("longitudes");
		context.getExternalContext().getSessionMap().remove("testName");
		context.getExternalContext().getSessionMap().remove("signalStrength");
		context.getExternalContext().getSessionMap().remove("networkType");
		context.getExternalContext().getSessionMap().remove("networkData");
		context.getExternalContext().getSessionMap().remove("networkRoaming");
		context.getExternalContext().getSessionMap().remove("networkMnc");
		context.getExternalContext().getSessionMap().remove("networkMcc");
		context.getExternalContext().getSessionMap().remove("cellLocationLac");
		context.getExternalContext().getSessionMap().remove("cellLocationCid");
		context.getExternalContext().getSessionMap()
				.remove("signalStrengthSnr");
		context.getExternalContext().getSessionMap().remove("devicePhoneType");
		context.getExternalContext().getSessionMap().remove(
				"timeStampForEachSample");
		return "createUsertoReportsPage";
	}

	/**
	 * Navigation From Edit User Page to Map Page.
	 */
	public String editUsertoReports() {
		TestConfigDao testConfigDao = new TestConfigDaoImpl();
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		context.getExternalContext().getSessionMap().remove("testNameMap");
		String roleName = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		String userId = context.getExternalContext().getSessionMap().get(
				"loggedInUserID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserID",
				userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				roleName);
		if (roleName.equals("superadmin")) {
			testNameMap = testConfigDao.getTestNamesInMapInStgDevice();
		} else {
			Set<String> userNameList = new UserDaoImpl().getUsersOfSuperior(
					userId, roleName);
			testNameMap = new TestConfigDaoImpl()
					.getnTestNamesInMapInHierarchy(userNameList.toString()
							.substring(1, userNameList.toString().length() - 1));
			// testNameMap = testConfigDao.getTestNamesInMapInStgDevice(userId);
		}
		context.getExternalContext().getSessionMap().remove("testNameMap");
		context.getExternalContext().getSessionMap().put("testNameMap",
				testNameMap);
		context.getExternalContext().getSessionMap().remove("lattitudes");
		context.getExternalContext().getSessionMap().remove("longitudes");
		context.getExternalContext().getSessionMap().remove("testName");
		context.getExternalContext().getSessionMap().remove("signalStrength");
		context.getExternalContext().getSessionMap().remove("networkType");
		context.getExternalContext().getSessionMap().remove("networkData");
		context.getExternalContext().getSessionMap().remove("networkRoaming");
		context.getExternalContext().getSessionMap().remove("networkMnc");
		context.getExternalContext().getSessionMap().remove("networkMcc");
		context.getExternalContext().getSessionMap().remove("cellLocationLac");
		context.getExternalContext().getSessionMap().remove("cellLocationCid");
		context.getExternalContext().getSessionMap()
				.remove("signalStrengthSnr");
		context.getExternalContext().getSessionMap().remove("devicePhoneType");
		context.getExternalContext().getSessionMap().remove(
				"timeStampForEachSample");
		return "editUsertoReportsPage";
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

	public static String trimEnd(String s) {
		if (s == null || s.length() == 0)
			return s;
		int i = s.length();
		while (i > 0 && Character.isWhitespace(s.charAt(i - 1)))
			i--;
		if (i == s.length())
			return s;
		else
			return s.substring(0, i);
	}

	public String saveFeedback() throws Exception {
		ReportDao reportdao = new ReportDaoImpl();
		CommentsTo commentTo = new CommentsTo();
		// commentTo.setComments(getTestComment());
		commentTo.setComments(getTestCommentKpi1());
		FacesContext context = FacesContext.getCurrentInstance();
		JSONArray jso = new JSONArray();
		String jsoStr = context.getExternalContext().getRequestParameterMap()
				.get("reportingParams");
		ArrayList<ArrayList<Integer>> myList = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < 10; i++) {
			ArrayList<Integer> myList2 = new ArrayList<Integer>();
			for (int j = 0; j < 10; j++) {
				myList2.add(j);
			}
			myList.add(myList2);
		}
		context.getExternalContext().getSessionMap().put("myList", myList);
		return "voiceConnectPage";
	}

	public String firstAjax() {
		return "hello";
	}

	public void listener(AjaxBehaviorEvent event) {
		String result = "called by "
				+ event.getComponent().getClass().getName();
		String reportType = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get(
						"j_idt10:reportType");
		Map<String, String> marketIdMap = new LinkedHashMap<String, String>();
		Map<String, String> filesIdsMap = new LinkedHashMap<String, String>();
		Map<String, String> filesOpts = new LinkedHashMap<String, String>();
		FacesContext context = FacesContext.getCurrentInstance();
		String testName = (String) ((UIOutput) event.getSource()).getValue();

		ReportDao rDao = new ReportDaoImpl();
		filesOpts = rDao.getTestTypes(getDeviceName(), testName);
		// marketIdMap = rDao.getMarketIds(testName,getDeviceName(),reportType);
		context.getExternalContext().getSessionMap().put("filesIdsMap",
				filesIdsMap);
		context.getExternalContext().getSessionMap().put("marketIdMap",
				marketIdMap);
		context.getExternalContext().getSessionMap()
				.put("filesOpts", filesOpts);
		
		setReportType("Select Report");
		setMarketId("Select Market".split(","));
		setFilesIds("Select Files".split(","));
	}

	public void getMarketsForType(AjaxBehaviorEvent event) {
		String result = "called by "
				+ event.getComponent().getClass().getName();
		Map<String, String> marketIdMap = new LinkedHashMap<String, String>();
		Map<String, String> filesIdsMap = new LinkedHashMap<String, String>();
		Map<String, String> filesOpts = new LinkedHashMap<String, String>();
		FacesContext context = FacesContext.getCurrentInstance();
		String testName = getTestCaseName();
		String newValue = (String) ((UIOutput) event.getSource()).getValue();
		ReportDao rDao = new ReportDaoImpl();
		filesOpts = rDao.getTestTypes(getDeviceName(), testName);
		marketIdMap = rDao.getMarketIds(testName, getDeviceName(), newValue);
		System.out.println("market map-"+marketIdMap);
		context.getExternalContext().getSessionMap().put("filesIdsMap",
				filesIdsMap);
		context.getExternalContext().getSessionMap().put("marketIdMap",
				marketIdMap);
		context.getExternalContext().getSessionMap()
				.put("filesOpts", filesOpts);
		//context.getExternalContext().getSessionMap().put("ReportTypeForActive",reportType);
		setMarketId("Select Market".split(","));
		setFilesIds("Select Files".split(","));
	}

	public void getFiles(AjaxBehaviorEvent event) {
		String result = "called by "
				+ event.getComponent().getClass().getName();
		Map<String, String> marketIdMap = new LinkedHashMap<String, String>();
		Map<String, String> filesIdsMap = new LinkedHashMap<String, String>();
		Map<String, String> filesOpts = new LinkedHashMap<String, String>();
		FacesContext context = FacesContext.getCurrentInstance();
		String testName = getTestCaseName();
		String newValue = (String) ((UIOutput) event.getSource()).getValue();
		ReportDao rDao = new ReportDaoImpl();
		filesOpts = rDao.getTestTypes(getDeviceName(), testName);
		marketIdMap = rDao.getMarketIds(testName, getDeviceName(), newValue);
		context.getExternalContext().getSessionMap().put("filesIdsMap",
				filesIdsMap);
		context.getExternalContext().getSessionMap().put("marketIdMap",
				marketIdMap);
		context.getExternalContext().getSessionMap()
				.put("filesOpts", filesOpts);
	}

	public void getTest(AjaxBehaviorEvent event) {/*
												 * String result = "called by "
												 * +
												 * event.getComponent().getClass
												 * ().getName(); Map<String,
												 * String> testNameMap = new
												 * HashMap<String, String>();
												 * //logger.info(result);
												 * 
												 * FacesContext context =
												 * FacesContext
												 * .getCurrentInstance(); String
												 * newValue = (String)
												 * ((UIOutput
												 * )event.getSource()).
												 * getValue(); ReportDao rDao =
												 * new ReportDaoImpl();
												 * testNameMap =
												 * rDao.getTestNames
												 * (newValue,getDeviceName());
												 * context.getExternalContext().
												 * getSessionMap
												 * ().put("testNameMap",
												 * testNameMap);
												 */
	}

	public String getdoPDFStatus() {
		String defaultPrinter;
		String status = "";
		PrintService[] ser = PrintServiceLookup.lookupPrintServices(null, null);
		for (int i = 0; i < ser.length; ++i) {
			String p_name = ser[i].getName();
			if (!p_name.contains("doPDF")) {
				status = "";
			} else {
				status = "false";
			}
		}
		return status;
	}

	public String changeActive(AjaxBehaviorEvent event) throws JSONException,
			org.richfaces.json.JSONException {
		FacesContext context = FacesContext.getCurrentInstance();
		String marketId = context.getExternalContext().getSessionMap().get(
				"reportMarket").toString();
		String testName = context.getExternalContext().getSessionMap().get(
				"reportTestName").toString();
		JSONArray jsonUplinkArr = (JSONArray) context.getExternalContext()
				.getSessionMap().get("jsonUplinkArr");
		JSONObject json = new JSONObject();
		ArrayList newDeviceList = new ArrayList<String>();
		newDeviceList.add("1");
		newDeviceList.add("2");
		newDeviceList.add("3");
		newDeviceList.add("4");
		json.put("name", "new Device");
		json.put("data", newDeviceList);
		jsonUplinkArr.add(json);
		ReportDao rDao = new ReportDaoImpl();
		String vqtAvg = "0";
		if (null != rDao.getDeviceAvgVoiceQty(getDeviceName(), marketId,
				testName)) {
			vqtAvg = rDao.getDeviceAvgVoiceQty(getDeviceName(), marketId,
					testName);
		}
		context.getExternalContext().getSessionMap().put("jusforFun",
				rDao.getDeviceAvgVoiceQty(getDeviceName(), marketId, testName));
		context.getExternalContext().getSessionMap().put("myFunny",
				"funny Text");
		context.getExternalContext().getSessionMap().put("jsonUplinkArr",
				jsonUplinkArr);
		String result = "called by "
				+ event.getComponent().getClass().getName();
		return "hi";
	}

	// THIS SECTION IS FOR CHARTS

	/*
	 * public void getDownlikChartForVoice(String deviceName,String[]
	 * marketId,String testName) throws Exception{
	 * //logger.info("heloooo"); List myFirstList = new ArrayList();
	 * FacesContext context = FacesContext.getCurrentInstance();
	 * 
	 * for(int i=0;i<marketId.length;i++){ JSONArray vqDownlinkcategories = new
	 * JSONArray(); JSONObject json = new JSONObject(); JSONArray jsonUplinkArr
	 * = new JSONArray(); String currentMarketId = marketId[i]; //POPULATING
	 * DATA FOR REFERENCE DEVICE DeviceTo ref_deviceTo = new
	 * ReportDaoImpl().getRefDevice(testName); List valueList = new
	 * ReportDaoImpl
	 * ().getDownlinkParmatersForGraph(ref_deviceTo.getDevicename(),
	 * currentMarketId,testName);
	 * 
	 * 
	 * vqDownlinkcategories.add("PESQ"); vqDownlinkcategories.add("PESQLQ");
	 * vqDownlinkcategories.add("PESQLQO"); vqDownlinkcategories.add("PESQWB");
	 * 
	 * json.put("name",ref_deviceTo.getDevicename()); json.put("data",
	 * valueList); jsonUplinkArr.add(json);
	 * 
	 * //POPULATING DUT
	 * 
	 * valueList = new
	 * ReportDaoImpl().getDownlinkParmatersForGraph(deviceName,currentMarketId
	 * ,testName);
	 * 
	 * json.put("name",deviceName); json.put("data", valueList);
	 * jsonUplinkArr.add(json);
	 * context.getExternalContext().getSessionMap().put("jsonUplinkArr_"+i,
	 * jsonUplinkArr);
	 * context.getExternalContext().getSessionMap().put("vqDownlinkcategories_"
	 * +i, vqDownlinkcategories); }
	 * 
	 * }
	 */

	public void getVQMarketWiseSignalStrengthForVoice(String[] deviceName,
			String[] marketId, String testName) throws Exception {
		List rangeList = new ArrayList();
		FacesContext context = FacesContext.getCurrentInstance();
		rangeList.add("1-1.5");
		rangeList.add("1.5-2");
		rangeList.add("2-2.5");
		rangeList.add("2.5-3");
		rangeList.add("3-3.5");
		rangeList.add("3.5-4");
		rangeList.add("4-4.5");
		rangeList.add("4.5-5");
		JSONArray vqSignalStrengthvals = new JSONArray();
		for (int j = 0; j < deviceName.length; j++) {
			for (int i = 0; i < marketId.length; i++) {
				JSONObject json = new JSONObject();
				String currentMarketId = marketId[i];
				String marketName = new ReportDaoImpl()
						.getMarketName(currentMarketId);
				// POPULATING DATA FOR REFERENCE DEVICE
				DeviceTo ref_deviceTo = new ReportDaoImpl()
						.getRefDevice(testName);
				List valueList = new ReportDaoImpl()
						.getVQDetailsFor_LineForGraph(deviceName[j],
								marketName, testName);
				json.put("name", deviceName[j] + "_" + testName);
				json.put("data", valueList);
				// vqSignalStrengthvals.add(json);
			}
		}

	}

	public String ajaxThroughJavaScript() {
		String returnedval = "hello";
		String numberOftimesCallsmade = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get(
						"numberTimesCallMade");
		String selectedMarkets = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get(
						"selectedMarkets");
		Map<String, String> marketIdMap = new LinkedHashMap<String, String>();
		String roleName = FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("loggedInUserRoleID")
				.toString();
		FacesContext context = FacesContext.getCurrentInstance();
		String userName = context.getExternalContext().getSessionMap().get(
				"userName").toString();
		String userId = context.getExternalContext().getSessionMap().get(
				"loggedInUserID").toString();
		ReportDao rDao = new ReportDaoImpl();
		if (roleName.equals("superadmin")) {
			testNameMap = new ReportDaoImpl().getTestNames(getDeviceName());
		} else {
			Set<String> userNameList = new UserDaoImpl().getUsersOfSuperior(
					userId, roleName);
			testNameMap = new ReportDaoImpl().getTestNames(getDeviceName(),
					userNameList.toString().substring(1,
							userNameList.toString().length() - 1));
		}
		context.getExternalContext().getSessionMap().remove("testNameMap");
		context.getExternalContext().getSessionMap().remove("filesOpts");
		context.getExternalContext().getSessionMap().remove("marketIdMap");
		context.getExternalContext().getSessionMap().remove("filesIdsMap");
		context.getExternalContext().getSessionMap().put("testNameMap",
				testNameMap);
		setTestCaseName("Select Test Name");
		setReportType("Select Report");
		setMarketId("Select Market".split(","));
		setFilesIds("Select Files".split(","));

		ajaxResponseStr = numberOftimesCallsmade;
		return numberOftimesCallsmade;
	}

	public void fetchFiles() throws FileNotFoundException {
		/*
		 * File file = new File("/D:/Logme.txt"); PrintStream printStream = new
		 * PrintStream(new FileOutputStream(file)); System.setOut(printStream);
		 */
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, List<String>> marketwiseFiles = (Map<String, List<String>>) context
				.getExternalContext().getSessionMap().get("marketwiseFiles");
		if (null == marketwiseFiles) {
			marketwiseFiles = new LinkedHashMap<String, List<String>>();
		}
		String device = getDeviceName();
		String testName = getDeviceName();
		Map<String, String> filesIdsMap = new LinkedHashMap<String, String>();
		Map<String, String> marketFiles = new LinkedHashMap<String, String>();
		ReportDao rdao = new ReportDaoImpl();

		Map<String, List<String>> marketFileListMap = (HashMap<String, List<String>>) context
				.getExternalContext().getSessionMap().get("marketFileListMap");
		if (null == marketFileListMap) {
			marketFileListMap = new HashMap<String, List<String>>();
		}
		for (int i = 0; i < getMarketId().length; i++) {
			String market = getMarketId()[i];
			String reportType = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestParameterMap().get(
							"j_idt10:reportType");
			filesIdsMap.putAll(rdao.getFiles(getTestCaseName(),
					getDeviceName(), getReportType(), market));
			marketFileListMap.put(getDeviceName() + getTestCaseName() + market,
					rdao.getFilesForMarket(getTestCaseName(), getDeviceName(),
							getReportType(), market));
			marketwiseFiles.putAll(new ReportDaoImpl()
					.getFilesListForMarket(getTestCaseName(), getDeviceName(),
							getReportType(), market));
		}
		// logger.info("marketwiseFiles----------"+marketwiseFiles);
		context.getExternalContext().getSessionMap().put("marketwiseFiles",
				marketwiseFiles);
		context.getExternalContext().getSessionMap().put("filesIdsMap",
				filesIdsMap);
		context.getExternalContext().getSessionMap().put("marketFileListMap",
				marketFileListMap);
		setFilesIds("Select Files".split(","));
	}

	private void populateKpiDataForCharts(List<String> deviceNamse,
			String[] marketId, String testName,
			org.json.me.JSONArray jsoArrMain, List<String> allMarketsList,
			List<String> allTestsList, List<String> allFilesList,
			HashMap<String, List<String>> marketFileMap, int VQTCount,
			int FinalMOCount, int FinalFTPCount, int FinalExternalCount)
			throws Exception {

		logger.info("enetredd the kpi calculation**************");
		int maprenderVQCount = 0;
		int maprenderVCCount = 0;
		int maprenderDCCount = 0;
		int maprenderSimulateousCount = 0;
		VoiceQualityHealth vq = new VoiceQualityHealth();
		HashMap<String, List<HealthIndexTo>> healthIndexMap = new HashMap<String, List<HealthIndexTo>>();
		HashMap<String, List<HealthIndexTo>> finalAvgMap = new HashMap<String, List<HealthIndexTo>>();
		HashMap<String, HealthIndexTo> avgHealthIndexMap = new HashMap<String, HealthIndexTo>();
		HashMap<String, HashMap<String, List<HealthIndexTo>>> testWiseMarketMap = new HashMap<String, HashMap<String, List<HealthIndexTo>>>();
		Map<String, List<HealthIndexTo>> finalAvgListMap = new HashMap<String, List<HealthIndexTo>>();
		Data_VoiceKpiDao data_VoiceKpi = new Data_VoiceKpiDaoImpl();

		net.sf.json.JSONArray vcIdleModeJsonArray = new JSONArray();
		List callRetentionChart4GMoValueList2 = new ArrayList();
		net.sf.json.JSONObject callsetUpJson = new net.sf.json.JSONObject();
		List<String> callDropList = new ArrayList<String>();
		List<String> callDropListLte = new ArrayList<String>();
		List<Float> callDropdownLinkvalueList = new ArrayList<Float>();
		boolean isPolqa = false;
		boolean isPesq = false;

		// voice quality parameters
		List<String> categoriesList = new ArrayList<String>();
		List<Float> valueList = new ArrayList<Float>();
		List<Float> downLinkvalueList = new ArrayList<Float>();
		List<Float> tcpDownloadSpeedList = new ArrayList<Float>();
		List<Float> tcpDownloadSpeedRssList = new ArrayList<Float>();
		List<Float> ftpDownloadSpeedList = new ArrayList<Float>();
		List<Float> ftpTxDownloadSpeedList = new ArrayList<Float>();
		List<Float> ftpDownloadSpeedRssList = new ArrayList<Float>();
		List<Float> ftpUploadSpeedRssList = new ArrayList<Float>();
		HashMap<String, List<BrowserBean>> marketWiseBrowserData = new HashMap<String, List<BrowserBean>>();
		HashMap<String, List<VoipBean>> marketWiseVoipData = new HashMap<String, List<VoipBean>>();
		List<BrowserBean> browserBeanList = new ArrayList<BrowserBean>();
		List<BrowserBean> browserBeanListLte = new ArrayList<BrowserBean>();
		List<VoipBean> voipBeanList = new ArrayList<VoipBean>();
		//
		List<Float> tcpDriveDownloadSpeedList = new ArrayList<Float>();
		List<Float> tcpDriveDownloadSpeedRssList = new ArrayList<Float>();
		//
		// LTE speed
		List<String> categoriesListLte = new ArrayList<String>();
		List<Float> valueListLte = new ArrayList<Float>();
		List<Float> downLinkvalueListLte = new ArrayList<Float>();
		List<Float> tcpDownloadSpeedListLte = new ArrayList<Float>();
		List<Float> tcpDownloadSpeedRssListLte = new ArrayList<Float>();
		List<Float> ftpDownloadSpeedListLte = new ArrayList<Float>();
		List<Float> ftpDownloadSpeedRssListLte = new ArrayList<Float>();
		//

		List<Float> udpDownloadSpeedList = new ArrayList<Float>();
		List<Float> udpDownloadSpeedRssList = new ArrayList<Float>();
		List<Float> udpDownloadSpeedQosList = new ArrayList<Float>();

		//
		List<Float> udpDownloadSpeedListLte = new ArrayList<Float>();
		List<Float> udpDownloadSpeedRssListLte = new ArrayList<Float>();
		List<Float> udpDownloadSpeedQosListLte = new ArrayList<Float>();
		//

		List<Float> dnsAvgLatencyList = new ArrayList<Float>();
		List<Float> dnsMaxLatencyList = new ArrayList<Float>();
		List<Float> dnsRssList = new ArrayList<Float>();

		//
		List<Float> dnsAvgLatencyListLte = new ArrayList<Float>();
		List<Float> dnsMaxLatencyListLte = new ArrayList<Float>();
		List<Float> dnsRssListLte = new ArrayList<Float>();
		//

		//
		List<Float> voipUpstreamList = new ArrayList<Float>();
		List<Float> voipDownstreamList = new ArrayList<Float>();
		List<Float> voipSipbyeList = new ArrayList<Float>();
		List<Float> voipSipInviteList = new ArrayList<Float>();
		List<Float> voipSipRegisterList = new ArrayList<Float>();
		List<Float> voipRssList = new ArrayList<Float>();

		//

		// KPI 1
		net.sf.json.JSONArray jsoArr = new net.sf.json.JSONArray();
		net.sf.json.JSONArray jsoValArr = new net.sf.json.JSONArray();
		net.sf.json.JSONObject signalStrengthCat = new net.sf.json.JSONObject();
		net.sf.json.JSONObject signalStrengthCatLte = new net.sf.json.JSONObject();

		// KPI 1
		net.sf.json.JSONObject json = new net.sf.json.JSONObject();
		net.sf.json.JSONObject jsonVal = new net.sf.json.JSONObject();

		// Kpi2:Voice Conectivity

		JSONArray callSetupSignalStrengthvals = new JSONArray();

		//
		net.sf.json.JSONArray jsoArrLte = new net.sf.json.JSONArray();
		net.sf.json.JSONArray jsoValArrLte = new net.sf.json.JSONArray();
		net.sf.json.JSONObject jsonLte = new net.sf.json.JSONObject();
		net.sf.json.JSONObject jsonValLte = new net.sf.json.JSONObject();
		//

		FacesContext context = FacesContext.getCurrentInstance();

		net.sf.json.JSONObject lineChartJso = new net.sf.json.JSONObject();
		net.sf.json.JSONArray jsoDownlinkArr = new net.sf.json.JSONArray();
		net.sf.json.JSONObject jsonDownlinkVal = new net.sf.json.JSONObject();
		net.sf.json.JSONArray jsoDownlinkValArr = new net.sf.json.JSONArray();
		//

		// rvp
		net.sf.json.JSONArray callDropJsoArr = new net.sf.json.JSONArray();
		net.sf.json.JSONArray callDropJsoDownlinkValArr = new net.sf.json.JSONArray();

		net.sf.json.JSONArray callDropJsoArrLte = new net.sf.json.JSONArray();
		net.sf.json.JSONArray callDropJsoDownlinkValArrLte = new net.sf.json.JSONArray();

		//
		//
		net.sf.json.JSONObject lineChartJsoLte = new net.sf.json.JSONObject();
		net.sf.json.JSONArray jsoDownlinkArrLte = new net.sf.json.JSONArray();
		net.sf.json.JSONObject jsonDownlinkValLte = new net.sf.json.JSONObject();
		net.sf.json.JSONArray jsoDownlinkValArrLte = new net.sf.json.JSONArray();
		//

		categoriesList.add("pesq");
		categoriesList.add("pesqlq");
		categoriesList.add("pesqlqo");
		// categoriesList.add("pesqlb");

		//
		callDropList.add("Total calls * 10");
		callDropList.add("Dropped calls");
		callDropList.add("DCR %");
		callDropList.add("Access Failure");
		callDropList.add("Missed calls");

		callDropListLte.add("Total calls");
		callDropListLte.add("Dropped calls");
		callDropListLte.add("DCR %");
		//

		//
		categoriesListLte.add("Good");
		categoriesListLte.add("Medium");
		categoriesListLte.add("Poor");
		//
		// DeviceTo ref_deviceTo = new ReportDaoImpl().getRefDevice(testName);

		// //logger.info("marketId======1====="+marketId.length);
		net.sf.json.JSONObject mark_CatJson = new net.sf.json.JSONObject();
		net.sf.json.JSONObject linchartJson = new net.sf.json.JSONObject();
		JSONArray vqSignalStrengthvals = new JSONArray();
		List<VQTsTo> vqtTosList = new ArrayList<VQTsTo>();

		//by ankit
		net.sf.json.JSONObject mark_CatJsonUdp = new net.sf.json.JSONObject();
		// //logger.info("marketFileMap===1====="+marketFileMap);

		//
		net.sf.json.JSONObject callDrop_CatJson = new net.sf.json.JSONObject();
		net.sf.json.JSONObject callDrop_CatJsonLte = new net.sf.json.JSONObject();
		//

		//
		net.sf.json.JSONObject mark_CatJsonLte = new net.sf.json.JSONObject();
		net.sf.json.JSONObject linchartJsonLte = new net.sf.json.JSONObject();
		JSONArray vqSignalStrengthvalsLte = new JSONArray();
		List<VQTsTo> vqtTosListLte = new ArrayList<VQTsTo>();
		HashMap<String, List<String>> marketFileMapLte = (HashMap<String, List<String>>) context
				.getExternalContext().getSessionMap().get("marketFileListMap");// new
		// HashMap<String,
		// List<String>>();

		//
		List rangeList = new ArrayList();

		//
		List rangeListLte = new ArrayList();
		//

		rangeList.add("1-1.5");
		rangeList.add("1.5-2");
		rangeList.add("2-2.5");
		rangeList.add("2.5-3");
		rangeList.add("3-3.5");
		rangeList.add("3.5-4");
		rangeList.add("4-4.5");
		rangeList.add("4.5-5");

		//
		rangeListLte.add("1-1.5");
		rangeListLte.add("1.5-2");
		rangeListLte.add("2-2.5");
		rangeListLte.add("2.5-3");
		rangeListLte.add("3-3.5");
		rangeListLte.add("3.5-4");
		rangeListLte.add("4-4.5");
		rangeListLte.add("4.5-5");
		//

		String prevMarket = "";
		int numberMarkets = 0;

		//
		int numberMarketsLte = 0;
		//

		DataConnectivityDao dcDao = new DataConnectivityDaoImpl();
		net.sf.json.JSONArray dataConnectivitytcpDownloadArr = new net.sf.json.JSONArray();
		net.sf.json.JSONArray dataConnectivitytcpValArr = new net.sf.json.JSONArray();

		net.sf.json.JSONArray dataConnectivityudpDownloadArr = new net.sf.json.JSONArray();
		net.sf.json.JSONArray dataConnectivityudpValArr = new net.sf.json.JSONArray();

		net.sf.json.JSONArray dataConnectivitydnsDownloadArr = new net.sf.json.JSONArray();
		net.sf.json.JSONArray dataConnectivitydnsValArr = new net.sf.json.JSONArray();

		net.sf.json.JSONArray dataConnectivityftpDownloadArr = new net.sf.json.JSONArray();
		net.sf.json.JSONArray dataConnectivityftpValArr = new net.sf.json.JSONArray();
		net.sf.json.JSONArray dataConnectivityftpUploadValArr = new net.sf.json.JSONArray();

		//
		net.sf.json.JSONArray dataConnectivitytcpDownloadArrLte = new net.sf.json.JSONArray();
		net.sf.json.JSONArray dataConnectivitytcpValArrLte = new net.sf.json.JSONArray();

		net.sf.json.JSONArray dataConnectivityudpDownloadArrLte = new net.sf.json.JSONArray();
		net.sf.json.JSONArray dataConnectivityudpValArrLte = new net.sf.json.JSONArray();

		net.sf.json.JSONArray dataConnectivitydnsDownloadArrLte = new net.sf.json.JSONArray();
		net.sf.json.JSONArray dataConnectivitydnsValArrLte = new net.sf.json.JSONArray();

		net.sf.json.JSONArray dataConnectivityftpDownloadArrLte = new net.sf.json.JSONArray();
		net.sf.json.JSONArray dataConnectivityftpValArrLte = new net.sf.json.JSONArray();

		net.sf.json.JSONArray dataConnectivityVoipDownloadArr = new net.sf.json.JSONArray();
		net.sf.json.JSONArray dataConnectivityVoipValArr = new net.sf.json.JSONArray();

		//
		//

		net.sf.json.JSONArray dataConnectivitytcpDriveDownloadArr = new net.sf.json.JSONArray();
		net.sf.json.JSONArray dataConnectivitytcpDriveValArr = new net.sf.json.JSONArray();
		//
		// voice quality parameters ends

		// call setup parameters starts
		List<String> callSetupCategoriesList = new ArrayList<String>();
		List<Integer> callSetupValueList = new ArrayList<Integer>();
		List<Integer> callRetentionvalueList = new ArrayList<Integer>();

		net.sf.json.JSONArray callSetupJsoArr = new net.sf.json.JSONArray();
		net.sf.json.JSONArray callSetupJsoValArr = new net.sf.json.JSONArray();
		net.sf.json.JSONObject callSetupJson = new net.sf.json.JSONObject();
		net.sf.json.JSONObject callSetupJsonVal = new net.sf.json.JSONObject();

		net.sf.json.JSONArray callRetentionValArr = new net.sf.json.JSONArray();
		net.sf.json.JSONArray idleModeValArr = new net.sf.json.JSONArray();
		net.sf.json.JSONArray callTearDownValArr = new net.sf.json.JSONArray();
		net.sf.json.JSONObject callRetentionJson = new net.sf.json.JSONObject();
		net.sf.json.JSONObject callRetentionJsonVal = new net.sf.json.JSONObject();

		net.sf.json.JSONObject callSetup_mark_CatJson = new net.sf.json.JSONObject();

		net.sf.json.JSONObject callSetupLineChartJso = new net.sf.json.JSONObject();
		net.sf.json.JSONArray callSetupJsoDownlinkArr = new net.sf.json.JSONArray();
		net.sf.json.JSONObject callSetupJsonDownlinkVal = new net.sf.json.JSONObject();
		net.sf.json.JSONArray callSetupJsoDownlinkValArr = new net.sf.json.JSONArray();

		// callsetup lte
		net.sf.json.JSONObject callSetupLineChartJsoLte = new net.sf.json.JSONObject();

		//

		callSetupCategoriesList.add("MO");
		callSetupCategoriesList.add("MT");

		// call setup parameters ends

		// call setup lte parameters starts
		List<String> callSetupCategoriesListLte = new ArrayList<String>();
		List<Integer> callSetupValueListLte = new ArrayList<Integer>();
		List<Integer> callRetentionvalueListLte = new ArrayList<Integer>();

		net.sf.json.JSONArray callSetupJsoArrLte = new net.sf.json.JSONArray();
		net.sf.json.JSONArray callSetupJsoValArrLte = new net.sf.json.JSONArray();
		net.sf.json.JSONObject callSetupJsonLte = new net.sf.json.JSONObject();
		net.sf.json.JSONObject callSetupJsonValLte = new net.sf.json.JSONObject();

		net.sf.json.JSONArray callRetentionValArrLte = new net.sf.json.JSONArray();
		net.sf.json.JSONObject callRetentionJsonLte = new net.sf.json.JSONObject();
		net.sf.json.JSONObject callRetentionJsonValLte = new net.sf.json.JSONObject();

		net.sf.json.JSONObject callSetup_mark_CatJsonLte = new net.sf.json.JSONObject();

		JSONArray callSetupSignalStrengthvalsLte = new JSONArray();

		net.sf.json.JSONArray callSetupJsoDownlinkArrLte = new net.sf.json.JSONArray();
		net.sf.json.JSONObject callSetupJsonDownlinkValLte = new net.sf.json.JSONObject();
		net.sf.json.JSONArray callSetupJsoDownlinkValArrLte = new net.sf.json.JSONArray();

		callSetupCategoriesListLte.add("MO");
		callSetupCategoriesListLte.add("MT");

		// call setup lte parameters ends
		List<String> marketNameList = new ArrayList<String>();
		dcDao.openConn();
		String tUserId = context.getExternalContext().getSessionMap().get(
				"loggedInUserID").toString();
		String isretrievedConfig = (String) context.getExternalContext()
				.getSessionMap().get("retrievedConfig");
		QualityRangeTo qualityTo = new QualityRangeTo();
		QualityRangeTo qualityLteTo = new QualityRangeTo();
		// logger.info("reportConfigName-------------"+reportConfigName);

		qualityTo = new QualityRangeHelper().getNonLteQualityrange(rangeName);
		qualityLteTo = new QualityRangeHelper().getLteQualityrange(rangeName);

		int numberParameters = 0;
		int numberOfNetwork = 0;
		int numberFtpNetworks = 0;
		int curr = 0;
		int prev = 0;
		logger.info("sysout befor enterin lopp");
		// iterating over selected market list
		for (int j = 0; j < allMarketsList.size(); j++) {
			// logger.info("allMarketsList========================================"+allMarketsList);
			// fix for after removing some index. sample allMarketsList is
			// ,1,4,3
			if (!allMarketsList.get(j).equalsIgnoreCase("")) {

				String masterMarketId = allMarketsList.get(j).trim();
				// logger.info(j+"-------masterMarketId-----======="+masterMarketId);
				jsoArr = new net.sf.json.JSONArray();
				jsoValArr = new net.sf.json.JSONArray();
				jsoDownlinkValArr = new net.sf.json.JSONArray();

				// VOICE CONNECTIVITY KPI
				net.sf.json.JSONArray vcJsonArray = new JSONArray();
				vcIdleModeJsonArray = new JSONArray();
				callSetupSignalStrengthvals = new net.sf.json.JSONArray();
				callSetupSignalStrengthvalsLte = new net.sf.json.JSONArray();
				callDropJsoArr = new net.sf.json.JSONArray();
				callDropJsoDownlinkValArr = new net.sf.json.JSONArray();

				callDropJsoArrLte = new net.sf.json.JSONArray();
				callDropJsoDownlinkValArrLte = new net.sf.json.JSONArray();

				callTearDownValArr = new net.sf.json.JSONArray();
				idleModeValArr = new net.sf.json.JSONArray();

				// DATA CONNECTIVITY KPI
				dataConnectivitytcpValArr = new net.sf.json.JSONArray();
				dataConnectivitytcpDownloadArr = new net.sf.json.JSONArray();

				dataConnectivityudpValArr = new net.sf.json.JSONArray();
				dataConnectivityudpDownloadArr = new net.sf.json.JSONArray();

				dataConnectivitydnsValArr = new net.sf.json.JSONArray();
				dataConnectivitydnsDownloadArr = new net.sf.json.JSONArray();

				dataConnectivityftpValArr = new net.sf.json.JSONArray();
				dataConnectivityftpUploadValArr = new net.sf.json.JSONArray();
				dataConnectivityftpDownloadArr = new net.sf.json.JSONArray();

				dataConnectivityVoipValArr = new net.sf.json.JSONArray();
				dataConnectivityVoipDownloadArr = new net.sf.json.JSONArray();

				dataConnectivitytcpDriveValArr = new net.sf.json.JSONArray();
				dataConnectivitytcpDriveDownloadArr = new net.sf.json.JSONArray();

				dataConnectivitytcpValArrLte = new net.sf.json.JSONArray();
				dataConnectivitytcpDownloadArrLte = new net.sf.json.JSONArray();

				dataConnectivityudpValArrLte = new net.sf.json.JSONArray();
				dataConnectivityudpDownloadArrLte = new net.sf.json.JSONArray();

				dataConnectivitydnsValArrLte = new net.sf.json.JSONArray();
				dataConnectivitydnsDownloadArrLte = new net.sf.json.JSONArray();

				dataConnectivityftpValArrLte = new net.sf.json.JSONArray();
				dataConnectivityftpDownloadArrLte = new net.sf.json.JSONArray();

				vqSignalStrengthvals = new net.sf.json.JSONArray();
				vqSignalStrengthvalsLte = new net.sf.json.JSONArray();
				// //logger.info("masterMarketId===="+masterMarketId);
				String marketName = new ReportDaoImpl().getMarketName(
						masterMarketId).trim();
				marketNameList.add(marketName.trim());
				// //logger.info("marketName====1======="+marketName);
				
//============================================================				
				
				net.sf.json.JSONArray networkTypes = dcDao
						.getNetworksTypeJsonForMarket(masterMarketId,testName);
				
						//dcDao.getNetworksTypeForMarket(marketId);
				
				//System.out.print("by ank tcp networks  - ========="+networkTypes.toString());
				// voice quality market-category json array
				mark_CatJson = new net.sf.json.JSONObject();
				mark_CatJson.put("name", marketName);
				mark_CatJson.put("categories", networkTypes);

				net.sf.json.JSONArray networkTypesUdp = dcDao
						.getNetworksTypeJsonForUDPByMID_TN(masterMarketId,testName);
				
				//dcDao.getNetworksTypeForMarket(marketId);
				
//				System.out.print("by ank udp networks - ========="+networkTypesUdp.toString());
				// voice quality market-category json array
				mark_CatJsonUdp = new net.sf.json.JSONObject();
				mark_CatJsonUdp.put("name", marketName);
				mark_CatJsonUdp.put("categories", networkTypesUdp);
				
//=============================================================
				
				// rvp
				callDrop_CatJson = new net.sf.json.JSONObject();
				callDrop_CatJson.put("name", marketName);
				callDrop_CatJson.put("categories", networkTypes);

				callDrop_CatJsonLte = new net.sf.json.JSONObject();
				callDrop_CatJsonLte.put("name", marketName);
				callDrop_CatJsonLte.put("categories", networkTypes);

				// rvp

				// ftp networks
				net.sf.json.JSONObject ftpmark_CatJson = new net.sf.json.JSONObject();
				ftpmark_CatJson.put("name", marketName);
				ftpmark_CatJson.put("categories", new DataConnectivityDaoImpl()
						.getNetworksTypeJsonFtpForMarket(masterMarketId,
								testName));

				// lte speed market-category json array
				mark_CatJsonLte = new net.sf.json.JSONObject();
				mark_CatJsonLte.put("name", marketName);
				mark_CatJsonLte.put("categories", categoriesListLte);

				// callSetup market-category json array
				callSetup_mark_CatJson = new net.sf.json.JSONObject();
				callSetup_mark_CatJson.put("name", marketName);
				callSetup_mark_CatJsonLte.put("name", marketName);

				// dataConnectivity market-category json array
				dataConnectivitytcpDownloadArr.add(mark_CatJson);
				dataConnectivityftpDownloadArr.add(ftpmark_CatJson);
				dataConnectivitydnsDownloadArr.add(mark_CatJson);
				dataConnectivityudpDownloadArr.add(mark_CatJsonUdp);

				dataConnectivityVoipDownloadArr.add(mark_CatJson);

				//
				dataConnectivitytcpDriveDownloadArr.add(mark_CatJson);
				//
				// dataConnectivity LTE market-category json array
				dataConnectivitytcpDownloadArrLte.add(mark_CatJsonLte);
				dataConnectivitydnsDownloadArrLte.add(mark_CatJsonLte);
				dataConnectivityftpDownloadArrLte.add(mark_CatJsonLte);
				dataConnectivityudpDownloadArrLte.add(mark_CatJsonLte);

				// adding category List json object
				// logger.info("mark_CatJson----------"+mark_CatJson);

				callDrop_CatJson.put("categories", callDropList);
				callDrop_CatJsonLte.put("categories", callDropListLte);

				mark_CatJsonLte.put("categories", categoriesListLte);
				callSetup_mark_CatJson.put("categories",
						new ChartCategoryHelper().getcallSetupCategory());
				callSetup_mark_CatJsonLte.put("categories",
						new ChartCategoryHelper().getcallSetupCategoryLte());

				// iterating over selected device list
				for (int a = 0; a < deviceNamse.size(); a++) {
					String masterDeviceName = deviceNamse.get(a);
					// iterating over selected test list
					for (int b = 0; b < allTestsList.size(); b++) {
						String masterTesName = allTestsList.get(b);
						// iterating over selected file list
						HealthIndexTo healthIndexTo = new HealthIndexTo();
						// both KPI6 & KPI7 comments are only market wise. so
						// not included inside files iteration
						// KPI 6 - browser comments starts

						// KPI 6 browser comments ends

						for (int c = 0; c < allFilesList.size(); c++) {
							String masterFileName = allFilesList.get(c);
							// //logger.info("masterFileName========"+masterFileName).;

							// array list to hold chart data for each kpis
							valueList = new ArrayList<Float>();
							downLinkvalueList = new ArrayList<Float>();
							tcpDownloadSpeedList = new ArrayList<Float>();
							ArrayList<Float> tcpUploadSpeedList = new ArrayList<Float>();
							ftpDownloadSpeedList = new ArrayList<Float>();
							ftpTxDownloadSpeedList = new ArrayList<Float>();
							ArrayList<Float> wififtpDownloadSpeedList = new ArrayList<Float>();
							ArrayList<Float> wififtpDownloadSpeedRssList = new ArrayList<Float>();

							udpDownloadSpeedList = new ArrayList<Float>();
							tcpDownloadSpeedRssList = new ArrayList<Float>();
							ArrayList<Float> tcpWifiList = new ArrayList<Float>();
							ArrayList<Float> tcpWifiUploadList = new ArrayList<Float>();
							ArrayList<Float> tcpWifiRssiList = new ArrayList<Float>();
							ftpDownloadSpeedRssList = new ArrayList<Float>();
							ftpUploadSpeedRssList = new ArrayList<Float>();
							udpDownloadSpeedRssList = new ArrayList<Float>();
							udpDownloadSpeedQosList = new ArrayList<Float>();
							ArrayList<Float> udpUploadSpeedList = new ArrayList<Float>();
							ArrayList<Float> udpWifiDownloadSpeedList = new ArrayList<Float>();
							ArrayList<Float> udpWifiUploadSpeedList = new ArrayList<Float>();
							ArrayList<Float> udpWifiDownloadSpeedQosList = new ArrayList<Float>();
							ArrayList<Float> udpWifiDownloadSpeedRssiList = new ArrayList<Float>();
							// rvp
							callDropdownLinkvalueList = new ArrayList<Float>();
							// rvp
							// voip
							voipUpstreamList = new ArrayList<Float>();
							voipDownstreamList = new ArrayList<Float>();
							voipDownstreamList = new ArrayList<Float>();
							voipRssList = new ArrayList<Float>();

							// voip
							//
							tcpDriveDownloadSpeedList = new ArrayList<Float>();
							tcpDriveDownloadSpeedRssList = new ArrayList<Float>();
							ArrayList<Float> wifitcpDriveDownloadSpeedList = new ArrayList<Float>();
							ArrayList<Float> wifitcpDriveDownloadSpeedRssList = new ArrayList<Float>();
							//

							valueListLte = new ArrayList<Float>();
							downLinkvalueListLte = new ArrayList<Float>();
							tcpDownloadSpeedListLte = new ArrayList<Float>();
							ArrayList<Float> tcpUploadSpeedListLte = new ArrayList<Float>();
							udpDownloadSpeedListLte = new ArrayList<Float>();
							ArrayList<Float> udpUploadSpeedListLte = new ArrayList<Float>();
							tcpDownloadSpeedRssListLte = new ArrayList<Float>();
							ftpDownloadSpeedRssListLte = new ArrayList<Float>();
							udpDownloadSpeedRssListLte = new ArrayList<Float>();
							udpDownloadSpeedQosListLte = new ArrayList<Float>();
							ftpDownloadSpeedListLte = new ArrayList<Float>();
							ftpDownloadSpeedRssListLte = new ArrayList<Float>();
							//
							callSetupValueList = new ArrayList<Integer>();
							callRetentionvalueList = new ArrayList<Integer>();
							//
							dnsAvgLatencyList = new ArrayList<Float>();
							dnsMaxLatencyList = new ArrayList<Float>();
							dnsRssList = new ArrayList<Float>();
							ArrayList<Float> wifidnsAvgLatencyList = new ArrayList<Float>();
							ArrayList<Float> wifidnsMaxLatencyList = new ArrayList<Float>();
							ArrayList<Float> wifidnsRssList = new ArrayList<Float>();

							voipUpstreamList = new ArrayList<Float>();
							voipDownstreamList = new ArrayList<Float>();
							voipSipbyeList = new ArrayList<Float>();
							voipSipInviteList = new ArrayList<Float>();
							voipSipRegisterList = new ArrayList<Float>();
							voipRssList = new ArrayList<Float>();

							List<Float> wifivoipUpstreamList = new ArrayList<Float>();
							List<Float> wifivoipDownstreamList = new ArrayList<Float>();
							List<Float> wifivoipSipbyeList = new ArrayList<Float>();
							List<Float> wifivoipSipInviteList = new ArrayList<Float>();
							List<Float> wifivoipSipRegisterList = new ArrayList<Float>();
							List<Float> wifivoipRssList = new ArrayList<Float>();
							//
							dnsAvgLatencyListLte = new ArrayList<Float>();
							dnsMaxLatencyListLte = new ArrayList<Float>();
							dnsRssListLte = new ArrayList<Float>();
							//
							callSetupValueListLte = new ArrayList<Integer>();
							callRetentionvalueListLte = new ArrayList<Integer>();

							// adding 0 for the list to maintain category count
							for (int mar = 0; mar < (numberMarkets * 4); mar++) {
								// valueList.add(null);
								// downLinkvalueList.add(null);

								//
								valueListLte.add(null);
								downLinkvalueListLte.add(null);
								//
								callSetupValueList.add(null);
								callRetentionvalueList.add(null);
							}
							// adding 0 for the list to maintain category count
							for (int mar = 0; mar < (numberMarkets * 2); mar++) {
								callSetupValueListLte.add(null);
								callRetentionvalueListLte.add(null);
							}
							// adding 0 for the list to maintain category count
							// iterating json array from javascript
							for (int k = 0; k < jsoArrMain.length(); k++) {
								// logger.info("jsoArrMain.length(:"+jsoArrMain.length());
								int count = 0;
								int vqtCount = 0;
								int moCount = 0;
								int ftpCount = 0;
								int ExtCount = 0;
								org.json.me.JSONObject configObi = new org.json.me.JSONObject(
										jsoArrMain.get(k).toString());
								String jsonDeviceName = configObi
										.getString("deviceName");
								String jsonTestName = configObi
										.getString("testName");
								String markets = configObi
										.getString("marketName");
								String mrktId = markets.substring(1, markets
										.length() - 1);
								String mrktId_value[] = mrktId.split(",");
								count = count + mrktId_value.length;
								String tempFilesName = configObi.get(
										"filesName").toString().replaceAll(
										"\"", "").replace("[", "").replace("]",
										"");
								if (tempFilesName.contains("VQTResults")) {
									String vqtmar = configObi
											.getString("marketName");
									String vqtmrktId = markets.substring(1,
											markets.length() - 1);
									String vqtmrktId_value[] = mrktId
											.split(",");
									vqtCount = vqtCount
											+ vqtmrktId_value.length;
								}
								if (tempFilesName.contains("mo")) {
									String momar = configObi
											.getString("marketName");
									String momrktId = markets.substring(1,
											markets.length() - 1);
									String momrktId_value[] = mrktId.split(",");
									moCount = moCount + momrktId_value.length;
								}
								if (tempFilesName.contains("ftp")) {
									String ftpmar = configObi
											.getString("marketName");
									String ftpmrktId = markets.substring(1,
											markets.length() - 1);
									String ftpmrktId_value[] = mrktId
											.split(",");
									ftpCount = ftpCount
											+ ftpmrktId_value.length;
								}
								if (tempFilesName.contains("externaltest")) {
									String Extmar = configObi
											.getString("marketName");
									String ExtmrktId = markets.substring(1,
											markets.length() - 1);
									String ExtmrktId_value[] = mrktId
											.split(",");
									ExtCount = ExtCount
											+ ExtmrktId_value.length;
								}

								if (masterFileName.contains("NetTest")
										|| tempFilesName.contains("voip")) {
									context.getExternalContext()
											.getSessionMap().put("isVoip",
													"true");
								}
								if (masterFileName.contains("NetTest")
										|| tempFilesName.contains("browser")) {
									context.getExternalContext()
											.getSessionMap().put("isBrowser",
													"true");
								}

								List<String> jsonFilesList = new ArrayList<String>(
										Arrays.asList(tempFilesName.split(",")));

								// logger.info("configObi :"+configObi.toString());
								String tempMarketsList = configObi.get(
										"marketName").toString().trim()
										.replaceAll("\"", "").replace("[", "")
										.replace("]", "");
								// logger.info("tempMarketsList :"+tempMarketsList);

								List<String> tempjsonMarketsList = new ArrayList<String>(
										Arrays.asList(tempMarketsList
												.split(",")));
								List<String> jsonMarketsList = new ArrayList<String>();
								List<String> marketFilesList = marketFileMap
										.get(masterDeviceName + masterTesName
												+ masterMarketId);
								String filesStr = "";
								for (int i = 0; i < tempjsonMarketsList.size(); i++) {
									// logger.info(tempjsonMarketsList.get(i)+"----airometric------"+tempjsonMarketsList.get(i).length());
									jsonMarketsList.add(tempjsonMarketsList
											.get(i).trim());
								}
								// iterating json array element for each
								// combination to
								// construct chart data
								Map<String, List<String>> marketFiles = (Map<String, List<String>>) context
										.getExternalContext().getSessionMap()
										.get("marketwiseFiles");
								// logger.info("marketFiles :"+marketFiles);
								// logger.info("masterFileName :"+masterFileName);
								boolean isFilePresent = false;
								for (int i = 1; i < 3; i++) {
									if (null != marketFiles
											.get(masterDeviceName + "_"
													+ masterTesName + "_"
													+ masterMarketId + "_" + i)
											&& marketFiles.get(masterDeviceName+ "_"+ masterTesName+ "_"+ masterMarketId+ "_" + i)
													.contains(masterFileName.replaceAll("__--__",":"))) {
										isFilePresent = true;
									}
								}

								if ((null != jsonFilesList)
										&& jsonDeviceName
												.equalsIgnoreCase(masterDeviceName)
										&& jsonTestName
												.equalsIgnoreCase(masterTesName)
										&& jsonMarketsList
												.contains(masterMarketId)
										&& isFilePresent) {
									for (int i = 1; i < 3; i++) {
										if (null != marketFiles
												.get(masterDeviceName + "_"
														+ masterTesName + "_"
														+ masterMarketId + "_"
														+ i)) {
											marketFiles.get(
													masterDeviceName + "_"
															+ masterTesName
															+ "_"
															+ masterMarketId
															+ "_" + i).remove(
													masterFileName.replaceAll(
															"__--__", ":"));
										}
										// logger.info("inside inner for loop.......................................");
									}
									// logger.info("inside if loop...................................................");
									context.getExternalContext()
											.getSessionMap().put(
													"marketwiseFiles",
													marketFiles);

									net.sf.json.JSONObject deviceJson = new net.sf.json.JSONObject();
									net.sf.json.JSONObject tcpDownloadJson = new net.sf.json.JSONObject();
									net.sf.json.JSONObject udpDownloadJson = new net.sf.json.JSONObject();
									net.sf.json.JSONObject dnsDownloadJson = new net.sf.json.JSONObject();

									net.sf.json.JSONObject voipDownloadJson = new net.sf.json.JSONObject();

									// rvp
									net.sf.json.JSONObject callDropJson = new net.sf.json.JSONObject();
									net.sf.json.JSONObject callDropJsonLte = new net.sf.json.JSONObject();
									// rvp

									filesStr = filesStr + "," + masterFileName;
									//
									TCPChartTO  dcTcpMap = dcDao
											.populateDataForTCP(
													masterDeviceName,
													jsonTestName,
													masterMarketId);
									UDPChartTO dcUdpMap = dcDao
											.populateDataForUDP(
													masterDeviceName,
													jsonTestName,
													masterMarketId);
									HashMap<String, List<Float>> dcdnsMap = dcDao
											.populateDataForDNS(
													masterDeviceName,
													jsonTestName,
													masterMarketId);
									HashMap<String, List<Float>> dcFtpMap = dcDao
											.populateDataForFTP(
													masterDeviceName,
													jsonTestName,
													masterMarketId, "rx");
									HashMap<String, List<Float>> dcFtpUploadMap = dcDao
											.populateDataForFTP(
													masterDeviceName,
													jsonTestName,
													masterMarketId, "tx");
									/**
									 * uncomment below code for data kpis
									 */
									
									tcpDownloadSpeedList.addAll(dcTcpMap.getTcpDownloadCapacityList());
									tcpUploadSpeedList.addAll(dcTcpMap.getTcpUploadCapacityList());
									tcpDownloadSpeedRssList.addAll(dcTcpMap.getRessList());
									/*tcpWifiList.addAll(dcTcpMap.getWifiDownloadList());
									tcpWifiUploadList.addAll(dcTcpMap.getWifiUploadList());
									tcpWifiRssiList.addAll(dcTcpMap.getWifiRssiList());*/
									
									/*tcpDownloadSpeedList.addAll(dcTcpMap
											.get("tcpDownloadSpeedList"));
									tcpUploadSpeedList.addAll(dcTcpMap
											.get("tcpUploadSpeedList"));
									tcpDownloadSpeedRssList.addAll(dcTcpMap
											.get("ressList"));
									tcpWifiList
											.addAll(dcTcpMap.get("wifiList"));
									tcpWifiUploadList.addAll(dcTcpMap
											.get("wifiUploadList"));
									tcpWifiRssiList.addAll(dcTcpMap
											.get("wifirssiList"));*/
									
									udpDownloadSpeedList.addAll(dcUdpMap
											.getUdpDownloadCapacityList());
									udpUploadSpeedList.addAll(dcUdpMap
											.getUdpUploadCapacityList());
									udpDownloadSpeedQosList.addAll(dcUdpMap
											.getUdpDownloadQOSList());
									udpDownloadSpeedRssList.addAll(dcUdpMap
											.getRessList());
									//System.out.println("UDP part data from precal udp 1 -NetworkTypes- "+ dcUdpMap.getNetworkTypes());
									/*udpWifiDownloadSpeedList.addAll(dcUdpMap
											.getWifiDownloadList());
									udpWifiUploadSpeedList.addAll(dcUdpMap
											.getWifiUploadList());
									udpWifiDownloadSpeedQosList.addAll(dcUdpMap
											.getWifiQosList());
									udpWifiDownloadSpeedRssiList
											.addAll(dcUdpMap
													.getWifiRssiList());*/

									dnsAvgLatencyList.addAll(dcdnsMap
											.get("dnsAvgLatencyList"));
									dnsMaxLatencyList.addAll(dcdnsMap
											.get("dnsMaxLatencyList"));
									dnsRssList.addAll(dcdnsMap
											.get("dnsRssList"));
									wifidnsAvgLatencyList.addAll(dcdnsMap
											.get("wifidnsAvLatencyList"));
									wifidnsMaxLatencyList.addAll(dcdnsMap
											.get("wifidnsMaxLatencyList"));
									wifidnsRssList.addAll(dcdnsMap
											.get("wifiressList"));

									//
									/*
									 * HashMap<String,List<Float>> dcTcpMapLte =
									 * dcDao
									 * .populateDataForTCPLte(masterDeviceName,
									 * jsonTestName, masterMarketId);
									 * HashMap<String,List<Float>> dcUdpMapLte =
									 * dcDao
									 * .populateDataForUDPLte(masterDeviceName,
									 * jsonTestName, masterMarketId);
									 * HashMap<String,List<Float>> dcdnsMapLte =
									 * dcDao
									 * .populateDataForDNSLte(masterDeviceName,
									 * jsonTestName, masterMarketId);
									 * HashMap<String,List<Float>> dcFtpMapLte =
									 * dcDao
									 * .populateDataForFTPLte(masterDeviceName,
									 * jsonTestName, masterMarketId,new
									 * DataConnectivityDaoImpl
									 * ().getNetworksTypeListFtpForMarketForLte
									 * (masterMarketId));
									 */
									/**
									 * uncomment below code for data kpis
									 */
									/*
									 * tcpDownloadSpeedListLte.addAll(dcTcpMapLte
									 * .get("tcpDownloadSpeedList"));
									 * tcpUploadSpeedListLte
									 * .addAll(dcTcpMapLte.get
									 * ("tcpUploadSpeedList"));
									 * tcpDownloadSpeedRssListLte
									 * .addAll(dcTcpMapLte.get("ressList"));
									 */

									ftpDownloadSpeedList.addAll(dcFtpMap
											.get("ftpThroughputList"));
									ftpDownloadSpeedRssList.addAll(dcFtpMap
											.get("ftpRssList"));
									ftpTxDownloadSpeedList
											.addAll(dcFtpUploadMap
													.get("ftpThroughputList"));
									ftpUploadSpeedRssList.addAll(dcFtpUploadMap
											.get("ftpRssList"));
									/*
									 * //
									 * ftpDownloadSpeedListLte.addAll(dcFtpMapLte
									 * .get("ftpThroughputList"));
									 * ftpDownloadSpeedRssListLte
									 * .addAll(dcFtpMapLte.get("ftpRssList"));
									 * //
									 * udpDownloadSpeedListLte.addAll(dcUdpMapLte
									 * .get("udpDownloadCapacityList"));
									 * udpUploadSpeedListLte
									 * .addAll(dcUdpMapLte.get
									 * ("udpUploadCapacityList"));
									 * udpDownloadSpeedQosListLte
									 * .addAll(dcUdpMapLte
									 * .get("udpDownloadQOSList"));
									 * udpDownloadSpeedRssListLte
									 * .addAll(dcUdpMapLte.get("ressList"));
									 * 
									 * 
									 * 
									 * 
									 * 
									 * dnsAvgLatencyListLte.addAll(dcdnsMapLte.get
									 * ("dnsAvgLatencyList"));
									 * dnsMaxLatencyListLte
									 * .addAll(dcdnsMapLte.get
									 * ("dnsMaxLatencyList"));
									 * dnsRssListLte.addAll
									 * (dcdnsMapLte.get("dnsRssList"));
									 */
									HashMap<String, List<Float>> driveMap = data_VoiceKpi
											.getVoice_DataData(
													masterDeviceName,
													jsonTestName,
													masterMarketId);
									tcpDriveDownloadSpeedList.addAll(driveMap
											.get("tcpDriveDownloadSpeedList"));
									tcpDriveDownloadSpeedRssList
											.addAll(driveMap
													.get("ressDriveList"));
									wifitcpDriveDownloadSpeedList
											.addAll(driveMap
													.get("wifitcpDriveDownloadSpeedList"));
									wifitcpDriveDownloadSpeedRssList
											.addAll(driveMap
													.get("wifiressDriveList"));

									// call setup line chart ends

									// call setup lte line chart starts
									/*
									 * callSetupLineChartJsoLte.put("name",masterDeviceName
									 * +"_"+marketName+"_"+marketName+"_"+
									 * masterFileName); List
									 * callSetupLineChartValueListLte1 = new
									 * ArrayList();// new
									 * VoiceConnectivityDaoImpl
									 * ().getCallSetupTime3G(masterMarketId,
									 * masterDeviceName,
									 * masterFileName.replace("__--__", ":"));
									 * List callSetupLineChartValueListLte2 =
									 * new ArrayList();// new
									 * VoiceConnectivityDaoImpl
									 * ().getCallSetupTime4G(masterMarketId,
									 * masterDeviceName,
									 * masterFileName.replace("__--__", ":"));
									 * List callSetupLineChartValueListLte = new
									 * ArrayList();
									 * callSetupLineChartValueListLte
									 * .addAll(callSetupLineChartValueListLte1);
									 * callSetupLineChartValueListLte
									 * .addAll(callSetupLineChartValueListLte2);
									 * callSetupLineChartJsoLte.put("data",
									 * callSetupLineChartValueListLte);
									 * callSetupSignalStrengthvalsLte
									 * .add(callSetupLineChartJsoLte);
									 */
									// call setup lte line chart ends

									//
									/*
									 * valueListLte.addAll((new
									 * ReportDaoImpl().getUplinkParmatersForGraph
									 * (masterDeviceName,masterMarketId,
									 * jsonTestName, masterFileName )));
									 */
									//

									HashMap<String, List<Float>> detailsUplinkDataMap = new HashMap<String, List<Float>>();
									/*
									 * new
									* ReportDaoImpl
									* (
									* )
									* .
									* getUplinkDetailedValuesForGraphTable
									* (
									* masterDeviceName
									* ,
									* masterMarketId
									* ,
									* jsonTestName
									* ,
									* masterFileName
									* )
									* ;
									*/
									context.getExternalContext()
											.getSessionMap().put(
													"up_" + masterDeviceName
															+ "_"
															+ masterTesName,
													detailsUplinkDataMap);
									HashMap<String, List<Float>> detailsDownlinkDataMap = new ReportDaoImpl()
											.getDownlinkDetailedValuesForGraphTable(
													masterDeviceName,
													masterMarketId,
													jsonTestName,
													masterFileName);
									context.getExternalContext()
											.getSessionMap().put(
													"down_" + masterDeviceName
															+ "_"
															+ masterTesName,
													detailsDownlinkDataMap);
									// table data for Data connectivity Tcp
									HashMap<String, HashMap<String, HashMap<String, List<Float>>>> detailsDataMap = dcDao
											.populateDetailedDataForTCP(
													masterDeviceName,
													jsonTestName,
													masterMarketId);
									HashMap<String, HashMap<String, HashMap<String, HashMap<String, List<Float>>>>> marketWiseDcTcpSUmmary = new HashMap<String, HashMap<String, HashMap<String, HashMap<String, List<Float>>>>>();
									marketWiseDcTcpSUmmary.put(marketName,
											detailsDataMap);
									context.getExternalContext()
											.getSessionMap().put(
													"dcTcp_DownloadSpeed_"
															+ masterDeviceName
															+ "_"
															+ masterTesName
															+ "_" + marketName,
													detailsDataMap);
									context.getExternalContext()
											.getSessionMap().put(
													"dcTcp_UploadSpeed_"
															+ masterDeviceName
															+ "_"
															+ masterTesName
															+ "_" + marketName,
													detailsDataMap);

									context.getExternalContext()
											.getSessionMap().put(
													"marketWiseDcTcpSUmmary",
													marketWiseDcTcpSUmmary);

									context.getExternalContext()
											.getSessionMap().put(
													"dcTcp_rssi_"
															+ masterDeviceName
															+ "_"
															+ masterTesName
															+ "_" + marketName,
													detailsDataMap);
									//

									// for UDP Starts//
									HashMap<String, HashMap<String, List<Float>>> detailsDataMapForDC = null;// dcDao.populateDetailedDataForUDP(masterDeviceName,
									// jsonTestName,
									// masterMarketId);
									HashMap<String, HashMap<String, HashMap<String, List<Float>>>> detailsDataMapForUdpDC = null;// dcDao.populateDetailedDataForUDP(masterDeviceName,
									// jsonTestName,
									// masterMarketId);
									HashMap<String, HashMap<String, HashMap<String, HashMap<String, List<Float>>>>> marketWiseDcUdpSUmmary = new HashMap<String, HashMap<String, HashMap<String, HashMap<String, List<Float>>>>>();
									marketWiseDcUdpSUmmary.put(marketName,
											detailsDataMapForUdpDC);
									context.getExternalContext()
											.getSessionMap().put(
													"marketWiseDcUdpSUmmary",
													marketWiseDcUdpSUmmary);
									context.getExternalContext()
											.getSessionMap().put(
													"dcUdp_Qos_"
															+ masterDeviceName
															+ "_"
															+ masterTesName
															+ "_" + marketName,
													detailsDataMapForUdpDC);
									context.getExternalContext()
											.getSessionMap().put(
													"dcUdp_DownloadCapacity_"
															+ masterDeviceName
															+ "_"
															+ masterTesName
															+ "_" + marketName,
													detailsDataMapForUdpDC);
									context.getExternalContext()
											.getSessionMap().put(
													"dcUdp_UploadCapacity_"
															+ masterDeviceName
															+ "_"
															+ masterTesName
															+ "_" + marketName,
													detailsDataMapForUdpDC);
									context.getExternalContext()
											.getSessionMap().put(
													"dcUdp_rssi_"
															+ masterDeviceName
															+ "_"
															+ masterTesName
															+ "_" + marketName,
													detailsDataMapForUdpDC);
									// for udp ends//

									// for DNS Starts//
									HashMap<String, HashMap<String, HashMap<String, List<Float>>>> detailsDataMapForDns = null;// dcDao.populateDetailedDataForDNS(masterDeviceName,
									// jsonTestName,
									// masterMarketId);
									HashMap<String, HashMap<String, HashMap<String, HashMap<String, List<Float>>>>> marketWiseDcDnsSUmmary = new HashMap<String, HashMap<String, HashMap<String, HashMap<String, List<Float>>>>>();
									marketWiseDcDnsSUmmary.put(marketName,
											detailsDataMapForDns);
									context.getExternalContext()
											.getSessionMap().put(
													"marketWiseDcDnsSUmmary",
													marketWiseDcDnsSUmmary);
									context.getExternalContext()
											.getSessionMap().put(
													"dcDns_AvgLatency_"
															+ masterDeviceName
															+ "_"
															+ masterTesName
															+ "_" + marketName,
													detailsDataMapForDns);
									context.getExternalContext()
											.getSessionMap().put(
													"dcDns_MaxLatency_"
															+ masterDeviceName
															+ "_"
															+ masterTesName
															+ "_" + marketName,
													detailsDataMapForDns);
									context.getExternalContext()
											.getSessionMap().put(
													"dcDns_rssi_"
															+ masterDeviceName
															+ "_"
															+ masterTesName
															+ "_" + marketName,
													detailsDataMapForDns);
									// for DNS ends//
									// FTP
									HashMap<String, List<Float>> tempDataMap = new HashMap<String, List<Float>>();
									tempDataMap.put("Good", dcFtpMap
											.get("Good_rx"));
									tempDataMap.put("Poor", dcFtpMap
											.get("Poor_rx"));
									tempDataMap.put("Medium", dcFtpMap
											.get("Medium_rx"));
									context.getExternalContext()
											.getSessionMap().put(
													"dcFtp_rxThroughput_"
															+ masterDeviceName
															+ "_"
															+ masterTesName
															+ "_" + marketName,
													tempDataMap);
									tempDataMap = new HashMap<String, List<Float>>();
									tempDataMap.put("Good", dcFtpMap
											.get("Good_rssi"));
									tempDataMap.put("Poor", dcFtpMap
											.get("Poor_rssi"));
									tempDataMap.put("Medium", dcFtpMap
											.get("Medium_rssi"));
									context.getExternalContext()
											.getSessionMap().put(
													"dcFtp_rssi_"
															+ masterDeviceName
															+ "_"
															+ masterTesName
															+ "_" + marketName,
													tempDataMap);
									//
									// For LTE
									// Starts***********************************
									HashMap<String, HashMap<String, HashMap<String, List<Float>>>> detailsDataMapTCPLte = null;// dcDao.populateDetailedDataForTCPLte(masterDeviceName,
									// jsonTestName,
									// masterMarketId);
									HashMap<String, HashMap<String, HashMap<String, HashMap<String, List<Float>>>>> marketWiseDcTcpLteSUmmary = new HashMap<String, HashMap<String, HashMap<String, HashMap<String, List<Float>>>>>();
									marketWiseDcTcpLteSUmmary.put(marketName,
											detailsDataMapTCPLte);
									context.getExternalContext()
											.getSessionMap()
											.put("marketWiseDcTcpLteSUmmary",
													marketWiseDcTcpLteSUmmary);
									context.getExternalContext()
											.getSessionMap().put(
													"lteTcp_DownloadSpeed_"
															+ masterDeviceName
															+ "_"
															+ masterTesName
															+ "_" + marketName,
													detailsDataMapTCPLte);
									context.getExternalContext()
											.getSessionMap().put(
													"lteTcp_UploadSpeed_"
															+ masterDeviceName
															+ "_"
															+ masterTesName
															+ "_" + marketName,
													detailsDataMapTCPLte);
									context.getExternalContext()
											.getSessionMap().put(
													"lteTcp_rssi_"
															+ masterDeviceName
															+ "_"
															+ masterTesName
															+ "_" + marketName,
													detailsDataMapTCPLte);

									// for UDP Starts//
									HashMap<String, HashMap<String, HashMap<String, List<Float>>>> detailsDataMapForUDPLte = null;// dcDao.populateDetailedDataForUDPLte(masterDeviceName,
									// jsonTestName,
									// masterMarketId);
									HashMap<String, HashMap<String, HashMap<String, HashMap<String, List<Float>>>>> marketWiseDcUdpLteSUmmary = new HashMap<String, HashMap<String, HashMap<String, HashMap<String, List<Float>>>>>();
									marketWiseDcUdpLteSUmmary.put(marketName,
											detailsDataMapForUDPLte);
									context.getExternalContext()
											.getSessionMap()
											.put("marketWiseDcUdpLteSUmmary",
													marketWiseDcUdpLteSUmmary);
									context.getExternalContext()
											.getSessionMap().put(
													"lteUdp_Qos_"
															+ masterDeviceName
															+ "_"
															+ masterTesName
															+ "_" + marketName,
													detailsDataMapForUDPLte);
									context.getExternalContext()
											.getSessionMap().put(
													"lteUdp_DownloadCapacity_"
															+ masterDeviceName
															+ "_"
															+ masterTesName
															+ "_" + marketName,
													detailsDataMapForUDPLte);
									context.getExternalContext()
											.getSessionMap().put(
													"lteUdp_UploadCapacity_"
															+ masterDeviceName
															+ "_"
															+ masterTesName
															+ "_" + marketName,
													detailsDataMapForUDPLte);
									context.getExternalContext()
											.getSessionMap().put(
													"lteUdp_rssi_"
															+ masterDeviceName
															+ "_"
															+ masterTesName
															+ "_" + marketName,
													detailsDataMapForUDPLte);
									// for udp ends//

									// for DNS Starts//
									HashMap<String, HashMap<String, HashMap<String, List<Float>>>> detailsDataMapForDNSLte = null;// dcDao.populateDetailedDataForDNSLte(masterDeviceName,
									// jsonTestName,
									// masterMarketId);
									HashMap<String, HashMap<String, HashMap<String, HashMap<String, List<Float>>>>> marketWiseDcDnsLteSUmmary = new HashMap<String, HashMap<String, HashMap<String, HashMap<String, List<Float>>>>>();
									marketWiseDcDnsLteSUmmary.put(marketName,
											detailsDataMapForDNSLte);
									context.getExternalContext()
											.getSessionMap()
											.put("marketWiseDcDnsLteSUmmary",
													marketWiseDcDnsLteSUmmary);
									context.getExternalContext()
											.getSessionMap().put(
													"lteDns_AvgLatency_"
															+ masterDeviceName
															+ "_"
															+ masterTesName
															+ "_" + marketName,
													detailsDataMapForDNSLte);
									context.getExternalContext()
											.getSessionMap().put(
													"lteDns_MaxLatency_"
															+ masterDeviceName
															+ "_"
															+ masterTesName
															+ "_" + marketName,
													detailsDataMapForDNSLte);
									context.getExternalContext()
											.getSessionMap().put(
													"lteDns_rssi_"
															+ masterDeviceName
															+ "_"
															+ masterTesName
															+ "_" + marketName,
													detailsDataMapForDNSLte);
									// for DNS ends//
									// FTP
									/*
									 * context.getExternalContext().getSessionMap
									 * ().put(
									 * "lteFtp_Throughput_"+masterDeviceName +
									 * "_" + masterTesName+ "_"+marketName ,
									 * dcFtpMapLte);
									 * context.getExternalContext()
									 * .getSessionMap().put(
									 * "lteFtp_rssi_"+masterDeviceName + "_" +
									 * masterTesName+ "_"+marketName ,
									 * dcFtpMapLte);
									 */
									//
									// For LTE Ends
									// ******************************************
									// Voip Starts
									HashMap<String, HashMap<String, HashMap<String, List<Float>>>> detailsDataMapForVoipDc = null;// dcDao.populateDetailedDataForVOIP(masterDeviceName,
									// jsonTestName,
									// masterMarketId);

									// float vqHshMap = new
									// VoiceQualityHealth().itrHash(detailsDataMapForVoipDc);

									HashMap<String, HashMap<String, HashMap<String, HashMap<String, List<Float>>>>> marketWiseDcVoipSUmmary = new HashMap<String, HashMap<String, HashMap<String, HashMap<String, List<Float>>>>>();
									marketWiseDcVoipSUmmary.put(marketName,
											detailsDataMapForVoipDc);
									context.getExternalContext()
											.getSessionMap().put(
													"marketWiseDcVoipSUmmary",
													marketWiseDcVoipSUmmary);
									context.getExternalContext()
											.getSessionMap().put(
													"dcVoip_UpstreamMaxJitter_"
															+ masterDeviceName
															+ "_"
															+ masterTesName
															+ "_" + marketName,
													detailsDataMapForVoipDc);
									context.getExternalContext()
											.getSessionMap().put(
													"dcVoip_DownstreamMaxJitter_"
															+ masterDeviceName
															+ "_"
															+ masterTesName
															+ "_" + marketName,
													detailsDataMapForVoipDc);
									context.getExternalContext()
											.getSessionMap().put(
													"dcVoip_SIPBye_"
															+ masterDeviceName
															+ "_"
															+ masterTesName
															+ "_" + marketName,
													detailsDataMapForVoipDc);
									context.getExternalContext()
											.getSessionMap().put(
													"dcVoip_rssi_"
															+ masterDeviceName
															+ "_"
															+ masterTesName
															+ "_" + marketName,
													detailsDataMapForVoipDc);

									// voip Ends

									// VOICE AND DATA

									HashMap<String, HashMap<String, HashMap<String, List<Float>>>> detailsVoiceDataMap = null;// data_VoiceKpi.getDetailedVoiceData(masterDeviceName,
									// jsonTestName,
									// masterMarketId);
									HashMap<String, HashMap<String, HashMap<String, HashMap<String, List<Float>>>>> marketWiseDcVoiceDataSUmmary = new HashMap<String, HashMap<String, HashMap<String, HashMap<String, List<Float>>>>>();
									marketWiseDcVoiceDataSUmmary.put(
											marketName, detailsDataMap);
									context.getExternalContext()
											.getSessionMap().put(
													"vdTcp_DownloadSpeed_"
															+ masterDeviceName
															+ "_"
															+ masterTesName
															+ "_"
															+ masterTesName
															+ "_" + marketName,
													detailsVoiceDataMap);

									context
											.getExternalContext()
											.getSessionMap()
											.put(
													"marketWiseDcVoiceDataSUmmary",
													marketWiseDcVoiceDataSUmmary);

									context.getExternalContext()
											.getSessionMap().put(
													"vdTcp_rssi_"
															+ masterDeviceName
															+ "_"
															+ masterTesName
															+ "_"
															+ masterTesName
															+ "_" + marketName,
													detailsVoiceDataMap);

									// / VOICE AND DATA

									//
									net.sf.json.JSONObject tcpDriveDownloadJson = new net.sf.json.JSONObject();
									//

									HashMap<String, List<Float>> detailsDataMapLte = new HashMap<String, List<Float>>();/*
																														 * //
																														 * new
																														 * ReportDaoImpl
																														 * (
																														 * )
																														 * .
																														 * getUplinkDetailedValuesForGraphTable
																														 * (
																														 * masterDeviceName
																														 * ,
																														 * masterMarketId
																														 * ,
																														 * jsonTestName
																														 * ,
																														 * masterFileName
																														 * )
																														 * ;
																														 */
									context.getExternalContext()
											.getSessionMap()
											.put(
													"up_"
															+ masterDeviceName
															+ "_"
															+ masterTesName
															+ "_"
															+ masterFileName
																	.replaceAll(
																			"__--__",
																			":"),
													detailsDataMapLte);

									net.sf.json.JSONObject deviceJsonLte = new net.sf.json.JSONObject();
									net.sf.json.JSONObject tcpDownloadJsonLte = new net.sf.json.JSONObject();

									net.sf.json.JSONObject ftpDownloadJson = new net.sf.json.JSONObject();
									net.sf.json.JSONObject ftpUploadJson = new net.sf.json.JSONObject();

									net.sf.json.JSONObject ftpDownloadJsonLte = new net.sf.json.JSONObject();
									net.sf.json.JSONObject udpDownloadJsonLte = new net.sf.json.JSONObject();
									net.sf.json.JSONObject dnsDownloadJsonLte = new net.sf.json.JSONObject();

									/*
									 * deviceJsonLte.put("name",
									 * masterDeviceName + "_" + masterTesName +
									 * "_" + masterFileName.replaceAll("__--__",
									 * ":")); deviceJsonLte.put("data",
									 * valueListLte);
									 * 
									 * 
									 * jsoValArrLte.add(deviceJsonLte);
									 * 
									 * deviceJsonLte.put("data",
									 * downLinkvalueListLte);
									 * jsoDownlinkValArrLte.add(deviceJsonLte);
									 */

									// Removing thrice because the json may
									// contain the file name is fllowin formats
									// 1) "File_name" or
									// File_name" or "File_name
									// commented , handing at the beginning
									jsonFilesList.remove("\"" + masterFileName
											+ "\"");
									jsonFilesList.remove("\"" + masterFileName);
									jsonFilesList.remove(masterFileName + "\"");
									jsonFilesList.remove(masterFileName);
									configObi.put("filesName", jsonFilesList);
									// logger.info("configObi----++-----"
									// + configObi);
									jsoArrMain.put(k, configObi);

									// rvp
									callDropJson.put("name", masterDeviceName
											+ "_" + masterTesName);

									// rvp

									/**
									 * 
									 * Srikanth commented below code
									 */
									List lineChartValueListLte = new ArrayList();// new
									// ReportDaoImpl().getVQDetailsFor_LineForGraphLTE(masterDeviceName,masterMarketId,jsonTestName);
									lineChartJsoLte.put("connectNulls", true);
									lineChartJsoLte.put("name",
											masterDeviceName + "_"
													+ jsonTestName);
									lineChartJsoLte.put("data",
											lineChartValueListLte);

									// /////////////////////Google Maps
									// Implementation
									// Starts/////////////////////////////////////

									// logger.info("j----"+j+"vqtCount-------------"+vqtCount+"k-----------"+k);

									// if(k==0){
									if (masterFileName.contains("VQTResults")) {
										generateVoiceQualityMap(
												jsonTestName,
												"VQTResults",
												masterMarketId,
												masterDeviceName,
												masterFileName,
												new ReportDaoImpl()
														.getMarketName(masterMarketId),
												maprenderVQCount);
										context.getExternalContext()
												.getSessionMap().put(
														"isVoiceData", "true");
										maprenderVQCount++;
									}
									if (masterFileName.contains("_mo")) {
										String tempTestType = "mo";

										if ((masterFileName.contains("&"))
												&& (!masterFileName
														.contains("VQTResults"))) {
											tempTestType = "externaltest";
										}
										System.out
												.println("@@@@@@@@@@@@@@@@@@@@entered map--"
														+ masterMarketId);
										generateVoiceConnectivityMap(
												jsonTestName,
												tempTestType,
												masterMarketId,
												masterDeviceName,
												masterFileName,
												new ReportDaoImpl()
														.getMarketName(masterMarketId),
												tempTestType, maprenderVCCount);
										context.getExternalContext()
												.getSessionMap().put(
														"isVoiceData", "true");
										maprenderVCCount++;
										System.out
												.println("@@@@@@@@@@@@@@@@@@@@@@2ended map");
									}
									if (masterFileName.contains("NetTest")
											|| masterFileName.contains("ftp")) {
										generateDataConnectivityMap(
												jsonTestName,
												"ftp",
												masterMarketId,
												masterDeviceName,
												masterFileName,
												new ReportDaoImpl()
														.getMarketName(masterMarketId),
												maprenderDCCount);
										context.getExternalContext()
												.getSessionMap()
												.put("isNetTestData", "true");
										maprenderDCCount++;
									}
									if (masterFileName.contains("externaltest")) {
										generateSignalStrenghtVoice_DataMap(
												jsonTestName,
												"externaltest",
												masterMarketId,
												masterDeviceName,
												masterFileName,
												new ReportDaoImpl()
														.getMarketName(masterMarketId),
												maprenderSimulateousCount);
										context.getExternalContext()
												.getSessionMap().put(
														"isExternalData",
														"true");
										maprenderSimulateousCount++;
									}
									/*
									 * }else{
									 * if(masterFileName.contains("VQTResults"
									 * )){ if(vqtCount==1){
									 * generateVoiceQualityMap
									 * (jsonTestName,"VQTResults",
									 * masterMarketId
									 * ,masterDeviceName,masterFileName,new
									 * ReportDaoImpl
									 * ().getMarketName(masterMarketId
									 * ),vqtCount);
									 * context.getExternalContext().
									 * getSessionMap().put("isVoiceData" ,
									 * "true"); }else{
									 * generateVoiceQualityMap(jsonTestName
									 * ,"VQTResults",
									 * masterMarketId,masterDeviceName
									 * ,masterFileName,new
									 * ReportDaoImpl().getMarketName
									 * (masterMarketId),vqtCount+j);
									 * context.getExternalContext
									 * ().getSessionMap().put("isVoiceData" ,
									 * "true"); } }
									 * if(masterFileName.contains("mo")){
									 * if(moCount==1){
									 * generateVoiceConnectivityMap
									 * (jsonTestName,"'mo'",
									 * masterMarketId,masterDeviceName
									 * ,masterFileName,new
									 * ReportDaoImpl().getMarketName
									 * (masterMarketId),"mo",moCount);
									 * context.getExternalContext
									 * ().getSessionMap().put("isVoiceData" ,
									 * "true"); }else{
									 * generateVoiceConnectivityMap
									 * (jsonTestName,"'mo'",
									 * masterMarketId,masterDeviceName
									 * ,masterFileName,new
									 * ReportDaoImpl().getMarketName
									 * (masterMarketId),"mo",moCount+j);
									 * context.
									 * getExternalContext().getSessionMap
									 * ().put("isVoiceData" , "true"); } }
									 * if(j==0){
									 * if(masterFileName.contains("NetTest"
									 * )||masterFileName.contains("ftp")){
									 * generateDataConnectivityMap
									 * (jsonTestName,"ftp",
									 * masterMarketId,masterDeviceName
									 * ,masterFileName,new
									 * ReportDaoImpl().getMarketName
									 * (masterMarketId),FinalFTPCount-(j+1)); }
									 * }else{
									 * if(masterFileName.contains("NetTest"
									 * )||masterFileName.contains("ftp")){
									 * generateDataConnectivityMap
									 * (jsonTestName,"ftp",
									 * masterMarketId,masterDeviceName
									 * ,masterFileName,new
									 * ReportDaoImpl().getMarketName
									 * (masterMarketId),FinalFTPCount-(j+1)); }
									 * }
									 * 
									 * 
									 * 
									 * 
									 * if(masterFileName.contains("externaltest")
									 * ){generateSignalStrenghtVoice_DataMap(
									 * jsonTestName,"externaltest",
									 * masterMarketId
									 * ,masterDeviceName,masterFileName,new
									 * ReportDaoImpl
									 * ().getMarketName(masterMarketId
									 * ),ExtCount+j);
									 * context.getExternalContext(
									 * ).getSessionMap().put("isExternalData" ,
									 * "true"); } }
									 */

									// /////////////////////Google Maps
									// Implementation
									// Ends/////////////////////////////////////
									healthIndexTo.setDeviceName(jsonDeviceName);
									if (masterFileName.contains("VQT")) {
										List<Float> tempDownLinkList = new ArrayList<Float>();
										List<Float> tempUpLinkList = new ArrayList<Float>();
										String chartType = new VoiceQualityDaoImpl()
												.getchartType(jsonTestName,
														masterMarketId);
										logger.info("chartType====="
												+ chartType);
										if (chartType.equalsIgnoreCase("pesq")) {
											isPesq = true;
										} else {
											isPolqa = true;
										}
										if (true) {
											if (!isPolqa) {
												System.out
														.println("inside pesq");
												context
														.getExternalContext()
														.getSessionMap()
														.put(
																"VQbucketlinecharttitle",
																"PESQ Values");
												categoriesList.set(0, "pesq");
												categoriesList.set(1, "pesqlq");
												categoriesList
														.set(2, "pesqlqo");
												mark_CatJson.put("categories",
														categoriesList);
												tempUpLinkList = new ReportDaoImpl()
														.getUplinkParmatersForGraph(
																masterMarketId,
																jsonTestName);
												if (tempUpLinkList.size() > 0) {
													valueList
															.addAll(tempUpLinkList);
													deviceJson
															.put(
																	"name",
																	masterDeviceName
																			+ "_"
																			+ masterTesName);
													deviceJson.put("data",
															valueList);
													jsoValArr.add(deviceJson);

													List lineChartValueList = new ReportDaoImpl()
															.getVQDetailsFor_LineForGraph(
																	masterDeviceName,
																	masterMarketId,
																	jsonTestName);
													lineChartJso.put(
															"connectNulls",
															true);
													lineChartJso
															.put(
																	"name",
																	masterDeviceName
																			+ "_"
																			+ jsonTestName);
													lineChartJso.put("data",
															lineChartValueList);
													tempDownLinkList = new ReportDaoImpl()
															.getDownlinkParmatersForGraph(
																	masterDeviceName,
																	masterMarketId,
																	jsonTestName,
																	masterFileName);
													downLinkvalueList
															.addAll(tempDownLinkList);
													deviceJson.put("data",
															downLinkvalueList);
													jsoDownlinkValArr
															.add(deviceJson);
													vqSignalStrengthvals
															.add(lineChartJso);

													valueListLte
															.addAll((new ReportDaoImpl()
																	.getDownlinkParmatersForGraph(
																			masterMarketId,
																			jsonTestName)));
													deviceJsonLte
															.put(
																	"name",
																	masterDeviceName
																			+ "_"
																			+ masterTesName
																			+ "_"
																			+ masterFileName
																					.replaceAll(
																							"__--__",
																							":"));
													deviceJsonLte.put("data",
															valueListLte);
													jsoValArrLte
															.add(deviceJsonLte);

													downLinkvalueListLte
															.addAll((new ReportDaoImpl()
																	.getDownlinkParmatersForGraph(
																			masterDeviceName,
																			masterMarketId,
																			jsonTestName,
																			masterFileName)));
													deviceJsonLte
															.put("data",
																	downLinkvalueListLte);
													jsoDownlinkValArrLte
															.add(deviceJsonLte);

													vqSignalStrengthvalsLte
															.add(lineChartJsoLte);

													List<String> avgVQForMarketList = vq
															.voiceQualityHealthJson(detailsDownlinkDataMap
																	.get("pesq"));
													float avgVQForMarket = Float
															.parseFloat(avgVQForMarketList
																	.get(0));
													healthIndexTo
															.setVoicequalityHealthIndex(avgVQForMarket);
													healthIndexTo
															.setVqAvgValue(avgVQForMarketList
																	.get(1));
													isPesq = true;
												}
											}
											if (!isPesq) {
												context
														.getExternalContext()
														.getSessionMap()
														.put(
																"VQbucketlinecharttitle",
																"POLQA Values");
												categoriesList.set(0,
														"Avg Polqa");
												categoriesList.set(1,
														"Max Polqa");
												categoriesList.set(2,
														"emodel * 10");
												// categoriesList.set(2,
												// "speech/noise");
												mark_CatJson.put("categories",
														categoriesList);
												List lineChartValueList = new VoiceQualityDaoImpl()
														.getVQDetailsFor_LineForGraphForPolqa(
																masterDeviceName,
																masterMarketId,
																jsonTestName);
												lineChartJso.put(
														"connectNulls", true);
												lineChartJso.put("name",
														masterDeviceName + "_"
																+ jsonTestName);
												lineChartJso.put("data",
														lineChartValueList);
												tempUpLinkList = new ReportDaoImpl()
														.getUplinkParmatersForGraph(
																masterMarketId,
																jsonTestName);
												valueList
														.addAll(tempUpLinkList);
												deviceJson
														.put(
																"name",
																masterDeviceName
																		+ "_"
																		+ masterTesName);
												deviceJson.put("data",
														valueList);
												jsoValArr.add(deviceJson);

												tempDownLinkList = new ReportDaoImpl()
														.getDownlinkParmatersForGraph(
																masterMarketId,
																jsonTestName);
												downLinkvalueList
														.addAll(tempDownLinkList);
												deviceJson.put("data",
														downLinkvalueList);
												jsoDownlinkValArr
														.add(deviceJson);
												vqSignalStrengthvals
														.add(lineChartJso);
												System.out
														.println("jsoDownlinkValArr-------jsoDownlinkValArr----------"
																+ vqSignalStrengthvals);

												vqSignalStrengthvalsLte
														.add(lineChartJsoLte);

												List<String> avgVQForMarketList = vq
														.voiceQualityHealthJson(detailsDownlinkDataMap
																.get("pesq"));
												float avgVQForMarket = Float
														.parseFloat(avgVQForMarketList
																.get(0));
												healthIndexTo
														.setVoicequalityHealthIndex(avgVQForMarket);
												healthIndexTo
														.setVqAvgValue(avgVQForMarketList
																.get(1));
												isPolqa = true;

											}
										}
										if (isPesq && isPolqa) {
											context.getExternalContext()
													.getSessionMap().put(
															"isBothPesqPolqa",
															"true");
										}
										// for VQT
									}

									/*
									 * callDropJson.put("name", masterDeviceName
									 * + "_" + masterTesName + "_" +
									 * masterFileName.replaceAll("__--__",
									 * ":"));callDropdownLinkvalueList.addAll(
									 * callRetentionChart4GMoValueList2);
									 * callDropJson.put("data",
									 * callDropdownLinkvalueList);
									 */
									if (masterFileName.contains("mo")) {
										System.out
												.println("start char##########################");
										List callSetupLineChartValueList = new ArrayList();
										net.sf.json.JSONObject idleModeJson = new net.sf.json.JSONObject();
										net.sf.json.JSONObject callTearDownJson = new net.sf.json.JSONObject();
										callSetupLineChartJso = new net.sf.json.JSONObject();
										callRetentionJson = new net.sf.json.JSONObject();
										// Map<String, List<CallSetUpTo>>
										// voiceConnectivityMap = new
										// VoiceConnectivityDaoImpl().getcallSetupChartDetails(
										// jsonTestName, "mo", masterMarketId);
										List callSetupLineChartMoValueList2 = new VoiceConnectivityDaoImpl()
												.getCallSetupTime(jsonTestName,
														masterMarketId).get(
														"mo");
										// call setup and retention lte

										callSetupLineChartJsoLte = new net.sf.json.JSONObject();
										callRetentionJsonLte = new net.sf.json.JSONObject();
										// call setup and retention lte

										callsetUpJson = new net.sf.json.JSONObject();
										List<Integer> categoryVals = new ArrayList<Integer>();
										for (int i = 1; i < 30; i++) {
											categoryVals.add(i);
										}
										callsetUpJson.put("name",
												masterDeviceName + "_"
														+ jsonTestName);
										callsetUpJson.put("categories",
												categoryVals);

										// vcJsonArray.add(callsetUpJson);

										callRetentionChart4GMoValueList2 = new VoiceConnectivityDaoImpl()
												.getCallRetentionTime(
														jsonTestName,
														masterMarketId).get(
														"mo");
										List callRetentionChart4GLte = new VoiceConnectivityDaoImpl()
												.getCallRetentionTime4G(
														jsonTestName,
														masterMarketId).get(
														"mo");
										callDropJson.put("name",
												masterDeviceName + "_"
														+ masterTesName);
										callSetupLineChartJso.put("name",
												masterDeviceName + "_"
														+ jsonTestName);
										callSetupLineChartJso.put("data",
												callSetupLineChartMoValueList2);
										callSetupSignalStrengthvals
												.add(callSetupLineChartJso);
										System.out.println("callSetupSignalStrengthvals------sri------"
														+ callSetupSignalStrengthvals);

										//
										List callSetupLineChartLte = new VoiceConnectivityDaoImpl()
												.getCallSetupTime4G(
														jsonTestName,
														masterMarketId).get(
														"mo");
										callSetupLineChartJsoLte.put("name",
												masterDeviceName + "_"
														+ jsonTestName);
										callSetupLineChartJsoLte.put("data",
												callSetupLineChartLte);
										callSetupSignalStrengthvalsLte
												.add(callSetupLineChartJsoLte);
										//

										callRetentionJson.put("name",
												masterDeviceName + "_"
														+ jsonTestName);
										// callSetupLineChartValueList.addAll(callSetupLineChartMtValueList1);
										callRetentionJson
												.put("data",
														callRetentionChart4GMoValueList2);
										callRetentionValArr
												.add(callRetentionJson);
										List idleTimeList = new ArrayList();// VoiceConnectivityDaoImpl().getIdleTimeOutOfCoverage(masterDeviceName,
										// masterMarketId,
										// jsonTestName,"mo");
										// List callTearDownList = new
										// VoiceConnectivityDaoImpl().getCallTearDownTime(voiceConnectivityMap.get("callTearDownDetails")).get("mo");
										List callTearDownList = new ArrayList();// new
										// VoiceConnectivityDaoImpl().getCallTearDownTime(jsonTestName,masterMarketId).get("mo");

										callDropJson.put("name",
												masterDeviceName + "_"
														+ jsonTestName);
										callDropJson
												.put("data",
														callRetentionChart4GMoValueList2);
										callDropJsoDownlinkValArr
												.add(callDropJson);

										//
										callDropJsonLte.put("name",
												masterDeviceName + "_"
														+ jsonTestName);
										callDropJsonLte.put("data",
												callRetentionChart4GLte);
										callDropJsoDownlinkValArrLte
												.add(callDropJsonLte);
										//
										net.sf.json.JSONObject IdleCatJson = new net.sf.json.JSONObject();
										List<Integer> idleModeCategoryVals = new ArrayList<Integer>();
										for (int i = 1; i < callSetupLineChartMoValueList2
												.size() + 1; i++) {
											idleModeCategoryVals.add(i);
										}
										IdleCatJson.put("name", marketName);
										IdleCatJson.put("categories",
												idleModeCategoryVals);

										vcIdleModeJsonArray.add(IdleCatJson);
										idleModeJson.put("name",
												masterDeviceName + "_"
														+ jsonTestName);
										idleModeJson.put("data", idleTimeList);
										idleModeValArr.add(idleModeJson);
										System.out
												.println("vcIdleModeJsonArray--------------"
														+ vcIdleModeJsonArray);

										callTearDownJson.put("name",
												masterDeviceName + "_"
														+ jsonTestName);
										callTearDownJson.put("data",
												callTearDownList);
										callTearDownValArr
												.add(callTearDownJson);

										List<String> avgVQForMarketList = vq
												.voiceConnectivityHealthIndex(callRetentionChart4GMoValueList2);
										float avgVCForMarket = Float
												.parseFloat(avgVQForMarketList
														.get(0));
										healthIndexTo
												.setVoiceConnectivityHealthIndex(avgVCForMarket);// for
										// Voice
										// Connectivity
										healthIndexTo
												.setVcAvgValue(avgVQForMarketList
														.get(1));
										System.out
												.println("end char##########################");
									}

									/********* DataConnectivity ***********/

									if (masterFileName.contains("NetTest")
											|| masterFileName.contains("voip")) {
										voipBeanList = marketWiseVoipData
												.get(marketName
														+ masterDeviceName);
										if (null == voipBeanList) {
											voipBeanList = new ArrayList<VoipBean>();
										}
										// KPI 7 - voIP comments starts
										List<VoipBean> voipValueList = new VoipDaoImpl()
												.getVoipDetails(
														masterDeviceName,
														masterMarketId,
														masterTesName,
														marketName);
										voipBeanList.addAll(voipValueList);
										marketWiseVoipData.put(marketName
												+ masterDeviceName,
												voipBeanList);
										// KPI 7 voIP comments ends
									}
									if (masterFileName.contains("NetTest")
											|| masterFileName
													.contains("browser")) {
										browserBeanList = new ArrayList<BrowserBean>();
										List<BrowserBean> browserValueList = new BrowserDaoImpl()
												.getBrowserDetails(
														masterDeviceName,
														masterMarketId,
														masterTesName,
														marketName);
										// browserBeanList.addAll(browserValueList);
										browserBeanList = marketWiseBrowserData
												.get(marketName
														+ masterDeviceName);
										if (null == browserBeanList) {
											browserBeanList = new ArrayList<BrowserBean>();
										}
										browserBeanList
												.addAll(browserValueList);
										List<BrowserBean> browserValueListLte = new BrowserDaoImpl()
												.getBrowserDetailsForLte(
														masterDeviceName,
														masterMarketId,
														masterTesName,
														marketName);
										// browserBeanListLte.addAll(browserValueListLte);
										// browserBeanList.addAll(browserValueListLte);
										marketWiseBrowserData.put(marketName
												+ masterDeviceName,
												browserBeanList);
										System.out
												.println("marketWiseBrowserData-----++-------"
														+ browserBeanList
																.get(0)
																.getAvgTime());
									}
									if (masterFileName.contains("NetTest")
											|| masterFileName.contains("ftp")) {
										// voip
										HashMap<String, List<Float>> dcVoipMap = dcDao
												.populateDataForVOIP(
														masterDeviceName,
														jsonTestName,
														masterMarketId);
										//
										// voip
										voipUpstreamList.addAll(dcVoipMap
												.get("upstreamJitterMaxList"));
										voipDownstreamList
												.addAll(dcVoipMap
														.get("downstreamJitterMaxList"));
										voipSipbyeList.addAll(dcVoipMap
												.get("sipByeList"));
										voipSipInviteList.addAll(dcVoipMap
												.get("sipInviteList"));
										voipSipRegisterList.addAll(dcVoipMap
												.get("sipRegisterList"));
										voipRssList.addAll(dcVoipMap
												.get("ressList"));

										wifivoipUpstreamList
												.addAll(dcVoipMap
														.get("wifiupstreamJitterMaxList"));
										wifivoipDownstreamList
												.addAll(dcVoipMap
														.get("wifidownstreamJitterMaxList"));
										wifivoipSipbyeList.addAll(dcVoipMap
												.get("wifisipByeList"));
										wifivoipSipInviteList.addAll(dcVoipMap
												.get("wifisipInviteList"));
										wifivoipSipRegisterList
												.addAll(dcVoipMap
														.get("wifisipRegisterList"));
										wifivoipRssList.addAll(dcVoipMap
												.get("wifiressList"));
										// voip ends
										tcpDownloadJson.put("data",
												tcpDownloadSpeedList);
										tcpDownloadJson.put("name",
												"DownloadSpeed_"
														+ masterDeviceName
														+ "_" + masterTesName);
										dataConnectivitytcpValArr
												.add(tcpDownloadJson);
										tcpDownloadJson.put("data",
												tcpUploadSpeedList);
										tcpDownloadJson.put("name",
												"UploadSpeed_"
														+ masterDeviceName
														+ "_" + masterTesName);
										dataConnectivitytcpValArr
												.add(tcpDownloadJson);
										tcpDownloadJson
												.put("data", tcpWifiList);
										tcpDownloadJson.put("name",
												"wifi_DownloadSpeed_"
														+ masterDeviceName
														+ "_" + masterTesName);
										dataConnectivitytcpValArr
												.add(tcpDownloadJson);
										tcpDownloadJson.put("data",
												tcpWifiUploadList);
										tcpDownloadJson.put("name",
												"wifi_UploadSpeed_"
														+ masterDeviceName
														+ "_" + masterTesName);
										dataConnectivitytcpValArr
												.add(tcpDownloadJson);
										tcpDownloadJson.put("data",
												tcpWifiRssiList);
										tcpDownloadJson.put("name",
												"wifi_rssi_" + masterDeviceName
														+ "_" + masterTesName);
										tcpDownloadJson.put("type", "scatter");
										tcpDownloadJson.put("yAxis", 1);
										dataConnectivitytcpValArr
												.add(tcpDownloadJson);
										tcpDownloadJson.put("data",
												tcpDownloadSpeedRssList);
										tcpDownloadJson.put("name", "rssi_"
												+ masterDeviceName + "_"
												+ masterTesName);
										tcpDownloadJson.put("type", "scatter");
										tcpDownloadJson.put("yAxis", 1);
										dataConnectivitytcpValArr
												.add(tcpDownloadJson);

										// UDP
										/*
										 * udpDownloadJson.put("data",
										 * udpDownloadSpeedQosList);
										 * udpDownloadJson.put("name",
										 * "Qos_"+masterDeviceName + "_" +
										 * masterTesName);
										 */
										// dataConnectivityudpValArr.add(udpDownloadJson);
										udpDownloadJson.put("data",
												udpDownloadSpeedList);
										udpDownloadJson.put("name",
												"DownloadCapacity_"
														+ masterDeviceName
														+ "_" + masterTesName);
										dataConnectivityudpValArr
												.add(udpDownloadJson);

										udpDownloadJson.put("data",
												udpUploadSpeedList);
										udpDownloadJson.put("name",
												"UploadCapacity_"
														+ masterDeviceName
														+ "_" + masterTesName);
										dataConnectivityudpValArr
												.add(udpDownloadJson);

										udpDownloadJson.put("data",
												udpWifiDownloadSpeedList);
										udpDownloadJson.put("name",
												"wifi_DownloadCapacity_"
														+ masterDeviceName
														+ "_" + masterTesName);
										dataConnectivityudpValArr
												.add(udpDownloadJson);

										udpDownloadJson.put("data",
												udpWifiUploadSpeedList);
										udpDownloadJson.put("name",
												"wifi_UploadCapacity_"
														+ masterDeviceName
														+ "_" + masterTesName);
										dataConnectivityudpValArr
												.add(udpDownloadJson);

										/*
										 * udpDownloadJson.put("data",
										 * udpWifiDownloadSpeedQosList);
										 * udpDownloadJson.put("name",
										 * "wifiQos_"+masterDeviceName + "_" +
										 * masterTesName);
										 */
										// dataConnectivityudpValArr.add(udpDownloadJson);
										udpDownloadJson.put("data",
												udpWifiDownloadSpeedRssiList);
										udpDownloadJson.put("name", "wifirssi_"
												+ masterDeviceName + "_"
												+ masterTesName);
										udpDownloadJson.put("type", "scatter");
										udpDownloadJson.put("yAxis", 1);
										dataConnectivityudpValArr
												.add(udpDownloadJson);
										udpDownloadJson.put("data",
												udpDownloadSpeedRssList);
										udpDownloadJson.put("name", "rssi_"
												+ masterDeviceName + "_"
												+ masterTesName);
										udpDownloadJson.put("type", "scatter");
										udpDownloadJson.put("yAxis", 1);
										dataConnectivityudpValArr
												.add(udpDownloadJson);

										// DNS

										dnsDownloadJson.put("data",
												dnsMaxLatencyList);
										dnsDownloadJson.put("name",
												"MaxLatency_"
														+ masterDeviceName
														+ "_" + masterTesName);
										dataConnectivitydnsValArr
												.add(dnsDownloadJson);

										dnsDownloadJson.put("data",
												dnsAvgLatencyList);
										dnsDownloadJson.put("name",
												"AvgLatency_"
														+ masterDeviceName
														+ "_" + masterTesName);
										dataConnectivitydnsValArr
												.add(dnsDownloadJson);

										dnsDownloadJson.put("data",
												wifidnsMaxLatencyList);
										dnsDownloadJson.put("name",
												"wifi_MaxLatency_"
														+ masterDeviceName
														+ "_" + masterTesName);
										dataConnectivitydnsValArr
												.add(dnsDownloadJson);

										dnsDownloadJson.put("data",
												wifidnsAvgLatencyList);
										dnsDownloadJson.put("name",
												"wifi_AvgLatency_"
														+ masterDeviceName
														+ "_" + masterTesName);
										dataConnectivitydnsValArr
												.add(dnsDownloadJson);

										dnsDownloadJson.put("data", dnsRssList);
										dnsDownloadJson.put("name", "rssi_"
												+ masterDeviceName + "_"
												+ masterTesName);
										dnsDownloadJson.put("type", "scatter");
										dnsDownloadJson.put("yAxis", 1);
										dataConnectivitydnsValArr
												.add(dnsDownloadJson);

										dnsDownloadJson.put("data",
												wifidnsRssList);
										dnsDownloadJson.put("name",
												"wifi_rssi_" + masterDeviceName
														+ "_" + masterTesName);
										dnsDownloadJson.put("type", "scatter");
										dnsDownloadJson.put("yAxis", 1);
										dataConnectivitydnsValArr
												.add(dnsDownloadJson);

										// FTP

										ftpDownloadJson.put("data",
												ftpDownloadSpeedList);
										ftpDownloadJson.put("name",
												"RxThroughput_"
														+ masterDeviceName
														+ "_" + masterTesName);
										dataConnectivityftpValArr
												.add(ftpDownloadJson);

										/*
										 * ftpDownloadJson.put("data",
										 * ftpTxDownloadSpeedList);
										 * ftpDownloadJson.put("name",
										 * "TxThroughput_"+masterDeviceName +
										 * "_" + masterTesName);
										 * dataConnectivityftpValArr
										 * .add(ftpDownloadJson);
										 */

										/*
										 * ftpDownloadJson.put("data",
										 * wififtpDownloadSpeedList);
										 * ftpDownloadJson.put("name",
										 * "wifi_Throughput_"+masterDeviceName +
										 * "_" + masterTesName);
										 * dataConnectivityftpValArr
										 * .add(ftpDownloadJson);
										 */

										ftpDownloadJson.put("data",
												ftpDownloadSpeedRssList);
										ftpDownloadJson.put("name", "rssi_"
												+ masterDeviceName + "_"
												+ masterTesName);
										ftpDownloadJson.put("type", "scatter");
										ftpDownloadJson.put("yAxis", 1);
										dataConnectivityftpValArr
												.add(ftpDownloadJson);

										ftpUploadJson.put("data",
												ftpTxDownloadSpeedList);
										ftpUploadJson.put("name",
												"TxThroughput_"
														+ masterDeviceName
														+ "_" + masterTesName);
										dataConnectivityftpUploadValArr
												.add(ftpUploadJson);

										ftpUploadJson.put("data",
												ftpUploadSpeedRssList);
										ftpUploadJson.put("name", "rssi_"
												+ masterDeviceName + "_"
												+ masterTesName);
										ftpUploadJson.put("type", "scatter");
										ftpUploadJson.put("yAxis", 1);
										dataConnectivityftpUploadValArr
												.add(ftpUploadJson);

										/*
										 * ftpDownloadJson.put("data",
										 * wififtpDownloadSpeedRssList);
										 * ftpDownloadJson.put("name",
										 * "wifi_rssi_"+masterDeviceName + "_" +
										 * masterTesName);
										 * ftpDownloadJson.put("type",
										 * "scatter");
										 * ftpDownloadJson.put("yAxis", 1);
										 * dataConnectivityftpValArr
										 * .add(ftpDownloadJson);
										 */

										// DNS LTE
										dnsDownloadJsonLte.put("data",
												dnsMaxLatencyListLte);
										dnsDownloadJsonLte.put("name",
												"MaxLatency_"
														+ masterDeviceName
														+ "_" + masterTesName);
										dataConnectivitydnsValArrLte
												.add(dnsDownloadJsonLte);

										dnsDownloadJsonLte.put("data",
												dnsAvgLatencyListLte);
										dnsDownloadJsonLte.put("name",
												"AvgLatency_"
														+ masterDeviceName
														+ "_" + masterTesName);
										dataConnectivitydnsValArrLte
												.add(dnsDownloadJsonLte);

										dnsDownloadJsonLte.put("data",
												dnsRssListLte);
										dnsDownloadJsonLte.put("name", "rssi_"
												+ masterDeviceName + "_"
												+ masterTesName);
										dnsDownloadJsonLte.put("type",
												"scatter");
										dnsDownloadJsonLte.put("yAxis", 1);
										dataConnectivitydnsValArrLte
												.add(dnsDownloadJsonLte);

										// TCP LTE

										tcpDownloadJsonLte.put("data",
												tcpDownloadSpeedListLte);
										tcpDownloadJsonLte.put("name",
												"DownloadSpeed_"
														+ masterDeviceName
														+ "_" + masterTesName);
										dataConnectivitytcpValArrLte
												.add(tcpDownloadJsonLte);

										tcpDownloadJsonLte.put("data",
												tcpUploadSpeedListLte);
										tcpDownloadJsonLte.put("name",
												"UploadSpeed_"
														+ masterDeviceName
														+ "_" + masterTesName);
										dataConnectivitytcpValArrLte
												.add(tcpDownloadJsonLte);

										tcpDownloadJsonLte.put("data",
												tcpDownloadSpeedRssListLte);
										tcpDownloadJsonLte.put("name", "rssi_"
												+ masterDeviceName + "_"
												+ masterTesName);
										tcpDownloadJsonLte.put("type",
												"scatter");
										tcpDownloadJsonLte.put("yAxis", 1);
										dataConnectivitytcpValArrLte
												.add(tcpDownloadJsonLte);

										// UDP LTe
										/*
										 * udpDownloadJsonLte.put("data",
										 * udpDownloadSpeedQosListLte);
										 * udpDownloadJsonLte.put("name",
										 * "Qos_"+masterDeviceName + "_" +
										 * masterTesName);
										 * dataConnectivityudpValArrLte
										 * .add(udpDownloadJsonLte);
										 */
										udpDownloadJsonLte.put("data",
												udpDownloadSpeedListLte);
										udpDownloadJsonLte.put("name",
												"DownloadCapacity_"
														+ masterDeviceName
														+ "_" + masterTesName);
										dataConnectivityudpValArrLte
												.add(udpDownloadJsonLte);

										udpDownloadJsonLte.put("data",
												udpUploadSpeedListLte);
										udpDownloadJsonLte.put("name",
												"UploadCapacity_"
														+ masterDeviceName
														+ "_" + masterTesName);
										dataConnectivityudpValArrLte
												.add(udpDownloadJsonLte);

										udpDownloadJsonLte.put("type",
												"scatter");
										udpDownloadJsonLte.put("yAxis", 1);
										dataConnectivityudpValArrLte
												.add(udpDownloadJsonLte);
										udpDownloadJsonLte.put("data",
												udpDownloadSpeedRssListLte);
										udpDownloadJsonLte.put("name", "rssi_"
												+ masterDeviceName + "_"
												+ masterTesName);
										udpDownloadJsonLte.put("type",
												"scatter");
										udpDownloadJsonLte.put("yAxis", 1);
										dataConnectivityudpValArrLte
												.add(udpDownloadJsonLte);

										// voip
										voipDownloadJson.put("data",
												voipUpstreamList);
										voipDownloadJson.put("name",
												"UpstreamMaxJitter_"
														+ masterDeviceName
														+ "_" + masterTesName);
										dataConnectivityVoipValArr
												.add(voipDownloadJson);
										voipDownloadJson.put("data",
												wifivoipUpstreamList);
										voipDownloadJson.put("name",
												"wifi_UpstreamMaxJitter_"
														+ masterDeviceName
														+ "_" + masterTesName);
										dataConnectivityVoipValArr
												.add(voipDownloadJson);

										voipDownloadJson.put("data",
												voipDownstreamList);
										voipDownloadJson.put("name",
												"DownstreamMaxJitter_"
														+ masterDeviceName
														+ "_" + masterTesName);
										dataConnectivityVoipValArr
												.add(voipDownloadJson);
										voipDownloadJson.put("data",
												wifivoipDownstreamList);
										voipDownloadJson.put("name",
												"wifi_DownstreamMaxJitter_"
														+ masterDeviceName
														+ "_" + masterTesName);
										dataConnectivityVoipValArr
												.add(voipDownloadJson);

										voipDownloadJson.put("data",
												voipSipbyeList);
										voipDownloadJson.put("name", "SIPBye_"
												+ masterDeviceName + "_"
												+ masterTesName);
										dataConnectivityVoipValArr
												.add(voipDownloadJson);
										voipDownloadJson.put("data",
												wifivoipSipbyeList);
										voipDownloadJson.put("name",
												"wifi_SIPBye_"
														+ masterDeviceName
														+ "_" + masterTesName);
										dataConnectivityVoipValArr
												.add(voipDownloadJson);

										voipDownloadJson.put("data",
												voipRssList);
										voipDownloadJson.put("name", "rssi_"
												+ masterDeviceName + "_"
												+ masterTesName);
										voipDownloadJson.put("type", "scatter");
										voipDownloadJson.put("yAxis", 1);
										dataConnectivityVoipValArr
												.add(voipDownloadJson);
										voipDownloadJson.put("data",
												wifivoipRssList);
										voipDownloadJson.put("name",
												"wifi_rssi_" + masterDeviceName
														+ "_" + masterTesName);
										voipDownloadJson.put("type", "scatter");
										voipDownloadJson.put("yAxis", 1);
										dataConnectivityVoipValArr
												.add(voipDownloadJson);

										// FTP LTE
										ftpDownloadJsonLte.put("data",
												ftpDownloadSpeedListLte);
										ftpDownloadJsonLte.put("name",
												"Throughput_"
														+ masterDeviceName
														+ "_" + masterTesName);
										dataConnectivityftpValArrLte
												.add(ftpDownloadJsonLte);

										ftpDownloadJsonLte.put("data",
												ftpDownloadSpeedRssListLte);
										ftpDownloadJsonLte.put("name", "rssi_"
												+ masterDeviceName + "_"
												+ masterTesName);
										ftpDownloadJsonLte.put("type",
												"scatter");
										ftpDownloadJsonLte.put("yAxis", 1);
										dataConnectivityftpValArrLte
												.add(ftpDownloadJsonLte);

										//									

										List<String> avgDCForMarketList = vq
												.dataConnectivityHealthIndex(
														tcpDownloadSpeedList,
														udpDownloadSpeedList,
														dnsAvgLatencyList, "3g");
										float avgDCForMarket = Float
												.parseFloat(avgDCForMarketList
														.get(0));
										healthIndexTo
												.setDataConnectivityHealthIndex(avgDCForMarket);// for
										// Data
										// Connectivity
										healthIndexTo
												.setDcAvgValue(avgDCForMarketList
														.get(1));

										List<String> avgVoipForMarketList = new VoiceQualityHealth()
												.voipHealthIndex(detailsDataMapForVoipDc);
										float avgVoipForMarket = Float
												.parseFloat(avgVoipForMarketList
														.get(0));
										healthIndexTo
												.setVoipHealthIndex(avgVoipForMarket);// for
										// Voip
										healthIndexTo
												.setVoipAvgValue(avgVoipForMarketList
														.get(1));

										List<String> avgLteForMarketList = vq
												.dataConnectivityHealthIndex(
														tcpDownloadSpeedListLte,
														udpDownloadSpeedListLte,
														dnsAvgLatencyListLte,
														"LTE");
										float avgLteForMarket = Float
												.parseFloat(avgLteForMarketList
														.get(0));
										healthIndexTo
												.setLteHealthIndex(avgLteForMarket);// for
										// Lte
										healthIndexTo
												.setDcLteAvgValue(avgLteForMarketList
														.get(1));

										List<String> avgBrowserForMarketList = vq
												.voiceBrowserHealthJson(marketWiseBrowserData
														.get(marketName
																+ masterDeviceName));
										float avgBrowserForMarket = Float
												.parseFloat(avgBrowserForMarketList
														.get(0));
										healthIndexTo
												.setBrowserHealthIndex(avgBrowserForMarket);// for
										// Browser
										healthIndexTo
												.setBrowserAvgValue(avgBrowserForMarketList
														.get(1));
									}
									// voice and data

									if (masterFileName.contains("externaltest")) {
										System.out
												.println("exteralllllllllllllllll");
										// DATA AND VOICE
										tcpDriveDownloadJson.put("data",
												tcpDriveDownloadSpeedList);
										tcpDriveDownloadJson.put("name",
												"DownloadSpeed_"
														+ masterDeviceName
														+ "_" + masterTesName);
										dataConnectivitytcpDriveValArr
												.add(tcpDriveDownloadJson);
										tcpDriveDownloadJson.put("data",
												wifitcpDriveDownloadSpeedList);
										tcpDriveDownloadJson.put("name",
												"wifi_DownloadSpeed_"
														+ masterDeviceName
														+ "_" + masterTesName);
										dataConnectivitytcpDriveValArr
												.add(tcpDriveDownloadJson);
										tcpDriveDownloadJson.put("data",
												tcpDriveDownloadSpeedRssList);
										tcpDriveDownloadJson.put("name",
												"rssi_" + masterDeviceName
														+ "_" + masterTesName);
										tcpDriveDownloadJson.put("type",
												"scatter");
										tcpDriveDownloadJson.put("yAxis", 1);
										dataConnectivitytcpDriveValArr
												.add(tcpDriveDownloadJson);
										tcpDriveDownloadJson
												.put("data",
														wifitcpDriveDownloadSpeedRssList);
										tcpDriveDownloadJson.put("name",
												"wifi_rssi_" + masterDeviceName
														+ "_" + masterTesName);
										tcpDriveDownloadJson.put("type",
												"scatter");
										tcpDriveDownloadJson.put("yAxis", 1);
										dataConnectivitytcpDriveValArr
												.add(tcpDriveDownloadJson);
									}

									List<String> avgVDForMarketList = vq
											.voiceDataHealthIndex(
													masterMarketId,
													masterDeviceName,
													masterTesName,
													tcpDriveDownloadSpeedList);
									float avgVDForMarket = Float
											.parseFloat(avgVDForMarketList
													.get(0));
									healthIndexTo
											.setVoiceDataHealthIndex(avgVDForMarket);// for
									// Voice
									// Data
									healthIndexTo
											.setVdAvgValue(avgVDForMarketList
													.get(1));

									// logger.info("mrktId................................."+mrktId);
									healthIndexTo.setMarktId(mrktId);
									// logger.info();
									List<HealthIndexTo> healthIndexToList = healthIndexMap
											.get(marketName);
									if (null == healthIndexToList) {
										healthIndexToList = new ArrayList<HealthIndexTo>();
									}
									if (null == testWiseMarketMap
											.get(masterTesName)) {
										healthIndexMap = new HashMap<String, List<HealthIndexTo>>();
										healthIndexToList = new ArrayList<HealthIndexTo>();
									} else {
										healthIndexMap = testWiseMarketMap
												.get(masterTesName);
										if (null == healthIndexMap
												.get(marketName)) {
											healthIndexToList = new ArrayList<HealthIndexTo>();
										} else {
											healthIndexToList = healthIndexMap
													.get(marketName);
										}
									}
									healthIndexTo.setMarketName(marketName);
									healthIndexToList.clear();
									healthIndexToList.add(0, healthIndexTo);
									healthIndexMap.put(marketName,
											healthIndexToList);
									testWiseMarketMap.put(masterTesName,
											healthIndexMap);
									System.out.println("testWiseMarketMap for Voice connectivity----------++------"
													+ testWiseMarketMap);

									/********* DataConncetivity ***********/

									/********* Lte ***********/

									/********* Lte ***********/

									callSetupJsoValArrLte.add(callSetupJsonLte);
									callRetentionValArrLte
											.add(callRetentionJsonLte);
									// call setup lte bar chart ends
								}
							}
						}

					}
				}
				numberMarkets++;
				jsoArr.add(mark_CatJson);
				callDropJsoArr.add(callDrop_CatJson);
				jsoArrLte.add(mark_CatJsonLte);
				callSetupJsoArr.add(callSetup_mark_CatJson);
				callSetupJsoArrLte.add(callSetup_mark_CatJsonLte);
				callDropJsoArrLte.add(callDrop_CatJsonLte);
				numberOfNetwork = numberOfNetwork
						+ dcDao.getNumberOfNetworksForMarket(masterMarketId);
				numberFtpNetworks = numberOfNetwork
						+ new DataConnectivityDaoImpl()
								.getNumberOfNetworksForFtpMarket(masterMarketId);
				net.sf.json.JSONObject xaxisLabel = new net.sf.json.JSONObject();
				xaxisLabel.put("text", marketName);
				context.getExternalContext().getSessionMap().put(
						"graphMarket_" + j, xaxisLabel);
				context.getExternalContext().getSessionMap().put(
						"voiceQualityUplinkChart_Market_" + j, jsoArr);
				context.getExternalContext().getSessionMap().put(
						"voiceQualityUplinkChartValue_Market_" + j, jsoValArr);

				context.getExternalContext().getSessionMap().put(
						"voiceQualityDownLinkChart_Market_" + j, jsoArr);
				context.getExternalContext().getSessionMap().put(
						"voiceQualityDownLinkVal_Market_" + j,
						jsoDownlinkValArr);
				net.sf.json.JSONArray bucketJsonArr = new JSONArray();
				signalStrengthCat.put("categories", rangeList);
				signalStrengthCat.put("name", marketName);
				bucketJsonArr.add(signalStrengthCat);

				// lte
				net.sf.json.JSONArray bucketJsonArrLte = new JSONArray();
				signalStrengthCatLte.put("categories", rangeListLte);
				signalStrengthCatLte.put("name", marketName);
				bucketJsonArrLte.add(signalStrengthCatLte);
				// lte
				context.getExternalContext().getSessionMap().put(
						"vqSignalStrengthCat_" + j, bucketJsonArr);
				context.getExternalContext().getSessionMap().put(
						"vqSignalStrengthvals_" + j, vqSignalStrengthvals);

				context.getExternalContext().getSessionMap().put(
						"vqSignalStrengthCatLte_" + j, bucketJsonArrLte);
				context.getExternalContext().getSessionMap()
						.put("vqSignalStrengthvalsLte_" + j,
								vqSignalStrengthvalsLte);

				// VOICE CONNECTIVITY KPI

				vcJsonArray.add(callsetUpJson);
				// logger.info("vcJsonArray---++-------"+vcJsonArray);
				context.getExternalContext().getSessionMap().put(
						"callSetupUplinkVal_" + j, callSetupSignalStrengthvals);
				context.getExternalContext().getSessionMap().put(
						"callSetupUplinkCat", vcJsonArray);

				context.getExternalContext().getSessionMap().put(
						"callDropJsoArr_" + j, callDropJsoArr);
				context.getExternalContext().getSessionMap().put(
						"callDropJsoDownlinkValArr_" + j,
						callDropJsoDownlinkValArr);

				context.getExternalContext().getSessionMap().put(
						"idleModeValArr_" + j, idleModeValArr);
				context.getExternalContext().getSessionMap().put(
						"callTearDownValArr_" + j, callTearDownValArr);

				// DATA CONNECTIVITY
				// TCP
				context.getExternalContext().getSessionMap().put(
						"dataConnectivitytcpDownloadArr_" + j,
						dataConnectivitytcpDownloadArr);
				context.getExternalContext().getSessionMap().put(
						"dataConnectivitytcpValArr_" + j,
						dataConnectivitytcpValArr);
				// UDP
				context.getExternalContext().getSessionMap().put(
						"dataConnectivityudpDownloadArr_" + j,
						dataConnectivityudpDownloadArr);
				context.getExternalContext().getSessionMap().put(
						"dataConnectivityudpValArr_" + j,
						dataConnectivityudpValArr);

				// DNS

				context.getExternalContext().getSessionMap().put(
						"dataConnectivitydnsDownloadArr_" + j,
						dataConnectivitydnsDownloadArr);
				context.getExternalContext().getSessionMap().put(
						"dataConnectivitydnsValArr_" + j,
						dataConnectivitydnsValArr);

				// FTP
				context.getExternalContext().getSessionMap().put(
						"dataConnectivityftpDownloadArr_" + j,
						dataConnectivityftpDownloadArr);
				context.getExternalContext().getSessionMap().put(
						"dataConnectivityftpValArr_" + j,
						dataConnectivityftpValArr);

				context.getExternalContext().getSessionMap().put(
						"dataConnectivityftpUploadArr_" + j,
						dataConnectivityftpDownloadArr);
				context.getExternalContext().getSessionMap().put(
						"dataConnectivityftpUploadValArr_" + j,
						dataConnectivityftpUploadValArr);

				context.getExternalContext().getSessionMap().put(
						"dataConnectivityVoipDownloadArr_" + j,
						dataConnectivityVoipDownloadArr);
				context.getExternalContext().getSessionMap().put(
						"dataConnectivityVoipValArr_" + j,
						dataConnectivityVoipValArr);

				context.getExternalContext().getSessionMap().put(
						"dataConnectivitytcpDriveDownloadArr_" + j,
						dataConnectivitytcpDriveDownloadArr);
				context.getExternalContext().getSessionMap().put(
						"dataConnectivitytcpDriveValArr_" + j,
						dataConnectivitytcpDriveValArr);

				context.getExternalContext().getSessionMap().put(
						"dataConnectivitytcpDownloadArrLte_" + j,
						dataConnectivitytcpDownloadArrLte);
				context.getExternalContext().getSessionMap().put(
						"dataConnectivitytcpValArrLte_" + j,
						dataConnectivitytcpValArrLte);

				context.getExternalContext().getSessionMap().put(
						"dataConnectivityudpDownloadArrLte_" + j,
						dataConnectivityudpDownloadArrLte);
				context.getExternalContext().getSessionMap().put(
						"dataConnectivityudpValArrLte_" + j,
						dataConnectivityudpValArrLte);

				context.getExternalContext().getSessionMap().put(
						"dataConnectivitydnsDownloadArrLte_" + j,
						dataConnectivitydnsDownloadArrLte);
				context.getExternalContext().getSessionMap().put(
						"dataConnectivitydnsValArrLte_" + j,
						dataConnectivitydnsValArrLte);

				context.getExternalContext().getSessionMap().put(
						"dataConnectivityftpDownloadArrLte_" + j,
						dataConnectivityftpDownloadArrLte);
				context.getExternalContext().getSessionMap().put(
						"dataConnectivityftpValArrLte_" + j,
						dataConnectivityftpValArrLte);

				// context.getExternalContext().getSessionMap().put("vqSignalStrengthCatLte_"+j,rangeListLte);
				// //logger.info("vqSignalStrengthCatLte-------------"+rangeListLte);
				// //logger.info("vqSignalStrengthvalsLte---------------"+vqSignalStrengthvalsLte);
				// context.getExternalContext().getSessionMap().put("vqSignalStrengthvalsLte_"+j,
				// vqSignalStrengthvalsLte);

				context.getExternalContext().getSessionMap().put(
						"callSetupUplinkCatLte", new net.sf.json.JSONArray());
				context.getExternalContext().getSessionMap().put(
						"callSetupUplinkValLte_" + j,
						callSetupSignalStrengthvalsLte);

				context.getExternalContext().getSessionMap().put(
						"callDropJsoArrLte_" + j, callDropJsoArrLte);
				context.getExternalContext().getSessionMap().put(
						"callDropJsoDownlinkValArrLte_" + j,
						callDropJsoDownlinkValArrLte);

				// context.getExternalContext().getSessionMap().put("callSetupDownlinkCat_"+j,
				// vcIdleModeJsonArray);
				context.getExternalContext().getSessionMap().put(
						"callSetupUplinkCat_" + j, new JSONArray());
			}
		}
		// KPI 1
		context.getExternalContext().getSessionMap().put(
				"multipleMarketsUplinkCat", jsoArr);
		context.getExternalContext().getSessionMap().put(
				"multipleMarketsUplink{al", jsoValArr);
		context.getExternalContext().getSessionMap().put("retrievedConfig",
				"false");
		// logger.info("marketWiseVoipData----------------------"+marketWiseVoipData);
		Iterator<String> marketItr = marketWiseVoipData.keySet().iterator();
		while (marketItr.hasNext()) {
			String browserMarketName = marketItr.next();
			// logger.info("browserMarketName------------"+browserMarketName);
			List<VoipBean> browserbeans = marketWiseVoipData
					.get(browserMarketName);
			for (int i = 0; i < browserbeans.size(); i++) {
				VoipBean browserBean = browserbeans.get(i);
				// logger.info("----name---"+browserMarketName);
				// logger.info("-----device-----"+browserBean.getDeviceName());
				// logger.info("-----latency-----"+browserBean.getVoipDownAvgJitter());
			}
		}

		avgHealthIndexMap = new VoiceQualityHealth()
				.getHealthIndexForAllKpi(testWiseMarketMap);
		// logger.info("avgHealthIndexMap-----------"+avgHealthIndexMap);
		for (Entry<String, HashMap<String, List<HealthIndexTo>>> entry : testWiseMarketMap
				.entrySet()) {
			// logger.info("testName ====++== " + entry.getKey() );
			HashMap<String, List<HealthIndexTo>> test = entry.getValue();

			for (Entry<String, List<HealthIndexTo>> Kentry : test.entrySet()) {
				// logger.info("mrktId ====++== " + Kentry.getKey() );
				List<HealthIndexTo> listTest = Kentry.getValue();
				for (int i = 0; i < listTest.size(); i++) {
					// logger.info("listTest-----**---"+listTest.get(i).getDataConnectivityHealthIndex());
				}

			}

		}
		context.getExternalContext().getSessionMap().put("testWiseMarketMap",
				testWiseMarketMap);
		context.getExternalContext().getSessionMap().put("avgHealthIndexMap",
				avgHealthIndexMap);

		context.getExternalContext().getSessionMap().put(
				"multipleMarketsDownlinkCat", jsoArr);
		context.getExternalContext().getSessionMap().put(
				"multipleMarketsDownlinkVal", jsoDownlinkValArr);

		// RVP
		context.getExternalContext().getSessionMap().put("callDropJsoArr",
				callDropJsoArr);
		context.getExternalContext().getSessionMap().put(
				"callDropJsoDownlinkValArr", callDropJsoDownlinkValArr);
		// RVP
		context.getExternalContext().getSessionMap().put("vqSignalStrengthCat",
				rangeList);
		context.getExternalContext().getSessionMap().put(
				"vqSignalStrengthvals", vqSignalStrengthvals);
		// KPI 1

		context.getExternalContext().getSessionMap().put(
				"dataConnectivityudpDownloadArr",
				dataConnectivityudpDownloadArr);
		context.getExternalContext().getSessionMap().put(
				"dataConnectivityudpValArr", dataConnectivityudpValArr);
		context.getExternalContext().getSessionMap().put(
				"dataConnectivityftpDownloadArr",
				dataConnectivityftpDownloadArr);
		context.getExternalContext().getSessionMap().put(
				"dataConnectivityftpValArr", dataConnectivityftpValArr);

		//
		context.getExternalContext().getSessionMap().put(
				"dataConnectivityftpDownloadArrLte",
				dataConnectivityftpDownloadArrLte);
		context.getExternalContext().getSessionMap().put(
				"dataConnectivityftpValArrLte", dataConnectivityftpValArrLte);

		// voip
		context.getExternalContext().getSessionMap().put(
				"dataConnectivityVoipDownloadArr",
				dataConnectivityVoipDownloadArr);
		context.getExternalContext().getSessionMap().put(
				"dataConnectivityVoipValArr", dataConnectivityVoipValArr);

		// kpi 4

		context.getExternalContext().getSessionMap().put(
				"dataConnectivitytcpDriveDownloadArr",
				dataConnectivitytcpDriveDownloadArr);
		context.getExternalContext().getSessionMap().put(
				"dataConnectivitytcpDriveValArr",
				dataConnectivitytcpDriveValArr);
		// voice quality

		// //logger.info("dataConnectivitytcpValArrLte--------++---------"+dataConnectivitytcpValArrLte);
		context.getExternalContext().getSessionMap().put(
				"multipleMarketsUplinkCatLte", jsoArrLte);
		context.getExternalContext().getSessionMap().put(
				"multipleMarketsUplinkValLte", jsoValArrLte);
		context.getExternalContext().getSessionMap().put(
				"multipleMarketsDownlinkCatLte", jsoArrLte);
		context.getExternalContext().getSessionMap().put(
				"multipleMarketsDownlinkValLte", jsoDownlinkValArrLte);
		context.getExternalContext().getSessionMap().put(
				"vqSignalStrengthCatLte", rangeListLte);
		context.getExternalContext().getSessionMap().put(
				"vqSignalStrengthvalsLte", vqSignalStrengthvalsLte);
		// data connectivity
		context.getExternalContext().getSessionMap().put(
				"dataConnectivitytcpDownloadArrLte",
				dataConnectivitytcpDownloadArrLte);
		context.getExternalContext().getSessionMap().put(
				"dataConnectivitytcpValArrLte", dataConnectivitytcpValArrLte);

		context.getExternalContext().getSessionMap().put(
				"dataConnectivitydnsDownloadArrLte",
				dataConnectivitydnsDownloadArrLte);
		context.getExternalContext().getSessionMap().put(
				"dataConnectivitydnsValArrLte", dataConnectivitydnsValArrLte);

		context.getExternalContext().getSessionMap().put(
				"dataConnectivityudpDownloadArrLte",
				dataConnectivityudpDownloadArrLte);
		context.getExternalContext().getSessionMap().put(
				"dataConnectivityudpValArrLte", dataConnectivityudpValArrLte);
		// call setup
		// context.getExternalContext().getSessionMap().put("callSetupUplinkCat",
		// vcJsonArray);
		context.getExternalContext().getSessionMap().put("callSetupUplinkVal",
				callSetupSignalStrengthvals);
		context.getExternalContext().getSessionMap().put(
				"callSetupDownlinkCat", vcIdleModeJsonArray);
		context.getExternalContext().getSessionMap().put("callRetentionValArr",
				callRetentionValArr);
		context.getExternalContext().getSessionMap().put("idleModeValArr",
				idleModeValArr);
		context.getExternalContext().getSessionMap().put(
				"callSetupSignalStrengthCat", rangeList);
		context.getExternalContext().getSessionMap().put(
				"callSetupSignalStrengthvals", callSetupSignalStrengthvals);
		// call setup lte
		context.getExternalContext().getSessionMap().put(
				"callSetupUplinkCatLte", new net.sf.json.JSONArray());
		context.getExternalContext().getSessionMap().put(
				"callSetupUplinkValLte", callSetupJsoValArrLte);
		context.getExternalContext().getSessionMap().put(
				"callSetupDownlinkCatLte", callSetupJsoArrLte);
		context.getExternalContext().getSessionMap().put(
				"callRetentionValArrLte", callRetentionValArrLte);
		context.getExternalContext().getSessionMap().put(
				"callSetupSignalStrengthCatLte", rangeList);
		context.getExternalContext().getSessionMap().put(
				"callSetupSignalStrengthvalsLte",
				callSetupSignalStrengthvalsLte);
		context.getExternalContext().getSessionMap().remove("browserBeanList");
		context.getExternalContext().getSessionMap().remove(
				"marketWiseVoipData");
		context.getExternalContext().getSessionMap().put("browserBeanList",
				marketWiseBrowserData);
		context.getExternalContext().getSessionMap().put("voipBeanList",
				marketWiseVoipData);
		context.getExternalContext().getSessionMap().put("marketList",
				marketNameList);
		context.getExternalContext().getSessionMap().put("upperRangeForGood",
				qualityTo.getUpperRangeForGood());
		context.getExternalContext().getSessionMap().put("lowerRangeForGood",
				qualityTo.getLowerRangeForGood());
		context.getExternalContext().getSessionMap().put("upperRangeForPoor",
				qualityTo.getUpperRangeForPoor());
		context.getExternalContext().getSessionMap().put("lowerRangeForPoor",
				qualityTo.getLowerRangeForPoor());
		context.getExternalContext().getSessionMap().put(
				"upperRangeForAverage", qualityTo.getUpperRangeForAvg());
		context.getExternalContext().getSessionMap().put(
				"lowerRangeForAverage", qualityTo.getLowerRangeForAvg());

		context.getExternalContext().getSessionMap().put(
				"lteupperRangeForGood", qualityLteTo.getUpperRangeForGood());
		context.getExternalContext().getSessionMap().put(
				"ltelowerRangeForGood", qualityLteTo.getLowerRangeForGood());
		context.getExternalContext().getSessionMap().put(
				"lteupperRangeForPoor", qualityLteTo.getUpperRangeForPoor());
		context.getExternalContext().getSessionMap().put(
				"ltelowerRangeForPoor", qualityLteTo.getLowerRangeForPoor());
		context.getExternalContext().getSessionMap().put(
				"lteupperRangeForAverage", qualityLteTo.getUpperRangeForAvg());
		context.getExternalContext().getSessionMap().put(
				"ltelowerRangeForAverage", qualityLteTo.getLowerRangeForAvg());

		context.getExternalContext().getSessionMap().put("qualityLteTo",
				qualityLteTo);
		context.getExternalContext().getSessionMap()
				.put("qualityTo", qualityTo);
		dcDao.closeConn();
		// logger.info("after marketFileMap...............helooooooooooooooo....................."+context.getExternalContext().getSessionMap());
	}

	/**
	 * Navigation From Market Page to Map Page.
	 */
	public String welcomeMarketReports() {
		TestConfigDao testConfigDao = new TestConfigDaoImpl();
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		context.getExternalContext().getSessionMap().remove("testNameMap");
		String roleName = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		String userId = context.getExternalContext().getSessionMap().get(
				"loggedInUserID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserID",
				userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID",
				roleName);
		if (roleName.equals("superadmin")) {
			testNameMap = testConfigDao.getTestNamesInMapInStgDevice();
		} else {
			Set<String> userNameList = new UserDaoImpl().getUsersOfSuperior(
					userId, roleName);
			testNameMap = new TestConfigDaoImpl()
					.getnTestNamesInMapInHierarchy(userNameList.toString()
							.substring(1, userNameList.toString().length() - 1));
			// testNameMap = testConfigDao.getTestNamesInMapInStgDevice(userId);
		}
		context.getExternalContext().getSessionMap().remove("testNameMap");
		context.getExternalContext().getSessionMap().put("testNameMap",
				testNameMap);
		context.getExternalContext().getSessionMap().remove("lattitudes");
		context.getExternalContext().getSessionMap().remove("longitudes");
		context.getExternalContext().getSessionMap().remove("testName");
		context.getExternalContext().getSessionMap().remove("signalStrength");
		context.getExternalContext().getSessionMap().remove("networkType");
		context.getExternalContext().getSessionMap().remove("networkData");
		context.getExternalContext().getSessionMap().remove("networkRoaming");
		context.getExternalContext().getSessionMap().remove("networkMnc");
		context.getExternalContext().getSessionMap().remove("networkMcc");
		context.getExternalContext().getSessionMap().remove("cellLocationLac");
		context.getExternalContext().getSessionMap().remove("cellLocationCid");
		context.getExternalContext().getSessionMap()
				.remove("signalStrengthSnr");
		context.getExternalContext().getSessionMap().remove("devicePhoneType");
		context.getExternalContext().getSessionMap().remove(
				"timeStampForEachSample");
		return "welcomeMarketReportsPage";
	}

	/**
	 * Navigation from map Page to file processing page.
	 */
	public String maptoFileProcessing() {
		activationMessage = "";
		errorMessage = "";
		TestConfigDao testConfigDao = new TestConfigDaoImpl();
		FacesContext context = FacesContext.getCurrentInstance();
		ReportBean reportBean = (ReportBean) context.getExternalContext()
				.getSessionMap().get("reportBean");
		reportBean.setButtonLable("Activate / Deactivate");
		reportBean.setActivationMessage(activationMessage);
		reportBean.setErrorMessage(errorMessage);
		testNameMap = testConfigDao.getTestNamesInFileHistory();
		context.getExternalContext().getSessionMap().remove("reportBean");
		context.getExternalContext().getSessionMap().remove("stringfullpath");
		context.getExternalContext().getSessionMap().remove("testNameMap");
		context.getExternalContext().getSessionMap().put("testNameMap",
				testNameMap);
		context.getExternalContext().getSessionMap().remove("fileNameMap");
		context.getExternalContext().getSessionMap().remove("reportBean");
		context.getExternalContext().getSessionMap().put("reportBean",
				reportBean);
		return "maptoFileProcessingPage";
	}

	public String fileUpload() {
		activationMessage = "";
		errorMessage = "";
		TestConfigDao testConfigDao = new TestConfigDaoImpl();
		FacesContext context = FacesContext.getCurrentInstance();
		ReportBean reportBean = (ReportBean) context.getExternalContext()
				.getSessionMap().get("reportBean");
		reportBean.setButtonLable("Activate / Deactivate");
		reportBean.setActivationMessage(activationMessage);
		reportBean.setErrorMessage(errorMessage);
		testNameMap = testConfigDao.getTestNamesInFileHistory();
		context.getExternalContext().getSessionMap().remove("reportBean");
		context.getExternalContext().getSessionMap().remove("stringfullpath");
		context.getExternalContext().getSessionMap().remove("testNameMap");
		context.getExternalContext().getSessionMap().put("testNameMap",
				testNameMap);
		context.getExternalContext().getSessionMap().remove("fileNameMap");
		context.getExternalContext().getSessionMap().remove("reportBean");
		context.getExternalContext().getSessionMap().put("reportBean",
				reportBean);
		context.getExternalContext().getSessionMap().put("analysisMsg", "");
		return "fileUploadPage";
	}

	public void testListener(AjaxBehaviorEvent event) {
		String result = "called by "
				+ event.getComponent().getClass().getName();
		FacesContext context = FacesContext.getCurrentInstance();
		TestConfigDao testConfigDao = new TestConfigDaoImpl();
		fileNameMap = testConfigDao.getFileNamesInFileHistory(testCaseName);
		if (testCaseName.equals("0")) {
			ReportBean reportBean = (ReportBean) context.getExternalContext()
					.getSessionMap().get("reportBean");
			reportBean.setButtonLable("Activate / Deactivate");
			reportBean.setActivationMessage("");
			context.getExternalContext().getSessionMap().remove("reportBean");
			context.getExternalContext().getSessionMap().put("reportBean",
					reportBean);
		}
		context.getExternalContext().getSessionMap().remove("fileNameMap");
		context.getExternalContext().getSessionMap().put("fileNameMap",
				fileNameMap);
	}

	/**
	 * Getting file names with respect to test name
	 */
	public void setFileName(ValueChangeEvent event)
			throws AbortProcessingException {
		fileName = (String) event.getNewValue();
		TestConfigDao testConfigDao = new TestConfigDaoImpl();
		FacesContext context = FacesContext.getCurrentInstance();
		ReportBean reportBean = (ReportBean) context.getExternalContext()
				.getSessionMap().get("reportBean");
		String updateStatus = "";
		if (null != fileName) {
			if (!fileName.equals("0")) {
				updateStatus = testConfigDao.getUpdateStatus(testCaseName,
						fileName);
				if (null == updateStatus) {
					updateStatus = "Activate / Deactivate";
				}
			} else {
				updateStatus = "Activate / Deactivate";
			}
		} else {
			updateStatus = "Activate / Deactivate";
		}

		reportBean.setButtonLable(updateStatus);
		reportBean.setActivationMessage("");
		reportBean.setErrorMessage("");
		context.getExternalContext().getSessionMap().remove("reportBean");
		context.getExternalContext().getSessionMap().put("reportBean",
				reportBean);
	}

	/**
	 * Activate Deactivate test name
	 */
	public String activeDeactivateTestName() {
		TestConfigDao testConfigDao = new TestConfigDaoImpl();
		FacesContext context = FacesContext.getCurrentInstance();
		ReportBean reportBean = (ReportBean) context.getExternalContext()
				.getSessionMap().get("reportBean");
		String activationStatus = "failure";

		if (null != fileName && fileName != "") {
			if (!fileName.equals("0")) {
				activationStatus = testConfigDao.goToActivateDeactiveTestName(
						testCaseName, fileName);
			}
		}
		if (buttonLable == "Activate") {
			activationMessage = "Test '" + fileName
					+ "' successfully activated.";
		} else if (buttonLable == "Deactivate") {
			activationMessage = "Test '" + fileName
					+ "' successfully deactivated.";
		}
		return "maptoFileProcessingPage";
	}

	public String getChartDetails() {
		// //logger.info("getChartDetails-----++---------"+FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap());
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getSessionMap().put("chart_datails",
				"THIS IS AJAX RETURN");
		return "";
	}

	public String getGraphDetailValues() {
		// check if null?
		// logger.info("in say helooooooo getGraphDetailValues");

		// logger.info(FacesContext.getCurrentInstance().getExternalContext().getSessionMap());
		String arrayList = "";
		// //logger.info();
		String sessionKey = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get(
						"hiddenValuesToGetForChart");
		if (null != sessionKey) {
			String[] keyArr = sessionKey.split(",");
			// logger.info("keyArr--+--"+keyArr);
			// logger.info("keyArr----"+keyArr[0]);
			if (keyArr.length > 1) {
				if (null != FacesContext.getCurrentInstance()
						.getExternalContext().getSessionMap().get(
								keyArr[0].trim() + "_" + keyArr[3].trim())) {

					HashMap<String, ArrayList<Integer>> detailMap = (HashMap<String, ArrayList<Integer>>) FacesContext
							.getCurrentInstance().getExternalContext()
							.getSessionMap().get(
									keyArr[0].trim() + "_" + keyArr[3].trim());
					// logger.info("detailMap--+++--====-----"+
					// detailMap);
					// logger.info("keyArr[0]-------**---"+keyArr[0]);
					// logger.info("keyArr[1]-------**---"+keyArr[1]);
					ArrayList<Integer> finalVales = detailMap.get(keyArr[1]);
					// logger.info("finalVales----va-----"+finalVales);
					// logger.info("finalVales-----++------"+finalVales.toString().length());
					arrayList = finalVales.toString().substring(1,
							finalVales.toString().length() - 1);
				}
			}
		}

		// ///logger.info("arrayList----------++----------"+arrayList);
		return arrayList;
	}

	public String getChartDetailsForDC() {
		// check if null?
		// logger.info("in say helooooooo------getChartDetailsForDC------");
		String imageUrlString = "/Airometric/javax.faces.resource/lg-nexus-phone.jpg.jsf?ln=images";
		setImageUrl(imageUrlString);
		// //logger.info(FacesContext.getCurrentInstance().getExternalContext().getSessionMap());
		String arrayList = "";
		// //logger.info();
		String sessionKey = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get(
						"hiddenValuesToGetForChart");
		// logger.info("sessionKey---++***---++--"+sessionKey);
		// logger.info("FacesContext.getCurrentInstance().getExternalContext().getSessionMap()---------------"+FacesContext.getCurrentInstance().getExternalContext().getSessionMap());
		if (null != sessionKey && !sessionKey.trim().equals("")) {
			String[] keyArr = sessionKey.split(",");
			String[] keyArrSplitBy_ = sessionKey.split("_");
			/*
			 * //logger.info("keyArrSplitBy_--+--"+keyArrSplitBy_[1]);
			 * //logger.info("keyArr[0----"+keyArr[0]);
			 */
			if (keyArr.length > 1) {
				if (null != FacesContext.getCurrentInstance()
						.getExternalContext().getSessionMap().get(
								keyArr[0].trim() + "_" + keyArr[3].trim())) {

					HashMap<String, HashMap<String, HashMap<String, ArrayList<Integer>>>> detailNetWorkMapMap = (HashMap<String, HashMap<String, HashMap<String, ArrayList<Integer>>>>) FacesContext
							.getCurrentInstance().getExternalContext()
							.getSessionMap().get(
									keyArr[0].trim() + "_" + keyArr[3].trim());
					// logger.info("---------detailNetWorkMapMap----====-----"+
					// detailNetWorkMapMap);
					// logger.info("keyArr[0]----------"+keyArr[0]);
					// logger.info("keyArr[1]----------"+keyArr[1]);
					// logger.info("keyArr[2]----------"+keyArr[2]);
					// logger.info("keyArrSplitBy_--+--"+keyArrSplitBy_[1]);
					HashMap<String, HashMap<String, ArrayList<Integer>>> detailMap = (HashMap<String, HashMap<String, ArrayList<Integer>>>) detailNetWorkMapMap
							.get(keyArr[2].trim());
					// logger.info(keyArrSplitBy_[1]+"------detailMap==========="+detailMap);
					HashMap<String, ArrayList<Integer>> finalVales = detailMap
							.get(keyArrSplitBy_[1]);
					// //logger.info("finalVales----va-----"+finalVales);
					ArrayList<Integer> finalArrayList = finalVales
							.get(keyArr[1]);
					// HashMap<String,
					// ArrayList<Integer>>chartvalues=finalVales.get(keyArr[1]);
					// //logger.info("finalArrayList----va-----"+finalArrayList);
					arrayList = finalArrayList.toString().substring(1,
							finalArrayList.toString().length() - 1);
				}
			}

		}

		// //logger.info("arrayList----------++----------"+arrayList);
		return arrayList;
	}

	public String getChartDetailsForDCForLTE() {
		// check if null?
		// //logger.info("in say helooooooo------getChartDetailsForDC----LTE------");

		// //logger.info(FacesContext.getCurrentInstance().getExternalContext().getSessionMap());
		String arrayList = "";
		// //logger.info();
		String sessionKey = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get(
						"hiddenValuesToGetForChart");
		// //logger.info("sessionKey---------"+sessionKey);
		// //logger.info("sessionKey---++***---++--"+sessionKey);
		// //logger.info("FacesContext.getCurrentInstance().getExternalContext().getSessionMap()---------------"+FacesContext.getCurrentInstance().getExternalContext().getSessionMap());
		if (null != sessionKey && !sessionKey.trim().equals("")) {
			String[] keyArr = sessionKey.split(",");
			String[] keyArrSplitBy_ = sessionKey.split("_");
			List list1 = Arrays.asList(keyArr);

			// //logger.info("list1---------"+list1);
			if (null != FacesContext.getCurrentInstance().getExternalContext()
					.getSessionMap().get(
							keyArr[0].trim() + "_" + keyArr[3].trim())) {

				HashMap<String, HashMap<String, HashMap<String, ArrayList<Integer>>>> detailNetWorkMapMap = (HashMap<String, HashMap<String, HashMap<String, ArrayList<Integer>>>>) FacesContext
						.getCurrentInstance().getExternalContext()
						.getSessionMap().get(
								keyArr[0].trim() + "_" + keyArr[3].trim());
				/*
				 * //logger.info("---------detailNetWorkMapMap--LTE--====-----"
				 * + detailNetWorkMapMap);
				 * //logger.info("keyArr[0]-------LTE---"+keyArr[0]);
				 * //logger.info("keyArr[1]-------LTE---"+keyArr[1]);
				 * //logger.info("keyArr[2]-------LTE---"+keyArr[2]);
				 * //System
				 * .out.println("keyArrSplitBy_-------LTE---"+keyArrSplitBy_[1])
				 */;
				HashMap<String, HashMap<String, ArrayList<Integer>>> detailMap = (HashMap<String, HashMap<String, ArrayList<Integer>>>) detailNetWorkMapMap
						.get("LTE");
				// //logger.info(keyArrSplitBy_[1]+"------detailMap=LTE========="+detailMap);
				HashMap<String, ArrayList<Integer>> finalVales = detailMap
						.get(keyArrSplitBy_[1]);
				// //logger.info("finalVales----LTE-----"+finalVales);
				ArrayList<Integer> finalArrayList = finalVales.get(keyArr[1]);
				// HashMap<String,
				// ArrayList<Integer>>chartvalues=finalVales.get(keyArr[1]);
				// //logger.info("finalArrayList----va-----"+finalArrayList);
				arrayList = finalArrayList.toString().substring(1,
						finalArrayList.toString().length() - 1);
			}

		}

		// //logger.info("arrayList----------++----------"+arrayList);
		return arrayList;
	}

	public void mapTestcaseListener(AjaxBehaviorEvent event) {
		String result = "called by "
				+ event.getComponent().getClass().getName();
		Map<String, String> testcase = new HashMap<String, String>();
		FacesContext context = FacesContext.getCurrentInstance();
		String newValue = (String) ((UIOutput) event.getSource()).getValue();
		ReportDao reportDao = new ReportDaoImpl();
		testcase = reportDao.getMarketForTest(newValue);
		setMarketmapId("0");
		context.getExternalContext().getSessionMap()
				.remove("testSelectNameMap");
		context.getExternalContext().getSessionMap().put("testSelectNameMap",
				testcase);
		Map<String, String> testtypecase = new HashMap<String, String>();
		testtypecase = reportDao.getFreqBandList(getTestCaseName());
		FacesContext context1 = FacesContext.getCurrentInstance();
//		setfrequencyPlan("0");
		context1.getExternalContext().getSessionMap().remove("freqBandMap");
		context1.getExternalContext().getSessionMap().put("freqBandMap",
				testtypecase);
	}

	public void getFreqBandbyTestnameListener(String testName) {
		Map<String, String> testcase = new HashMap<String, String>();
		ReportDao reportDao = new ReportDaoImpl();
		testcase = reportDao.getFreqBandList(testName);
		FacesContext context = FacesContext.getCurrentInstance();
		//System.out.println("band Name -"+testcase.toString());
		//		setfrequencyPlan("0");
		context.getExternalContext().getSessionMap().remove("freqBandMap");
		context.getExternalContext().getSessionMap().put("freqBandMap",testcase);
	}
	
	public void mapTestTypeListener(AjaxBehaviorEvent event) {
		// logger.info("in chage");
		String result = "called by "
				+ event.getComponent().getClass().getName();
		Map<String, String> testtypecase = new HashMap<String, String>();
		FacesContext context = FacesContext.getCurrentInstance();
		String marketId = (String) ((UIOutput) event.getSource()).getValue();
		ReportDao reportDao = new ReportDaoImpl();
		testtypecase = reportDao.getTestTypeForMarket(getTestCaseName(),
				marketId);
		context.getExternalContext().getSessionMap().remove("testTypeNameMap");
		context.getExternalContext().getSessionMap().put("testTypeNameMap",
				testtypecase);
	}
	
	
	public void freqTypeListener(AjaxBehaviorEvent event) {
		// logger.info("in chage");
		String result = "called by "
				+ event.getComponent().getClass().getName();
		Map<String, String> testtypecase = new HashMap<String, String>();
		FacesContext context = FacesContext.getCurrentInstance();
		String marketId = (String) ((UIOutput) event.getSource()).getValue();
		ReportDao reportDao = new ReportDaoImpl();
		testtypecase = reportDao.getFreqBandList(getTestCaseName());
		context.getExternalContext().getSessionMap().remove("freqBandMap");
		context.getExternalContext().getSessionMap().put("freqBandMap",
				testtypecase);
	}

	public void resetSelectionForMapRep() {
		Map<String, String> testtypecase = new HashMap<String, String>();
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getSessionMap()
				.remove("testSelectNameMap");
		context.getExternalContext().getSessionMap().remove("testTypeNameMap");
		context.getExternalContext().getSessionMap().remove("freqBandMap");
	}

	public void removeSession(FacesContext context) {
		context.getExternalContext().getSessionMap().remove("lattitudes");
		context.getExternalContext().getSessionMap().remove("longitudes");
		context.getExternalContext().getSessionMap().remove("testName");
		context.getExternalContext().getSessionMap().remove("signalStrength");
		context.getExternalContext().getSessionMap().remove("networkType");
		context.getExternalContext().getSessionMap().remove("networkData");
		context.getExternalContext().getSessionMap().remove("networkRoaming");
		context.getExternalContext().getSessionMap().remove("networkMnc");
		context.getExternalContext().getSessionMap().remove("networkMcc");
		context.getExternalContext().getSessionMap().remove("cellLocationLac");
		context.getExternalContext().getSessionMap().remove("cellLocationCid");
		context.getExternalContext().getSessionMap()
				.remove("signalStrengthSnr");
		context.getExternalContext().getSessionMap().remove("devicePhoneType");
		context.getExternalContext().getSessionMap().remove(
				"timeStampForEachSample");
		context.getExternalContext().getSessionMap().remove("neighbourInfo");
		context.getExternalContext().getSessionMap().remove("mapReportType");
		context.getExternalContext().getSessionMap().remove(
				"signalStrengthCDMACIO");
		context.getExternalContext().getSessionMap().remove(
				"signalStrengthEVDOECIO");
		context.getExternalContext().getSessionMap().remove("VQuadTimestamp");
		context.getExternalContext().getSessionMap().remove("VQuadLocation");
		context.getExternalContext().getSessionMap().remove("VQuadPhoneID");
		context.getExternalContext().getSessionMap().remove("DegradedFilename");
		context.getExternalContext().getSessionMap().remove("Rating");
		context.getExternalContext().getSessionMap().remove("PESQ");
		context.getExternalContext().getSessionMap()
				.remove("PESQAverageOffset");
		context.getExternalContext().getSessionMap().remove("PESQMaxOffset");
		context.getExternalContext().getSessionMap().remove("PESQMinOffset");
		context.getExternalContext().getSessionMap()
				.remove("NumberAllClipping");
		context.getExternalContext().getSessionMap().remove(
				"DurationALLClipping");
		context.getExternalContext().getSessionMap().remove("throughput");
		context.getExternalContext().getSessionMap().remove(
				"deviceInfoCallDropList");
		context.getExternalContext().getSessionMap().remove(
		"signalStrengthCDMACIO");
		context.getExternalContext().getSessionMap().remove(
		"signalStrengthEVDOECIO");
		context.getExternalContext().getSessionMap().remove(
		"signalStrengthSnr");
		context.getExternalContext().getSessionMap().remove(
		"signalStrengthCDMA");
		context.getExternalContext().getSessionMap().remove(
		"signalStrengthEVDO");
		context.getExternalContext().getSessionMap().remove(
		"signalStrengthGSMStr");
		
	}

	public static void removeVoiceQtySession(FacesContext context) {
		context.getExternalContext().getSessionMap().remove("VoiceStatusList");
		context.getExternalContext().getSessionMap().remove("lattitudes");
		context.getExternalContext().getSessionMap().remove("longitudes");
		context.getExternalContext().getSessionMap().remove("testName");
		context.getExternalContext().getSessionMap().remove("VQuadTimestamp");
		context.getExternalContext().getSessionMap().remove("CallTimestamp");
		context.getExternalContext().getSessionMap().remove("VQuadLocation");
		context.getExternalContext().getSessionMap().remove("VQuadPhoneID");
		context.getExternalContext().getSessionMap().remove("DegradedFilename");
		context.getExternalContext().getSessionMap().remove("Rating");
		context.getExternalContext().getSessionMap().remove("PESQ");
		context.getExternalContext().getSessionMap()
				.remove("PESQAverageOffset");
		context.getExternalContext().getSessionMap().remove("PESQMaxOffset");
		context.getExternalContext().getSessionMap().remove("PESQMinOffset");
		context.getExternalContext().getSessionMap()
				.remove("NumberAllClipping");
		context.getExternalContext().getSessionMap().remove(
				"DurationALLClipping");
		context.getExternalContext().getSessionMap().remove("mapReportType");
		context.getExternalContext().getSessionMap().remove("signalStrength");
		context.getExternalContext().getSessionMap().remove("networkType");
		context.getExternalContext().getSessionMap().remove("networkData");
		context.getExternalContext().getSessionMap().remove("networkRoaming");
		context.getExternalContext().getSessionMap().remove("networkMnc");
		context.getExternalContext().getSessionMap().remove("networkMcc");
		context.getExternalContext().getSessionMap().remove("cellLocationLac");
		context.getExternalContext().getSessionMap().remove("cellLocationCid");
		context.getExternalContext().getSessionMap()
				.remove("signalStrengthSnr");
		context.getExternalContext().getSessionMap().remove("devicePhoneType");
		context.getExternalContext().getSessionMap().remove(
				"timeStampForEachSample");
		context.getExternalContext().getSessionMap().remove("neighbourInfo");
		context.getExternalContext().getSessionMap().remove(
				"signalStrengthCDMACIO");
		context.getExternalContext().getSessionMap().remove(
				"signalStrengthEVDOECIO");
		context.getExternalContext().getSessionMap().remove("throughput");
		context.getExternalContext().getSessionMap().remove(
				"signalStrengthLTERSRP");
		context.getExternalContext().getSessionMap().remove(
				"signalStrengthLTERSRQ");
		context.getExternalContext().getSessionMap().remove(
				"signalStrengthLTERSSNR");
		context.getExternalContext().getSessionMap().remove(
				"signalStrengthLTECQI");
		context.getExternalContext().getSessionMap().remove(
				"signalStrengthRating");
		context.getExternalContext().getSessionMap().remove(
				"STGDeviceSignalStrengthList");
		context.getExternalContext().getSessionMap().remove(
				"STGDeviceNetworkTypeList");
		context.getExternalContext().getSessionMap().remove(
				"STGDeviceSourceCellIdList");
		context.getExternalContext().getSessionMap().remove("statusList");
		context.getExternalContext().getSessionMap().remove(
				"deviceinfolatitude");
		context.getExternalContext().getSessionMap().remove("Vquadlattitude");
	}

	public void removeThroughputSession(FacesContext context) {
		context.getExternalContext().getSessionMap().remove("lattitudes");
		context.getExternalContext().getSessionMap().remove("longitudes");
		context.getExternalContext().getSessionMap().remove("testName");
		context.getExternalContext().getSessionMap().remove("signalStrength");
		context.getExternalContext().getSessionMap().remove("networkType");
		context.getExternalContext().getSessionMap().remove("networkData");
		context.getExternalContext().getSessionMap().remove("networkRoaming");
		context.getExternalContext().getSessionMap().remove("networkMnc");
		context.getExternalContext().getSessionMap().remove("networkMcc");
		context.getExternalContext().getSessionMap()
				.remove("signalStrengthLTT");
		context.getExternalContext().getSessionMap().remove("cellLocationLac");
		context.getExternalContext().getSessionMap().remove("cellLocationCid");
		context.getExternalContext().getSessionMap()
				.remove("signalStrengthSnr");
		context.getExternalContext().getSessionMap().remove("devicePhoneType");
		context.getExternalContext().getSessionMap().remove(
				"timeStampForEachSample");
		context.getExternalContext().getSessionMap().remove("neighbourInfo");
		context.getExternalContext().getSessionMap().remove("mapReportType");
		context.getExternalContext().getSessionMap().remove(
				"signalStrengthCDMACIO");
		context.getExternalContext().getSessionMap().remove(
				"signalStrengthEVDOECIO");
		context.getExternalContext().getSessionMap().remove("VQuadTimestamp");
		context.getExternalContext().getSessionMap().remove("VQuadLocation");
		context.getExternalContext().getSessionMap().remove("VQuadPhoneID");
		context.getExternalContext().getSessionMap().remove("DegradedFilename");
		context.getExternalContext().getSessionMap().remove("Rating");
		context.getExternalContext().getSessionMap().remove("PESQ");
		context.getExternalContext().getSessionMap()
				.remove("PESQAverageOffset");
		context.getExternalContext().getSessionMap().remove("PESQMaxOffset");
		context.getExternalContext().getSessionMap().remove("PESQMinOffset");
		context.getExternalContext().getSessionMap()
				.remove("NumberAllClipping");
		context.getExternalContext().getSessionMap().remove(
				"DurationALLClipping");
		context.getExternalContext().getSessionMap().remove("throughput");
		context.getExternalContext().getSessionMap().remove(
				"signalStrengthLTERSRP");
		context.getExternalContext().getSessionMap().remove(
				"signalStrengthLTERSRQ");
		context.getExternalContext().getSessionMap().remove(
				"signalStrengthLTERSSNR");
		context.getExternalContext().getSessionMap().remove(
				"signalStrengthLTECQI");
		context.getExternalContext().getSessionMap().remove(
				"signalStrengthRating");
		context.getExternalContext().getSessionMap().remove(
				"STGDeviceTimeStampList");
		context.getExternalContext().getSessionMap().remove(
				"STGDeviceSignalStrengthList");
		context.getExternalContext().getSessionMap().remove("cellLocationCID");
	}

	public static void main(String[] args) {
		/*
		 * org.json.me.JSONArray jsonArray = new org.json.me.JSONArray(); String
		 * name = "hello"; jsonArray.put(Arrays.asList(name.split(",")));
		 * //logger.info(jsonArray);
		 */

		matchCallTimestamp("2014-06-13 10:08:15.000", "2014-06-13 11:05:23.940");
		generateVoiceConnectivityMap("D3MNM0620", "mo", "35", "dev", "file",
				"mini", "0", 0);
	}

	public String getConfigurationToSave() throws Exception {
		// logger.info("getConfigurationToSave...............");
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			setSucessMessage("");
			setErrorMessage("");
			context.getExternalContext().getSessionMap().remove("configObj_0");
			context.getExternalContext().getSessionMap().remove("configObj_1");
			context.getExternalContext().getSessionMap().remove("configObj_2");
			context.getExternalContext().getSessionMap().remove("configObj_3");
			String configurationId = context.getExternalContext()
					.getRequestParameterMap().get("existingConfigName");
			ReportDao rDao = new ReportDaoImpl();
			ReportDaoImpl reportDao = new ReportDaoImpl();
			Map<String, String> filesIdsMap = new LinkedHashMap<String, String>();
			HashMap<String, List<String>> marketFileListMap = new HashMap<String, List<String>>();
			List<ConfigBean> listCompareData = new ArrayList<ConfigBean>();
			listCompareData = rDao.getConfigurationDetails(configurationId);
			// logger.info("listCompareData size :"+listCompareData.size());

			ReportBean reportBean = (ReportBean) context.getExternalContext()
					.getSessionMap().get("reportBean");
			List<ReportBean> configComments = new ArrayList<ReportBean>();
			configComments = rDao.getConfigurationComments(configurationId);

			String marketString = "";
			String fileString = "";
			String allMarketsStr = "";
			String allDevicesStr = "";
			String allTestStr = "";
			String allFilesStr = "";

			String allMarkets = "";
			String allDevices = "";
			String hiddenTestcaseArr = "";
			String filesSelectedArr = "";

			Map<String, List<String>> marketwiseFiles = new LinkedHashMap<String, List<String>>();
			org.json.me.JSONObject jsonObj = new org.json.me.JSONObject();
			org.json.me.JSONArray jsonArray = new org.json.me.JSONArray();

			if (listCompareData.size() > 0) {
				reportBean.setTestSummaryComment(configComments.get(0)
						.getTestSummaryComment());
				reportBean.setTestCommentKpi1(configComments.get(0)
						.getTestCommentKpi1());
				reportBean.setTestCommentKpi2(configComments.get(1)
						.getTestCommentKpi2());
				reportBean.setTestCommentKpi3(configComments.get(2)
						.getTestCommentKpi3());
				reportBean.setTestCommentKpi4(configComments.get(3)
						.getTestCommentKpi4());
				reportBean.setTestCommentKpi5(configComments.get(4)
						.getTestCommentKpi5());
				reportBean.setTestCommentKpi6(configComments.get(5)
						.getTestCommentKpi6());
				reportBean.setTestCommentKpi7(configComments.get(6)
						.getTestCommentKpi7());
				for (int i = 0; i < listCompareData.size(); i++) {
					String reportType = listCompareData.get(i).getReportType();

					allDevicesStr = listCompareData.get(i).getDeviceName();
					allDevices = allDevices + allDevicesStr + ",";
					Set<String> deviceSet = new HashSet<String>(Arrays
							.asList(allDevicesStr.split(",")));
					List<String> allDevicesList = new ArrayList<String>(
							deviceSet);
					// //logger.info("allDevices............... :"+allDevices);

					allTestStr = listCompareData.get(i).getTestCaseName();
					hiddenTestcaseArr = hiddenTestcaseArr + allTestStr + ",";
					Set<String> testSet = new HashSet<String>(Arrays
							.asList(allTestStr.split(",")));
					List<String> allTestsList = new ArrayList<String>(testSet);
					// //logger.info("hiddenTestcaseArr............... :"+hiddenTestcaseArr);

					allMarketsStr = listCompareData.get(i).getMarketmapId();
					allMarkets = allMarkets + allMarketsStr + ",";
					Set<String> marketSet = new HashSet<String>(Arrays
							.asList(allMarketsStr.split(",")));
					List<String> allMarketsList = new ArrayList<String>(
							marketSet);
					// //logger.info("allMarkets............... :"+allMarkets);

					net.sf.json.JSONObject market_Json = new net.sf.json.JSONObject();
					net.sf.json.JSONObject file_Json = new net.sf.json.JSONObject();
					setMarketId(allMarketsList
							.toArray(new String[allMarketsList.size()]));
					marketId = allMarketsList.toArray(new String[allMarketsList
							.size()]);
					jsonArray = new org.json.me.JSONArray();
					for (int j = 0; j < allMarketsList.size(); j++) {
						String marketId = allMarketsList.get(j);
						String marketName = new ReportDaoImpl()
								.getMarketName(marketId);
						marketString = marketString + marketName + ",";
						jsonArray.put(marketName);
						filesIdsMap.putAll(rDao.getFiles(allTestStr,
								allDevicesStr, reportType, marketId));
						marketFileListMap.put(allDevicesStr + "_" + allTestStr
								+ "_" + marketId + "_" + reportType, rDao
								.getFilesForMarket(allTestStr, allDevicesStr,
										reportType, marketId));
						marketwiseFiles.putAll(new ReportDaoImpl()
								.getFilesListForMarket(allTestStr,
										allDevicesStr, reportType, marketId));
					}

					// //logger.info("filesIdsMap:::"+filesIdsMap.toString());
					// //logger.info("marketFileListMap:::"+marketFileListMap.toString());

					marketString = marketString.substring(0, marketString
							.length() - 1);
					jsonObj.put("marketLabel", jsonArray);

					jsonArray = new org.json.me.JSONArray();
					String fileNameStr = reportDao
							.getFileNamesForFileIds(listCompareData.get(i)
									.getFileId());
					// //logger.info("fileNameStr............... :"+fileNameStr);
					jsonArray.put(fileNameStr.replaceAll(":", "__--__"));

					filesSelectedArr = filesSelectedArr + fileNameStr + ",";
					// //logger.info("filesSelectedArr............... :"+filesSelectedArr);

					Map<String, List<String>> marketWiseFilesMap = reportDao
							.populateMarketWiseFilesForSavedConfig(
									allDevicesStr, allTestStr, allMarketsStr,
									reportType, marketString);

					jsonObj.put("deviceName", allDevicesStr);
					jsonObj.put("testName", allTestStr);
					jsonObj.put("reportType", reportType);
					jsonObj.put("filesName", jsonArray);

					// //logger.info("allMarketsList----getConfigurationToSave----------"+allMarketsList);
					jsonObj.put("marketName", allMarketsList);

					jsonArray = new org.json.me.JSONArray();
					String fileIds = listCompareData.get(i).getFileId();
					jsonArray.put(fileIds);
					jsonObj.put("filesNameId", jsonArray);
					// //logger.info("fileIds----getConfigurationToSave----------"+fileIds);

					context.getExternalContext().getSessionMap().put(
							"configObj_" + i, jsonObj.toString());
					// //logger.info(i+" :jsonObj.toString()----getConfigurationToSave----------"+jsonObj.toString());

				}

				allMarkets = allMarkets.substring(0, allMarkets.length() - 1);
				allDevices = allDevices.substring(0, allDevices.length() - 1);
				hiddenTestcaseArr = hiddenTestcaseArr.substring(0,
						hiddenTestcaseArr.length() - 1);
				filesSelectedArr = filesSelectedArr.substring(0,
						filesSelectedArr.length() - 1);

				context.getExternalContext().getSessionMap().put("filesIdsMap",
						filesIdsMap);
				context.getExternalContext().getSessionMap().put(
						"marketFileListMap", marketFileListMap);
				context.getExternalContext().getSessionMap().put(
						"allMarketsList", allMarkets);
				context.getExternalContext().getSessionMap().put(
						"allDevicesList", allDevices);
				context.getExternalContext().getSessionMap().put(
						"allTestsList", hiddenTestcaseArr);
				context.getExternalContext().getSessionMap().put(
						"allFilesList",
						filesSelectedArr.replaceAll(":", "__--__"));
				context.getExternalContext().getSessionMap().put(
						"marketwiseFiles", marketwiseFiles);
				context.getExternalContext().getSessionMap().put("reportBean",
						reportBean);

				context.getExternalContext().getSessionMap().put("allMarkets",
						allMarkets);
				context.getExternalContext().getSessionMap().put("allDevices",
						allDevices);
				context.getExternalContext().getSessionMap().put(
						"hiddenTestcaseArr", hiddenTestcaseArr);
				context.getExternalContext().getSessionMap().put(
						"filesSelectedArr",
						filesSelectedArr.replaceAll(":", "__--__"));

				context.getExternalContext().getSessionMap().put("isDownload",
						"true");
				context.getExternalContext().getSessionMap().put(
						"retrievedConfig", "true");
				/*
				 * //logger.info("allMarketsStr1:"+context.getExternalContext
				 * ().getSessionMap().get("allMarkets" ));
				 * //logger.info("allDevicesStr1:"
				 * +context.getExternalContext
				 * ().getSessionMap().get("allDevices" ));
				 * //logger.info("allTestStr1:"
				 * +context.getExternalContext
				 * ().getSessionMap().get("hiddenTestcaseArr"));
				 * //logger.info
				 * ("allFilesStr1:"+context.getExternalContext
				 * ().getSessionMap().get("filesSelectedArr") );
				 */

			} else {
				if (configurationId.equalsIgnoreCase("Select Configuration")) {
					errorMessage = "Please select existing configuration.";
				} else {
					errorMessage = "No data to display for the selected configuration.";
				}
				reportBean.setErrorMessage(errorMessage);
				context.getExternalContext().getSessionMap().put(
						"retrievedConfig", "false");
				context.getExternalContext().getSessionMap().put("reportBean",
						reportBean);
			}

		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		}
		return "configurationPage";
	}

	public String saveAsDraft() throws Exception {
		// logger.info("saveAsDraft :");
		String lConfigName = "";
		String lConfigRouteName = "";
		String lDataType = "";
		String lMarkgetId = "";
		String lFileNameId = "";
		String lCreatedBy = "";
		String lUpdatedBy = "";
		String sucessMessage = "";
		String lDeviceName = "";
		String errorMessage = "";

		// statusId 1 for draft , 2 for save
		int statusId = 1;
		ReportDao reportdao = new ReportDaoImpl();
		FacesContext context = FacesContext.getCurrentInstance();
		ReportBean reportBean = (ReportBean) context.getExternalContext()
				.getSessionMap().get("reportBean");
		String tUserId = context.getExternalContext().getSessionMap().get(
				"loggedInUserID").toString();

		// Update Counter table for each generate map report & save
		// configuration.
		// counterType for generate map report is 0 and save configuration is 1.
		Integer counterType = 1;
		Integer tempUserId = Integer.valueOf(tUserId);

		JSONArray jso = new JSONArray();
		String jsoStr = context.getExternalContext().getRequestParameterMap()
				.get("reportingParams");
		JSONObject jsnobject = new JSONObject(jsoStr);
		org.json.me.JSONArray jsoArr = jsnobject.getJSONArray("finalJso");
		lConfigName = reportConfigName.trim();
		Integer userId = Integer.valueOf(tUserId);

		Map<Integer, String> kpiCommentMap = new LinkedHashMap<Integer, String>();
		kpiCommentMap.put(1, testCommentKpi1.trim());
		kpiCommentMap.put(2, testCommentKpi2.trim());
		kpiCommentMap.put(3, testCommentKpi3.trim());
		kpiCommentMap.put(4, testCommentKpi4.trim());
		kpiCommentMap.put(5, testCommentKpi5.trim());
		kpiCommentMap.put(6, testCommentKpi6.trim());
		kpiCommentMap.put(7, testCommentKpi7.trim());

		configurationNameList = (List<String>) context.getExternalContext()
				.getSessionMap().get("configurationNameList");

		// logger.info("marketId................................................"+marketId);

		if (lConfigName != null && lConfigName != "") {
			if (null != configurationNameList) {
				if (!configurationNameList.contains(lConfigName)) {
					Integer savedTestConfigId = reportdao.insertConfigMaster(
							lConfigName, userId, statusId);
					reportdao.manageCounter(tempUserId, counterType);
					for (int j = 0; j < jsoArr.length(); j++) {
						org.json.me.JSONObject configObi = new org.json.me.JSONObject(
								jsoArr.get(j).toString());
						lConfigRouteName = configObi.getString("testName");
						lDataType = configObi.getString("reportType");
						lMarkgetId = configObi.getString("marketName").replace(
								"[", "").replace("]", "");
						lFileNameId = configObi.getString("filesNameId")
								.replace("[", "").replace("]", "");
						String masterdeviceName = configObi
								.getString("deviceName");
						// logger.info(j+" :details :"+lConfigName+" "+lConfigRouteName+
						// " "+lDataType+" "+lMarkgetId+" "+lFileNameId);
						reportdao.insertConfigCompare(savedTestConfigId,
								userId, lConfigRouteName, lDataType,
								lMarkgetId, lFileNameId, masterdeviceName);
					}
					sucessMessage = "Configuration '" + lConfigName
							+ "' saved sucessfully.";
				} else {
					errorMessage = "Configuration '" + lConfigName
							+ "' already exists.";
					// //logger.info("errorMessage:"+errorMessage);
				}
			} else {
				Integer savedTestConfigId = reportdao.insertConfigMaster(
						lConfigName, userId, statusId);
				reportdao.manageCounter(tempUserId, counterType);
				for (int j = 0; j < jsoArr.length(); j++) {
					org.json.me.JSONObject configObi = new org.json.me.JSONObject(
							jsoArr.get(j).toString());
					lConfigRouteName = configObi.getString("testName");
					lDataType = configObi.getString("reportType");
					lMarkgetId = configObi.getString("marketName").replace("[",
							"").replace("]", "");
					lFileNameId = configObi.getString("filesNameId").replace(
							"[", "").replace("]", "");
					String masterdeviceName = configObi.getString("deviceName");
					// logger.info(j+" :details :"+lConfigName+" "+lConfigRouteName+
					// " "+lDataType+" "+lMarkgetId+" "+lFileNameId);
					reportdao.insertConfigCompare(savedTestConfigId, userId,
							lConfigRouteName, lDataType, lMarkgetId,
							lFileNameId, masterdeviceName);
				}
				sucessMessage = "Configuration '" + lConfigName
						+ "' saved sucessfully.";
			}
		} else {
			errorMessage = "Configuration name is empty.";
		}
		reportBean.setSucessMessage(sucessMessage);
		reportBean.setErrorMessage(errorMessage);
		context.getExternalContext().getSessionMap().put("reportBean",
				reportBean);
		configurationNameMap = reportdao.getConfigurations();
		context.getExternalContext().getSessionMap().put(
				"configurationNameMap", configurationNameMap);
		return "configurationPage";
	}

	/**
	 * Save Report configuration
	 */
	public String saveConfiguration() throws Exception {
		// logger.info("inside saveReportConfiguration.... :");
		String lConfigName = "";
		String lConfigRouteName = "";
		String lDataType = "";
		String lMarkgetId = "";
		String lFileNameId = "";
		String lCreatedBy = "";
		String lUpdatedBy = "";
		String sucessMessage = "";
		String lDeviceName = "";
		String errorMessage = "";
		Integer savedTestConfigId = 0;

		// statusId 1 for draft , 2 for save
		int statusId = 2;
		ReportDao reportdao = new ReportDaoImpl();
		FacesContext context = FacesContext.getCurrentInstance();
		ReportBean reportBean = (ReportBean) context.getExternalContext()
				.getSessionMap().get("reportBean");
		String tUserId = context.getExternalContext().getSessionMap().get(
				"loggedInUserID").toString();

		// Update Counter table for each generate map report & save
		// configuration.
		// counterType for generate map report is 0 and save configuration is 1.
		Integer counterType = 1;
		Integer tempUserId = Integer.valueOf(tUserId);

		JSONArray jso = new JSONArray();
		String jsoStr = context.getExternalContext().getRequestParameterMap()
				.get("reportingParams");
		JSONObject jsnobject = new JSONObject(jsoStr);
		org.json.me.JSONArray jsoArr = jsnobject.getJSONArray("finalJso");
		lConfigName = reportConfigName.trim();
		Integer userId = Integer.valueOf(tUserId);

		Map<Integer, String> kpiCommentMap = new LinkedHashMap<Integer, String>();
		kpiCommentMap.put(1, testCommentKpi1.trim());
		kpiCommentMap.put(2, testCommentKpi2.trim());
		kpiCommentMap.put(3, testCommentKpi3.trim());
		kpiCommentMap.put(4, testCommentKpi4.trim());
		kpiCommentMap.put(5, testCommentKpi5.trim());
		kpiCommentMap.put(6, testCommentKpi6.trim());
		kpiCommentMap.put(7, testCommentKpi7.trim());

		configurationNameList = (List<String>) context.getExternalContext()
				.getSessionMap().get("configurationNameList");
		// //logger.info(lConfigName.length()+"  lConfigName-----"+lConfigName);
		// //logger.info("marketId................................................"+marketId);

		if (lConfigName != null && lConfigName != " "
				&& lConfigName.length() != 0) {
			if (null != configurationNameList) {
				if (!configurationNameList.contains(lConfigName)) {
					savedTestConfigId = reportdao.insertConfigMaster(
							lConfigName, userId, statusId);
					HashMap<String, HashMap<String, List<HealthIndexTo>>> kpiHealthIndexScoreMap = (HashMap<String, HashMap<String, List<HealthIndexTo>>>) context
							.getExternalContext().getSessionMap().get(
									"testWiseMarketMap");
					if (null != kpiHealthIndexScoreMap) {
						new ReportConfigHelper().saveHealthIndicies(
								kpiHealthIndexScoreMap, savedTestConfigId,
								userId);
					}
					reportdao.insertConfigComments(savedTestConfigId, userId,
							testSummaryComment);
					reportdao.insertKpiComments(savedTestConfigId,
							kpiCommentMap, userId);
					reportdao.manageCounter(tempUserId, counterType);
					generatedMapCount = reportdao.getGeneratedMapCount(
							counterType, userId);
					setGeneratedMapCount(generatedMapCount);
					// logger.info("savedConfigCount :"+generatedMapCount);

					for (int j = 0; j < jsoArr.length(); j++) {
						org.json.me.JSONObject configObi = new org.json.me.JSONObject(
								jsoArr.get(j).toString());
						lConfigRouteName = configObi.getString("testName");
						lDataType = configObi.getString("reportType");
						lMarkgetId = configObi.getString("marketName").replace(
								"[", "").replace("]", "");
						lFileNameId = configObi.getString("filesNameId")
								.replace("[", "").replace("]", "");
						String masterdeviceName = configObi
								.getString("deviceName");
						// logger.info(j+" :details :"+lConfigName+" "+lConfigRouteName+
						// " "+lDataType+" "+lMarkgetId+" "+lFileNameId);
						reportdao.insertConfigCompare(savedTestConfigId,
								userId, lConfigRouteName, lDataType,
								lMarkgetId, lFileNameId, masterdeviceName);
					}
					sucessMessage = "Configuration '" + lConfigName
							+ "' saved sucessfully.";
				} else {
					errorMessage = "Configuration '" + lConfigName
							+ "' already exists.";
					// //logger.info("errorMessage:"+errorMessage);
				}
			} else {
				savedTestConfigId = reportdao.insertConfigMaster(lConfigName,
						userId, statusId);
				HashMap<String, HashMap<String, List<HealthIndexTo>>> kpiHealthIndexScoreMap = (HashMap<String, HashMap<String, List<HealthIndexTo>>>) context
						.getExternalContext().getSessionMap().get(
								"testWiseMarketMap");
				new ReportConfigHelper().saveHealthIndicies(
						kpiHealthIndexScoreMap, savedTestConfigId, userId);
				reportdao.insertConfigComments(savedTestConfigId, userId,
						testSummaryComment);
				reportdao.insertKpiComments(savedTestConfigId, kpiCommentMap,
						userId);
				reportdao.manageCounter(tempUserId, counterType);
				setGeneratedMapCount(generatedMapCount);
				// logger.info("savedConfigCount :"+generatedMapCount);
				for (int j = 0; j < jsoArr.length(); j++) {
					org.json.me.JSONObject configObi = new org.json.me.JSONObject(
							jsoArr.get(j).toString());
					lConfigRouteName = configObi.getString("testName");
					lDataType = configObi.getString("reportType");
					lMarkgetId = configObi.getString("marketName").replace("[",
							"").replace("]", "");
					lFileNameId = configObi.getString("filesNameId").replace(
							"[", "").replace("]", "");
					String masterdeviceName = configObi.getString("deviceName");
					// logger.info(j+" :details :"+lConfigName+" "+lConfigRouteName+
					// " "+lDataType+" "+lMarkgetId+" "+lFileNameId);
					reportdao.insertConfigCompare(savedTestConfigId, userId,
							lConfigRouteName, lDataType, lMarkgetId,
							lFileNameId, masterdeviceName);

				}
				sucessMessage = "Configuration '" + lConfigName
						+ "' saved sucessfully.";
			}
			if (!configurationNameList.contains(lConfigName)) {
				new ReportConfigHelper()
						.saveQualityRangesForConfig(savedTestConfigId);
			}
		} else {
			errorMessage = "Configuration name is empty.";
		}
		reportBean.setSucessMessage(sucessMessage);
		reportBean.setErrorMessage(errorMessage);
		context.getExternalContext().getSessionMap().put("reportBean",
				reportBean);
		configurationNameMap = reportdao.getConfigurations();
		context.getExternalContext().getSessionMap().put(
				"configurationNameMap", configurationNameMap);
		return "configurationPage";
	}

	public String exportReport() throws Exception {
		final int DEFAULT_BUFFER_SIZE = 10240;
		String filePath = ""; // "c:\\Desktop\\Airometric_KP.xls";

		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) context
				.getExternalContext().getResponse();

		HealthIndexGenerator hig = new HealthIndexGenerator();
		filePath = hig.getFilePath();
		// logger.info("filePath:"+filePath);
		File file = new File(filePath);
		response.reset();
		response.setBufferSize(DEFAULT_BUFFER_SIZE);
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Length", String.valueOf(file.length()));
		response.setHeader("Content-Disposition", "attachment;filename=\""
				+ file.getName() + "\"");
		BufferedInputStream input = null;
		BufferedOutputStream output = null;
		try {
			input = new BufferedInputStream(new FileInputStream(file),
					DEFAULT_BUFFER_SIZE);
			output = new BufferedOutputStream(response.getOutputStream(),
					DEFAULT_BUFFER_SIZE);
			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			int length;
			while ((length = input.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}
		} finally {
			input.close();
			output.close();
		}
		context.responseComplete();
		return "exportPage";
	}

	public String exportPrecalReport() throws Exception {
		final int DEFAULT_BUFFER_SIZE = 10240;
		String filePath = ""; // "c:\\Desktop\\Airometric_KP.xls";

		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) context
				.getExternalContext().getResponse();

		String allTestStr = context.getExternalContext()
				.getRequestParameterMap().get("hiddenTestcaseArr");
		String marketId = context.getExternalContext().getRequestParameterMap()
				.get("marketName");

		String allMarkets = context.getExternalContext()
				.getRequestParameterMap().get("allMarkets");
		String precalParam = context.getExternalContext()
				.getRequestParameterMap().get("precalparam");
		// logger.info("precalParam-----------"+precalParam);
		filePath = PreprocessExport.exportReport(precalParam, allTestStr,
				allMarkets);
		// logger.info("filePath:"+filePath);
		File file = new File(filePath);
		response.reset();
		response.setBufferSize(DEFAULT_BUFFER_SIZE);
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Length", String.valueOf(file.length()));
		response.setHeader("Content-Disposition", "attachment;filename=\""
				+ file.getName() + "\"");
		BufferedInputStream input = null;
		BufferedOutputStream output = null;
		try {
			input = new BufferedInputStream(new FileInputStream(file),
					DEFAULT_BUFFER_SIZE);
			output = new BufferedOutputStream(response.getOutputStream(),
					DEFAULT_BUFFER_SIZE);
			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			int length;
			while ((length = input.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}
		} finally {
			input.close();
			output.close();
		}
		context.responseComplete();
		return "exportPage";
	}

	public String exportVoiceSummaryReport() throws Exception {
		final int DEFAULT_BUFFER_SIZE = 10240;
		String filePath = ""; // "c:\\Desktop\\Airometric_KP.xls";

		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) context
				.getExternalContext().getResponse();

		String allTestStr = context.getExternalContext()
				.getRequestParameterMap().get("hiddenTestcaseArr");
		String jsoStr = context.getExternalContext().getRequestParameterMap()
				.get("reportingParams");
		logger.info("jsoStr------------" + jsoStr);
		JSONObject jsnobject = new JSONObject(jsoStr);
		String jsoStrForRep = jsnobject.getString("finalJso");

		// logger.info("precalParam-----------"+precalParam);
		filePath = PreprocessorSummaryReportGenerator
				.triggerSummaryVoiceReps(jsoStrForRep);
		// logger.info("filePath:"+filePath);
		File file = new File(filePath);
		response.reset();
		response.setBufferSize(DEFAULT_BUFFER_SIZE);
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Length", String.valueOf(file.length()));
		response.setHeader("Content-Disposition", "attachment;filename=\""
				+ file.getName() + "\"");
		BufferedInputStream input = null;
		BufferedOutputStream output = null;
		try {
			input = new BufferedInputStream(new FileInputStream(file),
					DEFAULT_BUFFER_SIZE);
			output = new BufferedOutputStream(response.getOutputStream(),
					DEFAULT_BUFFER_SIZE);
			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			int length;
			while ((length = input.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}
		} finally {
			input.close();
			output.close();
		}
		context.responseComplete();
		return "exportPage";
	}

	public String convertDate(String date) {
		SimpleDateFormat inFormat = new SimpleDateFormat(
				"dd/MM/yyyy HH:mm:ss.SSS aa");
		SimpleDateFormat outFormat = new SimpleDateFormat(
				"dd/MM/yyyy HH:mm:ss.SSS");
		String time24 = null;
		;
		try {
			time24 = outFormat.format(inFormat.parse(date));
		} catch (ParseException e) {
			logger.error(e.getMessage());
		}
		return time24;
	}

	public String exportSummaryReport() throws Exception {
		final int DEFAULT_BUFFER_SIZE = 10240;
		String filePath = ""; // "c:\\Desktop\\Airometric_KP.xls";

		FacesContext context = FacesContext.getCurrentInstance();
		String userName = context.getExternalContext().getSessionMap().get(
				"userName").toString();
		HttpServletResponse response = (HttpServletResponse) context
				.getExternalContext().getResponse();
		// logger.info("precalParam-----------"+precalParam);
		filePath = PreprocessorSummaryReportGenerator
				.triggerSummaryReps(userName);
		// logger.info("filePath:"+filePath);
		File file = new File(filePath);
		response.reset();
		response.setBufferSize(DEFAULT_BUFFER_SIZE);
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Length", String.valueOf(file.length()));
		response.setHeader("Content-Disposition", "attachment;filename=\""
				+ file.getName() + "\"");
		BufferedInputStream input = null;
		BufferedOutputStream output = null;
		try {
			input = new BufferedInputStream(new FileInputStream(file),
					DEFAULT_BUFFER_SIZE);
			output = new BufferedOutputStream(response.getOutputStream(),
					DEFAULT_BUFFER_SIZE);
			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			int length;
			while ((length = input.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}
		} finally {
			input.close();
			output.close();
		}
		context.responseComplete();
		return "exportPage";
	}

	public static boolean matchCallTimestamp(String starttime, String Endtime) {
		boolean status = false;
		SimpleDateFormat first = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
		SimpleDateFormat format = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SSS");
		if (starttime.contains("11:59")) {
			logger.info("Yes");
		}
		if (Endtime.contains("12:")) {
			logger.info("Yes");
		}
		try {
			Date startDate = first.parse(starttime);
			starttime = format.format(startDate);

		} catch (ParseException e1) {
			logger.error(e1.getMessage());
		}

		Date d1 = null;
		Date d2 = null;
		int seconds = 0;
		try {
			// d1 = format.parse(starttime);
			// d2 = format.parse(Endtime);
			// long diff = d1.getTime() - d2.getTime();

			// long diffMilliSeconds = diff / 1000 % 60 % 60;
			// long diffSeconds = diff / 1000 % 60;
			// long diffMinutes = diff / (60 * 1000) % 60;
			// long diffHours = diff / (60 * 60 * 1000) % 24;
			// long diffDays = diff / (24 * 60 * 60 * 1000);
			// seconds= Math.abs((int) diffSeconds+(int)(60*diffMinutes));
			// logger.info(starttime);
			// logger.info(Endtime);
			d1 = format.parse(starttime);
			d2 = format.parse(Endtime);
			long diff = (d1.getTime() - d2.getTime()) / 1000;
			seconds = (int) diff;
			String temp = String.valueOf(seconds).replaceAll("-", "");
			seconds = Integer.parseInt(temp);
			// logger.info("seconds>>>>>" + seconds);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		/* New code changes by sheshadri */
		if (seconds >= 1 && seconds <= 10) {
			status = true;
		}
		// logger.info(seconds+"----status------"+status);
		return status;
	}

	public String exportReport1() throws Exception {
		final int DEFAULT_BUFFER_SIZE = 10240;
		String filePath = "";
		setErrorMessage("");
		// logger.info("inside exportReport1----------------");

		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) context
				.getExternalContext().getResponse();

		ReportBean reportBean = (ReportBean) context.getExternalContext()
				.getSessionMap().get("reportBean");
		ReportDaoImpl rdao = new ReportDaoImpl();
		String jsoStr = context.getExternalContext().getRequestParameterMap()
				.get("reportingParams");

		String configId = configName;
		if (!configId.equalsIgnoreCase("Select Configuration")) {
			String downLoadFileName = rdao.getConfigName(configId);
			// logger.info("exportReport1 fileName :"+downLoadFileName);
			String folder = HealthIndexGenerator.resourceBundle
					.getString("DOWNLOAD_FOLDER");
			// String path="C:\\uploadReport\\Report.pdf";
			String path = folder + "\\uploadReport\\" + downLoadFileName
					+ ".pdf";
			// logger.info("path :"+path);

			File file = new File(path);
			// logger.info("file :"+file);
			if (file.exists()) {
				response.reset();
				response.setBufferSize(DEFAULT_BUFFER_SIZE);
				response.setContentType("application/octet-stream");
				response.setHeader("Content-Length", String.valueOf(file
						.length()));
				response.setHeader("Content-Disposition",
						"attachment;filename=\"" + downLoadFileName + ".pdf"
								+ "\"");
				BufferedInputStream input = null;
				BufferedOutputStream output = null;
				try {
					input = new BufferedInputStream(new FileInputStream(file),
							DEFAULT_BUFFER_SIZE);
					output = new BufferedOutputStream(response
							.getOutputStream(), DEFAULT_BUFFER_SIZE);
					byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
					int length;
					while ((length = input.read(buffer)) > 0) {
						output.write(buffer, 0, length);
					}
				} catch (FileNotFoundException e) {
					reportBean.setErrorMessage("File '" + downLoadFileName
							+ "' not found");
				} catch (Exception e) {
					logger.error(e.getMessage());
				} finally {
					if (input != null) {
						input.close();
					}
					if (output != null) {
						output.close();
					}
				}
				context.responseComplete();
			} else {
				reportBean.setErrorMessage("File '" + downLoadFileName
						+ "' not found");
			}
		} else {
			reportBean.setErrorMessage("Please select file to download.");
		}

		context.getExternalContext().getSessionMap().put("reportBean",
				reportBean);
		return "exportPage1";
	}

	public String qxdmData() {
		FacesContext context = FacesContext.getCurrentInstance();
		String returnedval = "QxdmValue";

		// context.getExternalContext().getSessionMap().put("qxdmVal",new
		// QxdmDao().getAllRawData());
		return "";
	}

	public void downloadQxdmFile() throws IOException {
		final int DEFAULT_BUFFER_SIZE = 10240;
		String filePath = "";
		setErrorMessage("");
		String clickedDateOnMap = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get(
						"clickedDateOnMap");
		// logger.info("inside exportReport1--------+--------"+clickedDateOnMap);
		clickedDateOnMap = clickedDateOnMap.replaceAll("<b>", "").replaceAll(
				"</b>", "");
		new QxdmDao().getQXDMFile(clickedDateOnMap.trim());
		// logger.info("clickedDateOnMap----"+clickedDateOnMap);
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) context
				.getExternalContext().getResponse();

		ReportBean reportBean = (ReportBean) context.getExternalContext()
				.getSessionMap().get("reportBean");
		ReportDaoImpl rdao = new ReportDaoImpl();
		// new QxdmDao().getQXDMFile(clickedDateOnMap);
		String configId = configName;
		if (true) {
			String downLoadFileName = "QxdmFile";
			// logger.info("exportReport1 fileName :"+downLoadFileName);
			String folder = HealthIndexGenerator.resourceBundle
					.getString("DOWNLOAD_FOLDER");
			// String path="C:\\uploadReport\\Report.pdf";
			String path = folder + "\\QxdmResultFile\\qxdmChunkmerged.txt";
			// logger.info("path :"+path);

			File file = new File(path);
			// logger.info("file :"+file);
			if (file.exists()) {
				response.reset();
				response.setBufferSize(DEFAULT_BUFFER_SIZE);
				response.setContentType("text/plain");
				response.setHeader("Content-Length", String.valueOf(file
						.length()));
				response.setHeader("Content-Disposition",
						"attachment;filename=\"" + downLoadFileName + ".txt"
								+ "\"");
				BufferedInputStream input = null;
				BufferedOutputStream output = null;
				try {
					input = new BufferedInputStream(new FileInputStream(file),
							DEFAULT_BUFFER_SIZE);
					output = new BufferedOutputStream(response
							.getOutputStream(), DEFAULT_BUFFER_SIZE);
					byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
					int length;
					while ((length = input.read(buffer)) > 0) {
						output.write(buffer, 0, length);
					}
				} catch (FileNotFoundException e) {
					reportBean.setErrorMessage("File '" + downLoadFileName
							+ "' not found");
				} catch (Exception e) {
					logger.error(e.getMessage());
				} finally {
					if (input != null) {
						input.close();
					}
					if (output != null) {
						output.close();
					}
				}
				context.responseComplete();
			} else {
				reportBean.setErrorMessage("File '" + downLoadFileName
						+ "' not found");
			}
		} else {
			reportBean.setErrorMessage("Please select file to download.");
		}

		context.getExternalContext().getSessionMap().put("reportBean",
				reportBean);
	}

	public String migrateAccessToMysql() {
		logger.info("-----migrateAccessToMysql--------"
				+ testCaseNametoprocess);
		FacesContext context = FacesContext.getCurrentInstance();
		String marketId = context.getExternalContext().getRequestParameterMap()
				.get("marketId");
		String deviceId = context.getExternalContext().getRequestParameterMap()
				.get("deviceId");
		String testName = context.getExternalContext().getRequestParameterMap()
				.get("testName");
		String vqType = context.getExternalContext().getRequestParameterMap()
				.get("vqType");
		int diffHours = new Integer(context.getExternalContext()
				.getRequestParameterMap().get("diffHours"));
		logger.info("diffHours********" + diffHours);
		ExportColumns exCol = new ExportColumns();
		String accessTable = "";
		String sqlTable = "";
		if (vqType.equalsIgnoreCase("polqa")) {
			accessTable = "vqtpolqa";
			sqlTable = "stg_polqa_results";
		} else if (vqType.equalsIgnoreCase("pesq")) {
			accessTable = "vqtdata";
			sqlTable = "stg_vqt_results";
		} else {
			accessTable = "vquaddata";
			sqlTable = "stg_callevent_results";
		}
		exCol.getData(accessTable, sqlTable, marketId, testName, deviceId,
				vqType, diffHours);
		context.getExternalContext().getSessionMap().put(
				"precalculationmessage",
				"Migrationg Of '" + testName + "' has been Completed");
		return "mapGoogleDataSuccess";
	}

	public String precalculateTest() {
		// logger.info("--------------"+testCaseNametoprocess);
		FacesContext context = FacesContext.getCurrentInstance();
		//System.out.println("freq plan name - "+frequencyPlan);
		PreprocessorTrigger ppt = new PreprocessorTrigger(testCaseNametoprocess);
		ppt.triggerPreprocessin();
		/*
		 * This code will marge the freq bands with percal tables and update values of bands in freq_band column in all percal tables.
		 */
		if(!frequencyPlan.equals("0"))
		{
			ReportDao reportdao = new ReportDaoImpl();
			reportdao.margeFreqBandWithPrecalTable(testCaseNametoprocess,frequencyPlan,"pre_cal_voicequality_1");
			reportdao.margeFreqBandWithPrecalTable(testCaseNametoprocess,frequencyPlan,"pre_calc_voiceconnectivity_1");
			reportdao.margeFreqBandWithPrecalTable(testCaseNametoprocess,frequencyPlan,"pre_calc_voiceconnectivity_2");
			reportdao.margeFreqBandWithPrecalTable(testCaseNametoprocess,frequencyPlan,"ftpcalculationtable");
			reportdao.margeFreqBandWithPrecalTable(testCaseNametoprocess,frequencyPlan,"pre_calculation_tcp_level1");
			reportdao.margeFreqBandWithPrecalTable(testCaseNametoprocess,frequencyPlan,"pre_calculation_udp_level1");
		}
		if (null != testCaseNametoprocess) {
			context.getExternalContext().getSessionMap().put(
					"precalculationmessage",
					"Precalculation of '" + testCaseNametoprocess
							+ "' has been Completed");
		} else {
			context.getExternalContext().getSessionMap().put(
					"precalculationmessage", "");
		}
		
		return "mapGoogleDataSuccess";
	}

	public String isTestPreCalculated(AjaxBehaviorEvent event) {
		// logger.info("testName-----------"+testCaseNametoprocess);
		FacesContext context = FacesContext.getCurrentInstance();
		boolean isTestCompleted = new PreCalculationDBHelper()
				.isTestPrecalculated(testCaseNametoprocess);
		if (isTestCompleted) {
			context.getExternalContext().getSessionMap().put("processStatus",
					"Completed");
		} else {
			context.getExternalContext().getSessionMap().put("processStatus",
					"In Progress");
		}
		getFreqBandbyTestnameListener(testCaseNametoprocess);
		return "";
	}

	
	public String externalUpload() {
		// logger.info("externalUpload..............................");
		TestConfigDao testConfigDao = new TestConfigDaoImpl();
		FacesContext context = FacesContext.getCurrentInstance();
		MarketPlaceDao marketPlaceDao = new MarketPlaceDaoImpl();
		String userrole = context.getExternalContext().getSessionMap().get(
				"loggedInUserRoleID").toString();
		String userIdLogged = context.getExternalContext().getSessionMap().get(
				"loggedInUserID").toString();
		String userName = context.getExternalContext().getSessionMap().get(
				"userName").toString();
		marketPlaceMap = marketPlaceDao.getUploadMarketPlaceMap(userrole,
				userIdLogged);
		if (userrole.equals("superadmin")) {
			testNameMap = new TestConfigDaoImpl()
					.getTestNamesInMapInStgDevice();
		} else {
			Set<String> userNameList = new UserDaoImpl().getUsersOfSuperior(
					userIdLogged, userrole);
			testNameMap = new TestConfigDaoImpl()
					.getnTestNamesInMapInHierarchy(userNameList.toString()
							.substring(1, userNameList.toString().length() - 1));
		}
		context.getExternalContext().getSessionMap().remove("testNameMap");
		context.getExternalContext().getSessionMap().remove(
				"margetPlaceListSize");
		context.getExternalContext().getSessionMap().remove(
				"margetPlaceListdetails");
		context.getExternalContext().getSessionMap().put("testNameMap",
				testNameMap);
		context.getExternalContext().getSessionMap().put("differnceHours",
				Misc.getDiffHours());
		context.getExternalContext().getSessionMap().put("margetPlaceListSize",
				marketPlaceMap.size());
		context.getExternalContext().getSessionMap().put(
				"margetPlaceListdetails", marketPlaceMap);
		return "externalUploadPage";
	}

	public String precalculateTestNew() {
		// logger.info("--------------"+testCaseNametoprocess);
		FacesContext context = FacesContext.getCurrentInstance();
		PreprocessorTrigger ppt = new PreprocessorTrigger(testCaseNametoprocess);
		ppt.triggerPreprocessin();
		if (null != testCaseNametoprocess) {
			context.getExternalContext().getSessionMap().put(
					"precalculationmessage",
					"Precalculation of '" + testCaseNametoprocess
							+ "' has been Completed");
		} else {
			context.getExternalContext().getSessionMap().put(
					"precalculationmessage", "");
		}

		return "precalculateTestSuccess";
	}
	
	/*
	 * This function will used to change time for any selected test from config page. 
	 */
	public String changeVquadTimeStamp(){
	
		logger.info("-----changeVquadTimeStamp--------"
				+ testCaseNametoprocess);
		FacesContext context = FacesContext.getCurrentInstance();
		ReportDaoImpl reportDao = new ReportDaoImpl();
		ArrayList<String> Recordset;
		String ColumnName;
		String marketId = context.getExternalContext().getRequestParameterMap()
				.get("marketId");
		String deviceId = context.getExternalContext().getRequestParameterMap()
				.get("deviceId");
		String testName = context.getExternalContext().getRequestParameterMap()
				.get("testName");
		String vqType = context.getExternalContext().getRequestParameterMap()
				.get("vqType");
		int diffHours = new Integer(context.getExternalContext()
				.getRequestParameterMap().get("diffHours"));
		logger.info("diffHours********" + diffHours);
		//String accessTable = "";
		if(!deviceId.equals("0") && !testName.equals(""))
		{
		String sqlTable = "";String finaldate = "";
		if (vqType.equalsIgnoreCase("polqa")) {
			//accessTable = "vqtpolqa";
			sqlTable = "stg_polqa_results";
			ColumnName = "VQuad_Timestamp";
		} else if (vqType.equalsIgnoreCase("pesq")) {
			//accessTable = "vqtdata";
			sqlTable = "stg_vqt_results";
			ColumnName = "VQuadTimestamp";
			
		}else if (vqType.equalsIgnoreCase("NetTest")) {
			///accessTable = "vqtdata";
			sqlTable = "stg_net_results";
			ColumnName = "CallTimeStamp";//VQuadTimeStamp
		} else {
			//accessTable = "vquaddata";
			sqlTable = "stg_callevent_results";
			ColumnName = "Call_TimeStamp";//VQuad_Timestamp
			}
		Recordset = reportDao.getValuesInStgTablesByTestname(testName, marketId, ColumnName, sqlTable);
		if(Recordset.size()>0){
			for(int i=0;i<Recordset.size();i++)
			{
				
				String oldtime = Recordset.get(i);
//				System.out.println("hello this is vquad time stamp "+Recordset.get(i));
				finaldate = Misc.addOrsubHours(Recordset.get(i),
					diffHours, "MM/dd/yyyy HH:mm:ss");
//				System.out.println("finaldate "+finaldate);
				if(reportDao.updateTimeStampofStgTable(testName, ColumnName, sqlTable, finaldate, oldtime)){
					context.getExternalContext().getSessionMap().put(
							"ChangeTimeStampmessage",
							"TimeStamp Updated Successfully of that'" + testName + "'test");
				}
			}
		}else{
			context.getExternalContext().getSessionMap().put(
					"ChangeTimeStampmessage",
					"No Records Found for that '" + testName + "'test");
		}
		}else{
			context.getExternalContext().getSessionMap().put(
					"ChangeTimeStampmessage",
					"Please Select correct details.");
		}
		return "mapGoogleDataSuccess";
	}
	
	/*
	 * This function is for generating the charts using jfree library and that will stroe charts as image on server
	 */
	  /*public String generateReportBarChart() {
		  
		  FacesContext context = FacesContext.getCurrentInstance();
		  String data = context.getExternalContext().getRequestParameterMap().get("ajsdata");
		  String type = context.getExternalContext().getRequestParameterMap().get("type");
		  String title = context.getExternalContext().getRequestParameterMap().get("title");
		  String header = context.getExternalContext().getRequestParameterMap().get("header");
		  String footer = context.getExternalContext().getRequestParameterMap().get("footer");
		  String capture = context.getExternalContext().getRequestParameterMap().get("abc");
		  
		  try {
			 // System.out.println("capture data check kr "+ capture );
			  String urldecode = URLDecoder.decode(data,"UTF-8");
			  urldecode = urldecode.replaceAll("\\\\","");
			  System.out.println("data + "+ type +" " + title +" "+ header +" "+ urldecode);
			  int data_length = urldecode.length();
			  String replaceStart = "\\[\\{\"svg0\"\\:\"";
			  String replaceEnd = "\"\\}\\]";
			  urldecode = urldecode.replaceAll(replaceStart,"");
			  urldecode = urldecode.replaceAll(replaceEnd,"");
			System.out.println("After  data + "+ type +" " + title +" "+ header +" "+data_length + urldecode);
			FileWriter file;
			try {
				String Filepath = ANALYTICS_REPORTS_FOLDER_PATH + "\\test.svg";
				file = new FileWriter(Filepath);
				file.write(urldecode);
				file.flush();
				file.close();
				SvgToJpg(Filepath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 } catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
	  return "mapDataFail";
	  }
	  public void SvgToJpg(String SvgCodeString)
	  {
		// Create a JPEG transcoder
	        JPEGTranscoder t = new JPEGTranscoder();

	        // Set the transcoding hints.
	        t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY,
	                   new Float(.8));

	        // Create the transcoder input.
	        String svgURI = null;
			try {
				svgURI = new File(SvgCodeString).toURL().toString();
			} catch (MalformedURLException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
	        TranscoderInput input = new TranscoderInput(svgURI);

	        // Create the transcoder output.
	        OutputStream ostream = null;
			try {
				ostream = new FileOutputStream(ANALYTICS_REPORTS_FOLDER_PATH + "\\out.jpg");
				TranscoderOutput output = new TranscoderOutput(ostream);
				try {
					t.transcode(input, output);
				} catch (TranscoderException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		        // Save the image.
		       
				// Flush and close the stream.
		        try {
					ostream.flush();
					ostream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }*/
}