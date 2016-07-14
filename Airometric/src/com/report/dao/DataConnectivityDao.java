package com.report.dao;

import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONArray;

import com.report.result.to.TCPChartTO;
import com.report.result.to.UDPChartTO;
import com.to.DataConnectivityTo;

public interface DataConnectivityDao {
	public List<DataConnectivityTo> getTestNameThroughputDetailsResults(String testCaseName,String marketId);
	public boolean getCycleTestNames(String testCaseName,String marketId);
	public List<DataConnectivityTo> getTestNameMultipleThroughputDetailsResults(String testCaseName,String marketId);
	public String getThroughput(int i,double txvalue, double rxvalue, double mainValue, String eventName, String currTxBytes,
			String currTxBytes2, String prevRxBytes, String prevTxBytes, String throughputRX, String throughputTX,DataConnectivityTo 
			dataConnectivityTo,String throughtput);
	public String getMarketName(String MarketName);
	
	public net.sf.json.JSONArray getNetworksTypeForMarket(String marketId);
	
	public TCPChartTO populateDataForTCP(String deviceName,
			String testName, String marketId);
	
	 public UDPChartTO populateDataForUDP(String deviceName,
				String testName, String marketId);
	 
	 public HashMap<String,List<Float>> populateDataForDNS(String deviceName,
				String testName, String marketId);
	 
	 public HashMap<String,List<Float>> populateDataForFTP(String deviceName,
				String testName, String marketId,String type);
	
	public int getNumberOfNetworksForMarket(String marketId);
	
	/****************************Detailed data for charts****************************/
	public HashMap<String, HashMap<String,HashMap<String,List<Float>>>> populateDetailedDataForTCP(String deviceName,
			String testName, String marketId);
	
	public HashMap<String, HashMap<String, HashMap<String, List<Float>>>> populateDetailedDataForUDP(String deviceName,
				String testName, String marketId);
	 
	 public HashMap<String, HashMap<String, HashMap<String, List<Float>>>>populateDetailedDataForDNS(String deviceName,
				String testName, String marketId);
	
	////////////////////////////LTE/////////////////
	public HashMap<String,List<Float>> populateDataForTCPLte(String deviceName,
			String testName, String marketId);
	
	 public HashMap<String,List<Float>> populateDataForUDPLte(String deviceName,
				String testName, String marketId);
	 
	 public HashMap<String,List<Float>> populateDataForDNSLte(String deviceName,
				String testName, String marketId);
	 
	 public HashMap<String,List<Float>> populateDataForFTPLte(String deviceName,
				String testName, String marketId,List<String> networkList);
	
	 public HashMap<String, HashMap<String,HashMap<String,List<Float>>>> populateDetailedDataForTCPLte(String deviceName,
				String testName, String marketId);
	 public HashMap<String, HashMap<String, HashMap<String, List<Float>>>> populateDetailedDataForUDPLte(String deviceName,
				String testName, String marketId);
	 public HashMap<String, HashMap<String, HashMap<String, List<Float>>>>populateDetailedDataForDNSLte(String deviceName,
				String testName, String marketId);
		////////////////////////////LTE/////////////////
	 
	 //VOIP//
	 public HashMap<String,List<Float>> populateDataForVOIP(String deviceName,
				String testName, String marketId);
	 public HashMap<String, HashMap<String,HashMap<String,List<Float>>>> populateDetailedDataForVOIP(
				String deviceName, String testName, String marketId) ;
	 //voip//
	
	public void openConn();
	public void closeConn();
	//by Ankit
	public JSONArray getNetworksTypeJsonForMarket(String masterMarketId,
			String testName);
	public JSONArray getNetworksTypeJsonForUDPByMID_TN(String masterMarketId,
			String testName);
	
	
}
