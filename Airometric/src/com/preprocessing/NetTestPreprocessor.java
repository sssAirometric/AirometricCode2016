package com.preprocessing;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.model.DBUtil;
import com.report.to.NetTestTo;
import com.report.to.VoiceQualityTo;
import com.to.DeviceInfoTO;

public class NetTestPreprocessor {

	/*public static void populateTcpLevel1Table(String testName, String marketId) {
		List<DeviceInfoTO> deviceInfoList = PreprocessorMiscHelper
				.populateDataForFTP(testName, marketId, "externaltest");
		List<NetTestTo> tcpList = getNetTestForTestType(testName, marketId, "tcp");
		insertIntoTcpLevel1(mergedList(deviceInfoList, tcpList));
	}

	public static void populateUdpLevel1Table(String testName, String marketId) {
		List<DeviceInfoTO> deviceInfoList = PreprocessorMiscHelper
				.populateDataForFTP(testName, marketId, "externaltest");
		List<NetTestTo> udpList = getNetTestForTestType(testName, marketId, "udp");
		insertIntoUdpLevel1(mergedList(deviceInfoList, udpList));
	}*/
	//Comment by ankit on 11/04/16
	public static void populateTcpLevel1Table(String testName, String marketId,String TestType) {
		List<DeviceInfoTO> deviceInfoList = PreprocessorMiscHelper
				.populateDataForFTP(testName, marketId, TestType);
		List<NetTestTo> tcpList = getNetTestForTestType(testName, marketId, "tcp");
		insertIntoTcpLevel1(mergedList(deviceInfoList, tcpList));
	}
	public static void populateUdpLevel1Table(String testName, String marketId,String TestType) {
		List<DeviceInfoTO> deviceInfoList = PreprocessorMiscHelper
				.populateDataForFTP(testName, marketId, TestType);
		List<NetTestTo> udpList = getNetTestForTestType(testName, marketId, "udp");
		insertIntoUdpLevel1(mergedList(deviceInfoList, udpList));
	}
	public static List<NetTestTo> getNetTestForTestType(String testName,
			String marketId, String testType) {
		Statement st = null;
		ResultSet rs=null;
		List<NetTestTo> nettestList = new ArrayList<NetTestTo>();
		String query = "SELECT * FROM stg_net_results WHERE TEST_NAME LIKE '"
				+ testName + "' AND MARKET_ID='" + marketId
				+ "' AND NetSpeedTest='" + testType
				+ "' ORDER BY CallTimeStamp";
		//System.outprintln(query);
		Connection conn = DBUtil.openConn();
		try {
			st = conn.createStatement();
			rs = st.executeQuery(query);
			if(testType.equalsIgnoreCase("tcp")){
				nettestList = getAllTcpData(rs);
			}
			else if(testType.equalsIgnoreCase("udp")){
				nettestList = getAllUdpData(rs);
			}
		
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			try {
				rs.close();
				st.close();
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return nettestList;
	}
	
	private static List<NetTestTo> getAllTcpData(ResultSet rs){
		List<NetTestTo> tcpList = new ArrayList<NetTestTo>();
		try{
			while (rs.next()) {
				NetTestTo tcpTo = new NetTestTo();
				tcpTo.setTimeStamp(rs.getString("CallTimeStamp"));
				tcpTo.setImeiNumber(rs.getString("VQuadPhoneId"));
				tcpTo.setTcp_Download_Average_Delay(rs
						.getString("TCPDownloadAverageDelay"));
				tcpTo.setTcp_Download_Bytes_Retransmitted(rs
						.getString("TCPDownloadBytesRetransmitted"));
				tcpTo.setTcp_Download_Duplicate_Acks(rs
						.getString("TCPDownloadDuplicateAcks"));
				tcpTo.setTcp_Download_Effective_Speed(rs
						.getString("TCPDownloadEffectiveSpeed"));
				tcpTo.setTcp_Download_QoS(rs.getString("TCPDownloadQOS"));
				tcpTo.setTcp_Round_Trip_Time(rs.getString("TCPRoundTripTime"));
				tcpTo.setTcp_Upload_Average_Delay(rs
						.getString("TCPUploadAverageDelay"));
				tcpTo.setTcp_Upload_Bytes_Retransmitted(rs
						.getString("TCPUploadBytesLost"));
				tcpTo.setTcp_Upload_Effective_Speed(rs
						.getString("TCPUploadEffectiveSpeed"));
				tcpTo.setTcp_Upload_QoS(rs.getString("TCPUploadQOS"));
				tcpList.add(tcpTo);
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return tcpList;
	}
	
	
	private static List<NetTestTo> getAllUdpData(ResultSet rs){
		List<NetTestTo> udpList = new ArrayList<NetTestTo>();
		try{
			while (rs.next()) {
				NetTestTo udpTo = new NetTestTo();
				udpTo.setTimeStamp(rs.getString("CallTimeStamp"));
				udpTo.setImeiNumber(rs.getString("VQuadPhoneId"));
				udpTo.setUDPDownloadCapacity(rs
						.getString("UDPDownloadCapacity"));
				udpTo.setUDPDownloadKiloPacketsPerSec(rs
						.getString("UDPDownloadKiloPacketsPerSec"));
				udpTo.setUDPDownloadPacketSize(rs
						.getString("UDPDownloadPacketSize"));
				udpTo.setUDPDownloadQOS(rs
						.getString("UDPDownloadQOS"));
				udpTo.setUDPUploadCapacity(rs
						.getString("UDPUploadCapacity"));
				udpTo.setUDPUploadKiloPacketsPerSec(rs
						.getString("UDPUploadKiloPacketsPerSec"));
				udpTo.setUDPUploadPacketSize(rs
						.getString("UDPUploadPacketSize"));
				udpTo.setUDPUploadQOS(rs
						.getString("UDPUploadQOS"));
				udpList.add(udpTo);
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return udpList;
	}
	private static void insertIntoTcpLevel1(List<NetTestTo> mergedList) {
		Connection conn = DBUtil.getConnection();
		try {
			Statement stmt = conn.createStatement();

			for (NetTestTo tcpTo : mergedList) {
				String query = "INSERT INTO pre_calculation_tcp_level1 VALUES ('"
						+ tcpTo.getTcp_Download_Effective_Speed()
						+ "', '"
						+ tcpTo.getTcp_Download_QoS()
						+ "', '"
						+ tcpTo.getTcp_Download_Average_Delay()
						+ "', '"
						+ tcpTo.getTcp_Download_Bytes_Retransmitted()
						+ "', '"
						+ tcpTo.getTcp_Download_Duplicate_Acks()
						+ "', '"
						+ tcpTo.getTcp_Upload_Effective_Speed()
						+ "', '"
						+ tcpTo.getTcp_Upload_QoS()
						+ "', '"
						+ tcpTo.getTcp_Upload_Average_Delay()
						+ "', '"
						+ tcpTo.getTcp_Upload_Bytes_Retransmitted()
						+ "', '"
						+ tcpTo.getTcp_Round_Trip_Time()
						+ "','"
						+ tcpTo.getDeviceInfoTO().getImei()
						+ "','"
						+ tcpTo.getDeviceInfoTO().getTestType()
						+ "','"
						+ tcpTo.getDeviceInfoTO().getUserName()
						+ "',"
						+ "'"
						+ tcpTo.getDeviceInfoTO().getPhoneNumber()
						+ "','"
						+ tcpTo.getDeviceInfoTO().getPhoneType()
						+ "','"
						+ tcpTo.getDeviceInfoTO().getDeviceName()
						+ "','"
						+ tcpTo.getDeviceInfoTO().getDeviceManufacturer()
						+ "',"
						+ "'"
						+ tcpTo.getDeviceInfoTO().getDeviceVersion()
						+ "','"
						+ tcpTo.getDeviceInfoTO().getTimeStampForEachSample()
						+ "','"
						+ tcpTo.getDeviceInfoTO().getNetworkOperator()
						+ "','"
						+ tcpTo.getDeviceInfoTO().getNetworkOperatorName()
						+ "',"
						+ "'"
						+ tcpTo.getDeviceInfoTO().getNetworkType()
						+ "','"
						+ tcpTo.getDeviceInfoTO().getDataState()
						+ "','"
						+ tcpTo.getDeviceInfoTO().getDataActivity()
						+ "','"
						+ tcpTo.getDeviceInfoTO().getWifiState()
						+ "',"
						+ "'"
						+ tcpTo.getDeviceInfoTO().getNetworkRoaming()
						+ "','"
						+ tcpTo.getDeviceInfoTO().getNetworkMCC()
						+ "','"
						+ tcpTo.getDeviceInfoTO().getNetworkMNC()
						+ "','"
						+ tcpTo.getDeviceInfoTO().getSignalStrengthCDMA()
						+ "',"
						+ "'"
						+ tcpTo.getDeviceInfoTO().getSignalStrengthCDMACIO()
						+ "','"
						+ tcpTo.getDeviceInfoTO().getSignalStrengthEVDO()
						+ "','"
						+ tcpTo.getDeviceInfoTO().getSignalStrengthEVDOECIO()
						+ "','"
						+ tcpTo.getDeviceInfoTO().getSignalStrength_EVDOSNR()
						+ "',"
						+ "'"
						+ tcpTo.getDeviceInfoTO().getSignalStrengthGSM()
						+ "','"
						+ tcpTo.getDeviceInfoTO()
								.getSignalStrengthGSMBITRATEERROR()
						+ "','"
						+ tcpTo.getDeviceInfoTO().getSignalStrengthLTE()
						+ "','"
						+ tcpTo.getDeviceInfoTO().getSignalStrengthLTERSRP()
						+ "',"
						+ "'"
						+ tcpTo.getDeviceInfoTO().getSignalStrengthLTERSRQ()
						+ "','"
						+ tcpTo.getDeviceInfoTO().getSignalStrengthLTERSSNR()
						+ "','"
						+ tcpTo.getDeviceInfoTO().getSignalStrength_LTECQI()
						+ "','"
						+ tcpTo.getDeviceInfoTO().getCellLocationCID()
						+ "',"
						+ "'"
						+ tcpTo.getDeviceInfoTO().getCellLocationLAC()
						+ "','"
						+ tcpTo.getDeviceInfoTO().getNeighbourInfo()
						+ "','"
						+ tcpTo.getDeviceInfoTO().getBatteryLevel()
						+ "','"
						+ tcpTo.getDeviceInfoTO().getNetworkManuallyDone()
						+ "',"
						+ "'"
						+ tcpTo.getDeviceInfoTO().getLattitude()
						+ "','"
						+ tcpTo.getDeviceInfoTO().getLongitude()
						+ "','"
						+ tcpTo.getDeviceInfoTO().getSnapShotId()
						+ "','"
						+ tcpTo.getDeviceInfoTO().getMarketId()
						+ "','"
						+ tcpTo.getDeviceInfoTO().getSignalStrength()
						+ "','"
						+ tcpTo.getDeviceInfoTO().getTestName() + "','')";
//				System.out.println("check by ankit>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+query);
//				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>L>>>>>>>>>>>>>>>>>>>>"+tcpTo.getDeviceInfoTO().getNeighbourInfo().length());
				stmt.executeUpdate(query);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	private static void insertIntoUdpLevel1(List<NetTestTo> mergedList) {
		Connection conn = DBUtil.getConnection();
		try {
			Statement stmt = conn.createStatement();

			for (NetTestTo udpTo : mergedList) {
				String query = "INSERT INTO pre_calculation_udp_level1 VALUES ('"
						+ udpTo.getUDPDownloadCapacity()
						+ "', '"
						+ udpTo.getUDPDownloadQOS()
						+ "', '"
						+ udpTo.getUDPDownloadPacketSize()
						+ "', '"
						+ udpTo.getUDPDownloadKiloPacketsPerSec()
						+ "', '"
						+ udpTo.getUDPUploadCapacity()
						+ "', '"
						+ udpTo.getUDPUploadQOS()
						+ "', '"
						+ udpTo.getUDPUploadPacketSize()
						+ "', '"
						+ udpTo.getUDPUploadKiloPacketsPerSec()
						+ "','"
						+ udpTo.getDeviceInfoTO().getImei()
						+ "','"
						+ udpTo.getDeviceInfoTO().getTestType()
						+ "','"
						+ udpTo.getDeviceInfoTO().getUserName()
						+ "',"
						+ "'"
						+ udpTo.getDeviceInfoTO().getPhoneNumber()
						+ "','"
						+ udpTo.getDeviceInfoTO().getPhoneType()
						+ "','"
						+ udpTo.getDeviceInfoTO().getDeviceName()
						+ "','"
						+ udpTo.getDeviceInfoTO().getDeviceManufacturer()
						+ "',"
						+ "'"
						+ udpTo.getDeviceInfoTO().getDeviceVersion()
						+ "','"
						+ udpTo.getDeviceInfoTO().getTimeStampForEachSample()
						+ "','"
						+ udpTo.getDeviceInfoTO().getNetworkOperator()
						+ "','"
						+ udpTo.getDeviceInfoTO().getNetworkOperatorName()
						+ "',"
						+ "'"
						+ udpTo.getDeviceInfoTO().getNetworkType()
						+ "','"
						+ udpTo.getDeviceInfoTO().getDataState()
						+ "','"
						+ udpTo.getDeviceInfoTO().getDataActivity()
						+ "','"
						+ udpTo.getDeviceInfoTO().getWifiState()
						+ "',"
						+ "'"
						+ udpTo.getDeviceInfoTO().getNetworkRoaming()
						+ "','"
						+ udpTo.getDeviceInfoTO().getNetworkMCC()
						+ "','"
						+ udpTo.getDeviceInfoTO().getNetworkMNC()
						+ "','"
						+ udpTo.getDeviceInfoTO().getSignalStrengthCDMA()
						+ "',"
						+ "'"
						+ udpTo.getDeviceInfoTO().getSignalStrengthCDMACIO()
						+ "','"
						+ udpTo.getDeviceInfoTO().getSignalStrengthEVDO()
						+ "','"
						+ udpTo.getDeviceInfoTO().getSignalStrengthEVDOECIO()
						+ "','"
						+ udpTo.getDeviceInfoTO().getSignalStrength_EVDOSNR()
						+ "',"
						+ "'"
						+ udpTo.getDeviceInfoTO().getSignalStrengthGSM()
						+ "','"
						+ udpTo.getDeviceInfoTO()
								.getSignalStrengthGSMBITRATEERROR()
						+ "','"
						+ udpTo.getDeviceInfoTO().getSignalStrengthLTE()
						+ "','"
						+ udpTo.getDeviceInfoTO().getSignalStrengthLTERSRP()
						+ "',"
						+ "'"
						+ udpTo.getDeviceInfoTO().getSignalStrengthLTERSRQ()
						+ "','"
						+ udpTo.getDeviceInfoTO().getSignalStrengthLTERSSNR()
						+ "','"
						+ udpTo.getDeviceInfoTO().getSignalStrength_LTECQI()
						+ "','"
						+ udpTo.getDeviceInfoTO().getCellLocationCID()
						+ "',"
						+ "'"
						+ udpTo.getDeviceInfoTO().getCellLocationLAC()
						+ "','"
						+ udpTo.getDeviceInfoTO().getNeighbourInfo()
						+ "','"
						+ udpTo.getDeviceInfoTO().getBatteryLevel()
						+ "','"
						+ udpTo.getDeviceInfoTO().getNetworkManuallyDone()
						+ "',"
						+ "'"
						+ udpTo.getDeviceInfoTO().getLattitude()
						+ "','"
						+ udpTo.getDeviceInfoTO().getLongitude()
						+ "','"
						+ udpTo.getDeviceInfoTO().getSnapShotId()
						+ "','"
						+ udpTo.getDeviceInfoTO().getMarketId()
						+ "','"
						+ udpTo.getDeviceInfoTO().getSignalStrength()
						+ "','"
						+ udpTo.getDeviceInfoTO().getTestName() + "','')";
//				System.out.println(query);
				stmt.executeUpdate(query);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	private static List<NetTestTo> mergedList(List<DeviceInfoTO> deviceInfoList,
			List<NetTestTo> tcpList) {
		List<NetTestTo> mergedList = new ArrayList<NetTestTo>();
		int foundIndex = 0;

		for (int i = 0; i < deviceInfoList.size(); i++) {
			DeviceInfoTO deviceInfoTo = deviceInfoList.get(i);
			boolean isTimestampsClose = false;
			for (int j = foundIndex; j < tcpList.size(); j++) {
				
				NetTestTo tcpTo = tcpList.get(j);
//				if(tcpTo.getImeiNumber().equalsIgnoreCase(deviceInfoTo.getImei())){
					isTimestampsClose = PreprocessorMiscHelper.closeCallTimestamp(
							 deviceInfoTo.getTimeStampForEachSample(),tcpTo.getTimeStamp(),
							 "yyyy-MM-dd HH:mm:ss.SSS","MM/dd/yyyy HH:mm:ss",false);//dd/MM/yyyy HH:mm:ss
//					System.out.println("TCPLIST--------tcpTo.getTimeStamp() -"+tcpTo.getTimeStamp()+"-i-"+i);
					if(i==deviceInfoList.size()-1){
						System.out.println("deviceInfoTo--------last record");
//										.getTimeStampForEachSample());
						isTimestampsClose = PreprocessorMiscHelper.closeCallTimestamp(
							deviceInfoTo.getTimeStampForEachSample(),tcpTo.getTimeStamp(),
								 "yyyy-MM-dd HH:mm:ss.SSS","MM/dd/yyyy HH:mm:ss",true);
					}
					 System.out.println("isTimestampsClose-TRUE--"+isTimestampsClose);
					if (isTimestampsClose) {
						System.out.println("MergedList--------MergedList-"+"-i-"+i);
						tcpTo.setDeviceInfoTO(deviceInfoTo);
						mergedList.add(tcpTo);
						// foundIndex = j+1;
						foundIndex = j+1;
						break;
					}
//				}
			}
			if (!isTimestampsClose) {
				NetTestTo tcpTo = new NetTestTo();
				tcpTo.setDeviceInfoTO(deviceInfoTo);
				mergedList.add(tcpTo);
			}
		}
		System.out.println("check by ankit mergedList-size--"+mergedList.size());
		return mergedList;
	}

	public static void main(String[] args) {
//		populateUdpLevel1Table("Test90Ext", "45");
		populateUdpLevel1Table("htcnettest428", "29","");
	}
}
