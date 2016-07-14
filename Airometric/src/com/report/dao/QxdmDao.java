package com.report.dao;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;

import com.model.DBUtil;
import com.to.HealthIndexGenerator;

public class QxdmDao {

	String folder=HealthIndexGenerator.resourceBundle.getString("DOWNLOAD_FOLDER");
	public String getAllRawData(String date) {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		String allRawdata = "";
		int length;
		FacesContext context = FacesContext.getCurrentInstance();
		BufferedImage buffimg = null;
	
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String getRawData = "SELECT RAW_DATA FROM QXDM_DATA WHERE ENTRY_ID=25";
			rs = st.executeQuery(getRawData);
               FileWriter writer = new FileWriter(folder+"//mycopy.txt");
			while (rs.next()) {
				 InputStream is = rs.getBinaryStream("RAW_DATA") ;
//				 //System.out.println(getStringFromInputStream(bis));
				 File image = new File("D:\\qxdmSri.txt");
				 byte[] buffer = new byte[1];
			      FileOutputStream fos = new FileOutputStream(image);
			      while (is.read(buffer) > 0) {
			          fos.write(buffer);
			        }
			        fos.close();
			}

			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return allRawdata;
	}
	
	public void getQXDMFile(String date) {
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			Date clickeddate = sdf.parse(date.replaceAll("_", ":").replaceAll("Test Time:", ""));
			List<File> filesList = new ArrayList<File>();
			String folderStructure = "C:\\QxdmFold";
			File filePath = new File(folder+"\\QxdmResultFile\\qxdmChunkmerged.txt");
			filePath.delete();
			filePath.createNewFile();
			date = date.replaceAll("_", ":");
			//System.out.println("date-------"+date);
			File file = new File(folderStructure);
			File[] files = file.listFiles();
			for(int i=0;i<files.length;i++){
				String filedateStr = files[i].getName().replaceAll(".txt", "").replaceAll("_", ":");
				Date fileDate = sdf.parse(filedateStr);
//				if(fileDate.compareTo(clickeddate)>=0){
					long diff = fileDate.getTime() - clickeddate.getTime();
					long timediff = (diff / 1000) % 60;
					//System.out.println("timediff---------+"+timediff);
					
					if(timediff>-10&&timediff<10){
						//System.out.println("greater---"+filedateStr);
						filesList.add(files[i]);
					}
					
//				}
			}
			mergeFiles(filesList,filePath);
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	
	}
	
	public String insertIntoDB() {
		Connection conn = null;
		ResultSet rs = null;
		String allRawdata = "";
		FacesContext context = FacesContext.getCurrentInstance();
		
		try {
			String filePath = folder+"\\qxdmChunk.txt";
			InputStream inputStream = new FileInputStream(new File(filePath));
			conn = DBUtil.getConnection();
			InputStream is = 
                new ByteArrayInputStream("file content..blah blah".getBytes());
			String getRawData = "INSERT INTO `qxdm_data` VALUES ('25', '2013-12-23 12:48:54.786', ?, '1', '1', '1'); ";
			PreparedStatement statement = conn.prepareStatement(getRawData);
			statement.setBlob(1, inputStream);
			statement.executeUpdate();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return allRawdata;
	}
	
	
	private static String getStringFromInputStream(InputStream is) {
		 
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
 
		String line;
		try {
 
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine().toString()) != null) {
				//System.out.println("line---]------"+line);
				sb.append(line);
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
 
		return sb.toString();
 
	}
	
	
	public static void mergeFiles(List<File> files, File mergedFile) {
		 
		FileWriter fstream = null;
		BufferedWriter out = null;
		try {
			//System.out.println("files--------"+files);
			fstream = new FileWriter(mergedFile, true);
			 out = new BufferedWriter(fstream);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
 
		for (File f : files) {
			//System.out.println("merging: " + f.getName());
			FileInputStream fis;
			try {
				fis = new FileInputStream(f);
				BufferedReader in = new BufferedReader(new InputStreamReader(fis));
 
				String aLine;
				while ((aLine = in.readLine()) != null) {
					out.write(aLine);
					out.newLine();
				}
 
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
 
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
 
	}
	
	public static void main(String[] args) throws IOException {
		 
		// intilize an InputStream
	/*	InputStream is = 
                     new ByteArrayInputStream("file content..blah blah".getBytes());
 
		String result = getStringFromInputStream(is);
 
		//System.out.println(result);
		//System.out.println("Done");*/
//		new QxdmDao().insertIntoDB();
//		new QxdmDao().getAllRawData();
		try{
			new QxdmDao().getQXDMFile("2013-12-08 12_50_25.620");
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	
		
		
 
	}
 
	
	
}
