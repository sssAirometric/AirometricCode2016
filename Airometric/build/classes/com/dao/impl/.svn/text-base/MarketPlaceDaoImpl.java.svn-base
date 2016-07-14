package com.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dao.MarketPlaceDao;
import com.model.DBUtil;
import com.to.MarketBean;
import com.to.MarketPlaceBean;
import com.to.UserMarketInfo;
import com.util.DateUtil;

public class MarketPlaceDaoImpl implements MarketPlaceDao  {
	List<MarketPlaceBean> marketPlaceData = new ArrayList<MarketPlaceBean>();
	List<MarketPlaceBean> marketData = new ArrayList<MarketPlaceBean>();
	List<MarketPlaceBean> countryData = new ArrayList<MarketPlaceBean>();
	List<MarketPlaceBean> marketOperatorData = new ArrayList<MarketPlaceBean>();
	public List<MarketPlaceBean> getMarketPlaceList() {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
        int i=1;
		PreparedStatement pst = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String marketPlaceQuery = 
					"SELECT DISTINCT M.MARKET_ID, M.MARKET_NAME, C.CITY_NAME, S.STATE_NAME, CN.COUNTRY_NAME, M.ACTIVE, " +
					"U.USER_NAME, U.USER_ID ,M.CREATED_BY, M.CREATED_DATE " +
					"FROM MARKET M, CITY C, STATE S, COUNTRY CN, USERS U " +
					"WHERE  M.COUNTRY_ID = CN.COUNTRY_ID " +
					"AND M.CREATED_BY = U.USER_ID ";
			//System.out.println("marketPlaceQuery:"+marketPlaceQuery);
			rs = st.executeQuery(marketPlaceQuery);
			while (rs.next()) {
				MarketPlaceBean marketPlaceBean = new MarketPlaceBean();
				marketPlaceBean.setMarketId(rs.getString("MARKET_ID"));
				marketPlaceBean.setMarketName(rs.getString("MARKET_NAME"));
				marketPlaceBean.setCityName(rs.getString("CITY_NAME"));
				marketPlaceBean.setStateName(rs.getString("STATE_NAME"));
				marketPlaceBean.setCountryName(rs.getString("COUNTRY_NAME"));
				if(rs.getString("ACTIVE").equals("1")){
					marketPlaceBean.setStatusString("ACTIVE");
					marketPlaceBean.setButtonLable("Deactive");
				}else{
					marketPlaceBean.setStatusString("INACTIVE");
					marketPlaceBean.setButtonLable("Active");
				}
				marketPlaceBean.setCreatedBy(rs.getString("USER_NAME"));
				Date dt = rs.getDate("CREATED_DATE");
				String createDate = dateFormat.format(dt);
				marketPlaceBean.setCreatedDate(createDate);
				marketPlaceBean.setSlno(""+i);

				i++;
				marketPlaceData.add(marketPlaceBean);
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
		return marketPlaceData;
	}
	public List<MarketPlaceBean> getMarketList() {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
        int i=1;
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			/*String usersQuery = "SELECT DISTINCT M.MARKET_ID, M.MARKET_NAME, C.CITY_NAME, S.STATE_NAME, " +
					"CN.COUNTRY_NAME, M.CREATED_BY,U.USER_NAME,M.CREATED_DATE " +
					"FROM MARKET M, CITY C, STATE S, COUNTRY CN, USERS U " +
					"WHERE M.CITY_ID = C.CITY_ID " +
					"AND M.STATE_ID = S.STATE_ID " +
					"AND M.COUNTRY_ID = CN.COUNTRY_ID " +
					"AND M.CREATED_BY = U.USER_ID;"; includes city,state*/
			String usersQuery = "SELECT DISTINCT M.MARKET_ID, M.MARKET_NAME, " +
					"CN.COUNTRY_NAME, M.CREATED_BY,U.USER_NAME,M.CREATED_DATE " +
					"FROM MARKET M, CITY C, STATE S, COUNTRY CN, USERS U " +
					"WHERE M.COUNTRY_ID = CN.COUNTRY_ID " +
					"AND M.CREATED_BY = U.USER_ID;";
			//System.out.println("usersQuery:"+usersQuery);
			rs = st.executeQuery(usersQuery);
			while (rs.next()) {
				MarketPlaceBean marketPlaceBean = new MarketPlaceBean();
				marketPlaceBean.setMarketId(rs.getString("MARKET_ID"));
				marketPlaceBean.setMarketName(rs.getString("MARKET_NAME"));
				marketPlaceBean.setCountryName(rs.getString("COUNTRY_NAME"));
				marketPlaceBean.setCreatedBy(rs.getString("USER_NAME"));
				marketPlaceBean.setCreatedDate(rs.getString("CREATED_DATE"));
				marketPlaceBean.setSlno(""+i);
				i++;
				marketData.add(marketPlaceBean);
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
		return marketData;
	}
	public List<MarketPlaceBean> getCountryList() {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
        int i=1;
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String countryQuery = "SELECT * FROM COUNTRY";
			////System.out.println("usersQuery:"+usersQuery);
			rs = st.executeQuery(countryQuery);
			while (rs.next()) {
				MarketPlaceBean marketPlaceBean = new MarketPlaceBean();
				marketPlaceBean.setCountryId(rs.getString("COUNTRY_ID"));
				marketPlaceBean.setCountryName(rs.getString("COUNTRY_NAME"));
				marketPlaceBean.setSlno(""+i);
				i++;
				countryData.add(marketPlaceBean);
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
		return countryData;
	}
	
	public Map<String, String> getCountryMap() {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		Map<String,String> countryMap = new LinkedHashMap<String,String>();
		try {
			conn = DBUtil.getConnection();
			String query = null;
				query = "SELECT COUNTRY_ID,COUNTRY_NAME FROM COUNTRY ";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			countryMap.put("--Select Country", "0");
			while (rs.next()) {
				countryMap.put(rs.getString("COUNTRY_NAME"), rs.getString("COUNTRY_ID"));
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
		return countryMap;
	}
	public Map<String, String> getStateIdsForCountry(String countryId) {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		Map<String, String> stateMap = new LinkedHashMap<String, String>();
		stateMap.put("Select State", "0");
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String query = "SELECT DISTINCT STATE_ID,STATE_NAME FROM STATE WHERE COUNTRY_ID ='"+countryId+"' ";
			//System.out.println(query);
			rs = st.executeQuery(query);
			while (rs.next()) {
				String stateId = rs.getString("STATE_ID");
				String stateName = rs.getString("STATE_NAME");
				stateMap.put(stateName, stateId);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return stateMap;
	}

	public Map<String, String> getCityIdsForState(String stateId) {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		Map<String, String> cityMap = new LinkedHashMap<String, String>();
		cityMap.put("Select City", "0");
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String query = "SELECT DISTINCT CITY_ID,CITY_NAME FROM CITY WHERE STATE_ID ='"+stateId+"' ";
			//System.out.println(query);
			rs = st.executeQuery(query);
			while (rs.next()) {
				String cityId = rs.getString("CITY_ID");
				String cityName = rs.getString("CITY_NAME");
				cityMap.put(cityName, cityId);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return cityMap;
	}
	public boolean checkMarketExists(String marketName,String countryId) {
		boolean status = false;
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs=null;
		PreparedStatement pst=null;
		try {
			conn = DBUtil.getConnection();
			stmt = conn.createStatement();
			String removeQuery = "SELECT COUNT(*) FROM MARKET WHERE MARKET_NAME='"+marketName+"' AND COUNTRY_ID = '"+countryId+"'";
			rs=stmt.executeQuery(removeQuery);
			while(rs.next()){
			   int count=rs.getInt(1);
			   if(count>=1){
				   status = true;
			   }
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return status;
	}
	public String addMarket(String userId,String marketName,String countryId,String stateId,String cityId) {
		Connection conn = null;
		Statement st = null;
		String status = "failure";
		ResultSet rs = null;
		PreparedStatement pst = null;
		String date = DateUtil.getCurrentDateTime();
		int rows=0;
		int active= 1;
		int count=0;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			count=checkOtherMarketExist(marketName,countryId,stateId,cityId);
			//System.out.println("count----------------"+count);
			if(count>0){
				String deviceQuery = "INSERT INTO MARKET (MARKET_NAME,CREATED_DATE,CREATED_BY," +
						"COUNTRY_ID,CITY_ID,STATE_ID,ACTIVE)VALUES (?,?,?,?,?,?,?)";
			    pst = conn.prepareStatement(deviceQuery);
				pst.setString(1, marketName);
				pst.setString(2, date);
				pst.setString(3, userId);
				pst.setString(4, countryId);
				pst.setString(5, stateId);
				pst.setString(6, cityId);
				pst.setInt(7, active);
				rows=pst.executeUpdate();
				if(rows>0){
					status="success";
				}
			}else{
				for(int k=0;k<2;k++){
					if(k==1){
						marketName="others";
					}
					String deviceQuery = "INSERT INTO MARKET (MARKET_NAME,CREATED_DATE,CREATED_BY," +
					"COUNTRY_ID,CITY_ID,STATE_ID,ACTIVE)VALUES (?,?,?,?,?,?,?)";
				    pst = conn.prepareStatement(deviceQuery);
					pst.setString(1, marketName);
					pst.setString(2, date);
					pst.setString(3, userId);
					pst.setString(4, countryId);
					pst.setString(5, stateId);
					pst.setString(6, cityId);
					pst.setInt(7, active);
					rows=pst.executeUpdate();
					if(rows>0){
						status="success";
					}
				}
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
		return status;

	}
	
	public static int checkOtherMarketExist(String marketName,String countryId,String stateId,String cityId){
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
        int count=0;
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String usersQuery = "SELECT COUNT(MARKET_ID) FROM MARKET WHERE COUNTRY_ID='"+countryId+"' AND ACTIVE='1'";
			//System.out.println("usersQuery:"+usersQuery);
			rs = st.executeQuery(usersQuery);
			while (rs.next()) {
				count=rs.getInt(1);
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
		return count;
	}
	public List<MarketPlaceBean> getMarketOperatorList() {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
        int i=1;
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String usersQuery = "SELECT DISTINCT M.MARKET_ID, M.MARKET_NAME,O.OPERATOR_ID,O.OPERATOR_NAME , U.USER_NAME FROM MARKET M ," +
					"OPERATOR O ,MARKET_OPERATOR MO ,USERS U WHERE M.MARKET_ID=MO.MARKET_ID AND O.OPERATOR_ID=MO.OPERATOR_ID AND " +
					"M.CREATED_BY = U.USER_ID";
			//System.out.println("usersQuery:"+usersQuery);
			rs = st.executeQuery(usersQuery);
			while (rs.next()) {
				MarketPlaceBean marketPlaceBean = new MarketPlaceBean();
				marketPlaceBean.setMarketId(rs.getString("MARKET_ID"));
				marketPlaceBean.setMarketName(rs.getString("MARKET_NAME"));
				marketPlaceBean.setOperatorId(rs.getString("OPERATOR_ID"));
				marketPlaceBean.setOperatorName(rs.getString("OPERATOR_NAME"));
				marketPlaceBean.setCreatedBy(rs.getString("USER_NAME"));
				marketPlaceBean.setSlno(""+i);

				i++;
				marketOperatorData.add(marketPlaceBean);
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
		return marketOperatorData;
	}
	public Map<String, String> getMarketPlaceMap() {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		Map<String,String> marketPlaceMap = new LinkedHashMap<String,String>();
		try {
			conn = DBUtil.getConnection();
			String query = null;
				query = "SELECT MARKET_ID,MARKET_NAME FROM MARKET ";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			//marketPlaceMap.put("--Select Market Place--", "0");
			while (rs.next()) {
				marketPlaceMap.put(rs.getString("MARKET_NAME"), rs.getString("MARKET_ID"));
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
		return marketPlaceMap;
	}
	public Map<String, String> getUploadMarketPlaceMap(String role,String userId) {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		Map<String,String> marketPlaceMap = new LinkedHashMap<String,String>();
		try {
			conn = DBUtil.getConnection();
			String query = null;
			if(role.equals("superadmin")){
				query = "SELECT MARKET_ID,MARKET_NAME FROM MARKET WHERE MARKET_NAME!='others'";
			}else{
				query = "SELECT M.MARKET_ID,M.MARKET_NAME FROM MARKET M ,USER_MARKET U " +
				"WHERE U.USER_ID='"+userId+"' AND U.MARKET_ID=M.MARKET_ID ";
			}
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				marketPlaceMap.put(rs.getString("MARKET_NAME"), rs.getString("MARKET_ID"));
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
		return marketPlaceMap;
	}
	
	
	public Map<String, String> getOperatorMap() {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		Map<String,String> operatorMap = new LinkedHashMap<String,String>();
		try {
			conn = DBUtil.getConnection();
			String query = null;
				query = "SELECT OPERATOR_ID,OPERATOR_NAME FROM OPERATOR ";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			operatorMap.put("--Select Opeartor--", "0");
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
	
	public String assignMarket(String userId,String marketId,String operatorId) {
		Connection conn = null;
		Statement st = null;
		String status = "failure";
		ResultSet rs = null;
		PreparedStatement pst = null;
		String date = DateUtil.getCurrentDateTime();
		int rows=0;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String deviceQuery = "INSERT INTO MARKET_OPERATOR (MARKET_ID,OPERATOR_ID,CREATED_BY,CREATED_DATE)VALUES (?,?,?,?)";
			    pst = conn.prepareStatement(deviceQuery);
				pst.setString(1, marketId);
				pst.setString(2, operatorId);
				pst.setString(3, userId);
				pst.setString(4, date);
				
				rows=pst.executeUpdate();
				if(rows>0){
					status="success";
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
		return status;

	}
	public String getMarketDetail(String marketId) {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		Statement st1 = null;
		String active="";
		String status="failure";
		int count = 0;

		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String marketActivationQuery = "SELECT ACTIVE FROM MARKET WHERE MARKET_ID='"+marketId+"'";
			rs = st.executeQuery(marketActivationQuery);

			while(rs.next()) {
				if(rs.getString("ACTIVE").equals("1")){
					active="0";
				}else{
					active="1";
				}
			}
			if(active=="0"){
				count = getmarketAssignedToUserCount(marketId);
			}
			//System.out.println("count :"+count);
			
			if(active=="1" || (active=="0" && count==0)){
				String update = "UPDATE MARKET SET ACTIVE="+active+" WHERE MARKET_ID='"+marketId+"'";
				//System.out.println("update query :"+update);
				st1 = conn.createStatement();
				st1.executeUpdate(update);
				status="success";
			}else{
				status="failure";
			}

			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}		

		return status;
	}
	
	public int getmarketAssignedToUserCount(String marketId){
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		Statement st1 = null;
		String active="";
		String status="failure";
		int count = 0;

		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String marketActivationQuery = "SELECT COUNT(*) FROM USER_MARKET WHERE MARKET_ID='"+marketId+"'";
			rs = st.executeQuery(marketActivationQuery);

			while(rs.next()) {
				count=rs.getInt(1);
			}
			//System.out.println("count:"+count);
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}		

		return count;
	}

	public Map<String, String> getMarketPlaceMapForCountry(String countryId) {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		Map<String,String> marketPlaceMap = new LinkedHashMap<String,String>();
		try {
			conn = DBUtil.getConnection();
			String query = null;
				query = "SELECT MARKET_ID,MARKET_NAME FROM MARKET WHERE ACTIVE =1 AND COUNTRY_ID='"+countryId+"'";
			//System.out.println("query-----coun-------"+query);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			//marketPlaceMap.put("--Select Market Place--", "0");
			while (rs.next()) {
				marketPlaceMap.put(rs.getString("MARKET_NAME"), rs.getString("MARKET_ID"));
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
		return marketPlaceMap;
	}
	
	public String getOtherMarketId(String countryId) {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		String otherMarketId=null;
		try {
			conn = DBUtil.getConnection();
			String query = null;
				query = "SELECT MARKET_ID  FROM MARKET WHERE ACTIVE =1 AND COUNTRY_ID='"+countryId+"' and MARKET_NAME ='others'";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				otherMarketId=rs.getString(1);
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
		return otherMarketId;
	}
	public Map<String,String> getMarketForUser(String userId) {
		MarketBean marketBean = new MarketBean();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		Map<String, String> marketMap = new HashMap<String, String>();
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String userQuery = "SELECT UM.MARKET_ID,M.MARKET_NAME FROM USER_MARKET UM, MARKET M WHERE UM.MARKET_ID=M.MARKET_ID AND UM.USER_ID='"+userId+ "'";
//			System.out.println("userQuery----------"+userQuery);
			
			rs = st.executeQuery(userQuery);
			List<UserMarketInfo>  userMarketInfoList = new  ArrayList<UserMarketInfo>();
			while(rs.next()) {
				marketMap.put(rs.getString("M.MARKET_NAME"), rs.getString("UM.MARKET_ID"));
			}
			marketBean.setUserMarketInfos(userMarketInfoList);
		}catch (Exception e) {
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
		return marketMap;
	}
	
	public List<Integer> getMarketIDListForUser(String userId) {
		List<Integer> marketIdlist = new ArrayList<Integer>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String userQuery = "SELECT UM.MARKET_ID FROM USER_MARKET UM, MARKET M WHERE UM.MARKET_ID=M.MARKET_ID AND UM.USER_ID='"+userId+ "' AND UM.ACTIVE=1";
			//System.out.println("userQuery----1------"+userQuery);
			
			rs = st.executeQuery(userQuery);
			List<UserMarketInfo>  userMarketInfoList = new  ArrayList<UserMarketInfo>();
			while(rs.next()) {
				marketIdlist.add(rs.getInt("UM.MARKET_ID"));
			}
		}catch (Exception e) {
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
		return marketIdlist;
	}
	
}
