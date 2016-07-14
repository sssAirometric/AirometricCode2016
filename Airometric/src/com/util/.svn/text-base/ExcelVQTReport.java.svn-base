package com.util;

	import java.io.File;
	import java.io.FileOutputStream;
	import java.io.IOException;
	import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.HashMap;
	import java.util.Iterator;
	import java.util.List;

	import com.PropertyFileReader;
	import com.to.DeviceInfoTO;
	import jxl.CellView;
	import jxl.Workbook;
	import jxl.format.Alignment;
	import jxl.format.Border;
	import jxl.format.BorderLineStyle;
	import jxl.format.Colour;
	import jxl.format.PageOrientation;
	import jxl.format.UnderlineStyle;
	import jxl.write.Label;
	import jxl.write.Number;
	import jxl.write.WritableCellFormat;
	import jxl.write.WritableFont;
	import jxl.write.WritableImage;
	import jxl.write.WritableSheet;
	import jxl.write.WritableWorkbook;
	import jxl.write.WriteException;
	import jxl.write.biff.RowsExceededException;

	public class ExcelVQTReport {
		File file = null;
		int i=0;
		public static String imagePath=null;
		public FileOutputStream os  = null;
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM dd, yyyy");
		int cellWidth = 18;
		String isOverTimeEnabled = null;
		static HashMap<String,String>  propertiesFiledata =PropertyFileReader.getProperties();
		public static String folderpath =propertiesFiledata.get("REPORTS_FOLDER_PATH").toString();
		public static String AIROMETRIC_LOGO_PATH =propertiesFiledata.get("AIROMETRIC_LOGO_PATH").toString();
		
		
		public String getVQTReport(List<DeviceInfoTO> vqtReportList,String testCaseName)
		{
			file = reportFolderstructureSummary();
			String fullpath = file.getAbsolutePath();  
			WritableWorkbook workbook = null;	
			int col_index = 1;
			int row_index=14;	
			DeviceInfoTO deviceInfo=null;
			
			WritableFont TOPBoundaryRowWise = new WritableFont(WritableFont.ARIAL,10,WritableFont.BOLD,false,UnderlineStyle.NO_UNDERLINE,Colour.BLACK);
			WritableCellFormat TOPBoundaryRow= new WritableCellFormat(TOPBoundaryRowWise);
			setTextdata(TOPBoundaryRow,Colour.WHITE, Border.TOP, BorderLineStyle.MEDIUM);
			
			WritableFont headerTextData = new WritableFont(WritableFont.ARIAL,12,WritableFont.BOLD,false,UnderlineStyle.NO_UNDERLINE,Colour.BLACK);
			WritableCellFormat headerTextDataFormat = new WritableCellFormat(headerTextData);
			setTextdata(headerTextDataFormat,Colour.WHITE, Border.NONE, BorderLineStyle.THIN);
			
			WritableFont headerTextWFTimeSheet = new WritableFont(WritableFont.ARIAL,10,WritableFont.BOLD,false,UnderlineStyle.NO_UNDERLINE,Colour.BLACK);
			WritableCellFormat headerTextWCFTimeSheet = new WritableCellFormat(headerTextWFTimeSheet);
			setTextdata(headerTextWCFTimeSheet,Colour.WHITE, Border.ALL, BorderLineStyle.MEDIUM);
			
			WritableCellFormat headerTextWCF= new WritableCellFormat(headerTextWFTimeSheet);
			WritableCellFormat headerTextWCFTime = new WritableCellFormat(headerTextWCF);
			setTextdata(headerTextWCFTimeSheet,Colour.WHITE, Border.ALL, BorderLineStyle.MEDIUM);
			
			WritableFont leftBoundaryColumnWise = new WritableFont(WritableFont.ARIAL,10,WritableFont.BOLD,false,UnderlineStyle.NO_UNDERLINE,Colour.BLACK);
			WritableCellFormat leftColumnBoundary= new WritableCellFormat(leftBoundaryColumnWise);
			setTextdata(leftColumnBoundary,Colour.WHITE, Border.LEFT, BorderLineStyle.MEDIUM);
			
			WritableFont rightBoundaryColumnWise = new WritableFont(WritableFont.ARIAL,10,WritableFont.BOLD,false,UnderlineStyle.NO_UNDERLINE,Colour.BLACK);
			WritableCellFormat rightColumnBoundary= new WritableCellFormat(rightBoundaryColumnWise);
			setTextdata(rightColumnBoundary,Colour.WHITE, Border.RIGHT, BorderLineStyle.MEDIUM);
		
			WritableFont dataentry = new WritableFont(WritableFont.ARIAL,10,WritableFont.NO_BOLD,false,UnderlineStyle.NO_UNDERLINE,Colour.BLACK);
			WritableCellFormat dataentryText= new WritableCellFormat(dataentry);
			setTextdata(dataentryText,Colour.WHITE, Border.NONE, BorderLineStyle.NONE);
			
			WritableFont footerTextWFTimeSheet = new WritableFont(WritableFont.ARIAL,10,WritableFont.BOLD,false,UnderlineStyle.NO_UNDERLINE,Colour.BLACK);
			WritableCellFormat footerTextWCFTimeSheet = new WritableCellFormat(footerTextWFTimeSheet);
			setTextdata(footerTextWCFTimeSheet,Colour.WHITE, Border.BOTTOM, BorderLineStyle.MEDIUM);
			
			
			WritableFont datatexValue = new WritableFont(WritableFont.ARIAL,10,WritableFont.NO_BOLD,false,UnderlineStyle.NO_UNDERLINE,Colour.BLACK);
			WritableCellFormat dataValue= new WritableCellFormat(datatexValue);
			setTextdata(dataValue,Colour.WHITE, Border.ALL, BorderLineStyle.MEDIUM);
			try {
				dataValue.setAlignment(Alignment.LEFT);
			} catch (WriteException e4) {
				e4.printStackTrace();
			}
			
			
			WritableFont datatextentryValue = new WritableFont(WritableFont.ARIAL,10,WritableFont.NO_BOLD,false,UnderlineStyle.NO_UNDERLINE,Colour.BLACK);
			WritableCellFormat datatextValue= new WritableCellFormat(datatextentryValue);
			setTextdata(datatextValue,Colour.WHITE, Border.ALL, BorderLineStyle.MEDIUM);
			
			try {
				datatextValue.setAlignment(Alignment.CENTRE);
			} catch (WriteException e3) {
				e3.printStackTrace();
			}
			
			WritableCellFormat footerText = new WritableCellFormat(headerTextWFTimeSheet);
			setTextdata(footerText,Colour.WHITE, Border.NONE, BorderLineStyle.THIN);
			try {
				footerText.setAlignment(Alignment.RIGHT);
			} catch (WriteException e2) {
				e2.printStackTrace();
			}
			
			
			try {
				dataentryText.setAlignment(Alignment.RIGHT);
			} catch (WriteException e1) {
				e1.printStackTrace();
			}
			
			
			WritableFont headerTextWithTimeSheet = new WritableFont(WritableFont.ARIAL,12,WritableFont.BOLD,false,UnderlineStyle.NO_UNDERLINE,Colour.BLACK);
			WritableCellFormat textCustomerdataentry = new WritableCellFormat(headerTextWithTimeSheet);
			
			
			WritableFont headerTextWithFontTimeSheet = new WritableFont(WritableFont.ARIAL,10,WritableFont.BOLD,false,UnderlineStyle.NO_UNDERLINE,Colour.BLACK);
			WritableCellFormat textdataentry = new WritableCellFormat(headerTextWithFontTimeSheet);
			try {
				textdataentry.setWrap(true);
			} catch (WriteException e1) {
				e1.printStackTrace();
			}
			setTextdata(textdataentry,Colour.WHITE, Border.NONE, BorderLineStyle.NONE);
			try {
				textdataentry.setAlignment(Alignment.LEFT);
				textCustomerdataentry.setAlignment(Alignment.LEFT);
			} catch (WriteException e1) {
				e1.printStackTrace();
			}			
		  try 
		  {
			    workbook = Workbook.createWorkbook(file);
				String addressLine1 = "VOICE QUALITY REPORT";	
				String addressLine2 = "Test_Name:"+testCaseName;	
				WritableSheet sheet = workbook.createSheet("VQTTestReport", 0);
				WritableImage image=setImage("AIROMETRIC_LOGO_PATH");
				sheet.getSettings().setOrientation(PageOrientation.LANDSCAPE);
				sheet.getSettings().setFitWidth(1);
				sheet.getSettings().setShowGridLines(false);
				
				for(int i=col_index;i<col_index+4;i++){
					 addHeaderText(sheet,TOPBoundaryRow,"",5,i,cellWidth);
					 addHeaderText(sheet,TOPBoundaryRow,"",7,i,cellWidth);
				 }
				mergeCells(sheet,2,6,3,6);
				addHeaderText(sheet,headerTextDataFormat,addressLine1,6,2,40);
				mergeCells(sheet,2,9,3,9);
				addData(sheet,headerTextDataFormat,addressLine2,9,2,40);	

				addHeaders(sheet,headerTextWCFTimeSheet);
				Integer k=0;			    
				Iterator vqtReportListItr = vqtReportList.iterator();		
				while(vqtReportListItr.hasNext()){
					k++;				
					deviceInfo = new DeviceInfoTO();
					deviceInfo =(DeviceInfoTO)vqtReportListItr.next();
					String signalStrength = deviceInfo.getSignalStrength();
					String throughput = deviceInfo.getThroughput();
					String time_stamp = deviceInfo.getTime_stamp();
					
					addData(sheet,datatextValue,k.toString(),row_index,col_index,cellWidth);
					col_index++;
					addData(sheet,datatextValue,time_stamp,row_index,col_index,cellWidth);
					col_index++;
					addData(sheet,dataValue,signalStrength,row_index,col_index,cellWidth+10);
					col_index++;
					addData(sheet,datatextValue,throughput,row_index,col_index,cellWidth+7);
					col_index=1;
					row_index++;		
				 }
				 row_index++;
				 row_index=row_index+6;
				  for( i=1;i<col_index+4;i++){
						 addHeaderText(sheet,TOPBoundaryRow,"",0,i,cellWidth);
						 addHeaderText(sheet,TOPBoundaryRow,"",row_index+7,i,cellWidth);
				   }
				  for( i=(row_index+6);i>=0;i-- ){
						 addHeaderText(sheet,rightColumnBoundary,"",i,0,cellWidth);
						 addHeaderText(sheet,leftColumnBoundary,"",i,5,cellWidth);//Column addition done here 
				   }
				 			  
				 CellView cv  = new CellView();
				 cv.setSize(25 * 256);
				 sheet.setColumnView(1, cellWidth-3);
				 sheet.setColumnView(2, cellWidth+5);
				 sheet.setColumnView(3, cellWidth+10);
				 sheet.setColumnView(4, cellWidth+10);
				 addHeadersImage(sheet,image,1,0,2,3);
			     workbook.write();
			     workbook.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}finally
			{
		      if (os!= null)
		      {
		    	  try{
		    	  os.close();
		    	  }catch(IOException ioe)
		    	  {
		    		  ioe.printStackTrace();
		    		 
		    	  }
		      }
			}
		    return fullpath	;
		}
		private void addHeaders(WritableSheet sheet,WritableCellFormat cellFormat){
			addHeaderText(sheet,cellFormat,"SLNO",13,1,cellWidth-7);
			addHeaderText(sheet,cellFormat,"TIME_STAMP",13,2,cellWidth);
			addHeaderText(sheet,cellFormat,"SIGNAL_STRENGTH",13,3,cellWidth);
			addHeaderText(sheet,cellFormat,"THROUGHPUT",13,4,cellWidth);
		   }
		
		private void addHeaderText(WritableSheet sheet,WritableCellFormat wcf, Object data,int row, int col,int length) {
			String value=null;
				if(data instanceof String){
					value=(String)data;
					Label CompanyNameLabel = new Label(col,row,value,wcf);
				try {
					sheet.setColumnView(col,length);
					sheet.addCell(CompanyNameLabel);
				} catch (RowsExceededException e) {
					e.printStackTrace();
				} catch (WriteException e) {
					e.printStackTrace();
				}
			}else {
				Number noOfHours = new Number(col,row,((Double)data).doubleValue());
				try {
					sheet.addCell(noOfHours);
				} catch (RowsExceededException e) {
					e.printStackTrace();
				} catch (WriteException e) {
					e.printStackTrace();
				}
			}
				
		}
		
	     private WritableImage setImage(String image_Path){
			
			if(image_Path.equals("") || image_Path.equals(null)){
				imagePath=AIROMETRIC_LOGO_PATH;
				File imgFile=new File(imagePath);
				WritableImage image=new WritableImage(1,1,1,4,imgFile);
				return image;
			}
			else{
				imagePath=AIROMETRIC_LOGO_PATH;
				File imgFile=new File(imagePath);
				WritableImage image=new WritableImage(1,1,1,4,imgFile);
				return image;
			}
			
		}
	     private void addHeadersImage(WritableSheet sheet,WritableImage image,int from_col,int from_row,int to_col,int to_row)
			{			
				image.setX(1.35);
			    image.setY(0.80);
			    image.setWidth(1.50);
				sheet.addImage(image);
			}
	    private void setTextdata(WritableCellFormat wcf,Colour bgc,Border b,BorderLineStyle bs){
			
			try {
				wcf.setAlignment(Alignment.CENTRE);
				wcf.setBackground(bgc);
				wcf.setBorder(b, bs);				
			} catch (WriteException e) {
				e.printStackTrace();
			}
		}
		private void addData(WritableSheet sheet,WritableCellFormat wcf, Object data,int row, int col,int length) {
			String value=null;
			double totalHours = 0; 
				if(data instanceof String){
					value=(String)data;
					Label label = new Label(col,row,value,wcf);
				try {
					label = new Label(col,row,value,wcf);
					sheet.setColumnView(col,length);
					sheet.addCell(label);
				} catch (RowsExceededException e) {
					e.printStackTrace();
				} catch (WriteException e) {
					e.printStackTrace();
				}
				
				}else {
					Number noOfHours = new Number(col,row,((Double)data).doubleValue());
					noOfHours.setCellFormat(wcf);
					
					try {
						sheet.addCell(noOfHours);
					} catch (RowsExceededException e) {
						e.printStackTrace();
					} catch (WriteException e) {
						e.printStackTrace();
					}
				}
		}
		
		private void mergeCells(WritableSheet sheet,int from_col,int from_row,int to_col,int to_row){
			try {
				sheet.mergeCells(from_col,from_row,to_col,to_row);
			} catch (RowsExceededException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}
		}
		
		
		public static File reportFolderstructureSummary(){
			
			List detaillist = new ArrayList();
			String folder_name="VQT-Report";
			File file = null;
			detaillist.add(folder_name);
			detaillist.add("VQTFiles");
			Iterator itr  =detaillist.iterator();
			StringBuilder sb = new StringBuilder();
			sb.append(folderpath);
			while(itr.hasNext()){
			sb.append(itr.next());
			sb.append("\\");
			}
		    sb.append(currentDate());
		    String fullpath= sb.toString();
			File folder = new File(sb.toString());
			if(!folder.exists())
			folder.mkdirs();
			file = new File(sb.toString()+"\\VQTTestReport.xls");
			try {
				file.createNewFile();		
			} catch (IOException e) {
				e.printStackTrace();
			}
			return file;
		}
		
		public static StringBuilder currentDate(){
			StringBuilder sb = new StringBuilder();
			int year = new java.util.Date().getYear()+1900;
			int month = new java.util.Date().getMonth()+1;
			int day = new java.util.Date().getDate();
			int hr = new java.util.Date().getHours();
			int min = new java.util.Date().getMinutes();
			int sec = new java.util.Date().getSeconds();
			sb.append(year);
			sb.append("-");
			sb.append(month);
			sb.append("-");
			sb.append(day);
			sb.append("\\");
			sb.append(hr);
			sb.append(min);
			sb.append(sec);
			return sb;
		}

}
