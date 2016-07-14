package com.preprocessorhelpers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.model.DBUtil;
import com.report.to.VoiceDataTo;

public class Voice_DataPreprocessorHelper {
	
	static Connection conn = null;
	static Statement st = null;
	public static List<VoiceDataTo> getVoice_DataData(
			String testName, String marketId) {
		
		List<VoiceDataTo> voiceDataList = new ArrayList<VoiceDataTo>();
		String query="SELECT FINAL.CallTimeStamp AS TIMESTAMP,FINAL.FINALTCP AS TCP,FINAL.FINALRSSI AS RSSI,'Good' AS QUALITY,S1.NetworkType AS NetworkType FROM (select snr.CallTimeStamp,(snr.TCPDownloadSpeed) " +
		" AS FINALTCP,(snr.RSSI) AS FINALRSSI ,SNR.NetworkType from stg_net_results snr,(select DISTINCT Call_Timestamp " +
		" from stg_callevent_results where  Call_Control_Event = 'MDNetTest' and TEST_NAME = '"+testName+"' " +
		" and MARKET_ID = '"+marketId+"') scer where scer.Call_Timestamp = snr.CallTimeStamp and snr.NetSpeedTest ='TCP' " +
		" ) FINAL RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from " +
		" STG_NET_RESULTS WHERE MARKET_ID ='22') S1 ON S1.NetworkType  = FINAL.NetworkType";
		//System.out.println("query-------------"+query);
		
		try {
			ResultSet rs = st.executeQuery(query);
			while(rs.next()){
				VoiceDataTo voiceDataTo = new VoiceDataTo();
				String tcp = rs.getString("TCP");
				String rssi = rs.getString("RSSI");
				String networkType = rs.getString("NetworkType");
				String timestamp = rs.getString("TIMESTAMP");
				voiceDataTo.setTcp(tcp);
				voiceDataTo.setRssi(rssi);
				voiceDataTo.setNetworkType(networkType);
				voiceDataTo.setTimestamp(timestamp);
				voiceDataList.add(voiceDataTo);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return voiceDataList;
	}
	
	private static void insertIntoVoicedata(String market_id,String testName,List<VoiceDataTo> voiceDataList){
		for(int i=0;i<voiceDataList.size();i++){
			VoiceDataTo vdTo = voiceDataList.get(i);
			String query = "INSERT INTO pre_calculation_voicedata_1 VALUES ('"+market_id+"','"+testName+"','"+vdTo.getTimestamp()+"', '"+vdTo.getTcp()+"', '"+vdTo.getRssi()+"', '"+vdTo.getNetworkType()+"')";
			try {
				st.executeUpdate(query);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
	public static List<VoiceDataTo> getVoice_DataDataSecondary(
			String testName, String marketId) {
		
		List<VoiceDataTo> voiceDataList = new ArrayList<VoiceDataTo>();
		String query="SELECT FINAL.FINALTCP AS TCP,FINAL.FINALRSSI AS RSSI,'Good' AS QUALITY,S1.NetworkType AS NetworkType FROM " +
				"(select AVG(snr.TCPDownloadSpeed) " +
		" AS FINALTCP,AVG(snr.RSSI) AS FINALRSSI ,SNR.NetworkType from stg_net_results snr,(select DISTINCT Call_Timestamp " +
		" from stg_callevent_results where  Call_Control_Event = 'MDNetTest' and TEST_NAME = '"+testName+"' " +
		" and MARKET_ID = '"+marketId+"') scer where scer.Call_Timestamp = snr.CallTimeStamp and snr.NetSpeedTest ='TCP' " +
		" ) FINAL RIGHT JOIN (SELECT distinct NetworkType,MARKET_ID from " +
		" STG_NET_RESULTS WHERE MARKET_ID ='22') S1 ON S1.NetworkType  = FINAL.NetworkType";
		//System.out.println("query-------------"+query);
		
		try {
			ResultSet rs = st.executeQuery(query);
			while(rs.next()){
				VoiceDataTo voiceDataTo = new VoiceDataTo();
				String tcp = rs.getString("TCP");
				String rssi = rs.getString("RSSI");
				String networkType = rs.getString("NetworkType");
				voiceDataTo.setTcp(tcp);
				voiceDataTo.setRssi(rssi);
				voiceDataTo.setNetworkType(networkType);
				voiceDataList.add(voiceDataTo);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return voiceDataList;
	}
	
	private static void insertIntoVoicedataSecondaryTable(String market_id,String testName,List<VoiceDataTo> voiceDataList){
		for(int i=0;i<voiceDataList.size();i++){
			VoiceDataTo vdTo = voiceDataList.get(i);
			String query = "INSERT INTO pre_calculation_voicedata_2 VALUES ( '"+market_id+"','"+testName+"','"+vdTo.getTcp()+"', '"+vdTo.getRssi()+"', '"+vdTo.getNetworkType()+"')";
			try {
				st.executeUpdate(query);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void preCalculateVoiceData(String testName,String market_id){
		conn = DBUtil.getConnection();
		try {
			st = conn.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<VoiceDataTo> voiceDataList = getVoice_DataData(testName,market_id);
		insertIntoVoicedata(market_id,testName,voiceDataList);
		List<VoiceDataTo> voiceDataSecondaryList = getVoice_DataDataSecondary(testName,market_id);
		insertIntoVoicedataSecondaryTable(market_id,testName,voiceDataSecondaryList);
	}
	public static void main(String[] args) {
		
			
			preCalculateVoiceData("22","g2");
		
	}
}
