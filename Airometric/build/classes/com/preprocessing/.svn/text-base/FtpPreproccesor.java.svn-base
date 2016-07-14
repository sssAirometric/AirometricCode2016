package com.preprocessing;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.constants.ReportConstants;
import com.model.DBUtil;
import com.preprocessorhelpers.FTPReportHelper;
import com.to.DeviceInfoTO;
import com.to.Stg_Log_Cat_TO;

public class FtpPreproccesor {

	static Connection conn = null;
	static Statement st = null;



	public static HashMap<String, List<Stg_Log_Cat_TO>> getLogcatData(
			String test_name) {
		List<Stg_Log_Cat_TO> logCatToList = new ArrayList<Stg_Log_Cat_TO>();
		String getlogcatData = "SELECT DISTINCT * FROM STG_LOG_CAT_INFO WHERE TEST_NAME LIKE '"
				+ test_name + "-%' " + " ORDER BY TIME_STAMP";
		ResultSet rs = null;
		HashMap<String, List<Stg_Log_Cat_TO>> snapShotWiseList = new HashMap<String, List<Stg_Log_Cat_TO>>();
		String currentTestName = "";
		String prevTestName = "";
		//System.out.println("getlogcatData-----" + getlogcatData);
		try {
			rs = st.executeQuery(getlogcatData);
			while (rs.next()) {
				currentTestName = rs.getString("TEST_NAME");
				String snapShotId = rs.getString("SNAPSHOT_ID");
				logCatToList = snapShotWiseList.get(snapShotId);

				if (null == logCatToList) {
					logCatToList = new ArrayList<Stg_Log_Cat_TO>();
				}
				Stg_Log_Cat_TO stgLogCatTo = new Stg_Log_Cat_TO();
				String time_stamp = rs.getString("TIME_STAMP");
				String event_name = rs.getString("EVENT_NAME");
				String event_value = rs.getString("EVENT_VALUE");
				String ping_server_name = rs.getString("PING_SERVER_NAME");
				String ping_server_ip = rs.getString("PING_SERVER_IP");
				String ping_packets_transmitted = rs
						.getString("PING_PACKETS_TRANSMITTED");
				String ping_packets_received = rs
						.getString("PING_PACKETS_RECEIVED");
				String ping_packets_loss = rs.getString("PING_PACKETS_LOSS");
				String ping_transmission_time = rs
						.getString("PING_TRANSMISSION_TIME");
				String ping_rtt_min = rs.getString("PING_RTT_MIN");
				String ping_rtt_avg = rs.getString("PING_RTT_AVG");
				String ping_rtt_max = rs.getString("PING_RTT_MAX");
				String ping_rtt_mdev = rs.getString("PING_RTT_MDEV");
				String test_type = rs.getString("TEST_TYPE");
				String cycleTestname = rs.getString("TEST_NAME");
				String imei_number = rs.getString("IMEI_NUMBER");

				stgLogCatTo.setSnapShotId(snapShotId);
				stgLogCatTo.setEvent_value(event_value.toString());
				stgLogCatTo.setEvent_name(event_name);
				stgLogCatTo.setTime_stamp(time_stamp);
				stgLogCatTo.setPing_server_name(ping_server_name);
				stgLogCatTo.setPing_server_ip(ping_server_ip);
				stgLogCatTo
						.setPing_packets_transmitted(ping_packets_transmitted);
				stgLogCatTo.setPing_packets_received(ping_packets_received);
				stgLogCatTo.setPing_packets_loss(ping_packets_loss);
				stgLogCatTo.setPing_transmission_time(ping_transmission_time);
				stgLogCatTo.setPing_rtt_min(ping_rtt_min);
				stgLogCatTo.setPing_rtt_avg(ping_rtt_avg);
				stgLogCatTo.setPing_rtt_max(ping_rtt_max);
				stgLogCatTo.setPing_rtt_mdev(ping_rtt_mdev);
				stgLogCatTo.setTest_type(test_type);
				stgLogCatTo.setImei_number(imei_number);
				stgLogCatTo.setTest_name(cycleTestname);
				logCatToList.add(stgLogCatTo);
				if (null != snapShotId) {
					snapShotWiseList.put(snapShotId, logCatToList);
				}

			}
			getLogcatWitTenSecEntries(snapShotWiseList);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		//System.out.println("cyclewiseTestName------------" + snapShotWiseList);
		return snapShotWiseList;
	}

	private static List<DeviceInfoTO> getClosestEntries(
			List<DeviceInfoTO> deviceInfoList,
			Map<String, List<Stg_Log_Cat_TO>> logactSnapShotMap) {
		SimpleDateFormat newSdf = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SSS");
		/*
		 * File file = new File("/D:/Logme.txt"); PrintStream printStream; try {
		 * printStream = new PrintStream(new FileOutputStream(file));
		 * System.setOut(printStream); } catch (FileNotFoundException e1) { //
		 * TODO Auto-generated catch block e1.printStackTrace(); }
		 */

		List<DeviceInfoTO> resultList = new ArrayList<DeviceInfoTO>();
		// int locatListLength = stgList.size();
		int deviceInfoIndex = 0;
		long difference = 0;
		String market = "";
		Date currentLogcat = null;
		Date prevLogcat = null;
		int deviceInfoListListLength = deviceInfoList.size();
		try {

			/*
			 * New Logic
			 */
			//System.out.println(logactSnapShotMap);

			for (int j = deviceInfoIndex; j < deviceInfoListListLength; j++) {
				int count = 0;
				while (count < 2) {

					DeviceInfoTO currentDeviceInfo = deviceInfoList.get(j);
					String snapShotId = currentDeviceInfo.getSnapShotId();
					List<Stg_Log_Cat_TO> logCatList = logactSnapShotMap
							.get(snapShotId);
					
					Stg_Log_Cat_TO stgLogTo = new Stg_Log_Cat_TO();
					if(null!=logCatList){
						if (count == 0 ) {
							stgLogTo = findClosestLogcatBucket(0, logCatList,
									"Current RX bytes");
							DeviceInfoTO deviceInforesultTo = getmergedTo(stgLogTo,(DeviceInfoTO)currentDeviceInfo.clone());
							
							if(null!=deviceInforesultTo){
								resultList.add(deviceInforesultTo);
							}
							
						} else {
							stgLogTo = findClosestLogcatBucket(
									logCatList.size() , logCatList,
									"Current RX bytes");
							DeviceInfoTO deviceInforesultTo = getmergedTo(stgLogTo,(DeviceInfoTO)currentDeviceInfo.clone());
//							System.out.println(deviceInforesultTo.getSnapShotId());
							
							if(null!=deviceInforesultTo){
								resultList.add(deviceInforesultTo);
							}
						}
						
						
						
						if (count == 1 && null != logCatList) {
							stgLogTo = findClosestLogcatBucket(0, logCatList,
									"Current TX bytes");
							DeviceInfoTO deviceInforesultTo = getmergedTo(stgLogTo,(DeviceInfoTO)currentDeviceInfo.clone());
							if(null!=deviceInforesultTo){
								resultList.add(deviceInforesultTo);
							}
						} else {
							stgLogTo = findClosestLogcatBucket(
									logCatList.size() - 1, logCatList,
									"Current TX bytes");
							DeviceInfoTO deviceInforesultTo = getmergedTo(stgLogTo,(DeviceInfoTO)currentDeviceInfo.clone());
							if(null!=deviceInforesultTo){
								resultList.add(deviceInforesultTo);
							}
						}
					}
					count++;
					currentDeviceInfo = new DeviceInfoTO();
				}
				//System.out.println("resultList------"+resultList.size());
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
//		for(int i=0;i<resultList.size();i++){
//			System.out.println(resultList.get(i).getTime_stamp());
//		}
		return resultList;
	}

	static DeviceInfoTO getmergedTo(Stg_Log_Cat_TO stgLogTo,DeviceInfoTO currentDeviceInfo) {
		DeviceInfoTO mergedTo = new DeviceInfoTO();
		mergedTo = currentDeviceInfo;
		
		mergedTo.setEventValue(stgLogTo.getEvent_name());
		mergedTo.setEventName(stgLogTo.getEvent_value());
		mergedTo.setTime_stamp(stgLogTo.getTime_stamp());
		mergedTo.setEventName(stgLogTo.getEvent_name());
		mergedTo.setEventValue(stgLogTo.getEvent_value());
		return mergedTo;
	}

	static Stg_Log_Cat_TO findClosestLogcatBucket(int index,
			List<Stg_Log_Cat_TO> every10SecIntervalMap, String eventName) {
		Stg_Log_Cat_TO logcatTo = new Stg_Log_Cat_TO();
		float currentDiff = 0;
		float prevDiff = 0;
		SimpleDateFormat newSdf = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SSS");
		try {
			for (int i = 0;i<every10SecIntervalMap.size(); i++) {
				Stg_Log_Cat_TO tempLogCatTo = every10SecIntervalMap.get(i);
//				System.out.println("index---"+i+"logcatTo--++-"+tempLogCatTo.getEvent_value()+"-----EVENT NAME--"+tempLogCatTo.getEvent_name());
			}
			if (index == 0) {
				for (int i = 0;i<every10SecIntervalMap.size(); i++) {
					Stg_Log_Cat_TO tempLogCatTo = every10SecIntervalMap.get(i);
					
					if (tempLogCatTo.getEvent_name()
							.equalsIgnoreCase(eventName)) {
					
//						if(tempLogCatTo.getSnapShotId().equalsIgnoreCase("1400089672277_355386050623846_ColoradoData12-1_32_1")){
//							System.out.println("index---"+i+"logcatTo--++-"+tempLogCatTo.getEvent_value()+"-----EVENT NAME--"+tempLogCatTo.getEvent_name());
//						}
						return tempLogCatTo;
					}
				}
			} else {
				for (int i = every10SecIntervalMap.size() - 1; i >= 0; i--) {
					Stg_Log_Cat_TO tempLogCatTo = every10SecIntervalMap.get(i);
				
					
					if (tempLogCatTo.getEvent_name()
							.equalsIgnoreCase(eventName)) {
						if(tempLogCatTo.getSnapShotId().equalsIgnoreCase("1400089672277_355386050623846_ColoradoData12-1_32_1")){
//							System.out.println("index---"+i+"logcatTo--++-"+tempLogCatTo.getEvent_value()+"-----EVENT NAME--"+tempLogCatTo.getEvent_name());
						}
						return tempLogCatTo;
					}
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return logcatTo;
	}

	public static void insertIntoFtp(List<DeviceInfoTO> consolidatedList) {
		ResultSet rs = null;
		try {
			for (int i = 0; i < consolidatedList.size(); i++) {
				DeviceInfoTO vto = consolidatedList.get(i);
				String query = "INSERT INTO ftpcalculationtable (TimeStamp,MARKET_ID,TEST_NAME,EVENT_VALUE,EVENT_NAME,SIGNALSTRENGTH,NETWORK_TYPE,IMEI	" +
				",TEST_TYPE,	USER_NAME	,DEVICE_PHONENUMBER,	DEVICE_PHONETYPE,	" +
				"DEVICE_MANUFACTURER,	DEVICE_VERSION,	TIME_STAMP_FOREACH_SAMPLE,	NETWORK_NETWORKOPERATOR,	NETWORK_NETWORKOPERATORNAME," +
				"	NETWORK_NETWORKTYPE	,NETWORK_DATASTATE,	NETWORK_DATAACTIVITY,	NETWORK_WIFISTATE,	NETWORK_ROAMING,	NETWORK_MCC	,NETWORK_MNC	" +
				",SIGNALSTRENGTH_GSMSIGNALSTRENGTH,	SIGNALSTRENGTH_CDMADBM,	SIGNALSTRENGTH_CDMACIO,	SIGNALSTRENGTH_EVDODBM,	SIGNALSTRENGTH_EVDOECIO	" +
				",SIGNALSTRENGTH_EVDOSNR,	SIGNALSTRENGTH_GSM,	SIGNALSTRENGTH_GSMBITRATEERROR,	SIGNALSTRENGTH_LTESIGNALSTRENGTH,	" +
				"SIGNALSTRENGTH_LTERSRP,	SIGNALSTRENGTH_LTERSRQ,	SIGNALSTRENGTH_LTERSSNR,	SIGNALSTRENGTH_LTECQI,	CELLLOCATION_CID,	" +
				"CELLLOCATION_LAC,	NEIGHBOUR_INFO,	BATTERY_LEVEL,	NETWORK_MANUALLY_DONE,	GEOLOCATION_LATITUDE,	GEOLOCATION_LONGITUDE," +
				"		SNAPSHOT_ID) VALUES ('"
						+ vto.getTime_stamp() + "', '" + vto.getMarketId()
						+ "', '" + vto.getTestName() + "', '"
						+ vto.getEventValue() + "', '" + vto.getEventName()
						+ "', '" + vto.getSignalStrength() + "', '"
						+ vto.getNetworkType() + "', '"
						+ vto.getImei()+"','"+vto.getTestType()+"','"+vto.getUserName()+"'," +
						"'"+vto.getPhoneNumber()+"','"+vto.getPhoneType()+"','"+vto.getDeviceManufacturer()+"','"+vto.getDeviceVersion()+"'," +
						"'"+vto.getTimeStampForEachSample()+"','"+vto.getNetworkOperator()+"','"+vto.getNetworkOperatorName()+"','"+vto.getNetworkType()+"'," +
						"'"+vto.getNetworkDataState()+"','"+vto.getDataActivity()+"','"+vto.getWifiState()+"','"+vto.getNetworkRoaming()+"'," +
						"'"+vto.getNetworkMCC()+"','"+vto.getNetworkMNC()+"','"+vto.getSignalStrengthGSM()+"','"+vto.getSignalStrengthCDMA()+"'," +
						"'"+vto.getSignalStrengthCDMACIO()+"','"+vto.getSignalStrengthEVDO()+"','"+vto.getSignalStrengthEVDOECIO()+"','"+vto.getSignalStrength_EVDOSNR()+"'," +
						"'"+vto.getSignalStrengthGSM()+"','"+vto.getSignalStrengthGSMBITRATEERROR()+"','"+vto.getSignalStrengthLTE()+"','"+vto.getSignalStrengthLTERSRP()+"'," +
						/* Code correction by Sheshadri(05-05-15), Since of null pointer exception code has been updated to signalStrengthLTECQI instead signalStrength_LTECQI variable */
						"'"+vto.getSignalStrengthLTERSRQ()+"','"+vto.getSignalStrengthLTERSSNR()+"','"+vto.getSignalStrengthLTECQI()+"','"+vto.getCellLocationCID()+"'," +
						"'"+vto.getCellLocationLAC()+"','"+vto.getNeighbourInfo()+"','"+vto.getBatteryLevel()+"','"+vto.getNetworkManuallyDone()+"'," +
						"'"+vto.getLattitude()+"','"+vto.getLongitude()+"','"+vto.getSnapShotId()+"')" ;
//				System.out.println(query);
				if(null!=vto.getTime_stamp()){
//					//System.out.println("----" + query);
					st.executeUpdate(query);
				}
				
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static void insertInThroughPutTable(String test_name,
			String market_id) {
		String getlogcatData = "SELECT DISTINCT * FROM FTPCALCULATIONTABLE WHERE TEST_NAME LIKE '"
				+ test_name
				/*Code correction by Sheshadri, removed ifan character*/
//				+ "-%' AND MARKET_ID='"
				+ "%' AND MARKET_ID='"
				+ market_id
				+ "'  AND EVENT_NAME IN"
				+ " ('CURRENT RX BYTES','CURRENT TX BYTES') ORDER BY TimeStamp";
		List<DeviceInfoTO> ftpCommonList = new ArrayList<DeviceInfoTO>();
		HashMap<String, List<List<DeviceInfoTO>>> networkwiseGroupedMap = new HashMap<String, List<List<DeviceInfoTO>>>();
//		// //System.out.println("getlogcatData----------" + getlogcatData);
		try {
			ResultSet rs = st.executeQuery(getlogcatData);
			while (rs.next()) {
				DeviceInfoTO deviceInfoTo = new DeviceInfoTO();
				String networkType = rs.getString("NETWORK_TYPE");
				String testName = rs.getString("TEST_NAME");
				String marketId = rs.getString("MARKET_ID");
				String eventName = rs.getString("EVENT_NAME");
				String eventValue = rs.getString("EVENT_VALUE");
				String rssi = rs.getString("SIGNALSTRENGTH");
				String timeStamp = rs.getString("TimeStamp");
				networkwiseGroupedMap.put(networkType, null);
				deviceInfoTo.setNetworkType(networkType);
				deviceInfoTo.setTestName(testName);
				deviceInfoTo.setMarket(marketId);
				deviceInfoTo.setEventName(eventName);
				deviceInfoTo.setEventValue(eventValue);
				deviceInfoTo.setSignalStrength(rssi);
				deviceInfoTo.setTime_stamp(timeStamp);
				ftpCommonList.add(deviceInfoTo);
			}

			/*
			 * groupFTPData(test_name, market_id, ftpCommonList,
			 * networkwiseGroupedMap);
			 */
			FTPReportHelper ftpReportHelper = new FTPReportHelper();
			List<DeviceInfoTO> throughputList = ftpReportHelper
					.getThroughputForMaps(test_name, "Current TX bytes",market_id);
			throughputList.addAll(ftpReportHelper.getThroughputForMaps(
					test_name, "Current RX bytes",market_id));
			Map<String, Map<String, List<Float>>> transactionWiseMap = ftpReportHelper
					.getAvgNetworkWiseThroughput(throughputList);
			Map<String, Map<String, Float>> transactionWiseAvgMap = ftpReportHelper
					.getAvgNetworkWiseThroughput(transactionWiseMap);
			insertIntoThroughputTable(test_name, market_id,
					transactionWiseAvgMap);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static HashMap<String, List<Stg_Log_Cat_TO>> getLogcatWitTenSecEntries(
			HashMap<String, List<Stg_Log_Cat_TO>> cycleLogCatTests) {
		HashMap<String, List<Stg_Log_Cat_TO>> tenSecEntryMap = new LinkedHashMap<String, List<Stg_Log_Cat_TO>>();
		//System.out.println("cycleLogCatTests------" + cycleLogCatTests);
		Date currentDate = null;
		Date initialDate = null;
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Iterator<String> cycleTestNameItr = cycleLogCatTests.keySet()
				.iterator();
		List<Stg_Log_Cat_TO> listOf10SecData = null;
		try {
			while (cycleTestNameItr.hasNext()) {
				String testName = cycleTestNameItr.next();
				List<Stg_Log_Cat_TO> ctoList = cycleLogCatTests.get(testName);
				int entryNumber = 0;
				for (Stg_Log_Cat_TO slcTo : ctoList) {
					currentDate = sdf.parse(slcTo.getTime_stamp());
					if (entryNumber == 0) {
						listOf10SecData = new ArrayList<Stg_Log_Cat_TO>();
						initialDate = sdf.parse(slcTo.getTime_stamp());
					}
					if ((currentDate.getTime() - initialDate.getTime()) / 1000 % 60 > 10) {
						tenSecEntryMap.put(sdf.format(initialDate),
								listOf10SecData);
						listOf10SecData = new ArrayList<Stg_Log_Cat_TO>();
						initialDate = currentDate;
					}
					listOf10SecData.add(slcTo);
					entryNumber++;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		//System.out.println("tenSecEntryMap--------------" + tenSecEntryMap);
		Iterator<String> dateItr = tenSecEntryMap.keySet().iterator();
		while (dateItr.hasNext()) {
			String date = dateItr.next();
		}
		return tenSecEntryMap;
	}

	public static Float getAverageList(List<Float> list) {
		Float averagevalue = new Float(0);
		Float listSum = new Float(0);
		try {
			if (null != list) {
				//// //System.out.println("list-----++-----" + list.size());
				for (int i = 0; i < list.size(); i++) {
					listSum = listSum + list.get(i);
				}
				if (list.size() > 0) {
					averagevalue = listSum / list.size();
				} else {
					averagevalue = null;
				}
			} else {
				averagevalue = null;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return averagevalue;
	}

	public static void insertIntoThroughputTable(String testname,
			String market_id,
			Map<String, Map<String, Float>> networkWiseTransactionThroughput) {
		ResultSet rs = null;

		try {
			Map<String, Float> networkWiseThroughput = networkWiseTransactionThroughput
					.get("RxBytes");
			Iterator<String> networkItr = networkWiseThroughput.keySet()
					.iterator();
			while (networkItr.hasNext()) {
				String networkType = networkItr.next();
				Float throughPut = networkWiseThroughput.get(networkType);
				throughPut = throughPut / (1024 * 1024);
				Float signalStrength = networkWiseTransactionThroughput.get(
						"RxBytesSS").get(networkType);
				String query = "INSERT INTO ftp_throughput VALUES ( '"
						+ market_id + "', '" + testname + "', '1', '"
						+ throughPut + "', '" + signalStrength + "','"
						+ networkType + "','rx')";
//				//System.out.println("query---" + query);
				st.executeUpdate(query);
			}

			networkWiseThroughput = networkWiseTransactionThroughput
					.get("TxBytes");
			networkItr = networkWiseThroughput.keySet().iterator();
			while (networkItr.hasNext()) {
				String networkType = networkItr.next();
				Float throughPut = networkWiseThroughput.get(networkType);
				Float signalStrength = networkWiseTransactionThroughput.get(
						"TxBytesSS").get(networkType);
				throughPut = throughPut / (1024 * 1024);
				String query = "INSERT INTO ftp_throughput VALUES ( '"
						+ market_id + "', '" + testname + "', '1', '"
						+ throughPut + "', '" + signalStrength + "','"
						+ networkType + "','tx')";
				//System.out.println("query---" + query);
				st.executeUpdate(query);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static void prepopulateFtpData(String testName, String marketId) {
		conn = DBUtil.getConnection();
		try {
			st = conn.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PreProcessor.addSnapShot(testName, marketId);
		HashMap<String, List<Stg_Log_Cat_TO>> snapShotWiseList = getLogcatData(testName);
		List<DeviceInfoTO> getListDeviceInfo = PreprocessorMiscHelper.populateDataForFTP(testName,
				marketId,"ftp");
		List<DeviceInfoTO> finalyList = getClosestEntries(getListDeviceInfo,
				snapShotWiseList);
//		System.out.println("finalyList--------" + finalyList.size());
		
		insertIntoFtp(finalyList);
		insertInThroughPutTable(testName, marketId);
	}

	public static void main(String[] args) {
		prepopulateFtpData("ColoradoData2-1", "32");
		SimpleDateFormat newSdf = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SSS");
		try {
			Date d1 = newSdf.parse("2014-09-22 14:12:35.000");
			Date d2 = newSdf.parse("2014-09-22 14:12:25.000");
			//System.out.println((d2.getTime() - d1.getTime()) / 1000);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
