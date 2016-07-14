package com.preprocessing.reports;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
import com.report.to.VoiceDataTo;
import com.report.to.VoiceQualityTo;

public class PreprocessingVoiceDataReport {


	static WritableFont cellFont1 = null;
	static WritableCellFormat cellFormat1 = null;
	static WritableCellFormat cellFormat = null;
	static WritableFont cellFont = null;
	static WritableSheet writableSheet1 = null;
	static WritableWorkbook writableWorkbook = null;
	static int rowIndex = 1;
	static int colIndex = 1;

	private static void instantiateFonts(File exlFile, String sheetTitle) {
		try {
			 rowIndex = 1;
			 colIndex = 1;
			writableWorkbook = Workbook.createWorkbook(exlFile);
			writableSheet1 = writableWorkbook.createSheet(sheetTitle, 0);
			//System.out.println("writableSheet1----------"+writableSheet1);
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
	}

	private static void createHeaderLabels(List<String> labelList) {
		try{
			for (String labelName : labelList) {
				writableSheet1.setColumnView(1, 25);
				Label label = new Label(colIndex, rowIndex, labelName, cellFormat);
				writableSheet1.addCell(label);
				colIndex++;
			}
		}
		catch (RowsExceededException e) {
			// TODO: handle exception
		}
		catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rowIndex++;
	}
	private static void writeToCell(String value,int rowInd,int colInd,WritableCellFormat cellFormat){
		try{
			writableSheet1.setColumnView(1, 25);
			Label label = new Label(colInd,rowInd, value, cellFormat);
			writableSheet1.addCell(label);
		}
		catch (RowsExceededException e) {
			// TODO: handle exception
		}
		catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private static List<VoiceDataTo> getPreVoiceData(String testNames){
		List<VoiceDataTo> voiceDataList = new ArrayList<VoiceDataTo>();
		String query = "SELECT PC.*,M.MARKET_NAME FROM pre_calculation_voicedata_1 PC,MARKET M WHERE PC.TEST_NAME IN ('"+testNames+"') AND  M.MARKET_ID = PC.MARKET_ID";
		Connection conn = DBUtil.getConnection();
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			while(rs.next()){
				VoiceDataTo voiceDataTo = new VoiceDataTo();
				String marketName = rs.getString("MARKET_NAME");
				String tcp = rs.getString("TCP");
				String rssi = rs.getString("RSSI");
				String networkType = rs.getString("NETWORK_TYPE");
				String test_name = rs.getString("TEST_NAME");
				String timeStamp = rs.getString("TIMESTAMP");
				voiceDataTo.setMarketName(marketName);
				voiceDataTo.setNetworkType(networkType);
				voiceDataTo.setNetworkType(networkType);
				voiceDataTo.setRssi(rssi);
				voiceDataTo.setTcp(tcp);
				voiceDataTo.setTimestamp(timeStamp);
				voiceDataTo.setTestName(test_name);
				voiceDataList.add(voiceDataTo);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return voiceDataList;
	}
	
	private static void renderReport(List<VoiceDataTo> voiceDataList){
		for(VoiceDataTo voiceQualityTo : voiceDataList ){
			int colIndex = 1;
			writeToCell(voiceQualityTo.getMarketName(),rowIndex,colIndex,cellFormat1);
			colIndex++;
			writeToCell(voiceQualityTo.getTestName(),rowIndex,colIndex,cellFormat1);
			colIndex++;
			writeToCell(voiceQualityTo.getTcp().toString(),rowIndex,colIndex,cellFormat1);
			colIndex++;
			writeToCell(voiceQualityTo.getRssi().toString(),rowIndex,colIndex,cellFormat1);
			colIndex++;
			writeToCell(voiceQualityTo.getTimestamp().toString(),rowIndex,colIndex,cellFormat1);
			colIndex++;
			writeToCell(voiceQualityTo.getNetworkType().toString(),rowIndex,colIndex,cellFormat1);
			colIndex++;
			rowIndex++;
		}
	}
	
	public static void preProcessVDReport(String filePath,String testNames) {
		
		List<String> labelList = new ArrayList<String>();
		
		labelList.add("Market Name");
		labelList.add("Test Name");
		labelList.add("TCP");
		labelList.add("Signal Strength");
		labelList.add("Time Stamp");
		labelList.add("Network Type");
		try{
			File exlFile = new File(filePath);
			if(exlFile.exists()){
				exlFile.delete();
			}
			exlFile = new File(filePath);
			//System.out.println("hiiii");
			instantiateFonts(exlFile,"VD");
			createHeaderLabels(labelList);
			List<VoiceDataTo> vqList = getPreVoiceData(testNames);
			renderReport(vqList);
			
			   writableWorkbook.write();
		       writableWorkbook.close();
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	
	}

}
