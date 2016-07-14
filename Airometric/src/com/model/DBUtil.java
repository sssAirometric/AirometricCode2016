package com.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import com.PropertyFileReader;

public class DBUtil {
	/**
	 * Gets connection from DB
	 * 
	 * @return
	 */
	static Connection con = null;
	static HashMap<String, String> propertiesFiledata = PropertyFileReader
			.getProperties();

	public static Connection getConnection() {
		Connection con = null;
		try {
			String databaseUrl = null;
			String databaseUsername = null;
			String databasePassword = null;
			String environment = propertiesFiledata.get("ENVIRONMENT");
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			databaseUrl = propertiesFiledata.get(environment + "_DATABASE_URL");
			databaseUsername = propertiesFiledata.get(environment
					+ "_DATABASE_USERNAME");
			databasePassword = propertiesFiledata.get(environment
					+ "_DATABASE_PASSWORD");
//			System.out.println("databaseUrl------" + databaseUrl);
			con = DriverManager.getConnection(databaseUrl, databaseUsername,
					databasePassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	}

	public static Connection openConn() {
		try {
			if (null == con || con.isClosed()) {
				con = getConnection();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return con;
	}

	public static void closeConn() {
		try {
			con.close();
			con = null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
