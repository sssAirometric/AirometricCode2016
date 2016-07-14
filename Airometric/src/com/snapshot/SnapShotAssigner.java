package com.snapshot;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.model.DBUtil;
import com.to.DeviceInfoTO;

public class SnapShotAssigner {

	List<DeviceInfoTO> deviceInfoList;
	public void setSnapShot(String testName){
//		System.out.println("snaaaaapppppppppppppp");
		Connection conn = DBUtil.openConn();
		TreeSet<Date> timeStamp = new TreeSet<Date>();
		Map<Date, String> deviceInfoSnapMap = new HashMap<Date, String>();
		Map<Date, String> logCatSnapMap = new HashMap<Date, String>();
		SimpleDateFormat newSdf = new SimpleDateFormat(
		"yyyy-MM-dd HH:mm:ss.SSS");
		String query = "SELECT * FROM STG_DEVICE_INFO WHERE TEST_NAME LIKE '"+testName+"%' order by TIME_STAMP_FOREACH_SAMPLE";
		String logCatQuery = "SELECT TIME_STAMP FROM stg_log_cat_info WHERE TEST_NAME LIKE '%"+testName+"%'";
		
		try{
//			System.out.println(query);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				String dateStr = rs.getString("TIME_STAMP_FOREACH_SAMPLE");
				String snapShotId = rs.getString("SNAPSHOT_ID");
				deviceInfoSnapMap.put(newSdf.parse(dateStr), snapShotId);
				timeStamp.add(newSdf.parse(dateStr));
			}
			
			 rs = stmt.executeQuery(logCatQuery);
				while(rs.next()){
					String dateStr = rs.getString("TIME_STAMP");
					logCatSnapMap.put(newSdf.parse(dateStr), deviceInfoSnapMap.get(timeStamp.floor(newSdf.parse(dateStr))));
				}
				
				Iterator<Date> logCatdates = logCatSnapMap.keySet().iterator();
				while(logCatdates.hasNext()){
					Date logCatDate = logCatdates.next();
					if(null!=logCatSnapMap.get(logCatDate)){
						String updateLogcat = "UPDATE stg_log_cat_info SET SNAPSHOT_ID ='"+logCatSnapMap.get(logCatDate)+"' " +
						"WHERE TIME_STAMP LIKE '"+newSdf.format(logCatDate)+"'";
//				System.out.println(updateLogcat);
				stmt.executeUpdate(updateLogcat);
					}
					
				}
			
			
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {}
}
