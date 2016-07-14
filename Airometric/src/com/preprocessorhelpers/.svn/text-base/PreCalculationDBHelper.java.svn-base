package com.preprocessorhelpers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.model.DBUtil;

public class PreCalculationDBHelper {
	
	public static Connection conn = null;
	public static Statement st = null;
	public void openConn(){
		try{
			conn= DBUtil.getConnection();
			st = conn.createStatement();
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void closeConn(){
		try{
			st.close();
			conn.close();
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public boolean isTestPrecalculated(String testName){
		boolean isPreCalculated = false;
		openConn();
		String query = "SELECT * FROM precalculated_tests WHERE TEST_NAME LIKE '"+testName+"'";
		try{
			ResultSet rs = st.executeQuery(query);
			while(rs.next()){
				isPreCalculated = true;
			}
			
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		closeConn();
		return isPreCalculated;
	}

}
