package com.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import javax.faces.context.FacesContext;

import com.constants.Roles;
import com.dao.UserDao;
import com.model.DBUtil;
import com.preprocessorhelpers.VoiceConnectivityProccesorHelper;
import com.services.ResponseStatusENUM;
import com.to.DeviceInfoTO;
import com.to.MarketInfo;
import com.to.UserBean;
import com.to.UserDeviceInfo;
import com.util.DateUtil;

public class UserDaoImpl implements UserDao {

	final static Logger logger = Logger.getLogger(UserDaoImpl.class);

	List<UserBean> listUserData = new ArrayList<UserBean>();
	List<UserBean> editUserData = new ArrayList<UserBean>();
	List<UserBean> operatorUserData = new ArrayList<UserBean>();

	/**
	 * This method validates the user
	 */
	public String validateUser(String userName, String passWord, String imei) {
		PreparedStatement pst = null;
		Connection conn = null;
		ResultSet rs = null;
		String userStatus = ResponseStatusENUM.AUTH_FAILURE.getStatus();
		try {
			conn = DBUtil.getConnection();
			String query = "SELECT R.ROLE_NAME,U.TERMS_CONDITIONS_ACCEPTED,U.ACTIVE,UD.DEVICE_ID,UD.USER_ID FROM USERS U,ROLES R,"
					+ "USER_ROLE UR,USER_DEVICE UD,USER_OPERATOR UO WHERE U.USER_NAME = ? AND BINARY(U.PASSWORD)=BINARY(?) "
					+ "AND UR.USER_ID=U.USER_ID AND U.USER_ID=UO.USER_ID AND UR.ROLE_ID=R.ROLE_ID AND U.USER_ID = UD.USER_ID AND UD.IMEI=?";
			pst = conn.prepareStatement(query);
			pst.setString(1, userName);
			pst.setString(2, passWord);
			pst.setString(3, imei);
			rs = pst.executeQuery();
			if (rs.next()) {
				if (rs.getString("ACTIVE").equals("1")) {
					AuthenticateUserDevice(rs.getString("USER_ID"), rs.getString("DEVICE_ID"));
					userStatus = "success" + "," + rs.getString("ROLE_NAME")
							+ "," + rs.getString("TERMS_CONDITIONS_ACCEPTED");
				} else {
					userStatus = ResponseStatusENUM.USER_STATUS_INACTIVE
							.getStatus();
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			}
		}
		return userStatus;
	}

	public UserBean validateUserInfo(String userName, String passWord) {
		PreparedStatement pst = null;
		Connection conn = null;
		ResultSet rs = null;
		UserBean userBean = null;
		try {
			conn = DBUtil.getConnection();
			String query = "SELECT U.USER_ID,R.ROLE_NAME,U.ACTIVE FROM USERS U,ROLES R,USER_ROLE UR "
					+ "WHERE U.USER_NAME = ? AND BINARY(U.PASSWORD)=BINARY(?) AND UR.USER_ID=U.USER_ID AND UR.ROLE_ID=R.ROLE_ID";

			pst = conn.prepareStatement(query);
			pst.setString(1, userName);
			pst.setString(2, passWord);
			System.out.println("validate User at the time of login qry"+pst);
			rs = pst.executeQuery();
			if (rs.next()) {
				userBean = new UserBean();
				userBean.setUserId(rs.getString("USER_ID"));
				userBean.setRole(rs.getString("ROLE_NAME"));
				userBean.setUserStatus(rs.getInt("ACTIVE"));
				// userStatus = "success"+","+rs.getString("ROLE_NAME");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			}
		}
		return userBean;
	}

	public UserBean validateUserInformation(String userName, String passWord) {
		PreparedStatement pst = null;
		Connection conn = null;
		ResultSet rs = null;
		UserBean bean = null;
		boolean isUserValid = false;
		try {
			conn = DBUtil.getConnection();
			String query = " SELECT U.USER_ID,R.ROLE_NAME ,UO.OPERATOR_ID FROM USERS U,ROLES R,USER_ROLE UR ,USER_OPERATOR UO "
					+ " WHERE U.USER_NAME = ? AND BINARY(U.PASSWORD)=BINARY(?) AND U.ACTIVE=1 AND UR.USER_ID=U.USER_ID "
					+ " AND UR.ROLE_ID=R.ROLE_ID  AND   U.USER_ID=UO.USER_ID ";

			pst = conn.prepareStatement(query);
			pst.setString(1, userName);
			pst.setString(2, passWord);
			// logger.error("query---" + pst);
			rs = pst.executeQuery();
			if (rs.next()) {
				isUserValid = true;
				bean = new UserBean();
				bean.setUserId(rs.getString("USER_ID"));
				bean.setRole(rs.getString("ROLE_NAME"));
				bean.setOperatorId(rs.getString("OPERATOR_ID"));
			}
			if (null == bean) {
				query = " SELECT U.USER_ID,R.ROLE_NAME  FROM USERS U,ROLES R,USER_ROLE UR  "
						+ " WHERE U.USER_NAME = ? AND BINARY(U.PASSWORD)=BINARY(?) AND U.ACTIVE=1 AND UR.USER_ID=U.USER_ID "
						+ " AND UR.ROLE_ID=R.ROLE_ID   ";
				pst = conn.prepareStatement(query);
				pst.setString(1, userName);
				pst.setString(2, passWord);
				// logger.error("query-+++--" + pst);
				rs = pst.executeQuery();
				if (rs.next()) {
					isUserValid = true;
					bean = new UserBean();
					bean.setUserId(rs.getString("USER_ID"));
					bean.setRole(rs.getString("ROLE_NAME"));
					bean.setOperatorId("");
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			}
		}
		return bean;
	}

	public Map<String, String> getActiveUserBelongsToOperator(
			String selOperatorID) {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		Map<String, String> userMap = new LinkedHashMap<String, String>();
		try {
			conn = DBUtil.getConnection();
			String query = "SELECT U.USER_ID,U.USER_NAME FROM USERS U, USER_OPERATOR UO "
					+ "WHERE U.USER_ID= UO.USER_ID AND U.ACTIVE=1 AND UO.OPERATOR_ID='"
					+ selOperatorID + "' ORDER BY U.USER_NAME";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			userMap.put("--Select User", "0");
			while (rs.next()) {
				userMap.put(rs.getString("USER_NAME"), rs.getString("USER_ID"));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			}
		}
		return userMap;
	}

	public Map<String, String> getActiveUserBelongsToManager(
			String selOperatorID, String userId) {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		Map<String, String> userMap = new LinkedHashMap<String, String>();
		try {
			conn = DBUtil.getConnection();
			/*
			 * String sql =
			 * "SELECT USER_ID,USER_NAME FROM USERS WHERE USER_ID= '" + userId +
			 * "'  AND ACTIVE=1 " +
			 * "UNION ALL SELECT U.USER_ID,U.USER_NAME FROM USERS U, USER_OPERATOR UO "
			 * + "WHERE U.USER_ID= UO.USER_ID AND U.ACTIVE=1 AND U.CREATED_BY='"
			 * + userId + "'AND UO.OPERATOR_ID='" + selOperatorID +
			 * "'  ORDER BY USER_NAME";
			 */

			String sql = "SELECT USER_ID,USER_NAME FROM USERS WHERE USER_ID IN ("
					+ userId + " ) AND ACTIVE=1 " + "  ORDER BY USER_NAME";
			// logger.error("sql----------"+sql);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			userMap.put("--Select User", "0");
			while (rs.next()) {
				userMap.put(rs.getString("USER_NAME"), rs.getString("USER_ID"));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			}
		}
		return userMap;
	}

	public Map<String, String> getImeiBelongsToUser(String selUserID) {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		Map<String, String> imeiMap = new LinkedHashMap<String, String>();
		try {
			conn = DBUtil.getConnection();
			String query = "SELECT UD.DEVICE_ID,UD.IMEI FROM USERS U, USER_DEVICE UD WHERE U.USER_ID= UD.USER_ID AND UD.USER_ID='"
					+ selUserID + "'";
			stmt = conn.createStatement();
			// logger.error("query--------"+query);
			rs = stmt.executeQuery(query);
			imeiMap.put("--Select IMEI", "0");
			while (rs.next()) {
				imeiMap.put(rs.getString("IMEI"), rs.getString("DEVICE_ID"));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			}
		}
		return imeiMap;
	}

	public String addOperator(String userId, String operatorName,
			int operatorStatus) {
		Connection conn = null;
		Statement st = null;
		String status = "failure";
		ResultSet rs = null;
		PreparedStatement pst = null;
		String date = DateUtil.getCurrentDateTime();
		int rows = 0;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String deviceQuery = "INSERT INTO OPERATOR (OPERATOR_NAME,ACTIVE,CREATED_DATE,MODIFIED_DATE,"
					+ "CREATED_BY,MODIFIED_BY)VALUES (?,?,?,?,?,?)";
			pst = conn.prepareStatement(deviceQuery);
			pst.setString(1, operatorName);
			pst.setInt(2, operatorStatus);
			pst.setString(3, date);
			pst.setString(4, date);
			pst.setString(5, userId);
			pst.setString(6, userId);
			rows = pst.executeUpdate();
			if (rows > 0) {
				status = "success";
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			}
		}
		return status;

	}

	public String createUser(UserBean userBean) {
		FacesContext context = FacesContext.getCurrentInstance();
		Integer userIdLogged = Integer.parseInt(context.getExternalContext()
				.getSessionMap().get("loggedInUserID").toString());
		String validEmail = "false";
		String userName = userBean.getUserName();
		String password = userBean.getPassword();
		String email = userBean.getEmail();
		String roleId = userBean.getRole();
		String operatorId = userBean.getOperatorId();
		String[] mid = userBean.getMarketId();
		String countryId = userBean.getCountryId();
		String imeiLimit = userBean.getImeiLimit();
		int superioruser = userBean.getSuperioruser();
		
		boolean isUserExists = false;
		Connection conn = null;
		Statement st = null;
		String status = "failure";
		ResultSet rs = null;
		PreparedStatement pst = null;
		int userId = 0,imeiCount =0;
		String date = DateUtil.getCurrentDateTime();
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();

			String existingUserQuery = "SELECT USER_NAME FROM USERS WHERE USER_NAME='"
					+ userName + "'";
			rs = st.executeQuery(existingUserQuery);
			if (rs.next()) {
				isUserExists = true;
				userBean.setUserMessage("The UserName already Exists");
				return status + "&&&" + userBean.getUserMessage();
			}

			String count ="1";
			
			validEmail = echeck(email);
			boolean isContinue = true;
			String userRole = getUserRole(String.valueOf(userIdLogged));
			if(userRole.equalsIgnoreCase("admin"))
			{
				//System.out.println("userBean.getUserDeviceInfo().size()-"+userBean.getUserDeviceInfo().size());
				if(userBean.getUserDeviceInfo().size()>0)
				{
					//System.out.println("userBean.getUserDeviceInfo().size()in if-"+userBean.getUserDeviceInfo().size());
				ArrayList tempArray = new ArrayList();
				for(int i=0; i<userBean.getUserDeviceInfo().size();i++)
				{
					tempArray.add(userBean.getUserDeviceInfo().get(i).getImei());
					if(IsDuplicateIMEI(userBean.getUserDeviceInfo().get(i).getImei(), String.valueOf(userIdLogged)))
					{
						status = "IMEI_LIMITED";
						break;
					}
					
				}
				
				int listCount = findDuplicates(tempArray);
				if(listCount > 0) {
					status = "Duplicate IMEI";
					isContinue=false;
				}else if(tempArray.size() > getIMEILimitValue(String.valueOf(userIdLogged))){
					status = "IMEI_LIMITED";
					isContinue=false;
				}
			}
			}
			if ((count.equalsIgnoreCase("IMEI_LIMITED"))||(count.equalsIgnoreCase("IMEI_LIMITED"))) {
				status = "IMEI_LIMITED";
			} else if(isContinue){
				if (isUserExists == false
						&& validEmail.equalsIgnoreCase("valid")) {
					status = "success";
					String userQuery = "INSERT INTO USERS (USER_NAME,PASSWORD,EMAIL,ACTIVE,TERMS_CONDITIONS_ACCEPTED,CREATED_DATE,MODIFIED_DATE,CREATED_BY,MODIFIED_BY,COUNTRY_ID,IMEI_LIMIT,IMEI_COUNT) "
							+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
					pst = conn.prepareStatement(userQuery, Statement.RETURN_GENERATED_KEYS);
					pst.setString(1, userName);
					pst.setString(2, password);
					pst.setString(3, email);
					pst.setInt(4, userBean.getUserStatus());
					pst.setInt(5, 0);
					pst.setString(6, date);
					pst.setString(7, date);
					pst.setInt(8, userIdLogged);
					pst.setInt(9, userIdLogged);
					pst.setString(10, countryId);
					pst.setString(11, imeiLimit);
					//pst.setInt(12, imeiCount);
					/*String tempIMEI = userBean.getUserDeviceInfo().get(0)
							.getImei();
					if ((tempIMEI != null) && (tempIMEI.length() > 0)
							&& (tempIMEI != ""))
						pst.setString(12, "0");
					else*/
						pst.setString(12, "0");
					
					pst.executeUpdate();
					rs = pst.getGeneratedKeys();
					rs.next();
					userId = rs.getInt(1);

					String roleQuery = "INSERT INTO USER_ROLE (USER_ID,ROLE_ID,CREATED_DATE,MODIFIED_DATE,ACTIVE,CREATED_BY,MODIFIED_BY)"
							+ "VALUES (?,?,?,?,?,?,?)";
					pst = conn.prepareStatement(roleQuery);
					pst.setInt(1, userId);
					pst.setString(2, roleId);
					pst.setString(3, date);
					pst.setString(4, date);
					pst.setInt(5, 1);
					pst.setInt(6, userIdLogged);
					pst.setInt(7, userIdLogged);
//					System.out.println("create User INSERT INTO USER_ROLE Qry "+pst);
					pst.executeUpdate();
					/**
					 * THIS QUERY WILL INSERT INTO THE USER AND HIS SUPERIOR
					 * TABLE
					 */
					String user_hierarchyQuery = "INSERT INTO user_hierarchy (PARENT_USER,CHILD_USER)"
							+ "VALUES (?,?)";
					pst = conn.prepareStatement(user_hierarchyQuery);
					pst.setInt(1, userBean.getSuperioruser());
					pst.setInt(2, userId);
					pst.executeUpdate();
					/**
					 * THE SUPERIOR FUNCTIONALITY ENDS HERE
					 */
					String operatorQuery = "INSERT INTO USER_OPERATOR (USER_ID,OPERATOR_ID,ACTIVE,CREATED_DATE,MODIFIED_DATE,CREATED_BY,MODIFIED_BY)"
							+ "VALUES (?,?,?,?,?,?,?)";
					pst = conn.prepareStatement(operatorQuery);
					pst.setInt(1, userId);
					pst.setString(2, operatorId);
					pst.setInt(3, 1);
					pst.setString(4, date);
					pst.setString(5, date);
					pst.setInt(6, userIdLogged);
					pst.setInt(7, userIdLogged);
					pst.executeUpdate();

					
					String deviceQuery = "INSERT INTO USER_DEVICE (USER_ID,IMEI,DEVICE_TYPE,MOBILE_NUMBER,ACTIVE,CREATED_DATE,MODIFIED_DATE,CREATED_BY,MODIFIED_BY)"
							+ "VALUES (?,?,?,?,?,?,?,?,?)";
					pst = conn.prepareStatement(deviceQuery);
					for (int i = 0; i < userBean.getUserDeviceInfo().size(); i++) {
						UserDeviceInfo userDeviceInfo = userBean
								.getUserDeviceInfo().get(i);
						pst.setInt(1, userId);
						pst.setString(2, userDeviceInfo.getImei());
						pst.setString(3, userDeviceInfo.getDeviceType());
						pst.setString(4, userDeviceInfo.getMobileNumber());
						pst.setInt(5, 1);
						pst.setString(6, date);
						pst.setString(7, date);
						pst.setInt(8, userIdLogged);
						pst.setInt(9, userIdLogged);
						pst.executeUpdate();
					}
					
					
					//INSERT USER_MARKET
					String marketQuery = "INSERT INTO USER_MARKET (USER_ID,MARKET_ID,CREATED_BY,CREATED_DATE,UPDATED_DATE,UPDATED_BY,ACTIVE)"
							+ "VALUES (?,?,?,?,?,?,?)";

					for (int i = 0; i < mid.length; i++) {
						pst = conn.prepareStatement(marketQuery);
						pst.setInt(1, userId);
						pst.setString(2, mid[i]);
						pst.setInt(3, userIdLogged);
						pst.setString(4, date);
						pst.setString(5, date);
						pst.setInt(6, userIdLogged);
						pst.setInt(7, 1);
						// //logger.error("marketQuery===="+pst);
						pst.executeUpdate();
					}
				}
			}
		

		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			}
		}

		if (status.equalsIgnoreCase("success")) {
			userBean.setUserMessage("New user created.");
		} else if (status.equalsIgnoreCase("IMEI_LIMITED")) {
			userBean
					.setUserMessage("IMEI limit is crossed, Please contact Airometric administrator");
		} else if(status.equalsIgnoreCase("Entered IMEI already exists")){
			userBean.setUserMessage("Entered IMEI already exists");
		}else if(status.equalsIgnoreCase("Duplicate IMEI"))
		{
			userBean.setUserMessage("Entered IMEI's are duplicate, re-enter and try again");
		}else{
			userBean
			.setUserMessage("User creation failed due to invalid data.");
		}
		status = status + "&&&" + userBean.getUserMessage();
		return status;

	}

