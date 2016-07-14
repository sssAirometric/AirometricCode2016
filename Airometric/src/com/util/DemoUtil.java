package com.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.dao.UserDao;
import com.dao.impl.UserDaoImpl;
import com.model.DBUtil;
import com.services.ResponseStatusENUM;
import com.to.UserBean;
import com.to.UserDeviceInfo;

public class DemoUtil {
	
	/* this code is done by sss for demo purpose here we made a demo validate user who can log in by default user name and password and
	if imei is not exist then it will create a new user with that imei no.   */
	/**
	 * This method validates the user
	 */
	public String demovalidateUser(String username, String password,String imei) {
		PreparedStatement pst = null,pst1 = null;
		Connection conn = null;
		ResultSet  rs = null,rs1 = null,rs2 = null;
		String userid = null;String query;
		String userStatus = ResponseStatusENUM.AUTH_FAILURE.getStatus();
		try {
			conn = DBUtil.getConnection();
			query = "SELECT distinct(U.USER_ID),R.ROLE_NAME,U.TERMS_CONDITIONS_ACCEPTED,U.ACTIVE FROM USERS U,ROLES R,"
					+ "USER_ROLE UR,USER_DEVICE UD,USER_OPERATOR UO WHERE U.USER_NAME = ? AND BINARY(U.PASSWORD)=BINARY(?) AND "
					+ "UR.USER_ID=U.USER_ID AND U.USER_ID=UO.USER_ID AND UR.ROLE_ID=R.ROLE_ID  ";//AND U.USER_ID = UD.USER_ID AND UD.IMEI=?
			pst = conn.prepareStatement(query);
			pst.setString(1, username);
			pst.setString(2, password);
			//System.out.println(pst);
			//pst.setString(3, imei);
			rs = pst.executeQuery();
			if (rs.next()) {
				if (rs.getString("ACTIVE").equals("1")) {
					userid = rs.getString("USER_ID");
					String querycheckdevice =  query+"AND U.USER_ID = UD.USER_ID AND UD.IMEI=?";// 
					pst = conn.prepareStatement(querycheckdevice);
					pst.setString(1, username);
					pst.setString(2, password);
					pst.setString(3, imei);
					rs1 = pst.executeQuery();
					if (rs1.next()) {
							if (rs1.getString("ACTIVE").equals("1")) 
							{
							userStatus = "success" + "," + rs1.getString("ROLE_NAME")
							+ "," + rs1.getString("TERMS_CONDITIONS_ACCEPTED");
							} 
							else {userStatus = ResponseStatusENUM.USER_STATUS_INACTIVE.getStatus();}
							}else {
							UserBean userbeans = new UserBean();
							/*this code is for add IMEL to the manager of guest user 
							 * pst1 = conn.prepareStatement("select * from user_hierarchy where CHILD_USER=?");
							pst1.setString(1, userid);
							rs2 = pst1.executeQuery();
							if(rs2.next())
							{userid = rs2.getString("PARENT_USER");}
							else {userStatus = ResponseStatusENUM.AUTH_FAILURE.getStatus();}*/
							//System.out.println("r u der");
							userbeans.setUserId(userid);
							UserDeviceInfo udi = new UserDeviceInfo();
							udi.setImei(imei);
							udi.setDeviceType("testdevice");
							udi.setMobileNumber("0987654321");
							List<UserDeviceInfo>  userDeviceInfo = new  ArrayList<UserDeviceInfo>();
							userDeviceInfo.add(0, udi);
							userbeans.setUserDeviceInfo(userDeviceInfo);
							userStatus = democreateUserDevice(userbeans);
							if(userStatus.equals("success")){
							userStatus = "success" + "," + rs.getString("ROLE_NAME")+ "," + rs.getString("TERMS_CONDITIONS_ACCEPTED");
						}//end of if which checks the success
					}			
				}else {userStatus = ResponseStatusENUM.USER_STATUS_INACTIVE.getStatus();}
			}else {userStatus = ResponseStatusENUM.AUTH_FAILURE.getStatus();}
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
		return userStatus;
	}	

	//this code is to insert device for a guset user if that device is not exist
	public String democreateUserDevice(UserBean userBean) {
		Integer userIdLogged = Integer.parseInt(userBean.getUserId());
		UserDao userDao = new UserDaoImpl();
		Connection conn = null;
		String status = "failure";
		ResultSet rs = null;
		Statement st;
		PreparedStatement pst = null;
		int userId = Integer.parseInt(userBean.getUserId());
		String date = DateUtil.getCurrentDateTime();
		try {
			//if (isUserExists == false) {
			conn = DBUtil.getConnection();
			st = conn.createStatement();
				status = "success";
				String deviceQuery = "INSERT INTO USER_DEVICE (USER_ID,IMEI,DEVICE_TYPE,MOBILE_NUMBER,ACTIVE,CREATED_DATE,MODIFIED_DATE,CREATED_BY,MODIFIED_BY)"
						+ "VALUES (?,?,?,?,?,?,?,?,?)";
				pst = conn.prepareStatement(deviceQuery);
				for (int i = 0; i < userBean.getUserDeviceInfo().size(); i++) {
					UserDeviceInfo userDeviceInfo = userBean
							.getUserDeviceInfo().get(0);
					pst.setInt(1, userId);
					pst.setString(2, userDeviceInfo.getImei());
					pst.setString(3, userDeviceInfo.getDeviceType());
					pst.setString(4, userDeviceInfo.getMobileNumber());
					pst.setInt(5, 1);
					pst.setString(6, date);
					pst.setString(7, date);
					pst.setInt(8, userIdLogged);
					pst.setInt(9, userIdLogged);
					System.out.print(pst);
					pst.executeUpdate();
					 
					String SuperiorUserId = String.valueOf(userDao.getUserDetails(userBean.getUserId()).getSuperioruser());
					String AdminUserId = String.valueOf(userDao.getUserDetails(SuperiorUserId).getSuperioruser());
					int a = userDao.getIMEICountValue(AdminUserId)+1;
					userDao.updateIMEICount(AdminUserId, a);
				}
				
			//}

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

		//status = status + "&&&" + userBean.getUserMessage();
		return status;
}
}
