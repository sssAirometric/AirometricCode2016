package com.to;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;
import com.model.DBUtil;
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


public class HealthIndexGenerator {
	private static Locale currentLocale = new Locale("es");
	public static ResourceBundle resourceBundle = ResourceBundle.getBundle("Resource",currentLocale);
	Connection con = null;
	public String getFilePath(){
	String filePath = "";

	 try
	 {
	  con=DBUtil.getConnection();
	  Date date=new Date();
	  SimpleDateFormat sdf=new SimpleDateFormat("yyyy MMM dd");
	  String str=sdf.format(date);
	  String splt[]=str.split(" ");
//	  System.out.println(splt[0]+"---"+splt[1]+"--------"+splt[2]);
	  String folder=resourceBundle.getString("DOWNLOAD_FOLDER");
	  String path=folder+"\\"+splt[0]+"\\"+splt[1]+"\\"+splt[2]+"";
//	  System.out.println("path :"+path);
	  
	  	new File(folder+"\\"+splt[0]+"\\"+splt[1]+"\\"+splt[2]+"").mkdirs();
	  	filePath = path+"\\ConfiguratiopnScoreReport.xls";
		   File exlFile = new File(path+"\\ConfiguratiopnScoreReport.xls");
	       WritableWorkbook writableWorkbook = Workbook.createWorkbook(exlFile);
	
	       WritableSheet writableSheet1 = writableWorkbook.createSheet("Configuratiopn Score", 0);
	       
	       WritableFont cellFont = new WritableFont(WritableFont.ARIAL, 12);
	       cellFont.setBoldStyle(WritableFont.BOLD);
	       cellFont.setColour(Colour.BLACK);
	       
	       WritableFont cellFont1 = new WritableFont(WritableFont.ARIAL, 10);
	       cellFont.setColour(Colour.BLACK);
	       
	       WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
	       cellFormat.setShrinkToFit(true);
	       cellFormat.setBackground(Colour.GRAY_50);
	       cellFormat.setBorder(Border.ALL, BorderLineStyle.THIN,Colour.BLACK);
	       cellFormat.setAlignment(Alignment.CENTRE);
	       cellFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
	       cellFormat.setWrap(true);
	       
	       WritableCellFormat cellFormat1 = new WritableCellFormat(cellFont1);
	       cellFormat1.setShrinkToFit(true);
	       cellFormat1.setBorder(Border.ALL, BorderLineStyle.THIN,Colour.BLACK);
	       cellFormat1.setAlignment(Alignment.CENTRE);
	       cellFormat1.setVerticalAlignment(VerticalAlignment.CENTRE);
	       cellFormat1.setWrap(true);
	       
	       writableSheet1.setColumnView(1, 25);
	       Label label = new Label(1, 0, "CONFIG_SCORE_ID", cellFormat);
	       writableSheet1.setColumnView(2, 20);
	       Label label11 = new Label(2, 0, "CONFIG_NAME", cellFormat);
	       writableSheet1.setColumnView(3, 20);
	       Label label21 = new Label(3, 0, "KPI_NAME", cellFormat);
	       writableSheet1.setColumnView(4, 15);
	       Label label1 = new Label(4, 0, "CONFIG_ID", cellFormat);
	       writableSheet1.setColumnView(5, 15);
	       Label label2 = new Label(5, 0, "KPI_ID", cellFormat);
	       writableSheet1.setColumnView(6, 15);
	       Label label3 = new Label(6, 0, "SCORE", cellFormat);
	       writableSheet1.setColumnView(7, 20);
	       Label label4 = new Label(7, 0, "CREATED_BY", cellFormat);
	       writableSheet1.setColumnView(8, 30);
	       Label label5 = new Label(8, 0, "CREATED_DATE", cellFormat);
	       writableSheet1.setColumnView(9, 25);
	       Label label6 = new Label(9, 0, "UPDATED_BY", cellFormat);
	       writableSheet1.setColumnView(10, 30);
	       Label label7 = new Label(10, 0, "UPDATED_DATE", cellFormat);
	       writableSheet1.setColumnView(11, 15);
	       Label label8 = new Label(11, 0, "MARKET_ID", cellFormat);
	       writableSheet1.setColumnView(12, 35);
	       Label label9 = new Label(12, 0, "MARKET_NAME", cellFormat);
	       writableSheet1.setColumnView(0, 25);
	       Label label10 = new Label(0, 0, "TEST_NAME", cellFormat);
	      
	       writableSheet1.addCell(label10);
	       writableSheet1.addCell(label);
	       writableSheet1.addCell(label1);
	       writableSheet1.addCell(label2);
	       writableSheet1.addCell(label11);
	       writableSheet1.addCell(label21);
	       writableSheet1.addCell(label3);
	       writableSheet1.addCell(label4);
	       writableSheet1.addCell(label5);
	       writableSheet1.addCell(label6);
	       writableSheet1.addCell(label7);
	       writableSheet1.addCell(label8);
	       writableSheet1.addCell(label9);
	       
	       
	       ArrayList<ArrayList<String>> list=new ArrayList<ArrayList<String>>();
	       ArrayList<String>list1=null;
	       
	       
	       
	       String sql="select cs.config_score_id,cm.config_name,k.kpi_name,cs.config_id,cs.kpi_id,cs.score,cs.CREATED_BY,"
	       		+ "cs.CREATED_DATE,cs.UPDATED_BY,cs.UPDATED_DATE,cs.market_name,cs.TEST_NAME from configuration_score as cs inner join "
	       		+ "configuration_master as cm on cs.config_id=cm.config_id inner join kpi as k on cs.kpi_id=k.kpi_id order by "
	       		+ "cs.config_score_id, cm.config_name,k.kpi_name";
	       Statement st=con.createStatement();
	       ResultSet rs=st.executeQuery(sql);
	       
	       while(rs.next())
	       {
	    	   list1=new ArrayList<String>();
	    	   list1.add(rs.getString(12));
	    	   list1.add(rs.getInt(1)+"");
	    	   list1.add(rs.getString(2));
	    	   list1.add(rs.getString(3));
	    	   list1.add(rs.getInt(4)+"");
	    	   list1.add(rs.getInt(5)+"");
	    	   list1.add(rs.getInt(6)+"");
	    	   list1.add(rs.getInt(7)+"");
	    	   list1.add(rs.getString(8));
	    	   list1.add(rs.getInt(9)+"");
	    	   list1.add(rs.getString(10));
	    	   String marketName=rs.getString(11);
	    	   
	    	   if((marketName==null) || (marketName.equals("")))
	    	   {
	    		   list1.add("");
	    	   }
	    	   else
	    	   {
	    		   String mkId=MarketId(marketName,con);
//	    		   System.out.println("mkId........................................................."+mkId);
	    		   list1.add(mkId);
	    	   }
	    	   list1.add(marketName);
	    	   
	    	   //System.out.println(mkId+"------------------------------------------------------------------");
	    	   list.add(list1);
	       }
	       
	       int count=0,count1=1;
	       Iterator<ArrayList<String>>it=list.iterator();
	       while(it.hasNext())
	       {
	    	   ArrayList<String>subList=it.next();
	    	   Iterator<String>it1=subList.iterator();
	    	   while(it1.hasNext())
	    	   {
	    		   String value=it1.next();
	    		   Label val11 = new Label(count++, count1, value, cellFormat1);
	   			   writableSheet1.addCell(val11);
	    	   }
	    	   count=0;
	    	   count1++;
	       }
	       
	       writableWorkbook.write();
	       writableWorkbook.close();
	       
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
	   finally
	   {
		   try
		   {
			   con.close();
		   }
		   catch(Exception e1)
		   {
			   e1.printStackTrace();
		   }
	   }
//	   System.out.println("done");
	   return filePath;
   }
   
   public static String Marketname(String mkId,Connection con)
   {
	   String res=null;
	   try
	   {
		   String sql="SELECT MARKET_NAME FROM MARKET WHERE MARKET_ID IN("+mkId+")";
//		   System.out.println(sql);
		   Statement st=con.createStatement();
	       ResultSet rs=st.executeQuery(sql);
	       while(rs.next())
	       {
	    	   if(res==null)
	    	   {
	    		   res=rs.getString(1);
	    	   }
	    	   else
	    	   {
	    		   res=res+","+rs.getString(1);
	    	   }
	       }
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
//	   System.out.println(res);
	   return res;
   }
   public static String MarketId(String marketName,Connection con)
   {
	   String res=null;
	   try
	   {
		   String sql="SELECT MARKET_ID FROM MARKET WHERE MARKET_NAME='"+marketName+"'";
//		   System.out.println(sql);
		   Statement st=con.createStatement();
	       ResultSet rs=st.executeQuery(sql);
	       while(rs.next())
	       {
	    	   if(res==null)
	    	   {
	    		   res=rs.getString(1);
	    	   }
	    	   else
	    	   {
	    		   res=res+","+rs.getString(1);
	    	   }
	       }
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
//	   System.out.println("MarketId...........****................................"+res);
	   return res;
   }   
}