	public String echeck(String emailAddress) {
		String validEmail = "false";
		String emailRegEx = "^[A-Za-z0-9,!#\\$%&'\\*\\+/=\\?\\^_`\\{\\|}~-]+(\\.[A-Za-z0-9,!#\\$%&'\\*\\+/=\\?\\^_`\\{\\|}~-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.([A-Za-z]{2,})$";
		if (!emailAddress.matches(emailRegEx)) {
			validEmail = "invalid";
		} else {
			validEmail = "valid";
		}
		return validEmail;
	}

	public String getOperator(String userId) {
		PreparedStatement pst = null;
		Connection conn = null;
		ResultSet rs = null;
		String operatorID = null;
		try {
			conn = DBUtil.getConnection();
			String query = "SELECT DISTINCT OPERATOR_ID FROM USER_OPERATOR WHERE USER_ID=?";
			pst = conn.prepareStatement(query);
			pst.setString(1, userId);
			rs = pst.executeQuery();
			if (rs.next()) {
				operatorID = rs.getString("OPERATOR_ID");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			}
		}
		return operatorID;
	}

	public List<UserBean> listUser(String loggedUserId, String rolename) {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		int i = 1;
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String usersQuery = " SELECT  U.USER_ID, U.USER_NAME, U.EMAIL, R.ROLE_NAME,O.OPERATOR_NAME,U.ACTIVE,U.IMEI_LIMIT,U.IMEI_COUNT"//,COUNT(UD.IMEI) AS IMEI_USER
					+ " FROM USERS U,USER_ROLE UR,ROLES R,USER_OPERATOR UT,OPERATOR O"//,USER_DEVICE UD
					+ " WHERE U.USER_ID=UR.USER_ID  AND  U.USER_ID=UT.USER_ID AND U.ACTIVE='1' AND UT.OPERATOR_ID=O.OPERATOR_ID "
					+ " AND UR.ROLE_ID=R.ROLE_ID AND U.USER_ID  IN("//AND UD.USER_ID = U.USER_ID 
					+ loggedUserId
					+ ") GROUP BY U.USER_NAME ORDER BY U.USER_NAME ASC,U.CREATED_DATE DESC ";
			if (rolename.equalsIgnoreCase(Roles.SUPERUSER)) {
				usersQuery = " SELECT  U.USER_ID, U.USER_NAME, U.EMAIL, R.ROLE_NAME,O.OPERATOR_NAME,U.ACTIVE,U.IMEI_LIMIT,U.IMEI_COUNT"
						+ " FROM USERS U,USER_ROLE UR,ROLES R,USER_OPERATOR UT,OPERATOR O"
						+ " WHERE U.USER_ID=UR.USER_ID  AND  U.USER_ID=UT.USER_ID AND U.ACTIVE='1' AND UT.OPERATOR_ID=O.OPERATOR_ID AND  U.USER_ID=UT.USER_ID"
						+ " AND UR.ROLE_ID=R.ROLE_ID  GROUP BY U.USER_NAME ORDER BY U.USER_NAME ASC,U.CREATED_DATE DESC ";
			}
			System.out.println("usersQuery--++-------" + usersQuery);
			rs = st.executeQuery(usersQuery);

			while (rs.next()) {
				UserBean userBean = new UserBean();
				userBean.setUserId(Integer.toString(rs.getInt(1)));
				userBean.setUserName(rs.getString(2));
				userBean.setEmail(rs.getString(3));
				userBean.setRole(rs.getString(4));
				userBean.setOperatorName(rs.getString(5));
				userBean.setImeiCount(rs.getInt("IMEI_COUNT"));
				//userBean.setIMEI_USER(rs.getString("IMEI_USER"));
				
				if (rs.getString(6).equals("1")) {
					userBean.setUserStatusStr("ACTIVE");
				} else {
					userBean.setUserStatusStr("INACTIVE");
				}
				userBean.setSlno("" + i);
				i++;
				listUserData.add(userBean);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			}
		}
		// logger.error("listUserData----------" + listUserData);
		return listUserData;
	}

	public Set<String> getUsersOfSuperior(String userId, String userRole) {//int flag
		Set userIdsList = new TreeSet<String>();
		String query = "";
		int flag = 0;
		System.out.println("userRole----------" + userId+".."+userRole);
		if (userRole.equalsIgnoreCase(Roles.ADMIN)) {
			query = "SELECT UH1.PARENT_USER AS ADMIN,UH1.CHILD_USER AS MANAGER, UH2.CHILD_USER AS ENGINNER,UH3.CHILD_USER AS TECHNCIAN "
					+ " FROM user_hierarchy UH1 "
					+ " LEFT JOIN user_hierarchy UH2 ON UH2.PARENT_USER = UH1.CHILD_USER "
					+ " LEFT JOIN user_hierarchy UH3 ON UH3.PARENT_USER = UH2.CHILD_USER "
					+ " WHERE UH1.PARENT_USER = '" + userId + "'";
		} else if (userRole.equalsIgnoreCase(Roles.MANAGER)) {
			query = "SELECT null AS ADMIN ,UH1.PARENT_USER AS MANAGER,UH1.CHILD_USER AS ENGINNER,UH2.CHILD_USER AS TECHNCIAN "
					+ " FROM user_hierarchy UH1 "
					+ " LEFT JOIN user_hierarchy UH2 ON UH2.PARENT_USER = UH1.CHILD_USER "
					+ " WHERE UH1.PARENT_USER = '" + userId + "' ";
		} else if (userRole.equalsIgnoreCase(Roles.ENGINEER)) {
			query = "SELECT null AS ADMIN, null AS MANAGER,UH1.PARENT_USER AS ENGINNER,UH1.CHILD_USER AS TECHNCIAN "
					+ " FROM user_hierarchy UH1 "
					+ " WHERE UH1.PARENT_USER = '" + userId + "'";
		} else {
			
			userIdsList.add(userId);
			return userIdsList;
		}
		Connection conn = DBUtil.openConn();
		// logger.error("query----------" + query);

		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			String adminId = null;
			String managerId = null;
			String engineerId = null;
			String techId = null;

			while (rs.next()) {
				adminId = rs.getString("ADMIN");
				managerId = rs.getString("MANAGER");
				engineerId = rs.getString("ENGINNER");

				if (flag == 1) {

					techId = rs.getString("TECHNCIAN");

				}

				if (null != adminId)
					userIdsList.add(adminId);
				if (null != managerId)
					userIdsList.add(managerId);
				if (null != engineerId)
					userIdsList.add(engineerId);
				if (null != techId && flag == 1)
					userIdsList.add(techId);
			}
			userIdsList.add(userId);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		} finally {
			DBUtil.closeConn();
		}
		 System.out.println("userIdsList-----------" + userIdsList);
		return userIdsList;
	}

	public Map<String, String> getUsersForId(String userIds, String role) {
		Map<String, String> userMap = new LinkedHashMap<String, String>();
		String query = "SELECT USER_ID,USER_NAME FROM USERS WHERE USER_ID IN("
				+ userIds + ")";
		if (role.equalsIgnoreCase("superadmin")) {
			query = "select u.user_id,u.user_name from users as u inner join user_role as ur on u.user_id=ur.user_id"
					+ " inner join roles as r on ur.role_id=r.role_id where r.role_name !='technician'; ";
		}
		Connection conn = DBUtil.openConn();
		// logger.error("query----------" + query);
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				String userId = rs.getString("USER_ID");
				String userName = rs.getString("USER_NAME");
				userMap.put(userName, userId);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}

		return userMap;
	}

	public List<UserBean> getOperatorList() {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		int i = 1;
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String usersQuery = "SELECT O.OPERATOR_NAME,O.ACTIVE,U.USER_NAME  FROM OPERATOR O,USERS U "
					+ "WHERE O.CREATED_BY=U.USER_ID";
			rs = st.executeQuery(usersQuery);
			while (rs.next()) {
				UserBean userBean = new UserBean();
				userBean.setOperatorName(rs.getString(1));
				if (rs.getString(2).equals("1")) {
					userBean.setOperatorStatusStr("ACTIVE");
				} else {
					userBean.setOperatorStatusStr("INACTIVE");
				}
				userBean.setSlno("" + i);
				userBean.setUserName(rs.getString(3));
				i++;
				operatorUserData.add(userBean);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			}
		}
		return operatorUserData;
	}

	public List<UserBean> listUserBasedOnOperator(String operatorId,
			String loggedUserId) {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		int i = 1;
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String usersQuery = "SELECT  U.USER_ID, U.USER_NAME,U.EMAIL,R.ROLE_NAME,O.OPERATOR_NAME,U.ACTIVE "
					+ " FROM USERS U,USER_OPERATOR UO,USER_ROLE UR,ROLES R,OPERATOR O WHERE U.USER_ID=UO.USER_ID  "
					+ " AND U.USER_ID=UR.USER_ID AND R.ROLE_ID=UR.ROLE_ID AND UO.OPERATOR_ID='"
					+ operatorId
					+ "' "
					+ " AND R.ROLE_ID !=1 AND U.ACTIVE='1' AND U.USER_ID=UO.USER_ID AND U.USER_ID NOT IN('"
					+ loggedUserId
					+ "') "
					+ " AND U.CREATED_BY='"
					+ loggedUserId
					+ "' AND UO.OPERATOR_ID=O.OPERATOR_ID ORDER BY U.USER_NAME ASC,U.CREATED_DATE DESC ";
			// logger.error("usersQuery----------" + usersQuery);
			rs = st.executeQuery(usersQuery);
			while (rs.next()) {
				UserBean userBean = new UserBean();
				userBean.setUserId(Integer.toString(rs.getInt(1)));
				userBean.setUserName(rs.getString(2));
				userBean.setEmail(rs.getString(3));
				userBean.setRole(rs.getString(4));
				userBean.setOperatorName(rs.getString(5));
				if (rs.getString(6).equals("1")) {
					userBean.setUserStatusStr("ACTIVE");
				} else {
					userBean.setUserStatusStr("INACTIVE");
				}
				userBean.setSlno("" + i);
				i++;
				listUserData.add(userBean);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			}
		}
		return listUserData;
	}

	public UserBean getUserDetails(String userId) {
		UserBean userBean = new UserBean();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String userQuery = "SELECT  U.USER_NAME,U.PASSWORD, U.EMAIL,U.ACTIVE,UR.ROLE_ID,UO.OPERATOR_ID,U.COUNTRY_ID,UH.PARENT_USER,U.IMEI_LIMIT "
					+ " FROM USERS U,USER_ROLE UR,USER_OPERATOR UO,USER_HIERARCHY UH "
					+ " WHERE U.USER_ID='"
					+ userId
					+ "'"
					+ " AND UR.USER_ID=U.USER_ID AND UO.USER_ID=U.USER_ID AND UH.CHILD_USER=U.USER_ID ";
			// logger.error("userQuery----------" + userQuery);
			rs = st.executeQuery(userQuery);
			while (rs.next()) {
				userBean.setUserId(userId);
				userBean.setUserName(rs.getString("USER_NAME"));
				userBean.setPassword(rs.getString("PASSWORD"));
				userBean.setEmail(rs.getString("EMAIL"));
				userBean.setRole(rs.getString("ROLE_ID"));
				userBean.setUserStatus(rs.getInt("ACTIVE"));
				userBean.setOperatorId(rs.getString("OPERATOR_ID"));
				userBean.setCountryId(rs.getString("COUNTRY_ID"));
				userBean.setSuperioruser(rs.getInt("PARENT_USER"));
				userBean.setImeiLimit(rs.getString("IMEI_LIMIT"));
				
			}
			String userDeviceQuery = "SELECT UD.DEVICE_ID,UD.IMEI,UD.DEVICE_TYPE,MOBILE_NUMBER FROM USER_DEVICE UD ,USERS U WHERE UD.USER_ID = U.USER_ID AND U.USER_ID='"
					+ userId + "'";
			rs1 = st.executeQuery(userDeviceQuery);
			List<UserDeviceInfo> userDeviceInfoList = new ArrayList<UserDeviceInfo>();
			while (rs1.next()) {
				UserDeviceInfo userDeviceInfo = new UserDeviceInfo();
				userDeviceInfo.setDeviceId(rs1.getString("DEVICE_ID"));
				userDeviceInfo.setImei(rs1.getString("IMEI"));
				userDeviceInfo.setDeviceType(rs1.getString("DEVICE_TYPE"));
				userDeviceInfo.setMobileNumber(rs1.getString("MOBILE_NUMBER"));
				userDeviceInfoList.add(userDeviceInfo);
			}
			userBean.setUserDeviceInfo(userDeviceInfoList);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
					rs1.close();
				}
				if (pst != null) {
					pst.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				logger.error(ee.getMessage());
			}
		}
		return userBean;
	}

	public Map<String, String> getActiveRoleInMap(String userRole) {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		Map<String, String> userRoleMap = new LinkedHashMap<String, String>();
		try {
			conn = DBUtil.getConnection();
			Set<String> roleList = new TreeSet<String>();
			String query = null;
			if (userRole.equals("superadmin")) {
				query = "SELECT ROLE_ID,ROLE_NAME FROM ROLES WHERE ACTIVE=1 AND ROLE_ID <>5 ORDER BY ROLE_NAME";
			} else if (userRole.equalsIgnoreCase(Roles.ADMIN)) {
				query = "SELECT ROLE_ID,ROLE_NAME FROM ROLES WHERE ACTIVE=1 AND ROLE_ID <>5 AND  ROLE_ID <>1 ORDER BY ROLE_NAME";
			}

			if (userRole.equalsIgnoreCase(Roles.MANAGER)) {
				query = "SELECT UH1.PARENT_ROLE AS ENGINNER,UH1.CHILD_ROLE AS TECHNCIAN "
						+ " FROM roles_hierarchy UH1 "
						+ " WHERE UH1.PARENT_ROLE = 3";
			}
			/*
			 * else{ query = "SELECT *   FROM ROLES"; }
			 */
			// logger.error("query-----------" + query);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			userRoleMap.put("--Select Role---", "0");
			while (rs.next()) {
				/*
				 * userRoleMap.put(rs.getString("ROLE_NAME"), rs
				 * .getString("ROLE_ID"));
				 */

				String roleId = rs.getString("ROLE_ID");
				String roleName = rs.getString("ROLE_NAME");
				userRoleMap.put(roleName, roleId);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			}
		}
		return userRoleMap;
	}

	public Map<String, String> getActiveEditRoleInMap(String roleName) {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		Map<String, String> userRoleMap = new LinkedHashMap<String, String>();
		try {
			conn = DBUtil.getConnection();
			String query = null;
			if (roleName.equals("superadmin")) {
				query = "SELECT ROLE_ID,ROLE_NAME FROM ROLES WHERE ACTIVE=1 ORDER BY ROLE_NAME";
			} else {
				// query =
				// "SELECT ROLE_ID,ROLE_NAME FROM ROLES WHERE ACTIVE=1 AND ROLE_ID IN(2,4) ORDER BY ROLE_NAME";
				query = "SELECT ROLE_ID,ROLE_NAME FROM ROLES WHERE ACTIVE=1 AND ROLE_ID <>1 AND ROLE_ID <>5 ORDER BY ROLE_NAME";
			}
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			userRoleMap.put("--Select Role", "0");
			while (rs.next()) {
				userRoleMap.put(rs.getString("ROLE_NAME"), rs
						.getString("ROLE_ID"));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			}
		}
		return userRoleMap;
	}

	public int chkUserMarket(String userId, String marketId) {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		int counts = 0;
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			Integer userIdLogged = Integer.parseInt(context
					.getExternalContext().getSessionMap().get("loggedInUserID")
					.toString());
			conn = DBUtil.getConnection();
			String query = "select count(*) FROM USER_MARKET WHERE USER_ID='"
					+ userId + "' AND MARKET_ID ='" + marketId + "' ";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				counts = rs.getInt(1);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());// TODO: handle exception
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
				logger.error(ee.getMessage());
			}
		}
		return counts;
	}

	/**
	 * 
	 * public int isAlreadyExistIMEI(String deviceId){ Statement stmt = null;
	 * Connection conn = null; ResultSet rs = null; Map<String,String>
	 * userRoleMap = new LinkedHashMap<String,String>(); int count=0; try { conn
	 * = DBUtil.getConnection(); String query =
	 * "SELECT COUNT(*) FROM USER_DEVICE WHERE DEVICE_ID='"+deviceId+"'"; stmt =
	 * conn.createStatement(); rs = stmt.executeQuery(query); while (rs.next())
	 * { count=rs.getInt(1); } } catch (Exception e) {
	 * logger.error(e.getMessage()); } finally { try { if (rs != null) {
	 * rs.close(); } if (stmt != null) { stmt.close(); } if (conn != null) {
	 * conn.close(); } } catch (Exception ee) { logger.error(ee.getMessage()); }
	 * } return count; }
	 * 
	 */

	public void editUser(String userId, String email, String role,
			int reportingTo, int userStatus, String operatorId,
			List<UserDeviceInfo> userDeviceInfo, String password,
			String[] marketId, String countryId,String imeiLimit,int imeiCount) {
		// //logger.error("marketId==============="+marketId.length);
		/*
		 * for (int i = 0; i < marketId.length; i++) {
		 * //logger.error("marketId==============="+marketId[i]); }
		 */
		//logger.error(reportingTo);
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pst = null;

		int count = 0;
		int counts = 0;
		String query;
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			Integer userIdLogged = Integer.parseInt(context
					.getExternalContext().getSessionMap().get("loggedInUserID")
					.toString());
			String date = DateUtil.getCurrentDateTime();
			conn = DBUtil.getConnection();
			if(role.equals("1"))
			{
			query = "UPDATE USERS SET EMAIL='" + email + "',ACTIVE="
					+ userStatus + ",MODIFIED_DATE='" + date
					+ "',MODIFIED_BY='" + userIdLogged + "',PASSWORD='"
					+ password + "' ,IMEI_LIMIT ='"+imeiLimit+"',IMEI_COUNT = '"+imeiCount+"' , COUNTRY_ID='" + countryId
					+ "' WHERE USER_ID = '" + userId + "'";
			}
			else{
			query = "UPDATE USERS SET EMAIL='" + email + "',ACTIVE="
					+ userStatus + ",MODIFIED_DATE='" + date
					+ "',MODIFIED_BY='" + userIdLogged + "',PASSWORD='"
					+ password + "' , COUNTRY_ID='" + countryId
					+ "' WHERE USER_ID = '" + userId + "'";
			}
			String user_hierarchyQuery = "UPDATE user_hierarchy SET PARENT_USER = '"
					+ reportingTo + "' WHERE CHILD_USER='" + userId + "'";
			String queryOp = "UPDATE USER_OPERATOR SET OPERATOR_ID='"
					+ operatorId + "' WHERE USER_ID = '" + userId + "'";
			String queryRole = "UPDATE USER_ROLE SET ROLE_ID='" + role
					+ "' WHERE USER_ID = '" + userId + "'";
			// String deleteMarket
			// ="DELETE FROM USER_MARKET WHERE USER_ID='"+userId+"'";
			String updateMarket = "UPDATE USER_MARKET SET ACTIVE=0 WHERE USER_ID='"
					+ userId + "'";
			// String exsistingMarket
			// ="SELECT * FROM USER_MARKET WHERE USER_ID='"+userId+"'";

			logger.error(user_hierarchyQuery);
			System.out.print(queryRole);
			stmt = conn.createStatement();
			stmt.executeUpdate(query);
			stmt.executeUpdate(user_hierarchyQuery);
			logger.error(stmt);
			stmt.executeUpdate(queryOp);
			stmt.executeUpdate(queryRole);
			stmt.executeUpdate(updateMarket);
			for (int i = 0; i < marketId.length; i++) {
				counts = chkUserMarket(userId, marketId[i]);
				if (counts == 0) {
					String marketQuery = "INSERT INTO USER_MARKET (USER_ID,MARKET_ID,CREATED_BY,CREATED_DATE,UPDATED_DATE,UPDATED_BY,ACTIVE)"
							+ "VALUES (?,?,?,?,?,?,?)";
					pst = conn.prepareStatement(marketQuery);
					pst.setInt(1, Integer.parseInt(userId));
					pst.setString(2, marketId[i]);
					pst.setInt(3, userIdLogged);
					pst.setString(4, date);
					pst.setString(5, date);
					pst.setInt(6, userIdLogged);
					pst.setInt(7, 1);
					pst.executeUpdate();
				} else if (counts >= 1) {
					String update = "UPDATE USER_MARKET SET ACTIVE=1,UPDATED_DATE='"
							+ date
							+ "',UPDATED_BY='"
							+ userIdLogged
							+ "' WHERE 	USER_ID='"
							+ userId
							+ "' AND MARKET_ID='" + marketId[i] + "'";
					stmt.executeUpdate(update);
				}
			}
			String deviceQuery = "INSERT INTO USER_DEVICE (USER_ID,IMEI,DEVICE_TYPE,MOBILE_NUMBER,ACTIVE,CREATED_DATE,MODIFIED_DATE,CREATED_BY,MODIFIED_BY)"
					+ "VALUES (?,?,?,?,?,?,?,?,?)";
			pst = conn.prepareStatement(deviceQuery);
			for (int i = 0; i < userDeviceInfo.size(); i++) {
				UserDeviceInfo userDeviceInfo2 = userDeviceInfo.get(i);
				String deviceID = userDeviceInfo2.getDeviceId();
				count = isAlreadyExistIMEI(deviceID);
				if (count == 0) {
					pst.setInt(1, Integer.parseInt(userId));
					pst.setString(2, userDeviceInfo2.getImei());
					pst.setString(3, userDeviceInfo2.getDeviceType());
					pst.setString(4, userDeviceInfo2.getMobileNumber());
					pst.setInt(5, 1);
					pst.setString(6, date);
					pst.setString(7, date);
					pst.setInt(8, userIdLogged);
					pst.setInt(9, userIdLogged);
					pst.executeUpdate();
				} else if (count >= 1) {
					String update = "UPDATE USER_DEVICE SET IMEI='"
							+ userDeviceInfo2.getImei() + "',DEVICE_TYPE='"
							+ userDeviceInfo2.getDeviceType()
							+ "',MOBILE_NUMBER='"
							+ userDeviceInfo2.getMobileNumber()
							+ "',MODIFIED_DATE='" + date + "',MODIFIED_BY='"
							+ userIdLogged + "' WHERE DEVICE_ID='" + deviceID
							+ "'";
					stmt.executeUpdate(update);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			}
		}
	}

	public int isAlreadyExistIMEI(String deviceId) {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		Map<String, String> userRoleMap = new LinkedHashMap<String, String>();
		int count = 0;
		try {
			conn = DBUtil.getConnection();
			String query = "SELECT COUNT(*) FROM USER_DEVICE WHERE DEVICE_ID='"
					+ deviceId + "'";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			}
		}
		return count;
	}

	public void profileEditUser(String userId, String email, String role,
			int userStatus, String operatorId,
			List<UserDeviceInfo> userDeviceInfo, String password,
			String userrole, String[] marketId, String countryId) {
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		int count = 0;
		int counts = 0;
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			Integer userIdLogged = Integer.parseInt(context
					.getExternalContext().getSessionMap().get("loggedInUserID")
					.toString());
			String date = DateUtil.getCurrentDateTime();
			conn = DBUtil.getConnection();
			if (userrole.equals("superadmin")) {
				String query = "UPDATE USERS SET EMAIL='" + email + "',ACTIVE="
						+ userStatus + ",MODIFIED_DATE='" + date
						+ "',MODIFIED_BY='" + userIdLogged + "',PASSWORD='"
						+ password + "' ,COUNTRY_ID='" + countryId
						+ "' WHERE USER_ID = '" + userId + "'";
				String queryRole = "UPDATE USER_ROLE SET ROLE_ID='" + role
						+ "' WHERE USER_ID = '" + userId + "'";
				String updateMarket = "UPDATE USER_MARKET SET ACTIVE=0 WHERE USER_ID='"
						+ userId + "'";
				stmt = conn.createStatement();
				stmt.executeUpdate(query);
				stmt.executeUpdate(queryRole);
				stmt.executeUpdate(updateMarket);
				for (int i = 0; i < marketId.length; i++) {
					counts = chkUserMarket(userId, marketId[i]);
					if (counts == 0) {
						String marketQuery = "INSERT INTO USER_MARKET (USER_ID,MARKET_ID,CREATED_BY,CREATED_DATE,UPDATED_DATE,UPDATED_BY,ACTIVE)"
								+ "VALUES (?,?,?,?,?,?,?)";
						pst = conn.prepareStatement(marketQuery);
						pst.setInt(1, Integer.parseInt(userId));
						pst.setString(2, marketId[i]);
						pst.setInt(3, userIdLogged);
						pst.setString(4, date);
						pst.setString(5, date);
						pst.setInt(6, userIdLogged);
						pst.setInt(7, 1);
						pst.executeUpdate();
					} else if (counts >= 1) {
						String update = "UPDATE USER_MARKET SET ACTIVE=1,UPDATED_DATE='"
								+ date
								+ "',UPDATED_BY='"
								+ userIdLogged
								+ "' WHERE 	USER_ID='"
								+ userId
								+ "' AND MARKET_ID='" + marketId[i] + "'";
						stmt.executeUpdate(update);
					}
				}
			} else {
				String query = "UPDATE USERS SET EMAIL='" + email + "',ACTIVE="
						+ userStatus + ",MODIFIED_DATE='" + date
						+ "',MODIFIED_BY='" + userIdLogged + "',PASSWORD='"
						+ password + "'  WHERE USER_ID = '" + userId + "'";
				String queryOp = "UPDATE USER_OPERATOR SET OPERATOR_ID='"
						+ operatorId + "' WHERE USER_ID = '" + userId + "'";
				String queryRole = "UPDATE USER_ROLE SET ROLE_ID='" + role
						+ "' WHERE USER_ID = '" + userId + "'";
				String updateMarket = "UPDATE USER_MARKET SET ACTIVE=0 WHERE USER_ID='"
						+ userId + "'";
				stmt = conn.createStatement();
				stmt.executeUpdate(query);
				stmt.executeUpdate(queryOp);
				stmt.executeUpdate(queryRole);
				stmt.executeUpdate(updateMarket);
				for (int i = 0; i < marketId.length; i++) {
					counts = chkUserMarket(userId, marketId[i]);
					if (counts == 0) {
						String marketQuery = "INSERT INTO USER_MARKET (USER_ID,MARKET_ID,CREATED_BY,CREATED_DATE,UPDATED_DATE,UPDATED_BY,ACTIVE)"
								+ "VALUES (?,?,?,?,?,?,?)";
						pst = conn.prepareStatement(marketQuery);
						pst.setInt(1, Integer.parseInt(userId));
						pst.setString(2, marketId[i]);
						pst.setInt(3, userIdLogged);
						pst.setString(4, date);
						pst.setString(5, date);
						pst.setInt(6, userIdLogged);
						pst.setInt(7, 1);
						pst.executeUpdate();
					} else if (counts >= 1) {
						String update = "UPDATE USER_MARKET SET ACTIVE=1,UPDATED_DATE='"
								+ date
								+ "',UPDATED_BY='"
								+ userIdLogged
								+ "' WHERE 	USER_ID='"
								+ userId
								+ "' AND MARKET_ID='" + marketId[i] + "'";
						stmt.executeUpdate(update);
					}
				}
			}

			String deviceQuery = "INSERT INTO USER_DEVICE (USER_ID,IMEI,DEVICE_TYPE,MOBILE_NUMBER,ACTIVE,CREATED_DATE,MODIFIED_DATE,CREATED_BY,MODIFIED_BY)"
					+ "VALUES (?,?,?,?,?,?,?,?,?)";
			pst = conn.prepareStatement(deviceQuery);
			for (int i = 0; i < userDeviceInfo.size(); i++) {
				UserDeviceInfo userDeviceInfo2 = userDeviceInfo.get(i);
				String deviceID = userDeviceInfo2.getDeviceId();
				if (deviceID == null) {
					pst.setInt(1, Integer.parseInt(userId));
					pst.setString(2, userDeviceInfo2.getImei());
					pst.setString(3, userDeviceInfo2.getDeviceType());
					pst.setString(4, userDeviceInfo2.getMobileNumber());
					pst.setInt(5, 1);
					pst.setString(6, date);
					pst.setString(7, date);
					pst.setInt(8, userIdLogged);
					pst.setInt(9, userIdLogged);
					pst.executeUpdate();
				} else {
					String update = "UPDATE USER_DEVICE SET IMEI='"
							+ userDeviceInfo2.getImei() + "',DEVICE_TYPE='"
							+ userDeviceInfo2.getDeviceType()
							+ "',MOBILE_NUMBER='"
							+ userDeviceInfo2.getMobileNumber()
							+ "',MODIFIED_DATE='" + date + "',MODIFIED_BY='"
							+ userIdLogged + "' WHERE DEVICE_ID='" + deviceID
							+ "'";
					stmt.executeUpdate(update);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			}
		}
	}

	public List<DeviceInfoTO> getAllDeviceInfo(String testCaseName,
			String marketId, String testtype, String freqPlan) {
		List<DeviceInfoTO> deviceInfosList = new ArrayList<DeviceInfoTO>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String deviceInfoQuery = "SELECT  * FROM STG_DEVICE_INFO WHERE (TEST_NAME like'"
					+ testCaseName
					+ "-%' "
					+ " OR TEST_NAME LIKE '"
					+ testCaseName
					+ "')"
					+ "AND MARKET_ID='"
					+ marketId
					+ "'  AND   TEST_TYPE='"
					+ testtype
					+ "'  GROUP BY TIME_STAMP_FOREACH_SAMPLE";
			// System.out
			// .println("deviceInfoQuery------++-----" + deviceInfoQuery);
			rs = st.executeQuery(deviceInfoQuery);
			while (rs.next()) {
				DeviceInfoTO deviceInfos = new DeviceInfoTO();
				String networkType = rs.getString("NETWORK_TYPE");
				int rsrp = 0;
				if (!rs.getString("SIGNALSTRENGTH_LTERSRP").equalsIgnoreCase(
						"Empty")) {
					rsrp = rs.getInt("SIGNALSTRENGTH_LTERSRP");
				}

				if (!(networkType.contains("LTE"))
						|| ((networkType.contains("LTE")) && rsrp < 1000)) {
					// logger.error("rsrp-------------"+rsrp);
					deviceInfos.setTestName(rs.getString("TEST_NAME"));
					deviceInfos.setSignalStrengthGSM(rs
							.getString("SIGNALSTRENGTH_GSM"));
					deviceInfos.setNetworkType(rs.getString("NETWORK_TYPE"));
					deviceInfos.setNetworkDataState(rs
							.getString("NETWORK_DATASTATE"));
					deviceInfos.setNetworkRoaming(rs
							.getString("NETWORK_ROAMING"));
					deviceInfos.setSignalStrength(rs
							.getString("SIGNALSTRENGTH_GSMSIGNALSTRENGTH"));
					deviceInfos.setSignalStrengthCDMA(rs
							.getString("SIGNALSTRENGTH_CDMADBM"));
					deviceInfos.setSignalStrengthEVDO(rs
							.getString("SIGNALSTRENGTH_EVDODBM"));
					deviceInfos.setLattitude(rs
							.getDouble("GEOLOCATION_LATITUDE"));
					deviceInfos.setLongitude(rs
							.getDouble("GEOLOCATION_LANGITUDE"));
					deviceInfos.setCellLocationCID(rs
							.getString("CELLLOCATION_CID"));
					deviceInfos.setCellLocationLAC(rs
							.getString("CELLLOCATION_LAC"));
					deviceInfos.setDevicePhoneType(rs
							.getString("DEVICE_PHONETYPE"));
					deviceInfos.setNetworkMCC(rs.getString("NETWORK_MCC"));
					deviceInfos.setNetworkMNC(rs.getString("NETWORK_MNC"));
					deviceInfos.setSignalStrengthSnr(rs
							.getString("SIGNALSTRENGTH_EVDOSNR"));
					deviceInfos.setTimeStampForEachSample(rs
							.getString("TIME_STAMP_FOREACH_SAMPLE"));
					deviceInfos
							.setNeighbourInfo(rs.getString("NEIGHBOUR_INFO"));
					deviceInfos.setSignalStrengthCDMACIO(rs
							.getString("SIGNALSTRENGTH_CDMACIO"));
					deviceInfos.setSignalStrengthEVDOECIO(rs
							.getString("SIGNALSTRENGTH_EVDOECIO"));
					deviceInfos.setSignalStrengthLTE(rs
							.getString("SIGNALSTRENGTH_LTESIGNALSTRENGTH"));
					deviceInfos.setSignalStrengthLTERSRP(rs
							.getString("SIGNALSTRENGTH_LTERSRP"));
					deviceInfos.setSignalStrengthLTERSRQ(rs
							.getString("SIGNALSTRENGTH_LTERSRQ"));
					deviceInfos.setSignalStrengthLTERSSNR(rs
							.getString("SIGNALSTRENGTH_LTERSSNR"));
					deviceInfos.setSignalStrengthLTECQI(rs
							.getString("SIGNALSTRENGTH_LTECQI"));
					deviceInfos.setSnapShotId(rs.getString("SNAPSHOT_ID"));
					deviceInfos
							.setWifiInfoBssid(rs.getString("WIFIINFO_BSSID"));
					deviceInfos.setWifiInfoLinkSpeed(rs
							.getString("WIFIINFO_LINKSPEED"));
					deviceInfos.setWifiRssi(rs.getString("WIFIINFO_RSSI"));
					deviceInfos.setImei(rs
							.getString("IMEI"));
					//System.out.println(deviceInfos.getCellLocationCID());

					deviceInfos.setFreqBand(getFreqBand(freqPlan, deviceInfos
							.getCellLocationCID(), conn));
					deviceInfosList.add(deviceInfos);
				}

			}
			// logger.error("deviceInfosList-------"+deviceInfosList.size());
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				rs.close();
				st.close();
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}
		}
		return deviceInfosList;
	}

	public String getFreqBand(String frequencyPlan, String cellId,
			Connection conn) {
		String freqBand = "";
		if(cellId.equalsIgnoreCase("10267142"))
		{
			System.out.println("Yes");
		}
		Statement st = null;
		String query = "SELECT DISTINCT BAND FROM FREQ_BAND WHERE LTE_CI='"
				+ cellId + "' AND PLAN_NAME = '" + frequencyPlan
				+ "' LIMIT 1";
		try {
			conn = DBUtil.openConn();
			st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				System.out.println(">>>>>>"+rs.getString("BAND"));
				if(rs.getString("BAND")!=null)
					freqBand = rs.getString("BAND").trim();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return freqBand;
	}

	public List<DeviceInfoTO> getAllDeviceInfoForVQT(String testCaseName,
			String marketId, String testtype, List<String> vqtlist) {
		List<DeviceInfoTO> deviceInfosList = new ArrayList<DeviceInfoTO>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String deviceInfoQuery = "SELECT  * FROM STG_DEVICE_INFO WHERE TEST_NAME ='"
					+ testCaseName
					+ "' "
					+ "AND MARKET_ID IN("
					+ marketId
					+ ")  AND   TEST_TYPE='"
					+ testtype
					+ "'  GROUP  BY TIME_STAMP_FOREACH_SAMPLE";
			// System.out
			// .println("deviceInfoQuery-------------" + deviceInfoQuery);
			rs = st.executeQuery(deviceInfoQuery);
			while (rs.next()) {
				DeviceInfoTO deviceInfos = new DeviceInfoTO();
				String networkType = rs.getString("NETWORK_TYPE");
				int rsrp = 0;
				if (!rs.getString("SIGNALSTRENGTH_LTERSRP").equalsIgnoreCase(
						"Empty")) {
					rsrp = rs.getInt("SIGNALSTRENGTH_LTERSRP");
				}
				if (!(networkType.contains("LTE"))
						|| ((networkType.contains("LTE")) && rsrp < 1000)) {

					deviceInfos.setTestName(rs.getString("TEST_NAME"));
					deviceInfos.setNetworkType(rs.getString("NETWORK_TYPE"));
					deviceInfos.setNetworkDataState(rs
							.getString("NETWORK_DATASTATE"));
					deviceInfos.setNetworkRoaming(rs
							.getString("NETWORK_ROAMING"));
					// if(networkType.contains("LTE")){
					// deviceInfos.setSignalStrength(rs.getString("SIGNALSTRENGTH_LTESIGNALSTRENGTH"));
					// }
					// else{
					// deviceInfos.setSignalStrength(rs
					// .getString("SIGNALSTRENGTH_GSMSIGNALSTRENGTH"));
					// }
					deviceInfos.setSignalStrength(rs
							.getString("SIGNALSTRENGTH_GSMSIGNALSTRENGTH"));

					/*
					 * deviceInfos.setSignalStrength(rs
					 * .getString("SIGNALSTRENGTH_LTERSRP"));
					 */
					deviceInfos.setImei(rs.getString("DEVICE_IMEI"));
					deviceInfos.setTestType(rs.getString("TEST_TYPE"));
					deviceInfos.setUserName(rs.getString("USER_NAME"));
					deviceInfos.setUserName(rs.getString("USER_NAME"));
					deviceInfos.setPhoneNumber(rs
							.getString("DEVICE_PHONENUMBER"));
					deviceInfos.setPhoneType(rs.getString("DEVICE_PHONETYPE"));
					deviceInfos.setDeviceName(rs.getString("DEVICE_MODEL"));
					deviceInfos
							.setDeviceVersion(rs.getString("DEVICE_VERSION"));
					deviceInfos.setTimeStampForEachSample(rs
							.getString("TIME_STAMP_FOREACH_SAMPLE"));
					deviceInfos.setNetworkOperator(rs
							.getString("NETWORK_NETWORKOPERATOR"));
					deviceInfos.setNetworkOperatorName(rs
							.getString("NETWORK_NETWORKOPERATORNAME"));
					deviceInfos.setDataState(rs.getString("NETWORK_DATASTATE"));
					deviceInfos.setDataActivity(rs
							.getString("NETWORK_DATAACTIVITY"));
					deviceInfos.setWifiState(rs.getString("NETWORK_WIFISTATE"));
					deviceInfos.setNetworkRoaming(rs
							.getString("NETWORK_ROAMING"));
					deviceInfos.setNetworkMCC(rs.getString("NETWORK_MCC"));
					deviceInfos.setNetworkMNC(rs.getString("NETWORK_MNC"));
					deviceInfos.setSignalStrengthGSM(rs
							.getString("SIGNALSTRENGTH_GSMSIGNALSTRENGTH"));
					deviceInfos.setSignalStrengthCDMA(rs
							.getString("SIGNALSTRENGTH_CDMADBM"));
					deviceInfos.setSignalStrengthCDMACIO(rs
							.getString("SIGNALSTRENGTH_CDMACIO"));

					deviceInfos.setSignalStrengthEVDO(rs
							.getString("SIGNALSTRENGTH_EVDODBM"));
					deviceInfos.setSignalStrengthEVDOECIO(rs
							.getString("SIGNALSTRENGTH_EVDOECIO"));
					deviceInfos.setSignalStrength_EVDOSNR(rs
							.getString("SIGNALSTRENGTH_EVDOSNR"));
					deviceInfos
							.setSignalStrengthGSMBITRATEERROR("SIGNALSTRENGTH_GSMBITRATEERROR");

					deviceInfos.setSignalStrengthLTE(rs
							.getString("SIGNALSTRENGTH_LTESIGNALSTRENGTH"));
					deviceInfos.setSignalStrengthLTERSRP(rs
							.getString("SIGNALSTRENGTH_LTERSRP"));
					deviceInfos.setSignalStrengthLTERSRQ(rs
							.getString("SIGNALSTRENGTH_LTERSRQ"));
					deviceInfos.setSignalStrengthLTERSSNR(rs
							.getString("SIGNALSTRENGTH_LTERSSNR"));
					deviceInfos.setSignalStrengthLTECQI(rs
							.getString("SIGNALSTRENGTH_LTECQI"));
					deviceInfos.setCellLocationCID(rs
							.getString("CELLLOCATION_CID"));
					deviceInfos.setCellLocationLAC(rs
							.getString("CELLLOCATION_LAC"));
					deviceInfos
							.setNeighbourInfo(rs.getString("NEIGHBOUR_INFO"));
					deviceInfos.setBatteryLevel(rs.getString("BATTERY_LEVEL"));
					deviceInfos.setNetworkManuallyDone(rs
							.getString("NETWORK_MANUALLY_DONE"));

					deviceInfos.setLattitude(rs
							.getDouble("GEOLOCATION_LATITUDE"));
					deviceInfos.setLongitude(rs
							.getDouble("GEOLOCATION_LANGITUDE"));
					deviceInfos.setSnapShotId(rs.getString("SNAPSHOT_ID"));
					deviceInfos.setCellLocationCID(rs
							.getString("CELLLOCATION_CID"));
					deviceInfos.setCellLocationLAC(rs
							.getString("CELLLOCATION_LAC"));
					deviceInfos.setDevicePhoneType(rs
							.getString("DEVICE_PHONETYPE"));

					deviceInfos.setSignalStrengthSnr(rs
							.getString("SIGNALSTRENGTH_EVDOSNR"));

					deviceInfos.setSignalStrengthCDMACIO(rs
							.getString("SIGNALSTRENGTH_CDMACIO"));
					deviceInfos.setSignalStrengthEVDOECIO(rs
							.getString("SIGNALSTRENGTH_EVDOECIO"));

					// deviceInfos.set
					deviceInfos.setSignalStrengthLTECQI(rs
							.getString("SIGNALSTRENGTH_LTECQI"));

					deviceInfos.setDeviceManufacturer(rs
							.getString("DEVICE_MANUFACTURER"));
					deviceInfos.setImei(rs.getString("IMEI"));
					deviceInfosList.add(deviceInfos);
				}

			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				rs.close();
				st.close();
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}
		}
		return deviceInfosList;
	}

	public List<DeviceInfoTO> getAllDeviceInfoForLTE(String testCaseName,
			String marketId, String testtype) {
		List<DeviceInfoTO> deviceInfosList = new ArrayList<DeviceInfoTO>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String deviceInfoQuery = "SELECT  * FROM STG_DEVICE_INFO WHERE TEST_NAME ='"
					+ testCaseName
					+ "' AND MARKET_ID='"
					+ marketId
					+ "' AND TEST_TYPE='"
					+ testtype
					+ "' ORDER BY TIME_STAMP_FOREACH_SAMPLE";
			rs = st.executeQuery(deviceInfoQuery);
			while (rs.next()) {
				if (rs.getString("NETWORK_TYPE").trim().equals("LTE (4G)")) {
					DeviceInfoTO deviceInfos = new DeviceInfoTO();
					deviceInfos.setTestName(rs.getString("TEST_NAME"));
					deviceInfos.setNetworkType(rs.getString("NETWORK_TYPE"));
					deviceInfos.setNetworkDataState(rs
							.getString("NETWORK_DATASTATE"));
					deviceInfos.setNetworkRoaming(rs
							.getString("NETWORK_ROAMING"));
					deviceInfos.setSignalStrength(rs
							.getString("SIGNALSTRENGTH_GSMSIGNALSTRENGTH"));
					deviceInfos.setSignalStrengthCDMA(rs
							.getString("SIGNALSTRENGTH_CDMADBM"));
					deviceInfos.setSignalStrengthEVDO(rs
							.getString("SIGNALSTRENGTH_EVDODBM"));
					deviceInfos.setLattitude(rs
							.getDouble("GEOLOCATION_LATITUDE"));
					deviceInfos.setLongitude(rs
							.getDouble("GEOLOCATION_LANGITUDE"));
					deviceInfos.setCellLocationCID(rs
							.getString("CELLLOCATION_CID"));
					deviceInfos.setCellLocationLAC(rs
							.getString("CELLLOCATION_LAC"));
					deviceInfos.setDevicePhoneType(rs
							.getString("DEVICE_PHONETYPE"));
					deviceInfos.setNetworkMCC(rs.getString("NETWORK_MCC"));
					deviceInfos.setNetworkMNC(rs.getString("NETWORK_MNC"));
					deviceInfos.setSignalStrengthSnr(rs
							.getString("SIGNALSTRENGTH_EVDOSNR"));
					deviceInfos.setTimeStampForEachSample(rs
							.getString("TIME_STAMP_FOREACH_SAMPLE"));
					deviceInfos
							.setNeighbourInfo(rs.getString("NEIGHBOUR_INFO"));
					deviceInfos.setSignalStrengthCDMACIO(rs
							.getString("SIGNALSTRENGTH_CDMACIO"));
					deviceInfos.setSignalStrengthEVDOECIO(rs
							.getString("SIGNALSTRENGTH_EVDOECIO"));
					deviceInfos.setSignalStrengthLTE(rs
							.getString("SIGNALSTRENGTH_LTESIGNALSTRENGTH"));
					deviceInfos.setSignalStrengthLTERSRP(rs
							.getString("SIGNALSTRENGTH_LTERSRP"));
					deviceInfos.setSignalStrengthLTERSRQ(rs
							.getString("SIGNALSTRENGTH_LTERSRQ"));
					deviceInfos.setSignalStrengthLTERSSNR(rs
							.getString("SIGNALSTRENGTH_LTERSSNR"));
					deviceInfos.setSignalStrengthLTECQI(rs
							.getString("SIGNALSTRENGTH_LTECQI"));
					deviceInfosList.add(deviceInfos);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				rs.close();
				st.close();
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}
		}
		return deviceInfosList;
	}

	public List<DeviceInfoTO> getAllMultiplDeviceInfo(String testCaseName,
			String marketId, String testtype) {
		List<DeviceInfoTO> deviceInfosList = new ArrayList<DeviceInfoTO>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		String test_name = testCaseName + "\\-%";
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			/**
			 * Author : Srikanth Kulkarni Modified Date : 15 - 05 -2014
			 * 
			 * changed String deviceInfoQuery ="SELECT  * FROM STG_DEVICE_INFO WHERE TEST_NAME  IN(SELECT DISTINCT TEST_NAME FROM STG_DEVICE_INFO "
			 * + " WHERE TEST_NAME LIKE  '"+test_name+"' ) AND MARKET_ID='"+
			 * marketId+"' AND TEST_TYPE='"+testtype+
			 * "' ORDER BY TIME_STAMP_FOREACH_SAMPLE ";
			 * 
			 * to
			 * 
			 * String deviceInfoQuery =
			 * "SELECT  * FROM STG_DEVICE_INFO WHERE  TEST_NAME LIKE  '"
			 * +test_name
			 * +"' AND MARKET_ID='"+marketId+"' AND TEST_TYPE='"+testtype
			 * +"' ORDER BY TIME_STAMP_FOREACH_SAMPLE ";
			 **/

			String deviceInfoQuery = "SELECT  * FROM STG_DEVICE_INFO WHERE  TEST_NAME LIKE  '"
					+ test_name
					+ "' AND MARKET_ID='"
					+ marketId
					+ "' AND TEST_TYPE='"
					+ testtype
					+ "' ORDER BY TIME_STAMP_FOREACH_SAMPLE ";
			// logger.error("deviceInfoQuery----------" +
			// deviceInfoQuery);
			rs = st.executeQuery(deviceInfoQuery);
			while (rs.next()) {
				String networkType = rs.getString("NETWORK_TYPE");
				int rsrp = 0;
				if (rs.getString("SIGNALSTRENGTH_LTERSRP").equalsIgnoreCase(
						"Empty")) {
					rsrp = rs.getInt("SIGNALSTRENGTH_LTERSRP");
				}
				if (!(networkType.contains("LTE"))
						|| ((networkType.contains("LTE")) && rsrp < 1000)) {

					DeviceInfoTO deviceInfos = new DeviceInfoTO();
					deviceInfos.setTestName(rs.getString("TEST_NAME"));
					deviceInfos.setNetworkType(rs.getString("NETWORK_TYPE"));
					deviceInfos.setNetworkDataState(rs
							.getString("NETWORK_DATASTATE"));
					deviceInfos.setNetworkRoaming(rs
							.getString("NETWORK_ROAMING"));
					deviceInfos.setSignalStrength(rs
							.getString("SIGNALSTRENGTH_GSMSIGNALSTRENGTH"));
					deviceInfos.setSignalStrengthCDMA(rs
							.getString("SIGNALSTRENGTH_CDMADBM"));
					deviceInfos.setSignalStrengthEVDO(rs
							.getString("SIGNALSTRENGTH_EVDODBM"));
					deviceInfos.setLattitude(rs
							.getDouble("GEOLOCATION_LATITUDE"));
					deviceInfos.setLongitude(rs
							.getDouble("GEOLOCATION_LANGITUDE"));
					deviceInfos.setCellLocationCID(rs
							.getString("CELLLOCATION_CID"));
					deviceInfos.setCellLocationLAC(rs
							.getString("CELLLOCATION_LAC"));
					deviceInfos.setDevicePhoneType(rs
							.getString("DEVICE_PHONETYPE"));
					deviceInfos.setNetworkMCC(rs.getString("NETWORK_MCC"));
					deviceInfos.setNetworkMNC(rs.getString("NETWORK_MNC"));
					deviceInfos.setSignalStrengthSnr(rs
							.getString("SIGNALSTRENGTH_EVDOSNR"));
					deviceInfos.setTimeStampForEachSample(rs
							.getString("TIME_STAMP_FOREACH_SAMPLE"));
					deviceInfos
							.setNeighbourInfo(rs.getString("NEIGHBOUR_INFO"));
					deviceInfos.setSignalStrengthCDMACIO(rs
							.getString("SIGNALSTRENGTH_CDMACIO"));
					deviceInfos.setSignalStrengthEVDOECIO(rs
							.getString("SIGNALSTRENGTH_EVDOECIO"));
					deviceInfos.setSignalStrengthLTE(rs
							.getString("SIGNALSTRENGTH_LTESIGNALSTRENGTH"));
					deviceInfos.setSignalStrengthLTERSRP(rs
							.getString("SIGNALSTRENGTH_LTERSRP"));
					deviceInfos.setSignalStrengthLTERSRQ(rs
							.getString("SIGNALSTRENGTH_LTERSRQ"));
					deviceInfos.setSignalStrengthLTERSSNR(rs
							.getString("SIGNALSTRENGTH_LTERSSNR"));
					deviceInfos.setSignalStrengthLTECQI(rs
							.getString("SIGNALSTRENGTH_LTECQI"));
					deviceInfos.setImei(rs
							.getString("IMEI"));
					deviceInfosList.add(deviceInfos);
				}

			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				rs.close();
				st.close();
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}
		}
		return deviceInfosList;
	}

	public List<DeviceInfoTO> getAllMultiplDeviceInfoForLTE(
			String testCaseName, String marketId, String testtype) {
		List<DeviceInfoTO> deviceInfosList = new ArrayList<DeviceInfoTO>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		String test_name = testCaseName + "\\-%";
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String deviceInfoQuery = "SELECT  * FROM STG_DEVICE_INFO WHERE TEST_NAME  IN(SELECT DISTINCT TEST_NAME FROM STG_DEVICE_INFO "
					+ " WHERE TEST_NAME LIKE  '"
					+ test_name
					+ "' )AND MARKET_ID ='"
					+ marketId
					+ "' AND TEST_TYPE='"
					+ testtype + "' ORDER BY TIME_STAMP_FOREACH_SAMPLE ";
			rs = st.executeQuery(deviceInfoQuery);
			while (rs.next()) {
				String networkType = rs.getString("NETWORK_TYPE");
				int rsrp = 0;
				if (rs.getString("SIGNALSTRENGTH_LTERSRP").equalsIgnoreCase(
						"Empty")) {
					rsrp = rs.getInt("SIGNALSTRENGTH_LTERSRP");
				}
				if (!(networkType.contains("LTE"))
						|| ((networkType.contains("LTE")) && rsrp < 1000)) {
					if (rs.getString("NETWORK_TYPE").trim().equals("LTE (4G)")) {
						DeviceInfoTO deviceInfos = new DeviceInfoTO();
						deviceInfos.setTestName(rs.getString("TEST_NAME"));
						deviceInfos
								.setNetworkType(rs.getString("NETWORK_TYPE"));
						deviceInfos.setNetworkDataState(rs
								.getString("NETWORK_DATASTATE"));
						deviceInfos.setNetworkRoaming(rs
								.getString("NETWORK_ROAMING"));
						deviceInfos.setSignalStrength(rs
								.getString("SIGNALSTRENGTH_GSMSIGNALSTRENGTH"));
						deviceInfos.setLattitude(rs
								.getDouble("GEOLOCATION_LATITUDE"));
						deviceInfos.setLongitude(rs
								.getDouble("GEOLOCATION_LANGITUDE"));
						deviceInfos.setCellLocationCID(rs
								.getString("CELLLOCATION_CID"));
						deviceInfos.setCellLocationLAC(rs
								.getString("CELLLOCATION_LAC"));
						deviceInfos.setDevicePhoneType(rs
								.getString("DEVICE_PHONETYPE"));
						deviceInfos.setNetworkMCC(rs.getString("NETWORK_MCC"));
						deviceInfos.setNetworkMNC(rs.getString("NETWORK_MNC"));
						deviceInfos.setTimeStampForEachSample(rs
								.getString("TIME_STAMP_FOREACH_SAMPLE"));
						deviceInfos.setNeighbourInfo(rs
								.getString("NEIGHBOUR_INFO"));
						deviceInfos.setSignalStrengthLTE(rs
								.getString("SIGNALSTRENGTH_LTESIGNALSTRENGTH"));
						deviceInfos.setSignalStrengthLTERSRP(rs
								.getString("SIGNALSTRENGTH_LTERSRP"));
						deviceInfos.setSignalStrengthLTERSRQ(rs
								.getString("SIGNALSTRENGTH_LTERSRQ"));
						deviceInfos.setSignalStrengthLTERSSNR(rs
								.getString("SIGNALSTRENGTH_LTERSSNR"));
						deviceInfos.setSignalStrengthLTECQI(rs
								.getString("SIGNALSTRENGTH_LTECQI"));
						deviceInfosList.add(deviceInfos);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				rs.close();
				st.close();
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}
		}
		return deviceInfosList;
	}

	public List<String> getAllVoiceQualitySignalStrengthList(
			String testCaseName, String marketId, String testtype) {
		List<String> deviceInfosList = new ArrayList<String>();

		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;

		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String sql = "SELECT SIGNALSTRENGTH_GSMSIGNALSTRENGTH "
					+ " FROM STG_DEVICE_INFO WHERE TEST_NAME='"
					+ testCaseName
					+ "' AND MARKET_ID='"
					+ marketId
					+ "' AND TEST_TYPE='"
					+ testtype
					+ "' GROUP BY TIME_STAMP_FOREACH_SAMPLE ORDER BY TIME_STAMP_FOREACH_SAMPLE";
			rs = st.executeQuery(sql);
			while (rs.next()) {
				deviceInfosList.add(rs.getString(1));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				rs.close();
				st.close();
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}
		}
		return deviceInfosList;
	}

	public List<String> getAllVoiceQualityNetworkTypeList(String testCaseName,
			String marketId, String testtype) {
		List<String> deviceNetworkTypeList = new ArrayList<String>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String sql = "SELECT NETWORK_TYPE "
					+ " FROM STG_DEVICE_INFO WHERE TEST_NAME='"
					+ testCaseName
					+ "'  AND MARKET_ID='"
					+ marketId
					+ "' AND TEST_TYPE='"
					+ testtype
					+ "' GROUP BY TIME_STAMP_FOREACH_SAMPLE ORDER BY TIME_STAMP_FOREACH_SAMPLE";
			rs = st.executeQuery(sql);
			while (rs.next()) {
				deviceNetworkTypeList.add(rs.getString(1).trim());
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				rs.close();
				st.close();
				conn.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return deviceNetworkTypeList;
	}

	public List<String> getAllVoiceQualityMarketNetworkTypeList(
			String testCaseName, String market) {
		List<String> deviceNetworkTypeList = new ArrayList<String>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String sql = "SELECT NETWORK_TYPE "
					+ " FROM STG_DEVICE_INFO WHERE TEST_NAME='"
					+ testCaseName
					+ "'  AND MARKET_ID IN("
					+ market
					+ ") AND TEST_TYPE='externaltest' GROUP BY TIME_STAMP_FOREACH_SAMPLE ORDER BY TIME_STAMP_FOREACH_SAMPLE";
			rs = st.executeQuery(sql);
			while (rs.next()) {
				deviceNetworkTypeList.add(rs.getString(1).trim());
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				rs.close();
				st.close();
				conn.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return deviceNetworkTypeList;
	}

	public List<String> getAllVoiceQualitySourceCellIdList(String testCaseName,
			String marketId, String testtype) {
		List<String> deviceSourceCellIdList = new ArrayList<String>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String sql = "SELECT CELLLOCATION_CID "
					+ " FROM STG_DEVICE_INFO WHERE TEST_NAME='"
					+ testCaseName
					+ "'  and MARKET_ID='"
					+ marketId
					+ "' AND TEST_TYPE='"
					+ testtype
					+ "' GROUP BY TIME_STAMP_FOREACH_SAMPLE ORDER BY TIME_STAMP_FOREACH_SAMPLE";
			rs = st.executeQuery(sql);
			while (rs.next()) {
				deviceSourceCellIdList.add(rs.getString(1));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				rs.close();
				st.close();
				conn.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return deviceSourceCellIdList;
	}

	public List<DeviceInfoTO> getAllDeviceInfoVQTList(String testCaseName,
			String marketId, String testtype) {
		List<DeviceInfoTO> deviceInfoVQTList = new ArrayList<DeviceInfoTO>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String sql = "SELECT * "
					+ " FROM STG_DEVICE_INFO WHERE TEST_NAME='"
					+ testCaseName
					+ "'  and MARKET_ID='"
					+ marketId
					+ "' "
					+ " AND TEST_TYPE='"
					+ testtype
					+ "' GROUP BY TIME_STAMP_FOREACH_SAMPLE ORDER BY TIME_STAMP_FOREACH_SAMPLE";
			rs = st.executeQuery(sql);
			while (rs.next()) {
				// deviceInfoVQTList.add(rs.getString(1));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				rs.close();
				st.close();
				conn.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return deviceInfoVQTList;
	}

	public List<String> getAllVoiceQualityMarketSourceCellIdList(
			String testCaseName, String market) {
		List<String> deviceSourceCellIdList = new ArrayList<String>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String sql = "SELECT CELLLOCATION_CID "
					+ " FROM STG_DEVICE_INFO WHERE TEST_NAME='"
					+ testCaseName
					+ "' AND MARKET_ID IN("
					+ market
					+ ") AND TEST_TYPE='externaltest'  GROUP BY TIME_STAMP_FOREACH_SAMPLE "
					+ "ORDER BY TIME_STAMP_FOREACH_SAMPLE";
			rs = st.executeQuery(sql);
			while (rs.next()) {
				deviceSourceCellIdList.add(rs.getString(1));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				rs.close();
				st.close();
				conn.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return deviceSourceCellIdList;
	}

	public List<String> getAllVoiceQualityTimeStampList(String testCaseName) {
		// TODO Auto-generated method stub
		List<String> deviceInfosList = new ArrayList<String>();

		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;

		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String sql = "SELECT DATE_FORMAT(TIME_STAMP_FOREACH_SAMPLE, '%c/%e/%Y  %l:%i:%s') as TIME_STAMP "
					+ " FROM STG_DEVICE_INFO WHERE TEST_NAME='"
					+ testCaseName
					+ "' GROUP BY TIME_STAMP ORDER BY TIME_STAMP";
			rs = st.executeQuery(sql);
			while (rs.next()) {
				deviceInfosList.add(rs.getString(1));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				rs.close();
				st.close();
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}
		}
		return deviceInfosList;
	}

	public List<DeviceInfoTO> getAllDeviceInfoReportList(String testCaseName) {
		// TODO Auto-generated method stub
		List<DeviceInfoTO> deviceInfosList = new ArrayList<DeviceInfoTO>();

		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String sql = "SELECT DATE_FORMAT(TIME_STAMP_FOREACH_SAMPLE, '%Y-%m-%d %H:%i:%S') as TIME_STAMP ,"
					+ " SIGNALSTRENGTH_GSMSIGNALSTRENGTH,NETWORK_TYPE ,SIGNALSTRENGTH_EVDOECIO ,"
					+ " SIGNALSTRENGTH_CDMACIO,SIGNALSTRENGTH_LTECQI "
					+ " FROM STG_DEVICE_INFO WHERE TEST_NAME='"
					+ testCaseName
					+ "' GROUP BY TIME_STAMP ORDER BY TIME_STAMP";
			rs = st.executeQuery(sql);
			while (rs.next()) {
				DeviceInfoTO deviceInfo = new DeviceInfoTO();
				if (rs.getString(1).contains("EVDO")) {
					deviceInfo.setSignalStrength(rs.getString(4));
				} else if (rs.getString(1).contains("CDMA")) {
					deviceInfo.setSignalStrength(rs.getString(5));
				} else if (rs.getString(1).contains("LTE")) {
					deviceInfo.setSignalStrength(rs.getString(6));
				}
				deviceInfo.setTimeStampForEachSample(rs.getString(2));
				deviceInfosList.add(deviceInfo);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				rs.close();
				st.close();
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}
		}
		return deviceInfosList;
	}

	public List<DeviceInfoTO> getAllLogcatTimeStampList(String testCaseName) {
		// TODO Auto-generated method stub
		List<DeviceInfoTO> deviceInfosList = new ArrayList<DeviceInfoTO>();

		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;

		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String sql = "SELECT DATE_FORMAT(TIME_STAMP, '%Y-%m-%d %H:%i:%S') as TIME_STAMP ,EVENT_NAME,EVENT_VALUE"
					+ " FROM STG_LOG_CAT_INFO WHERE TEST_NAME='"
					+ testCaseName
					+ "' GROUP BY TIME_STAMP ORDER BY TIME_STAMP";
			rs = st.executeQuery(sql);
			while (rs.next()) {
				DeviceInfoTO deviceInfo = new DeviceInfoTO();
				deviceInfo.setTime_stamp(rs.getString(1));
				deviceInfo.setEventName(rs.getString(2));
				deviceInfo.setEventValue(rs.getString(3));
				deviceInfosList.add(deviceInfo);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				rs.close();
				st.close();
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}
		}
		return deviceInfosList;
	}

	public List<String> getAllVoiceQualityLatitudeList(String testCaseName,
			String marketId, String testype) {
		// TODO Auto-generated method stub
		List<String> deviceInfosList = new ArrayList<String>();

		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;

		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String sql = "SELECT  GEOLOCATION_LATITUDE "
					+ " FROM STG_DEVICE_INFO WHERE TEST_NAME='"
					+ testCaseName
					+ "' AND MARKET_ID='"
					+ marketId
					+ "' AND TEST_TYPE='"
					+ testype
					+ "' GROUP BY TIME_STAMP_FOREACH_SAMPLE ORDER BY TIME_STAMP_FOREACH_SAMPLE";
			rs = st.executeQuery(sql);
			while (rs.next()) {
				deviceInfosList.add(rs.getString(1));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				rs.close();
				st.close();
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}
		}
		return deviceInfosList;
	}

	public List<String> getAllDeviceInfoCallDropList(String testCaseName,
			String marketId, String testtype, int flag) {
		List<String> deviceInfosList = new ArrayList<String>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		String deviceInfoQuery = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			if (flag == 1)
				deviceInfoQuery = "select time_stamp_foreach_sample from pre_cal_callretention_1 where parameter = 'Call Dropped' and test_name = '"
						+ testCaseName + "'";
			else
				deviceInfoQuery = "select time_stamp from stg_log_cat_info where test_name like '%"
						+ testCaseName + "%' and event_name = 'CALL_DROP'";
			// logger.error("deviceInfoQuery------------"+deviceInfoQuery);
			rs = st.executeQuery(deviceInfoQuery);
			while (rs.next()) {
				String timeStamp = rs.getString(1);
				if (flag == 1) {
					deviceInfosList.add(timeStamp);
				} else {
					if (null != timeStamp) {
						VoiceConnectivityProccesorHelper vcHelper = new VoiceConnectivityProccesorHelper();
						DeviceInfoTO dto = vcHelper.matchinDeviceInfo(
								timeStamp, "'%Y-%m-%d %H:%i:%s'", testCaseName,
								marketId, "mo");
						deviceInfosList.add(dto.getTimeStampForEachSample());
					}

				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				rs.close();
				st.close();
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}
		}
		return deviceInfosList;
	}

	public List<String> getAllMultipleDeviceInfoCallDropList(
			String testCaseName, String marketId, String testtype) {
		List<String> deviceInfosList = new ArrayList<String>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		String test_name = testCaseName + "\\-%";
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String deviceInfoQuery = "SELECT  DISTINCT ST.TIME_STAMP FROM STG_LOG_CAT_INFO ST,STG_DEVICE_INFO SD WHERE "
					+ " ST.TEST_NAME LIKE  '"
					+ test_name
					+ "'AND ST.TEST_TYPE= '"
					+ testtype
					+ "' AND ST.TEST_TYPE=SD.TEST_TYPE"
					+ " AND ST.TEST_NAME=SD.TEST_NAME AND ST.TEST_IDENTIFIER_TIMESTAMP=SD.TEST_IDENTIFIER_TIMESTAMP AND ST.EVENT_NAME='CALL_DROP' ORDER BY ST.TIME_STAMP ";
			// logger.error("deviceInfoQuery-------------------"
			// + deviceInfoQuery);
			rs = st.executeQuery(deviceInfoQuery);
			while (rs.next()) {
				deviceInfosList.add(rs.getString("TIME_STAMP"));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				rs.close();
				st.close();
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}
		}
		return deviceInfosList;
	}

	public List<DeviceInfoTO> getAllDeviceInfoTimeStampList(String testCaseName) {
		// TODO Auto-generated method stub
		List<DeviceInfoTO> deviceInfosList = new ArrayList<DeviceInfoTO>();

		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;

		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String deviceInfoQuery = "SELECT TIME_STAMP_FOREACH_SAMPLE ,max(TIME_STAMP_FOREACH_SAMPLE)  "
					+ "FROM STG_DEVICE_INFO WHERE TEST_NAME ='"
					+ testCaseName
					+ "' GROUP BY TIME_STAMP_FOREACH_SAMPLE";
			rs = st.executeQuery(deviceInfoQuery);
			while (rs.next()) {
				DeviceInfoTO deviceInfos = new DeviceInfoTO();
				deviceInfos.setOld_timestamp(rs.getString(1));
				deviceInfos.setCurrent_timestamp(rs.getString(1));
				deviceInfosList.add(deviceInfos);

			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				rs.close();
				st.close();
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}
		}
		return deviceInfosList;
	}

	public String removeDeviceFromDb(String deviceId) {
		String status = "false";
		Statement stmt = null;
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			stmt = conn.createStatement();
			String removeQuery = "DELETE FROM USER_DEVICE WHERE DEVICE_ID ='"
					+ deviceId + "'";
			stmt.executeUpdate(removeQuery);
			status = "true";
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				stmt.close();
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}
		}
		return status;
	}

	public String removeDeviceFromDbStatus(String deviceId) {
		String status = "false";
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		int count = 0;
		try {
			conn = DBUtil.getConnection();
			stmt = conn.createStatement();
			String removeQuery = "SELECT COUNT(*)FROM USER_DEVICE WHERE DEVICE_ID ='"
					+ deviceId + "'";
			rs = stmt.executeQuery(removeQuery);
			while (rs.next()) {
				count = rs.getInt(1);
			}
			if (count >= 1) {
				status = "true";
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				stmt.close();
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}
		}
		return status;
	}

	/*public boolean deleteUser(int userId) {
		boolean status = false;
		PreparedStatement pst = null;
		Connection conn = null;
		ResultSet rs = null;

		try {

			conn = DBUtil.getConnection();
			String query = "delete user_operator WHERE USER_ID='" + userId
					+ "'";
			pst = conn.prepareStatement(query);
			int rowCount = pst.executeUpdate();
			
			String query1 = "delete user_role WHERE USER_ID='" + userId
					+ "'";
			pst = conn.prepareStatement(query1);
			int rowCount1 = pst.executeUpdate();
			
			String query2 = "delete user_market  WHERE USER_ID='" + userId
					+ "'";
			pst = conn.prepareStatement(query2);
			int rowCount2 = pst.executeUpdate();
			
			String query3 = "delete user_device WHERE USER_ID='" + userId
					+ "'";
			pst = conn.prepareStatement(query3);
			int rowCount3 = pst.executeUpdate();
			
			String query4 = "delete user_hierarchy WHERE USER_ID='" + userId
					+ "'";
			pst = conn.prepareStatement(query4);
			int rowCount4 = pst.executeUpdate();
			
			String query5 = "delete user_test WHERE USER_ID='" + userId
					+ "'";
			pst = conn.prepareStatement(query5);
			int rowCount5 = pst.executeUpdate();
			
			String query6 = "delete users WHERE USER_ID='" + userId
					+ "'";
			pst = conn.prepareStatement(query6);
			int rowCount6 = pst.executeUpdate();
			if (rowCount6 > 0) {
				status = true;
			}

		} catch (Exception e) {

			logger.error(e.getMessage());
			return false;
		} finally {
			try {
				pst.close();
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}
		}

		return status;
	}
*/
	public String getUserName(String userId) {
		boolean status = false;
		Statement stmt = null;
		Connection conn = null;
		int count = 0;
		ResultSet rs = null;
		PreparedStatement pst = null;
		String username = null;
		try {
			conn = DBUtil.getConnection();
			stmt = conn.createStatement();
			String removeQuery = "SELECT USER_NAME FROM USERS WHERE USER_ID='"
					+ userId + "'";
			rs = stmt.executeQuery(removeQuery);
			while (rs.next()) {
				username = rs.getString(1);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				stmt.close();
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}
		}
		return username;
	}

	public UserBean getLoginUserDetails(String userId, String userrole) {
		UserBean userBean = new UserBean();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String userQuery = null;
			if (userrole.equals("superadmin")) {
				userQuery = "SELECT  U.USER_NAME,U.PASSWORD, U.EMAIL,U.ACTIVE,UR.ROLE_ID,U.COUNTRY_ID   FROM USERS U,USER_ROLE UR WHERE U.USER_ID='"
						+ userId + "' " + "AND UR.USER_ID=U.USER_ID ";
			} else {
				userQuery = "SELECT  U.USER_NAME,U.PASSWORD, U.EMAIL,U.ACTIVE,UR.ROLE_ID,UO.OPERATOR_ID,U.COUNTRY_ID  "
						+ " FROM USERS U,USER_ROLE UR,USER_OPERATOR UO  "
						+ " WHERE U.USER_ID='"
						+ userId
						+ "'"
						+ " AND UR.USER_ID=U.USER_ID AND UO.USER_ID=U.USER_ID";
			}

			rs = st.executeQuery(userQuery);
			if (rs.next()) {
				userBean.setUserId(userId);
				userBean.setUserName(rs.getString("USER_NAME"));
				userBean.setPassword(rs.getString("PASSWORD"));
				userBean.setEmail(rs.getString("EMAIL"));
				userBean.setRole(rs.getString("ROLE_ID"));
				userBean.setUserStatus(rs.getInt("ACTIVE"));
				if (!userrole.equals("superadmin")) {
					userBean.setOperatorId(rs.getString("OPERATOR_ID"));
				}
				userBean.setCountryId(rs.getString("COUNTRY_ID"));
			}
			String userDeviceQuery = "SELECT UD.DEVICE_ID,UD.IMEI,UD.DEVICE_TYPE,MOBILE_NUMBER FROM USER_DEVICE UD ,USERS U WHERE UD.USER_ID = U.USER_ID AND U.USER_ID='"
					+ userId + "'";

			rs1 = st.executeQuery(userDeviceQuery);
			List<UserDeviceInfo> userDeviceInfoList = new ArrayList<UserDeviceInfo>();
			while (rs1.next()) {
				UserDeviceInfo userDeviceInfo = new UserDeviceInfo();
				userDeviceInfo.setDeviceId(rs1.getString("DEVICE_ID"));
				userDeviceInfo.setImei(rs1.getString("IMEI"));
				userDeviceInfo.setDeviceType(rs1.getString("DEVICE_TYPE"));
				userDeviceInfo.setMobileNumber(rs1.getString("MOBILE_NUMBER"));
				userDeviceInfoList.add(userDeviceInfo);
			}
			userBean.setUserDeviceInfo(userDeviceInfoList);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
					rs1.close();
				}
				if (pst != null) {
					pst.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ee) {
				logger.error(ee.getMessage());
			}
		}
		return userBean;
	}

	public String updateTermsAndConditions(String userName) {
		PreparedStatement pst = null;
		Connection conn = null;
		ResultSet rs = null;
		String updateStatus = ResponseStatusENUM.TERMS_UPDATE_FAILURE
				.getStatus();
		try {
			conn = DBUtil.getConnection();
			String query = "UPDATE USERS SET TERMS_CONDITIONS_ACCEPTED = ? WHERE USER_NAME = ?";
			pst = conn.prepareStatement(query);
			pst.setInt(1, 1);
			pst.setString(2, userName);
			int rowCount = pst.executeUpdate();
			if (rowCount > 0) {
				updateStatus = ResponseStatusENUM.TERMS_UPDATE_SUCCESS
						.getStatus();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			}
		}
		return updateStatus;
	}

	public List<String> getRatingList(String testCaseName, String marketId) {
		boolean status = false;
		Statement stmt = null;
		Connection conn = null;
		int count = 0;
		ResultSet rs = null;
		PreparedStatement pst = null;
		List<String> rating = new ArrayList<String>();
		try {
			conn = DBUtil.getConnection();
			stmt = conn.createStatement();
			String removeQuery = "SELECT DISTINCT RATING FROM STG_VQT_RESULTS WHERE TEST_NAME='"
					+ testCaseName + "'  and MARKET_ID='" + marketId + "' ";
			rs = stmt.executeQuery(removeQuery);
			while (rs.next()) {
				rating.add(rs.getString(1));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				stmt.close();
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}
		}
		return rating;
	}

	public List<String> getMultipleRatingList(String testCaseName,
			String marketId) {
		boolean status = false;
		Statement stmt = null;
		Connection conn = null;
		int count = 0;
		ResultSet rs = null;
		PreparedStatement pst = null;
		List<String> rating = new ArrayList<String>();
		try {
			conn = DBUtil.getConnection();
			stmt = conn.createStatement();
			String removeQuery = "SELECT DISTINCT RATING FROM STG_VQT_RESULTS WHERE TEST_NAME='"
					+ testCaseName + "' AND MARKET_ID IN('" + marketId + "') ";
			rs = stmt.executeQuery(removeQuery);
			while (rs.next()) {
				rating.add(rs.getString(1));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				stmt.close();
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}
		}
		return rating;
	}

	public boolean checkOperatorExists(String operatorName) {
		boolean status = false;
		Statement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			stmt = conn.createStatement();
			String removeQuery = "SELECT COUNT(*) FROM OPERATOR WHERE OPERATOR_NAME='"
					+ operatorName + "'";
			rs = stmt.executeQuery(removeQuery);
			while (rs.next()) {
				int count = rs.getInt(1);
				if (count >= 1) {
					status = true;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				stmt.close();
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}
		}
		return status;
	}

	public List<String> getAllVoiceQualityMultipleLatitudeList(
			String testCaseName, String market) {
		// TODO Auto-generated method stub
		List<String> deviceInfosList = new ArrayList<String>();
		String test_name = testCaseName + "\\-%";
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;

		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String sql = "SELECT  GEOLOCATION_LATITUDE "
					+ " FROM STG_DEVICE_INFO WHERE TEST_NAME LIKE '"
					+ test_name
					+ "' AND MARKET_ID= '"
					+ market
					+ "' GROUP BY TIME_STAMP_FOREACH_SAMPLE ORDER BY TIME_STAMP_FOREACH_SAMPLE";
			rs = st.executeQuery(sql);
			while (rs.next()) {
				deviceInfosList.add(rs.getString(1));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				rs.close();
				st.close();
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}
		}
		return deviceInfosList;
	}

	public List<String> getAllVoiceQualityMarketMultipleLatitudeList(
			String testCaseName, String market) {
		List<String> deviceInfosList = new ArrayList<String>();
		String test_name = testCaseName + "\\-%";
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String sql = "SELECT  GEOLOCATION_LATITUDE "
					+ " FROM STG_DEVICE_INFO WHERE TEST_NAME LIKE '"
					+ test_name
					+ "'  AND MARKET_ID IN('"
					+ market
					+ "') GROUP BY TIME_STAMP_FOREACH_SAMPLE ORDER BY TIME_STAMP_FOREACH_SAMPLE";
			rs = st.executeQuery(sql);
			while (rs.next()) {
				deviceInfosList.add(rs.getString(1));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				rs.close();
				st.close();
				conn.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return deviceInfosList;
	}

	public List<String> getAllVoiceQualityMultipleSignalStrengthList(
			String testCaseName, String marketId) {
		// TODO Auto-generated method stub
		List<String> deviceInfosList = new ArrayList<String>();

		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		String test_name = testCaseName + "\\-%";
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String sql = "SELECT SIGNALSTRENGTH_GSMSIGNALSTRENGTH "
					+ " FROM STG_DEVICE_INFO WHERE TEST_NAME LIKE '"
					+ test_name
					+ "' AND MARKET_ID='"
					+ marketId
					+ "'  GROUP BY TIME_STAMP_FOREACH_SAMPLE ORDER BY TIME_STAMP_FOREACH_SAMPLE";
			rs = st.executeQuery(sql);
			while (rs.next()) {
				deviceInfosList.add(rs.getString(1));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				rs.close();
				st.close();
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}
		}
		return deviceInfosList;
	}

	public List<String> getAllVoiceQualityMarketMultipleSignalStrengthList(
			String testCaseName, String market) {
		List<String> deviceInfosList = new ArrayList<String>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		String test_name = testCaseName + "\\-%";
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String sql = "SELECT SIGNALSTRENGTH_GSMSIGNALSTRENGTH "
					+ " FROM STG_DEVICE_INFO WHERE TEST_NAME LIKE '"
					+ test_name
					+ "'  AND MARKET_ID IN('"
					+ market
					+ "') "
					+ " GROUP BY TIME_STAMP_FOREACH_SAMPLE ORDER BY TIME_STAMP_FOREACH_SAMPLE";
			rs = st.executeQuery(sql);
			while (rs.next()) {
				deviceInfosList.add(rs.getString(1));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				rs.close();
				st.close();
				conn.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return deviceInfosList;
	}

	public List<String> getAllVoiceMarketQualityMultipleSignalStrengthList(
			String testCaseName, String market) {
		List<String> deviceInfosList = new ArrayList<String>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		String test_name = testCaseName + "\\-%";
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String sql = "SELECT SIGNALSTRENGTH_GSMSIGNALSTRENGTH "
					+ " FROM STG_DEVICE_INFO WHERE TEST_NAME LIKE '"
					+ test_name
					+ "'  AND MARKET_ID IN('"
					+ market
					+ "')  "
					+ "GROUP BY TIME_STAMP_FOREACH_SAMPLE ORDER BY TIME_STAMP_FOREACH_SAMPLE";
			rs = st.executeQuery(sql);
			while (rs.next()) {
				deviceInfosList.add(rs.getString(1));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				rs.close();
				st.close();
				conn.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return deviceInfosList;
	}

	public List<String> getAllVoiceQualityMultipleNetworkTypeList(
			String testCaseName, String market) {
		// TODO Auto-generated method stub
		List<String> deviceInfosList = new ArrayList<String>();

		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		String test_name = testCaseName + "\\-%";
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String sql = "SELECT NETWORK_TYPE "
					+ " FROM STG_DEVICE_INFO WHERE TEST_NAME LIKE '"
					+ test_name
					+ "' AND MARKET_ID='"
					+ market
					+ "' GROUP BY TIME_STAMP_FOREACH_SAMPLE ORDER BY TIME_STAMP_FOREACH_SAMPLE";
			rs = st.executeQuery(sql);
			while (rs.next()) {
				deviceInfosList.add(rs.getString(1));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				rs.close();
				st.close();
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}
		}
		return deviceInfosList;
	}

	public List<String> getAllVoiceQualityMarketMultipleNetworkTypeList(
			String testCaseName, String market) {
		List<String> deviceInfosList = new ArrayList<String>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		String test_name = testCaseName + "\\-%";
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String sql = "SELECT NETWORK_TYPE "
					+ " FROM STG_DEVICE_INFO WHERE TEST_NAME LIKE '"
					+ test_name
					+ "'  AND MARKET_ID IN('"
					+ market
					+ "') "
					+ "GROUP BY TIME_STAMP_FOREACH_SAMPLE ORDER BY TIME_STAMP_FOREACH_SAMPLE";
			rs = st.executeQuery(sql);
			while (rs.next()) {
				deviceInfosList.add(rs.getString(1));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				rs.close();
				st.close();
				conn.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return deviceInfosList;
	}

	public List<String> getAllVoiceQualityMultipleSourceCellIdList(
			String testCaseName, String market) {
		// TODO Auto-generated method stub
		List<String> deviceInfosList = new ArrayList<String>();

		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		String test_name = testCaseName + "\\-%";
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String sql = "SELECT CELLLOCATION_CID "
					+ " FROM STG_DEVICE_INFO WHERE TEST_NAME LIKE '"
					+ test_name
					+ "'  AND MARKET_ID= '"
					+ market
					+ "'  GROUP BY TIME_STAMP_FOREACH_SAMPLE ORDER BY TIME_STAMP_FOREACH_SAMPLE";
			rs = st.executeQuery(sql);
			while (rs.next()) {
				deviceInfosList.add(rs.getString(1));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				rs.close();
				st.close();
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}
		}
		return deviceInfosList;
	}

	public List<String> getAllVoiceQualityMarketMultipleSourceCellIdList(
			String testCaseName, String market) {
		List<String> deviceInfosList = new ArrayList<String>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		String test_name = testCaseName + "\\-%";
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String sql = "SELECT CELLLOCATION_CID "
					+ " FROM STG_DEVICE_INFO WHERE TEST_NAME LIKE '"
					+ test_name
					+ "' AND MARKET_ID IN('"
					+ market
					+ "' ) "
					+ "GROUP BY TIME_STAMP_FOREACH_SAMPLE ORDER BY TIME_STAMP_FOREACH_SAMPLE";
			rs = st.executeQuery(sql);
			while (rs.next()) {
				deviceInfosList.add(rs.getString(1));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				rs.close();
				st.close();
				conn.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return deviceInfosList;
	}

	public List<MarketInfo> getMarket_Operator(String userName) {
		List<MarketInfo> marketInfosList = new ArrayList<MarketInfo>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String sql = "SELECT M.MARKET_ID ,M.MARKET_NAME FROM MARKET M ,USER_MARKET MO,USERS U WHERE "
					+ " M.MARKET_ID=MO.MARKET_ID AND MO.USER_ID=U.USER_ID AND U.USER_NAME='"
					+ userName + "' AND MO.ACTIVE='1' ";

			// logger.error("MARKET_ID>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+sql);
			rs = st.executeQuery(sql);
			while (rs.next()) {
				MarketInfo marketInfo = new MarketInfo();
				marketInfo.setMarket_id(rs.getString(1));
				marketInfo.setMarket_name(rs.getString(2));
				marketInfosList.add(marketInfo);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				rs.close();
				st.close();
				conn.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return marketInfosList;
	}

	public static void main(String[] args) {
		// UserDao userDao = new UserDaoImpl();
		// // logger.error(userDao.getUserOperator("kumar"));
		//
		// SimpleDateFormat newSdf = new
		// SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
		// SimpleDateFormat format = new SimpleDateFormat(
		// "yyyy-MM-dd HH:mm:ss.SSS");
		// String inputDate = "01/07/2014 14:45:46";
		// if (!inputDate.equals("")) {
		// Date date;
		// try {
		// date = newSdf.parse(inputDate.trim());
		// // logger.error(date.toString());
		// String sheetVal = format.format(date);
		// // logger.error("sheetVal------" + sheetVal);
		// } catch (ParseException e) {
		// // TODO Auto-generated catch block
		// logger.error(e.getMessage());
		// }

		// }
		//
		// String dateStr = "Jul 27, 2011 8:35:29 PM";
		// DateFormat readFormat = new
		// SimpleDateFormat("MMM dd, yyyy hh:mm:ss aa");
		// DateFormat writeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// Date date = null;
		// try {
		// date = readFormat.parse(dateStr);
		// } catch (ParseException e) {
		// logger.error(e.getMessage());
		// }
		// if (date != null) {
		// String formattedDate = writeFormat.format(date);
		// // logger.error(formattedDate);
		// }
		// UserDaoImpl i = new UserDaoImpl();
		// i.validateIMEICount("241");
		IsDuplicateIMEI("123456789012345", "250");
	}

	public String getUserOperator(String username) {
		String operatorId = null;
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String sql = "SELECT DISTINCT UO.OPERATOR_ID FROM USER_OPERATOR UO, USERS U  "
					+ "WHERE U.USER_ID=UO.USER_ID AND U.USER_NAME='"
					+ username
					+ "'";
			rs = st.executeQuery(sql);
			while (rs.next()) {
				operatorId = rs.getString(1);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				rs.close();
				st.close();
				conn.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return operatorId;
	}

	public List<String> getAllVoiceQualityMarketLatitudeList(
			String testCaseName, String market) {
		List<String> deviceInfosList = new ArrayList<String>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String sql = "SELECT  GEOLOCATION_LATITUDE "
					+ " FROM STG_DEVICE_INFO WHERE TEST_NAME='"
					+ testCaseName
					+ "' AND MARKET_ID IN("
					+ market
					+ ") AND TEST_TYPE='externaltest' "
					+ "GROUP BY TIME_STAMP_FOREACH_SAMPLE ORDER BY TIME_STAMP_FOREACH_SAMPLE";
			rs = st.executeQuery(sql);
			while (rs.next()) {
				deviceInfosList.add(rs.getString(1));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				rs.close();
				st.close();
				conn.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return deviceInfosList;
	}

	public List<String> getAllVoiceQualityMarketSignalStrengthList(
			String testName, String market) {
		List<String> deviceInfosList = new ArrayList<String>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			String sql = "SELECT SIGNALSTRENGTH_GSMSIGNALSTRENGTH "
					+ " FROM STG_DEVICE_INFO WHERE TEST_NAME='"
					+ testName
					+ "'  AND MARKET_ID IN("
					+ market
					+ ") AND TEST_TYPE='externaltest' "
					+ " GROUP BY TIME_STAMP_FOREACH_SAMPLE ORDER BY TIME_STAMP_FOREACH_SAMPLE";
			// //logger.error("sql------------------------"+sql);
			rs = st.executeQuery(sql);
			while (rs.next()) {
				deviceInfosList.add(rs.getString(1));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				rs.close();
				st.close();
				conn.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return deviceInfosList;
	}

	public Map<String, String> getSuperiorRoleUsersMap(String loggedInUserRole,
			String roleId, String operatorId, String loggedUserId) {
		Map<String, String> superiorUsersMap = new HashMap<String, String>();
		Connection conn = DBUtil.openConn();
		String query = "SELECT UR.USER_ID,U.USER_NAME FROM USER_ROLE UR,USERS U,USER_OPERATOR UO, "
				+ "(SELECT PARENT_ROLE FROM ROLES_HIERARCHY WHERE CHILD_ROLE = '"
				+ roleId
				+ "')RH "
				+ " WHERE UR.ROLE_ID = RH.PARENT_ROLE AND U.USER_ID=UR.USER_ID AND UO.USER_ID=U.USER_ID AND UO.OPERATOR_ID = "
				+ operatorId;
		/*
		 * If its a admin user being created then the super admin above admin is
		 * independent of operator, thats y the below condition
		 */
		if (loggedInUserRole.equalsIgnoreCase("admin")) {
			query = "SELECT UR.USER_ID,U.USER_NAME FROM USER_ROLE UR,USERS U, "
					+ "(SELECT PARENT_ROLE FROM ROLES_HIERARCHY WHERE CHILD_ROLE = '"
					+ roleId
					+ "')RH "
					+ " WHERE UR.ROLE_ID = RH.PARENT_ROLE AND U.USER_ID=UR.USER_ID AND   U.USER_ID IN("
					+ loggedUserId + ")";
		}

		try {
			Statement stmt = conn.createStatement();
			System.out.println("query---------" + query);
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				String userId = rs.getString("USER_ID");
				String userName = rs.getString("USER_NAME");
				superiorUsersMap.put(userName, userId);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
		return superiorUsersMap;
	}

	public boolean chechRepoteeExists(String userId) {

		Connection conn = DBUtil.openConn();
		String query = "SELECT CHILD_USER FROM USER_HIERARCHY WHERE PARENT_USER='"
				+ userId + "'";

		try {

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {

				return true;

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}

		return false;
	}

	public Map<String, String> getSuperiorList(String superiorId) {
		Map<String, String> superiorUsersMap = new HashMap<String, String>();
		Connection conn = DBUtil.openConn();
		String roleId = null;
		String operatorId = null;

		String query = "SELECT UR.ROLE_ID,UO.OPERATOR_ID FROM USERS U,USER_ROLE UR,USER_OPERATOR UO "
				+ "WHERE U.USER_ID=UR.USER_ID AND UO.USER_ID=UR.USER_ID AND U.USER_ID='"
				+ superiorId + "'";
		logger.error(query);

		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				roleId = rs.getString("ROLE_ID");
				operatorId = rs.getString("OPERATOR_ID");

			}
			if (operatorId != null && roleId != null) {

				query = "SELECT U.USER_ID,U.USER_NAME FROM USERS U,USER_OPERATOR UO,USER_ROLE UR "
						+ "WHERE U.USER_ID=UO.USER_ID AND UO.USER_ID=UR.USER_ID AND UR.ROLE_ID='"
						+ roleId + "' AND UO.OPERATOR_ID='" + operatorId + "'";
				logger.error(query);
				rs.close();
				stmt.close();
				stmt = conn.createStatement();
				rs = stmt.executeQuery(query);

				while (rs.next()) {

					String userId = rs.getString("USER_ID");
					String userName = rs.getString("USER_NAME");
					superiorUsersMap.put(userName, userId);

				}

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
		return superiorUsersMap;
	}

	/***
	 * Gets user id based on name and password details.
	 * 
	 * @param userName
	 * @param password
	 * @return
	 */
	public int getManagerUserId(String userName, String password) {
		PreparedStatement pst = null;
		Connection conn = null;
		ResultSet rs = null;
		int managerId = 0;
		try {
			conn = DBUtil.getConnection();
			String query = "SELECT PARENT_USER FROM USER_HIERARCHY WHERE CHILD_USER = (SELECT USER_ID FROM USERS WHERE USER_NAME=? AND PASSWORD=?)";
			pst = conn.prepareStatement(query);
			pst.setString(1, userName);
			pst.setString(2, password);
			rs = pst.executeQuery();
			if (rs.next()) {
				managerId = rs.getInt("PARENT_USER");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			}
		}
		return managerId;
	}

	/***
	 * Adds the IMEI number for the manager based on user details.
	 * 
	 * @param userId
	 * @param IMEI
	 * @param mobileNumber
	 * @return
	 */
	public boolean addIMEI(int userId, String IMEI, String mobileNumber) {
		PreparedStatement pst = null;
		Connection conn = null;
		ResultSet rs = null;
		boolean returnStatus = false;
		try {
			conn = DBUtil.getConnection();
			String date = DateUtil.getCurrentDateTime();
			String insertQuery = "INSERT INTO USER_DEVICE (USER_ID,IMEI,CREATED_DATE,MODIFIED_DATE,MOBILE_NUMBER,CREATED_BY,MODIFIED_BY,ACTIVE)"
					+ " VALUES (?,?,?,?,?,?,?,?)";
			pst = conn.prepareStatement(insertQuery);
			pst.setInt(1, userId);
			pst.setString(2, IMEI);
			pst.setString(3, date);
			pst.setString(4, date);
			pst.setString(5, mobileNumber);
			pst.setInt(6, userId);
			pst.setInt(7, userId);
			pst.setInt(8, 1);
			int rows = pst.executeUpdate();
			if (rows > 0) {
				returnStatus = true;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			}
		}
		return returnStatus;
	}

	public String validateIMEICount(String adminId) {
		PreparedStatement pst = null;
		Connection conn = null;
		ResultSet rs = null;
		int limit = 0;int count;
		String output = "";
		String query;
		try {
			
			//getUserDetails(adminId).getSuperioruser();
			conn = DBUtil.getConnection();
			query = "SELECT count(user_device.IMEI) as NoIMEI, IMEI_LIMIT,IMEI_COUNT FROM users,user_device,user_role where users.USER_ID = ?"
							+ "AND user_device.USER_ID = users.USER_ID AND user_role.ROLE_ID = 1 AND users.USER_ID=user_role.USER_ID";
			pst = conn.prepareStatement(query);
			pst.setString(1, adminId);
			//pst.setString(2, adminId);
			rs = pst.executeQuery();
			if (rs.next()) {
				if (rs.getString("IMEI_LIMIT") != null)
					limit = Integer.parseInt(rs.getString("IMEI_LIMIT"));
				else
					limit = 0;

				
				if (rs.getString("NoIMEI") != null)
					count = Integer.parseInt(rs.getString("NoIMEI"));
				else
					count = 0;
			 
				if (count == 0) {
					if (limit == 0) {
						output = "1";
					} else {
						output = String.valueOf(count + 1);
					}
				}
				if (count == 0) {
					count = count + 1;
				}
				int tempString = count;
				if (limit > 0 && tempString > 0) {
					if (limit == count) {//Integer.parseInt(rs.getString("IMEI_COUNT"))
						output = "IMEI_LIMITED";
					} else {
						query = "UPDATE USERS SET IMEI_COUNT=" + count
								+ " WHERE USER_ID=" + adminId;
						output =query;
						pst = conn.prepareStatement(query);
						pst.executeUpdate(query);
					}
				}
			}
		}
	catch (Exception e) {
			logger.error(e.getMessage());
			System.out.println();
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
				logger.error(ee.getMessage());
			}
		}
		return output;
	}

	public String updateIMEICount(String adminId,int imeiCount) {
		PreparedStatement pst = null;
		Connection conn = null;
		ResultSet rs = null;
		int limit = 0;
		String output = "";
		String query;
		System.out.println("update qry data"+imeiCount +"---"+adminId);
		try {
			conn = DBUtil.getConnection();
			query = "UPDATE USERS SET IMEI_COUNT=" + imeiCount
					+ " WHERE USER_ID=" + adminId;
			output =query;
			pst = conn.prepareStatement(query);
			System.out.println("update query "+query);
			pst.executeUpdate();
		}
		 catch (Exception e) {
			logger.error(e.getMessage());
			System.out.println();
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
				logger.error(ee.getMessage());
			}
		}
		return output;
	}
	public static boolean IsDuplicateIMEI(String imei, String adminId) {
		PreparedStatement pst = null;
		Connection conn = null;
		ResultSet rs = null;
		boolean output = false;
		try {
			conn = DBUtil.getConnection();
			String query = "select * from user_device where IMEI=? and CREATED_BY = ?";
			pst = conn.prepareStatement(query);
			pst.setString(1, imei);
			pst.setString(2, adminId);
			rs = pst.executeQuery();
			if (rs.next()) {
				output = true;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			System.out.println();
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
				logger.error(ee.getMessage());
			}
		}
		return output;
	}
	
	public static String getUserRole(String userId) {
		PreparedStatement pst = null;
		Connection conn = null;
		ResultSet rs = null;
		String output = "";
		try {
			conn = DBUtil.getConnection();
//			String query = "select role_id,role_name from user_role where user_id=?";
			String query = "select ur.role_id , r.role_name from user_role ur, roles r where user_id= ? and r.role_id = ur.role_id";
			pst = conn.prepareStatement(query);
			pst.setString(1, userId);
			rs = pst.executeQuery();
			if (rs.next()) {
				System.out.println(rs.getString("role_id"));
				String temp = rs.getString("role_id");
				temp = temp.trim();
				if(temp.equalsIgnoreCase("1"))
					output = rs.getString("role_name").trim();
				 else if(temp.equalsIgnoreCase("5"))
					output = rs.getString("role_name").trim();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			System.out.println();
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
				logger.error(ee.getMessage());
			}
		}
		return output;
	}

	private int findDuplicates(List<String> tempArray) {		 
		final Set<String> setToReturn = new HashSet<String>();
		final Set<String> set1 = new HashSet<String>();
 
		for (String yourInt : tempArray) {
			if (!set1.add(yourInt)) {
				setToReturn.add(yourInt);
			}
		}
		return setToReturn.size();
	}

	/***
	 * Gets IMEI limit value against the user from users table  
	 * 
	 * @param userId
	 * @return
	 */
	private int getIMEILimitValue(String userId) {
		PreparedStatement pst = null;
		Connection conn = null;
		ResultSet rs = null;
		int output = 0;
		try {
			conn = DBUtil.getConnection();
			String query = "select imei_limit from users where user_id =?";
			pst = conn.prepareStatement(query);
			pst.setString(1, userId);
			rs = pst.executeQuery();
			if (rs.next()) {
				if(rs.getString("imei_limit").trim()!=null)
					output = Integer.parseInt(rs.getString("imei_limit").trim());
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			System.out.println();
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
				logger.error(ee.getMessage());
			}
		}
		return output;
	}
	/***
	 * Gets IMEI count value against the user from users table  
	 * 
	 * @param userId
	 * @return
	 */
	public int getIMEICountValue(String userId) {
		PreparedStatement pst = null;
		Connection conn = null;
		ResultSet rs = null;
		int output = 0;
		try {
			conn = DBUtil.getConnection();
			String query = "select imei_count from users where user_id =?";
			pst = conn.prepareStatement(query);
			pst.setString(1, userId);
			rs = pst.executeQuery();
			if (rs.next()) {
				if(rs.getString("imei_count").trim()!=null)
					output = Integer.parseInt(rs.getString("imei_count").trim());
				System.out.println("count output"+output);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			System.out.println();
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
				logger.error(ee.getMessage());
			}
		}
		return output;
	}

	public List<String> getAllDeviceInfoCallDropList(String testCaseName,
			String marketId, String testtype) {//, int flag
		List<String> deviceInfosList = new ArrayList<String>();
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		String deviceInfoQuery = null;
		try {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
			/*if (flag == 1)
				deviceInfoQuery = "select time_stamp_foreach_sample from pre_cal_callretention_1 where parameter = 'Call Dropped' and test_name = '"
						+ testCaseName + "'";
			else*/
				deviceInfoQuery = "select time_stamp from stg_log_cat_info where test_name like '%"
						+ testCaseName + "%' and event_name = 'CALL_DROP'";
			// logger.error("deviceInfoQuery------------"+deviceInfoQuery);
			rs = st.executeQuery(deviceInfoQuery);
			while (rs.next()) {
				String timeStamp = rs.getString(1);
				/*if (flag == 1) {
					deviceInfosList.add(timeStamp);
				} else {*/
					if (null != timeStamp) {
						VoiceConnectivityProccesorHelper vcHelper = new VoiceConnectivityProccesorHelper();
						DeviceInfoTO dto = vcHelper.matchinDeviceInfo(
								timeStamp, "'%Y-%m-%d %H:%i:%s'", testCaseName,
								marketId, "mo");
						deviceInfosList.add(dto.getTimeStampForEachSample());
					}

				}
			//}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				rs.close();
				st.close();
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}
		}
		return deviceInfosList;
	}

	/*public boolean deleteUser(int userId) {
		boolean status = false;
		PreparedStatement pst = null;
		Connection conn = null;
		ResultSet rs = null;

		try {

			conn = DBUtil.getConnection();
			String query = "UPDATE USERS SET ACTIVE=0 WHERE USER_ID='" + userId
					+ "'";
			pst = conn.prepareStatement(query);
			int rowCount = pst.executeUpdate();

			if (rowCount > 0) {

				status = true;
			}

		} catch (Exception e) {

			logger.error(e.getMessage());
			return false;
		} finally {
			try {
				pst.close();
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}
		}

		return status;
	}*/
	@Override
	public boolean deleteUser(String userId) {
		// TODO Auto-generated method stub
		boolean status = false;
		PreparedStatement pst = null;
		Connection conn = null;
		ResultSet rs = null;

		try {

			conn = DBUtil.getConnection();
			String query = "delete from user_operator WHERE USER_ID='" + userId + "'";
			System.out.println("query- "+query);
			pst = conn.prepareStatement(query);
			int rowCount = pst.executeUpdate();
			
			String query1 = "delete from user_role WHERE USER_ID='" + userId + "'";
			System.out.println("query1- "+query1);
			pst = conn.prepareStatement(query1);
			int rowCount1 = pst.executeUpdate();
			
			String query2 = "delete from user_market  WHERE USER_ID='" + userId + "'";
			System.out.println("query2- "+query2);
			pst = conn.prepareStatement(query2);
			int rowCount2 = pst.executeUpdate();
			
			String query3 = "delete from user_device WHERE USER_ID='" + userId + "'";
			System.out.println("query3- "+query3);
			pst = conn.prepareStatement(query3);
			int rowCount3 = pst.executeUpdate();
			
			/*String query4 = "delete user_hierarchy WHERE USER_ID='" + userId
					+ "'";
			pst = conn.prepareStatement(query4);
			int rowCount4 = pst.executeUpdate();*/
			
			String query5 = "delete from user_test WHERE USER_ID='" + userId + "'";
			System.out.println("query5- "+query5);
			pst = conn.prepareStatement(query5);
			int rowCount5 = pst.executeUpdate();
			
			String query6 = "delete from users WHERE USER_ID='" + userId + "'";
			System.out.println("query6- "+query6);
			pst = conn.prepareStatement(query6);
			int rowCount6 = pst.executeUpdate();
			if (rowCount6 > 0) {
				status = true;
			}
			
			/*String query = "UPDATE USERS SET ACTIVE=0 WHERE USER_ID='" + userId +"'";
			//System.out.println("update query :"+query);
			pst = conn.prepareStatement(query);
			int rowCount = pst.executeUpdate();

			if (rowCount > 0) {

				status = true;
			}*/

		} catch (Exception e) {

			logger.error(e.getMessage());
			return false;
		} finally {
			try {
				pst.close();
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}
		}

		return status;
	}
	
	public boolean AuthenticateUserDevice(String userId,String DeviceId) {
		boolean status = false;
		PreparedStatement pst = null;
		Connection conn = null;
		ResultSet rs = null;

		try {
			conn = DBUtil.getConnection();
			String query = "UPDATE USER_DEVICE SET AUTHENTICATED=1 WHERE USER_ID='"+userId +"' AND DEVICE_ID='"+ DeviceId +"'" ;
			pst = conn.prepareStatement(query);
			int rowCount = pst.executeUpdate();

			if (rowCount > 0) {

				status = true;
			}

		} catch (Exception e) {

			logger.error(e.getMessage());
			return false;
		} finally {
			try {
				pst.close();
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}
		}

		return status;
	}
	/***
	 * Gets user id based on name and password details.
	 * 
	 * @param userName
	 * @param password
	 * @return
	 */
	public int getSuperiorUserIdbyuserid(String userid) {
		PreparedStatement pst = null;
		Connection conn = null;
		ResultSet rs = null;
		int managerId = 0;
		try {
			conn = DBUtil.getConnection();
			String query = "SELECT PARENT_USER FROM USER_HIERARCHY WHERE CHILD_USER = ?";
			pst = conn.prepareStatement(query);
			pst.setString(1, userid);
			rs = pst.executeQuery();
			if (rs.next()) {
				managerId = rs.getInt("PARENT_USER");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
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
				logger.error(ee.getMessage());
			}
		}
		return managerId;
	}
}
