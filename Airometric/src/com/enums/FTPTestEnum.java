package com.enums;

public enum FTPTestEnum {
	FTPSERVERURL(5),NUMBEROFREPEATCYCLES(6),FILEPATHTOUPLOAD(7),FTPUSERNAME(22),FTPPASSWORD(23),FTPFILETODOWNLOAD(24);
	public int testParamID;
	 
	private FTPTestEnum(int testParam) {
		testParamID = testParam;
	}
	public int getTestParamID() {
		return testParamID;
	}
}
