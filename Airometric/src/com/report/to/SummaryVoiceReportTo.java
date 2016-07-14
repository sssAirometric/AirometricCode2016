package com.report.to;

public class SummaryVoiceReportTo {
	private String marketName;
	private String testName;
	private String totalCalls;
	private String callfailure;
	private String dropcalls;
	private String missedCalls;
	private String dcr;
	private String avg_downlinkMOS;
	private String avg_uplinkMOS;
	private String max_downlinkMOS;
	private String max_uplinkMOS;
	private String callfailurePercentage;
	private String eModel;
	private String date;
	private String userName;
	
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getMissedCalls() {
		return missedCalls;
	}

	public void setMissedCalls(String missedCalls) {
		this.missedCalls = missedCalls;
	}

	public String geteModel() {
		return eModel;
	}

	public void seteModel(String eModel) {
		this.eModel = eModel;
	}

	public String getCallfailurePercentage() {
		return callfailurePercentage;
	}

	public void setCallfailurePercentage(String callfailurePercentage) {
		this.callfailurePercentage = callfailurePercentage;
	}

	public String getCallfailure() {
		return callfailure;
	}

	public void setCallfailure(String callfailure) {
		this.callfailure = callfailure;
	}

	public String getDropcalls() {
		return dropcalls;
	}

	public void setDropcalls(String dropcalls) {
		this.dropcalls = dropcalls;
	}

	public String getDcr() {
		return dcr;
	}

	public void setDcr(String dcr) {
		this.dcr = dcr;
	}

	public String getAvg_downlinkMOS() {
		return avg_downlinkMOS;
	}

	public void setAvg_downlinkMOS(String avgDownlinkMOS) {
		avg_downlinkMOS = avgDownlinkMOS;
	}

	public String getAvg_uplinkMOS() {
		return avg_uplinkMOS;
	}

	public void setAvg_uplinkMOS(String avgUplinkMOS) {
		avg_uplinkMOS = avgUplinkMOS;
	}

	public String getMax_downlinkMOS() {
		return max_downlinkMOS;
	}

	public void setMax_downlinkMOS(String maxDownlinkMOS) {
		max_downlinkMOS = maxDownlinkMOS;
	}

	public String getMax_uplinkMOS() {
		return max_uplinkMOS;
	}

	public void setMax_uplinkMOS(String maxUplinkMOS) {
		max_uplinkMOS = maxUplinkMOS;
	}

	public String getMarketName() {
		return marketName;
	}

	public void setMarketName(String marketName) {
		this.marketName = marketName;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getTotalCalls() {
		return totalCalls;
	}

	public void setTotalCalls(String totalCalls) {
		this.totalCalls = totalCalls;
	}
}
