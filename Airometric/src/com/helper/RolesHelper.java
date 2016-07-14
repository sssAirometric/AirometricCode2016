package com.helper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.model.DBUtil;

public class RolesHelper {

	public String getUserRole(String userId){
		String query = "SELECT R.ROLE_NAME AS ROLE_NAME FROM ROLES R,USER_ROLE UR WHERE UR.ROLE_ID=R.ROLE_ID AND UR.USER_ID="+userId;
		Connection conn = DBUtil.openConn();
		String roleName = "";
		try{
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				roleName = rs.getString("ROLE_NAME");
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		finally{
			DBUtil.closeConn();
		}
		
		return roleName;
	}
}
