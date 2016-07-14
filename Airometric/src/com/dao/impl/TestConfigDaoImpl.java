package com.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dao.TestConfigDao;
import com.enums.DataPingTestEnum;
import com.enums.FTPTestEnum;
import com.enums.MOTestEnum;
import com.enums.MTTestEnum;
import com.enums.UDPTestEnum;
import com.enums.VoipTestEnum;
import com.enums.WebPageTestEnum;
import com.model.DBUtil;
import java.sql.Statement;

import com.to.DataPingTestCase;
import com.to.FTPTestCase;
import com.to.MOTestCase;
import com.to.MTTestCase;
import com.to.TestConfig;
import com.to.UDPTestCase;
import com.to.VoipTestCase;
import com.to.WebPageTestCase;
import com.util.DateUtil;

public class TestConfigDaoImpl implements TestConfigDao {
	/**
	 * This method is used to add the testconfig
	 */
	public Integer addTestCase(String testCaseXML, String testName,
			String userId) {
		PreparedStatement pst = null;
		Connection conn = null;
		ResultSet rs = null;
		Integer testConfidID = null;
		try {
			conn = DBUtil.getConnection();
			String query = "INSERT INTO TEST_CONFIG(TEST_CONFIG_NAME,CONFIG_XML,ACTIVE,CREATED_DATE,MODIFIED_DATE,CREATED_BY,MODIFIED_BY) VALUES (?,?,?,?,?,?,?)";
			pst = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			pst.setString(1, testName);
			pst.setString(2, testCaseXML);
			pst.setInt(3, 1);
			String date = DateUtil.getCurrentDateTime();
			pst.setString(4, date);
			pst.setString(5, date);
			// TODO need to remove the hardcode userID,has to be replaced with
			// the userID logged in
			pst.setString(6, userId);
			pst.setString(7, userId);
			System.out.println("INSERT INTO TEST_CONFIG "+pst);
			pst.executeUpdate();
			rs = pst.getGeneratedKeys();
			rs.next();
			testConfidID = rs.getInt(1);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		return testConfidID;
	}

	public String getTestMarket(String testConfigID){
		String marketId = "";

		PreparedStatement pst = null;
		Connection conn = null;
		ResultSet rs = null;
		int rows = 0;
		String xml = "";
		try {
			conn = DBUtil.getConnection();
			String query = "SELECT CONFIG_XML  FROM  TEST_CONFIG WHERE TEST_CONFIG_ID=?";
			pst = conn.prepareStatement(query);
			pst.setString(1, testConfigID);
//			System.out.println("pst=========="+pst);
			 rs = pst.executeQuery();
			 while(rs.next()){
				 xml = rs.getString("CONFIG_XML");
			 }
				int startingPoint = xml.indexOf("<market>")+8;
				int endingPoint = xml.indexOf("</market>");
				marketId = xml.substring(startingPoint,endingPoint);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
	
		return marketId;
	}
	public Integer deleteTestConfigParam(int testConfigID) {
		PreparedStatement pst = null;
		Connection conn = null;
		ResultSet rs = null;
		int rows = 0;
		try {
			conn = DBUtil.getConnection();
			String query = "DELETE  FROM  TEST_CONFIG_PARAM WHERE TEST_CONFIG_ID=?";
			pst = conn.prepareStatement(query);
			pst.setInt(1, testConfigID);
			rows = pst.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		return rows;
	}

	public Integer updateTestCase(String testCaseXML, String testName,
			String userId, String selectedTestConfigId) {
		PreparedStatement pst = null;
		Connection conn = null;
		ResultSet rs = null;
		try {
			conn = DBUtil.getConnection();
			String query = "UPDATE  TEST_CONFIG SET TEST_CONFIG_NAME=?, "
					+ " CONFIG_XML=?,MODIFIED_DATE=?,MODIFIED_BY=? WHERE TEST_CONFIG_ID=?";
			pst = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			pst.setString(1, testName);
			pst.setString(2, testCaseXML);
			String date = DateUtil.getCurrentDateTime();
			pst.setString(3, date);
			pst.setString(4, userId);
			pst.setString(5, selectedTestConfigId);
			pst.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		return Integer.parseInt(selectedTestConfigId);
	}

	public List<TestConfig> getActiveTestConfig() {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		List<TestConfig> testConfigList = new ArrayList<TestConfig>();
		int status = 0;
		try {
			conn = DBUtil.getConnection();
			String query = "SELECT TEST_CONFIG_ID,TEST_CONFIG_NAME,ACTIVE,CREATED_DATE,MODIFIED_DATE FROM TEST_CONFIG WHERE ACTIVE=1";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				TestConfig testConfig = new TestConfig();
				testConfig.setTestConfigId(rs.getString("TEST_CONFIG_ID"));
				testConfig.setTestConfigName(rs.getString("TEST_CONFIG_NAME"));
				status = Integer.parseInt(rs.getString("ACTIVE"));
				if (status == 1) {
					testConfig.setStatus("ACTIVE");
				} else {
					testConfig.setStatus("INACTIVE");
				}
				testConfig.setCreated_Date(rs.getString("CREATED_DATE"));
				testConfigList.add(testConfig);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		return testConfigList;
	}

	public Map<String, String> getActiveTestConfigInMap() {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		Map<String, String> testConfigMap = new LinkedHashMap<String, String>();
		try {
			conn = DBUtil.getConnection();
			String query = "SELECT TEST_CONFIG_ID,TEST_CONFIG_NAME FROM TEST_CONFIG WHERE ACTIVE=1 ORDER BY TEST_CONFIG_NAME";
//System.out.println("query------------"+query);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			testConfigMap.put("--Select Config--", "0");
			while (rs.next()) {
				String testName = rs.getString("TEST_CONFIG_NAME");
				String testId = rs.getString("TEST_CONFIG_ID");
				testConfigMap.put(testName, testId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		return testConfigMap;
	}

	public String getTestConfigNameById(String testConfigId) {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		String testConfigName = null;
		;
		try {
			conn = DBUtil.getConnection();
			String query = "SELECT TEST_CONFIG_NAME FROM TEST_CONFIG WHERE TEST_CONFIG_ID='"
					+ testConfigId + "' AND ACTIVE=1";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				testConfigName = rs.getString("TEST_CONFIG_NAME");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		return testConfigName;
	}

	public TestConfig getTestConfigXML(String imei, String userName) {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		TestConfig testConfig = null;
		;
		try {
			conn = DBUtil.getConnection();
			// SELECT TC.TEST_CONFIG_NAME,TC.CONFIG_XML FROM TEST_CONFIG
			// TC,TEST_CONFIG_USER TCU,USER_DEVICE D WHERE TC.ACTIVE =1 AND
			// TCU.TEST_CONFIG_ID=TC.TEST_CONFIG_ID AND
			// TCU.DEVICE_ID=D.DEVICE_ID AND D.IMEI='357238041284469' AND
			// TCU.USER_ID IN (SELECT USER_ID FROM USERS WHERE
			// USER_NAME='kumar') ORDER BY TCU.CREATED_DATE DESC
			String query = "SELECT TC.TEST_CONFIG_NAME,TC.CONFIG_XML FROM TEST_CONFIG TC,TEST_CONFIG_USER TCU,USER_DEVICE D WHERE TC.ACTIVE =1 AND TCU.TEST_CONFIG_ID=TC.TEST_CONFIG_ID "
					+ "AND TCU.DEVICE_ID=D.DEVICE_ID AND D.IMEI='"
					+ imei
					+ "' AND TCU.USER_ID IN (SELECT USER_ID FROM USERS WHERE USER_NAME='"
					+ userName
					+ "' AND ACTIVE=1) ORDER BY TCU.CREATED_DATE DESC";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				testConfig = new TestConfig();
				testConfig.setTestConfigName(rs.getString("TEST_CONFIG_NAME"));
				testConfig.setTestConfigXml(rs.getString("CONFIG_XML"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		return testConfig;
	}

	public Map<String, String> getActiveTestConfigInMapForManager(String managerId) 
	{
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		Map<String, String> testConfigMap = new LinkedHashMap<String, String>();
		try {
			conn = DBUtil.getConnection();
			String query = "SELECT T.TEST_CONFIG_ID,T.TEST_CONFIG_NAME FROM TEST_CONFIG T,TEST_CONFIG_USER TU WHERE "
					+ "TU.TEST_CONFIG_ID = T.TEST_CONFIG_ID AND TU.USER_ID='0' AND T.ACTIVE='1'  AND T.CREATED_BY='"
					+ managerId
					+ "' AND TU.OPERATOR_ID IN "
					+ "(SELECT UO.OPERATOR_ID FROM USER_OPERATOR UO,USERS U ,OPERATOR O WHERE  UO.OPERATOR_ID = O.OPERATOR_ID AND "
					+ "UO.USER_ID = U.USER_ID AND U.USER_ID='"
					+ managerId
					+ "' ) ORDER BY T.TEST_CONFIG_NAME ";
//			System.out.println("query--------"+query);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			testConfigMap.put("--Select Config--", "0");
			while (rs.next()) {
				testConfigMap.put(rs.getString("TEST_CONFIG_NAME"), rs
						.getString("TEST_CONFIG_ID"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		return testConfigMap;
	}

	public Map<String, String> getTestNamesInMap() {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		Map<String, String> testNameMap = new LinkedHashMap<String, String>();
		try {
			conn = DBUtil.getConnection();
			// String query =
			// "SELECT TG.TEST_CONFIG_NAME FROM TEST_CONFIG,precalculated_tests PT WHERE  TG.TEST_CONFIG_NAME LIKE CONCAT(PT.TEST_NAME ,'-%') ORDER BY TEST_CONFIG_NAME ";
			String query = "select distinct test_name as TEST_CONFIG_NAME from precalculated_tests";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			testNameMap.put("--Select TestName--", "0");
			while (rs.next()) {
				testNameMap.put(rs.getString("TEST_CONFIG_NAME"), rs
						.getString("TEST_CONFIG_NAME"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		return testNameMap;
	}

	public Map<String, String> getTestNamesInMapInStgDevice() {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		Map<String, String> testNameMap = new LinkedHashMap<String, String>();
		String testName = null;
		try {
			conn = DBUtil.getConnection();
			// Modified to display the Test names which is having the data.
			/*
			 * String query =
			 * "SELECT TEST_CONFIG_NAME FROM TEST_CONFIG UNION ALL " +
			 * "SELECT DISTINCT TEST_NAME FROM STG_DEVICE_INFO ORDER BY TEST_CONFIG_NAME"
			 * ;
			 */
			String query = "SELECT DISTINCT TEST_NAME FROM STG_DEVICE_INFO   ORDER BY TEST_NAME ";
			//System.out.println("query----------" + query);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			testNameMap.put("--Select TestName--", "0");
			while (rs.next()) {
				if (rs.getString(1).contains("-")) {
					testName = rs.getString(1);
					String[] split = testName.split("-");
					testName = split[0];
				} else {
					testName = rs.getString(1);
				}

				testNameMap.put(testName, testName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		return testNameMap;
	}

	public Map<String, String> getPrecalculationTestNamesInMapInStgDevice() {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		Map<String, String> testNameMap = new LinkedHashMap<String, String>();
		String testName = null;
		try {
			conn = DBUtil.getConnection();
			// Modified to display the Test names which is having the data.
			/*
			 * String query =
			 * "SELECT TEST_CONFIG_NAME FROM TEST_CONFIG UNION ALL " +
			 * "SELECT DISTINCT TEST_NAME FROM STG_DEVICE_INFO ORDER BY TEST_CONFIG_NAME"
			 * ;
			 */
			/*
			 * String query =
			 * "SELECT DISTINCT DI.TEST_NAME FROM STG_DEVICE_INFO DI,precalculated_tests PT WHERE  DI.TEST_NAME  like CONCAT(PT.TEST_NAME ,'-%')  "
			 * + "	UNION "+
			 * "SELECT DISTINCT DI.TEST_NAME FROM STG_DEVICE_INFO DI,precalculated_tests PT WHERE  DI.TEST_NAME  like CONCAT(PT.TEST_NAME ,'%') ORDER BY TEST_NAME "
			 * ;;
			 */
			String query = "select distinct test_name as TEST_CONFIG_NAME from precalculated_tests";
			// System.out.println("query-----------"+query);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			testNameMap.put("--Select TestName--", "0");
			while (rs.next()) {
				if (rs.getString(1).contains("-")) {
					testName = rs.getString(1);
					String[] split = testName.split("-");
					testName = split[0];
				} else {
					testName = rs.getString(1);
				}

				testNameMap.put(testName, testName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		return testNameMap;
	}

	public static void main(String[] args) {
		TestConfigDao configDao = new TestConfigDaoImpl();
		// System.out.println("-------------------------"+configDao.getTestNamesInMapInStgDevice());
	}

	public Map<String, String> getTestNamesInMapInVqtDevice() {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		Map<String, String> testNameMap = new LinkedHashMap<String, String>();
		try {
			conn = DBUtil.getConnection();
			String query = "SELECT DISTINCT TEST_NAME FROM STG_VQT_RESULTS ";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			testNameMap.put("--Select TestName--", "0");
			while (rs.next()) {
				String testName = rs.getString(1);
				testNameMap.put(testName, testName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}

		return testNameMap;
	}

	public boolean checkConfigurationAssigned(String selectedTestConfigId) {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		boolean status = false;
		int count = 0;
		try {
			conn = DBUtil.getConnection();
			String query = "SELECT COUNT(*) FROM TEST_CONFIG_USER WHERE TEST_CONFIG_ID='"
					+ selectedTestConfigId + "'";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				count = Integer.parseInt(rs.getString(1));
				if (count > 1) {
					status = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		return status;
	}

	public boolean deleteConfiguration(String selectedTestConfigId,
			String assigned) {
		Statement stmt = null;
		PreparedStatement preparedStatement = null;
		PreparedStatement pst = null;
		Connection conn = null;
		ResultSet rs = null;
		boolean status = false;
		int count = 0;
		int rows = 0;
		int total = 0;
		String sql = null;
		String query = null;
		try {
			conn = DBUtil.getConnection();
			if (assigned.equals("true")) {
				String sqlQuery = "DELETE FROM TEST_CONFIG_PARAM WHERE TEST_CONFIG_ID='"
						+ selectedTestConfigId + "'";
				pst = conn.prepareStatement(sqlQuery);
				rows = pst.executeUpdate();
				if (rows > 0) {
					sql = "DELETE FROM TEST_CONFIG_USER WHERE TEST_CONFIG_ID='"
							+ selectedTestConfigId + "'";
					pst = conn.prepareStatement(sql);
					total = pst.executeUpdate();
					if (total > 0) {
						query = "DELETE FROM TEST_CONFIG WHERE TEST_CONFIG_ID='"
								+ selectedTestConfigId + "'";
						preparedStatement = conn.prepareStatement(query);
						count = preparedStatement.executeUpdate();
						if (count > 0) {
							status = true;
						}
					}
				}
			} else {
				String sqlQuery = "DELETE FROM TEST_CONFIG_PARAM WHERE TEST_CONFIG_ID='"
						+ selectedTestConfigId + "'";
				pst = conn.prepareStatement(sqlQuery);
				rows = pst.executeUpdate();
				if (rows > 0) {
					query = "DELETE FROM TEST_CONFIG WHERE TEST_CONFIG_ID='"
							+ selectedTestConfigId + "'";
					preparedStatement = conn.prepareStatement(query);
					count = preparedStatement.executeUpdate();
					status = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		return status;
	}

	public FTPTestCase getFTPTestCaseDetails(String selectedTestConfigId) {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		FTPTestCase ftpTestCase = new FTPTestCase();
		int testParamId = 0;
		String testParamValue = null;
		try {
			conn = DBUtil.getConnection();
			String query = "SELECT TEST_PARAM_ID ,TEST_PARAM_VALUE FROM TEST_CONFIG_PARAM "
					+ " WHERE TEST_CONFIG_ID ='"
					+ selectedTestConfigId
					+ "' AND ACTIVE='1' ";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				testParamId = rs.getInt(1);
				testParamValue = rs.getString(2);
				if (testParamId == FTPTestEnum.FTPSERVERURL.getTestParamID()) {
					ftpTestCase.setFtpServerURL(testParamValue);
				}
				if (testParamId == FTPTestEnum.FTPUSERNAME.getTestParamID()) {
					ftpTestCase.setFtpUsername(testParamValue);
				}
				if (testParamId == FTPTestEnum.FTPPASSWORD.getTestParamID()) {
					ftpTestCase.setFtpPassword(testParamValue);
				}
				if (testParamId == FTPTestEnum.NUMBEROFREPEATCYCLES
						.getTestParamID()) {
					ftpTestCase.setNoOfRepeatCycles(testParamValue);
				}
				if (testParamId == FTPTestEnum.FILEPATHTOUPLOAD
						.getTestParamID()) {
					ftpTestCase.setUploadFilePath(testParamValue);
				}
				if (testParamId == FTPTestEnum.FTPFILETODOWNLOAD
						.getTestParamID()) {
					ftpTestCase.setFileDownloadPath(testParamValue);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		return ftpTestCase;
	}

	public MOTestCase getMOTestCaseDetails(String selectedTestConfigId) {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		MOTestCase moTestCase = new MOTestCase();
		try {
			conn = DBUtil.getConnection();
			String query = "SELECT TEST_PARAM_ID,TEST_PARAM_VALUE FROM TEST_CONFIG_PARAM "
					+ " WHERE TEST_CONFIG_ID ='"
					+ selectedTestConfigId
					+ "' AND ACTIVE='1' ";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				if (rs.getInt(1) == MOTestEnum.PHONENUMBER.getTestParamID()) {
					moTestCase.setPhoneNumber(rs.getString(2));
				}
				if (rs.getInt(1) == MOTestEnum.CALLDURATION.getTestParamID()) {
					moTestCase.setCallDuration(rs.getString(2));
				}
				if (rs.getInt(1) == MOTestEnum.PAUSETIME.getTestParamID()) {
					moTestCase.setPauseTime(rs.getString(2));
				}
				if (rs.getInt(1) == MOTestEnum.TESTDURATION.getTestParamID()) {
					moTestCase.setTestDuration(rs.getString(2));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		return moTestCase;
	}

	public MTTestCase getMTTestCaseDetails(String selectedTestConfigId) {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		MTTestCase mtTestCase = new MTTestCase();
		int testParamId = 0;
		String testParamValue = null;
		try {
			conn = DBUtil.getConnection();
			String query = "SELECT TEST_PARAM_ID ,TEST_PARAM_VALUE FROM TEST_CONFIG_PARAM "
					+ " WHERE TEST_CONFIG_ID ='"
					+ selectedTestConfigId
					+ "' AND ACTIVE='1'ORDER BY TEST_PARAM_ID";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				testParamId = rs.getInt(1);
				testParamValue = rs.getString(2);
				if (testParamId == MTTestEnum.TESTDURATION.getTestParamID()) {
					mtTestCase.setTestDurationMt(testParamValue);
				}
				if (testParamId == MTTestEnum.CALLDURATION.getTestParamID()) {
					mtTestCase.setCallDurationMt(testParamValue);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		return mtTestCase;
	}

	public UDPTestCase getUDPTestCaseDetails(String selectedTestConfigId) {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		UDPTestCase udpTestCase = new UDPTestCase();
		int testParamId = 0;
		String testParamValue = null;
		try {
			conn = DBUtil.getConnection();
			String query = "SELECT TEST_PARAM_ID ,TEST_PARAM_VALUE FROM TEST_CONFIG_PARAM "
					+ " WHERE TEST_CONFIG_ID ='"
					+ selectedTestConfigId
					+ "' AND ACTIVE='1' ";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				testParamId = rs.getInt(1);
				testParamValue = rs.getString(2);
				if (testParamId == UDPTestEnum.UDPSERVERURL.getTestParamID()) {
					udpTestCase.setUdpServerURL(testParamValue);
				}
				if (testParamId == UDPTestEnum.NUMBEROFREPEATCYCLES
						.getTestParamID()) {
					udpTestCase.setNoOfRepeatCyclesUDP(testParamValue);
				}
				if (testParamId == UDPTestEnum.FILEPATHTOUPLOAD
						.getTestParamID()) {
					udpTestCase.setFilePathToUpload(testParamValue);
				}
				if (testParamId == UDPTestEnum.UDPSERVERPORT.getTestParamID()) {
					udpTestCase.setUdpServerPort(testParamValue);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		return udpTestCase;
	}

	public VoipTestCase getVoipTestCaseDetails(String selectedTestConfigId) {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		VoipTestCase voipTestCase = new VoipTestCase();
		int testParamId = 0;
		String testParamValue = null;
		try {
			conn = DBUtil.getConnection();
			String query = "SELECT TEST_PARAM_ID ,TEST_PARAM_VALUE FROM TEST_CONFIG_PARAM "
					+ " WHERE TEST_CONFIG_ID ='"
					+ selectedTestConfigId
					+ "' AND ACTIVE='1' ";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				testParamId = rs.getInt(1);
				testParamValue = rs.getString(2);
				if (testParamId == VoipTestEnum.TESTDURATION.getTestParamID()) {
					voipTestCase.setTestDurationVoip(testParamValue);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		return voipTestCase;
	}
	
	public String getExternalTestCaseDetails(String selectedTestConfigId) {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		String isExternal = "";
		try {
			conn = DBUtil.getConnection();
			String query = "SELECT TEST_PARAM_ID ,TEST_PARAM_VALUE FROM TEST_CONFIG_PARAM "
					+ " WHERE TEST_CONFIG_ID ='"
					+ selectedTestConfigId
					+ "' AND ACTIVE='1' ";
			stmt = conn.createStatement();
			//System.out.println("query-----------"+query);
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				if(rs.getString("TEST_PARAM_VALUE").equalsIgnoreCase("true")){
					isExternal = "checked";
				}
				else{
					isExternal = "";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		return isExternal;
	}

	public WebPageTestCase getWebPageTestCaseDetails(String selectedTestConfigId) {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		WebPageTestCase webPageTestCase = new WebPageTestCase();
		int testParamId = 0;
		String testParamValue = null;
		try {
			conn = DBUtil.getConnection();
			String query = "SELECT TEST_PARAM_ID ,TEST_PARAM_VALUE FROM TEST_CONFIG_PARAM "
					+ " WHERE TEST_CONFIG_ID ='"
					+ selectedTestConfigId
					+ "' AND ACTIVE='1' ";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				testParamId = rs.getInt(1);
				testParamValue = rs.getString(2);
				if (testParamId == WebPageTestEnum.WEBPAGEURL.getTestParamID()) {
					webPageTestCase.setWebPageURL(testParamValue);
				}
				if (testParamId == WebPageTestEnum.NUMBEROFREPEATCYCLESWEB
						.getTestParamID()) {
					webPageTestCase
							.setNumberofrepeatcyclesInWeb(testParamValue);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		return webPageTestCase;
	}

	public DataPingTestCase getdataPingTestCaseDetails(
			String selectedTestConfigId) {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		DataPingTestCase dataPingTestCase = new DataPingTestCase();
		int testParamId = 0;
		String testParamValue = null;
		try {
			conn = DBUtil.getConnection();
			String query = "SELECT TEST_PARAM_ID ,TEST_PARAM_VALUE FROM TEST_CONFIG_PARAM "
					+ " WHERE TEST_CONFIG_ID ='"
					+ selectedTestConfigId
					+ "' AND ACTIVE='1' ";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				testParamId = rs.getInt(1);
				testParamValue = rs.getString(2);
				if (testParamId == DataPingTestEnum.SERVERURLTOPING
						.getTestParamID()) {
					dataPingTestCase.setServerURL(testParamValue);
				}
				if (testParamId == DataPingTestEnum.NUMBEROFREPEATCYCLESPING
						.getTestParamID()) {
					dataPingTestCase.setNoOfRepeatCyclesPing(testParamValue);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		return dataPingTestCase;
	}

	public Map<String, String> getTestNamesInMapInStgDevice(String userId) {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		Map<String, String> testNameMap = new LinkedHashMap<String, String>();
		String testName = null;
		try {
			conn = DBUtil.getConnection();
			/*
			 * Stringsql=
			 * "SELECT TEST_CONFIG_NAME FROM TEST_CONFIG WHERE TEST_CONFIG.CREATED_BY='"
			 * +userId+"'UNION ALL " +
			 * " SELECT DISTINCT TEST_NAME FROM STG_DEVICE_INFO ,USERS ,USER_OPERATOR "
			 * +
			 * " WHERE STG_DEVICE_INFO.USER_NAME = USERS.USER_NAME AND USERS.USER_ID=USER_OPERATOR.USER_ID "
			 * + " AND  USER_OPERATOR.OPERATOR_ID='"+new
			 * UserDaoImpl().getOperator(userId)+"' AND USERS.USER_ID='"+userId+
			 * "' ORDER BY TEST_CONFIG_NAME ";
			 */

			/*
			 * Stringsql=
			 * "SELECT DISTINCT STG_DEVICE_INFO.TEST_NAME FROM STG_DEVICE_INFO ,USERS ,USER_OPERATOR "
			 * +
			 * "WHERE STG_DEVICE_INFO.USER_NAME IN(SELECT U.USER_NAME FROM USERS U "
			 * +"WHERE U.CREATED_BY='"+userId+
			 * "'  UNION ALL  SELECT UD.USER_NAME FROM USERS UD WHERE UD.USER_ID='"
			 * +userId+"' )  " +
			 * "AND USERS.USER_ID=USER_OPERATOR.USER_ID AND  USER_OPERATOR.OPERATOR_ID='"
			 * +new UserDaoImpl().getOperator(userId)+"' " +
			 * "  ORDER BY TEST_NAME ";
			 */

			String sql = "SELECT DISTINCT STG_DEVICE_INFO.TEST_NAME FROM STG_DEVICE_INFO   ," +
					" (SELECT USER_ID,USER_NAME FROM USERS WHERE USER_ID = '"
					+ userId
					+ "') USERS "
					+ "WHERE   STG_DEVICE_INFO.USER_NAME = USERS.USER_NAME    ORDER BY TEST_NAME ";
			//System.out.println("sql------" + sql);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			testNameMap.put("--Select TestName--", "0");
			while (rs.next()) {
				// String testName = rs.getString(1);
				if (rs.getString(1).contains("-")) {
					testName = rs.getString(1);
					String[] split = testName.split("-");
					testName = split[0];
				} else {
					testName = rs.getString(1);
				}
				testNameMap.put(testName, testName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				};
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		return testNameMap;
	}

	public Map<String, String> getPreCalculationTestNamesInMapInStgDevice(
			String userId) {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		Map<String, String> testNameMap = new LinkedHashMap<String, String>();
		String testName = null;
		try {
			conn = DBUtil.getConnection();
			/*
			 * Stringsql=
			 * "SELECT TEST_CONFIG_NAME FROM TEST_CONFIG WHERE TEST_CONFIG.CREATED_BY='"
			 * +userId+"'UNION ALL " +
			 * " SELECT DISTINCT TEST_NAME FROM STG_DEVICE_INFO ,USERS ,USER_OPERATOR "
			 * +
			 * " WHERE STG_DEVICE_INFO.USER_NAME = USERS.USER_NAME AND USERS.USER_ID=USER_OPERATOR.USER_ID "
			 * + " AND  USER_OPERATOR.OPERATOR_ID='"+new
			 * UserDaoImpl().getOperator(userId)+"' AND USERS.USER_ID='"+userId+
			 * "' ORDER BY TEST_CONFIG_NAME ";
			 */

			String sql = "SELECT DISTINCT STG_DEVICE_INFO.TEST_NAME FROM STG_DEVICE_INFO ,USERS ,USER_OPERATOR,precalculated_tests "
					+ "WHERE STG_DEVICE_INFO.USER_NAME IN(SELECT U.USER_NAME FROM USERS U "
					+ "WHERE U.CREATED_BY='"
					+ userId
					+ "'  UNION ALL  SELECT UD.USER_NAME FROM USERS UD WHERE UD.USER_ID='"
					+ userId
					+ "' )  "
					+ "AND USERS.USER_ID=USER_OPERATOR.USER_ID AND  USER_OPERATOR.OPERATOR_ID='"
					+ new UserDaoImpl().getOperator(userId)
					+ "' "
					+ "AND precalculated_tests.TEST_NAME = STG_DEVICE_INFO.TEST_NAME  ORDER BY TEST_NAME ";
			// System.out.println("sql---------"+sql);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			testNameMap.put("--Select TestName--", "0");
			while (rs.next()) {
				// String testName = rs.getString(1);
				if (rs.getString(1).contains("-")) {
					testName = rs.getString(1);
					String[] split = testName.split("-");
					testName = split[0];
				} else {
					testName = rs.getString(1);
				}
				testNameMap.put(testName, testName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		return testNameMap;
	}
	
	public Map<String, String> getnTestNamesInMapInHierarchy(
			String userIds) {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		Map<String, String> testNameMap = new LinkedHashMap<String, String>();
		String testName = null;
		try {
			conn = DBUtil.openConn();

			String sql = "SELECT TEST_NAME FROM user_test WHERE USER_ID IN("+userIds+")"+
			" UNION "+
			"SELECT TEST_NAME FROM user_transfered_test WHERE USER_ID IN("+userIds+")";
			 //System.out.println("sql---------"+sql);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			testNameMap.put("--Select TestName--", "0");
			while (rs.next()) {
				// String testName = rs.getString(1);
				if (rs.getString(1).contains("-")) {
					testName = rs.getString(1);
					String[] split = testName.split("-");
					testName = split[0];
				} else {
					testName = rs.getString(1);
				}
				testNameMap.put(testName, testName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		DBUtil.closeConn();
		}
		return testNameMap;
	}


	public Map<String, String> getPreCalculationTestNamesInMapInStgDevice(
			String userId, String userName) {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		Map<String, String> testNameMap = new LinkedHashMap<String, String>();
		String testName = null;
		try {
			conn = DBUtil.getConnection();
			/*
			 * Stringsql=
			 * "SELECT TEST_CONFIG_NAME FROM TEST_CONFIG WHERE TEST_CONFIG.CREATED_BY='"
			 * +userId+"'UNION ALL " +
			 * " SELECT DISTINCT TEST_NAME FROM STG_DEVICE_INFO ,USERS ,USER_OPERATOR "
			 * +
			 * " WHERE STG_DEVICE_INFO.USER_NAME = USERS.USER_NAME AND USERS.USER_ID=USER_OPERATOR.USER_ID "
			 * + " AND  USER_OPERATOR.OPERATOR_ID='"+new
			 * UserDaoImpl().getOperator(userId)+"' AND USERS.USER_ID='"+userId+
			 * "' ORDER BY TEST_CONFIG_NAME ";
			 */

			String sql = "SELECT DISTINCT STG_DEVICE_INFO.TEST_NAME FROM STG_DEVICE_INFO ,USERS ,USER_OPERATOR,precalculated_tests "
					+ "WHERE STG_DEVICE_INFO.USER_NAME IN('"
					+ userName
					+ "')  "
					+ "AND USERS.USER_ID=USER_OPERATOR.USER_ID AND  USER_OPERATOR.OPERATOR_ID='"
					+ new UserDaoImpl().getOperator(userId)
					+ "' "
					+ "AND   CONCAT(STG_DEVICE_INFO.TEST_NAME,'-') LIKE CONCAT(precalculated_tests.TEST_NAME,'-%')  ORDER BY TEST_NAME ";
			// System.out.println("sql---------"+sql);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			testNameMap.put("--Select TestName--", "0");
			while (rs.next()) {
				// String testName = rs.getString(1);
				if (rs.getString(1).contains("-")) {
					testName = rs.getString(1);
					String[] split = testName.split("-");
					testName = split[0];
				} else {
					testName = rs.getString(1);
				}
				testNameMap.put(testName, testName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		return testNameMap;
	}

	public Map<String, String> getTestNamesInMapInStgDevice(String userId,
			String userName) {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		Map<String, String> testNameMap = new LinkedHashMap<String, String>();
		String testName = null;
		try {
			conn = DBUtil.getConnection();
			/*
			 * Stringsql=
			 * "SELECT TEST_CONFIG_NAME FROM TEST_CONFIG WHERE TEST_CONFIG.CREATED_BY='"
			 * +userId+"'UNION ALL " +
			 * " SELECT DISTINCT TEST_NAME FROM STG_DEVICE_INFO ,USERS ,USER_OPERATOR "
			 * +
			 * " WHERE STG_DEVICE_INFO.USER_NAME = USERS.USER_NAME AND USERS.USER_ID=USER_OPERATOR.USER_ID "
			 * + " AND  USER_OPERATOR.OPERATOR_ID='"+new
			 * UserDaoImpl().getOperator(userId)+"' AND USERS.USER_ID='"+userId+
			 * "' ORDER BY TEST_CONFIG_NAME ";
			 */

			String sql = "SELECT DISTINCT STG_DEVICE_INFO.TEST_NAME FROM STG_DEVICE_INFO ,USERS ,USER_OPERATOR "
					+ "WHERE STG_DEVICE_INFO.USER_NAME IN('"
					+ userName
					+ "')  "
					+ "AND USERS.USER_ID=USER_OPERATOR.USER_ID AND  USER_OPERATOR.OPERATOR_ID='"
					+ new UserDaoImpl().getOperator(userId)
					+ "' "
					+ "  ORDER BY TEST_NAME ";
			// System.out.println("sql------"+sql);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			testNameMap.put("--Select TestName--", "0");
			while (rs.next()) {
				// String testName = rs.getString(1);
				if (rs.getString(1).contains("-")) {
					testName = rs.getString(1);
					String[] split = testName.split("-");
					testName = split[0];
				} else {
					testName = rs.getString(1);
				}
				testNameMap.put(testName, testName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		return testNameMap;
	}

	public Map<String, String> getFileNamesInMapInStgDevice(
			String selectedTestValue) {
		// System.out.println("inside getFileNamesInMapInStgDevice:" +
		// selectedTestValue);
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		Map<String, String> fileNameMap = new LinkedHashMap<String, String>();
		String fileName = null;
		String timeStamp = null;
		String fileNameWithTimeStamp = null;
		try {
			conn = DBUtil.getConnection();
			String query = "SELECT DISTINCT TEST_NAME, TIME_STAMP_FOREACH_SAMPLE FROM STG_DEVICE_INFO ORDER BY TEST_NAME ";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			if (!selectedTestValue.equals("0")) {
				fileNameMap.put("--Select FileName--", "0");
			}
			while (rs.next()) {
				String tempFileName = rs.getString(1);
				if (tempFileName.contains("-")) {
					if (rs.getString(1).split("-")[0].equals(selectedTestValue)) {
						fileName = rs.getString(1);
						timeStamp = rs.getString(2);
						fileNameWithTimeStamp = fileName + "::" + timeStamp;
						fileNameMap.put(fileNameWithTimeStamp,
								fileNameWithTimeStamp);
					}

				} else {
					if (rs.getString(1).equals(selectedTestValue)) {
						fileName = rs.getString(1);
						timeStamp = rs.getString(2);
						fileNameWithTimeStamp = fileName + "::" + timeStamp;
						fileNameMap.put(fileNameWithTimeStamp,
								fileNameWithTimeStamp);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		return fileNameMap;
	}

	public String getUpdateStatus(String selectedTestValue, String fileName) {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		String updatedStatus = null;
		String testName = "";
		String timeStamp = "";
		try {
			conn = DBUtil.getConnection();
			/*
			 * if(!selectedTestValue.equals("0")){ testName =
			 * selectedTestValue.split("::")[0]; timeStamp =
			 * selectedTestValue.split("::")[1]; }
			 */

			String query = "SELECT DISTINCT TEST_NAME, ACTIVE FROM FILE_HISTORY WHERE TEST_NAME ='"
					+ selectedTestValue
					+ "' "
					+ "AND FILE_NAME='"
					+ fileName
					+ "'";
			// System.out.println("getUpdateStatus:"+ query);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				updatedStatus = rs.getString(1);
				if (rs.getString("ACTIVE").equals("1")) {
					updatedStatus = "Deactivate";
				} else {
					updatedStatus = "Activate";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		return updatedStatus;
	}

	public String goToActivateDeactiveTestName(String selectedTestValue,
			String fileName) {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		Statement st1 = null;
		int active = 1;
		String status = "failure";
		String testName = "";
		String timeStamp = "";
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			/*
			 * if(null != fileName){ if(!fileName.equals("0")){ testName =
			 * fileName.split("::")[0]; timeStamp = fileName.split("::")[1]; } }
			 */
			String selectQuery = "SELECT DISTINCT TEST_NAME,ACTIVE "
					+ "FROM FILE_HISTORY WHERE TEST_NAME='" + selectedTestValue
					+ "' " + "AND FILE_NAME='" + fileName + "'";
			// System.out.println("selectQuery query :"+selectQuery);
			rs = st.executeQuery(selectQuery);

			while (rs.next()) {
				if (rs.getInt(2) == 1) {
					active = 0;
				} else {
					active = 1;
				}
			}

			String update = "UPDATE FILE_HISTORY SET ACTIVE=" + active
					+ " WHERE TEST_NAME='" + selectedTestValue + "' "
					+ "AND FILE_NAME='" + fileName + "'";
			// System.out.println("update query :"+update);
			st1 = conn.createStatement();
			st1.executeUpdate(update);
			status = "success";

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}

		return status;
	}

	public Map<String, String> getFileNamesInFileHistory(
			String selectedTestValue) {
		// System.out.println("inside getFileNamesInFileHistory:" +
		// selectedTestValue);
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		Map<String, String> fileNameMap = new LinkedHashMap<String, String>();
		String fileName = null;
		try {
			conn = DBUtil.getConnection();
			String query = "SELECT DISTINCT TEST_NAME, FILE_NAME, ACTIVE FROM FILE_HISTORY WHERE TEST_NAME='"
					+ selectedTestValue + "'";
			// System.out.println("query :"+query);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			if (!selectedTestValue.equals("0")) {
				fileNameMap.put("--Select FileName--", "0");
			}
			while (rs.next()) {
				fileName = rs.getString(2);
				fileNameMap.put(fileName, fileName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		return fileNameMap;
	}

	public Map<String, String> getTestNamesInFileHistory() {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		Map<String, String> testNameMap = new LinkedHashMap<String, String>();
		String testName = null;
		try {
			conn = DBUtil.getConnection();
			String query = "SELECT DISTINCT TEST_NAME FROM FILE_HISTORY ORDER BY TEST_NAME ";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			testNameMap.put("--Select TestName--", "0");
			while (rs.next()) {
				/*
				 * if(rs.getString(1).contains("-")){ testName =
				 * rs.getString(1); String[] split = testName.split("-");
				 * testName=split[0]; }else{
				 */
				testName = rs.getString(1);
				// }

				testNameMap.put(testName, testName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		return testNameMap;
	}
	//by ankit on 5/11/15
	public Boolean checkTestNameExist(String testConfigName,String OperatorID){
		PreparedStatement pst = null;
		Connection conn = null;
		ResultSet rs = null;
		int rows = 0;
		Boolean TestNameExist = false;
		try {
			conn = DBUtil.getConnection();
			String query = "SELECT * FROM TEST_CONFIG TG,TEST_CONFIG_USER TGU WHERE TG.TEST_CONFIG_NAME=? AND TGU.OPERATOR_ID=?";
			pst = conn.prepareStatement(query);
			pst.setString(1, testConfigName);
			pst.setString(2, OperatorID);
//			System.out.println("pst=========="+pst);
			rs = pst.executeQuery();
			rs.last();
			rows = rs.getRow();
			 if(rows>0){
				 TestNameExist = true;
				}
			 System.out.println("rows=========="+rows+"   "+TestNameExist);	
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
	
		return TestNameExist;
	}
	//added by ankit
	public Map<String, String> getActiveTestConfigInMapForOprator(
			String OperatorID) {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		Map<String, String> testConfigMap = new LinkedHashMap<String, String>();
		try {
			conn = DBUtil.getConnection();
			String query = "SELECT T.TEST_CONFIG_ID,T.TEST_CONFIG_NAME FROM TEST_CONFIG T,TEST_CONFIG_USER TU WHERE "
					+ "TU.TEST_CONFIG_ID = T.TEST_CONFIG_ID  AND T.ACTIVE='1'  AND TU.OPERATOR_ID='"//AND TU.USER_ID='0'
					+ OperatorID
					+ "'GROUP BY T.TEST_CONFIG_ID ORDER BY T.TEST_CONFIG_NAME ";
			System.out.println("query--------"+query);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			testConfigMap.put("--Select Config--", "0");
			while (rs.next()) {
				testConfigMap.put(rs.getString("TEST_CONFIG_NAME"), rs
						.getString("TEST_CONFIG_ID"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		return testConfigMap;
	}
	
}
