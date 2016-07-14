package com.migratedata;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.helper.Misc;
import com.migratedata.TO.ColumnTO;
import com.model.DBUtil;

public class ExportColumns {

	static String databaseUrl = "jdbc:mysql://127.0.0.1:3306/gl_vq";
	static String databaseUsername = "root";
	static String databasePassword = "root";
	static Connection con = null;
	static Statement stmt = null;

	static Connection aw_con = null;
	static Statement aw_stmt = null;
	static int startinrowCount = 0;

	public ExportColumns() {
		super();
		// TODO Auto-generated constructor stub
		try {
			con = DriverManager.getConnection(databaseUrl, databaseUsername,
					databasePassword);
			stmt = con.createStatement();

			aw_con = DBUtil.getConnection();
			aw_stmt = aw_con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		try {

			/*String[] types = { "TABLE" };
			DatabaseMetaData dbmd = con.getMetaData();
			ResultSet rs = dbmd.getTables(null, null, "%", types);*/
			for(int i=0;i<10;i++){
				new ExportColumns();
			}
			String vqType = "Polqa";
//			System.out.println("vqType----------"+vqType);
			if(vqType.equalsIgnoreCase("polqa")){
//				System.out.println("inside if");
			}
			// getData("VQTPOLQA");
			// getData("VQTData", "");

			//			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public List<List<ColumnTO>> getData(String access_tableName,
			String mysqlTableName, String marketId, String testName,
			String devicename, String vqType, int diffHours) {
		List<List<ColumnTO>> rowWiseData = new ArrayList<List<ColumnTO>>();
		String query = "SELECT * FROM " + access_tableName
				+ " WHERE VQuad_Location LIKE '%" + testName + "%'";
//		System.out.println("---"+query);
		int noCols = 0;
		try {
			ResultSet rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			int numberOfColumns = rsmd.getColumnCount();
			String columnNames[] = new String[numberOfColumns];
			for (int i = 1; i <= numberOfColumns; i++) {
				columnNames[i - 1] = rsmd.getColumnLabel(i);
//				System.out.println(columnNames[i - 1]);
			}
			while (rs.next()) {
				List<ColumnTO> individualRowData = new ArrayList<ColumnTO>();
				for (String columnName : columnNames) {
					ColumnTO cto = new ColumnTO();
					String value = rs.getString(columnName);
					cto.setName(columnName);
					cto.setValue(value);
					individualRowData.add(cto);
				}
				noCols++;
				rowWiseData.add(individualRowData);

			}
			if (vqType.equalsIgnoreCase("polqa")) {
				Polqa.mappCols();
				insertData(mysqlTableName, marketId, testName, devicename,
						rowWiseData, Polqa.mappingPolqaColumns,
						getTimeCols("polqa"), diffHours);
				insertFileHistory(testName, "VQTResults", devicename, marketId);
			} else if (vqType.equalsIgnoreCase("pesq")){
				VoiceQuality.mappCols();
				insertData(mysqlTableName, marketId, testName, devicename,
						rowWiseData, VoiceQuality.mappingPesqColumns,
						getTimeCols("polqa"), diffHours);
				insertFileHistory(testName, "VQTResults", devicename, marketId);
			}
			else {
				CallEvents.mappCols();
				insertData(mysqlTableName, marketId, testName, devicename,
						rowWiseData, CallEvents.mappingCallEventsColumns,
						getTimeCols("CallEvents"), diffHours);
				insertFileHistory(testName, "mo", devicename, marketId);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				con.close();
				aw_con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return rowWiseData;
	}

	static void insertFileHistory(String testName, String testType,
			String deviceModel, String marketId) {
		String query = "INSERT INTO file_history (TEST_NAME,FILE_NAME,MARKET_ID,TEST_TYPE,DEVICE_MODEL,ACTIVE,DATA_ID) VALUES('"
				+ testName
				+ "',"
				+ "'"
				+ deviceModel
				+ "_"
				+ testType
				+ "',"
				+ "'"
				+ marketId
				+ "','"
				+ testType
				+ "','"
				+ deviceModel
				+ "',1,1)";
		try {
//			System.out.println(query);
			aw_stmt.executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static void insertData(String tableName, String marketId, String testName,
			String deviceModel, List<List<ColumnTO>> rowWiseData,
			Map<String, String> mappingColumns, List<String> callStampCols,
			int diffHours) {
		try {
			for (int i = 0; i < rowWiseData.size(); i++) {
				List<ColumnTO> rowData = rowWiseData.get(i);
				StringBuilder insertQuery = new StringBuilder();
				StringBuilder coulmnnames = new StringBuilder();
				StringBuilder values = new StringBuilder();
				coulmnnames.append("DEVICE_MODEL").append(",");
				values.append("'").append(deviceModel).append("'").append(",");
				coulmnnames.append("TEST_NAME").append(",");
				values.append("'").append(testName).append("'").append(",");
				coulmnnames.append("MARKET_ID").append(",");
				values.append("'").append(marketId).append("'").append(",");
				for (ColumnTO cto : rowData) {
					String mappingCol = mappingColumns.get(cto.getName());

					if (null != mappingCol) {
						coulmnnames.append(mappingCol).append(",");
						String colValue = cto.getValue();
				/*		if (callStampCols.contains(mappingCol)) {
							colValue = Misc.addOrsubHours(cto.getValue(),
									diffHours, "MM/dd/yyyy HH:mm:ss");
						}*/
						values.append("'").append(colValue).append("'").append(
								",");
					}
				}
				coulmnnames.setLength(coulmnnames.length() - 1);
				values.setLength(values.length() - 1);

				insertQuery.append("INSERT INTO ").append(tableName)
						.append("(").append(coulmnnames).append(")").append(
								"VALUES").append("(").append(values)
						.append(")");
//				System.out.println(insertQuery.toString());
				aw_stmt.executeUpdate(insertQuery.toString());
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	static List<String> getTimeCols(String tablename) {
		List<String> timeStampCols = new ArrayList<String>();
		if (tablename.equalsIgnoreCase("polqa")) {
			timeStampCols.add("VQuad_Timestamp");
			timeStampCols.add("Call_Timestamp");
			
		} else {
			timeStampCols.add("VQuadTimestamp");
			timeStampCols.add("CallTimestamp");
		}
		return timeStampCols;
	}
	
}
