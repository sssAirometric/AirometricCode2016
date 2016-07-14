package com.preprocessorhelpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import com.preprocessing.PreprocessorTrigger;
import com.report.to.CallretentionTo;
import com.to.CallSetUpTo;
import com.to.DeviceInfoTO;
import com.to.VoiceConnectivityTO;

public class VoiceConnectivityProccesorHelper {

	static Connection conn = null;
	static Statement st = null;
	static HashMap<String, String> propertiesFiledata = PropertyFileReader
			.getProperties();
	public final static String SIGNALSTRENGTH_GSM = propertiesFiledata
			.get("SIGNALSTRENGTH_GSM");
	public final static String SIGNALSTRENGTH_GSM1 = propertiesFiledata
			.get("SIGNALSTRENGTH_GSM1");
	/*
	 * public final static String SIGNALSTRENGTH_LTE = propertiesFiledata
	 * .get("SIGNALSTRENGTH_LTE");
	 */
	public final static String THROUGHPUT = propertiesFiledata
			.get("THROUGHPUT");
	public final static String ServerPort = propertiesFiledata
			.get("ServerPort");
	public final static String QXDM_FILE_PATH = propertiesFiledata
			.get("QXDM_FILE_PATH");
	public final static String ENVIRONMENT = propertiesFiledata
			.get("ENVIRONMENT");

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
			e.printStackTrace();
		}
		return neighbourtStr;
	}

	public List<VoiceConnectivityTO> getAllDeviceInfo(String testName,
			String market_id, String test_type) {
		List<VoiceConnectivityTO> deviceInfosList = new ArrayList<VoiceConnectivityTO>();
		ResultSet rs = null;
		PreparedStatement pst = null;
		String test_name = testName + "\\-%";
		try {
			String deviceInfoQuery = "SELECT  * FROM STG_DEVICE_INFO WHERE TEST_NAME LIKE ('"
					+ test_name
					+ "') AND TEST_TYPE IN ("
					+ test_type
					+ ") AND MARKET_ID IN("
					+ market_id
					+ ") "
					+ "ORDER BY TIME_STAMP_FOREACH_SAMPLE";
			rs = st.executeQuery(deviceInfoQuery);
			while (rs.next()) {
				String networkType = rs.getString("NETWORK_TYPE");
				int rsrp = 0;
				if (rs.getString("SIGNALSTRENGTH_LTERSRP").equalsIgnoreCase(
						"Empty")) {
					rsrp = rs.getInt("SIGNALSTRENGTH_LTERSRP");
				}
				if (!(networkType.contains("LTE"))
						|| ((networkType.contains("LTE")) && rsrp < 1000)) {
					VoiceConnectivityTO deviceInfos = new VoiceConnectivityTO();
					deviceInfos.setTestName(rs.getString("TEST_NAME"));
					int signalStrengthValue = 0;
					deviceInfos.setNetworkType(networkType);
					if (networkType.contains("LTE")) {
						int lteSignal = rs.getInt("SIGNALSTRENGTH_LTERSRP");
						signalStrengthValue = lteSignal;
					} else {
						int lteSignal = rs
								.getInt("SIGNALSTRENGTH_GSMSIGNALSTRENGTH");
						signalStrengthValue = -Integer
								.parseInt(SIGNALSTRENGTH_GSM)
								+ lteSignal;
					}
					deviceInfos.setSignalStrength(new Integer(
							signalStrengthValue).toString());
					deviceInfos.setNetworkDataState(rs
							.getString("NETWORK_DATASTATE"));
					deviceInfos.setNetworkRoaming(rs
							.getString("NETWORK_ROAMING"));
					deviceInfos.setSignalStrengthCDMA(rs
							.getString("SIGNALSTRENGTH_CDMADBM"));
					deviceInfos.setSignalStrengthEVDO(rs
							.getString("SIGNALSTRENGTH_EVDODBM"));
					deviceInfos.setLattitude(rs
							.getDouble("GEOLOCATION_LATITUDE"));
					deviceInfos.setLongitude(rs
							.getDouble("GEOLOCATION_LANGITUDE"));
					deviceInfos.setCellLocationCID(rs
							.getString("CELLLOCATION_CID"));
					deviceInfos.setCellLocationLAC(rs
							.getString("CELLLOCATION_LAC"));
					deviceInfos.setDevicePhoneType(rs
							.getString("DEVICE_PHONETYPE"));
					deviceInfos.setNetworkMCC(rs.getString("NETWORK_MCC"));
					deviceInfos.setNetworkMNC(rs.getString("NETWORK_MNC"));
					deviceInfos.setSignalStrengthSnr(rs
							.getString("SIGNALSTRENGTH_EVDOSNR"));
					deviceInfos.setTimeStampForEachSample(rs
							.getString("TIME_STAMP_FOREACH_SAMPLE"));
					deviceInfos
							.setNeighbourInfo(rs.getString("NEIGHBOUR_INFO"));
					deviceInfos.setSignalStrengthCDMACIO(rs
							.getString("SIGNALSTRENGTH_CDMACIO"));
					deviceInfos.setSignalStrengthEVDOECIO(rs
							.getString("SIGNALSTRENGTH_EVDOECIO"));
					deviceInfos.setSignalStrengthLTE(rs
							.getString("SIGNALSTRENGTH_LTERSRP"));
					deviceInfos.setSignalStrengthLTERSRP(rs
							.getString("SIGNALSTRENGTH_LTERSRP"));
					deviceInfos.setSignalStrengthLTERSRQ(rs
							.getString("SIGNALSTRENGTH_LTERSRQ"));
					deviceInfos.setSignalStrengthLTERSSNR(rs
							.getString("SIGNALSTRENGTH_LTERSSNR"));
					deviceInfos.setSignalStrengthLTECQI(rs
							.getString("SIGNALSTRENGTH_LTECQI"));
					deviceInfosList.add(deviceInfos);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} /*
		 * finally { try { rs.close(); st.close(); conn.close(); } catch
		 * (Exception e) { // TODO: handle exception e.printStackTrace(); } }
		 */
		return deviceInfosList;
	}

	public List<String> getAllDeviceInfoCallDropList(String testCaseName,
			String marketId, String testtype) {
		List<String> deviceInfosList = new ArrayList<String>();
		ResultSet rs = null;
		String test_name = testCaseName + "\\-%";
		PreparedStatement pst = null;
		try {
			String deviceInfoQuery = "SELECT  DISTINCT ST.TIME_STAMP FROM STG_LOG_CAT_INFO ST,STG_DEVICE_INFO SD WHERE "
					+ " ST.TEST_NAME LIKE  '"
					+ test_name
					+ "'AND ST.TEST_TYPE= "
					+ testtype
					+ " AND ST.TEST_TYPE=SD.TEST_TYPE"
					+ " AND ST.TEST_NAME=SD.TEST_NAME AND ST.TEST_IDENTIFIER_TIMESTAMP=SD.TEST_IDENTIFIER_TIMESTAMP AND ST.EVENT_NAME='CALL_DROP' ORDER BY ST.TIME_STAMP ";
			// ////System.out.println("deviceInfoQuery-------------------"+deviceInfoQuery);
			rs = st.executeQuery(deviceInfoQuery);
			while (rs.next()) {
				deviceInfosList.add(rs.getString("TIME_STAMP"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				st.close();
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return deviceInfosList;
	}

	public List<VoiceConnectivityTO> getAllMultiplDeviceInfo(
			String testCaseName, String marketId) {
		List<VoiceConnectivityTO> deviceInfosList = new ArrayList<VoiceConnectivityTO>();
		ResultSet rs = null;
		PreparedStatement pst = null;
		String test_name = testCaseName + "\\-%";
		try {
			String deviceInfoQuery = "SELECT  * FROM STG_DEVICE_INFO WHERE TEST_NAME  IN(SELECT DISTINCT TEST_NAME FROM STG_DEVICE_INFO "
					+ " WHERE TEST_NAME LIKE  '"
					+ test_name
					+ "' AND MARKET_ID= '"
					+ marketId
					+ "' )ORDER BY TIME_STAMP_FOREACH_SAMPLE ";
			rs = st.executeQuery(deviceInfoQuery);
			while (rs.next()) {
				String networkType = rs.getString("NETWORK_TYPE");
				int rsrp = 0;
				if (rs.getString("SIGNALSTRENGTH_LTERSRP").equalsIgnoreCase(
						"Empty")) {
					rsrp = rs.getInt("SIGNALSTRENGTH_LTERSRP");
				}
				if (!(networkType.contains("LTE"))
						|| ((networkType.contains("LTE")) && rsrp < 1000)) {
					VoiceConnectivityTO deviceInfos = new VoiceConnectivityTO();
					deviceInfos.setTestName(rs.getString("TEST_NAME"));
					deviceInfos.setNetworkType(rs
							.getString("NETWORK_NETWORKTYPE"));
					deviceInfos.setNetworkDataState(rs
							.getString("NETWORK_DATASTATE"));
					deviceInfos.setNetworkRoaming(rs
							.getString("NETWORK_ROAMING"));
					deviceInfos.setSignalStrength(rs
							.getString("SIGNALSTRENGTH_GSMSIGNALSTRENGTH"));
					deviceInfos.setLattitude(rs
							.getDouble("GEOLOCATION_LATITUDE"));
					deviceInfos.setLongitude(rs
							.getDouble("GEOLOCATION_LANGITUDE"));
					deviceInfos.setCellLocationCID(rs
							.getString("CELLLOCATION_CID"));
					deviceInfos.setCellLocationLAC(rs
							.getString("CELLLOCATION_LAC"));
					deviceInfos.setDevicePhoneType(rs
							.getString("DEVICE_PHONETYPE"));
					deviceInfos.setNetworkMCC(rs.getString("NETWORK_MCC"));
					deviceInfos.setNetworkMNC(rs.getString("NETWORK_MNC"));
					deviceInfos.setTimeStampForEachSample(rs
							.getString("TIME_STAMP_FOREACH_SAMPLE"));
					deviceInfos
							.setNeighbourInfo(rs.getString("NEIGHBOUR_INFO"));
					deviceInfos.setSignalStrengthLTE(rs
							.getString("SIGNALSTRENGTH_LTERSRP"));
					deviceInfos.setSignalStrengthLTERSRP(rs
							.getString("SIGNALSTRENGTH_LTERSRP"));
					deviceInfos.setSignalStrengthLTERSRQ(rs
							.getString("SIGNALSTRENGTH_LTERSRQ"));
					deviceInfos.setSignalStrengthLTERSSNR(rs
							.getString("SIGNALSTRENGTH_LTERSSNR"));
					deviceInfos.setSignalStrengthLTECQI(rs
							.getString("SIGNALSTRENGTH_LTECQI"));
					deviceInfosList.add(deviceInfos);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} /*
		 * finally { try { rs.close(); st.close(); conn.close(); } catch
		 * (Exception e) { e.printStackTrace(); } }
		 */
		return deviceInfosList;
	}

	public List<String> getAllMultipleDeviceInfoCallDropList(
			String testCaseName, String marketId) {
		List<String> deviceInfosList = new ArrayList<String>();
		ResultSet rs = null;
		String test_name = testCaseName + "\\-%";
		PreparedStatement pst = null;
		String sql = null;
		String deviceInfoQuery = null;
		try {
			sql = "SELECT TEST_NAME,TEST_TYPE,TEST_IDENTIFIER_TIMESTAMP "
					+ " FROM FILE_HISTORY WHERE TEST_NAME='" + test_name
					+ "' AND MARKET_ID='" + marketId + "' AND ACTIVE='1'";
			rs = st.executeQuery(sql);
			while (rs.next()) {
				String test_Name = rs.getString(1);
				String test_type = rs.getString(2);
				String test_identifier_timestamp = rs.getString(3);
				deviceInfoQuery = "SELECT *FROM STG_LOG_CAT_INFO WHERE TEST_NAME IN(SELECT DISTINCT TEST_NAME FROM STG_LOG_CAT_INFO "
						+ " WHERE TEST_NAME LIKE  '"
						+ test_Name
						+ "' AND TEST_TYPE = '"
						+ test_type
						+ "' AND TEST_IDENTIFIER_TIMESTAMP= '"
						+ test_identifier_timestamp
						+ "' "
						+ ")and EVENT_NAME='CALL_DROP' ORDER BY TIME_STAMP ";
				rs = st.executeQuery(deviceInfoQuery);
				while (rs.next()) {
					deviceInfosList.add(rs.getString("TIME_STAMP"));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} /*
		 * finally { try { rs.close(); st.close(); conn.close(); } catch
		 * (Exception e) { e.printStackTrace(); } }
		 */
		return deviceInfosList;
	}

	public String getMarketName(String marketId) {
		String marketName = "";

		ResultSet rs = null;
		PreparedStatement pst = null;
		String deviceId = "";
		try {
			String query = "SELECT MARKET_NAME FROM MARKET WHERE MARKET_ID='"
					+ marketId + "'";
			rs = st.executeQuery(query);
			while (rs.next()) {
				marketName = rs.getString("MARKET_NAME");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		 * finally{ try{ conn.close(); } catch (Exception e) { // TODO: handle
		 * exception e.printStackTrace(); }
		 * 
		 * }
		 */
		return marketName;

	}

	public String getTimeDiff(Date dateOne, Date dateTwo) {
		String diff = "";
		long timeDiff = Math.abs(dateOne.getTime() - dateTwo.getTime());
		diff = String.format("%d hour(s) %d min(s)", TimeUnit.MILLISECONDS
				.toHours(timeDiff), TimeUnit.MILLISECONDS.toMinutes(timeDiff)
				- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
						.toHours(timeDiff)));
		return diff;
	}

	public static List<String> getTestTimeStampInDeviceInfo(String test_name,
			String test_type, String marketId, String network) {
		List<String> timeStamp = new ArrayList<String>();
		String query = "";
		ResultSet rs = null;
		try {
			query = "SELECT DISTINCT ST.TIME_STAMP_FOREACH_SAMPLE FROM STG_DEVICE_INFO ST "
					+ "WHERE ST.TEST_NAME LIKE '"
					+ (test_name)
					+ "'  AND ST.TEST_TYPE='"
					+ test_type
					+ "' "
					+ " AND ST.MARKET_ID IN("
					+ marketId
					+ ") AND ST.NETWORK_TYPE IN('" + network + "')";
			rs = st.executeQuery(query);
			while (rs.next()) {
				timeStamp.add(rs.getString(1));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		 * finally{ try{ conn.close(); } catch (Exception e) {
		 * e.printStackTrace(); } }
		 */
		return timeStamp;
	}

	public static List<String> getTestTimeStampLogCatInfo(String test_name,
			String test_type, String marketId, String eventname) {
		List<String> timeStamp = new ArrayList<String>();
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
			rs = st.executeQuery(query);
			while (rs.next()) {
				timeStamp.add(rs.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		 * finally{ try{ conn.close(); } catch (Exception e) {
		 * e.printStackTrace(); } }
		 */
		return timeStamp;
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

	public static List<CallSetUpTo> getTestTimeStampInCallEvents(
			String test_name, String test_type, String marketId,
			String eventname) {
		List<CallSetUpTo> timeStampList = new ArrayList<CallSetUpTo>();
		String query = "";
		ResultSet rs = null;
		try {
			query = "SELECT STR_TO_DATE(VQuad_Timestamp,'%m/%d/%Y %H:%i:%s'),CALL_CONTROL_EVENT FROM STG_CALLEVENT_RESULTS WHERE TEST_NAME LIKE '%"
					+ test_name
					+ "%' AND CALL_CONTROL_EVENT IN ("
					+ eventname
					+ ") AND MARKET_ID = '"
					+ marketId
					+ "' GROUP BY CALL_TIMESTAMP";
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
							test_type);
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

	public static CallretentionTo getCallDropsMissedCalls(
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
			for (CallSetUpTo cto : singleCycle) {
				String eventName = cto.getEventName();
				calleventTime = cto.getCalleventsTimeStamp();
				// System.out.println(calleventTime + "-----------" +
				// eventName);
				if ((prevEvent.equalsIgnoreCase("Connected") && eventName
						.equalsIgnoreCase("CallerId"))
						|| (prevEvent.equalsIgnoreCase("Connected") && eventName
								.equalsIgnoreCase("PDD event"))
						|| (prevEvent.equalsIgnoreCase("Connected") && eventName
								.equalsIgnoreCase("Incoming Call"))
						|| (prevEvent.equalsIgnoreCase("Connected") && eventName
								.equalsIgnoreCase("Ring Back"))
						|| (prevEvent.equalsIgnoreCase("Connected") && eventName
								.equalsIgnoreCase("RingBack")))
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
			if (isConnect && isDisconnect) {
				numberOfCallDrops++;
				callDroppedCallTime.add("'" + calleventTime + "'");
			} else {
				missedCallTime.add("'" + calleventTime + "'");
			}
			// System.out.println("numberOfCallDrops-----" + numberOfCallDrops);
		}
		// rectifyEventDropCallToMissedCall(missedCallTime, test_name);
		rectifyEventDropCallToMissedCall(missedCallTime, test_name,
				"Missed Call");
		rectifyEventDropCallToMissedCall(callDroppedCallTime, test_name,
				"Call Dropped");
		missedCalls = missedCallTime.size();// ;allCyclesEvents.size() -
											// numberOfCallDrops;
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

	public static List<CallSetUpTo> getAllInLogCatInfo(String test_name,
			String test_type, String marketId, int flag) {
		List<CallSetUpTo> callsetUpList = new ArrayList<CallSetUpTo>();
		String query = "";
		ResultSet rs = null;
		try {
			query = "SELECT  STG.TEST_IDENTIFIER_TIMESTAMP,STG.TIME_STAMP,STG.EVENT_NAME,ST.*  FROM STG_LOG_CAT_INFO STG ,(SELECT TEST_NAME,TEST_TYPE,TEST_IDENTIFIER_TIMESTAMP,NETWORK_TYPE,MARKET_ID FROM STG_DEVICE_INFO "
					+ "WHERE TEST_NAME LIKE  ('"
					+ test_name
					+ "-%') AND TEST_TYPE='"
					+ test_type
					+ "' AND MARKET_ID='"
					+ marketId
					+ "'  GROUP BY TEST_IDENTIFIER_TIMESTAMP) ST WHERE "
					+ "ST.TEST_NAME=STG.TEST_NAME  AND ST.TEST_TYPE=STG.TEST_TYPE AND ST.TEST_IDENTIFIER_TIMESTAMP=STG.TEST_IDENTIFIER_TIMESTAMP   "
					+ "GROUP BY STG.TIME_STAMP";
			 System.out.println("getAllInLogCatInfo - "+query);
			rs = st.executeQuery(query);
			while (rs.next()) {
				CallSetUpTo cst = new CallSetUpTo();
				String timeStamp = rs.getString("TIME_STAMP");
				String timeStampIdentifier = rs
						.getString("TEST_IDENTIFIER_TIMESTAMP");
				String eventName = rs.getString("EVENT_NAME");
				String networkType = rs.getString("NETWORK_TYPE");
				if (null != timeStamp) {
					cst.setEventName(eventName);
					cst.setTimeStamp(timeStamp);
					cst.setNetworkType(networkType);
					cst.setTimeStampIdentifier(timeStampIdentifier);
					DeviceInfoTO dto = matchinDeviceInfo(timeStamp,
							"'%Y-%m-%d %H:%i:%s'", test_name, marketId,
							test_type);
					cst.setDeviceInfoTO(dto);
					callsetUpList.add(cst);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (callsetUpList.size() == 0) {
			callsetUpList = getAllInLogCatInfoFromCallEvents(test_name,
					test_type, marketId, flag);
		}
		// System.out.println("callsetUpList-----srikanth----"
		// + callsetUpList.size());
		return callsetUpList;
	}

	public static List<CallSetUpTo> getAllInLogCatInfoFromCallEvents(
			String test_name, String test_type, String marketId, int flag) {
		List<CallSetUpTo> callsetUpList = new ArrayList<CallSetUpTo>();
		String query = "";
		ResultSet rs = null;
		try {
			// all the details
			if (flag == 1)
				query = "SELECT STR_TO_DATE(VQuad_Timestamp,'%m/%d/%Y %H:%i:%s') AS VQuad_Timestamp ,CALL_CONTROL_EVENT FROM STG_CALLEVENT_RESULTS WHERE TEST_NAME LIKE '%"
						+ test_name
						+ "%' AND MARKET_ID='"
						+ marketId
						+ "'"
						+ " ORDER BY VQuad_Timestamp";
			else
				query = "SELECT STR_TO_DATE(VQuad_Timestamp,'%m/%d/%Y %H:%i:%s') AS VQuad_Timestamp ,CALL_CONTROL_EVENT FROM STG_CALLEVENT_RESULTS WHERE TEST_NAME LIKE '%"
						+ test_name
						+ "%' AND CALL_CONTROL_EVENT IN ('PLACING CALL','Connect from Far End') AND MARKET_ID='"
						+ marketId + "'" + " ORDER BY VQuad_Timestamp";

			// System.out.println(query);
			
//			query = "SELECT VQuad_CallId,STR_TO_DATE(VQuad_Timestamp,'%m/%d/%Y %H:%i:%s') AS VQuad_Timestamp ,CALL_CONTROL_EVENT FROM STG_CALLEVENT_RESULTS WHERE TEST_NAME LIKE '%"
//				+ test_name
//				+ "%' AND CALL_CONTROL_EVENT IN ('PLACING CALL','CONNECTED') AND MARKET_ID='"
//				+ marketId + "'" + " ORDER BY VQuad_Timestamp";

			rs = st.executeQuery(query);
			boolean isPrevEvent_Placing = false;
			while (rs.next()) {
				CallSetUpTo cst = new CallSetUpTo();
				String timeStamp = rs.getString("VQuad_Timestamp");
				String eventName = rs.getString("CALL_CONTROL_EVENT");
				String timeStampIdentifier = rs.getString("CALL_CONTROL_EVENT");
//				if (flag == 1) {
					cst.setEventName(eventName);
//				} else {
//					String vQuad_id = rs.getString("VQuad_CallId");
//					cst
//							.setEventName(eventName + "_"
//									+ vQuad_id.substring(0, 1));
//				}
				isPrevEvent_Placing = false;
				cst.setTimeStamp(timeStamp);
				DeviceInfoTO dto = matchinDeviceInfo(timeStamp,
						"'%Y-%m-%d %H:%i:%s'", test_name, marketId,
						"externaltest");
				cst.setDeviceInfoTO(dto);
				callsetUpList.add(cst);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		 * finally{ try{ conn.close(); } catch (Exception e) {
		 * e.printStackTrace(); } }
		 */
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
			// String tempEvent = "";
			while (rs.previous()) {
				// while(rs.next()){
				CallSetUpTo cst = new CallSetUpTo();
				String calleventTime = rs.getString("Call_Timestamp");
				String timeStamp = rs.getString("VQuad_Timestamp");
				String eventName = rs.getString("CALL_CONTROL_EVENT").trim();
				// //System.out.println(count+"--eventName------"+eventName);
				cst.setEventName(eventName);
				cst.setTimeStamp(timeStamp);
				cst.setCalleventsTimeStamp(calleventTime);
				// if (eventName.equalsIgnoreCase("Placing Call")) {
				// System.out.println("Yes");
				// }
				if (isPrevEvent_Placing
						&& eventName.equalsIgnoreCase("Placing Call")) {
					individulaEventList = new ArrayList<CallSetUpTo>();
					// tempEvent = "";
					// System.out.println("---------------------------");

				}
				if (!isPrevEvent_Placing
						&& eventName.equalsIgnoreCase("Placing Call")) {
					isPrevEvent_Placing = true;
					// tempEvent = eventName;
				}
				individulaEventList.add(cst);
				// System.out.println(tempEvent);
				if (eventName.equalsIgnoreCase("Call Dropped")
						|| eventName.equalsIgnoreCase("Missed Call")) {
					eventCycleList.add(individulaEventList);
					isPrevEvent_Placing = false;
					individulaEventList = new ArrayList<CallSetUpTo>();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println("eventCycleList------------" + eventCycleList);
		return eventCycleList;
	}

	public static String getTestTimeStampIn4GLogCatInfo(String test_name,
			String test_type, String marketId, String network) {
		String timeStamp = "";
		String query = "";
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		try {

			query = "SELECT STG.TIME_STAMP_FOREACH_SAMPLE FROM STG_DEVICE_INFO STG"
					+ " WHERE STG.TEST_NAME LIKE ('"
					+ test_name
					+ "') AND STG.TEST_TYPE='"
					+ test_type
					+ "' AND STG.NETWORK_NETWORKTYPE IN ("
					+ network
					+ ") "
					+ " AND STG.MARKET_ID='"
					+ marketId
					+ "' GROUP BY STG.TIME_STAMP_FOREACH_SAMPLE,STG.NETWORK_NETWORKTYPE";
			// ////System.out.println("query4g---------------"+query);
			rs = st.executeQuery(query);
			while (rs.next()) {
				if (timeStamp == "") {
					timeStamp = rs.getString(1);
				} else {
					timeStamp = timeStamp + "," + rs.getString(1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		 * finally{ try{ conn.close(); } catch (Exception e) {
		 * e.printStackTrace(); } }
		 */
		return timeStamp;
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
			int tempSeconds = Math.abs((int) diffMilliSeconds);
			// System.out.println("MSec>>>>>>>>>>>>"+tempSeconds);
			// System.out.println("Sec>>>>>>>>>>>>"+seconds);
			seconds = Math.abs((int) diff);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println("seconds---------" + seconds);
		return seconds;
	}

	public static int addTimeStamp(String starttime, String EndTime) {
		SimpleDateFormat format = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SSS");
		Date d1 = null;
		Date d2 = null;
		int seconds = 0;
		try {
			d1 = format.parse(starttime);
			d2 = format.parse(EndTime);
			long diff = d1.getTime() + d2.getTime();
			long diffMilliSeconds = diff / 1000 % 60 % 60;
			long diffSeconds = diff / 1000 % 60;
			long diffMinutes = diff / (60 * 1000) % 60;
			long diffHours = diff / (60 * 60 * 1000) % 24;
			long diffDays = diff / (24 * 60 * 60 * 1000);
			seconds = Math.abs((int) diffMilliSeconds);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return seconds;
	}

	public HashMap<String, List<Integer>> getCallSetupTime(String testType,
			List<CallSetUpTo> callSetetailsList) {
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
				if (testType.toLowerCase().trim().equals("externaltest")) {
					if (timeDiff / 1000 > 2 && timeDiff / 1000 < 60) {
						callSetUpMO.add(timeDiff);
					}
					callSetUpLogcatMO.add(timeDiff);
					// }
				} else {
					callSetUpMO.add(timeDiff);
				}
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

	public static CallretentionTo getCallRetentionTime(String test_type,
			String test_name, String marketId, List<List<CallSetUpTo>> allCycles,List totalCallsList) {
		ResultSet rs = null;
		List<CallSetUpTo> New_Outgoing_Call_Time_Stamp = new ArrayList<CallSetUpTo>();
		List<CallSetUpTo> dropOrMissedCalls = new ArrayList<CallSetUpTo>();
		List<CallSetUpTo> callDropTime_Stamp = new ArrayList<CallSetUpTo>();
		// System.out.println("in call retention");
		List<CallSetUpTo> accessFailure = new ArrayList<CallSetUpTo>();
		CallretentionTo callretentionTo = new CallretentionTo();
		// String test_type = "mo";
		long callDropRate = 0;
		try {
			// test_name=test_name + "\\-%";
			New_Outgoing_Call_Time_Stamp = getTestTimeStampInLogCatInfo(
					test_name + "\\-%", test_type, marketId,
					"NEW_OUTGOING_CALL");
			callDropTime_Stamp = getTestTimeStampInLogCatInfo(test_name
					+ "\\-%", test_type, marketId, "CALL_DROP");
			if (New_Outgoing_Call_Time_Stamp.size() == 0) {
				New_Outgoing_Call_Time_Stamp = getTestTimeStampInCallEvents(
						test_name, test_type, marketId, "'Placing Call'");
				callretentionTo = getCallDropsMissedCalls(allCycles, test_name,
						marketId);

				dropOrMissedCalls = getTestTimeStampInCallEvents(test_name,
						test_type, marketId, "'Missed Call','Call Dropped'");

				// System.out.println();
				/*
				 * callDropTime_Stamp = getTestTimeStampInCallEvents(test_name,
				 * test_type, marketId, "Call Failure");
				 */
				accessFailure = getTestTimeStampInCallEvents(test_name,
						test_type, marketId, "'Error: PlaceCall Failed'");

//				callretentionTo.setTotalCalls(New_Outgoing_Call_Time_Stamp
//						.size());
				callretentionTo.setTotalCalls(totalCallsList.size());

			} else {
				callretentionTo.setTotalCalls(New_Outgoing_Call_Time_Stamp
						.size());
				callretentionTo.setDropCalls(callDropTime_Stamp.size());
			}

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
			if (test_type.equalsIgnoreCase("mo")) {
				insertIntoPrimaryPreCalRententionTable(marketId, test_name,
						New_Outgoing_Call_Time_Stamp, "NEW OUTGOING CALL");
				insertIntoPrimaryPreCalRententionTable(marketId, test_name,
						callDropTime_Stamp, "Call Dropped");
			} else {
				insertIntoPrimaryPreCalRententionTable(marketId, test_name,
						New_Outgoing_Call_Time_Stamp, "BOTH_CALLS");
				insertIntoPrimaryPreCalRententionTable(marketId, test_name,
						dropOrMissedCalls, "BOTH_CALLS");

			}
			insertIntoPrimaryPreCalRententionTable(marketId, test_name,
					accessFailure, "Access Failure");
		} catch (Exception e) {
			e.printStackTrace();
		}
		// //System.out.println("callRetentionMO--------"+callRetentionMO);
		return callretentionTo;
	}

	public HashMap<String, List<Integer>> getCallTearDownTime(String testType,
			List<CallSetUpTo> callSetetailsList) {
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
		idleTimeList = new VoiceConnectivityProccesorHelper().getIdleTime(
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
						+ "\\-%' "
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
					+ "test_name LIKE '"
					+ testName
					+ "%' AND TEST_TYPE = '"
					+ testType
					+ "' "
					+ " ORDER BY TIME_STAMP_FOREACH_SAMPLE";
			// //System.out.println("getIdleTimestamp-------"+getIdleTimeQuery);
			rs = st.executeQuery(getIdleTimeQuery);
			while (rs.next()) {
				CallSetUpTo idleTimeTo = new CallSetUpTo();
				if (rs.getString("SIGNALSTRENGTH_GSMSIGNALSTRENGTH")
						.equalsIgnoreCase("Empty"))
					currentStrength = 0;
				else
					currentStrength = rs
							.getInt("SIGNALSTRENGTH_GSMSIGNALSTRENGTH");

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
			if (allIdleList.size() > 0) {
				insertIntoPrimaryPreCalTable(marketId, testName, allIdleList,
						"idleTime", 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println();
		return idleTimeList;
	}

	public static CallretentionTo getCallRetentionTime4G(String test_name,
			String marketId) {
		HashMap<String, List<Integer>> callRetenction4GList = new HashMap<String, List<Integer>>();
		ResultSet rs = null;
		List<String> New_Outgoing_Call_Time_Stamp = new ArrayList<String>();
		List<String> callDropTime_Stamp = new ArrayList<String>();
		;
		String test_type = "mo";
		boolean status = false;
		boolean callstatus = false;
		List<Integer> callRetentionMO = new ArrayList<Integer>();
		List<String> timestamp = new ArrayList<String>();
		List<String> CallDroptimestamp = new ArrayList<String>();
		List<String> deviceinfoTimestamp = new ArrayList<String>();
		CallretentionTo callretentionTo = new CallretentionTo();
		double callDropRate = 0;
		try {
			test_name = test_name + "\\-%";
			timestamp = getTestTimeStampLogCatInfo(test_name, test_type,
					marketId, "NEW_OUTGOING_CALL");
			deviceinfoTimestamp = getTestTimeStampInDeviceInfo(test_name,
					test_type, marketId, "LTE (4G)");
			CallDroptimestamp = getTestTimeStampLogCatInfo(test_name,
					test_type, marketId, "CALL_DROP");

			if (deviceinfoTimestamp.size() > 0) {
				if (deviceinfoTimestamp.size() > timestamp.size()) {
					for (int h = 0; h < timestamp.size(); h++) {
						status = matchTimestamp(deviceinfoTimestamp.get(h),
								timestamp.get(h));
						if (status == true) {
							New_Outgoing_Call_Time_Stamp.add(timestamp.get(h));
						}
					}
				} else if (deviceinfoTimestamp.size() < timestamp.size()) {
					for (int h = 0; h < deviceinfoTimestamp.size(); h++) {
						status = matchTimestamp(deviceinfoTimestamp.get(h),
								timestamp.get(h));
						if (status == true) {
							New_Outgoing_Call_Time_Stamp.add(timestamp.get(h));
						}
					}
				} else {
					for (int h = 0; h < timestamp.size(); h++) {
						status = matchTimestamp(deviceinfoTimestamp.get(h),
								timestamp.get(h));
						if (status == true) {
							New_Outgoing_Call_Time_Stamp.add(timestamp.get(h));
						}
					}
				}
				if (deviceinfoTimestamp.size() > CallDroptimestamp.size()) {
					for (int k = 0; k < CallDroptimestamp.size(); k++) {
						callstatus = matchCallTimestamp(deviceinfoTimestamp
								.get(k), CallDroptimestamp.get(k));
						if (callstatus == true) {
							callDropTime_Stamp.add(CallDroptimestamp.get(k));
						}
					}
				} else if (deviceinfoTimestamp.size() < CallDroptimestamp
						.size()) {
					for (int k = 0; k < deviceinfoTimestamp.size(); k++) {
						callstatus = matchCallTimestamp(deviceinfoTimestamp
								.get(k), CallDroptimestamp.get(k));
						if (callstatus == true) {
							callDropTime_Stamp.add(CallDroptimestamp.get(k));
						}
					}
				} else {
					for (int k = 0; k < CallDroptimestamp.size(); k++) {
						callstatus = matchCallTimestamp(deviceinfoTimestamp
								.get(k), CallDroptimestamp.get(k));
						if (callstatus == true) {
							callDropTime_Stamp.add(CallDroptimestamp.get(k));
						}
					}
				}

				if (test_type.equals("mo")) {
					callDropRate = (Double.parseDouble(String
							.valueOf(callDropTime_Stamp.size())) / Double
							.parseDouble(String
									.valueOf(New_Outgoing_Call_Time_Stamp
											.size()))) * 100;
					callRetentionMO.add(New_Outgoing_Call_Time_Stamp.size());
					callRetentionMO.add(callDropTime_Stamp.size());
					int value = (int) callDropRate;
					callRetentionMO.add(value);

					callretentionTo.setTotalCalls(New_Outgoing_Call_Time_Stamp
							.size());
					callretentionTo.setDropCalls(callDropTime_Stamp.size());
					callretentionTo.setDcr(value);
				}
			}
			callRetenction4GList.put("mo", callRetentionMO);
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		 * finally{ try{ conn.close(); rs.close(); } catch (Exception e) {
		 * e.printStackTrace(); } }
		 */
		return callretentionTo;
	}

	public HashMap<String, List<Integer>> getCallSetupTime4G(String testName,
			String marketId) {
		HashMap<String, List<Integer>> callSetup4GList = new HashMap<String, List<Integer>>();
		ResultSet rs = null;
		boolean status = false;
		List<String> New_Outgoing_Call_Time_Stamp = new ArrayList<String>();
		List<String> offHookTime_Stamp = new ArrayList<String>();
		;
		String test_type = "mo";
		List<Integer> callSetUpMO = new ArrayList<Integer>();
		List<String> deviceinfoTimestamp = null;
		List<String> timestamp = new ArrayList<String>();
		List<String> offHooktimestamp = new ArrayList<String>();
		try {
			testName = testName + "\\-%";
			timestamp = getTestTimeStampLogCatInfo(testName, test_type,
					marketId, "NEW_OUTGOING_CALL");
			deviceinfoTimestamp = getTestTimeStampInDeviceInfo(testName,
					test_type, marketId, "LTE (4G)");
			offHooktimestamp = getTestTimeStampLogCatInfo(testName, test_type,
					marketId, "OFFHOOK");
			if (deviceinfoTimestamp.size() > 0) {
				if (deviceinfoTimestamp.size() > timestamp.size()) {
					for (int k = 0; k < timestamp.size(); k++) {
						status = matchTimestamp(timestamp.get(k),
								deviceinfoTimestamp.get(k));
						if (status == true) {
							New_Outgoing_Call_Time_Stamp.add(timestamp.get(k));
						}
					}
				} else if (deviceinfoTimestamp.size() < timestamp.size()) {
					for (int k = 0; k < deviceinfoTimestamp.size(); k++) {
						status = matchTimestamp(timestamp.get(k),
								deviceinfoTimestamp.get(k));
						if (status == true) {
							New_Outgoing_Call_Time_Stamp.add(timestamp.get(k));
						}
					}
				} else {
					for (int k = 0; k < timestamp.size(); k++) {
						status = matchTimestamp(timestamp.get(k),
								deviceinfoTimestamp.get(k));
						if (status == true) {
							New_Outgoing_Call_Time_Stamp.add(timestamp.get(k));
						}
					}
				}

				if (deviceinfoTimestamp.size() > offHooktimestamp.size()) {
					for (int h = 0; h < offHooktimestamp.size(); h++) {
						status = matchTimestamp(offHooktimestamp.get(h),
								deviceinfoTimestamp.get(h));
						if (status == true) {
							offHookTime_Stamp.add(offHooktimestamp.get(h));
						}
					}
				} else if (deviceinfoTimestamp.size() < offHooktimestamp.size()) {
					for (int h = 0; h < deviceinfoTimestamp.size(); h++) {
						status = matchTimestamp(offHooktimestamp.get(h),
								deviceinfoTimestamp.get(h));
						if (status == true) {
							offHookTime_Stamp.add(offHooktimestamp.get(h));
						}
					}
				} else {
					for (int h = 0; h < offHooktimestamp.size(); h++) {
						status = matchTimestamp(offHooktimestamp.get(h),
								deviceinfoTimestamp.get(h));
						if (status == true) {
							offHookTime_Stamp.add(offHooktimestamp.get(h));
						}
					}
				}
				if (New_Outgoing_Call_Time_Stamp.size() < offHookTime_Stamp
						.size()) {
					for (int j = 0; j < New_Outgoing_Call_Time_Stamp.size(); j++) {
						if (test_type.equals("mo")) {
							if (New_Outgoing_Call_Time_Stamp != null
									&& offHookTime_Stamp != null) {
								callSetUpMO.add(diffTimeStamp(
										New_Outgoing_Call_Time_Stamp.get(j),
										offHookTime_Stamp.get(j)));
							}
						}
					}
				} else if (New_Outgoing_Call_Time_Stamp.size() > offHookTime_Stamp
						.size()) {
					for (int j = 0; j < offHookTime_Stamp.size(); j++) {
						if (test_type.equals("mo")) {
							if (New_Outgoing_Call_Time_Stamp != null
									&& offHookTime_Stamp != null) {
								callSetUpMO.add(diffTimeStamp(
										New_Outgoing_Call_Time_Stamp.get(j),
										offHookTime_Stamp.get(j)));
							}
						}
					}
				}

			}

			callSetup4GList.put("mo", callSetUpMO);
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		 * finally{ try{ conn.close(); rs.close(); } catch (Exception e) {
		 * e.printStackTrace(); } }
		 */
		return callSetup4GList;
	}

	public static Map<String, List<CallSetUpTo>> getcallSetupChartDetails(
			String testName, String testType, String marketId) {
		// ////System.out.println("Seattle---------"+new
		// VoiceConnectivityDaoImpl().getCallTearDownTime("22","SGH-M919","SGH-M919_mo_Seattle East_2014-01-08 14:47:48.067_355537050050378"));
		VoiceConnectivityProccesorHelper vcd = new VoiceConnectivityProccesorHelper();
		List<CallSetUpTo> callList = vcd.getAllInLogCatInfo(testName, testType,
				marketId, 0);
		List<CallSetUpTo> allDetailsList = vcd.getAllInLogCatInfo(testName,
				testType, marketId, 1);
		List<CallSetUpTo> callFormattedList = new ArrayList<CallSetUpTo>();
		List<CallSetUpTo> callTearDownList = new ArrayList<CallSetUpTo>();
		Map<String, List<CallSetUpTo>> voiceConnectivityMap = new HashMap<String, List<CallSetUpTo>>();
		int listSize = callList.size();
		// ////System.out.println("listSize--"+listSize);
		for (int i = 0; i < listSize; i++) {
			CallSetUpTo cst = callList.get(i);
			String eventName = cst.getEventName();
			String nextEvent = "";
			if ((i + 1 < listSize) && null != callList.get(i + 1)) {
				nextEvent = callList.get(i + 1).getEventName();
			}
//			if ((i + 2 < listSize) && null != callList.get(i + 2)) {
//				tempNextEvent = callList.get(i + 2).getEventName();
//			}
			// HERE WE ARE GETTING DETAILS FOR CALLSETUPDOWN,
			// THE LOGIC IS: WE PICK THE EVENTS WHICH IS NEW_OUTGOING_CALL AND
			// AGAIN NEW_OUTGOING_CALL DOESN REPEAST UNTIL OFFHOOK IS
			// ENCOUNTERED
			if ((eventName.equalsIgnoreCase("NEW_OUTGOING_CALL")
					&& !(nextEvent.equalsIgnoreCase("NEW_OUTGOING_CALL")) && nextEvent
					.equalsIgnoreCase("OFFHOOK"))
					||
					 (eventName.equalsIgnoreCase("Placing Call") && nextEvent.toLowerCase()
					 .equalsIgnoreCase("connect from far end"))){
					// THIS || CONDITION IS FOR THE EVENTS FROM CALL_EVENTS FILE
//					(eventName.equalsIgnoreCase("Placing Call_O") && nextEvent
//							.equalsIgnoreCase("Connected_O"))
//					&& tempNextEvent.equalsIgnoreCase("Connected_I")) {
				
				callFormattedList.add(callList.get(i));
				 callFormattedList.add(callList.get(i + 1));
//				callFormattedList.add(callList.get(i + 2));
				// System.out.println("event date---" + cst.getTimeStamp());

			}
		}
		voiceConnectivityMap.put("callSetupDetails", callFormattedList);
		voiceConnectivityMap.put("allDetailsList", allDetailsList);
		voiceConnectivityMap.put("callTearDownDetails", callTearDownList);
		// System.out.println("voiceConnectivityMap--------"
		// + voiceConnectivityMap);
		return voiceConnectivityMap;
	}

	public static boolean matchTimestamp(String starttime, String Endtime) {
		boolean status = false;
		SimpleDateFormat format = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SSS");
		Date d1 = null;
		Date d2 = null;
		int seconds = 0;
		try {
			d1 = format.parse(starttime);
			d2 = format.parse(Endtime);
			long diff = d1.getTime() - d2.getTime();
			long diffMilliSeconds = diff / 1000 % 60 % 60;
			long diffSeconds = diff / 1000 % 60;
			long diffMinutes = diff / (60 * 1000) % 60;
			long diffHours = diff / (60 * 60 * 1000) % 24;
			long diffDays = diff / (24 * 60 * 60 * 1000);
			seconds = Math.abs((int) diffSeconds);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (seconds >= 1 && seconds <= 10) {
			status = true;
		}
		return status;
	}

	public static DeviceInfoTO matchinDeviceInfo(String date, String format,
			String testName, String marketId, String test_type) {
		DeviceInfoTO deviceInfos = new DeviceInfoTO();
		// SimpleDateFormat sdfformat = new SimpleDateFormat(format);
		ResultSet rs = null;
		Connection conn = DBUtil.openConn();
		Statement stmt = null;
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

 System.out.println("matchinDeviceInfo query-----------" + query);

		try {
			stmt = conn.createStatement();
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
					//changes made by ankit on 25-04-2016
					if(networkType.equalsIgnoreCase("NA") || networkType.equalsIgnoreCase("NA") 
							|| networkType.equalsIgnoreCase("UNKNOWN") || networkType.equalsIgnoreCase("UNKNOWN")) //Added by ankit
					{//System.out.println("Inside NA & Unknown condition");
						deviceInfos.setNetworkType("UMTS (3G)");
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
					//deviceInfos.setNetworkType(rs.getString("NETWORK_TYPE"));

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}finally{
			try{
				if (stmt != null) { stmt.close();}
				if (rs != null) { rs.close();}
				}catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		return deviceInfos;
	}

	public static DeviceInfoTO matchinDeviceInfo(String date, String format,
			String testName, String marketId, String test_type,
			String timeStampIdentifier) {
		DeviceInfoTO deviceInfos = new DeviceInfoTO();
		// SimpleDateFormat sdfformat = new SimpleDateFormat(format);
		ResultSet rs = null;
		Connection conn = DBUtil.openConn();
		Statement stmt = null;
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
			stmt = conn.createStatement();
//			 System.out.println("matchinDeviceInfo query with time stampindentifier-----------" + query);
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				String networkType = rs.getString("NETWORK_TYPE");
				int rsrp = 0;
				if (rs.getString("SIGNALSTRENGTH_LTERSRP").equalsIgnoreCase(
						"Empty")) {
					rsrp = rs.getInt("SIGNALSTRENGTH_LTERSRP");
				}
				if (rsrp > 1000) {
					System.out.println(rsrp);
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
					//changes made by ankit on 25-04-2016
					if(networkType.equalsIgnoreCase("NA") || networkType.equalsIgnoreCase("NA") 
							|| networkType.equalsIgnoreCase("UNKNOWN") || networkType.equalsIgnoreCase("UNKNOWN")) //Added by ankit
					{//System.out.println("Inside NA & Unknown condition");
						deviceInfos.setNetworkType("UMTS (3G)");
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
					//deviceInfos.setNetworkType(rs.getString("NETWORK_TYPE"));

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			try{
				if (stmt != null) { stmt.close();}
				if (rs != null) { rs.close();}
				}catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		return deviceInfos;
	}

	public static boolean matchCallTimestamp(String starttime, String Endtime) {
		boolean status = false;
		SimpleDateFormat format = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SSS");
		Date d1 = null;
		Date d2 = null;
		int seconds = 0;
		try {
			d1 = format.parse(starttime);
			d2 = format.parse(Endtime);
			long diff = d1.getTime() - d2.getTime();
			long diffMilliSeconds = diff / 1000 % 60 % 60;
			long diffSeconds = diff / 1000 % 60;
			long diffMinutes = diff / (60 * 1000) % 60;
			long diffHours = diff / (60 * 60 * 1000) % 24;
			long diffDays = diff / (24 * 60 * 60 * 1000);
			seconds = Math.abs((int) diffSeconds);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (seconds >= 1 && seconds <= 20) {
			status = true;
		}
		return status;
	}

	/**
	 * 
	 * @param testname
	 * @param marketId
	 * 
	 *            THIS METHOD POPULATES ALL THE EVENTS OF LOGCAT WITH MATCHING
	 *            DEVICE INFO. IF THERE IS NO MATCH FOR DEVICE INFO.. EVEN THAT
	 *            ENTRY IS POPULATED
	 */
	public static void populateAllEntriesOfLogCatandDeviceInfo(String testType,
			String testname, String marketId) {
		String alllogCatEntriesQuery = "SELECT * FROM stg_log_cat_info WHERE TEST_NAME LIKE '"
				+ testname
				+ "-%' AND TEST_TYPE = '"
				+ testType
				+ "' order by time_stamp";
		Connection conn = DBUtil.openConn();
		ResultSet rs = null;
		Statement stmt = null;
		List<CallSetUpTo> callsetUpList = new ArrayList<CallSetUpTo>();
		Set<String> matchedDeviceInfoTimeStamps = new HashSet<String>();
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(alllogCatEntriesQuery);
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
					"all", 0);
			// System.out.println(callsetUpList.size());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			try{
				if (stmt != null) { stmt.close();}
				if (rs != null) { rs.close();}
				}catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
	}

	public static List<CallSetUpTo> getNonMatchinDeviceInfo(String testName,
			String marketId, String testType, String matchingTimeStamps) {
		List<CallSetUpTo> allcallSetUpList = new ArrayList<CallSetUpTo>();
		String query = "SELECT * FROM STG_DEVICE_INFO WHERE TEST_NAME LIKE '"
				+ testName + "-%' " + "AND MARKET_ID='" + marketId
				+ "' AND TEST_TYPE='" + testType
				+ "' AND TIME_STAMP_FOREACH_SAMPLE NOT IN ("
				+ matchingTimeStamps + ")";
		System.out.println(query);
		Connection conn = DBUtil.openConn();
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
					DeviceInfoTO deviceInfos = new DeviceInfoTO();
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
					//changes made by ankit on 25-04-2016
					if(networkType.equalsIgnoreCase("NA") || networkType.equalsIgnoreCase("NA") 
							|| networkType.equalsIgnoreCase("UNKNOWN") || networkType.equalsIgnoreCase("UNKNOWN")) //Added by ankit
					{//System.out.println("Inside NA & Unknown condition");
						deviceInfos.setNetworkType("UMTS (3G)");
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
			String chartType, int flag) {
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
					//System.out.println("insertQuery-------"+insertQuery);
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
					//System.out.println("insertQuery2-------"+insertQuery);
					st.executeUpdate(insertQuery);
				}
				if (chartType.equalsIgnoreCase("all")) {

					vto = voiceConnectivityValueList.get(i);

					String table = "pre_calc_voiceconnectivity_2";
					if (flag != 1) {
						if (vto.getDeviceInfoTO().getTestType()
								.equalsIgnoreCase("mo"))
							table = "pre_calc_voiceconnectivity_1";
					}

					String insertQuery = "INSERT INTO "
							+ table
							+ " (MARKET_ID,TEST_NAME,NETWORK_TYPE,CHART_TYPE,TIMESTAMP,IMEI	"
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
									.getSignalStrengthGSMBITRATEERROR() + "','"
							+ vto.getDeviceInfoTO().getSignalStrengthLTE()
							+ "','"
							+ vto.getDeviceInfoTO().getSignalStrengthLTERSRP()
							+ "'," + "'"
							+ vto.getDeviceInfoTO().getSignalStrengthLTERSRQ()
							+ "','"
							+ vto.getDeviceInfoTO().getSignalStrengthLTERSSNR()
							+ "','"
							+ vto.getDeviceInfoTO().getSignalStrength_LTECQI()
							+ "','"
							+ vto.getDeviceInfoTO().getCellLocationCID() + "',"
							+ "'" + vto.getDeviceInfoTO().getCellLocationLAC()
							+ "','" + vto.getDeviceInfoTO().getNeighbourInfo()
							+ "','" + vto.getDeviceInfoTO().getBatteryLevel()
							+ "','"
							+ vto.getDeviceInfoTO().getNetworkManuallyDone()
							+ "'," + "'" + vto.getDeviceInfoTO().getLattitude()
							+ "','" + vto.getDeviceInfoTO().getLongitude()
							+ "','" + vto.getDeviceInfoTO().getSnapShotId()
							+ "')";
					// System.out.println(insertQuery);
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
			Map<String, List<CallSetUpTo>> voiceConnectivityMap = new VoiceConnectivityProccesorHelper()
					.getcallSetupChartDetails(testName, testType, marketId);
			List callSetupLineChartMoValueList2 = new VoiceConnectivityProccesorHelper()
					.getCallSetupTime(testType,
							voiceConnectivityMap.get("callSetupDetails")).get(
							testType);
			List<List<CallSetUpTo>> allCycles = getAllInLogCatInfoEvents(
					testName, marketId);
			System.out.println("...");
			insertIntoPreCalTable(marketId, testName,
					callSetupLineChartMoValueList2.toString(), "callsetup",
					"ALL");
			List callTearDownList = new VoiceConnectivityProccesorHelper()
					.getCallTearDownTime(testType,
							voiceConnectivityMap.get("callTearDownDetails"))
					.get(testType);
			/*
			 * insertIntoPreCalTable(marketId, testName, callTearDownList
			 * .toString(), "callteardown", "ALL");
			 */
			List idleTimeList = getIdleTimeOutOfCoverage(marketId, testName,
					testType);
			// //System.out.println("idleTimeList--@@-------"+idleTimeList);
			insertIntoPreCalTable(marketId, testName, idleTimeList.toString(),
					"idleTime", "ALL");
			CallretentionTo callretentionTo = getCallRetentionTime(testType,
					testName, marketId, allCycles,callSetupLineChartMoValueList2);
			// //System.out.println("callSetupLineChartMoValueList2---------"+callSetupLineChartMoValueList2);
			insertIntoPreCalTableCalRentention(marketId, testName,
					callretentionTo, "all");
			// System.out.println("33333333333333333333333333");
			insertIntoPrimaryPreCalTable(marketId, testName,
					voiceConnectivityMap.get("callSetupDetails"),
					"Placing Call", 0);

			insertIntoPrimaryPreCalTable(marketId, testName,
					voiceConnectivityMap.get("callTearDownDetails"),
					"callteardown", 0);
			if (testType.equalsIgnoreCase("mo")) {
				System.out.println("---------this in MO test---------");
				populateAllEntriesOfLogCatandDeviceInfo(testType, testName,
						marketId);
			} else {
				insertIntoPrimaryPreCalTable(marketId, testName,
						voiceConnectivityMap.get("allDetailsList"), "all", 1);
			}

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

//	public static void main(String[] args) {
//		// prepopulateVCData("D3CHM0620", "24");
//		// //System.out.println(diffTimeStamp("",""));
//		// try {
//		// /*
//		// * matchinDeviceInfo("2014-08-06 18:29:18", "'%Y-%m-%d %H:%i:%s'",
//		// * "D1SEVQ0806", "externaltest");
//		// */
//		// String networkType = "LTE (4G)";
//		// int rsrp = 2147483647;
//		// if(!(networkType.contains("LTE"))||((networkType.contains("LTE"))&&rsrp<1000)){
//		// System.out.println("in");
//		// }
//		// } catch (Exception e) {
//		// // TODO: handle exception
//		// e.printStackTrace();
//		// }
//		// new PreprocessorTrigger("D1CHV0707").triggerPreprocessin();
//		// new PreprocessorTrigger("nokiatestmo1").triggerPreprocessin();
//		// new PreprocessorTrigger("18Feb15MO1").triggerPreprocessin();
//		new PreprocessorTrigger("T1vqt0517").triggerPreprocessin();
//	}
}
