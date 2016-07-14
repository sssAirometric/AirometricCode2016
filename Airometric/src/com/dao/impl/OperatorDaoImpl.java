package com.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

import com.dao.OperatorDao;
import com.model.DBUtil;

public class OperatorDaoImpl implements OperatorDao{
	public Map<String, String> getActiveOperatorsInMap() {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		Map<String,String> operatorMap = new LinkedHashMap<String,String>();
		try {
			conn = DBUtil.getConnection();
			String query = "SELECT OPERATOR_ID,OPERATOR_NAME FROM OPERATOR WHERE ACTIVE=1 ORDER BY OPERATOR_NAME";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			operatorMap.put("--Select Operator--", "0");
			while (rs.next()) {
				operatorMap.put(rs.getString("OPERATOR_NAME"), rs.getString("OPERATOR_ID"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		return operatorMap;
	}
}
