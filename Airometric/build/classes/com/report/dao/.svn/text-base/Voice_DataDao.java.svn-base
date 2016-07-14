package com.report.dao;

import java.util.HashMap;
import java.util.List;

import com.to.Voice_DataTO;

public interface Voice_DataDao {
	public HashMap<String,List<Integer>> getDataForChart();
	public List<Voice_DataTO> getAllDeviceInfo (String testName,String market_id,String test_type,List<String>timestamp);
	public List<String> getExternal_CallDropList(String testName,String market_id);
	public List<String> getExternal_througputList(String testName,String market_id);
	public List<String> getExternal_througput(String testName,String market_id);
	public String getNeighbourInfo(List<String> neighbourInfoList);
	public List<Float> getVoice_DataKPI(String testName,String market_id);
}
