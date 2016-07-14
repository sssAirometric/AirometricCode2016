package com.action;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.inject.New;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.validator.ValidatorException;

import org.primefaces.context.RequestContext;

import com.constants.ReportConstants;
import com.dao.MarketPlaceDao;
import com.dao.TestConfigDao;
import com.dao.TestConfigParamDao;
import com.dao.TestConfigUserDao;
import com.dao.UserDao;
import com.dao.impl.MarketPlaceDaoImpl;
import com.dao.impl.OperatorDaoImpl;
import com.dao.impl.TestConfigDaoImpl;
import com.dao.impl.TestConfigParamDaoImpl;
import com.dao.impl.TestConfigUserDaoImpl;
import com.dao.impl.UserDaoImpl;
import com.enums.TestTypeEnum;
import com.helper.Misc;
import com.helper.RolesHelper;
import com.report.helper.JSONObject;
import com.role.TransferTest;
import com.to.DataPingTestCase;
import com.to.FTPTestCase;
import com.to.MOTestCase;
import com.to.MTTestCase;
import com.to.TestConfig;
import com.to.UDPTestCase;
import com.to.UserBean;
import com.to.UserDeviceInfo;
import com.to.VoipTestCase;
import com.to.WebPageTestCase;

/**
 * @author 100787 Mohsin
 *
 */
public class TestCaseAction implements Serializable{

	private static final long serialVersionUID = 1L;
	private MOTestCase moTestCase = new MOTestCase();
	private MTTestCase mtTestCase = new MTTestCase();
	private FTPTestCase ftpTestCase = new FTPTestCase();
	private UDPTestCase udpTestCase = new UDPTestCase();
	private DataPingTestCase dataPingTestCase = new DataPingTestCase();
	private WebPageTestCase webPageTestCase=new WebPageTestCase();
	private VoipTestCase voipTestCase=new VoipTestCase();
	private String testName;
	Map<String,String> testConfigMap = new LinkedHashMap<String,String>();
	Map<String,String> operatorMap = new LinkedHashMap<String,String>();
	Map<String,String> imeiMap = new LinkedHashMap<String,String>();
	Map<String,String> userMap = new LinkedHashMap<String,String>();
	Map<String,String> testNameMap = new LinkedHashMap<String,String>();
	private String selectedTestConfigId;
	private String selectedOperatorID;
	private String selUserID="0";
	private String[] selImei;
	private  List<TestConfig> assignListTestConfig;
	private  List<TestConfig> testConfigList;
	private String testCaseName;
	private String testNameDupMsg;
	private String action;
	private String testConfigFile;
	private String[] transfertestname;
    private static final String urlPattern="[a-zA-Z0-9_/\\-\\.]+\\.([A-Za-z/]{2,5})[a-zA-Z0-9_/\\&\\?\\=\\-\\.\\~\\%]*";
    private static final String IPADDRESS_PATTERN = 
		"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    private final String USER_AGENT = "Mozilla/5.0";
    Map<String, String> selectedUserTostartAutotest = new LinkedHashMap<String, String>();	
    ArrayList<String> FailedStartedAutotest = new ArrayList<String>();
    
    Map<String, String> marketPlaceMap = new LinkedHashMap<String, String>();
    
	Map<String,String> frequencyNameMap = new LinkedHashMap<String,String>();
	
	Map<String,String> SelectedCheckbox = new LinkedHashMap<String,String>();
	
	public Map<String, String> getFrequencyNameMap() {
		return frequencyNameMap;
	}

	public void setFrequencyNameMap(Map<String, String> frequencyNameMap) {
		this.frequencyNameMap = frequencyNameMap;
	}
	
	private String marketId;
    
    
	public String[] getTransfertestname() {
		return transfertestname;
	}

	public void setTransfertestname(String[] transfertestname) {
		this.transfertestname = transfertestname;
	}

	public String getTestConfigFile() {
		return testConfigFile;
	}

	public void setTestConfigFile(String testConfigFile) {
		this.testConfigFile = testConfigFile;
	}

	public WebPageTestCase getWebPageTestCase() {
		return webPageTestCase;
	}

	public void setWebPageTestCase(WebPageTestCase webPageTestCase) {
		this.webPageTestCase = webPageTestCase;
	}

	public VoipTestCase getVoipTestCase() {
		return voipTestCase;
	}

