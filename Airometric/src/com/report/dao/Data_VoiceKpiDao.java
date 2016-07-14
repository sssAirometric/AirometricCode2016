package com.report.dao;

import java.util.HashMap;
import java.util.List;

public interface Data_VoiceKpiDao {

	public HashMap<String, List<Float>> getVoice_DataData(String deviceName,String testName,String marketId);
	 public HashMap<String, HashMap<String,HashMap<String,List<Float>>>> getDetailedVoiceData(
				String deviceName, String testName, String marketId);
}
