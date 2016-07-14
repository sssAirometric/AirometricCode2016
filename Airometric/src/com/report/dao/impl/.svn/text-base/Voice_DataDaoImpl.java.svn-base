package com.report.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.report.dao.Voice_DataDao;
import com.to.Voice_DataTO;

public class Voice_DataDaoImpl implements Voice_DataDao{

	public HashMap<String, List<Integer>> getDataForChart() {
		return null;
	}

	public List<Voice_DataTO> getAllDeviceInfo(String testName,
			String marketId, String testType,List<String>timestamp) {
		List<Voice_DataTO> deviceInfosList = new ArrayList<Voice_DataTO>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			conn = DataConnectivityDaoImpl.conn;
			st = conn.createStatement();
			String deviceInfoQuery = "SELECT  * FROM STG_DEVICE_INFO WHERE TEST_NAME ='"+testName+"' " +
					" AND TEST_TYPE='"+testType+"' AND MARKET_ID IN("+marketId+")  " +
					" AND TIME_STAMP_FOREACH_SAMPLE BETWEEN '"+timestamp.get(0)+"'  AND '"+timestamp.get(1)+"' " +
					" ORDER BY TIME_STAMP_FOREACH_SAMPLE";
			//System.out.println("deviceInfoQuery-----srikanth--------"+deviceInfoQuery);
			rs = st.executeQuery(deviceInfoQuery);
			while (rs.next()) {
				Voice_DataTO deviceInfos=new Voice_DataTO();
				deviceInfos.setTestName(rs.getString("TEST_NAME"));
				deviceInfos.setNetworkType(rs.getString("NETWORK_NETWORKTYPE"));
				deviceInfos.setNetworkDataState(rs.getString("NETWORK_DATASTATE"));
				deviceInfos.setNetworkRoaming(rs.getString("NETWORK_ROAMING"));
				deviceInfos.setSignalStrength(rs.getString("SIGNALSTRENGTH_GSMSIGNALSTRENGTH"));
				deviceInfos.setSignalStrengthCDMA(rs.getString("SIGNALSTRENGTH_CDMADBM"));
				deviceInfos.setSignalStrengthEVDO(rs.getString("SIGNALSTRENGTH_EVDODBM"));
				deviceInfos.setLattitude(rs.getDouble("GEOLOCATION_LATITUDE"));
				deviceInfos.setLongitude(rs.getDouble("GEOLOCATION_LANGITUDE"));
				deviceInfos.setCellLocationCID(rs.getString("CELLLOCATION_CID"));
				deviceInfos.setCellLocationLAC(rs.getString("CELLLOCATION_LAC"));
				deviceInfos.setDevicePhoneType(rs.getString("DEVICE_PHONETYPE"));
				deviceInfos.setNetworkMCC(rs.getString("NETWORK_MCC"));
				deviceInfos.setNetworkMNC(rs.getString("NETWORK_MNC"));
				deviceInfos.setSignalStrengthSnr(rs.getString("SIGNALSTRENGTH_EVDOSNR"));
				deviceInfos.setTimeStampForEachSample(rs.getString("TIME_STAMP_FOREACH_SAMPLE"));
				deviceInfos.setNeighbourInfo(rs.getString("NEIGHBOUR_INFO"));
				deviceInfos.setSignalStrengthCDMACIO(rs.getString("SIGNALSTRENGTH_CDMACIO"));
				deviceInfos.setSignalStrengthEVDOECIO(rs.getString("SIGNALSTRENGTH_EVDOECIO"));
				deviceInfos.setSignalStrengthLTE(rs.getString("SIGNALSTRENGTH_LTESIGNALSTRENGTH"));
				deviceInfos.setSignalStrengthLTERSRP(rs.getString("SIGNALSTRENGTH_LTERSRP"));
				deviceInfos.setSignalStrengthLTERSRQ(rs.getString("SIGNALSTRENGTH_LTERSRQ"));
				deviceInfos.setSignalStrengthLTERSSNR(rs.getString("SIGNALSTRENGTH_LTERSSNR"));
				deviceInfos.setSignalStrengthLTECQI(rs.getString("SIGNALSTRENGTH_LTECQI"));
				deviceInfosList.add(deviceInfos);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} /*finally {
			try {
				rs.close();
				st.close();
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
		return deviceInfosList;
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
			e.printStackTrace();
		}
		return neighbourtStr;
	
	}

	public String convertDate(String date){
        SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS aa");
        SimpleDateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String time24=null;;
		try {
			time24 = outFormat.format(inFormat.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return time24;
	}
	
	public List<String> getExternal_CallDropList(String testName,
			String marketId) {
		List<String>througputCallDropList=new ArrayList<String>();
		SimpleDateFormat newSdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	/*	String query = "SELECT  ST.CallTimeStamp" +
				" from stg_net_results ST,stg_callevent_results SD WHERE ST.TEST_NAME='"+testName+"' " +
				" AND ST.MARKET_ID IN("+marketId+") AND ST.TEST_NAME=SD.TEST_NAME AND SD.Call_Control_Event='BT Connection Drop' " +
				" AND ST.MARKET_ID=SD.MARKET_ID AND ST.CallTimeStamp=SD.Call_Timestamp " +
				" AND ST.VQuadPhoneId=SD.VQuad_PhoneId GROUP BY ST.CallTimeStamp ";
		*/
		String query="SELECT  SD.Call_Timestamp " +
				"from stg_callevent_results SD WHERE SD.TEST_NAME='"+testName+"' " +
				"AND SD.MARKET_ID IN("+marketId+") AND SD.Call_Control_Event='BT Connection Drop'  GROUP BY SD.Call_Timestamp ";
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			conn = DataConnectivityDaoImpl.conn;
			st = conn.createStatement();
			rs = st.executeQuery(query);
			while (rs.next()) {
				througputCallDropList.add(format.format(newSdf.parse(rs.getString(1))));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*finally{
			try{
				conn.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}*/
		return througputCallDropList;
	}
	
	public List<String> getExternal_througputList(String testName,
			String marketId) {
		List<String>througputList=new ArrayList<String>();
		String query = "SELECT  ST.TCPDownloadSpeed AS Throughput " +
				" from stg_net_results ST,stg_callevent_results SD WHERE ST.TEST_NAME='"+testName+"' " +
				"AND ST.MARKET_ID IN("+marketId+") AND ST.TEST_NAME=SD.TEST_NAME AND ST.MARKET_ID=SD.MARKET_ID AND ST.TCPDownloadSpeed!='Empty'  " +
				" AND ST.CallTimeStamp=SD.Call_Timestamp AND ST.DEVICE_MODEL=SD.DEVICE_MODEL GROUP BY ST.CallTimeStamp";
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		//System.out.println("query--------throughput Value------srikanth-----"+query);
		try {
			conn = DataConnectivityDaoImpl.conn;
			st = conn.createStatement();
			rs = st.executeQuery(query);
			while (rs.next()) {
				througputList.add(rs.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*finally{
			try{
				conn.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}*/
		return througputList;
	}

	public List<String> getExternal_througput(String testName,
			String marketId) {
		List<String>througputCallTimestamp=new ArrayList<String>();
		SimpleDateFormat newSdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String query = "SELECT  ST.CallTimeStamp AS Timestamp " +
				" from stg_net_results ST,stg_callevent_results SD WHERE ST.TEST_NAME='"+testName+"' " +
				"AND ST.MARKET_ID IN("+marketId+") AND ST.TEST_NAME=SD.TEST_NAME AND ST.MARKET_ID=SD.MARKET_ID AND ST.TCPDownloadSpeed!='Empty'  " +
				" AND ST.CallTimeStamp=SD.Call_Timestamp AND ST.DEVICE_MODEL=SD.DEVICE_MODEL GROUP BY ST.CallTimeStamp";
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		//System.out.println("query-----getExternal_througput---kulkarni---"+query);
		try {
			conn = DataConnectivityDaoImpl.conn;
			st = conn.createStatement();
			rs = st.executeQuery(query);
			while (rs.next()) {
				througputCallTimestamp.add(format.format(newSdf.parse(rs.getString(1))));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*finally{
			try{
				conn.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}*/
		return througputCallTimestamp;
	}
  
	public List<Float> getVoice_DataKPI(String testName,String marketId) {
		List<String>througputCallDropList=new ArrayList<String>();
		List<String>TotalCallList=new ArrayList<String>();
		List<Float>FinalList=new ArrayList<Float>();
		SimpleDateFormat newSdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		PreparedStatement pst = null;
		try {
			conn = DataConnectivityDaoImpl.conn;
			st = conn.createStatement();
			String query="SELECT  SD.Call_Timestamp " +
						 "from stg_callevent_results SD WHERE SD.TEST_NAME='"+testName+"' " +
						 "AND SD.MARKET_ID IN("+marketId+") AND SD.Call_Control_Event='BT Connection Drop'  GROUP BY SD.Call_Timestamp ";
			rs = st.executeQuery(query);
			while (rs.next()) {
				througputCallDropList.add(convertDate(format.format(newSdf.parse(rs.getString(1)))+" PM"));
			}
			String sql="SELECT  SD.Call_Timestamp " +
						"from stg_callevent_results SD WHERE SD.TEST_NAME='"+testName+"' " +
						"AND SD.MARKET_ID IN("+marketId+")  GROUP BY SD.Call_Timestamp ";
			rs1 = st.executeQuery(sql);
			while (rs1.next()) {
				TotalCallList.add(convertDate(format.format(newSdf.parse(rs1.getString(1)))+" PM"));
			}
			FinalList.add(0,new Float(TotalCallList.size()));
			FinalList.add(1,new Float(througputCallDropList.size()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*finally{
			try{
				conn.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}*/
		return FinalList;
	}
	public static void main(String[] args) {
		//System.out.println("data------------------"+new Voice_DataDaoImpl().getVoice_DataKPI("g2", "22"));
	}
}