	public void setVoipTestCase(VoipTestCase voipTestCase) {
		this.voipTestCase = voipTestCase;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getTestCaseName() {
		return testCaseName;
	}

	public void setTestCaseName(String testCaseName) {
		this.testCaseName = testCaseName;
	}

	public List<TestConfig> getAssignListTestConfig() {
		return assignListTestConfig;
	}

	public void setAssignListTestConfig(List<TestConfig> assignListTestConfig) {
		this.assignListTestConfig = assignListTestConfig;
	}

	public String getSelectedTestConfigId() {
		return selectedTestConfigId;
	}

	public void setSelectedTestConfigId(String selectedTestConfigId) {
		this.selectedTestConfigId = selectedTestConfigId;
	}

	public String getSelectedOperatorID() {
		return selectedOperatorID;
	}

	public UDPTestCase getUdpTestCase() {
		return udpTestCase;
	}

	public void setUdpTestCase(UDPTestCase udpTestCase) {
		this.udpTestCase = udpTestCase;
	}

	public void setSelectedOperatorID(String selectedOperatorID) {
		this.selectedOperatorID = selectedOperatorID;
	}

	public String getSelUserID() {
		return selUserID;
	}

	public void setSelUserID(String selUserID) {
		this.selUserID = selUserID;
	}

	public Map<String, String> getTestConfigMap() {
		return testConfigMap;
	}
	public void setTestConfigMap(Map<String, String> testConfigMap) {
		this.testConfigMap = testConfigMap;
	}
	
	public Map<String, String> getOperatorMap() {
		return operatorMap;
	}

	public void setOperatorMap(Map<String, String> operatorMap) {
		this.operatorMap = operatorMap;
	}

	public Map<String, String> getUserMap() {
		return userMap;
	}

	public void setUserMap(Map<String, String> userMap) {
		this.userMap = userMap;
	}

	public Map<String, String> getTestNameMap() {
		return testNameMap;
	}

	public void setTestNameMap(Map<String, String> testNameMap) {
		this.testNameMap = testNameMap;
	}

	public MOTestCase getMoTestCase() {
		return moTestCase;
	}
	public void setMoTestCase(MOTestCase moTestCase) {
		this.moTestCase = moTestCase;
	}
	public MTTestCase getMtTestCase() {
		return mtTestCase;
	}
	public void setMtTestCase(MTTestCase mtTestCase) {
		this.mtTestCase = mtTestCase;
	}
	public FTPTestCase getFtpTestCase() {
		return ftpTestCase;
	}
	public void setFtpTestCase(FTPTestCase ftpTestCase) {
		this.ftpTestCase = ftpTestCase;
	}
	
	public String getTestName() {
		return testName;
	}
	public void setTestName(String testName) {
		this.testName = testName;
	}
	
	public Map<String, String> getImeiMap() {
		return imeiMap;
	}

	public void setImeiMap(Map<String, String> imeiMap) {
		this.imeiMap = imeiMap;
	}

	public String[] getSelImei() {
		return selImei;
	}

	public void setSelImei(String[] selImei) {
		this.selImei = selImei;
	}

    
	public DataPingTestCase getDataPingTestCase() {
		return dataPingTestCase;
	}

	public void setDataPingTestCase(DataPingTestCase dataPingTestCase) {
		this.dataPingTestCase = dataPingTestCase;
	}

	public String getTestNameDupMsg() {
		return testNameDupMsg;
	}

	public void setTestNameDupMsg(String testNameDupMsg) {
		this.testNameDupMsg = testNameDupMsg;
	}
    
	

	public String getMarketId() {
		return marketId;
	}

	public void setMarketId(String marketId) {
		this.marketId = marketId;
	}

	/*private  TestConfig assignListTestConfig1=null;
	public void attrListener1(ActionEvent event){
		assignListTestConfig1 = (TestConfig)event.getComponent().getAttributes().get("selectedcheckbox");
	}
	*/
	/**
	  Navigation from Edit Configuration to Assign Configuration Page.
	 */
	
	public String editAssignCreateConfig(){
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		String roleName = context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		String userId =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
		TestConfigUserDao testConfigUserDao = new TestConfigUserDaoImpl();
		if((roleName.equals("superadmin")||roleName.equals("superadmin"))){
			testConfigMap = new TestConfigDaoImpl().getActiveTestConfigInMap();
			operatorMap = new OperatorDaoImpl().getActiveOperatorsInMap();
			assignListTestConfig=testConfigUserDao.assignListTestConfig();
			
			context.getExternalContext().getSessionMap().remove("testConfigMapSize");
			context.getExternalContext().getSessionMap().remove("assignListTestSize");
			context.getExternalContext().getSessionMap().remove("assignListTestConfig");
			context.getExternalContext().getSessionMap().remove("testConfigMessage");
			context.getExternalContext().getSessionMap().remove("operatorMaplist");
			context.getExternalContext().getSessionMap().put("operatorMaplist", operatorMap);
			context.getExternalContext().getSessionMap().remove("testConfigMapList");
			context.getExternalContext().getSessionMap().put("testConfigMapList", testConfigMap);
			context.getExternalContext().getRequestMap().put("testConfigMessage", "No Test-Configs are assigned for the Logged user");
			context.getExternalContext().getSessionMap().put("testConfigMapSize", testConfigMap.size());
			context.getExternalContext().getSessionMap().put("assignListTestSize", assignListTestConfig.size());
			context.getExternalContext().getSessionMap().put("assignListTestConfig", assignListTestConfig);
			context.getExternalContext().getSessionMap().put("assignType","view");
		}else{
			String operator= context.getExternalContext().getSessionMap().get("loggedInOperatorID").toString();
			testConfigMap = new TestConfigDaoImpl().getActiveTestConfigInMapForManager(userId);
			UserDao userDao = new UserDaoImpl();
			userMap = userDao.getActiveUserBelongsToManager(userDao.getOperator(userId),userId);
			assignListTestConfig=testConfigUserDao.assignListTestConfigOperator(operator,userId);
			context.getExternalContext().getSessionMap().remove("operatorMaplist");
			context.getExternalContext().getSessionMap().put("operatorMaplist", operatorMap);
			context.getExternalContext().getSessionMap().remove("testConfigMapList");
			context.getExternalContext().getSessionMap().put("testConfigMapList", testConfigMap);
			context.getExternalContext().getSessionMap().remove("userMaplist");
			context.getExternalContext().getSessionMap().put("userMaplist", userMap);
			context.getExternalContext().getSessionMap().remove("testConfigMapSize");
			context.getExternalContext().getSessionMap().remove("assignListTestSize");
			context.getExternalContext().getSessionMap().remove("assignListTestConfig");
			context.getExternalContext().getSessionMap().remove("testConfigMessage");
			context.getExternalContext().getRequestMap().put("testConfigMessage", "No Test-Configs are assigned for the Logged user");
			context.getExternalContext().getSessionMap().put("testConfigMapSize", testConfigMap.size());
			context.getExternalContext().getSessionMap().put("assignListTestSize", assignListTestConfig.size());
			context.getExternalContext().getSessionMap().put("assignListTestConfig", assignListTestConfig);
			context.getExternalContext().getSessionMap().put("assignType","view");
		}
		return "editAssignCreateConfigPage";
	}
	/**
	  Navigation from same Edit Configuration page.
	 */
	public String EditConfiguration(){
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		String roleName = context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		String userId =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserID", userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", roleName);
		context.getExternalContext().getSessionMap().remove("userStatus");
		if((roleName.equals("superadmin")||roleName.equals("superadmin"))){
			testConfigMap = new TestConfigDaoImpl().getActiveTestConfigInMap();
		}else{
			testConfigMap = new TestConfigDaoImpl().getActiveTestConfigInMapForManager(userId);
		}
		context.getExternalContext().getSessionMap().remove("testConfigMapList");
		context.getExternalContext().getSessionMap().put("testConfigMapList", testConfigMap);
		return "EditConfigurationPage";
	}
	/**
	  Navigation from Assign Configuration to Edit Configuration Page.
	 */
	public String editCreateAssignConfig(){
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		String roleName = context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		String userId =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserID", userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", roleName);
		context.getExternalContext().getSessionMap().remove("userStatus");
		if((roleName.equals("superadmin")||roleName.equals("superadmin"))){
			testConfigMap = new TestConfigDaoImpl().getActiveTestConfigInMap();
		}else{
			testConfigMap = new TestConfigDaoImpl().getActiveTestConfigInMapForManager(userId);
		}
		context.getExternalContext().getSessionMap().remove("testConfigMapList");
		context.getExternalContext().getSessionMap().put("testConfigMapList", testConfigMap);
		return "editCreateAssignConfigPage";
	}
	
	/**
	  Navigation from Create Configuration to Edit Configuration Page.changed by Ankit
	 */
	public String testEditAssignConfig(){
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		
		String roleName = context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		String userId =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserID", userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", roleName);
		context.getExternalContext().getSessionMap().remove("userStatus");
		if((roleName.equals("superadmin")||roleName.equals("superadmin"))){
			operatorMap = new OperatorDaoImpl().getActiveOperatorsInMap();
			//testConfigMap = new TestConfigDaoImpl().getActiveTestConfigInMap();
			context.getExternalContext().getSessionMap().remove("operatorMaplist");
			context.getExternalContext().getSessionMap().put("operatorMaplist", operatorMap);
		}else{
			testConfigMap = new TestConfigDaoImpl().getActiveTestConfigInMapForManager(userId);
			context.getExternalContext().getSessionMap().remove("testConfigMapList");
			context.getExternalContext().getSessionMap().put("testConfigMapList", testConfigMap);
		}
		//context.getExternalContext().getSessionMap().remove("testConfigMapList");
		//context.getExternalContext().getSessionMap().put("testConfigMapList", testConfigMap);
		Map<String,String> marketMap = null;
		if((roleName.equals("superadmin")||roleName.equals("superadmin"))){
			marketMap = new MarketPlaceDaoImpl().getMarketPlaceMap();
		}else{
			 marketMap = new MarketPlaceDaoImpl().getMarketForUser(userId);
		}
		context.getExternalContext().getSessionMap().put("marketMap", marketMap);
		return "testEditAssignConfigPage";
	}
	
	/**
	  Navigation from Upload Configuration to Assign Configuration Page.
	 */
	public String uploadAssignConfig(){
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		String roleName = context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		String userId =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
		TestConfigUserDao testConfigUserDao = new TestConfigUserDaoImpl();
		if((roleName.equals("superadmin")||roleName.equals("superadmin"))){
			testConfigMap = new TestConfigDaoImpl().getActiveTestConfigInMap();
			operatorMap = new OperatorDaoImpl().getActiveOperatorsInMap();
			assignListTestConfig=testConfigUserDao.assignListTestConfig();
			context.getExternalContext().getSessionMap().remove("testConfigMapSize");
			context.getExternalContext().getSessionMap().remove("assignListTestSize");
			context.getExternalContext().getSessionMap().remove("assignListTestConfig");
			context.getExternalContext().getSessionMap().remove("testConfigMessage");
			context.getExternalContext().getSessionMap().remove("operatorMaplist");
			context.getExternalContext().getSessionMap().put("operatorMaplist", operatorMap);
			context.getExternalContext().getSessionMap().remove("testConfigMapList");
			context.getExternalContext().getSessionMap().put("testConfigMapList", testConfigMap);
			context.getExternalContext().getRequestMap().put("testConfigMessage", "No Test-Configs are assigned for the Logged user");
			context.getExternalContext().getSessionMap().put("testConfigMapSize", testConfigMap.size());
			context.getExternalContext().getSessionMap().put("assignListTestSize", assignListTestConfig.size());
			context.getExternalContext().getSessionMap().put("assignListTestConfig", assignListTestConfig);
		}else{
			String operator= context.getExternalContext().getSessionMap().get("loggedInOperatorID").toString();
			testConfigMap = new TestConfigDaoImpl().getActiveTestConfigInMapForManager(userId);
			UserDao userDao = new UserDaoImpl();
			userMap = userDao.getActiveUserBelongsToManager(userDao.getOperator(userId),userId);
			assignListTestConfig=testConfigUserDao.assignListTestConfigOperator(operator,userId);
			context.getExternalContext().getSessionMap().remove("operatorMaplist");
			context.getExternalContext().getSessionMap().put("operatorMaplist", operatorMap);
			context.getExternalContext().getSessionMap().remove("testConfigMapList");
			context.getExternalContext().getSessionMap().put("testConfigMapList", testConfigMap);
			context.getExternalContext().getSessionMap().remove("userMaplist");
			context.getExternalContext().getSessionMap().put("userMaplist", userMap);
			context.getExternalContext().getSessionMap().remove("testConfigMapSize");
			context.getExternalContext().getSessionMap().remove("assignListTestSize");
			context.getExternalContext().getSessionMap().remove("assignListTestConfig");
			context.getExternalContext().getSessionMap().remove("testConfigMessage");
			context.getExternalContext().getRequestMap().put("testConfigMessage", "No Test-Configs are assigned for the Logged user");
			context.getExternalContext().getSessionMap().put("testConfigMapSize", testConfigMap.size());
			context.getExternalContext().getSessionMap().put("assignListTestSize", assignListTestConfig.size());
			context.getExternalContext().getSessionMap().put("assignListTestConfig", assignListTestConfig);
		}
		return "uploadAssignConfigPage";
	}
	
	/**
	  Navigation from Upload Configuration to Edit Configuration Page.
	 */
	public String uploadEditAssignConfig(){
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		String roleName = context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		String userId =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
		TestConfigUserDao testConfigUserDao = new TestConfigUserDaoImpl();
		if((roleName.equals("superadmin")||roleName.equals("superadmin"))){
			testConfigMap = new TestConfigDaoImpl().getActiveTestConfigInMap();
			operatorMap = new OperatorDaoImpl().getActiveOperatorsInMap();
			assignListTestConfig=testConfigUserDao.assignListTestConfig();
			context.getExternalContext().getSessionMap().remove("testConfigMapSize");
			context.getExternalContext().getSessionMap().remove("assignListTestSize");
			context.getExternalContext().getSessionMap().remove("assignListTestConfig");
			context.getExternalContext().getSessionMap().remove("testConfigMessage");
			context.getExternalContext().getSessionMap().remove("operatorMaplist");
			context.getExternalContext().getSessionMap().put("operatorMaplist", operatorMap);
			context.getExternalContext().getSessionMap().remove("testConfigMapList");
			context.getExternalContext().getSessionMap().put("testConfigMapList", testConfigMap);
			context.getExternalContext().getRequestMap().put("testConfigMessage", "No Test-Configs are assigned for the Logged user");
			context.getExternalContext().getSessionMap().put("testConfigMapSize", testConfigMap.size());
			context.getExternalContext().getSessionMap().put("assignListTestSize", assignListTestConfig.size());
			context.getExternalContext().getSessionMap().put("assignListTestConfig", assignListTestConfig);
		}else{
			String operator= context.getExternalContext().getSessionMap().get("loggedInOperatorID").toString();
			testConfigMap = new TestConfigDaoImpl().getActiveTestConfigInMapForManager(userId);
			UserDao userDao = new UserDaoImpl();
			userMap = userDao.getActiveUserBelongsToManager(userDao.getOperator(userId),userId);
			assignListTestConfig=testConfigUserDao.assignListTestConfigOperator(operator,userId);
			context.getExternalContext().getSessionMap().remove("operatorMaplist");
			context.getExternalContext().getSessionMap().put("operatorMaplist", operatorMap);
			context.getExternalContext().getSessionMap().remove("testConfigMapList");
			context.getExternalContext().getSessionMap().put("testConfigMapList", testConfigMap);
			context.getExternalContext().getSessionMap().remove("userMaplist");
			context.getExternalContext().getSessionMap().put("userMaplist", userMap);
			context.getExternalContext().getSessionMap().remove("testConfigMapSize");
			context.getExternalContext().getSessionMap().remove("assignListTestSize");
			context.getExternalContext().getSessionMap().remove("assignListTestConfig");
			context.getExternalContext().getSessionMap().remove("testConfigMessage");
			context.getExternalContext().getRequestMap().put("testConfigMessage", "No Test-Configs are assigned for the Logged user");
			context.getExternalContext().getSessionMap().put("testConfigMapSize", testConfigMap.size());
			context.getExternalContext().getSessionMap().put("assignListTestSize", assignListTestConfig.size());
			context.getExternalContext().getSessionMap().put("assignListTestConfig", assignListTestConfig);
		}
		return "uploadEditAssignConfigPage";
	}
	/**
	  Navigation from Assign Configuration to Upload Configuration Page.
	 */
	public String testAssignUpload(){
		return "testAssignUploadPage";
	}
	/**
	  Navigation from Edit Configuration to Upload Configuration Page.
	 */
	public String testEditAssignUpload(){
		return "testEditAssignUploadPage";
	}
	/**
	  Navigation from Assign Configuration to Create Configuration Page.
	 */
	public String assignConfigPage(){
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		String roleName = context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		String userId =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserID", userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", roleName);
		context.getExternalContext().getSessionMap().remove("userStatus");
		return "assignConfigurationPage";
	}
	/**
	  Navigation from  same Create Configuration Page.
	 */
	public String createConfigPage(){
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		String roleName = context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		String userId =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
		Map<String,String> marketMap = null;
		if((roleName.equals("superadmin")||roleName.equals("superadmin"))){
			marketMap = new MarketPlaceDaoImpl().getMarketPlaceMap();
		}else{
			 marketMap = new MarketPlaceDaoImpl().getMarketForUser(userId);
		}
		context.getExternalContext().getSessionMap().put("marketMap", marketMap);
		context.getExternalContext().getSessionMap().put("loggedInUserID", userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", roleName);
		context.getExternalContext().getSessionMap().remove("userStatus");
		return "createConfigPageview";
	}
	
	
	/**
	  Navigation from Edit Configuration to Create Configuration Page.
	 */
	public String assignEdittoConfig(){
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		String roleName = context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		String userId =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserID", userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", roleName);
		context.getExternalContext().getSessionMap().remove("userStatus");
		return "assignEdittoConfigPage";
	}
	/**
	  Navigation from Upload Configuration to Create Configuration Page.
	 */
	public String uploadConfiguration(){
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		String roleName = context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		String userId =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserID", userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", roleName);
		return "uploadConfigurationPage";
	}
	/**
	  Navigation from Create Configuration to Assign Configuration Page.
	 */
	public String testAssignConfigPage(){
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		String roleName = context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		String userId =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
		
		TestConfigUserDao testConfigUserDao = new TestConfigUserDaoImpl();
		if((roleName.equals("superadmin"))){
			//testConfigMap = new TestConfigDaoImpl().getActiveTestConfigInMapForManager(userId);
			//changes made by ankit
			testConfigMap = new TestConfigDaoImpl().getActiveTestConfigInMap();
			operatorMap = new OperatorDaoImpl().getActiveOperatorsInMap();
			assignListTestConfig=testConfigUserDao.assignListTestConfig();
			context.getExternalContext().getSessionMap().remove("testConfigMapSize");
			context.getExternalContext().getSessionMap().remove("assignListTestSize");
			context.getExternalContext().getSessionMap().remove("assignListTestConfig");
			context.getExternalContext().getSessionMap().remove("testConfigMessage");
			context.getExternalContext().getSessionMap().remove("operatorMaplist");
			context.getExternalContext().getSessionMap().put("operatorMaplist", operatorMap);
			context.getExternalContext().getSessionMap().remove("testConfigMapList");
			context.getExternalContext().getSessionMap().put("testConfigMapList", testConfigMap);
			context.getExternalContext().getRequestMap().put("testConfigMessage", "No Test-Configs are assigned for the Logged user");
			context.getExternalContext().getSessionMap().put("testConfigMapSize", testConfigMap.size());
			context.getExternalContext().getSessionMap().put("assignListTestSize", assignListTestConfig.size());
			context.getExternalContext().getSessionMap().put("assignListTestConfig", assignListTestConfig);
			}else{
			String operator= context.getExternalContext().getSessionMap().get("loggedInOperatorID").toString();
			Set<String> userNameList = new UserDaoImpl().getUsersOfSuperior(userId,roleName);
			testConfigMap = new TestConfigDaoImpl().getActiveTestConfigInMapForManager(userId);
//			testNameMap = testConfigDao.getTestNamesInMapInStgDevice(userId);
			UserDao userDao = new UserDaoImpl();
//			userMap = userDao.getActiveUserBelongsToManager(userDao.getOperator(userId),userId);
			userMap = userDao.getActiveUserBelongsToManager("",userNameList.toString().substring(1, userNameList.toString().length()-1));
//			userMap = userDao.listUser(userNameList.toString().substring(1, userNameList.toString().length()-1),roleName);
			assignListTestConfig=testConfigUserDao.assignListTestConfigOperator(operator,userNameList.toString().substring(1, userNameList.toString().length()-1));
			context.getExternalContext().getSessionMap().remove("operatorMaplist");
			context.getExternalContext().getSessionMap().put("operatorMaplist", operatorMap);
			context.getExternalContext().getSessionMap().remove("testConfigMapList");
			context.getExternalContext().getSessionMap().put("testConfigMapList", testConfigMap);
			context.getExternalContext().getSessionMap().remove("userMaplist");
			context.getExternalContext().getSessionMap().put("userMaplist", userMap);
			context.getExternalContext().getSessionMap().remove("testConfigMapSize");
			context.getExternalContext().getSessionMap().remove("assignListTestSize");
			context.getExternalContext().getSessionMap().remove("assignListTestConfig");
			context.getExternalContext().getSessionMap().remove("testConfigMessage");
			context.getExternalContext().getRequestMap().put("testConfigMessage", "No Test-Configs are assigned for the Logged user");
			context.getExternalContext().getSessionMap().put("testConfigMapSize", testConfigMap.size());
			context.getExternalContext().getSessionMap().put("assignListTestSize", assignListTestConfig.size());
			context.getExternalContext().getSessionMap().put("assignListTestConfig", assignListTestConfig);
			}
		return "testassignConfigurationPage";
	}
	
	/**
	  Navigation from  same  Assign Configuration Page.
	 */
	public String createAssignConfigPage(){
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		String roleName = context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		String userId =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
		TestConfigUserDao testConfigUserDao = new TestConfigUserDaoImpl();
		if((roleName.equals("superadmin")||roleName.equals("superadmin"))){
			testConfigMap = new TestConfigDaoImpl().getActiveTestConfigInMap();
			operatorMap = new OperatorDaoImpl().getActiveOperatorsInMap();
			assignListTestConfig=testConfigUserDao.assignListTestConfig();
			context.getExternalContext().getSessionMap().remove("testConfigMapSize");
			context.getExternalContext().getSessionMap().remove("assignListTestSize");
			context.getExternalContext().getSessionMap().remove("assignListTestConfig");
			context.getExternalContext().getSessionMap().remove("testConfigMessage");
			context.getExternalContext().getSessionMap().remove("operatorMaplist");
			context.getExternalContext().getSessionMap().put("operatorMaplist", operatorMap);
			context.getExternalContext().getSessionMap().remove("testConfigMapList");
			context.getExternalContext().getSessionMap().put("testConfigMapList", testConfigMap);
			context.getExternalContext().getRequestMap().put("testConfigMessage", "No Test-Configs are assigned for the Logged user");
			context.getExternalContext().getSessionMap().put("testConfigMapSize", testConfigMap.size());
			context.getExternalContext().getSessionMap().put("assignListTestSize", assignListTestConfig.size());
			context.getExternalContext().getSessionMap().put("assignListTestConfig", assignListTestConfig);
		}else{
			String operator= context.getExternalContext().getSessionMap().get("loggedInOperatorID").toString();
			testConfigMap = new TestConfigDaoImpl().getActiveTestConfigInMapForManager(userId);
			UserDao userDao = new UserDaoImpl();
			userMap = userDao.getActiveUserBelongsToManager(userDao.getOperator(userId),userId);
			assignListTestConfig=testConfigUserDao.assignListTestConfigOperator(operator,userId);
			context.getExternalContext().getSessionMap().remove("operatorMaplist");
			context.getExternalContext().getSessionMap().put("operatorMaplist", operatorMap);
			context.getExternalContext().getSessionMap().remove("testConfigMapList");
			context.getExternalContext().getSessionMap().put("testConfigMapList", testConfigMap);
			context.getExternalContext().getSessionMap().remove("userMaplist");
			context.getExternalContext().getSessionMap().put("userMaplist", userMap);
			context.getExternalContext().getSessionMap().remove("testConfigMapSize");
			context.getExternalContext().getSessionMap().remove("assignListTestSize");
			context.getExternalContext().getSessionMap().remove("assignListTestConfig");
			context.getExternalContext().getSessionMap().remove("testConfigMessage");
			context.getExternalContext().getRequestMap().put("testConfigMessage", "No Test-Configs are assigned for the Logged user");
			context.getExternalContext().getSessionMap().put("testConfigMapSize", testConfigMap.size());
			context.getExternalContext().getSessionMap().put("assignListTestSize", assignListTestConfig.size());
			context.getExternalContext().getSessionMap().put("assignListTestConfig", assignListTestConfig);
		}
		return "createAssignConfigPageview";
	}
	
	
	/**
	  Navigation from Upload Configuration to Edit Configuration Page.
	 */
	public String editAssignConfig(){
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		String roleName = context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		String userId =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserID", userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", roleName);
		context.getExternalContext().getSessionMap().remove("userStatus");
		if((roleName.equals("superadmin")||roleName.equals("superadmin"))){
			testConfigMap = new TestConfigDaoImpl().getActiveTestConfigInMap();
		}else{
			testConfigMap = new TestConfigDaoImpl().getActiveTestConfigInMapForManager(userId);
		}
		context.getExternalContext().getSessionMap().remove("testConfigMapList");
		context.getExternalContext().getSessionMap().put("testConfigMapList", testConfigMap);
		return "editAssignConfigPage";
	}
	/**
	  Navigation from Create Configuration to Upload Configuration Page.
	 */
	public String testUpload(){
		//System.out.println("testUpload:");
		FacesContext context = FacesContext.getCurrentInstance();
		MarketPlaceDao marketPlaceDao = new MarketPlaceDaoImpl();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		String userIdLogged =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
		String userName = context.getExternalContext().getSessionMap().get("userName").toString();
		marketPlaceMap=marketPlaceDao.getUploadMarketPlaceMap(userrole,userIdLogged);
		if(userrole.equals("superadmin")){
			testNameMap = new TestConfigDaoImpl().getTestNamesInMapInStgDevice();
		}else{
			Set<String> userNameList = new UserDaoImpl().getUsersOfSuperior(userIdLogged,userrole);
			testNameMap  = new TestConfigDaoImpl().getnTestNamesInMapInHierarchy(userNameList.toString().substring(1, userNameList.toString().length()-1));
		}
		context.getExternalContext().getSessionMap().remove("testNameMap");
		context.getExternalContext().getSessionMap().remove("margetPlaceListSize");
		context.getExternalContext().getSessionMap().remove("margetPlaceListdetails");
		context.getExternalContext().getSessionMap().put("testNameMap",testNameMap);
		context.getExternalContext().getSessionMap().put("differnceHours", Misc.getDiffHours());
		context.getExternalContext().getSessionMap().put("margetPlaceListSize", marketPlaceMap.size());
		context.getExternalContext().getSessionMap().put("margetPlaceListdetails", marketPlaceMap);
		return "testUploadPage";
	}
	/**
	    TestCases Validation for the Create Configuration 
	 */
	public String validateTestCases(String status,FacesContext context,String testName,MOTestCase moTestCase,MTTestCase mtTestCase,FTPTestCase ftpTestCase,UDPTestCase udpTestCase, DataPingTestCase dataPingTestCase,WebPageTestCase webPageTestCase,VoipTestCase voipTestCase,String isExternal){
		 Pattern pattern;
		 Matcher matcher;
		 final String TESTNAME_PATTERN = "[A-Za-z0-9]+";
		 pattern = Pattern.compile(TESTNAME_PATTERN);
		 if(!status.equals("Update")){
				if(testName.equals("")){
					context.getExternalContext().getRequestMap().put("failuremessage", " Enter Config Name");
					return "testCreatedFailed";
				}else{
					matcher = pattern.matcher(testName);
					boolean valid = matcher.matches();
					if(!valid){
						context.getExternalContext().getRequestMap().put("failuremessage", "Config name should be alphanumeric only.");
						return "testCreatedFailed";
					}else{
						TestConfigDao testConfigDao = new TestConfigDaoImpl();
						if(testConfigDao.getTestNamesInMap().containsKey(testName)){
							context.getExternalContext().getRequestMap().put("failuremessage", "Config with name: '"+testName+"' already exists");
							return "testCreatedFailed";
						}
					}
				}
		 }
	
		if((moTestCase.getPhoneNumber().equals("") && moTestCase.getCallDuration().equals("") && moTestCase.getPauseTime().equals("") && moTestCase.getTestDuration().equals("")) && (ftpTestCase.getFtpServerURL().equals("") && ftpTestCase.getFtpUsername().equals("") && ftpTestCase.getFtpPassword().equals("") && ftpTestCase.getNoOfRepeatCycles().equals("")&& ftpTestCase.getUploadFilePath().equals("")&& ftpTestCase.getFileDownloadPath().equals(""))
				&&(mtTestCase.getTestDurationMt().equals(""))&& (udpTestCase.getUdpServerURL().equals("") &&udpTestCase.getUdpServerPort().equals("")&&udpTestCase.getNoOfRepeatCyclesUDP().equals("") && udpTestCase.getFilePathToUpload().equals(""))&&(dataPingTestCase.getServerURL().equals("") && dataPingTestCase.getNoOfRepeatCyclesPing().equals(""))&&(webPageTestCase.getWebPageURL().equals("") && webPageTestCase.getNumberofrepeatcyclesInWeb().equals(""))&&(voipTestCase.getTestDurationVoip().equals(""))&&null==isExternal){
			context.getExternalContext().getRequestMap().put("failuremessage", "Please configure atleast one test");
			return "testCreatedFailed";
		}
		
		//MO TestCase
		Pattern pattern1 = Pattern.compile("^[+]?+\\d{0,20}");
	    Matcher matcher1 = pattern1.matcher(moTestCase.getPhoneNumber());
		if(!moTestCase.getPhoneNumber().equals("") || !moTestCase.getCallDuration().equals("") || !moTestCase.getPauseTime().equals("") || !moTestCase.getTestDuration().equals("")){
			if(moTestCase.getPhoneNumber().equals("")){
				 context.getExternalContext().getRequestMap().put("failuremessage", "MO Test: Please enter phone number.");
				 return "testUpdateCreatedFailed";
			  }else if(moTestCase.getPhoneNumber().length()>= 20){
					context.getExternalContext().getRequestMap().put("failuremessage", "MO Test: Please enter  phone numbers less than 20 digits");
					return "testUpdateCreatedFailed";
			  }else if(matcher1.matches()){
				  //System.out.println("Phone number is valid");
			  }else{
				  context.getExternalContext().getRequestMap().put("failuremessage", "MO Test: Phone Number must be in the format  +XXXXXXXXXX  OR XXXXXXXXXX");
				  return "testUpdateCreatedFailed";
			  }
			if(moTestCase.getCallDuration().equals("")){
				context.getExternalContext().getRequestMap().put("failuremessage", "MO Test: Please enter Call Duration.");
				return "testCreatedFailed";
			}else if(isInteger(moTestCase.getCallDuration())){
			  	if(Integer.valueOf(moTestCase.getCallDuration()) > 90){
					context.getExternalContext().getRequestMap().put("failuremessage", "MO Test: Maximum Call Duration limit is 90 mins only."); 
					return "testCreatedFailed";
				}
			  }else{
			  		context.getExternalContext().getRequestMap().put("failuremessage", "MO Test: Call Duration should be numbers only."); 
			  		return "testCreatedFailed";
			  }
			if(moTestCase.getPauseTime().equals("")){
				context.getExternalContext().getRequestMap().put("failuremessage", "MO Test: Please enter Pause Time.");
				return "testCreatedFailed";
			}else if(moTestCase.getPauseTime().equals("0")) {
		  		context.getExternalContext().getRequestMap().put("failuremessage", "MO Test: Please enter valid Pause Time."); 
		  		return "testCreatedFailed";
		  	}else if(isInteger(moTestCase.getPauseTime())){
				int pauseTime = Integer.valueOf(moTestCase.getPauseTime());
				double pausetime = Integer.valueOf(pauseTime)/60;
				if(pausetime==0.0){
					pausetime = 1.0;
				}
				if((Integer.valueOf(moTestCase.getCallDuration())) + (Math.floor(pausetime)) > 90){
					context.getExternalContext().getRequestMap().put("failuremessage", "MO Test: Total of CallDuration and PauseTime should not be exceeding 90mins."); 
			  		return "testCreatedFailed";
				}
			  }else{
			  		context.getExternalContext().getRequestMap().put("failuremessage", "MO Test: Please enter Pause Time in numeric only."); 
			  		return "testCreatedFailed";
			  	}
			if(moTestCase.getTestDuration().equals("")){
				context.getExternalContext().getRequestMap().put("failuremessage", "MO Test: Please enter Test Duration.");
				return "testCreatedFailed";
			}else if(isInteger(moTestCase.getTestDuration())){
					double pausetime = Integer.valueOf(moTestCase.getPauseTime())/60;
					if(pausetime==0.0){
						pausetime = 1.0;
					}
			    if((Integer.valueOf(moTestCase.getCallDuration()) + Math.floor(pausetime)) > Integer.valueOf(moTestCase.getTestDuration())){
					context.getExternalContext().getRequestMap().put("failuremessage", "MO Test: Test duration should not be lesser than the sum of Call Duration and Pause Time.");
					return "testCreatedFailed";
				}else if(Integer.valueOf(moTestCase.getTestDuration())>90){
					context.getExternalContext().getRequestMap().put("failuremessage", "MO Test: Test Duration max limit is 90 mins.");
					return "testCreatedFailed";
				}
			  }else{
			  		context.getExternalContext().getRequestMap().put("failuremessage", "MO Test: Test Duration should be numeric only.");
					return "testCreatedFailed";
			  }
		}
			//MT TestCase
			
		if(!mtTestCase.getTestDurationMt().equals("") || !mtTestCase.getCallDurationMt().equals("") ){
			if(mtTestCase.getTestDurationMt().equals("")){
				context.getExternalContext().getRequestMap().put("failuremessage", "MT Test: Please enter the Test Duration.");
				return "testCreatedFailed";
			}else if(isInteger(mtTestCase.getTestDurationMt())){
				 if(Integer.valueOf(mtTestCase.getTestDurationMt())>90){
					context.getExternalContext().getRequestMap().put("failuremessage", "MT Test: Test duration should not exceed 90mins.");
					return "testCreatedFailed";
				}else if(Integer.valueOf(mtTestCase.getTestDurationMt()) == 0) {
			  		context.getExternalContext().getRequestMap().put("failuremessage", "MT Test: Please enter valid test duration.");
					return "testCreatedFailed";
			  	}
			  }else{
			  		context.getExternalContext().getRequestMap().put("failuremessage", "MT Test: Please enter test duration  in numeric only.");
					return "testCreatedFailed";
			  }
			
			if(mtTestCase.getCallDurationMt().equals("")){
				context.getExternalContext().getRequestMap().put("failuremessage", "MT Test: Please enter Call Duration.");
				return "testCreatedFailed";
			}else if(isInteger(mtTestCase.getCallDurationMt())){
			  	if(Integer.valueOf(mtTestCase.getCallDurationMt()) > 90){
					context.getExternalContext().getRequestMap().put("failuremessage", "MT Test: Maximum Call Duration limit is 90 mins only."); 
					return "testCreatedFailed";
				}else if(Integer.valueOf(mtTestCase.getCallDurationMt())>=Integer.valueOf(mtTestCase.getTestDurationMt())){
					context.getExternalContext().getRequestMap().put("failuremessage", "MT Test: Test Duration should not be lesser than or equal to Call duration "); 
					return "testCreatedFailed";
				}	
			  }else{
			  		context.getExternalContext().getRequestMap().put("failuremessage", "MT Test: Call Duration should be numbers only."); 
			  		return "testCreatedFailed";
			  }
		  }
		
		
			//FTP TestCase
		if(!ftpTestCase.getFtpServerURL().equals("") || !ftpTestCase.getNoOfRepeatCycles().equals("")|| !ftpTestCase.getFtpUsername().equals("")|| !ftpTestCase.getFtpPassword().equals("")|| !ftpTestCase.getFileDownloadPath().equals("")|| !ftpTestCase.getUploadFilePath().equals("")){
			  if(ftpTestCase.getFtpServerURL().equals("")){
				context.getExternalContext().getRequestMap().put("failuremessage", "FTP Test: Please enter the FTP server URL/IP.");
				return "testCreatedFailed";
			  }
			  if(ftpTestCase.getNoOfRepeatCycles().equals("")){
				context.getExternalContext().getRequestMap().put("failuremessage", "FTP Test: Please enter the Number of Cycles.");
				return "testCreatedFailed";
			  }else if(!isInteger(ftpTestCase.getNoOfRepeatCycles())){
				    context.getExternalContext().getRequestMap().put("failuremessage", "FTP Test: Please enter only numbers in Number of Cycles.");
					return "testCreatedFailed";
			  }
			  if(Integer.valueOf(ftpTestCase.getNoOfRepeatCycles()) == 0) {
			  		context.getExternalContext().getRequestMap().put("failuremessage", "FTP Test: Please enter valid  number of Cycles");
					return "testCreatedFailed";
			   }
			  if(Integer.valueOf(ftpTestCase.getNoOfRepeatCycles()) > 100) {
			  		context.getExternalContext().getRequestMap().put("failuremessage", "FTP Test: Number of Cycles should not exceed more than 100.");
					return "testCreatedFailed";
			  }
			  if(ftpTestCase.getFtpUsername().equals("")){
				  context.getExternalContext().getRequestMap().put("failuremessage", "FTP Test: Please enter the Username.");
				  return "testCreatedFailed";
			  }else if(checkSpaces(ftpTestCase.getFtpUsername())){
				  context.getExternalContext().getRequestMap().put("failuremessage", "FTP Test: Enter valid Username");
				  return "testCreatedFailed";
			  }
			  
			  if(ftpTestCase.getFtpPassword().equals("")){
				  context.getExternalContext().getRequestMap().put("failuremessage", "FTP Test: Please Enter the Password.");
				  return "testCreatedFailed";
			  }else if(checkSpaces(ftpTestCase.getFtpPassword())){
				  context.getExternalContext().getRequestMap().put("failuremessage", "FTP Test: Enter valid Password");
				  return "testCreatedFailed";
			  }
			  if(ftpTestCase.getFileDownloadPath().equals("")){
				  context.getExternalContext().getRequestMap().put("failuremessage", "FTP Test: Please enter the File Download Path.");
				  return "testCreatedFailed";
			  }
			  if(ftpTestCase.getUploadFilePath().equals("")){
				  context.getExternalContext().getRequestMap().put("failuremessage", "FTP Test: Please enter the Upload File Path.");
				  return "testCreatedFailed";
			  }
	     	}	  
			//UDP TestCase
		 if(!udpTestCase.getUdpServerURL().equals("") || !udpTestCase.getUdpServerPort().equals("") || !udpTestCase.getFilePathToUpload().equals("")||!udpTestCase.getNoOfRepeatCyclesUDP().equals("")){
			  if(udpTestCase.getUdpServerURL().equals("")){
				  context.getExternalContext().getRequestMap().put("failuremessage", "UDP Test: Please enter the UDP Server URL/IP.");
				  return "testCreatedFailed";
			  }
			  if(udpTestCase.getUdpServerPort().equals("")){
				  context.getExternalContext().getRequestMap().put("failuremessage", "UDP Test: Please enter the UDP Server Port");
				  return "testCreatedFailed";
			  }else if(!isInteger(udpTestCase.getUdpServerPort())){
				  context.getExternalContext().getRequestMap().put("failuremessage", "UDP Test: Please enter only numeric for UDP server port");
				  return "testUpdateCreatedFailed";
			  }else{
				  if(udpTestCase.getUdpServerPort().length()>4){
					  context.getExternalContext().getRequestMap().put("failuremessage", "UDP Test: UDP server port length shouldn't be greater than 4 numbers.");
					  return "testUpdateCreatedFailed";
			     }
			  }
			  
			  if(udpTestCase.getFilePathToUpload().equals("")){
				  context.getExternalContext().getRequestMap().put("failuremessage", "UDP Test: Please enter the File Path To Upload");
				  return "testCreatedFailed";
			  }
			  if(udpTestCase.getNoOfRepeatCyclesUDP().equals("")){
				  context.getExternalContext().getRequestMap().put("failuremessage", "UDP Test: Please enter the number of Cycles.");
				  return "testCreatedFailed";
			  }else if(!isInteger(udpTestCase.getNoOfRepeatCyclesUDP())){
						context.getExternalContext().getRequestMap().put("failuremessage", "UDP Test: Please enter only numeric for No. Of Repeat Cycles");
						return "testCreatedFailed";
			  }
			  if(Integer.valueOf(udpTestCase.getNoOfRepeatCyclesUDP()) == 0) {
			  		context.getExternalContext().getRequestMap().put("failuremessage", "UDP Test: Please enter valid Number Of Cycles.");
					return "testCreatedFailed";
			   }
			  if(Integer.valueOf(udpTestCase.getNoOfRepeatCyclesUDP()) > 100) {
			  		context.getExternalContext().getRequestMap().put("failuremessage", "UDP Test: Number of cycles should not exceed more than 100 cycles.");
					return "testCreatedFailed";
			  }
		 }	  
			//PingTest TestCase
		 if(!dataPingTestCase.getNoOfRepeatCyclesPing().equals("")||!dataPingTestCase.getServerURL().equals("")){
			  if(dataPingTestCase.getServerURL().equals("")){
				  context.getExternalContext().getRequestMap().put("failuremessage", "Ping Test: Please enter the server URL.");
				  return "testCreatedFailed";
			  }else{
				  if(dataPingTestCase.getServerURL().equals("0.0.0.0")){
					  context.getExternalContext().getRequestMap().put("failuremessage", "Ping Test: Please enter the valid server URL.");
					  return "testCreatedFailed";
				  }
				  boolean valid = validate(dataPingTestCase.getServerURL());
				  boolean validWeb = validateWeb(dataPingTestCase.getServerURL()); 
				  if(valid == false && validWeb == false){
					  context.getExternalContext().getRequestMap().put("failuremessage", "Ping Test: Please enter the valid server URL.");
					  return "testCreatedFailed";
				  }
			  }
			  
			 if(dataPingTestCase.getNoOfRepeatCyclesPing().equals("")){
				  context.getExternalContext().getRequestMap().put("failuremessage", "Ping Test: Please enter the Number of Cycles.");
				  return "testCreatedFailed";
			  }else if(!isInteger(dataPingTestCase.getNoOfRepeatCyclesPing())){
					context.getExternalContext().getRequestMap().put("failuremessage", "Ping Test: Please Enter only numeric Number of Cycles.");
					return "testCreatedFailed";
			  }
			  if(Integer.valueOf(dataPingTestCase.getNoOfRepeatCyclesPing()) == 0) {
			  		context.getExternalContext().getRequestMap().put("failuremessage", "Ping Test: Please enter valid Cycles.");
					return "testCreatedFailed";
			   }
			  if(Integer.valueOf(dataPingTestCase.getNoOfRepeatCyclesPing()) > 100) {
			  		context.getExternalContext().getRequestMap().put("failuremessage", "Ping Test: Cycles should not exceed more than 100.");
					return "testCreatedFailed";
			  }
			  
		 }
			//webPage TestCase 
			  
		 if(!webPageTestCase.getWebPageURL().equals("") || !webPageTestCase.getNumberofrepeatcyclesInWeb().equals("")){ 
			  if(webPageTestCase.getWebPageURL().equals("")){
				  context.getExternalContext().getRequestMap().put("failuremessage", "Browser Test: Please enter the URL.");
				  return "testCreatedFailed";
			  }else{
				  URL u = null;
			 try {
			  	   u = new URL(webPageTestCase.getWebPageURL());
			  	  } catch (MalformedURLException e) {
			  		context.getExternalContext().getRequestMap().put("failuremessage", "Browser Test: URL is invalid.");
					return "testCreatedFailed";
			  	  }
			  	  try {
			  	   u.toURI();
			  	  } catch (URISyntaxException e) {
			  		context.getExternalContext().getRequestMap().put("failuremessage", "Browser Test: URL is invalid.");
					return "testCreatedFailed";
			  	  }
			  }
			  
			  if(webPageTestCase.getNumberofrepeatcyclesInWeb().equals("")){
				  context.getExternalContext().getRequestMap().put("failuremessage", "Browser Test: Please enter the Number of Cycles.");
				  return "testCreatedFailed";
			  }else if(!isInteger(webPageTestCase.getNumberofrepeatcyclesInWeb())){
				    context.getExternalContext().getRequestMap().put("failuremessage", "Browser Test: Please enter only numeric in Cycles.");
					return "testCreatedFailed";
			  }
			  if(Integer.valueOf(webPageTestCase.getNumberofrepeatcyclesInWeb()) == 0) {
			  		context.getExternalContext().getRequestMap().put("failuremessage", "Browser Test: Please enter valid  number of Cycles.");
					return "testCreatedFailed";
			   }
			  if(Integer.valueOf(webPageTestCase.getNumberofrepeatcyclesInWeb()) > 100) {
			  		context.getExternalContext().getRequestMap().put("failuremessage", "Browser Test: Number of Cycles should not exceed 100.");
					return "testCreatedFailed";
			  }
		 }
			// voip TestCase
		  if(!voipTestCase.getTestDurationVoip().equals("")){
			  
			  if(voipTestCase.getTestDurationVoip().equals("")){
				  context.getExternalContext().getRequestMap().put("failuremessage", "VoIP Test: Please enter the Test Duration.");
				  return "testCreatedFailed";
			  }else if(isInteger(voipTestCase.getTestDurationVoip())){
			    if(Integer.valueOf(voipTestCase.getTestDurationVoip())>90){
					context.getExternalContext().getRequestMap().put("failuremessage", "VoIP Test: Test Duration shouldn't exceed 90 mins.");
					return "testCreatedFailed";
				}else if(Integer.valueOf(voipTestCase.getTestDurationVoip()) == 0) {
			  		context.getExternalContext().getRequestMap().put("failuremessage", "VoIP Test: Please enter valid Test Duration.");
					return "testCreatedFailed";
			  	}
			  }else{
			  		context.getExternalContext().getRequestMap().put("failuremessage", "VoIP Test: Please enter test duration numeric only.");
					return "testCreatedFailed";
			  }
		}
		return "success";
	}
	/**
         TestCases Validation for the Edit Configuration 
    */
	public String validateEditTestCases(String status,FacesContext context,String testName,MOTestCase moTestCase,MTTestCase mtTestCase,FTPTestCase ftpTestCase,UDPTestCase udpTestCase, DataPingTestCase dataPingTestCase,WebPageTestCase webPageTestCase,VoipTestCase voipTestCase,String isExternal){
		 Pattern pattern;
		 Matcher matcher;
		 final String TESTNAME_PATTERN = "[A-Za-z0-9]+";
		 pattern = Pattern.compile(TESTNAME_PATTERN);
		 if(!status.equals("Update")){
				if(testName.equals("")){
					context.getExternalContext().getRequestMap().put("failuremessage", " Enter Config Name");
					return "testUpdateCreatedFailed";
				}else{
					matcher = pattern.matcher(testName);
					boolean valid = matcher.matches();
					if(!valid){
						context.getExternalContext().getRequestMap().put("failuremessage", "Config name should be alphanumeric only.");
						return "testUpdateCreatedFailed";
					}else{
						TestConfigDao testConfigDao = new TestConfigDaoImpl();
						if(testConfigDao.getTestNamesInMap().containsKey(testName)){
							context.getExternalContext().getRequestMap().put("failuremessage", "Config with name: '"+testName+"' already exists");
							return "testUpdateCreatedFailed";
						}
					}
				}
		 }
	
		if((moTestCase.getPhoneNumber().equals("") && moTestCase.getCallDuration().equals("") && moTestCase.getPauseTime().equals("") && moTestCase.getTestDuration().equals("")) && (ftpTestCase.getFtpServerURL().equals("") && ftpTestCase.getFtpUsername().equals("") && ftpTestCase.getFtpPassword().equals("") && ftpTestCase.getNoOfRepeatCycles().equals("")&& ftpTestCase.getUploadFilePath().equals("")&& ftpTestCase.getFileDownloadPath().equals(""))
				&&(mtTestCase.getTestDurationMt().equals(""))&& (udpTestCase.getUdpServerURL().equals("") &&udpTestCase.getUdpServerPort().equals("")&&udpTestCase.getNoOfRepeatCyclesUDP().equals("") && udpTestCase.getFilePathToUpload().equals(""))&&(dataPingTestCase.getServerURL().equals("") && dataPingTestCase.getNoOfRepeatCyclesPing().equals(""))&&(webPageTestCase.getWebPageURL().equals("") && webPageTestCase.getNumberofrepeatcyclesInWeb().equals(""))&&(voipTestCase.getTestDurationVoip().equals(""))&&null==isExternal){
			context.getExternalContext().getRequestMap().put("failuremessage", "Please configure atleast one test");
			return "testUpdateCreatedFailed";
		}
		
		//MO TestCase
		Pattern pattern1 = Pattern.compile("^[+]?+\\d{0,20}");
	    Matcher matcher1 = pattern1.matcher(moTestCase.getPhoneNumber());
		if(!moTestCase.getPhoneNumber().equals("") || !moTestCase.getCallDuration().equals("") || !moTestCase.getPauseTime().equals("") || !moTestCase.getTestDuration().equals("")){
			if(moTestCase.getPhoneNumber().equals("")){
				 context.getExternalContext().getRequestMap().put("failuremessage", "MO Test: Please enter phone number.");
				 return "testUpdateCreatedFailed";
			  }else if(moTestCase.getPhoneNumber().length()>= 20){
					context.getExternalContext().getRequestMap().put("failuremessage", "MO Test: Please enter  phone numbers less than 20 digits");
					return "testUpdateCreatedFailed";
			  }else if(matcher1.matches()){
				  System.out.println("Phone number is valid");
			  }else{
				  context.getExternalContext().getRequestMap().put("failuremessage", "MO Test: Phone Number must be in the format  +XXXXXXXXXX  OR XXXXXXXXXX");
				  return "testUpdateCreatedFailed";
			  }
			/*else if(isInteger(moTestCase.getPhoneNumber())){
				  if(moTestCase.getPhoneNumber().length()>= 20){
					context.getExternalContext().getRequestMap().put("failuremessage", "MO Test: Please enter  phone numbers less than 20 digits");
					return "testUpdateCreatedFailed";
				}
			}else{
				    context.getExternalContext().getRequestMap().put("failuremessage", "MO Test: Phone number should be numbers only.");
				    return "testUpdateCreatedFailed";
			}*/
			
			if(moTestCase.getCallDuration().equals("")){
				context.getExternalContext().getRequestMap().put("failuremessage", "MO Test: Please enter Call Duration.");
				return "testUpdateCreatedFailed";
			}else if(isInteger(moTestCase.getCallDuration())){
			  	if(Integer.valueOf(moTestCase.getCallDuration()) > 90){
					context.getExternalContext().getRequestMap().put("failuremessage", "MO Test: Maximum Call Duration limit is 90 mins only."); 
					return "testUpdateCreatedFailed";
				}
			  }else{
			  		context.getExternalContext().getRequestMap().put("failuremessage", "MO Test: Call Duration should be numbers only."); 
			  		return "testUpdateCreatedFailed";
			  }
			if(moTestCase.getPauseTime().equals("")){
				context.getExternalContext().getRequestMap().put("failuremessage", "MO Test: Please enter Pause Time.");
				return "testUpdateCreatedFailed";
			}else if(moTestCase.getPauseTime().equals("0")) {
		  		context.getExternalContext().getRequestMap().put("failuremessage", "MO Test: Please enter valid Pause Time."); 
		  		return "testUpdateCreatedFailed";
		  	}else if(isInteger(moTestCase.getPauseTime())){
				int pauTime = Integer.valueOf(moTestCase.getPauseTime());
				double pausetime = Integer.valueOf(pauTime)/60;
				if(pausetime==0.0){
					pausetime = 1.0;
				}
				if((Integer.valueOf(moTestCase.getCallDuration())) + (Math.floor(pausetime)) > 90){
					context.getExternalContext().getRequestMap().put("failuremessage", "MO Test: Total of CallDuration and PauseTime should not be exceeding 90mins."); 
			  		return "testUpdateCreatedFailed";
				}
			  }else{
			  		context.getExternalContext().getSessionMap().put("failuremessage", "MO Test: Please enter Pause Time in numeric only."); 
			  		return "testUpdateCreatedFailed";
			  }
			if(moTestCase.getTestDuration().equals("")){
				context.getExternalContext().getRequestMap().put("failuremessage", "MO Test: Please enter Test Duration.");
				return "testCreatedFailed";
			}else if(isInteger(moTestCase.getTestDuration())){
					double pausetime = Integer.valueOf(moTestCase.getPauseTime())/60;
					if(pausetime==0.0){
						pausetime = 1.0;
					}
			    if((Integer.valueOf(moTestCase.getCallDuration()) + Math.floor(pausetime)) > Integer.valueOf(moTestCase.getTestDuration())){
					context.getExternalContext().getRequestMap().put("failuremessage", "MO Test: Test duration should not be lesser than the sum of Call Duration and Pause Time.");
					return "testUpdateCreatedFailed";
				}else if(Integer.valueOf(moTestCase.getTestDuration())>90){
					context.getExternalContext().getRequestMap().put("failuremessage", "MO Test: Test Duration max limit is 90 mins.");
					return "testUpdateCreatedFailed";
				}
			  }else{
			  		context.getExternalContext().getRequestMap().put("failuremessage", "MO Test: Test Duration should be numeric only.");
					return "testUpdateCreatedFailed";
			  }
		}
			//MT TestCase
			
		if(!mtTestCase.getTestDurationMt().equals("") || !mtTestCase.getCallDurationMt().equals("")){
			if(mtTestCase.getTestDurationMt().equals("")){
				context.getExternalContext().getRequestMap().put("failuremessage", "MT Test: Please enter the Test Duration.");
				return "testUpdateCreatedFailed";
			}else if(isInteger(mtTestCase.getTestDurationMt())){
				 if(Integer.valueOf(mtTestCase.getTestDurationMt())>90){
					context.getExternalContext().getRequestMap().put("failuremessage", "MT Test: Test duration should not exceed 90mins.");
					return "testUpdateCreatedFailed";
				}else if(Integer.valueOf(mtTestCase.getTestDurationMt()) == 0) {
			  		context.getExternalContext().getRequestMap().put("failuremessage", "MT Test: Please enter valid test duration.");
					return "testUpdateCreatedFailed";
			  	}
			  }else{
			  		context.getExternalContext().getRequestMap().put("failuremessage", "MT Test: Please enter test duration  in numeric only.");
					return "testUpdateCreatedFailed";
			  }
			
			if(mtTestCase.getCallDurationMt().equals("")){
				context.getExternalContext().getRequestMap().put("failuremessage", "MT Test: Please enter Call Duration.");
				return "testUpdateCreatedFailed";
			}else if(isInteger(mtTestCase.getCallDurationMt())){
			  	if(Integer.valueOf(mtTestCase.getCallDurationMt()) > 90){
					context.getExternalContext().getRequestMap().put("failuremessage", "MT Test: Maximum Call Duration limit is 90 mins only."); 
					return "testUpdateCreatedFailed";
				} if(Integer.valueOf(mtTestCase.getCallDurationMt())>=Integer.valueOf(mtTestCase.getTestDurationMt())){
					context.getExternalContext().getRequestMap().put("failuremessage", "MT Test: Test Duration should not be lesser than or equal to Call duration "); 
					return "testCreatedFailed";
				}	
			  }else{
			  		context.getExternalContext().getRequestMap().put("failuremessage", "MT Test: Call Duration should be numbers only."); 
			  		return "testUpdateCreatedFailed";
			  }
		  }
		
	
			//FTP TestCase
		if(!ftpTestCase.getFtpServerURL().equals("") || !ftpTestCase.getNoOfRepeatCycles().equals("")|| !ftpTestCase.getFtpUsername().equals("")|| !ftpTestCase.getFtpPassword().equals("")|| !ftpTestCase.getFileDownloadPath().equals("")|| !ftpTestCase.getUploadFilePath().equals("")){
			  if(ftpTestCase.getFtpServerURL().equals("")){
				context.getExternalContext().getRequestMap().put("failuremessage", "FTP Test: Please enter the FTP server URL/IP.");
				return "testUpdateCreatedFailed";
			  }
			  if(ftpTestCase.getNoOfRepeatCycles().equals("")){
				context.getExternalContext().getRequestMap().put("failuremessage", "FTP Test: Please enter the Number of Cycles.");
				return "testUpdateCreatedFailed";
			  }else if(!isInteger(ftpTestCase.getNoOfRepeatCycles())){
				    context.getExternalContext().getRequestMap().put("failuremessage", "FTP Test: Please enter only numbers in Number of Cycles.");
					return "testUpdateCreatedFailed";
			  }
			  if(Integer.valueOf(ftpTestCase.getNoOfRepeatCycles()) == 0) {
			  		context.getExternalContext().getRequestMap().put("failuremessage", "FTP Test: Please enter valid  number of Cycles");
					return "testUpdateCreatedFailed";
			   }
			  if(Integer.valueOf(ftpTestCase.getNoOfRepeatCycles()) > 100) {
			  		context.getExternalContext().getRequestMap().put("failuremessage", "FTP Test: Number of Cycles should not exceed more than 100.");
					return "testUpdateCreatedFailed";
			  }
			  if(ftpTestCase.getFtpUsername().equals("")){
				  context.getExternalContext().getRequestMap().put("failuremessage", "FTP Test: Please enter the Username.");
				  return "testUpdateCreatedFailed";
			  }else if(checkSpaces(ftpTestCase.getFtpUsername())){
				  context.getExternalContext().getRequestMap().put("failuremessage", "FTP Test: Enter valid Username");
				  return "testUpdateCreatedFailed";
			  }
			  
			  if(ftpTestCase.getFtpPassword().equals("")){
				  context.getExternalContext().getRequestMap().put("failuremessage", "FTP Test: Please Enter the Password.");
				  return "testUpdateCreatedFailed";
			  }else if(checkSpaces(ftpTestCase.getFtpPassword())){
				  context.getExternalContext().getRequestMap().put("failuremessage", "FTP Test: Enter valid Password");
				  return "testUpdateCreatedFailed";
			  }
			  if(ftpTestCase.getFileDownloadPath().equals("")){
				  context.getExternalContext().getRequestMap().put("failuremessage", "FTP Test: Please enter the File Download Path.");
				  return "testUpdateCreatedFailed";
			  }
			  if(ftpTestCase.getUploadFilePath().equals("")){
				  context.getExternalContext().getRequestMap().put("failuremessage", "FTP Test: Please enter the Upload File Path.");
				  return "testUpdateCreatedFailed";
			  }
	     	}	  
			//UDP TestCase
		 if(!udpTestCase.getUdpServerURL().equals("") || !udpTestCase.getUdpServerPort().equals("") || !udpTestCase.getFilePathToUpload().equals("")||!udpTestCase.getNoOfRepeatCyclesUDP().equals("")){
			  if(udpTestCase.getUdpServerURL().equals("")){
				  context.getExternalContext().getRequestMap().put("failuremessage", "UDP Test: Please enter the UDP Server URL/IP.");
				  return "testUpdateCreatedFailed";
			  }
			  if(udpTestCase.getUdpServerPort().equals("")){
				  context.getExternalContext().getRequestMap().put("failuremessage", "UDP Test: Please enter the UDP Server Port");
				  return "testUpdateCreatedFailed";
			  }else if(!isInteger(udpTestCase.getUdpServerPort())){
				  context.getExternalContext().getRequestMap().put("failuremessage", "UDP Test: Please enter only numeric for UDP server port");
				  return "testUpdateCreatedFailed";
			  }else{
				  if(udpTestCase.getUdpServerPort().length()>4){
					  context.getExternalContext().getRequestMap().put("failuremessage", "UDP Test: UDP server port length shouldn't be greater than 4 numbers.");
					  return "testUpdateCreatedFailed";
			     }
			  }
			  if(udpTestCase.getFilePathToUpload().equals("")){
				  context.getExternalContext().getRequestMap().put("failuremessage", "UDP Test: Please enter the File Path To Upload");
				  return "testUpdateCreatedFailed";
			  }
			  if(udpTestCase.getNoOfRepeatCyclesUDP().equals("")){
				  context.getExternalContext().getRequestMap().put("failuremessage", "UDP Test: Please enter the number of Cycles.");
				  return "testUpdateCreatedFailed";
			  }else if(!isInteger(udpTestCase.getNoOfRepeatCyclesUDP())){
						context.getExternalContext().getRequestMap().put("failuremessage", "UDP Test: Please enter only numeric for No. Of Repeat Cycles");
						return "testUpdateCreatedFailed";
			  }
			  if(Integer.valueOf(udpTestCase.getNoOfRepeatCyclesUDP()) == 0) {
			  		context.getExternalContext().getRequestMap().put("failuremessage", "UDP Test: Please enter valid Number Of Cycles.");
					return "testUpdateCreatedFailed";
			   }
			  if(Integer.valueOf(udpTestCase.getNoOfRepeatCyclesUDP()) > 100) {
			  		context.getExternalContext().getRequestMap().put("failuremessage", "UDP Test: Number of cycles should not exceed more than 100 cycles.");
					return "testUpdateCreatedFailed";
			  }
		 }	  
			//PingTest TestCase
		 if(!dataPingTestCase.getNoOfRepeatCyclesPing().equals("")||!dataPingTestCase.getServerURL().equals("")){
			 if(dataPingTestCase.getServerURL().equals("")){
				  context.getExternalContext().getRequestMap().put("failuremessage", "Ping Test: Please enter the server URL.");
				  return "testUpdateCreatedFailed";
			  }else{
				  if(dataPingTestCase.getServerURL().equals("0.0.0.0")){
					  context.getExternalContext().getRequestMap().put("failuremessage", "Ping Test: Please enter the valid server URL.");
					  return "testCreatedFailed";
				  }
				  boolean valid = validate(dataPingTestCase.getServerURL());
				  boolean validWeb = validateWeb(dataPingTestCase.getServerURL());
				  if(valid == false && validWeb == false){
					  context.getExternalContext().getRequestMap().put("failuremessage", "Ping Test: Please enter the valid server URL.");
					  return "testUpdateCreatedFailed";
				  }
			  }
			  if(dataPingTestCase.getNoOfRepeatCyclesPing().equals("")){
				  context.getExternalContext().getRequestMap().put("failuremessage", "Ping Test: Please enter the Number of Cycles.");
				  return "testUpdateCreatedFailed";
			  }else if(!isInteger(dataPingTestCase.getNoOfRepeatCyclesPing())){
					context.getExternalContext().getRequestMap().put("failuremessage", "Ping Test: Please Enter only numeric Number of Cycles.");
					return "testUpdateCreatedFailed";
			  }
			  if(Integer.valueOf(dataPingTestCase.getNoOfRepeatCyclesPing()) == 0) {
			  		context.getExternalContext().getRequestMap().put("failuremessage", "Ping Test: Please enter valid Cycles.");
					return "testUpdateCreatedFailed";
			   }
			  if(Integer.valueOf(dataPingTestCase.getNoOfRepeatCyclesPing()) > 100) {
			  		context.getExternalContext().getRequestMap().put("failuremessage", "Ping Test: Cycles should not exceed more than 100.");
					return "testUpdateCreatedFailed";
			  }
			  
			 
		 }
			//webPage TestCase 
			  
		 if(!webPageTestCase.getWebPageURL().equals("") || !webPageTestCase.getNumberofrepeatcyclesInWeb().equals("")){ 
			  if(webPageTestCase.getWebPageURL().equals("")){
				  context.getExternalContext().getRequestMap().put("failuremessage", "Browser Test: Please enter the URL.");
				  return "testUpdateCreatedFailed";
			  }else{
				  URL u = null;
			 try {
			  	   u = new URL(webPageTestCase.getWebPageURL());
			  	  } catch (MalformedURLException e) {
			  		context.getExternalContext().getRequestMap().put("failuremessage", "Browser Test: URL is invalid.");
					return "testUpdateCreatedFailed";
			  	  }
			  	  try {
			  	   u.toURI();
			  	  } catch (URISyntaxException e) {
			  		context.getExternalContext().getRequestMap().put("failuremessage", "Browser Test: URL is invalid.");
					return "testUpdateCreatedFailed";
			  	  }
			  }
			  
			  if(webPageTestCase.getNumberofrepeatcyclesInWeb().equals("")){
				  context.getExternalContext().getRequestMap().put("failuremessage", "Browser Test: Please enter the Number of Cycles.");
				  return "testUpdateCreatedFailed";
			  }else if(!isInteger(webPageTestCase.getNumberofrepeatcyclesInWeb())){
				    context.getExternalContext().getRequestMap().put("failuremessage", "Browser Test: Please enter only numeric in Cycles.");
					return "testUpdateCreatedFailed";
			  }
			  if(Integer.valueOf(webPageTestCase.getNumberofrepeatcyclesInWeb()) == 0) {
			  		context.getExternalContext().getRequestMap().put("failuremessage", "Browser Test: Please enter valid  number of Cycles.");
					return "testUpdateCreatedFailed";
			   }
			  if(Integer.valueOf(webPageTestCase.getNumberofrepeatcyclesInWeb()) > 100) {
			  		context.getExternalContext().getRequestMap().put("failuremessage", "Browser Test: Number of Cycles should not exceed 100.");
					return "testUpdateCreatedFailed";
			  }
		 }
			// voip TestCase
		  if(!voipTestCase.getTestDurationVoip().equals("")){
			  
			  if(voipTestCase.getTestDurationVoip().equals("")){
				  context.getExternalContext().getRequestMap().put("failuremessage", "VoIP Test: Please enter the Test Duration.");
				  return "testUpdateCreatedFailed";
			  }else if(isInteger(voipTestCase.getTestDurationVoip())){
			    if(Integer.valueOf(voipTestCase.getTestDurationVoip())>90){
					context.getExternalContext().getRequestMap().put("failuremessage", "VoIP Test: Test Duration shouldn't exceed 90 mins.");
					return "testUpdateCreatedFailed";
				}else if(Integer.valueOf(voipTestCase.getTestDurationVoip()) == 0) {
			  		context.getExternalContext().getRequestMap().put("failuremessage", "VoIP Test: Please enter valid Test Duration.");
					return "testUpdateCreatedFailed";
			  	}
			  }else{
			  		context.getExternalContext().getRequestMap().put("failuremessage", "VoIP Test: Please enter test duration numeric only.");
					return "testUpdateCreatedFailed";
			  }
		}
		return "updateSuccess";
	}
	/**
        Implemented the Functionality to Create a configuration 
    */
	public String addTestCase() {
		FacesContext context = FacesContext.getCurrentInstance();
		//Need to store TestName in the database to maintain the unique testname
		String returnstatus=null;
		String status="Insert";
		String isExternal=context.getExternalContext().getRequestParameterMap().get("isExternal");
		if(testName.equals("")||!testName.equals("")){
			 returnstatus=validateTestCases(status,context, testName, moTestCase, mtTestCase, ftpTestCase, udpTestCase, dataPingTestCase, webPageTestCase, voipTestCase,isExternal);
			 if(!returnstatus.equals("success")){
				 return returnstatus;
			 }
		}
		
		String configXML="<testconfig><testname>"+testName+"</testname>";
		if(moTestCase.getPhoneNumber() !=null && !moTestCase.getPhoneNumber().equals("")){
			configXML=configXML+"<test type=\"mo\"><phonenumber>"+moTestCase.getPhoneNumber()+"</phonenumber><callduration>"+moTestCase.getCallDuration()+"</callduration><pausetime>"+moTestCase.getPauseTime()+"</pausetime><testduration>"+moTestCase.getTestDuration()+"</testduration></test>";
		}
		if(ftpTestCase.getFtpServerURL() !=null && !ftpTestCase.getFtpServerURL().equals("")){
			configXML=configXML+"<test type=\"ftp\"> <ftpserverurl>"+ftpTestCase.getFtpServerURL()+"</ftpserverurl><username>"+ftpTestCase.getFtpUsername()+"</username><password>"+ftpTestCase.getFtpPassword()+"</password><numberofrepeatcycles>"+ftpTestCase.getNoOfRepeatCycles()+"</numberofrepeatcycles><filepathtoupload>"+ftpTestCase.getUploadFilePath()+"</filepathtoupload><filepathtodownload>"+ftpTestCase.getFileDownloadPath()+"</filepathtodownload></test>";
		}
		if(mtTestCase.getTestDurationMt() !=null && !mtTestCase.getTestDurationMt().equals("")){
			configXML=configXML+"<test type=\"mt\"><testduration>"+mtTestCase.getTestDurationMt()+"</testduration><callduration>"+mtTestCase.getCallDurationMt()+"</callduration></test>";
		}
		if(udpTestCase.getUdpServerURL() != null && !udpTestCase.getUdpServerURL().equals("")){
			configXML=configXML+"<test type=\"udp\"><udpserverurl>"+udpTestCase.getUdpServerURL()+"</udpserverurl><udpserverport>"+udpTestCase.getUdpServerPort()+"</udpserverport><numberofrepeatcycles>"+udpTestCase.getNoOfRepeatCyclesUDP()+"</numberofrepeatcycles><filepathtoupload>"+udpTestCase.getFilePathToUpload()+"</filepathtoupload></test>";
		}
		if(dataPingTestCase.getServerURL() != null && !dataPingTestCase.getServerURL().equals("")){
			configXML=configXML+"<test type=\"pingtest\"><pingserverurl>"+dataPingTestCase.getServerURL()+"</pingserverurl><numberofrepeatcycles>"+dataPingTestCase.getNoOfRepeatCyclesPing()+"</numberofrepeatcycles></test>";
		}
		if(webPageTestCase.getWebPageURL() != null && !webPageTestCase.getWebPageURL().equals("")){
			configXML=configXML+"<test type=\"browser\"><pageurl>"+webPageTestCase.getWebPageURL()+"</pageurl><numberofrepeatcycles>"+webPageTestCase.getNumberofrepeatcyclesInWeb()+"</numberofrepeatcycles></test>";
		}
		if(voipTestCase.getTestDurationVoip() != null && !voipTestCase.getTestDurationVoip().equals("")){
			configXML=configXML+"<test type=\"voip\"><testduration>"+voipTestCase.getTestDurationVoip()+"</testduration></test>";
		}
		
		
		if(null!=isExternal&&isExternal.equalsIgnoreCase("on")){
			configXML = configXML +"<external>true</external>";
		}else{
			configXML = configXML +"<external>false</external>";
		}
		configXML = configXML +"<market>"+marketId+"</market>";
//		Map<String,String> marketMap = (Map<String,String>)context.getExternalContext().getSessionMap().get("loggedInUserRoleID");
	/*	for (Entry<Integer, String> entry : testMap.entrySet()) {
            if (entry.getValue().equals("c")) {
                System.out.println(entry.getKey());
            }
        }*/
		configXML=configXML+"</testconfig>";
		TestConfigDao testCaseDao = new TestConfigDaoImpl();
		String roleName = context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		String userId =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
		System.out.println("addTestCase userId -"+userId);
		//By Ankit For super admin we need to give selectedOperatorID default value....we couldn't  give 0 coz that me create problems for us.
		if((roleName.equals("superadmin")||roleName.equals("superadmin"))){
		//TestCaseAction testCaseAction=new TestCaseAction();
		TestCaseAction testCaseAction = (TestCaseAction) context.getExternalContext().getSessionMap().get("testCaseAction");
			if(testCaseAction.getSelectedOperatorID() != null){	
			selectedOperatorID = testCaseAction.getSelectedOperatorID();
			}
			//changed by ankit on 2/12/15
			else{selectedOperatorID = "1";}
		}
		else{
			selectedOperatorID = new UserDaoImpl().getOperator(userId);
			}
		boolean checkTestNameExist = testCaseDao.checkTestNameExist(testName,selectedOperatorID);
		System.out.println("checkTestNameExist "+ checkTestNameExist+",,,,selectedOperatorID -"+selectedOperatorID);
		if(checkTestNameExist)
		{
			context.getExternalContext().getRequestMap().put("failuremessage", "Configuration:"+testName+" is already exist.");
			return "failure";
		}
		else
		{
			Integer testConfigID = testCaseDao.addTestCase(configXML,testName,userId);
			if(testConfigID != null){
			TestConfigParamDao testConfigParamDao = new TestConfigParamDaoImpl();
			if(moTestCase.getPhoneNumber() !=null && !moTestCase.getPhoneNumber().equals("")){
				testConfigParamDao.addTestConfigParam(moTestCase,testConfigID,TestTypeEnum.MO.getTestTypeID(),userId);
			}
			if(ftpTestCase.getFtpServerURL() !=null && !ftpTestCase.getFtpServerURL().equals("")){
				testConfigParamDao.addTestConfigParam(ftpTestCase,testConfigID,TestTypeEnum.FTP.getTestTypeID(),userId);
			}
			if(mtTestCase.getTestDurationMt() !=null && ! mtTestCase.getTestDurationMt().equals("")){
				testConfigParamDao.addTestConfigParam(mtTestCase,testConfigID,TestTypeEnum.MT.getTestTypeID(),userId);
			}
			if(udpTestCase.getUdpServerURL() != null && !udpTestCase.getUdpServerURL().equals("")){
				testConfigParamDao.addTestConfigParam(udpTestCase,testConfigID,TestTypeEnum.UDP.getTestTypeID(),userId);
			}
			if(dataPingTestCase.getServerURL() != null && !dataPingTestCase.getServerURL().equals("")){
				testConfigParamDao.addTestConfigParam(dataPingTestCase,testConfigID,TestTypeEnum.DATAPING.getTestTypeID(),userId);
			}
			if(webPageTestCase.getWebPageURL() != null && !webPageTestCase.getWebPageURL().equals("")){
				testConfigParamDao.addTestConfigParam(webPageTestCase,testConfigID,TestTypeEnum.DATAPING.getTestTypeID(),userId);
			}
			if(voipTestCase.getTestDurationVoip() != null && !voipTestCase.getTestDurationVoip().equals("")){
				testConfigParamDao.addTestConfigParam(voipTestCase,testConfigID,TestTypeEnum.DATAPING.getTestTypeID(),userId);
			}
			if(null!=isExternal&&isExternal.equalsIgnoreCase("on")){
				testConfigParamDao.addTestConfigParam("true",testConfigID,TestTypeEnum.EXTERNAL.getTestTypeID(),userId);
				configXML = configXML +"<external>true</external>";
			}else{
				testConfigParamDao.addTestConfigParam("false",testConfigID,TestTypeEnum.EXTERNAL.getTestTypeID(),userId);
				configXML = configXML +"<external>false</external>";
			}
		}		
		
		System.out.println("selectedOperatorID-"+selectedOperatorID);
			TestConfigUserDao testConfigUserDao = new TestConfigUserDaoImpl();
			testConfigUserDao.assignTestConfigToUser(testConfigID.toString(), selectedOperatorID, "0","0",userId);
		}
		context.getExternalContext().getSessionMap().remove("userBean");
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		context.getExternalContext().getRequestMap().put("message", "Configuration:"+testName+" created Successfully");
		return "success";
	}
	/**
	    Get the List of Users for the Specific Operator.
	 */
	public void operatorValueChanged(ValueChangeEvent e){
		selectedOperatorID = e.getNewValue().toString();
		if(!selectedOperatorID.equals("0")){
			userMap = new UserDaoImpl().getActiveUserBelongsToOperator(selectedOperatorID);
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().getSessionMap().put("userStatus","Active");
		}else{
			//System.out.println("Please select valid Operator===");
		}
	}
	/**
         Get the List of IMEI for the Specific UserId.
     */
	public void userValueChanged(ValueChangeEvent e){
		selUserID = e.getNewValue().toString();
		String active = null;
		if(!selUserID.equals("0")){
			UserDao userdao = new UserDaoImpl();
			imeiMap = userdao.getImeiBelongsToUser(selUserID);
			String selUserRole = userdao.getUserDetails(selUserID).getRole();
			if(selUserRole.equals("6"))active="active";
			//that line selects test by user name if any user create that test then test name will be there after selecting user name. 
			//getTestconfigOnUserValueChanged(e);
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().getSessionMap().put("userStatus","Active");
			context.getExternalContext().getSessionMap().put("isGuestUser",active);
		}else{
			//System.out.println("Please select valid Operator===");
		}
	}
	
	/**
        Implemented the Functionality for assigning the Test Configuration 
     */
	public String assignTestConfig(){
		TestConfigUserDao testConfigUserDao = new TestConfigUserDaoImpl();
		UserDaoImpl udi = new UserDaoImpl();
		FacesContext context = FacesContext.getCurrentInstance();
		String roleName = context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		String userId =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
		//If the test config is not assigned to the operator then assign that to the operator
		if(!(roleName.equals("superadmin")||roleName.equals("superadmin"))){
			selectedOperatorID = new UserDaoImpl().getOperator(userId);
		}
		System.out.print(selUserID);
		
		if(udi.getUserDetails(selUserID).getRole().equals("6")) 
		{
			Map<String, String> getimei = udi.getImeiBelongsToUser(selUserID);
			String[] arr1 = new String[getimei.size()];
			String[] arr2 = new String[getimei.size()];
		    // Get keys.
			int i=0;
			Set<String> keys = getimei.keySet();
			// Loop over String keys.
			for (String key : keys) {
				arr1[i] = key;
				arr2[i] = getimei.get(key);
			    i++;
			}
			List<String> list = new ArrayList<String>(Arrays.asList(arr2));
			list.remove(0);
			selImei = list.toArray(new String[0]); 	
			}
		for(String imei:selImei){
		if(selectedOperatorID.equals("") ||selectedTestConfigId.equals("") ||selUserID.equals("")||selUserID.equals("0") || imei.equals("")){
			context.getExternalContext().getRequestMap().put("message", "Please select the mandatory fields");
			return "failure";
		}
		}
		for(String imei:selImei){
			System.out.println("imei----------"+imei);
			if(testConfigUserDao.checkTestConfigalreadyAssigned(selectedTestConfigId, selectedOperatorID, selUserID,imei)){
				context.getExternalContext().getRequestMap().put("message", "Configuration assigned Successfully");
			}else{
				context.getExternalContext().getRequestMap().put("message", "Configuration assigned Successfully");
				testConfigUserDao.assignTestConfigToUser(selectedTestConfigId, selectedOperatorID, selUserID,imei,userId);
			}
		}
		
		if(!(roleName.equals("superadmin")||roleName.equals("superadmin"))){
			assignListTestConfig=testConfigUserDao.assignListTestConfigOperator(selectedOperatorID,userId);
		}else{
			assignListTestConfig=testConfigUserDao.assignListTestConfig();
		}
		String testConfigMapSize=context.getExternalContext().getSessionMap().get("testConfigMapSize").toString();
		if(Integer.parseInt(testConfigMapSize)<=0){
			context.getExternalContext().getSessionMap().remove("testConfigMapSize");
			context.getExternalContext().getSessionMap().remove("testConfigMessage");
			context.getExternalContext().getRequestMap().put("testConfigMessage", "No Test-Configs are assigned for the Logged user");
			context.getExternalContext().getSessionMap().put("testConfigMapSize", testConfigMap.size());
			
		}
		context.getExternalContext().getSessionMap().remove("assignListTestSize");
		context.getExternalContext().getSessionMap().remove("assignListTestConfig");
		context.getExternalContext().getSessionMap().put("assignListTestSize", assignListTestConfig.size());
		context.getExternalContext().getSessionMap().put("assignListTestConfig", assignListTestConfig);
		context.getExternalContext().getSessionMap().remove("userBean");
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().put("isGuestUser","");
		return "success";
	}
	
	/**
          Navigation from Create user page to Create  Configuration page
	 */
	public String createUserToConfig(){
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		String roleName = context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		String userId =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserID", userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", roleName);
		return "createUserToConfigPage";
	}
	/**
        Navigation from Edit user page to Create  Configuration page
    */
	public String editUserToConfig(){
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		String roleName = context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		String userId =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserID", userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", roleName);
		return "editUserToConfigPage";
	}
	/**
        Navigation from map page to Create Configuration page
    */
	public String welcomeMaptoConfig(){
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		String roleName = context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		String userId =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserID", userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", roleName);
		return "welcomeMaptoConfigPage";
	}
	/**
       Navigation from welcome page to Create Configuration page
    */
	public String welcomeConfig(){
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		String roleName = context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		String userId =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserID", userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", roleName);
		return "welcomeConfigPage";
	}
	
	
	public String welcomeOperatorConfig(){
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		String roleName = context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		String userId =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserID", userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", roleName);
		return "welcomeOperatorConfigPage";
	}
	
	
	/**
        Navigation from Edit Configuration page to Create Configuration page
    */
	public String editWelcomeConfig(){
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		String roleName = context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		String userId =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserID", userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", roleName);
		return "editWelcomeConfigPage";
	}
	/**
        Navigation from Upload page to Create Configuration page
    */
	public String welcomeUploadConfig(){
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		String roleName = context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		String userId =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserID", userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", roleName);
		return "welcomeUploadConfigPage";
	}
	/**
        Implemented the Functionality to display the pop up alert message to  delete the configuration 
    */
	public String deleteConfiguration(){
		FacesContext context = FacesContext.getCurrentInstance();
		boolean status=false;
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		String roleName = context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		String userId =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserID", userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", roleName);
		if(Integer.parseInt(selectedTestConfigId) == 0 || selectedTestConfigId==""){
			context.getExternalContext().getRequestMap().put("failuremessage", "Please Select the Config");
			return "configSelectFailed";
		}
		status= new TestConfigDaoImpl().checkConfigurationAssigned(selectedTestConfigId);
		context.getExternalContext().getRequestMap().remove("configmessage");
		String message=null;
		if(status == true){
			message="Selected Config is assigned to the Users";
			context.getExternalContext().getRequestMap().put("configmessage", message);
			TestCaseAction testCaseAction=new TestCaseAction();
			testCaseAction.setSelectedTestConfigId(selectedTestConfigId);
			testCaseAction.setAction("delete");
			context.getExternalContext().getSessionMap().remove("assigned");
			context.getExternalContext().getSessionMap().remove("testCaseAction");
			context.getExternalContext().getSessionMap().put("testCaseAction",testCaseAction);
			context.getExternalContext().getSessionMap().put("assigned","true");
			return "configAssignedFailed";
		}else {
			message="Selected Config is not assigned to any Users";
			context.getExternalContext().getRequestMap().put("configmessage", message);
			TestCaseAction testCaseAction=new TestCaseAction();
			testCaseAction.setSelectedTestConfigId(selectedTestConfigId);
			testCaseAction.setAction("delete");
			context.getExternalContext().getSessionMap().remove("assigned");
			context.getExternalContext().getSessionMap().remove("testCaseAction");
			context.getExternalContext().getSessionMap().put("testCaseAction",testCaseAction);
			context.getExternalContext().getSessionMap().put("assigned","false");
			return "configAssignedFailed";
		}
	}
	/**
       Implemented the Functionality to delete the configuration 
    */
	public String deleteConfig(){
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		String roleName = context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		String userId =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserID", userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", roleName);
		String assigned=context.getExternalContext().getSessionMap().get("assigned").toString();
		TestConfigDao dao =new TestConfigDaoImpl();
		String testConfigName=dao.getTestConfigNameById(String.valueOf(selectedTestConfigId));
		boolean status=false;
		status=new TestConfigDaoImpl().deleteConfiguration(selectedTestConfigId,assigned);
		if(status == true){
			context.getExternalContext().getRequestMap().put("deleteSucessmessage", "Configuration:"+testConfigName+" deleted successfully");
		}else{
			context.getExternalContext().getRequestMap().put("deleteFailuremessage", "Configuration deletion is unsuccessful");
		}
		if((roleName.equals("superadmin")||roleName.equals("superadmin"))){
			testConfigMap = new TestConfigDaoImpl().getActiveTestConfigInMap();
		}else{
			testConfigMap = new TestConfigDaoImpl().getActiveTestConfigInMapForManager(userId);
		}
		context.getExternalContext().getSessionMap().remove("testConfigMapList");
		context.getExternalContext().getSessionMap().put("testConfigMapList", testConfigMap);
		context.getExternalContext().getSessionMap().remove("assigned");
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		return "deleteConfigPage";
	}
	
	/**
        Implemented the Functionality to display the edit Configuration details
    */
	public String editConfiguration(){
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
		context.getExternalContext().getSessionMap().remove("testCaseAction");
		context.getExternalContext().getSessionMap().remove("testConfig");
		String roleName = context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		String userId =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserID", userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", roleName);
		if(Integer.parseInt(selectedTestConfigId) == 0 || selectedTestConfigId == ""){
			context.getExternalContext().getRequestMap().put("failuremessage", "Please Select the Config");
			return "configeditSelectFailed";
		}
		TestCaseAction testCaseAction=new TestCaseAction();
		testCaseAction.setSelectedTestConfigId(selectedTestConfigId);
		testCaseAction.setSelectedOperatorID(selectedOperatorID);
		testCaseAction.setAction("edit");
		marketId = new TestConfigDaoImpl().getTestMarket(selectedTestConfigId);
		testCaseAction.setMarketId(marketId);
		TestConfigDao testConfigDao=new TestConfigDaoImpl();
		
		testCaseName=testConfigDao.getTestConfigNameById(selectedTestConfigId);
		moTestCase = testConfigDao.getMOTestCaseDetails(selectedTestConfigId);
		mtTestCase = testConfigDao.getMTTestCaseDetails(selectedTestConfigId);
		ftpTestCase =testConfigDao.getFTPTestCaseDetails(selectedTestConfigId);
		udpTestCase = testConfigDao.getUDPTestCaseDetails(selectedTestConfigId);
		dataPingTestCase = testConfigDao.getdataPingTestCaseDetails(selectedTestConfigId);
	    webPageTestCase= testConfigDao.getWebPageTestCaseDetails(selectedTestConfigId);
		voipTestCase=testConfigDao.getVoipTestCaseDetails(selectedTestConfigId);
		String isExternal = new TestConfigDaoImpl().getExternalTestCaseDetails(selectedTestConfigId);
		System.out.println("isExternal----++------"+testCaseAction.getMarketId());
		testCaseAction.setTestName(testCaseName);
		testCaseAction.moTestCase=moTestCase;
		testCaseAction.mtTestCase=mtTestCase;
		testCaseAction.ftpTestCase=ftpTestCase;
		testCaseAction.udpTestCase=udpTestCase;
		testCaseAction.dataPingTestCase=dataPingTestCase;
		testCaseAction.webPageTestCase=webPageTestCase;
		testCaseAction.voipTestCase=voipTestCase;
		context.getExternalContext().getSessionMap().put("testCaseAction",testCaseAction);
		context.getExternalContext().getSessionMap().put("isExternal",isExternal);
		return "editConfigurationPage";
	}
	
	/**
         Implemented the Functionality to edit Configuration details
    */
	public String updateConfiguration(){
		FacesContext context = FacesContext.getCurrentInstance();
		String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
		context.getExternalContext().getSessionMap().remove("testConfig");
		String roleName = context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
		String userId =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
		context.getExternalContext().getSessionMap().put("loggedInUserID", userId);
		context.getExternalContext().getSessionMap().put("loggedInUserRoleID", roleName);
		String returnstatus=null;
		String status="Update";
		String isExternal=context.getExternalContext().getRequestParameterMap().get("isExternal");
		if(testName.equals("")||!testName.equals("")){
			 returnstatus=validateEditTestCases(status,context, testName, moTestCase, mtTestCase, ftpTestCase, udpTestCase, dataPingTestCase, webPageTestCase, voipTestCase,isExternal);
			 if(!returnstatus.equals("updateSuccess")){
				 return returnstatus;
			 }
		}
		
		TestCaseAction testCaseAction=new TestCaseAction();
		testCaseAction.setSelectedTestConfigId(selectedTestConfigId);
		testCaseAction.setSelectedOperatorID(selectedOperatorID);
		testCaseAction.setAction("edit");
		TestConfigDao testConfigDao=new TestConfigDaoImpl();
		String configXML="<testconfig><testname>"+testName+"</testname>";
		if(moTestCase.getPhoneNumber() !=null && !moTestCase.getPhoneNumber().equals("")){
			configXML=configXML+"<test type=\"mo\"><phonenumber>"+moTestCase.getPhoneNumber()+"</phonenumber><callduration>"+moTestCase.getCallDuration()+"</callduration><pausetime>"+moTestCase.getPauseTime()+"</pausetime><testduration>"+moTestCase.getTestDuration()+"</testduration></test>";
		}
		if(ftpTestCase.getFtpServerURL() !=null && !ftpTestCase.getFtpServerURL().equals("")){
			configXML=configXML+"<test type=\"ftp\"> <ftpserverurl>"+ftpTestCase.getFtpServerURL()+"</ftpserverurl><username>"+ftpTestCase.getFtpUsername()+"</username><password>"+ftpTestCase.getFtpPassword()+"</password><numberofrepeatcycles>"+ftpTestCase.getNoOfRepeatCycles()+"</numberofrepeatcycles><filepathtoupload>"+ftpTestCase.getUploadFilePath()+"</filepathtoupload><filepathtodownload>"+ftpTestCase.getFileDownloadPath()+"</filepathtodownload></test>";
		}
		if(mtTestCase.getTestDurationMt() !=null && !mtTestCase.getTestDurationMt().equals("")){
			configXML=configXML+"<test type=\"mt\"><testduration>"+mtTestCase.getTestDurationMt()+"</testduration><callduration>"+mtTestCase.getCallDurationMt()+"</callduration></test>";
		}
		if(udpTestCase.getUdpServerURL() != null && !udpTestCase.getUdpServerURL().equals("")){
			configXML=configXML+"<test type=\"udp\"><udpserverurl>"+udpTestCase.getUdpServerURL()+"</udpserverurl><udpserverport>"+udpTestCase.getUdpServerPort()+"</udpserverport><numberofrepeatcycles>"+udpTestCase.getNoOfRepeatCyclesUDP()+"</numberofrepeatcycles><filepathtoupload>"+udpTestCase.getFilePathToUpload()+"</filepathtoupload></test>";
		}
		if(dataPingTestCase.getServerURL() != null && !dataPingTestCase.getServerURL().equals("")){
			configXML=configXML+"<test type=\"pingtest\"><pingserverurl>"+dataPingTestCase.getServerURL()+"</pingserverurl><numberofrepeatcycles>"+dataPingTestCase.getNoOfRepeatCyclesPing()+"</numberofrepeatcycles></test>";
		}
		if(webPageTestCase.getWebPageURL() != null && !webPageTestCase.getWebPageURL().equals("")){
			configXML=configXML+"<test type=\"browser\"><pageurl>"+webPageTestCase.getWebPageURL()+"</pageurl><numberofrepeatcycles>"+webPageTestCase.getNumberofrepeatcyclesInWeb()+"</numberofrepeatcycles></test>";
		}
		if(voipTestCase.getTestDurationVoip() != null && !voipTestCase.getTestDurationVoip().equals("")){
			configXML=configXML+"<test type=\"voip\"><testduration>"+voipTestCase.getTestDurationVoip()+"</testduration></test>";
		}
		if(null!=isExternal&&isExternal.equalsIgnoreCase("on")){
				configXML = configXML +"<external>true</external>";
			}else{
				configXML = configXML +"<external>false</external>";
			}
		configXML = configXML +"<market>"+marketId+"</market>";
		configXML=configXML+"</testconfig>";
		System.out.println("configXML-------"+configXML);
		TestConfigDao testCaseDao = new TestConfigDaoImpl();
		Integer testConfigID = null;
		/*Map<String, String> testDtaList = testCaseDao.getActiveTestConfigInMapForOprator(selectedOperatorID);
		System.out.println("ByAnkit UserID= "+userId);
		boolean TestNameExist = false;
		for (Map.Entry<String, String> entry : testDtaList.entrySet()) {
		    //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
		    if(entry.getKey().equals(testName)){TestNameExist = true;}
		}*/
		boolean checkTestNameExist = testCaseDao.checkTestNameExist(testName,selectedOperatorID);
		System.out.println("checkTestNameExist "+ checkTestNameExist);
		if(!checkTestNameExist)
		{
			context.getExternalContext().getSessionMap().put("testCaseAction",testCaseAction);
			context.getExternalContext().getSessionMap().put("isExternal",isExternal);
			addTestCase();
			/*context.getExternalContext().getRequestMap().put("failuremessage", "Configuration:"+testName+" is already exist.");
			return "updateConfigurationPage";*/
		}
		else
		
	/*if(!TestNameExist){}
	else*/
	{	
		testConfigID = testCaseDao.updateTestCase(configXML,testName,userId,selectedTestConfigId);
		Integer rows=testCaseDao.deleteTestConfigParam(testConfigID);
		if(rows>=1){
			TestConfigParamDao testConfigParamDao = new TestConfigParamDaoImpl();
			if(moTestCase.getPhoneNumber() !=null && !moTestCase.getPhoneNumber().equals("")){
				testConfigParamDao.updateTestConfigParam(moTestCase,testConfigID,TestTypeEnum.MO.getTestTypeID(),userId);
			}
			if(ftpTestCase.getFtpServerURL() !=null && !ftpTestCase.getFtpServerURL().equals("")){
				testConfigParamDao.updateTestConfigParam(ftpTestCase,testConfigID,TestTypeEnum.FTP.getTestTypeID(),userId);
			}
			if(mtTestCase.getTestDurationMt() !=null && ! mtTestCase.getTestDurationMt().equals("")){
				testConfigParamDao.updateTestConfigParam(mtTestCase,testConfigID,TestTypeEnum.MT.getTestTypeID(),userId);
			}
			if(udpTestCase.getUdpServerURL() != null && !udpTestCase.getUdpServerURL().equals("")){
				testConfigParamDao.updateTestConfigParam(udpTestCase,testConfigID,TestTypeEnum.UDP.getTestTypeID(),userId);
			}
			if(dataPingTestCase.getServerURL() != null && !dataPingTestCase.getServerURL().equals("")){
				testConfigParamDao.updateTestConfigParam(dataPingTestCase,testConfigID,TestTypeEnum.DATAPING.getTestTypeID(),userId);
			}
			if(webPageTestCase.getWebPageURL() != null && !webPageTestCase.getWebPageURL().equals("")){
				testConfigParamDao.updateTestConfigParam(webPageTestCase,testConfigID,TestTypeEnum.DATAPING.getTestTypeID(),userId);
			}
			if(voipTestCase.getTestDurationVoip() != null && !voipTestCase.getTestDurationVoip().equals("")){
				testConfigParamDao.updateTestConfigParam(voipTestCase,testConfigID,TestTypeEnum.DATAPING.getTestTypeID(),userId);
			}
			if(null!=isExternal&&isExternal.equalsIgnoreCase("on")){
				testConfigParamDao.updateTestConfigParam("true",testConfigID,TestTypeEnum.EXTERNAL.getTestTypeID(),userId);
			}else{
				testConfigParamDao.updateTestConfigParam("false",testConfigID,TestTypeEnum.EXTERNAL.getTestTypeID(),userId);
			}
		}	
	
		testCaseName=testConfigDao.getTestConfigNameById(selectedTestConfigId);
		moTestCase = testConfigDao.getMOTestCaseDetails(selectedTestConfigId);
		mtTestCase = testConfigDao.getMTTestCaseDetails(selectedTestConfigId);
		ftpTestCase =testConfigDao.getFTPTestCaseDetails(selectedTestConfigId);
		udpTestCase = testConfigDao.getUDPTestCaseDetails(selectedTestConfigId);
		dataPingTestCase = testConfigDao.getdataPingTestCaseDetails(selectedTestConfigId);
	    webPageTestCase= testConfigDao.getWebPageTestCaseDetails(selectedTestConfigId);
		voipTestCase=testConfigDao.getVoipTestCaseDetails(selectedTestConfigId);
		isExternal = new TestConfigDaoImpl().getExternalTestCaseDetails(selectedTestConfigId);
		System.out.println("isExternal---------"+isExternal);
		testCaseAction.setTestName(testCaseName);
		testCaseAction.moTestCase=moTestCase;
		testCaseAction.mtTestCase=mtTestCase;
		testCaseAction.ftpTestCase=ftpTestCase;
		testCaseAction.udpTestCase=udpTestCase;
		testCaseAction.dataPingTestCase=dataPingTestCase;
		testCaseAction.webPageTestCase=webPageTestCase;
		testCaseAction.voipTestCase=voipTestCase;
		if(selectedTestConfigId !=null){
			context.getExternalContext().getRequestMap().put("successmessage", "Configuration:"+testName+" Updated Successfully");
		}
	}
		context.getExternalContext().getSessionMap().put("testCaseAction",testCaseAction);
		context.getExternalContext().getSessionMap().put("isExternal",isExternal);
		
		return "updateConfigurationPage";
	}
	/**
	   Implemented the Functionality to check whether the Test-Config name already exists.
	 */
	public void attrListener(ActionEvent event){
		String testNameEntered = event.getComponent().getAttributes().get("testNameEntered").toString();
		TestConfigDao testConfigDao = new TestConfigDaoImpl();
		if(testConfigDao.getTestNamesInMap().containsKey(testNameEntered)){
			setTestNameDupMsg("Test Config with name: '"+testNameEntered+"' already exists");
			FacesMessage message = new FacesMessage();
			message.setSummary(testNameDupMsg);
		    message.setSeverity(FacesMessage.SEVERITY_ERROR);
		    throw new ValidatorException(message);
		}
	}
	/**
         Integer Validation
    */
	public static boolean isInteger(String str){
	  return str.matches("^[0-9]+$");  //match a number with optional '-' and decimal.
	}
	/**
        WhiteSpaces Validation
    */
	public boolean checkSpaces(String str){
		boolean spacesConatins = false;
		Pattern whitespace = Pattern.compile("\\s+?");
		Matcher matcher = whitespace.matcher(str);
		while (matcher.find()) {
			spacesConatins = true;
		}
		return spacesConatins;
	}
	/**
          IP Address Validation
	 */
	 public boolean validate(final String ip){	
		  Pattern pattern;
		  Matcher matcher;
		  pattern = Pattern.compile(IPADDRESS_PATTERN);
		  matcher = pattern.matcher(ip);
		  return matcher.matches();	    	    
	    }
	 public boolean validateWeb(final String ip){	
		  Pattern pattern;
		  Matcher matcher;
		  pattern = Pattern.compile(urlPattern);
		  matcher = pattern.matcher(ip);
		  return matcher.matches();	    	    
	    }
		public String welcomeMarketConfig(){
			FacesContext context = FacesContext.getCurrentInstance();
			String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
			context.getExternalContext().getSessionMap().put("loggedInUserRoleID", userrole);
			return "welcomeMarketConfigPage";
		}	
		
		public String gototransferTestPage(){
			//System.out.println("testUpload:");
			FacesContext context = FacesContext.getCurrentInstance();
			List<UserBean> userList = null;
			 Map<String,String> userMap = new LinkedHashMap<String,String>();
			Set<String> userNameList = new HashSet<String>();
			UserDao userDao = new UserDaoImpl();
			String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
			String userIdLogged =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
		
//				 userList = userDao.listUser(userIdLogged);
				userNameList = new UserDaoImpl().getUsersOfSuperior(userIdLogged,userrole);
				userMap = new UserDaoImpl().getUsersForId(userNameList.toString().substring(1, userNameList.toString().length()-1),userrole);
			int i=0;
			/*for(UserBean userBean : userList){
				
				String username = userBean.getUserName();
				userMap.put(userBean.getUserName(),userBean.getUserId() );
			}*/
			System.out.println("userNameList--------"+userNameList);
			context.getExternalContext().getSessionMap().put("userListSize", userMap.size());
			context.getExternalContext().getSessionMap().put("userListdetails", userMap);
			return "transferPage";
		}
		
		public String transferTest(){
			//System.out.println("testUpload:");
			FacesContext context = FacesContext.getCurrentInstance();
			String usrTests = context.getExternalContext().getRequestParameterMap().get("transfertestname");
			String assigneeUser = context.getExternalContext().getRequestParameterMap().get("assigneeUser");
			String[] usersTestArr = usrTests.substring(1, usrTests.length()).split(",");
			System.out.println(usrTests.substring(1, usrTests.length()).split(","));
			new TransferTest().moveManagerFiles(usersTestArr,assigneeUser); 
			return "transferPage";
		}
		
		  public void mapTestTypeListener(AjaxBehaviorEvent event) {
			  System.out.println("mapTestTypeListener-----------------");
			  FacesContext context = FacesContext.getCurrentInstance();
			 
			  
				//System.out.println("testUpload:");
			  TestConfigDao testConfigDao = new TestConfigDaoImpl();
			  String userId =  (String) ((UIOutput)event.getSource()).getValue();
			  String selectedUserRole = new RolesHelper().getUserRole(userId);
			  
			  Map<String,String> userMap = new HashMap<String,String>();
			  System.out.println("userId---------"+userId);
			  Set<String> userNameList = new HashSet<String>();
			  userNameList = new UserDaoImpl().getUsersOfSuperior(userId,selectedUserRole);
			  testNameMap  = new TestConfigDaoImpl().getnTestNamesInMapInHierarchy(userNameList.toString().substring(1, userNameList.toString().length()-1));
				List<UserBean> userList = null;
				UserDao userDao = new UserDaoImpl();
				String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
				String userIdLogged =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
			System.out.println("testNameMap---"+testNameMap);
				context.getExternalContext().getSessionMap().put("userTestListdetails", new ArrayList<String>(testNameMap.keySet()));
			}
		  
		public String frequencyUpload(){
			System.out.println("frequencyUpload:");
			TestConfigDao testConfigDao = new TestConfigDaoImpl();
			FacesContext context = FacesContext.getCurrentInstance();
			MarketPlaceDao marketPlaceDao = new MarketPlaceDaoImpl();
			String userrole=  context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
			String userIdLogged =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
			String userName = context.getExternalContext().getSessionMap().get("userName").toString();
			marketPlaceMap=marketPlaceDao.getUploadMarketPlaceMap(userrole,userIdLogged);
			if(userrole.equals("superadmin")){
				testNameMap = new TestConfigDaoImpl().getTestNamesInMapInStgDevice();
			}else{
				Set<String> userNameList = new UserDaoImpl().getUsersOfSuperior(userIdLogged,userrole);
				testNameMap  = new TestConfigDaoImpl().getnTestNamesInMapInHierarchy(userNameList.toString().substring(1, userNameList.toString().length()-1));
			}
			context.getExternalContext().getSessionMap().remove("testNameMap");
			context.getExternalContext().getSessionMap().remove("margetPlaceListSize");
			context.getExternalContext().getSessionMap().remove("margetPlaceListdetails");
			context.getExternalContext().getSessionMap().put("testNameMap",testNameMap);
			context.getExternalContext().getSessionMap().put("differnceHours", Misc.getDiffHours());
			context.getExternalContext().getSessionMap().put("margetPlaceListSize", marketPlaceMap.size());
			context.getExternalContext().getSessionMap().put("margetPlaceListdetails", marketPlaceMap);
			return "frequencyUploadPage";
		}	

		//code by ankit on 5/11/15 for getting test config value by operator id
		/**
	    Get the List of test config for the Specific Operator.
	 */
	public void getTestconfigOnOperatorValueChanged(ValueChangeEvent e){
		selectedOperatorID = e.getNewValue().toString();
		if(!selectedOperatorID.equals("0")){
			testConfigMap = new TestConfigDaoImpl().getActiveTestConfigInMapForOprator(selectedOperatorID);
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().getSessionMap().remove("testConfigMapList");
			context.getExternalContext().getSessionMap().put("testConfigMapList", testConfigMap);
		}
	}
	/**
    Get the List of test config for the Specific User.
 */
	public void getTestconfigOnUserValueChanged(ValueChangeEvent e){
		selUserID = e.getNewValue().toString();
		System.out.print("selUserID - "+ selUserID);
		if(!selUserID.equals("0")){
		testConfigMap = new TestConfigDaoImpl().getActiveTestConfigInMapForManager(selUserID);
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getSessionMap().remove("testConfigMapList");
		context.getExternalContext().getSessionMap().put("testConfigMapList", testConfigMap);
	}
}
public void ajaxListener(AjaxBehaviorEvent event)
{
	String result = ((UIOutput)event.getSource()).getClientId();//event.getComponent().getClass().getName();
	String[] a = result.split(":", 4);
	Boolean result1 = (Boolean) ((UIOutput)event.getSource()).getValue();
	SelectedCheckbox.put(a[2], result1.toString());
	
}
public void ajaxListener1(AjaxBehaviorEvent event)
{
	String result = ((UIOutput)event.getSource()).getClientId();//event.getComponent().getClass().getName();
	String[] a = result.split(":", 4);
	Boolean result1 = (Boolean) ((UIOutput)event.getSource()).getValue();
	SelectedCheckbox.put(a[2], result1.toString());	
	//System.out.print(SelectedCheckbox.toString());
}
public void StartAutoTest()
{
	FacesContext context = FacesContext.getCurrentInstance();
	String roleName = context.getExternalContext().getSessionMap().get("loggedInUserRoleID").toString();
	String userId =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
	String param =  context.getExternalContext().getRequestParameterMap().get("startparam").toString();
	//System.out.println("startparam "+param);
	
	TestConfigUserDao testConfigUserDao = new TestConfigUserDaoImpl();
	//System.out.println("SelectedCheckbox  " + SelectedCheckbox.toString());
	if((roleName.equals("superadmin")||roleName.equals("superadmin"))){
		assignListTestConfig=testConfigUserDao.assignListTestConfig();
	}else{
		String operator= context.getExternalContext().getSessionMap().get("loggedInOperatorID").toString();
		assignListTestConfig=testConfigUserDao.assignListTestConfigOperator(operator,userId);
	}
	 try {
		boolean status = false;
		ArrayList<String> uiid = new ArrayList<String>();
		if(param.equals("START")) status=true;else if(param.equals("STOP")) status=false;
		 for (String key : SelectedCheckbox.keySet()) {
			   //System.out.println("key: " + key + " value: " + SelectedCheckbox.get(key));
			   if(SelectedCheckbox.get(key).equals("true"))
				 {
				   selectedUserTostartAutotest.put(assignListTestConfig.get(Integer.parseInt(key)).getImei(), 
						 								assignListTestConfig.get(Integer.parseInt(key)).getUser_Name());
				 uiid.add(key);
				 }
			}
		 int i = 0,j=0;
		 //System.out.println("selectedUserTostartAutotest.size() "+selectedUserTostartAutotest.size());
		 String regids[] = new String[selectedUserTostartAutotest.size()];
		 for (String key : selectedUserTostartAutotest.keySet()) {
			   //System.out.println("key: " + key + " uiid: " + uiid.toString());
			   regids[i] = testConfigUserDao.getGCMregidbyUserIMEI(selectedUserTostartAutotest.get(key),key);
			   String response = sendGet(param, regids[i]);
			   if(response.equals("success"))
			   {
				   uiid.remove(j);
				   j--;
			   }else if(response.equals("SSLErrorFail"))
			   {
				   StartAutoTest();
			   }
			   else{
				   //System.out.println("on failure "+j);
				   FailedStartedAutotest.add(uiid.get(j));
				   
			   }
			   i++;j++;
			}
		 context.getExternalContext().getRequestMap().put("FailedStartedAutotest", uiid);
		 } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}
