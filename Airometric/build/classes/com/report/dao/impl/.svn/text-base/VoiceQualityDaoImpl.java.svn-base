package com.report.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.model.DBUtil;

public class VoiceQualityDaoImpl {

	public List<Float> getDownlinkParmatersForGraphForPolqa(String deviceName,
			String marketId, String testname, String filesName) {
		List<Float> paramsValue = new ArrayList<Float>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
		/*	String fileMarketName = "";
			String getMarketQuery = "SELECT MARKET_ID  FROM file_history WHERE FILE_NAME='"
					+ filesName.replaceAll("__--__", ":").replaceAll("\"", "")
					+ "'";
			// //System.out.println("getMarketQuery-------"+getMarketQuery);
			rs = st.executeQuery(getMarketQuery);
			while (rs.next()) {
				fileMarketName = rs.getString("MARKET_ID");
			}*/
//			if (fileMarketName.equals(marketId)) {
				String query = "SELECT AVG(POLQA_Score) AS POLQASCORE,AVG(EModel_Polqa) AS EMODELSCORE ,AVG((Speech_Level_Diff/SNR_Diff)) AS SNR FROM stg_polqa_results WHERE DEVICE_MODEL='"
						+ deviceName
						+ "'  AND TEST_NAME='"
						+ testname
						+ "' AND MARKET_ID='"
						+ marketId
						+ "'  AND TRIM(VQuad_CallID) LIKE 'I_%'";
				//System.out.println("query---polqa-----line--------" + query);
				rs = st.executeQuery(query);
				while (rs.next()) {
					if (rs.getFloat("POLQASCORE") == 0.0) {
						paramsValue.add(null);
					} else {
						paramsValue.add(rs.getFloat("POLQASCORE"));
					}
					if (rs.getFloat("EMODELSCORE") == 0.0) {
						paramsValue.add(null);
					} else {
						paramsValue.add(rs.getFloat("EMODELSCORE"));
					}
					if (rs.getFloat("SNR") == 0.0) {
						paramsValue.add(null);
					} else {
						paramsValue.add(rs.getFloat("SNR"));
					}
					/*
					 * if (rs.getFloat("AVG(PESQWB)")==0.0){
					 * paramsValue.add(null); }else{
					 * paramsValue.add(rs.getFloat("AVG(PESQWB)")); }
					 */
				}
//			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		}
		 //System.out.println("paramsValue----"+paramsValue);
		return paramsValue;
	}

	public List<Float> getUplinkParmatersForGraphForPolqa(String deviceName,
			String marketId, String testname, String filesName) {
		List<Float> paramsValue = new ArrayList<Float>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
	/*		
			String fileMarketName = "";
			String getMarketQuery = "SELECT MARKET_ID  FROM file_history WHERE FILE_NAME='"
					+ filesName.replaceAll("__--__", ":").replaceAll("\"", "")
					+ "'";
			// //System.out.println("getMarketQuery-------"+getMarketQuery);
			rs = st.executeQuery(getMarketQuery);
			while (rs.next()) {
				fileMarketName = rs.getString("MARKET_ID");
			}
			if (fileMarketName.equals(marketId)) {*/
				String query = "SELECT AVG(POLQA_Score),AVG(EModel_Polqa),AVG((Speech_Level_Diff/SNR_Diff)) AS SNR  FROM stg_polqa_results WHERE DEVICE_MODEL='"
						+ deviceName
						+ "'  AND TEST_NAME='"
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
					}
					if (rs.getFloat("AVG(EModel_Polqa)") == 0.0) {
						paramsValue.add(null);
					} else {
						paramsValue.add(rs.getFloat("AVG(EModel_Polqa)"));
					}
					if (rs.getFloat("SNR") == 0.0) {
						paramsValue.add(null);
					} else {
						paramsValue.add(rs.getFloat("SNR"));
					}
					/*
					 * if (rs.getFloat("AVG(PESQWB)")==0.0){
					 * paramsValue.add(null); }else{
					 * paramsValue.add(rs.getFloat("AVG(PESQWB)")); }
					 */
				}
