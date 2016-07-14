package com.dao.impl;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;

import antlr.Parser;

import com.constants.ReportConstants;
import com.constants.Roles;
import com.dao.ReportDao;
import com.model.DBUtil;
import com.report.to.SaveConfigTo;
import com.to.CommentsTo;
import com.to.ConfigBean;
import com.to.DeviceInfoTO;
import com.to.DeviceTo;
import com.to.FileHistoryTO;
import com.to.ReportBean;
import com.to.STGDevice;
import com.to.STGNetTest;
import com.util.DateUtil;
import com.PropertyFileReader;
import com.dao.impl.ReportDaoImpl;

import jxl.Sheet;
import jxl.Workbook;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReportDaoImpl implements ReportDao {
	final static Logger logger = Logger.getLogger(ReportDaoImpl.class);
	static HashMap<String, String> propertiesFiledata = PropertyFileReader
			.getProperties();
	private final static String THROUGHPUT = propertiesFiledata
			.get("THROUGHPUT");
	private final static String FREQUENCY_FILE_PATH = propertiesFiledata.get("FREQUENCY_FILE_PATH");

	public List<STGDevice> getTestNameMapDetailsResults(String testCaseName,
			String marketId) {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		List<STGDevice> listUserData = new ArrayList<STGDevice>();
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String stgQuery = " SELECT * FROM STG_VQT_RESULTS WHERE TEST_NAME='"
					+ testCaseName
					+ "' "
					+ "AND MARKET_ID='"
					+ marketId
					+ "' ORDER BY VQuadTimestamp ";
			rs = st.executeQuery(stgQuery);
			while (rs.next()) {
				STGDevice device = new STGDevice();
				device.setVQuadTimestamp(rs.getString("VQuadTimestamp"));
				device.setCallTimestamp(rs.getString("VQuadTimestamp"));
				device.setVQuadLocation(rs.getString("VQuadLocation"));
				device.setVQuadPhoneID(rs.getString("DEVICE_MODEL"));
				device.setVQuadLatLong(rs.getString("VQuadLatLong"));
				device.setDegradedFilename(rs.getString("DegradedFilename"));
				device.setRating(rs.getString("Rating"));
				device.setPESQ(rs.getString("PESQ"));
				device.setPESQLQ(rs.getString("PESQLQ"));
				device.setPESQLQO(rs.getString("PESQLQO"));
				device.setPESQWB(rs.getString("PESQWB"));
				device.setPSQMPLUS(rs.getString("PSQMPLUS"));
				device.setPESQAverageOffset(rs.getString("PESQAverageOffset"));
				device.setPESQMaxOffset(rs.getString("PESQMaxOffset"));
				device.setPESQMinOffset(rs.getString("PESQMinOffset"));
				device.setNumberAllClipping(rs.getString("NumberAllClipping"));
				device.setDurationALLClipping(rs
						.getString("DurationALLClipping"));
				device.setMeanDurationALLClipping(rs
						.getString("MeanDurationALLClipping"));
				device.setDurationHangover(rs.getString("DurationHangover"));
				device.setNumberOfHangover(rs.getString("NumberOfHangover"));
				device.setAverageJitter(rs.getString("AverageJitter"));
				device.setVQuadCallID(rs.getString("VQuadCallID"));
				device.setTestName(rs.getString("TEST_NAME"));
				device.setLattitude(rs.getDouble("GEOLOCATION_LATITUDE"));
				device.setLongitude(rs.getDouble("GEOLOCATION_LANGITUDE"));
				listUserData.add(device);
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			}
		}
		return listUserData;
	}

	public List<STGDevice> getTestNameMapDetailsResultsForVQT(
			String testCaseName, String marketId, List<String> vqtlist) {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		List<STGDevice> listUserData = new ArrayList<STGDevice>();
		PreparedStatement pst = null;
		SimpleDateFormat newSdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
		SimpleDateFormat format = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SSS");
		int resultCount = 0;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String stgQuery = " SELECT * FROM stg_vqt_results WHERE TEST_NAME='"
					+ testCaseName
					+ "' "
					+ "AND MARKET_ID='"
					+ marketId
					+ "' and VQuadTimestamp != 'Empty' ORDER BY VQuadTimestamp ";
			 //logger.error("stgQuery------------------"+stgQuery);
			rs = st.executeQuery(stgQuery);
			while (rs.next()) {
				resultCount++;
				STGDevice device = new STGDevice();
				String vquadCallId = "";
				device.setVQuadTimestamp(rs.getString("VQuadTimestamp"));
				device.setCallTimestamp(rs.getString("VQuadTimestamp"));
				device.setVQuadLocation(rs.getString("VQuadLocation"));
				device.setVQuadPhoneID(rs.getString("DEVICE_MODEL"));
				device.setVQuadLatLong(rs.getString("VQuadLatLong"));
				device.setDegradedFilename(rs.getString("DegradedFilename"));
				device.setRating(rs.getString("Rating"));
				device.setPESQ(rs.getString("PESQ"));
				device.setPESQLQ(rs.getString("PESQLQ"));
				device.setPESQLQO(rs.getString("PESQLQO"));
				device.setPESQWB(rs.getString("PESQWB"));
				device.setPSQMPLUS(rs.getString("PSQMPLUS"));
				device.setPESQAverageOffset(rs.getString("PESQAverageOffset"));
				device.setPESQMaxOffset(rs.getString("PESQMaxOffset"));
				device.setPESQMinOffset(rs.getString("PESQMinOffset"));
				device.setNumberAllClipping(rs.getString("NumberAllClipping"));
				device.setDurationALLClipping(rs
						.getString("DurationALLClipping"));
				device.setMeanDurationALLClipping(rs
						.getString("MeanDurationALLClipping"));
				device.setDurationHangover(rs.getString("DurationHangover"));
				device.setNumberOfHangover(rs.getString("NumberOfHangover"));
				device.setAverageJitter(rs.getString("AverageJitter"));
				vquadCallId = rs.getString("VQuadCallID");
				device.setVQuadCallID(vquadCallId);
				if (vquadCallId.contains("I_")) {
					device.setChartType("uplink");
				} else {
					device.setChartType("downlink");
				}
				device.setTestName(rs.getString("TEST_NAME"));
				device.setLattitude(rs.getDouble("GEOLOCATION_LATITUDE"));
				device.setLongitude(rs.getDouble("GEOLOCATION_LANGITUDE"));
				device.setFileSource("pesq");
				listUserData.add(device);
			}
			// logger.error("listUserData----"+listUserData.size());
			if (resultCount == 0) {
				listUserData = getTestNameMapDetailsResultsForVQTPolqa(
						testCaseName, marketId, vqtlist);
			}
			// logger.error("listUserData---after-"+listUserData.size());
		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			}
		}
		return listUserData;
	}

	public List<STGDevice> getTestNameMapDetailsResultsForVQTPolqa(
			String testCaseName, String marketId, List<String> vqtlist) {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		List<STGDevice> listUserData = new ArrayList<STGDevice>();
		PreparedStatement pst = null;
		DateFormat  format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//		SimpleDateFormat format = new SimpleDateFormat(
//				"yyyy-MM-dd HH:mm:ss.SSS");
		String d1 = null;
		Date vQuadTimeStamp = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String stgQuery = " SELECT *,(Speech_Level_Diff/SNR_Diff) AS SNR FROM stg_polqa_results WHERE TEST_NAME='"
					+ testCaseName
					+ "' "
					+ "AND MARKET_ID='"
					+ marketId
					+ "' and VQuad_Timestamp != 'Empty' ORDER BY VQuad_Timestamp";
//			 logger.error("stgQuery------------------"+stgQuery);
			rs = st.executeQuery(stgQuery);
			while (rs.next()) {
				STGDevice device = new STGDevice();
//				vQuadTimeStamp = new Date(rs.getString("VQuad_Timestamp"));
				
//				d1 = format.format(vQuadTimeStamp);
				/*New code added by Sheshadri based on Customer Requirements on 04-03-15*/
//				vQuadTimeStamp = newSdf.parse(rs.getString("VQuad_Timestamp"));
//				device.setCallTimestamp((format.format(d1)));
//				device.setVQuadTimestamp((format.format(vQuadTimeStamp)));
				device.setCallTimestamp(rs.getString("VQuad_Timestamp"));
				device.setVQuadTimestamp(rs.getString("VQuad_Timestamp"));
				
				// logger.error("convertDate(format.format(d1)+---testing---"+(format.format(d1)));
				device.setVQuadLocation(rs.getString("VQuad_Location"));
				device.setVQuadPhoneID(rs.getString("DEVICE_MODEL"));
				device.setVQuadLatLong(rs.getString("GPS_Position"));
				device.setDegradedFilename("");
				device.setRating(rs.getString("Pass_Or_Fail"));
				device.setPolqa(rs.getString("POLQA_Score"));
				device.setPESQ(rs.getString("POLQA_Score"));
				device.setPESQLQ(rs.getString("EModel_Polqa"));
				device.setEmodel(rs.getString("EModel_Polqa"));
				device.setSnr(rs.getString("SNR"));
				device.setPESQLQO("");
				device.setPESQWB("");
				device.setPSQMPLUS("");
				device.setPESQAverageOffset("");
				device.setPESQMaxOffset("");
				device.setPESQMinOffset("");
				device.setNumberAllClipping("");
				device.setDurationALLClipping("");
				device.setMeanDurationALLClipping("");
				device.setDurationHangover("");
				device.setNumberOfHangover("");
				device.setAverageJitter(rs.getString("Jitter_Ave"));

				String vquadCallId = rs.getString("VQuad_CallID");
				device.setVQuadCallID(vquadCallId);
				if (vquadCallId.contains("I_")) {
					device.setChartType("uplink");
				} else {
					device.setChartType("downlink");
				}
				device.setFileSource("polqa");
				device.setTestName(rs.getString("TEST_NAME"));
				device.setLattitude(rs.getDouble("GEOLOCATION_LATITUDE"));
				device.setLongitude(rs.getDouble("GEOLOCATION_LANGITUDE"));
				listUserData.add(device);
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			}
		}
		return listUserData;
	}

	public List<String> getMinandMaxTimestampforVQT(String testCaseName,
			String marketId) {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		List<String> listUserData = new ArrayList<String>();
		PreparedStatement pst = null;
		SimpleDateFormat newSdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		SimpleDateFormat format = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SSS");
		Date d1 = null;
		Date d2 = null;
		int resultCount = 0;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String stgQuery = " SELECT MIN(VQuadTimestamp),MAX(VQuadTimestamp) FROM STG_VQT_RESULTS WHERE TEST_NAME='"
					+ testCaseName
					+ "' "
					+ "AND MARKET_ID IN("
					+ marketId
					+ ") and VQuadTimestamp != 'Empty' AND VQuadTimestamp IS NOT null ORDER BY VQuadTimestamp ";
			rs = st.executeQuery(stgQuery);
			//logger.error("stgQuery------" + stgQuery);
			while (rs.next()) {
				//logger.error(newSdf + "--@@------befre parse---------"
//						+ rs.getString(1));
				if((rs.getString(1)!=null) && (rs.getString(2)!=null))
				{
						d1 = newSdf.parse(rs.getString(1));
						d2 = newSdf.parse(rs.getString(2));
					if (null != d1) {
						listUserData.add((format.format(d1)));
						listUserData.add((format.format(d2)));
					}
				}

			}
			if (listUserData.size() == 0) {
				listUserData = getMinandMaxTimestampforVQTPolqa(testCaseName,
						marketId);
			}

		} catch (Exception e) {
			listUserData = getMinandMaxTimestampforVQTPolqa(testCaseName,
					marketId);
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());

			}
		}
		return listUserData;
	}

	public List<String> getMinandMaxTimestampforVQTPolqa(String testCaseName,
			String marketId) {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		List<String> listUserData = new ArrayList<String>();
		PreparedStatement pst = null;
		SimpleDateFormat newSdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		SimpleDateFormat format = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SSS");
		Date d1 = null;
		Date d2 = null;
		int resultCount = 0;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String stgQuery = " SELECT MIN(VQuad_Timestamp),MAX(VQuad_Timestamp) FROM stg_polqa_results WHERE TEST_NAME='"
					+ testCaseName
					+ "' "
					+ "AND MARKET_ID IN("
					+ marketId
					+ ") and VQuad_Timestamp != 'Empty' ORDER BY VQuad_Timestamp ";
			//logger.error("stgQuery--------" + stgQuery);
			rs = st.executeQuery(stgQuery);

			while (rs.next()) {
				resultCount++;
				// logger.error("befre parse---------"+rs.getString(1));
				if(rs.getString(1)!=null && rs.getString(2)!=null)
				{
					d1 = newSdf.parse(rs.getString(1));
					d2 = newSdf.parse(rs.getString(2));
					listUserData.add((format.format(d1)));
					listUserData.add((format.format(d2)));
				}
				
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			}
		}
		return listUserData;
	}

	public List<String> getMinandMaxTimestampforNetCall(String testCaseName,
			String marketId) {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		List<String> listUserData = new ArrayList<String>();
		PreparedStatement pst = null;
		SimpleDateFormat newSdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
		SimpleDateFormat format = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SSS");
		Date d1 = null;
		Date d2 = null;

		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();

			String stgQuery = " SELECT MIN(ST.CallTimestamp),MAX(ST.CallTimestamp) FROM STG_NET_RESULTS ST,stg_callevent_results SC "
					+ "WHERE SC.TEST_NAME='"
					+ testCaseName
					+ "' AND SC.MARKET_ID IN("
					+ marketId
					+ ")  AND SC.MARKET_ID=ST.MARKET_ID AND SC.TEST_NAME=ST.TEST_NAME  "
					+ "AND SC.Call_Timestamp=ST.CallTimestamp and CallTimestamp != 'Empty' ORDER BY ST.CallTimestamp";

			rs = st.executeQuery(stgQuery);
			logger.error("stgQuery-------------"+stgQuery);
			while (rs.next()) {
				d1 = newSdf.parse(rs.getString(1));
				d2 = newSdf.parse(rs.getString(2));
				listUserData.add((format.format(d1)));
				listUserData.add((format.format(d2)));
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			}
		}
		return listUserData;
	}

	public String convertDate(String date) {
		SimpleDateFormat inFormat = new SimpleDateFormat(
				"yyyy-MM-dd hh:mm:ss.SSS aa");
		SimpleDateFormat outFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SSS");
		String time24 = null;
		;
		try {
			time24 = outFormat.format(inFormat.parse(date));
		} catch (ParseException e) {
			logger.error(e.getMessage());
		}
		return time24;
	}

	public List<STGDevice> getTestNameMapMarketDetailsResults(
			String testCaseName, String Market) {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		List<STGDevice> listUserData = new ArrayList<STGDevice>();
		PreparedStatement pst = null;

		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String stgQuery = " SELECT * FROM STG_VQT_RESULTS WHERE TEST_NAME='"
					+ testCaseName
					+ "' AND MARKET_ID IN("
					+ Market
					+ ") ORDER BY VQuad_Timestamp ";
			rs = st.executeQuery(stgQuery);
			while (rs.next()) {
				STGDevice device = new STGDevice();
				device.setVQuadTimestamp(rs.getString("VQuadTimestamp"));
				device.setCallTimestamp(rs.getString("VQuadTimestamp"));
				device.setVQuadLocation(rs.getString("VQuadLocation"));
				device.setVQuadPhoneID(rs.getString("DEVICE_MODEL"));
				device.setVQuadLatLong(rs.getString("VQuadLatLong"));
				device.setDegradedFilename(rs.getString("DegradedFilename"));
				device.setRating(rs.getString("Rating"));
				device.setPESQ(rs.getString("PESQ"));
				device.setPESQLQ(rs.getString("PESQLQ"));
				device.setPESQLQO(rs.getString("PESQLQO"));
				device.setPESQWB(rs.getString("PESQWB"));
				device.setPSQMPLUS(rs.getString("PSQMPLUS"));
				device.setPESQAverageOffset(rs.getString("PESQAverageOffset"));
				device.setPESQMaxOffset(rs.getString("PESQMaxOffset"));
				device.setPESQMinOffset(rs.getString("PESQMinOffset"));
				device.setNumberAllClipping(rs.getString("NumberAllClipping"));
				device.setDurationALLClipping(rs
						.getString("DurationALLClipping"));
				device.setMeanDurationALLClipping(rs
						.getString("MeanDurationALLClipping"));
				device.setDurationHangover(rs.getString("DurationHangover"));
				device.setNumberOfHangover(rs.getString("NumberOfHangover"));
				device.setAverageJitter(rs.getString("AverageJitter"));
				device.setVQuadCallID(rs.getString("VQuadCallID"));
				device.setTestName(rs.getString("TEST_NAME"));
				device.setLattitude(rs.getDouble("GEOLOCATION_LATITUDE"));
				device.setLongitude(rs.getDouble("GEOLOCATION_LANGITUDE"));
				device.setMarketName(getMarketName(rs.getString("MARKET_ID")));
				listUserData.add(device);
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			}
		}
		return listUserData;
	}

	public List<STGNetTest> getTestNameMapDetailsResultsForNetTest(
			String testCaseName) {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		List<STGNetTest> listUserData = new ArrayList<STGNetTest>();
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String stgQuery = "SELECT VQuadTimeStamp,CallTimeStamp,VQuadLocation,DEVICE_MODEL,VQuadLatLong,NetSpeedTest,BatteryLevel,"
					+ "RSSI,NetworkType,TNNumber,TargetServerIP,VQuadCallId,TEST_NAME,GEOLOCATION_LATITUDE,GEOLOCATION_LANGITUDE "
					+ "FROM STG_NET_RESULTS WHERE TEST_NAME='"
					+ testCaseName
					+ "'ORDER BY CallTimestamp ";
			rs = st.executeQuery(stgQuery);
			while (rs.next()) {
				STGNetTest netTest = new STGNetTest();
				netTest.setVQuadTimeStamp(rs.getString(1));
				netTest.setCallTimeStamp(rs.getString(2));
				netTest.setVQuadLocation(rs.getString(3));
				netTest.setVQuadPhoneId(rs.getString(4));
				netTest.setVQuadLatLong(rs.getString(5));
				netTest.setNetSpeedTest(rs.getString(6));
				netTest.setBatteryLevel(rs.getString(7));
				netTest.setRSSI(rs.getString(8));
				netTest.setNetworkType(rs.getString(9));
				netTest.setTNNumber(rs.getString(10));
				netTest.setTargetServerIP(rs.getString(11));
				netTest.setVQuadCallID(rs.getString(12));
				netTest.setTEST_NAME(rs.getString(13));
				netTest.setGEOLOCATION_LATITUDE(rs.getString(14));
				netTest.setGEOLOCATION_LANGITUDE(rs.getString(15));
				listUserData.add(netTest);
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			}
		}
		return listUserData;
	}

	public List<STGDevice> getTestNameDashboardDetails(String testCaseName) {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		List<STGDevice> deviceInfo = new ArrayList<STGDevice>();
		PreparedStatement pst = null;
		double SignalStrength = 0;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			/*
			 * String
			 * stgQuery=" SELECT * FROM STG_DEVICE_INFO WHERE TEST_NAME='"
			 * +testCaseName+"'";
			 */
			String stgQueryString = " SELECT  AVG(D.SIGNALSTRENGTH_GSMSIGNALSTRENGTH) ,S.PESQ  ,S.GEOLOCATION_LATITUDE  "
					+ " ,S.GEOLOCATION_LANGITUDE  FROM STG_DEVICE_INFO D ,STG_VQT_RESULTS S WHERE D.TEST_NAME='"
					+ testCaseName
					+ "' "
					+ " AND D.TEST_NAME=S.TEST_NAME AND D.GEOLOCATION_LATITUDE=S.GEOLOCATION_LATITUDE "
					+ " AND D.GEOLOCATION_LANGITUDE=S.GEOLOCATION_LANGITUDE  GROUP BY  D.GEOLOCATION_LATITUDE,D.GEOLOCATION_LANGITUDE ";
			rs = st.executeQuery(stgQueryString);
			while (rs.next()) {
				STGDevice device = new STGDevice();
				SignalStrength = round(Double.parseDouble(rs.getString(1)), 2);
				device.setSignalStrengthGSM(String.valueOf(SignalStrength));
				device.setPESQ(rs.getString(2));
				device.setLattitude(rs.getDouble(3));
				device.setLongitude(rs.getDouble(4));
				deviceInfo.add(device);
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			}
		}
		return deviceInfo;
	}

	public static double round(double targetValue, int roundToDecimalPlaces) {
		int valueInTwoDecimalPlaces = (int) (targetValue * Math.pow(10,
				roundToDecimalPlaces));
		return (double) (valueInTwoDecimalPlaces / Math.pow(10,
				roundToDecimalPlaces));
	}

	public List<DeviceInfoTO> getTestNameThroughputDetailsResults(
			String testCaseName, String marketId, String testtype) {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		List<DeviceInfoTO> deviceInfosList = new ArrayList<DeviceInfoTO>();
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String query = "SELECT sdi.SIGNALSTRENGTH_GSMSIGNALSTRENGTH,sdi.SIGNALSTRENGTH_CDMADBM,sdi.SIGNALSTRENGTH_EVDODBM,"
					+ " sdi.SIGNALSTRENGTH_LTESIGNALSTRENGTH,sdi.SIGNALSTRENGTH_LTERSRP,sdi.NETWORK_NETWORKTYPE,slc.EVENT_VALUE,slc.EVENT_NAME,sdi.GEOLOCATION_LATITUDE, sdi.GEOLOCATION_LANGITUDE ,"
					+ " DATE_FORMAT(slc.TIME_STAMP, '%Y-%m-%d %H:%i:%S')  AS TIMESTAMP ,sdi.CELLLOCATION_CID from stg_device_info sdi,stg_log_cat_info slc"
					+ " where sdi.TEST_NAME=slc.TEST_NAME and slc.TEST_NAME='"
					+ testCaseName
					+ "' and sdi.MARKET_ID='"
					+ marketId
					+ "'and sdi.TEST_TYPE=slc.TEST_TYPE  and sdi.TEST_TYPE='"
					+ testtype
					+ "' and "
					+ "slc.EVENT_NAME IN('Current TX bytes','Current RX bytes')"
					+ " and DATE_FORMAT(slc.TIME_STAMP, '%Y-%m-%d %H:%i:%S%')=DATE_FORMAT(sdi.TIME_STAMP_FOREACH_SAMPLE, '%Y-%m-%d %H:%i:%S%') "
					+ " GROUP BY DATE_FORMAT(slc.TIME_STAMP, '%Y-%m-%d %H:%i:%S') ";
			//logger.error("query---------ftp-----" + query);
			rs = st.executeQuery(query);
			while (rs.next()) {
				DeviceInfoTO deviceInfos = new DeviceInfoTO();
				deviceInfos.setTestName(testCaseName);
				deviceInfos.setSignalStrength(rs.getString(1));
				deviceInfos.setSignalStrengthCDMA(rs.getString(2));
				deviceInfos.setSignalStrengthEVDO(rs.getString(3));
				deviceInfos.setSignalStrengthLTE(rs.getString(4));
				deviceInfos.setSignalStrengthLTERSRP(rs.getString(5));
				deviceInfos.setNetworkType(rs.getString(6));
				deviceInfos.setEventValue(rs.getString(7));
				deviceInfos.setEventName(rs.getString(8));
				deviceInfos.setLattitude(rs.getDouble(9));
				deviceInfos.setLongitude(rs.getDouble(10));
				deviceInfos.setTimeStampForEachSample(rs.getString(11));
				deviceInfos.setCellLocationCID(rs.getString(12));
				deviceInfosList.add(deviceInfos);
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			}
		}
		return deviceInfosList;
	}

	public List<DeviceInfoTO> getTestNameMultipleThroughputDetailsResults(
			String testCaseName, String marketId, String testtype) {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		List<DeviceInfoTO> deviceInfosList = new ArrayList<DeviceInfoTO>();
		PreparedStatement pst = null;
		String test_name = testCaseName + "\\-%";
		String throughputTX = "";
		String throughputRX = "";
		String throughputMain = "";
		String currTxBytes = "";
		String prevTxBytes = "";
		String currRxBytes = "";
		String prevRxBytes = "";
		int i = 0;
		double txvalue = 0;
		double rxvalue = 0;
		double mainValue = 0;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String query = "SELECT sdi.SIGNALSTRENGTH_GSMSIGNALSTRENGTH,sdi.SIGNALSTRENGTH_CDMADBM,sdi.SIGNALSTRENGTH_EVDODBM,"
					+ " sdi.SIGNALSTRENGTH_LTESIGNALSTRENGTH,sdi.SIGNALSTRENGTH_LTERSRP,sdi.NETWORK_NETWORKTYPE,slc.EVENT_VALUE,slc.EVENT_NAME,sdi.GEOLOCATION_LATITUDE, sdi.GEOLOCATION_LANGITUDE ,"
					+ " DATE_FORMAT(slc.TIME_STAMP, '%Y-%m-%d %H:%i:%S')  AS TIMESTAMP,sdi.CELLLOCATION_CID from stg_device_info sdi,stg_log_cat_info slc"
					+ " where sdi.TEST_NAME=slc.TEST_NAME and slc.TEST_NAME LIKE '"
					+ test_name
					+ "' and sdi.MARKET_ID='"
					+ marketId
					+ "'and sdi.TEST_TYPE='"
					+ testtype
					+ "'and sdi.TEST_TYPE=slc.TEST_TYPE "
					+ "  and slc.EVENT_NAME IN('Current TX bytes','Current RX bytes')"
					+ " and DATE_FORMAT(slc.TIME_STAMP, '%Y-%m-%d %H:%i:%S%')=DATE_FORMAT(sdi.TIME_STAMP_FOREACH_SAMPLE, '%Y-%m-%d %H:%i:%S%') "
					+ " GROUP BY DATE_FORMAT(slc.TIME_STAMP, '%Y-%m-%d %H:%i:%S') ";

			//logger.error("query-------cycle------" + query);

			rs = st.executeQuery(query);
			while (rs.next()) {
				DeviceInfoTO deviceInfos = new DeviceInfoTO();
				deviceInfos.setTestName(testCaseName);
				deviceInfos.setSignalStrength(rs.getString(1));
				deviceInfos.setSignalStrengthCDMA(rs.getString(2));
				deviceInfos.setSignalStrengthEVDO(rs.getString(3));
				deviceInfos.setSignalStrengthLTE(rs.getString(4));
				deviceInfos.setSignalStrengthLTERSRP(rs.getString(5));
				deviceInfos.setNetworkType(rs.getString(6));
				deviceInfos.setEventValue(rs.getString(7));
				deviceInfos.setEventName(rs.getString(8));
				deviceInfos.setLattitude(rs.getDouble(9));
				deviceInfos.setLongitude(rs.getDouble(10));
				deviceInfos.setTimeStampForEachSample(rs.getString(11));
				deviceInfos.setCellLocationCID(rs.getString(12));
				throughputMain = getThroughput(i, rs.getString(8), deviceInfos,
						currTxBytes, THROUGHPUT, txvalue, throughputTX,
						prevTxBytes, throughputRX, currRxBytes, rxvalue,
						prevRxBytes, mainValue, throughputMain);
				deviceInfos.setThroughputmain(throughputMain);
				deviceInfosList.add(deviceInfos);

				i++;
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			} finally {
				try {
					conn.close();
				} catch (Exception e) {
					// TODO: handle exception
					logger.error(e.getMessage());
				}

			}
		}
		return deviceInfosList;
	}

	public boolean getCycleTestNames(String testCaseName) {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		String test_name = testCaseName + "\\-%";
		boolean status = false;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String query = "SELECT DISTINCT COUNT(TEST_NAME) FROM STG_LOG_CAT_INFO WHERE TEST_NAME LIKE '"
					+ test_name + "'";
			// logger.error("query--cyclcle----"+query);
			rs = st.executeQuery(query);
			while (rs.next()) {
				int count = rs.getInt(1);
				if (count > 1) {
					status = true;
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			} finally {
				try {
					conn.close();
				} catch (Exception e) {
					// TODO: handle exception
					logger.error(e.getMessage());
				}

			}
		}
		return status;
	}

	public boolean getCycleTestNamesInDeviceInfo(String testCaseName,
			String marketId) {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		String test_name = testCaseName + "\\-%";
		boolean status = false;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String query = "SELECT DISTINCT COUNT(TEST_NAME) FROM STG_DEVICE_INFO WHERE TEST_NAME LIKE '"
					+ test_name + "' AND MARKET_ID='" + marketId + "'";
			rs = st.executeQuery(query);
			while (rs.next()) {
				int count = rs.getInt(1);
				if (count > 1) {
					status = true;
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			} finally {
				try {
					conn.close();
				} catch (Exception e) {
					// TODO: handle exception
					logger.error(e.getMessage());
				}

			}
		}
		return status;
	}

	public boolean getCycleTestNamesMarketInDeviceInfo(String testCaseName,
			String market) {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		String test_name = testCaseName + "\\-%";
		boolean status = false;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String query = "SELECT DISTINCT COUNT(TEST_NAME) FROM STG_DEVICE_INFO WHERE TEST_NAME LIKE '"
					+ test_name + "' AND MARKET_ID='" + market + "'";
			rs = st.executeQuery(query);
			while (rs.next()) {
				int count = rs.getInt(1);
				if (count > 1) {
					status = true;
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			} finally {
				try {
					conn.close();
				} catch (Exception e) {
					// TODO: handle exception
					logger.error(e.getMessage());
				}

			}
		}
		return status;
	}

	public Map<String, String> getTestNames(String deviceName) {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		boolean status = false;
		String nametst = "";
		Map<String, String> testCaseMap = new LinkedHashMap<String, String>();
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String query = "SELECT DISTINCT FH.TEST_NAME FROM file_history FH,precalculated_tests PT WHERE FH.DEVICE_MODEL='"
					+ deviceName + "' AND FH.TEST_NAME = PT.TEST_NAME";
			// logger.error("query-------++------a------"+query);
			rs = st.executeQuery(query);
			while (rs.next()) {
				String testName = rs.getString("TEST_NAME");
				if (testName.contains("-")) {
					nametst = testName.substring(0, testName.indexOf("-"));
				} else {
					nametst = testName;
				}
				testCaseMap.put(nametst, nametst);
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}

		}
		return testCaseMap;
	}

	public Map<String, String> getTestNames(String deviceName, String userName) {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		boolean status = false;
		String nametst = "";
		Map<String, String> testCaseMap = new LinkedHashMap<String, String>();
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String query = "SELECT DISTINCT FH.TEST_NAME FROM file_history FH,precalculated_tests PT,STG_DEVICE_INFO SDI WHERE FH.DEVICE_MODEL='"
					+ deviceName
					+ "' AND FH.TEST_NAME = PT.TEST_NAME AND SDI.TEST_NAME like CONCAT(FH.TEST_NAME,'%') AND SDI.USER_NAME in (select user_name from users where user_id in ("+userName+"))";
//			 logger.error("query-------------a------"+query);
			rs = st.executeQuery(query);
			while (rs.next()) {
				String testName = rs.getString("TEST_NAME");
				if (testName.contains("-")) {
					nametst = testName.substring(0, testName.indexOf("-"));
				} else {
					nametst = testName;
				}
				testCaseMap.put(nametst, nametst);
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}

		}
		return testCaseMap;
	}

	public Map<String, String> getModelNames(String userIds, String roleName) {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		boolean status = false;
		Map<String, String> deviceMap = new LinkedHashMap<String, String>();
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String query = "SELECT  DISTINCT DEVICE_MODEL FROM file_history WHERE CREATED_BY IN("
					+ userIds + ")";
			if (roleName.equalsIgnoreCase(Roles.SUPERUSER)) {
				query = "SELECT  DISTINCT DEVICE_MODEL FROM file_history";
			}
			System.out.println("query---------" + query);
			rs = st.executeQuery(query);
			while (rs.next()) {
				String modelName = rs.getString("DEVICE_MODEL");
				deviceMap.put(modelName, modelName);
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}

		}
		return deviceMap;
	}

	public String getDeviceAvgVoiceQty(String deviceName, String marketName,
			String testName) {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		String vqtAvg = "0";
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String query = "SELECT AVG(PESQ) FROM stg_vqt_results WHERE DEVICE_MODEL='"
					+ deviceName + "'  AND TEST_NAME='" + testName + "'";
			rs = st.executeQuery(query);
			while (rs.next()) {
				vqtAvg = rs.getString(1);
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}

		}
		return vqtAvg;
	}

	public List<Float> getDownlinkParmatersForGraph(String deviceName,
			String marketId, String testname, String filesName) {
		List<Float> paramsValue = new ArrayList<Float>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String fileMarketName = "";
			String getMarketQuery = "SELECT MARKET_ID  FROM file_history WHERE FILE_NAME='"
					+ filesName.replaceAll("__--__", ":").replaceAll("\"", "")
					+ "'";
			// //logger.error("getMarketQuery-------"+getMarketQuery);
			rs = st.executeQuery(getMarketQuery);
			while (rs.next()) {
				fileMarketName = rs.getString("MARKET_ID");
			}
			if (fileMarketName.equals(marketId)) {
				String query = "SELECT AVG(PESQ),AVG(PESQLQ),AVG(PESQLQO),AVG(PESQWB) FROM stg_vqt_results WHERE DEVICE_MODEL='"
						+ deviceName
						+ "'  AND TEST_NAME='"
						+ testname
						+ "' AND MARKET_ID='"
						+ marketId
						+ "'  AND VQuadCallID LIKE 'I_%'";
				// //logger.error("query----------------"+query);
				rs = st.executeQuery(query);
				while (rs.next()) {
					if (rs.getFloat("AVG(PESQ)") == 0.0) {
						paramsValue.add(null);
					} else {
						paramsValue.add(rs.getFloat("AVG(PESQ)"));
					}
					if (rs.getFloat("AVG(PESQLQ)") == 0.0) {
						paramsValue.add(null);
					} else {
						paramsValue.add(rs.getFloat("AVG(PESQLQ)"));
					}
					if (rs.getFloat("AVG(PESQLQO)") == 0.0) {
						paramsValue.add(null);
					} else {
						paramsValue.add(rs.getFloat("AVG(PESQLQO)"));
					}
					/*
					 * if (rs.getFloat("AVG(PESQWB)")==0.0){
					 * paramsValue.add(null); }else{
					 * paramsValue.add(rs.getFloat("AVG(PESQWB)")); }
					 */
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}

		}
		// logger.error("paramsValue--0th index--"+paramsValue.get(0));
		return paramsValue;
	}

	public List<Float> getUplinkParmatersForGraph(String marketId,
			String testname) {
		List<Float> paramsValue = new ArrayList<Float>(4);
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String query = "SELECT MAX_PARAM_VALUE,PARAMETER_NAME,PARAMETER_VALUE FROM pre_cal_voicequality_2 WHERE  TEST_NAME='"
					+ testname
					+ "' AND MARKET_NAME='"
					+ marketId
					+ "'  AND CHART_TYPE LIKE 'uplink%'";
			// logger.error("query----++-------"+query);
			rs = st.executeQuery(query);
			while (rs.next()) {
				String paramName = rs.getString("PARAMETER_NAME");
				if (paramName.equalsIgnoreCase("EModel_Polqa")) {
					paramsValue.add(rs.getFloat("PARAMETER_VALUE") / 10);
				} else if (!paramName.equalsIgnoreCase("SNR")) {
					paramsValue.add(rs.getFloat("PARAMETER_VALUE"));
				}

				if (paramName.equalsIgnoreCase("polqa")) {
					paramsValue.add(rs.getFloat("MAX_PARAM_VALUE"));
				}

			}

		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}

		}
		return paramsValue;
	}

	public List<Float> getDownlinkParmatersForGraph(String marketId,
			String testname) {
		List<Float> paramsValue = new ArrayList<Float>(4);
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String query = "SELECT MAX_PARAM_VALUE,PARAMETER_NAME,PARAMETER_VALUE FROM pre_cal_voicequality_2 WHERE  TEST_NAME='"
					+ testname
					+ "' AND MARKET_NAME='"
					+ marketId
					+ "'  AND CHART_TYPE LIKE 'downlink%'";
			// logger.error("query----++-------"+query);
			rs = st.executeQuery(query);
			while (rs.next()) {
				String paramName = rs.getString("PARAMETER_NAME");
				if (paramName.equalsIgnoreCase("EModel_Polqa")) {
					paramsValue.add(rs.getFloat("PARAMETER_VALUE") / 10);
				} else if (!paramName.equalsIgnoreCase("SNR")) {
					paramsValue.add(rs.getFloat("PARAMETER_VALUE"));
				}
				if (paramName.equalsIgnoreCase("polqa")) {
					paramsValue.add(rs.getFloat("MAX_PARAM_VALUE"));
				}

			}

		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}

		}
		return paramsValue;
	}

	public HashMap<String, List<Float>> getUplinkDetailedValuesForGraphTable(
			String deviceName, String marketId, String testname,
			String filesName) {
		List<Float> pesqList = new ArrayList<Float>();
		List<Float> pesqlqqList = new ArrayList<Float>();
		List<Float> pesqlqoList = new ArrayList<Float>();
		List<Float> pesqwbList = new ArrayList<Float>();
		HashMap<String, List<Float>> detailsDataMap = new HashMap<String, List<Float>>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			/*
			 * String fileMarketName = ""; String getMarketQuery =
			 * "SELECT MARKET_ID  FROM file_history WHERE FILE_NAME='" +
			 * filesName.replaceAll("__--__", ":").replaceAll("\"", "") + "'";
			 * ////logger.error("getMarketQuery-------"+getMarketQuery);
			 * rs = st.executeQuery(getMarketQuery); while (rs.next()) {
			 * fileMarketName = rs.getString("MARKET_ID"); }
			 */
			// //logger.error("fileMarketName-------------"+fileMarketName);
			// //logger.error("marketId---------------"+marketId);
			// if (fileMarketName.equals(marketId)) {
			String query = "SELECT (PESQ),(PESQLQ),(PESQLQO),(PESQWB) FROM stg_vqt_results WHERE DEVICE_MODEL='"
					+ deviceName
					+ "'  AND TEST_NAME='"
					+ testname
					+ "' AND MARKET_ID='"
					+ marketId
					+ "'  AND PESQ NOT LIKE '0%' AND VQuadCallID LIKE 'O_%' ";
			// //logger.error("getUplinkDetailedValuesForGraphTable==========="+query);
			rs = st.executeQuery(query);
			while (rs.next()) {
				pesqList.add(rs.getFloat("PESQ"));
				pesqlqqList.add(rs.getFloat("PESQLQ"));
				pesqlqoList.add(rs.getFloat("PESQLQO"));
				// pesqwbList.add(rs.getInt("PESQWB"));
			}
			// }

		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}

		}
		detailsDataMap.put("pesq", pesqList);
		detailsDataMap.put("pesqlq", pesqlqqList);
		detailsDataMap.put("pesqlqo", pesqlqoList);
		detailsDataMap.put("pesqwb", pesqwbList);
		// logger.error("detailsDataMap----------"+pesqList.size());
		return detailsDataMap;
	}

	public HashMap<String, List<Float>> getDownlinkDetailedValuesForGraphTable(
			String deviceName, String marketId, String testname,
			String filesName) {
		List<Float> pesqList = new ArrayList<Float>();
		List<Float> pesqlqqList = new ArrayList<Float>();
		List<Float> pesqlqoList = new ArrayList<Float>();
		List<Float> pesqwbList = new ArrayList<Float>();
		HashMap<String, List<Float>> detailsDataMap = new HashMap<String, List<Float>>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String query = "SELECT (PESQ),(PESQLQ),(PESQLQO),(PESQWB) FROM stg_vqt_results WHERE DEVICE_MODEL='"
					+ deviceName
					+ "'  AND TEST_NAME='"
					+ testname
					+ "' AND MARKET_ID='"
					+ marketId
					+ "'  AND PESQ NOT LIKE '0%' AND VQuadCallID LIKE 'I_%' ";
//			System.out
//					.println("getDownlinkDetailedValuesForGraphTable==========="
//							+ query);
			rs = st.executeQuery(query);
			while (rs.next()) {
				pesqList.add(rs.getFloat("PESQ"));
				pesqlqqList.add(rs.getFloat("PESQLQ"));
				pesqlqoList.add(rs.getFloat("PESQLQO"));
				// pesqwbList.add(rs.getInt("PESQWB"));
			}
//			logger.error("pesqlqoList-----------" + pesqlqoList);
			if (pesqlqoList.size() == 0) {
				query = "SELECT (POLQA_Score) AS POLQASCORE,(EModel_Polqa) AS EMODELSCORE ,((Speech_Level_Diff/SNR_Diff)) AS SNR FROM stg_polqa_results WHERE DEVICE_MODEL='"
						+ deviceName
						+ "'  AND TEST_NAME='"
						+ testname
						+ "' AND MARKET_ID='"
						+ marketId
						+ "'  AND  VQuad_CallID LIKE '%I_%' ";
//				System.out
//						.println("getDownlinkDetailedValuesForGraphTable==========="
//								+ query);
				rs = st.executeQuery(query);
				while (rs.next()) {
					pesqList.add(rs.getFloat("POLQASCORE"));
					pesqlqqList.add(rs.getFloat("EMODELSCORE"));
					pesqlqoList.add(rs.getFloat("SNR"));
					// pesqwbList.add(rs.getInt("PESQWB"));
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}

		}
		detailsDataMap.put("pesq", pesqList);
		detailsDataMap.put("pesqlq", pesqlqqList);
		detailsDataMap.put("pesqlqo", pesqlqoList);
		detailsDataMap.put("pesqwb", pesqwbList);
		// //logger.error("detailsDataMap-------------"+detailsDataMap);
		return detailsDataMap;
	}

	public List<Float> getVQDetailsFor_LineForGraph(String deviceName,
			String marketId, String testname) {
		List<Float> paramsValue = new ArrayList<Float>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		Float bucket1_15 = new Float(0);
		Float bucket15_2 = new Float(0);
		Float bucket2_25 = new Float(0);
		Float bucket25_3 = new Float(0);
		Float bucket3_35 = new Float(0);
		Float bucket35_4 = new Float(0);
		Float bucket4_45 = new Float(0);
		Float bucket45_5 = new Float(0);
		Float totalValue = new Float(0);
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String query = "SELECT PESQ FROM stg_vqt_results WHERE DEVICE_MODEL='"
					+ deviceName
					+ "'  AND TEST_NAME='"
					+ testname
					+ "' AND MARKET_ID='"
					+ marketId
					+ "'  AND VQuadCallID LIKE 'I_%' AND PESQ NOT LIKE '0%' ORDER BY PESQ ";
			// logger.error("=====getVQDetailsFor_LineForGraph==="+query);
			rs = st.executeQuery(query);
			while (rs.next()) {
				totalValue++;
				Float value = rs.getFloat(1);
				// logger.error("value--------"+value);
				// totalValue = totalValue+value;
				if (value > 1 && value < 1.5) {
					bucket1_15++;
				} else if (value > 1.5 && value < 2) {
					// logger.error("inside if-------");
					bucket15_2++;
				} else if (value > 2 && value < 2.5) {
					bucket2_25++;
				} else if (value > 2 && value < 3) {
					bucket25_3++;
				} else if (value > 3 && value < 3.5) {
					bucket3_35++;
				} else if (value > 3.5 && value < 4) {
					bucket35_4++;
				} else if (value > 4 && value < 4.5) {
					bucket4_45++;
				} else if (value > 4.5 && value < 5) {
					bucket45_5++;
				}

			}
			// logger.error("bucket15_2--------"+bucket15_2);
			// logger.error("totalValue---------"+totalValue);
			if (bucket1_15 == 0.0) {
				bucket1_15 = null;
			} else {
				bucket1_15 = (bucket1_15 / totalValue) * 100;
			}
			if (bucket15_2 == 0.0) {
				bucket15_2 = null;
			} else {
				bucket15_2 = (bucket15_2 / totalValue) * 100;
			}
			if (bucket2_25 == 0.0) {
				bucket2_25 = null;
			} else {
				bucket2_25 = (bucket2_25 / totalValue) * 100;
			}
			if (bucket25_3 == 0.0) {
				bucket25_3 = null;
			} else {
				bucket25_3 = (bucket25_3 / totalValue) * 100;
			}
			if (bucket3_35 == 0.0) {
				bucket3_35 = null;
			} else {
				bucket3_35 = (bucket3_35 / totalValue) * 100;
			}
			if (bucket35_4 == 0.0) {
				bucket35_4 = null;
			} else {
				bucket35_4 = (bucket35_4 / totalValue) * 100;
			}
			if (bucket4_45 == 0.0) {
				bucket4_45 = null;
			} else {
				bucket4_45 = (bucket4_45 / totalValue) * 100;
			}
			if (bucket45_5 == 0.0) {
				bucket45_5 = null;
			} else {
				bucket45_5 = (bucket45_5 / totalValue) * 100;
			}
			// logger.error("bucket15_2-----leter---"+bucket15_2);
			paramsValue.add(bucket1_15);
			paramsValue.add((bucket15_2));
			paramsValue.add((bucket2_25));
			paramsValue.add((bucket25_3));
			paramsValue.add((bucket3_35));
			paramsValue.add((bucket35_4));
			paramsValue.add((bucket4_45));
			paramsValue.add((bucket45_5));
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}

		}
		// //logger.error("paramsValue for line graph :"+paramsValue);
		return paramsValue;
	}

	public List<Float> getVQDetailsFor_LineForGraphLTE(String deviceName,
			String marketId, String testname) {
		List<Float> paramsValue = new ArrayList<Float>();
		List<String> allLTETimeStamps = new ArrayList<String>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		Float bucket1_15 = new Float(0);
		Float bucket15_2 = new Float(0);
		Float bucket2_25 = new Float(0);
		Float bucket25_3 = new Float(0);
		Float bucket3_35 = new Float(0);
		Float bucket35_4 = new Float(0);
		Float bucket4_45 = new Float(0);
		Float bucket45_5 = new Float(0);
		Float totalValue = new Float(0);
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();

			String querytogetLTETimestamps = "SELECT DISTINCT TIME_STAMP_FOREACH_SAMPLE,NETWORK_NETWORKTYPE "
					+ "FROM STG_DEVICE_INFO WHERE NETWORK_NETWORKTYPE LIKE 'LTE%' ";

			rs = st.executeQuery(querytogetLTETimestamps);
			while (rs.next()) {
				String timeStamp = rs.getString("TIME_STAMP_FOREACH_SAMPLE");
				allLTETimeStamps.add(timeStamp);
			}
			for (int i = 0; i < allLTETimeStamps.size(); i++) {
				String getPesqValues = "select * from ("
						+ "(select * from STG_VQT_RESULTS where str_to_date(CallTimeStamp,'%d/%m/%Y %H:%i:%s') > str_to_date('"
						+ allLTETimeStamps.get(i)
						+ "','%Y-%m-%d %H:%i:%s') AND DEVICE_MODEL='"
						+ deviceName
						+ "'  AND TEST_NAME='"
						+ testname
						+ "' AND MARKET_ID='"
						+ marketId
						+ "'  AND VQuadCallID LIKE 'I_%' ORDER BY str_to_date(CallTimestamp,'%d/%m/%Y %H:%i:%s') ASC LIMIT 1)"
						+ " union "
						+ "(select * from STG_VQT_RESULTS where str_to_date(CallTimeStamp,'%d/%m/%Y %H:%i:%s') < str_to_date('"
						+ allLTETimeStamps.get(i)
						+ "','%Y-%m-%d %H:%i:%s') AND DEVICE_MODEL='"
						+ deviceName
						+ "'  AND TEST_NAME='"
						+ testname
						+ "' AND MARKET_ID='"
						+ marketId
						+ "'  AND VQuadCallID LIKE 'I_%' ORDER BY str_to_date(CallTimestamp,'%d/%m/%Y %H:%i:%s') ASC LIMIT 1"
						+ ")) as temp order by str_to_date(CallTimeStamp,'%d/%m/%Y %H:%i:%s') desc limit 1";
				// //logger.error("=====getVQDetailsFor_LineForGraph==="+getPesqValues);
				rs = st.executeQuery(getPesqValues);
				while (rs.next()) {
					Float value = rs.getFloat("PESQ");
					// //logger.error("value------------"+value);
					if (null != value) {
						totalValue = totalValue + value;
						if (value > 1 && value < 1.5) {
							bucket1_15 = value + bucket1_15;
						} else if (value > 1.5 && value < 2) {
							bucket15_2 = value + bucket15_2;
						} else if (value > 2 && value < 2.5) {
							bucket2_25 = value + bucket2_25;
						} else if (value > 2 && value < 3) {
							bucket25_3 = value + bucket25_3;
						} else if (value > 3 && value < 3.5) {
							bucket3_35 = value + bucket3_35;
						} else if (value > 3.5 && value < 4) {
							bucket35_4 = value + bucket35_4;
						} else if (value > 4 && value < 4.5) {
							bucket4_45 = value + bucket4_45;
						} else if (value > 4.5 && value < 5) {
							bucket45_5 = value + bucket45_5;
						}
					}

				}
				// //logger.error("bucket1_15-----------"+bucket1_15);
			}
			if (bucket1_15 == 0.0) {
				bucket1_15 = null;
			} else {
				bucket1_15 = (bucket1_15 / totalValue) * 100;
			}
			if (bucket15_2 == 0.0) {
				bucket15_2 = null;
			} else {
				bucket15_2 = (bucket15_2 / totalValue) * 100;
			}
			if (bucket2_25 == 0.0) {
				bucket2_25 = null;
			} else {
				bucket2_25 = (bucket2_25 / totalValue) * 100;
			}
			if (bucket25_3 == 0.0) {
				bucket25_3 = null;
			} else {
				bucket25_3 = (bucket25_3 / totalValue) * 100;
			}
			if (bucket3_35 == 0.0) {
				bucket3_35 = null;
			} else {
				bucket3_35 = (bucket3_35 / totalValue) * 100;
			}
			if (bucket35_4 == 0.0) {
				bucket35_4 = null;
			} else {
				bucket35_4 = (bucket35_4 / totalValue) * 100;
			}
			if (bucket4_45 == 0.0) {
				bucket4_45 = null;
			} else {
				bucket4_45 = (bucket4_45 / totalValue) * 100;
			}
			if (bucket45_5 == 0.0) {
				bucket45_5 = null;
			} else {
				bucket45_5 = (bucket45_5 / totalValue) * 100;
			}
			paramsValue.add(bucket1_15);
			paramsValue.add((bucket15_2));
			paramsValue.add((bucket2_25));
			paramsValue.add((bucket25_3));
			paramsValue.add((bucket3_35));
			paramsValue.add((bucket35_4));
			paramsValue.add((bucket4_45));
			paramsValue.add((bucket45_5));
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}

		}
		// //logger.error("paramsValue for line graph :"+paramsValue);
		return paramsValue;
	}

	public Map<String, String> getMarketIds(String testname, String deviceName,
			String testType) {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		boolean status = false;
		Map<String, String> testCaseMap = new LinkedHashMap<String, String>();
		// testCaseMap.put("Select Market", "Select Market");
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String query = "SELECT M.MARKET_ID,M.MARKET_NAME FROM MARKET M,file_history FH WHERE FH.TEST_NAME LIKE '"
					+ testname
					+ ""
					+ "' AND FH.DEVICE_MODEL='"
					+ deviceName
					+ "' AND FH.DATA_ID='"
					+ testType
					+ "' AND FH.MARKET_ID=M.MARKET_ID AND FH.MARKET_ID != '34'";
			//logger.error(query);
			System.out.println("market ID -"+query);
			rs = st.executeQuery(query);
			while (rs.next()) {
				String marketId = rs.getString("M.MARKET_ID");
				String marketName = rs.getString("M.MARKET_NAME");
				testCaseMap.put(marketName, marketId);
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}

		}
		return testCaseMap;
	}

	public Map<String, String> getFiles(String testname, String deviceName,
			String testType, String marketId) {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		boolean status = false;
		Map<String, String> testCaseMap = new LinkedHashMap<String, String>();
		// testCaseMap.put("Select Market", "Select Market");
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String query = "SELECT FILE_NAME, ID FROM file_history  WHERE TEST_NAME LIKE '"
					+ testname
					+ ""
					+ "' AND DEVICE_MODEL='"
					+ deviceName
					+ "' AND DATA_ID='"
					+ testType
					+ "' AND MARKET_ID='"
					+ marketId + "'";
			// logger.error(query);
			rs = st.executeQuery(query);
			while (rs.next()) {
				String fileName = rs.getString("FILE_NAME");
				String fileId = rs.getString("ID");
				if (!fileName.contains("mt")) {
					testCaseMap.put(fileName.replaceAll(":", "__--__"), fileId);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}

		}
		return testCaseMap;
	}

	public Map<String, List<String>> getFilesListForMarket(String testname,
			String deviceName, String testType, String marketId) {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		boolean status = false;
		List<String> filesList = new ArrayList<String>();
		Map<String, List<String>> testCaseMap = new LinkedHashMap<String, List<String>>();
		// testCaseMap.put("Select Market", "Select Market");
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String query = "SELECT FILE_NAME, ID FROM file_history  WHERE TEST_NAME LIKE '"
					+ testname
					+ ""
					+ "' AND DEVICE_MODEL='"
					+ deviceName
					+ "' AND DATA_ID='"
					+ testType
					+ "' AND MARKET_ID='"
					+ marketId + "'";
			// logger.error(query);
			rs = st.executeQuery(query);
			while (rs.next()) {
				String fileName = rs.getString("FILE_NAME");
				String fileId = rs.getString("ID");
				filesList.add(fileName);
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}

		}
		testCaseMap.put(deviceName + "_" + testname + "_" + marketId + "_"
				+ testType, filesList);
		return testCaseMap;
	}

	public String getTestId(String testName) {
		String testId = "";
		String query = "SELECT TEST_CONFIG_ID FROM test_config WHERE TEST_CONFIG_NAME='"
				+ testName + "'";
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			// //logger.error(query);
			rs = st.executeQuery(query);
			while (rs.next()) {
				testId = rs.getString("TEST_CONFIG_ID");
			}

		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}

		}
		return testId;
	}

	public void insertComments(CommentsTo commentTo) {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement st = null;

		PreparedStatement pst = null;
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String today = sdf.format(date);
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String query = "INSERT INTO comment (TEST_CONFIG_NAME,DEVICE_ID,MARKET_ID,KPI_ID,COMMENT,CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE)"
					+ "VALUES('"
					+ commentTo.getTestCaseName()
					+ "','1','"
					+ commentTo.getMarketId()
					+ "','"
					+ commentTo.getKpiId()
					+ "','"
					+ commentTo.getComments()
					+ "','"
					+ commentTo.getUserId()
					+ "','"
					+ today
					+ "','"
					+ commentTo.getUserId() + "','" + today + "')";
			// ///logger.error(query);
			st.executeUpdate(query);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}

		}
	}

	public List<String> getComments(CommentsTo commentTo) {
		List<String> commentsList = new ArrayList<String>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String query = "SELECT  C.COMMENT,U.USER_NAME FROM comment C,USERS U "
					+ "WHERE TEST_CONFIG_NAME='"
					+ commentTo.getTestCaseName()
					+ "' AND "
					+ "DEVICE_ID='"
					+ commentTo.getDeviceId()
					+ "' AND "
					+ "MARKET_ID='"
					+ commentTo.getMarketId()
					+ "' AND "
					+ "KPI_ID='"
					+ commentTo.getKpiId()
					+ "' AND"
					+ " C.CREATED_BY=U.USER_ID";

			rs = st.executeQuery(query);
			while (rs.next()) {
				String comments = rs.getString("C.COMMENT");
				String userName = rs.getString("U.USER_NAME");
				commentsList.add(userName + ":" + comments);
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}

		}
		return commentsList;

	}

	public DeviceTo getRefDevice(String testCaseName) {
		String query = "SELECT D.DEVICE_ID,D.DEVICE_NAME FROM DEVICE D,"
				+ "(SELECT ORD.DEVICE_ID FROM operator_refdevice ORD , "
				+ "(SELECT O.OPERATOR_ID FROM user_operator O,"
				+ "(SELECT U.USER_ID FROM USERS U,"
				+ "(SELECT DISTINCT USER_NAME FROM STG_DEVICE_INFO WHERE TEST_NAME='"
				+ testCaseName + "')STG "
				+ "WHERE STG.USER_NAME=U.USER_NAME) U "
				+ "WHERE O.USER_ID=U.USER_ID)OP "
				+ "WHERE ORD.OPERATOR_ID=OP.OPERATOR_ID) ORD "
				+ "WHERE ORD.DEVICE_ID=D.DEVICE_ID";
		DeviceTo dto = new DeviceTo();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;

		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();

			// //logger.error(query);
			rs = st.executeQuery(query);
			while (rs.next()) {
				String deviceId = rs.getString("D.DEVICE_ID");
				String deviceName = rs.getString("D.DEVICE_NAME");
				dto.setDeviceId(deviceId);
				dto.setDevicename(deviceName);
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}

		}
		return dto;

	}

	public String getDeviceId(String deviceName) {
		String query = "SELECT DEVICE_ID FROM DEVICE WHERE DEVICE_NAME='"
				+ deviceName + "'";
		DeviceTo dto = new DeviceTo();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		String deviceId = "";

		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			rs = st.executeQuery(query);
			while (rs.next()) {
				deviceId = rs.getString("DEVICE_ID");
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}

		}
		return deviceId;

	}

	public String getMarketName(String marketId) {
		String marketName = "";
		String query = "SELECT MARKET_NAME FROM MARKET WHERE MARKET_ID='"
				+ marketId + "'";
		DeviceTo dto = new DeviceTo();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		String deviceId = "";

		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			// logger.error("query-----getMarketName-------"+query);
			rs = st.executeQuery(query);
			while (rs.next()) {
				marketName = rs.getString("MARKET_NAME");
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}

		}
		return marketName;

	}

	public String getMarketNameList(String marketId) {
		String marketName = "";
		String query = "SELECT MARKET_NAME FROM MARKET WHERE MARKET_ID IN("
				+ marketId + ")";
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			rs = st.executeQuery(query);
			while (rs.next()) {
				if (marketName == "") {
					marketName = rs.getString("MARKET_NAME");
				} else {
					marketName = marketName + "," + rs.getString("MARKET_NAME");
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return marketName;
	}

	public String getMarketIdList(String marketId) {
		String marketName = "";
		String query = "SELECT MARKET_ID FROM MARKET WHERE MARKET_ID IN("
				+ marketId + ")";
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			rs = st.executeQuery(query);
			while (rs.next()) {
				if (marketName == "") {
					marketName = rs.getString("MARKET_ID");
				} else {
					marketName = marketName + "," + rs.getString("MARKET_ID");
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return marketName;
	}

	public void insertSummaryComments(CommentsTo commentTo) {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement st = null;

		PreparedStatement pst = null;
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String today = sdf.format(date);
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String query = "INSERT INTO comment (TEST_CONFIG_NAME,DEVICE_ID,MARKET_ID,COMMENT,CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE)"
					+ "VALUES('"
					+ commentTo.getTestCaseName()
					+ "','1','"
					+ commentTo.getMarketId()
					+ "','"
					+ commentTo.getComments()
					+ "','"
					+ commentTo.getUserId()
					+ "','"
					+ today
					+ "','"
					+ commentTo.getUserId() + "','" + today + "')";
			// //logger.error(query);
			st.executeUpdate(query);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}

		}
	}

	public List<String> getSummaryComments(CommentsTo commentTo) {
		List<String> commentsList = new ArrayList<String>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String query = "SELECT  C.COMMENT,U.USER_NAME FROM comment C,USERS U "
					+ "WHERE TEST_CONFIG_NAME='"
					+ commentTo.getTestCaseName()
					+ "' AND "
					+ "DEVICE_ID='"
					+ commentTo.getDeviceId()
					+ "' AND "
					+ "MARKET_ID='"
					+ commentTo.getMarketId()
					+ " AND " + " C.CREATED_BY=U.USER_ID";

			// //logger.error(query);
			rs = st.executeQuery(query);
			while (rs.next()) {
				String comments = rs.getString("C.COMMENT");
				String userName = rs.getString("U.USER_NAME");
				commentsList.add(userName + ":" + comments);
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}

		}
		return commentsList;

	}

	public String getNeighbourInfo(List<String> neighbourInfoList) {
		String neighbourtStr = null;
		List neighboursubInfoList = new ArrayList();
		try {
			for (int j = 0; j < neighbourInfoList.size(); j++) {
				if (null != neighbourInfoList.get(j)
						&& !neighbourInfoList.get(j).toString().equals("Empty")) {
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
											+ neighbourSubInfoArray[3] + ","
											+ "RSSI:"
											+ neighbourSubInfoArray[4];
								} else if (neighbourSubInfoArray[0]
										.matches("-1")) {
									neighbourtStr = "Cell: Empty" + ","
											+ "Lac:" + neighbourSubInfoArray[1]
											+ "," + "PSC:"
											+ neighbourSubInfoArray[3] + ","
											+ "RSSI:"
											+ neighbourSubInfoArray[4];
								} else if (neighbourSubInfoArray[1]
										.matches("-1")) {
									neighbourtStr = "Cell:"
											+ neighbourSubInfoArray[0] + ","
											+ "Lac:Empty" + "," + "PSC:"
											+ neighbourSubInfoArray[3] + ","
											+ "RSSI:"
											+ neighbourSubInfoArray[4];
								} else {
									neighbourtStr = "Cell:"
											+ neighbourSubInfoArray[0] + ","
											+ "Lac:" + neighbourSubInfoArray[1]
											+ "," + "PSC:"
											+ neighbourSubInfoArray[3] + ","
											+ "RSSI:"
											+ neighbourSubInfoArray[4];
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
						} else if (a == (neighbourInfoArray.length - 1)) {
							if (neighbourSubInfoArray[0].matches("-1")
									&& neighbourSubInfoArray[1].matches("-1")) {
								neighbourtStr = neighbourtStr + "|||Cell:Empty"
										+ "," + "Lac:Empty" + "," + "PSC:"
										+ neighbourSubInfoArray[3] + ","
										+ "RSSI:" + neighbourSubInfoArray[4]
										+ "||";
							} else if (neighbourSubInfoArray[0].matches("-1")) {
								neighbourtStr = neighbourtStr + "|||Cell:Empty"
										+ "," + "Lac:"
										+ neighbourSubInfoArray[1] + ","
										+ "PSC:" + neighbourSubInfoArray[3]
										+ "," + "RSSI:"
										+ neighbourSubInfoArray[4] + "||";
							} else if (neighbourSubInfoArray[1].matches("-1")) {
								neighbourtStr = neighbourtStr + "|||Cell:"
										+ neighbourSubInfoArray[0] + ","
										+ "Lac:Empty" + "," + "PSC:"
										+ neighbourSubInfoArray[3] + ","
										+ "RSSI:" + neighbourSubInfoArray[4]
										+ "||";
							} else {
								neighbourtStr = neighbourtStr + "|||Cell:"
										+ neighbourSubInfoArray[0] + ","
										+ "Lac:" + neighbourSubInfoArray[1]
										+ "," + "PSC:"
										+ neighbourSubInfoArray[3] + ","
										+ "RSSI:" + neighbourSubInfoArray[4]
										+ "||";
							}

						} else {
							if (neighbourSubInfoArray[0].matches("-1")
									&& neighbourSubInfoArray[1].matches("-1")) {
								neighbourtStr = neighbourtStr + "|||Cell:Empty"
										+ "," + "Lac:Empty" + "," + "PSC:"
										+ neighbourSubInfoArray[3] + ","
										+ "RSSI:" + neighbourSubInfoArray[4];
							} else if (neighbourSubInfoArray[0].matches("-1")) {
								neighbourtStr = neighbourtStr + "|||Cell:Empty"
										+ "," + "Lac:"
										+ neighbourSubInfoArray[1] + ","
										+ "PSC:" + neighbourSubInfoArray[3]
										+ "," + "RSSI:"
										+ neighbourSubInfoArray[4];
							} else if (neighbourSubInfoArray[1].matches("-1")) {
								neighbourtStr = neighbourtStr + "|||Cell:"
										+ neighbourSubInfoArray[0] + ","
										+ "Lac:Empty" + "," + "PSC:"
										+ neighbourSubInfoArray[3] + ","
										+ "RSSI:" + neighbourSubInfoArray[4];
							} else {
								neighbourtStr = neighbourtStr + "|||Cell:"
										+ neighbourSubInfoArray[0] + ","
										+ "Lac:" + neighbourSubInfoArray[1]
										+ "," + "PSC:"
										+ neighbourSubInfoArray[3] + ","
										+ "RSSI:" + neighbourSubInfoArray[4];
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
						neighbourtStr = neighboursubInfoList.get(x).toString();
					} else {
						neighbourtStr = neighbourtStr + ","
								+ neighboursubInfoList.get(x).toString();
					}
				}
			}

			if (null != neighbourtStr) {
				neighbourtStr = neighbourtStr.substring(0, neighbourtStr
						.length() - 1);
				neighbourtStr = neighbourtStr.substring(0, neighbourtStr
						.length() - 1);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return neighbourtStr;
	}

	public List<String> getNetworkdetails(String networkName,
			DeviceInfoTO deviceInfo, String SIGNALSTRENGTH_GSM,
			String SIGNALSTRENGTH_LTE, String SIGNALSTRENGTH_GSM1,
			List signalStrengthList, List signalStrengthListRating,
			List signalStrengthSnrList, List signalStrengthCDMACIOList,
			List signalStrengthEVDOECIOList, List signalStrengthLTERSRPList,
			List signalStrengthLTERSRQList, List signalStrengthLTERSSNRList,
			List signalStrengthLTECQIList, List signalStrengthLt, int i,
			List<DeviceInfoTO> deviceInfoList) {
		List<String> networkdetails = new ArrayList<String>();
		try {
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
					signalStrengthLt.add(String.valueOf(signalStrengthLtvalue)
							.substring(
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
					signalStrengthLt.add(String.valueOf(signalStrengthLtvalue)
							.substring(
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
				signalStrengthListRating.add(deviceInfo.getSignalStrength());
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
					signalStrengthList.add(deviceInfo.getSignalStrengthCDMA());
					signalStrengthLt.add(deviceInfo.getSignalStrengthCDMA());
					signalStrengthCDMACIOList.add(deviceInfo
							.getSignalStrengthCDMACIO());
					signalStrengthEVDOECIOList.add("null");
					signalStrengthSnrList.add("null");
					signalStrengthLTECQIList.add("null");
					signalStrengthLTERSSNRList.add("null");
					signalStrengthLTERSRQList.add("null");
					signalStrengthLTERSRPList.add("null");
				}
				signalStrengthListRating
						.add(deviceInfo.getSignalStrengthCDMA());
			} else if (networkName.matches("EVDO")) {
				if (deviceInfo.getSignalStrengthEVDO().equals("Empty")
						|| deviceInfo.getSignalStrengthEVDO().equals("")) {
					signalStrengthList.add("0");
					signalStrengthLt.add("0");
					signalStrengthEVDOECIOList.add(deviceInfo
							.getSignalStrengthEVDOECIO());
					signalStrengthSnrList
							.add(deviceInfo.getSignalStrengthSnr());
					signalStrengthCDMACIOList.add("null");
					signalStrengthLTECQIList.add("null");
					signalStrengthLTERSSNRList.add("null");
					signalStrengthLTERSRQList.add("null");
					signalStrengthLTERSRPList.add("null");
				} else {
					signalStrengthList.add(deviceInfo.getSignalStrengthEVDO());
					signalStrengthLt.add(deviceInfo.getSignalStrengthEVDO());
					signalStrengthEVDOECIOList.add(deviceInfo
							.getSignalStrengthEVDOECIO());
					signalStrengthSnrList
							.add(deviceInfo.getSignalStrengthSnr());
					signalStrengthCDMACIOList.add("null");
					signalStrengthLTECQIList.add("null");
					signalStrengthLTERSSNRList.add("null");
					signalStrengthLTERSRQList.add("null");
					signalStrengthLTERSRPList.add("null");
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
					int signalStrengthLTValue =  signalStrengthLTE;
					// signalStrengthLt.add(String.valueOf(signalStrengthLTValue).substring(1,
					// String.valueOf(signalStrengthLTValue).length()));
					signalStrengthLt.add(String.valueOf(
							deviceInfo.getSignalStrengthLTERSRP()).substring(
							1,
							String.valueOf(
									deviceInfo.getSignalStrengthLTERSRP())
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
					int signalStrengthLTEValue = -Integer
							.parseInt(SIGNALSTRENGTH_LTE)
							+ signalStrengthLTE;
					signalStrengthList.add(String
							.valueOf(signalStrengthLTEValue + "dBm."));
					int signalStrengthLTValue = -Integer
							.parseInt(SIGNALSTRENGTH_LTE)
							+ signalStrengthLTE;
					// signalStrengthLt.add(String.valueOf(signalStrengthLTValue).substring(1,
					// String.valueOf(signalStrengthLTValue).length()));
					signalStrengthLt.add(String.valueOf(
							deviceInfo.getSignalStrengthLTERSRP()).substring(
							1,
							String.valueOf(
									deviceInfo.getSignalStrengthLTERSRP())
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
					int signalStrengthLtvalue = Integer
							.parseInt(SIGNALSTRENGTH_GSM)
							+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
							* signalStrengthGSM;
					signalStrengthLt.add(String.valueOf(signalStrengthLtvalue)
							.substring(
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
					signalStrengthLt.add(String.valueOf(signalStrengthLtvalue)
							.substring(
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
				signalStrengthListRating.add(deviceInfo.getSignalStrength());
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		if (i == deviceInfoList.size() - 1) {
			networkdetails.add(signalStrengthList.toString());
			networkdetails.add(signalStrengthLt.toString());
			networkdetails.add(signalStrengthCDMACIOList.toString());
			networkdetails.add(signalStrengthEVDOECIOList.toString());
			networkdetails.add(signalStrengthSnrList.toString());
			networkdetails.add(signalStrengthLTECQIList.toString());
			networkdetails.add(signalStrengthLTERSSNRList.toString());
			networkdetails.add(signalStrengthLTERSRQList.toString());
			networkdetails.add(signalStrengthLTERSRPList.toString());
			networkdetails.add(signalStrengthListRating.toString());
		}
		return networkdetails;

	}

	public Map<String, String> getTestTypes(String deviceName, String tesName) {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		boolean status = false;
		HashMap<String, String> testTypeMap = new HashMap<String, String>();
		Set<String> testTypeSet = new HashSet<String>();
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String query = "SELECT  KT.DATA_ID,KT.DATA_TYPE FROM  file_history FH,kpi_type KT WHERE FH.TEST_NAME LIKE '"
					+ tesName
					+ ""
					+ "' AND FH.DEVICE_MODEL='"
					+ deviceName
					+ "' AND FH.DATA_ID = KT.DATA_ID";
			rs = st.executeQuery(query);
			// testTypeMap.put("Select Test Type", "0");
			while (rs.next()) {
				// testTypeMap.put(rs.getString("KT.DATA_TYPE"),
				// rs.getString("KT.DATA_ID"));
				String testType = rs.getString("KT.DATA_TYPE");
				String testId = rs.getString("KT.DATA_ID");
				testTypeMap.put(testType, testId);
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}

		}
		return testTypeMap;
	}

	public void insertKpiScore(Integer savedTestConfigId,
			Map<Integer, SaveConfigTo> kpiScoreMap, Integer userId,
			String testCaseName, String marketName, String valueSummary) {
		Connection conn = null;
		PreparedStatement pst = null;
		try {

			String insertKPIScoreSql = "INSERT INTO configuration_score(CONFIG_ID,KPI_ID,SCORE,CREATED_BY, "
					+ "CREATED_DATE,UPDATED_BY,UPDATED_DATE,TEST_NAME,MARKET_NAME,VALUE_SUMMARY) "
					+ "VALUES (?,?,?,?,?,?,?,?,?,?)";
			String date = DateUtil.getCurrentDateTime();
			conn = DBUtil.getConnection();
			pst = conn.prepareStatement(insertKPIScoreSql);
			Iterator it = kpiScoreMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				Integer kpiId = Integer.parseInt(pairs.getKey().toString());
				Float score = new Float(0);
				String avgSummaryVal = "";

				SaveConfigTo scto = (SaveConfigTo) pairs.getValue();
				score = Float.parseFloat(scto.getHealtIndex());
				avgSummaryVal = scto.getAvgScore();

				pst.setInt(1, savedTestConfigId);
				pst.setInt(2, kpiId);
				pst.setFloat(3, score);
				pst.setInt(4, userId);
				pst.setString(5, date);
				pst.setInt(6, userId);
				pst.setString(7, date);
				pst.setString(8, testCaseName);
				pst.setString(9, marketName);
				pst.setString(10, avgSummaryVal);
				// logger.error("insertKpiScore:"+ pst);
				pst.executeUpdate();
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				conn.close();
				pst.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage());
			}
		}
	}

	public List<String> getConfigurationNames() {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		List<String> configurationNameList = new ArrayList<String>();
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String query = "SELECT CONFIG_NAME FROM CONFIGURATION_MASTER";
			// //logger.error(query);
			rs = st.executeQuery(query);
			while (rs.next()) {
				String configName = rs.getString("CONFIG_NAME");
				configurationNameList.add(configName);
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		}
		return configurationNameList;
	}

	public Integer insertConfigMaster(String configName, Integer userId,
			Integer statusId) {
		Integer configId = null;
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		String date = DateUtil.getCurrentDateTime();
		String createdBy = null;
		String createdDate = null;
		String updatedBy = null;
		String updatedDate = null;
		int KpiId = 0;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String queryMaster = "INSERT INTO CONFIGURATION_MASTER (CONFIG_NAME,CONFIG_DESC,CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE,STATUS)"
					+ "VALUES('"
					+ configName
					+ "','"
					+ configName
					+ "','"
					+ userId
					+ "','"
					+ date
					+ "','"
					+ userId
					+ "','"
					+ date
					+ "','" + statusId + "')";
			// logger.error("insert config name :"+queryMaster);
			st.executeUpdate(queryMaster);
			st = conn.createStatement();
			// //logger.error("st=========="+st);
			rs = st
					.executeQuery("SELECT MAX(CONFIG_ID) FROM CONFIGURATION_MASTER");
			if (rs.next()) {
				configId = rs.getInt(1);
				// //logger.error("configId========="+configId);
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		}
		return configId;
	}

	public Integer getConfigurationId(String configName) {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		Integer configId = 0;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String query = "SELECT CONFIG_ID FROM CONFIGURATION_MASTER WHERE CONFIG_NAME='"
					+ configName + "'";
			// //logger.error(query);
			rs = st.executeQuery(query);
			while (rs.next()) {
				configId = rs.getInt("CONFIG_ID");
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		}
		return configId;
	}

	public void insertKpiComments(Integer savedTestConfigId,
			Map<Integer, String> kpiCommentMap, Integer userId) {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			String insertKPICommentsSql = "INSERT INTO configuration_kpi_comments(CONFIG_ID,KPI_ID,CONFIG_KPI_COMMENTS,CREATED_BY,CREATED_DATE) VALUES (?,?,?,?,?)";
			String date = DateUtil.getCurrentDateTime();
			pst = conn.prepareStatement(insertKPICommentsSql);
			Iterator it = kpiCommentMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				Integer kpiId = Integer.parseInt(pairs.getKey().toString());
				String comment = pairs.getValue().toString();
				pst.setInt(1, savedTestConfigId);
				pst.setInt(2, kpiId);
				pst.setString(3, comment);
				pst.setInt(4, userId);
				pst.setString(5, date);
				// logger.error("insertKpiComments query :"+ pst);
				pst.executeUpdate();
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				conn.close();
				pst.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage());
			}

		}

	}

	public void insertConfigComments(Integer savedTestConfigId, Integer userId,
			String summaryComments) {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		String date = DateUtil.getCurrentDateTime();
		String createdBy = null;
		String createdDate = null;
		String updatedBy = null;
		String updatedDate = null;

		// //logger.error("insertConfigComments savedTestConfigId :"+savedTestConfigId);
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String queryComments = "INSERT INTO CONFIGURATION_COMMENTS (CONFIG_ID,CONFIG_COMMENTS,CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE)"
					+ "VALUES('"
					+ savedTestConfigId
					+ "','"
					+ summaryComments
					+ "','"
					+ userId
					+ "','"
					+ date
					+ "','"
					+ userId
					+ "','"
					+ date + "')";
			// logger.error("insert queryComments :"+queryComments);
			st.executeUpdate(queryComments);

		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		}
	}

	public void manageCounter(Integer userId, Integer counterType) {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		String date = DateUtil.getCurrentDateTime();
		try {
			conn = DBUtil.getConnection();
			String insertCounterSql = "INSERT INTO COUNTER(USER_ID,COUNTER_TYPE,COUNT,CREATED_BY,CREATED_DATE) VALUES (?,?,?,?,?)";
			String updateCounterSql = "UPDATE COUNTER SET COUNT=?,MODIFIED_DATE=?,MODIFIED_BY=? WHERE USER_ID=? AND COUNTER_TYPE=?";
			String selectCounterSql = "SELECT COUNT FROM COUNTER WHERE USER_ID="
					+ userId + " AND COUNTER_TYPE=" + counterType;
			st = conn.createStatement();
			rs = st.executeQuery(selectCounterSql);
			int count = 0;
			if (rs.next()) {
				count = rs.getInt(1);
			}
			if (count > 0) {
				pst = conn.prepareStatement(updateCounterSql);
				pst.setInt(1, count + 1);
				pst.setString(2, date);
				pst.setInt(3, userId);
				pst.setInt(4, userId);
				pst.setInt(5, counterType);
				pst.executeUpdate();
			} else {
				pst = conn.prepareStatement(insertCounterSql);
				pst.setInt(1, userId);
				pst.setInt(2, counterType);
				pst.setInt(3, 1);
				pst.setInt(4, userId);
				pst.setString(5, date);
				pst.executeUpdate();
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				conn.close();
				st.close();
				rs.close();
				pst.close();
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}

		}

	}

	public List<Integer> getDownlinkParmatersForGraph(String deviceName,
			String marketId, String testname) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<FileHistoryTO> getMarketFiledetails(List<String> myList) {
		List<FileHistoryTO> paramsValue = new ArrayList<FileHistoryTO>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		FileHistoryTO fileHistoryTO = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String fileMarketName = "";
			for (int k = 0; k < myList.size(); k++) {
				String getMarketQuery = "SELECT TEST_NAME,FILE_NAME,MARKET_ID,DATA_ID,DEVICE_MODEL,TEST_TYPE  FROM file_history "
						+ " WHERE FILE_NAME='"
						+ myList.get(k).replaceAll("__--__", ":").replaceAll(
								"\"", "") + "'  AND ACTIVE='1' ";
				rs = st.executeQuery(getMarketQuery);
				while (rs.next()) {
					fileHistoryTO = new FileHistoryTO();
					fileHistoryTO.setTest_name(rs.getString(1));
					fileHistoryTO.setFile_name(rs.getString(2));
					fileHistoryTO.setMarket_Id(rs.getString(3));
					fileHistoryTO.setData_Id(rs.getString(4));
					fileHistoryTO.setDevice_model(rs.getString(5));
					fileHistoryTO.setTest_type(rs.getString(6));
					paramsValue.add(fileHistoryTO);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}

		}
		return paramsValue;
	}

	public void insertConfigCompare(Integer savedTestConfigId, Integer userId,
			String lConfigRouteName, String lDataType, String lMarkgetId,
			String lFileNameId, String lDeviceName) {
		// TODO Auto-generated method stub
		Connection conn = null;
		PreparedStatement pst = null;
		// //logger.error("insertConfigCompare..");
		// //logger.error("lDataType:"+lDataType+":lConfigRouteName:"+lConfigRouteName+":lMarkgetId:"+lMarkgetId
		// +":lFileName:"+lFileNameId);
		try {
			conn = DBUtil.getConnection();
			String insertConfigCompareSql = "INSERT INTO configuration_compare(CONFIG_ID,CONFIG_ROUTE_NAME,DATA_ID,MARKET_ID, "
					+ "FILE_ID,CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE,DEVICE_NAME) VALUES (?,?,?,?,?,?,?,?,?,?)";
			String date = DateUtil.getCurrentDateTime();
			pst = conn.prepareStatement(insertConfigCompareSql);

			pst.setInt(1, savedTestConfigId);
			pst.setString(2, lConfigRouteName);
			pst.setString(3, lDataType);
			pst.setString(4, lMarkgetId);
			pst.setString(5, lFileNameId.replaceAll("\"", ""));
			pst.setInt(6, userId);
			pst.setString(7, date);
			pst.setInt(8, userId);
			pst.setString(9, date);
			pst.setString(10, lDeviceName);
			// logger.error("insertConfigCompare.pst........:"+pst);
			pst.executeUpdate();

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				conn.close();
				pst.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage());
			}

		}

	}

	public List<String> getFilesForMarket(String testname, String deviceName,
			String testType, String marketId) {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		List<String> marketFileList = new ArrayList<String>();
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String query = "SELECT FILE_NAME, ID FROM file_history  WHERE TEST_NAME LIKE '"
					+ testname
					+ ""
					+ "' AND DEVICE_MODEL='"
					+ deviceName
					+ "' AND DATA_ID='"
					+ testType
					+ "' AND MARKET_ID='"
					+ marketId + "'";
			// //logger.error(query);
			rs = st.executeQuery(query);
			while (rs.next()) {
				String fileName = rs.getString("FILE_NAME");
				marketFileList.add(fileName.replaceAll(":", "__--__"));
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}

		}
		return marketFileList;
	}

	public Map<String, String> getMarketForTest(String testcaseName) {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		String sql = null;
		String query = null;
		Map<String, String> cityMap = new LinkedHashMap<String, String>();
		String test_name = testcaseName + "\\-%";
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			/*
			 * This was the old query
			 */
			/*
			 * query =
			 * "SELECT DISTINCT M.MARKET_ID,M.MARKET_NAME FROM MARKET  M ,STG_DEVICE_INFO ST WHERE ST.TEST_NAME  LIKE '"
			 * +test_name+"'" + " AND ST.MARKET_ID=M.MARKET_ID UNION ALL " +
			 * " SELECT DISTINCT M.MARKET_ID,M.MARKET_NAME FROM MARKET  M ,STG_DEVICE_INFO ST WHERE ST.TEST_NAME  LIKE '"
			 * +testcaseName+"'" + " AND ST.MARKET_ID=M.MARKET_ID ";
			 */

			/*
			 * Author:Srikanth Improved Query
			 */
			query = "SELECT M.MARKET_ID,M.MARKET_NAME FROM MARKET M, "
					+ "(SELECT  DISTINCT MARKET_ID FROM STG_DEVICE_INFO WHERE TEST_NAME  LIKE '"
					+ test_name + "'" + " OR TEST_NAME LIKE '" + testcaseName
					+ "'" + " )ST "
					+ " WHERE   ST.MARKET_ID=M.MARKET_ID";
			logger.error("query----------" + query);
			rs = st.executeQuery(query);
			while (rs.next()) {
				String cityId = rs.getString(1);
				String cityName = rs.getString(2);
				cityMap.put(cityName, cityId);
			}
			// //logger.error("sql-------------------------"+query);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return cityMap;
	}

	public Map<String, String> getTestTypeForMarket(String testcaseName,
			String marketId) {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		String test_type = null;
		String sql = null;
		String query = null;
		Map<String, String> TestTypeMap = new LinkedHashMap<String, String>();
		String test_name = testcaseName + "\\-%";
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			sql = "SELECT DISTINCT TEST_TYPE FROM STG_DEVICE_INFO WHERE TEST_NAME LIKE '"
					+ test_name
					+ "'  AND MARKET_ID='"
					+ marketId
					+ "' UNION ALL "
					+ "SELECT DISTINCT TEST_TYPE FROM STG_DEVICE_INFO WHERE TEST_NAME LIKE '"
					+ testcaseName + "'  AND MARKET_ID='" + marketId + "'";
			logger.error("sql---" + sql);
			rs = st.executeQuery(sql);
			while (rs.next()) {
				test_type = rs.getString(1);
				TestTypeMap.put(test_type, test_type);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return TestTypeMap;
	}

	/***
	 * Gets frequency band list
	 * 
	 * @param testcaseName
	 * @param marketId
	 * @return
	 */
	public Map<String, String> getFreqBandList(String testcaseName) {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		String test_type = null;
		String sql = null;
		String query = null;
		Map<String, String> TestTypeMap = new LinkedHashMap<String, String>();
		String test_name = testcaseName + "\\-%'";
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			sql = "select distinct fq.plan_name from freq_band fq,stg_device_info d where d.CELLLOCATION_CID = fq.LTE_CI and test_name like '"+test_name;
			logger.error("sql---" + sql);
			rs = st.executeQuery(sql);
			while (rs.next()) {
				test_type = rs.getString(1);
				TestTypeMap.put(test_type, test_type);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return TestTypeMap;
	}
	
	
	public Map<String, String> getDeviceForTest(String testcaseName) {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		String device = null;
		String sql = null;
		String query = null;
		Map<String, String> deviceMap = new LinkedHashMap<String, String>();
		String test_name = testcaseName + "\\-%";
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			sql = "SELECT DISTINCT DEVICE_MODEL FROM STG_DEVICE_INFO WHERE TEST_NAME LIKE '"
					+ test_name
					+ "'  UNION ALL "
					+ "SELECT DISTINCT DEVICE_MODEL FROM STG_DEVICE_INFO WHERE TEST_NAME LIKE '"
					+ testcaseName + "'";
			rs = st.executeQuery(sql);
			while (rs.next()) {
				device = rs.getString(1);
				deviceMap.put(device, device);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return deviceMap;
	}

	public String getThroughput(int i, String eventName,
			DeviceInfoTO deviceInfo, String currTxBytes, String THROUGHPUT,
			double txvalue, String throughputTX, String prevTxBytes,
			String throughputRX, String currRxBytes, double rxvalue,
			String prevRxBytes, double mainValue, String throughputMain) {
		if (i == 0) {
			if (eventName.equalsIgnoreCase("Current TX bytes")) {
				currTxBytes = deviceInfo.getEventValue();
				double h = Double.parseDouble(currTxBytes);
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
			if (throughputTX.equalsIgnoreCase("")) {
				throughputTX = "0";
			}
			if (throughputRX.equalsIgnoreCase("")) {
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
		return throughputMain;
	}

	public Map<String, String> getConfigurations() {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		boolean status = false;
		Map<String, String> configurationMap = new LinkedHashMap<String, String>();
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String query = "SELECT CONFIG_ID, CONFIG_NAME FROM CONFIGURATION_MASTER ORDER BY CONFIG_NAME";
			// //logger.error("getConfigurations :"+query);
			rs = st.executeQuery(query);
			while (rs.next()) {
				String configurationId = rs.getString("CONFIG_ID");
				String configurationName = rs.getString("CONFIG_NAME");
				configurationMap.put(configurationName, configurationId);
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}

		}
		return configurationMap;
	}

	public List<ConfigBean> getConfigurationDetails(String configurationId) {
		// TODO Auto-generated method stub
		// {"testName":"HTC520","reportType":1,"deviceName":"HTC One","filesNameId":[70],"filesName":["HTC One_VQTResults_Vijayanagar Market_2013-09-01 18__--__17__--__04.091_355972054004511"],"marketLabel":["Vijayanagar Market,M.G Road Market,JAYANAGAR MARKET"],"marketName":[3,2,1]}
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		List<ConfigBean> listCompareData = new ArrayList<ConfigBean>();
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();

			String configQuery = "SELECT CC.CONFIG_ID,CC.CONFIG_ROUTE_NAME,CC.MARKET_ID,CC.FILE_ID,CC.DEVICE_NAME,CC.DATA_ID "
					+ "FROM CONFIGURATION_COMPARE CC WHERE CC.CONFIG_ID='"
					+ configurationId + "'";
			// logger.error(".......getConfigurationDetails query........"+configQuery);
			rs = st.executeQuery(configQuery);

			while (rs.next()) {
				ConfigBean configBean = new ConfigBean();
				configBean.setConfigId(rs.getString("CC.CONFIG_ID"));
				configBean
						.setTestCaseName(rs.getString("CC.CONFIG_ROUTE_NAME"));
				configBean.setMarketmapId(rs.getString("CC.MARKET_ID"));
				configBean.setDeviceName(rs.getString("CC.DEVICE_NAME"));
				configBean.setReportType(rs.getString("CC.DATA_ID"));
				configBean.setFileId(rs.getString("CC.FILE_ID"));
				listCompareData.add(configBean);
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			}
		}
		// //logger.error("listCompareData :"+listCompareData.size());
		return listCompareData;
	}

	public String getFileName(String fileId) {
		// TODO Auto-generated method stub
		String fileName = "";
		String query = "SELECT FILE_NAME FROM FILE_HISTORY WHERE ID='" + fileId
				+ "'";
		DeviceTo dto = new DeviceTo();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		String deviceId = "";

		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			// //logger.error("query-----getFileName-------"+query);
			rs = st.executeQuery(query);
			while (rs.next()) {
				fileName = rs.getString("FILE_NAME");
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}

		}
		return fileName;
	}

	public Map<String, List<String>> populateMarketWiseFilesForSavedConfig(
			String deviceNames, String testNames, String marketNames,
			String testTypes, String marketId) {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		boolean status = false;
		List<String> filesList = new ArrayList<String>();
		Map<String, List<String>> marketWiseFilesMap = new LinkedHashMap<String, List<String>>();
		String[] deviceArr = deviceNames.split(",");
		String[] testArr = testNames.split(",");
		String[] marketArr = marketNames.split(",");
		String[] typeArr = testTypes.split(",");
		for (int i = 0; i < deviceArr.length; i++) {
			for (int j = 0; j < testArr.length; j++) {
				for (int k = 0; k < marketArr.length; k++) {
					for (int l = 0; l < typeArr.length; l++) {
						String deviceName = deviceArr[i];
						String testName = testArr[j];
						String marketName = marketArr[k];
						String reportType = typeArr[l];
						marketWiseFilesMap.putAll(new ReportDaoImpl()
								.getFilesListForMarket(testName, deviceName,
										reportType, marketName));

						try {
							conn = DBUtil.getConnection();
							st = conn.createStatement();
							String query = "SELECT FILE_NAME, ID FROM file_history  WHERE TEST_NAME LIKE '"
									+ testName
									+ ""
									+ "' AND DEVICE_MODEL='"
									+ deviceName
									+ "' AND DATA_ID='"
									+ reportType
									+ "' AND MARKET_ID='"
									+ marketName + "'";
							// //logger.error(query);
							rs = st.executeQuery(query);
							while (rs.next()) {
								String fileName = rs.getString("FILE_NAME");
								String fileId = rs.getString("ID");
								filesList.add(fileName);
							}
						} catch (Exception e) {
							// TODO: handle exception
							logger.error(e.getMessage());
						} finally {
							try {
								conn.close();
							} catch (Exception e) {
								// TODO: handle exception
								logger.error(e.getMessage());
							}

						}
						marketWiseFilesMap.put(deviceName + "_" + testName
								+ "_" + marketName, filesList);
					}
				}
			}
		}
		// testCaseMap.put("Select Market", "Select Market");

		return marketWiseFilesMap;
	}

	public Integer getGeneratedMapCount(Integer counterType, Integer tUserId) {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		String date = DateUtil.getCurrentDateTime();
		int count = 0;
		try {
			conn = DBUtil.getConnection();
			String selectCounterSql = "SELECT COUNT(*) FROM COUNTER WHERE USER_ID="
					+ tUserId + " AND COUNTER_TYPE=" + counterType;
			st = conn.createStatement();
			rs = st.executeQuery(selectCounterSql);

			if (rs.next()) {
				count = rs.getInt(1);
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				conn.close();
				st.close();
				rs.close();
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}

		}
		return count;
	}

	public List<ReportBean> getConfigurationComments(String configurationId) {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		List<ReportBean> configComments = new ArrayList<ReportBean>();
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String configCommentsQuery = "SELECT DISTINCT CM.CONFIG_ID,CM.CONFIG_COMMENTS, CKM.CONFIG_KPI_COMMENTS ,CKM.KPI_ID "
					+ "FROM CONFIGURATION_COMMENTS CM, CONFIGURATION_KPI_COMMENTS CKM "
					+ "WHERE CM.CONFIG_ID='"
					+ configurationId
					+ "' AND CKM.CONFIG_ID='" + configurationId + "'";
			// //logger.error("..............."+configCommentsQuery);
			rs = st.executeQuery(configCommentsQuery);

			while (rs.next()) {
				ReportBean reportBean = new ReportBean();
				reportBean.setConfigId(rs.getString("CM.CONFIG_ID"));
				reportBean.setTestSummaryComment(rs
						.getString("CM.CONFIG_COMMENTS"));
				int kpiId = rs.getInt("CKM.KPI_ID");
				if (kpiId == 1) {
					reportBean.setTestCommentKpi1(rs
							.getString("CKM.CONFIG_KPI_COMMENTS"));
				} else if (kpiId == 2) {
					reportBean.setTestCommentKpi2(rs
							.getString("CKM.CONFIG_KPI_COMMENTS"));
				} else if (kpiId == 3) {
					reportBean.setTestCommentKpi3(rs
							.getString("CKM.CONFIG_KPI_COMMENTS"));
				} else if (kpiId == 4) {
					reportBean.setTestCommentKpi4(rs
							.getString("CKM.CONFIG_KPI_COMMENTS"));
				} else if (kpiId == 5) {
					reportBean.setTestCommentKpi5(rs
							.getString("CKM.CONFIG_KPI_COMMENTS"));
				} else if (kpiId == 6) {
					reportBean.setTestCommentKpi6(rs
							.getString("CKM.CONFIG_KPI_COMMENTS"));
				} else if (kpiId == 7) {
					reportBean.setTestCommentKpi7(rs
							.getString("CKM.CONFIG_KPI_COMMENTS"));
				}
				configComments.add(reportBean);
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			}
		}
		// //logger.error("configComments :"+configComments.size());
		return configComments;
	}

	public String getFileNamesForFileIds(String fileId) {
		// TODO Auto-generated method stub
		String fileNames = "";
		String query = "SELECT FILE_NAME FROM FILE_HISTORY WHERE ID IN("
				+ fileId + ")";
		DeviceTo dto = new DeviceTo();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		String deviceId = "";

		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			// logger.error("query-----getFileNamesForFileIds-------"+query);
			rs = st.executeQuery(query);
			while (rs.next()) {
				fileNames = fileNames + rs.getString("FILE_NAME") + ",";
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}

		}
		// logger.error("fileNames in-----getFileNamesForFileIds-------"+fileNames);
		fileNames = fileNames.substring(0, fileNames.length() - 1); // to remove
		// last ","
		return fileNames;
	}

	public String getConfigName(String configId) {
		// TODO Auto-generated method stub
		String query = "SELECT CONFIG_NAME FROM CONFIGURATION_MASTER WHERE CONFIG_ID = '"
				+ configId + "'";
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		String configName = "";
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			// logger.error("query-----getConfigName-------"+query);
			rs = st.executeQuery(query);
			while (rs.next()) {
				configName = rs.getString("CONFIG_NAME");
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}

		}
		// logger.error("configName in-----getConfigName-------"+configName);
		return configName;
	}
/*
 * (This function will read the excel file which is uploaded and and checks for the same headings as db have for freq_band table,
 *  & also delete the records with the same plan name if exist & then insert records with the same Plan name.)
 * @see com.dao.ReportDao#ReadExcelFile(java.lang.String, java.lang.String) changes made on 09-02-16 by Ankit dicussed with Annie & Mathew.
 */
	public String ReadExcelFile(String FileName,String FreqPlanName,boolean overWrite){
		int length = ReportConstants.FREQ_BAND_HEADINGS.length;
		String[] Freq_band = new String[length];
		String[] Freq_band_values = new String[length];
		ArrayList a = new ArrayList();
		//if that plan name is already exist then it will delete records with that plan name.
		if(overWrite){deleteFreqBandPlanWithSameName(FreqPlanName);}
		int j=0;
		 try
	        {
			  FileInputStream file = new FileInputStream(new File(FREQUENCY_FILE_PATH+"/"+FileName));
	            //Create Workbook instance holding reference to .xlsx file
	            XSSFWorkbook workbook = new XSSFWorkbook(file);
	 
	            //Get first/desired sheet from the workbook
	            XSSFSheet sheet = workbook.getSheetAt(0);
	            int i = 0 ;
	            
	            // iterate through the rows
	            for (Row row : sheet) {
	                 /*// avoid first row as it is header
	                 if (row.getRowNum() == 0) {
	                      continue;
	                 }*/
	                
	                for (int count = 0; count < length; count++) {
	                     Cell cell = row.getCell(count, Row.RETURN_BLANK_AS_NULL);

	                    // whenever we get blank cell value, we avoid it and continues the loop
	                    if (cell != null) {
	                    // check the cell type
	                   switch (cell.getCellType()) {
	                   case Cell.CELL_TYPE_STRING:
	                	   if(i==0){ Freq_band[count] = cell.getStringCellValue();}
                       	else{Freq_band_values[count] =cell.getStringCellValue();}
	                        break;
	                  case Cell.CELL_TYPE_NUMERIC:
	                	  if(i==0){ Freq_band[count] = cell.getStringCellValue();}
                      	else{
                      		//Freq_band_values[count] = Double.toString(cell.getNumericCellValue());
                      		if(count==5){
                      			long ia = (long)cell.getNumericCellValue(); 
                      		Freq_band_values[count] = String.valueOf(ia);} 
                      		else{Freq_band_values[count] = Double.toString(cell.getNumericCellValue());}
                      	}
	                       break;
	                   		}//end of Switch loop
	                   }
	                   else{Freq_band_values[count] = null;
	                   		continue;
	                	    }
	                  }//end of count for loop which is for cell
	                if(i==0){ 
	                	if(!Arrays.equals(ReportConstants.FREQ_BAND_HEADINGS,Freq_band)){return "HeadingError";}
	                }
	                else
	                {	/*Insert new records with the same Plan name.
	                		working code. */
	                	//System.out.println("hello what r u showing loop kitni bar chal rha he row wala..."+Freq_band_values.toString());
	                	insertIntoFreqBand(Freq_band_values,FreqPlanName);
	                	
	                	//just need to try that null row will not insert in database. 
	                	/*if(Freq_band_values==null) {
	                		insertIntoFreqBand(Freq_band_values,FreqPlanName);
	                	}*/
	                	
	                }
	                i++;
	              }//end of row loop
	           file.close();
	            }
	         catch (Exception e)
	        {
	            e.printStackTrace();
	        }
		 return "Success";
	}
	
	public boolean CheckFreqBandPlan(String FreqPlanName) {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		boolean status = false;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String query = "SELECT COUNT(LTE_CI) FROM FREQ_BAND WHERE PLAN_NAME LIKE '"+ FreqPlanName + "'";
			// logger.error("query--cyclcle----"+query);
//			System.out.println("query--cyclcle----"+query);
			rs = st.executeQuery(query);
			while (rs.next()) {
				int count = rs.getInt(1);
				if (count < 1) {
					status = true;
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			} finally {
				try {
					conn.close();
				} catch (Exception e) {
					// TODO: handle exception
					logger.error(e.getMessage());
				}

			}
		}
		return status;
	}
	

	public void insertIntoFreqBand(String[] Freq_band_values,String FreqPlanName) {
		// TODO Auto-generated method stub
				Connection conn = null;
				PreparedStatement pst = null;
				try {
					conn = DBUtil.getConnection();
					String insertConfigCompareSql = "INSERT INTO freq_band(PLAN_NAME,Region,Market,Sector_ID,Latitude,Longitude, "
							+ "LTE_CI,CGI,DL_EUARFCN,UL_EUARFCN,GSMNeighbors,WCDMAneighbors,eUtranNeighRelations,BAND) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					String date = DateUtil.getCurrentDateTime();
					pst = conn.prepareStatement(insertConfigCompareSql);

					pst.setString(1, FreqPlanName);
					pst.setString(2, Freq_band_values[0]);
					pst.setString(3, Freq_band_values[1]);
					pst.setString(4, Freq_band_values[2]);
					pst.setString(5, Freq_band_values[3]);
					pst.setString(6, Freq_band_values[4]);
					pst.setString(7, Freq_band_values[5]);
					pst.setString(8, Freq_band_values[6]);
					pst.setString(9, Freq_band_values[7]);
					pst.setString(10, Freq_band_values[8]);
					pst.setString(11, Freq_band_values[9]);
					pst.setString(12, Freq_band_values[10]);
					pst.setString(13, Freq_band_values[11]);
					pst.setString(14, Freq_band_values[12]);
//				    System.out.println("insertIntoFreqBand.pst........:"+pst);
					pst.executeUpdate();

				} catch (Exception e) {
					logger.error(e.getMessage());
				} finally {
					try {
						conn.close();
						pst.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						logger.error(e.getMessage());
					}

				}
		}
	
	public void deleteFreqBandPlanWithSameName(String FreqPlanName) {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		boolean status = false;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String query = "DELETE FROM FREQ_BAND WHERE PLAN_NAME LIKE '"+ FreqPlanName + "'";
			// logger.error("query--cyclcle----"+query);
			//System.out.println("query--cyclcle----"+query);
			st.executeUpdate(query);
			}
		catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			} finally {
				try {
					conn.close();
				} catch (Exception e) {
					// TODO: handle exception
					logger.error(e.getMessage());
				}

			}
		}
		//return status;
	}
	
	public boolean margeFreqBandWithPrecalTable(String TestName,String FreqPlanName,String tableName) {
		// TODO Auto-generated method stub
				Connection conn = null;
				PreparedStatement pst = null;
				boolean MargeStatus = false;
				try {
					conn = DBUtil.getConnection();
					String updateQry = "UPDATE "+tableName+" INNER JOIN freq_band ON "
							+tableName+".CELLLOCATION_CID = freq_band.LTE_CI SET "
							+tableName+".FREQ_BAND = freq_band.BAND where test_name = '"+TestName+"'" 
							+"and Plan_name ='"+FreqPlanName+"'";
					pst = conn.prepareStatement(updateQry);
//					System.out.println("updateQry.pst........:"+pst);
					int status = pst.executeUpdate();
					if(status>0) { MargeStatus = true; }
				} catch (Exception e) {
					logger.error(e.getMessage());
				} finally {
					try {
						conn.close();
						pst.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						logger.error(e.getMessage());
					}

				}
				return MargeStatus;
		}
	//Check values in table for test names
	public ArrayList<String> getValuesInStgTablesByTestname(String TestName, String marketId, String ColumnName, String sqlTableName) {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		boolean status = false;
		//String[] vQuadTimeStampValues; 
		ArrayList<String> vQuadTimeStampValues = new ArrayList<String>(); 
		int i = 0;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String query = "SELECT "+ColumnName+" FROM "+sqlTableName+" WHERE TEST_NAME LIKE '"+ TestName + "%' AND MARKET_ID = '"+marketId+"'";
			// logger.error("query--cyclcle----"+query);
//			System.out.println("query--cyclcle----"+query);
			rs = st.executeQuery(query);
			while (rs.next()) {
				vQuadTimeStampValues.add(rs.getString(ColumnName));
				i++;
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			} finally {
				try {
					conn.close();
				} catch (Exception e) {
					// TODO: handle exception
					logger.error(e.getMessage());
				}

			}
		}
		return vQuadTimeStampValues;
	}
	//Function is used to update the time stamp of a stg table.
	public boolean updateTimeStampofStgTable(String TestName, String ColumnName, String sqlTableName, String FinalDate, String oldtime) {
		// TODO Auto-generated method stub
				Connection conn = null;
				PreparedStatement pst = null;
				boolean MargeStatus = false;
				try {
					conn = DBUtil.getConnection();
					String updateQry = "UPDATE "+sqlTableName+" set "+ColumnName+" = '"+FinalDate+"' where test_name = '"+TestName+"'"
							+ "and "+ColumnName+"='"+oldtime+"'";
					pst = conn.prepareStatement(updateQry);
//					System.out.println("updateQry.pst........:"+pst);
					int status = pst.executeUpdate();
					if(status>0) { MargeStatus = true; }
				} catch (Exception e) {
					logger.error(e.getMessage());
				} finally {
					try {
						conn.close();
						pst.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						logger.error(e.getMessage());
					}

				}
				return MargeStatus;
		}
	public static void main(String[] args) {

		// HH converts hour in 24 hours format (0-23), day calculation

		Date d1 = null;
		Date d2 = null;

		try {
			// for(int i=0;i<100;i++){
			d1 = new Date();
			new ReportDaoImpl()
					.getDownlinkParmatersForGraph(
							"LG-D801",
							"22",
							"g2app",
							"LG-D801_VQTResults_Seattle East_2014-01-07 15__--__26__--__55.562_013703000107825");
			d2 = new Date();

			// in milliseconds
			long diff = d2.getTime() - d1.getTime();
			// //logger.error("diff)---------"+diff);
			// }

		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		}
	}
}
