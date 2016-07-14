package com.preprocessing.reports.summary;

import java.io.File;
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

public class RenderSummaryReportHelper {

	static WritableFont cellFont1 = null;
	static WritableCellFormat cellFormat1 = null;
	static WritableCellFormat cellFormat = null;
	static WritableFont cellFont = null;
	static WritableSheet writableSheet1 = null;
	static WritableWorkbook writableWorkbook = null;
	static int rowIndex = 1;
	static int colIndex = 1;
	
	

	public RenderSummaryReportHelper(File exlFile,String sheetName,int sheetNo) {
		instantiateFonts(exlFile,sheetName,sheetNo);
	}

	static void instantiateFonts(File exlFile, String sheetTitle,int sheetNo) {
		try {
			rowIndex = 1;
			colIndex = 1;
			writableWorkbook = Workbook.createWorkbook(exlFile);
			writableSheet1 = writableWorkbook.createSheet(sheetTitle, sheetNo);
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
	}

	static void writeToCell(String value,int rowInd,int colInd,WritableCellFormat cellFormat){
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
	static void createHeaderLabels(List<String> labelList) {
		try {
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
	
}
