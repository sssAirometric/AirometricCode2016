package com.report.helper;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;

import com.model.DBUtil;
import com.to.HealthIndexTo;
import com.to.QualityRangeTo;
import com.util.DateUtil;

public class ReportConfigHelper {

	public boolean saveHealthIndicies(
			HashMap<String, HashMap<String, List<HealthIndexTo>>> kpiHealthIndexScoreMap,
			int configId , Integer userId) {
//		System.out.println("kpiHealthIndexScoreMap----------"
//				+ kpiHealthIndexScoreMap);
		boolean isHealthSaved = false;
		Iterator<String> testNameItr = kpiHealthIndexScoreMap.keySet()
				.iterator();
		Connection conn = null;
		try {
			String date = DateUtil.getCurrentDateTime();
			conn = DBUtil.getConnection();
			Statement stmt = conn.createStatement();
			String insertKPIScoreSql = "";
			String valueSummary = "";
			while (testNameItr.hasNext()) {
				String testName = testNameItr.next();
				HashMap<String, List<HealthIndexTo>> marketWiseScoresMap = kpiHealthIndexScoreMap
						.get(testName);

				Iterator<String> marketNameItr = marketWiseScoresMap.keySet()
						.iterator();
				while (marketNameItr.hasNext()) {
					String marketName = marketNameItr.next();
					HealthIndexTo healthTo = marketWiseScoresMap
							.get(marketName).get(0);
					int kpiId = 1;
					if(null!=healthTo.getVoicequalityHealthIndex()){
						if(healthTo.getVqAvgValue().toString().equals("NaN")){
							valueSummary = "0";
						}else{
							valueSummary = healthTo.getVqAvgValue();
						}
						insertKPIScoreSql = "INSERT INTO configuration_score (CONFIG_ID,KPI_ID,SCORE,TEST_NAME,MARKET_NAME,VALUE_SUMMARY," +
								"CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE)"
								+ "	VALUES('"
								+ configId
								+ "','"
								+ kpiId
								+ "','"
								+ healthTo.getVoicequalityHealthIndex()
								+ "','"
								+ testName
								+ "','"
								+ healthTo.getMarketName()
								+ "','" + valueSummary 
								+ "','"
								+ userId
								+ "','"
								+ date
								+ "','"
								+ userId
								+ "','"
								+ date
								+ "')";
//						System.out.println("insertKPIScoreSql=======voice===="
//								+ insertKPIScoreSql);
						stmt.executeUpdate(insertKPIScoreSql);
					}
					kpiId = 2;
					if(null!=healthTo.getVoiceConnectivityHealthIndex()){	
						if(healthTo.getVcAvgValue().toString().equals("NaN")){
							valueSummary = "0";
						}else{
							valueSummary = healthTo.getVcAvgValue();
						}
						insertKPIScoreSql = "INSERT INTO configuration_score (CONFIG_ID,KPI_ID,SCORE,TEST_NAME,MARKET_NAME,VALUE_SUMMARY," +
								"CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE)"
								+ "	VALUES('"
								+ configId
								+ "','"
								+ kpiId
								+ "','"
								+ healthTo.getVoiceConnectivityHealthIndex()
								+ "','"
								+ testName
								+ "','"
								+ healthTo.getMarketName()
								+ "','"
								+ valueSummary 
								+ "','"
								+ userId
								+ "','"
								+ date
								+ "','"
								+ userId
								+ "','"
								+ date
								+ "')";
//						System.out
//								.println("insertKPIScoreSql=====vc======" + insertKPIScoreSql);
						stmt.executeUpdate(insertKPIScoreSql);
					}
					kpiId = 3;
					if(null!=healthTo.getDataConnectivityHealthIndex()){
						if(healthTo.getDcAvgValue().toString().equals("NaN")){
							valueSummary = "0";
						}else{
							valueSummary = healthTo.getDcAvgValue();
						}
						insertKPIScoreSql = "INSERT INTO configuration_score (CONFIG_ID,KPI_ID,SCORE,TEST_NAME,MARKET_NAME,VALUE_SUMMARY," +
								"CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE)"
								+ "	VALUES('"
								+ configId
								+ "','"
								+ kpiId
								+ "','"
								+ healthTo.getDataConnectivityHealthIndex()
								+ "','"
								+ testName
								+ "','"
								+ healthTo.getMarketName()
								+ "','" + valueSummary
								+ "','"
								+ userId
								+ "','"
								+ date
								+ "','"
								+ userId
								+ "','"
								+ date
								+ "')";							
//						System.out.println("insertKPIScoreSql=====data======"
//								+ insertKPIScoreSql);
						stmt.executeUpdate(insertKPIScoreSql);
					}
					kpiId = 4;
					if(null!=healthTo.getVoiceDataHealthIndex()){
						if(healthTo.getVdAvgValue().toString().equals("NaN")){
							valueSummary = "0";
						}else{
							valueSummary = healthTo.getVdAvgValue();
						}
						insertKPIScoreSql = "INSERT INTO configuration_score (CONFIG_ID,KPI_ID,SCORE,TEST_NAME,MARKET_NAME,VALUE_SUMMARY," +
								"CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE)"
								+ "	VALUES('"
								+ configId
								+ "','"
								+ kpiId
								+ "','"
								+ healthTo.getVoiceDataHealthIndex()
								+ "','"
								+ testName
								+ "','"
								+ healthTo.getMarketName()
								+ "','" + valueSummary 
								+ "','"
								+ userId
								+ "','"
								+ date
								+ "','"
								+ userId
								+ "','"
								+ date
								+ "')";							
//						System.out.println("insertKPIScoreSql=====voice and data======"
//								+ insertKPIScoreSql);
						stmt.executeUpdate(insertKPIScoreSql);
					}
					kpiId = 5;
					if(null!=healthTo.getLteHealthIndex()){		
						if(healthTo.getDcLteAvgValue().toString().equals("NaN")){
							valueSummary = "0";
						}else{
							valueSummary = healthTo.getDcLteAvgValue();
						}
						insertKPIScoreSql = "INSERT INTO configuration_score (CONFIG_ID,KPI_ID,SCORE,TEST_NAME,MARKET_NAME,VALUE_SUMMARY," +
								"CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE)"
								+ "	VALUES('"
								+ configId
								+ "','"
								+ kpiId
								+ "','"
								+ healthTo.getLteHealthIndex()
								+ "','"
								+ testName
								+ "','"
								+ healthTo.getMarketName()
								+ "','"
								+ valueSummary
								+ "','"
								+ userId
								+ "','"
								+ date
								+ "','"
								+ userId
								+ "','"
								+ date
								+ "')";							
//						System.out.println("insertKPIScoreSql=====lte======"
//								+ insertKPIScoreSql);
						stmt.executeUpdate(insertKPIScoreSql);
					}
					kpiId = 6;
					if(null!=healthTo.getBrowserHealthIndex()){	
						if(healthTo.getBrowserAvgValue().toString().equals("NaN")){
							valueSummary = "0";
						}else{
							valueSummary = healthTo.getBrowserAvgValue();
						}
						insertKPIScoreSql = "INSERT INTO configuration_score (CONFIG_ID,KPI_ID,SCORE,TEST_NAME,MARKET_NAME,VALUE_SUMMARY," +
								"CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE)"
								+ "	VALUES('"
								+ configId
								+ "','"
								+ kpiId
								+ "','"
								+ healthTo.getBrowserHealthIndex()
								+ "','"
								+ testName
								+ "','"
								+ healthTo.getMarketName()
								+ "','" + valueSummary 
								+ "','"
								+ userId
								+ "','"
								+ date
								+ "','"
								+ userId
								+ "','"
								+ date
								+ "')";							
//						System.out.println("insertKPIScoreSql=====browser======"
//								+ insertKPIScoreSql);
						stmt.executeUpdate(insertKPIScoreSql);
					}
					kpiId = 7;
					if(null!=healthTo.getVoipHealthIndex()){	
						if(healthTo.getVoipAvgValue().toString().equals("NaN")){
							valueSummary = "0";
						}else{
							valueSummary = healthTo.getVoipAvgValue();
						}
						insertKPIScoreSql = "INSERT INTO configuration_score (CONFIG_ID,KPI_ID,SCORE,TEST_NAME,MARKET_NAME,VALUE_SUMMARY," +
								"CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE)"
								+ "	VALUES('"
								+ configId
								+ "','"
								+ kpiId
								+ "','"
								+ healthTo.getVoipHealthIndex()
								+ "','"
								+ testName
								+ "','"
								+ healthTo.getMarketName()
								+ "','"
								+ valueSummary 
								+ "','"
								+ userId
								+ "','"
								+ date
								+ "','"
								+ userId
								+ "','"
								+ date
								+ "')";							
//						System.out.println("insertKPIScoreSql=====voip======"
//								+ insertKPIScoreSql);
						stmt.executeUpdate(insertKPIScoreSql);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return isHealthSaved;
	}

	public void saveQualityRangesForConfig(Integer savedTestConfigId){
		FacesContext context = FacesContext.getCurrentInstance();
		QualityRangeTo qtoLte = (QualityRangeTo)context.getExternalContext().getSessionMap().get("qualityLteTo");
		QualityRangeTo qto = (QualityRangeTo)context.getExternalContext().getSessionMap().get("qualityTo");
		Connection conn = DBUtil.getConnection();
		try{
			Statement stmt = conn.createStatement();
			String inserLtegoodQuery = "INSERT INTO configsaved_quality_range(NETWORK_TYPE,QUALITY_NAME,MIN_VALUE,MAX_VALUE,CONFIG_ID)" +
			" VALUES ( 'LTE', 'GOOD', '"+qtoLte.getLowerRangeForGood()+"', '"+qtoLte.getUpperRangeForGood()+"', '"+savedTestConfigId+"')";
			String inserLteAvgQuery = "INSERT INTO configsaved_quality_range(NETWORK_TYPE,QUALITY_NAME,MIN_VALUE,MAX_VALUE,CONFIG_ID)" +
			" VALUES ( 'LTE', 'AVERAGE','"+qtoLte.getLowerRangeForAvg()+"', '"+qtoLte.getUpperRangeForAvg()+"','"+savedTestConfigId+"')";
			String inserLtePoorQuery = "INSERT INTO configsaved_quality_range(NETWORK_TYPE,QUALITY_NAME,MIN_VALUE,MAX_VALUE,CONFIG_ID)" +
			" VALUES ('LTE', 'POOR', '"+qtoLte.getLowerRangeForPoor()+"', '"+qtoLte.getUpperRangeForPoor()+"', '"+savedTestConfigId+"')";
			String inserGoodQuery = "INSERT INTO configsaved_quality_range(NETWORK_TYPE,QUALITY_NAME,MIN_VALUE,MAX_VALUE,CONFIG_ID)" +
			" VALUES ('NONLTE', 'GOOD','"+qto.getLowerRangeForGood()+"', '"+qto.getUpperRangeForGood()+"', '"+savedTestConfigId+"')";
			String inserAvgQuery = "INSERT INTO configsaved_quality_range(NETWORK_TYPE,QUALITY_NAME,MIN_VALUE,MAX_VALUE,CONFIG_ID)" +
			" VALUES ( 'NONLTE', 'AVERAGE','"+qto.getLowerRangeForAvg()+"', '"+qto.getUpperRangeForAvg()+"','"+savedTestConfigId+"')";
			String inserPoorQuery = "INSERT INTO configsaved_quality_range(NETWORK_TYPE,QUALITY_NAME,MIN_VALUE,MAX_VALUE,CONFIG_ID)" +
			" VALUES ( 'NONLTE', 'POOR','"+qto.getLowerRangeForPoor()+"', '"+qto.getUpperRangeForPoor()+"', '"+savedTestConfigId+"')";
//			System.out.println("inserLtegoodQuery-----------"+inserLtegoodQuery);
			stmt.executeUpdate(inserLtegoodQuery);
//			System.out.println("inserLteAvgQuery-----------"+inserLteAvgQuery);
			stmt.executeUpdate(inserLteAvgQuery);
			stmt.executeUpdate(inserLtePoorQuery);
			stmt.executeUpdate(inserGoodQuery);
			stmt.executeUpdate(inserAvgQuery);
			stmt.executeUpdate(inserPoorQuery);
			
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
