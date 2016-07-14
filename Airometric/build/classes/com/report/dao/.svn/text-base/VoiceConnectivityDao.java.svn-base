package com.report.dao;

import java.util.HashMap;
import java.util.List;

import com.to.CallSetUpTo;
import com.to.VoiceConnectivityTO;

public interface VoiceConnectivityDao {
	public List<VoiceConnectivityTO> getAllDeviceInfo (String testName,String market_id,String test_type);
	public List<String> getAllDeviceInfoCallDropList(String testName,String market_id,String test_type);
	public List<VoiceConnectivityTO> getAllMultiplDeviceInfo (String testName,String market_id);
	public String getNeighbourInfo(List<String> neighbourInfoList);
	public List<String> getAllMultipleDeviceInfoCallDropList(String testName,String market_id);
	public String getMarketName(String MarketName);
	public HashMap<String, List<Integer>> getCallTearDownTime(String testName,String marketId);
	public HashMap<String, List<Integer>> getCallSetupTime(String testName,String marketId) ;
	public HashMap<String, List<Float>> getCallRetentionTime(String testName,String marketId);
	public HashMap<String, List<Integer>> getCallSetupTime4G(String testName,String marketId) ;
	public HashMap<String, List<Float>> getCallRetentionTime4G(String testName,String marketId) ;
	public void openConn();
	public void closeConn();
}