//			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		}
		return paramsValue;
	}

	public HashMap<String, List<Float>> getUplinkDetailedValuesForGraphTable(
			String deviceName, String marketId, String testname,
			String filesName) {
		List<Float> pesqList = new ArrayList<Float>();
		List<Float> pesqlqqList = new ArrayList<Float>();
		List<Float> pesqlqoList = new ArrayList<Float>();
		List<Float> pesqwbList = new ArrayList<Float>();
		HashMap<String, List<Float>> detailsDataMap = new HashMap<String, List<Float>>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String query = "SELECT POLQA_Score,EModel_Polqa,(Speech_Level_Diff/SNR_Diff) AS SNR  FROM stg_polqa_results WHERE DEVICE_MODEL='"
					+ deviceName
					+ "'  AND TEST_NAME='"
					+ testname
					+ "' AND MARKET_ID='"
					+ marketId
					+ "'   AND TRIM(VQuad_CallID) LIKE 'O_%' ";
			// //System.out.println("getUplinkDetailedValuesForGraphTable==========="+query);
			rs = st.executeQuery(query);
			while (rs.next()) {
				pesqList.add(rs.getFloat("POLQA_Score"));
				pesqlqqList.add(rs.getFloat("EModel_Polqa"));
				pesqlqoList.add(rs.getFloat("SNR"));
				// pesqwbList.add(rs.getInt("PESQWB"));
			}
			// }

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		}
		detailsDataMap.put("pesq", pesqList);
		detailsDataMap.put("pesqlq", pesqlqqList);
		detailsDataMap.put("pesqlqo", pesqlqoList);
		detailsDataMap.put("pesqwb", pesqwbList);
		//System.out.println("detailsDataMap---polqa-------" + pesqList.size());
		return detailsDataMap;
	}

	public HashMap<String, List<Float>> getDownlinkDetailedValuesForGraphTable(
			String deviceName, String marketId, String testname,
			String filesName) {
		List<Float> pesqList = new ArrayList<Float>();
		List<Float> pesqlqqList = new ArrayList<Float>();
		List<Float> pesqlqoList = new ArrayList<Float>();
		List<Float> pesqwbList = new ArrayList<Float>();
		HashMap<String, List<Float>> detailsDataMap = new HashMap<String, List<Float>>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String query = "SELECT  POLQA_Score,EModel_Polqa,(Speech_Level_Diff/SNR_Diff) AS SNR FROM stg_vqt_results WHERE DEVICE_MODEL='"
						+ deviceName
						+ "'  AND TEST_NAME='"
						+ testname
						+ "' AND MARKET_ID='"
						+ marketId
						+ "'    AND TRIM(VQuad_CallID) LIKE 'I_%' ";
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
				conn.close();
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

	public List<Float> getVQDetailsFor_LineForGraphForPolqa(String deviceName,
			String marketId, String testname) {
		List<Float> paramsValue = new ArrayList<Float>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		Float bucket1_15 = new Float(0);
		Float bucket15_2 = new Float(0);
		Float bucket2_25 = new Float(0);
		Float bucket25_3 = new Float(0);
		Float bucket3_35 = new Float(0);
		Float bucket35_4 = new Float(0);
		Float bucket4_45 = new Float(0);
		Float bucket45_5 = new Float(0);
		Float totalValue = new Float(0);
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String query = "SELECT POLQA_Score FROM stg_polqa_results WHERE DEVICE_MODEL='"
					+ deviceName
					+ "'  AND TEST_NAME='"
					+ testname
					+ "' AND MARKET_ID='"
					+ marketId
					+ "'  AND TRIM(VQuad_CallID) LIKE 'O_%'  ORDER BY POLQA_Score ";
			//System.out.println("=====getVQDetailsFor_LineForGraph===" + query);
			rs = st.executeQuery(query);
			while (rs.next()) {
				totalValue++;
				Float value = rs.getFloat("POLQA_Score");
				// totalValue = totalValue+value;
				if (value > 1 && value <= 1.5) {
					bucket1_15++;
				} else if (value > 1.5 && value <= 2) {
					//System.out.println("inside if-------");
					bucket15_2++;
				} else if (value > 2 && value <= 2.5) {
					bucket2_25++;
				} else if (value > 2 && value <= 3) {
					bucket25_3++;
				} else if (value > 3 && value <= 3.5) {
					bucket3_35++;
				} else if (value > 3.5 && value <= 4) {
					bucket35_4++;
				} else if (value > 4 && value <= 4.5) {
					bucket4_45++;
				} else if (value > 4.5 && value <= 5) {
					bucket45_5++;
				}

			}
			//System.out.println("bucket15_2--------" + bucket15_2);
			//System.out.println("totalValue---polqa------" + totalValue);
			if (bucket1_15 == 0.0) {
				bucket1_15 = null;
			} else {
				bucket1_15 = (bucket1_15 / totalValue) * 100;
			}
			if (bucket15_2 == 0.0) {
				bucket15_2 = null;
			} else {
				bucket15_2 = (bucket15_2 / totalValue) * 100;
			}
			if (bucket2_25 == 0.0) {
				bucket2_25 = null;
			} else {
				bucket2_25 = (bucket2_25 / totalValue) * 100;
			}
			if (bucket25_3 == 0.0) {
				bucket25_3 = null;
			} else {
				bucket25_3 = (bucket25_3 / totalValue) * 100;
			}
			if (bucket3_35 == 0.0) {
				bucket3_35 = null;
			} else {
				bucket3_35 = (bucket3_35 / totalValue) * 100;
			}
			if (bucket35_4 == 0.0) {
				bucket35_4 = null;
			} else {
				bucket35_4 = (bucket35_4 / totalValue) * 100;
			}
			if (bucket4_45 == 0.0) {
				bucket4_45 = null;
			} else {
				bucket4_45 = (bucket4_45 / totalValue) * 100;
			}
			if (bucket45_5 == 0.0) {
				bucket45_5 = null;
			} else {
				bucket45_5 = (bucket45_5 / totalValue) * 100;
			}
			//System.out.println("bucket15_2-----leter---" + bucket15_2);
			paramsValue.add(bucket1_15);
			paramsValue.add((bucket15_2));
			paramsValue.add((bucket2_25));
			paramsValue.add((bucket25_3));
			paramsValue.add((bucket3_35));
			paramsValue.add((bucket35_4));
			paramsValue.add((bucket4_45));
			paramsValue.add((bucket45_5));
			//System.out.println("paramsValue--------"+paramsValue);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		}
		// //System.out.println("paramsValue for line graph :"+paramsValue);
		return paramsValue;
	}
	
	public String getchartType(String testName,String marketId){
		String chartType = "";
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = "SELECT * FROM pre_cal_voicequality_2 WHERE TEST_NAME ='"+testName+"' AND MARKET_NAME='"+marketId+"'";
		try{
			conn = DBUtil.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while(rs.next()){
				String parmameterName = rs.getString("PARAMETER_NAME");
				if(parmameterName.equalsIgnoreCase("pesq")){
					chartType = parmameterName;
				}
				if(parmameterName.equalsIgnoreCase("polqa")){
					chartType = parmameterName;
				}
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return chartType;
	}

	public static void main(String[] args) {
		new VoiceQualityDaoImpl().getVQDetailsFor_LineForGraphForPolqa("Nexus 4","3","Nexusfinal0816");
	}
}
