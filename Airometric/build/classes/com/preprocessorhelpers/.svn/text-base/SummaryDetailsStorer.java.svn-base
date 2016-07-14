package com.preprocessorhelpers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.model.DBUtil;
import com.report.to.SummaryVoiceReportTo;

public class SummaryDetailsStorer {

	public static SummaryVoiceReportTo insertVoiceSummaryData(String test_name,
			String marketId) {
		Connection conn = null;
		Statement stmt = null;
		SummaryVoiceReportTo svrTo = new SummaryVoiceReportTo();
		String query = "SELECT DI.TIME_STAMP_FOREACH_SAMPLE AS TEST_TIMESTAMP,DI.MARKET_ID,DI.USER_NAME AS USERNAME,VC.*,VQ.PARAMETER_NAME AS VQPARAM,VQ.CHART_TYPE AS VQCHART_TYPE,VQ.PARAMETER_VALUE AS VQPARAMETER_VALUE,"
				+ "VQ.MAX_PARAM_VALUE AS VQMAX_PARAM_VALUE FROM PRE_CAL_CALLRETENTION VC , ("
				+ "SELECT TIME_STAMP_FOREACH_SAMPLE,MARKET_ID,USER_NAME FROM STG_DEVICE_INFO WHERE MARKET_ID = '"+marketId+"' "
				+ "  AND TEST_NAME LIKE '%"+test_name+"%' ORDER BY TIME_STAMP_FOREACH_SAMPLE LIMIT 1 ) DI,"
				+ "PRE_CAL_VOICEQUALITY_2 VQ WHERE VC.TEST_NAME = VQ.TEST_NAME AND"
				+ " VC.TEST_NAME ='"
				+ test_name
				+ "' AND VC.MARKET_ID ='"
				+ marketId + "' AND VC.MARKET_ID =DI.MARKET_ID";
		try {
			//System.outprintln(query);
			conn = DBUtil.getConnection();
			stmt = conn.createStatement();
			//System.outprintln(query);
			ResultSet rs = stmt.executeQuery(query);
			//System.outprintln(query);
			svrTo.setTestName(test_name);
			svrTo.setMarketName(marketId);
			while (rs.next()) {
				//System.outprintln("in res");
				String vqparam = rs.getString("VQPARAM");
				String chartType = rs.getString("VQCHART_TYPE");
				String vqparamValue = rs.getString("VQPARAMETER_VALUE");
				String maxParamvalue = rs.getString("VQMAX_PARAM_VALUE");
				String param = rs.getString("PARAMETER");
				String paramValue = rs.getString("PARAMETER_VALUE");
				String date = rs.getString("TEST_TIMESTAMP");
				String userName = rs.getString("USERNAME");
				
				if (param.equalsIgnoreCase("NEW OUTGOING CALL")) {
					svrTo.setTotalCalls(paramValue);
				}
				if (param.equalsIgnoreCase("access failure")) {
					svrTo.setCallfailure(paramValue);
				}
				if (param.equalsIgnoreCase("dropcalls")) {
					svrTo.setDropcalls(paramValue);
				}
				if (param.equalsIgnoreCase("missed calls")) {
					svrTo.setMissedCalls(paramValue);
				}
				if (param.equalsIgnoreCase("dcr")) {
					svrTo.setDcr(paramValue);
				}
				if ((vqparam.equalsIgnoreCase("polqa") || vqparam
						.equalsIgnoreCase("pesq"))
						&& chartType.equalsIgnoreCase("downlink")) {
					svrTo.setAvg_downlinkMOS(vqparamValue);
					svrTo.setMax_downlinkMOS(maxParamvalue);
				}
				if ((vqparam.equalsIgnoreCase("polqa") || vqparam
						.equalsIgnoreCase("pesq"))
						&& chartType.equalsIgnoreCase("uplink")) {
					svrTo.setAvg_uplinkMOS(vqparamValue);
					svrTo.setMax_uplinkMOS(maxParamvalue);
				}
				svrTo.setDate(date);
				svrTo.setUserName(userName);
			}
			
			query = "INSERT INTO voice_summary_table VALUES ('"
					+ svrTo.getTestName() + "', '" + svrTo.getMarketName()
					+ "', '" + svrTo.getTotalCalls() + "', '"
					+ svrTo.getCallfailure() + "', '" + svrTo.getDropcalls() 
					+ "','"+svrTo.getMissedCalls()+"', '" + svrTo.getDcr() + "', '"
					+ svrTo.getAvg_downlinkMOS() + "', '"
					+ svrTo.getAvg_uplinkMOS() + "', '"
					+ svrTo.getMax_downlinkMOS() + "', '"
					+ svrTo.getMax_uplinkMOS() + "', '"+svrTo.geteModel()+"','"+svrTo.getDate()+"','"+svrTo.getUserName()+"' );";
//			System.out.println(query);
			stmt.executeUpdate(query);
//			System.out.println("svrTo------" + svrTo.getCallfailure());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}
		}
		return svrTo;
	}

	public SummaryVoiceReportTo getVoiceSummary(String testName, String marketId) {
		SummaryVoiceReportTo summaryVoiceReportTo = insertVoiceSummaryData(
				testName, marketId);
		return summaryVoiceReportTo;
	}
}
