package com.dao;

import java.util.List;
import java.util.Map;

import com.to.DataPingTestCase;
import com.to.FTPTestCase;
import com.to.MOTestCase;
import com.to.MTTestCase;
import com.to.TestConfig;
import com.to.UDPTestCase;
import com.to.VoipTestCase;
import com.to.WebPageTestCase;

public interface TestConfigDao {
	public Integer addTestCase(String testCaseXML,String testName,String userId);
	public Integer deleteTestConfigParam(int testConfigID);
	public Integer updateTestCase(String testCaseXML,String testName,String userId,String selectedTestConfigId);
	public List<TestConfig> getActiveTestConfig();
	public Map<String,String> getActiveTestConfigInMapForManager(String managerId);
	public Map<String,String> getActiveTestConfigInMap();
	public String getTestConfigNameById(String testConfigId);
	public TestConfig getTestConfigXML(String imei,String userName);
	public Map<String,String> getTestNamesInMap();
	public Map<String,String> getTestNamesInMapInStgDevice();
	public Map<String,String> getTestNamesInMapInVqtDevice();
	public Map<String,String> getTestNamesInMapInStgDevice(String userId);
	public boolean checkConfigurationAssigned(String selectedTestConfigId);
	public boolean deleteConfiguration(String selectedTestConfigId,String assigned);
	public MOTestCase getMOTestCaseDetails(String selectedTestConfigId);
	public MTTestCase getMTTestCaseDetails(String selectedTestConfigId);
	public FTPTestCase getFTPTestCaseDetails(String selectedTestConfigId);
	public DataPingTestCase getdataPingTestCaseDetails(String selectedTestConfigId);
	public UDPTestCase getUDPTestCaseDetails(String selectedTestConfigId);
	public WebPageTestCase getWebPageTestCaseDetails(String selectedTestConfigId);
	public VoipTestCase getVoipTestCaseDetails(String selectedTestConfigId);
	public Map<String, String> getFileNamesInMapInStgDevice(String selectedTestValue);
	public String getUpdateStatus(String selectedTestValue, String fileName);
	public String goToActivateDeactiveTestName(String selectedTestValue, String fileName);
	public Map<String, String> getFileNamesInFileHistory(String testCaseName);
	public Map<String, String> getTestNamesInFileHistory();
	public Boolean checkTestNameExist(String testConfigName,String UserId);
	public Map<String, String> getActiveTestConfigInMapForOprator(String OperatorId);
}
