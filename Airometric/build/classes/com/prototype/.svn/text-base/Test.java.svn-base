package com.prototype;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.constants.ReportConstants;
import com.model.DBUtil;
import com.report.helper.FTPReportHelper;
import com.to.DeviceInfoTO;
import com.to.Stg_Log_Cat_TO;

public class Test {

	public static List<DeviceInfoTO> populateDataForFTP(String testName,
			String marketId) {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		Float signalStrength = new Float(0);
		List<DeviceInfoTO> deviceInToList = new ArrayList<DeviceInfoTO>();

		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String getDeviceInfoData = "SELECT * FROM STG_DEVICE_INFO WHERE TEST_NAME LIKE '"
					+ testName
					+ "-%'AND TEST_TYPE='FTP' AND MARKET_ID='"
					+ marketId + "' " + "AND TEST_TYPE='FTP' ";

			System.out
					.println("getDeviceInfoData---------" + getDeviceInfoData);
			try {
				rs = st.executeQuery(getDeviceInfoData);

				while (rs.next()) {
					String networkType = rs.getString("NETWORK_TYPE");
					String timeStampForEachSample = rs
							.getString("TIME_STAMP_FOREACH_SAMPLE");
					DeviceInfoTO deviceInfoTo = new DeviceInfoTO();
					if (networkType.equalsIgnoreCase("LTE")
							|| networkType.equalsIgnoreCase("LTE (4G)")) {
						signalStrength = (new Float(
										new Float(
												rs
														.getString("SIGNALSTRENGTH_LTERSRP"))));
						networkType = "LTE";
					} else {

						signalStrength = -113
								+ 2
								* (new Float(
										new Float(
												rs
														.getString("SIGNALSTRENGTH_GSMSIGNALSTRENGTH"))));
					}

					deviceInfoTo.setNetworkType(networkType);
					deviceInfoTo.setSignalStrength(signalStrength.toString());
					deviceInfoTo.setNetworkType(networkType);
					deviceInfoTo
							.setTimeStampForEachSample(timeStampForEachSample);
					deviceInfoTo.setMarket(marketId);
					deviceInToList.add(deviceInfoTo);
				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return deviceInToList;
	}

	public static List<Stg_Log_Cat_TO> getLogcatData(String test_name) {
		List<Stg_Log_Cat_TO> logCatToList = new ArrayList<Stg_Log_Cat_TO>();
		String getlogcatData = "SELECT DISTINCT * FROM STG_LOG_CAT_INFO WHERE TEST_NAME LIKE '"
				+ test_name + "-%' " + " ORDER BY TIME_STAMP";
		Connection conn = DBUtil.getConnection();
		ResultSet rs = null;
		System.out.println("getlogcatData-----" + getlogcatData);
		try {
			Statement st = conn.createStatement();
			rs = st.executeQuery(getlogcatData);
			while (rs.next()) {
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
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return logCatToList;
	}

	private static List<DeviceInfoTO> getClosestEntries(
			List<Stg_Log_Cat_TO> stgList, List<DeviceInfoTO> deviceInfoList) {
		SimpleDateFormat newSdf = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SSS");
		/*
		 * File file = new File("/D:/Logme.txt"); PrintStream printStream; try {
		 * printStream = new PrintStream(new FileOutputStream(file));
		 * System.setOut(printStream); } catch (FileNotFoundException e1) { //
		 * TODO Auto-generated catch block e1.printStackTrace(); }
		 */

		List<DeviceInfoTO> resultList = new ArrayList<DeviceInfoTO>();
		int locatListLength = stgList.size();
		int deviceInfoIndex = 0;
		long difference = 0;
		String market = "";
		int deviceInfoListListLength = deviceInfoList.size();
		try {
			for (int logCatIndex = 0; logCatIndex < locatListLength; logCatIndex++) {
				Stg_Log_Cat_TO stgLogTo = stgList.get(logCatIndex);
				String logcatTime = stgLogTo.getTime_stamp();
				Date stgDate = newSdf.parse(logcatTime);
				System.out.println(stgLogTo.getEvent_name());
				System.out.println(stgLogTo.getEvent_value());
				DeviceInfoTO deviceInforesultTo = new DeviceInfoTO();
				for (int j = deviceInfoIndex; j < deviceInfoListListLength; j++) {
					DeviceInfoTO deviceInfoTo = deviceInfoList.get(j);
					String deviceInfoTime = deviceInfoTo
							.getTimeStampForEachSample();
					Date deviceInfoDate = newSdf.parse(deviceInfoTime);

					market = deviceInfoTo.getMarket();
					if (j == 0) {
						difference = Math.abs(stgDate.getTime()
								- deviceInfoDate.getTime());
					}

					else {
						if (difference > Math.abs(stgDate.getTime()
								- deviceInfoDate.getTime())) {
							difference = Math.abs(stgDate.getTime()
									- deviceInfoDate.getTime());
							deviceInfoIndex = j;
						}
					}
				}
				deviceInforesultTo.setTestName(stgLogTo.getTest_name());
				deviceInforesultTo.setMarket(market);
				deviceInforesultTo.setTime_stamp(stgList.get(logCatIndex)
						.getTime_stamp());
				deviceInforesultTo.setEventName(stgList.get(logCatIndex)
						.getEvent_name());
				deviceInforesultTo.setEventValue(stgList.get(logCatIndex)
						.getEvent_value());
				deviceInforesultTo.setNetworkType(deviceInfoList.get(
						deviceInfoIndex).getNetworkType());
				deviceInforesultTo.setSignalStrength(deviceInfoList.get(
						deviceInfoIndex).getSignalStrength());
				resultList.add(deviceInforesultTo);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return resultList;
	}

	public static void insertIntoFtp(List<DeviceInfoTO> consolidatedList) {
		Connection conn = DBUtil.getConnection();
		ResultSet rs = null;

		try {
			Statement st = conn.createStatement();
			for (int i = 0; i < consolidatedList.size(); i++) {
				DeviceInfoTO dto = consolidatedList.get(i);
				String query = "INSERT INTO ftpcalculationtable VALUES ('"
						+ dto.getTime_stamp() + "'," + " '" + dto.getMarket()
						+ "', " + "'" + dto.getTestName() + "', " + "'"
						+ dto.getEventName() + "', " + "'"
						+ dto.getEventValue() + "', " + "'"
						+ dto.getSignalStrength() + "', " + "'"
						+ dto.getNetworkType() + "');";
				System.out.println("query--------" + query);
				st.executeUpdate(query);
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
				+ "-%' AND MARKET_ID='"
				+ market_id
				+ "'  AND EVENT_NAME IN"
				+ " ('CURRENT RX BYTES','CURRENT TX BYTES') ORDER BY TimeStamp";
		Connection conn = null;
		Statement stmt = null;
		List<DeviceInfoTO> ftpCommonList = new ArrayList<DeviceInfoTO>();
		HashMap<String, List<List<DeviceInfoTO>>> networkwiseGroupedMap = new HashMap<String, List<List<DeviceInfoTO>>>();
		System.out.println("getlogcatData----------"+getlogcatData);
		try {
			conn = DBUtil.getConnection();
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(getlogcatData);
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

			groupFTPData(test_name,market_id,ftpCommonList, networkwiseGroupedMap);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private static HashMap<String, HashMap<String, HashMap<String, Float>>> groupFTPData(String test_name,String market_id,
			List<DeviceInfoTO> ftpCommonList,
			HashMap<String, List<List<DeviceInfoTO>>> networkwiseGroupedMap) {
		List<DeviceInfoTO> tempRxValueList = new ArrayList<DeviceInfoTO>();
		List<List<DeviceInfoTO>> masterRxList = new ArrayList<List<DeviceInfoTO>>();
		HashMap<String, HashMap<String, List<DeviceInfoTO>>> networkWiseRxThroughput = new HashMap<String, HashMap<String, List<DeviceInfoTO>>>();
		HashMap<String, HashMap<String, Float>> networkWiseRssiValue = new HashMap<String, HashMap<String, Float>>();
		HashMap<String, List<List<DeviceInfoTO>>> networkwiseTxGroupedMap = (HashMap<String, List<List<DeviceInfoTO>>>) networkwiseGroupedMap
				.clone();

		List<DeviceInfoTO> tempTxValueList = new ArrayList<DeviceInfoTO>();
		List<List<DeviceInfoTO>> masterTxList = new ArrayList<List<DeviceInfoTO>>();
		HashMap<String, HashMap<String, List<DeviceInfoTO>>> networkWiseTxThroughput = new HashMap<String, HashMap<String, List<DeviceInfoTO>>>();
		HashMap<String, HashMap<String, HashMap<String, Float>>> finalEvaluatedMap = new HashMap<String, HashMap<String, HashMap<String, Float>>>();

		boolean newRxCycle = false;
		boolean newTxCycle = false;
		String currentEventName = "";
		String prevEventName = "";

		for (int i = 0; i < ftpCommonList.size(); i++) {
			DeviceInfoTO deviceInfoTo = ftpCommonList.get(i);
			String networkType = deviceInfoTo.getNetworkType();
			masterRxList = networkwiseGroupedMap.get(networkType);
			if (null == masterRxList) {
				masterRxList = new ArrayList<List<DeviceInfoTO>>();
			}
			currentEventName = deviceInfoTo.getEventName();
			if (currentEventName.equals("Current RX bytes")) {
				tempRxValueList.add(deviceInfoTo);
			} else {
				if (tempRxValueList.size() > 0) {
					masterRxList.add(tempRxValueList);
				}

				tempRxValueList = new ArrayList<DeviceInfoTO>();
			}

			if (currentEventName.equals("Current TX bytes")) {
				tempTxValueList.add(deviceInfoTo);
			} else {
				if (tempTxValueList.size() > 0) {
					masterTxList.add(tempTxValueList);
				}

				tempTxValueList = new ArrayList<DeviceInfoTO>();
			}
			networkwiseGroupedMap.put(networkType, masterRxList);
			networkwiseTxGroupedMap.put(networkType, masterTxList);

		}

		// System.out.println(groupedHashmap);
		// System.out.println("networkwiseGroupedMap---------"+networkwiseGroupedMap);
		Iterator<String> networkTypeItr = networkwiseGroupedMap.keySet()
				.iterator();
		FTPReportHelper ftpRepHelper = new FTPReportHelper();
		while (networkTypeItr.hasNext()) {
			String networkName = networkTypeItr.next();
			// List<List<DeviceInfoTO>> masterRxList =
			// groupedHashmap.get(networkName);
			// System.out.println("networkName-----------"+networkName);
			networkWiseRxThroughput.put(networkName, ftpRepHelper
					.getCommonListForAllcycles(networkwiseGroupedMap
							.get(networkName)));
			// System.out.println("between");
			/*networkWiseRssiValue.put(networkName, ftpRepHelper
					.getRssiValue(networkwiseGroupedMap.get(networkName)));*/
			// System.out.println("after------------networkName----");
		}
		
		 networkTypeItr = networkwiseTxGroupedMap.keySet()
			.iterator();

			while (networkTypeItr.hasNext()) {
				String networkName = networkTypeItr.next();
				// List<List<DeviceInfoTO>> masterRxList =
				// groupedHashmap.get(networkName);
				// System.out.println("networkName-----------"+networkName);
				networkWiseTxThroughput.put(networkName, ftpRepHelper
						.getCommonListForAllcycles(networkwiseTxGroupedMap
								.get(networkName)));
				// System.out.println("between");
				/*networkWiseRssiValue.put(networkName, ftpRepHelper
						.getRssiValue(networkwiseGroupedMap.get(networkName)));*/
				// System.out.println("after------------networkName----");
			}
		combineAllCylesQualitywiToRender(networkWiseRxThroughput,"rx",market_id,test_name);
		combineAllCylesQualitywiToRender(networkWiseTxThroughput,"tx",market_id,test_name);

		/*
		 * finalEvaluatedMap.put("TxMap", networkWiseTxThroughput);
		 * finalEvaluatedMap.put("RxMap", networkWiseRxThroughput);
		 * finalEvaluatedMap.put("RssiMap", networkWiseRssiValue);
		 * 
		 * System.out.println("finalEvaluatedMap----------"+finalEvaluatedMap);
		 */

		// System.out.println("average rssi---------"+ftpRepHelper.getRssiValue(ftpCommonList));
		return finalEvaluatedMap;
	}

	public static HashMap<String, HashMap<String, DeviceInfoTO>> combineAllCylesQualitywiToRender(
			HashMap<String, HashMap<String, List<DeviceInfoTO>>> networkWiseRxThroughput,String type,String test_name,String market_id) {
		HashMap<String, HashMap<String, DeviceInfoTO>> networkWiseTxThroughput = new HashMap<String, HashMap<String, DeviceInfoTO>>();
		Iterator<String> networkItr = networkWiseRxThroughput.keySet()
				.iterator();
		while (networkItr.hasNext()) {
			HashMap<String, DeviceInfoTO> qualtiyWisemapCalMap = new HashMap<String, DeviceInfoTO>();
			String networkType = networkItr.next();
			HashMap<String, List<DeviceInfoTO>> qualtiyWisemap = networkWiseRxThroughput
					.get(networkType);
			qualtiyWisemapCalMap.put(ReportConstants.GOOD,
					getThroughputAvg(qualtiyWisemap.get(ReportConstants.GOOD)));
			qualtiyWisemapCalMap
					.put(ReportConstants.MEDIUM,
							getThroughputAvg(qualtiyWisemap
									.get(ReportConstants.MEDIUM)));
			qualtiyWisemapCalMap.put(ReportConstants.POOR,
					getThroughputAvg(qualtiyWisemap.get(ReportConstants.POOR)));
			networkWiseTxThroughput.put(networkType, qualtiyWisemapCalMap);
		}
		System.out.println("Srikant----------" + networkWiseTxThroughput);
		insertIntoThroughputTable(test_name,market_id,networkWiseTxThroughput,type);
		return networkWiseTxThroughput;
	}

	private static DeviceInfoTO getThroughputAvg(List<DeviceInfoTO> masterList) {
		DeviceInfoTO avgDto = new DeviceInfoTO();
		List<Float> throughputList = new ArrayList<Float>();
		List<Float> signalStrengthList = new ArrayList<Float>();
		System.out.println("masterList===="+masterList);
		if (null != masterList) {
			for (int i = 0; i < masterList.size(); i++) {
				DeviceInfoTO dto = masterList.get(i);
				if(null!=dto.getThroughput()){
					throughputList.add(new Float(dto.getThroughput()));
					signalStrengthList.add(new Float(dto.getSignalStrength()));
				}
				
			}
			avgDto.setThroughput(getAverageList(throughputList).toString());
			avgDto.setSignalStrength(getAverageList(signalStrengthList).toString());
		}

		
		return avgDto;
	}

	public static Float getAverageList(List<Float> list) {
		Float averagevalue = new Float(0);
		Float listSum = new Float(0);
		try {
			if (null != list) {
				System.out.println("list-----++-----" + list.size());
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

	public static void insertIntoThroughputTable(String testname, String market_id,HashMap<String, HashMap<String, 
			DeviceInfoTO>> networkWiseTxThroughput,String type) {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			Iterator<String> networkItr = networkWiseTxThroughput.keySet().iterator();
			while(networkItr.hasNext()){
				String networkType = networkItr.next();
				HashMap<String, DeviceInfoTO> qualityWiseMap = networkWiseTxThroughput.get(networkType);
				Iterator<String> qualityWiseItr = qualityWiseMap.keySet().iterator();
				while(qualityWiseItr.hasNext()){
					String quality = qualityWiseItr.next();
					DeviceInfoTO qualityDevice = qualityWiseMap.get(quality);
					String query = "INSERT INTO ftp_throughput VALUES ('"+quality+"', '"+testname+"', '"+market_id+"', '1', '"+qualityDevice.getThroughput()+"', '"+qualityDevice.getSignalStrength()+"','"+networkType+"','"+type+"')";
					System.out.println("query--------"+query);
					st.executeUpdate(query);
				}
				
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		/*  HashMap<String, List<List<DeviceInfoTO>>> networkwiseGroupedMap = new
		  HashMap<String, List<List<DeviceInfoTO>>>(); List<Stg_Log_Cat_TO>
		  getListLogcat = getLogcatData("g2"); List<DeviceInfoTO>
		  getListDeviceInfo = populateDataForFTP("g2","33");
		  HashMap<String,HashMap<String, HashMap<String,Float>>>
		  calculatedResultMap = new HashMap<String,
		  HashMap<String,HashMap<String,Float>>>(); List<DeviceInfoTO>
		  finalyList = getClosestEntries(getListLogcat,getListDeviceInfo); //
		 calculatedResultMap =
		  getClosestEntries(getListLogcat,getListDeviceInfo
		  ,networkwiseGroupedMap);
		  System.out.println("calculatedResultMap-------"+calculatedResultMap);
		 
		
		 * for(int i=0;i<getListDeviceInfo.size();i++){ DeviceInfoTO dto =
		 * getListDeviceInfo.get(i);
		 * System.out.println(dto.getSignalStrength()); }
		 
//		 insertIntoFtp(finalyList);
		insertInThroughPutTable("g2ref", "22");
		
		 * System.out.println("---getListLogcat----"+getListLogcat.size());
		 * System
		 * .out.println("---getListDeviceInfo----"+getListDeviceInfo.size());
		 */
		
		connectToMsAccess();
	}
	
	private static void connectToMsAccess(){
		 try
	        {
	            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
	            String database = 
	              "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=162.17.178.217\\VQT.mdb;";
	            Connection conn = DriverManager.getConnection(database, "GL", "gl");
	            Statement s = conn.createStatement();
	            
	            
	            // close and cleanup
	            s.close();
	            conn.close();
	        }
	        catch(Exception ex)
	        {
	            ex.printStackTrace();
	        }
	}
}
