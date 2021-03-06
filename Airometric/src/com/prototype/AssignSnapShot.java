package com.prototype;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.model.DBUtil;
import com.to.DeviceInfoTO;
import com.to.SnapShotTO;

public class AssignSnapShot {

	static List<DeviceInfoTO> getDeviceNames(String testName) {
		List<DeviceInfoTO> deviceInfoList = new ArrayList<DeviceInfoTO>();
		Connection conn = DBUtil.openConn();
		String query = "SELECT TIME_STAMP_FOREACH_SAMPLE,TEST_NAME,MARKET_ID FROM STG_DEVICE_INFO WHERE TEST_NAME LIKE '"
				+ testName + "' ORDER BY TIME_STAMP_FOREACH_SAMPLE";
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				DeviceInfoTO dto = new DeviceInfoTO();
				String timeStampForEachSample = rs
						.getString("TIME_STAMP_FOREACH_SAMPLE");
				String currenttestName = rs.getString("TEST_NAME");
				String marketId = rs.getString("MARKET_ID");
				dto.setMarketId(marketId);
				dto.setTestName(currenttestName);
				dto.setTimeStampForEachSample(timeStampForEachSample);
				deviceInfoList.add(dto);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return deviceInfoList;
	}

	static void updateDeviceInfo(List<DeviceInfoTO> deviceInfoList) {
		Connection conn = DBUtil.openConn();
		try {
			Statement stmt = conn.createStatement();
			for (int i = 0; i < deviceInfoList.size(); i++) {
				DeviceInfoTO dto = deviceInfoList.get(i);
				String updateQuery = "UPDATE STG_DEVICE_INFO SET SNAPSHOT_ID = '"
						+ dto.getTestName()
						+ "_"
						+ dto.getMarketId()
						+ "_"
						+ i
						+ "' WHERE TEST_NAME ='"
						+ dto.getTestName()
						+ "' AND MARKET_ID='"
						+ dto.getMarketId()
						+ "' AND TIME_STAMP_FOREACH_SAMPLE = '"
						+ dto.getTimeStampForEachSample() + "'";

				stmt.executeUpdate(updateQuery);

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public static void populateLogcat(String testName){
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			

			Connection conn = DBUtil.openConn();
			TreeSet<Date> timeStamp = new TreeSet<Date>();
			Map<Date, String> deviceInfoSnapMap = new HashMap<Date, String>();
			List<SnapShotTO> snapShotList = new ArrayList<SnapShotTO>();
			Map<Date, String> logCatSnapMap = new HashMap<Date, String>();
			SimpleDateFormat newSdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.SSS");
			SimpleDateFormat netTest = new SimpleDateFormat(
			"MM/dd/yyyy HH:mm:ss.SSS");
			String query = "SELECT * FROM STG_DEVICE_INFO WHERE TEST_NAME LIKE '"+testName+"%' order by TIME_STAMP_FOREACH_SAMPLE";
			String logCatQuery = "SELECT CallTimeStamp FROM stg_net_results WHERE TEST_NAME LIKE '%"+testName+"%'";
			
			try{
//				System.out.println(query);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				while(rs.next()){
					String dateStr = rs.getString("TIME_STAMP_FOREACH_SAMPLE");
					String snapShotId = rs.getString("SNAPSHOT_ID");
					SnapShotTO snapShotTO = new SnapShotTO();
					
					snapShotTO.setSnapshotId(snapShotId);
					snapShotTO.setTimeStamp(dateStr);
					snapShotList.add(snapShotTO);
					timeStamp.add(newSdf.parse(dateStr));
				}
				
				 rs = stmt.executeQuery(logCatQuery);
					
					for(int i=0;i<snapShotList.size();i++){
						SnapShotTO snapShotTO = snapShotList.get(i);
						if(i+1<snapShotList.size()){
							SnapShotTO nextsnapShotTO = snapShotList.get(i+1);
							String updateLogcat = "UPDATE stg_net_results SET SNAPSHOT_ID ='"+snapShotTO.getSnapshotId()+"' " +
							"WHERE CallTimeStamp > '"+netTest.format(newSdf.parse(snapShotTO.getTimeStamp()))+"' AND CallTimeStamp < '"+netTest.format(newSdf.parse(nextsnapShotTO.getTimeStamp()))+"' AND TEST_NAME ='"+testName+"'";
//							System.out.println(updateLogcat);
							stmt.executeUpdate(updateLogcat);
						}
						else{
							String updateLogcat = "UPDATE stg_net_results SET SNAPSHOT_ID ='"+snapShotTO.getSnapshotId()+"' " +
							"WHERE CallTimeStamp > '"+netTest.format(newSdf.parse(snapShotTO.getTimeStamp()))+"' AND TEST_NAME ='"+testName+"'";
//							System.out.println(updateLogcat);
							stmt.executeUpdate(updateLogcat);
						}
					}
			}
			catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			
		
		} catch (Exception intExe) {
			intExe.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		Connection conn = DBUtil.openConn();
		try{
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT DISTINCT TEST_NAME FROM STG_DEVICE_INFO WHERE TEST_NAME LIKE 'TMOFUT2%'");
			while(rs.next()){
				String testName = rs.getString("TEST_NAME");
//				System.out.println("testName--"+testName);
//				updateDeviceInfo(getDeviceNames(testName));
				populateLogcat(testName);
			}
			
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		updateDeviceInfo(getDeviceNames("D3CHV0905-2"));
		populateLogcat("D3CHV0905-2");
	}
}
