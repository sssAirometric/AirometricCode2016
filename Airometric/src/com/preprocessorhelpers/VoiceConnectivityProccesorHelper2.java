package com.preprocessorhelpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.PropertyFileReader;
import com.model.DBUtil;
import com.preprocessing.PreProcessor;
import com.preprocessing.PreprocessorTrigger;
import com.report.dao.impl.VoiceConnectivityDaoImpl;
import com.report.to.CallretentionTo;
import com.report.to.IdleTimeTo;
import com.to.CallSetUpTo;
import com.to.DeviceInfoTO;
import com.to.VoiceConnectivityTO;

public class VoiceConnectivityProccesorHelper2 {

	static Connection conn = null;
	static Statement st = null;

	public void openConn() {
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void closeConn() {
		try {
			st.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<CallSetUpTo> getAllCallEventandDeviceInfoDetails(
			String test_name, String test_type, String marketId, int flag) {
		List<CallSetUpTo> callsetUpList = new ArrayList<CallSetUpTo>();
		String query = "";
		ResultSet rs = null;
		try {
			query = "SELECT STR_TO_DATE(VQuad_Timestamp,'%m/%d/%Y %H:%i:%s') AS VQuad_Timestamp ,CALL_CONTROL_EVENT FROM STG_CALLEVENT_RESULTS WHERE TEST_NAME LIKE '%"
					+ test_name
					+ "%' AND MARKET_ID='"
					+ marketId
					+ "'"
					+ " ORDER BY VQuad_Timestamp";
			rs = st.executeQuery(query);
			while (rs.next()) {
				CallSetUpTo cst = new CallSetUpTo();
				String timeStamp = rs.getString("VQuad_Timestamp");
				String eventName = rs.getString("CALL_CONTROL_EVENT");
				/* Gets data for Call setup details */
				if (flag == 1) {
					if ((!eventName.equalsIgnoreCase("PDD event"))
							&& (!eventName.equalsIgnoreCase("RingBack"))
							&& (!eventName.equalsIgnoreCase("Incoming Call"))
							&& (!eventName.equalsIgnoreCase("CallerId"))) {

						cst.setEventName(eventName);
						cst.setTimeStamp(timeStamp);
						DeviceInfoTO dto = getMatchedDeviceInfo(timeStamp,
								"'%Y-%m-%d %H:%i:%s'", test_name, marketId,
								test_type);
						cst.setDeviceInfoTO(dto);
						callsetUpList.add(cst);
					}
				} else {
					/* Gets data for all details */
					cst.setEventName(eventName);
					cst.setTimeStamp(timeStamp);
					DeviceInfoTO dto = getMatchedDeviceInfo(timeStamp,
							"'%Y-%m-%d %H:%i:%s'", test_name, marketId,
							test_type);
					cst.setDeviceInfoTO(dto);
					callsetUpList.add(cst);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return callsetUpList;
	}

	public static List<List<CallSetUpTo>> getAllInLogCatInfoEvents(
			String test_name, String marketId) {
		List<List<CallSetUpTo>> eventCycleList = new ArrayList<List<CallSetUpTo>>();
		List<CallSetUpTo> individulaEventList = new ArrayList<CallSetUpTo>();
		String query = "";
		ResultSet rs = null;

		try {
			query = "SELECT Call_Timestamp,STR_TO_DATE(Call_Timestamp,'%m/%d/%Y %H:%i:%s') AS VQuad_Timestamp ,CALL_CONTROL_EVENT FROM STG_CALLEVENT_RESULTS WHERE TEST_NAME LIKE '%"
					+ test_name + "%'  AND MARKET_ID='" + marketId + "'";
			// System.out.println(query);
			rs = st.executeQuery(query);
			st.setFetchDirection(ResultSet.FETCH_REVERSE);
			boolean isPrevEvent_Placing = false;
			rs.afterLast();
			int count = 1417;
			while (rs.previous()) {
				CallSetUpTo cst = new CallSetUpTo();
				String calleventTime = rs.getString("Call_Timestamp");
				String timeStamp = rs.getString("VQuad_Timestamp");
				String eventName = rs.getString("CALL_CONTROL_EVENT").trim();
				if ((!eventName.equalsIgnoreCase("PDD event"))
						&& (!eventName.equalsIgnoreCase("RingBack"))
						&& (!eventName.equalsIgnoreCase("Incoming Call"))
						&& (!eventName.equalsIgnoreCase("CallerId"))) {
					// //System.out.println(count+"--eventName------"+eventName);
					cst.setEventName(eventName);
					cst.setTimeStamp(timeStamp);
					cst.setCalleventsTimeStamp(calleventTime);
					count--;
					if (isPrevEvent_Placing
							&& eventName.equalsIgnoreCase("Placing Call")) {
						individulaEventList = new ArrayList<CallSetUpTo>();

					}
					if (!isPrevEvent_Placing
							&& eventName.equalsIgnoreCase("Placing Call")) {
						isPrevEvent_Placing = true;
					}
					// if (!eventName.equalsIgnoreCase("Missed Call")) {
					individulaEventList.add(cst);
					// }
					// if (eventName.equalsIgnoreCase("Call Dropped"))
					// {
					// System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> "+eventName);
					// }
					if (eventName.equalsIgnoreCase("Call Dropped")
							|| eventName.equalsIgnoreCase("Missed Call")) {
						eventCycleList.add(individulaEventList);
						isPrevEvent_Placing = false;
						individulaEventList = new ArrayList<CallSetUpTo>();
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("eventCycleList------------" + eventCycleList);
		return eventCycleList;
	}

	public static Map<String, List<CallSetUpTo>> getcallSetupChartDetails2(
			String testName, String testType, String marketId) {
		// ////System.out.println("Seattle---------"+new
		// VoiceConnectivityDaoImpl().getCallTearDownTime("22","SGH-M919","SGH-M919_mo_Seattle East_2014-01-08 14:47:48.067_355537050050378"));
		VoiceConnectivityProccesorHelper2 vcd = new VoiceConnectivityProccesorHelper2();
		List<CallSetUpTo> forAllList = vcd.getAllCallEventandDeviceInfoDetails(
				testName, testType, marketId, 0);
		List<CallSetUpTo> callList = vcd.getAllCallEventandDeviceInfoDetails(
				testName, testType, marketId, 1);
		List<CallSetUpTo> callFormattedList = new ArrayList<CallSetUpTo>();
		List<CallSetUpTo> callTearDownList = new ArrayList<CallSetUpTo>();
		List<CallSetUpTo> allDetailsList = new ArrayList<CallSetUpTo>();
		Map<String, List<CallSetUpTo>> voiceConnectivityMap = new HashMap<String, List<CallSetUpTo>>();
		int listSize = callList.size();
		for (int i = 0; i < listSize; i++) {
			CallSetUpTo cst = callList.get(i);
			String eventName = cst.getEventName();
			String nextEvent = "";
			if ((i + 1 < listSize) && null != callList.get(i + 1)) {
				nextEvent = callList.get(i + 1).getEventName();
			}
			if ((eventName.equalsIgnoreCase("Placing Call") && nextEvent
					.equalsIgnoreCase("Connected"))) {
				callFormattedList.add(callList.get(i));
				callFormattedList.add(callList.get(i + 1));
				// System.out.println("event date---" + cst.getTimeStamp());

			}
		}
		for (int i = 0; i < forAllList.size(); i++) {
			allDetailsList.add(forAllList.get(i));
		}

		voiceConnectivityMap.put("callSetupDetails", callFormattedList);
		voiceConnectivityMap.put("callTearDownDetails", callTearDownList);
		voiceConnectivityMap.put("allDetails", allDetailsList);
		// //System.out.println("voiceConnectivityMap--------"+voiceConnectivityMap);
		return voiceConnectivityMap;
	}

	public static DeviceInfoTO getMatchedDeviceInfo(String date, String format,
			String testName, String marketId, String testType) {
		DeviceInfoTO deviceInfos = new DeviceInfoTO();
		// SimpleDateFormat sdfformat = new SimpleDateFormat(format);
		ResultSet rs = null;
		Connection conn = DBUtil.openConn();

		String query = "select * from ("
				+ "(select * from stg_device_info where TEST_NAME  LIKE  '"
				+ testName
				+ "%' AND TEST_TYPE='"
				+ testType
				+ "' AND str_to_date(TIME_STAMP_FOREACH_SAMPLE,'%Y-%m-%d %H:%i:%s') > str_to_date('"
				+ date
				+ "',"
				+ format
				+ ") ORDER BY str_to_date(TIME_STAMP_FOREACH_SAMPLE,'%Y-%m-%d %H:%i:%s')  ASC LIMIT 1)"
				+ "union"
				+ "(select * from stg_device_info where TEST_NAME  LIKE  '"
				+ testName
				+ "%' AND TEST_TYPE='"
				+ testType
				+ "' AND str_to_date(TIME_STAMP_FOREACH_SAMPLE,'%Y-%m-%d %H:%i:%s') < str_to_date('"
				+ date
				+ "',"
				+ format
				+ ")  ORDER BY str_to_date(TIME_STAMP_FOREACH_SAMPLE,'%Y-%m-%d %H:%i:%s')  DESC LIMIT 1)"
				+

				") as temp order by str_to_date(TIME_STAMP_FOREACH_SAMPLE,'%Y-%m-%d %H:%i:%s') desc limit 1";

		// System.out.println("Matched timestamped query-----------" + query);
		// System.out.println(">>>>>>>>>Second Count inner>>>>> " +
		// secondCount);

		try {
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(query);

			while (rs.next()) {
				String networkType = rs.getString("NETWORK_TYPE");
				int rsrp = 0;
				if (rs.getString("SIGNALSTRENGTH_LTERSRP").equalsIgnoreCase(
						"Empty")) {
					rsrp = rs.getInt("SIGNALSTRENGTH_LTERSRP");
				}
				if (!(networkType.contains("LTE"))
						|| ((networkType.contains("LTE")) && rsrp < 1000)) {
					deviceInfos.setTestName(rs.getString("TEST_NAME"));
					deviceInfos.setNetworkType(rs.getString("NETWORK_TYPE"));
					deviceInfos.setNetworkDataState(rs
							.getString("NETWORK_DATASTATE"));
					deviceInfos.setNetworkRoaming(rs
							.getString("NETWORK_ROAMING"));
					if (networkType.contains("LTE")) {
						deviceInfos.setSignalStrength(rs
								.getString("SIGNALSTRENGTH_LTERSRP"));
					} else {
						deviceInfos.setSignalStrength(rs
								.getString("SIGNALSTRENGTH_GSMSIGNALSTRENGTH"));
					}

					/*
					 * deviceInfos.setSignalStrength(rs
					 * .getString("SIGNALSTRENGTH_LTERSRP"));
					 */
					deviceInfos.setImei(rs.getString("DEVICE_IMEI"));
					deviceInfos.setTestType(rs.getString("TEST_TYPE"));
					deviceInfos.setUserName(rs.getString("USER_NAME"));
					deviceInfos.setUserName(rs.getString("USER_NAME"));
					deviceInfos.setPhoneNumber(rs
							.getString("DEVICE_PHONENUMBER"));
					deviceInfos.setPhoneType(rs.getString("DEVICE_PHONETYPE"));
					deviceInfos.setDeviceName(rs.getString("DEVICE_MODEL"));
					deviceInfos
							.setDeviceVersion(rs.getString("DEVICE_VERSION"));
					deviceInfos.setTimeStampForEachSample(rs
							.getString("TIME_STAMP_FOREACH_SAMPLE"));
					deviceInfos.setNetworkOperator(rs
							.getString("NETWORK_NETWORKOPERATOR"));
					deviceInfos.setNetworkOperatorName(rs
							.getString("NETWORK_NETWORKOPERATORNAME"));
					deviceInfos.setDataState(rs.getString("NETWORK_DATASTATE"));
					deviceInfos.setDataActivity(rs
							.getString("NETWORK_DATAACTIVITY"));
					deviceInfos.setWifiState(rs.getString("NETWORK_WIFISTATE"));
					deviceInfos.setNetworkRoaming(rs
							.getString("NETWORK_ROAMING"));
					deviceInfos.setNetworkMCC(rs.getString("NETWORK_MCC"));
					deviceInfos.setNetworkMNC(rs.getString("NETWORK_MNC"));
					deviceInfos.setSignalStrengthGSM(rs
							.getString("SIGNALSTRENGTH_GSMSIGNALSTRENGTH"));
					deviceInfos.setSignalStrengthCDMA(rs
							.getString("SIGNALSTRENGTH_CDMADBM"));
					deviceInfos.setSignalStrengthCDMACIO(rs
							.getString("SIGNALSTRENGTH_CDMACIO"));

					deviceInfos.setSignalStrengthEVDO(rs
							.getString("SIGNALSTRENGTH_EVDODBM"));
					deviceInfos.setSignalStrengthEVDOECIO(rs
							.getString("SIGNALSTRENGTH_EVDOECIO"));
					deviceInfos.setSignalStrength_EVDOSNR(rs
							.getString("SIGNALSTRENGTH_EVDOSNR"));
					deviceInfos
							.setSignalStrengthGSMBITRATEERROR("SIGNALSTRENGTH_GSMBITRATEERROR");

					deviceInfos.setSignalStrengthLTE(rs
							.getString("SIGNALSTRENGTH_LTESIGNALSTRENGTH"));
					deviceInfos.setSignalStrengthLTERSRP(rs
							.getString("SIGNALSTRENGTH_LTERSRP"));
					deviceInfos.setSignalStrengthLTERSRQ(rs
							.getString("SIGNALSTRENGTH_LTERSRQ"));
					deviceInfos.setSignalStrengthLTERSSNR(rs
							.getString("SIGNALSTRENGTH_LTERSSNR"));
					deviceInfos.setSignalStrengthLTECQI(rs
							.getString("SIGNALSTRENGTH_LTERSSNR"));
					deviceInfos.setCellLocationCID(rs
							.getString("CELLLOCATION_CID"));
					deviceInfos.setCellLocationLAC(rs
							.getString("CELLLOCATION_LAC"));
					deviceInfos
							.setNeighbourInfo(rs.getString("NEIGHBOUR_INFO"));
					deviceInfos.setBatteryLevel(rs.getString("BATTERY_LEVEL"));
					deviceInfos.setNetworkManuallyDone(rs
							.getString("NETWORK_MANUALLY_DONE"));

					deviceInfos.setLattitude(rs
							.getDouble("GEOLOCATION_LATITUDE"));
					deviceInfos.setLongitude(rs
							.getDouble("GEOLOCATION_LANGITUDE"));
					deviceInfos.setSnapShotId(rs.getString("SNAPSHOT_ID"));
					deviceInfos.setCellLocationCID(rs
							.getString("CELLLOCATION_CID"));
					deviceInfos.setCellLocationLAC(rs
							.getString("CELLLOCATION_LAC"));
					deviceInfos.setDevicePhoneType(rs
							.getString("DEVICE_PHONETYPE"));

					deviceInfos.setSignalStrengthSnr(rs
							.getString("SIGNALSTRENGTH_EVDOSNR"));

					deviceInfos.setSignalStrengthCDMACIO(rs
							.getString("SIGNALSTRENGTH_CDMACIO"));
					deviceInfos.setSignalStrengthEVDOECIO(rs
							.getString("SIGNALSTRENGTH_EVDOECIO"));

					// deviceInfos.set
					deviceInfos.setSignalStrengthLTECQI(rs
							.getString("SIGNALSTRENGTH_LTECQI"));

					deviceInfos.setDeviceManufacturer(rs
							.getString("DEVICE_MANUFACTURER"));
					deviceInfos.setNetworkType(rs.getString("NETWORK_TYPE"));

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return deviceInfos;
	}

	public static List<CallSetUpTo> getNonMatchinDeviceInfo(String testName,
			String marketId, String testType, String date) {
		String query = "SELECT * FROM STG_DEVICE_INFO WHERE TEST_NAME LIKE '"
				+ testName + "' AND MARKET_ID='" + marketId
				+ "' AND TEST_TYPE='" + testType
				+ "' AND TIME_STAMP_FOREACH_SAMPLE NOT IN ('" + date + "')";
		// System.out.println("nonMatchedQuery>>>>>>>>>>>>>>>>>>>>>>>>" +
		// query);
		List<CallSetUpTo> allcallSetUpList = new ArrayList<CallSetUpTo>();
		Connection conn = DBUtil.openConn();
		DeviceInfoTO deviceInfos = new DeviceInfoTO();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				String networkType = rs.getString("NETWORK_TYPE");
				int rsrp = 0;
				if (rs.getString("SIGNALSTRENGTH_LTERSRP").equalsIgnoreCase(
						"Empty")) {
					rsrp = rs.getInt("SIGNALSTRENGTH_LTERSRP");
				}
				if (!(networkType.contains("LTE"))
						|| ((networkType.contains("LTE")) && rsrp < 1000)) {

					deviceInfos.setTestName(rs.getString("TEST_NAME"));
					deviceInfos.setNetworkType(rs.getString("NETWORK_TYPE"));
					deviceInfos.setNetworkDataState(rs
							.getString("NETWORK_DATASTATE"));
					deviceInfos.setNetworkRoaming(rs
							.getString("NETWORK_ROAMING"));
					if (networkType.contains("LTE")) {
						deviceInfos.setSignalStrength(rs
								.getString("SIGNALSTRENGTH_LTERSRP"));
					} else {
						deviceInfos.setSignalStrength(rs
								.getString("SIGNALSTRENGTH_GSMSIGNALSTRENGTH"));
					}

					/*
					 * deviceInfos.setSignalStrength(rs
					 * .getString("SIGNALSTRENGTH_LTERSRP"));
					 */
					deviceInfos.setImei(rs.getString("DEVICE_IMEI"));
					deviceInfos.setTestType(rs.getString("TEST_TYPE"));
					deviceInfos.setUserName(rs.getString("USER_NAME"));
					deviceInfos.setUserName(rs.getString("USER_NAME"));
					deviceInfos.setPhoneNumber(rs
							.getString("DEVICE_PHONENUMBER"));
					deviceInfos.setPhoneType(rs.getString("DEVICE_PHONETYPE"));
					deviceInfos.setDeviceName(rs.getString("DEVICE_MODEL"));
					deviceInfos
							.setDeviceVersion(rs.getString("DEVICE_VERSION"));
					deviceInfos.setTimeStampForEachSample(rs
							.getString("TIME_STAMP_FOREACH_SAMPLE"));
					deviceInfos.setNetworkOperator(rs
							.getString("NETWORK_NETWORKOPERATOR"));
					deviceInfos.setNetworkOperatorName(rs
							.getString("NETWORK_NETWORKOPERATORNAME"));
					deviceInfos.setDataState(rs.getString("NETWORK_DATASTATE"));
					deviceInfos.setDataActivity(rs
							.getString("NETWORK_DATAACTIVITY"));
					deviceInfos.setWifiState(rs.getString("NETWORK_WIFISTATE"));
					deviceInfos.setNetworkRoaming(rs
							.getString("NETWORK_ROAMING"));
					deviceInfos.setNetworkMCC(rs.getString("NETWORK_MCC"));
					deviceInfos.setNetworkMNC(rs.getString("NETWORK_MNC"));
					deviceInfos.setSignalStrengthGSM(rs
							.getString("SIGNALSTRENGTH_GSMSIGNALSTRENGTH"));
					deviceInfos.setSignalStrengthCDMA(rs
							.getString("SIGNALSTRENGTH_CDMADBM"));
					deviceInfos.setSignalStrengthCDMACIO(rs
							.getString("SIGNALSTRENGTH_CDMACIO"));

					deviceInfos.setSignalStrengthEVDO(rs
							.getString("SIGNALSTRENGTH_EVDODBM"));
					deviceInfos.setSignalStrengthEVDOECIO(rs
							.getString("SIGNALSTRENGTH_EVDOECIO"));
					deviceInfos.setSignalStrength_EVDOSNR(rs
							.getString("SIGNALSTRENGTH_EVDOSNR"));
					deviceInfos
							.setSignalStrengthGSMBITRATEERROR("SIGNALSTRENGTH_GSMBITRATEERROR");

					deviceInfos.setSignalStrengthLTE(rs
							.getString("SIGNALSTRENGTH_LTESIGNALSTRENGTH"));
					deviceInfos.setSignalStrengthLTERSRP(rs
							.getString("SIGNALSTRENGTH_LTERSRP"));
					deviceInfos.setSignalStrengthLTERSRQ(rs
							.getString("SIGNALSTRENGTH_LTERSRQ"));
					deviceInfos.setSignalStrengthLTERSSNR(rs
							.getString("SIGNALSTRENGTH_LTERSSNR"));
					deviceInfos.setSignalStrengthLTECQI(rs
							.getString("SIGNALSTRENGTH_LTERSSNR"));
					deviceInfos.setCellLocationCID(rs
							.getString("CELLLOCATION_CID"));
					deviceInfos.setCellLocationLAC(rs
							.getString("CELLLOCATION_LAC"));
					deviceInfos
							.setNeighbourInfo(rs.getString("NEIGHBOUR_INFO"));
					deviceInfos.setBatteryLevel(rs.getString("BATTERY_LEVEL"));
					deviceInfos.setNetworkManuallyDone(rs
							.getString("NETWORK_MANUALLY_DONE"));

					deviceInfos.setLattitude(rs
							.getDouble("GEOLOCATION_LATITUDE"));
					deviceInfos.setLongitude(rs
							.getDouble("GEOLOCATION_LANGITUDE"));
					deviceInfos.setSnapShotId(rs.getString("SNAPSHOT_ID"));
					deviceInfos.setCellLocationCID(rs
							.getString("CELLLOCATION_CID"));
					deviceInfos.setCellLocationLAC(rs
							.getString("CELLLOCATION_LAC"));
					deviceInfos.setDevicePhoneType(rs
							.getString("DEVICE_PHONETYPE"));

					deviceInfos.setSignalStrengthSnr(rs
							.getString("SIGNALSTRENGTH_EVDOSNR"));

					deviceInfos.setSignalStrengthCDMACIO(rs
							.getString("SIGNALSTRENGTH_CDMACIO"));
					deviceInfos.setSignalStrengthEVDOECIO(rs
							.getString("SIGNALSTRENGTH_EVDOECIO"));

					// deviceInfos.set
					deviceInfos.setSignalStrengthLTECQI(rs
							.getString("SIGNALSTRENGTH_LTECQI"));

					deviceInfos.setDeviceManufacturer(rs
							.getString("DEVICE_MANUFACTURER"));
					CallSetUpTo cto = new CallSetUpTo();
					cto.setDeviceInfoTO(deviceInfos);
					allcallSetUpList.add(cto);

				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return allcallSetUpList;
	}

	// public static List<CallSetUpTo> getNonMatchinDeviceInfo(String testName,
	// String marketId, String testType, String matchingTimeStamps) {
	// List<CallSetUpTo> allcallSetUpList = new ArrayList<CallSetUpTo>();
	// String query = "SELECT * FROM STG_DEVICE_INFO WHERE TEST_NAME LIKE '"
	// + testName + "-%' " + "AND MARKET_ID='" + marketId
	// + "' AND TEST_TYPE='" + testType
	// + "' AND TIME_STAMP_FOREACH_SAMPLE NOT IN ("
	// + matchingTimeStamps + ")";
	// // System.out.println(query);
	// Connection conn = DBUtil.openConn();
	// try {
	// Statement stmt = conn.createStatement();
	// ResultSet rs = stmt.executeQuery(query);
	// while (rs.next()) {
	// String networkType = rs.getString("NETWORK_TYPE");
	// int rsrp = 0;
	// if (rs.getString("SIGNALSTRENGTH_LTERSRP").equalsIgnoreCase(
	// "Empty")) {
	// rsrp = rs.getInt("SIGNALSTRENGTH_LTERSRP");
	// }
	// if (!(networkType.contains("LTE"))
	// || ((networkType.contains("LTE")) && rsrp < 1000)) {
	// DeviceInfoTO deviceInfos = new DeviceInfoTO();
	// deviceInfos.setTestName(rs.getString("TEST_NAME"));
	// deviceInfos.setNetworkType(rs.getString("NETWORK_TYPE"));
	// deviceInfos.setNetworkDataState(rs
	// .getString("NETWORK_DATASTATE"));
	// deviceInfos.setNetworkRoaming(rs
	// .getString("NETWORK_ROAMING"));
	// if (networkType.contains("LTE")) {
	// deviceInfos.setSignalStrength(rs
	// .getString("SIGNALSTRENGTH_LTERSRP"));
	// } else {
	// deviceInfos.setSignalStrength(rs
	// .getString("SIGNALSTRENGTH_GSMSIGNALSTRENGTH"));
	// }
	//
	// /*
	// * deviceInfos.setSignalStrength(rs
	// * .getString("SIGNALSTRENGTH_LTERSRP"));
	// */
	// deviceInfos.setImei(rs.getString("DEVICE_IMEI"));
	// deviceInfos.setTestType(rs.getString("TEST_TYPE"));
	// deviceInfos.setUserName(rs.getString("USER_NAME"));
	// deviceInfos.setUserName(rs.getString("USER_NAME"));
	// deviceInfos.setPhoneNumber(rs
	// .getString("DEVICE_PHONENUMBER"));
	// deviceInfos.setPhoneType(rs.getString("DEVICE_PHONETYPE"));
	// deviceInfos.setDeviceName(rs.getString("DEVICE_MODEL"));
	// deviceInfos
	// .setDeviceVersion(rs.getString("DEVICE_VERSION"));
	// deviceInfos.setTimeStampForEachSample(rs
	// .getString("TIME_STAMP_FOREACH_SAMPLE"));
	// deviceInfos.setNetworkOperator(rs
	// .getString("NETWORK_NETWORKOPERATOR"));
	// deviceInfos.setNetworkOperatorName(rs
	// .getString("NETWORK_NETWORKOPERATORNAME"));
	// deviceInfos.setDataState(rs.getString("NETWORK_DATASTATE"));
	// deviceInfos.setDataActivity(rs
	// .getString("NETWORK_DATAACTIVITY"));
	// deviceInfos.setWifiState(rs.getString("NETWORK_WIFISTATE"));
	// deviceInfos.setNetworkRoaming(rs
	// .getString("NETWORK_ROAMING"));
	// deviceInfos.setNetworkMCC(rs.getString("NETWORK_MCC"));
	// deviceInfos.setNetworkMNC(rs.getString("NETWORK_MNC"));
	// deviceInfos.setSignalStrengthGSM(rs
	// .getString("SIGNALSTRENGTH_GSMSIGNALSTRENGTH"));
	// deviceInfos.setSignalStrengthCDMA(rs
	// .getString("SIGNALSTRENGTH_CDMADBM"));
	// deviceInfos.setSignalStrengthCDMACIO(rs
	// .getString("SIGNALSTRENGTH_CDMACIO"));
	//
	// deviceInfos.setSignalStrengthEVDO(rs
	// .getString("SIGNALSTRENGTH_EVDODBM"));
	// deviceInfos.setSignalStrengthEVDOECIO(rs
	// .getString("SIGNALSTRENGTH_EVDOECIO"));
	// deviceInfos.setSignalStrength_EVDOSNR(rs
	// .getString("SIGNALSTRENGTH_EVDOSNR"));
	// deviceInfos
	// .setSignalStrengthGSMBITRATEERROR("SIGNALSTRENGTH_GSMBITRATEERROR");
	//
	// deviceInfos.setSignalStrengthLTE(rs
	// .getString("SIGNALSTRENGTH_LTESIGNALSTRENGTH"));
	// deviceInfos.setSignalStrengthLTERSRP(rs
	// .getString("SIGNALSTRENGTH_LTERSRP"));
	// deviceInfos.setSignalStrengthLTERSRQ(rs
	// .getString("SIGNALSTRENGTH_LTERSRQ"));
	// deviceInfos.setSignalStrengthLTERSSNR(rs
	// .getString("SIGNALSTRENGTH_LTERSSNR"));
	// deviceInfos.setSignalStrengthLTECQI(rs
	// .getString("SIGNALSTRENGTH_LTERSSNR"));
	// deviceInfos.setCellLocationCID(rs
	// .getString("CELLLOCATION_CID"));
	// deviceInfos.setCellLocationLAC(rs
	// .getString("CELLLOCATION_LAC"));
	// deviceInfos
	// .setNeighbourInfo(rs.getString("NEIGHBOUR_INFO"));
	// deviceInfos.setBatteryLevel(rs.getString("BATTERY_LEVEL"));
	// deviceInfos.setNetworkManuallyDone(rs
	// .getString("NETWORK_MANUALLY_DONE"));
	//
	// deviceInfos.setLattitude(rs
	// .getDouble("GEOLOCATION_LATITUDE"));
	// deviceInfos.setLongitude(rs
	// .getDouble("GEOLOCATION_LANGITUDE"));
	// deviceInfos.setSnapShotId(rs.getString("SNAPSHOT_ID"));
	// deviceInfos.setCellLocationCID(rs
	// .getString("CELLLOCATION_CID"));
	// deviceInfos.setCellLocationLAC(rs
	// .getString("CELLLOCATION_LAC"));
	// deviceInfos.setDevicePhoneType(rs
	// .getString("DEVICE_PHONETYPE"));
	//
	// deviceInfos.setSignalStrengthSnr(rs
	// .getString("SIGNALSTRENGTH_EVDOSNR"));
	//
	// deviceInfos.setSignalStrengthCDMACIO(rs
	// .getString("SIGNALSTRENGTH_CDMACIO"));
	// deviceInfos.setSignalStrengthEVDOECIO(rs
	// .getString("SIGNALSTRENGTH_EVDOECIO"));
	//
	// // deviceInfos.set
	// deviceInfos.setSignalStrengthLTECQI(rs
	// .getString("SIGNALSTRENGTH_LTECQI"));
	//
	// deviceInfos.setDeviceManufacturer(rs
	// .getString("DEVICE_MANUFACTURER"));
	//
	// CallSetUpTo cto = new CallSetUpTo();
	// cto.setDeviceInfoTO(deviceInfos);
	// allcallSetUpList.add(cto);
	// }
	// }
	// } catch (SQLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return allcallSetUpList;
	// }

	public static void insertIntoPreCalTable(String marketId, String testName,
			String voiceConnectivityValue, String chartType, String networkType) {
		voiceConnectivityValue = voiceConnectivityValue.substring(1,
				voiceConnectivityValue.length() - 1);
		// //System.out.println("inserrrrr");
		String insertQuery = "INSERT INTO pre_calc_voiceconnectivity VALUES ('"
				+ marketId + "', '" + testName + "', '" + chartType + "', '"
				+ voiceConnectivityValue + "','" + networkType + "')";
		// //System.out.println("insertQuery--------"+insertQuery);
		try {
			// ////System.out.println("insertQuery-------"+insertQuery);
			st.executeUpdate(insertQuery);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static void insertIntoPreCalTableCalRentention(String marketId,
			String testName, CallretentionTo callretentionTo, String networkType) {
		// //System.out.println("inserrrrr");
		String insertQuery = "INSERT INTO pre_cal_callretention VALUES ('"
				+ marketId + "', '" + testName + "', 'totalcalls', '"
				+ callretentionTo.getTotalCalls() + "','" + networkType + "')";
		// //System.out.println("insertQuery--------"+insertQuery);
		try {
			// ////System.out.println("insertQuery-------"+insertQuery);
			st.executeUpdate(insertQuery);
			insertQuery = "INSERT INTO pre_cal_callretention VALUES ('"
					+ marketId + "', '" + testName + "', 'dropcalls', '"
					+ callretentionTo.getDropCalls() + "','" + networkType
					+ "')";
			st.executeUpdate(insertQuery);
			insertQuery = "INSERT INTO pre_cal_callretention VALUES ('"
					+ marketId + "', '" + testName + "', 'dcr', '"
					+ callretentionTo.getDcr() + "','" + networkType + "')";
			st.executeUpdate(insertQuery);
			insertQuery = "INSERT INTO pre_cal_callretention VALUES ('"
					+ marketId + "', '" + testName + "', 'access failure', '"
					+ callretentionTo.getAccess_failure() + "','" + networkType
					+ "')";
			st.executeUpdate(insertQuery);
			insertQuery = "INSERT INTO pre_cal_callretention VALUES ('"
					+ marketId + "', '" + testName + "', 'missed calls', '"
					+ callretentionTo.getMissedCalls() + "','" + networkType
					+ "')";
			st.executeUpdate(insertQuery);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static void insertIntoPrimaryPreCalTable(String marketId,
			String testName, List<CallSetUpTo> voiceConnectivityValueList,
			String chartType) {
		// //System.out.println("inserrrrr");

		try {
			// ////System.out.println("insertQuery-------"+insertQuery);
			for (int i = 0; i < voiceConnectivityValueList.size(); i++) {
				CallSetUpTo vto = voiceConnectivityValueList.get(i);

				/**
				 * The below if condition is exclusively for chartType call set
				 * up because, In the list we are 'voiceConnectivityValueList',
				 * the every alternate events are 'callsetup' and 'connected'.
				 * 
				 */
				if (chartType.equalsIgnoreCase("Placing Call")) {
					String insertQuery = "INSERT INTO pre_calc_voiceconnectivity_1 (MARKET_ID,TEST_NAME,CHART_TYPE,TIMESTAMP,IMEI	"
							+ ",TEST_TYPE,	USER_NAME	,DEVICE_PHONENUMBER,	DEVICE_PHONETYPE,	"
							+ "DEVICE_MANUFACTURER,	DEVICE_VERSION,	TIME_STAMP_FOREACH_SAMPLE,	NETWORK_NETWORKOPERATOR,	NETWORK_NETWORKOPERATORNAME,"
							+ "	NETWORK_NETWORKTYPE	,NETWORK_DATASTATE,	NETWORK_DATAACTIVITY,	NETWORK_WIFISTATE,	NETWORK_ROAMING,	NETWORK_MCC	,NETWORK_MNC	"
							+ ",SIGNALSTRENGTH_GSMSIGNALSTRENGTH,	SIGNALSTRENGTH_CDMADBM,	SIGNALSTRENGTH_CDMACIO,	SIGNALSTRENGTH_EVDODBM,	SIGNALSTRENGTH_EVDOECIO	"
							+ ",SIGNALSTRENGTH_EVDOSNR,	SIGNALSTRENGTH_GSM,	SIGNALSTRENGTH_GSMBITRATEERROR,	SIGNALSTRENGTH_LTESIGNALSTRENGTH,	"
							+ "SIGNALSTRENGTH_LTERSRP,	SIGNALSTRENGTH_LTERSRQ,	SIGNALSTRENGTH_LTERSSNR,	SIGNALSTRENGTH_LTECQI,	CELLLOCATION_CID,	"
							+ "CELLLOCATION_LAC,	NEIGHBOUR_INFO,	BATTERY_LEVEL,	NETWORK_MANUALLY_DONE,	GEOLOCATION_LATITUDE,	GEOLOCATION_LONGITUDE,"
							+ "		SNAPSHOT_ID) VALUES  ('"
							+ marketId
							+ "', '"
							+ testName
							+ "', '"
							+ chartType
							+ "', '"
							+ vto.getTimeStamp()
							+ "','"
							+ vto.getDeviceInfoTO().getImei()
							+ "','"
							+ vto.getDeviceInfoTO().getTestType()
							+ "','"
							+ vto.getDeviceInfoTO().getUserName()
							+ "',"
							+ "'"
							+ vto.getDeviceInfoTO().getPhoneNumber()
							+ "','"
							+ vto.getDeviceInfoTO().getPhoneType()
							+ "','"
							+ vto.getDeviceInfoTO().getDeviceManufacturer()
							+ "','"
							+ vto.getDeviceInfoTO().getDeviceVersion()
							+ "',"
							+ "'"
							+ vto.getDeviceInfoTO().getTimeStampForEachSample()
							+ "','"
							+ vto.getDeviceInfoTO().getNetworkOperator()
							+ "','"
							+ vto.getDeviceInfoTO().getNetworkOperatorName()
							+ "','"
							+ vto.getDeviceInfoTO().getNetworkType()
							+ "',"
							+ "'"
							+ vto.getDeviceInfoTO().getNetworkDataState()
							+ "','"
							+ vto.getDeviceInfoTO().getDataActivity()
							+ "','"
							+ vto.getDeviceInfoTO().getWifiState()
							+ "','"
							+ vto.getDeviceInfoTO().getNetworkRoaming()
							+ "',"
							+ "'"
							+ vto.getDeviceInfoTO().getNetworkMCC()
							+ "','"
							+ vto.getDeviceInfoTO().getNetworkMNC()
							+ "','"
							+ vto.getDeviceInfoTO().getSignalStrengthGSM()
							+ "','"
							+ vto.getDeviceInfoTO().getSignalStrengthCDMA()
							+ "',"
							+ "'"
							+ vto.getDeviceInfoTO().getSignalStrengthCDMACIO()
							+ "','"
							+ vto.getDeviceInfoTO().getSignalStrengthEVDO()
							+ "','"
							+ vto.getDeviceInfoTO().getSignalStrengthEVDOECIO()
							+ "','"
							+ vto.getDeviceInfoTO().getSignalStrength_EVDOSNR()
							+ "',"
							+ "'"
							+ vto.getDeviceInfoTO().getSignalStrengthGSM()
							+ "','"
							+ vto.getDeviceInfoTO()
									.getSignalStrengthGSMBITRATEERROR()
							+ "','"
							+ vto.getDeviceInfoTO().getSignalStrengthLTE()
							+ "','"
							+ vto.getDeviceInfoTO().getSignalStrengthLTERSRP()
							+ "',"
							+ "'"
							+ vto.getDeviceInfoTO().getSignalStrengthLTERSRQ()
							+ "','"
							+ vto.getDeviceInfoTO().getSignalStrengthLTERSSNR()
							+ "','"
							+ vto.getDeviceInfoTO().getSignalStrength_LTECQI()
							+ "','"
							+ vto.getDeviceInfoTO().getCellLocationCID()
							+ "',"
							+ "'"
							+ vto.getDeviceInfoTO().getCellLocationLAC()
							+ "','"
							+ vto.getDeviceInfoTO().getNeighbourInfo()
							+ "','"
							+ vto.getDeviceInfoTO().getBatteryLevel()
							+ "','"
							+ vto.getDeviceInfoTO().getNetworkManuallyDone()
							+ "',"
							+ "'"
							+ vto.getDeviceInfoTO().getLattitude()
							+ "','"
							+ vto.getDeviceInfoTO().getLongitude()
							+ "','"
							+ vto.getDeviceInfoTO().getSnapShotId()
							+ "')";
					st.executeUpdate(insertQuery);
					i++;
					vto = voiceConnectivityValueList.get(i);
					insertQuery = "INSERT INTO pre_calc_voiceconnectivity_1 (MARKET_ID,TEST_NAME,CHART_TYPE,TIMESTAMP,IMEI	"
							+ ",TEST_TYPE,	USER_NAME	,DEVICE_PHONENUMBER,	DEVICE_PHONETYPE,	"
							+ "DEVICE_MANUFACTURER,	DEVICE_VERSION,	TIME_STAMP_FOREACH_SAMPLE,	NETWORK_NETWORKOPERATOR,	NETWORK_NETWORKOPERATORNAME,"
							+ "	NETWORK_NETWORKTYPE	,NETWORK_DATASTATE,	NETWORK_DATAACTIVITY,	NETWORK_WIFISTATE,	NETWORK_ROAMING,	NETWORK_MCC	,NETWORK_MNC	"
							+ ",SIGNALSTRENGTH_GSMSIGNALSTRENGTH,	SIGNALSTRENGTH_CDMADBM,	SIGNALSTRENGTH_CDMACIO,	SIGNALSTRENGTH_EVDODBM,	SIGNALSTRENGTH_EVDOECIO	"
							+ ",SIGNALSTRENGTH_EVDOSNR,	SIGNALSTRENGTH_GSM,	SIGNALSTRENGTH_GSMBITRATEERROR,	SIGNALSTRENGTH_LTESIGNALSTRENGTH,	"
							+ "SIGNALSTRENGTH_LTERSRP,	SIGNALSTRENGTH_LTERSRQ,	SIGNALSTRENGTH_LTERSSNR,	SIGNALSTRENGTH_LTECQI,	CELLLOCATION_CID,	"
							+ "CELLLOCATION_LAC,	NEIGHBOUR_INFO,	BATTERY_LEVEL,	NETWORK_MANUALLY_DONE,	GEOLOCATION_LATITUDE,	GEOLOCATION_LONGITUDE,"
							+ "		SNAPSHOT_ID) VALUES  ('"
							+ marketId
							+ "', '"
							+ testName
							+ "', '"
							+ "Connected"
							+ "', '"
							+ vto.getTimeStamp()
							+ "','"
							+ vto.getDeviceInfoTO().getImei()
							+ "','"
							+ vto.getDeviceInfoTO().getTestType()
							+ "','"
							+ vto.getDeviceInfoTO().getUserName()
							+ "',"
							+ "'"
							+ vto.getDeviceInfoTO().getPhoneNumber()
							+ "','"
							+ vto.getDeviceInfoTO().getPhoneType()
							+ "','"
							+ vto.getDeviceInfoTO().getDeviceManufacturer()
							+ "','"
							+ vto.getDeviceInfoTO().getDeviceVersion()
							+ "',"
							+ "'"
							+ vto.getDeviceInfoTO().getTimeStampForEachSample()
							+ "','"
							+ vto.getDeviceInfoTO().getNetworkOperator()
							+ "','"
							+ vto.getDeviceInfoTO().getNetworkOperatorName()
							+ "','"
							+ vto.getDeviceInfoTO().getNetworkType()
							+ "',"
							+ "'"
							+ vto.getDeviceInfoTO().getNetworkDataState()
							+ "','"
							+ vto.getDeviceInfoTO().getDataActivity()
							+ "','"
							+ vto.getDeviceInfoTO().getWifiState()
							+ "','"
							+ vto.getDeviceInfoTO().getNetworkRoaming()
							+ "',"
							+ "'"
							+ vto.getDeviceInfoTO().getNetworkMCC()
							+ "','"
							+ vto.getDeviceInfoTO().getNetworkMNC()
							+ "','"
							+ vto.getDeviceInfoTO().getSignalStrengthGSM()
							+ "','"
							+ vto.getDeviceInfoTO().getSignalStrengthCDMA()
							+ "',"
							+ "'"
							+ vto.getDeviceInfoTO().getSignalStrengthCDMACIO()
							+ "','"
							+ vto.getDeviceInfoTO().getSignalStrengthEVDO()
							+ "','"
							+ vto.getDeviceInfoTO().getSignalStrengthEVDOECIO()
							+ "','"
							+ vto.getDeviceInfoTO().getSignalStrength_EVDOSNR()
							+ "',"
							+ "'"
							+ vto.getDeviceInfoTO().getSignalStrengthGSM()
							+ "','"
							+ vto.getDeviceInfoTO()
									.getSignalStrengthGSMBITRATEERROR()
							+ "','"
							+ vto.getDeviceInfoTO().getSignalStrengthLTE()
							+ "','"
							+ vto.getDeviceInfoTO().getSignalStrengthLTERSRP()
							+ "',"
							+ "'"
							+ vto.getDeviceInfoTO().getSignalStrengthLTERSRQ()
							+ "','"
							+ vto.getDeviceInfoTO().getSignalStrengthLTERSSNR()
							+ "','"
							+ vto.getDeviceInfoTO().getSignalStrength_LTECQI()
							+ "','"
							+ vto.getDeviceInfoTO().getCellLocationCID()
							+ "',"
							+ "'"
							+ vto.getDeviceInfoTO().getCellLocationLAC()
							+ "','"
							+ vto.getDeviceInfoTO().getNeighbourInfo()
							+ "','"
							+ vto.getDeviceInfoTO().getBatteryLevel()
							+ "','"
							+ vto.getDeviceInfoTO().getNetworkManuallyDone()
							+ "',"
							+ "'"
							+ vto.getDeviceInfoTO().getLattitude()
							+ "','"
							+ vto.getDeviceInfoTO().getLongitude()
							+ "','"
							+ vto.getDeviceInfoTO().getSnapShotId()
							+ "')";
					st.executeUpdate(insertQuery);
				}
				/*
				 * Testing purpose, pre_calc_voiceconnectivity_2 table has been
				 * used to insert all events cycle along with device info
				 * details
				 */
				if (chartType.equalsIgnoreCase("all")) {

					vto = voiceConnectivityValueList.get(i);
					String insertQuery = "INSERT INTO pre_calc_voiceconnectivity_2 (MARKET_ID,TEST_NAME,NETWORK_TYPE,CHART_TYPE,TIMESTAMP,IMEI	"
							+ ",TEST_TYPE,	USER_NAME	,DEVICE_PHONENUMBER,	DEVICE_PHONETYPE,	"
							+ "DEVICE_MANUFACTURER,	DEVICE_VERSION,	TIME_STAMP_FOREACH_SAMPLE,	NETWORK_NETWORKOPERATOR,	NETWORK_NETWORKOPERATORNAME,"
							+ "	NETWORK_NETWORKTYPE	,NETWORK_DATASTATE,	NETWORK_DATAACTIVITY,	NETWORK_WIFISTATE,	NETWORK_ROAMING,	NETWORK_MCC	,NETWORK_MNC	"
							+ ",SIGNALSTRENGTH_GSMSIGNALSTRENGTH,	SIGNALSTRENGTH_CDMADBM,	SIGNALSTRENGTH_CDMACIO,	SIGNALSTRENGTH_EVDODBM,	SIGNALSTRENGTH_EVDOECIO	"
							+ ",SIGNALSTRENGTH_EVDOSNR,	SIGNALSTRENGTH_GSM,	SIGNALSTRENGTH_GSMBITRATEERROR,	SIGNALSTRENGTH_LTESIGNALSTRENGTH,	"
							+ "SIGNALSTRENGTH_LTERSRP,	SIGNALSTRENGTH_LTERSRQ,	SIGNALSTRENGTH_LTERSSNR,	SIGNALSTRENGTH_LTECQI,	CELLLOCATION_CID,	"
							+ "CELLLOCATION_LAC,	NEIGHBOUR_INFO,	BATTERY_LEVEL,	NETWORK_MANUALLY_DONE,	GEOLOCATION_LATITUDE,	GEOLOCATION_LONGITUDE,"
							+ "		SNAPSHOT_ID) VALUES  ('"
							+ marketId
							+ "', '"
							+ testName
							+ "', '"
							+ vto.getDeviceInfoTO().getNetworkType()
							+ "', '"
							+ vto.getEventName()
							+ "', '"
							+ vto.getTimeStamp()
							+ "','"
							+ vto.getDeviceInfoTO().getImei()
							+ "','"
							+ vto.getDeviceInfoTO().getTestType()
							+ "','"
							+ vto.getDeviceInfoTO().getUserName()
							+ "',"
							+ "'"
							+ vto.getDeviceInfoTO().getPhoneNumber()
							+ "','"
							+ vto.getDeviceInfoTO().getPhoneType()
							+ "','"
							+ vto.getDeviceInfoTO().getDeviceManufacturer()
							+ "','"
							+ vto.getDeviceInfoTO().getDeviceVersion()
							+ "',"
							+ "'"
							+ vto.getDeviceInfoTO().getTimeStampForEachSample()
							+ "','"
							+ vto.getDeviceInfoTO().getNetworkOperator()
							+ "','"
							+ vto.getDeviceInfoTO().getNetworkOperatorName()
							+ "','"
							+ vto.getDeviceInfoTO().getNetworkType()
							+ "',"
							+ "'"
							+ vto.getDeviceInfoTO().getNetworkDataState()
							+ "','"
							+ vto.getDeviceInfoTO().getDataActivity()
							+ "','"
							+ vto.getDeviceInfoTO().getWifiState()
							+ "','"
							+ vto.getDeviceInfoTO().getNetworkRoaming()
							+ "',"
							+ "'"
							+ vto.getDeviceInfoTO().getNetworkMCC()
							+ "','"
							+ vto.getDeviceInfoTO().getNetworkMNC()
							+ "','"
							+ vto.getDeviceInfoTO().getSignalStrengthGSM()
							+ "','"
							+ vto.getDeviceInfoTO().getSignalStrengthCDMA()
							+ "',"
							+ "'"
							+ vto.getDeviceInfoTO().getSignalStrengthCDMACIO()
							+ "','"
							+ vto.getDeviceInfoTO().getSignalStrengthEVDO()
							+ "','"
							+ vto.getDeviceInfoTO().getSignalStrengthEVDOECIO()
							+ "','"
							+ vto.getDeviceInfoTO().getSignalStrength_EVDOSNR()
							+ "',"
							+ "'"
							+ vto.getDeviceInfoTO().getSignalStrengthGSM()
							+ "','"
							+ vto.getDeviceInfoTO()
									.getSignalStrengthGSMBITRATEERROR()
							+ "','"
							+ vto.getDeviceInfoTO().getSignalStrengthLTE()
							+ "','"
							+ vto.getDeviceInfoTO().getSignalStrengthLTERSRP()
							+ "',"
							+ "'"
							+ vto.getDeviceInfoTO().getSignalStrengthLTERSRQ()
							+ "','"
							+ vto.getDeviceInfoTO().getSignalStrengthLTERSSNR()
							+ "','"
							+ vto.getDeviceInfoTO().getSignalStrength_LTECQI()
							+ "','"
							+ vto.getDeviceInfoTO().getCellLocationCID()
							+ "',"
							+ "'"
							+ vto.getDeviceInfoTO().getCellLocationLAC()
							+ "','"
							+ vto.getDeviceInfoTO().getNeighbourInfo()
							+ "','"
							+ vto.getDeviceInfoTO().getBatteryLevel()
							+ "','"
							+ vto.getDeviceInfoTO().getNetworkManuallyDone()
							+ "',"
							+ "'"
							+ vto.getDeviceInfoTO().getLattitude()
							+ "','"
							+ vto.getDeviceInfoTO().getLongitude()
							+ "','"
							+ vto.getDeviceInfoTO().getSnapShotId()
							+ "')";
					// System.out
					// .println("********************************INSERT***"
					// + insertQuery);
					st.executeUpdate(insertQuery);

				}

			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static void insertIntoPrimaryPreCalRententionTable(String marketId,
			String testName, List<CallSetUpTo> voiceConnectivityValueList,
			String chartType) {
		// //System.out.println("inserrrrr");
		String eventName = "";
		try {
			// ////System.out.println("insertQuery-------"+insertQuery);
			for (int i = 0; i < voiceConnectivityValueList.size(); i++) {
				CallSetUpTo vto = voiceConnectivityValueList.get(i);

				if (chartType.equals("BOTH_CALLS"))
					eventName = vto.getEventName();
				else
					eventName = chartType;

				String insertQuery = "INSERT INTO pre_cal_callretention_1 (MARKET_ID,TEST_NAME,PARAMETER,PARAMETER_VALUE,IMEI	"
						+ ",TEST_TYPE,	USER_NAME	,DEVICE_PHONENUMBER,	DEVICE_PHONETYPE,	"
						+ "DEVICE_MANUFACTURER,	DEVICE_VERSION,	TIME_STAMP_FOREACH_SAMPLE,	NETWORK_NETWORKOPERATOR,	NETWORK_NETWORKOPERATORNAME,"
						+ "	NETWORK_NETWORKTYPE	,NETWORK_DATASTATE,	NETWORK_DATAACTIVITY,	NETWORK_WIFISTATE,	NETWORK_ROAMING,	NETWORK_MCC	,NETWORK_MNC	"
						+ ",SIGNALSTRENGTH_GSMSIGNALSTRENGTH,	SIGNALSTRENGTH_CDMADBM,	SIGNALSTRENGTH_CDMACIO,	SIGNALSTRENGTH_EVDODBM,	SIGNALSTRENGTH_EVDOECIO	"
						+ ",SIGNALSTRENGTH_EVDOSNR,	SIGNALSTRENGTH_GSM,	SIGNALSTRENGTH_GSMBITRATEERROR,	SIGNALSTRENGTH_LTESIGNALSTRENGTH,	"
						+ "SIGNALSTRENGTH_LTERSRP,	SIGNALSTRENGTH_LTERSRQ,	SIGNALSTRENGTH_LTERSSNR,	SIGNALSTRENGTH_LTECQI,	CELLLOCATION_CID,	"
						+ "CELLLOCATION_LAC,	NEIGHBOUR_INFO,	BATTERY_LEVEL,	NETWORK_MANUALLY_DONE,	GEOLOCATION_LATITUDE,	GEOLOCATION_LONGITUDE,"
						+ "		SNAPSHOT_ID) VALUES  ('"
						+ marketId
						+ "', '"
						+ testName
						+ "', '"
						+ eventName
						+ "', '"
						+ vto.getTimeStamp()
						+ "','"
						+ vto.getDeviceInfoTO().getImei()
						+ "','"
						+ vto.getDeviceInfoTO().getTestType()
						+ "','"
						+ vto.getDeviceInfoTO().getUserName()
						+ "',"
						+ "'"
						+ vto.getDeviceInfoTO().getPhoneNumber()
						+ "','"
						+ vto.getDeviceInfoTO().getPhoneType()
						+ "','"
						+ vto.getDeviceInfoTO().getDeviceManufacturer()
						+ "','"
						+ vto.getDeviceInfoTO().getDeviceVersion()
						+ "',"
						+ "'"
						+ vto.getDeviceInfoTO().getTimeStampForEachSample()
						+ "','"
						+ vto.getDeviceInfoTO().getNetworkOperator()
						+ "','"
						+ vto.getDeviceInfoTO().getNetworkOperatorName()
						+ "','"
						+ vto.getDeviceInfoTO().getNetworkType()
						+ "',"
						+ "'"
						+ vto.getDeviceInfoTO().getNetworkDataState()
						+ "','"
						+ vto.getDeviceInfoTO().getDataActivity()
						+ "','"
						+ vto.getDeviceInfoTO().getWifiState()
						+ "','"
						+ vto.getDeviceInfoTO().getNetworkRoaming()
						+ "',"
						+ "'"
						+ vto.getDeviceInfoTO().getNetworkMCC()
						+ "','"
						+ vto.getDeviceInfoTO().getNetworkMNC()
						+ "','"
						+ vto.getDeviceInfoTO().getSignalStrengthGSM()
						+ "','"
						+ vto.getDeviceInfoTO().getSignalStrengthCDMA()
						+ "',"
						+ "'"
						+ vto.getDeviceInfoTO().getSignalStrengthCDMACIO()
						+ "','"
						+ vto.getDeviceInfoTO().getSignalStrengthEVDO()
						+ "','"
						+ vto.getDeviceInfoTO().getSignalStrengthEVDOECIO()
						+ "','"
						+ vto.getDeviceInfoTO().getSignalStrength_EVDOSNR()
						+ "',"
						+ "'"
						+ vto.getDeviceInfoTO().getSignalStrengthGSM()
						+ "','"
						+ vto.getDeviceInfoTO()
								.getSignalStrengthGSMBITRATEERROR()
						+ "','"
						+ vto.getDeviceInfoTO().getSignalStrengthLTE()
						+ "','"
						+ vto.getDeviceInfoTO().getSignalStrengthLTERSRP()
						+ "',"
						+ "'"
						+ vto.getDeviceInfoTO().getSignalStrengthLTERSRQ()
						+ "','"
						+ vto.getDeviceInfoTO().getSignalStrengthLTERSSNR()
						+ "','"
						+ vto.getDeviceInfoTO().getSignalStrength_LTECQI()
						+ "','"
						+ vto.getDeviceInfoTO().getCellLocationCID()
						+ "',"
						+ "'"
						+ vto.getDeviceInfoTO().getCellLocationLAC()
						+ "','"
						+ vto.getDeviceInfoTO().getNeighbourInfo()
						+ "','"
						+ vto.getDeviceInfoTO().getBatteryLevel()
						+ "','"
						+ vto.getDeviceInfoTO().getNetworkManuallyDone()
						+ "',"
						+ "'"
						+ vto.getDeviceInfoTO().getLattitude()
						+ "','"
						+ vto.getDeviceInfoTO().getLongitude()
						+ "','"
						+ vto.getDeviceInfoTO().getSnapShotId() + "')";
				// System.out.println(insertQuery);
				st.executeUpdate(insertQuery);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static void prepopulateVCData(String testName, String marketId,
			String testType) {
		conn = DBUtil.getConnection();

		try {
			st = conn.createStatement();
			/*
			 * Gets the list of call events details from Log cat table or Call
			 * events table based on test name, later comparison will be done
			 * with device info table w.r.t timestamp
			 */
			Map<String, List<CallSetUpTo>> voiceConnectivityMap = new VoiceConnectivityProccesorHelper2()
					.getcallSetupChartDetails2(testName, testType, marketId);
			/* Applicable to MO testname */
			List callSetupLineChartMoValueList2 = new VoiceConnectivityProccesorHelper2()
					.getCallSetupTime(
							voiceConnectivityMap.get("callSetupDetails"),
							testType).get(testType);
			System.out.println();
			/*
			 * Gets the Placing cycle details based of Call dropped and missed
			 * call events
			 */
			List<List<CallSetUpTo>> allCycles = getAllInLogCatInfoEvents(
					testName, marketId);
			/* Adds callsetup details for line graph at UI */
			insertIntoPreCalTable(marketId, testName,
					callSetupLineChartMoValueList2.toString(), "callsetup",
					"ALL");

			/* Gets the value of Idle details for chart */
			List idleTimeList = getIdleTimeOutOfCoverage(marketId, testName,
					testType);

			/* Adds idle time details for line graph at UI */
			insertIntoPreCalTable(marketId, testName, idleTimeList.toString(),
					"idleTime", "ALL");

			CallretentionTo callretentionTo = getCallRetentionTime(testName,
					marketId, allCycles, testType);
			// //System.out.println("callSetupLineChartMoValueList2---------"+callSetupLineChartMoValueList2);
			insertIntoPreCalTableCalRentention(marketId, testName,
					callretentionTo, "all");
			// System.out.println("33333333333333333333333333");
			insertIntoPrimaryPreCalTable(marketId, testName,
					voiceConnectivityMap.get("callSetupDetails"),
					"Placing Call");
			/* Testing purpose */

			insertIntoPrimaryPreCalTable(marketId, testName,
					voiceConnectivityMap.get("allDetails"), "all");

			insertIntoPrimaryPreCalTable(marketId, testName,
					voiceConnectivityMap.get("callTearDownDetails"),
					"callteardown");
			// populateAllEntriesOfLogCatandDeviceInfo(testName, marketId,
			// testType);
			/*
			 * PreProcessor.getTimeStampsToDelete(PreProcessor.getLogcatEntriesToDelete
			 * (testName,"CALL_DROP",conn),conn);
			 * PreProcessor.getTimeStampsToDelete
			 * (PreProcessor.getLogcatEntriesToDelete
			 * (testName,"OFFHOOK",conn),conn);
			 */

			// LTE CALCULATION
			/*
			 * List callSetupLineChartMoValueList2 = new
			 * VoiceConnectivityProccesorHelper
			 * ().getCallSetupTime4G(testName,marketId).get("mo");
			 * insertIntoPreCalTable
			 * (marketId,testName,callSetupLineChartMoValueList2
			 * .toString(),"callsetup","LTE");
			 * 
			 * CallretentionTo callretentionTo =
			 * getCallRetentionTime4G(testName,marketId);
			 * insertIntoPreCalTableCalRentention
			 * (marketId,testName,callretentionTo,"LTE");
			 */

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static DeviceInfoTO matchinDeviceInfo(String date, String format,
			String testName, String marketId, String test_type,
			String timeStampIdentifier) {
		DeviceInfoTO deviceInfos = new DeviceInfoTO();
		// SimpleDateFormat sdfformat = new SimpleDateFormat(format);
		ResultSet rs = null;
		Connection conn = DBUtil.openConn();

		String query = "select * from (" +

		"(select * from stg_device_info where TEST_NAME  LIKE  '"
				+ testName
				+ "%' AND TEST_TYPE='"
				+ test_type
				+ "' AND str_to_date(TIME_STAMP_FOREACH_SAMPLE,'%Y-%m-%d %H:%i:%s') > str_to_date('"
				+ date
				+ "',"
				+ format
				+ ") AND TEST_IDENTIFIER_TIMESTAMP = '"
				+ timeStampIdentifier
				+ "' AND MARKET_ID = '"
				+ marketId
				+ "' ORDER BY str_to_date(TIME_STAMP_FOREACH_SAMPLE,'%Y-%m-%d %H:%i:%s')  ASC LIMIT 1)"
				+ "union"
				+ "(select * from stg_device_info where TEST_NAME  LIKE  '"
				+ testName
				+ "%' AND TEST_TYPE='"
				+ test_type
				+ "' AND str_to_date(TIME_STAMP_FOREACH_SAMPLE,'%Y-%m-%d %H:%i:%s') < str_to_date('"
				+ date
				+ "',"
				+ format
				+ ") AND TEST_IDENTIFIER_TIMESTAMP = '"
				+ timeStampIdentifier
				+ "' AND MARKET_ID = '"
				+ marketId
				+ "' ORDER BY str_to_date(TIME_STAMP_FOREACH_SAMPLE,'%Y-%m-%d %H:%i:%s')  DESC LIMIT 1)"
				+

				") as temp order by str_to_date(TIME_STAMP_FOREACH_SAMPLE,'%Y-%m-%d %H:%i:%s') desc limit 1";

		try {
			Statement stmt = conn.createStatement();
			// //System.out.println(query);
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				String networkType = rs.getString("NETWORK_TYPE");
				int rsrp = 0;
				if (rs.getString("SIGNALSTRENGTH_LTERSRP").equalsIgnoreCase(
						"Empty")) {
					rsrp = rs.getInt("SIGNALSTRENGTH_LTERSRP");
				}
				if (rsrp > 1000) {
					// System.out.println(rsrp);
					System.out.println(!(networkType.contains("LTE"))
							|| ((networkType.contains("LTE")) && rsrp < 1000));
				}
				if (!(networkType.contains("LTE"))
						|| ((networkType.contains("LTE")) && rsrp < 1000)) {
					deviceInfos.setTestName(rs.getString("TEST_NAME"));
					deviceInfos.setNetworkType(rs.getString("NETWORK_TYPE"));
					deviceInfos.setNetworkDataState(rs
							.getString("NETWORK_DATASTATE"));
					deviceInfos.setNetworkRoaming(rs
							.getString("NETWORK_ROAMING"));
					if (networkType.contains("LTE")) {
						deviceInfos.setSignalStrength(rs
								.getString("SIGNALSTRENGTH_LTERSRP"));
					} else {
						deviceInfos.setSignalStrength(rs
								.getString("SIGNALSTRENGTH_GSMSIGNALSTRENGTH"));
					}

					/*
					 * deviceInfos.setSignalStrength(rs
					 * .getString("SIGNALSTRENGTH_LTERSRP"));
					 */
					deviceInfos.setImei(rs.getString("DEVICE_IMEI"));
					deviceInfos.setTestType(rs.getString("TEST_TYPE"));
					deviceInfos.setUserName(rs.getString("USER_NAME"));
					deviceInfos.setUserName(rs.getString("USER_NAME"));
					deviceInfos.setPhoneNumber(rs
							.getString("DEVICE_PHONENUMBER"));
					deviceInfos.setPhoneType(rs.getString("DEVICE_PHONETYPE"));
					deviceInfos.setDeviceName(rs.getString("DEVICE_MODEL"));
					deviceInfos
							.setDeviceVersion(rs.getString("DEVICE_VERSION"));
					deviceInfos.setTimeStampForEachSample(rs
							.getString("TIME_STAMP_FOREACH_SAMPLE"));
					deviceInfos.setNetworkOperator(rs
							.getString("NETWORK_NETWORKOPERATOR"));
					deviceInfos.setNetworkOperatorName(rs
							.getString("NETWORK_NETWORKOPERATORNAME"));
					deviceInfos.setDataState(rs.getString("NETWORK_DATASTATE"));
					deviceInfos.setDataActivity(rs
							.getString("NETWORK_DATAACTIVITY"));
					deviceInfos.setWifiState(rs.getString("NETWORK_WIFISTATE"));
					deviceInfos.setNetworkRoaming(rs
							.getString("NETWORK_ROAMING"));
					deviceInfos.setNetworkMCC(rs.getString("NETWORK_MCC"));
					deviceInfos.setNetworkMNC(rs.getString("NETWORK_MNC"));
					deviceInfos.setSignalStrengthGSM(rs
							.getString("SIGNALSTRENGTH_GSMSIGNALSTRENGTH"));
					deviceInfos.setSignalStrengthCDMA(rs
							.getString("SIGNALSTRENGTH_CDMADBM"));
					deviceInfos.setSignalStrengthCDMACIO(rs
							.getString("SIGNALSTRENGTH_CDMACIO"));

					deviceInfos.setSignalStrengthEVDO(rs
							.getString("SIGNALSTRENGTH_EVDODBM"));
					deviceInfos.setSignalStrengthEVDOECIO(rs
							.getString("SIGNALSTRENGTH_EVDOECIO"));
					deviceInfos.setSignalStrength_EVDOSNR(rs
							.getString("SIGNALSTRENGTH_EVDOSNR"));
					deviceInfos
							.setSignalStrengthGSMBITRATEERROR("SIGNALSTRENGTH_GSMBITRATEERROR");

					deviceInfos.setSignalStrengthLTE(rs
							.getString("SIGNALSTRENGTH_LTESIGNALSTRENGTH"));
					deviceInfos.setSignalStrengthLTERSRP(rs
							.getString("SIGNALSTRENGTH_LTERSRP"));
					deviceInfos.setSignalStrengthLTERSRQ(rs
							.getString("SIGNALSTRENGTH_LTERSRQ"));
					deviceInfos.setSignalStrengthLTERSSNR(rs
							.getString("SIGNALSTRENGTH_LTERSSNR"));
					deviceInfos.setSignalStrengthLTECQI(rs
							.getString("SIGNALSTRENGTH_LTERSSNR"));
					deviceInfos.setCellLocationCID(rs
							.getString("CELLLOCATION_CID"));
					deviceInfos.setCellLocationLAC(rs
							.getString("CELLLOCATION_LAC"));
					deviceInfos
							.setNeighbourInfo(rs.getString("NEIGHBOUR_INFO"));
					deviceInfos.setBatteryLevel(rs.getString("BATTERY_LEVEL"));
					deviceInfos.setNetworkManuallyDone(rs
							.getString("NETWORK_MANUALLY_DONE"));

					deviceInfos.setLattitude(rs
							.getDouble("GEOLOCATION_LATITUDE"));
					deviceInfos.setLongitude(rs
							.getDouble("GEOLOCATION_LANGITUDE"));
					deviceInfos.setSnapShotId(rs.getString("SNAPSHOT_ID"));
					deviceInfos.setCellLocationCID(rs
							.getString("CELLLOCATION_CID"));
					deviceInfos.setCellLocationLAC(rs
							.getString("CELLLOCATION_LAC"));
					deviceInfos.setDevicePhoneType(rs
							.getString("DEVICE_PHONETYPE"));

					deviceInfos.setSignalStrengthSnr(rs
							.getString("SIGNALSTRENGTH_EVDOSNR"));

					deviceInfos.setSignalStrengthCDMACIO(rs
							.getString("SIGNALSTRENGTH_CDMACIO"));
					deviceInfos.setSignalStrengthEVDOECIO(rs
							.getString("SIGNALSTRENGTH_EVDOECIO"));

					// deviceInfos.set
					deviceInfos.setSignalStrengthLTECQI(rs
							.getString("SIGNALSTRENGTH_LTECQI"));

					deviceInfos.setDeviceManufacturer(rs
							.getString("DEVICE_MANUFACTURER"));
					deviceInfos.setNetworkType(rs.getString("NETWORK_TYPE"));

				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return deviceInfos;
	}

	public static void populateAllEntriesOfLogCatandDeviceInfo(String testname,
			String marketId, String testType) {
		String alllogCatEntriesQuery = "SELECT * FROM stg_log_cat_info WHERE TEST_NAME LIKE '"
				+ testname
				+ "-%' AND TEST_TYPE = '"
				+ testType
				+ "' order by time_stamp";
		Connection conn = DBUtil.openConn();
		List<CallSetUpTo> callsetUpList = new ArrayList<CallSetUpTo>();
		Set<String> matchedDeviceInfoTimeStamps = new HashSet<String>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(alllogCatEntriesQuery);
			while (rs.next()) {

				CallSetUpTo cst = new CallSetUpTo();
				String timeStamp = rs.getString("TIME_STAMP");
				String timeStampIdentifier = rs
						.getString("TEST_IDENTIFIER_TIMESTAMP");
				String eventName = rs.getString("EVENT_NAME");
				if (null != timeStamp) {
					cst.setEventName(eventName);
					cst.setTimeStamp(timeStamp);

					cst.setTimeStampIdentifier(timeStampIdentifier);
					// call for unmatchedDeviceInfo method if dto is null
					DeviceInfoTO dto = matchinDeviceInfo(timeStamp,
							"'%Y-%m-%d %H:%i:%s'", testname, marketId,
							testType, cst.getTimeStampIdentifier());
					cst.setDeviceInfoTO(dto);
					cst.setNetworkType(dto.getNetworkType());
					matchedDeviceInfoTimeStamps.add("'"
							+ dto.getTimeStampForEachSample() + "'");
					/*
					 * In case there is no device info match then we dont add to
					 * the list
					 */
					if (dto.getTimeStampForEachSample() != null)
						callsetUpList.add(cst);
				}
			}
			String stringValdates = matchedDeviceInfoTimeStamps.toString();
			stringValdates = stringValdates.substring(1, stringValdates
					.length() - 1);
			if (stringValdates.length() > 0) {
				callsetUpList.addAll(getNonMatchinDeviceInfo(testname,
						marketId, testType, stringValdates));
			}

			st = stmt;
			insertIntoPrimaryPreCalTable(marketId, testname, callsetUpList,
					"all");
			// System.out.println(callsetUpList.size());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public HashMap<String, List<Integer>> getCallSetupTime(
			List<CallSetUpTo> callSetetailsList, String testType) {
		HashMap<String, List<Integer>> callSetupList = new HashMap<String, List<Integer>>();
		List<Integer> callSetUpMO = new ArrayList<Integer>();
		List<Integer> callSetUpLogcatMO = new ArrayList<Integer>();
		try {
			int callsetupiListSize = callSetetailsList.size();
			for (int j = 0; j < callsetupiListSize; j++) {
				int timeDiff = diffTimeStamp(callSetetailsList.get(j + 1)
						.getTimeStamp(), callSetetailsList.get(j)
						.getTimeStamp());
				/**
				 * consider only if thr time stamp is greater than 2 secs and
				 * less the 60secs
				 */
				if (timeDiff / 1000 > 2 && timeDiff / 1000 < 60) {
					callSetUpMO.add(timeDiff);
				}
				callSetUpLogcatMO.add(timeDiff);
				// }
				j++;
			}
			if (callSetUpMO.size() > 0) {
				callSetupList.put(testType, callSetUpMO);
			} else {
				callSetupList.put(testType, callSetUpLogcatMO);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return callSetupList;
	}

	public static int diffTimeStamp(String starttime, String endTime) {
		SimpleDateFormat format = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SSS");
		// System.out.println("endTime---------" + endTime);
		// System.out.println("starttime---------" + starttime);
		/*
		 * starttime = "2014-06-20 13:25:40.0"; endTime =
		 * "2014-06-20 13:26:05.0";
		 */
		Date d1 = null;
		Date d2 = null;
		int seconds = 0;
		try {
			d1 = format.parse(starttime);
			d2 = format.parse(endTime);
			long diff = d1.getTime() - d2.getTime();
			long diffMilliSeconds = diff / 1000 % 60 % 60;
			long diffSeconds = diff / 1000 % 60;
			long diffMinutes = diff / (60 * 1000) % 60;
			long diffHours = diff / (60 * 60 * 1000) % 24;
			long diffDays = diff / (24 * 60 * 60 * 1000);
			seconds = Math.abs((int) diff);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println("seconds---------" + seconds);
		return seconds;
	}

	public HashMap<String, List<Integer>> getCallTearDownTime(
			List<CallSetUpTo> callSetetailsList, String testType) {
		HashMap<String, List<Integer>> callSetupList = new HashMap<String, List<Integer>>();
		List<Integer> callSetUpMO = new ArrayList<Integer>();
		try {
			// ////System.out.println("callSetetailsList------++------"+callSetetailsList.size());
			int callsetupiListSize = callSetetailsList.size();
			for (int j = 0; j < callsetupiListSize; j++) {
				// if(diffTimeStamp(callSetetailsList.get(j).getTimeStamp(),callSetetailsList.get(j+1).getTimeStamp())>0){
				// ////System.out.println("callSetetailsList.get(j).getTimeStamp()----------"+callSetetailsList.get(j).getTimeStamp());
				// ////System.out.println("callSetetailsList.get(j+1).getTimeStamp()----------"+callSetetailsList.get(j+1).getTimeStamp());
				callSetUpMO.add(diffTimeStamp(callSetetailsList.get(j)
						.getTimeStamp(), callSetetailsList.get(j + 1)
						.getTimeStamp()));
				// }
				j++;
			}
			callSetupList.put(testType, callSetUpMO);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return callSetupList;
	}

	public static List<Integer> getIdleTimeOutOfCoverage(String marketId,
			String testName, String testType) {
		List<String> idleTimeList = new ArrayList<String>();
		List<Integer> idleTimeDiffList = new ArrayList<Integer>();
		idleTimeList = new VoiceConnectivityProccesorHelper2().getIdleTime(
				marketId, testName, testType);
		// //System.out.println("idleTimeList------------"+idleTimeList);
		ResultSet rs = null;
		for (int i = 0; i < idleTimeList.size(); i++) {
			String idleTime = idleTimeList.get(i);
			try {
				String getIdleTimeQuery = " select TIMESTAMPDIFF(microsecond,'"
						+ idleTime
						+ "',TIME_STAMP_FOREACH_SAMPLE) AS DIFF "
						+ " FROM stg_device_info  "
						+ "WHERE   MARKET_ID = '"
						+ marketId
						+ "'  AND TEST_TYPE = '"
						+ testType
						+ "' AND SIGNALSTRENGTH_GSMSIGNALSTRENGTH>5 AND test_name LIKE '"
						+ testName
						+ "AND str_to_date(TIME_STAMP_FOREACH_SAMPLE,'%Y-%m-%d %H:%i:%s') > str_to_date('"
						+ idleTime
						+ "','%Y-%m-%d %H:%i:%s') "
						+ " AND SIGNALSTRENGTH_GSMSIGNALSTRENGTH>5 ORDER BY TIME_STAMP_FOREACH_SAMPLE LIMIT 1";

				// //System.out.println("getIdleTimeQuery--------"+getIdleTimeQuery);
				rs = st.executeQuery(getIdleTimeQuery);
				while (rs.next()) {
					float idleTimeDiff = rs.getFloat("DIFF") / 1000000;
					idleTimeDiffList.add(new Float(idleTimeDiff).intValue());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return idleTimeDiffList;
	}

	public static DeviceInfoTO matchinDeviceInfo(String date, String format,
			String testName, String marketId, String test_type) {
		DeviceInfoTO deviceInfos = new DeviceInfoTO();
		// SimpleDateFormat sdfformat = new SimpleDateFormat(format);
		ResultSet rs = null;
		Connection conn = DBUtil.openConn();

		String query = "select * from ("
				+

				"(select * from stg_device_info where TEST_NAME  LIKE  '"
				+ testName
				+ "%' AND TEST_TYPE='"
				+ test_type
				+ "' AND str_to_date(TIME_STAMP_FOREACH_SAMPLE,'%Y-%m-%d %H:%i:%s') > str_to_date('"
				+ date
				+ "',"
				+ format
				+ ") ORDER BY str_to_date(TIME_STAMP_FOREACH_SAMPLE,'%Y-%m-%d %H:%i:%s')  ASC LIMIT 1)"
				+ "union"
				+ "(select * from stg_device_info where TEST_NAME  LIKE  '"
				+ testName
				+ "%' AND TEST_TYPE='"
				+ test_type
				+ "' AND str_to_date(TIME_STAMP_FOREACH_SAMPLE,'%Y-%m-%d %H:%i:%s') < str_to_date('"
				+ date
				+ "',"
				+ format
				+ ")  ORDER BY str_to_date(TIME_STAMP_FOREACH_SAMPLE,'%Y-%m-%d %H:%i:%s')  DESC LIMIT 1)"
				+

				") as temp order by str_to_date(TIME_STAMP_FOREACH_SAMPLE,'%Y-%m-%d %H:%i:%s') desc limit 1";

		// System.out.println("query-----------" + query);

		try {
			Statement stmt = conn.createStatement();
			// //System.out.println(query);
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				String networkType = rs.getString("NETWORK_TYPE");
				int rsrp = 0;
				if (rs.getString("SIGNALSTRENGTH_LTERSRP").equalsIgnoreCase(
						"Empty")) {
					rsrp = rs.getInt("SIGNALSTRENGTH_LTERSRP");
				}
				if (!(networkType.contains("LTE"))
						|| ((networkType.contains("LTE")) && rsrp < 1000)) {
					deviceInfos.setTestName(rs.getString("TEST_NAME"));
					deviceInfos.setNetworkType(rs.getString("NETWORK_TYPE"));
					deviceInfos.setNetworkDataState(rs
							.getString("NETWORK_DATASTATE"));
					deviceInfos.setNetworkRoaming(rs
							.getString("NETWORK_ROAMING"));
					if (networkType.contains("LTE")) {
						deviceInfos.setSignalStrength(rs
								.getString("SIGNALSTRENGTH_LTERSRP"));
					} else {
						deviceInfos.setSignalStrength(rs
								.getString("SIGNALSTRENGTH_GSMSIGNALSTRENGTH"));
					}

					/*
					 * deviceInfos.setSignalStrength(rs
					 * .getString("SIGNALSTRENGTH_LTERSRP"));
					 */
					deviceInfos.setImei(rs.getString("DEVICE_IMEI"));
					deviceInfos.setTestType(rs.getString("TEST_TYPE"));
					deviceInfos.setUserName(rs.getString("USER_NAME"));
					deviceInfos.setUserName(rs.getString("USER_NAME"));
					deviceInfos.setPhoneNumber(rs
							.getString("DEVICE_PHONENUMBER"));
					deviceInfos.setPhoneType(rs.getString("DEVICE_PHONETYPE"));
					deviceInfos.setDeviceName(rs.getString("DEVICE_MODEL"));
					deviceInfos
							.setDeviceVersion(rs.getString("DEVICE_VERSION"));
					deviceInfos.setTimeStampForEachSample(rs
							.getString("TIME_STAMP_FOREACH_SAMPLE"));
					deviceInfos.setNetworkOperator(rs
							.getString("NETWORK_NETWORKOPERATOR"));
					deviceInfos.setNetworkOperatorName(rs
							.getString("NETWORK_NETWORKOPERATORNAME"));
					deviceInfos.setDataState(rs.getString("NETWORK_DATASTATE"));
					deviceInfos.setDataActivity(rs
							.getString("NETWORK_DATAACTIVITY"));
					deviceInfos.setWifiState(rs.getString("NETWORK_WIFISTATE"));
					deviceInfos.setNetworkRoaming(rs
							.getString("NETWORK_ROAMING"));
					deviceInfos.setNetworkMCC(rs.getString("NETWORK_MCC"));
					deviceInfos.setNetworkMNC(rs.getString("NETWORK_MNC"));
					deviceInfos.setSignalStrengthGSM(rs
							.getString("SIGNALSTRENGTH_GSMSIGNALSTRENGTH"));
					deviceInfos.setSignalStrengthCDMA(rs
							.getString("SIGNALSTRENGTH_CDMADBM"));
					deviceInfos.setSignalStrengthCDMACIO(rs
							.getString("SIGNALSTRENGTH_CDMACIO"));

					deviceInfos.setSignalStrengthEVDO(rs
							.getString("SIGNALSTRENGTH_EVDODBM"));
					deviceInfos.setSignalStrengthEVDOECIO(rs
							.getString("SIGNALSTRENGTH_EVDOECIO"));
					deviceInfos.setSignalStrength_EVDOSNR(rs
							.getString("SIGNALSTRENGTH_EVDOSNR"));
					deviceInfos
							.setSignalStrengthGSMBITRATEERROR("SIGNALSTRENGTH_GSMBITRATEERROR");

					deviceInfos.setSignalStrengthLTE(rs
							.getString("SIGNALSTRENGTH_LTESIGNALSTRENGTH"));
					deviceInfos.setSignalStrengthLTERSRP(rs
							.getString("SIGNALSTRENGTH_LTERSRP"));
					deviceInfos.setSignalStrengthLTERSRQ(rs
							.getString("SIGNALSTRENGTH_LTERSRQ"));
					deviceInfos.setSignalStrengthLTERSSNR(rs
							.getString("SIGNALSTRENGTH_LTERSSNR"));
					deviceInfos.setSignalStrengthLTECQI(rs
							.getString("SIGNALSTRENGTH_LTERSSNR"));
					deviceInfos.setCellLocationCID(rs
							.getString("CELLLOCATION_CID"));
					deviceInfos.setCellLocationLAC(rs
							.getString("CELLLOCATION_LAC"));
					deviceInfos
							.setNeighbourInfo(rs.getString("NEIGHBOUR_INFO"));
					deviceInfos.setBatteryLevel(rs.getString("BATTERY_LEVEL"));
					deviceInfos.setNetworkManuallyDone(rs
							.getString("NETWORK_MANUALLY_DONE"));

					deviceInfos.setLattitude(rs
							.getDouble("GEOLOCATION_LATITUDE"));
					deviceInfos.setLongitude(rs
							.getDouble("GEOLOCATION_LANGITUDE"));
					deviceInfos.setSnapShotId(rs.getString("SNAPSHOT_ID"));
					deviceInfos.setCellLocationCID(rs
							.getString("CELLLOCATION_CID"));
					deviceInfos.setCellLocationLAC(rs
							.getString("CELLLOCATION_LAC"));
					deviceInfos.setDevicePhoneType(rs
							.getString("DEVICE_PHONETYPE"));

					deviceInfos.setSignalStrengthSnr(rs
							.getString("SIGNALSTRENGTH_EVDOSNR"));

					deviceInfos.setSignalStrengthCDMACIO(rs
							.getString("SIGNALSTRENGTH_CDMACIO"));
					deviceInfos.setSignalStrengthEVDOECIO(rs
							.getString("SIGNALSTRENGTH_EVDOECIO"));

					// deviceInfos.set
					deviceInfos.setSignalStrengthLTECQI(rs
							.getString("SIGNALSTRENGTH_LTECQI"));

					deviceInfos.setDeviceManufacturer(rs
							.getString("DEVICE_MANUFACTURER"));
					deviceInfos.setNetworkType(rs.getString("NETWORK_TYPE"));

				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return deviceInfos;
	}

	public static List<CallSetUpTo> getTestTimeStampInLogCatInfo(
			String test_name, String test_type, String marketId,
			String eventname) {
		List<CallSetUpTo> timeStamplist = new ArrayList<CallSetUpTo>();
		String query = "";
		ResultSet rs = null;
		try {
			query = "SELECT STG.TIME_STAMP  FROM STG_LOG_CAT_INFO STG ,(SELECT TEST_NAME,TEST_TYPE,TEST_IDENTIFIER_TIMESTAMP,MARKET_ID FROM STG_DEVICE_INFO "
					+ "WHERE TEST_NAME LIKE  ('"
					+ test_name
					+ "') AND TEST_TYPE='"
					+ test_type
					+ "' AND MARKET_ID='"
					+ marketId
					+ "'  GROUP BY TEST_IDENTIFIER_TIMESTAMP) ST WHERE "
					+ "ST.TEST_NAME=STG.TEST_NAME  AND ST.TEST_TYPE=STG.TEST_TYPE AND ST.TEST_IDENTIFIER_TIMESTAMP=STG.TEST_IDENTIFIER_TIMESTAMP AND  STG.EVENT_NAME='"
					+ eventname + "'  " + "GROUP BY STG.TIME_STAMP";
			// System.out.println(query);
			rs = st.executeQuery(query);
			while (rs.next()) {
				if (rs.getString(1) != null) {
					CallSetUpTo cto = new CallSetUpTo();
					// timeStamp.add(rs.getString(1));
					String timeStamp = rs.getString(1);
					cto.setTimeStamp(rs.getString(1));
					DeviceInfoTO dto = matchinDeviceInfo(timeStamp,
							"'%Y-%m-%d %H:%i:%s'", test_name, marketId,
							test_type);
					cto.setDeviceInfoTO(dto);
					timeStamplist.add(cto);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		 * finally{ try{ conn.close(); } catch (Exception e) {
		 * e.printStackTrace(); } }
		 */
		return timeStamplist;
	}

	public static CallretentionTo getCallRetentionTime(String test_name,
			String marketId, List<List<CallSetUpTo>> allCycles, String testType) {
		ResultSet rs = null;
		List<CallSetUpTo> New_Outgoing_Call_Time_Stamp = new ArrayList<CallSetUpTo>();
		List<CallSetUpTo> callDropTime_Stamp = new ArrayList<CallSetUpTo>();
		// System.out.println("in call retention");
		List<CallSetUpTo> accessFailure = new ArrayList<CallSetUpTo>();
		CallretentionTo callretentionTo = new CallretentionTo();
		// String test_type = "mo";
		long callDropRate = 0;
		try {

			callretentionTo = getCallDropsAndMissedCalls(allCycles, test_name,
					marketId);
			/* Adding both Missed calls and Placing call events */
			New_Outgoing_Call_Time_Stamp = getTestTimeStampInCallEvents(
					test_name, testType, marketId,
					"'Placing Call', 'Connected','Call Dropped','Missed Call'");

			accessFailure = getTestTimeStampInCallEvents(test_name, testType,
					marketId, "'Error: PlaceCall Failed'");

			callretentionTo.setTotalCalls(Float
					.valueOf(getTotalCalls(test_name)));
			callDropRate = new Double(
					(callretentionTo.getDropCalls() / callretentionTo
							.getTotalCalls()) * 100).longValue();
			callretentionTo.setAccess_failure(accessFailure.size());
			if (callDropRate >= 0) {
				DecimalFormat df = new DecimalFormat("0.00");
				String formate = df.format(callDropRate);
				long finalValue = (Long) df.parse(formate);
				callretentionTo.setDcr(new Float(finalValue));
			} else {
				callretentionTo.setDcr(new Float(callDropRate));
			}
			insertIntoPrimaryPreCalRententionTable(marketId, test_name,
					New_Outgoing_Call_Time_Stamp, "BOTH_CALLS");
			insertIntoPrimaryPreCalRententionTable(marketId, test_name,
					callDropTime_Stamp, "callDrops");
			insertIntoPrimaryPreCalRententionTable(marketId, test_name,
					accessFailure, "Access Failure");
			// ////System.out.println("callRetenctionList-----------"+callRetenctionList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// //System.out.println("callRetentionMO--------"+callRetentionMO);
		return callretentionTo;
	}

	public static List<CallSetUpTo> getTestTimeStampInCallEvents(
			String test_name, String test_type, String marketId,
			String eventname) {
		List<CallSetUpTo> timeStampList = new ArrayList<CallSetUpTo>();
		String query = "";
		ResultSet rs = null;
		try {
			// query =
			// "SELECT STR_TO_DATE(VQuad_Timestamp,'%m/%d/%Y %H:%i:%s') FROM STG_CALLEVENT_RESULTS WHERE TEST_NAME LIKE '%"
			// + test_name
			// + "%' AND CALL_CONTROL_EVENT = '"
			// + eventname
			// + "' AND MARKET_ID = '"
			// + marketId
			// + "' ORDER BY CALL_TIMESTAMP";
			query = "SELECT STR_TO_DATE(CALL_TIMESTAMP,'%m/%d/%Y %H:%i:%s'),CALL_CONTROL_EVENT FROM STG_CALLEVENT_RESULTS WHERE TEST_NAME LIKE '%"
					+ test_name
					+ "%' AND CALL_CONTROL_EVENT IN ("
					+ eventname
					+ ") AND MARKET_ID = '"
					+ marketId
					+ "' ORDER BY CALL_TIMESTAMP";
			// System.out.println("Access---" + query);
			rs = st.executeQuery(query);
			while (rs.next()) {
				if (rs.getString(1) != null) {
					CallSetUpTo cto = new CallSetUpTo();
					// timeStamp.add(rs.getString(1));
					String timeStamp = rs.getString(1);
					cto.setTimeStamp(rs.getString(1));
					cto.setEventName(rs.getString(2));
					DeviceInfoTO dto = matchinDeviceInfo(timeStamp,
							"'%Y-%m-%d %H:%i:%s'", test_name, marketId,
							"externaltest");
					cto.setDeviceInfoTO(dto);
					timeStampList.add(cto);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		 * finally{ try{ conn.close(); } catch (Exception e) {
		 * e.printStackTrace(); } }
		 */
		return timeStampList;
	}

	public static int getTotalCalls(String testName) {

		String query = null;
		ResultSet rs = null;
		int totalCallsCount = 0;
		try {
			query = "SELECT count(*) as totalCall FROM STG_CALLEVENT_RESULTS  PC WHERE PC.TEST_NAME IN ('"
					+ testName
					+ "') AND PC.CALL_CONTROL_EVENT  IN ('Placing Call') ";

			rs = st.executeQuery(query);
			while (rs.next()) {
				totalCallsCount = rs.getInt("totalCall");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return totalCallsCount;

	}

	public static CallretentionTo getCallDropsAndMissedCalls(
			List<List<CallSetUpTo>> allCyclesEvents, String test_name,
			String marketId) {
		/**
		 * For drop calls, there should be two consective call connect and call
		 * disconnect in a single cycle, Rest others are missed call.Thats what
		 * we are doing below
		 * 
		 * if there is calelrid event b/w two connected events then ignore it
		 **/
		CallretentionTo callretentionTo = new CallretentionTo();
		int missedCalls = 0;
		int numberOfCallDrops = 0;
		Set<String> missedCallTime = new HashSet<String>();
		Set<String> callDroppedCallTime = new HashSet<String>();
		for (List<CallSetUpTo> singleCycle : allCyclesEvents) {
			boolean isConnect = false;
			boolean isDisconnect = false;
			String prevEvent = "";
			String calleventTime = "";
			String eventName = "";
			for (CallSetUpTo cto : singleCycle) {
				eventName = cto.getEventName();
				calleventTime = cto.getCalleventsTimeStamp();
				// System.out.println(calleventTime + "-----------" +
				// eventName);
				if (prevEvent.equalsIgnoreCase("Connected")
						&& eventName.equalsIgnoreCase("CallerId"))
					continue;
				if (prevEvent.equalsIgnoreCase("Connected")
						&& eventName.equalsIgnoreCase("Connected")) {
					isConnect = true;
				}
				if (isConnect && prevEvent.equalsIgnoreCase("Disconnected")
						&& eventName.equalsIgnoreCase("Disconnected")) {
					isDisconnect = true;
				}
				prevEvent = eventName;
			}
			// System.out.println(calleventTime + "*************" + eventName);
			if (isConnect && isDisconnect) {
				callDroppedCallTime.add("'" + calleventTime + "'");
				numberOfCallDrops++;
			} else {
				if (calleventTime.length() > 0 && calleventTime != null) {
					missedCallTime.add("'" + calleventTime + "'");
				}
			}
			// System.out.println("numberOfCallDrops-----" + numberOfCallDrops);
		}
		rectifyEventDropCallToMissedCall(missedCallTime, test_name,
				"Missed Call");
		rectifyEventDropCallToMissedCall(callDroppedCallTime, test_name,
				"Call Dropped");
		// missedCalls = allCyclesEvents.size() - numberOfCallDrops;
		missedCalls = missedCallTime.size();
		callretentionTo.setDropCalls(numberOfCallDrops);
		callretentionTo.setMissedCalls(missedCalls);
		return callretentionTo;
	}

	public static void rectifyEventDropCallToMissedCall(
			Set<String> missedCallTimeStamps, String testname, String eventName) {
		String allMissedCalls = "";
		String query = "";
		for (String missedCallTime : missedCallTimeStamps) {
			allMissedCalls = allMissedCalls + "," + missedCallTime;
		}
		if (allMissedCalls.length() > 1) {
			allMissedCalls = allMissedCalls.substring(1);
			try {
				if (eventName.equalsIgnoreCase("Call Dropped")) {
					query = "UPDATE  STG_CALLEVENT_RESULTS SET CALL_CONTROL_EVENT = '"
							+ eventName
							+ "'  WHERE Call_Timestamp IN ("
							+ allMissedCalls
							+ ") AND TEST_NAME = '"
							+ testname
							+ "' AND CALL_CONTROL_EVENT = 'Missed Call'";
				} else {
					query = "UPDATE  STG_CALLEVENT_RESULTS SET CALL_CONTROL_EVENT = '"
							+ eventName
							+ "'  WHERE Call_Timestamp IN ("
							+ allMissedCalls
							+ ") AND TEST_NAME = '"
							+ testname
							+ "' AND CALL_CONTROL_EVENT like '%Call Dropped%'";
				}
				st.executeUpdate(query);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

	}

	public List<String> getIdleTime(String marketId, String testName,
			String testType) {
		List<String> idleTimeList = new ArrayList<String>();
		boolean foundValue = false;
		int prevStrength = 0;
		int currentStrength = 0;
		ResultSet rs = null;
		List<CallSetUpTo> allIdleList = new ArrayList<CallSetUpTo>();

		try {
			String getIdleTimeQuery = " select (SIGNALSTRENGTH_GSMSIGNALSTRENGTH),TIME_STAMP_FOREACH_SAMPLE "
					+ " FROM stg_device_info  "
					+ "WHERE  MARKET_ID = '"
					+ marketId
					+ "' and "
					+ "test_name = '"
					+ testName
					+ "' AND TEST_TYPE = '"
					+ testType
					+ "' "
					+ " ORDER BY TIME_STAMP_FOREACH_SAMPLE";
			// System.out.println("getIdleTimestamp-------" + getIdleTimeQuery);
			rs = st.executeQuery(getIdleTimeQuery);
			while (rs.next()) {
				CallSetUpTo idleTimeTo = new CallSetUpTo();
				currentStrength = rs.getInt("SIGNALSTRENGTH_GSMSIGNALSTRENGTH");
				String idleTime = rs.getString("TIME_STAMP_FOREACH_SAMPLE");

				if (!foundValue && currentStrength < 6) {
					idleTimeList.add(idleTime);
					allIdleList.add(idleTimeTo);
				}
				if (currentStrength < 6) {
					idleTimeTo.setTimeStamp(idleTime);
					foundValue = true;

				} else {
					foundValue = false;
				}

			}
			insertIntoPrimaryPreCalTable(marketId, testName, allIdleList,
					"idleTime");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return idleTimeList;
	}

	public String getTestType(String testName) {
		String testType = "";
		ResultSet rs = null;
		try {
			openConn();
			String query = "SELECT DISTINCT TEST_TYPE as testType FROM STG_DEVICE_INFO WHERE TEST_NAME like '%"
					+ testName + "%'";
			rs = st.executeQuery(query);
			System.out.println("getAllmarkets---------" + query);
			while (rs.next()) {
				testType = rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeConn();
		}
		return testType;
	}

	public static void main(String[] args) {
		new PreprocessorTrigger("nokiatestmo1").triggerPreprocessin();
		// System.out.println(">>>>>"+new
		// VoiceConnectivityProccesorHelper2().getTotalCalls("nokiatestmo1").getTotalCalls());
	}
}
