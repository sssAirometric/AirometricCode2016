package com.report.dao.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.PropertyFileReader;
import com.constants.ReportConstants;
import com.dao.impl.ReportDaoImpl;
import com.model.DBUtil;
import com.mysql.jdbc.Constants;
import com.report.dao.DataConnectivityDao;
import com.report.helper.FTPReportHelper;
import com.to.DataConnectivityTo;
import com.to.DeviceInfoTO;
import com.to.Stg_Log_Cat_TO;
import com.util.DBHelper;

public class DataConnectivityDaoImpl implements DataConnectivityDao{
	
	
	public static Connection conn = null;
	public static Statement st = null;
	static HashMap<String,String>  propertiesFiledata =PropertyFileReader.getProperties();
	private final static String THROUGHPUT = propertiesFiledata.get("THROUGHPUT");
	
	public void openConn(){
		try{
			conn= DBUtil.getConnection();
			st = conn.createStatement();
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void closeConn(){
		try{
			st.close();
			conn.close();
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public List<DataConnectivityTo> getTestNameThroughputDetailsResults(
			String testCaseName,String marketId) {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		List<DataConnectivityTo> deviceInfosList = new ArrayList<DataConnectivityTo>();
		PreparedStatement pst = null;
		String test_name = testCaseName + "\\-%";
		try {
			
			String  query = "SELECT sdi.SIGNALSTRENGTH_GSMSIGNALSTRENGTH,sdi.SIGNALSTRENGTH_CDMADBM,sdi.SIGNALSTRENGTH_EVDODBM,"
				+ " sdi.SIGNALSTRENGTH_LTESIGNALSTRENGTH,sdi.SIGNALSTRENGTH_LTERSRP,sdi.NETWORK_TYPE,slc.EVENT_VALUE,slc.EVENT_NAME,sdi.GEOLOCATION_LATITUDE, sdi.GEOLOCATION_LANGITUDE ,"
				+ " DATE_FORMAT(slc.TIME_STAMP, '%Y-%m-%d %H:%i:%S')  AS TIMESTAMP ,sdi.CELLLOCATION_CID from stg_device_info sdi,stg_log_cat_info slc"
				+ " where sdi.TEST_NAME=slc.TEST_NAME and sdi.TEST_TYPE=slc.TEST_TYPE and sdi.TEST_IDENTIFIER_TIMESTAMP=slc.TEST_IDENTIFIER_TIMESTAMP and sdi.TEST_NAME LIKE ('"
				+ test_name
				+ "') and sdi.MARKET_ID IN('"
				+ marketId
				+ "') and slc.EVENT_NAME IN('Current TX bytes','Current RX bytes')"
				+ " and DATE_FORMAT(slc.TIME_STAMP, '%Y-%m-%d %H:%i:%S%')=DATE_FORMAT(sdi.TIME_STAMP_FOREACH_SAMPLE, '%Y-%m-%d %H:%i:%S%') "
				+ " GROUP BY DATE_FORMAT(slc.TIME_STAMP, '%Y-%m-%d %H:%i:%S') ";
			//System.out.println("query----srikanth------"+query);
			conn = DataConnectivityDaoImpl.conn;
			st = DataConnectivityDaoImpl.st;
			rs = st.executeQuery(query);
			while (rs.next()) {
				DataConnectivityTo deviceInfos = new DataConnectivityTo();
				deviceInfos.setTestName(testCaseName);
				deviceInfos.setSignalStrength(rs.getString(1));
				deviceInfos.setSignalStrengthCDMA(rs.getString(2));
				deviceInfos.setSignalStrengthEVDO(rs.getString(3));
				deviceInfos.setSignalStrengthLTE(rs.getString(4));
				Float lteSignalStrength = -140+new Float(rs.getString(5));
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
			e.printStackTrace();
		} /*finally {
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
		}*/
		return deviceInfosList;
	}


	public boolean getCycleTestNames(String testCaseName,String marketId) {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		String test_name = testCaseName + "\\-%";
		boolean status = false;
		try {
			conn = DataConnectivityDaoImpl.conn;
			st = DataConnectivityDaoImpl.st;
			String query = "SELECT DISTINCT COUNT(TEST_NAME) FROM STG_LOG_CAT_INFO ST ,STG_DEVICE_INFO SD  WHERE SD.TEST_NAME LIKE '"
					+ test_name + "' AND ST.MARKET_ID='"+ marketId + "' AND SD.TEST_NAME=ST.TEST_NAME  AND ST.TEST_TYPE=SD.TEST_TYPE" +
					" AND ST.TEST_IDENTIFIER_TIMESTAMP=SD.TEST_IDENTIFIER_TIMESTAMP ";
			rs = st.executeQuery(query);
			while (rs.next()) {
				int count = rs.getInt(1);
				if (count > 1) {
					status = true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} /*finally {
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
		}*/
		return status;
	}
	
	public List<DataConnectivityTo> getTestNameMultipleThroughputDetailsResults(
			String testCaseName,String marketId) {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		List<DataConnectivityTo> deviceInfosList = new ArrayList<DataConnectivityTo>();
		PreparedStatement pst = null;
		String test_name = testCaseName + "\\-%";
		try {
			conn = DataConnectivityDaoImpl.conn;
			st = DataConnectivityDaoImpl.st;
			String query = "SELECT sdi.SIGNALSTRENGTH_GSMSIGNALSTRENGTH,sdi.SIGNALSTRENGTH_CDMADBM,sdi.SIGNALSTRENGTH_EVDODBM,"
				+ " sdi.SIGNALSTRENGTH_LTESIGNALSTRENGTH,sdi.SIGNALSTRENGTH_LTERSRP,sdi.NETWORK_TYPE,slc.EVENT_VALUE,slc.EVENT_NAME,sdi.GEOLOCATION_LATITUDE, sdi.GEOLOCATION_LANGITUDE ,"
				+ " DATE_FORMAT(slc.TIME_STAMP, '%Y-%m-%d %H:%i:%S')  AS TIMESTAMP ,sdi.CELLLOCATION_CID from stg_device_info sdi,stg_log_cat_info slc"
				+ " where sdi.TEST_NAME=slc.TEST_NAME and sdi.TEST_TYPE=slc.TEST_TYPE and sdi.TEST_IDENTIFIER_TIMESTAMP=slc.TEST_IDENTIFIER_TIMESTAMP and slc.TEST_NAME='"
				+ testCaseName
				+ "'and sdi.MARKET_ID='"
				+ marketId
				+ "'and slc.EVENT_NAME IN('Current TX bytes','Current RX bytes')"
				+ " and DATE_FORMAT(slc.TIME_STAMP, '%Y-%m-%d %H:%i:%S%')=DATE_FORMAT(sdi.TIME_STAMP_FOREACH_SAMPLE, '%Y-%m-%d %H:%i:%S%') "
				+ " GROUP BY DATE_FORMAT(slc.TIME_STAMP, '%Y-%m-%d %H:%i:%S') ";
			rs = st.executeQuery(query);
			while (rs.next()) {
				DataConnectivityTo deviceInfos = new DataConnectivityTo();
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
			e.printStackTrace();
		} /*finally {
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
		}*/
		return deviceInfosList;
	}


	public String getThroughput(int i,double txvalue, double rxvalue,
			double mainValue, String eventName, String currTxBytes,
			String currRxBytes, String prevRxBytes, String prevTxBytes,
			String throughputRX, String throughputTX ,DataConnectivityTo deviceInfo,String THROUGHPUT) {
		 String throughputMain="";
		if(i==0){
			if(eventName.equalsIgnoreCase("Current TX bytes")){
				currTxBytes=deviceInfo.getEventValue();
				double h=Double.parseDouble(currTxBytes);
				//double k=30*1024;
				double k=Double.parseDouble(THROUGHPUT);
				txvalue=h*k;
				throughputTX=String.valueOf(txvalue);
				prevTxBytes=currTxBytes;
			}
            if(eventName.equalsIgnoreCase("Current RX bytes")){
            	currRxBytes=deviceInfo.getEventValue();
            	double h=Double.parseDouble(currRxBytes);
				//double k=30*1024;
            	double k=Double.parseDouble(THROUGHPUT);
				rxvalue=h*k;
            	throughputRX=String.valueOf(rxvalue);
            	prevRxBytes=currRxBytes;
			}
            if(throughputTX=="" ){
            	throughputTX="0";
            }
            if(throughputRX=="" ){
            	throughputRX="0";
            }
            mainValue=txvalue+rxvalue;
            mainValue=round(mainValue, 2);
            if(mainValue<0){
            	mainValue=0;
            }
            throughputMain=String.valueOf(mainValue);
	}else{
            if(eventName.equalsIgnoreCase("Current TX bytes")){
            	currTxBytes=deviceInfo.getEventValue();
            	if(currTxBytes==""){
            		currTxBytes="0";
            	}
            	if(prevTxBytes==""){
            		prevTxBytes="0";
            	}
            	double h=Double.parseDouble(currTxBytes);
            	double d=Double.parseDouble(prevTxBytes);
				//double k=30*1024;
				double k=Double.parseDouble(THROUGHPUT);
				txvalue=(h-d)*k;
				throughputTX=String.valueOf(txvalue);
				prevTxBytes=currTxBytes;
			}
            if(eventName.equalsIgnoreCase("Current RX bytes")){
            	currRxBytes=deviceInfo.getEventValue();
            	if(currRxBytes==""){
            		currRxBytes="0";
            	}
            	if(prevRxBytes==""){
            		prevRxBytes="0";
            	}
				double h=Double.parseDouble(currRxBytes);
            	double d=Double.parseDouble(prevRxBytes);
				//double k=30*1024;
            	double k=Double.parseDouble(THROUGHPUT);
				rxvalue=(h-d)*k;
				prevRxBytes=currRxBytes;
			}
            if(throughputTX=="" ){
            	throughputTX="0";
            }
            if(throughputRX=="" ){
            	throughputRX="0";
            }
            mainValue=txvalue+rxvalue;
            mainValue=round(mainValue, 2);
            txvalue=round(mainValue, 2);
            rxvalue=round(rxvalue, 2);
            if(mainValue<0){
            	mainValue=0;
            }
            throughputMain=String.valueOf(mainValue);
	  }
		return txvalue+"(uplink)<b>/</b>"+rxvalue+"(downlink)"; 
	}
	 public static double round(double targetValue, int roundToDecimalPlaces ){
	        int valueInTwoDecimalPlaces = (int) (targetValue * Math.pow(10, roundToDecimalPlaces));
	        return (double) (valueInTwoDecimalPlaces / Math.pow(10, roundToDecimalPlaces));
	    }
	 
	 public String getMarketName(String marketId) {
			String marketName = "";
			String query = "SELECT MARKET_NAME FROM MARKET WHERE MARKET_ID='"+ marketId + "'";
			Connection conn = null;
			Statement st = null;
			ResultSet rs = null;
			PreparedStatement pst = null;
			String deviceId = "";
			try {
				conn = DataConnectivityDaoImpl.conn;
				st = DataConnectivityDaoImpl.st;
				rs = st.executeQuery(query);
				while (rs.next()) {
					marketName = rs.getString("MARKET_NAME");
				}
			} catch (Exception e) {
				e.printStackTrace();
			} /*finally {
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
			}*/
			return marketName;

		}
	 public HashMap<String,List<Float>> populateDataForTCP(String deviceName,
				String testName, String marketId) {	
				Connection conn = null;
				Statement st = null;
				ResultSet rs = null;
		 		HashMap<String,List<Float>> dataMap = new HashMap<String, List<Float>>();
				List<Float> tcpDownloadSpeedList = new ArrayList<Float>();
				List<Float> tcpUploadSpeedList = new ArrayList<Float>();
				List<Float> ressList = new ArrayList<Float>();
				List<Float> wifiList = new  ArrayList<Float>();
				List<Float> wifiUploadList = new  ArrayList<Float>();
				List<Float> wifiRssList = new  ArrayList<Float>();
				String query = "SELECT  S1.NetworkType AS 'NetworkType', 'Good' AS QUALITY,AVG(S.TCPDownloadSpeed),AVG(S.TCPUploadSpeed),AVG(S.RSSI) FROM "+
				"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
						"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"') S1    ON S.MARKET_ID='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -45 AND S.RSSI > -70 AND "+
						"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType AND  S.NetSpeedTest  LIKE 'TCP' AND S.TCPDownloadSpeed NOT LIKE 'Empty' GROUP BY QUALITY,S1.NetworkType "+


						" UNION "+

						"SELECT  S1.NetworkType AS 'NetworkType', 'Medium' AS QUALITY ,AVG(S.TCPDownloadSpeed),AVG(S.TCPUploadSpeed),AVG(S.RSSI) FROM "+
						"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
						"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"') S1    ON S.MARKET_ID ='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -71 AND S.RSSI > -90 AND "+
						"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType AND  S.NetSpeedTest  LIKE 'TCP' AND S.TCPDownloadSpeed NOT LIKE 'Empty' GROUP BY QUALITY,S1.NetworkType "+


						" UNION "+

						"SELECT  S1.NetworkType AS 'NetworkType', 'Poor' AS QUALITY ,AVG(S.TCPDownloadSpeed),AVG(S.TCPUploadSpeed),AVG(S.RSSI) FROM "+
						"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
						"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"') S1    ON S.MARKET_ID ='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -103 AND "+
						"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType  AND  S.NetSpeedTest  LIKE 'TCP' AND S.TCPDownloadSpeed NOT LIKE 'Empty' GROUP BY QUALITY,S1.NetworkType "+

						"Order by NetworkType, QUALITY";
				try {
					System.out.println("query---populateDataForTCP------"+query);
					conn = DataConnectivityDaoImpl.conn;
					st = DataConnectivityDaoImpl.st;
					rs = st.executeQuery(query);
					while (rs.next()) {
						DataConnectivityTo dcTo = new DataConnectivityTo();
						Float tCPDownloadSpeed = rs.getFloat("AVG(S.TCPDownloadSpeed)");
						Float tCPUploadSpeed = rs.getFloat("AVG(S.TCPUploadSpeed)");
						Float rssi = rs.getFloat("AVG(S.RSSI)");
						String netWorkType = rs.getString("NetworkType");
						////System.out.println("rssi-----------"+rssi);
						if(netWorkType.equalsIgnoreCase("WIFI")){
							if(null==tCPDownloadSpeed || tCPDownloadSpeed==0){
								wifiList.add(null);
							}
							else{
								wifiList.add(tCPDownloadSpeed);
							}
							if(null==tCPUploadSpeed || tCPUploadSpeed==0){
								wifiUploadList.add(null);
							}
							else{
								wifiUploadList.add(tCPUploadSpeed);
							}
							if(rssi == 0 ||null==rssi){
								wifiRssList.add(null);
							}
							else{
								wifiRssList.add(rssi);
							}
						}
						else{
							wifiList.add(null);
							wifiUploadList.add(null);
							wifiRssList.add(null);
						}
						if(!netWorkType.equalsIgnoreCase("WIFI")){
							if(null==tCPDownloadSpeed || tCPDownloadSpeed==0){
								tcpDownloadSpeedList.add(null);
								tcpUploadSpeedList.add(null);
							}
							else{
								tcpDownloadSpeedList.add(tCPDownloadSpeed);
								tcpUploadSpeedList.add(tCPUploadSpeed);
							}
							if(rssi == 0 ||null==rssi){
								ressList.add(null);
							}
							else{
								ressList.add(rssi);
							}
						}
						else{
							ressList.add(null);
							tcpDownloadSpeedList.add(null);
							tcpUploadSpeedList.add(null);
						}
					
						
					}
					dataMap.put("tcpDownloadSpeedList", tcpDownloadSpeedList);
					dataMap.put("tcpUploadSpeedList", tcpUploadSpeedList);
					dataMap.put("ressList", ressList);
					dataMap.put("wifiList", wifiList);
					dataMap.put("wifirssiList", wifiRssList);
					dataMap.put("wifiUploadList", wifiUploadList);
					
					//System.out.println("dataMap---------"+dataMap);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				return dataMap;}
		
	 
	 public HashMap<String,List<Float>> populateDataForUDP(String deviceName,
				String testName, String marketId) {HashMap<String,List<Float>> dataMap = new HashMap<String, List<Float>>();
				List<Float> udpDownloadSpeedList = new ArrayList<Float>();
				List<Float> udpDownloadCapacityList = new ArrayList<Float>();
				List<Float> udpUploadCapacityList = new ArrayList<Float>();
				List<Float> udpDownloadQOSList = new ArrayList<Float>();
				List<Float> ressList = new ArrayList<Float>();
				List<Float> wifirssList = new ArrayList<Float>();
				List<Float> wifiDCList = new ArrayList<Float>();
				List<Float> wifiDCUploadCapacityList = new ArrayList<Float>();
				List<Float> wifiQosList = new ArrayList<Float>();
				Connection conn = null;
				Statement st = null;
				ResultSet rs = null;
				String query = "SELECT  S1.NetworkType AS 'NetworkType', 'Good' AS QUALITY,AVG(S.UDPDownloadCapacity),AVG(S.UDPUploadCapacity),AVG(S.UDPDownloadQOS),AVG(S.RSSI) FROM "+
				"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
						"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"') S1    ON S.MARKET_ID='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -45 AND S.RSSI > -70 AND "+
						"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType  AND  S.NetSpeedTest  LIKE 'UDP' AND S.UDPDownloadQOS NOT LIKE 'Empty' GROUP BY QUALITY,S1.NetworkType "+


						" UNION "+

						"SELECT  S1.NetworkType AS 'NetworkType', 'Medium'  AS QUALITY,AVG(S.UDPDownloadCapacity),AVG(S.UDPUploadCapacity),AVG(S.UDPDownloadQOS),AVG(S.RSSI) FROM "+
						"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
						"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"') S1    ON S.MARKET_ID ='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -71 AND S.RSSI > -90 AND "+
						"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType  AND  S.NetSpeedTest  LIKE 'UDP' AND S.UDPDownloadQOS NOT LIKE 'Empty' GROUP BY QUALITY,S1.NetworkType "+


						" UNION "+

						"SELECT  S1.NetworkType AS 'NetworkType', 'Poor' AS QUALITY,AVG(S.UDPDownloadCapacity),AVG(S.UDPUploadCapacity),AVG(S.UDPDownloadQOS),AVG(S.RSSI) FROM "+
						"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
						"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"') S1    ON S.MARKET_ID ='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -103 AND "+
						"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType AND  S.NetSpeedTest  LIKE 'UDP' AND S.UDPDownloadQOS NOT LIKE 'Empty' GROUP BY QUALITY,S1.NetworkType "+

						"Order by NetworkType, QUALITY";
				try {
					//System.out.println("query---populateDataForUDP-----------"+query);
					conn = DataConnectivityDaoImpl.conn;
					st = DataConnectivityDaoImpl.st;
					rs = st.executeQuery(query);
					while (rs.next()) {
						Float udpDownloadCapacity = rs.getFloat("AVG(S.UDPDownloadCapacity)");
						Float udpUploadCapacity = rs.getFloat("AVG(S.UDPUploadCapacity)");
						Float udpDownloadQOS =rs.getFloat("AVG(S.UDPDownloadQOS)");
						Float rssi = rs.getFloat("AVG(S.RSSI)");
						String networkType =  rs.getString("NetworkType");
						////System.out.println("rssi-----------"+rssi);
//						//System.out.println("udpDownloadCapacity-------"+udpDownloadCapacity);
						if(networkType.equalsIgnoreCase("WIFI")){
							if(null==udpDownloadCapacity || udpDownloadCapacity==0){
								wifiDCList.add(null);
							}
							else{
								wifiDCList.add(udpDownloadCapacity);
							}
							if(null==udpUploadCapacity || udpUploadCapacity==0){
								wifiDCUploadCapacityList.add(null);
							}
							else{
								wifiDCUploadCapacityList.add(udpUploadCapacity);
							}
							if(null==udpDownloadQOS || udpDownloadQOS==0){
								wifiQosList.add(null);
							}
							else{
								wifiQosList.add(udpDownloadQOS);
							}
							if(rssi == 0 ||null==rssi){
								wifirssList.add(null);
							}
							else{
								wifirssList.add(rssi);
							}
						}
						else{
							wifiQosList.add(null);
							wifiDCList.add(null);
							wifiDCUploadCapacityList.add(null);
							wifirssList.add(null);
						}
						if(!networkType.equalsIgnoreCase("WIFI")){
							//System.out.println("udpUploadCapacity--------------"+udpUploadCapacity);
							//System.out.println("rssi-------------"+rssi);
							if(null==udpDownloadCapacity || udpDownloadCapacity==0){
								udpDownloadCapacityList.add(null);
							}
							else{
								udpDownloadCapacityList.add(udpDownloadCapacity);
							}
							if(null==udpUploadCapacity || udpUploadCapacity==0){
								udpUploadCapacityList.add(null);
							}
							else{
								udpUploadCapacityList.add(udpUploadCapacity);
							}
							if(null==udpDownloadQOS || udpDownloadQOS==0){
								udpDownloadQOSList.add(null);
							}
							else{
								udpDownloadQOSList.add(udpDownloadQOS);
							}
							if(rssi == 0 ||null==rssi){
								ressList.add(null);
							}
							else{
								ressList.add(rssi);
							}
						}
						else{
							udpDownloadCapacityList.add(null);
							udpUploadCapacityList.add(null);
							udpDownloadQOSList.add(null);
							ressList.add(null);
						}
						
					
						
						
					}
					dataMap.put("udpDownloadQOSList", udpDownloadQOSList);
					dataMap.put("udpDownloadCapacityList", udpDownloadCapacityList);
					dataMap.put("udpUploadCapacityList", udpUploadCapacityList);
					dataMap.put("ressList", ressList);
					dataMap.put("wifiDownloadList", wifiDCList);
					dataMap.put("wifiUploadList", wifiDCUploadCapacityList);
					dataMap.put("wifiQosList", wifiQosList);
					dataMap.put("wifiRssiList", wifirssList);
					//System.out.println("dataMap------udp-------"+dataMap);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				return dataMap;}
	 
	 
	 public HashMap<String,List<Float>> populateDataForDNS(String deviceName,
				String testName, String marketId) {HashMap<String,List<Float>> dataMap = new HashMap<String, List<Float>>();
				List<Float> udpDownloadSpeedList = new ArrayList<Float>();
				List<Float> dnsAvLatencyList = new ArrayList<Float>();
				List<Float> dnsMaxLatencyList = new ArrayList<Float>();
				List<Float> ressList = new ArrayList<Float>();
				List<Float> wifiudpDownloadSpeedList = new ArrayList<Float>();
				List<Float> wifidnsAvLatencyList = new ArrayList<Float>();
				List<Float> wifidnsMaxLatencyList = new ArrayList<Float>();
				List<Float> wifiressList = new ArrayList<Float>();
				Connection conn = null;
				Statement st = null;
				ResultSet rs = null;
				String query = "SELECT  S1.NetworkType AS 'NetworkType', 'Good' AS QUALITY,AVG(S.DNSAvLatency),AVG(S.DNSMaxLatency),AVG(S.RSSI) FROM "+
						"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
						"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"') S1    ON S.MARKET_ID='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -45 AND S.RSSI > -70 AND "+
						"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType  AND  S.NetSpeedTest  LIKE 'DNS' AND S.DNSAvLatency NOT LIKE 'Empty' GROUP BY QUALITY,S1.NetworkType "+


						" UNION "+

						"SELECT  S1.NetworkType AS 'NetworkType', 'Medium'  AS QUALITY,AVG(S.DNSAvLatency),AVG(S.DNSMaxLatency),AVG(S.RSSI) FROM "+
						"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
						"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"') S1    ON S.MARKET_ID ='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -71 AND S.RSSI > -90 AND "+
						"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType  AND  S.NetSpeedTest  LIKE 'DNS' AND S.DNSAvLatency NOT LIKE 'Empty' GROUP BY QUALITY,S1.NetworkType "+


						" UNION "+

						"SELECT  S1.NetworkType AS 'NetworkType', 'Poor' AS QUALITY,AVG(S.DNSAvLatency),AVG(S.DNSMaxLatency),AVG(S.RSSI) FROM "+
						"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
						"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"') S1    ON S.MARKET_ID ='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -103 AND "+
						"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType  AND  S.NetSpeedTest  LIKE 'DNS' AND S.DNSAvLatency NOT LIKE 'Empty' GROUP BY QUALITY,S1.NetworkType "+

						"Order by NetworkType, QUALITY";
				try {
					//System.out.println("query---populateDataForDNS------"+query);
					conn = DataConnectivityDaoImpl.conn;
					st = DataConnectivityDaoImpl.st;
					rs = st.executeQuery(query);
					while (rs.next()) {
						DataConnectivityTo dcTo = new DataConnectivityTo();
						Float dnsAvLatency = rs.getFloat("AVG(S.dnsAvLatency)");
						Float dnsMaxLatency = rs.getFloat("AVG(S.DNSMaxLatency)");
						Float rssi = rs.getFloat("AVG(S.RSSI)");
						String networkType = rs.getString("NetworkType");
						if(networkType.equalsIgnoreCase("WIFI")){
							if(null==dnsMaxLatency || dnsMaxLatency==0){
								wifidnsMaxLatencyList.add(null);
							}
							else{
								wifidnsMaxLatencyList.add(dnsMaxLatency);
							}
							if(null==dnsAvLatency || dnsAvLatency== 0){
								wifidnsAvLatencyList.add(null);
							}
							else{
								wifidnsAvLatencyList.add(dnsAvLatency);
							}
							
							if(rssi == 0 || null==rssi){
								wifiressList.add(null);
							}
							else{
								wifiressList.add(rssi);
							}
						}
						else{
							wifiressList.add(null);
							wifidnsMaxLatencyList.add(null);
							wifidnsAvLatencyList.add(null);
						}
				if(!networkType.equalsIgnoreCase("WIFI")){
					if(null==dnsMaxLatency || dnsMaxLatency==0){
						dnsMaxLatencyList.add(null);
					}
					else{
						dnsMaxLatencyList.add(dnsMaxLatency);
					}
					if(null==dnsAvLatency || dnsAvLatency== 0){
						dnsAvLatencyList.add(null);
					}
					else{
						dnsAvLatencyList.add(dnsAvLatency);
					}
					
					if(rssi == 0 || null==rssi){
						ressList.add(null);
					}
					else{
						ressList.add(rssi);
					}
				}
				else{
					ressList.add(null);
					dnsAvLatencyList.add(null);
					dnsMaxLatencyList.add(null);
				}
						
					}
					dataMap.put("dnsMaxLatencyList", dnsMaxLatencyList);
					dataMap.put("dnsAvgLatencyList", dnsAvLatencyList);
					dataMap.put("dnsRssList", ressList);
					dataMap.put("wifidnsAvLatencyList", wifidnsAvLatencyList);
					dataMap.put("wifidnsMaxLatencyList", wifidnsMaxLatencyList);
					dataMap.put("wifiressList", wifiressList);
					////System.out.println("dataMap------DNS-------"+dataMap);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				return dataMap;
				}
	 
	 
	/* public HashMap<String,List<Float>> populateDataForFTP(String deviceName,
				String testName, String marketId,List<String> networkList) { 
		 //System.out.println("networkList----++------"+networkList);
			HashMap<String,List<Float>> dataMap = new HashMap<String, List<Float>>();
			List<Float> ftpThroughputList = new ArrayList<Float>();
			List<Float> rssList = new ArrayList<Float>();
			List<Float> rxThroughputList = new ArrayList<Float>();
			List<Float> txThroughputList = new ArrayList<Float>();
			List<Float> wififtpThroughputList = new ArrayList<Float>();
			List<Float> wifirssList = new ArrayList<Float>();
			HashMap<String, List<Float>> rssMap = new HashMap<String, List<Float>>();
			HashMap<String, HashMap<String, List<Float>>> networkWiseMap = new HashMap<String, HashMap<String, List<Float>>>();
			HashMap<String, List<Float>> throughputMap = new HashMap<String, List<Float>>();
			HashMap<String, List<Float>> rxMap = new HashMap<String, List<Float>>();
			HashMap<String, List<Float>> txMap = new HashMap<String, List<Float>>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat newSdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
			try {
//				List<DeviceInfo> deviceInfoList = getTestNameMultipleThroughputDetailsResultsForFTP(testName,marketId);
				HashMap<String,Integer> networkMap = getTestNameMultipleThroughputDetailsResultsForFTP(testName,marketId);
				//System.out.println("networkMap-----------------"+networkMap);
				Date date = null;//sdf.parse("2013-07-01 16:46:01");
				for(int i=0;i<networkList.size();i++){
					networkWiseMap.put("rss_"+networkList.get(i), new HashMap<String, List<Float>>());
					networkWiseMap.put("throughput_"+networkList.get(i), new HashMap<String, List<Float>>());
					networkWiseMap.put("rx_"+networkList.get(i), new HashMap<String, List<Float>>());
				}
				Iterator<String> networkItr = networkMap.keySet().iterator();
//				for(int k=0;k<deviceInfoList.size();k++){
				while(networkItr.hasNext()){
					DeviceInfo deviceInfo = deviceInfoList.get(k);
					String 
					String network = networkItr.next();
					int min = -103;
			    	int max = -0;
			    	Random ran = new Random();
			    	// Assumes max and min are non-negative.
			    	int rss = min + ran.nextInt(max - min + 1);
					float rxvalue = new Float(networkMap.get(network))/(1024*1024);
					float signalStrength = new Float(networkMap.get(network))/(1024*1024);
//					float throughput = new Float(deviceInfo.getThroughputmain())/(1024*1024);
//					String timeStamp = deviceInfo.getTimeStampForEachSample();
//					date =sdf.parse(timeStamp);
					if(!network.equalsIgnoreCase("UNKNOWN")){
						////System.out.println("getRssQuery-----------------"+getRssQuery);
						////System.out.println("networkWiseMap-----------"+networkWiseMap);
//						 signalStrength = -113+2*new Float(signalStrength);
						////System.out.println("throughput--------------"+throughput);
						////System.out.println("rssValue---------"+rssValue);
						////System.out.println("deviceInfo.getnetwork------"+deviceInfo.getNetworkType());
						rssMap = networkWiseMap.get("rss_"+network);
						rxMap = networkWiseMap.get("rx_"+network);
						if(signalStrength<-45 && signalStrength>-70){
							List<Float> rssGood = rssMap.get("Good");
							List<Float> rxGood = rxMap.get("Good");
							List<Float> throughPutGood = throughputMap.get("Good");
							if(null==rxGood){
								rssGood = new ArrayList<Float>();
								throughPutGood = new ArrayList<Float>();
								rxGood = new ArrayList<Float>();
							}
							////System.out.println("rssGood--------"+rssGood);
							rssGood.add(signalStrength);
							rssMap.put("Good", rssGood);
							rxGood.add(rxvalue);
							rxMap.put("Good", rxGood);
							////System.out.println("rssMap---in good condition-------"+rssMap);
//							throughPutGood.add(throughput);
							throughputMap.put("Good", throughPutGood);
							////System.out.println("rssMap---in good condition----throughput---"+rssMap);
						}
						else if(signalStrength<-71 && signalStrength>-90){
							List<Float> rssMedium = rssMap.get("Medium");
							List<Float> throughPutMedium = throughputMap.get("Medium");
							List<Float> rxMedium = rxMap.get("Medium");
							if(null==rxMedium){
								rssMedium = new ArrayList<Float>();
								throughPutMedium = new ArrayList<Float>();
								rxMedium = new ArrayList<Float>();
							}
							rssMedium.add(signalStrength);
							rssMap.put("Medium", rssMedium);
//							throughPutMedium.add(throughput);
							throughputMap.put("Medium", throughPutMedium);
							rxMedium.add(rxvalue);
							rxMap.put("Medium", rxMedium);
					}
						else if(signalStrength<-91 && signalStrength>-103){
							List<Float> rssPoor = rssMap.get("Poor");
							List<Float> throughPutPoor = throughputMap.get("Poor");
							List<Float> rxPoor = rxMap.get("Poor");
							if(null==rssPoor){
								rssPoor = new ArrayList<Float>();
								throughPutPoor = new ArrayList<Float>();
								rxPoor = new ArrayList<Float>();
							}
							rssPoor.add(signalStrength);
							rssMap.put("Poor", rssPoor);
//							throughPutPoor.add(throughput);
							throughputMap.put("Poor", throughPutPoor);
							rxPoor.add(rxvalue);
							rxMap.put("Poor", rxPoor);
						}
						////System.out.println("rssMap---before adding--------"+rssMap);
						networkWiseMap.put("rss_"+network, rssMap);
						////System.out.println("networkWiseMap------after adding------"+networkWiseMap);
						networkWiseMap.put("throughput_"+network, throughputMap);
						
						networkWiseMap.put("rx_"+network, rxMap);
					}
				}
				//System.out.println("rxMap---------"+rxMap);
				//System.out.println("networkList------------"+networkList);
				for(int i=0;i<networkList.size();i++){
					List<Float> valueList = new ArrayList<Float>();
					List<Float> txvalueList = new ArrayList<Float>();
					//System.out.println("networkList.get(i)-----------"+networkList.get(i));
					//System.out.println(networkMap.get("tx_"+networkList.get(i)));
					int min = -103;
			    	int max = -0;
			    	Random ran = new Random();
			    	// Assumes max and min are non-negative.
			    	int signalStrength = min + ran.nextInt(max - min + 1);
					if(null!=networkMap.get("rx_"+networkList.get(i))){
						
						Float rxthroughput = new Float(networkMap.get("rx_"+networkList.get(i)))/(1024*1024);;
						
						if(signalStrength<-45 && signalStrength>-70){
							valueList.add(rxthroughput);
							valueList.add(null);
							valueList.add(null);
						}
						else if(signalStrength<-71 && signalStrength>-90){
							valueList.add(null);
							valueList.add(rxthroughput);
							valueList.add(null);
						}
						else if(signalStrength<-91 && signalStrength>-103){
							valueList.add(null);
							valueList.add(null);
							valueList.add(rxthroughput);
						}
					}
					else{
						valueList.add(null);
						valueList.add(null);
						valueList.add(null);
					}
				
					
					if(null!=networkMap.get("tx_"+networkList.get(i))){
						
						Float txthroughput = new Float(networkMap.get("tx_"+networkList.get(i)))/(1024*1024);;
						
						if(signalStrength<-45 && signalStrength>-70){
							txvalueList.add(txthroughput);
							txvalueList.add(null);
							txvalueList.add(null);
						}
						else if(signalStrength<-71 && signalStrength>-90){
							txvalueList.add(null);
							txvalueList.add(txthroughput);
							txvalueList.add(null);
						}
						else if(signalStrength<-91 && signalStrength>-103){
							txvalueList.add(null);
							txvalueList.add(null);
							txvalueList.add(txthroughput);
						}
					}
					else{
						txvalueList.add(null);
						txvalueList.add(null);
						txvalueList.add(null);
					}
					
					if(!networkList.get(i).equalsIgnoreCase("UNKNOWN")){
						if(networkList.get(i).equalsIgnoreCase("WIFI")){
							rssMap = networkWiseMap.get("rss_"+networkList.get(i));
							throughputMap = networkWiseMap.get("throughput_"+networkList.get(i));
							wifirssList.add(getAverageList(rssMap.get("Good")));
							wifirssList.add(getAverageList(rssMap.get("Medium")));
							wifirssList.add(getAverageList(rssMap.get("Poor")));
							wififtpThroughputList.add(getAverageList(throughputMap.get("Good")));
							wififtpThroughputList.add(getAverageList(throughputMap.get("Medium")));
							wififtpThroughputList.add(getAverageList(throughputMap.get("Poor")));
						}
						else{
							rssMap = networkWiseMap.get("rss_"+networkList.get(i));
							throughputMap = networkWiseMap.get("throughput_"+networkList.get(i));
							rssList.add(getAverageList(rssMap.get("Good")));
							rssList.add(getAverageList(rssMap.get("Medium")));
							rssList.add(getAverageList(rssMap.get("Poor")));
							ftpThroughputList.add(getAverageList(throughputMap.get("Good")));
							ftpThroughputList.add(getAverageList(throughputMap.get("Medium")));
							ftpThroughputList.add(getAverageList(throughputMap.get("Poor")));
							
							
						}
					
						//System.out.println("rxThroughputList----------"+rxThroughputList);
						rxThroughputList.addAll(valueList);
						txThroughputList.addAll(txvalueList);
						}
				}
				
				dataMap.put("ftpThroughputList", rxThroughputList);
				dataMap.put("ftptxThroughputList", txThroughputList);
				dataMap.put("ftpRssList", rssList);
				dataMap.put("wififtpThroughputList", wififtpThroughputList);
				dataMap.put("wifirssList", wifirssList);
				dataMap.put("Good_throughput", throughputMap.get("Good"));
				dataMap.put("Poor_throughput", throughputMap.get("Poor"));
				dataMap.put("Medium_throughput", throughputMap.get("Medium"));
				dataMap.put("Good_rssi", rssMap.get("Good"));
				dataMap.put("Poor_rssi", rssMap.get("Poor"));
				dataMap.put("Medium_rssi", rssMap.get("Medium"));
				
				dataMap.put("Good_rx", rxMap.get("Good"));
				dataMap.put("Poor_rx", rxMap.get("Poor"));
				dataMap.put("Medium_rx", rxMap.get("Medium"));
				
				//System.out.println("dataMap------------------"+dataMap);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return dataMap;
			}
		*/
		
		public net.sf.json.JSONArray getNetworksTypeForMarket(String marketId){
			Connection conn = null;
			Statement st = null;
			ResultSet rs = null;
			List<String> strengthTypeList = new ArrayList<String>(3);
			strengthTypeList.add("Good");
			strengthTypeList.add("Medium");
			strengthTypeList.add("Poor");
			String query = "SELECT distinct NetworkType from STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"' GROUP BY NetworkType";
			net.sf.json.JSONObject networkAddJson = new net.sf.json.JSONObject();
			net.sf.json.JSONArray categoriesJsonArr = new net.sf.json.JSONArray();
			String currentNetwork = "";
			String oldTargetServerIp = "";
			try {
				conn = DataConnectivityDaoImpl.conn;
				st = DataConnectivityDaoImpl.st;
				rs = st.executeQuery(query);
			//	//System.out.println("query----------------------"+query);
				while(rs.next()){
					networkAddJson = new net.sf.json.JSONObject();
					currentNetwork = rs.getString("NetworkType");
					if(!currentNetwork.equalsIgnoreCase("UNKNOWN")){
						networkAddJson.put("name", currentNetwork);
						networkAddJson.put("categories", strengthTypeList);
						categoriesJsonArr.add(networkAddJson);
					}
						
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return categoriesJsonArr;
		}
		
		public net.sf.json.JSONArray getNetworksTypeJsonFtpForMarket(String marketId,String test_Name){
			Connection conn = null;
			Statement st = null;
			ResultSet rs = null;
			List<String> strengthTypeList = new ArrayList<String>(3);
			/*strengthTypeList.add("Good");
			strengthTypeList.add("Medium");
			strengthTypeList.add("Poor");*/
			String query = "SELECT distinct NETWORK_TYPE from stg_device_info WHERE MARKET_ID ='"+marketId+"' AND TEST_NAME LIKE '"+test_Name+"%' AND TEST_TYPE='FTP' ORDER BY NETWORK_TYPE";
			net.sf.json.JSONObject networkAddJson = new net.sf.json.JSONObject();
			net.sf.json.JSONArray categoriesJsonArr = new net.sf.json.JSONArray();
			String currentNetwork = "";
			String oldTargetServerIp = "";
			//System.out.println("query-------ftp network-----"+query);
			try {
				conn = DataConnectivityDaoImpl.conn;
				st = DataConnectivityDaoImpl.st;
				rs = st.executeQuery(query);
				while(rs.next()){
					networkAddJson = new net.sf.json.JSONObject();
					////System.out.println("rs---------"+rs);
					currentNetwork = rs.getString("NETWORK_TYPE");
					if(!currentNetwork.equalsIgnoreCase("UNKNOWN")){
						networkAddJson.put("categories", currentNetwork);
//						networkAddJson.put("categories", strengthTypeList);
						categoriesJsonArr.add(currentNetwork);
					}
						
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return categoriesJsonArr;
		}
		
		public List<String> getNetworksTypeListFtpForMarket(String marketId){
			Connection conn = null;
			Statement st = null;
			ResultSet rs = null;
			List<String> networkList = new ArrayList<String>();
			String query = "SELECT distinct NETWORK_TYPE from stg_device_info WHERE MARKET_ID ='"+marketId+"' ORDER BY NETWORK_TYPE";
			net.sf.json.JSONObject networkAddJson = new net.sf.json.JSONObject();
			net.sf.json.JSONArray categoriesJsonArr = new net.sf.json.JSONArray();
			String currentNetwork = "";
			String oldTargetServerIp = "";
			//System.out.println("query---getNetworksTypeListFtpForMarket-----"+query);
			try {
				conn = DataConnectivityDaoImpl.conn;
				st = DataConnectivityDaoImpl.st;
				rs = st.executeQuery(query);
				while(rs.next()){
					networkAddJson = new net.sf.json.JSONObject();
					currentNetwork = rs.getString("NETWORK_TYPE");
					networkList.add(currentNetwork);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return networkList;
		}
		
		public List<String> getNetworksTypeListFtpForMarketForLte(String marketId){
			Connection conn = null;
			Statement st = null;
			ResultSet rs = null;
			List<String> networkList = new ArrayList<String>();
			String query = "SELECT distinct NETWORK_TYPE from stg_device_info WHERE MARKET_ID ='"+marketId+"' AND NETWORK_TYPE like '%LTE%'";
			net.sf.json.JSONObject networkAddJson = new net.sf.json.JSONObject();
			net.sf.json.JSONArray categoriesJsonArr = new net.sf.json.JSONArray();

			String currentNetwork = "";
			String oldTargetServerIp = "";
			try {
				conn = DataConnectivityDaoImpl.conn;
				st = DataConnectivityDaoImpl.st;
				rs = st.executeQuery(query);
				while(rs.next()){
					networkAddJson = new net.sf.json.JSONObject();
					currentNetwork = rs.getString("NETWORK_TYPE");
					networkList.add(currentNetwork);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return networkList;
		}
		
		
		public int getNumberOfNetworksForFtpMarket(String marketId) {
			// TODO Auto-generated method stub
			Connection conn = null;
			Statement st = null;
			ResultSet rs = null;

			int count = 0;
			String query = "SELECT count(distinct NETWORK_TYPE) AS COUNT from stg_device_info WHERE MARKET_ID ='"+marketId+"' ORDER BY NETWORK_TYPE ";
			try{
				conn = DataConnectivityDaoImpl.conn;
				st = DataConnectivityDaoImpl.st;
				rs = st.executeQuery(query);
				while(rs.next()){
					count = rs.getInt("COUNT");
				}
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return count;
		}
	/*	public static void main(String[] args) {
			DataConnectivityDao dcDao = new DataConnectivityDaoImpl();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat newSdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
			try {
				Date date = sdf.parse("2013-07-01 16:46:01");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dcDao.openConn();
			//System.out.println(dcDao.getNetworksTypeForMarket("1"));
		}*/

		public int getNumberOfNetworksForMarket(String marketId) {
			// TODO Auto-generated method stub
			Connection conn = null;
			Statement st = null;
			ResultSet rs = null;
			int count = 0;
			String query = "SELECT distinct count(distinct NetworkType) AS COUNT from STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"' ";
			try{
				conn = DataConnectivityDaoImpl.conn;
				st = DataConnectivityDaoImpl.st;
				rs = st.executeQuery(query);
				while(rs.next()){
					count = rs.getInt("COUNT");
				}
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return count;
		}
		
		
		public HashMap<String,Integer> getTestNameMultipleThroughputDetailsResultsForFTP(
				String testCaseName,String marketId) {
			Connection conn = null;
			Statement st = null;
			ResultSet rs = null;
			List<DeviceInfoTO> deviceInfosList = new ArrayList<DeviceInfoTO>();
			PreparedStatement pst = null;
			String test_name = testCaseName + "\\-%";
			String throughputTX="";
		    String throughputRX="";
		    String throughputMain="";
		    String currTxBytes="";
		    String prevTxBytes="";
		    String currRxBytes="";
		    String prevRxBytes="";
			int i=0;
			double txvalue=0;
			double rxvalue=0;
			double mainValue=0;
			String test_type="FTP";
			HashMap<String, List<DeviceInfoTO>> networkWiseDetails = new HashMap<String, List<DeviceInfoTO>>();
			try {
				conn = DBUtil.getConnection();
				st = conn.createStatement();
				String query = "SELECT  DISTINCT * FROM STG_LOG_CAT_INFO WHERE TEST_NAME LIKE '"+testCaseName+"-%' AND TEST_TYPE='FTP' AND "+
								" TIME_STAMP > (SELECT TIME_STAMP_FOREACH_SAMPLE FROM STG_DEVICE_INFO WHERE TEST_NAME LIKE '"+testCaseName+"-%' " +
								"AND TEST_TYPE='FTP' AND MARKET_ID = '"+marketId+"' "+
									" ORDER BY TIME_STAMP_FOREACH_SAMPLE ASC LIMIT 1)"+
								 " ORDER BY TIME_STAMP ";
				//System.out.println("query-========FTP====="+query);
				rs = st.executeQuery(query);//AND sdi.NETWORK_TYPE =''LTE 
				while (rs.next()) {
					DeviceInfoTO deviceInfos = new DeviceInfoTO();
					deviceInfos.setTestName(testCaseName);
					deviceInfos.setSignalStrength(rs.getString(1));
					deviceInfos.setSignalStrengthCDMA(rs.getString(2));
					deviceInfos.setSignalStrengthEVDO(rs.getString(3));
					deviceInfos.setSignalStrengthLTE(rs.getString(4));
					deviceInfos.setSignalStrengthLTERSRP(rs.getString(5));
					deviceInfos.setNetworkType(rs.getString(6));
					deviceInfos.setEventValue(rs.getString("EVENT_VALUE"));
					deviceInfos.setEventName(rs.getString("EVENT_NAME"));
					deviceInfos.setLattitude(rs.getDouble(9));
					deviceInfos.setLongitude(rs.getDouble(10));
					deviceInfos.setTimeStampForEachSample(rs.getString(11));
					deviceInfos.setCellLocationCID(rs.getString(12));
			/*		throughputMain= new ReportDaoImpl().getThroughput( i, rs.getString(8), deviceInfos,
							  currTxBytes, THROUGHPUT, txvalue, throughputTX, prevTxBytes, throughputRX, currRxBytes
							 , rxvalue, prevRxBytes, mainValue, throughputMain);
					deviceInfos.setThroughputmain(throughputMain);
					deviceInfosList.add(deviceInfos);
					*/
					if(null!=networkWiseDetails.get(deviceInfos.getNetworkType())){
						List<DeviceInfoTO> deviceInfoList = networkWiseDetails.get(deviceInfos.getNetworkType());
						deviceInfoList.add(deviceInfos);
						networkWiseDetails.put(deviceInfos.getNetworkType(), deviceInfoList);
					}
					else{
						List<DeviceInfoTO> deviceInfoList = new ArrayList<DeviceInfoTO>();
						deviceInfoList.add(deviceInfos);
						networkWiseDetails.put(deviceInfos.getNetworkType(), deviceInfoList);
					}
					
					i++;
				}
				
				

			} catch (Exception e) {
				e.printStackTrace();
			} /*finally {
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
			}*/
			return getFtpUlinkDownlink(networkWiseDetails);
		}

		
		
		public HashMap<String,List<Float>> populateDataForFTP(String deviceName,
				String testName,String marketId,String type){
			
			String query = "SELECT * FROM ftp_throughput WHERE MARKET_ID='"+marketId+"' AND TEST_NAME LIKE '"+testName+"'" +
					"AND TRANSMISSION_TYPE = '"+type+"' order by network_type";
			Connection conn = null;
			Statement st = null;
			ResultSet rs = null;
			HashMap<String,List<Float>> resultMap = new HashMap<String, List<Float>>();
			List<Float> throughPutList = new ArrayList<Float>();
			List<Float> rssiList = new ArrayList<Float>();
			try {
				//System.out.println("query---"+query);
				conn = DataConnectivityDaoImpl.conn;
				st = DataConnectivityDaoImpl.st;
//				st = conn.createStatement();
				rs = st.executeQuery(query);
				while(rs.next()){
					String  throughPut = rs.getString("THROUGHPUT");
					String rssivalue = rs.getString("RSSI");
					if(null!=rssivalue){
						if(null!=throughPut && !throughPut.equalsIgnoreCase("null")){
							throughPutList.add(new Float(throughPut));
						}
						else{
							throughPutList.add(null);
						}
						if(null!=rssivalue && !rssivalue.equalsIgnoreCase("null")){
							rssiList.add(new Float(rssivalue));
						}
						else{
							rssiList.add(null);
						}
					}
				}
			}
			catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			resultMap.put("ftpThroughputList", throughPutList);
			resultMap.put("ftpRssList", rssiList);
			//System.out.println("resultMapftp srikath---------------"+resultMap);
			return resultMap;
		}
		
		
////////////////////LTE////////////////////////
		
		 public HashMap<String,List<Float>> populateDataForTCPLte(String deviceName,
					String testName, String marketId) {
				HashMap<String,List<Float>> dataMap = new HashMap<String, List<Float>>();
				List<Float> tcpDownloadSpeedList = new ArrayList<Float>();
				List<Float> tcpUploadSpeedList = new ArrayList<Float>();
				List<Float> ressList = new ArrayList<Float>();
				Connection conn = null;
				Statement st = null;
				ResultSet rs = null;

				try {
					////System.out.println("query---"+query);
					conn = DataConnectivityDaoImpl.conn;
					st = DataConnectivityDaoImpl.st;
					String query = "SELECT  S1.NetworkType AS 'NetworkType', 'Good' AS QUALITY,AVG(S.TCPDownloadSpeed),AVG(S.TCPUploadSpeed),AVG(S.RSSI) FROM "+
							"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
									"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"' AND NetworkType = 'LTE') S1    ON S.MARKET_ID='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -45 AND S.RSSI > -70 AND "+
									"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType AND  S.NetSpeedTest  LIKE 'TCP' GROUP BY QUALITY,S.NetworkType "+


									" UNION "+

									"SELECT  S1.NetworkType AS 'NetworkType', 'Medium' AS QUALITY ,AVG(S.TCPDownloadSpeed),AVG(S.TCPUploadSpeed),AVG(S.RSSI) FROM "+
									"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
									"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"' AND NetworkType = 'LTE') S1    ON S.MARKET_ID ='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -71 AND S.RSSI > -90 AND "+
									"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType AND  S.NetSpeedTest  LIKE 'TCP' GROUP BY QUALITY,S1.NetworkType "+


									" UNION "+

									"SELECT  S1.NetworkType AS 'NetworkType', 'Poor' AS QUALITY ,AVG(S.TCPDownloadSpeed),AVG(S.TCPUploadSpeed),AVG(S.RSSI) FROM "+
									"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
									"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"' AND NetworkType = 'LTE') S1    ON S.MARKET_ID ='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -103 AND "+
									"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType AND  S.NetSpeedTest  LIKE 'TCP' GROUP BY QUALITY,S1.NetworkType "+

									"Order by NetworkType, QUALITY";					
					rs = st.executeQuery(query);
					while (rs.next()) {
						DataConnectivityTo dcTo = new DataConnectivityTo();
						Float tCPDownloadSpeed = rs.getFloat("AVG(S.TCPDownloadSpeed)");
						Float tCPUploadSpeed = rs.getFloat("AVG(S.TCPUploadSpeed)");
						Float rssi = rs.getFloat("AVG(S.RSSI)");
						////System.out.println("rssi-----------"+rssi);
						if(null==tCPDownloadSpeed || tCPDownloadSpeed==0){
							tcpDownloadSpeedList.add(null);
						}
						else{
							tcpDownloadSpeedList.add(tCPDownloadSpeed);
						}
						if(null==tCPUploadSpeed || tCPUploadSpeed==0){
							tcpUploadSpeedList.add(null);
						}
						else{
							tcpUploadSpeedList.add(tCPUploadSpeed);
						}
						if(rssi == 0 ||null==rssi){
							ressList.add(null);
						}
						else{
							ressList.add(rssi);
						}
						
					}
					dataMap.put("tcpDownloadSpeedList", tcpDownloadSpeedList);
					dataMap.put("tcpUploadSpeedList", tcpUploadSpeedList);
					dataMap.put("ressList", ressList);
				} catch (Exception e) {
					e.printStackTrace();
				} /*finally {
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
				}*/
				return dataMap;
			}
			
		 
		 public HashMap<String,List<Float>> populateDataForUDPLte(String deviceName,
					String testName, String marketId) {
				HashMap<String,List<Float>> dataMap = new HashMap<String, List<Float>>();
				List<Float> udpDownloadSpeedList = new ArrayList<Float>();
				List<Float> udpDownloadCapacityList = new ArrayList<Float>();
				List<Float> udpUploadCapacityList = new ArrayList<Float>();
				List<Float> udpDownloadQOSList = new ArrayList<Float>();
				List<Float> ressList = new ArrayList<Float>();
				Connection conn = null;
				Statement st = null;
				ResultSet rs = null;

				try {
				//	//System.out.println("query---populateDataForUDPLTE-----------"+query);
					conn = DataConnectivityDaoImpl.conn;
					st = DataConnectivityDaoImpl.st;
					String query = "SELECT  S1.NetworkType AS 'NetworkType', 'Good' AS QUALITY,AVG(S.UDPDownloadCapacity),AVG(S.UDPUploadCapacity),AVG(S.UDPDownloadQOS),AVG(S.RSSI) FROM "+
							"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
									"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"' AND NetworkType = 'LTE') S1    ON S.MARKET_ID='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -45 AND S.RSSI > -70 AND "+
									"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType  AND  S.NetSpeedTest  LIKE 'UDP' AND S.UDPDownloadQOS NOT LIKE 'Empty' GROUP BY QUALITY,S1.NetworkType "+


									" UNION "+

									"SELECT  S1.NetworkType AS 'NetworkType', 'Medium'  AS QUALITY,AVG(S.UDPDownloadCapacity),AVG(S.UDPUploadCapacity),AVG(S.UDPDownloadQOS),AVG(S.RSSI) FROM "+
									"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
									"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"' AND NetworkType = 'LTE') S1    ON S.MARKET_ID ='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -91 AND S.RSSI > -103 AND "+
									"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType  AND  S.NetSpeedTest  LIKE 'UDP' AND S.UDPDownloadQOS NOT LIKE 'Empty' GROUP BY QUALITY,S1.NetworkType "+


									" UNION "+

									"SELECT  S1.NetworkType AS 'NetworkType', 'Poor' AS QUALITY,AVG(S.UDPDownloadCapacity),AVG(S.UDPUploadCapacity),AVG(S.UDPDownloadQOS),AVG(S.RSSI) FROM "+
									"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
									"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"' AND NetworkType = 'LTE') S1    ON S.MARKET_ID ='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -91 AND S.RSSI > -103 AND "+
									"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType AND  S.NetSpeedTest  LIKE 'UDP' AND S.UDPDownloadQOS NOT LIKE 'Empty' GROUP BY QUALITY,S1.NetworkType "+

									"Order by NetworkType, QUALITY";					
					rs = st.executeQuery(query);
					while (rs.next()) {
						Float udpDownloadCapacity = rs.getFloat("AVG(S.UDPDownloadCapacity)");
						Float udpUploadCapacity = rs.getFloat("AVG(S.UDPUploadCapacity)");
						Float udpDownloadQOS =rs.getFloat("AVG(S.UDPDownloadQOS)");
						Float rssi = rs.getFloat("AVG(S.RSSI)");
						String networkType =  rs.getString("NetworkType");
						////System.out.println("rssi-----------"+rssi);
//						//System.out.println("udpDownloadCapacity-------"+udpDownloadCapacity);
						if(null==udpDownloadCapacity || udpDownloadCapacity==0){
							udpDownloadCapacityList.add(null);
						}
						else{
							udpDownloadCapacityList.add(udpDownloadCapacity);
						}
						
						if(null==udpUploadCapacity || udpUploadCapacity==0){
							udpUploadCapacityList.add(null);
						}
						else{
							udpUploadCapacityList.add(udpUploadCapacity);
						}
						
						if(null==udpDownloadQOS || udpDownloadQOS==0){
							udpDownloadQOSList.add(null);
						}
						else{
							udpDownloadQOSList.add(udpDownloadQOS);
						}
						if(rssi == 0 ||null==rssi){
							ressList.add(null);
						}
						else{
							ressList.add(rssi);
						}
						
					}
					dataMap.put("udpDownloadQOSList", udpDownloadQOSList);
					dataMap.put("udpDownloadCapacityList", udpDownloadCapacityList);
					dataMap.put("udpUploadCapacityList", udpUploadCapacityList);
					dataMap.put("ressList", ressList);
					////System.out.println("dataMap------udp-------"+dataMap);
				}catch (Exception e) {
					e.printStackTrace();
				} /*finally {
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
				}*/
				return dataMap;
			}
		 
		 
		 public HashMap<String,List<Float>> populateDataForDNSLte(String deviceName,
					String testName, String marketId) {
				Connection conn = null;
				Statement st = null;
				ResultSet rs = null;
				HashMap<String,List<Float>> dataMap = new HashMap<String, List<Float>>();
				List<Float> udpDownloadSpeedList = new ArrayList<Float>();
				List<Float> dnsAvLatencyList = new ArrayList<Float>();
				List<Float> dnsMaxLatencyList = new ArrayList<Float>();
				List<Float> ressList = new ArrayList<Float>();

				try {
					////System.out.println("query---"+query);
					conn = DataConnectivityDaoImpl.conn;
					st = DataConnectivityDaoImpl.st;
					String query = "SELECT  S1.NetworkType AS 'NetworkType', 'Good' AS QUALITY,AVG(S.DNSAvLatency),AVG(S.DNSMaxLatency),AVG(S.RSSI) FROM "+
							"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
							"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"' AND NetworkType = 'LTE' ) S1    ON S.MARKET_ID='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -45 AND S.RSSI > -70 AND "+
							"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType  AND  S.NetSpeedTest  LIKE 'DNS' AND S.DNSAvLatency NOT LIKE 'Empty' GROUP BY QUALITY,S1.NetworkType "+


							" UNION "+

							"SELECT  S1.NetworkType AS 'NetworkType', 'Medium'  AS QUALITY,AVG(S.DNSAvLatency),AVG(S.DNSMaxLatency),AVG(S.RSSI) FROM "+
							"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
							"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"' AND NetworkType = 'LTE') S1    ON S.MARKET_ID ='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -91 AND S.RSSI > -103 AND "+
							"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType  AND  S.NetSpeedTest  LIKE 'DNS' AND S.DNSAvLatency NOT LIKE 'Empty' GROUP BY QUALITY,S1.NetworkType "+


							" UNION "+

							"SELECT  S1.NetworkType AS 'NetworkType', 'Poor' AS QUALITY,AVG(S.DNSAvLatency),AVG(S.DNSMaxLatency),AVG(S.RSSI) FROM "+
							"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
							"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"' AND NetworkType = 'LTE') S1    ON S.MARKET_ID ='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -93 AND S.RSSI >  -103 AND "+
							"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType  AND  S.NetSpeedTest  LIKE 'DNS' AND S.DNSAvLatency NOT LIKE 'Empty' GROUP BY QUALITY,S1.NetworkType "+

							"Order by NetworkType, QUALITY";					
					rs = st.executeQuery(query);
					while (rs.next()) {
						DataConnectivityTo dcTo = new DataConnectivityTo();
						Float dnsAvLatency = rs.getFloat("AVG(S.dnsAvLatency)");
						Float dnsMaxLatency = rs.getFloat("AVG(S.DNSMaxLatency)");
						Float rssi = rs.getFloat("AVG(S.RSSI)");
						if(null==dnsMaxLatency || dnsMaxLatency==0){
							dnsMaxLatencyList.add(null);
						}
						else{
							dnsMaxLatencyList.add(dnsMaxLatency);
						}
						if(null==dnsAvLatency || dnsAvLatency==0){
							dnsAvLatencyList.add(null);
						}
						else{
							dnsAvLatencyList.add(dnsAvLatency);
						}
						
						if(rssi == 0 || null==rssi){
							ressList.add(null);
						}
						else{
							ressList.add(rssi);
						}
						
					}
					dataMap.put("dnsMaxLatencyList", dnsMaxLatencyList);
					dataMap.put("dnsAvgLatencyList", dnsAvLatencyList);
					dataMap.put("dnsRssList", ressList);
				} catch (Exception e) {
					e.printStackTrace();
				} /*finally {
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
				}*/
				return dataMap;
			}

		 
		 
		 public HashMap<String, HashMap<String, HashMap<String, List<Float>>>> populateDetailedDataForDNS(
				String deviceName, String testName, String marketId) {
				Connection conn = null;
				Statement st = null;
				ResultSet rs = null;
				//HashMap<String,List<Integer>> dataMap = new HashMap<String, List<Integer>>();
				HashMap<String,HashMap<String,List<Float>>> dataMap = new HashMap<String, HashMap<String,List<Float>>>();
				HashMap<String,List<Float>> avgLatencyMap = new HashMap<String, List<Float>>();
				HashMap<String,List<Float>> maxLatencyMap = new HashMap<String, List<Float>>();
				HashMap<String,List<Float>> rssiMap = new HashMap<String, List<Float>>();
				List<Float> dnsDownloadSpeedGoodList = new ArrayList<Float>();
				List<Float> dnsDownloadMediumgoodList = new ArrayList<Float>();
				List<Float> dnsDownloadPoorgoodList = new ArrayList<Float>();
				List<Float> ressList = new ArrayList<Float>();
				String networkType = "";
				HashMap<String, HashMap<String,HashMap<String,List<Float>>>> networkWiseMap = new HashMap<String, HashMap<String,HashMap<String,List<Float>>>>();
				
				//   'g2' 
				

				try {
					conn = DataConnectivityDaoImpl.conn;
					st = DataConnectivityDaoImpl.st;
					String query = "SELECT  S1.NetworkType AS 'NetworkType', 'Good' AS QUALITY,(S.DNSAvLatency),(S.DNSMaxLatency),(S.RSSI) FROM "+
							" STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
							"  STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"' AND NetworkType NOT LIKE 'UNKNOWN') S1    ON S.MARKET_ID='"+marketId+"'and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -45 AND S.RSSI > -70 AND "+
							" TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType  AND  S.NetSpeedTest  LIKE 'DNS' AND S.DNSAvLatency NOT LIKE 'Empty'  "+


							" UNION "+

							"SELECT  S1.NetworkType AS 'NetworkType', 'Medium' AS QUALITY,(S.DNSAvLatency),(S.DNSMaxLatency),(S.RSSI) FROM "+
							" STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
							"  STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"' AND NetworkType NOT LIKE 'UNKNOWN') S1    ON S.MARKET_ID='"+marketId+"'and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -71 AND S.RSSI > -90 AND "+
							" TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType  AND  S.NetSpeedTest  LIKE 'DNS' AND S.DNSAvLatency NOT LIKE 'Empty'  "+


							" UNION "+

							"SELECT  S1.NetworkType AS 'NetworkType', 'Poor' AS QUALITY,(S.DNSAvLatency),(S.DNSMaxLatency),(S.RSSI) FROM "+
							" STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
							"  STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"' AND NetworkType NOT LIKE 'UNKNOWN') S1    ON S.MARKET_ID='"+marketId+"'and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -103 AND "+
							" TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType  AND  S.NetSpeedTest  LIKE 'DNS' AND S.DNSAvLatency NOT LIKE 'Empty'  "+

							"Order by NetworkType, QUALITY";	
					//System.out.println("query---populateDetailedDataForDNS-------"+query);
					rs = st.executeQuery(query);
					while (rs.next()) {
						DataConnectivityTo dcTo = new DataConnectivityTo();
						String dnsAvLatency = rs.getString("DNSAvLatency");
						String dnsMaxLatency = rs.getString("DNSMaxLatency");
						String rssi = rs.getString("RSSI");
						String quality = rs.getString("QUALITY");
						networkType = rs.getString("NetworkType");
						dataMap = networkWiseMap.get(networkType);
						////System.out.println("networkType==DNS===="+networkType);
						////System.out.println("rssi-----------"+rssi);
						if(null==dataMap){
							dataMap = new HashMap<String, HashMap<String,List<Float>>>();
							avgLatencyMap = new HashMap<String, List<Float>>();
							maxLatencyMap = new HashMap<String, List<Float>>();
							rssiMap = new HashMap<String, List<Float>>();
						}
						else{
							avgLatencyMap = dataMap.get("AvgLatency");
							maxLatencyMap = dataMap.get("MaxLatency");
							rssiMap = dataMap.get("rssi");
						}
						if(quality.equals("Good")){
							List<Float> algoodList = avgLatencyMap.get(quality);
							List<Float> mlgoodList = maxLatencyMap.get(quality);
							List<Float> rssGoodList = rssiMap.get(quality);
							if(null==algoodList){
								algoodList = new ArrayList<Float>();
							}
							if(null==mlgoodList){
								mlgoodList = new ArrayList<Float>();
							}
							if(null==rssGoodList){
								rssGoodList = new ArrayList<Float>();
							}
							if(null!=dnsAvLatency && !dnsAvLatency.equals("0") && !dnsAvLatency.trim().equals("Empty")){
								float dnsAvLatencyFloat=Float.parseFloat(dnsAvLatency);
								algoodList.add(dnsAvLatencyFloat);
							}
							if(null!=dnsMaxLatency && !dnsMaxLatency.equals("0") && !dnsMaxLatency.trim().equals("Empty")){
								float dnsMaxLatencyFloat=Float.parseFloat(dnsMaxLatency);
								mlgoodList.add(dnsMaxLatencyFloat);
							}
							if(null!=rssi && !rssi.trim().equals("Empty") && !rssi.equals("0")){
								float rssiFloat=Float.parseFloat(rssi);
								rssGoodList.add(new Float(rssiFloat));
							}
							avgLatencyMap.put(quality, algoodList);
							maxLatencyMap.put(quality, mlgoodList);
							rssiMap.put(quality, rssGoodList);
						} else
						if(quality.equals("Medium")){
							List<Float> alMediumList = avgLatencyMap.get(quality);
							List<Float> mlMediumList = maxLatencyMap.get(quality);
							List<Float> rssMediumList = rssiMap.get(quality);
							if(alMediumList==null){
								alMediumList = new ArrayList<Float>();
							}
							if(null==mlMediumList){
								mlMediumList = new ArrayList<Float>();
							}
							if(null==rssMediumList){
								rssMediumList = new ArrayList<Float>();
							}
							if(null!=dnsAvLatency  && !dnsAvLatency.equals("0") && !dnsAvLatency.trim().equals("Empty")){
								float dnsAvLatencyFlaot=Float.parseFloat(dnsAvLatency);
								alMediumList.add(dnsAvLatencyFlaot);
							}
							if(null!=dnsMaxLatency && !dnsMaxLatency.equals("0") && !dnsMaxLatency.trim().equals("Empty")){
								float dnsMaxLatencyFloat=Float.parseFloat(dnsMaxLatency);
								mlMediumList.add(dnsMaxLatencyFloat);
							}
							if(null!=rssi && !rssi.trim().equals("Empty") && !rssi.equals("0")){
								float rssiFloat=Float.parseFloat(rssi);
								rssMediumList.add(new Float(rssiFloat));
							}
							avgLatencyMap.put(quality, alMediumList);
							maxLatencyMap.put(quality, mlMediumList);
							rssiMap.put(quality, rssMediumList);
						}else
						if(quality.equals("Poor")){
							List<Float> alPoorList = avgLatencyMap.get(quality);
							List<Float> mlPoorList = maxLatencyMap.get(quality);
							List<Float> rssPoorList = rssiMap.get(quality);
							if(null==alPoorList){
								alPoorList = new ArrayList<Float>();
							}
							if(null==mlPoorList){
								mlPoorList = new ArrayList<Float>();
							}
							if(null==rssPoorList){
								rssPoorList = new ArrayList<Float>();
							}
							if(null!=dnsAvLatency  && !dnsAvLatency.equals("0") && !dnsAvLatency.trim().equals("Empty")){
							float dnsAvLatencyFlaot=Float.parseFloat(dnsAvLatency);
								alPoorList.add(dnsAvLatencyFlaot);
							}
							if(null!=dnsMaxLatency && !dnsMaxLatency.equals("0") && !dnsMaxLatency.trim().equals("Empty")){
								float dnsMaxLatencyFloat=Float.parseFloat(dnsMaxLatency);
								mlPoorList.add(dnsMaxLatencyFloat);
							}
							if(null!=rssi && !rssi.trim().equals("Empty") && !rssi.equals("0")){
								float rssiFloat=Float.parseFloat(rssi);
								rssPoorList.add(new Float(rssiFloat));
							}
							
							avgLatencyMap.put(quality, alPoorList);
							maxLatencyMap.put(quality, mlPoorList);	
							rssiMap.put(quality, rssPoorList);
						}
						dataMap.put("AvgLatency", avgLatencyMap);
						dataMap.put("MaxLatency", maxLatencyMap);
						dataMap.put("rssi", rssiMap);
						networkWiseMap.put(networkType, dataMap);
						
					}
				} catch (Exception e) {
					e.printStackTrace();
				} /*finally {
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
				}*/
				////System.out.println("networkWiseMap-----DNS--------"+networkWiseMap);
				return networkWiseMap;
		}

		 public HashMap<String, HashMap<String,HashMap<String,List<Float>>>> populateDetailedDataForTCP(
					String deviceName, String testName, String marketId) {
				Connection conn = null;
				Statement st = null;
				ResultSet rs = null;
//				HashMap<String,List<Integer>> dataMap = new HashMap<String, List<Integer>>();
				HashMap<String,HashMap<String,List<Float>>> dataMap = new HashMap<String, HashMap<String,List<Float>>>();
				HashMap<String,List<Float>> downloadSpeedMap = new HashMap<String, List<Float>>();
				HashMap<String,List<Float>> uploadSpeedMap = new HashMap<String, List<Float>>();
				HashMap<String,List<Float>> rssiMap = new HashMap<String, List<Float>>();
				List<Integer> tcpDownloadSpeedGoodList = new ArrayList<Integer>();
				List<Integer> tcpDownloadMediumgoodList = new ArrayList<Integer>();
				List<Integer> tcpDownloadPoorgoodList = new ArrayList<Integer>();
				List<Integer> ressList = new ArrayList<Integer>();
				String networkType = "";
				HashMap<String, HashMap<String,HashMap<String,List<Float>>>> networkWiseMap = new HashMap<String, HashMap<String,HashMap<String,List<Float>>>>();

				try {
					
					conn = DataConnectivityDaoImpl.conn;
					st = DataConnectivityDaoImpl.st;
					String query = "SELECT  S1.NetworkType AS 'NetworkType', 'Good' AS QUALITY,S.TCPDownloadSpeed,S.TCPUploadSpeed,S.RSSI FROM "+
							"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
									"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"' AND NetworkType NOT LIKE 'UNKNOWN') S1    ON S.MARKET_ID='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -45 AND S.RSSI > -70 AND "+
									"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType AND  S.NetSpeedTest  LIKE 'TCP' AND S.TCPDownloadSpeed NOT LIKE 'Empty'  "+


									" UNION "+

									"SELECT  S1.NetworkType AS 'NetworkType', 'Medium' AS QUALITY ,S.TCPDownloadSpeed,S.TCPUploadSpeed,S.RSSI FROM "+
									"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
									"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"' AND NetworkType NOT LIKE 'UNKNOWN') S1    ON S.MARKET_ID ='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND  S.RSSI < -71 AND S.RSSI > -90 AND "+
									"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType  AND  S.NetSpeedTest  LIKE 'TCP' AND S.TCPDownloadSpeed NOT LIKE 'Empty'  "+


									" UNION "+

									"SELECT  S1.NetworkType AS 'NetworkType', 'Poor' AS QUALITY ,S.TCPDownloadSpeed,S.TCPUploadSpeed,S.RSSI FROM "+
									"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
									"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"' AND NetworkType NOT LIKE 'UNKNOWN') S1    ON S.MARKET_ID ='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -103 AND "+
									"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType AND  S.NetSpeedTest  LIKE 'TCP' AND S.TCPDownloadSpeed NOT LIKE 'Empty'  "+

									"Order by NetworkType, QUALITY";	
					//System.out.println("query-----populateDetailedDataForTCP----"+query);
					rs = st.executeQuery(query);
					while (rs.next()) {
						DataConnectivityTo dcTo = new DataConnectivityTo();
						String tcpDownloadSpeed = rs.getString("TCPDownloadSpeed");
						String tcpUploadSpeed = rs.getString("TCPUploadSpeed");
						String rssi = rs.getString("RSSI");
						String quality = rs.getString("QUALITY");
					    networkType = rs.getString("NetworkType");
					    if(!networkType.equalsIgnoreCase("UNKNOWN")){
					    	dataMap = networkWiseMap.get(networkType);
							if(null==dataMap){
								dataMap = new HashMap<String, HashMap<String,List<Float>>>();
								downloadSpeedMap = new HashMap<String, List<Float>>();
								uploadSpeedMap = new HashMap<String, List<Float>>();
								rssiMap = new HashMap<String, List<Float>>();
							}
							else{
								downloadSpeedMap = dataMap.get("DownloadSpeed");
								uploadSpeedMap = dataMap.get("UploadSpeed");
								rssiMap = dataMap.get("rssi");
							}
							if(quality.equals("Good")){
								List<Float> dcgoodList = downloadSpeedMap.get(quality);
								List<Float> dcUploadgoodList = uploadSpeedMap.get(quality);
								List<Float> rssGoodList = rssiMap.get(quality);
								if(null==dcgoodList){
									dcgoodList = new ArrayList<Float>();
								}
								
								if(null==dcUploadgoodList){
									dcUploadgoodList = new ArrayList<Float>();
								}
								if(null==rssGoodList){
									rssGoodList = new ArrayList<Float>();
								}
								if(null!=tcpDownloadSpeed && !tcpDownloadSpeed.trim().equals("Empty") &&!tcpDownloadSpeed.equals("0") ){
									float tcpDownloadSpeedInt=Float.parseFloat(tcpDownloadSpeed);
									dcgoodList.add(new Float(tcpDownloadSpeedInt));
								}
								if(null!=tcpUploadSpeed && !tcpUploadSpeed.trim().equals("Empty") &&!tcpUploadSpeed.equals("0") ){
									float tcpUploadSpeedInt=Float.parseFloat(tcpUploadSpeed);
									dcUploadgoodList.add(new Float(tcpUploadSpeedInt));
								}
								if(null!=rssi && !rssi.trim().equals("Empty") && !rssi.equals("0")){
									float rssiFloat=Float.parseFloat(rssi);
									rssGoodList.add(new Float(rssiFloat));
								}
								downloadSpeedMap.put(quality, dcgoodList);
								uploadSpeedMap.put(quality, dcUploadgoodList);
								rssiMap.put(quality, rssGoodList);
							} else
							if(quality.equals("Medium")){
								List<Float> dcMediumList = downloadSpeedMap.get(quality);
								List<Float> dcUploadMediumList = uploadSpeedMap.get(quality);
								List<Float> rssMediumList = rssiMap.get(quality);
								if(null==dcMediumList){
									dcMediumList = new ArrayList<Float>();
								}
								if(null==dcUploadMediumList){
									dcUploadMediumList = new ArrayList<Float>();
								}
								if(null==rssMediumList){
									rssMediumList = new ArrayList<Float>();
								}
								if(null!=tcpUploadSpeed && !tcpUploadSpeed.trim().equals("Empty") &&!tcpUploadSpeed.equals("0") ){
									float tcpUploadSpeedInt=Float.parseFloat(tcpUploadSpeed);
									dcUploadMediumList.add(new Float(tcpUploadSpeedInt));
								}
								if(null!=tcpDownloadSpeed && !tcpDownloadSpeed.trim().equals("Empty") && !tcpDownloadSpeed.equals("0")){
									float tcpDownloadSpeedInt=Float.parseFloat(tcpDownloadSpeed);
									dcMediumList.add(new Float(tcpDownloadSpeedInt));
								}
								if(null!=rssi && !rssi.trim().equals("Empty")&&!rssi.equals("0")){
									float rssiFloat=Float.parseFloat(rssi);
									rssMediumList.add(new Float(rssiFloat));
								}
								
								downloadSpeedMap.put(quality, dcMediumList);
								uploadSpeedMap.put(quality, dcUploadMediumList);
								rssiMap.put(quality, rssMediumList);
							}else
							if(quality.equals("Poor")){
								List<Float> dcPoorList = downloadSpeedMap.get(quality);
								List<Float> dcUploadPoorList = downloadSpeedMap.get(quality);
								List<Float> rssPoorList = rssiMap.get(quality);
								if(null==dcPoorList){
									dcPoorList = new ArrayList<Float>();
								}
								if(null==dcUploadPoorList){
									dcUploadPoorList = new ArrayList<Float>();
								}
								if(null==rssPoorList){
									rssPoorList = new ArrayList<Float>();
								}
								if(null!=tcpDownloadSpeed && !tcpDownloadSpeed.trim().equals("Empty")&&!tcpDownloadSpeed.equals("0")){
									float tcpDownloadSpeedInt=Float.parseFloat(tcpDownloadSpeed);
									dcPoorList.add(new Float(tcpDownloadSpeedInt));
								}
								if(null!=tcpUploadSpeed && !tcpUploadSpeed.trim().equals("Empty") &&!tcpUploadSpeed.equals("0") ){
									float tcpUploadSpeedInt=Float.parseFloat(tcpUploadSpeed);
									dcUploadPoorList.add(new Float(tcpUploadSpeedInt));
								}
								if(null!=rssi && !rssi.trim().equals("Empty")&&!rssi.equals("0")){
									float rssiFloat=Float.parseFloat(rssi);
									rssPoorList.add(new Float(rssiFloat));
								}
								downloadSpeedMap.put(quality, dcPoorList);
								uploadSpeedMap.put(quality, dcUploadPoorList);
								rssiMap.put(quality, rssPoorList);
							}
							dataMap.put("DownloadSpeed", downloadSpeedMap);
							dataMap.put("UploadSpeed", uploadSpeedMap);
							dataMap.put("rssi", rssiMap);
							networkWiseMap.put(networkType, dataMap);
					    }
						
					}
//					dataMap.put("tcpDownloadSpeedList", tcpDownloadSpeedList);
					//System.out.println("networkWiseMap-------tcp------"+networkWiseMap);
				} catch (Exception e) {
					e.printStackTrace();
				} /*finally {
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
				}*/
				return networkWiseMap;
		}


		public HashMap<String, HashMap<String,HashMap<String,List<Float>>>> populateDetailedDataForUDP(
				String deviceName, String testName, String marketId) {
				Connection conn = null;
				Statement st = null;
				ResultSet rs = null;
//				HashMap<String,List<Integer>> dataMap = new HashMap<String, List<Integer>>();
				HashMap<String,HashMap<String,List<Float>>> dataMap = new HashMap<String, HashMap<String,List<Float>>>();
				HashMap<String,List<Float>> downloadCapacityMap = new HashMap<String, List<Float>>();
				HashMap<String,List<Float>> uploadCapacityMap = new HashMap<String, List<Float>>();
				HashMap<String,List<Float>> downloadQosMap = new HashMap<String, List<Float>>();
				HashMap<String,List<Float>> rssiMap = new HashMap<String, List<Float>>();
				List<Integer> udpDownloadSpeedGoodList = new ArrayList<Integer>();
				List<Integer> udpDownloadMediumgoodList = new ArrayList<Integer>();
				List<Integer> udpDownloadPoorgoodList = new ArrayList<Integer>();
				List<Integer> ressList = new ArrayList<Integer>();
				String networkType = "";
				HashMap<String, HashMap<String,HashMap<String,List<Float>>>> networkWiseMap = new HashMap<String, HashMap<String,HashMap<String,List<Float>>>>();
				
				try {
					////System.out.println("query---------"+query);
					conn = DataConnectivityDaoImpl.conn;
					st = DataConnectivityDaoImpl.st;
					String query = "SELECT  S1.NetworkType AS 'NetworkType', 'Good' AS QUALITY,S.UDPDownloadCapacity,S.UDPUploadCapacity,S.UDPDownloadQOS,S.RSSI FROM "+
							"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
									"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"' AND NetworkType NOT LIKE 'UNKNOWN') S1    ON S.MARKET_ID='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -45 AND S.RSSI > -70 AND "+
									"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType AND  S.NetSpeedTest  LIKE 'UDP' AND S.UDPDownloadQOS NOT LIKE 'Empty'  "+


									" UNION "+

									"SELECT  S1.NetworkType AS 'NetworkType', 'Medium'  AS QUALITY,S.UDPDownloadCapacity,S.UDPUploadCapacity,S.UDPDownloadQOS,S.RSSI FROM "+
									"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
									"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"' AND NetworkType NOT LIKE 'UNKNOWN') S1    ON S.MARKET_ID ='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -71 AND S.RSSI > -90 AND "+
									"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType AND  S.NetSpeedTest  LIKE 'UDP' AND S.UDPDownloadQOS NOT LIKE 'Empty' "+


									" UNION "+

									"SELECT  S1.NetworkType AS 'NetworkType', 'Poor' AS QUALITY,S.UDPDownloadCapacity,S.UDPUploadCapacity,S.UDPDownloadQOS,S.RSSI FROM "+
									"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
									"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"' AND NetworkType NOT LIKE 'UNKNOWN') S1    ON S.MARKET_ID ='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -103 AND "+
									"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType AND  S.NetSpeedTest  LIKE 'UDP' AND S.UDPDownloadQOS NOT LIKE 'Empty'  "+

									"Order by NetworkType, QUALITY";					
					rs = st.executeQuery(query);
					while (rs.next()) {
						DataConnectivityTo dcTo = new DataConnectivityTo();
						String udpDownloadCapacity = rs.getString("UDPDownloadCapacity");
						String udpUploadCapacity = rs.getString("UDPUploadCapacity");
						String udpDownloadQOS =rs.getString("UDPDownloadQOS");
						String rssi = rs.getString("RSSI");
						String quality = rs.getString("QUALITY");
					    networkType = rs.getString("NetworkType");
						dataMap = networkWiseMap.get(networkType);
						if(null==dataMap){
							dataMap = new HashMap<String, HashMap<String,List<Float>>>();
							downloadCapacityMap = new HashMap<String, List<Float>>();
							uploadCapacityMap = new HashMap<String, List<Float>>();
							downloadQosMap = new HashMap<String, List<Float>>();
							rssiMap = new HashMap<String, List<Float>>();
						}
						else{
							downloadCapacityMap = dataMap.get("DownloadCapacity");
							uploadCapacityMap= dataMap.get("UploadCapacity");
							downloadQosMap = dataMap.get("Qos");
							rssiMap = dataMap.get("rssi");
						}
						if(quality.equals("Good")){
							List<Float> dcgoodList = downloadCapacityMap.get(quality);
							List<Float> dcUploadgoodList = uploadCapacityMap.get(quality);
							List<Float> dQgoodList = downloadQosMap.get(quality);
							List<Float> rssGoodList = rssiMap.get(quality);
							if(null==dcgoodList){
								dcgoodList = new ArrayList<Float>();
							}
							if(null==dcUploadgoodList){
								dcUploadgoodList = new ArrayList<Float>();
							}
							if(null==dQgoodList){
								dQgoodList = new ArrayList<Float>();
							}
							if(null==rssGoodList){
								rssGoodList = new ArrayList<Float>();
							}
							//System.out.println("udpDownloadCapacity-------------"+udpDownloadCapacity);
							if(null!=udpDownloadCapacity && !udpDownloadCapacity.trim().trim().equals("Empty") &&!udpDownloadCapacity.equals("0") ){
								float udpDownloadCapacityInt=Float.parseFloat(udpDownloadCapacity);
								dcgoodList.add(new Float(udpDownloadCapacityInt));
							}
							if(null!=udpUploadCapacity && !udpUploadCapacity.trim().equals("Empty") &&!udpUploadCapacity.equals("0") ){
								float udpUploadCapacityInt=Float.parseFloat(udpUploadCapacity);
								dcUploadgoodList.add(new Float(udpUploadCapacityInt));
							}
							if(null!=udpDownloadQOS && !udpDownloadQOS.trim().equals("Empty")&&!udpDownloadQOS.equals("0")){
								float udpDownloadQOSInt=Float.parseFloat(udpDownloadQOS);
								dQgoodList.add(new Float(udpDownloadQOSInt));
							}
							if(null!=rssi && !rssi.trim().equals("Empty") && !rssi.equals("0")){
								float rssiFloat=Float.parseFloat(rssi);
								rssGoodList.add(new Float(rssiFloat));
							}
							downloadCapacityMap.put(quality, dcgoodList);
							uploadCapacityMap.put(quality, dcUploadgoodList);
							downloadQosMap.put(quality, dQgoodList);
							rssiMap.put(quality, rssGoodList);
						} else
						if(quality.equals("Medium")){
							List<Float> dcMediumList = downloadCapacityMap.get(quality);
							List<Float> dcUploadMediumList = uploadCapacityMap.get(quality);
							List<Float> dQMediumList = downloadQosMap.get(quality);
							List<Float> rssMediumList = rssiMap.get(quality);
							if(null==dcMediumList){
								dcMediumList = new ArrayList<Float>();
							}
							if(null==dcUploadMediumList){
								dcUploadMediumList = new ArrayList<Float>();
							}
							if(null==dQMediumList){
								dQMediumList = new ArrayList<Float>();
							}
							if(null==rssMediumList){
								rssMediumList = new ArrayList<Float>();
							}
							if(null!=udpDownloadCapacity && !udpDownloadCapacity.trim().equals("Empty") && !udpDownloadCapacity.equals("0")){
								float udpDownloadCapacityInt=Float.parseFloat(udpDownloadCapacity);
								dcMediumList.add(new Float(udpDownloadCapacityInt));
							}
							if(null!=udpUploadCapacity && !udpUploadCapacity.trim().equals("Empty") && !udpUploadCapacity.equals("0")){
								float udpUploadCapacityInt=Float.parseFloat(udpUploadCapacity);
								dcUploadMediumList.add(new Float(udpUploadCapacityInt));
							}
							if(null!=udpDownloadQOS && !udpDownloadQOS.trim().equals("Empty")&&!udpDownloadQOS.equals("0")){
								float udpDownloadQOSInt=Float.parseFloat(udpDownloadQOS);
								dQMediumList.add(new Float(udpDownloadQOSInt));
							}
							if(null!=rssi && !rssi.trim().equals("Empty")&&!rssi.equals("0")){
								float rssiFloat=Float.parseFloat(rssi);
								rssMediumList.add(new Float(rssiFloat));
							}
							
							downloadCapacityMap.put(quality, dcMediumList);
							uploadCapacityMap.put(quality, dcUploadMediumList);
							downloadQosMap.put(quality, dQMediumList);
							rssiMap.put(quality, rssMediumList);
						}else
						if(quality.equals("Poor")){
							List<Float> dcPoorList = downloadCapacityMap.get(quality);
							List<Float> dcUploadPoorList = uploadCapacityMap.get(quality);
							List<Float> dQPoorList = downloadQosMap.get(quality);
							List<Float> rssPoorList = rssiMap.get(quality);
							if(null==dcPoorList){
								dcPoorList = new ArrayList<Float>();
							}
							if(null==dcUploadPoorList){
								dcUploadPoorList = new ArrayList<Float>();
							}
							if(null==dQPoorList){
								dQPoorList = new ArrayList<Float>();
							}
							if(null==rssPoorList){
								rssPoorList = new ArrayList<Float>();
							}
							if(null!=udpDownloadCapacity && !udpDownloadCapacity.trim().equals("Empty")&&!udpDownloadCapacity.equals("0")){
								float udpDownloadCapacityInt=Float.parseFloat(udpDownloadCapacity);
								dcPoorList.add(new Float(udpDownloadCapacityInt));
							}
							if(null!=udpUploadCapacity && !udpUploadCapacity.trim().equals("Empty")&&!udpUploadCapacity.equals("0")){
								float udpUploadCapacityInt=Float.parseFloat(udpUploadCapacity);
								dcUploadPoorList.add(new Float(udpUploadCapacityInt));
							}
							if(null!=udpDownloadQOS && !udpDownloadQOS.trim().equals("Empty")&&!udpDownloadQOS.equals("0")){
								float udpDownloadQOSInt=Float.parseFloat(udpDownloadQOS);
								dQPoorList.add(new Float(udpDownloadQOSInt));
							}
							if(null!=rssi && !rssi.trim().equals("Empty")&&!rssi.equals("0")){
								float rssiFloat=Float.parseFloat(rssi);
								rssPoorList.add(new Float(rssiFloat));
							}
							downloadCapacityMap.put(quality, dcPoorList);
							uploadCapacityMap.put(quality, dcUploadPoorList);
							downloadQosMap.put(quality, dQPoorList);
							rssiMap.put(quality, rssPoorList);
						}
						dataMap.put("DownloadCapacity", downloadCapacityMap);
						dataMap.put("UploadCapacity", uploadCapacityMap);
						dataMap.put("Qos", downloadQosMap);
						dataMap.put("rssi", rssiMap);
						networkWiseMap.put(networkType, dataMap);
					}
//					dataMap.put("tcpDownloadSpeedList", tcpDownloadSpeedList);
					////System.out.println("networkWiseMap-------udp------"+networkWiseMap);
				} catch (Exception e) {
					e.printStackTrace();
				} /*finally {
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
				}*/
				return networkWiseMap;
		}		
		
		
		
		
		
///////////////////LTE//////////////////////////		
		
		
////////////////////LTE////////////////////////
		
		 public HashMap<String, HashMap<String,HashMap<String,List<Float>>>> populateDetailedDataForTCPLte(String deviceName,
					String testName, String marketId) {
				Connection conn = null;
				Statement st = null;
				ResultSet rs = null;
				HashMap<String,HashMap<String,List<Float>>> dataMap = new HashMap<String, HashMap<String,List<Float>>>();
				HashMap<String,List<Float>> downloadSpeedMap = new HashMap<String, List<Float>>();
				HashMap<String,List<Float>> uploadSpeedMap = new HashMap<String, List<Float>>();
				HashMap<String,List<Float>> rssiMap = new HashMap<String, List<Float>>();
				List<Integer> tcpDownloadSpeedGoodList = new ArrayList<Integer>();
				List<Integer> tcpDownloadMediumgoodList = new ArrayList<Integer>();
				List<Integer> tcpDownloadPoorgoodList = new ArrayList<Integer>();
				List<Integer> ressList = new ArrayList<Integer>();
				String networkType = "";
				HashMap<String, HashMap<String,HashMap<String,List<Float>>>> networkWiseMap = new HashMap<String, HashMap<String,HashMap<String,List<Float>>>>();
		
				try {
				//	//System.out.println("query---------"+query);
					conn = DataConnectivityDaoImpl.conn;
					st = DataConnectivityDaoImpl.st;	
					String query = "SELECT  S1.NetworkType AS 'NetworkType', 'Good' AS QUALITY,S.TCPDownloadSpeed,S.TCPUploadSpeed,S.RSSI FROM "+
							"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
									"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"') S1    ON S.MARKET_ID='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -45 AND S.RSSI > -70 AND "+
									"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType AND  S.NetSpeedTest  LIKE 'TCP' AND S.TCPDownloadSpeed NOT LIKE 'Empty'  "+


									" UNION "+

									"SELECT  S1.NetworkType AS 'NetworkType', 'Medium' AS QUALITY ,S.TCPDownloadSpeed,S.TCPUploadSpeed,S.RSSI FROM "+
									"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
									"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"') S1    ON S.MARKET_ID ='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -91 AND S.RSSI > -103 AND "+
									"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType  AND  S.NetSpeedTest  LIKE 'TCP' AND S.TCPDownloadSpeed NOT LIKE 'Empty'  "+


									" UNION "+

									"SELECT  S1.NetworkType AS 'NetworkType', 'Poor' AS QUALITY ,S.TCPDownloadSpeed,S.TCPUploadSpeed,S.RSSI FROM "+
									"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
									"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"') S1    ON S.MARKET_ID ='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -91 AND S.RSSI > -103 AND "+
									"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType AND  S.NetSpeedTest  LIKE 'TCP' AND S.TCPDownloadSpeed NOT LIKE 'Empty'  "+

									"Order by NetworkType, QUALITY";					
					rs = st.executeQuery(query);
					while (rs.next()) {
						DataConnectivityTo dcTo = new DataConnectivityTo();
						String tcpDownloadSpeed = rs.getString("TCPDownloadSpeed");
						String tcpUploadSpeed = rs.getString("TCPUploadSpeed");
						String rssi = rs.getString("RSSI");
						String quality = rs.getString("QUALITY");
					    networkType = rs.getString("NetworkType");
						dataMap = networkWiseMap.get(networkType);
						if(null==dataMap){
							dataMap = new HashMap<String, HashMap<String,List<Float>>>();
							downloadSpeedMap = new HashMap<String, List<Float>>();
							uploadSpeedMap = new HashMap<String, List<Float>>();
							rssiMap = new HashMap<String, List<Float>>();
						}
						else{
							downloadSpeedMap = dataMap.get("DownloadSpeed");
							uploadSpeedMap = dataMap.get("UploadSpeed");
							rssiMap = dataMap.get("rssi");
						}
						if(quality.equals("Good")){
							List<Float> dcgoodList = downloadSpeedMap.get(quality);
							List<Float> dcUploadgoodList = uploadSpeedMap.get(quality);
							List<Float> rssGoodList = rssiMap.get(quality);
							if(null==dcgoodList){
								dcgoodList = new ArrayList<Float>();
							}
							if(null==dcUploadgoodList){
								dcUploadgoodList = new ArrayList<Float>();
							}
							if(null==rssGoodList){
								rssGoodList = new ArrayList<Float>();
							}
							if(null!=tcpDownloadSpeed && !tcpDownloadSpeed.trim().equals("Empty") &&!tcpDownloadSpeed.equals("0") ){
								float tcpDownloadSpeedInt=Float.parseFloat(tcpDownloadSpeed);
								dcgoodList.add(new Float(tcpDownloadSpeedInt));
							}
							if(null!=tcpUploadSpeed && !tcpUploadSpeed.trim().equals("Empty") &&!tcpUploadSpeed.equals("0") ){
								float tcpUploadSpeedInt=Float.parseFloat(tcpUploadSpeed);
								dcUploadgoodList.add(new Float(tcpUploadSpeedInt));
							}
							if(null!=rssi && !rssi.trim().equals("Empty") && !rssi.equals("0")){
								float rssiFloat=Float.parseFloat(rssi);
								rssGoodList.add(new Float(rssiFloat));
							}
							downloadSpeedMap.put(quality, dcgoodList);
							uploadSpeedMap.put(quality, dcUploadgoodList);
							rssiMap.put(quality, rssGoodList);
						} else
						if(quality.equals("Medium")){
							List<Float> dcMediumList = downloadSpeedMap.get(quality);
							List<Float> dcUploadMediumList = uploadSpeedMap.get(quality);
							List<Float> rssMediumList = rssiMap.get(quality);
							if(null==dcMediumList){
								dcMediumList = new ArrayList<Float>();
							}
							if(null==dcUploadMediumList){
								dcUploadMediumList = new ArrayList<Float>();
							}
							if(null==rssMediumList){
								rssMediumList = new ArrayList<Float>();
							}
							if(null!=tcpDownloadSpeed && !tcpDownloadSpeed.trim().equals("Empty") && !tcpDownloadSpeed.equals("0")){
								float tcpDownloadSpeedInt=Float.parseFloat(tcpDownloadSpeed);
								dcMediumList.add(new Float(tcpDownloadSpeedInt));
							}
							if(null!=tcpUploadSpeed && !tcpUploadSpeed.trim().equals("Empty") && !tcpUploadSpeed.equals("0")){
								float tcpUploadSpeedInt=Float.parseFloat(tcpUploadSpeed);
								dcUploadMediumList.add(new Float(tcpUploadSpeedInt));
							}
							if(null!=rssi && !rssi.trim().equals("Empty")&&!rssi.equals("0")){
								float rssiFloat=Float.parseFloat(rssi);
								rssMediumList.add(new Float(rssiFloat));
							}
							
							downloadSpeedMap.put(quality, dcMediumList);
							uploadSpeedMap.put(quality, dcUploadMediumList);
							rssiMap.put(quality, rssMediumList);
						}else
						if(quality.equals("Poor")){
							List<Float> dcPoorList = downloadSpeedMap.get(quality);
							List<Float> dcUploadPoorList = downloadSpeedMap.get(quality);
							List<Float> rssPoorList = rssiMap.get(quality);
							if(null==dcPoorList){
								dcPoorList = new ArrayList<Float>();
							}
							if(null==dcUploadPoorList){
								dcUploadPoorList = new ArrayList<Float>();
							}
							if(null==rssPoorList){
								rssPoorList = new ArrayList<Float>();
							}
							if(null!=tcpDownloadSpeed && !tcpDownloadSpeed.trim().equals("Empty")&&!tcpDownloadSpeed.equals("0")){
								float tcpDownloadSpeedInt=Float.parseFloat(tcpDownloadSpeed);
								dcPoorList.add(new Float(tcpDownloadSpeedInt));
							}
							if(null!=tcpUploadSpeed && !tcpUploadSpeed.trim().equals("Empty")&&!tcpUploadSpeed.equals("0")){
								float tcpUploadSpeedInt=Float.parseFloat(tcpUploadSpeed);
								dcUploadPoorList.add(new Float(tcpUploadSpeedInt));
							}
							if(null!=rssi && !rssi.trim().equals("Empty")&&!rssi.equals("0")){
								float rssiFloat=Float.parseFloat(rssi);
								rssPoorList.add(new Float(rssiFloat));
							}
							downloadSpeedMap.put(quality, dcPoorList);
							uploadSpeedMap.put(quality, dcUploadPoorList);
							rssiMap.put(quality, rssPoorList);
						}
						dataMap.put("DownloadSpeed", downloadSpeedMap);
						dataMap.put("UploadSpeed", uploadSpeedMap);
						dataMap.put("rssi", rssiMap);
						networkWiseMap.put(networkType, dataMap);
					}
//					dataMap.put("tcpDownloadSpeedList", tcpDownloadSpeedList);
					////System.out.println("networkWiseMap-------tcp------"+networkWiseMap);
				} catch (Exception e) {
					e.printStackTrace();
				} /*finally {
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
				}*/
				return networkWiseMap;
			}
			
		 
		 public HashMap<String, HashMap<String, HashMap<String, List<Float>>>>  populateDetailedDataForUDPLte(String deviceName,
					String testName, String marketId) {
				Connection conn = null;
				Statement st = null;
				ResultSet rs = null;
				//HashMap<String,List<Integer>> dataMap = new HashMap<String, List<Integer>>();
			 	HashMap<String,HashMap<String,List<Float>>> dataMap = new HashMap<String, HashMap<String,List<Float>>>();
				HashMap<String,List<Float>> downloadCapacityMap = new HashMap<String, List<Float>>();
				HashMap<String,List<Float>> uploadCapacityMap = new HashMap<String, List<Float>>();
				HashMap<String,List<Float>> downloadQosMap = new HashMap<String, List<Float>>();
				HashMap<String,List<Float>> rssiMap = new HashMap<String, List<Float>>();
				List<Integer> udpDownloadSpeedList = new ArrayList<Integer>();
				List<Integer> udpDownloadCapacityList = new ArrayList<Integer>();
				List<Integer> udpDownloadQOSList = new ArrayList<Integer>();
				List<Integer> ressList = new ArrayList<Integer>();
				String networkType = "";
				HashMap<String, HashMap<String,HashMap<String,List<Float>>>> networkWiseMap = new HashMap<String, HashMap<String,HashMap<String,List<Float>>>>();
				

		try {
			////System.out.println("query----populateDetailedDataForUDPLte-----"+query);
			conn = DataConnectivityDaoImpl.conn;
			st = DataConnectivityDaoImpl.st;
			String query = "SELECT  S1.NetworkType AS 'NetworkType', 'Good' AS QUALITY,S.UDPDownloadCapacity,S.UDPUploadCapacity,S.UDPDownloadQOS,S.RSSI FROM "+
			"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
					"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"' AND NetworkType = 'LTE') S1    ON S.MARKET_ID='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -45 AND S.RSSI > -70 AND "+
					"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType AND  S.NetSpeedTest  LIKE 'UDP' AND S.UDPDownloadQOS NOT LIKE 'Empty'  "+


					" UNION "+

					"SELECT  S1.NetworkType AS 'NetworkType', 'Medium'  AS QUALITY,S.UDPDownloadCapacity,S.UDPUploadCapacity,S.UDPDownloadQOS,S.RSSI FROM "+
					"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
					"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"' AND NetworkType = 'LTE') S1    ON S.MARKET_ID ='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -91 AND S.RSSI > -103 AND "+
					"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType AND  S.NetSpeedTest  LIKE 'UDP' AND S.UDPDownloadQOS NOT LIKE 'Empty' "+


					" UNION "+

					"SELECT  S1.NetworkType AS 'NetworkType', 'Poor' AS QUALITY,S.UDPDownloadCapacity,S.UDPUploadCapacity,S.UDPDownloadQOS,S.RSSI FROM "+
					"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
					"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"' AND NetworkType = 'LTE') S1    ON S.MARKET_ID ='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -91 AND S.RSSI > -103 AND "+
					"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType AND  S.NetSpeedTest  LIKE 'UDP' AND S.UDPDownloadQOS NOT LIKE 'Empty'  "+

					"Order by NetworkType, QUALITY";			
			rs = st.executeQuery(query);
			while (rs.next()) {
				DataConnectivityTo dcTo = new DataConnectivityTo();
				String udpDownloadCapacity = rs.getString("UDPDownloadCapacity");
				String udpUploadCapacity = rs.getString("UDPUploadCapacity");
				String udpDownloadQOS =rs.getString("UDPDownloadQOS");
				String rssi = rs.getString("RSSI");
				String quality = rs.getString("QUALITY");
			    networkType = rs.getString("NetworkType");
				dataMap = networkWiseMap.get(networkType);
				if(null==dataMap){
					dataMap = new HashMap<String, HashMap<String,List<Float>>>();
					downloadCapacityMap = new HashMap<String, List<Float>>();
					uploadCapacityMap = new HashMap<String, List<Float>>();
					downloadQosMap = new HashMap<String, List<Float>>();
					rssiMap = new HashMap<String, List<Float>>();
				}
				else{
					downloadCapacityMap = dataMap.get("DownloadCapacity");
					uploadCapacityMap =  dataMap.get("UploadCapacity");
					downloadQosMap = dataMap.get("Qos");
					rssiMap = dataMap.get("rssi");
				}
				if(quality.equals("Good")){
					List<Float> dcgoodList = downloadCapacityMap.get(quality);
					List<Float> dcUploadgoodList = downloadCapacityMap.get(quality);
					List<Float> dQgoodList = downloadQosMap.get(quality);
					List<Float> rssGoodList = rssiMap.get(quality);
					if(null==dcgoodList){
						dcgoodList = new ArrayList<Float>();
					}
					if(null==dcUploadgoodList){
						dcUploadgoodList = new ArrayList<Float>();
					}
					if(null==dQgoodList){
						dQgoodList = new ArrayList<Float>();
					}
					if(null==rssGoodList){
						rssGoodList = new ArrayList<Float>();
					}
					if(null!=udpDownloadCapacity && !udpDownloadCapacity.trim().equals("Empty") &&!udpDownloadCapacity.equals("0") ){
						float udpDownloadCapacityInt=Float.parseFloat(udpDownloadCapacity);
						dcgoodList.add(new Float(udpDownloadCapacityInt));
					}
					if(null!=udpUploadCapacity && !udpUploadCapacity.trim().equals("Empty") &&!udpUploadCapacity.equals("0") ){
						float udpUploadCapacityInt=Float.parseFloat(udpUploadCapacity);
						dcUploadgoodList.add(new Float(udpUploadCapacityInt));
					}
					if(null!=udpDownloadQOS && !udpDownloadQOS.trim().equals("Empty")&&!udpDownloadQOS.equals("0")){
						float udpDownloadQOSInt=Float.parseFloat(udpDownloadQOS);
						dQgoodList.add(new Float(udpDownloadQOSInt));
					}
					if(null!=rssi && !rssi.trim().equals("Empty") && !rssi.equals("0")){
						float rssiFloat=Float.parseFloat(rssi);
						rssGoodList.add(new Float(rssiFloat));
					}
					downloadCapacityMap.put(quality, dcgoodList);
					uploadCapacityMap.put(quality, dcUploadgoodList);
					downloadQosMap.put(quality, dQgoodList);
					rssiMap.put(quality, rssGoodList);
				} else
				if(quality.equals("Medium")){
					List<Float> dcMediumList = downloadCapacityMap.get(quality);
					List<Float> dcUploadMediumList = uploadCapacityMap.get(quality);
					List<Float> dQMediumList = downloadQosMap.get(quality);
					List<Float> rssMediumList = rssiMap.get(quality);
					if(null==dcMediumList){
						dcMediumList = new ArrayList<Float>();
					}
					if(null==dcUploadMediumList){
						dcUploadMediumList = new ArrayList<Float>();
					}
					if(null==dQMediumList){
						dQMediumList = new ArrayList<Float>();
					}
					if(null==rssMediumList){
						rssMediumList = new ArrayList<Float>();
					}
					if(null!=udpDownloadCapacity && !udpDownloadCapacity.trim().equals("Empty") && !udpDownloadCapacity.equals("0")){
						float udpDownloadCapacityInt=Float.parseFloat(udpDownloadCapacity);
						dcMediumList.add(new Float(udpDownloadCapacityInt));
					}
					if(null!=udpUploadCapacity && !udpUploadCapacity.trim().equals("Empty") && !udpUploadCapacity.equals("0")){
						float udpUploadCapacityInt=Float.parseFloat(udpUploadCapacity);
						dcUploadMediumList.add(new Float(udpUploadCapacityInt));
					}
					if(null!=udpDownloadQOS && !udpDownloadQOS.trim().equals("Empty")&&!udpDownloadQOS.equals("0")){
						float udpDownloadQOSInt=Float.parseFloat(udpDownloadQOS);
						dQMediumList.add(new Float(udpDownloadQOSInt));
					}
					if(null!=rssi && !rssi.trim().equals("Empty")&&!rssi.equals("0")){
						float rssiFloat=Float.parseFloat(rssi);
						rssMediumList.add(new Float(rssiFloat));
					}
					
					downloadCapacityMap.put(quality, dcMediumList);
					uploadCapacityMap.put(quality, dcUploadMediumList);
					downloadQosMap.put(quality, dQMediumList);
					rssiMap.put(quality, rssMediumList);
				}else
				if(quality.equals("Poor")){
					List<Float> dcPoorList = downloadCapacityMap.get(quality);
					List<Float> dcUploadPoorList = downloadCapacityMap.get(quality);
					List<Float> dQPoorList = downloadQosMap.get(quality);
					List<Float> rssPoorList = rssiMap.get(quality);
					if(null==dcPoorList){
						dcPoorList = new ArrayList<Float>();
					}
					if(null==dcUploadPoorList){
						dcUploadPoorList = new ArrayList<Float>();
					}
					if(null==dQPoorList){
						dQPoorList = new ArrayList<Float>();
					}
					if(null==rssPoorList){
						rssPoorList = new ArrayList<Float>();
					}
					if(null!=udpDownloadCapacity && !udpDownloadCapacity.trim().equals("Empty")&&!udpDownloadCapacity.equals("0")){
						float udpDownloadCapacityInt=Float.parseFloat(udpDownloadCapacity);
						dcPoorList.add(new Float(udpDownloadCapacityInt));
					}
					if(null!=udpUploadCapacity && !udpUploadCapacity.trim().equals("Empty")&&!udpUploadCapacity.equals("0")){
						float udpUploadCapacityInt=Float.parseFloat(udpUploadCapacity);
						dcPoorList.add(new Float(udpUploadCapacityInt));
					}
					if(null!=udpDownloadQOS && !udpDownloadQOS.trim().equals("Empty")&&!udpDownloadQOS.equals("0")){
						float udpDownloadQOSInt=Float.parseFloat(udpDownloadQOS);
						dQPoorList.add(new Float(udpDownloadQOSInt));
					}
					if(null!=rssi && !rssi.trim().equals("Empty")&&!rssi.equals("0")){
						float rssiFloat=Float.parseFloat(rssi);
						rssPoorList.add(new Float(rssiFloat));
					}
					downloadCapacityMap.put(quality, dcPoorList);
					uploadCapacityMap.put(quality, dcUploadPoorList);
					downloadQosMap.put(quality, dQPoorList);
					rssiMap.put(quality, rssPoorList);
				}
				dataMap.put("DownloadCapacity", downloadCapacityMap);
				dataMap.put("UploadCapacity", uploadCapacityMap);
				dataMap.put("Qos", downloadQosMap);
				dataMap.put("rssi", rssiMap);
				networkWiseMap.put(networkType, dataMap);
			}
//			dataMap.put("tcpDownloadSpeedList", tcpDownloadSpeedList);
			////System.out.println("networkWiseMap-------udp------"+networkWiseMap);
		} catch (Exception e) {
			e.printStackTrace();
		} /*finally {
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
		}*/
		return networkWiseMap;
}		
		
		 
		 
		 public HashMap<String, HashMap<String, HashMap<String, List<Float>>>> populateDetailedDataForDNSLte(String deviceName,
					String testName, String marketId) {
				Connection conn = null;
				Statement st = null;
				ResultSet rs = null;
				//HashMap<String,List<Integer>> dataMap = new HashMap<String, List<Integer>>();
			 	HashMap<String,HashMap<String,List<Float>>> dataMap = new HashMap<String, HashMap<String,List<Float>>>();
				HashMap<String,List<Float>> avgLatencyMap = new HashMap<String, List<Float>>();
				HashMap<String,List<Float>> maxLatencyMap = new HashMap<String, List<Float>>();
				HashMap<String,List<Float>> rssiMap = new HashMap<String, List<Float>>();
				List<Float> udpDownloadSpeedList = new ArrayList<Float>();
				List<Float> dnsAvLatencyList = new ArrayList<Float>();
				List<Float> dnsMaxLatencyList = new ArrayList<Float>();
				List<Float> ressList = new ArrayList<Float>();
				String networkType = "";
				HashMap<String, HashMap<String,HashMap<String,List<Float>>>> networkWiseMap = new HashMap<String, HashMap<String,HashMap<String,List<Float>>>>();

				try {
					////System.out.println("query---populateDetailedDataForDNSLte-----------"+query);
					conn = DataConnectivityDaoImpl.conn;
					st = DataConnectivityDaoImpl.st;
					String query = "SELECT  S1.NetworkType AS 'NetworkType', 'Good' AS QUALITY,S.DNSAvLatency,S.DNSMaxLatency,S.RSSI FROM "+
							"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
									"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"' AND NetworkType = 'LTE') S1    ON S.MARKET_ID='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -45 AND S.RSSI > -70 AND "+
									"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType  AND  S.NetSpeedTest  LIKE 'DNS' AND S.DNSAvLatency NOT LIKE 'Empty' "+


									" UNION "+

									"SELECT  S1.NetworkType AS 'NetworkType', 'Medium'  AS QUALITY,S.DNSAvLatency,S.DNSMaxLatency,S.RSSI FROM "+
									"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
									"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"' AND NetworkType = 'LTE') S1    ON S.MARKET_ID ='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -91 AND S.RSSI > -103 AND "+
									"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType AND  S.NetSpeedTest  LIKE 'DNS' AND S.DNSAvLatency NOT LIKE 'Empty'  "+


									" UNION "+

									"SELECT  S1.NetworkType AS 'NetworkType', 'Poor' AS QUALITY,S.DNSAvLatency,S.DNSMaxLatency,S.RSSI FROM "+
									"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
									"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"' AND NetworkType = 'LTE') S1    ON S.MARKET_ID ='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -93 AND S.RSSI >  -103 AND "+
									"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType AND  S.NetSpeedTest  LIKE 'DNS'AND S.DNSAvLatency NOT LIKE 'Empty' "+

									"Order by NetworkType, QUALITY";					
					rs = st.executeQuery(query);
					while (rs.next()) {
						DataConnectivityTo dcTo = new DataConnectivityTo();
						String dnsAvLatency = rs.getString("dnsAvLatency");
						String dnsMaxLatency = rs.getString("DNSMaxLatency");
						String rssi = rs.getString("RSSI");
						String quality = rs.getString("QUALITY");
						networkType = rs.getString("NetworkType");
						dataMap = networkWiseMap.get(networkType);
						////System.out.println("networkType==DNSLTE===="+networkType);
						if(null==dataMap){
							dataMap = new HashMap<String, HashMap<String,List<Float>>>();
							avgLatencyMap = new HashMap<String, List<Float>>();
							maxLatencyMap = new HashMap<String, List<Float>>();
							rssiMap = new HashMap<String, List<Float>>();
						}
						else{
							avgLatencyMap = dataMap.get("AvgLatency");
							maxLatencyMap = dataMap.get("MaxLatency");
							rssiMap = dataMap.get("rssi");
						}
						if(quality.equals("Good")){
							List<Float> algoodList = avgLatencyMap.get(quality);
							List<Float> mlgoodList = maxLatencyMap.get(quality);
							List<Float> rssGoodList = rssiMap.get(quality);
							if(null==algoodList){
								algoodList = new ArrayList<Float>();
							}
							if(null==mlgoodList){
								mlgoodList = new ArrayList<Float>();
							}
							if(null==rssGoodList){
								rssGoodList = new ArrayList<Float>();
							}
							if(null!=dnsAvLatency && !dnsAvLatency.equals("0") && !dnsAvLatency.trim().equals("Empty")){
								float dnsAvLatencyFloat=Float.parseFloat(dnsAvLatency);
								algoodList.add(dnsAvLatencyFloat);
							}
							if(null!=dnsMaxLatency && !dnsMaxLatency.equals("0") && !dnsMaxLatency.trim().equals("Empty")){
								float dnsMaxLatencyFloat=Float.parseFloat(dnsMaxLatency);
								mlgoodList.add(dnsMaxLatencyFloat);
							}
							if(null!=rssi && !rssi.trim().equals("Empty") && !rssi.equals("0")){
								float rssiFloat=Float.parseFloat(rssi);
								rssGoodList.add(new Float(rssiFloat));
							}
							avgLatencyMap.put(quality, algoodList);
							maxLatencyMap.put(quality, mlgoodList);
							rssiMap.put(quality, rssGoodList);
						} else
						if(quality.equals("Medium")){
							List<Float> alMediumList = avgLatencyMap.get(quality);
							List<Float> mlMediumList = maxLatencyMap.get(quality);
							List<Float> rssMediumList = rssiMap.get(quality);
							if(alMediumList==null){
								alMediumList = new ArrayList<Float>();
							}
							if(null==mlMediumList){
								mlMediumList = new ArrayList<Float>();
							}
							if(null==rssMediumList){
								rssMediumList = new ArrayList<Float>();
							}
							if(null!=dnsAvLatency  && !dnsAvLatency.equals("0") && !dnsAvLatency.trim().equals("Empty")){
								float dnsAvLatencyFlaot=Float.parseFloat(dnsAvLatency);
								alMediumList.add(dnsAvLatencyFlaot);
							}
							if(null!=dnsMaxLatency && !dnsMaxLatency.equals("0") && !dnsMaxLatency.trim().equals("Empty")){
								float dnsMaxLatencyFloat=Float.parseFloat(dnsMaxLatency);
								mlMediumList.add(dnsMaxLatencyFloat);
							}
							if(null!=rssi && !rssi.trim().equals("Empty") && !rssi.equals("0")){
								float rssiFloat=Float.parseFloat(rssi);
								rssMediumList.add(new Float(rssiFloat));
							}
							avgLatencyMap.put(quality, alMediumList);
							maxLatencyMap.put(quality, mlMediumList);
							rssiMap.put(quality, rssMediumList);
						}else
						if(quality.equals("Poor")){
							List<Float> alPoorList = avgLatencyMap.get(quality);
							List<Float> mlPoorList = maxLatencyMap.get(quality);
							List<Float> rssPoorList = rssiMap.get(quality);
							if(null==alPoorList){
								alPoorList = new ArrayList<Float>();
							}
							if(null==mlPoorList){
								mlPoorList = new ArrayList<Float>();
							}
							if(null==rssPoorList){
								rssPoorList = new ArrayList<Float>();
							}
							if(null!=dnsAvLatency  && !dnsAvLatency.equals("0") && !dnsAvLatency.trim().equals("Empty")){
							float dnsAvLatencyFlaot=Float.parseFloat(dnsAvLatency);
								alPoorList.add(dnsAvLatencyFlaot);
							}
							if(null!=dnsMaxLatency && !dnsMaxLatency.equals("0") && !dnsMaxLatency.trim().equals("Empty")){
								float dnsMaxLatencyFloat=Float.parseFloat(dnsMaxLatency);
								mlPoorList.add(dnsMaxLatencyFloat);
							}
							if(null!=rssi && !rssi.trim().equals("Empty") && !rssi.equals("0")){
								float rssiFloat=Float.parseFloat(rssi);
								rssPoorList.add(new Float(rssiFloat));
							}
							
							avgLatencyMap.put(quality, alPoorList);
							maxLatencyMap.put(quality, mlPoorList);	
							rssiMap.put(quality, rssPoorList);
						}
						dataMap.put("AvgLatency", avgLatencyMap);
						dataMap.put("MaxLatency", maxLatencyMap);
						dataMap.put("rssi", rssiMap);
						networkWiseMap.put(networkType, dataMap);
						
					}
				} catch (Exception e) {
					e.printStackTrace();
				} /*finally {
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
				}*/
			//	//System.out.println("networkWiseMap-----DNS-LTE-------"+networkWiseMap);
				return networkWiseMap;
		}
		public Float getAverageList(List<Float> list){
			Float averagevalue = new Float(0);
			Float listSum = new Float(0);
			if(null!=list){
				for(int i=0;i<list.size();i++){
					listSum = listSum+list.get(i);
				}
				averagevalue = listSum/list.size();
			}
			else{
				averagevalue = null;
			}
			return averagevalue;
		}
		
		
		 public HashMap<String,List<Float>> populateDataForVOIP(String deviceName,
					String testName, String marketId) {
				Connection conn = null;
				Statement st = null;
				ResultSet rs = null;
				HashMap<String,List<Float>> dataMap = new HashMap<String, List<Float>>();
				List<Float> upstreamJitterMaxList = new ArrayList<Float>();
				List<Float> downstreamJitterMaxList = new ArrayList<Float>();
				List<Float> sipByeList = new ArrayList<Float>();
				List<Float> sipInviteList = new ArrayList<Float>();
				List<Float> sipRegisterList = new ArrayList<Float>();
				List<Float> ressList = new ArrayList<Float>();
				
				List<Float> wifiupstreamJitterMaxList = new ArrayList<Float>();
				List<Float> wifidownstreamJitterMaxList = new ArrayList<Float>();
				List<Float> wifisipByeList = new ArrayList<Float>();
				List<Float> wifisipInviteList = new ArrayList<Float>();
				List<Float> wifisipRegisterList = new ArrayList<Float>();
				List<Float> wifiressList = new ArrayList<Float>();
				

				try {
					////System.out.println("query---populateDataForVoip-----------"+query);
					conn = DataConnectivityDaoImpl.conn;
					st = DataConnectivityDaoImpl.st;
					String query = "SELECT  S1.NetworkType AS 'NetworkType', 'Good' AS QUALITY,AVG(S.VOIPSIPRegister),AVG(S.VoIPUpstreamMaxJitter),AVG(S.VoIPDownstreamMaxJitter),AVG(S.VoIPSIPBye),AVG(S.VoIPSIPInvite),AVG(S.RSSI) FROM "+
							"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
									"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"') S1    ON S.MARKET_ID='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -45 AND S.RSSI > -70 AND "+
									"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType  AND  S.NetSpeedTest  LIKE 'VOIP' AND S.VoIPUpstreamMaxJitter NOT LIKE 'Empty' GROUP BY QUALITY,S1.NetworkType "+


									" UNION "+

									"SELECT  S1.NetworkType AS 'NetworkType', 'Medium'  AS QUALITY,AVG(S.VOIPSIPRegister),AVG(S.VoIPUpstreamMaxJitter),AVG(S.VoIPDownstreamMaxJitter),AVG(S.VoIPSIPBye),AVG(S.VoIPSIPInvite),AVG(S.RSSI) FROM "+
									"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
									"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"') S1    ON S.MARKET_ID ='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -91 AND S.RSSI > -103 AND "+
									"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType  AND  S.NetSpeedTest  LIKE 'VOIP' AND S.VoIPUpstreamMaxJitter NOT LIKE 'Empty' GROUP BY QUALITY,S1.NetworkType "+


									" UNION "+

									"SELECT  S1.NetworkType AS 'NetworkType', 'Poor' AS QUALITY,AVG(S.VOIPSIPRegister),AVG(S.VoIPUpstreamMaxJitter),AVG(S.VoIPDownstreamMaxJitter),AVG(S.VoIPSIPBye),AVG(S.VoIPSIPInvite),AVG(S.RSSI) FROM "+
									"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
									"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"') S1    ON S.MARKET_ID ='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -103 AND "+
									"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType AND  S.NetSpeedTest  LIKE 'VOIP' AND S.VoIPUpstreamMaxJitter NOT LIKE 'Empty' GROUP BY QUALITY,S1.NetworkType "+

									"Order by NetworkType, QUALITY";
					System.out.println("srikanth----------"+query);
					rs = st.executeQuery(query);
					while (rs.next()) {
						Float voipUpstreamMaxJitter = rs.getFloat("AVG(S.VoIPUpstreamMaxJitter)");
						Float voipDownstreamMaxJitter =rs.getFloat("AVG(S.VoIPDownstreamMaxJitter)");
						Float voipSIPBye =rs.getFloat("AVG(S.VoIPSIPBye)");
						Float rssi = rs.getFloat("AVG(S.RSSI)");
						Float voipSIPInvite =rs.getFloat("AVG(S.VoIPSIPInvite)");
						Float voipSIPRegister =rs.getFloat("AVG(S.VOIPSIPRegister)");
						String networkType =  rs.getString("NetworkType");
						////System.out.println("rssi-----------"+rssi);
//						//System.out.println("udpDownloadCapacity-------"+udpDownloadCapacity);
						if(networkType.equalsIgnoreCase("WIFI")){
							if(null==voipUpstreamMaxJitter || voipUpstreamMaxJitter==0){
								wifiupstreamJitterMaxList.add(null);
							}
							else{
								wifiupstreamJitterMaxList.add(voipUpstreamMaxJitter);
							}
							
							if(null==voipDownstreamMaxJitter || voipDownstreamMaxJitter==0){
								wifidownstreamJitterMaxList.add(null);
							}
							else{
								wifidownstreamJitterMaxList.add(voipDownstreamMaxJitter);
							}
							if(null==voipSIPBye || voipSIPBye==0){
								wifisipByeList.add(null);
							}
							else{
								wifisipByeList.add(voipSIPBye);
							}
							if(null==voipSIPInvite || voipSIPInvite==0){
								wifisipInviteList.add(null);
							}
							else{
								wifisipInviteList.add(voipSIPInvite);
							}
							if(null==voipSIPRegister || voipSIPRegister==0){
								wifisipRegisterList.add(null);
							}
							else{
								wifisipRegisterList.add(voipSIPRegister);
							}
							if(rssi == 0 ||null==rssi){
								wifiressList.add(null);
							}
							else{
								wifiressList.add(rssi);
							}
						
			
						}
						else{
							wifiupstreamJitterMaxList.add(null);
							wifidownstreamJitterMaxList.add(null);
							wifisipByeList.add(voipSIPBye);
							wifisipInviteList.add(null);
							wifisipRegisterList.add(null);
							wifiressList.add(null);
						}
						if(!networkType.equalsIgnoreCase("WIFI")){
							if(null==voipUpstreamMaxJitter || voipUpstreamMaxJitter==0){
								upstreamJitterMaxList.add(null);
							}
							else{
								upstreamJitterMaxList.add(voipUpstreamMaxJitter);
							}
							
							if(null==voipDownstreamMaxJitter || voipDownstreamMaxJitter==0){
								downstreamJitterMaxList.add(null);
							}
							else{
								downstreamJitterMaxList.add(voipDownstreamMaxJitter);
							}
							if(null==voipSIPBye || voipSIPBye==0){
								sipByeList.add(null);
							}
							else{
								sipByeList.add(voipSIPBye);
							}
							if(null==voipSIPInvite || voipSIPInvite==0){
								sipInviteList.add(null);
							}
							else{
								sipInviteList.add(voipSIPInvite);
							}
							if(null==voipSIPRegister || voipSIPRegister==0){
								sipRegisterList.add(null);
							}
							else{
								sipRegisterList.add(voipSIPRegister);
							}
							if(rssi == 0 ||null==rssi){
								ressList.add(null);
							}
							else{
								ressList.add(rssi);
							}
						}
						else{
							upstreamJitterMaxList.add(null);
							downstreamJitterMaxList.add(null);
							sipByeList.add(voipSIPBye);
							sipInviteList.add(null);
							sipRegisterList.add(null);
							ressList.add(null);
							
						}
						
						
					}
					dataMap.put("upstreamJitterMaxList", upstreamJitterMaxList);
					dataMap.put("downstreamJitterMaxList", downstreamJitterMaxList);
					dataMap.put("sipByeList", sipByeList);
					dataMap.put("sipInviteList", sipInviteList);
					dataMap.put("sipRegisterList", sipRegisterList);
					dataMap.put("ressList", ressList);
					
					dataMap.put("wifiupstreamJitterMaxList", wifiupstreamJitterMaxList);
					dataMap.put("wifidownstreamJitterMaxList", wifidownstreamJitterMaxList);
					dataMap.put("wifisipByeList", wifisipByeList);
					dataMap.put("wifisipInviteList", wifisipInviteList);
					dataMap.put("wifisipRegisterList", wifisipRegisterList);
					dataMap.put("wifiressList", wifiressList);
					////System.out.println("dataMap------voip-------"+dataMap);
				} catch (Exception e) {
					e.printStackTrace();
				} /*finally {
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
				}*/
				return dataMap;
			}
		 
			public HashMap<String, HashMap<String,HashMap<String,List<Float>>>> populateDetailedDataForVOIP(
					String deviceName, String testName, String marketId) {
					Connection conn = null;
					Statement st = null;
					ResultSet rs = null;
//					HashMap<String,List<Integer>> dataMap = new HashMap<String, List<Integer>>();
					HashMap<String,HashMap<String,List<Float>>> dataMap = new HashMap<String, HashMap<String,List<Float>>>();
					HashMap<String,List<Float>> upstreamJitterMaxMap = new HashMap<String, List<Float>>();
					HashMap<String,List<Float>> downstreamJitterMaxMap = new HashMap<String, List<Float>>();
					HashMap<String,List<Float>> sipByeMap = new HashMap<String, List<Float>>();
					HashMap<String,List<Float>> sipInviteMap = new HashMap<String, List<Float>>();
					HashMap<String,List<Float>> sipRegMap = new HashMap<String, List<Float>>();
					HashMap<String,List<Float>> rssiMap = new HashMap<String, List<Float>>();
					String networkType = "";
					HashMap<String, HashMap<String,HashMap<String,List<Float>>>> networkWiseMap = new HashMap<String, HashMap<String,HashMap<String,List<Float>>>>();
				 
					try {
						////System.out.println("query-----populateDetailedDataForVOIP----"+query);
						conn = DataConnectivityDaoImpl.conn;
						st = DataConnectivityDaoImpl.st;
						String query = "SELECT  S1.NetworkType AS 'NetworkType', 'Good' AS QUALITY,S.VoIPSIPInvite,S.VOIPSIPRegister,S.VoIPUpstreamMaxJitter,S.VoIPDownstreamMaxJitter,S.VoIPSIPBye,S.VoIPSIPInvite,S.RSSI FROM "+
								"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
										"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"') S1    ON S.MARKET_ID='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -45 AND S.RSSI > -70 AND "+
										"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType AND  S.NetSpeedTest  LIKE 'VOIP' AND S.VoIPUpstreamMaxJitter NOT LIKE 'Empty'  "+


										" UNION "+

										"SELECT  S1.NetworkType AS 'NetworkType', 'Medium'  AS QUALITY,S.VoIPSIPInvite,S.VOIPSIPRegister,S.VoIPUpstreamMaxJitter,S.VoIPDownstreamMaxJitter,S.VoIPSIPBye,S.VoIPSIPInvite,S.RSSI FROM "+
										"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
										"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"') S1    ON S.MARKET_ID ='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -91 AND S.RSSI > -103 AND "+
										"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType AND  S.NetSpeedTest  LIKE 'VOIP' AND S.VoIPUpstreamMaxJitter NOT LIKE 'Empty' "+


										" UNION "+

										"SELECT  S1.NetworkType AS 'NetworkType', 'Poor' AS QUALITY,S.VoIPSIPInvite,S.VOIPSIPRegister,S.VoIPUpstreamMaxJitter,S.VoIPDownstreamMaxJitter,S.VoIPSIPBye,S.VoIPSIPInvite,S.RSSI FROM "+
										"STG_NET_RESULTS S RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from "+
										"STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"') S1    ON S.MARKET_ID ='"+marketId+"' and S.DEVICE_MODEL='"+deviceName+"' AND S.RSSI < -91 AND S.RSSI > -103 AND "+
										"TEST_NAME ='"+testName+"' AND S1.NetworkType  = S.NetworkType AND  S.NetSpeedTest  LIKE 'VOIP' AND S.VoIPUpstreamMaxJitter NOT LIKE 'Empty'  "+

										"Order by NetworkType, QUALITY";						
						rs = st.executeQuery(query);
						while (rs.next()) {
							DataConnectivityTo dcTo = new DataConnectivityTo();
							String voipUpstreamMaxJitter = rs.getString("VoIPUpstreamMaxJitter");
							String voipDownstreamMaxJitter =rs.getString("VoIPDownstreamMaxJitter");
							String voipSIPBye =rs.getString("VoIPSIPBye");
							String voipSIPInvite =rs.getString("VoIPSIPInvite");
							String voipSIPReg =rs.getString("VOIPSIPRegister");
							
							String rssi = rs.getString("RSSI");
							String quality = rs.getString("QUALITY");
						    networkType = rs.getString("NetworkType");
							dataMap = networkWiseMap.get(networkType);
							if(null==dataMap){
								dataMap = new HashMap<String, HashMap<String,List<Float>>>();
								upstreamJitterMaxMap = new HashMap<String, List<Float>>();
								downstreamJitterMaxMap = new HashMap<String, List<Float>>();
								sipByeMap = new HashMap<String, List<Float>>();
								sipInviteMap = new HashMap<String, List<Float>>();
								sipRegMap = new HashMap<String, List<Float>>();
								rssiMap = new HashMap<String, List<Float>>();
							}
							else{
								upstreamJitterMaxMap = dataMap.get("UpstreamMaxJitter");
								downstreamJitterMaxMap = dataMap.get("DownstreamMaxJitter");
								sipByeMap = dataMap.get("SIPBye");
								sipInviteMap = dataMap.get("SIPInvite");
								sipRegMap = dataMap.get("SIPReg");
								rssiMap = dataMap.get("rssi");
							}
							if(quality.equals("Good")){
								List<Float> upGoodList = upstreamJitterMaxMap.get(quality);
								List<Float> downGoodList = downstreamJitterMaxMap.get(quality);
								List<Float> sipGoodList = sipByeMap.get(quality);
								List<Float> sipInvGoodList = sipInviteMap.get(quality);
								List<Float> sipRegGoodList = sipRegMap.get(quality);
								List<Float> rssGoodList = rssiMap.get(quality);
								if(null==upGoodList){
									upGoodList = new ArrayList<Float>();
								}
								if(null==downGoodList){
									downGoodList = new ArrayList<Float>();
								}
								if(null==sipGoodList){
									sipGoodList = new ArrayList<Float>();
								}
								if(null==sipRegGoodList){
									sipRegGoodList = new ArrayList<Float>();
								}
								if(null==sipInvGoodList){
									sipInvGoodList = new ArrayList<Float>();
								}
								if(null==rssGoodList){
									rssGoodList = new ArrayList<Float>();
								}
								if(null!=voipUpstreamMaxJitter && !voipUpstreamMaxJitter.trim().equals("Empty") &&!voipUpstreamMaxJitter.equals("0") ){
									float voipUpstreamMaxJitterFloat=Float.parseFloat(voipUpstreamMaxJitter);
									upGoodList.add(new Float(voipUpstreamMaxJitterFloat));
								}
								if(null!=voipDownstreamMaxJitter && !voipDownstreamMaxJitter.trim().equals("Empty")&&!voipDownstreamMaxJitter.equals("0")){
									float voipDownstreamMaxJitterFloat=Float.parseFloat(voipDownstreamMaxJitter);
									downGoodList.add(new Float(voipDownstreamMaxJitterFloat));
								}
								if(null!=voipSIPBye && !voipSIPBye.trim().equals("Empty")&&!voipSIPBye.equals("0")){
									float voipSIPByeFloat=Float.parseFloat(voipSIPBye);
									sipGoodList.add(new Float(voipSIPByeFloat));
								}
								if(null!=voipSIPInvite && !voipSIPInvite.trim().equals("Empty")&&!voipSIPInvite.equals("0")){
									float voipSIPInviteFloat=Float.parseFloat(voipSIPInvite);
									sipInvGoodList.add(new Float(voipSIPInviteFloat));
								}
								if(null!=voipSIPReg && !voipSIPReg.trim().equals("Empty")&&!voipSIPReg.equals("0")){
									float voipSIPRegFloat=Float.parseFloat(voipSIPReg);
									sipRegGoodList.add(new Float(voipSIPRegFloat));
								}
								if(null!=rssi && !rssi.trim().equals("Empty") && !rssi.equals("0")){
									float rssiFloat=Float.parseFloat(rssi);
									rssGoodList.add(new Float(rssiFloat));
								}
								upstreamJitterMaxMap.put(quality, upGoodList);
								downstreamJitterMaxMap.put(quality, downGoodList);
								sipByeMap.put(quality, sipGoodList);
								sipInviteMap.put(quality, sipInvGoodList);
								sipRegMap.put(quality, sipRegGoodList);
								rssiMap.put(quality, rssGoodList);
							} else
							if(quality.equals("Medium")){
								List<Float> upMediumList = upstreamJitterMaxMap.get(quality);
								List<Float> downMediumList = downstreamJitterMaxMap.get(quality);
								List<Float> sipMediumList = sipByeMap.get(quality);
								List<Float> sipInvMediumList = sipInviteMap.get(quality);
								List<Float> sipRegMediumList = sipRegMap.get(quality);
								List<Float> rssMediumList = rssiMap.get(quality);
								if(null==upMediumList){
									upMediumList = new ArrayList<Float>();
								}
								if(null==downMediumList){
									downMediumList = new ArrayList<Float>();
								}
								if(null==sipMediumList){
									sipMediumList = new ArrayList<Float>();
								}
								if(null==sipInvMediumList){
									sipInvMediumList = new ArrayList<Float>();
								}
								if(null==sipRegMediumList){
									sipRegMediumList = new ArrayList<Float>();
								}
								if(null==rssMediumList){
									rssMediumList = new ArrayList<Float>();
								}
								if(null!=voipUpstreamMaxJitter && !voipUpstreamMaxJitter.trim().equals("Empty") &&!voipUpstreamMaxJitter.equals("0") ){
									float voipUpstreamMaxJitterFloat=Float.parseFloat(voipUpstreamMaxJitter);
									upMediumList.add(new Float(voipUpstreamMaxJitterFloat));
								}
								if(null!=voipDownstreamMaxJitter && !voipDownstreamMaxJitter.trim().equals("Empty")&&!voipDownstreamMaxJitter.equals("0")){
									float voipDownstreamMaxJitterFloat=Float.parseFloat(voipDownstreamMaxJitter);
									downMediumList.add(new Float(voipDownstreamMaxJitterFloat));
								}
								if(null!=voipSIPBye && !voipSIPBye.trim().equals("Empty")&&!voipSIPBye.equals("0")){
									float voipSIPByeFloat=Float.parseFloat(voipSIPBye);
									sipMediumList.add(new Float(voipSIPByeFloat));
								}
								if(null!=voipSIPInvite && !voipSIPInvite.trim().equals("Empty")&&!voipSIPInvite.equals("0")){
									float voipSIPInviteFloat=Float.parseFloat(voipSIPInvite);
									sipInvMediumList.add(new Float(voipSIPInviteFloat));
								}
								if(null!=voipSIPReg && !voipSIPReg.trim().equals("Empty")&&!voipSIPReg.equals("0")){
									float voipSIPRegFloat=Float.parseFloat(voipSIPReg);
									sipRegMediumList.add(new Float(voipSIPRegFloat));
								}
								if(null!=rssi && !rssi.trim().equals("Empty") && !rssi.equals("0")){
									float rssiFloat=Float.parseFloat(rssi);
									rssMediumList.add(new Float(rssiFloat));
								}
								upstreamJitterMaxMap.put(quality, upMediumList);
								downstreamJitterMaxMap.put(quality, downMediumList);
								sipByeMap.put(quality, sipMediumList);
								sipInviteMap.put(quality, sipInvMediumList);
								sipRegMap.put(quality, sipRegMediumList);
								rssiMap.put(quality, rssMediumList);
							}else
							if(quality.equals("Poor")){
								List<Float> upPoorList = upstreamJitterMaxMap.get(quality);
								List<Float> downPoorList = downstreamJitterMaxMap.get(quality);
								List<Float> sipPoorList = sipByeMap.get(quality);
								List<Float> sipInvPoorList = sipInviteMap.get(quality);
								List<Float> sipRegPoorList = sipRegMap.get(quality);
								List<Float> rssPoorList = rssiMap.get(quality);
								if(null==upPoorList){
									upPoorList = new ArrayList<Float>();
								}
								if(null==downPoorList){
									downPoorList = new ArrayList<Float>();
								}
								if(null==sipPoorList){
									sipPoorList = new ArrayList<Float>();
								}
								if(null==sipInvPoorList){
									sipInvPoorList = new ArrayList<Float>();
								}
								if(null==sipRegPoorList){
									sipRegPoorList = new ArrayList<Float>();
								}
								if(null==rssPoorList){
									rssPoorList = new ArrayList<Float>();
								}
								if(null!=voipUpstreamMaxJitter && !voipUpstreamMaxJitter.trim().equals("Empty") &&!voipUpstreamMaxJitter.equals("0") ){
									float voipUpstreamMaxJitterFloat=Float.parseFloat(voipUpstreamMaxJitter);
									upPoorList.add(new Float(voipUpstreamMaxJitterFloat));
								}
								if(null!=voipDownstreamMaxJitter && !voipDownstreamMaxJitter.trim().equals("Empty")&&!voipDownstreamMaxJitter.equals("0")){
									float voipDownstreamMaxJitterFloat=Float.parseFloat(voipDownstreamMaxJitter);
									downPoorList.add(new Float(voipDownstreamMaxJitterFloat));
								}
								if(null!=voipSIPBye && !voipSIPBye.trim().equals("Empty")&&!voipSIPBye.equals("0")){
									float voipSIPByeFloat=Float.parseFloat(voipSIPBye);
									sipPoorList.add(new Float(voipSIPByeFloat));
								}
								if(null!=voipSIPInvite && !voipSIPInvite.trim().equals("Empty")&&!voipSIPInvite.equals("0")){
									float voipSIPInviteFloat=Float.parseFloat(voipSIPInvite);
									sipInvPoorList.add(new Float(voipSIPInviteFloat));
								}
								if(null!=voipSIPReg && !voipSIPReg.trim().equals("Empty")&&!voipSIPReg.equals("0")){
									float voipSIPRegFloat=Float.parseFloat(voipSIPReg);
									sipRegPoorList.add(new Float(voipSIPRegFloat));
								}
								if(null!=rssi && !rssi.trim().equals("Empty")&&!rssi.equals("0")){
									float rssiFloat=Float.parseFloat(rssi);
									rssPoorList.add(new Float(rssiFloat));
								}
								upstreamJitterMaxMap.put(quality, upPoorList);
								downstreamJitterMaxMap.put(quality, downPoorList);
								sipByeMap.put(quality, sipPoorList);
								sipInviteMap.put(quality, sipInvPoorList);
								sipRegMap.put(quality, sipRegPoorList);
								rssiMap.put(quality, rssPoorList);
							}
							dataMap.put("UpstreamMaxJitter", upstreamJitterMaxMap);
							dataMap.put("DownstreamMaxJitter", downstreamJitterMaxMap);
							dataMap.put("SIPBye", sipByeMap);
							dataMap.put("SIPInvite", sipInviteMap);
							dataMap.put("SIPReg", sipRegMap);
							dataMap.put("rssi", rssiMap);
							networkWiseMap.put(networkType, dataMap);
						}
//						dataMap.put("tcpDownloadSpeedList", tcpDownloadSpeedList);
					//	//System.out.println("networkWiseMap-------Voip------"+networkWiseMap);
					} catch (Exception e) {
						e.printStackTrace();
					} /*finally {
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
					}*/
					return networkWiseMap;
			}	
/////////////////////// FTP LTE////////////////////////

/*			 public HashMap<String,List<Float>> populateDataForFTPLte(String deviceName,
						String testName, String marketId,List<String> networkList) {
				 //System.out.println("networkList----------"+networkList);
					HashMap<String,List<Float>> dataMap = new HashMap<String, List<Float>>();
					List<Float> ftpThroughputList = new ArrayList<Float>();
					List<Float> rssList = new ArrayList<Float>();
					HashMap<String, List<Float>> rssMap = new HashMap<String, List<Float>>();
					HashMap<String, HashMap<String, List<Float>>> networkWiseMap = new HashMap<String, HashMap<String, List<Float>>>();
					HashMap<String, List<Float>> throughputMap = new HashMap<String, List<Float>>();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					SimpleDateFormat newSdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
					try {
						List<DeviceInfo> deviceInfoList = MultipleThroughputDetailsResultsForFTPLte(testName,marketId);
						Date date = null;//sdf.parse("2013-07-01 16:46:01");
//						//System.out.println(newSdf.format(date));
						//populating netowirdwise, good medium and poor values
						HashMap<String,List<Float>> dummyValueMap =new HashMap<String, List<Float>>();
						List<Float> dummyValueList = new ArrayList<Float>();
						for(int i=0;i<3;i++){
							dummyValueList.add(new Float(0));
						}
						dummyValueMap.put("Good", dummyValueList);
						dummyValueMap.put("Medium", dummyValueList);
						dummyValueMap.put("Poor", dummyValueList);
						for(int i=0;i<networkList.size();i++){
							networkWiseMap.put("rss_"+networkList.get(i), new HashMap<String, List<Float>>());
							networkWiseMap.put("throughput_"+networkList.get(i), new HashMap<String, List<Float>>());
						}
						
						for(int k=0;k<deviceInfoList.size();k++){
							DeviceInfo deviceInfo = deviceInfoList.get(k);
							float throughput = new Float(deviceInfo.getThroughputmain())/1000000;
							String timeStamp = deviceInfo.getTimeStampForEachSample();
							date =sdf.parse(timeStamp);
							String getRssQuery = "select * from ("+

							"(select * from stg_net_results where str_to_date(CallTimeStamp,'%d/%m/%Y %H:%i:%s') > str_to_date('"+newSdf.format(date)+"','%d/%m/%Y %H:%i:%s') ORDER BY str_to_date(CallTimeStamp,'%d/%m/%Y %H:%i:%s') ASC LIMIT 1)"+
							"union"+
							"(select * from stg_net_results where str_to_date(CallTimeStamp,'%d/%m/%Y %H:%i:%s') < str_to_date('"+newSdf.format(date)+"','%d/%m/%Y %H:%i:%s') ORDER BY str_to_date(CallTimeStamp,'%d/%m/%Y %H:%i:%s') DESC LIMIT 1)"+

							") as temp order by str_to_date(CallTimeStamp,'%d/%m/%Y %H:%i:%s') desc limit 1";
							////System.out.println("getRssQuery-----------------"+getRssQuery);
							ResultSet rs = st.executeQuery(getRssQuery);
							while(rs.next()){
								////System.out.println("networkWiseMap-----------"+networkWiseMap);
								float rssValue = rs.getFloat("RSSI");
								////System.out.println("throughput--------------"+throughput);
								////System.out.println("rssValue---------"+rssValue);
								////System.out.println("deviceInfo.getnetwork------"+deviceInfo.getNetworkType());
								rssMap = networkWiseMap.get("rss_"+deviceInfo.getNetworkType());
								throughputMap = networkWiseMap.get("throughput_"+deviceInfo.getNetworkType());
								if(rssValue<-65 && rssValue>-80){
									List<Float> rssGood = rssMap.get("Good");
									List<Float> throughPutGood = throughputMap.get("Good");
									if(null==rssGood){
										rssGood = new ArrayList<Float>();
										throughPutGood = new ArrayList<Float>();
									}
									////System.out.println("rssGood--------"+rssGood);
									rssGood.add(rssValue);
									rssMap.put("Good", rssGood);
									////System.out.println("rssMap---in good condition-------"+rssMap);
									throughPutGood.add(throughput);
									throughputMap.put("Good", throughPutGood);
									////System.out.println("rssMap---in good condition----throughput---"+rssMap);
								}
								else if(rssValue<-80 && rssValue>-95){
									List<Float> rssMedium = rssMap.get("Medium");
									List<Float> throughPutMedium = throughputMap.get("Medium");
									if(null==rssMedium){
										rssMedium = new ArrayList<Float>();
										rssMedium = new ArrayList<Float>();
									}
									rssMedium.add(rssValue);
									rssMap.put("Medium", rssMedium);
									throughPutMedium.add(throughput);
									throughputMap.put("Medium", throughPutMedium);
								
							}
								else if(rssValue<-93 && rssValue>-103){
									List<Float> rssPoor = rssMap.get("Poor");
									List<Float> throughPutPoor = throughputMap.get("Poor");
									if(null==rssPoor){
										rssPoor = new ArrayList<Float>();
										throughPutPoor = new ArrayList<Float>();
									}
									rssPoor.add(rssValue);
									rssMap.put("Poor", rssPoor);
									throughPutPoor.add(throughput);
									throughputMap.put("Poor", throughPutPoor);
								}
								////System.out.println("rssMap---before adding--------"+rssMap);
								networkWiseMap.put("rss_"+deviceInfo.getNetworkType(), rssMap);
								////System.out.println("networkWiseMap------after adding------"+networkWiseMap);
								networkWiseMap.put("throughput_"+deviceInfo.getNetworkType(), throughputMap);
							}
							
						}
						
						for(int i=0;i<networkList.size();i++){
							rssMap = networkWiseMap.get("rss_"+networkList.get(i));
							throughputMap = networkWiseMap.get("throughput_"+networkList.get(i));
							rssList.add(getAverageList(rssMap.get("Good")));
							rssList.add(getAverageList(rssMap.get("Medium")));
							rssList.add(getAverageList(rssMap.get("Poor")));
							ftpThroughputList.add(getAverageList(throughputMap.get("Good")));
							ftpThroughputList.add(getAverageList(throughputMap.get("Medium")));
							ftpThroughputList.add(getAverageList(throughputMap.get("Poor")));
						}
						
						//System.out.println("rsslist=============="+rssList);
						dataMap.put("ftpThroughputList", ftpThroughputList);
						dataMap.put("ftpRssList", rssList);
						dataMap.put("Good", throughputMap.get("Good"));
						dataMap.put("Poor", throughputMap.get("Poor"));
						dataMap.put("Medium", throughputMap.get("Medium"));
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					return dataMap;
				}*/
			
			
			public HashMap<String,List<Float>> populateDataForFTPLte(String deviceName,
				      String testName, String marketId,List<String> networkList) {
					Connection conn = null;
					Statement st = null;
					ResultSet rs = null;
				     //System.out.println("networkList----------"+networkList);
				     HashMap<String,List<Float>> dataMap = new HashMap<String, List<Float>>();
				     List<Float> ftpThroughputList = new ArrayList<Float>();
				     List<Float> rssList = new ArrayList<Float>();
				     HashMap<String, List<Float>> rssMap = new HashMap<String, List<Float>>();
				     HashMap<String, HashMap<String, List<Float>>> networkWiseMap = new HashMap<String, HashMap<String, List<Float>>>();
				     HashMap<String, List<Float>> throughputMap = new HashMap<String, List<Float>>();
				     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				     SimpleDateFormat newSdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
				     try {
				      List<DeviceInfoTO> deviceInfoList = getTestNameMultipleThroughputDetailsResultsForFTPLte(testName,marketId);
				      Date date = null;//sdf.parse("2013-07-01 16:46:01");
//				      //System.out.println(newSdf.format(date));
				      //populating netowirdwise, good medium and poor values
				      HashMap<String,List<Float>> dummyValueMap =new HashMap<String, List<Float>>();
				      /*List<Float> dummyValueList = new ArrayList<Float>();
				      for(int i=0;i<3;i++){
				       dummyValueList.add(new Float(0));
				      }*/
				      /*dummyValueMap.put("Good", dummyValueList);
				      dummyValueMap.put("Medium", dummyValueList);
				      dummyValueMap.put("Poor", dummyValueList);*/
				      for(int i=0;i<networkList.size();i++){
				       networkWiseMap.put("rss_"+networkList.get(i), new HashMap<String, List<Float>>());
				       networkWiseMap.put("throughput_"+networkList.get(i), new HashMap<String, List<Float>>());
				      }
				      
				      for(int k=0;k<deviceInfoList.size();k++){
				       DeviceInfoTO deviceInfo = deviceInfoList.get(k);
				       float throughput = new Float(deviceInfo.getThroughputmain())/1000000;
				       String timeStamp = deviceInfo.getTimeStampForEachSample();
				       date =sdf.parse(timeStamp);
				       String getRssQuery = "select * from ("+

				       "(select * from stg_net_results where NetworkType = 'LTE' AND str_to_date(CallTimeStamp,'%d/%m/%Y %H:%i:%s') > str_to_date('"+newSdf.format(date)+"','%d/%m/%Y %H:%i:%s') ORDER BY str_to_date(CallTimeStamp,'%d/%m/%Y %H:%i:%s')  ASC LIMIT 1)"+
				       "union"+
				       "(select * from stg_net_results where NetworkType = 'LTE' AND str_to_date(CallTimeStamp,'%d/%m/%Y %H:%i:%s') < str_to_date('"+newSdf.format(date)+"','%d/%m/%Y %H:%i:%s') ORDER BY str_to_date(CallTimeStamp,'%d/%m/%Y %H:%i:%s')  DESC LIMIT 1)"+

				       ") as temp order by str_to_date(CallTimeStamp,'%d/%m/%Y %H:%i:%s') desc limit 1";
				       //System.out.println("getRssQuery-----------------"+getRssQuery);
				       conn = DataConnectivityDaoImpl.conn;
				       st = DataConnectivityDaoImpl.st;
				       rs = st.executeQuery(getRssQuery);
				       while(rs.next()){
				        ////System.out.println("networkWiseMap-----------"+networkWiseMap);
				        float rssValue = rs.getFloat("RSSI");
				        ////System.out.println("throughput--------------"+throughput);
				        ////System.out.println("rssValue---------"+rssValue);
				        ////System.out.println("deviceInfo.getnetwork------"+deviceInfo.getNetworkType());
				        rssMap = networkWiseMap.get("rss_"+deviceInfo.getNetworkType());
				        throughputMap = networkWiseMap.get("throughput_"+deviceInfo.getNetworkType());
				        if(rssValue<-65 && rssValue>-80){
				         List<Float> rssGood = rssMap.get("Good");
				         List<Float> throughPutGood = throughputMap.get("Good");
				         if(null==rssGood){
				          rssGood = new ArrayList<Float>();
				          throughPutGood = new ArrayList<Float>();
				         }
				         ////System.out.println("rssGood--------"+rssGood);
				         rssGood.add(rssValue);
				         rssMap.put("Good", rssGood);
				         ////System.out.println("rssMap---in good condition-------"+rssMap);
				         throughPutGood.add(throughput);
				         throughputMap.put("Good", throughPutGood);
				         ////System.out.println("rssMap---in good condition----throughput---"+rssMap);
				        }
				        else if(rssValue<-80 && rssValue>-95){
				         List<Float> rssMedium = rssMap.get("Medium");
				         List<Float> throughPutMedium = throughputMap.get("Medium");
				         if(null==rssMedium){
				          rssMedium = new ArrayList<Float>();
				          rssMedium = new ArrayList<Float>();
				         }
				         rssMedium.add(rssValue);
				         rssMap.put("Medium", rssMedium);
				         throughPutMedium.add(throughput);
				         throughputMap.put("Medium", throughPutMedium);
				        
				       }
				        else if(rssValue<-93 && rssValue>-103){
				         List<Float> rssPoor = rssMap.get("Poor");
				         List<Float> throughPutPoor = throughputMap.get("Poor");
				         if(null==rssPoor){
				          rssPoor = new ArrayList<Float>();
				          throughPutPoor = new ArrayList<Float>();
				         }
				         rssPoor.add(rssValue);
				         rssMap.put("Poor", rssPoor);
				         throughPutPoor.add(throughput);
				         throughputMap.put("Poor", throughPutPoor);
				        }
				        ////System.out.println("rssMap---before adding--------"+rssMap);
				        networkWiseMap.put("rss_"+deviceInfo.getNetworkType(), rssMap);
				        ////System.out.println("networkWiseMap------after adding------"+networkWiseMap);
				        networkWiseMap.put("throughput_"+deviceInfo.getNetworkType(), throughputMap);
				       }
				       //System.out.println("throughputMap------throughputMap------"+throughputMap);
				       //System.out.println("rssMap-------rssMap-----"+rssMap);
				      }
				      
				      for(int i=0;i<networkList.size();i++){
				       rssMap = networkWiseMap.get("rss_"+networkList.get(i));
				       throughputMap = networkWiseMap.get("throughput_"+networkList.get(i));
				       rssList.add(getAverageList(rssMap.get("Good")));
				       rssList.add(getAverageList(rssMap.get("Medium")));
				       rssList.add(getAverageList(rssMap.get("Poor")));
				       ftpThroughputList.add(getAverageList(throughputMap.get("Good")));
				       ftpThroughputList.add(getAverageList(throughputMap.get("Medium")));
				       ftpThroughputList.add(getAverageList(throughputMap.get("Poor")));
				      }
				      
				      //System.out.println("rsslist=============="+rssList);
				      dataMap.put("ftpThroughputList", ftpThroughputList);
				      dataMap.put("ftpRssList", rssList);
				      dataMap.put("Good", throughputMap.get("Good"));
				      dataMap.put("Poor", throughputMap.get("Poor"));
				      dataMap.put("Medium", throughputMap.get("Medium"));
				     } catch (Exception e) {
							e.printStackTrace();
						} /*finally {
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
						}*/
				     //System.out.println("dataMap---------"+dataMap);
				     return dataMap;
				    }			
			
			
			public List<DeviceInfoTO> getTestNameMultipleThroughputDetailsResultsForFTPLte(
					String testCaseName,String marketId) {
				Connection conn = null;
				Statement st = null;
				ResultSet rs = null;
				List<DeviceInfoTO> deviceInfosList = new ArrayList<DeviceInfoTO>();
				
				PreparedStatement pst = null;
				String test_name = testCaseName + "\\-%";
				String throughputTX="";
			    String throughputRX="";
			    String throughputMain="";
			    String currTxBytes="";
			    String prevTxBytes="";
			    String currRxBytes="";
			    String prevRxBytes="";
				int i=0;
				double txvalue=0;
				double rxvalue=0;
				double mainValue=0;
				String test_type="FTP";
				try {
					conn = DataConnectivityDaoImpl.conn;
					st = DataConnectivityDaoImpl.st;
					String query = "SELECT sdi.SIGNALSTRENGTH_GSMSIGNALSTRENGTH,sdi.SIGNALSTRENGTH_CDMADBM,sdi.SIGNALSTRENGTH_EVDODBM,"
							+ " sdi.SIGNALSTRENGTH_LTESIGNALSTRENGTH,sdi.SIGNALSTRENGTH_LTERSRP,sdi.NETWORK_TYPE,slc.EVENT_VALUE,slc.EVENT_NAME,sdi.GEOLOCATION_LATITUDE, sdi.GEOLOCATION_LANGITUDE ,"
							+ " DATE_FORMAT(slc.TIME_STAMP, '%Y-%m-%d %H:%i:%S')  AS TIMESTAMP,sdi.CELLLOCATION_CID from stg_device_info sdi,stg_log_cat_info slc"
							+ " where sdi.TEST_NAME=slc.TEST_NAME and slc.TEST_NAME LIKE '"
							+ test_name
							+ "' and sdi.MARKET_ID='"
							+ marketId
							+ "'and slc.TEST_TYPE='"+test_type+"' and sdi.TEST_TYPE=slc.TEST_TYPE and slc.EVENT_NAME IN('Current TX bytes','Current RX bytes')"
							+ " and DATE_FORMAT(slc.TIME_STAMP, '%Y-%m-%d %H:%i:%S%')=DATE_FORMAT(sdi.TIME_STAMP_FOREACH_SAMPLE, '%Y-%m-%d %H:%i:%S%') AND sdi.NETWORK_TYPE LIKE 'LTE (4G)' "
							+ " GROUP BY DATE_FORMAT(slc.TIME_STAMP, '%Y-%m-%d %H:%i:%S') ";
					//System.out.println("query-============="+query);
					rs = st.executeQuery(query);//AND sdi.NETWORK_TYPE =''LTE 
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
						String networkType = rs.getString(6);
						throughputMain= new ReportDaoImpl().getThroughput( i, rs.getString(8), deviceInfos,
								  currTxBytes, THROUGHPUT, txvalue, throughputTX, prevTxBytes, throughputRX, currRxBytes
								 , rxvalue, prevRxBytes, mainValue, throughputMain);
						deviceInfos.setThroughputmain(throughputMain);
				
						deviceInfosList.add(deviceInfos);
						
						i++;
					}
					

				} catch (Exception e) {
					e.printStackTrace();
				} /*finally {
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
				}*/
				return deviceInfosList;
			}

			static HashMap<String,Integer> getFtpUlinkDownlink(HashMap<String,List<DeviceInfoTO>> dataMap){
				HashMap<String,Integer> calculatedMap = new HashMap<String, Integer>();
				//System.out.println("dataMap------------"+dataMap);
				Iterator<String> keysetItr = dataMap.keySet().iterator();
				//System.out.println("if keyset--++-"+keysetItr.hasNext());
				try{
					while(keysetItr.hasNext()){
						//System.out.println("inside key set");
						String network = keysetItr.next();
						List<DeviceInfoTO> deviceInfoList = dataMap.get(network);
						//System.out.println("deviceInfoList----------"+deviceInfoList.size());
						List<Integer> txList = new ArrayList<Integer>();
						List<Integer> rxList = new ArrayList<Integer>();
						boolean firstRx = true;
						boolean firstTx = true;
						for(int i=0;i<deviceInfoList.size();i++){
							DeviceInfoTO dto = deviceInfoList.get(i);
							if(i<deviceInfoList.size()-1){
								if((firstRx&&dto.getEventName().equalsIgnoreCase("Current RX bytes"))||
										(deviceInfoList.get(i+1).getEventName().equalsIgnoreCase("Current TX bytes")&&
												dto.getEventName().equalsIgnoreCase("Current RX bytes"))){
									if(firstRx&&dto.getEventName().equalsIgnoreCase("Current RX bytes")){
										firstRx = false;
									}
									if(deviceInfoList.get(i+1).getEventName().equalsIgnoreCase("Current TX bytes")&&
												dto.getEventName().equalsIgnoreCase("Current RX bytes")){
										firstRx = true;
									}
									
									rxList.add(new Integer(dto.getEventValue()));
								}
								
								
								if((firstTx&&dto.getEventName().equalsIgnoreCase("Current TX bytes"))||
										(deviceInfoList.get(i+1).getEventName().equalsIgnoreCase("Current RX bytes")&&
												dto.getEventName().equalsIgnoreCase("Current TX bytes"))){
									if(firstTx&&dto.getEventName().equalsIgnoreCase("Current TX bytes")){
										firstTx = false;
									}
									if(deviceInfoList.get(i+1).getEventName().equalsIgnoreCase("Current RX bytes")&&
												dto.getEventName().equalsIgnoreCase("Current TX bytes")){
										firstTx = true;
									}
									
									txList.add(new Integer(dto.getEventValue()));
								}
							}
						
						}
						
						//System.out.println("txList---------"+txList);
						calculatedMap.put("rx_"+network, avgThroughputFtp(rxList));
						calculatedMap.put("tx_"+network, avgThroughputFtp(txList));
						
					}
					//System.out.println("calculatedMap-----------"+calculatedMap);
				}
				catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				
				
				return calculatedMap;
			}
			
			static Integer avgThroughputFtp (List<Integer> valueList){
				int avgThroughput = 0;
				List<Integer> tempList = new ArrayList<Integer>();
				//System.out.println("valueList=========="+valueList);
				HashMap<String,Integer> networkWiseVal = new HashMap<String, Integer>();
				for(int i=0;i<valueList.size();i++){
					if(i<valueList.size()-1){
						tempList.add(valueList.get(i+1)-valueList.get(i));
						i++;
					}
					
				}
				
				return listAvg(tempList);
			}
			
			static Integer listAvg(List<Integer> avgList){
				int avgValue = 0;
				int tempvalue = 0;
				if(avgList.size()>0){
				for(int i=0;i<avgList.size();i++){
					tempvalue = avgList.get(i)+tempvalue;
				}
				avgValue = tempvalue/avgList.size();
				}
				//System.out.println("avgValue-----------"+avgValue);
				return avgValue;
			}
			
			public String getsignalQuality(Float rssiValue) {

				String quality = "";
				if(null!=rssiValue){
					if (rssiValue < -45 && rssiValue > -70) {
						quality = ReportConstants.GOOD;
					} else if (rssiValue < -71 && rssiValue > -89) {
						quality = ReportConstants.MEDIUM;
					} else if (rssiValue < -91) {
						quality = ReportConstants.POOR;
					}
				}
			
				/*//System.out.println("rssiValue---------" + rssiValue
						+ "-----quality-------" + quality);*/
				return quality;
			}
			/*public static void main(String[] args) {
				File folder = new File("D:\\Airometric builds\\externalfiles\\New folder\\New folder\\renamed\\");
		        File[] listOfFiles = folder.listFiles();

		        for (int i = 0; i < listOfFiles.length; i++) {

		            if (listOfFiles[i].isFile()) {

		                File f = new File("D:\\Airometric builds\\externalfiles\\New folder\\New folder\\renamed\\"+listOfFiles[i].getName()); 
		                //System.out.println(listOfFiles[i].getName());
		                f.renameTo(new File("D:\\Airometric builds\\externalfiles\\New folder\\New folder\\renamed\\"+listOfFiles[i].getName().substring(23,listOfFiles[i].getName().length())+""));
		            }
		        }

		        //System.out.println("conversion is done");
				
				String hello = "31-01-14_1391110095335_deviceinfo_g2_1391109956265";
				//System.out.println(hello.substring(23,hello.length()));
				HashMap<String, String> testMap = new  HashMap<String, String>();
				HashMap<String, String> clonetestMap = (HashMap<String, String>)testMap.clone();
				
				testMap.put("test", "test");
				//System.out.println(testMap);
				//System.out.println(clonetestMap);
				
				
				//System.out.println("hello");
//				new DataConnectivityDaoImpl().getDataForFTP("g2","22","");
		        
		    }*/
///////////////////////////////////////////////////////
			
			public static void main(String[] args) {
				new DataConnectivityDaoImpl().populateDataForFTP("","sonimref1", "31","rx");
			}
					
			
}
