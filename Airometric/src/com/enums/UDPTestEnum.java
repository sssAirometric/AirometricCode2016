package com.enums;

public enum UDPTestEnum {
	UDPSERVERURL(11),NUMBEROFREPEATCYCLES(12),FILEPATHTOUPLOAD(13),UDPSERVERPORT(21);
	public int testParamID;
	 
	private UDPTestEnum(int testParam) {
		testParamID = testParam;
	}
	public int getTestParamID() {
		return testParamID;
	}
}
