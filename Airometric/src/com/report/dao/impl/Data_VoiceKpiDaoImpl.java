package com.report.dao.impl;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.report.dao.Data_VoiceKpiDao;
import com.to.DataConnectivityTo;
import com.util.DBHelper;

public class Data_VoiceKpiDaoImpl extends DBHelper implements Data_VoiceKpiDao {

	public HashMap<String, List<Float>> getVoice_DataData(String deviceName,
			String testName, String marketId) {
		// TODO Auto-generated method stub
		openConn();
		HashMap<String,List<Float>> dataMap = new HashMap<String, List<Float>>();
		List<Float> tcpDriveDownloadSpeedList = new ArrayList<Float>();
		List<Float> ressDriveList = new ArrayList<Float>();
		List<Float> wifitcpDriveDownloadSpeedList = new ArrayList<Float>();
		List<Float> wifiressDriveList = new ArrayList<Float>();
		
		/*
		 * 
		 * SELECT AVG.AVGTCP,AVG.AVGRSSI,AVG.QUALITY,S1.NetworkType FROM 
(select avg(snr.TCPDownloadSpeed) AS AVGTCP,avg(snr.RSSI) AS AVGRSSI,'Poor' AS QUALITY ,SNR.NetworkType from stg_net_results snr,(select DISTINCT Call_Timestamp 
from stg_callevent_results where  VQuad_PhoneID='LG-D801' and Call_Control_Event = 'MDNetTest' and TEST_NAME = 'g2' and MARKET_ID = '22') scer 
where scer.Call_Timestamp = snr.CallTimeStamp and snr.NetSpeedTest ='TCP' AND snr.RSSI < -80 AND snr.RSSI > -103) AVG RIGHT JOIN 
(SELECT distinct NetworkType,MARKET_ID from 
 STG_NET_RESULTS WHERE MARKET_ID ='22') S1 ON S1.NetworkType  = AVG.NetworkType
		 * 
		 */
		String query="SELECT AVG.AVGTCP,AVG.AVGRSSI,'Good' AS QUALITY,S1.NetworkType AS NetworkType FROM (select avg(snr.TCPDownloadSpeed) " +
				" AS AVGTCP,avg(snr.RSSI) AS AVGRSSI ,SNR.NetworkType from stg_net_results snr,(select DISTINCT Call_Timestamp " +
				" from stg_callevent_results where  DEVICE_MODEL='"+deviceName+"'and Call_Control_Event = 'MDNetTest' and TEST_NAME = '"+testName+"' " +
				" and MARKET_ID = '"+marketId+"') scer where scer.Call_Timestamp = snr.CallTimeStamp and snr.NetSpeedTest ='TCP' " +
				" AND snr.RSSI < -45 AND snr.RSSI > -70) AVG RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from " +
				" STG_NET_RESULTS WHERE MARKET_ID ='22') S1 ON S1.NetworkType  = AVG.NetworkType" +
								
				" union " +
				
				" SELECT AVG.AVGTCP,AVG.AVGRSSI,'Medium' AS QUALITY,S1.NetworkType AS NetworkType FROM (select avg(snr.TCPDownloadSpeed) " +
				" AS AVGTCP,avg(snr.RSSI) AS AVGRSSI ,SNR.NetworkType from stg_net_results snr,(select DISTINCT Call_Timestamp" +
				" from stg_callevent_results where  DEVICE_MODEL='"+deviceName+"'and Call_Control_Event = 'MDNetTest' and TEST_NAME = '"+testName+"' " +
				" and MARKET_ID = '"+marketId+"') scer where scer.Call_Timestamp = snr.CallTimeStamp and snr.NetSpeedTest ='TCP' " +
				" AND snr.RSSI < -71 AND snr.RSSI > -90) AVG RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from " +
				" STG_NET_RESULTS WHERE MARKET_ID ='22') S1 ON S1.NetworkType  = AVG.NetworkType" +
				
				" union " +
				
				"SELECT AVG.AVGTCP,AVG.AVGRSSI,'Poor' AS QUALITY,S1.NetworkType AS NetworkType FROM (select avg(snr.TCPDownloadSpeed) " +
				" AS AVGTCP,avg(snr.RSSI) AS AVGRSSI ,SNR.NetworkType from stg_net_results snr,(select DISTINCT Call_Timestamp" +
				" from stg_callevent_results where  DEVICE_MODEL='"+deviceName+"'and Call_Control_Event = 'MDNetTest' and TEST_NAME = '"+testName+"' " +
				" and MARKET_ID = '"+marketId+"') scer where scer.Call_Timestamp = snr.CallTimeStamp and snr.NetSpeedTest ='TCP' " +
				" AND snr.RSSI < -91 AND snr.RSSI > -103) AVG RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from " +
				" STG_NET_RESULTS WHERE MARKET_ID ='22') S1 ON S1.NetworkType  = AVG.NetworkType " +
				" Order by NetworkType, QUALITY";
		
		
		try {
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				DataConnectivityTo dcTo = new DataConnectivityTo();
				Float tCPDownloadSpeed = rs.getFloat("AVGTCP");
				Float rssi = rs.getFloat("AVGRSSI");
				String networkType = rs.getString("NetworkType");
				if(networkType.equalsIgnoreCase("WIFI")){
					if(null==tCPDownloadSpeed||tCPDownloadSpeed==0){
						wifitcpDriveDownloadSpeedList.add(null);
					}
					else{
						wifitcpDriveDownloadSpeedList.add(tCPDownloadSpeed);
					}
					if(rssi == 0 ||null==rssi){
						wifiressDriveList.add(null);
					}
					else{
						wifiressDriveList.add(rssi);
					}
				}
				else{
					wifiressDriveList.add(null);
					wifitcpDriveDownloadSpeedList.add(null);
					
				}
				////System.out.println("rssi-----------"+rssi);
				if(!networkType.equalsIgnoreCase("WIFI")){
					if(null==tCPDownloadSpeed||tCPDownloadSpeed==0){
						tcpDriveDownloadSpeedList.add(null);
					}
					else{
						tcpDriveDownloadSpeedList.add(tCPDownloadSpeed);
					}
					if(rssi == 0 ||null==rssi){
						ressDriveList.add(null);
					}
					else{
						ressDriveList.add(rssi);
					}
				}
				else{
					tcpDriveDownloadSpeedList.add(null);
					ressDriveList.add(null);
				}
				
			}
			dataMap.put("tcpDriveDownloadSpeedList", tcpDriveDownloadSpeedList);
			dataMap.put("ressDriveList", ressDriveList);
			dataMap.put("wifitcpDriveDownloadSpeedList", wifitcpDriveDownloadSpeedList);
			dataMap.put("wifiressDriveList", wifiressDriveList);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		finally{
//			closeConn();
		}
		return dataMap;
	}
	
