package com.role;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.model.DBUtil;

public class TransferTest {

	public String moveManagerFiles(String[] testNames,String userId){
		Connection conn = DBUtil.openConn();
		StringBuilder query = new StringBuilder();
		try{
			Statement stmt = conn.createStatement();
			for(String testName:testNames){
//				System.out.println("testName------"+testName);
				query = new StringBuilder();
				query.append("INSERT INTO user_transfered_test(USER_ID,TEST_NAME) VALUES('"+userId+"','"+testName+"'");
				query.append(")");
//				System.out.println(query.toString());
				stmt.executeUpdate(query.toString());
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		finally{
			DBUtil.closeConn();
		}
		return "";
	}
}
