package com.preprocessing.reports.summary;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.json.me.JSONArray;
import org.json.me.JSONObject;

import com.model.DBUtil;
import com.report.to.SummaryVoiceReportTo;
import com.report.to.VoiceQualityTo;

public class PreprocessorSummaryReportGenerator {

	public void generateReport() {

	}

	public static SummaryVoiceReportTo getData(String test_name, String marketId) {
		Connection conn = null;
		Statement stmt = null;
		SummaryVoiceReportTo svrTo = new SummaryVoiceReportTo();
		String query = "SELECT VC.*,VQ.PARAMETER_NAME AS VQPARAM,VQ.CHART_TYPE AS VQCHART_TYPE,VQ.PARAMETER_VALUE AS VQPARAMETER_VALUE,VQ.MAX_PARAM_VALUE AS VQMAX_PARAM_VALUE FROM PRE_CAL_CALLRETENTION VC , "
				+ "PRE_CAL_VOICEQUALITY_2 VQ WHERE VC.TEST_NAME = VQ.TEST_NAME AND"
				+ " VC.TEST_NAME ='"
				+ test_name
				+ "' AND VC.MARKET_ID ='"
				+ marketId + "' ";
		try {
			conn = DBUtil.getConnection();
			stmt = conn.createStatement();
//			System.out.println(query);
			ResultSet rs = stmt.executeQuery(query);
//			System.out.println(query);
			svrTo.setTestName(test_name);
			svrTo.setMarketName(marketId);
			while (rs.next()) {
//				System.out.println("in res");
				String vqparam = rs.getString("VQPARAM");
				String chartType = rs.getString("VQCHART_TYPE");
				String vqparamValue = rs.getString("VQPARAMETER_VALUE");
				String maxParamvalue = rs.getString("VQMAX_PARAM_VALUE");
				String param = rs.getString("PARAMETER");
				String paramValue = rs.getString("PARAMETER_VALUE");
				
				if (param.equalsIgnoreCase("totalcalls")) {
					svrTo.setTotalCalls(paramValue);
				}						    
				if (param.equalsIgnoreCase("access failure")) {
					svrTo.setCallfailure(paramValue);
				}
				if (param.equalsIgnoreCase("dropcalls")) {
					svrTo.setDropcalls(paramValue);
				}
				if (param.equalsIgnoreCase("dcr")) {
					svrTo.setDcr(paramValue);
				}
				
				if(param.equalsIgnoreCase("missed calls")){
					svrTo.setMissedCalls(paramValue);
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
			}
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
	
	
	public static List<SummaryVoiceReportTo> getDataForSummaryOfUser(String userName) {
		Connection conn = null;
		Statement stmt = null;
		SummaryVoiceReportTo svrTo = new SummaryVoiceReportTo();
		List<SummaryVoiceReportTo> summaryToList  = new ArrayList<SummaryVoiceReportTo>();
		String query = "SELECT * FROM voice_summary_table WHERE USER_ID = '"+userName+"'";
		try {
			conn = DBUtil.getConnection();
			stmt = conn.createStatement();
//			System.out.println(query);
			ResultSet rs = stmt.executeQuery(query);
//			System.out.println(query);
			
			while (rs.next()) {
//				System.out.println("in res");
				
					svrTo.setTotalCalls(rs.getString("TOTALCALLS"));
					svrTo.setCallfailure(rs.getString("CALL_FAILURE"));
					svrTo.setDropcalls(rs.getString("DROP_CALLS"));
					svrTo.setMissedCalls(rs.getString("MISSED_CALLS"));
					svrTo.setDcr(rs.getString("DCR"));
					svrTo.setAvg_downlinkMOS(rs.getString("AVG_DL_MOS"));
					svrTo.setMax_downlinkMOS(rs.getString("MAX_DL_MOS"));
					svrTo.setAvg_uplinkMOS(rs.getString("AVG_UL_MOS"));
					svrTo.setMax_uplinkMOS(rs.getString("MAX_UL_MOS"));
					svrTo.seteModel(rs.getString("MAX_UL_MOS"));
					svrTo.setDate(rs.getString("DATE"));
					svrTo.setUserName(rs.getString("USER_ID"));
					svrTo.setTestName(rs.getString("TEST_NAME"));
					svrTo.setMarketName(rs.getString("MARKET_ID"));
					summaryToList.add(svrTo);
			}
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
		return summaryToList;
	}

	public static File createFile(String filePath) {
		File exlFile = null;
		try {
			exlFile = new File(filePath);
			if (exlFile.exists()) {
				exlFile.delete();
			}
			exlFile = new File(filePath);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return exlFile;
	}

	public static void instantiateReport(File exlFile) {
		List<String> headerList = new ArrayList<String>();
		headerList.add("Test name");
		headerList.add("Market Name");
		headerList.add("totalCalls");
		headerList.add("Call Failure");
		headerList.add("Drop Calls");
		headerList.add("Missed Calls");
		headerList.add("DCR %");
		headerList.add("Avg. DL MOS ");
		headerList.add("Avg UL MOS ");
		headerList.add("Max DL MOS  ");
		headerList.add("Max UL MOS ");
		headerList.add("E-Model");
		headerList.add("Date");
		// System.out.println("hiiii");
		RenderSummaryReportHelper.instantiateFonts(exlFile, "Summary", 0);
		// System.out.println("nooooooo");
		RenderSummaryReportHelper.createHeaderLabels(headerList);
		// List<VoiceQualityTo> vqList = getPreVoiceData(testNames);
		// renderReport(vqList);
	}

	public static void renderReport(String test_name, String marketId) {
		try {

			SummaryVoiceReportTo summaryVoiceReportTo = getData(test_name,
					marketId);
			writeToExcel(summaryVoiceReportTo);
//			System.out
//					.println("RenderSummaryReportHelper.writableWorkbook-----"
//							+ RenderSummaryReportHelper.writableWorkbook);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static void renderSumamryReport(SummaryVoiceReportTo summaryVoiceReportTo) {
		try {
			writeToExcel(summaryVoiceReportTo);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private static void writeToExcel(SummaryVoiceReportTo summaryVoiceReportTo) {
		int colIndex = 1;
		RenderSummaryReportHelper.writeToCell(summaryVoiceReportTo
				.getTestName(), RenderSummaryReportHelper.rowIndex, colIndex,
				RenderSummaryReportHelper.cellFormat1);
		colIndex++;
		RenderSummaryReportHelper.writeToCell(summaryVoiceReportTo
				.getMarketName(), RenderSummaryReportHelper.rowIndex, colIndex,
				RenderSummaryReportHelper.cellFormat1);
		colIndex++;
		RenderSummaryReportHelper.writeToCell(summaryVoiceReportTo
				.getTotalCalls(), RenderSummaryReportHelper.rowIndex, colIndex,
				RenderSummaryReportHelper.cellFormat1);
		colIndex++;
		RenderSummaryReportHelper.writeToCell(summaryVoiceReportTo
				.getCallfailure(), RenderSummaryReportHelper.rowIndex,
				colIndex, RenderSummaryReportHelper.cellFormat1);
		colIndex++;
		RenderSummaryReportHelper.writeToCell(summaryVoiceReportTo
				.getDropcalls(), RenderSummaryReportHelper.rowIndex, colIndex,
				RenderSummaryReportHelper.cellFormat1);
		colIndex++;
		RenderSummaryReportHelper.writeToCell(summaryVoiceReportTo
				.getMissedCalls(), RenderSummaryReportHelper.rowIndex,
				colIndex, RenderSummaryReportHelper.cellFormat1);
		colIndex++;
		RenderSummaryReportHelper.writeToCell(summaryVoiceReportTo.getDcr(),
				RenderSummaryReportHelper.rowIndex, colIndex,
				RenderSummaryReportHelper.cellFormat1);
		colIndex++;
		RenderSummaryReportHelper.writeToCell(summaryVoiceReportTo
				.getAvg_downlinkMOS(), RenderSummaryReportHelper.rowIndex,
				colIndex, RenderSummaryReportHelper.cellFormat1);
		colIndex++;
		RenderSummaryReportHelper.writeToCell(summaryVoiceReportTo
				.getAvg_uplinkMOS(), RenderSummaryReportHelper.rowIndex,
				colIndex, RenderSummaryReportHelper.cellFormat1);
		colIndex++;
		RenderSummaryReportHelper.writeToCell(summaryVoiceReportTo
				.getMax_downlinkMOS(), RenderSummaryReportHelper.rowIndex,
				colIndex, RenderSummaryReportHelper.cellFormat1);
		colIndex++;
		RenderSummaryReportHelper.writeToCell(summaryVoiceReportTo
				.getMax_uplinkMOS(), RenderSummaryReportHelper.rowIndex,
				colIndex, RenderSummaryReportHelper.cellFormat1);
		colIndex++;
		RenderSummaryReportHelper.writeToCell(summaryVoiceReportTo
				.geteModel(), RenderSummaryReportHelper.rowIndex,
				colIndex, RenderSummaryReportHelper.cellFormat1);
		colIndex++;
		RenderSummaryReportHelper.writeToCell(summaryVoiceReportTo
				.getDate(), RenderSummaryReportHelper.rowIndex,
				colIndex, RenderSummaryReportHelper.cellFormat1);
		colIndex++;
	}

	public static String triggerSummaryVoiceReps(String jsoStr) {
		Locale currentLocale = new Locale("es");
		ResourceBundle resourceBundle = ResourceBundle.getBundle("Resource",
				currentLocale);
		String folder = resourceBundle.getString("DOWNLOAD_FOLDER");
		String filePath = folder + "\\Summary\\VoiceSummaryReport.xls";
		;
		try {
//			System.out.println("jsoStr-------" + jsoStr);
			org.json.me.JSONArray jsoArr = new JSONArray(jsoStr);

			File file = createFile(filePath);
			instantiateReport(file);
			for (int j = 0; j < jsoArr.length(); j++) {
				org.json.me.JSONObject configObi = new org.json.me.JSONObject(
						jsoArr.get(j).toString());
				String lConfigRouteName = configObi.getString("testName");
				String lMarketId = configObi.getString("marketName").replace(
						"[", "").replace("]", "");
				String[] marketIdArr = lMarketId.split(",");
				for (String marketId : marketIdArr) {
					renderReport(lConfigRouteName, marketId);
					RenderSummaryReportHelper.rowIndex++;
					RenderSummaryReportHelper.colIndex = 1;
				}
			}
			RenderSummaryReportHelper.writableWorkbook.write();
			RenderSummaryReportHelper.writableWorkbook.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return filePath;
	}
	
	
	public static String triggerSummaryReps(String username) {
		Locale currentLocale = new Locale("es");
		ResourceBundle resourceBundle = ResourceBundle.getBundle("Resource",
				currentLocale);
		String folder = resourceBundle.getString("DOWNLOAD_FOLDER");
		String filePath = folder + "\\Summary\\SummaryReport.xls";
		;
		try {

			File file = createFile(filePath);
			instantiateReport(file);
			List<SummaryVoiceReportTo> summaryVoiceReportToList =  getDataForSummaryOfUser(username);
				for (SummaryVoiceReportTo summaryVoiceReportTo : summaryVoiceReportToList) {
					renderSumamryReport(summaryVoiceReportTo);
					RenderSummaryReportHelper.rowIndex++;
					RenderSummaryReportHelper.colIndex = 1;
				}
			RenderSummaryReportHelper.writableWorkbook.write();
			RenderSummaryReportHelper.writableWorkbook.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return filePath;
	}

	public static void main(String[] args) {
		String jsoStr = "[{'deviceName':'LG-D725','testName':'D1CHV0707','marketName':[24],'marketLabel':['Chicago'],'reportType':1,'filesName':['LG-D725_mo_Chicago_2014-07-03 11__--__11__--__00.140_014071000014980'],'filesNameId':[352]},{'deviceName':'LG-D725','testName':'D3CHM0703','marketName':[24],'marketLabel':['Chicago'],'reportType':1,'filesName':['LG-D725_mo_Chicago_2014-07-03 11__--__11__--__04.272_014071000016522'],'filesNameId':[367]}]";
		triggerSummaryVoiceReps(jsoStr);
	}
}