	 public HashMap<String, HashMap<String,HashMap<String,List<Float>>>> getDetailedVoiceData(
				String deviceName, String testName, String marketId) {
		 	HashMap<String,HashMap<String,List<Float>>> dataMap = new HashMap<String, HashMap<String,List<Float>>>();
			HashMap<String,List<Float>> downloadSpeedMap = new HashMap<String, List<Float>>();
			HashMap<String,List<Float>> rssiMap = new HashMap<String, List<Float>>();
			
			String networkType = "";
			HashMap<String, HashMap<String,HashMap<String,List<Float>>>> networkWiseMap = new HashMap<String, HashMap<String,HashMap<String,List<Float>>>>();
			
			String query="SELECT TCP,RSSI,S1.NetworkType,'Good' AS QUALITY FROM (select (snr.TCPDownloadSpeed) " +
			" AS TCP,(snr.RSSI) AS RSSI,SNR.NetworkType from stg_net_results snr,(select DISTINCT Call_Timestamp " +
			" from stg_callevent_results where  VQuad_PhoneID='"+deviceName+"'and Call_Control_Event = 'MDNetTest' and TEST_NAME = '"+testName+"' " +
			" and MARKET_ID = '"+marketId+"') scer where scer.Call_Timestamp = snr.CallTimeStamp and snr.NetSpeedTest ='TCP' " +
			" AND snr.RSSI < -45 AND snr.RSSI > -70) AVG RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from " +
			" STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"') S1 ON S1.NetworkType  = AVG.NetworkType" +
							
			" union " +
			
			" SELECT TCP,RSSI,S1.NetworkType,'Medium' AS QUALITY FROM (select (snr.TCPDownloadSpeed) " +
			" AS TCP,(snr.RSSI) AS RSSI,SNR.NetworkType from stg_net_results snr,(select DISTINCT Call_Timestamp" +
			" from stg_callevent_results where  VQuad_PhoneID='"+deviceName+"'and Call_Control_Event = 'MDNetTest' and TEST_NAME = '"+testName+"' " +
			" and MARKET_ID = '"+marketId+"') scer where scer.Call_Timestamp = snr.CallTimeStamp and snr.NetSpeedTest ='TCP' " +
			" AND snr.RSSI < -71 AND snr.RSSI > -90) AVG RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from " +
			" STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"') S1 ON S1.NetworkType  = AVG.NetworkType" +
			
			" union " +
			
			"SELECT TCP,RSSI,S1.NetworkType,'Poor' AS QUALITY FROM (select (snr.TCPDownloadSpeed) " +
			" AS TCP,(snr.RSSI) AS RSSI,SNR.NetworkType from stg_net_results snr,(select DISTINCT Call_Timestamp" +
			" from stg_callevent_results where  VQuad_PhoneID='"+deviceName+"'and Call_Control_Event = 'MDNetTest' and TEST_NAME = '"+testName+"' " +
			" and MARKET_ID = '"+marketId+"') scer where scer.Call_Timestamp = snr.CallTimeStamp and snr.NetSpeedTest ='TCP' " +
			" AND snr.RSSI < -91 AND snr.RSSI > -103) AVG RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from " +
			" STG_NET_RESULTS WHERE MARKET_ID ='"+marketId+"') S1 ON S1.NetworkType  = AVG.NetworkType " +
			" Order by NetworkType, QUALITY";
	
			
			try {
					//System.out.println("query----getDetailedVoiceData-----"+query);
					ResultSet rs = st.executeQuery(query);
					while (rs.next()) {
						DataConnectivityTo dcTo = new DataConnectivityTo();
						String tcpDownloadSpeed = rs.getString("TCP");
						String rssi = rs.getString("RSSI");
						String quality = rs.getString("QUALITY");
					    networkType = rs.getString("NetworkType");
						dataMap = networkWiseMap.get(networkType);
						if(null==dataMap){
							dataMap = new HashMap<String, HashMap<String,List<Float>>>();
							downloadSpeedMap = new HashMap<String, List<Float>>();
							rssiMap = new HashMap<String, List<Float>>();
						}
						else{
							downloadSpeedMap = dataMap.get("DownloadSpeed");
							rssiMap = dataMap.get("rssi");
						}
						if(null!=quality){
							if(quality.equals("Good")){
								List<Float> dcgoodList = downloadSpeedMap.get(quality);
								List<Float> rssGoodList = rssiMap.get(quality);
								if(null==dcgoodList){
									dcgoodList = new ArrayList<Float>();
								}
								if(null==rssGoodList){
									rssGoodList = new ArrayList<Float>();
								}
								if(null!=tcpDownloadSpeed && !tcpDownloadSpeed.equals("Empty") &&!tcpDownloadSpeed.equals("0") ){
									float tcpDownloadSpeedInt=Float.parseFloat(tcpDownloadSpeed);
									dcgoodList.add(new Float(tcpDownloadSpeedInt));
								}
								if(null!=rssi && !rssi.equals("Empty") && !rssi.equals("0")){
									float rssiFloat=Float.parseFloat(rssi);
									rssGoodList.add(new Float(rssiFloat));
								}
								downloadSpeedMap.put(quality, dcgoodList);
								rssiMap.put(quality, rssGoodList);
							} else
							if(quality.equals("Medium")){
								List<Float> dcMediumList = downloadSpeedMap.get(quality);
								List<Float> rssMediumList = rssiMap.get(quality);
								if(null==dcMediumList){
									dcMediumList = new ArrayList<Float>();
								}
								if(null==rssMediumList){
									rssMediumList = new ArrayList<Float>();
								}
								if(null!=tcpDownloadSpeed && !tcpDownloadSpeed.equals("Empty") && !tcpDownloadSpeed.equals("0")){
									float tcpDownloadSpeedInt=Float.parseFloat(tcpDownloadSpeed);
									dcMediumList.add(new Float(tcpDownloadSpeedInt));
								}
								if(null!=rssi && !rssi.equals("Empty")&&!rssi.equals("0")){
									float rssiFloat=Float.parseFloat(rssi);
									rssMediumList.add(new Float(rssiFloat));
								}
								
								downloadSpeedMap.put(quality, dcMediumList);
								rssiMap.put(quality, rssMediumList);
							}else
							if(quality.equals("Poor")){
								List<Float> dcPoorList = downloadSpeedMap.get(quality);
								List<Float> rssPoorList = rssiMap.get(quality);
								if(null==dcPoorList){
									dcPoorList = new ArrayList<Float>();
								}
								if(null==rssPoorList){
									rssPoorList = new ArrayList<Float>();
								}
								if(null!=tcpDownloadSpeed && !tcpDownloadSpeed.equals("Empty")&&!tcpDownloadSpeed.equals("0")){
									float tcpDownloadSpeedInt=Float.parseFloat(tcpDownloadSpeed);
									dcPoorList.add(new Float(tcpDownloadSpeedInt));
								}
								if(null!=rssi && !rssi.equals("Empty")&&!rssi.equals("0")){
									float rssiFloat=Float.parseFloat(rssi);
									rssPoorList.add(new Float(rssiFloat));
								}
								downloadSpeedMap.put(quality, dcPoorList);
								rssiMap.put(quality, rssPoorList);
							}
						}
						dataMap.put("DownloadSpeed", downloadSpeedMap);
						dataMap.put("rssi", rssiMap);
						networkWiseMap.put(networkType, dataMap);
					}
//					dataMap.put("tcpDownloadSpeedList", tcpDownloadSpeedList);
					////System.out.println("networkWiseMap-------tcp------"+networkWiseMap);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			
			return networkWiseMap;
	 }
	

	 public static void main(String[] args) {
		String  tempMarketsList = "22,16";
		 List<String> jsonMarketsList = new ArrayList<String>(
					Arrays.asList(tempMarketsList.split(",")));
	//System.out.println("tempMarketsList-------------after spliitiin-------"+jsonMarketsList.contains("16"));
	}
}
