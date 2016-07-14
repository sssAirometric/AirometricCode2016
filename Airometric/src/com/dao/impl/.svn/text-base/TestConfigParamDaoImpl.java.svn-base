package com.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;

import com.dao.TestConfigParamDao;
import com.enums.DataPingTestEnum;
import com.enums.FTPTestEnum;
import com.enums.MOTestEnum;
import com.enums.MTTestEnum;
import com.enums.UDPTestEnum;
import com.enums.VoipTestEnum;
import com.enums.WebPageTestEnum;
import com.model.DBUtil;
import com.to.DataPingTestCase;
import com.to.FTPTestCase;
import com.to.MOTestCase;
import com.to.MTTestCase;
import com.to.UDPTestCase;
import com.to.VoipTestCase;
import com.to.WebPageTestCase;
import com.util.DateUtil;

public class TestConfigParamDaoImpl implements TestConfigParamDao{
	/**
	 * This method is used to add the testconfig parameters
	 */
	public void addTestConfigParam(Object obj,Integer testConfigID,Integer testTypeID,String userId) {
		PreparedStatement pst = null;
		Connection conn = null;
		ResultSet rs = null;
		try {
			conn = DBUtil.getConnection();
			String date = DateUtil.getCurrentDateTime();
			String query = "INSERT INTO TEST_CONFIG_PARAM(TEST_CONFIG_ID,TEST_TYPE_ID,TEST_PARAM_ID,TEST_PARAM_VALUE,ACTIVE,CREATED_DATE,MODIFIED_DATE,CREATED_BY,MODIFIED_BY) " +
							"VALUES (?,?,?,?,?,?,?,?,?);";
			pst = conn.prepareStatement(query);
			if(obj instanceof MOTestCase){
				MOTestCase moTestCase = (MOTestCase)obj;
				for(int x=0; x<moTestCase.getNumberOfParam();x++){
					pst.setInt(1, testConfigID);
					pst.setInt(2, testTypeID);
					if(x == 0){
						pst.setInt(3, MOTestEnum.PHONENUMBER.getTestParamID());
						pst.setString(4, moTestCase.getPhoneNumber());
					}else if(x == 1){
						pst.setInt(3, MOTestEnum.CALLDURATION.getTestParamID());
						pst.setString(4, moTestCase.getCallDuration());
					}else if(x == 2){
						pst.setInt(3, MOTestEnum.PAUSETIME.getTestParamID());
						pst.setString(4, moTestCase.getPauseTime());
					}else if(x == 3){
						pst.setInt(3, MOTestEnum.TESTDURATION.getTestParamID());
						pst.setString(4, moTestCase.getTestDuration());
					}
					pst.setInt(5, 1);
					pst.setString(6, date);
					pst.setString(7, date);
					pst.setString(8, userId);
					pst.setString(9, userId);
					pst.executeUpdate();
				}
			}else if(obj instanceof FTPTestCase){
				FTPTestCase ftpTestCase = (FTPTestCase)obj;
				for(int x=0; x<ftpTestCase.getNumberOfParam();x++){
					pst.setInt(1, testConfigID);
					pst.setInt(2, testTypeID);
					if(x == 0){
						pst.setInt(3, FTPTestEnum.FTPSERVERURL.getTestParamID());
						pst.setString(4, ftpTestCase.getFtpServerURL());
					}else if(x == 1){
						pst.setInt(3, FTPTestEnum.FTPUSERNAME.getTestParamID());
						pst.setString(4, ftpTestCase.getFtpUsername());
					}else if(x == 2){
						pst.setInt(3, FTPTestEnum.FTPPASSWORD.getTestParamID());
						pst.setString(4, ftpTestCase.getFtpPassword());
					}else if(x == 3){
						pst.setInt(3, FTPTestEnum.NUMBEROFREPEATCYCLES.getTestParamID());
						pst.setString(4, ftpTestCase.getNoOfRepeatCycles());
					}else if(x == 4){
						pst.setInt(3, FTPTestEnum.FILEPATHTOUPLOAD.getTestParamID());
						pst.setString(4, ftpTestCase.getUploadFilePath());
					}else if(x == 5){
						pst.setInt(3, FTPTestEnum.FTPFILETODOWNLOAD.getTestParamID());
						pst.setString(4, ftpTestCase.getFileDownloadPath());
					}
					pst.setInt(5, 1);
					pst.setString(6, date);
					pst.setString(7, date);
					pst.setString(8, userId);
					pst.setString(9, userId);
					pst.executeUpdate();
				}
			}else if(obj instanceof MTTestCase){
				MTTestCase mtTestCase = (MTTestCase)obj;
				for(int x=0; x<mtTestCase.getNumberOfParam();x++){
					pst.setInt(1, testConfigID);
					pst.setInt(2, testTypeID);
					if(x == 0){
						pst.setInt(3, MTTestEnum.TESTDURATION.getTestParamID());
						pst.setString(4, mtTestCase.getTestDurationMt());
					}else	if(x == 1){
						pst.setInt(3, MTTestEnum.CALLDURATION.getTestParamID());
						pst.setString(4, mtTestCase.getCallDurationMt());
					}
					pst.setInt(5, 1);
					pst.setString(6, date);
					pst.setString(7, date);
					pst.setString(8, userId);
					pst.setString(9, userId);
					pst.executeUpdate();
				}
			}else if(obj instanceof UDPTestCase){
				UDPTestCase udpTestCase = (UDPTestCase)obj;
				for(int x=0; x<udpTestCase.getNumberOfParam();x++){
					pst.setInt(1, testConfigID);
					pst.setInt(2, testTypeID);
					if(x == 0){
						pst.setInt(3, UDPTestEnum.UDPSERVERURL.getTestParamID());
						pst.setString(4, udpTestCase.getUdpServerURL());
					}else if(x == 1){
						pst.setInt(3, UDPTestEnum.NUMBEROFREPEATCYCLES.getTestParamID());
						pst.setString(4, udpTestCase.getNoOfRepeatCyclesUDP());
					}else if(x == 2){
						pst.setInt(3, UDPTestEnum.FILEPATHTOUPLOAD.getTestParamID());
						pst.setString(4, udpTestCase.getFilePathToUpload());
					}else if(x == 3){
						pst.setInt(3, UDPTestEnum.UDPSERVERPORT.getTestParamID());
						pst.setString(4, udpTestCase.getUdpServerPort());
					}
					pst.setInt(5, 1);
					pst.setString(6, date);
					pst.setString(7, date);
					pst.setString(8, userId);
					pst.setString(9, userId);
					pst.executeUpdate();
				}
			}else if(obj instanceof DataPingTestCase){
				DataPingTestCase dataPingTestCase  = (DataPingTestCase)obj;
				for(int x=0; x<dataPingTestCase.getNumberOfParam();x++){
					pst.setInt(1, testConfigID);
					pst.setInt(2, testTypeID);
					if(x == 0){
						pst.setInt(3, DataPingTestEnum.SERVERURLTOPING.getTestParamID());
						pst.setString(4, dataPingTestCase.getServerURL());
					}else if(x == 1){
						pst.setInt(3, DataPingTestEnum.NUMBEROFREPEATCYCLESPING.getTestParamID());
						pst.setString(4, dataPingTestCase.getNoOfRepeatCyclesPing());
					}
					pst.setInt(5, 1);
					pst.setString(6, date);
					pst.setString(7, date);
					pst.setString(8, userId);
					pst.setString(9, userId);
					pst.executeUpdate();
				}
			}else if(obj instanceof WebPageTestCase){
				WebPageTestCase webPageTestCase  = (WebPageTestCase)obj;
				for(int x=0; x<webPageTestCase.getNumberOfParam();x++){
					pst.setInt(1, testConfigID);
					pst.setInt(2, testTypeID);
					if(x == 0){
						pst.setInt(3, WebPageTestEnum.WEBPAGEURL.getTestParamID());
						pst.setString(4, webPageTestCase.getWebPageURL());
					}else if(x == 1){
						pst.setInt(3, WebPageTestEnum.NUMBEROFREPEATCYCLESWEB.getTestParamID());
						pst.setString(4, webPageTestCase.getNumberofrepeatcyclesInWeb());
					}
					pst.setInt(5, 1);
					pst.setString(6, date);
					pst.setString(7, date);
					pst.setString(8, userId);
					pst.setString(9, userId);
					pst.executeUpdate();
				}
			}else if(obj instanceof VoipTestCase){
				VoipTestCase  voipTestCase = (VoipTestCase)obj;
				for(int x=0; x<voipTestCase.getNumberOfParam();x++){
					pst.setInt(1, testConfigID);
					pst.setInt(2, testTypeID);
					if(x == 0){
						pst.setInt(3, VoipTestEnum.TESTDURATION.getTestParamID());
						pst.setString(4, voipTestCase.getTestDurationVoip());
					}
					pst.setInt(5, 1);
					pst.setString(6, date);
					pst.setString(7, date);
					pst.setString(8, userId);
					pst.setString(9, userId);
					pst.executeUpdate();
				}
			}
			else {
					pst.setInt(1, testConfigID);
					pst.setInt(2, testTypeID);
					pst.setInt(3, 27);
					pst.setString(4, obj.toString());
					pst.setInt(5, 1);
					pst.setString(6, date);
					pst.setString(7, date);
					pst.setString(8, userId);
					pst.setString(9, userId);
					pst.executeUpdate();
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
	}
  
	public void updateTestConfigParam(Object obj, Integer testConfigID,
			Integer testTypeID, String userId) {
		PreparedStatement pst = null;
		Connection conn = null;
		ResultSet rs = null;
		try {
			conn = DBUtil.getConnection();
			String date = DateUtil.getCurrentDateTime();
			String insertquery = " INSERT INTO TEST_CONFIG_PARAM(TEST_CONFIG_ID,TEST_TYPE_ID,TEST_PARAM_ID, " +
            " TEST_PARAM_VALUE,ACTIVE,CREATED_DATE,MODIFIED_DATE,CREATED_BY,MODIFIED_BY)  " +
            " VALUES (?,?,?,?,?,?,?,?,?); ";
			pst = conn.prepareStatement(insertquery);
			if(obj instanceof MOTestCase){
				MOTestCase moTestCase = (MOTestCase)obj;
				for(int x=0; x<moTestCase.getNumberOfParam();x++){
					pst.setInt(1, testConfigID);
					pst.setInt(2, testTypeID);
					if(x == 0){
						pst.setInt(3, MOTestEnum.PHONENUMBER.getTestParamID());
						pst.setString(4, moTestCase.getPhoneNumber());
					}else if(x == 1){
						pst.setInt(3, MOTestEnum.CALLDURATION.getTestParamID());
						pst.setString(4, moTestCase.getCallDuration());
					}else if(x == 2){
						pst.setInt(3, MOTestEnum.PAUSETIME.getTestParamID());
						pst.setString(4, moTestCase.getPauseTime());
					}else if(x == 3){
						pst.setInt(3, MOTestEnum.TESTDURATION.getTestParamID());
						pst.setString(4, moTestCase.getTestDuration());
					}
					pst.setInt(5, 1);
					pst.setString(6, date);
					pst.setString(7, date);
					pst.setString(8, userId);
					pst.setString(9, userId);
					pst.executeUpdate();
				}
			}else if(obj instanceof FTPTestCase){
				FTPTestCase ftpTestCase = (FTPTestCase)obj;
				for(int x=0; x<ftpTestCase.getNumberOfParam();x++){
					pst.setInt(1, testConfigID);
					pst.setInt(2, testTypeID);
					if(x == 0){
						pst.setInt(3, FTPTestEnum.FTPSERVERURL.getTestParamID());
						pst.setString(4, ftpTestCase.getFtpServerURL());
					}else if(x == 1){
						pst.setInt(3, FTPTestEnum.FTPUSERNAME.getTestParamID());
						pst.setString(4, ftpTestCase.getFtpUsername());
					}else if(x == 2){
						pst.setInt(3, FTPTestEnum.FTPPASSWORD.getTestParamID());
						pst.setString(4, ftpTestCase.getFtpPassword());
					}else if(x == 3){
						pst.setInt(3, FTPTestEnum.NUMBEROFREPEATCYCLES.getTestParamID());
						pst.setString(4, ftpTestCase.getNoOfRepeatCycles());
					}else if(x == 4){
						pst.setInt(3, FTPTestEnum.FILEPATHTOUPLOAD.getTestParamID());
						pst.setString(4, ftpTestCase.getUploadFilePath());
					}else if(x == 5){
						pst.setInt(3, FTPTestEnum.FTPFILETODOWNLOAD.getTestParamID());
						pst.setString(4, ftpTestCase.getFileDownloadPath());
					}
					pst.setInt(5, 1);
					pst.setString(6, date);
					pst.setString(7, date);
					pst.setString(8, userId);
					pst.setString(9, userId);
					pst.executeUpdate();
				}
			}else if(obj instanceof MTTestCase){
				MTTestCase mtTestCase = (MTTestCase)obj;
				for(int x=0; x<mtTestCase.getNumberOfParam();x++){
					pst.setInt(1, testConfigID);
					pst.setInt(2, testTypeID);
					if(x == 0){
						pst.setInt(3, MTTestEnum.TESTDURATION.getTestParamID());
						pst.setString(4, mtTestCase.getTestDurationMt());
					}else	if(x == 1){
						pst.setInt(3, MTTestEnum.CALLDURATION.getTestParamID());
						pst.setString(4, mtTestCase.getCallDurationMt());
					}
					pst.setInt(5, 1);
					pst.setString(6, date);
					pst.setString(7, date);
					pst.setString(8, userId);
					pst.setString(9, userId);
					pst.executeUpdate();
				}
			}else if(obj instanceof UDPTestCase){
				UDPTestCase udpTestCase = (UDPTestCase)obj;
				for(int x=0; x<udpTestCase.getNumberOfParam();x++){
					pst.setInt(1, testConfigID);
					pst.setInt(2, testTypeID);
					if(x == 0){
						pst.setInt(3, UDPTestEnum.UDPSERVERURL.getTestParamID());
						pst.setString(4, udpTestCase.getUdpServerURL());
					}else if(x == 1){
						pst.setInt(3, UDPTestEnum.NUMBEROFREPEATCYCLES.getTestParamID());
						pst.setString(4, udpTestCase.getNoOfRepeatCyclesUDP());
					}else if(x == 2){
						pst.setInt(3, UDPTestEnum.FILEPATHTOUPLOAD.getTestParamID());
						pst.setString(4, udpTestCase.getFilePathToUpload());
					}else if(x == 3){
						pst.setInt(3, UDPTestEnum.UDPSERVERPORT.getTestParamID());
						pst.setString(4, udpTestCase.getUdpServerPort());
					}
					pst.setInt(5, 1);
					pst.setString(6, date);
					pst.setString(7, date);
					pst.setString(8, userId);
					pst.setString(9, userId);
					pst.executeUpdate();
				}
			}else if(obj instanceof DataPingTestCase){
				DataPingTestCase dataPingTestCase  = (DataPingTestCase)obj;
				for(int x=0; x<dataPingTestCase.getNumberOfParam();x++){
					pst.setInt(1, testConfigID);
					pst.setInt(2, testTypeID);
					if(x == 0){
						pst.setInt(3, DataPingTestEnum.SERVERURLTOPING.getTestParamID());
						pst.setString(4, dataPingTestCase.getServerURL());
					}else if(x == 1){
						pst.setInt(3, DataPingTestEnum.NUMBEROFREPEATCYCLESPING.getTestParamID());
						pst.setString(4, dataPingTestCase.getNoOfRepeatCyclesPing());
					}
					pst.setInt(5, 1);
					pst.setString(6, date);
					pst.setString(7, date);
					pst.setString(8, userId);
					pst.setString(9, userId);
					pst.executeUpdate();
				}
			}else if(obj instanceof WebPageTestCase){
				WebPageTestCase webPageTestCase  = (WebPageTestCase)obj;
				for(int x=0; x<webPageTestCase.getNumberOfParam();x++){
					pst.setInt(1, testConfigID);
					pst.setInt(2, testTypeID);
					if(x == 0){
						pst.setInt(3, WebPageTestEnum.WEBPAGEURL.getTestParamID());
						pst.setString(4, webPageTestCase.getWebPageURL());
					}else if(x == 1){
						pst.setInt(3, WebPageTestEnum.NUMBEROFREPEATCYCLESWEB.getTestParamID());
						pst.setString(4, webPageTestCase.getNumberofrepeatcyclesInWeb());
					}
					pst.setInt(5, 1);
					pst.setString(6, date);
					pst.setString(7, date);
					pst.setString(8, userId);
					pst.setString(9, userId);
					pst.executeUpdate();
				}
			}else if(obj instanceof VoipTestCase){
				VoipTestCase  voipTestCase = (VoipTestCase)obj;
				for(int x=0; x<voipTestCase.getNumberOfParam();x++){
					pst.setInt(1, testConfigID);
					pst.setInt(2, testTypeID);
					if(x == 0){
						pst.setInt(3, VoipTestEnum.TESTDURATION.getTestParamID());
						pst.setString(4, voipTestCase.getTestDurationVoip());
					}
					pst.setInt(5, 1);
					pst.setString(6, date);
					pst.setString(7, date);
					pst.setString(8, userId);
					pst.setString(9, userId);
					pst.executeUpdate();
				}
			}
			else {
				pst.setInt(1, testConfigID);
				pst.setInt(2, testTypeID);
				pst.setInt(3, 27);
				pst.setString(4, obj.toString());
				pst.setInt(5, 1);
				pst.setString(6, date);
				pst.setString(7, date);
				pst.setString(8, userId);
				pst.setString(9, userId);
				pst.executeUpdate();
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
		
	}

}
