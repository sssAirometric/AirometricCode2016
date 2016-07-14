package com.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import com.dao.TestConfigUserDao;
import com.model.DBUtil;
import com.to.TestConfig;
import com.util.DateUtil;

public class TestConfigUserDaoImpl implements TestConfigUserDao{
	/**
	 * This method is used to assign the testconfig
	 */
	public void assignTestConfigToUser(String selTestConfigId,
			String selOperatorID, String selUserID,String seldeviceID,String userId) {
		PreparedStatement pst = null;
		Connection conn = null;
		ResultSet rs = null;
		try {
			conn = DBUtil.getConnection();
			String query = "INSERT INTO TEST_CONFIG_USER(TEST_CONFIG_ID,OPERATOR_ID,USER_ID,DEVICE_ID,ACTIVE,CREATED_DATE,MODIFIED_DATE,CREATED_BY,MODIFIED_BY) " +
							"VALUES (?,?,?,?,?,?,?,?,?)";
			pst = conn.prepareStatement(query);
			pst.setString(1, selTestConfigId);
			pst.setString(2, selOperatorID);
			pst.setString(3, selUserID);
			pst.setString(4, seldeviceID);
			pst.setInt(5, 1);
			String date = DateUtil.getCurrentDateTime();
			pst.setString(6, date);
			pst.setString(7, date);
			//TODO need to remove the hardcode userID,has to be replaced with the userID logged in
			pst.setString(8, userId);
			pst.setString(9, userId);
			pst.executeUpdate();
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
	}
	public boolean checkTestConfigalreadyAssigned(String selTestConfigId,
			String selOperatorID, String selUserID,String selImei) {
		Statement st = null;
		Connection conn = null;
		ResultSet rs = null;
		boolean assigned = false;
		int count=0;
		try {
			conn = DBUtil.getConnection();
			String query = "SELECT COUNT(*) FROM TEST_CONFIG_USER WHERE TEST_CONFIG_ID='"+selTestConfigId+"' " +
					"AND OPERATOR_ID = '"+selOperatorID+"'" +
					"AND USER_ID ='"+selUserID+"' AND DEVICE_ID='"+selImei+"' ";
			st = conn.createStatement();
			rs = st.executeQuery(query);
			if(rs.next()){
				count=rs.getInt(1);
				if(count>=1){
					assigned = true;
				}
			}
			if(assigned){
				FacesContext context = FacesContext.getCurrentInstance();
				String userId =  context.getExternalContext().getSessionMap().get("loggedInUserID").toString();
				String date = DateUtil.getCurrentDateTime();
				String queryUpdate = "UPDATE TEST_CONFIG_USER SET CREATED_DATE='"+date+"',MODIFIED_DATE='"+date+"',MODIFIED_BY='"+userId+"',TEST_CONFIG_ID='"+selTestConfigId+"' WHERE TEST_CONFIG_ID='"+selTestConfigId+"' AND OPERATOR_ID = '"+selOperatorID+"'" +
					"AND USER_ID ='"+selUserID+"' AND DEVICE_ID='"+selImei+"' ";
				int records = st.executeUpdate(queryUpdate);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		return assigned;
	}
	
	public String getDeviceIdForSelectedIMEI(String selImei,String  selUserID){
		Statement st = null;
		Connection conn = null;
		ResultSet rs = null;
		String deviceId=null;
		try {
			conn = DBUtil.getConnection();
			String query = "SELECT DEVICE_ID FROM USER_DEVICE WHERE IMEI='"+selImei+"' AND USER_ID ='"+selUserID+"'";
			st = conn.createStatement();
			rs = st.executeQuery(query);
			while(rs.next()){
				deviceId=rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}		
		return deviceId;
	}
	
	
	public boolean checkTestConfigAssignedToOperator(String selTestConfigId,
			String selOperatorID) {
		Statement st = null;
		Connection conn = null;
		ResultSet rs = null;
		boolean assigned = false;
		try {
			conn = DBUtil.getConnection();
			String query = "SELECT * FROM TEST_CONFIG_USER WHERE TEST_CONFIG_ID='"+selTestConfigId+"' AND OPERATOR_ID = '"+selOperatorID+"'" +
					"AND USER_ID ='0'";
			st = conn.createStatement();
			rs = st.executeQuery(query);
			while(rs.next()){
				assigned = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		return assigned;
	}
	public List<TestConfig> assignListTestConfig() {
		Statement st = null;
		Connection conn = null;
		ResultSet rs = null;
		List<TestConfig> assignListTestConfig=new ArrayList<TestConfig>();
		int i=1;
		try {
			conn = DBUtil.getConnection();
			String  query="SELECT T.TEST_CONFIG_ID,U.USER_NAME,TC.TEST_CONFIG_NAME ,UD.IMEI,t.CREATED_DATE  FROM TEST_CONFIG_USER  T ," +
					"USERS U, USER_DEVICE UD , TEST_CONFIG TC  WHERE T.TEST_CONFIG_ID=TC.TEST_CONFIG_ID  AND TC.ACTIVE='1' AND T.USER_ID=U.USER_ID " +
					"AND U.ACTIVE='1' AND  T.DEVICE_ID=UD.DEVICE_ID AND t.CREATED_DATE IN(SELECT   MAX(t.CREATED_DATE)  FROM TEST_CONFIG_USER  T ,USERS U, " +
					"USER_DEVICE UD , TEST_CONFIG TC ,(SELECT  MAX(CREATED_DATE) AS max_date , DEVICE_ID AS  DEVICE FROM TEST_CONFIG_USER WHERE USER_ID !=0  " +
					"GROUP BY DEVICE_ID,CREATED_DATE)  b  WHERE T.TEST_CONFIG_ID=TC.TEST_CONFIG_ID  AND TC.ACTIVE='1' AND T.USER_ID=U.USER_ID " +
					"AND U.ACTIVE='1' AND  T.DEVICE_ID=UD.DEVICE_ID  and b.max_date = t.CREATED_DATE AND b.DEVICE=T.DEVICE_ID  GROUP BY UD.IMEI    ORDER BY U.USER_NAME )  ORDER BY U.USER_NAME  ";
			st = conn.createStatement();
			rs = st.executeQuery(query);
			while(rs.next()){
				TestConfig testConfig = new TestConfig();
				testConfig.setTestConfigId(rs.getString(1));
				testConfig.setUser_Name(rs.getString(2));
				testConfig.setTestConfigName(rs.getString(3));
				testConfig.setImei(rs.getString(4));
				testConfig.setSlNO(""+i);
				i++;
				assignListTestConfig.add(testConfig);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}		
		return assignListTestConfig;
	}
	
	public static void main(String[] args) {
		TestConfigUserDao dao=new TestConfigUserDaoImpl();
		dao.assignListTestConfig();
	}
	public List<TestConfig> assignListTestConfigOperator(String operator,String userId) {
		Statement st = null;
		Connection conn = null;
		ResultSet rs = null;
		List<TestConfig> assignListTestConfig=new ArrayList<TestConfig>();
		TestConfig testConfig =null;
		int i=1;
		try {
			conn = DBUtil.getConnection();
			
			/*String query="SELECT    T.TEST_CONFIG_ID,U.USER_NAME,TC.TEST_CONFIG_NAME ,UD.IMEI,t.CREATED_DATE  FROM TEST_CONFIG_USER  T ,USERS U, " +
					" USER_DEVICE UD , TEST_CONFIG TC  WHERE T.TEST_CONFIG_ID=TC.TEST_CONFIG_ID  AND TC.ACTIVE='1' AND T.USER_ID=U.USER_ID " +
					" AND U.ACTIVE='1' AND U.CREATED_BY='"+userId+"' AND  T.DEVICE_ID=UD.DEVICE_ID  AND T.OPERATOR_ID='1' AND t.CREATED_DATE IN(SELECT   MAX(t.CREATED_DATE)  FROM TEST_CONFIG_USER  T ,USERS U," +
					" USER_DEVICE UD , TEST_CONFIG TC ,(SELECT  MAX(CREATED_DATE) AS max_date , DEVICE_ID AS  DEVICE FROM TEST_CONFIG_USER WHERE USER_ID !=0 " +
					" GROUP BY DEVICE_ID,CREATED_DATE)  b  WHERE T.TEST_CONFIG_ID=TC.TEST_CONFIG_ID  AND TC.ACTIVE='1' AND T.USER_ID=U.USER_ID " +
					" AND U.ACTIVE='1' AND  T.DEVICE_ID=UD.DEVICE_ID  AND T.OPERATOR_ID='1' and b.max_date = t.CREATED_DATE AND b.DEVICE=T.DEVICE_ID " +
					" GROUP BY UD.IMEI  ORDER BY U.USER_NAME )  ORDER BY U.USER_NAME";
			*/
			
			String query="SELECT    T.TEST_CONFIG_ID,U.USER_NAME,TC.TEST_CONFIG_NAME ,UD.IMEI,t.CREATED_DATE  FROM TEST_CONFIG_USER  T ,USERS U, " +
					"USER_DEVICE UD , TEST_CONFIG TC  WHERE T.TEST_CONFIG_ID=TC.TEST_CONFIG_ID  AND TC.ACTIVE='1' AND T.USER_ID=U.USER_ID " +
					"AND U.ACTIVE='1'  AND  T.DEVICE_ID=UD.DEVICE_ID  AND T.OPERATOR_ID='"+operator+"'  AND t.CREATED_DATE IN(SELECT   MAX(t.CREATED_DATE)  FROM TEST_CONFIG_USER  T ,USERS U," +
					"USER_DEVICE UD , TEST_CONFIG TC ,(SELECT  MAX(CREATED_DATE) AS max_date , DEVICE_ID AS  DEVICE FROM TEST_CONFIG_USER WHERE USER_ID !=0 " +
					"GROUP BY DEVICE_ID,CREATED_DATE)  b  WHERE T.TEST_CONFIG_ID=TC.TEST_CONFIG_ID  AND TC.ACTIVE='1' AND T.USER_ID=U.USER_ID " +
					"AND U.ACTIVE='1' AND  T.DEVICE_ID=UD.DEVICE_ID  AND T.OPERATOR_ID='"+operator+"' AND T.CREATED_BY IN ("+userId+")  and b.max_date = t.CREATED_DATE AND b.DEVICE=T.DEVICE_ID " +
					"GROUP BY UD.IMEI  ORDER BY U.USER_NAME)  ORDER BY U.USER_NAME";
			//System.out.println("query----------"+query);
			st = conn.createStatement();
			rs = st.executeQuery(query);
			while(rs.next()){
			    testConfig = new TestConfig();
				testConfig.setTestConfigId(rs.getString(1));
				testConfig.setUser_Name(rs.getString(2));
				testConfig.setTestConfigName(rs.getString(3));
				testConfig.setImei(rs.getString(4));
				testConfig.setSlNO(""+i);
				i++;
				assignListTestConfig.add(testConfig);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		return assignListTestConfig;
	}
}