//HTTP GET request
private String sendGet(String param, String regid) throws Exception {
			FacesContext context = FacesContext.getCurrentInstance();
			System.out.println("server Name "+context.getExternalContext().getRequestServerName());
			String ServerName = context.getExternalContext().getRequestServerName();//;"52.12.57.237"
			param = ServerName+":"+param;
			String url = "http://"+ServerName+"/gcm_server_php/send_message.php?message="+param+"_TEST&regId="+regid;
			System.out.println("is it right?"+url);
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			String Status = null;
			// optional default is GET
			con.setRequestMethod("GET");

			//add request header
			con.setRequestProperty("User-Agent", USER_AGENT);

			//int responseCode = con.getResponseCode();
			//System.out.println("\nSending 'GET' request to URL : " + url);
			//System.out.println("Response Code : " + responseCode);
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			//inputLine = response.toString(); 
			//inputLine = ReportConstants.SSLErrorMsg;
			System.out.println("response:"+response.toString());
			if(response.toString().contains(ReportConstants.SSLErrorMsg))
			{
				Status = "SSLErrorFail";
			}
			else{
			JSONObject myJson = new JSONObject(response.toString());
			//System.out.println("myJson"+ myJson.getString("success"));
			in.close();
			if(myJson.getString("success").equals("1") && myJson.getString("failure").equals("0"))
			{
				Status = "success";
			}else if(myJson.getString("success").equals("0") && myJson.getString("failure").equals("1"))
			{
				Status = "fail";
			}
			}
			return Status;
		}
		
}
