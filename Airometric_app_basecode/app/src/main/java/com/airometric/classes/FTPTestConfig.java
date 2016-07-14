package com.airometric.classes;

import java.io.Serializable;

import com.airometric.utility.L;

public class FTPTestConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5664986537590829266L;
	public String sServerURL, sUsername, sPassword, sTestCyles, sUploadPath,
			sFileToDownload, sManuallUploadFile;
	public boolean bManuallUploadStatus;

	public FTPTestConfig(String sServerURL, String sUsername, String sPassword,
			String sTestCyles, String sUploadPath, String sFileToDownload, String sManuallFileUploadPath, boolean bManuallUploadStatus) {
		this.sServerURL = sServerURL;
		this.sUsername = sUsername;
		this.sPassword = sPassword;
		this.sTestCyles = sTestCyles;
		this.sUploadPath = sUploadPath;
		this.sFileToDownload = sFileToDownload;
		this.sManuallUploadFile = sManuallFileUploadPath;
		this.bManuallUploadStatus = bManuallUploadStatus;
	}

	@Override
	public String toString() {
		String toString = "FTPTestConfig() :: Server URL = " + sServerURL
				+ ",  Username = " + sUsername + ",  Password = " + sPassword
				+ ",  Cycles = " + sTestCyles + ",  Upload path = "
				+ sUploadPath + ",  File to download = " + sFileToDownload
				+ ", Upload File Path =" + sManuallUploadFile;
		L.debug("=========== FTP TEST CONFIG INFO ==============: " + toString);
		
		return toString;
	}
}
