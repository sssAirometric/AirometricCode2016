package com.report.dao.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.model.DBUtil;
import com.report.dao.KpiDao;
import com.report.helper.JSONObject;

public class KpiDaoImpl implements KpiDao{

	public List<Double> getLongitude_Lattitude(String cityName) {
		List<Double>latLongList=new ArrayList<Double>();
		try {
			String responseJson = getHttp("http://maps.googleapis.com/maps/api/geocode/json?address="+cityName+"&sensor=true");
			JSONObject newObject = new JSONObject(responseJson);
			JSONObject resObject = newObject.getJSONArray("results")
					.getJSONObject(0).getJSONObject("geometry")
					.getJSONObject("location");
			double lati = new Double(resObject.getString("lat"));
			double lng = new Double(resObject.getString("lng"));
			latLongList.add(lati);
			latLongList.add(lng);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return latLongList;
	}
	
	
	public List<String>getGeoLocationParametersForMarket(String market,String testName){
		java.sql.Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		List<String> listUserData = new ArrayList<String>();
		PreparedStatement pst = null;
		try {
			conn=DBUtil.getConnection();
			stmt = conn.createStatement();
			String stgQuery = "SELECT GEOLOCATION_LATITUDE,GEOLOCATION_LANGITUDE" +
					" FROM STG_DEVICE_INFO WHERE MARKET_ID IN("+market+") AND  GEOLOCATION_LATITUDE!='0.0' AND  GEOLOCATION_LANGITUDE!='0.0' AND TEST_NAME like '"+testName+"%' ORDER BY TIME_STAMP_FOREACH_SAMPLE ASC  LIMIT 1 ";
			System.out.println("stgQuery GEOLOCATION:>>"+stgQuery);
			rs = stmt.executeQuery(stgQuery);
			while (rs.next()) {
					listUserData.add(rs.getString(1));
					listUserData.add(rs.getString(2));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		return listUserData;
	}
	
	public List<String>getGeoLocationParameters(String device,String testname,String market){
		java.sql.Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		List<String> listUserData = new ArrayList<String>();
		PreparedStatement pst = null;
		String test_name = testname + "\\-%";
		try {
			conn=DBUtil.getConnection();
			stmt = conn.createStatement();
			String stgQuery = "SELECT GEOLOCATION_LATITUDE,GEOLOCATION_LANGITUDE" +
					" FROM STG_DEVICE_INFO WHERE TEST_NAME like '"+ test_name + "' " +
					"AND MARKET_ID IN("+market+") AND DEVICE_MODEL IN('"+device+"') ORDER BY TIME_STAMP_FOREACH_SAMPLE ASC  LIMIT 1 ";
			rs = stmt.executeQuery(stgQuery);
			while (rs.next()) {
				listUserData.add(rs.getString(1));
				listUserData.add(rs.getString(2));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		return listUserData;
	}
	
	static public String getHttp(String url) throws IOException {
		URL yahoo = new URL(url);
		URLConnection yc = yahoo.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				yc.getInputStream()));
		String inputLine = "";
		String resp = "";
		while ((inputLine = in.readLine()) != null)
			resp = resp + inputLine;
		in.close();
		return resp;
	}

}
