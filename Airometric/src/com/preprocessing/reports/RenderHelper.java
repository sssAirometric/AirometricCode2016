package com.preprocessing.reports;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import com.model.DBUtil;
import com.preprocessorhelpers.VoiceConnectivityProccesorHelper2;

public class RenderHelper {

	static WritableFont cellFont1 = null;
	static WritableCellFormat cellFormat1 = null;
	static WritableCellFormat cellFormat = null;
	static WritableFont cellFont = null;
	static WritableSheet writableSheet1 = null;
	static WritableWorkbook writableWorkbook = null;
	static int rowIndex = 1;
	static int colIndex = 1;
	static int sheetNumber = 0;

	public static void renderTableInExcel(String tableName,String testname,String condition,String marketIds) {
		String query = "";
		if(condition.equalsIgnoreCase("allvc")){
			query = "SELECT * FROM " + tableName +" WHERE CHART_TYPE NOT IN ('Placing Call','Connected','callsetup')";
		}
		else if(condition.equalsIgnoreCase("vc")){
			query = "SELECT * FROM " + tableName +" WHERE CHART_TYPE  IN ('Placing Call','Connected','callsetup')";
		}
		else if(condition.equalsIgnoreCase("callRetention"))
			{
				query = "SELECT * FROM "+tableName+" WHERE PARAMETER IN ('Missed Call','Call Dropped')";
			}
		else if(condition.equalsIgnoreCase("")){
			 query = "SELECT * FROM " + tableName;
		}
//		System.out.println(query);
		Connection conn = DBUtil.openConn();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			ResultSetMetaData meta = rs.getMetaData();
			Integer columncount = meta.getColumnCount();
			int count = 1; // start counting from 1 always
			String[] columnNames = new String[columncount];
			while (count <= columncount) {
				columnNames[count - 1] = meta.getColumnLabel(count);
				count++;
			}
			
			List<List<String>> allRowsData = getDataToRender(testname, columnNames,tableName,condition);
			createHeaderLabels(columnNames);
			renderReport(allRowsData);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	public static void createHeaderLabels(String[] labelList) {
		try {
			// System.out.println("labelList00000"+labelList);
//			System.out.println(labelList.length);
			for (String labelName : labelList) {
				writableSheet1.setColumnView(1, 25);
				Label label = new Label(colIndex, rowIndex, labelName,
						cellFormat);
				writableSheet1.addCell(label);
				colIndex++;
			}
		} catch (RowsExceededException e) {
			// TODO: handle exception
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rowIndex++;
	}

	public static void preProcessVCReport(String filePath, String testNames) {

		List<String> labelList = new ArrayList<String>();
		try {
			File exlFile = new File(filePath);
			if (exlFile.exists()) {
				exlFile.delete();
			}
			exlFile = new File(filePath);
			writableWorkbook = Workbook.createWorkbook(exlFile);
			// System.out.println("hiiii");
			// instantiateFonts(exlFile,"CallSetup");
			// System.out.println("nooooooo");

			// List<VoiceConnectivityTO> vqList = getPreVoiceData(testNames);
			// renderReport(vqList);
			PreprocessingVCCallRetentionReport.renderRententionReport(filePath,
					testNames);
			writableWorkbook.write();
			writableWorkbook.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	static void instantiateFonts(File exlFile, String sheetTitle) {
		try {
//			System.out.println("sheetNumber--------------"+sheetNumber);
			
			rowIndex = 1;
			colIndex = 1;
			writableSheet1 = writableWorkbook.createSheet(sheetTitle, sheetNumber);
			// System.out.println("writableSheet1----------"+writableSheet1);
			cellFont = new WritableFont(WritableFont.ARIAL, 12);
			cellFont.setBoldStyle(WritableFont.BOLD);
			cellFont.setColour(Colour.BLACK);

			cellFont1 = new WritableFont(WritableFont.ARIAL, 10);
			cellFont.setColour(Colour.BLACK);

			cellFormat = new WritableCellFormat(cellFont);
			cellFormat.setShrinkToFit(true);
			cellFormat.setBackground(Colour.GRAY_50);
			cellFormat
					.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
			cellFormat.setAlignment(Alignment.CENTRE);
			cellFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			cellFormat.setWrap(true);

			cellFormat1 = new WritableCellFormat(cellFont1);
			cellFormat1.setShrinkToFit(true);
			cellFormat1.setBorder(Border.ALL, BorderLineStyle.THIN,
					Colour.BLACK);
			cellFormat1.setAlignment(Alignment.CENTRE);
			cellFormat1.setVerticalAlignment(VerticalAlignment.CENTRE);
			cellFormat1.setWrap(true);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		sheetNumber++;
	}
	
	static void renderReport(List<List<String>> voiceDataList) {
		for (List<String> dataList : voiceDataList) {
			int colIndex = 1;
			for (String colValue : dataList) {
				writeToCell(colValue, rowIndex, colIndex, cellFormat1);
				colIndex++;
			}
			rowIndex++;
		}
	}
	static void writeToCell(String value,int rowInd,int colInd,WritableCellFormat cellFormat){
		try{
			writableSheet1.setColumnView(1, 25);
			Label label = new Label(colInd,rowInd, value, cellFormat);
			writableSheet1.addCell(label);
		}
		catch (RowsExceededException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static List<List<String>> getDataToRender(String testNames,String[] colNames,String tableName,String condition){
		List<List<String>> allRowValuesList = new ArrayList<List<String>>();
		List<String> rowvalues = new ArrayList<String>();
		String query = "SELECT PC.*,M.MARKET_NAME FROM "+tableName+" PC,MARKET M WHERE PC.TEST_NAME IN ('"+testNames+"') AND M.MARKET_ID = PC.MARKET_ID";
		if(condition.equalsIgnoreCase("allvc")){
			query = "SELECT PC.*,M.MARKET_NAME FROM "+tableName+" PC,MARKET M WHERE PC.TEST_NAME IN ('"+testNames+"') AND M.MARKET_ID = PC.MARKET_ID AND PC.CHART_TYPE NOT IN ('Placing Call','Connected','callsetup')";
		}
		else if(condition.equalsIgnoreCase("vc")){
			query = "SELECT * FROM " + tableName +"  PC WHERE PC.TEST_NAME IN ('"+testNames+"') AND PC.CHART_TYPE  IN ('Placing Call','Connected','callsetup') ";
		}
		else if(condition.equalsIgnoreCase("callRetention")){
			query = "SELECT distinct PC.*,M.MARKET_NAME FROM "+tableName+" PC,MARKET M WHERE PC.TEST_NAME IN ('"+testNames+"') AND M.MARKET_ID = PC.MARKET_ID and PARAMETER IN ('Missed Call','Call Dropped')";
		}else{
			query = "SELECT PC.*,M.MARKET_NAME FROM "+tableName+" PC,MARKET M WHERE PC.TEST_NAME IN ('"+testNames+"') AND M.MARKET_ID = PC.MARKET_ID";
		}
		Connection conn = DBUtil.getConnection();
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			System.out.println(query);
			while(rs.next()){
				rowvalues = new ArrayList<String>();
				for(String colName:colNames){
					rowvalues.add(rs.getString(colName));
				}
				allRowValuesList.add(rowvalues);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return allRowValuesList;
	}
	public static void getReport(String testName,String reportType,String filePath,String allMarkets) {
	    Locale currentLocale = new Locale("es");
		ResourceBundle resourceBundle = ResourceBundle.getBundle("Resource",currentLocale);
		String folder=resourceBundle.getString("DOWNLOAD_FOLDER");
		 filePath = folder+"\\Precalculation\\PreCalculationInfo.xls";;
//		System.out.println(filePath);
		File exlFile = new File(filePath);
		if(exlFile.exists()){
			exlFile.delete();
		}
		exlFile = new File(filePath);
		try{
			writableWorkbook = Workbook.createWorkbook(exlFile);
			VoiceConnectivityProccesorHelper2 vc2 = new VoiceConnectivityProccesorHelper2();
			
			if(reportType.equalsIgnoreCase("VC")){
				instantiateFonts(exlFile,"Call retention");
				renderTableInExcel("pre_cal_callretention_1",testName,"callRetention",allMarkets);
				instantiateFonts(exlFile,"Completed Calls");
				renderTableInExcel("pre_calc_voiceconnectivity_1",testName,"vc",allMarkets);
				instantiateFonts(exlFile,"All the Details");
				
				testName = testName.replaceAll("\'", "");
				String temp[] = testName.trim().split(",");
				String testType = "";
				for(int i=0;i<temp.length;i++)
				{
					testType = vc2.getTestType(temp[i]);
					if(testType.equalsIgnoreCase("mo"))
						renderTableInExcel("pre_calc_voiceconnectivity_1",temp[i],"allvc",allMarkets);
					else
						renderTableInExcel("pre_calc_voiceconnectivity_2",temp[i],"",allMarkets);
				}
				
				//Code Added By Ankit on 13/04/16
				instantiateFonts(exlFile,"TCP");
				renderTableInExcel("pre_calculation_TCP_level1",testName,"","");
				instantiateFonts(exlFile,"UDP");
				renderTableInExcel("pre_calculation_udp_level1",testName,"","");
				
			}else if(reportType.equalsIgnoreCase("DC")){
				instantiateFonts(exlFile,"Ftp");
				renderTableInExcel("ftpcalculationtable",testName,"","");
			}
			else if(reportType.equalsIgnoreCase("TCP")){
				instantiateFonts(exlFile,"TCP");
				renderTableInExcel("pre_calculation_TCP_level1",testName,"","");
			}
			else if(reportType.equalsIgnoreCase("UDP")){
				instantiateFonts(exlFile,"UDP");
				renderTableInExcel("pre_calculation_udp_level1",testName,"","");
			}
			else{
				instantiateFonts(exlFile,"Voice Quality");
				renderTableInExcel("pre_cal_voicequality_1",testName,"","");
			}
			
			 writableWorkbook.write();
		     writableWorkbook.close();
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
}
