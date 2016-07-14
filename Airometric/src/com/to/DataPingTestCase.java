package com.to;

import java.io.Serializable;

public class DataPingTestCase implements Serializable{
	private static final long serialVersionUID = 1L;
	private String serverURL= "";
	private String noOfRepeatCyclesPing= "";
	private int numberOfParam =2;
	public int getNumberOfParam() {
		return numberOfParam;
	}
	public String getServerURL() {
		return serverURL;
	}
	public void setServerURL(String serverURL) {
		this.serverURL = serverURL;
	}
	public String getNoOfRepeatCyclesPing() {
		return noOfRepeatCyclesPing;
	}
	public void setNoOfRepeatCyclesPing(String noOfRepeatCyclesPing) {
		this.noOfRepeatCyclesPing = noOfRepeatCyclesPing;
	}
	
}
