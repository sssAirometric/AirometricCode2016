package com.report.healthindex;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.model.DBUtil;

public class HealthIndexDBHelper {

	public static float getDC3GvsLTEIndex(){
		float index = 0;
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		float lteCount = 0;
		float totalCount = 0;
		String query = "SELECT COUNT(*) AS TOTALCOUNT FROM STG_NET_RESULTS";
		String queryForLte = "SELECT COUNT(*) AS LTECOUNT FROM STG_NET_RESULTS WHERE NETWORKTYPE='LTE'";
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			rs = st.executeQuery(query);
			while(rs.next()){
				totalCount = rs.getInt("TOTALCOUNT");
			}
			rs = st.executeQuery(queryForLte);
			while(rs.next()){
				lteCount = rs.getInt("LTECOUNT");
			}
			index = (lteCount/totalCount)*100;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		finally{
			try{
				conn.close();
			}
			catch (Exception e) {
				// TODO: handle exception
			}
			
		}
//		System.out.println(index);
		return index;
	}
	
	public static void main(String[] args) {
		getDC3GvsLTEIndex();
	}
}
