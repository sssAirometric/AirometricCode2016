package com.preprocessorhelpers;



import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.preprocessing.PreprocessorMiscHelper;
import com.report.to.CallretentionTo;
import com.report.to.VoiceQualityTo;
import com.to.CallSetUpTo;
import com.to.DeviceInfoTO;
import com.to.STGDevice;
import com.to.Stg_Log_Cat_TO;
import com.dao.ReportDao;
import com.dao.UserDao;
import com.dao.impl.ReportDaoImpl;
import com.dao.impl.UserDaoImpl;
import com.model.DBUtil;

public class VoiceQualityHelper {

	
	static Connection conn = null;
	static Statement st = null;
	public static VoiceQualityTo getDownLinkParmatersForGraphForPolqa(
			String marketId, String testname) {
		List<Float> paramsValue = new ArrayList<Float>();
		ResultSet rs = null;
		VoiceQualityTo voiceQualityTo = new VoiceQualityTo();
		try {
				String query = "SELECT MAX(POLQA_Score),AVG(POLQA_Score),AVG(EModel_Polqa),AVG((Speech_Level_Diff/SNR_Diff)) AS SNR " +
						" FROM stg_polqa_results WHERE  TEST_NAME='"
						+ testname
						+ "' AND MARKET_ID='"
						+ marketId
						+ "'  AND TRIM(VQuad_CallID) LIKE 'O_%'";
				//System.out.println("query---polqa--------" + query);
				rs = st.executeQuery(query);
				while (rs.next()) {
					if (rs.getFloat("AVG(POLQA_Score)") == 0.0) {
						paramsValue.add(null);
					} else {
						paramsValue.add(rs.getFloat("AVG(POLQA_Score)"));
						voiceQualityTo.setPolqa(rs.getString("AVG(POLQA_Score)"));
					}
					if (rs.getFloat("AVG(EModel_Polqa)") == 0.0) {
						paramsValue.add(null);
						voiceQualityTo.setEmodel("0");
					} else {
						paramsValue.add(rs.getFloat("AVG(EModel_Polqa)"));
						voiceQualityTo.setEmodel(rs.getString("AVG(EModel_Polqa)"));
					}
					if (rs.getFloat("SNR") == 0.0) {
						paramsValue.add(null);
						voiceQualityTo.setSnr("0");
					} else {
						paramsValue.add(rs.getFloat("SNR"));
						voiceQualityTo.setSnr(rs.getString("SNR"));
					}
					if (rs.getFloat("MAX(POLQA_Score)")==0.0){
						voiceQualityTo.setPesq("0");
					}else{
						paramsValue.add(rs.getFloat("MAX(POLQA_Score)"));
						voiceQualityTo.setMaxValue(rs.getString("MAX(POLQA_Score)"));
					}
				}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
//				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		}
		return voiceQualityTo;
	}

	public static VoiceQualityTo getUplinkParmatersForGraphForPolqa(
			String marketId, String testname) {
		List<Float> paramsValue = new ArrayList<Float>();
		ResultSet rs = null;
		VoiceQualityTo voiceQualityTo = new VoiceQualityTo();
		try {
				String query = "SELECT MAX(POLQA_Score),AVG(POLQA_Score),AVG(EModel_Polqa),AVG((Speech_Level_Diff/SNR_Diff)) AS SNR " +
						" FROM stg_polqa_results WHERE  TEST_NAME='"
						+ testname
						+ "' AND MARKET_ID='"
						+ marketId
						+ "'  AND TRIM(VQuad_CallID) LIKE 'I_%'";
				//System.out.println("query---polqa--------" + query);
				rs = st.executeQuery(query);
				while (rs.next()) {
					if (rs.getFloat("AVG(POLQA_Score)") == 0.0) {
						paramsValue.add(null);
					} else {
						paramsValue.add(rs.getFloat("AVG(POLQA_Score)"));
						voiceQualityTo.setPolqa(rs.getString("AVG(POLQA_Score)"));
					}
					if (rs.getFloat("AVG(EModel_Polqa)") == 0.0) {
						paramsValue.add(null);
						voiceQualityTo.setEmodel("0");
					} else {
						paramsValue.add(rs.getFloat("AVG(EModel_Polqa)"));
						voiceQualityTo.setEmodel(rs.getString("AVG(EModel_Polqa)"));
					}
					if (rs.getFloat("SNR") == 0.0) {
						paramsValue.add(null);
						voiceQualityTo.setSnr("0");
					} else {
						paramsValue.add(rs.getFloat("SNR"));
						voiceQualityTo.setSnr(rs.getString("SNR"));
					}
					if (rs.getFloat("MAX(POLQA_Score)")==0.0){
						voiceQualityTo.setPesq("0");
					}else{
						paramsValue.add(rs.getFloat("MAX(POLQA_Score)"));
						voiceQualityTo.setMaxValue(rs.getString("MAX(POLQA_Score)"));
					}
				}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
//				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		}
		return voiceQualityTo;
	}

	
	public static List<VoiceQualityTo>  getDownlinkDetailedValuesForGraphTable(
			String testname,String marketId 
			) {
		List<Float> pesqList = new ArrayList<Float>();
		List<Float> pesqlqqList = new ArrayList<Float>();
		List<Float> pesqlqoList = new ArrayList<Float>();
		List<Float> speech_Level_Diff = new ArrayList<Float>();
		List<VoiceQualityTo> vqList = new ArrayList<VoiceQualityTo>();
		HashMap<String, String> detailsDataMap = new HashMap<String, String>();
		ResultSet rs = null;
		try {
			st = conn.createStatement();
			String query = "SELECT PESQ,PESQLQ,PESQLQO,PESQWB FROM stg_vqt_results WHERE TEST_NAME='"
				+ testname
				+ "' AND MARKET_ID='"
				+ marketId
				+ "'  AND TRIM(VQuadCallID) LIKE 'O_%'";
			 //System.out.println("getUplinkDetailedValuesForGraphTable======++====="+query);
			rs = st.executeQuery(query);
			while (rs.next()) {
				VoiceQualityTo vqTo = new VoiceQualityTo();
				vqTo.setPesq(rs.getString("PESQ"));
				vqTo.setPesqlq(rs.getString("PESQLQ"));
				vqTo.setPesqlqo(rs.getString("PESQLQO"));
				vqList.add(vqTo);
				// pesqwbList.add(rs.getInt("PESQWB"));
			}
			// }

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		}
		if(pesqList.size()>0){
			detailsDataMap.put("pesq", pesqList.toString().substring(1, pesqList.size()-1));
			detailsDataMap.put("pesqlq", pesqlqqList.toString().substring(1, pesqList.size()-1));
			detailsDataMap.put("pesqlqo", pesqlqoList.toString().substring(1, pesqList.size()-1));
		}
		else{
			detailsDataMap.put("pesq","");
			detailsDataMap.put("pesqlq","");
			detailsDataMap.put("pesqlqo", "");
		}
		
		return vqList;
	}

	public HashMap<String, List<Float>> getDownlinkDetailedValuesForGraphTable(
			String deviceName, String marketId, String testname,
			String filesName) {
		List<Float> pesqList = new ArrayList<Float>();
		List<Float> pesqlqqList = new ArrayList<Float>();
		List<Float> pesqlqoList = new ArrayList<Float>();
		List<Float> pesqwbList = new ArrayList<Float>();
		HashMap<String, List<Float>> detailsDataMap = new HashMap<String, List<Float>>();
		ResultSet rs = null;
		try {
			st = conn.createStatement();
				String query = "SELECT  POLQA_Score,EModel_Polqa,(Speech_Level_Diff/SNR_Diff) AS SNR FROM stg_vqt_results WHERE DEVICE_MODEL='"
						+ deviceName
						+ "'  AND TEST_NAME='"
						+ testname
						+ "' AND MARKET_ID='"
						+ marketId
						+ "'    AND TRIM(VQuadCallID) LIKE 'I_%' ";
				// //System.out.println("getDownlinkDetailedValuesForGraphTable==========="+query);
				rs = st.executeQuery(query);
				while (rs.next()) {
					pesqList.add(rs.getFloat("POLQA_Score"));
					pesqlqqList.add(rs.getFloat("EModel_Polqa"));
					pesqlqoList.add(rs.getFloat("SNR"));
					// pesqwbList.add(rs.getInt("PESQWB"));
				}
//			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
//				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		}
		detailsDataMap.put("pesq", pesqList);
		detailsDataMap.put("pesqlq", pesqlqqList);
		detailsDataMap.put("pesqlqo", pesqlqoList);
		detailsDataMap.put("pesqwb", pesqwbList);
		// //System.out.println("detailsDataMap---polqa----------"+detailsDataMap);
		return detailsDataMap;
	}


	public void prepopulateFtpData( String testName,
			String marketId) {
		conn = DBUtil.getConnection();
		try {
			st = conn.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static VoiceQualityTo getDownlinkParmatersForGraph(
			String testname,String marketId) {
		List<Float> paramsValue = new ArrayList<Float>();
		ResultSet rs = null;
		VoiceQualityTo voiceQualityTo = new VoiceQualityTo();
		try {
			////System.out.println("getMarketQuery-------"+getMarketQuery);
				String query = "SELECT MAX(PESQ),AVG(PESQ),AVG(PESQLQ),AVG(PESQLQO),AVG(PESQWB) FROM stg_vqt_results WHERE  TEST_NAME='"
						+ testname
						+ "' AND MARKET_ID='"
						+ marketId
						+ "'  AND TRIM(VQuadCallID) LIKE 'O_%'";
				//System.out.println("query------++----------"+query);
				st = conn.createStatement();
				rs = st.executeQuery(query);
				while (rs.next()) {
					if (rs.getFloat("AVG(PESQ)")==0.0){
						paramsValue.add(null);
						voiceQualityTo = getDownLinkParmatersForGraphForPolqa(marketId,testname);
						break;
//						voiceQualityTo.setPesq("0");
					}else{
						voiceQualityTo.setPesq(rs.getString("AVG(PESQ)"));
						if (rs.getFloat("AVG(PESQLQ)")==0.0){
							voiceQualityTo.setPesq("0");
						}else{
							voiceQualityTo.setPesqlq(rs.getString("AVG(PESQLQ)"));
						}
						if (rs.getFloat("AVG(PESQLQO)")==0.0){
							voiceQualityTo.setPesq("0");
						}else{
							voiceQualityTo.setPesqlqo(rs.getString("AVG(PESQLQO)"));
						}
						if (rs.getFloat("MAX(PESQ)")==0.0){
							voiceQualityTo.setPesq("0");
						}else{
							voiceQualityTo.setMaxValue(rs.getString("MAX(PESQ)"));
						}
					}
				
				}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return voiceQualityTo;
	}
	
	public static VoiceQualityTo getUplinkParmatersForGraph(
			String testname,String marketId) {
		List<Float> paramsValue = new ArrayList<Float>();
		ResultSet rs = null;
		VoiceQualityTo voiceQualityTo = new VoiceQualityTo();
		try {
			////System.out.println("getMarketQuery-------"+getMarketQuery);
				String query = "SELECT MAX(PESQ),AVG(PESQ),AVG(PESQLQ),AVG(PESQLQO),AVG(PESQWB) FROM stg_vqt_results WHERE  TEST_NAME='"
						+ testname
						+ "' AND MARKET_ID='"
						+ marketId
						+ "'  AND TRIM(VQuadCallID) LIKE 'I_%'";
				//System.out.println("query------++----------"+query);
				st = conn.createStatement();
				rs = st.executeQuery(query);
				while (rs.next()) {
					if (rs.getFloat("AVG(PESQ)")==0.0){
						paramsValue.add(null);
						voiceQualityTo = getUplinkParmatersForGraphForPolqa(marketId,testname);
						break;
//						voiceQualityTo.setPesq("0");
					}else{
						voiceQualityTo.setPesq(rs.getString("AVG(PESQ)"));
						if (rs.getFloat("AVG(PESQLQ)")==0.0){
							voiceQualityTo.setPesq("0");
						}else{
							voiceQualityTo.setPesqlq(rs.getString("AVG(PESQLQ)"));
						}
						if (rs.getFloat("AVG(PESQLQO)")==0.0){
							voiceQualityTo.setPesq("0");
						}else{
							voiceQualityTo.setPesqlqo(rs.getString("AVG(PESQLQO)"));
						}
						if (rs.getFloat("MAX(PESQ)")==0.0){
							voiceQualityTo.setPesq("0");
						}else{
							voiceQualityTo.setMaxValue(rs.getString("MAX(PESQ)"));
						}
					}
				
				}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return voiceQualityTo;
	}
	
	public static void insertVQDetailVals(String marketName,String testName,List<VoiceQualityTo> valuesList,String chartType){
		String insertQuery = "" ;
//		System.out.println("valuesList---------"+valuesList.size());
		for(VoiceQualityTo vto : valuesList){
			if(null!=vto.getTimestamp()){
				if(null==vto.getPolqa()){
					if((vto.getNetworkType().contains("3G"))){
						insertQuery = "INSERT INTO pre_cal_voiceQuality_1 (MARKET_ID,TEST_NAME,PESQ,PESQLQ,PESQLQO,CHARTTYPE,SIGNALSTRENGTH,RSRP,NETWORK_TYPE,TIMESTAMP,IMEI	" +
								",TEST_TYPE,	USER_NAME	,DEVICE_PHONENUMBER,	DEVICE_PHONETYPE,	" +
								"DEVICE_MANUFACTURER,	DEVICE_VERSION,	TIME_STAMP_FOREACH_SAMPLE,	NETWORK_NETWORKOPERATOR,	NETWORK_NETWORKOPERATORNAME," +
								"	NETWORK_NETWORKTYPE	,NETWORK_DATASTATE,	NETWORK_DATAACTIVITY,	NETWORK_WIFISTATE,	NETWORK_ROAMING,	NETWORK_MCC	,NETWORK_MNC	" +
								",SIGNALSTRENGTH_GSMSIGNALSTRENGTH,	SIGNALSTRENGTH_CDMADBM,	SIGNALSTRENGTH_CDMACIO,	SIGNALSTRENGTH_EVDODBM,	SIGNALSTRENGTH_EVDOECIO	" +
								",SIGNALSTRENGTH_EVDOSNR,	SIGNALSTRENGTH_GSM,	SIGNALSTRENGTH_GSMBITRATEERROR,	SIGNALSTRENGTH_LTESIGNALSTRENGTH,	" +
								"SIGNALSTRENGTH_LTERSRP,	SIGNALSTRENGTH_LTERSRQ,	SIGNALSTRENGTH_LTERSSNR,	SIGNALSTRENGTH_LTECQI,	CELLLOCATION_CID,	" +
								"CELLLOCATION_LAC,	NEIGHBOUR_INFO,	BATTERY_LEVEL,	NETWORK_MANUALLY_DONE,	GEOLOCATION_LATITUDE,	GEOLOCATION_LONGITUDE," +
								"		SNAPSHOT_ID)"+

						"VALUES('"+marketName+"','"+testName+"','"+vto.getPesq()+"','"+vto.getPesqlq()+"','"+vto.getPesqlqo()+"','"+vto.getChartType()+"','"+vto.getSignalStrength()+"','"+vto.getRsrp()+"','"+vto.getNetworkType()+"','"+vto.getTimestamp()+"'," +
								"'"+vto.getDeviceInfoTO().getImei()+"','"+vto.getDeviceInfoTO().getTestType()+"','"+vto.getDeviceInfoTO().getUserName()+"'," +
										"'"+vto.getDeviceInfoTO().getPhoneNumber()+"','"+vto.getDeviceInfoTO().getPhoneType()+"','"+vto.getDeviceInfoTO().getDeviceManufacturer()+"','"+vto.getDeviceInfoTO().getDeviceVersion()+"'," +
										"'"+vto.getDeviceInfoTO().getTimeStampForEachSample()+"','"+vto.getDeviceInfoTO().getNetworkOperator()+"','"+vto.getDeviceInfoTO().getNetworkOperatorName()+"','"+vto.getDeviceInfoTO().getNetworkType()+"'," +
										"'"+vto.getDeviceInfoTO().getNetworkDataState()+"','"+vto.getDeviceInfoTO().getDataActivity()+"','"+vto.getDeviceInfoTO().getWifiState()+"','"+vto.getDeviceInfoTO().getNetworkRoaming()+"'," +
										"'"+vto.getDeviceInfoTO().getNetworkMCC()+"','"+vto.getDeviceInfoTO().getNetworkMNC()+"','"+vto.getDeviceInfoTO().getSignalStrengthGSM()+"','"+vto.getDeviceInfoTO().getSignalStrengthCDMA()+"'," +
										"'"+vto.getDeviceInfoTO().getSignalStrengthCDMACIO()+"','"+vto.getDeviceInfoTO().getSignalStrengthEVDO()+"','"+vto.getDeviceInfoTO().getSignalStrengthEVDOECIO()+"','"+vto.getDeviceInfoTO().getSignalStrength_EVDOSNR()+"'," +
										"'"+vto.getDeviceInfoTO().getSignalStrengthGSM()+"','"+vto.getDeviceInfoTO().getSignalStrengthGSMBITRATEERROR()+"','"+vto.getDeviceInfoTO().getSignalStrengthLTE()+"','"+vto.getDeviceInfoTO().getSignalStrengthLTERSRP()+"'," +
										"'"+vto.getDeviceInfoTO().getSignalStrengthLTERSRQ()+"','"+vto.getDeviceInfoTO().getSignalStrengthLTERSSNR()+"','"+vto.getDeviceInfoTO().getSignalStrength_LTECQI()+"','"+vto.getDeviceInfoTO().getCellLocationCID()+"'," +
										"'"+vto.getDeviceInfoTO().getCellLocationLAC()+"','"+vto.getDeviceInfoTO().getNeighbourInfo()+"','"+vto.getDeviceInfoTO().getBatteryLevel()+"','"+vto.getDeviceInfoTO().getNetworkManuallyDone()+"'," +
										"'"+vto.getDeviceInfoTO().getLattitude()+"','"+vto.getDeviceInfoTO().getLongitude()+"','"+vto.getDeviceInfoTO().getSnapShotId()+"')" ;
					}
				}
				else{
					insertQuery = "INSERT INTO pre_cal_voiceQuality_1 (MARKET_ID,TEST_NAME,POLQA,EModel_Polqa,SNR,JITTER_AVG,CHARTTYPE,SIGNALSTRENGTH,RSRP,NETWORK_TYPE,TIMESTAMP,IMEI	" +
								",TEST_TYPE,	USER_NAME	,DEVICE_PHONENUMBER,	DEVICE_PHONETYPE,	" +
								"DEVICE_MANUFACTURER,	DEVICE_VERSION,	TIME_STAMP_FOREACH_SAMPLE,	NETWORK_NETWORKOPERATOR,	NETWORK_NETWORKOPERATORNAME," +
								"	NETWORK_NETWORKTYPE	,NETWORK_DATASTATE,	NETWORK_DATAACTIVITY,	NETWORK_WIFISTATE,	NETWORK_ROAMING,	NETWORK_MCC	,NETWORK_MNC	" +
								",SIGNALSTRENGTH_GSMSIGNALSTRENGTH,	SIGNALSTRENGTH_CDMADBM,	SIGNALSTRENGTH_CDMACIO,	SIGNALSTRENGTH_EVDODBM,	SIGNALSTRENGTH_EVDOECIO	" +
								",SIGNALSTRENGTH_EVDOSNR,	SIGNALSTRENGTH_GSM,	SIGNALSTRENGTH_GSMBITRATEERROR,	SIGNALSTRENGTH_LTESIGNALSTRENGTH,	" +
								"SIGNALSTRENGTH_LTERSRP,	SIGNALSTRENGTH_LTERSRQ,	SIGNALSTRENGTH_LTERSSNR,	SIGNALSTRENGTH_LTECQI,	CELLLOCATION_CID,	" +
								"CELLLOCATION_LAC,	NEIGHBOUR_INFO,	BATTERY_LEVEL,	NETWORK_MANUALLY_DONE,	GEOLOCATION_LATITUDE,	GEOLOCATION_LONGITUDE," +
								"		SNAPSHOT_ID)"+
					"VALUES('"+marketName+"','"+testName+"','"+vto.getPolqa()+"','"+vto.getEmodel()+"','"+vto.getSnr()+"','"+vto.getAvgJitter()+"'," +
							"'"+vto.getChartType()+"','"+vto.getSignalStrength()+"','"+vto.getRsrp()+"','"+vto.getNetworkType()+"','"+vto.getTimestamp()+"',"+
								"'"+vto.getDeviceInfoTO().getImei()+"','"+vto.getDeviceInfoTO().getTestType()+"','"+vto.getDeviceInfoTO().getUserName()+"'," +
										"'"+vto.getDeviceInfoTO().getPhoneNumber()+"','"+vto.getDeviceInfoTO().getPhoneType()+"','"+vto.getDeviceInfoTO().getDeviceManufacturer()+"','"+vto.getDeviceInfoTO().getDeviceVersion()+"'," +
										"'"+vto.getDeviceInfoTO().getTimeStampForEachSample()+"','"+vto.getDeviceInfoTO().getNetworkOperator()+"','"+vto.getDeviceInfoTO().getNetworkOperatorName()+"','"+vto.getDeviceInfoTO().getNetworkType()+"'," +
										"'"+vto.getDeviceInfoTO().getNetworkDataState()+"','"+vto.getDeviceInfoTO().getDataActivity()+"','"+vto.getDeviceInfoTO().getWifiState()+"','"+vto.getDeviceInfoTO().getNetworkRoaming()+"'," +
										"'"+vto.getDeviceInfoTO().getNetworkMCC()+"','"+vto.getDeviceInfoTO().getNetworkMNC()+"','"+vto.getDeviceInfoTO().getSignalStrengthGSM()+"','"+vto.getDeviceInfoTO().getSignalStrengthCDMA()+"'," +
										"'"+vto.getDeviceInfoTO().getSignalStrengthCDMACIO()+"','"+vto.getDeviceInfoTO().getSignalStrengthEVDO()+"','"+vto.getDeviceInfoTO().getSignalStrengthEVDOECIO()+"','"+vto.getDeviceInfoTO().getSignalStrength_EVDOSNR()+"'," +
										"'"+vto.getDeviceInfoTO().getSignalStrengthGSM()+"','"+vto.getDeviceInfoTO().getSignalStrengthGSMBITRATEERROR()+"','"+vto.getDeviceInfoTO().getSignalStrengthLTE()+"','"+vto.getDeviceInfoTO().getSignalStrengthLTERSRP()+"'," +
										"'"+vto.getDeviceInfoTO().getSignalStrengthLTERSRQ()+"','"+vto.getDeviceInfoTO().getSignalStrengthLTERSSNR()+"','"+vto.getDeviceInfoTO().getSignalStrength_LTECQI()+"','"+vto.getDeviceInfoTO().getCellLocationCID()+"'," +
										"'"+vto.getDeviceInfoTO().getCellLocationLAC()+"','"+vto.getDeviceInfoTO().getNeighbourInfo()+"','"+vto.getDeviceInfoTO().getBatteryLevel()+"','"+vto.getDeviceInfoTO().getNetworkManuallyDone()+"'," +
										"'"+vto.getDeviceInfoTO().getLattitude()+"','"+vto.getDeviceInfoTO().getLongitude()+"','"+vto.getDeviceInfoTO().getSnapShotId()+"')" ;
				}
				 
			try {
//				System.out.println(insertQuery);
				st.executeUpdate(insertQuery);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
			
		}
	
	}
	
	public static void insertVQSummaryVals(String marketName,String testName,VoiceQualityTo vqtTo,String chartType){
		String insertQuery = "INSERT INTO pre_cal_voicequality_2 VALUES ('"+marketName+"', '"+testName+"', 'pesq', '"+vqtTo.getPesq()+"','"+vqtTo.getMaxValue()+"','"+chartType+"')";
			try {
				if(null==vqtTo.getPesq()||vqtTo.getPesq().equalsIgnoreCase("0")||vqtTo.getPesq().length()==0){
					insertVQSummaryValsPolqa(marketName,testName,vqtTo,chartType);
				}
				else{
					st.executeUpdate(insertQuery);
					insertQuery = "INSERT INTO pre_cal_voicequality_2 VALUES ('"+marketName+"', '"+testName+"', 'pesqlq', '"+vqtTo.getPesqlq()+"','0','"+chartType+"')";
					st.executeUpdate(insertQuery);
					insertQuery = "INSERT INTO pre_cal_voicequality_2 VALUES ('"+marketName+"', '"+testName+"', 'pesqlqo', '"+vqtTo.getPesqlqo()+"','0','"+chartType+"')";
					st.executeUpdate(insertQuery);
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public static void insertVQSummaryValsPolqa(String marketName,String testName,VoiceQualityTo vqtTo,String chartType){
		String insertQuery = "INSERT INTO pre_cal_voicequality_2 VALUES ('"+marketName+"', '"+testName+"', 'polqa', '"+vqtTo.getPolqa()+"','"+vqtTo.getMaxValue()+"','"+chartType+"')";
			try {
				////System.outout.println("insertQuery---------"+insertQuery);
				st.executeUpdate(insertQuery);
				insertQuery = "INSERT INTO pre_cal_voicequality_2 VALUES ('"+marketName+"', '"+testName+"', 'EModel_Polqa', '"+vqtTo.getEmodel()+"','0','"+chartType+"')";
				st.executeUpdate(insertQuery);
				insertQuery = "INSERT INTO pre_cal_voicequality_2 VALUES ('"+marketName+"', '"+testName+"', 'SNR', '"+vqtTo.getSnr()+"','0','"+chartType+"')";
				st.executeUpdate(insertQuery);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	
	private static List<VoiceQualityTo> calculateMapvalues(String testName,String marketName){
		UserDao userDao = new UserDaoImpl();
		ReportDao dao=new ReportDaoImpl();
		List<String>minMaxTimStamps=dao.getMinandMaxTimestampforVQT(testName,marketName);
		//System.out.println("minMaxTimStamps--------"+minMaxTimStamps);
		List<DeviceInfoTO> deviceInfoList =new ArrayList<DeviceInfoTO>();
		PreprocessorMiscHelper preprocessorMiscHelper = new PreprocessorMiscHelper();
		List<STGDevice>vqtResults = new ArrayList<STGDevice>();
		vqtResults=dao.getTestNameMapDetailsResultsForVQT(testName,marketName,minMaxTimStamps);
		deviceInfoList = userDao.getAllDeviceInfoForVQT(testName,marketName,"externaltest",minMaxTimStamps);
		List<VoiceQualityTo> voiceQualityList = new ArrayList<VoiceQualityTo>();
		//System.out.println("deviceInfoList.size()---------"+deviceInfoList.size());
//		System.out.println("vqtResults.size()-------------"+vqtResults.size());
		int foundIndex = 0;
		for(int i=0;i<vqtResults.size();i++){
			STGDevice vqtDevice =vqtResults.get(i);
//			System.out.println("new loop");
			for(int j=0;j<deviceInfoList.size();j++){
				DeviceInfoTO deviceInfo =deviceInfoList.get(j);//
				//System.outout.println(vqtDevice.getCallTimestamp());
				//System.outout.println(deviceInfo.getTimeStampForEachSample());
				boolean isTimestampsClose=preprocessorMiscHelper.closeCallTimestamp(vqtDevice.getCallTimestamp(),deviceInfo.getTimeStampForEachSample());
				//System.out.println("isTimestampsClose---"+isTimestampsClose);
				if(isTimestampsClose){
					VoiceQualityTo vqTo = new VoiceQualityTo();
					vqTo.setPesq(vqtDevice.getPESQ());
					vqTo.setPesqlq(vqtDevice.getPESQLQ());
					vqTo.setPesqlqo(vqtDevice.getPESQLQO());
					vqTo.setNetworkType(deviceInfo.getNetworkType());
					vqTo.setSignalStrength(preprocessorMiscHelper.getSignalStrength(deviceInfo));
					vqTo.setRsrp(deviceInfo.getSignalStrengthLTERSRP());
					vqTo.setTimestamp(vqtDevice.getCallTimestamp());
					vqTo.setChartType(vqtDevice.getChartType());
					vqTo.setPolqa(vqtDevice.getPolqa());
					vqTo.setSnr(vqtDevice.getSnr());
					vqTo.setEmodel(vqtDevice.getEmodel());
					vqTo.setFilesource(vqtDevice.getFileSource());
					vqTo.setAvgJitter(vqtDevice.getAverageJitter());
					vqTo.setCellLocationCid(deviceInfo.getCellLocationCID());
					vqTo.setCellLocationLac(deviceInfo.getCellLocationLAC());
					vqTo.setPhoneNumber(deviceInfo.getPhoneNumber());
					vqTo.setPhoneType(deviceInfo.getPhoneType());
					vqTo.setDeviceModel(deviceInfo.getDeviceName());
					vqTo.setManufacturer(deviceInfo.getDeviceManufacturer());
					vqTo.setVersion(deviceInfo.getDeviceVersion());
					vqTo.setLattitude(deviceInfo.getLattitude());
					vqTo.setLongitude(deviceInfo.getLongitude());
					vqTo.setDataState(deviceInfo.getNetworkDataState());
					vqTo.setDeviceInfoTO(deviceInfo);
					voiceQualityList.add(vqTo);
//					foundIndex = j+1;
					//System.out.println("broke---"+foundIndex);
					break;
				}
			}
			
//			System.out.println("voiceQualityList--------"+voiceQualityList);
		}
		//System.out.println("finished");
		return voiceQualityList;
	}
	
	public static void prepopulateVQData(String testName,
			String marketId) {
		conn = DBUtil.getConnection();
	try {
			st = conn.createStatement();
			List<VoiceQualityTo> uplinkValues = new ArrayList<VoiceQualityTo>();
			List<VoiceQualityTo> downValues = new ArrayList<VoiceQualityTo>();
			VoiceQualityTo voiceQualityTo = new VoiceQualityTo();
			VoiceQualityTo voiceQualityUplinkTo = new VoiceQualityTo();
			uplinkValues = calculateMapvalues(testName,marketId);
			downValues = getDownlinkDetailedValuesForGraphTable(testName,marketId);
			voiceQualityTo = getDownlinkParmatersForGraph(testName,marketId);
			voiceQualityUplinkTo = getUplinkParmatersForGraph(testName,marketId);
			insertVQDetailVals(marketId,testName,uplinkValues,"uplink");
			insertVQDetailVals(marketId,testName,downValues,"downlink");
			insertVQSummaryVals(marketId,testName,voiceQualityTo,"downlink");
			insertVQSummaryVals(marketId,testName,voiceQualityUplinkTo,"uplink");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		prepopulateVQData("D3MNM0620","35");
	}

}
