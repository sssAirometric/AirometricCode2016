package com.enums;

public enum TestTypeEnum {
	MO(1),MT(2),FTP(3),UDP(4),DATAPING(5),WEBDOWNLOAD(6),VOIP(7),EXTERNAL(8);
	public int testTypeID;
	 
	private TestTypeEnum(int testtype) {
		testTypeID = testtype;
	}

	public int getTestTypeID() {
		return testTypeID;
	}
}
