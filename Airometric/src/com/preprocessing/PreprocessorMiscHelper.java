package com.preprocessing;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.PropertyFileReader;
import com.model.DBUtil;
import com.to.DeviceInfoTO;

public class PreprocessorMiscHelper {

	static HashMap<String, String> propertiesFiledata = PropertyFileReader
			.getProperties();
	private final static String SIGNALSTRENGTH_GSM = propertiesFiledata
			.get("SIGNALSTRENGTH_GSM");
	private final static String SIGNALSTRENGTH_GSM1 = propertiesFiledata
			.get("SIGNALSTRENGTH_GSM1");
	// private final static String SIGNALSTRENGTH_LTE =
	// propertiesFiledata.get("SIGNALSTRENGTH_LTE");
	private final static String THROUGHPUT = propertiesFiledata
			.get("THROUGHPUT");
	private final static String ServerPort = propertiesFiledata
			.get("ServerPort");
	private final static String QXDM_FILE_PATH = propertiesFiledata
			.get("QXDM_FILE_PATH");
	private final static String ENVIRONMENT = propertiesFiledata
			.get("ENVIRONMENT");

	
	public static boolean closeCallTimestamp(String starttime, String Endtime) {
		boolean status = false;
		Date d1 = null;
		Date d2 = null;
		int seconds = 0;
		try {
			SimpleDateFormat initialFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			SimpleDateFormat resultFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			String tempStartDate = resultFormat.format(initialFormat
					.parse(starttime));

			d1 = resultFormat.parse(tempStartDate);
			d2 = resultFormat.parse(Endtime);

			long diff = d1.getTime() - d2.getTime();
			long diffSeconds = diff / 1000 % 60;
			long diffMinutes = diff / (60 * 1000) % 60;
			long diffHours = diff / (60 * 60 *  1000) % 24;
			long diffDays = diff / (24 * 60 * 60 * 1000);
			seconds = Math.abs((int) diffSeconds + (int) (60 * diffMinutes)+(int)(diffHours*60*60));
			//seconds = Math.abs((int) diffSeconds + (int) (60 * diffMinutes));
			// d1 = format.parse(starttime);
			// d2 = format.parse(Endtime);
			// long diff = (d1.getTime() - d2.getTime()) / 1000;
			// seconds = (int) diff;
			String temp = String.valueOf(seconds).replaceAll("-", "");
			seconds = Integer.parseInt(temp);
			if (seconds >= 1 && seconds <= 10) {
				status = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
//		System.out.println("status--------" + status);
		return status;
	}

	/* Below method is used for Net Test Pre processing flow logic */
	/*public static boolean closeCallTimestamp(String starttime, String Endtime,
			String firstDf, String secondDf, boolean isLastEntry) {
		boolean status = false;
		SimpleDateFormat format = new SimpleDateFormat(firstDf);
		SimpleDateFormat format2 = new SimpleDateFormat(secondDf);
		Date d1 = null;
		Date d2 = null;
		int seconds = 0;
		try {
			d1 = format.parse(starttime);
			d2 = format2.parse(Endtime);
			long diff = d1.getTime() - d2.getTime();
			long diffMilliSeconds = diff / 1000 % 60 % 60;
			long diffSeconds = diff / 1000 % 60;
			long diffMinutes = diff / (60 * 1000) % 60;
			long diffHours = diff / (60 * 60 * 1000) % 24;
			long diffDays = diff / (24 * 60 * 60 * 1000);
			seconds = Math.abs((int) diffSeconds + (int) (60 * diffMinutes));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (isLastEntry) {
			if (seconds >= 1 && seconds <= 1200) {
				status = true;
			}
		} else {
			if (seconds >= 1 && seconds <= 20) {
				status = true;
			}
		}

		//System.outprintln("status--------" + status);
		return status;
	}*/
	public static boolean closeCallTimestamp(String starttime, String Endtime,
			   String firstDf, String secondDf, boolean isLastEntry) {
			  boolean status = false;
			  SimpleDateFormat format = new SimpleDateFormat(firstDf);
			  SimpleDateFormat format2 = new SimpleDateFormat(secondDf);
			  Date d1 = null;
			  Date d2 = null;
			  int seconds = 0;
			  try {
			   d1 = format.parse(starttime);
			   d2 = format2.parse(Endtime);
			   System.out.println("starttime-"+starttime+",,,,,Endtime-"+Endtime);
			   long diff = d1.getTime() - d2.getTime();
			   long diffMilliSeconds = diff / 1000 % 60 % 60;
			   long diffSeconds = diff / 1000 % 60;
			   long diffMinutes = diff / (60 * 1000) % 60;
			   long diffHours = diff / (60 * 60 *  1000) % 24;
			   long diffDays = diff / (24 * 60 * 60 * 1000);
			  
			   
				System.out.println("d-h-m-s diff by ank - "+diffDays + " days, "+diffHours + " hours, "+diffMinutes + " minutes, "+diffSeconds + " seconds.");

			   seconds = Math.abs((int) diffSeconds + (int) (60 * diffMinutes)+(int)(diffHours*60*60));
			  } catch (Exception e) {
			   e.printStackTrace();
			  }
//=======================	commented by ankit with mathew at 1/12/2015
//=================== comment open by ankit with mathew			  
			  System.out.println("seconds diff..." + seconds);
			  if (isLastEntry) {
			   if (seconds >= 1 && seconds <= 1800) {
				   System.out.println("Inside islast entry ");
				   status = true;
			   }
			  } else {
			   if (seconds >= -5 && seconds <= 15) {
			    status = true;
			   }
//======================        
			   }
			  System.out.println("status--------" + status);
			  return status;
			 }

	public String getSignalStrength(DeviceInfoTO deviceInfo) {
		String signalStrrngth = deviceInfo.getSignalStrength();
		String networkName = deviceInfo.getNetworkType();
		if (networkName.matches("GSM")) {
			if (deviceInfo.getSignalStrength().equals("Empty")
					|| deviceInfo.getSignalStrength().equals("")) {
				int signalStrengthGSM = Integer.parseInt("0");
				int signalStrengthGSMValue = -Integer
						.parseInt(SIGNALSTRENGTH_GSM)
						+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
						* signalStrengthGSM;
				signalStrrngth = new Integer(signalStrengthGSMValue).toString();
			} else {
				int signalStrengthGSM = Integer.parseInt(deviceInfo
						.getSignalStrength());
				int signalStrengthGSMValue = -Integer
						.parseInt(SIGNALSTRENGTH_GSM)
						+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
						* signalStrengthGSM;
				signalStrrngth = new Integer(signalStrengthGSMValue).toString();
			}
		} else if (networkName.matches("CDMA")) {
			if (deviceInfo.getSignalStrengthCDMA().equals("Empty")
					|| deviceInfo.getSignalStrengthCDMA().equals("")) {
			} else {
				signalStrrngth = deviceInfo.getSignalStrengthCDMA();
			}
		} else if (networkName.matches("EVDO")) {
			if (deviceInfo.getSignalStrengthEVDO().equals("Empty")
					|| deviceInfo.getSignalStrengthEVDO().equals("")) {

			} else {
				signalStrrngth = deviceInfo.getSignalStrengthEVDO();
			}
		} else if (networkName.equalsIgnoreCase("LTE (4G)")) {
			if (deviceInfo.getSignalStrengthLTE().equals("Empty")
					|| deviceInfo.getSignalStrengthLTE().equals("")) {
				int signalStrengthLTE = Integer.parseInt("0");
				int signalStrengthLTEValue = signalStrengthLTE;
				int signalStrengthLTValue = signalStrengthLTE;
				// signalStrengthLt.add(String.valueOf(signalStrengthLTValue).substring(1,
				// String.valueOf(signalStrengthLTValue).length()));
			} else {
				int signalStrengthLTE = Integer.parseInt(deviceInfo
						.getSignalStrengthLTE());
				int signalStrengthLTEValue = signalStrengthLTE;
				signalStrrngth = new Integer(signalStrengthLTEValue).toString();
				int signalStrengthLTValue = signalStrengthLTE;
				// signalStrengthLt.add(String.valueOf(signalStrengthLTValue).substring(1,
				// String.valueOf(signalStrengthLTValue).length()));
			}
		} else {

			if (deviceInfo.getSignalStrength().equals("Empty")
					|| deviceInfo.getSignalStrength().equals("")) {
				int signalStrengthGSM = Integer.parseInt("0");
				int signalStrengthGSMValue = -Integer
						.parseInt(SIGNALSTRENGTH_GSM)
						+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
						* signalStrengthGSM;
				signalStrrngth = new Integer(signalStrengthGSMValue).toString();
			} else {
				int signalStrengthGSM = Integer.parseInt(deviceInfo
						.getSignalStrength());
				int signalStrengthGSMValue = -Integer
						.parseInt(SIGNALSTRENGTH_GSM)
						+ Integer.parseInt(SIGNALSTRENGTH_GSM1)
						* signalStrengthGSM;
				signalStrrngth = new Integer(signalStrengthGSMValue).toString();
			}

		}
		return signalStrrngth;
	}

	public static List<DeviceInfoTO> populateDataForFTP(String testName,
			String marketId, String testType) {
		ResultSet rs = null;
		Statement st = null;
		Float signalStrength = new Float(0);
		Connection conn = DBUtil.openConn();
		List<DeviceInfoTO> deviceInToList = new ArrayList<DeviceInfoTO>();
		try {
			String getDeviceInfoData = "SELECT * FROM STG_DEVICE_INFO WHERE (TEST_NAME LIKE '"
					+ testName
					+ "' OR TEST_NAME LIKE '"
					+ testName
					+ "-%' )AND TEST_TYPE='"
					+ testType
					+ "' AND MARKET_ID='"
					+ marketId
					+ "' "
					+ "AND TEST_TYPE='"
					+ testType
					+ "' AND SIGNALSTRENGTH_LTESIGNALSTRENGTH NOT LIKE 'Empty' ORDER BY TIME_STAMP_FOREACH_SAMPLE";
//			System.out.println("getDeviceInfoData---------" + getDeviceInfoData);

			try {
				st = conn.createStatement();
				rs = st.executeQuery(getDeviceInfoData);
				while (rs.next()) {
					String networkType = rs.getString("NETWORK_TYPE");
					int rsrp = 0;
					if (!rs.getString("SIGNALSTRENGTH_LTERSRP")
							.equalsIgnoreCase("Empty")) {
						rsrp = rs.getInt("SIGNALSTRENGTH_LTERSRP");
					}
					if (!(networkType.contains("LTE"))
							|| ((networkType.contains("LTE")) && rsrp < 1000)) {
						DeviceInfoTO deviceInfos = new DeviceInfoTO();
						deviceInfos.setMarketId(rs.getString("MARKET_ID"));
						deviceInfos.setTestName(testName);
						deviceInfos.setNetworkType(rs.getString("NETWORK_TYPE"));
						deviceInfos.setNetworkDataState(rs.getString("NETWORK_DATASTATE"));
						deviceInfos.setNetworkRoaming(rs.getString("NETWORK_ROAMING"));
						//changes on 10/12/15
						if(networkType.equalsIgnoreCase("NA") || networkType.equalsIgnoreCase("NA") 
								|| networkType.equalsIgnoreCase("UNKNOWN") || networkType.equalsIgnoreCase("UNKNOWN")) //Added by ankit
						{System.out.println("Inside NA & Unknown condition");
							deviceInfos.setNetworkType("UMTS (3G)");
						}
						
						if (networkType.equalsIgnoreCase("LTE")
								|| networkType.equalsIgnoreCase("LTE (4G)")) {
							/*Code correction */
//							signalStrength = -140+ (new Float(
//											new Float(rs.getString("SIGNALSTRENGTH_LTERSRP"))));
							signalStrength = new Float(rs.getString("SIGNALSTRENGTH_LTERSRP"));
							networkType = "LTE";
						} else 
						if(networkType.equalsIgnoreCase("wifi") || networkType.equalsIgnoreCase("WIFI")) //Added by ankit
						{
							signalStrength = new Float(rs.getString("WIFIINFO_RSSI"));
							////
						}
						else{
							//SIGNALSTRENGTH_LTESIGNALSTRENGTH
						signalStrength = -113 + 2* (new Float(new Float(rs.getString("SIGNALSTRENGTH_GSMSIGNALSTRENGTH"))));
						}
						
						/*//SIGNALSTRENGTH_LTESIGNALSTRENGTH
						 * if(networkType.contains("LTE")){
						 * deviceInfos.setSignalStrength
						 * (rs.getString("SIGNALSTRENGTH_LTERSRP")); } else{
						 * deviceInfos.setSignalStrength(rs
						 * .getString("SIGNALSTRENGTH_GSMSIGNALSTRENGTH")); }
						 */

						/*
						 * deviceInfos.setSignalStrength(rs
						 * .getString("SIGNALSTRENGTH_LTERSRP"));
						 */

						deviceInfos.setSignalStrength(new Float(signalStrength)
								.toString());
						deviceInfos.setImei(rs.getString("DEVICE_IMEI"));
						deviceInfos.setTestType(rs.getString("TEST_TYPE"));
						deviceInfos.setUserName(rs.getString("USER_NAME"));
						deviceInfos.setUserName(rs.getString("USER_NAME"));
						deviceInfos.setPhoneNumber(rs
								.getString("DEVICE_PHONENUMBER"));
						deviceInfos.setPhoneType(rs
								.getString("DEVICE_PHONETYPE"));
						deviceInfos.setDeviceName(rs.getString("DEVICE_MODEL"));
						deviceInfos.setDeviceVersion(rs
								.getString("DEVICE_VERSION"));
						
						deviceInfos.setTimeStampForEachSample(rs
								.getString("TIME_STAMP_FOREACH_SAMPLE"));
						
						deviceInfos.setNetworkOperator(rs
								.getString("NETWORK_NETWORKOPERATOR"));
						deviceInfos.setNetworkOperatorName(rs
								.getString("NETWORK_NETWORKOPERATORNAME"));
						deviceInfos.setDataState(rs
								.getString("NETWORK_DATASTATE"));
						deviceInfos.setDataActivity(rs
								.getString("NETWORK_DATAACTIVITY"));
						deviceInfos.setWifiState(rs
								.getString("NETWORK_WIFISTATE"));
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
						deviceInfos.setNeighbourInfo(rs
								.getString("NEIGHBOUR_INFO"));
						deviceInfos.setBatteryLevel(rs
								.getString("BATTERY_LEVEL"));
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
						
						//changes made by ankit
						deviceInfos.setDeviceManufacturer(rs
								.getString("WIFIINFO_RSSI"));
						
						deviceInToList.add(deviceInfos);

					}
//					System.out.println("deviceInToList-------"
//							+ deviceInToList.size());
				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
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
		return deviceInToList;
	}

	public static void main(String[] args) {
		// populateDataForFTP("a89ftp","22","ftp");

		closeCallTimestamp("04/08/2015 16:16:42", "2015-04-08 16:01:50.758");

	}
}
