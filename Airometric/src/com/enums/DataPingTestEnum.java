package com.enums;

public enum DataPingTestEnum {
	SERVERURLTOPING(14), NUMBEROFREPEATCYCLESPING(25); 
	
	public int testParamID;
	 
	private DataPingTestEnum(int testParam) {
		testParamID = testParam;
	}
	public int getTestParamID() {
		return testParamID;
	}
}
